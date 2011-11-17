//
// UnsignedIntBuffer.java
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
 * DataBuffer that stores unsigned ints.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/UnsignedIntBuffer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/UnsignedIntBuffer.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class UnsignedIntBuffer extends DataBuffer {

  private int[][] bankData;

  /** Construct a new buffer of unsigned ints using the given int array.  */
  public UnsignedIntBuffer(int[] dataArray, int size) {
    super(DataBuffer.TYPE_INT, size);
    bankData = new int[1][];
    bankData[0] = dataArray;
  }

  /** Construct a new buffer of unsigned ints using the given 2D int array. */
  public UnsignedIntBuffer(int[][] dataArray, int size) {
    super(DataBuffer.TYPE_INT, size);
    bankData = dataArray;
  }

  /* @see java.awt.image.DataBuffer.getData() */
  public int[] getData() {
    return bankData[0];
  }

  /* @see java.awt.image.DataBuffer#getData(int) */
  public int[] getData(int bank) {
    return bankData[bank];
  }

  /* @see java.awt.image.DataBuffer#getElem(int) */
  public int getElem(int i) {
    return getElem(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElem(int, int) */
  public int getElem(int bank, int i) {
    int value = bankData[bank][i + getOffsets()[bank]];
    return (int) (value & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#getElemFloat(int) */
  public float getElemFloat(int i) {
    return getElemFloat(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElemFloat(int, int) */
  public float getElemFloat(int bank, int i) {
    return (float) (getElem(bank, i) & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#getElemDouble(int) */
  public double getElemDouble(int i) {
    return getElemDouble(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElemDouble(int, int) */
  public double getElemDouble(int bank, int i) {
    return (double) (getElem(bank, i) & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int) */
  public void setElem(int i, int val) {
    setElem(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int, int) */
  public void setElem(int bank, int i, int val) {
    bankData[bank][i + getOffsets()[bank]] = val;
  }

  /* @see java.awt.image.DataBuffer#setElemFloat(int, float) */
  public void setElemFloat(int i, float val) {
    setElemFloat(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElemFloat(int, int, float) */
  public void setElemFloat(int bank, int i, float val) {
    bankData[bank][i + getOffsets()[bank]] = (int) val;
  }

  /* @see java.awt.image.DataBuffer#setElemDouble(int, double) */
  public void setElemDouble(int i, double val) {
    setElemDouble(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElemDouble(int, int, double) */
  public void setElemDouble(int bank, int i, double val) {
    bankData[bank][i + getOffsets()[bank]] = (int) val;
  }

}
