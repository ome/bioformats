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
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * OxfordInstrumentsReader is the file format reader for
 * Oxford Instruments .top files.
 */
public class OxfordInstrumentsReader extends FormatReader {

  // -- Constants --

  public static final String OXFORD_MAGIC_STRING = "Oxford Instruments";

  // -- Fields --

  private long headerSize = 0;

  // -- Constructor --

  /** Constructs a new Oxford Instruments reader. */
  public OxfordInstrumentsReader() {
    super("Oxford Instruments", "top");
    suffixNecessary = false;
    domains = new String[] {FormatTools.SPM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = OXFORD_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(OXFORD_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(headerSize);
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

    in.seek(48);

    String comment = in.readString(32);

    String dateTime = readDate();

    in.skipBytes(8);

    double xSize = -in.readFloat() + in.readFloat();
    in.skipBytes(20);
    double ySize = -in.readFloat() + in.readFloat();
    in.skipBytes(24);
    double zMin = in.readFloat();
    double zMax = in.readFloat();

    in.skipBytes(864);

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    in.skipBytes(28);
    if (getSizeX() == 0 && getSizeY() == 0) {
      m.sizeX = in.readInt();
      m.sizeY = in.readInt();
      in.skipBytes(196);
    }
    else in.skipBytes(204);
    m.pixelType = FormatTools.UINT16;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.rgb = false;
    m.indexed = false;
    m.dimensionOrder = "XYZCT";
    m.interleaved = false;

    if (FormatTools.getPlaneSize(this) + in.getFilePointer() > in.length()) {
      m.sizeY = 1;
    }

    int lutSize = in.readInt();
    in.skipBytes(lutSize);
    headerSize = in.getFilePointer();

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.skipBytes(FormatTools.getPlaneSize(this));

      int nMetadataStrings = in.readInt();
      for (int i=0; i<nMetadataStrings; i++) {
        int length = in.readInt();
        String s = in.readString(length);
        if (s.indexOf(':') != -1) {
          String key = s.substring(0, s.indexOf(':')).trim();
          String value = s.substring(s.indexOf(':') + 1).trim();
          if (!value.equals("-")) {
            addGlobalMeta(key, value);
          }
        }
      }

      addGlobalMeta("Description", comment);
      addGlobalMeta("Acquisition date", dateTime);
      addGlobalMeta("X size (um)", xSize);
      addGlobalMeta("Y size (um)", ySize);
      addGlobalMeta("Z minimum (um)", zMin);
      addGlobalMeta("Z maximum (um)", zMax);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageDescription(comment, 0);
    if (dateTime != null) {
      store.setImageAcquisitionDate(new Timestamp(dateTime), 0);
    }

    double physicalSizeX = xSize / getSizeX();
    double physicalSizeY = ySize / getSizeY();

    Length sizeX = FormatTools.getPhysicalSizeX(physicalSizeX);
    Length sizeY = FormatTools.getPhysicalSizeY(physicalSizeY);

    if (sizeX != null) {
      store.setPixelsPhysicalSizeX(sizeX, 0);
    }
    if (sizeY != null) {
      store.setPixelsPhysicalSizeY(sizeY, 0);
    }
  }

  // -- Helper methods --

  private String readDate() throws IOException {
    final StringBuilder dateTime = new StringBuilder();
    dateTime.append(String.valueOf(in.readInt())); // year
    dateTime.append("-");
    int month = in.readInt();
    dateTime.append(String.format("%02d", month));
    dateTime.append("-");
    int day = in.readInt();
    dateTime.append(String.format("%02d", day));
    dateTime.append("T");
    int hour = in.readInt();
    dateTime.append(String.format("%02d", hour));
    dateTime.append(":");
    int minute = in.readInt();
    dateTime.append(String.format("%02d", minute));
    dateTime.append(":");
    in.skipBytes(4);

    float scanTime = in.readInt() / 100f;
    dateTime.append(String.format("%02d", (int) scanTime));
    addGlobalMeta("Scan time (s)", scanTime);
    return dateTime.toString();
  }

}
