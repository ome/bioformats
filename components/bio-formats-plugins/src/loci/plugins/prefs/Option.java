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

import loci.common.IniTable;

/**
 * Base class for an option for one of the plugins.
 */
public abstract class Option {

  // -- Constants --

  /** Prefix to use for all plugins preferences. */
  public static final String KEY_PREFIX = "bioformats.";

  /** INI key indicating option's key. */
  public static final String INI_KEY = IniTable.HEADER_KEY;

  /** INI key indicating whether option should be saved to prefs file. */
  public static final String INI_SAVE = "save";

  /** INI key indicating option's label. */
  public static final String INI_LABEL = "label";

  /** INI key indicating option's info. */
  public static final String INI_INFO = "info";

  /** INI key indicating option's default value. */
  public static final String INI_DEFAULT = "default";

  // -- Fields --

  /** Key name for ImageJ preferences file. */
  protected String key;

  /** Whether option state should be saved to ImageJ preferences file. */
  protected boolean save;

  /**
   * Label describing the option, for use with ImageJ dialogs.
   * May contain underscores to produce a better macro variable name.
   */
  protected String label;

  /** Documentation about the option, in HTML. */
  protected String info;

  // -- Constructor --

  /** Constructs an option with the given parameters. */
  public Option(String key, boolean save, String label, String info) {
    this.key = key;
    if (key == null) throw new IllegalArgumentException("Null key");
    this.save = save;
    this.label = label;
    this.info = info;
  }

  // -- Option methods --

  /** Gets the option's key name. */
  public String getKey() {
    return key;
  }

  /** Gets whether option should be saved to ImageJ preferences file. */
  public boolean isSaved() {
    return save;
  }

  /** Gets label describing the option. */
  public String getLabel() {
    return label;
  }

  /** Gets documentation about the option. */
  public String getInfo() {
    return info;
  }

  // -- Abstract Option methods --

  /** Parses the option's value from the given argument string. */
  public abstract void parseOption(String arg);

  /** Loads the option's value from the ImageJ preferences file. */
  public abstract void loadOption();

  /** Saves the option's value to the ImageJ preferences file. */
  public abstract void saveOption();

}
