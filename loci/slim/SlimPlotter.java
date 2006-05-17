// SlimPlotter.java

package loci.slim;

import ij.*;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.process.ShortProcessor;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.DataTools;
import visad.*;
import visad.java3d.*;

public class SlimPlotter implements ChangeListener,
  MouseListener, MouseMotionListener, WindowListener
{

  // -- Fields --

  private int[][][][] values;
  private float[][] sums;
  private int width, height;
  private int channels, timeBins;
  private int curX, curY;

  private ImageCanvas ic;

  private JLabel label;
  private JCheckBox surface, allpix, parallel;

  private RealType xt, yt, zt;
  private FunctionType ftype;
  private Integer2DSet fset;
  private DataReferenceImpl ref;
  private DisplayImpl display;


  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    if (args == null || args.length < 1) {
      System.out.println("Please specify an SDT file.");
      System.exit(1);
    }

    // parse command line arguments
    String filename = args[0];
    File file = new File(filename);
    width = height = 128;
    if (args.length >= 3) {
      width = Integer.parseInt(args[1]);
      height = Integer.parseInt(args[2]);
    }
    timeBins = 64;
    if (args.length >= 4) timeBins = Integer.parseInt(args[3]);
    channels = -1; // autodetect later
    if (args.length >= 5) channels = Integer.parseInt(args[4]);

    // read SDT file
    System.out.print("Reading data");
    DataInputStream fin = new DataInputStream(new FileInputStream(file));
    fin.skipBytes(14); // skip 14 byte header
    int offset = DataTools.read2UnsignedBytes(fin, true) + 22;
    if (channels < 0) {
      // autodetect number of channels based on file size
      channels = (int) ((file.length() - offset) /
        (2 * timeBins * width * height));
    }
    fin.skipBytes(offset - 16); // skip to data
    byte[] data = new byte[2 * channels * height * width * timeBins];
    fin.readFully(data);
    fin.close();
    System.out.println(" OK");

    // plot decay curves in 3D display
    System.out.print("Building display");
    display = new DisplayImplJ3D("decay");
    xt = RealType.getRealType("bin");
    yt = RealType.getRealType("channel");
    zt = RealType.getRealType("value");
    RealTupleType xy = new RealTupleType(xt, yt);
    ftype = new FunctionType(xy, zt);
    fset = new Integer2DSet(xy, timeBins, channels);
    display.addMap(new ScalarMap(xt, Display.XAxis));
    display.addMap(new ScalarMap(yt, Display.YAxis));
    display.addMap(new ScalarMap(zt, Display.ZAxis));
    display.addMap(new ScalarMap(zt, Display.RGB));
    ref = new DataReferenceImpl("ref");
    display.addReference(ref);
    display.getGraphicsModeControl().setScaleEnable(true);
    display.getGraphicsModeControl().setTextureEnable(false);
    System.out.println(" OK");

    // convert byte data to shorts
    System.out.print("Constructing images ");
    values = new int[channels][height][width][timeBins];
    short[][] pix = new short[channels][width * height];
    sums = new float[1][channels * timeBins];
    ImageStack stack = new ImageStack(width, height);
    for (int c=0; c<channels; c++) {
      System.out.print(".");
      int oc = timeBins * width * height * c;
      for (int h=0; h<height; h++) {
        int oh = timeBins * width * h;
        for (int w=0; w<width; w++) {
          int ow = timeBins * w;
          int sum = 0;
          for (int t=0; t<timeBins; t++) {
            int ndx = 2 * (oc + oh + ow + t);
            int q = DataTools.bytesToInt(data, ndx, 2, true);
            values[c][h][w][t] = q;
            sums[0][timeBins * c + t] += q;
            sum += q;
          }
          pix[c][width * h + w] = (short) sum;
        }
      }
      stack.addSlice(file + ":" + (c + 1),
        new ShortProcessor(width, height, pix[c], null));
    }
    System.out.println(" OK");

    // plot intensity data
    System.out.print("Creating interface");
    ImagePlus intensity = new ImagePlus("Intensity Data", stack);
    intensity.show();
    ImageWindow iw = intensity.getWindow();
    iw.addWindowListener(this);
    ic = iw.getCanvas();
    ic.addMouseListener(this);
    ic.addMouseMotionListener(this);
    ic.zoomIn(0, 0);
    ic.zoomIn(0, 0);

    // show 3D window on screen
    JFrame frame = new JFrame("Spectral Lifetime Data");
    frame.addWindowListener(this);
    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(display.getComponent(), BorderLayout.CENTER);

    label = new JLabel("Decay curve for all pixels");
    pane.add(label, BorderLayout.NORTH);

    JPanel options = new JPanel();
    options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
    surface = new JCheckBox("Surface", true);
    surface.addChangeListener(this);
    allpix = new JCheckBox("Sum pixels", true);
    allpix.addChangeListener(this);
    parallel = new JCheckBox("Parallel projection", false);
    parallel.addChangeListener(this);
    options.add(surface);
    options.add(allpix);
    options.add(parallel);
    pane.add(options, BorderLayout.SOUTH);

    frame.setContentPane(pane);
    Point iwLoc = iw.getLocation();
    Dimension iwSize = iw.getSize();
    frame.setBounds(iwLoc.x + iwSize.width, iwLoc.y,
      iwSize.width, iwSize.height);
    frame.show();
    System.out.println(" OK");

    // plot 3D data
    plotData();
  }


  // -- SlimPlotter methods --

  public void plotData() {
    if (curX < 0) curX = 0;
    if (curX >= width) curX = width - 1;
    if (curY < 0) curY = 0;
    if (curY >= height) curY = height - 1;
    boolean all = allpix.isSelected();
    label.setText("Decay curve for " +
      (all ? "all pixels" : ("(" + curX + ", " + curY + ")")));
    boolean lines = !surface.isSelected();
    float[][] samples = null;
    if (all) samples = sums;
    else {
      float[] samps = new float[channels * timeBins];
      for (int c=0; c<channels; c++) {
        if (all) System.out.print(".");
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
        }
      }
      samples = new float[][] {samps};
    }
    try {
      FlatField ff = new FlatField(ftype, fset);
      ff.setSamples(samples);
      ref.setData(lines ? ff.domainFactor(yt) : ff);
    }
    catch (Exception exc) { exc.printStackTrace(); }
  }

  public void doMouse(MouseEvent e) {
    curX = ic.offScreenX(e.getX());
    curY = ic.offScreenY(e.getY());
    boolean ap = allpix.isSelected();
    allpix.setSelected(false);
    if (!ap) plotData();
  }


  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == surface) plotData();
    else if (src == allpix) {
      plotData();
      display.reAutoScale();
    }
    else if (src == parallel) {
      try {
        display.getGraphicsModeControl().setProjectionPolicy(
          parallel.isSelected() ? DisplayImplJ3D.PARALLEL_PROJECTION :
          DisplayImplJ3D.PERSPECTIVE_PROJECTION);
      }
      catch (Exception exc) { exc.printStackTrace(); }
    }
  }


  // -- MouseListener methods --

  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mousePressed(MouseEvent e) { doMouse(e); }
  public void mouseReleased(MouseEvent e) { display.reAutoScale(); }


  // -- MouseMotionListener methods --

  public void mouseMoved(MouseEvent e) { }
  public void mouseDragged(MouseEvent e) { doMouse(e); }


  // -- WindowListener methods --

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { System.exit(0); }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
