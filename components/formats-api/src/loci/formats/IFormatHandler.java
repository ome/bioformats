/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
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

package loci.formats;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for all biological file format readers and writers.
 */
public interface IFormatHandler extends Closeable {

  /** Checks if the given string is a valid filename for this file format. */
  boolean isThisType(String name);

  /** Gets the name of this file format. */
  String getFormat();

  /** Gets the default file suffixes for this file format. */
  String[] getSuffixes();

  /**
   * Returns the native data type of image planes for this reader, as returned
   * by {@link IFormatReader#openPlane} or {@link IFormatWriter#savePlane}.
   * For most readers this type will be a byte array; however, some readers
   * call external APIs that work with other types such as
   * {@link java.awt.image.BufferedImage}.
   */
  Class<?> getNativeDataType();

  /** Sets the current file name. */
  void setId(String id) throws FormatException, IOException;
  
  /** Specifies whether or not to validate files when reading. */
  void setValidate(boolean validate);

  /** Returns true if files should be validated when read.*/
  boolean isValidate();

}
