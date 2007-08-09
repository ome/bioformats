//
// TransientSelectBox.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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
import java.util.Arrays;
import visad.*;
import visad.util.CursorUtil;

/**
 * TransientSelectBox represents the square that appears in a VisBio display
 * as a user drags the mouse to select overlays.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/TransientSelectBox.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/TransientSelectBox.java">SVN</a></dd></dl>
 */
public class TransientSelectBox {

  // -- Constants --

  /** Sets the transparency of the interior of the select box */
  private static final float ALPHA_VALUE = 0.3f;

  // -- Fields --

  /** Color of box */
  private Color color;

  /** Boundaries of box */
  private int x1, x2, y1, y2;

  /** Parent Transform */
  // not sure if this is useful at all
  private OverlayTransform overlay;

  /** Associated display */
  private DisplayImpl display;

  // -- Constructor --

  /** Constructs a selection box.
   */
  public TransientSelectBox(OverlayTransform overlay, DisplayImpl display,
    int downX, int downY)
  {
    this.overlay = overlay;
    this.display = display;
    x1 = downX;
    x2 = downX;
    y1 = downY;
    y2 = downY;
    // initially box has zero area.
    color = Color.green;
  }

  // -- TransientSelectBox API Methods --

  /** Sets coordinates of draggable box corner. */
  public void setCorner(int x, int y) {
    x2 = x;
    y2 = y;
  }

  /** Returns the display on which this TSB exists. */
  public DisplayImpl getActiveDisplay() { return display; }

  /** Returns a VisAD data object representing this box.
   *  The data object is compound, comprising 2 parts:
   *  1) a solid GriddedSet of manifold dimension 1, the outline
   *  2) a semi-transparent GriddedSet of manifold dimension 2, the interior
   */
  public DataImpl getData() {
    if (x1 == x2 || y1 == y2) return null;  // don't construct a data object
    // for a zero-area box

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    /*
     * 1------2
     * |      |
     * |      |
     * 4------3
     */
    double[] d1 = CursorUtil.pixelToDomain(getActiveDisplay(), x1, y1);
    double[] d2 = CursorUtil.pixelToDomain(getActiveDisplay(), x2, y1);
    double[] d3 = CursorUtil.pixelToDomain(getActiveDisplay(), x2, y2);
    double[] d4 = CursorUtil.pixelToDomain(getActiveDisplay(), x1, y2);

    float[] f1 = {(float) d1[0], (float) d1[1]};
    float[] f2 = {(float) d2[0], (float) d2[1]};
    float[] f3 = {(float) d3[0], (float) d3[1]};
    float[] f4 = {(float) d4[0], (float) d4[1]};

    float[][] shadeSamples = null;
    float[][] outlineSamples = null;
    GriddedSet shadeSet = null;
    GriddedSet outlineSet = null;

    try {
      shadeSamples = new float[][] {
        {f1[0], f2[0], f4[0], f3[0]},
        {f1[1], f2[1], f4[1], f3[1]}
      };

      outlineSamples = new float[][] {
        {f1[0], f2[0], f3[0], f4[0], f1[0]},
        {f1[1], f2[1], f3[1], f4[1], f1[1]}
      };

      shadeSet = new Gridded2DSet(domain,
          shadeSamples, 2, 2, null, null, null, false);

      outlineSet = new Gridded2DSet(domain, outlineSamples,
          outlineSamples[0].length, null, null, null, false);
    }
    catch (SetException set) { set.printStackTrace(); }
    catch (VisADException exc) { exc.printStackTrace(); }

    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;
    float[][] shadeRangeSamples = new float[4][shadeSamples[0].length];
    Arrays.fill(shadeRangeSamples[0], r);
    Arrays.fill(shadeRangeSamples[1], g);
    Arrays.fill(shadeRangeSamples[2], b);
    Arrays.fill(shadeRangeSamples[3], ALPHA_VALUE);

    float[][] outlineRangeSamples = new float[4][outlineSamples[0].length];
    Arrays.fill(outlineRangeSamples[0], r);
    Arrays.fill(outlineRangeSamples[1], g);
    Arrays.fill(outlineRangeSamples[2], b);
    Arrays.fill(outlineRangeSamples[3], 1.0f);

    FlatField inField = null;
    FlatField outField = null;
    DataImpl[] wholeTeam = null;
    Tuple ret = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      // interior field
      inField = new FlatField(fieldType, shadeSet);
      inField.setSamples(shadeRangeSamples);

      // outline field
      outField = new FlatField(fieldType, outlineSet);
      outField.setSamples(outlineRangeSamples);

      // go Mallards
      wholeTeam = new DataImpl[] {inField, outField};
      ret = new Tuple(wholeTeam, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return ret;
  }

  /**
   * Gets domain coordinates of box corners
   * Corners are returned as a double[4][2].
   */
  public double[][] getCornersDomain() {
    /*
     * 0----1
     * |    |
     * |    |
     * 3----2
     */

    double[] p0 = CursorUtil.pixelToDomain(getActiveDisplay(), x1, y1);
    double[] p1 = CursorUtil.pixelToDomain(getActiveDisplay(), x2, y1);
    double[] p2 = CursorUtil.pixelToDomain(getActiveDisplay(), x2, y2);
    double[] p3 = CursorUtil.pixelToDomain(getActiveDisplay(), x1, y2);

    double[][] ret = {{p0[0], p0[1]},
                      {p1[0], p1[1]},
                      {p2[0], p2[1]},
                      {p3[0], p3[1]}};

    return ret;
  }

  /** Gets X coordinate of the first endpoint. */
  public int getX1() { return x1; }

  /** Gets X coordinate of the second endpoint. */
  public int getX2() { return x2; }

  /** Gets Y coordinate of the first endpoint. */
  public int getY1() { return y1; }

  /** Gets Y coordinate of the second endpoint. */
  public int getY2() { return y2; }
}
