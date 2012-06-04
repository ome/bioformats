/*
 * #%L
 * OME Plugins for ImageJ: a collection of ImageJ plugins
 * including the Download from OME and Upload to OME plugins.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.plugins.ome;

import ij.plugin.PlugIn;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import loci.ome.io.OMEReader;

/**
 * Displays a small information dialog about the OME Plugins package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-plugins/src/loci/plugins/ome/About.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-plugins/src/loci/plugins/ome/About.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class About implements PlugIn {

  // -- Constants --

  /** URL of LOCI Software web page. */
  public static final String URL_LOCI_SOFTWARE =
    "http://loci.wisc.edu/software";

  /** URL of OME Plugins web page. */
  public static final String URL_OME_PLUGINS =
    "http://www.openmicroscopy.org/" +
    "site/support/legacy/ome-server/imagej-plugin";

  // -- Runnable API methods --

  public void run(String arg) {
    about();
  }

  // -- Static utility methods --

  public static void about() {
    String msg = "<html>" +
      "OME Plugins for ImageJ, revision @vcs.revision@, built @date@" +
      "<br>Copyright (C) 2005 - @year@ Open Microscopy Environment:" +
      "<ul>" +
      "<li>Board of Regents of the University of Wisconsin-Madison</li>" +
      "<li>Glencoe Software, Inc.</li>" +
      "<li>University of Dundee</li>" +
      "</ul>" +
      "<i>" + URL_LOCI_SOFTWARE + "</i>" +
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
