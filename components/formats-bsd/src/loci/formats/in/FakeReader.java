/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import static ome.xml.model.Pixels.getPhysicalSizeXUnitXsdDefault;
import static ome.xml.model.Pixels.getPhysicalSizeYUnitXsdDefault;
import static ome.xml.model.Pixels.getPhysicalSizeZUnitXsdDefault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.ResourceNamer;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import ome.specification.XMLMockObjects;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.MapPair;
import ome.xml.model.OME;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.units.UNITS;

/**
 * FakeReader is the file format reader for faking input data.
 * It is mainly useful for testing.
 * <p>Examples:<ul>
 *  <li>showinf 'multi-series&amp;series=11&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake' -series 9</li>
 *  <li>showinf '8bit-signed&amp;pixelType=int8&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '8bit-unsigned&amp;pixelType=uint8&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '16bit-signed&amp;pixelType=int16&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '16bit-unsigned&amp;pixelType=uint16&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-signed&amp;pixelType=int32&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-unsigned&amp;pixelType=uint32&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '32bit-floating&amp;pixelType=float&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf '64bit-floating&amp;pixelType=double&amp;sizeZ=3&amp;sizeC=5&amp;sizeT=7&amp;sizeY=50.fake'</li>
 *  <li>showinf 'SPW&amp;plates=2&amp;plateRows=3&amp;plateCols=3&amp;fields=8&amp;plateAcqs=5.fake'</li>
 *  <li>showinf 'SPW&amp;screens=2&amp;plates=1&amp;plateRows=3&amp;plateCols=3&amp;fields=1&amp;plateAcqs=1.fake'</li>
 *  <li>showinf 'Plate&amp;screens=0&amp;plates=1&amp;plateRows=3&amp;plateCols=3&amp;fields=8&amp;plateAcqs=5.fake'</li>
 *  <li>showinf 'regions&amp;points=10&amp;ellipses=5&amp;rectangles=10.fake'</li>
 *  <li>showinf 'pyramid&amp;sizeX=10000&amp;sizeY=10000&amp;resolutions=5&amp;resolutionScale=2.fake' -noflat -resolution 4</li>
 * </ul></p>
 */
public class FakeReader extends FormatReader {

  // -- Constants --
  private static final long ANN_LONG_VALUE = 365;
  private static final Double ANN_DOUBLE_VALUE = 0.111;
  private static final String ANNOTATION_PREFIX = "Annotation:";
  private static final String ANNOTATION_NAMESPACE = "fake-reader";
  private static final String ANN_TERM_VALUE = "Term:";
  private static final String ANN_TAG_VALUE = "Tag:";
  private static final Timestamp ANN_TIME_VALUE = new Timestamp("1970-01-01T00:00:00");
  private static final boolean ANN_BOOLEAN_VALUE = true;
  private static final String ANN_COMMENT_VALUE = "Comment:";
  private static final String ANN_XML_VALUE_START = "<dummyXml>";
  private static final String ANN_XML_VALUE_END = "</dummyXml>";
  private static final String ROI_PREFIX = "ROI:";
  private static final String SHAPE_PREFIX = "Shape:";

  public static final int BOX_SIZE = 10;
  private static final int ROI_SPACING = 10;

  public static final int DEFAULT_SIZE_X = 512;
  public static final int DEFAULT_SIZE_Y = 512;
  public static final int DEFAULT_SIZE_Z = 1;
  public static final int DEFAULT_SIZE_C = 1;
  public static final int DEFAULT_SIZE_T = 1;
  public static final int DEFAULT_PIXEL_TYPE = FormatTools.UINT8;
  public static final int DEFAULT_RGB_CHANNEL_COUNT = 1;
  public static final String DEFAULT_DIMENSION_ORDER = "XYZCT";

  public static final int DEFAULT_RESOLUTION_SCALE = 2;

  private static final String TOKEN_SEPARATOR = "&";
  private static final long SEED = 0xcafebabe;

  // -- Fields --

  /* dimensions per image */
  private int sizeX = DEFAULT_SIZE_X;
  private int sizeY = DEFAULT_SIZE_Y;
  private int sizeZ = DEFAULT_SIZE_Z;
  private int sizeC = DEFAULT_SIZE_C;
  private int sizeT = DEFAULT_SIZE_T;

  /** exposure time per plane info */
  private Time exposureTime = null;

  /* physical sizes */
  private Length physicalSizeX, physicalSizeY, physicalSizeZ;

  /* annotation counts per file */
  private int annBool = 0;
  private int annComment = 0;
  private int annDouble = 0;
  private int annLong = 0;
  private int annMap = 0;
  private int annTime = 0;
  private int annTag = 0;
  private int annTerm = 0;
  private int annXml = 0;
  private int annotationCount = 0;
  private int annotationBoolCount = 0;
  private int annotationCommentCount = 0;
  private int annotationDoubleCount = 0;
  private int annotationLongCount = 0;
  private int annotationMapCount = 0;
  private int annotationTagCount = 0;
  private int annotationTermCount = 0;
  private int annotationTimeCount = 0;
  private int annotationXmlCount = 0;

  /* ROIs per image*/
  private int ellipses = 0;
  private int labels = 0;
  private int lines = 0;
  private int masks = 0;
  private int points = 0;
  private int polygons = 0;
  private int polylines = 0;
  private int rectangles = 0;
  private int roiCount = 0;

  /** Scale factor for gradient, if any. */
  private double scaleFactor = 1;

  /** 8-bit lookup table, if indexed color, one per channel. */
  private byte[][][] lut8 = null;

  /** 16-bit lookup table, if indexed color, one per channel. */
  private short[][][] lut16 = null;

  /** For indexed color, mapping from indices to values and vice versa. */
  private int[][] indexToValue = null, valueToIndex = null;

  /** Channel of last opened image plane. */
  private int ac = 0;

  /** Properties companion file which can be associated with this fake file */
  private String iniFile;

