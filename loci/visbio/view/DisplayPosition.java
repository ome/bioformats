//
// DisplayPosition.java
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

package loci.visbio.view;

import loci.visbio.state.Dynamic;

import loci.visbio.util.ObjectUtil;

/** DisplayPosition represents an orientation of VisAD display. */
public class DisplayPosition implements Dynamic {

  // -- Fields --

  /** Name of the group. */
  private String name;

  /** Matrix representing the position. */
  private double[] matrix;


  // -- Constructor --

  /** Constructs an uninitialized display position. */
  public DisplayPosition() { }

  /** Constructs a display position. */
  public DisplayPosition(String name, double[] matrix) {
    this.name = name;
    this.matrix = matrix;
  }


  // -- DisplayPosition API methods - state logic --

  /** Writes the current state to the given OME-CA XML object. */
  public void saveState(DisplayWindow window, String attrName) {
    window.setAttr(attrName + "_name", name);
    window.setAttr(attrName + "_matrix", ObjectUtil.arrayToString(matrix));
  }

  /** Restores the current state from the given OME-CA XML object. */
  public void restoreState(DisplayWindow window, String attrName) {
    name = window.getAttr(attrName + "_name");
    matrix = ObjectUtil.stringToDoubleArray(
      window.getAttr(attrName + "_matrix"));
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects have matching states. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    DisplayPosition position = (DisplayPosition) dyn;

    return ObjectUtil.objectsEqual(name, position.name) &&
      ObjectUtil.arraysEqual(matrix, position.matrix);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof DisplayPosition;
  }

  /** Modifies this object's state to match that of the given object. */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    DisplayPosition position = (DisplayPosition) dyn;

    if (position != null) {
      name = position.name;
      matrix = position.matrix;
    }
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- New API methods --

  /** Gets the position's string representation (name). */
  public String toString() { return name; }

  /** Gets the positions's name. */
  public String getName() { return name; }

  /** Gets the position's description. */
  public double[] getMatrix() { return matrix; }

}
