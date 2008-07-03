//
// ImageIOReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ImageIOReader is the superclass for file format readers
 * that use the javax.imageio package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ImageIOReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ImageIOReader.java">SVN</a></dd></dl>
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

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    byte[] t = ImageTools.getBytes(openImage(no, x, y, w, h), false, no);
    int bytesPerChannel = w * h;
    if (t.length > bytesPerChannel) {
      for (int i=0; i<3; i++) {
        System.arraycopy(t, i * bytesPerChannel, buf, i*bytesPerChannel,
          bytesPerChannel);
      }
    }
    else System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int, int, int, int, int) */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    RandomAccessStream ras = new RandomAccessStream(currentId);
    DataInputStream dis =
      new DataInputStream(new BufferedInputStream(ras, 4096));
    BufferedImage b = ImageIO.read(dis);
    ras.close();
    dis.close();
    return b.getSubimage(x, y, w, h);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ImageIOReader.initFile(" + id + ")");
    super.initFile(id);

    status("Populating metadata");
    core.imageCount[0] = 1;
    BufferedImage img =
      ImageIO.read(new DataInputStream(new RandomAccessStream(currentId)));

    core.sizeX[0] = img.getWidth();
    core.sizeY[0] = img.getHeight();

    core.rgb[0] = img.getRaster().getNumBands() > 1;

    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? 3 : 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = ImageTools.getPixelType(img);
    core.interleaved[0] = false;
    core.littleEndian[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    // populate the metadata store
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
    // CTR CHECK
//    for (int i=0; i<core.sizeC[0]; i++) {
//      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
//        null, null, null, null, null, null, null, null, null, null, null, null,
//        null, null, null, null);
//    }
  }

}
