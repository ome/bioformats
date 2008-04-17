//
// IRandomAccess.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;

/**
 * Interface for random access into structures (e.g., files or arrays).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/IRandomAccess.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/IRandomAccess.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface IRandomAccess extends DataInput, DataOutput {

  /**
   * Closes this random access file stream and releases
   * any system resources associated with the stream.
   */
  void close() throws IOException;

  /** Returns the current offset in this file. */
  long getFilePointer() throws IOException;

  /** Returns the length of this file. */
  long length() throws IOException;

  /** Reads a byte of data from this file. */
  int read() throws IOException;

  /**
   * Reads up to b.length bytes of data
   * from this file into an array of bytes.
   */
  int read(byte[] b) throws IOException;

  /** Reads up to len bytes of data from this file into an array of bytes. */
  int read(byte[] b, int off, int len) throws IOException;

  /**
   * Sets the file-pointer offset, measured from the beginning
   * of this file, at which the next read or write occurs.
   */
  void seek(long pos) throws IOException;

  /** Sets the length of this file. */
  void setLength(long newLength) throws IOException;

}
