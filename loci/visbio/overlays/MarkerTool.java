//
// MarkerTool.java
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
import visad.DisplayEvent;

/**
 * MarkerTool is the tool for creating measurement markers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/MarkerTool.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/MarkerTool.java">SVN</a></dd></dl>
 */
public class MarkerTool extends OverlayTool {

  // -- Fields --

  /** Marker currently being drawn. */
  protected OverlayMarker marker;

  // -- Constructor --

  /** Constructs a measurement marker creation tool. */
  public MarkerTool(OverlayTransform overlay) {
    super(overlay, "Marker", "Marker", "marker.png");
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    deselectAll();
    marker = new OverlayMarker(overlay, dx, dy);
    overlay.addObject(marker, pos);
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    if (marker == null) return;
    marker.setDrawing(false);
    marker = null;
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    if (marker == null) return;
    marker.setCoords(dx, dy);
    overlay.notifyListeners(new TransformEvent(overlay));
  }

}
