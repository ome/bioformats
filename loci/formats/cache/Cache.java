//
// Cache.java
//

package loci.formats.cache;

import java.util.Arrays;
import loci.formats.*;

public class Cache {

  // -- Fields --

  private ICacheStrategy strategy;
  private ICacheSource source;
  private int[] currentPos;
  private Object[] cache;
  private boolean[] inCache;

  // -- Constructors --

  public Cache() {
  }

  public Cache(ICacheStrategy strategy, ICacheSource source) 
    throws CacheException
  {
    this.strategy = strategy;
    this.source = source;
    reset(); 
  }

  // -- Cache API methods --

  public Object getObject(int[] pos) throws CacheException {
    if (pos.length != strategy.getLengths().length) {
      throw new CacheException("Invalid number of axes; got " + pos.length +
        "; expected " + strategy.getLengths().length);
    }

    int ndx = FormatTools.positionToRaster(strategy.getLengths(), pos);
    load(pos);
    return cache[ndx];
  }

  public void reset() throws CacheException {
    currentPos = new int[strategy.getLengths().length];
    cache = new Object[source.getObjectCount()];
    inCache = new boolean[source.getObjectCount()];
    Arrays.fill(inCache, false);
  }

  public ICacheStrategy getStrategy() { return strategy; }
  public ICacheSource getSource() { return source; }
  public int[] getCurrentPos() { return currentPos; }

  public void setStrategy(ICacheStrategy strategy) throws CacheException {
    this.strategy = strategy;
    if (source != null && strategy != null) reset(); 
    if (currentPos != null) load(currentPos);
  }

  public void setSource(ICacheSource source) throws CacheException {
    this.source = source;
    if (source != null && strategy != null) reset(); 
    if (currentPos != null) load(currentPos);
  }

  // -- Helper methods --

  private void load(int[] pos) throws CacheException {
    int[][] indices = strategy.getLoadList(pos); 

    Arrays.fill(inCache, false);
    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(strategy.getLengths(), indices[i]);
      if (ndx >= 0) inCache[ndx] = true;
    } 

    for (int i=0; i<cache.length; i++) {
      if (!inCache[i]) {
        cache[i] = null;
      } 
    }    

    for (int i=0; i<indices.length; i++) {
      int ndx = FormatTools.positionToRaster(strategy.getLengths(), indices[i]);
      if (ndx >= 0 && cache[ndx] == null) {
        cache[ndx] = source.getObject(strategy.getLengths(), indices[i]);
      } 
    }
  }

}
