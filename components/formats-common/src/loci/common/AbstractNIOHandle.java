/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.common;

import java.io.IOException;

/**
 * A wrapper for buffered NIO logic that implements the IRandomAccess interface.
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
