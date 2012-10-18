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

package loci.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import loci.utils.ProtectedMethodInvoker;

/**
 * A legacy delegator class for ome.scifio.io.NIOByteBufferProvider.
 * 
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/NIOByteBufferProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/NIOByteBufferProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class NIOByteBufferProvider {

  // -- Constants --

  // -- Fields --

  private ome.scifio.io.NIOByteBufferProvider buf;
  
  private ProtectedMethodInvoker pmi = new ProtectedMethodInvoker();
  
  // -- Constructors --

  /**
   * Default constructor.
   * @param channel File channel to allocate or map byte buffers from.
   * @param mapMode The map mode. Required but only used if memory mapped I/O
   * is to occur.
   */
  public NIOByteBufferProvider(FileChannel channel, MapMode mapMode) {
    buf = new ome.scifio.io.NIOByteBufferProvider(channel, mapMode);
  }

  // -- NIOByteBufferProvider API Methods --
  
  /**
   * Allocates or maps the desired file data into memory.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly allocated or mapped NIO byte buffer.
   * @throws IOException If there is an issue mapping, aligning or allocating
   * the buffer.
   */
  public ByteBuffer allocate(long bufferStartPosition, int newSize)
    throws IOException {
    return buf.allocate(bufferStartPosition, newSize);
  }

  /**
   * Allocates memory and copies the desired file data into it.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly allocated NIO byte buffer.
   * @throws IOException If there is an issue aligning or allocating
   * the buffer.
   */
  protected ByteBuffer allocateDirect(long bufferStartPosition, int newSize)
    throws IOException {
    Class<?>[] c = new Class<?>[] {long.class, int.class};
    Object[] o = new Object[] {bufferStartPosition, newSize};
    
    try {
      return (ByteBuffer)pmi.invokeProtected(buf, "allocateDirect", c, o);
    }
    catch (InvocationTargetException e) {
      pmi.unwrapException(e, IOException.class);
      throw new IllegalStateException(e);
    }
  }

  /**
   * Memory maps the desired file data into memory.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly mapped NIO byte buffer.
   * @throws IOException If there is an issue mapping, aligning or allocating
   * the buffer.
   */
  protected ByteBuffer allocateMappedByteBuffer(
      long bufferStartPosition, int newSize) throws IOException {
    Class<?>[] c = new Class<?>[] {long.class, int.class};
    Object[] o = new Object[] {bufferStartPosition, newSize};
    
    try {
      return (ByteBuffer)pmi.invokeProtected(buf, "allocateMappedByteBuffer", c, o);
    }
    catch (InvocationTargetException e) {
      pmi.unwrapException(e, IOException.class);
      throw new IllegalStateException(e);
    }
  }
}
