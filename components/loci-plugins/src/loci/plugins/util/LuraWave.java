//
// LuraWave.java
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

package loci.plugins.util;

import ij.Prefs;
import ij.gui.GenericDialog;
import loci.formats.FormatException;
import loci.formats.services.LuraWaveServiceImpl;

/**
 * Utility methods for dealing with proprietary LuraWave licensing.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/LuraWave.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/LuraWave.java">SVN</a></dd></dl>
 */
public final class LuraWave {

  // -- Constants --

  public static final int MAX_ATTEMPTS = 5;
  public static final String TOO_MANY_ATTEMPTS =
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
