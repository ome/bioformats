/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

import ome.xml.model.primitives.PercentFraction;

import org.testng.annotations.Test;

/**
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
