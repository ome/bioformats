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
import ij.process.ImageProcessor;
import java.io.ByteArrayOutputStream;
import loci.plugins.Util;
import loci.formats.*;
import loci.formats.cache.*;
import loci.formats.ome.OMEXMLMetadata;

/**
 * LociDataBrowser is a plugin for ImageJ that allows for browsing of 4D
 * image data (stacks of image planes over time) with two-channel support.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/browser/LociDataBrowser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/browser/LociDataBrowser.java">SVN</a></dd></dl>
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

  /** The CustomWindow used to display data. */
  protected CustomWindow cw;

  /** The current file name. */
  protected String id;

  /** Number of Z, T and C positions. */
  protected int numZ, numT, numC;

  /** Lengths of all dimensional axes; lengths[0] equals image depth. */
  protected int[] lengths;

  /** Indices into lengths array for Z, T and C. */
  protected int zIndex, tIndex, cIndex;

  /** Whether stack is accessed from disk as needed. */
  protected boolean virtual;

  /** Series to use in a multi-series file. */
  protected int series;

  protected Cache cache;
  private ImageStack stack;
  protected boolean merged;
  protected boolean colorize;

  // -- Constructors --

  public LociDataBrowser(IFormatReader r, String name, int seriesNo,
    boolean merged, boolean colorize)
  {
    this.merged = merged;
    this.colorize = colorize;

    if (r == null) r = new ChannelSeparator();
    reader = new DimensionSwapper(r);
    id = name;
    series = seriesNo;
    virtual = true;
    try {
      reader.setId(id);
      reader.setSeries(series);
    }
    catch (Exception exc) {
      dumpException(exc);
    }

    if (this.merged) {
      int c = reader.getSizeC();
      int type = reader.getPixelType();

      if (c > 3 || (type != FormatTools.INT8 && type != FormatTools.UINT8)) {
        this.merged = false;
      }
    }
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
    try {
      String order = reader.getDimensionOrder().substring(2);
      setDimensions(reader.getSizeZ(), merged ? 1 : reader.getEffectiveSizeC(),
        reader.getSizeT(), order.indexOf("Z"), order.indexOf("C"),
        order.indexOf("T"));
    }
    catch (Exception exc) {
      dumpException(exc);
    }
  }

  /** Gets the slice number for the given Z, T and C indices. */
  public int getIndex(int z, int t, int c) {
    int result = -23;
    synchronized (reader) {
      result = reader.getIndex(z, c, t);
    }
    return result;
  }

  public static void dumpException(Exception exc) {
    LogTools.trace(exc);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
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
    if (numC <= 3 && (reader.getPixelType() == FormatTools.INT8 ||
      reader.getPixelType() == FormatTools.UINT8))
    {
      merged = !merged;
    }
    else merged = false;
    run();
  }

  public boolean isMerged() {
    return merged;
  }

  public void run() {
    stack = null;
    try {
      ImagePlusWrapper ipw = null;

      // process input
      lengths = new int[3];

      ImagePlus imp = null;

      if (virtual) {
        synchronized (reader) {
          int num = reader.getImageCount();

          // assemble axis lengths in the proper order
          int z = reader.getSizeZ();
          int c = reader.getSizeC();
          int t = reader.getSizeT();
          int tAxis = -1;
          int zAxis = -1;
          int cAxis = -1;
          String order = reader.getDimensionOrder();
          for (int i=0; i<lengths.length; i++) {
            char ch = order.charAt(i + 2);
            lengths[i] = ch == 'Z' ? z : ch == 'C' ? c : t;
            if (ch == 'T') tAxis = i;
            else if (ch == 'Z') zAxis = i;
            else cAxis = i;
          }

          ImageProcessorSource source = new ImageProcessorSource(reader);
          CrosshairStrategy strategy = new CrosshairStrategy(lengths);

          strategy.setRange(100, tAxis);
          for (int i=0; i<lengths.length; i++) {
            strategy.setOrder(ICacheStrategy.FORWARD_ORDER, i);
          }
          strategy.setPriority(ICacheStrategy.MAX_PRIORITY, tAxis);
          strategy.setPriority(ICacheStrategy.NORMAL_PRIORITY, zAxis);
          strategy.setPriority(ICacheStrategy.MIN_PRIORITY, cAxis);

          cache = new Cache(strategy, source);
          cache.setCurrentPos(new int[] {0, 0, 0});

          try {
            setDimensions();

            // CTR: stack must not be null
            int sizeX = reader.getSizeX();
            int sizeY = reader.getSizeY();
            stack = new ImageStack(sizeX, sizeY);
            // CTR: must add at least one image to the stack
            stack.addSlice(id + " : 1",
              (ImageProcessor) cache.getObject(new int[] {0, 0, 0}));
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
        imp = new ImagePlus(id, stack);
      }
      else {
        cache = null;
        ipw = new ImagePlusWrapper(id, reader);
        setDimensions();
        imp = ipw.getImagePlus();

        if (imp.getStackSize() != numZ * numT * numC) {
          LogTools.println("Error, stack size mismatch with dimension " +
            "sizes: stackSize=" + imp.getStackSize() + ", numZ=" + numZ +
            ", numT=" + numT + ", numC=" + numC);
        }
      }

      FileInfo fi = imp.getOriginalFileInfo();
      if (fi == null) fi = new FileInfo();

      MetadataStore store = virtual ? reader.getMetadataStore() : ipw.store;
      if (store instanceof OMEXMLMetadata) {
        fi.description = ((OMEXMLMetadata) store).dumpXML();
        Util.applyCalibration((MetadataRetrieve) store, imp, series);
      }

      imp.setFileInfo(fi);
      show(imp);
    }
    catch (Exception exc) { dumpException(exc); }
  }

}
