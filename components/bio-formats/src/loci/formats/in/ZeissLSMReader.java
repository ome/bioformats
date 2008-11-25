//
// ZeissLSMReader.java
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
public class ZeissLSMReader extends BaseTiffReader {

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

  // -- Static fields --

  private static Hashtable metadataKeys = createKeys();

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private boolean thumbnailsRemoved = false;
  private byte[] lut = null;
  private Vector timestamps;
  private int validChannels;

  private String mdbFilename;

  // -- Constructor --

  /** Constructs a new Zeiss LSM reader. */
  public ZeissLSMReader() { super("Zeiss Laser-Scanning Microscopy", "lsm"); }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    pixelSizeX = pixelSizeY = pixelSizeZ = 0f;
    lut = null;
    thumbnailsRemoved = false;
    timestamps = null;
    validChannels = 0;
    mdbFilename = null;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (mdbFilename == null) return super.getUsedFiles();
    return new String[] {currentId, mdbFilename};
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || getPixelType() != FormatTools.UINT8) return null;
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
    if (lut == null || getPixelType() != FormatTools.UINT16) return null;
    short[][] s = new short[3][65536];
    for (int i=2; i>=3-validChannels; i--) {
      for (int j=0; j<s[i].length; j++) {
        s[i][j] = (short) j;
      }
    }
    return s;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  protected void initMetadata() throws FormatException, IOException {
    if (!thumbnailsRemoved) return;
    Hashtable ifd = ifds[0];

    status("Reading LSM metadata");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    super.initStandardMetadata();

    // get TIF_CZ_LSMINFO structure
    short[] s = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
    byte[] cz = new byte[s.length];
    for (int i=0; i<s.length; i++) {
      cz[i] = (byte) s[i];
    }

    RandomAccessStream ras = new RandomAccessStream(cz);
    ras.order(isLittleEndian());

    put("MagicNumber", ras.readInt());
    put("StructureSize", ras.readInt());
    put("DimensionX", ras.readInt());
    put("DimensionY", ras.readInt());

    core[0].sizeZ = ras.readInt();
    ras.skipBytes(4);
    core[0].sizeT = ras.readInt();

    int dataType = ras.readInt();
    switch (dataType) {
      case 2:
        put("DataType", "12 bit unsigned integer");
        break;
      case 5:
        put("DataType", "32 bit float");
        break;
      case 0:
        put("DataType", "varying data types");
        break;
      default:
        put("DataType", "8 bit unsigned integer");
    }

    put("ThumbnailX", ras.readInt());
    put("ThumbnailY", ras.readInt());

    // pixel sizes are stored in meters, we need them in microns
    pixelSizeX = ras.readDouble() * 1000000;
    pixelSizeY = ras.readDouble() * 1000000;
    pixelSizeZ = ras.readDouble() * 1000000;

    put("VoxelSizeX", new Double(pixelSizeX));
    put("VoxelSizeY", new Double(pixelSizeY));
    put("VoxelSizeZ", new Double(pixelSizeZ));

    put("OriginX", ras.readDouble());
    put("OriginY", ras.readDouble());
    put("OriginZ", ras.readDouble());

    int scanType = ras.readShort();
    switch (scanType) {
      case 0:
        put("ScanType", "x-y-z scan");
        core[0].dimensionOrder = "XYZCT";
        break;
      case 1:
        put("ScanType", "z scan (x-z plane)");
        core[0].dimensionOrder = "XYZCT";
        break;
      case 2:
        put("ScanType", "line scan");
        core[0].dimensionOrder = "XYZCT";
        break;
      case 3:
        put("ScanType", "time series x-y");
        core[0].dimensionOrder = "XYTCZ";
        break;
      case 4:
        put("ScanType", "time series x-z");
        core[0].dimensionOrder = "XYZTC";
        break;
      case 5:
        put("ScanType", "time series 'Mean of ROIs'");
        core[0].dimensionOrder = "XYTCZ";
        break;
      case 6:
        put("ScanType", "time series x-y-z");
        core[0].dimensionOrder = "XYZTC";
        break;
      case 7:
        put("ScanType", "spline scan");
        core[0].dimensionOrder = "XYCTZ";
        break;
      case 8:
        put("ScanType", "spline scan x-z");
        core[0].dimensionOrder = "XYCZT";
        break;
      case 9:
        put("ScanType", "time series spline plane x-z");
        core[0].dimensionOrder = "XYTCZ";
        break;
      case 10:
        put("ScanType", "point mode");
        core[0].dimensionOrder = "XYZCT";
        break;
      default:
        put("ScanType", "x-y-z scan");
        core[0].dimensionOrder = "XYZCT";
    }

    core[0].indexed = lut != null && getSizeC() == 1;
    if (isIndexed()) {
      core[0].sizeC = 1;
      core[0].rgb = false;
    }
    if (getSizeC() == 0) core[0].sizeC = 1;

    if (isRGB()) {
      // shuffle C to front of order string
      core[0].dimensionOrder = getDimensionOrder().replaceAll("C", "");
      core[0].dimensionOrder = getDimensionOrder().replaceAll("XY", "XYC");
    }

    if (isIndexed()) core[0].rgb = false;
    if (getEffectiveSizeC() == 0) core[0].imageCount = getSizeZ() * getSizeT();
    else core[0].imageCount = getSizeZ() * getSizeT() * getEffectiveSizeC();

    if (getImageCount() != ifds.length) {
      int diff = getImageCount() - ifds.length;
      core[0].imageCount = ifds.length;
      if (diff % getSizeZ() == 0) {
        core[0].sizeT -= (diff / getSizeZ());
      }
      else if (diff % getSizeT() == 0) {
        core[0].sizeZ -= (diff / getSizeT());
      }
      else if (getSizeZ() > 1) {
        core[0].sizeZ = ifds.length;
        core[0].sizeT = 1;
      }
      else if (getSizeT() > 1) {
        core[0].sizeT = ifds.length;
        core[0].sizeZ = 1;
      }
    }

    if (getSizeZ() == 0) core[0].sizeZ = getImageCount();
    if (getSizeT() == 0) core[0].sizeT = getImageCount() / getSizeZ();

    MetadataTools.populatePixels(store, this, true);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, getCurrentFile(), 0);

    int spectralScan = ras.readShort();
    if (spectralScan != 1) put("SpectralScan", "no spectral scan");
    else put("SpectralScan", "acquired with spectral scan");

    int type = ras.readInt();
    switch (type) {
      case 1:
        put("DataType2", "calculated data");
        break;
      case 2:
        put("DataType2", "animation");
        break;
      default:
        put("DataType2", "original scan data");
    }

    long[] overlayOffsets = new long[9];
    String[] overlayKeys = new String[] {"VectorOverlay", "InputLut",
      "OutputLut", "ROI", "BleachROI", "MeanOfRoisOverlay",
      "TopoIsolineOverlay", "TopoProfileOverlay", "LinescanOverlay"};

    overlayOffsets[0] = ras.readInt();
    overlayOffsets[1] = ras.readInt();
    overlayOffsets[2] = ras.readInt();

    long channelColorsOffset = ras.readInt();

    put("TimeInterval", ras.readDouble());
    ras.skipBytes(4);
    long scanInformationOffset = ras.readInt();
    ras.skipBytes(4);
    long timeStampOffset = ras.readInt();
    long eventListOffset = ras.readInt();
    overlayOffsets[3] = ras.readInt();
    overlayOffsets[4] = ras.readInt();
    ras.skipBytes(4);

    put("DisplayAspectX", ras.readDouble());
    put("DisplayAspectY", ras.readDouble());
    put("DisplayAspectZ", ras.readDouble());
    put("DisplayAspectTime", ras.readDouble());

    overlayOffsets[5] = ras.readInt();
    overlayOffsets[6] = ras.readInt();
    overlayOffsets[7] = ras.readInt();
    overlayOffsets[8] = ras.readInt();

    for (int i=0; i<overlayOffsets.length; i++) {
      parseOverlays(overlayOffsets[i], overlayKeys[i]);
    }

    put("ToolbarFlags", ras.readInt());
    ras.close();

    // read referenced structures

    put("DimensionZ", getSizeZ());
    put("DimensionChannels", getSizeC());

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
          if (name.length() <= 128) put("ChannelName" + i, name);
        }
      }
    }

    if (timeStampOffset != 0) {
      in.seek(timeStampOffset + 8);
      for (int i=0; i<getSizeT(); i++) {
        double stamp = in.readDouble();
        put("TimeStamp" + i, stamp);
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
          put("Event" + i + " Time", eventTime);
          put("Event" + i + " Type", eventType);
          long fp = in.getFilePointer();
          int len = size - 16;
          if (len > 65536) len = 65536;
          if (len < 0) len = 0;
          put("Event" + i + " Description", in.readString(len));
          in.seek(fp + size - 16);
          if (in.getFilePointer() < 0) break;
        }
      }
    }

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
                count = 1;
                if (prefix.size() > 0) prefix.pop();
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
        if (value instanceof String) value = ((String) value).trim();
        if (key != null) addMeta(key, value);

        float n;
        switch (entry) {
          case RECORDING_ENTRY_DESCRIPTION:
            store.setImageDescription(value.toString(), 0);
            break;
          case RECORDING_ENTRY_OBJECTIVE:
            String[] tokens = value.toString().split(" ");
            StringBuffer model = new StringBuffer();
            int next = 0;
            for (; next<tokens.length; next++) {
              if (tokens[next].indexOf("/") != -1) break;
              model.append(tokens[next]);
            }
            store.setObjectiveModel(model.toString(), 0, 0);
            if (next < tokens.length) {
              String p = tokens[next++];
              String mag = p.substring(0, p.indexOf("/") - 1);
              String na = p.substring(p.indexOf("/") + 1);
              store.setObjectiveNominalMagnification(new Integer(mag), 0, 0);
              store.setObjectiveLensNA(new Float(na), 0, 0);
            }
            if (next < tokens.length) {
              store.setObjectiveImmersion(tokens[next++], 0, 0);
            }

            // link Objective to Image
            store.setObjectiveID("Objective:0", 0, 0);
            store.setObjectiveSettingsObjective("Objective:0", 0);

            break;
          case TRACK_ENTRY_TIME_BETWEEN_STACKS:
            store.setDimensionsTimeIncrement(
              new Float(value.toString()), 0, 0);
            break;
          case LASER_ENTRY_NAME:
            String medium = value.toString();
            String laserType = null;

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
              medium = null;
              laserType = "SolidState";
            }
            else if (medium.equals("Ar/Kr")) {
              medium = null;
              laserType = "Gas";
            }
            else if (medium.equals("Enterprise")) medium = null;

            if (medium != null && laserType != null) {
              store.setLaserLaserMedium(medium, 0, nextLaserMedium++);
              store.setLaserType(laserType, 0, nextLaserType++);
            }

            break;
          //case LASER_POWER:
            // TODO: this is a setting, not a fixed value
            //n = Float.parseFloat(value.toString());
            //store.setLaserPower(new Float(n), 0, count - 1);
            //break;
          case CHANNEL_ENTRY_DETECTOR_GAIN:
            //n = Float.parseFloat(value.toString());
            //store.setDetectorSettingsGain(new Float(n), 0, nextGain++);
            break;
          case CHANNEL_ENTRY_PINHOLE_DIAMETER:
            n = Float.parseFloat(value.toString());
            store.setLogicalChannelPinholeSize(new Float(n), 0,
              nextPinhole++);
            break;
          case ILLUM_CHANNEL_WAVELENGTH:
            n = Float.parseFloat(value.toString());
            if (nextEmWave < getSizeC()) {
              store.setLogicalChannelEmWave(new Integer((int) n), 0,
                nextEmWave++);
            }
            if (nextExWave < getSizeC()) {
              store.setLogicalChannelExWave(new Integer((int) n), 0,
                nextExWave++);
            }
            break;
          case START_TIME:
            // date/time on which the first pixel was acquired, in days
            // since 30 December 1899
            double time = Double.parseDouble(value.toString());
            store.setImageCreationDate(DataTools.convertDate(
              (long) (time * 86400000), DataTools.MICROSOFT), 0);

            break;
          case DATA_CHANNEL_NAME:
            store.setLogicalChannelName(value.toString(),
              0, nextChannelName++);
            break;
        }

        if (!done) done = in.getFilePointer() >= in.length() - 12;
      }
    }

    Float pixX = new Float((float) pixelSizeX);
    Float pixY = new Float((float) pixelSizeY);
    Float pixZ = new Float((float) pixelSizeZ);

    store.setDimensionsPhysicalSizeX(pixX, 0, 0);
    store.setDimensionsPhysicalSizeY(pixY, 0, 0);
    store.setDimensionsPhysicalSizeZ(pixZ, 0, 0);

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
          0, 0, i);
      }
    }

    // see if we have an associated MDB file

    Location dir = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] dirList = dir.list();

    for (int i=0; i<dirList.length; i++) {
      if (checkSuffix(dirList[i], MDB_SUFFIX)) {
        try {
          Location file = new Location(dir.getPath(), dirList[i]);
          if (!file.isDirectory()) {
            mdbFilename = file.getAbsolutePath();
            Vector[] tables = MDBParser.parseDatabase(mdbFilename);

            for (int table=0; table<tables.length; table++) {
              String[] columnNames = (String[]) tables[table].get(0);
              for (int row=1; row<tables[table].size(); row++) {
                String[] tableRow = (String[]) tables[table].get(row);
                String baseKey = columnNames[0] + " ";
                for (int col=0; col<tableRow.length; col++) {
                  addMeta(baseKey + columnNames[col + 1] + " " + row,
                    tableRow[col]);
                }
              }
            }
          }
        }
        catch (Exception exc) {
          if (debug) trace(exc);
        }
        i = dirList.length;
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ZeissLSMReader.initFile(" + id + ")");
    thumbnailsRemoved = false;
    super.initFile(id);

    timestamps = new Vector();

    // go through the IFD hashtable array and
    // remove anything with NEW_SUBFILE_TYPE = 1
    // NEW_SUBFILE_TYPE = 1 indicates that the IFD
    // contains a thumbnail image

    status("Removing thumbnails");

    Vector newIFDs = new Vector();
    for (int i=0; i<ifds.length; i++) {
      long subFileType = TiffTools.getIFDLongValue(ifds[i],
        TiffTools.NEW_SUBFILE_TYPE, true, 0);

      if (subFileType == 0) {
        // check that predictor is set to 1 if anything other
        // than LZW compression is used
        if (TiffTools.getCompression(ifds[i]) != TiffTools.LZW) {
          ifds[i].put(new Integer(TiffTools.PREDICTOR), new Integer(1));
        }
        newIFDs.add(ifds[i]);
      }
    }

    // reset numImages and ifds
    ifds = (Hashtable[]) newIFDs.toArray(new Hashtable[0]);
    thumbnailsRemoved = true;

    initMetadata();
    core[0].littleEndian = !isLittleEndian();
  }

  // -- Helper methods --

  /** Parses overlay-related fields. */
  protected void parseOverlays(long data, String suffix) throws IOException {
    if (data == 0) return;

    in.seek(data);

    int nde = in.readInt();
    put("NumberDrawingElements-" + suffix, nde);
    int size = in.readInt();
    int idata = in.readInt();
    put("LineWidth-" + suffix, idata);
    idata = in.readInt();
    put("Measure-" + suffix, idata);
    in.skipBytes(8);
    put("ColorRed-" + suffix, in.read());
    put("ColorGreen-" + suffix, in.read());
    put("ColorBlue-" + suffix, in.read());
    in.skipBytes(1);

    put("Valid-" + suffix, in.readInt());
    put("KnotWidth-" + suffix, in.readInt());
    put("CatchArea-" + suffix, in.readInt());

    // some fields describing the font
    put("FontHeight-" + suffix, in.readInt());
    put("FontWidth-" + suffix, in.readInt());
    put("FontEscapement-" + suffix, in.readInt());
    put("FontOrientation-" + suffix, in.readInt());
    put("FontWeight-" + suffix, in.readInt());
    put("FontItalic-" + suffix, in.readInt());
    put("FontUnderline-" + suffix, in.readInt());
    put("FontStrikeOut-" + suffix, in.readInt());
    put("FontCharSet-" + suffix, in.readInt());
    put("FontOutPrecision-" + suffix, in.readInt());
    put("FontClipPrecision-" + suffix, in.readInt());
    put("FontQuality-" + suffix, in.readInt());
    put("FontPitchAndFamily-" + suffix, in.readInt());
    put("FontFaceName-" + suffix, in.readString(64));

    // some flags for measuring values of different drawing element types
    put("ClosedPolyline-" + suffix, in.read());
    put("OpenPolyline-" + suffix, in.read());
    put("ClosedBezierCurve-" + suffix, in.read());
    put("OpenBezierCurve-" + suffix, in.read());
    put("ArrowWithClosedTip-" + suffix, in.read());
    put("ArrowWithOpenTip-" + suffix, in.read());
    put("Ellipse-" + suffix, in.read());
    put("Circle-" + suffix, in.read());
    put("Rectangle-" + suffix, in.read());
    put("Line-" + suffix, in.read());
    /*
    try {
      int drawingEl = (size - 194) / nde;
      if (drawingEl <= 0) return;
      if (DataTools.swap(nde) < nde) nde = DataTools.swap(nde);
      for (int i=0; i<nde; i++) {
        put("DrawingElement" + i + "-" + suffix, in.readString(drawingEl));
      }
    }
    catch (ArithmeticException exc) {
      if (debug) trace(exc);
    }
    */
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
