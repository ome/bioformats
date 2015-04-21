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
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides random access to URLs using the IRandomAccess interface.
 * Instances of URLHandle are read-only.
 *
 * @see IRandomAccess
 * @see StreamHandle
 * @see java.net.URLConnection
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class URLHandle extends StreamHandle {

  // -- Fields --

  /** URL of open socket */
  private String url;

  /** Socket underlying this stream */
  private URLConnection conn;

  // -- Constructors --

  /**
   * Constructs a new URLHandle using the given URL.
   */
  public URLHandle(String url) throws IOException {
    if (!url.startsWith("http") && !url.startsWith("file:")) {
      url = "http://" + url;
    }
    this.url = url;
    resetStream();
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#seek(long) */
  @Override
  public void seek(long pos) throws IOException {
    if (pos < fp && pos >= mark) {
      stream.reset();
      fp = mark;
      skip(pos - fp);
    }
    else super.seek(pos);
  }

  // -- StreamHandle API methods --

  /* @see StreamHandle#resetStream() */
  @Override
  protected void resetStream() throws IOException {
    conn = (new URL(url)).openConnection();
    stream = new DataInputStream(new BufferedInputStream(
      conn.getInputStream(), RandomAccessInputStream.MAX_OVERHEAD));
    fp = 0;
    mark = 0;
    length = conn.getContentLength();
    if (stream != null) stream.mark(RandomAccessInputStream.MAX_OVERHEAD);
  }

  // -- Helper methods --

  /** Skip over the given number of bytes. */
  private void skip(long bytes) throws IOException {
    while (bytes >= Integer.MAX_VALUE) {
      bytes -= skipBytes(Integer.MAX_VALUE);
    }
    int skipped = skipBytes((int) bytes);
    while (skipped < bytes) {
      int n = skipBytes((int) (bytes - skipped));
      if (n == 0) break;
      skipped += n;
    }
  }

}
