/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.xml.sax.Attributes;

/**
 * ScanrReader is the file format reader for Olympus ScanR datasets.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ScanrReader extends FormatReader {

  // -- Constants --

  private static final String XML_FILE = "experiment_descriptor.xml";
  private static final String EXPERIMENT_FILE = "experiment_descriptor.dat";
  private static final String ACQUISITION_FILE = "AcquisitionLog.dat";
  private static final String[] METADATA_SUFFIXES = new String[] {"dat", "xml"};

  // -- Fields --

  private final List<String> metadataFiles = new ArrayList<String>();
  private int wellRows, wellColumns;
  private int fieldRows, fieldColumns;
  private int wellCount = 0;
  private final List<String> channelNames = new ArrayList<String>();
  private Map<String, Integer> wellLabels =
    new HashMap<String, Integer>();
  private Map<Integer, Integer> wellNumbers =
    new HashMap<Integer, Integer>();
  private String plateName;
  private Double pixelSize;

  private int tileWidth = 0;
  private int tileHeight = 0;

  private String[] tiffs;
  private MinimalTiffReader reader;

  private boolean foundPositions = false;
  private Length[] fieldPositionX;
  private Length[] fieldPositionY;
  private final List<Double> exposures = new ArrayList<Double>();
  private Double deltaT = null;

  private Map<Integer, String[]> seriesFiles =
    new HashMap<Integer, String[]>();

  // -- Constructor --

  /** Constructs a new ScanR reader. */
  public ScanrReader() {
    super("Olympus ScanR", new String[] {"dat", "xml", "tif"});
    domains = new String[] {FormatTools.HCS_DOMAIN};
    suffixSufficient = false;
    hasCompanionFiles = true;
    datasetDescription = "One .xml file, one 'data' directory containing " +
      ".tif/.tiff files, and optionally two .dat files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    Location file = new Location(id).getAbsoluteFile();
    String name = file.getName();
    if (name.equals(XML_FILE) || name.equals(EXPERIMENT_FILE) ||
      name.equals(ACQUISITION_FILE))
    {
      return true;
    }
    Location parent = file.getParentFile();
    if (parent != null) {
      parent = parent.getParentFile();
    }
    return new Location(parent, XML_FILE).exists();
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    String localName = new Location(name).getName();
    if (localName.equals(XML_FILE) || localName.equals(EXPERIMENT_FILE) ||
      localName.equals(ACQUISITION_FILE))
    {
      return true;
    }

    Location parent = new Location(name).getAbsoluteFile().getParentFile();
    if (checkSuffix(name, "tif") && parent.getName().equalsIgnoreCase("Data"))
    {
      parent = parent.getParentFile();
    }
    Location xmlFile = new Location(parent, XML_FILE);
    if (!xmlFile.exists()) {
      return false;
    }

    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser p = new TiffParser(stream);
    IFD ifd = p.getFirstIFD();
    if (ifd == null) return false;

    Object s = ifd.getIFDValue(IFD.SOFTWARE);
    if (s == null) return false;
    String software = s instanceof String[] ? ((String[]) s)[0] : s.toString();
    return software.trim().equals("National Instruments IMAQ");
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (seriesFiles.containsKey(getSeries())) {
      return seriesFiles.get(getSeries());
    }

    final List<String> files = new ArrayList<String>();
    for (String file : metadataFiles) {
      if (file != null) files.add(file);
    }

    if (!noPixels && tiffs != null) {
      int offset = getSeries() * getImageCount();
      for (int i=0; i<getImageCount(); i++) {
        if (offset + i < tiffs.length && tiffs[offset + i] != null) {
          if (isThisType(tiffs[offset + i])) {
            files.add(tiffs[offset + i]);
          }
        }
      }
    }

    String[] fileList = files.toArray(new String[files.size()]);
    seriesFiles.put(getSeries(), fileList);
    return fileList;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (reader != null) {
        reader.close();
      }
      reader = null;
      tiffs = null;
      plateName = null;
      channelNames.clear();
      fieldRows = fieldColumns = 0;
      wellRows = wellColumns = 0;
      metadataFiles.clear();
      wellLabels.clear();
      wellNumbers.clear();
      wellCount = 0;
      pixelSize = null;
      tileWidth = 0;
      tileHeight = 0;
      fieldPositionX = null;
      fieldPositionY = null;
      exposures.clear();
      deltaT = null;
      foundPositions = false;
      seriesFiles.clear();
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int index = getSeries() * getImageCount() + no;
    if (index < tiffs.length && tiffs[index] != null) {
      try {
        reader.setId(tiffs[index]);
        reader.openBytes(0, buf, x, y, w, h);
        reader.close();
      }
      catch (FormatException e) {
        reader.close();
        Arrays.fill(buf, (byte) 0);
        return buf;
      }

      // mask out the sign bit
      ByteArrayHandle pixels = new ByteArrayHandle(buf);
      pixels.setOrder(
        isLittleEndian() ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
      for (int i=0; i<buf.length; i+=2) {
        pixels.seek(i);
        short value = pixels.readShort();
        value = (short) (value & 0xfff);
        pixels.seek(i);
        pixels.writeShort(value);
      }
      buf = pixels.getBytes();
      pixels.close();
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tileWidth;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tileHeight;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    if (metadataFiles.size() > 0) {
      // this dataset has already been initialized
      return;
    }

    // make sure we have the .xml file
    if (!checkSuffix(id, "xml") && isGroupFiles()) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      if (checkSuffix(id, "tif") && parent.getName().equalsIgnoreCase("Data"))
      {
        parent = parent.getParentFile();
      }
      String[] list = parent.list();
      for (String file : list) {
        if (file.equals(XML_FILE)) {
          id = new Location(parent, file).getAbsolutePath();
          super.initFile(id);
          break;
        }
      }
      if (!checkSuffix(id, "xml")) {
        throw new FormatException("Could not find " + XML_FILE + " in " +
          parent.getAbsolutePath());
      }
    }
    else if (!isGroupFiles() && checkSuffix(id, "tif")) {
      TiffReader r = new TiffReader();
      r.setMetadataStore(getMetadataStore());
      r.setId(id);
      core = new ArrayList<CoreMetadata>(r.getCoreMetadataList());
      metadataStore = r.getMetadataStore();

      final Map<String, Object> globalMetadata = r.getGlobalMetadata();
      for (final Map.Entry<String, Object> entry : globalMetadata.entrySet()) {
        addGlobalMeta(entry.getKey(), entry.getValue());
      }

      r.close();
      tiffs = new String[] {id};
      reader = new MinimalTiffReader();

      return;
    }

    Location dir = new Location(id).getAbsoluteFile().getParentFile();
    String[] list = dir.list(true);

    for (String file : list) {
      Location f = new Location(dir, file);
      if (checkSuffix(file, METADATA_SUFFIXES) && !f.isDirectory()) {
        metadataFiles.add(f.getAbsolutePath());
      }
    }

    // parse XML metadata

    String xml = DataTools.readFile(id).trim();

    // add the appropriate encoding, as some ScanR XML files use non-UTF8
    // characters without specifying an encoding

    if (xml.startsWith("<?")) {
      xml = xml.substring(xml.indexOf("?>") + 2);
    }
    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + xml;

    XMLTools.parseXML(xml, new ScanrHandler());

    final List<String> uniqueRows = new ArrayList<String>();
    final List<String> uniqueColumns = new ArrayList<String>();

    if (wellRows == 0 || wellColumns == 0) {
      for (String well : wellLabels.keySet()) {
        if (!Character.isLetter(well.charAt(0))) continue;
        String row = well.substring(0, 1).trim();
        String column = well.substring(1).trim();
        if (!uniqueRows.contains(row) && row.length() > 0) uniqueRows.add(row);
        if (!uniqueColumns.contains(column) && column.length() > 0) {
          uniqueColumns.add(column);
        }
      }

      wellRows = uniqueRows.size();
      wellColumns = uniqueColumns.size();

      if (wellRows * wellColumns != wellCount) {
        adjustWellDimensions();
      }
    }

    int nChannels = getSizeC() == 0 ? channelNames.size() :
      (int) Math.min(channelNames.size(), getSizeC());
    if (nChannels == 0) nChannels = 1;
    int nSlices = getSizeZ() == 0 ? 1 : getSizeZ();
    int nTimepoints = getSizeT();
    int nWells = wellCount;
    int nPos = 0;
    if (foundPositions)
        nPos = fieldPositionX.length;
    else
        nPos = fieldRows * fieldColumns;
    if (nPos == 0) nPos = 1;

    // get list of TIFF files

    Location dataDir = new Location(dir, "data");
    list = dataDir.list(true);
    if (list == null) {
      // try to find the TIFFs in the current directory
      list = dir.list(true);
    }
    else dir = dataDir;
    if (nTimepoints == 0 ||
      list.length < nTimepoints * nChannels * nSlices * nWells * nPos)
    {
      nTimepoints = list.length / (nChannels * nWells * nPos * nSlices);
      if (nTimepoints == 0) nTimepoints = 1;
    }

    tiffs = new String[nChannels * nWells * nPos * nTimepoints * nSlices];

    Arrays.sort(list, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        int lastSeparator1 = s1.lastIndexOf(File.separator) + 1;
        int lastSeparator2 = s2.lastIndexOf(File.separator) + 1;
        String dir1 = s1.substring(0, lastSeparator1);
        String dir2 = s2.substring(0, lastSeparator2);

        if (!dir1.equals(dir2)) {
          return dir1.compareTo(dir2);
        }

        int dash1 = s1.indexOf("-", lastSeparator1);
        int dash2 = s2.indexOf("-", lastSeparator2);

        String label1 = dash1 < 0 ? "" : s1.substring(lastSeparator1, dash1);
        String label2 = dash2 < 0 ? "" : s2.substring(lastSeparator2, dash2);

        if (label1.equals(label2)) {
          String remainder1 = dash1 < 0 ? s1 : s1.substring(dash1);
          String remainder2 = dash2 < 0 ? s2 : s2.substring(dash2);
          return remainder1.compareTo(remainder2);
        }

        Integer index1 = wellLabels.get(label1);
        Integer index2 = wellLabels.get(label2);

        if (index1 == null && index2 != null) {
          return 1;
        }
        else if (index1 != null && index2 == null) {
          return -1;
        }
        return index1.compareTo(index2);
      }
    });
    int lastListIndex = 0;

    int next = 0;
    String[] keys = wellLabels.keySet().toArray(new String[wellLabels.size()]);
    Arrays.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        char row1 = s1.charAt(0);
        char row2 = s2.charAt(0);

        final Integer col1 = new Integer(s1.substring(1));
        final Integer col2 = new Integer(s2.substring(1));

        if (row1 < row2) {
          return -1;
        }
        else if (row1 > row2) {
          return 1;
        }
        return col1.compareTo(col2);
      }
    });

    int realPosCount = 0;
    for (int well=0; well<nWells; well++) {
      int missingWellFiles = 0;
      int wellIndex = wellNumbers.get(well);
      String wellPos = getBlock(wellIndex, "W");
      int originalIndex = next;

      for (int pos=0; pos<nPos; pos++) {
        String posPos = getBlock(pos + 1, "P");
        int posIndex = next;

        for (int z=0; z<nSlices; z++) {
          String zPos = getBlock(z, "Z");

          for (int t=0; t<nTimepoints; t++) {
            String tPos = getBlock(t, "T");

            for (int c=0; c<nChannels; c++) {
              for (int i=lastListIndex; i<list.length; i++) {
                String file = list[i];
                if (file.indexOf(wellPos) != -1 && file.indexOf(zPos) != -1 &&
                  file.indexOf(posPos) != -1 && file.indexOf(tPos) != -1 &&
                  file.indexOf(channelNames.get(c)) != -1)
                {
                  tiffs[next++] = new Location(dir, file).getAbsolutePath();
                  if (c == nChannels - 1) {
                    lastListIndex = i;
                  }
                  break;
                }
              }
              if (next == originalIndex) {
                missingWellFiles++;
              }
            }
          }
        }
        if (posIndex != next) realPosCount++;
      }
      if (next == originalIndex && well < keys.length) {
        wellLabels.remove(keys[well]);
      }
      if (next == originalIndex &&
        missingWellFiles == nSlices * nTimepoints * nChannels * nPos)
      {
        wellNumbers.remove(well);
      }
    }
    nWells = wellNumbers.size();

    if (wellLabels.size() > 0 && wellLabels.size() != nWells) {
      uniqueRows.clear();
      uniqueColumns.clear();
      for (String well : wellLabels.keySet()) {
        if (!Character.isLetter(well.charAt(0))) continue;
        String row = well.substring(0, 1).trim();
        String column = well.substring(1).trim();
        if (!uniqueRows.contains(row) && row.length() > 0) uniqueRows.add(row);
        if (!uniqueColumns.contains(column) && column.length() > 0) {
          uniqueColumns.add(column);
        }
      }

      nWells = uniqueRows.size() * uniqueColumns.size();
      adjustWellDimensions();
    }
    if (realPosCount < nPos) {
      nPos = realPosCount;
    }

    reader = new MinimalTiffReader();
    reader.setId(tiffs[0]);
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int pixelType = reader.getPixelType();

    tileWidth = reader.getOptimalTileWidth();
    tileHeight = reader.getOptimalTileHeight();

    // we strongly suspect that ScanR incorrectly records the
    // signedness of the pixels

    switch (pixelType) {
      case FormatTools.INT8:
        pixelType = FormatTools.UINT8;
        break;
      case FormatTools.INT16:
        pixelType = FormatTools.UINT16;
        break;
    }

    boolean rgb = reader.isRGB();
    boolean interleaved = reader.isInterleaved();
    boolean indexed = reader.isIndexed();
    boolean littleEndian = reader.isLittleEndian();

    reader.close();

    int seriesCount = nWells * nPos;
    core.clear();
    for (int i=0; i<seriesCount; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      ms.sizeC = nChannels;
      ms.sizeZ = nSlices;
      ms.sizeT = nTimepoints;
      ms.sizeX = sizeX;
      ms.sizeY = sizeY;
      ms.pixelType = pixelType;
      ms.rgb = rgb;
      ms.interleaved = interleaved;
      ms.indexed = indexed;
      ms.littleEndian = littleEndian;
      ms.dimensionOrder = "XYCTZ";
      ms.imageCount = nSlices * nTimepoints * nChannels;
      ms.bitsPerPixel = 12;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateColumns(new PositiveInteger(wellColumns), 0);
    store.setPlateRows(new PositiveInteger(wellRows), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    int nFields = 0;
    if (foundPositions) {
      nFields = fieldPositionX.length;
    }
    else {
      nFields = fieldRows * fieldColumns;
    }

    PositiveInteger fieldCount = FormatTools.getMaxFieldCount(nFields);
    if (fieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(fieldCount, 0, 0);
    }

    for (int i=0; i<getSeriesCount(); i++) {
      int field = i % nFields;
      int well = i / nFields;
      int index = well;
      while (wellNumbers.get(index) == null && index < wellNumbers.size()) {
        index++;
      }
      int wellIndex =
        wellNumbers.get(index) == null ? index : wellNumbers.get(index) - 1;

      int wellRow = wellIndex / wellColumns;
      int wellCol = wellIndex % wellColumns;

      if (field == 0) {
        store.setWellID(MetadataTools.createLSID("Well", 0, well), 0, well);
        store.setWellColumn(new NonNegativeInteger(wellCol), 0, well);
        store.setWellRow(new NonNegativeInteger(wellRow), 0, well);
      }

      String wellSample =
        MetadataTools.createLSID("WellSample", 0, well, field);
      store.setWellSampleID(wellSample, 0, well, field);
      store.setWellSampleIndex(new NonNegativeInteger(i), 0, well, field);
      String imageID = MetadataTools.createLSID("Image", i);
      store.setWellSampleImageRef(imageID, 0, well, field);
      store.setImageID(imageID, i);

      String name = "Well " + (well + 1) + ", Field " + (field + 1) +
        " (Spot " + (i + 1) + ")";
      store.setImageName(name, i);

      store.setPlateAcquisitionWellSampleRef(wellSample, 0, 0, i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate LogicalChannel data

      for (int i=0; i<getSeriesCount(); i++) {
        for (int c=0; c<getSizeC(); c++) {
          store.setChannelName(channelNames.get(c), i, c);
        }

        Length x = FormatTools.getPhysicalSizeX(pixelSize);
        Length y = FormatTools.getPhysicalSizeY(pixelSize);
        if (x != null) {
          store.setPixelsPhysicalSizeX(x, i);
        }
        if (y != null) {
          store.setPixelsPhysicalSizeY(y, i);
        }

        if (fieldPositionX != null && fieldPositionY != null) {
          int field = i % nFields;
          int well = i / nFields;
          final Length posX = fieldPositionX[field];
          final Length posY = fieldPositionY[field];
          
          store.setWellSamplePositionX(posX, 0, well, field);
          store.setWellSamplePositionY(posY, 0, well, field);
          for (int c=0; c<getSizeC(); c++) {
            int image = getIndex(0, c, 0);
            store.setPlaneTheZ(new NonNegativeInteger(0), i, image);
            store.setPlaneTheC(new NonNegativeInteger(c), i, image);
            store.setPlaneTheT(new NonNegativeInteger(0), i, image);
            store.setPlanePositionX(fieldPositionX[field], i, image);
            store.setPlanePositionY(fieldPositionY[field], i, image);

            // exposure time is stored in milliseconds
            // convert to seconds before populating MetadataStore
            Double time = exposures.get(c);
            if (time != null) {
              time /= 1000;
              store.setPlaneExposureTime(new Time(time, UNITS.SECOND), i, image);
            }
            if (deltaT != null) {
              store.setPlaneDeltaT(new Time(deltaT, UNITS.SECOND), i, image);
            }
          }
        }
      }

      String row = wellRows > 26 ? "Number" : "Letter";
      String col = wellRows > 26 ? "Letter" : "Number";

      store.setPlateRowNamingConvention(getNamingConvention(row), 0);
      store.setPlateColumnNamingConvention(getNamingConvention(col), 0);
      store.setPlateName(plateName, 0);
    }
  }

  // -- Helper class --

  class ScanrHandler extends BaseHandler {
    private String key;
    private String qName;

    private String wellIndex;

    private boolean validChannel = false;
    private boolean foundPlateLayout = false;
    private int nextXPos = 0;
    private int nextYPos = 0;

    private StringBuffer currentValue = new StringBuffer();

    // -- DefaultHandler API methods --

    @Override
    public void characters(char[] ch, int start, int length) {
      String v = new String(ch, start, length);
      currentValue.append(v);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentValue.setLength(0);
      this.qName = qName;
      if (qName.equals("Array") || qName.equals("Cluster")) {
        validChannel = true;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String v = currentValue.toString().trim();
      if (v.length() > 0) {
        if (qName.equals("Name")) {
          key = v;

          if (v.equals("subposition list")) {
            foundPositions = true;
          }
          else if (v.equals("format typedef")) {
            foundPlateLayout = true;
          }
        }
        else if (qName.equals("Dimsize") && foundPositions &&
          fieldPositionX == null)
        {
          int nPositions = Integer.parseInt(v);
          fieldPositionX = new Length[nPositions];
          fieldPositionY = new Length[nPositions];
        }
        else if ("Rows".equals(key) && foundPlateLayout) {
          wellRows = Integer.parseInt(v);
        }
        else if ("Columns".equals(key) && foundPlateLayout) {
          wellColumns = Integer.parseInt(v);
          foundPlateLayout = false;
        }
        else if (qName.equals("Val")) {
          CoreMetadata ms0 = core.get(0);

          addGlobalMeta(key, v);

          if (key.equals("columns/well")) {
            fieldColumns = Integer.parseInt(v);
          }
          else if (key.equals("rows/well")) {
            fieldRows = Integer.parseInt(v);
          }
          else if (key.equals("# slices")) {
            ms0.sizeZ = Integer.parseInt(v);
          }
          else if (key.equals("timeloop real")) {
            ms0.sizeT = Integer.parseInt(v);
          }
          else if (key.equals("timeloop count")) {
            ms0.sizeT = Integer.parseInt(v) + 1;
          }
          else if (key.equals("timeloop delay [ms]")) {
            deltaT = Integer.parseInt(v) / 1000.0;
          }
          else if (key.equals("name") && validChannel) {
            if (!channelNames.contains(v)) {
              channelNames.add(v);
            }
          }
          else if (key.equals("plate name")) {
            plateName = v;
          }
          else if (key.equals("exposure time")) {
            exposures.add(new Double(v));
          }
          else if (key.equals("idle") && validChannel) {
            int lastIndex = channelNames.size() - 1;
            if (v.equals("0") &&
              !channelNames.get(lastIndex).equals("Autofocus"))
            {
              ms0.sizeC++;
            }
            else {
              channelNames.remove(lastIndex);
              exposures.remove(lastIndex);
            }
          }
          else if (key.equals("well selection table + cDNA")) {
            if (Character.isDigit(v.charAt(0))) {
              wellIndex = v;
              wellNumbers.put(wellCount, new Integer(v));
              wellCount++;
            }
            else {
              wellLabels.put(v, new Integer(wellIndex));
            }
          }
          else if (key.equals("conversion factor um/pixel")) {
            pixelSize = new Double(v);
          }
          else if (foundPositions) {
            if (nextXPos == nextYPos) {
              if (nextXPos < fieldPositionX.length) {
                final Double number = Double.valueOf(v);
                final Length length = new Length(number, UNITS.REFERENCEFRAME);
                fieldPositionX[nextXPos++] = length;
              }
            }
            else {
              if (nextYPos < fieldPositionY.length) {
                final Double number = Double.valueOf(v);
                final Length length = new Length(number, UNITS.REFERENCEFRAME);
                fieldPositionY[nextYPos++] = length;
              }
            }
          }
        }
      }

      if (qName.equals("Array") || qName.equals("Cluster")) {
        validChannel = false;
      }
    }
  }

  // -- Helper methods --

  private String getBlock(int index, String axis) {
    String b = String.valueOf(index);
    while (b.length() < 5) b = "0" + b;
    return axis + b;
  }

  private void adjustWellDimensions() {
    if (wellCount <= 8) {
      wellColumns = 2;
      wellRows = 4;
    }
    else if (wellCount <= 96) {
      wellColumns = 12;
      wellRows = 8;
    }
    else if (wellCount <= 384) {
      wellColumns = 24;
      wellRows = 16;
    }
  }

}
