/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.out;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffRational;

/**
 * Writes little-endian classic TIFF directories with NDPI extensions.
 *
 * Value encoding, external-value writing and directory emission are separate
 * steps, so that a directory can reference values written earlier in the
 * file. Genuine Hamamatsu files place every value an IFD names, and the image
 * payload the IFD describes, before the IFD itself, so those offsets point
 * backwards while the next-IFD pointer may link forward.
 *
 * The header's first-IFD field and each directory's next-IFD field are 64
 * bits wide and are left zero until the directory they name has been written,
 * then filled in by patchOffset().
 */
final class NDPIClassicTiffWriter {

  // -- Constants --

  /** Header length; unlike classic TIFF the first-IFD pointer is 64 bits. */
  static final int HEADER_LENGTH = 12;

  /** Location of the header's 64-bit first-IFD pointer. */
  static final long FIRST_IFD_POINTER = 4;

  private static final int ASCII = 2;
  private static final int SHORT = 3;
  private static final int LONG = 4;
  private static final int RATIONAL = 5;
  private static final int SLONG = 9;
  private static final int FLOAT = 11;

  private static final Comparator<Entry> BY_TAG = new Comparator<Entry>() {
    @Override
    public int compare(Entry left, Entry right) {
      return Integer.compare(left.value.tag, right.value.tag);
    }
  };

  // -- Fields --

  private final RandomAccessOutputStream out;

  // -- Constructor --

  NDPIClassicTiffWriter(RandomAccessOutputStream out) {
    this.out = out;
  }

  // -- NDPIClassicTiffWriter API methods --

  /**
   * Writes the file header with an as yet unknown first directory.
   * FIRST_IFD_POINTER is filled in once the first directory has been written.
   */
  void writeHeader() throws IOException {
    out.seek(0);
    out.order(true);
    out.write('I');
    out.write('I');
    out.writeShort(42);
    out.writeLong(0);
  }

  /**
   * Prepares one directory entry, writing its value now when that value is
   * too large to be stored inline.
   *
   * @param value the encoded value to prepare
   * @return the entry, which may be used by any later directory
   */
  Entry entry(Value value) throws IOException {
    if (!value.external()) return new Entry(value, -1);
    align();
    long offset = out.getFilePointer();
    out.write(value.data);
    return new Entry(value, offset);
  }

  /**
   * Writes one completed directory at the current end of the stream. Every
   * external value the entries name must already have been written, and the
   * directory's next-IFD pointer is left zero.
   *
   * @param entries the directory's entries, in any order
   * @return where the directory and its next-IFD pointer were written
   */
  Directory writeIFD(List<Entry> entries) throws IOException {
    List<Entry> sorted = new ArrayList<Entry>(entries);
    Collections.sort(sorted, BY_TAG);

    align();
    long offset = out.getFilePointer();
    out.writeShort(sorted.size());
    for (Entry entry : sorted) {
      Value value = entry.value;
      out.writeShort(value.tag);
      out.writeShort(value.type);
      out.writeInt(value.count);
      if (value.external()) out.writeInt((int) entry.offset);
      else writePadded(value.data);
    }
    long nextPointer = out.getFilePointer();
    out.writeLong(0);
    for (Entry entry : sorted) out.writeInt((int) entry.highWord());
    return new Directory(offset, nextPointer);
  }

  /**
   * Fills in one 64-bit offset already reserved in the output, leaving the
   * stream where it was so that the caller can continue appending.
   *
   * @param location where the reserved offset begins
   * @param offset the offset to store
   */
  void patchOffset(long location, long offset) throws IOException {
    long end = out.getFilePointer();
    out.seek(location);
    out.writeLong(offset);
    out.seek(end);
  }

  /** Encodes every value of the given directory, writing none of them. */
  static List<Value> values(IFD ifd) throws FormatException {
    List<Integer> tags = new ArrayList<Integer>(ifd.keySet());
    tags.remove(Integer.valueOf(IFD.LITTLE_ENDIAN));
    tags.remove(Integer.valueOf(IFD.BIG_TIFF));
    Collections.sort(tags);
    List<Value> values = new ArrayList<Value>();
    for (Integer tag : tags) values.add(value(tag, ifd.get(tag)));
    return values;
  }

  /** Encodes one TIFF value, writing nothing. */
  static Value value(int tag, Object value) throws FormatException {
    if (value instanceof String) {
      byte[] text = (((String) value) + "\0")
        .getBytes(StandardCharsets.US_ASCII);
      return new Value(tag, ASCII, text.length, text, 0);
    }
    if (value instanceof TiffRational) {
      TiffRational rational = (TiffRational) value;
      return new Value(tag, RATIONAL, 1,
        bytes(rational.getNumerator(), rational.getDenominator()), 0);
    }
    if (value instanceof Float) {
      long bits = Float.floatToIntBits(((Float) value).floatValue()) &
        0xffffffffL;
      return new Value(tag, FLOAT, 1, bytes(bits), bits);
    }
    if (value instanceof Number) {
      long number = ((Number) value).longValue();
      int type = shortTag(tag) ? SHORT : signedLongTag(tag) ? SLONG : LONG;
      return new Value(tag, type, 1,
        type == SHORT ? shorts((int) number) : bytes(number), number);
    }
    if (value instanceof int[]) {
      int[] values = (int[]) value;
      int type = signedLongArrayTag(tag) ? SLONG : SHORT;
      return new Value(tag, type, values.length,
        type == SLONG ? signedBytes(values) : shorts(values), 0);
    }
    if (value instanceof long[]) {
      long[] values = (long[]) value;
      return new Value(tag, LONG, values.length, bytes(values),
        values.length == 1 ? values[0] : 0);
    }
    throw new FormatException("Unsupported NDPI TIFF value for tag " + tag);
  }

