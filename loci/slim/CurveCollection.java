//
// CurveCollection.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.slim;

/**
 * Data structure for managing a collection of curves. The main purpose of this
 * structure is to create subsampled curve sets at a resolution for each order
 * of magnitude&mdash;e.g., for a 256 x 256 image, the collection constructs
 * 128 x 128, 64 x 64, 32 x 32, 16 x 16, 8 x 8, 4 x 4, 2 x 2 and 1 x 1
 * subsampled images&mdash;by summing neighboring curves.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/CurveCollection.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/CurveCollection.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class CurveCollection {

  // -- Constants --

  private static final double LOG2 = Math.log(2);

  // -- Fields --

  /** Curve fit data, dimensioned [maxDepth][numRows][numCols]. */
  protected CurveFitter[][][] curves;

  // -- Constructor --

  /**
   * Creates an object to manage the given collection of curves.
   *
   * @param cf Array of curve fitters dimensioned [numRows][numCols].
   *
   * @throws IllegalArgumentException
   *  if numRows or numCols is not a power of two or numRows != numCols
   */
  public CurveCollection(CurveFitter[][] cf) {
    int numRows = cf.length;
    int numCols = cf[0].length;
    if (numRows != numCols) {
      throw new IllegalArgumentException("Row and column counts do not match");
    }
    double log = Math.log(numRows) / LOG2;
    int depth = (int) log;
    if (log != depth) {
      throw new IllegalArgumentException("Row count is not a power of two");
    }
    curves = new CurveFitter[depth + 1][][];
    curves[0] = cf;

    // compute subsamplings
    for (int i=1; i<=depth; i++) {
      curves[i] = null;//TODO
    }
  }

  // -- CurveCollection API methods --

  /** Gets the collection of curves at full resolution. */
  public CurveFitter[][] getCurves() { return getCurves(0); }

  /**
   * Gets the collection of curves subsampled at the given depth.
   * For example, for a 256 x 256 image, getCurves(2) returns a 64 x 64
   * subsampling.
   *
   * @throws IllegalArgumentException
   *   if the subsampling depth is greater than {@link #getSubsamplingDepth}
   */
  public CurveFitter[][] getCurves(int depth) {
    if (depth < 0 || depth > getSubsamplingDepth()) {
      throw new IllegalArgumentException("Invalid subsampling depth " +
        "(expected 0 <= depth <= " + getSubsamplingDepth());
    }
    return curves[depth];
  }

  /**
   * Gets the maximum subsampling depth of the curve collection. For example,
   * for a 256 x 256 image, the depth is 8 because there exist subsamplings at
   * 128 x 128, 64 x 64, 32 x 32, 16 x 16, 8 x 8, 4 x 4, 2 x 2 and 1 x 1.
   */
  public int getSubsamplingDepth() { return curves.length - 1; }

}
