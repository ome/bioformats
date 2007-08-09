//
// OverlayFreeform.java
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

/**
 * OverlayFreeform is a freeform overlay.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/OverlayFreeform.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/OverlayFreeform.java">SVN</a></dd></dl>
 */

public class OverlayFreeform extends OverlayNodedObject {

  // -- Constructors --

  /** Constructs an uninitialized freeform. */
  public OverlayFreeform(OverlayTransform overlay) { super(overlay); }

  /** Constructs a freeform. */
  public OverlayFreeform(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay, x1, y1, x2, y2);
  }

  /** Constructs a freeform from an array of nodes. */
  public OverlayFreeform(OverlayTransform overlay, float[][] nodes) {
    super(overlay, nodes);
  }

  // -- OverlayObject API methods --

  // -- Internal OverlayObject API methods --

  // -- Object API methods --

  /** Gets a short string representation of this freeform. */
  public String toString() { return "Freeform"; }

}
