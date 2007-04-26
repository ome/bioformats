//
// IFormatWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.IOException;

/** Interface for all biological file format writers. */
public interface IFormatWriter extends IFormatHandler {

  /**
   * Saves the given image to the current file.
   * If this image is the last one in the file, the last flag must be set.
   */
  void saveImage(Image image, boolean last) throws FormatException, IOException;

  /**
   * Saves the given byte array to the current file.
   * If this is the last array to be written, the last flag must be set.
   */
  void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException;

  /** Reports whether the writer can save multiple images to a single file. */
  boolean canDoStacks();

  /** Sets the color model. */
  void setColorModel(ColorModel cm);

  /** Gets the color model. */
  ColorModel getColorModel();

  /** Sets the frames per second to use when writing. */
  void setFramesPerSecond(int rate);

  /** Gets the frames per second to use when writing. */
  int getFramesPerSecond();

  /** Gets the available compression types. */
  String[] getCompressionTypes();

  /** Gets the supported pixel types. */
  int[] getPixelTypes();

  /** Checks if the given pixel type is supported. */
  boolean isSupportedType(int type);

  /** Sets the current compression type. */
  void setCompression(String compress) throws FormatException;

  /** A utility method for converting a file from the command line. */
  boolean testConvert(String[] args) throws FormatException, IOException;

  // -- Deprecated API methods --

  /** @deprecated Replaced by {@link #saveImage(Image, boolean)} */
  void saveImage(String id, Image image, boolean last)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #canDoStacks()} */
  boolean canDoStacks(String id) throws FormatException;

  /** @deprecated Replaced by {@link #getPixelTypes()} */
  int[] getPixelTypes(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isSupportedType(int type)} */
  boolean isSupportedType(String id, int type)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #saveImage(Image, boolean)} */
  void save(String id, Image image, boolean last)
    throws FormatException, IOException;

}
