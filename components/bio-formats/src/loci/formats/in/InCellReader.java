//
// InCellReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * InCellReader is the file format reader for InCell 1000 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/InCellReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/InCellReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class InCellReader extends FormatReader {

  // -- Constants --

  public static final String INCELL_MAGIC_STRING = "IN Cell Analyzer 1000";

  private static final String[] PIXELS_SUFFIXES =
    new String[] {"tif", "tiff", "im"};

  // -- Fields --

  private Image[][][][] imageFiles;
  private MinimalTiffReader tiffReader;
  private Vector<Integer> emWaves, exWaves;
  private Vector<String> channelNames;
  private int totalImages;
  private int imageWidth, imageHeight;
  private String creationDate;
  private String rowName, colName;
  private int startRow, startCol;
  private int fieldCount;

  private int wellRows, wellCols;
  private Hashtable<Integer, int[]> wellCoordinates;
  private Vector<Float> posX, posY;

  private int firstRow, firstCol;
  private int lastCol;

  private boolean[][] exclude;

  private Vector<Integer> channelsPerTimepoint;
  private boolean oneTimepointPerSeries;
  private int totalChannels;

  private Vector<String> metadataFiles;

  // -- Constructor --

  /** Constructs a new InCell 1000 reader. */
  public InCellReader() {
    super("InCell 1000", new String[] {"xdce", "xml"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

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
      tiffReader.setId(filename);
      return tiffReader.openBytes(0, buf, x, y, w, h);
    }

    // pixels are stored in .im files
    RandomAccessInputStream s = new RandomAccessInputStream(filename);
    s.skipBytes(128);
    readPlane(s, x, y, w, h, buf);
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    files.addAll(metadataFiles);
    if (!noPixels && imageFiles != null) {
      int well = getWellFromSeries(getSeries());
      int field = getFieldFromSeries(getSeries());
      for (Image[] timepoints : imageFiles[well][field]) {
        for (Image plane : timepoints) {
          if (plane != null && plane.filename != null) {
            files.add(plane.filename);
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
      startRow = startCol = 0;
      fieldCount = 0;
      exclude = null;
      metadataFiles = null;
      imageWidth = imageHeight = 0;
      rowName = colName = null;
      firstRow = firstCol = 0;
      lastCol = 0;
      channelsPerTimepoint = null;
      oneTimepointPerSeries = false;
      totalChannels = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("InCellReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    firstRow = Integer.MAX_VALUE;
    firstCol = Integer.MAX_VALUE;
    lastCol = Integer.MIN_VALUE;

    channelNames = new Vector<String>();
    emWaves = new Vector<Integer>();
    exWaves = new Vector<Integer>();
    channelsPerTimepoint = new Vector<Integer>();
    metadataFiles = new Vector<String>();

    // build list of companion files

    Location directory = new Location(id).getAbsoluteFile().getParentFile();
    String[] files = directory.list(true);
    for (String file : files) {
      if (!checkSuffix(file, PIXELS_SUFFIXES)) {
        metadataFiles.add(new Location(directory, file).getAbsolutePath());
      }
    }

    // parse metadata from the .xdce or .xml file

    wellCoordinates = new Hashtable<Integer, int[]>();
    posX = new Vector<Float>();
    posY = new Vector<Float>();

    byte[] b = new byte[(int) in.length()];
    in.read(b);

    core[0].dimensionOrder = "XYZCT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    DefaultHandler handler = new MinimalInCellHandler();
    XMLTools.parseXML(b, handler);

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

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

    core = new CoreMetadata[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      int c = oneTimepointPerSeries ?
        channelsPerTimepoint.get(i % sizeT).intValue() : sizeC;

      core[i] = new CoreMetadata();
      core[i].sizeZ = z;
      core[i].sizeC = c;
      core[i].sizeT = t;
      core[i].imageCount = z * c * t;
      core[i].dimensionOrder = "XYZCT";
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
        core[i].sizeX = tiffReader.getSizeX();
        core[i].sizeY = tiffReader.getSizeY();
        core[i].interleaved = tiffReader.isInterleaved();
        core[i].indexed = tiffReader.isIndexed();
        core[i].rgb = tiffReader.isRGB();
        core[i].pixelType = tiffReader.getPixelType();
        core[i].littleEndian = tiffReader.isLittleEndian();
      }
    }
    else {
      for (int i=0; i<seriesCount; i++) {
        core[i].sizeX = imageWidth;
        core[i].sizeY = imageHeight;
        core[i].interleaved = false;
        core[i].indexed = false;
        core[i].rgb = false;
        core[i].pixelType = FormatTools.UINT16;
        core[i].littleEndian = true;
      }
    }

    MetadataTools.populatePixels(store, this, true);

    handler = new InCellHandler(store);
    XMLTools.parseXML(b, handler);

    // populate Image data

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String experimentID = MetadataTools.createLSID("Experiment", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setExperimentID(experimentID, 0);

    for (int i=0; i<seriesCount; i++) {
      int well = getWellFromSeries(i);
      int field = getFieldFromSeries(i) + 1;
      int timepoint = oneTimepointPerSeries ?
        (i % channelsPerTimepoint.size()) + 1 : -1;

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
      store.setImageCreationDate(creationDate, i);
    }

    // populate PlaneTiming data

    for (int i=0; i<seriesCount; i++) {
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
          store.setPlaneTimingDeltaT(img.deltaT, i, 0, plane);
          store.setPlaneTimingExposureTime(img.exposure, i, 0, plane);

          store.setStagePositionPositionX(posX.get(field), i, 0, plane);
          store.setStagePositionPositionY(posY.get(field), i, 0, plane);
          store.setStagePositionPositionZ(img.zPosition, i, 0, plane);
        }
      }
    }

    // populate LogicalChannel data

    for (int i=0; i<seriesCount; i++) {
      setSeries(i);
      for (int q=0; q<getEffectiveSizeC(); q++) {
        if (q < channelNames.size()) {
          store.setLogicalChannelName(channelNames.get(q), i, q);
        }
        if (q < emWaves.size()) {
          store.setLogicalChannelEmWave(emWaves.get(q), i, q);
        }
        if (q < exWaves.size()) {
          store.setLogicalChannelExWave(exWaves.get(q), i, q);
        }
      }
    }
    setSeries(0);

    // populate Plate data

    store.setPlateRowNamingConvention(rowName, 0);
    store.setPlateColumnNamingConvention(colName, 0);
    store.setPlateWellOriginX(new Double(0.5), 0);
    store.setPlateWellOriginY(new Double(0.5), 0);

    // populate Well data

    for (int i=0; i<seriesCount; i++) {
      int well = getWellFromSeries(i);
      int field = getFieldFromSeries(i);

      String imageID = MetadataTools.createLSID("Image", i);
      store.setWellSampleIndex(new Integer(i), 0, well, field);
      store.setWellSampleImageRef(imageID, 0, well, field);
      store.setWellSamplePosX(posX.get(field), 0, well, field);
      store.setWellSamplePosY(posY.get(field), 0, well, field);
    }

    // populate ROI data
    parseTextROIs(store);
  }

  // -- Helper methods --

  private int getFieldFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    return series % fieldCount;
  }

  private int getWellFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    int well = series / fieldCount;
    int wellRow = well / (lastCol - firstCol + 1);
    int wellCol = well % (lastCol - firstCol + 1);
    return (wellRow + firstRow) * wellCols + wellCol + firstCol;
  }

  /**
   * If a .txt file is present, parse ROI data and place it in the
   * given MetadataStore.
   */
  private void parseTextROIs(MetadataStore store) throws IOException {
    if (metadataFiles == null) return;
    for (String file : metadataFiles) {
      if (file.toLowerCase().endsWith(".txt")) {
        String[] lines = DataTools.readFile(file).split("\n");
        String[] skipRow = null, columns = null, values = null;
        String well = null, prevWell = null;
        int xIndex = -1, yIndex = -1;
        int cellIndex = -1, wellIndex = -1;
        int areaIndex = -1;
        int image = 0;
        for (String line : lines) {
          values = line.split("\t");
          if (values.length > 2) {
            if (skipRow == null) skipRow = values;
            else if (columns == null) {
              columns = values;
              xIndex = DataTools.indexOf(columns, "Cell cg X");
              yIndex = DataTools.indexOf(columns, "Cell cg Y");
              wellIndex = DataTools.indexOf(columns, "Well");
              cellIndex = DataTools.indexOf(columns, "Cell");
              areaIndex = DataTools.indexOf(columns, "Nuc Area");
            }
            else {
              // assume that rows are sorted by well
              if (wellIndex == -1) continue;
              well = values[wellIndex].trim();
              if (!well.equals(prevWell) && prevWell != null) image++;

              int roiIndex = 0;
              double area = 0d;

              String cell = values[cellIndex].trim();

              try {
                roiIndex = Integer.parseInt(cell) - 1;
                area = Double.parseDouble(values[areaIndex].trim());
              }
              catch (NumberFormatException e) {
                break;
              }

              // calculate the radius of the ROI, since it's not given
              double radius = Math.sqrt(area / Math.PI);

              // "Cell cg X", "Cell cg Y"
              store.setCircleCx(values[xIndex].trim(), image, roiIndex, 0);
              store.setCircleCy(values[yIndex].trim(), image, roiIndex, 0);
              store.setCircleR(String.valueOf(radius), image, roiIndex, 0);

              if (isMetadataCollected()) {
                setSeries(image);
                for (int col=2; col<values.length; col++) {
                  addSeriesMeta("Cell #" + cell + " " + columns[col],
                    values[col].trim());
                }
              }
              prevWell = well;
            }
          }
        }

        break;
      }
    }
    setSeries(0);
  }

  // -- Helper classes --

  class MinimalInCellHandler extends DefaultHandler {
    private String currentImageFile;
    private int wellRow, wellCol;
    private int nChannels = 0;

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("PlateMap")) {
        imageFiles = new Image[wellRows * wellCols][fieldCount][getSizeT()][];
        for (int well=0; well<wellRows*wellCols; well++) {
          for (int field=0; field<fieldCount; field++) {
            for (int t=0; t<getSizeT(); t++) {
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
      if (qName.equals("Plate")) {
        wellRows = Integer.parseInt(attributes.getValue("rows"));
        wellCols = Integer.parseInt(attributes.getValue("columns"));
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
        Location file = new Location(currentImageFile);
        img.filename = file.exists() ? currentImageFile : null;
        if (img.filename == null) {
          warn(currentImageFile + " does not exist.");
        }
        currentImageFile = currentImageFile.toLowerCase();
        img.isTiff = currentImageFile.endsWith(".tif") ||
          currentImageFile.endsWith(".tiff");
        imageFiles[wellRow * wellCols + wellCol][field][t][index] = img;
      }
      else if (qName.equals("offset_point")) {
        fieldCount++;
      }
      else if (qName.equals("TimePoint")) {
        core[0].sizeT++;
      }
      else if (qName.equals("Wavelength")) {
        String fusion = attributes.getValue("fusion_wave");
        if (fusion.equals("false")) core[0].sizeC++;
      }
      else if (qName.equals("AcqWave")) {
        nChannels++;
      }
      else if (qName.equals("ZDimensionParameters")) {
        String nz = attributes.getValue("number_of_slices");
        if (nz != null) {
          core[0].sizeZ = Integer.parseInt(nz);
        }
        else core[0].sizeZ = 1;
      }
      else if (qName.equals("Row")) {
        wellRow = Integer.parseInt(attributes.getValue("number")) - 1;
        firstRow = (int) Math.min(firstRow, wellRow);
      }
      else if (qName.equals("Column")) {
        wellCol = Integer.parseInt(attributes.getValue("number")) - 1;
        firstCol = (int) Math.min(firstCol, wellCol);
        lastCol = (int) Math.max(lastCol, wellCol);
      }
      else if (qName.equals("Size")) {
        imageWidth = Integer.parseInt(attributes.getValue("width"));
        imageHeight = Integer.parseInt(attributes.getValue("height"));
      }
    }
  }

  /** SAX handler for parsing XML. */
  class InCellHandler extends DefaultHandler {
    private String currentQName;
    private boolean openImage;
    private int nextEmWave = 0;
    private int nextExWave = 0;
    private MetadataStore store;
    private int nextPlate = 0;
    private int currentRow = -1, currentCol = -1;
    private int currentField = 0;
    private int currentImage, currentPlane;
    private Float timestamp, exposure, zPosition;
    private String channelName = null;

    public InCellHandler(MetadataStore store) {
      this.store = store;
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      if (currentQName.equals("UserComment")) {
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
      else if (qName.equals("Wavelength")) {
        channelName = null;
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;
      for (int i=0; i<attributes.getLength(); i++) {
        addGlobalMeta(qName + " - " + attributes.getQName(i),
          attributes.getValue(i));
      }

      if (qName.equals("Microscopy")) {
        String experimentID = MetadataTools.createLSID("Experiment", 0);
        store.setExperimentID(experimentID, 0);
        store.setExperimentType(attributes.getValue("type"), 0);
      }
      else if (qName.equals("Image")) {
        openImage = true;
        float time =
          Float.parseFloat(attributes.getValue("acquisition_time_ms"));
        timestamp = new Float(time / 1000);
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
        img.zPosition = zPosition;
      }
      else if (qName.equals("FocusPosition")) {
        zPosition = new Float(attributes.getValue("z"));
      }
      else if (qName.equals("Creation")) {
        String date = attributes.getValue("date"); // yyyy-mm-dd
        String time = attributes.getValue("time"); // hh:mm:ss
        creationDate = date + "T" + time;
      }
      else if (qName.equals("ObjectiveCalibration")) {
        store.setObjectiveNominalMagnification(new Integer(
          (int) Float.parseFloat(attributes.getValue("magnification"))), 0, 0);
        store.setObjectiveLensNA(new Float(
          attributes.getValue("numerical_aperture")), 0, 0);
        store.setObjectiveImmersion("Unknown", 0, 0);

        String objective = attributes.getValue("objective_name");
        String[] tokens = objective.split("_");

        store.setObjectiveManufacturer(tokens[0], 0, 0);
        if (tokens.length > 2) store.setObjectiveCorrection(tokens[2], 0, 0);
        else store.setObjectiveCorrection("Unknown", 0, 0);

        Float pixelSizeX = new Float(attributes.getValue("pixel_width"));
        Float pixelSizeY = new Float(attributes.getValue("pixel_height"));
        Float refractive = new Float(attributes.getValue("refractive_index"));

        // link Objective to Image
        String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
        store.setObjectiveID(objectiveID, 0, 0);
        for (int i=0; i<getSeriesCount(); i++) {
          store.setObjectiveSettingsObjective(objectiveID, i);
          store.setObjectiveSettingsRefractiveIndex(refractive, i);
          store.setDimensionsPhysicalSizeX(pixelSizeX, i, 0);
          store.setDimensionsPhysicalSizeY(pixelSizeY, i, 0);
        }
      }
      else if (qName.equals("ExcitationFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) exWaves.add(new Integer(wave));
        channelName = attributes.getValue("name");
      }
      else if (qName.equals("EmissionFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) emWaves.add(new Integer(wave));
        channelNames.add(channelName + " " + attributes.getValue("name"));
      }
      else if (qName.equals("Camera")) {
        store.setDetectorModel(attributes.getValue("name"), 0, 0);
        store.setDetectorType("Unknown", 0, 0);
        String detectorID = MetadataTools.createLSID("Detector", 0, 0);
        store.setDetectorID(detectorID, 0, 0);
        for (int i=0; i<getSeriesCount(); i++) {
          setSeries(i);
          for (int q=0; q<getSizeC(); q++) {
            store.setDetectorSettingsDetector(detectorID, i, q);
          }
        }
        setSeries(0);
      }
      else if (qName.equals("Binning")) {
        String binning = attributes.getValue("value");
        for (int i=0; i<getSeriesCount(); i++) {
          setSeries(i);
          for (int q=0; q<getSizeC(); q++) {
            store.setDetectorSettingsBinning(binning, i, q);
          }
        }
        setSeries(0);
      }
      else if (qName.equals("Gain")) {
        Float gain = new Float(attributes.getValue("value"));
        for (int i=0; i<getSeriesCount(); i++) {
          setSeries(i);
          for (int q=0; q<getSizeC(); q++) {
            store.setDetectorSettingsGain(gain, i, q);
          }
        }
        setSeries(0);
      }
      else if (qName.equals("PlateTemperature")) {
        Float temperature = new Float(attributes.getValue("value"));
        for (int i=0; i<getSeriesCount(); i++) {
          store.setImagingEnvironmentTemperature(temperature, i);
        }
      }
      else if (qName.equals("Plate")) {
        store.setPlateName(attributes.getValue("name"), nextPlate);

        for (int r=0; r<wellRows; r++) {
          for (int c=0; c<wellCols; c++) {
            store.setWellRow(new Integer(r), nextPlate, r*wellCols + c);
            store.setWellColumn(new Integer(c), nextPlate, r*wellCols + c);
          }
        }
        nextPlate++;
      }
      else if (qName.equals("Row")) {
        currentRow = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Column")) {
        currentCol = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Exposure") && openImage) {
        float exp = Float.parseFloat(attributes.getValue("time"));
        exposure = new Float(exp / 1000);
      }
      else if (qName.equals("NamingRows")) {
        rowName = attributes.getValue("begin");
        try {
          startRow = Integer.parseInt(rowName);
        }
        catch (NumberFormatException e) {
          startRow = rowName.charAt(0) - 'A' + 1;
        }
      }
      else if (qName.equals("NamingColumns")) {
        colName = attributes.getValue("begin");
        try {
          startCol = Integer.parseInt(colName);
        }
        catch (NumberFormatException e) {
          startCol = colName.charAt(0) - 'A' + 1;
        }
      }
      else if (qName.equals("offset_point")) {
        posX.add(new Float(attributes.getValue("x")));
        posY.add(new Float(attributes.getValue("y")));
      }
    }
  }

  class Image {
    public String filename;
    public boolean isTiff;
    public Float deltaT, exposure;
    public Float zPosition;
  }

}
