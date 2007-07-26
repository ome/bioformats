//
// ImageIOReader.java
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

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    byte[] b = ImageTools.getBytes(openImage(no), false, no);
    int bytesPerChannel = core.sizeX[0] * core.sizeY[0];
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

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    RandomAccessStream ras = new RandomAccessStream(currentId);
    DataInputStream dis =
      new DataInputStream(new BufferedInputStream(ras, 4096));
    BufferedImage b = ImageIO.read(dis);
    ras.close();
    dis.close();
    return b;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException { }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException { }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ImageIOReader.initFile(" + id + ")");
    super.initFile(id);

    status("Populating metadata");
    core.imageCount[0] = 1;
    BufferedImage img = openImage(0);

    core.sizeX[0] = img.getWidth();
    core.sizeY[0] = img.getHeight();

    core.rgb[0] = img.getRaster().getNumBands() > 1;

    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? 3 : 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = ImageTools.getPixelType(img);
    core.interleaved[0] = true;
    core.littleEndian[0] = false;

    // populate the metadata store
    MetadataStore store = getMetadataStore();
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]),
      core.currentOrder[0],
      null,
      null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
