//
// SISReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * SISReader is the file format reader for Olympus Soft Imaging Solutions
 * TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SISReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SISReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SISReader extends BaseTiffReader {

  // -- Constants --

  private static final int SIS_TAG = 33560;
  private static final int SIS_INI_TAG = 33471;

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
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    return ifd.get(SIS_TAG) != null;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
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
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    IFD ifd = ifds.get(0);

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
        core[0].sizeZ = z;
        core[0].sizeT = t;
        core[0].sizeC *= c;
      }

      // TODO : parse more metadata from the INI tables
    }

    long metadataPointer = ifd.getIFDLongValue(SIS_TAG, false, 0);

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
    while (check != 7 && check != 8 && check != 10) {
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

    in.skipBytes(10);

    int units = in.readShort() & 0xf;

    physicalSizeX = in.readDouble();
    physicalSizeY = in.readDouble();

    if (physicalSizeX != physicalSizeY) {
      physicalSizeX = physicalSizeY;
      physicalSizeY = in.readDouble();
    }

    physicalSizeX *= Math.pow(10, units - 7);
    physicalSizeY *= Math.pow(10, units - 7);

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
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName, 0);
    store.setImageCreationDate(acquisitionDate, 0);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);
    store.setImageInstrumentRef(instrument, 0);

    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveNominalMagnification((int) magnification, 0, 0);
    store.setObjectiveCorrection("Other", 0, 0);
    store.setObjectiveImmersion("Other", 0, 0);
    store.setObjectiveSettingsObjective(objective, 0);

    String detector = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detector, 0, 0);
    store.setDetectorModel(cameraName, 0, 0);
    store.setDetectorType("Other", 0, 0);
    store.setDetectorSettingsDetector(detector, 0, 0);

    store.setDimensionsPhysicalSizeX((float) physicalSizeX / 1000, 0, 0);
    store.setDimensionsPhysicalSizeY((float) physicalSizeY / 1000, 0, 0);
    store.setLogicalChannelName(channelName, 0, 0);
  }

}
