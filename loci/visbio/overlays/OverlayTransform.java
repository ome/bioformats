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

import java.rmi.RemoteException;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import loci.visbio.data.*;

import loci.visbio.state.Dynamic;

import loci.visbio.util.VisUtil;

import visad.*;

/** A set of overlays on top another transform. */
public class OverlayTransform extends DataTransform
  implements TransformListener
{

  // -- Fields --

  /** Controls for creating overlays. */
  protected OverlayWidget controls;

  /** List of overlays. */
  protected Vector overlays;

  /** Whether left mouse button is currently being pressed. */
  protected boolean mouseDownLeft;


  // -- Constructor --

  /** Creates an overlay object for the given transform. */
  public OverlayTransform(DataTransform parent, String name) {
    super(parent, name);
    overlays = new Vector();
    initState(null);
    parent.addTransformListener(this);
  }


  // -- OverlayTransform API methods --

  /** Adds an overlay object to the current frame. */
  public void addObject(OverlayObject obj) {
    overlays.add(obj);
    notifyListeners(new TransformEvent(this));
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
  public RealTupleType getRangeType() {
    RealTupleType rtt = null;
    try {
      rtt = new RealTupleType(
        RealType.getRealType("overlay_red"),
        RealType.getRealType("overlay_green"),
        RealType.getRealType("overlay_blue"));
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    return rtt;
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

    int size = overlays.size();
    FieldImpl field = null;
    if (size > 0) {
      try {
        RealType index = RealType.getRealType("overlay_index");
        FunctionType ftype = new FunctionType(getDomainType(), getRangeType());
        FunctionType fieldType = new FunctionType(index, ftype);
        GriddedSet fieldSet = new Integer1DSet(size);
        field = new FieldImpl(fieldType, fieldSet);
        for (int i=0; i<size; i++) {
          OverlayObject obj = (OverlayObject) overlays.elementAt(i);
          DataImpl data = obj.getData();
          field.setSample(i, data, false);
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    return field;
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
    // independently manipulated.
    RealType r = RealType.getRealType("overlay_red");
    RealType g = RealType.getRealType("overlay_green");
    RealType b = RealType.getRealType("overlay_blue");

    ScalarMap[] maps = null;
    try {
      ScalarMap rMap = new ScalarMap(r, Display.Red);
      ScalarMap gMap = new ScalarMap(g, Display.Green);
      ScalarMap bMap = new ScalarMap(b, Display.Blue);
      rMap.setRange(0, 1);
      gMap.setRange(0, 1);
      bMap.setRange(0, 1);
      maps = new ScalarMap[] {
        new ScalarMap(x, Display.XAxis),
        new ScalarMap(y, Display.YAxis),
        rMap, gMap, bMap
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
    DisplayImpl display = (DisplayImpl) e.getDisplay();
    if (eventId == DisplayEvent.MOUSE_PRESSED_LEFT) {
      mouseDownLeft = true;
      double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
      OverlayTool tool = controls.getActiveTool();
      if (tool != null) tool.mouseDown((float) coords[0], (float) coords[1]);
    }
    else if (eventId == DisplayEvent.MOUSE_RELEASED_LEFT) {
      mouseDownLeft = false;
      double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
      OverlayTool tool = controls.getActiveTool();
      if (tool != null) tool.mouseUp((float) coords[0], (float) coords[1]);
    }
    else if (mouseDownLeft && eventId == DisplayEvent.MOUSE_DRAGGED) {
      double[] coords = VisUtil.pixelToDomain(display, e.getX(), e.getY());
      OverlayTool tool = controls.getActiveTool();
      if (tool != null) tool.mouseDrag((float) coords[0], (float) coords[1]);
    }
    // CTR START HERE
    // Each type of overlay needs its own subclass.
    //
    // The OverlayTransform has a setTool() method that flags which type of
    // overlay is active. The OverlayWidget calls setTool whenever the user
    // changes the tool. Then, when one of the above three events happens, it
    // gets passed to the active tool. Each tool also provides its own set of
    // GUI controls, that get set visible when that tool is selected
    // (filled vs unfilled, endpoints visible vs invisible).
    //
    // One of the tools is a selection tool that allows the selection of
    // existing overlays. Otherwise, reclicking the left button will draw
    // another overlay.
    //
    // I have set up OverlayTool as the base class for these, and begun work on
    // LineTool. Continue that by implementing the mouse*(int, int) methods.
    //
    // Then, each overlay type also needs a class for the actual objects
    // (made up of VisAD Set constructs, perhaps?).
    //
    // Create OverlayObject as the base class for these, and then subclass into
    // LineObject, etc. OverlayObject covers the basics, including color.
    //
    // Color is actually tricky, because the FlatField will need a number of
    // samples equal to the number of samples in that portion of the UnionSet.
    // Will need a general-purpose combineSets method to do this, probably.
    //
    // One of the buttons on OverlayWidget is a delete button.
    // Also will be a way to link multiple overlays together, later.
    //
    //
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
    // CTR TODO update this transform if parent changes
    // or do we even need to? Probably...
    // if we do update, we also need to notify our listeners of a change:
    notifyListeners(new TransformEvent(this));
  }

}
