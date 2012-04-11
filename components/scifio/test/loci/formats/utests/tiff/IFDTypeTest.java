
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;
import loci.common.enumeration.EnumException;
import loci.formats.tiff.IFDType;

import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/IFDTypeTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/IFDTypeTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class IFDTypeTest {

  @Test
  public void testLookupByte() {
    IFDType pi = IFDType.get(1);
    assertEquals(IFDType.BYTE, pi);
  }

  @Test(expectedExceptions={ EnumException.class })
  public void testUnknownCode() {
    IFDType.get(-1);
  }
}
