//
// ImageIOWriter.java
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

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(String, byte[], boolean) */
  public void saveBytes(String id, byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    throw new FormatException("Not implemented yet.");
  }

  /* @see loci.formats.IFormatWriter#saveImage(String, Image, boolean) */ 
  public void saveImage(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);
    if (ImageTools.getPixelType(img) == FormatTools.FLOAT) {
      throw new FormatException("Floating point data not supported.");
    }
    out = new DataOutputStream(new BufferedOutputStream(
      new FileOutputStream(id), 4096));
    ImageIO.write(img, kind, out);
  }

  /* @see loci.formats.IFormatWriter#close() */
  public void close() throws FormatException, IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */ 
  public boolean canDoStacks(String id) { return false; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String id) throws FormatException, IOException {
    return new int[] {FormatTools.UINT8, FormatTools.UINT16};
  }

}
