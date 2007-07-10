//
// ICacheStrategy.java
//

package loci.formats.cache;

public interface ICacheStrategy {

  // -- Constants --

  int MIN_PRIORITY = -10;
  int LOW_PRIORITY = -5;
  int NORMAL_PRIORITY = 0;
  int HIGH_PRIORITY = 5;
  int MAX_PRIORITY = 10;

  // -- ICacheStrategy API methods --

  /** 
   * Get the indices of the objects to cache, 
   * surrounding the object with the given index.
   */
  int[][] getLoadList(int[] pos) throws CacheException;

  /** Retrieve the priority for caching each axis. */
  int[] getPriorities();

  /** Set the priority for caching the given axis. */
  void setPriority(int priority, int axis);

  /** Return true if forward slices should be loaded first. */
  boolean getForwardFirst();

  /** Set whether or not forward slices should be loaded first. */
  void setForwardFirst(boolean forwardFirst);

  /** Retrieve the number of planes to cache forward along each axis. */
  int[] getForward();

  /** Set the number of planes to cache forward along the given axis. */
  void setForward(int planes, int axis);

  /** Retrieve the number of planes to cache backward along each axis. */
  int[] getBackward();

  /** Set the number of planes to cache backward along the given axis. */
  void setBackward(int planes, int axis);

  /** Get the lengths of all the axes. */
  int[] getLengths();

}
