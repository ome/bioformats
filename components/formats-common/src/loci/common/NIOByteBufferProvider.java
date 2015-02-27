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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a facade to byte buffer allocation that enables
 * <code>FileChannel.map()</code> usage on platforms where it's unlikely to
 * give us problems and heap allocation where it is. References:
 * <ul>
 *   <li>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5092131</li>
 *   <li>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6417205</li>
 * </ul>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/NIOByteBufferProvider.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/NIOByteBufferProvider.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class NIOByteBufferProvider {

  // -- Constants --

  /** The minimum Java version we know is safe for memory mapped I/O. */
  public static final int MINIMUM_JAVA_VERSION = 6;

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(NIOByteBufferProvider.class);

  // -- Fields --

  /** Whether or not we are to use memory mapped I/O. */
  private static boolean useMappedByteBuffer = false;

  /** File channel to allocate or map data from. */
  private FileChannel channel;

  /** If we are to use memory mapped I/O, the map mode. */
  private MapMode mapMode;

  static {
    String mapping = System.getProperty("mappedBuffers");
    useMappedByteBuffer = Boolean.parseBoolean(mapping);
    LOGGER.debug("Using mapped byte buffer? {}", useMappedByteBuffer);
  }

  // -- Constructors --

  /**
   * Default constructor.
   * @param channel File channel to allocate or map byte buffers from.
   * @param mapMode The map mode. Required but only used if memory mapped I/O
   * is to occur.
   */
  public NIOByteBufferProvider(FileChannel channel, MapMode mapMode) {
    this.channel = channel;
    this.mapMode = mapMode;
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
    if (useMappedByteBuffer) {
      return allocateMappedByteBuffer(bufferStartPosition, newSize);
    }
    return allocateDirect(bufferStartPosition, newSize);
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
    ByteBuffer buffer = ByteBuffer.allocate(newSize);
    channel.read(buffer, bufferStartPosition);
    return buffer;
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
    long bufferStartPosition, int newSize) throws IOException
  {
    return channel.map(mapMode, bufferStartPosition, newSize);
  }
}
