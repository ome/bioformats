//
// SlimData.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

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

package loci.slim;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.common.DataTools;
import loci.slim.fit.*;

/**
 * Data structure for housing raw experimental data, as well as various
 * derived data such as subsamplings and curve estimates.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/SlimData.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/SlimData.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SlimData implements ActionListener, CurveListener {

  // -- Constants --

  public static final String TITLE = "SLIM Plotter";

  // -- Fields --

  /** Actual data values, dimensioned [channel][row][column][bin]. */
  protected int[][][][] data;

  /** Per-pixel curve estimates, dimensioned [channels]. */
  protected CurveCollection[] curves;

  // data parameters
  protected int width, height;
  protected int channels, timeBins;
  protected float timeRange;
  protected int minWave, waveStep, maxWave;

  // fit parameters
  protected int numExp;
  protected Class curveFitterClass;
  protected boolean allowCurveFit;
  protected int binRadius;
  protected int cutBins;
  protected int maxPeak;

  // miscellaneous parameters
  protected boolean computeFWHMs;

  // state parameters
  protected boolean[] cVisible;

  // GUI components for parameter dialog box
  private JDialog paramDialog;
  private JTextField wField, hField, tField, cField;
  private JTextField trField, wlField, sField;
  private JTextField fitField;
  private JRadioButton gaChoice, lmChoice, noChoice;
  private JTextField binField;
  private JCheckBox alignBox, cutBox;
  private JCheckBox fwhmBox;

  // GUI components for reporting progress
  private ProgressMonitor progress;
  private int progressBase, progressInc;

  // -- Constructor --

  /** Extracts data from the given file. */
  public SlimData(String id, ProgressMonitor progress)
    throws FormatException, IOException
  {
    this.progress = progress;

    // read SDT file header
    IFormatReader reader = new ChannelSeparator();
    reader.setId(id);
    width = reader.getSizeX();
    height = reader.getSizeY();
    int[] cLengths = reader.getChannelDimLengths();
    String[] cTypes = reader.getChannelDimTypes();
    timeBins = channels = 1;
    int lifetimeIndex = -1, spectraIndex = -1;
    for (int i=0; i<cTypes.length; i++) {
      if (cTypes[i].equals(FormatTools.LIFETIME)) {
        timeBins = cLengths[i];
        lifetimeIndex = i;
      }
      else if (cTypes[i].equals(FormatTools.SPECTRA)) {
        channels = cLengths[i];
        spectraIndex = i;
      }
      else if (lifetimeIndex < 0 && cTypes[i].equals(FormatTools.CHANNEL)) {
        timeBins = cLengths[i];
        lifetimeIndex = i;
      }
    }
    int pixelType = reader.getPixelType();
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    if (bpp != 2) {
      throw new FormatException(
        "Only 16-bit integer data is supported.");
    }
    timeRange = 12.5f;
    minWave = 400;
    waveStep = 10;
    binRadius = 3;

    // show dialog confirming data parameters
    showParamDialog();
    if (cVisible == null) System.exit(0); // dialog canceled (closed with X)
    maxWave = minWave + (channels - 1) * waveStep;

    // progress estimate:
    // * Reading data - 48%
    // * Adjusting peaks - 2%
    // * Subsampling data - 50%

    // read pixel data and convert to unsigned shorts
    progress.setNote("Reading data");
    byte[] plane = new byte[bpp * height * width];
    data = new int[channels][height][width][timeBins];
    int imageCount = reader.getImageCount();
    for (int i=0; i<imageCount; i++) {
      int[] zct = reader.getZCTCoords(i);
      if (zct[0] != 0 || zct[2] != 0) continue; // process only first Z and T
      int[] sub = FormatTools.rasterToPosition(cLengths, zct[1]);
      int c = spectraIndex < 0 ? 0 : sub[spectraIndex];
      int t = lifetimeIndex < 0 ? 0 : sub[lifetimeIndex];
      reader.openBytes(i, plane, 0, 0, width, height);
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = bpp * (y * width + x);
          int val = DataTools.bytesToInt(plane, index, 2, true);
          data[c][y][x][t] = val;
        }
      }
      SlimPlotter.setProgress(progress, 0, 480, (float) (i + 1) / imageCount);
    }
    reader.close();

    // adjust peaks
    if (allowCurveFit && maxPeak == 0) {
      progress.setNote("Adjusting peaks");
      int[] peaks = new int[channels];
      for (int c=0; c<channels; c++) {
        int[] sum = new int[timeBins];
        for (int h=0; h<height; h++) {
          for (int w=0; w<width; w++) {
            for (int t=0; t<timeBins; t++) sum[t] += data[c][h][w][t];
          }
        }
        int peak = 0, ndx = 0;
        for (int t=0; t<timeBins; t++) {
          if (peak <= sum[t]) {
            peak = sum[t];
            ndx = t;
          }
          else if (t > timeBins / 3) break; // HACK - too early to give up
        }
        peaks[c] = ndx;
        SlimPlotter.setProgress(progress, 480, 10,
          (double) (c + 1) / channels);
      }
      for (int c=0; c<channels; c++) {
        if (maxPeak < peaks[c]) maxPeak = peaks[c];
      }
      SlimPlotter.log("Aligning peaks to tmax = " + maxPeak);
      for (int c=0; c<channels; c++) {
        int shift = maxPeak - peaks[c];
        if (shift > 0) {
          for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
              for (int t=timeBins-1; t>=shift; t--) {
                data[c][h][w][t] = data[c][h][w][t - shift];
              }
              for (int t=shift-1; t>=0; t--) data[c][h][w][t] = 0;
            }
          }
          SlimPlotter.log("\tChannel #" + (c + 1) + ": tmax = " + peaks[c] +
            " (shifting by " + shift + ")");
        }
        SlimPlotter.setProgress(progress, 490, 10,
          (double) (c + 1) / channels);
      }
    }

    // construct subsampled per-pixel curve estimate data
    if (allowCurveFit) {
      curves = new CurveCollection[channels];
      progressInc = 500 / channels;
      for (int c=0; c<channels; c++) {
        if (channels == 1) progress.setNote("Subsampling data");
        else progress.setNote("Subsampling ch. " + (c + 1) + "/" + channels);
        curves[c] = new CurveCollection(data[c],
          curveFitterClass, binRadius, maxPeak, timeBins - 1 - cutBins);
        curves[c].addCurveListener(this);
        progressBase = 500 + 500 * c / channels;
        curves[c].computeCurves();
      }
    }
    progressBase = 1000;
  }

  // -- SlimData methods --

  /** Converts value in picoseconds to histogram bins. */
  public float picoToBins(float pico) {
    return (timeBins - 1) * pico / timeRange / 1000;
  }

  /** Converts value in histogram bins to picoseconds. */
  public float binsToPico(float bins) {
    return 1000 * timeRange * bins / (timeBins - 1);
  }

  // -- ActionListener methods --

  /** Handles OK button press. */
  public void actionPerformed(ActionEvent e) {
    width = parse(wField.getText(), width);
    height = parse(hField.getText(), height);
    timeBins = parse(tField.getText(), timeBins);
    channels = parse(cField.getText(), channels);
    timeRange = parse(trField.getText(), timeRange);
    minWave = parse(wlField.getText(), minWave);
    waveStep = parse(sField.getText(), waveStep);
    numExp = parse(fitField.getText(), numExp);
    curveFitterClass = null;
    if (gaChoice.isSelected()) curveFitterClass = GACurveFitter.class;
    else if (lmChoice.isSelected()) curveFitterClass = LMCurveFitter.class;
    allowCurveFit = !noChoice.isSelected();
    binRadius = parse(binField.getText(), binRadius);
    maxPeak = alignBox.isSelected() ? 0 : -1;
    cutBins = cutBox.isSelected() ? (int) picoToBins(1500) : 0;
    computeFWHMs = fwhmBox.isSelected();
    cVisible = new boolean[channels];
    Arrays.fill(cVisible, true);
    paramDialog.setVisible(false);
  }

  // -- CurveListener methods --

  /** Handles curve collection computation progress. */
  public void curveChanged(CurveEvent e) {
    String message = e.getMessage();
    if (message != null) progress.setNote(message);
    SlimPlotter.setProgress(progress, progressBase, progressInc,
      (double) e.getValue() / e.getMaximum());
  }

  // -- Utility methods --

  public static JTextField addRow(JPanel p,
    String label, double value, String unit)
  {
    p.add(new JLabel(label));
    JTextField textField = new JTextField(value == (int) value ?
      ("" + (int) value) : ("" + value), 8);
    JPanel textFieldPane = new JPanel();
    textFieldPane.setLayout(new BorderLayout());
    textFieldPane.add(textField, BorderLayout.CENTER);
    textFieldPane.setBorder(new EmptyBorder(2, 3, 2, 3));
    p.add(textFieldPane);
    p.add(new JLabel(unit));
    return textField;
  }

  public static int parse(String s, int last) {
    try { return Integer.parseInt(s); }
    catch (NumberFormatException exc) { return last; }
  }

  public static float parse(String s, float last) {
    try { return Float.parseFloat(s); }
    catch (NumberFormatException exc) { return last; }
  }

  // -- Helper methods --

  private void showParamDialog() {
    paramDialog = new JDialog((Frame) null, TITLE, true);
    JPanel paramPane = new JPanel();
    paramPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    paramDialog.setContentPane(paramPane);
    paramPane.setLayout(new SpringLayout());
    // rows 1-7
    wField = addRow(paramPane, "Image width", width, "pixels");
    hField = addRow(paramPane, "Image height", height, "pixels");
    tField = addRow(paramPane, "Time bins", timeBins, "");
    cField = addRow(paramPane, "Channel count", channels, "");
    trField = addRow(paramPane, "Time range", timeRange, "nanoseconds");
    wlField = addRow(paramPane, "Starting wavelength", minWave, "nanometers");
    sField = addRow(paramPane, "Channel width", waveStep, "nanometers");
    // row 8
    paramPane.add(new JLabel("Fit algorithm"));
    boolean fitEnabled = timeBins >= 16;
    gaChoice = new JRadioButton("Genetic", fitEnabled);
    lmChoice = new JRadioButton("LM");
    noChoice = new JRadioButton("None", !fitEnabled);
    ButtonGroup group = new ButtonGroup();
    group.add(gaChoice);
    group.add(lmChoice);
    group.add(noChoice);
    JPanel algPane = new JPanel();
    algPane.setLayout(new BoxLayout(algPane, BoxLayout.X_AXIS));
    algPane.add(gaChoice);
    algPane.add(lmChoice);
    //algPane.add(noChoice);
    paramPane.add(algPane);
    paramPane.add(noChoice);
    // rows 9-10
    fitField = addRow(paramPane, "Exponential fit", 1, "components");
    binField = addRow(paramPane, "Bin radius", binRadius, "pixels");
    // row 11, column 1
    paramPane.add(new JLabel("Fit options"));
    // row 11, column 2
    alignBox = new JCheckBox("Align peaks", true);
    alignBox.setToolTipText("<html>If checked, data peaks are globally " +
      "aligned spatially and across channels;<br>otherwise, each curve's " +
      "peak is individually auto-detected.</html>");
    paramPane.add(alignBox);
    // row 11, column 3
    cutBox = new JCheckBox("Cut 1.5ns", true);
    cutBox.setToolTipText("<html>When performing exponential curve " +
      "fitting, excludes the last 1.5 ns<br>from the computation. This " +
      "option is useful because the end of the<br>the lifetime histogram " +
      "sometimes drops off unexpectedly, skewing<br>the fit results.</html>");
    paramPane.add(cutBox);
    // row 12, column 1
    paramPane.add(new JLabel("Toggles"));
    // row 12, column 2
    fwhmBox = new JCheckBox("FWHMs", true);
    fwhmBox.setToolTipText(
      "<html>Computes the full width half max at each channel.</html>");
    paramPane.add(fwhmBox);
    // row 12, column 3
    paramPane.add(new JLabel());
    // row 13
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    // row 14
    paramPane.add(new JLabel());
    JButton ok = new JButton("OK");
    paramDialog.getRootPane().setDefaultButton(ok);
    ok.addActionListener(this);
    paramPane.add(ok);
    paramPane.add(new JLabel());

    // size dialog
    SpringUtilities.makeCompactGrid(paramPane, 14, 3, 3, 3, 3, 3);
    paramDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension ps = paramDialog.getSize();
    paramDialog.setLocation((screenSize.width - ps.width) / 2,
      (screenSize.height - ps.height) / 2);
    paramDialog.setVisible(true);
  }

}
