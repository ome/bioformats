//
// LeicaReader.java
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.AxisGuesser;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffConstants;
import loci.formats.tiff.TiffParser;

/**
 * LeicaReader is the file format reader for Leica files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/LeicaReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/LeicaReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaReader extends FormatReader {

  // -- Constants -

  public static final String[] LEI_SUFFIX = {"lei"};

  /** All Leica TIFFs have this tag. */
  private static final int LEICA_MAGIC_TAG = 33923;

  /** Format for dates. */
  private static final String DATE_FORMAT = "yyyy:MM:dd,HH:mm:ss:SSS";

  /** IFD tags. */
  private static final Integer SERIES = new Integer(10);
  private static final Integer IMAGES = new Integer(15);
  private static final Integer DIMDESCR = new Integer(20);
  private static final Integer FILTERSET = new Integer(30);
  private static final Integer TIMEINFO = new Integer(40);
  private static final Integer SCANNERSET = new Integer(50);
  private static final Integer EXPERIMENT = new Integer(60);
  private static final Integer LUTDESC = new Integer(70);
  private static final Integer CHANDESC = new Integer(80);
  private static final Integer SEQUENTIALSET = new Integer(90);
  private static final Integer SEQ_SCANNERSET = new Integer(200);
  private static final Integer SEQ_FILTERSET = new Integer(700);

  private static final int SEQ_SCANNERSET_END = 300;
  private static final int SEQ_FILTERSET_END = 800;

  private static final Hashtable<String, Integer> CHANNEL_PRIORITIES =
    createChannelPriorities();

  private static Hashtable<String, Integer> createChannelPriorities() {
    Hashtable<String, Integer> h = new Hashtable<String, Integer>();

    h.put("red", new Integer(0));
    h.put("green", new Integer(1));
    h.put("blue", new Integer(2));
    h.put("cyan", new Integer(3));
    h.put("magenta", new Integer(4));
    h.put("yellow", new Integer(5));
    h.put("black", new Integer(6));
    h.put("gray", new Integer(7));
    h.put("", new Integer(8));

    return h;
  }

  // -- Static fields --

  private static Hashtable<Integer, String> dimensionNames =
    makeDimensionTable();

  // -- Fields --

  protected IFDList ifds;

  /** Array of IFD-like structures containing metadata. */
  protected IFDList headerIFDs;

  /** Helper readers. */
  protected MinimalTiffReader tiff;

  /** Array of image file names. */
  protected Vector[] files;

  /** Number of series in the file. */
  private int numSeries;

  /** Name of current LEI file */
  private String leiFilename;

  private Vector<String> seriesNames;
  private Vector<String> seriesDescriptions;
  private int lastPlane = 0;

  private float[][] physicalSizes;
  private float[] pinhole, exposureTime;

  private int nextDetector = 0, nextChannel = 0;
  private Vector<Integer> activeChannelIndices = new Vector<Integer>();
  private boolean sequential = false;

  // -- Constructor --

  /** Constructs a new Leica reader. */
  public LeicaReader() {
    super("Leica", new String[] {"lei", "tif", "tiff"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, LEI_SUFFIX)) return true;
    if (!checkSuffix(name, TiffReader.TIFF_SUFFIXES)) return false;

    if (!open) return false; // not allowed to touch the file system

    // check for that there is an .lei file in the same directory
    String prefix = name;
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }
    Location lei = new Location(prefix + ".lei");
    if (!lei.exists()) {
      lei = new Location(prefix + ".LEI");
      while (!lei.exists() && prefix.indexOf("_") != -1) {
        prefix = prefix.substring(0, prefix.lastIndexOf("_"));
        lei = new Location(prefix + ".lei");
        if (!lei.exists()) lei = new Location(prefix + ".LEI");
      }
    }
    return lei.exists();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(new Integer(LEICA_MAGIC_TAG));
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    try {
      tiff.setId((String) files[series].get(lastPlane));
      return tiff.get8BitLookupTable();
    }
    catch (IOException e) {
      traceDebug(e);
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    try {
      tiff.setId((String) files[series].get(lastPlane));
      return tiff.get16BitLookupTable();
    }
    catch (IOException e) {
      traceDebug(e);
    }
    return null;
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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastPlane = no;
    if (no < files[series].size()) {
      String filename = (String) files[series].get(no);
      if (new Location(filename).exists()) {
        tiff.setId(filename);
        return tiff.openBytes(0, buf, x, y, w, h);
      }
    }
    // imitate Leica's software and return a blank plane if the
    // appropriate TIFF file is missing
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> v = new Vector<String>();
    v.add(leiFilename);
    if (!noPixels) {
      v.addAll(files[getSeries()]);
    }
    return v.toArray(new String[v.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiff != null) tiff.close(fileOnly);
    if (!fileOnly) {
      leiFilename = null;
      files = null;
      ifds = headerIFDs = null;
      tiff = null;
      seriesNames = null;
      numSeries = 0;
      lastPlane = 0;
      physicalSizes = null;
      seriesDescriptions = null;
      pinhole = exposureTime = null;
      nextDetector = 0;
      nextChannel = 0;
      sequential = false;
      activeChannelIndices.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("LeicaReader.initFile(" + id + ")");
    close();

    if (checkSuffix(id, TiffReader.TIFF_SUFFIXES)) {
      // need to find the associated .lei file
      if (ifds == null) super.initFile(id);

      in = new RandomAccessInputStream(id);
      TiffParser tp = new TiffParser(in);
      in.order(tp.checkHeader().booleanValue());

      in.seek(0);

      status("Finding companion file name");

      // open the TIFF file and look for the "Image Description" field

      ifds = tp.getIFDs();
      if (ifds == null) throw new FormatException("No IFDs found");
      String descr = ifds.get(0).getComment();

      // remove anything of the form "[blah]"

      descr = descr.replaceAll("\\[.*.\\]\n", "");

      // each remaining line in descr is a (key, value) pair,
      // where '=' separates the key from the value

      String lei = id.substring(0, id.lastIndexOf(File.separator) + 1);

      StringTokenizer lines = new StringTokenizer(descr, "\n");
      String line = null, key = null, value = null;
      while (lines.hasMoreTokens()) {
        line = lines.nextToken();
        if (line.indexOf("=") == -1) continue;
        key = line.substring(0, line.indexOf("=")).trim();
        value = line.substring(line.indexOf("=") + 1).trim();
        addGlobalMeta(key, value);

        if (key.startsWith("Series Name")) lei += value;
      }

      // now open the LEI file

      Location l = new Location(lei).getAbsoluteFile();
      if (l.exists()) {
        initFile(lei);
        return;
      }
      else {
        l = l.getParentFile();
        String[] list = l.list();
        for (int i=0; i<list.length; i++) {
          if (checkSuffix(list[i], LEI_SUFFIX)) {
            initFile(
              new Location(l.getAbsolutePath(), list[i]).getAbsolutePath());
            return;
          }
        }
      }
      throw new FormatException("LEI file not found.");
    }

    // parse the LEI file

    super.initFile(id);

    leiFilename = new File(id).exists() ?
      new Location(id).getAbsolutePath() : id;
    in = new RandomAccessInputStream(id);

    seriesNames = new Vector<String>();

    byte[] fourBytes = new byte[4];
    in.read(fourBytes);
    core[0].littleEndian = (fourBytes[0] == TiffConstants.LITTLE &&
      fourBytes[1] == TiffConstants.LITTLE &&
      fourBytes[2] == TiffConstants.LITTLE &&
      fourBytes[3] == TiffConstants.LITTLE);

    in.order(isLittleEndian());

    status("Reading metadata blocks");

    in.skipBytes(8);
    int addr = in.readInt();

    headerIFDs = new IFDList();

    while (addr != 0) {
      IFD ifd = new IFD();
      headerIFDs.add(ifd);
      in.seek(addr + 4);

      int tag = in.readInt();

      while (tag != 0) {
        // create the IFD structure
        int offset = in.readInt();

        long pos = in.getFilePointer();
        in.seek(offset + 12);

        int size = in.readInt();
        byte[] data = new byte[size];
        in.read(data);
        ifd.put(new Integer(tag), data);
        in.seek(pos);
        tag = in.readInt();
      }

      addr = in.readInt();
    }

    numSeries = headerIFDs.size();

    core = new CoreMetadata[numSeries];
    for (int i=0; i<numSeries; i++) {
      core[i] = new CoreMetadata();
    }

    files = new Vector[numSeries];

    // determine the length of a filename

    int nameLength = 0;
    int maxPlanes = 0;

    status("Parsing metadata blocks");

    core[0].littleEndian = !isLittleEndian();

    int seriesIndex = 0;
    boolean[] valid = new boolean[numSeries];
    for (int i=0; i<headerIFDs.size(); i++) {
      IFD ifd = headerIFDs.get(i);
      valid[i] = true;
      if (ifd.get(SERIES) != null) {
        byte[] temp = (byte[]) ifd.get(SERIES);
        nameLength = DataTools.bytesToInt(temp, 8, isLittleEndian()) * 2;
      }

      Vector<String> f = new Vector<String>();
      byte[] tempData = (byte[]) ifd.get(IMAGES);
      RandomAccessInputStream data = new RandomAccessInputStream(tempData);
      data.order(isLittleEndian());
      int tempImages = data.readInt();

      if (((long) tempImages * nameLength) > data.length()) {
        data.order(!isLittleEndian());
        tempImages = data.readInt();
        data.order(isLittleEndian());
      }

      core[i].sizeX = data.readInt();
      core[i].sizeY = data.readInt();
      data.skipBytes(4);
      int samplesPerPixel = data.readInt();
      core[i].rgb = samplesPerPixel > 1;
      core[i].sizeC = samplesPerPixel;

      File dirFile = new File(id).getAbsoluteFile();
      String[] listing = null;
      String dirPrefix = "";
      if (dirFile.exists()) {
        listing = dirFile.getParentFile().list();
        dirPrefix = dirFile.getParent();
        if (!dirPrefix.endsWith(File.separator)) dirPrefix += File.separator;
      }
      else {
        listing =
          (String[]) Location.getIdMap().keySet().toArray(new String[0]);
      }

      Vector<String> list = new Vector<String>();

      for (int k=0; k<listing.length; k++) {
        if (checkSuffix(listing[k], TiffReader.TIFF_SUFFIXES)) {
          list.add(listing[k]);
        }
      }

      boolean tiffsExist = false;

      String prefix = "";
      for (int j=0; j<tempImages; j++) {
        // read in each filename
        prefix = getString(data, nameLength);
        f.add(dirPrefix + prefix);
        // test to make sure the path is valid
        Location test = new Location(f.get(f.size() - 1));
        if (test.exists()) list.remove(prefix);
        if (!tiffsExist) tiffsExist = test.exists();
      }
      data.close();
      tempData = null;

      // all of the TIFF files were renamed

      if (!tiffsExist) {
        // Strategy for handling renamed files:
        // 1) Assume that files for each series follow a pattern.
        // 2) Assign each file group to the first series with the correct count.
        status("Handling renamed TIFF files");

        listing = list.toArray(new String[list.size()]);

        // grab the file patterns
        Vector<String> filePatterns = new Vector<String>();
        for (String q : listing) {
          Location l = new Location(dirPrefix, q).getAbsoluteFile();
          FilePattern pattern = new FilePattern(l);

          AxisGuesser guess = new AxisGuesser(pattern, "XYZCT", 1, 1, 1, false);
          String fp = pattern.getPattern();

          if (guess.getAxisCountS() >= 1) {
            String pre = pattern.getPrefix(guess.getAxisCountS());
            Vector<String> fileList = new Vector<String>();
            for (int n=0; n<listing.length; n++) {
              Location p = new Location(dirPrefix, listing[n]);
              if (p.getAbsolutePath().startsWith(pre)) {
                fileList.add(listing[n]);
              }
            }
            fp = FilePattern.findPattern(l.getAbsolutePath(), dirPrefix,
              fileList.toArray(new String[fileList.size()]));
          }

          if (fp != null && !filePatterns.contains(fp)) {
            filePatterns.add(fp);
          }
        }

        for (String q : filePatterns) {
          String[] pattern = new FilePattern(q).getFiles();
          if (pattern.length == tempImages) {
            // make sure that this pattern hasn't already been used

            boolean validPattern = true;
            for (int n=0; n<i; n++) {
              if (files[n] == null) continue;
              if (files[n].contains(pattern[0])) {
                validPattern = false;
                break;
              }
            }

            if (validPattern) {
              files[i] = new Vector<String>();
              files[i].addAll(Arrays.asList(pattern));
            }
          }
        }
      }
      else files[i] = f;
      if (files[i] == null) valid[i] = false;
      else {
        core[i].imageCount = files[i].size();
        if (core[i].imageCount > maxPlanes) maxPlanes = core[i].imageCount;
      }
    }

    int invalidCount = 0;
    for (int i=0; i<valid.length; i++) {
      if (!valid[i]) invalidCount++;
    }

    numSeries -= invalidCount;

    int[] count = new int[getSeriesCount()];
    for (int i=0; i<getSeriesCount(); i++) {
      count[i] = core[i].imageCount;
    }

    Vector[] tempFiles = files;
    IFDList tempIFDs = headerIFDs;
    core = new CoreMetadata[numSeries];
    files = new Vector[numSeries];
    headerIFDs = new IFDList();
    int index = 0;

    for (int i=0; i<numSeries; i++) {
      core[i] = new CoreMetadata();
      while (!valid[index]) index++;
      core[i].imageCount = count[index];
      files[i] = tempFiles[index];
      Object[] sorted = files[i].toArray();
      Arrays.sort(sorted);
      files[i].clear();
      files[i].addAll(Arrays.asList(sorted));

      headerIFDs.add(tempIFDs.get(index));
      index++;
    }

    tiff = new MinimalTiffReader();

    status("Populating metadata");

    if (headerIFDs == null) headerIFDs = ifds;

    int fileLength = 0;

    int resolution = -1;
    String[][] timestamps = new String[headerIFDs.size()][];
    seriesDescriptions = new Vector<String>();

    physicalSizes = new float[headerIFDs.size()][5];
    pinhole = new float[headerIFDs.size()];
    exposureTime = new float[headerIFDs.size()];

    for (int i=0; i<headerIFDs.size(); i++) {
      IFD ifd = headerIFDs.get(i);

      core[i].littleEndian = isLittleEndian();
      setSeries(i);
      Object[] keys = ifd.keySet().toArray();
      Arrays.sort(keys);

      for (int q=0; q<keys.length; q++) {
        byte[] tmp = (byte[]) ifd.get(keys[q]);
        if (tmp == null) continue;
        RandomAccessInputStream stream = new RandomAccessInputStream(tmp);
        stream.order(isLittleEndian());

        if (keys[q].equals(SERIES)) {
          addSeriesMeta("Version", stream.readInt());
          addSeriesMeta("Number of Series", stream.readInt());
          fileLength = stream.readInt();
          addSeriesMeta("Length of filename", fileLength);
          int extLen = stream.readInt();
          if (extLen > fileLength) {
            stream.seek(8);
            core[0].littleEndian = !isLittleEndian();
            stream.order(isLittleEndian());
            fileLength = stream.readInt();
            extLen = stream.readInt();
          }
          addSeriesMeta("Length of file extension", extLen);
          addSeriesMeta("Image file extension", getString(stream, extLen));
        }
        else if (keys[q].equals(IMAGES)) {
          core[i].imageCount = stream.readInt();
          core[i].sizeX = stream.readInt();
          core[i].sizeY = stream.readInt();

          addSeriesMeta("Number of images", getImageCount());
          addSeriesMeta("Image width", getSizeX());
          addSeriesMeta("Image height", getSizeY());
          addSeriesMeta("Bits per Sample", stream.readInt());
          addSeriesMeta("Samples per pixel", stream.readInt());

          String name = getString(stream, fileLength * 2);

          if (name.indexOf(".") != -1) {
            name = name.substring(0, name.lastIndexOf("."));
          }

          String[] tokens = name.split("_");
          StringBuffer buf = new StringBuffer();
          for (int p=1; p<tokens.length; p++) {
            String lcase = tokens[p].toLowerCase();
            if (!lcase.startsWith("ch0") && !lcase.startsWith("c0") &&
              !lcase.startsWith("z0") && !lcase.startsWith("t0"))
            {
              if (buf.length() > 0) buf.append("_");
              buf.append(tokens[p]);
            }
          }
          seriesNames.add(buf.toString());
        }
        else if (keys[q].equals(DIMDESCR)) {
          addSeriesMeta("Voxel Version", stream.readInt());
          core[i].rgb = stream.readInt() == 20;
          addSeriesMeta("VoxelType", isRGB() ? "RGB" : "gray");

          int bpp = stream.readInt();
          addSeriesMeta("Bytes per pixel", bpp);

          switch (bpp) {
            case 1:
              core[i].pixelType = FormatTools.UINT8;
              break;
            case 3:
              core[i].pixelType = FormatTools.UINT8;
              core[i].sizeC = 3;
              core[i].rgb = true;
              break;
            case 2:
              core[i].pixelType = FormatTools.UINT16;
              break;
            case 6:
              core[i].pixelType = FormatTools.UINT16;
              core[i].sizeC = 3;
              core[i].rgb = true;
              break;
            case 4:
              core[i].pixelType = FormatTools.UINT32;
              break;
            default:
              throw new FormatException("Unsupported bytes per pixel (" +
                bpp + ")");
          }

          core[i].dimensionOrder = "XY";

          resolution = stream.readInt();
          addSeriesMeta("Real world resolution", resolution);
          addSeriesMeta("Maximum voxel intensity", getString(stream, true));
          addSeriesMeta("Minimum voxel intensity", getString(stream, true));
          int len = stream.readInt();
          stream.skipBytes(len * 2 + 4);

          len = stream.readInt();
          for (int j=0; j<len; j++) {
            int dimId = stream.readInt();
            String dimType = dimensionNames.get(new Integer(dimId));
            if (dimType == null) dimType = "";

            int size = stream.readInt();
            int distance = stream.readInt();
            int strlen = stream.readInt() * 2;
            String[] sizeData = getString(stream, strlen).split(" ");
            String physicalSize = sizeData[0];
            String unit = "";
            if (sizeData.length > 1) unit = sizeData[1];

            float physical = Float.parseFloat(physicalSize) / size;
            if (unit.equals("m")) {
              physical *= 1000000;
            }

            if (dimType.equals("x")) {
              core[i].sizeX = size;
              physicalSizes[i][0] = physical;
            }
            else if (dimType.equals("y")) {
              core[i].sizeY = size;
              physicalSizes[i][1] = physical;
            }
            else if (dimType.equals("channel")) {
              if (getSizeC() == 0) core[i].sizeC = 1;
              core[i].sizeC *= size;
              if (getDimensionOrder().indexOf("C") == -1) {
                core[i].dimensionOrder += "C";
              }
              physicalSizes[i][3] = physical;
            }
            else if (dimType.equals("z")) {
              core[i].sizeZ = size;
            }
            else {
              core[i].sizeT = size;
              if (getDimensionOrder().indexOf("T") == -1) {
                core[i].dimensionOrder += "T";
              }
              physicalSizes[i][4] = physical;
            }

            String dimPrefix = "Dim" + j;

            addSeriesMeta(dimPrefix + " type", dimType);
            addSeriesMeta(dimPrefix + " size", size);
            addSeriesMeta(dimPrefix + " distance between sub-dimensions",
              distance);

            addSeriesMeta(dimPrefix + " physical length",
              physicalSize + " " + unit);

            addSeriesMeta(dimPrefix + " physical origin",
              getString(stream, true));
          }
          addSeriesMeta("Series name", getString(stream, false));

          String description = getString(stream, false);
          seriesDescriptions.add(description);
          addSeriesMeta("Series description", description);
        }
        else if (keys[q].equals(TIMEINFO)) {
          int nDims = stream.readInt();
          addSeriesMeta("Number of time-stamped dimensions", nDims);
          addSeriesMeta("Time-stamped dimension", stream.readInt());

          for (int j=0; j<nDims; j++) {
            String dimPrefix = "Dimension " + j;
            addSeriesMeta(dimPrefix + " ID", stream.readInt());
            addSeriesMeta(dimPrefix + " size", stream.readInt());
            addSeriesMeta(dimPrefix + " distance", stream.readInt());
          }

          int numStamps = stream.readInt();
          addSeriesMeta("Number of time-stamps", numStamps);
          timestamps[i] = new String[numStamps];
          for (int j=0; j<numStamps; j++) {
            timestamps[i][j] = getString(stream, 64);
            addSeriesMeta("Timestamp " + j, timestamps[i][j]);
          }

          if (stream.getFilePointer() < stream.length()) {
            int numTMs = stream.readInt();
            addSeriesMeta("Number of time-markers", numTMs);
            for (int j=0; j<numTMs; j++) {
              int numDims = stream.readInt();

              String time = "Time-marker " + j + " Dimension ";

              for (int k=0; k<numDims; k++) {
                addSeriesMeta(time + k + " coordinate", stream.readInt());
              }
              addSeriesMeta("Time-marker " + j, getString(stream, 64));
            }
          }
        }
        else if (keys[q].equals(EXPERIMENT)) {
          stream.skipBytes(8);
          String description = getString(stream, true);
          addSeriesMeta("Image Description", description);
          addSeriesMeta("Main file extension", getString(stream, true));
          addSeriesMeta("Image format identifier", getString(stream, true));
          addSeriesMeta("Single image extension", getString(stream, true));
        }
        else if (keys[q].equals(LUTDESC)) {
          int nChannels = stream.readInt();
          if (nChannels > 0) core[i].indexed = true;
          addSeriesMeta("Number of LUT channels", nChannels);
          addSeriesMeta("ID of colored dimension", stream.readInt());

          for (int j=0; j<nChannels; j++) {
            String p = "LUT Channel " + j;
            addSeriesMeta(p + " version", stream.readInt());
            addSeriesMeta(p + " inverted?", stream.read() == 1);
            addSeriesMeta(p + " description", getString(stream, false));
            addSeriesMeta(p + " filename", getString(stream, false));
            String lut = getString(stream, false);
            addSeriesMeta(p + " name", lut);
            stream.skipBytes(8);
          }
        }
        else if (keys[q].equals(CHANDESC)) {
          int nBands = stream.readInt();
          for (int band=0; band<nBands; band++) {
            String p = "Band #" + (band + 1) + " ";
            addSeriesMeta(p + "Lower wavelength", stream.readDouble());
            stream.skipBytes(4);
            addSeriesMeta(p + "Higher wavelength", stream.readDouble());
            stream.skipBytes(4);
            addSeriesMeta(p + "Gain", stream.readDouble());
            addSeriesMeta(p + "Offset", stream.readDouble());
          }
        }

        stream.close();
      }

      core[i].orderCertain = true;
      core[i].littleEndian = isLittleEndian();
      core[i].falseColor = true;
      core[i].metadataComplete = true;
      core[i].interleaved = false;
    }

    for (int i=0; i<numSeries; i++) {
      setSeries(i);
      if (getSizeZ() == 0) core[i].sizeZ = 1;
      if (getSizeT() == 0) core[i].sizeT = 1;
      if (getSizeC() == 0) core[i].sizeC = 1;
      if (getImageCount() == 0) core[i].imageCount = 1;
      if (getImageCount() == 1 && getSizeZ() * getSizeT() > 1) {
        core[i].sizeZ = 1;
        core[i].sizeT = 1;
      }
      if (getSizeY() == 1) {
        // XZ or XT scan
        if (getSizeZ() > 1 && getImageCount() == getSizeC() * getSizeT()) {
          core[i].sizeY = getSizeZ();
          core[i].sizeZ = 1;
        }
        else if (getSizeT() > 1 && getImageCount() == getSizeC() * getSizeZ()) {
          core[i].sizeY = getSizeT();
          core[i].sizeT = 1;
        }
      }
      if (isRGB()) core[i].indexed = false;

      if (getDimensionOrder().indexOf("C") == -1) {
        core[i].dimensionOrder += "C";
      }
      if (getDimensionOrder().indexOf("Z") == -1) {
        core[i].dimensionOrder += "Z";
      }
      if (getDimensionOrder().indexOf("T") == -1) {
        core[i].dimensionOrder += "T";
      }
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<numSeries; i++) {
      IFD ifd = headerIFDs.get(i);
      long firstPlane = 0;

      if (i < timestamps.length && timestamps[i] != null &&
        timestamps[i].length > 0)
      {
        firstPlane = DateTools.getTime(timestamps[i][0], DATE_FORMAT);
        store.setImageCreationDate(
          DateTools.formatDate(timestamps[i][0], DATE_FORMAT), i);
      }
      else {
        MetadataTools.setDefaultCreationDate(store, id, i);
      }

      store.setImageName(seriesNames.get(i), i);
      store.setImageDescription(seriesDescriptions.get(i), i);

      String instrumentID = MetadataTools.createLSID("Instrument", i);
      store.setInstrumentID(instrumentID, i);

      // parse instrument data

      nextDetector = 0;
      nextChannel = 0;

      Object[] keys = ifd.keySet().toArray();
      Arrays.sort(keys);
      int nextInstrumentBlock = 1;
      sequential = DataTools.indexOf(keys, SEQ_SCANNERSET) != -1;
      for (int q=0; q<keys.length; q++) {
        if (keys[q].equals(FILTERSET) || keys[q].equals(SCANNERSET) ||
          keys[q].equals(SEQ_SCANNERSET) || keys[q].equals(SEQ_FILTERSET) ||
          (((Integer) keys[q]).intValue() > SEQ_SCANNERSET.intValue() &&
          ((Integer) keys[q]).intValue() < SEQ_SCANNERSET_END) ||
          (((Integer) keys[q]).intValue() > SEQ_FILTERSET.intValue() &&
          ((Integer) keys[q]).intValue() < SEQ_FILTERSET_END))
        {
          if (sequential && (keys[q].equals(FILTERSET) ||
            keys[q].equals(SCANNERSET)))
          {
            continue;
          }
          byte[] tmp = (byte[]) ifd.get(keys[q]);
          if (tmp == null) continue;
          RandomAccessInputStream stream = new RandomAccessInputStream(tmp);
          stream.order(isLittleEndian());
          parseInstrumentData(stream, store, i, nextInstrumentBlock++);
          stream.close();
        }
      }
      activeChannelIndices.clear();

      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);

      store.setDimensionsPhysicalSizeX(new Float(physicalSizes[i][0]), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(physicalSizes[i][1]), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(physicalSizes[i][2]), i, 0);
      if ((int) physicalSizes[i][3] > 0) {
        store.setDimensionsWaveIncrement(
          new Integer((int) physicalSizes[i][3]), i, 0);
      }
      if ((int) physicalSizes[i][4] > 0) {
        store.setDimensionsTimeIncrement(new Float(physicalSizes[i][4]), i, 0);
      }

      for (int j=0; j<core[i].imageCount; j++) {
        if (timestamps[i] != null && j < timestamps[i].length) {
          long time = DateTools.getTime(timestamps[i][j], DATE_FORMAT);
          float elapsedTime = (float) (time - firstPlane) / 1000;
          store.setPlaneTimingDeltaT(new Float(elapsedTime), i, 0, j);
          store.setPlaneTimingExposureTime(new Float(exposureTime[i]), i, 0, j);
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  private void parseInstrumentData(RandomAccessInputStream stream,
    MetadataStore store, int series, int blockNum) throws IOException
  {
    setSeries(series);

    // read 24 byte SAFEARRAY
    stream.skipBytes(4);
    int cbElements = stream.readInt();
    stream.skipBytes(8);
    int nElements = stream.readInt();
    stream.skipBytes(4);

    Vector[] channelNames = new Vector[getSeriesCount()];
    Vector[] emWaves = new Vector[getSeriesCount()];
    Vector[] exWaves = new Vector[getSeriesCount()];

    for (int i=0; i<getSeriesCount(); i++) {
      channelNames[i] = new Vector();
      emWaves[i] = new Vector();
      exWaves[i] = new Vector();
    }

    for (int j=0; j<nElements; j++) {
      stream.seek(24 + j * cbElements);
      String contentID = getString(stream, 128);
      String description = getString(stream, 64);
      String data = getString(stream, 64);
      int dataType = stream.readShort();
      stream.skipBytes(6);

      // read data
      switch (dataType) {
        case 2:
          data = String.valueOf(stream.readShort());
          break;
        case 3:
          data = String.valueOf(stream.readInt());
          break;
        case 4:
          data = String.valueOf(stream.readFloat());
          break;
        case 5:
          data = String.valueOf(stream.readDouble());
          break;
        case 7:
        case 11:
          data = stream.read() == 0 ? "false" : "true";
          break;
        case 17:
          data = stream.readString(1);
          break;
      }

      String[] tokens = contentID.split("\\|");

      if (tokens[0].startsWith("CDetectionUnit")) {
        // detector information

        if (tokens[1].startsWith("PMT")) {
          try {
            if (tokens[2].equals("VideoOffset")) {
              store.setDetectorOffset(new Float(data), series, nextDetector);
            }
            else if (tokens[2].equals("HighVoltage")) {
              store.setDetectorVoltage(new Float(data), series, nextDetector);
              nextDetector++;
            }
            else if (tokens[2].equals("State")) {
              store.setDetectorType("PMT", series, nextDetector);
              // link Detector to Image, if the detector was actually used
              if (data.equals("Active")) {
                String index = tokens[1].substring(tokens[1].indexOf(" ") + 1);
                int channelIndex = -1;
                try {
                  channelIndex = Integer.parseInt(index) - 1;
                }
                catch (NumberFormatException e) { }
                if (channelIndex >= 0) {
                  activeChannelIndices.add(new Integer(channelIndex));
                }

                String detectorID =
                  MetadataTools.createLSID("Detector", series, nextDetector);
                store.setDetectorID(detectorID, series, nextDetector);

                if (nextDetector == 0) {
                  // link every channel to the first detector in the beginning
                  // if additional detectors are found, the links will be
                  // overwritten
                  for (int c=0; c<getEffectiveSizeC(); c++) {
                    store.setDetectorSettingsDetector(detectorID, series, c);
                  }
                }

                if (nextChannel < getEffectiveSizeC()) {
                  store.setDetectorSettingsDetector(
                    detectorID, series, nextChannel++);
                }
              }
            }
          }
          catch (NumberFormatException e) {
            traceDebug(e);
          }
        }
      }
      else if (tokens[0].startsWith("CTurret")) {
        // objective information

        int objective = Integer.parseInt(tokens[3]);
        if (tokens[2].equals("NumericalAperture")) {
          store.setObjectiveLensNA(new Float(data), series, objective);
        }
        else if (tokens[2].equals("Objective")) {
          String[] objectiveData = data.split(" ");
          StringBuffer model = new StringBuffer();
          String mag = null, na = null;
          String immersion = null, correction = null;
          for (int i=0; i<objectiveData.length; i++) {
            if (objectiveData[i].indexOf("x") != -1 && mag == null &&
              na == null)
            {
              int xIndex = objectiveData[i].indexOf("x");
              mag = objectiveData[i].substring(0, xIndex).trim();
              na = objectiveData[i].substring(xIndex + 1).trim();
            }
            else if (mag == null && na == null) {
              model.append(objectiveData[i]);
              model.append(" ");
            }
            else if (correction == null) {
              correction = objectiveData[i];
            }
            else if (immersion == null) {
              immersion = objectiveData[i];
            }
          }
          if (immersion == null || immersion.trim().equals("")) {
            immersion = "Unknown";
          }
          if (correction == null) correction = "Unknown";

          store.setObjectiveImmersion(immersion, series, objective);
          store.setObjectiveCorrection(correction.trim(), series,
            objective);
          store.setObjectiveModel(model.toString().trim(), series, objective);
          store.setObjectiveLensNA(new Float(na), series, objective);
          store.setObjectiveNominalMagnification(
            new Integer((int) Float.parseFloat(mag)), series, objective);
        }
        else if (tokens[2].equals("OrderNumber")) {
          store.setObjectiveSerialNumber(data, series, objective);
        }
        else if (tokens[2].equals("RefractionIndex")) {
          store.setObjectiveSettingsRefractiveIndex(new Float(data), series);
        }

        // link Objective to Image
        String objectiveID =
          MetadataTools.createLSID("Objective", series, objective);
        store.setObjectiveID(objectiveID, series, objective);
        if (objective == 0) {
          store.setObjectiveSettingsObjective(objectiveID, series);
        }
      }
      else if (tokens[0].startsWith("CSpectrophotometerUnit")) {
        int ndx = tokens[1].lastIndexOf(" ");
        int channel = Integer.parseInt(tokens[1].substring(ndx + 1)) - 1;

        if (tokens[2].equals("Wavelength")) {
          Integer wavelength = new Integer((int) Float.parseFloat(data));
          store.setFilterModel(tokens[1], series, channel);

          String filterID = MetadataTools.createLSID("Filter", series, channel);
          store.setFilterID(filterID, series, channel);

          if (activeChannelIndices.contains(new Integer(channel))) {
            store.setLogicalChannelSecondaryEmissionFilter(filterID, series,
              activeChannelIndices.indexOf(new Integer(channel)));
          }

          if (tokens[3].equals("0")) {
            store.setTransmittanceRangeCutIn(wavelength, series, channel);
          }
          else if (tokens[3].equals("1")) {
            store.setTransmittanceRangeCutOut(wavelength, series, channel);
          }
        }
        else if (tokens[2].equals("Stain")) {
          if (activeChannelIndices.contains(new Integer(channel))) {
            channelNames[series].add(data);
          }
        }
      }
      else if (tokens[0].startsWith("CXYZStage")) {
        // NB: there is only one stage position specified for each series
        if (tokens[2].equals("XPos")) {
          for (int q=0; q<core[series].imageCount; q++) {
            store.setStagePositionPositionX(new Float(data), series, 0, q);
          }
        }
        else if (tokens[2].equals("YPos")) {
          for (int q=0; q<core[series].imageCount; q++) {
            store.setStagePositionPositionY(new Float(data), series, 0, q);
          }
        }
        else if (tokens[2].equals("ZPos")) {
          for (int q=0; q<core[series].imageCount; q++) {
            store.setStagePositionPositionZ(new Float(data), series, 0, q);
          }
        }
      }
      else if (tokens[0].equals("CScanActuator") &&
        tokens[1].equals("Z Scan Actuator") && tokens[2].equals("Position"))
      {
        float pos = Float.parseFloat(data) * 1000000;
        for (int q=0; q<core[series].imageCount; q++) {
          store.setStagePositionPositionZ(new Float(pos), series, 0, q);
        }
      }

      if (contentID.equals("dblVoxelX")) {
        physicalSizes[series][0] = Float.parseFloat(data);
      }
      else if (contentID.equals("dblVoxelY")) {
        physicalSizes[series][1] = Float.parseFloat(data);
      }
      else if (contentID.equals("dblStepSize")) {
        physicalSizes[series][2] = Float.parseFloat(data);
      }
      else if (contentID.equals("dblPinhole")) {
        // pinhole is stored in meters
        pinhole[series] = Float.parseFloat(data) * 1000000;
      }
      else if (contentID.startsWith("nDelayTime")) {
        exposureTime[series] = Float.parseFloat(data);
        if (contentID.endsWith("_ms")) {
          exposureTime[series] /= 1000f;
        }
      }

      addSeriesMeta("Block " + blockNum + " " + contentID, data);
    }
    stream.close();

    // populate saved LogicalChannel data

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      for (int channel=0; channel<getEffectiveSizeC(); channel++) {
        if (channel < channelNames[i].size()) {
          String name = (String) channelNames[i].get(channel);
          if (name != null && !name.trim().equals("") && !name.equals("None")) {
            store.setLogicalChannelName(name, i, channel);
          }
        }
        if (channel < emWaves[i].size()) {
          store.setLogicalChannelEmWave((Integer) emWaves[i].get(channel),
            i, channel);
        }
        if (channel < exWaves[i].size()) {
          store.setLogicalChannelExWave((Integer) exWaves[i].get(channel),
            i, channel);
        }
        if (i < pinhole.length) {
          store.setLogicalChannelPinholeSize(new Float(pinhole[i]), i, channel);
        }
      }
    }

    setSeries(0);
  }

  private boolean usedFile(String s) {
    if (files == null) return false;

    for (int i=0; i<files.length; i++) {
      if (files[i] == null) continue;
      for (int j=0; j<files[i].size(); j++) {
        if (((String) files[i].get(j)).endsWith(s)) return true;
      }
    }
    return false;
  }

  private String getString(RandomAccessInputStream stream, int len)
    throws IOException
  {
    return DataTools.stripString(stream.readString(len));
  }

  private String getString(RandomAccessInputStream stream, boolean doubleLength)
    throws IOException
  {
    int len = stream.readInt();
    if (doubleLength) len *= 2;
    return getString(stream, len);
  }

  private static Hashtable<Integer, String> makeDimensionTable() {
    Hashtable<Integer, String> table = new Hashtable<Integer, String>();
    table.put(new Integer(0), "undefined");
    table.put(new Integer(120), "x");
    table.put(new Integer(121), "y");
    table.put(new Integer(122), "z");
    table.put(new Integer(116), "t");
    table.put(new Integer(6815843), "channel");
    table.put(new Integer(6357100), "wave length");
    table.put(new Integer(7602290), "rotation");
    table.put(new Integer(7798904), "x-wide for the motorized xy-stage");
    table.put(new Integer(7798905), "y-wide for the motorized xy-stage");
    table.put(new Integer(7798906), "z-wide for the z-stage-drive");
    table.put(new Integer(4259957), "user1 - unspecified");
    table.put(new Integer(4325493), "user2 - unspecified");
    table.put(new Integer(4391029), "user3 - unspecified");
    table.put(new Integer(6357095), "graylevel");
    table.put(new Integer(6422631), "graylevel1");
    table.put(new Integer(6488167), "graylevel2");
    table.put(new Integer(6553703), "graylevel3");
    table.put(new Integer(7864398), "logical x");
    table.put(new Integer(7929934), "logical y");
    table.put(new Integer(7995470), "logical z");
    table.put(new Integer(7602254), "logical t");
    table.put(new Integer(7077966), "logical lambda");
    table.put(new Integer(7471182), "logical rotation");
    table.put(new Integer(5767246), "logical x-wide");
    table.put(new Integer(5832782), "logical y-wide");
    table.put(new Integer(5898318), "logical z-wide");
    return table;
  }

}
