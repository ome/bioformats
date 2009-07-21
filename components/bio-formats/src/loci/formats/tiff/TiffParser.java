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

import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.TiffTools;
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
    boolean littleEndian = endianOne == TiffTools.LITTLE &&
      endianTwo == TiffTools.LITTLE; // II
    boolean bigEndian = endianOne == TiffTools.BIG &&
      endianTwo == TiffTools.BIG; // MM
    if (!littleEndian && !bigEndian) return null;

    // check magic number (42)
    in.order(littleEndian);
    short magic = in.readShort();
    if (magic != TiffTools.MAGIC_NUMBER &&
      magic != TiffTools.BIG_TIFF_MAGIC_NUMBER)
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
    // check TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffTools.BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(bigTiff);

    // compute maximum possible number of IFDs, for loop safety
    // each IFD must have at least one directory entry, which means that
    // each IFD must be at least 2 + 12 + 4 = 18 bytes in length
    long ifdMax = (in.length() - 8) / 18;

    // read in IFDs
    IFDList ifds = new IFDList();
    for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
      IFD ifd = getIFD(ifdNum, offset, bigTiff);
      if (ifd == null || ifd.size() <= 1) break;
      ifds.add(ifd);
      offset = bigTiff ? in.readLong() : (long) (in.readInt() & 0xffffffffL);
      if (offset <= 0 || offset >= in.length()) {
        if (offset != 0) {
          LogTools.debug("getIFDs: invalid IFD offset: " + offset);
        }
        break;
      }
    }

    return ifds;
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
    boolean bigTiff = in.readShort() == TiffTools.BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(bigTiff);

    IFD ifd = getIFD(0, offset, bigTiff);
    ifd.put(new Integer(IFD.BIG_TIFF), new Boolean(bigTiff));
    return ifd;
  }


  /**
   * Retrieve a given entry from the first IFD in the stream.
   *
   * @param tag the tag of the entry to be retrieved.
   * @return an object representing the entry's fields.
   * @throws IOException when there is an error accessing the stream.
   */
  public TiffIFDEntry getFirstIFDEntry(int tag) throws IOException {
    // First lets re-position the file pointer by checking the TIFF header
    Boolean result = checkHeader();
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == TiffTools.BIG_TIFF_MAGIC_NUMBER;

    // Get the offset of the first IFD
    long offset = getFirstOffset(bigTiff);

    // The following loosely resembles the logic of getIFD()...
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;

    for (int i = 0; i < numEntries; i++) {
      in.seek(offset + // The beginning of the IFD
        2 + // The width of the initial numEntries field
        (bigTiff ? TiffTools.BIG_TIFF_BYTES_PER_ENTRY :
        TiffTools.BYTES_PER_ENTRY) * i);

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
      long valueOffset = bigTiff ? in.readLong() : in.readInt();

      return new TiffIFDEntry(entryTag, entryType, valueCount, valueOffset);
    }
    throw new UnknownTagException();
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
      TiffTools.BIG_TIFF_BYTES_PER_ENTRY : TiffTools.BYTES_PER_ENTRY;
    int baseOffset = bigTiff ? 8 : 2;
    int threshhold = bigTiff ? 8 : 4;

    for (int i=0; i<numEntries; i++) {
      in.seek(offset + baseOffset + bytesPerEntry * i);
      int tag = in.readShort() & 0xffff;
      int type = in.readShort() & 0xffff;
      // BigTIFF case is a slight hack
      int count = bigTiff ? (int) (in.readLong() & 0xffffffff) : in.readInt();

      LogTools.debug("getIFDs: read " + IFD.getIFDTagName(tag) +
        " (type=" + IFD.getIFDTypeName(type) + "; count=" + count + ")");
      if (count < 0) return null; // invalid data
      Object value = null;

      int bpe = IFD.getIFDTypeLength(type);
      if (bpe <= 0) return null; // invalid data

      if (count > threshhold / bpe) {
        long pointer = bigTiff ? in.readLong() :
          (long) (in.readInt() & 0xffffffffL);
        LogTools.debug("getIFDs: seeking to offset: " + pointer);
        in.seek(pointer);
      }
      long inputLen = in.length();
      long inputPointer = in.getFilePointer();
      if (count * bpe + inputPointer > inputLen) {
        int oldCount = count;
        count = (int) ((inputLen - inputPointer) / bpe);
        LogTools.debug("getIFDs: truncated " + (oldCount - count) +
          " array elements for tag " + tag);
      }

      if (type == IFD.BYTE) {
        // 8-bit unsigned integer
        if (count == 1) value = new Short(in.readByte());
        else {
          byte[] bytes = new byte[count];
          in.readFully(bytes);
          // bytes are unsigned, so use shorts
          short[] shorts = new short[count];
          for (int j=0; j<count; j++) shorts[j] = (short) (bytes[j] & 0xff);
          value = shorts;
        }
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
        value = strings == null ? (Object) s : strings;
      }
      else if (type == IFD.SHORT) {
        // 16-bit (2-byte) unsigned integer
        if (count == 1) value = new Integer(in.readShort() & 0xffff);
        else {
          int[] shorts = new int[count];
          for (int j=0; j<count; j++) {
            shorts[j] = in.readShort() & 0xffff;
          }
          value = shorts;
        }
      }
      else if (type == IFD.LONG) {
        // 32-bit (4-byte) unsigned integer
        if (count == 1) value = new Long(in.readInt());
        else {
          long[] longs = new long[count];
          for (int j=0; j<count; j++) longs[j] = in.readInt();
          value = longs;
        }
      }
      else if (type == IFD.LONG8 || type == IFD.SLONG8 || type == IFD.IFD8) {
        if (count == 1) value = new Long(in.readLong());
        else {
          long[] longs = new long[count];
          for (int j=0; j<count; j++) longs[j] = in.readLong();
          value = longs;
        }
      }
      else if (type == IFD.RATIONAL || type == IFD.SRATIONAL) {
        // Two LONGs or SLONGs: the first represents the numerator
        // of a fraction; the second, the denominator
        if (count == 1) value = new TiffRational(in.readInt(), in.readInt());
        else {
          TiffRational[] rationals = new TiffRational[count];
          for (int j=0; j<count; j++) {
            rationals[j] = new TiffRational(in.readInt(), in.readInt());
          }
          value = rationals;
        }
      }
      else if (type == IFD.SBYTE || type == IFD.UNDEFINED) {
        // SBYTE: An 8-bit signed (twos-complement) integer
        // UNDEFINED: An 8-bit byte that may contain anything,
        // depending on the definition of the field
        if (count == 1) value = new Byte(in.readByte());
        else {
          byte[] sbytes = new byte[count];
          in.read(sbytes);
          value = sbytes;
        }
      }
      else if (type == IFD.SSHORT) {
        // A 16-bit (2-byte) signed (twos-complement) integer
        if (count == 1) value = new Short(in.readShort());
        else {
          short[] sshorts = new short[count];
          for (int j=0; j<count; j++) sshorts[j] = in.readShort();
          value = sshorts;
        }
      }
      else if (type == IFD.SLONG) {
        // A 32-bit (4-byte) signed (twos-complement) integer
        if (count == 1) value = new Integer(in.readInt());
        else {
          int[] slongs = new int[count];
          for (int j=0; j<count; j++) slongs[j] = in.readInt();
          value = slongs;
        }
      }
      else if (type == IFD.FLOAT) {
        // Single precision (4-byte) IEEE format
        if (count == 1) value = new Float(in.readFloat());
        else {
          float[] floats = new float[count];
          for (int j=0; j<count; j++) floats[j] = in.readFloat();
          value = floats;
        }
      }
      else if (type == IFD.DOUBLE) {
        // Double precision (8-byte) IEEE format
        if (count == 1) value = new Double(in.readDouble());
        else {
          double[] doubles = new double[count];
          for (int j=0; j<count; j++) {
            doubles[j] = in.readDouble();
          }
          value = doubles;
        }
      }
      if (value != null) ifd.put(new Integer(tag), value);
    }
    in.seek(offset + baseOffset + bytesPerEntry * numEntries);

    return ifd;
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
    in.seek(stripOffsets[tileNumber] & 0xffffffffL);
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
    TiffTools.unpackBytes(buf, 0, tile, ifd);

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

  public byte[] getSamples(IFD ifd,byte[] buf)
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
    long numTileRows = ifd.getTilesPerColumn();
    long numTileCols = ifd.getTilesPerRow();

    int planarConfig = ifd.getPlanarConfiguration();

    ifd.printIFD();

    if (width * height > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth x ImageLength > " +
        Integer.MAX_VALUE + " is not supported (" +
        width + " x " + height + ")");
    }
    int numSamples = (int) (width * height);

    // read in image strips
    LogTools.debug("reading image data (samplesPerPixel=" +
      samplesPerPixel + "; numSamples=" + numSamples + ")");

    int pixel = ifd.getBytesPerSample()[0];
    int effectiveChannels = planarConfig == 2 ? 1 : samplesPerPixel;
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

    for (int row=0; row<numTileRows; row++) {
      for (int col=0; col<numTileCols; col++) {
        Region tileBounds = new Region(col * (int) tileWidth,
          (int) (row * tileLength), (int) tileWidth, (int) tileLength);

        if (!imageBounds.intersects(tileBounds)) continue;

        if (planarConfig == 2) {
          tileBounds.y = (int) ((row % nrows) * tileLength);
        }

        byte[] tile = getTile(ifd, row, col);

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
            System.arraycopy(tile, src, buf, dest, copy);
            src += rowLen;
            dest += outputRowLen;
          }
        }
      }
    }

    return buf;
  }

}
