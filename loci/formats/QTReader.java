//
// QTReader.java
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

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.Vector;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Currently, compressed movies are not supported, although video codecs will
 * be added as time permits.
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
  public Image open(String id, int no) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (!codec.equals("raw ")) {
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

    byte[] pixs = new byte[nextOffset - offset];

    for (int i=0; i<pixs.length; i++) {
      pixs[i] = pixels[offset + i];
    }

    byte[] bytes = uncompress(pixs, codec);
    prevPixels = bytes;

    // determine whether we need to strip out any padding bytes

    int pad = width % 4;
    pad = (4 - pad) % 4;

    if (pad > 0) {
      bytes = new byte[prevPixels.length - height*pad];

      for (int row=0; row<height; row++) {
        System.arraycopy(prevPixels, row*(width+pad), bytes, row*width, width);
      }
    }

    if (bitsPerPixel <= 8 || bitsPerPixel == 40) {
      if (bitsPerPixel == 40) {
        // invert the pixels
        for (int i=0; i<bytes.length; i++) {
          bytes[i] = (byte) (255 - bytes[i]);
        }
      }

      return ImageTools.makeImage(bytes, width, height, 1, false);
    }
    else if (bitsPerPixel == 16) {
      short[] newPix = new short[bytes.length / 2 + 1];
      for (int i=0; i<bytes.length; i+=2) {
        newPix[i/2] = DataTools.bytesToShort(bytes, i, little);
      }
      return ImageTools.makeImage(newPix, width, height, 1, false);
    }
    else if (bitsPerPixel == 24) {
      byte[][] newPix = new byte[3][pixs.length / 3];
      for (int j=0; j<newPix[0].length; j++) {
        for (int i=0; i<newPix.length; i++) {
          newPix[i][j] = pixs[3*j + i];
        }
      }
      return ImageTools.makeImage(newPix, width, height);
    }
    else if (bitsPerPixel == 32) {
      int[] newPix = new int[bytes.length / 4];
      for (int i=0; i<bytes.length; i+=4) {
        newPix[i/4] = DataTools.bytesToInt(bytes, i, little);
      }
      return ImageTools.makeImage(newPix, width, height, 1, false);
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
              offsets.add(new Integer(off + i*rawSize));
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
          bitsPerPixel = DataTools.bytesToInt(data, 90, 2, little);
        }
        else if (atomType.equals("stsz")) {
          // found the number of planes
          rawSize = DataTools.bytesToInt(data, 4, 4, little);
          numImages = DataTools.bytesToInt(data, 8, 4, little);
        }
      }

      if (atomSize == 0) offset = in.length();
      else offset += atomSize;

      // if a 'udta' atom, skip ahead 4 bytes
      if (atomType.equals("udta")) offset += 4;
      //print(depth, atomSize, atomType, data);
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
  public byte[] uncompress(byte[] pixs, String code) throws FormatException {
    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) return rleUncompress(pixs);
    else {
      throw new FormatException("Sorry, " + codec + " codec is not supported");
    }
  }

  /** Uncompresses a QT RLE compressed image plane. */
  public byte[] rleUncompress(byte[] pixs) throws FormatException {
    // -- TODO -- broken

    /* debug */
    // print out the first 30 bytes
    System.out.println();
    for (int i=0; i<30; i++) {
      System.out.print(pixs[i] + " ");
    }
    System.out.println();

    int pt = 8;
    long size = DataTools.bytesToInt(pixs, pt, 4, little);
    pt += 4;
    if (size < 0) size += 4294967296L;

    // this frame is the same as the one before it
    if (size < 8) return prevPixels;

    int header = DataTools.bytesToInt(pixs, pt, 2, little);
    pt += 2;

    Vector output = new Vector();

    if (header == 0) {
      int skip = 1;  // always one more than the number of bytes to skip
      while (skip != 0) {
        skip = pixs[pt];
        pt++;
        int rleCode = 0;

        while (rleCode != -1) {
          if (skip == 0) break;  // finished decoding
          pt += skip;
          rleCode = pixs[pt];
          pt++;

          if (rleCode > 0) {
            // copy the pixels directly to output buffer
            for (int i=0; i<rleCode; i++) {
              output.add(new Byte(pixs[pt]));
              pt++;
            }
          }
          else if (rleCode < -1) {
            // grab next byte and repeat it -(rleCode) times
            for (int i=0; i<-1*rleCode; i++) {
              output.add(new Byte(pixs[pt]));
            }
          }
          else if (rleCode == 0) {
            skip = pixs[pt];
            pt++;
          }
        }
      }
    }
    else {
      throw new FormatException("Sorry, unsupported header : " + header);
    }

    byte[] toRtn = new byte[output.size()];
    for (int i=0; i<toRtn.length; i++) {
      toRtn[i] = ((Byte) output.get(i)).byteValue();
    }
    return toRtn;
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new QTReader().testRead(args);
  }

}
