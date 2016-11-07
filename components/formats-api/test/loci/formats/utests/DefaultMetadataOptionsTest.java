/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;

import loci.formats.in.MetadataOptions;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;


/**
 * Unit tests for {@link loci.formats.in.DefaultMetadataOptions}.
 */
public class DefaultMetadataOptionsTest {

  private static final String KEY = "test.key";

  private MetadataOptions opt;
  private enum One { FOO, BAR };
  private enum Two { TAR, FOO };

  @DataProvider(name = "booleanStrings")
  public Object[][] mkBools() {
    return new Object[][] {{"FALSE", "TRUE"},
                           {"False", "True"},
                           {"false", "true"}};
  }

  @BeforeMethod
  public void setUp() {
    opt = new DefaultMetadataOptions();
  }

  @Test
  public void testString() {
    assertEquals(opt.get(KEY, "default"), "default");
    opt.set(KEY, "v");
    assertEquals(opt.get(KEY, "default"), "v");
  }

  @Test
  public void testEnum() {
    assertEquals(opt.getEnum(KEY, One.BAR), One.BAR);
    opt.setEnum(KEY, One.FOO);
    assertEquals(opt.getEnum(KEY, One.BAR), One.FOO);
    assertEquals(opt.getEnum(KEY, Two.TAR), Two.FOO);
    opt.set(KEY, "TAR");
    assertEquals(opt.getEnum(KEY, Two.FOO), Two.TAR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBadEnum() {
    opt.setEnum(KEY, One.BAR);
    Two t = opt.getEnum(KEY, Two.FOO);
  }

  @Test
  public void testBoolean() {
    assertFalse(opt.getBoolean(KEY, false));
    assertTrue(opt.getBoolean(KEY, true));
    opt.setBoolean(KEY, false);
    assertFalse(opt.getBoolean(KEY, true));
    opt.setBoolean(KEY, true);
    assertTrue(opt.getBoolean(KEY, false));
  }

  @Test(dataProvider = "booleanStrings")
  public void testBooleanFromString(String falseString, String trueString) {
    opt.set(KEY, falseString);
    assertFalse(opt.getBoolean(KEY, true));
    opt.set(KEY, trueString);
    assertTrue(opt.getBoolean(KEY, false));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBadBoolean() {
    opt.set(KEY, "foo");
    boolean b = opt.getBoolean(KEY, true);
  }

  @Test
  public void testMetadataLevel() {
    assertEquals(opt.getMetadataLevel(), MetadataLevel.ALL);
    for (MetadataLevel level: MetadataLevel.values()) {
      opt.setMetadataLevel(level);
      assertEquals(opt.getMetadataLevel(), level);
      assertEquals(
          (new DefaultMetadataOptions(level)).getMetadataLevel(), level
      );
    }
  }

  @Test
  public void testIsValidate() {
    assertFalse(opt.isValidate());
    opt.setValidate(true);
    assertTrue(opt.isValidate());
    opt.setValidate(false);
    assertFalse(opt.isValidate());
  }

}
