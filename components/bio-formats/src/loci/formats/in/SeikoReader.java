/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * SeikoReader is the file format reader for Seiko .xqd/.xqf files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SeikoReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SeikoReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SeikoReader extends FormatReader {

  // -- Constants --

  private static final int HEADER_SIZE = 2944;

  // -- Fields --

  // -- Constructor --

  /** Constructs a new Seiko reader. */
  public SeikoReader() {
    super("Seiko", new String[] {"xqd", "xqf"});
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(HEADER_SIZE);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    core[0].littleEndian = true;
    in.order(isLittleEndian());

    String comment = null;
    double xSize = 0d, ySize = 0d;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.seek(40);
      comment = in.readCString();

      in.seek(156);

      xSize = in.readFloat();
      in.skipBytes(4);
      ySize = in.readFloat();

      addGlobalMeta("Comment", comment);
    }

    in.seek(1402);

    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYZCT";
    core[0].pixelType = FormatTools.UINT16;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(comment, 0);
      if (xSize > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(xSize), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}", xSize);
      }
      if (ySize > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(ySize), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}", ySize);
      }
    }
  }

}
