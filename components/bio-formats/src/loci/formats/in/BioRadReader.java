/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.IMinMaxStore;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * BioRadReader is the file format reader for Bio-Rad PIC files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/BioRadReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/BioRadReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
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

  /** Structure labels. */
  public static final String[] STRUCTURE_LABELS_1 = {
    "Scan Channel", "Both mode", "Speed", "Filter", "Factor", "Number of scans",
    "Photon counting mode (channel 1)", "Photon counting detector (channel 1)",
    "Photon counting mode (channel 2)", "Photon counting detector (channel 2)",
    "Photon mode", "Objective magnification", "Zoom factor", "Motor on",
    "Z Step Size"
  };

  public static final String[] STRUCTURE_LABELS_2 = {
    "Z Start", "Z Stop", "Scan area X coordinate", "Scan area Y coordinate",
    "Scan area width", "Scan area height"
  };

  public static final String[] STRUCTURE_LABELS_3 = {
    "Iris for PMT", "Gain for PMT", "Black level for PMT",
    "Emission filter for PMT", "Multiplier for channel"
  };

  public static final String[] STRUCTURE_LABELS_4 = {
    "enhanced", "PMT 1 percentage", "PMT 2 percentage",
    "Transmission 1 percentage", "Transmission 2 percentage",
    "Transmission 3 percentage"
  };

  public static final String[] STRUCTURE_LABELS_5 = {
    "laser ", "excitation filter for laser ", "ND filter for laser ",
    "emission filter for laser "
  };

  public static final String[] STRUCTURE_LABELS_6 = {
    "Part number for laser 3", "Part number for excitation filter for laser 3",
    "Part number for ND filter for laser 3",
    "Part number for emission filter for laser 3",
    "Part number for filter block 1", "Part number for filter block 2"
  };

  public static final String[] PIC_SUFFIX = {"pic"};

  public static final int LUT_LENGTH = 256;

  // -- Fields --

  private Vector<String> used;

  private String[] picFiles;

  private byte[][][] lut;
  private int lastChannel = 0;
  private boolean brokenNotes = false;

  private Vector<Note> noteStrings;

  private Vector<Double> offset, gain;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() {
    super("Bio-Rad PIC", new String[] {"pic", "xml", "raw"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One or more .pic files and an optional lse.xml file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

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
    Location thisFile = new Location(id).getAbsoluteFile();
    Location parent = thisFile.getParentFile();
    String[] list = parent.list(true);
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
      brokenNotes = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    // always initialize a PIC file, even if we were given something else
    if (!checkSuffix(id, PIC_SUFFIX)) {
      Location dir = new Location(id).getAbsoluteFile().getParentFile();

      String[] list = dir.list(true);
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

    offset = new Vector<Double>();
    gain = new Vector<Double>();

    used = new Vector<String>();
    used.add(currentId);

    LOGGER.info("Reading image dimensions");

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

    float magFactor = 1f;
    int lens = 0;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      int merged = in.readShort();
      int color1 = in.readShort();
      int fileId = in.readShort();
      int ramp2min = in.readShort();
      int ramp2max = in.readShort();
      int color2 = in.readShort();
      int edited = in.readShort();
      lens = in.readShort();
      magFactor = in.readFloat();

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
    }
    else in.skipBytes(20);

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

    LOGGER.info("Reading notes");

    String zoom = null, zstart = null, zstop = null, mag = null;
    String gain1 = null, gain2 = null, gain3 = null;
    String offset1 = null;
    String ex1 = null, ex2 = null, ex3 = null;
    String em1 = null, em2 = null, em3 = null;

    MetadataStore store = makeFilterMetadata();

    // read notes

    readNotes(in, true);

    LOGGER.info("Populating metadata");

    // look for companion metadata files

    Vector<String> pics = new Vector<String>();

    if (isGroupFiles()) {
      Location parent =
        new Location(currentId).getAbsoluteFile().getParentFile();
      String parentPath = parent.getAbsolutePath();
      String[] list = parent.list(true);
      Arrays.sort(list);

      for (int i=0; i<list.length; i++) {
        if (list[i].endsWith("lse.xml")) {
          String path = new Location(parentPath, list[i]).getAbsolutePath();
          used.add(path);

          DefaultHandler handler = new BioRadHandler();
          RandomAccessInputStream xml = new RandomAccessInputStream(path);
          XMLTools.parseXML(xml, handler);
          xml.close();

          used.remove(currentId);
          for (int q=0; q<list.length; q++) {
            if (checkSuffix(list[q], PIC_SUFFIX)) {
              path = new Location(parentPath, list[q]).getAbsolutePath();
              pics.add(path);
              if (!used.contains(path)) used.add(path);
            }
          }
        }
        else if (list[i].endsWith("data.raw")) {
          used.add(new Location(parentPath, list[i]).getAbsolutePath());
        }
      }
    }

    // populate Pixels

    core[0].dimensionOrder = "XYCTZ";

    boolean multipleFiles = parseNotes(store);

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

    if (getEffectiveSizeC() != getSizeC() && !isRGB()) {
      core[0].sizeC = 1;
    }

    LOGGER.info("Reading lookup tables");

    lut = new byte[getEffectiveSizeC()][][];
    for (int channel=0; channel<lut.length; channel++) {
      int plane = getIndex(0, channel, 0);
      String file =
        picFiles == null ? currentId : picFiles[plane % picFiles.length];
      LOGGER.trace("reading table for C = {} from {}", channel, file);
      RandomAccessInputStream s = new RandomAccessInputStream(file);
      s.order(true);
      readLookupTables(s);
      s.close();
      if (lut == null) break;
    }
    core[0].indexed = lut != null;

    MetadataTools.populatePixels(store, this);
    store.setImageName(name, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      // link Objective to Image using ObjectiveSettings
      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);

      store.setObjectiveLensNA(new Double(lens), 0, 0);
      if ((int) magFactor > 0) {
        store.setObjectiveNominalMagnification(
          new PositiveInteger((int) magFactor), 0, 0);
      }
      else {
        LOGGER.warn("Expected positive value for NominalMagnification; got {}",
          magFactor);
      }
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

      // link Detector to Image
      for (int i=0; i<getEffectiveSizeC(); i++) {
        Double detectorOffset = i < offset.size() ? offset.get(i) : null;
        Double detectorGain = i < gain.size() ? gain.get(i) : null;

        if (detectorOffset != null || detectorGain != null) {
          String detectorID = MetadataTools.createLSID("Detector", 0, i);
          store.setDetectorSettingsID(detectorID, 0, i);
          store.setDetectorID(detectorID, 0, i);
          store.setDetectorType(getDetectorType("Other"), 0, i);
        }
        if (detectorOffset != null) {
          store.setDetectorSettingsOffset(detectorOffset, 0, i);
        }
        if (detectorGain != null) {
          store.setDetectorSettingsGain(detectorGain, 0, i);
        }
      }
    }
  }

  // -- Helper methods --

  /**
   * Read all of the note strings from the given file.  If the 'add' flag is
   * set, the notes will be added to the 'noteStrings' list.
   */
  private void readNotes(RandomAccessInputStream s, boolean add)
    throws IOException
  {
    s.seek(70);
    int imageLen = getSizeX() * getSizeY();
    if (picFiles == null) imageLen *= getImageCount();
    else {
      imageLen *= (getImageCount() / picFiles.length);
    }
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    s.skipBytes(bpp * imageLen + 6);

    boolean notes = true;
    while (notes) {
      // read in note

      if (s.getFilePointer() >= s.length()) {
        brokenNotes = true;
        break;
      }

      Note n = new Note();
      n.level = s.readShort();
      notes = s.readInt() != 0;
      n.num = s.readShort();
      n.status = s.readShort();
      n.type = s.readShort();
      n.x = s.readShort();
      n.y = s.readShort();
      n.p = s.readString(80);

      if (n.type < 0 || n.type >= NOTE_NAMES.length) {
        notes = false;
        brokenNotes = true;
        break;
      }

      if (!add) continue;

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
      noteStrings.add(n);
    }
  }

  private boolean parseNotes(MetadataStore store) throws FormatException {
    boolean multipleFiles = false;
    int nextDetector = 0, nLasers = 0;
    for (int noteIndex=0; noteIndex<noteStrings.size(); noteIndex++) {
      Note n = noteStrings.get(noteIndex);
      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        switch (n.type) {
          case NOTE_TYPE_USER:
            // TODO : this should be an overlay
            addGlobalMeta("Note #" + noteIndex, n.toString());
            break;
          case NOTE_TYPE_SCALEBAR:
            // TODO : this should be an overlay
            // the format of the text is:
            // SCALEBAR = <length> <angle>
            // where <length> is the length of the scalebar in microns,
            // and <angle> is the angle in degrees
            addGlobalMeta("Note #" + noteIndex, n.toString());
            break;
          case NOTE_TYPE_ARROW:
            // TODO : this should be an overlay
            // the format of the text is:
            // ARROW = <lx> <ly> <angle> <fill>
            // where <lx> and <ly> define the arrow's bounding box,
            // <angle> is the angle in degrees and <fill> is either "Fill" or
            // "Outline"
            addGlobalMeta("Note #" + noteIndex, n.toString());
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
                int mag = (int) Float.parseFloat(value);
                if (mag > 0) {
                  store.setObjectiveNominalMagnification(
                    new PositiveInteger(mag), 0, 0);
                }
                else {
                  LOGGER.warn(
                    "Expected positive value for NominalMagnification; got {}",
                    mag);
                }
              }
              else if (key.equals("LENS_MAGNIFICATION")) {
                int magnification = (int) Float.parseFloat(value);
                if (magnification > 0) {
                  store.setObjectiveNominalMagnification(
                    new PositiveInteger(magnification), 0, 0);
                }
                else {
                  LOGGER.warn(
                    "Expected positive value for NominalMagnification; got {}",
                    magnification);
                }
              }
              else if (key.startsWith("SETTING")) {
                if (key.indexOf("_DET_") != -1) {
                  int index = key.indexOf("_DET_") + 5;
                  if (key.lastIndexOf("_") > index) {
                    String detectorID =
                      MetadataTools.createLSID("Detector", 0, nextDetector);
                    store.setDetectorID(detectorID, 0, nextDetector);
                    store.setDetectorType(
                      getDetectorType("Other"), 0, nextDetector);

                    if (key.endsWith("OFFSET")) {
                      if (nextDetector < offset.size()) {
                        offset.setElementAt(new Double(value), nextDetector);
                      }
                      else {
                        while (nextDetector > offset.size()) {
                          offset.add(null);
                        }
                        offset.add(new Double(value));
                      }
                    }
                    else if (key.endsWith("GAIN")) {
                      if (nextDetector < gain.size()) {
                        gain.setElementAt(new Double(value), nextDetector);
                      }
                      else {
                        while (nextDetector > gain.size()) {
                          gain.add(null);
                        }
                        gain.add(new Double(value));
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
                      Double pixelSize = new Double(values[2]);
                      if (pixelSize > 0) {
                        if (key.equals("AXIS_2")) {
                          store.setPixelsPhysicalSizeX(
                            new PositiveFloat(pixelSize), 0);
                        }
                        else if (key.equals("AXIS_3")) {
                          store.setPixelsPhysicalSizeY(
                            new PositiveFloat(pixelSize), 0);
                        }
                      }
                      else {
                        LOGGER.warn(
                          "Expected positive value for PhysicalSize; got {}",
                          pixelSize);
                      }
                    }
                  }
                  catch (NumberFormatException e) { }
                }
              }
            }
            else if (n.p.startsWith("AXIS_2")) {
              String[] values = n.p.split(" ");
              Double pixelSize = new Double(values[3]);
              if (pixelSize > 0) {
                store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSize), 0);
              }
              else {
                LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
                  pixelSize);
              }
            }
            else if (n.p.startsWith("AXIS_3")) {
              String[] values = n.p.split(" ");
              Double pixelSize = new Double(values[3]);
              if (pixelSize > 0) {
                store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSize), 0);
              }
              else {
                LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
                  pixelSize);
              }
            }
            else {
              addGlobalMeta("Note #" + noteIndex, n.toString());
            }
            break;
          case NOTE_TYPE_STRUCTURE:
            int structureType = (n.x & 0xff00) >> 8;
            int version = (n.x & 0xff);
            String[] values = n.p.split(" ");
            if (structureType == 1) {
              switch (n.y) {
                case 1:
                  for (int i=0; i<STRUCTURE_LABELS_1.length; i++) {
                    addGlobalMeta(STRUCTURE_LABELS_1[i], values[i]);
                  }

                  int mag = (int) Float.parseFloat(values[11]);
                  if (mag > 0) {
                    store.setObjectiveNominalMagnification(
                      new PositiveInteger(mag), 0, 0);
                  }
                  else {
                    LOGGER.warn("Expected positive value for " +
                      "NominalMagnification; got {}", mag);
                  }

                  Double sizeZ = new Double(values[14]);
                  if (sizeZ > 0) {
                    store.setPixelsPhysicalSizeZ(new PositiveFloat(sizeZ), 0);
                  }
                  else {
                    LOGGER.warn(
                      "Expected positive value for PhysicalSizeZ; got {}",
                      sizeZ);
                  }
                  break;
                case 2:
                  for (int i=0; i<STRUCTURE_LABELS_2.length; i++) {
                    addGlobalMeta(STRUCTURE_LABELS_2[i], values[i]);
                  }

                  double x1 = Double.parseDouble(values[2]);
                  double x2 = Double.parseDouble(values[4]);
                  double width = x2 - x1;
                  width /= getSizeX();

                  double y1 = Double.parseDouble(values[3]);
                  double y2 = Double.parseDouble(values[5]);
                  double height = y2 - y1;
                  height /= getSizeY();

                  if (width > 0) {
                    store.setPixelsPhysicalSizeX(new PositiveFloat(width), 0);
                  }
                  else {
                    LOGGER.warn(
                      "Expected positive value for PhysicalSizeX; got {}",
                      width);
                  }
                  if (height > 0) {
                    store.setPixelsPhysicalSizeY(new PositiveFloat(height), 0);
                  }
                  else {
                    LOGGER.warn(
                      "Expected positive value for PhysicalSizeY; got {}",
                      height);
                  }

                  break;
                case 3:
                  for (int i=0; i<3; i++) {
                    for (int j=0; j<STRUCTURE_LABELS_3.length; j++) {
                      String v = j == STRUCTURE_LABELS_3.length - 1 ?
                        values[12 + i] : values[i * 4 + j];
                      addGlobalMeta(STRUCTURE_LABELS_3[j] + " " + (i + 1), v);
                    }
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
                  String prefix = "Excitation filter name - laser ";
                  for (int i=0; i<nLasers; i++) {
                    addGlobalMeta(prefix + (i + 1), values[i]);
                  }
                  break;
                case 6:
                  prefix = "Emission filter name - laser ";
                  for (int i=0; i<nLasers; i++) {
                    addGlobalMeta(prefix + (i + 1), values[i]);
                  }
                  break;
                case 7:
                  for (int i=0; i<2; i++) {
                    prefix = "Mixer " + i + " - ";
                    for (int j=0; j<STRUCTURE_LABELS_4.length; j++) {
                      addGlobalMeta(prefix + STRUCTURE_LABELS_4[j],
                        values[i * 7 + j]);
                    }
                  }
                  addGlobalMeta("Mixer 0 - low signal on", values[14]);
                  addGlobalMeta("Mixer 1 - low signal on", values[15]);
                  break;
                case 8:
                case 9:
                case 10:
                  addGlobalMeta("Laser name - laser " + (n.y - 7), values[0]);
                  break;
                case 11:
                  for (int i=0; i<3; i++) {
                    prefix = "Transmission detector " + (i + 1) + " - ";
                    addGlobalMeta(prefix + "offset", values[i * 3]);
                    addGlobalMeta(prefix + "gain", values[i * 3 + 1]);
                    addGlobalMeta(prefix + "black level", values[i * 3 + 2]);

                    String detectorID =
                      MetadataTools.createLSID("Detector", 0, i);
                    store.setDetectorID(detectorID, 0, i);
                    store.setDetectorOffset(new Double(values[i * 3]), 0, i);
                    store.setDetectorGain(new Double(values[i * 3 + 1]), 0, i);
                    store.setDetectorType(getDetectorType("Other"), 0, i);
                  }
                  break;
                case 12:
                  for (int i=0; i<2; i++) {
                    prefix = "Part number for ";
                    for (int j=0; j<STRUCTURE_LABELS_5.length; j++) {
                      addGlobalMeta(prefix + STRUCTURE_LABELS_5[j] + (i + 1),
                        values[i * 4 + j]);
                    }
                  }
                  break;
                case 13:
                  for (int i=0; i<STRUCTURE_LABELS_6.length; i++) {
                    addGlobalMeta(STRUCTURE_LABELS_6[i], values[i]);
                  }
                  break;
                case 14:
                  prefix = "Filter Block Name - filter block ";
                  addGlobalMeta(prefix + "1", values[0]);
                  addGlobalMeta(prefix + "2", values[1]);
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
                  store.setImageAcquisitionDate(new Timestamp(date), 0);
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
                    prefix = "Mixer " + i + " - ";
                    String photon = prefix + "photon counting ";
                    addGlobalMeta(photon + "1", values[i * 4 - 4]);
                    addGlobalMeta(photon + "2", values[i * 4 - 3]);
                    addGlobalMeta(photon + "3", values[i * 4 - 2]);
                    addGlobalMeta(prefix + "mode", values[i * 4 - 1]);
                  }
                  break;
                case 20:
                  addGlobalMeta("Display mode", values[0]);
                  addGlobalMeta("Course", values[1]);
                  addGlobalMeta("Time Course - experiment type", values[2]);
                  addGlobalMeta("Time Course - kd factor", values[3]);
                  String experimentID =
                    MetadataTools.createLSID("Experiment", 0);
                  store.setExperimentID(experimentID, 0);
                  store.setExperimentType(getExperimentType(values[2]), 0);
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
                      values[i + 3]);
                    addGlobalMeta("Tx Selector used (TX " + i + ")",
                      values[i + 6]);
                  }
                  break;
              }
            }
            break;
          default:
            // notes for display only
            addGlobalMeta("Note #" + noteIndex, n.toString());
        }
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

        if (axisType == 11 && values.length > 2) {
          addGlobalMeta(key + " RGB type (X)", values[2]);
          addGlobalMeta(key + " RGB type (Y)", values[3]);

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
        }

        if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM &&
          values.length > 2)
        {
          switch (axisType) {
            case 1:
              addGlobalMeta(key + " distance (X) in microns", values[2]);
              addGlobalMeta(key + " distance (Y) in microns", values[3]);
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
    return multipleFiles;
  }

  /**
   * Read all lookup tables from the given file into the 'lut' variable.
   */
  private void readLookupTables(RandomAccessInputStream s) throws IOException {
    int channel = 0;
    while (channel < lut.length && lut[channel] != null) channel++;
    if (channel >= lut.length) return;

    readNotes(s, false);

    // read color tables
    boolean eof = false;
    int next = 0;
    while (!eof && channel < lut.length && !brokenNotes) {
      if (s.getFilePointer() + LUT_LENGTH <= s.length()) {
        if (lut[channel] == null) {
          lut[channel] = new byte[3][LUT_LENGTH];
        }

        s.read(lut[channel][next++]);
        if (next == 3) {
          next = 0;
          channel++;
        }
      }
      else eof = true;
      if (eof && channel == 0) {
        lut = null;
      }
    }
    if (brokenNotes) lut = null;
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  class BioRadHandler extends BaseHandler {
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
