//
// NumberFilter.java
//

package loci.util;

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;

/** NumberFilter is a helper filter for FilePattern.findPattern(). */
public class NumberFilter implements FileFilter {

  // -- Fields --

  /** String appearing before the numerical block. */
  private String pre;

  /** String appearing after the numerical block. */
  private String post;


  // -- Constructor --

  /**
   * Creates a filter for files containing a numerical block,
   * sandwiched between the given strings.
   */
  public NumberFilter(String pre, String post) {
    this.pre = pre;
    this.post = post;
  }


  // -- NumberFilter API methods --

  /** Gets numbers filling the asterisk positions. */
  public BigInteger getNumber(String name) {
    if (!name.startsWith(pre) || !name.endsWith(post)) return null;
    int ndx = pre.length();
    int end = name.length() - post.length();
    try { return new BigInteger(name.substring(ndx, end)); }
    catch (NumberFormatException exc) { return null; }
  }


  // -- FileFilter API methods --

  /** Tests if a specified file should be included in a file list. */
  public boolean accept(File pathname) {
    return getNumber(pathname.getName()) != null;
  }

}
