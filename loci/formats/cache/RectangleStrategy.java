//
// RectangleStrategy.java
//

package loci.formats.cache;

import java.util.Arrays;
import java.util.Vector;

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
 * in 2D with 56 dimensional positions (7Z x 8T). Forward first option is set
 * to false, forward value is 2 for both Z and T, backward value is also 2 for
 * both, and priority is normal for both.
 * The numbers indicate the order planes will be cached, with "0"
 * corresponding to the current dimensional position.
 * <pre>
 *      T  0  1  2  3  4  5  6  7
 *    Z /------------------------
 *    0 |
 *    1 |    24 16 12 15 23
 *    2 |    20  8  4  7 19
 *    3 |    10  2  0  1  9
 *    4 |    18  6  3  5 17
 *    5 |    22 14 11 13 21
 *    6 |
 * </pre>
 */
public class RectangleStrategy extends CacheStrategy {

  // -- Static fields --

  /** Persistent Vector for calling {@link #getLoadList(int[])} efficiently. */
  private static ThreadLocal listVector = new ThreadLocal() {
    protected synchronized Object initialValue() {
      return new Vector();
    }
  };

  // -- Fields --

  private int[] begins;
  private int[] ends;

  // -- Constructor --

  /** Constructs a rectangle strategy. */
  public RectangleStrategy(int[] lengths) { super(lengths); }

  // -- ICacheStrategy API methods --

  /* @see loci.formats.cache.ICacheStrategy#getLoadList(int[]) */
  public int[][] getLoadList(int[] pos) throws CacheException {
    int[] positions = new int[pos.length];
    begins = new int[pos.length];
    ends = new int[pos.length];

    int total = 1;

    for (int i=0; i<pos.length; i++) {
      positions[i] = pos[i];
      begins[i] = pos[i] - backward[i];
      ends[i] = pos[i] + forward[i];
      if (begins[i] < 0) begins[i] = 0;
      total *= (ends[i] - begins[i] + 1); 
      if (begins[i] < 0) begins[i] += lengths[i];
      if (ends[i] >= lengths[i]) ends[i] %= lengths[i];
    }

    int count = 0;

    Vector list = (Vector) listVector.get();
    list.clear();

    int firstNdx = 0;
    for (int i=0; i<priorities.length; i++) {
      if (priorities[i] == HIGH_PRIORITY) {
        firstNdx = i;
        break;
      }      
    }

    while (count < total) {
      int[] tmp = new int[positions.length];
      System.arraycopy(positions, 0, tmp, 0, positions.length);
      list.add(tmp);
      positions = increment(positions, firstNdx);
      if (Arrays.equals(positions, pos)) {
        firstNdx = nextPriority(firstNdx, priorities);
      }
      if (firstNdx == -1) {
        count = total;
        break;
      }
      count++; 
    }

    return (int[][]) list.toArray(new int[0][0]);
  }

  // -- Helper methods --
   
  private int[] increment(int[] pos, int ndx) {
    if (ndx == -1) return pos; 
    if (forwardFirst) {
      pos[ndx]++;
      if (pos[ndx] == lengths[ndx]) pos[ndx] = 0; 
      if (pos[ndx] > ends[ndx]) {
        pos[ndx] = begins[ndx];
        return increment(pos, nextPriority(ndx, priorities));
      }
    }
    else {
      pos[ndx]--;
      if (pos[ndx] == -1) pos[ndx] = lengths[ndx] - 1; 
      if (pos[ndx] < begins[ndx]) {
        pos[ndx] = ends[ndx];
        return increment(pos, nextPriority(ndx, priorities));
      }
    }
    return pos; 
  }

}
