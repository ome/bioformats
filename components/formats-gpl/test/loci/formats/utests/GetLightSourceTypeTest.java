/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class GetLightSourceTypeTest {

  private IMetadata metadata;

  @BeforeClass
  public void setUp() throws Exception {
    metadata = new OMEXMLMetadataImpl();
    OMEXMLMetadataRoot ome = new OMEXMLMetadataRoot();
    Instrument instrument = new Instrument();
    instrument.addArc(new Arc());
    instrument.addFilament(new Filament());
    instrument.addLaser(new Laser());
    instrument.addLightEmittingDiode(new LightEmittingDiode());
    ome.addInstrument(instrument);
    metadata.setRoot(ome);
  }

  @Test
  public void testLightSourceType() throws Exception {
    assertEquals(1, metadata.getInstrumentCount());
    assertEquals(1, metadata.getArcCount(0));
    assertEquals(1, metadata.getFilamentCount(0));
    assertEquals(1, metadata.getLaserCount(0));
    assertEquals(1, metadata.getLightEmittingDiodeCount(0));
  }

}
