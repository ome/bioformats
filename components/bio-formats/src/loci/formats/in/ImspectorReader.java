/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
import java.util.ArrayList;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * ImspectorReader is the file format reader for Imspector .msr files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ImspectorReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ImspectorReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ImspectorReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "CDataStack";

  // -- Fields --

  private ArrayList<Long> pixelsOffsets = new ArrayList<Long>();
  private ArrayList<String> uniquePMTs = new ArrayList<String>();
  private int planesPerBlock = 1;
  private boolean isFLIM = false;
  private int tileCount = 1;

  // -- Constructor --

  /** Constructs a new Imspector reader. */
  public ImspectorReader() {
    super("Lavision Imspector", "msr");
    domains = new String[] {FormatTools.FLIM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 32;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int offsetsPerSeries = pixelsOffsets.size() / getSeriesCount();
    int block = getSeries() * offsetsPerSeries + no / planesPerBlock;
    int plane = no % planesPerBlock;

    in.seek(pixelsOffsets.get(block) + FormatTools.getPlaneSize(this) * plane);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsOffsets.clear();
      uniquePMTs.clear();
      planesPerBlock = 1;
      isFLIM = false;
      tileCount = 1;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    // pixel data is stored uncompressed in blocks of one or more planes
    // as far as we know, pixels are always little-endian unsigned 16-bit

    m.littleEndian = true;

    in.order(isLittleEndian());
    in.skipBytes(20);

    int length = in.readShort();
    String tag = in.readString(length);
    int count = in.readInt();

    for (int i=0; i<count; i++) {
      int strlen = in.read();
      String value = in.readString(strlen);
      if (strlen == 0) {
        i--;
      }
    }

    if (in.getFilePointer() % 2 == 1) {
      in.skipBytes(1);
    }

    length = in.readShort();

    String metadata = in.readString(length);
    String[] values = metadata.split("::");

    int check = in.readShort();
    while (check != 3 && check != 2) {
      check = in.readShort();
    }

    in.skipBytes(26);
    int pmtMarkerLength = in.read();
    String pmtMarker = in.readString(pmtMarkerLength);
    uniquePMTs.add(pmtMarker);

    in.skipBytes(6);
    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    planesPerBlock = in.readInt();

    in.skipBytes(20);
    for (int i=0; i<4; i++) {
      int len = in.read();
      String pmtSetting = in.readString(len);
    }

    pixelsOffsets.add(in.getFilePointer());

    for (int i=0; i<values.length; i+=2) {
      addGlobalMeta(values[i], values[i + 1]);

      if (values[i].equals("Instrument Mode")) {
        isFLIM = values[i + 1].equals("TCSPC");
      }
    }
    m.pixelType = FormatTools.UINT16;
    in.skipBytes(m.sizeX * m.sizeY * planesPerBlock * 2 + 2);

    int tileX = 1;
    int tileY = 1;

    while (in.getFilePointer() < in.length()) {
      check = in.readShort() & 0xffff;
      if (check != 0x8003) {
        in.skipBytes(2);
        length = in.readShort();
      }
      else {
        length = in.read();
      }
      if (length == 0 && check == 65535) {
        in.skipBytes(46);
        skipTagBlock();
        in.skipBytes(50);
        findOffset();
        continue;
      }
      tag = in.readString(length);

      while (in.getFilePointer() < in.length()) {
        if (tag.equals("CImageStack")) {
          readStackHeader();

          in.skipBytes(65);
          length = in.readShort();
          if (length == 0) {
            in.skipBytes(1);
            length = in.readShort();
            in.skipBytes(length - 2);
            break;
          }
          in.skipBytes(length + 50);

          findOffset();
          break;
        }
        length = in.read();

        if (length < 0) {
          break;
        }

        String key = in.readString(length);
        int type = in.readInt();
        String value = null;
        if (type == 5) {
          length = in.read();
          if (length == 0) {
            in.skipBytes(4);
            length = in.read();
          }
          value = in.readString(length);
        }
        else if (type > 0xffff) {
          in.seek(in.getFilePointer() - 2);
          value = String.valueOf(in.readDouble());
        }
        else {
          length = in.read();
          value = in.readString(length);
        }

        int check1 = in.readShort();
        int check2 = in.readShort();

        // read the real value
        // ends with 0x03, 0x80
        if (check1 == 0 && check2 == 8) {
          if (in.readShort() != 0x8003) {
            in.seek(in.getFilePointer() - 2);
            int v = in.readInt();
            if (v < 0xffff) {
              value = String.valueOf(v);

              if (key.equals("StitchingX")) {
                tileX = v;
              }
              else if (key.equals("StitchingY")) {
                tileY = v;
              }
            }
            else {
              in.seek(in.getFilePointer() - 4);
            }
          }
          else {
            in.seek(in.getFilePointer() - 2);
          }
        }
        else {
          in.seek(in.getFilePointer() - 4);
        }

        check1 = in.read() & 0xff;
        check2 = in.read() & 0xff;
        while (!(check1 == 3 && check2 == 128) &&
          !(type == 5 && check1 == 255 && check2 == 255) &&
          in.getFilePointer() < in.length())
        {
          check1 = check2;
          check2 = in.read() & 0xff;
        }

        addGlobalMeta(key, value);

        if (check1 == 255 && check2 == 255) {
          in.seek(in.getFilePointer() - 2);
          break;
        }
      }
    }

    if (isFLIM) {
      m.sizeZ = 1;
      m.sizeC = planesPerBlock;
    }
    else {
      if (uniquePMTs.size() <= pixelsOffsets.size()) {
        m.sizeC = uniquePMTs.size();
      }
      else {
        m.sizeC = 1;
      }
      m.sizeZ = planesPerBlock;
    }
    m.imageCount = pixelsOffsets.size() * planesPerBlock;
    m.sizeT = m.imageCount / (m.sizeZ * m.sizeC);
    m.dimensionOrder = "XYZCT";

    tileCount = tileX * tileY;

    if (tileCount > 1) {
      m.imageCount /= tileCount;
      if (m.sizeT >= tileCount) {
        m.sizeT /= tileCount;
      }
      else if (m.sizeC >= tileCount) {
        m.sizeC /= tileCount;
      }
      else {
        m.sizeZ /= tileCount;
      }

      for (int i=1; i<tileCount; i++) {
        core.add(m);
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
    }
  }

  /** Read the header for an image stack. */
  private void readStackHeader() throws IOException {
    int count = in.readInt();
    for (int i=0; i<count; i++) {
      int length = in.read();
      if (length == 0) {
        i--;
      }
      if (length < 0) {
        return;
      }
      String tag = in.readString(length);
    }
    skipTagBlock();
  }

  /** Skip an unknown tag. */
  private void skipTagBlock() throws IOException {
    in.skipBytes(1);
    int length = in.readShort();
    in.skipBytes(length);
  }

  /** Find the offset to the next block of pixel data. */
  private void findOffset() throws IOException {
    readStackHeader();

    while (in.readShort() != 3);

    in.skipBytes(26);
    int len = in.read();
    String pmt = in.readString(len);
    // as far as we know, valid PMT names only start with "PMT" or "TCSPC",
    // depending upon the acquisition mode
    if (!uniquePMTs.contains(pmt) && (pmt.startsWith("PMT") ||
      pmt.startsWith("TCSPC")))
    {
      uniquePMTs.add(pmt);
    }

    CoreMetadata m = core.get(0);
    in.skipBytes(38);
    for (int i=0; i<4; i++) {
      len = in.read();
      String pmtSetting = in.readString(len);
    }

    int planeSize = m.sizeX * m.sizeY * planesPerBlock * 2;
    if (in.getFilePointer() + planeSize < in.length()) {
      pixelsOffsets.add(in.getFilePointer());
      in.skipBytes(planeSize + 2);
    }
  }

}
