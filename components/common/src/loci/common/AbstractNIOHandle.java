//
// AbstractNIOHandle.java
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

import java.io.IOException;

/**
 * A wrapper for buffered NIO logic that implements the IRandomAccess interface.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/AbstractNIOHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/AbstractNIOHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccess
 * @see java.io.RandomAccessFile
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public abstract class AbstractNIOHandle implements IRandomAccess {

  /** Error message to be used when instantiating an EOFException. */
  protected static final String EOF_ERROR_MSG =
    "Attempting to read beyond end of file.";

  //-- Constants --

  // -- Fields --

  // -- Constructors --

  // -- AbstractNIOHandle methods --

  /**
   * Ensures that the file mode is either "r" or "rw".
   * @param mode Mode to validate.
   * @throws IllegalArgumentException If an illegal mode is passed.
   */
  protected void validateMode(String mode) {
    if (!(mode.equals("r") || mode.equals("rw"))) {
      throw new IllegalArgumentException(
        String.format("%s mode not in supported modes ('r', 'rw')", mode));
    }
  }

  /**
   * Ensures that the handle has the correct length to be written to and
   * extends it as required.
   * @param writeLength Number of bytes to write.
   * @return <code>true</code> if the buffer has not required an extension.
   * <code>false</code> otherwise.
   * @throws IOException If there is an error changing the handle's length.
   */
  protected boolean validateLength(int writeLength) throws IOException {
    if (getFilePointer() + writeLength > length()) {
      setLength(getFilePointer() + writeLength);
      return false;
    }
    return true;
  }

  /**
   * Sets the new length of the handle.
   * @param length New length.
   * @throws IOException If there is an error changing the handle's length.
   */
  protected abstract void setLength(long length) throws IOException;
}
