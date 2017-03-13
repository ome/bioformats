/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.CoreMetadata;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

/**
 * LIMReader is the file format reader for Laboratory Imaging/Nikon LIM files.
 */
public class LIMReader extends FormatReader {

  // -- Constants --

  private static final int PIXELS_OFFSET = 0x94b;

  // -- Fields --

  private boolean isCompressed;

  // -- Constructor --

  /** Constructs a new LIM reader. */
  public LIMReader() {
    super("Laboratory Imaging", "lim");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(PIXELS_OFFSET);
    readPlane(in, x, y, w, h, buf);

    // swap red and blue channels
    if (isRGB()) {
      for (int i=0; i<buf.length/3; i++) {
        byte tmp = buf[i*3];
        buf[i*3] = buf[i*3 + 2];
        buf[i*3 + 2] = tmp;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) isCompressed = false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);

    m.littleEndian = true;
    in.order(isLittleEndian());

    m.sizeX = in.readShort() & 0x7fff;
    m.sizeY = in.readShort();
    int bits = in.readShort();

    while (bits % 8 != 0) bits++;
    if ((bits % 3) == 0) {
      m.sizeC = 3;
      bits /= 3;
    }
    m.pixelType = FormatTools.pixelTypeFromBytes(bits / 8, false, false);

    isCompressed = in.readShort() != 0;
    addGlobalMeta("Is compressed", isCompressed);
    if (isCompressed) {
      throw new UnsupportedCompressionException(
        "Compressed LIM files not supported.");
    }

    m.imageCount = 1;
    m.sizeZ = 1;
    m.sizeT = 1;
    if (getSizeC() == 0) m.sizeC = 1;
    m.rgb = getSizeC() > 1;
    m.dimensionOrder = "XYZCT";
    m.indexed = false;
    m.falseColor = false;
    m.interleaved = true;
    m.metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
