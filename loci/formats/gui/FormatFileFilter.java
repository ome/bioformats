//
// FormatFileFilter.java
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
import javax.swing.filechooser.FileFilter;
import loci.formats.IFormatReader;

/** A file filter for a biological file format, for use with a JFileChooser. */
public class FormatFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  // -- Fields --

  /** Associated file format reader. */
  private IFormatReader reader;

  /** Description. */
  private String desc;

  // -- Constructor --

  /** Constructs a new filter that accepts the given extension. */
  public FormatFileFilter(IFormatReader reader) {
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

  // -- FormatFileFilter API methods --

  /**
   * Accepts files in accordance with the file format reader.
   * @param allowOpen whether it is ok to open a file to determine its type.
   */
  public boolean accept(File f, boolean allowOpen) {
    if (f.isDirectory()) return true;
    return reader.isThisType(f.getPath(), allowOpen);
  }

  // -- FileFilter API methods --

  /** Accepts files in accordance with the file format reader. */
  public boolean accept(File f) { return accept(f, true); }

  /** Gets the filter's description. */
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  public String toString() { return "FormatFileFilter: " + desc; }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    return desc.compareTo(((FileFilter) o).getDescription());
  }

}
