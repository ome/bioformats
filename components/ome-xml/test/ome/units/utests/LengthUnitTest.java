/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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

package ome.units.utests;

import static org.testng.AssertJUnit.*;

import ome.units.quantity.Length;
import ome.units.unit.Unit;

import ome.units.UNITS;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/units/utests/LengthUnitTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/units/utests/LengthUnitTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class LengthUnitTest {

  @Test
  public void testDummy() {
    int a = 1;
    assertEquals(a, 1);
  }

  @Test
  public void testMetricScaleUp() {
    Unit<Length> myMeter = UNITS.M;
    assertEquals(myMeter, UNITS.M);
    myMeter = myMeter.multiply(10);
    assertEquals(myMeter, UNITS.DAM);
    myMeter = myMeter.multiply(10);
    assertEquals(myMeter, UNITS.HM);
    myMeter = myMeter.multiply(10);
    assertEquals(myMeter, UNITS.KM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.MEGAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.GIGAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.TERAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.PETAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.EXAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.ZETTAM);
    myMeter = myMeter.multiply(1000);
    assertEquals(myMeter, UNITS.YOTTAM);
  }

  public void testMetricScaleDown01() {
    Unit<Length> unitA = UNITS.M;
    Unit<Length> unitB = UNITS.M;
    assertEquals(unitA, unitB);
  }
  @Test
  public void testMetricScaleDown02() {
    Unit<Length> unitA = UNITS.M.divide(10);
    Unit<Length> unitB = UNITS.DM;
    assertEquals(unitA, unitB);
  }
/*
  @Test(groups = "broken")
  public void testMetricScaleDown03() {
    Unit<Length> unitA = UNITS.DM.divide(10);
    Unit<Length> unitB = UNITS.CM;
    assertEquals(unitA, unitB);
  }
*/
  @Test
  public void testMetricScaleDown04() {
    Unit<Length> unitA = UNITS.CM.divide(10);
    Unit<Length> unitB = UNITS.MM;
    assertEquals(unitA, unitB);
  }
/*
  @Test(groups = "broken")
  public void testMetricScaleDown05() {
    Unit<Length> unitA = UNITS.MM.divide(10);
    Unit<Length> unitB = UNITS.MICROM;
    assertEquals(unitA, unitB);
  }
*/
  @Test
  public void testMetricScaleDown06() {
    Unit<Length> unitA = UNITS.MICROM.divide(1000);
    Unit<Length> unitB = UNITS.NM;
    assertEquals(unitA, unitB);
  }
/*
  @Test(groups = "broken")
  public void testMetricScaleDown07() {
    Unit<Length> unitA = UNITS.NM.divide(1000);
    Unit<Length> unitB = UNITS.PM;
    assertEquals(unitA, unitB);
  }
*/
  @Test
  public void testMetricScaleDown08() {
    Unit<Length> unitA = UNITS.PM.divide(1000);
    Unit<Length> unitB = UNITS.FM;
    assertEquals(unitA, unitB);
  }
  @Test
  public void testMetricScaleDown09() {
    Unit<Length> unitA = UNITS.FM.divide(1000);
    Unit<Length> unitB = UNITS.AM;
    assertEquals(unitA, unitB);
  }
/*
  @Test(groups = "broken")
  public void testMetricScaleDown10() {
    Unit<Length> unitA = UNITS.AM.divide(1000);
    Unit<Length> unitB = UNITS.ZM;
    assertEquals(unitA, unitB);
  }
  @Test(groups = "broken")
  public void testMetricScaleDown11() {
    Unit<Length> unitA = UNITS.ZM.divide(1000);
    Unit<Length> unitB = UNITS.YM;
    assertEquals(unitA, unitB);
  }
*/

}
