//
// ImarisHDFReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.NetcdfTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * Reader for Bitplane Imaris 5.5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ImarisHDFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ImarisHDFReader.java">SVN</a></dd></dl>
 */
public class ImarisHDFReader extends FormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";

  // -- Fields --

  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private float minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private NetcdfTools netcdf;

  // channel parameters
  private Vector<String> emWave, exWave, channelMin, channelMax;
  private Vector<String> gain, pinhole, channelName, microscopyMode;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(HDF_MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = FormatTools.getZCTCoords(this, no);

    // pixel data is stored in XYZ blocks

    String path = "/DataSet/ResolutionLevel_" + series + "/TimePoint_" +
      zct[2] + "/Channel_" + zct[1] + "/Data";
    Object image = netcdf.getArray(path, new int[] {zct[0], 0, 0},
      new int[] {1, getSizeY(), getSizeX()});

    boolean big = !isLittleEndian();
    for (int row=0; row<h; row++) {
      if (image instanceof byte[][]) {
        byte[][] data = (byte[][]) image;
        byte[] rowData = data[row + y];
        System.arraycopy(rowData, x, buf, row*w, w);
      }
      else if (image instanceof short[][]) {
        short[][] data = (short[][]) image;
        short[] rowData = data[row + y];
        int base = row * w * 2;
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[x + i], buf, base + 2*i, 2, big);
        }
      }
      else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row + y];
        int base = row * w * 4;
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[x + i], buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row + y];
        int base = row * w * 4;
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(rowData[x + i]);
          DataTools.unpackBytes(v, buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row + y];
        int base = row * w * 8;
        for (int i=0; i<w; i++) {
          long v = Double.doubleToLongBits(rowData[x + i]);
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
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    netcdf = new NetcdfTools(id);

    pixelSizeX = pixelSizeY = pixelSizeZ = 1.0f;

    emWave = new Vector<String>();
    exWave = new Vector<String>();
    channelMin = new Vector<String>();
    channelMax = new Vector<String>();
    gain = new Vector<String>();
    pinhole = new Vector<String>();
    channelName = new Vector<String>();
    microscopyMode = new Vector<String>();

    seriesCount = 0;

    // read all of the metadata key/value pairs

    Vector attributes = netcdf.getAttributeList();
    for (int i=0; i<attributes.size(); i++) {
      String attr = (String) attributes.get(i);
      String name = attr.substring(attr.lastIndexOf("/") + 1);
      String value = netcdf.getAttributeValue(attr);
      if (value == null) continue;
      value = value.trim();

      if (name.equals("X") || name.equals("ImageSizeX")) {
        try {
          core[0].sizeX = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          traceDebug(e);
        }
      }
      else if (name.equals("Y") || name.equals("ImageSizeY")) {
        try {
          core[0].sizeY = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          traceDebug(e);
        }
      }
      else if (name.equals("Z") || name.equals("ImageSizeZ")) {
        try {
          core[0].sizeZ = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
          traceDebug(e);
        }
      }
      else if (name.equals("FileTimePoints")) {
        core[0].sizeT = Integer.parseInt(value);
      }
      else if (name.equals("RecordingEntrySampleSpacing")) {
        pixelSizeX = Float.parseFloat(value);
      }
      else if (name.equals("RecordingEntryLineSpacing")) {
        pixelSizeY = Float.parseFloat(value);
      }
      else if (name.equals("RecordingEntryPlaneSpacing")) {
        pixelSizeZ = Float.parseFloat(value);
      }
      else if (name.equals("ExtMax0")) maxX = Float.parseFloat(value);
      else if (name.equals("ExtMax1")) maxY = Float.parseFloat(value);
      else if (name.equals("ExtMax2")) maxZ = Float.parseFloat(value);
      else if (name.equals("ExtMin0")) minX = Float.parseFloat(value);
      else if (name.equals("ExtMin1")) minY = Float.parseFloat(value);
      else if (name.equals("ExtMin2")) minZ = Float.parseFloat(value);

      if (attr.startsWith("/DataSet/ResolutionLevel_")) {
        int slash = attr.indexOf("/", 25);
        int n = Integer.parseInt(attr.substring(25, slash == -1 ?
          attr.length() : slash));
        if (n == seriesCount) seriesCount++;
      }

      if (attr.startsWith("/DataSetInfo/Channel_")) {
        if (value.indexOf(" ") != -1) {
          value = value.substring(value.indexOf(" ") + 1);
        }
        if (value.indexOf("-") != -1) {
          value = value.substring(value.indexOf("-") + 1);
        }
        if (value.indexOf(".") != -1) {
          value = value.substring(0, value.indexOf("."));
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
      }

      if (value != null) addGlobalMeta(name, value);
    }

    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      core[0] = oldCore;
      for (int i=1; i<getSeriesCount(); i++) {
        core[i] = new CoreMetadata();
      }

      for (int i=1; i<getSeriesCount(); i++) {
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

    // determine pixel type - this isn't stored in the metadata, so we need
    // to check the pixels themselves

    int type = -1;

    Object pix = netcdf.getVariableValue("/DataSet/ResolutionLevel_" +
      (getSeriesCount() - 1) + "/TimePoint_0/Channel_0/Data");
    if (pix instanceof byte[][][]) type = FormatTools.UINT8;
    else if (pix instanceof short[][][]) type = FormatTools.UINT16;
    else if (pix instanceof int[][][]) type = FormatTools.UINT32;
    else if (pix instanceof float[][][]) type = FormatTools.FLOAT;
    else if (pix instanceof double[][][]) type = FormatTools.DOUBLE;
    else {
      throw new FormatException("Unknown pixel type: " + pix);
    }

    for (int i=0; i<getSeriesCount(); i++) {
      core[i].pixelType = type;
      core[i].dimensionOrder = "XYZCT";
      core[i].rgb = false;
      core[i].thumbSizeX = 128;
      core[i].thumbSizeY = 128;
      core[i].orderCertain = true;
      core[i].littleEndian = true;
      core[i].interleaved = false;
      core[i].indexed = false;
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<getSeriesCount(); i++) {
      float px = pixelSizeX, py = pixelSizeY, pz = pixelSizeZ;
      if (px == 1) px = (maxX - minX) / core[i].sizeX;
      if (py == 1) py = (maxY - minY) / core[i].sizeY;
      if (pz == 1) pz = (maxZ - minZ) / core[i].sizeZ;
      store.setDimensionsPhysicalSizeX(new Float(px), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(py), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pz), i, 0);
    }

    int cIndex = 0;
    String imageName = new Location(getCurrentFile()).getName();
    for (int s=0; s<getSeriesCount(); s++) {
      store.setImageName(imageName + " Resolution Level " + (s + 1), s);
      MetadataTools.setDefaultCreationDate(store, id, s);
      for (int i=0; i<core[s].sizeC; i++, cIndex++) {
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
      }
    }
  }

}
