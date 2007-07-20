//
// ICacheStrategy.java
//

package loci.formats.cache;

/**
 * Interface for cache strategies. A cache strategy identifies which
 * objects should be loaded into the cache, in which order. Unlike
 * {@link ICacheSource}, it works with multidimensional (N-D) position arrays
 * rather than rasterized (1-D) indices. The two are made equivalent via a
 * mapping between the two, invoked within {@link Cache} as needed.
 */
public interface ICacheStrategy extends CacheReporter {

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
   * surrounding the object at the given position.
   */
  int[][] getLoadList(int[] pos) throws CacheException;

  /** Retrieves the priority for caching each axis. */
  int[] getPriorities();

  /** Sets the priority for caching the given axis. */
  void setPriority(int priority, int axis);

  /**
   * Retrieves the order in which objects should be loaded along each axis.
   * @return An array whose constituents are each one of:<ul>
   *   <li>CENTERED_ORDER</li>
   *   <li>FORWARD_ORDER</li>
   *   <li>BACKWARD_ORDER</li>
   */
  int[] getOrder();

  /**
   * Sets the order in which objects should be loaded along each axis.
   * @param order One of:<ul>
   *   <li>CENTERED_ORDER</li>
   *   <li>FORWARD_ORDER</li>
   *   <li>BACKWARD_ORDER</li>
   * @param axis The axis for which to set the order.
   */
  void setOrder(int order, int axis);

  /** Retrieves the number of objects to cache along each axis. */
  int[] getRange();

  /** Sets the number of objects to cache along the given axis. */
  void setRange(int num, int axis);

  /** Gets the lengths of all the axes. */
  int[] getLengths();

}
