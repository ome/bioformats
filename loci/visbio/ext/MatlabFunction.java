//
// MatlabFunction.java
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

package loci.visbio.ext;

import javax.swing.JOptionPane;
import loci.visbio.data.*;
import visad.FlatField;

/** MatlabFunction executes a MATLAB script or function on a data object. */
public class MatlabFunction extends ExternalFunction {

  // -- Constructors --

  /** Creates an uninitialized MATLAB transform. */
  public MatlabFunction() { super(); }

  /** Creates a MATLAB transform from the given transform. */
  public MatlabFunction(DataTransform parent, String name, String function) {
    super(parent, name, function);
  }


  // -- ExternalFunction API methods --

  /**
   * Predicts the width, height and number of output planes, given the width,
   * height and number of input planes, and parameter values.
   *
   * @return An int[3] array representing output width, height and num values.
   */
  public int[] predict(int width, int height, int num, String[] params) {
    double[] p = new double[params.length];
    for (int i=0; i<params.length; i++) p[i] = Double.parseDouble(params[i]);
    int[] mdims = MatlabUtil.getDimensions(function, height, width, num, p);
    return new int[] {mdims[1], mdims[0], mdims[2]};
  }

  /** Gets the input parameter names and corresponding default values. */
  public FunctionParam[] params() {
    return MatlabUtil.getParameters(function);
  }

  /** Evaluates the function for the given input data and parameter values. */
  public FlatField evaluate(FlatField input, String[] params) {
    double[] p = new double[params.length];
    for (int i=0; i<params.length; i++) p[i] = Double.parseDouble(params[i]);
    return MatlabUtil.evaluate(function, input, p, getRangeTypes());
  }


  // -- Static DataTransform API methods --

  /** Creates a new MATLAB transform, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform data = dm.getSelectedData();
    if (!isValidParent(data)) return null;

    String func = (String) JOptionPane.showInputDialog(dm.getControls(),
      "MATLAB function to call:", "Create MATLAB transform",
      JOptionPane.INFORMATION_MESSAGE, null, null, "");
    if (func == null) return null;

    String n = (String) JOptionPane.showInputDialog(dm.getControls(),
      "Transform name:", "Create MATLAB transform",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      data.getName() + " " + func + " MATLAB");
    if (n == null) return null;

    return new MatlabFunction(data, n, func);
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

}
