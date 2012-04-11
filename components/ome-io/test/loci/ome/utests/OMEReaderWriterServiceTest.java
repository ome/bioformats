
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
