//
// LociImporter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
Data Browser, OME Plugin and Bio-Formats Exporter. Copyright (C) 2006
Melissa Linkert, Christopher Peterson, Curtis Rueden, Philip Huettl
and Francis Wong.

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

/**
 * ImageJ plugin for reading files using the LOCI Bio-Formats package.
 * Wraps core logic in loci.plugins.Importer, to avoid direct references
 * to classes in the external Bio-Formats library.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LociImporter implements PlugIn {

  // -- Fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success;

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public synchronized void run(String arg) {
    canceled = false;
    success = false;
    if ("about".equals(arg)) About.about();
    else if (Util.checkVersion() &&
      Util.checkLibraries(true, true, false, false))
    {
      new Importer(this).run(arg);
    }
  }

}
