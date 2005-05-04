//
// DataSampling.java
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
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.ObjectUtil;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;
import visad.*;

/** DataSampling is a resampling of another transform. */
public class DataSampling extends ImageTransform {

  // -- Fields --

  /** Minimum index value for each dimension. */
  protected int[] min;

  /** Maximum index value for each dimension. */
  protected int[] max;

  /** Index step value for each dimension. */
  protected int[] step;

  /** Width of each image. */
  protected int resX;

  /** Height of each image. */
  protected int resY;

  /** List of included range components. */
  protected boolean[] range;

  /** Range component count for each image. */
  protected int numRange;

  /** Controls for this data sampling. */
  protected SamplingWidget controls;


  // -- Constructors --

  /** Creates an uninitialized data sampling. */
  public DataSampling() { super(); }

  /** Creates a data sampling from the given transform. */
  public DataSampling(DataTransform parent, String name, int[] min,
    int[] max, int[] step, int resX, int resY, boolean[] range)
  {
    super(parent, name);
    this.min = min;
    this.max = max;
    this.step = step;
    this.resX = resX;
    this.resY = resY;
    this.range = range;
    initState(null);
  }


  // -- DataSampling API methods --

  /** Assigns the parameters for this data sampling. */
  public void setParameters(int[] min, int[] max, int[] step,
    int resX, int resY, boolean[] range)
  {
    this.min = min;
    this.max = max;
    this.step = step;
    this.resX = resX;
    this.resY = resY;
    this.range = range;

    // recompute range counter
    numRange = 0;
    for (int i=0; i<range.length; i++) if (range[i]) numRange++;

    // recompute lengths
    for (int i=0; i<min.length; i++) {
      lengths[i] = (max[i] - min[i]) / step[i] + 1;
    }
    makeLabels();

    // signal parameter change to listeners
    notifyListeners(new TransformEvent(this));
  }

  /** Gets minimum dimensional index values. */
  public int[] getMin() { return min; }

  /** Gets maximum dimensional index values. */
  public int[] getMax() { return max; }

  /** Gets dimensional index step values. */
  public int[] getStep() { return step; }

  /** Gets included range components. */
  public boolean[] getRange() { return range; }


  // -- ImageTransform API methods --

  /** Gets width of each image. */
  public int getImageWidth() { return resX; }

