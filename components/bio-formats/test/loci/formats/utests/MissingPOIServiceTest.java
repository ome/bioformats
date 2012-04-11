
package loci.formats.utests;

import static org.testng.AssertJUnit.assertNotNull;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.POIService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/MissingPOIServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/MissingPOIServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class MissingPOIServiceTest {

  private ServiceFactory sf;

  @BeforeMethod
  public void setUp() throws DependencyException {
    sf = new ServiceFactory();
  }

  @Test(expectedExceptions={DependencyException.class})
  public void testInstantiate() throws DependencyException {
    POIService service = sf.getInstance(POIService.class);
    assertNotNull(service);
  }

}
