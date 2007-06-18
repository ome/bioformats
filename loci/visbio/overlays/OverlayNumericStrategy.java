//
// OverlayNumericStrategy.java
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

public class OverlayNumericStrategy {

  // -- Fields --
  
  /**
   * When drawing or editing, how far mouse
   * must be dragged before new node is added.
   */
  protected static final double DRAW_THRESH = 2.0;

  /**
   * How close a mouseDrag event must be to a node
   * in order to erase it
   */
  protected static final double ERASE_THRESH = 10.0;

  /** Threshhold within which click must occur to invoke edit mode. */
  protected static final double EDIT_THRESH = 6.0;

  /**
   * Threshhold within which click must occur to
   * invoke extend mode or reconnect a freeformExtension.
   */
  protected static final double RECONNECT_THRESH = 1.0;

  /** How close mouse must be to end node to resume drawing */
  protected static final double RESUME_THRESH = 10.0;

  /** Smoothing factor for "single exponential smoothing" */
  protected static final float SMOOTHING_FACTOR = 0.35f;

  /** Maximum distance (in pixels) mouse can be from a node to be considered
   *  pointing to it. */
  protected static final float POLYLINE_THRESH = 7.0f;

  // -- Constructor --
  
  /** Private constructor--ensure this object is not instantiated. */
  private OverlayNumericStrategy() {}

  // -- Object API Methods --

  /** Returns the drawing threshold. */
  public static double getDrawThreshold() { return DRAW_THRESH; }

  /** Returns the erasing threshold. */
  public static double getEraseThreshold() { return ERASE_THRESH; }

  /** Returns the editing threshold. */
  public static double getEditThreshold() { return EDIT_THRESH; }

  /** Returns the resume threshold. */
  public static double getResumeThreshold() { return RESUME_THRESH; }

  /** Returns the reconnect threshold. */
  public static double getReconnectThreshold() { return RECONNECT_THRESH; }

  /** Returns the single exponential smoothing factor. */
  public static double getSmoothingFactor() { return SMOOTHING_FACTOR; }
  
  /** Returns the polyline threshold. */
  public static double getPolylineThreshold() { return POLYLINE_THRESH; }
}
