//
// DataCache.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.data;

import java.util.Hashtable;
import visad.Data;

/** Provides a simple caching mechanism for full resolution data in memory. */
public class DataCache {

  // -- Fields --

  /** Hashtable backing this cache of full resolution data. */
  protected Hashtable hash;


  // -- Constructor --

  /** Constructs a cache for managing full-resolution data in memory. */
  public DataCache() { hash = new Hashtable(); }


  // -- DataCache API methods --

  /** Gets the data object from the cache, computing it if the cache misses. */
  public synchronized Data getData(DataTransform trans,
    int[] pos, int dim)
  {
    Data d = getCachedData(getKey(trans, pos));
    if (d == null) {
      d = trans.getData(pos, dim);
      putCachedData(getKey(trans, pos), d);
    }
    return d;
  }

  /** Gets the data in the cache at the specified key. */
  public Data getCachedData(String key) {
    Object o = hash.get(key);
    if (!(o instanceof Data)) return null;
    return (Data) o;
  }

  /** Sets the data in the cache at the specified key. */
  public void putCachedData(String key, Data d) { hash.put(key, d); }

  /** Removes the data object at the specified key from the cache. */
  public void dump(String key) { hash.remove(key); }

  /** Removes everything from the cache. */
  public void dumpAll() { hash.clear(); }


  // -- Helper methods --

  /**
   * Gets a key string suitable for hashing for the given transform
   * at the specified position.
   */
  protected String getKey(DataTransform trans, int[] pos) {
    return trans.getCacheId(pos, true);
  }

}
