/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.codec.CompressionType;
import loci.formats.gui.LegacyQTTools;
import loci.formats.meta.MetadataRetrieve;

/**
 * QTWriter is the file format writer for uncompressed QuickTime movie files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */

public class QTWriter extends FormatWriter {

  // -- Constants --

  // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
  /** Value indicating Motion JPEG-B codec. */
  public static final int CODEC_MOTION_JPEG_B = 1835692130;

  /** Value indicating Cinepak codec. */
  public static final int CODEC_CINEPAK = 1668704612;

  /** Value indicating Animation codec. */
  public static final int CODEC_ANIMATION = 1919706400;

  /** Value indicating H.263 codec. */
  public static final int CODEC_H_263 = 1748121139;

  /** Value indicating Sorenson codec. */
  public static final int CODEC_SORENSON = 1398165809;

  /** Value indicating Sorenson 3 codec. */
  public static final int CODEC_SORENSON_3 = 0x53565133;

  /** Value indicating MPEG-4 codec. */
  public static final int CODEC_MPEG_4 = 0x6d703476;

  /** Value indicating Raw codec. */
  public static final int CODEC_RAW = 0;

  /** Value indicating Low quality. */
  public static final int QUALITY_LOW = 256;

  /** Value indicating Normal quality. */
  public static final int QUALITY_NORMAL = 512;

  /** Value indicating High quality. */
  public static final int QUALITY_HIGH = 768;

  /** Value indicating Maximum quality. */
  public static final int QUALITY_MAXIMUM = 1023;

  /** Seek to this offset to update the total number of pixel bytes. */
  private static final long BYTE_COUNT_OFFSET = 8;

  // -- Fields --

  /** The codec to use. */
  protected int codec = CODEC_RAW;

  /** The quality to use. */
  protected int quality = QUALITY_NORMAL;

  /** Total number of pixel bytes. */
  protected int numBytes;

  /** Vector of plane offsets. */
  protected List<Integer> offsets;

  /** Time the file was created. */
  protected int created;

  /** Number of padding bytes in each row. */
  protected int pad;

  /** Whether we need the legacy writer. */
  protected boolean needLegacy = false;

  /** Legacy QuickTime writer. */
  protected LegacyQTWriter legacy;

  private int numWritten = 0;

  // -- Constructor --

  public QTWriter() {
    super("QuickTime", "mov");
    LegacyQTTools tools = new LegacyQTTools();
    if (tools.canDoQT()) {
      compressionTypes = new String[] {
          CompressionType.UNCOMPRESSED.getCompression(),
        // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
        /*"Motion JPEG-B",*/
          CompressionType.CINEPAK.getCompression(), 
          CompressionType.ANIMATION.getCompression(), 
          CompressionType.H_263.getCompression(), 
          CompressionType.SORENSON.getCompression(), 
          CompressionType.SORENSON_3.getCompression(), 
          CompressionType.MPEG_4.getCompression()
      };
    }
    else compressionTypes = new String[] {
        CompressionType.UNCOMPRESSED.getCompression()};
  }

  // -- QTWriter API methods --

  /**
   * Sets the encoded movie's codec.
   * @param codec Codec value:<ul>
   *   <li>QTWriterCODEC_CINEPAK</li>
   *   <li>QTWriter.CODEC_ANIMATION</li>
   *   <li>QTWriter.CODEC_H_263</li>
   *   <li>QTWriter.CODEC_SORENSON</li>
   *   <li>QTWriter.CODEC_SORENSON_3</li>
   *   <li>QTWriter.CODEC_MPEG_4</li>
   *   <li>QTWriter.CODEC_RAW</li>
   * </ul>
   */
  public void setCodec(int codec) { this.codec = codec; }

  /**
   * Sets the quality of the encoded movie.
   * @param quality Quality value:<ul>
   *   <li>QTWriter.QUALITY_LOW</li>
   *   <li>QTWriter.QUALITY_MEDIUM</li>
   *   <li>QTWriter.QUALITY_HIGH</li>
   *   <li>QTWriter.QUALITY_MAXIMUM</li>
   * </ul>
   */
  public void setQuality(int quality) { this.quality = quality; }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    if (needLegacy) {
      legacy.saveBytes(no, buf, x, y, w, h);
      return;
    }

