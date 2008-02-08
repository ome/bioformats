//
// About.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson, Philip Huettl and Francis Wong.

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

import javax.swing.JOptionPane;

/**
 * Displays a small information dialog about this package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/About.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/About.java">SVN</a></dd></dl>
 */
public final class About {

  private About() { }

  public static void about() {
    JOptionPane.showMessageDialog(null,
      "LOCI Plugins for ImageJ\n" +
      "Built @date@\n\n" +
      "Copyright 2005-@year@ Laboratory for Optical and Computational\n" +
      "Instrumentation; http://www.loci.wisc.edu/software\n\n" +
      "The 4D Data Browser is written by Melissa Linkert,\n" +
      "Christopher Peterson, Francis Wong and Curtis Rueden.\n" +
      "http://www.loci.wisc.edu/ome/browser.html\n\n" +
      "The Bio-Formats Importer and Exporter are written by\n" +
      "Melissa Linkert and Curtis Rueden.\n" +
      "http://www.loci.wisc.edu/ome/formats.html\n\n" +
      "The OME plugins for ImageJ are written by Melissa Linkert\n" +
      "and Philip Huettl.\n" +
      "http://www.loci.wisc.edu/ome/imagej.html",
      "LOCI Plugins for ImageJ", JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
