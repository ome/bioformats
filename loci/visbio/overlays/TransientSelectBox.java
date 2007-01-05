//
// TransientSelectBox.java
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

import java.rmi.RemoteException;
import java.util.Arrays;
import visad.*;
import java.awt.Color;

/** TransientSelectBox represents the square that appears in a VisBio display
 *  as a user drags the mouse to select overlays */
public class TransientSelectBox {

  // -- Fields --
  
  /** Color of box */
  private Color color;

  /** Boundaries of box */
  private float x1, x2, y1, y2;

  /** Parent Transform */
  private OverlayTransform overlay;

  /** Whether the box is visible; toggled off if box
   *  boundaries would form an invalid GriddedSet */
  private boolean visible;
  
  // -- Constants -- 
  /** Sets the transparency of the interior of the select box */
  private float ALPHA_VALUE = 0.3f;

  // -- Constructor -- 

  /** Constructs a selection box 
   *  Initially, the box has zero area and is not visible
   */
  public TransientSelectBox(OverlayTransform overlay, float downX, float downY) {
    this.overlay = overlay;
    x1 = downX;
    x2 = downX;
    y1 = downY;
    y2 = downY;
    color = Color.green;
    visible = false;
  }

  // -- TransientSelectBox API Methods -- 

  /** Sets coordinates of draggable box corner */
  public void setCorner (float x, float y) {
    x2 = x;
    y2 = y;
    // toggle visible.  If x1 == x2 || y1 == y2, GriddedSet invalid.
    if (x1 != x2 && y1 != y2) visible = true;
    else visible = false;
  }

  /** Returns a VisAD data object representing this box 
   *  The data object is compound, consisting of 2 parts:
   *  1) a solid GriddedSet of manifold dimension 1, the outline
   *  2) a semi-transparent GriddedSet of manifold dimension 2, the interior
   */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] shadeSamples = null;
    float[][] outlineSamples = null;
    GriddedSet shadeSet = null;
    GriddedSet outlineSet = null;

    try {
      shadeSamples = new float[][] {
        {x1, x2, x1, x2}, 
        {y1, y1, y2, y2}
      };

      outlineSamples = new float[][] {
        {x1, x1, x2, x2, x1}, 
        {y1, y2, y2, y1, y1}
      };
    
      shadeSet = new Gridded2DSet(domain,
          shadeSamples, 2, 2, null, null, null, false);

      outlineSet = new Gridded2DSet(domain, outlineSamples, 
          outlineSamples[0].length, null, null, null, false);
    }
    catch (SetException set ) { set.printStackTrace(); }
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
  
      wholeTeam = new DataImpl[] {inField, outField};
      ret = new Tuple (wholeTeam, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return ret;
  }

  /** Whether this select box is visible */
  public boolean isVisible() { return visible; }

  /** Gets X coordinate of the overlay's first endpoint. */
  public float getX1() { return x1; } 

  /** Gets X coordinate of the overlay's second endpoint. */
  public float getX2() { return x2; } 
  
  /** Gets Y coordinate of the overlay's second endpoint. */
  public float getY1() { return y1; } 
  
  /** Gets Y coordinate of the overlay's first endpoint. */
  public float getY2() { return y2; } 
}
