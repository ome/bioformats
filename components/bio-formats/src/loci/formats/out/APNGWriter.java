//
// APNGWriter.java
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

import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

import loci.common.DataTools;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;

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

  private RandomAccessOutputStream out;
  private int numFrames = 0;
  private long numFramesPointer = 0;
  private int nextSequenceNumber;

  // -- Constructor --

  public APNGWriter() {
    super("Animated PNG", "png");
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (buf == null) {
      throw new FormatException("Byte array is null");
    }
    MetadataRetrieve meta = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(meta, series);
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(meta.getPixelsPixelType(series, 0)));

    boolean indexed = getColorModel() instanceof IndexColorModel;
    int nChannels = meta.getLogicalChannelSamplesPerPixel(series, 0).intValue();
    int width = meta.getPixelsSizeX(series, 0).intValue();
    int height = meta.getPixelsSizeY(series, 0).intValue();

    if (!initialized) {
      out = new RandomAccessOutputStream(currentId);

      // write 8-byte PNG signature
      out.write(PNG_SIG);

      // write IHDR chunk

      out.writeInt(13);
      byte[] b = new byte[17];
      b[0] = 'I';
      b[1] = 'H';
      b[2] = 'D';
      b[3] = 'R';

      DataTools.unpackBytes(width, b, 4, 4, false);
      DataTools.unpackBytes(height, b, 8, 4, false);

      b[12] = (byte) (bytesPerPixel * 8);
      if (indexed) b[13] = (byte) 3;
      else if (nChannels == 1) b[13] = (byte) 0;
      else if (nChannels == 2) b[13] = (byte) 4;
      else if (nChannels == 3) b[13] = (byte) 2;
      else if (nChannels == 4) b[13] = (byte) 6;
      b[14] = (byte) 0;
      b[15] = (byte) 0;
      b[16] = (byte) 0;

      out.write(b);
      out.writeInt(crc(b));

      // write acTL chunk

      out.writeInt(8);
      out.writeBytes("acTL");
      numFramesPointer = out.getFilePointer();
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0); // save a place for the CRC

      // write fcTL chunk
      writeFCTL(width, height);

      // write PLTE chunk, if needed
      writePLTE();

      // write IDAT chunk
      writePixels("IDAT", buf, width, height, nChannels);
      initialized = true;
    }
    else {
      // write fcTL chunk
      writeFCTL(width, height);

      // write fdAT chunk
      writePixels("fdAT", buf, width, height, nChannels);
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

  private void writePLTE() throws IOException {
    if (!(getColorModel() instanceof IndexColorModel)) return;

    IndexColorModel model = (IndexColorModel) getColorModel();
    byte[][] lut = new byte[3][256];
    model.getReds(lut[0]);
    model.getGreens(lut[1]);
    model.getBlues(lut[2]);

    out.writeInt(768);
    byte[] b = new byte[772];
    b[0] = 'P';
    b[1] = 'L';
    b[2] = 'T';
    b[3] = 'E';

    for (int i=0; i<lut[0].length; i++) {
      for (int j=0; j<lut.length; j++) {
        b[i*lut.length + j + 4] = lut[j][i];
      }
    }

    out.write(b);
    out.writeInt(crc(b));
  }

  private void writePixels(String chunk, byte[] stream, int width, int height,
    int sizeC) throws IOException
  {
    ByteArrayOutputStream s = new ByteArrayOutputStream();
    s.write(chunk.getBytes());
    if (chunk.equals("fdAT")) {
      s.write(DataTools.intToBytes(nextSequenceNumber++, false));
    }
    DeflaterOutputStream deflater = new DeflaterOutputStream(s);
    int planeSize = stream.length / sizeC;
    int rowLen = stream.length / height;
    int bytesPerPixel = stream.length / (width * height * sizeC);
    byte[] rowBuf = new byte[rowLen];
    for (int i=0; i<height; i++) {
      deflater.write(0);
      if (interleaved) {
        System.arraycopy(stream, i * rowLen, rowBuf, 0, rowLen);
      }
      else {
        for (int col=0; col<width; col++) {
          for (int c=0; c<sizeC; c++) {
            for (int q=0; q<bytesPerPixel; q++) {
              rowBuf[(col * sizeC + c) * bytesPerPixel + q] =
                stream[c * planeSize + (i * width + col) * bytesPerPixel + q];
            }
          }
        }
      }
      deflater.write(rowBuf);
    }
    deflater.finish();
    byte[] b = s.toByteArray();

    // write chunk length
    out.writeInt(b.length - 4);
    out.write(b);

    // write checksum
    out.writeInt(crc(b));
  }

}
