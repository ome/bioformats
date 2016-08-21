/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
