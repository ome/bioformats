//
// URLHandleProvider.java
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/providers/URLHandleProvider.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/providers/URLHandleProvider.java">SVN</a></dd></dl>
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
