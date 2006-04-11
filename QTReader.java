//
// QTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.ByteArrayInputStream;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Additional video codecs will be added as time permits.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class QTReader extends FormatReader {

  // -- Constants --

  /** List of identifiers for each container atom. */
  private static final String[] CONTAINER_TYPES = {
    "moov", "trak", "udta", "tref", "imap", "mdia", "minf", "stbl", "edts",
    "mdra", "rmra", "imag", "vnrp", "dinf"
  };

  /** Some MJPEG-B stuff. */

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
  private RandomAccessFile in;

  /** Flag indicating whether the current file is little endian. */
  private boolean little = false;

  /** Number of images in the file. */
  private int numImages;

  /** Pixel data stored in "mdat" atom. */
  private byte[] pixels;

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

  /** Video codec used by this movie. */
  private String codec;

  /** An instance of the old QuickTime reader, in case this one fails. */
  private LegacyQTReader legacy;

  /** Amount to subtract from each offset. */
  private int scale;

  /** Number of bytes in each plane. */
  private Vector chunkSizes;

  /** Set to true if the scanlines in a plane are interlaced (mjpb only). */
  private boolean interlaced;

  // -- Constructor --

  /** Constructs a new QuickTime reader. */
  public QTReader() { super("QuickTime", "mov"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a QuickTime file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given QuickTime file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if(!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given QuickTime file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (!codec.equals("raw ") && !codec.equals("rle ") &&
      !codec.equals("jpeg") && !codec.equals("mjpb"))
    {
      if (DEBUG) {
        System.out.println("Unsupported codec (" +
          codec + "); using QTJava reader");
      }
      if (legacy == null) legacy = new LegacyQTReader();
      return legacy.open(id, no);
    }

    int offset = ((Integer) offsets.get(no)).intValue();
    int nextOffset = pixels.length;

    if (no == 0) {
      scale = offset;
    }

    offset -= scale;

    if (no < offsets.size() - 1) {
      nextOffset = ((Integer) offsets.get(no+1)).intValue();
      nextOffset -= scale;
    }

    if ((nextOffset - offset) < 0) {
      int temp = offset;
      offset = nextOffset;
      nextOffset = offset;
    }

    byte[] pixs = new byte[nextOffset - offset];

    for (int i=0; i<pixs.length; i++) {
      if ((offset + i) < pixels.length) {
        pixs[i] = pixels[offset + i];
      }
    }

    byte[] bytes = uncompress(pixs, codec);
    prevPixels = bytes;

    // determine whether we need to strip out any padding bytes

    int pad = width % 4;
    pad = (4 - pad) % 4;

    if (width * height * (bitsPerPixel / 8) == prevPixels.length) {
      pad = 0;
    }

    if (!codec.equals("mjpb")) {
      if (pad > 0) {
        bytes = new byte[prevPixels.length - height*pad];

        for (int row=0; row<height; row++) {
          System.arraycopy(prevPixels, row*(width+pad), bytes,
            row*width, width);
        }
      }
    }

    if ((bitsPerPixel == 40) && !codec.equals("mjpb")) {
      // invert the pixels
      for (int i=0; i<bytes.length; i++) {
        bytes[i] = (byte) (255 - bytes[i]);
      }

      return ImageTools.makeImage(bytes, width, height, 1, false);
    }
    else if (bitsPerPixel == 40) {
      return ImageTools.makeImage(bytes, width, height, 1, false);
    }
    else if (bitsPerPixel == 8) {
      // invert the pixels
      for (int i=0; i<bytes.length; i++) {
        bytes[i] = (byte) (255 - bytes[i]);
      }
      return ImageTools.makeImage(bytes, width, height, 3, false);
    }
    else if (bitsPerPixel == 16) {
      short[] newPix = new short[bytes.length / 2 + 1];
      for (int i=0; i<bytes.length; i+=2) {
        newPix[i/2] = DataTools.bytesToShort(bytes, i, little);
      }
      return ImageTools.makeImage(newPix, width, height, 1, false);
    }
    else if (bitsPerPixel == 24) {
      return ImageTools.makeImage(bytes, width, height, 3, true);
    }
    else if (bitsPerPixel == 32) {
      // strip out alpha channel
      byte[][] data = new byte[3][bytes.length / 4];
      for (int i=0; i<data[0].length; i++) {
        data[0][i] = bytes[4*i + 1];
        data[1][i] = bytes[4*i + 2];
        data[2][i] = bytes[4*i + 3];
      }

      return ImageTools.makeImage(data, width, height);
    }
    else {
      if (legacy == null) legacy = new LegacyQTReader();
      return legacy.open(id, no);
    }
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given QuickTime file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");
    offsets = new Vector();
    chunkSizes = new Vector();
    parse(0, 0, in.length());
    numImages = offsets.size();
  }


  // -- Helper methods --

  /** Parse all of the atoms in the file. */
  public void parse(int depth, long offset, long length) throws IOException {
    while (offset < length) {
      in.seek(offset);
      // first 4 bytes are the atom size
      long atomSize = DataTools.read4UnsignedBytes(in, little);

      // read the atom type
      byte[] four = new byte[4];
      in.read(four);
      String atomType = new String(four);

      // if atomSize is 1, then there is an 8 byte extended size
      if (atomSize == 1) {
        atomSize = DataTools.read8SignedBytes(in, little);
      }

      if (atomSize < 0) {
        System.out.println("Invalid atom size : " + atomSize);
      }

      byte[] data = new byte[0];

      // if this is a container atom, parse the children
      if (isContainer(atomType)) {
        parse(depth++, in.getFilePointer(), offset + atomSize);
      }
      else {
        if (atomSize == 0) atomSize = in.length();
        data = new byte[(int) atomSize];
        in.read(data);

        if (atomType.equals("mdat")) {
          // we've found the pixel data
          pixels = data;
        }
        else if (atomType.equals("tkhd")) {
          // we've found the dimensions

          int off = 74;
          width = DataTools.bytesToInt(data, off, little);
          off += 4;
          height = DataTools.bytesToInt(data, off, little);
        }
        else if (atomType.equals("stco")) {
          // we've found the plane offsets

          int numPlanes = DataTools.bytesToInt(data, 4, little);
          if (numPlanes != numImages) {
            int off = DataTools.bytesToInt(data, 8, little);
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
            int j = 8;
            for (int i=0; i<numPlanes; i++) {
              offsets.add(new Integer(DataTools.bytesToInt(data, j, little)));
              j += 4;
            }
          }
        }
        else if (atomType.equals("stsd")) {
          // found video codec and pixel depth information
          codec = new String(data, 12, 4);
          int fieldsPerPlane = DataTools.bytesToInt(data, 102, 1, little);
          interlaced = fieldsPerPlane == 2;
          bitsPerPixel = DataTools.bytesToInt(data, 90, 2, little);
          metadata.put("Codec", codec);
          metadata.put("Bits per pixel", new Integer(bitsPerPixel));
        }
        else if (atomType.equals("stsz")) {
          // found the number of planes
          rawSize = DataTools.bytesToInt(data, 4, 4, little);
          numImages = DataTools.bytesToInt(data, 8, 4, little);

          if (rawSize == 0) {
            for (int b=0; b<numImages; b++) {
              chunkSizes.add(new Integer(DataTools.bytesToInt(
                data, 8+b*4, 4, little)));
            }
          }
        }
      }

      if (atomSize == 0) offset = in.length();
      else offset += atomSize;

      // if a 'udta' atom, skip ahead 4 bytes
      if (atomType.equals("udta")) offset += 4;
      if (DEBUG) print(depth, atomSize, atomType, data);
    }
  }

  /** Checks if the given String is a container atom type. */
  public boolean isContainer(String type) {
    for(int i=0; i<CONTAINER_TYPES.length; i++) {
      if(type.equals(CONTAINER_TYPES[i])) return true;
    }
    return false;
  }

  /** Debugging method; prints information on an atom. */
  public void print(int depth, long size, String type, byte[] data) {
    for (int i=0; i<depth; i++) System.out.print(" ");
    System.out.print(type + " : [" + size + "]\n");
  }

  /** Uncompresses an image plane according to the the codec identifier. */
  public byte[] uncompress(byte[] pixs, String code)
    throws FormatException
  {
    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) return rleUncompress(pixs);
    else if (code.equals("jpeg")) return jpegUncompress(pixs);
    else if (code.equals("mjpb")) return mjpbUncompress(pixs);
    else {
      throw new FormatException("Sorry, " + codec + " codec is not supported");
    }
  }

  /** Uncompresses a MJPEG-B compressed image plane. */
  public byte[] mjpbUncompress(byte[] input) throws FormatException {
    byte[] raw = null;
    byte[] raw2 = null;
    int pt = 16; // pointer into the compressed data

    // official documentation at
    // http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/
    // chapter_4_section_2.html

    // information on SOF, SOS, and SOD headers found at
    // http://www.obrador.com/essentialjpeg/headerinfo.htm

    // a brief overview for those who would rather not read the specifications:
    // MJPEB-B (or Motion JPEG) is a variant of the JPEG compression standard,
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

    pt += 4;
    if (pt >= input.length) {
      pt = 0;
    }

    // most MJPEG-B planes don't have this identifier
    if (!(input[pt] != 'm' || input[pt+1] != 'j' || input[pt+2] != 'p' ||
      input[pt+3] != 'g'))
    {
      pt += 4;

      // number of compressed bytes (minus padding)
      int size = DataTools.bytesToInt(input, pt, 4, little);
      pt += 4;

      // number of compressed bytes (including padding)
      int padSize = DataTools.bytesToInt(input, pt, 4, little);
      pt += 4;

      // offset to second field
      int offset = DataTools.bytesToInt(input, pt, 4, little) + 16;
      pt += 4;

      // offset to quantization table
      int quantOffset = DataTools.bytesToInt(input, pt, 4, little) + 16;
      pt += 4;

      // offset to Huffman table
      int huffmanOffset = DataTools.bytesToInt(input, pt, 4, little) + 16;
      pt += 4;

      // offset to start of frame
      int sof = DataTools.bytesToInt(input, pt, 4, little) + 16;
      pt += 4;

      // offset to start of scan
      int sos = DataTools.bytesToInt(input, pt, 4, little) + 16;
      pt += 4;

      // offset to start of data
      int sod = DataTools.bytesToInt(input, pt, 4, little) + 16;
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

      raw = new byte[padSize];
      if ((pt + padSize) >= input.length) padSize = input.length - pt - 1;
      System.arraycopy(input, pt, raw, 0, padSize);

      // get the second field
      // from the specs:
      // "Each QuickTime sample consists of two distinct compressed images,
      // each coding one field: the field with the topmost scan-line, T, and
      // the other field, B. Each field is half the height of the overall
      // image, as declared in the height field of the sample description.
      // To be precise, if the height field contains the value H, then the
      // field T has ((H+1) div 2) lines, and field B has (H div 2) lines."

      if (offset != 0) {
        pt = offset + 28;
        pt += DataTools.bytesToInt(input, pt, 4, little) + 4;
        raw2 = new byte[input.length - pt];
        System.arraycopy(input, pt, raw2, 0, raw2.length);
      }
    }

    if (raw == null) raw = input;

    // "Because Motion-JPEG format B does not support markers, the JPEG
    // bitstream does not have null bytes (0x00) inserted after data bytes
    // that are set to 0xFF."
    // Thus quoth the specifications.

    ByteVector b = new ByteVector();
    int tempPt = 0;
    for (int i=0; i<raw.length; i++) {
      b.add((byte) raw[i]);
      if (raw[i] == (byte) 0xff) {
        b.add((byte) 0x00);
      }
    }

    if (raw2 == null) raw2 = new byte[0];
    ByteVector b2 = new ByteVector();
    tempPt = 0;
    for (int i=0; i<raw2.length; i++) {
      b2.add((byte) raw2[i]);
      if (raw2[i] == (byte) 0xff) {
        b2.add((byte) 0x00);
      }
    }

    // assemble a fake JFIF plane
    // once again, allow me to say that this would be much easier if the Apple
    // developers had stuck to the JPEG specifications...*sigh*

    ByteVector v = new ByteVector(1000);
    v.add(HEADER);

    // add quantization tables

    v.add((byte) 0xff);
    v.add((byte) 0xdb);

    int length = LUM_QUANT.length + CHROM_QUANT.length + 4;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    v.add((byte) 0x00);
    v.add(LUM_QUANT);
    v.add((byte) 0x01);
    v.add(CHROM_QUANT);

    // add Huffman tables

    v.add((byte) 0xff);
    v.add((byte) 0xc4);

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

    length = 11;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    v.add((byte) 0x08); // bits per sample
    v.add((byte) ((height >>> 8) & 0xff));
    v.add((byte) (height & 0xff));
    v.add((byte) ((width >>> 8) & 0xff));
    v.add((byte) (width & 0xff));
    v.add((byte) 0x01);

    // channel information

    v.add((byte) 0x01); // channel id
    v.add((byte) 0x21); // sampling factors
    v.add((byte) 0x00); // quantization table number

    // add start-of-scan header

    v.add((byte) 0xff);
    v.add((byte) 0xda);

    length = 8;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    v.add((byte) 0x01);  // number of channels
    v.add((byte) 0x01);  // channel id
    v.add((byte) 0x00);  // DC and AC table numbers

    v.add((byte) 0x00);
    v.add((byte) 0x3f);
    v.add((byte) 0x00);

    v.add(b.toByteArray());
    v.add((byte) 0xff);
    v.add((byte) 0xd9);

    byte[] scanlines = jpegUncompress(v.toByteArray());

    // as if everything we had to do up to this point wasn't enough of a pain,
    // the MJPEG-B specifications allow for interlaced frames
    // so now we have to reorder the scanlines...*stabs self in eye*

    if (interlaced) {
      byte[] top = new byte[width * (height / 2)];
      byte[] bottom = new byte[width * (height / 2)];
      System.arraycopy(scanlines, 0, top, 0, top.length);
      System.arraycopy(scanlines, top.length, bottom, 0, bottom.length);

      scanlines = new byte[scanlines.length];

      int topLine = 0;
      int bottomLine = 0;
      for (int i=0; i<height; i++) {
        if ((i % 2) == 0) {
          System.arraycopy(top, topLine*width, scanlines, width*i, width);
          topLine++;
        }
        else {
          System.arraycopy(bottom, bottomLine*width, scanlines, width*i, width);
          bottomLine++;
        }
      }
    }
    return scanlines;
  }

  /** Uncompresses a JPEG compressed image plane. */
  public byte[] jpegUncompress(byte[] input) throws FormatException {
    // too lazy to write native JPEG support, so use ImageIO

    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(input));

      byte[][] bytes = ImageTools.getBytes(img);

      byte[] output = new byte[bytes.length * bytes[0].length];

      for (int i=0; i<bytes[0].length; i++) {
        for (int j=0; j<bytes.length; j++) {
          output[bytes.length*i + j] = bytes[j][i];
        }
      }
      return output;
    }
    catch (IOException e) {
      throw new FormatException("Invalid JPEG stream");
    }
  }

  /** Uncompresses a QT RLE compressed image plane. */
  public byte[] rleUncompress(byte[] input) throws FormatException {
    if (input.length < 8) return prevPixels;

    // read the 4 byte chunk length; this should equal input.length

    int length = DataTools.bytesToInt(input, 0, 4, little);

    // now read the header
    // if the header is 0, we uncompress the remaining bytes
    // otherwise, read extra header information to determine which
    // scanlines to uncompress

    int header = DataTools.bytesToInt(input, 4, 2, little);
    int off = 0; // offset into output
    int start = 0;
    int pt = 6;
    int numLines = height;
    byte[] output = new byte[width * height * (bitsPerPixel / 8)];

    if ((header & 0x0008) == 0x0008) {
      start = DataTools.bytesToInt(input, pt, 2, little);
      // skip 2 bytes
      pt += 4;
      numLines = DataTools.bytesToInt(input, pt, 2, little);
      // skip 2 bytes
      pt += 4;

      // copy appropriate lines from prevPixels

      for (int i=0; i<start; i++) {
        off = i * width * (bitsPerPixel / 8);
        System.arraycopy(prevPixels, off, output, off,
          width * (bitsPerPixel / 8));
      }
      off += (width * (bitsPerPixel / 8));

      for (int i=(start+numLines); i<height; i++) {
        int offset = i * width * (bitsPerPixel / 8);
        System.arraycopy(prevPixels, offset, output, offset,
          width * (bitsPerPixel / 8));
      }
    }
    else throw new FormatException("Unsupported header : " + header);

    // uncompress the remaining scanlines

    byte skip = 0; // number of bytes to skip
    byte rle = 0; // RLE code

    int rowPointer = start * (width * (bitsPerPixel / 8));


    for (int i=0; i<numLines; i++) {
      skip = input[pt];

      if (prevPixels != null) {
        try {
          System.arraycopy(prevPixels, off, output, off,
            (skip - 1) * (bitsPerPixel / 8));
        }
        catch (ArrayIndexOutOfBoundsException e) { }
      }

      off = rowPointer + ((skip - 1) * (bitsPerPixel / 8));
      pt++;
      while (true) {
        rle = input[pt];
        pt++;

        if (rle == 0) {
          skip = input[pt];

          if (prevPixels != null) {
            try {
              System.arraycopy(prevPixels, off, output, off,
                (skip - 1) * (bitsPerPixel / 8));
            }
            catch (ArrayIndexOutOfBoundsException e) { }
          }

          off += ((skip - 1) * (bitsPerPixel / 8));
          pt++;
        }
        else if (rle == -1) break;
        else if (rle < -1) {
          // unpack next pixel and copy it to output -(rle) times

          for (int j=0; j<(-1*rle); j++) {
            if (off < output.length) {
              System.arraycopy(input, pt, output, off, bitsPerPixel / 8);
              off += (bitsPerPixel / 8);
            }
            else j = (-1*rle);
          }
          pt += (bitsPerPixel / 8);
        }
        else {
          // copy (rle) pixels to output

          System.arraycopy(input, pt, output, off, rle*(bitsPerPixel / 8));
          pt += rle*(bitsPerPixel / 8);
          off += rle*(bitsPerPixel / 8);
        }
      }
      rowPointer += (width * (bitsPerPixel / 8));
    }

    return output;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new QTReader().testRead(args);
  }

}
