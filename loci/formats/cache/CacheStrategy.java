//
// CacheStrategy.java
//

package loci.formats.cache;

import java.util.Arrays;

public abstract class CacheStrategy implements ICacheStrategy {

  // -- Constants --

  public static final int DEFAULT_FORWARD = 20;
  public static final int DEFAULT_BACKWARD = 10;

  // -- Fields --
 
  /** Length of each dimensional axis. */
  protected int[] lengths;

  /** Whether or not forward slices should be loaded first. */
  protected boolean forwardFirst;

  /** Number of planes to cache forward along each axis. */
  protected int[] forward;

  /** Number of planes to cache backward along each axis. */
  protected int[] backward;

  /** Priority for caching each axis. Controls axis caching order. */
  protected int[] priorities;

  // -- Constructors --

  public CacheStrategy(int[] lengths) {
    this.lengths = lengths;
    forwardFirst = true;
    forward = new int[lengths.length];
    Arrays.fill(forward, DEFAULT_FORWARD);
    backward = new int[lengths.length];
    Arrays.fill(backward, DEFAULT_BACKWARD);
    priorities = new int[lengths.length];
    Arrays.fill(priorities, NORMAL_PRIORITY);
  }

  // -- Abstract ICacheStrategy API methods --

  /* @see ICacheStrategy#getLoadList(int[]) */
  public abstract int[][] getLoadList(int[] pos) throws CacheException; 

  // -- ICacheStrategy API methods --

  /* @see ICacheStrategy#getPriorities() */
  public int[] getPriorities() { return priorities; }

  /* @see ICacheStrategy#setPriority() */
  public void setPriority(int priority, int axis) {
    priorities[axis] = priority; 
  }

  /* @see ICacheStrategy#getForwardFirst() */
  public boolean getForwardFirst() { return forwardFirst; }

  /* @see ICacheStrategy#setForwardFirst(boolean) */
  public void setForwardFirst(boolean forwardFirst) {
    this.forwardFirst = forwardFirst;
  }

  /* @see ICacheStrategy#getForward() */
  public int[] getForward() { return forward; }

  /* @see ICacheStrategy#setForward(int, int) */
  public void setForward(int planes, int axis) {
    forward[axis] = planes;
  }

  /* @see ICacheStrategy#getBackward() */
  public int[] getBackward() { return backward; }

  /* @see ICacheStrategy#setBackward(int, int) */
  public void setBackward(int planes, int axis) {
    backward[axis] = planes;
  }

  /* @see ICacheStrategy#getLengths() */
  public int[] getLengths() { return lengths; }

  // -- Utility methods --

  public static int nextPriority(int ndx, int[] priorities) {
    int basePriority = priorities[ndx];
    for (int i=0; i<priorities.length; i++) {
      if (priorities[i] >= basePriority && i != ndx) {
        if (priorities[i] != basePriority || i >= ndx) { 
          return i;
        } 
      }
    }
    return -1; 
  }

}
