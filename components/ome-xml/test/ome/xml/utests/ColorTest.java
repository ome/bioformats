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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
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
    Color blue = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    assertEquals(blue.getRed(), 0.0f);
    assertEquals(blue.getGreen(), 0.0f);
    assertEquals(blue.getBlue(), 1.0f);
    assertEquals(blue.getAlpha(), 1.0f);
  }

  @Test
  public void testRed() {
    Color red = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    assertEquals(red.getRed(), 1.0f);
    assertEquals(red.getGreen(), 0.0f);
    assertEquals(red.getBlue(), 0.0f);
    assertEquals(red.getAlpha(), 1.0f);
  }

  @Test
  public void testGreen() {
    Color green = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    assertEquals(green.getRed(), 0.0f);
    assertEquals(green.getGreen(), 1.0f);
    assertEquals(green.getBlue(), 0.0f);
    assertEquals(green.getAlpha(), 1.0f);
  }

  @Test
  public void testCyan() {
    Color cyan = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    assertEquals(cyan.getRed(), 0.0f);
    assertEquals(cyan.getGreen(), 1.0f);
    assertEquals(cyan.getBlue(), 1.0f);
    assertEquals(cyan.getAlpha(), 1.0f);
  }

  @Test
  public void testMagenta() {
    Color magenta = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    assertEquals(magenta.getRed(), 1.0f);
    assertEquals(magenta.getGreen(), 0.0f);
    assertEquals(magenta.getBlue(), 1.0f);
    assertEquals(magenta.getAlpha(), 1.0f);
  }

  @Test
  public void testYellow() {
    Color yellow = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    assertEquals(yellow.getRed(), 1.0f);
    assertEquals(yellow.getGreen(), 1.0f);
    assertEquals(yellow.getBlue(), 0.0f);
    assertEquals(yellow.getAlpha(), 1.0f);
  }

  @Test
  public void testBlue8Bit() {
    Color blue = new Color(0, 0, 255, 255);
    assertEquals(blue.getRed8Bit(), 0);
    assertEquals(blue.getGreen8Bit(), 0);
    assertEquals(blue.getBlue8Bit(), 255);
    assertEquals(blue.getAlpha8Bit(), 255);
  }

  @Test
  public void testRed8Bit() {
    Color red = new Color(255, 0, 0, 255);
    assertEquals(red.getRed8Bit(), 255);
    assertEquals(red.getGreen8Bit(), 0);
    assertEquals(red.getBlue8Bit(), 0);
    assertEquals(red.getAlpha8Bit(), 255);
  }

  @Test
  public void testGreen8Bit() {
    Color green = new Color(0, 255, 0, 255);
    assertEquals(green.getRed8Bit(), 0);
    assertEquals(green.getGreen8Bit(), 255);
    assertEquals(green.getBlue8Bit(), 0);
    assertEquals(green.getAlpha8Bit(), 255);
  }

  @Test
  public void testCyan8Bit() {
    Color cyan = new Color(0, 255, 255, 255);
    assertEquals(cyan.getRed8Bit(), 0);
    assertEquals(cyan.getGreen8Bit(), 255);
    assertEquals(cyan.getBlue8Bit(), 255);
    assertEquals(cyan.getAlpha8Bit(), 255);
  }

  @Test
  public void testMagenta8Bit() {
    Color magenta = new Color(255, 0, 255, 255);
    assertEquals(magenta.getRed8Bit(), 255);
    assertEquals(magenta.getGreen8Bit(), 0);
    assertEquals(magenta.getBlue8Bit(), 255);
    assertEquals(magenta.getAlpha8Bit(), 255);
  }

  @Test
  public void testYellow8Bit() {
    Color yellow = new Color(255, 255, 0, 255);
    assertEquals(yellow.getRed8Bit(), 255);
    assertEquals(yellow.getGreen8Bit(), 255);
    assertEquals(yellow.getBlue8Bit(), 0);
    assertEquals(yellow.getAlpha8Bit(), 255);
  }

  @Test
  public void testBlue16Bit() {
    Color blue = new Color(0, 0, 255, 255);
    assertEquals(blue.getRed16Bit(), 0);
    assertEquals(blue.getGreen16Bit(), 0);
    assertEquals(blue.getBlue16Bit(), 65535);
    assertEquals(blue.getAlpha16Bit(), 65535);
  }

  @Test
  public void testRed16Bit() {
    Color red = new Color(255, 0, 0, 255);
    assertEquals(red.getRed16Bit(), 65535);
    assertEquals(red.getGreen16Bit(), 0);
    assertEquals(red.getBlue16Bit(), 0);
    assertEquals(red.getAlpha16Bit(), 65535);
  }

  @Test
  public void testGreen16Bit() {
    Color green = new Color(0, 255, 0, 255);
    assertEquals(green.getRed16Bit(), 0);
    assertEquals(green.getGreen16Bit(), 65535);
    assertEquals(green.getBlue16Bit(), 0);
    assertEquals(green.getAlpha16Bit(), 65535);
  }

  @Test
  public void testCyan16Bit() {
    Color cyan = new Color(0, 255, 255, 255);
    assertEquals(cyan.getRed16Bit(), 0);
    assertEquals(cyan.getGreen16Bit(), 65535);
    assertEquals(cyan.getBlue16Bit(), 65535);
    assertEquals(cyan.getAlpha16Bit(), 65535);
  }

  @Test
  public void testMagenta16Bit() {
    Color magenta = new Color(255, 0, 255, 255);
    assertEquals(magenta.getRed16Bit(), 65535);
    assertEquals(magenta.getGreen16Bit(), 0);
    assertEquals(magenta.getBlue16Bit(), 65535);
    assertEquals(magenta.getAlpha16Bit(), 65535);
  }

  @Test
  public void testYellow16Bit() {
    Color yellow = new Color(255, 255, 0, 255);
    assertEquals(yellow.getRed16Bit(), 65535);
    assertEquals(yellow.getGreen16Bit(), 65535);
    assertEquals(yellow.getBlue16Bit(), 0);
    assertEquals(yellow.getAlpha16Bit(), 65535);
  }

  // Test deserialisation.
  @Test
  public void testString1() {
    Color test1 = Color.valueOf("0.0 1.0 0.25 0.5");
    assertEquals(test1.getRed(), 0.0f);
    assertEquals(test1.getGreen(), 1.0f);
    assertEquals(test1.getBlue(), 0.25f);
    assertEquals(test1.getAlpha(), 0.5f);
  }

  // Test range clamp.
  @Test
  public void testString2() {
    Color test2 = Color.valueOf("12.4 -431.0 0.75 0.5");
    assertEquals(test2.getRed(), 1.0f);
    assertEquals(test2.getGreen(), 0.0f);
    assertEquals(test2.getBlue(), 0.75f);
    assertEquals(test2.getAlpha(), 0.5f);
  }

  // Test fail.
  @Test
  public void testString3() {
    Color test2 = Color.valueOf("0.5 0.5 foo bar baz");
    System.err.println("RGBA=" + test2.getRed() + " " + test2.getGreen() + " " + test2.getBlue() + " " + test2.getAlpha());
    assertEquals(test2.getRed(), 0.5f);
    assertEquals(test2.getGreen(), 0.5f);
    assertEquals(test2.getBlue(), 1.0f);
    assertEquals(test2.getAlpha(), 1.0f);
  }

}
