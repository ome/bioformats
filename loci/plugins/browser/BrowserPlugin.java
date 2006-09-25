//
// BrowserPlugin.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

package loci.plugins.browser;

import ij.plugin.PlugIn;
import loci.plugins.Util;

/**
 * BrowserPlugin is a wrapper for the LOCI Data Browser plugin for ImageJ,
 * to avoid direct references to classes from the external Bio-Formats library.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class BrowserPlugin implements PlugIn {

  // -- Fields --

  private LociDataBrowser ldb = new LociDataBrowser();

  // -- PlugIn API methods --

  public void run(String arg) {
    if (!Util.checkVersion()) return;
    if (!Util.checkLibraries(true, true, false)) return;
    ldb.run(arg);
  }

}
