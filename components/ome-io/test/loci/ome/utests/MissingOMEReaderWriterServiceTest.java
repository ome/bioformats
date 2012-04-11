
package loci.ome.utests;

import static org.testng.AssertJUnit.assertNotNull;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.ome.io.services.OMEReaderWriterService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/test/loci/ome/utests/MissingOMEReaderWriterServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/test/loci/ome/utests/MissingOMEReaderWriterServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class MissingOMEReaderWriterServiceTest {

  private ServiceFactory sf;

  @BeforeMethod
  public void setUp() throws DependencyException {
    sf = new ServiceFactory();
  }

  @Test(expectedExceptions={DependencyException.class})
  public void testInstantiate() throws DependencyException {
    OMEReaderWriterService service = 
      sf.getInstance(OMEReaderWriterService.class);
    assertNotNull(service);
  }

}
