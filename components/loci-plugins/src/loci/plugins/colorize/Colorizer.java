//
// Colorizer.java
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

package loci.plugins.colorize;

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

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Arrays;

import loci.formats.FormatTools;
import loci.plugins.BF;
import loci.plugins.in.ImportProcess;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.ImagePlusTools;
import loci.plugins.util.LibraryChecker;
import loci.plugins.util.WindowTools;

/**
 * A plugin for merging, colorizing and reordering image stacks.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/Colorizer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/Colorizer.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Colorizer implements PlugInFilter {

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
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;

    ImageStack stack = imp.getImageStack();

    if (stack.isVirtual()) {
      IJ.error("Colorizer plugin cannot be used with virtual stacks.\n" +
        "Please convert the virtual stack using Image>Duplicate.");
      return;
    }

    String stackOrder = "XYCZT";
    boolean color = false;
    boolean hyperstack = false;
    byte[][][] lut = null;
    String mergeOption = null;
    int series = 0;

    if (arg == null || arg.trim().equals("")) {
      // prompt for colorizing options

      GenericDialog gd = new GenericDialog("Colorizing options...");
      gd.addCheckbox("Merge to RGB", false);
      gd.addCheckbox("Colorize", false);
      gd.addChoice("Stack order", new String[] {"XYCZT", "XYCTZ", "XYZCT",
        "XYZTC", "XYTCZ", "XYTZC"}, "XYCZT");
      gd.addCheckbox("Open as hyperstack", true);
      gd.showDialog();

      if (gd.wasCanceled()) {
        canceled = true;
        return;
      }

      color = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
      hyperstack = gd.getNextBoolean();
    }
    else {
      series = Integer.parseInt(Macro.getValue(arg, "series", "0"));
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
      color = Boolean.valueOf(
        Macro.getValue(arg, "colorize", "false")).booleanValue();
      int colorNdx = Integer.parseInt(Macro.getValue(arg, "ndx", "-1"));
      if (color) lut = makeDefaultLut(imp.getNChannels(), colorNdx);
      mergeOption = Macro.getValue(arg, "merge_option", null);
      hyperstack = Boolean.valueOf(
        Macro.getValue(arg, "hyper_stack", "false")).booleanValue();
    }

    int nTimes = imp.getNFrames();
    int nSlices = imp.getNSlices();
    BF.status(false, "Running colorizer on " + (nTimes * nSlices) + " images");

    ImagePlus newImp = Colorizer.colorize(imp, color, stackOrder, lut, series, mergeOption, hyperstack);

    if (newImp != null) {
      imp.close(); // close original
      newImp.show();
    }

    lut = null;
  }

  // -- Colorizer API methods --

  public static ImagePlus colorize(ImagePlus imp, boolean color,
    String stackOrder, byte[][][] lut, int series,
    String mergeOption, boolean hyperstack)
  {
    ImageStack stack = imp.getImageStack();

    if (stack.isVirtual()) {
      throw new IllegalArgumentException("Cannot colorize virtual stack");
    }

    Calibration calibration = imp.getCalibration();

    int nChannels = imp.getNChannels();
    int nTimes = imp.getNFrames();
    int nSlices = imp.getNSlices();

    if (imp.isComposite() || stack.isRGB() || (nChannels == 1 && !color)) {
      return null;
    }

    ImagePlus newImp = new ImagePlus();

    if (color) {
      if (lut == null) lut = promptForColors(nChannels, series);

      if (nChannels > 1) {
        imp = ImagePlusTools.reorder(imp, stackOrder, "XYCZT");
        // NB: ImageJ v1.39+ is required for CompositeImage
        CompositeImage composite =
          new CompositeImage(imp, CompositeImage.COLOR);

        for (int i=0; i<nChannels; i++) {
          composite.setPosition(i + 1, 1, 1);
          if (lut != null) {
            LUT channelLut = new LUT(lut[i][0], lut[i][1], lut[i][2]);
            composite.setChannelLut(channelLut);
          }
        }
        newImp = composite;
        newImp.setPosition(1, 1, 1);
      }
      else {
        if (lut == null) return null;

        ImageStack newStack =
          new ImageStack(stack.getWidth(), stack.getHeight());
        for (int i=1; i<=stack.getSize(); i++) {
          newStack.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));
        }
        IndexColorModel model = new IndexColorModel(8, 256,
          lut[0][0], lut[0][1], lut[0][2]);
        newStack.setColorModel(model);
        newImp.setStack(imp.getTitle(), newStack);
      }
    }
    else {
      int type = imp.getType();

      if (nChannels < 4 && type == ImagePlus.GRAY8) {
        newImp = makeRGB(imp, stackOrder, newImp, stack, nChannels);
      }
      else if (nChannels <= 7 && type != ImagePlus.COLOR_256) {
        imp = ImagePlusTools.reorder(imp, stackOrder, "XYCZT");
        newImp = new CompositeImage(imp, CompositeImage.COMPOSITE);
      }
      else if (nChannels > 7) {
        // ask the user what they would like to do...

        int[] num = new int[6];
        for (int i=0; i<num.length; i++) {
          num[i] = stack.getSize() / (i + 2);
          if (num[i] * (i + 2) < stack.getSize()) num[i]++;
        }

        ImportProcess process = null;
        try {
          // TODO: remove dependency on importer package
          process = new ImportProcess();
        }
        catch (IOException exc) {
          WindowTools.reportException(exc);
        }

        if (mergeOption == null) {
          MergeDialog mergeDialog = new MergeDialog(process, num);
          int status = mergeDialog.showDialog();
          if (status == OptionsDialog.STATUS_OK) {
            mergeOption = process.getOptions().getMergeOption();
          }
        }

        if (mergeOption != null) {
          int ndx = mergeOption.indexOf("channels");
          if (ndx != -1) {
            int n = Integer.parseInt(mergeOption.substring(ndx - 2, ndx - 1));

            // add extra blank slices if the number of slices is not a
            // multiple of the number of channels
            if ((stack.getSize() % n) != 0) {
              int toAdd = n - (stack.getSize() % n);
              ImageProcessor blank =
                stack.getProcessor(stack.getSize()).duplicate();
              blank.setValue(0);
              blank.fill();
              for (int i=0; i<toAdd; i++) {
                stack.addSlice("", blank);
              }
              imp.setStack(imp.getTitle(), stack);
            }

            if (imp.getType() == ImagePlus.GRAY8 && n < 4) {
              newImp = makeRGB(imp, stackOrder, newImp, stack, n);
            }
            imp.setDimensions(n, imp.getNSlices()*num[n - 2],
              imp.getNFrames());
            imp = ImagePlusTools.reorder(imp, stackOrder, "XYCZT");
            newImp = new CompositeImage(imp, CompositeImage.COMPOSITE);
          }
        }
      }
    }

    if (newImp != null) {
      newImp.setTitle(imp.getTitle());
      newImp.setProperty("Info", imp.getProperty("Info"));
      if (!newImp.isComposite()) {
        newImp.setDimensions(newImp.getStackSize() / (nSlices * nTimes),
          nSlices, nTimes);
      }
      newImp.setCalibration(calibration);
      newImp.setFileInfo(imp.getOriginalFileInfo());
      if (!newImp.isComposite()) newImp.setOpenAsHyperStack(hyperstack);
    }

    return newImp;
  }

  public static byte[][][] makeDefaultLut(int nChannels, int index) {
    if (index < 0 || index >= 3) return null;

    byte[][][] lut = new byte[nChannels][3][256];
    for (int channel = 0; channel < nChannels; channel++) {
      if (index + channel >= lut[channel].length) break;
      for (int q = 0; q < lut[channel][index + channel].length; q++) {
        lut[channel][index + channel][q] = (byte) q;
      }
    }
    return lut;
  }

  // -- Helper methods --

  private static ImagePlus makeRGB(ImagePlus imp, String stackOrder, ImagePlus ip, ImageStack s, int c) {
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
        ImagePlusTools.makeRGB(processors[i]).getProcessor());
      BF.progress(false, i, indices[0].length);
    }

    ip.setStack(ip.getTitle(), newStack);
    return ip;
  }

  private static byte[][] promptForColor(int channel, int series) {
    CustomColorChooser chooser = new CustomColorChooser(
      "Color Chooser - Channel " + channel, null, series, channel);

    Color color = chooser.getColor();
    if (color == null) return null;
    double redIncrement = ((double) color.getRed()) / 255;
    double greenIncrement = ((double) color.getGreen()) / 255;
    double blueIncrement = ((double) color.getBlue()) / 255;

    byte[][] channelLut = new byte[3][256];
    for (int i=0; i<256; i++) {
      channelLut[0][i] = (byte) (i * redIncrement);
      channelLut[1][i] = (byte) (i * greenIncrement);
      channelLut[2][i] = (byte) (i * blueIncrement);
    }
    return channelLut;
  }

  private static byte[][][] promptForColors(int nChannels, int series) {
    byte[][][] lut = new byte[nChannels][][];
    for (int i=0; i<nChannels; i++) {
      lut[i] = promptForColor(i, series);
      if (lut[i] == null) return null;
    }
    return lut;
  }

}
