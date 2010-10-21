//
// FilePatternBlock.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats;

import java.math.BigInteger;

/**
 * FilePatternBlock represents a single block in a {@link loci.formats.FilePattern}.
 *
 * Examples:
 * <ul>
 *   <li>&lt;1-12&gt;</li>
 *   <li>&lt;A-H&gt;</li>
 *   <li>&lt;R,G,B&gt;</li>
 * </ul>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trukn/components/bio-formats/src/loci/formats/FilePatternBlock.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/FilePatternBlock.java">SVN</a></dd></dl>
 *
 */
public class FilePatternBlock {

  // -- Constants --

  public static final String BLOCK_START = "<";
  public static final String BLOCK_END = ">";

  // -- Fields --

  /** Elements within this block, e.g. ["R", "G", "B"] or ["1", "2", "3"]. */
  private String[] elements;

  /** Whether or not this is a fixed-width block. */
  private boolean fixed;

  /** The number of leading zeroes. */
  private int zeroes;

  /** String representation of this block. */
  private String block;

  // -- Constructor --

  public FilePatternBlock(String block) {
    this.block = block;
    explode();
  }

  // -- FilePatternBlock API methods --

  public String[] getElements() {
    return elements;
  }

  public String getBlock() {
    return block;
  }

  public boolean isFixed() {
    return fixed;
  }

  // -- Helper methods --

  private void explode() {
    int dash = block.indexOf("-");
    String b, e, s;
    if (dash < 0) {
      // check if this is an enumerated list
      int comma = block.indexOf(",");
      if (comma > 0) {
        elements = block.substring(1, block.length() - 1).split(",");
        return;
      }
      else {
        // no range and not a list; assume entire block is a single value
        b = e = block.substring(1, block.length() - 1);
        s = "1";
      }
    }
    else {
      int colon = block.indexOf(":");
      b = block.substring(1, dash);
      if (colon < 0) {
        e = block.substring(dash + 1, block.length() - 1);
        s = "1";
      }
      else {
        e = block.substring(dash + 1, colon);
        s = block.substring(colon + 1, block.length() - 1);
      }
    }

    BigInteger begin = null;
    BigInteger end = null;
    BigInteger step = null;
    boolean numeric = true;

    try {
      begin = new BigInteger(b);
      end = new BigInteger(e);
      step = new BigInteger(s);
    }
    catch (NumberFormatException exc) {
      numeric = false;
      begin = new BigInteger(b, 26);
      end = new BigInteger(e, 26);
      step = new BigInteger(s, 26);
    }

    fixed = b.length() == e.length();
    zeroes = 0;
    for (zeroes=0; zeroes<e.length(); zeroes++) {
      if (e.charAt(zeroes) != '0') break;
    }

    int count = end.subtract(begin).divide(step).intValue() + 1;
    elements = new String[count];

    for (int i=0; i<count; i++) {
      BigInteger v = begin.add(step.multiply(BigInteger.valueOf(i)));
      String value = numeric ? v.toString() : v.toString(26);
      if (!numeric) {
        if (Character.isLowerCase(b.charAt(0))) value = value.toLowerCase();
        else value = value.toUpperCase();
      }
      int padChars = fixed ? e.length() - value.length() : 0;
      elements[i] = value;
      for (int j=0; j<padChars; j++) {
        elements[i] = "0" + elements[i];
      }
    }
  }

}
