//
// EnumHandlerTest.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2008 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.handlers.BinningEnumHandler;
import ome.xml.model.enums.handlers.CorrectionEnumHandler;
import ome.xml.model.enums.handlers.ImmersionEnumHandler;
import ome.xml.model.primitives.NonNegativeInteger;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java">SVN</a></dd></dl>
 */
public class EnumHandlerTest {

  @Test
  public void testImmersionOI() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("OI");
    assertEquals(Immersion.OIL, v);
  }

  @Test
  public void testImmersionOIWithWhitespace() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("   OI  ");
    assertEquals(Immersion.OIL, v);
  }

  @Test
  public void testImmersionDRY() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("DRY");
    assertEquals(Immersion.AIR, v);
  }

  @Test
  public void testImmersionDRYWithWhitespace() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("   DRY  ");
    assertEquals(Immersion.AIR, v);
  }

  @Test
  public void testImmersionWl() throws EnumerationException {
    ImmersionEnumHandler handler = new ImmersionEnumHandler();
    Immersion v = (Immersion) handler.getEnumeration("Wl");
    assertEquals(Immersion.WATER, v);
  }

  @Test
  public void testBinning1x1WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   1 x 1  ");
    assertEquals(Binning.ONEXONE, v);
  }

  @Test
  public void testBinning2x2WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   2 x 2  ");
    assertEquals(Binning.TWOXTWO, v);
  }

  @Test
  public void testBinning4x4WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   4 x 4  ");
    assertEquals(Binning.FOURXFOUR, v);
  }

  @Test
  public void testBinning8x8WithWhitespace() throws EnumerationException {
    BinningEnumHandler handler = new BinningEnumHandler();
    Binning v = (Binning) handler.getEnumeration("   8 x 8  ");
    assertEquals(Binning.EIGHTXEIGHT, v);
  }
}
