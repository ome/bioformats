/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.util.Vector;

import loci.formats.FormatTools;

/**
 * Cache provides a means of managing subsets of large collections of image
 * planes in memory. Each cache has a source, which provides image planes or
 * other objects from somewhere (typically derived from an IFormatReader),
 * and a strategy dictating which image planes should be loaded into the cache,
 * in which order, and which planes should be dropped from the cache. The
 * essence of the logic is the idea that the cache has a "current" position
 * across the multidimensional image series's dimensional axes, with the
 * strategy indicating which surrounding planes to load into the cache (i.e.,
 * planes within a certain range along each dimensional axis).
 */
public class Cache implements CacheReporter {

  // -- Fields --

  /** Current cache strategy. */
  protected ICacheStrategy strategy;

  /** Current cache source. */
  protected ICacheSource source;

  /** Current dimensional position. */
  protected int[] currentPos;

  /** Master array containing cached objects. */
  protected Object[] cache;

  /** Whether each position is currently supposed to be cached. */
  protected boolean[] inCache;

  /** List of cache event listeners. */
  protected Vector<CacheListener> listeners;

  /** Whether the cache should automatically update when a parameter changes. */
  protected boolean autoUpdate;

  // -- Constructors --

  /** Constructs an object cache with the given cache strategy and source. */
  public Cache(ICacheStrategy strategy, ICacheSource source,
    boolean autoUpdate) throws CacheException
  {
    if (strategy == null) throw new CacheException("strategy is null");
    if (source == null) throw new CacheException("source is null");
    this.strategy = strategy;
    this.source = source;
    this.autoUpdate = autoUpdate;
    listeners = new Vector<CacheListener>();
    reset();
    if (autoUpdate) recache();
  }

  // -- Cache API methods --

  /** Gets the cached object at the given dimensional position. */
  public Object getObject(int[] pos) throws CacheException {
    if (pos.length != strategy.getLengths().length) {
      throw new CacheException("Invalid number of axes; got " + pos.length +
        "; expected " + strategy.getLengths().length);
    }

    int ndx = FormatTools.positionToRaster(strategy.getLengths(), pos);
    return cache[ndx];
  }

  /**
   * Returns true if the object at the given dimensional position is
   * in the cache.
   */
  public boolean isInCache(int[] pos) throws CacheException {
    return isInCache(FormatTools.positionToRaster(strategy.getLengths(), pos));
  }

  /** Returns true if the object at the given index is in the cache. */
  public boolean isInCache(int pos) throws CacheException {
    return inCache[pos];
  }

  /** Reallocates the cache. */
  public void reset() throws CacheException {
    currentPos = new int[strategy.getLengths().length];
    cache = new Object[source.getObjectCount()];
    inCache = new boolean[source.getObjectCount()];
  }

  /** Gets the cache's caching strategy. */
  public ICacheStrategy getStrategy() { return strategy; }

  /** Gets the cache's caching source. */
  public ICacheSource getSource() { return source; }

  /** Gets the current dimensional position. */
  public int[] getCurrentPos() { return currentPos; }

  /** Sets the cache's caching strategy. */
  public void setStrategy(ICacheStrategy strategy) throws CacheException {
    if (strategy == null) throw new CacheException("strategy is null");
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        CacheListener l = listeners.elementAt(i);
        this.strategy.removeCacheListener(l);
        strategy.addCacheListener(l);
      }
    }
    this.strategy = strategy;
    notifyListeners(new CacheEvent(this, CacheEvent.STRATEGY_CHANGED));
    reset();
    if (autoUpdate) recache();
  }

  /** Sets the cache's caching source. */
  public void setSource(ICacheSource source) throws CacheException {
    if (source == null) throw new CacheException("source is null");
    this.source = source;
    notifyListeners(new CacheEvent(this, CacheEvent.SOURCE_CHANGED));
    reset();
    if (autoUpdate) recache();
  }

  /** Sets the current dimensional position. */
  public void setCurrentPos(int[] pos) throws CacheException {
    if (pos == null) throw new CacheException("pos is null");
    if (pos.length != currentPos.length) {
      throw new CacheException("pos length mismatch (is " +
        pos.length + ", expected " + currentPos.length + ")");
    }
    int[] len = strategy.getLengths();
    for (int i=0; i<pos.length; i++) {
      if (pos[i] < 0 || pos[i] >= len[i]) {
        throw new CacheException("invalid pos[" + i + "] (is " +
          pos[i] + ", expected [0, " + (len[i] - 1) + "])");
      }
    }
    System.arraycopy(pos, 0, currentPos, 0, pos.length);
    int ndx = FormatTools.positionToRaster(len, pos);
    notifyListeners(new CacheEvent(this, CacheEvent.POSITION_CHANGED, ndx));
    if (autoUpdate) recache();
  }

  /** Updates the given plane. */
  public void recache(int n) throws CacheException {
    int[][] indices = strategy.getLoadList(currentPos);
    int[] len = strategy.getLengths();

    for (int i=0; i<inCache.length; i++) {
      boolean found = false;
      for (int j=0; j<indices.length; j++) {
        if (i == FormatTools.positionToRaster(len, indices[j])) {
          found = true;
          break;
        }
      }
      if (!found) {
        inCache[i] = false;
        if (cache[i] != null) {
          cache[i] = null;
          notifyListeners(new CacheEvent(this, CacheEvent.OBJECT_DROPPED, i));
        }
      }
    }

    int ndx = FormatTools.positionToRaster(len, indices[n]);
    if (ndx >= 0) inCache[ndx] = true;

    if (cache[ndx] == null) {
      cache[ndx] = source.getObject(ndx);
      notifyListeners(new CacheEvent(this, CacheEvent.OBJECT_LOADED, ndx));
    }
  }

  /** Updates all planes on the load list. */
  public void recache() throws CacheException {
    // what happens if cache source and cache strategy lengths do not match?
    // throw exception in that case
    // what if developer wants to change both source and strategy to something
    // completely different -- make sure it works
    // in general, what if developer wants to tweak a few parameters before
    // starting to reload things? probably should have a recache method that
    // you must explicitly call to trigger the separate thread refresh
    // need to be careful -- don't want cache parameter changes affecting the
    // recaching thread on the fly -- should refresh those parameter values
    // each time through the loop only (i.e., only when a recache call occurs)
    //
    // /lo
    for (int i=0; i<strategy.getLoadList(currentPos).length; i++) {
      recache(i);
    }
  }

  // -- CacheReporter API methods --

  /* @see CacheReporter#addCacheListener(CacheListener) */
  @Override
  public void addCacheListener(CacheListener l) {
    synchronized (listeners) {
      listeners.add(l);
      strategy.addCacheListener(l);
    }
  }

  /* @see CacheReporter#removeCacheListener(CacheListener) */
  @Override
  public void removeCacheListener(CacheListener l) {
    synchronized (listeners) {
      listeners.remove(l);
      strategy.removeCacheListener(l);
    }
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

  // -- Helper methods --

  /** Informs listeners of a cache update. */
  protected void notifyListeners(CacheEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        CacheListener l = listeners.elementAt(i);
        l.cacheUpdated(e);
      }
    }
  }

}
