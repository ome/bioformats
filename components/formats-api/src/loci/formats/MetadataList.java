/*
 * #%L
 * primary reader and writer APIs
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * A container of metadata for FormatReader and other reader implementations.  The structure is designed to contain
 * data for individual image series', including sub-resolutions.  This is implemented internally as a list of
 * lists indexed by series and resolution index.  However, it may be used to store any data in a two-level
 * structure.
 *
 * The container may be sized up front at the primary or at both levels.  Or either level may be appended to
 * individually.  Both patterns are used by many different readers.
 *
 * This class was originally developed to store the CoreMetadata for image series and sub-resolutions.  However, it
 * may be used to store any other data, for example reader-specific data including IFD mappings, extended
 * metadata, etc.
 *
 * @param <T> The type of metadata to store.
 */
public class MetadataList<T> {
  protected List<List<T>> data = new ArrayList<>();

  // Construct an empty list.
  public MetadataList() {
  }

  /**
   * Copy a list.
   */
  public MetadataList(MetadataList<T> copy) {
    for (int i = 0; i < copy.size(); i++) {
      add();
      for (int j = 0; j < copy.size(i); j++) {
        add(i, copy.get(i,j));
      }
    }
  }

  /**
   * Construct a list containing a specified number of primary elements.
   *
   * This may be used to specify e.g. the number of image series without any sub-resolutions.  The sub-resolutions,
   * including the full resolution, must be added afterward.
   *
   * @param size1 The number of primary list elements
   */
  public MetadataList(int size1) {
    this(size1, 0);
  }

  /**
   * Construct a list containing a specified number of primary elements and a fixed number of secondary elements.
   *
   * This may be used to specify e.g. the number of image series with a fixed number of sub-resolutions.
   *
   * @param size1 The number of primary list elements
   * @param size2 The number of secondary level list elements
   */
  public MetadataList(int size1, int size2) {
    for (int i = 0; i < size1; i++) {
      add(size2);
    }

  }

  /**
   * Construct a list containing a specified number of primary and secondary elements.
   *
   * This may be used to specify e.g. the number of image series including all sub-resolutions.
   *
   * @param sizes The number of primary and secondary list elements; the array elements are the secondary element sizes.
   */
  public MetadataList(int[] sizes) {
    for (int i : sizes) {
      add(i);
    }
  }

  /**
   * Get the array element for the specified indexes.

   * @param i1 The primary array index
   * @param i2 The secondary array index
   * @return The element
   */
  public T get(int i1, int i2) {
    return data.get(i1).get(i2);
  }

  /**
   * Set the array element for the specified indexes.
   * @param i1 The primary array index
   * @param i2 The secondary array index
   * @param value The element to set
   */
  public void set(int i1, int i2, T value) {
    data.get(i1).set(i2, value);
  }

  /**
   * Add a empty primary array element.
   */
  public void add() {
    data.add(new ArrayList<T>());
  }

  /**
   * Add a empty primary array element.
   * The number of secondary level list elements
   */
  public void add(int size2) {
    data.add(new ArrayList<>(Collections.<T>nCopies(size2, null)));
  }

  /**
   * Add a secondary array element to the specified primary array
   * @param i1 The primary array index
   * @param value The element to set
   */
  public void add(int i1, T value) {
    data.get(i1).add(value);
  }

  /**
   * Add a new primary element containing a single specified value.
   * @param value The value to add
   */
  public void add(T value) {
    ArrayList<T> list = new ArrayList<>();
    list.add(value);
    data.add(list);
  }

  /**
   * Add a new primary element containing the specified values.
   * @param values The values to add
   */
  public void add(List<T> values) {
    data.add(new ArrayList<>(values));
  }

  /**
   * Clear the array.
   */
  public void clear() {
    data.clear();
  }

  /**
   * Clear the specified primary array element.
   * @param i1 The primary array index
   */
  public void clear(int i1) {
    data.get(i1).clear();
  }

  /**
   * Get the size of the array (number of primary array elements).
   * @return The size
   */
  public int size() {
    return data.size();
  }

  /**
   * Get the size of the specified primary array element (number of secondary array elements).
   * @param i1 The primary array index
   * @return The size
   */
  public int size(int i1) {
    return data.get(i1).size();
  }

  /**
   * Get the sizes of all primary array elements.
   * @return An array containing the size of each primary element
   */
  public int[] sizes() {
    int[] s = new int[data.size()];
    for (int i = 0; i < data.size(); i++) {
      s[i] = data.get(i).size();
    }
    return s;
  }

}
