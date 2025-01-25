/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2024 Open Microscopy Environment:
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
import java.util.Arrays;
import java.util.List;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * DCIMGReader reads Hamamatsu DCIMG files.
 * 
 * Follows spec/code at https://github.com/python-microscopy/python-microscopy/blob/master/PYME/IO/dcimg.py
 * and https://github.com/lens-biophotonics/dcimg/blob/master/dcimg.py.
 * 
 */
public class DCIMGReader extends FormatReader {

  // -- Constants --

  private static final boolean IS_LITTLE = true;

  private static final long DCIMG_VERSION_0 = 0x7;
  private static final long DCIMG_VERSION_1 = 0x1000000;

  private static final long DCIMG_PIXELTYPE_MONO8 = 0x00000001;
  private static final long DCIMG_PIXELTYPE_MONO16 = 0x00000002;

  // -- Fields --

  private long version;
  private long headerSize;
  private long dataOffset;
  private long pixelType;
  private long bytesPerImage;
  private long bytesPerRow;
  private long offsetToFooter;
  private boolean fourPixelCorrectionInFooter = false;
  private long offsetToFourPixels;
  private long fourPixelOffsetInFrame;
  private int fourPixelCorrectionLine;
  private long fourPixelCorrectionOffset;
  private long currentStreamPosition;
  private long frameFooterSize;

  private List<String> companionFiles = new ArrayList<String>();
  private String[] uniqueFiles;

  public DCIMGReader() 
  {
    super("Hamamatsu DCIMG", "dcimg");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  @Override
  public boolean isSingleFile(String id) 
    throws FormatException, IOException 
  {
    return false;
  }

  @Override
  public int fileGroupOption(String id) 
    throws FormatException, IOException 
  {
    return FormatTools.CAN_GROUP;
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) 
  {
    FormatTools.assertId(currentId, true, 1);

    return uniqueFiles;

  }

  @Override
  public boolean isThisType(RandomAccessInputStream stream) 
    throws IOException 
  {
    String desc = stream.readString(5);

    return desc.equals("DCIMG");
  }
  
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) 
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int zp = no / getSizeT();
    int tp = no % getSizeT();

    try (RandomAccessInputStream stream = new RandomAccessInputStream(uniqueFiles[zp])) {
      stream.order(IS_LITTLE);

      // DCIMG is stored column major
      int byteFactor = FormatTools.getBytesPerPixel(getPixelType());
      stream.seek(headerSize + dataOffset + tp*bytesPerImage + byteFactor*y*getSizeX());
      for (int row=h-1; row>=0; row--) {
        if (fourPixelCorrectionInFooter && (row == fourPixelCorrectionLine) && (x < 4)) {

          // mark the current position, as we want to pick up from here
          currentStreamPosition = stream.getFilePointer();

          // go get the four pixel offset
          stream.seek(fourPixelCorrectionOffset + byteFactor*x);
          stream.read(buf, byteFactor*row*w, byteFactor*(4-x));

          // go back to our current position, plus four pixels
          stream.seek(currentStreamPosition + byteFactor*4);

          // continue reading
          stream.read(buf, byteFactor*(row*w+4), byteFactor*(w-4));
        } else {
          stream.skipBytes(byteFactor*x);
          stream.read(buf, byteFactor*row*w, byteFactor*w);
        }
        stream.skipBytes(byteFactor*(getSizeX() - w - x));
      }
    }

