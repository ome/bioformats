//
// AsciiImage.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.tools;

import java.awt.image.BufferedImage;

/**
 * A utility class for outputting a BufferedImage as ASCII text.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/AsciiImage.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/AsciiImage.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class AsciiImage {

  // -- Constants --

  private static final String NL = System.getProperty("line.separator");
  private static final String CHARS = " .,-+o*O#";

  // -- Fields --

  private BufferedImage img;

  // -- Constructor --

  public AsciiImage(BufferedImage img) {
    this.img = img;
  }

  // -- Object methods --

  public String toString() {
    final int width = img.getWidth();
    final int height = img.getHeight();
    final StringBuilder sb = new StringBuilder();
    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        final int pix = img.getRGB(x, y);
        final int a = 0xff & (pix >> 24);
        final int r = 0xff & (pix >> 16);
        final int g = 0xff & (pix >> 8);
        final int b = 0xff & pix;
        final int avg = (r + g + b) / 3;
        sb.append(getChar(avg));
      }
      sb.append(NL);
    }
    return sb.toString();
  }

  // -- Helper methods --

  private char getChar(int value) {
    int index = CHARS.length() * value / 256;
    return CHARS.charAt(index);
  }

}
