//
// RectangleStrategy.java
//

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
 * in 2D with 56 dimensional positions (7Z x 8T). For both Z and T, order is
 * centered, range is 5, and priority is normal.
 * The numbers indicate the order planes will be cached, with "0"
 * corresponding to the current dimensional position.
 * <pre>
 *      T  0  1  2  3  4  5  6  7
 *    Z /------------------------
 *    0 |
 *    1 |    24 20  7 16 23
 *    2 |    19 14  4  9 17
 *    3 |    10  3  0  1  6
 *    4 |    15  8  2  5 12
 *    5 |    22 18 11 13 21
 *    6 |
 * </pre>
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
