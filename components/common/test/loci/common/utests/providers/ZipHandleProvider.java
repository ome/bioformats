//
// ZipHandleProvider.java
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import loci.common.IRandomAccess;
import loci.common.ZipHandle;

/**
 * Implementation of IRandomAccessProvider that produces instances of
 * loci.common.ZipHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/providers/ZipHandleProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/providers/ZipHandleProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccessProvider
 * @see loci.common.ZipHandle
 */
class ZipHandleProvider implements IRandomAccessProvider {

  public IRandomAccess createMock(
      byte[] page, String mode, int bufferSize) throws IOException {
    File pageFile = File.createTempFile("page", ".zip");
    pageFile.deleteOnExit();
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(pageFile));
    out.putNextEntry(new ZipEntry(pageFile.getName()));
    out.write(page);
    out.close();

    return new ZipHandle(pageFile.getAbsolutePath());
  }

}
