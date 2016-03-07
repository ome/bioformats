/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.examples;

import mdbtools.publicapi.RandomAccess;

import java.io.IOException;

// allow an mdb file to be used from memory
public class MemoryRandomAccess implements RandomAccess
{
  private byte[] data;
  private long position;

  public MemoryRandomAccess(byte[] data)
  {
    this.data = data;
    this.position = 0;
  }

  public void close()
    throws IOException
  {
    // no op
  }

  public long length()
    throws IOException
  {
    return data.length;
  }


  public void seek(long l)
    throws IOException
  {
    position = l;
  }

  public long read(byte[] ba, int offset, int length)
    throws IOException
  {
    if (position == data.length)
      return -1;
    // check if will go past end
    if (position + length > data.length)
      length = length - (int)position;
    System.arraycopy(data,(int)position,ba,offset,length);
    position += length;
    return length;
  }
}

