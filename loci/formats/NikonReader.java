//
// NikonReader.java
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

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * NikonReader is the file format reader for
 * Nikon NEF (TIFF) files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class NikonReader extends BaseTiffReader {

  // -- Constants --

  /** Maximum number of bytes to check for Nikon header information. */
  private static final int BLOCK_CHECK_LEN = 16384;

  // Tags that give a good indication of whether this is an NEF file.
  private static final int EXIF_IFD_POINTER = 34665;
  private static final int TIFF_EPS_STANDARD = 37398;

  // EXIF IFD tags.
  private static final int EXPOSURE_TIME = 33434;
  private static final int APERTURE = 33437;
  private static final int EXPOSURE_PROGRAM = 34850;
  private static final int DATE_TIME_DIGITIZED = 36867;
  private static final int DATE_TIME_ORIGINAL = 36868;
  private static final int EXPOSURE_BIAS_VALUE = 37380;
  private static final int MAX_APERTURE_VALUE = 37381;
  private static final int METERING_MODE = 37383;
  private static final int LIGHT_SOURCE = 37384;
  private static final int FLASH = 37385;
  private static final int FOCAL_LENGTH = 37386;
  private static final int SENSING_METHOD = 37399;
  private static final int MAKER_NOTE = 37500;
  private static final int USER_COMMENT = 37510;
  private static final int SUBSEC_TIME = 37520;
  private static final int SUBSEC_TIME_ORIGINAL = 37521;
  private static final int SUBSEC_TIME_DIGITIZED = 37522;
  private static final int COLOR_SPACE = 40961;
  private static final int FILE_SOURCE = 41728;
  private static final int SCENE_TYPE = 41729;
  private static final int CFA_PATTERN = 41730;
  private static final int CUSTOM_RENDERED = 41985;
  private static final int EXPOSURE_MODE = 41986;
  private static final int WHITE_BALANCE = 41987;
  private static final int DIGITAL_ZOOM_RATIO = 41988;
  private static final int FOCAL_LENGTH_35MM_FILM = 41989;
  private static final int SCENE_CAPTURE_TYPE = 41990;
  private static final int GAIN_CONTROL = 41991;
  private static final int CONTRAST = 41992;
  private static final int SATURATION = 41993;
  private static final int SHARPNESS = 41994;
  private static final int SUBJECT_DISTANCE_RANGE = 41996;

  // Maker Note tags.
  private static final int FIRMWARE_VERSION = 1;
  private static final int ISO = 2;
  private static final int QUALITY = 4;
  private static final int MAKER_WHITE_BALANCE = 5;
  private static final int SHARPENING = 6;
  private static final int FOCUS_MODE = 7;
  private static final int FLASH_SETTING = 8;
  private static final int FLASH_MODE = 9;
  private static final int WHITE_BALANCE_FINE = 11;
  private static final int WHITE_BALANCE_RGB_COEFFS = 12;
  private static final int FLASH_COMPENSATION = 18;
  private static final int TONE_COMPENSATION = 129;
  private static final int LENS_TYPE = 131;
  private static final int LENS = 132;
  private static final int FLASH_USED = 135;
  private static final int CURVE = 140;
  private static final int COLOR_MODE = 141;
  private static final int LIGHT_TYPE = 144;
  private static final int HUE = 146;
  private static final int CAPTURE_EDITOR_DATA = 3585;


  // -- Fields --

  /** True if the data is little endian. */
  protected boolean littleEndian;

  /** Offset to the Nikon Maker Note. */
  protected int makerNoteOffset;


  // -- Constructor --

  /** Constructs a new Nikon reader. */
  public NikonReader() {
    super("Nikon NEF (TIFF)", new String[] {"nef", "tif", "tiff"});
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Nikon NEF file. */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])

    if (block.length < 3) {
      return false;
    }
    if (block.length < 8) {
      return true; // we have no way of verifying further
    }

    boolean little = (block[0] == 0x49 && block[1] == 0x49);

    int ifdlocation = DataTools.bytesToInt(block, 4, little);
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) {
      return false;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, little);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) {
          return false;
        }
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, little);
          if (ifdtag == EXIF_IFD_POINTER || ifdtag == TIFF_EPS_STANDARD) {
            return true;
          }
        }
      }
      return false;
    }
  }

  /** Checks if the given string is a valid filename for a Nikon NEF file. */
  public boolean isThisType(String name) {
    // just checking the filename isn't enough to differentiate between
    // Nikon and regular TIFF; open the file and check more thoroughly
    long len = new File(name).length();
    int size = len < BLOCK_CHECK_LEN ? (int) len : BLOCK_CHECK_LEN;
    byte[] buf = new byte[size];
    try {
      FileInputStream fin = new FileInputStream(name);
      int r = 0;
      while (r < size) r += fin.read(buf, r, size - r);
      fin.close();
      return isThisType(buf);
    }
    catch (IOException e) { return false; }
  }


  // -- Internal BaseTiffReader API methods --

  /** Populate the metadata hashtable. */
  protected void initMetadata() {
    super.initMetadata();

    // look for the TIFF_EPS_STANDARD tag
    // it should contain version information

    short[] version =
      (short[]) TiffTools.getIFDValue(ifds[0], TIFF_EPS_STANDARD);
    String v = "";
    for (int i=0; i<version.length; i++) {
      v += version[i];
    }
    metadata.put("Version", v);

    littleEndian = true;
    try {
      littleEndian = TiffTools.isLittleEndian(ifds[0]);
    }
    catch (FormatException f) { }

    // now look for the EXIF IFD pointer

    int exif = TiffTools.getIFDIntValue(ifds[0], EXIF_IFD_POINTER);
    if (exif != -1) {
      Hashtable exifIFD = parseIFD(exif);

      // put all the EXIF data in the metadata hashtable

      if (exifIFD != null) {
        Enumeration e = exifIFD.keys();
        Integer key;
        String id;
        while (e.hasMoreElements()) {
          key = (Integer) e.nextElement();
          int tag = key.intValue();
          metadata.put(getTagName(tag), exifIFD.get(key));
        }
      }
    }

    // read the maker note

    Hashtable makerNote = parseIFD(makerNoteOffset);
    if (makerNote != null) {
      Enumeration e = makerNote.keys();
      Integer key;
      String id;
      while (e.hasMoreElements()) {
        key = (Integer) e.nextElement();
        int tag = key.intValue();
        metadata.put(getTagName(tag), makerNote.get(key));
      }
    }
  }


  // -- Helper methods --

  /** Parse an IFD from the given offset. */
  private Hashtable parseIFD(int offset) {
    try {
      in.seek(offset);
      Hashtable newIFD = new Hashtable();
      int numEntries = DataTools.read2UnsignedBytes(in, littleEndian);
      for (int i=0; i<numEntries; i++) {
        int tag = DataTools.read2UnsignedBytes(in, littleEndian);
        int type = DataTools.read2UnsignedBytes(in, littleEndian);
        int count = (int) DataTools.read4UnsignedBytes(in, littleEndian);
        if (count < 0) break; // invalid data
        Object value = null;

        if (tag == MAKER_NOTE) {
          long pt = in.getFilePointer();
          makerNoteOffset =
            (int) DataTools.read4UnsignedBytes(in, littleEndian);
          in.seek(pt);
        }

        long pos = in.getFilePointer() + 4;

        if (type == TiffTools.BYTE) {
          // 8-bit unsigned integer
          short[] bytes = new short[count];
          if (count > 4) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            bytes[j] = DataTools.readUnsignedByte(in);
          }
          if (bytes.length == 1) value = new Short(bytes[0]);
          else value = bytes;
        }
        else if (type == TiffTools.ASCII) {
          // 8-bit byte that contain a 7-bit ASCII code;
          // the last byte must be NUL (binary zero)
          byte[] ascii = new byte[count];
          if (count > 4) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          DataTools.readFully(in, ascii);

          // count number of null terminators
          int nullCount = 0;
          for (int j=0; j<count; j++) if (ascii[j] == 0) nullCount++;

          // convert character array to array of strings
          String[] strings = new String[nullCount];
          int c = 0, ndx = -1;
          for (int j=0; j<count; j++) {
            if (ascii[j] == 0) {
              strings[c++] = new String(ascii, ndx + 1, j - ndx - 1);
              ndx = j;
            }
          }
          if (strings.length == 1) value = strings[0];
          else value = strings;
        }
        else if (type == TiffTools.SHORT) {
          // 16-bit (2-byte) unsigned integer

          int[] shorts = new int[count];
          if (count > 2) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            shorts[j] = DataTools.read2UnsignedBytes(in, littleEndian);
          }
          if (shorts.length == 1) value = new Integer(shorts[0]);
          else value = shorts;
        }
        else if (type == TiffTools.LONG) {
          // 32-bit (4-byte) unsigned integer
          long[] longs = new long[count];
          if (count > 1) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            longs[j] = DataTools.read4UnsignedBytes(in, littleEndian);
          }
          if (longs.length == 1) value = new Long(longs[0]);
          else value = longs;
        }
        else if (type == TiffTools.RATIONAL) {
          // Two LONGs: the first represents the numerator of a fraction;
          // the second, the denominator
          TiffRational[] rationals = new TiffRational[count];
          in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            long numer = DataTools.read4UnsignedBytes(in, littleEndian);
            long denom = DataTools.read4UnsignedBytes(in, littleEndian);
            rationals[j] = new TiffRational(numer, denom);
          }
          if (rationals.length == 1) value = rationals[0];
          else value = rationals;
        }
        else if (type == TiffTools.SBYTE || type == TiffTools.UNDEFINED) {
          // SBYTE: An 8-bit signed (twos-complement) integer
          // UNDEFINED: An 8-bit byte that may contain anything,
          // depending on the definition of the field
          byte[] sbytes = new byte[count];
          if (count > 4) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          DataTools.readFully(in, sbytes);
          if (sbytes.length == 1) value = new Byte(sbytes[0]);
          else value = sbytes;
        }
        else if (type == TiffTools.SSHORT) {
          // A 16-bit (2-byte) signed (twos-complement) integer
          short[] sshorts = new short[count];
          if (count > 2) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            sshorts[j] = DataTools.read2SignedBytes(in, littleEndian);
          }
          if (sshorts.length == 1) value = new Short(sshorts[0]);
          else value = sshorts;
        }
        else if (type == TiffTools.SLONG) {
          // A 32-bit (4-byte) signed (twos-complement) integer
          int[] slongs = new int[count];
          if (count > 1) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            slongs[j] = DataTools.read4SignedBytes(in, littleEndian);
          }
          if (slongs.length == 1) value = new Integer(slongs[0]);
          else value = slongs;
        }
        else if (type == TiffTools.SRATIONAL) {
          // Two SLONG's: the first represents the numerator of a fraction,
          // the second the denominator
          TiffRational[] srationals = new TiffRational[count];
          in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            int numer = DataTools.read4SignedBytes(in, littleEndian);
            int denom = DataTools.read4SignedBytes(in, littleEndian);
            srationals[j] = new TiffRational(numer, denom);
          }
          if (srationals.length == 1) value = srationals[0];
          else value = srationals;
        }
        else if (type == TiffTools.FLOAT) {
          // Single precision (4-byte) IEEE format
          float[] floats = new float[count];
          if (count > 1) {
            in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            floats[j] = DataTools.readFloat(in, littleEndian);
          }
          if (floats.length == 1) value = new Float(floats[0]);
          else value = floats;
        }
        else if (type == TiffTools.DOUBLE) {
          // Double precision (8-byte) IEEE format
          double[] doubles = new double[count];
          in.seek(DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            doubles[j] = DataTools.readDouble(in, littleEndian);
          }
          if (doubles.length == 1) value = new Double(doubles[0]);
          else value = doubles;
        }
        in.seek(pos);
        if (value != null) newIFD.put(new Integer(tag), value);
      }
      return newIFD;
    }
    catch (Exception e) { e.printStackTrace(); }
    return null;
  }

  /** Gets the name of the IFD tag encoded by the given number. */
  private String getTagName(int tag) {
    switch (tag) {
      case EXPOSURE_TIME: return "Exposure Time";
      case APERTURE: return "Aperture";
      case EXPOSURE_PROGRAM: return "Exposure Program";
      case DATE_TIME_DIGITIZED: return "Date/Time Digitized";
      case DATE_TIME_ORIGINAL: return "Date/Time Original";
      case EXPOSURE_BIAS_VALUE: return "Exposure Bias Value";
      case MAX_APERTURE_VALUE: return "Max Aperture Value";
      case METERING_MODE: return "Metering Mode";
      case LIGHT_SOURCE: return "Light Source";
      case FLASH: return "Flash Enabled?";
      case FOCAL_LENGTH: return "Focal length of lens";
      case SENSING_METHOD: return "Sensing Method";
      case MAKER_NOTE: return "Offset to maker note";
      case USER_COMMENT: return "User comment";
      case SUBSEC_TIME: return "Subsec. Sampling for Date/Time field";
      case SUBSEC_TIME_ORIGINAL: return "Subsec. Sampling for original date";
      case SUBSEC_TIME_DIGITIZED: return "Subsec. Sampling for digitized date";
      case COLOR_SPACE: return "Color space";
      case FILE_SOURCE: return "File source";
      case SCENE_TYPE: return "Scene type";
      case CFA_PATTERN: return "CFA pattern";
      case CUSTOM_RENDERED: return "Custom Rendered?";
      case EXPOSURE_MODE: return "Exposure mode";
      case WHITE_BALANCE: return "White Balance";
      case DIGITAL_ZOOM_RATIO: return "Digital Zoom Ratio";
      case FOCAL_LENGTH_35MM_FILM: return "Focal Length of 35mm lens";
      case SCENE_CAPTURE_TYPE: return "Scene Capture Type";
      case GAIN_CONTROL: return "Gain Control";
      case CONTRAST: return "Contrast";
      case SATURATION: return "Saturation";
      case SHARPNESS: return "Sharpness";
      case SUBJECT_DISTANCE_RANGE: return "Subject Distance Range";
      case FIRMWARE_VERSION: return "Firmware version";
      case ISO: return "ISO";
      case QUALITY: return "Quality";
      case MAKER_WHITE_BALANCE: return "White Balance (Maker)";
      case SHARPENING: return "Sharpening";
      case FOCUS_MODE: return "Focus Mode";
      case FLASH_SETTING: return "Flash Setting";
      case FLASH_MODE: return "Flash Mode";
      case WHITE_BALANCE_FINE: return "White Balance Fine";
      case WHITE_BALANCE_RGB_COEFFS: return "White Balance (RGB coefficients)";
      case FLASH_COMPENSATION: return "Flash compensation";
      case TONE_COMPENSATION: return "Tone compensation";
      case LENS_TYPE: return "Lens type";
      case LENS: return "Lens";
      case FLASH_USED: return "Flash used?";
      case CURVE: return "Curve";
      case COLOR_MODE: return "Color mode";
      case LIGHT_TYPE: return "Light type";
      case HUE: return "Hue";
      case CAPTURE_EDITOR_DATA: return "Capture Editor Data";
    }
    return "" + tag;
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new NikonReader().testRead(args);
  }

}
