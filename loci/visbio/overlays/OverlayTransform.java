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

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.VisBio;
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
    Vector[] loadedOverlays = null;
    boolean foundHeader = false;
    int lineNum = 0;
    while (true) {
      lineNum++;
      String line = in.readLine();
      if (line == null) break;
      String trim = line.trim();
      if (trim.startsWith("#")) continue; // skip comments
      if (trim.equals("")) continue; // skip blank lines

      StringTokenizer st = new StringTokenizer("#" + line + "#", "\t");
      int count = st.countTokens();
      if (!foundHeader) {
        // parse table header from first valid line
        int numDims = count - 10;
        if (numDims < 0) {
          JOptionPane.showMessageDialog(controls,
            "Invalid table header: insufficient column headings.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return;
        }
        st.nextToken(); // skip "Overlay" column heading

        // verify lengths and dims match the parent transform
        int[] theLengths = new int[numDims];
        String[] theDims = new String[numDims];
        for (int i=0; i<numDims; i++) {
          String s = st.nextToken();
          int left = s.lastIndexOf(" (");
          int right = s.lastIndexOf(")");
          try {
            theLengths[i] = Integer.parseInt(s.substring(left + 2, right));
            theDims[i] = s.substring(0, left);
          }
          catch (IndexOutOfBoundsException exc) { }
          catch (NumberFormatException exc) { }
        }
        if (!ObjectUtil.arraysEqual(dims, theDims)) {
          JOptionPane.showMessageDialog(controls,
            "Invalid table header: dimensional axis types do not match.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (!ObjectUtil.arraysEqual(lengths, theLengths)) {
          JOptionPane.showMessageDialog(controls,
            "Invalid table header: dimensional axis lengths do not match.",
            "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
          return;
        }
        // initialize replacement overlay lists
        loadedOverlays = new Vector[overlays.length];
        for (int i=0; i<loadedOverlays.length; i++) {
          loadedOverlays[i] = new Vector();
        }
        foundHeader = true;
      }
      else {
        // parse table entry
        if (count != lengths.length + 10) {
          System.err.println("Warning: line " + lineNum +
            " has the incorrect number of columns (" + count +
            " instead of " + (lengths.length + 10) + ") and will be ignored.");
          continue;
        }
        String type = st.nextToken().substring(1); // remove initial #
        int[] position = new int[lengths.length];
        for (int i=0; i<position.length; i++) {
          try { position[i] = Integer.parseInt(st.nextToken()); }
          catch (NumberFormatException exc) {
            position = null;
            break;
          }
        }
        if (position == null) {
          System.err.println("Warning: line " + lineNum +
            " has an invalid dimensional position and will be ignored.");
          continue;
        }
        String sx1 = st.nextToken();
        String sy1 = st.nextToken();
        String sx2 = st.nextToken();
        String sy2 = st.nextToken();
        float x1, y1, x2, y2;
        try {
          x1 = sx1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx1);
          y1 = sy1.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy1);
          x2 = sx2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sx2);
          y2 = sy2.equals(NOT_APPLICABLE) ? Float.NaN : Float.parseFloat(sy2);
        }
        catch (NumberFormatException exc) {
          System.err.println("Warning: line " + lineNum +
            " has invalid coordinate values and will be ignored.");
          continue;
        }
        String text = st.nextToken();
        Color color = ColorUtil.hexToColor(st.nextToken());
        if (color == null) {
          System.err.println("Warning: line " + lineNum +
            " has an invalid color value and will be ignored.");
          continue;
        }
        boolean filled = st.nextToken().equalsIgnoreCase("true");
        String group = st.nextToken();
        String notes = st.nextToken();
        notes = notes.substring(0, notes.length() - 1); // remove trailing #

        // instantiate overlay object
        String className = "loci.visbio.overlays.Overlay" + type;
        String classError = "Warning: could not reconstruct overlay " +
          "of type " + type + " defined on line " + lineNum + ": ";
        OverlayObject obj = null;
        try {
          Class c = Class.forName(className);
          Constructor con = c.getConstructor(new Class[] {getClass()});
          obj = (OverlayObject) con.newInstance(new Object[] {this});
        }
        catch (ClassCastException exc) {
          System.err.println(classError +
            "class " + className + " does not extend OverlayObject.");
          continue;
        }
        catch (ClassNotFoundException exc) {
          System.err.println(classError +
            "class " + className + " not found.");
          continue;
        }
        catch (IllegalAccessException exc) {
          System.err.println(classError +
            "cannot access constructor for class " + className + ".");
          continue;
        }
        catch (InstantiationException exc) {
          System.err.println(classError +
            "cannot instantiate class " + className + ".");
          continue;
        }
        catch (InvocationTargetException exc) {
          System.err.println(classError +
            "error invoking constructor for class " + className + ".");
          continue;
        }
        catch (NoSuchMethodException exc) {
          System.err.println(classError +
            "no appropriate constructor for class " + className + ".");
          continue;
        }
        if (obj == null) {
          System.err.println(classError +
            "constructor for class " + className + " returned null object.");
          continue;
        }

        // assign overlay parameters
        int r = MathUtil.positionToRaster(lengths, position);
        if (r < 0 || r >= loadedOverlays.length) {
          System.err.println(classError + "invalid dimensional position.");
          continue;
        }
        obj.x1 = x1;
        obj.y1 = y1;
        obj.x2 = x2;
        obj.y2 = y2;
        obj.text = text;
        obj.color = color;
        obj.filled = filled;
        obj.group = group;
        obj.notes = notes;
        obj.drawing = false;
        obj.selected = false;
        obj.computeGridParameters();

        // add overlay to list
        loadedOverlays[r].add(obj);
      }
    }

    if (loadedOverlays == null) {
      JOptionPane.showMessageDialog(controls,
        "Invalid overlay file: no table header found.",
        "Cannot load overlays", JOptionPane.ERROR_MESSAGE);
      return;
    }
    overlays = loadedOverlays;
    controls.refreshListObjects();
    notifyListeners(new TransformEvent(this));
  }

  /** Writes the overlays to the given writer. */
  public void saveOverlays(PrintWriter out) {
    // file header
    out.println("# " + VisBio.TITLE + " " + VisBio.VERSION +
      " overlay file written " + new Date());
    out.println();

    // table header
    out.print("Overlay\t");
    for (int p=0; p<lengths.length; p++) {
      out.print(dims[p] + " (" + lengths[p] + ")\t");
    }
    out.println("x1\ty1\tx2\ty2\ttext\tcolor\tfilled\tgroup\tnotes");

    // overlays table
    for (int i=0; i<overlays.length; i++) {
      int[] position = MathUtil.rasterToPosition(lengths, i);
      StringBuffer sb = new StringBuffer();
      for (int p=0; p<position.length; p++) sb.append(position[p] + "\t");
      String posString = sb.toString();
      for (int j=0; j<overlays[i].size(); j++) {
        OverlayObject obj = (OverlayObject) overlays[i].elementAt(j);
        out.print(obj.toString());
        out.print("\t");
        out.print(posString);
        out.print(obj.hasEndpoint() ? "" + obj.x1 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint() ? "" + obj.y1 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint2() ? "" + obj.x2 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasEndpoint2() ? "" + obj.y2 : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.hasText() ? obj.text : NOT_APPLICABLE);
        out.print("\t");
        out.print(ColorUtil.colorToHex(obj.color));
        out.print("\t");
        out.print(obj.canBeFilled() ? "" + obj.filled : NOT_APPLICABLE);
        out.print("\t");
        out.print(obj.group.replaceAll("\t", " "));
        out.print("\t");
        out.println(obj.notes.replaceAll("\t", " "));
      }
    }
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
  public Data getData(int[] pos, int dim) {
    if (dim != 2) return null;

    int q = MathUtil.positionToRaster(lengths, pos);
    if (q < 0 || q >= overlays.length) return null;
    int size = overlays[q].size();
    FieldImpl rgbField = null, txtField = null;
    try {
      if (size > 0) {
        // compute number of selected objects and number of text objects
        int txtSize = 0, sel = 0;
        for (int i=0; i<size; i++) {
          OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
          if (obj.hasText()) txtSize++;
          // do not paint grids for objects still in the initial draw phase
          if (obj.isSelected() && !obj.isDrawing()) sel++;
        }
        int rgbSize = size - txtSize;
        RealType index = RealType.getRealType("overlay_index");

        // compile standard objects into RGB field
        if (rgbSize > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getRangeType()));
          GriddedSet fieldSet = new Integer1DSet(rgbSize + sel);
          rgbField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each object
          int c = 0;
          for (int i=0; i<size && c<rgbSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (obj.hasText()) continue;
            rgbField.setSample(c++, obj.getData(), false);
          }
          // compute selection grid for each selected object
          c = 0;
          for (int i=0; i<size && c<sel; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.isSelected() || obj.isDrawing()) continue;
            rgbField.setSample(rgbSize + c++, obj.getSelectionGrid(), false);
          }
        }

        // compile text objects into text field
        if (txtSize > 0) {
          FunctionType fieldType = new FunctionType(index,
            new FunctionType(getDomainType(), getTextRangeType()));
          GriddedSet fieldSet = new Integer1DSet(txtSize);
          txtField = new FieldImpl(fieldType, fieldSet);
          // compute overlay data for each object
          int c = 0;
          for (int i=0; i<size && c<txtSize; i++) {
            OverlayObject obj = (OverlayObject) overlays[q].elementAt(i);
            if (!obj.hasText()) continue;
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

  /** Responds to mouse gestures with appropriate overlay interaction. */
  public void displayChanged(DisplayEvent e) {
    int eventId = e.getId();
    OverlayTool tool = controls.getActiveTool();
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    if (eventId == DisplayEvent.TRANSFORM_DONE) updatePosition(display);
    else if (eventId == DisplayEvent.MOUSE_PRESSED_LEFT) {
      mouseDownLeft = true;
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        tool.mouseDown((float) coords[0], (float) coords[1], pos,
          e.getInputEvent().getModifiers());
      }
    }
    else if (eventId == DisplayEvent.MOUSE_RELEASED_LEFT) {
      mouseDownLeft = false;
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
        tool.mouseUp((float) coords[0], (float) coords[1], pos,
          e.getInputEvent().getModifiers());
      }
    }
    else if (mouseDownLeft && eventId == DisplayEvent.MOUSE_DRAGGED) {
      updatePosition(display);
      if (tool != null) {
        double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
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
    pos = new int[lengths.length];

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
