//
// ImageIOWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataRetrieve;

/**
 * ImageIOWriter is the superclass for file format writers that use the
 * javax.imageio library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/out/ImageIOWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/out/ImageIOWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageIOWriter extends FormatWriter {

  // -- Fields --

  protected String kind;

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

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    MetadataRetrieve meta = getMetadataRetrieve();
    BufferedImage image = AWTImageTools.makeImage(buf,
      interleaved, meta, series);
    savePlane(no, image, x, y, w, h);
  }

  /**
   * @see loci.formats.IFormatWriter#savePlane(int, Object, int, int, int, int)
   */
  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (!(plane instanceof Image)) {
      throw new IllegalArgumentException(
        "Object to save must be a java.awt.Image");
    }
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException("ImageIOWriter does not support writing tiles");
    }

    BufferedImage img = AWTImageTools.makeBuffered((Image) plane, cm);
    ImageIO.write(img, kind, out);
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.UINT8, FormatTools.UINT16};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    return Image.class;
  }

}
