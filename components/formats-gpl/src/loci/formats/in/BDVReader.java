/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
  private String h5Id;
  private int sizeC = 0;
  private boolean timepointUsePattern = false;
  private Integer firstTimepoint = 0;
  private Integer lastTimepoint = 0;
  private Integer timepointIncrement = 1;
  private int seriesCount;
  private transient JHDFService jhdf;
  private int lastChannel = 0;

  private List<H5Coordinate> H5PositionList = new ArrayList<H5Coordinate>();
  private List<String> H5PathsToImageData = new ArrayList<String>();
  private List<String> cellObjectNames = new ArrayList<String>();

  // Store indexes of channels, does not have to be 0 indexed or ordered
  private List<Integer> channelIndexes = new ArrayList<Integer>();

  // Store all custom attributes for each setup ID
  private HashMap<Integer, HashMap<String, String>> setupAttributeList = new HashMap<Integer, HashMap<String, String>>();

  // Store the number of mipmap levels for each setup
  private HashMap<Integer, Integer> setupResolutionCounts = new HashMap<Integer, Integer>();

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
    store = makeFilterMetadata();
    in = new RandomAccessInputStream(id);
    in.setEncoding("ASCII");
    DefaultHandler handler = new BDVXMLHandler();
    try (RandomAccessInputStream s = new RandomAccessInputStream(id)) {
      // Setup details and Id of h5 file are parsed from XML
      XMLTools.parseXML(s, handler);
    }
    catch (IOException e) {
      throw new FormatException("Malformed XML", e);
    }
    if (StringUtils.isEmpty(h5Id)) {
      throw new FormatException("Could not find H5 file location in XML");
    }
    // The BDV XML will be available as an annotation
    store.setXMLAnnotationValue(bdvxml, 0);
    String xml_id = MetadataTools.createLSID("XMLAnnotation", 0); 
    store.setXMLAnnotationID(xml_id, 0);
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
  public String[] getUsedFiles(boolean noPixels) {
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
    try {
      super.close(fileOnly);
    }
    finally {
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
  }

  /* @see loci.formats.FormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    try {
      initializeJHDFService(h5Id);
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
    
    int seriesIndex = series;
    int requiredResolution = getResolution();
    if (hasFlattenedResolutions()) {
      // Number of resolutions can differ between setups
      int totalSeriesCount = 0;
      seriesIndex = 0;
      for (int setup : setupResolutionCounts.keySet()) {
        totalSeriesCount += setupResolutionCounts.get(setup);
        
        if (hasFlattenedResolutions()) {
          int numResolutions = core.get(seriesToCoreIndex(getSeries())).resolutionCount;
          requiredResolution = series % numResolutions;
        }
        if (totalSeriesCount > series) {
          requiredResolution = (series - (totalSeriesCount - setupResolutionCounts.get(setup))) % setupResolutionCounts.get(setup);
          break;
        }
        seriesIndex++;
      }
    }
    int currentSetup = (int)setupAttributeList.keySet().toArray()[seriesIndex];
    if (sizeC > 1) {
      // Locate the correct setup for the given channel
      int numChannelSetupFound = 0;
      for (int index: setupAttributeList.keySet()) {
        HashMap<String, String> setup = setupAttributeList.get(index);
        for (String key: setup.keySet()) {
          if (key.equals("channel")) {
            String value = setup.get(key);
            if (Integer.parseInt(value) == channelIndexes.get(channel)) {
              if (numChannelSetupFound == seriesIndex) {
                currentSetup = index;
                break;
              }
              numChannelSetupFound++;
            }
          }
        }
      }
    }
    String formattedTimepoint = String.format("t%05d",(firstTimepoint + (timepointIncrement * time)));
    String formattedSetup = String.format("s%02d", currentSetup);
    H5Coordinate imagePath = new H5Coordinate(formattedTimepoint, formattedSetup, ""+requiredResolution);
    if (!H5PathsToImageData.contains(imagePath.pathToImageData)) {
      LOGGER.warn("Attempting to retrieve invalid path: " + imagePath.pathToImageData);
    }

    int elementSize = jhdf.getElementSize(imagePath.pathToImageData);

    int[] arrayOrigin = new int[] {zslice, y, 0};
    int[] arrayDimension = new int[] {1, height, width};

    MDIntArray subBlock = jhdf.readIntBlockArray(imagePath.pathToImageData, arrayOrigin, arrayDimension);

    if (elementSize == 1) {
      byte[][] image = new byte[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (byte) subBlock.get(0, yy, xx);
        }
      }
      return image;
    }
    else if (elementSize == 2) {
      short[][] image = new short[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (short) subBlock.get(0, yy, xx);
        }
      }
      return image;
    }
    else {
      int[][] image = new int[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (int) subBlock.get(0, yy, xx);
        }
      }
      return image;
    }
  }

  private void parseStructure() throws FormatException {
    seriesCount = 0;
    core.clear();
    // read experiment structure and collect coordinates

    int numTimepoints = 1;
    if (timepointUsePattern && lastTimepoint > 0) {
      numTimepoints = lastTimepoint - firstTimepoint + 1;
      if (timepointIncrement > 0) {
        numTimepoints /= timepointIncrement;
      }
    }
    else {
      if (lastTimepoint == 0) lastTimepoint = firstTimepoint;
      numTimepoints = lastTimepoint - firstTimepoint + 1;
    }

    for (int timepoint = firstTimepoint; timepoint <= lastTimepoint; timepoint+=timepointIncrement) {
      String path_to_timepoint = String.format("t%05d", timepoint);
      LOGGER.info("Timepoint :" + path_to_timepoint );

      for (String setup : jhdf.getMember(path_to_timepoint)) {
        String path_to_setup = path_to_timepoint + "/" + setup;
        LOGGER.info("Setup :" + path_to_setup );
        if (jhdf.getMember(path_to_setup).size() > 0 && timepoint == firstTimepoint) {
          setupResolutionCounts.put(Integer.parseInt(setup.substring(1)), jhdf.getMember(path_to_setup).size());
        }
        for (String resolution : jhdf.getMember(path_to_setup)) {
          String path_to_site = path_to_setup + "/" + resolution;
          LOGGER.info("Site :" + path_to_site );  
          H5PositionList.add(new H5Coordinate(String.format("t%05d", timepoint), setup, resolution));
        }
      }
    }

    if (H5PositionList.size() == 0) {
      throw new FormatException("No series found in file...");
    }

    List<String> seriesNames = new ArrayList<String>();

    String formattedFirstTimepoint = String.format("t%05d", firstTimepoint);
    if (sizeC == 0) sizeC = 1;
    int sumOfResolutions = 0;
    for (H5Coordinate coord : H5PositionList) {
      if (jhdf.exists(coord.pathToImageData)) {
        H5PathsToImageData.add(coord.pathToImageData);
        
        // Don't create new series for each timepoint
        if (coord.timepoint.equals(formattedFirstTimepoint)) {
          int setupIndex = Integer.parseInt(coord.setup.substring(1));
          HashMap<String, String> setupAttributes = setupAttributeList.get(setupIndex);
          String firstChannelIndex = channelIndexes.size() > 0 ? channelIndexes.get(0).toString() : "";
          
          // Dont create a new series for each channel
          if (sizeC == 1 || (setupAttributes.containsKey("channel") && setupAttributes.get("channel").equals(firstChannelIndex))) {
            CoreMetadata m = new CoreMetadata();
            core.add(m);
            int resolutionsInThisSetup = setupResolutionCounts.get(setupIndex);
            if (hasFlattenedResolutions()) {
              setSeries(seriesCount);
            }
            else {
              setSeries(coreIndexToSeries(seriesCount));
              setResolution(seriesCount - sumOfResolutions);
              if (seriesCount == sumOfResolutions + resolutionsInThisSetup - 1) {
                sumOfResolutions += resolutionsInThisSetup;
              }
            }
    
            LOGGER.debug(coord.pathToImageData);
            int[] ctzyx = jhdf.getShape(coord.pathToImageData);
            m.sizeC = sizeC;
            m.sizeT = numTimepoints;
            m.sizeZ = ctzyx[0];
            m.sizeY = ctzyx[1];
            m.sizeX = ctzyx[2];
            m.resolutionCount = resolutionsInThisSetup;
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
    
            if (getResolution() == 0) {
              seriesNames.add(String.format("P_%s, W_%s_%s", coord.timepoint, coord.setup, coord.mipmapLevel));
            }
            seriesCount++;
          }
          else {
            // No need to store resolution counts for additional channel setups
            // This makes the assumption that channel setups will all have the same resolution count
            setupResolutionCounts.remove(setupIndex);
          }
        }
      }
    }

    if (seriesCount == 0) {
      throw new FormatException("No image data found...");
    }

    MetadataTools.populatePixels(store, this);

    for (int s=0; s<seriesNames.size(); s++) {
      store.setImageName(seriesNames.get(s), s);
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
    public String timepoint;
    public String setup;
    public String mipmapLevel;

    protected String pathToImageData;
    protected String pathToPosition;

    H5Coordinate(String timepoint, String setup, String mipmapLevel) {
        this.timepoint = timepoint;
        this.setup = setup;
        this.mipmapLevel = mipmapLevel;
        pathToPosition = this.timepoint + "/" + this.setup + "/" + this.mipmapLevel + "/cells/";
        this.pathToImageData = pathToPosition ;
        LOGGER.trace(pathToImageData);
    }

    public String toString() {
        return String.format("%s %s_%s", timepoint, setup, mipmapLevel);
    }
  }

  class BDVXMLHandler extends BaseHandler {
    private final StringBuilder xmlBuffer;
    private String currentQName;
    private boolean inPixels;
    private boolean parsingTimepoints;
    private boolean parsingAttributes;
    private boolean parsingViewSetups;
    private boolean parsingId;
    private int currentSetupIndex;

    public BDVXMLHandler() {
      xmlBuffer = new StringBuilder();
      inPixels = false;
    }
    
    private void parseIntegerString(String timepontPattern) {
      String[] parts = timepontPattern.split("-");
      if (DataTools.parseInteger(parts[0]) != null) {
        firstTimepoint = DataTools.parseInteger(parts[0]);
      }
      if (parts.length > 1 && DataTools.parseInteger(parts[1]) != null) {
        String[] parts2 = parts[1].split(":");
        if (DataTools.parseInteger(parts2[0]) != null) {
          lastTimepoint = DataTools.parseInteger(parts2[0]);
        }
        if (parts2.length > 1 && DataTools.parseInteger(parts2[1]) != null) {
          timepointIncrement = DataTools.parseInteger(parts2[1]);
        }
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (!inPixels || currentQName.indexOf("BinData") < 0) {
        if (currentQName.toLowerCase().equals("hdf5")) {
          String hdf5Contents = new String(ch, start, length);
          if (checkSuffix(hdf5Contents, "h5")) {
            String parent = new Location(currentId).getAbsoluteFile().getParent();
            h5Id = parent + File.separator + hdf5Contents;
          }
        }
        if (parsingTimepoints && currentQName.toLowerCase().equals("first")) {
          String timepoint = new String(ch, start, length);
          if (DataTools.parseInteger(timepoint) != null) {
            firstTimepoint = DataTools.parseInteger(timepoint);
          }
        }
        else if (parsingTimepoints && currentQName.toLowerCase().equals("last")) {
          String timepoint = new String(ch, start, length);
          if (DataTools.parseInteger(timepoint) != null) {
            lastTimepoint = DataTools.parseInteger(timepoint);
          }
        }
        else if (parsingTimepoints && currentQName.toLowerCase().equals("integerpattern")) {
          String timepointPattern = new String(ch, start, length);
          if (timepointUsePattern) parseIntegerString(timepointPattern);
        }
        if (parsingViewSetups && parsingId) {
          String setupId = new String(ch, start, length);
          if (DataTools.parseInteger(setupId) >= setupAttributeList.size()) {
            currentSetupIndex = DataTools.parseInteger(setupId);
            setupAttributeList.put(DataTools.parseInteger(setupId), new HashMap<String, String>());
          }
        }
        if (parsingViewSetups && parsingAttributes && !currentQName.isEmpty() && !currentQName.toLowerCase().equals("attributes")) {
          String attributeValue = new String(ch, start, length);
          setupAttributeList.get(currentSetupIndex).put(currentQName, attributeValue);
          if (currentQName.toLowerCase().equals("channel") && !channelIndexes.contains(Integer.parseInt(attributeValue))) {
            channelIndexes.add(Integer.parseInt(attributeValue));
            sizeC = channelIndexes.size();
          }
        }
        xmlBuffer.append(new String(ch, start, length));
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      currentQName = "";
      if (qName.toLowerCase().equals("timepoints")) {
        parsingTimepoints = false;
      }
      if (qName.toLowerCase().equals("attributes")) {
        parsingAttributes = false;
      }
      if (qName.toLowerCase().equals("viewsetup")) {
        parsingViewSetups = false;
      }
      if (qName.toLowerCase().equals("id")) {
        parsingId = false;
      }
      xmlBuffer.append("</");
      xmlBuffer.append(qName);
      xmlBuffer.append(">");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      currentQName = qName;

      if (qName.toLowerCase().equals("viewsetup")) {
        parsingViewSetups = true;
      }
      if (qName.toLowerCase().equals("id")) {
        parsingId = true;
      }
      if (qName.toLowerCase().equals("attributes")) {
        parsingAttributes = true;
      }
      if (qName.toLowerCase().equals("timepoints")) {
        parsingTimepoints = true;
        int typeIndex = attributes.getIndex("type");
        if (typeIndex != -1) {
          String timepointType = attributes.getValue(typeIndex);
          timepointUsePattern  = timepointType != null && timepointType.toLowerCase().equals("pattern");
        }
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
