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
import ij.gui.ImageCanvas;
import ij.io.FileInfo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadataStore;

/**
 * LociDataBrowser is a plugin for ImageJ that allows for browsing of 4D
 * image data (stacks of image planes over time) with two-channel support.
 *
 * @author Christopher Peterson at wisc.edu
 * @author Francis Wong yutaiwong at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LociDataBrowser {

  // -- Constants --

  /** Debugging flag. */
  protected static final boolean DEBUG = false;

  // -- Fields --

  /** Filename for each index. */
  protected String[] names;

  /** The file format reader used by the plugin. */
  protected DimensionSwapper reader;

  /** The file stitcher used by the reader. */
  protected FileStitcher fStitch;

  /** The CustomWindow used to display data. */
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

//  /** Macro manager - allows us to perform operations on a virtual stack. */
//  protected MacroManager macro;

//  /** Macro manager thread. */
//  protected Thread macroThread;

  /** Series to use in a multi-series file. */
  protected int series;

  private ImageStack stack;

  // -- Constructors --

  public LociDataBrowser(String name) {
    this(null, null, name, 0, false);
  }

  public LociDataBrowser(String name, boolean merged) {
    this(null, null, name, 0, merged);
  }

  public LociDataBrowser(IFormatReader r,
    FileStitcher fs, String name, int seriesNo, boolean merged)
  {
    fStitch = fs;
    if (r == null) {
      r = merged ? (IFormatReader)
        new ChannelMerger(fStitch) : new ChannelSeparator(fStitch);
    }
    reader = new DimensionSwapper(r);
    id = name;
    series = seriesNo;
    virtual = true;
    try {
      reader.setId(id);
      reader.setSeries(series);
      if (fStitch != null) { 
        fStitch.setId(id);
        fStitch.setSeries(series);
      } 
    }
    catch (Exception exc) {
      exc.printStackTrace();
      dumpException(exc);
      String msg = exc.getMessage();
      IJ.showMessage("4D Data Browser", "Sorry, there was a problem " +
        "reading the data" + (msg == null ? "." : (": " + msg)));
    }

// TODO: macros
//    macro = new MacroManager();
//    macroThread = new Thread(macro, "MacroRecorder");
//    macroThread.start();
  }

  // -- LociDataBrowser API methods --

  /** Displays the given ImageJ image in a 4D browser window. */
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
    if (cw != null) {
      cw.ow.dispose();
      cw.ow = null;
      cw.dispose();
      cw = null;
    }
    cw = new CustomWindow(this, imp, new ImageCanvas(imp));
  }

  /** Sets the length of each dimensional axis and the dimension order. */
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

  /** Resets all dimensional data in case they've switched. */
  public void setDimensions() {
    String order = null;

    try {
      numZ = reader.getSizeZ();
      numC = reader.getEffectiveSizeC();
      numT = reader.getSizeT();
      order = reader.getDimensionOrder();
      if (DEBUG) {
        System.out.println("setDimensions: numZ=" + numZ +
          ", numC=" + numC + ", numT=" + numT + ", order=" + order);
      }
    }
    catch (Exception exc) {
      dumpException(exc);
      return;
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
    synchronized (reader) {
      try {
        result = reader.getIndex(z, c, t);
      }
      catch (FormatException exc) {
        dumpException(exc);
      }
    }
    return result;
  }

  public static void dumpException(Exception exc) {
    exc.printStackTrace();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    exc.printStackTrace(new PrintStream(out));
    String dump = new String(out.toByteArray());
    String msg = "Sorry, there was a problem:\n\n" + dump;
    IJ.showMessage("4D Data Browser", msg);
  }

  public void toggleCache(boolean cached) {
    if (cached != virtual) {
      virtual = !virtual;
      run();
    }
  }

  public void toggleMerge() {
    if (reader.getReader() instanceof ChannelMerger) {
      IFormatReader parent = ((ReaderWrapper) reader).getReader();
      reader = new DimensionSwapper(new ChannelSeparator(parent));
      run();
    }
    else if (reader.getReader() instanceof ChannelSeparator) {
      IFormatReader parent = ((ReaderWrapper) reader).getReader();
      reader = new DimensionSwapper(new ChannelMerger(parent));
      run();
    }
    else {
      throw new RuntimeException("Unsupported reader class: " +
        reader.getClass().getName());
    }
  }

  public boolean isMerged() {
    return reader.getReader() instanceof ChannelMerger;
  }

  public void run() {
// TODO: macros
//    macro = new MacroManager();
//    macroThread = new Thread(macro, "MacroRecorder");
//    macroThread.start();

    stack = null;
    try {
      ImagePlusWrapper ipw = null;

      // process input
      lengths = new int[3];

      if (virtual) {
        synchronized (reader) {
          int num = reader.getImageCount();
          if (manager != null) {
            manager.finish();
            manager = null;
          }
          manager = new CacheManager(0, 0, 0, 0, 0, 0, 20, 0, 0,
            this, id, CacheManager.T_AXIS,
            CacheManager.CROSS_MODE, CacheManager.FORWARD_FIRST);

          try {
            setDimensions();

            // CTR: stack must not be null
            int sizeX = reader.getSizeX();
            int sizeY = reader.getSizeY();
            stack = new ImageStack(sizeX, sizeY);
            // CTR: must add at least one image to the stack
            stack.addSlice(id + " : 1", manager.getSlice(0, 0, 0));
          }
          catch (OutOfMemoryError e) {
            IJ.outOfMemory("4D Data Browser");
            if (stack != null) stack.trim();
          }
        }

        if (stack == null || stack.getSize() == 0) {
          IJ.error("Sorry, there was a problem creating the image stack.");
          return;
        }
        ImagePlus imp = new ImagePlus(id, stack);

        FileInfo fi = new FileInfo();

        MetadataStore store = reader.getMetadataStore();
        if (store instanceof OMEXMLMetadataStore) {
          fi.description = ((OMEXMLMetadataStore) store).dumpXML();
        }

        imp.setFileInfo(fi);
        show(imp);
      }
      else {
        manager = null;
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

        MetadataStore store = ipw.store;
        if (store instanceof OMEXMLMetadataStore) {
          fi.description = ((OMEXMLMetadataStore) store).dumpXML();
        }

        ipw.getImagePlus().setFileInfo(fi);
        show(ipw.getImagePlus());
      }
    }
    catch (Exception exc) { dumpException(exc); }
  }

  // -- Main method --

  /** Main method, for testing. */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Please specify a filename on the command line.");
      System.exit(1);
    }
    ImageJ ij = new ImageJ(null);
    LociDataBrowser ldb = new LociDataBrowser(args[0]);
    ldb.run();
    WindowAdapter closer = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    };
    ij.addWindowListener(closer);
    ldb.cw.addWindowListener(closer);
  }

}
