//
// About.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006 Christopher Peterson.

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

package loci.ome.notebook;

import javax.swing.JOptionPane;

/** Displays a small information dialog about this package. */
public abstract class About {

  public static void main(String[] args) {
    JOptionPane.showMessageDialog(null,
      "OME Metadata Notebook\n" +
      "Built @date@\n\n" +
      "The OME Metadata Notebook is LOCI software written by\n" +
      "Christopher Peterson.\n" +
      "http://www.loci.wisc.edu/software/#notebook",
      "OME Metadata Notebook", JOptionPane.INFORMATION_MESSAGE);
    System.exit(0);
  }

}
