/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common.utests.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.BZip2Handle;
import loci.common.IRandomAccess;
import loci.common.NIOFileHandle;

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
    pageFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(pageFile);
    out.write(page);
    out.close();

    try {
      Runtime rt = Runtime.getRuntime();
      Process p = rt.exec(new String[] {"bzip2", pageFile.getAbsolutePath()});
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      pageFile = new File(pageFile.getAbsolutePath() + ".bz2");
      pageFile.deleteOnExit();
      return new BZip2Handle(pageFile.getAbsolutePath());
    }
    catch (IOException e) {
      // bzip2 is likely not installed; this is typically the case on Windows
    }

    return new NIOFileHandle(pageFile, "r");
  }

}
