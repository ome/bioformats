//
// LociPrefs.java
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

package loci.plugins.util;

import ij.Prefs;
import loci.formats.IFormatReader;

/**
 * Utility methods for ImageJ preferences for LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/LociPrefs.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/LociPrefs.java">SVN</a></dd></dl>
 */
public class LociPrefs {

  // -- Constants --

  public static final String PREF_READER_ENABLED = "bioformats.enabled";
  public static final String PREF_READER_WINDOWLESS = "bioformats.windowless";

  public static final String PREF_ND2_NIKON = "bioformats.nd2.nikon";
  public static final String PREF_PICT_QTJAVA = "bioformats.pict.qtjava";
  public static final String PREF_QT_QTJAVA = "bioformats.qt.qtjava";
  public static final String PREF_SDT_INTENSITY = "bioformats.sdt.intensity";

  // -- Constructor --

  private LociPrefs() { }

  // -- Utility methods --

  /**
   * Gets whether windowless mode should be used when
   * opening this reader's currently initialized dataset.
   */
  public static boolean isWindowless(IFormatReader r) {
    return getPref(PREF_READER_WINDOWLESS, r.getClass(), false);
  }

  public static boolean isReaderEnabled(Class c) {
    return getPref(PREF_READER_ENABLED, c, true);
  }

  public static boolean isND2Nikon() {
    return Prefs.get(PREF_ND2_NIKON, false);
  }

  public static boolean isPictQTJava() {
    return Prefs.get(PREF_PICT_QTJAVA, false);
  }

  public static boolean isQTQTJava() {
    return Prefs.get(PREF_QT_QTJAVA, false);
  }

  public static boolean isSDTIntensity() {
    return Prefs.get(PREF_SDT_INTENSITY, false);
  }

  // -- Helper methods --

  private static boolean getPref(String pref, Class c, boolean defaultValue) {
    String n = c.getName();
    String readerName = n.substring(n.lastIndexOf(".") + 1, n.length() - 6);
    String key = pref + "." + readerName;
    return Prefs.get(key, defaultValue);
  }

}
