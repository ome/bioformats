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
import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.meta.MetadataRetrieve;

/**
 * AVIWriter is the file format writer for AVI files.
 *
 * Much of this writer's code was adapted from Wayne Rasband's
 * AVI Movie Writer plugin for ImageJ
 * (available at http://rsb.info.nih.gov/ij/).
 */
public class AVIWriter extends FormatWriter {

  // -- Constants --

  private static final long SAVE_MOVI = 4092;
  private static final long SAVE_FILE_SIZE = 4;

  // location of length of strf CHUNK - not including the first 8 bytes with
  // strf and size. strn follows the end of this CHUNK.
  private static final long SAVE_STRF_SIZE = 168;

  private static final long SAVE_STRN_POS = SAVE_STRF_SIZE + 1068;
  private static final long SAVE_JUNK_SIG = SAVE_STRN_POS + 24;

  // location of length of CHUNK with first LIST - not including first 8
  // bytes with LIST and size. JUNK follows the end of this CHUNK
  private static final long SAVE_LIST1_SIZE = 16;

  // location of length of CHUNK with second LIST - not including first 8
  // bytes with LIST and size. Note that saveLIST1subSize = saveLIST1Size +
  // 76, and that the length size written to saveLIST2Size is 76 less than
  // that written to saveLIST1Size. JUNK follows the end of this CHUNK.
  private static final long SAVE_LIST1_SUBSIZE = 92;

  private static final long FRAME_OFFSET = 48;
  private static final long FRAME_OFFSET_2 = 140;
  private static final long PADDING_BYTES = 4076 - SAVE_JUNK_SIG;
  private static final long SAVE_LIST2_SIZE = 4088;
  private static final String DATA_SIGNATURE = "00db";

  // -- Fields --

  private int planesWritten = 0;

  private int bytesPerPixel;
  private int xDim, yDim, zDim, tDim, xPad;
  private int microSecPerFrame;

  private Vector<Long> savedbLength;
  private long idx1Pos;
  private long endPos;
  private long saveidx1Length;

  // -- Constructor --

