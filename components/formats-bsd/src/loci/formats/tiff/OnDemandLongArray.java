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

package loci.formats.tiff;

import java.io.IOException;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;

/**
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class OnDemandLongArray {

  private transient RandomAccessInputStream stream;
  private int size;
  private long start;

  public OnDemandLongArray(RandomAccessInputStream in) throws IOException {
    stream = in;
    start = stream.getFilePointer();
  }

  public void setSize(int size) {
    this.size = size;
  }

  public RandomAccessInputStream getStream() {
    return stream;
  }

  public void setStream(RandomAccessInputStream in) {
    stream = in;
  }

  public long get(int index) throws IOException {
    long fp = stream.getFilePointer();
    stream.seek(start + index * 8);
    long value = stream.readLong();
    stream.seek(fp);
    return value;
  }

  public long size() {
    return size;
  }

  public long[] toArray() throws IOException {
    long fp = stream.getFilePointer();
    stream.seek(start);
    byte[] rawBytes = new byte[size * 8];
    stream.readFully(rawBytes);
    stream.seek(fp);
    return (long[]) DataTools.makeDataArray(rawBytes, 8, false, stream.isLittleEndian());
  }

  public void close() throws IOException {
    if (stream != null) {
      stream.close();
    }
    stream = null;
    size = 0;
    start = 0;
  }

}
