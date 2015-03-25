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
import ome.jxr.image.FrequencyBand;

import org.testng.annotations.Test;


public class FrequencyBandTest {

  @Test
  public void testFindByIdForReservedIds() {
    int[] reservedIds = {4,5,6,7,8,9,10,11,12,13,14,15};
    for (Integer id : reservedIds) {
      assertTrue(FrequencyBand.RESERVED.equals(FrequencyBand.findById(id)));
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithNegativeIdShouldThrowIAE() {
    int invalidId = -1;
    FrequencyBand.findById(invalidId);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFindByIdWithOutOfRangeIdShouldThrowIAE() {
    int invalidId = 16;
    FrequencyBand.findById(invalidId);
  }

}
