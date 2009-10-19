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
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTTiffTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
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

  /** The last offset written to. */
  protected long lastOffset;

  /** Current output stream. */
  protected RandomAccessOutputStream out;

  /** Image counts for each open series. */
  protected Vector imageCounts;

  /** Whether or not the output file is a BigTIFF file. */
  protected boolean isBigTiff;

  // -- Constructors --

  public TiffWriter() {
    this("Tagged Image File Format", new String[] {"tif", "tiff"});
  }

  public TiffWriter(String format, String[] exts) {
    super(format, exts);
    lastOffset = 0;
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
    Boolean bigEndian = retrieve.getPixelsBigEndian(series, 0);
    boolean littleEndian = bigEndian == null ?
      false : !bigEndian.booleanValue();

    if (!initialized) {
      imageCounts = new Vector();
      initialized = true;
      out = new RandomAccessOutputStream(currentId);

      RandomAccessInputStream tmp = new RandomAccessInputStream(currentId);
      if (tmp.length() == 0) {
        // write TIFF header
        TiffSaver tiffSaver = new TiffSaver(out);
        tiffSaver.writeHeader(littleEndian, isBigTiff);
        lastOffset = isBigTiff ? 16 : 8;
      }
      else {
        // compute the offset to the last IFD
        TiffParser tiffParser = new TiffParser(tmp);
        tiffParser.checkHeader();
        long offset = tiffParser.getFirstOffset();
        long ifdMax = (tmp.length() - 8) / 18;

        for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
          tiffParser.getIFD(ifdNum, offset);
          offset = tmp.readInt();
          if (offset <= 0 || offset >= tmp.length()) break;
        }
        lastOffset = offset;
      }
      tmp.close();
    }

    int width = retrieve.getPixelsSizeX(series, 0).intValue();
    int height = retrieve.getPixelsSizeY(series, 0).intValue();
    Integer channels = retrieve.getLogicalChannelSamplesPerPixel(series, 0);
    if (channels == null) {
      warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int c = channels == null ? 1 : channels.intValue();
    int type =
      FormatTools.pixelTypeFromString(retrieve.getPixelsPixelType(series, 0));
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

    ifd.put(new Integer(IFD.IMAGE_WIDTH), new Integer(width));
    ifd.put(new Integer(IFD.IMAGE_LENGTH), new Integer(height));

    if (!isBigTiff) {
      RandomAccessInputStream tmp = new RandomAccessInputStream(currentId);
      isBigTiff = (tmp.length() + 2 * plane) >= 4294967296L;
      if (isBigTiff) {
        throw new FormatException("File is too large; call setBigTiff(true)");
      }
      tmp.close();
    }

    // write the image
    ifd.put(new Integer(IFD.LITTLE_ENDIAN), new Boolean(littleEndian));
    out.seek(out.length());
    lastOffset += AWTTiffTools.writeImage(buf, ifd, out, lastOffset, last,
      isBigTiff, getColorModel(), type, interleaved);
    if (last) close();
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    IFD h = new IFD();
    if (compression == null) compression = "";
    Integer compressType = new Integer(TiffCompression.UNCOMPRESSED);
    if (compression.equals(COMPRESSION_LZW)) {
      compressType = new Integer(TiffCompression.LZW);
    }
    else if (compression.equals(COMPRESSION_J2K)) {
      compressType = new Integer(TiffCompression.JPEG_2000);
    }
    else if (compression.equals(COMPRESSION_J2K_LOSSY)) {
      compressType = new Integer(TiffCompression.JPEG_2000_LOSSY);
    }
    else if (compression.equals(COMPRESSION_JPEG)) {
      compressType = new Integer(TiffCompression.JPEG);
    }
    h.put(new Integer(IFD.COMPRESSION), compressType);
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
    lastOffset = 0;
    imageCounts = null;
    isBigTiff = false;
  }

  // -- TiffWriter API methods --

  public void setBigTiff(boolean bigTiff) {
    FormatTools.assertId(currentId, false, 1);
    isBigTiff = bigTiff;
  }

}
