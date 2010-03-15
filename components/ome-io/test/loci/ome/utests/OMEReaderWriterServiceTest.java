//
// OMEReaderWriterServiceTest.java
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

package loci.ome.utests;

import static org.testng.AssertJUnit.*;

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.services.OMEReaderWriterService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/OMEReaderWriterServiceTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/test/loci/formats/utests/OMEReaderWriterServiceTest.java">SVN</a></dd></dl>
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
