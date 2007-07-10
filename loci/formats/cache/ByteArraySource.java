//
// ByteArraySource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/** Retrieves byte arrays from a file, using Bio-Formats. */
public class ByteArraySource extends CacheSource {

  // -- Fields --

  /** Reader from which to draw byte arrays. */
  private IFormatReader reader;

  // -- Constructors --

  /** Constructs a byte array source from the given Bio-Formats reader. */
  public ByteArraySource(IFormatReader r) throws CacheException {
    super(r);
    reader = r;
  }

  /** Constructs a byte array source drawing from the given file. */
  public ByteArraySource(String id) throws CacheException {
    this(new FileStitcher());
    try { reader.setId(id); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObjectCount() */
  public int getObjectCount() {
    return reader.getImageCount();
  }

  /* @see loci.formats.cache.ICacheSource#getObject(int[], int[]) */
  public Object getObject(int[] len, int[] pos) throws CacheException {
    int ndx = FormatTools.positionToRaster(len, pos);
    try { return reader.openBytes(ndx); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

}
