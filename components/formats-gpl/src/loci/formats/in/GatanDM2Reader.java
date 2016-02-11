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

import ome.xml.model.primitives.Timestamp;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * GatanDM2Reader is the file format reader for Gatan .dm2 files.
 */
public class GatanDM2Reader extends FormatReader {

  // -- Constants --

  private static final int HEADER_SIZE = 24;
  private static final int DM2_MAGIC_BYTES = 0x3d0000;

  // -- Fields --

  private Double pixelSizeX;
  private Double pixelSizeY;
  private String pixelSizeUnits;

  // -- Constructor --

  /** Constructs a new Gatan .dm2 reader. */
  public GatanDM2Reader() {
    super("Gatan DM2", "dm2");
    domains = new String[] {FormatTools.SPM_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == DM2_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelSizeX = null;
      pixelSizeY = null;
      pixelSizeUnits = null;
    }
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
    in.setEncoding("ISO-8859-1");

    int magicBytes = in.readInt();
    if (magicBytes != DM2_MAGIC_BYTES) {
      throw new FormatException("Invalid DM2 file");
    }

    in.skipBytes(8);

    long footerOffset = in.readInt() + 16;

    CoreMetadata m = core.get(0);

    m.sizeX = in.readShort();
    m.sizeY = in.readShort();
    int bpp = in.readShort();
    boolean signed = in.readShort() == 1;

    m.pixelType = FormatTools.pixelTypeFromBytes(bpp, signed, true);
    m.sizeC = 1;
    m.sizeT = 1;
    m.sizeZ = 1;
    m.imageCount = 1;
    m.dimensionOrder = "XYZCT";
    m.littleEndian = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    String date = null, time = null, name = null;
    in.skipBytes(FormatTools.getPlaneSize(this) + 35);

    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    while (in.getFilePointer() < in.length()) {
      if (level == MetadataLevel.MINIMUM && date != null &&
        time != null && name != null)
      {
        break;
      }

      int strlen = in.readShort();
      if (strlen == 0 || strlen > 255) {
        in.skipBytes(35);
        strlen = in.readShort();

        if (strlen < 0 || strlen + in.getFilePointer() >= in.length()) {
          in.seek(in.getFilePointer() - 10);
          strlen = in.readShort();
        }
      }
      if (strlen < 0 || strlen + in.getFilePointer() >= in.length()) break;
      String label = in.readString(strlen);
      StringBuffer value = new StringBuffer();

      int block = in.readInt();
      if (block == 5) {
        in.skipBytes(33);
        if (in.readShort() == 0) {
          if (in.readShort() == 39) {
            in.skipBytes(1);
          }
          else {
            in.skipBytes(2);
          }
        }
        else {
          in.seek(in.getFilePointer() - 2);
          continue;
        }
      }
      else if (block == 0 || (block > 0xffff && block < 0x01000000)) {
        if (block != 0 && strlen > 0) {
          in.seek(in.getFilePointer() - 4);
          value.append(label);
          label = "Description";
          addGlobalMeta(label, value.toString());
        }
        else if (block != 0) {
          in.skipBytes(15);
        }
        parseExtraTags();
        continue;
      }
      else if (block >= 0x01000000) {
        in.skipBytes(34);
        strlen = in.readShort();

        if (strlen + in.getFilePointer() >= in.length()) {
          break;
        }

        label = in.readString(strlen);
        block = in.readInt();

        if (block == 5) {
          in.skipBytes(33);
          continue;
        }
      }
      int len = in.readInt();
      if (len + in.getFilePointer() >= in.length() || len < 0) break;
      String type = in.readString(len);
      int extra = in.readInt() - 2;
      int count = in.readInt();

      if (type.equals("TEXT")) {
        value.append(in.readString(count));
        if (block == 5) {
          in.skipBytes(22);

          if (in.readInt() == 4) {
            if (in.readString(4).equals("TEXT")) {
              in.skipBytes(4);
              count = in.readInt();
              value.append(", " + in.readString(count));

              in.skipBytes(37);
            }
            else {
              in.skipBytes(7);
            }
          }
          else {
            in.skipBytes(11);
          }
        }
      }
      else if (type.equals("long")) {
        count /= 8;
        for (int i=0; i<count; i++) {
          value.append(in.readLong());
          if (i < count - 1) value.append(", ");
        }
        in.skipBytes(4);
      }
      else if (type.equals("bool")) {
        for (int i=0; i<count; i++) {
          value.append(in.read() == 1);
          if (i < count - 1) value.append(", ");
        }
      }
      else if (type.equals("shor")) {
        count /= 2;
        for (int i=0; i<count; i++) {
          value.append(in.readShort());
          if (i < count - 1) value.append(", ");
        }
      }
      else if (type.equals("sing")) {
        count /= 4;
        for (int i=0; i<count; i++) {
          value.append(in.readFloat());
          if (i < count - 1) value.append(", ");
        }
      }
      else {
        if (count < 0 || count + in.getFilePointer() > in.length()) {
          break;
        }
        in.skipBytes(count);
      }

      in.skipBytes(16);

      addGlobalMeta(label, value.toString());

      if (label.equals("Acquisition Date")) {
        date = value.toString();
        if (date != null && date.indexOf("/") != -1) {
          // if the year is stored as a single digit, then it will be parsed
          // literally, e.g. '7' -> '0007', when we want '7' -> '2007'
          String year = date.substring(date.lastIndexOf("/") + 1);
          if (year.length() < 2) {
            year = "0" + year;
          }
          date = date.substring(0, date.lastIndexOf("/") + 1) + year;
        }
      }
      else if (label.equals("Acquisition Time")) {
        time = value.toString();
      }
      else if (label.equals("Binning")) {
        int bin = (int) Double.parseDouble(value.toString());
        store.setDetectorSettingsBinning(getBinning(bin + "x" + bin), 0, 0);
        String detectorID = MetadataTools.createLSID("Detector", 0, 0);
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorSettingsID(detectorID, 0, 0);
      }
      else if (label.equals("Name")) {
        name = value.toString();
      }
      else if (label.equals("Operator")) {
        String[] experimenterName = value.toString().split(" ");
        store.setExperimenterFirstName(experimenterName[0], 0);
        if (experimenterName.length > 1) {
          store.setExperimenterLastName(experimenterName[1], 0);
        }
        String expID = MetadataTools.createLSID("Experimenter", 0);
        store.setExperimenterID(expID, 0);
        store.setImageExperimenterRef(expID, 0);
      }
    }

    if (date != null && time != null) {
      String[] format = new String[] {
        "M/d/yy h:mm:ss a", "d/M/yy h:mm:ss a",
        "M/d/yy H:mm:ss", "d/M/yy H:mm:ss"};
      date += " " + time;
      date = DateTools.formatDate(date, format);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }
    if (name != null) {
      store.setImageName(name, 0);
    }
    if (pixelSizeX != null) {
      store.setPixelsPhysicalSizeX(FormatTools.createLength(pixelSizeX, UNITS.MICROMETER), 0);
    }
    if (pixelSizeY != null) {
      store.setPixelsPhysicalSizeY(FormatTools.createLength(pixelSizeY, UNITS.MICROMETER), 0);
    }
  }

