//
// LociConfig.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

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

package loci.plugins.config;

import ij.IJ;
import ij.plugin.PlugIn;

/**
 * An ImageJ plugin for displaying the LOCI plugins configuration dialog.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/LociConfig.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/LociConfig.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociConfig implements PlugIn {

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    IJ.showStatus("Loading LOCI plugins configuration dialog");
    ConfigWindow cw = new ConfigWindow();
    cw.setVisible(true);
    IJ.showStatus("");
  }

  // -- Main method --

  public static void main(String[] args) {
    new LociConfig().run(null);
  }

}
