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
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * INRReader is the file format reader for INR files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/INRReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/INRReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class INRReader extends FormatReader {

  // -- Constants --

  private static final String INR_MAGIC = "#INRIMAGE";
  private static final int HEADER_SIZE = 256;

  // -- Constructor --

  /** Constructs a new INR reader. */
  public INRReader() {
    super("INR", "inr");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = INR_MAGIC.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(INR_MAGIC) == 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long planeSize = (long) FormatTools.getPlaneSize(this);
    in.seek(HEADER_SIZE + no * planeSize);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    // read the fixed-size header

    LOGGER.info("Reading header");

    String header = in.readString(HEADER_SIZE);
    String[] lines = header.split("\n");

    Double physicalSizeX = null;
    Double physicalSizeY = null;
    Double physicalSizeZ = null;
    boolean isSigned = false;
    int nBits = 0;

    CoreMetadata m = core.get(0);

    for (String line : lines) {
      int index = line.indexOf("=");
      if (index >= 0) {
        String key = line.substring(0, index);
        String value = line.substring(index + 1);

        addGlobalMeta(key, value);

        if (key.equals("XDIM")) {
          m.sizeX = Integer.parseInt(value);
        }
        else if (key.equals("YDIM")) {
          m.sizeY = Integer.parseInt(value);
        }
        else if (key.equals("ZDIM")) {
          m.sizeZ = Integer.parseInt(value);
        }
        else if (key.equals("VDIM")) {
          m.sizeT = Integer.parseInt(value);
        }
        else if (key.equals("TYPE")) {
          isSigned = value.toLowerCase().startsWith("signed");
        }
        else if (key.equals("PIXSIZE")) {
          String bits = value.substring(0, value.indexOf(" "));
          nBits = Integer.parseInt(bits);
        }
        else if (key.equals("VX")) {
          physicalSizeX = new Double(value);
        }
        else if (key.equals("VY")) {
          physicalSizeY = new Double(value);
        }
        else if (key.equals("VZ")) {
          physicalSizeZ = new Double(value);
        }
      }
    }

    // finish populating core metadata

    LOGGER.info("Populating metadata");

    if (getSizeZ() == 0) {
      m.sizeZ = 1;
    }
    if (getSizeT() == 0) {
      m.sizeT = 1;
    }
    m.sizeC = 1;
    m.imageCount = getSizeZ() * getSizeT() * getSizeC();
    m.pixelType =
      FormatTools.pixelTypeFromBytes(nBits / 8, isSigned, false);
    m.dimensionOrder = "XYZTC";

    // populate the metadata store

    LOGGER.info("Populating OME metadata");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (physicalSizeX != null && physicalSizeX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          physicalSizeX);
      }
      if (physicalSizeY != null && physicalSizeY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          physicalSizeY);
      }
      if (physicalSizeZ != null && physicalSizeZ > 0) {
        store.setPixelsPhysicalSizeZ(new PositiveFloat(physicalSizeZ), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
          physicalSizeZ);
      }
    }
  }

}
