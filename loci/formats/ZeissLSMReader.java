//
// ZeissLSMReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.io.*;
import java.util.Hashtable;

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

  /** Initializes the given Zeiss LSM file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // go through the IFD hashtable array and
    // remove anything with NEw_SUBFILE_TYPE = 1
    // NEW_SUBFILE_TYPE = 1 indicates that the IFD
    // contains a thumbnail image

    int numThumbs = 0;
    for (int i=0; i<ifds.length; i++) {
      long subFileType = TiffTools.getIFDLongValue(ifds[i],
        TiffTools.NEW_SUBFILE_TYPE, true, 0);
      if (subFileType == 1) {
        ifds[i] = null;
        numThumbs++;
      }
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
    numImages = tempIFDs.length;
    ifds = tempIFDs;
  }

  /** Populates the metadata hashtable. */
  protected void initMetadata() {
    Hashtable ifd = ifds[0];

    long data = 0;
    int idata = 0;
    double ddata = 0;
    short sdata = 0;

    try {
      // -- Parse standard metadata --

      // determine byte order
      boolean little = TiffTools.isLittleEndian(ifd);

      in.seek(0);
      put("NewSubfileType", ifd, TiffTools.NEW_SUBFILE_TYPE);
      put("ImageWidth", ifd, TiffTools.IMAGE_WIDTH);
      put("ImageLength", ifd, TiffTools.IMAGE_LENGTH);
      put("BitsPerSample", ifd, TiffTools.BITS_PER_SAMPLE);

      int comp = TiffTools.getIFDIntValue(ifd,
        TiffTools.COMPRESSION, false, TiffTools.UNCOMPRESSED);
      String compression;
      switch (comp) {
        case 1: compression = "None"; break;
        case 2:
          compression = "CCITT Group 3 1-Dimensional Modified Huffman";
          break;
        case 3: compression = "CCITT T.4 bilevel encoding"; break;
        case 4: compression = "CCITT T.6 bilevel encoding"; break;
        case 5: compression = "LZW"; break;
        case 6: compression = "JPEG"; break;
        case 32773: compression = "PackBits"; break;
        default: compression = "None";
      }
      put("Compression", compression);

      int photo = TiffTools.getIFDIntValue(ifd,
        TiffTools.PHOTOMETRIC_INTERPRETATION, true, -1);

      String photoInterp;
      switch (photo) {
        case 0: photoInterp = "WhiteIsZero"; break;
        case 1: photoInterp = "BlackIsZero"; break;
        case 2: photoInterp = "RGB"; break;
        case 3: photoInterp = "Palette"; break;
        case 4: photoInterp = "Transparency Mask"; break;
        default: photoInterp = "unknown"; break;
      }
      put("PhotometricInterpretation", photoInterp);

      putInt("StripOffsets", ifd, TiffTools.STRIP_OFFSETS);
      putInt("SamplesPerPixel", ifd, TiffTools.SAMPLES_PER_PIXEL);
      putInt("StripByteCounts", ifd, TiffTools.STRIP_BYTE_COUNTS);
      putInt("ColorMap", ifd, TiffTools.COLOR_MAP);

      int planar = TiffTools.getIFDIntValue(ifd,
        TiffTools.PLANAR_CONFIGURATION);
      String planarConfig;
      switch (planar) {
        case 1: planarConfig = "Chunky"; break;
        case 2: planarConfig = "Planar"; break;
        default: planarConfig = "Chunky";
      }
      put("PlanarConfiguration", planarConfig);

      int predict = TiffTools.getIFDIntValue(ifd, TiffTools.PREDICTOR);
      String predictor;
      switch (predict) {
        case 1: predictor = "No prediction scheme"; break;
        case 2: predictor = "Horizontal differencing"; break;
        default: predictor = "No prediction scheme";
      }
      put("Predictor", predictor);

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
        case 1: type = "8 bit unsigned integer"; break;
        case 2: type = "12 bit unsigned integer"; break;
        case 5: type = "32 bit float"; break;
        case 0: type = "varying data types"; break;
        default: type = "8 bit unsigned integer"; break;
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
        case 0: type = "x-y-z scan"; break;
        case 1: type = "z scan (x-z plane)"; break;
        case 2: type = "line scan"; break;
        case 3: type = "time series x-y"; break;
        case 4: type = "time series x-z"; break;
        case 5: type = "time series 'Mean of ROIs'"; break;
        case 6: type = "time series x-y-z"; break;
        case 7: type = "spline scan"; break;
        case 8: type = "spline scan x-z"; break;
        case 9: type = "time series spline plane x-z"; break;
        case 10: type = "point mode"; break;
        default: type = "x-y-z scan";
      }
      put("ScanType", type);
      p += 2;

      idata = DataTools.bytesToInt(cz, p, 2, little);
      switch (idata) {
        case 0: type = "no spectral scan"; break;
        case 1: type = "acquired with spectral scan"; break;
        default: type = "no spectral scan";
      }
      put("SpectralScan", type);
      p += 2;

      data = DataTools.bytesToLong(cz, p, 4, little);
      switch ((int) data) {
        case 0: type = "original scan data"; break;
        case 1: type = "calculated data"; break;
        case 2: type = "animation"; break;
        default: type = "original scan data";
      }
      put("DataType2", type);
      p += 4;

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
        long fp = in.getFilePointer();
        in.seek(data);
        int blockSize = DataTools.read4SignedBytes(in, little);
        int numColors = DataTools.read4SignedBytes(in, little);
        int numNames = DataTools.read4SignedBytes(in, little);
        idata = DataTools.read4SignedBytes(in, little);
        long offset = data + idata; // will seek to this later
        idata = DataTools.read4SignedBytes(in, little);
        long offsetNames = data + idata; // will seek to this

        // read in the intensity value for each color
        in.seek(offset);
        for (int i=0; i<numColors; i++) {
          data = DataTools.read4UnsignedBytes(in, little);
          put("Intensity" + i, data);
        }

        // read in the channel names
        in.seek(offsetNames);
        for (int i=0; i<numNames; i++) {
          // we want to read until we find a null char
          String name = "";
          char[] current = new char[1];
          current[0] = in.readChar();
          while (current[0] != 0) {
            name.concat(new String(current));
            current[0] = in.readChar();
          }
          put("ChannelName" + i, name);
        }
        in.seek(fp);
      }
      p += 4;

      put("TimeInterval", Double.longBitsToDouble(
        DataTools.bytesToLong(cz, p, little)));
      p += 8;

      // the following 8 are file offsets

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        long fp = in.getFilePointer();
        in.seek(data);
        for (int i=0; i<dimensionChannels; i++) {
          data = DataTools.read4UnsignedBytes(in, little);
          put("OffsetChannelDataTypes" + i, data);
        }
        in.seek(fp);
      }
      p += 4;

      put("OffsetScanInformation", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      put("OffsetKsData", DataTools.bytesToLong(cz, p, 4, little));
      p += 4;

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        long fp = in.getFilePointer();
        in.seek(data);
        in.skipBytes(4);
        int numStamps = DataTools.read4SignedBytes(in, little);
        for (int i=0; i<numStamps; i++) {
          ddata = DataTools.readDouble(in, little);
          put("TimeStamp" + i, ddata);
        }
        in.seek(fp);
      }
      p += 4;

      data = DataTools.bytesToLong(cz, p, 4, little);
      if (data != 0) {
        long fp = in.getFilePointer();
        in.seek(data);
        long numBytes = DataTools.read4UnsignedBytes(in, little);
        int numEvents = DataTools.read4SignedBytes(in, little);
        for (int i=0; i<numEvents; i++) {
          in.skipBytes(4);
          ddata = DataTools.readDouble(in, little);
          put("Time" + i, ddata);

          data = DataTools.read4UnsignedBytes(in, little);
          put("EventType" + i, data);

          byte[] descr = new byte[(int) (numBytes - 16)];
          in.read(descr);
          put("Description" + i, new String(descr));
        }
        in.seek(fp);
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


      // -- Parse OME-XML metadata --

      short[] omeData = TiffTools.getIFDShortArray(ifd, ZEISS_ID, true);
      int magicNum = DataTools.bytesToInt(omeData, 0, little);

      ome = OMETools.createRoot();

      int photoInterp2 = TiffTools.getIFDIntValue(ifd,
        TiffTools.PHOTOMETRIC_INTERPRETATION, true, 0);
      String photo2;
      switch (photoInterp2) {
        case 0: photo2 = "monochrome"; break;
        case 1: photo2 = "monochrome"; break;
        case 2: photo2 = "RGB"; break;
        case 3: photo2 = "monochrome"; break;
        case 4: photo2 = "RGB"; break;
        default: photo2 = "monochrome";
      }
      OMETools.setAttribute(ome,
        "ChannelInfo", "PhotometricInterpretation", photo2);

      int imageWidth = DataTools.bytesToInt(omeData, 8, little);
      OMETools.setAttribute(ome, "Pixels", "SizeX", "" + imageWidth);

      int imageLength = DataTools.bytesToInt(omeData, 12, little);
      OMETools.setAttribute(ome, "Pixels", "SizeY", "" + imageLength);

      int zSize = DataTools.bytesToInt(omeData, 16, little);
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "" + zSize);

      int cSize = DataTools.bytesToInt(omeData, 20, little);
      OMETools.setAttribute(ome, "Pixels", "SizeC", "" + cSize);

      int tSize = DataTools.bytesToInt(omeData, 24, little);
      OMETools.setAttribute(ome, "Pixels", "SizeT", "" + tSize);

      int pixel = DataTools.bytesToInt(omeData, 28, little);
      String pixelType;
      switch (pixel) {
        case 1: pixelType = "Uint8"; break;
        case 2: pixelType = "Uint16"; break;
        case 5: pixelType = "float"; break;
        default: pixelType = "Uint8";
      }
      OMETools.setAttribute(ome, "Image", "PixelType", pixelType);

      short scanType = DataTools.bytesToShort(omeData, 88, little);
      String dimOrder;
      switch ((int) scanType) {
        case 0: dimOrder = "XYZCT"; break;
        case 1: dimOrder = "XYZCT"; break;
        case 3: dimOrder = "XYTCZ"; break;
        case 4: dimOrder = "XYZTC"; break;
        case 5: dimOrder = "XYTCZ"; break;
        case 6: dimOrder = "XYZTC"; break;
        case 7: dimOrder = "XYCTZ"; break;
        case 8: dimOrder = "XYCZT"; break;
        case 9: dimOrder = "XYTCZ"; break;
        default: dimOrder = "XYZCT";
      }
      OMETools.setAttribute(ome, "Pixels", "DimensionOrder", dimOrder);
    }
    catch (FormatException e) { e.printStackTrace(); }
    catch (IOException e) { e.printStackTrace(); }
  }


  // -- Helper methods --

  /** Parses overlay-related fields. */
  protected void parseOverlays(long data, String suffix, boolean little)
    throws IOException
  {
    if (data == 0) return;
    long fp = in.getFilePointer();
    in.seek(data);

    int nde = DataTools.read4SignedBytes(in, little);
    put("NumberDrawingElements-" + suffix, nde);
    int size = DataTools.read4SignedBytes(in, little);
    int idata = DataTools.read4SignedBytes(in, little);
    put("LineWidth-" + suffix, idata);
    idata = DataTools.read4SignedBytes(in, little);
    put("Measure-" + suffix, idata);
    in.skipBytes(8);
    put("ColorRed-" + suffix, DataTools.readSignedByte(in));
    put("ColorGreen-" + suffix, DataTools.readSignedByte(in));
    put("ColorBlue-" + suffix, DataTools.readSignedByte(in));
    in.skipBytes(1);

    put("Valid-" + suffix, DataTools.read4SignedBytes(in, little));
    put("KnotWidth-" + suffix, DataTools.read4SignedBytes(in, little));
    put("CatchArea-" + suffix, DataTools.read4SignedBytes(in, little));

    // some fields describing the font
    put("FontHeight-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontWidth-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontEscapement-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontOrientation-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontWeight-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontItalic-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontUnderline-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontStrikeOut-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontCharSet-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontOutPrecision-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontClipPrecision-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontQuality-" + suffix, DataTools.read4SignedBytes(in, little));
    put("FontPitchAndFamily-" + suffix,
      DataTools.read4SignedBytes(in, little));
    byte[] temp = new byte[64];
    in.read(temp);
    put("FontFaceName-" + suffix, new String(temp));

    // some flags for measuring values of different drawing element types
    put("ClosedPolyline-" + suffix, DataTools.readUnsignedByte(in));
    put("OpenPolyline-" + suffix, DataTools.readUnsignedByte(in));
    put("ClosedBezierCurve-" + suffix, DataTools.readUnsignedByte(in));
    put("OpenBezierCurve-" + suffix, DataTools.readUnsignedByte(in));
    put("ArrowWithClosedTip-" + suffix, DataTools.readUnsignedByte(in));
    put("ArrowWithOpenTip-" + suffix, DataTools.readUnsignedByte(in));
    put("Ellipse-" + suffix, DataTools.readUnsignedByte(in));
    put("Circle-" + suffix, DataTools.readUnsignedByte(in));
    put("Rectangle-" + suffix, DataTools.readUnsignedByte(in));
    put("Line-" + suffix, DataTools.readUnsignedByte(in));
    int drawingEl = (size - 194) / nde;
    for (int i=0; i<nde; i++) {
      byte[] draw = new byte[drawingEl];
      in.read(draw);
      put("DrawingElement" + i + "-" + suffix, new String(draw));
    }
    in.seek(fp);
  }

  /** Parses subblock-related fields. */
  protected void parseSubBlocks(long data, String suffix, boolean little)
    throws IOException
  {
    if (data == 0) return;
    long fp = in.getFilePointer();
    in.seek(data);

    long size = DataTools.read4UnsignedBytes(in, little);
    long numSubBlocks = DataTools.read4UnsignedBytes(in, little);
    put("NumSubBlocks-" + suffix, numSubBlocks);
    long numChannels = DataTools.read4UnsignedBytes(in, little);
    put("NumChannels-" + suffix, numChannels);
    data = DataTools.read4UnsignedBytes(in, little);
    put("LutType-" + suffix, data);
    data = DataTools.read4UnsignedBytes(in, little);
    put("Advanced-" + suffix, data);
    data = DataTools.read4UnsignedBytes(in, little);
    put("CurrentChannel-" + suffix, data);
    in.skipBytes(36);

    for (int i=0; i<numSubBlocks; i++) {
      data = DataTools.read4UnsignedBytes(in, little);
      put("Type" + i + "-" + suffix, data);

      put("Size" + i + "-" + suffix,
        DataTools.read4UnsignedBytes(in, little));

      switch ((int) data) {
        case 1:
          for (int j=0; j<numChannels; j++) {
            put("GammaChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
          }
          break;
        case 2:
          for (int j=0; j<numChannels; j++) {
            put("BrightnessChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
          }
          break;

        case 3:
          for (int j=0; j<numChannels; j++) {
            put("ContrastChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
          }
          break;

        case 4:
          for (int j=0; j<numChannels; j++) {
            put("RampStartXChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
            put("RampStartYChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
            put("RampEndXChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
            put("RampEndYChannel" + j + "-" + i + "-" + suffix,
              DataTools.readDouble(in, little));
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
    in.seek(fp);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ZeissLSMReader().testRead(args);
  }

}
