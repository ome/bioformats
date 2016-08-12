/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.out;

import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.meta.MetadataRetrieve;

/**
 * APNGWriter is the file format writer for PNG and APNG files.
 */
public class APNGWriter extends FormatWriter {

  // -- Constants --

  private static final byte[] PNG_SIG = new byte[] {
    (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
  };

  // -- Fields --

  private int numFrames = 0;
  private long numFramesPointer = 0;
  private int nextSequenceNumber;
  private boolean littleEndian;
  private long footerPointer = 0;

  // -- Constructor --

  public APNGWriter() {
    super("Animated PNG", "png");
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException(
        "APNGWriter does not yet support saving image tiles.");
    }
    MetadataRetrieve meta = getMetadataRetrieve();

    int width = meta.getPixelsSizeX(series).getValue().intValue();
    int height = meta.getPixelsSizeY(series).getValue().intValue();

    out.seek(footerPointer);
    if (!initialized[series][no]) {
      writeFCTL(width, height);
      if (numFrames == 0) writePLTE();
      initialized[series][no] = true;
    }

    writePixels(numFrames == 0 ? "IDAT" : "fdAT", buf, x, y, w, h);
    numFrames++;
    writeFooter();
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16};
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    if (out.length() == 0) {
      numFrames = 0;

      MetadataRetrieve r = getMetadataRetrieve();
      int width = r.getPixelsSizeX(series).getValue().intValue();
      int height = r.getPixelsSizeY(series).getValue().intValue();
      int bytesPerPixel =
        FormatTools.getBytesPerPixel(r.getPixelsType(series).toString());
      int nChannels = getSamplesPerPixel();
      boolean indexed =
        getColorModel() != null && (getColorModel() instanceof IndexColorModel);
      if (r.getPixelsBigEndian(series) != null) {
        littleEndian = !r.getPixelsBigEndian(series).booleanValue();
      }
      else if (r.getPixelsBinDataCount(series) == 0) {
        littleEndian = !r.getPixelsBinDataBigEndian(series, 0).booleanValue();
      }

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
      footerPointer = out.getFilePointer();
    }
    else {
      numFramesPointer = PNG_SIG.length + 33;
      RandomAccessInputStream in = new RandomAccessInputStream(id);
      in.order(littleEndian);
      in.seek(8);
      while (in.getFilePointer() < in.length()) {
        int length = in.readInt();
        String type = in.readString(4);
        if (type.equals("fcTL") || type.equals("fdAT")) {
          nextSequenceNumber = in.readInt() + 1;
          length -= 4;
        }
        in.skipBytes(length + 4);
      }
      in.seek(numFramesPointer);
      numFrames = in.readInt();
      in.close();
      footerPointer = out.length() - 12;
    }
    if (numFrames == 0) {
      nextSequenceNumber = 0;
    }
  }

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    super.close();

    numFrames = 0;
    numFramesPointer = 0;
    nextSequenceNumber = 0;
    littleEndian = false;
    footerPointer = 0;
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

  private void writePixels(String chunk, byte[] stream, int x, int y,
    int width, int height) throws FormatException, IOException
  {
    MetadataRetrieve r = getMetadataRetrieve();
    int sizeC = getSamplesPerPixel();
    String type = r.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);
    boolean signed = FormatTools.isSigned(pixelType);

    if (!isFullPlane(x, y, width, height)) {
      throw new FormatException("APNGWriter does not support writing tiles.");
    }

    ByteArrayOutputStream s = new ByteArrayOutputStream();
    s.write(chunk.getBytes(Constants.ENCODING));
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
        if (littleEndian) {
          for (int col=0; col<width*sizeC; col++) {
            int offset = (i * sizeC * width + col) * bytesPerPixel;
            int pixel = DataTools.bytesToInt(stream, offset,
              bytesPerPixel, littleEndian);
            DataTools.unpackBytes(pixel, rowBuf, col * bytesPerPixel,
              bytesPerPixel, false);
          }
        }
        else System.arraycopy(stream, i * rowLen, rowBuf, 0, rowLen);
      }
      else {
        int max = (int) Math.pow(2, bytesPerPixel * 8 - 1);
        for (int col=0; col<width; col++) {
          for (int c=0; c<sizeC; c++) {
            int offset = c * planeSize + (i * width + col) * bytesPerPixel;
            int pixel = DataTools.bytesToInt(stream, offset, bytesPerPixel,
              littleEndian);
            if (signed) {
              if (pixel < max) pixel += max;
              else pixel -= max;
            }
            int output = (col * sizeC + c) * bytesPerPixel;
            DataTools.unpackBytes(pixel, rowBuf, output, bytesPerPixel, false);
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

  private void writeFooter() throws IOException {
    footerPointer = out.getFilePointer();
    // write IEND chunk

    out.writeInt(0);
    out.writeBytes("IEND");
    out.writeInt(crc("IEND".getBytes(Constants.ENCODING)));

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
