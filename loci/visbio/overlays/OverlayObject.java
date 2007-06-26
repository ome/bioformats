//
// OverlayObject.java
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

import java.awt.Color;
import visad.*;

/** OverlayObject is the superclass of all overlay objects. */
public abstract class OverlayObject {

  // -- Constants --

  protected static final float GLOW_WIDTH = 1.0f;

  protected static final float GLOW_ALPHA = 0.15f;

  protected static final Color GLOW_COLOR = Color.YELLOW;

  // -- Fields --

  /** Associated overlay transform. */
  protected OverlayTransform overlay;

  /** Endpoint coordinates. */
  protected float x1, y1, x2, y2;

  /** Text string to render. */
  protected String text;

  /** Color of this overlay. */
  protected Color color;

  /** Flag indicating overlay is solid. */
  protected boolean filled;

  /** Group to which this overlay belongs. */
  protected String group;

  /** Notes associated with this overlay. */
  protected String notes;

  /** Flag indicating this overlay is currently selected. */
  protected boolean selected = true;

  /** Flag indicating this overlay is still being initially drawn. */
  protected boolean drawing = true;

  /** Top-left endpoint of selection grid rectangle. */
  protected float xGrid1, yGrid1;

  /** Top-right endpoint of selection grid rectangle. */
  protected float xGrid2, yGrid2;

  /** Bottom-left endpoint of selection grid rectangle. */
  protected float xGrid3, yGrid3;

  /** Bottom-right endpoint of selection grid rectangle. */
  protected float xGrid4, yGrid4;

  /** Number of horizontal and vertical dividing lines for selection grid. */
  protected int horizGridCount, vertGridCount;

  // -- Constructor --

  /** Constructs an overlay. */
  public OverlayObject(OverlayTransform overlay) {
    this.overlay = overlay;
    overlay.setTextDrawn(false);
  }

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public abstract DataImpl getData();

  /** Computes the shortest distance from this overlay to the given point. */
  public abstract double getDistance(double x, double y);

  /** Returns whether this object is drawable, i.e., is of nonzero
   *  size, area, length, etc.
   */
  public abstract boolean hasData();

  /** Gets a specific overlay statistic */
  public abstract String getStat(String name);

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    String name = getClass().getName();
    String pack = getClass().getPackage().getName();
    if (name.startsWith(pack)) name = name.substring(pack.length() + 1);
    return "No statistics for " + name;
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return false; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return false; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return false; }

  /** True iff this overlay can be resized using X1, X2, Y1, Y2 entry boxes */
  public boolean areBoundsEditable() { return true; }
  // currently, only non-noded objects can be resized this way.
  // (Actually could perform some rad scaling on all nodes)

  /** True iff this overlay returns text to render. */
  public boolean hasText() { return false; }

  /** Changes X coordinate of the overlay's first endpoint. */
  public void setX(float x1) {
    if (!hasEndpoint()) return;
    this.x1 = x1;
  }

  /** Changes Y coordinate of the overlay's first endpoint. */
  public void setY(float y1) {
    if (!hasEndpoint()) return;
    this.y1 = y1;
  }

  /** Changes coordinates of the overlay's first endpoint. */
  public void setCoords(float x1, float y1) {
    if (!hasEndpoint()) return;
    this.x1 = x1;
    this.y1 = y1;
  }

  /** Gets X coordinate of the overlay's first endpoint. */
  public float getX() { return x1; }

  /** Gets Y coordinate of the overlay's first endpoint. */
  public float getY() { return y1; }

   /** Changes X coordinate of the overlay's second endpoint. */
  public void setX2(float x2) {
    if (!hasEndpoint2()) return;
    this.x2 = x2;
  }

  /** Changes Y coordinate of the overlay's second endpoint. */
  public void setY2(float y2) {
    if (!hasEndpoint2()) return;
    this.y2 = y2;
  }

  /** Changes coordinates of the overlay's second endpoint. */
  public void setCoords2(float x2, float y2) {
    if (!hasEndpoint2()) return;
    this.x2 = x2;
    this.y2 = y2;
  }

  /** Gets X coordinate of the overlay's second endpoint. */
  public float getX2() { return x2; }

  /** Gets Y coordinate of the overlay's second endpoint. */
  public float getY2() { return y2; }

  /** Changes text to render. */
  public void setText(String text) {
    if (!hasText()) return;
    this.text = text;
  }

  /** Gets text to render. */
  public String getText() { return text; }

  /** Sets color of this overlay. */
  public void setColor(Color c) { color = c; }

  /** Gets color of this overlay. */
  public Color getColor() { return color; }

  /** Sets whether overlay is solid. */
  public void setFilled(boolean filled) {
    if (canBeFilled()) this.filled = filled;
  }

  /** Gets whether overlay is solid. */
  public boolean isFilled() { return filled; }
  
  /** Gets whether overlay is scalable. */
  public boolean isScalable() { return false; }

  /** Rescales this overlay object. */
  public void rescale(float multiplier) {}

  /** Sets group to which this overlay belongs. */
  public void setGroup(String group) { this.group = group; }

  /** Gets group to which this overlay belongs. */
  public String getGroup() { return group; }

  /** Sets notes for this overlay. */
  public void setNotes(String text) { notes = text; }

  /** Gets notes for this overlay. */
  public String getNotes() { return notes; }

  /** Sets whether this overlay is currently selected. */
  public void setSelected(boolean selected) { this.selected = selected; }

  /** Gets whether this overlay is currently selected. */
  public boolean isSelected() { return selected; }

  /** Sets whether this overlay is still being initially drawn. */
  public void setDrawing(boolean drawing) {
    this.drawing = drawing;
    overlay.setTextDrawn(!drawing);
  }

  /** Gets whether this overlay is still being initially drawn. */
  public boolean isDrawing() { return drawing; }

  // -- Internal OverlayObject API methods --

  /** Sets value of largest and smallest x, y values. */
  protected void setBoundaries(float x, float y) {
    x1 = Math.min(x1, x);
    x2 = Math.max(x2, x);
    y1 = Math.min(y1, y);
    y2 = Math.max(y2, y);
  }
}// end class
