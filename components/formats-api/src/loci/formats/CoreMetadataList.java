/*
 * #%L
 * Top-level reader and writer APIs
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CoreMetadataList extends MetadataList<CoreMetadata> {
  static final Comparator<CoreMetadata> comparator = new Comparator<CoreMetadata>() {
    @Override
    public int compare(CoreMetadata c1, CoreMetadata c2) {
      int result = Integer.compare(c1.sizeZ, c2.sizeZ);
      if (result == 0) {
        result = Integer.compare(c1.sizeY, c2.sizeY);
      }
      if (result == 0) {
        result = Integer.compare(c1.sizeX, c2.sizeX);
      }
      return -result; // descending order
    }
  };

  public CoreMetadataList() {
  }

  public CoreMetadataList(CoreMetadataList copy) {
    super(copy);
  }

  /**
   * Construct a list containing a specified number of primary elements.
   *
   * This may be used to specify e.g. the number of image series without any sub-resolutions.  The sub-resolutions,
   * including the full resolution, must be added afterward.
   *
   * @param size1 The number of primary list elements
   */
  public CoreMetadataList(int size1) {
    super(size1);
  }

  /**
   * Construct a list containing a specified number of primary elements and a fixed number of secondary elements.
   *
   * This may be used to specify e.g. the number of image series with a fixed number of sub-resolutions.
   *
   * @param size1 The number of primary list elements
   * @param size2 The number of secondary level list elements
   */
  public CoreMetadataList(int size1, int size2) { super(size1, size2); }

  /**
   * Construct a list containing a specified number of primary and secondary elements.
   *
   * This may be used to specify e.g. the number of image series including all sub-resolutions.
   *
   * @param sizes The number of primary and secondary list elements; the array elements are the secondary element sizes.
   */
  public CoreMetadataList(int[] sizes) { super(sizes); }

  public CoreMetadataList(List<CoreMetadata> list) {
    setFlattenedList(list);
  }

  /**
   * Add a secondary array element to the specified primary array
   * @param i1 The primary array index
   * @param value The element to set
   */
  @Override
  public void add(int i1, CoreMetadata value) {
    super.add(i1, value);
  }

  public void reorder() {
    for (List<CoreMetadata> s : data) {
      Collections.sort(s, comparator);
    }
  }

  public List<CoreMetadata> getFlattenedList() {
    List<CoreMetadata> l = new ArrayList<>();

    int[] sizes = sizes();

    for (int i = 0; i < sizes.length; ++i) {
      if (sizes[i] > 0) {
        get(i, 0).resolutionCount = sizes[i];
      }
      for (int j = 0; j < sizes[i]; ++j) {
        l.add(get(i,j));
      }
    }

    return l;
  }

  public void setFlattenedList(List<CoreMetadata> list) {
    clear();
    for (int i = 0; i < list.size(); i += list.get(i).resolutionCount) {
      List<CoreMetadata> sublist = new ArrayList<>();
      for (int j = 0; j < list.get(i).resolutionCount; ++j) {
        sublist.add(list.get(i + j));
      }
      add(sublist);
    }
  }

  public List<CoreMetadata> getUnflattenedList() {
    List<CoreMetadata> l = new ArrayList<>();

    for (int i = 0; i < size(); ++i) {
      l.add(get(i, 0));
    }

    return l;
  }

  /**
   * Get flattened size (all resolutions in all series)
   * @return The number of all resolutions in all series
   */
  public int flattenedSize() {
    int ncore = 0;
    for (int v : sizes()) {
      ncore += v;
    }
    return ncore;
  }

  public int flattenedIndex(int series, int resolution) {
    int idx = 0;

    if (series < 0 || series >= size()) {
      throw new IllegalArgumentException("Invalid series: " + series);
    }

    for (int i = 0; i < series; ++i) {
      idx += size(i);
    }

    if (resolution < 0 || resolution >= size(series)) {
      throw new IllegalArgumentException("Invalid resolution: " + resolution);
    }

    idx += resolution;

    return idx;
  }

  public int[] flattenedIndexes(int flattenedIndex) {
    int series = 0;
    int resolution = 0;

    if (flattenedIndex >= flattenedSize()) {
      throw new IllegalArgumentException("Invalid flattened index: " + flattenedIndex);
    }

    int found = 0;
    for (int i = 0; i < size(); ++i) {
      if (size(i) <= flattenedIndex - found) {
        ++series;
        found += size(i);
        if (found == flattenedIndex) {
          break;
        }
      } else {
        resolution = flattenedIndex - found;
        break;
      }
    }

    return new int[] {series,resolution};
  }
}
