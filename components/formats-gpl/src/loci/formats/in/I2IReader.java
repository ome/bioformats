/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * I2IReader is the file format reader for I2I files.
 */
public class I2IReader extends FormatReader {

  // -- Constants --

  private static final int HEADER_SIZE = 1024;

  // -- Constructor --

  /** Constructs a new I2I reader. */
  public I2IReader() {
    super("I2I", new String[] {"i2i"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /** @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return FormatTools.validStream(stream, HEADER_SIZE, false);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long planeSize = FormatTools.getPlaneSize(this);
    long offset = HEADER_SIZE + no * planeSize;

    if (offset + planeSize <= in.length() && offset >= 0) {
      in.seek(offset);
      readPlane(in, x, y, w, h, buf);
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);

    char pixelType = (char) in.readByte();

    switch (pixelType) {
      case 'I':
        m.pixelType = FormatTools.INT16;
        break;
      case 'R':
        m.pixelType = FormatTools.FLOAT;
        break;
      case 'C':
        throw new FormatException("Complex pixel data not yet supported");
      default:
        throw new FormatException("Invalid pixel type: " + pixelType);
    }

    if ((char) in.readByte() != ' ') {
      throw new FormatException("Expected space after pixel type character");
    }

    String x = in.readString(6).trim();
    String y = in.readString(6).trim();
    String z = in.readString(6).trim();

    m.littleEndian = (char) in.readByte() != 'B';
    m.sizeX = Integer.parseInt(x);
    m.sizeY = Integer.parseInt(y);
    m.sizeZ = Integer.parseInt(z);

    in.order(isLittleEndian());

    short minPixelValue = in.readShort();
    short maxPixelValue = in.readShort();
    short xCoordinate = in.readShort();
    short yCoordinate = in.readShort();
    int n = in.readShort();

    in.skipBytes(33); // reserved for future use

    for (int i=0; i<15; i++) {
      String history = in.readString(64);
      addGlobalMetaList("Image history", history.trim());
    }

    addGlobalMeta("Minimum intensity value", minPixelValue);
    addGlobalMeta("Maximum intensity value", maxPixelValue);
    addGlobalMeta("Image position X", xCoordinate);
    addGlobalMeta("Image position Y", yCoordinate);

    // the stored Z value is always the total number of planes
    // when an additional dimension is defined, the Z value
    // needs to be adjusted to reflect the true Z count
    if (n > 0) {
      m.sizeZ /= n;
    }

    // the user defines what the N dimension means
    // in practice, it could be timepoints, channels, etc. but we have no
    // way of knowing based on the file metadata
    m.sizeT = n;

    m.imageCount = getSizeZ() * getSizeT();
    m.sizeC = 1;
    m.rgb = false;
    m.dimensionOrder = "XYZTC";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
