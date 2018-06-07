/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.File;

import loci.common.Location;

/**
 * Utility class helping with generating human-readable resource names
 * based on alphanumeric indexing.
 *
 * @author Blazej Pindelski, bpindelski at dundee.ac.uk
 * @since 5.0
 */
public class ResourceNamer {

  public static final String PLATE = "Plate";

  public static final String RUN = "Run";

  public static final String WELL = "Well";

  public static final String FIELD = "Field";

  public static final String FAKE_EXT = "fake";

  public static final String DOT = ".";

  private static final int ALPHABET_LENGTH = 26;

  private int resourceCount = 0;

  private char letter, tmpLetter;

  public ResourceNamer(int resourceCount) {
    if (resourceCount < 0) {
      throw new IllegalArgumentException();
    }
    this.resourceCount = resourceCount;
    restartAlphabet();
  }

  /**
   * Creates a new {@link Location} instance using the provided parent path and
   * child node name. Concatenates the child name with a numerical index that
   * acts as an incrementing counter of node elements.
   * 
   * @param resourceParentPath
   *          Path to the parent element.
   * @param resourceName
   *          Template string used for naming the child resource.
   * @param nameIndex
   *          Numerical value used for naming the child resource.
   * @param resourceExtension
   *          Optional extension (if the child resource is a file) or path
   *          separator (if folder).
   * @return {@link Location} New instance representing the parent and child
   *          resources.
   */
  public Location getLocationFromResourceName(Location resourceParentPath,
      String resourceName, int nameIndex, String resourceExtension) {
    StringBuilder sb = new StringBuilder();
    sb.append(resourceName + String.format("%03d", nameIndex));
    if (resourceExtension != null) {
      if (!resourceExtension.startsWith(File.separator)) {
        sb.append(DOT);
      }
      sb.append(resourceExtension);
    }
    return new Location(resourceParentPath, sb.toString());
  }

  /**
   * Resets the internal state of the class so that it starts alphabet
   * generation from the first letter (ASCII A).
   */
  public void restartAlphabet() {
    letter = tmpLetter = 'A';
  }

  /**
   * Increments the internal alphabet pointer to the next element of the
   * alphabet.
   */
  public void nextLetter() {
    tmpLetter++;
    if (tmpLetter > 'Z') {
      tmpLetter = letter;
      letter++;
    }
  }

  /**
   * Returns a <code>String</code> representing a single letter (in the case
   * where the class instance has been initialized with a resource count lower
   * than 26) or a concatenation of two letters (if resource count is higher
   * than 26).
   * @return See above.
   */
  public String getLetter() {
    if (resourceCount > ALPHABET_LENGTH) {
      return Character.toString(letter) + Character.toString(tmpLetter);
    } else {
      return Character.toString(tmpLetter);
    }
  }

  public static int alphabeticIndexCount(String index) {
    int count = 0;
    char[] letters = index.toCharArray();
    for (int i = 0; i < letters.length; i++) {
      count += (letters[i] - 64) * Math.pow(ALPHABET_LENGTH, i);
    }
    return count;
  }

}
