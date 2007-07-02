//
// CacheSource.java
//

package loci.formats.cache;

public abstract class CacheSource implements ICacheSource {

  // -- Constructors --

  public CacheSource(Object o) { } 

  // -- ICacheSource API methods --

  /* @see ICacheSource#getObjectCount() */
  public abstract int getObjectCount() throws CacheException;

  /* @see ICacheSource#getObject(int[], int[]) */
  public abstract Object getObject(int[] len, int[] pos) throws CacheException;

  /* @see ICacheSource#getObject(int[], int[][]) */
  public Object[] getObjects(int[] len, int[][] pos) throws CacheException {
    Object[] o = new Object[pos.length];
    for (int i=0; i<o.length; i++) {
      o[i] = getObject(len, pos[i]);
    } 
    return o; 
  }

}
