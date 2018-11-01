/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import static java.util.Collections.unmodifiableSet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import loci.common.CBZip2InputStream;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;

import ome.xml.model.primitives.PositiveFloat;

/**
 * Reader for Keller Lab Block (KLB) files.
 */
public class KLBReader extends FormatReader {

  // -- Constants --
  private static final int KLB_DATA_DIMS = 5; //images have at most 5 dimensions: x,y,z, c, t
  private static final int KLB_METADATA_SIZE = 256; //number of bytes in metadata
  private static final int KLB_DEFAULT_HEADER_VERSION = 2;

  private static final int UINT8_TYPE = 0;
  private static final int UINT16_TYPE = 1;
  private static final int UINT32_TYPE = 2;
  private static final int UINT64_TYPE = 3;
  private static final int INT8_TYPE = 4;
  private static final int INT16_TYPE = 5;
  private static final int INT32_TYPE = 6;
  private static final int INT64_TYPE = 7;
  private static final int FLOAT32_TYPE = 8;
  private static final int FLOAT64_TYPE = 9;

  // Compression formats
  private static final int COMPRESSION_NONE = 0;
  private static final int COMPRESSION_BZIP2 = 1;
  private static final int COMPRESSION_ZLIB = 2;
  
  // -- Fields --

  private MetadataStore store;

  private int compressionType = 0;
  private double numBlocks = 1;
  private int[] dims_blockSize = new int[KLB_DATA_DIMS];
  private int[] dims_xyzct = new int[KLB_DATA_DIMS];
  private long[] blockOffsets;

  private long headerSize;
  private int blocksPerPlane;
  private long offsetFilePointer;
  private int headerVersion;

  private LinkedHashMap<String, String[][]> filelist = new LinkedHashMap<String, String[][]>();
  private ArrayList<Integer> channels = new ArrayList<Integer>();
  private static final String DEFAULT_SERIES = "Default";

  /** Prefixes indicating dimensions and projections */
  public static final String CHANNEL_PREFIX = "_CHN";
  public static final String TIME_PREFIX = ".TM";
  public static final String TIME_SUFFIX = "_timeFused";
  public static final String PROJECTION_PREFIX = "fusedStack_";
  public static final String PROJECTION_SUFFIX = "Projection";
  public static final Set<String> SERIES_PREFIXES = unmodifiableSet(
      new HashSet<String>(Arrays.asList("xy", "xz", "yz")));

  // -- Constructor --

