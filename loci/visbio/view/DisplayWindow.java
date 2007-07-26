//
// DisplayWindow.java
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

import com.jgoodies.plaf.LookUtils;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import loci.formats.ReflectedUniverse;
import loci.formats.ReflectException;
import loci.visbio.VisBioFrame;
import loci.visbio.WindowManager;
import loci.visbio.data.DataTransform;
import loci.visbio.state.*;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;

/**
 * DisplayWindow is a window containing a 2D or 3D
 * VisAD display and associated controls.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/view/DisplayWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/view/DisplayWindow.java">SVN</a></dd></dl>
 */
public class DisplayWindow extends JFrame
  implements ActionListener, DisplayListener, Dynamic, KeyListener, Saveable
{

  // -- Static fields --

  /** Stereo graphics configuration. */
  protected static final GraphicsConfiguration STEREO =
    DisplayUtil.getStereoConfiguration();

  // -- Fields --

  /** Name of this display. */
  protected String name;

  /** True if this display is 3D, false if 2D. */
  protected boolean threeD;

  // -- Fields - handlers --

  /** Handles logic for controlling the VisAD display's view. */
  protected ViewHandler viewHandler;

  /** Handles logic for capturing the display screenshots and movies. */
  protected CaptureHandler captureHandler;

  /** Handles logic for linking data transforms to the VisAD display. */
  protected TransformHandler transformHandler;

  // -- Fields - GUI components --

  /** Associated display manager. */
  protected DisplayManager manager;

  /** Associated VisAD display. */
  protected DisplayImpl display;

  /** Panel containing dimensional slider widgets. */
  protected JPanel sliders;

  /** Breakaway panel for display controls. */
  protected BreakawayPanel controls;

  // -- Fields - initial state --

  /** Initial edge of breakaway panel. */
  protected String initialEdge;

  // -- Fields - other --

  /** String representation of this display. */
  protected String string;

  // -- Constructors --

  /** Creates an uninitialized display object. */
  public DisplayWindow() { }

  /** Creates an uninitialized display object. */
  public DisplayWindow(DisplayManager dm) {
    super();
    manager = dm;
  }

  /** Creates a new display object according to the given parameters. */
  public DisplayWindow(DisplayManager dm, String name, boolean threeD) {
    super(name);
    manager = dm;
    this.name = name;
    this.threeD = threeD;
    initState(null);
  }

  // -- DisplayWindow API methods --

  /**
   * Enlarges the display to its preferred width and/or height
   * if it is too small, keeping the display itself square.
   */
  public void repack() {
    if (sliders == null) return; // not yet fully initialized
    sliders.removeAll();
    sliders.add(transformHandler.getSliderPanel());
    if (!isVisible()) validate(); // force recomputation of slider panel size
    Dimension size = SwingUtil.getRepackSize(this);
    String edge = controls.getEdge();
    if (edge == BorderLayout.EAST || edge == BorderLayout.WEST) {
      size.width = controls.getPreferredSize().width + size.height - 20;
      // HACK - work around a layout issue where panel is slightly too short
      // this hack also appears in loci.visbio.util.SwingUtil.pack()
      if (LookUtils.IS_OS_LINUX) size.height += 10;
    }
    else if (edge == BorderLayout.NORTH || edge == BorderLayout.SOUTH) {
      size.height = controls.getPreferredSize().height + size.width + 20;
    }
    else controls.repack();
    Dimension actualSize = getSize();
    if (actualSize.width > size.width) size.width = actualSize.width;
    if (actualSize.height > size.height) size.height = actualSize.height;
    setSize(size);
  }

  /** Gets associated VisBio frame. */
  public VisBioFrame getVisBio() { return manager.getVisBio(); }

  /** Gets associated display manager. */
  public DisplayManager getManager() { return manager; }

  /** Gets the associated VisAD display. */
  public DisplayImpl getDisplay() { return display; }

  /** Gets associated breakaway control panel. */
  public BreakawayPanel getControls() { return controls; }

  /** Gets the view handler. */
  public ViewHandler getViewHandler() { return viewHandler; }

  /** Gets the capture handler. */
  public CaptureHandler getCaptureHandler() { return captureHandler; }

  /** Gets the transform handler. */
  public TransformHandler getTransformHandler() { return transformHandler; }

  /** Gets the name of this display. */
  public String getName() { return name; }

  /** Gets whether this view handler's display is 3D. */
  public boolean is3D() { return threeD; }

  /** Links the given data transform to the display. */
  public void addTransform(DataTransform trans) {
    transformHandler.addTransform(trans);
    viewHandler.guessAspect();
    refresh();
    manager.getVisBio().generateEvent(manager,
      "add data object to display", true);
  }

  /** Removes the given data transform from the display. */
  public void removeTransform(DataTransform trans) {
    transformHandler.removeTransform(trans);
    refresh();
    manager.getVisBio().generateEvent(manager,
      "remove data object from display", true);
  }

  /** Unlinks all data transforms from the display. */
  public void removeAllTransforms() {
    transformHandler.removeAllTransforms();
    refresh();
    manager.getVisBio().generateEvent(manager,
      "remove all data objects from display", true);
  }

  /** Gets whether the given transform is currently linked to the display. */
  public boolean hasTransform(DataTransform trans) {
    return transformHandler.hasTransform(trans);
  }

  /** Sets whether transparency mode is nicest vs fastest. */
  public void setTransparencyMode(boolean nice) {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import visad.java3d.DisplayImplJ3D");
      int nicest = ((Integer) r.getVar("DisplayImplJ3D.NICEST")).intValue();
      int fastest = ((Integer) r.getVar("DisplayImplJ3D.FASTEST")).intValue();
      GraphicsModeControl gmc = display.getGraphicsModeControl();
      if (gmc.getClass().getName().equals(
        "visad.java3d.GraphicsModeControlJ3D"))
      {
        gmc.setTransparencyMode(nice ? nicest : fastest);
      }
    }
    catch (ReflectException exc) {
      System.err.println("Warning: transparency mode setting (nice=" +
        nice + ") will have no effect. Java3D is probably not installed.");
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Sets wehther texture mapping is enabled. */
  public void setTextureMapping(boolean textureMapping) {
    try {
      display.getGraphicsModeControl().setTextureEnable(textureMapping);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  /** Sets whether volume rendering uses 3D texturing. */
  public void set3DTexturing(boolean texture3d) {
    try {
      display.getGraphicsModeControl().setTexture3DMode(texture3d ?
        GraphicsModeControl.TEXTURE3D : GraphicsModeControl.STACK2D);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  // -- Component API methods --

  /** Shows or hides this window. */
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) controls.reshow();
  }

  // -- Object API methods --

  /** Gets a string representation of this display (just its name). */
  public String toString() { return string; }

  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
  }

  // -- DisplayListener API methods --

  /** Listens for keyboard presses within the display. */
  public void displayChanged(DisplayEvent e) {
    int id = e.getId();
    if (id == DisplayEvent.KEY_PRESSED) {
      keyPressed((KeyEvent) e.getInputEvent());
    }
    else if (id == DisplayEvent.KEY_RELEASED) {
      keyReleased((KeyEvent) e.getInputEvent());
    }
  }

  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    DisplayWindow window = (DisplayWindow) dyn;

    return ObjectUtil.objectsEqual(name, window.name) &&
      viewHandler.matches(window.viewHandler) &&
      captureHandler.matches(window.captureHandler) &&
      transformHandler.matches(window.transformHandler);
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    if (!(dyn instanceof DisplayWindow)) return false;
    DisplayWindow window = (DisplayWindow) dyn;
    return threeD == window.threeD;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    DisplayWindow window = (DisplayWindow) dyn;

    if (window != null) {
      name = window.name;
      threeD = window.threeD;
    }
    string = name + (threeD ? " (3D)" : " (2D)");

    if (display == null) {
      display = DisplayUtil.makeDisplay(name, threeD, STEREO);
      setTransparencyMode(manager.isNiceTransparency());
      setTextureMapping(manager.isTextureMapped());
      set3DTexturing(manager.is3DTextured());
      display.addDisplayListener(this);
    }
    else display.setName(name);
    setTitle("Display - " + name);

    // handlers
    createHandlers();
    if (window == null) {
      viewHandler.initState(null);
      captureHandler.initState(null);
      transformHandler.initState(null);
    }
    else {
      // handlers' initState methods are smart enough to reinitialize
      // their components only when necessary, to ensure efficiency
      viewHandler.initState(window.viewHandler);
      captureHandler.initState(window.captureHandler);
      transformHandler.initState(window.transformHandler);
    }

    if (controls == null) {
      // display window's content pane
      Container pane = getContentPane();
      pane.setLayout(new BorderLayout());

      // panel for dimensional sliders
      sliders = new JPanel();
      sliders.setLayout(new BorderLayout());

      // breakaway panel for display controls
      controls = new BreakawayPanel(pane, "Controls - " + name, true);
      if (initialEdge == null) initialEdge = BorderLayout.EAST;
      else if (initialEdge.equals("null")) initialEdge = null;
      controls.setEdge(initialEdge);

      // add display controls breakaway window to window manager
      WindowManager wm = (WindowManager)
        manager.getVisBio().getManager(WindowManager.class);
      wm.addWindow(controls.getWindow());

      // listen for key presses
      //addKeyListener(this);
      //controls.addKeyListener(this);

      // NB: Adding the KeyListener directly to frames and panels is not
      // effective, because some child always has the keyboard focus and eats
      // the event. Better would be to add the keyboard listener to each
      // component that does not need the arrow keys for its own purposes. For
      // now, the display itself must have the focus (just click it first).

      // lay out components
      pane.add(display.getComponent(), BorderLayout.CENTER);
      Object[] rows = {
        viewHandler.getPanel(),
        FormsUtil.makeRow(new Object[] {captureHandler.getPanel()}),
        "Data",
        transformHandler.getPanel(),
        sliders
      };
      controls.setContentPane(FormsUtil.makeColumn(rows, null, true));
      pack();
      repack();
    }
    else controls.getWindow().setTitle("Controls - " + name);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() {
    getContentPane().removeAll();

    // sever ties with un-GC-able display object
    if (display != null) {
      display.removeDisplayListener(this);
      try { display.destroy(); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      display = null;
    }

    // NB: Despite all of the above, ties are still not completely severed.
    // VisADCanvasJ3D maintains a parent field (from the Canvas3D superclass)
    // that points to this DisplayWindow. I am uncertain how to kill that
    // reference, so instead we cut ties to a number of objects below. That
    // way, though this DisplayWindow is not GCed, at least its constituent
    // handlers are.
    viewHandler = null;
    captureHandler = null;
    transformHandler = null;
    sliders = null;
    controls = null;
    string = null;

    // NB: And despite all of the fields nulled above, the handlers are also
    // still not GCed. I do not understand why not. Frustrating.
  }

  // -- KeyListener API methods --

  /** Handles key presses. */
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    int mods = e.getModifiers();
    if (mods != 0) return;
    if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
      int axis = transformHandler.getPanel().getLeftRightAxis();
      BioSlideWidget bsw = transformHandler.getSlider(axis);
      if (bsw == null) return;
      if (code == KeyEvent.VK_LEFT) bsw.step(false);
      else bsw.step(true); // code == KeyEvent.VK_RIGHT
    }
    else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
      int axis = transformHandler.getPanel().getUpDownAxis();
      BioSlideWidget bsw = transformHandler.getSlider(axis);
      if (bsw == null) return;
      if (code == KeyEvent.VK_DOWN) bsw.step(false);
      else bsw.step(true); // code == KeyEvent.VK_UP
    }
  }

  /** Handles key releases. */
  public void keyReleased(KeyEvent e) { }

  /** Handles key strokes. */
  public void keyTyped(KeyEvent e) { }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Displays"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "Display");
    child.setAttribute("name", name);
    child.setAttribute("threeD", "" + threeD);
    child.setAttribute("edge", "" +
      (controls == null ? initialEdge : controls.getEdge()));
    viewHandler.saveState(child);
    captureHandler.saveState(child);
    transformHandler.saveState(child);
  }

  /** Restores the current state from the given DOM element ("Display"). */
  public void restoreState(Element el) throws SaveException {
    name = el.getAttribute("name");
    threeD = el.getAttribute("threeD").equalsIgnoreCase("true");
    initialEdge = el.getAttribute("edge");

    createHandlers();
    viewHandler.restoreState(el);
    captureHandler.restoreState(el);
    transformHandler.restoreState(el);
  }

  // -- Helper methods --

  /** Constructs logic handlers. */
  protected void createHandlers() {
    if (viewHandler == null) viewHandler = new ViewHandler(this);
    if (captureHandler == null) captureHandler = new CaptureHandler(this);
    if (transformHandler == null) {
      transformHandler = threeD ?
        new StackHandler(this) : new TransformHandler(this);
    }
  }

  /** Refreshes GUI components. */
  protected void refresh() {
    if (transformHandler.getTransformCount() == 0) setVisible(false);
    manager.getControls().refresh();
  }

  // -- Utility methods --

  /** Figures out which DisplayWindow contains the given display, if any. */
  public static DisplayWindow getDisplayWindow(DisplayImpl d) {
    Window w = SwingUtil.getWindow(d.getComponent());
    if (!(w instanceof DisplayWindow)) return null;
    return (DisplayWindow) w;
  }

}
