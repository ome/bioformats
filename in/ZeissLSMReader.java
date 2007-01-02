//
// ZeissLSMReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import loci.formats.*;

/**
 * ZeissLSMReader is the file format reader for Zeiss LSM files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ZeissLSMReader extends BaseTiffReader {

  // -- Constants --

  /** Tag identifying a Zeiss LSM file. */
  private static final int ZEISS_ID = 34412;

  // -- Fields --

  /** Number of timepoints. */
  private int tSize;

  /** Number of Z slices. */
  private int zSize;

  /** Number of channels. */
  private int channels;

  /** Dimension order. */
  private String dimOrder;

  // -- Constructor --

  /** Constructs a new Zeiss LSM reader. */
  public ZeissLSMReader() { super("Zeiss Laser-Scanning Microscopy", "lsm"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Zeiss LSM file. */
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

  /** Obtains a thumbnail for the specified image from the given file. */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (2*no + 1 < ifds.length) return TiffTools.getImage(ifds[2*no + 1], in);
    return super.openThumbImage(id, no);
  }

  /** Get the size of the X dimension for the thumbnail. */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (ifds.length == 1) return super.getThumbSizeX(id);
    return TiffTools.getIFDIntValue(ifds[1], TiffTools.IMAGE_WIDTH, false, 1);
  }

  /** Get the size of the Y dimension for the thumbnail. */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (ifds.length == 1) return super.getThumbSizeY(id);
    return TiffTools.getIFDIntValue(ifds[1], TiffTools.IMAGE_LENGTH, false, 1);
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    ifds = TiffTools.getIFDs(in);
    return TiffTools.getImage(ifds[2*no], in);
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    ifds = TiffTools.getIFDs(in);
    byte[][] p = TiffTools.getSamples(ifds[2*no], in);
    byte[] b = new byte[p.length * p[0].length];

    for (int i=0; i<p.length; i++) {
      System.arraycopy(p[i], 0, b, i*p[0].length, p[i].length);
    }

    return b;
  }

  /** Initializes the given Zeiss LSM file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);
    channels = 0;

    // go through the IFD hashtable array and
    // remove anything with NEw_SUBFILE_TYPE = 1
    // NEW_SUBFILE_TYPE = 1 indicates that the IFD
    // contains a thumbnail image

    int numThumbs = 0;
    try {
      long prevOffset = 0;
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
          byte[] b = new byte[48];
          in.read(b);
          in.seek(offsets[0]);
          byte[] c = new byte[48];
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
    }
    catch (Exception e) { }

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
    numImages = tempIFDs.length;
    ifds = tempIFDs;
    initMetadata();
    ifds = TiffTools.getIFDs(in);
  }

  /** Populates the metadata hashtable. */
  protected void initMetadata() {
    Hashtable ifd = ifds[0];

    long data = 0;
    int idata = 0;
    double ddata = 0;
    //short sdata = 0;

    try {
      // -- Parse standard metadata --

      // determine byte order
      boolean little = TiffTools.isLittleEndian(ifd);
      in.order(little);

      super.initMetadata();

      // get Zeiss LSM-specific data

      // grab the TIF_CZ_LSMINFO structure, 512 bytes long
      short[] cz = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
      int p = 0; // pointer to next byte in the structure

      put("MagicNumber", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;
      put("StructureSize", DataTools.bytesToInt(cz, p, little));
      p += 4;
      put("DimensionX", DataTools.bytesToInt(cz, p, little));
      p += 4;
      put("DimensionY", DataTools.bytesToInt(cz, p, little));
      p += 4;
      put("DimensionZ", DataTools.bytesToInt(cz, p, little));
      p += 4;
      int dimensionChannels = DataTools.bytesToInt(cz, p, little);
      put("DimensionChannels", dimensionChannels);
      p += 4;
      put("DimensionTime", DataTools.bytesToInt(cz, p, little));
      p += 4;

      idata = DataTools.bytesToInt(cz, p, little);
      String type;
      switch (idata) {
        case 1:
          type = "8 bit unsigned integer";
          break;
        case 2:
          type = "12 bit unsigned integer";
          break;
        case 5:
          type = "32 bit float";
          break;
        case 0:
          type = "varying data types";
          break;
        default:
          type = "8 bit unsigned integer";
      }
      put("DataType", type);
      p += 4;

      put("ThumbnailX", DataTools.bytesToInt(cz, p, little));
      p += 4;
      put("ThumbnailY", DataTools.bytesToInt(cz, p, little));
      p += 4;
      put("VoxelSizeX", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;
      put("VoxelSizeY", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;
      put("VoxelSizeZ", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8 + 24; // skip over the next 24 bytes

      idata = DataTools.bytesToInt(cz, p, 2, little);
      switch (idata) {
        case 0:
          type = "x-y-z scan";
          break;
        case 1:
          type = "z scan (x-z plane)";
          break;
        case 2:
          type = "line scan";
          break;
        case 3:
          type = "time series x-y";
          break;
        case 4:
          type = "time series x-z";
          break;
        case 5:
          type = "time series 'Mean of ROIs'";
          break;
        case 6:
          type = "time series x-y-z";
          break;
        case 7:
          type = "spline scan";
          break;
        case 8:
          type = "spline scan x-z";
          break;
        case 9:
          type = "time series spline plane x-z";
          break;
        case 10:
          type = "point mode";
          break;
        default:
          type = "x-y-z scan";
      }
      put("ScanType", type);
      p += 2;

      idata = DataTools.bytesToInt(cz, p, 2, little);
      switch (idata) {
        case 0:
          type = "no spectral scan";
          break;
        case 1:
          type = "acquired with spectral scan";
          break;
        default:
          type = "no spectral scan";
      }
      put("SpectralScan", type);
      p += 2;

      data = DataTools.bytesToLong(cz, p, 4, little);
      switch ((int) data) {
        case 0:
          type = "original scan data";
          break;
        case 1:
          type = "calculated data";
          break;
        case 2:
          type = "animation";
          break;
        default:
          type = "original scan data";
      }
      put("DataType2", type);
      p += 4;

      // -- Parse OME-XML metadata --

      short[] omeData = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);

      int imageWidth = DataTools.bytesToInt(omeData, 8, little);
      int imageLength = DataTools.bytesToInt(omeData, 12, little);
      zSize = DataTools.bytesToInt(omeData, 16, little);
      int cSize = DataTools.bytesToInt(omeData, 20, little);
      tSize = DataTools.bytesToInt(omeData, 24, little);

      int pixel = DataTools.bytesToInt(omeData, 28, little);
      switch (pixel) {
        case 1:
          pixelType[0] = FormatReader.UINT8;
          break;
        case 2:
          pixelType[0] = FormatReader.UINT16;
          break;
        case 5:
          pixelType[0] = FormatReader.FLOAT;
          break;
        default:
          pixelType[0] = FormatReader.UINT8;
      }

      short scanType = DataTools.bytesToShort(omeData, 88, little);
      switch ((int) scanType) {
        case 0:
          dimOrder = "XYZCT";
          break;
        case 1:
          dimOrder = "XYZCT";
          break;
        case 3:
          dimOrder = "XYTCZ";
          break;
        case 4:
          dimOrder = "XYZTC";
          break;
        case 5:
          dimOrder = "XYTCZ";
          break;
        case 6:
          dimOrder = "XYZTC";
          break;
        case 7:
          dimOrder = "XYCTZ";
          break;
        case 8:
          dimOrder = "XYCZT";
          break;
        case 9:
          dimOrder = "XYTCZ";
          break;
        default:
          dimOrder = "XYZCT";
      }

      channels = cSize;

      if (channels == 0) channels++;
      if (channels == 2) channels--;

      while (numImages > zSize * channels * tSize) {
        if (zSize > tSize) zSize++;
        else tSize++;
      }

      // The metadata store we're working with.
      MetadataStore store = getMetadataStore(currentId);

      store.setPixels(
        new Integer(imageWidth), // SizeX
        new Integer(imageLength), // SizeY
        new Integer(zSize), // SizeZ
        new Integer(channels), // SizeC
        new Integer(tSize), // SizeT
        new Integer(pixelType[0]), // PixelType
        null, // BigEndian
        dimOrder, // DimensionOrder
        null);

      int pos = in.getFilePointer();

      // the following 4 are file offsets
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetVectorOverlay", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseSubBlocks(data, "OffsetInputLut", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseSubBlocks(data, "OffsetOutputLut", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      // seek to this offset and read in the structure there
      // first we have to make sure that the structure actually exists

      if (data != 0) {
        pos = in.getFilePointer();

        in.seek(data + 4);
        pos = in.getFilePointer() - 4;

        int numColors = in.readInt();
        int numNames = in.readInt();

        if (numColors > getSizeC(currentId)) {
          in.seek(data - 2);
          pos = in.getFilePointer() + 7;
          in.order(!in.isLittleEndian());
          in.readInt();
          numColors = in.readInt();
          numNames = in.readInt();
        }

        idata = in.readInt();
        int nameData = in.readInt();
        long offsetNames = pos + idata; // will seek to this

        // read in the intensity value for each color

        in.seek(offsetNames);

        for (int i=0; i<numColors; i++) {
          data = in.readInt();
          put("Intensity" + i, data);
        }

        // read in the channel names

        for (int i=0; i<numNames; i++) {
          // we want to read until we find a null char
          StringBuffer sb = new StringBuffer();
          char current = (char) in.read();
          while (current != 0) {
            sb.append(current);
            current = (char) in.read();
          }
          String name = sb.toString();
          put("ChannelName" + i, name);
        }
        in.seek(pos);
        in.order(isLittleEndian(currentId));
      }
      p += 4;

      put("TimeInterval", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;

      // the following 8 are file offsets

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        in.skipBytes((int) data);

        for (int i=0; i<dimensionChannels; i++) {
          put("OffsetChannelDataTypes" + i, in.readInt());
        }
        in.seek(pos);
      }
      p += 4;

      put("OffsetScanInformation", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      put("OffsetKsData", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        pos = in.getFilePointer();
        in.skipBytes((int) data + 4);

        int numStamps = in.readInt();
        if (numStamps > 1000) numStamps = 1000;
        for (int i=0; i<numStamps; i++) {
          ddata = in.readDouble();
          put("TimeStamp" + i, ddata);
        }
        in.seek(pos);
      }
      p += 4;

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        pos = in.getFilePointer();

        long numBytes = in.readInt();
        int numEvents = in.readInt();
        in.seek((int) (pos + data + 8));

        for (int i=0; i<numEvents; i++) {
          in.readInt();
          ddata = in.readDouble();
          put("Time" + i, ddata);

          data = in.readInt();
          put("EventType" + i, data);

          byte[] descr = new byte[(int) (numBytes - 16)];
          in.read(descr);
          put("Description" + i, new String(descr));
        }
        in.seek(pos);
      }
      p += 4;

      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetRoi", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetBleachRoi", little);
      p += 4;
      put("OffsetNextRecording", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      put("DisplayAspectX", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;
      put("DisplayAspectY", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;
      put("DisplayAspectZ", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;
      put("DisplayAspectTime", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;

      // the following 4 are file offsets
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetMeanOfRoisOverlay", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetTopoIsolineOverlay", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetTopoProfileOverlay", little);
      p += 4;
      data = DataTools.bytesToLong(cz, p, 4, little);
      parseOverlays(data, "OffsetLinescanOverlay", little);
      p += 4;

      put("ToolbarFlags", DataTools.bytesToLong(cz, p, 4, little));

      // the following 2 are file offsets
      put("OffsetChannelWavelength", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;
      put("OffsetChannelFactors", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      put("ObjectiveSphereCorrection", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;

      // the following is a file offset
      put("OffsetUnmixParameters", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      in.seek(pos);
    }
    catch (FormatException e) { e.printStackTrace(); }
    catch (IOException e) { e.printStackTrace(); }
    catch (Exception e) { }

    sizeZ[0] = zSize > 0 ? zSize : 1;
    sizeC[0] = channels > 0 ? channels : 1;
    sizeT[0] = tSize > 0 ? tSize : 1;
    currentOrder[0] = "XYZCT";

    try {
      Float pixX = new Float(metadata.get("VoxelSizeX").toString());
      Float pixY = new Float(metadata.get("VoxelSizeY").toString());
      Float pixZ = new Float(metadata.get("VoxelSizeZ").toString());

      MetadataStore store = getMetadataStore(currentId);
      store.setDimensions(pixX, pixY, pixZ, null, null, null);
    }
    catch (Exception e) { }

    // see if we have an associated MDB file

    File dir =
      new File(getMappedId(currentId)).getAbsoluteFile().getParentFile();
    String[] dirList = dir.list();

    for (int i=0; i<dirList.length; i++) {
      if (dirList[i].toLowerCase().endsWith(".mdb")) {
        try {
          MDBParser.parseDatabase(dirList[i], metadata);
        }
        catch (FormatException f) { }
        i = dirList.length;
      }
    }

  }

  // -- Helper methods --

  /** Parses overlay-related fields. */
  protected void parseOverlays(long data, String suffix, boolean little)
    throws IOException, FormatException
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
    int drawingEl = (size - 194) / nde;
    for (int i=0; i<nde; i++) {
      byte[] draw = new byte[drawingEl];
      in.read(draw);
      put("DrawingElement" + i + "-" + suffix, new String(draw));
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

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ZeissLSMReader().testRead(args);
  }

}
