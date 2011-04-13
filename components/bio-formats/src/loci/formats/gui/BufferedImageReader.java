//
// BufferedImageReader.java
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

package loci.formats.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ReaderWrapper;

/**
 * A reader wrapper for reading image planes as BufferedImage objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/BufferedImageReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/BufferedImageReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class BufferedImageReader extends ReaderWrapper {

  // -- Utility methods --

  /**
   * Converts the given reader into a BufferedImageReader, wrapping if needed.
   */
  public static BufferedImageReader makeBufferedImageReader(IFormatReader r) {
    if (r instanceof BufferedImageReader) return (BufferedImageReader) r;
    return new BufferedImageReader(r);
  }

  // -- Constructors --

  /** Constructs a BufferedImageReader around a new image reader. */
  public BufferedImageReader() { super(); }

  /** Constructs a BufferedImageReader with the given reader. */
  public BufferedImageReader(IFormatReader r) { super(r); }

  // -- BufferedImageReader methods --

  /** Obtains the specified image from the current file. */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return openImage(no, 0, 0, getSizeX(), getSizeY());
  }

  /**
   * Obtains a sub-image of the specified image, whose upper-left corner is
   * given by (x, y).
   */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    Class dataType = getNativeDataType();
    if (BufferedImage.class.isAssignableFrom(dataType)) {
      // native data type is compatible with BufferedImage
      return (BufferedImage) openPlane(no, x, y, w, h);
    }
    else {
      // must construct BufferedImage from byte array
      return AWTImageTools.openImage(openBytes(no, x, y, w, h), this, w, h);
    }
  }

  /** Obtains a thumbnail for the specified image from the current file. */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    Class dataType = getNativeDataType();
    if (BufferedImage.class.isAssignableFrom(dataType)) {
      BufferedImage img = AWTImageTools.makeUnsigned(openImage(no));
      return AWTImageTools.scale(img, getThumbSizeX(), getThumbSizeY(), false);
    }

    byte[] thumbBytes = openThumbBytes(no);
    return AWTImageTools.openImage(thumbBytes, this,
      getThumbSizeX(), getThumbSizeY());
  }

}
