/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;
import loci.common.DataTools;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link loci.common.DataTools}.
 */
public class DataToolsTest {

  // -- Tests --

  @Test
  public void testSafeMultiply32() {
    // test vacuous edge cases
    boolean ok = false;
    try {
      DataTools.safeMultiply32(null);
    }
    catch (NullPointerException e) {
      ok = true;
    }
    if (!ok) fail("Expected NullPointerException");
    assertSafeMultiply32Pass(0);

    // test simple cases
    assertSafeMultiply32Pass(1, 1);
    assertSafeMultiply32Pass(54, 9, 6);
    assertSafeMultiply32Pass(1037836800, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

    // test invalid arguments
    assertSafeMultiply32Fail("Invalid array size: -1", -1);
    assertSafeMultiply32Fail("Invalid array size: 0", 0);

    // test edge cases near Integer.MAX_VALUE
    assertSafeMultiply32Pass(2147483646, Integer.MAX_VALUE / 2, 2);
    assertSafeMultiply32Fail("Array size too large: 1073741824 x 2",
      Integer.MAX_VALUE / 2 + 1, 2);
    assertSafeMultiply32Pass(2147441940, 46340, 46341);
    assertSafeMultiply32Fail("Array size too large: 46341 x 46341", 46341,
      46341);
    assertSafeMultiply32Fail("Array size too large: 46340 x 46342", 46340,
      46342);
    assertSafeMultiply32Pass(2147418112, 65536, 32767);
    assertSafeMultiply32Pass(2147450880, 65535, 32768);
    assertSafeMultiply32Fail("Array size too large: 65536 x 32768", 65536,
      32768);
  }
  
  @Test
  public void testSafeMultiply64() {
    // test vacuous edge cases
    boolean ok = false;
    try {
      DataTools.safeMultiply64(null);
    }
    catch (NullPointerException e) {
      ok = true;
    }
    if (!ok) fail("Expected NullPointerException");
    assertSafeMultiply64Pass(0);

    // test simple cases
    assertSafeMultiply64Pass(1, 1);
    assertSafeMultiply64Pass(54, 9, 6);
    assertSafeMultiply64Pass(3632428800L, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

    // test invalid arguments
    assertSafeMultiply64Fail("Invalid array size: -1", -1);
    assertSafeMultiply64Fail("Invalid array size: 0", 0);

    // test edge cases near Long.MAX_VALUE
    assertSafeMultiply64Pass(9223372036854775806L, Long.MAX_VALUE / 6, 2, 3);
    assertSafeMultiply64Fail(
      "Array size too large: 1537228672809129302 x 2 x 3",
      Long.MAX_VALUE / 6 + 1, 2, 3);
    assertSafeMultiply64Pass(9223372033963249500L, 3037000499L, 3037000500L);
    assertSafeMultiply64Fail("Array size too large: 3037000500 x 3037000500",
      3037000500L, 3037000500L);
    assertSafeMultiply64Fail("Array size too large: 3037000499 x 3037000501",
      3037000499L, 3037000501L);
  }

  // -- Helper methods --

  private void assertSafeMultiply32Pass(int expected, int... sizes) {
    assertEquals(expected, DataTools.safeMultiply32(sizes));
  }

  private void assertSafeMultiply32Fail(String msg, int... sizes) {
    int actual;
    try {
      actual = DataTools.safeMultiply32(sizes);
    }
    catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), msg);
      return;
    }
    fail("Safe multiply succeeded with value: " + actual);
  }

  private void assertSafeMultiply64Pass(long expected, long... sizes) {
    assertEquals(expected, DataTools.safeMultiply64(sizes));
  }

  private void assertSafeMultiply64Fail(String msg, long... sizes) {
    long actual;
    try {
      actual = DataTools.safeMultiply64(sizes);
    }
    catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), msg);
      return;
    }
    fail("Safe multiply succeeded with value: " + actual);
  }

}
