/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * ImspectorReader is the file format reader for Imspector .msr files.
 */
public class ImspectorReader extends FormatReader {

  // -- Constants --

  /** The header for each file contains this string. */
  private static final String MAGIC_STRING = "CDataStack";

  // -- Fields --

  /** List of offsets to each block of pixel data. */
  private ArrayList<Long> pixelsOffsets = new ArrayList<Long>();

  /** List of PMTs active during acquisition. */
  private ArrayList<String> uniquePMTs = new ArrayList<String>();

  /**
   * Number of planes in each block of pixel data.  The blocks will
   * often (though not always) be of equal size.
   */
  private ArrayList<Integer> planesPerBlock = new ArrayList<Integer>();

  /** Whether or not this file contains FLIM data. */
  private boolean isFLIM = false;

  /** The number of mosaic tiles stored in the file. */
  private int tileCount = 1;

  // -- Constructor --

  /** Constructs a new Imspector reader. */
  public ImspectorReader() {
    super("Lavision Imspector", "msr");
    domains = new String[] {FormatTools.FLIM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 32;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    // calculate the block index from the series and image numbers
    int index = 0;
    for (int i=0; i<getSeries(); i++) {
      index += core.get(i).imageCount;
    }
    index += no;

    int[] zct = getZCTCoords(no);
    int block = getSeries() * getSizeC() + zct[1];
    int plane = getIndex(zct[0], 0, zct[2]);

    if (plane < planesPerBlock.get(block)) {
      in.seek(pixelsOffsets.get(block) + FormatTools.getPlaneSize(this) * plane);
      readPlane(in, x, y, w, h, buf);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsOffsets.clear();
      uniquePMTs.clear();
      planesPerBlock.clear();
      isFLIM = false;
      tileCount = 1;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
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

    skipTags(count);

    if (in.getFilePointer() % 2 == 1) {
      in.skipBytes(1);
    }
    else {
      int check = in.read() & 0xff;
      if (check != 0xff) {
        in.seek(in.getFilePointer() - 1);
      }
    }

    length = in.readShort();

    String metadata = in.readString(length);
    String[] values = metadata.split("::");
    // Correct alignment when length is an even number of bytes
    if (length % 2 == 0)  {
      if (in.read() != 13) {
        in.seek(in.getFilePointer() - 1);
      }
    }

    int check = in.readShort();
    while (check != 3 && check != 2) {
      in.seek(in.getFilePointer() - 1);
      check = in.readShort();
    }

    in.skipBytes(26);
    int pmtMarkerLength = in.read();
    String pmtMarker = in.readString(pmtMarkerLength);
    uniquePMTs.add(pmtMarker);

    in.skipBytes(6);
    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    int originalZ = in.readInt();
    int originalT = in.readInt();
    m.sizeZ = originalZ;
    m.sizeT = originalT;
    planesPerBlock.add(m.sizeT * m.sizeZ);

    in.skipBytes(16);
    for (int i=0; i<4; i++) {
      int len = in.read();
      String pmtSetting = in.readString(len);
    }

    pixelsOffsets.add(in.getFilePointer());

    for (int i=0; i<values.length-1; i+=2) {
      addGlobalMeta(values[i], values[i + 1]);

      if (values[i].equals("Instrument Mode")) {
        isFLIM =
          values[i + 1].startsWith("TCSPC") || values[i + 1].startsWith("FLIM");
      }
    }
    m.pixelType = FormatTools.UINT16;
    int bpp = FormatTools.getBytesPerPixel(m.pixelType);
    in.skipBytes(m.sizeX * m.sizeY * planesPerBlock.get(0) * bpp + 2);

    int tileX = 1;
    int tileY = 1;
    Double timeBase = null;
    int realTimepoints = 1;

    // read through the file looking for metadata and pixels offsets
    // 0x8003 and 0xffff appear to be special markers for metadata sections
    while (in.getFilePointer() < in.length()) {
      check = in.readShort() & 0xffff;
      if (check != 0x8003) {
        in.skipBytes(2);
        length = in.readShort();
      }
      else {
        length = in.read() & 0xff;
      }
      if (length == 0 && check == 65535) {
        in.skipBytes(46);
        int tagBlockSize = skipTagBlock();
        if (tagBlockSize == 0) {
          in.skipBytes(26);
        }
        else {
          in.skipBytes(50);
        }
        findOffset();
        continue;
      }
      else if (length == 3 && check == 65535) {
        in.seek(in.getFilePointer() - 2);
        findOffset(false);
        continue;
      }
      if (length < 0) {
        continue;
      }
      tag = in.readString(length);

      while (in.getFilePointer() < in.length()) {
        if (tag.equals("CImageStack")) {
          readStackHeader();

          in.skipBytes(60);
          while ((in.read() & 0xff) != 0xff);
          length = in.readShort();
          if (length == 0) {
            in.skipBytes(1);
            if ((in.read() & 0xff) == 0xff) {
              length = in.readShort();
            }
            else {
              in.seek(in.getFilePointer() - 1);
              length = in.readShort();
              in.skipBytes(length - 2);
              break;
            }
          }
          in.skipBytes(length + 50);

          findOffset();
          break;
        }
        else if (tag.equals("Label")) {
          in.skipBytes(4);
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
        else if (type > 0xffff && type != 0x80000) {
          in.seek(in.getFilePointer() - 2);
          value = String.valueOf(in.readDouble());
        }
        else {
          length = in.read();
          value = in.readString(length);
        }

        if (tag.equals("Label")) {
          in.skipBytes(350);
          findOffset();
          break;
        }

        int valueType = in.readShort();
        int valueLen = in.readShort();
        int check1 = 0, check2 = 0;

        // type codes were determined by trial and error, and may not be correct
        switch (valueType) {
          case 10:
          case 11:
          case 12:
            key = value;
            value = String.valueOf(in.readInt() == 1);
            break;
          case 2:
          case 3:
          case 9:
          case 13:
          case 14:
          case 17:
            key = value;
            value = String.valueOf(in.readFloat());
            break;
          case 0:
          case 1:
          case 4:
          case 5:
          case 6:
          case 8:
            key = value;
            value = String.valueOf(in.readInt());
            break;
          default:
            LOGGER.debug("found type {}", valueType);
            check1 = valueType;
            check2 = valueLen;
        }

        // parse the tile dimensions, if present
        if (key.equals("Stitching X")) {
          try {
            tileX = Integer.parseInt(value);
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("Stitching Y")) {
          try {
            tileY = Integer.parseInt(value);
          }
          catch (NumberFormatException e) { }
        }

        check1 = in.readShort();
        check2 = in.readShort();

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

        // find the end of this metadata section
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

        if (key.equals("TCSPC  Z Length") ||
          key.equals("TDC  Z Length") ||
          key.equals("DC-TCSPC T Length"))
        {
          try {
            if (timeBase == null) {
              timeBase = Double.parseDouble(value);
            }
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("xyz-Table Z Resolution")) {
          Double doubleValue = DataTools.parseDouble(value);
          if (doubleValue != null) {
            int z = doubleValue.intValue();
            if (z == 1 && getSizeZ() > 1) {
              originalT = getSizeT();
              originalZ = getSizeZ();
              m.sizeT *= getSizeZ();
              m.sizeZ = 1;
            }
          }
        }
        else if (key.equals("Time Time Resolution")) {
          realTimepoints = Integer.parseInt(value);
        }

        if (check1 == 255 && check2 == 255) {
          in.seek(in.getFilePointer() - 2);
          break;
        }
      }
    }

    for (Integer blockSize : planesPerBlock) {
      m.imageCount += blockSize;
    }

    if (isFLIM) {
      if (getSizeZ() == 1 || getSizeT() == 1) {
        m.sizeZ = 1;
        m.sizeT = m.imageCount;
        if (m.imageCount % uniquePMTs.size() == 0) {
          m.sizeT /= uniquePMTs.size();
        }
      }
      LOGGER.debug("m.imageCount = {}", m.imageCount);
      m.moduloT.parentType = FormatTools.SPECTRA;
      m.moduloT.type = FormatTools.LIFETIME;

      m.sizeC = m.imageCount / (m.sizeZ * m.sizeT);
      if (getSizeZ() > 1 && getSizeT() > 1) {
        m.dimensionOrder = "XYTZC";
      }
      else {
        m.dimensionOrder = "XYZTC";
      }

      if (timeBase == null) {
        timeBase = 1.0;
      }

      // convert time base to picoseconds
      if (timeBase > 1) {
        timeBase *= 1000;
      }

      if (realTimepoints > 1) {
        originalT = getSizeT() / realTimepoints;
      }
      else if (realTimepoints == 1 && originalT == 1) {
        originalT = getSizeT();
      }

      m.moduloT.step = timeBase / originalT;
      m.moduloT.end = m.moduloT.step * (originalT - 1);
      m.moduloT.unit = "ps";
      m.moduloT.typeDescription = "TCSPC";
    }
    else {
      if (uniquePMTs.size() <= pixelsOffsets.size()) {
        m.sizeC = uniquePMTs.size();
      }
      else {
        m.sizeC = 1;
      }
      if (m.imageCount != m.sizeZ * m.sizeC * m.sizeT) {
        if (planesPerBlock.size() > 1) {
          m.sizeZ = 1;
          for (Integer block : planesPerBlock) {
            if (block > m.sizeZ) {
              m.sizeZ = block;
            }
          }
          m.imageCount = m.sizeZ * m.sizeC * m.sizeT;
        }
        else {
          m.sizeZ = m.imageCount / m.sizeC;
          m.sizeT = m.imageCount / (m.sizeZ * m.sizeC);
        }
      }
      if (m.sizeC > 1 && m.sizeT != 1) {
        m.dimensionOrder = "XYZTC";
      }
      else {
        m.dimensionOrder = "XYZCT";
      }
    }

    tileCount = tileX * tileY;

    // correct the tile count if it doesn't make sense
    // the count may have been incorrectly recorded or parsed
    if (tileCount == 0 || (m.imageCount % tileCount) != 0) {
      tileCount = 1;
    }

    // recalculate image counts based upon the tile count
    // tiles are assumed to have the same dimensions
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
  }

  /** Read the header for an image stack. */
  private void readStackHeader() throws IOException {
    int count = in.readInt();
    if (count > 0xffff) {
      in.seek(in.getFilePointer() - 2);
      int len = in.readShort();
      in.skipBytes(len);
      count = in.readInt() + 1;
    }
    skipTags(count);
    skipTagBlock();
  }

  /** Skip a block of unknown string values. */
  private void skipTags(int count) throws IOException {
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
  }

  /** Skip an unknown tag. */
  private int skipTagBlock() throws IOException {
    in.skipBytes(1);
    int length = in.readShort();
    in.skipBytes(length);
    return length;
  }

  /** Find the offset to the next block of pixel data. */
  private void findOffset() throws IOException {
    findOffset(true);
  }

  private void findOffset(boolean readStackHeader) throws IOException {
    if (readStackHeader) {
      readStackHeader();
    }

    int pmtCheck = in.readShort();
    while (pmtCheck != 3 && pmtCheck != 2) {
      in.seek(in.getFilePointer() - 1);
      pmtCheck = in.readShort();
      if (pmtCheck == 2) {
        if (in.readShort() == 0) {
          in.seek(in.getFilePointer() - 2);
        }
        else {
          pmtCheck = 0;
        }
      }
    }

    in.skipBytes(26);
    int len = in.read();
    if (len < 0) {
      return;
    }
    String pmt = in.readString(len);
    // as far as we know, valid PMT names only start with "PMT" or "TCSPC",
    // depending upon the acquisition mode
    if (!uniquePMTs.contains(pmt) && (pmt.startsWith("PMT") ||
      pmt.indexOf("TCSPC") >= 0))
    {
      uniquePMTs.add(pmt);
    }

    boolean noPMT = len == 0;

    CoreMetadata m = core.get(0);
    in.skipBytes(14);
    int newT = in.readInt();
    int newZ = in.readInt();
    long planeSize = (long) getSizeX() * getSizeY() * FormatTools.getBytesPerPixel(getPixelType());
    long newCount = (long) newZ * newT;
    if (newZ > getSizeZ() && newT > getSizeT() &&
      newCount * planeSize < in.length() - in.getFilePointer() &&
      newCount > 0 && newCount * planeSize > 0)
    {
      uniquePMTs.remove(0);
      planesPerBlock.remove(planesPerBlock.size() - 1);
      pixelsOffsets.remove(pixelsOffsets.size() - 1);
      m.sizeZ = newZ;
      m.sizeT = newT;
    }
    planesPerBlock.add(newZ * newT);
    in.skipBytes(noPMT ? 12 : 16);
    for (int i=0; i<4; i++) {
      len = in.read();
      if (len < 0) {
        noPMT = true;
        break;
      }
      String pmtSetting = in.readString(len);
    }

    if (noPMT) {
      while ((in.readShort() & 0xffff) != 0x8003);
      in.seek(in.getFilePointer() - 2);
      planesPerBlock.remove(planesPerBlock.size() - 1);
      return;
    }

    planeSize = (long) m.sizeX * m.sizeY *
      planesPerBlock.get(planesPerBlock.size() - 1) * 2;
    if (planeSize > 0 && in.getFilePointer() + planeSize < in.length()) {
      pixelsOffsets.add(in.getFilePointer());
      in.skipBytes((int) planeSize + 2);
    }
    else {
      planesPerBlock.remove(planesPerBlock.size() - 1);
    }
  }

}
