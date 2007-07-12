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

  int CENTERED_ORDER = 0;
  int FORWARD_ORDER = 1;
  int BACKWARD_ORDER = -1;

  // -- ICacheStrategy API methods --

  /**
   * Gets the indices of the objects to cache,
   * surrounding the object with the given index.
   */
  int[][] getLoadList(int[] pos) throws CacheException;

  /** Retrieves the priority for caching each axis. */
  int[] getPriorities();

  /** Sets the priority for caching the given axis. */
  void setPriority(int priority, int axis);

  /**
   * Retrieves the order in which slices should be loaded along each axis.
   * @return An array whose constituents are each one of:<ul>
   *   <li>CENTERED_ORDER</li>
   *   <li>FORWARD_ORDER</li>
   *   <li>BACKWARD_ORDER</li>
   */
  int[] getOrder();

  /**
   * Sets the order in which slices should be loaded along each axis.
   * @param order One of:<ul>
   *   <li>CENTERED_ORDER</li>
   *   <li>FORWARD_ORDER</li>
   *   <li>BACKWARD_ORDER</li>
   * @param axis The axis for which to set the order.
   */
  void setOrder(int order, int axis);

  /** Retrieves the number of planes to cache along each axis. */
  int[] getRange();

  /** Sets the number of planes to cache along the given axis. */
  void setRange(int planes, int axis);

  /** Gets the lengths of all the axes. */
  int[] getLengths();

}
