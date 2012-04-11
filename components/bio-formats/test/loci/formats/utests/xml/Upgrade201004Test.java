
package loci.formats.utests.xml;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.io.InputStream;

import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.CommentAnnotation;
import ome.xml.model.OME;
import ome.xml.model.StructuredAnnotations;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/Upgrade201004Test.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/Upgrade201004Test.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class Upgrade201004Test {

  private static final String XML_FILE = "2010-04.ome";

  private OMEXMLService service;
  private String xml;
  private OMEXMLMetadata metadata;
  private OME ome;

  @BeforeMethod
  public void setUp() throws Exception {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);

    InputStream s = Upgrade201004Test.class.getResourceAsStream(XML_FILE);
    byte[] b = new byte[s.available()];
    s.read(b);
    s.close();

    xml = new String(b);
    metadata = service.createOMEXMLMetadata(xml);
    ome = (OME) metadata.getRoot();
  }

  @Test
  public void getOMEXMLVersion() throws ServiceException {
    assertEquals("2011-06", service.getOMEXMLVersion(metadata));
  }

  @Test
  public void validateUpgrade() throws ServiceException {
    assertEquals(1, ome.sizeOfImageList());
    // StringAnnotation --> CommentAnnotation
    StructuredAnnotations structuredAnnotations =
      ome.getStructuredAnnotations();
    assertNotNull(structuredAnnotations);
    assertEquals(1, structuredAnnotations.sizeOfCommentAnnotationList());
    CommentAnnotation commentAnnotation =
      structuredAnnotations.getCommentAnnotation(0);
    assertEquals("StringAnnotation:0", commentAnnotation.getID());
    assertEquals("Transform", commentAnnotation.getNamespace());
    assertEquals("Foobar", commentAnnotation.getValue());
  }

}
