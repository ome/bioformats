//
// CacheSource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/**
 * Retrieves image planes from a data source
 * (e.g., a file) using Bio-Formats.
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

  /* @see ICacheSource#getObject(int[], int[]) */
  public abstract Object getObject(int[] len, int[] pos) throws CacheException;

}
