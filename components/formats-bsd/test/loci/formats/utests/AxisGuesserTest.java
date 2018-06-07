/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.util.List;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import loci.formats.FilePattern;
import loci.formats.AxisGuesser;

import static loci.formats.AxisGuesser.Z_AXIS;
import static loci.formats.AxisGuesser.T_AXIS;
import static loci.formats.AxisGuesser.C_AXIS;
import static loci.formats.AxisGuesser.S_AXIS;
import static loci.formats.AxisGuesser.UNKNOWN_AXIS;
import static loci.formats.AxisGuesser.Z_PREFIXES;
import static loci.formats.AxisGuesser.T_PREFIXES;
import static loci.formats.AxisGuesser.C_PREFIXES;
import static loci.formats.AxisGuesser.S_PREFIXES;


public class AxisGuesserTest {

  @DataProvider(name = "booleanStates")
  public Object[][] createBooleans() {
    return new Object[][] {{true}, {false}};
  }

  @DataProvider(name = "prefixCases")
  public Object[][] createPrefixCases() {
    List<Object[]> cases = new ArrayList<Object[]>();
    String template = "%s_<0-1>%s_<2-3>%s_<4-5>%s_<6-7>";
    for (String z: Z_PREFIXES) {
      for (String t: T_PREFIXES) {
        for (String c: C_PREFIXES) {
          for (String s: S_PREFIXES) {
            cases.add(new Object[] {String.format(template, z, t, c, s)});
            cases.add(new Object[] {String.format(template,
              z.toUpperCase(), t.toUpperCase(), c.toUpperCase(), s.toUpperCase()
            )});
          }
        }
      }
    }
    return cases.toArray(new Object[cases.size()][]);
  }

  @DataProvider(name = "bioRadCases")
  public Object[][] createBioRadCases() {
    List<Object[]> cases = new ArrayList<Object[]>();
    String template = "_<%s>%s";
    for (String suffix: new String[] {".pic", ".PIC"}) {
      for (String  block: new String[] {"1,2", "2,3", "1,3", "1,2,3"}) {
        cases.add(new Object[] {String.format(template, block, suffix)});
      }
    }
    return cases.toArray(new Object[cases.size()][]);
  }

  @DataProvider(name = "RGBCases")
  public Object[][] createRGBCases() {
    List<Object[]> cases = new ArrayList<Object[]>();
    String template = "_<%s>";
    String[] blocks = new String[] {
      "r,g,b", "r,b,g", "g,r,b", "g,b,r", "b,r,g", "b,g,r",
      "r,g", "g,r"
    };
    for (String  b: blocks) {
      cases.add(new Object[] {String.format(template, b)});
      cases.add(new Object[] {String.format(template, b.toUpperCase())});
    }
    return cases.toArray(new Object[cases.size()][]);
  }

  @DataProvider(name = "swapCases")
  public Object[][] createSwapCases() {
    return new Object[][] {
      {"z<0-1>_<0-1>", 2, 1, true, new int[] {Z_AXIS, T_AXIS}},
      {"z<0-1>_<0-1>", 2, 1, false, new int[] {Z_AXIS, C_AXIS}},
      {"_<0-1>t<0-1>", 1, 2, true, new int[] {Z_AXIS, T_AXIS}},
      {"_<0-1>t<0-1>", 1, 2, false, new int[] {C_AXIS, T_AXIS}}
    };
  }

  @DataProvider(name = "fillInCases")
  public Object[][] createFillInCases() {
    return new Object[][] {
      // Assign missing dim if its size is 1, else assign last dim in order
      {"z<0-1>t<0-1>_<0-1>", "XYZCT", 1, 1, 1,
       new int[] {Z_AXIS, T_AXIS, C_AXIS}},
      {"z<0-1>t<0-1>_<0-1>", "XYZCT", 1, 1, 2,
       new int[] {Z_AXIS, T_AXIS, T_AXIS}},
      {"z<0-1>c<0-1>_<0-1>", "XYZTC", 1, 1, 1,
       new int[] {Z_AXIS, C_AXIS, T_AXIS}},
      {"z<0-1>c<0-1>_<0-1>", "XYZTC", 1, 2, 1,
       new int[] {Z_AXIS, C_AXIS, C_AXIS}},
      {"t<0-1>c<0-1>_<0-1>", "XYTZC", 1, 1, 1,
       new int[] {T_AXIS, C_AXIS, Z_AXIS}},
      {"t<0-1>c<0-1>_<0-1>", "XYTZC", 2, 1, 1,
       new int[] {T_AXIS, C_AXIS, C_AXIS}}
    };
  }

