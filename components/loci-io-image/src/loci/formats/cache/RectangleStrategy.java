//
// RectangleStrategy.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.cache;

/**
 * A rectangle strategy caches planes extending from the the current
 * dimensional position along each axis and combination of axes.
 * For example, if the current position is Z5-C2-T18, the strategy will
 * preload the next and previous focal planes (Z6-C2-T18 and Z4-C2-T18),
 * the next and previous channels (Z5-C3-T18 and Z5-C1-T18),
 * the next and previous time points (Z5-C2-T19 and Z5-C2-T17), and
 * all combinations thereof (Z6-C3-T18, Z6-C1-T18, Z4-C3-T18, Z4-C1-T18,
 * Z6-C3-T19, etc.).
 * <p>
 * Planes closest to the current position are loaded first, with axes
 * prioritized according to the cache strategy's priority settings.
 * <p>
 * To illustrate the rectangle strategy, here is a diagram showing a case
 * in 2D with 35 dimensional positions (5Z x 7T). For both Z and T, order is
 * centered, range is 2, and priority is normal.
 * The numbers indicate the order planes will be cached, with "0"
 * corresponding to the current dimensional position (Z2-T3).
 * <pre>
 *      T  0  1  2  3  4  5  6
 *    Z /---------------------
 *    0 |    24 20  6 14 22
 *    1 |    18 12  2 10 16
 *    2 |     8  4  0  3  7
 *    3 |    17 11  1  9 15
 *    4 |    23 19  5 13 21
 * </pre>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/cache/RectangleStrategy.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/cache/RectangleStrategy.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class RectangleStrategy extends CacheStrategy {

  // -- Constructor --

  /** Constructs a rectangle strategy. */
  public RectangleStrategy(int[] lengths) { super(lengths); }

  // -- CacheStrategy API methods --

  /* @see CacheStrategy#getPossiblePositions() */
  protected int[][] getPossiblePositions() {
    // with sufficient range, any position could be cached
    int[][] p = new int[length()][lengths.length];
    for (int i=0; i<p.length; i++) pos(i, p[i]);
    return p;
  }

}
