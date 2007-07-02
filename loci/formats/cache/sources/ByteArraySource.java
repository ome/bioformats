//
// ByteArraySource.java
//

package loci.formats.cache.sources;

import java.io.IOException;
import loci.formats.*;
import loci.formats.cache.*;

/** Retrieves byte arrays from a file, using Bio-Formats. */
public class ByteArraySource extends CacheSource {

  // -- Fields --

  private IFormatReader reader;

  // -- Constructor --

  public ByteArraySource(Object o) throws CacheException {
    super(o); 
    try {
      reader = new FileStitcher();
      reader.setId(o.toString()); 
    }
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
