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
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.NetCDFService;
import loci.formats.services.NetCDFServiceImpl;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PositiveFloat;

/**
 * Reader for Bitplane Imaris 5.5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ImarisHDFReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ImarisHDFReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ImarisHDFReader extends FormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";

  private static final String[] DELIMITERS = {" ", "-", "."};

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private double minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private NetCDFService netcdf;

  // channel parameters
  private Vector<String> emWave, exWave, channelMin, channelMax;
  private Vector<String> gain, pinhole, channelName, microscopyMode;
  private Vector<double[]> colors;
  private int lastChannel = 0;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(HDF_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
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
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= colors.size()) {
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = getZCTCoords(no)[1];

    // pixel data is stored in XYZ blocks

    Object image = getImageData(no, y, h);

    boolean big = !isLittleEndian();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    for (int row=0; row<h; row++) {
      int base = row * w * bpp;
      if (image instanceof byte[][]) {
        byte[][] data = (byte[][]) image;
        byte[] rowData = data[row];
        System.arraycopy(rowData, x, buf, row*w, w);
      }
      else if (image instanceof short[][]) {
        short[][] data = (short[][]) image;
        short[] rowData = data[row];
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + 2*i, 2, big);
        }
      }
      else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row];
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row];
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row];
        for (int i=0; i<w; i++) {
          long v = Double.doubleToLongBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 8, 8, big);
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesCount = 0;
      pixelSizeX = pixelSizeY = pixelSizeZ = 0;
      minX = minY = minZ = maxX = maxY = maxZ = 0;

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

    emWave = new Vector<String>();
    exWave = new Vector<String>();
    channelMin = new Vector<String>();
    channelMax = new Vector<String>();
    gain = new Vector<String>();
    pinhole = new Vector<String>();
    channelName = new Vector<String>();
    microscopyMode = new Vector<String>();
    colors = new Vector<double[]>();

    seriesCount = 0;

    // read all of the metadata key/value pairs

    parseAttributes();

    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      core[0] = oldCore;
      for (int i=1; i<seriesCount; i++) {
        core[i] = new CoreMetadata();
      }

      core[0].resolutionCount = seriesCount;

      for (int i=1; i<seriesCount; i++) {
        String groupPath =
          "/DataSet/ResolutionLevel_" + i + "/TimePoint_0/Channel_0";
        core[i].sizeX =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeX"));
        core[i].sizeY =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeY"));
        core[i].sizeZ =
          Integer.parseInt(netcdf.getAttributeValue(groupPath + "/ImageSizeZ"));
        core[i].imageCount = core[i].sizeZ * getSizeC() * getSizeT();
        core[i].sizeC = getSizeC();
        core[i].sizeT = getSizeT();
        core[i].thumbnail = true;
      }
    }
    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    core[0].thumbnail = false;
    core[0].dimensionOrder = "XYZCT";

    // determine pixel type - this isn't stored in the metadata, so we need
    // to check the pixels themselves

    int type = -1;

    Object pix = getImageData(0, 0, 1);
    if (pix instanceof byte[][]) type = FormatTools.UINT8;
    else if (pix instanceof short[][]) type = FormatTools.UINT16;
    else if (pix instanceof int[][]) type = FormatTools.UINT32;
    else if (pix instanceof float[][]) type = FormatTools.FLOAT;
    else if (pix instanceof double[][]) type = FormatTools.DOUBLE;
    else {
      throw new FormatException("Unknown pixel type: " + pix);
    }

    for (int i=0; i<core.length; i++) {
      core[i].pixelType = type;
      core[i].dimensionOrder = "XYZCT";
      core[i].rgb = false;
      core[i].thumbSizeX = 128;
      core[i].thumbSizeY = 128;
      core[i].orderCertain = true;
      core[i].littleEndian = true;
      core[i].interleaved = false;
      core[i].indexed = colors.size() >= getSizeC();
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

      if (px > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(px), s);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}", px);
      }
      if (py > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(py), s);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}", py);
      }
      if (pz > 0) {
        store.setPixelsPhysicalSizeZ(new PositiveFloat(pz), s);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}", pz);
      }

      for (int i=0; i<getSizeC(); i++, cIndex++) {
        Float gainValue = null;
        Integer pinholeValue = null, emWaveValue = null, exWaveValue;

        if (cIndex < gain.size()) {
          try {
            gainValue = new Float(gain.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < pinhole.size()) {
          try {
            pinholeValue = new Integer(pinhole.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < emWave.size()) {
          try {
            emWaveValue = new Integer(emWave.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < exWave.size()) {
          try {
            exWaveValue = new Integer(exWave.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }

        // CHECK
        /*
        store.setLogicalChannelName((String) channelName.get(cIndex), s, i);
        store.setDetectorSettingsGain(gainValue, s, i);
        store.setLogicalChannelPinholeSize(pinholeValue, s, i);
        store.setLogicalChannelMode((String) microscopyMode.get(cIndex), s, i);
        store.setLogicalChannelEmWave(emWaveValue, s, i);
        store.setLogicalChannelExWave(exWaveValue, s, i);
        */

        Double minValue = null, maxValue = null;

        if (cIndex < channelMin.size()) {
          try {
            minValue = new Double(channelMin.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }
        if (cIndex < channelMax.size()) {
          try {
            maxValue = new Double(channelMax.get(cIndex));
          }
          catch (NumberFormatException e) { }
        }

        if (i < colors.size()) {
          double[] color = colors.get(i);
          Color realColor = new Color(
            (int) (color[0] * 255), (int) (color[1] * 255),
            (int) (color[2] * 255), 255);
          store.setChannelColor(realColor, s, i);
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  private Object getImageData(int no, int y, int height)
    throws FormatException
  {
    int[] zct = getZCTCoords(no);
    String path = "/DataSet/ResolutionLevel_" + getCoreIndex() + "/TimePoint_" +
      zct[2] + "/Channel_" + zct[1] + "/Data";
    Object image = null;

    // the width and height cannot be 1, because then netCDF will give us a
    // singleton instead of an array
    if (height == 1) {
      height++;
    }

    int[] dimensions = new int[] {1, height, getSizeX()};
    int[] indices = new int[] {zct[0], y, 0};
    try {
      image = netcdf.getArray(path, indices, dimensions);
    }
    catch (ServiceException e) {
      throw new FormatException(e);
    }
    return image;
  }

  private void parseAttributes() {
    Vector<String> attributes = netcdf.getAttributeList();
    for (String attr : attributes) {
      String name = attr.substring(attr.lastIndexOf("/") + 1);
      String value = netcdf.getAttributeValue(attr);
      if (value == null) continue;
      value = value.trim();

      if (name.equals("X") || name.equals("ImageSizeX")) {
        try {
          core[0].sizeX = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("Y") || name.equals("ImageSizeY")) {
        try {
          core[0].sizeY = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("Z") || name.equals("ImageSizeZ")) {
        try {
          core[0].sizeZ = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          LOGGER.trace("Failed to parse '" + name + "'", e);
        }
      }
      else if (name.equals("FileTimePoints")) {
        core[0].sizeT = Integer.parseInt(value);
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

      if (attr.startsWith("/DataSet/ResolutionLevel_")) {
        int slash = attr.indexOf("/", 25);
        int n = Integer.parseInt(attr.substring(25, slash == -1 ?
          attr.length() : slash));
        if (n == seriesCount) seriesCount++;
      }

      if (attr.startsWith("/DataSetInfo/Channel_")) {
        String originalValue = value;
        for (String d : DELIMITERS) {
          if (value.indexOf(d) != -1) {
            value = value.substring(value.indexOf(d) + 1);
          }
        }

        int underscore = attr.indexOf("_") + 1;
        int cIndex = Integer.parseInt(attr.substring(underscore,
          attr.indexOf("/", underscore)));
        if (cIndex == getSizeC()) core[0].sizeC++;

        if (name.equals("Gain")) gain.add(value);
        else if (name.equals("LSMEmissionWavelength")) emWave.add(value);
        else if (name.equals("LSMExcitationWavelength")) exWave.add(value);
        else if (name.equals("Max")) channelMax.add(value);
        else if (name.equals("Min")) channelMin.add(value);
        else if (name.equals("Pinhole")) pinhole.add(value);
        else if (name.equals("Name")) channelName.add(value);
        else if (name.equals("MicroscopyMode")) microscopyMode.add(value);
        else if (name.equals("Color")) {
          double[] color = new double[3];
          String[] intensity = originalValue.split(" ");
          for (int i=0; i<intensity.length; i++) {
            color[i] = Double.parseDouble(intensity[i]);
          }
          colors.add(color);
        }
      }

      if (value != null) addGlobalMeta(name, value);
    }
  }

}
