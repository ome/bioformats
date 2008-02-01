//
// CacheException.java
//

package loci.formats.cache;

import loci.formats.FormatException;

/**
 * CacheException is the exception thrown when something
 * goes wrong performing a cache operation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/CacheException.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/CacheException.java">SVN</a></dd></dl>
 */
public class CacheException extends FormatException {

  public CacheException() { super(); }
  public CacheException(String s) { super(s); }
  public CacheException(String s, Throwable cause) { super(s, cause); }
  public CacheException(Throwable cause) { super(cause); }

}

