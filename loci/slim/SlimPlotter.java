//
// SlimPlotter.java
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

// Special thanks to Long Yan, Steve Trier, Kraig Kumfer,
// Paolo Provenzano and Tony Collins for suggestions and testing.

package loci.slim;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;
import loci.formats.in.SDTInfo;
import loci.formats.in.SDTReader;
import loci.formats.DataTools;
import loci.formats.gui.ExtensionFileFilter;
import loci.visbio.util.BreakawayPanel;
import loci.visbio.util.ColorUtil;
import loci.visbio.util.OutputConsole;
import visad.*;
import visad.java3d.*;
import visad.util.ColorMapWidget;
import visad.util.Util;

/**
 * A tool for visualization of spectral lifetime data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/SlimPlotter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/SlimPlotter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SlimPlotter implements ActionListener, ChangeListener,
  DocumentListener, Runnable, WindowListener
{

  // -- Constants --

  /** Names for preset color look-up table. */
  public static final String[] LUT_NAMES = {
    "Grayscale", "HSV", "RGB", null,
    "Red", "Green", "Blue", null,
    "Cyan", "Magenta", "Yellow", null,
    "Fire", "Ice"
  };

  /** Preset color look-up tables. */
  public static final float[][][] LUTS = {
    ColorUtil.LUT_GRAY, ColorUtil.LUT_HSV, ColorUtil.LUT_RGB, null,
    ColorUtil.LUT_RED, ColorUtil.LUT_GREEN, ColorUtil.LUT_BLUE, null,
    ColorUtil.LUT_CYAN, ColorUtil.LUT_MAGENTA, ColorUtil.LUT_YELLOW, null,
    ColorUtil.LUT_FIRE, ColorUtil.LUT_ICE
  };

  /** Default orientation for 3D decay curves display. */
  private static final double[] MATRIX_3D = {
    0.2821, 0.1503, -0.0201, 0.0418,
    -0.0500, 0.1323, 0.2871, 0.1198,
    0.1430, -0.2501, 0.1408, 0.0089,
    0.0000, 0.0000, 0.0000, 1.0000
  };

  /** Default orientation for 2D decay curve display. */
  private static final double[] MATRIX_2D = {
    0.400, 0.000, 0.000, 0.160,
    0.000, 0.400, 0.000, 0.067,
    0.000, 0.000, 0.400, 0.000,
    0.000, 0.000, 0.000, 1.000
  };

  private static final char TAU = 'T';

  // log base 10, for now
  private static final float BASE = 10;
  private static final float BASE_LOG = (float) Math.log(BASE);

  // -- Fields --

  /** Actual data values, dimensioned [channel][row][column][bin]. */
  private int[][][][] values;

  /** Current binned data values, dimensioned [channels * timeBins]. */
  private float[] samps;

  // data parameters
  private int width, height;
  private int channels, timeBins;
  private float timeRange;
  private int minWave, waveStep, maxWave;
  private int numExp;
  private boolean adjustPeaks, computeFWHMs, cutEnd;
  private boolean useLMA;
  private boolean[] cVisible;
  private int maxPeak;
  private int[] maxIntensity;

  private float maxVal;
  private float tauMin, tauMax;

  // fit parameters
  private float[][] tau;

  // system clipboard helper
  private TextTransfer clip = new TextTransfer();

  // menu items
  private MenuItem menuFileExit;
  private MenuItem menuViewSaveProj;
  private MenuItem menuViewLoadProj;

  // GUI components for parameter dialog box
  private JDialog paramDialog;
  private JTextField wField, hField, tField, cField;
  private JTextField trField, wlField, sField;
  private JTextField fitField;
  private JRadioButton gaChoice, lmChoice;
  private JCheckBox peaksBox, fwhmBox, cutBox;

  // GUI components for 2D pane
  private TwoDPane twoDPane;

  // GUI components for decay pane
  private JLabel decayLabel;
  private ColorMapWidget colorWidget;
  private JCheckBox cOverride;
  private JTextField cMinValue, cMaxValue;
  private JButton lutLoad, lutSave, lutPresets;
  private JPopupMenu lutsMenu;
  private JFileChooser lutBox;
  private JRadioButton linear, log;
  private JRadioButton perspective, parallel;
  private JRadioButton dataSurface, dataLines;
  private JRadioButton fitSurface, fitLines;
  private JRadioButton resSurface, resLines;
  private JRadioButton colorHeight, colorTau;
  private JCheckBox showData, showScale;
  private JCheckBox showBox, showLine;
  private JCheckBox showFit, showResiduals;
  private JCheckBox showFWHMs;
  private JCheckBox zOverride;
  private JTextField zScaleValue;
  private JButton exportData;

  // other GUI components
  private JFrame masterWindow;
  private OutputConsole console;

  // VisAD objects
  private Unit[] bcUnits;
  private RealType bType, cType;
  private RealTupleType bc, bv, bcv;
  private FunctionType bcvFunc, bcvFuncFit, bcvFuncRes;
  private ScalarMap zMap, zMapFit, zMapRes, vMap, vMapRes;
  //private ScalarMap vMapFit;
  private DataRenderer decayRend, fitRend, resRend, lineRend, fwhmRend;
  private DataReferenceImpl decayRef, fwhmRef, fitRef, resRef;
  private DisplayImpl decayPlot;

  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    console = new OutputConsole("Log");
    console.getTextArea().setColumns(54);
    console.getTextArea().setRows(10);

    PrintStream err = new PrintStream(console);

    // HACK - suppress hard-coded exception dumps
    err = LMCurveFitter.filter(err);

    System.setErr(err);

    // progress estimate:
    // * Reading data - 70%
    // * Creating types - 1%
    // * Building displays - 7%
    // * Constructing images - 14%
    // * Adjusting peaks - 4%
    // * Creating plots - 4%
    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching Slim Plotter", "Initializing", 0, 1000);
    progress.setMillisToPopup(0);
    progress.setMillisToDecideToPopup(0);

    int maxChan = -1;
    try {
      // check for required libraries
      try {
        Class.forName("javax.vecmath.Point3d");
      }
      catch (Throwable t) {
        String os = System.getProperty("os.name").toLowerCase();
        String url = null;
        if (os.indexOf("windows") >= 0 ||
          os.indexOf("linux") >= 0 || os.indexOf("solaris") >= 0)
        {
          url = "https://java3d.dev.java.net/binary-builds.html";
        }
        else if (os.indexOf("mac os x") >= 0) {
          url = "http://www.apple.com/downloads/macosx/apple/" +
            "java3dandjavaadvancedimagingupdate.html";
        }
        else if (os.indexOf("aix") >= 0) {
          url = "http://www-128.ibm.com/developerworks/java/jdk/aix/index.html";
        }
        else if (os.indexOf("hp-ux") >= 0) {
          url = "http://www.hp.com/products1/unix/java/java2/java3d/" +
            "downloads/index.html";
        }
        else if (os.indexOf("irix") >= 0) {
          url = "http://www.sgi.com/products/evaluation/6.5_java3d_1.3.1/";
        }
        JOptionPane.showMessageDialog(null,
          "Slim Plotter requires Java3D, but it was not found." +
          (url == null ? "" : ("\nPlease install it from:\n" + url)),
          "Slim Plotter", JOptionPane.ERROR_MESSAGE);
        System.exit(3);
      }

      // parse command line arguments
      String filename = null;
      File file = null;
      if (args == null || args.length < 1) {
        JFileChooser jc = new JFileChooser(System.getProperty("user.dir"));
        jc.addChoosableFileFilter(new ExtensionFileFilter("sdt",
          "Becker & Hickl SPC-Image SDT"));
        int rval = jc.showOpenDialog(null);
        if (rval != JFileChooser.APPROVE_OPTION) {
          System.out.println("Please specify an SDT file.");
          System.exit(1);
        }
        file = jc.getSelectedFile();
        filename = file.getPath();
      }
      else {
        filename = args[0];
        file = new File(filename);
      }

      if (!file.exists()) {
        System.out.println("File does not exist: " + filename);
        System.exit(2);
      }

      // read SDT file header
      SDTReader reader = new SDTReader();
      reader.setId(file.getPath());
      width = reader.getSizeX();
      height = reader.getSizeY();
      timeBins = reader.getTimeBinCount();
      channels = reader.getChannelCount();
      SDTInfo info = reader.getInfo();
      reader.close();
      timeRange = 12.5f;
      minWave = 400;
      waveStep = 10;

      // show dialog confirming data parameters
      showParamDialog();
      if (cVisible == null) System.exit(0); // dialog canceled (closed with X)
      maxWave = minWave + (channels - 1) * waveStep;

      // pop up progress monitor
      setProgress(progress, 1); // estimate: 0.1%

      // read pixel data
      progress.setNote("Reading data");
      DataInputStream fin = new DataInputStream(new FileInputStream(file));
      fin.skipBytes(info.dataBlockOffs); // skip to data
      byte[] data = new byte[2 * channels * height * width * timeBins];
      int blockSize = 65536;
      for (int off=0; off<data.length; off+=blockSize) {
        int len = data.length - off;
        if (len > blockSize) len = blockSize;
        fin.readFully(data, off, len);
        setProgress(progress, (int) (700L *
          (off + blockSize) / data.length)); // estimate: 0% -> 70%
      }
      fin.close();

      // create types
      progress.setNote("Creating types");
      RealType xType = RealType.getRealType("element");
      RealType yType = RealType.getRealType("line");
      ScaledUnit ns = new ScaledUnit(1e-9, SI.second, "ns");
      ScaledUnit nm = new ScaledUnit(1e-9, SI.meter, "nm");
      bcUnits = new Unit[] {ns, nm};
      bType = RealType.getRealType("bin", ns);
      cType = RealType.getRealType("channel", nm);
      RealType vType = RealType.getRealType("count");
      RealTupleType xy = new RealTupleType(xType, yType);
      FunctionType xyvFunc = new FunctionType(xy, vType);
      Integer2DSet xySet = new Integer2DSet(xy, width, height);
      FunctionType cxyvFunc = new FunctionType(cType, xyvFunc);
      Linear1DSet cSet = new Linear1DSet(cType,
        minWave, maxWave, channels, null, new Unit[] {nm}, null);
      bc = new RealTupleType(bType, cType);
      bv = new RealTupleType(bType, vType);
      bcv = new RealTupleType(bType, cType, vType);
      RealType vType2 = RealType.getRealType("value");
      RealTupleType vv = new RealTupleType(vType, vType2);
      bcvFunc = new FunctionType(bc, vv);
      RealType vTypeFit = RealType.getRealType("value_fit");
      bcvFuncFit = new FunctionType(bc, vTypeFit);
      RealType vTypeRes = RealType.getRealType("value_res");
      bcvFuncRes = new FunctionType(bc, vTypeRes);
      setProgress(progress, 710); // estimate: 71%

      // plot intensity data in 2D display
      progress.setNote("Building displays");

      setProgress(progress, 720); // estimate: 72%
      setProgress(progress, 730); // estimate: 73%
      setProgress(progress, 740); // estimate: 74%
      setProgress(progress, 750); // estimate: 75%

      // plot decay curves in 3D display
      decayPlot = channels > 1 ? new DisplayImplJ3D("decay") :
        new DisplayImplJ3D("decay", new TwoDDisplayRendererJ3D());
      ScalarMap xMap = new ScalarMap(bType, Display.XAxis);
      ScalarMap yMap = new ScalarMap(cType, Display.YAxis);
      DisplayRealType heightAxis = channels > 1 ? Display.ZAxis : Display.YAxis;
      zMap = new ScalarMap(vType, heightAxis);
      zMapFit = new ScalarMap(vTypeFit, heightAxis);
      zMapRes = new ScalarMap(vTypeRes, heightAxis);
      vMap = new ScalarMap(vType2, Display.RGB);
      //vMapFit = new ScalarMap(vTypeFit, Display.RGB);
      vMapRes = new ScalarMap(vTypeRes, Display.RGB);
      decayPlot.addMap(xMap);
      if (channels > 1) decayPlot.addMap(yMap);
      decayPlot.addMap(zMap);
      decayPlot.addMap(zMapFit);
      decayPlot.addMap(zMapRes);
      decayPlot.addMap(vMap);
      //decayPlot.addMap(vMapFit);
      decayPlot.addMap(vMapRes);
      setProgress(progress, 760); // estimate: 76%

      decayRend = new DefaultRendererJ3D();
      decayRef = new DataReferenceImpl("decay");
      decayPlot.addReferences(decayRend, decayRef);

      if (computeFWHMs) {
        // add reference for full width half maxes
        fwhmRend = new DefaultRendererJ3D();
        fwhmRef = new DataReferenceImpl("fwhm");
        decayPlot.addReferences(fwhmRend, fwhmRef, new ConstantMap[] {
          new ConstantMap(0, Display.Red),
          new ConstantMap(0, Display.Blue),
          //new ConstantMap(2, Display.LineWidth)
        });
      }

      if (adjustPeaks) {
        // add references for curve fitting
        fitRend = new DefaultRendererJ3D();
        fitRef = new DataReferenceImpl("fit");
        decayPlot.addReferences(fitRend, fitRef, new ConstantMap[] {
          new ConstantMap(1, Display.Red),
          new ConstantMap(1, Display.Green),
          new ConstantMap(1, Display.Blue)
        });
        fitRend.toggle(false);
        resRend = new DefaultRendererJ3D();
        resRef = new DataReferenceImpl("residuals");
        decayPlot.addReferences(resRend, resRef);
        resRend.toggle(false);
      }
      setProgress(progress, 770); // estimate: 77%

      xMap.setRange(0, timeRange);
      yMap.setRange(minWave, maxWave);
      AxisScale xScale = xMap.getAxisScale();
      Font font = Font.decode("serif 24");
      xScale.setFont(font);
      xScale.setTitle("Time (ns)");
      xScale.setSnapToBox(true);
      AxisScale yScale = yMap.getAxisScale();
      yScale.setFont(font);
      yScale.setTitle("Wavelength (nm)");
      yScale.setSide(AxisScale.SECONDARY);
      yScale.setSnapToBox(true);
      AxisScale zScale = zMap.getAxisScale();
      zScale.setFont(font);
      zScale.setTitle("Count");
      zScale.setSnapToBox(true); // workaround for weird axis spacing issue
      zMapFit.getAxisScale().setVisible(false);
      zMapRes.getAxisScale().setVisible(false);
      GraphicsModeControl gmc = decayPlot.getGraphicsModeControl();
      gmc.setScaleEnable(true);
      gmc.setTextureEnable(false);
      ProjectionControl pc = decayPlot.getProjectionControl();
      pc.setMatrix(channels > 1 ? MATRIX_3D : MATRIX_2D);
      pc.setAspectCartesian(
        new double[] {2, 1, 1});
      setProgress(progress, 780); // estimate: 78%

      // convert byte data to unsigned shorts
      progress.setNote("Constructing images");
      values = new int[channels][height][width][timeBins];
      float[][][] pix = new float[channels][1][width * height];
      FieldImpl field = new FieldImpl(cxyvFunc, cSet);
      maxIntensity = new int[channels];
      for (int c=0; c<channels; c++) {
        int oc = timeBins * width * height * c;
        for (int h=0; h<height; h++) {
          int oh = timeBins * width * h;
          for (int w=0; w<width; w++) {
            int ow = timeBins * w;
            int sum = 0;
            for (int t=0; t<timeBins; t++) {
              int ndx = 2 * (oc + oh + ow + t);
              int val = DataTools.bytesToInt(data, ndx, 2, true);
              values[c][h][w][t] = val;
              sum += val;
            }
            if (sum > maxIntensity[c]) maxIntensity[c] = sum;
            pix[c][0][width * h + w] = sum;
          }
          setProgress(progress, 780 + 140 *
            (height * c + h + 1) / (channels * height)); // estimate: 78% -> 92%
        }
        FlatField ff = new FlatField(xyvFunc, xySet);
        ff.setSamples(pix[c], false);
        field.setSample(c, ff);
      }

      // compute channel with brightest intensity
      maxChan = 0;
      int globalMax = 0;
      for (int c=0; c<channels; c++) {
        if (maxIntensity[c] > globalMax) {
          globalMax = maxIntensity[c];
          maxChan = c;
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
              for (int t=0; t<timeBins; t++) sum[t] += values[c][h][w][t];
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
          setProgress(progress, 920 + 20 *
            (c + 1) / channels); // estimate: 92% -> 94%
        }
        maxPeak = 0;
        for (int c=0; c<channels; c++) {
          if (maxPeak < peaks[c]) maxPeak = peaks[c];
        }
        log("Aligning peaks to tmax = " + maxPeak);
        for (int c=0; c<channels; c++) {
          int shift = maxPeak - peaks[c];
          if (shift > 0) {
            for (int h=0; h<height; h++) {
              for (int w=0; w<width; w++) {
                for (int t=timeBins-1; t>=shift; t--) {
                  values[c][h][w][t] = values[c][h][w][t - shift];
                }
                for (int t=shift-1; t>=0; t--) values[c][h][w][t] = 0;
              }
            }
            log("\tChannel #" + (c + 1) + ": tmax = " + peaks[c] +
              " (shifting by " + shift + ")");
          }
          setProgress(progress, 940 + 20 *
            (c + 1) / channels); // estimate: 94% -> 96%
        }

        // add yellow line to indicate adjusted peak position
        lineRend = new DefaultRendererJ3D();
        DataReferenceImpl peakRef = new DataReferenceImpl("peaks");
        float peakTime = (float) (maxPeak * timeRange / (timeBins - 1));
        peakRef.setData(new Gridded2DSet(bc,
          new float[][] {{peakTime, peakTime}, {minWave, maxWave}}, 2));
        decayPlot.addReferences(lineRend, peakRef, new ConstantMap[] {
          new ConstantMap(-1, Display.ZAxis),
          new ConstantMap(0, Display.Blue),
          //new ConstantMap(2, Display.LineWidth)
        });
      }

      // construct 2D pane
      progress.setNote("Creating plots");
      masterWindow = new JFrame("Slim Plotter - " + file.getName());
      masterWindow.addWindowListener(this);
      JPanel masterPane = new JPanel();
      masterPane.setLayout(new BorderLayout());
      masterWindow.setContentPane(masterPane);

      twoDPane = new TwoDPane(this, field,
        width, height, channels, globalMax, maxChan, cVisible,
        xType, yType, vType, cType);

      MenuBar menubar = new MenuBar();
      masterWindow.setMenuBar(menubar);
      Menu menuFile = new Menu("File");
//      menubar.add(menuFile);
      menuFileExit = new MenuItem("Exit");
      menuFileExit.addActionListener(this);
      menuFile.add(menuFileExit);

      Menu menuView = new Menu("View");
      menubar.add(menuView);
      menuViewSaveProj = new MenuItem("Save projection to clipboard");
      menuViewSaveProj.addActionListener(this);
      menuView.add(menuViewSaveProj);

      menuViewLoadProj = new MenuItem("Restore projection from clipboard");
      menuViewLoadProj.addActionListener(this);
      menuView.add(menuViewLoadProj);

      setProgress(progress, 980); // estimate: 98%

      // construct 3D pane
      JPanel decayPane = new JPanel();
      decayPane.setLayout(new BorderLayout());
      decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

      decayLabel = new JLabel("Decay curve for all pixels");
      decayLabel.setToolTipText(
        "Displays information about the selected region of interest");
      decayPane.add(decayLabel, BorderLayout.NORTH);

      colorWidget = new ColorMapWidget(vMap);
      Dimension prefSize = colorWidget.getPreferredSize();
      colorWidget.setPreferredSize(new Dimension(prefSize.width, 0));

      cOverride = new JCheckBox("", false);
      cOverride.setToolTipText(
        "Toggles manual override of color range");
      cOverride.addActionListener(this);
      cMinValue = new JTextField();
      Util.adjustTextField(cMinValue);
      cMinValue.setToolTipText("Overridden color minimum");
      cMinValue.setEnabled(false);
      cMinValue.getDocument().addDocumentListener(this);
      cMaxValue = new JTextField();
      Util.adjustTextField(cMaxValue);
      cMaxValue.setToolTipText("Overridden color maximum");
      cMaxValue.setEnabled(false);
      cMaxValue.getDocument().addDocumentListener(this);

      lutLoad = new JButton("Load LUT...");
      lutLoad.setToolTipText("Loads a color table from disk");
      lutLoad.addActionListener(this);

      lutSave = new JButton("Save LUT...");
      lutSave.setToolTipText("Saves this color table to disk");
      lutSave.addActionListener(this);

      lutsMenu = new JPopupMenu();
      for (int i=0; i<LUT_NAMES.length; i++) {
        if (LUT_NAMES[i] == null) lutsMenu.addSeparator();
        else {
          JMenuItem item = new JMenuItem(LUT_NAMES[i]);
          item.setActionCommand("lut" + i);
          item.addActionListener(this);
          lutsMenu.add(item);
        }
      }

      lutPresets = new JButton("LUTs >");
      lutPresets.setToolTipText("Selects a LUT from the list of presets");
      lutPresets.addActionListener(this);

      lutBox = new JFileChooser(System.getProperty("user.dir"));
      lutBox.addChoosableFileFilter(
        new ExtensionFileFilter("lut", "Binary color table files"));

      showData = new JCheckBox("Data", true);
      showData.setToolTipText("Toggles visibility of raw data");
      showData.addActionListener(this);
      showScale = new JCheckBox("Scale", true);
      showScale.setToolTipText("Toggles visibility of scale bars");
      showScale.addActionListener(this);
      showBox = new JCheckBox("Box", true);
      showBox.setToolTipText("Toggles visibility of bounding box");
      showBox.addActionListener(this);
      showLine = new JCheckBox("Line", adjustPeaks);
      showLine.setToolTipText(
        "Toggles visibility of aligned peaks indicator line");
      showLine.setEnabled(adjustPeaks);
      showLine.addActionListener(this);
      showFit = new JCheckBox("Fit", false);
      showFit.setToolTipText("Toggles visibility of fitted curves");
      showFit.setEnabled(adjustPeaks);
      showFit.addActionListener(this);
      showResiduals = new JCheckBox("Residuals", false);
      showResiduals.setToolTipText(
        "Toggles visibility of fitted curve residuals");
      showResiduals.setEnabled(adjustPeaks);
      showResiduals.addActionListener(this);
      showFWHMs = new JCheckBox("FWHMs", computeFWHMs);
      showFWHMs.setToolTipText("Toggles visibility of full width half maxes");
      showFWHMs.setEnabled(computeFWHMs);
      showFWHMs.addActionListener(this);

      linear = new JRadioButton("Linear", true);
      linear.setToolTipText("Plots 3D data with a linear scale");
      log = new JRadioButton("Log", false);
      log.setToolTipText("Plots 3D data with a logarithmic scale");
      perspective = new JRadioButton("Perspective", true);
      perspective.setToolTipText(
        "Displays 3D plot with a perspective projection");
      perspective.setEnabled(channels > 1);
      parallel = new JRadioButton("Parallel", false);
      parallel.setToolTipText(
        "Displays 3D plot with a parallel (orthographic) projection");
      parallel.setEnabled(channels > 1);
      dataSurface = new JRadioButton("Surface", channels > 1);
      dataSurface.setToolTipText("Displays raw data as a 2D surface");
      dataSurface.setEnabled(channels > 1);
      dataLines = new JRadioButton("Lines", channels == 1);
      dataLines.setToolTipText("Displays raw data as a series of lines");
      dataLines.setEnabled(channels > 1);
      fitSurface = new JRadioButton("Surface", false);
      fitSurface.setToolTipText("Displays fitted curves as a 2D surface");
      fitSurface.setEnabled(adjustPeaks && channels > 1);
      fitLines = new JRadioButton("Lines", true);
      fitLines.setToolTipText("Displays fitted curves as a series of lines");
      fitLines.setEnabled(adjustPeaks && channels > 1);
      resSurface = new JRadioButton("Surface", false);
      resSurface.setToolTipText(
        "Displays fitted curve residuals as a 2D surface");
      resSurface.setEnabled(adjustPeaks && channels > 1);
      resLines = new JRadioButton("Lines", true);
      resLines.setToolTipText(
        "Displays fitted curve residuals as a series of lines");
      resLines.setEnabled(adjustPeaks && channels > 1);
      colorHeight = new JRadioButton("Counts", true);
      colorHeight.setToolTipText(
        "Colorizes data according to the height (histogram count)");
      colorHeight.setEnabled(adjustPeaks && channels > 1);
      colorTau = new JRadioButton("Lifetimes", false);
      colorTau.setToolTipText(
        "Colorizes data according to aggregate lifetime value");
      colorTau.setEnabled(adjustPeaks && channels > 1);

      zOverride = new JCheckBox("", false);
      zOverride.setToolTipText(
        "Toggles manual override of Z axis scale (Count)");
      zOverride.addActionListener(this);
      zScaleValue = new JTextField(9);
      zScaleValue.setToolTipText("Overridden Z axis scale value");
      zScaleValue.setEnabled(false);
      zScaleValue.getDocument().addDocumentListener(this);

      exportData = new JButton("Export");
      exportData.setToolTipText(
        "Exports the selected ROI's raw data to a text file");
      exportData.addActionListener(this);

      setProgress(progress, 990); // estimate: 99%

      JPanel colorPanel = new JPanel();
      colorPanel.setBorder(new TitledBorder("Color Mapping"));
      colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
      colorPanel.add(colorWidget);

      JPanel colorRange = new JPanel();
      colorRange.setLayout(new BoxLayout(colorRange, BoxLayout.X_AXIS));
      colorRange.add(cOverride);
      colorRange.add(cMinValue);
      colorRange.add(cMaxValue);
      colorPanel.add(colorRange);

      JPanel colorButtons = new JPanel();
      colorButtons.setLayout(new BoxLayout(colorButtons, BoxLayout.X_AXIS));
      colorButtons.add(lutLoad);
      colorButtons.add(lutSave);
      colorButtons.add(lutPresets);
      colorPanel.add(colorButtons);

      JPanel showPanel = new JPanel();
      showPanel.setBorder(new TitledBorder("Show"));
      showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.Y_AXIS));
      showPanel.add(showData);
      showPanel.add(showScale);
      showPanel.add(showBox);
      showPanel.add(showLine);
      showPanel.add(showFit);
      showPanel.add(showResiduals);
      showPanel.add(showFWHMs);

      JPanel scalePanel = new JPanel();
      scalePanel.setBorder(new TitledBorder("Z Scale Override"));
      scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.X_AXIS));
      scalePanel.add(zOverride);
      scalePanel.add(zScaleValue);

      JPanel miscRow1 = new JPanel();
      miscRow1.setLayout(new BoxLayout(miscRow1, BoxLayout.X_AXIS));
      miscRow1.add(makeRadioPanel("Scale", linear, log));
      miscRow1.add(makeRadioPanel("Projection", perspective, parallel));
      miscRow1.add(makeRadioPanel("Data", dataSurface, dataLines));

      JPanel miscRow2 = new JPanel();
      miscRow2.setLayout(new BoxLayout(miscRow2, BoxLayout.X_AXIS));
      miscRow2.add(makeRadioPanel("Fit", fitSurface, fitLines));
      miscRow2.add(makeRadioPanel("Residuals", resSurface, resLines));
      miscRow2.add(makeRadioPanel("Colors", colorHeight, colorTau));

      JPanel miscRow3 = new JPanel();
      miscRow3.setLayout(new BoxLayout(miscRow3, BoxLayout.X_AXIS));
      miscRow3.add(scalePanel);
      miscRow3.add(Box.createHorizontalStrut(5));
      miscRow3.add(exportData);

      JPanel miscPanel = new JPanel();
      miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
      miscPanel.add(miscRow1);
      miscPanel.add(miscRow2);
      miscPanel.add(miscRow3);

      JPanel options = new JPanel();
      options.setBorder(new EmptyBorder(8, 5, 8, 5));
      options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
      options.add(colorPanel);
      options.add(showPanel);
      options.add(miscPanel);
      decayPane.add(options, BorderLayout.SOUTH);
      masterPane.add(decayPane, BorderLayout.CENTER);

      JPanel rightPanel = new JPanel() {
        public Dimension getMaximumSize() {
          Dimension pref = getPreferredSize();
          Dimension max = super.getMaximumSize();
          return new Dimension(pref.width, max.height);
        }
      };
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.add(twoDPane);
      rightPanel.add(console.getWindow().getContentPane());
      BreakawayPanel breakawayPanel = new BreakawayPanel(masterPane,
        "Intensity Data - " + file.getName(), false);
      breakawayPanel.setEdge(BorderLayout.EAST);
      breakawayPanel.setUpEnabled(false);
      breakawayPanel.setDownEnabled(false);
      breakawayPanel.setContentPane(rightPanel);

      setProgress(progress, 999); // estimate: 99.9%

      // show window on screen
      masterWindow.pack();
      Dimension size = masterWindow.getSize();
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      masterWindow.setLocation((screen.width - size.width) / 2,
        (screen.height - size.height) / 2);
      masterWindow.setVisible(true);
    }
    catch (Throwable t) {
      // display stack trace to the user
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(out));
      String stackTrace = new String(out.toByteArray());
      JOptionPane.showMessageDialog(null,
        "Sorry, Slim Plotter encountered a problem loading your data:\n" +
        stackTrace, "Slim Plotter", JOptionPane.ERROR_MESSAGE);
      System.exit(4);
    }

    setProgress(progress, 1000); // 100%
    progress.close();

    plotData(true, true, true);

    try { Thread.sleep(200); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
  }

  // -- SlimPlotter methods --

  private Thread plotThread;
  private boolean plotCanceled;
  private boolean doRecalc, doRescale, doRefit;

  /** Plots the data in a separate thread. */
  public void plotData(final boolean recalc,
    final boolean rescale, final boolean refit)
  {
    final SlimPlotter sp = this;
    new Thread("PlotSpawner") {
      public void run() {
        synchronized (sp) {
          if (plotThread != null) {
            // wait for old thread to cancel out
            plotCanceled = true;
            try { plotThread.join(); }
            catch (InterruptedException exc) { exc.printStackTrace(); }
          }
          sp.doRecalc = recalc;
          sp.doRescale = rescale;
          sp.doRefit = refit;
          plotCanceled = false;
          plotThread = new Thread(sp, "Plotter");
          plotThread.start();
        }
      }
    }.start();
  }

  /** Sets the currently selected range component's color widget table. */
  public void setWidgetTable(float[][] table) {
//    float[][] oldTable = colorWidget.getTableView();
//    float[] alpha = oldTable.length > 3 ? oldTable[3] : null;
//    table = ColorUtil.adjustColorTable(table, alpha, true);
    colorWidget.setTableView(table);
  }

  /** Logs the given output to the appropriate location. */
  public void log(String msg) {
    final String message = msg;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() { System.err.println(message); }
    });
  }

  // -- ActionListener methods --

  /** Handles checkbox and button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == menuFileExit) System.exit(0);
    else if (src == menuViewSaveProj) {
      String save = decayPlot.getProjectionControl().getSaveString();
      clip.setClipboardContents(save);
    }
    else if (src == menuViewLoadProj) {
      String save = clip.getClipboardContents();
      if (save != null) {
        try {
          decayPlot.getProjectionControl().setSaveString(save);
        }
        catch (VisADException exc) { }
        catch (RemoteException exc) { }
      }
    }
    else if (src == cOverride) {
      boolean manual = cOverride.isSelected();
      cMinValue.setEnabled(manual);
      cMaxValue.setEnabled(manual);
      updateColorScale();
    }
    else if (src == lutLoad) {
      // ask user to specify the file
      int returnVal = lutBox.showOpenDialog(masterWindow);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = lutBox.getSelectedFile();

      float[][] table = ColorUtil.loadColorTable(file);
      if (table == null) {
        JOptionPane.showMessageDialog(masterWindow, "Error reading LUT file.",
          "Cannot load color table", JOptionPane.ERROR_MESSAGE);
      }
      else setWidgetTable(table);
    }
    else if (src == lutSave) {
      // ask user to specify the file
      int returnVal = lutBox.showSaveDialog(masterWindow);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = lutBox.getSelectedFile();
      String s = file.getAbsolutePath();
      if (!s.toLowerCase().endsWith(".lut")) file = new File(s + ".lut");

      boolean success =
        ColorUtil.saveColorTable(colorWidget.getTableView(), file);
      if (!success) {
        JOptionPane.showMessageDialog(masterWindow, "Error writing LUT file.",
          "Cannot save color table", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if (src == lutPresets) {
      lutsMenu.show(lutPresets, lutPresets.getWidth(), 0);
    }
    else if (src == linear || src == log) plotData(true, true, true);
    else if (src == dataSurface || src == dataLines ||
      src == colorHeight || src == colorTau)
    {
      plotData(true, false, false);
    }
    else if (src == fitSurface || src == fitLines ||
      src == resSurface || src == resLines)
    {
      plotData(true, false, true);
    }
    else if (src == perspective || src == parallel) {
      try {
        decayPlot.getGraphicsModeControl().setProjectionPolicy(
          parallel.isSelected() ? DisplayImplJ3D.PARALLEL_PROJECTION :
          DisplayImplJ3D.PERSPECTIVE_PROJECTION);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == showData) decayRend.toggle(showData.isSelected());
    else if (src == showLine) lineRend.toggle(showLine.isSelected());
    else if (src == showBox) {
      try { decayPlot.getDisplayRenderer().setBoxOn(showBox.isSelected()); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == showScale) {
      try {
        boolean scale = showScale.isSelected();
        decayPlot.getGraphicsModeControl().setScaleEnable(scale);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == showFit) {
      fitRend.toggle(showFit.isSelected());
    }
    else if (src == showResiduals) {
      resRend.toggle(showResiduals.isSelected());
    }
    else if (src == showFWHMs) fwhmRend.toggle(showFWHMs.isSelected());
    else if (src == zOverride) {
      boolean manual = zOverride.isSelected();
      zScaleValue.setEnabled(manual);
      if (!manual) plotData(true, true, false);
    }
    else if (src == exportData) {
      // get output file from user
      JFileChooser jc = new JFileChooser(System.getProperty("user.dir"));
      jc.addChoosableFileFilter(new ExtensionFileFilter("txt",
        "Text files"));
      int rval = jc.showSaveDialog(exportData);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = jc.getSelectedFile();
      if (file == null) return;

      // write currently displayed binned data to file
      try {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.println(timeBins + " x " + channels +
          " (count=" + twoDPane.getROICount() +
          ", percent=" + twoDPane.getROIPercent() +
          ", maxVal=" + maxVal + ")");
        for (int c=0; c<channels; c++) {
          for (int t=0; t<timeBins; t++) {
            if (t > 0) out.print("\t");
            float s = samps[timeBins * c + t];
            int is = (int) s;
            if (is == s) out.print(is);
            else out.print(s);
          }
          out.println();
        }
        out.close();
      }
      catch (IOException exc) {
        JOptionPane.showMessageDialog(exportData,
          "There was a problem writing the file: " + exc.getMessage(),
          "Slim Plotter", JOptionPane.ERROR_MESSAGE);
      }
    }
    else {
      String cmd = e.getActionCommand();
      if (cmd != null && cmd.startsWith("lut")) {
        // apply the chosen LUT preset
        setWidgetTable(LUTS[Integer.parseInt(cmd.substring(3))]);
      }
      else { // OK button
        width = parse(wField.getText(), width);
        height = parse(hField.getText(), height);
        timeBins = parse(tField.getText(), timeBins);
        channels = parse(cField.getText(), channels);
        timeRange = parse(trField.getText(), timeRange);
        minWave = parse(wlField.getText(), minWave);
        waveStep = parse(sField.getText(), waveStep);
        numExp = parse(fitField.getText(), numExp);
        useLMA = lmChoice.isSelected();
        adjustPeaks = peaksBox.isSelected();
        computeFWHMs = fwhmBox.isSelected();
        cutEnd = cutBox.isSelected();
        cVisible = new boolean[channels];
        Arrays.fill(cVisible, true);
        paramDialog.setVisible(false);
      }
    }
  }

  // -- ChangeListener methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) {
    plotData(true, false, true);
  }

  // -- DocumentListener methods --

  public void changedUpdate(DocumentEvent e) { documentUpdate(e); }
  public void insertUpdate(DocumentEvent e) { documentUpdate(e); }
  public void removeUpdate(DocumentEvent e) { documentUpdate(e); }

  private void documentUpdate(DocumentEvent e) {
    Document doc = e.getDocument();
    if (doc == zScaleValue.getDocument()) updateZAxis();
    else updateColorScale(); // cMinValue or cMaxValue
  }

  // -- Runnable methods --

  public void run() {
    decayPlot.disableAction();
    boolean doLog = log.isSelected();

    if (doRecalc) {
      ProgressMonitor progress = new ProgressMonitor(null, "Plotting data",
        "Calculating sums", 0,
        channels * timeBins + (doRefit ? channels : 0) + 1);
      progress.setMillisToPopup(100);
      progress.setMillisToDecideToPopup(50);
      int p = 0;

      boolean doDataLines = dataLines.isSelected();
      boolean doFitLines = fitLines.isSelected();
      boolean doResLines = resLines.isSelected();
      boolean doTauColors = colorTau.isSelected();

      // update scale labels for log scale
      if (doLog) {
        AxisScale zScale = zMap.getAxisScale();
        Hashtable hash = new Hashtable();
        for (int i=1, v=1; v<=maxVal; i++, v*=BASE) {
          hash.put(new Double(Math.log(v) / BASE_LOG),
            i > 3 ? "1e" + i : "" + v);
        }
        try {
          zScale.setLabelTable(hash);
        }
        catch (VisADException exc) { exc.printStackTrace(); }
      }

      // calculate samples
      int numChanVis = 0;
      for (int c=0; c<channels; c++) {
        if (cVisible[c]) numChanVis++;
      }
      samps = new float[numChanVis * timeBins];
      maxVal = 0;
      float[] maxVals = new float[numChanVis];
      for (int c=0, cc=0; c<channels; c++) {
        if (!cVisible[c]) continue;
        for (int t=0; t<timeBins; t++) {
          int ndx = timeBins * cc + t;
          int sum = 0;
          if (twoDPane.getROICount() == 1) {
            int roiX = twoDPane.getROIX();
            int roiY = twoDPane.getROIY();
            sum = values[c][roiY][roiX][t];
          }
          else {
            boolean[][] roiMask = twoDPane.getROIMask();
            for (int h=0; h<height; h++) {
              for (int w=0; w<width; w++) {
                if (roiMask[h][w]) sum += values[c][h][w][t];
              }
            }
          }
          samps[ndx] = sum;
          if (samps[ndx] > maxVal) maxVal = samps[ndx];
          if (samps[ndx] > maxVals[cc]) maxVals[cc] = samps[ndx];
          setProgress(progress, ++p, false);
          if (progress.isCanceled()) plotCanceled = true;
          if (plotCanceled) break;
        }
        if (plotCanceled) break;
        cc++;
      }

      // full width half maxes
      float[][][] fwhmLines = null;
      if (computeFWHMs) {
        log("Calculating full width half maxes");
        fwhmLines = new float[channels][3][2];
        int grandTotal = 0;
        for (int c=0, cc=0; c<channels; c++) {
          if (!cVisible[c]) continue;
          // sum across all pixels
          int[] sums = new int[timeBins];
          int sumTotal = 0;
          for (int t=0; t<timeBins; t++) {
            if (twoDPane.getROICount() == 1) {
              int roiX = twoDPane.getROIX();
              int roiY = twoDPane.getROIY();
              sums[t] = values[c][roiY][roiX][t];
            }
            else {
              boolean[][] roiMask = twoDPane.getROIMask();
              for (int h=0; h<height; h++) {
                for (int w=0; w<width; w++) {
                  if (roiMask[h][w]) sums[t] += values[c][h][w][t];
                }
              }
            }
            sumTotal += sums[t];
          }
          int maxSum = 0;
          for (int t=0; t<timeBins; t++) if (sums[t] > maxSum) maxSum = sums[t];
          grandTotal += sumTotal;
          // find full width half max
          float half = maxSum / 2f;
          float fwhm1 = Float.NaN, fwhm2 = Float.NaN;
          for (int t=0; t<timeBins-1; t++) {
            if (sums[t] <= half && sums[t + 1] >= half) { // upslope
              float q = (half - sums[t]) / (sums[t + 1] - sums[t]);
              fwhm1 = timeRange * (t + q) / (timeBins - 1); // binsToNano
            }
            else if (sums[t] >= half && sums[t + 1] <= half) { // downslope
              float q = (half - sums[t + 1]) / (sums[t] - sums[t + 1]);
              fwhm2 = timeRange * (t + 1 - q) / (timeBins - 1); // binsToNano
            }
            if (fwhm1 == fwhm1 && fwhm2 == fwhm2) break;
          }
          fwhmLines[c][0][0] = fwhm1;
          fwhmLines[c][0][1] = fwhm2;
          fwhmLines[c][1][0] = fwhmLines[c][1][1] = minWave + c * waveStep;
          fwhmLines[c][2][0] = fwhmLines[c][2][1] = half;
          String s = "#" + (c + 1);
          while (s.length() < 3) s = " " + s;
          float h1 = 1000 * fwhm1, h2 = 1000 * fwhm2;
          log("\tChannel " + s + ": fwhm = " + (h2 - h1) + " ps");
          log("\t             counts = " + sumTotal);
          log("\t             peak = " + sums[maxPeak]);
          log("\t             center = " + ((h1 + h2) / 2) + " ps");
          log("\t             range = [" + h1 + ", " + h2 + "] ps");
          if (plotCanceled) break;
          cc++;
        }
        log("\tTotal counts = " + grandTotal);
      }

      // curve fitting
      double[][] fitResults = null;
      if (adjustPeaks && doRefit) {
        // perform exponential curve fitting: y(x) = a * e^(-b*t) + c
        progress.setNote("Fitting curves");
        fitResults = new double[channels][];
        tau = new float[channels][numExp];
        for (int c=0; c<channels; c++) Arrays.fill(tau[c], Float.NaN);
        int num = timeBins - maxPeak;

        // HACK - cut off last 1500 ps from lifetime histogram,
        // to improve accuracy of fit.
        if (cutEnd) {
          int cutBins = (int) picoToBins(1500);
          if (num > cutBins + 5) num -= cutBins;
        }

        StringBuffer equation = new StringBuffer();
        equation.append("y(t) = ");
        for (int i=0; i<numExp; i++) {
          equation.append("a");
          equation.append(i + 1);
          equation.append(" * e^(-t/");
          equation.append(TAU);
          equation.append(i + 1);
          equation.append(") + ");
        }
        equation.append("c");
        log("Computing fit parameters: " + equation.toString());
        int[] data = new int[num];
        for (int c=0, cc=0; c<channels; c++) {
          if (!cVisible[c]) {
            fitResults[c] = null;
            continue;
          }
          log("\tChannel #" + (c + 1) + ":");

          CurveFitter fitter;
          if (useLMA) fitter = new LMCurveFitter();
          else fitter = new GACurveFitter();
          for (int i=0; i<num; i++) {
            data[i] = (int) samps[timeBins * cc + maxPeak + i];
          }
          fitter.setDegrees(numExp);
          fitter.setData(data);
          fitter.estimate();
          for (int i=0; i<100; i++) fitter.iterate();
          double[][] results = fitter.getCurve();
          log("\t\tchi2=" + fitter.getReducedChiSquaredError());
          for (int i=0; i<numExp; i++) {
            log("\t\ta" + (i + 1) + "=" +
              (100 * results[i][0] / maxVals[cc]) + "%");
            tau[c][i] = binsToPico((float) (1 / results[i][1]));
            log("\t\t" + TAU + (i + 1) + "=" + tau[c][i] + " ps");
          }
          log("\t\tc=" + results[0][2]);
          fitResults[c] = new double[2 * numExp + 1];
          for (int i=0; i<numExp; i++) {
            int e = 2 * i;
            fitResults[c][e] = results[i][0];
            fitResults[c][e + 1] = results[i][1];
          }
          fitResults[c][2 * numExp] = results[0][2];

          setProgress(progress, ++p, false);
          cc++;
        }
      }

      tauMin = tauMax = Float.NaN;
      if (tau != null) {
        tauMin = tauMax = tau[0][0];
        for (int i=1; i<tau.length; i++) {
          if (tau[i][0] < tauMin) tauMin = tau[i][0];
          if (tau[i][0] > tauMax) tauMax = tau[i][0];
        }
      }
      StringBuffer sb = new StringBuffer();
      sb.append("Decay curve for ");
      if (twoDPane.getROICount() == 1) {
        int roiX = twoDPane.getROIX();
        int roiY = twoDPane.getROIY();
        sb.append("(");
        sb.append(roiX);
        sb.append(", ");
        sb.append(roiY);
        sb.append(")");
      }
      else {
        sb.append(twoDPane.getROICount());
        sb.append(" pixels (");
        sb.append(twoDPane.getROIPercent());
        sb.append("%)");
      }
      if (tauMin == tauMin && tauMax == tauMax) {
        sb.append("; ");
        sb.append(TAU);
        sb.append("=");
        if (tauMin != tauMax) {
          sb.append("[");
          sb.append(tauMin);
          sb.append(", ");
          sb.append(tauMax);
          sb.append("]");
        }
        else sb.append(tauMin);
        sb.append(" ps");
      }
      decayLabel.setText(sb.toString());

      try {
        // construct domain set for 3D surface plots
        float[][] bcGrid = new float[2][timeBins * numChanVis];
        for (int c=0, cc=0; c<channels; c++) {
          if (!cVisible[c]) continue;
          for (int t=0; t<timeBins; t++) {
            int ndx = timeBins * cc + t;
            bcGrid[0][ndx] = timeBins > 1 ? t * timeRange / (timeBins - 1) : 0;
            bcGrid[1][ndx] = channels > 1 ?
              c * (maxWave - minWave) / (channels - 1) + minWave : 0;
          }
          cc++;
        }
        Gridded2DSet bcSet = new Gridded2DSet(bc, bcGrid,
          timeBins, numChanVis, null, bcUnits, null, false);

        // compile color values for 3D surface plot
        float[] colors = new float[numChanVis * timeBins];
        for (int c=0, cc=0; c<channels; c++) {
          if (!cVisible[c]) continue;
          for (int t=0; t<timeBins; t++) {
            int ndx = timeBins * cc + t;
            colors[ndx] = doTauColors ?
              (tau == null ? Float.NaN : tau[c][0]) : samps[ndx];
          }
          cc++;
        }

        float[] fitSamps = null, residuals = null;
        if (fitResults != null) {
          // compute finite sampling matching fitted exponentials
          fitSamps = new float[numChanVis * timeBins];
          residuals = new float[numChanVis * timeBins];
          for (int c=0, cc=0; c<channels; c++) {
            if (!cVisible[c]) continue;
            double[] q = fitResults[c];
            for (int t=0; t<timeBins; t++) {
              int ndx = timeBins * cc + t;
              int et = t - maxPeak; // adjust for peak alignment
              if (et < 0) fitSamps[ndx] = residuals[ndx] = 0;
              else {
                float sum = 0;
                for (int i=0; i<numExp; i++) {
                  int e = 2 * i;
                  sum += (float) (q[e] * Math.exp(-q[e + 1] * et));
                }
                sum += (float) q[2 * numExp];
                fitSamps[ndx] = sum;
                residuals[ndx] = samps[ndx] - fitSamps[ndx];
              }
            }
            cc++;
          }
        }

        // construct "Data" plot
        if (doLog) {
          // convert samples to log values for plotting
          // this is done AFTER any computations involving samples (above)
          for (int i=0; i<samps.length; i++) {
            samps[i] = linearToLog(samps[i]);
          }
        }
        FlatField ff = new FlatField(bcvFunc, bcSet);
        ff.setSamples(new float[][] {samps, colors}, false);
        decayRef.setData(doDataLines ? makeLines(ff) : ff);

        if (fwhmLines != null) {
          // construct "FWHMs" plot
          SampledSet[] fwhmSets = new SampledSet[channels];
          for (int c=0; c<channels; c++) {
            if (doLog) {
              // convert samples to log values for plotting
              // this is done AFTER any computations involving samples (above)
              for (int i=0; i<fwhmLines[c].length; i++) {
                for (int j=0; j<fwhmLines[c][i].length; i++) {
                  fwhmLines[c][i][j] = linearToLog(fwhmLines[c][i][j]);
                }
              }
            }
            if (channels > 1) {
              fwhmSets[c] = new Gridded3DSet(bcv, fwhmLines[c], 2);
            }
            else {
              fwhmSets[c] = new Gridded2DSet(bv,
                new float[][] {fwhmLines[c][0], fwhmLines[c][2]}, 2);
            }
          }
          fwhmRef.setData(new UnionSet(fwhmSets));
        }

        if (fitSamps != null) {
          // construct "Fit" plot
          if (doLog) {
            // convert samples to log values for plotting
            // this is done AFTER any computations involving samples (above)
            for (int i=0; i<fitSamps.length; i++) {
              fitSamps[i] = linearToLog(fitSamps[i]);
            }
          }
          FlatField fit = new FlatField(bcvFuncFit, bcSet);
          fit.setSamples(new float[][] {fitSamps}, false);
          fitRef.setData(doFitLines ? makeLines(fit) : fit);
        }

        if (residuals != null) {
          // construct "Residuals" plot
          if (doLog) {
            // convert samples to log values for plotting
            // this is done AFTER any computations involving samples (above)
            for (int i=0; i<residuals.length; i++) {
              residuals[i] = linearToLog(residuals[i]);
            }
          }
          FlatField res = new FlatField(bcvFuncRes, bcSet);
          res.setSamples(new float[][] {residuals}, false);
          resRef.setData(doResLines ? makeLines(res) : res);
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      setProgress(progress, ++p, false);
      progress.close();

      // update scale labels for linear scale
      if (!doLog) {
        AxisScale zScale = zMap.getAxisScale();
        zScale.createStandardLabels(maxVal, 0, 0, maxVal);
      }

      updateColorScale();
    }

    if (doRescale) {
      float d = Float.NaN;
      boolean manual = zOverride.isSelected();
      if (manual) {
        try { d = Float.parseFloat(zScaleValue.getText()); }
        catch (NumberFormatException exc) { }
      }
      else {
        zScaleValue.getDocument().removeDocumentListener(this);
        zScaleValue.setText("" + maxVal);
        zScaleValue.getDocument().addDocumentListener(this);
      }
      float minZ = 0;
      float maxZ = d == d ? d : maxVal;
      if (doLog) maxZ = linearToLog(maxZ);
      if (maxZ != maxZ || maxZ == 0) maxZ = 1;
      try {
        zMap.setRange(minZ, maxZ);
        zMapFit.setRange(minZ, maxZ);
        zMapRes.setRange(minZ, maxZ);
        decayPlot.reAutoScale();
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    decayPlot.enableAction();
    plotThread = null;
  }

  // -- WindowListener methods --

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { System.exit(0); }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }

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

  /** Converts value in picoseconds to histogram bins. */
  private float picoToBins(float pico) {
    return (timeBins - 1) * pico / timeRange / 1000;
  }

  /** Converts value in histogram bins to picoseconds. */
  private float binsToPico(float bins) {
    return 1000 * timeRange * bins / (timeBins - 1);
  }

  /** Converts linear value to logarithm value. */
  private float linearToLog(float v) {
    return (float) v >= 1 ? (float) Math.log(v) / BASE_LOG : 0;
  }

  private JPanel makeRadioPanel(String title,
    JRadioButton b1, JRadioButton b2)
  {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new TitledBorder(title));
    p.add(b1);
    p.add(b2);
    ButtonGroup group = new ButtonGroup();
    group.add(b1);
    group.add(b2);
    b1.addActionListener(this);
    b2.addActionListener(this);
    return p;
  }

  private FieldImpl makeLines(FlatField surface) {
    try {
      // HACK - horrible conversion from aligned Gridded2DSet to ProductSet
      // probably could eliminate this by writing cleaner logic to convert
      // from 2D surface to 1D lines...
      Linear1DSet timeSet = new Linear1DSet(bType, 0,
        timeRange, timeBins, null, new Unit[] {bcUnits[0]}, null);
      int numChanVis = 0;
      for (int c=0; c<channels; c++) {
        if (cVisible[c]) numChanVis++;
      }
      float[][] cGrid = new float[1][numChanVis];
      for (int c=0, cc=0; c<channels; c++) {
        if (!cVisible[c]) continue;
        cGrid[0][cc++] = channels > 1 ?
          c * (maxWave - minWave) / (channels - 1) + minWave : 0;
      }
      Gridded1DSet waveSet = new Gridded1DSet(cType,
        cGrid, numChanVis, null, new Unit[] {bcUnits[1]}, null, false);
      ProductSet prodSet = new ProductSet(new SampledSet[] {timeSet, waveSet});
      float[][] samples = surface.getFloats(false);
      FunctionType ffType = (FunctionType) surface.getType();
      surface = new FlatField(ffType, prodSet);
      surface.setSamples(samples, false);

      return (FieldImpl) surface.domainFactor(cType);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  private void updateZAxis() {
    float f = Float.NaN;
    try { f = Float.parseFloat(zScaleValue.getText()); }
    catch (NumberFormatException exc) { }
    if (f == f) plotData(false, true, false);
  }

  private void updateColorScale() {
    boolean manual = cOverride.isSelected();
    float min = Float.NaN, max = Float.NaN;
    if (manual) {
      try { min = Float.parseFloat(cMinValue.getText()); }
      catch (NumberFormatException exc) { }
      try { max = Float.parseFloat(cMaxValue.getText()); }
      catch (NumberFormatException exc) { }
    }
    else {
      if (colorHeight.isSelected()) {
        min = 0;
        max = maxVal;
      }
      else { // colorTau.isSelected()
        min = tauMin;
        max = tauMax;
      }
      cMinValue.getDocument().removeDocumentListener(this);
      cMinValue.setText("" + min);
      cMinValue.getDocument().addDocumentListener(this);
      cMaxValue.getDocument().removeDocumentListener(this);
      cMaxValue.setText("" + max);
      cMaxValue.getDocument().addDocumentListener(this);
    }
    if (min == min && max == max && min < max) {
      try { vMap.setRange(min, max); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
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

  private static void setProgress(ProgressMonitor progress, int p) {
    setProgress(progress, p, false);
  }

  private static void setProgress(ProgressMonitor progress,
    int p, boolean quitOnCancel)
  {
    progress.setProgress(p);
    if (quitOnCancel && progress.isCanceled()) System.exit(0);
  }

  // -- Helper classes --

  /**
   * Helper class for clipboard interaction, stolen from
   * <a href="http://www.javapractices.com/Topic82.cjp">Java Practices</a>.
   */
  public final class TextTransfer implements ClipboardOwner {
    public void setClipboardContents(String value) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (clipboard != null) {
        clipboard.setContents(new StringSelection(value), this);
      }
    }

    public String getClipboardContents() {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (clipboard == null) return null;
      String result = null;
      Transferable contents = clipboard.getContents(null);
      boolean hasTransferableText = contents != null &&
        contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      if (hasTransferableText) {
        try {
          result = (String) contents.getTransferData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException ex) { ex.printStackTrace(); }
        catch (IOException ex) { ex.printStackTrace(); }
      }
      return result;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) { }
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
