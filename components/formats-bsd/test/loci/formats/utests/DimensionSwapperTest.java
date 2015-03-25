/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.utests;

import java.io.IOException;

import loci.common.Location;
import loci.formats.DimensionSwapper;
import loci.formats.FormatException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Verifies the input and output manipulations of DimensionSwapper are
 * functioning as intended, specifically with respect to updating 
 * dimension sizes and orders.
 * 
 * @author Mark Hiner
 */
public class DimensionSwapperTest {
  
  private static final int SIZE_C = 2;
  private static final int SIZE_T = 5;
  private static final int SIZE_Z = 4;
  private static final String NEW_ORDER = "XYCTZ";
  private static final String OUTPUT_ORDER = "XYZCT";
  private static final String TEST_FILE =
      "test&pixelType=uint8&sizeX=128&sizeY=64&sizeC="+SIZE_C+"&sizeZ="+SIZE_Z+"&sizeT="+SIZE_T+"&series=3.fake";
  
  @DataProvider(name = "swapper")
  public Object[][] createDimSwapper() {
    Location.mapId(TEST_FILE, TEST_FILE);
    
    DimensionSwapper swapper = new DimensionSwapper();
    
    try {
      swapper.setId(TEST_FILE);
    } catch (FormatException e) {  e.printStackTrace(); }
    catch (IOException e) { e.printStackTrace(); }
    
    swapper.setOutputOrder(OUTPUT_ORDER);
    
    return new Object[][]{{swapper}};
  }
  
  /**
   * Tests the results of setting the output order.
   */
  @Test(dataProvider="swapper")
  public void testOutputOrdering(DimensionSwapper swapper) {
    // set output order
    swapper.setOutputOrder(NEW_ORDER);
    
    // output order should be updated
    assertEquals(swapper.getDimensionOrder().equals(NEW_ORDER), true);
    
    // dimension sizes should be unchanged
    assertEquals(swapper.getSizeZ(), SIZE_Z);
    assertEquals(swapper.getSizeC(), SIZE_C);
    assertEquals(swapper.getSizeT(), SIZE_T);
  }
  
  /**
   * Tests the results of setting the input order.
   */
  @Test(dataProvider="swapper")
  public void testInputOrdering(DimensionSwapper swapper) {
    // set input (storage) order
    swapper.swapDimensions(NEW_ORDER);
    
    // output order should be unchanged
    assertEquals(swapper.getDimensionOrder().equals(OUTPUT_ORDER), true);
    
    // dimension sizes should be updated
    assertEquals(swapper.getSizeZ(), SIZE_T);
    assertEquals(swapper.getSizeC(), SIZE_Z);
    assertEquals(swapper.getSizeT(), SIZE_C);
    
  }
  
  /**
   * Tests the interactions of setting output and input orders.
   */
  @Test(dataProvider="swapper")
  public void testInputOutputOrdering(DimensionSwapper swapper) {
    
    swapper.setOutputOrder(NEW_ORDER);
    swapper.swapDimensions(NEW_ORDER);
    
    // output order should be updated
    assertEquals(swapper.getDimensionOrder().equals(NEW_ORDER), true);

    // dimension sizes should be updated
    assertEquals(swapper.getSizeZ(), SIZE_T);
    assertEquals(swapper.getSizeC(), SIZE_Z);
    assertEquals(swapper.getSizeT(), SIZE_C);
  }

}
