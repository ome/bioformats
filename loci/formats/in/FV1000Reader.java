//
// FV1000Reader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * FV1000Reader is the file format reader for Fluoview FV 1000 OIB and
 * Fluoview FV 1000 OIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/FV1000Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/FV1000Reader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class FV1000Reader extends FormatReader {

  // -- Fields --

  /** Names of every TIFF file to open. */
  private Vector tiffs;

  /** Name of thumbnail file. */
  private String thumbId;

  /** Helper reader for thumbnail. */
  private BMPReader thumbReader;

  /** Used file list. */
  private Vector usedFiles;

  /** Flag indicating this is an OIB dataset. */
  private boolean isOIB;

  /** File mappings for OIB file. */
  private Hashtable oibMapping;

  private String[] code, size;
  private int imageDepth;
  private Vector previewNames;

  private String pixelSizeX, pixelSizeY;
  private Vector channelNames, emWaves, exWaves, gains, voltages, offsets;

  private POITools poi;

  // -- Constructor --

  /** Constructs a new FV1000 reader. */
  public FV1000Reader() {
    super("Fluoview FV1000", new String[] {"oib", "oif", "pty", "lut"});
    blockCheckLen = 1024;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    String extension = name;
    if (extension.indexOf(".") != -1) {
      extension = extension.substring(extension.lastIndexOf(".") + 1);
    }
    extension = extension.toLowerCase();
    if (extension.equals("oib") || extension.equals("oif")) return true;

    Location parent = new Location(name).getAbsoluteFile().getParentFile();
    String path = parent.getPath();
    path = path.substring(path.lastIndexOf(File.separator) + 1);
    if (path.indexOf(".") != -1) {
      path = path.substring(0, path.lastIndexOf("."));
    }

    Location oif = new Location(parent.getParentFile(), path);
    return oif.exists() && !oif.isDirectory();
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < blockCheckLen) return false;
    String s = DataTools.stripString(new String(block));
    return s.indexOf("FileInformation") != -1 ||
      s.indexOf("Acquisition Parameters") != -1;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    String file = (String) (series == 0 ? tiffs.get(no) : previewNames.get(no));
    RandomAccessStream plane = getFile(file);
    Hashtable[] ifds = TiffTools.getIFDs(plane);
    buf = TiffTools.getSamples(ifds[0], plane, buf, x, y, w, h);
    plane.close();
    ifds = null;
    plane = null;
    System.gc();
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    RandomAccessStream thumb = getFile(thumbId);
    byte[] b = new byte[(int) thumb.length()];
    thumb.read(b);
    thumb.close();
    Location.mapFile("thumbnail.bmp", new RABytes(b));
    thumbReader.setId("thumbnail.bmp");
    return thumbReader.openImage(0);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (usedFiles == null) return new String[] {currentId};
    return (String[]) usedFiles.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (in != null) in.close();
    if (thumbReader != null) thumbReader.close(fileOnly);

    if (!fileOnly) {
      super.close();
      tiffs = usedFiles = null;
      thumbReader = null;
      thumbId = null;
      previewNames = null;
      if (poi != null) poi.close();
      poi = null;
    }
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    close(false);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("FV1000Reader.initFile(" + id + ")");

    super.initFile(id);

    isOIB = id.toLowerCase().endsWith(".oib");

    in = new RandomAccessStream(id);
    if (isOIB) poi = new POITools(id);

    channelNames = new Vector();
    emWaves = new Vector();
    exWaves = new Vector();
    gains = new Vector();
    offsets = new Vector();
    voltages = new Vector();

    String line = null, key = null, value = null, oifName = null;

    if (isOIB) {
      RandomAccessStream ras = poi.getDocumentStream("Root/OibInfo.txt");

      oibMapping = new Hashtable();

      String s = DataTools.stripString(ras.readString((int) ras.length()));
      ras.close();
      StringTokenizer lines = new StringTokenizer(s, "\n");
      String directoryKey = null, directoryValue = null;
      while (lines.hasMoreTokens()) {
        line = lines.nextToken().trim();
        if (line.indexOf("=") != -1) {
          key = line.substring(0, line.indexOf("="));
          value = line.substring(line.indexOf("=") + 1);

          if (key.startsWith("Stream")) {
            if (directoryKey != null && directoryValue != null) {
              value = value.replaceAll(directoryKey, directoryValue);
            }
            if (value.indexOf("GST") != -1) {
              String first = value.substring(0, value.indexOf("GST"));
              String last = value.substring(value.lastIndexOf("=") + 1);
              value = first + last;
            }
            if (value.toLowerCase().endsWith(".oif")) oifName = value;
            if (directoryKey != null) {
              oibMapping.put(value, "Root/" + directoryKey + "/" + key);
            }
            else oibMapping.put(value, "Root/" + key);
          }
          else if (key.startsWith("Storage")) {
            if (value.indexOf("GST") != -1) {
              String first = value.substring(0, value.indexOf("GST"));
              String last = value.substring(value.lastIndexOf("=") + 1);
              value = first + last;
            }
            directoryKey = key;
            directoryValue = value;
          }
        }
      }
      s = null;
    }
    else {
      // make sure we have the OIF file, not a TIFF
      if (!id.toLowerCase().endsWith(".oif")) {
        Location current = new Location(id).getAbsoluteFile();
        String parent = current.getParent();
        Location tmp = new Location(parent);
        parent = tmp.getParent();

        id = current.getPath();
        String oifFile = id.substring(id.lastIndexOf(File.separator));
        oifFile =
          parent + oifFile.substring(0, oifFile.lastIndexOf("_")) + ".oif";

        tmp = new Location(oifFile);
        if (!tmp.exists()) {
          oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
          tmp = new Location(oifFile);
          if (!tmp.exists()) throw new FormatException("OIF file not found");
          currentId = oifFile;
        }
        else currentId = oifFile;
        super.initFile(currentId);
        in = new RandomAccessStream(currentId);
        oifName = currentId;
      }
      else oifName = currentId;
    }

    String path = new Location(oifName).getAbsoluteFile().getAbsolutePath();
    String filename =
      isOIB ? path.substring(path.lastIndexOf(File.separator) + 1) : path;
    path = isOIB ? "" : path.substring(0, path.lastIndexOf(File.separator) + 1);

    RandomAccessStream oif = null;
    try {
      oif = getFile(filename.substring(0, filename.lastIndexOf(".")) + ".oif");
    }
    catch (IOException e) {
      oif = getFile(filename.substring(0, filename.lastIndexOf(".")) + ".OIF");
    }

    String s = oif.readString((int) oif.length());
    oif.close();

    code = new String[9];
    size = new String[9];

    StringTokenizer st = new StringTokenizer(s, "\r\n");

    previewNames = new Vector();

    Hashtable filenames = new Hashtable();
    String prefix = "";
    while (st.hasMoreTokens()) {
      line = DataTools.stripString(st.nextToken().trim());
      if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
        key = line.substring(0, line.indexOf("=")).trim();
        value = line.substring(line.indexOf("=") + 1).trim();
        if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1 &&
          value.indexOf("-R") == -1)
        {
          value = value.replaceAll("/", File.separator);
          value = value.replace('\\', File.separatorChar);
          while (value.indexOf("GST") != -1) {
            String first = value.substring(0, value.indexOf("GST"));
            int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
              value.length() : value.indexOf(File.separator);
            String last = value.substring(value.lastIndexOf("=", ndx) + 1);
            value = first + last;
          }
          filenames.put(new Integer(key.substring(11)), value.trim());
        }
        else if (key.indexOf("Thumb") != -1) {
          value = value.replaceAll("/", File.separator);
          value = value.replace('\\', File.separatorChar);
          while (value.indexOf("GST") != -1) {
            String first = value.substring(0, value.indexOf("GST"));
            int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
              value.length() : value.indexOf(File.separator);
            String last = value.substring(value.lastIndexOf("=", ndx) + 1);
            value = first + last;
          }
          if (thumbId == null) thumbId = value.trim();
        }
        else if (value.indexOf("-R") != -1) {
          value = value.replaceAll("/", File.separator);
          value = value.replace('\\', File.separatorChar);
          while (value.indexOf("GST") != -1) {
            String first = value.substring(0, value.indexOf("GST"));
            int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
              value.length() : value.indexOf(File.separator);
            String last = value.substring(value.lastIndexOf("=", ndx) + 1);
            value = first + last;
          }
          if (value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
          }
          previewNames.add(path + value.trim());
        }
        addMeta(prefix + key, value);

        if (prefix.startsWith("[Axis ") &&
          prefix.endsWith("Parameters Common] - "))
        {
          int ndx =
            Integer.parseInt(prefix.substring(6, prefix.indexOf("P")).trim());
          if (key.equals("AxisCode")) code[ndx] = value;
          else if (key.equals("MaxSize")) size[ndx] = value;
        }
        else if ((prefix + key).equals(
          "[Reference Image Parameter] - ImageDepth"))
        {
          imageDepth = Integer.parseInt(value);
        }
        else if ((prefix + key).equals(
          "[Reference Image Parameter] - WidthConvertValue"))
        {
          pixelSizeX = value;
        }
        else if ((prefix + key).equals(
          "[Reference Image Parameter] - HeightConvertValue"))
        {
          pixelSizeY = value;
        }
        else if (prefix.indexOf("[Channel ") != -1 &&
          prefix.indexOf("Parameters] - ") != -1)
        {
          if (key.equals("CH Name")) channelNames.add(value);
          else if (key.equals("EmissionWavelength")) emWaves.add(value);
          else if (key.equals("ExcitationWavelength")) exWaves.add(value);
          else if (key.equals("CountingPMTGain")) gains.add(value);
          else if (key.equals("CountingPMTVoltage")) voltages.add(value);
          else if (key.equals("CountingPMTOffset")) offsets.add(value);
        }
      }
      else if (line.length() > 0) {
        if (line.indexOf("[") == 2) {
          line = line.substring(2, line.length());
        }
        prefix = line + " - ";
      }
    }

    int reference = ((String) filenames.get(new Integer(0))).length();
    int numFiles = filenames.size();
    for (int i=0; i<numFiles; i++) {
      Integer ii = new Integer(i);
      value = (String) filenames.get(ii);
      if (value != null) {
        if (value.length() > reference) filenames.remove(ii);
      }
    }

    status("Initializing helper readers");

    if (previewNames.size() > 0) {
      Vector v = new Vector();
      for (int i=0; i<previewNames.size(); i++) {
        String ss = (String) previewNames.get(i);
        ss = ss.replaceAll("pty", "tif");
        if (ss.endsWith(".tif")) v.add(ss);
      }
      previewNames = v;
      String previewName = (String) previewNames.get(0);
      core = new CoreMetadata(2);
      Hashtable[] ifds = TiffTools.getIFDs(getFile(previewName));
      core.imageCount[1] = ifds.length * previewNames.size();
      core.sizeX[1] = (int) TiffTools.getImageWidth(ifds[0]);
      core.sizeY[1] = (int) TiffTools.getImageLength(ifds[0]);
      core.sizeZ[1] = 1;
      core.sizeT[1] = 1;
      core.sizeC[1] = core.imageCount[1];
      core.rgb[1] = false;
      int bits = TiffTools.getBitsPerSample(ifds[0])[0];
      while ((bits % 8) != 0) bits++;
      switch (bits) {
        case 8:
          core.pixelType[1] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[1] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[1] = FormatTools.UINT32;
      }
      core.currentOrder[1] = "XYCZT";
      core.indexed[1] = false;
    }

    thumbReader = new BMPReader();
    core.imageCount[0] = filenames.size();
    tiffs = new Vector(core.imageCount[0]);

    thumbId = thumbId.replaceAll("pty", "bmp");
    thumbId = sanitizeFile(thumbId, isOIB ? "" : path);
    if (isOIB) thumbId = thumbId.substring(1);

    status("Reading additional metadata");

    // open each INI file (.pty extension)

    String tiffPath = null;

    for (int i=0, ii=0; ii<core.imageCount[0]; i++, ii++) {
      String file = (String) filenames.get(new Integer(i));
      while (file == null) file = (String) filenames.get(new Integer(++i));
      file = sanitizeFile(file, isOIB ? "" : path);

      tiffPath = file.substring(0, file.lastIndexOf(File.separator));
      RandomAccessStream ptyReader = getFile(file);
      s = ptyReader.readString((int) ptyReader.length());
      ptyReader.close();
      st = new StringTokenizer(s, "\n");

      while (st.hasMoreTokens()) {
        line = st.nextToken().trim();
        if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
          key = line.substring(0, line.indexOf("=") - 1).trim();
          value = line.substring(line.indexOf("=") + 1).trim();
          key = DataTools.stripString(key);
          value = DataTools.stripString(value);
          if (key.equals("DataName")) {
            value = value.substring(1, value.length() - 1);
            if (value.indexOf("-R") == -1) {
              value = value.replaceAll("/", File.separator);
              value = value.replace('\\', File.separatorChar);
              while (value.indexOf("GST") != -1) {
                String first = value.substring(0, value.indexOf("GST"));
                int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
                  value.length() : value.indexOf(File.separator);
                String last = value.substring(value.lastIndexOf("=", ndx) + 1);
                value = first + last;
              }
              tiffs.add(ii, tiffPath + File.separator + value);
            }
          }
          addMeta("Image " + ii + " : " + key, value);
        }
      }
    }

    usedFiles = new Vector();

    if (tiffPath != null) {
      if (isOIB) usedFiles.add(id);
      else {
        Location dir = new Location(tiffPath);
        String[] list = dir.list();
        for (int i=0; i<list.length; i++) {
          usedFiles.add(new Location(tiffPath, list[i]).getAbsolutePath());
        }
      }
    }

    status("Populating metadata");

    int realChannels = 0;
    for (int i=0; i<9; i++) {
      int ss = Integer.parseInt(size[i]);
      code[i] = code[i].substring(1, code[i].length() - 1);
      if (code[i].equals("X")) core.sizeX[0] = ss;
      else if (code[i].equals("Y")) core.sizeY[0] = ss;
      else if (code[i].equals("Z")) core.sizeZ[0] = ss;
      else if (code[i].equals("T")) core.sizeT[0] = ss;
      else if (ss > 0) {
        if (core.sizeC[0] == 0) core.sizeC[0] = ss;
        else core.sizeC[0] *= ss;
        if (code[i].equals("C")) realChannels = ss;
      }
    }

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    if (core.imageCount[0] == core.sizeC[0]) {
      core.sizeZ[0] = 1;
      core.sizeT[0] = 1;
    }

    if (core.sizeZ[0] * core.sizeT[0] * core.sizeC[0] > core.imageCount[0]) {
      int diff =
        (core.sizeZ[0] * core.sizeC[0] * core.sizeT[0]) - core.imageCount[0];
      if (diff == previewNames.size()) {
        if (core.sizeT[0] > 1 && core.sizeZ[0] == 1) core.sizeT[0] -= diff;
        else if (core.sizeZ[0] > 1 && core.sizeT[0] == 1) core.sizeZ[0] -= diff;
      }
      else core.imageCount[0] += diff;
    }

    core.currentOrder[0] = "XYCZT";

    switch (imageDepth) {
      case 1:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 2:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 4:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      default:
        throw new RuntimeException("Unsupported pixel depth: " + imageDepth);
    }

    in.close();
    in = null;

    RandomAccessStream thumb = getFile(thumbId);
    if (thumb != null) {
      byte[] b = new byte[(int) thumb.length()];
      thumb.read(b);
      thumb.close();
      Location.mapFile("thumbnail.bmp", new RABytes(b));
      thumbReader.setId("thumbnail.bmp");
      Arrays.fill(core.thumbSizeX, thumbReader.getSizeX());
      Arrays.fill(core.thumbSizeY, thumbReader.getSizeY());
    }

    core.rgb[0] = false;
    core.littleEndian[0] = false;
    core.interleaved[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    // populate MetadataStore

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    for (int i=0; i<core.sizeX.length; i++) {
      store.setImageName("Series " + i, i);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
    }

    MetadataTools.populatePixels(store, this);

    if (pixelSizeX != null) {
      store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
    }
    if (pixelSizeY != null) {
      store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);
    }

    for (int i=0; i<core.sizeC[0]; i++) {
      String name = i < channelNames.size() ? (String) channelNames.get(i) : "";
      String emWave = i < emWaves.size() ? (String) emWaves.get(i) : "";
      String exWave = i < exWaves.size() ? (String) exWaves.get(i) : "";

      String gain = i < gains.size() ? (String) gains.get(i) : "";
      String voltage = i < voltages.size() ? (String) voltages.get(i) : "";
      String offset = i < offsets.size() ? (String) offsets.get(i) : "";

      // CTR CHECK
      /*
      if (gain != null) {
        gain = gain.replaceAll("\"", "");
        store.setDetectorSettingsGain(new Float(gain), 0, i);
      }
      if (voltage != null) {
        voltage = voltage.replaceAll("\"", "");
        store.setDetectorVoltage(new Float(voltage), 0, 0);
      }
      if (offset != null) {
        offset = offset.replaceAll("\"", "");
        store.setDetectorSettingsOffset(new Float(offset), 0, i);
      }
      */
    }
  }

  // -- Helper methods --

  private String sanitizeFile(String file, String path) {
    String f = file.substring(1, file.length() - 1);
    f = f.replace('\\', File.separatorChar);
    f = f.replace('/', File.separatorChar);
    return path + File.separator + f;
  }

  private RandomAccessStream getFile(String name)
    throws FormatException, IOException
  {
    if (isOIB) {
      if (name.startsWith("/") || name.startsWith("\\")) {
        name = name.substring(1);
      }
      name = name.replace('\\', '/');
      return poi.getDocumentStream((String) oibMapping.get(name));
    }
    else return new RandomAccessStream(name);
  }

}
