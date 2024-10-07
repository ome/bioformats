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
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.xml.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.xml.LifImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.xml.LifXmlDocument;
import loci.formats.services.OMEXMLService;

/**
 * LMSLIFReader is the new file format reader for Leica LIF files.
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
  private List<String> memoryBlockIds = new ArrayList<>();

  private int lastChannelLutColorIndex = 0;
  private long endPointer;
  List<LifImageXmlDocument> imageXmls = new ArrayList<>();

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
    // return false;
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

    if (lastChannelLutColorIndex < Channel.RED || lastChannelLutColorIndex > Channel.GREY) {
      return null;
    }

    byte[][] lut = new byte[3][256];
    for (int i = 0; i < 256; i++) {
      switch (lastChannelLutColorIndex) {
        case Channel.RED:
          lut[0][i] = (byte) (i & 0xff);
          break;
        case Channel.GREEN:
          lut[1][i] = (byte) (i & 0xff);
          break;
        case Channel.BLUE:
          lut[2][i] = (byte) (i & 0xff);
          break;
        case Channel.CYAN:
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case Channel.MAGENTA:
          lut[0][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case Channel.YELLOW:
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          break;
        case Channel.GREY:
        default:
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

    if (lastChannelLutColorIndex < Channel.RED || lastChannelLutColorIndex > Channel.GREY) {
      return null;
    }

    short[][] lut = new short[3][65536];
    for (int i = 0; i < 65536; i++) {
      switch (lastChannelLutColorIndex) {
        case Channel.RED:
          lut[0][i] = (short) (i & 0xffff);
          break;
        case Channel.GREEN:
          lut[1][i] = (short) (i & 0xffff);
          break;
        case Channel.BLUE:
          lut[2][i] = (short) (i & 0xffff);
          break;
        case Channel.CYAN:
          lut[1][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case Channel.MAGENTA:
          lut[0][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case Channel.YELLOW:
          lut[0][i] = (short) (i & 0xffff);
          lut[1][i] = (short) (i & 0xffff);
          break;
        case Channel.GREY:
        default:
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
      lastChannelLutColorIndex = metadataTranslators.get(getTileIndex(series)).dimensionStore.channels.get(pos[1]).lutColorIndex;
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

    if (getRGBChannelCount() == 3 && metadataTranslators.get(index).dimensionStore.inverseRgb) {
      ImageTools.bgrToRgb(buf, isInterleaved(), bytes, getRGBChannelCount());
    }

    return buf;
  }

  private void seekStartOfPlane(int no, long dataOffset, long planeSize)
      throws IOException {
    int index = getTileIndex(series);
    long posInFile;

    int numberOfTiles = metadataTranslators.get(index).dimensionStore.tileCount;
    if (numberOfTiles > 1) {
      // LAS AF treats tiles just like any other dimension, while we do not.
      // Hence we need to take the tiles into account for a frame's position.
      long bytesIncPerTile = metadataTranslators.get(index).dimensionStore.tileBytesInc;
      long framesPerTile = bytesIncPerTile / planeSize;

      if (framesPerTile > Integer.MAX_VALUE) {
        throw new IOException("Could not read frame due to int overflow");
      }

      int noOutsideTiles = no / (int) framesPerTile;
      int noInsideTiles = no % (int) framesPerTile;

      int tile = series;
      for (int i = 0; i < index; i++) {
        tile -= metadataTranslators.get(i).dimensionStore.tileCount;
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
    if (parent != null && getSeries() < metadataTranslators.size()) {
      // look for an XML file with the same name as this series
      Location xmlFile = new Location(parent, metadataTranslators.get(getSeries()).imageDetails.originalImageName.trim() + ".xml");
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
      lastChannelLutColorIndex = 0;
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

    // --- 1: XML ---
    // 4 bytes: test value 0x70
    if (in.readInt() != LIF_MAGIC_BYTE)
      throw new FormatException(id + " is not a valid Leica LIF file");

    // 4 bytes: length of the following binary chunk to read (1.1). we don't use it
    in.skipBytes(4);

    // --- 1.1: XML content ---
    // 1 byte: test value 0x2A
    if (in.readByte() != LIF_MEMORY_BYTE)
      throw new FormatException(id + " is not a valid Leica LIF file");

    // 4 bytes: number of unicode characters in the XML block (NC)
    int nc = in.readInt();
    // NC * 2 bytes: XML object description
    String xml = DataTools.stripString(in.readString(nc * 2));

    LOGGER.info("Finding image offsets");

    // --- 2: Object memory block(s) ---
    while (in.getFilePointer() < in.length()) {
      LOGGER.debug("Looking for a block at {}; {} blocks read",
          in.getFilePointer(), offsets.size());

      // 4 bytes: test value 0x70
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

      // 4 bytes: length of the following binary chunk to read (2.1). we don't use it
      in.skipBytes(4);

      // --- 2.1: memory description ---
      
      // 1 byte: test value 0x2A
      byte checkByte = in.readByte();
      if (checkByte != LIF_MEMORY_BYTE) {
        throw new FormatException("Invalid Memory Description: found magic " +
            "byte " + checkByte + ", expected " + LIF_MEMORY_BYTE);
      }

      // LIF version 1: 4 bytes: size of memory (2.2)
      // LIF version 2: 8 bytes: size of memory (2.2)
      // 1 byte: test value 0x2A

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

      // 4 bytes: number of unicode characters
      int descrLength = in.readInt();

      // NC * 2 bytes: XML object description
      String description = DataTools.stripString(in.readString(descrLength * 2));

      memoryBlockIds.add(description);
      offsets.add(in.getFilePointer());

      in.seek(in.getFilePointer() + blockLength);
    }

    String name = id.replaceAll("^.*[\\/\\\\]", "").replace(".lof", "");
    initMetadata(xml, name);
    xml = null;

    if (endPointer == 0) {
      endPointer = in.length();
    }

    // remove offsets of unused memory blocks (e.g. frame properties)
    List<Long> temp = new ArrayList<>();

    for (LifImageXmlDocument imageXml : imageXmls){
      String memoryBlockId = imageXml.getMemoryBlockId();
      for (int i = 0; i < memoryBlockIds.size(); i++){
        if (memoryBlockIds.get(i).equals(memoryBlockId)){
          temp.add(offsets.get(i));
          break;
        }
      }
    }

    offsets = temp;
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
    imageXmls = doc.getImageXmlDocuments();

    translateMetadata((List<LMSImageXmlDocument>) (List<? extends LMSImageXmlDocument>) imageXmls);
  }
}
