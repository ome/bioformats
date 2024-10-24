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
import java.util.List;
import java.util.Hashtable;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.SubResolutionFormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.NetCDFService;
import loci.formats.services.NetCDFServiceImpl;
import ome.xml.model.primitives.Color;
import ome.units.quantity.Length;

/**
 * Reader for Bitplane Imaris 5.5 (HDF) files.
 */
public class ImarisHDFReader extends SubResolutionFormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";

  private static final String[] DELIMITERS = {" ", "-", "."};

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private double minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private NetCDFService netcdf;

  // channel parameters
  private List<String> emWave, exWave, channelMin, channelMax;
  private List<String> gain, pinhole, channelName, microscopyMode;
  private List<double[]> colors;
  private int lastChannel = 0;

  // caching parameters
  private long maxBufferSize = 1024 * 1024 * 1024;
  private Object[] buffer = null;
  private int[] blockSizeZPerResolution;
  private int lastMinZ = 0;
  private int lastMaxZ = 0;
  private int lastRes = 0;
  private int lastT = 0;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return core.get(series, core.size(series) - 1).sizeX;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return core.get(series, core.size(series) - 1).sizeY;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(HDF_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= colors.size()) {
      return null;
    }

    double[] color = colors.get(lastChannel);

    byte[][] lut = new byte[3][256];
    for (int c=0; c<lut.length; c++) {
      double max = color[c] * 255;
      for (int p=0; p<lut[c].length; p++) {
        lut[c][p] = (byte) ((p / 255.0) * max);
      }
    }
    return lut;
  }

  /* @see loci.formats.IFormatReaderget16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= colors.size() || colors.get(lastChannel) == null) {
      return null;
    }

    double[] color = colors.get(lastChannel);

    short[][] lut = new short[3][65536];
    for (int c=0; c<lut.length; c++) {
      double max = color[c] * 65535;
      for (int p=0; p<lut[c].length; p++) {
        lut[c][p] = (short) ((p / 65535.0) * max);
      }
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

    lastChannel = getZCTCoords(no)[1];

    // pixel data is stored in XYZ blocks

    Object image = getImageData(no, x, y, w, h);

    boolean little = isLittleEndian();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    for (int row=0; row<h; row++) {
      int rowlen = w * bpp;
      int base = row * rowlen;

      // indexes into the source array add (data.length - h) and
      // (rowData.length - w) to account for cases where the source array
      // represents a tile that is larger than the desired tile
      if (image instanceof byte[][]) {
        byte[][] data = (byte[][]) image;
        byte[] rowData = data[row + data.length - h];
        System.arraycopy(rowData, rowData.length - w, buf, base, w);
      }
      else if (image instanceof short[][]) {
        short[][] data = (short[][]) image;
        short[] rowData = data[row + data.length - h];
        int index = rowData.length - w;
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + index], buf, base + 2*i, 2, little);
        }
      }
      else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row + data.length - h];
        int index = rowData.length - w;
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + index], buf, base + i*4, 4, little);
        }
      }
      else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row + data.length - h];
        int index = rowData.length - w;
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(rowData[i + index]);
          DataTools.unpackBytes(v, buf, base + i*4, 4, little);
        }
      }
      else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row + data.length - h];
        int index = rowData.length - w;
        for (int i=0; i<w; i++) {
          long v = Double.doubleToLongBits(rowData[i + index]);
          DataTools.unpackBytes(v, buf, base + i * 8, 8, little);
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesCount = 0;
      pixelSizeX = pixelSizeY = pixelSizeZ = 0;
      minX = minY = minZ = maxX = maxY = maxZ = 0;

      lastRes = lastT = lastMinZ = lastMaxZ = 0;
      buffer = null;
      blockSizeZPerResolution = null;
      
      if (netcdf != null) netcdf.close();
      netcdf = null;

      emWave = exWave = channelMin = channelMax = null;
      gain = pinhole = channelName = microscopyMode = null;
      colors = null;
      lastChannel = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      ServiceFactory factory = new ServiceFactory();
      netcdf = factory.getInstance(NetCDFService.class);
      netcdf.setFile(id);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException(NetCDFServiceImpl.NO_NETCDF_MSG, e);
    }

    pixelSizeX = pixelSizeY = pixelSizeZ = 1;

    emWave = new ArrayList<String>();
    exWave = new ArrayList<String>();
    channelMin = new ArrayList<String>();
    channelMax = new ArrayList<String>();
    gain = new ArrayList<String>();
    pinhole = new ArrayList<String>();
    channelName = new ArrayList<String>();
    microscopyMode = new ArrayList<String>();
    colors = new ArrayList<double[]>();

    seriesCount = 0;

    // read all of the metadata key/value pairs
    CoreMetadata ms0 = core.get(0, 0);

    parseAttributes();

    int currentSeries = 0;

    if (seriesCount > 1) {
      for (int i=1; i<seriesCount; i++) {
        CoreMetadata ms = new CoreMetadata();
        String groupPath =
          "DataSet/ResolutionLevel_" + i + "/TimePoint_0/Channel_0";
        ms.sizeX =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeX"));
        ms.sizeY =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeY"));
        ms.sizeZ =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeZ"));
        ms.imageCount = ms.sizeZ * getSizeC() * getSizeT();
        ms.sizeC = getSizeC();
        ms.sizeT = getSizeT();
        ms.thumbnail = true;

        if (ms.sizeZ == ms0.sizeZ && ms.sizeC == ms0.sizeC &&
            ms.sizeT == ms0.sizeT) {
          core.add(currentSeries, ms);
        }
        else {
          core.add(ms);
          currentSeries++;
        }
      }
    }
    ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
    ms0.thumbnail = false;
    ms0.dimensionOrder = "XYZCT";

    // read block sizes for caching
    blockSizeZPerResolution = new int[seriesCount];
    for (int res = 0; res < seriesCount; res++) {
      String datasetPath = "DataSet/ResolutionLevel_" + res + "/TimePoint_0/Channel_0/Data";
      Hashtable<String, Object> table = netcdf.getVariableAttributes(datasetPath);
      String chunkSizesString = (String) table.get("_ChunkSizes");
      String[] sizes = chunkSizesString.split(" ");
      blockSizeZPerResolution[res] = Integer.parseInt(sizes[0]);
    }
    
    // determine pixel type - this isn't stored in the metadata, so we need
    // to check the pixels themselves

    int type = -1;

    Object pix = getImageData(0, 0, 0, 1, 1);
    if (pix instanceof byte[][]) type = FormatTools.UINT8;
    else if (pix instanceof short[][]) type = FormatTools.UINT16;
    else if (pix instanceof int[][]) type = FormatTools.UINT32;
    else if (pix instanceof float[][]) type = FormatTools.FLOAT;
    else if (pix instanceof double[][]) type = FormatTools.DOUBLE;
    else {
      throw new FormatException("Unknown pixel type: " + pix);
    }

    for (int i = 0; i < core.size(); i++) {
      for (int j = 0; j < core.size(i); j++) {
        CoreMetadata ms = core.get(i, j);
        ms.pixelType = type;
        ms.dimensionOrder = "XYZCT";
        ms.rgb = false;
        ms.thumbSizeX = 128;
        ms.thumbSizeY = 128;
        ms.orderCertain = true;
        ms.littleEndian = true;
        ms.interleaved = false;
        ms.indexed = colors.size() >= getSizeC();
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String imageName = new Location(getCurrentFile()).getName();
    for (int s=0; s<getSeriesCount(); s++) {
      store.setImageName(imageName + " Resolution Level " + (s + 1), s);
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    int cIndex = 0;
    for (int s=0; s<getSeriesCount(); s++) {
      setSeries(s);
      double px = pixelSizeX, py = pixelSizeY, pz = pixelSizeZ;
      if (px == 1) px = (maxX - minX) / getSizeX();
      if (py == 1) py = (maxY - minY) / getSizeY();
      if (pz == 1) pz = (maxZ - minZ) / getSizeZ();

      Length sizeX = FormatTools.getPhysicalSizeX(px);
      Length sizeY = FormatTools.getPhysicalSizeY(py);
      Length sizeZ = FormatTools.getPhysicalSizeZ(pz);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, s);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, s);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, s);
      }

      for (int i=0; i<getSizeC(); i++, cIndex++) {
        Float gainValue = null;
        Integer pinholeValue = null, emWaveValue = null, exWaveValue;

        if (cIndex < gain.size()) {
          try {
            gainValue = Float.parseFloat(gain.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < pinhole.size()) {
          try {
            pinholeValue = Integer.parseInt(pinhole.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < emWave.size()) {
          try {
            emWaveValue = Integer.parseInt(emWave.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < exWave.size()) {
          try {
            exWaveValue = Integer.parseInt(exWave.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }

        Double minValue = null, maxValue = null;

        if (cIndex < channelMin.size()) {
          try {
            minValue = DataTools.parseDouble(channelMin.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < channelMax.size()) {
          try {
            maxValue = DataTools.parseDouble(channelMax.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }

        if (i < colors.size() && colors.get(i) != null) {
          double[] color = colors.get(i);
          Color realColor = new Color(
            (int) (color[0] * 255), (int) (color[1] * 255),
            (int) (color[2] * 255), 255);
          store.setChannelColor(realColor, s, i);
        }
        if (i < channelName.size()) {
          store.setChannelName(channelName.get(i), s, i);
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  /**
   * Retrieve an array corresponding to the specified image tile.
   * In some cases, the returned tile will be larger than the requested tile;
   * openBytes will correct for this as needed.
   */
  private Object getImageData(int no, int x, int y, int width, int height)
    throws FormatException
  {
    int[] zct = getZCTCoords(no);
    int resolutionIndex = getCoreIndex();
    Object image = null;

    // the width and height cannot be 1, because then netCDF will give us a
    // singleton instead of an array
    if (height == 1) {
      height++;

      // if we only wanted the last row, the Y coordinate must be adjusted
      // so that we don't attempt to read past the end of the image
      if (y == getSizeY() - 1) {
        y--;
      }
    }
    if (width == 1) {
      width++;

      // if we only wanted the last column, the X coordinate must be adjusted
      // so that we don't attempt to read past the end of the image
      if (x == getSizeX() - 1) {
        x--;
      }
    }

    // netCDF sometimes returns incorrect pixel values if the (X, Y) coordinate
    // is in the lower right quadrant of the image.  We correct for this by
    // moving the X coordinate to the left and adjusting the width.
    if (x >= getSizeX() / 2 && y >= getSizeY() / 2) {
      width += x - (getSizeX() / 2) + 1;
      x = (getSizeX() / 2) - 1;
    }

    // cache dataset blocks to avoid multiple reads.
    // use caching for 3D datasets and only if the size of the required buffer is < maxBufferSize
    if (getSizeZ() > 1 && getSizeX() * getSizeY() * blockSizeZPerResolution[resolutionIndex] * getSizeC() * FormatTools.getBytesPerPixel(getPixelType()) < maxBufferSize) {
      // update buffer if needed
      if (zct[0] < lastMinZ || zct[0] > lastMaxZ || zct[2] != lastT || resolutionIndex != lastRes || buffer == null) {
        buffer = new Object[getSizeC()];
        if (resolutionIndex != lastRes) {
          lastRes = resolutionIndex;
          lastT = zct[2];
        }
        if (zct[2] != lastT) {
          lastT = zct[2];
        }

        int blockNumber = zct[0] / blockSizeZPerResolution[resolutionIndex];
        int[] dims = new int[] {blockSizeZPerResolution[resolutionIndex], getSizeY(), getSizeX()};
        int[] idcs = new int[] {blockNumber * blockSizeZPerResolution[resolutionIndex], 0, 0};
        try {
          String path;
          for (int ch = 0; ch < getSizeC(); ch++) {
            path = "/DataSet/ResolutionLevel_" + resolutionIndex + "/TimePoint_" + zct[2] + "/Channel_" + ch + "/Data";
            buffer[ch] = netcdf.getArray(path, idcs, dims);
          }
        }
        catch (ServiceException e) {
          throw new FormatException(e);
        }

        lastMinZ = blockNumber * blockSizeZPerResolution[resolutionIndex];
        lastMaxZ = lastMinZ + blockSizeZPerResolution[resolutionIndex] - 1;
      }

      // read from buffer
      if (buffer[zct[1]] instanceof byte[][][]) {
        byte[][][] byteBuffer = (byte[][][]) buffer[zct[1]];
        byte[][] slice = new byte[height][width];
        for (int i = y; i < y + height; i++) {
          for (int j = x; j < x + width; j++) {
            slice[i - y][j - x] = byteBuffer[zct[0] - lastMinZ][i][j];
          }
        }
        image = (Object) slice;
      }
      else if (buffer[zct[1]] instanceof short[][][]) {
        short[][][] shortBuffer = (short[][][]) buffer[zct[1]];
        short[][] slice = new short[height][width];
        for (int i = y; i < y + height; i++) {
          for (int j = x; j < x + width; j++) {
            slice[i - y][j - x] = shortBuffer[zct[0] - lastMinZ][i][j];
          }
        }
        image = (Object) slice;
      }
      else if (buffer[zct[1]] instanceof int[][][]) {
        int[][][] intBuffer = (int[][][]) buffer[zct[1]];
        int[][] slice = new int[height][width];
        for (int i = y; i < y + height; i++) {
          for (int j = x; j < x + width; j++) {
            slice[i - y][j - x] = intBuffer[zct[0] - lastMinZ][i][j];
          }
        }
        image = (Object) slice;
      }
      else if (buffer[zct[1]] instanceof float[][][]) {
        float[][][] floatBuffer = (float[][][]) buffer[zct[1]];
        float[][] slice = new float[height][width];
        for (int i = y; i < y + height; i++) {
          for (int j = x; j < x + width; j++) {
            slice[i - y][j - x] = floatBuffer[zct[0] - lastMinZ][i][j];
          }
        }
        image = (Object) slice;
      }
    }
    else {
      int[] dimensions = new int[] {1, height, width};
      int[] indices = new int[] {zct[0], y, x};
      try {
        String path = "/DataSet/ResolutionLevel_" + resolutionIndex + "/TimePoint_" + zct[2] + "/Channel_" + zct[1] + "/Data";
        image = netcdf.getArray(path, indices, dimensions);
      }
      catch (ServiceException e) {
        throw new FormatException(e);
      }
    }
    
    return image;
  }

  private void parseAttributes() {
    final List<String> attributes = netcdf.getAttributeList();
    CoreMetadata ms0 = core.get(0, 0);

    for (String attr : attributes) {
      String name = attr.substring(attr.lastIndexOf("/") + 1);
      String value = netcdf.getAttributeValue(attr);
      if (value == null) continue;
      value = value.trim();

      if (name.equals("X") || (attr.startsWith("DataSet/ResolutionLevel_0") && name.equals("ImageSizeX"))) {
        try {
          ms0.sizeX = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("Y") || (attr.startsWith("DataSet/ResolutionLevel_0") && name.equals("ImageSizeY"))) {
        try {
          ms0.sizeY = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("Z") || (attr.startsWith("DataSet/ResolutionLevel_0") && name.equals("ImageSizeZ"))) {
        try {
          ms0.sizeZ = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("FileTimePoints")) {
        ms0.sizeT = Integer.parseInt(value);
      }
      else if (name.equals("NumberOfChannels") && getSizeC() == 0) {
        ms0.sizeC = Integer.parseInt(value);
      }
      else if (name.equals("RecordingEntrySampleSpacing")) {
        pixelSizeX = Double.parseDouble(value);
      }
      else if (name.equals("RecordingEntryLineSpacing")) {
        pixelSizeY = Double.parseDouble(value);
      }
      else if (name.equals("RecordingEntryPlaneSpacing")) {
        pixelSizeZ = Double.parseDouble(value);
      }
      else if (name.equals("ExtMax0")) maxX = Double.parseDouble(value);
      else if (name.equals("ExtMax1")) maxY = Double.parseDouble(value);
      else if (name.equals("ExtMax2")) maxZ = Double.parseDouble(value);
      else if (name.equals("ExtMin0")) minX = Double.parseDouble(value);
      else if (name.equals("ExtMin1")) minY = Double.parseDouble(value);
      else if (name.equals("ExtMin2")) minZ = Double.parseDouble(value);

      if (attr.startsWith("DataSet/ResolutionLevel_")) {
        int slash = attr.indexOf("/", 24);
        int n = Integer.parseInt(attr.substring(24, slash == -1 ?
          attr.length() : slash));
        if (n >= seriesCount) seriesCount = n + 1;
      }

      if (attr.startsWith("DataSetInfo/Channel_")) {
        String originalValue = value;
        for (String d : DELIMITERS) {
          if (value.indexOf(d) != -1) {
            value = value.substring(value.indexOf(d) + 1);
          }
        }

        int underscore = attr.indexOf('_') + 1;
        int cIndex = Integer.parseInt(attr.substring(underscore,
          attr.indexOf("/", underscore)));

        while (cIndex >= getSizeC()) ms0.sizeC++;

        if (name.equals("Gain")) {
          addValue(gain, value, cIndex);
        }
        else if (name.equals("LSMEmissionWavelength")) {
          addValue(emWave, value, cIndex);
        }
        else if (name.equals("LSMExcitationWavelength")) {
          addValue(exWave, value, cIndex);
        }
        else if (name.equals("Max")) {
          addValue(channelMax, value, cIndex);
        }
        else if (name.equals("Min")) {
          addValue(channelMin, value, cIndex);
        }
        else if (name.equals("Pinhole")) {
          addValue(pinhole, value, cIndex);
        }
        else if (name.equals("Name")) {
          addValue(channelName, originalValue, cIndex);
        }
        else if (name.equals("MicroscopyMode")) {
          addValue(microscopyMode, value, cIndex);
        }
        else if (name.equals("Color")) {
          double[] color = new double[3];
          String[] intensity = originalValue.split(" ");
          for (int i=0; i<intensity.length; i++) {
            color[i] = Double.parseDouble(intensity[i]);
          }
          addValue(colors, color, cIndex);
        }
      }

      if (value != null) addGlobalMeta(name, value);
    }
  }

  private void addValue(List l, Object value, int index) {
    if (index < l.size()) {
      l.set(index, value);
    }
    else {
      while (index > l.size()) {
        l.add(null);
      }
      l.add(value);
    }
  }

}
