/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * CellSensReader is the file format reader for cellSens .vsi files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CellSensReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CellSensReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CellSensReader extends FormatReader {

  // -- Constants --

  // Compression types
  private static final int RAW = 0;
  private static final int JPEG = 2;
  private static final int JPEG_2000 = 3;
  private static final int PNG = 8;
  private static final int BMP = 9;

  // Pixel types
  private static final int CHAR = 1;
  private static final int UCHAR = 2;
  private static final int SHORT = 3;
  private static final int USHORT = 4;
  private static final int INT = 5;
  private static final int UINT = 6;
  private static final int LONG = 7;
  private static final int ULONG = 8;
  private static final int FLOAT = 9;
  private static final int DOUBLE = 10;

  // Simple field types
  private static final int COMPLEX = 11;
  private static final int BOOLEAN = 12;
  private static final int TCHAR = 13;
  private static final int DWORD = 14;
  private static final int TIMESTAMP = 17;
  private static final int DATE = 18;
  private static final int INT_2 = 256;
  private static final int INT_3 = 257;
  private static final int INT_4 = 258;
  private static final int INT_RECT = 259;
  private static final int DOUBLE_2 = 260;
  private static final int DOUBLE_3 = 261;
  private static final int DOUBLE_4 = 262;
  private static final int DOUBLE_RECT = 263;
  private static final int DOUBLE_2_2 = 264;
  private static final int DOUBLE_3_3 = 265;
  private static final int DOUBLE_4_4 = 266;
  private static final int INT_INTERVAL = 267;
  private static final int DOUBLE_INTERVAL = 268;
  private static final int RGB = 269;
  private static final int BGR = 270;
  private static final int FIELD_TYPE = 271;
  private static final int MEM_MODEL = 272;
  private static final int COLOR_SPACE = 273;
  private static final int INT_ARRAY_2 = 274;
  private static final int INT_ARRAY_3 = 275;
  private static final int INT_ARRAY_4 = 276;
  private static final int INT_ARRAY_5 = 277;
  private static final int DOUBLE_ARRAY_2 = 279;
  private static final int DOUBLE_ARRAY_3 = 280;
  private static final int UNICODE_TCHAR = 8192;
  private static final int DIM_INDEX_1 = 8195;
  private static final int DIM_INDEX_2 = 8199;
  private static final int VOLUME_INDEX = 8200;
  private static final int PIXEL_INFO_TYPE = 8470;

  // Extended field types
  private static final int NEW_VOLUME_HEADER = 0;
  private static final int PROPERTY_SET_VOLUME = 1;
  private static final int NEW_MDIM_VOLUME_HEADER = 2;
  private static final int TIFF_IFD = 10;
  private static final int VECTOR_DATA = 11;

  // -- Fields --

  private String[] usedFiles;

  private TiffParser parser;
  private IFDList ifds;

  private Long[][] tileOffsets;
  private boolean jpeg = false;

  private int[] rows, cols;
  private int[] compressionType;
  private int[] tileX, tileY;

  private HashMap<TileCoordinate, Integer>[] tileMap;
  private int[] nDimensions;
  private boolean inDimensionProperties = false;
  private boolean foundChannelTag = false;
  private int dimensionTag;

  private HashMap<String, Integer> dimensionOrdering =
    new HashMap<String, Integer>();

  // -- Constructor --

  /** Constructs a new cellSens reader. */
  public CellSensReader() {
    super("CellSens VSI", new String[] {"vsi", "ets"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = true;
    datasetDescription = "One .vsi file and an optional directory with a " +
      "similar name that contains at least one subdirectory with .ets files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    return usedFiles;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    if (getCoreIndex() < core.length - ifds.size()) {
      return tileX[getCoreIndex()];
    }
    int ifdIndex = getCoreIndex() - (usedFiles.length - 1);
    try {
      return (int) ifds.get(ifdIndex).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    if (getCoreIndex() < core.length - ifds.size()) {
      return tileY[getCoreIndex()];
    }
    int ifdIndex = getCoreIndex() - (usedFiles.length - 1);
    try {
      return (int) ifds.get(ifdIndex).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /* @see loci.formats.IFormatHandler#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    int currentSeries = getSeries();
    int thumbSize = getThumbSizeX() * getThumbSizeY() *
      FormatTools.getBytesPerPixel(getPixelType()) * getRGBChannelCount();

    if (getCoreIndex() >= usedFiles.length - 1 ||
      usedFiles.length >= core.length)
    {
      return super.openThumbBytes(no);
    }

    setSeries(usedFiles.length);
    byte[] thumb = FormatTools.openThumbBytes(this, 0);
    setSeries(currentSeries);
    if (thumb.length == thumbSize) {
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (getCoreIndex() < core.length - ifds.size()) {
      int tileRows = rows[getCoreIndex()];
      int tileCols = cols[getCoreIndex()];

      Region image = new Region(x, y, w, h);
      int outputRow = 0, outputCol = 0;
      Region intersection = null;

      byte[] tileBuf = null;
      int pixel =
        getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());
      int outputRowLen = w * pixel;

      for (int row=0; row<tileRows; row++) {
        for (int col=0; col<tileCols; col++) {
          int width = tileX[getCoreIndex()];
          int height = tileY[getCoreIndex()];
          Region tile = new Region(col * width, row * height, width, height);
          if (!tile.intersects(image)) {
            continue;
          }

          intersection = tile.intersection(image);
          int intersectionX = 0;

          if (tile.x < image.x) {
            intersectionX = image.x - tile.x;
          }

          tileBuf = decodeTile(no, row, col);

          int rowLen = pixel * (int) Math.min(intersection.width, width);

          int outputOffset = outputRow * outputRowLen + outputCol;
          for (int trow=0; trow<intersection.height; trow++) {
            int realRow = trow + intersection.y - tile.y;
            int inputOffset = pixel * (realRow * width + intersectionX);
            System.arraycopy(tileBuf, inputOffset, buf, outputOffset, rowLen);
            outputOffset += outputRowLen;
          }

          outputCol += rowLen;
        }

        if (intersection != null) {
          outputRow += intersection.height;
          outputCol = 0;
        }
      }

      return buf;
    }
    else {
      int ifdIndex = getCoreIndex() - (usedFiles.length - 1);
      return parser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (parser != null && parser.getStream() != null) {
        parser.getStream().close();
      }
      parser = null;
      ifds = null;
      usedFiles = null;
      tileOffsets = null;
      jpeg = false;
      rows = null;
      cols = null;
      compressionType = null;
      tileX = null;
      tileY = null;
      tileMap = null;
      nDimensions = null;
      inDimensionProperties = false;
      foundChannelTag = false;
      dimensionTag = 0;
      dimensionOrdering.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (!checkSuffix(id, "vsi")) {
      Location current = new Location(id).getAbsoluteFile();
      Location parent = current.getParentFile();
      parent = parent.getParentFile();
      Location grandparent = parent.getParentFile();
      String vsi = parent.getName();
      vsi = vsi.substring(1, vsi.length() - 1) + ".vsi";

      Location vsiFile = new Location(grandparent, vsi);
      if (!vsiFile.exists()) {
        throw new FormatException("Could not find .vsi file.");
      }
      else {
        id = vsiFile.getAbsolutePath();
      }
    }

    parser = new TiffParser(id);
    ifds = parser.getIFDs();

    RandomAccessInputStream vsi = new RandomAccessInputStream(id);
    vsi.order(parser.getStream().isLittleEndian());
    vsi.seek(8);
    readTags(vsi);
    vsi.seek(parser.getStream().getFilePointer());

    vsi.skipBytes(273);

    ArrayList<String> files = new ArrayList<String>();
    Location file = new Location(id).getAbsoluteFile();

    Location dir = file.getParentFile();

    String name = file.getName();
    name = name.substring(0, name.lastIndexOf("."));

    Location pixelsDir = new Location(dir, "_" + name + "_");
    String[] stackDirs = pixelsDir.list(true);
    if (stackDirs != null) {
      for (String f : stackDirs) {
        Location stackDir = new Location(pixelsDir, f);
        String[] pixelsFiles = stackDir.list(true);
        if (pixelsFiles != null) {
          for (String pixelsFile : pixelsFiles) {
            if (checkSuffix(pixelsFile, "ets")) {
              files.add(new Location(stackDir, pixelsFile).getAbsolutePath());
            }
          }
        }
      }
    }
    files.add(file.getAbsolutePath());
    usedFiles = files.toArray(new String[files.size()]);

    core = new CoreMetadata[files.size() - 1 + ifds.size()];

    tileOffsets = new Long[files.size() - 1][];
    rows = new int[files.size() - 1];
    cols = new int[files.size() - 1];
    nDimensions = new int[core.length];

    IFDList exifs = parser.getExifIFDs();

    compressionType = new int[core.length];
    tileX = new int[core.length];
    tileY = new int[core.length];
    tileMap = new HashMap[core.length];

    for (int s=0; s<core.length; s++) {
      core[s] = new CoreMetadata();
    }

    for (int s=0; s<core.length; s++) {
      tileMap[s] = new HashMap<TileCoordinate, Integer>();

      if (s == 0) {
        core[s].resolutionCount = ifds.size() + (files.size() == 1 ? 0 : 1);
      }

      if (s < files.size() - 1) {
        setSeries(s);
        parseETSFile(files.get(s), s);

        core[s].littleEndian = compressionType[s] == RAW;
        core[s].interleaved = core[s].rgb;

        if (s == 0 && exifs.size() > 0) {
          IFD exif = exifs.get(0);

          int newX = exif.getIFDIntValue(IFD.PIXEL_X_DIMENSION);
          int newY = exif.getIFDIntValue(IFD.PIXEL_Y_DIMENSION);

          if (getSizeX() > newX || getSizeY() > newY) {
            core[s].sizeX = newX;
            core[s].sizeY = newY;
          }
        }

        setSeries(0);
      }
      else {
        IFD ifd = ifds.get(s - files.size() + 1);
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        core[s].rgb = samples > 1 || p == PhotoInterp.RGB;
        core[s].sizeX = (int) ifd.getImageWidth();
        core[s].sizeY = (int) ifd.getImageLength();
        core[s].sizeZ = 1;
        core[s].sizeT = 1;
        core[s].sizeC = core[s].rgb ? samples : 1;
        core[s].littleEndian = ifd.isLittleEndian();
        core[s].indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        core[s].imageCount = 1;
        core[s].pixelType = ifd.getPixelType();
        core[s].interleaved = false;
        core[s].falseColor = false;
        core[s].thumbnail = s != 0;
      }
      core[s].metadataComplete = true;
      core[s].dimensionOrder = "XYCZT";
    }
    vsi.close();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private int getTileSize() {
    int channels = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    return bpp * channels * tileX[getCoreIndex()] * tileY[getCoreIndex()];
  }

  private byte[] decodeTile(int no, int row, int col)
    throws FormatException, IOException
  {
    if (tileMap[getCoreIndex()] == null) {
      return new byte[getTileSize()];
    }

    int[] zct = getZCTCoords(no);
    TileCoordinate t = new TileCoordinate(nDimensions[getCoreIndex()]);
    t.coordinate[0] = col;
    t.coordinate[1] = row;

    for (String dim : dimensionOrdering.keySet()) {
      int index = dimensionOrdering.get(dim) + 2;

      if (dim.equals("Z")) {
        t.coordinate[index] = zct[0];
      }
      else if (dim.equals("C")) {
        t.coordinate[index] = zct[1];
      }
      else if (dim.equals("T")) {
        t.coordinate[index] = zct[2];
      }
    }

    Integer index = (Integer) tileMap[getCoreIndex()].get(t);
    if (index == null) {
      return new byte[getTileSize()];
    }

    Long offset = tileOffsets[getCoreIndex()][index];
    RandomAccessInputStream ets =
      new RandomAccessInputStream(usedFiles[getCoreIndex()]);
    ets.seek(offset);

    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    int tileSize = getTileSize();
    if (tileSize == 0) {
      tileSize = tileX[getCoreIndex()] * tileY[getCoreIndex()] * 10;
    }
    options.maxBytes = (int) (offset + tileSize);

    byte[] buf = null;
    long end = index < tileOffsets[getCoreIndex()].length - 1 ?
      tileOffsets[getCoreIndex()][index + 1] : ets.length();

    IFormatReader reader = null;
    String file = null;

    switch (compressionType[getCoreIndex()]) {
      case RAW:
        buf = new byte[tileSize];
        ets.read(buf);
        break;
      case JPEG:
        Codec codec = new JPEGCodec();
        buf = codec.decompress(ets, options);
        break;
      case JPEG_2000:
        codec = new JPEG2000Codec();
        buf = codec.decompress(ets, options);
        break;
      case PNG:
        file = "tile.png";
        reader = new APNGReader();
      case BMP:
        if (reader == null) {
          file = "tile.bmp";
          reader = new BMPReader();
        }

        byte[] b = new byte[(int) (end - offset)];
        ets.read(b);
        Location.mapFile(file, new ByteArrayHandle(b));
        reader.setId(file);
        buf = reader.openBytes(0);
        Location.mapFile(file, null);
        break;
    }

    if (reader != null) {
      reader.close();
    }

    ets.close();
    return buf;
  }

  private void parseETSFile(String file, int s)
    throws FormatException, IOException
  {
    RandomAccessInputStream etsFile = new RandomAccessInputStream(file);
    etsFile.order(true);

    // read the volume header
    String magic = etsFile.readString(4).trim();
    if (!magic.equals("SIS")) {
      throw new FormatException("Unknown magic bytes: " + magic);
    }

    int headerSize = etsFile.readInt();
    int version = etsFile.readInt();
    nDimensions[s] = etsFile.readInt();
    long additionalHeaderOffset = etsFile.readLong();
    int additionalHeaderSize = etsFile.readInt();
    etsFile.skipBytes(4); // reserved
    long usedChunkOffset = etsFile.readLong();
    int nUsedChunks = etsFile.readInt();
    etsFile.skipBytes(4); // reserved

    // read the additional header
    etsFile.seek(additionalHeaderOffset);

    String moreMagic = etsFile.readString(4).trim();
    if (!moreMagic.equals("ETS")) {
      throw new FormatException("Unknown magic bytes: " + moreMagic);
    }

    etsFile.skipBytes(4); // extra version number

    int pixelType = etsFile.readInt();
    core[s].sizeC = etsFile.readInt();
    int colorspace = etsFile.readInt();
    compressionType[s] = etsFile.readInt();
    int compressionQuality = etsFile.readInt();
    tileX[s] = etsFile.readInt();
    tileY[s] = etsFile.readInt();
    int tileZ = etsFile.readInt();

    core[s].rgb = core[s].sizeC > 1;

    // read the used chunks

    etsFile.seek(usedChunkOffset);

    tileOffsets[s] = new Long[nUsedChunks];

    ArrayList<TileCoordinate> tmpTiles = new ArrayList<TileCoordinate>();

    for (int chunk=0; chunk<nUsedChunks; chunk++) {
      etsFile.skipBytes(4);
      TileCoordinate t = new TileCoordinate(nDimensions[s]);
      for (int i=0; i<nDimensions[s]; i++) {
        t.coordinate[i] = etsFile.readInt();
      }
      tileOffsets[s][chunk] = etsFile.readLong();
      int nBytes = etsFile.readInt();
      etsFile.skipBytes(4);

      tmpTiles.add(t);
    }

    int maxX = 0;
    int maxY = 0;
    int maxZ = 0;
    int maxC = 0;
    int maxT = 0;

    for (TileCoordinate t : tmpTiles) {
      Integer tv = dimensionOrdering.get("T");
      Integer zv = dimensionOrdering.get("Z");
      Integer cv = dimensionOrdering.get("C");

      int tIndex = tv == null ? -1 : tv + 2;
      int zIndex = zv == null ? -1 : zv + 2;
      int cIndex = cv == null ? -1 : cv + 2;

      if (tv == null && zv == null) {
        if (t.coordinate.length > 4 && cv == null) {
          cIndex = 2;
          dimensionOrdering.put("C", cIndex - 2);
        }

        if (t.coordinate.length > 4) {
          if (cv == null) {
            tIndex = 3;
          }
          else {
            tIndex = cIndex + 2;
          }
          if (tIndex < t.coordinate.length) {
            dimensionOrdering.put("T", tIndex - 2);
          }
          else {
            tIndex = -1;
          }
        }

        if (t.coordinate.length > 5) {
          if (cv == null) {
            zIndex = 4;
          }
          else {
            zIndex = cIndex + 1;
          }
          if (zIndex < t.coordinate.length) {
            dimensionOrdering.put("Z", zIndex - 2);
          }
          else {
            zIndex = -1;
          }
        }
      }

      if (t.coordinate[0] > maxX) {
        maxX = t.coordinate[0];
      }
      if (t.coordinate[1] > maxY) {
        maxY = t.coordinate[1];
      }

      if (tIndex >= 0 && t.coordinate[tIndex] > maxT) {
        maxT = t.coordinate[tIndex];
      }
      if (zIndex >= 0 && t.coordinate[zIndex] > maxZ) {
        maxZ = t.coordinate[zIndex];
      }
      if (cIndex >= 0 && t.coordinate[cIndex] > maxC) {
        maxC = t.coordinate[cIndex];
      }
    }

    if (maxX > 1) {
      core[s].sizeX = tileX[s] * (maxX + 1);
    }
    else {
      core[s].sizeX = tileX[s];
    }
    if (maxY > 1) {
      core[s].sizeY = tileY[s] * (maxY + 1);
    }
    else {
      core[s].sizeY = tileY[s];
    }
    core[s].sizeZ = maxZ + 1;
    if (maxC > 0) {
      core[s].sizeC *= (maxC + 1);
    }
    core[s].sizeT = maxT + 1;
    if (core[s].sizeZ == 0) {
      core[s].sizeZ = 1;
    }
    core[s].imageCount = core[s].sizeZ * core[s].sizeT;
    if (maxC > 0) {
      core[s].imageCount *= (maxC + 1);
    }

    if (maxY > 1) {
      rows[s] = maxY + 1;
    }
    else {
      rows[s] = 1;
    }
    if (maxX > 1) {
      cols[s] = maxX + 1;
    }
    else {
      cols[s] = 1;
    }

    for (int i=0; i<tmpTiles.size(); i++) {
      tileMap[s].put(tmpTiles.get(i), i);
    }

    core[s].pixelType = convertPixelType(pixelType);
    etsFile.close();
  }

  private int convertPixelType(int pixelType) throws FormatException {
    switch (pixelType) {
      case CHAR:
        return FormatTools.INT8;
      case UCHAR:
        return FormatTools.UINT8;
      case SHORT:
        return FormatTools.INT16;
      case USHORT:
        return FormatTools.UINT16;
      case INT:
        return FormatTools.INT32;
      case UINT:
        return FormatTools.UINT32;
      case LONG:
        throw new FormatException("Unsupported pixel type: long");
      case ULONG:
        throw new FormatException("Unsupported pixel type: unsigned long");
      case FLOAT:
        return FormatTools.FLOAT;
      case DOUBLE:
        return FormatTools.DOUBLE;
      default:
        throw new FormatException("Unsupported pixel type: " + pixelType);
    }
  }

  private void readTags(RandomAccessInputStream vsi) throws IOException {
    // read the VSI header
    long fp = vsi.getFilePointer();
    if (fp + 24 >= vsi.length()) {
      return;
    }
    int headerSize = vsi.readShort(); // should always be 24
    int version = vsi.readShort(); // always 21321
    int volumeVersion = vsi.readInt();
    long dataFieldOffset = vsi.readLong();
    int flags = vsi.readInt();
    vsi.skipBytes(4);

    int tagCount = flags & 0xfffffff;

    if (fp + dataFieldOffset < 0) {
      return;
    }

    vsi.seek(fp + dataFieldOffset);
    if (vsi.getFilePointer() >= vsi.length()) {
      return;
    }

    for (int i=0; i<tagCount; i++) {
      if (vsi.getFilePointer() + 16 >= vsi.length()) {
        break;
      }

      // read the data field

      int fieldType = vsi.readInt();
      int tag = vsi.readInt();
      long nextField = vsi.readInt() & 0xffffffffL;
      int dataSize = vsi.readInt();

      boolean extraTag = ((fieldType & 0x8000000) >> 27) == 1;
      boolean extendedField = ((fieldType & 0x10000000) >> 28) == 1;
      boolean inlineData = ((fieldType & 0x40000000) >> 30) == 1;
      boolean array = (!inlineData && !extendedField) &&
        ((fieldType & 0x20000000) >> 29) == 1;
      boolean newVolume = ((fieldType & 0x80000000) >> 31) == 1;

      int realType = fieldType & 0xffffff;
      int secondTag = -1;

      if (extraTag) {
        secondTag = vsi.readInt();
      }

      if (extendedField && realType == NEW_VOLUME_HEADER) {
        if (tag == 2007) {
          dimensionTag = secondTag;
          inDimensionProperties = true;
        }
        long endPointer = vsi.getFilePointer() + dataSize;
        while (vsi.getFilePointer() < endPointer &&
          vsi.getFilePointer() < vsi.length())
        {
          long start = vsi.getFilePointer();
          readTags(vsi);
          long end = vsi.getFilePointer();
          if (start == end) {
            break;
          }
        }
        if (tag == 2007) {
          inDimensionProperties = false;
          foundChannelTag = false;
        }
      }

      if (inDimensionProperties) {
        if (tag == 2012 && !dimensionOrdering.containsValue(dimensionTag)) {
          dimensionOrdering.put("Z", dimensionTag);
        }
        else if ((tag == 2100 || tag == 2027) &&
          !dimensionOrdering.containsValue(dimensionTag))
        {
          dimensionOrdering.put("T", dimensionTag);
        }
        else if (tag == 2039 && !dimensionOrdering.containsValue(dimensionTag))
        {
          dimensionOrdering.put("L", dimensionTag);
        }
        else if (tag == 2008 && foundChannelTag &&
          !dimensionOrdering.containsValue(dimensionTag))
        {
          dimensionOrdering.put("C", dimensionTag);
        }
        else if (tag == 2008) {
          foundChannelTag = true;
        }
      }

      if (nextField == 0) {
        return;
      }

      if (fp + nextField < vsi.length() && fp + nextField >= 0) {
        vsi.seek(fp + nextField);
      }
      else break;
    }
  }

  // -- Helper class --

  class TileCoordinate {
    public int[] coordinate;

    public TileCoordinate(int nDimensions) {
      coordinate = new int[nDimensions];
    }

    public boolean equals(Object o) {
      if (!(o instanceof TileCoordinate)) {
        return false;
      }

      TileCoordinate t = (TileCoordinate) o;
      if (coordinate.length != t.coordinate.length) {
        return false;
      }

      for (int i=0; i<coordinate.length; i++) {
        if (coordinate[i] != t.coordinate[i]) {
          return false;
        }
      }
      return true;
    }

    public int hashCode() {
      int[] lengths = new int[coordinate.length];
      lengths[0] = rows[getSeries()];
      lengths[1] = cols[getSeries()];

      for (String dim : dimensionOrdering.keySet()) {
        int index = dimensionOrdering.get(dim) + 2;

        if (dim.equals("Z")) {
          lengths[index] = getSizeZ();
        }
        else if (dim.equals("C")) {
          lengths[index] = getEffectiveSizeC();
        }
        else if (dim.equals("T")) {
          lengths[index] = getSizeT();
        }
      }

      for (int i=0; i<lengths.length; i++) {
        if (lengths[i] == 0) {
          lengths[i] = 1;
        }
      }

      return FormatTools.positionToRaster(lengths, coordinate);
    }

    public String toString() {
      StringBuffer b = new StringBuffer("{");
      for (int p : coordinate) {
        b.append(p);
        b.append(", ");
      }
      b.append("}");
      return b.toString();
    }
  }

}
