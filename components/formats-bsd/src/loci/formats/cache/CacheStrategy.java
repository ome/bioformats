/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.cache;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import loci.formats.FormatTools;

/**
 * Superclass of cache strategies.
 */
public abstract class CacheStrategy
  implements CacheReporter, Comparator, ICacheStrategy
{

  // -- Constants --

  /** Default cache range. */
  public static final int DEFAULT_RANGE = 0;

  // -- Fields --

  /** Length of each dimensional axis. */
  protected int[] lengths;

  /** The order in which planes should be loaded along each axis. */
  protected int[] order;

  /** Number of planes to cache along each axis. */
  protected int[] range;

  /** Priority for caching each axis. Controls axis caching order. */
  protected int[] priorities;

  /**
   * The list of dimensional positions to consider caching, in order of
   * preference based on strategy, axis priority and planar ordering.
   */
  private int[][] positions;

  /**
   * Whether load order array needs to be recomputed
   * before building the next load list.
   */
  private boolean dirty;

  /** List of cache event listeners. */
  protected Vector listeners;

  // -- Constructors --

  /** Constructs a cache strategy. */
  public CacheStrategy(int[] lengths) {
    this.lengths = lengths;
    order = new int[lengths.length];
    Arrays.fill(order, CENTERED_ORDER);
    range = new int[lengths.length];
    Arrays.fill(range, DEFAULT_RANGE);
    priorities = new int[lengths.length];
    Arrays.fill(priorities, NORMAL_PRIORITY);
    positions = getPossiblePositions();
    dirty = true;
    listeners = new Vector();
  }

  // -- Abstract CacheStrategy API methods --

  /**
   * Gets positions to consider for possible inclusion in the cache,
   * assuming a current position at the origin (0).
   */
  protected abstract int[][] getPossiblePositions();

  // -- CacheStrategy API methods --

  /**
   * Computes the distance from the given axis value to the
   * axis center, taking into account the axis ordering scheme.
   */
  public int distance(int axis, int value) {
    switch (order[axis]) {
      case CENTERED_ORDER:
        if (value == 0) return 0;
        int vb = lengths[axis] - value;
        return value <= vb ? value : vb;
      case FORWARD_ORDER:
        return value;
      case BACKWARD_ORDER:
        if (value == 0) return 0;
        return lengths[axis] - value;
      default:
        throw new IllegalStateException("unknown order: " + order[axis]);
    }
  }

  // -- Internal CacheStrategy API methods --

  /** Shortcut for converting N-D position to rasterized position. */
  protected int raster(int[] pos) {
    return FormatTools.positionToRaster(lengths, pos);
  }

  /** Shortcut for converting rasterized position to N-D position. */
  protected int[] pos(int raster) {
    return FormatTools.rasterToPosition(lengths, raster);
  }

  /**
   * Shortcut for converting rasterized position to N-D position,
   * using the given array instead of allocating a new one.
   */
  protected int[] pos(int raster, int[] pos) {
    return FormatTools.rasterToPosition(lengths, raster, pos);
  }

  /** Shortcut for computing total number of positions. */
  protected int length() { return FormatTools.getRasterLength(lengths); }

  // -- CacheReporter API methods --

  /* @see CacheReporter#addCacheListener(CacheListener) */
  @Override
  public void addCacheListener(CacheListener l) {
    synchronized (listeners) { listeners.add(l); }
  }

  /* @see CacheReporter#removeCacheListener(CacheListener) */
  @Override
  public void removeCacheListener(CacheListener l) {
    synchronized (listeners) { listeners.remove(l); }
  }

  /* @see CacheReporter#getCacheListeners() */
  @Override
  public CacheListener[] getCacheListeners() {
    CacheListener[] l;
    synchronized (listeners) {
      l = new CacheListener[listeners.size()];
      listeners.copyInto(l);
    }
    return l;
  }

  // -- Comparator API methods --

  /**
   * Default comparator orders dimensional positions based on distance from the
   * current position, taking into account axis priorities and planar ordering.
   */
  @Override
  public int compare(Object o1, Object o2) {
    int[] p1 = (int[]) o1;
    int[] p2 = (int[]) o2;

    // compare sum of axis distances for each priority
    for (int p=MAX_PRIORITY; p>=MIN_PRIORITY; p--) {
      int dist1 = 0, dist2 = 0;
      for (int i=0; i<p1.length; i++) {
        if (priorities[i] == p) {
          dist1 += distance(i, p1[i]);
          dist2 += distance(i, p2[i]);
        }
      }
      int diff = dist1 - dist2;
      if (diff != 0) return diff;
    }

    // compare number of diverging axes for each priority
    for (int p=MAX_PRIORITY; p>=MIN_PRIORITY; p--) {
      int div1 = 0, div2 = 0;
      for (int i=0; i<p1.length; i++) {
        if (priorities[i] == p) {
          if (p1[i] != 0) div1++;
          if (p2[i] != 0) div2++;
        }
      }
      int diff = div1 - div2;
      if (diff != 0) return diff;
    }

    return 0;
  }

  // -- ICacheStrategy API methods --

  /* @see ICacheStrategy#getLoadList(int[]) */
  @Override
  public int[][] getLoadList(int[] pos) throws CacheException {
    int[][] loadList = null;
    synchronized (positions) {
      if (dirty) {
        // reorder the positions list
        Arrays.sort(positions, this);
        dirty = false;
      }

      // count up number of load list entries
      int c = 0;
      for (int i=0; i<positions.length; i++) {
        int[] ipos = positions[i];

        // verify position is close enough to current position
        boolean ok = true;
        for (int j=0; j<ipos.length; j++) {
          if (distance(j, ipos[j]) > range[j]) {
            ok = false;
            break;
          }
        }
        if (ok) c++; // in range
      }

      loadList = new int[c][lengths.length];
      c = 0;

      // build load list
      for (int i=0; i<positions.length; i++) {
        int[] ipos = positions[i];

        // verify position is close enough to current position
        boolean ok = true;
        for (int j=0; j<ipos.length && c<loadList.length; j++) {
          if (distance(j, ipos[j]) > range[j]) {
            ok = false;
            break;
          }
          int value = (pos[j] + ipos[j]) % lengths[j]; // normalize axis value
          loadList[c][j] = value;
        }
        if (ok) c++; // in range; lock in load list entry
      }
    }
    return loadList;
  }

  /* @see ICacheStrategy#getPriorities() */
  @Override
  public int[] getPriorities() { return priorities; }

  /* @see ICacheStrategy#setPriority() */
  @Override
  public void setPriority(int priority, int axis) {
    if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
      throw new IllegalArgumentException(
        "Invalid priority for axis #" + axis + ": " + priority);
    }
    synchronized (positions) {
      priorities[axis] = priority;
      dirty = true;
    }
    notifyListeners(new CacheEvent(this, CacheEvent.PRIORITIES_CHANGED));
  }

  /* @see ICacheStrategy#getOrder() */
  @Override
  public int[] getOrder() { return order; }

  /* @see ICacheStrategy#getOrder() */
  @Override
  public void setOrder(int order, int axis) {
    if (order != CENTERED_ORDER &&
      order != FORWARD_ORDER && order != BACKWARD_ORDER)
    {
      throw new IllegalArgumentException(
        "Invalid order for axis #" + axis + ": " + order);
    }
    synchronized (positions) {
      this.order[axis] = order;
      dirty = true;
    }
    notifyListeners(new CacheEvent(this, CacheEvent.ORDER_CHANGED));
  }

  /* @see ICacheStrategy#getRange() */
  @Override
  public int[] getRange() { return range; }

  /* @see ICacheStrategy#setRange(int, int) */
  @Override
  public void setRange(int num, int axis) {
    if (num < 0) {
      throw new IllegalArgumentException(
        "Invalid range for axis #" + axis + ": " + num);
    }
    range[axis] = num;
    notifyListeners(new CacheEvent(this, CacheEvent.RANGE_CHANGED));
  }

  /* @see ICacheStrategy#getLengths() */
  @Override
  public int[] getLengths() { return lengths; }

  // -- Helper methods --

  /** Informs listeners of a cache update. */
  protected void notifyListeners(CacheEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        CacheListener l = (CacheListener) listeners.elementAt(i);
        l.cacheUpdated(e);
      }
    }
  }

}
