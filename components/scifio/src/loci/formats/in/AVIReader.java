//
// AVIReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.BitBuffer;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.MSRLECodec;
import loci.formats.codec.MSVideoCodec;
import loci.formats.meta.MetadataStore;

/**
 * AVIReader is the file format reader for AVI files.
 *
 * Much of this code was adapted from Wayne Rasband's AVI Movie Reader
 * plugin for ImageJ (available at http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/AVIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/AVIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class AVIReader extends FormatReader {

  // -- Constants --

  public static final String AVI_MAGIC_STRING = "RIFF";

  /** Supported compression types. */
  private static final int MSRLE = 1;
  private static final int MS_VIDEO = 1296126531;
  //private static final int CINEPAK = 1684633187;
  private static final int JPEG = 1196444237;
  private static final int Y8 = 538982489;

  // -- Fields --

  /** Offset to each plane. */
  private Vector<Long> offsets;

  /** Number of bytes in each plane. */
  private Vector<Long> lengths;

  private String listString;
  private String type = "error";
  private String fcc = "error";
  private int size = -1;
  private long pos;
  private int bytesPerPlane;

  // Stream Format chunk fields

  private int bmpColorsUsed, bmpWidth;
  private int bmpCompression, bmpScanLineSize;
  private short bmpBitsPerPixel;
  private byte[][] lut = null;

  private byte[] lastImage;
  private int lastImageNo;

  // -- Constructor --

  /** Constructs a new AVI reader. */
  public AVIReader() {
    super("Audio Video Interleave", "avi");
    suffixNecessary = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 12;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String type = stream.readString(4);
    stream.skipBytes(4);
    String format = stream.readString(4);
    return type.equals(AVI_MAGIC_STRING) && format.equals("AVI ");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return isRGB() ? null : lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    double p = ((double) bmpScanLineSize) / bmpBitsPerPixel;
    int effectiveWidth = (int) (bmpScanLineSize / p);
    if (effectiveWidth == 0 || effectiveWidth < getSizeX()) {
      effectiveWidth = getSizeX();
    }

    long fileOff = offsets.get(no).longValue();
    long end = no < offsets.size() - 1 ? offsets.get(no + 1) : in.length();
    long maxBytes = end - fileOff;
    in.seek(fileOff);

    if (bmpCompression != 0 && bmpCompression != Y8) {
      byte[] b = uncompress(no, buf);
      int rowLen = FormatTools.getPlaneSize(this, w, 1);
      int inputRowLen = FormatTools.getPlaneSize(this, getSizeX(), 1);
      for (int row=0; row<h; row++) {
        System.arraycopy(b, (row + y) * inputRowLen + x * bytes, buf,
          row * rowLen, rowLen);
      }
      b = null;
      return buf;
    }

    if (bmpBitsPerPixel < 8) {
      int rawSize = FormatTools.getPlaneSize(this, effectiveWidth, getSizeY());
      rawSize /= (8 / bmpBitsPerPixel);

      byte[] b = new byte[rawSize];

      int len = rawSize / getSizeY();
      in.read(b);

      BitBuffer bb = new BitBuffer(b);
      bb.skipBits(bmpBitsPerPixel * len * (getSizeY() - h - y));

      for (int row=h; row>=y; row--) {
        bb.skipBits(bmpBitsPerPixel * x);
        for (int col=0; col<len; col++) {
          buf[(row - y) * len + col] = (byte) bb.getBits(bmpBitsPerPixel);
        }
        bb.skipBits(bmpBitsPerPixel * (getSizeX() - w - x));
      }

      return buf;
    }

    int pad = (bmpScanLineSize / getRGBChannelCount()) - getSizeX() * bytes;
    int scanline = w * bytes * (isInterleaved() ? getRGBChannelCount() : 1);

    in.skipBytes((getSizeX() + pad) * bytes * (getSizeY() - h - y));

    if (getSizeX() == w && pad == 0) {
      for (int row=0; row<getSizeY(); row++) {
        int outputRow = bmpCompression == Y8 ? row : getSizeY() - row - 1;
        in.read(buf, outputRow * scanline, scanline);
      }

      // swap channels
      if (bmpBitsPerPixel == 24 || bmpBitsPerPixel == 32) {
        for (int i=0; i<buf.length / getRGBChannelCount(); i++) {
          byte r = buf[i * getRGBChannelCount() + 2];
          buf[i * getRGBChannelCount() + 2] = buf[i * getRGBChannelCount()];
          buf[i * getRGBChannelCount()] = r;
        }
      }
    }
    else {
      int skip = FormatTools.getPlaneSize(this, getSizeX() - w - x + pad, 1);
      if ((getSizeX() + pad) * getSizeY() * getRGBChannelCount() > maxBytes) {
        skip /= getRGBChannelCount();
      }
      for (int i=h - 1; i>=0; i--) {
        in.skipBytes(x * (bmpBitsPerPixel / 8));
        in.read(buf, (i - y)*scanline, scanline);
        if (bmpBitsPerPixel == 24) {
          for (int j=0; j<w; j++) {
            byte r = buf[i*scanline + j*3 + 2];
            buf[i*scanline + j*3 + 2] = buf[i*scanline + j*3];
            buf[i*scanline + j*3] = r;
          }
        }
        if (i > 0) in.skipBytes(skip);
      }
    }

    if (bmpBitsPerPixel == 16 && isRGB()) {
      // channels are stored as BGR, need to swap them
      ImageTools.bgrToRgb(buf, isInterleaved(), 2, getRGBChannelCount());
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      listString = null;
      offsets = null;
      lengths = null;
      type = null;
      fcc = null;
      size = -1;
      pos = 0;
      bytesPerPlane = 0;
      bmpColorsUsed = bmpWidth = bmpCompression = bmpScanLineSize = 0;
      bmpBitsPerPixel = 0;
      lut = null;
      lastImage = null;
      lastImageNo = -1;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    LOGGER.info("Verifying AVI format");

    offsets = new Vector<Long>();
    lengths = new Vector<Long>();
    lastImageNo = -1;

    while (in.getFilePointer() < in.length() - 8) {
      readChunk();
    }
    LOGGER.info("Populating metadata");

    core[0].imageCount = offsets.size();
    core[0].sizeZ = 1;
    core[0].sizeT = getImageCount();
    core[0].littleEndian = true;
    core[0].interleaved = bmpBitsPerPixel != 16;

    addGlobalMeta("Compression", getCodecName(bmpCompression));

    if (bmpCompression == JPEG) {
      long fileOff = offsets.get(0).longValue();
      in.seek(fileOff);
      int nBytes = uncompress(0, null).length / (getSizeX() * getSizeY());

      if (bmpBitsPerPixel == 16) {
        nBytes /= 2;
      }
      core[0].sizeC = nBytes;
      core[0].rgb = getSizeC() > 1;
    }
    else if (bmpBitsPerPixel == 32) {
      core[0].sizeC = 4;
      core[0].rgb = true;
    }
    else if (bytesPerPlane == 0 || bmpBitsPerPixel == 24) {
      core[0].rgb = bmpBitsPerPixel > 8 || (bmpCompression != 0 && lut == null);
      core[0].sizeC = isRGB() ? 3 : 1;
    }
    else {
      core[0].sizeC = bytesPerPlane /
        (getSizeX() * getSizeY() * (bmpBitsPerPixel / 8));
      core[0].rgb = getSizeC() > 1;
    }
    core[0].dimensionOrder = isRGB() ? "XYCTZ" : "XYTCZ";
    core[0].falseColor = false;
    core[0].metadataComplete = true;
    core[0].indexed = lut != null && !isRGB();

    if (bmpBitsPerPixel <= 8) {
      core[0].pixelType = FormatTools.UINT8;
      core[0].bitsPerPixel = bmpBitsPerPixel;
    }
    else if (bmpBitsPerPixel == 16) core[0].pixelType = FormatTools.UINT16;
    else if (bmpBitsPerPixel == 24 || bmpBitsPerPixel == 32) {
      core[0].pixelType = FormatTools.UINT8;
    }
    else {
      throw new FormatException(
          "Unknown matching for pixel bit width of: " + bmpBitsPerPixel);
    }

    if (bmpCompression != 0) core[0].pixelType = FormatTools.UINT8;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private byte[] uncompress(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (lastImageNo == no) {
      buf = lastImage;
      return buf;
    }
    CodecOptions options = new CodecOptions();
    options.width = getSizeX();
    options.height = getSizeY();
    options.previousImage = (lastImageNo == no - 1) ? lastImage : null;
    options.bitsPerSample = bmpBitsPerPixel;
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();

    if (bmpCompression == MSRLE) {
      byte[] b = new byte[(int) lengths.get(no).longValue()];
      in.read(b);
      MSRLECodec codec = new MSRLECodec();
      buf = codec.decompress(b, options);
      lastImage = buf;
      lastImageNo = no;
    }
    else if (bmpCompression == MS_VIDEO) {
      MSVideoCodec codec = new MSVideoCodec();
      buf = codec.decompress(in, options);
      lastImage = buf;
      lastImageNo = no;
    }
    else if (bmpCompression == JPEG) {
      JPEGCodec codec = new JPEGCodec();
      buf = codec.decompress(in, options);
    }
    /*
    else if (bmpCompression == CINEPAK) {
      Object[] options = new Object[2];
      options[0] = new Integer(bmpBitsPerPixel);
      options[1] = lastImage;

      CinepakCodec codec = new CinepakCodec();
      buf = codec.decompress(b, options);
      lastImage = buf;
      if (no == core[0].imageCount - 1) lastImage = null;
      return buf;
    }
    */
    else {
      throw new UnsupportedCompressionException(
        bmpCompression + " not supported");
    }
    return buf;
  }

  private void readChunkHeader() throws IOException {
    readTypeAndSize();
    fcc = in.readString(4);
  }

  private void readTypeAndSize() throws IOException {
    type = in.readString(4);
    size = in.readInt();
  }

  private void readChunk() throws FormatException, IOException {
    readChunkHeader();

    if (type.equals("RIFF")) {
      if (!fcc.startsWith("AVI")) {
        throw new FormatException("Sorry, AVI RIFF format not found.");
      }
    }
    else if (in.getFilePointer() == 12) {
      throw new FormatException("Not an AVI file");
    }
    else {
      if (in.getFilePointer() + size - 4 <= in.length()) {
        in.skipBytes(size - 4);
      }
      return;
    }

    pos = in.getFilePointer();
    long spos = pos;

    LOGGER.info("Searching for image data");

    while ((in.length() - in.getFilePointer()) > 4) {
      listString = in.readString(4);
      if (listString.equals("RIFF")) {
        in.seek(in.getFilePointer() - 4);
        return;
      }
      in.seek(pos);
      if (listString.equals(" JUN")) {
        in.skipBytes(1);
        pos++;
      }

      if (listString.equals("JUNK")) {
        readTypeAndSize();

        if (type.equals("JUNK")) {
          in.skipBytes(size);
        }
      }
      else if (listString.equals("LIST")) {
        spos = in.getFilePointer();
        readChunkHeader();

        in.seek(spos);
        if (fcc.equals("hdrl")) {
          readChunkHeader();

          if (type.equals("LIST")) {
            if (fcc.equals("hdrl")) {
              readTypeAndSize();
              if (type.equals("avih")) {
                spos = in.getFilePointer();

                addGlobalMeta("Microseconds per frame", in.readInt());
                addGlobalMeta("Max. bytes per second", in.readInt());

                in.skipBytes(8);

                addGlobalMeta("Total frames", in.readInt());
                addGlobalMeta("Initial frames", in.readInt());

                in.skipBytes(8);
                core[0].sizeX = in.readInt();

                addGlobalMeta("Frame height", in.readInt());
                addGlobalMeta("Scale factor", in.readInt());
                addGlobalMeta("Frame rate", in.readInt());
                addGlobalMeta("Start time", in.readInt());
                addGlobalMeta("Length", in.readInt());

                addGlobalMeta("Frame width", getSizeX());

                if (spos + size <= in.length()) {
                  in.seek(spos + size);
                }
              }
            }
          }
        }
        else if (fcc.equals("strl")) {
          long startPos = in.getFilePointer();
          long streamSize = size;

          readChunkHeader();

          if (type.equals("LIST")) {
            if (fcc.equals("strl")) {
              readTypeAndSize();

              if (type.equals("strh")) {
                spos = in.getFilePointer();
                in.skipBytes(40);

                addGlobalMeta("Stream quality", in.readInt());
                bytesPerPlane = in.readInt();
                addGlobalMeta("Stream sample size", bytesPerPlane);

                if (spos + size <= in.length()) {
                  in.seek(spos + size);
                }
              }

              readTypeAndSize();
              if (type.equals("strf")) {
                spos = in.getFilePointer();

                if (getSizeY() != 0) {
                  in.skipBytes(size);
                  readTypeAndSize();
                  while (type.equals("indx")) {
                    in.skipBytes(size);
                    readTypeAndSize();
                  }
                  pos = in.getFilePointer() - 4;
                  in.seek(pos - 4);
                  continue;
                }

                in.skipBytes(4);
                bmpWidth = in.readInt();
                core[0].sizeY = in.readInt();
                in.skipBytes(2);
                bmpBitsPerPixel = in.readShort();
                bmpCompression = in.readInt();
                in.skipBytes(4);

                addGlobalMeta("Horizontal resolution", in.readInt());
                addGlobalMeta("Vertical resolution", in.readInt());

                bmpColorsUsed = in.readInt();
                in.skipBytes(4);

                addGlobalMeta("Bitmap compression value", bmpCompression);
                addGlobalMeta("Number of colors used", bmpColorsUsed);
                addGlobalMeta("Bits per pixel", bmpBitsPerPixel);

                // scan line is padded with zeros to be a multiple of 4 bytes
                int npad = bmpWidth % 4;
                if (npad > 0) npad = 4 - npad;

                bmpScanLineSize = (bmpWidth + npad) * (bmpBitsPerPixel / 8);

                int bmpActualColorsUsed = 0;
                if (bmpColorsUsed != 0) {
                  bmpActualColorsUsed = bmpColorsUsed;
                }
                else if (bmpBitsPerPixel < 16) {
                  // a value of 0 means we determine this based on the
                  // bits per pixel
                  bmpActualColorsUsed = 1 << bmpBitsPerPixel;
                  bmpColorsUsed = bmpActualColorsUsed;
                }

                if (bmpCompression != MSRLE && bmpCompression != 0 &&
                  bmpCompression != MS_VIDEO && bmpCompression != JPEG &&
                  bmpCompression != Y8)
                {
                  throw new UnsupportedCompressionException(
                    bmpCompression + " not supported");
                }

                if (!(bmpBitsPerPixel == 4 || bmpBitsPerPixel == 8 ||
                  bmpBitsPerPixel == 24 || bmpBitsPerPixel == 16 ||
                  bmpBitsPerPixel == 32))
                {
                  throw new FormatException(bmpBitsPerPixel +
                    " bits per pixel not supported");
                }

                if (bmpActualColorsUsed != 0) {
                  // read the palette
                  lut = new byte[3][bmpColorsUsed];

                  for (int i=0; i<bmpColorsUsed; i++) {
                    if (bmpCompression != Y8) {
                      lut[2][i] = in.readByte();
                      lut[1][i] = in.readByte();
                      lut[0][i] = in.readByte();
                      in.skipBytes(1);
                    }
                    else {
                      lut[0][i] = (byte) i;
                      lut[1][i] = (byte) i;
                      lut[2][i] = (byte) i;
                    }
                  }
                }

                in.seek(spos + size);
              }
            }

            spos = in.getFilePointer();
            readTypeAndSize();
            if (type.equals("strd")) {
              in.skipBytes(size);
            }
            else {
              in.seek(spos);
            }

            spos = in.getFilePointer();
            readTypeAndSize();
            if (type.equals("strn") || type.equals("indx")) {
              in.skipBytes(size);
            }
            else {
              in.seek(spos);
            }
          }

          if (startPos + streamSize + 8 <= in.length()) {
            in.seek(startPos + 8 + streamSize);
          }
        }
        else if (fcc.equals("movi")) {
          readChunkHeader();

          if (type.equals("LIST")) {
            if (fcc.equals("movi")) {
              spos = in.getFilePointer();
              if (spos >= in.length() - 12) break;
              readChunkHeader();
              if (!(type.equals("LIST") && (fcc.equals("rec ") ||
                fcc.equals("movi"))))
              {
                in.seek(spos);
              }

              spos = in.getFilePointer();
              boolean end = false;
              while (!end) {
                readTypeAndSize();
                String oldType = type;
                while (type.startsWith("ix") || type.endsWith("tx") ||
                  type.equals("JUNK"))
                {
                  in.skipBytes(size);
                  readTypeAndSize();
                }

                String check = type.substring(2);
                boolean foundPixels = false;
                while (check.equals("db") || check.equals("dc") ||
                  check.equals("wb"))
                {
                  foundPixels = true;
                  if (check.startsWith("d")) {
                    if (size > 0 || bmpCompression != 0) {
                      offsets.add(new Long(in.getFilePointer()));
                      lengths.add(new Long(size));
                      in.skipBytes(size);
                    }
                  }

                  spos = in.getFilePointer();
                  if (spos + 8 >= in.length()) return;

                  readTypeAndSize();
                  if (type.equals("JUNK")) {
                    in.skipBytes(size);
                    spos = in.getFilePointer();
                    if (spos + 8 >= in.length()) return;
                    readTypeAndSize();
                  }
                  check = type.substring(2);
                  if (check.equals("0d")) {
                    in.seek(spos + 1);
                    readTypeAndSize();
                    check = type.substring(2);
                  }
                }
                in.seek(spos);
                if (!oldType.startsWith("ix") && !foundPixels) {
                  end = true;
                }
              }
            }
          }
        }
        else {
          int oldSize = size;
          size = in.readInt() - 8;
          if (size > oldSize) {
            size = oldSize;
            in.seek(in.getFilePointer() - 4);
          }
          // skipping unknown block
          if (size + 8 >= 0) in.skipBytes(8 + size);
        }
      }
      else {
        // skipping unknown block
        readTypeAndSize();
        if (in.getFilePointer() + 8 < in.length() && !type.equals("idx1")) {
          readTypeAndSize();
        }
        else if (!type.equals("idx1")) break;
        if (in.getFilePointer() + size + 4 <= in.length()) {
          in.skipBytes(size);
        }
        if (type.equals("idx1")) break;
      }
      pos = in.getFilePointer();
    }
  }

  private String getCodecName(final int bmpCompression) {
    switch (bmpCompression) {
      case 0:
        return "Raw (uncompressed)";
      case MSRLE:
        return "Microsoft Run-Length Encoding (MSRLE)";
      case MS_VIDEO:
        return "Microsoft Video (MSV1)";
      case JPEG:
        return "JPEG";
      //case CINEPAK:
      //  return "Cinepak";
      default:
        return "Unknown";
    }
  }

}
