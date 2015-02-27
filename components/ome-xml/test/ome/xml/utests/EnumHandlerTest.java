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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/EnumHandlerTest.java;hb=HEAD">Gitweb</a></dd></dl>
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
