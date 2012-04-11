
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;
import loci.common.enumeration.EnumException;
import loci.formats.tiff.PhotoInterp;

import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/PhotoInterpTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/PhotoInterpTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class PhotoInterpTest {

  @Test
  public void testLookupBlackIsZero() {
    PhotoInterp pi = PhotoInterp.get(1);
    assertEquals(PhotoInterp.BLACK_IS_ZERO, pi);
  }
  
  @Test(expectedExceptions={ EnumException.class })
  public void testUnknownCode() {
    PhotoInterp.get(-1);
  }
}
