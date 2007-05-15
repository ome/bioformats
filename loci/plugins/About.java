//
// About.java
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

import javax.swing.JOptionPane;

/** Displays a small information dialog about this package. */
public final class About {

  private About() { }

  public static void about() {
    JOptionPane.showMessageDialog(null,
      "LOCI Plugins for ImageJ\n" +
      "Built @date@\n\n" +
      "Copyright 2006-@year@ Laboratory for Optical and Computational\n" +
      "Instrumentation; http://www.loci.wisc.edu/software\n\n" +
      "The 4D Data Browser is written by Christopher Peterson,\n" +
      "Francis Wong, Curtis Rueden and Melissa Linkert.\n" +
      "http://www.loci.wisc.edu/4d/#browser\n\n" +
      "The Bio-Formats Importer and Exporter are written by\n" +
      "Melissa Linkert and Curtis Rueden.\n" +
      "http://www.loci.wisc.edu/ome/formats.html\n\n" +
      "The OME plugins for ImageJ are written by Philip Huettl\n" +
      "and Melissa Linkert.\n" +
      "http://www.loci.wisc.edu/ome/imagej.html",
      "LOCI Plugins for ImageJ", JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args) {
    about();
    System.exit(0);
  }

}
