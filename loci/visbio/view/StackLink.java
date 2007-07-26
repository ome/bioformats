//
// StackLink.java
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

package loci.visbio.view;

import java.util.Vector;
import java.rmi.RemoteException;
import loci.visbio.data.*;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;
import visad.util.CursorUtil;

/**
 * Represents a link between a data transform and a display
 * that produces an image stack.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/view/StackLink.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/view/StackLink.java">SVN</a></dd></dl>
 */
public class StackLink extends TransformLink {

  // -- Constants --

  /** Dummy data to avoid "Data is null" message for volume reference. */
  protected static final Data DUMMY = new Real(0);

  // -- Fields --

  /** Data references linking data to the display. */
  protected Vector references;

  /** Data renderers for toggling data's visibility and other parameters. */
  protected Vector renderers;

  /** Dimensional axis to use for image stacks. */
  protected int stackAxis;

  /**
   * Flag indicating whether stackAxis is valid. If it is not, it will be
   * autodetected when initState is called. Also, if it is valid, then
   * setStackAxis will not rebuild display references unless the new axis value
   * is different from the old one.
   */
  protected boolean axisValid;

  /** Last known dimensional position of the link. */
  protected int[] lastPos;

  /** Data reference for volume rendered cube. */
  protected DataReferenceImpl volumeRef;

  /** Whether volume rendering is currently enabled. */
  protected boolean volume;

  /** Resolution of rendered volumes. */
  protected int volumeRes = StackHandler.DEFAULT_VOLUME_RESOLUTION;

  // -- Fields - initial state --

  /** Data transform's current slice. */
  protected int currentSlice;

  /**
   * Flags indicating visibility of each slice of the stack.
   * Used for both initial state and temporary storage while volume rendering.
   */
  protected boolean[] visSlices;

  // -- Constructor --

  /** Constructs an uninitialized stack link. */
  public StackLink(StackHandler h) { super(h); }

  /**
   * Creates a link between the given data transform and
   * the specified stack handler's display.
   */
  public StackLink(StackHandler h, DataTransform t) { super(h, t); }

  // -- StackLink API methods --

  /** Gets references corresponding to each plane in the image stack. */
  public DataReferenceImpl[] getReferences() {
    DataReferenceImpl[] refs = new DataReferenceImpl[references.size()];
    references.copyInto(refs);
    return refs;
  }

  /** Gets renderers corresponding to each plane in the image stack. */
  public DataRenderer[] getRenderers() {
    DataRenderer[] rends = new DataRenderer[renderers.size()];
    renderers.copyInto(rends);
    return rends;
  }

