/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2015 Open Microscopy Environment:
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

package ome.jxr.constants;

/**
 * Constants found in the file header structure. All sizes, if the name doesn't
 * suggest otherwise, have been provided in bytes.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class File {

  public static final int MINIMAL_HEADER_SIZE = 4;
  public static final long MAX_SIZE = 4294967295L;
  public static final short LITTLE_ENDIAN = 0x4949;
  public static final byte MAGIC_NUMBER = (byte) 0xBC;
  public static final int CODESTREAM_VERSION = 1;

}
