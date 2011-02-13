//
// GZipHandle.java
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * StreamHandle implementation for reading from gzip-compressed files
 * or byte arrays.  Instances of GZipHandle are read-only.
 *
 * @see StreamHandle
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/GZipHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/GZipHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class GZipHandle extends StreamHandle {

  // -- Constructor --

  /**
   * Construct a new GZipHandle for the given file.
   *
   * @throws HandleException if the given file name is not a GZip file.
   */
  public GZipHandle(String file) throws IOException {
    super();
    this.file = file;
    if (!isGZipFile(file)) {
      throw new HandleException(file + " is not a gzip file.");
    }

    resetStream();

    length = 0;
    while (true) {
      int skip = stream.skipBytes(1024);
      if (skip <= 0) break;
      length += skip;
    }

    resetStream();
  }

  // -- GZipHandle API methods --

  /** Returns true if the given filename is a gzip file. */
  public static boolean isGZipFile(String file) throws IOException {
    if (!file.toLowerCase().endsWith(".gz")) return false;

    FileInputStream s = new FileInputStream(file);
    byte[] b = new byte[2];
    s.read(b);
    s.close();
    return DataTools.bytesToInt(b, true) == GZIPInputStream.GZIP_MAGIC;
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    if (stream != null) stream.close();
    BufferedInputStream bis = new BufferedInputStream(
      new FileInputStream(file), RandomAccessInputStream.MAX_OVERHEAD);
    stream = new DataInputStream(new GZIPInputStream(bis));
  }

}