  /** Assigns the given axis as the stack axis. */
  public void setStackAxis(int axis) {
    if (axisValid && axis == stackAxis) return; // no change
    stackAxis = axis;
    references.removeAllElements();
    renderers.removeAllElements();
    lastPos = null;

    // convert slider axis to stack axis for this transform
    int[] lengths = trans.getLengths();
    int len = axis >= 0 && axis < lengths.length ? lengths[axis] : 1;

    // build reference/renderer pairs
    String name = trans.getName();
    DisplayRenderer dr = handler.getWindow().getDisplay().getDisplayRenderer();
    try {
      for (int i=0; i<len; i++) {
        references.add(new DataReferenceImpl(name + i));
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    axisValid = true;
  }

  /** Gets the currently assigned stack axis. */
  public int getStackAxis() { return stackAxis; }

  /** Gets the number of slices, assuming the currently assigned stack axis. */
  public int getSliceCount() { return references.size(); }

  /**
   * Gets the current slice index, assuming
   * the currently assigned stack axis.
   */
  public int getCurrentSlice() {
    if (handler == null) return currentSlice;
    int[] pos = handler.getPos(trans);
    return stackAxis >= 0 && stackAxis < pos.length ? pos[stackAxis] : -1;
  }

  /** Enables or disables visibility at the specified slice index. */
  public void setSliceVisible(int slice, boolean vis) {
    if (slice < 0 || slice >= references.size()) return;
    ((DataRenderer) renderers.elementAt(slice)).toggle(vis);
  }

  /** Gets visibility at the specified slice index. */
  public boolean isSliceVisible(int slice) {
    if (renderers == null) {
      return visSlices != null && slice >= 0 && slice < visSlices.length ?
        visSlices[slice] : false;
    }
    if (slice < 0 || slice >= renderers.size()) return false;
    return ((DataRenderer) renderers.elementAt(slice)).getEnabled();
  }

  /** Enables or disables visibility of the yellow bounding box. */
  public void setBoundingBoxVisible(boolean vis) { super.setVisible(vis); }

  /** Gets visibility of the given data transform's yellow bounding box. */
  public boolean isBoundingBoxVisible() { return super.isVisible(); }

  /** Enables or disables volume rendering. */
  public void setVolumeRendered(boolean volume) {
    if (this.volume == volume) return;
    this.volume = volume;
    if (volume) {
      // save slice visibility settings and hide slice data
      int len = renderers.size();
      visSlices = new boolean[len];
      for (int s=0; s<len; s++) {
        DataRenderer sliceRend = (DataRenderer) renderers.elementAt(s);
        visSlices[s] = sliceRend.getEnabled();
        sliceRend.toggle(false);
      }
    }
    else {
      // restore slice visibility settings
      int len = renderers.size();
      boolean all = visSlices == null || visSlices.length != len;
      for (int s=0; s<len; s++) {
        DataRenderer sliceRend = (DataRenderer) renderers.elementAt(s);
        sliceRend.toggle(all ? true : visSlices[s]);
      }
    }
    doTransform(TransformHandler.MINIMUM_BURN_DELAY, true);
  }

  /** Gets status of volume rendering. */
  public boolean isVolumeRendered() { return volume; }

  /** Sets maximum resolution per axis of rendered volumes. */
  public void setVolumeResolution(int res) {
    if (volumeRes == res) return;
    volumeRes = res;
    if (volume) doTransform(TransformHandler.MINIMUM_BURN_DELAY, true);
  }

  /** Gets maximum resolution per axis of rendered volumes. */
  public int getVolumeResolution() { return volumeRes; }

  // -- TransformLink API methods --

  /** Links this stack into the display. */
  public void link() {
    int len = references.size();
    DisplayImpl display = handler.getWindow().getDisplay();

    // rebuild renderers if necessary
    int numMaps = colorHandler.getMaps().length;
    String imageRenderer = "visad.bom.ImageRendererJ3D";
    boolean isImageRend = renderers.size() > 0 &&
      renderers.firstElement().getClass().getName().equals(imageRenderer);
    boolean[] vis = new boolean[renderers.size()];
    for (int i=0; i<vis.length; i++) {
      vis[i] = ((DataRenderer) renderers.elementAt(i)).getEnabled();
    }
    if (numMaps == 1 && !isImageRend) {
      // use ImageRendererJ3D when possible
      renderers.removeAllElements();
      try {
        Class c = Class.forName(imageRenderer);
        for (int i=0; i<len; i++) {
          DataRenderer dr = (DataRenderer) c.newInstance();
          renderers.addElement(dr);
          if (vis.length > i) dr.toggle(vis[i]);
        }
      }
      catch (NoClassDefFoundError err) { }
      catch (ClassNotFoundException exc) { exc.printStackTrace(); }
      catch (IllegalAccessException exc) { exc.printStackTrace(); }
      catch (InstantiationException exc) { exc.printStackTrace(); }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        Throwable cause = exc.getCause();
        if (!(cause instanceof ClassNotFoundException)) throw exc;
        exc.printStackTrace();
      }
    }
    else if (numMaps > 1 && isImageRend) {
      // ImageRendererJ3D does not allow multiple mappings to Display.RGBA
      renderers.removeAllElements();
    }
    if (renderers.size() == 0) {
      for (int i=0; i<len; i++) {
        DataRenderer dr = display.getDisplayRenderer().makeDefaultRenderer();
        renderers.addElement(dr);
        if (vis.length > i) dr.toggle(vis[i]);
      }
    }
    if (rend == null) {
      rend = display.getDisplayRenderer().makeDefaultRenderer();
      rend.toggle(visible);
    }

    // link in the transform
    try {
      // add image slices
      if (stackAxis < 0) {
        display.addReferences((DataRenderer) renderers.elementAt(0),
          (DataReferenceImpl) references.elementAt(0));
      }
      else {
        for (int i=0; i<len; i++) {
          DataRenderer dataRend = (DataRenderer) renderers.elementAt(i);
          DataReference dataRef = (DataReferenceImpl) references.elementAt(i);
          display.addReferences(dataRend, dataRef);
          setZLevel(dataRend, i, len);
        }
      }

      // add yellow bounding box
      display.addReferences(rend, ref, new ConstantMap[] {
        new ConstantMap(1, Display.Red),
        new ConstantMap(1, Display.Green),
        new ConstantMap(0, Display.Blue),
        new ConstantMap(3, Display.LineWidth)
      });

      // add volume
      display.addReference(volumeRef);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Unlinks this stack from the display. */
  public void unlink() {
    try {
      // remove image slices
      int len = stackAxis < 0 ? 1 : references.size();
      DisplayImpl display = handler.getWindow().getDisplay();
      for (int i=0; i<len; i++) {
        display.removeReference((DataReferenceImpl) references.elementAt(i));
      }

      // remove yellow bounding box
      display.removeReference(ref);

      // remove volume reference
      display.removeReference(volumeRef);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Toggles visibility of the transform. */
  public void setVisible(boolean vis) {
    for (int i=0; i<renderers.size(); i++) {
      ((DataRenderer) renderers.elementAt(i)).toggle(vis);
    }
  }

  /** Gets visibility of the transform. */
  public boolean isVisible() {
    boolean vis = false;
    for (int i=0; i<renderers.size(); i++) {
      if (((DataRenderer) renderers.elementAt(i)).getEnabled()) {
        vis = true;
        break;
      }
    }
    return vis;
  }

  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    StackLink link = (StackLink) dyn;

    int num = getSliceCount();
    if (num != link.getSliceCount()) return false;
    for (int i=0; i<num; i++) {
      if (isSliceVisible(i) != link.isSliceVisible(i)) return false;
    }

    return super.matches(link) &&
      getStackAxis() == link.getStackAxis() &&
      getCurrentSlice() == link.getCurrentSlice() &&
      isVolumeRendered() == link.isVolumeRendered() &&
      getVolumeResolution() == link.getVolumeResolution();
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) { return dyn instanceof StackLink; }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);

    references = new Vector();
    renderers = new Vector();
    try { volumeRef = new DataReferenceImpl(trans.getName() + "_volume"); }
    catch (VisADException exc) { exc.printStackTrace(); }

    if (!axisValid) {
      // auto-detect stack axis
      stackAxis = -1;
      String[] dims = trans.getDimTypes();
      if (dims.length > 0) stackAxis = 0;
      for (int i=0; i<dims.length; i++) {
        if (dims[i].equals("Slice")) {
          stackAxis = i;
          break;
        }
      }
    }
    axisValid = false; // make sure display references get rebuilt
    setStackAxis(stackAxis);
  }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("LinkedData"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "StackLink");
    child.setAttribute("id", "" + trans.getTransformId());
    if (colorHandler != null) colorHandler.saveState(child);
    child.setAttribute("visible", "" + isVisible());

    // save stack parameters
    child.setAttribute("axis", "" + stackAxis);
    child.setAttribute("slice", "" + getCurrentSlice());
    int sliceCount = getSliceCount();
    StringBuffer sb = new StringBuffer(sliceCount);
    for (int i=0; i<sliceCount; i++) sb.append(isSliceVisible(i) ? "y" : "n");
    child.setAttribute("visibleSlices", sb.toString());
    child.setAttribute("isVolume", "" + isVolumeRendered());
    child.setAttribute("volumeResolution", "" + getVolumeResolution());
  }

  /** Restores the current state from the given DOM element ("StackLink"). */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);

