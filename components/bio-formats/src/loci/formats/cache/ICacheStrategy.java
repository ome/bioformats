//
// ICacheStrategy.java
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
 * Interface for cache strategies. A cache strategy identifies which
 * objects should be loaded into the cache, in which order. Unlike
 * {@link ICacheSource}, it works with multidimensional (N-D) position arrays
 * rather than rasterized (1-D) indices. The two are made equivalent via a
 * mapping between the two, invoked within {@link Cache} as needed.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/cache/ICacheStrategy.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/cache/ICacheStrategy.java;hb=HEAD">Gitweb</a></dd></dl>
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
