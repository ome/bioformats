//
// ZeissZVIReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ZeissZVIReader extends FormatReader {

  // -- Constants --

  private static final String NO_POI_MSG = "You need to install " +
    "Jakarta POI from http://jakarta.apache.org/poi/";


  // -- Static fields --

  private static boolean noPOI = false;

  private static ReflectedUniverse r = createReflectedUniverse();
  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import org.apache.poi.poifs.filesystem.POIFSFileSystem");
      r.exec("import org.apache.poi.poifs.filesystem.DirectoryEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentInputStream");
      r.exec("import java.util.Iterator");
    }
    catch (Throwable exc) { noPOI = true; }
    return r;
  }


  // -- Fields --

  /** An instance of the old ZVI reader, for use if this one fails. */
  private LegacyZVIReader legacy = new LegacyZVIReader();

  /** Flag indicating the current file requires the legacy ZVI reader. */
  private boolean needLegacy = false;

  private Hashtable pixelData = new Hashtable();
  private Hashtable headerData = new Hashtable();
  private byte[] header; // general image header data
  private int nImages = 0;  // number of images
  private byte[] tags;  // tags data

  // -- Fields used by parseDir --
  private int counter = 0;  // the number of the Image entry
  private int imageWidth = 0;
  private int imageHeight = 0;
  private int bytesPerPixel = 0;


  // -- Constructor --

  /** Constructs a new Zeiss ZVI reader. */
  public ZeissZVIReader() { super("Zeiss Vision Image", "zvi"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Zeiss ZVI file. */
  public boolean isThisType(byte[] block) {
    // just use the legacy version for now
    return legacy.isThisType(block);
  }

  /** Determines the number of images in the given Zeiss ZVI file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (noPOI) return legacy.getImageCount(id);
    if (!id.equals(currentId)) initFile(id);
    return needLegacy ? legacy.getImageCount(id) : nImages;
  }

  /** Obtains the specified image from the given Zeiss ZVI file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (noPOI) return legacy.open(id, no);
    if (!id.equals(currentId)) initFile(id);
    if (needLegacy) return legacy.open(id, no);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read image header data

    byte[] imageHead = (byte[]) headerData.get(new Integer(no));

    int pointer = 14;
    int numBytes = DataTools.bytesToInt(imageHead, pointer, 2, true);
    pointer += 2 + numBytes;

    pointer += 2;
    int width = DataTools.bytesToInt(imageHead, pointer, 4, true);
    pointer += 4 + 2;

    int height = DataTools.bytesToInt(imageHead, pointer, 4, true);
    pointer += 4 + 2;

    int depth = DataTools.bytesToInt(imageHead, pointer, 4, true);
    pointer += 4 + 2;

    int pixelFormat = DataTools.bytesToInt(imageHead, pointer, 4, true);
    pointer += 4 + 2;

    pointer += 6; // count field is always 0

    int validBPP = DataTools.bytesToInt(imageHead, pointer, 4, true);
    pointer += 4 + 2;

    // read image bytes and convert to floats

    pointer = 0;
    int numSamples = width*height;
    int[] bitsPerSample = {validBPP};

    int channels;
    switch (pixelFormat) {
      case 1: channels = 3; break;
      case 2: channels = 4; break;
      case 3: channels = 1; break;
      case 4: channels = 1; break;
      case 6: channels = 1; break;
      case 8: channels = 3; break;
      default: channels = 1;
    }

    byte[] px = (byte[]) pixelData.get(new Integer(no));
    byte[] tempPx = new byte[px.length];

    if (bitsPerSample[0] > 64) { bitsPerSample[0] = 8; }

    byte[][] samples = new byte[(int) channels][numSamples];

    if (bitsPerSample[0] == 8) {
      // special case for 8 bit data

      // we need to re-order the bytes to reverse each row

      for (int i=0; i<height; i++) {
        for (int j=0; j<width; j++) {
          tempPx[(width*i) + j] = px[(width*(i+1)) -1-j];
        }
      }

      // and now re-order bytes to reverse the row order

      for (int i=0; i<height; i++) {
        for (int j=0; j<width; j++) {
          px[(width*i) + j] = tempPx[j + (width*(height-1-i))];
        }
      }

      for (int i=0; i<4*numSamples; i++) {
        byte q = (byte) DataTools.bytesToShort(px, i, 1, true);

        if (i < numSamples) samples[0][i] = q;
        else if (channels > 1 && i<2*numSamples) {
          samples[1][i%numSamples] = q;
        }
        else if (channels > 2 && i<3*numSamples) {
          samples[2][i%numSamples] = q;
        }
        else if (channels == 4) {
          // if we happen to have RBG quad data
          samples[3][i%numSamples] = q;
        }
      }
    }
    else if (bitsPerSample[0] == 16) {
      // case for 16 bit data
      // this should eventually handle 32 bit data as well

      // we need to re-order the bytes to reverse each row

      for (int i=0; i<height; i++) {
        for (int j=0; j<2*width; j+=2) {
          tempPx[(2*width*i)+j] = px[(2*width*(i+1))-2-j];
          tempPx[(2*width*i)+j+1] =
            px[(2*width*(i+1))-1-j];
        }
      }

      System.arraycopy(tempPx, 0, px, 0, tempPx.length);

      for (int i=0; i<px.length-1; i+=2) {
        byte q = (byte) ((0x000000ff & px[i+1] << 8) |
          (0x000000ff & px[i]));

        if (i < 2*numSamples) {
          samples[0][i/2] = q;
        }
        else if (channels == 3 && i<4*numSamples) {
          samples[1][i%numSamples] = q;
        }
        else if (channels == 3) {
          samples[2][i%numSamples] = q;
        }
      }
    }
    return ImageTools.makeImage(samples, width, height);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (noPOI || needLegacy) legacy.close();
    needLegacy = false;
  }

  /** Initializes the given Zeiss ZVI file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (noPOI) throw new FormatException(NO_POI_MSG);
    super.initFile(id);

    try {
      r.setVar("fis", new FileInputStream(id));
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));
      if (nImages == 0) {
        if (DEBUG) System.out.println("Using LegacyZVIForm");
        needLegacy = true;
      }
    }
    catch (Throwable t) {
      noPOI = true;
      if (DEBUG) t.printStackTrace();
    }
    initMetadata();
  }


  // -- Helper methods --

  public static final void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    System.out.println(sb.toString());
  }

  /** Populates the metadata hashtable. */
  protected void initMetadata() {
    metadata.put("Legacy", needLegacy ? "yes" : "no");

    // parse the "header" byte array
    // right now we're using header data from an image item

    int pt = 14;
    int numBytes = DataTools.bytesToInt(header, pt, 2, true);
    pt += 2 + numBytes;

    pt += 2;

    metadata.put("ImageWidth",
      new Integer(DataTools.bytesToInt(header, pt, 4, true)));
    pt += 6;
    metadata.put("ImageHeight",
      new Integer(DataTools.bytesToInt(header, pt, 4, true)));
    pt += 6;
    pt += 6;
    int pixel = DataTools.bytesToInt(header, pt, 4, true);
    pt += 6;

    String fmt;
    switch (pixel) {
      case 1: fmt = "8-bit RGB Triple (B, G, R)"; break;
      case 2: fmt = "8-bit RGB Quad (B, G, R, A)"; break;
      case 3: fmt = "8-bit grayscale"; break;
      case 4: fmt = "16-bit signed integer"; break;
      case 5: fmt = "32-bit integer"; break;
      case 6: fmt = "32-bit IEEE float"; break;
      case 7: fmt = "64-bit IEEE float"; break;
      case 8: fmt = "16-bit unsigned RGB Triple (B, G, R)"; break;
      case 9: fmt = "32-bit RGB Triple (B, G, R)"; break;
      default: fmt = "unknown";
    }

    metadata.put("PixelFormat", fmt);

    metadata.put("NumberOfImages", new Integer(nImages));
    pt += 6;
    metadata.put("BitsPerPixel",
      new Integer(DataTools.bytesToInt(header, pt, 4, true)));
    pt += 6;

    if (ome != null) {
      String type;
      switch (pixel) {
        case 1: type = "Uint8"; break;
        case 2: type = "Uint8"; break;
        case 3: type = "Uint8"; break;
        case 4: type = "int16"; break;
        case 5: type = "Uint32"; break;
        case 6: type = "float"; break;
        case 7: type = "float"; break;
        case 8: type = "Uint16"; break;
        case 9: type = "Uint32"; break;
        default: type = "Uint8";
      }

      Integer sizeX = (Integer) metadata.get("ImageWidth");
      Integer sizeY = (Integer) metadata.get("ImageHeight");
      OMETools.setPixels(ome, sizeX, sizeY,
        new Integer(1), // SizeZ
        new Integer(1), // SizeC
        new Integer(nImages), // SizeT
        type, // PixelType
        null, // BigEndian
        "XYZTC"); // DimensionOrder

      OMETools.setImageName(ome, currentId);
    }

    // parse the "tags" byte array

    pt = 0;
    pt += 16;

    int w = DataTools.bytesToInt(tags, pt, 2, true);
    pt += 2;

    int version = DataTools.bytesToInt(tags, pt, 4, true);
    pt += 4;
    w = DataTools.bytesToInt(tags, pt, 2, true);
    pt += 2;
    int numTags = DataTools.bytesToInt(tags, pt, 4, true);
    pt += 4;

    for (int i=0; i<numTags; i++) {
      // we have duples of {value, ID} form

      // first read the data type
      int type = DataTools.bytesToInt(tags, pt, 2, true);
      pt += 2;

      // read in appropriate amount of data
      Object data;
      int length;
      switch (type) {
        case 2: // VT_I2: 16 bit integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 2, true));
          pt += 2;
          break;
        case 3: // VT_I4: 32 bit integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 4, true));
          pt += 4;
          break;
        case 4: // VT_R4: 32 bit float
          data = new Float(Float.intBitsToFloat(
            DataTools.bytesToInt(tags, pt, 4, true)));
          pt += 4;
          break;
        case 5: // VT_R8: 64 bit float
          data = new Double(Double.longBitsToDouble(
            DataTools.bytesToLong(tags, pt, 8, true)));
          pt += 8;
          break;
        case 7: // VT_DATE: 64 bit float
          data = new Double(Double.longBitsToDouble(
            DataTools.bytesToLong(tags, pt, 8, true)));
          pt += 8;
          break;
        case 8: // VT_BSTR: streamed storage object
          length = DataTools.bytesToInt(tags, pt, 2, true);
          pt += 2;
          data = new String(tags, pt, length);
          pt += length;
          break;
        case 11: // VT_BOOL: 16 bit integer (true if !0)
          int temp = DataTools.bytesToInt(tags, pt, 4, true);
          data = new Boolean(temp != 0);
          pt += 4;
          break;
        case 16: // VT_I1: 8 bit integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 1, true));
          pt += 1;
          break;
        case 17: // VT_UI1: 8 bit unsigned integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 1, true));
          pt += 1;
          break;
        case 18: // VT_UI2: 16 bit unsigned integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 2, true));
          pt += 2;
          break;
        case 19: // VT_UI4: 32 bit unsigned integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 4, true));
          pt += 4;
          break;
        case 20: // VT_I8: 64 bit integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 8, true));
          pt += 8;
          break;
        case 21: // VT_UI8: 64 bit unsigned integer
          data = new Integer(DataTools.bytesToInt(tags, pt, 8, true));
          pt += 8;
          break;
        case 65: // VT_BLOB: binary data
          length = DataTools.bytesToInt(tags, pt, 4, true);
          pt += 4;
          try {
            data = new String(tags, pt, length);
          }
          catch (Throwable e) { data = null; }
          pt += length;
          break;
        case 68: // VT_STORED_OBJECT: streamed storage object
          length = DataTools.bytesToInt(tags, pt, 2, true);
          pt += 2;
          data = new String(tags, pt, length);
          pt += length;
          break;
        default:
          pt += 4;
          data = null;
      }

      // read in tag ID
      int tagID = DataTools.bytesToInt(tags, pt, 4, true);
      pt += 4;

      // really ugly switch statement to put metadata in hashtable
      if (data != null) {
        switch (tagID) {
          case 222: metadata.put("Compression", data); break;
          case 258:
            metadata.put("BlackValue", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome,
//                "Grey Channel", "BlackLevel", "" + data);
//            }
            break;
          case 259:
            metadata.put("WhiteValue", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome,
//                "Grey Channel", "WhiteLevel", "" + data);
//            }
            break;
          case 260:
            metadata.put("ImageDataMappingAutoRange", data);
            break;
          case 261:
            metadata.put("Thumbnail", data);
            break;
          case 262:
            metadata.put("GammaValue", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome,
//                "Grey Channel", "GammaLevel", "" + data);
//            }
            break;
          case 264: metadata.put("ImageOverExposure", data); break;
          case 265: metadata.put("ImageRelativeTime1", data); break;
          case 266: metadata.put("ImageRelativeTime2", data); break;
          case 267: metadata.put("ImageRelativeTime3", data); break;
          case 268: metadata.put("ImageRelativeTime4", data); break;
          case 515: metadata.put("ImageWidth", data); break;
          case 516: metadata.put("ImageHeight", data); break;
          case 518: metadata.put("PixelType", data); break;
          case 519: metadata.put("NumberOfRawImages", data); break;
          case 520: metadata.put("ImageSize", data); break;
          case 523: metadata.put("Acquisition pause annotation", data); break;
          case 530: metadata.put("Document Subtype", data); break;
          case 531: metadata.put("Acquisition Bit Depth", data); break;
          case 534: metadata.put("Z-Stack single representative", data); break;
          case 769: metadata.put("Scale Factor for X", data); break;
          case 770: metadata.put("Scale Unit for X", data); break;
          case 771: metadata.put("Scale Width", data); break;
          case 772: metadata.put("Scale Factor for Y", data); break;
          case 773: metadata.put("Scale Unit for Y", data); break;
          case 774: metadata.put("Scale Height", data); break;
          case 775: metadata.put("Scale Factor for Z", data); break;
          case 776: metadata.put("Scale Unit for Z", data); break;
          case 777: metadata.put("Scale Depth", data); break;
          case 778: metadata.put("Scaling Parent", data); break;
          case 1001:
            metadata.put("Date", data);
            if (ome != null) OMETools.setCreationDate(ome, data.toString());
            break;
          case 1002: metadata.put("code", data); break;
          case 1003: metadata.put("Source", data); break;
          case 1004: metadata.put("Message", data); break;
          case 1026: metadata.put("8-bit acquisition", data); break;
          case 1027: metadata.put("Camera Bit Depth", data); break;
          case 1029: metadata.put("MonoReferenceLow", data); break;
          case 1030: metadata.put("MonoReferenceHigh", data); break;
          case 1031: metadata.put("RedReferenceLow", data); break;
          case 1032: metadata.put("RedReferenceHigh", data); break;
          case 1033: metadata.put("GreenReferenceLow", data); break;
          case 1034: metadata.put("GreenReferenceHigh", data); break;
          case 1035: metadata.put("BlueReferenceLow", data); break;
          case 1036: metadata.put("BlueReferenceHigh", data); break;
          case 1041: metadata.put("FrameGrabber Name", data); break;
          case 1042: metadata.put("Camera", data); break;
          case 1044: metadata.put("CameraTriggerSignalType", data); break;
          case 1045: metadata.put("CameraTriggerEnable", data); break;
          case 1046: metadata.put("GrabberTimeout", data); break;
          case 1281:
            metadata.put("MultiChannelEnabled", data);
            if (((Integer) data).intValue() == 1 && ome != null) {
              OMETools.setSizeC(ome, nImages);
              OMETools.setSizeT(ome, 1);
              OMETools.setDimensionOrder(ome, "XYCZT");
            }
            break;
          case 1282: metadata.put("MultiChannel Color", data); break;
          case 1283: metadata.put("MultiChannel Weight", data); break;
          case 1284: metadata.put("Channel Name", data); break;
          case 1536: metadata.put("DocumentInformationGroup", data); break;
          case 1537:
            metadata.put("Title", data);
            if (ome != null) OMETools.setImageName(ome, data.toString());
            break;
          case 1538:
            metadata.put("Author", data);
            if (ome != null) {
              // populate Experimenter element
              String name = data.toString();
              if (name != null) {
                String firstName = null, lastName = null;
                int ndx = name.indexOf(" ");
                if (ndx < 0) lastName = name;
                else {
                  firstName = name.substring(0, ndx);
                  lastName = name.substring(ndx + 1);
                }
                OMETools.setExperimenter(ome,
                  firstName, lastName, null, null, null, null);
              }
            }
            break;
          case 1539: metadata.put("Keywords", data); break;
          case 1540:
            metadata.put("Comments", data);
            if (ome != null) OMETools.setDescription(ome, data.toString());
            break;
          case 1541: metadata.put("SampleID", data); break;
          case 1542: metadata.put("Subject", data); break;
          case 1543: metadata.put("RevisionNumber", data); break;
          case 1544: metadata.put("Save Folder", data); break;
          case 1545: metadata.put("FileLink", data); break;
          case 1546: metadata.put("Document Type", data); break;
          case 1547: metadata.put("Storage Media", data); break;
          case 1548: metadata.put("File ID", data); break;
          case 1549: metadata.put("Reference", data); break;
          case 1550: metadata.put("File Date", data); break;
          case 1551: metadata.put("File Size", data); break;
          case 1553: metadata.put("Filename", data); break;
          case 1792:
            metadata.put("ProjectGroup", data);
            if (ome != null) {
              OMETools.setGroup(ome, data.toString(), null, null);
            }
            break;
          case 1793: metadata.put("Acquisition Date", data); break;
          case 1794: metadata.put("Last modified by", data); break;
          case 1795: metadata.put("User company", data); break;
          case 1796: metadata.put("User company logo", data); break;
          case 1797: metadata.put("Image", data); break;
          case 1800: metadata.put("User ID", data); break;
          case 1801: metadata.put("User Name", data); break;
          case 1802: metadata.put("User City", data); break;
          case 1803: metadata.put("User Address", data); break;
          case 1804: metadata.put("User Country", data); break;
          case 1805: metadata.put("User Phone", data); break;
          case 1806: metadata.put("User Fax", data); break;
          case 2049: metadata.put("Objective Name", data); break;
          case 2050: metadata.put("Optovar", data); break;
          case 2051: metadata.put("Reflector", data); break;
          case 2052: metadata.put("Condenser Contrast", data); break;
          case 2053: metadata.put("Transmitted Light Filter 1", data); break;
          case 2054: metadata.put("Transmitted Light Filter 2", data); break;
          case 2055: metadata.put("Reflected Light Shutter", data); break;
          case 2056: metadata.put("Condenser Front Lens", data); break;
          case 2057: metadata.put("Excitation Filter Name", data); break;
          case 2060:
            metadata.put("Transmitted Light Fieldstop Aperture", data);
            break;
          case 2061: metadata.put("Reflected Light Aperture", data); break;
          case 2062: metadata.put("Condenser N.A.", data); break;
          case 2063: metadata.put("Light Path", data); break;
          case 2064: metadata.put("HalogenLampOn", data); break;
          case 2065: metadata.put("Halogen Lamp Mode", data); break;
          case 2066: metadata.put("Halogen Lamp Voltage", data); break;
          case 2068: metadata.put("Fluorescence Lamp Level", data); break;
          case 2069: metadata.put("Fluorescence Lamp Intensity", data); break;
          case 2070: metadata.put("LightManagerEnabled", data); break;
          case 2072: metadata.put("Focus Position", data); break;
          case 2073:
            metadata.put("Stage Position X", data);
            if (ome != null) {
              OMETools.setStageX(ome, Integer.parseInt(data.toString()));
            }
            break;
          case 2074:
            metadata.put("Stage Position Y", data);
            if (ome != null) {
              OMETools.setStageY(ome, Integer.parseInt(data.toString()));
            }
            break;
          case 2075:
            metadata.put("Microscope Name", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome, "Microscope", "Name", "" + data);
//            }
            break;
          case 2076: metadata.put("Objective Magnification", data); break;
          case 2077: metadata.put("Objective N.A.", data); break;
          case 2078: metadata.put("MicroscopeIllumination", data); break;
          case 2079: metadata.put("External Shutter 1", data); break;
          case 2080: metadata.put("External Shutter 2", data); break;
          case 2081: metadata.put("External Shutter 3", data); break;
          case 2082: metadata.put("External Filter Wheel 1 Name", data); break;
          case 2083: metadata.put("External Filter Wheel 2 Name", data); break;
          case 2084: metadata.put("Parfocal Correction", data); break;
          case 2086: metadata.put("External Shutter 4", data); break;
          case 2087: metadata.put("External Shutter 5", data); break;
          case 2088: metadata.put("External Shutter 6", data); break;
          case 2089: metadata.put("External Filter Wheel 3 Name", data); break;
          case 2090: metadata.put("External Filter Wheel 4 Name", data); break;
          case 2103: metadata.put("Objective Turret Position", data); break;
          case 2104: metadata.put("Objective Contrast Method", data); break;
          case 2105: metadata.put("Objective Immersion Type", data); break;
          case 2107: metadata.put("Reflector Position", data); break;
          case 2109:
            metadata.put("Transmitted Light Filter 1 Position", data);
            break;
          case 2110:
            metadata.put("Transmitted Light Filter 2 Position", data);
            break;
          case 2112: metadata.put("Excitation Filter Position", data); break;
          case 2113: metadata.put("Lamp Mirror Position", data); break;
          case 2114:
            metadata.put("External Filter Wheel 1 Position", data);
            break;
          case 2115:
            metadata.put("External Filter Wheel 2 Position", data);
            break;
          case 2116:
            metadata.put("External Filter Wheel 3 Position", data);
            break;
          case 2117:
            metadata.put("External Filter Wheel 4 Position", data);
            break;
          case 2118: metadata.put("Lightmanager Mode", data); break;
          case 2119: metadata.put("Halogen Lamp Calibration", data); break;
          case 2120: metadata.put("CondenserNAGoSpeed", data); break;
          case 2121:
            metadata.put("TransmittedLightFieldstopGoSpeed", data);
            break;
          case 2122: metadata.put("OptovarGoSpeed", data); break;
          case 2123: metadata.put("Focus calibrated", data); break;
          case 2124: metadata.put("FocusBasicPosition", data); break;
          case 2125: metadata.put("FocusPower", data); break;
          case 2126: metadata.put("FocusBacklash", data); break;
          case 2127: metadata.put("FocusMeasurementOrigin", data); break;
          case 2128: metadata.put("FocusMeasurementDistance", data); break;
          case 2129: metadata.put("FocusSpeed", data); break;
          case 2130: metadata.put("FocusGoSpeed", data); break;
          case 2131: metadata.put("FocusDistance", data); break;
          case 2132: metadata.put("FocusInitPosition", data); break;
          case 2133: metadata.put("Stage calibrated", data); break;
          case 2134: metadata.put("StagePower", data); break;
          case 2135: metadata.put("StageXBacklash", data); break;
          case 2136: metadata.put("StageYBacklash", data); break;
          case 2137: metadata.put("StageSpeedX", data); break;
          case 2138: metadata.put("StageSpeedY", data); break;
          case 2139: metadata.put("StageSpeed", data); break;
          case 2140: metadata.put("StageGoSpeedX", data); break;
          case 2141: metadata.put("StageGoSpeedY", data); break;
          case 2142: metadata.put("StageStepDistanceX", data); break;
          case 2143: metadata.put("StageStepDistanceY", data); break;
          case 2144: metadata.put("StageInitialisationPositionX", data); break;
          case 2145: metadata.put("StageInitialisationPositionY", data); break;
          case 2146: metadata.put("MicroscopeMagnification", data); break;
          case 2147: metadata.put("ReflectorMagnification", data); break;
          case 2148: metadata.put("LampMirrorPosition", data); break;
          case 2149: metadata.put("FocusDepth", data); break;
          case 2150:
            metadata.put("MicroscopeType", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome, "Microscope", "Type", "" + data);
//            }
            break;
          case 2151: metadata.put("Objective Working Distance", data); break;
          case 2152:
            metadata.put("ReflectedLightApertureGoSpeed", data);
            break;
          case 2153: metadata.put("External Shutter", data); break;
          case 2154: metadata.put("ObjectiveImmersionStop", data); break;
          case 2155: metadata.put("Focus Start Speed", data); break;
          case 2156: metadata.put("Focus Acceleration", data); break;
          case 2157: metadata.put("ReflectedLightFieldstop", data); break;
          case 2158:
            metadata.put("ReflectedLightFieldstopGoSpeed", data);
            break;
          case 2159: metadata.put("ReflectedLightFilter 1", data); break;
          case 2160: metadata.put("ReflectedLightFilter 2", data); break;
          case 2161:
            metadata.put("ReflectedLightFilter1Position", data);
            break;
          case 2162:
            metadata.put("ReflectedLightFilter2Position", data);
            break;
          case 2163: metadata.put("TransmittedLightAttenuator", data); break;
          case 2164: metadata.put("ReflectedLightAttenuator", data); break;
          case 2165: metadata.put("Transmitted Light Shutter", data); break;
          case 2166:
            metadata.put("TransmittedLightAttenuatorGoSpeed", data);
            break;
          case 2167:
            metadata.put("ReflectedLightAttenuatorGoSpeed", data);
            break;
          case 2176:
            metadata.put("TransmittedLightVirtualFilterPosition", data);
            break;
          case 2177:
            metadata.put("TransmittedLightVirtualFilter", data);
            break;
          case 2178:
            metadata.put("ReflectedLightVirtualFilterPosition", data);
            break;
          case 2179: metadata.put("ReflectedLightVirtualFilter", data); break;
          case 2180:
            metadata.put("ReflectedLightHalogenLampMode", data);
            break;
          case 2181:
            metadata.put("ReflectedLightHalogenLampVoltage", data);
            break;
          case 2182:
            metadata.put("ReflectedLightHalogenLampColorTemperature", data);
            break;
          case 2183: metadata.put("ContrastManagerMode", data); break;
          case 2184: metadata.put("Dazzle Protection Active", data); break;
          case 2195:
            metadata.put("Zoom", data);
//            if (ome != null) {
//              OMETools.setAttribute(ome,
//                "DisplayOptions", "Zoom", "" + data);
//            }
            break;
          case 2196: metadata.put("ZoomGoSpeed", data); break;
          case 2197: metadata.put("LightZoom", data); break;
          case 2198: metadata.put("LightZoomGoSpeed", data); break;
          case 2199: metadata.put("LightZoomCoupled", data); break;
          case 2200:
            metadata.put("TransmittedLightHalogenLampMode", data);
            break;
          case 2201:
            metadata.put("TransmittedLightHalogenLampVoltage", data);
            break;
          case 2202:
            metadata.put("TransmittedLightHalogenLampColorTemperature", data);
            break;
          case 2203: metadata.put("Reflected Coldlight Mode", data); break;
          case 2204:
            metadata.put("Reflected Coldlight Intensity", data);
            break;
          case 2205:
            metadata.put("Reflected Coldlight Color Temperature", data);
            break;
          case 2206: metadata.put("Transmitted Coldlight Mode", data); break;
          case 2207:
            metadata.put("Transmitted Coldlight Intensity", data);
            break;
          case 2208:
            metadata.put("Transmitted Coldlight Color Temperature", data);
            break;
          case 2209:
            metadata.put("Infinityspace Portchanger Position", data);
            break;
          case 2210: metadata.put("Beamsplitter Infinity Space", data); break;
          case 2211: metadata.put("TwoTv VisCamChanger Position", data); break;
          case 2212: metadata.put("Beamsplitter Ocular", data); break;
          case 2213:
            metadata.put("TwoTv CamerasChanger Position", data);
            break;
          case 2214: metadata.put("Beamsplitter Cameras", data); break;
          case 2215: metadata.put("Ocular Shutter", data); break;
          case 2216: metadata.put("TwoTv CamerasChangerCube", data); break;
          case 2218: metadata.put("Ocular Magnification", data); break;
          case 2219: metadata.put("Camera Adapter Magnification", data); break;
          case 2220: metadata.put("Microscope Port", data); break;
          case 2221: metadata.put("Ocular Total Magnification", data); break;
          case 2222: metadata.put("Field of View", data); break;
          case 2223: metadata.put("Ocular", data); break;
          case 2224: metadata.put("CameraAdapter", data); break;
          case 2225: metadata.put("StageJoystickEnabled", data); break;
          case 2226:
            metadata.put("ContrastManager Contrast Method", data);
            break;
          case 2229:
            metadata.put("CamerasChanger Beamsplitter Type", data);
            break;
          case 2235: metadata.put("Rearport Slider Position", data); break;
          case 2236: metadata.put("Rearport Source", data); break;
          case 2237:
            metadata.put("Beamsplitter Type Infinity Space", data);
            break;
          case 2238: metadata.put("Fluorescence Attenuator", data); break;
          case 2239:
            metadata.put("Fluorescence Attenuator Position", data);
            break;
          case 2307: metadata.put("Camera Framestart Left", data); break;
          case 2308: metadata.put("Camera Framestart Top", data); break;
          case 2309: metadata.put("Camera Frame Width", data); break;
          case 2310: metadata.put("Camera Frame Height", data); break;
          case 2311: metadata.put("Camera Binning", data); break;
          case 2312: metadata.put("CameraFrameFull", data); break;
          case 2313: metadata.put("CameraFramePixelDistance", data); break;
          case 2318: metadata.put("DataFormatUseScaling", data); break;
          case 2319: metadata.put("CameraFrameImageOrientation", data); break;
          case 2320: metadata.put("VideoMonochromeSignalType", data); break;
          case 2321: metadata.put("VideoColorSignalType", data); break;
          case 2322: metadata.put("MeteorChannelInput", data); break;
          case 2323: metadata.put("MeteorChannelSync", data); break;
          case 2324: metadata.put("WhiteBalanceEnabled", data); break;
          case 2325: metadata.put("CameraWhiteBalanceRed", data); break;
          case 2326: metadata.put("CameraWhiteBalanceGreen", data); break;
          case 2327: metadata.put("CameraWhiteBalanceBlue", data); break;
          case 2331: metadata.put("CameraFrameScalingFactor", data); break;
          case 2562: metadata.put("Meteor Camera Type", data); break;
          case 2564: metadata.put("Exposure Time [ms]", data); break;
          case 2568:
            metadata.put("CameraExposureTimeAutoCalculate", data);
            break;
          case 2569: metadata.put("Meteor Gain Value", data); break;
          case 2571: metadata.put("Meteor Gain Automatic", data); break;
          case 2572: metadata.put("MeteorAdjustHue", data); break;
          case 2573: metadata.put("MeteorAdjustSaturation", data); break;
          case 2574: metadata.put("MeteorAdjustRedLow", data); break;
          case 2575: metadata.put("MeteorAdjustGreenLow", data); break;
          case 2576: metadata.put("Meteor Blue Low", data); break;
          case 2577: metadata.put("MeteorAdjustRedHigh", data); break;
          case 2578: metadata.put("MeteorAdjustGreenHigh", data); break;
          case 2579: metadata.put("MeteorBlue High", data); break;
          case 2582:
            metadata.put("CameraExposureTimeCalculationControl", data);
            break;
          case 2585:
            metadata.put("AxioCamFadingCorrectionEnable", data);
            break;
          case 2587: metadata.put("CameraLiveImage", data); break;
          case 2588: metadata.put("CameraLiveEnabled", data); break;
          case 2589: metadata.put("LiveImageSyncObjectName", data); break;
          case 2590: metadata.put("CameraLiveSpeed", data); break;
          case 2591: metadata.put("CameraImage", data); break;
          case 2592: metadata.put("CameraImageWidth", data); break;
          case 2593: metadata.put("CameraImageHeight", data); break;
          case 2594: metadata.put("CameraImagePixelType", data); break;
          case 2595: metadata.put("CameraImageShMemoryName", data); break;
          case 2596: metadata.put("CameraLiveImageWidth", data); break;
          case 2597: metadata.put("CameraLiveImageHeight", data); break;
          case 2598: metadata.put("CameraLiveImagePixelType", data); break;
          case 2599: metadata.put("CameraLiveImageShMemoryName", data); break;
          case 2600: metadata.put("CameraLiveMaximumSpeed", data); break;
          case 2601: metadata.put("CameraLiveBinning", data); break;
          case 2602: metadata.put("CameraLiveGainValue", data); break;
          case 2603: metadata.put("CameraLiveExposureTimeValue", data); break;
          case 2604: metadata.put("CameraLiveScalingFactor", data); break;
          case 2822: metadata.put("ImageTile Index", data); break;
          case 2823: metadata.put("Image acquisition Index", data); break;
          case 2841: metadata.put("Original Stage Position X", data); break;
          case 2842: metadata.put("Original Stage Position Y", data); break;
          case 3088: metadata.put("LayerDrawFlags", data); break;
          case 3334: metadata.put("RemainingTime", data); break;
          case 3585: metadata.put("User Field 1", data); break;
          case 3586: metadata.put("User Field 2", data); break;
          case 3587: metadata.put("User Field 3", data); break;
          case 3588: metadata.put("User Field 4", data); break;
          case 3589: metadata.put("User Field 5", data); break;
          case 3590: metadata.put("User Field 6", data); break;
          case 3591: metadata.put("User Field 7", data); break;
          case 3592: metadata.put("User Field 8", data); break;
          case 3593: metadata.put("User Field 9", data); break;
          case 3594: metadata.put("User Field 10", data); break;
          case 3840: metadata.put("ID", data); break;
          case 3841: metadata.put("Name", data); break;
          case 3842: metadata.put("Value", data); break;
          case 5501: metadata.put("PvCamClockingMode", data); break;
          case 8193: metadata.put("Autofocus Status Report", data); break;
          case 8194: metadata.put("Autofocus Position", data); break;
          case 8195: metadata.put("Autofocus Position Offset", data); break;
          case 8196:
            metadata.put("Autofocus Empty Field Threshold", data);
            break;
          case 8197: metadata.put("Autofocus Calibration Name", data); break;
          case 8198:
            metadata.put("Autofocus Current Calibration Item", data);
            break;
          case 65537: metadata.put("CameraFrameFullWidth", data); break;
          case 65538: metadata.put("CameraFrameFullHeight", data); break;
          case 65541: metadata.put("AxioCam Shutter Signal", data); break;
          case 65542: metadata.put("AxioCam Delay Time", data); break;
          case 65543: metadata.put("AxioCam Shutter Control", data); break;
          case 65544:
            metadata.put("AxioCam BlackRefIsCalculated", data);
            break;
          case 65545: metadata.put("AxioCam Black Reference", data); break;
          case 65547: metadata.put("Camera Shading Correction", data); break;
          case 65550: metadata.put("AxioCam Enhance Color", data); break;
          case 65551: metadata.put("AxioCam NIR Mode", data); break;
          case 65552: metadata.put("CameraShutterCloseDelay", data); break;
          case 65553:
            metadata.put("CameraWhiteBalanceAutoCalculate", data);
            break;
          case 65556: metadata.put("AxioCam NIR Mode Available", data); break;
          case 65557:
            metadata.put("AxioCam Fading Correction Available", data);
            break;
          case 65559:
            metadata.put("AxioCam Enhance Color Available", data);
            break;
          case 65565: metadata.put("MeteorVideoNorm", data); break;
          case 65566: metadata.put("MeteorAdjustWhiteReference", data); break;
          case 65567: metadata.put("MeteorBlackReference", data); break;
          case 65568: metadata.put("MeteorChannelInputCountMono", data); break;
          case 65570: metadata.put("MeteorChannelInputCountRGB", data); break;
          case 65571: metadata.put("MeteorEnableVCR", data); break;
          case 65572: metadata.put("Meteor Brightness", data); break;
          case 65573: metadata.put("Meteor Contrast", data); break;
          case 65575: metadata.put("AxioCam Selector", data); break;
          case 65576: metadata.put("AxioCam Type", data); break;
          case 65577: metadata.put("AxioCam Info", data); break;
          case 65580: metadata.put("AxioCam Resolution", data); break;
          case 65581: metadata.put("AxioCam Color Model", data); break;
          case 65582: metadata.put("AxioCam MicroScanning", data); break;
          case 65585: metadata.put("Amplification Index", data); break;
          case 65586: metadata.put("Device Command", data); break;
          case 65587: metadata.put("BeamLocation", data); break;
          case 65588: metadata.put("ComponentType", data); break;
          case 65589: metadata.put("ControllerType", data); break;
          case 65590:
            metadata.put("CameraWhiteBalanceCalculationRedPaint", data);
            break;
          case 65591:
            metadata.put("CameraWhiteBalanceCalculationBluePaint", data);
            break;
          case 65592: metadata.put("CameraWhiteBalanceSetRed", data); break;
          case 65593: metadata.put("CameraWhiteBalanceSetGreen", data); break;
          case 65594: metadata.put("CameraWhiteBalanceSetBlue", data); break;
          case 65595:
            metadata.put("CameraWhiteBalanceSetTargetRed", data);
            break;
          case 65596:
            metadata.put("CameraWhiteBalanceSetTargetGreen", data);
            break;
          case 65597:
            metadata.put("CameraWhiteBalanceSetTargetBlue", data);
            break;
          case 65598: metadata.put("ApotomeCamCalibrationMode", data); break;
          case 65599: metadata.put("ApoTome Grid Position", data); break;
          case 65600: metadata.put("ApotomeCamScannerPosition", data); break;
          case 65601: metadata.put("ApoTome Full Phase Shift", data); break;
          case 65602: metadata.put("ApoTome Grid Name", data); break;
          case 65603: metadata.put("ApoTome Staining", data); break;
          case 65604: metadata.put("ApoTome Processing Mode", data); break;
          case 65605: metadata.put("ApotmeCamLiveCombineMode", data); break;
          case 65606: metadata.put("ApoTome Filter Name", data); break;
          case 65607: metadata.put("Apotome Filter Strength", data); break;
          case 65608: metadata.put("ApotomeCamFilterHarmonics", data); break;
          case 65609: metadata.put("ApoTome Grating Period", data); break;
          case 65610: metadata.put("ApoTome Auto Shutter Used", data); break;
          case 65611: metadata.put("Apotome Cam Status", data); break;
          case 65612: metadata.put("ApotomeCamNormalize", data); break;
          case 65613: metadata.put("ApotomeCamSettingsManager", data); break;
          case 65614: metadata.put("DeepviewCamSupervisorMode", data); break;
          case 65615: metadata.put("DeepView Processing", data); break;
          case 65616: metadata.put("DeepviewCamFilterName", data); break;
          case 65617: metadata.put("DeepviewCamStatus", data); break;
          case 65618: metadata.put("DeepviewCamSettingsManager", data); break;
          case 65619: metadata.put("DeviceScalingName", data); break;
          case 65620: metadata.put("CameraShadingIsCalculated", data); break;
          case 65621:
            metadata.put("CameraShadingCalculationName", data);
            break;
          case 65622: metadata.put("CameraShadingAutoCalculate", data); break;
          case 65623: metadata.put("CameraTriggerAvailable", data); break;
          case 65626: metadata.put("CameraShutterAvailable", data); break;
          case 65627:
            metadata.put("AxioCam ShutterMicroScanningEnable", data);
            break;
          case 65628: metadata.put("ApotomeCamLiveFocus", data); break;
          case 65629: metadata.put("DeviceInitStatus", data); break;
          case 65630: metadata.put("DeviceErrorStatus", data); break;
          case 65631:
            metadata.put("ApotomeCamSliderInGridPosition", data);
            break;
          case 65632: metadata.put("Orca NIR Mode Used", data); break;
          case 65633: metadata.put("Orca Analog Gain", data); break;
          case 65634: metadata.put("Orca Analog Offset", data); break;
          case 65635: metadata.put("Orca Binning", data); break;
          case 65636: metadata.put("Orca Bit Depth", data); break;
          case 65637: metadata.put("ApoTome Averaging Count", data); break;
          case 65638: metadata.put("DeepView DoF", data); break;
          case 65639: metadata.put("DeepView EDoF", data); break;
          case 65643: metadata.put("DeepView Slider Name", data); break;
        }
      }
    }
  }

  /** Parse the OLE document structure using Jakarta POI */
  protected void parseDir(int depth, Object dir)
    throws IOException, FormatException, ReflectException
  {
    r.setVar("dir", dir);
    r.exec("dirName = dir.getName()");
    if (DEBUG) print(depth, r.getVar("dirName") + " {");
    r.setVar("depth", depth);
    r.exec("iter = dir.getEntries()");
    Iterator iter = (Iterator) r.getVar("iter");
    while (iter.hasNext()) {
      r.setVar("entry", iter.next());
      r.exec("isInstance = entry.isDirectoryEntry()");
      r.exec("isDocument = entry.isDocumentEntry()");
      boolean isInstance = ((Boolean) r.getVar("isInstance")).booleanValue();
      boolean isDocument = ((Boolean) r.getVar("isDocument")).booleanValue();
      r.setVar("dir", dir);
      r.exec("dirName = dir.getName()");
      if (isInstance) parseDir(depth + 1, r.getVar("entry"));
      else if (isDocument) {
        r.exec("entryName = entry.getName()");
        if (DEBUG) {
          print(depth + 1, "Found document: " + r.getVar("entryName"));
        }
        r.setVar("doc", r.getVar("entry"));
        r.exec("dis = new DocumentInputStream(doc)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes];
        r.setVar("data", data);
        r.exec("dis.read(data)");

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.equals("Contents");
        boolean isImage = dirName.equals("Image");

        if (isContents && isImage) {
          // we've found the header data

          int length = ((byte[]) r.getVar("data")).length;
          header = new byte[length];
          for (int i=0; i<length; i++) {
            header[i] = ((byte[]) r.getVar("data"))[i];
          }

          int pointer = 14;

          length = DataTools.bytesToInt((byte[])
            r.getVar("data"), pointer, 2, true);
          pointer += 4;
          pointer += length; // don't need these bytes

          int width = DataTools.bytesToInt((byte[])
            r.getVar("data"), pointer, 4, true);
          imageWidth = width;
          pointer += 4 + 2;
          int height = DataTools.bytesToInt((byte[])
            r.getVar("data"), pointer, 4, true);
          imageHeight = height;
          pointer += 4 + 2;
          pointer += 4 + 2;

          int fmt = DataTools.bytesToInt((byte[])
            r.getVar("data"), pointer, 4, true);
          pointer += 4 + 2;

          switch (fmt) {
            case 1: bytesPerPixel = 3; break;
            case 2: bytesPerPixel = 4; break;
            case 3: bytesPerPixel = 1; break;
            case 4: bytesPerPixel = 2; break;
            case 6: bytesPerPixel = 4; break;
            case 8: bytesPerPixel = 6; break;
            default: bytesPerPixel = 1;
          }
        }

        else if (entryName.equals("Contents") &&
          dirName.substring(0, 4).equals("Item"))
        {
          int length = ((byte[]) r.getVar("data")).length;
          header = new byte[length];
          for (int i=0; i<length; i++) {
            header[i] = ((byte[]) r.getVar("data"))[i];
          }

          String name = dirName.substring(5, dirName.length() - 1);
          Integer imageNum = Integer.valueOf(name);

          int byteCount = 2;
          byteCount += 4; // version field
          byteCount += 6; // type field
          byteCount += 2;
          int numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 2, true);
          byteCount += 2 + numBytes;

          byteCount += 6; // width
          byteCount += 6; // height
          byteCount += 6; // depth
          byteCount += 6; // pixel format
          byteCount += 6; // count
          byteCount += 6; // bits per pixel

          numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 2, true);
          byteCount += 2 + numBytes; // plugin CLSID
          byteCount += 2 + 4 + (8*4); // not sure what this is for

          byteCount += 2;

          numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 4, true);
          byteCount += 4 + numBytes; // layers

          byteCount += 2;

          numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 4, true);
          byteCount += 4 + numBytes; // scaling

          byteCount += 2;

          numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 2, true);
          byteCount += 2 + numBytes; // root folder name

          byteCount += 2;

          numBytes = DataTools.bytesToInt((byte[])
            r.getVar("data"), byteCount, 2, true);
          byteCount += 2 + numBytes; // display item name

          byteCount += 28; // streamed header data

          // get pixel data

          if (((byte[]) r.getVar("data")).length > byteCount) {
            byte[] head = new byte[byteCount];
            for (int i=0; i<head.length; i++) {
              head[i] = ((byte[]) r.getVar("data"))[i];
            }

            headerData.put(imageNum, (Object) head);
            byte[] px = new byte[
              ((byte[]) r.getVar("data")).length - byteCount];

            for (int i=0; i<px.length; i++) {
              px[i] = ((byte[]) r.getVar("data"))[
                (((byte[]) r.getVar("data")).length - 1)
                - i];
            }

            pixelData.put(imageNum, (Object) px);
            nImages++;
          }
        }
        else if (entryName.equals("Tags") && dirName.equals("Root Entry")) {
          // the main tags stream

          tags = new byte[((byte[]) r.getVar("data")).length];
          for (int i=0; i<tags.length; i++) {
            tags[i] = ((byte[]) r.getVar("data"))[i];
          }
        }
        r.exec("dis.close()");
        if (DEBUG) {
          print(depth + 1, ((byte[])
            r.getVar("data")).length + " bytes read.");
        }
      }
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ZeissZVIReader().testRead(args);
  }

}
