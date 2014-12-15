/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  public static final int BOX_SIZE = 10;

  public static final int DEFAULT_SIZE_X = 512;
  public static final int DEFAULT_SIZE_Y = 512;
  public static final int DEFAULT_SIZE_Z = 1;
  public static final int DEFAULT_SIZE_C = 1;
  public static final int DEFAULT_SIZE_T = 1;
  public static final int DEFAULT_PIXEL_TYPE = FormatTools.UINT8;
  public static final int DEFAULT_RGB_CHANNEL_COUNT = 1;
  public static final String DEFAULT_DIMENSION_ORDER = "XYZCT";

  private static final String TOKEN_SEPARATOR = "&";
  private static final long SEED = 0xcafebabe;

  // -- Fields --

  /** exposure time per plane info */
  private Time exposureTime = null;

  /* physical sizes */
  private Length physicalSizeX, physicalSizeY, physicalSizeZ;

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
    int sizeX = DEFAULT_SIZE_X;
    int sizeY = DEFAULT_SIZE_Y;
    int sizeZ = DEFAULT_SIZE_Z;
    int sizeC = DEFAULT_SIZE_C;
    int sizeT = DEFAULT_SIZE_T;
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

    int seriesCount = 1;
    int lutLength = 3;

    String acquisitionDate = null;

    int plates = 0;
    int plateRows = 0;
    int plateCols = 0;
    int fields = 0;
    int plateAcqs = 0;

