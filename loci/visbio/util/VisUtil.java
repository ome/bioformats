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

import java.rmi.RemoteException;

import java.util.Hashtable;
import java.util.Vector;

import visad.*;

import visad.java2d.DisplayImplJ2D;
import visad.java2d.DisplayRendererJ2D;

import visad.java3d.DisplayImplJ3D;
import visad.java3d.DisplayRendererJ3D;
import visad.java3d.TwoDDisplayRendererJ3D;
import visad.java3d.VisADCanvasJ3D;

import visad.util.ReflectedUniverse;
import visad.util.Util;

/** VisUtil contains useful VisAD functions. */
public abstract class VisUtil {

  // -- Data utility methods --

  /**
   * Collapses a field from the form: <tt>(z -> ((x, y) -> (v1, ..., vn))</tt>
   * into the form: <tt>((x, y, z) -> (v1, ..., vn))</tt>.
   */
  public static FlatField collapse(FieldImpl f)
    throws VisADException, RemoteException
  {
    if (f == null) return null;

    FlatField collapse;
    try { collapse = (FlatField) f.domainMultiply(); }
    catch (FieldException exc) { collapse = null; }

    if (collapse == null) {
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
      collapse = (FlatField) nf.domainMultiply();
    }

    return collapse;
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
    DisplayImpl d;
    try {
      // determine whether Java3D is available
      boolean ok3D = canDo3D();

      // create display
      if (threeD) {
        if (!ok3D) return null;
        d = config == null ? new DisplayImplJ3D(name) :
          new DisplayImplJ3D(name, config);
      }
      else {
        if (ok3D) {
          TwoDDisplayRendererJ3D renderer = new TwoDDisplayRendererJ3D();
          d = config == null ? new DisplayImplJ3D(name, renderer) :
            new DisplayImplJ3D(name, renderer, config);
        }
        else d = new DisplayImplJ2D(name);
      }

      // configure display
      d.disableEvent(DisplayEvent.MOUSE_PRESSED);
      d.disableEvent(DisplayEvent.MOUSE_PRESSED_LEFT);
      d.disableEvent(DisplayEvent.MOUSE_PRESSED_CENTER);
      d.disableEvent(DisplayEvent.MOUSE_PRESSED_RIGHT);
      d.disableEvent(DisplayEvent.MOUSE_RELEASED);
      d.disableEvent(DisplayEvent.MOUSE_RELEASED_LEFT);
      d.disableEvent(DisplayEvent.MOUSE_RELEASED_CENTER);
      d.disableEvent(DisplayEvent.MOUSE_RELEASED_RIGHT);
      d.disableEvent(DisplayEvent.MAP_ADDED);
      d.disableEvent(DisplayEvent.MAPS_CLEARED);
      d.disableEvent(DisplayEvent.REFERENCE_ADDED);
      d.disableEvent(DisplayEvent.REFERENCE_REMOVED);
      d.disableEvent(DisplayEvent.DESTROYED);
      d.disableEvent(DisplayEvent.MAP_REMOVED);
      d.getMouseBehavior().getMouseHelper().setFunctionMap(new int[][][] {
        {{MouseHelper.DIRECT, MouseHelper.DIRECT},
         {MouseHelper.DIRECT, MouseHelper.DIRECT}},
        {{MouseHelper.CURSOR_TRANSLATE, MouseHelper.CURSOR_ZOOM},
         {MouseHelper.CURSOR_ROTATE, MouseHelper.NONE}},
        {{MouseHelper.ROTATE, MouseHelper.ZOOM},
         {MouseHelper.TRANSLATE, MouseHelper.NONE}}
      });
      //d.getDisplayRenderer().setPickThreshhold(Float.MAX_VALUE);
      if (threeD) d.getGraphicsModeControl().setLineWidth(2.0f);
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
    return display instanceof DisplayImplJ3D &&
      !(display.getDisplayRenderer() instanceof TwoDDisplayRendererJ3D);
  }

  /**
   * Gets an array of ScalarMaps from the given display,
   * corresponding to the specified DisplayRealTypes.
   */
  public static ScalarMap[] getMaps(DisplayImpl display,
    DisplayRealType[] drt)
  {
    Vector v = new Vector();
    Vector maps = display.getMapVector();
    int size = maps.size();
    for (int i=0; i<size; i++) {
      ScalarMap map = (ScalarMap) maps.elementAt(i);
      DisplayRealType type = map.getDisplayScalar();
      for (int j=0; j<drt.length; j++) {
        if (type.equals(drt[j])) {
          v.add(map);
          break;
        }
      }
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
    if (dr instanceof DisplayRendererJ3D) {
      VisADCanvasJ3D canvas = ((DisplayRendererJ3D) dr).getCanvas();
      canvas.renderField(0);
      canvas.getGraphicsContext3D().flush(true);
    }
    else if (dr instanceof DisplayRendererJ2D) {
      ((DisplayRendererJ2D) dr).getCanvas().repaint();
    }
  }

}
