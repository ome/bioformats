/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
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

package ome.jxr.ifd;

/**
 * Possible data types for pixels in an image.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 * @deprecated See <a href="http://blog.openmicroscopy.org/file-formats/community/2016/01/06/format-support">blog post</a>
 */
@Deprecated
public enum PixelType {

  UINT1("uint", 1),
  UINT8("uint", 8),
  UINT10("uint", 10),
  UINT16("uint", 16),
  SINT16("sint", 16),
  SINT32("sint", 32),
  FLOAT8("float", 8),
  FLOAT16("float", 16),
  FLOAT32("float", 32);

  private String datatype;
  private int bits;

  private PixelType(String datatype, int bits) {
    this.datatype = datatype;
    this.bits = bits;
  }

  public String getDatatype() {
    return datatype;
  }

  public int getBits() {
    return bits;
  }

}