  // -- Helper methods --

  /** Aligns the stream so that the next structure begins on a TIFF word. */
  private void align() throws IOException {
    if ((out.getFilePointer() & 1) != 0) out.write(0);
  }

  private static boolean extendedScalarTag(int tag) {
    return tag == IFD.STRIP_OFFSETS || tag == IFD.STRIP_BYTE_COUNTS;
  }

  private void writePadded(byte[] data) throws IOException {
    out.write(data);
    for (int i = data.length; i < 4; i++) out.write(0);
  }

  private static boolean shortTag(int tag) {
    return tag == IFD.COMPRESSION ||
      tag == IFD.PHOTOMETRIC_INTERPRETATION ||
      tag == IFD.SAMPLES_PER_PIXEL ||
      tag == IFD.PLANAR_CONFIGURATION ||
      tag == IFD.RESOLUTION_UNIT ||
      tag == NDPITags.VERSION;
  }

  private static boolean signedLongTag(int tag) {
    return tag == NDPITags.X_POSITION || tag == NDPITags.Y_POSITION ||
      tag == NDPITags.Z_POSITION || tag == NDPITags.REFOCUS_INTERVAL ||
      tag == NDPITags.FOCUS_OFFSET;
  }

  private static boolean signedLongArrayTag(int tag) {
    return tag == NDPITags.FOCUS_POINTS ||
      tag == NDPITags.FOCUS_POINT_REGIONS;
  }

  private static byte[] signedBytes(int... values) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    for (int value : values) {
      bytes.write(value);
      bytes.write(value >>> 8);
      bytes.write(value >>> 16);
      bytes.write(value >>> 24);
    }
    return bytes.toByteArray();
  }

  private static byte[] shorts(int... values) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    for (int value : values) {
      bytes.write(value);
      bytes.write(value >>> 8);
    }
    return bytes.toByteArray();
  }

  private static byte[] bytes(long... values) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    for (long value : values) {
      bytes.write((int) value);
      bytes.write((int) (value >>> 8));
      bytes.write((int) (value >>> 16));
      bytes.write((int) (value >>> 24));
    }
    return bytes.toByteArray();
  }

  // -- Helper classes --

  /** One encoded TIFF value that has not yet been placed in the file. */
  static final class Value {

    private final int tag;
    private final int type;
    private final int count;
    private final byte[] data;
    private final long scalar;

    private Value(int tag, int type, int count, byte[] data, long scalar) {
      this.tag = tag;
      this.type = type;
      this.count = count;
      this.data = data;
      this.scalar = scalar;
    }

    int tag() {
      return tag;
    }

    /**
     * @return whether this value must be stored outside its directory entry.
     *   Every ASCII value is stored externally, as genuine NDPI files do,
     *   even when it would fit inline.
     */
    boolean external() {
      return type == ASCII || data.length > 4;
    }

    /** @return a key identifying values whose emitted bytes are identical */
    String key() {
      StringBuilder key = new StringBuilder().append(type).append(':');
      for (byte b : data) key.append(Integer.toHexString(b & 0xff)).append('.');
      return key.toString();
    }
  }

  /** One prepared directory entry, and where its value was written. */
  static final class Entry {

    private final Value value;
    private final long offset;

    private Entry(Value value, long offset) {
      this.value = value;
      this.offset = offset;
    }

    /** @return this entry with the same value written under another tag */
    Entry retag(int tag) {
      return new Entry(new Value(tag, value.type, value.count, value.data,
        value.scalar), offset);
    }

    int tag() {
      return value.tag;
    }

    /**
     * @return the entry's NDPI 64-bit extension word, which carries the high
     *   word of an external value's offset, or of the few scalars NDPI
     *   deliberately extends past 32 bits. Every other entry, including a
     *   negative SLONG, extends to zero.
     */
    private long highWord() {
      if (value.external()) return offset >>> 32;
      if (extendedScalarTag(value.tag)) return value.scalar >>> 32;
      return 0;
    }
  }

  /** Where a completed directory and its next-IFD pointer were written. */
  static final class Directory {

    private final long offset;
    private final long nextPointer;

    private Directory(long offset, long nextPointer) {
      this.offset = offset;
      this.nextPointer = nextPointer;
    }

    long offset() {
      return offset;
    }

    /** @return where this directory's 64-bit next-IFD pointer begins */
    long nextPointer() {
      return nextPointer;
    }
  }
}
