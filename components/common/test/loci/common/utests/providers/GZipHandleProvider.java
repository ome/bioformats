
package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import loci.common.GZipHandle;
import loci.common.IRandomAccess;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.GZipHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/GZipHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/GZipHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.GZipHandle
 */
class GZipHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File pageFile = File.createTempFile("page", ".gz");
    pageFile.deleteOnExit();
    OutputStream out = new GZIPOutputStream(new FileOutputStream(pageFile));
    out.write(page);
    out.close();

    return new GZipHandle(pageFile.getAbsolutePath());
  }

}
