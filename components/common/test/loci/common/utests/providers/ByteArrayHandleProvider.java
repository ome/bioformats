
package loci.common.utests.providers;

import java.io.IOException;
import java.nio.ByteOrder;

import loci.common.ByteArrayHandle;
import loci.common.IRandomAccess;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.ByteArrayHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/ByteArrayHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/ByteArrayHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.ByteArrayHandle
 */
class ByteArrayHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    IRandomAccess handle = new ByteArrayHandle();
    handle.setOrder(ByteOrder.BIG_ENDIAN);
    handle.write(page);
    handle.seek(0);
    return handle;
  }

}
