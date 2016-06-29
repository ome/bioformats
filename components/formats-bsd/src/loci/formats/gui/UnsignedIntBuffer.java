/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.gui;

import java.awt.image.DataBuffer;

/**
 * DataBuffer that stores unsigned ints.
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
  @Override
  public int getElem(int i) {
    return getElem(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElem(int, int) */
  @Override
  public int getElem(int bank, int i) {
    int value = bankData[bank][i + getOffsets()[bank]];
    return (int) (value & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#getElemFloat(int) */
  @Override
  public float getElemFloat(int i) {
    return getElemFloat(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElemFloat(int, int) */
  @Override
  public float getElemFloat(int bank, int i) {
    return (float) (getElem(bank, i) & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#getElemDouble(int) */
  @Override
  public double getElemDouble(int i) {
    return getElemDouble(0, i);
  }

  /* @see java.awt.image.DataBuffer#getElemDouble(int, int) */
  @Override
  public double getElemDouble(int bank, int i) {
    return (double) (getElem(bank, i) & 0xffffffffL);
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int) */
  @Override
  public void setElem(int i, int val) {
    setElem(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElem(int, int, int) */
  @Override
  public void setElem(int bank, int i, int val) {
    bankData[bank][i + getOffsets()[bank]] = val;
  }

  /* @see java.awt.image.DataBuffer#setElemFloat(int, float) */
  @Override
  public void setElemFloat(int i, float val) {
    setElemFloat(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElemFloat(int, int, float) */
  @Override
  public void setElemFloat(int bank, int i, float val) {
    bankData[bank][i + getOffsets()[bank]] = (int) val;
  }

  /* @see java.awt.image.DataBuffer#setElemDouble(int, double) */
  @Override
  public void setElemDouble(int i, double val) {
    setElemDouble(0, i, val);
  }

  /* @see java.awt.image.DataBuffer#setElemDouble(int, int, double) */
  @Override
  public void setElemDouble(int bank, int i, double val) {
    bankData[bank][i + getOffsets()[bank]] = (int) val;
  }

}
