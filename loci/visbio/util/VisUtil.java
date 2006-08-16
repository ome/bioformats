//
// VisUtil.java
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
import java.rmi.RemoteException;
import visad.*;

/**
 * @deprecated This class has been split into
 * separate DataUtil and DisplayUtil classes.
 */
public abstract class VisUtil {

  // -- Data utility methods --

  /** @deprecated */
  public static FlatField resample(FlatField f, int[] res, boolean[] range)
    throws VisADException, RemoteException
  {
    return DataUtil.resample(f, res, range);
  }

  /** @deprecated */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    boolean forceIntegerSet) throws VisADException, RemoteException
  {
    return DataUtil.resample(f, res, range, forceIntegerSet);
  }

  /** @deprecated */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    float[] min, float[] max) throws VisADException, RemoteException
  {
    return DataUtil.resample(f, res, range, min, max);
  }


  /** @deprecated */
  public static FieldImpl makeField(FlatField[] fields, RealType zType)
    throws VisADException, RemoteException
  {
    return DataUtil.makeField(fields, zType);
  }

  /** @deprecated */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    Unit setUnit) throws VisADException, RemoteException
  {
    return DataUtil.makeField(fields, zType, setUnit);
  }

  /** @deprecated */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    double min, double max) throws VisADException, RemoteException
  {
    return DataUtil.makeField(fields, zType, min, max);
  }

  /** @deprecated */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    double min, double max, Unit setUnit)
    throws VisADException, RemoteException
  {
    return DataUtil.makeField(fields, zType, min, max, setUnit);
  }

  /** @deprecated */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    SampledSet fieldSet) throws VisADException, RemoteException
  {
    return DataUtil.makeField(fields, zType, fieldSet);
  }

  /** @deprecated */
  public static FlatField collapse(FieldImpl f)
    throws VisADException, RemoteException
  {
    return DataUtil.collapse(f);
  }

  /** @deprecated */
  public static FlatField makeCube(FlatField volume, int res)
    throws VisADException, RemoteException
  {
    return DataUtil.makeCube(volume, res);
  }

  /** @deprecated */
  public static ScalarMap[] getMaps(DisplayImpl display, RealType[] types) {
    return DataUtil.getMaps(display, types);
  }

  /** @deprecated */
  public static FlatField switchType(FlatField field, FunctionType newType)
    throws VisADException, RemoteException
  {
    return DataUtil.switchType(field, newType);
  }

  /** @deprecated */
  public static FlatField switchType(FlatField field, FunctionType newType,
    Unit[] units) throws VisADException, RemoteException
  {
    return DataUtil.switchType(field, newType, units);
  }

  /** @deprecated */
  public static RealType getRealType(String name) {
    return DataUtil.getRealType(name);
  }

  /** @deprecated */
  public static RealType getRealType(String name, Unit unit) {
    return DataUtil.getRealType(name, unit);
  }


  // -- Display utility methods --

  /** @deprecated */
  public static boolean canDo3D() { return DisplayUtil.canDo3D(); }

  /** @deprecated */
  public static DisplayImpl makeDisplay(String name, boolean threeD) {
    return DisplayUtil.makeDisplay(name, threeD);
  }

  /** @deprecated */
  public static DisplayImpl makeDisplay(String name,
    boolean threeD, GraphicsConfiguration config)
  {
    return DisplayUtil.makeDisplay(name, threeD, config);
  }

  /** @deprecated */
  public static void setEyeSeparation(DisplayImpl d, double position) {
    DisplayUtil.setEyeSeparation(d, position);
  }

  /** @deprecated */
  public static GraphicsConfiguration getStereoConfiguration() {
    return DisplayUtil.getStereoConfiguration();
  }

  /** @deprecated */
  public static boolean isDisplay3D(DisplayImpl display) {
    return DisplayUtil.isDisplay3D(display);
  }

  /** @deprecated */
  public static ScalarMap[] getMaps(DisplayImpl display,
    ScalarType[] st, DisplayRealType[] drt)
  {
    return DisplayUtil.getMaps(display, st, drt);
  }

  /** @deprecated */
  public static void setDisplayDisabled(DisplayImpl d, boolean disable) {
    DisplayUtil.setDisplayDisabled(d, disable);
  }

  /** @deprecated */
  public static double[] cursorToDomain(DisplayImpl d, double[] cursor) {
    return DisplayUtil.cursorToDomain(d, cursor);
  }

  /** @deprecated */
  public static double[] cursorToDomain(DisplayImpl d,
    RealType[] types, double[] cursor)
  {
    return DisplayUtil.cursorToDomain(d, types, cursor);
  }

  /** @deprecated */
  public static double[] pixelToCursor(DisplayImpl d, int x, int y) {
    return DisplayUtil.pixelToCursor(d, x, y);
  }

  /** @deprecated */
  public static double[] pixelToDomain(DisplayImpl d, int x, int y) {
    return DisplayUtil.pixelToDomain(d, x, y);
  }

  /** @deprecated */
  public static void redrawMessages(DisplayImpl d) {
    DisplayUtil.redrawMessages(d);
  }

  /** @deprecated */
  public static void setParallelProjection(DisplayImpl d, boolean parallel) {
    DisplayUtil.setParallelProjection(d, parallel);
  }

}
