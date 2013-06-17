/*
 * Copyright (C) 2013 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package loci.formats;

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

  public static final String FAKE_EXT = ".fake";

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
   *         resources.
   */
  public Location getLocationFromResourceName(Location resourceParentPath,
      String resourceName, int nameIndex, String resourceExtension) {
    StringBuilder sb = new StringBuilder();
    sb.append(resourceName + String.format("%03d", nameIndex));
    if (resourceExtension != null) {
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

}
