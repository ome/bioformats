//
// QTReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import javax.imageio.ImageIO;
import loci.formats.*;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Video codecs currently supported: raw, rle, jpeg, mjpb, rpza.
 * Additional video codecs will be added as time permits.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class QTReader extends FormatReader {

  // -- Constants --

  /** List of identifiers for each container atom. */
  private static final String[] CONTAINER_TYPES = {
    "moov", "trak", "udta", "tref", "imap", "mdia", "minf", "stbl", "edts",
    "mdra", "rmra", "imag", "vnrp", "dinf"
  };

  // Some MJPEG-B stuff.

  /** Header data. */
  private static final byte[] HEADER = new byte[] {
    (byte) 0xff, (byte) 0xd8, 0x00, 0x10,
    0x4a, 0x46, 0x49, 0x46, 0x00,  // 'JFIF'
    0x01, 0x01,  // version number (major, minor)
    0x00, // units; 0 => X and Y specify pixel aspect ratio
    0x48, 0x48, // X and Y density
    0x00, 0x00 // no thumbnail specified
  };

  /** Luminance quantization table. */
  private static final byte[] LUM_QUANT = new byte[] {
    3, 2, 2, 2, 2, 2, 3, 2,
    2, 2, 3, 3, 3, 3, 4, 6,
    4, 4, 4, 4, 4, 7, 5, 6,
    5, 6, 9, 8, 9, 9, 8, 8,
    8, 8, 9, 10, 13, 11, 9, 10,
    13, 10, 8, 8, 12, 16, 12, 13,
    14, 14, 15, 15, 15, 9, 11, 16,
    17, 16, 14, 17, 13, 14, 15, 14
  };

  /** Chrominance quantization table. */
  private static final byte[] CHROM_QUANT = new byte[] {
    3, 3, 3, 4, 3, 4, 7, 4,
    4, 7, 14, 10, 8, 10, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
    14, 14, 14, 14, 14, 14, 14, 14,
  };

  /** Defines the number of entries in the luminance DC Huffman table. */
  private static final byte[] LUM_DC_BITS = new byte[] {
    0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0
  };

  /** The luminance DC Huffman table. */
  private static final byte[] LUM_DC = new byte[] {
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
  };

  /** Defines the number of entries in the chrominance DC Huffman table. */
  private static final byte[] CHROM_DC_BITS = new byte[] {
    0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0
  };

  /** The chrominance DC Huffman table. */
  private static final byte[] CHROM_DC = new byte[] {
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
  };

  /** Defines the number of entries in the luminance AC Huffman table. */
  private static final byte[] LUM_AC_BITS = new byte[] {
    0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d
  };

  /** The luminance AC Huffman table. */
  private static final byte[] LUM_AC = new byte[] {
    1, 2, 3, 0, 4, 17, 5, 18, 33, 0x31, 0x41, 6, 19, 0x51, 0x61, 7,
    34, 0x71, 20, 0x32, (byte) 0x81, (byte) 0x91, (byte) 161, 8, 35, 0x42,
    (byte) 0xb1, (byte) 0xc1, 0x15, 0x52, (byte) 0xd1, (byte) 0xf0,
    0x24, 0x33, 0x62, 0x72, (byte) 0x82, 9, 10, 22, 23, 24, 25, 26, 0x25,
    0x26, 0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
    0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49,
    0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59,
    0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69,
    0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79,
    0x7a, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
    (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93,
    (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98,
    (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4,
    (byte) 0xa5, (byte) 0xa6, (byte) 0xa7, (byte) 0xa8, (byte) 0xa9,
    (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5,
    (byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba,
    (byte) 0xc2, (byte) 0xc3, (byte) 0xc4, (byte) 0xc5, (byte) 0xc6,
    (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2,
    (byte) 0xd3, (byte) 0xd4, (byte) 0xd5, (byte) 0xd6, (byte) 0xd7,
    (byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe1, (byte) 0xe2,
    (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7,
    (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xf1, (byte) 0xf2,
    (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7,
    (byte) 0xf8, (byte) 0xf9, (byte) 0xfa
  };

  /** Defines the number of entries in the chrominance AC Huffman table. */
  private static final byte[] CHROM_AC_BITS = new byte[] {
    0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 0x77
  };

  /** The chrominance AC Huffman table. */
  private static final byte[] CHROM_AC = new byte[] {
    0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21,
    0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71,
    0x13, 0x22, 0x32, (byte) 0x81, 0x08, 0x14, 0x42, (byte) 0x91, (byte) 0xa1,
    (byte) 0xb1, (byte) 0xc1, 0x09, 0x23, 0x33, 0x52, (byte) 0xf0,
    0x15, 0x62, 0x72, (byte) 0xd1, 0x0a, 0x16, 0x24, 0x34,
    (byte) 0xe1, 0x25, (byte) 0xf1, 0x17, 0x18, 0x19, 0x1a, 0x26,
    0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38,
    0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
    0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
    0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
    0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,
    0x79, 0x7a, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85,
    (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a,
    (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
    (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0xa2,
    (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xa6, (byte) 0xa7,
    (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3,
    (byte) 0xb4, (byte) 0xb5, (byte) 0xb6, (byte) 0xb7, (byte) 0xb8,
    (byte) 0xb9, (byte) 0xba, (byte) 0xc2, (byte) 0xc3, (byte) 0xc4,
    (byte) 0xc5, (byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9,
    (byte) 0xca, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4, (byte) 0xd5,
    (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda,
    (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6,
    (byte) 0xe7, (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xf2,
    (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7,
    (byte) 0xf8, (byte) 0xf9, (byte) 0xfa
  };

  // -- Fields --

  /** Current file. */
  private RandomAccessStream in;

  /** Flag indicating whether the current file is little endian. */
  private boolean little = false;

  /** Number of images in the file. */
  private int numImages;

  /** Offset to start of pixel data. */
  private int pixelOffset;

  /** Total number of bytes of pixel data. */
  private int pixelBytes;

  /** Width of a single plane. */
  private int width;

  /** Height of a single plane. */
  private int height;

  /** Pixel depth. */
  private int bitsPerPixel;

  /** Raw plane size, in bytes. */
  private int rawSize;

  /** Offsets to each plane's pixel data. */
  private Vector offsets;

  /** Pixel data for the previous image plane. */
  private byte[] prevPixels;

  /** Previous plane number. */
  private int prevPlane;

  /** Flag indicating whether we can safely use prevPixels. */
  private boolean canUsePrevious;

  /** Video codec used by this movie. */
  private String codec;

  /** Some movies use two video codecs -- this is the second codec. */
  private String altCodec;

  /** Number of frames that use the alternate codec. */
  private int altPlanes;

  /** An instance of the old QuickTime reader, in case this one fails. */
  private LegacyQTReader legacy;

  /** Flag indicating whether to use legacy reader by default. */
  private boolean useLegacy;

  /** Amount to subtract from each offset. */
  private int scale;

  /** Number of bytes in each plane. */
  private Vector chunkSizes;

  /** Set to true if the scanlines in a plane are interlaced (mjpb only). */
  private boolean interlaced;

  /** Gamma value. */
  private double gamma;

  /** Flag indicating whether the resource and data fork are separated. */
  private boolean spork;

  private boolean flip;

  // -- Constructor --

  /** Constructs a new QuickTime reader. */
  public QTReader() { super("QuickTime", "mov"); }

  // -- QTReader API methods --

  /** Sets whether to use the legacy reader (QTJava) by default. */
  public void setLegacy(boolean legacy) { useLegacy = legacy; }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a QuickTime file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given QuickTime file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return bitsPerPixel < 40;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return little;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /* @see FormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    if (useLegacy) legacy.setMetadataStore(store);
  }

  /* @see FormatReader#swapDimensions(String, String) */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    super.swapDimensions(id, order);
    if (useLegacy) legacy.swapDimensions(id, order);
  }

  /** Obtains the specified image from the given file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    String code = codec;
    if (no >= getImageCount(id) - altPlanes) code = altCodec;

    boolean doLegacy = useLegacy;
    if (!doLegacy && !code.equals("raw ") && !code.equals("rle ") &&
      !code.equals("jpeg") && !code.equals("mjpb") && !code.equals("rpza"))
    {
      if (debug) {
        debug("Unsupported codec (" + code + "); using QTJava reader");
      }
      doLegacy = true;
    }
    if (doLegacy) {
      if (legacy == null) legacy = createLegacyReader();
      return legacy.openBytes(id, no);
    }

    int offset = ((Integer) offsets.get(no)).intValue();
    int nextOffset = pixelBytes;

    scale = ((Integer) offsets.get(0)).intValue();
    offset -= scale;

    if (no < offsets.size() - 1) {
      nextOffset = ((Integer) offsets.get(no + 1)).intValue();
      nextOffset -= scale;
    }

    if ((nextOffset - offset) < 0) {
      int temp = offset;
      offset = nextOffset;
      nextOffset = temp;
    }

    byte[] pixs = new byte[nextOffset - offset];

    in.seek(pixelOffset + offset);
    in.read(pixs);

    canUsePrevious = (prevPixels != null) && (prevPlane == no - 1);

    if (code.equals("jpeg") || code.equals("mjpb")) {
      byte[] s = ImageTools.getBytes(openImage(id, no), false, no);
      updateMinMax(s, no);
      return s;
    }

    byte[] bytes = uncompress(pixs, code);
    // on rare occassions, we need to trim the data
    if (canUsePrevious && (prevPixels.length < bytes.length)) {
      byte[] temp = bytes;
      bytes = new byte[prevPixels.length];
      System.arraycopy(temp, 0, bytes, 0, bytes.length);
    }

    if (flip) {
      // we need to flip the X and Y axes before displaying

      byte[] tmp = bytes;
      bytes = new byte[tmp.length];

      int t = width;
      width = height;
      height = t;

      int b = bytes.length / (width * height);

      if (b % 3 != 0) {
        for (int i=0; i<width; i++) {
          for (int j=0; j<height; j++) {
            for (int k=0; k<b; k++) {
              bytes[j*width*b + (width - i) + b] = tmp[i*height*b + j + b];
            }
          }
        }
      }
      else {
        for (int i=0; i<3; i++) {
          for (int j=0; j<width; j++) {
            for (int k=0; k<height; k++) {
              bytes[k*width +i*width*height + (width - j - 1)] =
                tmp[j*height + k + i*width*height];
            }
          }
        }
      }
    }

    prevPixels = bytes;
    prevPlane = no;

    // determine whether we need to strip out any padding bytes

    int pad = width % 4;
    pad = (4 - pad) % 4;

    if (width * height * (bitsPerPixel / 8) == prevPixels.length) {
      pad = 0;
    }

    if (pad > 0 && !code.equals("rpza")) {
      bytes = new byte[prevPixels.length - height*pad];

      for (int row=0; row<height; row++) {
        System.arraycopy(prevPixels, row*(width+pad), bytes,
          row*width, width);
      }
    }
    else if (code.equals("rpza")) {
      bytes = new byte[prevPixels.length];

      int cut = 0;

      for (int i=width-1; i>=0; i--) {
        byte[] redColumn = new byte[height];
        byte[] greenColumn = new byte[height];
        byte[] blueColumn = new byte[height];

        for (int j=0; j<cut; j++) {
          redColumn[j] = prevPixels[(j+height-cut)*width + i];
          greenColumn[j] = prevPixels[(j+height-cut)*width + i + width*height];
          blueColumn[j] = prevPixels[(j+height-cut)*width + i + 2*width*height];
        }

        for (int j=cut; j<height; j++) {
          redColumn[j] = prevPixels[j*width + i];
          greenColumn[j] = prevPixels[j*width + i + width*height];
          blueColumn[j] = prevPixels[j*width + i + 2*width*height];
        }

        if (i > width - 1 - height) cut++;

        for (int j=0; j<height; j++) {
          bytes[j*width + i] = redColumn[j];
          bytes[j*width + i + width*height] = greenColumn[j];
          bytes[j*width + i + 2*width*height] = blueColumn[j];
        }
      }
    }

    if (flip) {
      int t = width;
      width = height;
      height = t;
    }

    if (bitsPerPixel == 40 || bitsPerPixel == 8) {
      // invert the pixels
      for (int i=0; i<bytes.length; i++) {
        bytes[i] = (byte) (255 - bytes[i]);
      }
      updateMinMax(bytes, no);
      return bytes;
    }
    else if (bitsPerPixel == 32) {
      // strip out alpha channel
      byte[][] data = new byte[3][bytes.length / 4];
      for (int i=0; i<data[0].length; i++) {
        data[0][i] = bytes[4*i + 1];
        data[1][i] = bytes[4*i + 2];
        data[2][i] = bytes[4*i + 3];
      }

      byte[] rtn = new byte[data.length * data[0].length];
      for (int i=0; i<data.length; i++) {
        System.arraycopy(data[i], 0, rtn, i * data[0].length, data[i].length);
      }
      updateMinMax(rtn, no);
      return rtn;
    }
    else {
      updateMinMax(bytes, no);
      return bytes;
    }
  }

  /** Obtains the specified image from the given QuickTime file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    String code = codec;
    if (no >= getImageCount(id) - altPlanes) code = altCodec;

    boolean doLegacy = useLegacy;
    if (!doLegacy && !code.equals("raw ") && !code.equals("rle ") &&
      !code.equals("jpeg") && !code.equals("mjpb") && !code.equals("rpza"))
    {
      if (debug) {
        debug("Unsupported codec (" + code + "); using QTJava reader");
      }
      doLegacy = true;
    }
    if (doLegacy) {
      if (legacy == null) legacy = createLegacyReader();
      return legacy.openImage(id, no);
    }

    int offset = ((Integer) offsets.get(no)).intValue();
    int nextOffset = pixelBytes;

    scale = ((Integer) offsets.get(0)).intValue();
    offset -= scale;

    if (no < offsets.size() - 1) {
      nextOffset = ((Integer) offsets.get(no + 1)).intValue();
      nextOffset -= scale;
    }

    if ((nextOffset - offset) < 0) {
      int temp = offset;
      offset = nextOffset;
      nextOffset = temp;
    }

    byte[] pixs = new byte[nextOffset - offset];

    in.seek(pixelOffset + offset);
    in.read(pixs);

    canUsePrevious = (prevPixels != null) && (prevPlane == no - 1);

    BufferedImage b = null;

    if (code.equals("jpeg")) {
      b = bufferedJPEG(pixs);
    }
    else if (code.equals("mjpb")) {
      b = mjpbUncompress(pixs);
    }
    else {
      int bpp = bitsPerPixel / 8;
      if (bpp == 3 || bpp == 4 || bpp == 5) bpp = 1;
      b = ImageTools.makeImage(openBytes(id, no), flip ? height : width,
        flip ? width : height, isRGB(id) ? 3 : 1, false, bpp, little);
    }
    updateMinMax(b, no);
    return b;
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
    prevPixels = null;
  }

  /** Initializes the given QuickTime file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("QTReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    spork = true;
    offsets = new Vector();
    chunkSizes = new Vector();
    parse(0, 0, in.length());
    numImages = offsets.size();

    int bytesPerPixel = bitsPerPixel / 8;
    bytesPerPixel %= 4;

    switch (bytesPerPixel) {
      case 0:
      case 1:
        pixelType[0] = FormatTools.UINT8;
        break;
      case 2:
        pixelType[0] = FormatTools.INT16;
        break;
      case 3:
        pixelType[0] = FormatTools.UINT8;
        break;
    }

    sizeX[0] = flip ? height : width;
    sizeY[0] = flip ? width : height;
    sizeZ[0] = 1;
    sizeC[0] = bitsPerPixel < 40 ? 3 : 1;
    sizeT[0] = numImages;
    currentOrder[0] = "XYCZT";

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    store.setPixels(
      new Integer(width),
      new Integer(height),
      new Integer(numImages),
      new Integer(bitsPerPixel < 40 ? 3 : 1),
      new Integer(1),
      new Integer(pixelType[0]),
      new Boolean(!little),
      "XYCZT",
      null,
      null);
    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }

    // this handles the case where the data and resource forks have been
    // separated
    if (spork) {
      // first we want to check if there is a resource fork present
      // the resource fork will generally have the same name as the data fork,
      // but will have either the prefix "._" or the suffix ".qtr"
      // (or <filename>/rsrc on a Mac)

      String base = null;
      if (id.indexOf(".") != -1) {
        base = id.substring(0, id.lastIndexOf("."));
      }
      else base = id;

      Location f = new Location(base + ".qtr");
      if (f.exists()) {
        in = new RandomAccessStream(f.getAbsolutePath());

        stripHeader();
        parse(0, 0, in.length());
        numImages = offsets.size();
        return;
      }
      else {
        f = new Location(base.substring(0,
          base.lastIndexOf(File.separator) + 1) + "._" +
          base.substring(base.lastIndexOf(File.separator) + 1));
        if (f.exists()) {
          in = new RandomAccessStream(f.getAbsolutePath());
          stripHeader();
          parse(0, 0, in.length());
          numImages = offsets.size();
          return;
        }
        else {
          f = new Location(base + "/rsrc");
          if (f.exists()) {
            in = new RandomAccessStream(f.getAbsolutePath());
            stripHeader();
            parse(0, 0, in.length());
            numImages = offsets.size();
            return;
          }
        }
      }

      throw new FormatException("QuickTime resource fork not found. " +
        " To avoid this issue, please flatten your QuickTime movies " +
        "before importing with Bio-Formats.");

      /* TODO
      // If we didn't find the resource fork, we can check to see if the file
      // uses a JPEG-compatible codec.  In this case, we can do some guesswork
      // to read the file; otherwise we will fail gracefully.

      if (debug) {
        debug("Failed to find the QuickTime resource fork. " +
          "Attempting to proceed using only the data fork.");
      }

      // read through the file looking for occurences of the codec string
      numImages = 0;
      String codecString = new String(pixels, 4, 4);
      if (codecString.equals("mjpg")) codec = "mjpb";
      else codec = codecString;

      if (codec.equals("mjpb") || codec.equals("jpeg")) {
        // grab the width, height, and bits per pixel from the first plane

      }
      else {
        throw new FormatException("Sorry, this QuickTime movie does not " +
          "contain a Resource Fork.  Support for this case will be improved " +
          "as time permits.  To avoid this issue, please flatten your " +
          "QuickTime movies before importing with Bio-Formats.");
      }

      boolean canAdd = true;
      for (int i=0; i<pixels.length-5; i++) {
        if (codecString.equals(new String(pixels, i, 4))) {
          if (canAdd) {
            offsets.add(new Integer(i - 4));
            numImages++;
            canAdd = false;
          }
          else {
            canAdd = true;
          }
          i += 1000;
        }
      }
      */
    }
  }

  // -- Helper methods --

  /** Parse all of the atoms in the file. */
  public void parse(int depth, long offset, long length) throws IOException {
    while (offset < length) {
      in.seek(offset);

      // first 4 bytes are the atom size
      long atomSize = in.readInt();
      if (atomSize < 0) atomSize += 4294967296L;

      // read the atom type
      byte[] four = new byte[4];
      in.read(four);
      String atomType = new String(four);

      // if atomSize is 1, then there is an 8 byte extended size
      if (atomSize == 1) {
        atomSize = in.readLong();
      }

      if (atomSize < 0) {
        System.err.println("QTReader: invalid atom size: " + atomSize);
      }

      if (debug) {
        debug("Seeking to " + offset +
          "; atomType=" + atomType + "; atomSize=" + atomSize);
      }

      byte[] data = new byte[0];

      // if this is a container atom, parse the children
      if (isContainer(atomType)) {
        parse(depth++, in.getFilePointer(), offset + atomSize);
      }
      else {
        if (atomSize == 0) atomSize = in.length();
        int oldpos = (int) in.getFilePointer();

        if (atomType.equals("mdat")) {
          // we've found the pixel data
          pixelOffset = (int) in.getFilePointer();
          pixelBytes = (int) atomSize;

          if (pixelBytes > (in.length() - pixelOffset)) {
            pixelBytes = (int) (in.length() - pixelOffset);
          }
        }
        else if (atomType.equals("tkhd")) {
          // we've found the dimensions

          in.skipBytes(38);
          int[][] matrix = new int[3][3];

          for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
              matrix[i][j] = in.readInt();
            }
          }

          // The contents of the matrix we just read determine whether or not
          // we should flip the width and height.  We can check the first two
          // rows of the matrix - they should correspond to the first two rows
          // of an identity matrix.

          // TODO : adapt to use the value of flip
          flip = matrix[0][0] == 0 && matrix[1][0] != 0;

          width = in.readInt();
          height = in.readInt();
        }
        else if (atomType.equals("stco")) {
          // we've found the plane offsets

          spork = false;
          in.readInt();
          int numPlanes = in.readInt();
          if (numPlanes != numImages) {
            in.seek(in.getFilePointer() - 4);
            int off = in.readInt();
            offsets.add(new Integer(off));
            for (int i=1; i<numImages; i++) {
              if ((chunkSizes.size() > 0) && (i < chunkSizes.size())) {
                rawSize = ((Integer) chunkSizes.get(i)).intValue();
              }
              else i = numImages;
              off += rawSize;
              offsets.add(new Integer(off));
            }
          }
          else {
            for (int i=0; i<numPlanes; i++) {
              offsets.add(new Integer(in.readInt()));
            }
          }
        }
        else if (atomType.equals("stsd")) {
          // found video codec and pixel depth information

          in.readInt();
          int numEntries = in.readInt();
          in.readInt();

          for (int i=0; i<numEntries; i++) {
            byte[] b = new byte[4];
            in.read(b);
            if (i == 0) {
              codec = new String(b);
              in.skipBytes(74);

              bitsPerPixel = in.readShort();
              if (codec.equals("rpza")) bitsPerPixel = 8;
              in.readShort();
              in.readDouble();
              int fieldsPerPlane = in.read();
              interlaced = fieldsPerPlane == 2;
              addMeta("Codec", codec);
              addMeta("Bits per pixel", new Integer(bitsPerPixel));
              in.readDouble();
              in.read();
            }
            else {
              altCodec = new String(b);
              addMeta("Second codec", altCodec);
            }
          }
        }
        else if (atomType.equals("stsz")) {
          // found the number of planes
          in.readInt();
          rawSize = in.readInt();
          numImages = in.readInt();

          if (rawSize == 0) {
            in.seek(in.getFilePointer() - 4);
            for (int b=0; b<numImages; b++) {
              chunkSizes.add(new Integer(in.readInt()));
            }
          }
        }
        else if (atomType.equals("stsc")) {
          in.readInt();

          int numChunks = in.readInt();

          if (altCodec != null) {
            int prevChunk = 0;
            for (int i=0; i<numChunks; i++) {
              int chunk = in.readInt();
              int planesPerChunk = in.readInt();
              int id = in.readInt();

              if (id == 2) altPlanes += planesPerChunk * (chunk - prevChunk);

              prevChunk = chunk;
            }
          }
        }
        else if (atomType.equals("stts")) {
          in.readDouble();
          in.readInt();
          int fps = in.readInt();
          addMeta("Frames per second", new Integer(fps));
        }
        if (oldpos + atomSize < in.length()) {
          in.seek(oldpos + atomSize);
        }
        else break;
      }

      if (atomSize == 0) offset = in.length();
      else offset += atomSize;

      // if a 'udta' atom, skip ahead 4 bytes
      if (atomType.equals("udta")) offset += 4;
      if (debug) print(depth, atomSize, atomType, data);
    }
  }

  /** Checks if the given String is a container atom type. */
  public boolean isContainer(String type) {
    for (int i=0; i<CONTAINER_TYPES.length; i++) {
      if (type.equals(CONTAINER_TYPES[i])) return true;
    }
    return false;
  }

  /** Debugging method; prints information on an atom. */
  public void print(int depth, long size, String type, byte[] data) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append(" ");
    sb.append(type + " : [" + size + "]");
    debug(sb.toString());
  }

  /** Uncompresses an image plane according to the the codec identifier. */
  public byte[] uncompress(byte[] pixs, String code)
    throws FormatException, IOException
  {
    // JPEG and mjpb codecs handled separately, so not included in this list
    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) return rleUncompress(pixs);
    else if (code.equals("rpza")) return rpzaUncompress(pixs);
    else {
      throw new FormatException("Sorry, " + codec + " codec is not supported");
    }
  }

  /** Uncompresses an RPZA compressed image plane. */
  private byte[] rpzaUncompress(byte[] input) throws FormatException {
    bitsPerPixel = 8;
    int[] out = new int[width * height];

    int pt = 1; // pointer into the array of compressed bytes

    // get the chunk size
    int size = DataTools.bytesToInt(input, pt, 3, false);
    pt += 3;

    int totalBlocks = ((width + 3) / 4) * ((height + 3) / 4);
    int currentBlock = 0;
    int blocksPerRow = ((width + 3) / 4);
    int rowPtr = 0;
    int pixelPtr = 0;
    int colorA = 0, colorB = 0;

    // process chunk data

    while (pt + 4 < input.length && currentBlock < totalBlocks) {
      byte opcode = input[pt];
      pt++;

      int nBlocks = (opcode & 0x1f) + 1;

      if ((opcode & 0x80) == 0) {
        colorA = (opcode << 8) | input[pt];
        pt++;
        opcode = 0;

        if ((input[pt] & 0x80) != 0) {
          opcode = 0x20;
          nBlocks = 1;
        }
      }

      switch (opcode & 0xe0) {
        case 0x80:
          // skip blocks
          while (nBlocks > 0) {
            currentBlock++;
            nBlocks--;
            pixelPtr += 4;
            if (pixelPtr >= width - 1) {
              rowPtr += 4;
              pixelPtr = 0;
            }
          }
          break;
        case 0xa0:
          // fill blocks with one color
          colorA = DataTools.bytesToInt(input, pt, 2, false);
          pt += 2;
          while (nBlocks > 0) {
            // fill the whole block with colorA

            for (int y=0; y<4; y++) {
              for (int x=0; x<4; x++) {
                if ((rowPtr + y)*width + pixelPtr + x < out.length) {
                  out[(rowPtr + y)*width + pixelPtr + x] = colorA;
                }
              }
            }

            pixelPtr += 4;
            if (pixelPtr >= width - 1) {
              rowPtr += 4;
              pixelPtr = 0;
            }
            currentBlock++;
            nBlocks--;
          }
          break;
        case 0xc0:
          colorA = DataTools.bytesToInt(input, pt, 2, false);
          pt += 2;
          break;
        case 0x20:
          // fill blocks with 4 colors
          colorA = DataTools.bytesToInt(input, pt, 2, false);
          colorB = DataTools.bytesToInt(input, pt, 2, false);
          pt += 4;

          // sort out the colors
          int[] colors = new int[4];
          colors[0] = colorB;
          colors[1] = 0;
          colors[2] = 0;
          colors[3] = colorA;

          int ta = (colorA >> 10) & 0x1f;
          int tb = (colorB >> 10) & 0x1f;
          colors[1] |= ((11 * ta + 21 * tb) >> 5) << 10;
          colors[2] |= ((21 * ta + 11 * tb) >> 5) << 10;

          ta = (colorA >> 5) & 0x1f;
          tb = (colorB >> 5) & 0x1f;
          colors[1] |= ((11 * ta + 21 * tb) >> 5) << 5;
          colors[2] |= ((21 * ta + 11 * tb) >> 5) << 5;

          ta = colorA & 0x1f;
          tb = colorB & 0x1f;
          colors[1] |= ((11 * ta + 21 * tb) >> 5);
          colors[2] |= ((21 * ta + 11 * tb) >> 5);

          while (nBlocks > 0) {
            for (int y=0; y<4; y++) {
              if (pt >= input.length) break;
              int ndx = input[pt];
              pt++;
              for (int x=0; x<4; x++) {
                int idx = (ndx >> (2 * (3 - x))) & 0x03;
                if ((rowPtr + y)*width + pixelPtr + x < out.length) {
                  out[(rowPtr + y)*width + pixelPtr + x] = colors[idx];
                }
              }
            }

            pixelPtr += 4;
            if (pixelPtr >= width - 1) {
              rowPtr += 4;
              pixelPtr = 0;
            }
            currentBlock++;
            nBlocks--;
          }

          break;
        case 0x00:
          // fill block with 16 colors

          for (int y=0; y<4; y++) {
            for (int x=0; x<4; x++) {
              if (y != 0 || x != 0) {
                colorA = DataTools.bytesToInt(input, pt, 2, false);
                pt += 2;
              }
              if ((rowPtr + y)*width + pixelPtr + x < out.length) {
                out[(rowPtr + y)*width + pixelPtr + x] = colorA;
              }
            }
          }

          pixelPtr += 4;
          if (pixelPtr >= width - 1) {
            rowPtr += 4;
            pixelPtr = 0;
          }

          currentBlock++;
          break;
      }
    }

    // convert int array to byte array and return
    // note that the colors need to be reversed

    byte[] rtn = new byte[out.length * 3];
    for (int i=0; i<out.length; i++) {
      int color = 65535 - out[i];
      rtn[i] = (byte) ((color >> 10) & 0x1f);
      rtn[i + out.length] = (byte) ((color >> 5) & 0x1f);
      rtn[i + 2*out.length] = (byte) (color & 0x1f);
    }
    return rtn;
  }

  /** Uncompresses a MJPEG-B compressed image plane. */
  public BufferedImage mjpbUncompress(byte[] input) throws FormatException {
    byte[] raw = null;
    byte[] raw2 = null;
    int pt = 16; // pointer into the compressed data

    // official documentation at
    // http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/
    // chapter_4_section_2.html

    // information on SOF, SOS, and SOD headers found at
    // http://www.obrador.com/essentialjpeg/headerinfo.htm

    // a brief overview for those who would rather not read the specifications:
    // MJPEG-B (or Motion JPEG) is a variant of the JPEG compression standard,
    // with one major difference.  In JPEG files (technically JFIF), important
    // blocks are denoted by a 'marker' - the byte '0xff' followed by a byte
    // identifying the type of data to follow.  According to JPEG standards,
    // all '0xff' bytes in the stream of compressed pixel data are to be
    // followed by a null byte (0x00), so that the reader knows not to
    // interpret the '0xff' as the beginning of a marker.  MJPEG-B does not
    // support markers, thus the null byte is not included after '0xff' bytes
    // in the data stream.  Also, an insufficient number of quantization
    // and Huffman tables are provided, so we must use the defaults obtained
    // from the JPEG documentation.  Finally, MJPEG-B allows for the data to
    // be spread across multiple fields, effectively forcing the reader to
    // interlace the scanlines.
    //
    // further aside -
    // http://lists.apple.com/archives/quicktime-talk/2000/Nov/msg00269.html
    // contains some interesting notes on why Apple chose to define this codec

    pt += 4;
    if (pt >= input.length) pt = 0;

    // most MJPEG-B planes don't have this identifier
    if (!(input[pt] != 'm' || input[pt+1] != 'j' || input[pt+2] != 'p' ||
      input[pt+3] != 'g') || !(input[pt-16] != 'm' || input[pt-15] != 'j' ||
      input[pt-14] != 'p' || input[pt-13] != 'g'))
    {
      int extra = 16;
      if (input[pt-16] == 'm') {
        pt = 4;
        extra = 0;
      }
      pt += 4;

      // number of compressed bytes (minus padding)
      pt += 4;

      // number of compressed bytes (including padding)
      pt += 4;

      // offset to second field
      int offset = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // offset to quantization table
      int quantOffset = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // offset to Huffman table
      int huffmanOffset = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // offset to start of frame
      int sof = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // offset to start of scan
      int sos = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // offset to start of data
      int sod = DataTools.bytesToInt(input, pt, 4, little) + extra;
      pt += 4;

      // skip over the quantization table, if it exists
      if (quantOffset != 0) {
        pt = quantOffset;
        int length = DataTools.bytesToInt(input, pt, 2, little);
        pt += length;
      }

      // skip over the Huffman table, if it exists
      if (huffmanOffset != 0) {
        pt = huffmanOffset;
        int length = DataTools.bytesToInt(input, pt, 2, little);
        pt += length;
      }

      // skip to the frame header
      pt = sof;

      // read sof header
      // we can skip over the first 7 bytes (length, bps, height, width)
      pt += 7;

      // number of channels
      int channels = DataTools.bytesToInt(input, pt, 1, little);
      pt++;

      int[] sampling = new int[channels];
      for (int i=0; i<channels; i++) {
        pt++;
        sampling[i] = DataTools.bytesToInt(input, pt, 1, little);
        pt += 2;
      }

      // skip to scan header
      pt = sos;

      // we can skip over the first 3 bytes (length, number of channels)
      pt += 3;
      int[] tables = new int[channels];
      for (int i=0; i<channels; i++) {
        pt++;
        tables[i] = DataTools.bytesToInt(input, pt, 1, little);
        pt++;
      }
      pt += 3;

      // now we can finally read this field's data
      pt = sod;

      int numBytes = offset - pt;
      if (offset == 0) numBytes = input.length - pt;
      raw = new byte[numBytes];
      System.arraycopy(input, pt, raw, 0, raw.length);

      // get the second field
      // from the specs:
      // "Each QuickTime sample consists of two distinct compressed images,
      // each coding one field: the field with the topmost scan-line, T, and
      // the other field, B. Each field is half the height of the overall
      // image, as declared in the height field of the sample description.
      // To be precise, if the height field contains the value H, then the
      // field T has ((H+1) div 2) lines, and field B has (H div 2) lines."

      if (offset != 0) {
        pt = offset;

        pt += 4; // reserved = 0
        pt += 4; // 'mjpg' tag
        pt += 4; // field size
        pt += 4; // padded field size
        pt += 4; // offset to next field = 0
        pt += 4; // quantization table offset
        pt += 4; // Huffman table offset
        pt += 4; // sof offset
        pt += 4; // sos offset

        pt += DataTools.bytesToInt(input, pt, 4, little);
        pt -= 36; // HACK

        numBytes = input.length - pt;
        raw2 = new byte[numBytes];
        System.arraycopy(input, pt, raw2, 0, raw2.length);
      }
    }

    if (raw == null) raw = input;

    // "Because Motion-JPEG format B does not support markers, the JPEG
    // bitstream does not have null bytes (0x00) inserted after data bytes
    // that are set to 0xFF."
    // Thus quoth the specifications.

    ByteVector b = new ByteVector();
    for (int i=0; i<raw.length; i++) {
      b.add((byte) raw[i]);
      if (raw[i] == (byte) 0xff) {
        b.add((byte) 0x00);
      }
    }

    if (raw2 == null) raw2 = new byte[0];
    ByteVector b2 = new ByteVector();
    for (int i=0; i<raw2.length; i++) {
      b2.add((byte) raw2[i]);
      if (raw2[i] == (byte) 0xff) {
        b2.add((byte) 0x00);
      }
    }

    // assemble a fake JFIF plane

    ByteVector v = new ByteVector(1000);
    v.add(HEADER);

    // add quantization tables

    v.add(new byte[] {(byte) 0xff, (byte) 0xdb});

    int length = LUM_QUANT.length + CHROM_QUANT.length + 4;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    v.add((byte) 0x00);
    v.add(LUM_QUANT);
    v.add((byte) 0x01);
    v.add(CHROM_QUANT);

    // add Huffman tables

    v.add(new byte[] {(byte) 0xff, (byte) 0xc4});

    length = LUM_DC_BITS.length + LUM_DC.length + CHROM_DC_BITS.length +
      CHROM_DC.length + LUM_AC_BITS.length + LUM_AC.length +
      CHROM_AC_BITS.length + CHROM_AC.length + 6;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    // the ordering of these tables matters

    v.add((byte) 0x00);
    v.add(LUM_DC_BITS);
    v.add(LUM_DC);

    v.add((byte) 0x01);
    v.add(CHROM_DC_BITS);
    v.add(CHROM_DC);

    v.add((byte) 0x10);
    v.add(LUM_AC_BITS);
    v.add(LUM_AC);

    v.add((byte) 0x11);
    v.add(CHROM_AC_BITS);
    v.add(CHROM_AC);

    // add start-of-frame header

    v.add((byte) 0xff);
    v.add((byte) 0xc0);

    length = (bitsPerPixel >= 40) ? 11 : 17;

    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    int fieldHeight = height;
    if (interlaced) fieldHeight /= 2;
    if (height % 2 == 1) fieldHeight++;

    int c = bitsPerPixel == 24 ? 3 : (bitsPerPixel == 32 ? 4 : 1);

    v.add(bitsPerPixel >= 40 ? (byte) (bitsPerPixel - 32) :
      (byte) (bitsPerPixel / c));  // bits per sample
    v.add((byte) ((fieldHeight >>> 8) & 0xff));
    v.add((byte) (fieldHeight & 0xff));
    v.add((byte) ((width >>> 8) & 0xff));
    v.add((byte) (width & 0xff));
    v.add((bitsPerPixel >= 40) ? (byte) 0x01 : (byte) 0x03);

    // channel information

    v.add((byte) 0x01); // channel id
    v.add((byte) 0x21); // sampling factors
    v.add((byte) 0x00); // quantization table number

    if (bitsPerPixel < 40) {
      v.add((byte) 0x02);
      v.add((byte) 0x11);
      v.add((byte) 0x01);
      v.add((byte) 0x03);
      v.add((byte) 0x11);
      v.add((byte) 0x01);
    }

    // add start-of-scan header

    v.add((byte) 0xff);
    v.add((byte) 0xda);

    length = (bitsPerPixel >= 40) ? 8 : 12;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    // number of channels
    v.add((bitsPerPixel >= 40) ? (byte) 0x01 : (byte) 0x03);
    v.add((byte) 0x01);  // channel id
    v.add((byte) 0x00);  // DC and AC table numbers

    if (bitsPerPixel < 40) {
      v.add((byte) 0x02);  // channel id
      v.add((byte) 0x01);  // DC and AC table numbers
      v.add((byte) 0x03);  // channel id
      v.add((byte) 0x01);  // DC and AC table numbers
    }

    v.add((byte) 0x00);
    v.add((byte) 0x3f);
    v.add((byte) 0x00);

    // as if everything we had to do up to this point wasn't enough of a pain,
    // the MJPEG-B specifications allow for interlaced frames
    // so now we have to reorder the scanlines...*stabs self in eye*

    if (interlaced) {
      ByteVector v2 = new ByteVector(v.size());
      v2.add(v.toByteArray());

      v.add(b.toByteArray());
      v.add((byte) 0xff);
      v.add((byte) 0xd9);
      v2.add(b2.toByteArray());
      v2.add((byte) 0xff);
      v2.add((byte) 0xd9);

      BufferedImage top = bufferedJPEG(v.toByteArray());
      BufferedImage bottom = bufferedJPEG(v2.toByteArray());

      BufferedImage result = null;

      if (flip) {
        result = new BufferedImage(top.getHeight() + bottom.getHeight(),
          top.getWidth(), top.getType());

        int topCount = 0;
        int bottomCount = 0;

        for (int i=0; i<result.getWidth(); i++) {
          if (i % 2 == 0) {
            for (int j=0; j<result.getHeight(); j++) {
              result.setRGB((result.getWidth() - i - 1), j,
                top.getRGB(j, topCount));
            }
            topCount++;
          }
          else {
            for (int j=0; j<result.getHeight(); j++) {
              result.setRGB((result.getWidth() - i - 1), j,
                bottom.getRGB(j, bottomCount));
            }
            bottomCount++;
          }
        }
      }
      else {
        result = new BufferedImage(top.getWidth(),
          top.getHeight() + bottom.getHeight(), top.getType());

        int topCount = 0;
        int bottomCount = 0;

        for (int i=0; i<result.getHeight(); i++) {
          if (i % 2 == 0) {
            for (int j=0; j<result.getWidth(); j++) {
              result.setRGB(j, i, top.getRGB(j, topCount));
            }
            topCount++;
          }
          else {
            for (int j=0; j<result.getWidth(); j++) {
              result.setRGB(j, i, bottom.getRGB(j, bottomCount));
            }
            bottomCount++;
          }
        }
      }

      return result;
    }
    else {
      v.add(b.toByteArray());
      v.add((byte) 0xff);
      v.add((byte) 0xd9);
      return bufferedJPEG(v.toByteArray());
    }
  }

  /** Uncompresses a JPEG compressed image plane. */
  public BufferedImage bufferedJPEG(byte[] input) throws FormatException {
    // some planes have a 16 byte header that needs to be removed
    if (input[0] != (byte) 0xff || input[1] != (byte) 0xd8) {
      byte[] temp = input;
      input = new byte[temp.length - 16];
      System.arraycopy(temp, 16, input, 0, input.length);
    }

    try {
      return ImageIO.read(new ByteArrayInputStream(input));
    }
    catch (IOException e) {
      throw new FormatException("Invalid JPEG stream");
    }
  }

  /** Uncompresses a QT RLE compressed image plane. */
  public byte[] rleUncompress(byte[] input) throws FormatException, IOException
  {
    if (input.length < 8) return prevPixels;

    // read the 4 byte chunk length; this should equal input.length

    // now read the header
    // if the header is 0, we uncompress the remaining bytes
    // otherwise, read extra header information to determine which
    // scanlines to uncompress

    int header = DataTools.bytesToInt(input, 4, 2, little);
    int off = 0; // offset into output
    int start = 0;
    int pt = 6;
    int numLines = height;
    int ebpp = bitsPerPixel / 8;  // effective bytes per pixel
    if (ebpp == 1 || ebpp == 2) ebpp *= 3;
    else if (ebpp >= 5) ebpp %= 4;
    byte[] output = new byte[width * height * ebpp];

    if ((header & 0x0008) == 0x0008) {
      start = DataTools.bytesToInt(input, pt, 2, little);
      // skip 2 bytes
      pt += 4;
      numLines = DataTools.bytesToInt(input, pt, 2, little);
      // skip 2 bytes
      pt += 4;

      // copy appropriate lines from prevPixels

      if (canUsePrevious) {
        for (int i=0; i<start; i++) {
          off = i * width * ebpp;
          System.arraycopy(prevPixels, off, output, off, width * ebpp);
        }
      }
      off += (width * ebpp);

      if (canUsePrevious) {
        for (int i=(start+numLines); i<height; i++) {
          int offset = i * width * ebpp;
          System.arraycopy(prevPixels, offset, output, offset,
            width * ebpp);
        }
      }
    }
    else throw new FormatException("Unsupported header : " + header);

    // uncompress the remaining scanlines

    int skip = 0; // number of bytes to skip
    byte rle = 0; // RLE code

    int rowPointer = start * (width * ebpp);

    for (int i=0; i<numLines; i++) {
      skip = input[pt];
      if (skip < 0) skip += 256;

      if (canUsePrevious) {
        try {
          System.arraycopy(prevPixels, rowPointer, output, rowPointer,
            (skip-1) * ebpp);
        }
        catch (ArrayIndexOutOfBoundsException e) { }
      }

      off = rowPointer + ((skip-1) * ebpp);
      pt++;
      while (true) {
        rle = input[pt];
        pt++;

        if (rle == 0) {
          skip = input[pt];

          if (canUsePrevious) {
            try {
              System.arraycopy(prevPixels, off, output, off,
                (skip-1) * ebpp);
            }
            catch (ArrayIndexOutOfBoundsException e) { }
          }

          off += ((skip-1) * ebpp);
          pt++;
        }
        else if (rle == -1) {
          // make sure we copy enough pixels to fill the line

          if (off < (rowPointer + (width * ebpp)) && canUsePrevious) {
            System.arraycopy(prevPixels, off, output, off,
              (rowPointer + (width * ebpp)) - off);
          }

          break;
        }
        else if (rle < -1) {
          // unpack next pixel and copy it to output -(rle) times
          for (int j=0; j<(-1*rle); j++) {
            if (off < output.length) {
              System.arraycopy(input, pt, output, off, ebpp);
              off += ebpp;
            }
            else j = (-1*rle);
          }
          pt += ebpp;
        }
        else {
          // copy (rle) pixels to output
          System.arraycopy(input, pt, output, off, rle*ebpp);
          pt += rle*ebpp;
          off += rle*ebpp;
        }
      }
      rowPointer += (width * ebpp);
    }
    return output;
  }

  /** Cut off header bytes from a resource fork file. */
  private void stripHeader() throws IOException {
    // seek to 4 bytes before first occurence of 'moov'

    String test = null;
    boolean found = false;
    byte[] b = new byte[4];
    while (!found && in.getFilePointer() < (in.length() - 4)) {
      in.read(b);
      test = new String(b);
      if (test.equals("moov")) {
        found = true;
        in.seek(in.getFilePointer() - 8);
      }
      else in.seek(in.getFilePointer() - 3);
    }
  }

  /** Creates a legacy QT reader. */
  private LegacyQTReader createLegacyReader() {
    // use the same id mappings that this reader does
    return new LegacyQTReader();
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new QTReader().testRead(args);
  }

}
