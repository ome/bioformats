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
import java.math.BigInteger;

import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * MRCReader is the file format reader for MRC files.
 * Specifications available at
 * http://bio3d.colorado.edu/imod/doc/mrc_format.txt
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/MRCReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/MRCReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MRCReader extends FormatReader {

  // -- Constants --

  private static final String[] TYPES =
    {"mono", "tilt", "tilts", "lina", "lins"};

  // NB: Unfortunately, we cannot just look for "MAP " at offset 0xd0, which
  // works for modern .mrc files, because older IMOD versions did not put that
  // there, according to: http://bio3d.colorado.edu/imod/doc/mrc_format.txt

  private static final String[] MRC_SUFFIXES =
    {"mrc", "st", "ali", "map", "rec"};

  private static final int HEADER_SIZE = 1024;
  private static final int ENDIANNESS_OFFSET = 212;

  // -- Fields --

  /** Size of extended header */
  private int extHeaderSize = 0;

  // -- Constructor --

  /** Constructs a new MRC reader. */
  public MRCReader() {
    super("Medical Research Council", MRC_SUFFIXES);
    domains = new String[] {FormatTools.MEDICAL_DOMAIN, FormatTools.LM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /** @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return FormatTools.validStream(stream, HEADER_SIZE, false);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long planeSize = FormatTools.getPlaneSize(this);
    long offset = HEADER_SIZE + extHeaderSize + no * planeSize;

    if (offset + planeSize <= in.length() && offset >= 0) {
      in.seek(offset);
      readPlane(in, x, getSizeY() - h - y, w, h, buf);

      // reverse the order of the rows
      // planes are stored with the origin in the lower-left corner
      byte[] tmp = new byte[w * FormatTools.getBytesPerPixel(getPixelType())];
      for (int row=0; row<h/2; row++) {
        int src = row * tmp.length;
        int dest = (h - row - 1) * tmp.length;
        System.arraycopy(buf, src, tmp, 0, tmp.length);
        System.arraycopy(buf, dest, buf, src, tmp.length);
        System.arraycopy(tmp, 0, buf, dest, tmp.length);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      extHeaderSize = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    CoreMetadata m = core.get(0);

    LOGGER.info("Reading header");

    // check endianness

    in.seek(ENDIANNESS_OFFSET);
    m.littleEndian = in.read() == 68;

    // read dimension information from 1024 byte header

    in.seek(0);
    in.order(isLittleEndian());

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    m.sizeZ = in.readInt();

    // We are using BigInteger here because of the very real possiblity
    // of not just an int overflow but also a long overflow when multiplying
    // sizeX * sizeY * sizeZ.
    BigInteger v = BigInteger.valueOf(getSizeX());
    v = v.multiply(BigInteger.valueOf(getSizeY()));
    v = v.multiply(BigInteger.valueOf(getSizeZ()));
    if (getSizeX() < 0 || getSizeY() < 0 || getSizeZ() < 0 ||
      (v.compareTo(BigInteger.valueOf(in.length())) > 0))
    {
      LOGGER.debug("Detected endianness is wrong, swapping");
      m.littleEndian = !isLittleEndian();
      in.seek(0);
      in.order(isLittleEndian());
      m.sizeX = in.readInt();
      m.sizeY = in.readInt();
      m.sizeZ = in.readInt();
    }

    m.sizeC = 1;
    m.rgb = false;

    int mode = in.readInt();
    switch (mode) {
      case 0:
        m.pixelType = FormatTools.UINT8;
        break;
      case 1:
        m.pixelType = FormatTools.INT16;
        break;
      case 6:
        m.pixelType = FormatTools.UINT16;
        break;
      case 2:
        m.pixelType = FormatTools.FLOAT;
        break;
      case 3:
        m.pixelType = FormatTools.UINT32;
        break;
      case 4:
        m.pixelType = FormatTools.DOUBLE;
        break;
      case 16:
        m.sizeC = 3;
        m.pixelType = FormatTools.UINT8;
	m.rgb = true;
        break;
    }

    in.skipBytes(12);

    // pixel size = xlen / mx

    double xSize = 0d, ySize = 0d, zSize = 0d;

    if (level != MetadataLevel.MINIMUM) {
      int mx = in.readInt();
      int my = in.readInt();
      int mz = in.readInt();

      // physical sizes are stored in ångströms, we want them in µm
      xSize = (in.readFloat() / mx) / 10000.0;
      ySize = (in.readFloat() / my) / 10000.0;
      zSize = (in.readFloat() / mz) / 10000.0;

      addGlobalMeta("Pixel size (X)", xSize);
      addGlobalMeta("Pixel size (Y)", ySize);
      addGlobalMeta("Pixel size (Z)", zSize);

      addGlobalMeta("Alpha angle", in.readFloat());
      addGlobalMeta("Beta angle", in.readFloat());
      addGlobalMeta("Gamma angle", in.readFloat());

      in.skipBytes(12);

      // min, max and mean pixel values
    }
    else in.skipBytes(48);

    double minValue = in.readFloat();
    double maxValue = in.readFloat();

    addGlobalMeta("Minimum pixel value", minValue);
    addGlobalMeta("Maximum pixel value", maxValue);
    addGlobalMeta("Mean pixel value", in.readFloat());

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    double range = Math.pow(2, bytes * 8) - 1;
    double pixelTypeMin = 0;
    boolean signed = FormatTools.isSigned(getPixelType());
    if (signed) {
      pixelTypeMin -= (range / 2);
    }
    double pixelTypeMax = pixelTypeMin + range;

    if (pixelTypeMax < maxValue || pixelTypeMin > minValue && signed) {
      // make the pixel type unsigned
      switch (getPixelType()) {
        case FormatTools.INT8:
          m.pixelType = FormatTools.UINT8;
          break;
        case FormatTools.INT16:
          m.pixelType = FormatTools.UINT16;
          break;
        case FormatTools.INT32:
          m.pixelType = FormatTools.UINT32;
          break;
      }
    }

    in.skipBytes(4);

    extHeaderSize = in.readInt();

    if (level != MetadataLevel.MINIMUM) {
      in.skipBytes(64);

      int idtype = in.readShort();

      String type = "unknown";
      if (idtype >= 0 && idtype < TYPES.length) type = TYPES[idtype];

      addGlobalMeta("Series type", type);
      addGlobalMeta("Lens", in.readShort());
      addGlobalMeta("ND1", in.readShort());
      addGlobalMeta("ND2", in.readShort());
      addGlobalMeta("VD1", in.readShort());
      addGlobalMeta("VD2", in.readShort());

      for (int i=0; i<6; i++) {
        addGlobalMetaList("Angle", in.readFloat());
      }

      in.skipBytes(24);

      addGlobalMeta("Number of useful labels", in.readInt());

      for (int i=0; i<10; i++) {
        addGlobalMetaList("Label", in.readString(80));
      }
    }

    LOGGER.info("Populating metadata");

    m.sizeT = 1;
    m.dimensionOrder = isRGB() ? "XYCZT" : "XYZTC";
    m.imageCount = getSizeZ() * (isRGB() ? 1 : getSizeC());
    m.interleaved = true;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (level != MetadataLevel.MINIMUM) {
      PositiveFloat sizeX = FormatTools.getPhysicalSizeX(xSize);
      PositiveFloat sizeY = FormatTools.getPhysicalSizeY(ySize);
      PositiveFloat sizeZ = FormatTools.getPhysicalSizeZ(zSize);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
    }
  }

}
