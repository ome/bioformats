//
// CacheSource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/**
 * Superclass of cache sources that retrieve image planes
 * from a data source (e.g., a file) using Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/CacheSource.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/CacheSource.java">SVN</a></dd></dl>
 */
public abstract class CacheSource implements ICacheSource {

  // -- Fields --

  /** Reader from which to draw image planes. */
  protected IFormatReader reader;

  // -- Constructors --

  /** Constructs a cache source from the given Bio-Formats reader. */
  public CacheSource(IFormatReader r) { reader = r; }

  /** Constructs a cache source that draws from the given file. */
  public CacheSource(String id) throws CacheException {
    this(new FileStitcher());
    try { reader.setId(id); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObjectCount() */
  public int getObjectCount() { return reader.getImageCount(); }

  /* @see ICacheSource#getObject(int) */
  public abstract Object getObject(int index) throws CacheException;

}
