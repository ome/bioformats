/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2014 - 2016 Open Microscopy Environment:
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
 * @deprecated See <a href="http://blog.openmicroscopy.org/file-formats/community/2016/01/06/format-support">blog post</a>
 */
@Deprecated
public enum ComponentMode {

  UNIFORM(0),
  SEPARATE(1),
  INDEPENDENT(2),
  RESERVED(3);

  private int id;

  private ComponentMode(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public static ComponentMode findById(int id) {
    for (ComponentMode mode : ComponentMode.values()) {
      if (mode.getId() == id) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Unspecified component mode id: " + id);
  }
}
