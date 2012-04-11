
package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.BZip2Handle;
import loci.common.IRandomAccess;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.BZip2Handle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/BZip2HandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/BZip2HandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.BZip2Handle
 */
class BZip2HandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File pageFile = File.createTempFile("page", ".dat");
    FileOutputStream out = new FileOutputStream(pageFile);
    out.write(page);
    out.close();

    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(new String[] {"bzip2", pageFile.getAbsolutePath()});
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    pageFile = new File(pageFile.getAbsolutePath() + ".bz2");
    return new BZip2Handle(pageFile.getAbsolutePath());
  }

}
