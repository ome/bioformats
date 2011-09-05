//
// ExtensionFileFilter.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter based on file extensions, for use with a JFileChooser.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/ExtensionFileFilter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/ExtensionFileFilter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ExtensionFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  // -- Fields --

  /** List of valid extensions. */
  private String[] exts;

  /** Description. */
  private String desc;

  // -- Constructors --

  /** Constructs a new filter that accepts the given extension. */
  public ExtensionFileFilter(String extension, String description) {
    this(new String[] {extension}, description);
  }

  /** Constructs a new filter that accepts the given extensions. */
  public ExtensionFileFilter(String[] extensions, String description) {
    exts = new String[extensions.length];
    System.arraycopy(extensions, 0, exts, 0, extensions.length);
    StringBuffer sb = new StringBuffer(description);
    boolean first = true;
    for (int i=0; i<exts.length; i++) {
      if (exts[i] == null) exts[i] = "";
      if (exts[i].equals("")) continue;
      if (first) {
        sb.append(" (");
        first = false;
      }
      else sb.append(", ");
      sb.append("*.");
      sb.append(exts[i]);
    }
    if (!first) {
      sb.append(")");
    }
    desc = sb.toString();
  }

  // -- ExtensionFileFilter API methods --

  /** Gets the filter's first valid extension. */
  public String getExtension() { return exts[0]; }

  /** Gets the filter's valid extensions. */
  public String[] getExtensions() { return exts; }

  // -- FileFilter API methods --

  /** Accepts files with the proper extensions. */
  public boolean accept(File f) {
    if (f.isDirectory()) return true;

    String name = f.getName().toLowerCase();

    for (int i=0; i<exts.length; i++) {
      if (name.endsWith(exts[i].toLowerCase())) return true;
    }

    return false;
  }

  /** Gets the filter's description. */
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  public String toString() { return "ExtensionFileFilter: " + desc; }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  public int compareTo(Object o) {
    return desc.compareToIgnoreCase(((FileFilter) o).getDescription());
  }

}
