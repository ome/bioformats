//
// TransformLink.java
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

import java.awt.Font;
import java.rmi.RemoteException;
import java.util.Vector;
import loci.visbio.VisBioFrame;
import loci.visbio.data.*;
import loci.visbio.state.*;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;
import visad.util.Util;

/**
 * Represents a link between a data transform and a display
 * that produces a single image.
 */
public class TransformLink
  implements DisplayListener, Dynamic, Runnable, Saveable, TransformListener
{

  // -- Fields --

  /** Associated transform handler. */
  protected TransformHandler handler;

  /** Data transform linked to the display. */
  protected DataTransform trans;

  /** Associated handler for managing this link's color settings. */
  protected ColorHandler colorHandler;

  /** Data reference linking data to the display. */
  protected DataReferenceImpl ref;

  /** Data renderer for toggling data's visibility and other parameters. */
  protected DataRenderer rend;

  /** Separate thread for managing full-resolution burn-in. */
  protected Thread burnThread;

  /** Whether a full-resolution burn-in should occur immediately. */
  protected boolean burnNow;

  /** Next clock time a full-resolution burn-in should occur. */
  protected long burnTime;

  /** Whether this link is still active. */
  protected boolean alive = true;

  /** Status message, to be displayed in bottom left corner. */
  protected VisADException status;

  /**
   * Range values for current cursor location,
   * to be displayed in bottom left corner.
   */
  protected VisADException[] cursor;

  /** Whether a TRANSFORM_DONE event should clear the status message. */
  protected boolean clearWhenDone;

  /** Last cached dimensional position of the link. */
  protected int[] cachedPos;


  // -- Fields - initial state --

  /** Whether data transform is visible onscreen. */
  protected boolean visible;


  // -- Constructor --

  /** Constructs an uninitialized transform link. */
  public TransformLink(TransformHandler h) { handler = h; }

  /**
   * Creates a link between the given data transform and
   * the specified transform handler's display.
   */
  public TransformLink(TransformHandler h, DataTransform t) {
    handler = h;
    trans = t;
    if (trans instanceof ImageTransform) colorHandler = new ColorHandler(this);
    visible = true;
    initState(null);
  }


  // -- TransformLink API methods --

  /** Gets the link's transform handler. */
  public TransformHandler getHandler() { return handler; }

  /** Gets the link's data transform. */
  public DataTransform getTransform() { return trans; }

  /** Gets the link's color handler. */
  public ColorHandler getColorHandler() { return colorHandler; }

  /** Gets the link's reference. */
  public DataReferenceImpl getReference() { return ref; }

  /** Gets the link's renderer. */
  public DataRenderer getRenderer() { return rend; }

  /** Links this transform into the display. */
  public void link() {
    // rebuild renderer if necessary
    int numMaps = colorHandler == null ? -1 : colorHandler.getMaps().length;
    String imageRenderer = "visad.bom.ImageRendererJ3D";
    boolean isImageRend = rend != null &&
      rend.getClass().getName().equals(imageRenderer);
    if (numMaps == 1 && !isImageRend) {
      // use ImageRendererJ3D when possible
      rend = null;
      try {
        Class c = Class.forName(imageRenderer);
        rend = (DataRenderer) c.newInstance();
        rend.toggle(visible);
      }
      catch (NoClassDefFoundError err) { }
      catch (ClassNotFoundException exc) { exc.printStackTrace(); }
      catch (IllegalAccessException exc) { exc.printStackTrace(); }
      catch (InstantiationException exc) { exc.printStackTrace(); }
    }
    else if (numMaps > 1 && isImageRend) {
      // ImageRendererJ3D does not allow multiple mappings to Display.RGBA
      rend = null;
    }
    if (rend == null) {
      DisplayImpl display = handler.getWindow().getDisplay();
      rend = display.getDisplayRenderer().makeDefaultRenderer();
      rend.toggle(visible);
    }

    // link in the transform
    try { handler.getWindow().getDisplay().addReferences(rend, ref); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Unlinks this transform from the display. */
  public void unlink() {
    try { handler.getWindow().getDisplay().removeReference(ref); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Frees resources being consumed by this transform link. */
  public void destroy() { alive = false; }

  /** Toggles visibility of the transform. */
  public void setVisible(boolean vis) { rend.toggle(vis); }

  /** Gets visibility of the transform. */
  public boolean isVisible() {
    return rend == null ? visible : rend.getEnabled();
  }

  /** Sets status messages displayed in display's bottom left-hand corner. */
  public void setMessage(String msg) {
    if (trans.isImmediate()) return; // no messages in immediate mode
    status = msg == null ? null :
      new VisADException(trans.getName() + ": " + msg);
    doMessages(false);
  }


  // -- DisplayListener API methods --

  /** Handles VisAD display events. */
  public void displayChanged(DisplayEvent e) {
    // ensure status messages stay visible
    int id = e.getId();
    if (id == DisplayEvent.FRAME_DONE) {
      computeCursor();
      doMessages(true);
    }
    else if (e.getId() == DisplayEvent.TRANSFORM_DONE) {
      if (clearWhenDone) {
        setMessage(null);
        clearWhenDone = false;
      }
      else doMessages(false);
    }

    // pass along DisplayEvents to linked transform
    trans.displayChanged(e);
  }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    TransformLink link = (TransformLink) dyn;
    return getTransform().matches(link.getTransform()) &&
      isVisible() == link.isVisible();
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof TransformLink;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;

    if (burnThread != null) {
      alive = false;
      try { burnThread.join(); }
      catch (InterruptedException exc) { }
      alive = true;
    }

    TransformLink link = (TransformLink) dyn;
    if (link != null) {
      if (trans != null) trans.removeTransformListener(this);
      trans = link.getTransform();
      if (trans instanceof ImageTransform && colorHandler == null) {
        colorHandler = new ColorHandler(this);
      }
      if (colorHandler != null) colorHandler.initState(link.getColorHandler());
      visible = link.isVisible();
    }
    else if (colorHandler != null) colorHandler.initState(null);

    // remove old data reference
    DisplayImpl display = handler.getWindow().getDisplay();
    if (ref != null) {
      try { display.removeReference(ref); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }

    // build data reference
    try {
      ref = new DataReferenceImpl(trans.getName());
      display.addDisplayListener(this);
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    // listen for changes to this transform
    trans.addTransformListener(this);

    // initialize thread for handling full-resolution burn-in operations
    if (!trans.isImmediate()) {
      burnThread = new Thread(this, "VisBio-BurnThread-" +
        handler.getWindow().getName() + ":" + trans.getName());
      burnThread.start();
    }
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { destroy(); }


  // -- Runnable API methods --

  /** Executes full-resolution burn-in operations. */
  public void run() {
    while (true) {
      // wait until a new burn-in is requested
      if (!alive) break;
      while (System.currentTimeMillis() > burnTime && !burnNow) {
        try { Thread.sleep(50); }
        catch (InterruptedException exc) { }
      }
      burnNow = false;

      // wait until appointed burn-in time (which could change during the wait)
      long time;
      while ((time = System.currentTimeMillis()) < burnTime) {
        if (!alive) break;
        long wait = burnTime - time;
        if (wait >= 1000) {
          long seconds = wait / 1000;
          setMessage(seconds + " second" +
            (seconds == 1 ? "" : "s") + " until burn in");
          try { Thread.sleep(1000); }
          catch (InterruptedException exc) { }
        }
        else {
          try { Thread.sleep(wait); }
          catch (InterruptedException exc) { }
        }
      }

      // burn-in full resolution data
      if (!alive) break;
      computeData(false);
    }
  }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("LinkedData"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "TransformLink");
    child.setAttribute("id", "" + trans.getTransformId());
    if (colorHandler != null) colorHandler.saveState(child);
    child.setAttribute("visible", "" + isVisible());
  }

  /**
   * Restores the current state from the given DOM element ("TransformLink").
   */
  public void restoreState(Element el) throws SaveException {
    VisBioFrame bio = handler.getWindow().getVisBio();
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    trans = dm.getDataById(Integer.parseInt(el.getAttribute("id")));
    if (trans instanceof ImageTransform && colorHandler == null) {
      colorHandler = new ColorHandler(this);
    }
    if (colorHandler != null) colorHandler.restoreState(el);
    visible = el.getAttribute("visible").equalsIgnoreCase("true");
  }


  // -- TransformListener API methods --

  /** Called when a data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) {
      doTransform(TransformHandler.MINIMUM_BURN_DELAY);
    }
    else if (id == TransformEvent.FONT_CHANGED) {
      Font font = trans.getFont();

      // HACK - always use font size 8, since it renders faster
      // and has virtually no effect on rendering size
      if (font != null) font = new Font(font.getName(), font.getStyle(), 8);

      // compile list of potential maps to Display.Text
      Vector textMaps = new Vector();
      ScalarMap[] maps = trans.getSuggestedMaps();
      for (int i=0; i<maps.length; i++) {
        if (maps[i].getDisplayScalar().equals(Display.Text)) {
          textMaps.add(maps[i]);
        }
      }

      // search display for matching text maps
      DisplayImpl display = handler.getWindow().getDisplay();
      Vector mapVector = display.getMapVector();
      for (int i=0; i<mapVector.size(); i++) {
        ScalarMap map = (ScalarMap) mapVector.elementAt(i);
        for (int j=0; j<textMaps.size(); j++) {
          ScalarMap textMap = (ScalarMap) textMaps.elementAt(j);
          if (map.equals(textMap)) {
            // update font for matching text map
            TextControl textControl = (TextControl) map.getControl();
            if (textControl != null) {
              try { textControl.setFont(font); }
              catch (VisADException exc) { exc.printStackTrace(); }
              catch (RemoteException exc) { exc.printStackTrace(); }
            }
          }
        }
      }
    }
  }


  // -- Internal TransformLink API methods --

  /** Updates displayed data based on current dimensional position. */
  protected void doTransform() { doTransform(handler.getBurnDelay()); }

  /** Updates displayed data based on current dimensional position. */
  protected void doTransform(long delay) { doTransform(delay, false); }

  /** Updates displayed data based on current dimensional position. */
  protected void doTransform(long delay, boolean now) {
    String append = handler.getWindow().getName() + ":" + trans.getName();
    final boolean immediate = now || trans.isImmediate();
    final long burnDelay = delay;
    new Thread("VisBio-ComputeDataThread-" + append) {
      public void run() {
        int[] pos = handler.getPos(trans);
        if (handler.getCache().hasData(trans, pos, null)) {
          // cache hit; burn in immediately
          burnTime = System.currentTimeMillis();
          burnNow = true;
        }
        else if (immediate) computeData(false);
        else {
          computeData(true);
          // request a new burn-in in delay milliseconds
          burnTime = System.currentTimeMillis() + burnDelay;
          if (burnDelay < 100) burnNow = true;
        }
      }
    }.start();
  }

  /**
   * Computes the reference data at the current position,
   * utilizing thumbnails as appropriate.
   */
  protected synchronized void computeData(boolean thumbs) {
    int dim = handler.getWindow().is3D() ? 3 : 2;
    if (dim == 3 && !trans.isValidDimension(3)) dim = 2;
    if (!trans.isValidDimension(dim)) {
      System.err.println("Warning: display \"" +
        handler.getWindow().getName() +
        "\" is incapable of showing data \"" + trans.getName() + "\"");
      return;
    }

    int[] pos = handler.getPos(trans);
    ThumbnailHandler th = trans.getThumbHandler();
    Data thumb = th == null ? null : th.getThumb(pos);
    if (thumbs) setData(thumb);
    else {
      setMessage("loading full-resolution data");
      if (!ObjectUtil.arraysEqual(pos, cachedPos)) {
        // for now, simply dump old full-resolution data
        handler.getCache().dump(trans, cachedPos, null);
      }
      cachedPos = pos;
      Data d = dim == 3 ?
        trans.getData(pos, 3, handler.getCache()) : getImageData(pos);
      if (th != null && thumb == null) {
        // fill in missing thumbnail
        th.setThumb(pos, th.makeThumb(d));
      }
      setMessage("burning in full-resolution data");
      clearWhenDone = true;
      setData(d);
      if (colorHandler != null) colorHandler.reAutoScale();
    }
  }

  /** Gets the transform's data at the given dimensional position. */
  protected Data getImageData(int[] pos) {
    return trans.getData(pos, 2, handler.getCache());
  }

  /** Assigns the given data object to the data reference. */
  protected void setData(Data d) { setData(d, ref, true); }

  /**
   * Assigns the given data object to the data reference,
   * switching to the proper types if the flag is set.
   */
  protected void setData(Data d, boolean autoSwitch) {
    setData(d, ref, autoSwitch);
  }

  /** Assigns the given data object to the given data reference. */
  protected void setData(Data d, DataReference dataRef) {
    setData(d, dataRef, true);
  }

  /**
   * Assigns the given data object to the given data reference,
   * switching to the proper types if the flag is set.
   */
  protected void setData(Data d, DataReference dataRef, boolean autoSwitch) {
    if (autoSwitch && d instanceof FlatField &&
      trans instanceof ImageTransform)
    {
      // special case: use ImageTransform's suggested MathType instead
      FlatField ff = (FlatField) d;
      ImageTransform it = (ImageTransform) trans;
      FunctionType ftype = it.getType();
      Unit[] units = it.getImageUnits();
      try { d = DataUtil.switchType(ff, ftype, units); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    try { dataRef.setData(d); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
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

    // retrieve data object to be probed
    Data data = ref.getData();
    if (!(data instanceof FunctionImpl)) return;
    FunctionImpl func = (FunctionImpl) data;

    // get cursor's domain coordinates
    RealType xType = it.getXType();
    RealType yType = it.getYType();
    double[] domain = DisplayUtil.cursorToDomain(display,
      new RealType[] {xType, yType, null}, cur);

    // evaluate function at the cursor location
    double[] rangeValues = null;
    try {
      RealTuple tuple = new RealTuple(new Real[] {
        new Real(xType, domain[0]),
        new Real(yType, domain[1])
      });

      Data result = func.evaluate(tuple,
        Data.NEAREST_NEIGHBOR, Data.NO_ERRORS);
      if (result instanceof Real) {
        Real r = (Real) result;
        rangeValues = new double[] {r.getValue()};
      }
      else if (result instanceof RealTuple) {
        RealTuple rt = (RealTuple) result;
        int dim = rt.getDimension();
        rangeValues = new double[dim];
        for (int j=0; j<dim; j++) {
          Real r = (Real) rt.getComponent(j);
          rangeValues[j] = r.getValue();
        }
      }
      else return;
    }
    catch (VisADException exc) { return; }
    catch (RemoteException exc) { return; }

    // compile range value messages
    if (rangeValues == null) return;
    RealType[] range = it.getRangeTypes();
    if (range.length < rangeValues.length) return;

    cursor = new VisADException[rangeValues.length];
    String prefix = trans.getName() + ": ";
    for (int i=0; i<rangeValues.length; i++) {
      cursor[i] = new VisADException(prefix +
        range[i].getName() + " = " + rangeValues[i]);
    }
  }


  // -- Helper methods --

  /**
   * Assigns the current status and cursor messages to the data renderer
   * and redraws the display, optionally using the Swing event thread.
   */
  private void doMessages(boolean swing) {
    if (rend == null) return;
    Vector oldList = rend.getExceptionVector();
    Vector newList = new Vector();
    if (cursor != null) {
      for (int i=0; i<cursor.length; i++) newList.add(cursor[i]);
    }
    if (status != null) newList.add(status);

    boolean equal = true;
    if (oldList == null) equal = false;
    else {
      int len = oldList.size();
      if (newList.size() != len) equal = false;
      else {
        for (int i=0; i<len; i++) {
          VisADException oldExc = (VisADException) oldList.elementAt(i);
          VisADException newExc = (VisADException) newList.elementAt(i);
          if (!oldExc.getMessage().equals(newExc.getMessage())) {
            equal = false;
            break;
          }
        }
      }
    }
    if (equal) return; // no change;

    rend.clearExceptions();
    int len = newList.size();
    for (int i=0; i<len; i++) {
      rend.addException((VisADException) newList.elementAt(i));
    }

    if (swing) {
      Util.invoke(false, new Runnable() {
        public void run() {
          DisplayUtil.redrawMessages(handler.getWindow().getDisplay());
        }
      });
    }
    else DisplayUtil.redrawMessages(handler.getWindow().getDisplay());
  }

}
