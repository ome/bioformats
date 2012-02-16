//
// ImageIOReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;

/**
 * ImageIOReader is the superclass for file format readers
 * that use the javax.imageio package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ImageIOReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ImageIOReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageIOReader extends BIFormatReader {

  // -- Fields --

  private BufferedImage img;

  // -- Constructors --

  /** Constructs a new ImageIOReader. */
  public ImageIOReader(String name, String suffix) {
    super(name, suffix);
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  /** Constructs a new ImageIOReader. */
  public ImageIOReader(String name, String[] suffixes) {
    super(name, suffixes);
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      img = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#openPlane(int, int, int, int, int int) */
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);

    return AWTImageTools.getSubimage(img, isLittleEndian(), x, y, w, h);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    LOGGER.info("Populating metadata");
    core[0].imageCount = 1;
    RandomAccessInputStream ras = new RandomAccessInputStream(currentId);
    DataInputStream dis = new DataInputStream(ras);
    img = ImageIO.read(dis);
    dis.close();
    if (img == null) throw new FormatException("Invalid image stream");

    core[0].sizeX = img.getWidth();
    core[0].sizeY = img.getHeight();

    core[0].rgb = img.getRaster().getNumBands() > 1;

    core[0].sizeZ = 1;
    core[0].sizeC = isRGB() ? 3 : 1;
    core[0].sizeT = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = AWTImageTools.getPixelType(img);
    core[0].interleaved = false;
    core[0].littleEndian = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

    // populate the metadata store
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
