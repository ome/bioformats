/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * StreamHandle implementation for reading from BZip2-compressed files
 * or byte arrays.  Instances of BZip2Handle are read-only.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/BZip2Handle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/BZip2Handle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see StreamHandle
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class BZip2Handle extends StreamHandle {

  // -- Constructor --

  /**
   * Construct a new BZip2Handle corresponding to the given file.
   *
   * @throws HandleException if the given file is not a BZip2 file.
   */
  public BZip2Handle(String file) throws IOException {
    super();
    this.file = file;
    if (!isBZip2File(file)) {
      throw new HandleException(file + " is not a BZip2 file.");
    }

    resetStream();

    length = 0;
    while (true) {
      int skip = stream.skipBytes(1024);
      if (skip <= 0) {
        break;
      }
      length += skip;
    }

    resetStream();
  }

  // -- BZip2Handle API methods --

  /** Returns true if the given filename is a BZip2 file. */
  public static boolean isBZip2File(String file) throws IOException {
    if (!file.toLowerCase().endsWith(".bz2")) {
      return false;
    }

    FileInputStream s = new FileInputStream(file);
    byte[] b = new byte[2];
    s.read(b);
    s.close();
    return new String(b, Constants.ENCODING).equals("BZ");
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  protected void resetStream() throws IOException {
    BufferedInputStream bis = new BufferedInputStream(
      new FileInputStream(file), RandomAccessInputStream.MAX_OVERHEAD);
    int skipped = 0;
    while (skipped < 2) {
      skipped += bis.skip(2 - skipped);
    }
    stream = new DataInputStream(new CBZip2InputStream(bis));
  }


}
