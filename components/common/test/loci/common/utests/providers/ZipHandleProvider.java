
package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import loci.common.IRandomAccess;
import loci.common.ZipHandle;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.ZipHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/ZipHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/ZipHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.ZipHandle
 */
class ZipHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File pageFile = File.createTempFile("page", ".zip");
    pageFile.deleteOnExit();
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(pageFile));
    out.putNextEntry(new ZipEntry(pageFile.getName()));
    out.write(page);
    out.close();

    return new ZipHandle(pageFile.getAbsolutePath());
  }

}
