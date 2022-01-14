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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;

import loci.formats.CoreMetadata;
import loci.formats.CoreMetadataList;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.SubResolutionFormatReader;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.Color;

import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * NativeND2Reader is the file format reader for Nikon ND2 files.
 * The JAI ImageIO library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net. Note that JAI ImageIO is bundled with a
 * version of the JJ2000 library, so it is important that either:
 * (1) the JJ2000 jar file is *not* in the classpath; or
 * (2) the JAI jar file precedes JJ2000 in the classpath.
 *
 * Thanks to Tom Caswell for additions to the ND2 metadata parsing logic.
 */
public class NativeND2Reader extends SubResolutionFormatReader {

  // -- Constants --

  public static final long ND2_MAGIC_BYTES_1 = 0xdacebe0aL;
  public static final long ND2_MAGIC_BYTES_2 = 0x6a502020L;
  private static final int BUFFER_SIZE = 32 * 1024;

  public static final String USE_CHUNKMAP_KEY = "nativend2.chunkmap";
  public static final boolean USE_CHUNKMAP_DEFAULT = true;

  // -- Fields --

  /** Array of image offsets. */
  private long[][] offsets;

  /** Whether or not the pixel data is compressed using JPEG 2000. */
  private boolean isJPEG;

  /** Codec to use when decompressing pixel data. */
  private Codec codec;

  /** Whether or not the pixel data is losslessly compressed. */
  private boolean isLossless;

  private ArrayList<Double> tsT = new ArrayList<Double>();

  private int positionCount = 0;

  private int fieldIndex;

  private long xOffset, yOffset, zOffset;
  private long pfsOffset, pfsStateOffset;

  private ArrayList<Length> posX;
  private ArrayList<Length> posY;
  private ArrayList<Length> posZ;
  private ArrayList<Double> exposureTime = new ArrayList<Double>();

  private Map<String, Integer> channelColors;
  private boolean split = false;
  private int lastChannel = 0;
  private int[] colors;
  private Boolean useZ = null;

  private int nXFields;

  private ND2Handler backupHandler;

  private double trueSizeX = 0;
  private double trueSizeY = 0;
  private Double trueSizeZ = null;

  private ArrayList<String> textChannelNames = new ArrayList<String>();
  private ArrayList<Double> textEmissionWavelengths = new ArrayList<Double>();
  private transient ArrayList<Double> textExcitationWavelengths = new ArrayList<Double>();

