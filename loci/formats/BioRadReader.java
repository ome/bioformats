//
// BioRadReader.java
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * BioRadReader is the file format reader for Bio-Rad PIC files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class BioRadReader extends FormatReader {

  // -- Constants --

  /** Numerical ID of a valid Bio-Rad PIC file. */
  private static final int PIC_FILE_ID = 12345;

  /** Always little endian. */
  private static final boolean LITTLE_ENDIAN = true;

  // Merge types

  /** Image is not merged. */
  private static final int MERGE_OFF = 0;

  /** All pixels merged, 16 color (4-bit). */
  private static final int MERGE_16 = 1;

  /** Alternate pixels merged, 128 color (7-bit). */
  private static final int MERGE_ALTERNATE = 2;

  /** Alternate columns merged. */
  private static final int MERGE_COLUMN = 3;

  /** Alternate rows merged. */
  private static final int MERGE_ROW = 4;

  /** Maximum pixels merged. */
  private static final int MERGE_MAXIMUM = 5;

  /** 64-color (12-bit) optimized 2-image merge. */
  private static final int MERGE_OPT12 = 6;

  /**
   * As above except convert look up table saved after the notes in file,
   * as opposed to at the end of each image data.
   */
  private static final int MERGE_OPT12_V2 = 7;

  /** List of merge types. */
  private static final String[] MERGE_NAMES = {
    "MERGE_OFF", "MERGE_16", "MERGE_ALTERNATE", "MERGE_COLUMN", "MERGE_ROW",
    "MERGE_MAXIMUM", "MERGE_OPT12", "MERGE_OPT12_V2"
  };

  // Look-up table constants

  /** A red pane appears on the screen. */
  private static final int RED_LUT = 0x01;

  /** A green pane appears on the screen. */
  private static final int GREEN_LUT = 0x02;

  /** A blue pane appears on the screen. */
  private static final int BLUE_LUT = 0x04;

  /** List of allowed variable names for NOTE_TYPE_VARIABLE notes. */
  private static final String[] NOTE_VAR_NAMES = {
    "SCALE_FACTOR", "LENS_MAGNIFICATION", "RAMP_GAMMA1", "RAMP_GAMMA2",
    "RAMP_GAMMA3", "RAMP1_MIN", "RAMP2_MIN", "RAMP3_MIN", "RAMP1_MAX",
    "RAMP2_MAX", "RAMP3_MAX", "PIC_FF_VERSION", "Z_CORRECT_FACTOR"
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
  private RandomAccessFile in;

  /** Dimensions of each image in current Bio-Rad PIC. */
  private int nx, ny;

  /** Number of images in current Bio-Rad PIC. */
  private int npic;

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

  /** Obtains the specified image from the given Bio-Rad PIC file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if(!id.equals(currentId)) initFile(id);

    if(no < 0 || no >= npic) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read image bytes
    int imageLen = nx * ny;
    byte[] data = new byte[imageLen];

    if (byteFormat) {
      // jump to proper image number
      in.seek(no * imageLen + 76);
      in.readFully(data);

      // each pixel is 8 bits
      return ImageTools.makeImage(data, nx, ny, 1, false);
    }
    else {
      // jump to proper image number
      in.seek(no * 2 * imageLen + 76);

      // read in 2 * imageLen bytes
      data = new byte[imageLen * 2];
      in.readFully(data);

      // each pixel is 16 bits
      short[] pixs = new short[imageLen];
      for(int i=0; i<pixs.length; i++) {
        pixs[i] = DataTools.bytesToShort(data, 2 * i, LITTLE_ENDIAN);
      }
      return ImageTools.makeImage(pixs, nx, ny, 1, false);
    }
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (currentId == null) return;
    in.close();
    currentId = null;
    in = null;
    metadata = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    // read header
    byte[] header = new byte[76];
    in.readFully(header);

    nx = DataTools.bytesToInt(header, 0, 2, LITTLE_ENDIAN);
    ny = DataTools.bytesToInt(header, 2, 2, LITTLE_ENDIAN);
    npic = DataTools.bytesToInt(header, 4, 2, LITTLE_ENDIAN);
    byteFormat = DataTools.bytesToInt(header, 14, 2, LITTLE_ENDIAN) != 0;

    int ramp1min = DataTools.bytesToInt(header, 6, 2, LITTLE_ENDIAN);
    int ramp1max = DataTools.bytesToInt(header, 8, 2, LITTLE_ENDIAN);
    boolean notes = (header[10] | header[11] | header[12] | header[13]) != 0;
    int imageNumber = DataTools.bytesToInt(header, 16, 2, LITTLE_ENDIAN);
    String name = new String(header, 18, 32);
    int merged = DataTools.bytesToInt(header, 50, 2, LITTLE_ENDIAN);
    int color1 = DataTools.bytesToInt(header, 52, 2, LITTLE_ENDIAN);
    int fileId = DataTools.bytesToInt(header, 54, 2, LITTLE_ENDIAN);
    int ramp2min = DataTools.bytesToInt(header, 56, 2, LITTLE_ENDIAN);
    int ramp2max = DataTools.bytesToInt(header, 58, 2, LITTLE_ENDIAN);
    int color2 = DataTools.bytesToInt(header, 60, 2, LITTLE_ENDIAN);
    int edited = DataTools.bytesToInt(header, 62, 2, LITTLE_ENDIAN);
    int lens = DataTools.bytesToInt(header, 64, 2, LITTLE_ENDIAN);
    float magFactor =
      Float.intBitsToFloat(DataTools.bytesToInt(header, 66, 4, LITTLE_ENDIAN));

    // check validity of header
    if (fileId != PIC_FILE_ID) {
      throw new FormatException("Invalid file header : " + fileId);
    }

    // populate metadata fields
    metadata.put("nx", new Integer(nx));
    metadata.put("ny", new Integer(ny));
    metadata.put("npic", new Integer(npic));
    metadata.put("ramp1_min", new Integer(ramp1min));
    metadata.put("ramp1_max", new Integer(ramp1max));
    metadata.put("notes", new Boolean(notes));
    metadata.put("byte_format", new Boolean(byteFormat));
    metadata.put("image_number", new Integer(imageNumber));
    metadata.put("name", name);
    metadata.put("merged", MERGE_NAMES[merged]);
    metadata.put("color1", new Integer(color1));
    metadata.put("file_id", new Integer(fileId));
    metadata.put("ramp2_min", new Integer(ramp2min));
    metadata.put("ramp2_max", new Integer(ramp2max));
    metadata.put("color2", new Integer(color2));
    metadata.put("edited", new Integer(edited));
    metadata.put("lens", new Integer(lens));
    metadata.put("mag_factor", new Float(magFactor));

    // skip image data
    int imageLen = nx * ny;
    int bpp = byteFormat ? 1 : 2;
    in.skipBytes(bpp * npic * imageLen);

    Vector pixelSize = new Vector();

    // read notes
    int noteCount = 0;
    while (notes) {
      // read in note
      byte[] note = new byte[96];
      in.readFully(note);
      int level = DataTools.bytesToInt(note, 0, 2, LITTLE_ENDIAN);
      notes = (note[2] | note[3] | note[4] | note[5]) != 0;
      int num = DataTools.bytesToInt(note, 6, 2, LITTLE_ENDIAN);
      int status = DataTools.bytesToInt(note, 8, 2, LITTLE_ENDIAN);
      int type = DataTools.bytesToInt(note, 10, 2, LITTLE_ENDIAN);
      int x = DataTools.bytesToInt(note, 12, 2, LITTLE_ENDIAN);
      int y = DataTools.bytesToInt(note, 14, 2, LITTLE_ENDIAN);
      String text = new String(note, 16, 80);

      // add note to list
      noteCount++;
      metadata.put("note" + noteCount,
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
              metadata.put(key + " distance (X) in microns", dx);
              metadata.put(key + " distance (Y) in microns", dy);
              pixelSize.add(dy);
              break;
            case 2:
              metadata.put(key + " time (X) in seconds", params.get(0));
              metadata.put(key + " time (Y) in seconds", params.get(1));
              break;
            case 3:
              metadata.put(key + " angle (X) in degrees", params.get(0));
              metadata.put(key + " angle (Y) in degrees", params.get(1));
              break;
            case 4:
              metadata.put(key + " intensity (X)", params.get(0));
              metadata.put(key + " intensity (Y)", params.get(1));
              break;
            case 6:
              metadata.put(key + " ratio (X)", params.get(0));
              metadata.put(key + " ratio (Y)", params.get(1));
              break;
            case 7:
              metadata.put(key + " log ratio (X)", params.get(0));
              metadata.put(key + " log ratio (Y)", params.get(1));
              break;
            case 9:
              metadata.put(key + " noncalibrated intensity min",
                params.get(0));
              metadata.put(key + " noncalibrated intensity max",
                params.get(1));
              metadata.put(key + " calibrated intensity min", params.get(2));
              metadata.put(key + " calibrated intensity max", params.get(3));
              break;
            case 11:
              metadata.put(key + " RGB type (X)", params.get(0));
              metadata.put(key + " RGB type (Y)", params.get(1));
              break;
            case 14:
              metadata.put(key + " time course type (X)", params.get(0));
              metadata.put(key + " time course type (Y)", params.get(1));
              break;
            case 15:
              metadata.put(key + " inverse sigmoid calibrated intensity (min)",
                params.get(0));
              metadata.put(key + " inverse sigmoid calibrated intensity (max)",
                params.get(1));
              metadata.put(key +
                " inverse sigmoid calibrated intensity (beta)", params.get(2));
              metadata.put(key + " inverse sigmoid calibrated intensity (Kd)",
                params.get(3));
              metadata.put(key + " inverse sigmoid calibrated intensity " +
                "(calibrated max)", params.get(0));
              break;
            case 16:
              metadata.put(key + " log inverse sigmoid calibrated " +
                "intensity (min)", params.get(0));
              metadata.put(key + " log inverse sigmoid calibrated " +
                "intensity (max)", params.get(1));
              metadata.put(key + " log inverse sigmoid calibrated " +
                "intensity (beta)", params.get(2));
              metadata.put(key + " log inverse sigmoid calibrated " +
                "intensity (Kd)", params.get(3));
              metadata.put(key + " log inverse sigmoid calibrated " +
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
      try {
        in.readFully(lut[numLuts]);
        numLuts++;
      }
      catch (IOException exc) {
        eof = true;
        if (DEBUG) exc.printStackTrace();
      }
    }

    if (DEBUG && DEBUG_LEVEL >= 2) {
      System.out.println(numLuts + " color table" +
        (numLuts == 1 ? "" : "s") + " present.");
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
    metadata.put("luts", colors);

    // create and populate OME-XML DOM tree

    // populate Image element
    OMETools.setImage(ome, name, null, null);

    // populate Pixels element
    int type = DataTools.bytesToInt(header, 14, 2, LITTLE_ENDIAN);
    String fmt;
    if (type == 1) fmt = "Uint8";
    else fmt = "Uint16";
    OMETools.setPixels(ome,
      new Integer(nx), // SizeX
      new Integer(ny), // SizeY
      new Integer(npic), // SizeZ
      new Integer(1), // SizeC
      new Integer(1), // SizeT
      fmt, // PixelType
      null, // BigEndian
      null); // DimensionOrder

    // populate Dimensions element
    int size = pixelSize.size();
    Float pixelSizeX = null, pixelSizeY = null, pixelSizeZ = null;
    if (size >= 1) pixelSizeX = new Float((String) pixelSize.get(0));
    if (size >= 2) pixelSizeY = new Float((String) pixelSize.get(1));
    if (size >= 3) pixelSizeZ = new Float((String) pixelSize.get(2));
    OMETools.setDimensions(ome,
      pixelSizeX, pixelSizeY, pixelSizeZ, null, null);
  }

  public String noteString(int n, int l, int s, int t, int x, int y, String p)
  {
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
