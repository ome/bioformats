/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.Random;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

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
 * </ul></p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/FakeReader.java">Trac</a>
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/FakeReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class FakeReader extends FormatReader {

  // -- Constants --

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

  // -- Constructor --

  /** Constructs a new fake reader. */
  public FakeReader() { super("Simulated data", "fake"); }

  // -- IFormatReader API methods --

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return ac < 0 || lut8 == null ? null : lut8[ac];
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return ac < 0 || lut16 == null ? null : lut16[ac];
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    final int series = getSeries();
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
                pixel = series;
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

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String path = id;
    if (new Location(id).exists()) {
      path = new Location(id).getAbsoluteFile().getName();
    }
    String noExt = path.substring(0, path.lastIndexOf("."));
    String[] tokens = noExt.split(TOKEN_SEPARATOR);

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
      double doubleValue = Double.NaN;
      try {
        doubleValue = Double.parseDouble(value);
      }
      catch (NumberFormatException exc) { }
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

    // populate core metadata
    int effSizeC = sizeC / rgb;
    core = new CoreMetadata[seriesCount];
    for (int s=0; s<seriesCount; s++) {
      core[s] = new CoreMetadata();
      core[s].sizeX = sizeX;
      core[s].sizeY = sizeY;
      core[s].sizeZ = sizeZ;
      core[s].sizeC = sizeC;
      core[s].sizeT = sizeT;
      core[s].thumbSizeX = thumbSizeX;
      core[s].thumbSizeY = thumbSizeY;
      core[s].pixelType = pixelType;
      core[s].bitsPerPixel = bitsPerPixel;
      core[s].imageCount = sizeZ * effSizeC * sizeT;
      core[s].rgb = rgb > 1;
      core[s].dimensionOrder = dimOrder;
      core[s].orderCertain = orderCertain;
      core[s].littleEndian = little;
      core[s].interleaved = interleaved;
      core[s].indexed = indexed;
      core[s].falseColor = falseColor;
      core[s].metadataComplete = metadataComplete;
      core[s].thumbnail = thumbnail;
    }

    // populate OME metadata
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    for (int s=0; s<seriesCount; s++) {
      String imageName = s > 0 ? name + " " + (s + 1) : name;
      store.setImageName(imageName, s);
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

  // -- Helper methods --

  /** Creates a mapping between indices and color values. */
  private void createIndexMap(int num) {
    int sizeC = core[0].sizeC;

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

}
