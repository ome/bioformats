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

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LifImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LifXmlDocument;
import loci.formats.services.OMEXMLService;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSLIFReader extends LMSFileReader {

  // -- Constants --

  public static final String OLD_PHYSICAL_SIZE_KEY = "leicalif.old_physical_size";
  public static final boolean OLD_PHYSICAL_SIZE_DEFAULT = false;

  public static final byte LIF_MAGIC_BYTE = 0x70;
  public static final byte LIF_MEMORY_BYTE = 0x2a;

  /** The encoding used in this file. */
  private static final String ENCODING = "ISO-8859-1";

  private static final String LOGO_FILE = "LeicaLogo.jpg";
  private static final String STYLESHEET_FILE = "LASAF_CIP.xsl";

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private List<Long> offsets;

  private int lastChannel = 0;
  private long endPointer;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LMSLIFReader() {
    super("Leica Image File Format", "lif");
    suffixNecessary = false;
    hasCompanionFiles = true;
    domains = new String[] { FormatTools.LM_DOMAIN };
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
    if (!FormatTools.validStream(stream, blockLen, true))
      return false;
    if (stream.read() != LIF_MAGIC_BYTE)
      return false;
    stream.skipBytes(7);
    if (stream.readByte() != LIF_MEMORY_BYTE)
      return false;
    int nc = stream.readInt();
    String desc = DataTools.stripString(stream.readString(nc * 2));
    if (desc.equals("LMS_Object_File"))
      return false; // LOF file
    return true;
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
      throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (!isRGB()) {
      int[] pos = getZCTCoords(no);
      lastChannel = metaTemp.channelPrios[getTileIndex(series)][pos[1]];
    }

    int index = getTileIndex(series);
    if (index >= offsets.size()) {
      // truncated file; imitate LAS AF and return black planes
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    long offset = offsets.get(index).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();

    long planeSize = (long) getSizeX() * getSizeY() * bpp;
    long nextOffset = index + 1 < offsets.size() ? offsets.get(index + 1).longValue() : endPointer;
    long bytesToSkip = nextOffset - offset - planeSize * getImageCount();
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
      readPlane(in, x, y, w, h, buf);
    } else {
      in.skipBytes(bytesToSkip * getSizeY() * no);
      in.skipBytes(y * (getSizeX() * bpp + bytesToSkip));
      for (int row = 0; row < h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        in.skipBytes(bpp * (getSizeX() - w - x) + bytesToSkip);
      }
    }

    if (getRGBChannelCount() == 3 && metaTemp.inverseRgb[0]) {
      ImageTools.bgrToRgb(buf, isInterleaved(), bytes, getRGBChannelCount());
    }

    return buf;
  }

  private void seekStartOfPlane(int no, long dataOffset, long planeSize)
      throws IOException {
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

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);

    Location currentFile = new Location(currentId).getAbsoluteFile();
    Location parent = currentFile.getParentFile();
    if (parent != null && getSeries() < metaTemp.imageNames.length &&
        metaTemp.imageNames[getSeries()] != null) {
      // look for an XML file with the same name as this series
      Location xmlFile = new Location(parent, metaTemp.imageNames[getSeries()].trim() + ".xml");
      if (xmlFile.exists()) {
        files.add(xmlFile.getAbsolutePath());
      }

      Location logoFile = new Location(parent, LOGO_FILE);
      if (logoFile.exists()) {
        files.add(logoFile.getAbsolutePath());
      }

      Location stylesheetFile = new Location(parent, STYLESHEET_FILE);
      if (stylesheetFile.exists()) {
        files.add(stylesheetFile.getAbsolutePath());
      }
    }

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

  /* @see loci.formats.FormatReader#getAvailableOptions() */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(OLD_PHYSICAL_SIZE_KEY);
    return optionsList;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.setEncoding(ENCODING);
    offsets = new ArrayList<Long>();

    in.order(true);

    // read the header

    LOGGER.info("Reading header");

    byte checkOne = in.readByte();
    in.skipBytes(2);
    byte checkTwo = in.readByte();
    if (checkOne != LIF_MAGIC_BYTE && checkTwo != LIF_MAGIC_BYTE) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != LIF_MEMORY_BYTE) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block
    int nc = in.readInt();
    String xml = DataTools.stripString(in.readString(nc * 2));

    LOGGER.info("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      LOGGER.debug("Looking for a block at {}; {} blocks read",
          in.getFilePointer(), offsets.size());
      int check = in.readInt();
      if (check != LIF_MAGIC_BYTE) {
        if (check == 0 && offsets.size() > 0) {
          // newer .lif file; the remainder of the file is all 0s
          endPointer = in.getFilePointer();
          break;
        }
        throw new FormatException("Invalid Memory Block: found magic bytes " +
            check + ", expected " + LIF_MAGIC_BYTE);
      }

      in.skipBytes(4);
      check = in.read();
      if (check != LIF_MEMORY_BYTE) {
        throw new FormatException("Invalid Memory Description: found magic " +
            "byte " + check + ", expected " + LIF_MEMORY_BYTE);
      }

      long blockLength = in.readInt();
      if (in.read() != LIF_MEMORY_BYTE) {
        in.seek(in.getFilePointer() - 5);
        blockLength = in.readLong();
        check = in.read();
        if (check != LIF_MEMORY_BYTE) {
          throw new FormatException("Invalid Memory Description: found magic " +
              "byte " + check + ", expected " + LIF_MEMORY_BYTE);
        }
      }

      int descrLength = in.readInt() * 2;

      if (blockLength > 0) {
        offsets.add(in.getFilePointer() + descrLength);
      }

      in.seek(in.getFilePointer() + descrLength + blockLength);
    }

    String name = id.replaceAll("^.*[\\/\\\\]", "").replace(".lof", "");
    initMetadata(xml, name);
    xml = null;

    if (endPointer == 0) {
      endPointer = in.length();
    }

    // correct offsets, if necessary
    if (offsets.size() > getSeriesCount()) {
      Long[] storedOffsets = offsets.toArray(new Long[offsets.size()]);
      offsets.clear();
      int index = 0;
      for (int i = 0; i < getSeriesCount(); i++) {
        setSeries(i);
        long nBytes = (long) FormatTools.getPlaneSize(this) * getImageCount();
        long start = storedOffsets[index];
        long end = index == storedOffsets.length - 1 ? in.length() : storedOffsets[index + 1];
        while (end - start < nBytes && ((end - start) / nBytes) != 1) {
          index++;
          start = storedOffsets[index];
          end = index == storedOffsets.length - 1 ? in.length() : storedOffsets[index + 1];
        }
        offsets.add(storedOffsets[index]);
        index++;
      }
      setSeries(0);
    }
  }

  // -- LeicaFileReader methods --
  @Override
  public ImageFormat getImageFormat() {
    return ImageFormat.LIF;
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml, String name) throws FormatException, IOException {
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      service.createOMEXMLMetadata();
    } catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    } catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    xml = XMLTools.sanitizeXML(xml);
    LOGGER.trace(xml);

    LifXmlDocument doc = new LifXmlDocument(xml);
    List<LifImageXmlDocument> imageXmls = doc.getImageXmlDocuments();

    translateMetadata((List<LMSImageXmlDocument>) (List<? extends LMSImageXmlDocument>) imageXmls);
  }
}
