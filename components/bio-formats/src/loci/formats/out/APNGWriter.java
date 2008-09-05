//
// APNGWriter.java
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
import java.util.zip.*;
import loci.formats.*;

/**
 * APNGWriter is the file format writer for PNG and APNG files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/APNGWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/APNGWriter.java">SVN</a></dd></dl>
 */
public class APNGWriter extends FormatWriter {

  // -- Constants --

  private static final byte[] PNG_SIG = new byte[] {
    (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
  };

  // -- Fields --

  private RandomAccessFile out;
  private int numFrames = 0;
  private long numFramesPointer = 0;
  private int nextSequenceNumber;

  // -- Constructor --

  public APNGWriter() {
    super("Animated PNG", "png");
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
      new byte[byteData.length * byteData[0].length + img.getHeight()];
    int next = 0;
    int rowLen = byteData[0].length / img.getHeight();
    int bpp = rowLen / img.getWidth();
    for (int row=0; row<img.getHeight(); row++) {
      stream[next++] = 0;
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

    if (!initialized) {
      out = new RandomAccessFile(currentId, "rw");

      // write 8-byte PNG signature
      out.write(PNG_SIG);

      // write IHDR chunk

      out.writeInt(13);
      byte[] buf = new byte[17];
      buf[0] = 'I';
      buf[1] = 'H';
      buf[2] = 'D';
      buf[3] = 'R';
      DataTools.unpackBytes(img.getWidth(), buf, 4, 4, false);
      DataTools.unpackBytes(img.getHeight(), buf, 8, 4, false);
      buf[12] = (byte) (bytesPerPixel * 8);
      if (byteData.length == 1) buf[13] = (byte) 0;
      else if (byteData.length == 2) buf[13] = (byte) 4;
      else if (byteData.length == 3) buf[13] = (byte) 2;
      else if (byteData.length == 4) buf[13] = (byte) 6;
      buf[14] = (byte) 0;
      buf[15] = (byte) 0;
      buf[16] = (byte) 0;

      out.write(buf);
      out.writeInt(crc(buf));

      // write acTL chunk

      out.writeInt(8);
      out.writeBytes("acTL");
      numFramesPointer = out.getFilePointer();
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0); // save a place for the CRC

      // write fcTL chunk
      writeFCTL(img.getWidth(), img.getHeight());

      // write IDAT chunk

      writePixels("IDAT", stream);
      initialized = true;
    }
    else {
      // write fcTL chunk
      writeFCTL(img.getWidth(), img.getHeight());

      // write fdAT chunk
      writePixels("fdAT", stream);
    }

    numFrames++;

    if (last) {
      // write IEND chunk
      out.writeInt(0);
      out.writeBytes("IEND");
      out.writeInt(crc("IEND".getBytes()));

      // update frame count
      out.seek(numFramesPointer);
      out.writeInt(numFrames);
      out.skipBytes(4);
      byte[] b = new byte[12];
      b[0] = 'a';
      b[1] = 'c';
      b[2] = 'T';
      b[3] = 'L';
      DataTools.unpackBytes(numFrames, b, 4, 4, false);
      DataTools.unpackBytes(0, b, 8, 4, false);
      out.writeInt(crc(b));
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

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
    initialized = false;
    numFrames = 0;
  }

  // -- Helper methods --

  private int crc(byte[] buf) {
    return crc(buf, 0, buf.length);
  }

  private int crc(byte[] buf, int off, int len) {
    CRC32 crc = new CRC32();
    crc.update(buf, off, len);
    return (int) crc.getValue();
  }

  private void writeFCTL(int width, int height) throws IOException {
    out.writeInt(26);
    byte[] b = new byte[30];
    b[0] = 'f';
    b[1] = 'c';
    b[2] = 'T';
    b[3] = 'L';

    DataTools.unpackBytes(nextSequenceNumber++, b, 4, 4, false);
    DataTools.unpackBytes(width, b, 8, 4, false);
    DataTools.unpackBytes(height, b, 12, 4, false);
    DataTools.unpackBytes(0, b, 16, 4, false);
    DataTools.unpackBytes(0, b, 20, 4, false);
    DataTools.unpackBytes(1, b, 24, 2, false);
    DataTools.unpackBytes(fps, b, 26, 2, false);
    b[28] = (byte) 1;
    b[29] = (byte) 0;

    out.write(b);
    out.writeInt(crc(b));
  }

  private void writePixels(String chunk, byte[] stream) throws IOException {
    Deflater deflater = new Deflater();
    deflater.setInput(stream);
    deflater.finish();
    int extra = chunk.equals("fdAT") ? 4 : 0;
    byte[] tmp = new byte[stream.length + 4 + extra];
    byte[] chunkBytes = chunk.getBytes();
    System.arraycopy(chunkBytes, 0, tmp, 0, 4);
    if (chunk.equals("fdAT")) {
      DataTools.unpackBytes(nextSequenceNumber++, tmp, 4, 4, false);
    }
    int nBytes = deflater.deflate(tmp, extra + 4, stream.length);

    // write chunk length
    out.writeInt(nBytes + extra);
    out.write(tmp, 0, nBytes + extra + 4);

    // write checksum
    out.writeInt(crc(tmp, 0, nBytes + extra + 4));
  }

}
