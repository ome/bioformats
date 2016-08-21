/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.prefs;

import ij.Macro;
import ij.Prefs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * A string option for one of the plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/prefs/StringOption.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/prefs/StringOption.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class StringOption extends Option {

  // -- Constants --

  /** INI key indicating option's possible values. */
  public static final String INI_POSSIBLE = "values";

  // -- Fields --

  /** The option's default value. */
  protected String defaultValue;

  /** List of possible values. */
  protected Vector<String> possibleValues;

  /** The option's current value. */
  protected String value;

  // -- Static utility methods --

  /** Parses a string of comma-separated values into a list of tokens. */
  public static Vector<String> parseList(String s) {
    if (s == null) return null;
    String[] array = s.split(",");
    for (int i=0; i<array.length; i++) array[i] = array[i].trim();
    return new Vector<String>(Arrays.asList(array));
  }

  // -- Constructors --

  /** Constructs a string option with the parameters from the given map. */
  public StringOption(HashMap<String, String> entry) {
    this(entry.get(INI_KEY),
      !"false".equals(entry.get(INI_SAVE)), // default behavior is saved
      entry.get(INI_LABEL),
      entry.get(INI_INFO),
      entry.get(INI_DEFAULT),
      parseList(entry.get(INI_POSSIBLE)));
  }

  /**
   * Constructs a string option with the given parameters.
   * If possible values list is null, any string value is allowed.
   */
  public StringOption(String key, boolean save, String label,
    String info, String defaultValue, Vector<String> possibleValues)
  {
    super(key, save, label, info);
    this.defaultValue = defaultValue;
    this.possibleValues = possibleValues;
    this.value = defaultValue;
  }

  // -- StringOption methods --

  /** Gets the list of possible values. */
  public Vector<String> getPossible() {
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

  /** Sets the current value of the option. */
  public void setValue(String value) {
    if (possibleValues == null) this.value = value;
    else {
      for (String p : possibleValues) {
        if (p.equals(value)) {
          this.value = value;
          return;
        }
      }
      throw new IllegalArgumentException("'" +
        value + "' is not a possible value");
    }
  }

  // -- Option methods --

  /* @see Option#parseOption(String arg) */
  public void parseOption(String arg) {
    String keyValue = Macro.getValue(arg, key, value);
    if ((value == null || keyValue.equals(value)) && label != null) {
      value = Macro.getValue(arg, label, value);
    }
    else value = keyValue;
  }

  /* @see Option#loadOption() */
  public void loadOption() {
    value = Prefs.get(KEY_PREFIX + key, defaultValue);
  }

  /* @see Option#saveOption() */
  public void saveOption() {
    if (save) Prefs.set(KEY_PREFIX + key, value);
  }

}
