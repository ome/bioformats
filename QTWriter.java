//
// QTWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.util.Vector;


/**
 * QTWriter is the file format writer for uncompressed QuickTime movie files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class QTWriter extends FormatWriter {

  /** Current file. */
  protected RandomAccessFile out;

  /** Number of planes written. */
  protected int numWritten;

  /** Seek to this offset to update the total number of pixel bytes. */
  protected long byteCountOffset;

  /** Total number of pixel bytes. */
  protected int numBytes;

  /** Vector of plane offsets. */
  protected Vector offsets;

  // -- Constructor --

  public QTWriter() {
    super("QTWriter", "mov");
  }

  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    if (image == null) {
      throw new FormatException("Image is null");
    }

    BufferedImage img = ImageTools.makeImage(image);

    // get the width and height of the image
    int width = img.getWidth();
    int height = img.getHeight();

    // retrieve pixel data for this plane
    byte[] buf = ImageTools.getPixels(img, width, height);

    // reorder the scanlines
    // Why do we need to do this?  Don't ask me...QuickTime has *problems*

    byte[] temp = buf;
    buf = new byte[temp.length];

    int newScanline = height - 1;

    for (int oldScanline=0; oldScanline < height; oldScanline++) {
      System.arraycopy(temp, oldScanline*width, buf,  newScanline*width, width);
      newScanline--;
    }

    if (!id.equals(currentId)) {
      // -- write the header --

      offsets = new Vector();
      currentId = id;
      out = new RandomAccessFile(id, "rw");
      numWritten++;

      DataTools.writeReverseShort(out, 0);
      DataTools.writeReverseShort(out, 8);
      DataTools.writeString(out, "wide");

      // -- write the pixel data (mdat) --

      numBytes = buf.length;
      byteCountOffset = out.getFilePointer();
      DataTools.writeReverseInt(out, numBytes + 8);
      DataTools.writeString(out, "mdat");
      out.write(buf);
      offsets.add(new Integer(0));
    }
    else {
      // update the number of pixel bytes written
      int planeOffset = numBytes;
      numBytes += buf.length;
      out.seek(byteCountOffset);
      DataTools.writeReverseInt(out, numBytes + 8);

      // write this plane's pixel data
      out.seek(out.length());
      out.write(buf);

      offsets.add(new Integer(planeOffset));
      numWritten++;
    }

    if (last) {
      // -- write a stub 'moov' atom for compatibility with other readers --

      DataTools.writeReverseInt(out, 8);
      DataTools.writeString(out, "moov");

      // -- write the dimension info (tkhd) --

      DataTools.writeReverseInt(out, 94);
      DataTools.writeString(out, "tkhd");
      // write 74 bytes of 0
      for (int i=0; i<70; i+=4) {
        DataTools.writeReverseInt(out, 0);
      }
      DataTools.writeReverseShort(out, 0);
      // write the image width
      DataTools.writeReverseInt(out, width);
      // write the image height
      DataTools.writeReverseInt(out, height);
      // write another 4 bytes of 0
      DataTools.writeReverseInt(out, 0);

      // -- write the number of planes (stsz) --

      DataTools.writeReverseInt(out, 20);
      DataTools.writeString(out, "stsz");

      // write 4 bytes of 0
      DataTools.writeReverseInt(out, 0);
      // write the raw size of a plane
      DataTools.writeReverseInt(out, buf.length);
      // write the number of planes
      DataTools.writeReverseInt(out, numWritten);

      // -- write the plane offset (stco) --

      DataTools.writeReverseInt(out, 16 + 4*numWritten);
      DataTools.writeString(out, "stco");
      // write four bytes of 0
      DataTools.writeReverseInt(out, 0);
      // write the number of planes
      DataTools.writeReverseInt(out, numWritten);
      // write the offsets

      for (int i=0; i<offsets.size(); i++) {
        DataTools.writeReverseInt(out, ((Integer) offsets.get(i)).intValue());
      }

      // -- write the codec info (stsd) --

      DataTools.writeReverseInt(out, 100);
      DataTools.writeString(out, "stsd");

      // write 12 bytes of 0
      for (int i=0; i<12; i+=4) {
        DataTools.writeReverseInt(out, 0);
      }

      DataTools.writeString(out, "raw ");  // codec -- uncompressed
      // write 74 bytes of 0
      for (int i=0; i<70; i+=4) {
        DataTools.writeReverseInt(out, 0);
      }
      DataTools.writeReverseShort(out, 0);

      // write the number of bits per pixel
      DataTools.writeReverseShort(out, 8);

      out.close();
    }
  }


  // -- Main method --

  public static void main(String[] args) throws IOException, FormatException {
    new QTWriter().testConvert(args);
  }
}
