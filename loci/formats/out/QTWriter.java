//
// QTWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.out;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.util.Vector;
import loci.formats.*;

/**
 * QTWriter is the file format writer for uncompressed QuickTime movie files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/QTWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/QTWriter.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */

public class QTWriter extends FormatWriter {

  // -- Constants --

  // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
  /** Value indicating Motion JPEG-B codec. */
  public static final int CODEC_MOTION_JPEG_B = 1835692130;

  /** Value indicating Cinepack codec. */
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
  protected RandomAccessFile out;

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
  protected Vector offsets;

  /** Time the file was created. */
  protected int created;

  /** Whether we need the legacy writer. */
  protected boolean needLegacy = false;

  /** Legacy QuickTime writer. */
  protected LegacyQTWriter legacy;

  // -- Constructor --

  public QTWriter() {
    super("QuickTime", "mov");
    compressionTypes = new String[] {
      "Uncompressed",
      // NB: Writing to Motion JPEG-B with QTJava seems to be broken.
      "Motion JPEG-B",
      "Cinepak", "Animation", "H.263", "Sorenson", "Sorenson 3", "MPEG 4"
    };

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

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(Image image, boolean last)
    throws FormatException, IOException
  {
    if (image == null) throw new FormatException("Image is null");
    if (legacy == null) legacy = new LegacyQTWriter();

    if (needLegacy) {
      legacy.setId(currentId);
      legacy.saveImage(image, last);
      return;
    }

    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);

    // get the width and height of the image
    int width = img.getWidth();
    int height = img.getHeight();

    // retrieve pixel data for this plane
    byte[][] byteData = ImageTools.getPixelBytes(img, false);

    // need to check if the width is a multiple of 8
    // if it is, great; if not, we need to pad each scanline with enough
    // bytes to make the width a multiple of 8

    int pad = width % 4;
    pad = (4 - pad) % 4;

    int bytesPerPixel = byteData[0].length / (width * height);

    if (bytesPerPixel > 1) {
      throw new FormatException("Unsupported bits per pixel : " +
        (8 * bytesPerPixel) + ".");
    }

    pad *= bytesPerPixel;

    byte[][] temp = byteData;
    byteData = new byte[temp.length][temp[0].length + height*pad];

    int rowLength = width * bytesPerPixel;
    for (int oldScanline=0; oldScanline<height; oldScanline++) {
      for (int k=0; k<temp.length; k++) {
        System.arraycopy(temp[k], oldScanline*rowLength, byteData[k],
          oldScanline*(rowLength + pad), rowLength);
      }
    }

    // invert each pixel
    // this will makes the colors look right in other readers (e.g. xine),
    // but needs to be reversed in QTReader

    if (byteData.length == 1 && bytesPerPixel == 1) {
      for (int i=0; i<byteData.length; i++) {
        for (int k=0; k<byteData[0].length; k++) {
          byteData[i][k] = (byte) (255 - byteData[i][k]);
        }
      }
    }

    if (!initialized) {
      initialized = true;
      setCodec();
      if (codec != 0) {
        needLegacy = true;
        legacy.setCodec(codec);
        legacy.setId(currentId);
        legacy.saveImage(image, last);
        return;
      }

      // -- write the header --

      offsets = new Vector();
      out = new RandomAccessFile(currentId, "rw");
      created = (int) System.currentTimeMillis();
      numWritten = 0;
      numBytes = byteData.length * byteData[0].length;
      byteCountOffset = 8;

      if (out.length() == 0) {
        // -- write the first header --

        DataTools.writeInt(out, 8, false);
        DataTools.writeString(out, "wide");

        DataTools.writeInt(out, numBytes + 8, false);
        DataTools.writeString(out, "mdat");
      }
      else {
        out.seek(byteCountOffset);
        numBytes = (int) DataTools.read4UnsignedBytes(out, false) - 8;
        numWritten = numBytes / (byteData[0].length * byteData.length);

        numBytes += byteData.length * byteData[0].length;

        out.seek(byteCountOffset);
        DataTools.writeInt(out, numBytes + 8, false);

        for (int i=0; i<numWritten; i++) {
          offsets.add(
            new Integer(16 + i * byteData.length * byteData[0].length));
        }

        out.seek(out.length());
      }

      // -- write the first plane of pixel data (mdat) --

      offsets.add(new Integer((int) out.length()));

      numWritten++;

      for (int i=0; i<byteData.length; i++) {
        out.write(byteData[i]);
      }
    }
    else {
      // update the number of pixel bytes written
      int planeOffset = numBytes;
      numBytes += (byteData.length * byteData[0].length);
      out.seek(byteCountOffset);
      DataTools.writeInt(out, numBytes + 8, false);

      // write this plane's pixel data
      out.seek(out.length());

      for (int i=0; i<byteData.length; i++) {
        out.write(byteData[i]);
      }

      offsets.add(new Integer(planeOffset + 16));
      numWritten++;
    }

    if (last) {
      int timeScale = 100;
      int duration = numWritten * (timeScale / fps);
      int bitsPerPixel = (byteData.length > 1) ? bytesPerPixel * 24 :
        bytesPerPixel * 8 + 32;
      int channels = (bitsPerPixel >= 40) ? 1 : 3;

      // -- write moov atom --

      int atomLength = 685 + 8*numWritten;
      DataTools.writeInt(out, atomLength, false);
      DataTools.writeString(out, "moov");

      // -- write mvhd atom --

      DataTools.writeInt(out, 108, false);
      DataTools.writeString(out, "mvhd");
      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, created, false); // creation time
      DataTools.writeInt(out, (int) System.currentTimeMillis(), false);
      DataTools.writeInt(out, timeScale, false); // time scale
      DataTools.writeInt(out, duration, false); // duration
      out.write(new byte[] {0, 1, 0, 0});  // preferred rate & volume
      out.write(new byte[] {0, -1, 0, 0, 0, 0, 0, 0, 0, 0}); // reserved

      // 3x3 matrix - tells reader how to rotate image

      DataTools.writeInt(out, 1, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 1, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 16384, false);

      DataTools.writeShort(out, 0, false); // not sure what this is
      DataTools.writeInt(out, 0, false); // preview duration
      DataTools.writeInt(out, 0, false); // preview time
      DataTools.writeInt(out, 0, false); // poster time
      DataTools.writeInt(out, 0, false); // selection time
      DataTools.writeInt(out, 0, false); // selection duration
      DataTools.writeInt(out, 0, false); // current time
      DataTools.writeInt(out, 2, false); // next track's id

      // -- write trak atom --

      atomLength -= 116;
      DataTools.writeInt(out, atomLength, false);
      DataTools.writeString(out, "trak");

      // -- write tkhd atom --

      DataTools.writeInt(out, 92, false);
      DataTools.writeString(out, "tkhd");
      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 15, false); // flags

