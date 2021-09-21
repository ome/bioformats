/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2021 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.SubResolutionFormatReader;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.PackbitsCodec;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;
import ome.units.UNITS;

import static loci.formats.in.DicomAttribute.*;
import static loci.formats.in.DicomVR.*;

/**
 * DicomReader is the file format reader for DICOM files.
 */
public class DicomReader extends SubResolutionFormatReader {

  // -- Constants --

  public static final String DICOM_MAGIC_STRING = "DICM";
  public static final int HEADER_LENGTH = 128;

  private static final String[] DICOM_SUFFIXES = {
    "dic", "dcm", "dicom", "j2ki", "j2kr"
  };

  // -- Fields --

  private byte[][] lut;
  private short[][] shortLut;
  private int maxPixelRange;
  private int centerPixelValue;

  private boolean inverted;

  private String date, time, imageType;
  private String pixelSizeX, pixelSizeY;
  private Double pixelSizeZ;
  private List<Double> positionX = new ArrayList<Double>();
  private List<Double> positionY = new ArrayList<Double>();
  private List<Double> positionZ = new ArrayList<Double>();
  private List<String> channelNames = new ArrayList<String>();

  private boolean isJPEG = false;
  private boolean isRLE = false;
  private boolean isJP2K = false;
  private boolean isDeflate = false;

  private Map<Integer, List<String>> fileList;
  private int imagesPerFile;

  private String instanceUID;
  private String originalDate, originalTime, originalInstance;
  private int originalSeries;
  private int originalX, originalY;
  private String originalSpecimen;

  private List<String> companionFiles = new ArrayList<String>();

  private Map<Integer, List<DicomTile>> tilePositions;
  private Map<Integer, List<Double>> zOffsets;
  private Number concatenationNumber = null;
  private boolean edf = false;

  private List<DicomTag> tags;

  // -- Constructor --

