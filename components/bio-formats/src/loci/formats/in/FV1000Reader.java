//
// FV1000Reader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.POITools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;

/**
 * FV1000Reader is the file format reader for Fluoview FV 1000 OIB and
 * Fluoview FV 1000 OIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FV1000Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FV1000Reader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class FV1000Reader extends FormatReader {

  // -- Constants --

  public static final String FV1000_MAGIC_STRING_1 = "FileInformation";
  public static final String FV1000_MAGIC_STRING_2 = "Acquisition Parameters";

  public static final String[] OIB_SUFFIX = {"oib"};
  public static final String[] OIF_SUFFIX = {"oif"};
  public static final String[] FV1000_SUFFIXES = {"oib", "oif"};

  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private static final int NUM_DIMENSIONS = 9;

  /** ROI types. */
  private static final int POINT = 2;
  private static final int LINE = 3;
  private static final int POLYLINE = 4;
  private static final int RECTANGLE = 5;
  private static final int CIRCLE = 6;
  private static final int ELLIPSE = 7;
  private static final int POLYGON = 8;
  private static final int FREE_SHAPE = 9;
  private static final int FREE_LINE = 10;
  private static final int GRID = 11;
  private static final int ARROW = 12;
  private static final int COLOR_BAR = 13;
  private static final int SCALE = 15;

  // -- Fields --

  /** Names of every TIFF file to open. */
  private Vector<String> tiffs;

  /** Name of thumbnail file. */
  private String thumbId;

  /** Helper reader for thumbnail. */
  private BMPReader thumbReader;

  /** Used file list. */
  private Vector<String> usedFiles;

  /** Flag indicating this is an OIB dataset. */
  private boolean isOIB;

  /** File mappings for OIB file. */
  private Hashtable<String, String> oibMapping;

  private String[] code, size, pixelSize;
  private int imageDepth;
  private Vector<String> previewNames;

  private String pixelSizeX, pixelSizeY;
  private Vector<String> channelNames, illuminations, dyeNames;
  private Vector<Integer> emWaves, exWaves, wavelengths;
  private String gain, offset, voltage, pinholeSize;
  private String magnification, lensNA, objectiveName, workingDistance;
  private String creationDate;

  private POITools poi;

  private short[][][] lut;
  private int lastChannel;

  // -- Constructor --

  /** Constructs a new FV1000 reader. */
  public FV1000Reader() {
    super("Olympus FV1000", new String[] {"oib", "oif", "pty", "lut"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, FV1000_SUFFIXES)) return true;

    if (!open) return false; // not allowed to touch the file system

    try {
      Location parent = new Location(name).getAbsoluteFile().getParentFile();
      String path = parent.getPath();
      path = path.substring(path.lastIndexOf(File.separator) + 1);
      if (path.indexOf(".") != -1) {
        path = path.substring(0, path.lastIndexOf("."));
      }

      Location oif = new Location(parent.getParentFile(), path);
      return oif.exists() && !oif.isDirectory();
    }
    catch (NullPointerException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1024;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String s = DataTools.stripString(stream.readString(blockLen));
    return s.indexOf(FV1000_MAGIC_STRING_1) >= 0 ||
      s.indexOf(FV1000_MAGIC_STRING_2) >= 0;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    String name = id.toLowerCase();
    if (name.endsWith(".oib") || name.endsWith(".oif")) {
      return FormatTools.CANNOT_GROUP;
    }
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut == null ? null : lut[lastChannel];
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int file = no;
    int image = 0;
    String filename = null;

    if (series == 0) {
      file = no / (getImageCount() / tiffs.size());
      image = no % (getImageCount() / tiffs.size());
      if (file < tiffs.size()) filename = tiffs.get(file);
    }
    else {
      file = no / (getImageCount() / previewNames.size());
      image = no % (getImageCount() / previewNames.size());
      if (file < previewNames.size()) {
        filename = previewNames.get(file);
      }
    }

    int[] coords = getZCTCoords(image);
    lastChannel = coords[1];

    if (filename == null) return buf;

    RandomAccessInputStream plane = getFile(filename);
    TiffParser tp = new TiffParser(plane);
    IFDList ifds = tp.getIFDs();
    if (image >= ifds.size()) return buf;

    IFD ifd = ifds.get(image);
    if (getSizeY() != ifd.getImageLength()) {
      tp.getSamples(ifd, buf, x,
        getIndex(coords[0], 0, coords[2]), w, 1);
    }
    else tp.getSamples(ifd, buf, x, y, w, h);

    plane.close();
    plane = null;
    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      if (isOIB) return null;
      Vector<String> files = new Vector<String>();
      for (int i=0; i<usedFiles.size(); i++) {
        String f = usedFiles.get(i).toLowerCase();
        if (!f.endsWith(".tif") && !f.endsWith(".tiff") && !f.endsWith(".bmp"))
        {
          files.add(usedFiles.get(i));
        }
      }
      return files.toArray(new String[0]);
    }
    if (usedFiles == null) return new String[] {currentId};
    return usedFiles.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (thumbReader != null) thumbReader.close(fileOnly);

    if (!fileOnly) {
      tiffs = usedFiles = null;
      thumbReader = null;
      thumbId = null;
      isOIB = false;
      previewNames = null;
      if (poi != null) poi.close();
      poi = null;
      lastChannel = 0;
      dyeNames = null;
      wavelengths = null;
      illuminations = null;
      oibMapping = null;
      code = size = pixelSize = null;
      imageDepth = 0;
      pixelSizeX = pixelSizeY = null;
      channelNames = null;
      emWaves = exWaves = null;
      gain = offset = voltage = pinholeSize = null;
      magnification = lensNA = objectiveName = workingDistance = null;
      creationDate = null;
      lut = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("FV1000Reader.initFile(" + id + ")");

    super.initFile(id);

    isOIB = checkSuffix(id, OIB_SUFFIX);

    in = new RandomAccessInputStream(id);
    if (isOIB) poi = new POITools(Location.getMappedId(id));

    // mappedOIF is used to distinguish between datasets that are being read
    // directly (e.g. using ImageJ or showinf), and datasets that are being
    // imported through omebf. In the latter case, the necessary directory
    // structure is not preserved (only relative file names are stored in
    // OMEIS), so we will need to use slightly different logic to build the
    // list of associated files.
    boolean mappedOIF = !isOIB && !new File(id).getAbsoluteFile().exists();

    channelNames = new Vector<String>();
    emWaves = new Vector<Integer>();
    exWaves = new Vector<Integer>();
    dyeNames = new Vector<String>();
    wavelengths = new Vector<Integer>();
    illuminations = new Vector<String>();

    String line = null, key = null, value = null, oifName = null;

    if (isOIB) {
      String infoFile = null;
      Vector list = poi.getDocumentList();
      for (int i=0; i<list.size(); i++) {
        String name = (String) list.get(i);
        if (name.endsWith("OibInfo.txt")) {
          infoFile = name;
          break;
        }
      }
      if (infoFile == null) {
        throw new FormatException("OibInfo.txt not found in " + id);
      }
      RandomAccessInputStream ras = poi.getDocumentStream(infoFile);

      oibMapping = new Hashtable<String, String>();

      // set up file name mappings

      String s = DataTools.stripString(ras.readString((int) ras.length()));
      ras.close();
      StringTokenizer lines = new StringTokenizer(s, "\n");
      String directoryKey = null, directoryValue = null;
      while (lines.hasMoreTokens()) {
        line = lines.nextToken().trim();
        if (line.indexOf("=") != -1) {
          key = line.substring(0, line.indexOf("="));
          value = line.substring(line.indexOf("=") + 1);

          if (directoryKey != null && directoryValue != null) {
            value = value.replaceAll(directoryKey, directoryValue);
          }

          if (value.indexOf("GST") != -1) {
            String first = value.substring(0, value.indexOf("GST"));
            String last = value.substring(value.lastIndexOf("=") + 1);
            value = first + last;
          }

          if (key.startsWith("Stream")) {
            if (checkSuffix(value, OIF_SUFFIX)) oifName = value;
            if (directoryKey != null) {
              oibMapping.put(value, "Root Entry" + File.separator +
                directoryKey + File.separator + key);
            }
            else oibMapping.put(value, "Root Entry" + File.separator + key);
          }
          else if (key.startsWith("Storage")) {
            directoryKey = key;
            directoryValue = value;
          }
        }
      }
      s = null;
    }
    else {
      // make sure we have the OIF file, not a TIFF
      if (!checkSuffix(id, OIF_SUFFIX)) {
        Location current = new Location(id).getAbsoluteFile();
        String parent = current.getParent();
        Location tmp = new Location(parent);
        parent = tmp.getParent();

        id = current.getName();
        String oifFile = parent + id.substring(0, id.lastIndexOf("_")) + ".oif";

        tmp = new Location(oifFile);
        if (!tmp.exists()) {
          oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
          tmp = new Location(oifFile);
          if (!tmp.exists()) {
            // check in parent directory
            if (parent.endsWith(File.separator)) {
              parent = parent.substring(0, parent.length() - 1);
            }
            String dir = parent.substring(parent.lastIndexOf(File.separator));
            tmp = new Location(parent);
            parent = tmp.getParent();
            oifFile = parent + dir.substring(0, dir.lastIndexOf("."));
            if (!new Location(oifFile).exists()) {
              throw new FormatException("OIF file not found");
            }
          }
          currentId = oifFile;
        }
        else currentId = oifFile;
        super.initFile(currentId);
        in = new RandomAccessInputStream(currentId);
      }
      oifName = currentId;
    }

    String f = new Location(oifName).getAbsoluteFile().getAbsolutePath();
    String path = (isOIB || !f.endsWith(oifName) || mappedOIF) ? "" :
      f.substring(0, f.lastIndexOf(File.separator) + 1);

    RandomAccessInputStream oif = null;
    try {
      oif = getFile(oifName);
    }
    catch (IOException e) {
      oif = getFile(oifName.replaceAll(".oif", ".OIF"));
    }

    // parse key/value pairs from the OIF file

    String s = oif.readString((int) oif.length());
    oif.close();

    code = new String[NUM_DIMENSIONS];
    size = new String[NUM_DIMENSIONS];
    pixelSize = new String[NUM_DIMENSIONS];

    StringTokenizer st = new StringTokenizer(s, "\r\n");

    previewNames = new Vector<String>();
    boolean laserEnabled = true;

    String ptyStart = null, ptyEnd = null, ptyPattern = null;

    Vector<String> lutNames = new Vector<String>();
    Hashtable<Integer, String> filenames = new Hashtable<Integer, String>();
    Hashtable<Integer, String> roiFilenames = new Hashtable<Integer, String>();
    String prefix = "";
    while (st.hasMoreTokens()) {
      line = DataTools.stripString(st.nextToken().trim());
      if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
        key = line.substring(0, line.indexOf("=")).trim();
        value = line.substring(line.indexOf("=") + 1).trim();
        if (value.startsWith("\"")) {
          value = value.substring(1, value.length() - 1);
        }

        value = value.replaceAll("/", File.separator);
        value = value.replace('\\', File.separatorChar);
        while (value.indexOf("GST") != -1) {
          String first = value.substring(0, value.indexOf("GST"));
          int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
            value.length() : value.indexOf(File.separator);
          String last = value.substring(value.lastIndexOf("=", ndx) + 1);
          value = first + last;
        }

        if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1 &&
          !isPreviewName(value))
        {
          if (mappedOIF) {
            value = value.substring(value.lastIndexOf(File.separator) + 1);
          }
          filenames.put(new Integer(key.substring(11)), value.trim());
        }
        else if (key.startsWith("RoiFileName") && key.indexOf("Thumb") == -1 &&
          !isPreviewName(value))
        {
          if (mappedOIF) {
            value = value.substring(value.lastIndexOf(File.separator) + 1);
          }
          try {
            roiFilenames.put(new Integer(key.substring(11)), value.trim());
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("PtyFileNameS")) ptyStart = value;
        else if (key.equals("PtyFileNameE")) ptyEnd = value;
        else if (key.equals("PtyFileNameT2")) ptyPattern = value;
        else if (key.indexOf("Thumb") != -1) {
          if (mappedOIF) {
            value = value.substring(value.lastIndexOf(File.separator) + 1);
          }
          if (thumbId == null) thumbId = value.trim();
        }
        else if (key.startsWith("LutFileName")) {
          if (mappedOIF) {
            value = value.substring(value.lastIndexOf(File.separator) + 1);
          }
          lutNames.add(path + value);
        }
        else if (isPreviewName(value)) {
          if (mappedOIF) {
            value = value.substring(value.lastIndexOf(File.separator) + 1);
          }
          previewNames.add(path + value.trim());
        }
        addGlobalMeta(prefix + key, value);

        if (prefix.startsWith("[Axis ") &&
          prefix.endsWith("Parameters Common] - "))
        {
          int ndx =
            Integer.parseInt(prefix.substring(6, prefix.indexOf("P")).trim());
          if (key.equals("AxisCode")) code[ndx] = value;
          else if (key.equals("MaxSize")) size[ndx] = value;
          else if (key.equals("Interval")) pixelSize[ndx] = value;
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
          else if (key.equals("DyeName")) dyeNames.add(value);
          else if (key.equals("EmissionWavelength")) {
            emWaves.add(new Integer(value));
          }
          else if (key.equals("ExcitationWavelength")) {
            exWaves.add(new Integer(value));
          }
          else if (key.equals("LightType")) {
            String illumination = value.toLowerCase();
            if (illumination.indexOf("fluorescence") != -1) {
              illumination = "Epifluorescence";
            }
            else if (illumination.indexOf("transmitted") != -1) {
              illumination = "Transmitted";
            }
            else illumination = null;
            illuminations.add(illumination);
          }
        }
        else if (prefix.startsWith("[Laser ") && key.equals("Laser Enable")) {
          laserEnabled = value.equals("1");
        }
        else if (prefix.startsWith("[Laser ") && key.equals("LaserWavelength"))
        {
          if (laserEnabled) wavelengths.add(new Integer(value));
        }
        else if (key.equals("ImageCaputreDate") ||
          key.equals("ImageCaptureDate"))
        {
          creationDate = value;
        }
      }
      else if (line.length() > 0) {
        if (line.indexOf("[") == 2) {
          line = line.substring(2, line.length());
        }
        prefix = line + " - ";
      }
    }

    // if this is a version 2 file, the .pty files have not yet been
    // added to the file list
    if (ptyStart != null && ptyEnd != null && ptyPattern != null) {
      // FV1000 version 2 gives the first .pty file, the last .pty and
      // the file name pattern.  Version 1 lists each .pty file individually.

      // pattern is typically 's_C%03dT%03d.pty'

      // build list of block indexes

      String[] prefixes = ptyPattern.split("%03d");

      // get first and last numbers for each block
      int[] first = new int[prefixes.length - 1];
      int[] last = new int[prefixes.length - 1];
      int[] lengths = new int[prefixes.length - 1];
      int index = 0;
      int totalFiles = 1;
      for (int i=0; i<first.length; i++) {
        index += prefixes[i].length();
        first[i] = Integer.parseInt(ptyStart.substring(index, index + 3));
        last[i] = Integer.parseInt(ptyEnd.substring(index, index + 3));
        lengths[i] = last[i] - first[i] + 1;
        totalFiles *= lengths[i];
        index += 3;
      }

      // add each .pty file

      for (int file=0; file<totalFiles; file++) {
        int[] pos = FormatTools.rasterToPosition(lengths, file);
        StringBuffer pty = new StringBuffer();
        for (int block=0; block<prefixes.length; block++) {
          pty.append(prefixes[block]);

          if (block < pos.length) {
            String num = String.valueOf(pos[block] + 1);
            for (int q=0; q<3 - num.length(); q++) {
              pty.append("0");
            }
            pty.append(num);
          }
        }
        filenames.put(new Integer(file), pty.toString());
      }
    }

    status("Initializing helper readers");

    // populate core metadata for preview series

    if (previewNames.size() > 0) {
      Vector<String> v = new Vector<String>();
      for (int i=0; i<previewNames.size(); i++) {
        String ss = previewNames.get(i);
        ss = replaceExtension(ss, "pty", "tif");
        if (ss.endsWith(".tif")) v.add(ss);
      }
      previewNames = v;
      if (previewNames.size() > 0) {
        core = new CoreMetadata[2];
        core[0] = new CoreMetadata();
        core[1] = new CoreMetadata();
        IFDList ifds = null;
        for (int i=0; i<previewNames.size(); i++) {
          String previewName = previewNames.get(i);
          RandomAccessInputStream preview = getFile(previewName);
          TiffParser tp = new TiffParser(preview);
          ifds = tp.getIFDs();
          preview.close();
          core[1].imageCount += ifds.size();
        }
        core[1].sizeX = (int) ifds.get(0).getImageWidth();
        core[1].sizeY = (int) ifds.get(0).getImageLength();
        core[1].sizeZ = 1;
        core[1].sizeT = 1;
        core[1].sizeC = core[1].imageCount;
        core[1].rgb = false;
        int bits = ifds.get(0).getBitsPerSample()[0];
        while ((bits % 8) != 0) bits++;
        switch (bits) {
          case 8:
            core[1].pixelType = FormatTools.UINT8;
            break;
          case 16:
            core[1].pixelType = FormatTools.UINT16;
            break;
          case 32:
            core[1].pixelType = FormatTools.UINT32;
        }
        core[1].dimensionOrder = "XYCZT";
        core[1].indexed = false;
      }
    }

    core[0].imageCount = filenames.size();
    tiffs = new Vector<String>(getImageCount());

    thumbReader = new BMPReader();
    thumbId = replaceExtension(thumbId, "pty", "bmp");
    thumbId = sanitizeFile(thumbId, (isOIB || mappedOIF) ? "" : path);

    status("Reading additional metadata");

    // open each INI file (.pty extension) and build list of TIFF files

    String tiffPath = null;

    core[0].dimensionOrder = "XY";

    for (int i=0, ii=0; ii<getImageCount(); i++, ii++) {
      String file = filenames.get(new Integer(i));
      while (file == null) file = filenames.get(new Integer(++i));
      file = sanitizeFile(file, (isOIB || mappedOIF) ? "" : path);

      if (file.indexOf(File.separator) != -1) {
        tiffPath = file.substring(0, file.lastIndexOf(File.separator));
      }
      else tiffPath = file;

      Location ptyFile = new Location(file);
      if (!isOIB && !ptyFile.exists()) {
        warn("Could not find .pty file (" + file + "); guessing at the " +
          "corresponding TIFF file.");
        String tiff = replaceExtension(file, ".pty", ".tif");
        tiffs.add(ii, tiff);
        continue;
      }

      RandomAccessInputStream ptyReader = getFile(file);
      s = ptyReader.readString((int) ptyReader.length());
      ptyReader.close();
      st = new StringTokenizer(s, "\n");

      boolean zAxis = false, cAxis = false, tAxis = false;

      while (st.hasMoreTokens()) {
        line = st.nextToken().trim();
        line = DataTools.stripString(line);

        if (line.equals("[Axis 2 Parameters]")) cAxis = true;
        else if (line.equals("[Axis 3 Parameters]")) zAxis = true;
        else if (line.equals("[Axis 4 Parameters]")) tAxis = true;

        if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
          key = line.substring(0, line.indexOf("=")).trim();
          value = line.substring(line.indexOf("=") + 1).trim();
          value = value.replaceAll("\"", "");
          if (key.equals("DataName")) {
            if (!isPreviewName(value)) {
              value = value.replaceAll("/", File.separator);
              value = value.replace('\\', File.separatorChar);
              while (value.indexOf("GST") != -1) {
                String first = value.substring(0, value.indexOf("GST"));
                int ndx = value.indexOf(File.separator) < value.indexOf("GST") ?
                  value.length() : value.indexOf(File.separator);
                String last = value.substring(value.lastIndexOf("=", ndx) + 1);
                value = first + last;
              }
              if (mappedOIF) tiffs.add(ii, value);
              else tiffs.add(ii, tiffPath + File.separator + value);
            }
          }
          else if (key.equals("Number")) {
            boolean addAxis = Integer.parseInt(value) > 1;
            if (zAxis) {
              if (addAxis && getDimensionOrder().indexOf("Z") == -1) {
                core[0].dimensionOrder += "Z";
              }
              zAxis = false;
            }
            if (cAxis) {
              if (addAxis && getDimensionOrder().indexOf("C") == -1) {
                core[0].dimensionOrder += "C";
              }
              cAxis = false;
            }
            if (tAxis) {
              if (addAxis && getDimensionOrder().indexOf("T") == -1) {
                core[0].dimensionOrder += "T";
              }
              tAxis = false;
            }
          }

          addGlobalMeta("Image " + ii + " : " + key, value);

          if (key.equals("AnalogPMTGain") || key.equals("CountingPMTGain")) {
            gain = value;
          }
          else if (key.equals("AnalogPMTOffset") ||
            key.equals("CountingPMTOffset"))
          {
            offset = value;
          }
          else if (key.equals("Magnification")) {
            magnification = value;
          }
          else if (key.equals("ObjectiveLens NAValue")) {
            lensNA = value;
          }
          else if (key.equals("ObjectiveLens Name")) {
            objectiveName = value;
          }
          else if (key.equals("ObjectiveLens WDValue")) {
            workingDistance = value;
          }
          else if (key.equals("PMTVoltage")) {
            voltage = value;
          }
          else if (key.equals("PinholeDiameter")) {
            pinholeSize = value;
          }
        }
      }
    }

    if (tiffs.size() != getImageCount()) {
      core[0].imageCount = tiffs.size();
    }

    usedFiles = new Vector<String>();

    if (tiffPath != null) {
      usedFiles.add(id);
      if (!isOIB) {
        Location dir = new Location(tiffPath);
        String[] list = mappedOIF ?
          Location.getIdMap().keySet().toArray(new String[0]) : dir.list();
        for (int i=0; i<list.length; i++) {
          if (mappedOIF) usedFiles.add(list[i]);
          else {
            String p = new Location(tiffPath, list[i]).getAbsolutePath();
            String check = p.toLowerCase();
            if (!check.endsWith(".tif") && !check.endsWith(".pty") &&
              !check.endsWith(".roi") && !check.endsWith(".lut") &&
              !check.endsWith(".bmp"))
            {
              continue;
            }
            usedFiles.add(p);
          }
        }
      }
    }

    status("Populating metadata");

    // calculate axis sizes

    float sizeZ = 1f, sizeT = 1f;

    int realChannels = 0;
    for (int i=0; i<9; i++) {
      int ss = Integer.parseInt(size[i]);
      if (pixelSize[i] == null) pixelSize[i] = "1.0";
      pixelSize[i] = pixelSize[i].replaceAll("\"", "");
      Float pixel = new Float(pixelSize[i]);
      if (code[i].equals("X")) core[0].sizeX = ss;
      else if (code[i].equals("Y")) core[0].sizeY = ss;
      else if (code[i].equals("Z")) {
        core[0].sizeZ = ss;
        // Z size stored in nm
        sizeZ = (float) (pixel.floatValue() * 0.001);
      }
      else if (code[i].equals("T")) {
        core[0].sizeT = ss;
        sizeT = pixel.floatValue() / 1000;
      }
      else if (ss > 0) {
        if (getSizeC() == 0) core[0].sizeC = ss;
        else core[0].sizeC *= ss;
        if (code[i].equals("C")) realChannels = ss;
      }
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    if (getImageCount() == getSizeC() && getSizeY() == 1) {
      core[0].imageCount *= getSizeZ() * getSizeT();
    }
    else if (getImageCount() == getSizeC()) {
      core[0].sizeZ = 1;
      core[0].sizeT = 1;
    }

    if (getSizeZ() * getSizeT() * getSizeC() != getImageCount()) {
      int diff = (getSizeZ() * getSizeC() * getSizeT()) - getImageCount();
      if (diff == previewNames.size() || diff < 0) {
        diff /= getSizeC();
        if (getSizeT() > 1 && getSizeZ() == 1) core[0].sizeT -= diff;
        else if (getSizeZ() > 1 && getSizeT() == 1) core[0].sizeZ -= diff;
      }
      else core[0].imageCount += diff;
    }

    if (getDimensionOrder().indexOf("C") == -1) core[0].dimensionOrder += "C";
    if (getDimensionOrder().indexOf("Z") == -1) core[0].dimensionOrder += "Z";
    if (getDimensionOrder().indexOf("T") == -1) core[0].dimensionOrder += "T";

    switch (imageDepth) {
      case 1:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 2:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 4:
        core[0].pixelType = FormatTools.UINT32;
        break;
      default:
        throw new RuntimeException("Unsupported pixel depth: " + imageDepth);
    }

    in.close();
    in = null;

    // set up thumbnail file mapping

    try {
      RandomAccessInputStream thumb = getFile(thumbId);
      byte[] b = new byte[(int) thumb.length()];
      thumb.read(b);
      thumb.close();
      Location.mapFile("thumbnail.bmp", new ByteArrayHandle(b));
      thumbReader.setId("thumbnail.bmp");
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].thumbSizeX = thumbReader.getSizeX();
        core[i].thumbSizeY = thumbReader.getSizeY();
      }
      Location.mapFile("thumbnail.bmp", null);
    }
    catch (IOException e) {
      traceDebug(e);
    }

    // initialize lookup table

    lut = new short[getSizeC()][3][65536];
    byte[] buffer = new byte[65536 * 4];
    int count = (int) Math.min(getSizeC(), lutNames.size());
    for (int c=0; c<count; c++) {
      try {
        RandomAccessInputStream stream = getFile(lutNames.get(c));
        stream.seek(stream.length() - 65536 * 4);
        stream.read(buffer);
        stream.close();
        for (int q=0; q<buffer.length; q+=4) {
          lut[c][0][q / 4] = buffer[q + 1];
          lut[c][1][q / 4] = buffer[q + 2];
          lut[c][2][q / 4] = buffer[q + 3];
        }
      }
      catch (IOException e) {
        traceDebug(e);
        lut = null;
        break;
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      core[i].rgb = false;
      core[i].littleEndian = true;
      core[i].interleaved = false;
      core[i].metadataComplete = true;
      core[i].indexed = false;
      core[i].falseColor = false;
    }

    // populate MetadataStore

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    MetadataTools.populatePixels(store, this);

    if (creationDate != null) {
      creationDate = creationDate.replaceAll("'", "");
      creationDate = DateTools.formatDate(creationDate, DATE_FORMAT);
    }

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      // populate Image data
      store.setImageName("Series " + (i + 1), i);
      if (creationDate != null) store.setImageCreationDate(creationDate, i);
      else MetadataTools.setDefaultCreationDate(store, id, i);

      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);

      // populate Dimensions data

      if (pixelSizeX != null) {
        store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), i, 0);
      }
      if (pixelSizeY != null) {
        store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), i, 0);
      }
      store.setDimensionsPhysicalSizeZ(new Float(sizeZ), i, 0);
      store.setDimensionsTimeIncrement(new Float(sizeT), i, 0);

      // populate LogicalChannel data

      for (int c=0; c<core[i].sizeC; c++) {
        if (c < channelNames.size()) {
          store.setLogicalChannelName(channelNames.get(c), i, c);
        }
        if (c < emWaves.size()) {
          store.setLogicalChannelEmWave(emWaves.get(c), i, c);
        }
        if (c < exWaves.size()) {
          store.setLogicalChannelExWave(exWaves.get(c), i, c);
        }
        if (c < illuminations.size()) {
          store.setLogicalChannelIlluminationType(illuminations.get(c), i, c);
        }
      }
    }

    // populate Laser data

    int nLasers = (int) Math.min(dyeNames.size(), wavelengths.size());
    for (int i=0; i<nLasers; i++) {
      // link LightSource to Image
      String lightSourceID = MetadataTools.createLSID("LightSource", 0, i);
      store.setLightSourceID(lightSourceID, 0, i);
      store.setLightSourceSettingsLightSource(lightSourceID, 0, i);
      if (i < exWaves.size()) {
        store.setLightSourceSettingsWavelength(exWaves.get(i), 0, i);
      }
      store.setLaserLaserMedium(dyeNames.get(i), 0, i);
      store.setLaserWavelength(wavelengths.get(i), 0, i);
    }

    // populate Detector data

    if (gain != null) store.setDetectorGain(new Float(gain), 0, 0);
    if (offset != null) store.setDetectorOffset(new Float(offset), 0, 0);
    if (voltage != null) store.setDetectorVoltage(new Float(voltage), 0, 0);
    store.setDetectorType("Unknown", 0, 0);

    // link Detector to Image using DetectorSettings
    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorSettingsDetector(detectorID, 0, 0);

    // populate Objective data

    if (lensNA != null) store.setObjectiveLensNA(new Float(lensNA), 0, 0);
    store.setObjectiveModel(objectiveName, 0, 0);
    if (magnification != null) {
      store.setObjectiveNominalMagnification(
        new Integer((int) Float.parseFloat(magnification)), 0, 0);
    }
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(new Float(workingDistance), 0, 0);
    }
    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    // link Objective to Image using ObjectiveSettings
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);

    int nextROI = -1;

    // populate ROI data - there is one ROI file per plane
    for (int i=0; i<roiFilenames.size(); i++) {
      if (i >= getImageCount()) break;
      int[] coordinates = getZCTCoords(i);
      String filename = roiFilenames.get(new Integer(i));
      filename = sanitizeFile(filename, (isOIB || mappedOIF) ? "" : path);

      RandomAccessInputStream stream = getFile(filename);
      String data = stream.readString((int) stream.length());
      stream.close();

      String[] lines = data.split("\n");

      boolean validROI = false;
      int nextShape = -1;
      int shapeType = -1;

      String[] xc = null, yc = null;
      int divide = 0;
      int color = 0, fontSize = 0, lineWidth = 0, angle = 0;
      String fontName = null, name = null;

      for (int q=0; q<lines.length; q++) {
        lines[q] = DataTools.stripString(lines[q]);

        int eq = lines[q].indexOf("=");
        if (eq == -1) continue;
        key = lines[q].substring(0, eq).trim();
        value = lines[q].substring(eq + 1).trim();

        if (key.equals("Name")) {
          value = value.replaceAll("\"", "");
          try {
            validROI = Integer.parseInt(value) > 1;
          }
          catch (NumberFormatException e) { validROI = false; }
        }
        if (!validROI) continue;

        if (key.equals("SHAPE")) {
          shapeType = Integer.parseInt(value);
        }
        else if (key.equals("DIVIDE")) {
          divide = Integer.parseInt(value);
        }
        else if (key.equals("FONT")) {
          String[] fontAttributes = value.split(",");
          fontName = fontAttributes[0];
          fontSize = Integer.parseInt(fontAttributes[1]);
        }
        else if (key.equals("LINEWIDTH")) {
          lineWidth = Integer.parseInt(value);
        }
        else if (key.equals("FORECOLOR")) {
          color = Integer.parseInt(value);
        }
        else if (key.equals("NAME")) {
          name = value;
        }
        else if (key.equals("ANGLE")) {
          angle = Integer.parseInt(value);
        }
        else if (key.equals("X")) {
          xc = value.split(",");
        }
        else if (key.equals("Y")) {
          yc = value.split(",");

          int x = Integer.parseInt(xc[0]);
          int width = xc.length > 1 ? Integer.parseInt(xc[1]) - x : 0;
          int y = Integer.parseInt(yc[0]);
          int height = yc.length > 1 ? Integer.parseInt(yc[1]) - y : 0;

          if (width + x <= getSizeX() && height + y <= getSizeY()) {
            nextShape++;
            if (nextShape == 0) {
              nextROI++;
              store.setROIZ0(new Integer(coordinates[0]), 0, nextROI);
              store.setROIZ1(new Integer(coordinates[0]), 0, nextROI);
              store.setROIT0(new Integer(coordinates[2]), 0, nextROI);
              store.setROIT1(new Integer(coordinates[2]), 0, nextROI);
            }

            store.setShapeTheZ(new Integer(coordinates[0]), 0,
              nextROI, nextShape);
            store.setShapeTheT(new Integer(coordinates[2]), 0,
              nextROI, nextShape);

            store.setShapeFontSize(new Integer(fontSize), 0, nextROI,
              nextShape);
            store.setShapeFontFamily(fontName, 0, nextROI, nextShape);
            store.setShapeText(name, 0, nextROI, nextShape);
            store.setShapeStrokeWidth(new Integer(lineWidth), 0, nextROI,
              nextShape);
            store.setShapeStrokeColor(String.valueOf(color), 0, nextROI,
              nextShape);

            if (shapeType == POINT) {
              store.setPointCx(xc[0], 0, nextROI, nextShape);
              store.setPointCy(yc[0], 0, nextROI, nextShape);
            }
            else if (shapeType == RECTANGLE) {
              store.setRectX(String.valueOf(x), 0, nextROI, nextShape);
              store.setRectWidth(String.valueOf(width), 0, nextROI, nextShape);
              store.setRectY(String.valueOf(y), 0, nextROI, nextShape);
              store.setRectHeight(String.valueOf(height), 0, nextROI,
                nextShape);

              int centerX = x + (width / 2);
              int centerY = y + (height / 2);
              store.setRectTransform("rotate(" + angle + " " + centerX + " " +
                centerY + ")", 0, nextROI, nextShape);
            }
            else if (shapeType == GRID) {
              width /= divide;
              height /= divide;
              for (int row=0; row<divide; row++) {
                for (int col=0; col<divide; col++) {
                  store.setRectX(String.valueOf(x + col*width), 0, nextROI,
                    nextShape);
                  store.setRectY(String.valueOf(y + row*height), 0, nextROI,
                    nextShape);
                  store.setRectWidth(String.valueOf(width), 0, nextROI,
                    nextShape);
                  store.setRectHeight(String.valueOf(height), 0, nextROI,
                    nextShape);

                  int centerX = x + col * width + (width / 2);
                  int centerY = y + row * height + (height / 2);

                  store.setRectTransform("rotate(" + angle + " " + centerX +
                    " " + centerY + ")", 0, nextROI, nextShape);

                  if (row < divide - 1 || col < divide - 1) nextShape++;
                }
              }
            }
            else if (shapeType == LINE) {
              store.setLineX1(String.valueOf(x), 0, nextROI, nextShape);
              store.setLineY1(String.valueOf(y), 0, nextROI, nextShape);
              store.setLineX2(String.valueOf(x + width), 0, nextROI, nextShape);
              store.setLineY2(String.valueOf(y + height), 0, nextROI,
                nextShape);

              int centerX = x + (width / 2);
              int centerY = y + (height / 2);

              store.setLineTransform("rotate(" + angle + " " + centerX + " " +
                centerY + ")", 0, nextROI, nextShape);
            }
            else if (shapeType == CIRCLE) {
              int r = width / 2;
              store.setCircleCx(String.valueOf(x + r), 0, nextROI, nextShape);
              store.setCircleCy(String.valueOf(y + r), 0, nextROI, nextShape);
              store.setCircleR(String.valueOf(r), 0, nextROI, nextShape);
            }
            else if (shapeType == ELLIPSE) {
              int rx = width / 2;
              int ry = height / 2;
              store.setEllipseCx(String.valueOf(x + rx), 0, nextROI, nextShape);
              store.setEllipseCy(String.valueOf(y + ry), 0, nextROI, nextShape);
              store.setEllipseRx(String.valueOf(rx), 0, nextROI, nextShape);
              store.setEllipseRy(String.valueOf(ry), 0, nextROI, nextShape);
              store.setEllipseTransform("rotate(" + angle + " " + (x + rx) +
                " " + (y + ry) + ")", 0, nextROI, nextShape);
            }
            else if (shapeType == POLYGON || shapeType == FREE_SHAPE) {
              StringBuffer points = new StringBuffer();
              for (int point=0; point<xc.length; point++) {
                points.append(xc[point]);
                points.append(",");
                points.append(yc[point]);
                if (point < xc.length - 1) points.append(" ");
              }
              store.setPolygonPoints(points.toString(), 0, nextROI, nextShape);
              store.setPolygonTransform("rotate(" + angle + ")", 0, nextROI,
                nextShape);
            }
            else if (shapeType == POLYLINE || shapeType == FREE_LINE) {
              StringBuffer points = new StringBuffer();
              for (int point=0; point<xc.length; point++) {
                points.append(xc[point]);
                points.append(",");
                points.append(yc[point]);
                if (point < xc.length - 1) points.append(" ");
              }
              store.setPolylinePoints(points.toString(), 0, nextROI, nextShape);
              store.setPolylineTransform("rotate(" + angle + ")", 0, nextROI,
                nextShape);
            }
          }
        }
      }
    }
  }

  // -- Helper methods --

  private String sanitizeFile(String file, String path) {
    String f = file;
    f = f.replaceAll("\"", "");
    f = f.replace('\\', File.separatorChar);
    f = f.replace('/', File.separatorChar);
    if (!isOIB && path.equals("")) return f;
    return path + File.separator + f;
  }

  private RandomAccessInputStream getFile(String name)
    throws FormatException, IOException
  {
    if (isOIB) {
      if (name.startsWith("/") || name.startsWith("\\")) {
        name = name.substring(1);
      }
      name = name.replace('\\', '/');
      return poi.getDocumentStream(oibMapping.get(name));
    }
    else return new RandomAccessInputStream(name);
  }

  private boolean isPreviewName(String name) {
    // "-R" in the file name indicates that this is a preview image
    int index = name.indexOf("-R");
    return index == name.length() - 9;
  }

  private String replaceExtension(String name, String oldExt, String newExt) {
    if (!name.endsWith("." + oldExt)) {
      return name;
    }
    return name.substring(0, name.length() - oldExt.length()) + newExt;
  }

}
