//
// ByteArraySource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/**
 * Retrieves byte arrays from a data source
 * (e.g., a file) using Bio-Formats.
 */
public class ByteArraySource extends CacheSource {

  // -- Constructors --

  /** Constructs a byte array source from the given Bio-Formats reader. */
  public ByteArraySource(IFormatReader r) { super(r); }

  /** Constructs a byte array source that draws from the given file. */
  public ByteArraySource(String id) throws CacheException { super(id); }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObject(int[], int[]) */
  public Object getObject(int[] len, int[] pos) throws CacheException {
    int ndx = FormatTools.positionToRaster(len, pos);
    try { return reader.openBytes(ndx); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

}
