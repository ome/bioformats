//
// PercentFractionTest.java
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

import ome.xml.model.primitives.PercentFraction;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/PercentFractionTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/PercentFractionTest.java">SVN</a></dd></dl>
 */
public class PercentFractionTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new PercentFraction(-1.0f);
  }

  @Test
  public void testZero() {
    new PercentFraction(0.0f);
  }

  @Test
  public void testPositiveOne() {
    new PercentFraction(1.0f);
  }

  @Test
  public void testEquivalence() {
    PercentFraction a = new PercentFraction(0.5f);
    PercentFraction b = new PercentFraction(0.5f);
    PercentFraction c = new PercentFraction(1.0f);
    Float d = new Float(0.5f);
    Float e = new Float(1.0f);
    Float f = new Float(1.5f);
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
    assertEquals("1.0", new PercentFraction(1.0f).toString());
  }
}
