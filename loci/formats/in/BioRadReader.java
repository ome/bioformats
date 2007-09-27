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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import loci.formats.*;

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

  /** Numerical ID of a valid Bio-Rad PIC file. */
  private static final int PIC_FILE_ID = 12345;

  /** Always little endian. */
  private static final boolean LITTLE_ENDIAN = true;

  /** List of merge types. */
  private static final String[] MERGE_NAMES = {
    "MERGE_OFF", "MERGE_16", "MERGE_ALTERNATE", "MERGE_COLUMN", "MERGE_ROW",
    "MERGE_MAXIMUM", "MERGE_OPT12", "MERGE_OPT12_V2"
  };

  // Note types

  /** List of note types. */
  public static final String[] NOTE_NAMES = {
    "0", "LIVE", "FILE1", "NUMBER", "USER", "LINE", "COLLECT", "FILE2",
    "SCALEBAR", "MERGE", "THRUVIEW", "ARROW", "12", "13", "14", "15",
    "16", "17", "18", "19", "VARIABLE", "STRUCTURE", "4D SERIES"
  };

  // -- Fields --

  /** Flag indicating current Bio-Rad PIC is packed with bytes. */
  private boolean byteFormat;

  private Vector used;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() { super("Bio-Rad PIC", "pic"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 56) return false;
    return DataTools.bytesToShort(block, 54, 2, LITTLE_ENDIAN) == PIC_FILE_ID;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return (String[]) used.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    long offset = no * core.sizeX[0] * core.sizeY[0] * (byteFormat ? 1 : 2);
    in.seek(offset + 76);
    in.read(buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BioRadReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    used = new Vector();
    used.add(currentId);

    status("Reading image dimensions");

    // read header

    core.sizeX[0] = in.readShort();
    core.sizeY[0] = in.readShort();
    core.imageCount[0] = in.readShort();

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
          i = text.length();
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

          String[] keys = new String[0];
          int idx = 0;

          switch (y) {
            case 1:
              keys = new String[] {"Scan Channel", "Both Mode", "Speed",
                "Filter", "Factor", "Number of scans",
                "Photon counting mode (channel 1)",
                "Photon counting detector (channel 1)",
                "Photon counting mode (channel 2)",
                "Photon counting detector (channel 2)", "Photon mode",
                "Objective lens magnification", "Zoom factor (user selected)",
                "Motor on", "Z step size"};
              break;
            case 2:
              keys = new String[] {"Z start", "Z stop", "Scan area - cx",
                "Scan area - cy", "Scan area - lx", "Scan area - ly"};
              break;
            case 3:
              keys = new String[] {"PMT 1 Iris", "PMT 1 Gain",
                "PMT 1 Black level", "PMT 1 Emission filter", "PMT 2 Iris",
                "PMT 2 Gain", "PMT 2 Black level", "PMT 2 Emission filter",
                "PMT 3 Iris", "PMT 3 Gain", "PMT 3 Black level",
                "PMT 3 Emission filter", "Multiplier of channel 1",
                "Multiplier of channel 2", "Multiplier of channel 3"};
              break;
            case 4:
              keys = new String[] {"Number of lasers",
                "Number of transmission detectors", "Number of PMTs",
                "Shutter present for laser 1",
                "Shutter present for laser 2", "Shutter present for laser 3",
                "Neutral density filter for laser 1",
                "Excitation filter for laser 1", "Use laser 1",
                "Neutral density filter for laser 2",
                "Excitation filter for laser 2", "Use laser 2",
                "Neutral density filter for laser 3",
                "Excitation filter for laser 3", "Use laser 3",
                "Neutral density filter name - laser 1",
                "Neutral density filter name - laser 2",
                "Neutral density filter name - laser 3"};
              break;
            case 5:
              keys = new String[] {"Excitation filter name - laser 1",
                "Excitation filter name - laser 2",
                "Excitation filter name - laser 3"};
              break;
            case 6:
              keys = new String[] {"Emission filter name - laser 1",
                "Emission filter name - laser 2",
                "Emission filter name - laser 3"};
              break;
            case 7:
              keys = new String[] {"Mixer 0 - enhanced",
                "Mixer 0 - PMT 1 percentage",
                "Mixer 0 - PMT 2 percentage", "Mixer 0 - PMT 3 percentage",
                "Mixer 0 - Transmission 1 percentage",
                "Mixer 0 - Transmission 2 percentage",
                "Mixer 0 - Transmission 3 percentage", "Mixer 1 - enhanced",
                "Mixer 1 - PMT 1 percentage", "Mixer 1 - PMT 2 percentage",
                "Mixer 1 - PMT 3 percentage",
                "Mixer 1 - Transmission 1 percentage",
                "Mixer 1 - Transmission 2 percentage",
                "Mixer 1 - Transmission 3 percentage",
                "Mixer 0 - low signal on", "Mixer 1 - low signal on"};
              break;
            case 8:
              keys = new String[] {"Laser 1 name"};
              break;
            case 9:
              keys = new String[] {"Laser 2 name"};
              break;
            case 10:
              keys = new String[] {"Laser 3 name"};
              break;
            case 11:
              keys = new String[] {"Transmission detector 1 - offset",
                "Transmission detector 1 - gain",
                "Transmission detector 1 - black level",
                "Transmission detector 2 - offset",
                "Transmission detector 2 - gain",
                "Transmission detector 2 - black level",
                "Transmission detector 3 - offset",
                "Transmission detector 3 - gain",
                "Transmission detector 3 - black level"};
              break;
            case 12:
              keys = new String[] {"Part number of laser 1",
                "Part number of excitation filter for laser 1",
                "Part number of ND filter for laser 1",
                "Part number of emission filter for laser 1",
                "Part number of laser 2",
                "Part number of excitation filter for laser 2",
                "Part number of ND filter for laser 2",
                "Part number of emission filter for laser 2"};
              break;
            case 13:
              keys = new String[] {"Part number of laser 3",
                "Part number of excitation filter for laser 3",
                "Part number of ND filter for laser 3",
                "Part number of emission filter for laser 3",
                "Part number of filter block 1",
                "Part number of filter block 2",
                "Filter block 1", "Filter block 2"};
              break;
            case 14:
              keys = new String[] {"Filter block 1 name",
                "Filter block 2 name"};
              break;
            case 15:
              keys = new String[] {"Image band 1 status", "Image band 1 min",
                "Image band 1 max", "Image band 2 status", "Image band 2 min",
                "Image band 2 max", "Image band 3 status", "Image band 3 min",
                "Image band 3 max", "Image band 4 status", "Image band 4 min",
                "Image band 4 max", "Image band 5 status", "Image band 5 min",
                "Image band 5 max"};
              break;
            case 16:
              keys = new String[] {"Image band 5 status", "Image band 5 min",
                "Image band 5 max"};
              break;
            case 17:
              keys = new String[] {"Date stamp (seconds)",
                "Date stamp (minutes)",
                "Date stamp (hours)", "Date stamp (day of month)",
                "Date stamp (month)", "Date stamp (year: actual year - 1900)",
                "Date stamp (day of week)", "Date stamp (day of year)",
                "Daylight savings?"};
              break;
            case 18:
              keys = new String[] {"Mixer 3 - enhanced",
                "Mixer 3 - PMT 1 percentage",
                "Mixer 3 - PMT 2 percentage", "Mixer 3 - PMT 3 percentage",
                "Mixer 3 - Transmission 1 percentage",
                "Mixer 3 - Transmission 2 percentage",
                "Mixer 3 - Transmission 3 percentage",
                "Mixer 3 - low signal on",
                "Mixer 3 - photon counting 1", "Mixer 3 - photon counting 2",
                "Mixer 3 - photon counting 3", "Mixer 3 - mode"};
              break;
            case 19:
              keys = new String[] {"Mixer 1 - photon counting 1",
                "Mixer 1 - photon counting 2", "Mixer 1 - photon counting 3",
                "Mixer 1 - mode", "Mixer2 - photon counting 1",
                "Mixer 2 - photon counting 2", "Mixer 2 - photon counting 3",
                "Mixer 2 - mode"};
              break;
            case 20:
              keys = new String[] {"Display mode", "Course",
                "Time Course - experiment type",
                "Time Course - kd factor"};
              break;
            case 21:
              keys = new String[] {"Time Course - ion name"};
              break;
            case 22:
              keys = new String[] {"PIC file generated on Isoscan (lite)",
                "Photon counting used (PMT 1)", "Photon counting used (PMT 2)",
                "Photon counting used (PMT 3)", "Hot spot filter used (PMT 1)",
                "Hot spot filter used (PMT 2)", "Hot spot filter used (PMT 3)",
                "Tx selector used (TX 1)", "Tx selected used (TX 2)",
                "Tx selector used (TX 3)"};
              break;
          }

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

    String colorString = "";
    for (int i=0; i<numLuts; i++) {
      for (int j=0; j<256; j++) {
        for (int k=0; k<3; k++) {
          colorString += (colors[i][k][j]);
          if (!(j == 255 && k == 2)) colorString += ",";
        }
      }
      colorString += "\n\n";
    }

    addMeta("luts", colorString);

    status("Populating metadata");

    // look for companion metadata files

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] list = parent.list();

    for (int i=0; i<list.length; i++) {
      if (list[i].endsWith("data.raw")) {
        RandomAccessStream raw = new RandomAccessStream(
          new Location(parent.getAbsolutePath(), list[i]).getAbsolutePath());
        used.add(new Location(
          parent.getAbsolutePath(), list[i]).getAbsolutePath());
        String line = raw.readLine();
        while (line != null && line.length() > 0) {
          if (line.charAt(0) != '[') {
            String key = line.substring(0, line.indexOf("="));
            String value = line.substring(line.indexOf("=") + 1);
            addMeta(key.trim(), value.trim());
          }
          line = raw.readLine();
        }
        raw.close();
      }
      else if (list[i].endsWith("lse.xml")) {
        RandomAccessStream raw = new RandomAccessStream(
          new Location(parent.getAbsolutePath(), list[i]).getAbsolutePath());
        used.add(new Location(
          parent.getAbsolutePath(), list[i]).getAbsolutePath());
        byte[] b = new byte[(int) raw.length()];
        raw.read(b);
        String xml = new String(b);

        if (xml.indexOf("SectionInfo") != -1) {
          int start = xml.indexOf("<SectionInfo>") + 13;
          int end = xml.indexOf("</SectionInfo>");
          xml = xml.substring(start, end);

          // parse the timestamps
          while (xml.length() > 0) {
            String element = xml.substring(0, xml.indexOf(">") + 1);
            xml = xml.substring(xml.indexOf(">") + 1);

            int ndx = element.indexOf("TimeCompleted") + 15;
            String stamp = element.substring(ndx, element.indexOf("\"", ndx));

            String key = element.substring(1, element.indexOf("\"",
              element.indexOf("\"") + 1));
            key = key.replace('\"', '\0');
            key = key.replace('=', ' ');

            addMeta(key + " Timestamp", stamp);
          }
        }
        raw.close();
        b = null;
      }
    }

    core.indexed[0] = false;
    core.falseColor[0] = false;

    // Populate the metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    // populate Image element
    store.setImage(name, null, null, null);

    // populate Pixels element
    in.seek(14);
    core.pixelType[0] = in.readShort() == 1 ? FormatTools.UINT8 :
      FormatTools.UINT16;

    core.currentOrder[0] = "XY";
    int[] dims = new int[] {core.sizeZ[0], core.sizeC[0], core.sizeT[0]};
    int max = 0;
    int min = Integer.MAX_VALUE;
    int median = 1;

    for (int i=0; i<dims.length; i++) {
      if (dims[i] < min) min = dims[i];
      if (dims[i] > max) max = dims[i];
      else median = dims[i];
    }

    int[] orderedDims = new int[] {max, median, min};
    for (int i=0; i<orderedDims.length; i++) {
      if (orderedDims[i] == core.sizeZ[0] &&
        core.currentOrder[0].indexOf("Z") == -1)
      {
        core.currentOrder[0] += "Z";
      }
      else if (orderedDims[i] == core.sizeC[0] &&
        core.currentOrder[0].indexOf("C") == -1)
      {
        core.currentOrder[0] += "C";
      }
      else core.currentOrder[0] += "T";
    }

    FormatTools.populatePixels(store, this);

    // populate Dimensions element
    int size = pixelSize.size();
    Float pixelSizeX = null, pixelSizeY = null, pixelSizeZ = null;
    if (size >= 1) pixelSizeX = new Float((String) pixelSize.get(0));
    if (size >= 2) pixelSizeY = new Float((String) pixelSize.get(1));
    if (size >= 3) pixelSizeZ = new Float((String) pixelSize.get(2));
    store.setDimensions(pixelSizeX, pixelSizeY, pixelSizeZ, null, null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      String gain = i == 0 ? gain1 : i == 1 ? gain2 : gain3;
      String offset = i == 0 ? offset1 : i == 1 ? gain2 : gain3;
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
       offset == null ? null : new Float(offset),
       gain == null ? null : new Float(gain), null, null, null, null, null,
       null, null, null, null, null, null, null, null, null);
      store.setDisplayChannel(new Integer(i), new Double(ramp1max),
        new Double(ramp1min), null, null);
    }
    store.setDisplayOptions(zoom == null ? null : new Float(zoom),
      new Boolean(core.sizeC[0] > 1), new Boolean(core.sizeC[0] >= 2),
      new Boolean(core.sizeC[0] >= 3), Boolean.FALSE, null,
      zstart == null ? null :
      new Integer((int) (new Double(zstart).doubleValue())), zstop == null ?
      null : new Integer((int) (new Double(zstop).doubleValue())), null, null,
      null, null, core.sizeC[0] > 1 ? new Integer(0) : null,
      core.sizeC[0] > 1 ? new Integer(1) : null,
      core.sizeC[0] > 1 ? new Integer(2) : null, new Integer(0));

    for (int i=0; i<3; i++) {
      String exc = i == 0 ? ex1 : i == 1 ? ex2 : ex3;
      String ems = i == 0 ? em1 : i == 1 ? em2 : em3;
      if (exc != null) store.setExcitationFilter(null, null, exc, null, null);
      if (ems != null) store.setEmissionFilter(null, null, ems, null, null);
    }
    if (mag != null) {
      store.setObjective(null, null, null, null, new Float(mag), null, null);
    }
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

}
