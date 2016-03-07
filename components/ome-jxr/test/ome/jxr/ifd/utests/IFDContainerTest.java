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

package ome.jxr.ifd.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import ome.jxr.constants.IFD;
import ome.jxr.ifd.IFDContainer;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class IFDContainerTest {

  private final int expectedOffset = 10;
  private final short expectedNumberOfEntries = 20;

  private IFDContainer container;

  @BeforeClass
  public void setUp() {
    container = new IFDContainer(expectedOffset, expectedNumberOfEntries);
  }

  @Test
  public void testGetOffsetOfContainer() throws Exception {
    assertEquals(container.getOffsetOfContainer(), expectedOffset);
  }

  @Test
  public void testGetOffsetOfFirstEntry() throws Exception {
    assertEquals(container.getOffsetOfFirstEntry(), expectedOffset
        + IFD.ENTRIES_COUNT_SIZE);
  }

  @Test
  public void testGetEntryOffsets() throws Exception {
    List<Long> actualOffsets = container.getEntryOffsets();
    Long lastEntryOffset = new Long(expectedNumberOfEntries*IFD.ENTRY_SIZE);
    assertEquals(expectedNumberOfEntries, actualOffsets.size());
    assertEquals(lastEntryOffset, actualOffsets.get(actualOffsets.size() - 1));
  }

  @Test
  public void testGetNumberOfEntries() throws Exception {
    assertEquals(expectedNumberOfEntries, container.getNumberOfEntries());
  }

}
