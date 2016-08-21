/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.MJPBCodec;
import loci.formats.codec.MJPBCodecOptions;
import loci.formats.codec.QTRLECodec;
import loci.formats.codec.RPZACodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;

/**
 * NativeQTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Video codecs currently supported: raw, rle, jpeg, mjpb, rpza.
 * Additional video codecs will be added as time permits.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/NativeQTReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/NativeQTReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
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
  private boolean separatedFork;

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
    in.read(pixs);

    canUsePrevious = (prevPixels != null) && (prevPlane == no - 1) &&
      !code.equals(altCodec);

    byte[] t = prevPlane == no && prevPixels != null && !code.equals(altCodec) ?
      prevPixels : uncompress(pixs, code);
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

    int bytes = bitsPerPixel < 40 ? bitsPerPixel / 8 : (bitsPerPixel - 32) / 8;
    int pad = (4 - (getSizeX() % 4)) % 4;
    if (codec.equals("mjpb")) pad = 0;

    int expectedSize = FormatTools.getPlaneSize(this);

    if (prevPixels.length == expectedSize ||
      (bitsPerPixel == 32 && (3 * (prevPixels.length / 4)) == expectedSize))
    {
      pad = 0;
    }

    if (pad > 0) {
      t = new byte[prevPixels.length - getSizeY()*pad];

      for (int row=0; row<getSizeY(); row++) {
        System.arraycopy(prevPixels, row * (bytes * getSizeX() + pad), t,
          row * getSizeX() * bytes, getSizeX() * bytes);
      }
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int srcRowLen = getSizeX() * bpp * getSizeC();
    int destRowLen = w * bpp * getSizeC();
    for (int row=0; row<h; row++) {
      if (bitsPerPixel == 32) {
        for (int col=0; col<w; col++) {
          int src = (row + y) * getSizeX() * bpp * 4 + (x + col) * bpp * 4 + 1;
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
      interlaced = separatedFork = flip = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    separatedFork = true;
    offsets = new Vector<Integer>();
    chunkSizes = new Vector<Integer>();
    LOGGER.info("Parsing tags");

    parse(0, 0, in.length());

    CoreMetadata m = core.get(0);

    m.imageCount = offsets.size();
    if (chunkSizes.size() < getImageCount() && chunkSizes.size() > 0) {
      m.imageCount = chunkSizes.size();
    }

    LOGGER.info("Populating metadata");

    int bytes = (bitsPerPixel / 8) % 4;
    m.pixelType = bytes == 2 ? FormatTools.UINT16 : FormatTools.UINT8;

    m.sizeZ = 1;
    m.dimensionOrder = "XYCZT";
    m.littleEndian = false;
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

    // this handles the case where the data and resource forks have been
    // separated
    if (separatedFork) {
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
      LOGGER.debug("Searching for resource fork:");
      if (f.exists()) {
        LOGGER.debug("\t Found: {}", f);
        if (in != null) in.close();
        in = new RandomAccessInputStream(f.getAbsolutePath());

        stripHeader();
        parse(0, 0, in.length());
        m.imageCount = offsets.size();
      }
      else {
        LOGGER.debug("\tAbsent: {}", f);
        f = new Location(id.substring(0,
          id.lastIndexOf(File.separator) + 1) + "._" +
          id.substring(base.lastIndexOf(File.separator) + 1));
        if (f.exists()) {
          LOGGER.debug("\t Found: {}", f);
          if (in != null) in.close();
          in = new RandomAccessInputStream(f.getAbsolutePath());
          stripHeader();
          parse(0, in.getFilePointer(), in.length());
          m.imageCount = offsets.size();
        }
        else {
          LOGGER.debug("\tAbsent: {}", f);
          f = new Location(id + "/..namedfork/rsrc");
          if (f.exists()) {
            LOGGER.debug("\t Found: {}", f);
            if (in != null) in.close();
            in = new RandomAccessInputStream(f.getAbsolutePath());
            stripHeader();
            parse(0, in.getFilePointer(), in.length());
            m.imageCount = offsets.size();
          }
          else {
            LOGGER.debug("\tAbsent: {}", f);
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

    m.rgb = bitsPerPixel < 40;
    m.sizeC = isRGB() ? 3 : 1;
    m.interleaved = isRGB();
    m.sizeT = getImageCount();

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  /** Parse all of the atoms in the file. */
  private void parse(int depth, long offset, long length)
    throws FormatException, IOException
  {
    while (offset < length) {
      in.seek(offset);

      // first 4 bytes are the atom size
      long atomSize = in.readInt() & 0xffffffffL;

      // read the atom type
      String atomType = in.readString(4);

      // if atomSize is 1, then there is an 8 byte extended size
      if (atomSize == 1) {
        atomSize = in.readLong();
      }

      if (atomSize < 0) {
        LOGGER.warn("QTReader: invalid atom size: {}", atomSize);
      }
      else if (atomSize > in.length()) {
        offset += 4;
        continue;
      }

      LOGGER.debug("Seeking to {}; atomType={}; atomSize={}",
        new Object[] {offset, atomType, atomSize});

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

          if (getSizeX() == 0) core.get(0).sizeX = in.readInt();
          if (getSizeY() == 0) core.get(0).sizeY = in.readInt();
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
          else {
            throw new UnsupportedCompressionException(
              "Compressed header not supported.");
          }
        }
        else if (atomType.equals("stco")) {
          // we've found the plane offsets

          if (offsets.size() > 0) break;
          separatedFork = false;
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
                !codec.equals("jpeg"))
              {
                throw new UnsupportedCompressionException(
                  "Unsupported codec: " + codec);
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
          core.get(0).imageCount = in.readInt();

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
          in.skipBytes(12);
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
    LOGGER.debug(sb.toString());
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
    else {
      throw new UnsupportedCompressionException("Unsupported codec : " + code);
    }
  }

  /** Cut off header bytes from a resource fork file. */
  private void stripHeader() throws IOException {
    in.seek(0);
    while (!in.readString(4).equals("moov")) {
      in.seek(in.getFilePointer() - 2);
    }
    in.seek(in.getFilePointer() - 8);
  }

}
