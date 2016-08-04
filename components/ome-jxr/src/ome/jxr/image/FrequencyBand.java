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
 * Enumeration of available frequency band counts. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table 29.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum FrequencyBand {
  ALL(4, 0),
  NOFLEXBITS(3, 1),
  NOHIGHPASS(2, 2),
  DCONLY(1, 3),
  RESERVED(0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

  private List<Integer> ids = new ArrayList<Integer>();

  private int numberOfBands;

  private FrequencyBand(int numberOfBands, Integer... ids) {
    this.numberOfBands = numberOfBands;
    this.ids.addAll(Arrays.asList(ids));
  }

  public List<Integer> getId() {
    return ids;
  }

  public int getNumberOfBands() {
    return numberOfBands;
  }

  public static FrequencyBand findById(int id) {
    for (FrequencyBand band : FrequencyBand.values()) {
      if (band.getId().contains(id)) {
        return band;
      }
    }
    throw new IllegalArgumentException("Unspecified frequency band id: " + id);
  }
}
