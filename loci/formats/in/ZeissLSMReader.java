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
import java.util.Hashtable;
import loci.formats.*;

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

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    ifds = TiffTools.getIFDs(in);
    TiffTools.getSamples(ifds[2*no], in, buf);
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
    Hashtable ifd = ifds[0];

    try {
      boolean little = TiffTools.isLittleEndian(ifd);
      in.order(little);

      super.initMetadata();

      // get TIF_CZ_LSMINFO structure
      short[] s = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
      byte[] cz = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        cz[i] = (byte) s[i];
        if (cz[i] < 0) cz[i]++; // account for byte->short conversion
      }

      RandomAccessStream ras = new RandomAccessStream(cz);
      ras.order(little);

      put("MagicNumber", ras.readInt());
      put("StructureSize", ras.readInt());
      put("DimensionX", ras.readInt());
      put("DimensionY", ras.readInt());

      core.sizeZ[0] = ras.readInt();
      int c = ras.readInt();
      core.sizeT[0] = ras.readInt();

      if (c > core.sizeC[0] || c != 1) core.sizeC[0] = c;
      if (core.sizeC[0] == 0) core.sizeC[0]++;

      while (core.imageCount[0] > core.sizeZ[0] * core.sizeC[0] * core.sizeT[0])
      {
        if (core.sizeZ[0] > core.sizeT[0]) core.sizeZ[0]++;
        else core.sizeT[0]++;
      }

      while (core.imageCount[0] > core.sizeZ[0] * core.sizeT[0] *
        getEffectiveSizeC())
      {
        core.imageCount[0]--;
      }

      put("DimensionZ", core.sizeZ[0]);
      put("DimensionChannels", core.sizeC[0]);
      put("DimensionTime", core.sizeT[0]);

      int dataType = ras.readInt();
      switch (dataType) {
        case 1:
          put("DataType", "8 bit unsigned integer");
          core.pixelType[0] = FormatTools.UINT8;
          break;
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

      put("VoxelSizeX", ras.readDouble());
      put("VoxelSizeY", ras.readDouble());
      put("VoxelSizeZ", ras.readDouble());

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

      MetadataStore store = getMetadataStore();

      FormatTools.populatePixels(store, this);
      for (int i=0; i<core.sizeC[0]; i++) {
        store.setLogicalChannel(i, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, null);
      }

      int spectralScan = ras.readShort();
      switch (spectralScan) {
        case 0:
          put("SpectralScan", "no spectral scan");
          break;
        case 1:
          put("SpectralScan", "acquired with spectral scan");
          break;
        default:
          put("SpectralScan", "no spectral scan");
      }

      long type = ras.readInt();
      switch ((int) type) {
        case 0:
          put("DataType2", "original scan data");
          break;
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

      ras.skipBytes(12);
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
        parseOverlays(overlayOffset, "OffsetVectorOverlay", little);
      }

      if (inputLUTOffset != 0) {
        parseSubBlocks(inputLUTOffset, "OffsetInputLut", little);
      }

      if (outputLUTOffset != 0) {
        parseSubBlocks(outputLUTOffset, "OffsetOutputLut", little);
      }

      if (channelColorsOffset != 0) {
        in.seek(channelColorsOffset + 4);
        int numColors = in.readInt();
        int numNames = in.readInt();

        if (numColors > core.sizeC[0]) {
          in.seek(channelColorsOffset - 2);
          in.order(!little);
          in.readInt();
          numColors = in.readInt();
          numNames = in.readInt();
        }

        long namesOffset = in.readInt() + channelColorsOffset;
        in.skipBytes(4);

        // read in the intensity value for each color

        if (namesOffset >= 0) {
          in.seek(namesOffset);
          for (int i=0; i<numColors; i++) put("Intensity" + i, in.readInt());
        }

        // read in the channel names

        for (int i=0; i<numNames; i++) {
          // we want to read until we find a null char
          StringBuffer sb = new StringBuffer();
          char current = (char) in.read();
          while (current != 0) {
            if (current < 128) sb.append(current);
            current = (char) in.read();
          }
          if (sb.length() <= 128) put("ChannelName" + i, sb.toString());
          else put("ChannelName" + i, "");
        }
      }

      if (timeStampOffset != 0) {
        in.seek(timeStampOffset + 4);
        int numberOfStamps = in.readInt();
        for (int i=0; i<numberOfStamps; i++) {
          put("TimeStamp" + i, in.readDouble());
        }
      }

      if (eventListOffset != 0) {
        in.seek(eventListOffset);
        in.skipBytes(4); // skipping the block size
        int numEvents = in.readInt();
        for (int i=0; i<numEvents; i++) {
          int size = in.readInt();
          double eventTime = in.readDouble();
          int eventType = in.readInt();
          byte[] b = new byte[size - 16];
          in.read(b);
          put("Event" + i + " Time", eventTime);
          put("Event" + i + " Type", eventType);
          put("Event" + i + " Description", new String(b));
        }
      }

      if (roiOffset != 0) parseOverlays(roiOffset, "ROIOffset", little);

      if (bleachRoiOffset != 0) {
        parseOverlays(bleachRoiOffset, "BleachROIOffset", little);
      }

      if (meanOfRoisOverlayOffset != 0) {
        parseOverlays(meanOfRoisOverlayOffset,
          "OffsetMeanOfRoisOverlay", little);
      }

      if (topoIsolineOverlayOffset != 0) {
        parseOverlays(topoIsolineOverlayOffset,
          "OffsetTopoIsolineOverlay", little);
      }

      if (topoProfileOverlayOffset != 0) {
        parseOverlays(topoProfileOverlayOffset,
          "OffsetTopoProfileOverlay", little);
      }

      if (linescanOverlayOffset != 0) {
        parseOverlays(linescanOverlayOffset, "OffsetLinescanOverlay", little);
      }
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
    catch (IOException exc) {
      if (debug) trace(exc);
    }

    Object pixelSizeX = getMeta("VoxelSizeX");
    Object pixelSizeY = getMeta("VoxelSizeY");
    Object pixelSizeZ = getMeta("VoxelSizeZ");

    Float pixX = new Float(pixelSizeX == null ? "0" : pixelSizeX.toString());
    Float pixY = new Float(pixelSizeY == null ? "0" : pixelSizeY.toString());
    Float pixZ = new Float(pixelSizeZ == null ? "0" : pixelSizeZ.toString());

    MetadataStore store = getMetadataStore();
    store.setDimensions(pixX, pixY, pixZ, null, null, null);

    // see if we have an associated MDB file

    Location dir = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] dirList = dir.list();

    for (int i=0; i<dirList.length; i++) {
      if (dirList[i].toLowerCase().endsWith(".mdb")) {
        try {
          MDBParser.parseDatabase((new Location(dir.getPath(),
            dirList[i])).getAbsolutePath(), metadata);
        }
        catch (FormatException exc) {
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
    super.initFile(id);

    // go through the IFD hashtable array and
    // remove anything with NEW_SUBFILE_TYPE = 1
    // NEW_SUBFILE_TYPE = 1 indicates that the IFD
    // contains a thumbnail image

    status("Removing thumbnails");

    int numThumbs = 0;
    long prevOffset = 0;
    byte[] b = new byte[48];
    byte[] c = new byte[48];
    for (int i=0; i<ifds.length; i++) {
      long subFileType = TiffTools.getIFDLongValue(ifds[i],
        TiffTools.NEW_SUBFILE_TYPE, true, 0);
      long[] offsets = TiffTools.getStripOffsets(ifds[i]);

      if (subFileType == 1) {
        ifds[i] = null;
        numThumbs++;
      }
      else if (i > 0) {
        // make sure that we don't grab the thumbnail by accident
        // there's probably a better way to do this

        in.seek(prevOffset);
        in.read(b);
        in.seek(offsets[0]);
        in.read(c);

        boolean equal = true;
        for (int j=0; j<48; j++) {
          if (b[j] != c[j]) {
            equal = false;
            j = 48;
          }
        }

        if (equal) {
          offsets[0] += (offsets[0] - prevOffset);
          TiffTools.putIFDValue(ifds[i], TiffTools.STRIP_OFFSETS, offsets);
        }
      }
      prevOffset = offsets[0];
    }

    // now copy ifds to a temp array so that we can get rid of
    // any null entries

    int ifdPointer = 0;
    Hashtable[] tempIFDs = new Hashtable[ifds.length - numThumbs];
    for (int i=0; i<tempIFDs.length; i++) {
      if (ifds[ifdPointer] != null) {
        tempIFDs[i] = ifds[ifdPointer];
        ifdPointer++;
      }
      else {
        while ((ifds[ifdPointer] == null) && ifdPointer < ifds.length) {
          ifdPointer++;
        }
        tempIFDs[i] = ifds[ifdPointer];
        ifdPointer++;
      }
    }

    // reset numImages and ifds
    core.imageCount[0] = tempIFDs.length;
    ifds = tempIFDs;
    initMetadata();
    ifds = TiffTools.getIFDs(in);

    if (ifds.length > 1) {
      core.thumbSizeX[0] = TiffTools.getIFDIntValue(ifds[1],
        TiffTools.IMAGE_WIDTH, false, 1);
      core.thumbSizeY[0] = TiffTools.getIFDIntValue(ifds[1],
        TiffTools.IMAGE_LENGTH, false, 1);
    }
  }

  // -- Helper methods --

  /** Parses overlay-related fields. */
  protected void parseOverlays(long data, String suffix, boolean little)
    throws IOException
  {
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
    byte[] temp = new byte[64];
    in.read(temp);
    put("FontFaceName-" + suffix, new String(temp));

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
        byte[] draw = new byte[drawingEl];
        in.read(draw);
        put("DrawingElement" + i + "-" + suffix, new String(draw));
      }
    }
    catch (ArithmeticException exc) {
      if (debug) trace(exc);
    }
  }

  /** Parses subblock-related fields. */
  protected void parseSubBlocks(long data, String suffix, boolean little)
    throws IOException, FormatException
  {
    if (data == 0) return;

    in.seek((int) data);

    in.order(little);

    long size = in.readInt();
    if (size < 0) size += 4294967296L;
    long numSubBlocks = in.readInt();
    if (numSubBlocks < 0) numSubBlocks += 4294967296L;
    put("NumSubBlocks-" + suffix, numSubBlocks);
    long numChannels = in.readInt();
    if (numChannels < 0) numChannels += 4294967296L;
    put("NumChannels-" + suffix, numChannels);
    data = in.readInt();
    if (data < 0) data += 4294967296L;
    put("LutType-" + suffix, data);
    data = in.readInt();
    if (data < 0) data += 4294967296L;
    put("Advanced-" + suffix, data);
    data = in.readInt();
    if (data < 0) data += 4294967296L;
    put("CurrentChannel-" + suffix, data);
    in.skipBytes(36);

    if (numSubBlocks > 100) numSubBlocks = 20;

    for (int i=0; i<numSubBlocks; i++) {
      data = in.readInt();
      if (data < 0) data += 4294967296L;
      put("Type" + i + "-" + suffix, data);
      put("Size" + i + "-" + suffix, in.readInt());

      switch ((int) data) {
        case 1:
          for (int j=0; j<numChannels; j++) {
            put("GammaChannel" + j + "-" + i + "-" + suffix, in.readDouble());
          }
          break;
        case 2:
          for (int j=0; j<numChannels; j++) {
            put("BrightnessChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
          }
          break;

        case 3:
          for (int j=0; j<numChannels; j++) {
            put("ContrastChannel" + j + "-" + i + "-" + suffix,
              in.readDouble());
          }
          break;

        case 4:
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

        case 5:
          // the specs are unclear as to how
          // this subblock should be read, so I'm
          // skipping it for the present
          break;

        case 6:
          // also skipping this block for
          // the moment
          break;
      }
    }
  }

}
