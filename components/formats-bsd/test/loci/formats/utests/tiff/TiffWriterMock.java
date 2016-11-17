package loci.formats.utests.tiff;

import java.io.IOException;
import java.nio.ByteBuffer;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.out.TiffWriter;
import loci.formats.tiff.TiffSaver;

public class TiffWriterMock extends TiffWriter {
  
  private static final int INITIAL_CAPACITY = 1024 * 1024;
  private ByteArrayHandle handle;
  
  public TiffWriterMock() {
    super();
    handle = new ByteArrayHandle(INITIAL_CAPACITY);
  }
  
  public TiffWriterMock(String format, String[] exts) {
    super(format, exts);
    handle = new ByteArrayHandle(INITIAL_CAPACITY);
  }
  
  @Override
  protected RandomAccessOutputStream createOutputStream() throws IOException {
    return new RandomAccessOutputStream(handle);
  }
  
  @Override
  protected RandomAccessInputStream createInputStream() throws IOException {
    return new RandomAccessInputStream(handle);
  }
  
  @Override
  protected TiffSaver createTiffSaver() {
    return new TiffSaver(out, handle);
  }
  
  public void createOutputBuffer(boolean mock) throws IOException {
    if (mock) {
      handle = new ByteArrayHandleMock(INITIAL_CAPACITY);
    }
    else {
      handle = new ByteArrayHandle(INITIAL_CAPACITY);
    }
  }
  
  public void setBufferLength(long length) throws IOException {
    handle.setLength(length);
  }

}
