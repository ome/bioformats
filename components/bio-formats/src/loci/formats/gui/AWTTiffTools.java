//
// AWTTiffTools.java
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

package loci.formats.gui;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import loci.common.DataTools;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.TiffTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffRational;

/**
 * A utility class for manipulating TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/gui/AWTTiffTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/gui/AWTTiffTools.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public final class AWTTiffTools {

  /**
   * Writes the given field to the specified output stream using the given
   * byte offset and IFD, in big-endian format.
   *
   * @param buf The pixels to write
   * @param ifd The TIFF IFD; can be null
   * @param out The output stream to which the TIFF data should be written
   * @param offset The value to use for specifying byte offsets
   * @param last Whether this image is the final IFD entry of the TIFF data
   * @param bigTiff Whether this image should be written as BigTIFF
   * @return total number of bytes written
   */
  public static long writeImage(byte[] buf, IFD ifd,
    RandomAccessOutputStream out, long offset, boolean last, boolean bigTiff,
    ColorModel colorModel, int pixelType, boolean interleaved)
    throws FormatException, IOException
  {
    if (buf == null) throw new FormatException("Byte array is null");
    TiffTools.debug("writeImage (offset=" + offset + "; last=" + last + ")");

    boolean little = TiffTools.isLittleEndian(ifd);

    int width = (int) TiffTools.getImageWidth(ifd);
    int height = (int) TiffTools.getImageLength(ifd);

    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    int plane = (int) (width * height * bytesPerPixel);
    int nChannels = buf.length / plane;

    boolean indexed =
      colorModel != null && (colorModel instanceof IndexColorModel);

    // populate required IFD directory entries (except strip information)
    if (ifd == null) ifd = new IFD();
    TiffTools.putIFDValue(ifd, TiffTools.IMAGE_WIDTH, width);
    TiffTools.putIFDValue(ifd, TiffTools.IMAGE_LENGTH, height);
    if (TiffTools.getIFDValue(ifd, TiffTools.BITS_PER_SAMPLE) == null) {
      int bps = 8 * bytesPerPixel;
      int[] bpsArray = new int[nChannels];
      Arrays.fill(bpsArray, bps);
      TiffTools.putIFDValue(ifd, TiffTools.BITS_PER_SAMPLE, bpsArray);
    }
    if (FormatTools.isFloatingPoint(pixelType)) {
      TiffTools.putIFDValue(ifd, TiffTools.SAMPLE_FORMAT, 3);
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.COMPRESSION) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.COMPRESSION, TiffTools.UNCOMPRESSED);
    }
    if (TiffTools.getIFDValue(ifd,
      TiffTools.PHOTOMETRIC_INTERPRETATION) == null)
    {
      int photometricInterpretation = indexed ? TiffTools.RGB_PALETTE :
        nChannels == 1 ? TiffTools.BLACK_IS_ZERO : TiffTools.RGB;
      TiffTools.putIFDValue(ifd, TiffTools.PHOTOMETRIC_INTERPRETATION,
        photometricInterpretation);
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.SAMPLES_PER_PIXEL) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.SAMPLES_PER_PIXEL, nChannels);
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.X_RESOLUTION) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.X_RESOLUTION,
        new TiffRational(1, 1)); // no unit
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.Y_RESOLUTION) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.Y_RESOLUTION,
        new TiffRational(1, 1)); // no unit
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.RESOLUTION_UNIT) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.RESOLUTION_UNIT, 1); // no unit
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.SOFTWARE, "LOCI Bio-Formats");
    }
    if (TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION) == null) {
      TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION, "");
    }
    if (indexed && TiffTools.getIFDValue(ifd, TiffTools.COLOR_MAP) == null) {
      byte[][] lut = new byte[3][256];
      IndexColorModel model = (IndexColorModel) colorModel;
      model.getReds(lut[0]);
      model.getGreens(lut[1]);
      model.getBlues(lut[2]);
      int[] colorMap = new int[3 * 256];
      for (int i=0; i<lut.length; i++) {
        for (int j=0; j<lut[0].length; j++) {
          colorMap[i * lut[0].length + j] = (int) ((lut[i][j] & 0xff) << 8);
        }
      }
      TiffTools.putIFDValue(ifd, TiffTools.COLOR_MAP, colorMap);
    }

    // create pixel output buffers
    int compression = TiffTools.getIFDIntValue(ifd,
      TiffTools.COMPRESSION, false, TiffTools.UNCOMPRESSED);
    boolean fullImageCompression = false;
    if (compression == TiffTools.JPEG_2000 || compression == TiffTools.JPEG) {
      fullImageCompression = true;
    }
    int pixels = fullImageCompression ? width * height : width;
    int stripSize = Math.max(8192, pixels * bytesPerPixel * nChannels);
    int rowsPerStrip = stripSize / (width * bytesPerPixel * nChannels);
    int stripsPerImage = (height + rowsPerStrip - 1) / rowsPerStrip;
    int[] bps = (int[]) TiffTools.getIFDValue(ifd,
      TiffTools.BITS_PER_SAMPLE, true, int[].class);
    ByteArrayOutputStream[] stripBuf =
      new ByteArrayOutputStream[stripsPerImage];
    DataOutputStream[] stripOut = new DataOutputStream[stripsPerImage];
    for (int i=0; i<stripsPerImage; i++) {
      stripBuf[i] = new ByteArrayOutputStream(stripSize);
      stripOut[i] = new DataOutputStream(stripBuf[i]);
    }

    // write pixel strips to output buffers
    for (int y=0; y<height; y++) {
      int strip = y / rowsPerStrip;
      for (int x=0; x<width; x++) {
        int ndx = y * width * bytesPerPixel + x * bytesPerPixel;
        for (int c=0; c<nChannels; c++) {
          for (int n=0; n<bps[c]/8; n++) {
            int off = interleaved ? ndx * nChannels + c * bytesPerPixel + n :
              c * plane + ndx + n;
            stripOut[strip].writeByte(buf[off]);
          }
        }
      }
    }

    // compress strips according to given differencing and compression schemes
    int planarConfig = TiffTools.getPlanarConfiguration(ifd);
    int predictor = TiffTools.getIFDIntValue(ifd,
      TiffTools.PREDICTOR, false, 1);

    byte[][] strips = new byte[stripsPerImage][];
    for (int i=0; i<stripsPerImage; i++) {
      strips[i] = stripBuf[i].toByteArray();
      TiffTools.difference(strips[i], bps, width, planarConfig, predictor);
      strips[i] = TiffTools.compress(strips[i], ifd);
    }

    // record strip byte counts and offsets
    long[] stripByteCounts = new long[stripsPerImage];
    long[] stripOffsets = new long[stripsPerImage];
    TiffTools.putIFDValue(ifd, TiffTools.STRIP_OFFSETS, stripOffsets);
    TiffTools.putIFDValue(ifd, TiffTools.ROWS_PER_STRIP, rowsPerStrip);
    TiffTools.putIFDValue(ifd, TiffTools.STRIP_BYTE_COUNTS, stripByteCounts);

    Object[] keys = ifd.keySet().toArray();
    Arrays.sort(keys); // sort IFD tags in ascending order

    int keyCount = keys.length;
    if (ifd.containsKey(new Integer(TiffTools.LITTLE_ENDIAN))) keyCount--;
    if (ifd.containsKey(new Integer(TiffTools.BIG_TIFF))) keyCount--;

    int bytesPerEntry = bigTiff ? TiffTools.BIG_TIFF_BYTES_PER_ENTRY :
      TiffTools.BYTES_PER_ENTRY;
    int ifdBytes = (bigTiff ? 16 : 6) + bytesPerEntry * keyCount;

    long pixelBytes = 0;
    for (int i=0; i<stripsPerImage; i++) {
      stripByteCounts[i] = strips[i].length;
      stripOffsets[i] = pixelBytes + offset + ifdBytes;
      pixelBytes += stripByteCounts[i];
    }

    // create IFD output buffers
    ByteArrayOutputStream ifdBuf = new ByteArrayOutputStream(ifdBytes);
    DataOutputStream ifdOut = new DataOutputStream(ifdBuf);
    ByteArrayOutputStream extraBuf = new ByteArrayOutputStream();
    DataOutputStream extraOut = new DataOutputStream(extraBuf);

    offset += ifdBytes + pixelBytes;

    // write IFD to output buffers

    // number of directory entries
    if (bigTiff) DataTools.writeLong(ifdOut, keyCount, little);
    else DataTools.writeShort(ifdOut, keyCount, little);
    for (int k=0; k<keys.length; k++) {
      Object key = keys[k];
      if (!(key instanceof Integer)) {
        throw new FormatException("Malformed IFD tag (" + key + ")");
      }
      if (((Integer) key).intValue() == TiffTools.LITTLE_ENDIAN) continue;
      if (((Integer) key).intValue() == TiffTools.BIG_TIFF) continue;
      Object value = ifd.get(key);
      String sk = TiffTools.getIFDTagName(((Integer) key).intValue());
      String sv = value instanceof int[] ?
        ("int[" + ((int[]) value).length + "]") : value.toString();
      TiffTools.debug("writeImage: writing " + sk + " (value=" + sv + ")");
      TiffTools.writeIFDValue(ifdOut, extraBuf, extraOut, offset,
        ((Integer) key).intValue(), value, bigTiff, little);
    }
    // offset to next IFD
    if (bigTiff) {
      DataTools.writeLong(ifdOut, last ? 0 : offset + extraBuf.size(), little);
    }
    else {
      DataTools.writeInt(ifdOut, last ? 0 : (int) (offset + extraBuf.size()),
        little);
    }

    // flush buffers to output stream
    byte[] ifdArray = ifdBuf.toByteArray();
    byte[] extraArray = extraBuf.toByteArray();
    long numBytes = ifdArray.length + extraArray.length;
    out.write(ifdArray);
    for (int i=0; i<strips.length; i++) {
      out.write(strips[i]);
      numBytes += strips[i].length;
    }
    out.write(extraArray);
    return numBytes;
  }

}
