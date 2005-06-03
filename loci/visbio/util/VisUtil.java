//
// VisUtil.java
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

package loci.visbio.util;

import java.awt.GraphicsConfiguration;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;
import visad.*;
import visad.java2d.*;
import visad.util.ReflectedUniverse;
import visad.util.Util;

/** VisUtil contains useful VisAD functions. */
public abstract class VisUtil {

  // -- Data utility methods --

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType)
    throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType, new Integer1DSet(zType, fields.length));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    double min, double max) throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType,
      new Linear1DSet(zType, min, max, fields.length));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    SampledSet fieldSet) throws VisADException, RemoteException
  {
    if (fields == null || zType == null ||
      fields.length == 0 || fields[0] == null)
    {
      return null;
    }
    if (fields[0] == null) return null;
    FunctionType fieldType = new FunctionType(zType, fields[0].getType());
    FieldImpl field = new FieldImpl(fieldType, fieldSet);
    field.setSamples(fields, false);
    return field;
  }

  /**
   * Collapses a field from the form <tt>(z -> ((x, y) -> (v1, ..., vn)))</tt>
   * into the form <tt>((x, y, z) -> (v1, ..., vn))</tt>.
   */
  public static FlatField collapse(FieldImpl f)
    throws VisADException, RemoteException
  {
    if (f == null) return null;

    try { return (FlatField) f.domainMultiply(); }
    catch (FieldException exc) { }

    // images dimensions do not match; resample so that they do
    GriddedSet set = (GriddedSet) f.getDomainSet();
    int len = set.getLengths()[0];
    int resX = 0;
    int resY = 0;
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      int[] l = flatSet.getLengths();
      if (l[0] > resX) resX = l[0];
      if (l[1] > resY) resY = l[1];
    }
    FieldImpl nf = new FieldImpl((FunctionType) f.getType(), set);
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      int[] l = flatSet.getLengths();
      if (l[0] > resX) resX = l[0];
      if (l[1] > resY) resY = l[1];
    }
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      nf.setSample(i, flat.resample(
        new Integer2DSet(flatSet.getType(), resX, resY),
        Data.WEIGHTED_AVERAGE, Data.NO_ERRORS), false);
    }
    return (FlatField) nf.domainMultiply();
  }

  /**
   * Resamples a volume of the form <tt>((x, y, z) -> (v1, ..., vn))</tt>
   * to the given resolution along each of its three spatial axes.
   */
  public static FlatField makeCube(FlatField volume, int res)
    throws VisADException, RemoteException
  {
    GriddedSet set = (GriddedSet) volume.getDomainSet();
    int[] len = set.getLengths();
    if (len[0] == res && len[1] == res && len[2] == res) return volume;
    else {
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      Linear3DSet nset = new Linear3DSet(set.getType(),
        lo[0], hi[0], res, lo[1], hi[1], res, lo[2], hi[2], res);
      return (FlatField) volume.resample(nset,
        Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
    }
  }

  /**
   * Gets an array of ScalarMaps from the given display,
   * corresponding to the specified RealTypes.
   */
  public static ScalarMap[] getMaps(DisplayImpl display, RealType[] types) {
    ScalarMap[] maps = new ScalarMap[types.length];
    Vector v = display.getMapVector();
    int size = v.size();
    for (int i=0; i<size; i++) {
      ScalarMap map = (ScalarMap) v.elementAt(i);
      ScalarType type = map.getScalar();
      for (int j=0; j<types.length; j++) {
        if (type.equals(types[j])) {
          maps[j] = map;
          break;
        }
      }
    }
    return maps;
  }

  /** Converts a FlatField's MathType into the one given (if compatible). */
  public static FlatField switchType(FlatField field, FunctionType newType)
    throws VisADException, RemoteException
  {
    return switchType(field, newType, null);
  }

  /**
   * Converts a FlatField's MathType into the one given (if compatible),
   * and uses the specified domain set units.
   */
  public static FlatField switchType(FlatField field, FunctionType newType,
    Unit[] units) throws VisADException, RemoteException
  {
    if (field == null) throw new VisADException("field cannot be null");
    if (newType == null) throw new VisADException("newType cannot be null");
    if (!newType.equalsExceptName(field.getType())) {
      throw new VisADException("Types do not match");
    }

    // there is no way to alter a domain set's units, so we are forced to
    // reconstruct the domain set to set the units properly
    Set domainSet = field.getDomainSet();
    if (domainSet instanceof LinearSet) {
      LinearSet set = (LinearSet) domainSet;
      int dim = domainSet.getDimension();
      double[] first = new double[dim];
      double[] last = new double[dim];
      int[] lengths = new int[dim];
      for (int i=0; i<dim; i++) {
        Linear1DSet iset = set.getLinear1DComponent(i);
        first[i] = iset.getFirst();
        last[i] = iset.getLast();
        lengths[i] = iset.getLength();
      }
      domainSet = (Set) LinearNDSet.create(newType.getDomain(),
        first, last, lengths, null, units, null);
    }
    else throw new VisADException("domain set not linear");

    CoordinateSystem rangeCoordSys = field.getRangeCoordinateSystem()[0];
    Set[] rangeSets = field.getRangeSets();
    Unit[][] rangeUnits = field.getRangeUnits();
    Unit[] newRangeUnits = new Unit[rangeUnits.length];
    for (int i=0; i<rangeUnits.length; i++) {
      newRangeUnits[i] = rangeUnits[i][0];
    }
    float[][] samples = field.getFloats(false);
    FlatField newField = new FlatField(newType,
      domainSet, rangeCoordSys, rangeSets, newRangeUnits);
    newField.setSamples(samples, false);
    return newField;
  }

  /**
   * Gets a RealType with the given name, converting
   * invalid characters within the name to underscores.
   */
  public static RealType getRealType(String name) {
    return getRealType(name, null);
  }

  /**
   * Gets a RealType with the given name and unit, converting
   * invalid characters within the name to underscores.
   */
  public static RealType getRealType(String name, Unit unit) {
    char[] c = name.toCharArray();
    for (int i=0; i<c.length; i++) {
      if (" .()".indexOf(c[i]) >= 0) c[i] = '_';
    }
    return RealType.getRealType(new String(c), unit);
  }


  // -- Display utility methods --

  /** Flag for enabling or disabling Java3D, for debugging. */
  protected static final boolean ALLOW_3D = true;

  /** Whether this JVM supports 3D displays. */
  public static boolean canDo3D() { return ALLOW_3D && Util.canDoJava3D(); }

  /** Creates a VisAD display according to the given parameters. */
  public static DisplayImpl makeDisplay(String name, boolean threeD) {
    return makeDisplay(name, threeD, null);
  }

  /** Creates a VisAD display according to the given parameters. */
  public static DisplayImpl makeDisplay(String name,
    boolean threeD, GraphicsConfiguration config)
  {
    DisplayImpl d = null;
    try {
      // determine whether Java3D is available
      boolean ok3D = canDo3D();

      // create display
      if (threeD) {
        if (!ok3D) return null;
        // keep class loader ignorant of visad.java3d classes
        ReflectedUniverse r = new ReflectedUniverse();
        try {
          r.exec("import visad.java3d.DisplayImplJ3D");
          r.setVar("name", name);
          if (config == null) r.exec("d = new DisplayImplJ3D(name)");
          else {
            r.setVar("config", config);
            r.exec("d = new DisplayImplJ3D(name, config)");
          }
          d = (DisplayImpl) r.getVar("d");
        }
        catch (VisADException exc) { exc.printStackTrace(); }
        //d = config == null ? new DisplayImplJ3D(name) :
        //  new DisplayImplJ3D(name, config);
      }
      else {
        if (ok3D) {
          // keep class loader ignorant of visad.java3d classes
          ReflectedUniverse r = new ReflectedUniverse();
          try {
            r.exec("import visad.java3d.DisplayImplJ3D");
            r.exec("import visad.java3d.TwoDDisplayRendererJ3D");
            r.setVar("name", name);
            r.exec("renderer = new TwoDDisplayRendererJ3D()");
            if (config == null) {
              r.exec("d = new DisplayImplJ3D(name, renderer)");
            }
            else {
              r.setVar("config", config);
              r.exec("d = new DisplayImplJ3D(name, renderer, config)");
            }
            d = (DisplayImpl) r.getVar("d");
          }
          catch (VisADException exc) { exc.printStackTrace(); }
          //TwoDDisplayRendererJ3D renderer = new TwoDDisplayRendererJ3D();
          //d = config == null ? new DisplayImplJ3D(name, renderer) :
          //  new DisplayImplJ3D(name, renderer, config);
        }
        else d = new DisplayImplJ2D(name);
      }

      // configure display events
      //d.disableEvent(DisplayEvent.MOUSE_PRESSED);
      //d.disableEvent(DisplayEvent.MOUSE_PRESSED_LEFT);
      //d.disableEvent(DisplayEvent.MOUSE_PRESSED_CENTER);
      //d.disableEvent(DisplayEvent.MOUSE_PRESSED_RIGHT);
      //d.disableEvent(DisplayEvent.MOUSE_RELEASED);
      //d.disableEvent(DisplayEvent.MOUSE_RELEASED_LEFT);
      //d.disableEvent(DisplayEvent.MOUSE_RELEASED_CENTER);
      //d.disableEvent(DisplayEvent.MOUSE_RELEASED_RIGHT);
      d.disableEvent(DisplayEvent.MAP_ADDED);
      d.disableEvent(DisplayEvent.MAPS_CLEARED);
      d.disableEvent(DisplayEvent.REFERENCE_ADDED);
      d.disableEvent(DisplayEvent.REFERENCE_REMOVED);
      d.disableEvent(DisplayEvent.DESTROYED);
      d.disableEvent(DisplayEvent.MAP_REMOVED);
      d.enableEvent(DisplayEvent.MOUSE_DRAGGED);

      // configure keyboard behavior
      KeyboardBehavior kb = null;
      if (ok3D) {
        // keep class loader ignorant of visad.java3d classes
        ReflectedUniverse r = new ReflectedUniverse();
        try {
          r.exec("import java.awt.event.KeyEvent");
          r.exec("import visad.java3d.DisplayRendererJ3D");
          r.exec("import visad.java3d.KeyboardBehaviorJ3D");
          r.setVar("d", d);
          r.exec("dr = d.getDisplayRenderer()");
          r.exec("kb = new KeyboardBehaviorJ3D(dr)");
          r.exec("dr.addKeyboardBehavior(kb)");
          r.setVar("mods", InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);
          r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_POS, " +
            "KeyEvent.VK_DOWN, mods)");
          r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_NEG, " +
            "KeyEvent.VK_UP, mods)");
          r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_POS, " +
            "KeyEvent.VK_LEFT, mods)");
          r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_NEG, " +
            "KeyEvent.VK_RIGHT, mods)");
          kb = (KeyboardBehavior) r.getVar("kb");
        }
        catch (VisADException exc) { exc.printStackTrace(); }
        //DisplayRendererJ3D dr = (DisplayRendererJ3D) d.getDisplayRenderer();
        //kb = new KeyboardBehaviorJ3D(dr);
        //dr.addKeyboardBehavior((KeyboardBehaviorJ3D) kb);
        //int mods = InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK;
        //kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_POS,
        //  KeyEvent.VK_DOWN, mods);
        //kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_NEG,
        //  KeyEvent.VK_UP, mods);
        //kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_POS,
        //  KeyEvent.VK_LEFT, mods);
        //kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_NEG,
        //  KeyEvent.VK_RIGHT, mods);
      }
      else {
        DisplayRendererJ2D dr = (DisplayRendererJ2D) d.getDisplayRenderer();
        kb = new KeyboardBehaviorJ2D(dr);
        dr.addKeyboardBehavior((KeyboardBehaviorJ2D) kb);
      }
      if (!threeD) {
        kb.mapKeyToFunction(KeyboardBehavior.TRANSLATE_UP,
          KeyEvent.VK_UP, InputEvent.CTRL_MASK);
        kb.mapKeyToFunction(KeyboardBehavior.TRANSLATE_DOWN,
          KeyEvent.VK_DOWN, InputEvent.CTRL_MASK);
        kb.mapKeyToFunction(KeyboardBehavior.TRANSLATE_LEFT,
          KeyEvent.VK_LEFT, InputEvent.CTRL_MASK);
        kb.mapKeyToFunction(KeyboardBehavior.TRANSLATE_RIGHT,
          KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK);
      }

      // configure mouse behavior
      d.getMouseBehavior().getMouseHelper().setFunctionMap(new int[][][] {
        {{MouseHelper.DIRECT, MouseHelper.DIRECT},
         {MouseHelper.DIRECT, MouseHelper.DIRECT}},
        {{MouseHelper.CURSOR_TRANSLATE, MouseHelper.CURSOR_ZOOM},
         {MouseHelper.CURSOR_ROTATE, MouseHelper.NONE}},
        {{MouseHelper.ROTATE, MouseHelper.ZOOM},
         {MouseHelper.TRANSLATE, MouseHelper.NONE}}
      });

      // configure other display parameters
      if (threeD) d.getGraphicsModeControl().setLineWidth(2.0f);
      //d.getDisplayRenderer().setPickThreshhold(Float.MAX_VALUE);
    }
    catch (VisADException exc) { d = null; }
    catch (RemoteException exc) { d = null; }
    return d;
  }

  /** Gets a graphics configuration for use with stereo displays. */
  public static GraphicsConfiguration getStereoConfiguration() {
    GraphicsConfiguration config;
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import java.awt.GraphicsDevice");
      r.exec("import java.awt.GraphicsEnvironment");
      r.exec("import javax.media.j3d.GraphicsConfigTemplate3D");

      r.exec("ge = GraphicsEnvironment.getLocalGraphicsEnvironment()");
      r.exec("gd = ge.getDefaultScreenDevice()");
      r.exec("gct3d = new GraphicsConfigTemplate3D()");
      r.exec("gct3d.setStereo(GraphicsConfigTemplate3D.REQUIRED)");
      r.exec("cfgs = gd.getConfigurations()");
      r.exec("config = gct3d.getBestConfiguration(cfgs)");
      config = (GraphicsConfiguration) r.getVar("config");
    }
    catch (VisADException exc) { config = null; }
    return config;
  }

  /** Determines whether the given display is 3D. */
  public static boolean isDisplay3D(DisplayImpl display) {
    // keep class loader ignorant of visad.java3d classes
    String displayClass = display.getClass().getName();
    String drClass = display.getDisplayRenderer().getClass().getName();
    return displayClass.equals("visad.java3d.DisplayImplJ3D") &&
      !drClass.equals("visad.java3d.TwoDDisplayRendererJ3D");
    //return display instanceof DisplayImplJ3D &&
    //  !(display.getDisplayRenderer() instanceof TwoDDisplayRendererJ3D);
  }

  /**
   * Gets an array of ScalarMaps from the given display,
   * matching the specified RealTypes and/or DisplayRealTypes.
   */
  public static ScalarMap[] getMaps(DisplayImpl display,
    ScalarType[] st, DisplayRealType[] drt)
  {
    Vector v = new Vector();
    Vector maps = display.getMapVector();
    int size = maps.size();
    for (int i=0; i<size; i++) {
      ScalarMap map = (ScalarMap) maps.elementAt(i);
      if (st != null) {
        boolean success = false;
        ScalarType type = map.getScalar();
        for (int j=0; j<st.length; j++) {
          if (type.equals(st[j])) {
            success = true;
            break;
          }
        }
        if (!success) continue;
      }
      if (drt != null) {
        boolean success = false;
        DisplayRealType type = map.getDisplayScalar();
        for (int j=0; j<drt.length; j++) {
          if (type.equals(drt[j])) {
            success = true;
            break;
          }
        }
        if (!success) continue;
      }
      v.add(map);
    }
    ScalarMap[] matches = new ScalarMap[v.size()];
    v.copyInto(matches);
    return matches;
  }

  /** Hashtable for keeping track of display states. */
  private static final Hashtable DISPLAY_HASH = new Hashtable();

  /** Enables or disables the given display. */
  public static void setDisplayDisabled(DisplayImpl d, boolean disable) {
    if (d == null) return;
    Integer i = (Integer) DISPLAY_HASH.get(d);
    if (i == null) i = new Integer(0);
    boolean doDisable = false, doEnable = false;
    if (disable) {
      if (i.intValue() == 0) doDisable = true;
      i = new Integer(i.intValue() + 1);
    }
    else {
      i = new Integer(i.intValue() - 1);
      if (i.intValue() == 0) doEnable = true;
    }
    DISPLAY_HASH.put(d, i);
    if (doDisable) d.disableAction();
    else if (doEnable) d.enableAction();
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d, double[] cursor) {
    return cursorToDomain(d, null, cursor);
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d,
    RealType[] types, double[] cursor)
  {
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

  /** Converts the given pixel coordinates to cursor coordinates. */
  public static double[] pixelToCursor(DisplayImpl d, int x, int y) {
    MouseBehavior mb = d.getDisplayRenderer().getMouseBehavior();
    VisADRay ray = mb.findRay(x, y);
    return ray.position;
  }

  /** Converts the given pixel coordinates to domain coordinates. */
  public static double[] pixelToDomain(DisplayImpl d, int x, int y) {
    return cursorToDomain(d, pixelToCursor(d, x, y));
  }

  /** Redraws exception messages in a display's bottom left-hand corner. */
  public static void redrawMessages(DisplayImpl d) {
    // HACK - awful, awful code to force quick redraw of exception strings
    DisplayRenderer dr = d.getDisplayRenderer();
    if (dr instanceof DisplayRendererJ2D) {
      ((DisplayRendererJ2D) dr).getCanvas().repaint();
    }
    else { // renderer instanceof visad.java3d.DisplayRendererJ3D
      ReflectedUniverse r = new ReflectedUniverse();
      try {
        r.setVar("dr", dr);
        r.exec("canvas = dr.getCanvas()");
        r.setVar("zero", 0);
        r.exec("canvas.renderField(zero)");
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      //VisADCanvasJ3D canvas = ((DisplayRendererJ3D) dr).getCanvas();
      //canvas.renderField(0);

      // CTR: The following line forces the countdown to update immediately,
      // but causes a strange deadlock problem to occur when there are multiple
      // data objects in a single display. This deadlock is difficult to
      // diagnose because it seems to be occurring within a single thread (one
      // of the burn-in threads locks a GraphicsContext3D within
      // GraphicsContext3D.flush(), then inexplicably waits for it to be
      // unlocked within GraphicsContext3D.runMonitor()).

      //canvas.getGraphicsContext3D().flush(true);
    }
  }

  /** Sets whether the given 3D display uses a parallel projection. */
  public static void setParallelProjection(DisplayImpl d, boolean parallel) {
    if (!isDisplay3D(d)) return;
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import visad.java3d.DisplayImplJ3D");
      r.setVar("d", d);
      r.exec("gmc = d.getGraphicsModeControl()");
      if (parallel) {
        r.exec("gmc.setProjectionPolicy(DisplayImplJ3D.PARALLEL_PROJECTION)");
      }
      else {
        r.exec("gmc.setProjectionPolicy(" +
          "DisplayImplJ3D.PERSPECTIVE_PROJECTION)");
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }
  }

}
