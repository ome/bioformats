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

package ome.jxr.constants;

/**
 * Constants found in the codestream header structure.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 * @deprecated See <a href="http://blog.openmicroscopy.org/file-formats/community/2016/01/06/format-support">blog post</a>
 */
@Deprecated
public final class Image {

  public static final String GDI_SIGNATURE = "WMPHOTO\0";
  public static final int RESERVED_B = 1;
  public static final int SPATIAL_XFRM_SUBORDINATE_MAX = 7;
  public static final int OVERLAP_MODE_RESERVED = 3;
  public static final int OUTPUT_CLR_FMT_MAX = 15;

}
