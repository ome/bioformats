//
// CacheException.java
//

package loci.formats.cache;

/**
 * CacheException is the exception thrown when something
 * goes wrong performing a cache operation.
 */
public class CacheException extends Exception {

  public CacheException() { super(); }
  public CacheException(String s) { super(s); }
  public CacheException(String s, Throwable cause) { super(s, cause); }
  public CacheException(Throwable cause) { super(cause); }

}

