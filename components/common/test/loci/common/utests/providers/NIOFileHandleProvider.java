
package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import loci.common.IRandomAccess;
import loci.common.NIOFileHandle;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.NIOFileHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/NIOFileHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/NIOFileHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.NIOFileHandle
 */
class NIOFileHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File pageFile = File.createTempFile("page", ".dat");
    OutputStream stream = new FileOutputStream(pageFile);
    try {
      stream.write(page);
    } finally {
      stream.close();
    }
    return new NIOFileHandle(pageFile, mode, bufferSize);
  }

}
