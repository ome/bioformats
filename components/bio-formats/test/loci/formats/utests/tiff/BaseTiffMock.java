//
// BaseTiffMock.java
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
package loci.formats.utests.tiff;

import java.io.IOException;
import java.nio.ByteOrder;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDType;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/tiff/BaseTiffMock.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/utests/tiff/BaseTiffMock.java">SVN</a></dd></dl>
 */
public class BaseTiffMock {

  private ByteArrayHandle handle;
  
  private RandomAccessInputStream in;

  private TiffParser tiffParser;

  private static byte[] TIFF_HEADER = new byte[] {
    0x49, 0x49, 0x2A, 0x00, 0x08, 0x00, 0x00, 0x00,
  };

  private static final int ENTRY_VALUE_BEGIN_OFFSET = 65535;

  public BaseTiffMock() throws IOException {
    handle = new ByteArrayHandle(TIFF_HEADER);
    handle.seek(TIFF_HEADER.length);
    handle.setOrder(ByteOrder.LITTLE_ENDIAN);
    long next = ENTRY_VALUE_BEGIN_OFFSET;
    // IFD entry count
    handle.writeShort(getEntryCount());
    // IMAGE_WIDTH
    writeIFDEntry((short) IFD.IMAGE_WIDTH, IFDType.SHORT, getImageWidth());
    // IMAGE_LENGTH
    writeIFDEntry((short) IFD.IMAGE_LENGTH, IFDType.SHORT, getImageLength());
    // BITS_PER_SAMPLE
    next = writeBitsPerSample(next);
    // COMPRESSION
    writeIFDEntry((short) IFD.COMPRESSION, IFDType.SHORT,
                  getCompression());
    // PHOTOMETRIC_INTERPRETATION
    writeIFDEntry((short) IFD.PHOTOMETRIC_INTERPRETATION, IFDType.SHORT,
                  PhotoInterp.RGB.getCode());
    // STRIP_OFFSETS
    next = writeIFDEntry((short) IFD.STRIP_OFFSETS, IFDType.LONG,
                         getStripOffsets(), next);
    // SAMPLES_PER_PIXEL
    writeIFDEntry((short) IFD.SAMPLES_PER_PIXEL, IFDType.SHORT,
                   getSamplesPerPixel());
    // ROWS_PER_STRIP
    next = writeIFDEntry((short) IFD.ROWS_PER_STRIP, IFDType.LONG,
                         getRowsPerStrip(), next);
    // X_RESOLUTION
    next = writeIFDEntry((short) IFD.X_RESOLUTION, IFDType.RATIONAL,
                         getXResolution(), next);
    // Y_RESOLUTION
    next = writeIFDEntry((short) IFD.Y_RESOLUTION, IFDType.RATIONAL,
                         getYResolution(), next);
    // RESOLUTION_UNIT
    writeIFDEntry((short) IFD.RESOLUTION_UNIT, IFDType.SHORT,
                  getResolutionUnit());
    // Terminating IFD offset
    handle.writeInt(0);
    in = new RandomAccessInputStream(handle);
    tiffParser = new TiffParser(in);
  }

  protected void writeIFDEntry(short tag, IFDType type, int value)
                               throws IOException {
    handle.writeShort(tag);  // IFD entry tag
    handle.writeShort(type.getCode());  // IFD entry type
    handle.writeInt(1);  // IFD entry value count
    handle.writeInt(value);  // The actual value
  }

  protected long writeIFDEntry(short tag, IFDType type, int[] values,
                               long offset) throws IOException {
    handle.writeShort(tag);  // IFD entry tag
    handle.writeShort(type.getCode());  // IFD entry type
    handle.writeInt(values.length);  // IFD entry value count
    handle.writeInt((int) offset); // The offset to the value
    long before = handle.getFilePointer();
    handle.seek(offset);
    for (int value : values) {
      handle.writeInt(value);
    }
    long after = handle.getFilePointer();
    handle.seek(before);
    return after;
  }

  protected long writeIFDEntry(short tag, IFDType type, short[] values,
                               long offset) throws IOException {
    handle.writeShort(tag);  // IFD entry tag
    handle.writeShort(type.getCode());  // IFD entry type
    handle.writeInt(values.length);  // IFD entry value count
    handle.writeInt((int) offset); // The offset to the value
    long before = handle.getFilePointer();
    handle.seek(offset);
    for (int value : values) {
      handle.writeShort(value);
    }
    long after = handle.getFilePointer();
    handle.seek(before);
    return after;
  }

  protected long writeIFDEntry(short tag, IFDType type, TiffRational values,
                               long offset) throws IOException {
    handle.writeShort(tag);  // IFD entry tag
    handle.writeShort(type.getCode());  // IFD entry type
    handle.writeInt(1);  // IFD entry value count
    handle.writeInt((int) offset); // The offset to the value
    long before = handle.getFilePointer();
    handle.seek(offset);
    handle.writeInt((int) values.getNumerator());
    handle.writeInt((int) values.getDenominator());
    long after = handle.getFilePointer();
    handle.seek(before);
    return after;
  }

  protected int getEntryCount() {
    return 11;
  }

  protected long writeBitsPerSample(long offset) throws IOException {
    writeIFDEntry((short) IFD.BITS_PER_SAMPLE, IFDType.SHORT,
                  getBitsPerSample()[0]);
    return offset;
  }

  public TiffParser getTiffParser() {
    return tiffParser;
  }

  public int getImageWidth() {
    return 6;
  }

  public int getImageLength() {
    return 4;
  }

  public short[] getBitsPerSample() {
    return new short[] { 8 };
  }

  public int getCompression() {
    return 1;
  }

  public int[] getStripOffsets() {
    return new int[] { 0, 1, 2 };
  }

  public int[] getRowsPerStrip() {
    return new int[] { 2, 2, 2 };
  }

  public TiffRational getXResolution() {
    return new TiffRational(1, 4);
  }

  public TiffRational getYResolution() {
    return new TiffRational(1, 2);
  }

  public short getResolutionUnit() {
    return 1;
  }

  public int getSamplesPerPixel() {
    return 1;
  }

}
