//
// MatlabUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

import loci.visbio.math.*;
import visad.*;

/** MatlabUtil contains useful MATLAB and Octave functions. */
public abstract class MatlabUtil {

  // -- MATLAB methods --

  /** The singleton MATLAB control. */
  private static MatlabControl matlab;

  /** Gets the singleton MATLAB interface object. */
  public static MatlabControl getMatlab() {
    if (matlab == null) {
      try { matlab = new MatlabControl(); }
      catch (Throwable t) { }
    }
    return matlab;
  }

  /** Gets the version of MATLAB available, or null if none. */
  public static String getMatlabVersion() {
    MatlabControl mc = getMatlab();
    if (mc == null) return null;
    Object rval = null;
    try { rval = mc.blockingFeval("version", null); }
    catch (InterruptedException exc) { }
    return rval == null ? "Installed" : rval.toString();
  }


  // -- Octave methods --

  /** Gets the singleton Octave interface object. */
  public static OctaveContext getOctave() {
    try { return OctaveContext.getInstance(); }
    catch (Throwable t) { return null; }
  }

  /** Gets the version of Octave available, or null if none. */
  public static String getOctaveVersion() {
    OctaveContext cx = getOctave();
    if (cx == null) return null;
    OctaveValueList ovl = cx.evalString("version");
    return ovl.get(0).stringValue();
  }


  // -- FlatField methods --

  /**
   * Converts FlatField samples to a series of
   * 2D image planes (one per range component).
   */
  public static double[][][] samplesToDoubles(double[][] samples,
    int xLen, int yLen)
  {
    double[][][] d = new double[samples.length][][];
    for (int i=0; i<samples.length; i++) {
      d[i] = unrasterize(samples[i], xLen, yLen);
    }
    return d;
  }

  /** Converts a series of 2D image planes to a FlatField samples array. */
  public static double[][] doublesToSamples(double[][][] d) {
    double[][] samples = new double[d.length][];
    for (int i=0; i<d.length; i++) samples[i] = rasterize(d[i]);
    return samples;
  }

  /** Converts a rasterized 1D array into a properly dimensioned 2D array. */
  public static double[][] unrasterize(double[] arr, int xLen, int yLen) {
    double[][] d = new double[yLen][xLen];
    for (int y=0; y<yLen; y++) System.arraycopy(arr, y * xLen, d[y], 0, xLen);
    return d;
  }

  /** Converts a 2D array into a rasterized 1D array. */
  public static double[] rasterize(double[][] arr) {
    int xLen = arr[0].length;
    int yLen = arr.length;
    double[] d = new double[xLen * yLen];
    for (int y=0; y<yLen; y++) System.arraycopy(arr[y], 0, d, y * xLen, xLen);
    return d;
  }

}
