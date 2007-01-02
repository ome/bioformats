//
// LociDataBrowser.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.browser;

import ij.*;
import ij.gui.GenericDialog;
import ij.gui.MessageDialog;
import ij.gui.ImageCanvas;
import ij.io.FileInfo;
import java.awt.Frame;
import loci.formats.*;

/**
 * LociDataBrowser is a plugin for ImageJ that allows for browsing of 4D
 * image data (stacks of image planes over time) with two-channel support.
 *
 * @author Christopher Peterson at wisc.edu
 * @author Francis Wong yutaiwong at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LociDataBrowser {

  // -- Constants --

  /** Debugging flag. */
  protected static final boolean DEBUG = false;

  // -- Fields --

  /** Filename for each index. */
  protected String[] names;

  /** The file format reader used by the plugin. */
  protected IFormatReader reader;

  /** The file stitcher used by the reader.*/
  protected FileStitcher fStitch;

  /** The CustomWindow used to display data.*/
  protected CustomWindow cw;

  /** The current file name. */
  protected String id;

  /** Whether dataset has multiple Z, T and C positions. */
  protected boolean hasZ, hasT, hasC;

  /** Number of Z, T and C positions. */
  protected int numZ, numT, numC;

  /** Lengths of all dimensional axes; lengths[0] equals image depth. */
  protected int[] lengths;

  /** Indices into lengths array for Z, T and C. */
  protected int zIndex, tIndex, cIndex;

  /** Whether stack is accessed from disk as needed. */
  protected boolean virtual;

  /** Cache manager (if virtual stack is used). */
  protected CacheManager manager;

  /** Series to use in a multi-series file. */
  protected int series;

  private ImageStack stack;

  // -- Constructor --

  /** Constructs a new data browser. */
  public LociDataBrowser() {
    fStitch = new FileStitcher();
    reader = new ChannelSeparator(fStitch);
  }

  public LociDataBrowser(String name) {
    this();
    id = name;
    virtual = true;
  }

  public LociDataBrowser(boolean merged) {
    fStitch = new FileStitcher();
    if(merged) reader = new ChannelMerger(fStitch);
    else reader = new ChannelSeparator(fStitch);
  }

  public LociDataBrowser(String name, boolean merged) {
    this(merged);
    id = name;
    virtual = true;
  }

  public LociDataBrowser(IFormatReader r, FileStitcher fs, String name) {
    virtual = true;
    reader = r;
    fStitch = fs;
    id = name;
  }

  // -- LociDataBrowser API methods --

  /**
  * Displays the given ImageJ image in a 4D browser window.
  * NB: this method is needed only internally. Do not call.
  */
  private void show(ImagePlus imp) {
    int stackSize = imp == null ? 0 : imp.getStackSize();

    if (stackSize == 0) {
      IJ.showMessage("Cannot show invalid image.");
      return;
    }

    if (stackSize == 1 && !virtual) {
      // show single image normally
      imp.show();
      return;
    }

    cw = new CustomWindow(this, imp, new ImageCanvas(imp));
  }

  /** Set the length of each dimensional axis and the dimension order. */
  public void setDimensions(int sizeZ, int sizeC, int sizeT, int z,
    int c, int t)
  {
    numZ = sizeZ;
    numC = sizeC;
    numT = sizeT;

    hasZ = numZ > 1;
    hasC = numC > 1;
    hasT = numT > 1;

    lengths = new int[3];
    lengths[z] = numZ;
    lengths[c] = numC;
    lengths[t] = numT;

    zIndex = z;
    cIndex = c;
    tIndex = t;
  }

  /** Reset all dimensional data in case they've switched.*/
  public void setDimensions() {
    String order = null;

    if (fStitch != null) {
      try {
        numZ = fStitch.getSizeZ(id);
        if(reader instanceof ChannelMerger) {
          numC = fStitch.getEffectiveSizeC(id);
//          IJ.showMessage("ChannelMerger Detected!! Size C = " + numC);
        }
        else numC = fStitch.getSizeC(id);
        numT = fStitch.getSizeT(id);
        order = fStitch.getDimensionOrder(id);
      }
      catch (Exception exc) {
        if (DEBUG) exc.printStackTrace();
        exceptionMessage(exc);
        return;
      }
    }
    else {
      try {
        numZ = reader.getSizeZ(id);
        if(reader instanceof ChannelMerger) numC = reader.getEffectiveSizeC(id);
        else numC = reader.getSizeC(id);
        numT = reader.getSizeT(id);
        order = reader.getDimensionOrder(id);
      }
      catch (Exception exc) {
        if (DEBUG) exc.printStackTrace();
        exceptionMessage(exc);
        return;
      }
    }

    hasZ = numZ > 1;
    hasC = numC > 1;
    hasT = numT > 1;

    zIndex = order.indexOf("Z") - 2;
    cIndex = order.indexOf("C") - 2;
    tIndex = order.indexOf("T") - 2;

    lengths[zIndex] = numZ;
    lengths[tIndex] = numT;
    lengths[cIndex] = numC;
  }

  /** Gets the slice number for the given Z, T and C indices. */
  public int getIndex(int z, int t, int c) {
    int result = -23;
    synchronized(reader) {
      try {
        result = reader.getIndex(id,z,c,t);
      }
      catch (Exception exc) {
        if (DEBUG) exc.printStackTrace();
        exceptionMessage(exc);
      }
    }
    return result;
  }

  /** Sets the series to open. */
  public void setSeries(int num) {
    // TODO : this isn't the prettiest way of prompting for a series
    GenericDialog datasets =
      new GenericDialog("4D Data Browser Series Chooser");

    String[] values = new String[num];
    for (int i=0; i<values.length; i++) values[i] = "" + i;

    datasets.addChoice("Series ", values, "0");

    if (num > 1) datasets.showDialog();

    series = Integer.parseInt(datasets.getNextChoice());
  }

  public static void exceptionMessage(Exception exc) {
    String msg = exc.toString();
    StackTraceElement[] ste = exc.getStackTrace();
    for(int i = 0;i<ste.length;i++) {
     msg = msg + "\n" + ste[i].toString();
    }

    IJ.showMessage(msg);
  }

  public void toggleCache(boolean cached) {
    if(cached) {
      if(!virtual) {
        cw.ow.dispose();
        cw.dispose();
        virtual = true;
        run("");
      }
    }
    else {
      if(virtual) {
        cw.ow.dispose();
        cw.dispose();
        virtual = false;
        run("");
      }
    }
  }

  public void run(String arg) {
    LociOpener lociOpener = new LociOpener();
    boolean done2 = false;
    String directory = "";
    String name = "";
    boolean quiet = false;

    // get file name and virtual stack option
    stack = null;
    while (!done2) {
      try {
        if(id == null) {
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

          id = name;
          if (DEBUG) System.err.println("id = " + id);
        }

        ImagePlusWrapper ipw = null;

        // process input
        lengths = new int[3];

        if (virtual) {
          OMEXMLMetadataStore store = new OMEXMLMetadataStore();
          synchronized (reader) {
            reader.setMetadataStore(store);
            store.createRoot();
            reader.setSeries(id, series);

            int num = reader.getImageCount(id);

            manager = new CacheManager(0, 0, 0, 0, 0, 0, 20, 0, 0,
              this, id, CacheManager.T_AXIS,
              CacheManager.CROSS_MODE, CacheManager.FORWARD_FIRST);

            try {
              setDimensions();

              // CTR: stack must not be null
              int sizeX = reader.getSizeX(id);
              int sizeY = reader.getSizeY(id);
              stack = new ImageStack(sizeX, sizeY);
              // CTR: must add at least one image to the stack
              stack.addSlice(id + " : 1", manager.getSlice(0, 0, 0));
            }
            catch (OutOfMemoryError e) {
              IJ.outOfMemory("LociDataBrowser");
              if (stack != null) stack.trim();
            }
          }

          if (stack == null || stack.getSize() == 0) {
            IJ.error("Sorry, there was a problem creating the image stack.");
            return;
          }
          ImagePlus imp = new ImagePlus(id, stack);

          FileInfo fi = new FileInfo();
          try {
            fi.description = store.dumpXML();
          }
          catch (Exception exc) {
            exc.printStackTrace();
            exceptionMessage(exc);
          }

          imp.setFileInfo(fi);
          show(imp);
        }
        else {
          ipw = new ImagePlusWrapper(id, reader, fStitch, true);
          setDimensions();

          int stackSize = ipw.getImagePlus().getStackSize();
          if (stackSize != numZ * numT * numC) {
            System.err.println("Error, stack size mismatch with dimension " +
              "sizes: stackSize=" + stackSize + ", numZ=" + numZ +
              ", numT=" + numT + ", numC=" + numC);
          }

          FileInfo fi = ipw.getImagePlus().getOriginalFileInfo();
          if (fi == null) fi = new FileInfo();
          try {
            fi.description = ((OMEXMLMetadataStore) ipw.store).dumpXML();
          }
          catch (Exception exc) {
            exceptionMessage(exc);
          }
          ipw.getImagePlus().setFileInfo(fi);

          show(ipw.getImagePlus());
        }
        done2 = true;
      }
      catch (Exception exc) {
        exc.printStackTrace();
        IJ.showStatus("");
        if (!quiet) {
          exceptionMessage(exc);
          String msg = exc.getMessage();
          IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
            "reading the data" + (msg == null ? "." : (": " + msg)));
        }
        if (DEBUG) System.err.println("Read error");
        done2 = false;
      }
    }
  }

  // -- Main method --

  /** Main method, for testing. */
  public static void main(String[] args) {
    new ImageJ(null);
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<args.length; i++) {
      if (i > 0) sb.append(" ");
      sb.append(args[i]);
    }
    new LociDataBrowser().run(sb.toString());
  }

}
