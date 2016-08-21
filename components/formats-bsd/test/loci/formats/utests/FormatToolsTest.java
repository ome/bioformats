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

package loci.formats.utests;

import static org.testng.AssertJUnit.*;

import java.io.IOException;

import loci.formats.FormatTools;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/FormatToolsTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/FormatToolsTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Roger Leigh <r.leigh at dundee dot ac dot uk>
 */
public class FormatToolsTest {

  @Test
  public void testDefaultMinMaxInt8() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT8);
    assertEquals(lim[0], -128);
    assertEquals(lim[1], 127);
  }

  @Test
  public void testDefaultMinMaxInt16() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT16);
    assertEquals(lim[0], -32768);
    assertEquals(lim[1], 32767);
  }

  @Test
  public void testDefaultMinMaxInt32() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.INT32);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  @Test
  public void testDefaultMinMaxUint8() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT8);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 255);
  }

  @Test
  public void testDefaultMinMaxUint16() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT16);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 65535);
  }

  @Test
  public void testDefaultMinMaxUint32() {
    long[] lim = FormatTools.defaultMinMax(FormatTools.UINT32);
    assertEquals(lim[0], 0);
    assertEquals(lim[1], 4294967295L);
  }

  public void testDefaultMinMaxFloat() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(FormatTools.FLOAT);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  public void testDefaultMinMaxDouble() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(FormatTools.DOUBLE);
    assertEquals(lim[0], -2147483648);
    assertEquals(lim[1], 2147483647);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testDefaultMinMaxInvalid() throws IllegalArgumentException {
    long[] lim = FormatTools.defaultMinMax(9999); // Invalid pixel type number
  }

}