      DataTools.writeInt(out, created, false); // creation time
      DataTools.writeInt(out, (int) System.currentTimeMillis(), false);
      DataTools.writeInt(out, 1, false); // track id
      DataTools.writeInt(out, 0, false); // reserved

      DataTools.writeInt(out, duration, false); // duration
      DataTools.writeInt(out, 0, false); // reserved
      DataTools.writeInt(out, 0, false); // reserved
      DataTools.writeShort(out, 0, false); // reserved

      DataTools.writeInt(out, 0, false); // unknown

      // 3x3 matrix - tells reader how to rotate the image

      DataTools.writeInt(out, 1, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 1, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 0, false);
      DataTools.writeInt(out, 16384, false);

      DataTools.writeInt(out, width, false); // image width
      DataTools.writeInt(out, height, false); // image height
      DataTools.writeShort(out, 0, false); // reserved

      // -- write edts atom --

      DataTools.writeInt(out, 36, false);
      DataTools.writeString(out, "edts");

      // -- write elst atom --

      DataTools.writeInt(out, 28, false);
      DataTools.writeString(out, "elst");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, 1, false); // number of entries in the table
      DataTools.writeInt(out, duration, false); // duration
      DataTools.writeShort(out, 0, false); // time
      DataTools.writeInt(out, 1, false); // rate
      DataTools.writeShort(out, 0, false); // unknown

      // -- write mdia atom --

      atomLength -= 136;
      DataTools.writeInt(out, atomLength, false);
      DataTools.writeString(out, "mdia");

      // -- write mdhd atom --

      DataTools.writeInt(out, 32, false);
      DataTools.writeString(out, "mdhd");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, created, false); // creation time
      DataTools.writeInt(out, (int) System.currentTimeMillis(), false);
      DataTools.writeInt(out, timeScale, false); // time scale
      DataTools.writeInt(out, duration, false); // duration
      DataTools.writeShort(out, 0, false); // language
      DataTools.writeShort(out, 0, false); // quality

      // -- write hdlr atom --

      DataTools.writeInt(out, 58, false);
      DataTools.writeString(out, "hdlr");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeString(out, "mhlr");
      DataTools.writeString(out, "vide");
      DataTools.writeString(out, "appl");
      out.write(new byte[] {16, 0, 0, 0, 0, 1, 1, 11, 25});
      DataTools.writeString(out, "Apple Video Media Handler");

      // -- write minf atom --

      atomLength -= 98;
      DataTools.writeInt(out, atomLength, false);
      DataTools.writeString(out, "minf");

      // -- write vmhd atom --

      DataTools.writeInt(out, 20, false);
      DataTools.writeString(out, "vmhd");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 1, false); // flags
      DataTools.writeShort(out, 64, false); // graphics mode
      DataTools.writeShort(out, 32768, false);  // opcolor 1
      DataTools.writeShort(out, 32768, false);  // opcolor 2
      DataTools.writeShort(out, 32768, false);  // opcolor 3

      // -- write hdlr atom --

      DataTools.writeInt(out, 57, false);
      DataTools.writeString(out, "hdlr");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeString(out, "dhlr");
      DataTools.writeString(out, "alis");
      DataTools.writeString(out, "appl");
      out.write(new byte[] {16, 0, 0, 1, 0, 1, 1, 31, 24});
      DataTools.writeString(out, "Apple Alias Data Handler");

      // -- write dinf atom --

      DataTools.writeInt(out, 36, false);
      DataTools.writeString(out, "dinf");

      // -- write dref atom --

      DataTools.writeInt(out, 28, false);
      DataTools.writeString(out, "dref");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeShort(out, 0, false); // version 2
      DataTools.writeShort(out, 1, false); // flags 2
      out.write(new byte[] {0, 0, 0, 12});
      DataTools.writeString(out, "alis");
      DataTools.writeShort(out, 0, false); // version 3
      DataTools.writeShort(out, 1, false); // flags 3

      // -- write stbl atom --

      atomLength -= 121;
      DataTools.writeInt(out, atomLength, false);
      DataTools.writeString(out, "stbl");

      // -- write stsd atom --

      DataTools.writeInt(out, 118, false);
      DataTools.writeString(out, "stsd");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, 1, false); // number of entries in the table
      out.write(new byte[] {0, 0, 0, 102});
      DataTools.writeString(out, "raw "); // codec
      out.write(new byte[] {0, 0, 0, 0, 0, 0});  // reserved
      DataTools.writeShort(out, 1, false); // data reference
      DataTools.writeShort(out, 1, false); // version
      DataTools.writeShort(out, 1, false); // revision
      DataTools.writeString(out, "appl");
      DataTools.writeInt(out, 0, false); // temporal quality
      DataTools.writeInt(out, 768, false); // spatial quality
      DataTools.writeShort(out, width, false); // image width
      DataTools.writeShort(out, height, false); // image height
      out.write(new byte[] {0, 72, 0, 0}); // horizontal dpi
      out.write(new byte[] {0, 72, 0, 0}); // vertical dpi
      DataTools.writeInt(out, 0, false); // data size
      DataTools.writeShort(out, 1, false); // frames per sample
      DataTools.writeShort(out, 12, false); // length of compressor name
      DataTools.writeString(out, "Uncompressed"); // compressor name
      DataTools.writeInt(out, bitsPerPixel, false); // unknown
      DataTools.writeInt(out, bitsPerPixel, false); // unknown
      DataTools.writeInt(out, bitsPerPixel, false); // unknown
      DataTools.writeInt(out, bitsPerPixel, false); // unknown
      DataTools.writeInt(out, bitsPerPixel, false); // unknown
      DataTools.writeShort(out, bitsPerPixel, false); // bits per pixel
      DataTools.writeInt(out, 65535, false); // ctab ID
      out.write(new byte[] {12, 103, 97, 108}); // gamma
      out.write(new byte[] {97, 1, -52, -52, 0, 0, 0, 0}); // unknown

      // -- write stts atom --

      DataTools.writeInt(out, 24, false);
      DataTools.writeString(out, "stts");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, 1, false); // number of entries in the table
      DataTools.writeInt(out, numWritten, false); // number of planes
      DataTools.writeInt(out, (timeScale / fps), false); // frames per second

      // -- write stsc atom --

      DataTools.writeInt(out, 28, false);
      DataTools.writeString(out, "stsc");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, 1, false); // number of entries in the table
      DataTools.writeInt(out, 1, false); // chunk
      DataTools.writeInt(out, 1, false); // samples
      DataTools.writeInt(out, 1, false); // id

      // -- write stsz atom --

      DataTools.writeInt(out, 20 + 4*numWritten, false);
      DataTools.writeString(out, "stsz");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, 0, false); // sample size
      DataTools.writeInt(out, numWritten, false); // number of planes
      for (int i=0; i<numWritten; i++) {
        // sample size
        DataTools.writeInt(out, channels*height*(width+pad)*bytesPerPixel,
          false);
      }

      // -- write stco atom --

      DataTools.writeInt(out, 16 + 4*numWritten, false);
      DataTools.writeString(out, "stco");

      DataTools.writeShort(out, 0, false); // version
      DataTools.writeShort(out, 0, false); // flags
      DataTools.writeInt(out, numWritten, false); // number of planes
      for (int i=0; i<numWritten; i++) {
        // write the plane offset
        DataTools.writeInt(out, ((Integer) offsets.get(i)).intValue(), false);
      }

      out.close();
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.UINT8, FormatTools.UINT16};
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
