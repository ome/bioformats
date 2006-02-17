//
// AVIWriter.java
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
import java.io.*;
import java.awt.image.*;
import java.util.Vector;

/**
 * AVIWriter is the file format writer for AVI files.
 *
 * Much of this writer's code was adapted from Wayne Rasband's
 * AVI Movie Writer plugin for ImageJ
 * (available at http://rsb.info.nih.gov/ij/).
 */

public class AVIWriter extends FormatWriter {

  // -- Fields --

  private RandomAccessFile raFile;

  private int planesWritten = 0;

  private int bytesPerPixel;
  private File file;
  private int xDim, yDim, zDim, tDim, xPad;
  private int microSecPerFrame;
  private int[] dcLength;

  // location of file size in bytes not counting first 8 bytes
  private long saveFileSize;

  // location of length of CHUNK with first LIST - not including first 8
  // bytes with LIST and size. JUNK follows the end of this CHUNK
  private long saveLIST1Size;

  private int[] extents;

  // location of length of CHUNK with second LIST - not including first 8
  // bytes with LIST and size. Note that saveLIST1subSize = saveLIST1Size +
  // 76, and that the length size written to saveLIST2Size is 76 less than
  // that written to saveLIST1Size. JUNK follows the end of this CHUNK.
  private long saveLIST1subSize;

  // location of length of strf CHUNK - not including the first 8 bytes with
  // strf and size. strn follows the end of this CHUNK.
  private long savestrfSize;

  private int resXUnit = 0;
  private int resYUnit = 0;
  private float xResol = 0.0f; // in distance per pixel
  private float yResol = 0.0f; // in distance per pixel
  private long biXPelsPerMeter = 0L;
  private long biYPelsPerMeter = 0L;
  private byte[] strnSignature;
  private byte[] text;
  private long savestrnPos;
  private long saveJUNKsignature;
  private int paddingBytes;
  private long saveLIST2Size;
  private byte[] dataSignature;
  private byte[] idx1Signature;
  private Vector savedbLength;
  private long savedcLength[];
  private long idx1Pos;
  private long endPos;
  private long saveidx1Length;
  private int t,z;
  private long savemovi;
  private int xMod;


  // -- Constructor --

  public AVIWriter() {
    super("AVI", "avi");
    setFramesPerSecond(10);
  }

  // -- AVIWriter API methods --

