//
// JPEG2000Writer.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;
import loci.formats.codec.JPEG2000Codec;

/**
 * JPEG2000Writer is the file format writer for JPEG2000 files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/JPEG2000Writer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/JPEG2000Writer.java">SVN</a></dd></dl>
 */
public class JPEG2000Writer extends FormatWriter {

  // -- Fields --

  private RandomAccessFile out;

  // -- Constructor --

  public JPEG2000Writer() {
    super("JPEG-2000", "jp2");
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (image == null) {
      throw new FormatException("Image is null");
    }
    BufferedImage img = null;
    if (cm != null) img = ImageTools.makeBuffered(image, cm);
    else img = ImageTools.makeBuffered(image);

    byte[][] byteData = ImageTools.getPixelBytes(img, false);
    byte[] stream =
      new byte[byteData.length * byteData[0].length];
    int next = 0;
    int rowLen = byteData[0].length / img.getHeight();
    int bpp = rowLen / img.getWidth();
    for (int row=0; row<img.getHeight(); row++) {
      for (int col=0; col<img.getWidth(); col++) {
        for (int c=0; c<byteData.length; c++) {
          System.arraycopy(byteData[c], row*rowLen + col*bpp, stream,
            next, bpp);
          next += bpp;
        }
      }
    }
    int bytesPerPixel =
      FormatTools.getBytesPerPixel(ImageTools.getPixelType(img));

    out = new RandomAccessFile(currentId, "rw");
    byte[] compressedData = new JPEG2000Codec().compress(stream, img.getWidth(),
      img.getHeight(), new int[] {img.getRaster().getNumBands(), bytesPerPixel},
      new Boolean[] {Boolean.TRUE, Boolean.FALSE});
    out.write(compressedData);
    out.close();
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return false; }

  /* @see loci.formats.IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
  }

}
