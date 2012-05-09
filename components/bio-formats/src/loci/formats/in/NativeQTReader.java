//
// NativeQTReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.MJPBCodec;
import loci.formats.codec.MJPBCodecOptions;
import loci.formats.codec.QTRLECodec;
import loci.formats.codec.RPZACodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * NativeQTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Video codecs currently supported: raw, rle, jpeg, mjpb, rpza.
 * Additional video codecs will be added as time permits.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NativeQTReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NativeQTReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class NativeQTReader extends FormatReader {

  // -- Constants --

  /** List of identifiers for each container atom. */
  private static final String[] CONTAINER_TYPES = {
    "moov", "trak", "udta", "tref", "imap", "mdia", "minf", "stbl", "edts",
    "mdra", "rmra", "imag", "vnrp", "dinf"
  };

  // -- Fields --

  /** Offset to start of pixel data. */
  private long pixelOffset;

  /** Total number of bytes of pixel data. */
  private long pixelBytes;

  /** Pixel depth. */
  private int bitsPerPixel;

  /** Raw plane size, in bytes. */
  private int rawSize;

  /** Offsets to each plane's pixel data. */
  private Vector<Integer> offsets;

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

  /** Amount to subtract from each offset. */
  private int scale;

  /** Number of bytes in each plane. */
  private Vector<Integer> chunkSizes;

  /** Set to true if the scanlines in a plane are interlaced (mjpb only). */
  private boolean interlaced;

  /** Flag indicating whether the resource and data fork are separated. */
  private boolean spork;

  private boolean flip;

  // -- Constructor --

  /** Constructs a new QuickTime reader. */
  public NativeQTReader() {
    super("QuickTime", "mov");
    suffixNecessary = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 64;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    // use a crappy hack for now
    String s = stream.readString(blockLen);
    for (int i=0; i<CONTAINER_TYPES.length; i++) {
      if (s.indexOf(CONTAINER_TYPES[i]) >= 0 &&
        !CONTAINER_TYPES[i].equals("imag"))
      {
        return true;
      }
    }
    return s.indexOf("wide") >= 0 ||
      s.indexOf("mdat") >= 0 || s.indexOf("ftypqt") >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    String code = codec;
    if (no >= getImageCount() - altPlanes) code = altCodec;

    int offset = offsets.get(no).intValue();
    int nextOffset = (int) pixelBytes;

    scale = offsets.get(0).intValue();
    offset -= scale;

    if (no < offsets.size() - 1) {
      nextOffset = offsets.get(no + 1).intValue() - scale;
    }

    if ((nextOffset - offset) < 0) {
      int temp = offset;
      offset = nextOffset;
      nextOffset = temp;
    }

    byte[] pixs = new byte[nextOffset - offset];

    in.seek(pixelOffset + offset);

    if (code.equals("tiff")) {
      in.skipBytes(16);
    }

    in.read(pixs);

    canUsePrevious = (prevPixels != null) && (prevPlane == no - 1) &&
      !code.equals(altCodec);

    byte[] t = uncompress(pixs, code);
    if (code.equals("rpza")) {
      for (int i=0; i<t.length; i++) {
        t[i] = (byte) (255 - t[i]);
      }
      prevPlane = no;
      return buf;
    }

    // on rare occassions, we need to trim the data
    if (canUsePrevious && (prevPixels.length < t.length)) {
      byte[] temp = t;
      t = new byte[prevPixels.length];
      System.arraycopy(temp, 0, t, 0, t.length);
    }

    prevPixels = t;
    prevPlane = no;

    // determine whether we need to strip out any padding bytes

    int pad = (4 - (getSizeX() % 4)) % 4;
    if (codec.equals("mjpb")) pad = 0;

    int expectedSize = FormatTools.getPlaneSize(this);

    if (prevPixels.length == expectedSize ||
      (bitsPerPixel == 32 && (3 * (prevPixels.length / 4)) == expectedSize))
    {
      pad = 0;
    }

    if (pad > 0) {
      int bytes = bitsPerPixel < 40 ? bitsPerPixel / 8 :
        (bitsPerPixel - 32) / 8;
      t = new byte[prevPixels.length - getSizeY()*pad*bytes];

      for (int row=0; row<getSizeY(); row++) {
        System.arraycopy(prevPixels, bytes * row * (getSizeX() + pad), t,
          row * getSizeX() * bytes, getSizeX() * bytes);
      }
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int srcRowLen = getSizeX() * bpp * getSizeC();
    int destRowLen = w * bpp * getSizeC();
    for (int row=0; row<h; row++) {
      if (bitsPerPixel == 32) {
        for (int col=0; col<w; col++) {
          int src = row * getSizeX() * bpp * 4 + (x + col) * bpp * 4 + 1;
          int dst = row * destRowLen + col * bpp * 3;
          if (src + 3 <= t.length && dst + 3 <= buf.length) {
            System.arraycopy(t, src, buf, dst, 3);
          }
        }
      }
      else {
        System.arraycopy(t, row*srcRowLen + x*bpp*getSizeC(), buf,
          row*destRowLen, destRowLen);
      }
    }

    if ((bitsPerPixel == 40 || bitsPerPixel == 8) && !code.equals("mjpb")) {
      // invert the pixels
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) (255 - buf[i]);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      prevPixels = null;
      codec = altCodec = null;
      pixelOffset = pixelBytes = bitsPerPixel = rawSize = 0;
      prevPlane = altPlanes = 0;
      canUsePrevious = false;
      scale = 0;
      chunkSizes = null;
      interlaced = spork = flip = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("QTReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    spork = true;
    offsets = new Vector<Integer>();
    chunkSizes = new Vector<Integer>();
    status("Parsing tags");

    parse(0, 0, in.length());

    core[0].imageCount = offsets.size();
    if (chunkSizes.size() < getImageCount() && chunkSizes.size() > 0) {
      core[0].imageCount = chunkSizes.size();
    }

    status("Populating metadata");

    int bytesPerPixel = (bitsPerPixel / 8) % 4;
    core[0].pixelType =
      bytesPerPixel == 2 ?  FormatTools.UINT16 : FormatTools.UINT8;

    core[0].sizeZ = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].littleEndian = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

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
      debug("Searching for research fork:");
      if (f.exists()) {
        debug("\t Found: " + f);
        if (in != null) in.close();
        in = new RandomAccessInputStream(f.getAbsolutePath());

        stripHeader();
        parse(0, 0, in.length());
        core[0].imageCount = offsets.size();
      }
      else {
        debug("\tAbsent: " + f);
        f = new Location(id.substring(0,
          id.lastIndexOf(File.separator) + 1) + "._" +
          id.substring(base.lastIndexOf(File.separator) + 1));
        if (f.exists()) {
          debug("\t Found: " + f);
          if (in != null) in.close();
          in = new RandomAccessInputStream(f.getAbsolutePath());
          stripHeader();
          parse(0, in.getFilePointer(), in.length());
          core[0].imageCount = offsets.size();
        }
        else {
          debug("\tAbsent: " + f);
          f = new Location(id + "/..namedfork/rsrc");
          if (f.exists()) {
            debug("\t Found: " + f);
            if (in != null) in.close();
            in = new RandomAccessInputStream(f.getAbsolutePath());
            stripHeader();
            parse(0, in.getFilePointer(), in.length());
            core[0].imageCount = offsets.size();
          }
          else {
            debug("\tAbsent: " + f);
            throw new FormatException("QuickTime resource fork not found. " +
              " To avoid this issue, please flatten your QuickTime movies " +
              "before importing with Bio-Formats.");
          }
        }
      }
      // reset the stream, otherwise openBytes will try to read pixels
      // from the resource fork
      in = new RandomAccessInputStream(currentId);
    }

    core[0].rgb = bitsPerPixel < 40;
    core[0].sizeC = isRGB() ? 3 : 1;
    core[0].interleaved = isRGB();
    core[0].sizeT = getImageCount();

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

  // -- Helper methods --

  /** Parse all of the atoms in the file. */
  private void parse(int depth, long offset, long length)
    throws FormatException, IOException
  {
    while (offset < length) {
      in.seek(offset);

      // first 4 bytes are the atom size
      long atomSize = in.readInt() & 0xffffffff;

      // read the atom type
      String atomType = in.readString(4);

      // if atomSize is 1, then there is an 8 byte extended size
      if (atomSize == 1) {
        atomSize = in.readLong();
      }

      if (atomSize < 0) {
        LogTools.println("QTReader: invalid atom size: " + atomSize);
      }

      debug("Seeking to " + offset +
        "; atomType=" + atomType + "; atomSize=" + atomSize);

      // if this is a container atom, parse the children
      if (isContainer(atomType)) {
        parse(depth++, in.getFilePointer(), offset + atomSize);
      }
      else {
        if (atomSize == 0) atomSize = in.length();
        long oldpos = in.getFilePointer();

        if (atomType.equals("mdat")) {
          // we've found the pixel data
          pixelOffset = in.getFilePointer();
          pixelBytes = atomSize;

          if (pixelBytes > (in.length() - pixelOffset)) {
            pixelBytes = in.length() - pixelOffset;
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

          if (getSizeX() == 0) core[0].sizeX = in.readInt();
          if (getSizeY() == 0) core[0].sizeY = in.readInt();
        }
        else if (atomType.equals("cmov")) {
          in.skipBytes(8);
          if ("zlib".equals(in.readString(4))) {
            atomSize = in.readInt();
            in.skipBytes(4);
            int uncompressedSize = in.readInt();

            byte[] b = new byte[(int) (atomSize - 12)];
            in.read(b);

            byte[] output = new ZlibCodec().decompress(b, null);

            RandomAccessInputStream oldIn = in;
            in = new RandomAccessInputStream(output);
            parse(0, 0, output.length);
            in.close();
            in = oldIn;
          }
          else throw new FormatException("Compressed header not supported.");
        }
        else if (atomType.equals("stco")) {
          // we've found the plane offsets

          if (offsets.size() > 0) break;
          spork = false;
          in.skipBytes(4);
          int numPlanes = in.readInt();
          if (numPlanes != getImageCount()) {
            in.seek(in.getFilePointer() - 4);
            int off = in.readInt();
            offsets.add(new Integer(off));
            for (int i=1; i<getImageCount(); i++) {
              if ((chunkSizes.size() > 0) && (i < chunkSizes.size())) {
                rawSize = chunkSizes.get(i).intValue();
              }
              else i = getImageCount();
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

          in.skipBytes(4);
          int numEntries = in.readInt();
          in.skipBytes(4);

          for (int i=0; i<numEntries; i++) {
            if (i == 0) {
              codec = in.readString(4);

              if (!codec.equals("raw ") && !codec.equals("rle ") &&
                !codec.equals("rpza") && !codec.equals("mjpb") &&
                !codec.equals("jpeg") && !codec.equals("tiff"))
              {
                throw new FormatException("Unsupported codec: " + codec);
              }

              in.skipBytes(16);
              if (in.readShort() == 0) {
                in.skipBytes(56);

                bitsPerPixel = in.readShort();
                if (codec.equals("rpza")) bitsPerPixel = 8;
                in.skipBytes(10);
                interlaced = in.read() == 2;
                addGlobalMeta("Codec", codec);
                addGlobalMeta("Bits per pixel", bitsPerPixel);
                in.skipBytes(9);
              }
            }
            else {
              altCodec = in.readString(4);
              addGlobalMeta("Second codec", altCodec);
            }
          }
        }
        else if (atomType.equals("stsz")) {
          // found the number of planes
          in.skipBytes(4);
          rawSize = in.readInt();
          core[0].imageCount = in.readInt();

          if (rawSize == 0) {
            in.seek(in.getFilePointer() - 4);
            for (int b=0; b<getImageCount(); b++) {
              chunkSizes.add(new Integer(in.readInt()));
            }
          }
        }
        else if (atomType.equals("stsc")) {
          in.skipBytes(4);

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
          in.skipBytes(10);
          int fps = in.readInt();
          addGlobalMeta("Frames per second", fps);
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
      print(depth, atomSize, atomType);
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
  private void print(int depth, long size, String type) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append(" ");
    sb.append(type + " : [" + size + "]");
    debug(sb.toString());
  }

  /** Uncompresses an image plane according to the the codec identifier. */
  private byte[] uncompress(byte[] pixs, String code)
    throws FormatException, IOException
  {
    CodecOptions options = new MJPBCodecOptions();
    options.width = getSizeX();
    options.height = getSizeY();
    options.bitsPerSample = bitsPerPixel;
    options.channels = bitsPerPixel < 40 ? bitsPerPixel / 8 :
      (bitsPerPixel - 32) / 8;
    options.previousImage = canUsePrevious ? prevPixels : null;
    options.littleEndian = isLittleEndian();
    options.interleaved = isRGB();

    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) {
      return new QTRLECodec().decompress(pixs, options);
    }
    else if (code.equals("rpza")) {
      return new RPZACodec().decompress(pixs, options);
    }
    else if (code.equals("mjpb")) {
      ((MJPBCodecOptions) options).interlaced = interlaced;
      return new MJPBCodec().decompress(pixs, options);
    }
    else if (code.equals("jpeg")) {
      return new JPEGCodec().decompress(pixs, options);
    }
    else if (code.equals("tiff")) {
      MinimalTiffReader tiffReader = new MinimalTiffReader();
      Location.mapFile("plane.tiff", new ByteArrayHandle(pixs));
      tiffReader.setId("plane.tiff");
      byte[] result = tiffReader.openBytes(0);
      tiffReader.close();
      Location.mapFile("plane.tiff", null);
      return result;
    }
    else throw new FormatException("Unsupported codec : " + code);
  }

  /** Cut off header bytes from a resource fork file. */
  private void stripHeader() throws IOException {
    // seek to 4 bytes before first occurence of 'moov'

    // read 8K at a time, for efficiency
    long fp = in.getFilePointer();
    byte[] buf = new byte[8192];
    int index = -1;
    while (true) {
      int r = in.read(buf, 3, buf.length - 3);
      if (r <= 0) break;
      // search buffer for "moov"
      for (int i=0; i<buf.length-3; i++) {
        if (buf[i] == 'm' && buf[i+1] == 'o' &&
          buf[i+2] == 'o' && buf[i+3] == 'v')
        {
          index = i - 3; // first three characters are zeroes or leftovers
          break;
        }
      }
      if (index >= 0) break;

      // save last three bytes of buffer
      fp += r;
      buf[0] = buf[buf.length - 3];
      buf[1] = buf[buf.length - 2];
      buf[2] = buf[buf.length - 1];
    }
    if (index >= 0) in.seek(fp + index - 4);
  }

}
