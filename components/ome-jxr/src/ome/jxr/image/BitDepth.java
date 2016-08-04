/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2014 - 2015 Open Microscopy Environment:
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
 * Enumeration of available image bit depth types. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table 23.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum BitDepth {

  BD1WHITE1(0),
  BD8(1),
  BD16(2),
  BD16S(3),
  BD16F(4),
  BD32S(6),
  BD32F(7),
  BD5(8),
  BD10(9),
  BD565(10),
  RESERVED(5, 11, 12, 13, 14),
  BD1BLACK1(15);

  private List<Integer> ids = new ArrayList<Integer>();

  private BitDepth(Integer... ids) {
    this.ids.addAll(Arrays.asList(ids));
  }

  public List<Integer> getId() {
    return ids;
  }

  public static BitDepth findById(int id) {
    for (BitDepth bitdepth : BitDepth.values()) {
      if (bitdepth.getId().contains(id)) {
        return bitdepth;
      }
    }
    throw new IllegalArgumentException("Unspecified bitdepth format id: " + id);
  }
}
