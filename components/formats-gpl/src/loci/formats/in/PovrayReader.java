/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
 * PovrayReader is the file format reader for POV-Ray .df3 files.
 *
 * @see <a href=http://www.povray.org/documentation/view/3.6.1/374/>http://www.povray.org/documentation/view/3.6.1/374/<a>
 */
public class PovrayReader extends FormatReader {

  // -- Constants --

  private static final int HEADER_SIZE = 6;

  // -- Constructor --

  /** Constructs a new POV-Ray reader. */
  public PovrayReader() {
    super("POV-Ray", "df3");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
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

    in.seek(HEADER_SIZE + FormatTools.getPlaneSize(this) * no);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);

    m.littleEndian = false;

    in.order(isLittleEndian());

    m.sizeX = in.readShort();
    m.sizeY = in.readShort();
    m.sizeZ = in.readShort();

    long fileLength = in.length() - HEADER_SIZE;
    int nBytes = (int) (fileLength / (getSizeX() * getSizeY() * getSizeZ()));

    m.pixelType = FormatTools.pixelTypeFromBytes(nBytes, false, false);
    m.sizeC = 1;
    m.sizeT = 1;
    m.rgb = false;
    m.dimensionOrder = "XYZCT";
    m.imageCount = getSizeZ() * getSizeC() * getSizeT();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
