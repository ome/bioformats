//
// BioRadReader.java
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
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import loci.formats.*;

/**
 * BioRadReader is the file format reader for Bio-Rad PIC files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */

public class BioRadReader extends FormatReader {

  // -- Constants --

  /** Numerical ID of a valid Bio-Rad PIC file. */
  private static final int PIC_FILE_ID = 12345;

  /** Always little endian. */
  private static final boolean LITTLE_ENDIAN = true;

  /** List of merge types. */
  private static final String[] MERGE_NAMES = {
    "MERGE_OFF", "MERGE_16", "MERGE_ALTERNATE", "MERGE_COLUMN", "MERGE_ROW",
    "MERGE_MAXIMUM", "MERGE_OPT12", "MERGE_OPT12_V2"
  };

  // Note types

  /** List of note types. */
  public static final String[] NOTE_NAMES = {
    "0", "LIVE", "FILE1", "NUMBER", "USER", "LINE", "COLLECT", "FILE2",
    "SCALEBAR", "MERGE", "THRUVIEW", "ARROW", "12", "13", "14", "15",
    "16", "17", "18", "19", "VARIABLE", "STRUCTURE", "4D SERIES"
  };

  // -- Fields --

  /** Input stream for current Bio-Rad PIC. */
  private RandomAccessStream in;

  /** Dimensions of each image in current Bio-Rad PIC. */
  private int nx, ny, nz, nt;

  /** Number of images in current Bio-Rad PIC. */
  private int npic;

  /** Dimension order in current Bio-Rad PIC. */
  private String order;

  /** Flag indicating current Bio-Rad PIC is packed with bytes. */
  private boolean byteFormat;

  // -- Constructor --

  /** Constructs a new BioRadReader. */
  public BioRadReader() { super("Bio-Rad PIC", "pic"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Bio-Rad PIC file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 56) return false;
    return DataTools.bytesToShort(block, 54, 2, LITTLE_ENDIAN) == PIC_FILE_ID;
  }

