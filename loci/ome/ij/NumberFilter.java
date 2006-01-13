//
// NumberFilter.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;

/** NumberFilter is a helper filter for DataManager.findPattern(). */
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
