//
// DisplayUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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
import loci.formats.ReflectException;
import loci.formats.ReflectedUniverse;
import visad.*;
import visad.java2d.*;
import visad.util.Util;

/** DisplayUtil contains useful VisAD display functions. */
public final class DisplayUtil {

  // -- Constructor --

  private DisplayUtil() { }

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
        catch (ReflectException exc) { exc.printStackTrace(); }
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
          catch (ReflectException exc) { exc.printStackTrace(); }
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
          r.exec("import java.awt.event.InputEvent");
          r.exec("import java.awt.event.KeyEvent");
          r.exec("import visad.java3d.DisplayRendererJ3D");
          r.exec("import visad.java3d.KeyboardBehaviorJ3D");
          r.setVar("d", d);
          r.exec("dr = d.getDisplayRenderer()");
          r.exec("kb = new KeyboardBehaviorJ3D(dr)");
          r.exec("dr.addKeyboardBehavior(kb)");
          if (threeD) {
            r.setVar("mods", InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_POS, " +
              "KeyEvent.VK_DOWN, mods)");
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_X_NEG, " +
              "KeyEvent.VK_UP, mods)");
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_POS, " +
              "KeyEvent.VK_LEFT, mods)");
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Y_NEG, " +
              "KeyEvent.VK_RIGHT, mods)");
          }
          else {
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Z_POS, " +
              "KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK)");
            r.exec("kb.mapKeyToFunction(KeyboardBehaviorJ3D.ROTATE_Z_NEG, " +
              "KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK)");
          }
          kb = (KeyboardBehavior) r.getVar("kb");
        }
        catch (ReflectException exc) { exc.printStackTrace(); }
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
        kb.mapKeyToFunction(KeyboardBehaviorJ2D.ROTATE_Z_POS,
          KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK);
        kb.mapKeyToFunction(KeyboardBehaviorJ2D.ROTATE_Z_NEG,
          KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK);
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

  /**
   * Sets the eye separation for a stereo 3D display.
   * Does nothing for other types of displays.
   */
  public static void setEyeSeparation(DisplayImpl d, double position) {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      // set eye separation (from Dan Bramer and Don Murray)
      r.exec("import javax.vecmath.Point3d");
      r.setVar("renderer", d.getDisplayRenderer());
      r.exec("view = renderer.getView()");
      r.exec("body = view.getPhysicalBody()");
      r.setVar("lpos", -position);
      r.setVar("rpos", +position);
      r.setVar("zero", 0.0);
      r.exec("left = new Point3d(lpos, zero, zero)");
      r.exec("right = new Point3d(rpos, zero, zero)");
      r.exec("body.setLeftEyePosition(lpos)");
      r.exec("body.setRightEyePosition(rpos)");
    }
    catch (ReflectException exc) { } // fails for non-stereo displays
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
    catch (ReflectException exc) { config = null; }
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

  /**
   * Gets scale values (multiplier and offset) for the X, Y and Z maps
   * corresponding to the given RealTypes (or the first ScalarMaps to
   * X, Y and Z if types is null). If no mapping to a spatial axis is
   * found, that component of the array will be null.
   *
   * @return Scale array of size [3][2], with the first dimension
   * corresponding to X, Y or Z, and the second giving multiplier and offset.
   * For example, cursor_x = scale[0][0] * domain_x + scale[0][1].
   */
  public static double[][] getScaleValues(DisplayImpl d, RealType[] types) {
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

    // get scale values
    double[][] scale = new double[3][];
    double[] dummy = new double[2];
    if (mapX == null) scale[0] = null;
    else {
      scale[0] = new double[2];
      mapX.getScale(scale[0], dummy, dummy);
    }
    if (mapY == null) scale[1] = null;
    else {
      scale[1] = new double[2];
      mapY.getScale(scale[1], dummy, dummy);
    }
    if (mapZ == null) scale[2] = null;
    else {
      scale[2] = new double[2];
      mapZ.getScale(scale[2], dummy, dummy);
    }
    return scale;
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d,
    RealType[] types, double[] cursor)
  {
    if (d == null) return null;
    double[][] scale = getScaleValues(d, types);
    double[] domain = new double[3];
    for (int i=0; i<3; i++) {
      domain[i] = scale[i] == null ? 0 :
        (cursor[i] - scale[i][1]) / scale[i][0];
    }
    return domain;
  }

  /** Converts the given domain coordinates to cursor coordinates. */
  public static double[] domainToCursor(DisplayImpl d,
    RealType[] types, double[] domain)
  {
    if (d == null) return null;
    double[][] scale = getScaleValues(d, types);
    double[] cursor = new double[3];
    for (int i=0; i<3; i++) {
      cursor[i] = scale[i] == null ? 0 :
        scale[i][0] * domain[i] + scale[i][1];
    }
    return cursor;
  }

  /** Converts the given cursor coordinates to domain coordinates. */
  public static double[] cursorToDomain(DisplayImpl d, double[] cursor) {
    return cursorToDomain(d, null, cursor);
  }

  /** Converts the given domain coordinates to cursor coordinates. */
  public static double[] domainToCursor(DisplayImpl d, double[] domain) {
    return domainToCursor(d, null, domain);
  }

  /** Converts the given pixel coordinates to cursor coordinates. */
  public static double[] pixelToCursor(DisplayImpl d, int x, int y) {
    if (d == null) return null;
    MouseBehavior mb = d.getDisplayRenderer().getMouseBehavior();
    VisADRay ray = mb.findRay(x, y);
    return ray.position;
  }

  /** Converts the given cursor coordinates to pixel coordinates. */
  public static int[] cursorToPixel(DisplayImpl d, double[] cursor) {
    if (d == null) return null;
    MouseBehavior mb = d.getDisplayRenderer().getMouseBehavior();
    return mb.getScreenCoords(cursor);
  }

  /** Converts the given pixel coordinates to domain coordinates. */
  public static double[] pixelToDomain(DisplayImpl d, int x, int y) {
    return cursorToDomain(d, pixelToCursor(d, x, y));
  }

  /** Converts the given domain coordinates to pixel coordinates. */
  public static int[] domainToPixel(DisplayImpl d, double[] domain) {
    return cursorToPixel(d, domainToCursor(d, domain));
  }

  /** Redraws exception messages in a display's bottom left-hand corner. */
  public static void redrawMessages(DisplayImpl d) {
    if (d == null) return;

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
      catch (ReflectException exc) { exc.printStackTrace(); }
      //VisADCanvasJ3D canvas = ((DisplayRendererJ3D) dr).getCanvas();
      //canvas.renderField(0);

      // NB: The following line forces the countdown to update immediately,
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
    catch (ReflectException exc) { exc.printStackTrace(); }
  }

}