  public AVIWriter() { super("Audio Video Interleave", "avi"); }

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
        "AVIWriter does not yet support saving image tiles.");
    }
    int nChannels = getSamplesPerPixel();

    if (!initialized[series][no]) {
      initialized[series][no] = true;
    }

    // Write the data. Each 3-byte triplet in the bitmap array represents the
    // relative intensities of blue, green, and red, respectively, for a pixel.
    // The color bytes are in reverse order from the Windows convention.

    int width = xDim - xPad;
    int height = buf.length / (width * bytesPerPixel);

    out.seek(idx1Pos);
    out.writeBytes(DATA_SIGNATURE);
    savedbLength.add(new Long(out.getFilePointer()));

    // Write the data length
    out.writeInt(bytesPerPixel * xDim * yDim);

    int rowPad = xPad * bytesPerPixel;

    byte[] rowBuffer = new byte[width * bytesPerPixel + rowPad];

    for (int row=height-1; row>=0; row--) {
      for (int col=0; col<width; col++) {
        int offset = row * width + col;
        if (interleaved) offset *= nChannels;
        byte r = buf[offset];
        if (nChannels > 1) {
          byte g = buf[offset + (interleaved ? 1 : width * height)];
          byte b = 0;
          if (nChannels > 2) {
            b = buf[offset + (interleaved ? 2 : 2 * width * height)];
          }

          rowBuffer[col * bytesPerPixel] = b;
          rowBuffer[col * bytesPerPixel + 1] = g;
        }
        rowBuffer[col * bytesPerPixel + bytesPerPixel - 1] = r;
      }
      out.write(rowBuffer);
    }

    planesWritten++;

    // Write the idx1 CHUNK
    // Write the 'idx1' signature
    idx1Pos = out.getFilePointer();
    out.seek(SAVE_LIST2_SIZE);
    out.writeInt((int) (idx1Pos - (SAVE_LIST2_SIZE + 4)));

    out.seek(idx1Pos);
    out.writeBytes("idx1");

    saveidx1Length = out.getFilePointer();

    // Write the length of the idx1 CHUNK not including the idx1 signature
    out.writeInt(4 + (planesWritten*16));

    for (int z=0; z<planesWritten; z++) {
      // In the ckid field write the 4 character code to identify the chunk
      // 00db or 00dc
      out.writeBytes(DATA_SIGNATURE);
      // Write the flags - select AVIIF_KEYFRAME
      if (z == 0) out.writeInt(0x10);
      else out.writeInt(0x00);

      // AVIIF_KEYFRAME 0x00000010L
      // The flag indicates key frames in the video sequence.
      // Key frames do not need previous video information to be
      // decompressed.
      // AVIIF_NOTIME 0x00000100L The CHUNK does not influence video timing
      // (for example a palette change CHUNK).
      // AVIIF_LIST 0x00000001L Marks a LIST CHUNK.
      // AVIIF_TWOCC 2L
      // AVIIF_COMPUSE 0x0FFF0000L These bits are for compressor use.
      out.writeInt((int) (savedbLength.get(z) - 4 - SAVE_MOVI));

      // Write the offset (relative to the 'movi' field) to the relevant
      // CHUNK. Write the length of the relevant CHUNK. Note that this length
      // is also written at savedbLength
      out.writeInt(bytesPerPixel*xDim*yDim);
    }
    endPos = out.getFilePointer();
    out.seek(SAVE_FILE_SIZE);
    out.writeInt((int) (endPos - (SAVE_FILE_SIZE + 4)));

    out.seek(saveidx1Length);
    out.writeInt((int) (endPos - (saveidx1Length + 4)));

    // write the total number of planes
    out.seek(FRAME_OFFSET);
    out.writeInt(planesWritten);
    out.seek(FRAME_OFFSET_2);
    out.writeInt(planesWritten);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.UINT8};
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    super.close();
    planesWritten = 0;
    bytesPerPixel = 0;
    xDim = yDim = zDim = tDim = xPad = 0;
    microSecPerFrame = 0;
    savedbLength = null;
    idx1Pos = 0;
    endPos = 0;
    saveidx1Length = 0;
  }

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    savedbLength = new Vector<Long>();

    if (out.length() > 0) {
      RandomAccessInputStream in = new RandomAccessInputStream(currentId);
      in.order(true);
      in.seek(FRAME_OFFSET);
      planesWritten = in.readInt();

      in.seek(SAVE_FILE_SIZE);
      endPos = in.readInt() + SAVE_FILE_SIZE + 4;

      in.seek(SAVE_LIST2_SIZE);
      idx1Pos = in.readInt() + SAVE_LIST2_SIZE + 4;
      saveidx1Length = idx1Pos + 4;

      if (planesWritten > 0) in.seek(saveidx1Length + 4);
      for (int z=0; z<planesWritten; z++) {
        in.skipBytes(8);
        savedbLength.add(in.readInt() + 4 + SAVE_MOVI);
        in.skipBytes(4);
      }
      in.close();
      out.seek(idx1Pos);
    }
    else {
      planesWritten = 0;
    }

    out.order(true);

    MetadataRetrieve meta = getMetadataRetrieve();
    tDim = meta.getPixelsSizeZ(series).getValue().intValue();
    zDim = meta.getPixelsSizeT(series).getValue().intValue();
    yDim = meta.getPixelsSizeY(series).getValue().intValue();
    xDim = meta.getPixelsSizeX(series).getValue().intValue();
    String type = meta.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);
    bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    bytesPerPixel *= getSamplesPerPixel();

    xPad = 0;
    int xMod = xDim % 4;
    if (xMod != 0) {
      xPad = 4 - xMod;
      xDim += xPad;
    }

    byte[][] lut = null;

    if (getColorModel() instanceof IndexColorModel) {
      lut = new byte[4][256];
      IndexColorModel model = (IndexColorModel) getColorModel();
      model.getReds(lut[0]);
      model.getGreens(lut[1]);
      model.getBlues(lut[2]);
      model.getAlphas(lut[3]);
    }

    if (out.length() == 0) {
      out.writeBytes("RIFF"); // signature
      // Bytes 4 thru 7 contain the length of the file. This length does
      // not include bytes 0 thru 7.
      out.writeInt(0); // for now write 0 for size
      out.writeBytes("AVI "); // RIFF type
      // Write the first LIST chunk, which contains
      // information on data decoding
      out.writeBytes("LIST"); // CHUNK signature
      // Write the length of the LIST CHUNK not including the first 8 bytes
      // with LIST and size. Note that the end of the LIST CHUNK is followed
      // by JUNK.
      out.writeInt((bytesPerPixel == 1) ? 1240 : 216);
      out.writeBytes("hdrl"); // CHUNK type
      out.writeBytes("avih"); // Write the avih sub-CHUNK

      // Write the length of the avih sub-CHUNK (38H) not including the
      // the first 8 bytes for avihSignature and the length
      out.writeInt(0x38);

      // dwMicroSecPerFrame - Write the microseconds per frame
      microSecPerFrame = (int) (1.0 / fps * 1.0e6);
      out.writeInt(microSecPerFrame);

      // Write the maximum data rate of the file in bytes per second
      out.writeInt(0); // dwMaxBytesPerSec

      out.writeInt(0); // dwReserved1 - set to 0
      // dwFlags - just set the bit for AVIF_HASINDEX
      out.writeInt(0x10);

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

      // dwTotalFrames - total frame number
      out.writeInt(0);

      // dwInitialFrames -Initial frame for interleaved files.
      // Noninterleaved files should specify 0.
      out.writeInt(0);

      // dwStreams - number of streams in the file - here 1 video and
      // zero audio.
      out.writeInt(1);

      // dwSuggestedBufferSize - Suggested buffer size for reading the file.
      // Generally, this size should be large enough to contain the largest
      // chunk in the file.
      out.writeInt(0);

      // dwWidth - image width in pixels
      out.writeInt(xDim - xPad);
      out.writeInt(yDim); // dwHeight - height in pixels

      // dwReserved[4] - Microsoft says to set the following 4 values to 0.
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);

      // Write the Stream line header CHUNK
      out.writeBytes("LIST");

      // Write the size of the first LIST subCHUNK not including the first 8
      // bytes with LIST and size. Note that saveLIST1subSize = saveLIST1Size
      // + 76, and that the length written to saveLIST1subSize is 76 less than
      // the length written to saveLIST1Size. The end of the first LIST
      // subCHUNK is followed by JUNK.

      out.writeInt((bytesPerPixel == 1) ? 1164 : 140);
      out.writeBytes("strl");   // Write the chunk type
      out.writeBytes("strh"); // Write the strh sub-CHUNK
      out.writeInt(56); // Write length of strh sub-CHUNK

      // fccType - Write the type of data stream - here vids for video stream
      out.writeBytes("vids");

      // Write DIB for Microsoft Device Independent Bitmap.
      // Note: Unfortunately, at least 3 other four character codes are
      // sometimes used for uncompressed AVI videos: 'RGB ', 'RAW ',
      // 0x00000000
      out.writeBytes("DIB ");

      out.writeInt(0); // dwFlags

      // 0x00000001 AVISF_DISABLED The stram data should be rendered only when
      // explicitly enabled.
      // 0x00010000 AVISF_VIDEO_PALCHANGES Indicates that a palette change is
      // included in the AVI file. This flag warns the playback software that
      // it will need to animate the palette.

      // dwPriority - priority of a stream type. For example, in a file with
      // multiple audio streams, the one with the highest priority might be
      // the default one.
      out.writeInt(0);

      // dwInitialFrames - Specifies how far audio data is skewed ahead of
      // video frames in interleaved files. Typically, this is about 0.75
      // seconds. In interleaved files specify the number of frames in the
      // file prior to the initial frame of the AVI sequence.
      // Noninterleaved files should use zero.
      out.writeInt(0);

      // rate/scale = samples/second
      out.writeInt(1); // dwScale

      //  dwRate - frame rate for video streams
      out.writeInt(fps);

      // dwStart - this field is usually set to zero
      out.writeInt(0);

      // dwLength - playing time of AVI file as defined by scale and rate
      // Set equal to the number of frames
      out.writeInt(tDim * zDim);

      // dwSuggestedBufferSize - Suggested buffer size for reading the stream.
      // Typically, this contains a value corresponding to the largest chunk
      // in a stream.
      out.writeInt(0);

      // dwQuality - encoding quality given by an integer between 0 and
      // 10,000. If set to -1, drivers use the default quality value.
      out.writeInt(-1);

      // dwSampleSize #
      // 0 if the video frames may or may not vary in size
      // If 0, each sample of data(such as a video frame) must be in a
      // separate chunk. If nonzero, then multiple samples of data can be
      // grouped into a single chunk within the file.
      out.writeInt(0);

      // rcFrame - Specifies the destination rectangle for a text or video
      // stream within the movie rectangle specified by the dwWidth and
      // dwHeight members of the AVI main header structure. The rcFrame member
      // is typically used in support of multiple video streams. Set this
      // rectangle to the coordinates corresponding to the movie rectangle to
      // update the whole movie rectangle. Units for this member are pixels.
      // The upper-left corner of the destination rectangle is relative to the
      // upper-left corner of the movie rectangle.
      out.writeShort((short) 0); // left
      out.writeShort((short) 0); // top
      out.writeShort((short) 0); // right
      out.writeShort((short) 0); // bottom

      // Write the size of the stream format CHUNK not including the first 8
      // bytes for strf and the size. Note that the end of the stream format
      // CHUNK is followed by strn.
      out.writeBytes("strf"); // Write the stream format chunk

      // write the strf CHUNK size
      out.writeInt((bytesPerPixel == 1) ? 1068 : 44);

      // Applications should use this size to determine which BITMAPINFO
      // header structure is being used. This size includes this biSize field.
      // biSize- Write header size of BITMAPINFO header structure

      out.writeInt(40);

      // biWidth - image width in pixels
      out.writeInt(xDim);

      // biHeight - image height in pixels. If height is positive, the bitmap
      // is a bottom up DIB and its origin is in the lower left corner. If
      // height is negative, the bitmap is a top-down DIB and its origin is
      // the upper left corner. This negative sign feature is supported by the
      // Windows Media Player, but it is not supported by PowerPoint.
      out.writeInt(yDim);

      // biPlanes - number of color planes in which the data is stored
      // This must be set to 1.
      out.writeShort(1);

      int bitsPerPixel = (bytesPerPixel == 3) ? 24 : 8;

      // biBitCount - number of bits per pixel #
      // 0L for BI_RGB, uncompressed data as bitmap
      out.writeShort((short) bitsPerPixel);

      out.writeInt(0); // biSizeImage #
      out.writeInt(0); // biCompression - compression type
      // biXPelsPerMeter - horizontal resolution in pixels
      out.writeInt(0);
      // biYPelsPerMeter - vertical resolution in pixels per meter
      out.writeInt(0);

      int nColors = 256;
      out.writeInt(nColors);

      // biClrImportant - specifies that the first x colors of the color table
      // are important to the DIB. If the rest of the colors are not
      // available, the image still retains its meaning in an acceptable
      // manner. When this field is set to zero, all the colors are important,
      // or, rather, their relative importance has not been computed.
      out.writeInt(0);

      // Write the LUTa.getExtents()[1] color table entries here. They are
      // written: blue byte, green byte, red byte, 0 byte
      if (bytesPerPixel == 1) {
        if (lut != null) {
          for (int i=0; i<256; i++) {
            out.write(lut[2][i]);
            out.write(lut[1][i]);
            out.write(lut[0][i]);
            out.write(lut[3][i]);
          }
        }
        else {
          byte[] lutWrite = new byte[4 * 256];
          for (int i=0; i<256; i++) {
            lutWrite[4*i] = (byte) i; // blue
            lutWrite[4*i+1] = (byte) i; // green
            lutWrite[4*i+2] = (byte) i; // red
            lutWrite[4*i+3] = 0;
          }
          out.write(lutWrite);
        }
      }

      out.seek(SAVE_STRF_SIZE);
      out.writeInt((int) (SAVE_STRN_POS - (SAVE_STRF_SIZE + 4)));
      out.seek(SAVE_STRN_POS);

      // Use strn to provide zero terminated text string describing the stream
      out.writeBytes("strn");
      out.writeInt(16); // Write length of strn sub-CHUNK
      out.writeBytes("FileAVI write  ");

      out.seek(SAVE_LIST1_SIZE);
      out.writeInt((int) (SAVE_JUNK_SIG - (SAVE_LIST1_SIZE + 4)));
      out.seek(SAVE_LIST1_SUBSIZE);
      out.writeInt((int) (SAVE_JUNK_SIG - (SAVE_LIST1_SUBSIZE + 4)));
      out.seek(SAVE_JUNK_SIG);

      // write a JUNK CHUNK for padding
      out.writeBytes("JUNK");
      out.writeInt((int) PADDING_BYTES);
      for (int i=0; i<PADDING_BYTES/2; i++) {
        out.writeShort((short) 0);
      }

      // Write the second LIST chunk, which contains the actual data
      out.writeBytes("LIST");

      out.writeInt(4);  // For now write 0
      out.writeBytes("movi"); // Write CHUNK type 'movi'
      idx1Pos = out.getFilePointer();
    }
  }

}
