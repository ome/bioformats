//
// BufferedImageSource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/**
 * Retrieves BufferedImages from a data source
 * (e.g., a file) using Bio-Formats.
 */
public class BufferedImageSource extends CacheSource {

  // -- Constructors --

  /** Constructs a BufferedImage source from the given Bio-Formats reader. */
  public BufferedImageSource(IFormatReader r) { super(r); }

  /** Constructs a BufferedImage source that draws from the given file. */
  public BufferedImageSource(String id) throws CacheException { super(id); }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public Object getObject(int index) throws CacheException {
    try { return reader.openImage(index); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

}
