//
// SignedShortBuffer.java
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

package loci.formats.gui;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;

/**
 * DataBuffer that stores signed shorts.
 *
 * SignedShortBuffer serves the same purpose as java.awt.image.DataBufferShort;
 * the only difference is that SignedShortBuffer's getType() method
 * returns DataBuffer.TYPE_USHORT.
 * This is a workaround for the fact that java.awt.image.BufferedImage does
 * not support DataBuffers with type DataBuffer.TYPE_SHORT.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/SignedShortBuffer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/SignedShortBuffer.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SignedShortBuffer extends DataBuffer {

  private DataBufferShort helper;

  // -- Constructors --

  public SignedShortBuffer(int size) {
    super(DataBuffer.TYPE_USHORT, size);
    helper = new DataBufferShort(size);
  }

  public SignedShortBuffer(int size, int numbanks) {
    super(DataBuffer.TYPE_USHORT, size, numbanks);
    helper = new DataBufferShort(size, numbanks);
  }

  public SignedShortBuffer(short[] data, int size) {
    super(DataBuffer.TYPE_USHORT, size);
    helper = new DataBufferShort(data, size);
  }

  public SignedShortBuffer(short[] data, int size, int offset) {
    super(DataBuffer.TYPE_USHORT, size, 1, offset);
    helper = new DataBufferShort(data, size, offset);
  }

  public SignedShortBuffer(short[][] data, int size) {
    super(DataBuffer.TYPE_USHORT, size, data.length);
    helper = new DataBufferShort(data, size);
  }

  public SignedShortBuffer(short[][] data, int size, int[] offsets) {
    super(DataBuffer.TYPE_USHORT, size, data.length, offsets);
    helper = new DataBufferShort(data, size, offsets);
  }

  // -- DataBuffer API methods --

  /* @see java.awt.image.DataBufferShort#getData() */
  public short[] getData() {
    return helper.getData();
  }

  /* @see java.awt.image.DataBufferShort#getData(int) */
  public short[] getData(int bank) {
    return helper.getData(bank);
  }

  /* @see java.awt.image.DataBufferShort#getElem(int, int) */
  public int getElem(int bank, int i) {
    return helper.getElem(bank, i);
  }

  /* @see java.awt.image.DataBufferShort#setElem(int, int, int) */
  public void setElem(int bank, int i, int val) {
    helper.setElem(bank, i, val);
  }

}
