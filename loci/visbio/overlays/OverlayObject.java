//
// OverlayObject.java
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

import java.awt.Color;

import visad.DataImpl;

/** OverlayObject is the superclass of all overlay objects. */
public abstract class OverlayObject {

  // -- Fields --

  /** Associated overlay transform. */
  protected OverlayTransform overlay;

  /** Flag indicating this overlay is currently selected. */
  protected boolean selected;

  /** Color of this overlay. */
  protected Color color;

  /** Group to which this overlay belongs. */
  protected String group;

  /** Description of this overlay. */
  protected String description;


  // -- Constructor --

  /** Constructs a measurement line creation tool. */
  public OverlayObject(OverlayTransform overlay) { this.overlay = overlay; }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public abstract DataImpl getData();

  /** Computes the shortest distance from this object to the given point. */
  public abstract double getDistance(double x, double y);

  /** Sets whether this overlay is currently selected. */
  public void setSelected(boolean selected) { this.selected = selected; }

  /** Gets whether this overlay is currently selected. */
  public boolean isSelected() { return selected; }

  /** Sets color of this overlay. */
  public void setColor(Color c) { color = c; }

  /** Gets color of this overlay. */
  public Color getColor() { return color; }

  /** Sets group to which this overlay belongs. */
  public void setGroup(String group) { this.group = group; }

  /** Gets group to which this overlay belongs. */
  public String getGroup() { return group; }

  /** Sets description of this overlay. */
  public void setDescription(String text) { description = text; }

  /** Gets description of this overlay. */
  public String getDescription() { return description; }

}
