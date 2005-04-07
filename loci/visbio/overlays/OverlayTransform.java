//
// OverlayTransform.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.overlays;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.data.*;
import loci.visbio.state.Dynamic;
import loci.visbio.util.*;
import loci.visbio.view.DisplayWindow;
import visad.*;

/** A set of overlays on top another transform. */
public class OverlayTransform extends DataTransform
  implements TransformListener
{

  // -- Constants --

  /** MathType for blue color mappings. */
  protected static final RealType RED_TYPE =
    RealType.getRealType("overlay_red");

  /** MathType for blue color mappings. */
  protected static final RealType GREEN_TYPE =
    RealType.getRealType("overlay_green");

  /** MathType for blue color mappings. */
  protected static final RealType BLUE_TYPE =
    RealType.getRealType("overlay_blue");

  /** Overlay range type. */
  protected static final RealTupleType RANGE_TUPLE = makeRangeTuple();

  /** Constructs the overlay range type for most overlays. */
  protected static RealTupleType makeRangeTuple() {
    RealTupleType rtt = null;
    try {
      rtt = new RealTupleType(new RealType[] {
        RED_TYPE, GREEN_TYPE, BLUE_TYPE
      });
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    return rtt;
  }

  /** String indicating a given field is not applicable to an overlay. */
  protected static final String NOT_APPLICABLE = "N/A";


  // -- Fields --

  /** Controls for creating overlays. */
  protected OverlayWidget controls;

  /** List of overlays for each dimensional position. */
  protected Vector[] overlays;

  /** Current dimensional position. */
  protected int[] pos;

  /** MathType for Text mappings. */
  protected TextType textType;

  /** Overlay range type for text overlays. */
  protected TupleType textRangeTuple;

  /** Whether left mouse button is currently being pressed. */
  protected boolean mouseDownLeft;

  /** Font metrics for the current font. */
  protected FontMetrics fontMetrics;

  /** Whether to draw text. */
  protected boolean drawText = true;


  // -- Constructor --

  /** Creates an overlay object for the given transform. */
  public OverlayTransform(DataTransform parent, String name) {
    super(parent, name);
    // text type is unique to each transform instance so that
    // each transform can have its own font settings
    textType = TextType.getTextType("overlay_text_" + transformId);
    try {
      textRangeTuple = new TupleType(new ScalarType[] {
        textType, RED_TYPE, GREEN_TYPE, BLUE_TYPE
      });
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    initState(null);
    parent.addTransformListener(this);
  }


  // -- OverlayTransform API methods --

  /** Adds an overlay object at the current dimensional position. */
  public void addObject(OverlayObject obj) { addObject(obj, pos); }

  /** Adds an overlay object at the given dimensional position. */
  public void addObject(OverlayObject obj, int[] pos) {
    int ndx = MathUtil.positionToRaster(lengths, pos);
    if (ndx < 0 || ndx >= overlays.length) return;
    overlays[ndx].add(obj);
    if (ObjectUtil.arraysEqual(pos, this.pos)) controls.refreshListObjects();
    notifyListeners(new TransformEvent(this));
  }

  /** Removes an overlay object at the current dimensional position. */
  public void removeObject(OverlayObject obj) { removeObject(obj, pos); }

  /** Removes an overlay object at the given dimensional position. */
  public void removeObject(OverlayObject obj, int[] pos) {
    int ndx = MathUtil.positionToRaster(lengths, pos);
    if (ndx < 0 || ndx >= overlays.length) return;
    overlays[ndx].remove(obj);
    if (ObjectUtil.arraysEqual(pos, this.pos)) controls.refreshListObjects();
    notifyListeners(new TransformEvent(this));
  }

  /** Removes selected overlay objects at the current dimensional position. */
  public void removeSelectedObjects() { removeSelectedObjects(pos); }

  /** Removes selected overlay objects at the given dimensional position. */
  public void removeSelectedObjects(int[] pos) {
    boolean anyRemoved = false;
    for (int j=0; j<overlays.length; j++) {
      int i = 0;
      while (i < overlays[j].size()) {
        OverlayObject obj = (OverlayObject) overlays[j].elementAt(i);
        if (obj.isSelected()) {
          overlays[j].removeElementAt(i);
          anyRemoved = true;
        }
        else i++;
      }
    }
    if (anyRemoved) {
      if (ObjectUtil.arraysEqual(pos, this.pos)) controls.refreshListObjects();
      notifyListeners(new TransformEvent(this));
    }
  }

  /** Gets the overlay objects at the current dimensional position. */
  public OverlayObject[] getObjects() { return getObjects(pos); }

  /** Gets the overlay objects at the given dimensional position. */
  public OverlayObject[] getObjects(int[] pos) {
    int ndx = MathUtil.positionToRaster(lengths, pos);
    if (ndx < 0 || ndx >= overlays.length) return null;
    OverlayObject[] oo = new OverlayObject[overlays[ndx].size()];
    overlays[ndx].copyInto(oo);
    return oo;
  }

  /** Sets transform's current dimensional position. */
  public void setPos(int[] pos) {
    if (ObjectUtil.arraysEqual(this.pos, pos)) return;
    this.pos = pos;
    controls.refreshListObjects();
  }

  /** Gets transform's current dimensional position. */
  public int[] getPos() { return pos; }

  /** Reads the overlays from the given reader. */
  public void loadOverlays(BufferedReader in) throws IOException {
    Vector[] loadedOverlays = OverlayIO.loadOverlays(in, this);
    if (loadedOverlays == null) return;
    overlays = loadedOverlays;
    controls.refreshListObjects();
    notifyListeners(new TransformEvent(this));
  }

  /** Writes the overlays to the given writer. */
  public void saveOverlays(PrintWriter out) {
    OverlayIO.saveOverlays(out, this);
  }

  /** Gets domain type (XY). */
  public RealTupleType getDomainType() {
    ImageTransform it = (ImageTransform) parent;
    RealTupleType rtt = null;
    try { rtt = new RealTupleType(it.getXType(), it.getYType()); }
    catch (VisADException exc) { exc.printStackTrace(); }
    return rtt;
  }

  /** Gets range type (RGB). */
  public TupleType getRangeType() { return RANGE_TUPLE; }

  /** Gets range type for text overlays (R, G, B, text). */
  public TupleType getTextRangeType() { return textRangeTuple; }

  /** Constructs a range value with the given component values. */
  public Tuple getTextRangeValue(String text,
    float r, float g, float b)
  {
    Tuple tuple = null;
    try {
      tuple = new Tuple(textRangeTuple, new Data[] {
        new Text(textType, text),
        new Real(RED_TYPE, r),
        new Real(GREEN_TYPE, g),
        new Real(BLUE_TYPE, b)
      });
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return tuple;
  }

  /**
   * Gets a scaling value suitable for computing
   * an overlay size or picking threshold.
   */
  public int getScalingValue() {
    int width = getScalingValueX();
    int height = getScalingValueY();
    return width < height ? width : height;
  }

  /**
   * Gets a scaling value along the X axis, suitable for
   * computing an overlay width or picking threshold.
   */
  public int getScalingValueX() {
    ImageTransform it = (ImageTransform) parent;
    return it.getImageWidth();
  }

  /**
   * Gets a scaling value along the Y axis, suitable for
   * computing an overlay height or picking threshold.
   */
  public int getScalingValueY() {
    ImageTransform it = (ImageTransform) parent;
    return it.getImageHeight();
  }

  /** Gets font metrics for the current font. */
  public FontMetrics getFontMetrics() { return fontMetrics; }

  /**
   * Whether text is drawn. This toggle exists because drawing text is very
   * slow, so some operations temporarily turn off text rendering to speed
   * up onscreen updates.
   */
  public void setTextDrawn(boolean value) {
    drawText = value;
    notifyListeners(new TransformEvent(this));
  }

  /** Gets whether text is drawn. */
  public boolean isTextDrawn() { return drawText; }


  // -- Static DataTransform API methods --

  /** Creates a new set of overlays, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform dt = dm.getSelectedData();
    if (!isValidParent(dt)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getControlPanel(),
      "Title of overlays:", "Create overlays",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      dt.getName() + " overlays");
    if (n == null) return null;
    return new OverlayTransform(dt, n);
  }

  /**
   * Indicates whether this transform type would accept
   * the given transform as its parent transform.
   */
  public static boolean isValidParent(DataTransform data) {
    return data != null && data instanceof ImageTransform;
  }

  /** Indicates whether this transform type requires a parent transform. */
  public static boolean isParentRequired() { return true; }


  // -- DataTransform API methods --

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   *
   * @return null if the transform does not provide data of that dimensionality
   */
  public Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 2) {
      System.err.println(name + ": invalid dimensionality (" + dim + ")");
      return null;
    }

    int q = MathUtil.positionToRaster(lengths, pos);
    if (q < 0 || q >= overlays.length) return null;
    int size = overlays[q].size();
    FieldImpl rgbField = null, txtField = null;
    try {
      if (size > 0) {
        // compute number of selected objects and number of text objects
        int rgbSize = 0, txtSize = 0, sel = 0, outline = 0;
        for (int i=0; i<size; i++) {
          OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
          if (obj.hasText()) {
            if (drawText || obj.isSelected()) txtSize++;
            else outline++;
          }
          else rgbSize++;
          // do not paint grids for objects still in the initial draw phase
          if (obj.isSelected() && !obj.isDrawing()) sel++;
        }
        RealType index = RealType.getRealType("overlay_index");

        // compile standard objects into RGB field
        if (rgbSize > 0 || sel > 0 || outline > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getRangeType()));
          GriddedSet fieldSet = new Integer1DSet(rgbSize + sel + outline);
          rgbField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each non-text object
          for (int i=0, c=0; i<size && c<rgbSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (obj.hasText()) continue;
            rgbField.setSample(c++, obj.getData(), false);
          }
          // compute selection grid for each selected object
          for (int i=0, c=0; i<size && c<sel; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.isSelected() || obj.isDrawing()) continue;
            rgbField.setSample(rgbSize + c++, obj.getSelectionGrid(), false);
          }
          // compute outline grid for each invisible text object
          for (int i=0, c=0; i<size && c<outline; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.hasText() || obj.isSelected()) continue;
            rgbField.setSample(rgbSize + sel + c++,
              obj.getSelectionGrid(true), false);
          }
        }

        // compile text objects into text field
        if (txtSize > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getTextRangeType()));
          GriddedSet fieldSet = new Integer1DSet(txtSize);
          txtField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each text object
          int c = 0;
          for (int i=0; i<size && c<txtSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.hasText()) continue;
            if (!drawText && !obj.isSelected()) continue;
            txtField.setSample(c++, obj.getData(), false);
          }
        }
      }
      if (rgbField == null && txtField == null) return null;
      else if (rgbField == null) return txtField;
      else if (txtField == null) return rgbField;
      else return new Tuple(new Data[] {rgbField, txtField}, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) { return dim == 2; }

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public ScalarMap[] getSuggestedMaps() {
    // Overlay XYZ types piggyback on the parent, so that two sets of XYZ
    // coordinates don't show up during cursor probes (and so that the overlays
    // are placed properly without an extra set of setRange calls).
    RealType x = ((ImageTransform) parent).getXType();
    RealType y = ((ImageTransform) parent).getYType();

    // We do need new RGB types, so that overlay colors can be
    // independently manipulated, and a new Text type, for overlaid text.
    ScalarMap[] maps = null;
    try {
      ScalarMap xMap = new ScalarMap(x, Display.XAxis);
      ScalarMap yMap = new ScalarMap(y, Display.YAxis);
      ScalarMap tMap = new ScalarMap(textType, Display.Text);
      ScalarMap rMap = new ScalarMap(RED_TYPE, Display.Red);
      ScalarMap gMap = new ScalarMap(GREEN_TYPE, Display.Green);
      ScalarMap bMap = new ScalarMap(BLUE_TYPE, Display.Blue);
      rMap.setRange(0, 1);
      gMap.setRange(0, 1);
      bMap.setRange(0, 1);
      maps = new ScalarMap[] {xMap, yMap, tMap, rMap, gMap, bMap};
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return maps;
  }

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public String getCacheId(int[] pos, boolean global) { return null; }

  /**
   * Overlay transforms are rendered immediately,
   * due to frequent user interaction.
   */
  public boolean isImmediate() { return true; }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }

  /**
   * Sets the font used for text overlays.
   * Since changing the font changes the data (text overlay bounding boxes,
   * this method is overridden to send a DATA_CHANGED after recomputing them.
   */
  public void setFont(Font font) {
    super.setFont(font);

    // obtain new font metrics
    fontMetrics = controls.getFontMetrics(font);

    // recompute grid boxes for text overlays
    for (int j=0; j<overlays.length; j++) {
      for (int i=0; i<overlays[j].size(); i++) {
        OverlayObject obj = (OverlayObject) overlays[j].elementAt(i);
        if (obj instanceof OverlayText) obj.computeGridParameters();
      }
    }

    notifyListeners(new TransformEvent(this, TransformEvent.DATA_CHANGED));
  }

  /** Responds to mouse gestures with appropriate overlay interaction. */
  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    OverlayTool tool = controls.getActiveTool();
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    if (id == DisplayEvent.TRANSFORM_DONE) updatePosition(display);
    else if (id == DisplayEvent.MOUSE_PRESSED_LEFT) {
      mouseDownLeft = true;
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        tool.mouseDown((float) coords[0], (float) coords[1],
          pos, e.getModifiers());
      }
    }
    else if (id == DisplayEvent.MOUSE_RELEASED_LEFT) {
      mouseDownLeft = false;
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        tool.mouseUp((float) coords[0], (float) coords[1],
          pos, e.getModifiers());
      }
    }
    else if (mouseDownLeft && id == DisplayEvent.MOUSE_DRAGGED) {
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        tool.mouseDrag((float) coords[0], (float) coords[1],
          pos, e.getModifiers());
      }
    }
    else if (id == DisplayEvent.KEY_PRESSED) {
      updatePosition(display);
      int code = e.getKeyCode();
      if (code == KeyEvent.VK_DELETE) removeSelectedObjects();
      else {
        // update selected text objects
        int ndx = MathUtil.positionToRaster(lengths, pos);
        if (ndx < 0 || ndx >= overlays.length) return;
        Vector objs = overlays[ndx];
        boolean changed = false;
        for (int i=0; i<objs.size(); i++) {
          OverlayObject oo = (OverlayObject) objs.elementAt(i);
          if (oo.isSelected() && oo.hasText()) {
            if (code == KeyEvent.VK_BACK_SPACE) {
              String text = oo.getText();
              int len = text.length();
              if (len > 0) {
                oo.setText(text.substring(0, len - 1));
                changed = true;
              }
            }
            else {
              char c = ((KeyEvent) e.getInputEvent()).getKeyChar();
              if (c != KeyEvent.CHAR_UNDEFINED && c >= ' ') {
                oo.setText(oo.getText() + c);
                changed = true;
              }
            }
          }
        }
        if (changed) notifyListeners(new TransformEvent(this));
      }
      // No tools use keyPressed functionality, so it is disabled for now
      //if (tool != null) tool.keyPressed(e.getKeyCode(), e.getModifiers());
    }
    else if (id == DisplayEvent.KEY_RELEASED) {
      updatePosition(display);
      // No tools use keyReleased functionality, so it is disabled for now
      //if (tool != null) tool.keyReleased(e.getKeyCode(), e.getModifiers());
    }
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    OverlayTransform data = (OverlayTransform) dyn;

    // CTR TODO return true iff data matches this object
    return false;
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof OverlayTransform;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    OverlayTransform data = (OverlayTransform) dyn;

    if (data != null) {
      // CTR TODO synchronize data with this object
    }

    lengths = parent.getLengths();
    dims = parent.getDimTypes();
    makeLabels();

    int len = MathUtil.getRasterLength(lengths);
    Vector[] v = new Vector[len];
    int minLen = 0;
    if (overlays != null) {
      // CTR - This logic is simplistic and will result in erroneous behavior
      // should a transform with multiple dimensional axes suffer a length
      // alteration along its axes. That is, the rasterization will probably be
      // shifted so that a particular position such as (3, 5) will no longer be
      // (3, 5), even if (3, 5) is still a valid dimensional position. But we
      // cannot guarantee much in the general case, because the number of
      // dimensional positions could also have shifted. What we should do is
      // sniff out exactly how a transform has changed by examining the old and
      // new lengths arrays, and act appropriately, but for now we simply
      // preserve as many overlays as possible. If the dimensional axes have
      // been significantly altered, too bad.
      minLen = overlays.length < len ? overlays.length : len;
      System.arraycopy(overlays, 0, v, 0, minLen);
    }
    for (int i=minLen; i<len; i++) v[i] = new Vector();
    overlays = v;
    pos = new int[lengths.length];

    controls = new OverlayWidget(this);
    fontMetrics = controls.getFontMetrics(font);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- TransformListener API methods --

  /** Called when parent data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) {
      initState(null);
      notifyListeners(new TransformEvent(this));
    }
  }


  // -- Helper methods --

  /**
   * Updates the dimensional position based on
   * the current state of the given display.
   */
  public void updatePosition(DisplayImpl display) {
    DisplayWindow window = DisplayWindow.getDisplayWindow(display);
    setPos(window.getTransformHandler().getPos(this));
  }

}