/*
 *  Other annotation types that could be added
 *  int annFile = 0; // FileAnnotation
 *  int annList = 0; // ListAnnotation
 */
    int annLong = 0;
    int annDouble = 0;
    int annComment = 0;
    int annBool = 0;
    int annTime = 0;
    int annTag = 0;
    int annTerm = 0;
    int annXml = 0;
    int annMap = 0;

    Integer defaultColor = null;
    ArrayList<Integer> color = new ArrayList<Integer>();

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

      String[] newTokArr = newTokens.toArray(new String[0]);
      String[] oldTokArr = tokens;
      tokens = new String[newTokArr.length + oldTokArr.length];
      System.arraycopy(oldTokArr, 0, tokens, 0, oldTokArr.length);
      System.arraycopy(newTokArr, 0, tokens, oldTokArr.length, newTokArr.length);
      // Properties overrides file name values
    }

    // parse tokens from filename
    for (String token : tokens) {
      if (name == null) {
        // first token is the image name
        name = token;
        continue;
      }
      int equals = token.indexOf("=");
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
      else if (key.equals("lutLength")) lutLength = intValue;
      else if (key.equals("scaleFactor")) scaleFactor = doubleValue;
      else if (key.equals("exposureTime")) exposureTime = new Time((float) doubleValue, UNITS.S);
      else if (key.equals("acquisitionDate")) acquisitionDate = value;
      else if (key.equals("plates")) plates = intValue;
      else if (key.equals("plateRows")) plateRows = intValue;
      else if (key.equals("plateCols")) plateCols = intValue;
      else if (key.equals("fields")) fields = intValue;
      else if (key.equals("plateAcqs")) plateAcqs = intValue;
      else if (key.equals("annLong")) annLong = intValue;
      else if (key.equals("annDouble")) annDouble = intValue;
      else if (key.equals("annMap")) annMap = intValue;
      else if (key.equals("annComment")) annComment = intValue;
      else if (key.equals("annBool")) annBool = intValue;
      else if (key.equals("annTime")) annTime = intValue;
      else if (key.equals("annTag")) annTag = intValue;
      else if (key.equals("annTerm")) annTerm = intValue;
      else if (key.equals("annXml")) annXml = intValue;
      else if (key.equals("physicalSizeX")) physicalSizeX = parseLength(value, getPhysicalSizeXUnitXsdDefault());
      else if (key.equals("physicalSizeY")) physicalSizeY = parseLength(value, getPhysicalSizeYUnitXsdDefault());
      else if (key.equals("physicalSizeZ")) physicalSizeZ = parseLength(value, getPhysicalSizeZUnitXsdDefault());
      else if (key.equals("color")) {
        defaultColor = parseColor(value);
      }
      else if (key.startsWith("color_")) {
        // 'color' and 'color_x' can be used together, but 'color_x' takes
        // precedence.  'color' will in that case be used for any missing
        // or invalid 'color_x' values.
        int index = Integer.parseInt(key.substring(key.indexOf("_") + 1));

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
    getDimensionOrder(dimOrder);
    if (falseColor && !indexed) {
      throw new FormatException("False color images must be indexed");
    }
    if (seriesCount < 1) {
      throw new FormatException("Invalid seriesCount: " + seriesCount);
    }
    if (lutLength < 1) {
      throw new FormatException("Invalid lutLength: " + lutLength);
    }

    // populate SPW metadata
    MetadataStore store = makeFilterMetadata();
    boolean hasSPW = plates > 0 && plateRows > 0 &&
      plateCols > 0 && fields > 0 && plateAcqs > 0;
    if (hasSPW) {
      // generate SPW metadata and override series count to match
      int imageCount =
        populateSPW(store, plates, plateRows, plateCols, fields, plateAcqs);
      if (imageCount > 0) seriesCount = imageCount;
      else hasSPW = false; // failed to generate SPW metadata
    }

    // populate core metadata
    int effSizeC = sizeC / rgb;
    core.clear();
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
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
    }

    // populate OME metadata
    boolean planeInfo = (exposureTime != null);
    // per file counts
    int annotationCount = 0;
    int annotationDoubleCount = 0;
    int annotationLongCount = 0;
    int annotationBoolCount = 0;
    int annotationCommentCount = 0;
    int annotationTagCount = 0;
    int annotationTermCount = 0;
    int annotationTimeCount = 0;
    int annotationXmlCount = 0;
    int annotationMapCount = 0;
    // per image count
    int annotationRefCount = 0;

    String nextAnnotationID;
    MetadataTools.populatePixels(store, this, planeInfo);
    fillExposureTime(store);
    fillPhysicalSizes(store);
    for (int currentImageIndex=0; currentImageIndex<seriesCount; currentImageIndex++) {
      String imageName = currentImageIndex > 0 ? name + " " + (currentImageIndex + 1) : name;
      store.setImageName(imageName, currentImageIndex);
      if (acquisitionDate != null) {
        if(DateTools.getTime(acquisitionDate, DateTools.FILENAME_FORMAT) != -1) {
          store.setImageAcquisitionDate(new Timestamp(DateTools.formatDate(acquisitionDate, DateTools.FILENAME_FORMAT)), currentImageIndex);
        }
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        Color channel = defaultColor == null ? null: new Color(defaultColor);
        if (c < color.size() && color.get(c) != null) {
          channel = new Color(color.get(c));
        }
        if (channel != null) {
          store.setChannelColor(channel, currentImageIndex, c);
        }
      }

      // new image so reset annotationRefCount
      annotationRefCount = 0;
      for (int currentAnnotation=0; currentAnnotation<annLong; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setLongAnnotationID(nextAnnotationID, annotationLongCount);
        store.setLongAnnotationNamespace(ANNOTATION_NAMESPACE, annotationLongCount);
        store.setLongAnnotationValue(ANN_LONG_VALUE+annotationCount, annotationLongCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationLongCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annDouble; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setDoubleAnnotationID(nextAnnotationID, annotationDoubleCount);
        store.setDoubleAnnotationNamespace(ANNOTATION_NAMESPACE, annotationDoubleCount);
        store.setDoubleAnnotationValue(ANN_DOUBLE_VALUE*(annotationCount+1), annotationDoubleCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationDoubleCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annMap; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setMapAnnotationID(nextAnnotationID, annotationMapCount);
        store.setMapAnnotationNamespace(ANNOTATION_NAMESPACE, annotationMapCount);
        List<MapPair> mapValue = new ArrayList<MapPair>();
        for (int keyNum=0; keyNum<10; keyNum++) {
          mapValue.add(new MapPair("keyS" + currentImageIndex + "N" + keyNum, "val" + (keyNum+1)*(annotationCount+1)));
        }
        store.setMapAnnotationValue(mapValue, annotationMapCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationMapCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annComment; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setCommentAnnotationID(nextAnnotationID, annotationCommentCount);
        store.setCommentAnnotationNamespace(ANNOTATION_NAMESPACE, annotationCommentCount);
        store.setCommentAnnotationValue(ANN_COMMENT_VALUE + (annotationCount+1), annotationCommentCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationCommentCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annBool; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setBooleanAnnotationID(nextAnnotationID, annotationBoolCount);
        store.setBooleanAnnotationNamespace(ANNOTATION_NAMESPACE, annotationBoolCount);
        store.setBooleanAnnotationValue(ANN_BOOLEAN_VALUE, annotationBoolCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationBoolCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annTime; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setTimestampAnnotationID(nextAnnotationID, annotationTimeCount);
        store.setTimestampAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTimeCount);
        store.setTimestampAnnotationValue(ANN_TIME_VALUE, annotationTimeCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationTimeCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annTag; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setTagAnnotationID(nextAnnotationID, annotationTagCount);
        store.setTagAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTagCount);
        store.setTagAnnotationValue(ANN_TAG_VALUE + (annotationCount+1), annotationTagCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationTagCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annTerm; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setTermAnnotationID(nextAnnotationID, annotationTermCount);
        store.setTermAnnotationNamespace(ANNOTATION_NAMESPACE, annotationTermCount);
        store.setTermAnnotationValue(ANN_TERM_VALUE + (annotationCount+1), annotationTermCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationTermCount++;
        annotationCount++;
        annotationRefCount++;
      }

      for (int currentAnnotation=0; currentAnnotation<annXml; currentAnnotation++) {
        nextAnnotationID = ANNOTATION_PREFIX + annotationCount;
        store.setXMLAnnotationID(nextAnnotationID, annotationXmlCount);
        store.setXMLAnnotationNamespace(ANNOTATION_NAMESPACE, annotationXmlCount);
        store.setXMLAnnotationValue(ANN_XML_VALUE_START + (annotationCount+1) + ANN_XML_VALUE_END, annotationXmlCount);
        store.setImageAnnotationRef(nextAnnotationID, currentImageIndex, annotationRefCount);
        annotationXmlCount++;
        annotationCount++;
        annotationRefCount++;
      }

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

// -- Helper methods --

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
        plateAcqs = Integer.valueOf(pathToken.substring(pathToken.lastIndexOf(
            ResourceNamer.RUN) + ResourceNamer.RUN.length(),
            pathToken.length())) + 1;
      } else if (pathToken.startsWith(ResourceNamer.WELL)) {
        String wellId = pathToken.substring(pathToken.lastIndexOf(
            ResourceNamer.WELL) + ResourceNamer.WELL.length(),
            pathToken.length());
        String[] elements = wellId.split("(?<=\\p{L})(?=\\d)");
        rows = ResourceNamer.alphabeticIndexCount(elements[0]);
        cols = Integer.valueOf(elements[1]) + 1;
      } else if (pathToken.startsWith(ResourceNamer.FIELD)) {
        String fieldName = pathToken.substring(0, pathToken.lastIndexOf("."));
        fields = Integer.valueOf(fieldName.substring(fieldName.lastIndexOf(
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

  private int populateSPW(MetadataStore store, int plates, int rows, int cols,
    int fields, int acqs)
  {
    final XMLMockObjects xml = new XMLMockObjects();
    final OME ome =
      xml.createPopulatedScreen(plates, rows, cols, fields, acqs);
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

  private Length parseLength(String value, String defaultUnit) {
      Matcher m = Pattern.compile("\\s*([\\d.]+)\\s*([\\D\\S]*)\\s*").matcher(value);
      if (!m.matches()) {
        throw new RuntimeException(String.format(
                "%s does not match a physical size!", value));
      }
      String number = m.group(1);
      String unit = m.group(2);
      if (unit == null || unit.trim().length() == 0) {
        unit = defaultUnit;
      }

      double d = Double.valueOf(number);
      Unit<Length> l = null;
      try {
        l = UnitsLengthEnumHandler.getBaseUnit(UnitsLength.fromString(unit));
      } catch (EnumerationException e) {
        throw new RuntimeException(String.format(
                "%s does not match a length unit!", unit));
      }
      return new Length(d, l);
  }
}
