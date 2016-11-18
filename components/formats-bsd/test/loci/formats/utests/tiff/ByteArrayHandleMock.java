package loci.formats.utests.tiff;

import java.io.IOException;
import java.nio.ByteBuffer;

import loci.common.ByteArrayHandle;

public class ByteArrayHandleMock extends ByteArrayHandle {
  
  private long mockCapacity;
  
  public ByteArrayHandleMock(int capacity) {
    buffer = ByteBuffer.allocate(INITIAL_LENGTH);
    buffer.limit(INITIAL_LENGTH);
    mockCapacity = capacity;
  }
  
  @Override
  public void setLength(long length) throws IOException {
    mockCapacity = length;
  }
  
  @Override
  public long length() {
    return mockCapacity;
  }
  
  @Override
  public void seek(long pos) throws IOException {
    if (pos > length()) {
      setLength(pos);
    }
    if (pos > INITIAL_LENGTH) {
      buffer.position((int) INITIAL_LENGTH);
    }
    else {
      buffer.position((int) pos);
    }
  }
  
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    mockCapacity += b.length;
  }
}
