//
// NoExtensionFileFilter.java
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

/**
 * A file filter that selects files with no extension,
 * for use with a JFileChooser.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/gui/NoExtensionFileFilter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/gui/NoExtensionFileFilter.java">SVN</a></dd></dl>
 */
public class NoExtensionFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  // -- FileFilter API methods --

  /** Accepts files with no extension. */
  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    return f.getName().lastIndexOf('.') < 0;
  }

  /** Gets the filter's description. */
  public String getDescription() { return "Files with no extension"; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  public String toString() { return "NoExtensionFileFilter"; }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    FileFilter filter = (FileFilter) o;
    return getDescription().compareToIgnoreCase(filter.getDescription());
  }

}
