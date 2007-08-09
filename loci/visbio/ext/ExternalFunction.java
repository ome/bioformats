//
// ExternalFunction.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

This function is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This function is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this function; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.ext;

import javax.swing.JComponent;
import loci.visbio.data.*;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.XMLUtil;
import loci.visbio.view.TransformLink;
import org.w3c.dom.Element;
import visad.*;

/**
 * ExternalFunction is an abstract transform that calls some form of external
 * function. Examples include system calls to an external program using stdin
 * and stdout for binary data transfer, or calls to a MATLAB function.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/ext/ExternalFunction.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/ext/ExternalFunction.java">SVN</a></dd></dl>
 */
public abstract class ExternalFunction extends ImageTransform {

  // -- Fields --

  /** The name of the external function to execute. */
  protected String function;

  /** List of parameters and associated default values. */
  protected FunctionParam[] paramList;

  /** Parameter values for passing to the function. */
  protected String[] params;

  /** Range component count for each image. */
  protected int numRange;

  /** Width of each image. */
  protected int resX;

  /** Height of each image. */
  protected int resY;

  /** Controls for this external function transform. */
  protected FunctionWidget controls;

  // -- Constructors --

  /** Creates an uninitialized external function transform. */
  public ExternalFunction() { super(); }

  /** Creates an external function transform from the given transform. */
  public ExternalFunction(DataTransform parent, String name, String function) {
    super(parent, name);
    this.function = function;
    initState(null);
  }

  // -- ExternalFunction API methods --

  /**
   * Predicts the width, height and number of output planes, given the width,
   * height and number of input planes, and parameter values.
   *
   * @return An int[3] array representing output width, height and num values.
   */
  public abstract int[] predict(int width,
    int height, int num, String[] params);

  /** Gets the input parameter names and corresponding default values. */
  public abstract FunctionParam[] params();

  /** Evaluates the function for the given input data and parameter values. */
  public abstract FlatField evaluate(FlatField input, String[] params);

  /** Assigns the parameters for this external function transform. */
  public void setParameters(String[] params) { setParameters(params, true); }

  /** Assigns the parameters for this external function transform. */
  protected void setParameters(String[] params, boolean notify) {
    this.params = params;

    // get output dimensions for these parameters
    ImageTransform it = (ImageTransform) parent;
    int w = it.getImageWidth();
    int h = it.getImageHeight();
    int n = it.getRangeCount();
    int[] outDims = predict(w, h, n, params);
    resX = outDims[0];
    resY = outDims[1];
    numRange = outDims[2];

    // signal parameter change to listeners
    if (notify) notifyListeners(new TransformEvent(this));
  }

  /** Gets the name of each parameter for this external function transform.*/
  public String[] getParameterNames() {
    String[] s = new String[paramList.length];
    for (int i=0; i<s.length; i++) s[i] = paramList[i].getName();
    return s;
  }

  /**
   * Gets the default parameter values
   * for this external function transform.
   */
  public String[] getParameterDefaults() {
    String[] d = new String[paramList.length];
    for (int i=0; i<d.length; i++) d[i] = paramList[i].getValue();
    return d;
  }

  /**
   * Gets the current parameter values
   * for this external function transform.
   */
  public String[] getParameters() { return params; }

  // -- ImageTransform API methods --

  /** Gets width of each image. */
  public int getImageWidth() { return resX; }

  /** Gets height of each image. */
  public int getImageHeight() { return resY; }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return numRange; }

  // -- DataTransform API methods --

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   *
   * @return null if the transform does not provide data of that dimensionality
   */
  public Data getData(TransformLink link, int[] pos, int dim, DataCache cache) {
    if (dim != 2) return null;

    Data data = parent.getData(link, pos, dim, cache);
    if (!(data instanceof FlatField)) return null;

    return evaluate((FlatField) data, params);
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
    if (global) sb.append("function=");
    sb.append(function);
    sb.append("}");
    return sb.toString();
  }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }

  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    ExternalFunction data = (ExternalFunction) dyn;

    return function.equals(data.function);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof ExternalFunction;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    ExternalFunction data = (ExternalFunction) dyn;

    if (data != null) {
      function = data.function;
    }

    lengths = parent.getLengths();
    dims = parent.getDimTypes();
    makeLabels();

    // determine parameters
    paramList = params();
    String[] prms = new String[paramList.length];
    for (int i=0; i<prms.length; i++) {
      prms[i] = paramList[i].getValue();
    }
    setParameters(prms, false);

    controls = new FunctionWidget(this);
    thumbs = new ThumbnailHandler(this, getCacheFilename());
  }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("DataTransforms"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "ExternalFunction");
    super.saveState(child);
    child.setAttribute("function", function);
  }

  /**
   * Restores the current state from the given DOM element
   * ("ExternalFunction").
   */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    function = el.getAttribute("function");
  }

  // -- Helper methods --

  /** Chooses filename for thumbnail cache based on parent cache's name. */
  private String getCacheFilename() {
    ThumbnailCache cache = null;
    ThumbnailHandler th = parent.getThumbHandler();
    if (th != null) cache = th.getCache();
    if (cache == null) return "external_function.visbio";
    else {
      String s = cache.getCacheFile().getAbsolutePath();
      int dot = s.lastIndexOf(".");
      String suffix;
      if (dot < 0) suffix = "";
      else {
        suffix = s.substring(dot);
        s = s.substring(0, dot);
      }
      return s + "_function" + suffix;
    }
  }

}
