//
// SingularBackReferenceTest.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2012 Open Microscopy Environment
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

import ome.xml.model.Detector;
import ome.xml.model.Instrument;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/test/ome/xml/utests/SingularBackReferenceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/test/ome/xml/utests/SingularBackReferenceTest.java;hb=HEAD">Gitweb</a></dd></dl>
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
