//
// LociDataBrowser.java
//

package loci.browser;

import ij.ImageStack;
import ij.ImagePlus;
import ij.IJ;
import ij.process.*;
import ij.io.FileInfo;
import java.awt.image.*;
import ij.gui.ImageCanvas;
import ij.plugin.PlugIn;
import java.awt.image.ColorModel;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.awt.Toolkit;
import java.util.Arrays;
import loci.util.FilePattern;
import loci.util.MathUtil;
import loci.formats.*;
import loci.formats.ChannelMerger;
import javax.swing.*;


/**
 * LociDataBrowser is a plugin for ImageJ that allows for browsing of 4D
 * image data (stacks of image planes over time) with two-channel support.
 *
 * @author Francis Wong yutaiwong at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LociDataBrowser implements PlugIn {

  // -- Constants --

  /** Debugging flag. */
  protected static final boolean DEBUG = true;

  /** Prefix endings indicating numbering block represents T. */
  private static final String[] PRE_T = {
    "_T", "-T", ".T", "_TP", "-TP", ".TP", "_TL", "-TL", ".TL"
  };

  /** Prefix endings indicating numbering block represents Z. */
  private static final String[] PRE_Z = {
    "_Z", "-Z", ".Z", "_ZS", "-ZS", ".ZS",
    "_FP", "-FP", ".FP", "_SEC", "-SEC", ".SEC"
  };

  /** Prefix endings indicating numbering block represents C. */
  private static final String[] PRE_C = {
    "_C", "-C", ".C", "_CH", "-CH", ".CH"
  };


  // -- Fields --

  /** filename for each index */
  protected String[] names;

  /** whether dataset has multiple Z, T and C positions */
  protected boolean hasZ, hasT, hasC;

  /** number of Z, T and C positions */
  protected int numZ, numT, numC;

  /** lengths of all dimensional axes; lengths[0] equals image depth */
  protected int[] lengths;

  /** indices into lengths array for Z, T and C */
  protected int zIndex, tIndex, cIndex;

  /** whether stack is accessed from disk as needed */
  protected boolean virtual;

  private ImagePlus imp;

    private ImageStack stack;

  // -- LociDataBrowser methods --

		

  private void slice(ImageStack is, String file, int c) {
    ImageStack[] newStacks = new ImageStack[c];
    for (int i=0; i<newStacks.length; i++) {
      newStacks[i] = new ImageStack(is.getWidth(), is.getHeight());
    }

    for (int i=1; i<=is.getSize(); i+=c) {
      for (int j=0; j<c; j++) {
        newStacks[j].addSlice(is.getSliceLabel(i+j), is.getProcessor(i+j));
      }
    }

    for (int i=0; i<newStacks.length; i++) {
      new ImagePlus(file + " - Ch" + (i+1), newStacks[i]).show();
    }
  }
	

  /** Displays the given ImageJ image in a 4D browser window. */
  public void show(ImagePlus imp) {
    int stackSize = imp == null ? 0 : imp.getStackSize();

    if (stackSize == 0) {
      msg("Cannot show invalid image.");
      return;
    }

    if (stackSize == 1) {
      // show single image normally
      imp.show();
      return;
    }

    new CustomWindow(this, imp, new ImageCanvas(imp));
  }

  /**
   * Shows the given image in a 4D browser window, using the given parameters.
   * @param imp the image to be shown
   * @param labels text to be shown for each slice
   * @param axes length of each dimensional axis
   * @param countZ number of Z positions (should be at least 1)
   * @param countT number of T positions (should be at least 1)
   * @param countC number of C positions (should be at least 1)
   * @param indexZ axis number of Z axis, or -1 for no Z axis
   * @param indexT axis number of T axis, or -1 for no T axis
   * @param indexC axis number of C axis, or -1 for no C axis
   */
  public void show(ImagePlus imp, String[] labels, int[] axes,
    int countZ, int countT, int countC, int indexZ, int indexT, int indexC)
  {
    names = labels;
    lengths = axes;
    numZ = countZ;
    numT = countT;
    numC = countC;
    hasZ = numZ > 1;
    hasT = numT > 1;
    hasC = numC > 1;
    zIndex = indexZ;
    tIndex = indexT;
    cIndex = indexC;
    show(imp);
  }

  /** gets the slice number for the given Z, T and C indices */
  public int getIndex(int z, int t, int c) {
      int[] pos = new int[lengths.length];
      if (zIndex >= 0) pos[zIndex] = z;
      if (tIndex >= 0) pos[tIndex] = t;
      if (cIndex >= 0) pos[cIndex] = c;
//       System.err.println("lengths.length = "+lengths.length);
//       System.err.println("pos[zIndex] = "+pos[zIndex]);
//       System.err.println("pos[tIndex] = "+pos[tIndex]);
//       System.err.println("pos[cIndex] = "+pos[cIndex]);
//       System.err.println("positionToRaster = "+(MathUtil.positionToRaster(lengths, pos) + 1));
      
      return MathUtil.positionToRaster(lengths, pos);
  }


  // -- Plugin methods --

  public void run(String arg) {
    String version = System.getProperty("java.version");
    double ver = Double.parseDouble(version.substring(0, 3));
    if (ver < 1.4) {
      msg("Sorry, the 4D Data Browser requires\n" +
        "Java 1.4 or later. You can download ImageJ\n" +
        "with JRE 5.0 from the ImageJ web site.");
      return;
    }
    LociOpener lociOpener;
    loci.formats.ImageReader reader = new ImageReader();
    boolean done2 = false;
    String directory = "";
    String name = "";
    boolean quiet = false;
    // get file name and virtual stack option
    lociOpener = new LociOpener();
    stack = null;
    while (!done2) {
	try {
	    lociOpener.show();
	    directory = lociOpener.getDirectory();
	    name = lociOpener.getAbsolutePath();
	    virtual = lociOpener.getVirtual();
	    VirtualStack.path = directory;
	    if (name == null || lociOpener.isCanceled()) return;
	    if (DEBUG) {
		log("directory", directory);
		log("name", name);
		log("virtual", virtual);
	    }
	    System.err.println("ImageCount = "+reader.getImageCount(name));
	    ImagePlusWrapper ipw = null;
	    // process input
	    ipw = new ImagePlusWrapper(name, virtual);
	    numZ = ipw.Z; numT = ipw.T; numC = ipw.C;
	    zIndex = ipw.dim.indexOf('Z')-2;
	    tIndex = ipw.dim.indexOf('T')-2;
	    cIndex = ipw.dim.indexOf('C')-2;
	    hasZ = numZ != 1;
	    hasT = numT != 1;
	    hasC = numC != 1;
	    lengths = new int[3];
	    lengths[zIndex] = numZ;
	    lengths[tIndex] = numT;
	    lengths[cIndex] = numC;
	    System.err.println("dim = "+ipw.dim);
	    System.err.println("zIndex = "+zIndex);
	    System.err.println("tIndex = "+tIndex);
	    System.err.println("cIndex = "+cIndex);
	    System.err.println("numZ = "+numZ);
	    System.err.println("numT = "+numT);
	    System.err.println("numC = "+numC);
	    String absname = name;
	    name = strip(name);
	    name = name.substring(name.lastIndexOf(File.separatorChar)+1);
	    System.err.println("name = "+name);

	    if (virtual) {
		String pattern = FilePattern.findPattern(name,directory);
		FilePattern fp = new FilePattern(pattern);
		imp = new ImagePlus(name, Toolkit.getDefaultToolkit().createImage(
				 (new ChannelMerger((new ImageReader()).getReader(
				  absname))).openImage(absname,0).getSource()));
		String[] filenames = fp.getFiles(); // all the image files
		int dirLen = directory.length();
		for (int i=0; i<filenames.length; i++) {
		    int q = dirLen;
		    while (filenames[i].charAt(q) == File.separatorChar) q++;
		    filenames[i] = filenames[i].substring(q);
		}
		    
		// read images
		int depth = 0, width = 0, height = 0, type = 0;
		FileInfo fi = null;
		try {
		    ProgressMonitor progress = new ProgressMonitor(null,
				  "Reading", null, 0, filenames.length);
		    progress.setMillisToPopup(50);
		    System.err.println("filenames.length = "+filenames.length);
		    for (int i=0; i<filenames.length; i++) {
			if (progress.isCanceled()) break;
			progress.setProgress(i);
			progress.setNote(filenames[i]);
			    
			// open image
			if (imp == null) {
			    // invalid image
			    log(filenames[i] + ": unable to open");
			    continue;
			}
			    
			if (stack == null) {
			    // first image in the stack
			    System.err.println("depth = "+depth);
			    width = imp.getWidth();
			    height = imp.getHeight();
			    type = imp.getType();
			    System.err.println("In here!!");
			    ColorModel cm = imp.getProcessor().getColorModel();
			    stack = new VirtualStack(width, height, cm, directory);
			    depth = ipw.getNumSingle();
			    ((VirtualStack)stack).stacksize = depth;
			}
			    
			// verify image is sane
			int w = imp.getWidth(), h = imp.getHeight(), t = imp.getType();
			if (w != width || h != height) {
			    // current image dimension different than those in the stack
			    log(filenames[i] + ": wrong dimensions (" + w + " x " + h +
				" instead of " + width + " x " + height + ")");
			    continue;
			}
			if (t != type) {
			    // current image file type different than those in the stack
			    log(filenames[i] + ": wrong type (" +
				t + " instead of " + type + ")");
			    continue;
			}
			    
			// process every slice in each stack
			for (int j=1; j<=depth; j++) {
			    imp.setSlice(j);
			    stack.addSlice(filenames[i], imp.getProcessor());
			}
		    }
		    progress.setProgress(filenames.length);

		    if (stack == null || stack.getSize() == 0) {
			System.err.println("stack == null is "+(stack==null));
			System.err.println("stack.getSize() == 0 is "+(stack.getSize()));
			// all image files were invalid
			msg("No valid files found.");
			return;
		    }
		} catch (OutOfMemoryError e) {
		    IJ.outOfMemory("LociDataBrowser");
		    if (stack != null) stack.trim();
		}
		show (new ImagePlus(name, stack));
	    }
	    else show(ipw.getImagePlus());
	    done2 = true;
	} catch (Exception exc) {
	    exc.printStackTrace();
	    IJ.showStatus("");
	    if (!quiet) {
		String msg = exc.getMessage();
		IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
			       "reading the data" + (msg == null ? "." : (": " + msg)));
	    }
	    System.err.println("Read error");
	    done2 = false;
	}
    }
  }


  // -- Internal LociDataBrowser methods --

  protected String strip(String name) {
    // strip off endings, if any, from name
    int zLen = PRE_Z.length, tLen = PRE_T.length, cLen = PRE_C.length;
    String[] endings = new String[zLen + tLen + cLen];
    System.arraycopy(PRE_Z, 0, endings, 0, zLen);
    System.arraycopy(PRE_T, 0, endings, zLen, tLen);
    System.arraycopy(PRE_C, 0, endings, zLen + tLen, cLen);
    String upper = name.toUpperCase();
    for (int i=0; i<endings.length; i++) {
      if (upper.endsWith(endings[i])) {
        name = name.substring(0, name.length() - endings[i].length());
        name = name.replaceAll("\\d+$", ""); // strip trailing digits
        upper = name.toUpperCase();
        i = -1; // start over
      }
    }
    return name;
  }

  protected void msg(String msg) { IJ.showMessage("LociDataBrowser", msg); }

  protected void log(String msg) { IJ.log("LociDataBrowser: " + msg); }

  protected void log(String var, Object val) {
    int len = -1;
    try { len = Array.getLength(val); }
    catch (Exception exc) { }

    if (len < 0) log(var + " = " + val); // single object
    else {
      StringBuffer sb = new StringBuffer(var);
      sb.append(" =");
      for (int i=0; i<len; i++) {
        sb.append(" ");
        sb.append(Array.get(val, i).toString());
      }
      log(sb.toString());
    }
  }

  protected void log(String var, int val) { log(var + " = " + val); }

  protected void log(String var, boolean val) { log(var + " = " + val); }

}
