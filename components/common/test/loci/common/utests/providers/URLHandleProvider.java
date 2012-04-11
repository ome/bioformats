
package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.IRandomAccess;
import loci.common.URLHandle;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.URLHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/URLHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/URLHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.URLHandle
 */
class URLHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File f = File.createTempFile("url", ".dat");
    f.deleteOnExit();
    FileOutputStream out = new FileOutputStream(f);
    out.write(page);
    out.close();

    return new URLHandle(f.toURL().toString());
  }

}
