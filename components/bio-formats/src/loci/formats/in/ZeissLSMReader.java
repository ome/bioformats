//
// ZeissLSMReader.java
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

import java.io.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ZeissLSMReader is the file format reader for Zeiss LSM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ZeissLSMReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ZeissLSMReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ZeissLSMReader extends FormatReader {

  // -- Constants --

  public static final String[] MDB_SUFFIX = {"mdb"};

  /** Tag identifying a Zeiss LSM file. */
  private static final int ZEISS_ID = 34412;

  /** Data types. */
  private static final int TYPE_SUBBLOCK = 0;
  private static final int TYPE_ASCII = 2;
  private static final int TYPE_LONG = 4;
  private static final int TYPE_RATIONAL = 5;

  /** Subblock types. */
  private static final int SUBBLOCK_RECORDING = 0x10000000;
  private static final int SUBBLOCK_LASERS = 0x30000000;
  private static final int SUBBLOCK_LASER = 0x50000000;
  private static final int SUBBLOCK_TRACKS = 0x20000000;
  private static final int SUBBLOCK_TRACK = 0x40000000;
  private static final int SUBBLOCK_DETECTION_CHANNELS = 0x60000000;
  private static final int SUBBLOCK_DETECTION_CHANNEL = 0x70000000;
  private static final int SUBBLOCK_ILLUMINATION_CHANNELS = 0x80000000;
  private static final int SUBBLOCK_ILLUMINATION_CHANNEL = 0x90000000;
  private static final int SUBBLOCK_BEAM_SPLITTERS = 0xa0000000;
  private static final int SUBBLOCK_BEAM_SPLITTER = 0xb0000000;
  private static final int SUBBLOCK_DATA_CHANNELS = 0xc0000000;
  private static final int SUBBLOCK_DATA_CHANNEL = 0xd0000000;
  private static final int SUBBLOCK_TIMERS = 0x11000000;
  private static final int SUBBLOCK_TIMER = 0x12000000;
  private static final int SUBBLOCK_MARKERS = 0x13000000;
  private static final int SUBBLOCK_MARKER = 0x14000000;
  private static final int SUBBLOCK_END = (int) 0xffffffff;

  private static final int SUBBLOCK_GAMMA = 1;
  private static final int SUBBLOCK_BRIGHTNESS = 2;
  private static final int SUBBLOCK_CONTRAST = 3;
  private static final int SUBBLOCK_RAMP = 4;
  private static final int SUBBLOCK_KNOTS = 5;
  private static final int SUBBLOCK_PALETTE = 6;

  /** Data types. */
  private static final int RECORDING_ENTRY_DESCRIPTION = 0x10000002;
  private static final int RECORDING_ENTRY_OBJECTIVE = 0x10000004;

  private static final int TRACK_ENTRY_TIME_BETWEEN_STACKS = 0x4000000b;

  private static final int LASER_ENTRY_NAME = 0x50000001;

  private static final int CHANNEL_ENTRY_DETECTOR_GAIN = 0x70000003;
  private static final int CHANNEL_ENTRY_PINHOLE_DIAMETER = 0x70000009;
  private static final int CHANNEL_ENTRY_SPI_WAVELENGTH_START = 0x70000022;
  private static final int CHANNEL_ENTRY_SPI_WAVELENGTH_END = 0x70000023;
  private static final int ILLUM_CHANNEL_WAVELENGTH = 0x90000003;
  private static final int START_TIME = 0x10000036;
  private static final int DATA_CHANNEL_NAME = 0xd0000001;

  /** Drawing element types. */
  private static final int TEXT = 13;
  private static final int LINE = 14;
  private static final int SCALE_BAR = 15;
  private static final int OPEN_ARROW = 16;
  private static final int CLOSED_ARROW = 17;
  private static final int RECTANGLE = 18;
  private static final int ELLIPSE = 19;
  private static final int CLOSED_POLYLINE = 20;
  private static final int OPEN_POLYLINE = 21;
  private static final int CLOSED_BEZIER = 22;
  private static final int OPEN_BEZIER = 23;
  private static final int CIRCLE = 24;
  private static final int PALETTE = 25;
  private static final int POLYLINE_ARROW = 26;
  private static final int BEZIER_WITH_ARROW = 27;
  private static final int ANGLE = 28;
  private static final int CIRCLE_3POINT = 29;

  // -- Static fields --

  private static Hashtable metadataKeys = createKeys();

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private byte[][] lut = null;
  private Vector timestamps;
  private int validChannels;

  private String[] lsmFilenames;
  private Hashtable[][] ifds;

  // -- Constructor --

  /** Constructs a new Zeiss LSM reader. */
  public ZeissLSMReader() {
    super("Zeiss Laser-Scanning Microscopy", new String[] {"lsm", "mdb"});
    blockCheckLen = 4;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    pixelSizeX = pixelSizeY = pixelSizeZ = 0f;
    lut = null;
    timestamps = null;
    validChannels = 0;
    lsmFilenames = null;
    ifds = null;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    byte[] check = new byte[blockCheckLen];
    stream.read(check);
    return TiffTools.isValidHeader(check) ||
      (check[2] == 0x53 && check[3] == 0x74);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (lsmFilenames == null) return new String[] {currentId};
    if (lsmFilenames.length == 1 && currentId.equals(lsmFilenames[0])) {
      return lsmFilenames;
    }
    String[] files = new String[lsmFilenames.length + 1];
    System.arraycopy(lsmFilenames, 0, files, 0, lsmFilenames.length);
    files[files.length - 1] = currentId;
    return files;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || lut[getSeries()]  == null ||
      getPixelType() != FormatTools.UINT8)
    {
      return null;
    }
    byte[][] b = new byte[3][256];
    for (int i=2; i>=3-validChannels; i--) {
      for (int j=0; j<256; j++) {
        b[i][j] = (byte) j;
      }
    }
    return b;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || lut[getSeries()] == null ||
      getPixelType() != FormatTools.UINT16)
    {
      return null;
    }
    short[][] s = new short[3][65536];
    for (int i=2; i>=3-validChannels; i--) {
      for (int j=0; j<s[i].length; j++) {
        s[i][j] = (short) j;
      }
    }
    return s;
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

    in = new RandomAccessStream(lsmFilenames[getSeries()]);
    in.order(!isLittleEndian());
    TiffTools.getSamples(ifds[getSeries()][no], in, buf, x, y, w, h);
    in.close();
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ZeissLSMReader.initFile(" + id + ")");
    super.initFile(id);

    if (!checkSuffix(id, MDB_SUFFIX)) {
      // look for an .mdb file
      Location parentFile = new Location(id).getAbsoluteFile().getParentFile();
      String[] fileList = parentFile.list();
      for (int i=0; i<fileList.length; i++) {
        if (checkSuffix(fileList[i], MDB_SUFFIX)) {
          Location file =
            new Location(parentFile, fileList[i]).getAbsoluteFile();
          if (file.isDirectory()) continue;
          setId(new Location(parentFile, fileList[i]).getAbsolutePath());
          return;
        }
      }
      lsmFilenames = new String[] {id};
    }
    else lsmFilenames = parseMDB(id);

    if (lsmFilenames.length == 0) {
      throw new FormatException("LSM files were not found.");
    }

    timestamps = new Vector();

    core = new CoreMetadata[lsmFilenames.length];
    ifds = new Hashtable[core.length][];
    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
      RandomAccessStream s = new RandomAccessStream(lsmFilenames[i]);
      core[i].littleEndian = s.read() == TiffTools.LITTLE;
      s.order(isLittleEndian());
      s.seek(0);
      ifds[i] = TiffTools.getIFDs(s);
      s.close();
    }

    // go through the IFD hashtable arrays and
    // remove anything with NEW_SUBFILE_TYPE = 1
    // NEW_SUBFILE_TYPE = 1 indicates that the IFD
    // contains a thumbnail image

    status("Removing thumbnails");

    for (int series=0; series<ifds.length; series++) {
      Vector newIFDs = new Vector();
      for (int i=0; i<ifds[series].length; i++) {
        Hashtable ifd = ifds[series][i];
        long subFileType = TiffTools.getIFDLongValue(ifd,
          TiffTools.NEW_SUBFILE_TYPE, false, 0);

        if (subFileType == 0) {
          // check that predictor is set to 1 if anything other
          // than LZW compression is used
          if (TiffTools.getCompression(ifd) != TiffTools.LZW) {
            ifd.put(new Integer(TiffTools.PREDICTOR), new Integer(1));
          }
          newIFDs.add(ifd);
        }
      }
      ifds[series] = (Hashtable[]) newIFDs.toArray(new Hashtable[0]);

      // fix the offsets for > 4 GB files
      for (int i=1; i<ifds[series].length; i++) {
        long thisOffset =
          TiffTools.getStripOffsets(ifds[series][i])[0] & 0xffffffffL;
        long prevOffset = TiffTools.getStripOffsets(ifds[series][i - 1])[0];
        if (prevOffset < 0) prevOffset &= 0xffffffffL;

        if (prevOffset > thisOffset) {
          thisOffset += 0xffffffffL;
          ifds[series][i].put(new Integer(TiffTools.STRIP_OFFSETS),
            new Long(thisOffset));
        }
      }

      initMetadata(series);
      core[series].littleEndian = !isLittleEndian();
    }
    setSeries(0);
  }

  // -- Helper methods --

  protected void initMetadata(int series) throws FormatException, IOException {
    setSeries(series);
    Hashtable ifd = ifds[series][0];

    in = new RandomAccessStream(lsmFilenames[series]);
    in.order(isLittleEndian());

    int photo = TiffTools.getPhotometricInterpretation(ifd);
    int samples = TiffTools.getSamplesPerPixel(ifd);

    core[series].sizeX = (int) TiffTools.getImageWidth(ifd);
    core[series].sizeY = (int) TiffTools.getImageLength(ifd);
    core[series].rgb = samples > 1 || photo == TiffTools.RGB;
    core[series].interleaved = false;
    core[series].sizeC = isRGB() ? samples : 1;
    core[series].pixelType = TiffTools.getPixelType(ifd);
    core[series].imageCount = ifds[series].length;
    core[series].sizeZ = getImageCount();
    core[series].sizeT = 1;

    String keyPrefix = "Series " + series + " ";

    status("Reading LSM metadata");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", series);
    store.setImageInstrumentRef("Instrument:0", series);

    // get TIF_CZ_LSMINFO structure
    short[] s = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
    byte[] cz = new byte[s.length];
    for (int i=0; i<s.length; i++) {
      cz[i] = (byte) s[i];
    }

    RandomAccessStream ras = new RandomAccessStream(cz);
    ras.order(isLittleEndian());

    addMeta(keyPrefix + "MagicNumber ", ras.readInt());
    addMeta(keyPrefix + "StructureSize", ras.readInt());
    addMeta(keyPrefix + "DimensionX", ras.readInt());
    addMeta(keyPrefix + "DimensionY", ras.readInt());

    core[series].sizeZ = ras.readInt();
    ras.skipBytes(4);
    core[series].sizeT = ras.readInt();

    int dataType = ras.readInt();
    switch (dataType) {
      case 2:
        addMeta(keyPrefix + "DataType", "12 bit unsigned integer");
        break;
      case 5:
        addMeta(keyPrefix + "DataType", "32 bit float");
        break;
      case 0:
        addMeta(keyPrefix + "DataType", "varying data types");
        break;
      default:
        addMeta(keyPrefix + "DataType", "8 bit unsigned integer");
    }

    addMeta(keyPrefix + "ThumbnailX", ras.readInt());
    addMeta(keyPrefix + "ThumbnailY", ras.readInt());

    // pixel sizes are stored in meters, we need them in microns
    pixelSizeX = ras.readDouble() * 1000000;
    pixelSizeY = ras.readDouble() * 1000000;
    pixelSizeZ = ras.readDouble() * 1000000;

    addMeta(keyPrefix + "VoxelSizeX", new Double(pixelSizeX));
    addMeta(keyPrefix + "VoxelSizeY", new Double(pixelSizeY));
    addMeta(keyPrefix + "VoxelSizeZ", new Double(pixelSizeZ));

    addMeta(keyPrefix + "OriginX", ras.readDouble());
    addMeta(keyPrefix + "OriginY", ras.readDouble());
    addMeta(keyPrefix + "OriginZ", ras.readDouble());

    int scanType = ras.readShort();
    switch (scanType) {
      case 0:
        addMeta(keyPrefix + "ScanType", "x-y-z scan");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 1:
        addMeta(keyPrefix + "ScanType", "z scan (x-z plane)");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 2:
        addMeta(keyPrefix + "ScanType", "line scan");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 3:
        addMeta(keyPrefix + "ScanType", "time series x-y");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 4:
        addMeta(keyPrefix + "ScanType", "time series x-z");
        core[series].dimensionOrder = "XYZTC";
        break;
      case 5:
        addMeta(keyPrefix + "ScanType", "time series 'Mean of ROIs'");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 6:
        addMeta(keyPrefix + "ScanType", "time series x-y-z");
        core[series].dimensionOrder = "XYZTC";
        break;
      case 7:
        addMeta(keyPrefix + "ScanType", "spline scan");
        core[series].dimensionOrder = "XYCTZ";
        break;
      case 8:
        addMeta(keyPrefix + "ScanType", "spline scan x-z");
        core[series].dimensionOrder = "XYCZT";
        break;
      case 9:
        addMeta(keyPrefix + "ScanType", "time series spline plane x-z");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 10:
        addMeta(keyPrefix + "ScanType", "point mode");
        core[series].dimensionOrder = "XYZCT";
        break;
      default:
        addMeta(keyPrefix + "ScanType", "x-y-z scan");
        core[series].dimensionOrder = "XYZCT";
    }

    core[series].indexed =
      lut != null && lut[series] != null && getSizeC() == 1;
    if (isIndexed()) {
      core[series].sizeC = 1;
      core[series].rgb = false;
    }
    if (getSizeC() == 0) core[series].sizeC = 1;

    if (isRGB()) {
      // shuffle C to front of order string
      core[series].dimensionOrder = getDimensionOrder().replaceAll("C", "");
      core[series].dimensionOrder = getDimensionOrder().replaceAll("XY", "XYC");
    }

    if (isIndexed()) core[series].rgb = false;
    if (getEffectiveSizeC() == 0) {
      core[series].imageCount = getSizeZ() * getSizeT();
    }
    else {
      core[series].imageCount = getSizeZ() * getSizeT() * getEffectiveSizeC();
    }

    if (getImageCount() != ifds[series].length) {
      int diff = getImageCount() - ifds[series].length;
      core[series].imageCount = ifds[series].length;
      if (diff % getSizeZ() == 0) {
        core[series].sizeT -= (diff / getSizeZ());
      }
      else if (diff % getSizeT() == 0) {
        core[series].sizeZ -= (diff / getSizeT());
      }
      else if (getSizeZ() > 1) {
        core[series].sizeZ = ifds[series].length;
        core[series].sizeT = 1;
      }
      else if (getSizeT() > 1) {
        core[series].sizeT = ifds[series].length;
        core[series].sizeZ = 1;
      }
    }

    if (getSizeZ() == 0) core[series].sizeZ = getImageCount();
    if (getSizeT() == 0) core[series].sizeT = getImageCount() / getSizeZ();

    MetadataTools.populatePixels(store, this, true);
    MetadataTools.setDefaultCreationDate(store, getCurrentFile(), series);

    int spectralScan = ras.readShort();
    if (spectralScan != 1) {
       addMeta(keyPrefix + "SpectralScan", "no spectral scan");
    }
    else  addMeta(keyPrefix + "SpectralScan", "acquired with spectral scan");

    int type = ras.readInt();
    switch (type) {
      case 1:
        addMeta(keyPrefix + "DataType2", "calculated data");
        break;
      case 2:
        addMeta(keyPrefix + "DataType2", "animation");
        break;
      default:
        addMeta(keyPrefix + "DataType2", "original scan data");
    }

    long[] overlayOffsets = new long[9];
    String[] overlayKeys = new String[] {"VectorOverlay", "InputLut",
      "OutputLut", "ROI", "BleachROI", "MeanOfRoisOverlay",
      "TopoIsolineOverlay", "TopoProfileOverlay", "LinescanOverlay"};

    overlayOffsets[0] = ras.readInt();
    overlayOffsets[1] = ras.readInt();
    overlayOffsets[2] = ras.readInt();

    long channelColorsOffset = ras.readInt();

    addMeta(keyPrefix + "TimeInterval", ras.readDouble());
    ras.skipBytes(4);
    long scanInformationOffset = ras.readInt();
    ras.skipBytes(4);
    long timeStampOffset = ras.readInt();
    long eventListOffset = ras.readInt();
    overlayOffsets[3] = ras.readInt();
    overlayOffsets[4] = ras.readInt();
    ras.skipBytes(4);

    addMeta(keyPrefix + "DisplayAspectX", ras.readDouble());
    addMeta(keyPrefix + "DisplayAspectY", ras.readDouble());
    addMeta(keyPrefix + "DisplayAspectZ", ras.readDouble());
    addMeta(keyPrefix + "DisplayAspectTime", ras.readDouble());

    overlayOffsets[5] = ras.readInt();
    overlayOffsets[6] = ras.readInt();
    overlayOffsets[7] = ras.readInt();
    overlayOffsets[8] = ras.readInt();

    for (int i=0; i<overlayOffsets.length; i++) {
      parseOverlays(series, overlayOffsets[i], overlayKeys[i], store);
    }

    addMeta(keyPrefix + "ToolbarFlags", ras.readInt());
    ras.close();

    // read referenced structures

    addMeta(keyPrefix + "DimensionZ", getSizeZ());
    addMeta(keyPrefix + "DimensionChannels", getSizeC());

    if (channelColorsOffset != 0) {
      in.seek(channelColorsOffset + 16);
      int namesOffset = in.readInt();

      // read in the intensity value for each color

      if (namesOffset > 0) {
        in.skipBytes(namesOffset - 16);

        for (int i=0; i<getSizeC(); i++) {
          if (in.getFilePointer() >= in.length() - 1) break;
          // we want to read until we find a null char
          String name = in.readCString();
          if (name.length() <= 128) addMeta(keyPrefix + "ChannelName" + i, name);
        }
      }
    }

    if (timeStampOffset != 0) {
      in.seek(timeStampOffset + 8);
      for (int i=0; i<getSizeT(); i++) {
        double stamp = in.readDouble();
        addMeta(keyPrefix + "TimeStamp" + i, stamp);
        timestamps.add(new Double(stamp));
      }
    }

    if (eventListOffset != 0) {
      in.seek(eventListOffset + 4);
      int numEvents = in.readInt();
      in.seek(in.getFilePointer() - 4);
      in.order(!in.isLittleEndian());
      int tmpEvents = in.readInt();
      if (numEvents < 0) numEvents = tmpEvents;
      else numEvents = (int) Math.min(numEvents, tmpEvents);
      in.order(!in.isLittleEndian());

      if (numEvents > 65535) numEvents = 0;

      for (int i=0; i<numEvents; i++) {
        if (in.getFilePointer() + 16 <= in.length()) {
          int size = in.readInt();
          double eventTime = in.readDouble();
          int eventType = in.readInt();
          addMeta(keyPrefix + "Event" + i + " Time", eventTime);
          addMeta(keyPrefix + "Event" + i + " Type", eventType);
          long fp = in.getFilePointer();
          int len = size - 16;
          if (len > 65536) len = 65536;
          if (len < 0) len = 0;
          addMeta(keyPrefix + "Event" + i + " Description", in.readString(len));
          in.seek(fp + size - 16);
          if (in.getFilePointer() < 0) break;
        }
      }
    }

    Hashtable acquireChannels = new Hashtable();
    Hashtable channelData = new Hashtable();

    Hashtable acquireLasers = new Hashtable();
    Hashtable laserData = new Hashtable();

    if (scanInformationOffset != 0) {
      in.seek(scanInformationOffset);

      Stack prefix = new Stack();
      int count = 1;

      Object value = null;

      boolean done = false;
      int nextLaserMedium = 0, nextLaserType = 0, nextGain = 0;
      int nextPinhole = 0, nextEmWave = 0, nextExWave = 0;
      int nextChannelName = 0;

      while (!done) {
        int entry = in.readInt();
        int blockType = in.readInt();
        int dataSize = in.readInt();

        switch (blockType) {
          case TYPE_SUBBLOCK:
            switch (entry) {
              case SUBBLOCK_RECORDING:
                prefix.push("Recording");
                break;
              case SUBBLOCK_LASERS:
                prefix.push("Lasers");
                break;
              case SUBBLOCK_LASER:
                prefix.push("Laser " + count);
                count++;
                break;
              case SUBBLOCK_TRACKS:
                prefix.push("Tracks");
                break;
              case SUBBLOCK_TRACK:
                prefix.push("Track " + count);
                count++;
                break;
              case SUBBLOCK_DETECTION_CHANNELS:
                prefix.push("Detection Channels");
                break;
              case SUBBLOCK_DETECTION_CHANNEL:
                prefix.push("Detection Channel " + count);
                count++;
                validChannels = count;
                break;
              case SUBBLOCK_ILLUMINATION_CHANNELS:
                prefix.push("Illumination Channels");
                break;
              case SUBBLOCK_ILLUMINATION_CHANNEL:
                prefix.push("Illumination Channel " + count);
                count++;
                break;
              case SUBBLOCK_BEAM_SPLITTERS:
                prefix.push("Beam Splitters");
                break;
              case SUBBLOCK_BEAM_SPLITTER:
                prefix.push("Beam Splitter " + count);
                count++;
                break;
              case SUBBLOCK_DATA_CHANNELS:
                prefix.push("Data Channels");
                break;
              case SUBBLOCK_DATA_CHANNEL:
                prefix.push("Data Channel " + count);
                count++;
                break;
              case SUBBLOCK_TIMERS:
                prefix.push("Timers");
                break;
              case SUBBLOCK_TIMER:
                prefix.push("Timer " + count);
                count++;
                break;
              case SUBBLOCK_MARKERS:
                prefix.push("Markers");
                break;
              case SUBBLOCK_MARKER:
                prefix.push("Marker " + count);
                count++;
                break;
              case SUBBLOCK_END:
                if (prefix.size() > 0) {
                  String p = (String) prefix.pop();
                  if (p.endsWith("s")) count = 1;
                }
                if (prefix.size() == 0) done = true;
                break;
            }
            break;
          case TYPE_LONG:
            value = new Long(in.readInt());
            break;
          case TYPE_RATIONAL:
            value = new Double(in.readDouble());
            break;
          case TYPE_ASCII:
            value = in.readString(dataSize);
            break;
        }
        String key = getKey(prefix, entry).trim();
        if (key != null) addMeta(keyPrefix + key, value);
        String v = value != null ? value.toString().trim() : "";

        if (key.endsWith("Acquire")) {
          Integer index = new Integer(count - 2);
          Integer acquire = new Integer(v);
          if (key.indexOf("Detection Channel") != -1) {
            acquireChannels.put(index, acquire);
          }
          else if (key.indexOf("Laser") != -1) {
            acquireLasers.put(index, acquire);
          }
        }

        switch (entry) {
          case RECORDING_ENTRY_DESCRIPTION:
            store.setImageDescription(v, series);
            break;
          case RECORDING_ENTRY_OBJECTIVE:
            String[] tokens = v.split(" ");
            StringBuffer model = new StringBuffer();
            int next = 0;
            for (; next<tokens.length; next++) {
              if (tokens[next].indexOf("/") != -1) break;
              model.append(tokens[next]);
            }
            store.setObjectiveModel(model.toString(), series, 0);
            if (next < tokens.length) {
              String p = tokens[next++];
              String mag = p.substring(0, p.indexOf("/") - 1);
              String na = p.substring(p.indexOf("/") + 1);
              try {
                store.setObjectiveNominalMagnification(new Integer(mag),
                  series, 0);
              }
              catch (NumberFormatException e) { }
              try {
                store.setObjectiveLensNA(new Float(na), series, 0);
              }
              catch (NumberFormatException e) { }
            }
            if (next < tokens.length) {
              store.setObjectiveImmersion(tokens[next++], series, 0);
            }
            else store.setObjectiveImmersion("Unknown", series, 0);
            boolean iris = false;
            if (next < tokens.length) {
              iris = tokens[next++].trim().equalsIgnoreCase("iris");
            }
            store.setObjectiveIris(new Boolean(iris), series, 0);
            store.setObjectiveCorrection("Unknown", series, 0);

            // link Objective to Image
            store.setObjectiveID("Objective:0", series, 0);
            store.setObjectiveSettingsObjective("Objective:0", series);

            break;
          case TRACK_ENTRY_TIME_BETWEEN_STACKS:
            try {
              store.setDimensionsTimeIncrement(new Float(v), series, 0);
            }
            catch (NumberFormatException e) { }
            break;
          case LASER_ENTRY_NAME:
            String medium = v;
            String laserType = "";

            if (medium.startsWith("HeNe")) {
              medium = "HeNe";
              laserType = "Gas";
            }
            else if (medium.startsWith("Argon")) {
              medium = "Ar";
              laserType = "Gas";
            }
            else if (medium.equals("Titanium:Sapphire") ||
              medium.equals("Mai Tai"))
            {
              medium = "TiSapphire";
              laserType = "SolidState";
            }
            else if (medium.equals("YAG")) {
              medium = "";
              laserType = "SolidState";
            }
            else if (medium.equals("Ar/Kr")) {
              medium = "";
              laserType = "Gas";
            }
            else if (medium.equals("Enterprise")) medium = "";

            laserData.put("medium " + nextLaserMedium, medium);
            laserData.put("type " + nextLaserType, laserType);

            nextLaserMedium++;
            nextLaserType++;

            break;
          //case LASER_POWER:
            // TODO: this is a setting, not a fixed value
            //store.setLaserPower(new Float(v), 0, count - 1);
            //break;
          case CHANNEL_ENTRY_DETECTOR_GAIN:
            //store.setDetectorSettingsGain(new Float(v), 0, nextGain++);
            break;
          case CHANNEL_ENTRY_PINHOLE_DIAMETER:
            try {
              channelData.put("pinhole " + count, new Float(v));
            }
            catch (NumberFormatException e) { }
            break;
          case ILLUM_CHANNEL_WAVELENGTH:
            try {
              Integer wave = new Integer((int) Float.parseFloat(v));
              channelData.put("em " + count, wave);
              channelData.put("ex " + count, wave);
            }
            catch (NumberFormatException e) { }
            break;
          case START_TIME:
            // date/time on which the first pixel was acquired, in days
            // since 30 December 1899
            double time = Double.parseDouble(v);
            store.setImageCreationDate(DataTools.convertDate(
              (long) (time * 86400000), DataTools.MICROSOFT), series);

            break;
          case DATA_CHANNEL_NAME:
            channelData.put("name " + count, v);
            break;
        }

        if (!done) done = in.getFilePointer() >= in.length() - 12;
      }
    }

    // set laser data

    int nextLaser = 0;

    for (int i=0; i<acquireLasers.size(); i++) {
      boolean acquire =
        ((Integer) acquireLasers.get(new Integer(i))).intValue() != 0;
      if (!acquire) continue;

      String medium = (String) laserData.get("medium " + i);
      String laserType = (String) laserData.get("type " + i);

      if (!medium.equals("") && !laserType.equals("")) {
        store.setLaserLaserMedium(medium, 0, nextLaser);
        store.setLaserType(laserType, 0, nextLaser);

        if (nextLaser < getEffectiveSizeC()) {
          // link Laser (LightSource) to Image
          store.setLightSourceID("LightSource:" + nextLaser, 0, nextLaser);
          store.setLightSourceSettingsLightSource("LightSource:" + nextLaser,
            series, nextLaser);
        }

        nextLaser++;
      }
    }

    // set channel data

    int nextChannel = 0;

    for (int i=0; i<acquireChannels.size(); i++) {
      boolean acquire =
        ((Integer) acquireChannels.get(new Integer(i + 1))).intValue() != 0;
      if (!acquire) continue;

      String name = (String) channelData.get("name " + (i + 3));
      Integer ex = (Integer) channelData.get("ex " + (i + 3));
      Integer em = (Integer) channelData.get("em " + (i + 3));
      Float pinhole = (Float) channelData.get("pinhole " + (i + 3));

      store.setLogicalChannelName(name, series, nextChannel);
      store.setLogicalChannelEmWave(em, series, nextChannel);
      store.setLogicalChannelExWave(ex, series, nextChannel);
      store.setLogicalChannelPinholeSize(pinhole, series, nextChannel);
      nextChannel++;
    }

    Float pixX = new Float((float) pixelSizeX);
    Float pixY = new Float((float) pixelSizeY);
    Float pixZ = new Float((float) pixelSizeZ);

    store.setDimensionsPhysicalSizeX(pixX, series, 0);
    store.setDimensionsPhysicalSizeY(pixY, series, 0);
    store.setDimensionsPhysicalSizeZ(pixZ, series, 0);

    float firstStamp =
      timestamps.size() == 0 ? 0f : ((Double) timestamps.get(0)).floatValue();

    for (int i=0; i<getImageCount(); i++) {
      int[] zct = FormatTools.getZCTCoords(this, i);

      if (zct[2] < timestamps.size()) {
        float thisStamp = ((Double) timestamps.get(zct[2])).floatValue();
        store.setPlaneTimingDeltaT(new Float(thisStamp - firstStamp), 0, 0, i);
        float nextStamp = zct[2] < getSizeT() - 1 ?
          ((Double) timestamps.get(zct[2] + 1)).floatValue() : thisStamp;
        if (i == getSizeT() - 1 && zct[2] > 0) {
          thisStamp = ((Double) timestamps.get(zct[2] - 1)).floatValue();
        }
        store.setPlaneTimingExposureTime(new Float(nextStamp - thisStamp),
          series, 0, i);
      }
    }
    in.close();
  }

  /** Parses overlay-related fields. */
  protected void parseOverlays(int series, long data, String suffix,
    MetadataStore store) throws IOException
  {
    if (data == 0) return;
    String prefix = "Series " + series + " ";

    in.seek(data);

    int numberOfShapes = in.readInt();
    int size = in.readInt();
    if (size <= 194) return;
    in.skipBytes(20);

    boolean valid = in.readInt() == 1;

    in.skipBytes(164);

    for (int i=0; i<numberOfShapes; i++) {
      long offset = in.getFilePointer();
      int type = in.readInt();
      int blockLength = in.readInt();
      int lineWidth = in.readInt();
      int measurements = in.readInt();
      double textOffsetX = in.readDouble();
      double textOffsetY = in.readDouble();
      int color = in.readInt();
      boolean validShape = in.readInt() != 0;
      int knotWidth = in.readInt();
      int catchArea = in.readInt();
      int fontHeight = in.readInt();
      int fontWidth = in.readInt();
      int fontEscapement = in.readInt();
      int fontOrientation = in.readInt();
      int fontWeight = in.readInt();
      boolean fontItalic = in.readInt() != 0;
      boolean fontUnderlined = in.readInt() != 0;
      boolean fontStrikeout = in.readInt() != 0;
      int fontCharSet = in.readInt();
      int fontOutputPrecision = in.readInt();
      int fontClipPrecision = in.readInt();
      int fontQuality = in.readInt();
      int fontPitchAndFamily = in.readInt();
      String fontName = DataTools.stripString(in.readString(64));
      boolean enabled = in.readShort() == 0;
      boolean moveable = in.readInt() == 0;
      in.skipBytes(34);

      switch (type) {
        case TEXT:
          double x = in.readDouble();
          double y = in.readDouble();
          String text = DataTools.stripString(in.readCString());
          //store.setShapeText(text, series, 0, i);
          break;
        case LINE:
          in.skipBytes(4);
          double startX = in.readDouble();
          double startY = in.readDouble();
          double endX = in.readDouble();
          double endY = in.readDouble();

          store.setLineX1(String.valueOf(startX), series, 0, i);
          store.setLineY1(String.valueOf(startY), series, 0, i);
          store.setLineX2(String.valueOf(endX), series, 0, i);
          store.setLineY2(String.valueOf(endY), series, 0, i);
          break;
        case SCALE_BAR:
        case OPEN_ARROW:
        case CLOSED_ARROW:
        case PALETTE:
          in.skipBytes(36);
          break;
        case RECTANGLE:
          in.skipBytes(4);
          double topX = in.readDouble();
          double topY = in.readDouble();
          double bottomX = in.readDouble();
          double bottomY = in.readDouble();
          double width = Math.abs(bottomX - topX);
          double height = Math.abs(bottomY - topY);

          topX = Math.min(topX, bottomX);
          topY = Math.min(topY, bottomY);

          store.setRectX(String.valueOf(topX), series, 0, i);
          store.setRectY(String.valueOf(topY), series, 0, i);
          store.setRectWidth(String.valueOf(width), series, 0, i);
          store.setRectHeight(String.valueOf(height), series, 0, i);

          break;
        case ELLIPSE:
          int knots = in.readInt();
          double[] xs = new double[knots];
          double[] ys = new double[knots];
          for (int j=0; j<xs.length; j++) {
            xs[j] = in.readDouble();
            ys[j] = in.readDouble();
          }

          double rx = 0, ry = 0, centerX = 0, centerY = 0;

          if (knots == 4) {
            double r1x = Math.abs(xs[2] - xs[0]) / 2;
            double r1y = Math.abs(ys[2] - ys[0]) / 2;
            double r2x = Math.abs(xs[3] - xs[1]) / 2;
            double r2y = Math.abs(ys[3] - ys[1]) / 2;

            if (r1x > r2x) {
              ry = r1y;
              rx = r2x;
              centerX = Math.min(xs[3], xs[1]) + rx;
              centerY = Math.min(ys[2], ys[0]) + ry;
            }
            else {
              ry = r2y;
              rx = r1x;
              centerX = Math.min(xs[2], xs[0]) + rx;
              centerY = Math.min(ys[3], ys[1]) + ry;
            }
          }
          else if (knots == 3) {
            centerX = xs[0];
            centerY = ys[0];

            rx = Math.sqrt(Math.pow(xs[1] - xs[0], 2) +
              Math.pow(ys[1] - ys[0], 2));
            ry = Math.sqrt(Math.pow(xs[2] - xs[0], 2) +
              Math.pow(ys[2] - ys[0], 2));

            // TODO: calculate and store rotation angle
          }

          store.setEllipseCx(String.valueOf(centerX), series, 0, i);
          store.setEllipseCy(String.valueOf(centerY), series, 0, i);
          store.setEllipseRx(String.valueOf(rx), series, 0, i);
          store.setEllipseRy(String.valueOf(ry), series, 0, i);

          break;
        case CIRCLE:
          in.skipBytes(4);
          centerX = in.readDouble();
          centerY = in.readDouble();
          double curveX = in.readDouble();
          double curveY = in.readDouble();

          double radius = Math.sqrt(Math.pow(curveX - centerX, 2) +
            Math.pow(curveY - centerY, 2));

          store.setCircleCx(String.valueOf(centerX), series, 0, i);
          store.setCircleCy(String.valueOf(centerY), series, 0, i);
          store.setCircleR(String.valueOf(radius), series, 0, i);

          break;
        case CIRCLE_3POINT:
          in.skipBytes(4);
          double[][] points = new double[3][2];
          for (int j=0; j<points.length; j++) {
            for (int k=0; k<points[j].length; k++) {
              points[j][k] = in.readDouble();
            }
          }

          // TODO : calculate center and radius from 3 points on perimeter

          break;
        case ANGLE:
          in.skipBytes(4);
          points = new double[3][2];
          for (int j=0; j<points.length; j++) {
            for (int k=0; k<points[j].length; k++) {
              points[j][k] = in.readDouble();
            }
          }

          StringBuffer p = new StringBuffer();
          for (int j=0; j<points.length; j++) {
            p.append(points[j][0]);
            p.append(",");
            p.append(points[j][1]);
            if (j < points.length - 1) p.append(" ");
          }

          store.setPolylinePoints(p.toString(), series, 0, i);

          break;
        case CLOSED_POLYLINE:
        case OPEN_POLYLINE:
        case POLYLINE_ARROW:
          int nKnots = in.readInt();
          points = new double[nKnots][2];
          for (int j=0; j<points.length; j++) {
            for (int k=0; k<points[j].length; k++) {
              points[j][k] = in.readDouble();
            }
          }

          p = new StringBuffer();
          for (int j=0; j<points.length; j++) {
            p.append(points[j][0]);
            p.append(",");
            p.append(points[j][1]);
            if (j < points.length - 1) p.append(" ");
          }

          if (type == CLOSED_POLYLINE) {
            store.setPolygonPoints(p.toString(), series, 0, i);
          }
          else store.setPolylinePoints(p.toString(), series, 0, i);

          break;
        case CLOSED_BEZIER:
        case OPEN_BEZIER:
        case BEZIER_WITH_ARROW:
          nKnots = in.readInt();
          points = new double[nKnots][2];
          for (int j=0; j<points.length; j++) {
            for (int k=0; k<points[j].length; k++) {
              points[j][k] = in.readDouble();
            }
          }
          break;
      }

      in.seek(offset + blockLength);
    }
  }

  /** Construct a metadata key from the given stack. */
  private String getKey(Stack stack, int entry) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<stack.size(); i++) {
      sb.append((String) stack.get(i));
      sb.append("/");
    }
    sb.append(" - ");

    sb.append(metadataKeys.get(new Integer(entry)));
    return sb.toString();
  }

  /** Parse a .mdb file and return a list of referenced .lsm files. */
  private String[] parseMDB(String mdbFile) throws FormatException {
    Location mdb = new Location(mdbFile).getAbsoluteFile();
    Location parent = mdb.getParentFile();
    Vector[] tables = MDBParser.parseDatabase(mdbFile);
    Vector referencedLSMs = new Vector();

    for (int table=0; table<tables.length; table++) {
      String[] columnNames = (String[]) tables[table].get(0);
      String tableName = columnNames[0];

      for (int row=1; row<tables[table].size(); row++) {
        String[] tableRow = (String[]) tables[table].get(row);
        for (int col=0; col<tableRow.length; col++) {
          String key = tableName + " " + columnNames[col + 1] + " " + row;
          addMeta(key, tableRow[col]);

          if (tableName.equals("Recordings") && columnNames[col + 1] != null &&
            columnNames[col + 1].equals("SampleData"))
          {
            String filename = tableRow[col].trim();
            filename = filename.replace('\\', File.separatorChar);
            filename = filename.replaceAll("/", File.separator);
            filename =
              filename.substring(filename.lastIndexOf(File.separator) + 1);
            if (filename.length() > 0) {
              Location file = new Location(parent, filename);
              if (file.exists()) {
                referencedLSMs.add(file.getAbsolutePath());
              }
            }
          }
        }
      }
    }

    if (referencedLSMs.size() > 0) {
      return (String[]) referencedLSMs.toArray(new String[0]);
    }

    String[] fileList = parent.list();
    for (int i=0; i<fileList.length; i++) {
      if (checkSuffix(fileList[i], new String[] {"lsm"})) {
        referencedLSMs.add(new Location(parent, fileList[i]).getAbsolutePath());
      }
    }
    return (String[]) referencedLSMs.toArray(new String[0]);
  }

  private static Hashtable createKeys() {
    Hashtable h = new Hashtable();
    h.put(new Integer(0x10000001), "Name");
    h.put(new Integer(0x4000000c), "Name");
    h.put(new Integer(0x50000001), "Name");
    h.put(new Integer(0x90000001), "Name");
    h.put(new Integer(0x90000005), "Name");
    h.put(new Integer(0xb0000003), "Name");
    h.put(new Integer(0xd0000001), "Name");
    h.put(new Integer(0x12000001), "Name");
    h.put(new Integer(0x14000001), "Name");
    h.put(new Integer(0x10000002), "Description");
    h.put(new Integer(0x14000002), "Description");
    h.put(new Integer(0x10000003), "Notes");
    h.put(new Integer(0x10000004), "Objective");
    h.put(new Integer(0x10000005), "Processing Summary");
    h.put(new Integer(0x10000006), "Special Scan Mode");
    h.put(new Integer(0x10000007), "Scan Type");
    h.put(new Integer(0x10000008), "Scan Mode");
    h.put(new Integer(0x10000009), "Number of Stacks");
    h.put(new Integer(0x1000000a), "Lines Per Plane");
    h.put(new Integer(0x1000000b), "Samples Per Line");
    h.put(new Integer(0x1000000c), "Planes Per Volume");
    h.put(new Integer(0x1000000d), "Images Width");
    h.put(new Integer(0x1000000e), "Images Height");
    h.put(new Integer(0x1000000f), "Number of Planes");
    h.put(new Integer(0x10000010), "Number of Stacks");
    h.put(new Integer(0x10000011), "Number of Channels");
    h.put(new Integer(0x10000012), "Linescan XY Size");
    h.put(new Integer(0x10000013), "Scan Direction");
    h.put(new Integer(0x10000014), "Time Series");
    h.put(new Integer(0x10000015), "Original Scan Data");
    h.put(new Integer(0x10000016), "Zoom X");
    h.put(new Integer(0x10000017), "Zoom Y");
    h.put(new Integer(0x10000018), "Zoom Z");
    h.put(new Integer(0x10000019), "Sample 0X");
    h.put(new Integer(0x1000001a), "Sample 0Y");
    h.put(new Integer(0x1000001b), "Sample 0Z");
    h.put(new Integer(0x1000001c), "Sample Spacing");
    h.put(new Integer(0x1000001d), "Line Spacing");
    h.put(new Integer(0x1000001e), "Plane Spacing");
    h.put(new Integer(0x1000001f), "Plane Width");
    h.put(new Integer(0x10000020), "Plane Height");
    h.put(new Integer(0x10000021), "Volume Depth");
    h.put(new Integer(0x10000034), "Rotation");
    h.put(new Integer(0x10000035), "Precession");
    h.put(new Integer(0x10000036), "Sample 0Time");
    h.put(new Integer(0x10000037), "Start Scan Trigger In");
    h.put(new Integer(0x10000038), "Start Scan Trigger Out");
    h.put(new Integer(0x10000039), "Start Scan Event");
    h.put(new Integer(0x10000040), "Start Scan Time");
    h.put(new Integer(0x10000041), "Stop Scan Trigger In");
    h.put(new Integer(0x10000042), "Stop Scan Trigger Out");
    h.put(new Integer(0x10000043), "Stop Scan Event");
    h.put(new Integer(0x10000044), "Stop Scan Time");
    h.put(new Integer(0x10000045), "Use ROIs");
    h.put(new Integer(0x10000046), "Use Reduced Memory ROIs");
    h.put(new Integer(0x10000047), "User");
    h.put(new Integer(0x10000048), "Use B/C Correction");
    h.put(new Integer(0x10000049), "Position B/C Contrast 1");
    h.put(new Integer(0x10000050), "Position B/C Contrast 2");
    h.put(new Integer(0x10000051), "Interpolation Y");
    h.put(new Integer(0x10000052), "Camera Binning");
    h.put(new Integer(0x10000053), "Camera Supersampling");
    h.put(new Integer(0x10000054), "Camera Frame Width");
    h.put(new Integer(0x10000055), "Camera Frame Height");
    h.put(new Integer(0x10000056), "Camera Offset X");
    h.put(new Integer(0x10000057), "Camera Offset Y");
    h.put(new Integer(0x40000001), "Multiplex Type");
    h.put(new Integer(0x40000002), "Multiplex Order");
    h.put(new Integer(0x40000003), "Sampling Mode");
    h.put(new Integer(0x40000004), "Sampling Method");
    h.put(new Integer(0x40000005), "Sampling Number");
    h.put(new Integer(0x40000006), "Acquire");
    h.put(new Integer(0x50000002), "Acquire");
    h.put(new Integer(0x7000000b), "Acquire");
    h.put(new Integer(0x90000004), "Acquire");
    h.put(new Integer(0xd0000017), "Acquire");
    h.put(new Integer(0x40000007), "Sample Observation Time");
    h.put(new Integer(0x40000008), "Time Between Stacks");
    h.put(new Integer(0x4000000d), "Collimator 1 Name");
    h.put(new Integer(0x4000000e), "Collimator 1 Position");
    h.put(new Integer(0x4000000f), "Collimator 2 Name");
    h.put(new Integer(0x40000010), "Collimator 2 Position");
    h.put(new Integer(0x40000011), "Is Bleach Track");
    h.put(new Integer(0x40000012), "Bleach After Scan Number");
    h.put(new Integer(0x40000013), "Bleach Scan Number");
    h.put(new Integer(0x40000014), "Trigger In");
    h.put(new Integer(0x12000004), "Trigger In");
    h.put(new Integer(0x14000003), "Trigger In");
    h.put(new Integer(0x40000015), "Trigger Out");
    h.put(new Integer(0x12000005), "Trigger Out");
    h.put(new Integer(0x14000004), "Trigger Out");
    h.put(new Integer(0x40000016), "Is Ratio Track");
    h.put(new Integer(0x40000017), "Bleach Count");
    h.put(new Integer(0x40000018), "SPI Center Wavelength");
    h.put(new Integer(0x40000019), "Pixel Time");
    h.put(new Integer(0x40000020), "ID Condensor Frontlens");
    h.put(new Integer(0x40000021), "Condensor Frontlens");
    h.put(new Integer(0x40000022), "ID Field Stop");
    h.put(new Integer(0x40000023), "Field Stop Value");
    h.put(new Integer(0x40000024), "ID Condensor Aperture");
    h.put(new Integer(0x40000025), "Condensor Aperture");
    h.put(new Integer(0x40000026), "ID Condensor Revolver");
    h.put(new Integer(0x40000027), "Condensor Revolver");
    h.put(new Integer(0x40000028), "ID Transmission Filter 1");
    h.put(new Integer(0x40000029), "ID Transmission 1");
    h.put(new Integer(0x40000030), "ID Transmission Filter 2");
    h.put(new Integer(0x40000031), "ID Transmission 2");
    h.put(new Integer(0x40000032), "Repeat Bleach");
    h.put(new Integer(0x40000033), "Enable Spot Bleach Pos");
    h.put(new Integer(0x40000034), "Spot Bleach Position X");
    h.put(new Integer(0x40000035), "Spot Bleach Position Y");
    h.put(new Integer(0x40000036), "Bleach Position Z");
    h.put(new Integer(0x50000003), "Power");
    h.put(new Integer(0x90000002), "Power");
    h.put(new Integer(0x70000003), "Detector Gain");
    h.put(new Integer(0x70000005), "Amplifier Gain");
    h.put(new Integer(0x70000007), "Amplifier Offset");
    h.put(new Integer(0x70000009), "Pinhole Diameter");
    h.put(new Integer(0x7000000c), "Detector Name");
    h.put(new Integer(0x7000000d), "Amplifier Name");
    h.put(new Integer(0x7000000e), "Pinhole Name");
    h.put(new Integer(0x7000000f), "Filter Set Name");
    h.put(new Integer(0x70000010), "Filter Name");
    h.put(new Integer(0x70000013), "Integrator Name");
    h.put(new Integer(0x70000014), "Detection Channel Name");
    h.put(new Integer(0x70000015), "Detector Gain B/C 1");
    h.put(new Integer(0x70000016), "Detector Gain B/C 2");
    h.put(new Integer(0x70000017), "Amplifier Gain B/C 1");
    h.put(new Integer(0x70000018), "Amplifier Gain B/C 2");
    h.put(new Integer(0x70000019), "Amplifier Offset B/C 1");
    h.put(new Integer(0x70000020), "Amplifier Offset B/C 2");
    h.put(new Integer(0x70000021), "Spectral Scan Channels");
    h.put(new Integer(0x70000022), "SPI Wavelength Start");
    h.put(new Integer(0x70000023), "SPI Wavelength End");
    h.put(new Integer(0x70000026), "Dye Name");
    h.put(new Integer(0xd0000014), "Dye Name");
    h.put(new Integer(0x70000027), "Dye Folder");
    h.put(new Integer(0xd0000015), "Dye Folder");
    h.put(new Integer(0x90000003), "Wavelength");
    h.put(new Integer(0x90000006), "Power B/C 1");
    h.put(new Integer(0x90000007), "Power B/C 2");
    h.put(new Integer(0xb0000001), "Filter Set");
    h.put(new Integer(0xb0000002), "Filter");
    h.put(new Integer(0xd0000004), "Color");
    h.put(new Integer(0xd0000005), "Sample Type");
    h.put(new Integer(0xd0000006), "Bits Per Sample");
    h.put(new Integer(0xd0000007), "Ratio Type");
    h.put(new Integer(0xd0000008), "Ratio Track 1");
    h.put(new Integer(0xd0000009), "Ratio Track 2");
    h.put(new Integer(0xd000000a), "Ratio Channel 1");
    h.put(new Integer(0xd000000b), "Ratio Channel 2");
    h.put(new Integer(0xd000000c), "Ratio Const. 1");
    h.put(new Integer(0xd000000d), "Ratio Const. 2");
    h.put(new Integer(0xd000000e), "Ratio Const. 3");
    h.put(new Integer(0xd000000f), "Ratio Const. 4");
    h.put(new Integer(0xd0000010), "Ratio Const. 5");
    h.put(new Integer(0xd0000011), "Ratio Const. 6");
    h.put(new Integer(0xd0000012), "Ratio First Images 1");
    h.put(new Integer(0xd0000013), "Ratio First Images 2");
    h.put(new Integer(0xd0000016), "Spectrum");
    h.put(new Integer(0x12000003), "Interval");
    return h;
  }

}
