//
// MatlabUtil.java
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

package loci.visbio.ext;

import java.rmi.RemoteException;
import visad.*;
import visad.util.ReflectedUniverse;

/** MatlabUtil contains useful MATLAB functions. */
public final class MatlabUtil {

  // -- Constructor --

  private MatlabUtil() { }

  // -- Fields --

  /** Reflected universe containing singleton MATLAB instance. */
  private static ReflectedUniverse r;


  // -- MatlabUtil API methods --

  /** Gets the version of MATLAB available, or null if none. */
  public static String getMatlabVersion() {
    if (r == null) {
      r = new ReflectedUniverse();
      try {
        r.exec("import com.mathworks.jmi.Matlab");
        r.exec("matlab = new Matlab()");
        r.setDebug(true);
      }
      catch (VisADException exc) { }
    }
    try {
      r.setVar("version", "version");
      String version = (String) r.exec("matlab.eval(version)");
      if (version.startsWith("ans =")) version = version.substring(5);
      return version.trim();
    }
    catch (VisADException exc) { }
    return null;
  }

  /**
   * Evaluates the given MATLAB function for the specified field,
   * passing the given extra parameters to the function.
   */
  public static FlatField evaluate(String func,
    FlatField field, double[] params, RealType[] rangeTypes)
  {
    Object obj = exec(func, field, params);
    try {
      FunctionType ftype = (FunctionType) field.getType();
      Set fset = field.getDomainSet();
      int y = 0, x = 0, n = 0;
      if (fset instanceof GriddedSet) {
        GriddedSet gset = (GriddedSet) fset;
        int[] lengths = gset.getLengths();
        if (lengths.length == 2) {
          x = lengths[0];
          y = lengths[1];
        }
      }
      MathType range = ftype.getRange();
      n = range instanceof TupleType ?
        ((TupleType) range).getNumberOfRealComponents() : 1;
      int[] len = getDimensions(func, y, x, n, params);

      double[][] samps = null;
      if (obj instanceof double[]) {
        samps = raster1Dto2D((double[]) obj, len[1], len[0]);
      }
      else if (obj instanceof double[][]) samps = (double[][]) obj;
      else if (obj instanceof double[][][]) {
        samps = raster3Dto2D((double[][][]) obj);
      }
      if (samps == null) {
        System.err.println("Unable to convert MATLAB function result to " +
          "proper type (result is " + obj.getClass().getName() + ")");
        return null;
      }

      ftype = new FunctionType(ftype.getDomain(),
        new RealTupleType(rangeTypes));
      if (fset instanceof SampledSet) {
        SampledSet ss = (GriddedSet) fset;
        float[] lo = ss.getLow();
        float[] hi = ss.getHi();
        fset = new Linear2DSet(fset.getType(), lo[0], hi[0], len[1],
          hi[1], lo[1], len[0], fset.getCoordinateSystem(),
          fset.getSetUnits(), fset.getSetErrors());
      }
      FlatField result = new FlatField(ftype, fset);
      result.setSamples(samps, false);
      return result;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Gets the list of parameters for the given MATLAB function. */
  public static FunctionParam[] getParameters(String func) {
    // func([], []) returns a cell array of string/double pairs, each
    // representing the name and default value of a function parameter
    Object obj = exec(func, null, null);
    if (!(obj instanceof Object[])) return null;
    Object[] o = (Object[]) obj;
    FunctionParam[] paramList = new FunctionParam[o.length];
    for (int i=0; i<o.length; i++) {
      if (o[i] instanceof Object[]) {
        Object[] oo = (Object[]) o[i];
        if (oo.length == 2 && oo[0] instanceof String &&
          oo[1] instanceof double[])
        {
          String name = (String) oo[0];
          double[] values = (double[]) oo[1];
          if (values.length == 1) {
            paramList[i] = new FunctionParam(name, "" + values[0]);
          }
        }
      }
    }
    return paramList;
  }

  /**
   * Gets output pixel dimensions for the given
   * input dimensions and parameter values.
   */
  public static int[] getDimensions(String func,
    int y, int x, int n, double[] params)
  {
    // func([], params) returns a vector of size 3 containing pixel dimensions
    Object obj = exec(func, null, prepend(y, x, n, params));
    if (!(obj instanceof double[])) return null;
    double[] d = (double[]) obj;
    int[] dim = new int[d.length];
    for (int i=0; i<d.length; i++) dim[i] = (int) d[i];
    return dim;
  }

  /**
   * Executes the given MATLAB function, with the
   * specified field and parameters as arguments.
   */
  public static Object exec(String func, FlatField field, double[] params) {
    // func(pix, params) executes the function on the input pixels
    // with the given parameters, returning the resultant pixels
    if (r == null) getMatlabVersion();
    try {
      r.setVar("func", "vbget");
      r.setVar("args", new Object[] {func, field, params});
      r.setVar("zero", 0);
      return r.exec("matlab.mtFeval(func, args, zero)");
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    return null;
  }

  /**
   * Gets pixel values from the given field, converting the result
   * to a 3D array of double-precision floating point values.
   */
  public static double[][][] getImagePixels(FlatField field) {
    // this method is called by vbget.m
    if (field == null) return null;
    Set set = field.getDomainSet();
    if (!(set instanceof GriddedSet)) return null;
    GriddedSet gset = (GriddedSet) set;
    int[] len = gset.getLengths();
    if (len.length != 2) return null;

    double[][] samps = null;
    try { samps = field.getValues(false); }
    catch (VisADException exc) { exc.printStackTrace(); }

    return raster2Dto3D(samps, len[0], len[1]);
  }

  /** Converts a double[n][y*x] into a double[y][x][n]. */
  public static double[][][] raster2Dto3D(double[][] d, int lenx, int leny) {
    if (d == null) return null;
    int num = d.length;

    double[][][] result = new double[leny][lenx][num];
    for (int y=0; y<leny; y++) {
      for (int x=0; x<lenx; x++) {
        int index = y * lenx + x;
        for (int n=0; n<num; n++) result[y][x][n] = d[n][index];
      }
    }
    return result;
  }

  /** Converts a double[y][x][n] into a double[n][y*x]. */
  public static double[][] raster3Dto2D(double[][][] d) {
    if (d == null) return null;
    int num = d.length;
    int leny = num == 0 ? 0 : (d[0] == null ? 0 : d[0].length);
    int lenx = leny == 0 ? 0 : (d[0][0] == null ? 0 : d[0][0].length);

    double[][] result = new double[num][leny * lenx];
    for (int y=0; y<leny; y++) {
      for (int x=0; x<lenx; x++) {
        int index = y * lenx + x;
        for (int n=0; n<num; n++) result[n][index] = d[y][x][n];
      }
    }
    return result;
  }

  /** Converts a double[n*x*y] into a double[n][y*x]. */
  public static double[][] raster1Dto2D(double[] d, int lenx, int leny) {
    if (d == null) return null;
    int num = d.length / lenx / leny;

    double[][] result = new double[num][leny * lenx];
    for (int y=0; y<leny; y++) {
      for (int x=0; x<lenx; x++) {
        int index = y * lenx + x;
        for (int n=0; n<num; n++) {
          result[n][index] = d[n * lenx * leny + x * leny + y];
        }
      }
    }
    return result;
  }

  /** Prepends the given dimensions to the specified parameter list. */
  public static double[] prepend(int y, int x, int n, double[] params) {
    int len = params == null ? 0 : params.length;
    double[] p = new double[3 + len];
    p[0] = y;
    p[1] = x;
    p[2] = n;
    if (params != null) System.arraycopy(params, 0, p, 3, len);
    return p;
  }

}
