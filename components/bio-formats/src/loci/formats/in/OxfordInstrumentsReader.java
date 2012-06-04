//
// OxfordInstrumentsReader.java
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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * OxfordInstrumentsReader is the file format reader for
 * Oxford Instruments .top files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/OxfordInstrumentsReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/OxfordInstrumentsReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = OXFORD_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(OXFORD_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
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
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    core[0].littleEndian = true;
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

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    in.skipBytes(28);
    if (getSizeX() == 0 && getSizeY() == 0) {
      core[0].sizeX = in.readInt();
      core[0].sizeY = in.readInt();
      in.skipBytes(196);
    }
    else in.skipBytes(204);
    core[0].pixelType = FormatTools.UINT16;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].rgb = false;
    core[0].indexed = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].interleaved = false;

    if (FormatTools.getPlaneSize(this) + in.getFilePointer() > in.length()) {
      core[0].sizeY = 1;
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
        if (s.indexOf(":") != -1) {
          String key = s.substring(0, s.indexOf(":")).trim();
          String value = s.substring(s.indexOf(":") + 1).trim();
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
    store.setImageAcquisitionDate(new Timestamp(dateTime), 0);

    double physicalSizeX = xSize / getSizeX();
    double physicalSizeY = ySize / getSizeY();

    if (physicalSizeX > 0) {
      store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX), 0);
    }
    else {
      LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
        physicalSizeX);
    }
    if (physicalSizeY > 0) {
      store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY), 0);
    }
    else {
      LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
        physicalSizeY);
    }
  }

  // -- Helper methods --

  private String readDate() throws IOException {
    StringBuffer dateTime = new StringBuffer();
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
