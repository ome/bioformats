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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.in.LeicaMicrosystemsMetadata.*;

/**
 * LOFReader is the file format reader for Leica Microsystems' LOF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LOFReader extends LMSFileReader {

  // -- Constants --
  public static final byte LOF_MAGIC_BYTE = 0x70;
  public static final byte LOF_MEMORY_BYTE = 0x2a;

  /** The encoding used in this file. */
  private static final String ENCODING = "ISO-8859-1";

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private List<Long> offsets;
  private int lastChannel = 0;
  private long endPointer;
  
  public enum MetadataSource {
    LOF,
    XLIF
  }
  private MetadataSource metadataSource = MetadataSource.LOF;

  // -- Constructor --

  /** Constructs a new Leica LOF reader. */
  public LOFReader() {
    super("Leica Object Format", "lof");
    suffixNecessary = false;
    suffixSufficient = false;
    domains = new String[] { FormatTools.LM_DOMAIN };
  }
  public LOFReader(XlifDocument xlif){
    super("Leica Object Format", "lof");
    suffixNecessary = false;
    suffixSufficient = false;
    domains = new String[] { FormatTools.LM_DOMAIN };
    associatedXmlDoc = xlif;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    
    // ------------------ 1. Part: Header ------------------
    // test value: 0x70
    if (stream.readInt() != LOF_MAGIC_BYTE) return false;

    // length of the following binary chunk to read (1.1)
    stream.readInt();

    // --------- 1.1 Type Content ---------
    // test value: 0x2A
    if (stream.readByte() != LOF_MEMORY_BYTE) return false;

    // number of unicode characters (NC)
    int nc = stream.readInt();
    String typeName = "";
    for (int i = 0; i < nc; i++) {
      typeName += stream.readChar();
    }
    if (!typeName.equals("LMS_Object_File")) return false;

    // --------- 1.2 Major version ---------
    // test value: 0x2A
    if (stream.readByte() != LOF_MEMORY_BYTE) return false;
    stream.readInt();

    // --------- 1.3 Minor version ---------
    // test value: 0x2A
    if (stream.readByte() != LOF_MEMORY_BYTE) return false;
    stream.readInt();

    // --------- 1.4 Memory size ---------
    // test value: 0x2A
    if (stream.readByte() != LOF_MEMORY_BYTE) return false;
    // memory size (MS)
    long memorySize = stream.readLong();

    // ------------------ 2. Part: Memory ------------------
    stream.skipBytes(memorySize);

    if (stream.getFilePointer() >= stream.length()) return false;
    // ------------------ 3. Part: XML ------------------
    // test value: 0x70
    if (stream.readInt() != LOF_MAGIC_BYTE) return false;
    // length of the following binary chunk to read (3.1)
    stream.readInt();

    // --------- 3.1 XML Content ---------
    // test value: 0x2A
    if (stream.readByte() != LOF_MEMORY_BYTE) return false;
    
    int xmlLength = stream.readInt();
    String lofXml = "";
      for (int i = 0; i < xmlLength; i++) {
        lofXml += stream.readChar();
      }
    LofXmlDocument doc = new LofXmlDocument(lofXml, "");
    if (doc.getImageNode() != null) {
      return true;
    } else {
      LOGGER.info("This LOF does not contain image data, it cannot be opened directly.");
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed())
      return null;

    if (lastChannel < 0 || lastChannel >= 9) {
      return null;
    }

    byte[][] lut = new byte[3][256];
    for (int i = 0; i < 256; i++) {
      switch (lastChannel) {
        case 0:
          // red
          lut[0][i] = (byte) (i & 0xff);
          break;
        case 1:
          // green
          lut[1][i] = (byte) (i & 0xff);
          break;
        case 2:
          // blue
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 3:
          // cyan
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 4:
          // magenta
          lut[0][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 5:
          // yellow
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          break;
        default:
          // gray
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
      }
    }
    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || !isIndexed())
      return null;

    if (lastChannel < 0 || lastChannel >= 9) {
      return null;
    }

    short[][] lut = new short[3][65536];
    for (int i = 0; i < 65536; i++) {
      switch (lastChannel) {
        case 0:
          // red
          lut[0][i] = (short) (i & 0xffff);
          break;
        case 1:
          // green
          lut[1][i] = (short) (i & 0xffff);
          break;
        case 2:
          // blue
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 3:
          // cyan
          lut[1][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 4:
          // magenta
          lut[0][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 5:
          // yellow
          lut[0][i] = (short) (i & 0xffff);
          lut[1][i] = (short) (i & 0xffff);
          break;
        default:
          // gray
          lut[0][i] = (short) (i & 0xffff);
          lut[1][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
      }
    }
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (!isRGB()) {
      int[] pos = getZCTCoords(no);
      lastChannel = metaTemp.channelPrios[getTileIndex(series)][pos[1]];
    }

    int tileIndex = getTileIndex(series);
    if (tileIndex >= offsets.size()) {
      // truncated file; imitate LAS AF and return black planes
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    long offset = offsets.get(tileIndex).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();

    long planeSize = (long) getSizeX() * getSizeY() * bpp;
    long nextOffset = tileIndex + 1 < offsets.size() ? offsets.get(tileIndex + 1).longValue() : endPointer;
    long bytesToSkip = nextOffset - offset - planeSize * getImageCount();
    if (bytesToSkip % planeSize != 0) bytesToSkip = 0;
    bytesToSkip /= getSizeY();
    if ((getSizeX() % 4) == 0)
      bytesToSkip = 0;

    if (offset + (planeSize + bytesToSkip * getSizeY()) * no >= in.length()) {
      // truncated file; imitate LAS AF and return black planes
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    seekStartOfPlane(no, offset, planeSize);

    if (bytesToSkip == 0) {
      buf = readPlane(in, x, y, w, h, buf);
    } else {
      in.skipBytes(bytesToSkip * getSizeY() * no);
      in.skipBytes(y * (getSizeX() * bpp + bytesToSkip));
      for (int row = 0; row < h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        long skip = bpp * (getSizeX() - w - x) + bytesToSkip;
        if (in.getFilePointer() + skip < in.length()) {
          in.skipBytes(skip);
        }
      }
    }

    // rearrange if color planes are stored in BGR order
    if (getRGBChannelCount() == 3 && metaTemp.inverseRgb[0]) {
      ImageTools.bgrToRgb(buf, isInterleaved(), bytes, getRGBChannelCount());
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      lastChannel = 0;
      endPointer = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.setEncoding(ENCODING);

    in.order(true);

    checkForLofLayout(in, id);
    
    if (metadataSource == MetadataSource.LOF) {
      // number of unicode characters
      int xmlLength = in.readInt();

      // XML object description
      String lofXml = "";
      for (int i = 0; i < xmlLength; i++) {
        lofXml += in.readChar();
      }

      String name = id.replaceAll("^.*[\\/\\\\]", "").replace(".lof", "");
      LofXmlDocument doc = new LofXmlDocument(lofXml, name);
      translateMetadata(doc);
    } else {
      translateMetadata((XlifDocument)associatedXmlDoc);
    }

    if (endPointer == 0) {
      endPointer = in.length();
    }
  }

  // -- LeicaFileReader methods --
  @Override
  public ImageFormat getImageFormat() {
    return ImageFormat.LOF;
  }

  // -- Helper methods --

  /**
   * Checks if file layout meets lof file specifications
   * @param in lof file stream
   * @param filename name of the lof file
   * @throws FormatException
   * @throws IOException
   */
  private void checkForLofLayout(RandomAccessInputStream in, String filename) throws FormatException, IOException {
    offsets = new ArrayList<Long>();

    // ------------------ 1. Part: Header ------------------
    // test value: 0x70
    if (in.readInt() != LOF_MAGIC_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at header section)");
    }

    // length of the following binary chunk to read (1.1)
    in.readInt();

    // --------- 1.1 Type Content ---------
    // test value: 0x2A
    if (in.readByte() != LOF_MEMORY_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at header section)");
    }

    // number of unicode characters (NC)
    int nc = in.readInt();
    String typeName = "";
    for (int i = 0; i < nc; i++) {
      typeName += in.readChar();
    }
    if (!typeName.equals("LMS_Object_File")) { 
      //if we land here it's probably a LIF file
      throw new FormatException(filename + " is not a valid Leica LOF file (typename=" + typeName + ")");
    }

    // --------- 1.2 Major version ---------
    // test value: 0x2A
    if (in.readByte() != LOF_MEMORY_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at header section)");
    }
    in.readInt();

    // --------- 1.3 Minor version ---------
    // test value: 0x2A
    if (in.readByte() != LOF_MEMORY_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at header section)");
    }
    in.readInt();

    // --------- 1.4 Memory size ---------
    // test value: 0x2A
    if (in.readByte() != LOF_MEMORY_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at header section)");
    }
    // memory size (MS)
    long memorySize = in.readLong();

    if (memorySize > 0) {
      offsets.add(in.getFilePointer());
    }

    // ------------------ 2. Part: Memory ------------------
    in.skipBytes(memorySize);

    if (in.getFilePointer() >= in.length()) {
      throw new FormatException(filename + "is not a valid Leica LOF file (xml section not found)");
    }
    // ------------------ 3. Part: XML ------------------
    // test value: 0x70
    if (in.readInt() != LOF_MAGIC_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at xml section)");
    }
    // length of the following binary chunk to read (3.1)
    in.readInt();

    // --------- 3.1 XML Content ---------
    // test value: 0x2A
    if (in.readByte() != LOF_MEMORY_BYTE) {
      throw new FormatException(filename + " is not a valid Leica LOF file (error at xml section)");
    }
  }

  private void seekStartOfPlane(int no, long dataOffset, long planeSize) throws IOException {
    int index = getTileIndex(series);
    long posInFile;

    int numberOfTiles = metaTemp.tileCount[index];
    if (numberOfTiles > 1) {
      // LAS AF treats tiles just like any other dimension, while we do not.
      // Hence we need to take the tiles into account for a frame's position.
      long bytesIncPerTile = metaTemp.tileBytesInc[index];
      long framesPerTile = bytesIncPerTile / planeSize;

      if (framesPerTile > Integer.MAX_VALUE) {
        throw new IOException("Could not read frame due to int overflow");
      }

      int noOutsideTiles = no / (int) framesPerTile;
      int noInsideTiles = no % (int) framesPerTile;

      int tile = series;
      for (int i = 0; i < index; i++) {
        tile -= metaTemp.tileCount[i];
      }

      posInFile = dataOffset;
      posInFile += noOutsideTiles * bytesIncPerTile * numberOfTiles;
      posInFile += tile * bytesIncPerTile;
      posInFile += noInsideTiles * planeSize;
    } else {
      posInFile = dataOffset + no * planeSize;
    }

    // seek instead of skipBytes to prevent dangerous int cast
    in.seek(posInFile);
  }

  private int getTileIndex(int coreIndex) {
    int count = 0;
    for (int tile = 0; tile < metaTemp.tileCount.length; tile++) {
      if (coreIndex < count + metaTemp.tileCount[tile]) {
        return tile;
      }
      count += metaTemp.tileCount[tile];
    }
    return -1;
  }

  /**
   * Inits the LOFReader using passed xml (from XLIF) for metadata initiation,
   * instead of reading the LOF's included xml
   * 
   * @param id  LOF file id
   * @param xml XML from xlif that points to LOF file
   * @throws FormatException
   * @throws IOException
   */
  public void setIdWithMetadata(String id, XlifDocument xml) throws FormatException, IOException {
    metadataSource = MetadataSource.XLIF;
    associatedXmlDoc = xml;
    super.setId(id);
  }
}