//
// ColorTest.java
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

import ome.xml.model.primitives.Color;

import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/ColorTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/ColorTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ColorTest {

  @Test
  public void testBlue() {
    Color blue = new Color(0, 0, 255, 255);
    assertEquals(blue.getValue().intValue(), 65535);
    assertEquals(blue.getRed(), 0);
    assertEquals(blue.getGreen(), 0);
    assertEquals(blue.getBlue(), 255);
    assertEquals(blue.getAlpha(), 255);
  }

  @Test
  public void testRed() {
    Color red = new Color(255, 0, 0, 255);
    assertEquals(red.getValue().intValue(), -16776961);
    assertEquals(red.getRed(), 255);
    assertEquals(red.getGreen(), 0);
    assertEquals(red.getBlue(), 0);
    assertEquals(red.getAlpha(), 255);
  }

  @Test
  public void testGreen() {
    Color green = new Color(0, 255, 0, 255);
    assertEquals(green.getValue().intValue(), 16711935);
    assertEquals(green.getRed(), 0);
    assertEquals(green.getGreen(), 255);
    assertEquals(green.getBlue(), 0);
    assertEquals(green.getAlpha(), 255);
  }

  @Test
  public void testCyan() {
    Color cyan = new Color(0, 255, 255, 255);
    assertEquals(cyan.getValue().intValue(), 16777215);
    assertEquals(cyan.getRed(), 0);
    assertEquals(cyan.getGreen(), 255);
    assertEquals(cyan.getBlue(), 255);
    assertEquals(cyan.getAlpha(), 255);
  }

  @Test
  public void testMagenta() {
    Color magenta = new Color(255, 0, 255, 255);
    assertEquals(magenta.getValue().intValue(), -16711681);
    assertEquals(magenta.getRed(), 255);
    assertEquals(magenta.getGreen(), 0);
    assertEquals(magenta.getBlue(), 255);
    assertEquals(magenta.getAlpha(), 255);
  }

  @Test
  public void testYellow() {
    Color yellow = new Color(255, 255, 0, 255);
    assertEquals(yellow.getValue().intValue(), -65281);
    assertEquals(yellow.getRed(), 255);
    assertEquals(yellow.getGreen(), 255);
    assertEquals(yellow.getBlue(), 0);
    assertEquals(yellow.getAlpha(), 255);
  }

}
