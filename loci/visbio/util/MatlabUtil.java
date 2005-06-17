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

import java.rmi.RemoteException;
import java.util.StringTokenizer;
import loci.visbio.math.*;
import visad.*;

/** MatlabUtil contains useful MATLAB and Octave functions. */
public abstract class MatlabUtil {

  // -- Constants --

  /** Variable naming scheme for MATLAB return values. */
  protected static final String VAR = "visbioValue";



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
   * Executes the given MATLAB/Octave function, converting the specified VisAD
   * FlatField into a series of 2D double arrays. Returns results as a new
   * FlatField.
   */
  public static FlatField exec(String function, FlatField ff) {
    Set set = ff.getDomainSet();
    if (!(set instanceof GriddedSet)) return null;
    GriddedSet gset = (GriddedSet) set;
    int[] len = gset.getLengths();
    double[][] samples = null;
    try { samples = ff.getValues(false); }
    catch (VisADException exc) {
      exc.printStackTrace();
      return null;
    }

    int num = samples.length;
    int cols = len[0];
    int rows = len[1];
    int size = cols * rows;

    // convert arguments into command string
    StringBuffer sb = new StringBuffer();
    sb.append(function);
    sb.append("(");
    for (int i=0; i<num; i++) {
      if (i > 0) sb.append(",");
      sb.append("[");
      for (int j=0; j<size; j++) {
        if (j > 0 && j % len[0] == 0) sb.append(";");
        sb.append(" ");
        sb.append(samples[i][j]);
      }
      sb.append("]");
    }
    sb.append(")");
    String command = sb.toString();

    double[][] samps = null;

    OctaveContext octave = getOctave();
    if (octave != null) { // try Octave
      // execute command
      OctaveValueList ovl = octave.evalString(command);

      // convert result to samples array
      samps = new double[ovl.size()][];
      for (int i=0; i<samps.length; i++) {
        OctaveMatrix m = ovl.get(i).matrixValue();
        samps[i] = m.toArray(); // samples are already rasterized
      }
    }

    MatlabControl matlab = getMatlab();
    if (samps == null && matlab != null) { // try MATLAB
      // prepend return value variables
      sb = new StringBuffer();
      sb.append("[");
      for (int i=0; i<num; i++) {
        sb.append(" ");
        sb.append(VAR);
        sb.append(i);
      }
      sb.append("] = ");
      sb.append(command);
      command = sb.toString();

      // execute command
      String ans = null;
      try { ans = matlab.blockingEval(command); }
      catch (InterruptedException exc) {
        exc.printStackTrace();
        return null;
      }

      // convert result to samples array
      samps = new double[num][size];
      StringTokenizer st = new StringTokenizer(ans);
      String matlabError = "Invalid MATLAB output: expected ";
      int v = -1, r = -1, c = -1, clo = -1, chi = -1;
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.startsWith(VAR)) {
          // parse variable name
          token = token.substring(VAR.length());
          int n = -1;
          try { n = Integer.parseInt(token); }
          catch (NumberFormatException exc) { }

          String eq = st.hasMoreTokens() ? st.nextToken() : "";

          if (n != v + 1 || !eq.equals("=")) {
            System.err.println(matlabError + "variable assignment");
            return null;
          }

          v++;
          r = 0;
          c = 0;
        }
        else if (token.equals("Columns")) {
          // parse columns range
          String first = st.hasMoreTokens() ? st.nextToken() : "";
          String through = st.hasMoreTokens() ? st.nextToken() : "";
          String last = st.hasMoreTokens() ? st.nextToken() : "";

          clo = -1;
          try { clo = Integer.parseInt(first) - 1; }
          catch (NumberFormatException exc) { }

          chi = -1;
          try { chi = Integer.parseInt(last) - 1; }
          catch (NumberFormatException exc) { }

          if (chi < clo || !through.equals("through")) {
            System.err.println(matlabError + "columns range");
            return null;
          }

          c = clo;
          r = 0;
        }
        else if (v < 0) {
          System.err.println(matlabError + "variable assignment");
          return null;
        }
        else {
          // parse matrix entry
          double value = Double.NaN;
          try { value = Double.parseDouble(token); }
          catch (NumberFormatException exc) { }

          if (value != value) {
            System.err.println(matlabError + "matrix entry");
            return null;
          }

          samps[v][r * cols + c] = value;

          c++;
          if (c > chi) {
            r++;
            c = clo;
          }
        }
      }
    }

    try {
      FlatField result = new FlatField((FunctionType) ff.getType(), set);
      result.setSamples(samps, false);
      return result;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

}
