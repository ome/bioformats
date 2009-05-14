//
// OptionsList.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.prefs;

import ij.Prefs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import loci.common.IniParser;

/**
 * Base class for ImageJ preferences for LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/prefs/OptionsList.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/prefs/OptionsList.java">SVN</a></dd></dl>
 */
public class OptionsList {

  // -- Constants --

  /** INI key indicating option's type. */
  public static final String INI_TYPE = "type";

  /** String indicating option is of type boolean. */
  public static final String TYPE_BOOLEAN = "boolean";

  /** String indicating option is of type string. */
  public static final String TYPE_STRING = "string";

  // -- Fields --

  /** List of options defining this set of preferences. */
  protected Vector<Option> options;

  // -- Constructor --

  public OptionsList(String path) throws IOException {
    this(new IniParser().parseINI(path));
  }

  public OptionsList(Vector<HashMap<String, String>> ini) {
    options = new Vector<Option>();
    for (HashMap<String, String> entry : ini) {
      String type = entry.get(INI_TYPE);
      if (type.equals(TYPE_BOOLEAN)) {
        options.add(new BooleanOption(entry));
      }
      else if (type.equals(TYPE_STRING)) {
        options.add(new StringOption(entry));
      }
      else throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

}
