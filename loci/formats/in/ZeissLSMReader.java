//
// ZeissLSMReader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ZeissLSMReader is the file format reader for Zeiss LSM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ZeissLSMReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ZeissLSMReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ZeissLSMReader extends BaseTiffReader {

  // -- Constants --

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
  private static final int DATA_CHANNEL_NAME = 0xd0000001;

  // -- Static fields --

  private static Hashtable metadataKeys = createKeys();

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private boolean thumbnailsRemoved = false;
  private byte[] lut = null;
  private Vector timestamps;
  private int validChannels;

  // -- Constructor --

  /** Constructs a new Zeiss LSM reader. */
  public ZeissLSMReader() { super("Zeiss Laser-Scanning Microscopy", "lsm"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 3) return false;
    if (block[0] != TiffTools.LITTLE) return false; // denotes little-endian
    if (block[1] != TiffTools.LITTLE) return false;
    if (block[2] != TiffTools.MAGIC_NUMBER) return false; // denotes TIFF
    if (block.length < 8) return true; // we have no way of verifying
    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation + 1 > block.length) {
      // no way of verifying this is a Zeiss file; it is at least a TIFF
      return true;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i * 12) > block.length) return true;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i * 12), 2, true);
          if (ifdtag == ZEISS_ID) return true; // absolutely a valid file
        }
      }
      return false; // we went through the IFD; the ID wasn't found.
    }
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || core.pixelType[0] != FormatTools.UINT8) return null;
    byte[][] b = new byte[3][256];
    for (int i=core.sizeC[0]-1; i>=core.sizeC[0]-validChannels; i--) {
      for (int j=0; j<256; j++) {
        b[i][j] = (byte) j;
      }
    }
    return b;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || core.pixelType[0] != FormatTools.UINT16) return null;
    short[][] s = new short[3][65536];
    for (int i=core.sizeC[0]-1; i>=core.sizeC[0]-validChannels; i--) {
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

    if (core.sizeY[0] > 1) {
      // check that predictor is set to 1 if anything other
      // than LZW compression is used
      if (TiffTools.getCompression(ifds[2*no]) != TiffTools.LZW) {
        ifds[2*no].put(new Integer(TiffTools.PREDICTOR), new Integer(1));
      }

      TiffTools.getSamples(ifds[2*no], in, buf, x, y, w, h);
    }
    else {
      if (TiffTools.getCompression(ifds[0]) != TiffTools.LZW) {
        ifds[0].put(new Integer(TiffTools.PREDICTOR), new Integer(1));
      }

      TiffTools.getSamples(ifds[0], in, buf, x, no, w, 1);
    }
    return swapIfRequired(buf);
  }

  /* @see loci.formats.IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    if (2*no + 1 < ifds.length) return TiffTools.getImage(ifds[2*no + 1], in);
    return super.openThumbImage(no);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  protected void initMetadata() {
    if (!thumbnailsRemoved) return;
    Hashtable ifd = ifds[0];

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    try {
      super.initStandardMetadata();

      // get TIF_CZ_LSMINFO structure
      short[] s = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
      byte[] cz = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        cz[i] = (byte) s[i];
        if (cz[i] < 0) cz[i]++; // account for byte->short conversion
      }

      RandomAccessStream ras = new RandomAccessStream(cz);
      ras.order(core.littleEndian[0]);

      put("MagicNumber", ras.readInt());
      put("StructureSize", ras.readInt());
      put("DimensionX", ras.readInt());
      put("DimensionY", ras.readInt());

      core.sizeZ[0] = ras.readInt();
      ras.skipBytes(4);
      core.sizeT[0] = ras.readInt();

      int dataType = ras.readInt();
      switch (dataType) {
        case 2:
          put("DataType", "12 bit unsigned integer");
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 5:
          put("DataType", "32 bit float");
          core.pixelType[0] = FormatTools.FLOAT;
          break;
        case 0:
          put("DataType", "varying data types");
          core.pixelType[0] = -1;
          break;
        default:
          put("DataType", "8 bit unsigned integer");
          core.pixelType[0] = -1;
      }

      if (core.pixelType[0] == -1) {
        int[] bps = TiffTools.getBitsPerSample(ifd);
        switch (bps[0]) {
          case 16:
            core.pixelType[0] = FormatTools.UINT16;
            break;
          case 32:
            core.pixelType[0] = FormatTools.FLOAT;
            break;
          default:
            core.pixelType[0] = FormatTools.UINT8;
        }
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
          core.currentOrder[0] = "XYZCT";
          break;
        case 1:
          put("ScanType", "z scan (x-z plane)");
          core.currentOrder[0] = "XYZCT";
          break;
        case 2:
          put("ScanType", "line scan");
          core.currentOrder[0] = "XYZCT";
          break;
        case 3:
          put("ScanType", "time series x-y");
          core.currentOrder[0] = "XYTCZ";
          break;
        case 4:
          put("ScanType", "time series x-z");
          core.currentOrder[0] = "XYZTC";
          break;
        case 5:
          put("ScanType", "time series 'Mean of ROIs'");
          core.currentOrder[0] = "XYTCZ";
          break;
        case 6:
          put("ScanType", "time series x-y-z");
          core.currentOrder[0] = "XYZTC";
          break;
        case 7:
          put("ScanType", "spline scan");
          core.currentOrder[0] = "XYCTZ";
          break;
        case 8:
          put("ScanType", "spline scan x-z");
          core.currentOrder[0] = "XYCZT";
          break;
        case 9:
          put("ScanType", "time series spline plane x-z");
          core.currentOrder[0] = "XYTCZ";
          break;
        case 10:
          put("ScanType", "point mode");
          core.currentOrder[0] = "XYZCT";
          break;
        default:
          put("ScanType", "x-y-z scan");
          core.currentOrder[0] = "XYZCT";
      }

      store.setImageName("", 0);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);

      int spectralScan = ras.readShort();
      switch (spectralScan) {
        case 1:
          put("SpectralScan", "acquired with spectral scan");
          break;
        default:
          put("SpectralScan", "no spectral scan");
      }

      long type = ras.readInt();
      switch ((int) type) {
        case 1:
          put("DataType2", "calculated data");
          break;
        case 2:
          put("DataType2", "animation");
          break;
        default:
          put("DataType2", "original scan data");
      }

      long overlayOffset = ras.readInt();
      long inputLUTOffset = ras.readInt();
      long outputLUTOffset = ras.readInt();
      long channelColorsOffset = ras.readInt();

      put("TimeInterval", ras.readDouble());

      ras.skipBytes(4);
      long scanInformationOffset = ras.readInt();
      ras.skipBytes(4);
      long timeStampOffset = ras.readInt();
      long eventListOffset = ras.readInt();
      long roiOffset = ras.readInt();
      long bleachRoiOffset = ras.readInt();
      ras.skipBytes(4);

      put("DisplayAspectX", ras.readDouble());
      put("DisplayAspectY", ras.readDouble());
      put("DisplayAspectZ", ras.readDouble());
      put("DisplayAspectTime", ras.readDouble());

      long meanOfRoisOverlayOffset = ras.readInt();
      long topoIsolineOverlayOffset = ras.readInt();
      long topoProfileOverlayOffset = ras.readInt();
      long linescanOverlayOffset = ras.readInt();

      put("ToolbarFlags", ras.readInt());
      ras.skipBytes(20);

      // read referenced structures

      if (overlayOffset != 0) {
        parseOverlays(overlayOffset, "OffsetVectorOverlay");
      }

      if (inputLUTOffset != 0) {
        parseSubBlocks(inputLUTOffset, "OffsetInputLut");
      }

      if (outputLUTOffset != 0) {
        parseSubBlocks(outputLUTOffset, "OffsetOutputLut");
      }

      core.indexed[0] = lut != null && getSizeC() == 1;
      if (core.indexed[0]) core.sizeC[0] = 3;
      if (core.sizeC[0] == 0) core.sizeC[0] = 1;

      put("DimensionZ", core.sizeZ[0]);
      put("DimensionChannels", core.sizeC[0]);

      if (channelColorsOffset != 0) {
        in.seek(channelColorsOffset + 4);
        int numColors = in.readInt();
        int numNames = in.readInt();

        long namesOffset = in.readInt();

        // read in the intensity value for each color

        if (namesOffset > 0) {
          in.seek(namesOffset + channelColorsOffset);

          for (int i=0; i<numNames; i++) {
            // we want to read until we find a null char
            StringBuffer sb = new StringBuffer();
            char current = (char) in.read();
            while (current != 0) {
              if (current < 128) sb.append(current);
              current = (char) in.read();
            }
            if (sb.length() <= 128) put("ChannelName" + i, sb.toString());
          }
        }
      }

      if (timeStampOffset != 0) {
        in.seek(timeStampOffset + 4);
        int numberOfStamps = in.readInt();
        for (int i=0; i<numberOfStamps; i++) {
          double stamp = in.readDouble();
          put("TimeStamp" + i, stamp);
          timestamps.add(new Double(stamp));
        }
      }

      if (eventListOffset != 0) {
        in.seek(eventListOffset + 4);
        int numEvents = in.readInt();
        for (int i=0; i<numEvents; i++) {
          int size = in.readInt();
          double eventTime = in.readDouble();
          int eventType = in.readInt();
          put("Event" + i + " Time", eventTime);
          put("Event" + i + " Type", eventType);
          put("Event" + i + " Description", in.readString(size - 16));
        }
      }

      if (roiOffset != 0) parseOverlays(roiOffset, "ROIOffset");

      if (bleachRoiOffset != 0) {
        parseOverlays(bleachRoiOffset, "BleachROIOffset");
      }

      if (meanOfRoisOverlayOffset != 0) {
        parseOverlays(meanOfRoisOverlayOffset, "OffsetMeanOfRoisOverlay");
      }

      if (topoIsolineOverlayOffset != 0) {
        parseOverlays(topoIsolineOverlayOffset, "OffsetTopoIsolineOverlay");
      }

      if (topoProfileOverlayOffset != 0) {
        parseOverlays(topoProfileOverlayOffset, "OffsetTopoProfileOverlay");
      }

      if (linescanOverlayOffset != 0) {
        parseOverlays(linescanOverlayOffset, "OffsetLinescanOverlay");
      }

      if (scanInformationOffset != 0) {
        in.seek(scanInformationOffset);

        Stack prefix = new Stack();
        int count = 1;

        Object value = null;

        boolean done = false;
        int nextLaserMedium = 0, nextLaserType = 0, nextGain = 0,
          nextPinhole = 0, nextEmWave = 0, nextExWave = 0, nextChannelName = 0;
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
                  prefix.pop();
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

          switch (entry) {
            case RECORDING_ENTRY_DESCRIPTION:
              store.setImageDescription(value.toString(), 0);
              break;
            case RECORDING_ENTRY_OBJECTIVE:
              store.setObjectiveModel(value.toString(), 0, 0);
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
          //  TODO: this is a setting, not a fixed value
          //  store.setLaserPower(new Float(value.toString()), 0, count - 1);
          //  break;
            case CHANNEL_ENTRY_DETECTOR_GAIN:
              //store.setDetectorSettingsGain(
              //  new Float(value.toString()), 0, nextGain++);
              break;
            case CHANNEL_ENTRY_PINHOLE_DIAMETER:
              int n = (int) Float.parseFloat(value.toString());
              if (n > 0) {
                store.setLogicalChannelPinholeSize(new Integer(n), 0,
                  nextPinhole++);
              }
              break;
            case CHANNEL_ENTRY_SPI_WAVELENGTH_START:
              n = (int) Float.parseFloat(value.toString());
              store.setLogicalChannelEmWave(new Integer(n), 0, nextEmWave++);
              break;
            case CHANNEL_ENTRY_SPI_WAVELENGTH_END:
              n = (int) Float.parseFloat(value.toString());
              store.setLogicalChannelExWave(new Integer(n), 0, nextExWave++);
              break;
            case DATA_CHANNEL_NAME:
              store.setLogicalChannelName(value.toString(),
                0, nextChannelName++);
              break;
          }

          if (!done) done = in.getFilePointer() >= in.length() - 12;
        }
      }
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
    catch (IOException exc) {
      if (debug) trace(exc);
    }

    if (core.indexed[0]) core.rgb[0] = true;
    core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] *
      ((core.rgb[0] || core.indexed[0]) ? 1 : core.sizeC[0]);

    MetadataTools.populatePixels(store, this);

    Float pixX = new Float((float) pixelSizeX);
    Float pixY = new Float((float) pixelSizeY);
    Float pixZ = new Float((float) pixelSizeZ);

    store.setDimensionsPhysicalSizeX(pixX, 0, 0);
    store.setDimensionsPhysicalSizeY(pixY, 0, 0);
    store.setDimensionsPhysicalSizeZ(pixZ, 0, 0);

    for (int i=0; i<core.imageCount[0]; i++) {
      int[] zct = FormatTools.getZCTCoords(this, i);
      store.setPlaneTheZ(new Integer(zct[0]), 0, 0, i);
      store.setPlaneTheC(new Integer(zct[1]), 0, 0, i);
      store.setPlaneTheT(new Integer(zct[2]), 0, 0, i);

      if (zct[2] < timestamps.size()) {
        float thisStamp = ((Double) timestamps.get(zct[2])).floatValue();
        store.setPlaneTimingDeltaT(new Float(thisStamp), 0, 0, i);
        float nextStamp = i < core.sizeT[0] - 1 ?
          ((Double) timestamps.get(zct[2] + 1)).floatValue() : thisStamp;
        if (i == core.sizeT[0] - 1 && zct[2] > 0) {
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
      if (dirList[i].toLowerCase().endsWith(".mdb")) {
        try {
          MDBParser.parseDatabase((new Location(dir.getPath(),
            dirList[i])).getAbsolutePath(), metadata);
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

      if (subFileType == 0) newIFDs.add(ifds[i]);
    }

    // reset numImages and ifds
    ifds = (Hashtable[]) newIFDs.toArray(new Hashtable[0]);
    thumbnailsRemoved = true;

    initMetadata();
    ifds = TiffTools.getIFDs(in);

    if (ifds.length > 1) {
      core.thumbSizeX[0] = TiffTools.getIFDIntValue(ifds[1],
        TiffTools.IMAGE_WIDTH, false, 1);
      core.thumbSizeY[0] = TiffTools.getIFDIntValue(ifds[1],
        TiffTools.IMAGE_LENGTH, false, 1);
    }

    if (ifds.length == 2 && core.imageCount[0] > ifds.length / 2) {
      core.sizeY[0] = 1;
    }
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
    in.readDouble();
    put("ColorRed-" + suffix, in.read());
    put("ColorGreen-" + suffix, in.read());
    put("ColorBlue-" + suffix, in.read());
    in.read();

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
    try {
      int drawingEl = (size - 194) / nde;
      for (int i=0; i<nde; i++) {
        put("DrawingElement" + i + "-" + suffix, in.readString(drawingEl));
      }
    }
    catch (ArithmeticException exc) {
      if (debug) trace(exc);
    }
  }

  /** Parses subblock-related fields. */
  protected void parseSubBlocks(long data, String suffix)
    throws IOException, FormatException
  {
    if (data == 0) return;

    in.seek(data);

    in.order(core.littleEndian[0]);

    long size = in.readInt() & 0xffffffff;
    long numSubBlocks = in.readInt() & 0xffffffff;
    put("NumSubBlocks-" + suffix, numSubBlocks);
    long numChannels = in.readInt() & 0xffffffff;
    put("NumChannels-" + suffix, numChannels);
    data = in.readInt() & 0xffffffff;
    put("LutType-" + suffix, data);
    data = in.readInt() & 0xffffffff;
    put("Advanced-" + suffix, data);
    data = in.readInt() & 0xffffffff;
    put("CurrentChannel-" + suffix, data);
    in.skipBytes(36);

    if (numSubBlocks > 100) numSubBlocks = 20;

    for (int i=0; i<numSubBlocks; i++) {
      data = in.readInt() & 0xffffffff;
      put("Type" + i + "-" + suffix, data);
      int v = in.readInt();
      put("Size" + i + "-" + suffix, v);

      switch ((int) data) {
        case SUBBLOCK_GAMMA:
          for (int j=0; j<numChannels; j++) {
            put("GammaChannel" + j + "-" + i + "-" + suffix, in.readDouble());
          }
          break;
        case SUBBLOCK_BRIGHTNESS:
          for (int j=0; j<numChannels; j++) {
            put("BrightnessChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
          }
          break;
        case SUBBLOCK_CONTRAST:
          for (int j=0; j<numChannels; j++) {
            put("ContrastChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
          }
          break;
        case SUBBLOCK_RAMP:
          for (int j=0; j<numChannels; j++) {
            put("RampStartXChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
            put("RampStartYChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
            put("RampEndXChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
            put("RampEndYChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
            j += 4;
          }
          break;
        case SUBBLOCK_KNOTS:
          for (int j=0; j<numChannels; j++) {
            double knotX = -1, knotY = -1;
            int n = 0;
            while (knotX < 255) {
              knotX = in.readDouble();
              knotY = in.readDouble();
              addMeta("Channel " + j + " Knot " + n + " X", new Double(knotX));
              addMeta("Channel " + j + " Knot " + n + " Y", new Double(knotY));
              n++;
            }
          }
          break;
        case SUBBLOCK_PALETTE:
          if (lut == null) {
            lut = new byte[(int) (8192 * numChannels)];
            in.read(lut);
          }
          else in.skipBytes((int) (8192 * numChannels));
          break;
      }
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
