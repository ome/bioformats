//
// OvalTool.java
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

package loci.visbio.overlays;

import loci.visbio.data.TransformEvent;

/** OvalTool is the tool for creating oval overlays. */
public class OvalTool extends OverlayTool {

  // -- Fields --

  /** Oval currently being drawn. */
  protected OverlayOval oval;


  // -- Constructor --

  /** Constructs an oval overlay creation tool. */
  public OvalTool(OverlayTransform overlay) {
    super(overlay, "Oval", "Oval", "oval.png");
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    deselectAll();
    oval = new OverlayOval(overlay, x, y, x, y);
    configureOverlay(oval);
    OverlayWidget panel = (OverlayWidget) overlay.getControls();
    oval.setFilled(panel.isFilled());
    overlay.addObject(oval, pos);
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    if (oval == null) return;
    oval.setDrawing(false);
    oval = null;
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    if (oval == null) return;
    oval.setCoords2(x, y);
    overlay.notifyListeners(new TransformEvent(overlay));
  }

}
