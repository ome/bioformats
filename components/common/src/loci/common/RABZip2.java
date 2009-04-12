//
// RABZip2.java
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

package loci.common;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * CompressedRandomAccess implementation for reading from BZip2-compressed files
 * or byte arrays.  Instances of RABZip2 are read-only.
 *
 * @see CompressedRandomAccess
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/RABZip2.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/RABZip2.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class RABZip2 extends CompressedRandomAccess {

  // -- Fields --

  private String file;

  // -- Constructor --

  public RABZip2(String file) throws IOException {
    super();
    this.file = file;
    if (!isBZip2File(file)) {
      throw new IOException(file + " is not a BZip2 file.");
    }

    BufferedInputStream bis =
      new BufferedInputStream(new FileInputStream(file), MAX_OVERHEAD);
    bis.skip(2);
    stream = new DataInputStream(new CBZip2InputStream(bis));

    length = 0;
    while (true) {
      int skip = stream.skipBytes(1024);
      if (skip <= 0) break;
      length += skip;
    }

    bis = new BufferedInputStream(new FileInputStream(file), MAX_OVERHEAD);
    bis.skip(2);
    stream = new DataInputStream(new CBZip2InputStream(bis));
  }

  // -- RABZip2 API methods --

  /** Returns true if the given filename is a BZip2 file. */
  public static boolean isBZip2File(String file) {
    return file.toLowerCase().endsWith(".bz2");
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#seek(long) */
  public void seek(long pos) throws IOException {
    long oldFP = fp;
    fp = pos;
    if (fp > oldFP) {
      long diff = fp - oldFP;
      stream.skipBytes((int) diff);
    }
    else if (fp < oldFP) {
      stream.close();

      BufferedInputStream bis =
        new BufferedInputStream(new FileInputStream(file), MAX_OVERHEAD);
      bis.skip(2);
      stream = new DataInputStream(new CBZip2InputStream(bis));

      stream.skipBytes((int) fp);
    }
  }

}
