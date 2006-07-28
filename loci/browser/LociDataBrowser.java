//
// LociDataBrowser.java
//

package loci.browser;

import ij.*;
import ij.gui.ImageCanvas;
import ij.plugin.PlugIn;
import java.io.File;
import javax.swing.*;
import loci.formats.*;
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
  protected static final boolean DEBUG = true;

  // -- Fields --

  /** filename for each index */
  protected String[] names;

  /** The file format reader used by the plugin. */
  protected static ImageReader reader = new ImageReader();

  /** The current file name. */
  protected static String filename;

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

  private ImageStack stack;

  // -- LociDataBrowser methods --

  /** Displays the given ImageJ image in a 4D browser window. */
  public void show(ImagePlus imp) {
    int stackSize = imp == null ? 0 : imp.getStackSize();

    if (stackSize == 0) {
      IJ.showMessage("Cannot show invalid image.");
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
    return MathUtil.positionToRaster(lengths, pos);
  }

  // -- Plugin methods --

  public void run(String arg) {
    String version = System.getProperty("java.version");
    double ver = Double.parseDouble(version.substring(0, 3));
    if (ver < 1.4) {
      IJ.showMessage("Sorry, the 4D Data Browser requires\n" +
        "Java 1.4 or later. You can download ImageJ\n" +
        "with JRE 5.0 from the ImageJ web site.");
      return;
    }
    LociOpener lociOpener = new LociOpener();
    boolean done2 = false;
    String directory = "";
    String name = "";
    boolean quiet = false;
    // get file name and virtual stack option
    stack = null;
    while (!done2) {
      try {
        lociOpener.show();
        directory = lociOpener.getDirectory();
        name = lociOpener.getAbsolutePath();
        virtual = lociOpener.getVirtual();
        if (name == null || lociOpener.isCanceled()) return;
        if (DEBUG) {
          IJ.log("directory = " + directory);
          IJ.log("name = " + name);
          IJ.log("virtual = " + virtual);
        }
        ImagePlusWrapper ipw = null;

        // process input
        lengths = new int[3];

        if (DEBUG) {
          System.err.println("zIndex = "+zIndex);
          System.err.println("tIndex = "+tIndex);
          System.err.println("cIndex = "+cIndex);
          System.err.println("numZ = "+numZ);
          System.err.println("numT = "+numT);
          System.err.println("numC = "+numC);
        }
        String absname = name;
        filename = absname;
        name = FilePattern.findPattern(new File(name));
        name = name.substring(name.lastIndexOf(File.separatorChar)+1);
        if (DEBUG) System.err.println("name = "+name);

        if (virtual) {
          FormatReader fr = reader.getReader(absname);
          fr.setMetadataStore(new OMEXMLMetadataStore());
          fr.getMetadataStore(absname).createRoot();
          FileStitcher fs = new FileStitcher(fr);
          ChannelMerger cm = new ChannelMerger(fs);

          int num = cm.getImageCount(absname);

          try {
            ProgressMonitor progress = new ProgressMonitor(null,
              "Reading image", null, 0, num);

            // set dimensions appropriately

            numZ = cm.getSizeZ(absname);
            numC = cm.getSizeC(absname);
            if (cm.isRGB(absname)) {
              if (numC <= 3) numC = 1;
              else numC /= 3;
            }
            numT = cm.getSizeT(absname);
            hasZ = numZ > 1;
            hasC = numC > 1;
            hasT = numT > 1;

            String order = cm.getDimensionOrder(absname);
            zIndex = order.indexOf("Z") - 2;
            cIndex = order.indexOf("C") - 2;
            tIndex = order.indexOf("T") - 2;

            lengths[zIndex] = numZ;
            lengths[tIndex] = numT;
            lengths[cIndex] = numC;

            for (int i=0; i<num; i++) {
              if (progress.isCanceled()) break;
              progress.setProgress(i);
              progress.setNote(" " + (i+1) + " / " + num + " (" + name + ")");

              // open image
              if (stack == null) {
                stack =
                  new ImageStack(cm.getSizeX(absname), cm.getSizeY(absname));
              }

              stack.addSlice(absname, (new ImagePlus(absname,
                cm.openImage(absname, i))).getProcessor());
            }
            progress.setProgress(num);

            if (stack == null || stack.getSize() == 0) {
              IJ.showMessage("No valid files found.");
              return;
            }
          }
          catch (OutOfMemoryError e) {
            IJ.outOfMemory("LociDataBrowser");
            if (stack != null) stack.trim();
          }

          reader.setMetadataStore(fr.getMetadataStore(absname));

          show(new ImagePlus(absname, stack));
        }
        else {
          ipw = new ImagePlusWrapper(absname, virtual);
          numZ = ipw.sizeZ; numT = ipw.sizeT; numC = ipw.sizeC;
          zIndex = ipw.dim.indexOf('Z')-2;
          tIndex = ipw.dim.indexOf('T')-2;
          cIndex = ipw.dim.indexOf('C')-2;

          lengths[zIndex] = numZ;
          lengths[tIndex] = numT;
          lengths[cIndex] = numC;

          show(ipw.getImagePlus());
        }
        done2 = true;
      }
      catch (Exception exc) {
        exc.printStackTrace();
        IJ.showStatus("");
        if (!quiet) {
          String msg = exc.getMessage();
          IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
            "reading the data" + (msg == null ? "." : (": " + msg)));
        }
        if (DEBUG) System.err.println("Read error");
        done2 = false;
      }
    }
  }

}
