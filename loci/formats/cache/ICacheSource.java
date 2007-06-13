//
// ICacheSource.java
//

package loci.formats.cache;

public interface ICacheSource {

  /** Get the total number of objects that this source can retrieve. */
  int getObjectCount() throws CacheException;

  /** Get the object corresponding to the given index. */
  Object getObject(int[] len, int[] pos) throws CacheException;

  /** Get the objects corresponding to the given indices. */
  Object[] getObjects(int[] len, int[][] pos) throws CacheException;

}
