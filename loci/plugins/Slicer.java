//
// Slicer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

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

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import loci.formats.*;

/**
 * A plugin for splitting an image stack into separate channels, focal planes
 * and/or time points.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Slicer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Slicer.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Slicer implements PlugInFilter {

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Plugin options. */
  private String arg;

  /** Current image stack. */
  private ImagePlus imp;

  // -- PlugInFilter API methods --

  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  public void run(ImageProcessor ip) {
    boolean sliceZ = false;
    boolean sliceC = false;
    boolean sliceT = false;
    String stackOrder = null;
    boolean keepOriginal = false;
    boolean hyperstack = false;

    if (arg == null || arg.trim().equals("")) {
      // prompt for slicing options

      GenericDialog gd = new GenericDialog("Slicing options...");
      gd.addCheckbox("Split channels", false);
      gd.addCheckbox("Split Z slices", false);
      gd.addCheckbox("Split timepoints", false);
      gd.addCheckbox("Keep original stack", false);
      gd.addCheckbox("Open as HyperStack", false);
      gd.addChoice("Stack order", new String[] {"XYCZT", "XYCTZ", "XYZCT",
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
      hyperstack = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
    }
    else {
      sliceZ =
        Boolean.valueOf(Macro.getValue(arg, "slice_z", "false")).booleanValue();
      sliceC =
        Boolean.valueOf(Macro.getValue(arg, "slice_c", "false")).booleanValue();
      sliceT =
        Boolean.valueOf(Macro.getValue(arg, "slice_t", "false")).booleanValue();
      keepOriginal = Boolean.valueOf(Macro.getValue(arg,
        "keep_original", "false")).booleanValue();
      hyperstack = Boolean.valueOf(
        Macro.getValue(arg, "hyper_stack", "false")).booleanValue();
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
    }

    ImageStack stack = imp.getImageStack();
    Calibration calibration = imp.getCalibration();

    Class c = null;
    try {
      c = Class.forName("ij.CompositeImage");
    }
    catch (ClassNotFoundException e) { }
    if (imp.getClass().equals(c)) return;

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

    for (int i=0; i<newStacks.length; i++) {
      String title = imp.getTitle();

      int[] zct = FormatTools.getZCTCoords(stackOrder, sliceZ ? sizeZ : 1,
        sliceC ? sizeC : 1, sliceT ? sizeT : 1, newStacks.length, i);

      if (sliceZ) title += " - Z=" + zct[0];
      if (sliceT) {
        if (sliceZ) title += " T=" + zct[2];
        else title += " - T=" + zct[2];
      }
      if (sliceC) {
        if (sliceZ || sliceT) title += " C=" + zct[1];
        else title += " - C=" + zct[1];
      }

      ImagePlus p = new ImagePlus(title, newStacks[i]);
      p.setProperty("Info", imp.getProperty("Info"));
      p.setDimensions(sliceC ? 1 : sizeC, sliceZ ? 1 : sizeZ,
        sliceT ? 1 : sizeT);
      p.setCalibration(calibration);
      p.setFileInfo(imp.getOriginalFileInfo());
      if (IJ.getVersion().compareTo("1.39l") >= 0 &&
        !(p instanceof CompositeImage))
      {
        p.setOpenAsHyperStack(hyperstack);
      }
      if (imp.getClass().equals(c) && !sliceC &&
        Util.checkVersion("1.39l", Util.COMPOSITE_MSG))
      {
        try {
          ReflectedUniverse r = new ReflectedUniverse();
          r.exec("import ij.CompositeImage");
          r.setVar("p", Util.reorder(p, stackOrder, "XYCZT"));
          r.exec("c = new CompositeImage(p, CompositeImage.COMPOSITE)");
          r.exec("c.show()");
        }
        catch (ReflectException e) {
          ByteArrayOutputStream s = new ByteArrayOutputStream();
          e.printStackTrace(new PrintStream(s));
          IJ.error(s.toString());
        }
      }
      else p.show();
    }
    if (!keepOriginal) imp.close();
  }

}
