//
// Slicer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.LUT;

import loci.formats.FormatTools;
import loci.plugins.util.ImagePlusTools;
import loci.plugins.util.LibraryChecker;

/**
 * A plugin for splitting an image stack into separate
 * channels, focal planes and/or time points.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/Slicer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/Slicer.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Slicer implements PlugInFilter {

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Plugin options. */
  private String arg;

  /** Current image stack. */
  private ImagePlus imp;
  
  // -- Slicer methods --
  
  public ImagePlus[] reslice(ImagePlus imp,
    boolean sliceC, boolean sliceZ, boolean sliceT,
    String stackOrder)
  {
    ImageStack stack = imp.getImageStack();
    boolean hyperstack = imp.isHyperStack();

    if (stack.isVirtual()) {
      throw new IllegalArgumentException("Cannot reslice virtual stacks");
    }

    Calibration calibration = imp.getCalibration();

    int sizeZ = imp.getNSlices();
    int sizeC = imp.getNChannels();
    int sizeT = imp.getNFrames();

    int slicesPerStack = stack.getSize();
    if (sliceZ) slicesPerStack /= sizeZ;
    if (sliceC) slicesPerStack /= sizeC;
    if (sliceT) slicesPerStack /= sizeT;

    ImageStack[] newStacks =
      new ImageStack[stack.getSize() / slicesPerStack];
    for (int i=0; i<newStacks.length; i++) {
      newStacks[i] = new ImageStack(stack.getWidth(), stack.getHeight());
    }

    for (int i=0; i<sizeZ*sizeC*sizeT; i++) {
      int[] zct = FormatTools.getZCTCoords(stackOrder, sizeZ, sizeC, sizeT,
        stack.getSize(), i);
      int stackNdx = FormatTools.getIndex(stackOrder, sliceZ ? sizeZ : 1,
        sliceC ? sizeC : 1, sliceT ? sizeT : 1, newStacks.length,
        sliceZ ? zct[0] : 0, sliceC ? zct[1] : 0, sliceT ? zct[2] : 0);

      newStacks[stackNdx].addSlice(stack.getSliceLabel(i + 1),
        stack.getProcessor(i + 1));
    }

    ImagePlus[] newImps = new ImagePlus[newStacks.length];
    for (int i=0; i<newStacks.length; i++) {
      int[] zct = FormatTools.getZCTCoords(stackOrder, sliceZ ? sizeZ : 1,
        sliceC ? sizeC : 1, sliceT ? sizeT : 1, newStacks.length, i);

      if (imp.isComposite()) {
        LUT lut = ((CompositeImage) imp).getChannelLut(zct[1] + 1);
        newStacks[i].setColorModel(lut);
      }

      String title = imp.getTitle();

      title += " -";
      if (sliceZ) title += " Z=" + zct[0];
      if (sliceT) title += " T=" + zct[2];
      if (sliceC) title += " C=" + zct[1];

      ImagePlus p = new ImagePlus(title, newStacks[i]);
      p.setProperty("Info", imp.getProperty("Info"));
      p.setDimensions(sliceC ? 1 : sizeC, sliceZ ? 1 : sizeZ,
        sliceT ? 1 : sizeT);
      p.setCalibration(calibration);
      p.setFileInfo(imp.getOriginalFileInfo());
      if (!p.isComposite()) {
        p.setOpenAsHyperStack(hyperstack);
      }
      if (imp.isComposite() && !sliceC) {
        p = ImagePlusTools.reorder(p, stackOrder, "XYCZT");
        int mode = ((CompositeImage) imp).getMode();
        newImps[i] = new CompositeImage(p, mode);
      }
      else newImps[i] = p;
    }
    return newImps;
  }

  // -- PlugInFilter methods --

  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  public void run(ImageProcessor ip) {
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;

    boolean sliceC = false;
    boolean sliceZ = false;
    boolean sliceT = false;
    String stackOrder = null;
    boolean keepOriginal = false;

    if (arg == null || arg.trim().equals("")) {
      // prompt for slicing options

      GenericDialog gd = new GenericDialog("Slicing options...");
      gd.addCheckbox("Split_channels", false);
      gd.addCheckbox("Split_Z slices", false);
      gd.addCheckbox("Split_timepoints", false);
      gd.addCheckbox("Keep_original stack", false);
      gd.addChoice("Stack_order", new String[] {"XYCZT", "XYCTZ", "XYZCT",
        "XYZTC", "XYTCZ", "XYTZC"}, "XYCZT");
      gd.showDialog();

      if (gd.wasCanceled()) {
        canceled = true;
        return;
      }

      sliceC = gd.getNextBoolean();
      sliceZ = gd.getNextBoolean();
      sliceT = gd.getNextBoolean();
      keepOriginal = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
    }
    else {
      sliceC = getBooleanValue("split_channels");
      sliceZ = getBooleanValue("split_z");
      sliceT = getBooleanValue("split_timepoints");
      keepOriginal = getBooleanValue("keep_original");
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
    }

    if (imp.getImageStack().isVirtual()) {
      IJ.error("Slicer plugin cannot be used with virtual stacks.\n" +
      "Please convert the virtual stack using Image>Duplicate.");
      return;
    }
    ImagePlus[] newImps = reslice(imp,
      sliceC, sliceZ, sliceT, stackOrder);
    if (!keepOriginal) imp.close();
    for (ImagePlus imp : newImps) imp.show();
  }

  // -- Helper methods --

  /** Gets the value of the given macro key as a boolean. */
  private boolean getBooleanValue(String key) {
    return Boolean.valueOf(Macro.getValue(arg, key, "false"));
  }

}
