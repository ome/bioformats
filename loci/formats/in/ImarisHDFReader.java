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

/**
 * Reader for Imaris 5.5 (HDF) files.
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
    return r;
  }

  // -- Fields --

  private byte[][][] previousImage;
  private int previousImageNumber;
  private Vector channelParameters;
  private float pixelSizeX, pixelSizeY, pixelSizeZ;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public ImarisHDFReader() { super("Imaris 5.5 (HDF)", "ims"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return new String(block).indexOf("HDF") != -1;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int[] zct = FormatTools.getZCTCoords(this, no);
    int[] oldZCT = previousImageNumber == -1 ? new int[] {-1, -1, -1} :
      FormatTools.getZCTCoords(this, previousImageNumber);
    if (zct[1] != oldZCT[1] || zct[2] != oldZCT[2]) {
      try {
        r.exec("ncfile = NetcdfFile.open(currentId)");
        r.exec("g = ncfile.getRootGroup()");
        findGroup("DataSet", "g", "g");
        findGroup("ResolutionLevel_0", "g", "g");
        findGroup("TimePoint_" + zct[2], "g", "g");
        findGroup("Channel_" + zct[1], "g", "g");
        r.setVar("name", "Data");
        r.exec("var = g.findVariable(name)");
        r.exec("pixelData = var.read()");
        r.exec("data = pixelData.copyToNDJavaArray()");
        previousImage = (byte[][][]) r.getVar("data");
      }
      catch (ReflectException exc) {
        if (debug) LogTools.trace(exc);
        return null;
      }
    }
    previousImageNumber = no;

    for (int y=0; y<core.sizeY[0]; y++) {
      System.arraycopy(previousImage[zct[0]][y], 0, buf, y*core.sizeX[0],
        core.sizeX[0]);
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false;
    if (!open) return true;
    return checkBytes(name, 8);
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    previousImageNumber = -1;
    previousImage = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (noNetCDF) throw new FormatException(NO_NETCDF_MSG);

    previousImageNumber = -1;
    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);

    try {
      r.setVar("currentId", id);
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
    }
    catch (ReflectException exc) {
      if (debug) LogTools.trace(exc);
    }

    core.currentOrder[0] = "XYZCT";
    core.rgb[0] = false;
    core.thumbSizeX[0] = 128;
    core.thumbSizeY[0] = 128;
    core.pixelType[0] = FormatTools.UINT8;
    core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
    core.orderCertain[0] = true;
    core.littleEndian[0] = true;
    core.interleaved[0] = false;
    core.indexed[0] = false;

    FormatTools.populatePixels(store, this);
    store.setDimensions(new Float(pixelSizeX), new Float(pixelSizeY),
      new Float(pixelSizeZ), null, null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
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

      store.setLogicalChannel(i, params[6], null,
        null, null, null, null, null, null, null, gainValue, null,
        pinholeValue, null, params[7], null, null, null, null,
        null, emWaveValue, exWaveValue, null, null, null);

      Double minValue = null, maxValue = null;
      try { minValue = new Double(params[4]); }
      catch (NumberFormatException exc) { }
      catch (NullPointerException exc) { }
      try { maxValue = new Double(params[3]); }
      catch (NumberFormatException exc) { }
      catch (NullPointerException exc) { }

      if (minValue != null && maxValue != null) {
        store.setChannelGlobalMinMax(i, minValue, maxValue, null);
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
          else if (name.equals("Max")) params[3] = v;
          else if (name.equals("Min")) params[4] = v;
          else if (name.equals("Pinhole")) params[5] = v;
          else if (name.equals("Name")) params[6] = v;
          else if (name.equals("MicroscopyMode")) params[7] = v;
        }
      }

      if (groupName.indexOf("/Channel_") != -1) {
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

      r.exec("groups = group.getGroups()");
      parseGroups((List) r.getVar("groups"));
    }
  }

}
