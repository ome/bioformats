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

import java.awt.Image;
import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.gui.AWTTiffTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;

/**
 * TiffWriter is the file format writer for TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/TiffWriter.java">SVN</a></dd></dl>
 */
public class TiffWriter extends FormatWriter {

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
    compressionTypes = new String[] {"Uncompressed", "LZW", "J2K", "JPEG"};
    isBigTiff = false;
  }

  // -- TiffWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.  If this image is the last one in the file,
   * the last flag must be set.
   */
  public void saveImage(Image image, IFD ifd, boolean last) {
    // TODO
  }

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
      TiffParser tiffParser = new TiffParser(tmp);
      if (tmp.length() == 0) {
        // write TIFF header
        TiffTools.writeHeader(out, littleEndian, isBigTiff);
        lastOffset = isBigTiff ? 16 : 8;
      }
      else {
        // compute the offset to the last IFD
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
    int c = retrieve.getLogicalChannelSamplesPerPixel(series, 0).intValue();
    int type =
      FormatTools.pixelTypeFromString(retrieve.getPixelsPixelType(series, 0));
    int bytesPerPixel = FormatTools.getBytesPerPixel(type);

    ifd.put(new Integer(IFD.IMAGE_WIDTH), new Integer(width));
    ifd.put(new Integer(IFD.IMAGE_LENGTH), new Integer(height));

    int plane = width * height * c * bytesPerPixel;

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
    if (compression.equals("LZW")) compressType = new Integer(TiffCompression.LZW);

    else if (compression.equals("J2K")) {
      compressType = new Integer(TiffCompression.JPEG_2000);
    }
    else if (compression.equals("JPEG")) {
      compressType = new Integer(TiffCompression.JPEG);
    }
    h.put(new Integer(IFD.COMPRESSION), compressType);
    saveBytes(buf, h, series, lastInSeries, last);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks() { return true; }

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
