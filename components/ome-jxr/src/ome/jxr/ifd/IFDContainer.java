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

package ome.jxr.ifd;

import java.util.ArrayList;
import java.util.List;

import ome.jxr.constants.IFD;

/**
 * Container holding information about IFD entries. Each individual IFD
 * container is a unique group of IFD entries and the object of this class is
 * aware of the count of entries in itself.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class IFDContainer {

  private long offset;

  private short numberOfEntries;

  private List<Long> entryOffsets = new ArrayList<Long>();

  public IFDContainer(long offset, short numberOfEntries) {
    this.offset = offset;
    this.numberOfEntries = numberOfEntries;
    for (int i=0; i<numberOfEntries; i++) {
      entryOffsets.add(getOffsetOfFirstEntry() + i*IFD.ENTRY_SIZE);
    }
  }

  /**
   * Returns the offset from the start of a data stream to the address where
   * this IFD Container begins.
   *
   * @return See above.
   */
  public long getOffsetOfContainer() {
    return offset;
  }

  /**
   * Returns the offset from the start of a data stream to the address where
   * this IFD Container begins, skipping {@link IFD#ENTRIES_COUNT_SIZE} bytes
   * that hold the Entry count for this container.
   *
   * @return See above.
   */
  public long getOffsetOfFirstEntry() {
    return offset + IFD.ENTRIES_COUNT_SIZE;
  }

  /**
   * Returns the list of offsets counted from the beginning of a data stream.
   * Each offset points to an individual IFD Entry in this Container.
   *
   * @return See above.
   */
  public List<Long> getEntryOffsets() {
    return entryOffsets;
  }

  /**
   * Returns the number of entries in this IFD Container.
   *
   * @return See above.
   */
  public short getNumberOfEntries() {
    return numberOfEntries;
  }

  @Override
  public String toString() {
    return "IFDContainer [offset=" + offset + ", numberOfEntries="
        + numberOfEntries + "]";
  }

}