  /** Gets height of each image. */
  public int getImageHeight() { return resY; }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return numRange; }


  // -- Static DataTransform API methods --

  /** Creates a new data sampling, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform data = dm.getSelectedData();
    if (!isValidParent(data)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getControlPanel(),
      "Sampling name:", "Create sampling", JOptionPane.INFORMATION_MESSAGE,
      null, null, data.getName() + " sampling");
    if (n == null) return null;

    // guess at some reasonable defaults
    int[] len = data.getLengths();
    int[] lo = new int[len.length];
    int[] hi = new int[len.length];
    int[] inc = new int[len.length];
    for (int i=0; i<len.length; i++) {
      lo[i] = 1;
      hi[i] = len[i];
      inc[i] = 1;
    }
    ImageTransform it = (ImageTransform) data;
    int w = it.getImageWidth();
    int h = it.getImageHeight();
    boolean[] rng = new boolean[it.getRangeCount()];
    for (int i=0; i<rng.length; i++) rng[i] = true;

    return new DataSampling(data, n, lo, hi, inc, w, h, rng);
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
  public Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 2) return null;

    int[] p = new int[pos.length];
    for (int i=0; i<p.length; i++) p[i] = min[i] + step[i] * pos[i] - 1;
    Data data = parent.getData(p, dim, cache);
    if (data == null || !(data instanceof FlatField)) return null;

    int[] res = resX > 0 && resY > 0 ? new int[] {resX, resY} : null;
    return resample((FlatField) data, res, range, true);
  }

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) {
    return dim == 2 && parent.isValidDimension(dim);
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
    appendArray(sb, "min", min, true);
    appendArray(sb, "max", max, true);
    appendArray(sb, "step", step, true);
    sb.append("x=" + resX + ";");
    sb.append("y=" + resY + ";");
    int[] r = new int[range.length];
    for (int i=0; i<r.length; i++) r[i] = range[i] ? 1 : 0;
    appendArray(sb, "range", r, false);
    sb.append("}");
    return sb.toString();
  }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }


  // -- Internal DataTransform API methods --

  /** Creates labels based on data transform parameters. */
  protected void makeLabels() {
    labels = new String[lengths.length][];
    for (int i=0; i<lengths.length; i++) {
      labels[i] = new String[lengths[i]];
      for (int j=0; j<lengths[i]; j++) {
        labels[i][j] = "" + (min[i] + j * step[i]);
      }
    }
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    DataSampling data = (DataSampling) dyn;

    return ObjectUtil.arraysEqual(min, data.min) &&
      ObjectUtil.arraysEqual(max, data.max) &&
      ObjectUtil.arraysEqual(step, data.step) &&
      resX == data.resX && resY == data.resY &&
      ObjectUtil.arraysEqual(range, data.range);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof DataSampling;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    DataSampling data = (DataSampling) dyn;

    if (data != null) {
      min = data.min;
      max = data.max;
      step = data.step;
      resX = data.resX;
      resY = data.resY;
      range = data.range;
    }

    lengths = new int[min.length];
    dims = (String[]) ObjectUtil.copy(parent.dims);

    setParameters(min, max, step, resX, resY, range);

    controls = new SamplingWidget(this);
    thumbs = makeThumbnailHandler();
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("DataTransforms"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "DataSampling");
    super.saveState(child);
    child.setAttribute("min", ObjectUtil.arrayToString(min));
    child.setAttribute("max", ObjectUtil.arrayToString(max));
    child.setAttribute("step", ObjectUtil.arrayToString(step));
    child.setAttribute("resX", "" + resX);
    child.setAttribute("resY", "" + resY);
    child.setAttribute("range", ObjectUtil.arrayToString(range));
  }

  /**
   * Restores the current state from the given DOM element ("DataSampling").
   */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    min = ObjectUtil.stringToIntArray(el.getAttribute("min"));
    max = ObjectUtil.stringToIntArray(el.getAttribute("max"));
    step = ObjectUtil.stringToIntArray(el.getAttribute("step"));
    resX = Integer.parseInt(el.getAttribute("resX"));
    resY = Integer.parseInt(el.getAttribute("resY"));
    range = ObjectUtil.stringToBooleanArray(el.getAttribute("range"));
  }


  // -- Utility methods --

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * leaving the domain set's low and high values untouched.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range) {
    return resample(f, res, range, false);
  }

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * forcing the result onto an IntegerSet of appropriate length if the
   * relevant flag is set.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    boolean forceIntegerSet)
  {
    int[] min = new int[res.length], max = new int[res.length];
    if (forceIntegerSet) {
      // use min and max appropriate for IntegerSet
      for (int i=0; i<res.length; i++) {
        min[i] = 0;
        max[i] = res[i] - 1;
      }
    }
    else {
      // compute min and max from original set
      GriddedSet set = (GriddedSet) f.getDomainSet();
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      for (int i=0; i<res.length; i++) {
        min[i] = (int) lo[i];
        max[i] = (int) hi[i];
      }
    }
    return resample(f, res, range, min, max);
  }

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * using the given minimum and maximum sampling values for each dimension.
   *
   * If min and max have appropriate values (min is all zeroes and max equals
   * len-1 across all dimensions), an IntegerSet is used, otherwise a LinearSet
   * is constructed.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    int[] min, int[] max)
  {
    try {
      GriddedSet set = (GriddedSet) f.getDomainSet();
      float[][] samples = f.getFloats(false);
      FunctionType function = (FunctionType) f.getType();
      MathType frange = function.getRange();
      RealType[] rt = frange instanceof RealTupleType ?
        ((RealTupleType) frange).getRealComponents() :
        new RealType[] {(RealType) frange};

      // count number of range components to keep
      int keep = 0;
      if (range != null) {
        for (int i=0; i<range.length; i++) if (range[i]) keep++;
      }

      // strip out unwanted range components
      FlatField ff;
      if (range == null || keep == range.length) ff = f;
      else {
        float[][] samps = new float[keep][];
        RealType[] reals = new RealType[keep];
        int count = 0;
        for (int i=0; i<range.length; i++) {
          if (range[i]) {
            samps[count] = samples[i];
            reals[count] = rt[i];
            count++;
          }
        }
        MathType funcRange = count == 1 ?
          (MathType) reals[0] : (MathType) new RealTupleType(reals);
        FunctionType func = new FunctionType(function.getDomain(), funcRange);
        ff = new FlatField(func, set);
        ff.setSamples(samps, false);
      }

      // determine whether original resolution matches desired resolution
      int[] len = set.getLengths();
      boolean same = true;
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      double[] nlo = new double[len.length];
      double[] nhi = new double[len.length];
      for (int i=0; i<len.length; i++) {
        if (res != null && len[i] != res[i]) same = false;
        nlo[i] = (double) lo[i];
        nhi[i] = (double) hi[i];
      }
      if (!same) {
        // resample field to proper resolution
        ff = (FlatField) ff.resample(new LinearNDSet(set.getType(),
          nlo, nhi, res), Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
      }

      // determine whether original low and high values match desired ones
      boolean canUseInteger = true;
      same = true;
      for (int i=0; i<len.length; i++) {
        if (min != null) {
          if (nlo[i] != min[i]) same = false;
          if (min[i] != 0) canUseInteger = false;
          nlo[i] = min[i];
        }
        if (max != null) {
          if (nhi[i] != max[i]) same = false;
          if (min[i] != res[i] - 1) canUseInteger = false;
          nhi[i] = max[i];
        }
      }

      if (canUseInteger || !same) {
        // adjust field set appropriately
        FunctionType ffType = (FunctionType) ff.getType();
        Set ffSet = canUseInteger ?
          new IntegerNDSet(ffType.getDomain(), res) :
          new LinearNDSet(ffType.getDomain(), nlo, nhi, res);
        float[][] ffSamples = ff.getFloats(false);
        ff = new FlatField(ffType, ffSet);
        ff.setSamples(ffSamples);
      }
      return ff;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }


  // -- Helper methods --

  /** Creates custom handler for data sampling's thumbnails. */
  private ThumbnailHandler makeThumbnailHandler() {
    return new SamplingThumbHandler(this, getCacheFilename());
  }

  /** Chooses filename for thumbnail cache based on parent cache's name. */
  private String getCacheFilename() {
    ThumbnailCache cache = null;
    ThumbnailHandler th = parent.getThumbHandler();
    if (th != null) cache = th.getCache();
    if (cache == null) return "data_sampling.visbio";
    else {
      String s = cache.getCacheFile().getAbsolutePath();
      int dot = s.lastIndexOf(".");
      String suffix;
      if (dot < 0) suffix = "";
      else {
        suffix = s.substring(dot);
        s = s.substring(0, dot);
      }
      return s + "_sampling" + suffix;
    }
  }

  /** Appends the given array to the specified string buffer. */
  private void appendArray(StringBuffer sb,
    String name, int[] array, boolean semicolon)
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
