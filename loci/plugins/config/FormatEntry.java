//
// FormatEntry.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

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

package loci.plugins.config;

import java.lang.reflect.Method;

/**
 * A list entry for the configuration window's Formats tab.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/FormatEntry.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/FormatEntry.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FormatEntry implements Comparable {

  // -- Fields --

  private Object reader;
  private String name;
  protected String[] suffixes;

  // -- Constructor --

  public FormatEntry(Object reader) {
    this.reader = reader;
    try {
      Method getFormat = reader.getClass().getMethod("getFormat", null);
      name = (String) getFormat.invoke(reader, null);
      Method getSuffixes = reader.getClass().getMethod("getSuffixes", null);
      suffixes = (String[]) getSuffixes.invoke(reader, null);
    }
    catch (Throwable t) {
      t.printStackTrace();
      suffixes = new String[0];
    }
  }

  // -- Comparable API methods --

  public int compareTo(Object o) {
    return toString().compareTo(o.toString());
  }

  // -- Object API methods --

  public String toString() {
    return "<html>" + name;
  }

}
