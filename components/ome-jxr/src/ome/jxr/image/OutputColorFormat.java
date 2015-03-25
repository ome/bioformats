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

package ome.jxr.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of available color format entries. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table 22.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum OutputColorFormat {

  YONLY(0),
  YUV420(1),
  YUV422(2),
  YUV444(3),
  CMYK(4),
  CMYKDIRECT(5),
  NCOMPONENT(6),
  RGB(7),
  RGBE(8),
  RESERVED(9,10,11,12,13,14,15);

  private List<Integer> ids = new ArrayList<Integer>();

  private OutputColorFormat(Integer... ids) {
    this.ids.addAll(Arrays.asList(ids));
  }

  public List<Integer> getId() {
    return ids;
  }

  public static OutputColorFormat findById(int id) {
    for (OutputColorFormat format : OutputColorFormat.values()) {
      if (format.getId().contains(id)) {
        return format;
      }
    }
    throw new IllegalArgumentException("Unspecified color format id: " + id);
  }
}
