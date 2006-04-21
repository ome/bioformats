//
// VirtualStack.java
//

package loci.browser;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;
import java.util.Arrays;

/** This class represents an array of disk-resident images. */
class VirtualStack extends ImageStack {

  // -- Constants --

  static final int INITIAL_SIZE = 100;

  // -- Fields --

  String path;
  int nSlices;
  String[] names;
  protected static final int Z_AXIS = 1;
  protected static final int T_AXIS = 2;
  protected int tp;
  private int fileIndex;
  private int stacksize;
  private ImagePlus imp;
  private boolean animT; // keeping track of which axis to animate
  private ImageStack currentStack;
  private static int[] indices;
  
    // fields about the image
    private int height;
    private int width;
    private ColorModel cm;

    private boolean animate;
    private int[] oldptr;  

  // -- Constructor --

  /** Creates a new, empty virtual stack. */
  public VirtualStack(int width, int height, ColorModel cm, String path) {
    super(width, height, cm);
    this.width = width;
    this.height = height;
    this.cm = cm;
    this.path = path;
    names = new String[INITIAL_SIZE];
    fileIndex = -1;
    stacksize = -1;
    animT = true;
    nSlices = 0;

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


    public void swapAxes(int val, int max) {
	animT = !animT;
	stacksize = -1;
	fileIndex = -1;
	if (animT) {
	    currentStack = new ImageStack(width, height, cm);
	}
    }
    
    // -- ImageStack methods --
    
    /** Does nothing. */
    public void addSlice(String sliceLabel, Object pixels) {
	System.err.println("ERROR: calling addSlice(sliceLabel, pixels)");
    }
    

  public void addSlice(String sliceLabel, ImageProcessor ip) {
    if (LociDataBrowser.DEBUG) {
	System.err.println("calling VirtualStack.addSlice(sliceLabel, ip)");
    }
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
    synchronized public ImageProcessor getProcessor(int n) {
      if (indices == null) {
    //IJ.log("getProcessor: "+n+"  "+names[n-1]);
	  if (stacksize == -1 || fileIndex != (n-1) / stacksize) {
	      imp = new Opener().openImage(path, names[n-1]);
	      stacksize = imp.getStackSize();
	      fileIndex = (n-1) / stacksize;
	  }
	  if (imp!=null) {
	      imp.setSlice(n % stacksize == 0 ? stacksize : n % stacksize);
	      if (LociDataBrowser.DEBUG) 
		  System.err.println("Stack label = "+imp.getStack().getSliceLabel(n % stacksize == 0 ? stacksize : n % stacksize));
	  }
	  else return null;
      return imp.getProcessor();
      } else {
	  if (LociDataBrowser.DEBUG) {
	      System.err.println("n = "+n);
	      System.err.println("indices[n] = "+indices[n]);
	  }
	  if (indices[n] != -1) return currentStack.getProcessor(indices[n]);
	  else {
	      if (LociDataBrowser.DEBUG) System.err.println("*** indices["+n+"] == -1!!! ***");
	      imp = new Opener().openImage(path,names[n-1]);
	      if (imp!=null) imp.setSlice(n % stacksize == 0 ? stacksize : n % stacksize);
	      return imp.getProcessor();
	  }
      }
  }
    
    public static void resetIndices() {
	indices = null;
    }

    synchronized public void setIndices(int[] ptr) {
	// same subset of slices on stack
	if (oldptr != null && Arrays.equals(ptr,oldptr)) {
	    if (LociDataBrowser.DEBUG) System.err.println("oldptr == ptr");
	    return; 
	}
	else {
	    oldptr = (int[])ptr.clone();  // store pointers
	    currentStack = new ImageStack(width, height, cm);
	    if (indices == null) indices = new int[nSlices+1];
	    for (int k=0; k<=nSlices; k++) indices[k] = -1;
	    for (int k=0; k<ptr.length; k++) {
		indices[ptr[k]] = k+1;  // adjust for getIndex() off-by-1
		try {
		    imp = new Opener().openImage(path, names[ptr[k]-1]);
		    int idx = ptr[k] % stacksize == 0 ? stacksize : ptr[k] % stacksize;
		    imp.setSlice(idx);
		    currentStack.addSlice(imp.getTitle(), imp.getProcessor());
		    if (LociDataBrowser.DEBUG) {
			System.err.println("ptr["+k+"] = "+ptr[k]);
			System.err.println("imp.setSlice("+idx+")");
		    }
		} catch (NullPointerException npe) {
		    if (LociDataBrowser.DEBUG) {
			System.err.println("*** NULL POINTER: ptr["+k+"] = "+ptr[k]);
			System.err.println("Name of file = "+names[ptr[k]]);
		    }
		    
		}

	    }
	}
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
