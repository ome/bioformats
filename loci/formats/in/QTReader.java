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
import loci.formats.*;
import loci.formats.codec.*;
import loci.formats.meta.MetadataStore;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not require any external libraries to be installed.
 *
 * Video codecs currently supported: raw, rle, jpeg, mjpb, rpza.
 * Additional video codecs will be added as time permits.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/QTReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/QTReader.java">SVN</a></dd></dl>
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

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

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
      return legacy.openBytes(no, buf, x, y, w, h);
    }

    int offset = ((Integer) offsets.get(no)).intValue();
    int nextOffset = (int) pixelBytes;

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

    int pad = (4 - (core.sizeX[0] % 4)) % 4;
    if (codec.equals("mjpb")) pad = 0;

    int size = core.sizeX[0] * core.sizeY[0];
    if (size * (bitsPerPixel / 8) == prevPixels.length) pad = 0;

    if (pad > 0) {
      t = new byte[prevPixels.length - core.sizeY[0]*pad];

      for (int row=0; row<core.sizeY[0]; row++) {
        System.arraycopy(prevPixels, row*(core.sizeX[0]+pad), t,
          row*core.sizeX[0], core.sizeX[0]);
      }
    }

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[0]);
    int srcRowLen = core.sizeX[0] * bpp * core.sizeC[0];
    int destRowLen = w * bpp * core.sizeC[0];
    for (int row=0; row<h; row++) {
      if (bitsPerPixel == 32) {
        for (int col=0; col<w; col++) {
          System.arraycopy(t, row*core.sizeX[0]*bpp*4 + (x + col)*bpp*4 + 1,
            buf, row*destRowLen + col*bpp*3, 3);
        }
      }
      else {
        System.arraycopy(t, row*srcRowLen + x*bpp*core.sizeC[0], buf,
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

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (legacy != null) {
      legacy.close();
      legacy = null;
    }
    offsets = null;
    prevPixels = null;
    codec = altCodec = null;
    pixelOffset = pixelBytes = bitsPerPixel = rawSize = 0;
    prevPlane = altPlanes = 0;
    canUsePrevious = useLegacy = false;
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

    Exception exc = null;
    try { parse(0, 0, in.length()); }
    catch (FormatException e) { exc = e; }
    catch (IOException e) { exc = e; }
    if (exc != null) {
      if (debug) trace(exc);
      useLegacy = true;
      legacy = createLegacyReader();
      legacy.setId(id, true);
      core = legacy.getCoreMetadata();
      return;
    }

    core.imageCount[0] = offsets.size();
    if (chunkSizes.size() < core.imageCount[0] && chunkSizes.size() > 0) {
      core.imageCount[0] = chunkSizes.size();
    }

    status("Populating metadata");

    int bytesPerPixel = (bitsPerPixel / 8) % 4;
    core.pixelType[0] =
      bytesPerPixel == 2 ?  FormatTools.UINT16 : FormatTools.UINT8;

    core.rgb[0] = bitsPerPixel < 40;
    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? 3 : 1;
    core.sizeT[0] = core.imageCount[0];
    core.currentOrder[0] = "XYCZT";
    core.littleEndian[0] = false;
    core.interleaved[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);
    // CTR CHECK
//    for (int i=0; i<core.sizeC[0]; i++) {
//      store.setLogicalChannel(i, null, null, null, null, null, null, null,
//        null, null, null, null, null, null, null, null, null, null, null, null,
//        null, null, null, null, null);
//    }

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

      if (debug) {
        debug("Seeking to " + offset +
          "; atomType=" + atomType + "; atomSize=" + atomSize);
      }

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

          if (core.sizeX[0] == 0) core.sizeX[0] = in.readInt();
          if (core.sizeY[0] == 0) core.sizeY[0] = in.readInt();
        }
        else if (atomType.equals("cmov")) {
          in.skipBytes(8);
          if ("zlib".equals(in.readString(4))) {
            atomSize = in.readInt();
            in.skipBytes(4);
            int uncompressedSize = in.readInt();

            byte[] b = new byte[(int) (atomSize - 12)];
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

          if (offsets.size() > 0) break;
          spork = false;
          in.skipBytes(4);
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
                throw new FormatException("Unsupported codec: " + codec);
              }

              in.skipBytes(16);
              if (in.readShort() == 0) {
                in.skipBytes(56);

                bitsPerPixel = in.readShort();
                if (codec.equals("rpza")) bitsPerPixel = 8;
                in.skipBytes(10);
                interlaced = in.read() == 2;
                addMeta("Codec", codec);
                addMeta("Bits per pixel", new Integer(bitsPerPixel));
                in.skipBytes(9);
              }
            }
            else {
              altCodec = in.readString(4);
              addMeta("Second codec", altCodec);
            }
          }
        }
        else if (atomType.equals("stsz")) {
          // found the number of planes
          in.skipBytes(4);
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
      if (debug) print(depth, atomSize, atomType);
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
    if (code.equals("raw ")) return pixs;
    else if (code.equals("rle ")) {
      Object[] options = new Object[2];
      options[0] = new int[] {core.sizeX[0], core.sizeY[0],
        bitsPerPixel < 40 ? bitsPerPixel / 8 : (bitsPerPixel - 32) / 8};
      options[1] = canUsePrevious ? prevPixels : null;
      return new QTRLECodec().decompress(pixs, options);
    }
    else if (code.equals("rpza")) {
      int[] options = new int[2];
      options[0] = core.sizeX[0];
      options[1] = core.sizeY[0];
      return new RPZACodec().decompress(pixs, options);
    }
    else if (code.equals("mjpb")) {
      int[] options = new int[4];
      options[0] = core.sizeX[0];
      options[1] = core.sizeY[0];
      options[2] = bitsPerPixel;
      options[3] = interlaced ? 1 : 0;
      return new MJPBCodec().decompress(pixs, options);
    }
    else if (code.equals("jpeg")) {
      return new JPEGCodec().decompress(pixs,
        new Boolean(core.littleEndian[0]));
    }
    else throw new FormatException("Unsupported codec : " + code);
  }

  /** Cut off header bytes from a resource fork file. */
  private void stripHeader() throws IOException {
    // seek to 4 bytes before first occurence of 'moov'

    String test = null;
    boolean found = false;
    while (!found && in.getFilePointer() < (in.length() - 4)) {
      test = in.readString(4);
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
