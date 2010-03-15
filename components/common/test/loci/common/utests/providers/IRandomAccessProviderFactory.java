//
// IRandomAccessProviderFactory.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.common.utests.providers;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for providing instances of IRandomAccessProvider.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/providers/IRandomAccessProviderFactory.java">SVN</a></dd></dl>
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
