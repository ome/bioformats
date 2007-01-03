//
// ImageIOWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import loci.formats.*;

/**
 * ImageIOWriter is the superclass for file format writers that use the
 * javax.imageio library.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageIOWriter extends FormatWriter {

  // -- Fields --

  protected String kind;
  protected DataOutputStream out;

  // -- Constructors --

  /**
   * Constructs an ImageIO-based writer with the given name, default suffix
   * and output type (e.g., png, jpeg).
   */
  public ImageIOWriter(String format, String suffix, String kind) {
    super(format, suffix);
    this.kind = kind;
  }

  /**
   * Constructs an ImageIO-based writer with the given name, default suffixes
   * and output type (e.g., png, jpeg). */
  public ImageIOWriter(String format, String[] suffixes, String kind) {
    super(format, suffixes);
    this.kind = kind;
  }

  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The last flag is ignored, since this writer produces single-image files.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);
    if (ImageTools.getPixelType(img) == FormatReader.FLOAT) {
      throw new FormatException("Floating point data not supported.");
    }
    out = new DataOutputStream(new BufferedOutputStream(
      new FileOutputStream(id), 4096));
    ImageIO.write(img, kind, out); 
  }

  /* @see IFormatWriter#close() */
  public void close() throws FormatException, IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
  }

  /** Reports whether the writer can save multiple images to a single file. */
  public boolean canDoStacks(String id) { return false; }

}
