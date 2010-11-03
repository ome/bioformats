//
// TiffWriter.java
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

package loci.formats.out;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffSaver;

/**
 * TiffWriter is the file format writer for TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">SVN</a></dd></dl>
 */
public class TiffWriter extends FormatWriter {

  // -- Constants --

  public static final String COMPRESSION_UNCOMPRESSED = "Uncompressed";
  public static final String COMPRESSION_LZW = "LZW";
  public static final String COMPRESSION_J2K = "J2K";
  public static final String COMPRESSION_J2K_LOSSY = "J2K-Lossy";
  public static final String COMPRESSION_JPEG = "JPEG";

  // -- Fields --

  /** Whether or not the output file is a BigTIFF file. */
  protected boolean isBigTiff;

  /** Whether or not we are writing planes sequentially. */
  protected boolean sequential;

  /** The TiffSaver that will do most of the writing. */
  protected TiffSaver tiffSaver;

  /** Whether or not to check the parameters passed to saveBytes. */
  private boolean checkParams = true;

  // -- Constructors --

  public TiffWriter() {
    this("Tagged Image File Format", new String[] {"tif", "tiff"});
  }

  public TiffWriter(String format, String[] exts) {
    super(format, exts);
    compressionTypes = new String[] {
      COMPRESSION_UNCOMPRESSED,
      COMPRESSION_LZW,
      COMPRESSION_J2K,
      COMPRESSION_J2K_LOSSY,
      COMPRESSION_JPEG
    };
    isBigTiff = false;
  }

  // -- TiffWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.  If this image is the last one in the file,
   * the last flag must be set.
   */
  public void saveBytes(int no, byte[] buf, IFD ifd)
    throws IOException, FormatException
  {
    MetadataRetrieve r = getMetadataRetrieve();
    int w = r.getPixelsSizeX(series).getValue().intValue();
    int h = r.getPixelsSizeY(series).getValue().intValue();
    saveBytes(no, buf, ifd, 0, 0, w, h);
  }

  /**
   * Saves the given image to the specified series in the current file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units. If this image is the last one in the series,
   * the lastInSeries flag must be set. If this image is the last one in the
   * file, the last flag must be set.
   */
  public void saveBytes(int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
    throws IOException, FormatException
  {
    if (checkParams) checkParams(no, buf, x, y, w, h);
    MetadataRetrieve retrieve = getMetadataRetrieve();
    Boolean bigEndian = retrieve.getPixelsBinDataBigEndian(series, 0);
    boolean littleEndian = bigEndian == null ?
      false : !bigEndian.booleanValue();

    tiffSaver = new TiffSaver(out);
    tiffSaver.setWritingSequentially(sequential);
    tiffSaver.setLittleEndian(littleEndian);
    tiffSaver.setBigTiff(isBigTiff);

    if (!initialized[series][no]) {
      initialized[series][no] = true;

      RandomAccessInputStream tmp = new RandomAccessInputStream(currentId);
      if (tmp.length() == 0) {
        // write TIFF header
        tiffSaver.writeHeader();
      }
      tmp.close();
    }

    int width = retrieve.getPixelsSizeX(series).getValue().intValue();
    int height = retrieve.getPixelsSizeY(series).getValue().intValue();
    int c = getSamplesPerPixel();
    int type = FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(type);

    int plane = width * height * c * bytesPerPixel;
    if (plane > buf.length) {
      c = buf.length / (width * height * bytesPerPixel);
      plane = width * height * c * bytesPerPixel;
    }

    if (bytesPerPixel > 1 && c != 1 && c != 3) {
      // split channels
      checkParams = false;

      if (no == 0) {
        initialized[series] = new boolean[initialized[series].length * c];
      }

      for (int i=0; i<c; i++) {
        byte[] b = ImageTools.splitChannels(buf, i, c, bytesPerPixel,
          false, interleaved);

        saveBytes(no + i, b, ifd, x, y, w, h);
      }
      checkParams = true;
      return;
    }

    byte[][] lut = AWTImageTools.get8BitLookupTable(cm);
    if (lut != null) {
      int[] colorMap = new int[lut.length * lut[0].length];
      for (int i=0; i<lut.length; i++) {
        for (int j=0; j<lut[0].length; j++) {
          colorMap[i * lut[0].length + j] = (int) ((lut[i][j] & 0xff) << 8);
        }
      }
      ifd.putIFDValue(IFD.COLOR_MAP, colorMap);
    }

    ifd.put(new Integer(IFD.IMAGE_WIDTH), new Integer(width));
    ifd.put(new Integer(IFD.IMAGE_LENGTH), new Integer(height));

    if (!isBigTiff) {
      isBigTiff = (out.length() + 2 * plane) >= 4294967296L;
      if (isBigTiff) {
        throw new FormatException("File is too large; call setBigTiff(true)");
      }
    }

    // write the image
    ifd.put(new Integer(IFD.LITTLE_ENDIAN), new Boolean(littleEndian));
    out.seek(out.length());
    ifd.putIFDValue(IFD.PLANAR_CONFIGURATION, interleaved ? 1 : 2);

    int sampleFormat = 1;
    if (FormatTools.isSigned(type)) sampleFormat = 2;
    if (FormatTools.isFloatingPoint(type)) sampleFormat = 3;
    ifd.putIFDValue(IFD.SAMPLE_FORMAT, sampleFormat);
    RandomAccessInputStream in = new RandomAccessInputStream(currentId);
    in.order(littleEndian);
    tiffSaver.setInputStream(in);

    int index = no;
    int realSeries = getSeries();
    for (int i=0; i<realSeries; i++) {
      setSeries(i);
      index += getPlaneCount();
    }
    setSeries(realSeries);

    tiffSaver.writeImage(buf, ifd, index, type, x, y, w, h,
      no == getPlaneCount() - 1 && getSeries() == retrieve.getImageCount() - 1);
    tiffSaver.setInputStream(null);
    in.close();
  }

  // -- FormatWriter API methods --

  protected int getPlaneCount() {
    int c = getSamplesPerPixel();
    if (c == 1 || c == 3) return super.getPlaneCount();
    return c * super.getPlaneCount();
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    IFD ifd = new IFD();
    if (compression == null) compression = "";
    TiffCompression compressType = TiffCompression.UNCOMPRESSED;
    if (compression.equals(COMPRESSION_LZW)) {
      compressType = TiffCompression.LZW;
    }
    else if (compression.equals(COMPRESSION_J2K)) {
      compressType = TiffCompression.JPEG_2000;
    }
    else if (compression.equals(COMPRESSION_J2K_LOSSY)) {
      compressType = TiffCompression.JPEG_2000_LOSSY;
    }
    else if (compression.equals(COMPRESSION_JPEG)) {
      compressType = TiffCompression.JPEG;
    }
    ifd.put(new Integer(IFD.COMPRESSION), compressType.getCode());
    saveBytes(no, buf, ifd, x, y, w, h);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    if (codec.startsWith(COMPRESSION_J2K) || codec.equals(COMPRESSION_JPEG)) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8};
    }
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT, FormatTools.DOUBLE};
  }

  // -- TiffWriter API methods --

  /**
   * Sets whether or not BigTIFF files should be written.
   * This flag is not reset when close() is called.
   */
  public void setBigTiff(boolean bigTiff) {
    FormatTools.assertId(currentId, false, 1);
    isBigTiff = bigTiff;
  }

  /**
   * Sets whether or not we know that planes will be written sequentially.
   * If planes are written sequentially and this flag is set, then performance
   * will be slightly improved.
   */
  public void setWriteSequentially(boolean sequential) {
    this.sequential = sequential;
  }

}