  /** Constructs a new DICOM reader. */
  // "Digital Imaging and Communications in Medicine" is nasty long.
  public DicomReader() {
    super("DICOM", new String[] {
      "dic", "dcm", "dicom", "jp2", "j2ki", "j2kr", "raw", "ima"});
    suffixNecessary = false;
    suffixSufficient = false;
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    datasetDescription = "One or more .dcm or .dicom files";
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    // extension is sufficient as long as it is DIC, DCM, DICOM, J2KI, or J2KR
    if (checkSuffix(name, DICOM_SUFFIXES)) return true;
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1024;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;

    stream.seek(HEADER_LENGTH);
    if (stream.readString(4).equals(DICOM_MAGIC_STRING)) return true;
    stream.seek(0);

    try {
      DicomTag tag = new DicomTag(stream, false, 0, false);
      return tag.attribute != null && tag.attribute != INVALID;
    }
    catch (NullPointerException e) { }
    catch (FormatException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files) throws FormatException, IOException {
    for (String file : files) {
      if (file.endsWith("DICOMDIR")) {
        return 1;
      }
    }
    return super.getRequiredDirectories(files);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.INT8 &&
      getPixelType() != FormatTools.UINT8)
    {
      return null;
    }
    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.INT16 &&
      getPixelType() != FormatTools.UINT16)
   {
      return null;
   }
    return shortLut;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels || fileList == null) return null;
    ArrayList<String> uniqueFiles = new ArrayList<String>();
    uniqueFiles.add(new Location(currentId).getAbsolutePath());
    if (tilePositions != null) {
      for (List<DicomTile> tilePos : tilePositions.values()) {
        for (DicomTile t : tilePos) {
          if (!uniqueFiles.contains(t.file)) {
            uniqueFiles.add(t.file);
          }
        }
      }
    }
    for (String f : companionFiles) {
      if (!uniqueFiles.contains(f)) {
        uniqueFiles.add(f);
      }
    }
    return uniqueFiles.toArray(new String[uniqueFiles.size()]);
  }

  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return CAN_GROUP;
  }

  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    if (originalX < getSizeX() && originalX > 0) {
      return originalX;
    }
    if (tilePositions.containsKey(getCoreIndex())) {
      return tilePositions.get(getCoreIndex()).get(0).region.width;
    }
    return super.getOptimalTileWidth();
  }

  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    if (originalY < getSizeY() && originalY > 0) {
      return originalY;
    }
    if (tilePositions.containsKey(getCoreIndex())) {
      return tilePositions.get(getCoreIndex()).get(0).region.height;
    }
    return super.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    Region currentRegion = new Region(x, y, w, h);
    int z = getZCTCoords(no)[0];
    int c = getZCTCoords(no)[1];

    if (!tilePositions.containsKey(getCoreIndex())) {
      LOGGER.warn("No tiles for core index = {}", getCoreIndex());
      return buf;
    }

    // look for any tiles that match the requested tile and plane
    List<Double> zs = zOffsets.get(getCoreIndex());
    List<DicomTile> tiles = tilePositions.get(getCoreIndex());
    for (int t=0; t<tiles.size(); t++) {
      DicomTile tile = tiles.get(t);
      if ((getSizeZ() == 1 || (getSizeZ() <= zs.size() && tile.zOffset.equals(zs.get(z))) || (getSizeZ() == tiles.size() && t == z)) &&
        (tile.channel == c || getEffectiveSizeC() == 1) &&
        tile.region.intersects(currentRegion))
      {
        byte[] tileBuf = new byte[tile.region.width * tile.region.height * pixel];
        Region intersection = tile.region.intersection(currentRegion);
        getTile(tile, tileBuf, intersection.x - tile.region.x, intersection.y - tile.region.y,
          intersection.width, intersection.height);

        for (int row=0; row<intersection.height; row++) {
          int srcIndex = row * intersection.width * pixel;
          int destIndex = pixel * ((intersection.y - y + row) * w + (intersection.x - x));
          System.arraycopy(tileBuf, srcIndex, buf, destIndex, intersection.width * pixel);
        }
      }
    }

    if (inverted) {
      // pixels are stored such that white -> 0; invert the values so that
      // white -> 255 (or 65535)
      if (bpp == 1) {
        for (int i=0; i<buf.length; i++) {
          buf[i] = (byte) (255 - buf[i]);
        }
      }
      else if (bpp == 2) {
        long maxPixelValue = maxPixelRange + (centerPixelValue/2);
        if (maxPixelRange == -1 || centerPixelValue < (maxPixelRange/2)) {
          maxPixelValue = FormatTools.defaultMinMax(getPixelType())[1];
        }
        boolean little = isLittleEndian();
        for (int i=0; i<buf.length; i+=2) {
          short s = DataTools.bytesToShort(buf, i, 2, little);
          DataTools.unpackBytes(maxPixelValue - s, buf, i, 2, little);
        }
      }
    }

    // NB: do *not* apply the rescale function

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      isJPEG = false;
      isRLE = false;
      isJP2K = false;
      isDeflate = false;
      lut = null;
      shortLut = null;
      maxPixelRange = 0;
      centerPixelValue = 0;
      pixelSizeX = pixelSizeY = null;
      pixelSizeZ = null;
      imagesPerFile = 0;
      fileList = null;
      inverted = false;
      date = time = imageType = null;
      originalDate = originalTime = originalInstance = null;
      originalSpecimen = null;
      instanceUID = null;
      originalSeries = 0;
      originalX = 0;
      originalY = 0;
      companionFiles.clear();
      positionX.clear();
      positionY.clear();
      positionZ.clear();
      tilePositions = null;
      zOffsets = null;
      concatenationNumber = null;
      edf = false;
      tags = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(id);
    in.order(true);
    CoreMetadata m = core.get(0, 0);

    // look for companion files
    attachCompanionFiles();

    m.littleEndian = true;
    long location = 0;
    boolean bigEndianTransferSyntax = false;
    boolean oddLocations = false;
    int bitsPerPixel = 0;
    lut = null;
    inverted = false;

    // some DICOM files have a 128 byte header followed by a 4 byte identifier

    LOGGER.info("Verifying DICOM format");
    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    in.seek(HEADER_LENGTH);
    if (in.readString(4).equals("DICM")) {
      if (level != MetadataLevel.MINIMUM) {
        // header exists, so we'll read it
        in.seek(0);
        String header = in.readString(HEADER_LENGTH).trim();
        // ignore the header if it has TIFF magic bytes
        if (!header.startsWith("II") && !header.startsWith("MM")) {
          addSeriesMeta("Header information", header);
        }
        in.skipBytes(4);
      }
      location = HEADER_LENGTH;
    }
    else in.seek(0);

    LOGGER.info("Reading tags");

    long baseOffset = 0;

    boolean decodingTags = true;
    boolean signed = false;
    String currentType = "";
    boolean tiledFull = false;

    Double fileZOffset = null;

    tags = new ArrayList<DicomTag>();
    int frameOffsetNumber = 0;
    int opticalChannels = 0;

    while (decodingTags) {
      if (in.getFilePointer() + 4 >= in.length()) {
        break;
      }
      LOGGER.debug("Reading tag from {}", in.getFilePointer());
      DicomTag tag = new DicomTag(in, bigEndianTransferSyntax, location, oddLocations);
      LOGGER.debug("  {}", tag);
      tags.add(tag);

      oddLocations = (location & 1) != 0;

      addInfo(tag);

      if (tag.attribute == null) {
        in.seek(tag.getEndPointer());
        continue;
      }

      switch (tag.attribute) {
        case TRANSFER_SYNTAX_UID:
          // this tag can indicate which compression scheme is used
          String uid = tag.getStringValue();
          isJP2K = uid.startsWith("1.2.840.10008.1.2.4.9");
          isJPEG = !isJP2K && uid.startsWith("1.2.840.10008.1.2.4");
          isRLE = uid.startsWith("1.2.840.10008.1.2.5");
          isDeflate = uid.startsWith("1.2.8.10008.1.2.1.99");
          if ((!isJP2K && !isJPEG && !isRLE && !isDeflate) &&
            (uid.indexOf("1.2.4") > -1 || uid.indexOf("1.2.5") > -1))
          {
            throw new UnsupportedCompressionException(
              "Sorry, compression type " + uid + " not supported");
          }
          if (uid.indexOf("1.2.840.10008.1.2.2") >= 0) {
            bigEndianTransferSyntax = true;
          }
          break;
        case NUMBER_OF_FRAMES:
          double frames = tag.getNumberValue().doubleValue();
          if (frames > 1.0 && frames > imagesPerFile) {
            imagesPerFile = (int) frames;
          }
          // this shouldn't take precedence over TOTAL_PIXEL_MATRIX_FOCAL_PLANES
          // if both are present
          if (m.sizeZ == 0) {
            m.sizeZ = imagesPerFile;
          }
          break;
        case SLICE_LOCATION:
          fileZOffset = tag.getNumberValue().doubleValue();
          break;
        case PLANAR_CONFIGURATION:
          m.interleaved = tag.getNumberValue().intValue() == 0;
          break;
        case ROWS:
          int y = tag.getNumberValue().intValue();
          if (y > getSizeY()) {
            m.sizeY = y;
            originalY = y;
          }
          break;
        case COLUMNS:
          int x = tag.getNumberValue().intValue();
          if (x > getSizeX()) {
            m.sizeX = x;
            originalX = x;
          }
          break;
        case TOTAL_PIXEL_MATRIX_COLUMNS:
          int mx = tag.getNumberValue().intValue();
          if (m.sizeX == originalX) {
            m.sizeX = mx;
            if (m.sizeZ == imagesPerFile) {
              m.sizeZ = 1;
            }
          }
          break;
        case TOTAL_PIXEL_MATRIX_ROWS:
          int my = tag.getNumberValue().intValue();
          if (m.sizeY == originalY) {
            m.sizeY = my;
          }
          break;
        case TOTAL_PIXEL_MATRIX_FOCAL_PLANES:
          m.sizeZ = tag.getNumberValue().intValue();
          break;
        case WINDOW_CENTER:
          if (tag.getStringValue().isEmpty() || tag.getNumberValue() == null) {
            centerPixelValue = -1;
          }
          else {
            centerPixelValue = tag.getNumberValue().intValue();
          }
          break;
        case BITS_ALLOCATED:
          if (bitsPerPixel == 0) {
            bitsPerPixel = tag.getNumberValue().intValue();
          }
          break;
        case PIXEL_REPRESENTATION:
        case PIXEL_SIGN:
          signed = tag.getNumberValue().intValue() == 1;
          break;
        case WINDOW_WIDTH:
          if (tag.getStringValue().isEmpty() || tag.getNumberValue() == null) {
            maxPixelRange = -1;
          }
          else {
            maxPixelRange = tag.getNumberValue().intValue();
          }
          break;
        case PIXEL_DATA:
        case ITEM:
        case INVALID_PIXEL_DATA:
          if (tag.getValueStartPointer() <= tag.getEndPointer()) {
            baseOffset = tag.getValueStartPointer();
            decodingTags = tag.getEndPointer() < in.length() && !isRLE && !isJPEG && !isJP2K;
          }
          break;
        case VARIABLE_PIXEL_DATA:
          if (tag.getValueStartPointer() <= tag.getEndPointer()) {
            baseOffset = location + 4;
            decodingTags = false;
          }
          break;
        case INVALID:
          in.seek(tag.getValueStartPointer() - 4);
          break;
        case DIRECTORY_RECORD_TYPE:
          currentType = tag.getStringValue();
          break;
        case DIRECTORY_RECORD_SEQUENCE:
          for (DicomTag child : tag.children) {
            if (child.attribute == REFERENCED_FILE_ID) {
              handleReferencedFile(child, currentType);
              currentType = "";
            }
            else if (child.attribute == DIRECTORY_RECORD_TYPE) {
              currentType = child.getStringValue();
            }
          }
          break;
        case REFERENCED_FILE_ID:
          handleReferencedFile(tag, currentType);
          currentType = "";
          break;
        case DIMENSION_ORGANIZATION_TYPE:
          tiledFull = tag.getStringValue().equals("TILED_FULL");
          break;
        case PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE:
          int channel = 0;
          for (DicomTag child : tag.children) {
            if (child.attribute == OPTICAL_PATH_ID_SEQUENCE) {
              DicomTag opticalPath = child.lookupChild(OPTICAL_PATH_ID);
              if (opticalPath != null && opticalPath.getStringValue() != null) {
                String v = opticalPath.getStringValue().replace("#", "");
                channel = Integer.parseInt(v);
              }
            }
            else if (child.attribute == PLANE_POSITION_SLIDE_SEQUENCE) {
              if (tilePositions == null) {
                tilePositions = new HashMap<Integer, List<DicomTile>>();
                tilePositions.put(0, new ArrayList<DicomTile>());
              }
              int col = -1;
              int row = -1;
              Double zOffset = null;
              for (DicomTag position : child.children) {
                // positions are indexed from 1, not 0
                // values are pixel coordinates, not tile indexes
                if (position.attribute == ROW_POSITION_IN_MATRIX) {
                  row = position.getNumberValue().intValue() - 1;
                }
                else if (position.attribute == COLUMN_POSITION_IN_MATRIX) {
                  col = position.getNumberValue().intValue() - 1;
                }
                else if (position.attribute == Z_OFFSET_IN_SLIDE) {
                  zOffset = position.getNumberValue().doubleValue();
                }
              }

              if (col >= 0 && row >= 0) {
                DicomTile tile = new DicomTile();
                tile.region = new Region(col, row, originalX, originalY);
                tile.file = new Location(currentId).getAbsolutePath();
                tile.zOffset = zOffset;
                tile.channel = channel;
                tile.isRLE = isRLE;
                tile.isJPEG = isJPEG;
                tile.isJP2K = isJP2K;
                tile.isDeflate = isDeflate;
                tilePositions.get(0).add(tile);
              }
            }
          }
          break;
        case SHARED_FUNCTIONAL_GROUPS_SEQUENCE:
          DicomTag pixelSequence = tag.lookupChild(PIXEL_MEASURES_SEQUENCE);
          if (pixelSequence != null) {
            DicomTag pixelSpacing = pixelSequence.lookupChild(PIXEL_SPACING);
            if (pixelSpacing != null) {
              parsePixelSpacing(pixelSpacing.getStringValue());
            }
          }

          break;
        case SPECIMEN_DESCRIPTION_SEQUENCE:
          DicomTag specimenID = tag.lookupChild(SPECIMEN_ID);
          if (specimenID != null) {
            originalSpecimen = specimenID.getStringValue();
          }
          break;
        case IN_CONCATENATION_NUMBER:
          concatenationNumber = tag.getNumberValue();
          break;
        case CONCATENATION_FRAME_OFFSET_NUMBER:
          if (tag.getNumberValue() != null) {
            frameOffsetNumber = tag.getNumberValue().intValue();
          }
          break;
        case OPTICAL_PATH_SEQUENCE:
          for (DicomTag child : tag.children) {
            if (child.attribute == OPTICAL_PATH_ID) {
              opticalChannels++;
            }
            else if (child.attribute == OPTICAL_PATH_DESCRIPTION) {
              channelNames.add(child.getStringValue());
            }
          }
          break;
        case EXTENDED_DEPTH_OF_FIELD:
          edf = tag.getStringValue().equalsIgnoreCase("yes");
          break;
        default:
          in.seek(tag.getEndPointer());
      }
      if (in.getFilePointer() >= (in.length() - 4)) {
        decodingTags = false;
      }
    }
    if (imagesPerFile == 0) imagesPerFile = 1;

    if (new Location(currentId).getName().equals("DICOMDIR")) {
      String parent = new Location(currentId).getAbsoluteFile().getParent();
      Integer[] fileKeys = fileList.keySet().toArray(new Integer[0]);
      Arrays.sort(fileKeys);
      for (int q=0; q<fileList.size(); q++) {
        for (int i=0; i<fileList.get(fileKeys[q]).size(); i++) {
          String file = fileList.get(fileKeys[q]).get(i);
          file = file.replace('\\', File.separatorChar);
          file = file.replaceAll("/", File.separator);
          fileList.get(fileKeys[q]).set(i, parent + File.separator + file);
        }
      }
      for (int i=0; i<companionFiles.size(); i++) {
        String file = companionFiles.get(i);
        file = file.replace('\\', File.separatorChar);
        file = file.replaceAll("/", File.separator);
        companionFiles.set(i, parent + File.separator + file);
      }
      tilePositions = new HashMap<Integer, List<DicomTile>>();
      zOffsets = new HashMap<Integer, List<Double>>();
      core.clear();
    }
    else {
      if (m.sizeZ == 0) {
        m.sizeZ = 1;
      }
      if (opticalChannels == 0 || (concatenationNumber == null && ((imagesPerFile / m.sizeZ) % opticalChannels != 0))) {
        opticalChannels = 1;
      }
      m.sizeC *= opticalChannels;
      m.imageCount *= opticalChannels;

      // fill in the implicit tile boundaries so that there doesn't need
      // to be any more distinct logic for TILED_FULL vs TILED_SPARSE
      if (tiledFull || tilePositions == null) {
        tilePositions = new HashMap<Integer, List<DicomTile>>();
        tilePositions.put(0, new ArrayList<DicomTile>());

        int cols = (int) Math.ceil((double) getSizeX() / originalX);
        int rows = (int) Math.ceil((double) getSizeY() / originalY);
        int tilesPerPlane = rows * cols;
        int c = frameOffsetNumber / (tilesPerPlane * getSizeZ());
        int newOffset = frameOffsetNumber - (c * tilesPerPlane * getSizeZ());
        int z = newOffset / tilesPerPlane;
        int x = originalX * ((newOffset % tilesPerPlane) % cols);
        int y = originalY * ((newOffset % tilesPerPlane) / cols);

        for (int p=0; p<imagesPerFile; p++) {
          DicomTile tile = new DicomTile();
          tile.fileIndex = tilePositions.get(0).size();
          if (m.sizeZ == 1 && fileZOffset != null) {
            tile.zOffset = fileZOffset;
          }
          else {
            tile.zOffset = (double) z;
          }
          tile.channel = c;
          tile.file = new Location(currentId).getAbsolutePath();
          tile.region = new Region(x, y, originalX, originalY);
          tile.isRLE = isRLE;
          tile.isJPEG = isJPEG;
          tile.isJP2K = isJP2K;
          tile.isDeflate = isDeflate;
          tilePositions.get(0).add(tile);

          if (x + originalX < getSizeX()) {
            x += originalX;
          }
          else if (y + originalY < getSizeY()) {
            x = 0;
            y += originalY;
          }
          else {
            x = 0;
            y = 0;
            z++;
            if (z == m.sizeZ) {
              z = 0;
              c++;
            }
          }
        }
      }

      m.bitsPerPixel = bitsPerPixel;
      while (bitsPerPixel % 8 != 0) bitsPerPixel++;
      if (bitsPerPixel == 24 || bitsPerPixel == 48) {
        bitsPerPixel /= 3;
        m.bitsPerPixel /= 3;
      }

      m.pixelType =
        FormatTools.pixelTypeFromBytes(bitsPerPixel / 8, signed, false);

      LOGGER.info("Calculating image offsets");

      calculatePixelsOffsets(baseOffset);
    }
    makeFileList();

    LOGGER.info("Populating metadata");

    int seriesCount = fileList.size();

    Integer[] keys = fileList.keySet().toArray(new Integer[0]);
    Arrays.sort(keys);

    // at this point, we have a list of all files to be grouped together
    // and have parsed tags from the current file

    ArrayList<DicomFileInfo> metadataInfo = new ArrayList<DicomFileInfo>();

    if (seriesCount > 1) {
      // TODO: this case is largely untested at the moment and may need revisiting
      for (int i=0; i<seriesCount; i++) {
        List<String> currentFileList = fileList.get(keys[i]);
        DicomFileInfo fileInfo = createFileInfo(currentFileList.get(0));
        tilePositions.put(i, fileInfo.tiles);
        zOffsets.put(i, fileInfo.zOffsets);
        fileInfo.coreMetadata.sizeZ *= currentFileList.size();
        fileInfo.coreMetadata.imageCount = fileInfo.coreMetadata.sizeZ;
        core.add(fileInfo.coreMetadata);
        metadataInfo.add(fileInfo);
      }
    }
    else {
      List<String> allFiles = fileList.get(keys[0]);
      List<DicomFileInfo> infos = new ArrayList<DicomFileInfo>();

      // parse tags for each file
      for (String file : allFiles) {
        DicomFileInfo info = createFileInfo(file);
        infos.add(info);
      }

      if (infos.size() > 1) {
        infos.sort(null);

        metadataInfo.add(infos.get(0));

        core.clear();
        tilePositions.clear();
        updateCoreMetadata(infos.get(0).coreMetadata);
        core.add(infos.get(0).coreMetadata);

        tilePositions.put(0, infos.get(0).tiles);

        zOffsets.put(0, infos.get(0).zOffsets);

        // determine what each file is:
        //  - a separate image (Bio-Formats series), e.g. largest pyramid level, label, overview, etc.
        //  - a downsampled resolution
        //  - another Z section or channel in an existing Bio-Formats series
        for (int i=1; i<infos.size(); i++) {
          DicomFileInfo info = infos.get(i);
          DicomFileInfo prevInfo = infos.get(i - 1);
          updateCoreMetadata(info.coreMetadata);

          // image type is used to distinguish between downsampled resolutions and smaller separate images
          if ((info.imageType.indexOf("VOLUME") < 0 || info.edf != prevInfo.edf) &&
            (!info.imageType.equals(prevInfo.imageType) || info.coreMetadata.sizeX != prevInfo.coreMetadata.sizeX ||
            info.coreMetadata.sizeY != prevInfo.coreMetadata.sizeY))
          {
            core.add(info.coreMetadata);
            metadataInfo.add(info);
          }
          else if (info.coreMetadata.sizeX != prevInfo.coreMetadata.sizeX &&
            info.coreMetadata.sizeY != prevInfo.coreMetadata.sizeY)
          {
            core.add(core.size() - 1, info.coreMetadata);
            metadataInfo.add(info);
          }
          else if (info.coreMetadata.sizeX != prevInfo.coreMetadata.sizeX ||
            info.coreMetadata.sizeY != prevInfo.coreMetadata.sizeY)
          {
            core.add(info.coreMetadata);
            metadataInfo.add(info);
          }
          else if (info.concatenationIndex == 0) {
            core.get(core.size() - 1, core.sizes()[core.size() - 1] - 1).sizeZ++;
            core.get(core.size() - 1, core.sizes()[core.size() - 1] - 1).imageCount += info.coreMetadata.imageCount;
          }

          int lastCoreIndex = core.flattenedIndex(core.size() - 1, core.sizes()[core.size() - 1] - 1);
          if (!tilePositions.containsKey(lastCoreIndex)) {
            tilePositions.put(lastCoreIndex, info.tiles);
          }
          else {
            tilePositions.get(lastCoreIndex).addAll(info.tiles);
          }

          if (zOffsets.containsKey(lastCoreIndex)) {
            for (Double z : info.zOffsets) {
              if (!zOffsets.get(lastCoreIndex).contains(z)) {
                zOffsets.get(lastCoreIndex).add(z);
              }
            }
          }
          else {
            zOffsets.put(lastCoreIndex, info.zOffsets);
          }
        }
      }
      else {
        tilePositions.put(0, infos.get(0).tiles);
        updateCoreMetadata(core.get(0, 0));
        metadataInfo.add(infos.get(0));
        zOffsets.put(0, infos.get(0).zOffsets);
      }
    }

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      DicomFileInfo info = metadataInfo.get(seriesToCoreIndex(i));

      if (info.timestamp != null) {
        store.setImageAcquisitionDate(info.timestamp, i);
      }

      store.setImageName("Series " + i, i);

      if (level != MetadataLevel.MINIMUM) {
        store.setImageDescription(info.imageType, i);

        if (info.pixelSizeX != null) {
          store.setPixelsPhysicalSizeX(info.pixelSizeX, i);
        }
        if (info.pixelSizeY != null) {
          store.setPixelsPhysicalSizeY(info.pixelSizeY, i);
        }
        if (info.pixelSizeZ != null) {
          store.setPixelsPhysicalSizeZ(info.pixelSizeZ, i);
        }

        for (int c=0; c<getEffectiveSizeC(); c++) {
          if (c < info.channelNames.size()) {
            store.setChannelName(info.channelNames.get(c), i, c);
          }
        }

        for (int p=0; p<getImageCount(); p++) {
          if (p < info.positionX.size()) {
            if (info.positionX.get(p) != null) {
              Length x = new Length(info.positionX.get(p), UNITS.MILLIMETER);
              if (x != null) {
                store.setPlanePositionX(x, i, p);
              }
            }
          }
          if (p < info.positionY.size()) {
            if (info.positionY.get(p) != null) {
              Length y = new Length(info.positionY.get(p), UNITS.MILLIMETER);
              if (y != null) {
                store.setPlanePositionY(y, i, p);
              }
            }
          }
          if (p < info.positionZ.size()) {
            if (info.positionZ.get(p) != null) {
              Length z = new Length(info.positionZ.get(p), UNITS.MILLIMETER);
              if (z != null) {
                store.setPlanePositionZ(z, i, p);
              }
            }
          }
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  // TODO: target for refactoring, this can possibly be combined with the
  // tag parsing loop that calls this
  private void addInfo(DicomTag info) throws IOException {
    CoreMetadata m = core.get(0, 0);
    m.littleEndian = in.isLittleEndian();

    if (info.attribute != ITEM) {
      if (info.attribute != null) {
        String infoString = info.getStringValue();
        Number infoNumber = info.getNumberValue();

        switch (info.attribute) {
          case SAMPLES_PER_PIXEL:
            m.sizeC = infoNumber.intValue();
            if (getSizeC() > 1) m.rgb = true;
            break;
          case PHOTOMETRIC_INTERPRETATION:
            if (infoString.equals("PALETTE COLOR")) {
              m.indexed = true;
              m.sizeC = 1;
              m.rgb = false;
              lut = new byte[3][];
              shortLut = new short[3][];
            }
            else if (infoString.startsWith("MONOCHROME")) {
              inverted = infoString.endsWith("1");
            }
            break;
          case ACQUISITION_TIMESTAMP:
            if (infoString.length() >= 8) {
              String timestamp = infoString.substring(0, 8);
              try {
                if (Integer.parseInt(timestamp) > 0) {
                  originalDate = timestamp;
                }
              }
              catch (NumberFormatException e) {
                LOGGER.trace("", e);
              }
              originalTime = infoString.substring(8);
            }
            break;
          case ACQUISITION_DATE:
            if (infoNumber != null && infoNumber.intValue() > 0) {
              originalDate = infoString;
            }
            break;
          case ACQUISITION_TIME:
            originalTime = infoString;
            break;
          case SOP_INSTANCE_UID:
            instanceUID = infoString;
            break;
          case INSTANCE_NUMBER:
            if (infoString != null && !infoString.isEmpty()) {
              originalInstance = infoString;
            }
            break;
          case SERIES_NUMBER:
            originalSeries = parseIntValue(infoNumber, 0);
            break;
          case RED_LUT_DATA:
          case GREEN_LUT_DATA:
          case BLUE_LUT_DATA:
          case SEGMENTED_RED_LUT_DATA:
          case SEGMENTED_GREEN_LUT_DATA:
          case SEGMENTED_BLUE_LUT_DATA:
            if (info.value != null && shortLut != null) {
              String color = info.key.substring(0, info.key.indexOf(' ')).trim();
              int ndx = color.equals("Red") ? 0 : color.equals("Green") ? 1 : 2;
              shortLut[ndx] = (short[]) info.value;
              lut[ndx] = new byte[shortLut[ndx].length];
              for (int i=0; i<lut[ndx].length; i++) {
                lut[ndx][i] = (byte) (shortLut[ndx][i] & 0xff);
              }
            }
            break;
          case CONTENT_TIME:
            time = infoString;
            break;
          case CONTENT_DATE:
            date = infoString;
            break;
          case IMAGE_TYPE:
            if (imageType == null) {
              imageType = infoString;
            }
            break;
          case PIXEL_SPACING:
            parsePixelSpacing(infoString);
            break;
          case SLICE_SPACING:
            if (infoNumber != null) {
              pixelSizeZ = infoNumber.doubleValue();
            }
            break;
          case IMAGE_POSITION_PATIENT:
            String[] positions = infoString.replace('\\', '_').split("_");
            if (positions.length > 0) {
              try {
                positionX.add(Double.valueOf(positions[0]));
              }
              catch (NumberFormatException e) {
                positionX.add(null);
              }
            }
            else {
              positionX.add(null);
              positionY.add(null);
              positionZ.add(null);
            }
            if (positions.length > 1) {
              try {
                positionY.add(Double.valueOf(positions[1]));
              }
              catch (NumberFormatException e) {
                positionY.add(null);
              }
            }
            else {
              positionY.add(null);
              positionZ.add(null);
            }
            if (positions.length > 2) {
              try {
                positionZ.add(Double.valueOf(positions[2]));
              }
              catch (NumberFormatException e) {
                positionZ.add(null);
              }
            }
            else {
              positionZ.add(null);
            }
            break;
        }
      }

      int tag = info.tag;
      if (((tag & 0xffff0000) >> 16) != 0x7fe0) {
        String key = DicomAttribute.formatTag(tag);
        if (info.key != null) {
          key += " " + info.key;
        }
        addOriginalMetadata(key, info);
      }
    }
  }

  /**
   * Store the given tag's value (if present) in the original metadata
   * hashtable using the provided key. Any children of the tag will
   * be added to the table as well.
   *
   * Note that the "Per-Frame Functional Groups Sequence" and
   * "Referenced Image Navigation Sequence" and any children
   * will be omitted, as will any values that are byte or short arrays
   * (e.g. lookup tables). This is necessary to prevent memory exhaustion.
   * Applications that need to traverse the complete hierarchy of
   * DICOM tags should use getTags() to access tags directly and not
   * rely upon the original metadata table.
   */
  private void addOriginalMetadata(String key, DicomTag info) {
    if (info.value != null && !(info.value instanceof byte[]) &&
      !(info.value instanceof short[]))
    {
      if (info.value instanceof String) {
        addSeriesMetaList(key, ((String) info.value).trim());
      }
      else {
        addSeriesMetaList(key, info.value);
      }
    }
    if (info.attribute != PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE &&
      info.attribute != REFERENCED_IMAGE_NAVIGATION_SEQUENCE)
    {
      for (DicomTag child : info.children) {
        String childKey = DicomAttribute.formatTag(child.tag);
        if (child.key != null) {
          childKey += " " + child.key;
        }
        addOriginalMetadata(childKey, child);
      }
    }
  }

  /**
   * Build a list of files that belong with the current file.
   */
  private void makeFileList() throws FormatException, IOException {
    LOGGER.info("Building file list");

    if (fileList == null && originalInstance != null && originalDate != null &&
      originalTime != null && instanceUID != null && isGroupFiles())
    {
      currentId = new Location(currentId).getAbsolutePath();
      fileList = new HashMap<Integer, List<String>>();
      final Integer s = originalSeries;
      fileList.put(s, new ArrayList<String>());

      int instanceNumber = Integer.parseInt(originalInstance) - 1;
      if (instanceNumber == 0) fileList.get(s).add(currentId);
      else {
        while (instanceNumber > fileList.get(s).size()) {
          fileList.get(s).add(null);
        }
        fileList.get(s).add(currentId);
      }

      Location currentFile = new Location(currentId).getAbsoluteFile();
      Location directory = currentFile.getParentFile();

      scanDirectory(directory, true);

      for (final List<String> files : fileList.values()) {
        final Iterator<String> fileIterator = files.iterator();
        while (fileIterator.hasNext()) {
          if (fileIterator.next() == null) {
            fileIterator.remove();
          }
        }
      }
    }
    else if (fileList == null || !isGroupFiles()) {
      fileList = new HashMap<Integer, List<String>>();
      fileList.put(0, new ArrayList<String>());
      fileList.get(0).add(new Location(currentId).getAbsolutePath());
    }
  }

  // -- Utility methods --

  /**
   * Scan the given directory for files that belong to this dataset.
   */
  private void scanDirectory(Location dir, boolean checkSeries)
    throws FormatException, IOException
  {
    String[] files = dir.list(true);
    if (files == null) return;
    Arrays.sort(files);
    for (String f : files) {
      String file = new Location(dir, f).getAbsolutePath();
      LOGGER.debug("Checking file {}", file);
      if (!f.equals(currentId) && !file.equals(currentId) && isThisType(file)) {
        addFileToList(file, checkSeries);
      }
    }
  }

  /**
   * Determine if the given file belongs in the same dataset as this file.
   */
  private void addFileToList(String file, boolean checkSeries)
    throws FormatException, IOException
  {
    int currentX = 0, currentY = 0;
    int fileSeries = -1;
    String thisSpecimen = null;

    String date = null, time = null, instance = null;
    String thisInstanceUID = null;
    try (RandomAccessInputStream stream = new RandomAccessInputStream(file)) {
      if (!isThisType(stream)) {
        return;
      }
      stream.order(true);
      stream.seek(HEADER_LENGTH);
      if (!stream.readString(4).equals("DICM")) {
        stream.seek(0);
      }

      // TODO: probably best to remove this tag parsing logic and use a DicomFileInfo
      boolean bigEndian = false;
      boolean odd = false;
      long currentLocation = stream.getFilePointer();
      while (date == null || time == null || instance == null ||
        (checkSeries && fileSeries < 0) || currentX == 0 || currentY == 0 ||
        (originalSpecimen != null && thisSpecimen == null))
      {
        long fp = stream.getFilePointer();
        if (fp + 4 >= stream.length() || fp < 0) break;
        DicomTag tag = new DicomTag(stream, bigEndian, 0, odd);

        odd = (currentLocation & 1) != 0;

        if (tag.attribute == null || (tag.value == null && tag.children.size() == 0)) {
          stream.seek(tag.getEndPointer());
          continue;
        }

        switch (tag.attribute) {
          case SPECIMEN_DESCRIPTION_SEQUENCE:
            DicomTag specimenID = tag.lookupChild(SPECIMEN_ID);
            if (specimenID != null) {
              thisSpecimen = specimenID.getStringValue();
            }
            break;
          case SOP_INSTANCE_UID:
            thisInstanceUID = tag.getStringValue();
            break;
          case INSTANCE_NUMBER:
            instance = tag.getStringValue();
            if (instance.length() == 0) instance = null;
            break;
          case ACQUISITION_TIMESTAMP:
            if (tag.getStringValue().length() >= 8) {
              date = tag.getStringValue().substring(0, 8);
              time = tag.getStringValue().substring(8);
            }
            break;
          case ACQUISITION_TIME:
            time = tag.getStringValue();
            break;
          case ACQUISITION_DATE:
            date = tag.getStringValue();
            break;
          case SERIES_NUMBER:
            fileSeries = parseIntValue(tag.getNumberValue(), 0);
            break;
          case ROWS:
            currentY = (int) Math.max(currentY, tag.getNumberValue().intValue());
            break;
          case COLUMNS:
            currentX = (int) Math.max(currentX, tag.getNumberValue().intValue());
            break;
          case PIXEL_DATA:
          case INVALID_PIXEL_DATA:
          case VARIABLE_PIXEL_DATA:
            stream.seek(stream.length() - 1);
            break;
          default:
            stream.seek(tag.getEndPointer());
        }
      }
    }

    LOGGER.debug("file = {}", file);
    LOGGER.debug("  date = {}, originalDate = {}", date, originalDate);
    LOGGER.debug("  time = {}, originalTime = {}", time, originalTime);
    LOGGER.debug("  instance = {}, originalInstance = {}", instance, originalInstance);
    LOGGER.debug("  checkSeries = {}", checkSeries);
    LOGGER.debug("  fileSeries = {}, originalSeries = {}", fileSeries, originalSeries);
    LOGGER.debug("  currentX = {}, originalX = {}", currentX, originalX);
    LOGGER.debug("  currentY = {}, originalY = {}", currentY, originalY);
    LOGGER.debug("  thisSpecimen = {}, originalSpecimen = {}", thisSpecimen, originalSpecimen);

    boolean noSpecimens = originalSpecimen == null && thisSpecimen == null;
    boolean oneNullSpecimen = originalSpecimen == null || thisSpecimen == null;

    // can't group a different series, or same instance and series as original file
    // unless the file we're checking is a DICOMDIR
    boolean dicomdir = new Location(file).getName().equals("DICOMDIR");
    if (date == null || time == null || instance == null ||
      (checkSeries && fileSeries != originalSeries) ||
      (!noSpecimens && oneNullSpecimen) || (!noSpecimens && !originalSpecimen.equals(thisSpecimen)))
    {
      return;
    }
    if (dicomdir) {
      companionFiles.add(new Location(file).getAbsolutePath());
      return;
    }

    if (tilePositions == null) {
      if (instanceUID != null && thisInstanceUID != null) {
        String[] uid = instanceUID.split("\\.");
        String[] thisUID = thisInstanceUID.split("\\.");
        for (int i=0; i<Math.min(uid.length, thisUID.length)-2; i++) {
          if (!uid[i].equals(thisUID[i])) {
            return;
          }
        }
      }
      else if (instanceUID != null || thisInstanceUID != null) {
        return;
      }
      if (currentX != originalX || currentY != originalY) {
        fileSeries++;
      }
    }

    double stamp = getTimestampMicroseconds(time);
    double timestamp = getTimestampMicroseconds(originalTime);

    LOGGER.trace("  stamp = {}", stamp);
    LOGGER.trace("  timestamp = {}", timestamp);

    if (date.equals(originalDate) && (Math.abs(stamp - timestamp) < 150000000)) {
      int position = Integer.parseInt(instance) - 1;
      if (position < 0) position = 0;
      if (fileList.get(fileSeries) == null) {
        fileList.put(fileSeries, new ArrayList<String>());
      }
      if (position < fileList.get(fileSeries).size()) {
        while (position < fileList.get(fileSeries).size() &&
          fileList.get(fileSeries).get(position) != null)
        {
          position++;
        }
        if (position < fileList.get(fileSeries).size()) {
          fileList.get(fileSeries).set(position, file);
        }
        else if (!fileList.get(fileSeries).contains(file)) {
          fileList.get(fileSeries).add(file);
        }
      }
      else if (!fileList.get(fileSeries).contains(file)) {
        while (position > fileList.get(fileSeries).size()) {
          fileList.get(fileSeries).add(null);
        }
        fileList.get(fileSeries).add(file);
      }
    }
  }

  private int parseIntValue(Number v, int defaultValue) {
    return v == null ? defaultValue : v.intValue();
  }

  /**
   * Convert the timestamp from a TM value to microseconds.
   * The timestamp format defined in the standard is HHMMSS.FFFFFF
   * but HH:MM:SS.FFFFFF is supported as well since some files
   * incorrectly use that format.
   */
  private long getTimestampMicroseconds(String v) {
    if (v == null) {
      return 0;
    }
    v = v.trim();
    v = v.replaceAll(":", "");
    if (v.indexOf("+") >= 0) {
      v = v.substring(0, v.indexOf("+"));
    }
    if (v.indexOf("-") >= 0) {
      v = v.substring(0, v.indexOf("-"));
    }
    if (v.isEmpty()) {
      return 0;
    }
    int hours = Integer.parseInt(v.substring(0, 2));
    long total = hours * 60 * 60;
    if (v.length() > 2) {
      int minutes = Integer.parseInt(v.substring(2, 4));
      total += minutes * 60;
    }
    if (v.length() > 4) {
      int seconds = Integer.parseInt(v.substring(4, 6));
      total += seconds;
    }
    total *= 1000000;
    if (v.length() > 6) {
      total += Integer.parseInt(v.substring(v.indexOf(".") + 1));
    }
    return total;
  }

  /**
   * DICOM datasets produced by:
   * http://www.ct-imaging.de/index.php/en/ct-systeme-e/mikro-ct-e.html
   * contain a bunch of extra metadata and log files.
   *
   * We do not parse these extra files, but do locate and attach them to the
   * DICOM file(s).
   */
  private void attachCompanionFiles() throws IOException {
    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    Location grandparent = parent.getParentFile();

    if (new Location(grandparent, parent.getName() + ".mif").exists()) {
      String[] list = grandparent.list(true);
      for (String f : list) {
        Location file = new Location(grandparent, f);
        if (!file.isDirectory()) {
          companionFiles.add(file.getAbsolutePath());
        }
      }
    }
  }

  /**
   * Decompress pixel data associated with the given DicomTile.
   */
  private void getTile(DicomTile tile, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int ec = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int bytes = tile.region.width * tile.region.height * bpp * ec;
    try (RandomAccessInputStream stream = new RandomAccessInputStream(tile.file)) {
      if (tile.fileOffset >= stream.length()) {
        LOGGER.error("attempted to read beyond end of file ({}, {})", tile.fileOffset, tile.file);
        return;
      }
      stream.seek(tile.fileOffset);
      LOGGER.debug("reading from offset = {}, file = {}", tile.fileOffset, tile.file);

      if (tile.isRLE) {
        // plane is compressed using run-length encoding
        CodecOptions options = new CodecOptions();
        options.maxBytes = tile.region.width * tile.region.height;
        for (int c=0; c<ec; c++) {
          PackbitsCodec codec = new PackbitsCodec();
          byte[] t = null;

          if (bpp > 1) {
            int plane = bytes / (bpp * ec);
            byte[][] tmp = new byte[bpp][];
            long start = stream.getFilePointer();
            for (int i=0; i<bpp; i++) {
              // one or more extra 0 bytes can be inserted between
              // the planes, but there isn't a good way to know in advance
              // only way to know is to see if decompressing produces the
              // correct number of bytes
              tmp[i] = codec.decompress(stream, options);
              if (i > 0 && tmp[i].length > options.maxBytes) {
                stream.seek(start);
                tmp[i] = codec.decompress(stream, options);
              }
              if (!tile.last || i < bpp - 1) {
                start = stream.getFilePointer();
                while (stream.read() == 0);
                long end = stream.getFilePointer();
                stream.seek(end - 1);
              }
            }
            t = new byte[bytes / ec];
            for (int i=0; i<plane; i++) {
              for (int j=0; j<bpp; j++) {
                int byteIndex = isLittleEndian() ? bpp - j - 1 : j;
                if (i < tmp[byteIndex].length) {
                  t[i * bpp + j] = tmp[byteIndex][i];
                }
              }
            }
          }
          else {
            t = codec.decompress(stream, options);
            if (t.length < (bytes / ec)) {
              byte[] tmp = t;
              t = new byte[bytes / ec];
              System.arraycopy(tmp, 0, t, 0, tmp.length);
            }
            if (!tile.last || c < ec - 1) {
              while (stream.read() == 0);
              stream.seek(stream.getFilePointer() - 1);
            }
          }

          int rowLen = w * bpp;
          int srcRowLen = tile.region.width * bpp;

          for (int row=0; row<h; row++) {
            int src = (row + y) * srcRowLen + x * bpp;
            int dest = (h * c + row) * rowLen;
            int len = (int) Math.min(rowLen, t.length - src - 1);
            if (len < 0) break;
            System.arraycopy(t, src, buf, dest, len);
          }
        }
      }
      else if (tile.isJPEG || tile.isJP2K) {
        // plane is compressed using JPEG or JPEG-2000
        byte[] b = new byte[(int) (tile.endOffset - stream.getFilePointer())];
        stream.read(b);
        if (b.length < 8) {
          return;
        }

        if (b[2] != (byte) 0xff) {
          byte[] tmp = new byte[b.length + 1];
          tmp[0] = b[0];
          tmp[1] = b[1];
          tmp[2] = (byte) 0xff;
          System.arraycopy(b, 2, tmp, 3, b.length - 2);
          b = tmp;
        }

        int pt = b.length - 2;
        while (pt >= 0 && (b[pt] != (byte) 0xff || b[pt + 1] != (byte) 0xd9)) {
          pt--;
        }
        if (pt < 0) {
          byte[] tmp = b;
          b = new byte[tmp.length + 2];
          System.arraycopy(tmp, 0, b, 0, tmp.length);
          b[b.length - 2] = (byte) 0xff;
          b[b.length - 1] = (byte) 0xd9;
        }
        else if (pt < b.length - 2) {
          byte[] tmp = b;
          b = new byte[pt + 2];
          System.arraycopy(tmp, 0, b, 0, b.length);
        }

        Codec codec = null;
        CodecOptions options = new CodecOptions();
        options.littleEndian = isLittleEndian();
        options.interleaved = isInterleaved();
        if (tile.isJPEG) codec = new JPEGCodec();
        else codec = new JPEG2000Codec();
        b = codec.decompress(b, options);

        int rowLen = w * bpp;
        int srcRowLen = tile.region.width * bpp;

        if (isInterleaved()) {
          rowLen *= ec;
          srcRowLen *= ec;
          for (int row=0; row<h; row++) {
            System.arraycopy(b, (row + y) * srcRowLen + x * bpp * ec,
              buf, row * rowLen, rowLen);
          }
        }
        else {
          int srcPlane = originalY * srcRowLen;
          for (int c=0; c<ec; c++) {
            for (int row=0; row<h; row++) {
              System.arraycopy(b, c * srcPlane + (row + y) * srcRowLen + x * bpp,
                buf, h * rowLen * c + row * rowLen, rowLen);
            }
          }
        }
      }
      else if (tile.isDeflate) {
        // TODO
        throw new UnsupportedCompressionException(
          "Deflate data is not supported.");
      }
      else {
        // plane is not compressed
        if (originalX > 0 && originalY > 0) {
          readPlane(stream, x, y, w, h, 0, originalX, originalY, buf);
        }
        else {
          readPlane(stream, x, y, w, h, buf);
        }
      }
    }
  }

  private void parsePixelSpacing(String value) {
    pixelSizeY = value.substring(0, value.indexOf("\\"));
    pixelSizeX = value.substring(value.lastIndexOf("\\") + 1);
  }

  private void handleReferencedFile(DicomTag tag, String currentType) {
    if ("IMAGE".equals(currentType)) {
      if (fileList == null) {
        fileList = new HashMap<Integer, List<String>>();
      }
      int seriesIndex = originalSeries;
      if (fileList.get(seriesIndex) == null) {
        fileList.put(seriesIndex, new ArrayList<String>());
      }
      fileList.get(seriesIndex).add(tag.getStringValue());
    }
    else {
      companionFiles.add(new Location(tag.getStringValue()).getAbsolutePath());
    }
  }

  /**
   * Calculate offsets to all pixel data (tiles or planes) in the file,
   * starting from the given offset.
   */
  private void calculatePixelsOffsets(long baseOffset) throws FormatException, IOException {
    zOffsets = new HashMap<Integer, List<Double>>();

    if (baseOffset == in.length()) {
      return;
    }
    int channelCount = getRGBChannelCount();
    if (lut != null || channelCount == 0) {
      channelCount = 1;
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int plane = originalX * originalY * channelCount * bpp;

    in.seek(baseOffset - 12);
    int len = in.readInt();
    if (len >= 0 && len + in.getFilePointer() < in.length()) {
      in.skipBytes(len);
      int check = in.readShort() & 0xffff;
      if (check == 0xfffe) {
        baseOffset = in.getFilePointer() + 2;
      }
    }

    long offset = baseOffset;
    for (int i=0; i<imagesPerFile; i++) {
      if (isRLE) {
        if (i == 0) in.seek(baseOffset);
        else {
          in.seek(offset);
          CodecOptions options = new CodecOptions();
          options.maxBytes = plane / bpp;
          for (int q=0; q<bpp; q++) {
            new PackbitsCodec().decompress(in, options);
            while (in.read() == 0);
            in.seek(in.getFilePointer() - 1);
          }
        }
        in.skipBytes(i == 0 ? 64 : 53);
        while (in.read() == 0);
        offset = in.getFilePointer() - 1;
      }
      else if (isJPEG || isJP2K) {
        // scan for next JPEG magic byte sequence
        // ideally this would just use the encapsulated stream data to
        // efficiently assemble the list of offsets, but we have some
        // datasets that have incorrect stored block lengths
        if (i == 0) {
          offset = baseOffset;
        }
        else {
          offset += 3;
        }

        in.seek(offset);
        byte secondCheck = isJPEG ? (byte) 0xd8 : (byte) 0x4f;

        byte[] buf = new byte[(int) Math.min(8192, in.length() - in.getFilePointer())];
        int n = in.read(buf);
        boolean found = false;
        boolean endFound = false;
        while (!found && n > 4) {
          for (int q=0; q<n-3; q++) {
            if (buf[q] == (byte) 0xff && buf[q + 1] == secondCheck &&
              buf[q + 2] == (byte) 0xff)
            {
              if (isJPEG || (isJP2K && buf[q + 3] == 0x51)) {
                if (endFound || i == 0) {
                  found = true;
                  offset = in.getFilePointer() + q - n;
                }
                break;
              }
            }
            else if (buf[q] == (byte) 0xff && buf[q + 1] == (byte) 0xd9) {
              endFound = true;
            }
          }
          if (!found) {
            for (int q=0; q<4; q++) {
              buf[q] = buf[buf.length + q - 4];
            }
            n = in.read(buf, 4, buf.length - 4) + 4;
          }
        }
      }
      else offset = baseOffset + plane*i;

      tilePositions.get(getCoreIndex()).get(i).fileOffset = offset;
      tilePositions.get(getCoreIndex()).get(i).last = i == imagesPerFile - 1;
      if (i > 0) {
        tilePositions.get(getCoreIndex()).get(i - 1).endOffset = tilePositions.get(getCoreIndex()).get(i).fileOffset;
      }
      if (i == imagesPerFile - 1) {
        tilePositions.get(getCoreIndex()).get(i).endOffset = in.length();
      }
      if (!zOffsets.containsKey(getCoreIndex())) {
        zOffsets.put(getCoreIndex(), new ArrayList<Double>());
      }
      Double z = tilePositions.get(getCoreIndex()).get(i).zOffset;
      if (!zOffsets.get(getCoreIndex()).contains(z)) {
        zOffsets.get(getCoreIndex()).add(z);
      }
    }
  }

  /**
   * Construct a DicomFileInfo for the given file.
   * If the file is the currently initialized file, don't parse it again.
   */
  private DicomFileInfo createFileInfo(String file) throws FormatException, IOException {
    if (new Location(file).getAbsolutePath().equals(new Location(currentId).getAbsolutePath())) {
      DicomFileInfo info = new DicomFileInfo();
      info.file = new Location(file).getAbsolutePath();
      info.concatenationIndex = getConcatenationIndex();
      info.coreMetadata = core.get(0, 0);
      info.tiles = new ArrayList<DicomTile>();
      for (DicomTile t : getTiles()) {
        info.tiles.add(t);
      }
      info.imageType = getImageType();
      info.zOffsets = getZOffsets();
      info.edf = edf;
      info.pixelSizeX = getPixelSizeX();
      info.pixelSizeY = getPixelSizeY();
      info.pixelSizeZ = getPixelSizeZ();
      info.positionX = getPositionX();
      info.positionY = getPositionY();
      info.positionZ = getPositionZ();
      info.channelNames = getChannelNames();
      info.timestamp = getTimestamp();
      return info;
    }
    return new DicomFileInfo(new Location(file).getAbsolutePath());
  }

  private void updateCoreMetadata(CoreMetadata ms) {
    if (ms.sizeC == 0) ms.sizeC = 1;
    ms.sizeT = 1;
    ms.dimensionOrder = "XYCZT";
    ms.metadataComplete = true;
    ms.falseColor = false;
    if (isRLE) ms.interleaved = false;
    ms.imageCount = ms.sizeZ;
    if (!ms.rgb) {
      ms.imageCount *= ms.sizeC;
    }
  }

  protected String getImageType() {
    return imageType;
  }

  protected List<DicomTile> getTiles() {
    return tilePositions.get(0);
  }

  protected List<Double> getZOffsets() {
    return zOffsets.get(0);
  }

  protected int getConcatenationIndex() {
    if (concatenationNumber == null) {
      return 0;
    }
    return concatenationNumber.intValue() - 1;
  }

  protected Length getPixelSizeX() {
    if (pixelSizeX == null) {
      return null;
    }
    return FormatTools.getPhysicalSizeX(new Double(pixelSizeX), UNITS.MILLIMETER);
  }

  protected Length getPixelSizeY() {
    if (pixelSizeY == null) {
      return null;
    }
    return FormatTools.getPhysicalSizeY(new Double(pixelSizeY), UNITS.MILLIMETER);
  }

  protected Length getPixelSizeZ() {
    if (pixelSizeZ == null) {
      return null;
    }
    return FormatTools.getPhysicalSizeZ(new Double(pixelSizeZ), UNITS.MILLIMETER);
  }

  protected List<Double> getPositionX() {
    return positionX;
  }

  protected List<Double> getPositionY() {
    return positionY;
  }

  protected List<Double> getPositionZ() {
    return positionZ;
  }

  protected List<String> getChannelNames() {
    return channelNames;
  }

  protected boolean isExtendedDepthOfField() {
    return edf;
  }

  protected Timestamp getTimestamp() {
    String stamp = null;

    if (date != null && time != null) {
      stamp = date + " " + time;
      stamp = DateTools.formatDate(stamp, "yyyy.MM.dd HH:mm:ss", ".");
    }
    if (stamp != null && !stamp.isEmpty()) {
      return new Timestamp(stamp);
    }
    return null;
  }

  /**
   * Provide the complete hierarchy of DICOM tags.
   * Applications that need to query or display the complete
   * tag structure should use this method to retrieve tags
   * instead of relying upon original metadata.
   */
  public List<DicomTag> getTags() {
    return tags;
  }

}
