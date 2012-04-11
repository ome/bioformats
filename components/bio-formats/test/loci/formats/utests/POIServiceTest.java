
package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.POIService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/POIServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/POIServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class POIServiceTest {

  private POIService service;

  private static final String TEST_XLS = "test.xls";

  private static final String WORKBOOK_DOCUMENT = "Root Entry/Workbook";

  private static final int WORKBOOK_LENGTH = 9604;

  @BeforeMethod
  public void setUp() throws DependencyException, IOException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(POIService.class);
    URL file = this.getClass().getResource(TEST_XLS);
    service.initialize(file.getPath());
  }

  @Test
  public void testDocuments() {
    Vector<String> documents = service.getDocumentList();
    for (String document : documents) {
      if (document.equals(WORKBOOK_DOCUMENT)) {
        return;
      }
    }
    fail("Unable to find document: " + WORKBOOK_DOCUMENT);
  }

  @Test
  public void testWorkbookDocumentAsStream() throws IOException {
    RandomAccessInputStream stream = 
      service.getDocumentStream(WORKBOOK_DOCUMENT); 
    assertNotNull(stream);
    assertEquals(WORKBOOK_LENGTH, stream.length());
  }

  @Test
  public void testWorkbookDocumentBytes() throws IOException {
    byte[] bytes = service.getDocumentBytes(WORKBOOK_DOCUMENT); 
    assertNotNull(bytes);
    assertEquals(WORKBOOK_LENGTH, bytes.length);
  }

  @Test
  public void testWorkbookFileSize() {
    assertEquals(WORKBOOK_LENGTH, service.getFileSize(WORKBOOK_DOCUMENT));
  }
}
