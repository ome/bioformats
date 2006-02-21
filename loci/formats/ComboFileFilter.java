//
// ComboFileFilter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

import java.io.File;
import javax.swing.filechooser.FileFilter;

/** A file filter that recognizes files from a union of other filters. */
public class ComboFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{
  
  // -- Fields --

  /** list of filters to be combined */
  private FileFilter[] filts;

  /** description */
  private String desc;


  // -- Constructor --

  /** construct a new filter from a list of other filters */
  public ComboFileFilter(FileFilter[] filters, String description) {
    filts = new FileFilter[filters.length];
    System.arraycopy(filters, 0, filts, 0, filters.length);
    desc = description;
  }


  // -- FileFilter API methods --

  /** accept files with the proper filename prefix */
  public boolean accept(File f) {
    for (int i=0; i<filts.length; i++) {
      if (filts[i].accept(f)) return true;
    }
    return false;
  }
    
  /** return the filter's description */
  public String getDescription() { return desc; }


  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    return desc.compareTo(((FileFilter) o).getDescription());
  }

}
