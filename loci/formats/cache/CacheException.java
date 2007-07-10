//
// CacheException.java
//

package loci.formats.cache;

import loci.formats.FormatException;

/**
 * CacheException is the exception thrown when something
 * goes wrong performing a cache operation.
 */
public class CacheException extends FormatException {

  public CacheException() { super(); }
  public CacheException(String s) { super(s); }
  public CacheException(String s, Throwable cause) { super(s, cause); }
  public CacheException(Throwable cause) { super(cause); }

}

