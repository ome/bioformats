//
// SlimPlotter.java
//

/*
Coded in 2006-@year@ by Curtis Rueden, with suggestions from
Long Yan, Steve Trier, Kraig Kumfer and Paolo Provenzano.
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
import java.util.*;
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
import visad.util.ColorMapWidget;

/** A tool for visualization of spectral lifetime data. */
public class SlimPlotter implements ActionListener, ChangeListener,
  DisplayListener, DocumentListener, Runnable, WindowListener
{

  // -- Constants --

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
  private boolean adjustPeaks, cutEnd;
  private boolean[] cVisible;
  private int maxPeak;
  private int[] maxIntensity;

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

  // fit parameters
  private float[][] tau;

  // GUI components for parameter dialog box
  private JDialog paramDialog;
  private JTextField wField, hField, tField, cField, trField, wlField, sField;
  private JCheckBox peaksBox, cutBox;

  // GUI components for intensity pane
  private JSlider cSlider;
  private JLabel minLabel, maxLabel;
  private JSlider minSlider, maxSlider;
  private JCheckBox cToggle;

  // GUI components for decay pane
  private JLabel decayLabel;
  private JRadioButton linear, log;
  private JRadioButton perspective, parallel;
  private JRadioButton dataSurface, dataLines;
  private JRadioButton fitSurface, fitLines;
  private JRadioButton resSurface, resLines;
  private JRadioButton colorHeight, colorTau;
  private JSpinner numCurves;
  private JCheckBox showData, showScale;
  private JCheckBox showBox, showLine;
  private JCheckBox showFit, showResiduals;
  private JCheckBox zOverride;
  private JTextField zScaleValue;
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
  private ScalarMap intensityMap;
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
      paramDialog = new JDialog((Frame) null, "Slim Plotter", true);
      JPanel paramPane = new JPanel();
      paramPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      paramDialog.setContentPane(paramPane);
      paramPane.setLayout(new GridLayout(11, 3));
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
      peaksBox.setToolTipText("<html>Computes the peak of each spectral " +
        "channel, and aligns those peaks <br>to match by adjusting the " +
        "lifetime histograms. This option corrects<br>for skew across " +
        "channels caused by the multispectral detector's<br>variable system " +
        "response time between channels. This option must<br>be enabled to " +
        "perform exponential curve fitting.</html>");
      paramPane.add(peaksBox);
      paramPane.add(new JLabel());
      paramPane.add(new JLabel());
      // row 8
      cutBox = new JCheckBox("Cut 1.5ns from fit", true);
      cutBox.setToolTipText("<html>When performing exponential curve " +
        "fitting, excludes the last 1.5 ns<br>from the computation. This " +
        "option is useful because the end of the <br>the lifetime histogram " +
        "sometimes drops off unexpectedly, skewing<br>the fit results.</html>");
      paramPane.add(cutBox);
      paramPane.add(new JLabel());
      paramPane.add(new JLabel());
      // row 10
      paramPane.add(new JLabel());
      paramPane.add(new JLabel());
      paramPane.add(new JLabel());
      // row 11
      paramPane.add(new JLabel());
      paramPane.add(ok);
      paramDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension ps = paramDialog.getSize();
      paramDialog.setLocation((screenSize.width - ps.width) / 2,
        (screenSize.height - ps.height) / 2);
      paramDialog.setVisible(true);
      if (cVisible == null) System.exit(0); // dialog canceled (closed with X)
      maxWave = minWave + (channels - 1) * waveStep;
      roiCount = width * height;
      roiPercent = 100;

      // pop up progress monitor
      setProgress(progress, 1); // estimate: 0.1%
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
        setProgress(progress, (int) (700L *
          (off + blockSize) / data.length)); // estimate: 0% -> 70%
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
      RealType vType2 = RealType.getRealType("tau");
      RealTupleType vv = new RealTupleType(vType, vType2);
      bcvFunc = new FunctionType(bc, vv);
      RealType vTypeFit = RealType.getRealType("value_fit");
      bcvFuncFit = new FunctionType(bc, vTypeFit);
      RealType vTypeRes = RealType.getRealType("value_res");
      bcvFuncRes = new FunctionType(bc, vTypeRes);
      setProgress(progress, 710); // estimate: 71%
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
      setProgress(progress, 720); // estimate: 72%
      if (progress.isCanceled()) System.exit(0);

      iPlot.addMap(new ScalarMap(xType, Display.XAxis));
      iPlot.addMap(new ScalarMap(yType, Display.YAxis));
      intensityMap = new ScalarMap(vType, Display.RGB);
      iPlot.addMap(intensityMap);
      iPlot.addMap(new ScalarMap(cType, Display.Animation));
      DataReferenceImpl intensityRef = new DataReferenceImpl("intensity");
      iPlot.addReference(intensityRef);
      setProgress(progress, 730); // estimate: 73%
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
      setProgress(progress, 740); // estimate: 74%
      if (progress.isCanceled()) System.exit(0);

      ac = (AnimationControl) iPlot.getControl(AnimationControl.class);
      iPlot.getProjectionControl().setMatrix(
        iPlot.make_matrix(0, 0, 0, 0.85, 0, 0, 0));

      setProgress(progress, 750); // estimate: 75%
      if (progress.isCanceled()) System.exit(0);

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
      setProgress(progress, 770); // estimate: 77%
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
      pc.setMatrix(channels > 1 ? MATRIX_3D : MATRIX_2D);
      pc.setAspectCartesian(
        new double[] {2, 1, 1});
      setProgress(progress, 780); // estimate: 78%
      if (progress.isCanceled()) System.exit(0);

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
              if (val > maxIntensity[c]) maxIntensity[c] = val;
              values[c][h][w][t] = val;
              sum += val;
            }
            pix[c][0][width * h + w] = sum;
          }
          setProgress(progress, 780 + 140 *
            (height * c + h + 1) / (channels * height)); // estimate: 78% -> 92%
          if (progress.isCanceled()) System.exit(0);
        }
        FlatField ff = new FlatField(xyvFunc, xySet);
        ff.setSamples(pix[c], false);
        field.setSample(c, ff);
      }

      // compute channel with brightest intensity
      maxChan = 0;
      int max = 0;
      for (int c=0; c<channels; c++) {
        if (maxIntensity[c] > max) {
          max = maxIntensity[c];
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
          if (progress.isCanceled()) System.exit(0);
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
          //new ConstantMap(2, Display.LineWidth)
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
      intensityPane.setLayout(new BoxLayout(intensityPane, BoxLayout.Y_AXIS));
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
      intensityPane.add(iPlotPane);

      setProgress(progress, 970); // estimate: 97%
      if (progress.isCanceled()) System.exit(0);

      JPanel sliderPane = new JPanel();
      sliderPane.setLayout(new BoxLayout(sliderPane, BoxLayout.X_AXIS));
      intensityPane.add(sliderPane);
      cSlider = new JSlider(1, channels, 1);
      cSlider.setToolTipText(
        "Selects the channel to display in the 2D intensity plot above");
      cSlider.setSnapToTicks(true);
      cSlider.setMajorTickSpacing(channels / 4);
      cSlider.setMinorTickSpacing(1);
      cSlider.setPaintTicks(true);
      cSlider.addChangeListener(this);
      cSlider.setBorder(new EmptyBorder(8, 5, 8, 5));
      cSlider.setEnabled(channels > 1);
      sliderPane.add(cSlider);
      cToggle = new JCheckBox("", true);
      cToggle.setToolTipText(
        "Toggles the selected channel's visibility in the 3D data plot");
      cToggle.addActionListener(this);
      cToggle.setEnabled(channels > 1);
      sliderPane.add(cToggle);

      JPanel minMaxPane = new JPanel();
      minMaxPane.setLayout(new BoxLayout(minMaxPane, BoxLayout.X_AXIS));
      intensityPane.add(minMaxPane);

      JPanel minPane = new JPanel();
      minPane.setLayout(new BoxLayout(minPane, BoxLayout.Y_AXIS));
      minMaxPane.add(minPane);
      minLabel = new JLabel("min=0");
      minLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
      minPane.add(minLabel);
      minSlider = new JSlider(0, max, 0);
      minSlider.setToolTipText("<html>" +
        "Adjusts intensity plot's minimum color value.<br>" +
        "Anything less than this value appears black.</html>");
      minSlider.setMajorTickSpacing(max);
      int minor = max / 16;
      if (minor < 1) minor = 1;
      minSlider.setMinorTickSpacing(minor);
      minSlider.setPaintTicks(true);
      minSlider.addChangeListener(this);
      minSlider.setBorder(new EmptyBorder(0, 5, 8, 5));
      minPane.add(minSlider);

      JPanel maxPane = new JPanel();
      maxPane.setLayout(new BoxLayout(maxPane, BoxLayout.Y_AXIS));
      minMaxPane.add(maxPane);
      maxLabel = new JLabel("max=" + max);
      maxLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
      maxPane.add(maxLabel);
      maxSlider = new JSlider(0, max, max);
      maxSlider.setToolTipText("<html>" +
        "Adjusts intensity plot's maximum color value.<br>" +
        "Anything greater than this value appears white.</html>");
      maxSlider.setMajorTickSpacing(max);
      maxSlider.setMinorTickSpacing(minor);
      maxSlider.setPaintTicks(true);
      maxSlider.addChangeListener(this);
      maxSlider.setBorder(new EmptyBorder(0, 5, 8, 5));
      maxPane.add(maxSlider);

      intensityRef.setData(field);
      ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
      cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

      setProgress(progress, 980); // estimate: 98%
      if (progress.isCanceled()) System.exit(0);

      // construct 3D pane
      JPanel decayPane = new JPanel();
      decayPane.setLayout(new BorderLayout());
      decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

      decayLabel = new JLabel("Decay curve for all pixels");
      decayLabel.setToolTipText(
        "Displays information about the selected region of interest");
      decayPane.add(decayLabel, BorderLayout.NORTH);

      ColorMapWidget colorWidget = new ColorMapWidget(vMap);
      Dimension prefSize = colorWidget.getPreferredSize();
      colorWidget.setPreferredSize(new Dimension(prefSize.width, 0));

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

      numCurves = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
      numCurves.setToolTipText("Number of components in exponential fit");
      numCurves.setMaximumSize(numCurves.getPreferredSize());
      numCurves.addChangeListener(this);

      setProgress(progress, 990); // estimate: 99%
      if (progress.isCanceled()) System.exit(0);

      JPanel showPanel = new JPanel();
      showPanel.setBorder(new TitledBorder("Show"));
      showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.Y_AXIS));
      showPanel.add(showData);
      showPanel.add(showScale);
      showPanel.add(showBox);
      showPanel.add(showLine);
      showPanel.add(showFit);
      showPanel.add(showResiduals);

      JPanel scalePanel = new JPanel();
      scalePanel.setBorder(new TitledBorder("Z Scale Override"));
      scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.X_AXIS));
      scalePanel.add(zOverride);
      scalePanel.add(zScaleValue);

      JPanel colorPanel = new JPanel();
      colorPanel.setBorder(new TitledBorder("Color Mapping"));
      colorPanel.setLayout(new BorderLayout());
      colorPanel.add(colorWidget);

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
      miscRow3.add(numCurves);
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
      rightPanel.add(intensityPane);
      rightPanel.add(console.getWindow().getContentPane());
      BreakawayPanel breakawayPanel = new BreakawayPanel(masterPane,
        "Intensity Data - " + file.getName(), false);
      breakawayPanel.setEdge(BorderLayout.EAST);
      breakawayPanel.setUpEnabled(false);
      breakawayPanel.setDownEnabled(false);
      breakawayPanel.setContentPane(rightPanel);

      setProgress(progress, 999); // estimate: 99.9%
      if (progress.isCanceled()) System.exit(0);

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

    setProgress(progress, 1000);
    progress.close();

    plotData(true, true, true);

    try { Thread.sleep(200); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
    cSlider.setValue(maxChan + 1);
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
    plotData(true, rescale, refit);
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
      plotData(true, true, false);
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
          "Slim Plotter", JOptionPane.ERROR_MESSAGE);
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
      cutEnd = cutBox.isSelected();
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
    else if (src == minSlider) {
      int min = minSlider.getValue();
      int max = maxSlider.getMaximum();
      maxSlider.setMajorTickSpacing(max - min);
      int minor = (max - min) / 16;
      if (minor < 1) minor = 1;
      maxSlider.setMinorTickSpacing(minor);
      maxSlider.setMinimum(min);
      minLabel.setText("min=" + min);
      rescaleMinMax();
    }
    else if (src == maxSlider) {
      int max = maxSlider.getValue();
      minSlider.setMajorTickSpacing(max);
      int minor = max / 16;
      if (minor < 1) minor = 1;
      minSlider.setMinorTickSpacing(minor);
      minSlider.setMaximum(max);
      maxLabel.setText("max=" + max);
      rescaleMinMax();
    }
    else if (src == numCurves) plotData(true, false, true);
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
          plotData(true, true, true);
        }
      }
      catch (VisADException exc) {
        String msg = exc.getMessage();
        if ("path self intersects".equals(msg)) {
          JOptionPane.showMessageDialog(iPlot.getComponent(),
            "Please draw a curve that does not intersect itself.",
            "Slim Plotter", JOptionPane.ERROR_MESSAGE);
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

  // -- DocumentListener methods --

  public void changedUpdate(DocumentEvent e) { updateZAxis(); }
  public void insertUpdate(DocumentEvent e) { updateZAxis(); }
  public void removeUpdate(DocumentEvent e) { updateZAxis(); }

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
        for (int i=1; i<=maxVal; i*=BASE) {
          hash.put(new Double(Math.log(i) / BASE_LOG), "" + i);
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
          if (roiCount == 1) sum = values[c][roiY][roiX][t];
          else {
            for (int h=0; h<height; h++) {
              for (int w=0; w<width; w++) {
                if (roiMask[h][w]) sum += values[c][h][w][t];
              }
            }
          }
          samps[ndx] = sum;
          if (samps[ndx] > maxVal) maxVal = samps[ndx];
          if (samps[ndx] > maxVals[cc]) maxVals[cc] = samps[ndx];
          setProgress(progress, ++p);
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
        tau = new float[channels][numExp];
        for (int c=0; c<channels; c++) Arrays.fill(tau[c], Float.NaN);
        ExpFunction func = new ExpFunction(numExp);
        float[] params = new float[2 * numExp + 1];
        if (numExp == 1) {
          //params[0] = maxVal;
          params[1] = picoToBins(1000);
          params[2] = 0;
        }
        else if (numExp == 2) {
          //params[0] = maxVal / 2;
          params[1] = picoToBins(800);
          //params[2] = maxVal / 2;
          params[3] = picoToBins(2000);
          params[4] = 0;
        }
        int num = timeBins - maxPeak;

        // HACK - cut off last 1500 ps from lifetime histogram,
        // to improve accuracy of fit.
        if (cutEnd) {
          int cutBins = (int) picoToBins(1500);
          if (num > cutBins + 5) num -= cutBins;
        }

        float[] xVals = new float[num];
        for (int i=0; i<num; i++) xVals[i] = i;
        float[] yVals = new float[num];
        float[] weights = new float[num];
        Arrays.fill(weights, 1); // no weighting
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
        for (int c=0, cc=0; c<channels; c++) {
          if (!cVisible[c]) {
            fitResults[c] = null;
            continue;
          }
          log("\tChannel #" + (c + 1) + ":");
          System.arraycopy(samps, timeBins * cc + maxPeak, yVals, 0, num);
          LMA lma = null;
          for (int i=0; i<numExp; i++) {
            int e = 2 * i;
            params[e] = maxVals[cc] / numExp;
          }
          lma = new LMA(func, params, new float[][] {xVals, yVals},
            weights, new JAMAMatrix(params.length, params.length));
          lma.fit();
          log("\t\titerations=" + lma.iterationCount);
          // normalize chi2 by the channel's peak value
          if (maxVals[cc] != 0) lma.chi2 /= maxVals[cc];
          // scale chi2 by degrees of freedom
          lma.chi2 /= num - params.length;
          log("\t\tchi2=" + lma.chi2);
          for (int i=0; i<numExp; i++) {
            int e = 2 * i;
            log("\t\ta" + (i + 1) + "=" +
              (100 * lma.parameters[e] / maxVals[cc]) + "%");
            tau[c][i] = binsToPico((float) (1 / lma.parameters[e + 1]));
            log("\t\t" + TAU + (i + 1) + "=" + tau[c][i] + " ps");
          }
          log("\t\tc=" + lma.parameters[2 * numExp]);
          fitResults[c] = lma.parameters;
          setProgress(progress, ++p);
          cc++;
        }
      }

      float tauMin = Float.NaN, tauMax = Float.NaN;
      if (tau != null) {
        tauMin = tauMax = tau[0][0];
        for (int i=1; i<tau.length; i++) {
          if (tau[i][0] < tauMin) tauMin = tau[i][0];
          if (tau[i][0] > tauMax) tauMax = tau[i][0];
        }
      }
      StringBuffer sb = new StringBuffer();
      sb.append("Decay curve for ");
      if (roiCount == 1) {
        sb.append("(");
        sb.append(roiX);
        sb.append(", ");
        sb.append(roiY);
        sb.append(")");
      }
      else {
        sb.append(roiCount);
        sb.append(" pixels (");
        sb.append(roiPercent);
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
      catch (Exception exc) { exc.printStackTrace(); }
      setProgress(progress, ++p);
      progress.close();

      // update scale labels for linear scale
      if (!doLog) {
        AxisScale zScale = zMap.getAxisScale();
        zScale.createStandardLabels(maxVal, 0, 0, maxVal);
      }
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
      float maxZ = d == d ? d : maxVal;
      if (doLog) maxZ = linearToLog(maxZ);
      if (maxZ != maxZ || maxZ == 0) maxZ = 1;
      try {
        zMap.setRange(0, maxZ);
        zMapFit.setRange(0, maxZ);
        zMapRes.setRange(0, maxZ);
        //vMap.setRange(0, max);
        //vMapFit.setRange(0, max);
        //vMapRes.setRange(0, max);
        decayPlot.reAutoScale();
      }
      catch (Exception exc) { exc.printStackTrace(); }
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

  /** Converts value in picoseconds to histogram bins. */
  private float picoToBins(float pico) {
    return timeBins * pico / timeRange / 1000;
  }

  /** Converts value in histogram bins to picoseconds. */
  private float binsToPico(float bins) {
    return 1000 * timeRange * bins / timeBins;
  }

  /** Converts linear value to logarithm value. */
  private float linearToLog(float v) {
    return (float) v >= 1 ? (float) Math.log(v) / BASE_LOG : Float.NaN;
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

  private void rescaleMinMax() {
    int min = minSlider.getValue();
    int max = maxSlider.getValue();
    try { intensityMap.setRange(min, max); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
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

  /** Updates progress monitor status; mainly for debugging. */
  private static void setProgress(ProgressMonitor progress, int p) {
    progress.setProgress(p);
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
        int e = 2 * i;
        sum += a[e] * Math.exp(-a[e + 1] * x);
      }
      sum += a[2 * numExp];
      return sum;
    }

    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
      if (parameterIndex == 2 * numExp) return 1; // c
      int e = parameterIndex / 2;
      int off = parameterIndex % 2;
      switch (off) {
        case 0:
          return Math.exp(-a[e + 1] * x); // a
        case 1:
          return -a[e] * x * Math.exp(-a[e + 1] * x); // b
      }
      throw new RuntimeException("No such parameter index: " +
        parameterIndex);
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
