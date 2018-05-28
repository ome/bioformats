/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2018 Open Microscopy Environment:
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

import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.hdf5.HDF5CompoundDataMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JHDFService;
import loci.formats.services.JHDFServiceImpl;

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;

/**
 * Reader for BDV files.
 */
public class BDVReader extends FormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";
  public static final String BDV_MAGIC_STRING = "SpimData";
  // Default colors for bounding box colors if no classification present
  private final int[][] COLORS = {{255, 0, 0}, {0, 255, 0}, {0, 0, 255},
                              {255, 255, 0}, {0, 255, 255}, {255, 0, 255},
                              {255, 255, 255}, {255, 0, 128}, {0, 255, 128},
                              {0, 128, 256}, {128, 0, 128}, {255, 128, 0},
                              {64, 128, 0}, {0, 64, 128}, {128, 0, 64}};

  // -- Fields --

  private MetadataStore store;
  private String bdvxml;
  String xmlId;
  String h5Id;
  private int sizeC = 0;
  private Integer firstTimepoint = 0;
  private Integer lastTimepoint = 0;
  private int seriesCount;
  private transient JHDFService jhdf;
  private int lastChannel = 0;

  private List<H5Coordinate> H5PositionList = new ArrayList<H5Coordinate>();
  private List<String> H5PathsToImageData = new ArrayList<String>();
  private List<String> cellObjectNames = new ArrayList<String>();

  private HDF5CompoundDataMap[] times = null;
  private HDF5CompoundDataMap[] classes = null;
  private HDF5CompoundDataMap[] bbox = null;

  // -- Constructor --

  /**
   * Constructs a new BDV reader.
   */
  public BDVReader() {
    super("BDV", "xml");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    xmlId = id;
    store = makeFilterMetadata();
    in = new RandomAccessInputStream(xmlId);
    in.setEncoding("ASCII");
    DefaultHandler handler = new BDVXMLHandler();
    try {
      RandomAccessInputStream s = new RandomAccessInputStream(id);
      XMLTools.parseXML(s, handler);
      s.close();
    }
    catch (IOException e) {
      throw new FormatException("Malformed XML", e);
    }
    store.setXMLAnnotationValue(bdvxml, 0);
    initializeJHDFService(h5Id);
    parseStructure();

    // The ImageJ RoiManager can not distinguish ROIs from different
    // Series. This is why they only will be loaded if the BDV contains two
    // image / series assuming that the first is the image and 2nd the labels
    if (seriesCount <= 2 && getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
      parseROIs(0);
    }
  }
  
  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return new String[] {currentId, h5Id};
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 100;
    if (!FormatTools.validStream(stream, blockLen, false)) {
      return false;
    }
    return stream.readString(blockLen).contains(BDV_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed()) {
      return null;
    }

    if (lastChannel < 0) {
      return null;
    }

    byte[][] lut = new byte[3][256];
    for (int i = 0; i < 256; i++) {
      switch (lastChannel) {
        case 0:
          // red
          lut[0][i] = (byte) (i & 0xff);
          break;
        case 1:
          // green
          lut[1][i] = (byte) (i & 0xff);
          break;
        case 2:
          // blue
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 3:
          // cyan
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 4:
          // magenta
          lut[0][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 5:
          // yellow
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          break;
        default:
          // gray
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
      }
    }
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    lastChannel = getZCTCoords(no)[1];

    // pixel data is stored in XYZ blocks
    Object image = getImageData(no, y, h);

    boolean little = isLittleEndian();

    // images is of type byte[][]. Left these checks and unpacking
    // in the code for feature data types
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    for (int row = 0; row < h; row++) {
      int base = row * w * bpp;
      if (image instanceof byte[][]) {
        byte[][] data = (byte[][]) image;
        byte[] rowData = data[row];
        System.arraycopy(rowData, x, buf, row * w, w);
      } else if (image instanceof short[][]) {
        short[][] data = (short[][]) image;
        short[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + 2 * i, 2, little);
        }
      } else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + i * 4, 4, little);
        }
      } else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          int v = Float.floatToIntBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 4, 4, little);
        }
      } else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          long v = Double.doubleToLongBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 8, 8, little);
        }
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesCount = 0;
      H5PositionList.clear();
      H5PathsToImageData.clear();
      cellObjectNames.clear();
      if (jhdf != null) {
        jhdf.close();
      }
      jhdf = null;
      lastChannel = 0;
    }
  }

  /* @see loci.formats.FormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    try {
      initializeJHDFService(currentId);
    }
    catch (MissingLibraryException e) {
      throw new IOException(e);
    }
  }

  // -- Helper methods --

  private void initializeJHDFService(String id) throws IOException, MissingLibraryException {
    try {
      ServiceFactory factory = new ServiceFactory();
      jhdf = factory.getInstance(JHDFService.class);
      jhdf.setFile(id);
    } catch (DependencyException e) {
      throw new MissingLibraryException(JHDFServiceImpl.NO_JHDF_MSG, e);
    }
  }

  private Object getImageData(int no, int y, int height) throws FormatException
  {
    int[] zct = getZCTCoords(no);
    int zslice = zct[0];
    int channel = zct[1];
    int time = zct[2];
    int width = getSizeX();

    int elementSize = jhdf.getElementSize(H5PathsToImageData.get(series));

    int[] arrayOrigin = new int[] {zslice, y, 0};
    int[] arrayDimension = new int[] {1, height, width};

    MDIntArray test = jhdf.readIntBlockArray(H5PathsToImageData.get(series),
      arrayOrigin, arrayDimension);

    if (elementSize == 1) {
      byte[][] image = new byte[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (byte) test.get(0, yy, xx);
        }
      }
      return image;
    }
    else if (elementSize == 2) {
      short[][] image = new short[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (short) test.get(0, yy, xx);
        }
      }
      return image;
    }
    else {
      int[][] image = new int[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (int) test.get(0, yy, xx);
        }
      }
      return image;
    }
  }

  private void parseStructure() throws FormatException {
    seriesCount = 0;
    core.clear();
    // read experiment structure and collect coordinates

    String path_to_plate = "t00000";
    LOGGER.info("Plate :" + path_to_plate );
    int resolutionCount = 0;
    for (String plate : jhdf.getMember(path_to_plate)) {
      String path_to_well = path_to_plate + "/" + plate;
      LOGGER.info("Well :" + path_to_well );
      if (jhdf.getMember(path_to_well).size() > resolutionCount) {
        resolutionCount = jhdf.getMember(path_to_well).size();
      }
      for (String well : jhdf.getMember(path_to_well)) {
        String path_to_site = path_to_well + "/" + well;
        LOGGER.info("Site :" + path_to_site );
        for (String site : jhdf.getMember(path_to_site)) {    
          H5PositionList.add(new H5Coordinate("t00000", plate, well));
        }
      }
    }

    if (H5PositionList.size() == 0) {
      throw new FormatException("No series found in file...");
    }

    List<String> seriesNames = new ArrayList<String>();
    List<String> seriesPlate = new ArrayList<String>();
    List<String> seriesWell = new ArrayList<String>();
    List<String> seriesSite = new ArrayList<String>();

    if (sizeC == 0) sizeC = 1;
    for (H5Coordinate coord : H5PositionList) {
      if (jhdf.exists(coord.pathToImageData)) {
        CoreMetadata m = new CoreMetadata();
        core.add(m);
        if (hasFlattenedResolutions()) {
          setSeries(seriesCount);
        }
        else {
          setSeries(seriesCount / resolutionCount);
          setResolution(seriesCount % resolutionCount);
        }

        LOGGER.debug(coord.pathToImageData);
        int[] ctzyx = jhdf.getShape(coord.pathToImageData);
        m.sizeC = sizeC;
        m.sizeT = lastTimepoint - firstTimepoint + 1;
        m.sizeZ = ctzyx[0];
        m.sizeY = ctzyx[1];
        m.sizeX = ctzyx[2];
        m.resolutionCount = resolutionCount;
        m.thumbnail = false;
        m.imageCount = getSizeC() * getSizeT() * getSizeZ();
        m.dimensionOrder = "XYZTC";
        m.rgb = false;
        m.thumbSizeX = 128;
        m.thumbSizeY = 128;
        m.orderCertain = false;
        m.littleEndian = true;
        m.interleaved = false;
        m.indexed = true;
        int bpp = jhdf.getElementSize(coord.pathToImageData);
        if (bpp==1) {
          m.pixelType = FormatTools.UINT8;
        }
        else if (bpp==2) {
          m.pixelType = FormatTools.UINT16;
        }
        else if (bpp==4) {
          m.pixelType = FormatTools.INT32;
        }
        else {
          throw new FormatException("Pixel type not understood. Only 8, 16 and 32 bit images supported");
        }

        seriesNames.add(String.format("P_%s, W_%s_%s", coord.plate, coord.well, coord.site));
        seriesPlate.add(coord.plate);
        seriesWell.add(coord.well);
        seriesSite.add(coord.site);
        H5PathsToImageData.add(coord.pathToImageData);
        seriesCount++;
      }
    }

    if (seriesCount == 0) {
      throw new FormatException("No image data found...");
    }

    MetadataTools.populatePixels(store, this);

    for (int s=0; s<seriesNames.size(); s++) {
      String image_id = MetadataTools.createLSID("Image", s);  
      store.setImageName(seriesNames.get(s), s);

      String plate_id =  MetadataTools.createLSID("Plate", 0);

      store.setPlateID(plate_id, 0);
      store.setPlateName(seriesPlate.get(s), 0);

      String well_id =  MetadataTools.createLSID("Well", 0);
      store.setWellID(well_id, 0, 0);

      String cellh5WellCoord = seriesWell.get(s); 
      String wellRowLetter = cellh5WellCoord.substring(0, 1);
      String wellColNumber = cellh5WellCoord.substring(1);

      int wellRowLetterIndex = "ABCDEFGHIJKLMNOP".indexOf(wellRowLetter);
      int wellColNumberIndex = -1;
      try {
        wellColNumberIndex = Integer.parseInt(wellColNumber);
      } catch (NumberFormatException e){
        LOGGER.error("Number format exception caught while parsing well column number");
      }

      if (wellRowLetterIndex > -1 && wellColNumberIndex > 0) {
        store.setWellRow(new NonNegativeInteger(wellRowLetterIndex), 0, 0);
        store.setWellColumn(new NonNegativeInteger(wellColNumberIndex - 1), 0, 0);
      } else {
        store.setWellRow(new NonNegativeInteger(0), 0, 0);
        store.setWellColumn(new NonNegativeInteger(0), 0, 0);
      }

      store.setWellExternalIdentifier(cellh5WellCoord, 0, 0);

      String site_id = MetadataTools.createLSID("WellSample", 0);
      store.setWellSampleID(site_id, 0, 0, 0);
      store.setWellSampleIndex(NonNegativeInteger.valueOf(seriesSite.get(s)), 0, 0, 0);
      store.setWellSampleImageRef(image_id, 0, 0, 0);   
    }
    setSeries(0);
  }

  private int getChannelIndexOfCellObjectName(String cellObjectName) {
    HDF5CompoundDataMap[] allImageRegions =
      jhdf.readCompoundArrayDataMap(
      H5Constants.DEFINITION + H5Constants.SEGMENTATION_PATH);
    for (int regionIdx = 0; regionIdx < allImageRegions.length; regionIdx++) {
      String regionName = (String) allImageRegions[regionIdx].get("region_name");
      Integer channelIdx = (Integer) allImageRegions[regionIdx].get("channel_idx");
      if (regionName.endsWith(cellObjectName)) {
        return channelIdx.intValue();
      }
    }
    return -1;
  }

  private static Color hex2Rgb(String colorStr) {
    int red = Integer.parseInt(colorStr.substring(1, 3), 16);
    int green = Integer.parseInt(colorStr.substring(3, 5), 16);
    int blue = Integer.parseInt(colorStr.substring(5, 7), 16);
    return new Color(red, green, blue, 0xff);
  }

  private void parseROIs(int s) {
    int objectIdx = 0;
    List<Color> classColors = new ArrayList<Color>();
    int roiIndexOffset = 0;

    H5Coordinate coord = H5PositionList.get(0);

    for (String cellObjectName : cellObjectNames) {
      LOGGER.info("Parse segmentation ROIs for cell object {} : {}",
        cellObjectName, objectIdx);
      String featureName = H5Constants.FEATURE + cellObjectName + "/";
      String pathToBoundingBox = coord.pathToPosition +
        featureName + H5Constants.BBOX;
      String pathToClassDefinition = H5Constants.DEFINITION +
        featureName + H5Constants.CLASS_LABELS;
      boolean hasClassification = false;
      if (jhdf.exists(pathToClassDefinition)) {
        String classColorHexString;
        HDF5CompoundDataMap[] classDef = jhdf.readCompoundArrayDataMap(pathToClassDefinition);
        for (int cls = 0; cls < classDef.length; cls++) {
          classColorHexString = (String) classDef[cls].get("color");
          classColors.add(hex2Rgb(classColorHexString));
        }
        if (classDef.length > 0) {
          hasClassification = true;
          classes = jhdf.readCompoundArrayDataMap(coord.pathToPosition +
            featureName + H5Constants.PREDICTED_CLASS_LABELS);
        }
      }

      if (jhdf.exists(pathToBoundingBox)) {
        bbox = jhdf.readCompoundArrayDataMap(pathToBoundingBox);
        times = jhdf.readCompoundArrayDataMap(
          coord.pathToPosition + H5Constants.OBJECT + cellObjectName);
        int roiChannel = getChannelIndexOfCellObjectName(cellObjectName);
        int roiZSlice = 0;

        for (int roiIndex = 0; roiIndex < bbox.length; roiIndex++) {
          int roiManagerRoiIndex = roiIndex + roiIndexOffset;
          int roiTime = (Integer) times[roiIndex].get("time_idx");
          int objectLabelId = (Integer) times[roiIndex].get("obj_label_id");

          int left = (Integer) bbox[roiIndex].get("left");
          int right = (Integer) bbox[roiIndex].get("right");
          int top = (Integer) bbox[roiIndex].get("top");
          int bottom = (Integer) bbox[roiIndex].get("bottom");
          int width = right - left;
          int height = bottom - top;

          String roiID = MetadataTools.createLSID("ROI", roiManagerRoiIndex);
          store.setROIID(roiID, roiManagerRoiIndex);
          store.setImageROIRef(roiID, s, roiManagerRoiIndex);

          store.setROIName(cellObjectName + " " + objectLabelId, roiManagerRoiIndex);

          String shapeID =MetadataTools.createLSID("Shape", roiManagerRoiIndex, 0);
          store.setRectangleID(shapeID, roiManagerRoiIndex, 0);

          store.setRectangleX((double) left, roiManagerRoiIndex, 0);
          store.setRectangleY((double) top, roiManagerRoiIndex, 0);
          store.setRectangleWidth((double) width, roiManagerRoiIndex, 0);
          store.setRectangleHeight((double) height, roiManagerRoiIndex, 0);
          store.setRectangleText(cellObjectName, roiManagerRoiIndex, 0);
          store.setRectangleTheT(new NonNegativeInteger(roiTime), roiManagerRoiIndex, 0);
          store.setRectangleTheC(new NonNegativeInteger(roiChannel), roiManagerRoiIndex, 0);
          store.setRectangleTheZ(new NonNegativeInteger(roiZSlice), roiManagerRoiIndex, 0);

          Color strokeColor;
          if (hasClassification) {
            int classLabelIDx = (Integer) classes[roiIndex].get("label_idx");
            strokeColor = classColors.get(classLabelIDx);
          } else {
            strokeColor = new Color(COLORS[objectIdx][0],
              COLORS[objectIdx][1], COLORS[objectIdx][2], 0xff);
          }
          store.setRectangleStrokeColor(strokeColor, roiManagerRoiIndex, 0);
        }

        objectIdx++;
        roiIndexOffset += bbox.length;
      }
      else {
        LOGGER.info("No Segmentation data found...");
        break;
      }
    }
  }

  // -- Helper class --
  
  private class H5Constants {
    public final static String SEGMENTATION_PATH = "image/region/";
    public final static String DEFINITION = "/definition/";
    public final static String OBJECT = "object/";
    public final static String FEATURE = "feature/";
    public final static String BBOX = "bounding_box/";
    public final static String CLASS_LABELS = "object_classification/class_labels/";
    public final static String PREDICTED_CLASS_LABELS = "object_classification/prediction";
  }

  private class H5Coordinate {
    public String plate;
    public String well;
    public String site;

    protected String pathToImageData;
    protected String pathToPosition;

    H5Coordinate(String plate, String well, String site) {
        this.plate = plate;
        this.well = well;
        this.site = site;
        pathToPosition = this.plate + "/" + this.well + "/" + this.site + "/cells/";
        this.pathToImageData = pathToPosition ;
        LOGGER.trace(pathToImageData);
    }

    public String toString() {
        return String.format("%s %s_%s", plate, well, site);
    }
  }

  class BDVXMLHandler extends BaseHandler {
    private final StringBuilder xmlBuffer;
    private String currentQName;
    private boolean inPixels;
    private boolean parsingTimepoints;

    public BDVXMLHandler() {
      xmlBuffer = new StringBuilder();
      inPixels = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (!inPixels || currentQName.indexOf("BinData") < 0) {
        if (currentQName.equals("hdf5")) {
          String hdf5Contents = new String(ch, start, length);
          if (checkSuffix(hdf5Contents, "h5")) {
            String parent = new Location(currentId).getAbsoluteFile().getParent();
            h5Id = parent + File.separator + hdf5Contents;
          }
        }
        if (parsingTimepoints && currentQName.equals("first")) {
          String timepoint = new String(ch, start, length);
          if (DataTools.parseInteger(timepoint) != null) {
            firstTimepoint = DataTools.parseInteger(timepoint);
          }
        }
        else if (parsingTimepoints && currentQName.equals("last")) {
          String timepoint = new String(ch, start, length);
          if (DataTools.parseInteger(timepoint) != null) {
            lastTimepoint = DataTools.parseInteger(timepoint);
          }
        }
        xmlBuffer.append(new String(ch, start, length));
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Timepoints")) {
        parsingTimepoints = false;
      }
      xmlBuffer.append("</");
      xmlBuffer.append(qName);
      xmlBuffer.append(">");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      currentQName = qName;
      if (qName.equals("Channel")) {
        sizeC++;
      }
      if (qName.equals("Timepoints")) {
        parsingTimepoints = true;
      }
      xmlBuffer.append("<");
      xmlBuffer.append(qName);
      for (int i=0; i<attributes.getLength(); i++) { 
        String key = XMLTools.escapeXML(attributes.getQName(i));
        String value = XMLTools.escapeXML(attributes.getValue(i));
        xmlBuffer.append(" ");
        xmlBuffer.append(key);
        xmlBuffer.append("=\"");
        xmlBuffer.append(value);
        xmlBuffer.append("\"");
      }
      xmlBuffer.append(">");
    }

    @Override
    public void endDocument() {
      bdvxml = xmlBuffer.toString();
    }
  }
}
