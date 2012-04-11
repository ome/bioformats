
package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.IOException;

import loci.common.BZip2Handle;
import loci.common.GZipHandle;
import loci.common.ZipHandle;

import org.testng.annotations.Test;

/**
 * Tests compressed IRandomAccess implementation type detection.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/TypeDetectionTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/TypeDetectionTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAcess
 */
public class TypeDetectionTest {

  @Test
  public void testBZip2TypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".bz2");
    invalidFile.deleteOnExit();
    assertEquals(BZip2Handle.isBZip2File(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testGZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".gz");
    invalidFile.deleteOnExit();
    assertEquals(GZipHandle.isGZipFile(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".zip");
    invalidFile.deleteOnExit();
    assertEquals(ZipHandle.isZipFile(invalidFile.getAbsolutePath()), false);
  }
}
