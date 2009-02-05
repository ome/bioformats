//
// ARFReader.java
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

import java.io.IOException;
import loci.common.DataTools;
import loci.common.RandomAccessStream;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ARFReader is the file format reader for Axon Raw Format (ARF) files,
 * produced by INDEC BioSystems's
 * <a href="http://www.imagingworkbench.com/">Imaging Workbench</a>
 * software.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ARFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ARFReader.java">SVN</a></dd></dl>
 *
 * @author Johannes Schindelin johannes.schindelin at gmx.de
 */
public class ARFReader extends FormatReader {

  // -- Constructor --

  /** Constructs a new ARF reader. */
  public ARFReader() {
    super("ARF", "arf");
    blockCheckLen = 4;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    byte endian1 = stream.readByte();
    byte endian2 = stream.readByte();
    return ((endian1 == 1 && endian2 == 0) || (endian1 == 0 && endian2 == 1)) &&
      stream.readString(2).startsWith("AR");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int bytesPerPixel = FormatTools.getBytesPerPixel(getPixelType());
    int sizeX = getSizeX();
    int sizeY = getSizeY();
    in.seek(12 + 512 + bytesPerPixel * (x + sizeX * (y + sizeY * no)));
    for (int q=0; q < h; q++) {
      in.read(buf, q * y * bytesPerPixel, w * bytesPerPixel);
      if (w < sizeX)
        in.skipBytes((sizeX - w) * bytesPerPixel);
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ARFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading file header");

    in.seek(0);
    short endian1 = in.readByte();
    short endian2 = in.readByte();
    if (endian1 == 1 && endian2 == 0)
      in.order(true);
    else if (endian1 == 0 && endian2 == 1)
      in.order(false);
    else
      throw new FormatException("Undefined endianness");

    in.skipBytes(2);
    short version = in.readShort();
    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();

    int bitsPerPixel = in.readShort();
    if (bitsPerPixel > 32)
      throw new FormatException("Too many bits per pixel: " + bitsPerPixel);
    if (bitsPerPixel > 16)
      core[0].pixelType = FormatTools.UINT32;
    else if (bitsPerPixel > 7)
      core[0].pixelType = FormatTools.UINT16;
    else
      core[0].pixelType = FormatTools.UINT8;

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = version == 2 ? in.readShort() : 1;
    core[0].imageCount = core[0].sizeT;
    core[0].littleEndian = endian1 == 1;
    core[0].rgb = false;
    core[0].indexed = false;
    core[0].interleaved = false;
    core[0].dimensionOrder = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
