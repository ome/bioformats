//
// PointerTool.java
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

import java.awt.event.InputEvent;

/** PointerTool is the tool for manipulating existing overlays. */
public class PointerTool extends OverlayTool {

  // -- Constructor --

  /** Constructs an overlay manipulation tool. */
  public PointerTool(OverlayTransform overlay) {
    super(overlay, "Pointer", "Pointer", "pointer.png");
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;
    System.out.println("PointerTool: mouseDown:" + (shift ? " SHIFT" : "") + (ctrl ? " CTRL" : ""));//TEMP

    // pick nearest object
    OverlayObject[] oo = overlay.getObjects(pos);
    double dist = Double.POSITIVE_INFINITY;
    int ndx = -1;
    for (int i=0; i<oo.length; i++) {
      double d = oo[i].getDistance(x, y);
      if (d < dist) {
        dist = d;
        ndx = i;
      }
    }
    double threshold = 0.02 * overlay.getScalingValue();
    if (dist < threshold) {
      System.out.println("Picked index #" + ndx);//TEMP
      oo[ndx].setSelected(true);
    }
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;
    System.out.println("PointerTool: mouseUp:" + (shift ? " SHIFT" : "") + (ctrl ? " CTRL" : ""));//TEMP
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;
    System.out.println("PointerTool: mouseDrag:" + (shift ? " SHIFT" : "") + (ctrl ? " CTRL" : ""));//TEMP
  }

}
