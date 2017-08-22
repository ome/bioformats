/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

package loci.plugins;

import ij.plugin.PlugIn;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import loci.formats.FormatTools;
import loci.formats.IFormatHandler;

/**
 * Displays a small information dialog about the Bio-Formats Plugins package.
 */
public final class About implements PlugIn {

  // -- Constants --

  /** URL of the Bio-Formats web page. */
  public static final String URL_SOFTWARE =
    "https://www.openmicroscopy.org/bio-formats";

  /** URL of Bio-Formats ImageJ web page. */
  public static final String URL_BIO_FORMATS_IMAGEJ =
    "http://www.openmicroscopy.org/site/support/bio-formats/users/imagej/features.html";

  /** URL of Data Browser web page. */
  public static final String URL_DATA_BROWSER =
    "http://loci.wisc.edu/software/data-browser";

  // -- PlugIn API methods --

  @Override
  public void run(String arg) {
    about();
  }

  // -- Static utility methods --

  public static void about() {
    String msg = "<html>" +
      "Bio-Formats Plugins for ImageJ" +
      "<br>Release: " + FormatTools.VERSION +
      "<br>Build date: " + FormatTools.DATE +
      "<br>VCS revision: " + FormatTools.VCS_REVISION +
      "<br>Copyright (C) 2005 - " + FormatTools.YEAR +
      " Open Microscopy Environment:" +
      "<ul>" +
      "<li>Board of Regents of the University of Wisconsin-Madison</li>" +
      "<li>Glencoe Software, Inc.</li>" +
      "<li>University of Dundee</li>" +
      "</ul>" +
      "<i>" + URL_SOFTWARE + "</i>" +
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
    JOptionPane.showMessageDialog(null, msg, "Bio-Formats Plugins for ImageJ",
      JOptionPane.INFORMATION_MESSAGE, bioFormatsLogo);
  }

  // -- Main method --

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
