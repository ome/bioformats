//
// About.java
//

/*
OME Plugins for ImageJ: a collection of ImageJ plugins
including the Download from OME and Upload to OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Philip Huettl and Curtis Rueden.

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

package loci.plugins.ome;

import ij.plugin.PlugIn;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import loci.ome.io.OMEReader;

/**
 * Displays a small information dialog about the OME Plugins package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/ome-plugins/src/loci/plugins/ome/About.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/ome-plugins/src/loci/plugins/ome/About.java">SVN</a></dd></dl>
 */
public final class About implements PlugIn {

  // -- Constants --

  /** URL of LOCI Software web page. */
  public static final String URL_LOCI_SOFTWARE =
    "http://www.loci.wisc.edu/software";

  /** URL of OME Plugins web page. */
  public static final String URL_OME_PLUGINS =
    "http://ome-xml.org/wiki/OmePlugins";

  // -- Runnable API methods --

  public void run(String arg) {
    about();
  }

  // -- Static utility methods --

  public static void about() {
    String msg = "<html>" +
      "OME Plugins for ImageJ, revision @vcs.revision@, built @date@" +
      "<br>Copyright 2005-@year@ UW-Madison LOCI" +
      "<br><i>" + URL_LOCI_SOFTWARE + "</i>" +
      "<br>" +
      "<br><b>Download from OME</b> and <b>Upload to OME</b>" +
      "<br>Authors: Melissa Linkert, Philip Huettl" +
      "<br><i>" + URL_OME_PLUGINS + "</i>";
    ImageIcon omeLogo = new ImageIcon(
      OMEReader.class.getResource("ome-logo.png"));
    JOptionPane.showMessageDialog(null, msg, "OME Plugins for ImageJ",
      JOptionPane.INFORMATION_MESSAGE, omeLogo);
  }

  // -- Main method --

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
