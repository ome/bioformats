//
// BufferedImageSource.java
//

package loci.formats.cache;

import java.io.IOException;
import loci.formats.*;

/** Retrieves BufferedImages from a file, using Bio-Formats. */
public class BufferedImageSource extends CacheSource {

  // -- Fields --

  private IFormatReader reader;

  // -- Constructor --

  public BufferedImageSource(Object o) throws CacheException {
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
    try { return reader.openImage(ndx); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

}
