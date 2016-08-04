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

import java.util.HashMap;

/**
 * A boolean option for one of the plugins.
 */
public class BooleanOption extends Option {

  // -- Fields --

  /** The option's default value. */
  protected boolean defaultValue;

  /** The option's current value. */
  protected boolean value;

  // -- Constructors --

  /** Constructs a boolean option with the parameters from the given map. */
  public BooleanOption(HashMap<String, String> entry) {
    this(entry.get(INI_KEY),
      !"false".equals(entry.get(INI_SAVE)), // default behavior is saved
      entry.get(INI_LABEL),
      entry.get(INI_INFO),
      "true".equals(entry.get(INI_DEFAULT))); // default value is false
  }

  /** Constructs a boolean option with the given parameters. */
  public BooleanOption(String key, boolean save, String label,
    String info, boolean defaultValue)
  {
    super(key, save, label, info);
    this.defaultValue = defaultValue;
    this.value = defaultValue;
  }

  // -- BooleanOption methods --

  /** Gets the default value of the option. */
  public boolean getDefault() {
    return defaultValue;
  }

  /** Gets the current value of the option. */
  public boolean getValue() {
    return value;
  }

  /** Sets the current value of the option. */
  public void setValue(boolean value) {
    this.value = value;
  }

  // -- Option methods --

  /* @see Option#parseOption(String arg) */
  @Override
  public void parseOption(String arg) {
    String s = Macro.getValue(arg, key, null);
    if (s != null) value = s.equalsIgnoreCase("true");
    else if (label != null) {
      s = Macro.getValue(arg, label, null);
      if (s != null) value = s.equalsIgnoreCase("true");
    }
  }

  /* @see Option#loadOption() */
  @Override
  public void loadOption() {
    value = Prefs.get(KEY_PREFIX + key, defaultValue);
  }

  /* @see Option#saveOption() */
  @Override
  public void saveOption() {
    if (save) Prefs.set(KEY_PREFIX + key, value);
  }

}
