//
// URLHandle.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

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

package loci.common;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides random access to URLs using the IRandomAccess interface.
 * Instances of URLHandle are read-only.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/URLHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/URLHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccess
 * @see StreamHandle
 * @see java.net.URLConnection
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class URLHandle extends StreamHandle {

  // -- Fields --

  /** URL of open socket */
  private String url;

  /** Socket underlying this stream */
  private URLConnection conn;

  // -- Constructors --

  /**
   * Constructs a new URLHandle using the given URL.
   */
  public URLHandle(String url) throws IOException {
    if (!url.startsWith("http") && !url.startsWith("file:")) {
      url = "http://" + url;
    }
    this.url = url;
    resetStream();
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#seek(long) */
  public void seek(long pos) throws IOException {
    if (pos < fp && pos >= mark) {
      stream.reset();
      fp = mark;
      skip(pos - fp);
    }
    else super.seek(pos);
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    conn = (new URL(url)).openConnection();
    stream = new DataInputStream(new BufferedInputStream(
      conn.getInputStream(), RandomAccessInputStream.MAX_OVERHEAD));
    fp = 0;
    mark = 0;
    length = conn.getContentLength();
    if (stream != null) stream.mark(RandomAccessInputStream.MAX_OVERHEAD);
  }

  // -- Helper methods --

  /** Skip over the given number of bytes. */
  private void skip(long bytes) throws IOException {
    while (bytes >= Integer.MAX_VALUE) {
      bytes -= skipBytes(Integer.MAX_VALUE);
    }
    int skipped = skipBytes((int) bytes);
    while (skipped < bytes) {
      int n = skipBytes((int) (bytes - skipped));
      if (n == 0) break;
      skipped += n;
    }
  }

}
