//
// TransformHandler.java
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.visbio.VisBioFrame;
import loci.visbio.data.DataTransform;
import loci.visbio.state.StateManager;
import loci.visbio.util.VisUtil;
import visad.*;

/** Provides logic for linking data transforms to a display. */
public class TransformHandler implements ChangeListener, Runnable  {

  // -- Constant --

  /** Starting burn-in delay in milliseconds. */
  public static final long DEFAULT_BURN_DELAY = 3000;

  /** Minimum amount of time to delay burn-in. */
  public static final long MINIMUM_BURN_DELAY = 0;

  /** Starting FPS for animation. */
  public static final int DEFAULT_ANIMATION_RATE = 10;


  // -- Fields --

  /** Associated display window. */
  protected DisplayWindow window;

  /** GUI controls for transform handler. */
  protected TransformPanel panel;

  /** Data transform links. */
  protected Vector links;

  /** Dimensional slider widgets for linked transforms. */
  protected Vector sliders;

  /** Panel containing dimensional slider widgets. */
  protected JPanel sliderPanel;

  /** Default burn-in delay in milliseconds. */
  protected long burnDelay;

  /** Flag indicating status of animation. */
  protected boolean animating;

  /** Animation rate. */
  protected int fps;

  /** Dimensional axis to use for animation. */
  protected int animAxis;

  /** Thread responsible for animation. */
  protected Thread animThread;

  /** Synchronization object for animation. */
  protected Object animSync = new Object();


  // -- Fields - initial state --

  /** List of uninitialized links. */
  protected Vector newLinks;


  // -- Constructor --

  /** Creates a display transform handler. */
  public TransformHandler(DisplayWindow dw) {
    window = dw;
    links = new Vector();
    sliders = new Vector();
    sliderPanel = new JPanel();
    burnDelay = DEFAULT_BURN_DELAY;
    fps = DEFAULT_ANIMATION_RATE;
    makePanel();
  }


  // -- TransformHandler API methods --

  /** Links the given data transform to the display. */
  public void addTransform(DataTransform trans) {
    links.add(new TransformLink(this, trans));
    rebuild();
    panel.addTransform(trans);
  }

  /** Removes the given data transform from the display. */
  public void removeTransform(DataTransform trans) {
    TransformLink link = getLink(trans);
    if (link != null) {
      links.remove(link);
      link.destroy();
    }
    panel.removeTransform(trans);
    rebuild();
  }

  /** Unlinks all data transforms from the display. */
  public void removeAllTransforms() {
    links.removeAllElements();
    panel.removeAllTransforms();
    rebuild();
  }

  /** Moves the given transform up in the Z-order. */
  public void moveTransformUp(DataTransform trans) {
    int index = getLinkIndex(trans);
    if (index >= 0) {
      TransformLink link = (TransformLink) links.elementAt(index);
      links.removeElementAt(index);
      links.insertElementAt(link, index - 1);
    }
    doLinks(index - 1, true);
    panel.moveTransformUp(trans);
  }

  /** Moves the given transform down in the Z-order. */
  public void moveTransformDown(DataTransform trans) {
    int index = getLinkIndex(trans);
    if (index >= 0) {
      TransformLink link = (TransformLink) links.elementAt(index);
      links.removeElementAt(index);
      links.insertElementAt(link, index + 1);
    }
    doLinks(index, true);
    panel.moveTransformDown(trans);
  }

  /** Gets whether the given transform is currently linked to the display. */
  public boolean hasTransform(DataTransform trans) {
    return panel.hasTransform(trans);
  }

  /** Gets data transforms linked to the display. */
  public DataTransform[] getTransforms() {
    DataTransform[] dt = new DataTransform[links.size()];
    for (int i=0; i<links.size(); i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      dt[i] = link.getTransform();
    }
    return dt;
  }

  /** Gets number of data transforms linked to the display. */
  public int getTransformCount() { return links.size(); }

  /** Gets associated display window. */
  public DisplayWindow getWindow() { return window; }

  /** Gets GUI controls for this transform handler. */
  public TransformPanel getPanel() { return panel; }

  /** Gets a panel containing sliders widgets for linked transforms. */
  public JPanel getSliderPanel() { return sliderPanel; }

  /**
   * Gets the dimensional position specified
   * by the given transform's slider widgets.
   */
  public int[] getPos(DataTransform trans) {
    int[] pos = new int[trans.getLengths().length];
    Arrays.fill(pos, -1);
    for (int s=0; s<sliders.size(); s++) {
      BioSlideWidget bsw = (BioSlideWidget) sliders.elementAt(s);
      int axis = getAxis(trans, s);
      if (axis >= 0) pos[axis] = bsw.getValue();
    }
    return pos;
  }

  /** Sets the delay in milliseconds before full-resolution burn-in occurs. */
  public void setBurnDelay(long delay) {
    if (delay < MINIMUM_BURN_DELAY) delay = MINIMUM_BURN_DELAY;
    burnDelay = delay;
  }

  /** Gets the delay in milliseconds before full-resolution burn-in occurs. */
  public long getBurnDelay() { return burnDelay; }

