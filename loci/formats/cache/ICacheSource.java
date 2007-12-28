//
// ICacheSource.java
//

package loci.formats.cache;

/**
 * Interface for cache sources. A cache source returns objects
 * in a defined order, from 0 to getObjectCount() - 1.
 * The actual source of the objects is implementation-dependent;
 * Bio-Formats uses {@link loci.formats.IFormatReader} to obtain image planes,
 * but any ordered collection of objects can conceivably be cached.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/ICacheSource.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/ICacheSource.java">SVN</a></dd></dl>
 */
public interface ICacheSource {

  /** Get the total number of objects that this source can retrieve. */
  int getObjectCount() throws CacheException;

  /** Get the object corresponding to the given index. */
  Object getObject(int index) throws CacheException;

}
