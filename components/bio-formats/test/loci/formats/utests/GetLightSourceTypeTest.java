//
// GetLightSourceTypeTest.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;

import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.OME;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Chris Allan <callan at blackcat dot ca>
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/GetLightSourceTypeTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/GetLightSourceTypeTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class GetLightSourceTypeTest {

  private IMetadata metadata;

  @BeforeClass
  public void setUp() throws Exception {
    metadata = new OMEXMLMetadataImpl();
    OME ome = new OME();
    Instrument instrument = new Instrument();
    instrument.addLightSource(new Arc());
    instrument.addLightSource(new Filament());
    instrument.addLightSource(new Laser());
    instrument.addLightSource(new LightEmittingDiode());
    ome.addInstrument(instrument);
    metadata.setRoot(ome);
  }

  @Test
  public void testLightSourceType() throws Exception {
    assertEquals(1, metadata.getInstrumentCount());
    assertEquals(4, metadata.getLightSourceCount(0));
    assertEquals("Arc", metadata.getLightSourceType(0, 0));
    assertEquals("Filament", metadata.getLightSourceType(0, 1));
    assertEquals("Laser", metadata.getLightSourceType(0, 2));
    assertEquals("LightEmittingDiode", metadata.getLightSourceType(0, 3));
  }

}