  /** List of used files if the fake is a SPW structure */
  private List<String> fakeSeries = new ArrayList<String>();

  private OMEXMLMetadata omeXmlMetadata;

  private OMEXMLService omeXmlService;

  private transient int screens = 0;
  private transient int plates = 0;
  private transient int plateRows = 0;
  private transient int plateCols = 0;
  private transient int fields = 0;
  private transient int plateAcqs = 0;

  /**
   * Read byte-encoded metadata from the given plane.
   * @see FakeReader#readSpecialPixels(byte[], int, boolean, int, boolean)
   */
  public static int[] readSpecialPixels(byte[] plane) {
    return readSpecialPixels(plane, FormatTools.UINT8, true);
  }

  /**
   * Read byte-encoded metadata from the given plane.
   * @see FakeReader#readSpecialPixels(byte[], int, boolean, int, boolean)
   */
  public static int[] readSpecialPixels(
      byte[] plane, int pixelType, boolean little) {
    return readSpecialPixels(plane, pixelType, little, 1, false);
  }

  /**
   * Read byte-encoded metadata from the given plane.
   * <p>
   * FakeReader encodes the following information in the upper-left
   * section of each plane: series, plane number, Z index, C index, T
   * index. Given an image plane generated by FakeReader (or by
   * anything that follows the same encoding rules), this method
   * decodes the above information and returns it as an array of
   * integers.
   * <p>
   * Note that the above numbers are encoded as valid pixels, so that
   * the information is still retrievable after conversion to a
   * pixel-oriented image format (this allows to use the class for
   * testing the ImageJ plugin). Among other things, this implies that
   * the maximum encodable values depend on the pixel type.
   * <p>
   * <b>Note:</b>
   * <p>
   * In the case of indexed color data, the returned values will be
   * the indices of the actual values in the relevant lookup table
   * (see {@link FakeReader#get8BitLookupTable} and
   * {@link FakeReader#get16BitLookupTable}).
   *
   * @param plane an image plane as a byte array
   * @param pixelType the pixel type of the image; see
   *   {@link loci.formats.FormatTools}
   * @param little true if the byte order is little-endian, false otherwise
   * @param rgb the RGB channel count
   * @param interleaved true if the channels are interleaved, false otherwise
   * @return an array of integers representing, in order: series;
   *   plane number; Z index; C index, T index
   */
  public static int[] readSpecialPixels(byte[] plane, int pixelType,
      boolean little, int rgb, boolean interleaved) {
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    int[] idx = new int[5];  // S, no., Z, C, T
    for (int i = 0; i < idx.length; i++) {
      int offset = i * BOX_SIZE * bpp * (interleaved ? rgb : 1);
      if (pixelType == FormatTools.FLOAT) {
        idx[i] = (int) DataTools.bytesToFloat(plane, offset, bpp, little);
      } else if (pixelType == FormatTools.DOUBLE) {
        idx[i] = (int) DataTools.bytesToDouble(plane, offset, bpp, little);
      } else if (2 == bpp) {
        idx[i] = (int) DataTools.bytesToShort(plane, offset, bpp, little);
      } else {
        idx[i] = DataTools.bytesToInt(plane, offset, bpp, little);
      }
    }
    return idx;
  }

  // -- Constructor --

