//
// ArbitrarySlice.java
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

package loci.visbio.data;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.state.Dynamic;
import visad.Data;
import visad.ScalarMap;

/** A transform for slicing a stack of images in 3D. */
public class ArbitrarySlice extends DataTransform
  implements TransformListener
{

  // -- Fields --

  /** Controls for the arbitrary slice. */
  protected SliceWidget controls;


  // -- Constructor --

  /** Creates an overlay object for the given transform. */
  public ArbitrarySlice(DataTransform parent, String name) {
    super(parent, name);
    initState(null);
    parent.addTransformListener(this);
  }


  // -- ArbitrarySlice API methods --

  // CTR TODO add API methods


  // -- Static DataTransform API methods --

  /** Creates a new set of overlays, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform dt = dm.getSelectedData();
    if (!isValidParent(dt)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getControlPanel(),
      "Title of slice:", "Create arbitrary slice",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      dt.getName() + " slice");
    if (n == null) return null;
    return new ArbitrarySlice(dt, n);
  }

  /**
   * Indicates whether this transform type would accept
   * the given transform as its parent transform.
   */
  public static boolean isValidParent(DataTransform data) {
    return data != null && data instanceof ImageTransform &&
      data.getLengths().length > 0;
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
    if (dim != 3) return null;
    // CTR TODO getData
    return null;
  }

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) { return dim == 2; }

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public ScalarMap[] getSuggestedMaps() {
    // CTR TODO
    return null;
  }

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public String getCacheId(int[] pos, boolean global) { return null; }

  /**
   * Arbitrary slices are rendered immediately,
   * due to frequent user interaction.
   */
  public boolean isImmediate() { return true; }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    ArbitrarySlice data = (ArbitrarySlice) dyn;

    // CTR TODO return true iff data matches this object
    return false;
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof ArbitrarySlice;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    ArbitrarySlice data = (ArbitrarySlice) dyn;

    if (data != null) {
      // CTR TODO synchronize data with this object
    }

    lengths = parent.getLengths();
    dims = parent.getDimTypes();
    makeLabels();

    controls = new SliceWidget(this);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- TransformListener API methods --

  /** Called when parent data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) {
      initState(null);
      notifyListeners(new TransformEvent(this));
    }
  }

}
