/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.gui;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter that recognizes files from a union of other filters.
 */
public class ComboFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  // -- Fields --

  /** List of filters to be combined. */
  private FileFilter[] filts;

  /** Description. */
  private String desc;

  // -- Constructor --

  /** Constructs a new filter from a list of other filters. */
  public ComboFileFilter(FileFilter[] filters, String description) {
    filts = new FileFilter[filters.length];
    System.arraycopy(filters, 0, filts, 0, filters.length);
    desc = description;
  }

  // -- ComboFileFilter API methods --

  /** Gets the list of file filters forming this filter combination. */
  public FileFilter[] getFilters() {
    FileFilter[] ff = new FileFilter[filts.length];
    System.arraycopy(filts, 0, ff, 0, filts.length);
    return ff;
  }

  // -- Static ComboFileFilter API methods --

  /**
   * Sorts the given list of file filters, and combines filters with identical
   * descriptions into a combination filter that accepts anything any of its
   * constituant filters do.
   */
  public static FileFilter[] sortFilters(FileFilter[] filters) {
    return sortFilters(new Vector(Arrays.asList(filters)));
  }

  /**
   * Sorts the given list of file filters, and combines filters with identical
   * descriptions into a combination filter that accepts anything any of its
   * constituant filters do.
   */
  public static FileFilter[] sortFilters(Vector filters) {
    // sort filters alphanumerically
    Collections.sort(filters);

    // combine matching filters
    int len = filters.size();
    Vector v = new Vector(len);
    for (int i=0; i<len; i++) {
      FileFilter ffi = (FileFilter) filters.elementAt(i);
      int ndx = i + 1;
      while (ndx < len) {
        FileFilter ff = (FileFilter) filters.elementAt(ndx);
        if (!ffi.getDescription().equals(ff.getDescription())) break;
        ndx++;
      }
      if (ndx > i + 1) {
        // create combination filter for matching filters
        FileFilter[] temp = new FileFilter[ndx - i];
        for (int j=0; j<temp.length; j++) {
          temp[j] = (FileFilter) filters.elementAt(i + j);
        }
        v.add(new ComboFileFilter(temp, temp[0].getDescription()));
        i += temp.length - 1; // skip next temp-1 filters
      }
      else v.add(ffi);
    }
    FileFilter[] result = new FileFilter[v.size()];
    v.copyInto(result);
    return result;
  }

  // -- FileFilter API methods --

  /** Accepts files with the proper filename prefix. */
  @Override
  public boolean accept(File f) {
    for (int i=0; i<filts.length; i++) {
      if (filts[i].accept(f)) return true;
    }
    return false;
  }

  /** Returns the filter's description. */
  @Override
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ComboFileFilter: ");
    sb.append(desc);
    for (int i=0; i<filts.length; i++) {
      sb.append("\n\t");
      sb.append(filts[i].toString());
    }
    return sb.toString();
  }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  @Override
  public int compareTo(Object o) {
    return desc.compareToIgnoreCase(((FileFilter) o).getDescription());
  }

}
