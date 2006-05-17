// SlimPlotter.java

package loci.slim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.DataTools;
import loci.formats.ExtensionFileFilter;
import visad.*;
import visad.java3d.*;

public class SlimPlotter
  implements ChangeListener, DisplayListener, WindowListener
{

  // -- Constants --

  private static final double[] MATRIX = {
     0.3950,  0.2104, -0.0282, 0.1125,
    -0.0700,  0.1853,  0.4020, 0.1641,
     0.2002, -0.3502,  0.1971, 0.0124,
     0.0000,  0.0000,  0.0000, 1.0000
  };


  // -- Fields --

  private int[][][][] values;
  private float[][] sums, logs;
  private int width, height;
  private int channels, timeBins;
  private int curX, curY;

  private JLabel decayLabel;
  private JCheckBox surface, allpix, parallel, logScale;
  private JSlider cSlider;

  private RealType cType;
  private FunctionType bcvFunc;
  private Integer2DSet bcSet;
  private DataReferenceImpl ref;
  private DisplayImpl iPlot, decayPlot;
  private AnimationControl ac;


  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
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
      // parse command line arguments
      filename = args[0];
      file = new File(filename);
    }
    width = height = 128;
    if (args.length >= 3) {
      width = Integer.parseInt(args[1]);
      height = Integer.parseInt(args[2]);
    }
    timeBins = 64;
    if (args.length >= 4) timeBins = Integer.parseInt(args[3]);
    channels = -1; // autodetect later
    if (args.length >= 5) channels = Integer.parseInt(args[4]);

    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching SlimPlotter", "Reading data", 0, 23);
    progress.setMillisToPopup(0);
    progress.setMillisToDecideToPopup(0);
    int p = 0;

    // read SDT file
    DataInputStream fin = new DataInputStream(new FileInputStream(file));
    fin.skipBytes(14); // skip 14 byte header
    int offset = DataTools.read2UnsignedBytes(fin, true) + 22;
    if (channels < 0) {
      // autodetect number of channels based on file size
      channels = (int) ((file.length() - offset) /
        (2 * timeBins * width * height));
    }
    progress.setMaximum(channels + 7);
    fin.skipBytes(offset - 16); // skip to data
    byte[] data = new byte[2 * channels * height * width * timeBins];
    fin.readFully(data);
    fin.close();
    progress.setProgress(++p);

    // create types
    progress.setNote("Creating types");
    RealType xType = RealType.getRealType("element");
    RealType yType = RealType.getRealType("line");
    RealType bType = RealType.getRealType("bin");
    cType = RealType.getRealType("channel");
    RealType vType = RealType.getRealType("value");
    RealTupleType xy = new RealTupleType(xType, yType);
    FunctionType xyvFunc = new FunctionType(xy, vType);
    Integer2DSet xySet = new Integer2DSet(xy, width, height);
    FunctionType cxyvFunc = new FunctionType(cType, xyvFunc);
    Integer1DSet cSet = new Integer1DSet(cType, channels);
    RealTupleType bc = new RealTupleType(bType, cType);
    bcvFunc = new FunctionType(bc, vType);
    bcSet = new Integer2DSet(bc, timeBins, channels);
    progress.setProgress(++p);

    // plot intensity data in 2D display
    progress.setNote("Building displays");
    iPlot = new DisplayImplJ3D("intensity", new TwoDDisplayRendererJ3D());
    iPlot.getMouseBehavior().getMouseHelper().setFunctionMap(new int[][][] {
      {{MouseHelper.CURSOR_TRANSLATE, MouseHelper.CURSOR_ZOOM}, // L, shift-L
       {MouseHelper.CURSOR_ROTATE, MouseHelper.NONE}}, // ctrl-L, ctrl-shift-L
      {{MouseHelper.NONE, MouseHelper.NONE}, // M, shift-M
       {MouseHelper.NONE, MouseHelper.NONE}}, // ctrl-M, ctrl-shift-M
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
    ac = (AnimationControl) iPlot.getControl(AnimationControl.class);
    iPlot.getProjectionControl().setMatrix(
      iPlot.make_matrix(0, 0, 0, 0.85, 0, 0, 0));
    progress.setProgress(++p);

    // plot decay curves in 3D display
    decayPlot = new DisplayImplJ3D("decay");
    decayPlot.addMap(new ScalarMap(bType, Display.XAxis));
    decayPlot.addMap(new ScalarMap(cType, Display.YAxis));
    decayPlot.addMap(new ScalarMap(vType, Display.ZAxis));
    decayPlot.addMap(new ScalarMap(vType, Display.RGB));
    ref = new DataReferenceImpl("decay");
    decayPlot.addReference(ref);
    decayPlot.getGraphicsModeControl().setScaleEnable(true);
    decayPlot.getGraphicsModeControl().setTextureEnable(false);
    decayPlot.getProjectionControl().setMatrix(MATRIX);
    progress.setProgress(++p);

    // convert byte data to shorts
    progress.setNote("Constructing images ");
    values = new int[channels][height][width][timeBins];
    float[][][] pix = new float[channels][1][width * height];
    sums = new float[1][channels * timeBins];
    logs = new float[1][channels * timeBins];
    FieldImpl field = new FieldImpl(cxyvFunc, cSet);
    for (int c=0; c<channels; c++) {
      progress.setProgress(++p);
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
            sums[0][timeBins * c + t] += val;
            sum += val;
          }
          pix[c][0][width * h + w] = sum;
        }
      }
      FlatField ff = new FlatField(xyvFunc, xySet);
      ff.setSamples(pix[c], false);
      field.setSample(c, ff);
    }
    for (int i=0; i<sums[0].length; i++) {
      logs[0][i] = (float) Math.log(sums[0][i] + 1);
    }
    progress.setProgress(++p);

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

    Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
    int pad = 30;
    int ifx = pad / 2, ify = pad / 2;
    int ifw = (ss.width - pad) / 2, ifh = ifw;
    intensityFrame.setContentPane(intensityPane);
    intensityFrame.setBounds(ifx, ify, ifw, ifh);

    intensityRef.setData(field);
    ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
    cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

    intensityFrame.show();
    progress.setProgress(++p);

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
    allpix = new JCheckBox("Sum pixels", true);
    allpix.addChangeListener(this);
    parallel = new JCheckBox("Parallel projection", false);
    parallel.addChangeListener(this);
    logScale = new JCheckBox("Log scale", false);
    logScale.addChangeListener(this);
    options.add(surface);
    options.add(allpix);
    options.add(parallel);
    options.add(logScale);
    decayPane.add(options, BorderLayout.SOUTH);

    decayFrame.setContentPane(decayPane);
    decayFrame.setBounds(ifx + ifw, ify, ifw, ifh);

    plotData();

    decayFrame.show();
    progress.setProgress(++p);
  }


  // -- SlimPlotter methods --

  public void plotData() {
    if (curX < 0) curX = 0;
    if (curX >= width) curX = width - 1;
    if (curY < 0) curY = 0;
    if (curY >= height) curY = height - 1;
    boolean all = allpix.isSelected();
    decayLabel.setText("Decay curve for " +
      (all ? "all pixels" : ("(" + curX + ", " + curY + ")")));
    boolean lines = !surface.isSelected();
    boolean log = logScale.isSelected();
    float[][] samples = null;
    if (all) samples = log ? logs : sums;
    else {
      float[] samps = new float[channels * timeBins];
      for (int c=0; c<channels; c++) {
        for (int t=0; t<timeBins; t++) {
          int ndx = timeBins * c + t;
          if (all) {
            // sum all pixels
            int sum = 0;
            for (int h=0; h<height; h++) {
              for (int w=0; w<width; w++) {
                sum += values[c][h][w][t];
              }
            }
            samps[ndx] = sum;
          }
          else {
            // pixel at (X, Y)
            samps[ndx] = values[c][curY][curX][t];
          }
          if (log) samps[ndx] = (float) Math.log(samps[ndx] + 1);
        }
      }
      samples = new float[][] {samps};
    }
    try {
      FlatField ff = new FlatField(bcvFunc, bcSet);
      ff.setSamples(samples, false);
      ref.setData(lines ? ff.domainFactor(cType) : ff);
    }
    catch (Exception exc) { exc.printStackTrace(); }
  }

  public void doMouse() {
    double[] cursor = iPlot.getDisplayRenderer().getCursor();
    double[] domain = cursorToDomain(iPlot, cursor);
    curX = (int) domain[0];
    curY = (int) domain[1];
    boolean ap = allpix.isSelected();
    allpix.setSelected(false);
    if (!ap) plotData();
  }


  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == surface) plotData();
    else if (src == allpix || src == logScale) {
      plotData();
      decayPlot.reAutoScale();
    }
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
    if (id == DisplayEvent.MOUSE_PRESSED_LEFT) {
      drag = true;
      decayPlot.getDisplayRenderer();
      doMouse();
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_LEFT) {
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


  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
