//
// CacheStrategy.java
//

package loci.formats.cache;

public abstract class CacheStrategy implements ICacheStrategy {

  // -- Constants --

  public static final int HIGH_PRIORITY = 0;
  public static final int MEDIUM_PRIORITY = 1;
  public static final int LOW_PRIORITY = 2;

  // -- Fields --
 
  protected boolean forwardFirst;
  protected int[] priorities;
  protected int[] forward, backward;
  protected int[] lengths;

  public CacheStrategy(boolean forwardFirst, int[] lengths, 
    int[] forward, int[] backward) 
  {
    this.lengths = lengths;
    this.forward = forward;
    this.backward = backward;
    this.forwardFirst = forwardFirst; 
    priorities = new int[lengths.length];
  }

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

  /* @see ICacheStrategy#getLoadList(int[]) */
  public abstract int[][] getLoadList(int[] pos) throws CacheException; 

}
