//
// LeicaReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
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
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import loci.formats.*;

/**
 * LeicaReader is the file format reader for Leica files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LeicaReader extends BaseTiffReader {

  // -- Constants -

  /** Maximum number of bytes to check for Leica header information. */
  private static final int BLOCK_CHECK_LEN = 16384*8;

  /** All Leica TIFFs have this tag. */
  private static final int LEICA_MAGIC_TAG = 33923;


  // -- Fields --

  /** Current file. */
  //protected RandomAccessStream in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Array of IFD-like structures containing metadata. */
  protected Hashtable[] headerIFDs;

  /** Helper reader. */
  protected TiffReader tiff;

  /** Number of channels in the file. */
  protected int numChannels;

  /** Array of image file names. */
  protected String[] files;

  // -- Constructor --

  /** Constructs a new Leica reader. */
  public LeicaReader() {
    super("Leica", new String[] {"lei", "tif", "tiff"});
    tiff = new TiffReader();
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Leica file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 4) return false;

    if ((block[0] == 0x49 && block[1] == 0x49 && block[2] == 0x49 &&
      block[3] == 0x49) || (block[0] == 0x4d && block[1] == 0x4d &&
      block[2] == 0x4d && block[3] == 0x4d))
    {
      return true;
    }

    if (block.length < 8) return true;

    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) {
      return false;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) return false;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, true);
          if (ifdtag == LEICA_MAGIC_TAG) return true;
        }
      }
      return false;
    }
  }

  /** Checks if the given string is a valid filename for a Leica file. */
  public boolean isThisType(String name) {
    String lname = name.toLowerCase();
    if (lname.endsWith(".lei")) return true;
    else if (!lname.endsWith(".tif") && !lname.endsWith(".tiff")) return false;

    // just checking the filename isn't enough to differentiate between
    // Leica and regular TIFF; open the file and check more thoroughly
    return checkBytes(name, BLOCK_CHECK_LEN);
  }

  /** Determines the number of images in the given Leica file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return (isRGB(id) && separated) ? 3*numImages : numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return tiff.isRGB(files[0]);
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return ((Integer) metadata.get("Image width")).intValue();
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return ((Integer) metadata.get("Image height")).intValue();
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return ((Integer) metadata.get("Number of images")).intValue();
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return numChannels;
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    return 1;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return littleEndian;
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    return "XYZTC";
  }

  /** Obtains the specified image from the given Leica file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return tiff.openBytes(currentId, no);
  }

  /** Obtains the specified image from the given Leica file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    return tiff.openImage(files[no], 0);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Leica file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (id.toLowerCase().endsWith("tif") || id.toLowerCase().endsWith("tiff"))
    {
      if (ifds == null) super.initFile(id);
      in = new RandomAccessStream(id);
      numChannels = 0;

      // open the TIFF file and look for the "Image Description" field

      ifds = TiffTools.getIFDs(in);
      try {
        super.initMetadata();
      }
      catch (NullPointerException n) { }

      if (ifds == null) throw new FormatException("No IFDs found");

      String descr = (String) metadata.get("Comment");
      metadata.remove("Comment");

      int ndx = descr.indexOf("Series Name");

      // should be more graceful about this
      if (ndx == -1) throw new FormatException("LEI file not found");

      String lei = descr.substring(descr.indexOf("=", ndx) + 1);
      lei = lei.substring(0, lei.indexOf("\n"));
      lei = lei.trim();

      String dir = id.substring(0, id.lastIndexOf("/") + 1);
      lei = dir + lei;

      // parse key/value pairs in ImageDescription

      // first thing is to remove anything of the form "[blah]"

      String first;
      String last;

      while(descr.indexOf("[") != -1) {
        first = descr.substring(0, descr.indexOf("["));
        last = descr.substring(descr.indexOf("\n", descr.indexOf("[")));
        descr = first + last;
      }

      // each remaining line in descr is a (key, value) pair,
      // where '=' separates the key from the value

      String key;
      String value;
      int eqIndex = descr.indexOf("=");

      while(eqIndex != -1) {
        key = descr.substring(0, eqIndex);
        value = descr.substring(eqIndex+1, descr.indexOf("\n", eqIndex));
        metadata.put(key.trim(), value.trim());
        descr = descr.substring(descr.indexOf("\n", eqIndex));
        eqIndex = descr.indexOf("=");
      }

      // now open the LEI file
      initFile(lei);
      //super.initMetadata();
    }
    else {
      // parse the LEI file

      if (metadata == null) {
        currentId = id;
        metadata = new Hashtable();
      }
      else {
        if (currentId != id) currentId = id;
      }
      in = new RandomAccessStream(id);

      byte[] fourBytes = new byte[4];
      in.read(fourBytes);
      littleEndian = (fourBytes[0] == TiffTools.LITTLE &&
        fourBytes[1] == TiffTools.LITTLE &&
        fourBytes[2] == TiffTools.LITTLE &&
        fourBytes[3] == TiffTools.LITTLE);

      in.skipBytes(8);
      int addr = (int) DataTools.read4UnsignedBytes(in, littleEndian);
      Vector v = new Vector();
      while (addr != 0) {
        Hashtable ifd = new Hashtable();
        v.add(ifd);

        in.seek(addr);

        int numEntries = (int) DataTools.read4UnsignedBytes(in, littleEndian);
        int tag = (int) DataTools.read4UnsignedBytes(in, littleEndian);

        int numIFDs = 0;
        while (tag != 0) {
          // create the IFD structure
          int offset = (int) DataTools.read4UnsignedBytes(in, littleEndian);

          long pos = in.getFilePointer();
          in.seek(offset + 12);

          int size = (int) DataTools.read4UnsignedBytes(in, littleEndian);
          byte[] data = new byte[size];
          in.read(data);
          ifd.put(new Integer(tag), (Object) data);
          in.seek(pos);
          tag = (int) DataTools.read4UnsignedBytes(in, littleEndian);
        }

        addr = (int) DataTools.read4UnsignedBytes(in, littleEndian);
      }
      headerIFDs = new Hashtable[v.size()];
      v.copyInto(headerIFDs);

      // determine the length of a filename

      int nameLength = 0;
      for (int i=0; i<headerIFDs.length; i++) {
        if (headerIFDs[i].get(new Integer(10)) != null) {
          byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
          nameLength = DataTools.bytesToInt(temp, 8, 4, littleEndian);
        }
      }

      Vector f = new Vector();
      for (int i=0; i<headerIFDs.length; i++) {
        byte[] tempData = (byte[]) headerIFDs[i].get(new Integer(15));
        int tempImages = DataTools.bytesToInt(tempData, 0, 4, littleEndian);
        for (int j=0; j<tempImages; j++) {
          // read in each filename
          f.add(new String(tempData, 20 + 2*(j*nameLength), 2*nameLength));
        }
      }

      files = new String[f.size()];
      numImages = f.size();
      f.copyInto(files);

      String dirPrefix = new File(id).getParent();
      dirPrefix = dirPrefix == null ? "" : (dirPrefix + File.separator);
      for (int i=0; i<files.length; i++) {
        files[i] = dirPrefix + DataTools.stripString(files[i]);
      }
      initMetadata();
    }
  }

  // -- Helper methods --

  /* (non-Javadoc)
   * @see loci.formats.BaseTiffReader#initMetadata()
   */
  protected void initMetadata() {
    if (headerIFDs == null) headerIFDs = ifds;

    for (int i=0; i<headerIFDs.length; i++) {
      byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
      if (temp != null) {
        // the series data
        // ID_SERIES
        metadata.put("Version",
          new Integer(DataTools.bytesToInt(temp, 0, 4, littleEndian)));
        metadata.put("Number of Series",
          new Integer(DataTools.bytesToInt(temp, 4, 4, littleEndian)));
        metadata.put("Length of filename",
          new Integer(DataTools.bytesToInt(temp, 8, 4, littleEndian)));
        metadata.put("Length of file extension",
          new Integer(DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        metadata.put("Image file extension",
          DataTools.stripString(new String(temp, 16,
          ((Integer) metadata.get("Length of file extension")).intValue())));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(15));
      if (temp != null) {
        // the image data
        // ID_IMAGES
        metadata.put("Number of images", new Integer(
          DataTools.bytesToInt(temp, 0, 4, littleEndian)));
        metadata.put("Image width", new Integer(
          DataTools.bytesToInt(temp, 4, 4, littleEndian)));
        metadata.put("Image height", new Integer(
          DataTools.bytesToInt(temp, 8, 4, littleEndian)));
        metadata.put("Bits per Sample", new Integer(
          DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        metadata.put("Samples per pixel", new Integer(
          DataTools.bytesToInt(temp, 16, 4, littleEndian)));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(20));
      if (temp != null) {
        // dimension description
        // ID_DIMDESCR
        int pt = 0;
        metadata.put("Voxel Version", new Integer(
          DataTools.bytesToInt(temp, 0, 4, littleEndian)));
        int voxelType = DataTools.bytesToInt(temp, 4, 4, littleEndian);
        String type = "";
        switch (voxelType) {
          case 0: type = "undefined"; break;
          case 10: type = "gray normal"; break;
          case 20: type = "RGB"; break;
        }

        metadata.put("VoxelType", type);

        metadata.put("Bytes per pixel", new Integer(
          DataTools.bytesToInt(temp, 8, 4, littleEndian)));
        metadata.put("Real world resolution", new Integer(
          DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        int length = DataTools.bytesToInt(temp, 16, 4, littleEndian);
        metadata.put("Maximum voxel intensity",
          DataTools.stripString(new String(temp, 20, length)));
        pt = 20 + length;
        pt += 4;
        metadata.put("Minimum voxel intensity",
          DataTools.stripString(new String(temp, pt, length)));
        pt += length;
        length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4 + length + 4;

        length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        for (int j=0; j<length; j++) {
          int dimId = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          String dimType = "";
          switch (dimId) {
            case 0: dimType = "undefined"; break;
            case 120: dimType = "x"; break;
            case 121: dimType = "y"; break;
            case 122: dimType = "z"; break;
            case 116: dimType = "t"; break;
            case 6815843: dimType = "channel"; break;
            case 6357100: dimType = "wave length"; break;
            case 7602290: dimType = "rotation"; break;
            case 7798904: dimType = "x-wide for the motorized xy-stage"; break;
            case 7798905: dimType = "y-wide for the motorized xy-stage"; break;
            case 7798906: dimType = "z-wide for the z-stage-drive"; break;
            case 4259957: dimType = "user1 - unspecified"; break;
            case 4325493: dimType = "user2 - unspecified"; break;
            case 4391029: dimType = "user3 - unspecified"; break;
            case 6357095: dimType = "graylevel"; break;
            case 6422631: dimType = "graylevel1"; break;
            case 6488167: dimType = "graylevel2"; break;
            case 6553703: dimType = "graylevel3"; break;
            case 7864398: dimType = "logical x"; break;
            case 7929934: dimType = "logical y"; break;
            case 7995470: dimType = "logical z"; break;
            case 7602254: dimType = "logical t"; break;
            case 7077966: dimType = "logical lambda"; break;
            case 7471182: dimType = "logical rotation"; break;
            case 5767246: dimType = "logical x-wide"; break;
            case 5832782: dimType = "logical y-wide"; break;
            case 5898318: dimType = "logical z-wide"; break;
          }

          if (dimType.equals("channel")) numChannels++;
          metadata.put("Dim" + j + " type", dimType);
          pt += 4;
          metadata.put("Dim" + j + " size", new Integer(
            DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;
          metadata.put("Dim" + j + " distance between sub-dimensions",
            new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;

          int len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " physical length",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " physical origin",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " name",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " description",
            DataTools.stripString(new String(temp, pt, len)));
        }
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(30));
      if (temp != null) {
        // filter data
        // ID_FILTERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(40));
      if (temp != null) {
        // time data
        // ID_TIMEINFO
        try {
          metadata.put("Number of time-stamped dimensions",
            new Integer(DataTools.bytesToInt(temp, 0, 4, littleEndian)));
          int nDims = DataTools.bytesToInt(temp, 4, 4, littleEndian);
          metadata.put("Time-stamped dimension", new Integer(nDims));

          int pt = 8;

          for (int j=0; j < nDims; j++) {
            metadata.put("Dimension " + j + " ID",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
            metadata.put("Dimension " + j + " size",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
            metadata.put("Dimension " + j + " distance between dimensions",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
          }

          int numStamps = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Number of time-stamps", new Integer(numStamps));
          for (int j=0; j<numStamps; j++) {
            metadata.put("Timestamp " + j,
              DataTools.stripString(new String(temp, pt, 64)));
            pt += 64;
          }

          int numTMs = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Number of time-markers", new Integer(numTMs));
          for (int j=0; j<numTMs; j++) {
            int numDims = DataTools.bytesToInt(temp, pt, 4, littleEndian);
            pt += 4;

            for (int k=0; k<numDims; k++) {
              metadata.put("Time-marker " + j + " Dimension " + k +
                " coordinate", new Integer(DataTools.bytesToInt(temp,
                pt, 4, littleEndian)));
              pt += 4;
            }
            metadata.put("Time-marker " + j,
              DataTools.stripString(new String(temp, pt, 64)));
            pt += 64;
          }
        }
        catch (Throwable t) { }
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(50));
      if (temp != null) {
        // scanner data
        // ID_SCANNERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(60));
      if (temp != null) {
        // experiment data
        // ID_EXPERIMENT
        int pt = 8;
        int len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;

        metadata.put("Image Description",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;
        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;

        metadata.put("Main file extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Single image format identifier",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Single image extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(70));
      if (temp != null) {
        // LUT data
        // ID_LUTDESC
        int pt = 0;
        int nChannels = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Number of LUT channels", new Integer(nChannels));
        metadata.put("ID of colored dimension",
          new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
        pt += 4;

        for (int j=0; j<nChannels; j++) {
          metadata.put("LUT Channel " + j + " version",
            new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;

          int invert = DataTools.bytesToInt(temp, pt, 1, littleEndian);
          pt += 1;
          boolean inverted = invert == 1;
          metadata.put("LUT Channel " + j + " inverted?",
            new Boolean(inverted).toString());

          int length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("LUT Channel " + j + " description",
            DataTools.stripString(new String(temp, pt, length)));

          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("LUT Channel " + j + " filename",
            DataTools.stripString(new String(temp, pt, length)));
          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("LUT Channel " + j + " name",
            DataTools.stripString(new String(temp, pt, length)));
          pt += length;

          pt += 8;
        }
      }
    }

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    try {
      if (isRGB(currentId)) numChannels *= 3;
    }
    catch (Exception exc) { }
    Integer sizeX = (Integer) metadata.get("Image width");
    Integer sizeY = (Integer) metadata.get("Image height");
    Integer sizeZ = (Integer) metadata.get("Number of images");
    store.setPixels(sizeX, sizeY, sizeZ,
      new Integer(numChannels == 0 ? 1 : numChannels), // SizeC
      new Integer(1), // SizeT
      null, // PixelType
      new Boolean(!littleEndian), // BigEndian
      "XYZTC", // DimensionOrder
      null); // Use index 0

    String timestamp = (String) metadata.get("Timestamp 1");
    String description = (String) metadata.get("Image Description");

    try {
      store.setImage(null, timestamp.substring(3), description, null);
    }
    catch (NullPointerException n) { }

//  String voxel = metadata.get("VoxelType").toString();
//  String photoInterp;
//  if (voxel.equals("gray normal")) photoInterp = "monochrome";
//  else if (voxel.equals("RGB")) photoInterp = "RGB";
//  else photoInterp = "monochrome";

//  OMETools.setAttribute(ome, "ChannelInfo",
//  "PhotometricInterpretation", photoInterp);

//  OMETools.setAttribute(ome, "ChannelInfo", "SamplesPerPixel",
//  metadata.get("Samples per pixel").toString());
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LeicaReader().testRead(args);
  }

}
