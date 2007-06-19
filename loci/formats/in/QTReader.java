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
import java.util.zip.*;
import javax.imageio.ImageIO;
import loci.formats.*;
import loci.formats.codec.ByteVector;

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

  // -- Fields --

  /** Offset to start of pixel data. */
  private int pixelOffset;

  /** Total number of bytes of pixel data. */
  private int pixelBytes;

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

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 1);
    super.setMetadataStore(store);
    if (useLegacy) legacy.setMetadataStore(store);
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    String code = codec;
    if (no >= getImageCount() - altPlanes) code = altCodec;

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
      legacy.setId(currentId);
      return legacy.openBytes(no);
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

    canUsePrevious = (prevPixels != null) && (prevPlane == no - 1) &&
      !code.equals(altCodec);

    if (code.equals("jpeg") || code.equals("mjpb")) {
      byte[] s = ImageTools.getBytes(openImage(no), false, no);
      return s;
    }

    byte[] bytes = null;
    if (!code.equals("rpza")) bytes = uncompress(pixs, code);
    else {
      bytes = rpzaUncompress(pixs, canUsePrevious ? prevPixels : null);

      for (int i=0; i<bytes.length; i++) {
        bytes[i] = (byte) (255 - bytes[i]);
      }
      prevPlane = no;
      return bytes;
    }

    // on rare occassions, we need to trim the data
    if (canUsePrevious && (prevPixels.length < bytes.length)) {
      byte[] temp = bytes;
      bytes = new byte[prevPixels.length];
      System.arraycopy(temp, 0, bytes, 0, bytes.length);
    }

    prevPixels = bytes;
    prevPlane = no;

    // determine whether we need to strip out any padding bytes

    int pad = core.sizeX[0] % 4;
    pad = (4 - pad) % 4;

    int size = core.sizeX[0] * core.sizeY[0];
    if (size * (bitsPerPixel / 8) == prevPixels.length) pad = 0;

    if (pad > 0) {
      bytes = new byte[prevPixels.length - core.sizeY[0]*pad];

      for (int row=0; row<core.sizeY[0]; row++) {
        System.arraycopy(prevPixels, row*(core.sizeX[0]+pad), bytes,
          row*core.sizeX[0], core.sizeX[0]);
      }
    }

    if (bitsPerPixel == 40 || bitsPerPixel == 8) {
      // invert the pixels
      for (int i=0; i<bytes.length; i++) {
        bytes[i] = (byte) (255 - bytes[i]);
      }
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
      return rtn;
    }
    else {
      return bytes;
    }
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    String code = codec;
    if (no >= getImageCount() - altPlanes) code = altCodec;

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
      legacy.setId(currentId);
      return legacy.openImage(no);
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
      b = ImageTools.makeImage(openBytes(no), core.sizeX[0],
        core.sizeY[0], core.sizeC[0], false, bpp, core.littleEndian[0]);
    }
    return b;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    prevPixels = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("QTReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    spork = true;
    offsets = new Vector();
    chunkSizes = new Vector();
    status("Parsing tags");

    try {
      parse(0, 0, in.length());
    }
    catch (Exception exc) {
      if (debug) trace(exc);
      useLegacy = true;
      legacy = createLegacyReader();
      legacy.setId(id);
    }

    core.imageCount[0] = offsets.size();

    status("Populating metadata");

    int bytesPerPixel = bitsPerPixel / 8;
    bytesPerPixel %= 4;

    switch (bytesPerPixel) {
      case 0:
      case 1:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 2:
        core.pixelType[0] = FormatTools.INT16;
        break;
      case 3:
        core.pixelType[0] = FormatTools.UINT8;
        break;
    }

    /*
    if (flip) {
      int tmp = core.sizeX[0];
      core.sizeX[0] = core.sizeY[0];
      core.sizeY[0] = tmp;
    }
    */

    core.rgb[0] = bitsPerPixel < 40;
    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? 3 : 1;
    core.sizeT[0] = core.imageCount[0];
    core.currentOrder[0] = "XYCZT";
    core.littleEndian[0] = false;
    core.interleaved[0] = true;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]),
      core.currentOrder[0],
      null,
      null);
    for (int i=0; i<core.sizeC[0]; i++) {
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
        core.imageCount[0] = offsets.size();
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
          core.imageCount[0] = offsets.size();
          return;
        }
        else {
          f = new Location(base + "/rsrc");
          if (f.exists()) {
            in = new RandomAccessStream(f.getAbsolutePath());
            stripHeader();
            parse(0, 0, in.length());
            core.imageCount[0] = offsets.size();
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
      core.imageCount[0] = 0;
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
            core.imageCount[0]++;
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
  private void parse(int depth, long offset, long length)
    throws FormatException, IOException
  {
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
        LogTools.println("QTReader: invalid atom size: " + atomSize);
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

          core.sizeX[0] = in.readInt();
          core.sizeY[0] = in.readInt();
        }
        else if (atomType.equals("cmov")) {
          in.skipBytes(8);
          byte[] b = new byte[4];
          in.read(b);
          if ("zlib".equals(new String(b))) {
            atomSize = in.readInt();
            in.skipBytes(4);
            int uncompressedSize = in.readInt();

            b = new byte[(int) (atomSize - 12)];
            in.read(b);

            Inflater inf = new Inflater();
            inf.setInput(b, 0, b.length);
            byte[] output = new byte[uncompressedSize];
            try {
              inf.inflate(output);
            }
            catch (DataFormatException exc) {
              if (debug) trace(exc);
              throw new FormatException("Compressed header not supported.");
            }
            inf.end();

            RandomAccessStream oldIn = in;
            in = new RandomAccessStream(output);
            parse(0, 0, output.length);
            in.close();
            in = oldIn;
          }
          else throw new FormatException("Compressed header not supported.");
        }
        else if (atomType.equals("stco")) {
          // we've found the plane offsets

          spork = false;
          in.readInt();
          int numPlanes = in.readInt();
          if (numPlanes != core.imageCount[0]) {
            in.seek(in.getFilePointer() - 4);
            int off = in.readInt();
            offsets.add(new Integer(off));
            for (int i=1; i<core.imageCount[0]; i++) {
              if ((chunkSizes.size() > 0) && (i < chunkSizes.size())) {
                rawSize = ((Integer) chunkSizes.get(i)).intValue();
              }
              else i = core.imageCount[0];
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
          core.imageCount[0] = in.readInt();

          if (rawSize == 0) {
            in.seek(in.getFilePointer() - 4);
            for (int b=0; b<core.imageCount[0]; b++) {
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
  private boolean isContainer(String type) {
    for (int i=0; i<CONTAINER_TYPES.length; i++) {
      if (type.equals(CONTAINER_TYPES[i])) return true;
    }
    return false;
  }

  /** Debugging method; prints information on an atom. */
  private void print(int depth, long size, String type, byte[] data) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append(" ");
    sb.append(type + " : [" + size + "]");
    debug(sb.toString());
  }

  /** Uncompresses an image plane according to the the codec identifier. */
  private byte[] uncompress(byte[] pixs, String code)
    throws FormatException, IOException
  {
    // JPEG and mjpb codecs handled separately, so not included in this list
    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) return rleUncompress(pixs);
    else if (code.equals("rpza")) return rpzaUncompress(pixs, null);
    else {
      throw new FormatException("Sorry, " + codec + " codec is not supported");
    }
  }

  /**
   * Uncompresses an RPZA compressed image plane. Adapted from the ffmpeg
   * codec - see http://ffmpeg.mplayerhq.hu
   */
  private byte[] rpzaUncompress(byte[] input, byte[] rtn)
    throws FormatException
  {
    int width = core.sizeX[0];
    int stride = core.sizeX[0];
    int rowInc = stride - 4;
    int streamPtr = 0;
    short opcode;
    int nBlocks;
    int colorA = 0, colorB;
    int[] color4 = new int[4];
    int index, idx;
    int ta, tb;
    int rowPtr = 0, pixelPtr = 0, blockPtr = 0;
    int pixelX, pixelY;
    int totalBlocks;

    int[] pixels = new int[width * core.sizeY[0]];
    if (rtn == null) rtn = new byte[width * core.sizeY[0] * 3];

    while (input[streamPtr] != (byte) 0xe1) streamPtr++;
    streamPtr += 4;

    totalBlocks = ((width + 3) / 4) * ((core.sizeY[0] + 3) / 4);

    while (streamPtr < input.length) {
      opcode = input[streamPtr++];
      nBlocks = (opcode & 0x1f) + 1;

      if ((opcode & 0x80) == 0) {
        if (streamPtr >= input.length) break;
        colorA = (opcode << 8) | input[streamPtr++];
        opcode = 0;
        if (streamPtr >= input.length) break;
        if ((input[streamPtr] & 0x80) != 0) {
          opcode = 0x20;
          nBlocks = 1;
        }
      }

      switch (opcode & 0xe0) {
        case 0x80:
          while (nBlocks-- > 0) {
            pixelPtr += 4;
            if (pixelPtr >= width) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0xa0:
          colorA = DataTools.bytesToInt(input, streamPtr, 2, false);
          streamPtr += 2;
          while (nBlocks-- > 0) {
            blockPtr = rowPtr + pixelPtr;
            for (pixelY=0; pixelY < 4; pixelY++) {
              for (pixelX=0; pixelX < 4; pixelX++) {
                if (blockPtr >= pixels.length) break;
                pixels[blockPtr] = colorA;

                short s = (short) (pixels[blockPtr] & 0x7fff);

                rtn[blockPtr] = (byte) (255 - ((s & 0x7c00) >> 10));
                rtn[blockPtr + pixels.length] =
                  (byte) (255 - ((s & 0x3e0) >> 5));
                rtn[blockPtr + 2*pixels.length] = (byte) (255 - (s & 0x1f));

                blockPtr++;
              }
              blockPtr += rowInc;
            }
            pixelPtr += 4;
            if (pixelPtr >= width) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0xc0:
          colorA = DataTools.bytesToInt(input, streamPtr, 2, false);
          streamPtr += 2;
        case 0x20:
          colorB = DataTools.bytesToInt(input, streamPtr, 2, false);
          streamPtr += 2;

          color4[0] = colorB;
          color4[1] = 0;
          color4[2] = 0;
          color4[3] = colorA;

          ta = (colorA >> 10) & 0x1f;
          tb = (colorB >> 10) & 0x1f;
          color4[1] |= ((11*ta + 21*tb) >> 5) << 10;
          color4[2] |= ((21*ta + 11*tb) >> 5) << 10;

          ta = (colorA >> 5) & 0x1f;
          tb = (colorB >> 5) & 0x1f;
          color4[1] |= ((11*ta + 21*tb) >> 5) << 5;
          color4[2] |= ((21*ta + 11*tb) >> 5) << 5;

          ta = colorA & 0x1f;
          tb = colorB & 0x1f;
          color4[1] |= ((11*ta + 21*tb) >> 5);
          color4[2] |= ((21*ta + 11*tb) >> 5);

          while (nBlocks-- > 0) {
            blockPtr = rowPtr + pixelPtr;
            for (pixelY=0; pixelY<4; pixelY++) {
              if (streamPtr >= input.length) break;
              index = input[streamPtr++];
              for (pixelX=0; pixelX<4; pixelX++) {
                idx = (index >> (2*(3 - pixelX))) & 0x03;
                if (blockPtr >= pixels.length) break;
                pixels[blockPtr] = color4[idx];

                short s = (short) (pixels[blockPtr] & 0x7fff);

                rtn[blockPtr] = (byte) (255 - ((s & 0x7c00) >> 10));
                rtn[blockPtr + pixels.length] =
                  (byte) (255 - ((s & 0x3e0) >> 5));
                rtn[blockPtr + 2*pixels.length] = (byte) (255 - (s & 0x1f));

                blockPtr++;
              }
              blockPtr += rowInc;
            }
            pixelPtr += 4;
            if (pixelPtr >= width) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0x00:
          blockPtr = rowPtr + pixelPtr;
          for (pixelY=0; pixelY < 4; pixelY++) {
            for (pixelX=0; pixelX < 4; pixelX++) {
              if ((pixelY != 0) || (pixelX != 0)) {
                colorA = DataTools.bytesToInt(input, streamPtr, 2, false);
                streamPtr += 2;
              }
              if (blockPtr >= pixels.length) break;
              pixels[blockPtr] = colorA;

              short s = (short) (pixels[blockPtr] & 0x7fff);

              rtn[blockPtr] = (byte) (255 - ((s & 0x7c00) >> 10));
              rtn[blockPtr + pixels.length] = (byte) (255 - ((s & 0x3e0) >> 5));
              rtn[blockPtr + 2*pixels.length] = (byte) (255 - (s & 0x1f));

              blockPtr++;
            }
            blockPtr += rowInc;
          }
          pixelPtr += 4;
          if (pixelPtr >= width) {
            pixelPtr = 0;
            rowPtr += stride * 4;
          }
          totalBlocks--;
          break;
      }
    }

    return rtn;
  }

  /** Uncompresses a MJPEG-B compressed image plane. */
  private BufferedImage mjpbUncompress(byte[] input) throws FormatException {
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

    byte[] lumDcBits = null, lumAcBits = null, lumDc = null, lumAc = null;
    byte[] quant = null;

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
      int offset =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // offset to quantization table
      int quantOffset =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // offset to Huffman table
      int huffmanOffset =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // offset to start of frame
      int sof =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // offset to start of scan
      int sos =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // offset to start of data
      int sod =
        DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]) + extra;
      pt += 4;

      // skip over the quantization table, if it exists
      if (quantOffset != 0) {
        pt = quantOffset;
        int length = DataTools.bytesToInt(input, pt, 2, core.littleEndian[0]);
        pt += 3;
        quant = new byte[length - 3];
        System.arraycopy(input, pt, quant, 0, quant.length);
        pt += quant.length;
      }

      // skip over the Huffman table, if it exists
      if (huffmanOffset != 0) {
        pt = huffmanOffset;
        int length = DataTools.bytesToInt(input, pt, 2, core.littleEndian[0]);
        pt += 3;
        lumDcBits = new byte[16];
        System.arraycopy(input, pt, lumDcBits, 0, lumDcBits.length);
        pt += lumDcBits.length;
        lumDc = new byte[12];
        System.arraycopy(input, pt, lumDc, 0, lumDc.length);
        pt += lumDc.length + 1;
        lumAcBits = new byte[16];
        System.arraycopy(input, pt, lumAcBits, 0, lumAcBits.length);
        pt += lumAcBits.length;
        lumAc = new byte[162];
        System.arraycopy(input, pt, lumAc, 0, lumAc.length);
        pt += lumAc.length;
      }

      // skip to the frame header
      pt = sof;

      // read sof header
      // we can skip over the first 7 bytes (length, bps, height, width)
      pt += 7;

      // number of channels
      int channels = DataTools.bytesToInt(input, pt, 1, core.littleEndian[0]);
      pt++;

      int[] sampling = new int[channels];
      for (int i=0; i<channels; i++) {
        pt++;
        sampling[i] = DataTools.bytesToInt(input, pt, 1, core.littleEndian[0]);
        pt += 2;
      }

      // skip to scan header
      pt = sos;

      // we can skip over the first 3 bytes (length, number of channels)
      pt += 3;
      int[] tables = new int[channels];
      for (int i=0; i<channels; i++) {
        pt++;
        tables[i] = DataTools.bytesToInt(input, pt, 1, core.littleEndian[0]);
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

        pt += DataTools.bytesToInt(input, pt, 4, core.littleEndian[0]);
        pt -= 36; // HACK

        numBytes = input.length - pt;
        raw2 = new byte[numBytes];
        System.arraycopy(input, pt, raw2, 0, raw2.length);
      }
    }

    if (raw == null) raw = input;

    // insert zero after each byte equal to 0xff
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

    int length = 67;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    v.add((byte) 0x00);
    v.add(quant);

    // add Huffman tables

    v.add(new byte[] {(byte) 0xff, (byte) 0xc4});

    length = (lumDcBits.length + lumDc.length +
      lumAcBits.length + lumAc.length)*2 + 6;
    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    // the ordering of these tables matters

    v.add((byte) 0x00);
    v.add(lumDcBits);
    v.add(lumDc);

    v.add((byte) 0x01);
    v.add(lumDcBits);
    v.add(lumDc);

    v.add((byte) 0x10);
    v.add(lumAcBits);
    v.add(lumAc);

    v.add((byte) 0x11);
    v.add(lumAcBits);
    v.add(lumAc);

    // add start-of-frame header

    v.add((byte) 0xff);
    v.add((byte) 0xc0);

    length = (bitsPerPixel >= 40) ? 11 : 17;

    v.add((byte) ((length >>> 8) & 0xff));
    v.add((byte) (length & 0xff));

    int fieldHeight = core.sizeY[0];
    if (interlaced) fieldHeight /= 2;
    if (core.sizeY[0] % 2 == 1) fieldHeight++;

    int c = bitsPerPixel == 24 ? 3 : (bitsPerPixel == 32 ? 4 : 1);

    v.add(bitsPerPixel >= 40 ? (byte) (bitsPerPixel - 32) :
      (byte) (bitsPerPixel / c));  // bits per sample
    v.add((byte) ((fieldHeight >>> 8) & 0xff));
    v.add((byte) (fieldHeight & 0xff));
    v.add((byte) ((core.sizeX[0] >>> 8) & 0xff));
    v.add((byte) (core.sizeX[0] & 0xff));
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

      /*
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
      */
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
      //}

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
  private BufferedImage bufferedJPEG(byte[] input) throws FormatException {
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
  private byte[] rleUncompress(byte[] input) throws FormatException, IOException
  {
    if (input.length < 8) return prevPixels;

    // read the 4 byte chunk length; this should equal input.length

    // now read the header
    // if the header is 0, we uncompress the remaining bytes
    // otherwise, read extra header information to determine which
    // scanlines to uncompress

    int header = DataTools.bytesToInt(input, 4, 2, core.littleEndian[0]);
    int off = 0; // offset into output
    int start = 0;
    int pt = 6;
    int numLines = core.sizeY[0];
    int ebpp = bitsPerPixel / 8;  // effective bytes per pixel
    if (ebpp == 1 || ebpp == 2) ebpp *= 3;
    else if (ebpp >= 5) ebpp %= 4;
    byte[] output = new byte[core.sizeX[0] * core.sizeY[0] * ebpp];

    if ((header & 0x0008) == 0x0008) {
      start = DataTools.bytesToInt(input, pt, 2, core.littleEndian[0]);
      // skip 2 bytes
      pt += 4;
      numLines = DataTools.bytesToInt(input, pt, 2, core.littleEndian[0]);
      // skip 2 bytes
      pt += 4;

      // copy appropriate lines from prevPixels

      if (canUsePrevious) {
        for (int i=0; i<start; i++) {
          off = i * core.sizeX[0] * ebpp;
          System.arraycopy(prevPixels, off, output, off, core.sizeX[0] * ebpp);
        }
      }
      off += (core.sizeX[0] * ebpp);

      if (canUsePrevious) {
        for (int i=(start+numLines); i<core.sizeY[0]; i++) {
          int offset = i * core.sizeX[0] * ebpp;
          System.arraycopy(prevPixels, offset, output, offset,
            core.sizeX[0] * ebpp);
        }
      }
    }
    else throw new FormatException("Unsupported header : " + header);

    // uncompress the remaining scanlines

    int skip = 0; // number of bytes to skip
    byte rle = 0; // RLE code

    int rowPointer = start * (core.sizeX[0] * ebpp);

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

          if (off < (rowPointer + (core.sizeX[0] * ebpp)) && canUsePrevious) {
            System.arraycopy(prevPixels, off, output, off,
              (rowPointer + (core.sizeX[0] * ebpp)) - off);
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
      rowPointer += (core.sizeX[0] * ebpp);
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

}
