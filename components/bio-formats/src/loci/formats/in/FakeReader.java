//
// FakeReader.java
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
import java.util.Random;

import loci.common.DataTools;
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FakeReader.java">Trac</a>
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FakeReader.java">SVN</a></dd></dl>
 */
public class FakeReader extends FormatReader {

  // -- Constants --

  public static final int BOX_SIZE = 10;
  private static final String TOKEN_SEPARATOR = "&";
  private static final long SEED = 0xcafebabe;

  // -- Fields --

  /** 8-bit lookup table, if indexed color. */
  private byte[][] lut8 = null;

  /** 16-bit lookup table, if indexed color. */
  private short[][] lut16 = null;

  private int[] indexToValue = null, valueToIndex = null;

  // -- Constructor --

  /** Constructs a new fake reader. */
  public FakeReader() { super("Simulated data", "fake"); }

  // -- IFormatReader API methods --

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return lut8;
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return lut16;
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

    // integer types start gradient at the smallest value
    long min = signed ? (long) -Math.pow(2, 8 * bpp - 1) : 0;
    if (floating) min = 0; // floating point types always start at 0

    for (int cOffset=0; cOffset<rgb; cOffset++) {
      int channel = cIndex + cOffset;
      for (int row=0; row<h; row++) {
        int yy = y + row;
        for (int col=0; col<w; col++) {
          int xx = x + col;
  
          // encode various information into the image plane
          long pixel = min + xx;
          if (yy < BOX_SIZE) {
            int grid = xx / BOX_SIZE;
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
            }
          }
  
          // if indexed color with non-null LUT, convert value to index
          if (indexed) {
            if (lut8 != null) pixel = valueToIndex[(int) (pixel % 256)];
            if (lut16 != null) pixel = valueToIndex[(int) (pixel % 65536)];
          }
  
          // if floating point, convert value to raw IEEE floating point bits
          switch (pixelType) {
            case FormatTools.FLOAT:
              pixel = Float.floatToIntBits(pixel);
              break;
            case FormatTools.DOUBLE:
              pixel = Double.doubleToLongBits(pixel);
              break;
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

    String noExt = id.substring(0, id.lastIndexOf("."));
    String[] tokens = noExt.split(TOKEN_SEPARATOR);

    String name = null;
    int sizeX = 512;
    int sizeY = 512;
    int sizeZ = 1;
    int sizeC = 1;
    int sizeT = 1;
    int thumbSizeX = 0; // default
    int thumbSizeY = 0; // default
    int pixelType = FormatTools.UINT8;
    int rgb = 1;
    String dimOrder = "XYZCT";
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

      boolean bool = value.equals("true");
      int num = -1;
      try { num = Integer.parseInt(value); }
      catch (NumberFormatException exc) { }

      if (key.equals("sizeX")) sizeX = num;
      else if (key.equals("sizeY")) sizeY = num;
      else if (key.equals("sizeZ")) sizeZ = num;
      else if (key.equals("sizeC")) sizeC = num;
      else if (key.equals("sizeT")) sizeT = num;
      else if (key.equals("thumbSizeX")) thumbSizeX = num;
      else if (key.equals("thumbSizeY")) thumbSizeY = num;
      else if (key.equals("pixelType")) {
        pixelType = FormatTools.pixelTypeFromString(value);
      }
      else if (key.equals("rgb")) rgb = num;
      else if (key.equals("dimOrder")) dimOrder = value.toUpperCase();
      else if (key.equals("orderCertain")) orderCertain = bool;
      else if (key.equals("little")) little = bool;
      else if (key.equals("interleaved")) interleaved = bool;
      else if (key.equals("indexed")) indexed = bool;
      else if (key.equals("falseColor")) falseColor = bool;
      else if (key.equals("metadataComplete")) metadataComplete = bool;
      else if (key.equals("thumbnail")) thumbnail = bool;
      else if (key.equals("series")) seriesCount = num;
      else if (key.equals("lutLength")) lutLength = num;
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
      MetadataTools.setDefaultCreationDate(store, id, s);
    }

    // for indexed color images, create lookup tables
    if (indexed) {
      if (pixelType == FormatTools.UINT8) {
        // create 8-bit LUT
        final int num = 256;
        createIndexMap(num);
        lut8 = new byte[lutLength][num];
        // linear ramp
        for (int c=0; c<lutLength; c++) {
          for (int index = 0; index < num; index++) {
            lut8[c][index] = (byte) indexToValue[index];
          }
        }
      }
      else if (pixelType == FormatTools.UINT16) {
        // create 16-bit LUT
        final int num = 65536;
        createIndexMap(num);
        lut16 = new short[lutLength][num];
        // linear ramp
        for (int c=0; c<lutLength; c++) {
          for (int index = 0; index < num; index++) {
            lut16[c][index] = (short) indexToValue[index];
          }
        }
      }
      // NB: Other pixel types will have null LUTs.
    }
  }

  // -- Helper methods --

  /** Creates a mapping between indices and color values. */
  private void createIndexMap(int num) {
    // create random mapping from indices to values
    indexToValue = new int[num];
    for (int index = 0; index < num; index++) indexToValue[index] = index;
    shuffle(indexToValue);

    // create inverse mapping: values to indices
    valueToIndex = new int[num];
    for (int index = 0; index < num; index++) {
      int value = indexToValue[index];
      valueToIndex[value] = index;
    }
  }

  /** Fisher-Yates shuffle with constant seed to ensure reproducibility. */
  private static void shuffle(int[] array) {
    Random r = new Random(SEED);
    for (int i = array.length; i > 1; i--) {
      int j = r.nextInt(i);
      int tmp = array[j];
      array[j] = array[i - 1];
      array[i - 1] = tmp;
    }
  }

}
