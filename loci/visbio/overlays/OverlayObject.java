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

import java.rmi.RemoteException;

import visad.*;

/** OverlayObject is the superclass of all overlay objects. */
public abstract class OverlayObject {

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
  public OverlayObject(OverlayTransform overlay) { this.overlay = overlay; }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public abstract DataImpl getData();

  /** Computes the shortest distance from this overlay to the given point. */
  public abstract double getDistance(double x, double y);

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    String name = getClass().getName();
    String pack = getClass().getPackage().getName();
    if (name.startsWith(pack)) name = name.substring(pack.length() + 1);
    return "No statistics for " + name;
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint1() { return false; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return false; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return false; }

  /** True iff this overlay returns text to render. */
  public boolean hasText() { return false; }

  /**
   * Computes a grid to be superimposed on this overlay
   * to indicate it is currently selected.
   */
  public DataImpl getSelectionGrid() {
    // Grid computation relies on parameters:
    //   (xGrid1, yGrid1): top-left endpoint of grid rectangle
    //   (xGrid2, yGrid2): top-right endpoint of grid rectangle
    //   (xGrid3, yGrid3): bottom-left endpoint of grid rectangle
    //   (xGrid4, yGrid4): bottom-right endpoint of grid rectangle
    //     horizGridCount: number of horizontal dividing lines
    //      vertGridCount: number of vertical dividing lines

    // compute (X, Y) values along grid left and right edges
    int numLR = horizGridCount + 2;
    float[] xl = new float[numLR];
    float[] xr = new float[numLR];
    float[] yl = new float[numLR];
    float[] yr = new float[numLR];
    for (int i=0; i<numLR; i++) {
      float q = (float) i / (numLR - 1);
      xl[i] = q * (xGrid3 - xGrid1) + xGrid1;
      xr[i] = q * (xGrid4 - xGrid2) + xGrid2;
      yl[i] = q * (yGrid3 - yGrid1) + yGrid1;
      yr[i] = q * (yGrid4 - yGrid2) + yGrid2;
    }

    // compute (X, Y) values along grid top and bottom edges
    int numTB = vertGridCount + 2;
    float[] xt = new float[numTB];
    float[] xb = new float[numTB];
    float[] yt = new float[numTB];
    float[] yb = new float[numTB];
    for (int i=0; i<numTB; i++) {
      float q = (float) i / (numTB - 1);
      xt[i] = q * (xGrid2 - xGrid1) + xGrid1;
      xb[i] = q * (xGrid4 - xGrid3) + xGrid3;
      yt[i] = q * (yGrid2 - yGrid1) + yGrid1;
      yb[i] = q * (yGrid4 - yGrid3) + yGrid3;
    }

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    // compute zig-zagging grid mesh values
    int count = 2 * (numLR + numTB) - 1;
    float[][] setSamples = new float[2][count];
    for (int i=0; i<numLR; i++) {
      int ndx = 2 * i;
      boolean dir = i % 2 == 0;
      setSamples[0][ndx] = dir ? xl[i] : xr[i];
      setSamples[1][ndx] = dir ? yl[i] : yr[i];
      setSamples[0][ndx + 1] = dir ? xr[i] : xl[i];
      setSamples[1][ndx + 1] = dir ? yr[i] : yl[i];
    }
    boolean leftToRight = numLR % 2 == 0;
    for (int i=0; i<numTB; i++) {
      int ndx = 2 * (numLR + i) - 1;
      boolean dir = i % 2 == 0;
      int ii = leftToRight ? i : (numTB - i - 1);
      setSamples[0][ndx] = dir ? xb[ii] : xt[ii];
      setSamples[1][ndx] = dir ? yb[ii] : yt[ii];
      setSamples[0][ndx + 1] = dir ? xt[ii] : xb[ii];
      setSamples[1][ndx + 1] = dir ? yt[ii] : yb[ii];
    }

    // populate grid color values
    Color c = Color.yellow;
    float r = c.getRed() / 255f;
    float g = c.getGreen() / 255f;
    float b = c.getBlue() / 255f;
    float[][] fieldSamples = new float[3][count];
    for (int i=0; i<count; i++) {
      fieldSamples[0][i] = r;
      fieldSamples[1][i] = g;
      fieldSamples[2][i] = b;
    }

    // construct field
    FlatField field = null;
    try {
      GriddedSet fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(fieldSamples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Changes coordinates of the overlay's first endpoint. */
  public void setCoords(float x1, float y1) {
    this.x1 = x1;
    this.y1 = y1;
    computeGridParameters();
  }

  /** Gets X coordinate of the overlay's first endpoint. */
  public float getX() { return x1; }

  /** Gets Y coordinate of the overlay's first endpoint. */
  public float getY() { return y1; }

  /** Changes coordinates of the overlay's second endpoint. */
  public void setCoords2(float x2, float y2) {
    this.x2 = x2;
    this.y2 = y2;
    computeGridParameters();
  }

  /** Gets X coordinate of the overlay's second endpoint. */
  public float getX2() { return x2; }

  /** Gets Y coordinate of the overlay's second endpoint. */
  public float getY2() { return y2; }

  /** Changes text to render. */
  public void setText(String text) {
    this.text = text;
    computeGridParameters();
  }

  /** Gets text to render. */
  public String getText() { return text; }

  /** Sets color of this overlay. */
  public void setColor(Color c) { color = c; }

  /** Gets color of this overlay. */
  public Color getColor() { return color; }

  /** Sets whether overlay is solid. */
  public void setFilled(boolean filled) { this.filled = filled; }

  /** Gets whether overlay is solid. */
  public boolean isFilled() { return filled; }

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
  public void setDrawing(boolean drawing) { this.drawing = drawing; }

  /** Gets whether this overlay is still being initially drawn. */
  public boolean isDrawing() { return drawing; }


  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected abstract void computeGridParameters();

}
