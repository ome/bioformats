//
// LeicaReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
import java.text.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * LeicaReader is the file format reader for Leica files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LeicaReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LeicaReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaReader extends FormatReader {

  // -- Constants -

  public static final String[] LEI_SUFFIX = {"lei"};

  /** All Leica TIFFs have this tag. */
  private static final int LEICA_MAGIC_TAG = 33923;

  /** IFD tags. */
  private static final int SERIES = 10;
  private static final int IMAGES = 15;
  private static final int DIMDESCR = 20;
  private static final int FILTERSET = 30;
  private static final int TIMEINFO = 40;
  private static final int SCANNERSET = 50;
  private static final int EXPERIMENT = 60;
  private static final int LUTDESC = 70;

  // -- Static fields --

  private Hashtable dimensionNames = makeDimensionTable();

  // -- Fields --

  protected Hashtable[] ifds;

  /** Array of IFD-like structures containing metadata. */
  protected Hashtable[] headerIFDs;

  /** Helper readers. */
  protected MinimalTiffReader tiff;

  /** Array of image file names. */
  protected Vector[] files;

  /** Number of series in the file. */
  private int numSeries;

  /** Name of current LEI file */
  private String leiFilename;

  private Vector seriesNames;
  private int lastPlane = 0;

  // -- Constructor --

  /** Constructs a new Leica reader. */
  public LeicaReader() {
    super("Leica", new String[] {"lei", "tif", "tiff"});
  }

  // -- IFormatReader API methods --

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

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    Hashtable ifd = TiffTools.getFirstIFD(stream);
    return ifd.containsKey(new Integer(LEICA_MAGIC_TAG));
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    tiff.setId((String) files[series].get(lastPlane));
    return tiff.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    tiff.setId((String) files[series].get(lastPlane));
    return tiff.get16BitLookupTable();
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
    lastPlane = no;
    tiff.setId((String) files[series].get(no));
    return tiff.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector v = new Vector();
    v.add(leiFilename);
    for (int i=0; i<files.length; i++) {
      for (int j=0; j<files[i].size(); j++) {
        v.add(files[i].get(j));
      }
    }
    return (String[]) v.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (in != null) in.close();
    if (tiff != null) tiff.close();
    tiff = null;
    if (!fileOnly) {
      super.close();
      leiFilename = null;
      files = null;
      ifds = headerIFDs = null;
      tiff = null;
      seriesNames = null;
      numSeries = 0;
      lastPlane = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LeicaReader.initFile(" + id + ")");
    close();

    if (checkSuffix(id, TiffReader.TIFF_SUFFIXES)) {
      // need to find the associated .lei file
      if (ifds == null) super.initFile(id);

      in = new RandomAccessStream(id);
      in.order(TiffTools.checkHeader(in).booleanValue());

      in.seek(0);

      status("Finding companion file name");

      // open the TIFF file and look for the "Image Description" field

      ifds = TiffTools.getIFDs(in);
      if (ifds == null) throw new FormatException("No IFDs found");
      String descr = TiffTools.getComment(ifds[0]);

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
        addMeta(key, value);

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
    in = new RandomAccessStream(id);

    seriesNames = new Vector();

    byte[] fourBytes = new byte[4];
    in.read(fourBytes);
    core.littleEndian[0] = (fourBytes[0] == TiffTools.LITTLE &&
      fourBytes[1] == TiffTools.LITTLE &&
      fourBytes[2] == TiffTools.LITTLE &&
      fourBytes[3] == TiffTools.LITTLE);

    in.order(isLittleEndian());

    status("Reading metadata blocks");

    in.skipBytes(8);
    int addr = in.readInt();
    Vector v = new Vector();
    Hashtable ifd;
    while (addr != 0) {
      ifd = new Hashtable();
      v.add(ifd);
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

    numSeries = v.size();

    core = new CoreMetadata(numSeries);
    files = new Vector[numSeries];

    headerIFDs = (Hashtable[]) v.toArray(new Hashtable[0]);

    // determine the length of a filename

    int nameLength = 0;
    int maxPlanes = 0;

    status("Parsing metadata blocks");

    core.littleEndian[0] = !isLittleEndian();

    int seriesIndex = 0;
    boolean[] valid = new boolean[numSeries];
    for (int i=0; i<headerIFDs.length; i++) {
      valid[i] = true;
      if (headerIFDs[i].get(new Integer(SERIES)) != null) {
        byte[] temp = (byte[]) headerIFDs[i].get(new Integer(SERIES));
        nameLength = DataTools.bytesToInt(temp, 8, isLittleEndian()) * 2;
      }

      Vector f = new Vector();
      byte[] tempData = (byte[]) headerIFDs[i].get(new Integer(IMAGES));
      RandomAccessStream data = new RandomAccessStream(tempData);
      data.order(isLittleEndian());
      int tempImages = data.readInt();

      if (((long) tempImages * nameLength) > data.length()) {
        data.order(!isLittleEndian());
        tempImages = data.readInt();
        data.order(isLittleEndian());
      }

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

      Vector list = new Vector();

      for (int k=0; k<listing.length; k++) {
        if (checkSuffix(listing[k], TiffReader.TIFF_SUFFIXES)) {
          list.add(listing[k]);
        }
      }

      boolean tiffsExist = true;

      data.seek(20);

      String prefix = "";
      for (int j=0; j<tempImages; j++) {
        // read in each filename
        prefix = getString(data, nameLength);
        f.add(dirPrefix + prefix);
        // test to make sure the path is valid
        Location test = new Location((String) f.get(f.size() - 1));
        if (test.exists()) list.remove(prefix);
        if (tiffsExist) tiffsExist = test.exists();
      }
      data.close();
      tempData = null;

      // at least one of the TIFF files was renamed

      if (!tiffsExist) {
        // Strategy for handling renamed files:
        // 1) Assume that files for each series follow a pattern.
        // 2) Assign each file group to the first series with the correct count.
        status("Handling renamed TIFF files");

        listing = (String[]) list.toArray(new String[0]);

        // grab the file patterns
        Vector filePatterns = new Vector();
        for (int q=0; q<listing.length; q++) {
          Location l = new Location(dirPrefix, listing[q]);
          l = l.getAbsoluteFile();
          FilePattern pattern = new FilePattern(l);

          AxisGuesser guess = new AxisGuesser(pattern, "XYZCT", 1, 1, 1, false);
          String fp = pattern.getPattern();

          if (guess.getAxisCountS() >= 1) {
            String pre = pattern.getPrefix(guess.getAxisCountS());
            Vector fileList = new Vector();
            for (int n=0; n<listing.length; n++) {
              Location p = new Location(dirPrefix, listing[n]);
              if (p.getAbsolutePath().startsWith(pre)) {
                fileList.add(listing[n]);
              }
            }
            fp = FilePattern.findPattern(l.getAbsolutePath(), dirPrefix,
              (String[]) fileList.toArray(new String[0]));
          }

          if (fp != null && !filePatterns.contains(fp)) {
            filePatterns.add(fp);
          }
        }

        for (int q=0; q<filePatterns.size(); q++) {
          String[] pattern =
            new FilePattern((String) filePatterns.get(q)).getFiles();
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
              files[i] = new Vector();
              for (int n=0; n<pattern.length; n++) {
                files[i].add(pattern[n]);
              }
            }
          }
        }
      }
      else files[i] = f;
      if (files[i] == null) valid[i] = false;
      else {
        core.imageCount[i] = files[i].size();
        if (core.imageCount[i] > maxPlanes) maxPlanes = core.imageCount[i];
      }
    }

    int invalidCount = 0;
    for (int i=0; i<valid.length; i++) {
      if (!valid[i]) invalidCount++;
    }

    numSeries -= invalidCount;

    int[] count = core.imageCount;
    Vector[] tempFiles = files;
    Hashtable[] tempIFDs = headerIFDs;
    core = new CoreMetadata(numSeries);
    files = new Vector[numSeries];
    headerIFDs = new Hashtable[numSeries];
    int index = 0;

    for (int i=0; i<numSeries; i++) {
      while (!valid[index]) index++;
      core.imageCount[i] = count[index];
      files[i] = tempFiles[index];
      headerIFDs[i] = tempIFDs[index];
      index++;
    }

    tiff = new MinimalTiffReader();

    status("Populating metadata");

    if (headerIFDs == null) headerIFDs = ifds;

    int fileLength = 0;

    int resolution = -1;
    String description = null;
    String[] timestamps = null;

    for (int i=0; i<headerIFDs.length; i++) {
      byte[] temp = (byte[]) headerIFDs[i].get(new Integer(SERIES));
      if (temp != null) {
        // the series data
        // ID_SERIES
        RandomAccessStream stream = new RandomAccessStream(temp);
        stream.order(isLittleEndian());
        addMeta("Version", new Integer(stream.readInt()));
        addMeta("Number of Series", new Integer(stream.readInt()));
        fileLength = stream.readInt();
        addMeta("Length of filename", new Integer(fileLength));
        Integer extLen = new Integer(stream.readInt());
        if (extLen.intValue() > fileLength) {
          stream.seek(0);
          core.littleEndian[0] = !isLittleEndian();
          stream.order(isLittleEndian());
          fileLength = stream.readInt();
          extLen = new Integer(stream.readInt());
        }
        addMeta("Length of file extension", extLen);
        addMeta("Image file extension", getString(stream, extLen.intValue()));
        stream.close();
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(IMAGES));
      if (temp != null) {
        // the image data
        // ID_IMAGES
        RandomAccessStream s = new RandomAccessStream(temp);
        s.order(isLittleEndian());

        core.imageCount[i] = s.readInt();
        core.sizeX[i] = s.readInt();
        core.sizeY[i] = s.readInt();

        addMeta("Number of images", new Integer(core.imageCount[i]));
        addMeta("Image width", new Integer(core.sizeX[i]));
        addMeta("Image height", new Integer(core.sizeY[i]));
        addMeta("Bits per Sample", new Integer(s.readInt()));
        addMeta("Samples per pixel", new Integer(s.readInt()));

        String prefix = getString(s, fileLength * 2);
        s.close();

        StringTokenizer st = new StringTokenizer(prefix, "_");
        StringBuffer buf = new StringBuffer();
        st.nextToken();
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          String lcase = token.toLowerCase();
          if (!checkSuffix(lcase, TiffReader.TIFF_SUFFIXES) &&
            !lcase.startsWith("ch0") && !lcase.startsWith("c0") &&
            !lcase.startsWith("z0"))
          {
            if (buf.length() > 0) buf.append("_");
            buf.append(token);
          }
        }
        seriesNames.add(buf.toString());
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(DIMDESCR));
      if (temp != null) {
        // dimension description
        // ID_DIMDESCR

        RandomAccessStream stream = new RandomAccessStream(temp);
        stream.order(isLittleEndian());

        addMeta("Voxel Version", new Integer(stream.readInt()));
        int voxelType = stream.readInt();
        core.rgb[i] = voxelType == 20;

        addMeta("VoxelType", voxelType == 20 ? "RGB" : "gray");

        int bpp = stream.readInt();
        addMeta("Bytes per pixel", new Integer(bpp));

        switch (bpp) {
          case 1:
          case 3:
            core.pixelType[i] = FormatTools.UINT8;
            break;
          case 2:
          case 6:
            core.pixelType[i] = FormatTools.UINT16;
            break;
          case 4:
            core.pixelType[i] = FormatTools.UINT32;
            break;
          default:
            throw new FormatException("Unsupported bytes per pixel (" +
              bpp + ")");
        }

        core.currentOrder[i] = "XY";

        resolution = stream.readInt();
        addMeta("Real world resolution", new Integer(resolution));
        int length = stream.readInt();
        addMeta("Maximum voxel intensity", getString(stream, length * 2));
        length = stream.readInt();
        addMeta("Minimum voxel intensity", getString(stream, length * 2));
        length = stream.readInt();
        stream.skipBytes(length * 2);
        stream.skipBytes(4); // version number
        length = stream.readInt();
        for (int j=0; j<length; j++) {
          int dimId = stream.readInt();
          String dimType = (String) dimensionNames.get(new Integer(dimId));

          int size = stream.readInt();

          if (dimType.equals("x")) core.sizeX[i] = size;
          else if (dimType.equals("y")) core.sizeY[i] = size;
          else if (dimType.indexOf("z") != -1) {
            core.sizeZ[i] = size;
            if (core.currentOrder[i].indexOf("Z") == -1) {
              core.currentOrder[i] += "Z";
            }
          }
          else if (dimType.equals("channel")) {
            core.sizeC[i] = size;
            if (core.currentOrder[i].indexOf("C") == -1) {
              core.currentOrder[i] += "C";
            }
          }
          else {
            core.sizeT[i] = size;
            if (core.currentOrder[i].indexOf("T") == -1) {
              core.currentOrder[i] += "T";
            }
          }

          addMeta("Dim" + j + " type", dimType);
          addMeta("Dim" + j + " size", new Integer(size));
          addMeta("Dim" + j + " distance between sub-dimensions",
            new Integer(stream.readInt()));

          int len = stream.readInt();
          addMeta("Dim" + j + " physical length", getString(stream, len * 2));

          len = stream.readInt();
          addMeta("Dim" + j + " physical origin", getString(stream, len * 2));
        }
        int len = stream.readInt();
        addMeta("Series name", getString(stream, len));

        len = stream.readInt();
        addMeta("Series description", getString(stream, len));
        stream.close();
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(FILTERSET));
      if (temp != null) {
        // filter data
        // ID_FILTERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(TIMEINFO));

      if (temp != null) {
        // time data
        // ID_TIMEINFO

        RandomAccessStream stream = new RandomAccessStream(temp);
        stream.order(isLittleEndian());
        stream.seek(0);

        int nDims = stream.readInt();
        addMeta("Number of time-stamped dimensions", new Integer(nDims));
        addMeta("Time-stamped dimension", new Integer(stream.readInt()));

        for (int j=0; j < nDims; j++) {
          addMeta("Dimension " + j + " ID", new Integer(stream.readInt()));
          addMeta("Dimension " + j + " size", new Integer(stream.readInt()));
          addMeta("Dimension " + j + " distance between dimensions",
            new Integer(stream.readInt()));
        }

        int numStamps = stream.readInt();
        addMeta("Number of time-stamps", new Integer(numStamps));
        timestamps = new String[numStamps];
        for (int j=0; j<numStamps; j++) {
          timestamps[j] = getString(stream, 64);
          addMeta("Timestamp " + j, timestamps[j]);
        }

        if (stream.getFilePointer() < stream.length()) {
          int numTMs = stream.readInt();
          addMeta("Number of time-markers", new Integer(numTMs));
          for (int j=0; j<numTMs; j++) {
            int numDims = stream.readInt();

            for (int k=0; k<numDims; k++) {
              int value = stream.readInt();
              addMeta("Time-marker " + j +
                " Dimension " + k + " coordinate", new Integer(value));
            }
            addMeta("Time-marker " + j, getString(stream, 64));
          }
        }
        stream.close();
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(SCANNERSET));
      if (temp != null) {
        // scanner data
        // ID_SCANNERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(EXPERIMENT));
      if (temp != null) {
        // experiment data
        // ID_EXPERIMENT

        RandomAccessStream stream = new RandomAccessStream(temp);
        stream.order(isLittleEndian());
        stream.seek(8);

        int len = stream.readInt();
        description = getString(stream, len * 2);
        addMeta("Image Description", description);
        len = stream.readInt();

        addMeta("Main file extension", getString(stream, len * 2));

        len = stream.readInt();
        addMeta("Single image format identifier", getString(stream, len * 2));

        len = stream.readInt();
        addMeta("Single image extension", getString(stream, len * 2));
        stream.close();
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(LUTDESC));
      if (temp != null) {
        // LUT data
        // ID_LUTDESC

        RandomAccessStream stream = new RandomAccessStream(temp);
        stream.order(isLittleEndian());

        int nChannels = stream.readInt();
        if (nChannels > 0) core.indexed[i] = true;
        addMeta("Number of LUT channels", new Integer(nChannels));
        addMeta("ID of colored dimension", new Integer(stream.readInt()));

        for (int j=0; j<nChannels; j++) {
          int value = stream.readInt();
          String prefix = "LUT Channel " + j;
          addMeta(prefix + " version", new Integer(value));

          int invert = stream.read();
          boolean inverted = invert == 1;
          addMeta(prefix + " inverted?", new Boolean(inverted));

          int length = stream.readInt();
          addMeta(prefix + " description", getString(stream, length));

          length = stream.readInt();
          addMeta(prefix + " filename", getString(stream, length));
          length = stream.readInt();

          addMeta(prefix + " name", getString(stream, length));
          stream.skipBytes(8);
        }
        stream.close();
      }
    }

    Arrays.fill(core.orderCertain, true);
    Arrays.fill(core.littleEndian, isLittleEndian());
    Arrays.fill(core.falseColor, true);
    Arrays.fill(core.metadataComplete, true);
    Arrays.fill(core.interleaved, false);

    // the metadata store we're working with
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int i=0; i<numSeries; i++) {
      if (core.sizeZ[i] == 0) core.sizeZ[i] = 1;
      if (core.sizeT[i] == 0) core.sizeT[i] = 1;
      if (core.sizeC[i] == 0) core.sizeC[i] = 1;
      if (core.imageCount[i] == 0) core.imageCount[i] = 1;
      if (core.imageCount[i] == 1 && core.sizeZ[i] > 1) {
        core.sizeZ[i] = 1;
      }
      if (core.imageCount[i] == 1 && core.sizeT[i] > 1) {
        core.sizeT[i] = 1;
      }
      tiff.setId((String) files[i].get(0));
      core.sizeX[i] = tiff.getSizeX();
      core.sizeY[i] = tiff.getSizeY();
      core.rgb[i] = tiff.isRGB();
      core.indexed[i] = tiff.isIndexed();
      core.sizeC[i] *= tiff.getSizeC();

      if (core.currentOrder[i].indexOf("C") == -1) {
        core.currentOrder[i] += "C";
      }
      if (core.currentOrder[i].indexOf("Z") == -1) {
        core.currentOrder[i] += "Z";
      }
      if (core.currentOrder[i].indexOf("T") == -1) {
        core.currentOrder[i] += "T";
      }

      if (i < timestamps.length && timestamps[i] != null) {
        SimpleDateFormat parse =
          new SimpleDateFormat("yyyy:MM:dd,HH:mm:ss:SSS");
        Date date = parse.parse(timestamps[i], new ParsePosition(0));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        timestamps[i] = fmt.format(date);
        store.setImageCreationDate(timestamps[i], i);
      }
      else {
        MetadataTools.setDefaultCreationDate(store, id, i);
      }

      store.setImageName((String) seriesNames.get(i), i);
      store.setImageDescription(description, i);
    }
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

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

  private String getString(RandomAccessStream stream, int len)
    throws IOException
  {
    return DataTools.stripString(stream.readString(len));
  }

  private static Hashtable makeDimensionTable() {
    Hashtable table = new Hashtable();
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
