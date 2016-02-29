/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package ome.xml.utests;

import static org.testng.AssertJUnit.*;

import ome.xml.model.primitives.Color;

import org.testng.annotations.Test;

/**
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
