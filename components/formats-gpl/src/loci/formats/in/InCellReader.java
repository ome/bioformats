/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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
import java.util.Hashtable;
import java.util.Vector;

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

import ome.xml.model.enums.Binning;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * InCellReader is the file format reader for InCell 1000/2000 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/InCellReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/InCellReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class InCellReader extends FormatReader {

  // -- Constants --

  public static final String INCELL_MAGIC_STRING = "IN Cell Analyzer";

  private static final String[] PIXELS_SUFFIXES =
    new String[] {"tif", "tiff", "im"};
  private static final String[] METADATA_SUFFIXES =
    new String[] {"xml", "xlog"};

  // -- Fields --

  private boolean[][] plateMap;

  private Image[][][][] imageFiles;
  private MinimalTiffReader tiffReader;
  private Vector<Double> emWaves, exWaves;
  private Vector<String> channelNames;
  private int totalImages;
  private int imageWidth, imageHeight;
  private String creationDate;
  private String rowName = "A", colName = "1";
  private int fieldCount;

  private int wellRows, wellCols;
  private Hashtable<Integer, int[]> wellCoordinates;
  private Vector<Double> posX, posY;

  private boolean[][] exclude;

  private Vector<Integer> channelsPerTimepoint;
  private boolean oneTimepointPerSeries;
  private int totalChannels;

  private Vector<String> metadataFiles;

  private Binning bin;
  private PositiveFloat x, y;
  private Double gain;
  private Double temperature;
  private Double refractive;

  // -- Constructor --

  /** Constructs a new InCell 1000/2000 reader. */
  public InCellReader() {
    super("InCell 1000/2000",
      new String[] {"xdce", "xml", "tiff", "tif", "xlog"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .xdce file with at least one .tif/.tiff or " +
      ".im file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "xdce") || checkSuffix(name, "xml")) {
      return super.isThisType(name, open);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2048;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.indexOf(INCELL_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader == null ? null : tiffReader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader == null ? null : tiffReader.get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] coordinates = getZCTCoords(no);

    int well = getWellFromSeries(getSeries());
    int field = getFieldFromSeries(getSeries());
    int timepoint = oneTimepointPerSeries ?
      getSeries() % channelsPerTimepoint.size() : coordinates[2];
    int image = getIndex(coordinates[0], coordinates[1], 0);

    if (imageFiles[well][field][timepoint][image] == null) return buf;
    String filename = imageFiles[well][field][timepoint][image].filename;
    if (filename == null || !(new Location(filename).exists())) return buf;

    if (imageFiles[well][field][timepoint][image].isTiff) {
      try {
        tiffReader.setId(filename);
        return tiffReader.openBytes(0, buf, x, y, w, h);
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
      return buf;
    }

    // pixels are stored in .im files
    RandomAccessInputStream s = new RandomAccessInputStream(filename);
    if (s.length() > FormatTools.getPlaneSize(this)) {
      s.seek(128);
      readPlane(s, x, y, w, h, buf);
    }
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    files.add(currentId);
    files.addAll(metadataFiles);
    if (!noPixels && imageFiles != null) {
      int well = getWellFromSeries(getSeries());
      int field = getFieldFromSeries(getSeries());
      for (Image[] timepoints : imageFiles[well][field]) {
        for (Image plane : timepoints) {
          if (plane != null && plane.filename != null) {
            if (new Location(plane.filename).exists()) {
              files.add(plane.filename);
            }
            if (new Location(plane.thumbnailFile).exists()) {
              files.add(plane.thumbnailFile);
            }
          }
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiffReader != null) tiffReader.close(fileOnly);

    if (!fileOnly) {
      imageFiles = null;
      tiffReader = null;
      totalImages = 0;
      emWaves = exWaves = null;
      channelNames = null;
      wellCoordinates = null;
      posX = null;
      posY = null;
      creationDate = null;
      wellRows = wellCols = 0;
      fieldCount = 0;
      exclude = null;
      metadataFiles = null;
      imageWidth = imageHeight = 0;
      rowName = "A";
      colName = "1";
      channelsPerTimepoint = null;
      oneTimepointPerSeries = false;
      totalChannels = 0;
      plateMap = null;
      bin = null;
      x = null;
      y = null;
      gain = null;
      temperature = null;
      refractive = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    for (Image[][][] well : imageFiles) {
      for (Image[][] field : well) {
        for (Image[] timepoint : field) {
          for (Image img : timepoint) {
            if (img != null) {
              if (img.isTiff && tiffReader != null) {
                return tiffReader.getOptimalTileWidth();
              }
              break;
            }
          }
        }
      }
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    for (Image[][][] well : imageFiles) {
      for (Image[][] field : well) {
        for (Image[] timepoint : field) {
          for (Image img : timepoint) {
            if (img != null) {
              if (img.isTiff && tiffReader != null) {
                return tiffReader.getOptimalTileHeight();
              }
              break;
            }
          }
        }
      }
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    // make sure that we have the .xdce (or .xml) file
    if (checkSuffix(id, PIXELS_SUFFIXES) || checkSuffix(id, "xlog")) {
      Location currentFile = new Location(id).getAbsoluteFile();
      Location parent = currentFile.getParentFile();
      String[] list = parent.list(true);
      for (String f : list) {
        if (checkSuffix(f, new String[] {"xdce", "xml"})) {
          String path = new Location(parent, f).getAbsolutePath();
          if (isThisType(path)) {
            // make sure that the .xdce file references the current file
            // this ensures that the correct file is chosen if multiple
            // .xdce files are the same directory
            String data = DataTools.readFile(path);
            if (data.indexOf(currentFile.getName()) >= 0) {
              id = path;
              break;
            }
          }
        }
      }
    }

    super.initFile(id);
    in = new RandomAccessInputStream(id);

    channelNames = new Vector<String>();
    emWaves = new Vector<Double>();
    exWaves = new Vector<Double>();
    channelsPerTimepoint = new Vector<Integer>();
    metadataFiles = new Vector<String>();

    // parse metadata from the .xdce or .xml file

    wellCoordinates = new Hashtable<Integer, int[]>();
    posX = new Vector<Double>();
    posY = new Vector<Double>();

    byte[] b = new byte[(int) in.length()];
    in.read(b);

    CoreMetadata ms0 = core.get(0);

    ms0.dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    DefaultHandler handler = new MinimalInCellHandler();
    XMLTools.parseXML(b, handler);

    if (getSizeZ() == 0) ms0.sizeZ = 1;
    if (getSizeC() == 0) ms0.sizeC = 1;
    if (getSizeT() == 0) ms0.sizeT = 1;

    int seriesCount = 0;

    if (exclude != null) {
      for (int row=0; row<wellRows; row++) {
        for (int col=0; col<wellCols; col++) {
          if (!exclude[row][col]) {
            seriesCount += imageFiles[row*wellCols + col].length;
          }
        }
      }
      int expectedSeries = totalImages / (getSizeZ() * getSizeC() * getSizeT());
      seriesCount = (int) Math.min(seriesCount, expectedSeries);
    }
    else seriesCount = totalImages / (getSizeZ() * getSizeC() * getSizeT());

    totalChannels = getSizeC();

    oneTimepointPerSeries = false;
    for (int i=1; i<channelsPerTimepoint.size(); i++) {
      if (!channelsPerTimepoint.get(i).equals(channelsPerTimepoint.get(i - 1)))
      {
        oneTimepointPerSeries = true;
        break;
      }
    }
    if (oneTimepointPerSeries) {
      int imageCount = 0;
      for (Integer timepoint : channelsPerTimepoint) {
        imageCount += timepoint.intValue() * getSizeZ();
      }
      seriesCount = (totalImages / imageCount) * getSizeT();
    }

    int sizeT = getSizeT();
    int sizeC = getSizeC();
    int z = getSizeZ();
    int t = oneTimepointPerSeries ? 1 : getSizeT();

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      int c = oneTimepointPerSeries ?
        channelsPerTimepoint.get(i % sizeT).intValue() : sizeC;

      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      ms.sizeZ = z;
      ms.sizeC = c;
      ms.sizeT = t;
      ms.imageCount = z * c * t;
      ms.dimensionOrder = "XYZCT";
    }

    int wellIndex = getWellFromSeries(0);
    int fieldIndex = getFieldFromSeries(0);

    String filename = imageFiles[wellIndex][fieldIndex][0][0].filename;
    boolean isTiff = imageFiles[wellIndex][fieldIndex][0][0].isTiff;

    if (isTiff && filename != null) {
      tiffReader = new MinimalTiffReader();
      tiffReader.setId(filename);
      int nextTiming = 0;
      for (int i=0; i<seriesCount; i++) {
        CoreMetadata ms = core.get(i);
        ms.sizeX = tiffReader.getSizeX();
        ms.sizeY = tiffReader.getSizeY();
        ms.interleaved = tiffReader.isInterleaved();
        ms.indexed = tiffReader.isIndexed();
        ms.rgb = tiffReader.isRGB();
        ms.pixelType = tiffReader.getPixelType();
        ms.littleEndian = tiffReader.isLittleEndian();
      }
    }
    else {
      for (int i=0; i<seriesCount; i++) {
        CoreMetadata ms = core.get(i);
        ms.sizeX = imageWidth;
        ms.sizeY = imageHeight;
        ms.interleaved = false;
        ms.indexed = false;
        ms.rgb = false;
        ms.pixelType = FormatTools.UINT16;
        ms.littleEndian = true;
      }
    }

    MetadataTools.populatePixels(store, this, true);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      handler = new InCellHandler(store);
      XMLTools.parseXML(b, handler);
    }

    String rowNaming =
      Character.isDigit(rowName.charAt(0)) ? "Number" : "Letter";
    String colNaming =
      Character.isDigit(colName.charAt(0)) ? "Number" : "Letter";

    String plateName = currentId;
    int begin = plateName.lastIndexOf(File.separator) + 1;
    int end = plateName.lastIndexOf(".");
    plateName = plateName.substring(begin, end);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRowNamingConvention(getNamingConvention(rowNaming), 0);
    store.setPlateColumnNamingConvention(getNamingConvention(colNaming), 0);

    for (int r=0; r<wellRows; r++) {
      for (int c=0; c<wellCols; c++) {
        int well = r * wellCols + c;
        String wellID = MetadataTools.createLSID("Well", 0, well);
        store.setWellID(wellID, 0, well);
        store.setWellRow(new NonNegativeInteger(r), 0, well);
        store.setWellColumn(new NonNegativeInteger(c), 0, well);
      }
    }

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    PositiveInteger maxFieldCount = FormatTools.getMaxFieldCount(fieldCount);
    if (maxFieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(maxFieldCount, 0, 0);
    }

    // populate Image data

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String experimentID = MetadataTools.createLSID("Experiment", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setExperimentID(experimentID, 0);

    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);

    for (int i=0; i<seriesCount; i++) {
      store.setObjectiveSettingsID(objectiveID, i);

      if (refractive != null) {
        store.setObjectiveSettingsRefractiveIndex(refractive, i);
      }
      if (x != null) {
        store.setPixelsPhysicalSizeX(x, i);
      }
      if (y != null) {
        store.setPixelsPhysicalSizeY(y, i);
      }

      int well = getWellFromSeries(i);
      int field = getFieldFromSeries(i) + 1;
      int totalTimepoints =
        oneTimepointPerSeries ? channelsPerTimepoint.size() : 1;
      int timepoint = oneTimepointPerSeries ? (i % totalTimepoints) + 1 : -1;

      String imageID = MetadataTools.createLSID("Image", i);
      store.setImageID(imageID, i);
      store.setImageInstrumentRef(instrumentID, i);
      store.setImageExperimentRef(experimentID, i);

      int wellRow = well / wellCols;
      int wellCol = well % wellCols;

      char rowChar = rowName.charAt(rowName.length() - 1);
      char colChar = colName.charAt(colName.length() - 1);
      String row = rowName.substring(0, rowName.length() - 1);
      String col = colName.substring(0, colName.length() - 1);

      if (Character.isDigit(rowChar)) {
        row += wellRow + Integer.parseInt(String.valueOf(rowChar));
      }
      else row += (char) (rowChar + wellRow);

      if (Character.isDigit(colChar)) {
        col += wellCol + Integer.parseInt(String.valueOf(colChar));
      }
      else col += (char) (colChar + wellCol);

      String imageName = "Well " + row + "-" + col + ", Field #" + field;
      if (timepoint >= 0) {
        imageName += ", Timepoint #" + timepoint;
      }

      store.setImageName(imageName, i);
      if (creationDate != null) {
        store.setImageAcquisitionDate(new Timestamp(creationDate), i);
      }

      timepoint--;
      if (timepoint < 0) timepoint = 0;
      int sampleIndex = (field - 1) * totalTimepoints + timepoint;

      String wellSampleID =
        MetadataTools.createLSID("WellSample", 0, well, sampleIndex);
      store.setWellSampleID(wellSampleID, 0, well, sampleIndex);
      store.setWellSampleIndex(new NonNegativeInteger(i), 0, well, sampleIndex);
      store.setWellSampleImageRef(imageID, 0, well, sampleIndex);
      if (field < posX.size()) {
        store.setWellSamplePositionX(posX.get(field), 0, well, sampleIndex);
      }
      if (field < posY.size()) {
        store.setWellSamplePositionY(posY.get(field), 0, well, sampleIndex);
      }

      store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate PlaneTiming data

      for (int i=0; i<seriesCount; i++) {
        setSeries(i);

        int well = getWellFromSeries(i);
        int field = getFieldFromSeries(i);
        int timepoint = oneTimepointPerSeries ?
          i % channelsPerTimepoint.size() : 0;
        for (int time=0; time<getSizeT(); time++) {
          if (!oneTimepointPerSeries) timepoint = time;
          int c = channelsPerTimepoint.get(timepoint).intValue();
          for (int q=0; q<getSizeZ()*c; q++) {
            Image img = imageFiles[well][field][timepoint][q];
            if (img == null) continue;
            int plane = time * getSizeZ() * c + q;
            store.setPlaneDeltaT(img.deltaT, i, plane);
            store.setPlaneExposureTime(img.exposure, i, plane);

            store.setPlanePositionX(posX.get(field), i, plane);
            store.setPlanePositionY(posY.get(field), i, plane);
            store.setPlanePositionZ(img.zPosition, i, plane);
          }
        }

        // populate LogicalChannel data
        for (int q=0; q<getEffectiveSizeC(); q++) {
          if (q < channelNames.size()) {
            store.setChannelName(channelNames.get(q), i, q);
          }
          if (q < emWaves.size()) {
            Double wave = emWaves.get(q);
            PositiveFloat emission = FormatTools.getEmissionWavelength(wave);
            if (emission != null) {
              store.setChannelEmissionWavelength(emission, i, q);
            }
          }
          if (q < exWaves.size()) {
            Double wave = exWaves.get(q);
            PositiveFloat excitation =
              FormatTools.getExcitationWavelength(wave);
            if (excitation != null) {
              store.setChannelExcitationWavelength(excitation, i, q);
            }
          }

          if (detectorID != null) {
            store.setDetectorSettingsID(detectorID, i, q);
            if (bin != null) {
              store.setDetectorSettingsBinning(bin, i, q);
            }
            if (gain != null) {
              store.setDetectorSettingsGain(gain, i, q);
            }
          }
        }
        if (temperature != null) {
          store.setImagingEnvironmentTemperature(temperature, i);
        }
      }
      setSeries(0);

      // populate Plate data

      store.setPlateWellOriginX(0.5, 0);
      store.setPlateWellOriginY(0.5, 0);
    }
  }

  // -- Helper methods --

  private int getFieldFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    return series % fieldCount;
  }

  private int getWellFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    int well = series / fieldCount;

    int counter = -1;

    for (int row=0; row<plateMap.length; row++) {
      for (int col=0; col<plateMap[row].length; col++) {
        if (plateMap[row][col]) {
          counter++;
        }
        if (counter == well) {
          return row * wellCols + col;
        }
      }
    }
    return -1;
  }

  // -- Helper classes --

  class MinimalInCellHandler extends BaseHandler {
    private String currentImageFile;
    private String currentThumbnail;
    private int wellRow, wellCol;
    private int nChannels = 0;
    private boolean doT = true;
    private boolean doZ = true;

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("PlateMap")) {
        int sizeT = getSizeT();
        if (sizeT == 0) {
          // There has been no <TimeSchedule> in the <PlateMap> defined to
          // populate channelsPerTimepoint so we have to assume that there is
          // only one timepoint otherwise the imageFiles array below will not
          // be correctly initialized.
          sizeT = 1;
        }
        if (channelsPerTimepoint.size() == 0) {
          // There has been no <TimeSchedule> in the <PlateMap> defined to
          // populate channelsPerTimepoint so we have to assume that all
          // channels are being acquired.
          CoreMetadata ms0 = core.get(0);
          channelsPerTimepoint.add(ms0.sizeC);
        }
        imageFiles = new Image[wellRows * wellCols][fieldCount][sizeT][];
        for (int well=0; well<wellRows*wellCols; well++) {
          for (int field=0; field<fieldCount; field++) {
            for (int t=0; t<sizeT; t++) {
              int channels = channelsPerTimepoint.get(t).intValue();
              imageFiles[well][field][t] = new Image[channels * getSizeZ()];
            }
          }
        }
      }
      else if (qName.equals("TimePoint")) {
        channelsPerTimepoint.add(new Integer(nChannels));
        nChannels = 0;
      }
      else if (qName.equals("Times")) {
        if (channelsPerTimepoint.size() == 0) {
          channelsPerTimepoint.add(new Integer(getSizeC()));
        }
        for (int i=0; i<channelsPerTimepoint.size(); i++) {
          int c = channelsPerTimepoint.get(i).intValue();
          if (c == 0) {
            channelsPerTimepoint.setElementAt(new Integer(getSizeC()), i);
          }
        }
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      CoreMetadata ms0 = core.get(0);
      if (qName.equals("Plate")) {
        wellRows = Integer.parseInt(attributes.getValue("rows"));
        wellCols = Integer.parseInt(attributes.getValue("columns"));
        plateMap = new boolean[wellRows][wellCols];
      }
      else if (qName.equals("Exclude")) {
        if (exclude == null) exclude = new boolean[wellRows][wellCols];
        int row = Integer.parseInt(attributes.getValue("row")) - 1;
        int col = Integer.parseInt(attributes.getValue("col")) - 1;
        exclude[row][col] = true;
      }
      else if (qName.equals("Images")) {
        totalImages = Integer.parseInt(attributes.getValue("number"));
      }
      else if (qName.equals("Image")) {
        String file = attributes.getValue("filename");
        String thumb = attributes.getValue("thumbnail");
        Location current = new Location(currentId).getAbsoluteFile();

        Location imageFile = new Location(current.getParentFile(), file);
        currentImageFile = imageFile.getAbsolutePath();
        currentThumbnail =
          new Location(current.getParentFile(), thumb).getAbsolutePath();
      }
      else if (qName.equals("Identifier")) {
        int field = Integer.parseInt(attributes.getValue("field_index"));
        int z = Integer.parseInt(attributes.getValue("z_index"));
        int c = Integer.parseInt(attributes.getValue("wave_index"));
        int t = Integer.parseInt(attributes.getValue("time_index"));
        int channels = channelsPerTimepoint.get(t).intValue();

        int index = FormatTools.getIndex("XYZCT", getSizeZ(),
          channels, 1, getSizeZ() * channels, z, c, 0);

        Image img = new Image();
        img.thumbnailFile = currentThumbnail;
        Location file = new Location(currentImageFile);
        img.filename = file.exists() ? currentImageFile : null;
        if (img.filename == null) {
          LOGGER.debug("{} does not exist.", currentImageFile);
        }
        currentImageFile = currentImageFile.toLowerCase();
        img.isTiff = currentImageFile.endsWith(".tif") ||
          currentImageFile.endsWith(".tiff");
        imageFiles[wellRow * wellCols + wellCol][field][t][index] = img;
      }
      else if (qName.equals("offset_point")) {
        fieldCount++;
      }
      else if (qName.equals("TimePoint") && doT) {
        ms0.sizeT++;
      }
      else if (qName.equals("Wavelength")) {
        String fusion = attributes.getValue("fusion_wave");
        if (fusion.equals("false")) ms0.sizeC++;
        String mode = attributes.getValue("imaging_mode");
        if (mode != null) {
          doZ = mode.equals("3-D");
        }
      }
      else if (qName.equals("AcqWave")) {
        nChannels++;
      }
      else if (qName.equals("ZDimensionParameters")) {
        String nz = attributes.getValue("number_of_slices");
        if (nz != null && doZ) {
          ms0.sizeZ = Integer.parseInt(nz);
        }
        else ms0.sizeZ = 1;
      }
      else if (qName.equals("Row")) {
        wellRow = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Column")) {
        wellCol = Integer.parseInt(attributes.getValue("number")) - 1;
        plateMap[wellRow][wellCol] = true;
      }
      else if (qName.equals("Size")) {
        imageWidth = Integer.parseInt(attributes.getValue("width"));
        imageHeight = Integer.parseInt(attributes.getValue("height"));
      }
      else if (qName.equals("NamingRows")) {
        rowName = attributes.getValue("begin");
      }
      else if (qName.equals("NamingColumns")) {
        colName = attributes.getValue("begin");
      }
      else if (qName.equals("TimeSchedule")) {
        doT = Boolean.parseBoolean(attributes.getValue("enabled"));
      }
    }
  }

  /** SAX handler for parsing XML. */
  class InCellHandler extends BaseHandler {
    private String currentQName;
    private boolean openImage;
    private int nextEmWave = 0;
    private int nextExWave = 0;
    private MetadataStore store;
    private int nextPlate = 0;
    private int currentRow = -1, currentCol = -1;
    private int currentField = 0;
    private int currentImage, currentPlane;
    private Double timestamp, exposure, zPosition;

    public InCellHandler(MetadataStore store) {
      this.store = store;
    }

    public void characters(char[] ch, int start, int length) {
      if (currentQName.equals("UserComment")) {
        String value = new String(ch, start, length);
        store.setImageDescription(value, 0);
      }
    }

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Image")) {
        wellCoordinates.put(new Integer(currentField),
          new int[] {currentRow, currentCol});
        openImage = false;

        int well = currentRow * wellCols + currentCol;
        Image img = imageFiles[well][currentField][currentImage][currentPlane];
        if (img != null) {
          img.deltaT = timestamp;
          img.exposure = exposure;
        }
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;
      for (int i=0; i<attributes.getLength(); i++) {
        String key = qName + " - " + attributes.getQName(i);
        if (getMetadataValue(key) != null) {
          break;
        }
        addGlobalMeta(key, attributes.getValue(i));
      }

      if (qName.equals("Microscopy")) {
        String experimentID = MetadataTools.createLSID("Experiment", 0);
        store.setExperimentID(experimentID, 0);
        try {
          store.setExperimentType(
            getExperimentType(attributes.getValue("type")), 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }
      }
      else if (qName.equals("Image")) {
        openImage = true;
        double time =
          Double.parseDouble(attributes.getValue("acquisition_time_ms"));
        timestamp = new Double(time / 1000);
      }
      else if (qName.equals("Identifier")) {
        currentField = Integer.parseInt(attributes.getValue("field_index"));
        int z = Integer.parseInt(attributes.getValue("z_index"));
        int c = Integer.parseInt(attributes.getValue("wave_index"));
        int t = Integer.parseInt(attributes.getValue("time_index"));
        currentImage = t;
        currentPlane = z * getSizeC() + c;
        int well = currentRow * wellCols + currentCol;
        Image img = imageFiles[well][currentField][currentImage][currentPlane];
        if (img != null) {
          img.zPosition = zPosition;
        }
      }
      else if (qName.equals("FocusPosition")) {
        zPosition = new Double(attributes.getValue("z"));
      }
      else if (qName.equals("Creation")) {
        String date = attributes.getValue("date"); // yyyy-mm-dd
        String time = attributes.getValue("time"); // hh:mm:ss
        creationDate = date + "T" + time;
      }
      else if (qName.equals("ObjectiveCalibration")) {
        Double mag =
          Double.parseDouble(attributes.getValue("magnification"));
        store.setObjectiveNominalMagnification(mag, 0, 0);
        store.setObjectiveLensNA(new Double(
          attributes.getValue("numerical_aperture")), 0, 0);
        try {
         store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }

        String objective = attributes.getValue("objective_name");
        String[] tokens = objective.split("_");

        store.setObjectiveManufacturer(tokens[0], 0, 0);
        String correction = tokens.length > 2 ? tokens[2] : "Other";
        try {
          store.setObjectiveCorrection(getCorrection(correction), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }

        Double pixelSizeX = new Double(attributes.getValue("pixel_width"));
        Double pixelSizeY = new Double(attributes.getValue("pixel_height"));
        refractive = new Double(attributes.getValue("refractive_index"));

        x = FormatTools.getPhysicalSizeX(pixelSizeX);
        y = FormatTools.getPhysicalSizeY(pixelSizeY);
      }
      else if (qName.equals("ExcitationFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) exWaves.add(new Double(wave));
      }
      else if (qName.equals("EmissionFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) emWaves.add(new Double(wave));
        channelNames.add(attributes.getValue("name"));
      }
      else if (qName.equals("Camera")) {
        store.setDetectorModel(attributes.getValue("name"), 0, 0);
        try {
          store.setDetectorType(getDetectorType("Other"), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }
      }
      else if (qName.equals("Binning")) {
        String binning = attributes.getValue("value");
        try {
          bin = getBinning(binning);
        }
        catch (FormatException e) { }
      }
      else if (qName.equals("Gain")) {
        String value = attributes.getValue("value");
        if (value == null) {
          return;
        }
        try {
          gain = new Double(value);
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse gain '" + value + "'", e);
        }
      }
      else if (qName.equals("PlateTemperature")) {
        temperature = new Double(attributes.getValue("value"));
      }
      else if (qName.equals("Plate")) {
        nextPlate++;
      }
      else if (qName.equals("Row")) {
        currentRow = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Column")) {
        currentCol = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Exposure") && openImage) {
        double exp = Double.parseDouble(attributes.getValue("time"));
        exposure = new Double(exp / 1000);
      }
      else if (qName.equals("offset_point")) {
        String x = attributes.getValue("x");
        String y = attributes.getValue("y");

        posX.add(new Double(x));
        posY.add(new Double(y));

        addGlobalMetaList("X position for position", x);
        addGlobalMetaList("Y position for position", y);
      }
    }
  }

  class Image {
    public String filename;
    public String thumbnailFile;
    public boolean isTiff;
    public Double deltaT, exposure;
    public Double zPosition;
  }

}
