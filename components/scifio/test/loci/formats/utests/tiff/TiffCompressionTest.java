
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;
import loci.common.enumeration.EnumException;
import loci.formats.tiff.TiffCompression;

import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/TiffCompressionTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/TiffCompressionTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class TiffCompressionTest {

  @Test
  public void testLookupUncompressed() {
    TiffCompression pi = TiffCompression.get(1);
    assertEquals(TiffCompression.UNCOMPRESSED, pi);
  }
  
  @Test(expectedExceptions={ EnumException.class })
  public void testUnknownCode() {
    TiffCompression.get(-1);
  }
}
