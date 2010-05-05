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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">SVN</a></dd></dl>
 */
public class TiffWriter extends FormatWriter {

  // -- Constants --

  public static final String COMPRESSION_UNCOMPRESSED = "Uncompressed";
  public static final String COMPRESSION_LZW = "LZW";
  public static final String COMPRESSION_J2K = "J2K";
  public static final String COMPRESSION_J2K_LOSSY = "J2K-Lossy";
  public static final String COMPRESSION_JPEG = "JPEG";

  // -- Fields --

  /** Current output stream. */
  protected RandomAccessOutputStream out;

  /** Whether or not the output file is a BigTIFF file. */
  protected boolean isBigTiff;

  /** The TiffSaver that will do most of the writing. */
  protected TiffSaver tiffSaver;

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
  public void saveBytes(byte[] buf, IFD ifd, boolean last)
    throws IOException, FormatException
  {
    saveBytes(buf, ifd, 0, last, last);
  }

  /**
   * Saves the given image to the specified series in the current file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units. If this image is the last one in the series,
   * the lastInSeries flag must be set. If this image is the last one in the
   * file, the last flag must be set.
   */
  public void saveBytes(byte[] buf, IFD ifd, int series,
    boolean lastInSeries, boolean last) throws IOException, FormatException
  {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(retrieve, series);
    Boolean bigEndian = retrieve.getPixelsBinDataBigEndian(series, 0);
    boolean littleEndian = bigEndian == null ?
      false : !bigEndian.booleanValue();

    if (initialized) {
      tiffSaver = new TiffSaver(out);
      tiffSaver.setLittleEndian(littleEndian);
      tiffSaver.setBigTiff(isBigTiff);
    }

    if (!initialized) {
      initialized = true;
      out = new RandomAccessOutputStream(currentId);
      tiffSaver = new TiffSaver(out);
      tiffSaver.setLittleEndian(littleEndian);
      tiffSaver.setBigTiff(isBigTiff);

      RandomAccessInputStream tmp = new RandomAccessInputStream(currentId);
      if (tmp.length() == 0) {
        // write TIFF header
        tiffSaver.writeHeader();
      }
    }

    int width = retrieve.getPixelsSizeX(series).getValue().intValue();
    int height = retrieve.getPixelsSizeY(series).getValue().intValue();
    Integer channels = retrieve.getChannelSamplesPerPixel(series, 0);
    if (channels == null) {
      LOGGER.warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int c = channels == null ? 1 : channels.intValue();
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
      for (int i=0; i<c; i++) {
        byte[] b = ImageTools.splitChannels(buf, i, c, bytesPerPixel,
          false, interleaved);

        saveBytes(b, ifd, series, lastInSeries && i == c - 1,
          last && i == c - 1);
      }
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
    tiffSaver.writeImage(buf, ifd, last, type);
    if (last) close();
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    IFD h = new IFD();
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
    h.put(new Integer(IFD.COMPRESSION), compressType.getCode());
    saveBytes(buf, h, series, lastInSeries, last);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    if (codec.startsWith(COMPRESSION_J2K) || codec.equals(COMPRESSION_JPEG)) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8};
    }
    return super.getPixelTypes(codec);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
    initialized = false;
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

}
