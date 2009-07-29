//
// TiffSaver.java
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import loci.common.DataTools;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;

/**
 * Parses TIFF data from an input source.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tiff/TiffSaver.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tiff/TiffSaver.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffSaver {

  // -- Fields --

  /** Output stream to use when saving TIFF data. */
  protected RandomAccessOutputStream out;

  // -- Constructors --

  /** Constructs a new TIFF saver from the given output source. */
  public TiffSaver(RandomAccessOutputStream out) {
    this.out = out;
  }

  // -- TiffSaver methods --

  /** Gets the stream from which TIFF data is being saved. */
  public RandomAccessOutputStream getStream() {
    return out;
  }

  /** Writes the TIFF file header. */
  public void writeHeader(boolean littleEndian, boolean bigTiff)
    throws IOException
  {
    // write endianness indicator
    if (littleEndian) {
      out.writeByte(TiffConstants.LITTLE);
      out.writeByte(TiffConstants.LITTLE);
    }
    else {
      out.writeByte(TiffConstants.BIG);
      out.writeByte(TiffConstants.BIG);
    }
    // write magic number
    if (bigTiff) {
      DataTools.writeShort(out,
        TiffConstants.BIG_TIFF_MAGIC_NUMBER, littleEndian);
    }
    else DataTools.writeShort(out, TiffConstants.MAGIC_NUMBER, littleEndian);

    // write the offset to the first IFD

    // for vanilla TIFFs, 8 is the offset to the first IFD
    // for BigTIFFs, 8 is the number of bytes in an offset
    DataTools.writeInt(out, 8, littleEndian);
    if (bigTiff) {
      // write the offset to the first IFD for BigTIFF files
      DataTools.writeLong(out, 16, littleEndian);
    }
  }

  // -- Utility methods --

  /**
   * Writes the given IFD value to the given output object.
   * @param ifdOut output object for writing IFD stream
   * @param extraBuf buffer to which "extra" IFD information should be written
   * @param extraOut data output wrapper for extraBuf (passed for efficiency)
   * @param offset global offset to use for IFD offset values
   * @param tag IFD tag to write
   * @param value IFD value to write
   */
  public static void writeIFDValue(DataOutput ifdOut,
    ByteArrayOutputStream extraBuf, DataOutputStream extraOut, long offset,
    int tag, Object value, boolean bigTiff, boolean littleEndian)
    throws FormatException, IOException
  {
    // convert singleton objects into arrays, for simplicity
    if (value instanceof Short) {
      value = new short[] {((Short) value).shortValue()};
    }
    else if (value instanceof Integer) {
      value = new int[] {((Integer) value).intValue()};
    }
    else if (value instanceof Long) {
      value = new long[] {((Long) value).longValue()};
    }
    else if (value instanceof TiffRational) {
      value = new TiffRational[] {(TiffRational) value};
    }
    else if (value instanceof Float) {
      value = new float[] {((Float) value).floatValue()};
    }
    else if (value instanceof Double) {
      value = new double[] {((Double) value).doubleValue()};
    }

    int dataLength = bigTiff ? 8 : 4;

    // write directory entry to output buffers
    DataTools.writeShort(ifdOut, tag, littleEndian); // tag
    if (value instanceof short[]) {
      short[] q = (short[]) value;
      DataTools.writeShort(ifdOut, IFD.BYTE, littleEndian);
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]);
        for (int i=q.length; i<dataLength; i++) ifdOut.writeByte(0);
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]);
      }
    }
    else if (value instanceof String) { // ASCII
      char[] q = ((String) value).toCharArray();
      DataTools.writeShort(ifdOut, IFD.ASCII, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length + 1, littleEndian);
      else DataTools.writeInt(ifdOut, q.length + 1, littleEndian);
      if (q.length < dataLength) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]); // value(s)
        for (int i=q.length; i<dataLength; i++) ifdOut.writeByte(0); // padding
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
        extraOut.writeByte(0); // concluding NULL byte
      }
    }
    else if (value instanceof int[]) { // SHORT
      int[] q = (int[]) value;
      DataTools.writeShort(ifdOut, IFD.SHORT, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength / 2) {
        for (int i=0; i<q.length; i++) {
          DataTools.writeShort(ifdOut, q[i], littleEndian); // value(s)
        }
        for (int i=q.length; i<dataLength / 2; i++) {
          DataTools.writeShort(ifdOut, 0, littleEndian); // padding
        }
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeShort(extraOut, q[i], littleEndian); // values
        }
      }
    }
    else if (value instanceof long[]) { // LONG
      long[] q = (long[]) value;

      if (bigTiff) {
        DataTools.writeShort(ifdOut, IFD.LONG8, littleEndian);
        DataTools.writeLong(ifdOut, q.length, littleEndian);

        if (q.length <= dataLength / 4) {
          for (int i=0; i<q.length; i++) {
            DataTools.writeLong(ifdOut, q[0], littleEndian);
          }
          for (int i=q.length; i<dataLength / 4; i++) {
            DataTools.writeLong(ifdOut, 0, littleEndian);
          }
        }
        else {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
          for (int i=0; i<q.length; i++) {
            DataTools.writeLong(extraOut, q[i], littleEndian);
          }
        }
      }
      else {
        DataTools.writeShort(ifdOut, IFD.LONG, littleEndian);
        DataTools.writeInt(ifdOut, q.length, littleEndian);
        if (q.length <= dataLength / 4) {
          for (int i=0; i<q.length; i++) {
            DataTools.writeInt(ifdOut, (int) q[0], littleEndian);
          }
          for (int i=q.length; i<dataLength / 4; i++) {
            DataTools.writeInt(ifdOut, 0, littleEndian); // padding
          }
        }
        else {
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
          for (int i=0; i<q.length; i++) {
            DataTools.writeInt(extraOut, (int) q[i], littleEndian);
          }
        }
      }
    }
    else if (value instanceof TiffRational[]) { // RATIONAL
      TiffRational[] q = (TiffRational[]) value;
      DataTools.writeShort(ifdOut, IFD.RATIONAL, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (bigTiff && q.length == 1) {
        DataTools.writeInt(ifdOut, (int) q[0].getNumerator(), littleEndian);
        DataTools.writeInt(ifdOut, (int) q[0].getDenominator(), littleEndian);
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeInt(extraOut, (int) q[i].getNumerator(), littleEndian);
          DataTools.writeInt(extraOut, (int) q[i].getDenominator(),
            littleEndian);
        }
      }
    }
    else if (value instanceof float[]) { // FLOAT
      float[] q = (float[]) value;
      DataTools.writeShort(ifdOut, IFD.FLOAT, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength / 4) {
        for (int i=0; i<q.length; i++) {
          DataTools.writeFloat(ifdOut, q[0], littleEndian); // value
        }
        for (int i=q.length; i<dataLength / 4; i++) {
          DataTools.writeInt(ifdOut, 0, littleEndian); // padding
        }
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeFloat(extraOut, q[i], littleEndian); // values
        }
      }
    }
    else if (value instanceof double[]) { // DOUBLE
      double[] q = (double[]) value;
      DataTools.writeShort(ifdOut, IFD.DOUBLE, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (bigTiff) {
        DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
      }
      else {
        DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
          littleEndian);
      }
      for (int i=0; i<q.length; i++) {
        DataTools.writeDouble(extraOut, q[i], littleEndian); // values
      }
    }
    else {
      throw new FormatException("Unknown IFD value type (" +
        value.getClass().getName() + "): " + value);
    }
  }

  /**
   * Surgically overwrites an existing IFD value with the given one. This
   * method requires that the IFD directory entry already exist. It
   * intelligently updates the count field of the entry to match the new
   * length. If the new length is longer than the old length, it appends the
   * new data to the end of the file and updates the offset field; if not, or
   * if the old data is already at the end of the file, it overwrites the old
   * data in place.
   */
  public static void overwriteIFDValue(String file,
    int ifd, int tag, Object value) throws FormatException, IOException
  {
    LogTools.debug("overwriteIFDValue (ifd=" + ifd + "; tag=" + tag +
      "; value=" + value + ")");
    byte[] header = new byte[4];

    RandomAccessInputStream raf = new RandomAccessInputStream(file);
    raf.seek(0);
    raf.readFully(header);
    if (!TiffTools.isValidHeader(header)) {
      throw new FormatException("Invalid TIFF header");
    }
    boolean little =
      header[0] == TiffConstants.LITTLE &&
      header[1] == TiffConstants.LITTLE; // II
    boolean bigTiff =
      header[2] == TiffConstants.BIG_TIFF_MAGIC_NUMBER ||
      header[3] == TiffConstants.BIG_TIFF_MAGIC_NUMBER;
    long offset = bigTiff ? 8 : 4; // offset to the IFD
    long num = 0; // number of directory entries

    int baseOffset = bigTiff ? 8 : 2;
    int bytesPerEntry = bigTiff ?
      TiffConstants.BIG_TIFF_BYTES_PER_ENTRY : TiffConstants.BYTES_PER_ENTRY;

    raf.seek(offset);

    // skip to the correct IFD
    for (int i=0; i<=ifd; i++) {
      offset = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read4UnsignedBytes(raf, little);
      if (offset <= 0) {
        throw new FormatException("No such IFD (" + ifd + " of " + i + ")");
      }
      raf.seek(offset);
      num = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read2UnsignedBytes(raf, little);
      if (i < ifd) raf.seek(offset + baseOffset + bytesPerEntry * num);
    }

    // search directory entries for proper tag
    for (int i=0; i<num; i++) {
      int oldTag = DataTools.read2UnsignedBytes(raf, little);
      int oldType = DataTools.read2UnsignedBytes(raf, little);
      int oldCount =
        bigTiff ? (int) (DataTools.read8SignedBytes(raf, little) & 0xffffffff) :
        DataTools.read4SignedBytes(raf, little);
      long oldOffset = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read4SignedBytes(raf, little);
      if (oldTag == tag) {
        // write new value to buffers
        ByteArrayOutputStream ifdBuf = new ByteArrayOutputStream(bytesPerEntry);
        DataOutputStream ifdOut = new DataOutputStream(ifdBuf);
        ByteArrayOutputStream extraBuf = new ByteArrayOutputStream();
        DataOutputStream extraOut = new DataOutputStream(extraBuf);
        writeIFDValue(ifdOut, extraBuf, extraOut, oldOffset, tag, value,
          bigTiff, little);
        byte[] bytes = ifdBuf.toByteArray();
        byte[] extra = extraBuf.toByteArray();

        // extract new directory entry parameters
        int newTag = DataTools.bytesToInt(bytes, 0, 2, little);
        int newType = DataTools.bytesToInt(bytes, 2, 2, little);
        int newCount;
        long newOffset;
        if (bigTiff) {
          newCount =
            (int) (DataTools.bytesToLong(bytes, 4, little) & 0xffffffff);
          newOffset = DataTools.bytesToLong(bytes, 12, little);
        }
        else {
          newCount = DataTools.bytesToInt(bytes, 4, little);
          newOffset = DataTools.bytesToInt(bytes, 8, little);
        }
        boolean terminate = false;
        LogTools.debug("overwriteIFDValue:\n\told: (tag=" + oldTag +
          "; type=" + oldType + "; count=" + oldCount + "; offset=" +
          oldOffset + ");\n\tnew: (tag=" + newTag + "; type=" + newType +
          "; count=" + newCount + "; offset=" + newOffset + ")");

        // determine the best way to overwrite the old entry
        if (extra.length == 0) {
          // new entry is inline; if old entry wasn't, old data is orphaned
          // do not override new offset value since data is inline
          LogTools.debug("overwriteIFDValue: new entry is inline");
        }
        else if (oldOffset +
          oldCount * IFD.getIFDTypeLength(oldType) == raf.length())
        {
          // old entry was already at EOF; overwrite it
          newOffset = oldOffset;
          terminate = true;
          LogTools.debug("overwriteIFDValue: old entry is at EOF");
        }
        else if (newCount <= oldCount) {
          // new entry is as small or smaller than old entry; overwrite it
          newOffset = oldOffset;
          LogTools.debug("overwriteIFDValue: new entry is <= old entry");
        }
        else {
          // old entry was elsewhere; append to EOF, orphaning old entry
          newOffset = raf.length();
          LogTools.debug("overwriteIFDValue: old entry will be orphaned");
        }

        long filePointer = raf.getFilePointer();
        raf.close();

        RandomAccessOutputStream out = new RandomAccessOutputStream(file);

        // overwrite old entry
        out.seek(filePointer - (bigTiff ? 18 : 10)); // jump back
        DataTools.writeShort(out, newType, little);
        if (bigTiff) DataTools.writeLong(out, newCount, little);
        else DataTools.writeInt(out, newCount, little);
        if (bigTiff) DataTools.writeLong(out, newOffset, little);
        else DataTools.writeInt(out, (int) newOffset, little);
        if (extra.length > 0) {
          out.seek(newOffset);
          out.write(extra);
        }
        return;
      }
    }

    throw new FormatException("Tag not found (" + IFD.getIFDTagName(tag) + ")");
  }

  /** Convenience method for overwriting a file's first ImageDescription. */
  public static void overwriteComment(String id, Object value)
    throws FormatException, IOException
  {
    overwriteIFDValue(id, 0, IFD.IMAGE_DESCRIPTION, value);
  }

}
