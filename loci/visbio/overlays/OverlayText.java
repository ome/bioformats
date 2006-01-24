//
// OverlayText.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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

import java.awt.FontMetrics;
import java.rmi.RemoteException;
import visad.*;

/** OverlayText is a text string overlay. */
public class OverlayText extends OverlayObject {


  // -- Constructors --

  /** Constructs an uninitialized text string overlay. */
  public OverlayText(OverlayTransform overlay) { super(overlay); }

  /** Constructs a text string overlay. */
  public OverlayText(OverlayTransform overlay, float x, float y, String text) {
    super(overlay);
    x1 = x;
    y1 = y;
    this.text = text;
    computeGridParameters();
  }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getTextRangeType();

    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;

    FieldImpl field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      Set fieldSet = new SingletonSet(
        new RealTuple(domain, new double[] {x1, y1}));
      field = new FieldImpl(fieldType, fieldSet);
      field.setSample(0, overlay.getTextRangeValue(text, r, g, b), false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    double xdist = 0;
    if (x < x1 && x < x2) xdist = Math.min(x1, x2) - x;
    else if (x > x1 && x > x2) xdist = x - Math.max(x1, x2);
    double ydist = 0;
    if (y < y1 && y < y2) ydist = Math.min(y1, y2) - y;
    else if (y > y1 && y > y2) ydist = y - Math.max(y1, y2);
    return Math.sqrt(xdist * xdist + ydist * ydist);
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return "Text coordinates = (" + x1 + ", " + y1 + ")";
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay object returns text to render. */
  public boolean hasText() { return true; }


  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected void computeGridParameters() {
    // Computing the grid for text overlays is difficult because the size of
    // the overlay depends on the font metrics, which are obtained from an AWT
    // component (in this case, window.getDisplay().getComponent() for a
    // display window), but data transforms have no knowledge of which display
    // windows are currently displaying them.

    // HACK - for now, use this harebrained scheme to estimate the bounds
    int sx = overlay.getScalingValueX();
    int sy = overlay.getScalingValueY();
    float mw = sx / 318f, mh = sy / 640f; // obtained through experimentation
    FontMetrics fm = overlay.getFontMetrics();
    x2 = x1 + mw * fm.stringWidth(text);
    y2 = y1 + mh * fm.getHeight();

    float padx = 0.02f * sx;
    float pady = 0.02f * sy;
    float xx1 = x1 - padx;
    float xx2 = x2 + padx;
    float yy1 = y1 - pady;
    float yy2 = y2 + pady;

    xGrid1 = xx1; yGrid1 = yy1;
    xGrid2 = xx2; yGrid2 = yy1;
    xGrid3 = xx1; yGrid3 = yy2;
    xGrid4 = xx2; yGrid4 = yy2;
    horizGridCount = 2; vertGridCount = 3;
  }


  // -- Object API methods --

  /** Gets a short string representation of this overlay text. */
  public String toString() { return "Text"; }

}
