//
// RAZip.java
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
 * CompressedRandomAccess implementation for reading from Zip-compressed files
 * or byte arrays.  Instances of RAZip are read-only.
 *
 * @see CompressedRandomAccess
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/RAZip.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/RAZip.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class RAZip extends CompressedRandomAccess {

  // -- Fields --

  private ZipFile zip;
  private ZipEntry entry;

  // -- Constructor --

  public RAZip(String file) throws IOException {
    super();
    if (!isZipFile(file)) {
      throw new IOException(file + " is not a Zip file.");
    }
    zip = new ZipFile(file);

    // strip off .zip extension and directory prefix
    String innerFile = file.substring(0, file.length() - 4);
    int slash = innerFile.lastIndexOf(File.separator);
    if (slash < 0) slash = innerFile.lastIndexOf("/");
    if (slash >= 0) innerFile = innerFile.substring(slash + 1);

    // look for Zip entry with same prefix as the original Zip file
    entry = null;
    Enumeration e = zip.entries();
    while (e.hasMoreElements()) {
      ZipEntry ze = (ZipEntry) e.nextElement();
      if (ze.getName().startsWith(innerFile)) {
        // found entry with matching name
        entry = ze;
        break;
      }
      else if (entry == null) entry = ze; // default to first entry
    }
    if (entry == null) {
      throw new IOException("Zip file '" + file + "' has no entries");
    }

    length = entry.getSize();
    stream = new DataInputStream(new BufferedInputStream(
      zip.getInputStream(entry), RandomAccessStream.MAX_OVERHEAD));
  }

  // -- RAZip API methods --

  /** Returns true if the given filename is a Zip file. */
  public static boolean isZipFile(String file) {
    return file.toLowerCase().endsWith(".zip");
  }

  /** Get the name of the first entry. */
  public String getEntryName() {
    return entry.getName();
  }

  /** Returns the DataInputStream corresponding to the first entry. */
  public DataInputStream getInputStream() {
    return stream;
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    super.close();
    zip = null;
    entry = null;
  }

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
      stream = new DataInputStream(new BufferedInputStream(
        zip.getInputStream(entry), RandomAccessStream.MAX_OVERHEAD));
      stream.skipBytes((int) fp);
    }
  }

}
