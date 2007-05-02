//
// OverlayStat.java
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

/** OverlayStat is an arrow wedge overlay. */
public class OverlayStat {

  // -- Static Fields --
  /** List of all OverlayObject subclasses */
  public static final String[] OVERLAY_TYPES =
    {"Line", "Freeform", "Marker", "Text",
    "Oval", "Box", "Arrow", "Polyline"};

  // -- Fields --

  /** The name of this statistic (e.g., 'Curve Length (microns)') */
  protected String name;

  /** The value of this statistic in formatted string form */
  protected String value;

  // -- Constructors --

  /** Constructs an OverlayStat object */
  public OverlayStat(String name, String value) {
    this.name = name;
    this.value = value;
  }

  // -- Static OverlayStat API methods --
  /** Get list of all OverlayObject subclasses */
  public static String[] getOverlayTypes() { return OVERLAY_TYPES; }

  /**
   * Returns statistic names for a particular class
   */
  protected static String[] getStatTypes(String overlayType) {
    String[] statTypes = null;
    if (overlayType.equals("Arrow")) statTypes =
      OverlayArrow.getStatTypes();
    else if (overlayType.equals("Box")) statTypes =
      OverlayBox.getStatTypes();
    else if (overlayType.equals("Freeform")) statTypes =
      OverlayFreeform.getStatTypes();
    else if (overlayType.equals("Line")) statTypes =
      OverlayLine.getStatTypes();
    else if (overlayType.equals("Marker")) statTypes =
      OverlayMarker.getStatTypes();
    else if (overlayType.equals("Oval")) statTypes =
      OverlayOval.getStatTypes();
    else if (overlayType.equals("Polyline"))statTypes =
      OverlayPolyline.getStatTypes();
    else if (overlayType.equals("Text")) statTypes =
      OverlayText.getStatTypes();
    return statTypes;
  }

  // -- OverlayStat API methods --

  /** Gets the name of this OverlayStat. */
  public String getName() { return name; }

  /** Gets the value of this OverlayStat. */
  public String getValue() { return value; }

  // -- Object API methods --

  /** Gets a short string representation of this overlay arrow. */
  public String toString() { return name + " [" + value + "]"; }

}
