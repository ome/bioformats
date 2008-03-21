//
// ImarisHDFReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
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

  // -- Constants --

  private static final String NO_NETCDF_MSG =
    "NetCDF is required to read Imaris 5.5 files.  Please obtain " +
    "the necessary JAR files from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noNetCDF = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ucar.ma2.Array");
      r.exec("import ucar.ma2.ArrayByte");
      r.exec("import ucar.nc2.Attribute");
      r.exec("import ucar.nc2.Group");
      r.exec("import ucar.nc2.NetcdfFile");
    }
    catch (ReflectException exc) {
      noNetCDF = true;
      if (debug) LogTools.trace(exc);
    }
    catch (UnsupportedClassVersionError exc) {
      noNetCDF = true;
      if (debug) LogTools.trace(exc);
    }
    return r;
  }

  // -- Fields --

  private int previousSeries;
  private Object previousImage;
  private int previousImageNumber;
  private Vector channelParameters;
  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private float minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    blockCheckLen = 8;
    suffixSufficient = false;
    OutputFilter out = new OutputFilter(System.out);
    System.setOut(out);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < blockCheckLen) return false;
    return new String(block, 0, 8).indexOf("HDF") >= 0;
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

    if (zct[1] != oldZCT[1] || zct[2] != oldZCT[2] || series != previousSeries)
    {
      try {
        r.exec("ncfile = NetcdfFile.open(currentId)");
        r.exec("g = ncfile.getRootGroup()");
        findGroup("DataSet", "g", "g");
        findGroup("ResolutionLevel_" + series, "g", "g");
        findGroup("TimePoint_" + zct[2], "g", "g");
        findGroup("Channel_" + zct[1], "g", "g");
        r.setVar("name", "Data");
        r.exec("var = g.findVariable(name)");
        r.exec("pixelData = var.read()");
        r.exec("data = pixelData.copyToNDJavaArray()");
        previousImage = r.getVar("data");
      }
      catch (ReflectException exc) {
        if (debug) LogTools.trace(exc);
        return null;
      }
    }
    previousImageNumber = no;

    for (int row=0; row<h; row++) {
      if (previousImage instanceof byte[][][]) {
        System.arraycopy(((byte[][][]) previousImage)[zct[0]][row + y], x, buf,
          row*w, w);
      }
      else if (previousImage instanceof short[][][]) {
        short[] s = ((short[][][]) previousImage)[zct[0]][row + y];
        for (int i=0; i<w; i++) {
          buf[row*w*2 + i*2] = (byte) ((s[x + i] >> 8) & 0xff);
          buf[row*w*2 + i*2 + 1] = (byte) (s[x + i] & 0xff);
        }
      }
      else if (previousImage instanceof int[][][]) {
        int[] s = ((int[][][]) previousImage)[zct[0]][row + y];
        int base = row * w * 4;
        for (int i=0; i<w; i++) {
          buf[base + i*4] = (byte) ((s[x + i] >> 24) & 0xff);
          buf[base + i*4 + 1] = (byte) ((s[x + i] >> 16) & 0xff);
          buf[base + i*4 + 2] = (byte) ((s[x + i] >> 8) & 0xff);
          buf[base + i*4 + 3] = (byte) (s[x + i] & 0xff);
        }
      }
      else if (previousImage instanceof float[][][]) {
        float[] s = ((float[][][]) previousImage)[zct[0]][row + y];
        int base = row * w * 4;
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(s[x + i]);
          buf[base + i*4] = (byte) ((v >> 24) & 0xff);
          buf[base + i*4 + 1] = (byte) ((v >> 16) & 0xff);
          buf[base + i*4 + 2] = (byte) ((v >> 8) & 0xff);
          buf[base + i*4 + 3] = (byte) (v & 0xff);
        }
      }
    }
    previousSeries = series;

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    previousSeries = -1;
    previousImageNumber = -1;
    previousImage = null;
    seriesCount = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (noNetCDF) throw new FormatException(NO_NETCDF_MSG);

    pixelSizeX = pixelSizeY = pixelSizeZ = 1.0f;

    seriesCount = 0;

    previousImageNumber = -1;
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    try {
      r.setVar("currentId", Location.getMappedId(id));
      r.exec("ncfile = NetcdfFile.open(currentId)");
      r.exec("root = ncfile.getRootGroup()");
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }

    getValue("root", "ImarisDataSet");
    getValue("root", "ImarisVersion");

    findGroup("DataSetInfo", "root", "dataSetInfo");
    findGroup("DataSet", "root", "dataSet");

    channelParameters = new Vector();

    try {
      List l = new Vector();
      l.add(r.getVar("dataSetInfo"));
      parseGroups(l);
      l.clear();
      l.add(r.getVar("dataSet"));
      parseGroups(l);
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }

    if (seriesCount > 1) {
      int x = core.sizeX[0];
      int y = core.sizeY[0];
      int z = core.sizeZ[0];
      int c = core.sizeC[0];
      int t = core.sizeT[0];
      core = new CoreMetadata(seriesCount);
      Arrays.fill(core.sizeX, x);
      Arrays.fill(core.sizeY, y);
      Arrays.fill(core.sizeZ, z);
      Arrays.fill(core.sizeC, c);
      Arrays.fill(core.sizeT, t);

      for (int i=1; i<seriesCount; i++) {
        findGroup("ResolutionLevel_" + i, "dataSet", "g");
        findGroup("TimePoint_0", "g", "g");
        findGroup("Channel_0", "g", "g");
        core.sizeX[i] = Integer.parseInt(getValue("g", "ImageSizeX"));
        core.sizeY[i] = Integer.parseInt(getValue("g", "ImageSizeY"));
        core.sizeZ[i] = Integer.parseInt(getValue("g", "ImageSizeZ"));
        core.imageCount[i] = core.sizeZ[i] * core.sizeC[0] * core.sizeT[0];
      }
    }
    core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];

    try {
      findGroup("ResolutionLevel_0", "dataSet", "g");
      findGroup("TimePoint_0", "g", "g");
      findGroup("Channel_0", "g", "g");
      r.setVar("name", "Data");
      r.exec("var = g.findVariable(name)");
      r.exec("pixelData = var.read()");
      r.exec("data = pixelData.copyToNDJavaArray()");
      Object pix = r.getVar("data");
      if (pix instanceof byte[][][]) {
        Arrays.fill(core.pixelType, FormatTools.UINT8);
      }
      else if (pix instanceof short[][][]) {
        Arrays.fill(core.pixelType, FormatTools.UINT16);
      }
      else if (pix instanceof int[][][]) {
        Arrays.fill(core.pixelType, FormatTools.UINT32);
      }
      else if (pix instanceof float[][][]) {
        Arrays.fill(core.pixelType, FormatTools.FLOAT);
      }
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }

    Arrays.fill(core.currentOrder, "XYZCT");
    Arrays.fill(core.rgb, false);
    Arrays.fill(core.thumbSizeX, 128);
    Arrays.fill(core.thumbSizeY, 128);
    Arrays.fill(core.orderCertain, true);
    Arrays.fill(core.littleEndian, true);
    Arrays.fill(core.interleaved, false);
    Arrays.fill(core.indexed, false);

    MetadataTools.populatePixels(store, this);
    for (int i=0; i<seriesCount; i++) {
      float px = pixelSizeX, py = pixelSizeY, pz = pixelSizeZ;
      if (px == 1) px = (maxX - minX) / core.sizeX[i];
      if (py == 1) py = (maxY - minY) / core.sizeY[i];
      if (pz == 1) pz = (maxZ - minZ) / core.sizeZ[i];
      store.setDimensionsPhysicalSizeX(new Float(px), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(py), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pz), i, 0);
    }

    for (int s=0; s<seriesCount; s++) {
      store.setImageName("Resolution Level " + s, s);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), s);
      for (int i=0; i<core.sizeC[s]; i++) {
        String[] params = (String[]) channelParameters.get(i);

        Float gainValue = null;
        try { gainValue = new Float(params[0]); }
        catch (NumberFormatException e) { }
        catch (NullPointerException e) { }
        Integer pinholeValue = null, emWaveValue = null, exWaveValue = null;
        try { pinholeValue = new Integer(params[5]); }
        catch (NumberFormatException e) { }
        catch (NullPointerException e) { }
        try {
          if (params[1].indexOf("-") != -1) {
            params[1] = params[1].substring(params[1].indexOf("-") + 1);
          }
          emWaveValue = new Integer(params[1]);
        }
        catch (NumberFormatException e) { }
        catch (NullPointerException e) { }
        try {
          if (params[2].indexOf("-") != -1) {
            params[2] = params[2].substring(params[2].indexOf("-") + 1);
          }
          exWaveValue = new Integer(params[2]);
        }
        catch (NumberFormatException e) { }
        catch (NullPointerException e) { }

        // CHECK
        /*
        store.setLogicalChannelName(params[6], s, i);
        store.setDetectorSettingsGain(gainValue, s, i);
        store.setLogicalChannelPinholeSize(pinholeValue, s, i);
        store.setLogicalChannelMode(params[7], s, i);
        store.setLogicalChannelEmWave(emWaveValue, s, i);
        store.setLogicalChannelExWave(exWaveValue, s, i);
        */

        Double minValue = null, maxValue = null;
        try { minValue = new Double(params[4]); }
        catch (NumberFormatException exc) { }
        catch (NullPointerException exc) { }
        try { maxValue = new Double(params[3]); }
        catch (NumberFormatException exc) { }
        catch (NullPointerException exc) { }

        // CTR CHECK
//        if (minValue != null && maxValue != null && maxValue.doubleValue() > 0)
//        {
//          store.setChannelGlobalMinMax(i, minValue, maxValue, new Integer(s));
//        }
      }
    }
  }

  // -- Helper methods --

  private String getValue(String group, String name) {
    try {
      r.setVar("name", name);
      r.exec("attribute = " + group + ".findAttribute(name)");
      if (r.getVar("attribute") == null) return null;
      r.exec("isString = attribute.isString()");
      if (!((Boolean) r.getVar("isString")).booleanValue()) return null;
      r.exec("array = attribute.getValues()");
      r.exec("s = array.copyTo1DJavaArray()");
      Object[] s = (Object[]) r.getVar("s");
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<s.length; i++) {
        sb.append((String) s[i]);
      }
      String st = sb.toString();

      if (name.equals("X")) {
        core.sizeX[0] = Integer.parseInt(st.trim());
      }
      else if (name.equals("Y")) {
        core.sizeY[0] = Integer.parseInt(st.trim());
      }
      else if (name.equals("Z")) {
        core.sizeZ[0] = Integer.parseInt(st.trim());
      }
      else if (name.equals("FileTimePoints")) {
        core.sizeT[0] = Integer.parseInt(st.trim());
      }
      else if (name.equals("RecordingEntrySampleSpacing")) {
        pixelSizeX = Float.parseFloat(st.trim());
      }
      else if (name.equals("RecordingEntryLineSpacing")) {
        pixelSizeY = Float.parseFloat(st.trim());
      }
      else if (name.equals("RecordingEntryPlaneSpacing")) {
        pixelSizeZ = Float.parseFloat(st.trim());
      }
      else if (name.equals("ExtMax0")) maxX = Float.parseFloat(st.trim());
      else if (name.equals("ExtMax1")) maxY = Float.parseFloat(st.trim());
      else if (name.equals("ExtMax2")) maxZ = Float.parseFloat(st.trim());
      else if (name.equals("ExtMin0")) minX = Float.parseFloat(st.trim());
      else if (name.equals("ExtMin1")) minY = Float.parseFloat(st.trim());
      else if (name.equals("ExtMin2")) minZ = Float.parseFloat(st.trim());

      if (st != null) addMeta(name, st);
      return st;
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }
    return null;
  }

  private Object findGroup(String name, String parent, String store) {
    try {
      r.setVar("name", name);
      r.exec(store + " = " + parent + ".findGroup(name)");
      return r.getVar(store);
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }
    return null;
  }

  private void parseGroups(List groups) throws ReflectException {
    for (int i=0; i<groups.size(); i++) {
      r.setVar("group", groups.get(i));
      r.exec("groupName = group.getName()");
      String groupName = (String) r.getVar("groupName");
      if (debug) LogTools.println("Parsing group: " + groupName);

      if (groupName.startsWith("/DataSet/ResolutionLevel_")) {
        int slash = groupName.indexOf("/", 25);
        int n = Integer.parseInt(groupName.substring(25,
          slash == -1 ? groupName.length() : slash));
        if (n == seriesCount) seriesCount++;
      }

      r.exec("attributes = group.getAttributes()");
      List l = (List) r.getVar("attributes");
      String[] params = new String[8];
      for (int j=0; j<l.size(); j++) {
        r.setVar("attr", l.get(j));
        r.exec("name = attr.getName()");
        String name = (String) r.getVar("name");
        String v = getValue("group", (String) r.getVar("name"));
        if (groupName.startsWith("/DataSetInfo/Channel_")) {
          if (name.equals("Gain")) params[0] = v;
          else if (name.equals("LSMEmissionWavelength")) params[1] = v;
          else if (name.equals("LSMExcitationWavelength")) params[2] = v;
          else if (name.equals("Max")) {
            params[3] = v;
          }
          else if (name.equals("Min")) {
            params[4] = v;
          }
          else if (name.equals("Pinhole")) params[5] = v;
          else if (name.equals("Name")) params[6] = v;
          else if (name.equals("MicroscopyMode")) params[7] = v;
        }
      }

      if (groupName.indexOf("/Channel_") != -1) {
        int ndx = groupName.indexOf("/Channel_") + 9;
        int end = groupName.indexOf("/", ndx);
        if (end == -1) end = groupName.length();
        int n = Integer.parseInt(groupName.substring(ndx, end));
        if (n == core.sizeC[0]) {
          for (int j=0; j<6; j++) {
            if (params[j] != null) {
              if (params[j].indexOf(" ") != -1) {
                params[j] = params[j].substring(params[j].indexOf(" ") + 1);
              }
              if (params[j].indexOf("-") != -1) {
                params[j] = params[j].substring(params[j].indexOf("-") + 1);
              }
              if (params[j].indexOf(".") != -1) {
                params[j] = params[j].substring(0, params[j].indexOf("."));
              }
            }
          }

          channelParameters.add(params);
          core.sizeC[0]++;
        }
      }

      r.exec("groups = group.getGroups()");
      parseGroups((List) r.getVar("groups"));
    }
  }

}
