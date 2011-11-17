//
// SignedByteBuffer.java
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

/**
 * DataBuffer that stores signed bytes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/SignedByteBuffer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/SignedByteBuffer.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class SignedByteBuffer extends DataBuffer {

  private byte[][] bankData;

  /** Construct a new buffer of signed bytes using the given byte array.  */
  public SignedByteBuffer(byte[] dataArray, int size) {
    super(DataBuffer.TYPE_BYTE, size);
    bankData = new byte[1][];
    bankData[0] = dataArray;
  }

  /** Construct a new buffer of signed bytes using the given 2D byte array. */
  public SignedByteBuffer(byte[][] dataArray, int size) {
    super(DataBuffer.TYPE_BYTE, size);
    bankData = dataArray;
  }

  /* @see java.awt.image.DataBuffer.getData() */
  public byte[] getData() {
    return bankData[0];
  }

  /* @see java.awt.image.DataBuffer#getData(int) */
  public byte[] getData(int bank) {
    return bankData[bank];
  }

  /* @see java.awt.image.DataBuffer#getElem(int) */
  public int getElem(int i) {
    return getElem(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElem(int, int) */
  public int getElem(int bank, int i) {
    return (int) bankData[bank][i + getOffsets()[bank]];
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int) */
  public void setElem(int i, int val) {
    setElem(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int, int) */
  public void setElem(int bank, int i, int val) {
    bankData[bank][i + getOffsets()[bank]] = (byte) val;
  }

}
