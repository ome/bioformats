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
import ij.util.Tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import loci.formats.FormatTools;
import loci.plugins.importer.ImporterOptions;
import loci.plugins.importer.MergeDialog;
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

  private String stackOrder;
  private boolean color;
  private boolean hyperstack;
  private byte[][][] lut;
  private String mergeOption;

  private int series = 0;

  // -- PlugInFilter API methods --

  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  public void run(ImageProcessor ip) {
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;

    stackOrder = "XYCZT";

    boolean doPrompt = false;

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

      color = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
      hyperstack = gd.getNextBoolean();

      if (color && lut == null) doPrompt = true;
    }
    else {
      series = Integer.parseInt(Macro.getValue(arg, "series", "0"));
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
      color = Boolean.valueOf(
        Macro.getValue(arg, "colorize", "false")).booleanValue();
      int colorNdx = Integer.parseInt(Macro.getValue(arg, "ndx", "-1"));
      if (color) {
        if (colorNdx >= 0 && colorNdx < 3) {
          lut = new byte[imp.getNChannels()][3][256];
          for (int channel=0; channel<lut.length; channel++) {
            if (colorNdx + channel >= lut[channel].length) break;
            for (int q=0; q<lut[channel][colorNdx + channel].length; q++) {
              lut[channel][colorNdx + channel][q] = (byte) q;
            }
          }
        }
        else if (lut == null) doPrompt = true;
      }
      mergeOption = Macro.getValue(arg, "merge_option", null);
      hyperstack = Boolean.valueOf(
        Macro.getValue(arg, "hyper_stack", "false")).booleanValue();
    }

    ImageStack stack = imp.getImageStack();

    if (stack.isVirtual()) {
      IJ.error("Colorizer plugin cannot be used with virtual stacks.\n" +
        "Please convert the virtual stack using Image>Duplicate.");
      return;
    }

    Calibration calibration = imp.getCalibration();

    int nChannels = imp.getNChannels();
    int nTimes = imp.getNFrames();
    int nSlices = imp.getNSlices();

    if (imp.isComposite() || stack.isRGB() || (nChannels == 1 && !color)) {
      return;
    }

    ImagePlus newImp = new ImagePlus();
    boolean closeOriginal = true;

    if (color) {
      if (nChannels > 1) {
        imp = ImagePlusTools.reorder(imp, stackOrder, "XYCZT");
        // NB: ImageJ v1.39+ is required for CompositeImage
        CompositeImage composite =
          new CompositeImage(imp, CompositeImage.COLOR);
        for (int i=0; i<nChannels; i++) {
          composite.setPosition(i + 1, 1, 1);
          if (doPrompt) promptForColor(i);
          if (lut != null) {
            LUT channelLut = new LUT(lut[i][0], lut[i][1], lut[i][2]);
            composite.setChannelLut(channelLut);
          }
        }
        newImp = composite;
        newImp.setPosition(1, 1, 1);
      }
      else {
        ImageStack newStack =
          new ImageStack(stack.getWidth(), stack.getHeight());
        for (int i=1; i<=stack.getSize(); i++) {
          newStack.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));
        }

        if (doPrompt) {
          promptForColor(0);
        }
        if (lut == null) return;

        IndexColorModel model = new IndexColorModel(8, 256, lut[0][0],
          lut[0][1], lut[0][2]);
        newStack.setColorModel(model);
        newImp.setStack(imp.getTitle(), newStack);
      }
    }
    else {
      int type = imp.getType();

      if (nChannels < 4 && type == ImagePlus.GRAY8) {
        newImp = makeRGB(newImp, stack, nChannels);
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

        ImporterOptions options = null;
        try {
          // TODO: remove dependency on importer package
          options = new ImporterOptions();
        }
        catch (IOException exc) {
          WindowTools.reportException(exc);
        }

        if (mergeOption == null) {
          MergeDialog mergeDialog = new MergeDialog(options, num);
          int status = mergeDialog.showDialog();
          if (status == OptionsDialog.STATUS_OK) {
            mergeOption = options.getMergeOption();
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
              newImp = makeRGB(newImp, stack, n);
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
      newImp.show();
    }
    if (closeOriginal) imp.close();
    lut = null;
  }

  // -- Colorizer API methods --

  public void setLookupTable(byte[][] lut, int channel) {
    if (this.lut == null) {
      this.lut = new byte[imp.getNChannels()][][];
    }
    if (channel < this.lut.length) this.lut[channel] = lut;
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
        ImagePlusTools.makeRGB(processors[i]).getProcessor());
    }

    ip.setStack(ip.getTitle(), newStack);
    return ip;
  }

  private void promptForColor(int channel) {
    CustomColorChooser chooser = new CustomColorChooser(
      "Color Chooser - Channel " + channel, null, series, channel);

    Color color = chooser.getColor();
    if (color == null) return;
    double redIncrement = ((double) color.getRed()) / 255;
    double greenIncrement = ((double) color.getGreen()) / 255;
    double blueIncrement = ((double) color.getBlue()) / 255;

    if (lut == null) lut = new byte[imp.getNChannels()][3][256];
    for (int i=0; i<256; i++) {
      lut[channel][0][i] = (byte) (i * redIncrement);
      lut[channel][1][i] = (byte) (i * greenIncrement);
      lut[channel][2][i] = (byte) (i * blueIncrement);
    }
  }

  // -- Helper class --

  /**
   * Adapted from ij.gui.ColorChooser.  ColorChooser is not used because
   * there is no way to change the slider labels - this means that we can't
   * record macros in which custom colors are chosen for multiple channels.
   */
  class CustomColorChooser implements TextListener {
    Vector colors;
    ColorPanel panel;
    Color initialColor;
    int red, green, blue;
    String title;

    private int series, channel;

    public CustomColorChooser(String title, Color initialColor, int series,
      int channel)
    {
        this.title = title;
        if (initialColor == null) initialColor = Color.BLACK;
        this.initialColor = initialColor;
        red = initialColor.getRed();
        green = initialColor.getGreen();
        blue = initialColor.getBlue();
        this.series = series;
        this.channel = channel;
    }

    // -- ColorChooser API methods --

    /**
     * Displays a color selection dialog and returns the color
     *  selected by the user.
     */
    public Color getColor() {
      GenericDialog gd = new GenericDialog(title);
      gd.addSlider(makeLabel("Red:"), 0, 255, red);
      gd.addSlider(makeLabel("Green:"), 0, 255, green);
      gd.addSlider(makeLabel("Blue:"), 0, 255, blue);
      panel = new ColorPanel(initialColor);
      gd.addPanel(panel, GridBagConstraints.CENTER, new Insets(10, 0, 0, 0));
      colors = gd.getNumericFields();
      for (int i=0; i<colors.size(); i++) {
        ((TextField) colors.elementAt(i)).addTextListener(this);
      }
      gd.showDialog();
      if (gd.wasCanceled()) return null;
      int red = (int) gd.getNextNumber();
      int green = (int) gd.getNextNumber();
      int blue = (int) gd.getNextNumber();
      return new Color(red, green, blue);
    }

    public void textValueChanged(TextEvent e) {
      int red = getColorValue(0);
      int green = getColorValue(1);
      int blue = getColorValue(2);
      panel.setColor(new Color(red, green, blue));
      panel.repaint();
    }

    // -- Helper methods --

    private int getColorValue(int index) {
      int color =
        (int) Tools.parseDouble(((TextField) colors.get(index)).getText());
      if (color < 0) color = 0;
      if (color > 255) color = 255;
      return color;
    }

    private String makeLabel(String baseLabel) {
      return "Series_" + series + "_Channel_" + channel + "_" + baseLabel;
    }
  }

  static class ColorPanel extends Panel {
    private static final int WIDTH = 100, HEIGHT = 50;
    private Color c;

    public ColorPanel(Color c) {
      this.c = c;
    }

    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, HEIGHT);
    }

    void setColor(Color c) { this.c = c; }

    public Dimension getMinimumSize() {
      return new Dimension(WIDTH, HEIGHT);
    }

    public void paint(Graphics g) {
      g.setColor(c);
      g.fillRect(0, 0, WIDTH, HEIGHT);
      g.setColor(Color.black);
      g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
    }

  }

}
