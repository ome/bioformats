//
// LociDataBrowser.java
//

package loci.browser;

import ij.*;
import ij.gui.ImageCanvas;
import ij.io.*;
import ij.plugin.PlugIn;
import java.awt.image.ColorModel;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import loci.util.FilePattern;
import loci.util.MathUtil;

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
  protected static final boolean DEBUG = false;

  /** Prefix endings indicating numbering block represents T. */
  private static final String[] PRE_T = {
    "_TP", "-TP", ".TP", "_TL", "-TL", ".TL"
  };

  /** Prefix endings indicating numbering block represents Z. */
  private static final String[] PRE_Z = {
    "_Z", "-Z", ".Z", "_ZS", "-ZS", ".ZS"
  };

  /** Prefix endings indicating numbering block represents C. */
  private static final String[] PRE_C = {"_C", "-C", ".C"};


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


  // -- LociDataBrowser methods --

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

  /** gets the slice number for the given Z, T and C indices */
  public int getIndex(int z, int t, int c) {
    int[] pos = new int[lengths.length];
    if (zIndex >= 0) pos[zIndex] = z;
    if (tIndex >= 0) pos[tIndex] = t;
    if (cIndex >= 0) pos[cIndex] = c;
    return MathUtil.positionToRaster(lengths, pos) + 1;
  }


  // -- Plugin methods --

  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Sequence of Image Stacks:", "");
    String directory = od.getDirectory();
    String name = od.getFileName();
    if (name == null) return;

    // find all the files having similar names (using FilePattern class)
    String pattern = FilePattern.findPattern(name, directory);
    FilePattern fp = new FilePattern(pattern);
    String[] filenames = fp.getFiles(); // all the image files
    int numFiles = filenames.length;

    // trim directory prefix from filename list
    int dirLen = directory.length();
    for (int i=0; i<filenames.length; i++) {
      int q = dirLen;
      while (filenames[i].charAt(q) == File.separatorChar) q++;
      filenames[i] = filenames[i].substring(q);
    }

    if (DEBUG) {
      log("directory", directory);
      log("name", name);
      log("pattern", pattern);
      log("filenames", filenames);
    }

    // read images
    ImageStack stack = null;
    int depth = 0, width = 0, height = 0, type = 0;
    try {
      for (int i=0; i<filenames.length; i++) {
        // open image
        ImagePlus imp = new Opener().openImage(directory, filenames[i]);
        if (imp == null) {
          // invalid image
          log(filenames[i] + ": unable to open");
          continue;
        }

        if (stack == null) {
          // first image in the stack
          depth = imp.getStackSize();
          width = imp.getWidth();
          height = imp.getHeight();
          type = imp.getType();
          ColorModel cm = imp.getProcessor().getColorModel();
          stack = new ImageStack(width, height, cm);
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
          if (depth > 1) IJ.showStatus(filenames[i] + " " + j + "/" + depth);
          else IJ.showStatus(filenames[i]);
          IJ.showProgress((double) (i * depth + j - 1) / (numFiles * depth));
          imp.setSlice(j);
          stack.addSlice(imp.getTitle(), imp.getProcessor());
        }
      }

      if (stack == null || stack.getSize() == 0) {
        // all image files were invalid
        msg("No valid files found.");
        return;
      }
    }
    catch (OutOfMemoryError e) {
      IJ.outOfMemory("LociDataBrowser");
      if (stack != null) stack.trim();
    }
    if (DEBUG) {
      log("depth", depth);
      log("width", width);
      log("height", height);
      log("type", type);
    }

    // populate names list
    names = new String[numFiles * depth];
    for (int i=0; i<filenames.length; i++) {
      Arrays.fill(names, depth * i, depth * (i + 1), filenames[i]);
    }
    if (DEBUG) log("names", names);

    // gather more pattern information
    BigInteger[] first = fp.getFirst();
    BigInteger[] last = fp.getLast();
    BigInteger[] step = fp.getStep();
    int[] count = fp.getCount();
    String[] pre = fp.getPrefixes();
    if (DEBUG) {
      log("first", first);
      log("last", last);
      log("step", step);
      log("count", count);
      log("pre", pre);
    }

    // determine which axes are which
    zIndex = tIndex = cIndex = -1;
    boolean[] match = new boolean[count.length + 1];
    for (int i=1; i<=pre.length; i++) {
      // remove trailing digits and capitalize
      String p = pre[i - 1].replaceAll("\\d+$", "");
      if (i == 1) name = p;
      p = p.toUpperCase();

      // check for Z endings
      if (zIndex < 0) {
        for (int j=0; j<PRE_Z.length; j++) {
          if (p.endsWith(PRE_Z[j])) {
            zIndex = i;
            match[i] = true;
            break;
          }
        }
        if (match[i]) continue;
      }

      // check for T endings
      if (tIndex < 0) {
        for (int j=0; j<PRE_T.length; j++) {
          if (p.endsWith(PRE_T[j])) {
            tIndex = i;
            match[i] = true;
            break;
          }
        }
        if (match[i]) continue;
      }

      // check for C endings
      if (cIndex < 0) {
        for (int j=0; j<PRE_C.length; j++) {
          if (p.endsWith(PRE_C[j])) {
            cIndex = i;
            match[i] = true;
            break;
          }
        }
        if (match[i]) continue;

        // check for 2-3 numbering (C)
        if (first[i - 1].intValue() == 2 &&
          last[i - 1].intValue() == 3 && step[i - 1].intValue() == 1)
        {
          cIndex = i;
          match[i] = true;
          break;
        }
      }
    }

    lengths = new int[count.length + 1];
    lengths[0] = depth;
    System.arraycopy(count, 0, lengths, 1, count.length);

    // assign as many remaining axes as possible
    for (int i=(depth > 1 ? 0 : 1); i<match.length; i++) {
      if (match[i]) continue;
      if (tIndex < 0) tIndex = i;
      else if (zIndex < 0) zIndex = i;
      else if (cIndex < 0) cIndex = i;
    }

    hasZ = zIndex >= 0;
    hasT = tIndex >= 0;
    hasC = cIndex >= 0;
    numZ = hasZ ? lengths[zIndex] : 1;
    numT = hasT ? lengths[tIndex] : 1;
    numC = hasC ? lengths[cIndex] : 1;

    if (DEBUG) {
      log("lengths", lengths);
      log("zIndex = " + zIndex);
      log("tIndex = " + tIndex);
      log("cIndex = " + cIndex);
      log("hasZ = " + hasZ);
      log("hasT = " + hasT);
      log("hasC = " + hasC);
      log("numZ = " + numZ);
      log("numT = " + numT);
      log("numC = " + numC);
    }

    // strip off endings, if any, from name
    name = strip(name, PRE_Z);
    name = strip(name, PRE_T);
    name = strip(name, PRE_C);

    // display image onscreen
    show(new ImagePlus(name, stack));
    IJ.showStatus("");
    IJ.showProgress(1);
  }


  // -- Internal LociDataBrowser methods --

  protected String strip(String name, String[] endings) {
    String n = name.toUpperCase();
    for (int i=0; i<endings.length; i++) {
      if (n.endsWith(endings[i])) {
        return name.substring(0, name.length() - endings[i].length());
      }
    }
    return name;
  }

  protected void msg(String msg) { IJ.showMessage("LociDataBrowser", msg); }

  protected void log(String msg) { IJ.log("LociDataBrowser: " + msg); }

  protected void log(String var, Object val) {
    int len = -1;
    try { len = Array.getLength(val); }
    catch (IllegalArgumentException exc) { }

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

}
