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

import ij.*;
import ij.gui.ColorChooser;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.ColorPicker;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.util.Tools;
import java.awt.*;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.FormatTools;
import loci.plugins.importer.ImporterOptions;//FIXME
import loci.plugins.util.Util;

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

      merge = gd.getNextBoolean();
      color = gd.getNextBoolean();
      stackOrder = gd.getNextChoice();
      hyperstack = gd.getNextBoolean();

      if (color && lut == null) doPrompt = true;
    }
    else {
      series = Integer.parseInt(Macro.getValue(arg, "series", "0"));
      stackOrder = Macro.getValue(arg, "stack_order", "XYCZT");
      merge =
        Boolean.valueOf(Macro.getValue(arg, "merge", "true")).booleanValue();
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
      if (Util.checkVersion("1.39l", Util.COMPOSITE_MSG) && nChannels > 1) {
        // use reflection to construct CompositeImage,
        // in case ImageJ version is too old
        try {
          r.setVar("imp", Util.reorder(imp, stackOrder, "XYCZT"));
          r.exec("composite = new CompositeImage(imp, CompositeImage.COLOR)");
          r.setVar("1", 1);
          for (int i=0; i<nChannels; i++) {
            r.setVar("channel", i + 1);
            r.exec("composite.setPosition(channel, 1, 1)");
            if (doPrompt) {
              promptForColor(i);
            }
            if (lut != null) {
              LUT channelLut = new LUT(lut[i][0], lut[i][1], lut[i][2]);
              r.setVar("lut", channelLut);
              r.exec("composite.setChannelLut(lut)");
            }
          }
          newImp = (ImagePlus) r.getVar("composite");
          newImp.setPosition(1, 1, 1);
        }
        catch (ReflectException exc) {
          ByteArrayOutputStream s = new ByteArrayOutputStream();
          exc.printStackTrace(new PrintStream(s));
          IJ.error(s.toString());
        }
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

        ImporterOptions options = new ImporterOptions();//FIXME

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
              // TODO - Add spectral projection logic here (see ticket #86).
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
        Util.makeRGB(processors[i]).getProcessor());
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

  class ColorPanel extends Panel {
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
