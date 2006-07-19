//
// SlimPlotter.java
//

package loci.slim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import loci.formats.DataTools;
import loci.formats.ExtensionFileFilter;
import loci.visbio.util.OutputConsole;
import visad.*;
import visad.bom.CurveManipulationRendererJ3D;
import visad.java3d.*;

public class SlimPlotter implements ActionListener,
  ChangeListener, DisplayListener, Runnable, WindowListener
{

  // -- Constants --

  /** Default orientation for decay curves display. */
  private static final double[] MATRIX = {
     0.2821,  0.1503, -0.0201, 0.0418,
    -0.0500,  0.1323,  0.2871, 0.1198,
     0.1430, -0.2501,  0.1408, 0.0089,
     0.0000,  0.0000,  0.0000, 1.0000
  };

  // -- Fields --

  /** Actual data values, dimensioned [channel][row][column][bin]. */
  private int[][][][] values;

  // data parameters
  private int width, height;
  private int channels, timeBins;
  private double timeRange;
  private int minWave, waveStep;
  private boolean adjustPeaks;

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

  // GUI components for decay pane
  private JLabel decayLabel;
  private JCheckBox surface, parallel, logScale;
  private JButton showLog;
  private JSlider cSlider;

  // other GUI components
  private OutputConsole console;

  // VisAD objects
  private RealType cType;
  private FunctionType bcvFunc;
  private Linear2DSet bcSet;
  private ScalarMap zMap, vMap;
  private DataReferenceImpl decayRef;
  private DisplayImpl iPlot, decayPlot;
  private AnimationControl ac;

  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    console = new OutputConsole("Log");
    System.setErr(new PrintStream(console));

    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching SlimPlotter", "Initializing", 0, 16 + 7);
    progress.setMillisToPopup(0);
    progress.setMillisToDecideToPopup(0);
    int p = 0;

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
    width = height = timeBins = channels = -1;
    if (args.length >= 3) {
      width = parse(args[1], -1);
      height = parse(args[2], -1);
    }
    if (args.length >= 4) timeBins = parse(args[3], -1);
    if (args.length >= 5) channels = parse(args[4], -1);

    // read SDT file header
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    short revision = DataTools.read2SignedBytes(raf, true);
    int infoOffset = DataTools.read4SignedBytes(raf, true);
    short infoLength = DataTools.read2SignedBytes(raf, true);
    int setupOffs = DataTools.read4SignedBytes(raf, true);
    short setupLength = DataTools.read2SignedBytes(raf, true);
    int dataBlockOffset = DataTools.read4SignedBytes(raf, true);
    short noOfDataBlocks = DataTools.read2SignedBytes(raf, true);
    int dataBlockLength = DataTools.read4SignedBytes(raf, true);
    int measDescBlockOffset = DataTools.read4SignedBytes(raf, true);
    short noOfMeasDescBlocks = DataTools.read2SignedBytes(raf, true);
    short measDescBlockLength = DataTools.read2SignedBytes(raf, true);
    int headerValid = DataTools.read2UnsignedBytes(raf, true);
    long reserved1 = DataTools.read4UnsignedBytes(raf, true);
    int reserved2 = DataTools.read2UnsignedBytes(raf, true);
    int chksum = DataTools.read2UnsignedBytes(raf, true);

    int offset = dataBlockOffset + 22;
    log("File length: " + file.length());
    log("Data offset: " + offset);
    log("Setup offset: " + setupOffs);

    // read SDT setup block
    raf.seek(setupOffs);
    byte[] setupData = new byte[4095];
    raf.readFully(setupData);
    String setup = new String(setupData);
    raf.close();

    int leftToFind = 3;
    if (width < 0) {
      String wstr = "[SP_SCAN_X,I,";
      int wndx = setup.indexOf(wstr);
      width = 128;
      if (wndx >= 0) {
        int q = setup.indexOf("]", wndx);
        if (q >= 0) {
          width = parse(setup.substring(wndx + wstr.length(), q), width);
          log("Got width from file: " + width);
          leftToFind--;
        }
      }
    }
    if (height < 0) {
      height = 128;
      String hstr = "[SP_SCAN_Y,I,";
      int hndx = setup.indexOf(hstr);
      if (hndx >= 0) {
        int q = setup.indexOf("]", hndx);
        if (q >= 0) {
          height = parse(setup.substring(hndx + hstr.length(), q), height);
          log("Got height from file: " + height);
          leftToFind--;
        }
      }
    }
    if (timeBins < 0) {
      timeBins = 64;
      String tstr = "[SP_ADC_RE,I,";
      int tndx = setup.indexOf(tstr);
      if (tndx >= 0) {
        int q = setup.indexOf("]", tndx);
        if (q >= 0) {
          timeBins = parse(setup.substring(tndx + tstr.length(), q), timeBins);
          log("Got timeBins from file: " + timeBins);
          leftToFind--;
        }
      }
    }
    if (channels < 0) {
      // autodetect number of channels based on file size
      channels = (int) ((file.length() - offset) /
        (2 * timeBins * width * height));
    }
    timeRange = 12.5;
    minWave = 400;
    waveStep = 10;

    // warn user if some numbers were not found
    if (leftToFind > 0) {
      JOptionPane.showMessageDialog(null,
        "Some numbers (image width, image height, and number of time bins)\n" +
        "were not found within the file. The numbers you are about to see\n" +
        "are best guesses. Please change them if they are incorrect.",
        "SlimPlotter", JOptionPane.WARNING_MESSAGE);
    }

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
    Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension ps = paramDialog.getSize();
    paramDialog.setLocation((ss.width - ps.width) / 2,
      (ss.height - ps.height) / 2);
    paramDialog.setVisible(true);
    int maxWave = minWave + (channels - 1) * waveStep;

    // read pixel data
    progress.setMaximum(adjustPeaks ?
      (channels * height + 2 * channels + 9) : (channels * height + 7));
    progress.setNote("Reading data");
    DataInputStream fin = new DataInputStream(new FileInputStream(file));
    fin.skipBytes(offset); // skip to data
    byte[] data = new byte[2 * channels * height * width * timeBins];
    fin.readFully(data);
    fin.close();
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // create types
    progress.setNote("Creating types");
    RealType xType = RealType.getRealType("element");
    RealType yType = RealType.getRealType("line");
    ScaledUnit ns = new ScaledUnit(1e-9, SI.second, "ns");
    ScaledUnit nm = new ScaledUnit(1e-9, SI.meter, "nm");
    RealType bType = RealType.getRealType("bin", ns);
    cType = RealType.getRealType("channel", nm);
    RealType vType = RealType.getRealType("value");
    RealTupleType xy = new RealTupleType(xType, yType);
    FunctionType xyvFunc = new FunctionType(xy, vType);
    Integer2DSet xySet = new Integer2DSet(xy, width, height);
    FunctionType cxyvFunc = new FunctionType(cType, xyvFunc);
    Linear1DSet cSet = new Linear1DSet(cType,
      minWave, maxWave, channels, null, new Unit[] {nm}, null);
    RealTupleType bc = new RealTupleType(bType, cType);
    bcvFunc = new FunctionType(bc, vType);
    bcSet = new Linear2DSet(bc, 0, timeRange, timeBins,
      minWave, maxWave, channels, null, new Unit[] {ns, nm}, null);
    progress.setProgress(++p);
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

    iPlot.addMap(new ScalarMap(xType, Display.XAxis));
    iPlot.addMap(new ScalarMap(yType, Display.YAxis));
    iPlot.addMap(new ScalarMap(vType, Display.RGB));
    iPlot.addMap(new ScalarMap(cType, Display.Animation));
    DataReferenceImpl intensityRef = new DataReferenceImpl("intensity");
    iPlot.addReference(intensityRef);

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

    ac = (AnimationControl) iPlot.getControl(AnimationControl.class);
    iPlot.getProjectionControl().setMatrix(
      iPlot.make_matrix(0, 0, 0, 0.85, 0, 0, 0));
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // plot decay curves in 3D display
    decayPlot = new DisplayImplJ3D("decay");
    ScalarMap xMap = new ScalarMap(bType, Display.XAxis);
    ScalarMap yMap = new ScalarMap(cType, Display.YAxis);
    zMap = new ScalarMap(vType, Display.ZAxis);
    decayPlot.addMap(xMap);
    decayPlot.addMap(yMap);
    decayPlot.addMap(zMap);
    vMap = new ScalarMap(vType, Display.RGB);
    decayPlot.addMap(vMap);
    decayRef = new DataReferenceImpl("decay");
    decayPlot.addReference(decayRef);
    xMap.setRange(0, timeRange);
    yMap.setRange(minWave, maxWave);
    xMap.getAxisScale().setTitle("Time (ns)");
    yMap.getAxisScale().setTitle("Wavelength (nm)");
    zMap.getAxisScale().setTitle("Count");
    GraphicsModeControl gmc = decayPlot.getGraphicsModeControl();
    gmc.setScaleEnable(true);
    gmc.setTextureEnable(false);
    ProjectionControl pc = decayPlot.getProjectionControl();
    pc.setMatrix(MATRIX);
    pc.setAspectCartesian(
      new double[] {2, 1, 1});
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // convert byte data to unsigned shorts
    progress.setNote("Constructing images ");
    values = new int[channels][height][width][timeBins];
    float[][][] pix = new float[channels][1][width * height];
    FieldImpl field = new FieldImpl(cxyvFunc, cSet);
    int max = 0;
    int maxChan = 0;
    for (int c=0; c<channels; c++) {
      int oc = timeBins * width * height * c;
      for (int h=0; h<height; h++) {
        progress.setProgress(++p);
        if (progress.isCanceled()) System.exit(0);
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
      }
      FlatField ff = new FlatField(xyvFunc, xySet);
      ff.setSamples(pix[c], false);
      field.setSample(c, ff);
    }
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // adjust peaks
    if (adjustPeaks) {
      progress.setNote("Adjusting peaks ");
      int[] peaks = new int[channels];
      for (int c=0; c<channels; c++) {
        progress.setProgress(++p);
        if (progress.isCanceled()) System.exit(0);
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
      }
      progress.setProgress(++p);
      if (progress.isCanceled()) System.exit(0);
      int maxPeak = 0;
      for (int c=1; c<channels; c++) {
        if (maxPeak < peaks[c]) maxPeak = peaks[c];
      }
      log("Aligning peaks to tmax = " + maxPeak);
      for (int c=0; c<channels; c++) {
        progress.setProgress(++p);
        if (progress.isCanceled()) System.exit(0);
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
          log("\tShifted channel #" + c + " by " +
            shift + " [tmax = " + peaks[c] + "]");
        }
      }
      progress.setProgress(++p);
      if (progress.isCanceled()) System.exit(0);

      // add yellow line to indicate adjusted peak position
      DataReferenceImpl peakRef = new DataReferenceImpl("peaks");
      float peakTime = (float) (maxPeak * timeRange / timeBins);
      peakRef.setData(new Gridded2DSet(bc,
        new float[][] {{peakTime, peakTime}, {minWave, maxWave}}, 2));
      decayPlot.addReference(peakRef, new ConstantMap[] {
        new ConstantMap(-1, Display.ZAxis),
        new ConstantMap(0, Display.Blue),
//        new ConstantMap(2, Display.LineWidth)
      });
    }

    // show 2D window on screen
    progress.setNote("Creating plots");
    JFrame intensityFrame = new JFrame("Intensity Data - " + file.getName());
    intensityFrame.addWindowListener(this);
    JPanel intensityPane = new JPanel();
    intensityPane.setLayout(new BorderLayout());
    intensityPane.add(iPlot.getComponent(), BorderLayout.CENTER);

    cSlider = new JSlider(1, channels, 1);
    cSlider.setSnapToTicks(true);
    cSlider.setMajorTickSpacing(channels / 4);
    cSlider.setMinorTickSpacing(1);
    cSlider.setPaintTicks(true);
    cSlider.addChangeListener(this);
    cSlider.setBorder(new EmptyBorder(8, 5, 8, 5));
    intensityPane.add(cSlider, BorderLayout.SOUTH);

    int pad = 30;
    int ifx = pad / 2, ify = pad / 2;
    int ifw = (ss.width - pad) / 2, ifh = ifw;
    intensityFrame.setContentPane(intensityPane);
    intensityFrame.setBounds(ifx, ify, ifw, ifh);

    intensityRef.setData(field);
    ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
    cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

    intensityFrame.setVisible(true);
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // show 3D window on screen
    JFrame decayFrame = new JFrame("Spectral Lifetime Data - " +
      file.getName());
    decayFrame.addWindowListener(this);
    JPanel decayPane = new JPanel();
    decayPane.setLayout(new BorderLayout());
    decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

    decayLabel = new JLabel("Decay curve for all pixels");
    decayPane.add(decayLabel, BorderLayout.NORTH);

    JPanel options = new JPanel();
    options.setBorder(new EmptyBorder(8, 5, 8, 5));
    options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
    surface = new JCheckBox("Surface", true);
    surface.addActionListener(this);
    parallel = new JCheckBox("Parallel projection", false);
    parallel.addActionListener(this);
    logScale = new JCheckBox("Log scale", false);
    logScale.addActionListener(this);
    showLog = new JButton("Show log");
    showLog.addActionListener(this);
    options.add(surface);
    options.add(parallel);
    options.add(logScale);
    options.add(showLog);
    decayPane.add(options, BorderLayout.SOUTH);

    decayFrame.setContentPane(decayPane);
    decayFrame.setBounds(ifx + ifw, ify, ifw, ifh);

    cSlider.setValue(maxChan + 1);

    decayFrame.setVisible(true);
    progress.setProgress(++p);
    progress.close();
    plotData(true);
  }

  // -- SlimPlotter methods --

  private Thread plotThread;
  private boolean plotCanceled;
  private boolean rescale;

  /** Plots the data in a separate thread. */
  public void plotData(boolean rescale) {
    final boolean doRescale = rescale;
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
          sp.rescale = doRescale;
          plotCanceled = false;
          plotThread = new Thread(sp, "Plotter");
          plotThread.start();
        }
      }
    }.start();
  }

  /** Handles cursor updates. */
  public void doCursor(double[] cursor, boolean rescale) {
    double[] domain = cursorToDomain(iPlot, cursor);
    roiX = (int) Math.round(domain[0]);
    roiY = (int) Math.round(domain[1]);
    if (roiX < 0) roiX = 0;
    if (roiX >= width) roiX = width - 1;
    if (roiY < 0) roiY = 0;
    if (roiY >= height) roiY = height - 1;
    roiCount = 1;
    plotData(rescale);
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
    if (src == surface) plotData(false);
    else if (src == logScale) plotData(true);
    else if (src == parallel) {
      try {
        decayPlot.getGraphicsModeControl().setProjectionPolicy(
          parallel.isSelected() ? DisplayImplJ3D.PARALLEL_PROJECTION :
          DisplayImplJ3D.PERSPECTIVE_PROJECTION);
      }
      catch (Exception exc) { exc.printStackTrace(); }
    }
    else if (src == showLog) console.show();
    else { // OK button
      width = parse(wField.getText(), width);
      height = parse(hField.getText(), height);
      timeBins = parse(tField.getText(), timeBins);
      channels = parse(cField.getText(), channels);
      timeRange = parse(trField.getText(), timeRange);
      minWave = parse(wlField.getText(), minWave);
      waveStep = parse(sField.getText(), waveStep);
      adjustPeaks = peaksBox.isSelected();
      paramDialog.setVisible(false);
    }
  }

  // -- ChangeListener methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) {
    int c = cSlider.getValue();
    try { ac.setCurrent(c - 1); }
    catch (Exception exc) { exc.printStackTrace(); }
  }

  // -- DisplayListener methods --

  private boolean drag = false;

  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    if (id == DisplayEvent.MOUSE_PRESSED_CENTER) {
      drag = true;
      decayPlot.getDisplayRenderer();
      doCursor(iPlot.getDisplayRenderer().getCursor(), false);
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_CENTER) {
      drag = false;
      doCursor(iPlot.getDisplayRenderer().getCursor(), true);
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_LEFT) {
      // done drawing curve
      try {
        roiSet = DelaunayCustom.fillCheck(curveSet, false);
        if (roiSet == null) {
          roiRef.setData(new Real(0));
          doCursor(pixelToCursor(iPlot, e.getX(), e.getY()), true);
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
          plotData(true);
        }
      }
      catch (VisADException exc) {
        String msg = exc.getMessage();
        if ("path self intersects".equals(msg)) {
          JOptionPane.showMessageDialog(null,
            "Please draw a curve that does not intersect itself.",
            "SlimPlotter", JOptionPane.ERROR_MESSAGE);
        }
        else exc.printStackTrace();
      }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (id == DisplayEvent.MOUSE_DRAGGED) {
      if (!drag) return; // not a center mouse drag
      doCursor(iPlot.getDisplayRenderer().getCursor(), false);
    }
  }

  // -- Runnable methods --

  public void run() {
    ProgressMonitor progress = new ProgressMonitor(null,
      "Plotting data", null, 0, channels * timeBins + 1);
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
    boolean lines = !surface.isSelected();
    boolean log = logScale.isSelected();
    float[] samps = new float[channels * timeBins];
    maxVal = 0;
    for (int c=0; c<channels; c++) {
      for (int t=0; t<timeBins; t++) {
        int ndx = timeBins * c + t;
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
        if (log) samps[ndx] = (float) Math.log(samps[ndx] + 1);
        if (samps[ndx] > maxVal) maxVal = samps[ndx];
        progress.setProgress(++p);
        if (progress.isCanceled()) plotCanceled = true;
        if (plotCanceled) break;
      }
      if (plotCanceled) break;
    }
    try {
      FlatField ff = new FlatField(bcvFunc, bcSet);
      ff.setSamples(new float[][] {samps}, false);
      if (rescale) {
        zMap.setRange(0, maxVal == 0 ? 1 : maxVal);
        vMap.setRange(0, maxVal == 0 ? 1 : maxVal);
      }
      decayRef.setData(lines ? ff.domainFactor(cType) : ff);
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

  public static double parse(String s, double last) {
    try { return Double.parseDouble(s); }
    catch (NumberFormatException exc) { return last; }
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
