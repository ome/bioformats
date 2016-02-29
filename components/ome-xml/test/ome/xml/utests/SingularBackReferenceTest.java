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

import ome.xml.model.Detector;
import ome.xml.model.Instrument;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 */
public class SingularBackReferenceTest {

  public static final String DETECTOR_ID = "Detector:1";

  public static final String INSTRUMENT_ID = "Instrument:1";

  private Instrument instrument;

  private Detector detector;

  @BeforeMethod
  public void setUp() {
    instrument = new Instrument();
    instrument.setID(INSTRUMENT_ID);
    detector = new Detector();
    detector.setID("DETECTOR_ID");
    instrument.addDetector(detector);
  }

  @Test
  public void testDetectorInstrumentBackReference() {
    Instrument backReference = detector.getInstrument();
    assertEquals(1, instrument.sizeOfDetectorList());
    Detector forwardReference = instrument.getDetector(0);
    assertNotNull(forwardReference);
    assertNotNull(backReference);
    assertEquals(INSTRUMENT_ID, backReference.getID());
  }
}
