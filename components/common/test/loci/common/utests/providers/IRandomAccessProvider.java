
package loci.common.utests.providers;

import java.io.IOException;

import loci.common.IRandomAccess;

/**
 * Interface for all providers of IRandomAccess objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/IRandomAccessProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/IRandomAccessProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 */
public interface IRandomAccessProvider {

  IRandomAccess createMock(byte[] page, String mode, int bufferSize)
    throws IOException;

}
