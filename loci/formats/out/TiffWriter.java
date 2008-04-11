//
// TiffWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.out;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * TiffWriter is the file format writer for TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/TiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/TiffWriter.java">SVN</a></dd></dl>
 */
public class TiffWriter extends FormatWriter {

  // -- Fields --

  /** The last offset written to. */
  protected long lastOffset;

  /** Current output stream. */
  protected BufferedOutputStream out;

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
    compressionTypes = new String[] {"Uncompressed", "LZW"};
    isBigTiff = false;
  }

  // -- TiffWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.  If this image is the last one in the file,
   * the last flag must be set.
   */
  public void saveImage(Image image, Hashtable ifd, boolean last)
    throws IOException, FormatException
  {
    saveImage(image, ifd, 0, last, last);
  }

  /**
   * Saves the given image to the specified series in the current file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units. If this image is the last one in the series,
   * the lastInSeries flag must be set. If this image is the last one in the
   * file, the last flag must be set.
   */
  public void saveImage(Image image, Hashtable ifd, int series,
    boolean lastInSeries, boolean last) throws IOException, FormatException
  {
    if (!initialized) {
      imageCounts = new Vector();
      initialized = true;
      out =
        new BufferedOutputStream(new FileOutputStream(currentId, true), 4096);

      RandomAccessStream tmp = new RandomAccessStream(currentId);
      if (tmp.length() == 0) {
        // write TIFF header
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeByte(TiffTools.BIG);
        dataOut.writeByte(TiffTools.BIG);
        if (isBigTiff) dataOut.writeShort(TiffTools.BIG_TIFF_MAGIC_NUMBER);
        else dataOut.writeShort(TiffTools.MAGIC_NUMBER);
        dataOut.writeInt(8); // offset to first IFD
        lastOffset = 8;
        if (isBigTiff) {
          dataOut.writeLong(16);
          lastOffset = 16;
        }
      }
      else {
        // compute the offset to the last IFD
        TiffTools.checkHeader(tmp);
        long offset = TiffTools.getFirstOffset(tmp);
        long ifdMax = (tmp.length() - 8) / 18;

        for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
          TiffTools.getIFD(tmp, ifdNum, offset);
          offset = tmp.readInt();
          if (offset <= 0 || offset >= tmp.length()) break;
        }
        lastOffset = offset;
      }
      tmp.close();
    }

    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);

    int plane = img.getWidth() * img.getHeight() *
      img.getRaster().getNumBands() *
      FormatTools.getBytesPerPixel(ImageTools.getPixelType(img));

    if (!isBigTiff) {
      RandomAccessStream tmp = new RandomAccessStream(currentId);
      isBigTiff = (tmp.length() + 2 * plane) >= 4294967296L;
      if (isBigTiff) {
        throw new FormatException("File is too large; call setBigTiff(true)");
      }
      tmp.close();
    }

    // write the image
    lastOffset +=
      TiffTools.writeImage(img, ifd, out, lastOffset, last, isBigTiff);
    if (last) close();
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(Image image, boolean last)
    throws FormatException, IOException
  {
    saveImage(image, 0, last, last);
  }

  /* @see loci.formats.IFormatWriter#saveImage(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    Hashtable h = new Hashtable();
    if (compression == null) compression = "";
    h.put(new Integer(TiffTools.COMPRESSION), compression.equals("LZW") ?
      new Integer(TiffTools.LZW) : new Integer(TiffTools.UNCOMPRESSED));
    saveImage(image, h, series, lastInSeries, last);
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
  }

  // -- TiffWriter API methods --

  public void setBigTiff(boolean bigTiff) {
    FormatTools.assertId(currentId, false, 1);
    isBigTiff = bigTiff;
  }

}
