
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/OMEXMLServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/OMEXMLServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
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
    assertEquals("2011-06", service.getLatestVersion());
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
    assertEquals("2011-06",
      service.getOMEXMLVersion(service.createOMEXMLMetadata(xml)));
  }

  @Test
  public void getOMEXML() throws ServiceException {
    assertNotNull(service.getOMEXML(service.createOMEXMLMetadata(xml)));
  }

}
