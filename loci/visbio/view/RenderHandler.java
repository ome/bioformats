//
// RenderHandler.java
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

/** Provides logic for capturing display screenshots and movies. */
public class RenderHandler {

  // -- Constants --

  /** Minimum length of each spatial axis. */
  public static final int MINIMUM_RESOLUTION = 2;

  /** Maximum length of each spatial axis. */
  public static final int MAXIMUM_RESOLUTION = 192;

  /** Default length of each spatial axis. */
  public static final int DEFAULT_RESOLUTION = 64;


  // -- Fields - GUI components --

  /** Associated display window. */
  protected DisplayWindow window;

  /** GUI controls for volume rendering handler. */
  protected RenderPanel panel;


  // -- Fields - initial state --

  /** Volume rendering resolution. */
  protected int renderRes;


  // -- Constructor --

  /** Creates a volume rendering handler. */
  public RenderHandler(DisplayWindow dw) {
    window = dw;
    renderRes = DEFAULT_RESOLUTION;
  }


  // -- RenderHandler API methods --

  /** Gets volume rendering resolution. */
  public int getResolution() {
    return panel == null ? renderRes : panel.getRenderWindow().getResolution();
  }

  /** Gets associated display window. */
  public DisplayWindow getWindow() { return window; }

  /** Gets GUI controls for this volume rendering handler. */
  public RenderPanel getPanel() { return panel; }


  // -- RenderHandler API methods - state logic --

  /** Writes the current state. */
  public void saveState() {
    RenderWindow renderWindow = panel.getRenderWindow();

    // save parameters
    int res = renderWindow.getResolution();
    window.setAttr("renderRes", "" + res);
  }

  /** Restores the current state. */
  public void restoreState() {
    renderRes = Integer.parseInt(window.getAttr("renderRes"));
  }

  /** Tests whether two objects are in equivalent states. */
  public boolean matches(RenderHandler handler) {
    if (handler == null) return false;
    if (getResolution() != handler.getResolution()) return false;
    return true;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */ 
  public void initState(RenderHandler handler) {
    if (handler != null) {
      // set parameters
      renderRes = handler.getResolution();
    }

    if (panel == null) panel = new RenderPanel(this);

    // set render window state to match
    RenderWindow renderWindow = panel.getRenderWindow();
    renderWindow.setResolution(renderRes);
  }

}
