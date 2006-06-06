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
import visad.*;
import visad.bom.RubberBandBoxRendererJ3D;
import visad.java3d.*;

public class SlimPlotter
  implements ActionListener, ChangeListener, DisplayListener, WindowListener
{

  // -- Constants --

  private static final double[] MATRIX = {
     0.2821,  0.1503, -0.0201, 0.0418,
    -0.0500,  0.1323,  0.2871, 0.1198,
     0.1430, -0.2501,  0.1408, 0.0089,
     0.0000,  0.0000,  0.0000, 1.0000
  };


  // -- Fields --

  private int[][][][] values;
  private int width, height;
  private int channels, timeBins;
  private int minWave, waveStep;
  private double timeRange;
  private int minX, minY, maxX, maxY;
  private float maxVal;

  private JDialog paramDialog;
  private JTextField wField, hField, tField, cField, trField, wlField, sField;

  private JLabel decayLabel;
  private JCheckBox surface, parallel, logScale;
  private JSlider cSlider;

  private RealType cType;
  private FunctionType bcvFunc;
  private Linear2DSet bcSet;
  private ScalarMap zMap, vMap;
  private DataReferenceImpl ref;
  private DisplayImpl iPlot, decayPlot;
  private AnimationControl ac;


  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching SlimPlotter", "Initializing", 0, 23);
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

    minX = minY = 0;
    maxX = width;
    maxY = height;

    // read SDT file header
    DataInputStream fin = new DataInputStream(new BufferedInputStream(
      new FileInputStream(file)));
    fin.mark(4096);
    fin.skipBytes(14); // skip 14 byte header
    int offset = DataTools.read2UnsignedBytes(fin, true) + 22;
    System.out.println("Data offset: " + offset);//TEMP

    byte[] stuff = new byte[4077];
    fin.readFully(stuff);
    String s = new String(stuff);
    fin.reset();

    int leftToFind = 3;
    if (width < 0) {
      String wstr = "[SP_SCAN_X,I,";
      int wndx = s.indexOf(wstr);
      width = 128;
      if (wndx >= 0) {
        int q = s.indexOf("]", wndx);
        if (q >= 0) {
          width = parse(s.substring(wndx + wstr.length(), q), width);
          System.out.println("Got width from file: " + width);//TEMP
          leftToFind--;
        }
      }
    }
    if (height < 0) {
      height = 128;
      String hstr = "[SP_SCAN_Y,I,";
      int hndx = s.indexOf(hstr);
      if (hndx >= 0) {
        int q = s.indexOf("]", hndx);
        if (q >= 0) {
          height = parse(s.substring(hndx + hstr.length(), q), height);
          System.out.println("Got height from file: " + height);//TEMP
          leftToFind--;
        }
      }
    }
    if (timeBins < 0) {
      timeBins = 64;
      String tstr = "[SP_ADC_RE,I,";
      int tndx = s.indexOf(tstr);
      if (tndx >= 0) {
        int q = s.indexOf("]", tndx);
        if (q >= 0) {
          timeBins = parse(s.substring(tndx + tstr.length(), q), timeBins);
          System.out.println("Got timeBins from file: " + timeBins);//TEMP
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
    paramPane.setLayout(new GridLayout(9, 3));
    wField = addRow(paramPane, "Image width", width, "pixels");
    hField = addRow(paramPane, "Image height", height, "pixels");
    tField = addRow(paramPane, "Time bins", timeBins, "");
    cField = addRow(paramPane, "Channel count", channels, "");
    trField = addRow(paramPane, "Time range", timeRange, "nanoseconds");
    wlField = addRow(paramPane, "Starting wavelength", minWave, "nanometers");
    sField = addRow(paramPane, "Channel width", waveStep, "nanometers");
    JButton ok = new JButton("OK");
    ok.addActionListener(this);
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
    paramPane.add(new JLabel());
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
    progress.setMaximum(channels + 7);
    progress.setNote("Reading data");
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
    iPlot.enableEvent(DisplayEvent.MOUSE_MOVED);
    iPlot.addDisplayListener(this);

    iPlot.addMap(new ScalarMap(xType, Display.XAxis));
    iPlot.addMap(new ScalarMap(yType, Display.YAxis));
    iPlot.addMap(new ScalarMap(vType, Display.RGB));
    iPlot.addMap(new ScalarMap(cType, Display.Animation));
    DataReferenceImpl intensityRef = new DataReferenceImpl("intensity");
    iPlot.addReference(intensityRef);

    final DataReferenceImpl boxRef = new DataReferenceImpl("box");
    boxRef.setData(new Gridded2DSet(xy, null, 1)); // dummy
    iPlot.addReferences(new RubberBandBoxRendererJ3D(xType, yType, 0, 0),
      boxRef);
    CellImpl cell = new CellImpl() {
      public void doAction() throws VisADException, RemoteException {
        Set set = (Set) boxRef.getData();
        if (set == null) return;
        float[][] samples = set.getSamples(false);
        if (samples == null) return;
        minX = (int) Math.round(samples[0][0]);
        minY = (int) Math.round(samples[1][0]);
        maxX = (int) Math.round(samples[0][1]);
        maxY = (int) Math.round(samples[1][1]);
        plotData(true);
      }
    };
    cell.addReference(boxRef);

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
    ref = new DataReferenceImpl("decay");
    decayPlot.addReference(ref);
    xMap.getAxisScale().setTitle("Time (ns)");
    yMap.getAxisScale().setTitle("Wavelength (nm)");
    zMap.getAxisScale().setTitle("Count");
    decayPlot.getGraphicsModeControl().setScaleEnable(true);
    decayPlot.getGraphicsModeControl().setTextureEnable(false);
    decayPlot.getProjectionControl().setMatrix(MATRIX);
    decayPlot.getProjectionControl().setAspectCartesian(
      new double[] {2, 1, 1});
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // convert byte data to shorts
    progress.setNote("Constructing images ");
    values = new int[channels][height][width][timeBins];
    float[][][] pix = new float[channels][1][width * height];
    FieldImpl field = new FieldImpl(cxyvFunc, cSet);
    int max = 0;
    int maxChan = 0;
    for (int c=0; c<channels; c++) {
      progress.setProgress(++p);
      if (progress.isCanceled()) System.exit(0);
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
      }
      FlatField ff = new FlatField(xyvFunc, xySet);
      ff.setSamples(pix[c], false);
      field.setSample(c, ff);
    }
    progress.setProgress(++p);
    if (progress.isCanceled()) System.exit(0);

    // show 2D window on screen
    progress.setNote("Creating plots");
    JFrame intensityFrame = new JFrame("Intensity Data");
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
    JFrame decayFrame = new JFrame("Spectral Lifetime Data");
    decayFrame.addWindowListener(this);
    JPanel decayPane = new JPanel();
    decayPane.setLayout(new BorderLayout());
    decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

    decayLabel = new JLabel("Decay curve for all pixels");
    decayPane.add(decayLabel, BorderLayout.NORTH);

    JPanel options = new JPanel();
    options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
    surface = new JCheckBox("Surface", true);
    surface.addChangeListener(this);
    parallel = new JCheckBox("Parallel projection", false);
    parallel.addChangeListener(this);
    logScale = new JCheckBox("Log scale", false);
    logScale.addChangeListener(this);
    options.add(surface);
    options.add(parallel);
    options.add(logScale);
    decayPane.add(options, BorderLayout.SOUTH);

    decayFrame.setContentPane(decayPane);
    decayFrame.setBounds(ifx + ifw, ify, ifw, ifh);

    plotData(true);
    cSlider.setValue(maxChan + 1);

    decayFrame.setVisible(true);
    progress.setProgress(++p);
  }


  // -- SlimPlotter methods --

  public void plotData(boolean rescale) {
    if (minX < 0) minX = 0;
    if (minX >= width) minX = width - 1;
    if (maxX < 0) maxX = 0;
    if (maxX >= width) maxX = width - 1;
    if (minX > maxX) {
      int x = minX;
      minX = maxX;
      maxX = x;
    }
    if (minY < 0) minY = 0;
    if (minY >= height) minY = height - 1;
    if (maxY < 0) maxY = 0;
    if (maxY >= height) maxY = height - 1;
    if (minY > maxY) {
      int y = minY;
      minY = maxY;
      maxY = y;
    }
    boolean single = minX == maxX && minY == maxY;
    if (single) {
      decayLabel.setText("Decay curve for " + "(" + minX + ", " + minY + ")");
    }
    else {
      decayLabel.setText("Decay curve for " + "(" + minX +
        ", " + minY + ") - (" + maxX + ", " + maxY + ")");
    }
    boolean lines = !surface.isSelected();
    boolean log = logScale.isSelected();
    float[] samps = new float[channels * timeBins];
    maxVal = 0;
    for (int c=0; c<channels; c++) {
      for (int t=0; t<timeBins; t++) {
        int ndx = timeBins * c + t;
        int sum = 0;
        for (int h=minY; h<=maxY; h++) {
          for (int w=minX; w<=maxX; w++) {
            sum += values[c][h][w][t];
          }
        }
        samps[ndx] = sum;
        if (log) samps[ndx] = (float) Math.log(samps[ndx] + 1);
        if (samps[ndx] > maxVal) maxVal = samps[ndx];
      }
    }
    try {
      FlatField ff = new FlatField(bcvFunc, bcSet);
      ff.setSamples(new float[][] {samps}, false);
      if (rescale) {
        zMap.setRange(0, maxVal == 0 ? 1 : maxVal);
        vMap.setRange(0, maxVal == 0 ? 1 : maxVal);
      }
      ref.setData(lines ? ff.domainFactor(cType) : ff);
    }
    catch (Exception exc) { exc.printStackTrace(); }
  }

  public void doMouse() {
    double[] cursor = iPlot.getDisplayRenderer().getCursor();
    double[] domain = cursorToDomain(iPlot, cursor);
    minX = maxX = (int) Math.round(domain[0]);
    minY = maxY = (int) Math.round(domain[1]);
    plotData(false);
  }

  // -- ActionListener methods --

  public void actionPerformed(ActionEvent e) {
    width = parse(wField.getText(), width);
    height = parse(hField.getText(), height);
    timeBins = parse(tField.getText(), timeBins);
    channels = parse(cField.getText(), channels);
    timeRange = parse(trField.getText(), timeRange);
    minWave = parse(wlField.getText(), minWave);
    waveStep = parse(sField.getText(), waveStep);
    paramDialog.setVisible(false);
  }


  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
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
    else if (src == cSlider) {
      int c = cSlider.getValue();
      try { ac.setCurrent(c - 1); }
      catch (Exception exc) { exc.printStackTrace(); }
    }
  }


  // -- DisplayListener methods --

  private boolean drag = false;

  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    if (id == DisplayEvent.MOUSE_PRESSED_CENTER) {
      drag = true;
      decayPlot.getDisplayRenderer();
      doMouse();
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_CENTER) {
      drag = false;
      decayPlot.reAutoScale();
    }
    else if (id == DisplayEvent.MOUSE_DRAGGED) {
      if (!drag) return; // not a left mouse drag
      doMouse();
    }
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
