//
// ImageIOReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import loci.formats.*;

/**
 * ImageIOReader is the superclass for file format readers
 * that use the javax.imageio package.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageIOReader extends FormatReader {

  // -- Fields --

  /** Flag indicating image is RGB. */
  private boolean rgb;

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

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return rgb;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    byte[] b = ImageTools.getBytes(openImage(id, no), false, no);
    int bytesPerChannel = getSizeX(id) * getSizeY(id);
    if (b.length > bytesPerChannel) {
      byte[] tmp = b;
      b = new byte[bytesPerChannel * 3];
      for (int i=0; i<3; i++) {
        System.arraycopy(tmp, i * bytesPerChannel, b, i*bytesPerChannel,
          bytesPerChannel);
      }
    }
    return b;
  }

  /** Obtains the image from the given image file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    return ImageIO.read(new DataInputStream(new BufferedInputStream(
      new RandomAccessStream(id), 4096)));
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException { }

  /** Initializes the given file. */
  public void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);

    sizeX[0] = openImage(id, 0).getWidth();
    sizeY[0] = openImage(id, 0).getHeight();

    rgb = openBytes(id, 0).length > getSizeX(id) * getSizeY(id);

    sizeZ[0] = 1;
    sizeC[0] = rgb ? 3 : 1;
    sizeT[0] = 1;
    currentOrder[0] = "XYCZT";

    // populate the metadata store

    MetadataStore store = getMetadataStore(id);

    pixelType[0] = FormatReader.UINT8;
    store.setPixels(
      new Integer(getSizeX(id)),
      new Integer(getSizeY(id)),
      new Integer(1),
      new Integer(3),
      new Integer(1),
      new Integer(pixelType[0]),
      new Boolean(false),
      getDimensionOrder(id),
      null);
    setMetadataStore(store);
  }

}
