/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.services.OMEXMLService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class OMEXMLServiceTest {

  private static final String XML_FILE = "2008-09.ome";

  private OMEXMLService service;
  private String xml;

  @BeforeMethod
  public void setUp() throws DependencyException, IOException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);

    InputStream s = OMEXMLServiceTest.class.getResourceAsStream(XML_FILE);
    byte[] b = new byte[s.available()];
    s.read(b);
    s.close();

    xml = new String(b);
  }

  @Test
  public void testGetLatestVersion() {
    assertEquals("2016-06", service.getLatestVersion());
  }

  @Test
  public void testCreateEmptyOMEXMLMetadata() throws ServiceException {
    assertNotNull(service.createOMEXMLMetadata());
  }

  @Test
  public void testCreateOMEXMLMetadata() throws ServiceException {
    assertNotNull(service.createOMEXMLMetadata(xml));
  }

  @Test
  public void testCreateOMEXMLRoot() throws ServiceException {
    assertNotNull(service.createOMEXMLRoot(xml));
  }

  @Test
  public void isOMEXMLMetadata() throws ServiceException {
    assertEquals(true,
      service.isOMEXMLMetadata(service.createOMEXMLMetadata()));
  }

  @Test
  public void getOMEXMLVersion() throws ServiceException {
    assertEquals("2016-06",
      service.getOMEXMLVersion(service.createOMEXMLMetadata(xml)));
  }

  @Test
  public void getOMEXML() throws ServiceException {
    assertNotNull(service.getOMEXML(service.createOMEXMLMetadata(xml)));
  }

}
