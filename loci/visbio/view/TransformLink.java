//
// TransformLink.java
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

import java.rmi.RemoteException;

import java.util.Vector;

import loci.visbio.data.DataTransform;
import loci.visbio.data.ImageTransform;
import loci.visbio.data.ThumbnailHandler;
import loci.visbio.data.TransformEvent;
import loci.visbio.data.TransformListener;

import loci.visbio.util.VisUtil;

import visad.*;

import visad.util.Util;

/** Represents a link between a data transform and a display. */
public class TransformLink
  implements DisplayListener, Runnable, TransformListener
{

  // -- Constants --

  protected static final int BURN_DELAY = 3000;


  // -- Fields --

  /** Associated transform handler. */
  protected TransformHandler handler;

  /** Data transform linked to the display. */
  protected DataTransform trans;

  /** Data reference linking data to the display. */
  protected DataReferenceImpl ref;

  /** Data renderer for toggling data's visibility and other parameters. */
  protected DataRenderer rend;

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


  // -- Constructor --

  /**
   * Creates a link between the given data transform and
   * the specified transform handler's display.
   */
  public TransformLink(TransformHandler h, DataTransform t) {
    handler = h;
    trans = t;

    // build data reference
    try {
      ref = new DataReferenceImpl(trans.getName());
      DisplayImpl display = handler.getWindow().getDisplay();
      rend = display.getDisplayRenderer().makeDefaultRenderer();
      display.addDisplayListener(this);
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    // listen for changes to this transform
    t.addTransformListener(this);

    // initialize thread for handling full-resolution burn-in operations
    Thread burnThread = new Thread(this);
    burnThread.start();
  }


  // -- TransformLink API methods --

  /** Gets the link's data transform. */
  public DataTransform getTransform() { return trans; }

  /** Gets the link's reference. */
  public DataReferenceImpl getReference() { return ref; }

  /** Gets the link's renderer. */
  public DataRenderer getRenderer() { return rend; }

  /** Links this transform into the display. */
  public void link() {
    try {
      DisplayImpl display = handler.getWindow().getDisplay();
      display.addReferences(rend, ref);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Frees resources being consumed by this transform link. */
  public void destroy() { alive = false; }

  /** Toggles visibility of the transform. */
  public void setVisible(boolean vis) { rend.toggle(vis); }

  /** Gets visibility of the transform. */
  public boolean isVisible() { return rend.getEnabled(); }

  /** Sets status messages displayed in display's bottom left-hand corner. */
  public void setMessage(String msg) {
    status = msg == null ? null :
      new VisADException(trans.getName() + ": " + msg);
    doMessages(false);
  }


  // -- DisplayListener API methods --

  /** Ensures status messages stay visible. */
  public void displayChanged(DisplayEvent e) {
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
  }


  // -- Runnable API methods --

  /** Executes full-resolution burn-in operations. */
  public void run() {
    while (true) {
      // wait until a new burn-in is requested
      if (!alive) break;
      while (System.currentTimeMillis() > burnTime) {
        try { Thread.sleep(100); }
        catch (InterruptedException exc) { }
      }

      // wait until appointed burn-in time (which could change during the wait)
      if (!alive) break;
      long time;
      while ((time = System.currentTimeMillis()) < burnTime) {
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


  // -- TransformListener API methods --

  /** Called when a data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) { doTransform(100); }


  // -- Internal TransformLink API methods --

  /** Updates displayed data based on current dimensional position. */
  protected void doTransform() { doTransform(BURN_DELAY); }

  /** Updates displayed data based on current dimensional position. */
  protected void doTransform(long delay) {
    // request a new burn-in in delay milliseconds
    burnTime = System.currentTimeMillis() + delay;
    computeData(true);
  }

  /**
   * Computes the reference data at the current position,
   * utilizing thumbnails as appropriate.
   */
  protected void computeData(boolean thumbs) {
    int[] pos = handler.getPos(trans);
    ThumbnailHandler th = trans.getThumbHandler();
    Data thumb = th == null ? null : th.getThumb(pos);
    if (thumbs) {
      try { ref.setData(thumb); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else {
      setMessage("loading full-resolution data");
      Data d = getImageData(pos);
      if (th != null && thumb == null) {
        // fill in missing thumbnail
        th.setThumb(pos, th.makeThumb(d));
      }
      setMessage("burning in full-resolution data");
      clearWhenDone = true;
      try { ref.setData(d); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      handler.getWindow().getColorHandler().reAutoScale();
    }
  }

  /** Gets the transform's data at the given dimensional position. */
  protected Data getImageData(int[] pos) { return trans.getData(pos, 2); }

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
    double[] domain = VisUtil.cursorToDomain(display,
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
          VisUtil.redrawMessages(handler.getWindow().getDisplay());
        }
      });
    }
    else VisUtil.redrawMessages(handler.getWindow().getDisplay());
  }

}
