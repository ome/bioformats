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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.gui.AWTImageTools;

/**
 * BIFormatReader is the superclass for file format readers
 * that use java.awt.image.BufferedImage as the native data type.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class BIFormatReader extends FormatReader {

  // -- Constructors --

  /** Constructs a new BIFormatReader. */
  public BIFormatReader(String name, String suffix) {
    super(name, suffix);
  }

  /** Constructs a new BIFormatReader. */
  public BIFormatReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    BufferedImage data = (BufferedImage) openPlane(no, x, y, w, h);
    switch (data.getColorModel().getComponentSize(0)) {
      case 8:
        byte[] t = AWTImageTools.getBytes(data, false);
        System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));
        break;
      case 16:
        short[][] ts = AWTImageTools.getShorts(data);
        for (int c=0; c<ts.length; c++) {
          int offset = c * ts[c].length * 2;
          for (int i=0; i<ts[c].length && offset < buf.length; i++) {
            DataTools.unpackBytes(ts[c][i], buf, offset, 2, isLittleEndian());
            offset += 2;
          }
        }
        break;
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    return BufferedImage.class;
  }

}
