/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2015 - 2017 Open Microscopy Environment:
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


  @DataProvider(name = "pixelTypes")
  public Object[][] createPixelTypes() {
    return new Object[][] {
      {FormatTools.INT8, -128L, 127L},
      {FormatTools.INT16, -32768L, 32767L},
      {FormatTools.INT32, -2147483648L, 2147483647L},
      {FormatTools.UINT8, 0L, 255L},
      {FormatTools.UINT16, 0L, 65535L},
      {FormatTools.UINT32, 0L, 4294967295L},
      {FormatTools.FLOAT, -2147483648L, 2147483647L},
      {FormatTools.DOUBLE, -2147483648L, 2147483647L},
    };
  }

  @Test(dataProvider = "pixelTypes")
  public void testDefaultMinMax(int type, long min, long max) {
    long[] lim = FormatTools.defaultMinMax(type);
    assertEquals(lim[0], min);
    assertEquals(lim[1], max);
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


  @DataProvider(name = "stagePositionStringUnit")
  public Object[][] createStagePositionStringUnit() {
    return new Object[][] {
      {null, "mm", null},
      {0.0, "mm", new Length(0.0, UNITS.MILLIMETER)},
      {1.0, "mm", new Length(1.0, UNITS.MILLIMETER)},
      {.1, "mm", new Length(.1, UNITS.MILLIMETER)},
      {Constants.EPSILON, "mm", new Length(Constants.EPSILON, UNITS.MILLIMETER)},
      {-Constants.EPSILON, "mm", new Length(-Constants.EPSILON, UNITS.MILLIMETER)},
      {Double.POSITIVE_INFINITY, "mm", null},
      {Double.NaN, "mm", null},
      // Invalid length string units
      {1.0, null, new Length(1.0, UNITS.REFERENCEFRAME)},
      {1.0, "", new Length(1.0, UNITS.REFERENCEFRAME)},
      {1.0, "foo", new Length(1.0, UNITS.REFERENCEFRAME)},
      {1.0, "s", new Length(1.0, UNITS.REFERENCEFRAME)},
    };
  }

  @DataProvider(name = "stagePositionUnit")
  public Object[][] createStagePositionUnit() {
    return new Object[][] {
      {null, UNITS.MILLIMETER, null},
      {0.0, UNITS.MILLIMETER, new Length(0.0, UNITS.MILLIMETER)},
      {1.0, UNITS.MILLIMETER, new Length(1.0, UNITS.MILLIMETER)},
      {.1, UNITS.MILLIMETER, new Length(.1, UNITS.MILLIMETER)},
      {Constants.EPSILON, UNITS.MILLIMETER, new Length(Constants.EPSILON, UNITS.MILLIMETER)},
      {-Constants.EPSILON, UNITS.MILLIMETER, new Length(-Constants.EPSILON, UNITS.MILLIMETER)},
      {Double.POSITIVE_INFINITY, UNITS.MILLIMETER, null},
      {-Double.POSITIVE_INFINITY, UNITS.MILLIMETER, null},
      {Double.NaN, UNITS.MILLIMETER, null},
      {1.0, null, null},
    };
  }

  @DataProvider(name = "lengthStringsWithDefault")
  public Object[][] createLengthStringsWithDefault() {
    return new Object[][] {
      {"invalidvalue", "", "m", null},
      {"1.0", "", "", null},
      {"1.0", "", "s", null},
      {"1.0", "s", "m", null},
      {"1e400", "m", "", null},
      {"-1e400", "m", "", null},
      {"e10", "m", "", null},
      {"1.0", "invalidunit", "m", null},
      {"-0.1", "m", "", new Length(-0.1, UNITS.METER)},
      {"0.0", "m", "", new Length(0.0, UNITS.METER)},
      {"1.0", "m", "", new Length(1.0, UNITS.METER)},
      {"1.0", "m", "m", new Length(1.0, UNITS.METER)},
      {"1.0", "m", "mm", new Length(1.0, UNITS.METER)},
      {"1.0", "mm", "mm", new Length(1.0, UNITS.MILLIMETER)},
      {"1.0", "", "m", new Length(1.0, UNITS.METER)},
      {"1.0", "", "mm", new Length(1.0, UNITS.MILLIMETER)},
    };
  }

  @DataProvider(name = "lengthStrings")
  public Object[][] createLengthStrings() {
    return new Object[][] {
      {"1.0", "", null},
      {"invalidvalue", "", null},
      {"1.0", "invalidunit", null},
      {"1.0", "s", null},
      {"1e400", "m", null},
      {"-1e400", "m", null},
      {"e10", "m", null},
      {"-1e-3", "mm", new Length(-0.001, UNITS.MILLIMETER)},
      {"-0.1", "m", new Length(-0.1, UNITS.METER)},
      {"0.0", "m", new Length(0.0, UNITS.METER)},
      {"1.0", "m", new Length(1.0, UNITS.METER)},
      {"1.0", "mm", new Length(1.0, UNITS.MILLIMETER)},
    };
  }


  @DataProvider(name = "fileLists")
  public Object[][] createFileLists() {
    return new Object[][] {
      {null, 0},
      {new String[]{}, 0},
      {new String[]{"/dir1/dir2/test.tif"}, 0},
      {new String[]{"test.tif"}, 0},
      {new String[]{"/dir1/dir2/test.tif", "/dir1/dir2/test2.tif"}, 0},
      {new String[]{"/dir1/test.tif", "/dir1/dir2/test2.tif"}, 1},
      {new String[]{"test.tif", "dir2/test2.tif"}, 1},
      {new String[]{"/test.tif", "/dir1/dir2/test2.tif"}, 2},
      {new String[]{"/dir1/dir2/test.tif", "/dir3/dir4/dir5/test2.tif"}, 3},
    };
  }

  @DataProvider(name = "wellNames")
  public Object[][] createWells() {
    return new Object[][] {
        {0, 0, "A", "A01"},
        {10, 5, "K", "K06"},
        {30, 99, "AE", "AE100"}
    };
  }

  @Test(dataProvider = "stagePositionStringUnit")
  public void testGetStagePositionStringUnit(Double value, String unit, Length length) {
    assertEquals(length, FormatTools.getStagePosition(value, unit));
  }


  @Test(dataProvider = "stagePositionUnit")
  public void testGetStagePositionUnit(Double value, Unit<Length> unit, Length length) {
    assertEquals(length, FormatTools.getStagePosition(value, unit));
  }

  @Test(dataProvider = "lengthStringsWithDefault")
  public void testParseLengthWithDefault(String value, String unit, String defaultUnit, Length length) {
    assertEquals(length, FormatTools.parseLength(value + unit, defaultUnit));
    assertEquals(length, FormatTools.parseLength(value + " " + unit, defaultUnit));
    assertEquals(length, FormatTools.parseLength(" " + value + unit, defaultUnit));
    assertEquals(length, FormatTools.parseLength(value + unit + " ", defaultUnit));
  }

  @Test(dataProvider = "lengthStrings")
  public void testParseLength(String value, String unit, Length length) {
    assertEquals(length, FormatTools.parseLength(value + unit));
    assertEquals(length, FormatTools.parseLength(value + " " + unit));
    assertEquals(length, FormatTools.parseLength(" " + value + unit));
    assertEquals(length, FormatTools.parseLength(value + unit + " "));
  }

  @Test(dataProvider = "fileLists")
  public void testGetRequiredDirectories(String[] files, int number) {
    String[] newfiles = null;
    if (files != null) {
      newfiles = new String[files.length];
      for (int i = 0; i < files.length; i++) {
        newfiles[i] = files[i].replace('/', File.separatorChar);
      }
    }
    assertEquals(number, FormatTools.getRequiredDirectories(newfiles));
  }

  @Test(dataProvider = "wellNames")
  public void testWellNames(Integer row, Integer column, String rowName, String wellName) {
    assertEquals(FormatTools.getWellRowName(row), rowName);
    assertEquals(FormatTools.getWellName(row, column), wellName);
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void testInvalidWellRow() {
    FormatTools.getWellRowName(-1);
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void testInvalidWellColumn() {
    FormatTools.getWellName(0, -1);
  }
}
