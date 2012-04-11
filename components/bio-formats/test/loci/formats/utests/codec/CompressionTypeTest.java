
package loci.formats.utests.codec;

import static org.testng.AssertJUnit.assertEquals;
import loci.common.enumeration.EnumException;
import loci.formats.codec.CompressionType;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/codec/CompressionTypeTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/codec/CompressionTypeTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CompressionTypeTest {

  @Test
  public void testLookupName() {
    CompressionType type = CompressionType.get(1);
    assertEquals(CompressionType.UNCOMPRESSED, type);
  }

  @Test(expectedExceptions={ EnumException.class })
  public void testUnknownCode() {
    CompressionType.get(-1);
  }
  
}