    MetadataRetrieve r = getMetadataRetrieve();

    // get the width and height of the image
    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();

    // need to check if the width is a multiple of 8
    // if it is, great; if not, we need to pad each scanline with enough
    // bytes to make the width a multiple of 8

    int nChannels = getSamplesPerPixel();
    int planeSize = width * height * nChannels;

    if (!initialized[series][no]) {
      initialized[series][no] = true;
      setCodec();
      if (codec != CODEC_RAW) {
        needLegacy = true;
        legacy.setId(currentId);
        legacy.saveBytes(no, buf, x, y, w, h);
        return;
      }

      // update the number of pixel bytes written
      int planeOffset = numBytes;
      numBytes += (planeSize + pad * height);
      out.seek(BYTE_COUNT_OFFSET);
      out.writeInt(numBytes + 8);

      out.seek(offsets.get(no));

      if (!isFullPlane(x, y, w, h)) {
        out.skipBytes(planeSize + pad * height);
      }
    }

    out.seek(offsets.get(no) + y * (nChannels * width + pad));

    // invert each pixel
    // this will makes the colors look right in other readers (e.g. xine),
    // but needs to be reversed in QTReader

    byte[] tmp = new byte[buf.length];
    if (nChannels == 1 && !needLegacy) {
      for (int i=0; i<buf.length; i++) {
        tmp[i] = (byte) (255 - buf[i]);
      }
    }
    else System.arraycopy(buf, 0, tmp, 0, buf.length);

    if (!interleaved) {
      // need to write interleaved data
      byte[] tmp2 = new byte[tmp.length];
      System.arraycopy(tmp, 0, tmp2, 0, tmp.length);
      for (int i=0; i<tmp.length; i++) {
        int c = i / (w * h);
        int index = i % (w * h);
        tmp[index * nChannels + c] = tmp2[i];
      }
    }

    int rowLen = tmp.length / h;
    for (int row=0; row<h; row++) {
      out.skipBytes(nChannels * x);
      out.write(tmp, row * rowLen, rowLen);
      for (int i=0; i<pad; i++) {
        out.writeByte(0);
      }
      if (row < h - 1) {
        out.skipBytes(nChannels * (width - w - x));
      }
    }
    numWritten++;
    writeFooter();
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

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    MetadataRetrieve r = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(r, series);

    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();
    int nChannels = getSamplesPerPixel();
    int planeSize = width * height * nChannels;

    pad = nChannels > 1 ? 0 : (4 - (width % 4)) % 4;

    if (legacy == null) {
      legacy = new LegacyQTWriter();
      legacy.setCodec(codec);
      legacy.setMetadataRetrieve(r);
    }
    offsets = new ArrayList<Integer>();
    created = (int) System.currentTimeMillis();
    numBytes = 0;

    if (out.length() == 0) {
      // -- write the first header --

      writeAtom(8, "wide");
      writeAtom(numBytes + 8, "mdat");
      numWritten = 0;
    }
    else {
      out.seek(BYTE_COUNT_OFFSET);

      RandomAccessInputStream in = new RandomAccessInputStream(currentId);
      in.seek(BYTE_COUNT_OFFSET);
      numBytes = in.readInt() - 8;
      numWritten = numBytes / (planeSize + pad * height);
      in.close();
    }

