/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
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

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.util.BFVirtualStack;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LibraryChecker;
import loci.plugins.util.VirtualImagePlus;
import loci.plugins.util.WindowTools;

/**
 * A plugin for splitting an image stack into separate
 * channels, focal planes and/or time points.
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

    Calibration calibration = imp.getCalibration();

    int sizeZ = imp.getNSlices();
    int sizeC = imp.getNChannels();
    int sizeT = imp.getNFrames();

    int slicesPerStack = stack.getSize();
    if (sliceZ) slicesPerStack /= sizeZ;
    if (sliceC) slicesPerStack /= sizeC;
    if (sliceT) slicesPerStack /= sizeT;

    int realSizeZ = sliceZ ? 1 : sizeZ;
    int realSizeC = sliceC ? 1 : sizeC;
    int realSizeT = sliceT ? 1 : sizeT;

    BFVirtualStack virtualStack = null;
    if (stack instanceof BFVirtualStack) {
      virtualStack = (BFVirtualStack) stack;
    }

    ImageStack[] newStacks = new ImageStack[stack.getSize() / slicesPerStack];
    for (int i=0; i<newStacks.length; i++) {
      newStacks[i] = makeStack(stack);
      if (newStacks[i] == null) return null;
    }

    int stackZ = sliceZ ? sizeZ : 1;
    int stackC = sliceC ? sizeC : 1;
    int stackT = sliceT ? sizeT : 1;

    int[][] planeIndexes = new int[newStacks.length][slicesPerStack];
    for (int i=0; i<sizeZ*sizeC*sizeT; i++) {
      int[] zct = FormatTools.getZCTCoords(stackOrder, sizeZ, sizeC, sizeT,
        stack.getSize(), i);
      int stackNdx = FormatTools.getIndex(stackOrder, stackZ, stackC, stackT,
        newStacks.length, sliceZ ? zct[0] : 0, sliceC ? zct[1] : 0,
        sliceT ? zct[2] : 0);
      String label = stack.getSliceLabel(i + 1);

      if (virtualStack != null) {
        ((BFVirtualStack) newStacks[stackNdx]).addSlice(label);
        int sliceNdx = FormatTools.getIndex(stackOrder, realSizeZ, realSizeC,
          realSizeT, slicesPerStack, sliceZ ? 0 : zct[0], sliceC ? 0 : zct[1],
          sliceT ? 0 : zct[2]);
        planeIndexes[stackNdx][sliceNdx] = i;
      }
      else {
        newStacks[stackNdx].addSlice(label, stack.getProcessor(i + 1));
      }
    }

    ImagePlus[] newImps = new ImagePlus[newStacks.length];
    for (int i=0; i<newStacks.length; i++) {
      if (virtualStack != null) {
        ((BFVirtualStack) newStacks[i]).setPlaneIndexes(planeIndexes[i]);
      }

      int[] zct = FormatTools.getZCTCoords(stackOrder, stackZ, stackC, stackT,
        newStacks.length, i);

      if (imp.isComposite()) {
        CompositeImage composite = (CompositeImage) imp;
        if (composite.getMode() == CompositeImage.COLOR) {
          LUT lut = composite.getChannelLut(zct[1] + 1);
          newStacks[i].setColorModel(lut);
        }
      }

      String title = imp.getTitle();

      title += " -";
      if (sliceZ) title += " Z=" + zct[0];
      if (sliceT) title += " T=" + zct[2];
      if (sliceC) title += " C=" + zct[1];

      ImagePlus p = null;

      if (virtualStack != null) {
        p = new VirtualImagePlus(title, newStacks[i]);
        ((VirtualImagePlus) p).setReader(virtualStack.getReader());
      }
      else {
        p = new ImagePlus(title, newStacks[i]);
      }

      p.setProperty(ImagePlusReader.PROP_SERIES,
              imp.getProperty(ImagePlusReader.PROP_SERIES));
      p.setProperty("Info", imp.getProperty("Info"));
      p.setDimensions(realSizeC, realSizeZ, realSizeT);
      p.setCalibration(calibration);
      p.setFileInfo(imp.getOriginalFileInfo());
      if (!p.isComposite()) {
        p.setOpenAsHyperStack(hyperstack);
      }
      if (imp.isComposite() && !sliceC) {
        p = reorder(p, stackOrder, "XYCZT");
        int mode = ((CompositeImage) imp).getMode();
        newImps[i] = new CompositeImage(p, mode);
      }
      else newImps[i] = p;

      double max = imp.getDisplayRangeMax();
      double min = imp.getDisplayRangeMin();

      newImps[i].setDisplayRange(min, max);

      if (imp.isComposite() && newImps[i].isComposite()) {
        for (int c=1; c<newImps[i].getNChannels(); c++) {
          LUT originalLut = ((CompositeImage) imp).getChannelLut(c);
          LUT lut = ((CompositeImage) newImps[i]).getChannelLut(c);
          lut.min = originalLut.min;
          lut.max = originalLut.max;
        }
      }
    }
    return newImps;
  }

  /** Reorder the given ImagePlus's stack. */
  public static ImagePlus reorder(ImagePlus imp, String origOrder,
    String newOrder)
  {
    ImageStack s = imp.getStack();
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());

    int z = imp.getNSlices();
    int c = imp.getNChannels();
    int t = imp.getNFrames();

    int stackSize = s.getSize();
    for (int i=0; i<stackSize; i++) {
      int ndx = FormatTools.getReorderedIndex(
        origOrder, newOrder, z, c, t, stackSize, i);
      newStack.addSlice(s.getSliceLabel(ndx + 1), s.getProcessor(ndx + 1));
    }
    ImagePlus p = new ImagePlus(imp.getTitle(), newStack);
    p.setDimensions(c, z, t);
    p.setCalibration(imp.getCalibration());
    p.setFileInfo(imp.getOriginalFileInfo());
    return p;
  }

  // -- PlugInFilter methods --

  @Override
  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  @Override
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

  /** Returns a new ImageStack using the given ImageStack as a template. */
  private ImageStack makeStack(ImageStack stack) {
    if (!(stack instanceof BFVirtualStack)) {
      return new ImageStack(stack.getWidth(), stack.getHeight());
    }

    BFVirtualStack virtualStack = (BFVirtualStack) stack;
    String path = virtualStack.getPath();
    ImageProcessorReader reader = virtualStack.getReader();

    try {
      return new BFVirtualStack(path, reader, false, false, false);
    }
    catch (FormatException e) {
      WindowTools.reportException(e);
    }
    catch (IOException e) {
      WindowTools.reportException(e);
    }
    return null;
  }

}
