/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

package loci.formats.utests.xml;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.io.InputStream;

import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.Image;
import ome.xml.model.OME;
import ome.xml.model.Pixels;
import ome.xml.model.primitives.PositiveFloat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/Upgrade201006Test.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/Upgrade201006Test.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Colin Blackburn <cblackburn at dundee dot ac dot uk>
 */
public class Upgrade201006Test {

  private static final String XML_FILE = "2010-06.ome";

  private OMEXMLService service;
  private String xml;
  private OMEXMLMetadata metadata;
  private OME ome;

  @BeforeMethod
  public void setUp() throws Exception {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);

    InputStream s = Upgrade201006Test.class.getResourceAsStream(XML_FILE);
    byte[] b = new byte[s.available()];
    s.read(b);
    s.close();

    xml = new String(b);
    metadata = service.createOMEXMLMetadata(xml);
    ome = (OME) metadata.getRoot();
  }

  @Test
  public void getOMEXMLVersion() throws ServiceException {
    assertEquals("2013-10-dev-4", service.getOMEXMLVersion(metadata));
  }

  @Test
  public void validateUpgrade() throws ServiceException {
    assertEquals(1, ome.sizeOfImageList());
    Image image = ome.getImage(0);
    Pixels pixels = image.getPixels();
    // Pixels physical sizes are restricted to positive values
    assertEquals(new PositiveFloat(10000.0), pixels.getPhysicalSizeX());
    assertEquals(new PositiveFloat(10000.0), pixels.getPhysicalSizeY());
    assertNull(pixels.getPhysicalSizeZ());
  }

}
