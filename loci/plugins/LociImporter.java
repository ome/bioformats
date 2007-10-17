//
// LociImporter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.plugin.PlugIn;
import java.util.HashSet;

/**
 * ImageJ plugin for reading files using the LOCI Bio-Formats package.
 * Wraps core logic in {@link loci.plugins.Importer}, to avoid direct
 * references to classes in the external Bio-Formats library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/LociImporter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/LociImporter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LociImporter implements PlugIn {

  // -- Fields --

  /**
   * Flag indicating whether last operation was successful.
   * NB: This field must be updated properly, or the plugin
   * will stop working correctly with HandleExtraFileTypes.
   */
  public boolean success;

  /**
   * Flag indicating whether last operation was canceled.
   * NB: This field must be updated properly, or the plugin
   * will stop working correctly with HandleExtraFileTypes.
   */
  public boolean canceled;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    canceled = false;
    success = false;
    if ("about".equals(arg)) About.about();
    else {
      if (!Checker.checkVersion()) return;
      HashSet missing = new HashSet();
      Checker.checkLibrary(Checker.BIO_FORMATS, missing);
      Checker.checkLibrary(Checker.OME_JAVA_XML, missing);
      if (!Checker.checkMissing(missing)) return;
      new Importer(this).run(arg);
    }
  }

}
