//
// BoxTool.java
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

/** BoxTool is the tool for creating box overlays. */
public class BoxTool extends OverlayTool {

  // -- Fields --

  /** Box currently being drawn. */
  protected OverlayBox box;


  // -- Constructor --

  /** Constructs a box overlay creation tool. */
  public BoxTool(OverlayTransform overlay) {
    super(overlay, "Box", "Box", "box.png");
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    box = new OverlayBox(overlay, x, y, x, y);
    configureOverlay(box);
    OverlayWidget panel = (OverlayWidget) overlay.getControls();
    box.setFilled(panel.isFilled());
    overlay.addObject(box, pos);
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    box.setDrawing(false);
    box = null;
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    if (box == null) return;
    box.setCoords2(x, y);
    overlay.notifyListeners(new TransformEvent(overlay));
  }

}
