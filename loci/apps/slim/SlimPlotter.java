//
// SlimPlotter.java
//

/*
Coded in 2006 by Curtis Rueden, for Long Yan and others.
Permission is granted to use this code for anything.
*/

package loci.apps.slim;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import jaolho.data.lma.implementations.JAMAMatrix;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import loci.formats.in.SDTInfo;
import loci.formats.in.SDTReader;
import loci.formats.DataTools;
import loci.formats.ExtensionFileFilter;
import loci.visbio.util.BreakawayPanel;
import loci.visbio.util.OutputConsole;
import visad.*;
import visad.bom.CurveManipulationRendererJ3D;
import visad.java3d.*;

/** A tool for visualization of spectral lifetime data. */
public class SlimPlotter implements ActionListener,
  ChangeListener, DisplayListener, Runnable, WindowListener
{

  // -- Constants --

  /** Default orientation for decay curves display. */
  private static final double[] MATRIX = {
    0.2821, 0.1503, -0.0201, 0.0418,
    -0.0500, 0.1323, 0.2871, 0.1198,
    0.1430, -0.2501, 0.1408, 0.0089,
    0.0000, 0.0000, 0.0000, 1.0000
  };

  private static final char TAU = 'T';

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
  private boolean adjustPeaks;
  private boolean[] cVisible;
  private int maxPeak;

  // ROI parameters
  private float[][] roiGrid;
  private boolean[][] roiMask;
  private UnionSet curveSet;
  private Irregular2DSet roiSet;
  private DataReferenceImpl roiRef;
  private int roiX, roiY;
  private int roiCount;
  private double roiPercent;
  private float maxVal;

  // GUI components for parameter dialog box
  private JDialog paramDialog;
  private JTextField wField, hField, tField, cField, trField, wlField, sField;
  private JCheckBox peaksBox;

  // GUI components for intensity pane
  private JSlider cSlider;
  private JCheckBox cToggle;

  // GUI components for decay pane
  private JLabel decayLabel;
  private JRadioButton linear, log;
  private JRadioButton perspective, parallel;
  private JRadioButton dataSurface, dataLines;
  private JRadioButton fitSurface, fitLines;
  private JRadioButton resSurface, resLines;
  private JCheckBox showData, showScale;
  private JCheckBox showBox, showLine;
  private JCheckBox showFit, showResiduals;
  private JSpinner numCurves;
  private JButton exportData;

  // other GUI components
  private OutputConsole console;

  // VisAD objects
  private Unit[] bcUnits;
  private RealType bType, cType;
  private RealTupleType bc;
  private FunctionType bcvFunc, bcvFuncFit, bcvFuncRes;
  private ScalarMap zMap, zMapFit, zMapRes, vMap, vMapFit, vMapRes;
  private DataRenderer decayRend, fitRend, resRend, lineRend;
  private DataReferenceImpl decayRef, fitRef, resRef;
  private DisplayImpl iPlot, decayPlot;
  private AnimationControl ac;

  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    console = new OutputConsole("Log");
    System.setErr(new ConsoleStream(new PrintStream(console)));
    console.getTextArea().setColumns(54);
    console.getTextArea().setRows(10);

    // progress estimate:
    // * Reading data - 70%
    // * Creating types - 1%
    // * Building displays - 7%
    // * Constructing images - 14%
    // * Adjusting peaks - 4%
    // * Creating plots - 4%
    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching SlimPlotter", "Initializing", 0, 1000);
    progress.setMillisToPopup(0);
    progress.setMillisToDecideToPopup(0);

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
        url = "http://www.hp.com/products1/unix/java/java2/java3d/downloads/" +
          "index.html";
      }
      else if (os.indexOf("irix") >= 0) {
        url = "http://www.sgi.com/products/evaluation/6.5_java3d_1.3.1/";
      }
      JOptionPane.showMessageDialog(null,
        "SlimPlotter requires Java3D, but it was not found." +
        (url == null ? "" : ("\nPlease install it from:\n" + url)),
        "SlimPlotter", JOptionPane.ERROR_MESSAGE);
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
    SDTInfo info = reader.getInfo(file.getPath());
    reader.close();
    int offset = info.dataBlockOffs + 22;
    width = info.width;
    height = info.height;
    timeBins = info.timeBins;
    channels = info.channels;
    timeRange = 12.5f;
    minWave = 400;
    waveStep = 10;

    // show dialog confirming data parameters
    paramDialog = new JDialog((Frame) null, "SlimPlotter", true);
    JPanel paramPane = new JPanel();
    paramPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    paramDialog.setContentPane(paramPane);
    paramPane.setLayout(new GridLayout(10, 3));
    wField = addRow(paramPane, "Image width", width, "pixels");
    hField = addRow(paramPane, "Image height", height, "pixels");
    tField = addRow(paramPane, "Time bins", timeBins, "");
    cField = addRow(paramPane, "Channel count", channels, "");
    trField = addRow(paramPane, "Time range", timeRange, "nanoseconds");
    wlField = addRow(paramPane, "Starting wavelength", minWave, "nanometers");
    sField = addRow(paramPane, "Channel width", waveStep, "nanometers");
    JButton ok = new JButton("OK");
    paramDialog.getRootPane().setDefaultButton(ok);
    ok.addActionListener(this);
    // row 8
    peaksBox = new JCheckBox("Align peaks", true);
    paramPane.add(peaksBox);
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    // row 9
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    // row 10
    paramPane.add(new JLabel());
    paramPane.add(ok);
    paramDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension ps = paramDialog.getSize();
    paramDialog.setLocation((screenSize.width - ps.width) / 2,
      (screenSize.height - ps.height) / 2);
    paramDialog.setVisible(true);
    maxWave = minWave + (channels - 1) * waveStep;
    roiCount = width * height;
    roiPercent = 100;

    // pop up progress monitor
    progress.setProgress(1); // estimate: 0.1%
    if (progress.isCanceled()) System.exit(0);

    // read pixel data
    progress.setNote("Reading data");
    DataInputStream fin = new DataInputStream(new FileInputStream(file));
    fin.skipBytes(offset); // skip to data
    byte[] data = new byte[2 * channels * height * width * timeBins];
    int blockSize = 65536;
    for (int off=0; off<data.length; off+=blockSize) {
      int len = data.length - off;
      if (len > blockSize) len = blockSize;
      fin.readFully(data, off, len);
      progress.setProgress(700 *
        (off + blockSize) / data.length); // estimate: 0% -> 70%
      if (progress.isCanceled()) System.exit(0);
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
    RealType vType2 = RealType.getRealType("color");
    RealTupleType vv = new RealTupleType(vType, vType2);
    bcvFunc = new FunctionType(bc, vv);
    RealType vTypeFit = RealType.getRealType("value_fit");
    bcvFuncFit = new FunctionType(bc, vTypeFit);
    RealType vTypeRes = RealType.getRealType("value_res");
    bcvFuncRes = new FunctionType(bc, vTypeRes);
    progress.setProgress(710); // estimate: 71%
    if (progress.isCanceled()) System.exit(0);

    // plot intensity data in 2D display
    progress.setNote("Building displays");
    iPlot = new DisplayImplJ3D("intensity", new TwoDDisplayRendererJ3D());
    iPlot.getMouseBehavior().getMouseHelper().setFunctionMap(new int[][][] {
      {{MouseHelper.DIRECT, MouseHelper.NONE}, // L, shift-L
       {MouseHelper.NONE, MouseHelper.NONE}}, // ctrl-L, ctrl-shift-L
      {{MouseHelper.CURSOR_TRANSLATE, MouseHelper.CURSOR_ZOOM}, // M, shift-M
       {MouseHelper.CURSOR_ROTATE, MouseHelper.NONE}}, // ctrl-M, ctrl-shift-M
      {{MouseHelper.ROTATE, MouseHelper.ZOOM}, // R, shift-R
       {MouseHelper.TRANSLATE, MouseHelper.NONE}}, // ctrl-R, ctrl-shift-R
    });
    iPlot.enableEvent(DisplayEvent.MOUSE_DRAGGED);
    iPlot.addDisplayListener(this);
    progress.setProgress(720); // estimate: 72%
    if (progress.isCanceled()) System.exit(0);

    iPlot.addMap(new ScalarMap(xType, Display.XAxis));
    iPlot.addMap(new ScalarMap(yType, Display.YAxis));
    iPlot.addMap(new ScalarMap(vType, Display.RGB));
    iPlot.addMap(new ScalarMap(cType, Display.Animation));
    DataReferenceImpl intensityRef = new DataReferenceImpl("intensity");
    iPlot.addReference(intensityRef);
    progress.setProgress(730); // estimate: 73%
    if (progress.isCanceled()) System.exit(0);

    // set up curve manipulation renderer in 2D display
    roiGrid = new float[2][width * height];
    roiMask = new boolean[height][width];
    for (int h=0; h<height; h++) {
      for (int w=0; w<width; w++) {
        int ndx = h * width + w;
        roiGrid[0][ndx] = w;
        roiGrid[1][ndx] = h;
        roiMask[h][w] = true;
      }
    }
    final DataReferenceImpl curveRef = new DataReferenceImpl("curve");
    UnionSet dummyCurve = new UnionSet(xy, new Gridded2DSet[] {
      new Gridded2DSet(xy, new float[][] {{0}, {0}}, 1)
    });
    curveRef.setData(dummyCurve);
    CurveManipulationRendererJ3D curve =
      new CurveManipulationRendererJ3D(0, 0, true);
    iPlot.addReferences(curve, curveRef);
    CellImpl cell = new CellImpl() {
      public void doAction() throws VisADException, RemoteException {
        // save latest drawn curve
        curveSet = (UnionSet) curveRef.getData();
      }
    };
    cell.addReference(curveRef);
    roiRef = new DataReferenceImpl("roi");
    roiRef.setData(new Real(0)); // dummy
    iPlot.addReference(roiRef, new ConstantMap[] {
      new ConstantMap(0, Display.Blue),
      new ConstantMap(0.1, Display.Alpha)
    });
    progress.setProgress(740); // estimate: 74%
    if (progress.isCanceled()) System.exit(0);

    ac = (AnimationControl) iPlot.getControl(AnimationControl.class);
    iPlot.getProjectionControl().setMatrix(
      iPlot.make_matrix(0, 0, 0, 0.85, 0, 0, 0));

    progress.setProgress(750); // estimate: 75%
    if (progress.isCanceled()) System.exit(0);

    // plot decay curves in 3D display
    decayPlot = new DisplayImplJ3D("decay");
    ScalarMap xMap = new ScalarMap(bType, Display.XAxis);
    ScalarMap yMap = new ScalarMap(cType, Display.YAxis);
    zMap = new ScalarMap(vType, Display.ZAxis);
    zMapFit = new ScalarMap(vTypeFit, Display.ZAxis);
    zMapRes = new ScalarMap(vTypeRes, Display.ZAxis);
    vMap = new ScalarMap(vType, Display.RGB);
    //vMapFit = new ScalarMap(vTypeFit, Display.RGB);
    vMapRes = new ScalarMap(vTypeRes, Display.RGB);
    decayPlot.addMap(xMap);
    decayPlot.addMap(yMap);
    decayPlot.addMap(zMap);
    decayPlot.addMap(zMapFit);
    decayPlot.addMap(zMapRes);
    decayPlot.addMap(vMap);
    //decayPlot.addMap(vMapFit);
    decayPlot.addMap(vMapRes);
    progress.setProgress(760); // estimate: 76%
    if (progress.isCanceled()) System.exit(0);

    decayRend = new DefaultRendererJ3D();
    decayRef = new DataReferenceImpl("decay");
    decayPlot.addReferences(decayRend, decayRef);
    if (adjustPeaks) {
      fitRend = new DefaultRendererJ3D();
      fitRef = new DataReferenceImpl("fit");
      decayPlot.addReferences(fitRend, fitRef, new ConstantMap[] {
        new ConstantMap(1.0, Display.Red),
        new ConstantMap(1.0, Display.Green),
        new ConstantMap(1.0, Display.Blue)
      });
      fitRend.toggle(false);
      resRend = new DefaultRendererJ3D();
      resRef = new DataReferenceImpl("residuals");
      decayPlot.addReferences(resRend, resRef);
      resRend.toggle(false);
    }
    progress.setProgress(770); // estimate: 77%
    if (progress.isCanceled()) System.exit(0);

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
    pc.setMatrix(MATRIX);
    pc.setAspectCartesian(
      new double[] {2, 1, 1});
    progress.setProgress(780); // estimate: 78%
    if (progress.isCanceled()) System.exit(0);

    // convert byte data to unsigned shorts
    progress.setNote("Constructing images");
    values = new int[channels][height][width][timeBins];
    float[][][] pix = new float[channels][1][width * height];
    FieldImpl field = new FieldImpl(cxyvFunc, cSet);
    int max = 0;
    int maxChan = 0;
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
            if (val > max) {
              max = val;
              maxChan = c;
            }
            values[c][h][w][t] = val;
            sum += val;
          }
          pix[c][0][width * h + w] = sum;
        }
        progress.setProgress(780 + 140 *
          (height * c + h + 1) / (channels * height)); // estimate: 78% -> 92%
        if (progress.isCanceled()) System.exit(0);
      }
      FlatField ff = new FlatField(xyvFunc, xySet);
      ff.setSamples(pix[c], false);
      field.setSample(c, ff);
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
          else if (t > 20) break; // HACK - too early to give up
        }
        peaks[c] = ndx;
        progress.setProgress(920 + 20 *
          (c + 1) / channels); // estimate: 92% -> 94%
        if (progress.isCanceled()) System.exit(0);
      }
      maxPeak = 0;
      for (int c=1; c<channels; c++) {
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
        progress.setProgress(940 + 20 *
          (c + 1) / channels); // estimate: 94% -> 96%
        if (progress.isCanceled()) System.exit(0);
      }

      // add yellow line to indicate adjusted peak position
      lineRend = new DefaultRendererJ3D();
      DataReferenceImpl peakRef = new DataReferenceImpl("peaks");
      float peakTime = (float) (maxPeak * timeRange / timeBins);
      peakRef.setData(new Gridded2DSet(bc,
        new float[][] {{peakTime, peakTime}, {minWave, maxWave}}, 2));
      decayPlot.addReferences(lineRend, peakRef, new ConstantMap[] {
        new ConstantMap(-1, Display.ZAxis),
        new ConstantMap(0, Display.Blue),
//        new ConstantMap(2, Display.LineWidth)
      });
    }

    // construct 2D pane
    progress.setNote("Creating plots");
    JFrame masterWindow = new JFrame("Slim Plotter - " + file.getName());
    masterWindow.addWindowListener(this);
    JPanel masterPane = new JPanel();
    masterPane.setLayout(new BorderLayout());
    masterWindow.setContentPane(masterPane);
    JPanel intensityPane = new JPanel();
    intensityPane.setLayout(new BorderLayout());
    JPanel iPlotPane = new JPanel() {
      private int height = 380;
      public Dimension getMinimumSize() {
        Dimension min = super.getMinimumSize();
        return new Dimension(min.width, height);
      }
      public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        return new Dimension(pref.width, height);
      }
      public Dimension getMaximumSize() {
        Dimension max = super.getMaximumSize();
        return new Dimension(max.width, height);
      }
    };
    iPlotPane.setLayout(new BorderLayout());
    iPlotPane.add(iPlot.getComponent(), BorderLayout.CENTER);
    intensityPane.add(iPlotPane, BorderLayout.CENTER);

    progress.setProgress(970); // estimate: 97%
    if (progress.isCanceled()) System.exit(0);

    JPanel sliderPane = new JPanel();
    sliderPane.setLayout(new BoxLayout(sliderPane, BoxLayout.X_AXIS));
    intensityPane.add(sliderPane, BorderLayout.SOUTH);
    cSlider = new JSlider(1, channels, 1);
    cSlider.setSnapToTicks(true);
    cSlider.setMajorTickSpacing(channels / 4);
    cSlider.setMinorTickSpacing(1);
    cSlider.setPaintTicks(true);
    cSlider.addChangeListener(this);
    cSlider.setBorder(new EmptyBorder(8, 5, 8, 5));
    sliderPane.add(cSlider);
    cToggle = new JCheckBox("", true);
    cToggle.addActionListener(this);
    sliderPane.add(cToggle);

    intensityRef.setData(field);
    ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
    cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

    progress.setProgress(980); // estimate: 98%
    if (progress.isCanceled()) System.exit(0);

    // construct 3D pane
    JPanel decayPane = new JPanel();
    decayPane.setLayout(new BorderLayout());
    decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

    decayLabel = new JLabel("Decay curve for all pixels");
    decayPane.add(decayLabel, BorderLayout.NORTH);

    JPanel options = new JPanel();
    options.setBorder(new EmptyBorder(8, 5, 8, 5));
    options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

    linear = new JRadioButton("Linear", true);
    log = new JRadioButton("Log", false);
    perspective = new JRadioButton("Perspective", true);
    parallel = new JRadioButton("Parallel", false);
    dataSurface = new JRadioButton("Surface", true);
    dataLines = new JRadioButton("Lines", false);
    fitSurface = new JRadioButton("Surface", false);
    fitLines = new JRadioButton("Lines", true);
    resSurface = new JRadioButton("Surface", false);
    resLines = new JRadioButton("Lines", true);

    JPanel showPanel = new JPanel();
    showPanel.setBorder(new TitledBorder("Show"));
    showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));

    showData = new JCheckBox("Data", true);
    showData.addActionListener(this);
    showPanel.add(showData);
    showScale = new JCheckBox("Scale", true);
    showScale.addActionListener(this);
    showPanel.add(showScale);
    showBox = new JCheckBox("Box", true);
    showBox.addActionListener(this);
    showPanel.add(showBox);
    showLine = new JCheckBox("Line", adjustPeaks);
    showLine.setEnabled(adjustPeaks);
    showLine.addActionListener(this);
    showPanel.add(showLine);
    showFit = new JCheckBox("Fit", false);
    showFit.setEnabled(adjustPeaks);
    showFit.addActionListener(this);
    showPanel.add(showFit);
    showResiduals = new JCheckBox("Residuals", false);
    showResiduals.setEnabled(adjustPeaks);
    showResiduals.addActionListener(this);
    showPanel.add(showResiduals);

    numCurves = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
    numCurves.setMaximumSize(numCurves.getPreferredSize());
    numCurves.addChangeListener(this);

    exportData = new JButton("Export");
    exportData.addActionListener(this);

    progress.setProgress(99); // estimate: 99%
    if (progress.isCanceled()) System.exit(0);

    JPanel leftPanel = new JPanel() {
      public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        Dimension max = super.getMaximumSize();
        return new Dimension(pref.width, max.height);
      }
    };
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.add(intensityPane);
    leftPanel.add(console.getWindow().getContentPane());
    BreakawayPanel breakawayPanel = new BreakawayPanel(masterPane,
      "Intensity Data - " + file.getName(), false);
    breakawayPanel.setEdge(BorderLayout.WEST);
    breakawayPanel.setUpEnabled(false);
    breakawayPanel.setDownEnabled(false);
    breakawayPanel.setContentPane(leftPanel);

    JPanel options1 = new JPanel();
    options1.setLayout(new BoxLayout(options1, BoxLayout.X_AXIS));
    options1.add(makeRadioPanel("Scale", linear, log));
    options1.add(makeRadioPanel("Projection", perspective, parallel));
    options1.add(makeRadioPanel("Data", dataSurface, dataLines));
    options1.add(makeRadioPanel("Fit", fitSurface, fitLines));
    options1.add(makeRadioPanel("Residuals", resSurface, resLines));
