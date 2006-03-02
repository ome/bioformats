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

  /** Time the file was created. */
  protected int created;

  // -- Constructor --

  public QTWriter() { super("QuickTime", "mov"); }


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
    // also need to check if the width is a multiple of 8
    // if it is, great; if not, we need to pad each scanline with enough
    // bytes to make the width a multiple of 8

    int pad = width % 4;
    pad = (4 - pad) % 4;

    byte[] temp = buf;
    buf = new byte[temp.length + height*pad];

    int newScanline = height - 1;

    for (int oldScanline=0; oldScanline<height; oldScanline++) {
      System.arraycopy(temp, oldScanline*width, buf,
        newScanline*(width+pad), width);


      // add padding bytes

      for (int i=0; i<pad; i++) {
        buf[newScanline*(width+pad) + width + i] = 0;
      }

      newScanline--;
    }

    // invert each pixel
    // this will makes the colors look right in other readers (e.g. xine),
    // but needs to be reversed in QTReader

    for (int i=0; i<buf.length; i++) {
      buf[i] = (byte) (255 - buf[i]);
    }

    if (!id.equals(currentId)) {
      // -- write the header --

      offsets = new Vector();
      currentId = id;
      out = new RandomAccessFile(id, "rw");
      created = (int) System.currentTimeMillis();
      numWritten++;

      // -- write the first header --

      DataTools.writeReverseInt(out, 8);
      DataTools.writeString(out, "wide");

      // -- write the first plane of pixel data (mdat) --

      numBytes = buf.length;
      byteCountOffset = out.getFilePointer();
      DataTools.writeReverseInt(out, numBytes + 8);
      DataTools.writeString(out, "mdat");

      out.write(buf);

      offsets.add(new Integer(16));
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

      offsets.add(new Integer(planeOffset + 16));
      numWritten++;
    }

    if (last) {
      int duration = numWritten * 50;
      int timeScale = 100;

      // -- write moov atom --

      int atomLength = 685 + 8*numWritten;
      DataTools.writeReverseInt(out, atomLength);
      DataTools.writeString(out, "moov");

      // -- write mvhd atom --

      DataTools.writeReverseInt(out, 108);
      DataTools.writeString(out, "mvhd");
      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, created); // creation time
      DataTools.writeReverseInt(out, (int) System.currentTimeMillis());
      DataTools.writeReverseInt(out, timeScale); // time scale
      DataTools.writeReverseInt(out, duration); // duration
      out.write(new byte[] {0, 1, 0, 0});  // preferred rate & volume
      out.write(new byte[] {0, -1, 0, 0, 0, 0, 0, 0, 0, 0}); // reserved

      // 3x3 matrix - unsure of significance

      DataTools.writeReverseInt(out, 1);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 1);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 16384);

      DataTools.writeReverseShort(out, 0); // not sure what this is
      DataTools.writeReverseInt(out, 0); // preview duration
      DataTools.writeReverseInt(out, 0); // preview time
      DataTools.writeReverseInt(out, 0); // poster time
      DataTools.writeReverseInt(out, 0); // selection time
      DataTools.writeReverseInt(out, 0); // selection duration
      DataTools.writeReverseInt(out, 0); // current time
      DataTools.writeReverseInt(out, 2); // next track's id

      // -- write trak atom --

      atomLength -= 116;
      DataTools.writeReverseInt(out, atomLength);
      DataTools.writeString(out, "trak");

      // -- write tkhd atom --

      DataTools.writeReverseInt(out, 92);
      DataTools.writeString(out, "tkhd");
      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 15); // flags

      DataTools.writeReverseInt(out, created); // creation time
      DataTools.writeReverseInt(out, (int) System.currentTimeMillis());
      DataTools.writeReverseInt(out, 1); // track id
      DataTools.writeReverseInt(out, 0); // reserved

      DataTools.writeReverseInt(out, duration); // duration
      DataTools.writeReverseInt(out, 0); // reserved
      DataTools.writeReverseInt(out, 0); // reserved
      DataTools.writeReverseShort(out, 0); // reserved

      DataTools.writeReverseInt(out, 0); // unknown
      // 3x3 matrix - unsure of significance

      DataTools.writeReverseInt(out, 1);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 1);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 0);
      DataTools.writeReverseInt(out, 16384);


      DataTools.writeReverseInt(out, width); // image width
      DataTools.writeReverseInt(out, height); // image height
      DataTools.writeReverseShort(out, 0); // reserved


      // -- write edts atom --

      DataTools.writeReverseInt(out, 36);
      DataTools.writeString(out, "edts");

      // -- write elst atom --

      DataTools.writeReverseInt(out, 28);
      DataTools.writeString(out, "elst");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, 1); // number of entries in the table
      DataTools.writeReverseInt(out, duration); // duration
      DataTools.writeReverseShort(out, 0); // time
      DataTools.writeReverseInt(out, 1); // rate
      DataTools.writeReverseShort(out, 0); // unknown

      // -- write mdia atom --

      atomLength -= 136;
      DataTools.writeReverseInt(out, atomLength);
      DataTools.writeString(out, "mdia");

      // -- write mdhd atom --

      DataTools.writeReverseInt(out, 32);
      DataTools.writeString(out, "mdhd");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, created); // creation time
      DataTools.writeReverseInt(out, (int) System.currentTimeMillis());
      DataTools.writeReverseInt(out, timeScale); // time scale
      DataTools.writeReverseInt(out, duration); // duration
      DataTools.writeReverseShort(out, 0); // language
      DataTools.writeReverseShort(out, 0); // quality

      // -- write hdlr atom --

      DataTools.writeReverseInt(out, 58);
      DataTools.writeString(out, "hdlr");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeString(out, "mhlr");
      DataTools.writeString(out, "vide");
      DataTools.writeString(out, "appl");
      out.write(new byte[] {16, 0, 0, 0, 0, 1, 1, 11, 25});
      DataTools.writeString(out, "Apple Video Media Handler");

      // -- write minf atom --

      atomLength -= 98;
      DataTools.writeReverseInt(out, atomLength);
      DataTools.writeString(out, "minf");

      // -- write vmhd atom --

      DataTools.writeReverseInt(out, 20);
      DataTools.writeString(out, "vmhd");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 1); // flags
      DataTools.writeReverseShort(out, 64); // graphics mode
      DataTools.writeReverseShort(out, 32768);  // opcolor 1
      DataTools.writeReverseShort(out, 32768);  // opcolor 2
      DataTools.writeReverseShort(out, 32768);  // opcolor 3

      // -- write hdlr atom --

      DataTools.writeReverseInt(out, 57);
      DataTools.writeString(out, "hdlr");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeString(out, "dhlr");
      DataTools.writeString(out, "alis");
      DataTools.writeString(out, "appl");
      out.write(new byte[] {16, 0, 0, 1, 0, 1, 1, 31, 24});
      DataTools.writeString(out, "Apple Alias Data Handler");

      // -- write dinf atom --

      DataTools.writeReverseInt(out, 36);
      DataTools.writeString(out, "dinf");

      // -- write dref atom --

      DataTools.writeReverseInt(out, 28);
      DataTools.writeString(out, "dref");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseShort(out, 0); // version 2
      DataTools.writeReverseShort(out, 1); // flags 2
      out.write(new byte[] {0, 0, 0, 12});
      DataTools.writeString(out, "alis");
      DataTools.writeReverseShort(out, 0); // version 3
      DataTools.writeReverseShort(out, 1); // flags 3

      // -- write stbl atom --

      atomLength -= 121;
      DataTools.writeReverseInt(out, atomLength);
      DataTools.writeString(out, "stbl");

      // -- write stsd atom --

      DataTools.writeReverseInt(out, 118);
      DataTools.writeString(out, "stsd");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, 1); // number of entries in the table
      out.write(new byte[] {0, 0, 0, 102});
      DataTools.writeString(out, "raw "); // codec
      out.write(new byte[] {0, 0, 0, 0, 0, 0});  // reserved
      DataTools.writeReverseShort(out, 1); // data reference
      DataTools.writeReverseShort(out, 1); // version
      DataTools.writeReverseShort(out, 1); // revision
      DataTools.writeString(out, "appl");
      DataTools.writeReverseInt(out, 0); // temporal quality
      DataTools.writeReverseInt(out, 768); // spatial quality
      DataTools.writeReverseShort(out, width); // image width
      DataTools.writeReverseShort(out, height); // image height
      out.write(new byte[] {0, 72, 0, 0}); // horizontal dpi
      out.write(new byte[] {0, 72, 0, 0}); // vertical dpi
      DataTools.writeReverseInt(out, 0); // data size
      DataTools.writeReverseShort(out, 1); // frames per sample
      DataTools.writeReverseShort(out, 12); // length of compressor name
      DataTools.writeString(out, "Uncompressed"); // compressor name
      DataTools.writeReverseInt(out, 40); // unknown
      DataTools.writeReverseInt(out, 40); // unknown
      DataTools.writeReverseInt(out, 40); // unknown
      DataTools.writeReverseInt(out, 40); // unknown
      DataTools.writeReverseInt(out, 40); // unknown
      DataTools.writeReverseShort(out, 40); // bits per pixel
      DataTools.writeReverseInt(out, 65535); // ctab ID
      out.write(new byte[] {12, 103, 97, 108}); // gamma
      out.write(new byte[] {97, 1, -52, -52, 0, 0, 0, 0}); // unknown

      // -- write stts atom --

      DataTools.writeReverseInt(out, 24);
      DataTools.writeString(out, "stts");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, 1); // number of entries in the table
      DataTools.writeReverseInt(out, numWritten); // number of planes
      DataTools.writeReverseInt(out, 50); // duration

      // -- write stsc atom --

      DataTools.writeReverseInt(out, 28);
      DataTools.writeString(out, "stsc");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, 1); // number of entries in the table
      DataTools.writeReverseInt(out, 1); // chunk
      DataTools.writeReverseInt(out, 1); // samples
      DataTools.writeReverseInt(out, 1); // id

      // -- write stsz atom --

      DataTools.writeReverseInt(out, 20 + 4*numWritten);
      DataTools.writeString(out, "stsz");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, 0); // sample size
      DataTools.writeReverseInt(out, numWritten); // number of planes
      for (int i=0; i<numWritten; i++) {
        DataTools.writeReverseInt(out, height*(width+pad)); // sample size
      }

      // -- write stco atom --

      DataTools.writeReverseInt(out, 16 + 4*numWritten);
      DataTools.writeString(out, "stco");

      DataTools.writeReverseShort(out, 0); // version
      DataTools.writeReverseShort(out, 0); // flags
      DataTools.writeReverseInt(out, numWritten); // number of planes
      for (int i=0; i<numWritten; i++) {
        // write the plane offset
        DataTools.writeReverseInt(out, ((Integer) offsets.get(i)).intValue());
      }

      out.close();
    }
  }


  // -- Main method --

  public static void main(String[] args) throws IOException, FormatException {
    new QTWriter().testConvert(args);
  }

}
