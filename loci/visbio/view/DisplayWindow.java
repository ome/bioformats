//
// DisplayWindow.java
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

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import loci.ome.xml.CAElement;
import loci.ome.xml.OMEElement;

import loci.visbio.VisBioFrame;
import loci.visbio.WindowManager;

import loci.visbio.data.DataTransform;

import loci.visbio.state.Dynamic;

import loci.visbio.util.*;

import visad.DisplayImpl;

/**
 * DisplayWindow is a window containing a 2D or 3D
 * VisAD display and associated controls.
 */
public class DisplayWindow extends JFrame implements ActionListener, Dynamic {

  // -- Static fields --

  /** Stereo graphics configuration. */
  protected static final GraphicsConfiguration STEREO =
    VisUtil.getStereoConfiguration();


  // -- Fields --

  /** Name of this display. */
  protected String name;

  /** True if this display is 3D, false if 2D. */
  protected boolean threeD;


  // -- Handlers --

  /** Handles logic for controlling the VisAD display's view. */
  protected ViewHandler viewHandler;

  /** Handles logic for capturing the display screenshots and movies. */
  protected CaptureHandler captureHandler;

  /** Handles logic for volume rendering. */
  protected RenderHandler renderHandler;

  /** Handles logic for linking data transforms to the VisAD display. */
  protected TransformHandler transformHandler;


  // -- GUI components --

  /** Associated display manager. */
  protected DisplayManager manager;

  /** Associated VisAD display. */
  protected DisplayImpl display;

  /** Panel containing dimensional slider widgets. */
  protected JPanel sliders;

  /** Breakaway panel for display controls. */
  protected BreakawayPanel controls;


  // -- Other fields --

  /** String representation of this display. */
  protected String string;


  // -- Constructors --

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
      if ("Linux".equals(System.getProperty("os.name"))) size.height += 10;
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

  /** Gets the associated VisAD display. */
  public DisplayImpl getDisplay() { return display; }

  /** Gets the view handler. */
  public ViewHandler getViewHandler() { return viewHandler; }

  /** Gets the capture handler. */
  public CaptureHandler getCaptureHandler() { return captureHandler; }

  /** Gets the volume rendering handler. */
  public RenderHandler getRenderHandler() { return renderHandler; }

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


  // -- DisplayWindow API methods - state logic --

  protected static final String DISPLAY_DIALOG = "VisBio_DisplayWindow";

  protected CAElement custom;
  protected int index;

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome, int id) {
    custom = ome.getCustomAttr();
    custom.createElement(DISPLAY_DIALOG);
    setAttr("id", "" + id);
    setAttr("name", name);
    setAttr("threeD", "" + threeD);
    viewHandler.saveState();
    captureHandler.saveState();
    if (renderHandler != null) renderHandler.saveState();
    transformHandler.saveState();
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(OMEElement ome, int id) {
    custom = ome.getCustomAttr();
    String[] idList = custom.getAttributes(DISPLAY_DIALOG, "id");

    // identify transform index
    index = -1;
    for (int i=0; i<idList.length; i++) {
      try {
        int iid = Integer.parseInt(idList[i]);
        if (id == iid) {
          index = i;
          break;
        }
      }
      catch (NumberFormatException exc) { }
    }
    if (index < 0) {
      System.err.println("Attributes for display #" + id + " not found.");
      return;
    }

    name = getAttr("name");
    threeD = getAttr("threeD").equalsIgnoreCase("true");

    createHandlers();
    viewHandler.restoreState();
    captureHandler.restoreState();
    if (renderHandler != null) renderHandler.saveState();
    transformHandler.restoreState();
  }

  /** Sets value for the given attribute. */
  protected void setAttr(String attr, String value) {
    custom.setAttribute(attr, value);
  }

  /** Gets value for the given attribute. */
  protected String getAttr(String attr) {
    return custom.getAttributes(DISPLAY_DIALOG, attr)[index];
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


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!isCompatible(dyn)) return false;
    DisplayWindow window = (DisplayWindow) dyn;

    boolean renderMatch =
      (renderHandler == null && window.renderHandler == null) ||
      (renderHandler != null && renderHandler.matches(window.renderHandler));
    return ObjectUtil.objectsEqual(name, window.name) &&
      viewHandler.matches(window.viewHandler) &&
      captureHandler.matches(window.captureHandler) && renderMatch &&
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

    if (display == null) display = VisUtil.makeDisplay(name, threeD, STEREO);
    else display.setName(name);
    setTitle("Display - " + name);

    // handlers
    createHandlers();
    if (window == null) {
      viewHandler.initState(null);
      captureHandler.initState(null);
      if (renderHandler != null) renderHandler.initState(null);
      transformHandler.initState(null);
    }
    else {
      // handlers' initState methods are smart enough to reinitialize
      // their components only when necessary, to ensure efficiency
      viewHandler.initState(window.viewHandler);
      captureHandler.initState(window.captureHandler);
      renderHandler.initState(window.renderHandler);
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
      controls.setEdge(BorderLayout.EAST);

      // add display controls breakaway window to window manager
      WindowManager wm = (WindowManager)
        manager.getVisBio().getManager(WindowManager.class);
      wm.addWindow(controls.getWindow());

      // lay out handler panels
      Vector handlerPanels = new Vector();
      handlerPanels.add(captureHandler.getPanel());
      if (renderHandler != null) handlerPanels.add(renderHandler.getPanel());
      Object[] handlerPanelArray = new Object[handlerPanels.size()];
      handlerPanels.copyInto(handlerPanelArray);

      // lay out components
      pane.add(display.getComponent(), BorderLayout.CENTER);
      controls.setContentPane(FormsUtil.makeColumn(new Object[] {
        viewHandler.getPanel(), FormsUtil.makeRow(handlerPanelArray),
        "Data", transformHandler.getPanel(), sliders}, null, true));
      pack();
      repack();
    }
    else controls.getWindow().setTitle("Controls - " + name);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


  // -- Helper methods --

  /** Constructs logic handlers. */
  protected void createHandlers() {
    if (viewHandler == null) viewHandler = new ViewHandler(this);
    if (captureHandler == null) captureHandler = new CaptureHandler(this);
    if (renderHandler == null) {
      if (threeD) renderHandler = new RenderHandler(this);
    }
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

}
