/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.formats;

import java.io.IOException;

import loci.common.lut.LutSource;

/**
 * Logic for colorizing images. The logic for this class was largely converted from
 * loci.plugins.in.Colorizer.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Josh Moore josh at glencoesoftware.com
 */
public class Colorizer extends BytesWrapper {

  // -- Constants --

  // -- Fields --

  protected LutSource lutSource = null;

  // -- Constructor --

  public Colorizer(IFormatReader reader) {
      super(reader);
  }

  public Colorizer(IFormatReader reader, LutSource source) {
      super(reader);
      setLutSource(source);
  }

  // -- Colorizer methods --

  public void setLutSource(LutSource source) {
      this.lutSource = source;
  }

  /**
   * Passes through the {@link byte[]} returned from the delegate's openBytes
   * method to the configured {@link LutSource}.
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    buf = reader.openBytes(no, buf, x, y, w, h);
    if (lutSource != null) {
      return lutSource.applyLut(no, buf, x, y, w, h);
    }
    return buf;
  }

  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    MetadataTools.populatePixels(this.getMetadataStore(), this);
  }

  @Override
  public boolean isIndexed() {
    return lutSource == null ? super.isIndexed() : true;
  }

  @Override
  public boolean isFalseColor() {
    return lutSource == null ? super.isFalseColor() : true;
  }

  @Override
  public boolean isRGB() {
    return lutSource == null ? super.isRGB() : true;
  }

  @Override
  public boolean isInterleaved() {
    return lutSource == null ? super.isInterleaved() : true;
  }

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return super.get8BitLookupTable();
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return super.get16BitLookupTable();
  }

  @Override
  public int getBitsPerPixel() {
    return super.getBitsPerPixel();
  }


}
