/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
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
 * Reader for CellH5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a
 * href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CellH5Reader.java">Trac</a>,
 * <a
 * href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CellH5Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CellH5Reader extends FormatReader {

  // -- Constants --
  public static final String HDF_MAGIC_STRING = "HDF";

  public class CellH5Constants {
      public final static String PREFIX_PATH = "/sample/0/";
      public final static String IMAGE_PATH = "image/channel/";
      public final static String SEGMENTATION_PATH = "image/region/";

      public final static String PLATE = "plate/";
      public final static String WELL = "/experiment/";
      public final static String SITE = "/position/";
      public final static String DEFINITION = "/definition/";
      public final static String OBJECT = "object/";
      public final static String FEATURE = "feature/";
      public final static String BBOX = "bounding_box/";
      public final static String CLASS_LABELS = "object_classification/class_labels/";
      public final static String PREDICTED_CLASS_LABELS = "object_classification/prediction";
  }

  public class CellH5Coordinate {
      public String plate;
      public String well;
      public String site;

      protected String pathToImageData;
      protected String pathToSegmentationData;
      protected String pathToPosition;

      CellH5Coordinate(String plate, String well, String site) {
          this.plate = plate;
          this.well = well;
          this.site = site;

          pathToPosition = CellH5Constants.PREFIX_PATH + CellH5Constants.PLATE +
                             this.plate + CellH5Constants.WELL + this.well +
                             CellH5Constants.SITE + this.site + "/";

          this.pathToImageData = pathToPosition + CellH5Constants.IMAGE_PATH;
          this.pathToSegmentationData = pathToPosition + CellH5Constants.SEGMENTATION_PATH;

          System.out.println(pathToImageData);
      }

      public String toString() {
          return String.format("%s %s_%s", plate, well, site);
      }
  }

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private double minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private JHDFService jhdf;

  private MetadataStore store;
  private int lastChannel = 0;

  private List<CellH5Coordinate> CellH5PositionList = new LinkedList<CellH5Coordinate>();
  private List<String> CellH5PathsToImageData = new LinkedList<String>();
  private List<String> cellObjectNames = new LinkedList<String>();

  // Default colors for bounding box colors if no classification present
  private final int[][] COLORS = {{255, 0, 0}, {0, 255, 0}, {0, 0, 255},
                              {255, 255, 0}, {0, 255, 255}, {255, 0, 255},
                              {255, 255, 255}, {255, 0, 128}, {0, 255, 128},
                              {0, 128, 256}, {128, 0, 128}, {255, 128, 0},
                              {64, 128, 0}, {0, 64, 128}, {128, 0, 64}};

  private HDF5CompoundDataMap[] times = null;
  private HDF5CompoundDataMap[] classes = null;
  private HDF5CompoundDataMap[] bbox = null;

  // -- Constructor --

  /**
   * Constructs a new CellH5 HDF reader.
   */
  public CellH5Reader() {
    super("CellH5 (HDF)", "ch5");
    suffixSufficient = true;
    domains = new String[]{FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    String[] tokens = name.split("\\.(?=[^\\.]+$)");
    if (tokens[1].equals("ch5")) {
        return true;
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) {
      return false;
    }
    return stream.readString(blockLen).contains(HDF_MAGIC_STRING);
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

    boolean big = !isLittleEndian();

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
          DataTools.unpackBytes(rowData[i + x], buf, base + 2 * i, 2, big);
        }
      } else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + i * 4, 4, big);
        }
      } else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          int v = Float.floatToIntBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 4, 4, big);
        }
      } else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row];
        for (int i = 0; i < w; i++) {
          long v = Double.doubleToLongBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 8, 8, big);
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
      pixelSizeX = pixelSizeY = pixelSizeZ = 0;

      if (jhdf != null) {
        jhdf.close();
      }
      jhdf = null;
      lastChannel = 0;
    }
  }

  // -- Internal FormatReader API methods --


  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      ServiceFactory factory = new ServiceFactory();
      jhdf = factory.getInstance(JHDFService.class);
      jhdf.setFile(id);
    } catch (DependencyException e) {
      throw new MissingLibraryException(JHDFServiceImpl.NO_JHDF_MSG, e);
    }

    parseStructure();
    // The ImageJ RoiManager can not distinguish ROIs from different
    // Series. This is why they only will be loaded if the CellH5 contains two
    // image / series assuming that the first is the image and 2nd the labels
    if (seriesCount <= 2) {
        parseROIs(0);
    }
  }

  // -- Helper methods --

  private Object getImageData(int no, int y, int height) throws FormatException
  {
    int[] zct = getZCTCoords(no);
    int zslice = zct[0];
    int channel = zct[1];
    int time = zct[2];
    int width = getSizeX();

    int elementSize = jhdf.getElementSize(CellH5PathsToImageData.get(series));

    int[] arrayOrigin = new int[] {channel, time, zslice, 0, 0};
    int[] arrayDimension = new int[] {1, 1, 1, height, width};

    MDIntArray test = jhdf.readIntBlockArray(CellH5PathsToImageData.get(series),
      arrayOrigin, arrayDimension);

    if (elementSize == 1) {
      byte[][] image = new byte[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (byte) test.get(0, 0, 0, yy, xx);
        }
      }
      return image;
    }
    else if (elementSize == 2) {
      short[][] image = new short[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (short) test.get(0, 0, 0, yy, xx);
        }
      }
      return image;
    }
    else {
      int[][] image = new int[height][width];

      // Slice x, y dimension
      for (int yy = 0; yy < height; yy++) {
        for (int xx = 0; xx < width; xx++) {
          image[yy][xx] = (int) test.get(0, 0, 0, yy, xx);
        }
      }
      return image;
    }
  }

  private void parseStructure() throws FormatException {
    seriesCount = 0;
    pixelSizeX = pixelSizeY = pixelSizeZ = 1;
    core.clear();
    // read experiment structure and collect coordinates

    String path = CellH5Constants.PREFIX_PATH + CellH5Constants.PLATE;
    for (String plate : jhdf.getMember(path)) {
      path += plate + "/" + CellH5Constants.WELL;
      for (String well : jhdf.getMember(path)) {
        path += well + "/" + CellH5Constants.SITE;
        for (String site : jhdf.getMember(path)) {
          CellH5PositionList.add(new CellH5Coordinate(plate, well, site));
        }
      }
    }

    if (CellH5PositionList.size() == 0) {
      throw new FormatException("No series found in file...");
    }

    List<String> seriesNames = new LinkedList<String>();

    int s = 0;
    for (CellH5Coordinate coord : CellH5PositionList) {
      if (jhdf.exists(coord.pathToImageData)) {
        CoreMetadata core_s = new CoreMetadata();
        core.add(core_s);
        setSeries(s);

        LOGGER.debug(coord.pathToImageData);
        int[] ctzyx = jhdf.getShape(coord.pathToImageData);
        core.get(s).sizeC = ctzyx[0];
        core.get(s).sizeT = ctzyx[1];
        core.get(s).sizeZ = ctzyx[2];
        core.get(s).sizeY = ctzyx[3];
        core.get(s).sizeX = ctzyx[4];
        core.get(s).resolutionCount = 1;
        core.get(s).thumbnail = false;
        core.get(s).imageCount = getSizeC() * getSizeT() * getSizeZ();
        core.get(s).dimensionOrder = "XYZTC";
        core.get(s).rgb = false;
        core.get(s).thumbSizeX = 128;
        core.get(s).thumbSizeY = 128;
        core.get(s).orderCertain = false;
        core.get(s).littleEndian = true;
        core.get(s).interleaved = false;
        core.get(s).indexed = true;
        core.get(s).pixelType = FormatTools.UINT8;

        seriesNames.add(String.format("P_%s, W_%s_%s", coord.plate, coord.well, coord.site));
        CellH5PathsToImageData.add(coord.pathToImageData);
        s++;
        seriesCount++;
      }
    }

    s = seriesCount;
    for (CellH5Coordinate coord : CellH5PositionList) {
      if (jhdf.exists(coord.pathToSegmentationData)) {
        CoreMetadata core_s = new CoreMetadata();
        core.add(core_s);
        setSeries(s);

        LOGGER.debug(coord.pathToSegmentationData);
        int[] ctzyx = jhdf.getShape(coord.pathToSegmentationData);
        core.get(s).sizeC = ctzyx[0];
        core.get(s).sizeT = ctzyx[1];
        core.get(s).sizeZ = ctzyx[2];
        core.get(s).sizeY = ctzyx[3];
        core.get(s).sizeX = ctzyx[4];
        core.get(s).resolutionCount = 1;
        core.get(s).thumbnail = false;
        core.get(s).imageCount = getSizeC() * getSizeT() * getSizeZ();
        core.get(s).dimensionOrder = "XYZTC";
        core.get(s).rgb = false;
        core.get(s).thumbSizeX = 128;
        core.get(s).thumbSizeY = 128;
        core.get(s).orderCertain = false;
        core.get(s).littleEndian = true;
        core.get(s).interleaved = false;
        core.get(s).indexed = true;
        core.get(s).pixelType = FormatTools.UINT16;

        seriesNames.add(String.format("P_%s, W_%s_%s label image",
          coord.plate, coord.well, coord.site));
        CellH5PathsToImageData.add(coord.pathToSegmentationData);
        s++;
        seriesCount++;
      }
    }

    if (seriesCount == 0) {
      throw new FormatException("No image data found...");
    }

    store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    s = 0;
    for (String name : seriesNames) {
      store.setImageName(name, s);
      s++;
    }
    setSeries(0);
    parseCellObjects();
  }

  private void parseCellObjects() {
    String rootObject = CellH5Constants.DEFINITION + CellH5Constants.OBJECT;
    List<String> allObjects = jhdf.getMember(rootObject);
    for (String objectName : allObjects) {
      String objectType = (String) jhdf.readCompoundArrayDataMap(
        rootObject + objectName)[0].get("type");
      if (objectType.equals("region")) {
        cellObjectNames.add(objectName);
        LOGGER.debug(String.format("CellH5Reader: Found cell object %s", objectName));
      }
    }
  }

  private int getChannelIndexOfCellObjectName(String cellObjectName) {
    HDF5CompoundDataMap[] allImageRegions = jhdf.readCompoundArrayDataMap(CellH5Constants.DEFINITION + CellH5Constants.SEGMENTATION_PATH);
    for (int regionIdx = 0; regionIdx < allImageRegions.length; regionIdx++) {
      String regionName = (String) allImageRegions[regionIdx].get("region_name");
      Integer channelIdx = (Integer) allImageRegions[regionIdx].get("channel_idx");
      if (regionName.endsWith(cellObjectName)) {
        return channelIdx.intValue();
      }
    }
    return -1;
  }

  private static int[] hex2Rgb(String colorStr) {
    return new int[]{
      Integer.valueOf(colorStr.substring(1, 3), 16),
      Integer.valueOf(colorStr.substring(3, 5), 16),
      Integer.valueOf(colorStr.substring(5, 7), 16)};
  }

  private void parseROIs(int s) {
    int objectIdx = 0;
    List<int[]> classColors = new LinkedList<int[]>();
    int roiIndexOffset = 0;

    CellH5Coordinate coord = CellH5PositionList.get(0);

    for (String cellObjectName : cellObjectNames) {
      LOGGER.info(
        String.format("Parse segmentation ROIs for cell object %s : %d",
        cellObjectName, objectIdx));
      String featureName = CellH5Constants.FEATURE + cellObjectName + "/";
      String pathToBoundingBox = coord.pathToPosition +
        featureName + CellH5Constants.BBOX;
      String pathToClassDefinition = CellH5Constants.DEFINITION +
        featureName + CellH5Constants.CLASS_LABELS;
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
            featureName + CellH5Constants.PREDICTED_CLASS_LABELS);
        }
      }

      if (jhdf.exists(pathToBoundingBox)) {
        bbox = jhdf.readCompoundArrayDataMap(pathToBoundingBox);
        times = jhdf.readCompoundArrayDataMap(
          coord.pathToPosition + CellH5Constants.OBJECT + cellObjectName);
        int roiChannel = getChannelIndexOfCellObjectName(cellObjectName);
        int roiZSlice = (Integer) 0;

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

          store.setRectangleX((double) left , roiManagerRoiIndex, 0);
          store.setRectangleY((double) top , roiManagerRoiIndex, 0);
          store.setRectangleWidth((double) width , roiManagerRoiIndex, 0);
          store.setRectangleHeight((double) height , roiManagerRoiIndex, 0);

          store.setRectangleTheT(
            new NonNegativeInteger(roiTime + 1), roiManagerRoiIndex, 0);
          store.setRectangleTheC(
            new NonNegativeInteger(roiChannel + 1), roiManagerRoiIndex, 0);
          store.setRectangleTheZ(
            new NonNegativeInteger(roiZSlice + 1), roiManagerRoiIndex, 0);
          store.setROIName(cellObjectName + " " + String.valueOf(objectLabelId),
            roiManagerRoiIndex);
          store.setRectangleID(
            cellObjectName + "::" + String.valueOf(objectLabelId),
            roiManagerRoiIndex, 0);

          Color strokeColor;
          if (hasClassification) {
            int classLabelIDx = (Integer) classes[roiIndex].get("label_idx");
            int[] rgb = classColors.get(classLabelIDx);
            strokeColor = new Color(rgb[0], rgb[1], rgb[2], 0xff);
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
}
