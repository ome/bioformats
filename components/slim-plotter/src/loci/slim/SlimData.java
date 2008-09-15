//
// SlimData.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import loci.formats.FormatException;
import loci.formats.in.SDTInfo;
import loci.formats.in.SDTReader;
import loci.formats.DataTools;
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
public class SlimData implements ActionListener {

  // -- Constants --

  private static final int BIN_RADIUS = 3; // TODO - make this a UI option

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
  protected boolean adjustPeaks, cutEnd;
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
  private JRadioButton gaChoice, lmChoice;
  private JCheckBox peaksBox, fwhmBox, cutBox;

  // -- Constructor --

  /** Extracts data from the given file. */
  public SlimData(String id, ProgressMonitor progress)
    throws FormatException, IOException
  {
    // read SDT file header
    SDTReader reader = new SDTReader();
    reader.setId(id);
    width = reader.getSizeX();
    height = reader.getSizeY();
    timeBins = reader.getTimeBinCount();
    channels = reader.getChannelCount();
    SDTInfo info = reader.getInfo();
    reader.close();
    timeRange = 12.5f;
    minWave = 400;
    waveStep = 10;
    int offset = info.dataBlockOffs + 22;

    // show dialog confirming data parameters
    showParamDialog();
    if (cVisible == null) System.exit(0); // dialog canceled (closed with X)
    maxWave = minWave + (channels - 1) * waveStep;

    // progress estimate:
    // * Reading data - 50%
    // * Constructing images - 2%
    // * Adjusting peaks - 2%
    // * Subsampling histograms - 36%

    // read pixel data
    progress.setNote("Reading data");
    DataInputStream fin = new DataInputStream(new FileInputStream(id));
    fin.skipBytes(offset); // skip to data
    byte[] bytes = new byte[2 * channels * height * width * timeBins];
    int blockSize = 65536;
    for (int off=0; off<bytes.length; off+=blockSize) {
      int len = bytes.length - off;
      if (len > blockSize) len = blockSize;
      fin.readFully(bytes, off, len);
      SlimPlotter.setProgress(progress, 0, 500,
        (double) (off + blockSize) / bytes.length);
    }
    fin.close();

    // convert byte data to unsigned shorts
    progress.setNote("Constructing images");
    data = new int[channels][height][width][timeBins];
    for (int c=0; c<channels; c++) {
      int oc = timeBins * width * height * c;
      for (int h=0; h<height; h++) {
        int oh = timeBins * width * h;
        for (int w=0; w<width; w++) {
          int ow = timeBins * w;
          for (int t=0; t<timeBins; t++) {
            int ndx = 2 * (oc + oh + ow + t);
            int val = DataTools.bytesToInt(bytes, ndx, 2, true);
            data[c][h][w][t] = val;
          }
        }
        SlimPlotter.setProgress(progress, 500, 20,
          (double) (height * c + h + 1) / (channels * height));
      }
    }

    // adjust peaks
    if (adjustPeaks) {
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
        SlimPlotter.setProgress(progress, 580, 10,
          (double) (c + 1) / channels);
      }
      maxPeak = 0;
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
        SlimPlotter.setProgress(progress, 590, 10,
          (double) (c + 1) / channels);
      }
    }

    // construct subsampled per-pixel curve estimate data
    progress.setNote("Subsampling data");
    curves = new CurveCollection[channels];
    for (int c=0; c<channels; c++) {
      curves[c] = new CurveCollection(data[c], curveFitterClass, BIN_RADIUS);
      SlimPlotter.setProgress(progress, 540, 360,
        (double) (c + 1) / channels);
    }
  }

  // -- ActionListener methods --

  /** Handles checkbox and button presses. */
  public void actionPerformed(ActionEvent e) {
    width = parse(wField.getText(), width);
    height = parse(hField.getText(), height);
    timeBins = parse(tField.getText(), timeBins);
    channels = parse(cField.getText(), channels);
    timeRange = parse(trField.getText(), timeRange);
    minWave = parse(wlField.getText(), minWave);
    waveStep = parse(sField.getText(), waveStep);
    numExp = parse(fitField.getText(), numExp);
    curveFitterClass = lmChoice.isSelected() ?
      LMCurveFitter.class : GACurveFitter.class;
    adjustPeaks = peaksBox.isSelected();
    computeFWHMs = fwhmBox.isSelected();
    cutEnd = cutBox.isSelected();
    cVisible = new boolean[channels];
    Arrays.fill(cVisible, true);
    paramDialog.setVisible(false);
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
    paramDialog = new JDialog((Frame) null, "Slim Plotter", true);
    JPanel paramPane = new JPanel();
    paramPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    paramDialog.setContentPane(paramPane);
    paramPane.setLayout(new GridLayout(13, 3));
    wField = addRow(paramPane, "Image width", width, "pixels");
    hField = addRow(paramPane, "Image height", height, "pixels");
    tField = addRow(paramPane, "Time bins", timeBins, "");
    cField = addRow(paramPane, "Channel count", channels, "");
    trField = addRow(paramPane, "Time range", timeRange, "nanoseconds");
    wlField = addRow(paramPane, "Starting wavelength", minWave, "nanometers");
    sField = addRow(paramPane, "Channel width", waveStep, "nanometers");
    fitField = addRow(paramPane, "Exponential fit", 1, "components");
    JButton ok = new JButton("OK");
    paramDialog.getRootPane().setDefaultButton(ok);
    ok.addActionListener(this);
    // row 8
    paramPane.add(new JLabel("Fit algorithm"));
    lmChoice = new JRadioButton("Levenberg-Marquardt");
    gaChoice = new JRadioButton("Genetic", true);
    ButtonGroup group = new ButtonGroup();
    group.add(lmChoice);
    group.add(gaChoice);
    paramPane.add(lmChoice);
    paramPane.add(gaChoice);
    // row 9
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    // row 10, column 1
    peaksBox = new JCheckBox("Align peaks", true);
    peaksBox.setToolTipText("<html>Computes the peak of each spectral " +
      "channel, and aligns those peaks <br>to match by adjusting the " +
      "lifetime histograms. This option corrects<br>for skew across " +
      "channels caused by the multispectral detector's<br>variable system " +
      "response time between channels. This option must<br>be enabled to " +
      "perform exponential curve fitting.</html>");
    paramPane.add(peaksBox);
    // row 10, column 2
    fwhmBox = new JCheckBox("Compute FWHMs", false);
    fwhmBox.setToolTipText(
      "<html>Computes the full width half max at each channel.</html>");
    paramPane.add(fwhmBox);
    // row 10, column 3
    cutBox = new JCheckBox("Cut 1.5ns from fit", true);
    cutBox.setToolTipText("<html>When performing exponential curve " +
      "fitting, excludes the last 1.5 ns<br>from the computation. This " +
      "option is useful because the end of the <br>the lifetime histogram " +
      "sometimes drops off unexpectedly, skewing<br>the fit results.</html>");
    paramPane.add(cutBox);
    // row 11
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    // row 12
    paramPane.add(new JLabel());
    paramPane.add(ok);
    paramDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension ps = paramDialog.getSize();
    paramDialog.setLocation((screenSize.width - ps.width) / 2,
      (screenSize.height - ps.height) / 2);
    paramDialog.setVisible(true);
  }

}
