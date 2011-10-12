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
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * StreamHandle implementation for reading from Zip-compressed files
 * or byte arrays.  Instances of ZipHandle are read-only.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/ZipHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/ZipHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see StreamHandle
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ZipHandle extends StreamHandle {

  // -- Fields --

  private RandomAccessInputStream in;
  private ZipInputStream zip;
  private ZipEntry entry;
  private int entryCount = 0;

  // -- Constructor --

  public ZipHandle(String file) throws IOException {
    super();
    this.file = file;
    in = openStream(file);
    zip = new ZipInputStream(in);

    // strip off .zip extension and directory prefix
    String innerFile = file.substring(0, file.length() - 4);
    int slash = innerFile.lastIndexOf(File.separator);
    if (slash < 0) slash = innerFile.lastIndexOf("/");
    if (slash >= 0) innerFile = innerFile.substring(slash + 1);

    // look for Zip entry with same prefix as the original Zip file
    entry = null;

    while (true) {
      ZipEntry ze = zip.getNextEntry();
      if (ze == null) break;
      entryCount++;
    }
    resetStream();

    while (true) {
      ZipEntry ze = zip.getNextEntry();
      if (ze == null) break;
      if (entry == null) entry = ze;
      if (ze.getName().startsWith(innerFile)) {
        // found entry with matching name
        entry = ze;
        break;
      }
    }

    resetStream();
    populateLength();
  }

  /**
   * Constructs a new ZipHandle corresponding to the given entry of the
   * specified Zip file.
   *
   * @throws HandleException if the given file is not a Zip file.
   */
  public ZipHandle(String file, ZipEntry entry) throws IOException {
    super();
    this.file = file;
    in = openStream(file);
    zip = new ZipInputStream(in);
    while (!entry.getName().equals(zip.getNextEntry().getName()));
    entryCount = 1;
    this.entry = entry;
    resetStream();
    populateLength();
  }

  // -- ZipHandle API methods --

  /** Returns true if the given filename is a Zip file. */
  public static boolean isZipFile(String file) throws IOException {
    if (!file.toLowerCase().endsWith(".zip")) return false;

    IRandomAccess handle = getHandle(file);
    byte[] b = new byte[2];
    if (handle.length() >= 2) {
      handle.read(b);
    }
    handle.close();
    return new String(b, Constants.ENCODING).equals("PK");
  }

  /** Get the name of the backing Zip entry. */
  public String getEntryName() {
    return entry.getName();
  }

  /** Returns the DataInputStream corresponding to the backing Zip entry. */
  public DataInputStream getInputStream() {
    return stream;
  }

  /** Returns the number of entries. */
  public int getEntryCount() {
    return entryCount;
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    if (!Location.getIdMap().containsValue(this)) {
      super.close();
      zip = null;
      entry = null;
      if (in != null) in.close();
      in = null;
      entryCount = 0;
    }
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    if (stream != null) stream.close();
    if (in != null) {
      in.close();
      in = openStream(file);
    }
    if (zip != null) zip.close();
    zip = new ZipInputStream(in);
    if (entry != null) {
      while (!entry.getName().equals(zip.getNextEntry().getName()));
    }
    stream = new DataInputStream(new BufferedInputStream(
      zip, RandomAccessInputStream.MAX_OVERHEAD * 10));
    stream.mark(RandomAccessInputStream.MAX_OVERHEAD * 10);
  }

  // -- Helper methods --

  private void populateLength() throws IOException {
    length = -1;
    while (stream.available() > 0) {
      stream.skip(1);
      length++;
    }
    resetStream();
  }

  private static IRandomAccess getHandle(String file) throws IOException {
    return Location.getHandle(file, false, false);
  }

  private static RandomAccessInputStream openStream(String file)
    throws IOException
  {
    return new RandomAccessInputStream(getHandle(file), file);
  }

}
