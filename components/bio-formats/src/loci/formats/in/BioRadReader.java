//
// BioRadReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-0year1 UW-Madison LOCI and Glencoe Software, Inc.

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
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.IMinMaxStore;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * BioRadReader is the file format reader for Bio-Rad PIC files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/BioRadReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/BioRadReader.java">SVN</a></dd></dl>
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

  /** List of note types. */
  public static final String[] NOTE_NAMES = {
    "0", "LIVE", "FILE1", "NUMBER", "USER", "LINE", "COLLECT", "FILE2",
    "SCALEBAR", "MERGE", "THRUVIEW", "ARROW", "12", "13", "14", "15",
    "16", "17", "18", "19", "VARIABLE", "STRUCTURE", "4D SERIES"
  };

  public static final int NOTE_TYPE_LIVE = 1;
  public static final int NOTE_TYPE_FILE1 = 2;
  public static final int NOTE_TYPE_NUMBER = 3;
  public static final int NOTE_TYPE_USER = 4;
  public static final int NOTE_TYPE_LINE = 5;
  public static final int NOTE_TYPE_COLLECT = 6;
  public static final int NOTE_TYPE_FILE2 = 7;
  public static final int NOTE_TYPE_SCALEBAR = 8;
  public static final int NOTE_TYPE_MERGE = 9;
  public static final int NOTE_TYPE_THRUVIEW = 10;
  public static final int NOTE_TYPE_ARROW = 11;
  public static final int NOTE_TYPE_VARIABLE = 20;
  public static final int NOTE_TYPE_STRUCTURE = 21;
  public static final int NOTE_TYPE_4D_SERIES = 22;

  public static final String[] PIC_SUFFIX = {"pic"};

  // -- Fields --

  private Vector used;

  private String[] picFiles;

  private byte[][][] lut;
  private int lastChannel;

  private Vector noteStrings;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() {
    super("Bio-Rad PIC", new String[] {"pic", "xml", "raw"});
    blockCheckLen = 56;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, PIC_SUFFIX)) return true;
    String fname = new File(name.toLowerCase()).getName();
    return fname.equals("lse.xml") || fname.equals("data.raw");
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, LITTLE_ENDIAN)) {
      return false;
    }
    String c = stream.readString(blockCheckLen);
    stream.seek(54);
    return stream.readShort() == PIC_FILE_ID || c.startsWith("[Input Sources]");
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return id.toLowerCase().endsWith(".pic") ? FormatTools.CAN_GROUP :
      FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut == null ? null : lut[lastChannel];
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

    lastChannel = getZCTCoords(no)[1];

    int bytes = FormatTools.getBytesPerPixel(getPixelType());

    if (picFiles != null) {
      int file = no % picFiles.length;
      RandomAccessStream ras = new RandomAccessStream(picFiles[file]);
      long offset = (no / picFiles.length) * getSizeX() * getSizeY() * bytes;
      ras.seek(offset + 76);

      readPlane(ras, x, y, w, h, buf);
      ras.close();
    }
    else {
      in.seek(no * getSizeX() * getSizeY() * bytes + 76);
      readPlane(in, x, y, w, h, buf);
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    used = null;
    picFiles = null;
    lut = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BioRadReader.initFile(" + id + ")");

    // always initialize a PIC file, even if we were given something else
    if (!checkSuffix(id, PIC_SUFFIX)) {
      Location dir = new Location(id).getAbsoluteFile().getParentFile();

      String[] list = dir.list();
      for (int i=0; i<list.length; i++) {
        if (checkSuffix(list[i], PIC_SUFFIX)) {
          id = new Location(dir.getAbsolutePath(), list[i]).getAbsolutePath();
        }
      }
      if (!checkSuffix(id, PIC_SUFFIX)) {
        throw new FormatException("No .pic files found - invalid dataset.");
      }
    }

    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    used = new Vector();
    used.add(currentId);

    status("Reading image dimensions");

    noteStrings = new Vector();

    // read header

    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();
    int npic = in.readShort();
    core[0].imageCount = npic;

    int ramp1min = in.readShort();
    int ramp1max = in.readShort();
    boolean notes = in.readInt() != 0;
    core[0].pixelType =
      in.readShort() == 0 ? FormatTools.UINT16 : FormatTools.UINT8;
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
    addMeta("nx", getSizeX());
    addMeta("ny", getSizeY());
    addMeta("npic", getImageCount());
    addMeta("ramp1_min", ramp1min);
    addMeta("ramp1_max", ramp1max);
    addMeta("notes", notes);
    addMeta("image_number", imageNumber);
    addMeta("name", name);
    addMeta("merged", MERGE_NAMES[merged]);
    addMeta("color1", color1);
    addMeta("file_id", fileId);
    addMeta("ramp2_min", ramp2min);
    addMeta("ramp2_max", ramp2max);
    addMeta("color2", color2);
    addMeta("edited", edited);
    addMeta("lens", lens);
    addMeta("mag_factor", magFactor);

    // skip image data
    int imageLen = getSizeX() * getSizeY();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    in.skipBytes(bpp * getImageCount() * imageLen + 6);

    core[0].sizeZ = getImageCount();
    core[0].sizeC = 1;
    core[0].sizeT = 1;

    core[0].orderCertain = false;
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].littleEndian = LITTLE_ENDIAN;
    core[0].metadataComplete = true;
    core[0].falseColor = true;

    status("Reading notes");

    String zoom = null, zstart = null, zstop = null, mag = null;
    String gain1 = null, gain2 = null, gain3 = null;
    String offset1 = null;
    String ex1 = null, ex2 = null, ex3 = null;
    String em1 = null, em2 = null, em3 = null;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    int nLasers = 0;

    // read notes
    int noteCount = 0;
    boolean brokenNotes = false;
    while (notes) {
      // read in note

      Note n = new Note();
      n.level = in.readShort();
      notes = in.readInt() != 0;
      n.num = in.readShort();
      n.status = in.readShort();
      n.type = in.readShort();
      n.x = in.readShort();
      n.y = in.readShort();
      n.p = in.readString(80);

      if (n.type < 0 || n.type >= NOTE_NAMES.length) {
        notes = false;
        brokenNotes = true;
        break;
      }

      // be sure to remove binary data from the note text
      int ndx = n.p.length();
      for (int i=0; i<n.p.length(); i++) {
        if (n.p.charAt(i) == 0) {
          ndx = i;
          break;
        }
      }

      n.p = n.p.substring(0, ndx).trim();

      String[] tokens = n.p.split(" ");
      try {
        if (tokens.length > 1) {
          int noteType = Integer.parseInt(tokens[1]);

          if (noteType == 2 && n.p.indexOf("AXIS_4") != -1) {
            core[0].sizeZ = 1;
            core[0].sizeT = getImageCount();
            core[0].orderCertain = true;
          }
        }
      }
      catch (NumberFormatException e) { }

      // add note to list
      noteCount++;

      noteStrings.add(n);
    }

    status("Reading color table");

    // read color tables
    int numLuts = 0;
    lut = new byte[3][3][256];
    boolean eof = false;
    int next = 0;
    while (!eof && numLuts < 3 && !brokenNotes) {
      if (in.getFilePointer() + lut[numLuts][next].length <= in.length()) {
        in.read(lut[numLuts][next++]);
        if (next == 3) {
          next = 0;
          numLuts++;
        }
      }
      else eof = true;
      if (eof && numLuts == 0) lut = null;
    }
    if (brokenNotes) lut = null;

    if (debug && debugLevel >= 2) {
      debug(numLuts + " color table" + (numLuts == 1 ? "" : "s") + " present.");
    }

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

        DefaultHandler handler = new BioRadHandler();
        DataTools.parseXML(xml, handler);

        for (int q=0; q<list.length; q++) {
          if (checkSuffix(list[q], PIC_SUFFIX)) {
            path =
              new Location(parent.getAbsolutePath(), list[q]).getAbsolutePath();
            pics.add(path);
            if (!used.contains(path)) used.add(path);
          }
        }
      }
      else if (list[i].endsWith("data.raw")) {
        used.add(
          new Location(parent.getAbsolutePath(), list[i]).getAbsolutePath());
      }
    }

    picFiles = (String[]) pics.toArray(new String[0]);

    core[0].indexed = lut != null;

    // populate Pixels

    core[0].dimensionOrder = "XYCTZ";

    if (picFiles.length > 0) {
      core[0].imageCount = npic * picFiles.length;
      core[0].sizeC = getImageCount() / (getSizeZ() * getSizeT());
    }
    else picFiles = null;

    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setImageName(name, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    // link Objective to Image using ObjectiveSettings
    store.setObjectiveID("Objective:0", 0, 0);
    store.setObjectiveSettingsObjective("Objective:0", 0);

    store.setObjectiveLensNA(new Float(lens), 0, 0);
    store.setObjectiveNominalMagnification(new Integer((int) magFactor), 0, 0);
    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    for (int q=0; q<noteStrings.size(); q++) {
      Note n = (Note) noteStrings.get(q);

      switch (n.type) {
        case NOTE_TYPE_USER:
          // TODO : this should be an overlay
          addMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_SCALEBAR:
          // TODO : this should be an overlay
          // the format of the text is:
          // SCALEBAR = <length> <angle>
          // where <length> is the length of the scalebar in microns,
          // and <angle> is the angle in degrees
          addMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_ARROW:
          // TODO : this should be an overlay
          // the format of the text is:
          // ARROW = <lx> <ly> <angle> <fill>
          // where <lx> and <ly> define the arrow's bounding box,
          // <angle> is the angle in degrees and <fill> is either "Fill" or
          // "Outline"
          addMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_VARIABLE:
          if (n.p.indexOf("=") >= 0) {
            String key = n.p.substring(0, n.p.indexOf("=")).trim();
            String value = n.p.substring(n.p.indexOf("=") + 1).trim();
            addMeta(key, value);

            if (key.equals("INFO_OBJECTIVE_NAME")) {
              store.setObjectiveModel(value, 0, 0);
            }
            else if (key.equals("INFO_OBJECTIVE_MAGNIFICATION")) {
              store.setObjectiveNominalMagnification(
                new Integer((int) Double.parseDouble(value)), 0, 0);
            }
            else if (key.equals("LENS_MAGNIFICATION")) {
              store.setObjectiveNominalMagnification(
                new Integer((int) Double.parseDouble(value)), 0, 0);
            }
            else if (key.startsWith("SETTING")) {
              if (key.indexOf("_DET_") != -1) {
                int index = key.indexOf("_DET_") + 5;
                if (key.lastIndexOf("_") > index) {
                  String idx = key.substring(index, key.indexOf("_", index));
                  int detector = Integer.parseInt(idx) - 1;
                  if (key.endsWith("OFFSET")) {
                    store.setDetectorSettingsOffset(
                      new Float(value), 0, detector);
                  }
                  else if (key.endsWith("GAIN")) {
                    store.setDetectorSettingsGain(
                      new Float(value), 0, detector);
                  }

                  store.setDetectorType("Unknown", 0, detector);

                  // link DetectorSettings to an actual Detector
                  store.setDetectorID("Detector:" + detector, 0, detector);
                  store.setDetectorSettingsDetector("Detector:" + detector, 0,
                    detector);
                }
              }
            }
          }
          else {
            addMeta("Note #" + noteCount, n.toString());
          }
          break;
        case NOTE_TYPE_STRUCTURE:
          int structureType = (n.x & 0xff00) >> 8;
          int version = (n.x & 0xff);
          String[] values = n.p.split(" ");
          if (structureType == 1) {
            switch (n.y) {
              case 1:
                addMeta("Scan Channel", values[0]);
                addMeta("Both mode", values[1]);
                addMeta("Speed", values[2]);
                addMeta("Filter", values[3]);
                addMeta("Factor", values[4]);
                addMeta("Number of scans", values[5]);
                addMeta("Photon counting mode (channel 1)", values[6]);
                addMeta("Photon counting detector (channel 1)", values[7]);
                addMeta("Photon counting mode (channel 2)", values[8]);
                addMeta("Photon counting detector (channel 2)", values[9]);
                addMeta("Photon mode", values[10]);
                addMeta("Objective magnification", values[11]);
                addMeta("Zoom factor", values[12]);
                addMeta("Motor on", values[13]);
                addMeta("Z Step Size", values[14]);

                store.setObjectiveNominalMagnification(
                  new Integer((int) Double.parseDouble(values[11])), 0, 0);
                store.setDisplayOptionsZoom(new Float(values[12]), 0);
                store.setDimensionsPhysicalSizeZ(new Float(values[14]), 0, 0);
                break;
              case 2:
                addMeta("Z Start", values[0]);
                addMeta("Z Stop", values[1]);
                addMeta("Scan area X coordinate", values[2]);
                addMeta("Scan area Y coordinate", values[3]);
                addMeta("Scan area width", values[4]);
                addMeta("Scan area height", values[5]);

                float width =
                  Float.parseFloat(values[4]) - Float.parseFloat(values[2]);
                width /= getSizeX();
                float height =
                  Float.parseFloat(values[5]) - Float.parseFloat(values[3]);
                height /= getSizeY();

                store.setDimensionsPhysicalSizeX(new Float(width), 0, 0);
                store.setDimensionsPhysicalSizeY(new Float(height), 0, 0);

                break;
              case 3:
                for (int i=0; i<3; i++) {
                  addMeta("Iris for PMT " + (i + 1), values[i * 4]);
                  addMeta("Gain for PMT " + (i + 1), values[i * 4 + 1]);
                  addMeta("Black level for PMT " + (i + 1), values[i * 4 + 2]);
                  addMeta("Emission filter for PMT " + (i+1), values[i*4] + 3);
                  addMeta("Multiplier for channel " + (i+1), values[12 + i]);
                }
                break;
              case 4:
                nLasers = Integer.parseInt(values[0]);
                addMeta("Number of lasers", values[0]);
                addMeta("Number of transmission detectors", values[1]);
                addMeta("Number of PMTs", values[2]);
                for (int i=1; i<=3; i++) {
                  int idx = (i + 1) * 3;
                  addMeta("Shutter present for laser " + i, values[i + 2]);
                  addMeta("Neutral density filter for laser " + i, values[idx]);
                  addMeta("Excitation filter for laser " + i, values[idx + 1]);
                  addMeta("Use laser " + i, values[idx + 2]);
                }
                for (int i=0; i<nLasers; i++) {
                  addMeta("Neutral density filter name - laser " + (i + 1),
                    values[15 + i]);
                }
                break;
              case 5:
                for (int i=0; i<nLasers; i++) {
                  addMeta("Excitation filter name - laser " + (i+1), values[i]);
                }
                break;
              case 6:
                for (int i=0; i<nLasers; i++) {
                  addMeta("Emission filter name - laser " + (i+1), values[i]);
                }
                break;
              case 7:
                for (int i=0; i<2; i++) {
                  String prefix = "Mixer " + i;
                  addMeta(prefix + " - enhanced", values[i*7]);
                  addMeta(prefix + " - PMT 1 percentage", values[i*7 + 1]);
                  addMeta(prefix + " - PMT 2 percentage", values[i*7 + 2]);
                  addMeta(prefix + " - PMT 3 percentage", values[i*7 + 3]);
                  addMeta(prefix + " - Transmission 1 percentage",
                    values[i * 7 + 4]);
                  addMeta(prefix + " - Transmission 2 percentage",
                    values[i * 7 + 5]);
                  addMeta(prefix + " - Transmission 3 percentage",
                    values[i * 7 + 6]);
                }
                addMeta("Mixer 0 - low signal on", values[14]);
                addMeta("Mixer 1 - low signal on", values[15]);
                break;
              case 8:
                addMeta("Laser name - laser 1", values[0]);
                break;
              case 9:
                addMeta("Laser name - laser 2", values[0]);
                break;
              case 10:
                addMeta("Laser name - laser 3", values[0]);
                break;
              case 11:
                for (int i=0; i<3; i++) {
                  addMeta("Transmission detector " + (i + 1) + " - offset",
                    values[i * 3]);
                  addMeta("Transmission detector " + (i + 1) + " - gain",
                    values[i * 3 + 1]);
                  addMeta("Transmission detector " + (i + 1) + " - black level",
                    values[i * 3 + 2]);

                  store.setDetectorOffset(new Float(values[i * 3]), 0, i);
                  store.setDetectorGain(new Float(values[i * 3 + 1]), 0, i);
                  store.setDetectorType("Unknown", 0, i);
                }
                break;
              case 12:
                for (int i=0; i<2; i++) {
                  addMeta("Part number for laser " + (i + 1), values[i * 4]);
                  addMeta("Part number for excitation filter for laser " +
                    (i + 1), values[i * 4 + 1]);
                  addMeta("Part number for ND filter for laser " + (i + 1),
                    values[i * 4 + 2]);
                  addMeta("Part number for emission filter for laser " +
                    (i + 1), values[i * 4 + 3]);
                }
                break;
              case 13:
                  addMeta("Part number for laser 3", values[0]);
                  addMeta("Part number for excitation filter for laser 3",
                    values[1]);
                  addMeta("Part number for ND filter for laser 3", values[2]);
                  addMeta("Part number for emission filter for laser 3",
                    values[3]);
                  addMeta("Part number for filter block 1", values[4]);
                  addMeta("Part number for filter block 2", values[5]);
                break;
              case 14:
                addMeta("Filter Block Name - filter block 1", values[0]);
                addMeta("Filter Block Name - filter block 2", values[1]);
                break;
              case 15:
                for (int i=0; i<5; i++) {
                  addMeta("Image bands status - band " + (i + 1), values[i*3]);
                  addMeta("Image bands min - band " + (i + 1), values[i*3 + 1]);
                  addMeta("Image bands max - band " + (i + 1), values[i*3 + 2]);
                  if (store instanceof IMinMaxStore) {
                    ((IMinMaxStore) store).setChannelGlobalMinMax(i,
                      Double.parseDouble(values[i*3 + 1]),
                      Double.parseDouble(values[i*3 + 2]), 0);
                  }
                }
                break;
              case 17:
                int year = Integer.parseInt(values[5]) + 1900;
                for (int i=0; i<5; i++) {
                  if (values[i].length() == 1) values[i] = "0" + values[i];
                }

                // date is in yyyy-MM-dd'T'HH:mm:ss
                String date = year + "-" + values[4] + "-" + values[3] + "T" +
                  values[2] + ":" + values[1] + ":" + values[0];
                addMeta("Acquisition date", date);
                store.setImageCreationDate(date, 0);
                break;
              case 18:
                addMeta("Mixer 3 - enhanced", values[0]);
                for (int i=1; i<=3; i++) {
                  addMeta("Mixer 3 - PMT " + i + " percentage", values[i]);
                  addMeta("Mixer 3 - Transmission " + i + " percentage",
                    values[i + 3]);
                  addMeta("Mixer 3 - photon counting " + i, values[i + 7]);
                }
                addMeta("Mixer 3 - low signal on", values[7]);
                addMeta("Mixer 3 - mode", values[11]);
                break;
              case 19:
                for (int i=1; i<=2; i++) {
                  String prefix = "Mixer " + i;
                  addMeta(prefix + " - photon counting 1", values[i * 4 - 4]);
                  addMeta(prefix + " - photon counting 2", values[i * 4 - 3]);
                  addMeta(prefix + " - photon counting 3", values[i * 4 - 2]);
                  addMeta(prefix + " - mode", values[i * 4 - 1]);
                }
                break;
              case 20:
                addMeta("Display mode", values[0]);
                addMeta("Course", values[1]);
                addMeta("Time Course - experiment type", values[2]);
                addMeta("Time Course - kd factor", values[3]);
                store.setExperimentType(values[2], 0);
                break;
              case 21:
                addMeta("Time Course - ion name", values[0]);
                break;
              case 22:
                addMeta("PIC file generated on Isoscan (lite)", values[0]);
                for (int i=1; i<=3; i++) {
                  addMeta("Photon counting used (PMT " + i + ")", values[i]);
                  addMeta("Hot spot filter used (PMT " + i + ")", values[i+3]);
                  addMeta("Tx Selector used (TX " + i + ")", values[i + 6]);
                }
                break;
            }
          }
          break;
        default:
          // notes for display only
          addMeta("Note #" + noteCount, n.toString());
      }

      // if the text of the note contains "AXIS", parse the text
      // more thoroughly (see pg. 21 of the BioRad specs)

      if (n.p.indexOf("AXIS") != -1) {
        String[] values = n.p.split(" ");
        String key = values[0];
        String noteType = values[1];

        int axisType = Integer.parseInt(noteType);

        if (values.length > 2) {
          switch (axisType) {
            case 1:
              String dx = values[2];
              String dy = values[3];
              addMeta(key + " distance (X) in microns", dx);
              addMeta(key + " distance (Y) in microns", dy);
              break;
            case 3:
              addMeta(key + " angle (X) in degrees", values[2]);
              addMeta(key + " angle (Y) in degrees", values[3]);
              break;
            case 4:
              addMeta(key + " intensity (X)", values[2]);
              addMeta(key + " intensity (Y)", values[3]);
              break;
            case 6:
              addMeta(key + " ratio (X)", values[2]);
              addMeta(key + " ratio (Y)", values[3]);
              break;
            case 7:
              addMeta(key + " log ratio (X)", values[2]);
              addMeta(key + " log ratio (Y)", values[3]);
              break;
            case 9:
              addMeta(key + " noncalibrated intensity min", values[2]);
              addMeta(key + " noncalibrated intensity max", values[3]);
              addMeta(key + " calibrated intensity min", values[4]);
              addMeta(key + " calibrated intensity max", values[5]);
              break;
            case 11:
              addMeta(key + " RGB type (X)", values[2]);
              addMeta(key + " RGB type (Y)", values[3]);
              break;
            case 14:
              addMeta(key + " time course type (X)", values[2]);
              addMeta(key + " time course type (Y)", values[3]);
              break;
            case 15:
              addMeta(key + " inverse sigmoid calibrated intensity (min)",
                values[2]);
              addMeta(key + " inverse sigmoid calibrated intensity (max)",
                values[3]);
              addMeta(key + " inverse sigmoid calibrated intensity (beta)",
                values[4]);
              addMeta(key + " inverse sigmoid calibrated intensity (Kd)",
                values[5]);
              break;
            case 16:
              addMeta(key + " log inverse sigmoid calibrated intensity (min)",
                values[2]);
              addMeta(key + " log inverse sigmoid calibrated intensity (max)",
                values[3]);
              addMeta(key + " log inverse sigmoid calibrated intensity (beta)",
                values[4]);
              addMeta(key + " log inverse sigmoid calibrated intensity (Kd)",
                values[5]);
              break;
          }
        }
      }
    }
  }

  // -- Helper classes --

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
        int count = getSizeZ() * getSizeC() * getSizeT();
        core[0].sizeZ = z;
        core[0].sizeC = c;
        core[0].sizeT = t;
        if (count >= getImageCount()) core[0].imageCount = count;
        else core[0].sizeC = getImageCount() / count;
      }
      else if (qName.equals("Z") || qName.equals("C") || qName.equals("T")) {
        String stamp = attributes.getValue("TimeCompleted");
        int count = 0;
        while (metadata.containsKey("Timestamp " + count)) count++;
        addMeta("Timestamp " + count, stamp);
      }
    }
  }

  class Note {
    public int num;
    public int level;
    public int status;
    public int type;
    public int x;
    public int y;
    public String p;

    public String toString() {
      StringBuffer sb = new StringBuffer(100);
      sb.append("level=");
      sb.append(level);
      sb.append("; num=");
      sb.append(num);
      sb.append("; status=");
      sb.append(status);
      sb.append("; type=");
      sb.append(NOTE_NAMES[type]);
      sb.append("; x=");
      sb.append(x);
      sb.append("; y=");
      sb.append(y);
      sb.append("; text=");
      sb.append(p == null ? "null" : p.trim());
      return sb.toString();
    }
  }

}
