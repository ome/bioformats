//
// StringOption.java
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A string option for one of the LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/prefs/StringOption.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/prefs/StringOption.java">SVN</a></dd></dl>
 */
public class StringOption extends Option {

  // -- Constants --

  /** INI key indicating option's possible values. */
  public static final String INI_POSSIBLE = "values";

  // -- Fields --

  /** List of possible values. */
  protected List<String> possibleValues;

  /** The option's default value. */
  protected String defaultValue;

  /** The option's current value. */
  protected String value;

  // -- Static utility methods --

  /** Parses a string of comma-separated values into a list of tokens. */
  public static List<String> parseList(String s) {
    if (s == null) return null;
    String[] array = s.split(",");
    for (int i=0; i<array.length; i++) array[i] = array[i].trim();
    return Arrays.asList(array);
  }

  // -- Constructors --

  /** Constructs a string option with the parameters from the given map. */
  public StringOption(HashMap<String, String> entry) {
    this(entry.get(INI_KEY), entry.get(INI_LABEL), entry.get(INI_INFO),
      entry.get(INI_DEFAULT), parseList(entry.get(INI_POSSIBLE)));
  }

  /**
   * Constructs a string option with the given parameters.
   * If possible values list is null, any string value is allowed.
   */
  public StringOption(String key, String label, String info,
    String defaultValue, List<String> possibleValues)
  {
    super(key, label, info);
    this.defaultValue = defaultValue;
    this.possibleValues = possibleValues;
  }

  // -- StringOption methods --

  /** Gets the list of possible values. */
  public List<String> getPossible() {
    return possibleValues;
  }

  /** Adds a possible value to the list. */
  public void addPossible(String val) {
    possibleValues.add(val);
  }

  /** Removes a possible value from the list. */
  public void removePossible(String val) {
    possibleValues.remove(val);
  }

  /** Gets the default value of the option. */
  public String getDefault() {
    return defaultValue;
  }

  /** Gets the current value of the option. */
  public String getValue() {
    return value;
  }

  // -- Option methods --

  /* @see Option#loadOption() */
  public void loadOption() {
    value = Prefs.get(KEY_PREFIX + key, defaultValue);
  }

  /* @see Option#saveOption() */
  public void saveOption() {
    Prefs.set(KEY_PREFIX + key, value);
  }

}
