//
// ImageIOReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * ImageIOReader is the superclass for file format readers
 * that use the javax.imageio package.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageIOReader extends FormatReader {

  // -- Constructors --

  /** Constructs a new ImageIOReader. */
  public ImageIOReader(String name, String suffix) { super(name, suffix); }

  /** Constructs a new ImageIOReader. */
  public ImageIOReader(String name, String[] suffixes) {
    super(name, suffixes);
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an image file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given image file. */
  public int getImageCount(String id) throws FormatException, IOException {
    return 1;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    throw new FormatException("ImageIOReader.openBytes(String, int) " +
      "not implemented");
  }

  /** Obtains the image from the given image file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (no != 0) throw new FormatException("Invalid image number: " + no);
    return ImageIO.read(new DataInputStream(
      new BufferedInputStream(new FileInputStream(id), 4096)));
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException { }

}