  private boolean textData = false;
  private Double refractiveIndex = null;
  Boolean imageMetadataLVProcessed = false;
  String  imageMetadataLVOrder = "";
  private transient Double lensNA = null;
  private transient Double objectiveMag = null;
  private transient String objectiveModel = null;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public NativeND2Reader() {
    super("Nikon ND2", new String[] {"nd2", "jp2"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- NativeND2Reader methods --

  public boolean useChunkMap() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
        USE_CHUNKMAP_KEY, USE_CHUNKMAP_DEFAULT);
    }
    return USE_CHUNKMAP_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    long magic1 = stream.readInt() & 0xffffffffL;
    long magic2 = stream.readInt() & 0xffffffffL;
    return magic1 == ND2_MAGIC_BYTES_1 || magic2 == ND2_MAGIC_BYTES_2;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    if (FormatTools.getBytesPerPixel(getPixelType()) != 1 ||
      !isIndexed() || lastChannel < 0 || lastChannel >= colors.length)
    {
      return null;
    }

    int color = colors[lastChannel];
    if (color == 0) return null;

    byte[][] lut = new byte[3][256];

    int redMax = color & 0xff;
    int greenMax = (color & 0xff00) >> 8;
    int blueMax = (color & 0xff0000) >> 16;

    for (int i=0; i<256; i++) {
      double scale = i / 255.0;
      lut[0][i] = (byte) (redMax * scale);
      lut[1][i] = (byte) (greenMax * scale);
      lut[2][i] = (byte) (blueMax * scale);
    }

    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    if (FormatTools.getBytesPerPixel(getPixelType()) != 2 ||
      !isIndexed() || lastChannel < 0 || lastChannel >= colors.length)
    {
      return null;
    }

    int color = colors[lastChannel];
    if (color == 0) return null;

    short[][] lut = new short[3][65536];

    int redMax = color & 0xff;
    int greenMax = (color & 0xff00) >> 8;
    int blueMax = (color & 0xff0000) >> 16;

    for (int i=0; i<65536; i++) {
      // *Max values are from 0-255; we want LUT values from 0-65535
      double scale = i / 255.0;
      lut[0][i] = (short) (redMax * scale);
      lut[1][i] = (short) (greenMax * scale);
      lut[2][i] = (short) (blueMax * scale);
    }

    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = split ? no % getSizeC() : 0;
    int planeIndex = split ? no / getSizeC() : no;
    in.seek(offsets[getSeries()][planeIndex]);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    if (split) pixel *= getSizeC();

    int totalPlanes = split ? getImageCount() / getSizeC() : getImageCount();

    long maxFP = planeIndex == totalPlanes - 1 ?
      in.length() : offsets[getSeries()][planeIndex + 1];

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();
    options.maxBytes = (int) maxFP;

    int scanlinePad = getScanlinePad();

    if (isJPEG || isLossless) {
      if (codec == null) codec = createCodec(isJPEG);
      byte[] t = null;
      try {
        t = codec.decompress(in, options);
      }
      catch (IOException e) {
        LOGGER.debug("Failed to decompress; plane may be corrupt", e);
        return buf;
      }
      if ((getSizeX() + scanlinePad) * getSizeY() * pixel > t.length) {
        // one padding pixel per row total, instead of one padding pixel
        // per channel per row
        int rowLength = getSizeX() * pixel + scanlinePad * bpp;
        int destLength = w * pixel;

        int p = rowLength * y + x * pixel;
        byte[] pix = new byte[destLength * h];
        for (int row=0; row<h; row++) {
          if (p + destLength <= t.length) {
            System.arraycopy(t, p, pix, row * destLength, destLength);
            int skip = pixel * (getSizeX() - w - x) + scanlinePad * bpp;
            p += destLength + skip;
          }
          else {
            break;
          }
        }

        if (split) {
          pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(),
            bpp, false, true);
        }
        System.arraycopy(pix, 0, buf, 0, pix.length);
      }
      else {
        copyPixels(x, y, w, h, bpp, scanlinePad, t, buf, split);
      }
      t = null;
    }
    else if (split && (getSizeC() <= 4 || scanlinePad == 0) && nXFields == 1) {
      byte[] pix = new byte[(getSizeX() + scanlinePad) * getSizeY() * pixel];
      in.read(pix);
      copyPixels(x, y, w, h, bpp, scanlinePad, pix, buf, split);
      pix = null;
    }
    else if (split) {
      // one padding pixel per row total, instead of one padding pixel
      // per channel per row
      int rowLength = getSizeX() * pixel + scanlinePad * bpp;
      int destLength = w * pixel;

      long skip = (long) rowLength * y;
      in.seek(in.getFilePointer() + skip);
      byte[] pix = new byte[destLength * h];
      long pre = (long) x * pixel;
      long post = (long) pixel * (getSizeX() - w - x) + (scanlinePad * bpp);
      for (int row=0; row<h; row++) {
        in.seek(in.getFilePointer() + pre);
        in.read(pix, row * destLength, destLength);
        in.seek(in.getFilePointer() + post);
      }

      pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(),
        bpp, false, true);
      System.arraycopy(pix, 0, buf, 0, pix.length);
    }
    else {
      // plane is not compressed
      readPlane(in, x, y, w, h, scanlinePad, buf);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      isJPEG = isLossless = false;
      codec = null;
      tsT.clear();

      fieldIndex = 0;
      xOffset = yOffset = zOffset = 0;
      pfsOffset = pfsStateOffset = 0;
      posX = posY = posZ = null;
      channelColors = null;
      split = false;
      nXFields = 0;
      backupHandler = null;
      trueSizeX = 0;
      trueSizeY = 0;
      trueSizeZ = null;
      textChannelNames.clear();
      textEmissionWavelengths.clear();
      textExcitationWavelengths.clear();
      useZ = null;
      textData = false;
      refractiveIndex = null;
      lensNA = null;
      objectiveMag = null;
      objectiveModel = null;
      exposureTime.clear();
      positionCount = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#getAvailableOptions() */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(USE_CHUNKMAP_KEY);
    return optionsList;
  }

  static class ChunkMapEntry {
    public String name;
    public long position;
    public long length;

    public String toString() {
      return String.format("ChunkMapEntry<%s@%d(%d)>", name, position, length);
    }
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // using a 32KB buffer instead of the default 1MB gives
    // better performance with the seek/skip pattern used here
    in = new RandomAccessInputStream(id, BUFFER_SIZE);

    boolean useChunkMap = useChunkMap();
    LOGGER.debug("Attempting to use chunk map = {}", useChunkMap);

    channelColors = new HashMap<String, Integer>();

    if (in.read() == -38 && in.read() == -50) {
      // newer version of ND2 - doesn't use JPEG2000
      LOGGER.info("Searching for blocks");

      isJPEG = false;
      in.seek(0);
      in.order(true);

      // assemble offsets to each block

      ArrayList<String> imageNames = new ArrayList<String>();
      ArrayList<Long> imageOffsets = new ArrayList<Long>();
      ArrayList<long[]> imageLengths = new ArrayList<long[]>();
      ArrayList<Long> customDataOffsets = new ArrayList<Long>();
      ArrayList<long[]> customDataLengths = new ArrayList<long[]>();

      // order matters when working with the text blocks, which is
      // why two ArrayLists are used instead of a HashMap
      ArrayList<String> textStrings = new ArrayList<String>();
      ArrayList<Boolean> validDimensions = new ArrayList<Boolean>();

      ByteArrayHandle xml = new ByteArrayHandle();
      final StringBuilder name = new StringBuilder();

      int extraZDataCount = 0;
      boolean foundMetadata = false;
      boolean foundAttributes = false;
      boolean useLastText = false;
      int blockCount = 0;


      TreeMap<Long, ChunkMapEntry> allChunkPositions = new TreeMap<Long, ChunkMapEntry>();

      if(useChunkMap) {
        /* In modern ND2 files, the chunk map is stored near the end, and contains
         * a list of blocks and their offsets. By using these offsets instead of
         * scanning through the whole file, an enormous speed up can be achieved.
         *
         * Implementation: Read the chunk map beforehand, process the file as normally,
         * once the first ImageDataSeq block is reached, add all images and skip past the
         * image data to process remaining metadata.
         * I haven't read through all of NativeND2Reader, but I hope to have the least
         * chance of inadvertedly breaking something by this approach.
         */
        String chunkMapSignature = "ND2 CHUNK MAP SIGNATURE 0000001";
        in.seek(in.length() - 40);

        if(!in.readString(chunkMapSignature.length()).equals(chunkMapSignature)) {
          useChunkMap = false;
          LOGGER.info("ND2 Warning: No chunk map found!");
        } else {
          in.skipBytes(1);

          long chunkMapPosition = in.readLong();
          in.seek(chunkMapPosition);

          int tmpLenOne = in.readInt();
          int tmpLenTwo = in.readInt();
          long chunkMapLength = in.readLong();

          chunkMapPosition += 16 + tmpLenTwo;
          in.seek(chunkMapPosition);

          long chunkMapEnd = chunkMapPosition + chunkMapLength;

          int imageDataCount = 0;
          int maxImageIndex = -1;

          while(in.getFilePointer() + 1 + 16 < chunkMapEnd) {

            char b = (char) in.readByte();
            while (b != '!') {
              name.append(b);
              b = (char) in.readByte();
            }

            ChunkMapEntry entry = new ChunkMapEntry();
            entry.name = name.toString();
            name.delete(0, name.length());

            if(entry.name.equals(chunkMapSignature))
              break;

            entry.position = in.readLong();
            entry.length = in.readLong();

            if (entry.name.startsWith("ImageDataSeq|")) {
              imageDataCount++;
              int imageIndex = -1;
              try {
                imageIndex = Integer.parseInt(entry.name.substring("ImageDataSeq|".length()));
                if (imageIndex > maxImageIndex) {
                  maxImageIndex = imageIndex;
                }
              }
              catch (NumberFormatException e) {
                LOGGER.trace(entry.name, e);
              }
            }

            allChunkPositions.put(entry.position, entry);

            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("ND2 {}", entry.toString());
            }
          }
          if (imageDataCount != maxImageIndex + 1) {
            LOGGER.warn("Discarding chunk map; image data count = {}, max index = {}",
              imageDataCount, maxImageIndex);
            useChunkMap = false;
          }
        }

        in.seek(0);
      }

      // search for blocks
      byte[] sigBytes = {-38, -50, -66, 10}; // 0xDACEBE0A
      byte[] buf = new byte[BUFFER_SIZE];

      if(useChunkMap) {

        long checkEvery = in.length() / 10;
        long nextCheck = 0;

        // chunk map sanity checks

        for (ChunkMapEntry entry : allChunkPositions.values()) {
          if (!entry.name.startsWith("ImageDataSeq")) {
            continue;
          }

          if(entry.position > nextCheck) {

            in.seek(entry.position);
            in.read(buf, 0, sigBytes.length);

            if (!(buf[0] == sigBytes[0] && buf[1] == sigBytes[1] && buf[2] == sigBytes[2] && buf[3] == sigBytes[3])) {
              LOGGER.warn("Broken ND2 File detected! Disabling chunk map processing.");

              useChunkMap = false;
              break;
            }

            nextCheck = entry.position + checkEvery;
          }
        }
      }

      int chunkmapSkips = 0;
      Boolean currentCountSetted = false;
      int XYCount = 1;
      int timeCount = 1;
      int zCount = 1;

      in.seek(0);
      int validBits = 0;

      while (in.getFilePointer() < in.length() - 1 && in.getFilePointer() >= 0)
      {
        int foundIndex = -1;
        in.read(buf, 0, sigBytes.length);
        while (foundIndex == -1 && in.getFilePointer() < in.length()) {
          int n = in.read(buf, sigBytes.length, buf.length - sigBytes.length);
          for (int i=0; i<buf.length-sigBytes.length; i++) {
            for (int j=0; j<sigBytes.length; j++) {
              if (buf[i + j] != sigBytes[j]) break;
              if (j == sigBytes.length - 1) foundIndex = i;
            }
            if (foundIndex != -1) break;
          }
          if (foundIndex == -1) {
            // likely faster than System.arraycopy
            buf[0] = buf[buf.length - 4];
            buf[1] = buf[buf.length - 3];
            buf[2] = buf[buf.length - 2];
            buf[3] = buf[buf.length - 1];
          }
          else if (in.getFilePointer() - n + foundIndex < in.length()) {
            in.seek(in.getFilePointer() - n + foundIndex);
          }
        }
        if (in.getFilePointer() > in.length() - 24 || foundIndex == -1) {
          break;
        }

        Long helper = in.getFilePointer();       // Remember starting position

        int nameLength = in.readInt();         // Length of the block name
        long dataLength = in.readLong();       // Length of the data
        String nameAttri = in.readString(nameLength).trim();  // Read the name
        Long stop = helper + (dataLength + nameLength); // Where this block ends

        boolean seq = false; // Only 1 MetadataSeq is needed

        // Send to iteration
        // (we are interested only in xxxxLV - LV = light variant)
        if (nameAttri.contains("MetadataLV") ||
          nameAttri.contains("CalibrationLV") ||
          (nameAttri.contains("MetadataSeqLV") && !seq))
        {
          // prevent position count from being doubled
          if (nameAttri.equals("ImageMetadataLV!")) {
            positionCount = 0;
          }
          iterateIn(in, stop);
        }

        // Only 1 MetadataSeq is needed
        // (others should be same, and time is saved elsewhere)
        if (nameAttri.contains("MetadataSeqLV")) {
          seq = true;
        }

        in.seek(helper + 12);  // Return to starting position

        int len = (int) (nameLength + dataLength);

        long fp = in.getFilePointer();
        String blockType = in.readString(12);

        int percent = (int) (100 * fp / in.length());
        LOGGER.info("Parsing block '{}' {}%", blockType, percent);
        blockCount++;

        int skip = len - 12 - nameLength * 2;
        if (skip <= 0) skip += nameLength * 2;

        // Image calibration for newer nd2 files

        if (blockType.endsWith("Calibra")) {
          long veryStart = in.getFilePointer();
          in.skipBytes(12); // ImageCalibra|tionLV

          long endFP = in.getFilePointer() + len - 24;
          while (in.read() == 0);

          while (in.getFilePointer() < endFP) {
            int nameLen = in.read();
            if (nameLen == 0) {
              in.seek(in.getFilePointer() - 3);
              nameLen = in.read();
            }
            if (nameLen < 0) {
              break;
            }

            // Get data
            String attributeName =
              DataTools.stripString(in.readString(nameLen * 2));
            double valueOrLength = in.readDouble();

            if (attributeName.equals("dCalibration")) {
              if (valueOrLength > 0) {
                addGlobalMeta(attributeName, valueOrLength);
                if (trueSizeX == 0) {
                  trueSizeX = valueOrLength;
                }
                else if (trueSizeY == 0) {
                  trueSizeY = valueOrLength;
                }
              }
              break;  // Done with calibration
            }
          }
          in.seek(veryStart); // For old nd2 files
        }

        if (blockType.startsWith("ImageDataSeq")) {
          if (foundMetadata && foundAttributes) {
            imageOffsets.clear();
            imageNames.clear();
            imageLengths.clear();
            customDataOffsets.clear();
            customDataLengths.clear();
            foundMetadata = false;
            foundAttributes = false;
            extraZDataCount = 0;
            useLastText = true;
          }

          if(useChunkMap && chunkmapSkips == 0) {
            ChunkMapEntry lastImage = null;

            // sanity check: see if the chunk we just found is actually in the chunkmap ...

            long lookupPosition = in.getFilePointer() - 28;

            Long lookupResult = allChunkPositions.floorKey(lookupPosition);

            if(lookupResult == null || lookupResult != lookupPosition) {
              // if not, deactivate chunkmap processing and try classic
              useChunkMap = false;
              in.seek(lookupPosition);
              continue;
            }

            for(ChunkMapEntry entry : allChunkPositions.values()) {
              if((entry.position + 28) < in.getFilePointer()) {
                continue;
              }

              if(!entry.name.startsWith("ImageDataSeq")) {
                continue;
              }

              if(lastImage!=null) {
                chunkmapSkips = (int)(((entry.position - lastImage.position) / entry.length) - 1);
                if(chunkmapSkips > 0) {
                  break;
                }
              }

              lastImage = entry;

              imageOffsets.add(new Long(entry.position + 16));
              int realLength = (int) Math.max(entry.name.length() + 1, nameLength);
              imageLengths.add(new long[] {realLength, entry.length - nameLength - 16, getSizeX() * getSizeY()});
              imageNames.add(entry.name.substring(12));

              blockCount ++;

              percent = (int) (100 * entry.position / in.length());
              LOGGER.info("Parsing block '{}' {}%", "ImageDataSeq", percent);
            }

            blockCount --; // one was already added by the outer blockCount ++;

            if (lastImage.position + lastImage.length >= in.length()) {
              in.seek(lastImage.position + 16);
            }
            else {
              in.seek(lastImage.position + lastImage.length);
            }

            continue;

          }

          if(chunkmapSkips > 0) {
            chunkmapSkips -= 1;
          }

          dataLength -= 31;
          LOGGER.debug(
            "Adding non-chunkmap offset {}, nameLength = {}, dataLength = {}",
              fp, nameLength, dataLength);
          imageOffsets.add(fp);
          imageLengths.add(new long[] {nameLength, dataLength, getSizeX() * getSizeY()});
          char b = (char) in.readByte();
          while (b != '!') {
            name.append(b);
            b = (char) in.readByte();
          }
          imageNames.add(name.toString());
          name.setLength(0);
        }
        else if (blockType.startsWith("ImageText")) {
          foundMetadata = true;
          in.skipBytes(6);
          while (in.read() == 0);
          long startFP = in.getFilePointer();
          in.seek(startFP - 1);

          // text block can contain XML (which may be cut off)
          // or sequence of string objects

          int typeByte = in.read();

          // 11 is the max object type code (see iterateIn case statement)
          // don't look for '<' because old ND2s might have
          // cut off or incorrectly aligned XML

          if (typeByte > 11) {
            in.seek(startFP - 1);
            String textString = DataTools.stripString(in.readString((int) dataLength));
            textStrings.add(textString);
            validDimensions.add(blockCount > 2);
            if (!textString.startsWith("<")) {
              skip = 0;
            }
          }
          else {
            int charCount = in.read();
            textStrings.add(DataTools.stripString(in.readString(charCount * 2)));
            validDimensions.add(blockCount > 2);
            int numTextInfos = in.readInt();
            long remainingBytes = in.readLong();

            // reassemble sequence of strings into single multi-line string
            // that will be parsed once so that the same ND2Handler is
            // used for the whole block
            // this should maybe be refactored at some point?
            ArrayList<String> text = iterateIn(in, startFP + dataLength - 1, true);
            StringBuffer b = new StringBuffer();
            for (int t=0; t<text.size(); t++) {
              b.append(text.get(t));
              b.append("\n");
            }
            textStrings.add(b.toString());
            validDimensions.add(blockCount > 2);

            // make sure the file pointer is at the end of the block
            in.seek(startFP + dataLength - 1);
            skip = 0;
          }
        }
        else if (blockType.startsWith("Image") ||
          blockType.startsWith("CustomDataVa"))
        {
          if (blockType.equals("ImageAttribu")) {
            foundAttributes = true;
            in.skipBytes(6);
            long endFP = in.getFilePointer() + len - 18;
            while (in.read() == 0);

            boolean canBeLossless = true;

            while (in.getFilePointer() < endFP) {
              int nameLen = in.read();
              if (nameLen == 0) {
                in.seek(in.getFilePointer() - 3);
                nameLen = in.read();
              }
              if (nameLen < 0) {
                break;
              }
              long start = in.getFilePointer();
              String attributeName =
                DataTools.stripString(in.readString(nameLen * 2));
              if (attributeName.startsWith("xml ") ||
                attributeName.startsWith("ml version") ||
                attributeName.startsWith("l version") ||
                attributeName.startsWith("version"))
              {
                if (attributeName.startsWith("xml ")) {
                  in.seek(start - 2);
                }
                else if (attributeName.startsWith("ml version")) {
                  in.seek(start - 3);
                }
                else if (attributeName.startsWith("l version")) {
                  in.seek(start - 4);
                }
                else {
                  in.seek(start - 6);
                }
                attributeName = in.readCString();
                String xmlString = XMLTools.sanitizeXML(attributeName.trim());
                xmlString =
                  xmlString.substring(0, xmlString.lastIndexOf(">") + 1);
                if (xmlString.startsWith("<?xml")) {
                  xmlString = xmlString.substring(xmlString.indexOf('>') + 1);
                }
                if (!xmlString.endsWith("</variant>")) {
                  xmlString += "</variant>";
                }

                if (getDimensionOrder() == null) {
                  core.get(0, 0).dimensionOrder = "";
                }

                try {
                  ND2Handler handler =
                    new ND2Handler(core, imageOffsets.size());
                  XMLTools.parseXML(xmlString, handler);
                  xmlString = null;
                  core = handler.getCoreMetadataList();
                  if (backupHandler == null) {
                    backupHandler = handler;
                  }
                }
                catch (IOException e) {
                  LOGGER.debug("Could not parse XML", e);
                }

                in.seek(in.getFilePointer() - 8);
                break;
              }

              int valueOrLength = in.readInt();

              addGlobalMeta(attributeName, valueOrLength);

              if (attributeName.equals("uiWidth")) {
                core.get(0, 0).sizeX = valueOrLength;
              }
              else if (attributeName.equals("uiHeight")) {
                core.get(0, 0).sizeY = valueOrLength;
              }
              else if (attributeName.equals("uiComp")) {
                core.get(0, 0).sizeC = valueOrLength;
              }
              else if (attributeName.equals("uiBpcInMemory")) {
                core.get(0, 0).pixelType = FormatTools.pixelTypeFromBytes(
                  valueOrLength / 8, false, true);
              }
              else if (attributeName.equals("uiBpcSignificant")) {
                validBits = valueOrLength;
                core.get(0, 0).bitsPerPixel = valueOrLength;
              }
              else if (attributeName.equals("dCompressionParam")) {
                isLossless = valueOrLength >= 0;
              }
              else if (attributeName.equals("eCompression")) {
                canBeLossless = valueOrLength <= 0;
              }
              else if (attributeName.equals("SLxImageAttributes")) {
                int toSkip = valueOrLength - 5;
                if ((toSkip % 2) == 1) {
                  toSkip++;
                }
                in.skipBytes(toSkip);
              }
              else if (attributeName.endsWith("Desc")) {
                in.seek(in.getFilePointer() - 2);
              }
              else if (attributeName.equals("SLxExperiment")) {
                in.skipBytes(8);
              }
              else if (attributeName.equals("wsCameraName")) {
                in.seek(in.getFilePointer() - 4);
                byte[] b = new byte[2];
                in.read(b);
                StringBuilder value = new StringBuilder();
                while (b[0] != 0) {
                  value.append(b[0]);
                  in.read(b);
                }
                addGlobalMeta(attributeName, value.toString());
              }
              else if (attributeName.equals("uLoopPars")) {
                int v2 = in.readInt();
                int v3 = in.readInt();
                addGlobalMeta(attributeName,
                  valueOrLength + ", " + v2 + ", " + v3);
              }
              else if (attributeName.equals("pPeriod")) {
                in.skipBytes(22);
              }
              else if (attributeName.equals("dPeriod") ||
                attributeName.equals("dDuration"))
              {
                in.skipBytes(4);
              }
              else if (attributeName.equals("bDurationPref")) {
                  in.seek(in.getFilePointer() - 3);
                }
              in.skipBytes(1);
            }

            if (in.getFilePointer() > endFP) {
              in.seek(endFP);
            }

            isLossless = isLossless && canBeLossless;
          }
          else {
            if (blockType.startsWith("ImageMetadat") && !imageMetadataLVProcessed) {
              foundMetadata = true;
              long startFilePointer = in.getFilePointer();
              in.skipBytes(6);
              long endFP = in.getFilePointer() + len - 18;
              while (in.read() == 0);

              int eType = 0;
              Boolean nextExperiment = true;

              long currentFilePointer = in.getFilePointer();

              while (true) {
                in.seek(currentFilePointer);

                int nameLen = in.read();
                if (nameLen == 0 || nameLen < 0) {
                  currentFilePointer++;
                  continue;
                }

                String attributeName =
                        DataTools.stripString(in.readString(nameLen * 2));

                if(attributeName.length() != nameLen - 1)
                {
                  currentFilePointer++;
                  continue;
                }

                if (attributeName.equals("SLxExperiment")) {
                  currentFilePointer += nameLen * 2;
                  imageMetadataLVProcessed = true;
                  imageMetadataLVOrder = "";
                }

                if (attributeName.equals("eType")) {
                  currentFilePointer += nameLen * 2;
                  if(nextExperiment)
                    eType = in.readInt();
                  nextExperiment = false;
                } else
                if (attributeName.equals("uiCount")) {
                  currentFilePointer += nameLen * 2;

                  if(!currentCountSetted)
                  {
                    if(eType == 2)
                    {
                      imageMetadataLVOrder = "M" + imageMetadataLVOrder;
                      XYCount = in.readInt();
                    } else if(eType == 1)
                    {
                      imageMetadataLVOrder = "T" + imageMetadataLVOrder;
                      timeCount = in.readInt();
                    } if(eType == 4)
                    {
                      imageMetadataLVOrder = "Z" + imageMetadataLVOrder;
                      zCount = in.readInt();
                    }
                    currentCountSetted = true;
                  }
                } else
                if (attributeName.equals("bKeepObject")) {
                  currentFilePointer += nameLen * 2;
                } else
                if (attributeName.equals("uiRepeatCount")) {
                  currentFilePointer += nameLen * 2;
                } else
                  if (attributeName.equals("vectStimulationConfigurationsSize")) {
                  currentFilePointer += nameLen * 2;
                } else
                if (attributeName.equals("uiNextLevelCount")) {
                  currentFilePointer += nameLen * 2;
                  int uiNextLevelCount = in.readInt();

                  if(uiNextLevelCount == 0)
                  {
                    break;
                  }
                  currentCountSetted = false;
                  nextExperiment = true;
                }

                if (in.getFilePointer() > endFP) {
                  in.seek(startFilePointer);
                  break;
                }

                currentFilePointer++;
              }

              if (in.getFilePointer() > startFilePointer) {
                in.seek(startFilePointer);
              }
            }

            int length = len - 12;
            byte[] b = new byte[length];
            in.read(b);

            // strip out invalid characters
            int off = 0;
            for (int j=0; j<length; j++) {
              char c = (char) b[j];
              if ((off == 0 && c == '!') || c == 0) off = j + 1;
              if (Character.isISOControl(c) || !Character.isDefined(c)) {
                b[j] = (byte) ' ';
              }
            }

            if (length - off >= 5 && b[off] == '<' && b[off + 1] == '?' &&
              b[off + 2] == 'x' && b[off + 3] == 'm' && b[off + 4] == 'l')
            {
              boolean endBracketFound = false;
              while (!endBracketFound) {
                if (b[off++] == '>') {
                  endBracketFound = true;
                }
              }
              xml.write(b, off, b.length - off);
            }

          }
          skip = 0;
        }
        else if (getMetadataOptions().getMetadataLevel() !=
          MetadataLevel.MINIMUM)
        {
          int nDoubles = len / 8;
          int nInts = len / 4;
          long doubleOffset = fp + 8 * (nDoubles - imageOffsets.size());
          long intOffset = fp + 4 * (nInts - imageOffsets.size());
          if (nameAttri.startsWith("CustomData|AcqTimesCache")) {
            customDataOffsets.add(fp);
            customDataLengths.add(new long[] {nameLength, dataLength});
          }
          else if (blockType.startsWith("CustomData|Z")) {
            if (zOffset == 0) {
              zOffset = doubleOffset;
            }
            extraZDataCount++;
          }
          else if (blockType.startsWith("CustomData|X")) {
            xOffset = doubleOffset;
          }
          else if (blockType.startsWith("CustomData|Y")) {
            yOffset = doubleOffset;
          }
          else if (blockType.startsWith("CustomData|P")) {
            if (pfsOffset == 0) {
              pfsOffset = intOffset;
            }
            else if (pfsStateOffset == 0) {
              pfsStateOffset = intOffset;
            }
          }
        }
        if (skip > 0 && skip + in.getFilePointer() <= in.length()) {
          in.skipBytes(skip);
        }
      }
      
      if(currentCountSetted && imageMetadataLVOrder.length() > 0 && 
          (imageOffsets.size() == 0 || timeCount * zCount * XYCount == imageOffsets.size())) {
        setDimensions(timeCount, zCount, XYCount);
      }
      else {
        imageMetadataLVProcessed = false;
      }

      // parse text blocks

      int nChannelNames = textChannelNames.size();
      for (int i=0; i<textStrings.size(); i++) {
        parseText(textStrings.get(i), imageOffsets.size(),
          validDimensions.get(i));
      }
      if (textChannelNames.size() > nChannelNames) {
        int diff = textChannelNames.size() - nChannelNames;
        while (textChannelNames.size() > diff) {
          textChannelNames.remove(0);
        }
      }

      // parse XML blocks

      String xmlString =
        new String(xml.getBytes(), 0, (int) xml.length(), Constants.ENCODING);
      xml = null;
      xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ND2>" +
        xmlString + "</ND2>";
      xmlString = XMLTools.sanitizeXML(xmlString);

      core.get(0, 0).dimensionOrder = "";
      ND2Handler handler =
        new ND2Handler(core, getSizeX() == 0, imageOffsets.size());
      XMLTools.parseXML(xmlString, handler);
      xmlString = null;

      if (handler.getChannelColors().size() > 0) {
        channelColors = handler.getChannelColors();
      }
      if (!isLossless) {
        isLossless = handler.isLossless();
      }
      fieldIndex = handler.getFieldIndex();
      core = handler.getCoreMetadataList();

      final Map<String, Object> globalMetadata = handler.getMetadata();
      nXFields = handler.getXFields();
      if (nXFields > 6) {
        nXFields = 0;
      }
      for (String key : globalMetadata.keySet()) {
        addGlobalMeta(key, globalMetadata.get(key));
        if (key.equals("ChannelCount")) {
          for (int i=0; i<getSeriesCount(); i++) {
            CoreMetadata ms = core.get(i, 0);
            if (ms.sizeC == 0) {
              ms.sizeC =
                Integer.parseInt(globalMetadata.get(key).toString());
              if (ms.sizeC > 1) {
                ms.rgb = true;
              }
            }
          }
        }
        else if (key.equals("uiBpcInMemory")) {
          int bpc = Integer.parseInt(globalMetadata.get(key).toString());
          core.get(0, 0).pixelType = FormatTools.pixelTypeFromBytes(
            bpc / 8, false, false);
        }
      }

      int planeCount = core.size() * getSizeZ() * getSizeT();
      if (!textData && planeCount < imageOffsets.size() && planeCount > 0 &&
        (imageOffsets.size() % (planeCount / core.size())) == 0)
      {
        int seriesCount = imageOffsets.size() / (planeCount / core.size());
        core = new CoreMetadataList();

        for (int i=0; i<seriesCount; i++) {
          core.add(handler.getCoreMetadataList().get(0, 0));
        }
      }

      int numSeries = core.size();

      // rearrange image data offsets

      if (numSeries == 0) numSeries = 1;
      else if (numSeries == 1 && positionCount > 1) {
        for (int i=1; i<positionCount; i++) {
          core.add(core.get(0, 0));
        }
        numSeries = core.size();
      }

      if (getSizeZ() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core.get(i, 0).sizeZ = 1;
        }
        if (getSizeT() == 0) {
          for (int i=0; i<getSeriesCount(); i++) {
            core.get(i, 0).sizeT = imageOffsets.size() / getSeriesCount();
          }
        }
      }
      if (getSizeT() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core.get(i, 0).sizeT = 1;
        }
      }
      if (getSizeC() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core.get(i, 0).sizeC = 1;
        }
      }

      if (getSizeZ() * getSizeT() == imageOffsets.size() && core.size() > 1) {
        CoreMetadata ms0 = core.get(0, 0);
        core = new CoreMetadataList();
        core.add(ms0);
      }

      if (positionCount != getSeriesCount() && (getSizeZ() == imageOffsets.size() || (extraZDataCount > 1 && getSizeZ() == 1 && (extraZDataCount == getSizeC())) || (handler.getXPositions().size() == 0 && (xOffset == 0 && getSizeZ() != getSeriesCount()))) && getSeriesCount() > 1) {
        CoreMetadata ms0 = core.get(0, 0);
        if (getSeriesCount() > ms0.sizeZ) {
          ms0.sizeZ = getSeriesCount();
        }
        core = new CoreMetadataList();
        core.add(ms0);
      }

      // make sure the channel count is reasonable
      // sometimes the XML will indicate that there are multiple channels,
      // when in fact there is only one channel

      long firstOffset = imageOffsets.get(0);
      long secondOffset =
        imageOffsets.size() > 1 ? imageOffsets.get(1) : in.length();
      long availableBytes = secondOffset - firstOffset;

      // make sure that we have the compression setting correct
      // it's not always easy to tell from the metadata
      isLossless = true;

      long fp = in.getFilePointer();
      long[] firstLengths = imageLengths.get(0);
      in.seek(firstOffset + firstLengths[0] + 8);

      if (codec == null) codec = createCodec(false);
      try {
        CodecOptions options = new CodecOptions();
        options.littleEndian = isLittleEndian();
        options.interleaved = true;
        options.maxBytes = (int) secondOffset;
        byte[] t = codec.decompress(in, options);

        if (t.length == 2 * getSizeX() * getSizeY() &&
          getPixelType() == FormatTools.INT8)
        {
          core.get(0, 0).pixelType = FormatTools.UINT16;
        }
        availableBytes = t.length;
      }
      catch (IOException e) {
        isLossless = false;
      }

      boolean allEqual = true;
      long offsetDiff = imageOffsets.get(0) - imageLengths.get(0)[1];
      for (int i=1; i<imageOffsets.size(); i++) {
        long nextOffsetDiff = imageOffsets.get(i) - imageLengths.get(i)[1];
        if (imageLengths.get(i)[1] != imageLengths.get(0)[1] && offsetDiff != nextOffsetDiff) {
          allEqual = false;
          break;
        }
      }

      if (!allEqual && !isLossless && imageOffsets.size() > 1) {
        int plane = (getSizeX() + getScanlinePad()) * getSizeY();
        boolean fixByteCounts = false;
        if (plane > 0) {
          for (int i=0; i<imageOffsets.size(); i++) {
            long check = imageLengths.get(i)[2];
            long length = imageLengths.get(i)[1] - 8;
            if ((length % plane != 0 && length % (getSizeX() * getSizeY()) != 0) || (check > 0 && plane != check)) {
              if (imageOffsets.get(i) - length != offsetDiff + 8) {
                if (i == 0) {
                  fixByteCounts = true;
                }
                imageOffsets.remove(i);
                imageLengths.remove(i);
                i--;
              }
            }
          }
        }

        if (fixByteCounts) {
          firstOffset = imageOffsets.get(0);
          secondOffset = imageOffsets.size() > 1 ?
            imageOffsets.get(1) : in.length();
          availableBytes = secondOffset - firstOffset;

          if (isLossless) {
            firstLengths = imageLengths.get(0);
            in.seek(firstOffset + firstLengths[0] + 8);
            CodecOptions options = new CodecOptions();
            options.littleEndian = isLittleEndian();
            options.interleaved = true;
            options.maxBytes = (int) secondOffset;
            byte[] t = codec.decompress(in, options);
            availableBytes = t.length;
          }

        }
      }

      in.seek(fp);

      long planeSize = getSizeX() * getSizeY() * getSizeC() *
        FormatTools.getBytesPerPixel(getPixelType());

      if (availableBytes < planeSize) {
        LOGGER.debug("Correcting SizeC: was {}", getSizeC());
        LOGGER.debug("plane size = {}", planeSize);
        LOGGER.debug("available bytes = {}", availableBytes);

        core.get(0, 0).sizeC = (int) (availableBytes / (planeSize / getSizeC()));
        if (getSizeC() == 0) {
          core.get(0, 0).sizeC = 1;
        }
        planeSize = getSizeX() * getSizeY() * getSizeC() *
          FormatTools.getBytesPerPixel(getPixelType());
      }

      if (planeSize > 0 && availableBytes % planeSize != 0) {
        // an extra 4K block of zeros may have been appended
        if ((availableBytes - 4096) % planeSize == 0) {
          availableBytes -= 4096;
        }
      }
      if (planeSize > 0 && imageOffsets.size() > 1 &&
        availableBytes > DataTools.safeMultiply64(planeSize, 3))
      {
        if (availableBytes < DataTools.safeMultiply64(planeSize, 6)) {
          core.get(0, 0).sizeC = 3;
          core.get(0, 0).rgb = true;
          if (getPixelType() == FormatTools.INT8) {
            core.get(0, 0).pixelType = availableBytes > planeSize * 5 ?
              FormatTools.UINT16 : FormatTools.UINT8;
          }
        }
      }
      else if (((planeSize > 0 &&
        availableBytes >= DataTools.safeMultiply64(planeSize, 2)) ||
        getSizeC() > 3) && getPixelType() == FormatTools.INT8)
      {
        core.get(0, 0).pixelType = FormatTools.UINT16;
        planeSize *= 2;
        if (getSizeC() > 3 && availableBytes % planeSize != 0 &&
          planeSize > availableBytes)
        {
          core.get(0, 0).sizeC = 3;
          core.get(0, 0).rgb = true;
        }
      }
      else if (getSizeC() == 2 && getPixelType() == FormatTools.INT8 &&
        availableBytes >= planeSize * 2)
      {
        core.get(0, 0).pixelType = FormatTools.UINT16;
      }
      else if (getPixelType() == FormatTools.INT8) {
        core.get(0, 0).pixelType = FormatTools.UINT8;
      }

      // now that pixel type is est, restore number of valid bits per pixel
      if (core.get(0, 0).bitsPerPixel == 0 && validBits > 0 &&
        validBits <= 8 * FormatTools.getBytesPerPixel(core.get(0, 0).pixelType))
      {
        core.get(0, 0).bitsPerPixel = validBits;
      }

      if (getSizeX() == 0) {
        core.get(0, 0).sizeX = (int) Math.sqrt(availableBytes /
          (getSizeC() * FormatTools.getBytesPerPixel(getPixelType())));
        core.get(0, 0).sizeY = getSizeX();
      }

      int rowSize = getSizeX() * FormatTools.getBytesPerPixel(getPixelType()) *
        getSizeC();
      long sizeY = availableBytes / rowSize;
      if (sizeY < getSizeY()) {
        core.get(0, 0).sizeY = (int) sizeY;
      }

      if (getSizeT() == imageOffsets.size() && getSeriesCount() > 1) {
        CoreMetadata firstCore = core.get(0, 0);
        core = new CoreMetadataList();
        core.add(firstCore);
      }

      // calculate the image count
      for (int i=0; i<getSeriesCount(); i++) {
        CoreMetadata ms = core.get(i, 0);
        ms.imageCount = getSizeZ() * getSizeT() * getSizeC();
        if (imageOffsets.size() / getSeriesCount() < ms.imageCount) {
          ms.imageCount /= getSizeC();
        }
        if (ms.imageCount > imageOffsets.size() / getSeriesCount()) {
          int diff = imageOffsets.size() - ms.imageCount;
          if (diff >= 0 && diff < ms.sizeZ && diff < ms.sizeT) {
            CoreMetadata ms0 = core.get(0, 0);
            core = new CoreMetadataList();
            core.add(ms0);
            numSeries = 1;
            break;
          }
          else if (imageOffsets.size() % ms.sizeT == 0) {
            ms.imageCount = imageOffsets.size() / getSeriesCount();
            ms.sizeZ = ms.imageCount / ms.sizeT;
            ms.dimensionOrder = "CZT";
          }
          else {
            ms.imageCount = imageOffsets.size() / getSeriesCount();
            ms.sizeZ = 1;
            ms.sizeT = ms.imageCount;
          }
        }
      }

      if (numSeries * getImageCount() == 1 && imageOffsets.size() > 1) {
        for (int i=0; i<getSeriesCount(); i++) {
          core.get(i, 0).imageCount = imageOffsets.size() / getSeriesCount();
          core.get(i, 0).sizeZ = getImageCount();
          core.get(i, 0).sizeT = 1;
        }
      }

      if (getSizeZ() * getSizeT() * (split ? 1 : getSizeC()) <
        imageOffsets.size() / getSeriesCount())
      {
        split = getSizeC() > 1;
        int count = imageOffsets.size() / getSeriesCount();
        if (!split && count >= getSizeC()) {
          count /= getSizeC();
        }

        int diff = count - getSizeZ() * getSizeT();
        if (diff == getSizeZ()) {
          core.get(0, 0).sizeT++;
        }
        else if (getSizeT() > getSizeZ()) {
          core.get(0, 0).sizeZ = 1;
          core.get(0, 0).sizeT = count;
        }
        else {
          core.get(0, 0).sizeT = 1;
          core.get(0, 0).sizeZ = count;
        }

        if (useZ != null && !useZ) {
          CoreMetadata original = core.get(0, 0);
          int nSeries = imageOffsets.size() / (getSizeZ() * getSizeT());
          for (int i=1; i<nSeries; i++) {
            core.add(original);
          }
          numSeries = core.size();
        }

        if (getSizeZ() * getSizeT() * (split ? 1 : getSizeC()) <
          imageOffsets.size() / getSeriesCount() && getSizeC() > 4)
        {
          core.get(0, 0).sizeZ = 1;
          core.get(0, 0).sizeT = imageOffsets.size() / getSeriesCount();
        }

        core.get(0, 0).imageCount = getSizeZ() * getSizeT() * getSizeC();
      }

      if (getDimensionOrder().equals("T")) {
        fieldIndex = 0;
      }
      else if (getDimensionOrder().equals("ZT") && fieldIndex == 2) {
        fieldIndex--;
      }

      if (getSizeC() > 1 && getDimensionOrder().indexOf('C') == -1) {
        core.get(0, 0).dimensionOrder = "C" + getDimensionOrder();
        fieldIndex++;
      }

      core.get(0, 0).dimensionOrder = "XY" + getDimensionOrder();
      if (getDimensionOrder().indexOf('Z') == -1) core.get(0, 0).dimensionOrder += 'Z';
      if (getDimensionOrder().indexOf('C') == -1) core.get(0, 0).dimensionOrder += 'C';
      if (getDimensionOrder().indexOf('T') == -1) core.get(0, 0).dimensionOrder += 'T';

      if (getSizeZ() == 0) {
        core.get(0, 0).sizeZ = 1;
      }
      if (getSizeT() == 0) {
        core.get(0, 0).sizeT =  1;
      }
      if (getSizeC() == 0) {
        core.get(0, 0).sizeC = 1;
      }
      core.get(0, 0).imageCount = getSizeZ() * getSizeT();
      if (!isRGB()) {
        core.get(0, 0).imageCount *= getSizeC();
      }

      posX = handler.getXPositions();
      posY = handler.getYPositions();
      posZ = handler.getZPositions();

      int uniqueX = 0, uniqueY = 0, uniqueZ = 0;
      if (posX.size() == 0 && xOffset != 0) {
        in.seek(xOffset);
        for (int i=0; i<imageOffsets.size(); i++) {
          final Double number = Double.valueOf(in.readDouble());
          final Length x = new Length(number, UNITS.MICROMETER);
          if (!posX.contains(x)) {
            uniqueX++;
          }
          posX.add(x);
        }
      }
      if (posY.size() == 0 && yOffset != 0) {
        in.seek(yOffset);
        for (int i=0; i<imageOffsets.size(); i++) {
          final Double number = Double.valueOf(in.readDouble());
          final Length y = new Length(number, UNITS.MICROMETER);
          if (!posY.contains(y)) {
            uniqueY++;
          }
          posY.add(y);
        }
      }
      if (posZ.size() == 0 && zOffset != 0) {
        in.seek(zOffset);
        for (int i=0; i<imageOffsets.size(); i++) {
          final Double number = Double.valueOf(in.readDouble());
          final Length z = new Length(number, UNITS.MICROMETER);
          if (!posZ.contains(z)) {
            boolean unique = true;
            for (int q=0; q<posZ.size(); q++) {
              // account for potential stage drift
              final double z1 = z.value(UNITS.MICROMETER).doubleValue();
              final double z2 = posZ.get(q).value(UNITS.MICROMETER).doubleValue();
              if (Math.abs(z1 - z2) <= 0.05) {
                unique = false;
                break;
              }
            }
            if (unique) {
              uniqueZ++;
            }
          }
          posZ.add(z);
        }
      }
      if (pfsOffset != 0) {
        in.seek(pfsOffset);
        for (int i=0; i<imageOffsets.size(); i++) {
          addGlobalMetaList("PFS Offset", in.readInt());
        }
      }
      if (pfsStateOffset != 0) {
        in.seek(pfsStateOffset);
        for (int i=0; i<imageOffsets.size(); i++) {
          addGlobalMetaList("PFS Status", in.readInt());
        }
      }

      if (core.size() == 1 && ((uniqueX == getSizeT() &&
        uniqueY == getSizeT()) || uniqueZ == getSizeT()))
      {
        int count = getSizeT();
        core.get(0, 0).imageCount /= count;
        core.get(0, 0).sizeT = 1;

        for (int i=1; i<count; i++) {
          core.add(core.get(0, 0));
        }
        numSeries = core.size();
      }

      // reset the series count if we're confident that the image count
      // covers all of the offsets; this prevents too much memory being used
      // when the offsets array is allocated
      if (numSeries > 1 && ((getImageCount() == imageOffsets.size()  &&
          getSizeC() == 1) || (getImageCount() == imageOffsets.size() * getSizeC()))) 
      {
        CoreMetadata first = core.get(0, 0);
        core.clear();
        core.add(first);
        numSeries = 1;
      }

      offsets = new long[numSeries][getImageCount()];

      int[] lengths = new int[4];
      if(!imageMetadataLVProcessed) {
        int nextChar = 2;
        for (int i = 0; i < lengths.length; i++) {
          if (i == fieldIndex) lengths[i] = core.size();
          else {
            char axis = getDimensionOrder().charAt(nextChar++);
            if (axis == 'Z') lengths[i] = getSizeZ();
            else if (axis == 'C') lengths[i] = 1;
            else if (axis == 'T') lengths[i] = getSizeT();
          }
        }
      } else
      {
        int currPos = 1;
        lengths[0] = 1;
        lengths[1] = 1;
        lengths[2] = 1;
        lengths[3] = 1;
        for (char c:
                imageMetadataLVOrder.toCharArray()) {
          if(c == 'Z')
          {
            lengths[currPos] = getSizeZ();
          }
          else if(c == 'M')
          {
            fieldIndex = currPos;
            lengths[currPos] = core.size();
          }
          else if(c == 'T')
          {
            lengths[currPos] = getSizeT();
          }
          else
          {
            currPos--;
          }
          currPos++;
        }

        if(!imageMetadataLVOrder.contains("M"))
          fieldIndex = 3;
      }
      int[] zctLengths = new int[4];
      System.arraycopy(lengths, 0, zctLengths, 0, lengths.length);
      zctLengths[fieldIndex] = 1;

      boolean oneIndexed = false;
      for (int i=0; i<imageOffsets.size(); i++) {
        long offset = imageOffsets.get(i).longValue();
        long[] p = imageLengths.get(i);
        long length = p[0] + p[1];

        if (getSizeC() == 0) {
          int sizeC = (int) (length / (getSizeX() * getSizeY() *
            FormatTools.getBytesPerPixel(getPixelType())));
          for (int q=0; q<getSeriesCount(); q++) {
            core.get(q, 0).sizeC = sizeC;
          }
        }

        String imageName = imageNames.get(i);
        int ndx = Integer.parseInt(imageName.replaceAll("\\D", ""));
        if (ndx == 1 && i == 0) {
          oneIndexed = true;
        }
        if (oneIndexed) {
          ndx--;
        }

        int[] pos = FormatTools.rasterToPosition(lengths, ndx);
        int seriesIndex = pos[fieldIndex];
        pos[fieldIndex] = 0;
        int plane = FormatTools.positionToRaster(zctLengths, pos);

        if (seriesIndex < offsets.length && plane < offsets[seriesIndex].length)
        {
          offsets[seriesIndex][plane] = offset + p[0] + 8;
        }
      }

      ArrayList<long[]> tmpOffsets = new ArrayList<long[]>();
      for (int i=0; i<offsets.length; i++) {
        if (offsets[i].length > 0 && offsets[i][0] > 0) {
          tmpOffsets.add(offsets[i]);
        }
      }

      offsets = new long[tmpOffsets.size()][];
      for (int i=0; i<tmpOffsets.size(); i++) {
        offsets[i] = tmpOffsets.get(i);
      }

      if (offsets.length != getSeriesCount()) {
        int x = getSizeX();
        int y = getSizeY();
        int c = getSizeC();
        int pixelType = getPixelType();
        int bitsPerPixel = getBitsPerPixel();
        boolean rgb = isRGB();
        String order = getDimensionOrder();
        core = new CoreMetadataList();
        for (int i=0; i<offsets.length; i++) {
          CoreMetadata ms = new CoreMetadata();
          core.add(ms);
          ms.sizeX = x;
          ms.sizeY = y;
          ms.sizeC = c == 0 ? 1 : c;
          ms.pixelType = pixelType;
          ms.bitsPerPixel = bitsPerPixel;
          ms.rgb = rgb;
          ms.sizeZ = 1;
          ms.dimensionOrder = order;

          int invalid = 0;
          for (int q=0; q<offsets[i].length; q++) {
            if (offsets[i][q] == 0) invalid++;
          }
          ms.imageCount = offsets[i].length - invalid;
          ms.sizeT = ms.imageCount / (rgb ? 1 : ms.sizeC);
          if (ms.sizeT == 0) ms.sizeT = 1;
        }
      }
      else {
        for (int i=0; i<getSeriesCount(); i++) {
          CoreMetadata ms = core.get(i, 0);
          ms.sizeX = getSizeX();
          ms.sizeY = getSizeY();
          ms.sizeC = getSizeC() == 0 ? 1 : getSizeC();
          ms.sizeZ = getSizeZ() == 0 ? 1 : getSizeZ();
          ms.sizeT = getSizeT() == 0 ? 1 : getSizeT();
          ms.imageCount = getImageCount();
          ms.pixelType = getPixelType();
          ms.bitsPerPixel = getBitsPerPixel();
          ms.dimensionOrder = getDimensionOrder();
        }
      }

      boolean hasColor = false;
      for (String ch : channelColors.keySet()) {
        Integer color = channelColors.get(ch);
        // look for a color that is neither black nor white
        if (color != 0xffffff && color != 0) {
          hasColor = true;
          break;
        }
      }

      split = getSizeC() > 1;
      for (int i=0; i<getSeriesCount(); i++) {
        CoreMetadata ms = core.get(i, 0);
        ms.rgb = false;
        ms.littleEndian = true;
        ms.interleaved = false;
        ms.indexed = channelColors.size() > 0 && hasColor;
        ms.falseColor = true;
        ms.metadataComplete = true;
        ms.imageCount = ms.sizeZ * ms.sizeT * ms.sizeC;
      }

      // read first CustomData block

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        if (customDataOffsets.size() > 0) {
          in.seek(customDataOffsets.get(0).longValue());
          long[] p = customDataLengths.get(0);
          long len = p[0] + p[1];

          int timestampBytes = imageOffsets.size() * 8;
          in.seek(in.getFilePointer() + (len - timestampBytes));

          // the acqtimecache is a undeliniated stream of doubles

          for (int series=0; series<getSeriesCount(); series++) {
            setSeries(series);
            int count = split ? getImageCount() / getSizeC() : getImageCount();
            for (int plane=0; plane<count; plane++) {
              // timestamps are stored in ms; we want them in seconds
              double time = in.readDouble() / 1000;
              tsT.add(time);
              addSeriesMetaList("timestamp", time);
            }
          }
          setSeries(0);
        }
      }

      populateMetadataStore(handler);
      return;
    }
    else in.seek(0);

    // older version of ND2 - uses JPEG 2000 compression

    isJPEG = true;

    LOGGER.info("Calculating image offsets");

    ArrayList<Long> vs = new ArrayList<Long>();

    long pos = in.getFilePointer();
    boolean lastBoxFound = false;
    int length = 0;
    int box = 0;

    // assemble offsets to each plane

    int x = 0, y = 0, c = 0, type = 0;

    while (!lastBoxFound) {
      pos = in.getFilePointer();
      length = in.readInt();
      long nextPos = pos + length;
      if (nextPos < 0 || nextPos >= in.length() || length == 0) {
        lastBoxFound = true;
      }
      box = in.readInt();
      pos = in.getFilePointer();
      length -= 8;

      if (box == 0x6a703263) {
        vs.add(pos);
      }
      else if (box == 0x6a703268) {
        in.skipBytes(4);
        String s = in.readString(4);
        if (s.equals("ihdr")) {
          y = in.readInt();
          x = in.readInt();
          c = in.readShort();
          type = in.readInt();
          if (type == 0xf070100 || type == 0xf070000) type = FormatTools.UINT16;
          else type = FormatTools.UINT8;
        }
      }
      if (!lastBoxFound && box != 0x6a703268) in.skipBytes(length);
    }

    LOGGER.info("Finding XML metadata");

    // read XML metadata from the end of the file

    in.seek(vs.get(vs.size() - 1).longValue());

    boolean found = false;
    long off = -1;
    byte[] buf = new byte[8192];
    while (!found && in.getFilePointer() < in.length()) {
      int read = 0;
      if (in.getFilePointer() == vs.get(vs.size() - 1).longValue()) {
        read = in.read(buf);
      }
      else {
        System.arraycopy(buf, buf.length - 10, buf, 0, 10);
        read = in.read(buf, 10, buf.length - 10);
      }

      if (read == buf.length) read -= 10;
      for (int i=0; i<read+9; i++) {
        if (buf[i] == (byte) 0xff && buf[i+1] == (byte) 0xd9) {
          found = true;
          off = in.getFilePointer() - (read + 10) + i;
          i = buf.length;
          break;
        }
      }
    }

    buf = null;

    LOGGER.info("Parsing XML");

    ArrayList<Long> zs = new ArrayList<Long>();
    ArrayList<Long> ts = new ArrayList<Long>();

    int numSeries = 0;
    ND2Handler handler = null;
    if (off > 0 && off < in.length() - 5 && (in.length() - off - 5) > 14) {
      in.seek(off + 4);

      StringBuilder sb = new StringBuilder();
      // stored XML doesn't have a root node - add one, so that we can parse
      // using SAX

      sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><NIKON>");

      String s = null;
      int blockLength = 0;

      while (in.getFilePointer() < in.length()) {
        blockLength = in.readShort();
        if (blockLength < 2) break;
        blockLength -= 2;
        if (blockLength + in.getFilePointer() >= in.length()) {
          blockLength = (int) (in.length() - in.getFilePointer());
        }
        s = in.readString(blockLength);
        s = s.replaceAll("<!--.+?>", ""); // remove comments
        int openBracket = s.indexOf('<');
        if (openBracket == -1) continue;
        int closedBracket = s.lastIndexOf(">") + 1;
        if (closedBracket < openBracket) continue;
        s = s.substring(openBracket, closedBracket).trim();
        if (s.indexOf("CalibrationSeq") == -1 && s.indexOf("VCAL") == -1 &&
          s.indexOf("jp2cLUNK") == -1)
        {
          sb.append(s);
        }
      }
      s = null;

      sb.append("</NIKON>");

      LOGGER.info("Finished assembling XML string");

      // strip out invalid characters
      int offset = 0;
      int len = sb.length();
      for (int i=0; i<len; i++) {
        char ch = sb.charAt(i);
        if (offset == 0 && ch == '!') offset = i + 1;

        if (Character.isISOControl(ch) || !Character.isDefined(ch)) {
          sb.setCharAt(i, ' ');
        }
      }

      core.get(0, 0).dimensionOrder = "";

      if (len - offset < offset) {
        offset = 0;
      }
      String xml = sb.substring(offset, len);
      sb = null;
      handler = new ND2Handler(core, vs.size());
      try {
        xml = XMLTools.sanitizeXML(xml);
        XMLTools.parseXML(xml, handler);
      }
      catch (IOException e) { }
      xml = null;

      isLossless = handler.isLossless();
      fieldIndex = handler.getFieldIndex();
      zs = handler.getZSections();
      ts = handler.getTimepoints();
      numSeries = handler.getSeriesCount();
      core = handler.getCoreMetadataList();
      final Map<String, Object> globalMetadata = handler.getMetadata();
      for (final Map.Entry<String, Object> entry : globalMetadata.entrySet()) {
        addGlobalMeta(entry.getKey(), entry.getValue());
      }
    }

    LOGGER.info("Populating metadata");

    core.get(0, 0).pixelType = FormatTools.UINT8;
    offsets = new long[1][2];
    offsets[0][0] = vs.get(0).longValue();
    if (offsets[0].length > 1 && vs.size() > 1) {
      offsets[0][1] = vs.get(1).longValue();
    }

    in.seek(offsets[0][0]);

    if (getSizeC() == 0) core.get(0, 0).sizeC = 1;
    int numBands = c;
    c = numBands > 1 ? numBands : getSizeC();
    if (numBands == 1 && getImageCount() == 1) c = 1;
    for (int i=0; i<getSeriesCount(); i++) {
      CoreMetadata ms = core.get(i, 0);
      ms.sizeC = c;
      ms.rgb = numBands > 1;
      ms.pixelType = type;
    }

    if (getDimensionOrder() == null) core.get(0, 0).dimensionOrder = "";

    if (getSizeC() > 1) {
      core.get(0, 0).dimensionOrder = getDimensionOrder().replaceAll("C", "");
      core.get(0, 0).dimensionOrder = "C" + getDimensionOrder();
      fieldIndex++;
    }

    if (getDimensionOrder().indexOf('Z') == -1) core.get(0, 0).dimensionOrder += 'Z';
    if (getDimensionOrder().indexOf('C') == -1) core.get(0, 0).dimensionOrder += 'C';
    if (getDimensionOrder().indexOf('T') == -1) core.get(0, 0).dimensionOrder += 'T';
    core.get(0, 0).dimensionOrder = "XY" + getDimensionOrder();

    if (getImageCount() == 0) {
      core.get(0, 0).imageCount = vs.size();
      core.get(0, 0).sizeZ = (int) Math.max(zs.size(), 1);
      core.get(0, 0).sizeT = (int) Math.max(ts.size(), 1);
      int channels = isRGB() ? 1 : getSizeC();
      if (channels * getSizeZ() * getSizeT() != getImageCount()) {
        core.get(0, 0).sizeZ = 1;
        core.get(0, 0).sizeT = getImageCount() / channels;
        core.get(0, 0).imageCount = getSizeZ() * getSizeT() * channels;
      }
    }

    if (getSizeZ() == 0) core.get(0, 0).sizeZ = 1;
    if (getSizeT() == 0) core.get(0, 0).sizeT = 1;

    for (int i=0; i<getSeriesCount(); i++) {
      CoreMetadata ms = core.get(i, 0);
      ms.sizeZ = getSizeZ();
      ms.sizeT = getSizeT();
      ms.imageCount = getSizeZ() * getSizeT() * (isRGB() ? 1 : getSizeC());
      ms.dimensionOrder = getDimensionOrder();
      ms.sizeX = x;
      ms.sizeY = y;
      ms.interleaved = false;
      ms.littleEndian = false;
      ms.metadataComplete = true;
    }

    int nplanes = getSizeZ() * getEffectiveSizeC();
    if (numSeries == 0) numSeries = 1;
    if (numSeries * nplanes * getSizeT() > vs.size()) {
      numSeries = vs.size() / (nplanes * getSizeT());
    }
    offsets = new long[numSeries][getImageCount()];

    for (int i=0; i<getSizeT(); i++) {
      for (int j=0; j<numSeries; j++) {
        for (int q=0; q<nplanes; q++) {
          offsets[j][i*nplanes + q] = vs.remove(0).longValue();
        }
      }
    }

    populateMetadataStore(handler);
  }

  // -- Helper methods --

  /**
   * Function for iterating through ND2 metaAttributes
   * @param in    stream of bytes from file
   * @param stop position where to stop
   * @return text info values, if found
   */
  private ArrayList<String> iterateIn(RandomAccessInputStream in, Long stop) {
    return iterateIn(in, stop, false);
  }

  /**
   * Function for iterating through ND2 metaAttributes
   * @param in    stream of bytes from file
   * @param stop position where to stop
   * @param minimal true if values should be read with no additional metadata parsing
   * @return text info values, if found
   */
  private ArrayList<String> iterateIn(RandomAccessInputStream in, Long stop, boolean minimal) {
    Object value; // We don't know if attribute will be int, double, string....
    Double zHigh = null, zLow = null;

    ArrayList<String> textInfos = new ArrayList<String>();

    try {
      Integer currentColor = null;
      while (in.getFilePointer() < stop) {
        long startOffset = in.getFilePointer();
        int type = in.read();        // @See switch
        int letters = in.read();        // Letters in the Attribute name

        // Attribute name
        String name = DataTools.stripString(in.readString(letters*2));

        int numberOfItems; // Number of items in level (see level)
        Long off;           // Offset to index (see level)

        switch (type) {
          case (1):  // bool
            value = in.readBoolean();
            break;
          case (2): // int32
            value = in.readInt();
            break;
          case (3): // unsigned int32
            value = in.readInt();
            break;
          case (4): // int64
            value = in.readLong();
            break;
          case (5): // unsigned int64
            value = in.readLong();
            break;
          case (6): // double
            value = in.readDouble();
            break;
          case (7): // VoidPointer
            value = in.readLong();
            break;
          case (8): // String
            char currentChar = 0;
            StringBuilder resultString = new StringBuilder();
            while((currentChar = in.readChar()) != 0) {
                resultString.append(currentChar);
            }
            value = resultString.toString();

            if (name.startsWith("TextInfoItem")) {
              textInfos.add((String) value);
            }
            break;
          case (9): // ByteArray
            long length = in.readLong();
            if (length + in.getFilePointer() > stop) {
              in.seek(stop);
              continue;
            }
            value = "ByteArray";
            if (length > 2) {
              iterateIn(in, stop);
            }
            else {
              in.skipBytes(length);
            }
            break;
          case (10): // deprecated
            // Its like LEVEL but offset is pointing absolutely not relatively

            numberOfItems = in.readInt();   // number of level atributes

            // offset to index (absolute number - current position -
            //   numberOfItems(4B) - type (1B) - nameLength (1B) - name
            off = in.readLong() - in.getFilePointer() - 4 - 2 - letters * 2;
            value = "LEVEL";

            // Iterate
            iterateIn(in, off + in.getFilePointer());

            // Last 4 bytes in level is some kind of point table

            if (off < 0) {
              break;
            }
            in.seek(off + in.getFilePointer() + numberOfItems * 8);
            value = in.readLong();
            break;
          case (11): // level
            numberOfItems = in.readInt(); // number of level attributes
            off = in.readLong();         // offset to index
            value = "LEVEL";

            // Iterate
            long endOffset = off + startOffset;
            iterateIn(in, endOffset);

            // Last 4 bytes in level is some kind of point table
            /* ***** Index is pointer. NumberofItemes*8B ***** */

            in.seek(endOffset + numberOfItems * 8);
            break;
          default: // Shall not happen :-)
            continue;
        }

        if (minimal) {
          continue;
        }

        name = name.trim();

        if (name.equals("bUseZ")) {
          useZ = new Boolean(value.toString());
        }
        else if (name.equals("sDescription")) {
          if (currentColor != null) {
            textChannelNames.add(value.toString());
            channelColors.put(value.toString(), currentColor);
          }
        }
        else if (name.equals("uiColor")) {
          currentColor = (Integer) value;
        }
        else if (name.equals("dExposureTime")) {
          if ((Double) value > 0) {
            exposureTime.add(((Double) value) / 1000);
          }
        }
        else if (name.equals("EmWavelength")) {
          Double wave = Double.parseDouble(value.toString());
          textEmissionWavelengths.add(wave);
        }
        else if (name.equals("ExWavelength")) {
          Double wave = DataTools.parseDouble(value.toString());
          textExcitationWavelengths.add(wave);
        }
        else if (name.equals("dZStep")) {
          trueSizeZ = new Double(value.toString());
        }
        else if (name.equals("dZHigh")) {
          zHigh = DataTools.parseDouble(value.toString());
        }
        else if (name.equals("dZLow")) {
          zLow = DataTools.parseDouble(value.toString());
        }
        else if (name.equals("dPosX")) {
          positionCount++;
        }
        else if (name.equals("dObjectiveMag")) {
          Double mag = DataTools.parseDouble(value.toString());
          if (mag != null && mag > 0) {
            objectiveMag = mag;
          }
        }
        else if (name.equals("sObjective")) {
          objectiveModel = value.toString();
        }

        if (type != 11 && type != 10 && type != 9) {    // if not level add global meta
          addGlobalMeta(name, value);
        }
      }
    }
    catch (Exception e) {
      LOGGER.debug("", e);
    }

    if (zHigh != null && zLow != null && trueSizeZ != null && trueSizeZ > 0) {
      core.get(0, 0).sizeZ = (int) (Math.ceil(Math.abs(zHigh - zLow) / trueSizeZ)) + 1;
    }

    return textInfos;
  }

  private void populateMetadataStore(ND2Handler handler) throws FormatException
  {
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String filename = new Location(getCurrentFile()).getName();
    if (handler != null) {
      ArrayList<String> posNames = handler.getPositionNames();
      int nameWidth = String.valueOf(getSeriesCount()).length();
      for (int i=0; i<getSeriesCount(); i++) {
        String seriesSuffix = String.format("(series %0" + nameWidth + "d)", i + 1);
        String suffix = (i < posNames.size() && !posNames.get(i).equals("")) ?
          posNames.get(i) : seriesSuffix;
        String name = filename + " " + suffix;
        store.setImageName(name.trim(), i);
      }
    }

    colors = new int[getEffectiveSizeC()];

    ArrayList<String> channelNames = null;
    if (handler != null) {
      channelNames = handler.getChannelNames();
      if (channelNames.size() < getEffectiveSizeC() && backupHandler != null) {
        channelNames = backupHandler.getChannelNames();
      }
      if (channelNames.size() < getEffectiveSizeC()) {
        channelNames = textChannelNames;
      }
      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (c < channelNames.size()) {
          String channelName = channelNames.get(c);
          Integer channelColor = channelColors.get(channelName);
          colors[c] = channelColor == null ? 0 : channelColor.intValue();
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);

      // set the channel color
      for (int c=0; c<getEffectiveSizeC(); c++) {
        int red = colors[c] & 0xff;
        int green = (colors[c] & 0xff00) >> 8;
        int blue = (colors[c] & 0xff0000) >> 16;

        // do not set the channel color if the recorded color is black
        // doing so can prevent the image from displaying correctly
        if (red != 0 || green != 0 || blue != 0) {
          // always set the alpha to 255, otherwise the colors may not appear
          store.setChannelColor(new Color(red, green, blue, 255), i, c);
        }
      }
    }

    // populate Dimensions data
    if (handler != null) {
      for (int i=0; i<getSeriesCount(); i++) {
        double sizeX = handler.getPixelSizeX();
        double sizeY = handler.getPixelSizeY();
        double sizeZ = handler.getPixelSizeZ();

        if (trueSizeX > 0) {
          store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(trueSizeX), i);
        }
        else {
          Length size = FormatTools.getPhysicalSizeX(sizeX);
          if (size != null) {
            store.setPixelsPhysicalSizeX(size, i);
          }
        }
        if (trueSizeY > 0) {
          store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(trueSizeY), i);
        }
        else if (trueSizeX > 0) {
          // if the X size is set, assume X and Y are equal
          store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(trueSizeX), i);
        }
        else {
          Length size = FormatTools.getPhysicalSizeY(sizeY);
          if (size == null) {
            // if the X size is set, assume X and Y are equal
            size = FormatTools.getPhysicalSizeY(sizeX);
          }
          if (size != null) {
            store.setPixelsPhysicalSizeY(size, i);
          }
        }
        if (trueSizeZ != null && trueSizeZ > 0) {
          store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(trueSizeZ), i);
        }
        else {
          Length size = FormatTools.getPhysicalSizeZ(sizeZ);
          if (size != null) {
            store.setPixelsPhysicalSizeZ(size, i);
          }
        }
      }
    }

    // populate PlaneTiming and StagePosition data

    // potentially 3 separate sets of exposure times
    // attempt to sort out which one is most correct for OME-XML
    // but also supply all 3 lists via global metadata table

    for (Double time : exposureTime) {
      addGlobalMetaList("Exposure time (text)", time);
    }
    boolean validHandler = handler != null && handler.getExposureTimes().size() > 0;
    if (validHandler) {
      for (Double time : handler.getExposureTimes()) {
        addGlobalMetaList("Exposure time (primary XML)", time);
      }
    }
    if (backupHandler != null) {
      for (Double time : backupHandler.getExposureTimes()) {
        addGlobalMetaList("Exposure time (secondary XML)", time);
      }
    }

    if (validHandler && (exposureTime.size() == 0 || handler.getExposureTimes().size() == 1 || exposureTime.size() % getSizeC() != 0)) {
      exposureTime = handler.getExposureTimes();
      if (backupHandler != null && backupHandler.getExposureTimes().size() > exposureTime.size()) {
        exposureTime = backupHandler.getExposureTimes();
      }
    }
    else if (validHandler && (backupHandler == null || backupHandler.getExposureTimes().size() == 0)) {
      exposureTime = handler.getExposureTimes();
    }
    else if (backupHandler != null && backupHandler.getExposureTimes().size() % exposureTime.size() == 0) {
      exposureTime = backupHandler.getExposureTimes();
    }
    int zcPlanes = getImageCount() / ((split ? getSizeC() : 1) * getSizeT());
    for (int i=0; i<getSeriesCount(); i++) {
      if (tsT.size() > 0) {
        setSeries(i);
        for (int n=0; n<getImageCount(); n++) {
          int[] coords = getZCTCoords(n);
          int stampIndex = coords[0];
          if (!split) {
            stampIndex = getIndex(coords[0], coords[1], 0);
          }
          stampIndex += (coords[2] * getSeriesCount() + i) * zcPlanes;
          if (tsT.size() == getImageCount()) stampIndex = n;
          else if (tsT.size() == getSizeZ()) {
            stampIndex = coords[0];
          }
          if (stampIndex < tsT.size()) {
            double stamp = tsT.get(stampIndex).doubleValue();
            store.setPlaneDeltaT(new Time(stamp, UNITS.SECOND), i, n);
          }

          int index = i * getSizeC() + coords[1];
          if (exposureTime.size() >= getSizeC() && exposureTime.size() < getSizeC() * getSeriesCount()) {
            index = coords[1];
          }
          else if (exposureTime.size() == 1) {
            // if exposure times are the same for all channels,
            // then sometimes only one value is stored
            index = 0;
          }
          if (exposureTime != null && index < exposureTime.size() && exposureTime.get(index) != null) {
            store.setPlaneExposureTime(new Time(exposureTime.get(index), UNITS.SECOND), i, n);
          }
        }
      }

      if (handler != null) {
        if (posX == null) posX = handler.getXPositions();
        if (posY == null) posY = handler.getYPositions();
        if (posZ == null) posZ = handler.getZPositions();
      }

      String pos = "for position";
      for (int n=0; n<getImageCount(); n++) {
        int[] coords = getZCTCoords(n);
        int index = coords[0];
        index += (coords[2] * getSeriesCount() + i) * zcPlanes;
        if (posX != null) {
          if (index >= posX.size()) index = i;
          if (index < posX.size()) {
            String key = "X position ";
            store.setPlanePositionX(posX.get(index), i, n);
            addSeriesMetaList(key, posX.get(index));
            addGlobalMetaList(key + pos, posX.get(index));
          }
        }
        if (posY != null) {
          if (index < posY.size()) {
            String key = "Y position ";
            store.setPlanePositionY(posY.get(index), i, n);
            addSeriesMetaList(key, posY.get(index));
            addGlobalMetaList(key + pos, posY.get(index));
          }
        }
        if (posZ != null) {
          if (index < posZ.size()) {
            store.setPlanePositionZ(posZ.get(index), i, n);
            String key = "Z position " + pos + ", plane";
            addSeriesMetaList(key, posZ.get(index));
            addGlobalMetaList(key, posZ.get(index));
          }
        }
      }
    }

    if (handler == null) {
      setSeries(0);
      return;
    }

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorModel(handler.getCameraModel(), 0, 0);
    store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, 0);

    ArrayList<String> modality = handler.getModalities();
    ArrayList<String> binning = handler.getBinnings();
    ArrayList<Double> speed = handler.getSpeeds();
    ArrayList<Double> gain = handler.getGains();
    ArrayList<Double> temperature = handler.getTemperatures();
    ArrayList<Double> exWave = handler.getExcitationWavelengths();
    ArrayList<Double> emWave = handler.getEmissionWavelengths();
    ArrayList<Integer> power = handler.getPowers();
    ArrayList<Hashtable<String, String>> rois = handler.getROIs();

    if (backupHandler != null) {
      if (emWave.size() == 0) {
        emWave = backupHandler.getEmissionWavelengths();
      }
      if (exWave.size() == 0) {
        exWave = backupHandler.getExcitationWavelengths();
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      for (int c=0; c<getEffectiveSizeC(); c++) {
        int index = c;

        Double pinholeSize = handler.getPinholeSize();
        if (pinholeSize != null) {
          store.setChannelPinholeSize(new Length(pinholeSize, UNITS.MICROMETER), i, c);
        }
        if (index < channelNames.size()) {
          String channelName = channelNames.get(index);
          store.setChannelName(channelName, i, c);
        }
        else if (channelNames.size() >= getEffectiveSizeC()) {
          store.setChannelName(channelNames.get(c), i, c);
        }
        if (index < modality.size()) {
          store.setChannelAcquisitionMode(
            MetadataTools.getAcquisitionMode(modality.get(index)), i, c);
        }
        if (index < emWave.size() || index < textEmissionWavelengths.size()) {
          Double value = index < emWave.size() ? emWave.get(index) :
            textEmissionWavelengths.get(index);
          Length emission = FormatTools.getEmissionWavelength(value);
          if (emission != null) {
            store.setChannelEmissionWavelength(emission, i, c);
          }
        }
        else if (emWave.size() > 0 || textEmissionWavelengths.size() > 0) {
          store.setChannelColor(new Color(255, 255, 255, 255), i, c);
        }
        if (index < exWave.size() || index < textExcitationWavelengths.size()) {
          Double value = index < exWave.size() ? exWave.get(index) :
            textExcitationWavelengths.get(index);
          Length excitation = FormatTools.getExcitationWavelength(value);
          if (excitation != null) {
            store.setChannelExcitationWavelength(excitation, i, c);
          }
        }
        if (index < binning.size()) {
          store.setDetectorSettingsBinning(
            MetadataTools.getBinning(binning.get(index)), i, c);
        }
        if (index < gain.size()) {
          store.setDetectorSettingsGain(gain.get(index), i, c);
        }
        if (index < speed.size()) {
          store.setDetectorSettingsReadOutRate(
                  new Frequency(speed.get(index), UNITS.HERTZ), i, c);
        }
        store.setDetectorSettingsID(detectorID, i, c);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (i * getSizeC() < temperature.size()) {
        Double temp = temperature.get(i * getSizeC());
        store.setImagingEnvironmentTemperature(
                new Temperature(temp, UNITS.CELSIUS), i);
      }
    }

    // populate DetectorSettings
    Double voltage = handler.getVoltage();
    if (voltage != null) {
      store.setDetectorSettingsVoltage(
              new ElectricPotential(voltage, UNITS.VOLT), 0, 0);
    }

    // populate Objective
    if (lensNA == null) {
      lensNA = handler.getNumericalAperture();
    }
    if (lensNA != null) {
      store.setObjectiveLensNA(lensNA, 0, 0);
    }
    if (objectiveMag == null) {
      objectiveMag = handler.getMagnification();
    }
    if (objectiveMag != null) {
      store.setObjectiveCalibratedMagnification(objectiveMag, 0, 0);
    }
    if (objectiveModel == null) {
      objectiveModel = handler.getObjectiveModel();
    }
    store.setObjectiveModel(objectiveModel, 0, 0);

    String immersion = handler.getImmersion();
    if (immersion == null) immersion = "Other";
    store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), 0, 0);

    String correction = handler.getCorrection();
    if (correction == null || correction.length() == 0) correction = "Other";
    store.setObjectiveCorrection(MetadataTools.getCorrection(correction), 0, 0);

    // link Objective to Image
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    if (refractiveIndex == null) {
      refractiveIndex = handler.getRefractiveIndex();
    }

    for (int i=0; i<getSeriesCount(); i++) {
      store.setObjectiveSettingsID(objectiveID, i);
      if (refractiveIndex != null) {
        store.setObjectiveSettingsRefractiveIndex(refractiveIndex, i);
      }
    }

    setSeries(0);

    // populate ROI data

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.NO_OVERLAYS) {
      return;
    }
    handler.populateROIs(store);
  }

  private Codec createCodec(boolean isJPEG) {
    return isJPEG ? new JPEG2000Codec() : new ZlibCodec();
  }

  private void copyPixels(int x, int y, int w, int h, int bpp, int scanlinePad,
    byte[] pix, byte[] buf, boolean split)
    throws IOException
  {
    if (split) {
      pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(), bpp,
        false, true);
    }
    RandomAccessInputStream s = new RandomAccessInputStream(pix);
    readPlane(s, x, y, w, h, scanlinePad, buf);
    s.close();
  }

  /** Remove control and invalid characters from the given string. */
  public static String sanitizeControl(String s) {
    final char[] c = s.toCharArray();
    for (int i=0; i<s.length(); i++) {
      if (Character.isISOControl(c[i]) ||
        !Character.isDefined(c[i]))
      {
        c[i] = ' ';
      }
    }
    return new String(c);
  }

  private int getScanlinePad() {
    if (getSizeX() % 2 == 0) return 0;
    if (getSizeC() % 2 == 0) return 0;
    return 1;
  }

  private void parseText(String textString, int offsetCount, boolean useDimensions) {
    try {
      ND2Handler handler = new ND2Handler(core, offsetCount);
      String xmlString = XMLTools.sanitizeXML(textString);
      int start = xmlString.indexOf('<');
      int end = xmlString.lastIndexOf(">");
      if (start >= 0 && end >= 0 && end >= start) {
        xmlString = xmlString.substring(start, end + 1);
      }

      XMLTools.parseXML(xmlString, handler);
      xmlString = null;
      textString = null;
      core = handler.getCoreMetadataList();
      if (backupHandler == null ||
        backupHandler.getChannelNames().size() == 0)
      {
        backupHandler = handler;
      }

      final Map<String, Object> globalMetadata = handler.getMetadata();
      for (final Map.Entry<String, Object> entry : globalMetadata.entrySet()) {
        addGlobalMeta(entry.getKey(), entry.getValue());
      }
    }
    catch (IOException e) {
      LOGGER.debug("Could not parse XML", e);

      String[] lines = textString.split("\n");
      ND2Handler handler = new ND2Handler(core, offsetCount);
      handler.imageMetadataLVExists = imageMetadataLVProcessed;
      for (String line : lines) {
        int separator = line.indexOf(':');
        if (separator >= 0) {
          String key = line.substring(0, separator).trim();
          String value = line.substring(separator + 1).trim();

          if (useDimensions) {
            handler.parseKeyAndValue(key, value, null);
          }

          if (handler.isDimensions(key)) {
            textData = true;
          }
        }
      }
      if (useDimensions) {
        core = handler.getCoreMetadataList();
        if (backupHandler == null ||
          backupHandler.getChannelNames().size() == 0)
        {
          backupHandler = handler;
        }
      }

      // only accept the Z and T sizes from the text annotations
      // if both values were set
      if (core.get(0, 0).sizeZ == 0 && getSizeT() != offsetCount) {
        core.get(0, 0).sizeT = 0;
      }

      textString = sanitizeControl(textString);

      lines = textString.split(" ");
      for (int i=0; i<lines.length; i++) {
        StringBuilder sb = new StringBuilder(lines[i++]);
        while ((sb.length() == 0 || sb.charAt(sb.length() - 1) != ':') && sb.indexOf("_") < 0 && i < lines.length) {
          sb.append(" ");
          sb.append(lines[i++]);
          if (i >= lines.length) {
            break;
          }
        }

        if (i >= lines.length) {
          break;
        }
        String key = sb.toString();

        sb.setLength(0);
        sb.append(lines[i++]);
        while (i < lines.length && lines[i].trim().length() > 0) {
          sb.append(' ');
          sb.append(lines[i++]);
          if (i >= lines.length) {
            break;
          }
        }

        key = key.trim();
        key = key.substring(0, key.length() - 1);
        String value = sb.toString().trim();

        if (key.startsWith("- Step")) {
          int end = key.indexOf(" ", 8);
          if (end < 0) {
            end = key.length();
          }
          if (end >= 8) {
            value = key.substring(8, end);
            key = key.substring(0, 8);
          }
          if (trueSizeZ == null) {
            trueSizeZ = DataTools.parseDouble(value);
          }
        }
        else if (key.equals("Name")) {
          textChannelNames.add(value);
        }
        else if (key.startsWith("Line:")) {
          if (value.endsWith("Active")) {
            int first = key.lastIndexOf(":") + 1;
            int last = key.lastIndexOf(";");
            if (last-first < 0){
                last = first + key.substring(first).indexOf(' ');
            }
            Double wavelength = DataTools.parseDouble(key.substring(
              first, last).trim()) + 20;
            if (wavelength != null) textEmissionWavelengths.add(wavelength);
          }
        }
        else if (key.equals("Refractive Index")) {
          refractiveIndex = DataTools.parseDouble(value);
        }
        else if (key.equals("Numerical Aperture")) {
          lensNA = DataTools.parseDouble(value);
        }

        if (metadata.containsKey(key)) {
          Object oldValue = metadata.get(key);
          metadata.put(key + " #1", oldValue);
          metadata.put(key + " #2", value);
          metadata.remove(key);
        }
        else if (metadata.containsKey(key + " #1")) {
          int index = 1;
          while (metadata.containsKey(key + " #" + index)) {
            index++;
          }
          metadata.put(key + " #" + index, value);
        }
        else {
          metadata.put(key, value);
        }
      }
    }
  }

  private void setDimensions(int numT, int numZ, int numSeries) {
    CoreMetadata ms0 = core.get(0, 0);
    ms0.sizeT = numT;
    ms0.sizeZ = numZ;
    if (numSeries > 1) {
      int x = ms0.sizeX;
      int y = ms0.sizeY;
      int z = ms0.sizeZ;
      int tSize = ms0.sizeT;
      int c = ms0.sizeC;
      String order = ms0.dimensionOrder;
      core = new CoreMetadataList();
      for (int i=0; i<numSeries; i++) {
        CoreMetadata ms = new CoreMetadata();
        core.add(ms);
        ms.sizeX = x;
        ms.sizeY = y;
        ms.sizeZ = z == 0 ? 1 : z;
        ms.sizeC = c == 0 ? 1 : c;
        ms.sizeT = tSize == 0 ? 1 : tSize;
        ms.dimensionOrder = order;
      }
      ms0 = core.get(0, 0);
    }


    ms0.imageCount = ms0.sizeZ * ms0.sizeC * ms0.sizeT;
  }

}