  /** Determines the number of images in the given Bio-Rad PIC file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return npic;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return LITTLE_ENDIAN;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double(((Integer) getMeta("ramp1_min")).intValue());
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double(((Integer) getMeta("ramp1_max")).intValue());
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if(!id.equals(currentId)) initFile(id);

    if(no < 0 || no >= npic) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read image bytes
    int imageLen = nx * ny;
    byte[] data = new byte[imageLen * ((byteFormat) ? 1 : 2)];
    int offset = no * imageLen;
    if (!byteFormat) offset *= 2;
    in.seek(offset + 76);
    in.read(data);
    return data;
  }

  /** Obtains the specified image from the given Bio-Rad PIC file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), nx, ny, 1, false,
      byteFormat ? 1 : 2, LITTLE_ENDIAN);
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;
    if (in != null) in.close();
    in = null;
    metadata = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BioRadReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    // read header

    nx = in.readShort();
    ny = in.readShort();
    npic = in.readShort();

    int ramp1min = in.readShort();
    int ramp1max = in.readShort();
    boolean notes = (in.read() | in.read() | in.read() | in.read()) != 0;
    byteFormat = in.readShort() != 0;
    int imageNumber = in.readShort();
    byte[] s = new byte[32];
    in.read(s);
    String name = new String(s);
    int merged = in.readShort();
    int color1 = in.readShort();
    int fileId = in.readShort();
    int ramp2min = in.readShort();
    int ramp2max = in.readShort();
    int color2 = in.readShort();
    int edited = in.readShort();
    int lens = in.readShort();
    float magFactor = in.readFloat();

    // check validity of header
    if (fileId != PIC_FILE_ID) {
      throw new FormatException("Invalid file header : " + fileId);
    }

    // populate metadata fields
    addMeta("nx", new Integer(nx));
    addMeta("ny", new Integer(ny));
    addMeta("npic", new Integer(npic));
    addMeta("ramp1_min", new Integer(ramp1min));
    addMeta("ramp1_max", new Integer(ramp1max));
    addMeta("notes", new Boolean(notes));
    addMeta("byte_format", new Boolean(byteFormat));
    addMeta("image_number", new Integer(imageNumber));
    addMeta("name", name);
    addMeta("merged", MERGE_NAMES[merged]);
    addMeta("color1", new Integer(color1));
    addMeta("file_id", new Integer(fileId));
    addMeta("ramp2_min", new Integer(ramp2min));
    addMeta("ramp2_max", new Integer(ramp2max));
    addMeta("color2", new Integer(color2));
    addMeta("edited", new Integer(edited));
    addMeta("lens", new Integer(lens));
    addMeta("mag_factor", new Float(magFactor));

    // skip image data
    int imageLen = nx * ny;
    int bpp = byteFormat ? 1 : 2;
    in.skipBytes(bpp * npic * imageLen + 6);

    Vector pixelSize = new Vector();

    int zSize = npic;
    int cSize = 1;
    int tSize = 1;

    orderCertain[0] = false;

    // read notes
    int noteCount = 0;
    while (notes) {
      // read in note

      int level = in.readShort();
      notes = (in.read() | in.read() | in.read() | in.read()) != 0;
      int num = in.readShort();
      int status = in.readShort();
      int type = in.readShort();
      int x = in.readShort();
      int y = in.readShort();
      s = new byte[80];
      in.read(s);
      String text = new String(s);

      // be sure to remove binary data from the note text
      int ndx = text.length();
      for (int i=0; i<text.length(); i++) {
        if (text.charAt(i) == 0) {
          ndx = i;
          i = text.length();
        }
      }

      text = text.substring(0, ndx).trim();

      // add note to list
      noteCount++;

      addMeta("note" + noteCount,
        noteString(num, level, status, type, x, y, text));

      // if the text of the note contains "AXIS", parse the text
      // more thoroughly (see pg. 21 of the BioRad specs)

      if (text.indexOf("AXIS") != -1) {
        // use StringTokenizer to break up the text
        StringTokenizer st = new StringTokenizer(text);
        String key = st.nextToken();
        String noteType = "";
        if (st.hasMoreTokens()) {
          noteType = st.nextToken();
        }

        int axisType = Integer.parseInt(noteType);
        Vector params = new Vector();
        while (st.hasMoreTokens()) {
          params.add(st.nextToken());
        }

        if (params.size() > 1) {
          switch (axisType) {
            case 1:
              String dx = (String) params.get(0), dy = (String) params.get(1);
              addMeta(key + " distance (X) in microns", dx);
              addMeta(key + " distance (Y) in microns", dy);
              pixelSize.add(dy);
              break;
            case 2:
              if (text.indexOf("AXIS_4") != -1) {
                addMeta(key + " time (X) in seconds", params.get(0));
                addMeta(key + " time (Y) in seconds", params.get(1));
                zSize = 1;
                tSize = npic;
                orderCertain[0] = true;
              }
              break;
            case 3:
              addMeta(key + " angle (X) in degrees", params.get(0));
              addMeta(key + " angle (Y) in degrees", params.get(1));
              break;
            case 4:
              addMeta(key + " intensity (X)", params.get(0));
              addMeta(key + " intensity (Y)", params.get(1));
              break;
            case 6:
              addMeta(key + " ratio (X)", params.get(0));
              addMeta(key + " ratio (Y)", params.get(1));
              break;
            case 7:
              addMeta(key + " log ratio (X)", params.get(0));
              addMeta(key + " log ratio (Y)", params.get(1));
              break;
            case 9:
              addMeta(key + " noncalibrated intensity min", params.get(0));
              addMeta(key + " noncalibrated intensity max", params.get(1));
              addMeta(key + " calibrated intensity min", params.get(2));
              addMeta(key + " calibrated intensity max", params.get(3));
              break;
            case 11:
              addMeta(key + " RGB type (X)", params.get(0));
              addMeta(key + " RGB type (Y)", params.get(1));
              break;
            case 14:
              addMeta(key + " time course type (X)", params.get(0));
              addMeta(key + " time course type (Y)", params.get(1));
              break;
            case 15:
              addMeta(key + " inverse sigmoid calibrated intensity (min)",
                params.get(0));
              addMeta(key + " inverse sigmoid calibrated intensity (max)",
                params.get(1));
              addMeta(key +
                " inverse sigmoid calibrated intensity (beta)", params.get(2));
              addMeta(key + " inverse sigmoid calibrated intensity (Kd)",
                params.get(3));
              addMeta(key + " inverse sigmoid calibrated intensity " +
                "(calibrated max)", params.get(0));
              break;
            case 16:
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (min)", params.get(0));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (max)", params.get(1));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (beta)", params.get(2));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (Kd)", params.get(3));
              addMeta(key + " log inverse sigmoid calibrated " +
                "intensity (calibrated max)", params.get(0));
              break;
          }

        }
      }
    }

    // read color tables
    int numLuts = 0;
    byte[][] lut = new byte[3][768];
    boolean eof = false;
    while (!eof && numLuts < 3) {
      if (in.getFilePointer() + lut[numLuts].length <= in.length()) {  
        in.read(lut[numLuts]);
        numLuts++;
      }
      else eof = true;
    }

    if (debug && debugLevel >= 2) {
      debug(numLuts + " color table" + (numLuts == 1 ? "" : "s") + " present.");
    }

    // convert color table bytes to floats
    float[][][] colors = new float[numLuts][3][256];
    for (int i=0; i<numLuts; i++) {
      for (int l=0; l<256; l++) {
        int qr = 0x000000ff & lut[i][l];
        int qg = 0x000000ff & lut[i][l + 256];
        int qb = 0x000000ff & lut[i][l + 512];
        colors[i][0][l] = (float) qr;
        colors[i][1][l] = (float) qg;
        colors[i][2][l] = (float) qb;
      }
    }

    String colorString = "";
    for (int i=0; i<numLuts; i++) {
      for (int j=0; j<256; j++) {
        for (int k=0; k<3; k++) {
          colorString += (colors[i][k][j]);
          if (!(j == 255 && k == 2)) colorString += ",";
        }
      }
      colorString += "\n\n";
    }

    addMeta("luts", colorString);

    // Populate the metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    // populate Image element
    store.setImage(name, null, null, null);

    // populate Pixels element
    in.seek(14);
    //int type = DataTools.bytesToInt(header, 14, 2, LITTLE_ENDIAN);
    int type = in.readShort();
    if (type == 1)
      pixelType[0] = FormatReader.UINT8;
    else
      pixelType[0] = FormatReader.UINT16;

    String dimOrder = "XY";
    int[] dims = new int[] {zSize, cSize, tSize};
    int max = 0;
    int min = Integer.MAX_VALUE;
    int median = 1;

    for (int i=0; i<dims.length; i++) {
      if (dims[i] < min) min = dims[i];
      if (dims[i] > max) max = dims[i];
      else median = dims[i];
    }

    int[] orderedDims = new int[] {max, median, min};
    for (int i=0; i<orderedDims.length; i++) {
      if (orderedDims[i] == zSize && dimOrder.indexOf("Z") == -1) {
        dimOrder += "Z";
      }
      else if (orderedDims[i] == cSize && dimOrder.indexOf("C") == -1) {
        dimOrder += "C";
      }
      else dimOrder += "T";
    }

    nz = zSize;
    nt = tSize;
    order = dimOrder;

    sizeX[0] = nx;
    sizeY[0] = ny;
    sizeZ[0] = nz;
    sizeC[0] = 1;
    sizeT[0] = nt;
    currentOrder[0] = order;

    store.setPixels(
      new Integer(nx), // SizeX
      new Integer(ny), // SizeY
      new Integer(zSize), // SizeZ
      new Integer(cSize), // SizeC
      new Integer(tSize), // SizeT
      new Integer(pixelType[0]), // PixelType
      null, // BigEndian
      dimOrder, // DimensionOrder
      null); // Use index 0

    // populate Dimensions element
    int size = pixelSize.size();
    Float pixelSizeX = null, pixelSizeY = null, pixelSizeZ = null;
    if (size >= 1) pixelSizeX = new Float((String) pixelSize.get(0));
    if (size >= 2) pixelSizeY = new Float((String) pixelSize.get(1));
    if (size >= 3) pixelSizeZ = new Float((String) pixelSize.get(2));
    store.setDimensions(pixelSizeX, pixelSizeY, pixelSizeZ, null, null, null);
  }

  public String noteString(int n, int l, int s, int t, int x, int y, String p) {
    StringBuffer sb = new StringBuffer(100);
    sb.append("level=");
    sb.append(l);
    sb.append("; num=");
    sb.append(n);
    sb.append("; status=");
    sb.append(s);
    sb.append("; type=");
    sb.append(NOTE_NAMES[t]);
    sb.append("; x=");
    sb.append(x);
    sb.append("; y=");
    sb.append(y);
    sb.append("; text=");
    sb.append(p == null ? "null" : p.trim());
    return sb.toString();
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new BioRadReader().testRead(args);
  }
}
