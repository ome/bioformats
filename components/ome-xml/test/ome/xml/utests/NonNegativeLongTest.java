//
// NonNegativeLongTest.java
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

import ome.xml.model.primitives.NonNegativeLong;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/NonNegativeLongTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/NonNegativeLongTest.java">SVN</a></dd></dl>
 */
public class NonNegativeLongTest {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testMinusOne() {
    new NonNegativeLong(-1L);
  }

  @Test
  public void testZero() {
    new NonNegativeLong(0L);
  }

  @Test
  public void testPositiveOne() {
    new NonNegativeLong(1L);
  }

  @Test
  public void testEquivalence() {
    NonNegativeLong a = new NonNegativeLong(1L);
    NonNegativeLong b = new NonNegativeLong(1L);
    NonNegativeLong c = new NonNegativeLong(2L);
    Long d = new Long(1L);
    Long e = new Long(2L);
    Long f = new Long(3L);
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
    assertEquals("1", new NonNegativeLong(1L).toString());
  }
}
