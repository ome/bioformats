/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;

/**
 * Base class for ImageJ preferences for plugins.
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

  /** Table of options defining this set of preferences. */
  protected HashMap<String, Option> options;

  // -- Constructor --

  public OptionsList(String path, Class<?> c) throws IOException {
    this(new IniParser().parseINI(path, c));
  }

  public OptionsList(IniList ini) {
    options = new HashMap<String, Option>();
    for (IniTable entry : ini) {
      String type = entry.get(INI_TYPE);
      Option o;
      if (type.equals(TYPE_BOOLEAN)) o = new BooleanOption(entry);
      else if (type.equals(TYPE_STRING)) o = new StringOption(entry);
      else throw new IllegalArgumentException("Unknown type: " + type);
      options.put(o.getKey(), o);
    }
  }

  // -- OptionsList methods --

  /** Parses option values from the given argument string. */
  public void parseOptions(String arg) {
    for (Option o : options.values()) o.parseOption(arg);
  }

  /** Loads option values from the ImageJ preferences file. */
  public void loadOptions() {
    for (Option o : options.values()) o.loadOption();
  }

  /** Saves option values to the ImageJ preferences file. */
  public void saveOptions() {
    for (Option o : options.values()) o.saveOption();
  }

  /**
   * Gets whether the option with the given key
   * is saved to the ImageJ preferences file.
   */
  public boolean isSaved(String key) { return options.get(key).isSaved(); }

  /** Gets the label for the option with the given key. */
  public String getLabel(String key) { return options.get(key).getLabel(); }

  /** Gets the documentation for the option with the given key. */
  public String getInfo(String key) { return options.get(key).getInfo(); }

  /** Gets the possible values for the string option with the given key. */
  public String[] getPossible(String key) {
    Option o = options.get(key);
    if (o instanceof StringOption) {
      Vector<String> possible = ((StringOption) o).getPossible();
      return possible.toArray(new String[0]);
    }
    throw new IllegalArgumentException("Not a string key: " + key);
  }

  /**
   * Gets whether the specified value is a possible one
   * for the string option with the given key.
   */
  public boolean isPossible(String key, String value) {
    Option o = options.get(key);
    if (o instanceof StringOption) {
      Vector<String> possible = ((StringOption) o).getPossible();
      return possible.contains(value);
    }
    throw new IllegalArgumentException("Not a string key: " + key);
  }

  /** Gets the default value of the boolean option with the given key. */
  public boolean isSetByDefault(String key) {
    Option o = options.get(key);
    if (o instanceof BooleanOption) return ((BooleanOption) o).getDefault();
    throw new IllegalArgumentException("Not a boolean key: " + key);
  }

  /** Gets the default value of the string option with the given key. */
  public String getDefaultValue(String key) {
    Option o = options.get(key);
    if (o instanceof StringOption) return ((StringOption) o).getDefault();
    throw new IllegalArgumentException("Not a string key: " + key);
  }

  /** Gets the value of the boolean option with the given key. */
  public boolean isSet(String key) {
    Option o = options.get(key);
    if (o instanceof BooleanOption) return ((BooleanOption) o).getValue();
    throw new IllegalArgumentException("Not a boolean key: " + key);
  }

  /** Gets the value of the string option with the given key. */
  public String getValue(String key) {
    Option o = options.get(key);
    if (o instanceof StringOption) return ((StringOption) o).getValue();
    throw new IllegalArgumentException("Not a string key: " + key);
  }

  /** Sets the value of the boolean option with the given key. */
  public void setValue(String key, boolean value) {
    Option o = options.get(key);
    if (o instanceof BooleanOption) ((BooleanOption) o).setValue(value);
    else throw new IllegalArgumentException("Not a boolean key: " + key);
  }

  /** Sets the value of the string option with the given key. */
  public void setValue(String key, String value) {
    Option o = options.get(key);
    if (o instanceof StringOption) ((StringOption) o).setValue(value);
    else throw new IllegalArgumentException("Not a string key: " + key);
  }

  /** Gets the option with the given key. */
  public Option getOption(String key) {
    return options.get(key);
  }

  /** Gets the boolean option with the given key. */
  public BooleanOption getBooleanOption(String key) {
    Option o = options.get(key);
    if (o instanceof BooleanOption) return (BooleanOption) o;
    throw new IllegalArgumentException("Not a boolean key: " + key);
  }

  /** Gets the string option with the given key. */
  public StringOption getStringOption(String key) {
    Option o = options.get(key);
    if (o instanceof StringOption) return (StringOption) o;
    throw new IllegalArgumentException("Not a string key: " + key);
  }

  // -- Object API methods --

  /* @see java.lang.Object#equals(Object) */
  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof OptionsList)) return false;
    OptionsList optionsList = (OptionsList) o;
    if (options.size() != optionsList.options.size()) {
      return false;
    }
    for (String key : options.keySet()) {
      Option a = options.get(key);
      Option b = optionsList.options.get(key);
      if ((a != null && b == null) || (a == null && b != null)) {
        return false;
      }
      else if (a == null && b == null) continue;

      if ((a instanceof BooleanOption) && (b instanceof BooleanOption)) {
        if (((BooleanOption) a).getValue() != ((BooleanOption) b).getValue()) {
          return false;
        }
      }
      else if ((a instanceof StringOption) && (b instanceof StringOption)) {
        String aValue = ((StringOption) a).getValue();
        String bValue = ((StringOption) b).getValue();
        if (aValue == null && bValue == null) continue;
        if (aValue == null) {
          return false;
        }
        if (aValue == null || !aValue.equals(bValue)) {
          return false;
        }
      }
      else {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return options.hashCode();
  }

}
