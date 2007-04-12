//
// MetamorphReader.java
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.StringTokenizer;
import loci.formats.DataTools;
import loci.formats.FormatException;
import loci.formats.TiffIFDEntry;
import loci.formats.TiffRational;
import loci.formats.TiffTools;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Sebastien Huart Sebastien dot Huart at curie.fr
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  // IFD tag numbers of important fields
  private static final int METAMORPH_ID = 33628;
  private static final int UIC1TAG = METAMORPH_ID;
  private static final int UIC2TAG = 33629;
  private static final int UIC3TAG = 33630;
  private static final int UIC4TAG = 33631;

  // -- Fields --

  /** The TIFF's name */
  private String imageName;

  /** The TIFF's creation date */
  private String imageCreationDate;

  //** The TIFF's emWavelength */
  private long[] emWavelength;

  private int mmPlanes; //number of metamorph planes
  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() { super("Metamorph STK", "stk"); }

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // If the file is a Metamorph STK file, it should have a specific IFD tag.
    // Most Metamorph files seem to have the IFD information at the end, so it
    // is difficult to determine whether or not the block is a Metamorph block
    // without being passed the entire file.  Therefore, we will check the only
    // things we can reasonably check at the beginning of the file, and if we
    // happen to be passed the entire file, well, great, we'll check that too.

    // Must be little-endian TIFF
    if (block.length < 3) return false;
    if (block[0] != TiffTools.LITTLE) return false; // denotes little-endian
    if (block[1] != TiffTools.LITTLE) return false;
    if (block[2] != TiffTools.MAGIC_NUMBER) return false; // denotes TIFF
    if (block.length < 8) return true; // we have no way of verifying further
    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation + 1 > block.length) {
      // we have no way of verifying this is a Metamorph file.
      // It is at least a TIFF.
      return true;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i = 0; i < ifdnumber; i++) {
        if (ifdlocation + 3 + (i * 12) > block.length) return true;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i * 12), 2, true);
          if (ifdtag == METAMORPH_ID) return true; // absolutely a valid file
        }
      }
      return false; // we went through the IFD; the ID wasn't found.
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    try {
      // Now that the base TIFF standard metadata has been parsed, we need to
      // parse out the STK metadata from the UIC4TAG.
      TiffIFDEntry uic1tagEntry= TiffTools.getFirstIFDEntry(in, UIC1TAG);
      TiffIFDEntry uic2tagEntry=TiffTools.getFirstIFDEntry(in, UIC2TAG);
      TiffIFDEntry uic3tagEntry=TiffTools.getFirstIFDEntry(in, UIC3TAG);
      TiffIFDEntry uic4tagEntry = TiffTools.getFirstIFDEntry(in, UIC4TAG);
      int planes = uic4tagEntry.getValueCount();
      mmPlanes = planes;
      parseUIC2Tags(uic2tagEntry.getValueOffset());
      parseUIC4Tags(uic4tagEntry.getValueOffset());
      parseUIC1Tags(uic1tagEntry.getValueOffset(),
        uic1tagEntry.getValueCount());
      in.seek(uic4tagEntry.getValueOffset());

      // copy ifds into a new array of Hashtables that will accommodate the
      // additional image planes
      long[] uic2 = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true);
      core.imageCount[0] = uic2.length;

      long[] uic3 = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);
      for (int i=0; i<uic3.length; i++) {
        in.seek(uic3[i]);
        put("Wavelength [" + intFormatMax(i, mmPlanes) + "]",
          in.readLong() / in.readLong());
      }

      Hashtable[] tempIFDs = new Hashtable[core.imageCount[0]];

      long[] oldOffsets = TiffTools.getIFDLongArray(ifds[0],
          TiffTools.STRIP_OFFSETS, true);

      long[] stripByteCounts = TiffTools.getIFDLongArray(ifds[0],
          TiffTools.STRIP_BYTE_COUNTS, true);

      int stripsPerImage = oldOffsets.length;

      int check = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION);
      if (check == TiffTools.RGB_PALETTE) {
        TiffTools.putIFDValue(ifds[0], TiffTools.PHOTOMETRIC_INTERPRETATION,
          TiffTools.BLACK_IS_ZERO);
      }

      emWavelength = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);

      // for each image plane, construct an IFD hashtable

      int pointer = 0;

      Hashtable temp;
      for(int i=0; i<core.imageCount[0]; i++) {
        temp = new Hashtable();

        // copy most of the data from 1st IFD
        temp.put(new Integer(TiffTools.LITTLE_ENDIAN), ifds[0].get(
            new Integer(TiffTools.LITTLE_ENDIAN)));
        temp.put(new Integer(TiffTools.IMAGE_WIDTH), ifds[0].get(
            new Integer(TiffTools.IMAGE_WIDTH)));
        temp.put(new Integer(TiffTools.IMAGE_LENGTH),
            ifds[0].get(new Integer(TiffTools.IMAGE_LENGTH)));
        temp.put(new Integer(TiffTools.BITS_PER_SAMPLE), ifds[0].get(
            new Integer(TiffTools.BITS_PER_SAMPLE)));
        temp.put(new Integer(TiffTools.COMPRESSION), ifds[0].get(
            new Integer(TiffTools.COMPRESSION)));
        temp.put(new Integer(TiffTools.PHOTOMETRIC_INTERPRETATION),
            ifds[0].get(new Integer(TiffTools.PHOTOMETRIC_INTERPRETATION)));
        temp.put(new Integer(TiffTools.STRIP_BYTE_COUNTS), ifds[0].get(
            new Integer(TiffTools.STRIP_BYTE_COUNTS)));
        temp.put(new Integer(TiffTools.ROWS_PER_STRIP), ifds[0].get(
            new Integer(TiffTools.ROWS_PER_STRIP)));
        temp.put(new Integer(TiffTools.X_RESOLUTION), ifds[0].get(
            new Integer(TiffTools.X_RESOLUTION)));
        temp.put(new Integer(TiffTools.Y_RESOLUTION), ifds[0].get(
            new Integer(TiffTools.Y_RESOLUTION)));
        temp.put(new Integer(TiffTools.RESOLUTION_UNIT), ifds[0].get(
            new Integer(TiffTools.RESOLUTION_UNIT)));
        temp.put(new Integer(TiffTools.PREDICTOR), ifds[0].get(
            new Integer(TiffTools.PREDICTOR)));

        // now we need a StripOffsets entry

        long planeOffset = i*(oldOffsets[stripsPerImage - 1] +
            stripByteCounts[stripsPerImage - 1] - oldOffsets[0]);

        long[] newOffsets = new long[oldOffsets.length];
        newOffsets[0] = planeOffset + oldOffsets[0];

        for(int j=1; j<newOffsets.length; j++) {
          newOffsets[j] = newOffsets[j-1] + stripByteCounts[0];
        }

        temp.put(new Integer(TiffTools.STRIP_OFFSETS), newOffsets);

        tempIFDs[pointer] = temp;
        pointer++;
      }
      ifds = tempIFDs;
    }
    catch (NullPointerException n) { n.printStackTrace(); }
    catch (IOException io) { io.printStackTrace(); }
    catch (FormatException e) { e.printStackTrace(); }

    try {
      super.initStandardMetadata();
    }
    catch (FormatException exc) {
      if (debug) exc.printStackTrace();
    }
    catch (IOException exc) {
      if (debug) exc.printStackTrace();
    }

    // parse (mangle) TIFF comment
    String descr = (String) getMeta("Comment");
    if (descr != null) {
      StringTokenizer st = new StringTokenizer(descr, "\n");
      StringBuffer sb = new StringBuffer();
      boolean first = true;
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int colon = line.indexOf(": ");

        if (colon < 0) {
          // normal line (not a key/value pair)
          if (line.trim().length() > 0) {
            // not a blank line
            sb.append(line);
            if (!line.endsWith(".")) sb.append(".");
            sb.append("  ");
          }
          first = false;
          continue;
        }

        if (first) {
          // first line could be mangled; make a reasonable guess
          int dot = line.lastIndexOf(".", colon);
          if (dot >= 0) {
            String s = line.substring(0, dot + 1);
            sb.append(s);
            if (!s.endsWith(".")) sb.append(".");
            sb.append("  ");
          }
          line = line.substring(dot + 1);
          colon -= dot + 1;
          first = false;
        }

        // add key/value pair embedded in comment as separate metadata
        String key = line.substring(0, colon);
        String value = line.substring(colon + 2);
        put(key, value);
      }

      // replace comment with trimmed version
      descr = sb.toString().trim();
      if (descr.equals("")) metadata.remove("Comment");
      else put("Comment", descr);
    }
    try {
      core.sizeZ[0] = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true).length;
      core.sizeT[0] = getImageCount() / core.sizeZ[0];
    }
    catch (FormatException exc) {
      if (debug) exc.printStackTrace();
    }
  }

  /* @see BaseTiffReader#getImageName() */
  protected String getImageName() {
    if (imageName == null) return super.getImageName();
    return imageName;
  }

  /* @see BaseTiffReader#getImageCreationDate() */
  protected String getImageCreationDate() {
    if (imageCreationDate == null) return super.getImageCreationDate();
    return imageCreationDate;
  }

  Integer getEmWave(int i) {
    if (emWavelength[i] == 0)  return null;
    return new Integer((int) emWavelength[i]);
  }

  // -- Utility methods --

  /**
   * Populates metadata fields with some contained in MetaMorph UIC2 Tag.
   * (for each plane: 6 integers:
   * zdistance numerator, zdistance denominator,
   * creation date, creation time, modif date, modif time)
   * @param uic2offset offset to UIC2 (33629) tag entries
   *
   * not a regular tiff tag (6*N entries, N being the tagCount)
   * @throws IOException
   */
  void parseUIC2Tags(long uic2offset) throws IOException {

    int saveLoc = in.getFilePointer();
    in.seek(uic2offset);

    /*number of days since the 1st of January 4713 B.C*/
    int cDate;
    /*milliseconds since 0:00*/
    int cTime;

    /*z step, distance separating previous slice from  current one*/
    double zDistance;
    String iAsString;

    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      int num = in.readInt();
      int den = in.readInt();
      zDistance = (double) num / den;
      put("zDistance[" + iAsString + "]", zDistance);
      cDate = in.readInt();
      put("creationDate[" + iAsString + "]", decodeDate(cDate));

      cTime = in.readInt();
      put("creationTime[" + iAsString + "]", decodeTime(cTime));
      // modification date and time are skipped as they all seem equal to 0...?
      in.skip(8);
    }
    in.seek(saveLoc);
  }

  /**
   * UIC4 metadata parser
   *
   * UIC4 Table contains per-plane blocks of metadata
   * stage X/Y positions,
   * camera chip offsets,
   * stage labels...
   * @param long uic4offset: offset of UIC4 table (not tiff-compliant)
   * @throws IOException
   */
  private void parseUIC4Tags(long uic4offset) throws IOException {
    long saveLoc = in.getFilePointer();
    in.seek(uic4offset);
    boolean end=false;
    short id;
    while (!end) {
      id = in.readShort();

      switch (id) {
        case 0:
          end=true;
          break;
        case 28:
          readStagePositions();
          break;
        case 29:
          readCameraChipOffsets();
          break;
        case 37:
          readStageLabels();
          break;
        case 40:
          readAbsoluteZ();
          break;
        case 41:
          readAbsoluteZValid();
          break;
        default:
          //unknown tags: do nothing
          break;
        //28->stagePositions
        //29->cameraChipOffsets
        //30->stageLabel
        //40->AbsoluteZ
        //41AbsoluteZValid
        //0->end
      }
    }
    in.seek(saveLoc);
  }

  void readStagePositions() throws IOException {
    int nx, dx, ny, dy;
    // for each plane:
    // 2 ints (rational:numerator,denominator) for stage X,
    // 2 ints (idem) for stage Y position
    double xPosition, yPosition;
    String iAsString;
    for(int i=0; i<mmPlanes; i++) {
      nx = in.readInt();
      dx = in.readInt();
      ny = in.readInt();
      dy = in.readInt();
      xPosition = (dx == 0) ? Double.NaN : (double) nx / dx;
      yPosition = (dy == 0) ? Double.NaN : (double) ny / dy;
      iAsString = intFormatMax(i, mmPlanes);
      put("stageX[" + iAsString + "]", xPosition);
      put("stageY[" + iAsString + "]", yPosition);
    }

  }

  void readCameraChipOffsets() throws IOException {
    int nx, dx, ny, dy;
    double cameraXChipOffset, cameraYChipOffset;
    String iAsString;
    for(int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      nx = in.readInt();
      dx = in.readInt();
      ny = in.readInt();
      dy = in.readInt();
      cameraXChipOffset = (dx == 0) ? Double.NaN: (double) nx / dx;
      cameraYChipOffset = (dy == 0) ? Double.NaN: (double) ny/ dy;
      put("cameraXChipOffset[" + iAsString + "]", cameraXChipOffset);
      put("cameraYChipOffset[" + iAsString + "]", cameraYChipOffset);
    }
  }

  void readStageLabels() throws IOException {
    int strlen;
    byte[] curlabel;
    String iAsString;
    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      strlen = in.readInt();
      curlabel = new byte[strlen];
      in.read(curlabel);
      put("stageLabel[" + iAsString + "]", new String(curlabel));
    }
  }

  void readAbsoluteZ() throws IOException {
    int nz, dz;
    double absoluteZ;
    for(int i=0; i<mmPlanes; i++) {
      nz = in.readInt();
      dz = in.readInt();
      absoluteZ = (dz == 0) ? Double.NaN : (double) nz / dz;
      put("absoluteZ[" + intFormatMax(i, mmPlanes) + "]", absoluteZ);
    }
  }

  void readAbsoluteZValid() throws IOException {
    for (int i=0; i<mmPlanes; i++) {
      put("absoluteZValid[" + intFormatMax(i, mmPlanes) + "]", in.readInt());
    }
  }

  /**
   * UIC1 entry parser
   * @throws IOException
   * @param long uic1offset : offset as found in the tiff tag 33628 (UIC1Tag)
   * @param int uic1count : number of entries in UIC1 table (not tiff-compliant)
   */
  private void parseUIC1Tags(long uic1offset, int uic1count) throws IOException
  {
    // Loop through and parse out each field. A field whose
    // code is "0" represents the end of the fields so we'll stop
    // when we reach that; much like a NULL terminated C string.
    long saveLoc = in.getFilePointer();
    in.seek(uic1offset);
    int currentID, valOrOffset;
    // variable declarations, because switch is dumb
    int num, denom;
    String thedate, thetime;
    long lastOffset;
    byte[] toread;
    for (int i=0; i<uic1count; i++) {
      currentID = in.readInt();
      valOrOffset = in.readInt();

      switch (currentID) {
        case 1:
          put("MinScale", valOrOffset);
          break;
        case 2:
          put("MaxScale", valOrOffset);
          break;
        case 3:
          int calib = valOrOffset;
          String calibration = calib != 0 ? "on" : "off";
          put("Spatial Calibration", calibration);
          break;
        case 4:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("XCalibration", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 5:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("YCalibration", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 6:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          put("CalibrationUnits", new String(toread));
          in.seek(lastOffset);
          break;
        case 7:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          String name = new String(toread);
          put("Name", name);
          imageName = name;
          in.seek(lastOffset);
          break;

        case 8:
          int thresh = valOrOffset;
          String threshState = "off";
          if (thresh == 1) threshState = "inside";
          else if (thresh == 2) threshState = "outside";
          put("ThreshState", threshState);
          break;
        case 9:
          put("ThreshStateRed", valOrOffset);
          break;
          // there is no 10
        case 11:
          put("ThreshStateGreen", valOrOffset);
          break;
        case 12:
          put("ThreshStateBlue", valOrOffset);
          break;
        case 13:
          put("ThreshStateLo", valOrOffset);
          break;
        case 14:
          put("ThreshStateHi", valOrOffset);
          break;
        case 15:
          int zoom = valOrOffset;
          put("Zoom", zoom);
          //OMETools.setAttribute(ome, "DisplayOptions", "Zoom", "" + zoom);
          break;
        case 16: // oh how we hate you Julian format...
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          put("DateTime", thedate + " " + thetime);
          imageCreationDate = thedate + " " + thetime;
          in.seek(lastOffset);
          break;
        case 17:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          put("LastSavedTime", thedate + " " + thetime);
          in.seek(lastOffset);
          break;
        case 18:
          put("currentBuffer", valOrOffset);
          break;
        case 19:
          put("grayFit", valOrOffset);
          break;
        case 20:
          put("grayPointCount", valOrOffset);
          break;
        case 21:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayX", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 22:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("gray", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 23:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayMin", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 24:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayMax", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 25:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          put("grayUnitName", new String(toread));
          in.seek(lastOffset);
          break;
        case 26:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          int standardLUT = in.readInt();
          in.seek(lastOffset);
          String standLUT;
          switch (standardLUT) {
            case 0:
              standLUT = "monochrome";
              break;
            case 1:
              standLUT = "pseudocolor";
              break;
            case 2:
              standLUT = "Red";
              break;
            case 3:
              standLUT = "Green";
              break;
            case 4:
              standLUT = "Blue";
              break;
            case 5:
              standLUT = "user-defined";
              break;
            default:
              standLUT = "monochrome"; break;
          }
          put("StandardLUT", standLUT);
          break;
        case 27:
          put("Wavelength", valOrOffset);
          break;
        case 30:
          put("OverlayMask", valOrOffset);
          break;
        case 31:
          put("OverlayCompress", valOrOffset);
          break;
        case 32:
          put("Overlay", valOrOffset);
          break;
        case 33:
          put("SpecialOverlayMask", valOrOffset);
          break;
        case 34:
          put("SpecialOverlayCompress", in.readInt());
          break;
        case 35:
          put("SpecialOverlay", valOrOffset);
          break;
        case 36:
          put("ImageProperty", valOrOffset);
          break;
        case 38:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("AutoScaleLoInfo", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 39:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("AutoScaleHiInfo", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 42:
          put("Gamma", valOrOffset);
          break;
        case 43:
          put("GammaRed", valOrOffset);
          break;
        case 44:
          put("GammaGreen", valOrOffset);
          break;
        case 45:
          put("GammaBlue", valOrOffset);
          break;
        case 46:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          int xBin, yBin;
          xBin = in.readInt();
          yBin = in.readInt();
          put("CameraBin", new String("(" + xBin + "," + yBin + ")"));
          in.seek(lastOffset);
          break;
        default:
          break;
      }
    }
    in.seek(saveLoc);
  }

  /** Converts a Julian date value into a human-readable string. */
  public static String decodeDate(int julian) {
    long a, b, c, d, e, alpha, z;
    short day, month, year;

    // code reused from the Metamorph data specification
    z = julian + 1;

    if (z < 2299161L) a = z;
    else {
      alpha = (long) ((z - 1867216.25) / 36524.25);
      a = z + 1 + alpha - alpha / 4;
    }

    b = (a > 1721423L ? a + 1524 : a + 1158);
    c = (long) ((b - 122.1) / 365.25);
    d = (long) (365.25 * c);
    e = (long) ((b - d) / 30.6001);

    day = (short) (b - d - (long) (30.6001 * e));
    month = (short) ((e < 13.5) ? e - 1 : e - 13);
    year = (short) ((month > 2.5) ? (c - 4716) : c - 4715);

    return day + "/" + month + "/" + year;
  }

  /** Converts a time value in milliseconds into a human-readable string. */
  public static String decodeTime(int millis) {
    int ms, seconds, minutes, hours;

    ms = millis % 1000;
    millis -= ms;
    millis /= 1000;
    seconds = millis % 60;
    millis -= seconds;
    millis /= 60;
    minutes = millis % 60;
    millis -= minutes;
    millis /= 60;
    hours = millis;
    return intFormat(hours, 2) + ":" + intFormat(minutes, 2) + ":" +
      intFormat(seconds, 2) + "." + intFormat(ms, 3);
  }

  /** Formats an integer value with leading 0s if needed. */
  public static String intFormat(int myint, int digits) {
    String formatstring = "0";
    while (formatstring.length() < digits) {
      formatstring += "0";
    }
    DecimalFormat df = new DecimalFormat(formatstring);
    return df.format(myint);
  }

  /**
   * Formats an integer with leading 0 using maximum sequence number.
   *
   * @param myint integer to format
   * @param maxint max of "myint"
   * @return String
   */
  public static String intFormatMax(int myint, int maxint) {
    return intFormat(myint, new Integer(maxint).toString().length());
  }

}