  /** Toggles animation of the display. */
  public void setAnimating(boolean on) {
    if (animating == on) return;
    animating = on;
    if (!animating) return;
    startAnimation();
  }

  /** Gets whether display is currently animating. */
  public boolean isAnimating() { return animating; }

  /** Sets animation rate. */
  public void setAnimationRate(int fps) {
    synchronized (animSync) { this.fps = fps; }
    VisBioFrame bio = window.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      "change animation rate for " + window.getName(), true);
  }

  /** Gets animation rate. */
  public int getAnimationRate() { return fps; }

  /** Assigns the given axis as the animation axis. */
  public void setAnimationAxis(int animAxis) {
    synchronized (animSync) { this.animAxis = animAxis; }
    VisBioFrame bio = window.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      "change animation axis for " + window.getName(), true);
  }

  /** Gets the currently assigned animation axis. */
  public int getAnimationAxis() { return animAxis; }

  /** Gets the transform link object for the given data transform. */
  public TransformLink getLink(DataTransform trans) {
    for (int i=0; i<links.size(); i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      if (link.getTransform() == trans) return link;
    }
    return null;
  }

  /** Gets the transform link index for the given data transform. */
  public int getLinkIndex(DataTransform trans) {
    for (int i=0; i<links.size(); i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      if (link.getTransform() == trans) return i;
    }
    return -1;
  }

  /**
   * Identifies the dimensional axis of the given transform
   * to which the specified slider axis corresponds, or -1 if none.
   */
  public int getAxis(DataTransform trans, int axis) {
    if (axis < 0 || axis >= sliders.size()) return -1;
    BioSlideWidget bsw = (BioSlideWidget) sliders.elementAt(axis);
    DataTransform[] dt = bsw.getTransforms();
    int[] ndx = bsw.getIndices();
    for (int t=0; t<dt.length; t++) if (trans == dt[t]) return ndx[t];
    return -1;
  }


  // -- TransformHandler API methods - state logic --

  /** Writes the current state. */
  public void saveState() {
    // save number of links
    int len = links.size();
    window.setAttr("numLinks", "" + len);

    // save all links
    for (int i=0; i<len; i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      link.saveState(window, "link" + i);
    }

    // save other parameters
    window.setAttr("animating", "" + animating);
    window.setAttr("animFPS", "" + fps);
    window.setAttr("animAxis", "" + animAxis);
  }

  /** Restores the current state. */
  public void restoreState() {
    int len = Integer.parseInt(window.getAttr("numLinks"));
    newLinks = new Vector();
    for (int i=0; i<len; i++) {
      TransformLink link = new TransformLink(this);
      link.restoreState(window, "link" + i);
      newLinks.add(link);
    }
    animating = window.getAttr("animating").equalsIgnoreCase("true");
    fps = Integer.parseInt(window.getAttr("animFPS"));
    animAxis = Integer.parseInt(window.getAttr("animAxis"));
  }

  /** Tests whether two objects are in equivalent states. */
  public boolean matches(TransformHandler handler) {
    if (handler == null) return false;
    int size = links.size();
    if (handler.links == null || handler.links.size() != size) return false;
    for (int i=0; i<size; i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      TransformLink hlink = (TransformLink) handler.links.elementAt(i);
      if (link == null && hlink == null) continue;
      if (link == null || hlink == null || !link.matches(hlink)) return false;
    }
    return animating == handler.animating &&
      fps == handler.fps && animAxis == handler.animAxis;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(TransformHandler handler) {
    if (handler == null) {
      if (newLinks != null) {
        // initialize new links
        StateManager.mergeStates(links, newLinks);
        links = newLinks;
        newLinks = null;
      }
    }
    else {
      // merge handler links with current links
      Vector vn = handler.newLinks == null ? handler.links : handler.newLinks;
      StateManager.mergeStates(links, vn);
      links = vn;
      animating = handler.animating;
      fps = handler.fps;
      animAxis = handler.animAxis;
    }

    panel.removeAllTransforms();
    int size = links.size();
    for (int i=0; i<size; i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      panel.addTransform(link.getTransform());
    }

    rebuild();
    if (animating) startAnimation();
  }


  // -- Internal TransformHandler API methods --

  /** Constructs GUI controls for the transform handler. */
  protected void makePanel() { panel = new TransformPanel(this); }

  /** Notifies the transform panel of a new dimensional axis. */
  protected void addAxis(String axis) { panel.addAxis(axis); }

  /** Adds any required custom mappings to the display. */
  protected void doCustomMaps() throws VisADException, RemoteException { }

  /** Links in the transform links, starting at the given index. */
  protected void doLinks(int startIndex, boolean unlinkFirst) {
    DisplayImpl display = window.getDisplay();
    VisUtil.setDisplayDisabled(display, true);

    int size = links.size();
    if (unlinkFirst) {
      for (int l=startIndex; l<size; l++) {
        ((TransformLink) links.elementAt(l)).unlink();
      }
    }

    for (int l=startIndex; l<size; l++) {
      ((TransformLink) links.elementAt(l)).link();
    }
    for (int l=startIndex; l<size; l++) {
      ((TransformLink) links.elementAt(l)).doTransform();
    }

    VisUtil.setDisplayDisabled(display, false);
  }

  /** Rebuilds sliders and display mappings for all linked transforms. */
  protected void rebuild() {
    synchronized (animSync) {
      TransformLink[] lnk = new TransformLink[links.size()];
      links.copyInto(lnk);

      // clear old transforms
      DisplayImpl display = window.getDisplay();
      try {
        VisUtil.setDisplayDisabled(display, true);
        display.removeAllReferences();
        display.clearMaps();
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      sliders.removeAllElements();
      panel.removeAllAxes();

      // rebuild dimensional sliders and mappings list
      Vector mapList = new Vector(), mapTrans = new Vector();
      for (int l=0; l<lnk.length; l++) {
        DataTransform trans = lnk[l].getTransform();
        String[] types = trans.getDimTypes();
        for (int ndx=0; ndx<types.length; ndx++) {
          boolean success = false;
          for (int s=0; s<sliders.size(); s++) {
            BioSlideWidget bsw = (BioSlideWidget) sliders.elementAt(s);
            success = bsw.addTransform(trans, ndx);
            if (success) break;
          }
          if (!success) {
            // create new slider to accommodate incompatible dimensional axis
            BioSlideWidget bsw = new BioSlideWidget(trans, ndx);
            bsw.getSlider().addChangeListener(this);
            sliders.add(bsw);
            addAxis(types[ndx]);
          }
        }
        ScalarMap[] maps = trans.getSuggestedMaps();
        for (int m=0; m<maps.length; m++) {
          if (!mapList.contains(maps[m])) {
            mapList.add(maps[m]);
            mapTrans.add(trans); // save first transform that needs this map
          }
        }
      }

      // reconstruct display mappings
      try {
        for (int i=0; i<mapList.size(); i++) {
          ScalarMap map = (ScalarMap) mapList.elementAt(i);
          display.addMap((ScalarMap) mapList.elementAt(i));

          // configure map's controls according to transform settings;
          // if multiple transforms have the same map, but different
          // settings, the first transform's settings take precedence
          DataTransform trans = (DataTransform) mapTrans.elementAt(i);
          if (map.getDisplayScalar().equals(Display.Text)) {
            // for Text maps, configure font
            TextControl textControl = (TextControl) map.getControl();
            if (textControl != null) textControl.setFont(trans.getFont());
          }
        }
        doCustomMaps();
        doLinks(0, false);
        VisUtil.setDisplayDisabled(display, false);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }

      // rebuild slider panel
      int size = sliders.size();
      if (size == 0) sliderPanel = new JPanel();
      else {
        StringBuffer sb = new StringBuffer("pref");
        for (int i=1; i<size; i++) sb.append(", 3dlu, pref");

        PanelBuilder builder = new PanelBuilder(
          new FormLayout("pref:grow", sb.toString()));
        CellConstraints cc = new CellConstraints();
        for (int i=0; i<size; i++) {
          BioSlideWidget bsw = (BioSlideWidget) sliders.elementAt(i);
          builder.add(bsw, cc.xy(1, 2 * i + 1));
        }
        sliderPanel = builder.getPanel();
      }

      // update GUI to reflect new dimensional position
      panel.updateControls();

      // reinitialize colors
      for (int i=0; i<lnk.length; i++) {
        ColorHandler colorHandler = lnk[i].getColorHandler();
        if (colorHandler != null) colorHandler.initColors();
      }

      if (lnk.length > 0) window.repack();
      else window.setVisible(false);
    }
  }

  /** Starts a new thread for animation. */
  protected void startAnimation() {
    if (animThread != null) {
      try { animThread.join(); }
      catch (InterruptedException exc) { }
    }
    animThread = new Thread(this, "VisBio-AnimThread-" + window.getName());
    animThread.start();
  }


  // -- ChangeListener API methods --

  /** Handles slider updates. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    DataTransform[] trans = null;
    for (int s=0; s<sliders.size(); s++) {
      BioSlideWidget bsw = (BioSlideWidget) sliders.elementAt(s);
      JSlider slider = bsw.getSlider();
      if (src == slider) {
        trans = bsw.getTransforms();
        break;
      }
    }
    for (int t=0; t<trans.length; t++) {
      TransformLink link = getLink(trans[t]);
      link.doTransform();
    }

    // update GUI to reflect new dimensional position
    panel.updateControls();
  }


  // -- Runnable API methods --

  /** Animates the display. */
  public void run() {
    while (animating) {
      long waitTime;
      synchronized (animSync) {
        long start = System.currentTimeMillis();
        if (animAxis >= 0) {
          ((BioSlideWidget) sliders.elementAt(animAxis)).step(true);
        }
        long end = System.currentTimeMillis();
        waitTime = 1000 / fps - end + start;
      }
      if (waitTime >= 0) {
        try { Thread.sleep(waitTime); }
        catch (InterruptedException exc) { exc.printStackTrace(); }
      }
    }
    for (int l=0; l<links.size(); l++) {
      TransformLink link = (TransformLink) links.elementAt(l);
      link.doTransform();
    }
  }

}
