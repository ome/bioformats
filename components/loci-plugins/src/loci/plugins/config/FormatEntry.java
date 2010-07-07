//
// FormatEntry.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

package loci.plugins.config;

import java.awt.Component;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * A list entry for the configuration window's Formats tab.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/config/FormatEntry.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/config/FormatEntry.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FormatEntry implements Comparable<Object> {

  // -- Fields --

  private Object reader;
  private String formatName;
  protected String readerName;
  protected String[] suffixes;
  protected String[] labels;
  protected Component[] widgets;

  // -- Constructor --

  public FormatEntry(PrintWriter log, Object reader) {
    this.reader = reader;
    Class<?> readerClass = reader.getClass();
    String n = readerClass.getName();
    readerName = n.substring(n.lastIndexOf(".") + 1, n.length() - 6);
    try {
      Method getFormat = readerClass.getMethod("getFormat");
      formatName = (String) getFormat.invoke(this.reader);
      Method getSuffixes = readerClass.getMethod("getSuffixes");
      suffixes = (String[]) getSuffixes.invoke(this.reader);
      log.println("Successfully queried " + readerName + " reader.");
    }
    catch (Throwable t) {
      log.println("Error querying " + readerName + " reader:");
      t.printStackTrace(log);
      log.println();
      suffixes = new String[0];
    }
    // create any extra widgets for this format, if any
    IFormatWidgets fw = null;
    String fwClassName = "loci.plugins.config." + readerName + "Widgets";
    try {
      Class<?> fwClass = Class.forName(fwClassName);
      fw = (IFormatWidgets) fwClass.newInstance();
      log.println("Initialized extra widgets for " + readerName + " reader.");
    }
    catch (Throwable t) {
      if (t instanceof ClassNotFoundException) {
        // no extra widgets for this reader
      }
      else {
        log.println("Error constructing widgets for " +
          readerName + " reader:");
        t.printStackTrace(log);
      }
    }
    labels = fw == null ? new String[0] : fw.getLabels();
    widgets = fw == null ? new Component[0] : fw.getWidgets();
  }

  // -- Comparable API methods --

  public int compareTo(Object o) {
    return toString().compareTo(o.toString());
  }

  // -- Object API methods --

  public int hashCode() {
    return toString().hashCode();
  }

  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof FormatEntry)) return false;
    return compareTo(o) == 0;
  }

  public String toString() {
    return "<html>" + formatName;
  }

}