  /**
   * Constructs a new BDV reader.
   */
  public KLBReader() {
    super("KLB", "klb");
    suffixSufficient = true;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    setGroupFiles(true);
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    in.close();
    String fileName;
    int[] currentCoords = getZCTCoords(no);
    int currentSeries = getSeries();

    Set<String> keys = filelist.keySet();
    fileName = filelist.get(keys.toArray()[currentSeries])[currentCoords[2]][currentCoords[1]];

    in = new RandomAccessInputStream(fileName);

    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    //As number of offsets can be greater than INT_MAX only storing enough required for given plane
    //New offsets are read from header each time openBytes is called
    reCalculateBlockOffsets(no);
    
    //Calculate block offsets for tiled reading
    int xBlockOffset = x % dims_blockSize[0];
    int yBlockOffset = y % dims_blockSize[1];
    int xBlockRemainder = dims_blockSize[0] - xBlockOffset;
    int yBlockRemainder = dims_blockSize[1] - yBlockOffset;
    int xNumBlocks = 1 + (int) (Math.ceil((float)(w - xBlockRemainder)/dims_blockSize[0]));
    int yNumBlocks = 1 + (int) (Math.ceil((float)(h - yBlockRemainder)/dims_blockSize[1]));
    int blocksPerImageRow = (int) (Math.ceil((float)getSizeX()/dims_blockSize[0]));
    int xBlockStartIndex = 0;
    if (x > 0) xBlockStartIndex = x / dims_blockSize[0];
    int yBlockStartIndex = 0;
    if (y > 0) yBlockStartIndex = y / dims_blockSize[1];

    int bytesPerPixel = FormatTools.getBytesPerPixel(getPixelType());
    
    int[]dimsBlock = new int[KLB_DATA_DIMS]; //Number of blocks on each dimension
    int[] coordBlock = new int[KLB_DATA_DIMS]; //Coordinates
    int[] blockSizeAux = new int[KLB_DATA_DIMS]; //Block sizes taking into account border cases

    for (int ii = 0; ii < KLB_DATA_DIMS; ii++)
    {
      dimsBlock[ii] = (int) Math.ceil((float)dims_xyzct[ii] / (float)dims_blockSize[ii]);
    }

    long compressedBlockSize = blockOffsets[1] - blockOffsets[0];
    int outputOffset = 0;

    for (int yy=0; yy < yNumBlocks; yy++) {
      for (int xx=0; xx < xNumBlocks; xx++) {

        //calculate coordinate (in block space)        
        int blockId = (yBlockStartIndex + yy) * blocksPerImageRow + xBlockStartIndex + xx;
        for (int ii = 0; ii < KLB_DATA_DIMS; ii++)
        {
          //parsing coordinates to image space (not block anymore)
          if (ii == 1) {
            coordBlock[1] = blockId / dimsBlock[0];
          }
          else {
            coordBlock[ii] = blockId % dimsBlock[ii];
          }
          coordBlock[ii] *= dims_blockSize[ii];
        }

        // Calculate block size in case we had border block
        blockSizeAux[0] = Math.min(dims_blockSize[0], (x + w - coordBlock[0]));
        blockSizeAux[0] = Math.min(blockSizeAux[0], coordBlock[0] + dims_blockSize[0] - x);
        blockSizeAux[1] = Math.min(dims_blockSize[1], (y + h - coordBlock[1]));
        blockSizeAux[1] = Math.min(blockSizeAux[1], coordBlock[1] + dims_blockSize[1] - y);
        for (int ii = 2; ii < KLB_DATA_DIMS; ii++) {
          blockSizeAux[ii] = Math.min(dims_blockSize[ii], (dims_xyzct[ii] - coordBlock[ii]));
        }

        int blockSizeBytes = bytesPerPixel;
        for (int ii = 0; ii < KLB_DATA_DIMS; ii++)
        {
          if (ii == 0 && coordBlock[ii] + blockSizeAux[ii] >= dims_xyzct[ii]) {
            blockSizeBytes *= blockSizeAux[0];
          }
          else {
            blockSizeBytes *= dims_blockSize[ii];
          }
        }
        
        compressedBlockSize = blockOffsets[blockId+1] - blockOffsets[blockId];
        long offset =  blockOffsets[blockId];
        //Seek to start of block
        in.seek((long) (headerSize + offset));

        //Read compressed block
        byte[] block = new byte[(int) compressedBlockSize];
        in.read(block);

        //Decompress block
        if (compressionType == COMPRESSION_BZIP2) {
          // Discard first two bytes of BZIP2 header
          byte[] tempPixels = block;
          block = new byte[tempPixels.length - 2];
          System.arraycopy(tempPixels, 2, block, 0, block.length);

          try {
            ByteArrayInputStream bais = new ByteArrayInputStream(block);
            CBZip2InputStream bzip = new CBZip2InputStream(bais);
            block = new byte[blockSizeBytes];
            bzip.read(block, 0, block.length);
            tempPixels = null;
            bais.close();
            bzip.close();
            bais = null;
            bzip = null;
          }
          catch(IOException e) {
            LOGGER.error("IOException while decompressing block {}", xx);
            throw e;
          }
        }
        else if (compressionType == COMPRESSION_ZLIB) {
          CodecOptions options = new CodecOptions();
          block = new ZlibCodec().decompress(block, options);
        }

        try {
          int imageRowSize = w * bytesPerPixel;
          int blockRowSize = blockSizeAux[0] * bytesPerPixel;
          int fullBlockRowSize = dims_blockSize[0] * bytesPerPixel;

          // Location in output buffer to copy block
          outputOffset = (imageRowSize * (coordBlock[1] - y)) + ((coordBlock[0] - x) * bytesPerPixel);
          if (coordBlock[0] < x && blockSizeAux[0] != dims_blockSize[0]) outputOffset += (dims_blockSize[0] - blockSizeAux[0]) * bytesPerPixel;
          if (coordBlock[1] < y && blockSizeAux[1] != dims_blockSize[1]) outputOffset = (coordBlock[0] - x) * bytesPerPixel;
          if (coordBlock[1] < y && coordBlock[0] < x && blockSizeAux[1] != dims_blockSize[1] && blockSizeAux[0] != dims_blockSize[0]) outputOffset = 0;

          // Location within the block for required XY plane
          int inputOffset = (coordBlock[2] % dims_blockSize[2]) * blockRowSize * blockSizeAux[1];
          if (coordBlock[0] < x && coordBlock[1] < y && blockSizeAux[1] != dims_blockSize[1] && blockSizeAux[0] != dims_blockSize[0]) inputOffset += ((dims_blockSize[0] * (dims_blockSize[1] - blockSizeAux[1])) + (x - coordBlock[0])) * bytesPerPixel;
          // Partial block at the start of x tile
          else if (coordBlock[0] < x && blockSizeAux[0] != dims_blockSize[0]) inputOffset += (dims_blockSize[0] - blockSizeAux[0]) * bytesPerPixel;
          // Partial block at the start of y tile
          else if (coordBlock[1] < y && blockSizeAux[1] != dims_blockSize[1] && coordBlock[0] + blockSizeAux[0] == dims_xyzct[0]) inputOffset += blockSizeAux[0] * (dims_blockSize[1] - blockSizeAux[1]) * bytesPerPixel;
          else if (coordBlock[1] < y && blockSizeAux[1] != dims_blockSize[1]) inputOffset += dims_blockSize[0] * (dims_blockSize[1] - blockSizeAux[1]) * bytesPerPixel;
          
          inputOffset += (coordBlock[3] % dims_blockSize[3]) * blockRowSize * blockSizeAux[1] * blockSizeAux[2];
          inputOffset += (coordBlock[4] % dims_blockSize[4]) * blockRowSize * blockSizeAux[1] * blockSizeAux[2] * blockSizeAux[3];

          // If its the last block in a row then use the corrected rowSize
          if (coordBlock[0] + blockSizeAux[0] == dims_xyzct[0]) {
            fullBlockRowSize = blockRowSize;
          }
          // Copy row at a time from decompressed block to output buffer
          for (int numRows = 0; numRows < blockSizeAux[1]; numRows++) {
            int destPos = outputOffset + (numRows * imageRowSize);
            if (destPos + blockRowSize <= buf.length) {
              System.arraycopy(block, inputOffset + (numRows * fullBlockRowSize), buf, destPos, blockRowSize);
            }
          }
        }
        catch(Exception e) {
          throw new FormatException("Exception caught while copying decompressed block data to output buffer : " + e);
        }
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    store = makeFilterMetadata();
    in = new RandomAccessInputStream(id);

    int sizeT = 0;
    int sizeC = 1;

    String basePrefix;
    String parent = new Location(id).getAbsoluteFile().getParent();
    File folder = new File(parent);
    File[] listOfFiles = folder.listFiles();
    if (isGroupFiles() && id.indexOf(CHANNEL_PREFIX) >= 0) {
      basePrefix = id.substring(id.lastIndexOf(File.separator) + 1, id.indexOf(CHANNEL_PREFIX));
      for (int i=0; i < listOfFiles.length; i++) {
        String fileName = listOfFiles[i].getName();
        if (fileName.contains(basePrefix)) {
          int channelNum = DataTools.parseInteger(fileName.substring(fileName.indexOf(CHANNEL_PREFIX)+CHANNEL_PREFIX.length(), fileName.indexOf('.')));
          if (!channels.contains(channelNum)) {
            channels.add(channelNum);
          }
        }
      }

      if (channels.size() > 0) {
        sizeC = channels.size();
        Collections.sort(channels);
      }

      String topLevelFolder = new Location(parent).getAbsoluteFile().getParent();
      folder = new File(topLevelFolder);
      listOfFiles = folder.listFiles();
      basePrefix = parent.substring(parent.lastIndexOf(File.separator)+1, parent.lastIndexOf('.'));

      for (int i=0; i < listOfFiles.length; i++) {
        String fileName = listOfFiles[i].getName();
        if (fileName.startsWith(basePrefix)) {
          sizeT++;
        }
      }
    }

    if (isGroupFiles() && sizeT > 0) {
      filelist.put(DEFAULT_SERIES, new String[sizeT][sizeC]);
      basePrefix = parent.substring(parent.lastIndexOf(File.separator)+1, parent.lastIndexOf('.'));
      Arrays.sort(listOfFiles);
      for (int i=0; i < listOfFiles.length; i++) {
        String fileName = listOfFiles[i].getName();
        if (fileName.startsWith(basePrefix)) {
          String timepointString = fileName.substring(fileName.indexOf(TIME_PREFIX)+TIME_PREFIX.length(), fileName.indexOf(TIME_SUFFIX));
          int currentTimepoint = DataTools.parseInteger(timepointString);
          File[] innerFileList = listOfFiles[i].listFiles();
          Arrays.sort(innerFileList);
          for (int j=0; j < innerFileList.length; j++) {
            String innerFileName = innerFileList[j].getName();
            if (innerFileName.contains(PROJECTION_PREFIX.substring(0, PROJECTION_PREFIX.length()-1))) {
              String channelNumString = innerFileName.substring(innerFileName.indexOf(CHANNEL_PREFIX)+CHANNEL_PREFIX.length(), innerFileName.indexOf('.'));
              int currentChannelNum = DataTools.parseInteger(channelNumString);
              int channelIndex = channels.indexOf(currentChannelNum);
              if (innerFileName.indexOf(PROJECTION_PREFIX) >= 0) {
                String projection = innerFileName.substring(innerFileName.indexOf(PROJECTION_PREFIX)+PROJECTION_PREFIX.length(), innerFileName.indexOf(PROJECTION_SUFFIX));
                if (SERIES_PREFIXES.contains(projection)) {
                  if (filelist.get(projection) == null) {
                    filelist.put(projection, new String[sizeT][sizeC]);
                    core.add(new CoreMetadata(this, 0));
                  }
                  filelist.get(projection)[currentTimepoint][channelIndex] = innerFileList[j].getAbsolutePath();
                  if (currentTimepoint == 0 && channelIndex == 0) {
                    in.close();
                    in = new RandomAccessInputStream(innerFileList[j].getAbsolutePath());
                    List<String> stringsList = new ArrayList<>(filelist.keySet());
                    readHeader(core.get(stringsList.indexOf(projection)));
                  }
                }
              }
              else {
                filelist.get(DEFAULT_SERIES)[currentTimepoint][channelIndex] = innerFileList[j].getAbsolutePath();
                if (currentTimepoint == 0 && channelIndex == 0) {
                  in.close();
                  in = new RandomAccessInputStream(innerFileList[j].getAbsolutePath());
                  readHeader(core.get(0));
                }
              }
            }
          }
        }
      }
    }
    else {
      //Dealing with a single file
      filelist.put(DEFAULT_SERIES, new String[1][1]);
      String absolutePath = new Location(id).getAbsolutePath();
      filelist.get(DEFAULT_SERIES)[0][0] = absolutePath;
      in = new RandomAccessInputStream(absolutePath);
      readHeader(core.get(0));
    }

    MetadataTools.populatePixels(store, this);
  }

  private void readHeader(CoreMetadata coreMeta) throws IOException, FormatException {
    headerVersion = in.readUnsignedByte();
    coreMeta.littleEndian = true;
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_xyzct[i] = readUInt32();
    }
    coreMeta.dimensionOrder = "XYZCT";

    coreMeta.sizeX = dims_xyzct[0];
    coreMeta.sizeY = dims_xyzct[1];
    coreMeta.sizeZ = dims_xyzct[2];
    if (!isGroupFiles() && filelist.size() > 1) {
      coreMeta.sizeC = dims_xyzct[3];
      coreMeta.sizeT = dims_xyzct[4];
    }
    else {
      coreMeta.sizeT = filelist.get(DEFAULT_SERIES).length;
      coreMeta.sizeC = filelist.get(DEFAULT_SERIES)[0].length;
    }
    coreMeta.imageCount = coreMeta.sizeZ * coreMeta.sizeC * coreMeta.sizeT;

    PositiveFloat[] dims_pixelSize = new PositiveFloat[KLB_DATA_DIMS];
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_pixelSize[i] = readFloat32();
    }
    store.setPixelsPhysicalSizeX(FormatTools.createLength(dims_pixelSize[0], UNITS.MICROMETER), 0);
    store.setPixelsPhysicalSizeY(FormatTools.createLength(dims_pixelSize[1], UNITS.MICROMETER), 0);
    store.setPixelsPhysicalSizeZ(FormatTools.createLength(dims_pixelSize[2], UNITS.MICROMETER), 0);

    int dataType = in.readUnsignedByte();
    convertPixelType(coreMeta, dataType);

    compressionType = in.readUnsignedByte();
    byte[] user_metadata = new byte[KLB_METADATA_SIZE];
    in.read(user_metadata);

    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_blockSize[i] = readUInt32();
    }
    blocksPerPlane = (int) (Math.ceil((float)coreMeta.sizeX/dims_blockSize[0]) * Math.ceil((float)coreMeta.sizeY/dims_blockSize[1]));
    numBlocks = 1;
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      numBlocks *= Math.ceil((float)(dims_xyzct[i]) / (float)(dims_blockSize[i]));
    }

    headerSize = (long) ((KLB_DATA_DIMS * 12) + 2 + (numBlocks * 8) + KLB_METADATA_SIZE + 1);
    blockOffsets = new long[blocksPerPlane];

    offsetFilePointer = in.getFilePointer();
    for (int i=0; i < blocksPerPlane; i++) {
      blockOffsets[i] = readUInt64();
    }
  }
  
  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    String [] completeFileList = new String[getSizeT() * getSizeC() * filelist.size()];
    int index = 0;
    for (String seriesKey: filelist.keySet()) {
      String[][] seriesFiles = filelist.get(seriesKey);
      for (int timepoint = 0; timepoint < getSizeT(); timepoint++) {
        for (int channel = 0; channel < getSizeC(); channel++) {
          completeFileList[index] = seriesFiles[timepoint][channel];
          index ++;
        }
      }
    }
    return noPixels ? ArrayUtils.EMPTY_STRING_ARRAY : completeFileList;
  }
  
  // Needed as offsets array can only be int max and full image may be greater
  private void reCalculateBlockOffsets(int no) throws IOException, FormatException {
    LOGGER.debug("Beginning calulating offsets for plane : " + no);
    headerVersion = in.readUnsignedByte();
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_xyzct[i] = readUInt32();
    }

    PositiveFloat[] dims_pixelSize = new PositiveFloat[KLB_DATA_DIMS];
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_pixelSize[i] = readFloat32();
    }
    int dataType = in.readUnsignedByte();
    compressionType = in.readUnsignedByte();
    byte[] user_metadata = new byte[KLB_METADATA_SIZE];
    in.read(user_metadata);

    for (int i=0; i < KLB_DATA_DIMS; i++) {
      dims_blockSize[i] = readUInt32();
    }
    blocksPerPlane = (int) (Math.ceil((float)getSizeX()/dims_blockSize[0]) * Math.ceil((float)getSizeY()/dims_blockSize[1]));
    
    numBlocks = 1;
    for (int i=0; i < KLB_DATA_DIMS; i++) {
      numBlocks *= Math.ceil((float)(dims_xyzct[i]) / (float)(dims_blockSize[i]));
    }

    headerSize = (long) ((KLB_DATA_DIMS * 12) + 2 + (numBlocks * 8) + KLB_METADATA_SIZE + 1);
    
    String order = core.get(getSeries()).dimensionOrder;
    int[] ztc = FormatTools.getZCTCoords(order, getSizeZ(), getSizeC(), getSizeT(), getImageCount(), no);

    // Calculate the first required block
    int requiredBlockNum = (ztc[0] / dims_blockSize[2]);

    // Mark the current file pointer to return after reading offsets
    long filePoointer = in.getFilePointer();

    // Seek to start of offsets and read required offsets
    blockOffsets = new long[blocksPerPlane + 1];
    in.seek(offsetFilePointer + (requiredBlockNum * blocksPerPlane * 8));
    for (int i=0; i < blocksPerPlane; i++) {
      blockOffsets[i+1] = readUInt64();
    }

    // If not the first plane then the first offset needs to be calculated as last of previous plane
    if (requiredBlockNum > 0) {
      in.seek(offsetFilePointer + (requiredBlockNum * blocksPerPlane * 8) - 8);
      blockOffsets[0] = readUInt64();
    } else {
      blockOffsets[0] = 0;
    }
    in.seek(filePoointer);
  }

  // Helper methods

  private void convertPixelType(CoreMetadata ms0, int pixelType) throws FormatException {
    switch (pixelType) {
      case UINT8_TYPE:
        ms0.pixelType = FormatTools.UINT8;
        break;
      case UINT16_TYPE:
        ms0.pixelType = FormatTools.UINT16;
        break;
      case UINT32_TYPE:
        ms0.pixelType = FormatTools.UINT32;
        break;
      case UINT64_TYPE:
      case INT64_TYPE:
        ms0.pixelType = FormatTools.DOUBLE;
        break;
      case INT8_TYPE:
        ms0.pixelType = FormatTools.INT8;
        break;
      case INT16_TYPE:
        ms0.pixelType = FormatTools.INT16;
        break;
      case INT32_TYPE:
        ms0.pixelType = FormatTools.INT32;
        break;
      case FLOAT32_TYPE:
      case FLOAT64_TYPE:
        ms0.pixelType = FormatTools.FLOAT;
        break;
      default:
        throw new FormatException("Unknown pixel type: " + pixelType);
    }
    ms0.interleaved = ms0.rgb;
  }

  private int readUInt32() throws IOException {
    byte[] b = new byte[4];
    in.read(b, 0, 4);
    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
    return bb.getInt();
  }

  private long readUInt64() throws IOException {
    byte[] b = new byte[8];
    in.read(b, 0, 8);
    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
    return bb.getLong();
  }

  private PositiveFloat readFloat32() throws IOException {
    byte[] b = new byte[4];
    in.read(b, 0, 4);
    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
    return new PositiveFloat((double) bb.getFloat());
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    filelist.clear();
    Arrays.fill(dims_blockSize, 0);
    Arrays.fill(dims_xyzct, 0);
    blockOffsets = null;
    compressionType = 0;
    numBlocks = 1;
    headerSize = 0;
    blocksPerPlane = 0;
    offsetFilePointer = 0;
    headerVersion = 0;    
  }

}
