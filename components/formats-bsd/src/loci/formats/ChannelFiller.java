/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats;

import java.io.IOException;

import loci.common.DataTools;
import loci.formats.meta.MetadataStore;

/**
 * For indexed color data representing true color, factors out
 * the indices, replacing them with the color table values directly.
 *
 * For all other data (either non-indexed, or indexed with
 * "false color" tables), does nothing.
 */
public class ChannelFiller extends ReaderWrapper {

  // -- Utility methods --

  /** Converts the given reader into a ChannelFiller, wrapping if needed. */
  public static ChannelFiller makeChannelFiller(IFormatReader r) {
    if (r instanceof ChannelFiller) return (ChannelFiller) r;
    return new ChannelFiller(r);
  }

  // -- Fields --

  /**
   * Whether to fill in the indices.
   * By default, indices are filled iff data not false color.
   */
  protected Boolean filled = null;

  /** Number of LUT components. */
  protected int lutLength;

  // -- Constructors --

  /** Constructs a ChannelFiller around a new image reader. */
  public ChannelFiller() { super(); }

  /** Constructs a ChannelFiller with a given reader. */
  public ChannelFiller(IFormatReader r) { super(r); }

  // -- ChannelFiller methods --

  /** Returns true if the indices are being factored out. */
  public boolean isFilled() {
    if (!reader.isIndexed()) return false; // cannot fill non-indexed color
    if (lutLength < 1) return false; // cannot fill when LUTs are missing
    return filled == null ? !reader.isFalseColor() : filled;
  }

  /** Toggles whether the indices should be factored out. */
  public void setFilled(boolean filled) {
    this.filled = filled;
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    if (!isFilled()) return reader.getSizeC();
    return reader.getSizeC() * lutLength;
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    if (!isFilled()) return reader.isRGB();
    return getRGBChannelCount() > 1;
  }

  /* @see IFormatReader#isIndexed() */
  @Override
  public boolean isIndexed() {
    if (!isFilled()) return reader.isIndexed();
    return false;
  }

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (!isFilled()) return reader.get8BitLookupTable();
    return null;
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (!isFilled()) return reader.get16BitLookupTable();
    return null;
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    byte[] buf = DataTools.allocate(w, h, getRGBChannelCount(),
      FormatTools.getBytesPerPixel(getPixelType()));
    return openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (!isFilled()) return reader.openBytes(no, buf, x, y, w, h);

    // TODO: The pixel type should change to match the available color table.
    // That is, even if the indices are uint8, if the color table is 16-bit,
    // The pixel type should change to uint16. Similarly, if the indices are
    // uint16 but we are filling with an 8-bit color table, the pixel type
    // should change to uint8.

    // TODO: This logic below is opaque and could use some comments.

    byte[] pix = reader.openBytes(no, x, y, w, h);
    if (getPixelType() == FormatTools.UINT8) {
      byte[][] b = ImageTools.indexedToRGB(reader.get8BitLookupTable(), pix);
      if (isInterleaved()) {
        int pt = 0;
        for (int i=0; i<b[0].length; i++) {
          for (int j=0; j<b.length; j++) {
            buf[pt++] = b[j][i];
          }
        }
      }
      else {
        for (int i=0; i<b.length; i++) {
          System.arraycopy(b[i], 0, buf, i*b[i].length, b[i].length);
        }
      }
      return buf;
    }
    short[][] s = ImageTools.indexedToRGB(reader.get16BitLookupTable(),
      pix, isLittleEndian());

    if (isInterleaved()) {
      int pt = 0;
      for (int i=0; i<s[0].length; i++) {
        for (int j=0; j<s.length; j++) {
          buf[pt++] = (byte) (isLittleEndian() ?
            (s[j][i] & 0xff) : (s[j][i] >> 8));
          buf[pt++] = (byte) (isLittleEndian() ?
            (s[j][i] >> 8) : (s[j][i] & 0xff));
        }
      }
    }
    else {
      int pt = 0;
      for (int i=0; i<s.length; i++) {
        for (int j=0; j<s[i].length; j++) {
          buf[pt++] = (byte) (isLittleEndian() ?
            (s[i][j] & 0xff) : (s[i][j] >> 8));
          buf[pt++] = (byte) (isLittleEndian() ?
            (s[i][j] >> 8) : (s[i][j] & 0xff));
        }
      }
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    return byte[].class;
  }

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    lutLength = getLookupTableComponentCount();

    MetadataStore store = getMetadataStore();
    MetadataTools.populatePixelsOnly(store, this);
  }

  // -- Helper methods --

  /** Gets the number of color components in the lookup table. */
  private int getLookupTableComponentCount()
    throws FormatException, IOException
  {
    byte[][] lut8 = reader.get8BitLookupTable();
    if (lut8 != null) return lut8.length;
    short[][] lut16 = reader.get16BitLookupTable();
    if (lut16 != null) return lut16.length;
    lut8 = reader.get8BitLookupTable();
    if (lut8 != null) return lut8.length;
    lut16 = reader.get16BitLookupTable();
    if (lut16 != null) return lut16.length;
    return 0; // LUTs are missing
  }

}
