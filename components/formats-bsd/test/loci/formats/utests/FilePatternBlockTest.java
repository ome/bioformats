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

import java.math.BigInteger;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import loci.formats.FilePatternBlock;


public class FilePatternBlockTest {

  @DataProvider(name = "valid")
  public Object[][] validBlocks() {
    return new Object[][] {
      {"<9>", new String[] {"9"}, true, true},
      {"<0-2>", new String[] {"0", "1", "2"}, true, true},
      {"<9-11>", new String[] {"9", "10", "11"}, false, true},
      {"<09-11>", new String[] {"09", "10", "11"}, true, true},
      {"<1-5:2>", new String[] {"1", "3", "5"}, true, true},
      {"<Z>", new String[] {"Z"}, true, false},
      {"<A-C>", new String[] {"A", "B", "C"}, true, false},
      {"<A-E:2>", new String[] {"A", "C", "E"}, true, false},
      {"<z>", new String[] {"z"}, true, false},
      {"<a-c>", new String[] {"a", "b", "c"}, true, false},
      {"<a-e:2>", new String[] {"a", "c", "e"}, true, false}
    };
  }

  @Test(dataProvider = "valid")
  public void testValidBlocks(String pattern, String[] expElements,
      boolean fixed, boolean numeric) {
    FilePatternBlock block = new FilePatternBlock(pattern);
    String[] elements = block.getElements();
    assertEquals(elements.length, expElements.length);
    for (int i = 0; i < elements.length; i++) {
      assertEquals(elements[i], expElements[i]);
    }
    assertEquals(block.getBlock(), pattern);
    assertEquals(block.isFixed(), fixed);
    assertEquals(block.isNumeric(), numeric);
    int radix = numeric ? 10 : Character.MAX_RADIX;
    BigInteger first = new BigInteger(expElements[0], radix);
    BigInteger last = new BigInteger(expElements[expElements.length-1], radix);
    assertTrue(block.getFirst().equals(first));
    assertTrue(block.getLast().equals(last));
  }

}
