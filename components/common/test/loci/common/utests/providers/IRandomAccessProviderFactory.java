
package loci.common.utests.providers;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for providing instances of IRandomAccessProvider.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 */
public class IRandomAccessProviderFactory {

  private static final Map<String, IRandomAccessProvider> providers =
    new HashMap<String, IRandomAccessProvider>();

  static {
    providers.put("NewByteArrayHandle", new NewByteArrayHandleProvider());
    providers.put("ExistingByteArrayHandle",
      new ExistingByteArrayHandleProvider());
    providers.put("ByteArrayHandle", new ByteArrayHandleProvider());
    providers.put("BZip2Handle", new BZip2HandleProvider());
    providers.put("GZipHandle", new GZipHandleProvider());
    providers.put("NIOFileHandle", new NIOFileHandleProvider());
    providers.put("URLHandle", new URLHandleProvider());
    providers.put("ZipHandle", new ZipHandleProvider());
  }

  public IRandomAccessProvider getInstance(String provider) {
    return providers.get(provider);
  }

}
