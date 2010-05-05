//
// QTWriter.java
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

import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.gui.LegacyQTTools;
import loci.formats.meta.MetadataRetrieve;

/**
 * QTWriter is the file format writer for uncompressed QuickTime movie files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/QTWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/QTWriter.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
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

  // -- Fields --

  /** Current file. */
  protected RandomAccessOutputStream out;

  /** The codec to use. */
  protected int codec = CODEC_RAW;

  /** The quality to use. */
  protected int quality = QUALITY_NORMAL;

  /** Number of planes written. */
  protected int numWritten;

  /** Seek to this offset to update the total number of pixel bytes. */
  protected long byteCountOffset;

  /** Total number of pixel bytes. */
  protected int numBytes;

  /** Vector of plane offsets. */
  protected Vector<Integer> offsets;

  /** Time the file was created. */
  protected int created;

  /** Whether we need the legacy writer. */
  protected boolean needLegacy = false;

  /** Legacy QuickTime writer. */
  protected LegacyQTWriter legacy;

  // -- Constructor --

  public QTWriter() {
    super("QuickTime", "mov");
    LegacyQTTools tools = new LegacyQTTools();
    if (tools.canDoQT()) {
      compressionTypes = new String[] {
        "Uncompressed",
        // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
        /*"Motion JPEG-B",*/
        "Cinepak", "Animation", "H.263", "Sorenson", "Sorenson 3", "MPEG 4"
      };
    }
    else compressionTypes = new String[] {"Uncompressed"};
  }

  // -- QTWriter API methods --

  /**
   * Sets the encoded movie's codec.
   * @param codec Codec value:<ul>
   *   <li>QTWriter.CODEC_CINEPAK</li>
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

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (legacy == null) legacy = new LegacyQTWriter();

    if (needLegacy) {
      legacy.saveBytes(buf, last);
      return;
    }

    MetadataRetrieve r = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(r, series);

    // get the width and height of the image
    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();

    // need to check if the width is a multiple of 8
    // if it is, great; if not, we need to pad each scanline with enough
    // bytes to make the width a multiple of 8

    int bytesPerPixel =
      FormatTools.pixelTypeFromString(r.getPixelsType(series).toString());
    Integer samples = r.getChannelSamplesPerPixel(series, 0);
    if (samples == null) {
      LOGGER.warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int nChannels = samples == null ? 1 : samples.intValue();
    int pad = nChannels > 1 ? 0 : (4 - (width % 4)) % 4;

    if (!initialized) {
      initialized = true;
      setCodec();
      if (codec != CODEC_RAW) {
        needLegacy = true;
        legacy.setCodec(codec);
        legacy.setMetadataRetrieve(getMetadataRetrieve());
        legacy.setId(currentId);
        legacy.saveBytes(buf, series, lastInSeries, last);
        return;
      }

      // -- write the header --

      offsets = new Vector<Integer>();
      out = new RandomAccessOutputStream(currentId);
      created = (int) System.currentTimeMillis();
      numWritten = 0;
      numBytes = buf.length + pad * height;
      byteCountOffset = 8;

      if (out.length() == 0) {
        // -- write the first header --

        out.writeInt(8);
        out.writeBytes("wide");

        out.writeInt(numBytes + 8);
        out.writeBytes("mdat");
      }
      else {
        out.seek(byteCountOffset);

        RandomAccessInputStream in = new RandomAccessInputStream(currentId);
        in.seek(byteCountOffset);
        numBytes = (int) (in.readInt() & 0xffffffff) - 8;
        in.close();

        numWritten = numBytes / (buf.length + pad * height);
        numBytes += (buf.length + pad * height);

        out.seek(byteCountOffset);
        out.write(numBytes + 8);

        for (int i=0; i<numWritten; i++) {
          offsets.add(16 + i * (buf.length + pad * height));
        }

        out.seek(out.length());
      }

      // -- write the first plane of pixel data (mdat) --

      offsets.add((int) out.length());
    }
    else {
      // update the number of pixel bytes written
      int planeOffset = numBytes;
      numBytes += (buf.length + pad * height);
      out.seek(byteCountOffset);
      out.writeInt(numBytes + 8);

      // write this plane's pixel data
      out.seek(out.length());

      offsets.add(planeOffset + 16);
    }

    // invert each pixel
    // this will makes the colors look right in other readers (e.g. xine),
    // but needs to be reversed in QTReader

    if (nChannels == 1 && bytesPerPixel == 1 && !needLegacy) {
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) (255 - buf[i]);
      }
    }

    if (!interleaved) {
      // need to write interleaved data
      byte[] tmp = new byte[buf.length];
      System.arraycopy(buf, 0, tmp, 0, buf.length);
      for (int i=0; i<buf.length; i++) {
        int c = i / (width * height);
        int index = i % (width * height);
        buf[index * nChannels + c] = tmp[i];
      }
    }

    int rowLen = buf.length / height;
    for (int row=0; row<height; row++) {
      out.write(buf, row * rowLen, rowLen);
      for (int i=0; i<pad; i++) {
        out.writeByte(0);
      }
    }

    numWritten++;

    if (last) {
      int timeScale = 100;
      int duration = numWritten * (timeScale / fps);
      int bitsPerPixel = (nChannels > 1) ? bytesPerPixel * 24 :
        bytesPerPixel * 8 + 32;
      if (bytesPerPixel == 2) {
        bitsPerPixel = nChannels > 1 ? 16 : 40;
      }
      int channels = (bitsPerPixel >= 40) ? 1 : 3;

      // -- write moov atom --

      int atomLength = 685 + 8*numWritten;
      out.writeInt(atomLength);
      out.writeBytes("moov");

      // -- write mvhd atom --

      out.writeInt(108);
      out.writeBytes("mvhd");
      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(created); // creation time
      out.writeInt((int) System.currentTimeMillis());
      out.writeInt(timeScale); // time scale
      out.writeInt(duration); // duration
      out.write(new byte[] {0, 1, 0, 0});  // preferred rate & volume
      out.write(new byte[] {0, -1, 0, 0, 0, 0, 0, 0, 0, 0}); // reserved

      // 3x3 matrix - tells reader how to rotate image

      out.writeInt(1);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(1);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(16384);

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
      out.writeInt(atomLength);
      out.writeBytes("trak");

      // -- write tkhd atom --

      out.writeInt(92);
      out.writeBytes("tkhd");
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

      // 3x3 matrix - tells reader how to rotate the image

      out.writeInt(1);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(1);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(16384);

      out.writeInt(width); // image width
      out.writeInt(height); // image height
      out.writeShort(0); // reserved

      // -- write edts atom --

      out.writeInt(36);
      out.writeBytes("edts");

      // -- write elst atom --

      out.writeInt(28);
      out.writeBytes("elst");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(1); // number of entries in the table
      out.writeInt(duration); // duration
      out.writeShort(0); // time
      out.writeInt(1); // rate
      out.writeShort(0); // unknown

      // -- write mdia atom --

      atomLength -= 136;
      out.writeInt(atomLength);
      out.writeBytes("mdia");

      // -- write mdhd atom --

      out.writeInt(32);
      out.writeBytes("mdhd");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(created); // creation time
      out.writeInt((int) System.currentTimeMillis());
      out.writeInt(timeScale); // time scale
      out.writeInt(duration); // duration
      out.writeShort(0); // language
      out.writeShort(0); // quality

      // -- write hdlr atom --

      out.writeInt(58);
      out.writeBytes("hdlr");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeBytes("mhlr");
      out.writeBytes("vide");
      out.writeBytes("appl");
      out.write(new byte[] {16, 0, 0, 0, 0, 1, 1, 11, 25});
      out.writeBytes("Apple Video Media Handler");

      // -- write minf atom --

      atomLength -= 98;
      out.writeInt(atomLength);
      out.writeBytes("minf");

      // -- write vmhd atom --

      out.writeInt(20);
      out.writeBytes("vmhd");

      out.writeShort(0); // version
      out.writeShort(1); // flags
      out.writeShort(64); // graphics mode
      out.writeShort(32768);  // opcolor 1
      out.writeShort(32768);  // opcolor 2
      out.writeShort(32768);  // opcolor 3

      // -- write hdlr atom --

      out.writeInt(57);
      out.writeBytes("hdlr");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeBytes("dhlr");
      out.writeBytes("alis");
      out.writeBytes("appl");
      out.write(new byte[] {16, 0, 0, 1, 0, 1, 1, 31, 24});
      out.writeBytes("Apple Alias Data Handler");

      // -- write dinf atom --

      out.writeInt(36);
      out.writeBytes("dinf");

      // -- write dref atom --

      out.writeInt(28);
      out.writeBytes("dref");

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
      out.writeInt(atomLength);
      out.writeBytes("stbl");

      // -- write stsd atom --

      out.writeInt(118);
      out.writeBytes("stsd");

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
      out.write(new byte[] {0, 72, 0, 0}); // horizontal dpi
      out.write(new byte[] {0, 72, 0, 0}); // vertical dpi
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

      out.writeInt(24);
      out.writeBytes("stts");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(1); // number of entries in the table
      out.writeInt(numWritten); // number of planes
      out.writeInt(fps); // frames per second

      // -- write stsc atom --

      out.writeInt(28);
      out.writeBytes("stsc");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(1); // number of entries in the table
      out.writeInt(1); // chunk
      out.writeInt(1); // samples
      out.writeInt(1); // id

      // -- write stsz atom --

      out.writeInt(20 + 4*numWritten);
      out.writeBytes("stsz");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(0); // sample size
      out.writeInt(numWritten); // number of planes
      for (int i=0; i<numWritten; i++) {
        // sample size
        out.writeInt(channels * height * (width + pad) * bytesPerPixel);
      }

      // -- write stco atom --

      out.writeInt(16 + 4*numWritten);
      out.writeBytes("stco");

      out.writeShort(0); // version
      out.writeShort(0); // flags
      out.writeInt(numWritten); // number of planes
      for (int i=0; i<numWritten; i++) {
        // write the plane offset
        out.writeInt(offsets.get(i));
      }

      out.close();
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.UINT8};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    numWritten = 0;
    byteCountOffset = 0;
    numBytes = 0;
    created = 0;
    offsets = null;
    currentId = null;
    initialized = false;
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

}
