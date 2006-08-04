//
// MetamorphReader.java
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

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import loci.formats.*;

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
  private static final int UIC2TAG = 33629;
  private static final int UIC4TAG = 33631;

  // -- Fields --

  /** The TIFF's name */
  private String imageName;

  /** The TIFF's creation date */
  private String imageCreationDate;

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

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  public void setSeparated(boolean separate) {
    separated = separate;
    super.setSeparated(separate);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true).length;
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return getImageCount(id) / getSizeZ(id);
  }

  /** Initializes the given Metamorph file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    long[] uic2 = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true);
    numImages = uic2.length;

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

    super.initMetadataStore();
    MetadataStore store = getMetadataStore(id);
    store.setPixels(null, null, new Integer(getSizeZ(currentId)), null,
      new Integer(getSizeT(currentId)), null, null, null, null);

  }

  // -- Internal BaseTiffReader API methods --

  /** Populates the metadata hashtable. */
  protected void initStandardMetadata() {
    try {
      Hashtable ifd = ifds[0];
      super.initStandardMetadata();

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

      while ((currentcode < 66) && ((in.length() - in.getFilePointer()) > 2)) {
        currentcode = in.readShort();

        // variable declarations, because switch is dumb
        int num, denom;
        int xnum, xdenom, ynum, ydenom;
        double xpos, ypos;
        String thedate, thetime;
        switch (currentcode) {
          case 0:
            String autoscale = in.readInt() == 0 ?
              "no auto-scaling" : "16-bit to 8-bit scaling";
            put("AutoScale", autoscale);
            break;
          case 1:
            put("MinScale", in.readInt());
            break;
          case 2:
            put("MaxScale", in.readInt());
            break;
          case 3:
            int calib = in.readInt();
            String calibration = calib == 0 ? "on" : "off";
            put("Spatial Calibration", calibration);
            break;
          case 4:
            num = in.readInt();
            denom = in.readInt();
            put("XCalibration", new TiffRational(num, denom));
            break;
          case 5:
            num = in.readInt();
            denom = in.readInt();
            put("YCalibration", new TiffRational(num, denom));
            break;
          case 6:
            num = in.readInt();
            toread = new byte[num];
            in.read(toread);
            put("CalibrationUnits", new String(toread));
            break;
          case 7:
            num = in.readInt();
            toread = new byte[num];
            in.read(toread);
            String name = new String(toread);
            put("Name", name);
            imageName = name;
            break;
          case 8:
            int thresh = in.readInt();
            String threshState = "off";
            if (thresh == 1) threshState = "inside";
            else if (thresh == 2) threshState = "outside";
            put("ThreshState", threshState);
            break;
          case 9:
            put("ThreshStateRed", in.readInt());
            break;
          // there is no 10
          case 11:
            put("ThreshStateGreen", in.readInt());
            break;
          case 12:
            put("ThreshStateBlue", in.readInt());
            break;
          case 13:
            put("ThreshStateLo", in.readInt());
            break;
          case 14:
            put("ThreshStateHi", in.readInt());
            break;
          case 15:
            int zoom = in.readInt();
            put("Zoom", zoom);
//            OMETools.setAttribute(ome, "DisplayOptions", "Zoom", "" + zoom);
            break;
          case 16: // oh how we hate you Julian format...
            thedate = decodeDate(in.readInt());
            thetime = decodeTime(in.readInt());
            put("DateTime", thedate + " " + thetime);
            imageCreationDate = thedate + " " + thetime;
            break;
          case 17:
            thedate = decodeDate(in.readInt());
            thetime = decodeTime(in.readInt());
            put("LastSavedTime", thedate + " " + thetime);
            break;
          case 18:
            put("currentBuffer", in.readInt());
            break;
          case 19:
            put("grayFit", in.readInt());
            break;
          case 20:
            put("grayPointCount", in.readInt());
            break;
          case 21:
            num = in.readInt();
            denom = in.readInt();
            put("grayX", new TiffRational(num, denom));
            break;
          case 22:
            num = in.readInt();
            denom = in.readInt();
            put("gray", new TiffRational(num, denom));
            break;
          case 23:
            num = in.readInt();
            denom = in.readInt();
            put("grayMin", new TiffRational(num, denom));
            break;
          case 24:
            num = in.readInt();
            denom = in.readInt();
            put("grayMax", new TiffRational(num, denom));
            break;
          case 25:
            num = in.readInt();
            toread = new byte[num];
            in.read(toread);
            put("grayUnitName", new String(toread));
            break;
          case 26:
            int standardLUT = in.readInt();
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
            put("Wavelength", in.readInt());
            break;
          case 28:
            for (int i = 0; i < planes; i++) {
                xnum = in.readInt();
                xdenom = in.readInt();
                ynum = in.readInt();
                ydenom = in.readInt();
                xpos = xnum / xdenom;
                ypos = ynum / ydenom;
                put("Stage Position Plane " + i,
                  "(" + xpos + ", " + ypos + ")");
            }
            break;
          case 29:
            for (int i = 0; i < planes; i++) {
              xnum = in.readInt();
              xdenom = in.readInt();
              ynum = in.readInt();
              ydenom = in.readInt();
              xpos = xnum / xdenom;
              ypos = ynum / ydenom;
              put("Camera Offset Plane " + i,
                "(" + xpos + ", " + ypos + ")");
            }
            break;
          case 30:
            put("OverlayMask", in.readInt());
            break;
          case 31:
            put("OverlayCompress", in.readInt());
            break;
          case 32:
            put("Overlay", in.readInt());
            break;
          case 33:
            put("SpecialOverlayMask", in.readInt());
            break;
          case 34:
            put("SpecialOverlayCompress", in.readInt());
            break;
          case 35:
            put("SpecialOverlay", in.readInt());
            break;
          case 36:
            put("ImageProperty", in.readInt());
            break;
          case 37:
            for (int i = 0; i<planes; i++) {
              num = in.readInt();
              toread = new byte[num];
              in.read(toread);
              String s = new String(toread);
              put("StageLabel Plane " + i, s);
            }
            break;
          case 38:
            num = in.readInt();
            denom = in.readInt();
            put("AutoScaleLoInfo", new TiffRational(num, denom));
            break;
          case 39:
            num = in.readInt();
            denom = in.readInt();
            put("AutoScaleHiInfo", new TiffRational(num, denom));
            break;
          case 40:
            for (int i=0;i<planes;i++) {
              num = in.readInt();
              denom = in.readInt();
              put("AbsoluteZ Plane " + i, new TiffRational(num, denom));
            }
            break;
          case 41:
            for (int i=0; i<planes; i++) {
              put("AbsoluteZValid Plane " + i, in.readInt());
            }
            break;
          case 42:
            put("Gamma", in.readInt());
            break;
          case 43:
            put("GammaRed", in.readInt());
            break;
          case 44:
            put("GammaGreen", in.readInt());
            break;
          case 45:
            put("GammaBlue", in.readInt());
            break;
        } // end switch
      }
    }
    catch (NullPointerException n) { n.printStackTrace(); }
    catch (IOException io) { io.printStackTrace(); }
    catch (FormatException e) { e.printStackTrace(); }

    try { super.initStandardMetadata(); }
    catch (Throwable t) { t.printStackTrace(); }

    // parse (mangle) TIFF comment
    String descr = (String) metadata.get("Comment");
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
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.BaseTiffReader#getImageName()
   */
  protected String getImageName() { return imageName; }

  /*
   * (non-Javadoc)
   * @see loci.formats.BaseTiffReader#getImageCreationDate()
   */
  protected String getImageCreationDate() { return imageCreationDate; }

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
