/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/FilePatternBlock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/FilePatternBlock.java;hb=HEAD">Gitweb</a></dd></dl>
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

  private BigInteger begin = null, end = null, step = null;

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

  public BigInteger getFirst() {
    return begin;
  }

  public BigInteger getLast() {
    return end;
  }

  public BigInteger getStep() {
    return step;
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
