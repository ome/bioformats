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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import loci.common.Constants;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * SISReader is the file format reader for Olympus Soft Imaging Solutions
 * TIFF files.
 */
public class SISReader extends BaseTiffReader {

  // -- Constants --

  private static final int SIS_TAG = 33560;
  private static final int SIS_INI_TAG = 33471;
  private static final int SIS_TAG_2 = 34853;

  // -- Fields --

  private String imageName;
  private double magnification;
  private String channelName;
  private String cameraName;
  private double physicalSizeX, physicalSizeY;
  private String acquisitionDate;

  // -- Constructor --

  public SISReader() {
    super("Olympus SIS TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    suffixNecessary = true;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    String make = ifd.getIFDTextValue(IFD.MAKE);
    return (ifd.get(SIS_TAG) != null &&
      (software == null || software.startsWith("analySIS"))) ||
      (ifd.get(SIS_TAG_2) != null &&
      (make != null && make.startsWith("Olympus")));
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      imageName = null;
      channelName = null;
      cameraName = null;
      magnification = 0d;
      physicalSizeX = physicalSizeY = 0d;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    IFD ifd = ifds.get(0);
    CoreMetadata m = core.get(0);

    String iniMetadata = ifd.getIFDTextValue(SIS_INI_TAG);
    if (iniMetadata != null) {
      IniParser parser = new IniParser();
      IniList ini =
        parser.parseINI(new BufferedReader(new StringReader(iniMetadata)));

      IniTable dimensions = ini.getTable("Dimension");
      int z = Integer.parseInt(dimensions.get("Z"));
      int c = Integer.parseInt(dimensions.get("Band"));
      int t = Integer.parseInt(dimensions.get("Time"));

      if (z * c * t == ifds.size()) {
        m.sizeZ = z;
        m.sizeT = t;
        m.sizeC *= c;
      }

      // TODO : parse more metadata from the INI tables
    }

    if (!ifd.containsKey(SIS_TAG)) {
      // TODO: parse this metadata more thoroughly
      in.seek(ifd.getIFDLongValue(SIS_TAG_2, 0));

      while (!in.readString(2).equals("IS")) {
        in.seek(in.getFilePointer() - 1);
      }

      in.skipBytes(28);
      in.seek(in.readLong() - 84);
      physicalSizeX = in.readDouble() * 1000;
      physicalSizeY = in.readDouble() * 1000;

      return;
    }

    long metadataPointer = ifd.getIFDLongValue(SIS_TAG, 0);

    in.seek(metadataPointer);

    in.skipBytes(4);

    in.skipBytes(6);
    int minute = in.readShort();
    int hour = in.readShort();
    int day = in.readShort();
    int month = in.readShort() + 1;
    int year = 1900 + in.readShort();

    acquisitionDate =
      year + "-" + month + "-" + day + " " + hour + ":" + minute;
    acquisitionDate = DateTools.formatDate(acquisitionDate, "yyyy-M-d H:m");

    in.skipBytes(6);

    imageName = in.readCString();
    if ((in.getFilePointer() % 2) == 1) {
      in.skipBytes(1);
    }

    short check = in.readShort();
    while (check != 7 && check != 8) {
      check = in.readShort();

      if (check == 0x700 || check == 0x800 || check == 0xa00) {
        in.skipBytes(1);
        break;
      }
    }
    in.skipBytes(4);

    long pos = in.readInt() & 0xffffffffL;
    if (pos >= in.length()) {
      return;
    }
    in.seek(pos);

    in.skipBytes(12);

    physicalSizeX = in.readDouble();
    physicalSizeY = in.readDouble();

    if (Math.abs(physicalSizeX - physicalSizeY) > Constants.EPSILON) { // ??
      physicalSizeX = physicalSizeY;
      physicalSizeY = in.readDouble();
    }

    in.skipBytes(8);

    magnification = in.readDouble();
    int cameraNameLength = in.readShort();
    channelName = in.readCString();

    if (channelName.length() > 128) {
      channelName = "";
    }

    int length = (int) Math.min(cameraNameLength, channelName.length());
    if (length > 0) {
      cameraName = channelName.substring(0, length);
    }

    // these are no longer valid
    getGlobalMetadata().remove("XResolution");
    getGlobalMetadata().remove("YResolution");

    addGlobalMeta("Nanometers per pixel (X)", physicalSizeX);
    addGlobalMeta("Nanometers per pixel (Y)", physicalSizeY);
    addGlobalMeta("Magnification", magnification);
    addGlobalMeta("Channel name", channelName);
    addGlobalMeta("Camera name", cameraName);
    addGlobalMeta("Image name", imageName);
    addGlobalMeta("Acquisition date", acquisitionDate);
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName, 0);
    if (acquisitionDate != null) {
      store.setImageAcquisitionDate(new Timestamp(acquisitionDate), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);
      store.setImageInstrumentRef(instrument, 0);

      String objective = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objective, 0, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
      store.setObjectiveSettingsID(objective, 0);

      String detector = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detector, 0, 0);
      store.setDetectorModel(cameraName, 0, 0);
      store.setDetectorType(getDetectorType("Other"), 0, 0);
      store.setDetectorSettingsID(detector, 0, 0);

      physicalSizeX /= 1000;
      physicalSizeY /= 1000;

      Length sizeX = FormatTools.getPhysicalSizeX(physicalSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(physicalSizeY);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      store.setChannelName(channelName, 0, 0);
    }
  }

}