  private void check(
      String p, String order, int sZ, int sT, int sC, boolean cert,  // IN
      String newOrder, int[] types) {                                // OUT
    FilePattern fp = new FilePattern(p);
    AxisGuesser ag = new AxisGuesser(fp, order, sZ, sT, sC, cert);
    assertEquals(ag.getFilePattern().getPattern(), p);
    assertEquals(ag.getOriginalOrder(), order);
    assertEquals(ag.getAdjustedOrder(), newOrder);
    assertEquals(ag.getAxisTypes(), types);
    checkAxisCount(ag, types);
  }

  private String mkPrefix(String baseTag, Boolean upperCase) {
    if (upperCase) {
      baseTag = baseTag.toUpperCase();
    }
    return String.format("_%s", baseTag);
  }

  private void checkAxisCount(AxisGuesser ag, int[] axisTypes) {
    // Should be as simple as possible
    int countZ = 0, countT = 0, countC = 0, countS = 0;
    for (int t: axisTypes) {
      switch (t) {
      case Z_AXIS:
        countZ++;
        break;
      case T_AXIS:
        countT++;
        break;
      case C_AXIS:
        countC++;
        break;
      case S_AXIS:
        countS++;
        break;
      }
    }
    assertEquals(ag.getAxisCount(Z_AXIS), countZ);
    assertEquals(ag.getAxisCountZ(), countZ);
    assertEquals(ag.getAxisCount(T_AXIS), countT);
    assertEquals(ag.getAxisCountT(), countT);
    assertEquals(ag.getAxisCount(C_AXIS), countC);
    assertEquals(ag.getAxisCountC(), countC);
    assertEquals(ag.getAxisCount(S_AXIS), countS);
    assertEquals(ag.getAxisCountS(), countS);
    assertEquals(ag.getAxisCount(UNKNOWN_AXIS), 0);
  }

  @Test(dataProvider = "prefixCases")
  public void testPrefix(String pattern) {
    check(pattern, "XYZCT", 1, 1, 1, true,
          "XYZCT", new int[] {Z_AXIS, T_AXIS, C_AXIS, S_AXIS});
  }

  @Test(dataProvider = "bioRadCases")
  public void testBioRad(String pattern) {
    check(pattern, "XYZCT", 1, 1, 1, true, "XYZCT", new int[] {C_AXIS});
  }

  @Test(dataProvider = "RGBCases")
  public void testRGB(String pattern) {
    check(pattern, "XYZCT", 1, 1, 1, true, "XYZCT", new int[] {C_AXIS});
  }

  @Test(dataProvider = "swapCases")
  public void testSwap(
      String pattern, int sZ, int sT, boolean isCertain, int[] types) {
    String order = "XYZCT";
    String newOrder = isCertain ? order : "XYTCZ";
    check(pattern, order, sZ, sT, 1, isCertain, newOrder, types);
  }

  @Test(dataProvider = "fillInCases")
  public void testFillIn(
      String pattern, String order, int sZ, int sT, int sC, int[] types) {
    check(pattern, order, sZ, sT, sC, true, order, types);
  }

  @Test(dataProvider = "booleanStates")
  public void testGetAxisType(Boolean upperCase) {
    for (String s: Z_PREFIXES) {
      assertEquals(AxisGuesser.getAxisType(mkPrefix(s, upperCase)), Z_AXIS);
    }
    for (String s: T_PREFIXES) {
      assertEquals(AxisGuesser.getAxisType(mkPrefix(s, upperCase)), T_AXIS);
    }
    for (String s: C_PREFIXES) {
      assertEquals(AxisGuesser.getAxisType(mkPrefix(s, upperCase)), C_AXIS);
    }
    for (String s: S_PREFIXES) {
      assertEquals(AxisGuesser.getAxisType(mkPrefix(s, upperCase)), S_AXIS);
    }
  }

}
