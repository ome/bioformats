//
// ByteArrayHandleProvider.java
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

import java.io.IOException;
import java.nio.ByteOrder;

import loci.common.ByteArrayHandle;
import loci.common.IRandomAccess;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.ByteArrayHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/providers/ByteArrayHandleProvider.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/providers/ByteArrayHandleProvider.java">SVN</a></dd></dl>
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
