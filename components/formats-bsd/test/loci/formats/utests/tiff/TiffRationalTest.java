/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import loci.formats.tiff.TiffRational;

import org.testng.annotations.Test;

/**
 * Unit tests for TIFF rationals.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffRationalTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffRationalTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TiffRationalTest {

  @Test
  public void testEqualTiffRational() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 4);
    assertTrue(a.equals(b));
    assertTrue(a.equals((Object) b));
    assertEquals(0, a.compareTo(b));
  }
  
  @Test
  public void testEqualObject() {
    TiffRational a = new TiffRational(1, 4);
    Object b = new Object();
    assertTrue(!(a.equals(b)));
  }

  @Test
  public void testNotEqual() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 5);
    assertTrue(!(a.equals(b)));
  }

  @Test
  public void testGreaterThan() {
    TiffRational a = new TiffRational(1, 4);
    TiffRational b = new TiffRational(1, 5);
    assertEquals(1, a.compareTo(b));
  }

  @Test
  public void testLessThan() {
    TiffRational a = new TiffRational(1, 5);
    TiffRational b = new TiffRational(1, 4);
    assertEquals(-1, a.compareTo(b));
  }
}