  /** Constructs a new fake reader. */
  public FakeReader() {
    super("Simulated data", "fake");
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return ac < 0 || lut8 == null ? null : lut8[ac];
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return ac < 0 || lut16 == null ? null : lut16[ac];
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    final int s = getSeries();
    final int pixelType = getPixelType();
    final int bpp = FormatTools.getBytesPerPixel(pixelType);
    final boolean signed = FormatTools.isSigned(pixelType);
    final boolean floating = FormatTools.isFloatingPoint(pixelType);
    final int rgb = getRGBChannelCount();
    final boolean indexed = isIndexed();
    final boolean little = isLittleEndian();
    final boolean interleaved = isInterleaved();

    final int[] zct = getZCTCoords(no);
    final int zIndex = zct[0], cIndex = zct[1], tIndex = zct[2];
    ac = cIndex;

    // integer types start gradient at the smallest value
    long min = signed ? (long) -Math.pow(2, 8 * bpp - 1) : 0;
    if (floating) min = 0; // floating point types always start at 0

    for (int cOffset=0; cOffset<rgb; cOffset++) {
      int channel = rgb * cIndex + cOffset;
      for (int row=0; row<h; row++) {
        int yy = y + row;
        for (int col=0; col<w; col++) {
          int xx = x + col;
          long pixel = min + xx;

          // encode various information into the image plane
          boolean specialPixel = false;
          if (yy < BOX_SIZE) {
            int grid = xx / BOX_SIZE;
            specialPixel = true;
            switch (grid) {
              case 0:
                pixel = s;
                break;
              case 1:
                pixel = no;
                break;
              case 2:
                pixel = zIndex;
                break;
              case 3:
                pixel = channel;
                break;
              case 4:
                pixel = tIndex;
                break;
              default:
                // just a normal pixel in the gradient
                specialPixel = false;
            }
          }

          // if indexed color with non-null LUT, convert value to index
          if (indexed) {
            if (lut8 != null) pixel = valueToIndex[ac][(int) (pixel % 256)];
            if (lut16 != null) pixel = valueToIndex[ac][(int) (pixel % 65536)];
          }

          // scale pixel value by the scale factor
          // if floating point, convert value to raw IEEE floating point bits
          switch (pixelType) {
            case FormatTools.FLOAT:
              float floatPixel;
              if (specialPixel) floatPixel = pixel;
              else floatPixel = (float) (scaleFactor * pixel);
              pixel = Float.floatToIntBits(floatPixel);
              break;
            case FormatTools.DOUBLE:
              double doublePixel;
              if (specialPixel) doublePixel = pixel;
              else doublePixel = scaleFactor * pixel;
              pixel = Double.doubleToLongBits(doublePixel);
              break;
            default:
              if (!specialPixel) pixel = (long) (scaleFactor * pixel);
          }

          // unpack pixel into byte buffer
          int index;
          if (interleaved) index = w * rgb * row + rgb * col + cOffset; // CXY
          else index = h * w * cOffset + w * row + col; // XYC
          index *= bpp;
          DataTools.unpackBytes(pixel, buf, index, bpp, little);
        }
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    if (new Location(id).isDirectory() && checkSuffix(id, "fake")) {
      fakeSeries.clear();
      return listFakeSeries(id).size() <= 1;
    }
    if (checkSuffix(id, "fake" + ".ini")) {
      return ! new Location(id).exists();
    }
    return ! new Location(id + ".ini").exists();
  }

  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "fake.ini"))
    {
      return true;
    }
    fakeSeries.clear();
    if (name.endsWith(".fake") && listFakeSeries(name).size() > 0) {
      return true;
    }
    return super.isThisType(name, open);
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
      FormatTools.assertId(currentId, true, 1);
      List<String> files = new ArrayList<String>();
      fakeSeries.clear();
      if (!noPixels) files.addAll(listFakeSeries(currentId));
      if (iniFile != null) files.add(iniFile);
      return files.toArray(new String[files.size()]);
  }

  private void findLogFiles() {
    iniFile = null;
    Location loc = new Location(getCurrentFile() + ".ini");
    if (loc.exists()) {
      iniFile = loc.getAbsolutePath();
    }
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    iniFile = null;
    sizeX = DEFAULT_SIZE_X;
    sizeY = DEFAULT_SIZE_Y;
    sizeZ = DEFAULT_SIZE_Z;
    sizeC = DEFAULT_SIZE_C;
    sizeT = DEFAULT_SIZE_T;
    exposureTime = null;
    physicalSizeX = null;
    physicalSizeY = null;
    physicalSizeZ = null;
    annBool = 0;
    annComment = 0;
    annDouble = 0;
    annLong = 0;
    annMap = 0;
    annTime = 0;
    annTag = 0;
    annTerm = 0;
    annXml = 0;
    annotationCount = 0;
    annotationBoolCount = 0;
    annotationCommentCount = 0;
    annotationDoubleCount = 0;
    annotationLongCount = 0;
    annotationMapCount = 0;
    annotationTagCount = 0;
    annotationTermCount = 0;
    annotationTimeCount = 0;
    annotationXmlCount = 0;
    ellipses = 0;
    labels = 0;
    lines = 0;
    masks = 0;
    points = 0;
    polygons = 0;
    polylines = 0;
    rectangles = 0;
    roiCount = 0;
    scaleFactor = 1;
    lut8 = null;
    lut16 = null;
    screens = 0;
    plates = 0;
    plateRows = 0;
    plateCols = 0;
    fields = 0;
    plateAcqs = 0;
    super.close(fileOnly);
  }

  public OMEXMLMetadata getOmeXmlMetadata() {
    if (omeXmlMetadata == null) {
      try {
        omeXmlMetadata = getOmeXmlService().createOMEXMLMetadata();
      } catch (ServiceException exc) {
        LOGGER.error("Could not create OME-XML metadata", exc);
      }
    }
    return omeXmlMetadata;
  }

  public OMEXMLService getOmeXmlService() {
    if (omeXmlService == null) {
      try {
        omeXmlService = new ServiceFactory().getInstance(OMEXMLService.class);
      } catch (DependencyException exc) {
        LOGGER.error("Could not create OME-XML service", exc);
      }
    }
    return omeXmlService;
  }

  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "fake")) {
      if (checkSuffix(id, "fake.ini")) {
        id = id.substring(0, id.lastIndexOf("."));
      }
      Location file = new Location(id).getAbsoluteFile();
      if (!file.exists()) {
        Location dir = file.getParentFile();
        String[] list = dir.list(true);
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf("."));
        for (String f : list) {
          if (checkSuffix(f, "fake") && f.startsWith(name)) {
            id = new Location(dir, f).getAbsolutePath();
            break;
          }
        }
      }
    }

    // Logic copied from deltavision. This should probably be refactored into
    // a helper method "replaceBySuffix" or something.
    super.initFile(id);
    findLogFiles();

    String path = id;
    Location location = new Location(id);
    String[] tokens = null;
    if (location.exists()) {
      path = location.getAbsoluteFile().getName();

      if (path.startsWith("Field")) {
        Location root = location.getAbsoluteFile().getParentFile();
        if (root != null) {
          root = root.getParentFile();
          if (root != null) {
            root = root.getParentFile();
            if (root != null) {
              root = root.getParentFile();
              if (isSPWStructure(root.getAbsolutePath())) {
               tokens = extractTokensFromFakeSeries(root.getAbsolutePath());
               // makes sure that getSeriesUsedFiles returns correctly
               currentId = root.getAbsolutePath();
              }
            }
          }
        }
      }
    }
    if (location.isDirectory() && isSPWStructure(location.getAbsolutePath())) {
      tokens = extractTokensFromFakeSeries(location.getAbsolutePath());
    } else if (tokens == null) {
      String noExt = path.substring(0, path.lastIndexOf("."));
      tokens = noExt.split(TOKEN_SEPARATOR);
    }

    String name = null;

    int thumbSizeX = 0; // default
    int thumbSizeY = 0; // default
    int pixelType = DEFAULT_PIXEL_TYPE;
    int bitsPerPixel = 0; // default
    int rgb = DEFAULT_RGB_CHANNEL_COUNT;
    String dimOrder = DEFAULT_DIMENSION_ORDER;
    boolean orderCertain = true;
    boolean little = true;
    boolean interleaved = false;
    boolean indexed = false;
    boolean falseColor = false;
    boolean metadataComplete = true;
    boolean thumbnail = false;
    boolean withMicrobeam = false;

    int seriesCount = 1;
    int resolutionCount = 1;
    int resolutionScale = DEFAULT_RESOLUTION_SCALE;
    int lutLength = 3;

    String acquisitionDate = null;

    Integer defaultColor = null;
    ArrayList<Integer> color = new ArrayList<Integer>();

    ArrayList<IniTable> seriesTables = new ArrayList<IniTable>();

    // add properties file values to list of tokens.
    if (iniFile != null) {
      IniParser parser = new IniParser();
      IniList list = parser.parseINI(new File(iniFile));

      List<String> newTokens = new ArrayList<String>();
      // Unclear what to do with other headers...
      IniTable table = list.getTable(IniTable.DEFAULT_HEADER);
      if (table != null) {
        for (Map.Entry<String, String> entry : table.entrySet()) {
          newTokens.add(entry.getKey() + "=" + entry.getValue());
        }
      }

      table = list.getTable("GlobalMetadata");
      if (table != null) {
        for (Map.Entry<String, String> entry : table.entrySet()) {
          addGlobalMeta(entry.getKey(), entry.getValue());
        }
      }

      String[] newTokArr = newTokens.toArray(new String[0]);
      String[] oldTokArr = tokens;
      tokens = new String[newTokArr.length + oldTokArr.length];
      System.arraycopy(oldTokArr, 0, tokens, 0, oldTokArr.length);
      System.arraycopy(newTokArr, 0, tokens, oldTokArr.length, newTokArr.length);
      // Properties overrides file name values

      int seriesIndex = 0;
      while (list.getTable("series_" + seriesIndex) != null) {
        seriesTables.add(list.getTable("series_" + seriesIndex));
        seriesIndex++;
      }
    }

    // parse tokens from filename
    for (String token : tokens) {
      if (name == null) {
        // first token is the image name
        name = token;
        continue;
      }
      int equals = token.indexOf('=');
      if (equals < 0) {
        LOGGER.warn("ignoring token: {}", token);
        continue;
      }
      String key = token.substring(0, equals);
      String value = token.substring(equals + 1);

      boolean boolValue = value.equals("true");
      double doubleValue;
      try {
        doubleValue = Double.parseDouble(value);
      }
      catch (NumberFormatException exc) {
        doubleValue = Double.NaN;
      }
      int intValue = Double.isNaN(doubleValue) ? -1 : (int) doubleValue;

      if (key.equals("sizeX")) sizeX = intValue;
      else if (key.equals("sizeY")) sizeY = intValue;
      else if (key.equals("sizeZ")) sizeZ = intValue;
      else if (key.equals("sizeC")) sizeC = intValue;
      else if (key.equals("sizeT")) sizeT = intValue;
      else if (key.equals("thumbSizeX")) thumbSizeX = intValue;
      else if (key.equals("thumbSizeY")) thumbSizeY = intValue;
      else if (key.equals("pixelType")) {
        pixelType = FormatTools.pixelTypeFromString(value);
      }
      else if (key.equals("bitsPerPixel")) bitsPerPixel = intValue;
      else if (key.equals("rgb")) rgb = intValue;
      else if (key.equals("dimOrder")) dimOrder = value.toUpperCase();
      else if (key.equals("orderCertain")) orderCertain = boolValue;
      else if (key.equals("little")) little = boolValue;
      else if (key.equals("interleaved")) interleaved = boolValue;
      else if (key.equals("indexed")) indexed = boolValue;
      else if (key.equals("falseColor")) falseColor = boolValue;
      else if (key.equals("metadataComplete")) metadataComplete = boolValue;
      else if (key.equals("thumbnail")) thumbnail = boolValue;
      else if (key.equals("series")) seriesCount = intValue;
      else if (key.equals("resolutions")) resolutionCount = intValue;
      else if (key.equals("resolutionScale")) resolutionScale = intValue;
      else if (key.equals("lutLength")) lutLength = intValue;
      else if (key.equals("scaleFactor")) scaleFactor = doubleValue;
      else if (key.equals("exposureTime")) exposureTime = new Time((float) doubleValue, UNITS.SECOND);
      else if (key.equals("acquisitionDate")) acquisitionDate = value;
      else if (key.equals("screens")) screens = intValue;
      else if (key.equals("plates")) plates = intValue;
      else if (key.equals("plateRows")) plateRows = intValue;
      else if (key.equals("plateCols")) plateCols = intValue;
      else if (key.equals("fields")) fields = intValue;
      else if (key.equals("plateAcqs")) plateAcqs = intValue;
      else if (key.equals("withMicrobeam")) withMicrobeam = boolValue;
      else if (key.equals("annLong")) annLong = intValue;
      else if (key.equals("annDouble")) annDouble = intValue;
      else if (key.equals("annMap")) annMap = intValue;
      else if (key.equals("annComment")) annComment = intValue;
      else if (key.equals("annBool")) annBool = intValue;
      else if (key.equals("annTime")) annTime = intValue;
      else if (key.equals("annTag")) annTag = intValue;
      else if (key.equals("annTerm")) annTerm = intValue;
      else if (key.equals("annXml")) annXml = intValue;
      else if (key.equals("ellipses")) ellipses = intValue;
      else if (key.equals("labels")) labels = intValue;
      else if (key.equals("lines")) lines = intValue;
      else if (key.equals("masks")) masks = intValue;
      else if (key.equals("points")) points = intValue;
      else if (key.equals("polygons")) polygons = intValue;
      else if (key.equals("polylines")) polylines = intValue;
      else if (key.equals("rectangles")) rectangles = intValue;
      else if (key.equals("physicalSizeX")) physicalSizeX = FormatTools.parseLength(value, getPhysicalSizeXUnitXsdDefault());
      else if (key.equals("physicalSizeY")) physicalSizeY = FormatTools.parseLength(value, getPhysicalSizeYUnitXsdDefault());
      else if (key.equals("physicalSizeZ")) physicalSizeZ = FormatTools.parseLength(value, getPhysicalSizeZUnitXsdDefault());
      else if (key.equals("color")) {
        defaultColor = parseColor(value);
      }
      else if (key.startsWith("color_")) {
        // 'color' and 'color_x' can be used together, but 'color_x' takes
        // precedence.  'color' will in that case be used for any missing
        // or invalid 'color_x' values.
        int index = Integer.parseInt(key.substring(key.indexOf('_') + 1));

        while (index >= color.size()) {
          color.add(null);
        }
        color.set(index, parseColor(value));
      }
    }

    // do some sanity checks
    if (sizeX < 1) throw new FormatException("Invalid sizeX: " + sizeX);
    if (sizeY < 1) throw new FormatException("Invalid sizeY: " + sizeY);
    if (sizeZ < 1) throw new FormatException("Invalid sizeZ: " + sizeZ);
    if (sizeC < 1) throw new FormatException("Invalid sizeC: " + sizeC);
    if (sizeT < 1) throw new FormatException("Invalid sizeT: " + sizeT);
    if (thumbSizeX < 0) {
      throw new FormatException("Invalid thumbSizeX: " + thumbSizeX);
    }
    if (thumbSizeY < 0) {
      throw new FormatException("Invalid thumbSizeY: " + thumbSizeY);
    }
    if (rgb < 1 || rgb > sizeC || sizeC % rgb != 0) {
      throw new FormatException("Invalid sizeC/rgb combination: " +
        sizeC + "/" + rgb);
    }
    MetadataTools.getDimensionOrder(dimOrder);
    if (falseColor && !indexed) {
      throw new FormatException("False color images must be indexed");
    }
    if (seriesCount < 1) {
      throw new FormatException("Invalid seriesCount: " + seriesCount);
    }
    if (lutLength < 1) {
      throw new FormatException("Invalid lutLength: " + lutLength);
    }
    if (resolutionCount < 1) {
      throw new FormatException("Invalid resolutionCount: " + resolutionCount);
    }
    if (resolutionScale <= 1) {
      throw new FormatException("Invalid resolutionScale: " + resolutionScale);
    }

    // populate SPW metadata
    MetadataStore store = makeFilterMetadata();
    boolean hasSPW = screens > 0 || plates > 0 || plateRows > 0 ||
      plateCols > 0 || fields > 0 || plateAcqs > 0;
    if (hasSPW) {
      if (screens<0) screens = 0;
      if (plates<=0) plates = 1;
      if (plateRows<=0) plateRows = 1;
      if (plateCols<=0) plateCols = 1;
      if (fields<=0) fields = 1;
      if (plateAcqs<=0) plateAcqs = 1;
      // generate SPW metadata and override series count to match
      int imageCount =
        populateSPW(store, screens, plates, plateRows, plateCols, fields, plateAcqs, withMicrobeam);
      if (imageCount > 0) seriesCount = imageCount;
      else hasSPW = false; // failed to generate SPW metadata
    }

    // populate core metadata
    int effSizeC = sizeC / rgb;
    core.clear();
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      ms.resolutionCount = resolutionCount;
      core.add(ms);
      ms.sizeX = sizeX;
      ms.sizeY = sizeY;
      ms.sizeZ = sizeZ;
      ms.sizeC = sizeC;
      ms.sizeT = sizeT;
      ms.thumbSizeX = thumbSizeX;
      ms.thumbSizeY = thumbSizeY;
      ms.pixelType = pixelType;
      ms.bitsPerPixel = bitsPerPixel;
      ms.imageCount = sizeZ * effSizeC * sizeT;
      ms.rgb = rgb > 1;
      ms.dimensionOrder = dimOrder;
      ms.orderCertain = orderCertain;
      ms.littleEndian = little;
      ms.interleaved = interleaved;
      ms.indexed = indexed;
      ms.falseColor = falseColor;
      ms.metadataComplete = metadataComplete;
      ms.thumbnail = thumbnail;

      for (int r=1; r<resolutionCount; r++) {
        CoreMetadata subres = new CoreMetadata(ms);
        int scale = (int) Math.pow(resolutionScale, r);
        subres.sizeX /= scale;
        subres.sizeY /= scale;
        core.add(subres);
      }
    }

    // populate OME metadata
    boolean planeInfo = (exposureTime != null) || seriesTables.size() > 0;

    MetadataTools.populatePixels(store, this, planeInfo);
    fillExposureTime(store);
    fillPhysicalSizes(store);
    for (int currentImageIndex=0; currentImageIndex<seriesCount; currentImageIndex++) {
      if (currentImageIndex < seriesTables.size()) {
        parseSeriesTable(seriesTables.get(currentImageIndex), store, currentImageIndex);
      }

      String imageName = currentImageIndex > 0 ? name + " " + (currentImageIndex + 1) : name;
      store.setImageName(imageName, currentImageIndex);
      fillAcquisitionDate(store, acquisitionDate, currentImageIndex);

      for (int c=0; c<getEffectiveSizeC(); c++) {
        Color channel = defaultColor == null ? null: new Color(defaultColor);
        if (c < color.size() && color.get(c) != null) {
          channel = new Color(color.get(c));
        }
        if (channel != null) {
          store.setChannelColor(channel, currentImageIndex, c);
        }
      }
      fillAnnotations(store, currentImageIndex);
      fillRegions(store, currentImageIndex);
    }

    // for indexed color images, create lookup tables
    if (indexed) {
      if (pixelType == FormatTools.UINT8) {
        // create 8-bit LUTs
        final int num = 256;
        createIndexMap(num);
        lut8 = new byte[sizeC][lutLength][num];
        // linear ramp
        for (int c=0; c<sizeC; c++) {
          for (int i=0; i<lutLength; i++) {
            for (int index=0; index<num; index++) {
              lut8[c][i][index] = (byte) indexToValue[c][index];
            }
          }
        }
      }
      else if (pixelType == FormatTools.UINT16) {
        // create 16-bit LUTs
        final int num = 65536;
        createIndexMap(num);
        lut16 = new short[sizeC][lutLength][num];
        // linear ramp
        for (int c=0; c<sizeC; c++) {
          for (int i=0; i<lutLength; i++) {
            for (int index=0; index<num; index++) {
              lut16[c][i][index] = (short) indexToValue[c][index];
            }
          }
        }
      }
      // NB: Other pixel types will have null LUTs.
    }
  }

  @Override
  public void reopenFile() throws IOException {
  }

  private void fillPhysicalSizes(MetadataStore store) {
    if (physicalSizeX == null && physicalSizeY == null && physicalSizeZ == null) return;
    for (int s=0; s<getSeriesCount(); s++) {
      store.setPixelsPhysicalSizeX(physicalSizeX, s);
      store.setPixelsPhysicalSizeY(physicalSizeY, s);
      store.setPixelsPhysicalSizeZ(physicalSizeZ, s);
    }
  }

  private void fillExposureTime(MetadataStore store) {
    if (exposureTime == null) return;
    int oldSeries = getSeries();
    for (int s=0; s<getSeriesCount(); s++) {
      setSeries(s);
      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneExposureTime(exposureTime, s, i);
      }
    }
    setSeries(oldSeries);
  }

  private void fillAcquisitionDate(MetadataStore store, String date, int imageIndex) {
    if (date == null) return;
    if(DateTools.getTime(date, DateTools.FILENAME_FORMAT) != -1) {
      Timestamp stamp = new Timestamp(
        DateTools.formatDate(date, DateTools.FILENAME_FORMAT));
      store.setImageAcquisitionDate(stamp, imageIndex);
    }
  }

  private void fillAnnotations(MetadataStore store, int imageIndex) {

    int annotationRefCount = 0;
    String annotationID;

    for (int i=0; i<annBool; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setBooleanAnnotationID(annotationID, annotationBoolCount);
      store.setBooleanAnnotationNamespace(ANNOTATION_NAMESPACE, annotationBoolCount);
      store.setBooleanAnnotationValue(ANN_BOOLEAN_VALUE, annotationBoolCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationBoolCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annComment; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setCommentAnnotationID(annotationID, annotationCommentCount);
      store.setCommentAnnotationNamespace(ANNOTATION_NAMESPACE, annotationCommentCount);
      store.setCommentAnnotationValue(ANN_COMMENT_VALUE + (annotationCount+1), annotationCommentCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationCommentCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annDouble; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setDoubleAnnotationID(annotationID, annotationDoubleCount);
      store.setDoubleAnnotationNamespace(ANNOTATION_NAMESPACE, annotationDoubleCount);
      store.setDoubleAnnotationValue(ANN_DOUBLE_VALUE*(annotationCount+1), annotationDoubleCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationDoubleCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annLong; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setLongAnnotationID(annotationID, annotationLongCount);
      store.setLongAnnotationNamespace(ANNOTATION_NAMESPACE, annotationLongCount);
      store.setLongAnnotationValue(ANN_LONG_VALUE+annotationCount, annotationLongCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationLongCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annMap; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setMapAnnotationID(annotationID, annotationMapCount);
      store.setMapAnnotationNamespace(ANNOTATION_NAMESPACE, annotationMapCount);
      List<MapPair> mapValue = new ArrayList<MapPair>();
      for (int keyNum=0; keyNum<10; keyNum++) {
        mapValue.add(new MapPair("keyS" + imageIndex + "N" + keyNum, "val" + (keyNum+1)*(annotationCount+1)));
      }
      store.setMapAnnotationValue(mapValue, annotationMapCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationMapCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annTag; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setTagAnnotationID(annotationID, annotationTagCount);
      store.setTagAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTagCount);
      store.setTagAnnotationValue(ANN_TAG_VALUE + (annotationCount+1), annotationTagCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationTagCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annTerm; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setTermAnnotationID(annotationID, annotationTermCount);
      store.setTermAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTermCount);
      store.setTermAnnotationValue(ANN_TERM_VALUE + (annotationCount+1), annotationTermCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationTermCount++;
      annotationCount++;
      annotationRefCount++;
    }

    for (int i=0; i<annTime; i++) {
       annotationID = ANNOTATION_PREFIX + annotationCount;
       store.setTimestampAnnotationID(annotationID, annotationTimeCount);
       store.setTimestampAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTimeCount);
       store.setTimestampAnnotationValue(ANN_TIME_VALUE, annotationTimeCount);
       store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
       annotationTimeCount++;
       annotationCount++;
       annotationRefCount++;
     }

    for (int i=0; i<annXml; i++) {
      annotationID = ANNOTATION_PREFIX + annotationCount;
      store.setXMLAnnotationID(annotationID, annotationXmlCount);
      store.setXMLAnnotationNamespace(ANNOTATION_NAMESPACE, annotationXmlCount);
      store.setXMLAnnotationValue(ANN_XML_VALUE_START + (annotationCount+1) + ANN_XML_VALUE_END, annotationXmlCount);
      store.setImageAnnotationRef(annotationID, imageIndex, annotationRefCount);
      annotationXmlCount++;
      annotationCount++;
      annotationRefCount++;
    }
  }

  private Double getX(int i) {
      return new Double(ROI_SPACING * i % sizeX);
  }

  private Double getY(int i) {
      return new Double(ROI_SPACING * ((int) ROI_SPACING * i / sizeX) % sizeY);
  }

  private String getPoints(int i) {
      Double x0 = getX(i) + ROI_SPACING / 2;
      Double y0 = getY(i) + ROI_SPACING / 2;
      double [] dx = { -0.8, -.3, .4, .5, -.1};
      double [] dy = { -0.4, .6, .5, -.3, -.7};
      final StringBuilder p = new StringBuilder();
      for (int j=0; j<5; j++) {
        p.append(x0 + ROI_SPACING /2 * dx[j]);
        p.append(",");
        p.append(y0 + ROI_SPACING /2 * dy[j]);
        if (j < dx.length - 1) p.append(" ");
      }
      return p.toString();
  }

  private void fillRegions(MetadataStore store, int imageIndex) {
    int roiRefCount = 0;
    String roiID;
    Random random = new Random();
    for (int i=0; i<ellipses; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setEllipseID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setEllipseX(getX(i) + ROI_SPACING / 2, roiCount, 0);
        store.setEllipseY(getY(i) + ROI_SPACING / 2, roiCount, 0);
        store.setEllipseRadiusX(new Double(ROI_SPACING / 2), roiCount, 0);
        store.setEllipseRadiusY(new Double(ROI_SPACING / 2), roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<labels; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setLabelID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setLabelX(getX(i), roiCount, 0);
        store.setLabelY(getY(i), roiCount, 0);
        store.setLabelText("Label " + i, roiCount, 0 );
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<lines; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setLineID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setLineX1(getX(i) + ROI_SPACING / 4, roiCount, 0);
        store.setLineY1(getY(i) + ROI_SPACING / 4, roiCount, 0);
        store.setLineX2(getX(i) + ROI_SPACING / 2, roiCount, 0);
        store.setLineY2(getY(i) + ROI_SPACING / 2, roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<masks; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setMaskID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setMaskX((double)ROI_SPACING, roiCount, 0);
        store.setMaskY((double)ROI_SPACING, roiCount, 0);
        store.setMaskWidth((double)ROI_SPACING, roiCount, 0);
        store.setMaskHeight((double)ROI_SPACING, roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        byte[] rawBytes = new byte[ROI_SPACING*ROI_SPACING];
        random.nextBytes(rawBytes);
        store.setMaskBinData(rawBytes, roiCount, 0);
        store.setMaskBinDataBigEndian(true, roiCount, 0);
        store.setMaskBinDataLength(new NonNegativeLong((long)ROI_SPACING*ROI_SPACING), roiCount, 0);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<points; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setPointID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setPointX(getX(i) + ROI_SPACING / 2, roiCount, 0);
        store.setPointY(getY(i) + ROI_SPACING / 2, roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<polygons; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setPolygonID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setPolygonPoints(getPoints(i), roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<polylines; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setPolylineID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setPolylinePoints(getPoints(i), roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }

    for (int i=0; i<rectangles; i++) {
        roiID = ROI_PREFIX + roiCount;
        store.setROIID(roiID, roiCount);
        store.setRectangleID(SHAPE_PREFIX + roiCount, roiCount, 0);
        store.setRectangleX(getX(i) + ROI_SPACING / 4, roiCount, 0);
        store.setRectangleY(getY(i) + ROI_SPACING / 4, roiCount, 0);
        store.setRectangleWidth(new Double(ROI_SPACING / 2), roiCount, 0);
        store.setRectangleHeight(new Double(ROI_SPACING / 2), roiCount, 0);
        store.setImageROIRef(roiID, imageIndex, roiRefCount);
        roiCount++;
        roiRefCount++;
    }
  }

  /**
   * Translate key/value pairs from the INI table for the specified series.
   */
  private void parseSeriesTable(IniTable table, MetadataStore store, int newSeries) {
    int s = getSeries();
    setSeries(newSeries);

    for (int i=0; i<getImageCount(); i++) {
      String exposureTime = table.get("ExposureTime_" + i);
      String exposureTimeUnit = table.get("ExposureTimeUnit_" + i);

      if (exposureTime != null) {
        try {
          Double v = Double.valueOf(exposureTime);
          Time exposure = FormatTools.getTime(v, exposureTimeUnit);
          if (exposure != null) {
            store.setPlaneExposureTime(exposure, newSeries, i);
          }
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Could not parse ExposureTime for series #" + s + " plane #" + i, e);
        }
      }

      int[] spwCoordinate = toSPWCoordinates(newSeries);

      // TODO: could be cleaned up further when Java 8 is the minimum version
      Length x = parsePosition("X", s, i, table);
      if (x != null) {
        store.setPlanePositionX(x, newSeries, i);
        if (spwCoordinate != null) {
          store.setWellSamplePositionX(x,
            spwCoordinate[2], spwCoordinate[1], spwCoordinate[0]);
        }
      }

      Length y = parsePosition("Y", s, i, table);
      if (y != null) {
        store.setPlanePositionY(y, newSeries, i);
        if (spwCoordinate != null) {
          store.setWellSamplePositionY(y,
            spwCoordinate[2], spwCoordinate[1], spwCoordinate[0]);
        }
      }

      Length z = parsePosition("Z", s, i, table);
      if (z != null) {
        store.setPlanePositionZ(z, newSeries, i);
      }
    }

    setSeries(s);
  }

// -- Helper methods --

  /**
   * Convert the given series (Image) index to a
   * WellSample, Well, and Plate index.
   * This should match the ordering used by XMLMockObjects.
   *
   * @param seriesIndex the index of the series/Image
   * @return an array of length 3 containing the
   *  WellSample, Well, and Plate indices (in order),
   *  or null if SPW metadata was not defined
   */
  private int[] toSPWCoordinates(int seriesIndex) {
    if (plates < 1) {
      return null;
    }
    int screenCount = (int) Math.max(screens, 1);
    return FormatTools.rasterToPosition(
      new int[] {plateAcqs * fields, plateRows * plateCols,
        screenCount * plates}, seriesIndex);
  }

  private String[] extractTokensFromFakeSeries(String path) {
    List<String> tokens = new ArrayList<String>();
    int plates = 0, plateAcqs = 0, rows = 0, cols = 0, fields = 0;
    String currentPlate = "";
    String regExFileSeparator = File.separatorChar == '\\' ?
        "\\\\" : File.separator;
    // This is a sub-optimal approach, based on the assumption
    // that the last fakeSeries[] element has the fakeImage with biggest indices
    // in its name.
    for (String fakeImage : fakeSeries) {
      for (String pathToken : fakeImage.split(regExFileSeparator)) {
        if (pathToken.startsWith(ResourceNamer.PLATE)) {
          if (!pathToken.equals(currentPlate)) {
            currentPlate = pathToken;
            plates++;
          }
        }
      }
    }

    for (String pathToken : fakeSeries.get(fakeSeries.size() - 1)
        .split(regExFileSeparator)) {
      if (pathToken.startsWith(ResourceNamer.RUN)) {
        plateAcqs = Integer.parseInt(pathToken.substring(pathToken.lastIndexOf(
            ResourceNamer.RUN) + ResourceNamer.RUN.length(),
            pathToken.length())) + 1;
      } else if (pathToken.startsWith(ResourceNamer.WELL)) {
        String wellId = pathToken.substring(pathToken.lastIndexOf(
            ResourceNamer.WELL) + ResourceNamer.WELL.length(),
            pathToken.length());
        String[] elements = wellId.split("(?<=\\p{L})(?=\\d)");
        rows = ResourceNamer.alphabeticIndexCount(elements[0]);
        cols = Integer.parseInt(elements[1]) + 1;
      } else if (pathToken.startsWith(ResourceNamer.FIELD)) {
        String fieldName = pathToken.substring(0, pathToken.lastIndexOf("."));
        fields = Integer.parseInt(fieldName.substring(fieldName.lastIndexOf(
            ResourceNamer.FIELD) + ResourceNamer.FIELD.length(),
            fieldName.length())) + 1;
      }
    }

    tokens.add(path);
    tokens.add("plates="+plates);
    tokens.add("plateRows="+rows);
    tokens.add("plateCols="+cols);
    tokens.add("fields="+fields);
    tokens.add("plateAcqs="+plateAcqs);

    return tokens.toArray(new String[tokens.size()]);
  }

  private boolean isSPWStructure(String path) {
    fakeSeries.clear();
    return !listFakeSeries(path).get(0).equals(path);
  }

  private int populateSPW(MetadataStore store, int screens, int plates, int rows, int cols, int fields, int acqs, boolean withMicrobeam)
  {
    final XMLMockObjects xml = new XMLMockObjects();
    OME ome = null;
    if (screens==0) {
      ome = xml.createPopulatedPlate(plates, rows, cols, fields, acqs, withMicrobeam);
    } else {
      ome = xml.createPopulatedScreen(screens, plates, rows, cols, fields, acqs, withMicrobeam);
    }
    if (withMicrobeam) roiCount = roiCount + plates;;
    getOmeXmlMetadata().setRoot(new OMEXMLMetadataRoot(ome));
    // copy populated SPW metadata into destination MetadataStore
    getOmeXmlService().convertMetadata(omeXmlMetadata, store);
    domains = new String[] {FormatTools.HCS_DOMAIN};
    return ome.sizeOfImageList();
  }

  /** Creates a mapping between indices and color values. */
  private void createIndexMap(int num) {
    int sizeC = core.get(0).sizeC;

    // create random mapping from indices to values
    indexToValue = new int[sizeC][num];
    for (int c=0; c<sizeC; c++) {
      for (int index=0; index<num; index++) indexToValue[c][index] = index;
      shuffle(c, indexToValue[c]);
    }

    // create inverse mapping: values to indices
    valueToIndex = new int[sizeC][num];
    for (int c=0; c<sizeC; c++) {
      for (int index=0; index<num; index++) {
        int value = indexToValue[c][index];
        valueToIndex[c][value] = index;
      }
    }
  }

  /** Traverses a fake file folder structure indicated by traversedDirectory */
  private List<String> listFakeSeries(String traversedDirectory) {
    File parent = new File(traversedDirectory);
    if (parent.isDirectory()) {
      File[] children = parent.listFiles();
      Arrays.sort(children);
      if (children != null) {
        for (File child : children) {
          listFakeSeries(child.getAbsolutePath());
        }
      }
    } else {
      String path = parent.getAbsolutePath();
      // explicitly check suffixes, otherwise any other files that were put
      // in the directory will be picked up (e.g. .DS_Store)
      if (checkSuffix(path, "fake") || checkSuffix(path, "fake.ini")) {
        fakeSeries.add(path);
      }
    }
    return fakeSeries;
  }

  /** Fisher-Yates shuffle with constant seeds to ensure reproducibility. */
  private static void shuffle(int c, int[] array) {
    Random r = new Random(SEED + c);
    for (int i = array.length; i > 1; i--) {
      int j = r.nextInt(i);
      int tmp = array[j];
      array[j] = array[i - 1];
      array[i - 1] = tmp;
    }
  }

  private int parseColor(String value) {
    // parse colors as longs so that unsigned values can be specified,
    // e.g. 0xff0000ff for red with opaque alpha
    int base = 10;
    if (value.startsWith("0x") || value.startsWith("0X")) {
      value = value.substring(2);
      base = 16;
    }
    try {
      return (int) Long.parseLong(value, base);
    }
    catch (NumberFormatException e) { }
    return 0;
  }

  private Length parsePosition(String axis, int s, int index, IniTable table) {
    String position = table.get("Position" + axis + "_" + index);
    String positionUnit = table.get("Position" + axis + "Unit_" + index);

    if (position != null) {
      try {
        Double v = Double.valueOf(position);
        Length size = new Length(v, UNITS.MICROMETER);
        if (positionUnit != null) {
          try {
            UnitsLength ul = UnitsLength.fromString(positionUnit);
            size = UnitsLength.create(v, ul);
          }
          catch (EnumerationException e) {
            LOGGER.trace("Could not parse Position" + axis + "Unit for series #" + s + " plane #" + index, e);
          }
        }
        return size;
      }
      catch (NumberFormatException e) {
        LOGGER.trace("Could not parse Position" + axis + " for series #" + s + " plane #" + index, e);
      }
    }

    return null;
  }

}
