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
 * Constants describing an IFD Container and its Entries. All sizes, if the name
 * doesn't suggest otherwise, have been provided in bytes.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class IFD {

  public static final short ENTRY_SIZE = 12;
  public static final int ENTRIES_COUNT_SIZE = 2;
  public static final short ENTRY_TAG_SIZE = 2;
  public static final short ENTRY_TYPE_SIZE = 2;
  public static final short ENTRY_TYPE_COUNT_SIZE = 4;
  public static final short ENTRY_VALUE_SIZE = 4;
  public static final short PIXEL_FORMAT_SIZE = 16;

}
