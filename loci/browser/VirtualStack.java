//
// VirtualStack.java
//
package loci.browser;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;

/** This class represents an array of disk-resident images. */
class VirtualStack extends ImageStack {

  // -- Constants (VirtualStack) --

  static final int INITIAL_SIZE = 100;

  // -- Fields (VirtualStack) --

  String path;
  int nSlices;
  String[] names;
  protected static final int Z_AXIS = 1;
  protected static final int T_AXIS = 2;
  private int fileIndex;  
  private int stacksize;
  private ImagePlus imp;

  // -- Constructor (VirtualStack) --

  /** Creates a new, empty virtual stack. */
  public VirtualStack(int width, int height, ColorModel cm, String path) {
    super(width, height, cm);
    this.path = path;
    names = new String[INITIAL_SIZE];
    fileIndex = -1;
    stacksize = -1;
    //IJ.log("VirtualStack: "+path);
  }


  // -- VirtualStack methods --

  /** Adds an image FILENAME to the end of the stack. */
  public void addSlice(String name) {
    if (name == null) throw new IllegalArgumentException("'name' is null!");
    nSlices++;
    //IJ.log("addSlice: "+nSlices+"  "+name);
    if (nSlices == names.length) {
      String[] tmp = new String[nSlices*2];
      System.arraycopy(names, 0, tmp, 0, nSlices);
      names = tmp;
    }
    names[nSlices-1] = name;
  }


  // -- ImageStack methods (VirtualStack) --

  /** Does nothing. */
  public void addSlice(String sliceLabel, Object pixels) {
    System.err.println("ERROR: calling addSlice(sliceLabel, pixels)");
  }

  
  public void addSlice(String sliceLabel, ImageProcessor ip) {
    System.err.println("calling VirtualStack.addSlice(sliceLabel, ip)");
    addSlice(sliceLabel);
  }

  /** Does noting. */
  public void addSlice(String sliceLabel, ImageProcessor ip, int n) {
    System.err.println("ERROR: calling addSlice(sliceLabel, ip, n)");
  }

  /** Deletes the specified slice, were 1<=n<=nslices. */
  public void deleteSlice(int n) {
    if (n<1 || n>nSlices) {
      throw new IllegalArgumentException("Argument out of range: "+n);
    }
    if (nSlices<1) return;
    for (int i=n; i<nSlices; i++) names[i-1] = names[i];
    names[nSlices-1] = null;
    nSlices--;
  }

  /** Deletes the last slice in the stack. */
  public void deleteLastSlice() {
    if (nSlices>0) deleteSlice(nSlices);
  }

  /** Returns the pixel array for the specified slice, were 1<=n<=nslices. */
  public Object getPixels(int n) {
    ImageProcessor ip = getProcessor(n);
    return ip == null ? null : ip.getPixels();
  }

  /** Assigns a pixel array to the specified slice, were 1<=n<=nslices. */
  public void setPixels(Object pixels, int n) {
    // TODO
  }

  /**
   * Returns an ImageProcessor for the specified slice,
   * were 1<=n<=nslices. Returns null if the stack is empty.
   */
  public ImageProcessor getProcessor(int n) {
    //IJ.log("getProcessor: "+n+"  "+names[n-1]);
    if (stacksize == -1 || fileIndex != (n-1) / stacksize) {
	imp = new Opener().openImage(path, names[n-1]);
	stacksize = imp.getStackSize();
	fileIndex = (n-1) / stacksize;
    }
    if (imp!=null) {
      int w = imp.getWidth();
      int h = imp.getHeight();
      int type = imp.getType();
      ColorModel cm = imp.getProcessor().getColorModel();
      imp.setSlice(n % stacksize == 0 ? stacksize : n % stacksize);
    }
    else return null;
    System.err.println("fileIndex = " + fileIndex);
    System.err.println("stacksize = " + stacksize);
    System.err.println("n = " + n);

    return imp.getProcessor();
  }

  /** Returns the number of slices in this stack. */
  public int getSize() { return nSlices; }

  /** Returns the file name of the Nth image. */
  public String getSliceLabel(int n) { return names[n-1]; }

  /** Returns null. */
  public Object[] getImageArray() { return null; }

  /** Does nothing. */
  public void setSliceLabel(String label, int n) { }

  /** Always return true. */
  public boolean isVirtual() { return true; }

  /** Does nothing. */
  public void trim() { }

}
