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

import java.rmi.RemoteException;

import loci.visbio.VisBioFrame;

import loci.visbio.util.VisUtil;

import visad.*;

/** Provides logic for capturing display screenshots and movies. */
public class RenderHandler {

  // -- Constants --

  /** Minimum length of each spatial axis. */
  public static final int MINIMUM_RESOLUTION = 2;

  /** Maximum length of each spatial axis. */
  public static final int MAXIMUM_RESOLUTION = 160;

  /** Default length of each spatial axis. */
  public static final int DEFAULT_RESOLUTION = 64;


  // -- Fields - GUI components --

  /** Associated display window. */
  protected DisplayWindow window;

  /** GUI controls for volume rendering handler. */
  protected RenderPanel panel;


  // -- Fields - initial state --

  /** Volume rendering flag. */
  protected boolean renderEnabled;

  /** Volume rendering resolution. */
  protected int renderRes;


  // -- Constructor --

  /** Creates a volume rendering handler. */
  public RenderHandler(DisplayWindow dw) {
    window = dw;
    renderEnabled = false;
    renderRes = DEFAULT_RESOLUTION;
  }


  // -- RenderHandler API methods --

  /** Sets whether volume rendering is enabled. */
  public void setRenderEnabled(boolean enabled) {
    if (panel == null) renderEnabled = enabled;
    else panel.getRenderWindow().setRenderEnabled(enabled);
    // CTR TODO toggle rendering
  }

  /** Gets whether volume rendering is enabled. */
  public boolean isRenderEnabled() {
    if (panel == null) return renderEnabled;
    else return panel.getRenderWindow().isRenderEnabled();
  }

  /** Sets volume rendering resolution. */
  public void setResolution(int res) {
    if (panel == null) renderRes = res;
    else panel.getRenderWindow().setResolution(res);
    if (isRenderEnabled()) {
      // CTR TODO retrigger rendering
    }
    VisBioFrame bio = window.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      "volume rendering resolution adjustment for " + window.getName(), true);
  }

  /** Gets volume rendering resolution. */
  public int getResolution() {
    int res = panel == null ? renderRes :
      panel.getRenderWindow().getResolution();
    if (res < MINIMUM_RESOLUTION) res = MINIMUM_RESOLUTION;
    else if (res > MAXIMUM_RESOLUTION) res = MAXIMUM_RESOLUTION;
    return res;
  }

  /** Gets associated display window. */
  public DisplayWindow getWindow() { return window; }

  /** Gets GUI controls for this volume rendering handler. */
  public RenderPanel getPanel() { return panel; }


  // -- RenderHandler API methods - state logic --

  /** Writes the current state. */
  public void saveState() {
    // save parameters
    int res = getResolution();
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


  // -- Utility methods --

  /** Computes a volume across the given fields. */
  public static FlatField makeVolume(FieldImpl field, int volumeRes) {
    FlatField collapse = null;
    try { collapse = VisUtil.collapse(field); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    if (collapse == null) return null;

    GriddedSet set = (GriddedSet) collapse.getDomainSet();
    int[] len = set.getLengths();

    FlatField volumeField = null;
    if (len[0] == volumeRes && len[1] == volumeRes && len[2] == volumeRes) {
      // no need to resample
      volumeField = collapse;
    }
    else {
      // resampling required
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      try {
        Linear3DSet nset = new Linear3DSet(set.getType(), lo[0], hi[0],
          volumeRes, lo[1], hi[1], volumeRes, lo[2], hi[2], volumeRes);
        volumeField = (FlatField) collapse.resample(nset,
          Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    return volumeField;
  }

}
