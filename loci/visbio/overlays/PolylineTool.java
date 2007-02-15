//
// PolylineTool.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Greg Meyer.

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

import java.awt.event.InputEvent;
import loci.visbio.data.TransformEvent;
import loci.visbio.util.DisplayUtil;
import visad.DisplayEvent;
import visad.DisplayImpl;

/** FreeformTool is the tool for creating freeform objects. */
public class PolylineTool extends OverlayTool {

  // -- Constants --

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayPolyline line;

  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public PolylineTool(OverlayTransform overlay) {
    super(overlay, "Polyline", "Polyline", "polyline.png");
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    // -- housekeeping
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    double dpx = (double) px;
    double dpy = (double) py;
   
    overlay.notifyListeners(new TransformEvent(overlay));
  } // end mouseDown

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;

    DisplayImpl display = (DisplayImpl) e.getDisplay();

    double dpx = (double) px;
    double dpy = (double) py;
    
    overlay.notifyListeners(new TransformEvent(overlay));
  } // end mouseDrag

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py, 
      float dx, float dy, int[] pos, int mods) {
      overlay.notifyListeners(new TransformEvent(overlay));
  }

  // -- Helper methods -- 

  /** Casts an array of floats to doubles */
  private double[][] floatsToPixelDoubles(DisplayImpl d, float[][] nodes) {
    double[][] nodesDbl = new double[nodes.length][nodes[0].length];
    for (int j=0; j<nodes[0].length; j++) {
      int[] c = DisplayUtil.domainToPixel(d, new double[]{
        (double) nodes[0][j], (double) nodes[1][j]});
      nodesDbl[0][j] = (double) c[0];
      nodesDbl[1][j] = (double) c[1];
    }
    return nodesDbl;
  }

  /** Prints node array of current freeform; for debugging */
  private void printNodes(float[][] nodes) {
    System.out.println("Printing nodes...");
    for (int i = 0; i < nodes[0].length; i++){
      System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
    }
  }

  /** Prints node array of current freeform; for debugging */
  private void printNodes() {
    if (line!=null){
      float[][] nodes = line.getNodes();
      printNodes(nodes);
    }
  }

  /** Prints a message for debugging */
  public void print(String methodName, String message) {
    boolean toggle = true;
    if (toggle) {
      String header = "FreeformTool.";
      String out = header + methodName + ": " + message;
      System.out.println(out);
    }
  }
} // end class FreeformTool
