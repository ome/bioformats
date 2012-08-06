/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This package is based on the work done by Keiron Liddle, Aftex Software
 * <keiron@aftexsw.com> to whom the Ant project is very grateful for his
 * great code.
 */

package loci.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * Legacy delegator class for ome.scifio.io.CBZip2InputStream.
 *
 * <p>Instances of this class are not threadsafe.</p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/CBZip2InputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/CBZip2InputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CBZip2InputStream extends InputStream {

  // -- Fields --
  
  private ome.scifio.io.CBZip2InputStream cbzStream;

  // -- Constructor --

  /**
   * Constructs a new CBZip2InputStream which decompresses bytes read from
   * the specified stream.
   *
   * <p>Although BZip2 headers are marked with the magic
   * <tt>"Bz"</tt> this constructor expects the next byte in the
   * stream to be the first one after the magic.  Thus callers have
   * to skip the first two bytes. Otherwise this constructor will
   * throw an exception. </p>
   *
   * @throws IOException
   *   if the stream content is malformed or an I/O error occurs.
   * @throws NullPointerException
   *   if <tt>in == null</tt>
   */
  public CBZip2InputStream(final InputStream in) throws IOException {
    cbzStream = new ome.scifio.io.CBZip2InputStream(in);
  }
  
  // -- InputStream API --

  public int read() throws IOException {
    return cbzStream.read();
  }

  public int read(final byte[] dest, final int offs, final int len)
    throws IOException
  {
    return cbzStream.read(dest, offs, len);
  }

  public void close() throws IOException {
    cbzStream.close();
  }
  
  // -- Object delegators --
  
  @Override
  public boolean equals(Object obj) {
    return cbzStream.equals(obj);
  }

  @Override
  public int hashCode() {
    return cbzStream.hashCode();
  }
  
  @Override
  public String toString() {
    return cbzStream.toString();
  }
}