    for (int i=0; i<getPlaneCount(); i++) {
      offsets.add(16 + i * (planeSize + pad * height));
    }
  }

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    if (out != null) writeFooter();
    super.close();
    numBytes = 0;
    created = 0;
    offsets = null;
    pad = 0;
    numWritten = 0;
  }

  // -- Helper methods --

  private void setCodec() {
    if (compression == null) return;
    if (compression.equals("Uncompressed")) codec = CODEC_RAW;
    // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
    else if (compression.equals("Motion JPEG-B")) codec = CODEC_MOTION_JPEG_B;
    else if (compression.equals("Cinepak")) codec = CODEC_CINEPAK;
    else if (compression.equals("Animation")) codec = CODEC_ANIMATION;
    else if (compression.equals("H.263")) codec = CODEC_H_263;
    else if (compression.equals("Sorenson")) codec = CODEC_SORENSON;
    else if (compression.equals("Sorenson 3")) codec = CODEC_SORENSON_3;
    else if (compression.equals("MPEG 4")) codec = CODEC_MPEG_4;
  }

  private void writeFooter() throws IOException {
    out.seek(out.length());
    MetadataRetrieve r = getMetadataRetrieve();
    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();
    int nChannels = getSamplesPerPixel();

    int timeScale = 1000;
    int duration = (int) (numWritten * ((double) timeScale / fps));
    int bitsPerPixel = (nChannels > 1) ? 24 : 40;
    int channels = (bitsPerPixel >= 40) ? 1 : 3;

    // -- write moov atom --

    int atomLength = 685 + 8*numWritten;
    writeAtom(atomLength, "moov");

    // -- write mvhd atom --

    writeAtom(108, "mvhd");
    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(created); // creation time
    out.writeInt((int) System.currentTimeMillis());
    out.writeInt(timeScale); // time scale
    out.writeInt(duration); // duration
    out.write(new byte[] {0, 1, 0, 0});  // preferred rate & volume
    out.write(new byte[] {0, -1, 0, 0, 0, 0, 0, 0, 0, 0}); // reserved

    writeRotationMatrix();

    out.writeShort(0); // not sure what this is
    out.writeInt(0); // preview duration
    out.writeInt(0); // preview time
    out.writeInt(0); // poster time
    out.writeInt(0); // selection time
    out.writeInt(0); // selection duration
    out.writeInt(0); // current time
    out.writeInt(2); // next track's id

    // -- write trak atom --

    atomLength -= 116;
    writeAtom(atomLength, "trak");

    // -- write tkhd atom --

    writeAtom(92, "tkhd");
    out.writeShort(0); // version
    out.writeShort(15); // flags

    out.writeInt(created); // creation time
    out.writeInt((int) System.currentTimeMillis());
    out.writeInt(1); // track id
    out.writeInt(0); // reserved

    out.writeInt(duration); // duration
    out.writeInt(0); // reserved
    out.writeInt(0); // reserved
    out.writeShort(0); // reserved
    out.writeInt(0); // unknown

    writeRotationMatrix();

    out.writeInt(width); // image width
    out.writeInt(height); // image height
    out.writeShort(0); // reserved

    // -- write edts atom --

    writeAtom(36, "edts");

    // -- write elst atom --

    writeAtom(28, "elst");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(1); // number of entries in the table
    out.writeInt(duration); // duration
    out.writeShort(0); // time
    out.writeInt(1); // rate
    out.writeShort(0); // unknown

    // -- write mdia atom --

    atomLength -= 136;
    writeAtom(atomLength, "mdia");

    // -- write mdhd atom --

    writeAtom(32, "mdhd");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(created); // creation time
    out.writeInt((int) System.currentTimeMillis());
    out.writeInt(timeScale); // time scale
    out.writeInt(duration); // duration
    out.writeShort(0); // language
    out.writeShort(0); // quality

    // -- write hdlr atom --

    writeAtom(58, "hdlr");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeBytes("mhlr");
    out.writeBytes("vide");
    out.writeBytes("appl");
    out.write(new byte[] {16, 0, 0, 0, 0, 1, 1, 11, 25});
    out.writeBytes("Apple Video Media Handler");

    // -- write minf atom --

    atomLength -= 98;
    writeAtom(atomLength, "minf");

    // -- write vmhd atom --

    writeAtom(20, "vmhd");

    out.writeShort(0); // version
    out.writeShort(1); // flags
    out.writeShort(64); // graphics mode
    out.writeShort(32768);  // opcolor 1
    out.writeShort(32768);  // opcolor 2
    out.writeShort(32768);  // opcolor 3

    // -- write hdlr atom --

    writeAtom(57, "hdlr");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeBytes("dhlr");
    out.writeBytes("alis");
    out.writeBytes("appl");
    out.write(new byte[] {16, 0, 0, 1, 0, 1, 1, 31, 24});
    out.writeBytes("Apple Alias Data Handler");

    // -- write dinf atom --

    writeAtom(36, "dinf");

    // -- write dref atom --

    writeAtom(28, "dref");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeShort(0); // version 2
    out.writeShort(1); // flags 2
    out.write(new byte[] {0, 0, 0, 12});
    out.writeBytes("alis");
    out.writeShort(0); // version 3
    out.writeShort(1); // flags 3

    // -- write stbl atom --

    atomLength -= 121;
    writeAtom(atomLength, "stbl");

    // -- write stsd atom --

    writeAtom(118, "stsd");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(1); // number of entries in the table
    out.write(new byte[] {0, 0, 0, 102});
    out.writeBytes("raw "); // codec
    out.write(new byte[] {0, 0, 0, 0, 0, 0});  // reserved
    out.writeShort(1); // data reference
    out.writeShort(1); // version
    out.writeShort(1); // revision
    out.writeBytes("appl");
    out.writeInt(0); // temporal quality
    out.writeInt(768); // spatial quality
    out.writeShort(width); // image width
    out.writeShort(height); // image height
    byte[] dpi = new byte[] {0, 72, 0, 0};
    out.write(dpi); // horizontal dpi
    out.write(dpi); // vertical dpi
    out.writeInt(0); // data size
    out.writeShort(1); // frames per sample
    out.writeShort(12); // length of compressor name
    out.writeBytes("Uncompressed"); // compressor name
    out.writeInt(bitsPerPixel); // unknown
    out.writeInt(bitsPerPixel); // unknown
    out.writeInt(bitsPerPixel); // unknown
    out.writeInt(bitsPerPixel); // unknown
    out.writeInt(bitsPerPixel); // unknown
    out.writeShort(bitsPerPixel); // bits per pixel
    out.writeInt(65535); // ctab ID
    out.write(new byte[] {12, 103, 97, 108}); // gamma
    out.write(new byte[] {97, 1, -52, -52, 0, 0, 0, 0}); // unknown

    // -- write stts atom --

    writeAtom(24, "stts");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(1); // number of entries in the table
    out.writeInt(numWritten); // number of planes
    out.writeInt((int) ((double) timeScale / fps)); // milliseconds per frame

    // -- write stsc atom --

    writeAtom(28, "stsc");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(1); // number of entries in the table
    out.writeInt(1); // chunk
    out.writeInt(1); // samples
    out.writeInt(1); // id

    // -- write stsz atom --

    writeAtom(20 + 4 * numWritten, "stsz");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(0); // sample size
    out.writeInt(numWritten); // number of planes
    for (int i=0; i<numWritten; i++) {
      // sample size
      out.writeInt(channels * height * (width + pad));
    }

    // -- write stco atom --

    writeAtom(16 + 4 * numWritten, "stco");

    out.writeShort(0); // version
    out.writeShort(0); // flags
    out.writeInt(numWritten); // number of planes
    for (int i=0; i<numWritten; i++) {
      // write the plane offset
      out.writeInt(offsets.get(i));
    }
  }

  /** Write the 3x3 matrix that describes how to rotate the image. */
  private void writeRotationMatrix() throws IOException {
    out.writeInt(1);
    out.writeInt(0);
    out.writeInt(0);
    out.writeInt(0);
    out.writeInt(1);
    out.writeInt(0);
    out.writeInt(0);
    out.writeInt(0);
    out.writeInt(16384);
  }

  /** Write the atom length and type. */
  private void writeAtom(int length, String type) throws IOException {
    out.writeInt(length);
    out.writeBytes(type);
  }

}
