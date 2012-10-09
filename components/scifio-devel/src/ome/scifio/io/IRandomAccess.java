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

package ome.scifio.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Interface for random access into structures (e.g., files or arrays).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/IRandomAccess.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/IRandomAccess.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface IRandomAccess extends DataInput, DataOutput {

  /**
   * Closes this random access stream and releases
   * any system resources associated with the stream.
   */
  void close() throws IOException;

  /** Returns the current offset in this stream. */
  long getFilePointer() throws IOException;

  /** Returns the length of this stream. */
  long length() throws IOException;

  /**
   * Returns the current order of the stream.
   * @return See above.
   */
  ByteOrder getOrder();

  /**
   * Sets the byte order of the stream.
   * @param order Order to set.
   */
  void setOrder(ByteOrder order);

  /**
   * Reads up to b.length bytes of data
   * from this stream into an array of bytes.
   *
   * @return the total number of bytes read into the buffer.
   */
  int read(byte[] b) throws IOException;

  /**
   * Reads up to len bytes of data from this stream into an array of bytes.
   *
   * @return the total number of bytes read into the buffer.
   */
  int read(byte[] b, int off, int len) throws IOException;

  /**
   * Reads up to buffer.capacity() bytes of data
   * from this stream into a ByteBuffer.
   */
  int read(ByteBuffer buffer) throws IOException;

  /**
   * Reads up to len bytes of data from this stream into a ByteBuffer.
   *
   * @return the total number of bytes read into the buffer.
   */
  int read(ByteBuffer buffer, int offset, int len) throws IOException;

  /**
   * Sets the stream pointer offset, measured from the beginning
   * of this stream, at which the next read or write occurs.
   */
  void seek(long pos) throws IOException;

  /**
   * Writes up to buffer.capacity() bytes of data from the given
   * ByteBuffer to this stream.
   */
  void write(ByteBuffer buf) throws IOException;

  /**
   * Writes up to len bytes of data from the given ByteBuffer to this
   * stream.
   */
  void write(ByteBuffer buf, int off, int len) throws IOException;

}
