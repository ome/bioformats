//
// PrairieReader.java
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
import java.text.*;
import java.util.*;
import loci.formats.*;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PrairieReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PrairieReader.java">SVN</a></dd></dl>
 */
public class PrairieReader extends FormatReader {

  // -- Constants --

  // Private tags present in Prairie TIFF files
  // IMPORTANT NOTE: these are the same as Metamorph's private tags - therefore,
  //                 it is likely that Prairie TIFF files will be incorrectly
  //                 identified unless the XML or CFG file is specified
  private static final int PRAIRIE_TAG_1 = 33628;
  private static final int PRAIRIE_TAG_2 = 33629;
  private static final int PRAIRIE_TAG_3 = 33630;

  // -- Fields --

  /** List of files in the current dataset */
  private String[] files;

  /** Helper reader for opening images */
  private TiffReader tiff;

  /** Names of the associated XML files */
  private String xmlFile, cfgFile;
  private boolean readXML = false, readCFG = false;

  // -- Constructor --

  /** Constructs a new Prairie TIFF reader. */
  public PrairieReader() {
    super("Prairie (TIFF)", new String[] {"tif", "tiff", "cfg", "xml"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])
    if (block.length < 3) return false;
    if (block.length < 8) {
      return true; // we have no way of verifying further
    }

    String s = new String(block);
    if (s.indexOf("xml") != -1 && s.indexOf("PV") != -1) return true;

    boolean little = (block[0] == 0x49 && block[1] == 0x49);

    int ifdlocation = DataTools.bytesToInt(block, 4, little);

    if (ifdlocation < 0) return false;
    else if (ifdlocation + 1 > block.length) return true;
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, little);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) {
          return false;
        }
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, little);
          if (ifdtag == PRAIRIE_TAG_1 || ifdtag == PRAIRIE_TAG_2 ||
            ifdtag == PRAIRIE_TAG_3)
          {
            return true;
          }
        }
      }
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    id = id.toLowerCase();
    return (id.endsWith(".cfg") || id.endsWith(".xml")) ?
      FormatTools.MUST_GROUP : FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    String[] s = new String[files.length + 2];
    System.arraycopy(files, 0, s, 0, files.length);
    s[files.length] = xmlFile;
    s[files.length + 1] = cfgFile;
    return s;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    tiff.setId(files[no]);
    return tiff.openBytes(0, buf);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly && tiff != null) tiff.close(fileOnly);
    else if (!fileOnly) close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension
    if (!isGroupFiles()) return false;

    // check if there is an XML file in the same directory
    Location  f = new Location(name);
    f = f.getAbsoluteFile();
    Location parent = f.getParentFile();
    String[] listing = parent.list();
    int xmlCount = 0;
    for (int i=0; i<listing.length; i++) {
      if (listing[i].toLowerCase().endsWith(".xml")) {
        try {
          RandomAccessStream s = new RandomAccessStream(
            parent.getAbsolutePath() + File.separator + listing[i]);
          if (s.readString(512).indexOf("PV") != -1) xmlCount++;
        }
        catch (IOException e) { }
      }
    }
    if (xmlCount == 0) {
      listing = (String[]) Location.getIdMap().keySet().toArray(new String[0]);
      for (int i=0; i<listing.length; i++) {
        if (listing[i].toLowerCase().endsWith(".xml")) {
          try {
            RandomAccessStream s = new RandomAccessStream(listing[i]);
            if (s.readString(512).indexOf("PV") != -1) xmlCount++;
          }
          catch (IOException e) { }
        }
      }
    }

    boolean xml = xmlCount > 0;

    // just checking the filename isn't enough to differentiate between
    // Prairie and regular TIFF; open the file and check more thoroughly
    return open ? checkBytes(name, 524304) && xml : xml;
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    files = null;
    if (tiff != null) tiff.close();
    tiff = null;
    currentId = null;
    readXML = false;
    readCFG = false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.IFormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PrairieReader.initFile(" + id + ")");

    if (metadata == null) metadata = new Hashtable();
    if (core == null) core = new CoreMetadata(1);

    if (id.endsWith("xml") || id.endsWith("cfg")) {
      // we have been given the XML file that lists TIFF files (best case)

      status("Parsing XML");

      if (id.endsWith("xml")) {
        super.initFile(id);
        tiff = new TiffReader();
        xmlFile = id;
        readXML = true;
      }
      else if (id.endsWith("cfg")) {
        cfgFile = id;
        readCFG = true;
      }

      RandomAccessStream is = new RandomAccessStream(id);
      byte[] b = new byte[(int) is.length()];
      is.read(b);
      is.close();
      String s = new String(b);

      Vector elements = new Vector();

      while (s.length() > 0) {
        int ndx = s.indexOf("<");
        int val1 = s.indexOf(">", ndx);
        if (val1 != -1 && val1 > ndx) {
          String sub = s.substring(ndx + 1, val1);
          s = s.substring(val1 + 1);
          elements.add(sub);
        }
      }

      int zt = 0;
      boolean isZ = false;
      Vector f = new Vector();
      int fileIndex = 1;
      if (id.endsWith(".xml")) core.imageCount[0] = 0;

      String pastPrefix = "";
      for (int i=1; i<elements.size(); i++) {
        String el = (String) elements.get(i);
        if (el.indexOf(" ") != -1) {
          boolean closed = el.endsWith("/");

          String prefix = el.substring(0, el.indexOf(" "));
          if (prefix.equals("File")) core.imageCount[0]++;
          if (prefix.equals("Frame")) {
            zt++;
            fileIndex = 1;
          }

          if (!prefix.equals("Key") && !prefix.equals("Frame")) {
            el = el.substring(el.indexOf(" ") + 1);
            while (el.indexOf("=") != -1) {
              int eq = el.indexOf("=");
              String key = el.substring(0, eq);
              String value = el.substring(eq + 2, el.indexOf("\"", eq + 2));
              if (prefix.equals("File")) {
                addMeta(pastPrefix + " " + prefix + " " + fileIndex +
                  " " + key, value);
                if (key.equals("filename")) fileIndex++;
              }
              else {
                addMeta(pastPrefix + " " + prefix + " " + key, value);
                if (pastPrefix.equals("PVScan") &&
                  prefix.equals("Sequence") && key.equals("type"))
                {
                  isZ = value.equals("ZSeries");
                }
              }
              el = el.substring(el.indexOf("\"", eq + 2) + 1).trim();
              if (prefix.equals("File") && key.equals("filename")) {
                File current = new File(id).getAbsoluteFile();
                String dir = "";
                if (current.exists()) {
                  dir = current.getPath();
                  dir = dir.substring(0, dir.lastIndexOf(File.separator) + 1);
                }
                f.add(dir + value);
              }
            }
          }
          else if (prefix.equals("Key")) {
            int keyIndex = el.indexOf("key") + 5;
            int valueIndex = el.indexOf("value") + 7;
            String key = el.substring(keyIndex, el.indexOf("\"", keyIndex));
            String value =
              el.substring(valueIndex, el.indexOf("\"", valueIndex));
            addMeta(key, value);

            if (key.equals("pixelsPerLine")) {
              core.sizeX[0] = Integer.parseInt(value);
            }
            else if (key.equals("linesPerFrame")) {
              core.sizeY[0] = Integer.parseInt(value);
            }
          }
          if (!closed) {
            pastPrefix = prefix;
            if (prefix.equals("Frame")) {
              int index = el.indexOf("index") + 7;
              String idx = el.substring(index, el.indexOf("\"", index));
              pastPrefix += " " + idx;
            }
          }
        }
      }

      if (id.endsWith("xml")) {
        files = new String[f.size()];
        f.copyInto(files);
        tiff.setId(files[0]);

        status("Populating metadata");

        if (zt == 0) zt = 1;

        core.sizeZ[0] = isZ ? zt : 1;
        core.sizeT[0] = isZ ? 1 : zt;
        core.sizeC[0] = core.imageCount[0] / (core.sizeZ[0] * core.sizeT[0]);
        core.currentOrder[0] = "XYC" + (isZ ? "ZT" : "TZ");
        core.pixelType[0] = FormatTools.UINT16;
        core.rgb[0] = false;
        core.interleaved[0] = false;
        core.littleEndian[0] = tiff.isLittleEndian();
        core.indexed[0] = tiff.isIndexed();
        core.falseColor[0] = false;

        String px = (String) getMeta("micronsPerPixel_XAxis");
        String py = (String) getMeta("micronsPerPixel_YAxis");
        float pixSizeX = px == null ? 0f : Float.parseFloat(px);
        float pixSizeY = py == null ? 0f : Float.parseFloat(py);

        MetadataStore store = getMetadataStore();

        FormatTools.populatePixels(store, this);
        store.setDimensions(new Float(pixSizeX), new Float(pixSizeY), null,
          null, null, null);
        for (int i=0; i<core.sizeC[0]; i++) {
          String gain = (String) getMeta("pmtGain_" + i);
          String offset = (String) getMeta("pmtOffset_" + i);

          store.setLogicalChannel(i, null, null,
            null, null, null, null, null,
            null, offset == null ? null : new Float(offset),
            gain == null ? null : new Float(gain), null, null, null, null,
            null, null, null, null, null, null, null, null, null, null);
        }

        String date = (String) getMeta(" PVScan date");

        if (date != null) {
          SimpleDateFormat parse = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
          Date d = parse.parse(date, new ParsePosition(0));
          SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          date = fmt.format(d);
        }
        store.setImage(currentId, date, null, null);

        String laserPower = (String) getMeta("laserPower_0");

        store.setLaser(null, null, null, null, null, null,
          laserPower == null ? null : new Float(laserPower),
          null, null, null, null);

        /*
        String zoom = (String) getMeta("opticalZoom");
        if (zoom != null) {
          store.setDisplayOptions(new Float(zoom),
            new Boolean(core.sizeC[0] > 1), new Boolean(core.sizeC[0] > 1),
            new Boolean(core.sizeC[0] > 2), Boolean.FALSE,
            null, null, null, null, null, null, null, null, null, null, null);
        }
        */
      }

      if (!readXML || !readCFG) {
        File file = new File(id).getAbsoluteFile();
        File parent = file.getParentFile();
        String[] listing = file.exists() ? parent.list() :
          (String[]) Location.getIdMap().keySet().toArray(new String[0]);
        for (int i=0; i<listing.length; i++) {
          String path = listing[i].toLowerCase();
          if ((!readXML && path.endsWith(".xml")) ||
            (readXML && path.endsWith(".cfg")))
          {
            String dir = "";
            if (file.exists()) {
              dir = parent.getPath();
              if (!dir.endsWith(File.separator)) dir += File.separator;
            }
            initFile(dir + listing[i]);
          }
        }
      }
    }
    else {
      // we have been given a TIFF file - reinitialize with the proper XML file

      status("Finding XML file");

      Location f = new Location(id);
      f = f.getAbsoluteFile();
      Location parent = f.getParentFile();
      String[] listing = parent.list();
      for (int i=0; i<listing.length; i++) {
        String path = listing[i].toLowerCase();
        if (path.endsWith(".xml") || path.endsWith(".cfg")) {
          initFile(new Location(path).getAbsolutePath());
        }
      }
    }
    if (currentId == null) currentId = id;
  }

}
