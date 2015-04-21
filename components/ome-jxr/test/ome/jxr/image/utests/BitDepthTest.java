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

package ome.jxr.image.utests;

import static org.testng.AssertJUnit.assertTrue;
import ome.jxr.image.BitDepth;

import org.testng.annotations.Test;

public class BitDepthTest {

  @Test
  public void testFindByIdForReservedIds() {
    int[] reservedIds = {5,11,12,13,14};
    for (Integer id : reservedIds) {
      assertTrue(BitDepth.RESERVED.equals(BitDepth.findById(id)));
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithNegativeIdShouldThrowIAE() {
    int invalidId = -1;
    BitDepth.findById(invalidId);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithOutOfRangeIdShouldThrowIAE() {
    int invalidId = 16;
    BitDepth.findById(invalidId);
  }

}
