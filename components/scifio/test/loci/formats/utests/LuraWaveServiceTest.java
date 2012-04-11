
package loci.formats.utests;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.services.LuraWaveService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/LuraWaveServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/LuraWaveServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class LuraWaveServiceTest {

  private ServiceFactory sf;
  private LuraWaveService service;

  @BeforeMethod
  public void setUp() throws DependencyException {
    sf = new ServiceFactory();
  }

  @Test(expectedExceptions={DependencyException.class})
  public void testInitialize()
    throws IOException, DependencyException, ServiceException {
    service = sf.getInstance(LuraWaveService.class);
    service.initialize(new ByteArrayInputStream(new byte[0]));
  }

}
