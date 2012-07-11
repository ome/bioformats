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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffConstants;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffTools;

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
  private static final int SUBBLOCK_LASER = 0x50000000;
  private static final int SUBBLOCK_TRACK = 0x40000000;
  private static final int SUBBLOCK_DETECTION_CHANNEL = 0x70000000;
  private static final int SUBBLOCK_ILLUMINATION_CHANNEL = 0x90000000;
  private static final int SUBBLOCK_BEAM_SPLITTER = 0xb0000000;
  private static final int SUBBLOCK_DATA_CHANNEL = 0xd0000000;
  private static final int SUBBLOCK_TIMER = 0x12000000;
  private static final int SUBBLOCK_MARKER = 0x14000000;
  private static final int SUBBLOCK_END = (int) 0xffffffff;

  /** Data types. */
  private static final int RECORDING_NAME = 0x10000001;
  private static final int RECORDING_DESCRIPTION = 0x10000002;
  private static final int RECORDING_OBJECTIVE = 0x10000004;
  private static final int RECORDING_ZOOM = 0x10000016;
  private static final int RECORDING_SAMPLE_0TIME = 0x10000036;
  private static final int RECORDING_CAMERA_BINNING = 0x10000052;

  private static final int TRACK_ACQUIRE = 0x40000006;
  private static final int TRACK_TIME_BETWEEN_STACKS = 0x4000000b;

  private static final int LASER_NAME = 0x50000001;
  private static final int LASER_ACQUIRE = 0x50000002;
  private static final int LASER_POWER = 0x50000003;

  private static final int CHANNEL_DETECTOR_GAIN = 0x70000003;
  private static final int CHANNEL_PINHOLE_DIAMETER = 0x70000009;
  private static final int CHANNEL_AMPLIFIER_GAIN = 0x70000005;
  private static final int CHANNEL_FILTER_SET = 0x7000000f;
  private static final int CHANNEL_FILTER = 0x70000010;
  private static final int CHANNEL_ACQUIRE = 0x7000000b;
  private static final int CHANNEL_NAME = 0x70000014;

  private static final int ILLUM_CHANNEL_NAME = 0x90000001;
  private static final int ILLUM_CHANNEL_ATTENUATION = 0x90000002;
  private static final int ILLUM_CHANNEL_WAVELENGTH = 0x90000003;
  private static final int ILLUM_CHANNEL_ACQUIRE = 0x90000004;

  private static final int START_TIME = 0x10000036;
  private static final int DATA_CHANNEL_NAME = 0xd0000001;
  private static final int DATA_CHANNEL_ACQUIRE = 0xd0000017;

  private static final int BEAM_SPLITTER_FILTER = 0xb0000002;
  private static final int BEAM_SPLITTER_FILTER_SET = 0xb0000003;

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

  private static Hashtable<Integer, String> metadataKeys = createKeys();

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private byte[][] lut = null;
  private Vector<Double> timestamps;
  private int validChannels;

  private String[] lsmFilenames;
  private Vector<IFDList> ifdsList;
  private TiffParser tiffParser;

  private int nextLaser = 0, nextDetector = 0;
  private int nextFilter = 0, nextFilterSet = 0;
  private int nextDataChannel = 0, nextIllumChannel = 0, nextDetectChannel = 0;
  private boolean splitPlanes = false;
  private double zoom;
  private Vector<String> imageNames;
  private String binning;
  private String userName;

  private int totalROIs = 0;

  // -- Constructor --

  /** Constructs a new Zeiss LSM reader. */
  public ZeissLSMReader() {
    super("Zeiss Laser-Scanning Microscopy", new String[] {"lsm", "mdb"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, MDB_SUFFIX)) return false;
    if (isGroupFiles()) {
      // look for an .mdb file
      Location parentFile = new Location(id).getAbsoluteFile().getParentFile();
      String[] fileList = parentFile.list();
      for (int i=0; i<fileList.length; i++) {
        if (fileList[i].startsWith(".")) continue;
        if (checkSuffix(fileList[i], MDB_SUFFIX)) {
          Location file =
            new Location(parentFile, fileList[i]).getAbsoluteFile();
          if (file.isDirectory()) continue;
          // make sure that the .mdb references this .lsm
          String[] lsms = parseMDB(file.getAbsolutePath());
          for (String lsm : lsms) {
            if (id.endsWith(lsm) || lsm.endsWith(id)) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelSizeX = pixelSizeY = pixelSizeZ = 0;
      lut = null;
      timestamps = null;
      validChannels = 0;
      lsmFilenames = null;
      ifdsList = null;
      tiffParser = null;
      nextLaser = nextDetector = 0;
      nextFilter = nextFilterSet = 0;
      nextDataChannel = nextIllumChannel = nextDetectChannel = 0;
      splitPlanes = false;
      zoom = 0;
      imageNames = null;
      binning = null;
      totalROIs = 0;
      userName = null;
    }
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    byte[] check = new byte[blockLen];
    stream.readFully(check);
    return TiffTools.isValidHeader(check) ||
      (check[2] == 0x53 && check[3] == 0x74);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      if (checkSuffix(currentId, MDB_SUFFIX)) return new String[] {currentId};
      return null;
    }
    if (lsmFilenames == null) return new String[] {currentId};
    if (lsmFilenames.length == 1 && currentId.equals(lsmFilenames[0])) {
      return lsmFilenames;
    }
    return new String[] {currentId, lsmFilenames[getSeries()]};
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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in = new RandomAccessInputStream(lsmFilenames[getSeries()]);
    in.order(!isLittleEndian());

    tiffParser = new TiffParser(in);

    IFDList ifds = ifdsList.get(getSeries());

    if (splitPlanes && getSizeC() > 1 && ifds.size() == getSizeZ() * getSizeT())
    {
      int plane = no / getSizeC();
      int c = no % getSizeC();
      byte[][] samples = tiffParser.getSamples(ifds.get(plane), x, y, w, h);
      System.arraycopy(samples[c], 0, buf, 0, buf.length);
    }
    else tiffParser.getSamples(ifds.get(no), buf, x, y, w, h);
    in.close();
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("ZeissLSMReader.initFile(" + id + ")");
    super.initFile(id);

    if (!checkSuffix(id, MDB_SUFFIX) && isGroupFiles()) {
      // look for an .mdb file
      Location parentFile = new Location(id).getAbsoluteFile().getParentFile();
      String[] fileList = parentFile.list();
      for (int i=0; i<fileList.length; i++) {
        if (fileList[i].startsWith(".")) continue;
        if (checkSuffix(fileList[i], MDB_SUFFIX)) {
          Location file =
            new Location(parentFile, fileList[i]).getAbsoluteFile();
          if (file.isDirectory()) continue;
          // make sure that the .mdb references this .lsm
          String[] lsms = parseMDB(file.getAbsolutePath());
          for (String lsm : lsms) {
            if (id.endsWith(lsm) || lsm.endsWith(id)) {
              setId(file.getAbsolutePath());
              return;
            }
          }
        }
      }
      lsmFilenames = new String[] {id};
    }
    else if (checkSuffix(id, MDB_SUFFIX)) {
      lsmFilenames = parseMDB(id);
    }
    else lsmFilenames = new String[] {id};

    if (lsmFilenames.length == 0) {
      throw new FormatException("LSM files were not found.");
    }

    timestamps = new Vector<Double>();
    imageNames = new Vector<String>();

    core = new CoreMetadata[lsmFilenames.length];
    ifdsList = new Vector<IFDList>();
    ifdsList.setSize(core.length);
    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
      RandomAccessInputStream s = new RandomAccessInputStream(lsmFilenames[i]);
      core[i].littleEndian = s.read() == TiffConstants.LITTLE;
      s.order(isLittleEndian());
      s.seek(0);
      ifdsList.set(i, new TiffParser(s).getIFDs(true));
      s.close();
    }

    status("Removing thumbnails");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int series=0; series<ifdsList.size(); series++) {
      IFDList ifds = ifdsList.get(series);
      for (int i=0; i<ifds.size(); i++) {
        IFD ifd = ifds.get(i);

        // check that predictor is set to 1 if anything other
        // than LZW compression is used
        if (ifd.getCompression() != TiffCompression.LZW) {
          ifd.put(new Integer(IFD.PREDICTOR), new Integer(1));
        }
      }

      // fix the offsets for > 4 GB files
      for (int i=1; i<ifds.size(); i++) {
        long[] stripOffsets = ifds.get(i).getStripOffsets();
        long[] previousStripOffsets = ifds.get(i - 1).getStripOffsets();
        boolean neededAdjustment = false;
        for (int j=0; j<stripOffsets.length; j++) {
          if (stripOffsets[j] < previousStripOffsets[j]) {
            stripOffsets[j] = (previousStripOffsets[j] & ~0xffffffffL) |
              (stripOffsets[j] & 0xffffffffL);
            if (stripOffsets[j] < previousStripOffsets[j]) {
              stripOffsets[j] += 0x100000000L;
            }
            neededAdjustment = true;
          }
          if (neededAdjustment) {
            ifds.get(i).putIFDValue(IFD.STRIP_OFFSETS, stripOffsets);
          }
        }
      }

      initMetadata(series);
      store.setPixelsBigEndian(new Boolean(!isLittleEndian()), series, 0);
    }
    for (int series=0; series<ifdsList.size(); series++) {
      store.setImageName(imageNames.get(series), series);
    }
    setSeries(0);
  }

  // -- Helper methods --

  protected void initMetadata(int series) throws FormatException, IOException {
    setSeries(series);
    IFDList ifds = ifdsList.get(series);
    IFD ifd = ifds.get(0);

    in = new RandomAccessInputStream(lsmFilenames[series]);
    in.order(isLittleEndian());

    tiffParser = new TiffParser(in);

    int photo = ifd.getPhotometricInterpretation();
    int samples = ifd.getSamplesPerPixel();

    core[series].sizeX = (int) ifd.getImageWidth();
    core[series].sizeY = (int) ifd.getImageLength();
    core[series].rgb = samples > 1 || photo == PhotoInterp.RGB;
    core[series].interleaved = false;
    core[series].sizeC = isRGB() ? samples : 1;
    core[series].pixelType = ifd.getPixelType();
    core[series].imageCount = ifds.size();
    core[series].sizeZ = getImageCount();
    core[series].sizeT = 1;

    status("Reading LSM metadata for series #" + series);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    String imageName = lsmFilenames[series];
    if (imageName.indexOf(".") != -1) {
      imageName = imageName.substring(0, imageName.lastIndexOf("."));
    }
    if (imageName.indexOf(File.separator) != -1) {
      imageName =
        imageName.substring(imageName.lastIndexOf(File.separator) + 1);
    }

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", series);
    store.setInstrumentID(instrumentID, series);
    store.setImageInstrumentRef(instrumentID, series);

    // get TIF_CZ_LSMINFO structure
    short[] s = ifd.getIFDShortArray(ZEISS_ID, true);
    byte[] cz = new byte[s.length];
    for (int i=0; i<s.length; i++) {
      cz[i] = (byte) s[i];
    }

    RandomAccessInputStream ras = new RandomAccessInputStream(cz);
    ras.order(isLittleEndian());

    addSeriesMeta("MagicNumber ", ras.readInt());
    addSeriesMeta("StructureSize", ras.readInt());
    addSeriesMeta("DimensionX", ras.readInt());
    addSeriesMeta("DimensionY", ras.readInt());

    core[series].sizeZ = ras.readInt();
    ras.skipBytes(4);
    core[series].sizeT = ras.readInt();

    int dataType = ras.readInt();
    switch (dataType) {
      case 2:
        addSeriesMeta("DataType", "12 bit unsigned integer");
        break;
      case 5:
        addSeriesMeta("DataType", "32 bit float");
        break;
      case 0:
        addSeriesMeta("DataType", "varying data types");
        break;
      default:
        addSeriesMeta("DataType", "8 bit unsigned integer");
    }

    addSeriesMeta("ThumbnailX", ras.readInt());
    addSeriesMeta("ThumbnailY", ras.readInt());

    // pixel sizes are stored in meters, we need them in microns
    pixelSizeX = ras.readDouble() * 1000000;
    pixelSizeY = ras.readDouble() * 1000000;
    pixelSizeZ = ras.readDouble() * 1000000;

    addSeriesMeta("VoxelSizeX", new Double(pixelSizeX));
    addSeriesMeta("VoxelSizeY", new Double(pixelSizeY));
    addSeriesMeta("VoxelSizeZ", new Double(pixelSizeZ));

    addSeriesMeta("OriginX", ras.readDouble());
    addSeriesMeta("OriginY", ras.readDouble());
    addSeriesMeta("OriginZ", ras.readDouble());

    int scanType = ras.readShort();
    switch (scanType) {
      case 0:
        addSeriesMeta("ScanType", "x-y-z scan");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 1:
        addSeriesMeta("ScanType", "z scan (x-z plane)");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 2:
        addSeriesMeta("ScanType", "line scan");
        core[series].dimensionOrder = "XYZCT";
        break;
      case 3:
        addSeriesMeta("ScanType", "time series x-y");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 4:
        addSeriesMeta("ScanType", "time series x-z");
        core[series].dimensionOrder = "XYZTC";
        break;
      case 5:
        addSeriesMeta("ScanType", "time series 'Mean of ROIs'");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 6:
        addSeriesMeta("ScanType", "time series x-y-z");
        core[series].dimensionOrder = "XYZTC";
        break;
      case 7:
        addSeriesMeta("ScanType", "spline scan");
        core[series].dimensionOrder = "XYCTZ";
        break;
      case 8:
        addSeriesMeta("ScanType", "spline scan x-z");
        core[series].dimensionOrder = "XYCZT";
        break;
      case 9:
        addSeriesMeta("ScanType", "time series spline plane x-z");
        core[series].dimensionOrder = "XYTCZ";
        break;
      case 10:
        addSeriesMeta("ScanType", "point mode");
        core[series].dimensionOrder = "XYZCT";
        break;
      default:
        addSeriesMeta("ScanType", "x-y-z scan");
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

    if (getImageCount() != ifds.size()) {
      int diff = getImageCount() - ifds.size();
      core[series].imageCount = ifds.size();
      if (diff % getSizeZ() == 0) {
        core[series].sizeT -= (diff / getSizeZ());
      }
      else if (diff % getSizeT() == 0) {
        core[series].sizeZ -= (diff / getSizeT());
      }
      else if (getSizeZ() > 1) {
        core[series].sizeZ = ifds.size();
        core[series].sizeT = 1;
      }
      else if (getSizeT() > 1) {
        core[series].sizeT = ifds.size();
        core[series].sizeZ = 1;
      }
    }

    if (getSizeZ() == 0) core[series].sizeZ = getImageCount();
    if (getSizeT() == 0) core[series].sizeT = getImageCount() / getSizeZ();

    MetadataTools.setDefaultCreationDate(store, getCurrentFile(), series);

    int spectralScan = ras.readShort();
    if (spectralScan != 1) {
      addSeriesMeta("SpectralScan", "no spectral scan");
    }
    else addSeriesMeta("SpectralScan", "acquired with spectral scan");

    int type = ras.readInt();
    switch (type) {
      case 1:
        addSeriesMeta("DataType2", "calculated data");
        break;
      case 2:
        addSeriesMeta("DataType2", "animation");
        break;
      default:
        addSeriesMeta("DataType2", "original scan data");
    }

    long[] overlayOffsets = new long[9];
    String[] overlayKeys = new String[] {"VectorOverlay", "InputLut",
      "OutputLut", "ROI", "BleachROI", "MeanOfRoisOverlay",
      "TopoIsolineOverlay", "TopoProfileOverlay", "LinescanOverlay"};

    overlayOffsets[0] = ras.readInt();
    overlayOffsets[1] = ras.readInt();
    overlayOffsets[2] = ras.readInt();

    long channelColorsOffset = ras.readInt();

    addSeriesMeta("TimeInterval", ras.readDouble());
    ras.skipBytes(4);
    long scanInformationOffset = ras.readInt();
    ras.skipBytes(4);
    long timeStampOffset = ras.readInt();
    long eventListOffset = ras.readInt();
    overlayOffsets[3] = ras.readInt();
    overlayOffsets[4] = ras.readInt();
    ras.skipBytes(4);

    addSeriesMeta("DisplayAspectX", ras.readDouble());
    addSeriesMeta("DisplayAspectY", ras.readDouble());
    addSeriesMeta("DisplayAspectZ", ras.readDouble());
    addSeriesMeta("DisplayAspectTime", ras.readDouble());

    overlayOffsets[5] = ras.readInt();
    overlayOffsets[6] = ras.readInt();
    overlayOffsets[7] = ras.readInt();
    overlayOffsets[8] = ras.readInt();

    for (int i=0; i<overlayOffsets.length; i++) {
      parseOverlays(series, overlayOffsets[i], overlayKeys[i], store);
    }

    totalROIs = 0;

    addSeriesMeta("ToolbarFlags", ras.readInt());

    int wavelengthOffset = ras.readInt();

    ras.close();

    // read referenced structures

    addSeriesMeta("DimensionZ", getSizeZ());
    addSeriesMeta("DimensionChannels", getSizeC());

    if (channelColorsOffset != 0) {
      in.seek(channelColorsOffset + 16);
      int namesOffset = in.readInt();

      // read the name of each channel

      if (namesOffset > 0) {
        in.skipBytes(namesOffset - 16);

        for (int i=0; i<getSizeC(); i++) {
          if (in.getFilePointer() >= in.length() - 1) break;
          // we want to read until we find a null char
          String name = in.readCString();
          if (name.length() <= 128) {
            addSeriesMeta("ChannelName" + i, name);
          }
        }
      }
    }

    if (timeStampOffset != 0) {
      in.seek(timeStampOffset + 8);
      for (int i=0; i<getSizeT(); i++) {
        double stamp = in.readDouble();
        addSeriesMeta("TimeStamp" + i, stamp);
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
          addSeriesMeta("Event" + i + " Time", eventTime);
          addSeriesMeta("Event" + i + " Type", eventType);
          long fp = in.getFilePointer();
          int len = size - 16;
          if (len > 65536) len = 65536;
          if (len < 0) len = 0;
          addSeriesMeta("Event" + i + " Description", in.readString(len));
          in.seek(fp + size - 16);
          if (in.getFilePointer() < 0) break;
        }
      }
    }

    if (scanInformationOffset != 0) {
      in.seek(scanInformationOffset);

      nextLaser = nextDetector = 0;
      nextFilter = nextFilterSet = 0;
      nextDataChannel = nextDetectChannel = nextIllumChannel = 0;

      Vector<SubBlock> blocks = new Vector<SubBlock>();

      while (in.getFilePointer() < in.length() - 12) {
        if (in.getFilePointer() < 0) break;
        int entry = in.readInt();
        int blockType = in.readInt();
        int dataSize = in.readInt();

        if (blockType == TYPE_SUBBLOCK) {
          SubBlock block = null;
          switch (entry) {
            case SUBBLOCK_RECORDING:
              block = new Recording();
              break;
            case SUBBLOCK_LASER:
              block = new Laser();
              break;
            case SUBBLOCK_TRACK:
              block = new Track();
              break;
            case SUBBLOCK_DETECTION_CHANNEL:
              block = new DetectionChannel();
              break;
            case SUBBLOCK_ILLUMINATION_CHANNEL:
              block = new IlluminationChannel();
              break;
            case SUBBLOCK_BEAM_SPLITTER:
              block = new BeamSplitter();
              break;
            case SUBBLOCK_DATA_CHANNEL:
              block = new DataChannel();
              break;
            case SUBBLOCK_TIMER:
              block = new Timer();
              break;
            case SUBBLOCK_MARKER:
              block = new Marker();
              break;
          }
          if (block != null) {
            blocks.add(block);
          }
        }
        else in.skipBytes(dataSize);
      }

      Vector<SubBlock> nonAcquiredBlocks = new Vector<SubBlock>();

      SubBlock[] metadataBlocks = blocks.toArray(new SubBlock[0]);
      for (SubBlock block : metadataBlocks) {
        block.addToHashtable();
        if (!block.acquire) {
          nonAcquiredBlocks.add(block);
          blocks.remove(block);
        }
      }

      for (int i=0; i<blocks.size(); i++) {
        SubBlock block = blocks.get(i);
        // every valid IlluminationChannel must be immediately followed by
        // a valid DataChannel or IlluminationChannel
        if ((block instanceof IlluminationChannel) && i < blocks.size() - 1) {
          SubBlock nextBlock = blocks.get(i + 1);
          if (!(nextBlock instanceof DataChannel) &&
            !(nextBlock instanceof IlluminationChannel))
          {
            ((IlluminationChannel) block).wavelength = null;
          }
        }
        // every valid DetectionChannel must be immediately preceded by
        // a valid Track or DetectionChannel
        else if ((block instanceof DetectionChannel) && i > 0) {
          SubBlock prevBlock = blocks.get(i - 1);
          if (!(prevBlock instanceof Track) &&
            !(prevBlock instanceof DetectionChannel))
          {
            block.acquire = false;
            nonAcquiredBlocks.add(block);
          }
        }
        if (block.acquire) populateMetadataStore(block, store, series);
      }

      for (SubBlock block : nonAcquiredBlocks) {
        populateMetadataStore(block, store, series);
      }
    }
    int nLogicalChannels = nextDataChannel == 0 ? 1 : nextDataChannel;
    if (nLogicalChannels == getSizeC() || nextDataChannel == 0) {
      if (!splitPlanes) splitPlanes = isRGB();
      core[series].rgb = false;
      if (splitPlanes) core[series].imageCount *= getSizeC();
    }
    else if (splitPlanes) {
      core[series].rgb = false;
      if (splitPlanes) core[series].imageCount *= getSizeC();
    }

    MetadataTools.populatePixels(store, this, true);

    imageNames.add(imageName);

    Float pixX = new Float(pixelSizeX);
    Float pixY = new Float(pixelSizeY);
    Float pixZ = new Float(pixelSizeZ);

    store.setDimensionsPhysicalSizeX(pixX, series, 0);
    store.setDimensionsPhysicalSizeY(pixY, series, 0);
    store.setDimensionsPhysicalSizeZ(pixZ, series, 0);

    double firstStamp = 0;
    if (timestamps.size() > 0) {
      firstStamp = timestamps.get(0).doubleValue();
    }

    for (int i=0; i<getImageCount(); i++) {
      int[] zct = FormatTools.getZCTCoords(this, i);

      if (zct[2] < timestamps.size()) {
        double thisStamp = timestamps.get(zct[2]).doubleValue();
        store.setPlaneTimingDeltaT(new Float(thisStamp - firstStamp),
          series, 0, i);
        int index = zct[2] + 1;
        double nextStamp = index < timestamps.size() ?
          timestamps.get(index).doubleValue() : thisStamp;
        if (i == getSizeT() - 1 && zct[2] > 0) {
          thisStamp = timestamps.get(zct[2] - 1).doubleValue();
        }
        store.setPlaneTimingExposureTime(new Float(nextStamp - thisStamp),
          series, 0, i);
      }
    }
    in.close();
  }

  protected void populateMetadataStore(SubBlock block, MetadataStore store,
    int series)
  {
    // NB: block.acquire can be false.  If that is the case, Instrument data
    // is the only thing that should be populated.
    if (block instanceof Recording) {
      Recording recording = (Recording) block;
      String objectiveID = MetadataTools.createLSID("Objective", series, 0);
      if (recording.acquire) {
        store.setImageDescription(recording.description, series);
        store.setImageCreationDate(recording.startTime, series);
        store.setObjectiveSettingsObjective(objectiveID, series);
        binning = recording.binning;
      }
      store.setObjectiveCorrection(recording.correction, series, 0);
      store.setObjectiveImmersion(recording.immersion, series, 0);
      store.setObjectiveNominalMagnification(recording.magnification,
        series, 0);
      store.setObjectiveLensNA(recording.lensNA, series, 0);
      store.setObjectiveIris(recording.iris, series, 0);
      store.setObjectiveID(objectiveID, series, 0);
    }
    else if (block instanceof Laser) {
      Laser laser = (Laser) block;
      if (laser.medium != null) {
        store.setLaserLaserMedium(laser.medium, series, nextLaser);
      }
      if (laser.type != null) {
        store.setLaserType(laser.type, series, nextLaser);
      }
      String lightSourceID =
        MetadataTools.createLSID("LightSource", series, nextLaser);
      store.setLightSourceID(lightSourceID, series, nextLaser);
      nextLaser++;
    }
    else if (block instanceof Track) {
      Track track = (Track) block;
      if (track.acquire) {
        store.setDimensionsTimeIncrement(track.timeIncrement, series, 0);
      }
    }
    else if (block instanceof DataChannel) {
      DataChannel channel = (DataChannel) block;
      if (channel.name != null && nextDataChannel < getSizeC() &&
        channel.acquire)
      {
        store.setLogicalChannelName(channel.name, series, nextDataChannel++);
      }
    }
    else if (block instanceof DetectionChannel) {
      DetectionChannel channel = (DetectionChannel) block;
      if (channel.pinhole != null && channel.pinhole.doubleValue() != 0f &&
        nextDetectChannel < getSizeC() && channel.acquire)
      {
        store.setLogicalChannelPinholeSize(channel.pinhole, series,
          nextDetectChannel);
      }
      if (channel.filter != null) {
        String id = MetadataTools.createLSID("Filter", series, nextFilter);
        if (channel.acquire && nextDetectChannel < getSizeC()) {
          store.setLogicalChannelSecondaryEmissionFilter(
            id, series, nextDetectChannel);
        }
        store.setFilterID(id, series, nextFilter);
        store.setFilterModel(channel.filter, series, nextFilter);

        int space = channel.filter.indexOf(" ");
        if (space != -1) {
          String type = channel.filter.substring(0, space).trim();
          if (type.equals("BP")) type = "BandPass";
          else if (type.equals("LP")) type = "LongPass";

          store.setFilterType(type, series, nextFilter);

          String transmittance = channel.filter.substring(space + 1).trim();
          String[] v = transmittance.split("-");
          try {
            store.setTransmittanceRangeCutIn(new Integer(v[0].trim()), series,
              nextFilter);
          }
          catch (NumberFormatException e) { }
          if (v.length > 1) {
            try {
              store.setTransmittanceRangeCutOut(new Integer(v[1].trim()),
                series, nextFilter);
            }
            catch (NumberFormatException e) { }
          }
        }

        nextFilter++;
      }
      if (channel.channelName != null) {
        String detectorID =
          MetadataTools.createLSID("Detector", series, nextDetector);
        store.setDetectorID(detectorID, series, nextDetector);
        if (channel.acquire && nextDetector < getSizeC()) {
          store.setDetectorSettingsDetector(detectorID, series, nextDetector);
          store.setDetectorSettingsBinning(binning, series, nextDetector);
        }
      }
      if (channel.amplificationGain != null) {
        store.setDetectorAmplificationGain(channel.amplificationGain, series,
          nextDetector);
      }
      if (channel.gain != null) {
        store.setDetectorGain(channel.gain, series, nextDetector);
      }
      store.setDetectorType("PMT", series, nextDetector);
      store.setDetectorZoom(new Float(zoom), series, nextDetector);
      nextDetectChannel++;
      nextDetector++;
    }
    else if (block instanceof BeamSplitter) {
      BeamSplitter beamSplitter = (BeamSplitter) block;
      if (beamSplitter.filterSet != null) {
        String filterSetID =
          MetadataTools.createLSID("FilterSet", series, nextFilterSet);
        store.setFilterSetID(filterSetID, series, nextFilterSet);
        if (beamSplitter.filter != null) {
          String id = MetadataTools.createLSID("Dichroic", series, nextFilter);
          store.setDichroicID(id, series, nextFilter);
          store.setDichroicModel(beamSplitter.filter, series, nextFilter);
          store.setFilterSetDichroic(id, series, nextFilterSet);
          nextFilter++;
        }
        nextFilterSet++;
      }
    }
    else if (block instanceof IlluminationChannel) {
      IlluminationChannel channel = (IlluminationChannel) block;
      if (channel.acquire && channel.wavelength != null) {
        //store.setLogicalChannelEmWave(
        //  channel.wavelength, series, nextIllumChannel++);
      }
    }
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

    for (int i=totalROIs; i<totalROIs+numberOfShapes; i++) {
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
          store.setShapeText(text, series, i, 0);
          break;
        case LINE:
          in.skipBytes(4);
          double startX = in.readDouble();
          double startY = in.readDouble();
          double endX = in.readDouble();
          double endY = in.readDouble();

          store.setLineX1(String.valueOf(startX), series, i, 0);
          store.setLineY1(String.valueOf(startY), series, i, 0);
          store.setLineX2(String.valueOf(endX), series, i, 0);
          store.setLineY2(String.valueOf(endY), series, i, 0);
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

          store.setRectX(String.valueOf(topX), series, i, 0);
          store.setRectY(String.valueOf(topY), series, i, 0);
          store.setRectWidth(String.valueOf(width), series, i, 0);
          store.setRectHeight(String.valueOf(height), series, i, 0);

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
            // we are given the center point and one cut point for each axis
            centerX = xs[0];
            centerY = ys[0];

            rx = Math.sqrt(Math.pow(xs[1] - xs[0], 2) +
              Math.pow(ys[1] - ys[0], 2));
            ry = Math.sqrt(Math.pow(xs[2] - xs[0], 2) +
              Math.pow(ys[2] - ys[0], 2));

            // calculate rotation angle
            double slope = (ys[2] - centerY) / (xs[2] - centerX);
            double theta = Math.toDegrees(Math.atan(slope));

            store.setEllipseTransform("rotate(" + theta + " " + centerX +
              " " + centerY + ")", series, i, 0);
          }

          store.setEllipseCx(String.valueOf(centerX), series, i, 0);
          store.setEllipseCy(String.valueOf(centerY), series, i, 0);
          store.setEllipseRx(String.valueOf(rx), series, i, 0);
          store.setEllipseRy(String.valueOf(ry), series, i, 0);

          break;
        case CIRCLE:
          in.skipBytes(4);
          centerX = in.readDouble();
          centerY = in.readDouble();
          double curveX = in.readDouble();
          double curveY = in.readDouble();

          double radius = Math.sqrt(Math.pow(curveX - centerX, 2) +
            Math.pow(curveY - centerY, 2));

          store.setCircleCx(String.valueOf(centerX), series, i, 0);
          store.setCircleCy(String.valueOf(centerY), series, i, 0);
          store.setCircleR(String.valueOf(radius), series, i, 0);

          break;
        case CIRCLE_3POINT:
          in.skipBytes(4);
          // given 3 points on the perimeter of the circle, we need to
          // calculate the center and radius
          double[][] points = new double[3][2];
          for (int j=0; j<points.length; j++) {
            for (int k=0; k<points[j].length; k++) {
              points[j][k] = in.readDouble();
            }
          }

          double s = 0.5 * ((points[1][0] - points[2][0]) *
            (points[0][0] - points[2][0]) - (points[1][1] - points[2][1]) *
            (points[2][1] - points[0][1]));
          double div = (points[0][0] - points[1][0]) *
            (points[2][1] - points[0][1]) - (points[1][1] - points[0][1]) *
            (points[0][0] - points[2][0]);
          s /= div;

          double cx = 0.5 * (points[0][0] + points[1][0]) +
            s * (points[1][1] - points[0][1]);
          double cy = 0.5 * (points[0][1] + points[1][1]) +
            s * (points[0][0] - points[1][0]);

          double r = Math.sqrt(Math.pow(points[0][0] - cx, 2) +
            Math.pow(points[0][1] - cy, 2));

          store.setCircleCx(String.valueOf(cx), series, i, 0);
          store.setCircleCy(String.valueOf(cy), series, i, 0);
          store.setCircleR(String.valueOf(r), series, i, 0);

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

          store.setPolylinePoints(p.toString(), series, i, 0);

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
            store.setPolygonPoints(p.toString(), series, i, 0);
          }
          else store.setPolylinePoints(p.toString(), series, i, 0);

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

          p = new StringBuffer();
          for (int j=0; j<points.length; j++) {
            p.append(points[j][0]);
            p.append(",");
            p.append(points[j][1]);
            if (j < points.length - 1) p.append(" ");
          }

          if (type == OPEN_BEZIER) {
            store.setPolylinePoints(p.toString(), series, i, 0);
          }
          else store.setPolygonPoints(p.toString(), series, i, 0);

          break;
        default:
          i--;
          numberOfShapes--;
          continue;
      }

      // populate shape attributes

      store.setShapeFontFamily(fontName, series, i, 0);
      store.setShapeFontSize(new Integer(fontHeight), series, i, 0);
      store.setShapeFontStyle(fontItalic ? "normal" : "italic", series, i, 0);
      store.setShapeFontWeight(String.valueOf(fontWeight), series, i, 0);
      store.setShapeLocked(new Boolean(moveable), series, i, 0);
      store.setShapeStrokeColor(String.valueOf(color), series, i, 0);
      store.setShapeStrokeWidth(new Integer(lineWidth), series, i, 0);
      store.setShapeTextDecoration(fontUnderlined ? "underline" :
        fontStrikeout ? "line-through" : "normal", series, i, 0);
      store.setShapeVisibility(new Boolean(enabled), series, i, 0);

      in.seek(offset + blockLength);
    }
    totalROIs += numberOfShapes;
  }

  /** Parse a .mdb file and return a list of referenced .lsm files. */
  private String[] parseMDB(String mdbFile) throws FormatException {
    Location mdb = new Location(mdbFile).getAbsoluteFile();
    Location parent = mdb.getParentFile();
    Vector[] tables = MDBParser.parseDatabase(mdbFile);
    Vector<String> referencedLSMs = new Vector<String>();

    for (int table=0; table<tables.length; table++) {
      String[] columnNames = (String[]) tables[table].get(0);
      String tableName = columnNames[0];

      for (int row=1; row<tables[table].size(); row++) {
        String[] tableRow = (String[]) tables[table].get(row);
        for (int col=0; col<tableRow.length; col++) {
          String key = tableName + " " + columnNames[col + 1] + " " + row;
          if (currentId != null) {
            addGlobalMeta(key, tableRow[col]);
          }

          if (tableName.equals("Recordings") && columnNames[col + 1] != null &&
            columnNames[col + 1].equals("SampleData"))
          {
            String filename = tableRow[col].trim();
            filename = filename.replace('\\', File.separatorChar);
            filename = filename.replace('/', File.separatorChar);
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
      return referencedLSMs.toArray(new String[0]);
    }

    String[] fileList = parent.list();
    for (int i=0; i<fileList.length; i++) {
      if (checkSuffix(fileList[i], new String[] {"lsm"}) &&
        !fileList[i].startsWith("."))
      {
        referencedLSMs.add(new Location(parent, fileList[i]).getAbsolutePath());
      }
    }
    return referencedLSMs.toArray(new String[0]);
  }

  private static Hashtable<Integer, String> createKeys() {
    Hashtable<Integer, String> h = new Hashtable<Integer, String>();
    h.put(new Integer(0x10000001), "Name");
    h.put(new Integer(0x4000000c), "Name");
    h.put(new Integer(0x50000001), "Name");
    h.put(new Integer(0x90000001), "Name");
    h.put(new Integer(0x90000005), "Detection Channel Name");
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

  private Integer readEntry() throws IOException {
    return new Integer(in.readInt());
  }

  private Object readValue() throws IOException {
    int blockType = in.readInt();
    int dataSize = in.readInt();

    switch (blockType) {
      case TYPE_LONG:
        return new Long(in.readInt());
      case TYPE_RATIONAL:
        return new Double(in.readDouble());
      case TYPE_ASCII:
        String s = in.readString(dataSize).trim();
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
          if (s.charAt(i) >= 10) sb.append(s.charAt(i));
          else break;
        }

        return sb.toString();
      case TYPE_SUBBLOCK:
        return null;
    }
    in.skipBytes(dataSize);
    return "";
  }

  // -- Helper classes --

  class SubBlock {
    public Hashtable<Integer, Object> blockData;
    public boolean acquire = true;

    public SubBlock() {
      try {
        read();
      }
      catch (IOException e) {
        traceDebug(e);
      }
    }

    protected int getIntValue(int key) {
      Object o = blockData.get(new Integer(key));
      if (o == null) return -1;
      return !(o instanceof Number) ? -1 : ((Number) o).intValue();
    }

    protected float getFloatValue(int key) {
      Object o = blockData.get(new Integer(key));
      if (o == null) return -1f;
      return !(o instanceof Number) ? -1f : ((Number) o).floatValue();
    }

    protected double getDoubleValue(int key) {
      Object o = blockData.get(new Integer(key));
      if (o == null) return -1d;
      return !(o instanceof Number) ? -1d : ((Number) o).doubleValue();
    }

    protected String getStringValue(int key) {
      Object o = blockData.get(new Integer(key));
      return o == null ? null : o.toString();
    }

    protected void read() throws IOException {
      blockData = new Hashtable<Integer, Object>();
      Integer entry = readEntry();
      Object value = readValue();
      while (value != null) {
        if (!blockData.containsKey(entry)) blockData.put(entry, value);
        entry = readEntry();
        value = readValue();
      }
    }

    public void addToHashtable() {
      String prefix = this.getClass().getSimpleName() + " #";
      int index = 1;
      while (getSeriesMeta(prefix + index + " Acquire") != null) index++;
      prefix += index;
      Integer[] keys = blockData.keySet().toArray(new Integer[0]);
      for (Integer key : keys) {
        if (metadataKeys.get(key) != null) {
          addSeriesMeta(prefix + " " + metadataKeys.get(key),
            blockData.get(key));

          if (metadataKeys.get(key).equals("User")) {
            userName = blockData.get(key).toString();
          }
        }
      }
      addGlobalMeta(prefix + " Acquire", new Boolean(acquire));
    }
  }

  class Recording extends SubBlock {
    public String description;
    public String name;
    public String binning;
    public String startTime;
    // Objective data
    public String correction, immersion;
    public Integer magnification;
    public Float lensNA;
    public Boolean iris;

    protected void read() throws IOException {
      super.read();
      description = getStringValue(RECORDING_DESCRIPTION);
      name = getStringValue(RECORDING_NAME);
      binning = getStringValue(RECORDING_CAMERA_BINNING);
      if (binning != null && binning.indexOf("x") == -1) {
        if (binning.equals("0")) binning = null;
        else binning += "x" + binning;
      }

      // start time in days since Dec 30 1899
      long stamp = (long) (getDoubleValue(RECORDING_SAMPLE_0TIME) * 86400000);
      if (stamp > 0) {
        startTime = DateTools.convertDate(stamp, DateTools.MICROSOFT);
      }

      zoom = (Float) getFloatValue(RECORDING_ZOOM);

      String objective = getStringValue(RECORDING_OBJECTIVE);

      correction = "";

      if (objective == null) objective = "";
      String[] tokens = objective.split(" ");
      int next = 0;
      for (; next<tokens.length; next++) {
        if (tokens[next].indexOf("/") != -1) break;
        correction += tokens[next];
      }
      if (next < tokens.length) {
        String p = tokens[next++];
        try {
          magnification = new Integer(p.substring(0, p.indexOf("/") - 1));
        }
        catch (NumberFormatException e) { }
        try {
          lensNA = new Float(p.substring(p.indexOf("/") + 1));
        }
        catch (NumberFormatException e) { }
      }

      immersion = next < tokens.length ? tokens[next++] : "Unknown";
      iris = Boolean.FALSE;
      if (next < tokens.length) {
        iris = new Boolean(tokens[next++].trim().equalsIgnoreCase("iris"));
      }
    }
  }

  class Laser extends SubBlock {
    public String medium, type;
    public Float power;

    protected void read() throws IOException {
      super.read();
      type = getStringValue(LASER_NAME);
      if (type == null) type = "";
      medium = "";

      if (type.startsWith("HeNe")) {
        medium = "HeNe";
        type = "Gas";
      }
      else if (type.startsWith("Argon")) {
        medium = "Ar";
        type = "Gas";
      }
      else if (type.equals("Titanium:Sapphire") || type.equals("Mai Tai")) {
        medium = "TiSapphire";
        type = "SolidState";
      }
      else if (type.equals("YAG")) {
        medium = "";
        type = "SolidState";
      }
      else if (type.equals("Ar/Kr")) {
        medium = "";
        type = "Gas";
      }

      acquire = getIntValue(LASER_ACQUIRE) != 0;
      power = getFloatValue(LASER_POWER);
    }
  }

  class Track extends SubBlock {
    public Float timeIncrement;

    protected void read() throws IOException {
      super.read();
      timeIncrement = getFloatValue(TRACK_TIME_BETWEEN_STACKS);
      acquire = getIntValue(TRACK_ACQUIRE) != 0;
    }
  }

  class DetectionChannel extends SubBlock {
    public Float pinhole;
    public Float gain, amplificationGain;
    public String filter, filterSet;
    public String channelName;

    protected void read() throws IOException {
      super.read();
      pinhole = new Float(getFloatValue(CHANNEL_PINHOLE_DIAMETER));
      gain = new Float(getFloatValue(CHANNEL_DETECTOR_GAIN));
      amplificationGain = new Float(getFloatValue(CHANNEL_AMPLIFIER_GAIN));
      filter = getStringValue(CHANNEL_FILTER);
      if (filter != null) {
        filter = filter.trim();
        if (filter.length() == 0 || filter.equals("None")) {
          filter = null;
        }
      }

      filterSet = getStringValue(CHANNEL_FILTER_SET);
      channelName = getStringValue(CHANNEL_NAME);
      acquire = getIntValue(CHANNEL_ACQUIRE) != 0;
    }
  }

  class IlluminationChannel extends SubBlock {
    public Integer wavelength;
    public Float attenuation;
    public String name;

    protected void read() throws IOException {
      super.read();
      wavelength = new Integer(getIntValue(ILLUM_CHANNEL_WAVELENGTH));
      attenuation = new Float(getFloatValue(ILLUM_CHANNEL_ATTENUATION));
      acquire = getIntValue(ILLUM_CHANNEL_ACQUIRE) != 0;

      name = getStringValue(ILLUM_CHANNEL_NAME);
      try {
        wavelength = new Integer(name);
      }
      catch (NumberFormatException e) { }
    }
  }

  class DataChannel extends SubBlock {
    public String name;

    protected void read() throws IOException {
      super.read();
      name = getStringValue(DATA_CHANNEL_NAME);
      for (int i=0; i<name.length(); i++) {
        if (name.charAt(i) < 10) {
          name = name.substring(0, i);
          break;
        }
      }

      acquire = getIntValue(DATA_CHANNEL_ACQUIRE) != 0;
    }
  }

  class BeamSplitter extends SubBlock {
    public String filter, filterSet;

    protected void read() throws IOException {
      super.read();

      filter = getStringValue(BEAM_SPLITTER_FILTER);
      if (filter != null) {
        filter = filter.trim();
        if (filter.length() == 0 || filter.equals("None")) {
          filter = null;
        }
      }
      filterSet = getStringValue(BEAM_SPLITTER_FILTER_SET);
    }
  }

  class Timer extends SubBlock { }
  class Marker extends SubBlock { }

}