  public void save(String id, Image[] image)
    throws FormatException, IOException
  {
    for (int i=0; i<image.length; i++) {
      if (i == image.length - 1) {
        save(id, image[i], true);
      }
      else {
        save(id, image[i], false);
      }
    }
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

    if (!id.equals(currentId)) {
      currentId = id;
      bytesPerPixel = 1;

      file = new File(id);
      raFile = new RandomAccessFile(file, "rw");

      writeString("RIFF"); // signature
      saveFileSize = raFile.getFilePointer();
      // Bytes 4 thru 7 contain the length of the file. This length does
      // not include bytes 0 thru 7.
      writeInt(0); // for now write 0 in the file size location
      writeString("AVI "); // RIFF type
      // Write the first LIST chunk, which contains
      // information on data decoding
      writeString("LIST"); // CHUNK signature
      // Write the length of the LIST CHUNK not including the first 8 bytes
      // with LIST and size. Note that the end of the LIST CHUNK is followed
      // by JUNK.
      saveLIST1Size = raFile.getFilePointer();
      writeInt(0); // for now write 0 in avih sub-CHUNK size location
      writeString("hdrl"); // CHUNK type
      writeString("avih"); // Write the avih sub-CHUNK

      // Write the length of the avih sub-CHUNK (38H) not including the
      // the first 8 bytes for avihSignature and the length
      writeInt(0x38);

      // dwMicroSecPerFrame - Write the microseconds per frame
      microSecPerFrame = (int) (1.0 / fps * 1.0e6);
      writeInt(microSecPerFrame);

      // Write the maximum data rate of the file in bytes per second
      writeInt(0); // dwMaxBytesPerSec

      writeInt(0); // dwReserved1 - Reserved1 field set to zero
      writeInt(0x10); // dwFlags - just set the bit for AVIF_HASINDEX

      // 10H AVIF_HASINDEX: The AVI file has an idx1 chunk containing
      //   an index at the end of the file. For good performance, all
      //   AVI files should contain an index.
      // 20H AVIF_MUSTUSEINDEX: Index CHUNK, rather than the physical
      // ordering of the chunks in the file, must be used to determine the
      // order of the frames.
      // 100H AVIF_ISINTERLEAVED: Indicates that the AVI file is interleaved.
      //   This is used to read data from a CD-ROM more efficiently.
      // 800H AVIF_TRUSTCKTYPE: USE CKType to find key frames
      // 10000H AVIF_WASCAPTUREFILE: The AVI file is used for capturing
      //   real-time video. Applications should warn the user before
      //   writing over a file with this fla set because the user
      //   probably defragmented this file.
      // 20000H AVIF_COPYRIGHTED: The AVI file contains copyrighted data
      //   and software. When, this flag is used, software should not
      //   permit the data to be duplicated.

      tDim = 1;
      zDim = 1;
      yDim = ((BufferedImage) image).getRaster().getHeight();
      xDim = ((BufferedImage) image).getRaster().getWidth();

      xPad = 0;
      xMod = xDim % 4;
      if (xMod != 0) {
        xPad = 4 - xMod;
        xDim += xPad;
      }

      // dwTotalFrames - total frame number
      writeInt(zDim * tDim);

      // dwInitialFrames -Initial frame for interleaved files.
      // Noninterleaved files should specify 0.
      writeInt(0);

      // dwStreams - number of streams in the file - here 1 video and
      // zero audio.
      writeInt(1);

      // dwSuggestedBufferSize - Suggested buffer size for reading the file.
      // Generally, this size should be large enough to contain the largest
      // chunk in the file.
      writeInt(0);

      writeInt(xDim - xPad); // dwWidth - image width in pixels
      writeInt(yDim); // dwHeight - image height in pixels

      // dwReserved[4] - Microsoft says to set the following 4 values to 0.
      writeInt(0);
      writeInt(0);
      writeInt(0);
      writeInt(0);

      // Write the Stream line header CHUNK
      writeString("LIST");

      // Write the size of the first LIST subCHUNK not including the first 8
      // bytes with LIST and size. Note that saveLIST1subSize = saveLIST1Size +
      // 76, and that the length written to saveLIST1subSize is 76 less than
      // the length written to saveLIST1Size. The end of the first LIST
      // subCHUNK is followed by JUNK.
      saveLIST1subSize = raFile.getFilePointer();

      writeInt(0); // for now write 0 in CHUNK size location
      writeString("strl");   // Write the chunk type
      writeString("strh"); // Write the strh sub-CHUNK
      writeInt(56); // Write the length of the strh sub-CHUNK

      // fccType - Write the type of data stream - here vids for video stream
      writeString("vids");

      // Write DIB for Microsoft Device Independent Bitmap.
      // Note: Unfortunately, at least 3 other four character codes are
      // sometimes used for uncompressed AVI videos: 'RGB ', 'RAW ', 0x00000000
      writeString("DIB ");

      writeInt(0); // dwFlags

      // 0x00000001 AVISF_DISABLED The stram data should be rendered only when
      // explicitly enabled.
      // 0x00010000 AVISF_VIDEO_PALCHANGES Indicates that a palette change is
      // included in the AVI file. This flag warns the playback software that
      // it will need to animate the palette.

      // dwPriority - priority of a stream type. For example, in a file with
      // multiple audio streams, the one with the highest priority might be the
      // default one.
      writeInt(0);

      // dwInitialFrames - Specifies how far audio data is skewed ahead of
      // video frames in interleaved files. Typically, this is about 0.75
      // seconds. In interleaved files specify the number of frames in the file
      // prior to the initial frame of the AVI sequence.
      // Noninterleaved files should use zero.
      writeInt(0);

      // rate/scale = samples/second
      writeInt(1); // dwScale

      //  dwRate - frame rate for video streams
      writeInt(fps);

      writeInt(0); // dwStart - this field is usually set to zero

      // dwLength - playing time of AVI file as defined by scale and rate
      // Set equal to the number of frames
      writeInt(tDim * zDim);

      // dwSuggestedBufferSize - Suggested buffer size for reading the stream.
      // Typically, this contains a value corresponding to the largest chunk
      // in a stream.
      writeInt(0);

      // dwQuality - encoding quality given by an integer between 0 and 10,000.
      // If set to -1, drivers use the default quality value.
      writeInt(-1);

      // dwSampleSize #
      // 0 if the video frames may or may not vary in size
      // If 0, each sample of data(such as a video frame) must be in a separate
      // chunk. If nonzero, then multiple samples of data can be grouped into
      // a single chunk within the file.
      writeInt(0);

      // rcFrame - Specifies the destination rectangle for a text or video
      // stream within the movie rectangle specified by the dwWidth and
      // dwHeight members of the AVI main header structure. The rcFrame member
      // is typically used in support of multiple video streams. Set this
      // rectangle to the coordinates corresponding to the movie rectangle to
      // update the whole movie rectangle. Units for this member are pixels.
      // The upper-left corner of the destination rectangle is relative to the
      // upper-left corner of the movie rectangle.
      writeShort((short) 0); // left
      writeShort((short) 0); // top
      writeShort((short) 0); // right
      writeShort((short) 0); // bottom

      // Write the size of the stream format CHUNK not including the first 8
      // bytes for strf and the size. Note that the end of the stream format
      // CHUNK is followed by strn.
      writeString("strf"); // Write the stream format chunk

      savestrfSize = raFile.getFilePointer();
      writeInt(0); // for now write 0 in the strf CHUNK size location

      // Applications should use this size to determine which BITMAPINFO header
      // structure is being used. This size includes this biSize field.
      writeInt(40); // biSize- Write header size of BITMAPINFO header structure

      writeInt(xDim - xPad);  // biWidth - image width in pixels

      // biHeight - image height in pixels. If height is positive, the bitmap
      // is a bottom up DIB and its origin is in the lower left corner. If
      // height is negative, the bitmap is a top-down DIB and its origin is the
      // upper left corner. This negative sign feature is supported by the
      // Windows Media Player, but it is not supported by PowerPoint.
      writeInt(yDim);

      // biPlanes - number of color planes in which the data is stored
      // This must be set to 1.
      writeShort(1);

      int bitsPerPixel = (bytesPerPixel == 3) ? 24 : 8;

      // biBitCount - number of bits per pixel #
      // 0L for BI_RGB, uncompressed data as bitmap
      writeShort((short) bitsPerPixel);

      //writeInt(bytesPerPixel * xDim * yDim * zDim * tDim); // biSizeImage #
      writeInt(0); // biSizeImage #
      writeInt(0); // biCompression - type of compression used
      writeInt(0); // biXPelsPerMeter - horizontal resolution in pixels
      writeInt(0); // biYPelsPerMeter - vertical resolution in pixels per meter
      if (bitsPerPixel == 8)
        writeInt(256); // biClrUsed
      else
        writeInt(0); // biClrUsed

      // biClrImportant - specifies that the first x colors of the color table
      // are important to the DIB. If the rest of the colors are not available,
      // the image still retains its meaning in an acceptable manner. When this
      // field is set to zero, all the colors are important, or, rather, their
      // relative importance has not been computed.
      writeInt(0);

      // Write the LUTa.getExtents()[1] color table entries here. They are
      // written: blue byte, green byte, red byte, 0 byte
      if (bytesPerPixel == 1) {
        byte[] lutWrite = new byte[4 * 256];
        for (int i=0; i<256; i++) {
          lutWrite[4*i] = (byte) i; // blue
          lutWrite[4*i+1] = (byte) i; // green
          lutWrite[4*i+2] = (byte) i; // red
          lutWrite[4*i+3] = 0;
        }
        raFile.write(lutWrite);
      }

      // Use strn to provide zero terminated text string describing the stream
      savestrnPos = raFile.getFilePointer();
      raFile.seek(savestrfSize);
      writeInt((int)(savestrnPos - (savestrfSize+4)));
      raFile.seek(savestrnPos);
      writeString("strn");
      writeInt(16); // Write the length of the strn sub-CHUNK
      text = new byte[16];
      text[0] = 70; // F
      text[1] = 105; // i
      text[2] = 108; // l
      text[3] = 101; // e
      text[4] = 65; // A
      text[5] = 118; // v
      text[6] = 105; // i
      text[7] = 32; // space
      text[8] = 119; // w
      text[9] = 114; // r
      text[10] = 105; // i
      text[11] = 116; // t
      text[12] = 101; // e
      text[13] = 32; // space
      text[14] = 32; // space
      text[15] = 0; // termination byte
      raFile.write(text);

      // write a JUNK CHUNK for padding
      saveJUNKsignature = raFile.getFilePointer();
      raFile.seek(saveLIST1Size);
      writeInt((int)(saveJUNKsignature - (saveLIST1Size+4)));
      raFile.seek(saveLIST1subSize);
      writeInt((int)(saveJUNKsignature - (saveLIST1subSize+4)));
      raFile.seek(saveJUNKsignature);
      writeString("JUNK");
      paddingBytes = (int)(4084 - (saveJUNKsignature + 8));
      writeInt(paddingBytes);
      for (int i=0; i<(paddingBytes/2); i++) writeShort((short) 0);

      // Write the second LIST chunk, which contains the actual data
      writeString("LIST");

      // Write the length of the LIST CHUNK not including the first 8 bytes
      // with LIST and size. The end of the second LIST CHUNK is followed by
      // idx1.
      saveLIST2Size = raFile.getFilePointer();

      writeInt(0);  // For now write 0
      savemovi = raFile.getFilePointer();
      writeString("movi"); // Write CHUNK type 'movi'
      savedbLength = new Vector();
      savedcLength = new long[tDim * zDim];
      dcLength = new int[tDim * zDim];

      dataSignature = new byte[4];
      dataSignature[0] = 48; // 0
      dataSignature[1] = 48; // 0
      dataSignature[2] = 100; // d
      dataSignature[3] = 98; // b
    }

    // Write the data. Each 3-byte triplet in the bitmap array represents the
    // relative intensities of blue, green, and red, respectively, for a pixel.
    // The color bytes are in reverse order from the Windows convention.
    byte[] buf = new byte[bytesPerPixel * xDim * yDim];

    int width = xDim - xPad;

    raFile.write(dataSignature);
    savedbLength.add(new Long(raFile.getFilePointer()));
    writeInt(bytesPerPixel * xDim * yDim); // Write the data length

    int[][] values = new int[0][0];

    // get pixels

    DataBuffer dbuf = ((BufferedImage) image).getRaster().getDataBuffer();

    if (dbuf instanceof DataBufferByte) {
      // originally 8 bit data
      byte[][] data = ((DataBufferByte) dbuf).getBankData();
      values = new int[data.length][data[0].length];
      for (int i=0; i<data.length; i++) {
        for (int j=0; j<data[i].length; j++) {
          values[i][j] = (int) data[i][j];
        }
      }
    }
    else if (dbuf instanceof DataBufferUShort) {
      // originally 16 bit data
      short[][] data = ((DataBufferUShort) dbuf).getBankData();
      values = new int[data.length][data[0].length];
      for (int i=0; i<data.length; i++) {
        for (int j=0; j<data[i].length; j++) {
          values[i][j] = (int) data[i][j];
        }
      }
    }
    else if (dbuf instanceof DataBufferInt) {
      // originally 32 bit data
      values = ((DataBufferInt) dbuf).getBankData();
    }

    int index = 0;
    int offset = 0;

    for (int y=yDim-1; y>=0; y--) {
      offset = y*width;
      for (int x=0; x<width; x++) {
        buf[index++] = (byte) values[0][offset++];
      }
      for (int i=0; i<xPad; i++) {
        buf[index++ % buf.length] = (byte) 0;
      }
    }

    raFile.write(buf);

    planesWritten++;

    if (last) {
      // Write the idx1 CHUNK
      // Write the 'idx1' signature
      idx1Pos = raFile.getFilePointer();
      raFile.seek(saveLIST2Size);
      writeInt((int)(idx1Pos - (saveLIST2Size + 4)));
      raFile.seek(idx1Pos);
      writeString("idx1");

      // Write the length of the idx1 CHUNK not including the idx1 signature
      // and the 4 length bytes. Write 0 for now.
      saveidx1Length = raFile.getFilePointer();
      writeInt(0);

      for (z=0; z<planesWritten; z++) {
        // In the ckid field write the 4 character code to identify the chunk
        // 00db or 00dc
        raFile.write(dataSignature);
        if (z == 0) writeInt(0x10); // Write the flags - select AVIIF_KEYFRAME
        else writeInt(0x00);

        // AVIIF_KEYFRAME 0x00000010L
        // The flag indicates key frames in the video sequence.
        // Key frames do not need previous video information to be
        // decompressed.
        // AVIIF_NOTIME 0x00000100L The CHUNK does not influence video timing
        // (for example a palette change CHUNK).
        // AVIIF_LIST 0x00000001L Marks a LIST CHUNK.
        // AVIIF_TWOCC 2L
        // AVIIF_COMPUSE 0x0FFF0000L These bits are for compressor use.
        writeInt((int) (((Long)
          savedbLength.get(z)).longValue() - 4 - savemovi));

        // Write the offset (relative to the 'movi' field) to the relevant
        // CHUNK. Write the length of the relevant CHUNK. Note that this length
        // is also written at savedbLength
        writeInt(bytesPerPixel*xDim*yDim);
      }
      endPos = raFile.getFilePointer();
      raFile.seek(saveFileSize);
      writeInt((int)(endPos - (saveFileSize+4)));
      raFile.seek(saveidx1Length);
      writeInt((int)(endPos - (saveidx1Length+4)));
      raFile.close();
    }

  }


  // -- Helper methods --

  private void writeString(String s) throws IOException {
    byte[] bytes =  s.getBytes("UTF-8");
    raFile.write(bytes);
  }

  private void writeInt(int v) throws IOException {
    raFile.write(v & 0xFF);
    raFile.write((v >>>  8) & 0xFF);
    raFile.write((v >>> 16) & 0xFF);
    raFile.write((v >>> 24) & 0xFF);
  }

  private void writeShort(int v) throws IOException {
    raFile.write(v& 0xFF);
    raFile.write((v >>> 8) & 0xFF);
  }

  // -- Main method --

  public static void main(String[] args) throws IOException, FormatException {
    new AVIWriter().testConvert(args);
  }
}
