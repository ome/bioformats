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

