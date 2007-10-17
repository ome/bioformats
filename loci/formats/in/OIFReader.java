//
// OIFReader.java
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

/**
 * OIFReader is the file format reader for Fluoview FV 1000 OIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OIFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OIFReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OIFReader extends FormatReader {

  // -- Fields --

  /** Names of every TIFF file to open. */
  protected Vector tiffs;

  /** Helper reader to open TIFF files. */
  protected TiffReader[] tiffReader;

  /** Helper reader to open the thumbnail. */
  protected BMPReader thumbReader;

  /** List of files in the current OIF dataset. */
  protected Vector usedFiles;

  protected String[] size = new String[9], code = new String[9];
  protected int imageDepth;

  // -- Constructor --

  /** Constructs a new OIF reader. */
  public OIFReader() {
    super("Fluoview FV1000 OIF",
      new String[] {"oif", "roi", "pty", "lut", "bmp"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    tiffReader[no].openBytes(0, buf);
    tiffReader[no].close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    String dir =
      currentId.substring(0, currentId.lastIndexOf(File.separator) + 1);
    dir += currentId.substring(currentId.lastIndexOf(File.separator) + 1) +
      ".files" + File.separator;

    String thumbId = dir + currentId.substring(currentId.lastIndexOf(
      File.separator) + 1, currentId.lastIndexOf(".")) + "_Thumb.bmp";
    thumbReader.setId(thumbId);
    return thumbReader.openImage(0);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return (String[]) usedFiles.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
      if (thumbReader != null) thumbReader.close(fileOnly);
      if (tiffReader != null) {
        for (int i=0; i<tiffReader.length; i++) {
          if (tiffReader[i] != null) tiffReader[i].close(fileOnly);
        }
      }
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (thumbReader != null) thumbReader.close();
    if (tiffReader != null) {
      for (int i=0; i<tiffReader.length; i++) {
        if (tiffReader[i] != null) tiffReader[i].close();
      }
    }
    tiffs = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OIFReader.initFile(" + id + ")");

    // check to make sure that we have the OIF file
    // if not, we need to look for it in the parent directory

    status("Finding metadata file");

    String oifFile = id;
    if (!id.toLowerCase().endsWith("oif")) {
      Location current = new Location(id);
      current = current.getAbsoluteFile();
      String parent = current.getParent();
      Location tmp = new Location(parent);
      parent = tmp.getParent();

      // strip off the filename

      id = current.getPath();

      oifFile = id.substring(id.lastIndexOf(File.separator));
      oifFile = parent + oifFile.substring(0, oifFile.indexOf("_")) + ".oif";

      tmp = new Location(oifFile);
      if (!tmp.exists()) {
        oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
        tmp = new Location(oifFile);
        if (!tmp.exists()) throw new FormatException("OIF file not found");
        currentId = oifFile;
      }
      else currentId = oifFile;
    }

    super.initFile(oifFile);
    in = new RandomAccessStream(oifFile);

    usedFiles = new Vector();
    usedFiles.add(new Location(oifFile).getAbsolutePath());

    int slash = oifFile.lastIndexOf(File.separator);
    String path = slash < 0 ? "." : oifFile.substring(0, slash);

    // parse each key/value pair (one per line)

    status("Parsing metadata values");

    byte[] b = new byte[(int) in.length()];
    in.read(b);
    String s = new String(b);
    StringTokenizer st = new StringTokenizer(s, "\r\n");

    Hashtable filenames = new Hashtable();
    String prefix = "";
    while (st.hasMoreTokens()) {
      String line = DataTools.stripString(st.nextToken().trim());
      if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
        String key = line.substring(0, line.indexOf("=")).trim();
        String value = line.substring(line.indexOf("=") + 1).trim();
        if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1) {
          int pos = Integer.parseInt(key.substring(11));
          filenames.put(new Integer(pos), value.trim());
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
        else if ((prefix + key).equals("[Axis Parameter Common] - AxisOrder")) {
          core.currentOrder[0] = value;
        }
        else if ((prefix + key).equals(
          "[Reference Image Parameter] - ImageDepth"))
        {
          imageDepth = Integer.parseInt(value);
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
      String value = (String) filenames.get(new Integer(i));
      if (value.length() > reference) {
        filenames.remove(new Integer(i));
      }
    }

    status("Initializing helper readers");

    thumbReader = new BMPReader();
    core.imageCount[0] = filenames.size();
    tiffs = new Vector(core.imageCount[0]);

    tiffReader = new TiffReader[core.imageCount[0]];
    for (int i=0; i<core.imageCount[0]; i++) {
      tiffReader[i] = new TiffReader();
      if (i > 0) tiffReader[i].setMetadataCollected(false);
    }

    // open each INI file (.pty extension)

    status("Reading additional metadata");

    String tiffPath = null;
    RandomAccessStream ptyReader;

    for (int i=0; i<core.imageCount[0]; i++) {
      String file = (String) filenames.get(new Integer(i));
      file = file.substring(1, file.length() - 1);
      file = file.replace('\\', File.separatorChar);
      file = file.replace('/', File.separatorChar);
      file = path + File.separator + file;
      tiffPath = file.substring(0, file.lastIndexOf(File.separator));

      ptyReader = new RandomAccessStream(file);
      b = new byte[(int) ptyReader.length()];
      ptyReader.read(b);
      s = new String(b);
      st = new StringTokenizer(s, "\n");

      while (st.hasMoreTokens()) {
        String line = st.nextToken().trim();
        if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
          String key = line.substring(0, line.indexOf("=") - 1).trim();
          String value = line.substring(line.indexOf("=") + 1).trim();
          key = DataTools.stripString(key);
          value = DataTools.stripString(value);
          if (key.equals("DataName")) {
            value = value.substring(1, value.length() - 1);
            if (value.indexOf("-R") == -1) {
              tiffs.add(i, tiffPath + File.separator + value);
              tiffReader[i].setId((String) tiffs.get(i));
            }
          }
          addMeta("Image " + i + " : " + key, value);
        }
      }
      ptyReader.close();
    }

    if (tiffPath != null) {
      Location dir = new Location(tiffPath);
      String[] list = dir.list();
      for (int i=0; i<list.length; i++) {
        usedFiles.add(new Location(tiffPath, list[i]).getAbsolutePath());
      }
    }

    status("Populating metadata");

    for (int i=0; i<9; i++) {
      int ss = Integer.parseInt(size[i]);
      if (code[i].equals("\"X\"")) core.sizeX[0] = ss;
      else if (code[i].equals("\"Y\"")) core.sizeY[0] = ss;
      else if (code[i].equals("\"C\"")) core.sizeC[0] = ss;
      else if (code[i].equals("\"T\"")) core.sizeT[0] = ss;
      else if (code[i].equals("\"Z\"")) core.sizeZ[0] = ss;
    }

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    while (core.imageCount[0] >
      core.sizeZ[0] * core.sizeT[0] * getEffectiveSizeC())
    {
      if (core.sizeZ[0] == 1) core.sizeT[0]++;
      else if (core.sizeT[0] == 1) core.sizeZ[0]++;
    }

    core.currentOrder[0] =
      core.currentOrder[0].substring(1, core.currentOrder[0].length() - 1);
    if (core.currentOrder[0] == null) core.currentOrder[0] = "XYZTC";
    else {
      String[] names = new String[] {"X", "Y", "Z", "C", "T"};
      if (core.currentOrder[0].length() < 5) {
        for (int i=0; i<names.length; i++) {
          if (core.currentOrder[0].indexOf(names[i]) == -1) {
            core.currentOrder[0] += names[i];
          }
        }
      }
    }

    BufferedImage thumbImage = openThumbImage(0);
    core.thumbSizeX[0] = thumbImage.getWidth();
    core.thumbSizeY[0] = thumbImage.getHeight();

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);

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
        throw new RuntimeException(
          "Unknown matching for pixel depth of: " + imageDepth);
    }

    core.rgb[0] = tiffReader[0].isRGB();
    core.littleEndian[0] = true;
    core.interleaved[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = tiffReader[0].isIndexed();
    core.falseColor[0] = false;

    FormatTools.populatePixels(store, this);

    prefix = "[Reference Image Parameter] - ";
    String px = (String) getMeta(prefix + "WidthConvertValue");
    String py = (String) getMeta(prefix + "HeightConvertValue");
    Float pixX = null, pixY = null;
    if (px != null) pixX = new Float(px);
    if (py != null) pixY = new Float(py);
    store.setDimensions(pixX, pixY, null, null, null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      prefix = "[Channel " + (i+1) + " Parameters] - ";
      String name = (String) getMeta(prefix + "CH Name");
      String emWave = (String) getMeta(prefix + "EmissionWavelength");
      String exWave = (String) getMeta(prefix + "ExcitationWavelength");

      prefix = "[Channel " + (i+1) + " Parameters] - ";
      String gain = (String) getMeta(prefix + "CountingPMTGain");
      String voltage = (String) getMeta(prefix + "CountingPMTVoltage");
      String offset = (String) getMeta(prefix + "CountingPMTOffset");

      if (gain != null) gain.replaceAll("\"", "");
      if (voltage != null) voltage.replaceAll("\"", "");
      if (offset != null) offset.replaceAll("\"", "");

      if (voltage != null) {
        store.setDetector(null, null, null, null, null, new Float(voltage),
          null, null, new Integer(i));
      }

      store.setLogicalChannel(i, name, null, null, null, null, null, null,
        null, offset == null ? null : new Float(offset),
        gain == null ? null : new Float(gain), null, null, null, null, null,
        null, null, null, null, emWave == null ? null : new Integer(emWave),
        exWave == null ? null : new Integer(exWave), null, null, null);
    }

    String mag = (String) getMeta("Image 0 : Magnification");
    if (mag != null) {
      store.setObjective(null, null, null, null, new Float(mag), null, null);
    }

    String num =
      (String) getMeta("[Acquisition Parameters Common] - Number of use Laser");
    if (num != null) {
      int numLasers = Integer.parseInt(num);
      for (int i=0; i<numLasers; i++) {
        String wave = (String) getMeta("[Acquisition Parameters Common] - " +
          "LaserWavelength0" + (i+1));
        if (wave != null) {
          store.setLaser(null, null, new Integer(wave), null, null, null, null,
            null, null, null, new Integer(i));
        }
      }
    }
  }

}
