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

import java.rmi.RemoteException;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import loci.visbio.data.*;

import loci.visbio.state.Dynamic;

import loci.visbio.util.MathUtil;
import loci.visbio.util.VisUtil;

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

  /** MathType for Text mappings. */
  protected static final TextType TEXT_TYPE =
    TextType.getTextType("overlay_text");

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

  /** Overlay range type. */
  protected static final TupleType TEXT_RANGE_TUPLE = makeTextRangeTuple();

  /** Constructs the overlay range type for text overlays. */
  protected static TupleType makeTextRangeTuple() {
    TupleType tt = null;
    try {
      tt = new TupleType(new ScalarType[] {
        TEXT_TYPE, RED_TYPE, GREEN_TYPE, BLUE_TYPE
      });
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    return tt;
  }


  // -- Fields --

  /** Controls for creating overlays. */
  protected OverlayWidget controls;

  /** List of overlays for each dimensional position. */
  protected Vector[] overlays;

  /** Whether left mouse button is currently being pressed. */
  protected boolean mouseDownLeft;


  // -- Constructor --

  /** Creates an overlay object for the given transform. */
  public OverlayTransform(DataTransform parent, String name) {
    super(parent, name);
    initState(null);
    parent.addTransformListener(this);
  }


  // -- OverlayTransform API methods --

  /** Adds an overlay object at the given dimensional position. */
  public void addObject(OverlayObject obj, int[] pos) {
    int ndx = MathUtil.positionToRaster(lengths, pos);
    if (ndx < 0 || ndx >= overlays.length) return;
    overlays[ndx].add(obj);
    notifyListeners(new TransformEvent(this));
  }

  /** Gets the overlay objects at the given dimensional position. */
  public OverlayObject[] getObjects(int[] pos) {
    int ndx = MathUtil.positionToRaster(lengths, pos);
    if (ndx < 0 || ndx >= overlays.length) return null;
    OverlayObject[] oo = new OverlayObject[overlays[ndx].size()];
    overlays[ndx].copyInto(oo);
    return oo;
  }

  /** Sets the font used to render text for these overlays. */
  public void setFont(Font font) {
    // CTR START HERE TODO - Improve this so that it doesn't suck.
    // Somehow need to update font for all textcontrols of all currently
    // active displays. This is very tricky. Think about it some more.
    /*
    TextControl textControl = (TextControl) tMap.getControl();
    if (textControl != null) textControl.setFont(font);
    */
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
  public TupleType getTextRangeType() { return TEXT_RANGE_TUPLE; }

  /**
   * Gets a scaling value suitable for computing
   * an overlay size or picking threshold.
   */
  public int getScalingValue() {
    ImageTransform it = (ImageTransform) parent;
    int width = it.getImageWidth();
    int height = it.getImageHeight();
    return width < height ? width : height;
  }


  // -- Static DataTransform API methods --

  /** Creates a new set of overlays, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform dt = dm.getSelectedData();
    if (!isValidParent(dt)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getVisBio(),
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
  public Data getData(int[] pos, int dim) {
    if (dim != 2) return null;

    int q = MathUtil.positionToRaster(lengths, pos);
    if (q < 0 || q >= overlays.length) return null;
    int size = overlays[q].size();
    FieldImpl rgbField = null, txtField = null;
    try {
      if (size > 0) {
        // compute number of selected objects and number of text objects
        int txtSize = 0, rgbSel = 0, txtSel = 0;
        for (int i=0; i<size; i++) {
          OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
          if (obj.isText()) {
            txtSize++;
            if (obj.isSelected()) txtSel++;
          }
          else if (obj.isSelected()) rgbSel++;
        }
        int rgbSize = size - txtSize;
        RealType index = RealType.getRealType("overlay_index");

        // compile standard objects into RGB field
        if (rgbSize > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getRangeType()));
          GriddedSet fieldSet = new Integer1DSet(rgbSize + rgbSel);
          rgbField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each object
          int c = 0;
          for (int i=0; i<size && c<rgbSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (obj.isText()) continue;
            DataImpl data = obj.getData();
            rgbField.setSample(c, data, false);
            c++;
          }
          // compute selection grid for each selected object
          c = 0;
          for (int i=0; i<size && c<rgbSel; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (obj.isText() || !obj.isSelected()) continue;
            rgbField.setSample(rgbSize + c, obj.getSelectionGrid(), false);
            c++;
          }
        }

        // compile text objects into text field
        if (txtSize > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getTextRangeType()));
          GriddedSet fieldSet = new Integer1DSet(txtSize + txtSel);
          txtField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each object
          int c = 0;
          for (int i=0; i<size && c<txtSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.isText()) continue;
            DataImpl data = obj.getData();
            txtField.setSample(c, data, false);
            c++;
          }
          // compute selection grid for each selected object
          c = 0;
          for (int i=0; i<size && c<txtSel; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.isText() || !obj.isSelected()) continue;
            txtField.setSample(txtSize + c, obj.getSelectionGrid(), false);
            c++;
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
    // independently manipulated; and a new Text type, for overlaid text.
    ScalarMap[] maps = null;
    try {
      ScalarMap tMap = new ScalarMap(TEXT_TYPE, Display.Text);
      ScalarMap rMap = new ScalarMap(RED_TYPE, Display.Red);
      ScalarMap gMap = new ScalarMap(GREEN_TYPE, Display.Green);
      ScalarMap bMap = new ScalarMap(BLUE_TYPE, Display.Blue);
      rMap.setRange(0, 1);
      gMap.setRange(0, 1);
      bMap.setRange(0, 1);
      maps = new ScalarMap[] {
        new ScalarMap(x, Display.XAxis),
        new ScalarMap(y, Display.YAxis),
        tMap, rMap, gMap, bMap
      };
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

  /** Responds to mouse gestures with appropriate overlay interaction. */
  public void displayChanged(DisplayEvent e) {
    int eventId = e.getId();
    OverlayTool tool = controls.getActiveTool();
    DisplayImpl display = (DisplayImpl) e.getDisplay();
    if (eventId == DisplayEvent.MOUSE_PRESSED_LEFT) {
      mouseDownLeft = true;
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        DisplayWindow window = DisplayWindow.getDisplayWindow(display);
        int[] pos = window.getTransformHandler().getPos(this);
        tool.mouseDown((float) coords[0], (float) coords[1], pos,
          e.getInputEvent().getModifiers());
      }
    }
    else if (eventId == DisplayEvent.MOUSE_RELEASED_LEFT) {
      mouseDownLeft = false;
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        DisplayWindow window = DisplayWindow.getDisplayWindow(display);
        int[] pos = window.getTransformHandler().getPos(this);
        tool.mouseUp((float) coords[0], (float) coords[1], pos,
          e.getInputEvent().getModifiers());
      }
    }
    else if (mouseDownLeft && eventId == DisplayEvent.MOUSE_DRAGGED) {
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        DisplayWindow window = DisplayWindow.getDisplayWindow(display);
        int[] pos = window.getTransformHandler().getPos(this);
        tool.mouseDrag((float) coords[0], (float) coords[1], pos,
          e.getInputEvent().getModifiers());
      }
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

    controls = new OverlayWidget(this);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- TransformListener API methods --

  /** Called when parent data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    initState(null);
    notifyListeners(new TransformEvent(this));
  }


  // -- Utility methods --

  /** Constructs a range value with the given component values. */
  public static Tuple getTextRangeValue(String text,
    float r, float g, float b)
  {
    Tuple tuple = null;
    try {
      tuple = new Tuple(TEXT_RANGE_TUPLE, new Data[] {
        new Text(TEXT_TYPE, text),
        new Real(RED_TYPE, r),
        new Real(GREEN_TYPE, g),
        new Real(BLUE_TYPE, b)
      });
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return tuple;
  }

}
