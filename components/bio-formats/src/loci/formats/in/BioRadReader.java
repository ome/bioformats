//
// BioRadReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-0year1 UW-Madison LOCI and Glencoe Software, Inc.

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
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

  private Vector<String> used;

  private String[] picFiles;

  private byte[][][] lut;
  private int lastChannel;

  private Vector<Note> noteStrings;

  private Vector<Float> offset, gain;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() {
    super("Bio-Rad PIC", new String[] {"pic", "xml", "raw"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, PIC_SUFFIX)) return true;
    String fname = new File(name.toLowerCase()).getName();
    return fname.equals("lse.xml") || fname.equals("data.raw");
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 56;
    if (!FormatTools.validStream(stream, blockLen, LITTLE_ENDIAN)) {
      return false;
    }
    String c = stream.readString(blockLen);
    stream.seek(54);
    return stream.readShort() == PIC_FILE_ID || c.startsWith("[Input Sources]");
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    Location parent = new Location(id).getParentFile();
    String[] list = parent.list();
    for (String f : list) {
      if (checkSuffix(f, "raw") || checkSuffix(f, "xml")) {
        return FormatTools.MUST_GROUP;
      }
    }
    return FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut == null ? null : lut[lastChannel];
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      Vector<String> files = new Vector<String>();
      for (String f : used) {
        if (!checkSuffix(f, PIC_SUFFIX)) files.add(f);
      }
      return files.toArray(new String[files.size()]);
    }
    return used.toArray(new String[used.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = getZCTCoords(no)[1];

    if (picFiles != null) {
      int file = no % picFiles.length;
      RandomAccessInputStream ras = new RandomAccessInputStream(picFiles[file]);
      long offset = (no / picFiles.length) * FormatTools.getPlaneSize(this);
      ras.seek(offset + 76);

      readPlane(ras, x, y, w, h, buf);
      ras.close();
    }
    else {
      in.seek(no * FormatTools.getPlaneSize(this) + 76);
      readPlane(in, x, y, w, h, buf);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      used = null;
      picFiles = null;
      lut = null;
      lastChannel = 0;
      noteStrings = null;
      offset = gain = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("BioRadReader.initFile(" + id + ")");

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
    in = new RandomAccessInputStream(id);
    in.order(true);

    offset = new Vector<Float>();
    gain = new Vector<Float>();

    used = new Vector<String>();
    used.add(currentId);

    status("Reading image dimensions");

    noteStrings = new Vector<Note>();

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
    for (int i=0; i<name.length(); i++) {
      if (name.charAt(i) == 0) {
        name = name.substring(0, i);
        break;
      }
    }

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
    addGlobalMeta("nx", getSizeX());
    addGlobalMeta("ny", getSizeY());
    addGlobalMeta("npic", getImageCount());
    addGlobalMeta("ramp1_min", ramp1min);
    addGlobalMeta("ramp1_max", ramp1max);
    addGlobalMeta("notes", notes);
    addGlobalMeta("image_number", imageNumber);
    addGlobalMeta("name", name);
    addGlobalMeta("merged", MERGE_NAMES[merged]);
    addGlobalMeta("color1", color1);
    addGlobalMeta("file_id", fileId);
    addGlobalMeta("ramp2_min", ramp2min);
    addGlobalMeta("ramp2_max", ramp2max);
    addGlobalMeta("color2", color2);
    addGlobalMeta("edited", edited);
    addGlobalMeta("lens", lens);
    addGlobalMeta("mag_factor", magFactor);

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

      String value = n.p.replaceAll("=", "");
      Vector<String> v = new Vector<String>();
      StringTokenizer t = new StringTokenizer(value, " ");
      while (t.hasMoreTokens()) {
        String token = t.nextToken().trim();
        if (token.length() > 0) v.add(token);
      }
      String[] tokens = v.toArray(new String[v.size()]);
      try {
        if (tokens.length > 1) {
          int noteType = Integer.parseInt(tokens[1]);

          if (noteType == 2 && value.indexOf("AXIS_4") != -1) {
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


    String message = numLuts + " color table" + (numLuts == 1 ? "" : "s") +
      " present.";
    debug(message, 2);

    status("Populating metadata");

    // look for companion metadata files

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] list = parent.list(true);
    Arrays.sort(list);

    Vector<String> pics = new Vector<String>();

    for (int i=0; i<list.length; i++) {
      if (list[i].endsWith("lse.xml")) {
        String path =
          new Location(parent.getAbsolutePath(), list[i]).getAbsolutePath();
        RandomAccessInputStream raw = new RandomAccessInputStream(path);
        used.add(path);
        byte[] xml = new byte[(int) raw.length()];
        raw.read(xml);
        raw.close();

        DefaultHandler handler = new BioRadHandler();
        XMLTools.parseXML(xml, handler);

        used.remove(currentId);
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

    core[0].indexed = lut != null;

    // populate Pixels

    core[0].dimensionOrder = "XYCTZ";

    int nextDetector = 0;

    boolean multipleFiles = false;
    for (Note n : noteStrings) {
      switch (n.type) {
        case NOTE_TYPE_USER:
          // TODO : this should be an overlay
          addGlobalMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_SCALEBAR:
          // TODO : this should be an overlay
          // the format of the text is:
          // SCALEBAR = <length> <angle>
          // where <length> is the length of the scalebar in microns,
          // and <angle> is the angle in degrees
          addGlobalMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_ARROW:
          // TODO : this should be an overlay
          // the format of the text is:
          // ARROW = <lx> <ly> <angle> <fill>
          // where <lx> and <ly> define the arrow's bounding box,
          // <angle> is the angle in degrees and <fill> is either "Fill" or
          // "Outline"
          addGlobalMeta("Note #" + noteCount, n.toString());
          break;
        case NOTE_TYPE_VARIABLE:
          if (n.p.indexOf("=") >= 0) {
            String key = n.p.substring(0, n.p.indexOf("=")).trim();
            String value = n.p.substring(n.p.indexOf("=") + 1).trim();
            addGlobalMeta(key, value);

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
                  String detectorID =
                    MetadataTools.createLSID("Detector", 0, nextDetector);
                  store.setDetectorID(detectorID, 0, nextDetector);
                  store.setDetectorType("Unknown", 0, nextDetector);

                  if (key.endsWith("OFFSET")) {
                    if (nextDetector < offset.size()) {
                      offset.setElementAt(new Float(value), nextDetector);
                    }
                    else {
                      while (nextDetector > offset.size()) {
                        offset.add(null);
                      }
                      offset.add(new Float(value));
                    }
                  }
                  else if (key.endsWith("GAIN")) {
                    if (nextDetector < gain.size()) {
                      gain.setElementAt(new Float(value), nextDetector);
                    }
                    else {
                      while (nextDetector > gain.size()) {
                        gain.add(null);
                      }
                      gain.add(new Float(value));
                    }
                  }
                  nextDetector++;
                }
              }
            }
            else {
              String[] values = value.split(" ");
              if (values.length > 1) {
                try {
                  int type = Integer.parseInt(values[0]);
                  if (type == 257 && values.length >= 3) {
                    // found length of axis in um
                    Float pixelSize = new Float(values[2]);
                    if (key.equals("AXIS_2")) {
                      store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
                    }
                    else if (key.equals("AXIS_3")) {
                      store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
                    }
                  }
                }
                catch (NumberFormatException e) { }
              }
            }
          }
          else if (n.p.startsWith("AXIS_2")) {
            String[] values = n.p.split(" ");
            Float pixelSize = new Float(values[3]);
            store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
          }
          else if (n.p.startsWith("AXIS_3")) {
            String[] values = n.p.split(" ");
            Float pixelSize = new Float(values[3]);
            store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
          }
          else {
            addGlobalMeta("Note #" + noteCount, n.toString());
          }
          break;
        case NOTE_TYPE_STRUCTURE:
          int structureType = (n.x & 0xff00) >> 8;
          int version = (n.x & 0xff);
          String[] values = n.p.split(" ");
          if (structureType == 1) {
            switch (n.y) {
              case 1:
                addGlobalMeta("Scan Channel", values[0]);
                addGlobalMeta("Both mode", values[1]);
                addGlobalMeta("Speed", values[2]);
                addGlobalMeta("Filter", values[3]);
                addGlobalMeta("Factor", values[4]);
                addGlobalMeta("Number of scans", values[5]);
                addGlobalMeta("Photon counting mode (channel 1)", values[6]);
                addGlobalMeta("Photon counting detector (channel 1)",
                  values[7]);
                addGlobalMeta("Photon counting mode (channel 2)", values[8]);
                addGlobalMeta("Photon counting detector (channel 2)",
                  values[9]);
                addGlobalMeta("Photon mode", values[10]);
                addGlobalMeta("Objective magnification", values[11]);
                addGlobalMeta("Zoom factor", values[12]);
                addGlobalMeta("Motor on", values[13]);
                addGlobalMeta("Z Step Size", values[14]);

                store.setObjectiveNominalMagnification(
                  new Integer((int) Double.parseDouble(values[11])), 0, 0);
                store.setDimensionsPhysicalSizeZ(new Float(values[14]), 0, 0);
                break;
              case 2:
                addGlobalMeta("Z Start", values[0]);
                addGlobalMeta("Z Stop", values[1]);
                addGlobalMeta("Scan area X coordinate", values[2]);
                addGlobalMeta("Scan area Y coordinate", values[3]);
                addGlobalMeta("Scan area width", values[4]);
                addGlobalMeta("Scan area height", values[5]);

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
                  addGlobalMeta("Iris for PMT " + (i + 1), values[i * 4]);
                  addGlobalMeta("Gain for PMT " + (i + 1), values[i * 4 + 1]);
                  addGlobalMeta("Black level for PMT " + (i + 1),
                    values[i * 4 + 2]);
                  addGlobalMeta("Emission filter for PMT " + (i+1),
                    values[i*4] + 3);
                  addGlobalMeta("Multiplier for channel " + (i+1),
                    values[12 + i]);
                }
                break;
              case 4:
                nLasers = Integer.parseInt(values[0]);
                addGlobalMeta("Number of lasers", values[0]);
                addGlobalMeta("Number of transmission detectors", values[1]);
                addGlobalMeta("Number of PMTs", values[2]);
                for (int i=1; i<=3; i++) {
                  int idx = (i + 1) * 3;
                  addGlobalMeta("Shutter present for laser " + i,
                    values[i + 2]);
                  addGlobalMeta("Neutral density filter for laser " + i,
                    values[idx]);
                  addGlobalMeta("Excitation filter for laser " + i,
                    values[idx + 1]);
                  addGlobalMeta("Use laser " + i, values[idx + 2]);
                }
                for (int i=0; i<nLasers; i++) {
                  addGlobalMeta("Neutral density filter name - laser " +
                    (i + 1), values[15 + i]);
                }
                break;
              case 5:
                for (int i=0; i<nLasers; i++) {
                  addGlobalMeta("Excitation filter name - laser " + (i+1),
                    values[i]);
                }
                break;
              case 6:
                for (int i=0; i<nLasers; i++) {
                  addGlobalMeta("Emission filter name - laser " + (i+1),
                    values[i]);
                }
                break;
              case 7:
                for (int i=0; i<2; i++) {
                  String prefix = "Mixer " + i;
                  addGlobalMeta(prefix + " - enhanced", values[i*7]);
                  addGlobalMeta(prefix + " - PMT 1 percentage",
                    values[i*7 + 1]);
                  addGlobalMeta(prefix + " - PMT 2 percentage",
                    values[i*7 + 2]);
                  addGlobalMeta(prefix + " - PMT 3 percentage",
                    values[i*7 + 3]);
                  addGlobalMeta(prefix + " - Transmission 1 percentage",
                    values[i * 7 + 4]);
                  addGlobalMeta(prefix + " - Transmission 2 percentage",
                    values[i * 7 + 5]);
                  addGlobalMeta(prefix + " - Transmission 3 percentage",
                    values[i * 7 + 6]);
                }
                addGlobalMeta("Mixer 0 - low signal on", values[14]);
                addGlobalMeta("Mixer 1 - low signal on", values[15]);
                break;
              case 8:
                addGlobalMeta("Laser name - laser 1", values[0]);
                break;
              case 9:
                addGlobalMeta("Laser name - laser 2", values[0]);
                break;
              case 10:
                addGlobalMeta("Laser name - laser 3", values[0]);
                break;
              case 11:
                for (int i=0; i<3; i++) {
                  String prefix = "Transmission detector " + (i + 1) + " - ";
                  addGlobalMeta(prefix + "offset", values[i * 3]);
                  addGlobalMeta(prefix + "gain", values[i * 3 + 1]);
                  addGlobalMeta(prefix + "black level", values[i * 3 + 2]);

                  String detectorID =
                    MetadataTools.createLSID("Detector", 0, i);
                  store.setDetectorID(detectorID, 0, i);
                  store.setDetectorOffset(new Float(values[i * 3]), 0, i);
                  store.setDetectorGain(new Float(values[i * 3 + 1]), 0, i);
                  store.setDetectorType("Unknown", 0, i);
                }
                break;
              case 12:
                for (int i=0; i<2; i++) {
                  String prefix = "Part number for ";
                  addGlobalMeta(prefix + "laser " + (i + 1), values[i * 4]);
                  addGlobalMeta(prefix + "excitation filter for laser " +
                    (i + 1), values[i * 4 + 1]);
                  addGlobalMeta(prefix + "ND filter for laser " + (i + 1),
                    values[i * 4 + 2]);
                  addGlobalMeta(prefix + "emission filter for laser " + (i + 1),
                    values[i * 4 + 3]);
                }
                break;
              case 13:
                  addGlobalMeta("Part number for laser 3", values[0]);
                  addGlobalMeta("Part number for excitation filter for laser 3",
                    values[1]);
                  addGlobalMeta("Part number for ND filter for laser 3",
                    values[2]);
                  addGlobalMeta("Part number for emission filter for laser 3",
                    values[3]);
                  addGlobalMeta("Part number for filter block 1", values[4]);
                  addGlobalMeta("Part number for filter block 2", values[5]);
                break;
              case 14:
                addGlobalMeta("Filter Block Name - filter block 1", values[0]);
                addGlobalMeta("Filter Block Name - filter block 2", values[1]);
                break;
              case 15:
                for (int i=0; i<5; i++) {
                  addGlobalMeta("Image bands status - band " + (i + 1),
                    values[i*3]);
                  addGlobalMeta("Image bands min - band " + (i + 1),
                    values[i*3 + 1]);
                  addGlobalMeta("Image bands max - band " + (i + 1),
                    values[i*3 + 2]);
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
                addGlobalMeta("Acquisition date", date);
                store.setImageCreationDate(date, 0);
                break;
              case 18:
                addGlobalMeta("Mixer 3 - enhanced", values[0]);
                for (int i=1; i<=3; i++) {
                  addGlobalMeta("Mixer 3 - PMT " + i + " percentage",
                    values[i]);
                  addGlobalMeta("Mixer 3 - Transmission " + i + " percentage",
                    values[i + 3]);
                  addGlobalMeta("Mixer 3 - photon counting " + i,
                    values[i + 7]);
                }
                addGlobalMeta("Mixer 3 - low signal on", values[7]);
                addGlobalMeta("Mixer 3 - mode", values[11]);
                break;
              case 19:
                for (int i=1; i<=2; i++) {
                  String prefix = "Mixer " + i;
                  addGlobalMeta(prefix + " - photon counting 1",
                    values[i * 4 - 4]);
                  addGlobalMeta(prefix + " - photon counting 2",
                    values[i * 4 - 3]);
                  addGlobalMeta(prefix + " - photon counting 3",
                    values[i * 4 - 2]);
                  addGlobalMeta(prefix + " - mode", values[i * 4 - 1]);
                }
                break;
              case 20:
                addGlobalMeta("Display mode", values[0]);
                addGlobalMeta("Course", values[1]);
                addGlobalMeta("Time Course - experiment type", values[2]);
                addGlobalMeta("Time Course - kd factor", values[3]);
                store.setExperimentType(values[2], 0);
                break;
              case 21:
                addGlobalMeta("Time Course - ion name", values[0]);
                break;
              case 22:
                addGlobalMeta("PIC file generated on Isoscan (lite)",
                  values[0]);
                for (int i=1; i<=3; i++) {
                  addGlobalMeta("Photon counting used (PMT " + i + ")",
                    values[i]);
                  addGlobalMeta("Hot spot filter used (PMT " + i + ")",
                    values[i+3]);
                  addGlobalMeta("Tx Selector used (TX " + i + ")",
                    values[i + 6]);
                }
                break;
            }
          }
          break;
        default:
          // notes for display only
          addGlobalMeta("Note #" + noteCount, n.toString());
      }

      // if the text of the note contains "AXIS", parse the text
      // more thoroughly (see pg. 21 of the BioRad specs)

      if (n.p.indexOf("AXIS") != -1) {
        n.p = n.p.replaceAll("=", "");
        Vector<String> v = new Vector<String>();
        StringTokenizer tokens = new StringTokenizer(n.p, " ");
        while (tokens.hasMoreTokens()) {
          String token = tokens.nextToken().trim();
          if (token.length() > 0) v.add(token);
        }
        String[] values = v.toArray(new String[v.size()]);
        String key = values[0];
        String noteType = values[1];

        int axisType = Integer.parseInt(noteType);

        if (values.length > 2) {
          switch (axisType) {
            case 1:
              String dx = values[2];
              String dy = values[3];
              addGlobalMeta(key + " distance (X) in microns", dx);
              addGlobalMeta(key + " distance (Y) in microns", dy);
              break;
            case 3:
              addGlobalMeta(key + " angle (X) in degrees", values[2]);
              addGlobalMeta(key + " angle (Y) in degrees", values[3]);
              break;
            case 4:
              addGlobalMeta(key + " intensity (X)", values[2]);
              addGlobalMeta(key + " intensity (Y)", values[3]);
              break;
            case 6:
              addGlobalMeta(key + " ratio (X)", values[2]);
              addGlobalMeta(key + " ratio (Y)", values[3]);
              break;
            case 7:
              addGlobalMeta(key + " log ratio (X)", values[2]);
              addGlobalMeta(key + " log ratio (Y)", values[3]);
              break;
            case 9:
              addGlobalMeta(key + " noncalibrated intensity min", values[2]);
              addGlobalMeta(key + " noncalibrated intensity max", values[3]);
              addGlobalMeta(key + " calibrated intensity min", values[4]);
              addGlobalMeta(key + " calibrated intensity max", values[5]);
              break;
            case 11:
              addGlobalMeta(key + " RGB type (X)", values[2]);
              addGlobalMeta(key + " RGB type (Y)", values[3]);

              // NB: This logic has not been tested, so it may be broken.
              if (key.equals("AXIS_4")) {
                // this is a single section multi-channel dataset
                core[0].sizeC = getImageCount();
                core[0].sizeZ = 1;
                core[0].sizeT = 1;
              }
              else if (key.equals("AXIS_9")) {
                multipleFiles = true;
                core[0].sizeC = (int) Double.parseDouble(values[3]);
              }
              break;
            case 14:
              addGlobalMeta(key + " time course type (X)", values[2]);
              addGlobalMeta(key + " time course type (Y)", values[3]);
              break;
            case 15:
              String prefix = " inverse sigmoid calibrated intensity ";
              addGlobalMeta(key + prefix + "(min)", values[2]);
              addGlobalMeta(key + prefix + "(max)", values[3]);
              addGlobalMeta(key + prefix + "(beta)", values[4]);
              addGlobalMeta(key + prefix + "(Kd)", values[5]);
              break;
            case 16:
              prefix = " log inverse sigmoid calibrated intensity ";
              addGlobalMeta(key + prefix + "(min)", values[2]);
              addGlobalMeta(key + prefix + "(max)", values[3]);
              addGlobalMeta(key + prefix + "(beta)", values[4]);
              addGlobalMeta(key + prefix + "(Kd)", values[5]);
              break;
          }
        }
      }
    }

    if (multipleFiles && isGroupFiles() && pics.size() == 0) {
      // do file grouping
      used.remove(currentId);
      long length = new Location(currentId).length();
      FilePattern pattern = new FilePattern(new Location(id).getAbsoluteFile());
      String[] patternFiles = pattern.getFiles();
      for (String file : patternFiles) {
        Location f = new Location(file);
        if (f.length() == length) {
          pics.add(file);
          used.add(file);
        }
      }
      if (pics.size() == 1) core[0].sizeC = 1;
    }

    picFiles = pics.toArray(new String[pics.size()]);
    Arrays.sort(picFiles);
    if (picFiles.length > 0) {
      if (getSizeC() == 0) core[0].sizeC = 1;
      core[0].imageCount = npic * picFiles.length;
      if (multipleFiles) {
        core[0].sizeT = getImageCount() / (getSizeZ() * getSizeC());
      }
      else core[0].sizeC = getImageCount() / (getSizeZ() * getSizeT());
    }
    else picFiles = null;

    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setImageName(name, 0);

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    // link Objective to Image using ObjectiveSettings
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);

    store.setObjectiveLensNA(new Float(lens), 0, 0);
    store.setObjectiveNominalMagnification(new Integer((int) magFactor), 0, 0);
    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    // link Detector to Image
    for (int i=0; i<getEffectiveSizeC(); i++) {
      Float detectorOffset = i < offset.size() ? offset.get(i) : null;
      Float detectorGain = i < gain.size() ? gain.get(i) : null;

      if (detectorOffset != null || detectorGain != null) {
        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorSettingsDetector(detectorID, 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorType("Unknown", 0, i);
      }
      if (detectorOffset != null) {
        store.setDetectorSettingsOffset(detectorOffset, 0, i);
      }
      if (detectorGain != null) {
        store.setDetectorSettingsGain(detectorGain, 0, i);
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
        addGlobalMeta("Timestamp " + count, stamp);
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
