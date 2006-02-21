//
// FormatFileFilter.java
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

/** A file filter for a biological file format, for use with a JFileChooser. */
public class FormatFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  /** Associated file format reader. */
  private FormatReader reader;

  /** Description. */
  private String desc;


  // -- Constructor --

  /** Constructs a new filter that accepts the given extension. */
  public FormatFileFilter(FormatReader reader) {
    this.reader = reader;
    StringBuffer sb = new StringBuffer(reader.getFormat());
    String[] exts = reader.getSuffixes();
    boolean first = true;
    for (int i=0; i<exts.length; i++) {
      if (exts[i] == null || exts[i].equals("")) continue;
      if (first) {
        sb.append(" (");
        first = false;
      }
      else sb.append(", ");
      sb.append("*.");
      sb.append(exts[i]);
    }
    sb.append(")");
    desc = sb.toString();
  }


  // -- FileFilter API methods --

  /** Accepts files in accordance with the file format reader. */
  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    return reader.isThisType(f.getPath());
  }

  /** Gets the filter's description. */
  public String getDescription() { return desc; }


  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    return desc.compareTo(((FileFilter) o).getDescription());
  }

}
