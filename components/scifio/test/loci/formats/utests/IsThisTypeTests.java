
package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import loci.formats.ImageReader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for checking the speed and accuracy of isThisType(String, boolean) in
 * IFormatReader.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/IsThisTypeTests.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/IsThisTypeTests.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class IsThisTypeTests {

  // The test file is 'C1979091.h5' from:
  // http://www.hdfgroup.org/training/other-ex5/sample-programs/convert/Conversion.html
  private static final String TEST_FILE = "test.h5";
  private static final long TIMEOUT = 2000;

  private ImageReader openReader;
  private ImageReader noOpenReader;

  @BeforeMethod
  public void setUp() {
    openReader = new ImageReader();
    noOpenReader = new ImageReader();
    noOpenReader.setAllowOpenFiles(false);
  }

  @Test
  public void testAccuracy() {
    boolean openReaderIsValid = openReader.isThisType(TEST_FILE);
    boolean noOpenReaderIsValid = noOpenReader.isThisType(TEST_FILE);

    assertEquals(openReaderIsValid, noOpenReaderIsValid);
    assertEquals(openReaderIsValid, false);
  }

  @Test
  public void testTypeCheckingSpeed() {
    long t0 = System.currentTimeMillis();
    openReader.isThisType(TEST_FILE);
    long t1 = System.currentTimeMillis();
    noOpenReader.isThisType(TEST_FILE);
    long t2 = System.currentTimeMillis();

    assertTrue((t1 - t0) < TIMEOUT);
    assertTrue((t2 - t1) < TIMEOUT);
  }

}
