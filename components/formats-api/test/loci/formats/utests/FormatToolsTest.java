/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2015 - 2016 Open Microscopy Environment:
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

import loci.common.Constants;
import loci.formats.FormatTools;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.units.UNITS;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Unit tests for {@link loci.formats.FormatTools}.
 */
public class FormatToolsTest {

  @Test
  public void testDefaultMinMaxInt8() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT8);
    assertEquals(lim[0], -128);
    assertEquals(lim[1], 127);
  }

  @Test
  public void testDefaultMinMaxInt16() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT16);
    assertEquals(lim[0], -32768);
    assertEquals(lim[1], 32767);
  }

  @Test
  public void testDefaultMinMaxInt32() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT32);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  @Test
  public void testDefaultMinMaxUint8() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT8);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 255);
  }

  @Test
  public void testDefaultMinMaxUint16() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT16);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 65535);
  }

  @Test
  public void testDefaultMinMaxUint32() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT32);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 4294967295L);
  }

  public void testDefaultMinMaxFloat() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(FormatTools.FLOAT);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  public void testDefaultMinMaxDouble() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(FormatTools.DOUBLE);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testDefaultMinMaxInvalid() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(9999); // Invalid pixel type number
  }

  @DataProvider(name = "physicalSizeNoUnit")
  public Object[][] createValueLengths() {
    return new Object[][] {
      {null, null},
      {Constants.EPSILON, null},
      {0.0, null},
      {Double.POSITIVE_INFINITY, null},
      {1.0, new Length(1.0, UNITS.MICROMETER)},
      {.1, new Length(.1, UNITS.MICROMETER)},
    };
  }

  @DataProvider(name = "physicalSizeStringUnit")
  public Object[][] createValueStringLengths() {
    return new Object[][] {
      {null, "mm", null},
      {Constants.EPSILON, "mm", null},
      {0.0, "mm", null},
      {Double.POSITIVE_INFINITY, "mm", null},
      {1.0, "microns", new Length(1.0, UNITS.MICROMETER)},
      {1.0, "mm", new Length(1.0, UNITS.MILLIMETER)},
      {.1, "microns", new Length(.1, UNITS.MICROMETER)},
      {.1, "mm", new Length(.1, UNITS.MILLIMETER)},
      // Invalid length units
      {1.0, null, new Length(1.0, UNITS.MICROMETER)},
      {1.0, "foo", new Length(1.0, UNITS.MICROMETER)},
      {1.0, "s", new Length(1.0, UNITS.MICROMETER)},
    };
  }

  @DataProvider(name = "physicalSizeUnit")
  public Object[][] createValueUnitLengths() {
    return new Object[][] {
      {null, UNITS.MICROMETER, null},
      {Constants.EPSILON, UNITS.MICROMETER, null},
      {0.0, UNITS.MICROMETER, null},
      {Double.POSITIVE_INFINITY, UNITS.MICROMETER, null},
      {1.0, UNITS.MICROMETER, new Length(1.0, UNITS.MICROMETER)},
      {1.0, UNITS.MILLIMETER, new Length(1.0, UNITS.MILLIMETER)},
      {.1, UNITS.MICROMETER, new Length(.1, UNITS.MICROMETER)},
      {.1, UNITS.MILLIMETER, new Length(.1, UNITS.MILLIMETER)},
    };
  }

  @Test(dataProvider = "physicalSizeNoUnit")
  public void testGetPhysicalSizeNoUnit(Double value, Length length) {
    assertEquals(length, FormatTools.getPhysicalSizeX(value));
    assertEquals(length, FormatTools.getPhysicalSizeY(value));
    assertEquals(length, FormatTools.getPhysicalSizeZ(value));
  }

  @Test(dataProvider = "physicalSizeStringUnit")
  public void testGetPhysicalSizeStringUnit(Double value, String unit, Length length) {
    assertEquals(length, FormatTools.getPhysicalSizeX(value, unit));
    assertEquals(length, FormatTools.getPhysicalSizeY(value, unit));
    assertEquals(length, FormatTools.getPhysicalSizeZ(value, unit));
  }

  @Test(dataProvider = "physicalSizeUnit")
  public void testGetPhysicalSizeUnit(Double value, Unit<Length> unit, Length length) {
    assertEquals(length, FormatTools.getPhysicalSizeX(value, unit));
    assertEquals(length, FormatTools.getPhysicalSizeY(value, unit));
    assertEquals(length, FormatTools.getPhysicalSizeZ(value, unit));
  }

  @DataProvider(name = "positionNoUnit")
  public Object[][] createPositionNoUnit() {
    return new Object[][] {
      {null, null},
      {0.0, new Length(0.0, UNITS.REFERENCEFRAME)},
      {1.0, new Length(1.0, UNITS.REFERENCEFRAME)},
      {.1, new Length(.1, UNITS.REFERENCEFRAME)},
      {Constants.EPSILON, new Length(Constants.EPSILON, UNITS.REFERENCEFRAME)},
      {-Constants.EPSILON, new Length(-Constants.EPSILON, UNITS.REFERENCEFRAME)},
      {Double.POSITIVE_INFINITY, null},
      {-Double.POSITIVE_INFINITY, null},
    };
  }

  @DataProvider(name = "positionStringUnit")
  public Object[][] createPositionStringUnit() {
    return new Object[][] {
      {null, "mm", null},
      {0.0, "mm", new Length(0.0, UNITS.MILLIMETER)},
      {1.0, "mm", new Length(1.0, UNITS.MILLIMETER)},
      {.1, "mm", new Length(.1, UNITS.MILLIMETER)},
      {Constants.EPSILON, "mm", new Length(Constants.EPSILON, UNITS.MILLIMETER)},
      {-Constants.EPSILON, "mm", new Length(-Constants.EPSILON, UNITS.MILLIMETER)},
      {Double.POSITIVE_INFINITY, "mm", null},
      // Invalid length string units
      {1.0, null, new Length(1.0, UNITS.REFERENCEFRAME)},
      {1.0, "foo", new Length(1.0, UNITS.REFERENCEFRAME)},
      {1.0, "s", new Length(1.0, UNITS.REFERENCEFRAME)},
    };
  }

  @DataProvider(name = "positionUnit")
  public Object[][] createPositionUnit() {
    return new Object[][] {
      {null, UNITS.MILLIMETER, null},
      {0.0, UNITS.MILLIMETER, new Length(0.0, UNITS.MILLIMETER)},
      {1.0, UNITS.MILLIMETER, new Length(1.0, UNITS.MILLIMETER)},
      {.1, UNITS.MILLIMETER, new Length(.1, UNITS.MILLIMETER)},
      {Constants.EPSILON, UNITS.MILLIMETER, new Length(Constants.EPSILON, UNITS.MILLIMETER)},
      {-Constants.EPSILON, UNITS.MILLIMETER, new Length(-Constants.EPSILON, UNITS.MILLIMETER)},
      {Double.POSITIVE_INFINITY, UNITS.MILLIMETER, null},
      {-Double.POSITIVE_INFINITY, UNITS.MILLIMETER, null},
      {1.0, null, new Length(1.0, UNITS.REFERENCEFRAME)},
    };
  }

  @Test(dataProvider = "positionNoUnit")
  public void testGetPositionNoUnit(Double value, Length length) {
    assertEquals(length, FormatTools.getPosition(value));
    assertEquals(length, FormatTools.getPosition(value, ""));
  }

  @Test(dataProvider = "positionStringUnit")
  public void testGetPositionStringUnit(Double value, String unit, Length length) {
    assertEquals(length, FormatTools.getPosition(value, unit));
  }


  @Test(dataProvider = "positionUnit")
  public void testGetPositionUnit(Double value, Unit<Length> unit, Length length) {
    assertEquals(length, FormatTools.getPosition(value, unit));
  }
}