//    options1.add(numCurves);
    JPanel options2 = new JPanel();
    options2.setLayout(new BoxLayout(options2, BoxLayout.X_AXIS));
    options2.add(showPanel);
    options2.add(Box.createHorizontalStrut(5));
    options2.add(exportData);
    options.add(options1);
    options.add(options2);
    decayPane.add(options, BorderLayout.SOUTH);
    masterPane.add(decayPane, BorderLayout.CENTER);

    progress.setProgress(999); // estimate: 99.9%
    if (progress.isCanceled()) System.exit(0);

    // show window on screen
    masterWindow.pack();
    Dimension size = masterWindow.getSize();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    masterWindow.setLocation((screen.width - size.width) / 2,
      (screen.height - size.height) / 2);
    masterWindow.setVisible(true);
    progress.setProgress(1000);
    progress.close();
    plotData(true, true);

    try { Thread.sleep(200); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
    cSlider.setValue(maxChan + 1);
  }

  // -- SlimPlotter methods --

  private Thread plotThread;
  private boolean plotCanceled;
  private boolean doRescale, doRefit;

  /** Plots the data in a separate thread. */
  public void plotData(final boolean rescale, final boolean refit) {
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
          sp.doRescale = rescale;
          sp.doRefit = refit;
          plotCanceled = false;
          plotThread = new Thread(sp, "Plotter");
          plotThread.start();
        }
      }
    }.start();
  }

  /** Handles cursor updates. */
  public void doCursor(double[] cursor, boolean rescale, boolean refit) {
    double[] domain = cursorToDomain(iPlot, cursor);
    roiX = (int) Math.round(domain[0]);
    roiY = (int) Math.round(domain[1]);
    if (roiX < 0) roiX = 0;
    if (roiX >= width) roiX = width - 1;
    if (roiY < 0) roiY = 0;
    if (roiY >= height) roiY = height - 1;
    roiCount = 1;
    plotData(rescale, refit);
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
    if (src == cToggle) {
      // toggle visibility of this channel
      int c = cSlider.getValue() - 1;
      cVisible[c] = !cVisible[c];
      plotData(true, false);
    }
    else if (src == dataSurface || src == dataLines) plotData(false, false);
    else if (src == fitSurface || src == fitLines ||
      src == resSurface || src == resLines)
    {
      plotData(false, true);
    }
    else if (src == linear || src == log) plotData(true, true);
    else if (src == perspective || src == parallel) {
      try {
        decayPlot.getGraphicsModeControl().setProjectionPolicy(
          parallel.isSelected() ? DisplayImplJ3D.PARALLEL_PROJECTION :
          DisplayImplJ3D.PERSPECTIVE_PROJECTION);
      }
      catch (Exception exc) { exc.printStackTrace(); }
    }
    else if (src == showData) decayRend.toggle(showData.isSelected());
    else if (src == showLine) lineRend.toggle(showLine.isSelected());
    else if (src == showBox) {
      try { decayPlot.getDisplayRenderer().setBoxOn(showBox.isSelected()); }
      catch (Exception exc) { exc.printStackTrace(); }
    }
    else if (src == showScale) {
      try {
        boolean scale = showScale.isSelected();
        decayPlot.getGraphicsModeControl().setScaleEnable(scale);
      }
      catch (Exception exc) { exc.printStackTrace(); }
    }
    else if (src == showFit) {
      fitRend.toggle(showFit.isSelected());
    }
    else if (src == showResiduals) {
      resRend.toggle(showResiduals.isSelected());
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
        out.println(timeBins + " x " + channels + " (count=" + roiCount +
          ", percent=" + roiPercent + ", maxVal=" + maxVal + ")");
        for (int c=0; c<channels; c++) {
          for (int t=0; t<timeBins; t++) {
            if (t > 0) out.print(" ");
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
          "SlimPlotter", JOptionPane.ERROR_MESSAGE);
      }
    }
    else { // OK button
      width = parse(wField.getText(), width);
      height = parse(hField.getText(), height);
      timeBins = parse(tField.getText(), timeBins);
      channels = parse(cField.getText(), channels);
      timeRange = parse(trField.getText(), timeRange);
      minWave = parse(wlField.getText(), minWave);
      waveStep = parse(sField.getText(), waveStep);
      adjustPeaks = peaksBox.isSelected();
      cVisible = new boolean[channels];
      Arrays.fill(cVisible, true);
      paramDialog.setVisible(false);
    }
  }

  // -- ChangeListener methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == cSlider) {
      int c = cSlider.getValue() - 1;
      try { ac.setCurrent(c); }
      catch (Exception exc) { exc.printStackTrace(); }
      cToggle.removeActionListener(this);
      cToggle.setSelected(cVisible[c]);
      cToggle.addActionListener(this);
    }
    else if (src == numCurves) plotData(false, true);
  }

  // -- DisplayListener methods --

  private boolean drag = false;

  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    if (id == DisplayEvent.MOUSE_PRESSED_CENTER) {
      drag = true;
      decayPlot.getDisplayRenderer();
      doCursor(iPlot.getDisplayRenderer().getCursor(), false, false);
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_CENTER) {
      drag = false;
      doCursor(iPlot.getDisplayRenderer().getCursor(), true, true);
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_LEFT) {
      // done drawing curve
      try {
        roiSet = DelaunayCustom.fillCheck(curveSet, false);
        if (roiSet == null) {
          roiRef.setData(new Real(0));
          doCursor(pixelToCursor(iPlot, e.getX(), e.getY()), true, true);
          iPlot.reAutoScale();
        }
        else {
          roiRef.setData(roiSet);
          int[] tri = roiSet.valueToTri(roiGrid);
          roiX = roiY = 0;
          roiCount = 0;
          for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
              int ndx = h * width + w;
              roiMask[h][w] = tri[ndx] >= 0;
              if (roiMask[h][w]) {
                roiX = w;
                roiY = h;
                roiCount++;
              }
            }
          }
          roiPercent = 100000 * roiCount / (width * height) / 1000.0;
          plotData(true, true);
        }
      }
      catch (VisADException exc) {
        String msg = exc.getMessage();
        if ("path self intersects".equals(msg)) {
          JOptionPane.showMessageDialog(iPlot.getComponent(),
            "Please draw a curve that does not intersect itself.",
            "SlimPlotter", JOptionPane.ERROR_MESSAGE);
        }
        else exc.printStackTrace();
      }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (id == DisplayEvent.MOUSE_DRAGGED) {
      if (!drag) return; // not a center mouse drag
      doCursor(iPlot.getDisplayRenderer().getCursor(), false, false);
    }
  }

  // -- Runnable methods --

  public void run() {
    ProgressMonitor progress = new ProgressMonitor(null, "Plotting data",
      "Calculating sums", 0,
      channels * timeBins + (doRefit ? channels : 0) + 1);
    progress.setMillisToPopup(100);
    progress.setMillisToDecideToPopup(50);
    int p = 0;
    if (roiCount == 1) {
      decayLabel.setText("Decay curve for " + "(" + roiX + ", " + roiY + ")");
    }
    else {
      decayLabel.setText("Decay curve for " + roiCount +
        " pixels (" + roiPercent + "%)");
    }
    boolean doDataLines = dataLines.isSelected();
    boolean doFitLines = fitLines.isSelected();
    boolean doResLines = resLines.isSelected();
    boolean doLog = log.isSelected();

    // calculate samples
    int numChanVis = 0;
    for (int c=0; c<channels; c++) {
      if (cVisible[c]) numChanVis++;
    }
    samps = new float[numChanVis * timeBins];
    maxVal = 0;
    for (int c=0, cc=0; c<channels; c++) {
      if (!cVisible[c]) continue;
      for (int t=0; t<timeBins; t++) {
        int ndx = timeBins * cc + t;
        int sum = 0;
        if (roiCount == 1) sum = values[c][roiY][roiX][t];
        else {
          for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
              if (roiMask[h][w]) sum += values[c][h][w][t];
            }
          }
        }
        samps[ndx] = sum;
        if (doLog) samps[ndx] = (float) Math.log(samps[ndx] + 1);
        if (samps[ndx] > maxVal) maxVal = samps[ndx];
        progress.setProgress(++p);
        if (progress.isCanceled()) plotCanceled = true;
        if (plotCanceled) break;
      }
      if (plotCanceled) break;
      cc++;
    }

    double[][] fitResults = null;
    int numExp = ((Integer) numCurves.getValue()).intValue();
    if (adjustPeaks && doRefit) {
      // perform exponential curve fitting: y(x) = a * e^(-b*t) + c
      progress.setNote("Fitting curves");
      fitResults = new double[channels][];
      ExpFunction func = new ExpFunction(numExp);
      float[] params = new float[3 * numExp];
      if (numExp == 1) {
        params[0] = maxVal;
        params[1] = 1;
        params[2] = 0;
      }
      else if (numExp == 2) {
        params[0] = maxVal / 2;
        params[1] = 0.8f;
        params[2] = 0;
        params[0] = maxVal / 2;
        params[1] = 2;
        params[2] = 0;
      }
//      for (int i=0; i<numExp; i++) {
//        // initial guess for (a, b, c)
//        int e = 3 * i;
//        params[e] = (numExp - i) * maxVal / (numExp + 1);
//        params[e + 1] = 1;
//        params[e + 2] = 0;
//      }
      int num = timeBins - maxPeak;
      float[] xVals = new float[num];
      for (int i=0; i<num; i++) xVals[i] = i;
      float[] yVals = new float[num];
      float[] weights = new float[num];
      Arrays.fill(weights, 1); // no weighting
      log("Computing fit parameters: y(t) = a * e^(-t/" + TAU + ") + c");
      for (int c=0, cc=0; c<channels; c++) {
        if (!cVisible[c]) {
          fitResults[c] = null;
          continue;
        }
        log("\tChannel #" + (c + 1) + ":");
        System.arraycopy(samps, timeBins * cc + maxPeak, yVals, 0, num);
        LMA lma = null;
        lma = new LMA(func, params, new float[][] {xVals, yVals},
          weights, new JAMAMatrix(params.length, params.length));
        lma.fit();
        log("\t\titerations=" + lma.iterationCount);
        log("\t\tchi2=" + lma.chi2);
        for (int i=0; i<numExp; i++) {
          int e = 3 * i;
          log("\t\ta" + i + "=" + lma.parameters[e]);
          log("\t\t" + TAU + i + "=" + (1 / lma.parameters[e + 1]));
          log("\t\tc" + i + "=" + lma.parameters[e + 2]);
        }
        fitResults[c] = lma.parameters;
        progress.setProgress(++p);
        cc++;
      }
    }

    try {
      // construct domain set for 3D surface plots
      float[][] bcGrid = new float[2][timeBins * numChanVis];
      for (int c=0, cc=0; c<channels; c++) {
        if (!cVisible[c]) continue;
        for (int t=0; t<timeBins; t++) {
          int ndx = timeBins * cc + t;
          bcGrid[0][ndx] = t * timeRange / (timeBins - 1);
          bcGrid[1][ndx] = c * (maxWave - minWave) / (channels - 1) + minWave;
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
          int ndx = timeBins * c + t;
          colors[ndx] = samps[ndx]; // CTR TODO: use Tau value for this channel
        }
        cc++;
      }

      // construct "Data" plot
      FlatField ff = new FlatField(bcvFunc, bcSet);
      ff.setSamples(new float[][] {samps, colors}, false);
      decayRef.setData(doDataLines ? makeLines(ff) : ff);

      if (fitResults != null) {
        // compute finite sampling matching fitted exponentials
        float[] fitSamps = new float[numChanVis * timeBins];
        float[] residuals = new float[numChanVis * timeBins];
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
                int e = 3 * i;
                sum += (float) (q[e] * Math.exp(-q[e + 1] * et) + q[e + 2]);
              }
              fitSamps[ndx] = sum;
              residuals[ndx] = samps[ndx] - fitSamps[ndx];
            }
          }
          cc++;
        }

        // construct "Fit" plot
        FlatField fit = new FlatField(bcvFuncFit, bcSet);
        fit.setSamples(new float[][] {fitSamps}, false);
        fitRef.setData(doFitLines ? makeLines(fit) : fit);

        // construct "Residuals" plot
        FlatField res = new FlatField(bcvFuncRes, bcSet);
        res.setSamples(new float[][] {residuals}, false);
        resRef.setData(doResLines ? makeLines(res) : res);
      }

      if (doRescale) {
        float max = maxVal == 0 ? 1 : maxVal;
        zMap.setRange(0, max);
        zMapFit.setRange(0, max);
        zMapRes.setRange(0, max);
        vMap.setRange(0, max);
        //vMapFit.setRange(0, max);
        //vMapRes.setRange(0, max);
        //decayPlot.reAutoScale();
      }
    }
    catch (Exception exc) { exc.printStackTrace(); }
    progress.setProgress(++p);
    progress.close();
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
        cGrid[0][cc++] = c * (maxWave - minWave) / (channels - 1) + minWave;
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

  // -- Utility methods --

  /** Converts the given pixel coordinates to cursor coordinates. */
  public static double[] pixelToCursor(DisplayImpl d, int x, int y) {
    if (d == null) return null;
    MouseBehavior mb = d.getDisplayRenderer().getMouseBehavior();
    VisADRay ray = mb.findRay(x, y);
    return ray.position;
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d, double[] cursor) {
    return cursorToDomain(d, null, cursor);
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d,
    RealType[] types, double[] cursor)
  {
    if (d == null) return null;

    // locate x, y and z mappings
    Vector maps = d.getMapVector();
    int numMaps = maps.size();
    ScalarMap mapX = null, mapY = null, mapZ = null;
    for (int i=0; i<numMaps; i++) {
      if (mapX != null && mapY != null && mapZ != null) break;
      ScalarMap map = (ScalarMap) maps.elementAt(i);
      if (types == null) {
        DisplayRealType drt = map.getDisplayScalar();
        if (drt.equals(Display.XAxis)) mapX = map;
        else if (drt.equals(Display.YAxis)) mapY = map;
        else if (drt.equals(Display.ZAxis)) mapZ = map;
      }
      else {
        ScalarType st = map.getScalar();
        if (st.equals(types[0])) mapX = map;
        if (st.equals(types[1])) mapY = map;
        if (st.equals(types[2])) mapZ = map;
      }
    }

    // adjust for scale
    double[] scaleOffset = new double[2];
    double[] dummy = new double[2];
    double[] values = new double[3];
    if (mapX == null) values[0] = Double.NaN;
    else {
      mapX.getScale(scaleOffset, dummy, dummy);
      values[0] = (cursor[0] - scaleOffset[1]) / scaleOffset[0];
    }
    if (mapY == null) values[1] = Double.NaN;
    else {
      mapY.getScale(scaleOffset, dummy, dummy);
      values[1] = (cursor[1] - scaleOffset[1]) / scaleOffset[0];
    }
    if (mapZ == null) values[2] = Double.NaN;
    else {
      mapZ.getScale(scaleOffset, dummy, dummy);
      values[2] = (cursor[2] - scaleOffset[1]) / scaleOffset[0];
    }

    return values;
  }

  public static JTextField addRow(JPanel p,
    String label, double value, String unit)
  {
    p.add(new JLabel(label));
    JTextField field = new JTextField(value == (int) value ?
      ("" + (int) value) : ("" + value), 8);
    JPanel fieldPane = new JPanel();
    fieldPane.setLayout(new BorderLayout());
    fieldPane.add(field, BorderLayout.CENTER);
    fieldPane.setBorder(new EmptyBorder(2, 3, 2, 3));
    p.add(fieldPane);
    p.add(new JLabel(unit));
    return field;
  }

  public static int parse(String s, int last) {
    try { return Integer.parseInt(s); }
    catch (NumberFormatException exc) { return last; }
  }

  public static float parse(String s, float last) {
    try { return Float.parseFloat(s); }
    catch (NumberFormatException exc) { return last; }
  }

  // -- Helper classes --

  /**
   * A summed exponential function of the form:
   * y(t) = a1*e^(-b1*t) + ... + an*e^(-bn*t) + c.
   */
  public class ExpFunction extends LMAFunction {
    /** Number of exponentials to fit. */
    private int numExp = 1;

    /** Constructs a function with the given number of summed exponentials. */
    public ExpFunction(int num) { numExp = num; }

    public double getY(double x, double[] a) {
      double sum = 0;
      for (int i=0; i<numExp; i++) {
        int e = 3 * i;
        sum += a[e] * Math.exp(-a[e + 1] * x) + a[e + 2];
      }
      return sum;
    }

    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
      int e = parameterIndex / 3;
      int off = parameterIndex % 3;
      switch (off) {
        case 0: return Math.exp(-a[e + 1] * x);
        case 1: return -a[e] * x * Math.exp(-a[e + 1] * x);
        case 2: return 1;
        default:
          throw new RuntimeException("No such parameter index: " +
            parameterIndex);
      }
    }
  }

  /**
   * HACK - OutputStream extension for filtering out hardcoded
   * RuntimeException.printStackTrace() exceptions within LMA library.
   */
  public class ConsoleStream extends PrintStream {
    private PrintStream ps;
    private boolean ignore;

    public ConsoleStream(OutputStream out) {
      super(out);
      ps = (PrintStream) out;
    }

    public void println(String s) {
      if (s.equals("java.lang.RuntimeException: Matrix is singular.")) {
        ignore = true;
      }
      else if (ignore && !s.startsWith("\tat ")) ignore = false;
      if (!ignore) super.println(s);
    }

    public void println(Object o) {
      String s = o.toString();
      println(s);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
