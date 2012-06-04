/*
 * #%L
 * OME database I/O package for communicating with OME and OMERO servers.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.ome.utests;

import static org.testng.AssertJUnit.*;

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.ome.io.services.OMEReaderWriterService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/test/loci/ome/utests/OMEReaderWriterServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/test/loci/ome/utests/OMEReaderWriterServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class OMEReaderWriterServiceTest {

  private OMEReaderWriterService service;

  @BeforeMethod
  public void setUp() throws DependencyException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEReaderWriterService.class);
  }

  @Test
  public void testNewOMEReader() {
    IFormatReader reader = service.newOMEReader();
    assertNotNull(reader);
  }

  @Test
  public void testNewOMEROReader() {
    IFormatReader reader = service.newOMEROReader();
    assertNotNull(reader);
  }

  @Test
  public void testNewOMEWriter()
    throws IOException, ServiceException {
    IFormatWriter writer = service.newOMEWriter();
    assertNotNull(writer);
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testNewOMEROWriter() throws IOException, ServiceException {
    IFormatWriter writer = service.newOMEROWriter();
    assertNotNull(writer);
  }

}