    // restore stack parameters
    stackAxis = Integer.parseInt(el.getAttribute("axis"));
    axisValid = true; // do not detect axis if this link is later initialized
    currentSlice = Integer.parseInt(el.getAttribute("slice"));
    String s = el.getAttribute("visibleSlices");
    visSlices = new boolean[s.length()];
    for (int i=0; i<visSlices.length; i++) visSlices[i] = s.charAt(i) == 'y';
    volume = el.getAttribute("isVolume").equalsIgnoreCase("true");
    volumeRes = Integer.parseInt(el.getAttribute("volumeResolution"));
  }

  // -- Internal StackLink API methods

  /**
   * Assigns the given data object to the specified data reference,
   * switching to the proper types if the flag is set.
   */
  protected void setData(Data d, DataReference dataRef,
    DataRenderer dataRend, boolean autoSwitch, double zval)
  {
    if (autoSwitch && d instanceof FlatField &&
      trans instanceof ImageTransform)
    {
      // special case: use ImageTransform's suggested MathType instead
      FlatField ff = (FlatField) d;
      ImageTransform it = (ImageTransform) trans;
      FunctionType ftype = it.getType();
      Unit[] imageUnits = it.getImageUnits();
      try { d = DataUtil.switchType(ff, ftype, imageUnits); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    try { dataRef.setData(d); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /**
   * Assigns the given value to the specified
   * data renderer's ConstantMap to the Z axis.
   */
  protected void setZLevel(DataRenderer rend, double zval, int len) {
    zval = 2 * zval / (len - 1) - 1;
    try {
      ConstantMap zmap = new ConstantMap(zval, Display.ZAxis);
      rend.getLinks()[0].setConstantMaps(new ConstantMap[] {zmap});
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  // -- Internal TransformLink API methods --

  /** Updates the currently displayed data for the given transform. */
  protected void doTransform() {
    boolean doBox = false, doRefs = false;

    // get dimensional position of this transform
    int[] pos = handler.getPos(trans);

    // check whether dimensional position has changed
    if (lastPos == null || lastPos.length != pos.length) doBox = doRefs = true;
    else {
      for (int i=0; i<pos.length; i++) {
        if (lastPos[i] != pos[i]) {
          if (i == stackAxis) doBox = true;
          else doRefs = true;
        }
      }
    }
    lastPos = pos;

    int len = references.size();
    if (doBox) {
      // compute yellow bounding box
      ImageTransform it = (ImageTransform) trans;
      int xres = it.getImageWidth();
      int yres = it.getImageHeight();
      float zval = stackAxis < 0 ? 0.0f : (float) pos[stackAxis];
      float[][] samps = {
        {0, xres - 1, xres - 1, 0, 0},
        {0, 0, yres - 1, yres - 1, 0},
        {zval, zval, zval, zval, zval}
      };
      RealType xbox = it.getXType();
      RealType ybox = it.getYType();
      RealType zbox = it.getZType();
      Unit[] imageUnits = it.getImageUnits();
      Unit[] xyzUnits = {imageUnits[0], imageUnits[1], it.getZUnit(stackAxis)};
      try {
        RealTupleType xyz = new RealTupleType(xbox, ybox, zbox);
        setData(new Gridded3DSet(xyz, samps, 5, null, xyzUnits, null, false));
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }

    if (doRefs) super.doTransform();
  }

  /**
   * Computes the reference data at the current position,
   * utilizing thumbnails as appropriate.
   */
  protected synchronized void computeData(boolean thumbs) {
    int[] pos = handler.getPos(trans);
    ThumbnailHandler th = trans.getThumbHandler();
    int len = references.size();

    // check whether dimensional position has changed
    DataCache cache = handler.getCache();
    if (!thumbs) {
      if (cachedPos != null && cachedPos.length == pos.length) {
        for (int i=0; i<pos.length; i++) {
          if (cachedPos[i] != pos[i] && i != stackAxis) {
            // for now, simply dump old full-resolution data
            for (int s=0; s<len; s++) {
              if (stackAxis >= 0) cachedPos[stackAxis] = s;
              cache.dump(trans, cachedPos, null);
            }
            // also dump old collapsed image stack
            cache.dump(trans, cachedPos, "collapse");
            break;
          }
        }
      }
      cachedPos = pos;
    }

    // retrieve collapsed image stack from data cache
    FlatField collapse = (FlatField) cache.getData(trans, pos, "collapse", 3);

    // compute image data at each slice
    DisplayImpl display = handler.getWindow().getDisplay();
    DisplayUtil.setDisplayDisabled(display, true);
    FlatField[] slices = new FlatField[len];
    for (int s=0; s<len; s++) {
      if (stackAxis >= 0) pos[stackAxis] = s;

      Data thumb = th == null ? null : th.getThumb(pos);
      DataReferenceImpl sliceRef = (DataReferenceImpl) references.elementAt(s);
      DataRenderer sliceRend = (DataRenderer) renderers.elementAt(s);
      if (thumbs) setData(thumb, sliceRef, sliceRend, true, s);
      else {
        if (!volume || collapse == null) {
          // load data from disk, unless collapsed volume is current
          setMessage("loading full-resolution data (" +
            (s + 1) + "/" + len + ")");
          slices[s] = (FlatField) getImageData(pos);
          if (th != null && thumb == null && !volume) {
            // fill in missing thumbnail
            th.setThumb(pos, th.makeThumb(slices[s]));
          }
        }
        if (!volume) setData(slices[s], sliceRef, sliceRend, true, s);
      }
    }
    if (stackAxis >= 0) pos[stackAxis] = 0;

    // compute volume data
    if (thumbs) setData(DUMMY, volumeRef, false);
    else {
      if (volume) {
        // render slices as a volume
        String res = volumeRes + "x" + volumeRes + "x" + volumeRes;
        setMessage("constructing " + res + " volume");
        ImageTransform it = (ImageTransform) trans;
        RealType zType = it.getZType();
        FunctionType imageType = it.getType();
        Unit[] imageUnits = it.getImageUnits();
        Unit zUnit = it.getZUnit(stackAxis);
        try {
          if (collapse == null) {
            // use image transform's recommended MathType
            for (int i=0; i<len; i++) {
              slices[i] = DataUtil.switchType((FlatField) slices[i],
                imageType, imageUnits);
            }
            // compile slices into a single volume and collapse
            collapse = DataUtil.collapse(
              DataUtil.makeField(slices, zType, 0, len - 1, zUnit));
            cache.putData(trans, pos, "collapse", collapse);
          }
          // resample volume
          setData(DataUtil.makeCube(collapse, volumeRes), volumeRef, false);
          setMessage("rendering " + res + " volume");
        }
        catch (VisADException exc) { exc.printStackTrace(); }
        catch (RemoteException exc) { exc.printStackTrace(); }
      }
      else {
        // slice data is already set; just display burn-in message
        setData(DUMMY, volumeRef, false);
        setMessage("burning in full-resolution data");
      }
      clearWhenDone = true;
    }
    DisplayUtil.setDisplayDisabled(display, false);
  }

  /** Gets 2D data from the specified data transform. */
  protected Data getImageData(int[] pos) {
    Data d = super.getImageData(pos);
    if (!(d instanceof FlatField)) return d;
    FlatField ff = (FlatField) d;

    GriddedSet set = (GriddedSet) ff.getDomainSet();
    int[] len = set.getLengths();
    int[] res = new int[len.length];
    int[] maxRes = handler.getWindow().getManager().getStackResolution();
    for (int i=0; i<len.length; i++) {
      if (len[i] > maxRes[i]) res[i] = maxRes[i];
      else res[i] = len[i];
    }
    try { return DataUtil.resample(ff, res, null); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Computes range values at the current cursor location. */
  protected void computeCursor() {
    // check for active cursor
    cursor = null;
    DisplayImpl display = handler.getWindow().getDisplay();
    DisplayRenderer dr = display.getDisplayRenderer();
    Vector cursorStringVector = dr.getCursorStringVector();
    if (cursorStringVector == null || cursorStringVector.size() == 0) return;

    // get cursor value
    double[] cur = dr.getCursor();
    if (cur == null || cur.length == 0 || cur[0] != cur[0]) return;

    // get range values at the given cursor location
    if (!(trans instanceof ImageTransform)) return;
    ImageTransform it = (ImageTransform) trans;

    // get cursor's domain coordinates
    RealType xType = it.getXType();
    RealType yType = it.getYType();
    RealType zType = it.getZType();
    double[] domain = CursorUtil.cursorToDomain(display,
      new RealType[] {xType, yType, zType}, cur);

    // determine which slice to probe
    int index = -1;
    int len = references.size();
    double step = it.getMicronStep(stackAxis);
    if (step != step) step = 1;
    double zpos = Math.round(domain[2] / step);
    if (zpos >= 0 && zpos < len) index = (int) zpos;
    if (index < 0) return;

    DataReferenceImpl sliceRef =
      (DataReferenceImpl) references.elementAt(index);

    // get data at appropriate slice
    Data data = sliceRef.getData();
    if (!(data instanceof FunctionImpl)) return;
    FunctionImpl func = (FunctionImpl) data;

    // evaluate function at the cursor location
    double[] rangeValues = null;
    try {
      rangeValues = CursorUtil.evaluate(func, domain);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    // compile range value messages
    if (rangeValues == null) return;
    RealType[] range = it.getRangeTypes();
    if (range.length < rangeValues.length) return;

    int num = stackAxis < 0 ? rangeValues.length : rangeValues.length + 1;
    cursor = new VisADException[num];
    String prefix = trans.getName() + ": ";
    for (int i=0; i<rangeValues.length; i++) {
      cursor[i] = new VisADException(prefix +
        range[i].getName() + " = " + rangeValues[i]);
    }
    if (stackAxis >= 0) {
      String dimType = trans.getDimTypes()[stackAxis];
      cursor[rangeValues.length] = new VisADException(prefix +
        "<" + (stackAxis + 1) + "> " + dimType + " = " +
        trans.getLabels()[stackAxis][index]);
    }
  }

}
