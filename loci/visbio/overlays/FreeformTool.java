//
// FreeformTool.java
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

package loci.visbio.overlays;

import loci.visbio.data.TransformEvent;

/**
 * FreeformTool is the tool for creating freeform objects.
 * @author Greg Meyer
 */
public class FreeformTool extends OverlayTool {

  // -- Fields --

  /** Line currently being drawn. */
  protected OverlayFreeform freeform;


  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    deselectAll();
    freeform = new OverlayFreeform(overlay, x, y, x, y);
    configureOverlay(freeform);
    overlay.addObject(freeform, pos);
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    if (freeform == null) return;
    freeform.truncateNodeArray();
    freeform.computeGridParameters();
    freeform.setDrawing(false);
    freeform = null;
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    float dx = x - freeform.getLastNodeX();
    float dy = y - freeform.getLastNodeY();
    if (Math.sqrt(dx*dx + dy*dy) > 2.0f) {
      freeform.setNextNode(x, y);
      freeform.setBoundaries(x, y);
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

}
