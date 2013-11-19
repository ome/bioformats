/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
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

package ome.jxr.metadata;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import ome.jxr.JXRException;
import ome.jxr.ifd.IFDEntry;
import ome.jxr.ifd.PixelFormat;

public class JXRMetadata {

  private Map<IFDEntry, Object> values =
      new EnumMap<IFDEntry, Object>(IFDEntry.class);

  public void put(IFDEntry element, Object value) {
    values.put(element, value);
  }

  public void verifyRequiredElements() throws JXRException {
    if (!values.isEmpty()) {
      if (!values.keySet().containsAll(IFDEntry.getRequiredElements())) {
        throw new JXRException("Metadata object is missing entries for required"
            + " IFD elements.");
      }
    }
  }

  public int getBitsPerPixel() {
    return 0;
  }

  public int getNumberOfChannels() {
    //if (!values.containsKey(IFDEntry.PIXEL_FORMAT)) {
    return 0;
    //}
    //String id = new String((byte[]) values.get(IFDEntry.PIXEL_FORMAT));
    //return PixelFormat.findById(id).getNumberOfChannels();
  }

}