  // -- Helper methods --

  private void parseExtraTags() throws IOException {
    while (in.getFilePointer() < in.length()) {
      int tag = in.readShort();
      int length = in.readInt();
      String value = "foo";

      if (length == 4) {
        value = String.valueOf(in.readFloat());
      }
      else if (length == 2) {
        value = String.valueOf(in.readShort());
      }
      else if (length == 1) {
        value = String.valueOf(in.read());
      }
      else {
        value = in.readString(length);
        int nullIndex = value.indexOf("\0");
        if (nullIndex >= 0) {
          value = value.substring(0, nullIndex);
        }
      }
      value = value.trim();

      String label = "Tag " + Integer.toHexString(tag);

      switch (tag) {
        case 17:
          label = "BlackContrastLimit";
          break;
        case 18:
          label = "WhiteContrastLimit";
          break;
        case 22:
          label = "Scale";
          break;
        case 27:
          label = "MaxPixelValue";
          break;
        case 28:
          label = "MinPixelValue";
          break;
        case 31:
          label = "Physical width";
          pixelSizeX = new Double(value);
          break;
        case 32:
          label = "Physical height";
          pixelSizeY = new Double(value);
          break;
        case 37:
          label = "Image label";
          break;
        case 38:
          label = "MinimumContrast";
          break;
        case 53:
          label = "Physical size units";
          pixelSizeUnits = value;
          break;
        case 62:
          label = "Origin";
          break;
      }

      addGlobalMeta(label, value);
    }

    if (pixelSizeUnits != null && !pixelSizeUnits.equals("µm")) {
      LOGGER.warn("Unsupported units: " + pixelSizeUnits);
    }

  }

}
