//
// ZipHandle.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * StreamHandle implementation for reading from Zip-compressed files
 * or byte arrays.  Instances of ZipHandle are read-only.
 *
 * @see StreamHandle
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/ZipHandle.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/ZipHandle.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ZipHandle extends StreamHandle {

  // -- Fields --

  private ZipFile zip;
  private ZipEntry entry;

  // -- Constructor --

  /**
   * Constructs a new ZipHandle corresponding to the given file.
   *
   * @throws HandleException if:
   *   <li>The given file is not a Zip file.<br>
   *   <li>The Zip file contains more than one entry.<br>
   */
  public ZipHandle(String file) throws IOException {
    super();
    this.file = file;
    if (!isZipFile(file)) {
      throw new HandleException(file + " is not a Zip file.");
    }
    zip = new ZipFile(file);

    // strip off .zip extension and directory prefix
    String innerFile = file.substring(0, file.length() - 4);
    int slash = innerFile.lastIndexOf(File.separator);
    if (slash < 0) slash = innerFile.lastIndexOf("/");
    if (slash >= 0) innerFile = innerFile.substring(slash + 1);

    // look for Zip entry with same prefix as the original Zip file
    entry = null;
    Enumeration<? extends ZipEntry> e = zip.entries();
    while (e.hasMoreElements()) {
      ZipEntry ze = e.nextElement();
      if (ze.getName().startsWith(innerFile)) {
        // found entry with matching name
        entry = ze;
        break;
      }
      else if (entry == null) entry = ze; // default to first entry
    }
    if (entry == null) {
      throw new HandleException("Zip file '" + file + "' has no entries");
    }

    length = entry.getSize();
    resetStream();
  }

  // -- ZipHandle API methods --

  /** Returns true if the given filename is a Zip file. */
  public static boolean isZipFile(String file) throws IOException {
    if (!file.toLowerCase().endsWith(".zip")) return false;

    FileInputStream f = new FileInputStream(file);
    byte[] b = new byte[2];
    f.read(b);
    f.close();
    return new String(b).equals("PK");
  }

  /** Get the name of the first entry. */
  public String getEntryName() {
    return entry.getName();
  }

  /** Returns the DataInputStream corresponding to the first entry. */
  public DataInputStream getInputStream() {
    return stream;
  }

  /** Returns the number of entries. */
  public int getEntryCount() {
    return zip.size();
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    super.close();
    zip = null;
    entry = null;
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    if (stream != null) stream.close();
    stream = new DataInputStream(new BufferedInputStream(
     zip.getInputStream(entry), RandomAccessInputStream.MAX_OVERHEAD));
  }

}
