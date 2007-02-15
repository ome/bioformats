//
// OverlayPolyline.java
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

import visad.*;

/**
 * OverlayFreeform is a freeform overlay.
 */

public class OverlayPolyline extends OverlayNodedObject {

  // -- Constructors --

  /** Constructs an uninitialized freeform. */
  public OverlayPolyline(OverlayTransform overlay) { super(overlay); }

  /** Constructs a freeform. */
  public OverlayPolyline(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay, x1, y1, x2, y2);
  }

  /** Constructs a freeform from an array of nodes */
  public OverlayPolyline(OverlayTransform overlay, float[][] nodes) {
    super(overlay, nodes);
  }

  // -- OverlayObject API methods --

  // -- Internal OverlayObject API methods --

  // -- Object API methods --

  /** Gets a short string representation of this freeform. */
  public String toString() { return "Polyline"; }
  
  /**
   * Computes a grid to be superimposed on this overlay
   * to indicate it is currently selected.
   */
  public DataImpl getSelectionGrid() { return getSelectionGrid(false); }

  /**
   * Computes a grid to be superimposed on this overlay to
   * indicate it is currently selected.
   * If the outline flag is set, computes only an outline instead.
   */
  public DataImpl getSelectionGrid(boolean outline) {
    return super.getSelectionGrid(outline);
    /*
    try {
      RealType index = new RealType("index");
      Set indexSet = new Integer1DSet(index, numNodes);
    }
    catch (VisADException ex) {}
    float[][] setSamples = nodes;

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();
    FunctionType indexXY = (index, domain);
    FlatField(indexXY, indexSet);

    System.out.println(domain);
    System.out.println(range);
    return null;
    */

    /*
    float[][] setSamples = new float[nodes.length][numNodes];
    setSamples[0][0] = nodes[0][0]; // for now
    setSamples[1][0] = nodes[1][0];
    for (int j=1; j<numNodes-1; j++) {
      float x = nodes[0][j];
      float y = nodes[1][j];
      float dx = nodes[0][j+1] - nodes[0][j-1];
      float dy = nodes[1][j+1] - nodes[1][j-1]; // slope of perpendicular
      float r = (float) Math.sqrt(dx*dx + dy*dy);
      float scale = 1.0f;
      float newX =  x + scale * dy / r;
      float newY = y + scale * dx / r;
      setSamples[0][j] = newX;
      setSamples[1][j] = newY;
    }
    setSamples[0][numNodes-1] = nodes[0][numNodes-1];
    setSamples[0][numNodes-1] = nodes[1][numNodes-1];

    Color yellow = Color.YELLOW;
    float r = yellow.getRed() / 255f;
    float g = yellow.getGreen() / 255f;
    float b = yellow.getBlue() / 255f;

    System.out.println ("r = " + r + " g = " + g + " b = " + b ); //TEMP 

    FlatField field = null;
    try {
      SampledSet fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
      if (filled) {
        Irregular2DSet roiSet =
          DelaunayCustom.fillCheck((Gridded2DSet) fieldSet, false);
        if (roiSet != null) fieldSet = roiSet;
      }

      int len = fieldSet.getSamples(false)[0].length;
      float[][] rangeSamples = new float[4][len];
      Arrays.fill(rangeSamples[0], r);
      Arrays.fill(rangeSamples[1], g);
      Arrays.fill(rangeSamples[2], b);
      Arrays.fill(rangeSamples[3], 1.0f);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;

    // Grid computation relies on parameters:
    //   (xGrid1, yGrid1): top-left endpoint of grid rectangle
    //   (xGrid2, yGrid2): top-right endpoint of grid rectangle
    //   (xGrid3, yGrid3): bottom-left endpoint of grid rectangle
    //   (xGrid4, yGrid4): bottom-right endpoint of grid rectangle
    //     horizGridCount: number of horizontal dividing lines
    //      vertGridCount: number of vertical dividing lines

    /*
    // compute (X, Y) values along grid left and right edges
    int numLR = outline ? 2 : (horizGridCount + 2);
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
    int numTB = outline ? 2 : (vertGridCount + 2);
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
    Color c = outline ? Color.cyan : Color.yellow;
    float r = c.getRed() / 255f;
    float g = c.getGreen() / 255f;
    float b = c.getBlue() / 255f;
    float[][] fieldSamples = new float[4][count];
    for (int i=0; i<count; i++) {
      fieldSamples[0][i] = r;
      fieldSamples[1][i] = g;
      fieldSamples[2][i] = b;
      fieldSamples[3][i] = 1.0f;
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
    */
  }

}
