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

import java.io.File;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import loci.formats.FormatOptions;


/**
 * Unit tests for {@link loci.formats.in.FormatOptions}.
 */
public class FormatOptionsTest {

  private static final String KEY = "test.key";
  private static final double DDELTA = 1e-7;
  private static final float FDELTA = 1e-7f;

  private FormatOptions opt;
  private enum One { FOO, BAR };
  private enum Two { TAR, FOO };

  private static void assertAlmostEquals(Double actual, Double expected) {
    assertEquals(actual, expected, DDELTA);
  }

  private static void assertAlmostEquals(Float actual, Float expected) {
    assertEquals(actual, expected, FDELTA);
  }

  @DataProvider(name = "booleanStrings")
  public Object[][] mkBools() {
    return new Object[][] {{"FALSE", "TRUE"},
                           {"False", "True"},
                           {"false", "true"}};
  }

  @DataProvider(name = "intCases")
  public Object[][] mkInts() {
    return new Object[][] {{"30", 30},
                           {"-30", -30}};
  }

  @DataProvider(name = "longCases")
  public Object[][] mkLongs() {
    return new Object[][] {{"30", 30L},
                           {"-30", -30L}};
  }

  @DataProvider(name = "floatCases")
  public Object[][] mkFloats() {
    return new Object[][] {{"3.14", 3.14f},
                           {"03.14", 3.14f},
                           {"-3.14", -3.14f}};
  }

  @DataProvider(name = "doubleCases")
  public Object[][] mkDoubles() {
    return new Object[][] {{"3.14", 3.14},
                           {"03.14", 3.14},
                           {"-3.14", -3.14}};
  }

  @BeforeMethod
  public void setUp() {
    opt = new FormatOptions();
  }

  @Test
  public void testString() {
    assertNull(opt.get(KEY));
    assertEquals(opt.get(KEY, "default"), "default");
    opt.set(KEY, "v");
    assertEquals(opt.get(KEY, "default"), "v");
    opt.set(KEY, null);
    assertNull(opt.get(KEY));
    assertEquals(opt.get(KEY, "default"), "default");
  }

  @Test
  public void testEnum() {
    assertEquals(opt.getEnum(KEY, One.BAR), One.BAR);
    opt.setEnum(KEY, One.FOO);
    assertEquals(opt.getEnum(KEY, One.BAR), One.FOO);
    assertEquals(opt.getEnum(KEY, Two.TAR), Two.FOO);
    opt.set(KEY, "TAR");
    assertEquals(opt.getEnum(KEY, Two.FOO), Two.TAR);
    opt.setEnum(KEY, null);
    assertEquals(opt.getEnum(KEY, Two.FOO), Two.FOO);
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
  public void testInt() {
    assertEquals(opt.getInt(KEY, 1), 1);
    opt.setInt(KEY, 2);
    assertEquals(opt.getInt(KEY, 1), 2);
    opt.set(KEY, "1");
    assertEquals(opt.getInt(KEY, 2), 1);
  }

  @Test(dataProvider = "intCases")
  public void testIntFromString(String intString, int expected) {
    opt.set(KEY, intString);
    assertEquals(opt.getInt(KEY, 0), expected);
  }

  @Test(expectedExceptions = NumberFormatException.class)
  public void testBadInt() {
    opt.set(KEY, "2147483648");
    int f = opt.getInt(KEY, 0);
  }

  @Test
  public void testLong() {
    assertEquals(opt.getLong(KEY, 1L), 1L);
    opt.setLong(KEY, 2L);
    assertEquals(opt.getLong(KEY, 1L), 2L);
    opt.set(KEY, "1");
    assertEquals(opt.getLong(KEY, 2L), 1L);
  }

  @Test(dataProvider = "longCases")
  public void testLongFromString(String longString, long expected) {
    opt.set(KEY, longString);
    assertEquals(opt.getLong(KEY, 0L), expected);
  }

  @Test(expectedExceptions = NumberFormatException.class)
  public void testBadLong() {
    opt.set(KEY, "9223372036854775808");
    long f = opt.getLong(KEY, 0L);
  }

  @Test
  public void testFloat() {
    assertAlmostEquals(opt.getFloat(KEY, 2.72f), 2.72f);
    opt.setFloat(KEY, 3.14f);
    assertAlmostEquals(opt.getFloat(KEY, 2.72f), 3.14f);
    opt.set(KEY, "2.72");
    assertAlmostEquals(opt.getFloat(KEY, 3.14f), 2.72f);
  }

  @Test(dataProvider = "floatCases")
  public void testFloatFromString(String floatString, float expected) {
    opt.set(KEY, floatString);
    assertAlmostEquals(opt.getFloat(KEY, 0f), expected);
  }

  @Test(expectedExceptions = NumberFormatException.class)
  public void testBadFloat() {
    opt.set(KEY, "foo");
    float f = opt.getFloat(KEY, 0f);
  }

  @Test
  public void testDouble() {
    assertAlmostEquals(opt.getDouble(KEY, 2.72), 2.72);
    opt.setDouble(KEY, 3.14);
    assertAlmostEquals(opt.getDouble(KEY, 2.72), 3.14);
    opt.set(KEY, "2.72");
    assertAlmostEquals(opt.getDouble(KEY, 3.14), 2.72);
  }

  @Test(dataProvider = "doubleCases")
  public void testDoubleFromString(String doubleString, double expected) {
    opt.set(KEY, doubleString);
    assertAlmostEquals(opt.getDouble(KEY, 0), expected);
  }

  @Test(expectedExceptions = NumberFormatException.class)
  public void testBadDouble() {
    opt.set(KEY, "foo");
    double f = opt.getDouble(KEY, 0);
  }

  @Test
  public void testClass() throws ClassNotFoundException {
    final Class<?> c1 = Thread.class;
    final Class<?> c2 = Number.class;
    assertEquals(opt.getClass(KEY, c1), c1);
    opt.setClass(KEY, c2);
    assertEquals(opt.getClass(KEY, c1), c2);
    opt.set(KEY, "java.lang.Thread");
    assertEquals(opt.getClass(KEY, c2), c1);
    opt.setClass(KEY, null);
    assertEquals(opt.getClass(KEY, c2), c2);
  }

  @Test(expectedExceptions = ClassNotFoundException.class)
  public void testBadClass() throws ClassNotFoundException {
    opt.set(KEY, "org.foo.Bar");
    opt.getClass(KEY, Thread.class);
  }

  @Test
  public void testFile() {
    final File f1 = new File("/foo/f1");
    final File f2 = new File("/foo/f2");
    assertEquals(opt.getFile(KEY, f1), f1);
    opt.setFile(KEY, f2);
    assertEquals(opt.getFile(KEY, f1), f2);
    opt.set(KEY, "/foo/f1");
    assertEquals(opt.getFile(KEY, f2), f1);
    opt.setFile(KEY, null);
    assertEquals(opt.getFile(KEY, f2), f2);
  }

}
