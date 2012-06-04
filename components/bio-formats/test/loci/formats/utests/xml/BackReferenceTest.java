//
// BackReferenceTest.java
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

package loci.formats.utests.xml;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.OME;
import ome.xml.model.Plate;
import ome.xml.model.Well;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/BackReferenceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/BackReferenceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class BackReferenceTest {

  private OMEXMLService service;
  private OMEXMLMetadata metadata;

  @BeforeMethod
  public void setUp() throws DependencyException, ServiceException, IOException
  {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);
    metadata = service.createOMEXMLMetadata();
  }

  @Test
  public void testPlateWellReferences() {
    metadata.setPlateID("Plate:0", 0);
    metadata.setWellID("Well:0:0", 0, 0);

    OME root = (OME) metadata.getRoot();

    Plate plate = root.getPlate(0);
    Well well = plate.getWell(0);

    assertNotNull(plate);
    assertNotNull(well);
    assertEquals(plate, well.getPlate());
  }

}
