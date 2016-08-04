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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * WATOPReader is the file format reader for WA Technology .wat files.
 */
public class WATOPReader extends FormatReader {

  // -- Constants --

  private static final int HEADER_SIZE = 4864;
  private static final String WAT_MAGIC_STRING = "0TOPSystem W.A.Technology";

  // -- Fields --

  // -- Constructor --

  /** Constructs a new WA Technology reader. */
  public WATOPReader() {
    super("WA Technology TOP", "wat");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 25;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(WAT_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
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
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);
    m.littleEndian = true;
    in.order(isLittleEndian());

    String comment = null;

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    if (level != MetadataLevel.MINIMUM) {
      in.seek(49);
      comment = in.readString(33);
    }

    in.seek(211);
    int year = in.readInt();
    int month = in.readInt();
    int day = in.readInt();
    int hour = in.readInt();
    int min = in.readInt();
    String date = year + "-" + month + "-" + day + "T" + hour + ":" + min;
    date = DateTools.formatDate(date, "yyyy-MM-dd'T'HH:mm");

    in.skipBytes(8);

    double xSize = in.readInt() / 100d;
    double ySize = in.readInt() / 100d;
    double zSize = in.readInt() / 100d;

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();

    if (level != MetadataLevel.MINIMUM) {
      double tunnelCurrent = in.readInt() / 1000d;
      double sampleVolts = in.readInt() / 1000d;

      in.skipBytes(180);

      int originalZMax = in.readInt();
      int originalZMin = in.readInt();
      int zMax = in.readInt();
      int zMin = in.readInt();

      addGlobalMeta("Comment", comment);
      addGlobalMeta("X size (in um)", xSize);
      addGlobalMeta("Y size (in um)", ySize);
      addGlobalMeta("Z size (in um)", zSize);
      addGlobalMeta("Tunnel current (in amps)", tunnelCurrent);
      addGlobalMeta("Sample volts", sampleVolts);
      addGlobalMeta("Acquisition date", date);
    }

    m.pixelType = FormatTools.INT16;
    m.sizeC = 1;
    m.sizeZ = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.dimensionOrder = "XYZCT";
    m.rgb = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (level != MetadataLevel.MINIMUM) {
      store.setImageDescription(comment, 0);

      Length sizeX =
        FormatTools.getPhysicalSizeX((double) xSize / getSizeX());
      Length sizeY =
        FormatTools.getPhysicalSizeY((double) ySize / getSizeY());
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
