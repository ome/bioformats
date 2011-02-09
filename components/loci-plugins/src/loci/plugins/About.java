//
// About.java
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

package loci.plugins;

import ij.plugin.PlugIn;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import loci.formats.IFormatHandler;

/**
 * Displays a small information dialog about the LOCI Plugins package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/About.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/About.java">SVN</a></dd></dl>
 */
public final class About implements PlugIn {

  // -- Constants --

  /** URL of LOCI Software web page. */
  public static final String URL_LOCI_SOFTWARE =
    "http://www.loci.wisc.edu/software";

  /** URL of Bio-Formats ImageJ web page. */
  public static final String URL_BIO_FORMATS_IMAGEJ =
    "http://www.loci.wisc.edu/bio-formats/imagej";

  /** URL of Data Browser web page. */
  public static final String URL_DATA_BROWSER =
    "http://www.loci.wisc.edu/software/data-browser";

  // -- PlugIn API methods --

  public void run(String arg) {
    about();
  }

  // -- Static utility methods --

  public static void about() {
    String msg = "<html>" +
      "LOCI Plugins for ImageJ, revision @vcs.revision@, built @date@" +
      "<br>Release: @release.version@" +
      "<br>Copyright 2005-@year@ UW-Madison LOCI" +
      "<br><i>" + URL_LOCI_SOFTWARE + "</i>" +
      "<br>" +
      "<br><b>Bio-Formats Importer</b>, <b>Bio-Formats Exporter</b> " +
      "and <b>Stack Slicer</b>" +
      "<br>Authors: Curtis Rueden, Melissa Linkert" +
      "<br><i>" + URL_BIO_FORMATS_IMAGEJ + "</i>" +
      "<br>" +
      "<br><b>Data Browser</b>" +
      "<br>Authors: Curtis Rueden, Melissa Linkert, Chris Peterson" +
      "<br><i>" + URL_DATA_BROWSER + "</i>";
    ImageIcon bioFormatsLogo = new ImageIcon(
      IFormatHandler.class.getResource("bio-formats-logo.png"));
    JOptionPane.showMessageDialog(null, msg, "LOCI Plugins for ImageJ",
      JOptionPane.INFORMATION_MESSAGE, bioFormatsLogo);
  }

  // -- Main method --

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
