/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.Prefs;
import ij.gui.GenericDialog;
import loci.formats.FormatException;
import loci.formats.services.LuraWaveServiceImpl;

/**
 * Utility methods for dealing with proprietary LuraWave licensing.
 */
public final class LuraWave {

  // -- Constants --

  public static final int MAX_TRIES = 5;
  public static final String TOO_MANY_TRIES =
    "Too many LuraWave license code attempts; giving up.";

  // -- Constructor --

  private LuraWave() { }

  // -- Utility methods --

  /** Reads LuraWave license code from ImageJ preferences, if available. */
  public static String initLicenseCode() {
    String code = Prefs.get(LuraWaveServiceImpl.LICENSE_PROPERTY, null);
    if (code != null && code.length() >= 6) {
      System.setProperty(LuraWaveServiceImpl.LICENSE_PROPERTY, code);
    }
    return code;
  }

  /**
   * Returns true if the given exception was cause
   * by a missing or invalid LuraWave license code.
   */
  public static boolean isLicenseCodeException(FormatException exc) {
    String msg = exc == null ? null : exc.getMessage();
    return msg != null && (msg.equals(LuraWaveServiceImpl.NO_LICENSE_MSG) ||
      msg.startsWith(LuraWaveServiceImpl.INVALID_LICENSE_MSG));
  }

  /**
   * Prompts the user to enter their LuraWave
   * license code in an ImageJ dialog window.
   */
  public static String promptLicenseCode(String code, boolean first) {
    GenericDialog gd = new GenericDialog("LuraWave License Code");
    if (!first) gd.addMessage("Invalid license code; try again.");
    gd.addStringField("LuraWave_License Code: ", code, 16);
    gd.showDialog();
    if (gd.wasCanceled()) return null;
    code = gd.getNextString();
    if (code != null) Prefs.set(LuraWaveServiceImpl.LICENSE_PROPERTY, code);
    return code;
  }

}
