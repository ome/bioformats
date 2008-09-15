//
// TwoDPane.java
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
import java.rmi.RemoteException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import loci.slim.fit.BurnInRenderer;
import loci.slim.fit.Renderer;
import visad.*;
import visad.bom.CurveManipulationRendererJ3D;
import visad.java3d.DisplayImplJ3D;
import visad.java3d.TwoDDisplayRendererJ3D;

/**
 * Slim Plotter's 2D image pane.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/TwoDPane.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/TwoDPane.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class TwoDPane extends JPanel
  implements ActionListener, ChangeListener, DisplayListener, DocumentListener
{

  // -- Constants --

  private static final int BIN_RADIUS = 3; // TODO - make this a UI option
  private static final Color INVALID_COLOR = Color.red.brighter();

  /** Progress bar is updated every RATE milliseconds. */
  private static final int RATE = 100; // TODO - make this a UI option

  /** Lifetime image is redrawn every SKIPth frame. */
  private static final int SKIP = 2;

  // -- Fields --

  private SlimPlotter slim;
  private DisplayImpl iPlot;
  private ScalarMap imageMap;
  private DataReferenceImpl imageRef;
  private AnimationControl ac;

  // data parameters
  private SlimData data;
  private SlimTypes types;

  // intensity parameters
  private FieldImpl intensityField;
  private int intensityMin, intensityMax;

  // lifetime parameters
  private FieldImpl lifetimeField;
  private double lifetimeMin, lifetimeMax;

  // ROI parameters
  private float[][] roiGrid;
  private UnionSet curveSet;
  private Irregular2DSet roiSet;
  private DataReferenceImpl roiRef;

  private int roiCount;
  private double roiPercent;
  private int roiX, roiY;
  private boolean[][] roiMask;

  // GUI components for image pane
  private JProgressBar progress;
  private JButton startStopButton;
  private JSlider cSlider;
  private JRadioButton intensityMode, lifetimeMode;
  private JRadioButton projectionMode, emissionMode;
  private JTextField minField, maxField;
  private Color validColor = null;
  private JCheckBox cToggle;

  // parameters for multithreaded lifetime computation
  private Renderer[] curveRenderers;
  private Thread[] curveThreads;
  private Timer lifetimeRefresh;
  private boolean lifetimeActive;
  private int frame; // internal frame counter

  // -- Constructor --

  public TwoDPane(SlimPlotter slim, SlimData data, SlimTypes types)
    throws VisADException, RemoteException
  {
    this.slim = slim;
    this.data = data;
    this.types = types;

    roiCount = data.width * data.height;
    roiPercent = 100;

    // create 2D display
    iPlot = new DisplayImplJ3D("image", new TwoDDisplayRendererJ3D());
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

    iPlot.addMap(new ScalarMap(types.xType, Display.XAxis));
    iPlot.addMap(new ScalarMap(types.yType, Display.YAxis));
    imageMap = new ScalarMap(types.vType, Display.RGB);
    iPlot.addMap(imageMap);
    iPlot.addMap(new ScalarMap(types.cType, Display.Animation));
    imageRef = new DataReferenceImpl("image");
    iPlot.addReference(imageRef);

    // set up curve manipulation renderer
    roiGrid = new float[2][data.width * data.height];
    roiMask = new boolean[data.height][data.width];
    for (int y=0; y<data.height; y++) {
      for (int x=0; x<data.width; x++) {
        int ndx = y * data.width + x;
        roiGrid[0][ndx] = x;
        roiGrid[1][ndx] = y;
        roiMask[y][x] = true;
      }
    }
    final DataReferenceImpl curveRef = new DataReferenceImpl("curve");
    RealTupleType xy = new RealTupleType(types.xType, types.yType);
    UnionSet dummyCurve = new UnionSet(new Gridded2DSet[] {
      new Gridded2DSet(types.xy, new float[][] {{0}, {0}}, 1)
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

    // lay out components
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
    add(iPlotPane);

    progress = new JProgressBar();
    progress.setStringPainted(true);

    startStopButton = new JButton("Start");
    startStopButton.addActionListener(this);

    JPanel progressPanel = new JPanel();
    progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
    progressPanel.add(progress);
    progressPanel.add(startStopButton);
    add(progressPanel);

    JPanel viewModePane = new JPanel();
    viewModePane.setLayout(new BoxLayout(viewModePane, BoxLayout.X_AXIS));
    add(viewModePane);

    viewModePane.add(new JLabel("View mode:"));
    intensityMode = new JRadioButton("Intensity", true);
    lifetimeMode = new JRadioButton("Lifetime");
    emissionMode = new JRadioButton("Emission");
    emissionMode.setToolTipText("<html>" +
      "Displays an emission spectrum of the data.</html>");
    emissionMode.setEnabled(false);
    projectionMode = new JRadioButton("Projection");
    projectionMode.setToolTipText("<html>" +
      "Displays a \"spectral projection\" of the data.</html>");
    projectionMode.setEnabled(false);
    ButtonGroup group = new ButtonGroup();
    group.add(intensityMode);
    group.add(lifetimeMode);
    group.add(projectionMode);
    group.add(emissionMode);
    intensityMode.addActionListener(this);
    lifetimeMode.addActionListener(this);
    projectionMode.addActionListener(this);
    emissionMode.addActionListener(this);
    viewModePane.add(Box.createHorizontalStrut(5));
    viewModePane.add(intensityMode);
    viewModePane.add(lifetimeMode);
    viewModePane.add(projectionMode);
    viewModePane.add(emissionMode);

    JPanel sliderPane = new JPanel();
    sliderPane.setLayout(new BoxLayout(sliderPane, BoxLayout.X_AXIS));
    add(sliderPane);

    JPanel minMaxPane = new JPanel();
    minMaxPane.setLayout(new BoxLayout(minMaxPane, BoxLayout.Y_AXIS));
    minMaxPane.setBorder(new EmptyBorder(3, 3, 3, 3));
    sliderPane.add(minMaxPane);

    JPanel minPane = new JPanel();
    minPane.setLayout(new BoxLayout(minPane, BoxLayout.X_AXIS));
    minMaxPane.add(minPane);
    JLabel minLabel = new JLabel("Min");
    minLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
    minPane.add(minLabel);
    minField = new JTextField(4);
    minField.setMaximumSize(minField.getPreferredSize());
    minField.setToolTipText("<html>" +
      "Adjusts image plot's minimum color value.<br>" +
      "Anything less than this value appears black.</html>");
    minField.getDocument().addDocumentListener(this);
    minPane.add(minField);

    JPanel maxPane = new JPanel();
    maxPane.setLayout(new BoxLayout(maxPane, BoxLayout.X_AXIS));
    minMaxPane.add(maxPane);
    JLabel maxLabel = new JLabel("Max");
    maxLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
    minLabel.setPreferredSize(maxLabel.getPreferredSize());
    maxPane.add(maxLabel);
    maxField = new JTextField(4);
    maxField.setMaximumSize(maxField.getPreferredSize());
    maxField.setToolTipText("<html>" +
      "Adjusts image plot's maximum color value.<br>" +
      "Anything greater than this value appears white.</html>");
    maxField.getDocument().addDocumentListener(this);
    maxPane.add(maxField);

    validColor = minField.getBackground();

    cSlider = new JSlider(1, data.channels, 1);
    cSlider.setToolTipText(
      "Selects the channel to display in the 2D image plot above");
    cSlider.setSnapToTicks(true);
    cSlider.setMajorTickSpacing(data.channels / 4);
    cSlider.setMinorTickSpacing(1);
    cSlider.setPaintTicks(true);
    cSlider.addChangeListener(this);
    cSlider.setBorder(new EmptyBorder(8, 5, 8, 5));
    cSlider.setEnabled(data.channels > 1);
    sliderPane.add(cSlider);
    cToggle = new JCheckBox("", true);
    cToggle.setToolTipText(
      "Toggles the selected channel's visibility in the 3D data plot");
    cToggle.addActionListener(this);
    cToggle.setEnabled(data.channels > 1);
    sliderPane.add(cToggle);

    int maxChan = doIntensity();

    cSlider.setValue(maxChan + 1);

    // set up lifetime curve fitting renderers for per-pixel lifetime analysis
    curveRenderers = new Renderer[data.channels];
    curveThreads = new Thread[data.channels];
    for (int c=0; c<data.channels; c++) {
      curveRenderers[c] = new BurnInRenderer(data.curves[c]);
      curveRenderers[c].setComponentCount(data.numExp);
      curveRenderers[c].setMaxIterations(10);//TEMP
    }
    int delay = RATE;
    lifetimeRefresh = new Timer(delay, this);
    lifetimeRefresh.start();
  }

  // -- TwoDPane methods --

  public int getROICount() { return roiCount; }
  public double getROIPercent() { return roiPercent; }
  public int getROIX() { return roiX; }
  public int getROIY() { return roiY; }
  public boolean[][] getROIMask() { return roiMask; }

  // -- ActionListener methods --

  /** Handles checkbox presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == startStopButton) {
      lifetimeActive = !lifetimeActive;
      if (lifetimeActive) {
        // begin lifetime computation
        startStopButton.setText("Stop");
        for (int c=0; c<data.channels; c++) {
          curveThreads[c] = new Thread(curveRenderers[c], "Lifetime-" + c);
          curveThreads[c].setPriority(Thread.MIN_PRIORITY);
        }
        for (int c=0; c<data.channels; c++) curveThreads[c].start();
      }
      else {
        // terminate lifetime computation
        startStopButton.setText("Start");
        for (int c=0; c<data.channels; c++) curveRenderers[c].stop();
      }
    }
    else if (src == cToggle) {
      // toggle visibility of this channel
      int c = cSlider.getValue() - 1;
      data.cVisible[c] = !data.cVisible[c];
      slim.plotData(true, true, false);
    }
    else if (src == intensityMode) doIntensity();
    else if (src == lifetimeMode) doLifetime();
    else if (src == projectionMode) doSpectralProjection();
    else if (src == emissionMode) doEmissionSpectrum();
    else {
      // timer event - update progress bar and lifetime display
      if (!lifetimeActive) return;
      frame++;
      int c = cSlider.getValue() - 1;

      int curProg = curveRenderers[c].getCurrentProgress();
      int maxProg = curveRenderers[c].getMaxProgress();
      progress.setValue(curProg <= maxProg ? curProg : 0);
      progress.setMaximum(maxProg);
      if (curProg == maxProg) {
        int totalIter = curveRenderers[c].getTotalIterations();
        double wRCSE = curveRenderers[c].getWorstRCSE();
        wRCSE = ((int) (wRCSE * 100)) / 100.0;
        progress.setString("Improving image: iter. #" + totalIter +
             " Worst RCSE: " + wRCSE);
      }
      else {
        int subLevel = curveRenderers[c].getSubsampleLevel();
        if (subLevel < 0) {
          double percent = (10000 * curProg / maxProg) / 100.0;
          progress.setString("Iterating: " + percent + "%");
        }
        else {
          progress.setString("Estimating; " + (subLevel + 1) +
            " step" + (subLevel > 0 ? "s" : "") + " until total burn-in");
        }
      }

      if (frame % SKIP == 0 && lifetimeMode.isSelected()) {
        // update VisAD display
        double[][] lifetimeImage = curveRenderers[c].getImage();
        if (lifetimeImage.length > 1) {
          // TEMP - only visualize first component of multi-component fit
          lifetimeImage = new double[][] {lifetimeImage[0]};
        }
        try {
          FlatField ff = (FlatField) lifetimeField.getSample(c);
          ff.setSamples(lifetimeImage);
          //imageRef.setData(lifetimeField);
          // CTR START HERE - this works, but need to make this change
          // unilateral and then fix slider behavior to match
          imageRef.setData(ff);
        }
        catch (VisADException exc) { exc.printStackTrace(); }
        catch (RemoteException exc) { exc.printStackTrace(); }

        // recompute min and max
        double lifeMin = 0, lifeMax = 0;
        /*
        for (int h=0; h<data.height; h++) {
          for (int w=0; w<data.width; w++) {
            double val = lifetimeImage[0][data.width * h + w];
            if (val < lifeMin) lifeMin = val;
            if (val > lifeMax) lifeMax = val;
          }
        }
        */
        if (lifeMin < lifetimeMin || lifeMax > lifetimeMax) {
          lifetimeMin = lifeMin;
          lifetimeMax = lifeMax;
          resetMinMax(lifetimeMin, lifetimeMax);
        }
      }
    }
  }

  // -- ChangeListener methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == cSlider) {
      int c = cSlider.getValue() - 1;
      try { ac.setCurrent(c); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      cToggle.removeActionListener(this);
      cToggle.setSelected(data.cVisible[c]);
      cToggle.addActionListener(this);
    }
  }

  // -- DisplayListener methods --

  private boolean drag = false;

  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    if (id == DisplayEvent.MOUSE_PRESSED_CENTER) {
      drag = true;
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
          for (int y=0; y<data.height; y++) {
            for (int x=0; x<data.width; x++) {
              int ndx = y * data.width + x;
              roiMask[y][x] = tri[ndx] >= 0;
              if (roiMask[y][x]) {
                roiX = x;
                roiY = y;
                roiCount++;
              }
            }
          }
          roiPercent = 100000 * roiCount / (data.width * data.height) / 1000.0;
          slim.plotData(true, true, true);
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

  public void changedUpdate(DocumentEvent e) { rescaleMinMax(); }
  public void insertUpdate(DocumentEvent e) { rescaleMinMax(); }
  public void removeUpdate(DocumentEvent e) { rescaleMinMax(); }

  // -- Helper methods --

  /** Handles cursor updates. */
  private void doCursor(double[] cursor, boolean rescale, boolean refit) {
    double[] domain = cursorToDomain(iPlot, cursor);
    roiX = (int) Math.round(domain[0]);
    roiY = (int) Math.round(domain[1]);
    if (roiX < 0) roiX = 0;
    if (roiX >= data.width) roiX = data.width - 1;
    if (roiY < 0) roiY = 0;
    if (roiY >= data.height) roiY = data.height - 1;
    roiCount = 1;
    slim.plotData(true, rescale, refit);
  }

  private void resetMinMax(double min, double max) {
    System.out.println("resetMinMax: min=" + min + ", max=" + max);//TEMP
    minField.getDocument().removeDocumentListener(this);
    maxField.getDocument().removeDocumentListener(this);
    minField.setText("" + min);
    maxField.setText("" + max);
    minField.getDocument().addDocumentListener(this);
    maxField.getDocument().addDocumentListener(this);
    rescaleMinMax();
  }

  private void rescaleMinMax() {
    double min = 0, max = 0;
    boolean validRange = true;
    try {
      min = Double.parseDouble(minField.getText());
      minField.setBackground(validColor);
    }
    catch (NumberFormatException exc) {
      validRange = false;
      minField.setBackground(INVALID_COLOR);
    }
    try {
      max = Double.parseDouble(maxField.getText());
      maxField.setBackground(validColor);
    }
    catch (NumberFormatException exc) {
      validRange = false;
      maxField.setBackground(INVALID_COLOR);
    }
    if (validRange) {
      try {
        imageMap.setRange(min, max);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  private int doIntensity() {
    int maxChan = 0;
    try {
      if (intensityField == null) {
        intensityField = new FieldImpl(types.cxyvFunc, types.cSet);
        intensityMin = intensityMax = 0;
        maxChan = 0;
        for (int c=0; c<data.channels; c++) {
          float[][] samples = new float[1][data.width * data.height];
          for (int y=0; y<data.height; y++) {
            for (int x=0; x<data.width; x++) {
              int sum = 0;
              for (int t=0; t<data.timeBins; t++) sum += data.data[c][y][x][t];
              if (sum > intensityMax) {
                intensityMax = sum;
                maxChan = c;
              }
              samples[0][data.width * y + x] = sum;
            }
          }
          FlatField ff = new FlatField(types.xyvFunc, types.xySet);
          ff.setSamples(samples, false);
          intensityField.setSample(c, ff);
        }
      }
      imageRef.setData(intensityField);

      // reset to grayscale color map
      ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
      cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

      resetMinMax(intensityMin, intensityMax);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return maxChan;
  }

  private void doLifetime() {
    // TODO
    // https://skyking.microscopy.wisc.edu/trac/java/ticket/130
    try {
      if (lifetimeField == null) {
        lifetimeField = new FieldImpl(types.cxyvFunc, types.cSet);
        for (int c=0; c<data.channels; c++) {
          float[][] samples = new float[1][data.width * data.height];
          FlatField ff = new FlatField(types.xyvFunc, types.xySet);
          ff.setSamples(samples, false);
          lifetimeField.setSample(c, ff);
        }
      }
      imageRef.setData(lifetimeField);

      // reset to RGB color map
      ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
      cc.setTable(ColorControl.initTableVis5D(new float[3][256]));
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  private void doSpectralProjection() {
    // TODO
    // https://skyking.microscopy.wisc.edu/trac/java/ticket/86
  }

  private void doEmissionSpectrum() {
    // TODO
    // https://skyking.microscopy.wisc.edu/trac/java/ticket/164
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

}
