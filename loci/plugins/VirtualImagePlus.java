package loci.plugins;

import ij.*;
import java.io.IOException;
import java.util.Vector;
import loci.formats.IFormatReader;

public class VirtualImagePlus extends ImagePlus {

  // -- Fields --

  private IFormatReader r;

  // -- Constructor --

  public VirtualImagePlus(String title, ImageStack stack) {
    super(title, stack);
  }

  // -- VirtualImagePlus API methods --

  public void setReader(IFormatReader r) {
    this.r = r;
  }

  // -- ImagePlus API methods --

  public synchronized void setSlice(int index) {
    super.setSlice(index);

    BFVirtualStack stack = null;
    if (getStack() instanceof BFVirtualStack) {
      stack = (BFVirtualStack) getStack();
      RecordedImageProcessor proc = stack.getRecordedProcessor();
      Vector methods = stack.getMethodStack();
      if (methods != null) {
        proc.applyMethodStack(methods);
      }
      setProcessor(getTitle(), proc);
    }
  }

  public void close() {
    super.close();
    try {
      r.close();
    }
    catch (IOException e) { }
  }

}
