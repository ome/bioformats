package mdbtools.jdbc2;

import mdbtools.publicapi.RandomAccess;

import java.io.IOException;
import java.io.RandomAccessFile;

// read an mdb file from a file
public class File implements RandomAccess
{
  RandomAccessFile file;

  public File(String fileName)
    throws IOException
  {
    file = new RandomAccessFile(fileName,"r");
  }

  public void close()
    throws IOException
  {
    file.close();
  }

  public long length()
    throws IOException
  {
    return file.length();
  }

  public void seek(long l)
    throws IOException
  {
    file.seek(l);
  }

  public long read(byte[] ba, int offset, int length)
    throws IOException
  {
    return file.read(ba,offset,length);
  }
}
