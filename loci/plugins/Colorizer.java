//
// Colorizer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
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
import ij.process.*;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import loci.formats.*;

public class Colorizer implements PlugInFilter {

  // -- Static fields --

  private static ReflectedUniverse r = createReflectedUniverse();
  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ij.CompositeImage");
    }
    catch (ReflectException exc) { }
    return r;
  }

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Plugin options. */
  private String arg;

  /** Current image stack. */
  private ImagePlus imp;

  private String stackOrder;
  private boolean merge;
  private boolean color;
  private boolean hyperstack;
  private int colorNdx;
  private String mergeOption;

  // -- PlugInFilter API methods --

  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  public void run(ImageProcessor ip) {
    stackOrder = "XYCZT";

    if (arg == null || arg.trim().equals("")) {
      // prompt for colorizing options

      GenericDialog gd = new GenericDialog("Colorizing options...");
      gd.addCheckbox("Merge to RGB", false);
      gd.addCheckbox("Colorize", false);
      gd.addChoice("Stack order", new String[] {"XYCZT", "XYCTZ", "XYZCT",
        "XYZTC", "XYTCZ", "XYTZC"}, "XYCZT");
      gd.addCheckbox("Open as HyperStack", false);
      gd.showDialog();

      if (gd.wasCanceled()) {
        canceled = true;
        return;
      }

      merge = gd.getNextBoolean();
      color = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
      hyperstack = gd.getNextBoolean();

      if (color) {
        gd = new GenericDialog("Choose color...");
        gd.addChoice("Color", new String[] {"Red", "Green", "Blue"}, "Red");
        gd.showDialog();
        colorNdx = gd.getNextChoiceIndex();
      }
    }
    else {
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
      merge = Boolean.parseBoolean(Macro.getValue(arg, "merge", "true"));
      color = Boolean.parseBoolean(Macro.getValue(arg, "colorize", "false"));
      colorNdx = Integer.parseInt(Macro.getValue(arg, "ndx", "0"));
      mergeOption = Macro.getValue(arg, "merge_option", null);
      hyperstack =
        Boolean.parseBoolean(Macro.getValue(arg, "hyper_stack", "false"));
    }

    ImageStack stack = imp.getImageStack();
    Calibration calibration = imp.getCalibration();

    int nChannels = imp.getNChannels();
    int nTimes = imp.getNFrames();
    int nSlices = imp.getNSlices();

    Class c = null;
    try {
      c = Class.forName("ij.CompositeImage");
    }
    catch (ClassNotFoundException e) { }

    if (imp.getClass().equals(c) || stack.isRGB() ||
      (nChannels == 1 && !color))
    {
      return;
    }

    ImagePlus newImp = new ImagePlus();
    boolean closeOriginal = true;

    if (color) {
      ImageStack newStack = new ImageStack(stack.getWidth(), stack.getHeight());
      for (int i=1; i<=stack.getSize(); i++) {
        newStack.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));
      }
      byte[] lut = new byte[256];
      byte[] blank = new byte[256];
      Arrays.fill(blank, (byte) 0);
      for (int i=0; i<lut.length; i++) {
        lut[i] = (byte) i;
      }

      IndexColorModel model = new IndexColorModel(8, 256,
        colorNdx == 0 ? lut : blank, colorNdx == 1 ? lut : blank,
        colorNdx == 2 ? lut : blank);
      newStack.setColorModel(model);
      newImp.setStack(imp.getTitle(), newStack);
    }
    else {
      int type = imp.getType();

      if (nChannels < 4 && type == ImagePlus.GRAY8) {
        newImp = makeRGB(newImp, stack, nChannels);
      }
      else if (nChannels <= 7 && type != ImagePlus.COLOR_256) {
        if (Util.checkVersion("1.39l", Util.COMPOSITE_MSG)) {
          // use reflection to construct CompositeImage,
          // in case ImageJ version is too old
          try {
            r.setVar("imp", Util.reorder(imp, stackOrder, "XYCZT"));
            newImp = (ImagePlus)
              r.exec("new CompositeImage(imp, CompositeImage.COMPOSITE)");
          }
          catch (ReflectException exc) {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            exc.printStackTrace(new PrintStream(s));
            IJ.error(s.toString());
          }
        }
        else {
          closeOriginal = false;
          newImp = null;
        }
      }
      else if (nChannels > 7) {
        // ask the user what they would like to do...

        int planes1 = stack.getSize() / 2;
        if (planes1 * 2 < stack.getSize()) planes1++;
        int planes2 = stack.getSize() / 3;
        if (planes2 * 3 < stack.getSize()) planes2++;
        int planes3 = stack.getSize() / 4;
        if (planes3 * 4 < stack.getSize()) planes3++;

        int[] num = new int[6];
        for (int i=0; i<num.length; i++) {
          num[i] = stack.getSize() / (i + 2);
          if (num[i] * (i + 2) < stack.getSize()) num[i]++;
        }

        ImporterOptions options = new ImporterOptions();

        boolean spectral =
          stack.getSliceLabel(1).indexOf(FormatTools.SPECTRA) != -1;

        if (mergeOption == null) {
          int status = options.promptMergeOption(num, spectral);
          if (status == ImporterOptions.STATUS_OK) {
            mergeOption = options.getMergeOption();
          }
          // TEMP - remove this once spectral projection is implemented
          while (mergeOption.equals(ImporterOptions.MERGE_PROJECTION)) {
            IJ.error("Spectral projection has not been implemented.");
            status = options.promptMergeOption(num, spectral);
            if (status == ImporterOptions.STATUS_OK) {
              mergeOption = options.getMergeOption();
            }
          }
        }

        try {
          if (mergeOption != null) {
            int ndx = mergeOption.indexOf("channels");
            if (ndx != -1) {
              int n = Integer.parseInt(mergeOption.substring(ndx - 2, ndx - 1));
              if (imp.getType() == ImagePlus.GRAY8 && n < 4) {
                newImp = makeRGB(newImp, stack, n);
              }
              else if (Util.checkVersion("1.39l", Util.COMPOSITE_MSG)) {
                imp.setDimensions(n, imp.getNSlices()*num[n - 2],
                  imp.getNFrames());
                r.setVar("imp", Util.reorder(imp, stackOrder, "XYCZT"));
                r.exec("mode = CompositeImage.COMPOSITE");
                r.exec("newImp = new CompositeImage(imp, mode)");
                newImp = (ImagePlus) r.getVar("newImp");
              }
              else {
                closeOriginal = false;
                newImp = null;
              }
            }
            else if (mergeOption.equals(ImporterOptions.MERGE_PROJECTION)) {
              // TODO
              // Add spectral projection logic here (see ticket #86).
            }
            else closeOriginal = false;
          }
        }
        catch (ReflectException e) {
          ByteArrayOutputStream s = new ByteArrayOutputStream();
          e.printStackTrace(new PrintStream(s));
          IJ.error(s.toString());
        }
      }
    }

    if (newImp != null) {
      newImp.setTitle(imp.getTitle());
      newImp.setProperty("Info", imp.getProperty("Info"));
      if (!newImp.getClass().equals(c)) {
        newImp.setDimensions(newImp.getStackSize() / (nSlices * nTimes),
          nSlices, nTimes);
      }
      newImp.setCalibration(calibration);
      newImp.setFileInfo(imp.getOriginalFileInfo());
      if (IJ.getVersion().compareTo("1.39l") >= 0 &&
        !(newImp instanceof CompositeImage))
      {
        newImp.setOpenAsHyperStack(hyperstack);
      }
      newImp.show();
    }
    if (closeOriginal) imp.close();
  }

  // -- Helper methods --

  private ImagePlus makeRGB(ImagePlus ip, ImageStack s, int c) {
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());

    int z = imp.getNSlices();
    int t = imp.getNFrames();

    t *= imp.getNChannels() / c;

    int[][] indices = new int[c][s.getSize() / c];
    ImageProcessor[][] processors = new ImageProcessor[s.getSize() / c][c];
    int[] pt = new int[c];
    Arrays.fill(pt, 0);

    for (int i=0; i<s.getSize(); i++) {
      int[] zct = FormatTools.getZCTCoords(stackOrder, z, c, t, s.getSize(), i);
      indices[zct[1]][pt[zct[1]]] = i + 1;
      processors[pt[zct[1]]++][zct[1]] = s.getProcessor(i + 1);
    }

    for (int i=0; i<indices[0].length; i++) {
      newStack.addSlice(s.getSliceLabel(indices[indices.length - 1][i]),
        Util.makeRGB(processors[i]).getProcessor());
    }

    ip.setStack(ip.getTitle(), newStack);
    return ip;
  }

}
