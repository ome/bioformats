//
// BioRadReader.java
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
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * BioRadReader is the file format reader for Bio-Rad PIC files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/BioRadReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/BioRadReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */

public class BioRadReader extends FormatReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  /** Numerical ID of a valid Bio-Rad PIC file. */
  private static final int PIC_FILE_ID = 12345;

  /** Always little endian. */
  private static final boolean LITTLE_ENDIAN = true;

  /** List of merge types. */
  private static final String[] MERGE_NAMES = {
    "MERGE_OFF", "MERGE_16", "MERGE_ALTERNATE", "MERGE_COLUMN", "MERGE_ROW",
    "MERGE_MAXIMUM", "MERGE_OPT12", "MERGE_OPT12_V2"
  };

  /** List of note types. */
  public static final String[] NOTE_NAMES = {
    "0", "LIVE", "FILE1", "NUMBER", "USER", "LINE", "COLLECT", "FILE2",
    "SCALEBAR", "MERGE", "THRUVIEW", "ARROW", "12", "13", "14", "15",
    "16", "17", "18", "19", "VARIABLE", "STRUCTURE", "4D SERIES"
  };

  /** List of metadata keys. */
  public static final String[][] METADATA_KEYS = {
    {},
    {"Scan Channel", "Both Mode", "Speed", "Filter", "Factor",
      "Number of scans", "Photon counting mode (channel 1)",
      "Photon counting detector (channel 1)",
      "Photon counting mode (channel 2)",
      "Photon counting detector (channel 2)", "Photon mode",
      "Objective lens magnification", "Zoom factor (user selected)",
      "Motor on", "Z step size"},
    {"Z start", "Z stop", "Scan area - cx", "Scan area - cy",
      "Scan area - lx", "Scan area - ly"},
    {"PMT 1 Iris", "PMT 1 Gain", "PMT 1 Black level", "PMT 1 Emission filter",
      "PMT 2 Iris", "PMT 2 Gain", "PMT 2 Black level", "PMT 2 Emission filter",
      "PMT 3 Iris", "PMT 3 Gain", "PMT 3 Black level", "PMT 3 Emission filter",
      "Multiplier of channel 1", "Multiplier of channel 2",
      "Multiplier of channel 3"},
    {"Number of lasers", "Number of transmission detectors", "Number of PMTs",
      "Shutter present for laser 1", "Shutter present for laser 2",
      "Shutter present for laser 3", "Neutral density filter for laser 1",
      "Excitation filter for laser 1", "Use laser 1",
      "Neutral density filter for laser 2", "Excitation filter for laser 2",
      "Use laser 2", "Neutral density filter for laser 3",
      "Excitation filter for laser 3", "Use laser 3",
      "Neutral density filter name - laser 1",
      "Neutral density filter name - laser 2",
      "Neutral density filter name - laser 3"},
    {"Excitation filter name - laser 1", "Excitation filter name - laser 2",
      "Excitation filter name - laser 3"},
    {"Emission filter name - laser 1", "Emission filter name - laser 2",
      "Emission filter name - laser 3"},
    {"Mixer 0 - enhanced", "Mixer 0 - PMT 1 percentage",
      "Mixer 0 - PMT 2 percentage", "Mixer 0 - PMT 3 percentage",
      "Mixer 0 - Transmission 1 percentage",
      "Mixer 0 - Transmission 2 percentage",
      "Mixer 0 - Transmission 3 percentage", "Mixer 1 - enhanced",
      "Mixer 1 - PMT 1 percentage", "Mixer 1 - PMT 2 percentage",
      "Mixer 1 - PMT 3 percentage", "Mixer 1 - Transmission 1 percentage",
      "Mixer 1 - Transmission 2 percentage",
      "Mixer 1 - Transmission 3 percentage", "Mixer 0 - low signal on",
      "Mixer 1 - low signal on"},
    {"Laser 1 name"},
    {"Laser 2 name"},
    {"Laser 3 name"},
    {"Transmission detector 1 - offset", "Transmission detector 1 - gain",
      "Transmission detector 1 - black level",
      "Transmission detector 2 - offset", "Transmission detector 2 - gain",
      "Transmission detector 2 - black level",
      "Transmission detector 3 - offset", "Transmission detector 3 - gain",
      "Transmission detector 3 - black level"},
    {"Part number of laser 1", "Part number of excitation filter for laser 1",
      "Part number of ND filter for laser 1",
      "Part number of emission filter for laser 1", "Part number of laser 2",
      "Part number of excitation filter for laser 2",
      "Part number of ND filter for laser 2",
      "Part number of emission filter for laser 2"},
    {"Part number of laser 3", "Part number of excitation filter for laser 3",
      "Part number of ND filter for laser 3",
      "Part number of emission filter for laser 3",
      "Part number of filter block 1", "Part number of filter block 2",
      "Filter block 1", "Filter block 2"},
    {"Filter block 1 name", "Filter block 2 name"},
    {"Image band 1 status", "Image band 1 min", "Image band 1 max",
      "Image band 2 status", "Image band 2 min", "Image band 2 max",
      "Image band 3 status", "Image band 3 min", "Image band 3 max",
      "Image band 4 status", "Image band 4 min", "Image band 4 max",
      "Image band 5 status", "Image band 5 min", "Image band 5 max"},
    {"Image band 5 status", "Image band 5 min", "Image band 5 max"},
    {"Date stamp (seconds)", "Date stamp (minutes)", "Date stamp (hours)",
      "Date stamp (day of month)", "Date stamp (month)",
      "Date stamp (year: actual year - 1900)", "Date stamp (day of week)",
      "Date stamp (day of year)", "Daylight savings?"},
    {"Mixer 3 - enhanced", "Mixer 3 - PMT 1 percentage",
      "Mixer 3 - PMT 2 percentage", "Mixer 3 - PMT 3 percentage",
      "Mixer 3 - Transmission 1 percentage",
      "Mixer 3 - Transmission 2 percentage",
      "Mixer 3 - Transmission 3 percentage", "Mixer 3 - low signal on",
      "Mixer 3 - photon counting 1", "Mixer 3 - photon counting 2",
      "Mixer 3 - photon counting 3", "Mixer 3 - mode"},
    {"Mixer 1 - photon counting 1", "Mixer 1 - photon counting 2",
      "Mixer 1 - photon counting 3", "Mixer 1 - mode",
      "Mixer2 - photon counting 1", "Mixer 2 - photon counting 2",
      "Mixer 2 - photon counting 3", "Mixer 2 - mode"},
    {"Display mode", "Course", "Time Course - experiment type",
      "Time Course - kd factor"},
    {"Time Course - ion name"},
    {"PIC file generated on Isoscan (lite)", "Photon counting used (PMT 1)",
      "Photon counting used (PMT 2)", "Photon counting used (PMT 3)",
      "Hot spot filter used (PMT 1)", "Hot spot filter used (PMT 2)",
      "Hot spot filter used (PMT 3)", "Tx selector used (TX 1)",
      "Tx selected used (TX 2)", "Tx selector used (TX 3)"}
  };


  // -- Fields --

  /** Flag indicating current Bio-Rad PIC is packed with bytes. */
  private boolean byteFormat;

  private Vector used;

  private String[] picFiles;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() {
    super("Bio-Rad PIC", new String[] {"pic", "xml", "raw"});
    blockCheckLen = 56;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    String lname = name.toLowerCase();
    if (lname.endsWith(".pic")) return true;
    String fname = new File(lname).getName();
    if (fname.equals("lse.xml") || fname.equals("data.raw")) return true;
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < blockCheckLen) return false;
    return DataTools.bytesToShort(block, 54, LITTLE_ENDIAN) == PIC_FILE_ID ||
      new String(block).startsWith("[Input Sources]");
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return (String[]) used.toArray(new String[0]);
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

    int bytes = byteFormat ? 1 : 2;

    if (picFiles != null) {
      int file = no % picFiles.length;
      RandomAccessStream ras = new RandomAccessStream(picFiles[file]);
      long offset = (no / picFiles.length) *
        core.sizeX[0] * core.sizeY[0] * bytes;
      ras.seek(offset + 76);

      ras.skipBytes(y * core.sizeX[0] * bytes);
      for (int row=0; row<h; row++) {
        ras.skipBytes(x * bytes);
        ras.read(buf, row * w * bytes, w * bytes);
        ras.skipBytes(bytes * (core.sizeX[0] - w - x));
      }

      ras.close();
    }
    else {
      in.seek(no * core.sizeX[0] * core.sizeY[0] * bytes + 76);

      in.skipBytes(y * core.sizeX[0] * bytes);
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bytes);
        in.read(buf, row * w * bytes, w * bytes);
        in.skipBytes(bytes * (core.sizeX[0] - w - x));
      }
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    byteFormat = false;
    used = null;
    picFiles = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BioRadReader.initFile(" + id + ")");

    if (!id.toLowerCase().endsWith(".pic")) {
      Location dir = new Location(id).getAbsoluteFile().getParentFile();

      String[] list = dir.list();
      for (int i=0; i<list.length; i++) {
        if (list[i].toLowerCase().endsWith(".pic")) {
          id = new Location(dir.getAbsolutePath(), list[i]).getAbsolutePath();
        }
      }
      if (!id.toLowerCase().endsWith(".pic")) {
        throw new FormatException("No .pic files found - invalid dataset.");
      }
    }

    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    used = new Vector();
    used.add(currentId);

    status("Reading image dimensions");

    // read header

    core.sizeX[0] = in.readShort();
    core.sizeY[0] = in.readShort();
    int npic = in.readShort();
    core.imageCount[0] = npic;

    int ramp1min = in.readShort();
    int ramp1max = in.readShort();
    boolean notes = in.readInt() != 0;
    byteFormat = in.readShort() != 0;
    int imageNumber = in.readShort();
    String name = in.readString(32);
    int merged = in.readShort();
    int color1 = in.readShort();
    int fileId = in.readShort();
    int ramp2min = in.readShort();
    int ramp2max = in.readShort();
    int color2 = in.readShort();
    int edited = in.readShort();
    int lens = in.readShort();
    float magFactor = in.readFloat();

    // check validity of header
    if (fileId != PIC_FILE_ID) {
      throw new FormatException("Invalid file header : " + fileId);
    }

    // populate metadata fields
    addMeta("nx", new Integer(core.sizeX[0]));
    addMeta("ny", new Integer(core.sizeY[0]));
    addMeta("npic", new Integer(core.imageCount[0]));
    addMeta("ramp1_min", new Integer(ramp1min));
    addMeta("ramp1_max", new Integer(ramp1max));
    addMeta("notes", new Boolean(notes));
    addMeta("byte_format", new Boolean(byteFormat));
    addMeta("image_number", new Integer(imageNumber));
    addMeta("name", name);
    addMeta("merged", MERGE_NAMES[merged]);
    addMeta("color1", new Integer(color1));
    addMeta("file_id", new Integer(fileId));
    addMeta("ramp2_min", new Integer(ramp2min));
    addMeta("ramp2_max", new Integer(ramp2max));
    addMeta("color2", new Integer(color2));
    addMeta("edited", new Integer(edited));
    addMeta("lens", new Integer(lens));
    addMeta("mag_factor", new Float(magFactor));

    // skip image data
    int imageLen = core.sizeX[0] * core.sizeY[0];
    int bpp = byteFormat ? 1 : 2;
    in.skipBytes(bpp * core.imageCount[0] * imageLen + 6);

    Vector pixelSize = new Vector();

    core.sizeZ[0] = core.imageCount[0];
    core.sizeC[0] = 1;
    core.sizeT[0] = 1;

    core.orderCertain[0] = false;
    core.rgb[0] = false;
    core.interleaved[0] = false;
    core.littleEndian[0] = LITTLE_ENDIAN;
    core.metadataComplete[0] = true;

    status("Reading notes");

    String zoom = null, zstart = null, zstop = null, mag = null;
    String gain1 = null, gain2 = null, gain3 = null;
    String offset1 = null;
    String ex1 = null, ex2 = null, ex3 = null;
    String em1 = null, em2 = null, em3 = null;

    // read notes
    int noteCount = 0;
    while (notes) {
      // read in note

      int level = in.readShort();
      notes = in.readInt() != 0;
      int num = in.readShort();
      int status = in.readShort();
      int type = in.readShort();
      int x = in.readShort();
      int y = in.readShort();
      String text = in.readString(80);

      // be sure to remove binary data from the note text
      int ndx = text.length();
      for (int i=0; i<text.length(); i++) {
        if (text.charAt(i) == 0) {
          ndx = i;
          break;
        }
      }

      text = text.substring(0, ndx).trim();

      // add note to list
      noteCount++;

      switch (type) {
        case 8: // NOTE_TYPE_SCALEBAR
          int eq = text.indexOf("=");
          if (eq != -1) {
            text = text.substring(eq + 1).trim();
            String len = text.substring(0, text.indexOf(" ")).trim();
            String angle = text.substring(text.indexOf(" ")).trim();
            addMeta("Scalebar length (in microns)", len);
            addMeta("Scalebar angle (in degrees)", angle);
          }
          break;
        case 11: // NOTE_TYPE_ARROW
          eq = text.indexOf("=");
          if (eq != -1) {
            text = text.substring(eq + 1).trim();
            StringTokenizer st = new StringTokenizer(text, " ");
            addMeta("Arrow width", st.nextToken());
            addMeta("Arrow height", st.nextToken());
            addMeta("Arrow angle", st.nextToken());
            addMeta("Arrow fill type", st.nextToken());
          }
          break;
        case 20: // NOTE_TYPE_VARIABLE
          eq = text.indexOf("=");
          if (eq != -1) {
            String key = text.substring(0, eq);
            String value = text.substring(eq + 1);
            addMeta(key, value);
          }
          break;
        case 21: // NOTE_TYPE_STRUCTURE
          StringTokenizer st = new StringTokenizer(text, " ");

          String[] keys =
            y < METADATA_KEYS.length ? METADATA_KEYS[y] : new String[0];
          int idx = 0;

          String value;
          while (st.hasMoreTokens() && idx < keys.length) {
            value = st.nextToken();
            addMeta(keys[idx], value);
            if (keys[idx].equals("Zoom factor (user selected)")) zoom = value;
            else if (keys[idx].equals("Z start")) zstart = value;
            else if (keys[idx].equals("Z stop")) zstop = value;
            else if (keys[idx].equals("Transmission detector 1 - gain")) {
              gain1 = value;
            }
            else if (keys[idx].equals("Transmission detector 2 - gain")) {
              gain2 = value;
            }
            else if (keys[idx].equals("Transmission detector 3 - gain")) {
              gain3 = value;
            }
            else if (keys[idx].equals("Transmission detector 1 - offset")) {
              offset1 = value;
            }
            else if (keys[idx].equals(
              "Part number of excitation filter for laser 1"))
            {
              ex1 = value;
            }
            else if (keys[idx].equals(
              "Part number of excitation filter for laser 2"))
            {
              ex2 = value;
            }
            else if (keys[idx].equals(
              "Part number of excitation filter for laser 3"))
            {
              ex3 = value;
            }
            else if (keys[idx].equals(
              "Part number of emission filter for laser 1"))
            {
              em1 = value;
            }
            else if (keys[idx].equals(
              "Part number of emission filter for laser 2"))
            {
              em2 = value;
            }
            else if (keys[idx].equals(
              "Part number of emission filter for laser 3"))
            {
              em3 = value;
            }
            else if (keys[idx].equals("Objective lens magnification")) {
              mag = value;
            }

            idx++;
          }
          break;
        default:
          addMeta("note" + noteCount,
            noteString(num, level, status, type, x, y, text));
      }

      // if the text of the note contains "AXIS", parse the text
      // more thoroughly (see pg. 21 of the BioRad specs)

      if (text.indexOf("AXIS") != -1) {
        // use StringTokenizer to break up the text
        StringTokenizer st = new StringTokenizer(text);
        String key = st.nextToken();
        String noteType = "";
        if (st.hasMoreTokens()) {
          noteType = st.nextToken();
        }

        int axisType = Integer.parseInt(noteType);
        Vector params = new Vector();
        while (st.hasMoreTokens()) {
          params.add(st.nextToken());
        }

        if (params.size() > 1) {
          switch (axisType) {
            case 1:
              String dx = (String) params.get(0), dy = (String) params.get(1);
              addMeta(key + " distance (X) in microns", dx);
              addMeta(key + " distance (Y) in microns", dy);
              pixelSize.add(dy);
              break;
            case 2:
              if (text.indexOf("AXIS_4") != -1) {
                addMeta(key + " time (X) in seconds", params.get(0));
                addMeta(key + " time (Y) in seconds", params.get(1));
                core.sizeZ[0] = 1;
                core.sizeT[0] = core.imageCount[0];
                core.orderCertain[0] = true;
              }
              break;
            case 3:
              addMeta(key + " angle (X) in degrees", params.get(0));
              addMeta(key + " angle (Y) in degrees", params.get(1));
              break;
            case 4:
              addMeta(key + " intensity (X)", params.get(0));
              addMeta(key + " intensity (Y)", params.get(1));
              break;
            case 6:
              addMeta(key + " ratio (X)", params.get(0));
              addMeta(key + " ratio (Y)", params.get(1));
              break;
            case 7:
              addMeta(key + " log ratio (X)", params.get(0));
              addMeta(key + " log ratio (Y)", params.get(1));
              break;
            case 9:
              addMeta(key + " noncalibrated intensity min", params.get(0));
              addMeta(key + " noncalibrated intensity max", params.get(1));
              addMeta(key + " calibrated intensity min", params.get(2));
              addMeta(key + " calibrated intensity max", params.get(3));
              break;
            case 11:
              addMeta(key + " RGB type (X)", params.get(0));
              addMeta(key + " RGB type (Y)", params.get(1));
              break;
            case 14:
              addMeta(key + " time course type (X)", params.get(0));
              addMeta(key + " time course type (Y)", params.get(1));
              break;
            case 15:
              addMeta(key + " inverse sigmoid calibrated intensity (min)",
                params.get(0));
              addMeta(key + " inverse sigmoid calibrated intensity (max)",
                params.get(1));
              addMeta(key +
                " inverse sigmoid calibrated intensity (beta)", params.get(2));
              addMeta(key + " inverse sigmoid calibrated intensity (Kd)",
                params.get(3));
              addMeta(key + " inverse sigmoid calibrated intensity " +
                "(calibrated max)", params.get(0));
              break;
            case 16:
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (min)", params.get(0));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (max)", params.get(1));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (beta)", params.get(2));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (Kd)", params.get(3));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (calibrated max)", params.get(0));
              break;
          }

        }
      }
    }

    status("Reading color table");

    // read color tables
    int numLuts = 0;
    byte[][] lut = new byte[3][768];
    boolean eof = false;
    while (!eof && numLuts < 3) {
      if (in.getFilePointer() + lut[numLuts].length <= in.length()) {
        in.read(lut[numLuts]);
        numLuts++;
      }
      else eof = true;
    }

    if (debug && debugLevel >= 2) {
      debug(numLuts + " color table" + (numLuts == 1 ? "" : "s") + " present.");
    }

    // convert color table bytes to floats
    float[][][] colors = new float[numLuts][3][256];
    for (int i=0; i<numLuts; i++) {
      for (int l=0; l<256; l++) {
        colors[i][0][l] = (float) (lut[i][l] & 0xff);
        colors[i][1][l] = (float) (lut[i][l + 256] & 0xff);
        colors[i][2][l] = (float) (lut[i][l + 512] & 0xff);
      }
    }

    StringBuffer colorString = new StringBuffer();
    for (int i=0; i<numLuts; i++) {
      for (int j=0; j<256; j++) {
        for (int k=0; k<3; k++) {
          colorString = colorString.append(colors[i][k][j]);
          if (!(j == 255 && k == 2)) {
            colorString = colorString.append(",");
          }
        }
      }
      colorString = colorString.append("\n\n");
    }

    addMeta("luts", colorString.toString());

    status("Populating metadata");

    // look for companion metadata files

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] list = parent.list();
    Arrays.sort(list);

    Vector pics = new Vector();

    for (int i=0; i<list.length; i++) {
      if (list[i].endsWith("lse.xml")) {
        String path =
          new Location(parent.getAbsolutePath(), list[i]).getAbsolutePath();
        RandomAccessStream raw = new RandomAccessStream(path);
        used.add(path);
        byte[] xml = new byte[(int) raw.length()];
        raw.read(xml);
        raw.close();

        BioRadHandler handler = new BioRadHandler();
        try {
          SAXParser parser = SAX_FACTORY.newSAXParser();
          parser.parse(new ByteArrayInputStream(xml), handler);
        }
        catch (SAXException e) {
          if (debug) LogTools.trace(e);
        }
        catch (ParserConfigurationException e) {
          if (debug) LogTools.trace(e);
        }

        for (int q=0; q<list.length; q++) {
          if (list[q].toLowerCase().endsWith(".pic")) {
            path =
              new Location(parent.getAbsolutePath(), list[q]).getAbsolutePath();
            pics.add(path);
            if (!used.contains(path)) used.add(path);
          }
        }
      }
    }

    picFiles = (String[]) pics.toArray(new String[0]);

    core.indexed[0] = false;
    core.falseColor[0] = false;

    // Populate the metadata store

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);

    // populate Pixels
    in.seek(14);
    core.pixelType[0] = in.readShort() == 1 ? FormatTools.UINT8 :
      FormatTools.UINT16;

    core.currentOrder[0] = "XYCTZ";

    if (picFiles.length > 0) {
      core.imageCount[0] = npic * picFiles.length;
      core.sizeC[0] = core.imageCount[0] / (core.sizeZ[0] * core.sizeT[0]);
    }
    else picFiles = null;

    MetadataTools.populatePixels(store, this);

    // populate Dimensions
    int size = pixelSize.size();
    Float pixelSizeX = null, pixelSizeY = null, pixelSizeZ = null;
    if (size >= 1) pixelSizeX = new Float((String) pixelSize.get(0));
    if (size >= 2) pixelSizeY = new Float((String) pixelSize.get(1));
    if (size >= 3) pixelSizeZ = new Float((String) pixelSize.get(2));
    store.setDimensionsPhysicalSizeX(pixelSizeX, 0, 0);
    store.setDimensionsPhysicalSizeY(pixelSizeY, 0, 0);
    store.setDimensionsPhysicalSizeZ(pixelSizeZ, 0, 0);

    /*
    for (int i=0; i<core.sizeC[0]; i++) {
      String gain = i == 0 ? gain1 : i == 1 ? gain2 : gain3;
      String offset = i == 0 ? offset1 : i == 1 ? gain2 : gain3;
      Integer ii = new Integer(i);
      if (offset != null) {
        store.setDetectorSettingsOffset(new Float(offset), 0, 0);
      }
      if (gain != null) store.setDetectorSettingsGain(new Float(gain), 0, 0);
      // CTR CHECK
//      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
//       offset == null ? null : new Float(offset),
//       gain == null ? null : new Float(gain), null, null, null, null, null,
//       null, null, null, null, null, null, null, null, null);
      // CTR FIXME
//      store.setDisplayChannel(ii, new Double(ramp1max),
//        new Double(ramp1min), null, null);
    }
    // CTR FIXME
//    store.setDisplayOptions(zoom == null ? null : new Float(zoom),
//      new Boolean(core.sizeC[0] > 1), new Boolean(core.sizeC[0] >= 2),
//      new Boolean(core.sizeC[0] >= 3), Boolean.FALSE, null,
//      zstart == null ? null :
//      new Integer((int) (new Double(zstart).doubleValue())), zstop == null ?
//      null : new Integer((int) (new Double(zstop).doubleValue())), null, null,
//      null, null, core.sizeC[0] > 1 ? new Integer(0) : null,
//      core.sizeC[0] > 1 ? new Integer(1) : null,
//      core.sizeC[0] > 1 ? new Integer(2) : null, new Integer(0));

    for (int i=0; i<3; i++) {
      String exc = i == 0 ? ex1 : i == 1 ? ex2 : ex3;
      String ems = i == 0 ? em1 : i == 1 ? em2 : em3;
      // CTR FIXME
//      if (exc != null) store.setExcitationFilter(null, null, exc, null, null);
//      if (ems != null) store.setEmissionFilter(null, null, ems, null, null);
    }
    if (mag != null) {
      // CTR CHECK
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }
    */
  }

  // -- Helper methods --

  private String noteString(int n, int l,
    int s, int t, int x, int y, String p)
  {
    StringBuffer sb = new StringBuffer(100);
    sb.append("level=");
    sb.append(l);
    sb.append("; num=");
    sb.append(n);
    sb.append("; status=");
    sb.append(s);
    sb.append("; type=");
    sb.append(NOTE_NAMES[t]);
    sb.append("; x=");
    sb.append(x);
    sb.append("; y=");
    sb.append(y);
    sb.append("; text=");
    sb.append(p == null ? "null" : p.trim());
    return sb.toString();
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class BioRadHandler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("Pixels")) {
        String sizeZ = attributes.getValue("SizeZ");
        String sizeC = attributes.getValue("SizeC");
        String sizeT = attributes.getValue("SizeT");
        int z = sizeZ == null ? 1 : Integer.parseInt(sizeZ);
        int c = sizeC == null ? 1 : Integer.parseInt(sizeC);
        int t = sizeT == null ? 1 : Integer.parseInt(sizeT);
        int count = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
        if (z >= core.sizeZ[0] && c >= core.sizeC[0] && t >= core.sizeT[0] &&
          count >= core.imageCount[0])
        {
          core.sizeZ[0] = z;
          core.sizeC[0] = c;
          core.sizeT[0] = t;
          core.imageCount[0] = count;
        }
      }
      else if (qName.equals("Z") || qName.equals("C") || qName.equals("T")) {
        String stamp = attributes.getValue("TimeCompleted");
        int count = 0;
        while (metadata.containsKey("Timestamp " + count)) count++;
        addMeta("Timestamp " + count, stamp);
      }
    }
  }

}
