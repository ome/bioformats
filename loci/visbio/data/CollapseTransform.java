//
// CollapseTransform.java
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

import java.rmi.RemoteException;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.ome.xml.CAElement;
import loci.ome.xml.OMEElement;
import loci.visbio.state.Dynamic;
import visad.*;

/**
 * CollapseTransform converts a dimensional axis
 * into a series of range components.
 */
public class CollapseTransform extends ImageTransform {

  // -- Fields --

  /** Dimensional axis to collapse. */
  protected int axis;

  /** Range display mappings. */
  protected RealType[] range;

  /** Controls for this dimensional collapse. */
  protected CollapseWidget controls;


  // -- Constructors --

  /** Creates an uninitialized dimensional collapse. */
  public CollapseTransform() { super(); }

  /** Creates a dimensional collapse from the given transform. */
  public CollapseTransform(DataTransform parent, String name, int axis) {
    super(parent, name);
    this.axis = axis;
    initState(null);
  }


  // -- CollapseTransform API methods --

  /** Assigns the parameters for this dimensional collapse. */
  public void setParameters(int axis) {
    if (axis < 0 || axis >= lengths.length) return;
    this.axis = axis;
    computeLengths();

    // signal parameter change to listeners
    notifyListeners(new TransformEvent(this));
  }


  // -- ImageTransform API methods --

  /** Gets width of each image. */
  public int getImageWidth() {
    return ((ImageTransform) parent).getImageWidth();
  }

  /** Gets height of each image. */
  public int getImageHeight() {
    return ((ImageTransform) parent).getImageHeight();
  }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return range.length; }


  // -- Static DataTransform API methods --

  /** Creates a new dimensional collapse, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform data = dm.getSelectedData();
    if (!isValidParent(data)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getVisBio(),
      "Collapse name:", "Create dimensional collapse",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      data.getName() + " collapse");
    if (n == null) return null;

    // guess at some reasonable defaults
    int collapseAxis = 0;

    return new CollapseTransform(data, n, collapseAxis);
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
    if (dim != 2) return null;

    int len = parent.getLengths()[axis];
    FlatField[] fields = new FlatField[len];
    int[] npos = getParentPos(pos);
    for (int i=0; i<len; i++) {
      npos[axis] = i;
      Data data = parent.getData(npos, dim);
      if (data == null || !(data instanceof FlatField)) return null;
      fields[i] = (FlatField) data;
    }
    return collapse(fields, range);
  }

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) {
    return dim == 2 && parent.isValidDimension(dim);
  }

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public ScalarMap[] getSuggestedMaps() {
    ScalarMap[] pmaps = parent.getSuggestedMaps();
    Vector v = new Vector();

    // strip off parent range component mappings
    for (int i=0; i<pmaps.length; i++) {
      if (!pmaps[i].getDisplayScalar().equals(Display.RGBA)) v.add(pmaps[i]);
    }

    // append new range component mappings
    try {
      for (int i=0; i<range.length; i++) {
        v.add(new ScalarMap(range[i], Display.RGBA));
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    ScalarMap[] nmaps = new ScalarMap[v.size()];
    v.copyInto(nmaps);
    return nmaps;
  }

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public String getCacheId(int[] pos, boolean global) {
    int[] npos = getParentPos(pos);
    StringBuffer sb = new StringBuffer(parent.getCacheId(npos, global));
    sb.append("{");
    if (global) sb.append("collapse=");
    sb.append(axis);
    sb.append("}");
    return sb.toString();
  }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }


  // -- DataTransform API methods - state logic --

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome, int id, Vector list) {
    super.saveState(ome, id, list);

    CAElement custom = ome.getCustomAttr();
    custom.setAttribute("collapseAxis", "" + axis);
  }

  /** Restores the current state from the given XML object. */
  public int restoreState(OMEElement ome, int id, Vector list) {
    int index = super.restoreState(ome, id, list);
    if (index < 0) return index;
    CAElement custom = ome.getCustomAttr();

    axis = Integer.parseInt(
      custom.getAttributes(DATA_TRANSFORM, "collapseAxis")[index]);

    return index;
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    CollapseTransform data = (CollapseTransform) dyn;

    return axis == data.axis;
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof CollapseTransform;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    CollapseTransform data = (CollapseTransform) dyn;

    if (data != null) {
      axis = data.axis;
    }

    computeLengths();

    controls = new CollapseWidget(this);
    thumbs = new ThumbnailHandler(this, getCacheFilename());
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- Utility methods --

  /** Collapses the given fields. */
  public static FlatField collapse(FlatField[] fields, RealType[] types) {
    if (fields == null || types == null || fields.length == 0) return null;

    FlatField ff = null;
    try {
      FunctionType ftype = (FunctionType) fields[0].getType();
      RealTupleType domain = ftype.getDomain();
      MathType rtype = types.length == 1 ?
        (MathType) types[0] : (MathType) new RealTupleType(types);
      ff = new FlatField(new FunctionType(domain, rtype),
        fields[0].getDomainSet());

      float[][] samples = new float[types.length][];
      int ndx = 0;

      for (int i=0; i<fields.length; i++) {
        float[][] samps = fields[i].getFloats(false);
        if (samps.length + ndx > types.length) return null;
        for (int j=0; j<samps.length; j++) samples[ndx++] = samps[j];
      }
      if (ndx != types.length) return null;

      ff.setSamples(samples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return ff;
  }


  // -- Helper methods --

  /** Chooses filename for thumbnail cache based on parent cache's name. */
  private String getCacheFilename() {
    ThumbnailCache cache = null;
    ThumbnailHandler th = parent.getThumbHandler();
    if (th != null) cache = th.getCache();
    if (cache == null) return "dimensional_collapse.visbio";
    else {
      String s = cache.getCacheFile().getAbsolutePath();
      int dot = s.lastIndexOf(".");
      String suffix;
      if (dot < 0) suffix = "";
      else {
        suffix = s.substring(dot);
        s = s.substring(0, dot);
      }
      return s + "_collapse" + suffix;
    }
  }

  /**
   * Computes lengths, dims and range arrays
   * based on dimensional axis to be collapsed.
   */
  private void computeLengths() {
    int[] plens = parent.getLengths();
    lengths = new int[plens.length - 1];
    System.arraycopy(plens, 0, lengths, 0, axis);
    System.arraycopy(plens, axis + 1, lengths, axis, lengths.length - axis);

    String[] pdims = parent.getDimTypes();
    dims = new String[pdims.length - 1];
    System.arraycopy(pdims, 0, dims, 0, axis);
    System.arraycopy(pdims, axis + 1, dims, axis, dims.length - axis);

    makeLabels();

    int len = plens[axis];
    char letter = pdims[axis].equals("Slice") ? 'Z' : pdims[axis].charAt(0);
    RealType[] prange = ((ImageTransform) parent).getRangeTypes();
    range = new RealType[prange.length * len];
    for (int i=0; i<prange.length; i++) {
      String rname = prange[i].getName();
      for (int j=0; j<len; j++) {
        int ndx = j * prange.length + i;
        range[ndx] = RealType.getRealType(rname + "_" + letter + (j + 1));
      }
    }
  }

  /** Gets dimensional position for parent transform. */
  private int[] getParentPos(int[] pos) {
    int[] npos = new int[pos.length + 1];
    System.arraycopy(pos, 0, npos, 0, axis);
    System.arraycopy(pos, axis, npos, axis + 1, pos.length - axis);
    return npos;
  }

}
