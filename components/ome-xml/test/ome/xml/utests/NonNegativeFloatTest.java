//
// NonNegativeFloatFloat.java
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

import ome.xml.model.primitives.NonNegativeFloat;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/NonNegativeFloatTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/NonNegativeFloatTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NonNegativeFloatTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new NonNegativeFloat(-1.0);
  }

  @Test
  public void testZero() {
    new NonNegativeFloat(0.0);
  }

  @Test
  public void testPositiveOne() {
    new NonNegativeFloat(1.0);
  }

  @Test
  public void testEquivalence() {
    NonNegativeFloat a = new NonNegativeFloat(0.1);
    NonNegativeFloat b = new NonNegativeFloat(0.1);
    NonNegativeFloat c = new NonNegativeFloat(2.0);
    Double d = new Double(0.1);
    Double e = new Double(2.0);
    Double f = new Double(3.0);
    assertFalse(a == b);
    assertFalse(a == c);
    assertTrue(a.equals(b));
    assertFalse(a.equals(c));
    assertTrue(a.equals(d));
    assertFalse(a.equals(e));
    assertTrue(c.equals(e));
    assertFalse(a.equals(f));
  }

  @Test
  public void testToString() {
    assertEquals("0.1", new NonNegativeFloat(0.1).toString());
  }
}
