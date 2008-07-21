//
// IntensityPane.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import visad.*;
import visad.bom.CurveManipulationRendererJ3D;
import visad.java3d.DisplayImplJ3D;
import visad.java3d.TwoDDisplayRendererJ3D;

/**
 * Slim Plotter's 2D intensity pane.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/IntensityPane.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/IntensityPane.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IntensityPane extends JPanel
  implements ActionListener, ChangeListener, DisplayListener, DocumentListener
{

  // -- Fields --

  private SlimPlotter slim;
  private DisplayImpl iPlot;
  private ScalarMap intensityMap;
  private AnimationControl ac;

  // data parameters
  private int width, height;
  private boolean[] cVisible;

  // ROI parameters
  private float[][] roiGrid;
  private UnionSet curveSet;
  private Irregular2DSet roiSet;
  private DataReferenceImpl roiRef;

  private int roiCount;
  private double roiPercent;
  private int roiX, roiY;
  private boolean[][] roiMask;

  // GUI components for intensity pane
  private JSlider cSlider;
  private JTextField minField, maxField;
  private JCheckBox cToggle;

  // -- Constructor --

  public IntensityPane(SlimPlotter slim, FieldImpl field,
    int width, int height, int channels,
    int max, int maxChan, boolean[] cVisible,
    RealType xType, RealType yType, RealType vType, RealType cType)
    throws VisADException, RemoteException
  {
    this.slim = slim;
    this.width = width;
    this.height = height;
    this.cVisible = cVisible;

    roiCount = width * height;
    roiPercent = 100;

    // plot intensity data
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
    intensityMap = new ScalarMap(vType, Display.RGB);
    iPlot.addMap(intensityMap);
    iPlot.addMap(new ScalarMap(cType, Display.Animation));
    DataReferenceImpl intensityRef = new DataReferenceImpl("intensity");
    iPlot.addReference(intensityRef);

    // set up curve manipulation renderer
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
    RealTupleType xy = new RealTupleType(xType, yType);
    UnionSet dummyCurve = new UnionSet(new Gridded2DSet[] {
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

    intensityRef.setData(field);
    ColorControl cc = (ColorControl) iPlot.getControl(ColorControl.class);
    cc.setTable(ColorControl.initTableGreyWedge(new float[3][256]));

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
    minField = new JTextField("0", 4);
    minField.setMaximumSize(minField.getPreferredSize());
    minField.setToolTipText("<html>" +
      "Adjusts intensity plot's minimum color value.<br>" +
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
    maxField = new JTextField("" + max, 4);
    maxField.setMaximumSize(maxField.getPreferredSize());
    maxField.setToolTipText("<html>" +
      "Adjusts intensity plot's maximum color value.<br>" +
      "Anything greater than this value appears white.</html>");
    maxField.getDocument().addDocumentListener(this);
    maxPane.add(maxField);

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

    cSlider.setValue(maxChan + 1);
    rescaleMinMax();
    /*
    JPanel minMaxPane = new JPanel();
    minMaxPane.setLayout(new BoxLayout(minMaxPane, BoxLayout.X_AXIS));
    add(minMaxPane);

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
    maxSlider.setMajorTickSpacing(max);
    maxSlider.setMinorTickSpacing(minor);
    maxSlider.setPaintTicks(true);
    maxSlider.addChangeListener(this);
    maxSlider.setBorder(new EmptyBorder(0, 5, 8, 5));
    maxPane.add(maxSlider);
    */
  }

  // -- IntensityPane methods --

  public int getROICount() { return roiCount; }
  public double getROIPercent() { return roiPercent; }
  public int getROIX() { return roiX; }
  public int getROIY() { return roiY; }
  public boolean[][] getROIMask() { return roiMask; }

  // -- ActionListener methods --

  /** Handles checkbox presses. */
  public void actionPerformed(ActionEvent e) {
    // toggle visibility of this channel
    int c = cSlider.getValue() - 1;
    cVisible[c] = !cVisible[c];
    slim.plotData(true, true, false);
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
      cToggle.setSelected(cVisible[c]);
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
    if (roiX >= width) roiX = width - 1;
    if (roiY < 0) roiY = 0;
    if (roiY >= height) roiY = height - 1;
    roiCount = 1;
    slim.plotData(true, rescale, refit);
  }

  private void rescaleMinMax() {
    try {
      int min = Integer.parseInt(minField.getText());
      int max = Integer.parseInt(maxField.getText());
      intensityMap.setRange(min, max);
    }
    catch (NumberFormatException exc) { }
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

}
