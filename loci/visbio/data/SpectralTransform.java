//
// SpectralTransform.java
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

import loci.visbio.util.ObjectUtil;

import visad.*;

/**
 * SpectralTransform is a mapping of spectral bands onto an RGB or grayscale
 * color space, as specified by the user with weighted sliders.
 */
public class SpectralTransform extends DataTransform
  implements ImageTransform, TransformListener
{

  // -- Fields --

  /** Output range components. */
  protected RealType[] range;

  /** Spectral channel weights. */
  protected double[][] weights;

  /** Controls for this spectral mapping. */
  protected SpectralWidget controls;


  // -- Constructors --

  /** Creates an uninitialized spectral mapping. */
  public SpectralTransform() { super(); }

  /** Creates a spectral mapping from the given transform. */
  public SpectralTransform(DataTransform parent, String name, int numRange) {
    super(parent, name);

    // weights default to evenly distributed 1s
    ImageTransform it = (ImageTransform) parent;
    int in = it.getRangeCount();
    weights = new double[numRange][in];

    int div = in / numRange;
    int rem = in % numRange;
    int count = 0;
    for (int i=0; i<numRange; i++) {
      int num = i < rem ? div + 1 : div;
      for (int j=count; j<count+num; j++) weights[i][j] = 1.0;
      count += num;
    }

    initState(null);
    parent.addTransformListener(this);
  }


  // -- SpectralTransform API methods --

  /** Assigns the parameters for this spectral mapping. */
  public void setParameters(double[][] weights) {
    if (weights.length != range.length) return;

    this.weights = new double[weights.length][];
    for (int i=0; i<weights.length; i++) {
      int len = weights[i].length;
      this.weights[i] = new double[len];
      System.arraycopy(weights[i], 0, this.weights[i], 0, len);
    }

    // signal parameter change to listeners
    notifyListeners(new TransformEvent(this));
  }

  /** Gets spectral channel weights. */
  public double[][] getWeights() { return weights; }


  // -- Static DataTransform API methods --

  /** Creates a new spectral mapping, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform data = dm.getSelectedData();
    if (!isValidParent(data)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getVisBio(),
      "Mapping name:", "Create spectral mapping",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      data.getName() + " mapping");
    if (n == null) return null;

    // guess at some reasonable defaults
    int rangeCount = 3;

    return new SpectralTransform(data, n, rangeCount);
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


  // -- DataTransform API methods --

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   *
   * @return null if the transform does not provide data of that dimensionality
   */
  public Data getData(int[] pos, int dim) {
    if (dim != 2) return null;

    Data data = parent.getData(pos, dim);
    if (data == null || !(data instanceof FlatField)) return null;

    return doWeightedMapping((FlatField) data, range, weights);
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
    StringBuffer sb = new StringBuffer(parent.getCacheId(pos, global));
    sb.append("{");
    appendArray(sb, "w0", weights[0], true);
    appendArray(sb, "w1", weights[1], true);
    appendArray(sb, "w2", weights[2], false);
    sb.append("}");
    return sb.toString();
  }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }


  // -- DataTransform API methods - state logic --

  /** Writes the current state to the given OME-CA XML object. */
  public void saveState(OMEElement ome, int id, Vector list) {
    super.saveState(ome, id, list);

    CAElement custom = ome.getCustomAttr();
    custom.setAttribute("numWeights", "" + weights.length);
    for (int i=0; i<weights.length; i++) {
      custom.setAttribute("weights" + i, ObjectUtil.arrayToString(weights[i]));
    }
  }

  /** Restores the current state from the given OME-CA XML object. */
  public int restoreState(OMEElement ome, int id, Vector list) {
    int index = super.restoreState(ome, id, list);
    if (index < 0) return index;
    CAElement custom = ome.getCustomAttr();

    int numRange = Integer.parseInt(
      custom.getAttributes(DATA_TRANSFORM, "numWeights")[index]);
    weights = new double[numRange][];
    for (int i=0; i<numRange; i++) {
      weights[i] = ObjectUtil.stringToDoubleArray(
        custom.getAttributes(DATA_TRANSFORM, "weights" + i)[index]);
    }

    return index;
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    SpectralTransform data = (SpectralTransform) dyn;

    return ObjectUtil.arraysEqual(weights, data.weights);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof SpectralTransform;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    SpectralTransform data = (SpectralTransform) dyn;

    if (data != null) {
      weights = new double[data.weights.length][];
      for (int i=0; i<weights.length; i++) {
        weights[i] = ObjectUtil.copy(data.weights[i]);
      }
    }

    lengths = parent.getLengths();
    dims = parent.getDimTypes();
    makeLabels();

    int numRange = weights.length;
    String[] rangeLabels = new String[numRange];
    range = new RealType[numRange];
    for (int i=0; i<numRange; i++) {
      rangeLabels[i] = "value" + (i + 1);
      range[i] = RealType.getRealType(rangeLabels[i]);
    }

    controls = new SpectralWidget(this, rangeLabels);
    thumbs = new ThumbnailHandler(this, getCacheFilename());
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


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

  /** Gets type associated with image X component. */
  public RealType getXType() { return ((ImageTransform) parent).getXType(); }

  /** Gets type associated with image Y component. */
  public RealType getYType() { return ((ImageTransform) parent).getYType(); }

  /** Gets types associated with image range components. */
  public RealType[] getRangeTypes() {
    RealType[] rt = new RealType[range.length];
    System.arraycopy(range, 0, rt, 0, range.length);
    return rt;
  }


  // -- TransformListener API methods --

  /** Called when parent data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    // CTR TODO
    // depending on how parent changed, recompute weights array size
    // if it changes size, need to redo spectral widget layout
    // but always need to notify listeners of spectral transform change
    notifyListeners(new TransformEvent(this));
  }


  // -- Utility methods --

  /**
   * Applies a spectral mapping to the given field based on the
   * given weights and range types.
   */
  public static FlatField doWeightedMapping(FlatField field,
    RealType[] types, double[][] weights)
  {
    if (types.length != weights.length) return null;

    try {
      float[][] samples = field.getFloats(false);
      int count = samples[0].length;

      float[][] nsamps = new float[types.length][count];
      for (int r=0; r<types.length; r++) {
        int wlen = weights[r].length;
        for (int c=0; c<count; c++) {
          float sum = 0;
          for (int w=0; w<wlen; w++) sum += weights[r][w] * samples[w][c];
          nsamps[r][c] = sum / wlen;
        }
      }

      RealTupleType domain = ((FunctionType) field.getType()).getDomain();
      FunctionType ftype = new FunctionType(domain, new RealTupleType(types));

      FlatField ff = new FlatField(ftype, field.getDomainSet());
      ff.setSamples(nsamps, false);
      return ff;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }


  // -- Helper methods --

  /** Chooses filename for thumbnail cache based on parent cache's name. */
  private String getCacheFilename() {
    ThumbnailCache cache = null;
    ThumbnailHandler th = parent.getThumbHandler();
    if (th != null) cache = th.getCache();
    if (cache == null) return "spectral_mapping.visbio";
    else {
      String s = cache.getCacheFile().getAbsolutePath();
      int dot = s.lastIndexOf(".");
      String suffix;
      if (dot < 0) suffix = "";
      else {
        suffix = s.substring(dot);
        s = s.substring(0, dot);
      }
      return s + "_mapping" + suffix;
    }
  }

  /** Appends the given array to the specified string buffer. */
  private void appendArray(StringBuffer sb,
    String name, double[] array, boolean semicolon)
  {
    sb.append(name);
    sb.append("=[");
    for (int i=0; i<array.length; i++) {
      if (i > 0) sb.append(",");
      sb.append(array[i]);
    }
    sb.append("]");
    if (semicolon) sb.append(";");
  }

}
