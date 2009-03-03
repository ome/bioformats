package mdbtools.publicapi;

import java.io.IOException;

// all reads from an mdb file will come through here
public interface RandomAccess
{
  public void close()
    throws IOException;

  public long length()
    throws IOException;

  public void seek(long l)
    throws IOException;

  public long read(byte[] ba, int offset, int length)
    throws IOException;
}
