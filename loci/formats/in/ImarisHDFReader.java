//
// ImarisHDFReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * Reader for Bitplane Imaris 5.5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ImarisHDFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ImarisHDFReader.java">SVN</a></dd></dl>
 */
public class ImarisHDFReader extends FormatReader {

  // -- Fields --

  private int previousSeries;
  private Object previousImage;
  private int previousImageNumber;
  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private float minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private NetcdfTools netcdf;

  // channel parameters
  private Vector emWave, exWave, channelMin, channelMax;
  private Vector gain, pinhole, channelName, microscopyMode;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    blockCheckLen = 8;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readString(8).indexOf("HDF") >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int[] zct = FormatTools.getZCTCoords(this, no);
    if (previousImageNumber > getImageCount()) previousImageNumber = -1;
    int[] oldZCT = previousImageNumber == -1 ? new int[] {-1, -1, -1} :
      FormatTools.getZCTCoords(this, previousImageNumber);

    // pixel data is stored in XYZ blocks

    if (zct[1] != oldZCT[1] || zct[2] != oldZCT[2] || series != previousSeries)
    {
      previousImage = netcdf.getVariableValue("/DataSet/ResolutionLevel_" +
        series + "/TimePoint_" + zct[2] + "/Channel_" + zct[1] + "/Data");
    }
    previousImageNumber = no;

    for (int row=0; row<h; row++) {
      if (previousImage instanceof byte[][][]) {
        System.arraycopy(((byte[][][]) previousImage)[zct[0]][row + y], x, buf,
          row*w, w);
      }
      else if (previousImage instanceof short[][][]) {
        for (int i=0; i<w; i++) {
          DataTools.unpackShort(
            ((short[][][]) previousImage)[zct[0]][row + y][x + i], buf,
            2 * (row * w + i), !isLittleEndian());
        }
      }
      else if (previousImage instanceof int[][][]) {
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(
            ((int[][][]) previousImage)[zct[0]][row + y][x + i], buf,
            4 * (row * w + i), 4, !isLittleEndian());
        }
      }
      else if (previousImage instanceof float[][][]) {
        float[] s = ((float[][][]) previousImage)[zct[0]][row + y];
        int base = row * w * 4;
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(s[x + i]);
          DataTools.unpackBytes(v, buf, base + i*4, 4, !isLittleEndian());
        }
      }
    }
    previousSeries = series;

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    previousSeries = -1;
    previousImageNumber = -1;
    previousImage = null;
    seriesCount = 0;
    pixelSizeX = pixelSizeY = pixelSizeZ = 0;
    minX = minY = minZ = maxX = maxY = maxZ = 0;

    if (netcdf != null) netcdf.close();
    netcdf = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    netcdf = new NetcdfTools(id);

    pixelSizeX = pixelSizeY = pixelSizeZ = 1.0f;

    emWave = new Vector();
    exWave = new Vector();
    channelMin = new Vector();
    channelMax = new Vector();
    gain = new Vector();
    pinhole = new Vector();
    channelName = new Vector();
    microscopyMode = new Vector();

    seriesCount = 0;

    previousImageNumber = -1;
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    // read all of the metadata key/value pairs

    Vector attributes = netcdf.getAttributeList();
    for (int i=0; i<attributes.size(); i++) {
      String attr = (String) attributes.get(i);
      String name = attr.substring(attr.lastIndexOf("/") + 1);
      String value = netcdf.getAttributeValue(attr);
      if (value == null) continue;
      value = value.trim();

      if (name.equals("X")) {
        core[0].sizeX = Integer.parseInt(value);
      }
      else if (name.equals("Y")) {
        core[0].sizeY = Integer.parseInt(value);
      }
      else if (name.equals("Z")) {
        core[0].sizeZ = Integer.parseInt(value);
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

      if (value != null) addMeta(name, value);
    }

    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      core[0] = oldCore;
      for (int i=1; i<seriesCount; i++) {
        core[i] = new CoreMetadata();
      }

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
      }
    }
    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();

    // determine pixel type - this isn't stored in the metadata, so we need
    // to check the pixels themselves

    int type = -1;

    Object pix = netcdf.getVariableValue(
      "/DataSet/ResolutionLevel_0/TimePoint_0/Channel_0/Data");
    if (pix instanceof byte[][][]) type = FormatTools.UINT8;
    else if (pix instanceof short[][][]) type = FormatTools.UINT16;
    else if (pix instanceof int[][][]) type = FormatTools.UINT32;
    else if (pix instanceof float[][][]) type = FormatTools.FLOAT;

    for (int i=0; i<core.length; i++) {
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

    MetadataTools.populatePixels(store, this);
    for (int i=0; i<seriesCount; i++) {
      float px = pixelSizeX, py = pixelSizeY, pz = pixelSizeZ;
      if (px == 1) px = (maxX - minX) / core[i].sizeX;
      if (py == 1) py = (maxY - minY) / core[i].sizeY;
      if (pz == 1) pz = (maxZ - minZ) / core[i].sizeZ;
      store.setDimensionsPhysicalSizeX(new Float(px), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(py), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pz), i, 0);
    }

    int cIndex = 0;
    for (int s=0; s<seriesCount; s++) {
      store.setImageName("Resolution Level " + s, s);
      MetadataTools.setDefaultCreationDate(store, id, s);
      for (int i=0; i<core[s].sizeC; i++) {
        Float gainValue = null;
        Integer pinholeValue = null, emWaveValue = null, exWaveValue;

        if (cIndex < gain.size()) {
          try {
            gainValue = new Float((String) gain.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
        }
        if (cIndex < pinhole.size()) {
          try {
            pinholeValue = new Integer((String) pinhole.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
        }
        if (cIndex < emWave.size()) {
          try {
            emWaveValue = new Integer((String) emWave.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
        }
        if (cIndex < exWave.size()) {
          try {
            exWaveValue = new Integer((String) exWave.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
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
            minValue = new Double((String) channelMin.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
        }
        if (cIndex < channelMax.size()) {
          try {
            maxValue = new Double((String) channelMax.get(cIndex));
          }
          catch (NumberFormatException e) {
            if (debug) LogTools.trace(e);
          }
        }

        cIndex++;
      }
    }
  }

}