    return buf;
  }

  @Override
  protected void initFile(String id)
    throws FormatException, IOException
  {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    in.order(IS_LITTLE);  // little endian 

    // confirm this is DCIMG
    if (!isThisType(in)) throw new FormatException("Not a valid DCIMG file.");

    in.seek(8);

    version = in.readUnsignedInt();  // DCIMG version number
    LOGGER.info("DCIMG Version: {}", version);
    if ((!(version == DCIMG_VERSION_0)) && (!(version >= DCIMG_VERSION_1))) {
      throw new FormatException(String.format("Unknown DCIMG version number %d.", version));
    }

    if (version > DCIMG_VERSION_1) {
      LOGGER.warn(String.format("Your file is DCAM version %d, but only %d is guaranteed to work.", version, DCIMG_VERSION_1));
    }

    // in.skipBytes(20);

    // in.skipBytes(4);  // long numSessions = in.readUnsignedInt();
    // in.skipBytes(4);  // long numFrames = in.readUnsignedInt();
    in.skipBytes(28);
    headerSize = in.readUnsignedInt();
    in.skipBytes(4);
    long fileSize = in.readUnsignedInt();
    in.skipBytes(12);
    long fileSize2 = in.readUnsignedInt();
    if (fileSize != fileSize2) throw new FormatException("Improper header. File sizes do not match.");
    in.skipBytes(16);
    // long mystery1 = in.readUnsignedInt();  // 1024 in all examples

    CoreMetadata m = core.get(0);

    m.dimensionOrder = "XYZCT";
    m.rgb = false;
    m.interleaved = false;
    m.littleEndian = IS_LITTLE;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;
    m.thumbnail = false;
    m.sizeC = 1;  // AFAIK DCIMG are always grayscale

    // One shot header reading to avoid lots of control flow statements (because of
    // the different DCIMG versions). Unfortunately, a bit less clean than parsing
    // each parameter individually.
    if (version == DCIMG_VERSION_0) {
      parseDCAMVersion0Header(in);
      parseDCAMVersion0Footer(in);
    } else if (version == DCIMG_VERSION_1) {
      parseDCAMVersion1Header(in);
    }

    if (pixelType == DCIMG_PIXELTYPE_MONO8) {
      m.pixelType = FormatTools.UINT8;
    } else if (pixelType == DCIMG_PIXELTYPE_MONO16) {
      m.pixelType = FormatTools.UINT16;
    } else {
      throw new FormatException(String.format("Unknown pixel type %d.", pixelType));
    }

    // DCIMG sometimes stores the first 4 pixels of one of its lines somewhere separate
    // from the rest of the data.
    fourPixelCorrectionLine = getFourPixelCorrectionLine();
    fourPixelCorrectionOffset = getFourPixelCorrectionOffset();

    // Make the assumption that all files in the group have the same header
    if (isGroupFiles()) {
      Location currentFile = new Location(id).getAbsoluteFile();
      Location directory = currentFile.getParentFile();
      scanDirectory(directory);
    } else {
      String file = new Location(id).getAbsolutePath();
      companionFiles.add(file);
    }

    uniqueFiles = companionFiles.toArray(new String[companionFiles.size()]);
    m.sizeZ = uniqueFiles.length;
    m.imageCount = getSizeZ() * getSizeT() * getSizeC();

    LOGGER.info("sizeX: {} sizeY: {} sizeZ: {} sizeC: {} sizeT: {}", m.sizeX, m.sizeY, m.sizeZ, m.sizeC, m.sizeT);

    addGlobalMeta("Version", version);

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    in.close();

  }

  // Logic copied from DicomReader 
  private void scanDirectory(Location dir)
    throws FormatException, IOException 
  {
    String[] files = dir.list(true);
    if (files == null) return;
    Arrays.sort(files);
    for (String f : files) {
      String file = new Location(dir, f).getAbsolutePath();
      LOGGER.info("Checking file {}", file);
      addFileToList(file);
    }
  }

  // Logic copied from DicomReader 
  private void addFileToList(String file)
    throws FormatException, IOException
  {
    if (!checkSuffix(file, "dcimg")) {
      LOGGER.warn("File {} failed extension check. Does not end in 'dcimg'.", file);
      return;
    }
    try (RandomAccessInputStream stream = new RandomAccessInputStream(file)) {
      if (!isThisType(stream)) {
        LOGGER.warn("File {} is not DCIMG.", file);
        return;
      }
      // stream.order(IS_LITTLE);
      // TODO: now check width/height
      companionFiles.add(file);
    }
  }

  // The header and footer code feature many commented out properties "for later"
  private void parseDCAMVersion0Header(RandomAccessInputStream stream)
    throws IOException 
  {
    CoreMetadata m = core.get(0);

    stream.seek(headerSize);
    // long sessionLength = stream.readLong();
    // stream.skipBytes(24);
    stream.skipBytes(32);
    m.sizeT = stream.readInt();
    pixelType = stream.readInt();
    stream.skipBytes(4);  // long mystery1 = stream.readUnsignedInt();
    m.sizeX = stream.readInt();  // num columns (this is a column-major format)
    bytesPerRow = stream.readUnsignedInt();
    m.sizeY = stream.readInt();  // num rows
    bytesPerImage = stream.readUnsignedInt();
    stream.skipBytes(8);
    dataOffset = stream.readInt();
    offsetToFooter = stream.readLong();
  }

  private void parseDCAMVersion1Header(RandomAccessInputStream stream)
    throws IOException 
  {
    CoreMetadata m = core.get(0);

    stream.seek(headerSize);
    // stream.skipBytes(8);  // long sessionLength = stream.readLong();
    // stream.skipBytes(52);  // unknown numbers 1, 144, 65537
    stream.skipBytes(60);
    m.sizeT = stream.readInt();
    pixelType = stream.readInt();
    stream.skipBytes(4); // long mystery1 = stream.readUnsignedInt();
    m.sizeX = stream.readInt();  // num columns (this is a column-major format)
    m.sizeY = stream.readInt();  // num rows
    stream.skipBytes(4); // long bytesPerRow = stream.readUnsignedInt();
    bytesPerImage = stream.readUnsignedInt();
    stream.skipBytes(8);
    dataOffset = stream.readLong();
    stream.skipBytes(20);
    frameFooterSize = stream.readUnsignedInt();
  }

  private void parseDCAMVersion0Footer(RandomAccessInputStream stream) 
    throws IOException, FormatException
  {
    // Go to the first footer and find out where the second footer is
    long footerOffset = headerSize + offsetToFooter;
    stream.seek(footerOffset);
    long footerVersion = stream.readUnsignedInt();
    if (version != footerVersion) {
      throw new FormatException(String.format("Header DCIMG version %d does not match footer version %d.", footerVersion, version));
    }
    stream.skipBytes(4);
    long secondFooterOffset = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToOffsetToEndOfData = stream.readLong();
    // stream.skipBytes(8);
    // long footerSize = stream.readUnsignedInt();
    // stream.skipBytes(4);
    // long secondFooterSize = stream.readUnsignedInt();
    // stream.skipBytes(76);
    // long offsetToEndOfData = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToEndOfData2 = stream.readLong();
    // stream.skipBytes(8);

    // Go to the second footer and get information about the 4px offset
    stream.seek(footerOffset+secondFooterOffset);
    // long offsetToOffsetToTimestamps = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToOffsetToFrameCounts = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToOffsetToFourPixels = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToFrameCounts = stream.readLong();
    // stream.skipBytes(8);
    // long offsetToTimestamps = stream.readLong();
    // stream.skipBytes(16);
    stream.skipBytes(88);
    offsetToFourPixels = stream.readLong();
    stream.skipBytes(4);
    fourPixelOffsetInFrame = stream.readUnsignedInt();
    long fourPixelSize = stream.readLong();
    if (fourPixelSize > 0) {
      fourPixelCorrectionInFooter = true;
    }
  }

  // Get the correct line and offset for the 4 pixel correction
  private int getFourPixelCorrectionLine() 
  {
    if (version == DCIMG_VERSION_0) {
      if (fourPixelCorrectionInFooter) {
        return ((int)(fourPixelOffsetInFrame / bytesPerRow + 1));  // TODO: Why do we need the +1?
      } else {
        return (getSizeY() - 1);
      }
    }
    if (version == DCIMG_VERSION_1) {
      if ((frameFooterSize >= 512) | (frameFooterSize == 32)){
        fourPixelCorrectionInFooter = true;
      }

      // TODO: This is a guess because the spec in 
      // https://github.com/lens-biophotonics/dcimg/blob/master/dcimg.py
      // resulted in a div by zero on my example file.
      if ((getSizeY() % 2) == 0) {
        return (getSizeY() / 2);
      } else {
        return (getSizeY() / 2 + 1);
      }
    }
    return 0;
  }

  private long getFourPixelCorrectionOffset()
  {
    if (version == DCIMG_VERSION_0) {
      return headerSize + offsetToFooter + offsetToFourPixels;
    }
    return headerSize + dataOffset + bytesPerImage + 12;
  }

  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      version = 0;
      headerSize = 0;
      dataOffset = 0;
      pixelType = 0;
      bytesPerImage = 0;
      bytesPerRow = 0;
      offsetToFooter = 0;
      fourPixelCorrectionInFooter = false;
      offsetToFourPixels = 0;
      fourPixelOffsetInFrame = 0;
      fourPixelCorrectionLine = 0;
      fourPixelCorrectionOffset = 0;
      currentStreamPosition = 0;
      frameFooterSize = 0;
      
      companionFiles.clear();
      uniqueFiles = null;
    }
  }

}