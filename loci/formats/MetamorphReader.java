//
// MetamorphReader.java
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

import java.io.IOException;
import java.util.Hashtable;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  // IFD tag numbers of important fields
  private static final int METAMORPH_ID = 33629;
  private static final int UIC1TAG = 33628;
  private static final int UIC2TAG = 33629;
  private static final int UIC3TAG = 33630;
  private static final int UIC4TAG = 33631;

  // -- Fields --

  private int numPlanes;


  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() { super("Metamorph STK", "stk"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Metamorph file. */
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

  /** Initializes the given Metamorph file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    long[] uic2 = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true);
    numImages = uic2.length;
    numPlanes = numImages;

    // copy ifds into a new array of Hashtables that will accomodate the
    // additional image planes

    Hashtable[] tempIFDs = new Hashtable[ifds.length + numImages];
    System.arraycopy(ifds, 0, tempIFDs, 0, ifds.length);
    int pointer = ifds.length;

    long[] oldOffsets = TiffTools.getIFDLongArray(ifds[0],
      TiffTools.STRIP_OFFSETS, true);

    long[] stripByteCounts = TiffTools.getIFDLongArray(ifds[0],
      TiffTools.STRIP_BYTE_COUNTS, true);

    int stripsPerImage = oldOffsets.length;

    // for each image plane, construct an IFD hashtable

    Hashtable temp;
    for(int i=1; i<numImages; i++) {
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

  // -- Internal BaseTiffReader API methods --

  /** Initialize the OME-XML tree. */
  protected void initOMEMetadata() {
    super.initOMEMetadata();
    if (ome != null) {
      try {
        int sizeZ = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true).length;
        OMETools.setSizeZ(ome, sizeZ);
      }
      catch (FormatException e) { e.printStackTrace(); }
    }
  }

  /** Populates the metadata hashtable. */
  protected void initStandardMetadata() {
    try {
      Hashtable ifd = ifds[0];
      super.initStandardMetadata();

      String newDescr = ((String[])
        ifd.get(new Integer(TiffTools.IMAGE_DESCRIPTION)))[0];
      ifd.put(new Integer(TiffTools.IMAGE_DESCRIPTION), newDescr);

      Integer obj = new Integer(0);

      int offset;

      Integer k = new Integer(UIC4TAG);
      long[] temp = (long[]) ifd.get(k);

      Long[] q = new Long[temp.length];
      for (int i=0; i<q.length; i++) q[i] = new Long(temp[i]);

      if (q == null) offset = -1;
      Integer w = new Integer(q[1].intValue());
      if (w == null) offset = -1;
      else offset =  w.intValue();

      if (offset < 0) throw new FormatException("UIC4TAG not found");
      in.seek(offset);
      int currentcode = -1;
      byte[] toread;

      TiffRational[] temp2 = (TiffRational[])
        ifd.get(new Integer(METAMORPH_ID));
      Long[] v = new Long[temp2.length];
      for (int i=0; i<v.length; i++) v[i] = new Long(temp2[i].longValue());
      if (v == null) throw new FormatException("Metamorph ID not found");

      int planes = v[1].intValue();
      boolean little = TiffTools.isLittleEndian(ifd);

      while (currentcode != 0) {
        currentcode = DataTools.read2SignedBytes(in, little);

        // variable declarations, because switch is dumb
        int num, denom;
        int xnum, xdenom, ynum, ydenom;
        double xpos, ypos;
        String thedate, thetime;
        switch (currentcode) {
          case 0:
            String autoscale = DataTools.read4SignedBytes(in, little) == 0 ?
              "no auto-scaling" : "16-bit to 8-bit scaling";
            put("AutoScale", autoscale);
            break;
          case 1:
            put("MinScale", DataTools.read4SignedBytes(in, little));
            break;
          case 2:
            put("MaxScale", DataTools.read4SignedBytes(in, little));
            break;
          case 3:
            int calib = DataTools.read4SignedBytes(in, little);
            String calibration = calib == 0 ? "on" : "off";
            put("Spatial Calibration", calibration);
            break;
          case 4:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("XCalibration", new TiffRational(num, denom));
            break;
          case 5:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("YCalibration", new TiffRational(num, denom));
            break;
          case 6:
            num = DataTools.read4SignedBytes(in, little);
            toread = new byte[num];
            in.read(toread);
            put("CalibrationUnits", new String(toread));
            break;
          case 7:
            num = DataTools.read4SignedBytes(in, little);
            toread = new byte[num];
            in.read(toread);
            String name = new String(toread);
            put("Name", name);
            OMETools.setImageName(ome, name);
            break;
          case 8:
            int thresh = DataTools.read4SignedBytes(in, little);
            String threshState = "off";
            if (thresh == 1) threshState = "inside";
            else if (thresh == 2) threshState = "outside";
            put("ThreshState", threshState);
            break;
          case 9:
            put("ThreshStateRed", DataTools.read4SignedBytes(in, little));
            break;
          // there is no 10
          case 11:
            put("ThreshStateGreen", DataTools.read4SignedBytes(in, little));
            break;
          case 12:
            put("ThreshStateBlue", DataTools.read4SignedBytes(in, little));
            break;
          case 13:
            put("ThreshStateLo", DataTools.read4SignedBytes(in, little));
            break;
          case 14:
            put("ThreshStateHi", DataTools.read4SignedBytes(in, little));
            break;
          case 15:
            int zoom = DataTools.read4SignedBytes(in, little);
            put("Zoom", zoom);
//            OMETools.setAttribute(ome, "DisplayOptions", "Zoom", "" + zoom);
            break;
          case 16: // oh how we hate you Julian format...
            thedate = decodeDate(DataTools.read4SignedBytes(in, little));
            thetime = decodeTime(DataTools.read4SignedBytes(in, little));
            put("DateTime", thedate + " " + thetime);
            OMETools.setCreationDate(ome, thedate + " " + thetime);
            break;
          case 17:
            thedate = decodeDate(DataTools.read4SignedBytes(in, little));
            thetime = decodeTime(DataTools.read4SignedBytes(in, little));
            put("LastSavedTime", thedate + " " + thetime);
            break;
          case 18:
            put("currentBuffer", DataTools.read4SignedBytes(in, little));
            break;
          case 19:
            put("grayFit", DataTools.read4SignedBytes(in, little));
            break;
          case 20:
            put("grayPointCount", DataTools.read4SignedBytes(in, little));
            break;
          case 21:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("grayX", new TiffRational(num, denom));
            break;
          case 22:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("gray", new TiffRational(num, denom));
            break;
          case 23:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("grayMin", new TiffRational(num, denom));
            break;
          case 24:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("grayMax", new TiffRational(num, denom));
            break;
          case 25:
            num = DataTools.read4SignedBytes(in, little);
            toread = new byte[num];
            in.read(toread);
            put("grayUnitName", new String(toread));
            break;
          case 26:
            int standardLUT = DataTools.read4SignedBytes(in, little);
            String standLUT;
            switch (standardLUT) {
              case 0: standLUT = "monochrome"; break;
              case 1: standLUT = "pseudocolor"; break;
              case 2: standLUT = "Red"; break;
              case 3: standLUT = "Green"; break;
              case 4: standLUT = "Blue"; break;
              case 5: standLUT = "user-defined"; break;
              default: standLUT = "monochrome"; break;
            }
            put("StandardLUT", standLUT);
            break;
          case 27:
            put("Wavelength", DataTools.read4SignedBytes(in, little));
            break;
          case 28:
            for (int i = 0; i < planes; i++) {
                xnum = DataTools.read4SignedBytes(in, little);
                xdenom = DataTools.read4SignedBytes(in, little);
                ynum = DataTools.read4SignedBytes(in, little);
                ydenom = DataTools.read4SignedBytes(in, little);
                xpos = xnum / xdenom;
                ypos = ynum / ydenom;
                put("Stage Position Plane " + i,
                  "(" + xpos + ", " + ypos + ")");
            }
            break;
          case 29:
            for (int i = 0; i < planes; i++) {
              xnum = DataTools.read4SignedBytes(in, little);
              xdenom = DataTools.read4SignedBytes(in, little);
              ynum = DataTools.read4SignedBytes(in, little);
              ydenom = DataTools.read4SignedBytes(in, little);
              xpos = xnum / xdenom;
              ypos = ynum / ydenom;
              put("Camera Offset Plane " + i,
                "(" + xpos + ", " + ypos + ")");
            }
            break;
          case 30:
            put("OverlayMask", DataTools.read4SignedBytes(in, little));
            break;
          case 31:
            put("OverlayCompress", DataTools.read4SignedBytes(in, little));
            break;
          case 32:
            put("Overlay", DataTools.read4SignedBytes(in, little));
            break;
          case 33:
            put("SpecialOverlayMask", DataTools.read4SignedBytes(in, little));
            break;
          case 34:
            put("SpecialOverlayCompress",
              DataTools.read4SignedBytes(in, little));
            break;
          case 35:
            put("SpecialOverlay", DataTools.read4SignedBytes(in, little));
            break;
          case 36:
            put("ImageProperty", DataTools.read4SignedBytes(in, little));
            break;
          case 37:
            for (int i = 0; i<planes; i++) {
              num = DataTools.read4SignedBytes(in, little);
              toread = new byte[num];
              in.read(toread);
              String s = new String(toread);
              put("StageLabel Plane " + i, s);
            }
            break;
          case 38:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("AutoScaleLoInfo", new TiffRational(num, denom));
            break;
          case 39:
            num = DataTools.read4SignedBytes(in, little);
            denom = DataTools.read4SignedBytes(in, little);
            put("AutoScaleHiInfo", new TiffRational(num, denom));
            break;
          case 40:
            for (int i=0;i<planes;i++) {
              num = DataTools.read4SignedBytes(in, little);
              denom = DataTools.read4SignedBytes(in, little);
              put("AbsoluteZ Plane " + i, new TiffRational(num, denom));
            }
            break;
          case 41:
            for (int i=0; i<planes; i++) {
              put("AbsoluteZValid Plane " + i,
                DataTools.read4SignedBytes(in, little));
            }
            break;
          case 42:
            put("Gamma", DataTools.read4SignedBytes(in, little));
            break;
          case 43:
            put("GammaRed", DataTools.read4SignedBytes(in, little));
            break;
          case 44:
            put("GammaGreen", DataTools.read4SignedBytes(in, little));
            break;
          case 45:
            put("GammaBlue", DataTools.read4SignedBytes(in, little));
            break;
        } // end switch
      }
    }
    catch (NullPointerException n) { }
    catch (IOException e) { e.printStackTrace(); }
    catch (FormatException e) { e.printStackTrace(); }


    try {
      super.initStandardMetadata();
    }
    catch (Throwable t) { t.printStackTrace(); }
  }


  // -- Utility methods --

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

    return hours + ":" + minutes + ":" + seconds + "." + ms;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new MetamorphReader().testRead(args);
  }

}
