//
// ARFReader.java
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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * ARFReader is the file format reader for Axon Raw Format (ARF) files,
 * produced by INDEC BioSystems's
 * <a href="http://www.imagingworkbench.com/">Imaging Workbench</a>
 * software.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ARFReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ARFReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Johannes Schindelin johannes.schindelin at gmx.de
 */
public class ARFReader extends FormatReader {

  // -- Constants --

  private static final long PIXELS_OFFSET = 524;

  // -- Constructor --

  /** Constructs a new ARF reader. */
  public ARFReader() {
    super("ARF", "arf");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    byte endian1 = stream.readByte();
    byte endian2 = stream.readByte();
    return ((endian1 == 1 && endian2 == 0) || (endian1 == 0 && endian2 == 1)) &&
      stream.readString(2).equals("AR");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(PIXELS_OFFSET + no * FormatTools.getPlaneSize(this));
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    // parse file header

    LOGGER.info("Reading file header");

    byte endian1 = in.readByte();
    byte endian2 = in.readByte();
    boolean little;
    if (endian1 == 1 && endian2 == 0) little = true;
    else if (endian1 == 0 && endian2 == 1) little = false;
    else throw new FormatException("Undefined endianness");
    in.order(little);

    in.skipBytes(2); // 'AR' signature
    int version = in.readUnsignedShort();
    int width = in.readUnsignedShort();
    int height = in.readUnsignedShort();
    int bitsPerPixel = in.readUnsignedShort();
    int numImages = version == 2 ? in.readUnsignedShort() : 1;
    // NB: The next 510 bytes are unused 'application dependent' data,
    // followed by raw image data with no padding.

    // populate core metadata

    core[0].sizeX = width;
    core[0].sizeY = height;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = numImages;

    int bpp = bitsPerPixel / 8;
    if ((bitsPerPixel % 8) != 0) bpp++;
    core[0].pixelType = FormatTools.pixelTypeFromBytes(bpp, false, false);

    core[0].bitsPerPixel = bitsPerPixel;
    core[0].imageCount = numImages;
    core[0].dimensionOrder = "XYCZT";
    core[0].orderCertain = true;
    core[0].littleEndian = little;
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].metadataComplete = true;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate original metadata

      addGlobalMeta("Endianness", little ? "little" : "big");
      addGlobalMeta("Version", version);
      addGlobalMeta("Width", width);
      addGlobalMeta("Height", height);
      addGlobalMeta("Bits per pixel", bitsPerPixel);
      addGlobalMeta("Image count", numImages);
    }

    // populate OME metadata

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
