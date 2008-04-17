//
// ComboFileFilter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.gui;

import java.io.File;
import java.util.*;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter that recognizes files from a union of other filters.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/gui/ComboFileFilter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/gui/ComboFileFilter.java">SVN</a></dd></dl>
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
  public boolean accept(File f) {
    for (int i=0; i<filts.length; i++) {
      if (filts[i].accept(f)) return true;
    }
    return false;
  }

  /** Returns the filter's description. */
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  public String toString() {
    StringBuffer sb = new StringBuffer("ComboFileFilter: ");
    sb.append(desc);
    for (int i=0; i<filts.length; i++) {
      sb.append("\n\t");
      sb.append(filts[i].toString());
    }
    return sb.toString();
  }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    return desc.compareToIgnoreCase(((FileFilter) o).getDescription());
  }

}
