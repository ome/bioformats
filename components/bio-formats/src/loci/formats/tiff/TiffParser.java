//
// TiffParser.java
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

package loci.formats.tiff;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.codec.BitBuffer;
import loci.formats.codec.CodecOptions;

/**
 * Parses TIFF data from an input source.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tiff/TiffParser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tiff/TiffParser.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffParser {

  // -- Fields --

  /** Input source from which to parse TIFF data. */
  protected RandomAccessInputStream in;

  /** Cached tile buffer to avoid re-allocations when reading tiles. */
  private byte[] cachedTileBuffer;

  // -- Constructors --

  /** Constructs a new TIFF parser from the given input source. */
  public TiffParser(RandomAccessInputStream in) {
    this.in = in;
  }

  // -- TiffParser methods --

  /** Gets the stream from which TIFF data is being parsed. */
  public RandomAccessInputStream getStream() {
    return in;
  }

  /** Tests this stream to see if it represents a TIFF file. */
  public boolean isValidHeader() {
    try {
      return checkHeader() != null;
    }
    catch (IOException e) {
      return false;
    }
  }

  /**
   * Checks the TIFF header.
   *
   * @return true if little-endian,
   *         false if big-endian,
   *         or null if not a TIFF.
   */
  public Boolean checkHeader() throws IOException {
    if (in.length() < 4) return null;

    // byte order must be II or MM
    in.seek(0);
    int endianOne = in.read();
    int endianTwo = in.read();
    boolean littleEndian = endianOne == TiffConstants.LITTLE &&
      endianTwo == TiffConstants.LITTLE; // II
    boolean bigEndian = endianOne == TiffConstants.BIG &&
      endianTwo == TiffConstants.BIG; // MM
    if (!littleEndian && !bigEndian) return null;

    // check magic number (42)
    in.order(littleEndian);
    short magic = in.readShort();
    if (magic != TiffConstants.MAGIC_NUMBER &&
      magic != TiffConstants.BIG_TIFF_MAGIC_NUMBER)
    {
      return null;
    }

    return new Boolean(littleEndian);
  }

  // -- TiffParser methods - IFD parsing --

  /**
   * Gets all IFDs within the TIFF file, or null
   * if the input source is not a valid TIFF file.
   */
  public IFDList getIFDs() throws IOException {
    return getIFDs(false);
  }

  /**
   * Gets all IFDs within the TIFF file, or null
   * if the input source is not a valid TIFF file.
   * If 'skipThumbnails' is set to true, thumbnail IFDs will not be returned.
   */
  public IFDList getIFDs(boolean skipThumbnails) throws IOException {
    return getIFDs(skipThumbnails, true);
  }

  /**
   * Gets all IFDs within the TIFF file, or null
   * if the input source is not a valid TIFF file.
   * If 'skipThumbnails' is set to true, thumbnail IFDs will not be returned.
   * If 'fillInEntries' is set to true, IFD entry values that are stored at
   * an arbitrary offset will be read.
   */
  public IFDList getIFDs(boolean skipThumbnails, boolean fillInEntries)
    throws IOException
  {
    // check TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffConstants.BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(bigTiff);

    // compute maximum possible number of IFDs, for loop safety
    // each IFD must have at least one directory entry, which means that
    // each IFD must be at least 2 + 12 + 4 = 18 bytes in length
    long ifdMax = (in.length() - 8) / (bigTiff ? 22 : 18);

    // read in IFDs
    IFDList ifds = new IFDList();
    for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
      IFD ifd = getIFD(ifdNum, offset, bigTiff, fillInEntries);
      if (ifd == null || ifd.size() <= 2) break;
      Number subfile = (Number) ifd.getIFDValue(IFD.NEW_SUBFILE_TYPE);
      int subfileType = subfile == null ? 0 : subfile.intValue();
      if (!skipThumbnails || subfileType == 0) {
        ifds.add(ifd);
      }
      else ifd = null;
      offset = getNextOffset(bigTiff, offset);
      if (offset <= 0 || offset >= in.length()) {
        if (offset != 0) {
          LogTools.debug("getIFDs: invalid IFD offset: " + offset);
        }
        break;
      }
    }

    return ifds;
  }

  /** Gets the offsets to every IFD in the file. */
  public long[] getIFDOffsets() throws IOException {
    // check TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffConstants.BIG_TIFF_MAGIC_NUMBER;
    int bytesPerEntry = bigTiff ? TiffConstants.BIG_TIFF_BYTES_PER_ENTRY :
      TiffConstants.BYTES_PER_ENTRY;

    Vector<Long> offsets = new Vector<Long>();
    long offset = getFirstOffset(bigTiff);
    while (true) {
      in.seek(offset);
      offsets.add(offset);
      int nEntries = in.readShort();
      in.skipBytes(nEntries * bytesPerEntry);
      offset = getNextOffset(bigTiff, offset);
      if (offset <= 0 || offset >= in.length()) break;
    }

    long[] f = new long[offsets.size()];
    for (int i=0; i<f.length; i++) {
      f[i] = offsets.get(i).longValue();
    }

    return f;
  }

  /**
   * Gets the first IFD within the TIFF file, or null
   * if the input source is not a valid TIFF file.
   */
  public IFD getFirstIFD() throws IOException {
    // check TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffConstants.BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(bigTiff);

    IFD ifd = getIFD(0, offset, bigTiff);
    if (ifd != null) {
      ifd.put(new Integer(IFD.BIG_TIFF), new Boolean(bigTiff));
    }
    return ifd;
  }


  /**
   * Retrieve a given entry from the first IFD in the stream.
   *
   * @param tag the tag of the entry to be retrieved.
   * @return an object representing the entry's fields.
   * @throws IOException when there is an error accessing the stream.
   * @throws IllegalArgumentException when the tag number is unknown.
   */
  public TiffIFDEntry getFirstIFDEntry(int tag) throws IOException {
    // First lets re-position the file pointer by checking the TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffConstants.BIG_TIFF_MAGIC_NUMBER;

    // Get the offset of the first IFD
    long offset = getFirstOffset(bigTiff);

    // The following loosely resembles the logic of getIFD()...
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;

    for (int i = 0; i < numEntries; i++) {
      in.seek(offset + // The beginning of the IFD
        2 + // The width of the initial numEntries field
        (bigTiff ? TiffConstants.BIG_TIFF_BYTES_PER_ENTRY :
        TiffConstants.BYTES_PER_ENTRY) * i);

      int entryTag = in.readShort() & 0xffff;

      // Skip this tag unless it matches the one we want
      if (entryTag != tag) continue;

      // Parse the entry's "Type"
      int entryType = in.readShort() & 0xffff;

      // Parse the entry's "ValueCount"
      int valueCount =
        bigTiff ? (int) (in.readLong() & 0xffffffff) : in.readInt();
      if (valueCount < 0) {
        throw new RuntimeException("Count of '" + valueCount + "' unexpected.");
      }

      // Parse the entry's "ValueOffset"
      long valueOffset = getNextOffset(bigTiff, 0);

      return new TiffIFDEntry(entryTag, entryType, valueCount, valueOffset);
    }
    throw new IllegalArgumentException("Unknown tag: " + tag);
  }

  /**
   * Gets offset to the first IFD, or -1 if stream is not TIFF.
   * Assumes the stream is positioned properly (checkHeader just called).
   */
  public long getFirstOffset() throws IOException {
    return getFirstOffset(false);
  }

  /**
   * Gets offset to the first IFD, or -1 if stream is not TIFF.
   * Assumes the stream is positioned properly (checkHeader just called).
   *
   * @param bigTiff true if this is a BigTIFF file (8 byte pointers).
   */
  public long getFirstOffset(boolean bigTiff) throws IOException {
    if (bigTiff) in.skipBytes(4);
    return bigTiff ? in.readLong() : in.readInt();
  }

  /** Gets the IFD stored at the given offset. */
  public IFD getIFD(long ifdNum, long offset) throws IOException {
    return getIFD(ifdNum, offset, false);
  }

  /** Gets the IFD stored at the given offset. */
  public IFD getIFD(long ifdNum, long offset, boolean bigTiff)
    throws IOException
  {
    return getIFD(ifdNum, offset, bigTiff, true);
  }

  /**
   * Gets the IFD stored at the given offset.
   * If 'fillInEntries' is set to true, IFD entry values that are stored at
   * an arbitrary offset will be read.
   */
  public IFD getIFD(long ifdNum, long offset, boolean bigTiff,
    boolean fillInEntries) throws IOException
  {
    IFD ifd = new IFD();

    // save little-endian flag to internal LITTLE_ENDIAN tag
    ifd.put(new Integer(IFD.LITTLE_ENDIAN), new Boolean(in.isLittleEndian()));
    ifd.put(new Integer(IFD.BIG_TIFF), new Boolean(bigTiff));

    // read in directory entries for this IFD
    LogTools.debug("getIFDs: seeking IFD #" + ifdNum + " at " + offset);
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;
    LogTools.debug("getIFDs: " + numEntries + " directory entries to read");
    if (numEntries == 0 || numEntries == 1) return ifd;

    int bytesPerEntry = bigTiff ?
      TiffConstants.BIG_TIFF_BYTES_PER_ENTRY : TiffConstants.BYTES_PER_ENTRY;
    int baseOffset = bigTiff ? 8 : 2;
    int threshhold = bigTiff ? 8 : 4;

    for (int i=0; i<numEntries; i++) {
      in.seek(offset + baseOffset + bytesPerEntry * i);
      int tag = in.readShort() & 0xffff;
      int type = in.readShort() & 0xffff;
      // BigTIFF case is a slight hack because the count could be
      // greater than Integer.MAX_VALUE
      int count = bigTiff ? (int) (in.readLong() & 0xffffffff) : in.readInt();
      int bpe = IFD.getIFDTypeLength(type);

      LogTools.debug("getIFDs: read " + IFD.getIFDTagName(tag) +
        " (type=" + IFD.getIFDTypeName(type) + "; count=" + count + ")");
      if (count < 0 || bpe <= 0) {
        // invalid data
        in.skipBytes(bytesPerEntry - 4 - (bigTiff ? 8 : 4));
        continue;
      }
      Object value = null;

      long pointer = in.getFilePointer();

      if (count > threshhold / bpe) {
        pointer = getNextOffset(bigTiff, 0);
      }

      long inputLen = in.length();
      if (count * bpe + pointer > inputLen) {
        int oldCount = count;
        count = (int) ((inputLen - pointer) / bpe);
        LogTools.debug("getIFDs: truncated " + (oldCount - count) +
          " array elements for tag " + tag);
      }
      if (count < 0 || count > in.length()) break;

      TiffIFDEntry entry = new TiffIFDEntry(tag, type, count, pointer);

      if (pointer != in.getFilePointer() && !fillInEntries) {
        value = entry;
      }
      else value = getIFDValue(entry);

      if (value != null && !ifd.containsKey(new Integer(tag))) {
        ifd.put(new Integer(tag), value);
      }
    }

    in.seek(offset + baseOffset + bytesPerEntry * numEntries);

    if (!(ifd.get(IFD.IMAGE_WIDTH) instanceof Number) ||
      !(ifd.get(IFD.IMAGE_LENGTH) instanceof Number))
    {
      return null;
    }

    return ifd;
  }

  /** Fill in IFD entries that are stored at an arbitrary offset. */
  public void fillInIFD(IFD ifd) throws IOException {
    Vector<TiffIFDEntry> entries = new Vector<TiffIFDEntry>();
    for (Object key : ifd.keySet()) {
      if (ifd.get(key) instanceof TiffIFDEntry) {
        entries.add((TiffIFDEntry) ifd.get(key));
      }
    }

    TiffIFDEntry[] e = entries.toArray(new TiffIFDEntry[entries.size()]);
    Arrays.sort(e);

    for (TiffIFDEntry entry : e) {
      ifd.put(new Integer(entry.getTag()), getIFDValue(entry));
    }
  }

  /** Retrieve the value corresponding to the given TiffIFDEntry. */
  public Object getIFDValue(TiffIFDEntry entry) throws IOException {
    int type = entry.getType();
    int count = entry.getValueCount();
    long offset = entry.getValueOffset();

    if (offset != in.getFilePointer()) {
      in.seek(offset);
    }

    if (type == IFD.BYTE) {
      // 8-bit unsigned integer
      if (count == 1) return new Short(in.readByte());
      byte[] bytes = new byte[count];
      in.readFully(bytes);
      // bytes are unsigned, so use shorts
      short[] shorts = new short[count];
      for (int j=0; j<count; j++) shorts[j] = (short) (bytes[j] & 0xff);
      return shorts;
    }
    else if (type == IFD.ASCII) {
      // 8-bit byte that contain a 7-bit ASCII code;
      // the last byte must be NUL (binary zero)
      byte[] ascii = new byte[count];
      in.read(ascii);

      // count number of null terminators
      int nullCount = 0;
      for (int j=0; j<count; j++) {
        if (ascii[j] == 0 || j == count - 1) nullCount++;
      }

      // convert character array to array of strings
      String[] strings = nullCount == 1 ? null : new String[nullCount];
      String s = null;
      int c = 0, ndx = -1;
      for (int j=0; j<count; j++) {
        if (ascii[j] == 0) {
          s = new String(ascii, ndx + 1, j - ndx - 1);
          ndx = j;
        }
        else if (j == count - 1) {
          // handle non-null-terminated strings
          s = new String(ascii, ndx + 1, j - ndx);
        }
        else s = null;
        if (strings != null && s != null) strings[c++] = s;
      }
      return strings == null ? (Object) s : strings;
    }
    else if (type == IFD.SHORT) {
      // 16-bit (2-byte) unsigned integer
      if (count == 1) return new Integer(in.readShort() & 0xffff);
      int[] shorts = new int[count];
      for (int j=0; j<count; j++) {
        shorts[j] = in.readShort() & 0xffff;
      }
      return shorts;
    }
    else if (type == IFD.LONG || type == IFD.IFD) {
      // 32-bit (4-byte) unsigned integer
      if (count == 1) return new Long(in.readInt());
      long[] longs = new long[count];
      for (int j=0; j<count; j++) longs[j] = in.readInt();
      return longs;
    }
    else if (type == IFD.LONG8 || type == IFD.SLONG8 || type == IFD.IFD8) {
      if (count == 1) return new Long(in.readLong());
      long[] longs = new long[count];
      for (int j=0; j<count; j++) longs[j] = in.readLong();
      return longs;
    }
    else if (type == IFD.RATIONAL || type == IFD.SRATIONAL) {
      // Two LONGs or SLONGs: the first represents the numerator
      // of a fraction; the second, the denominator
      if (count == 1) return new TiffRational(in.readInt(), in.readInt());
      TiffRational[] rationals = new TiffRational[count];
      for (int j=0; j<count; j++) {
        rationals[j] = new TiffRational(in.readInt(), in.readInt());
      }
      return rationals;
    }
    else if (type == IFD.SBYTE || type == IFD.UNDEFINED) {
      // SBYTE: An 8-bit signed (twos-complement) integer
      // UNDEFINED: An 8-bit byte that may contain anything,
      // depending on the definition of the field
      if (count == 1) return new Byte(in.readByte());
      byte[] sbytes = new byte[count];
      in.read(sbytes);
      return sbytes;
    }
    else if (type == IFD.SSHORT) {
      // A 16-bit (2-byte) signed (twos-complement) integer
      if (count == 1) return new Short(in.readShort());
      short[] sshorts = new short[count];
      for (int j=0; j<count; j++) sshorts[j] = in.readShort();
      return sshorts;
    }
    else if (type == IFD.SLONG) {
      // A 32-bit (4-byte) signed (twos-complement) integer
      if (count == 1) return new Integer(in.readInt());
      int[] slongs = new int[count];
      for (int j=0; j<count; j++) slongs[j] = in.readInt();
      return slongs;
    }
    else if (type == IFD.FLOAT) {
      // Single precision (4-byte) IEEE format
      if (count == 1) return new Float(in.readFloat());
      float[] floats = new float[count];
      for (int j=0; j<count; j++) floats[j] = in.readFloat();
      return floats;
    }
    else if (type == IFD.DOUBLE) {
      // Double precision (8-byte) IEEE format
      if (count == 1) return new Double(in.readDouble());
      double[] doubles = new double[count];
      for (int j=0; j<count; j++) {
        doubles[j] = in.readDouble();
      }
      return doubles;
    }

    return null;
  }

  /** Convenience method for obtaining a stream's first ImageDescription. */
  public String getComment() throws IOException {
    IFD firstIFD = getFirstIFD();
    return firstIFD == null ? null : firstIFD.getComment();
  }

  // -- TiffParser methods - image reading --

  public byte[] getTile(IFD ifd, int row, int col)
    throws FormatException, IOException
  {
    int samplesPerPixel = ifd.getSamplesPerPixel();
    if (ifd.getPlanarConfiguration() == 2) samplesPerPixel = 1;
    int bpp = ifd.getBytesPerSample()[0];
    int width = (int) ifd.getTileWidth();
    int height = (int) ifd.getTileLength();
    byte[] buf = new byte[width * height * samplesPerPixel * bpp];

    return getTile(ifd, buf, row, col);
  }

  public byte[] getTile(IFD ifd, byte[] buf, int row, int col)
    throws FormatException, IOException
  {
    byte[] jpegTable = (byte[]) ifd.getIFDValue(IFD.JPEG_TABLES, false, null);

    CodecOptions options = new CodecOptions();
    options.interleaved = true;
    options.littleEndian = ifd.isLittleEndian();

    long tileWidth = ifd.getTileWidth();
    long tileLength = ifd.getTileLength();
    int samplesPerPixel = ifd.getSamplesPerPixel();
    int planarConfig = ifd.getPlanarConfiguration();
    int compression = ifd.getCompression();

    long numTileCols = ifd.getTilesPerRow();

    int pixel = ifd.getBytesPerSample()[0];
    int effectiveChannels = planarConfig == 2 ? 1 : samplesPerPixel;

    long[] stripOffsets = ifd.getStripOffsets();
    long[] stripByteCounts = ifd.getStripByteCounts();

    int tileNumber = (int) (row * numTileCols + col);
    byte[] tile = new byte[(int) stripByteCounts[tileNumber]];
    in.seek(stripOffsets[tileNumber]);
    in.read(tile);

    int size = (int) (tileWidth * tileLength * pixel * effectiveChannels);
    options.maxBytes = size;

    if (jpegTable != null) {
      byte[] q = new byte[jpegTable.length + tile.length - 4];
      System.arraycopy(jpegTable, 0, q, 0, jpegTable.length - 2);
      System.arraycopy(tile, 2, q, jpegTable.length - 2, tile.length - 2);
      tile = TiffCompression.uncompress(q, compression, options);
    }
    else tile = TiffCompression.uncompress(tile, compression, options);

    TiffCompression.undifference(tile, ifd);
    unpackBytes(buf, 0, tile, ifd);

    return buf;
  }

  /** Reads the image defined in the given IFD from the input source. */
  public byte[][] getSamples(IFD ifd) throws FormatException, IOException {
    return getSamples(ifd, 0, 0, (int) ifd.getImageWidth(),
      (int) ifd.getImageLength());
  }

  /** Reads the image defined in the given IFD from the input source. */
  public byte[][] getSamples(IFD ifd, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int samplesPerPixel = ifd.getSamplesPerPixel();
    int bpp = ifd.getBytesPerSample()[0];
    long width = ifd.getImageWidth();
    long length = ifd.getImageLength();
    byte[] b = new byte[(int) (w * h * samplesPerPixel * bpp)];

    getSamples(ifd, b, x, y, w, h);
    byte[][] samples = new byte[samplesPerPixel][(int) (w * h * bpp)];
    for (int i=0; i<samplesPerPixel; i++) {
      System.arraycopy(b, (int) (i*w*h*bpp), samples[i], 0, samples[i].length);
    }
    b = null;
    return samples;
  }

  public byte[] getSamples(IFD ifd, byte[] buf)
    throws FormatException, IOException
  {
    long width = ifd.getImageWidth();
    long length = ifd.getImageLength();
    return getSamples(ifd, buf, 0, 0, width, length);
  }

  public byte[] getSamples(IFD ifd, byte[] buf, int x, int y,
    long width, long height) throws FormatException, IOException
  {
    LogTools.debug("parsing IFD entries");

    // get internal non-IFD entries
    boolean littleEndian = ifd.isLittleEndian();
    in.order(littleEndian);

    // get relevant IFD entries
    int samplesPerPixel = ifd.getSamplesPerPixel();
    long tileWidth = ifd.getTileWidth();
    long tileLength = ifd.getTileLength();
    if (tileLength <= 0) {
      LogTools.debug("Tile length is " + tileLength +
        "; setting it to " + height);
      tileLength = height;
    }

    long numTileRows = ifd.getTilesPerColumn();
    long numTileCols = ifd.getTilesPerRow();

    int photoInterp = ifd.getPhotometricInterpretation();
    int planarConfig = ifd.getPlanarConfiguration();
    int pixel = ifd.getBytesPerSample()[0];
    int effectiveChannels = planarConfig == 2 ? 1 : samplesPerPixel;

    ifd.printIFD();

    if (width * height > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth x ImageLength > " +
        Integer.MAX_VALUE + " is not supported (" +
        width + " x " + height + ")");
    }
    if (width * height * effectiveChannels * pixel > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth x ImageLength x " +
        "SamplesPerPixel x BitsPerSample > " + Integer.MAX_VALUE +
        " is not supported (" + width + " x " + height + " x " +
        samplesPerPixel + " x " + (pixel * 8) + ")");
    }

    // casting to int is safe because we have already determined that
    // width * height is less than Integer.MAX_VALUE
    int numSamples = (int) (width * height);

    // read in image strips
    LogTools.debug("reading image data (samplesPerPixel=" +
      samplesPerPixel + "; numSamples=" + numSamples + ")");

    int compression = ifd.getCompression();

    // special case: if we only need one tile, and that tile doesn't need
    // any special handling, then we can just read it directly and return
    if ((x % tileWidth) == 0 && (y % tileLength) == 0 && width == tileWidth &&
      height == tileLength && samplesPerPixel == 1 &&
      (ifd.getBitsPerSample()[0] % 8) == 0 &&
      photoInterp != PhotoInterp.WHITE_IS_ZERO &&
      photoInterp != PhotoInterp.CMYK && photoInterp != PhotoInterp.Y_CB_CR &&
      compression == TiffCompression.UNCOMPRESSED)
    {
      long[] stripOffsets = ifd.getStripOffsets();
      long[] stripByteCounts = ifd.getStripByteCounts();

      int tile = (int) ((y / tileLength) * numTileCols + (x / tileWidth));
      in.seek(stripOffsets[tile]);
      in.read(buf, 0, (int) Math.min(buf.length, stripByteCounts[tile]));
      return buf;
    }

    long nrows = numTileRows;
    if (planarConfig == 2) numTileRows *= samplesPerPixel;

    Region imageBounds = new Region(x, y, (int) width,
      (int) (height * (samplesPerPixel / effectiveChannels)));

    int endX = (int) width + x;
    int endY = (int) height + y;

    int rowLen = pixel * (int) tileWidth;
    int tileSize = (int) (rowLen * tileLength);
    int planeSize = (int) (width * height * pixel);
    int outputRowLen = (int) (pixel * width);

    int bufferSizeSamplesPerPixel = samplesPerPixel;
    if (ifd.getPlanarConfiguration() == 2) bufferSizeSamplesPerPixel = 1;
    int bpp = ifd.getBytesPerSample()[0];
    if (bpp == 3) bpp = 4;
    int bufferSize = (int) tileWidth * (int) tileLength *
      bufferSizeSamplesPerPixel * bpp;
    if (cachedTileBuffer == null || cachedTileBuffer.length != bufferSize) {
      cachedTileBuffer = new byte[bufferSize];
    }

    Region tileBounds = new Region(0, 0, (int) tileWidth, (int) tileLength);

    for (int row=0; row<numTileRows; row++) {
      for (int col=0; col<numTileCols; col++) {
        tileBounds.x = col * (int) tileWidth;
        tileBounds.y = row * (int) tileLength;

        if (!imageBounds.intersects(tileBounds)) continue;

        if (planarConfig == 2) {
          tileBounds.y = (int) ((row % nrows) * tileLength);
        }

        getTile(ifd, cachedTileBuffer, row, col);

        // adjust tile bounds, if necessary

        int tileX = (int) Math.max(tileBounds.x, x);
        int tileY = (int) Math.max(tileBounds.y, y);
        int realX = tileX % (int) tileWidth;
        int realY = tileY % (int) tileLength;

        int twidth = (int) Math.min(endX - tileX, tileWidth - realX);
        int theight = (int) Math.min(endY - tileY, tileLength - realY);

        // copy appropriate portion of the tile to the output buffer

        int copy = pixel * twidth;

        realX *= pixel;
        realY *= rowLen;

        for (int q=0; q<effectiveChannels; q++) {
          int src = (int) (q * tileSize) + realX + realY;
          int dest = (int) (q * planeSize) + pixel * (tileX - x) +
            outputRowLen * (tileY - y);
          if (planarConfig == 2) dest += (planeSize * (row / nrows));
          for (int tileRow=0; tileRow<theight; tileRow++) {
            System.arraycopy(cachedTileBuffer, src, buf, dest, copy);
            src += rowLen;
            dest += outputRowLen;
          }
        }
      }
    }

    return buf;
  }

  // -- Utility methods - byte stream decoding --

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation, and the specified byte
   * ordering.
   * No error checking is performed.
   * This method is tailored specifically for planar (separated) images.
   */
  public static void planarUnpack(byte[] samples, int startIndex, byte[] bytes,
    IFD ifd) throws FormatException
  {
    BitBuffer bb = new BitBuffer(bytes);

    long imageWidth = ifd.getTileWidth();
    long imageLength = ifd.getTileLength();
    int numBytes = ifd.getBytesPerSample()[0];
    if (numBytes == 3) numBytes++;

    int bitsPerSample = ifd.getBitsPerSample()[0];
    int nSamples = ((bytes.length - startIndex) * 8) / bitsPerSample;
    boolean littleEndian = ifd.isLittleEndian();
    int photoInterp = ifd.getPhotometricInterpretation();

    int skipBits = (int) (8 - ((imageWidth * bitsPerSample) % 8));
    if (skipBits == 8) skipBits = 0;
    nSamples -= (int) ((imageLength * skipBits) / bitsPerSample);

    for (int j=0; j<nSamples; j++) {
      int value = bb.getBits(bitsPerSample);
      if (littleEndian) value = DataTools.swap(value) >> (32 - bitsPerSample);

      if (photoInterp == PhotoInterp.WHITE_IS_ZERO) {
        value = (int) (Math.pow(2, bitsPerSample) - 1 - value);
      }
      else if (photoInterp == PhotoInterp.CMYK) {
        value = Integer.MAX_VALUE - value;
      }

      if ((j % imageWidth) == imageWidth - 1) {
        bb.skipBits(skipBits);
      }

      if (numBytes*(startIndex + j) < samples.length) {
        DataTools.unpackBytes(value, samples, numBytes*(startIndex + j),
          numBytes, littleEndian);
      }
    }
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation and color map IFD directory
   * entry values, and the specified byte ordering.
   * No error checking is performed.
   */
  public static void unpackBytes(byte[] samples, int startIndex, byte[] bytes,
    IFD ifd) throws FormatException
  {
    if (ifd.getPlanarConfiguration() == 2) {
      planarUnpack(samples, startIndex, bytes, ifd);
      return;
    }

    int compression = ifd.getCompression();
    int photoInterp = ifd.getPhotometricInterpretation();
    if (compression == TiffCompression.JPEG) photoInterp = PhotoInterp.RGB;

    int[] bitsPerSample = ifd.getBitsPerSample();
    int nChannels = bitsPerSample.length;
    int nSamples = samples.length / nChannels;

    int totalBits = 0;
    for (int i=0; i<nChannels; i++) totalBits += bitsPerSample[i];
    int sampleCount = 8 * bytes.length / totalBits;
    if (photoInterp == PhotoInterp.Y_CB_CR) sampleCount *= 3;

    LogTools.debug("unpacking " + sampleCount + " samples (startIndex=" +
      startIndex + "; totalBits=" + totalBits +
      "; numBytes=" + bytes.length + ")");

    long imageWidth = ifd.getTileWidth();

    int bps0 = bitsPerSample[0];
    int numBytes = ifd.getBytesPerSample()[0];

    boolean noDiv8 = bps0 % 8 != 0;
    boolean bps8 = bps0 == 8;
    boolean bps16 = bps0 == 16;

    int row = startIndex / (int) imageWidth;
    int col = 0;

    int cw = 0, ch = 0;

    boolean littleEndian = ifd.isLittleEndian();

    int[] reference = ifd.getIFDIntArray(IFD.REFERENCE_BLACK_WHITE, false);
    int[] subsampling = ifd.getIFDIntArray(IFD.Y_CB_CR_SUB_SAMPLING, false);
    TiffRational[] coefficients = (TiffRational[])
      ifd.getIFDValue(IFD.Y_CB_CR_COEFFICIENTS);

    int count = 0;

    BitBuffer bb = new BitBuffer(bytes);

    // Hyper optimisation that takes any 8-bit or 16-bit data, where there is
    // only one channel, the source byte buffer's size is less than or equal to
    // that of the destination buffer and for which no special unpacking is
    // required and performs a simple array copy. Over the course of reading
    // semi-large datasets this can save **billions** of method calls.
    // Wed Aug  5 19:04:59 BST 2009
    // Chris Allan <callan@glencoesoftware.com>
    if ((bps8 || bps16) && bytes.length <= samples.length && nChannels == 1
        && photoInterp != PhotoInterp.WHITE_IS_ZERO
        && photoInterp != PhotoInterp.CMYK
        && photoInterp != PhotoInterp.Y_CB_CR) {
      System.arraycopy(bytes, 0, samples, 0, bytes.length);
      return;
    }

    int skipBits = (int) (8 - ((imageWidth * bps0 * nChannels) % 8));
    if (skipBits == 8) skipBits = 0;

    for (int j=0; j<sampleCount; j++) {
      for (int i=0; i<nChannels; i++) {
        int index = numBytes * (j * nChannels + i);
        int ndx = startIndex + j;
        if (ndx >= nSamples) {
          break;
        }
        int outputIndex = i * nSamples + ndx * numBytes;

        if (noDiv8) {
          // bits per sample is not a multiple of 8

          short s = 0;
          if ((i == 0 && photoInterp == PhotoInterp.RGB_PALETTE) ||
            (photoInterp != PhotoInterp.CFA_ARRAY &&
            photoInterp != PhotoInterp.RGB_PALETTE))
          {
            try {
              s = (short) (bb.getBits(bps0) & 0xffff);
            }
            catch (ArrayIndexOutOfBoundsException e) { }
            if ((ndx % imageWidth) == imageWidth - 1 && i == nChannels - 1) {
              bb.skipBits(skipBits);
            }
          }

          if (photoInterp == PhotoInterp.WHITE_IS_ZERO ||
            photoInterp == PhotoInterp.CMYK)
          {
            // invert colors
            s = (short) (Math.pow(2, bitsPerSample[0]) - 1 - s);
          }

          if (outputIndex + numBytes <= samples.length) {
            DataTools.unpackBytes(s, samples, outputIndex, numBytes,
              littleEndian);
          }
        }
        else if (bps8) {
          // special case handles 8-bit data more quickly

          if (outputIndex >= samples.length) break;

          if (photoInterp != PhotoInterp.Y_CB_CR) {
            samples[outputIndex] = (byte) (bytes[index] & 0xff);
          }

          if (photoInterp == PhotoInterp.WHITE_IS_ZERO) { // invert color value
            samples[outputIndex] = (byte) (255 - samples[outputIndex]);
          }
          else if (photoInterp == PhotoInterp.CMYK) {
            samples[outputIndex] =
              (byte) (Integer.MAX_VALUE - samples[outputIndex]);
          }
          else if (photoInterp == PhotoInterp.Y_CB_CR) {
            if (i == bitsPerSample.length - 1) {
              float lumaRed = 0.299f;
              float lumaGreen = 0.587f;
              float lumaBlue = 0.114f;
              if (coefficients != null) {
                lumaRed = coefficients[0].floatValue();
                lumaGreen = coefficients[1].floatValue();
                lumaBlue = coefficients[2].floatValue();
              }

              int subX = subsampling == null ? 2 : subsampling[0];
              int subY = subsampling == null ? 2 : subsampling[1];

              int block = subX * subY;
              int lumaIndex = j + (2 * (j / block));
              int chromaIndex = (j / block) * (block + 2) + block;

              if (chromaIndex + 1 >= bytes.length) break;

              int tile = ndx / block;
              int pixel = ndx % block;
              int nTiles = (int) (imageWidth / subX);
              long r = subY * (tile / nTiles) + (pixel / subX);
              long c = subX * (tile % nTiles) + (pixel % subX);

              int idx = (int) (r * imageWidth + c);

              if (idx < nSamples) {
                if (reference == null) {
                  reference = new int[] {0, 0, 0, 0, 0, 0};
                }
                int y = (bytes[lumaIndex] & 0xff) - reference[0];
                int cb = (bytes[chromaIndex] & 0xff) - reference[2];
                int cr = (bytes[chromaIndex + 1] & 0xff) - reference[4];

                int red = (int) (cr * (2 - 2 * lumaRed) + y);
                int blue = (int) (cb * (2 - 2 * lumaBlue) + y);
                int green = (int)
                  ((y - lumaBlue * blue - lumaRed * red) / lumaGreen);

                samples[idx] = (byte) red;
                samples[nSamples + idx] = (byte) green;
                samples[2*nSamples + idx] = (byte) blue;
              }
            }
          }
        } // end if (bps8)
        else {
          int offset = numBytes + index < bytes.length ?
            index : bytes.length - numBytes;
          long v = DataTools.bytesToLong(bytes, offset, numBytes, littleEndian);

          if (photoInterp == PhotoInterp.WHITE_IS_ZERO) { // invert color value
            long max = (long) Math.pow(2, numBytes * 8) - 1;
            v = max - v;
          }
          else if (photoInterp == PhotoInterp.CMYK) {
            v = Integer.MAX_VALUE - v;
          }
          if (ndx*numBytes >= nSamples) break;
          int length = numBytes == 3 ? 4 : numBytes;
          DataTools.unpackBytes(v, samples, i*nSamples + ndx*length,
            length, littleEndian);
        } // end else
      }
    }
  }

  /**
   * Read a file offset.
   * For bigTiff, a 64-bit number is read.  For other Tiffs, a 32-bit number
   * is read and possibly adjusted for a possible carry-over from the previous
   * offset.
   */
  long getNextOffset(boolean bigTiff, long previous) throws IOException {
    if (bigTiff) {
      return in.readLong();
    }
    long offset = (previous & ~0xffffffffL) | (in.readInt() & 0xffffffffL);
    if (offset < previous) {
      offset += 0x100000000L;
    }
    return offset;
  }
}
