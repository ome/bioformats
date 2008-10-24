//
// NikonReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.BitBuffer;
import loci.formats.codec.ByteVector;
import loci.formats.codec.NikonCodec;

/**
 * NikonReader is the file format reader for Nikon NEF (TIFF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NikonReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NikonReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class NikonReader extends BaseTiffReader {

  // -- Constants --

  public static final String[] NEF_SUFFIX = {"nef"};

  // Tags that give a good indication of whether this is an NEF file.
  private static final int EXIF_IFD_POINTER = 34665;
  private static final int TIFF_EPS_STANDARD = 37398;

  // EXIF IFD tags.
  private static final int CFA_REPEAT_DIM = 33421;
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

  // Custom IFD tags
  private static final int SUB_IFD = 330;

  // -- Fields --

  /** Offset to the Nikon Maker Note. */
  protected int makerNoteOffset;

  /** The original IFD. */
  protected Hashtable original;

  private TiffRational[] whiteBalance;
  private Object cfaPattern;
  private int[] curve;
  private int[] vPredictor;
  private boolean lossyCompression;
  private int split = -1;

  // -- Constructor --

  /** Constructs a new Nikon reader. */
  public NikonReader() {
    super("Nikon NEF", new String[] {"nef", "tif", "tiff"});
    blockCheckLen = 16384;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    // extension is sufficient as long as it is NEF
    if (checkSuffix(name, NEF_SUFFIX)) return true;

    if (!open) return false;
    try {
      RandomAccessStream stream = new RandomAccessStream(name);
      boolean isThisType = isThisType(stream);
      stream.close();
      return isThisType;
    }
    catch (IOException e) {
      if (debug) trace(e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    Hashtable ifd = TiffTools.getFirstIFD(stream);
    return ifd != null && ifd.containsKey(new Integer(TIFF_EPS_STANDARD));
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int dataSize = TiffTools.getBitsPerSample(ifds[no])[0];

    long[] byteCounts = TiffTools.getStripByteCounts(ifds[no]);
    long[] offsets = TiffTools.getStripOffsets(ifds[no]);
    long[] rowsPerStrip = TiffTools.getRowsPerStrip(ifds[no]);

    ByteVector src = new ByteVector();

    float bytesPerSample = (float) dataSize / 8;
    boolean maybeCompressed =
      TiffTools.getCompression(ifds[no]) == TiffTools.NIKON;
    boolean compressed = vPredictor != null && curve != null && maybeCompressed;

    if (!maybeCompressed && dataSize == 14) dataSize = 16;

    for (int i=0; i<byteCounts.length; i++) {
      byte[] t = new byte[(int) byteCounts[i]];

      in.seek(offsets[i]);
      in.read(t);
      if (compressed) {
        Object[] options = new Object[8];
        options[0] = curve;
        options[1] = new Integer(dataSize);
        options[2] = new Integer((int) byteCounts[i]);
        options[3] = new Integer(getSizeX());
        options[4] = new Integer(getSizeY());
        options[5] = vPredictor;
        options[6] = new Boolean(lossyCompression);
        options[7] = new Integer(split);
        t = new NikonCodec().decompress(t, options);
      }
      src.add(t);
    }

    BitBuffer bb = new BitBuffer(src.toByteArray());
    short[] pix = new short[getSizeX() * getSizeY() * 3];

    int[] colorMap = new int[4];
    short[] s = (short[]) ifds[no].get(new Integer(33422));
    for (int q=0; q<colorMap.length; q++) {
      colorMap[q] = s[q];
      if (colorMap[q] > 2) {
        // found invalid channel index, use default color map instead
        colorMap[0] = 1;
        colorMap[1] = 0;
        colorMap[2] = 2;
        colorMap[3] = 1;
        break;
      }
    }

    boolean interleaveRows = offsets.length == 1 && !maybeCompressed;

    for (int row=0; row<getSizeY(); row++) {
      int realRow = interleaveRows ? (row < (getSizeY() / 2) ?
        row * 2 : (row - (getSizeY() / 2)) * 2 + 1) : row;
      for (int col=0; col<getSizeX(); col++) {
        short val = (short) (bb.getBits(dataSize) & 0xffff);
        int mapIndex = (realRow % 2) * 2 + (col % 2);

        int redOffset = realRow * getSizeX() + col;
        int greenOffset = (getSizeY() + realRow) * getSizeX() + col;
        int blueOffset = (2 * getSizeY() + realRow) * getSizeX() + col;

        if (colorMap[mapIndex] == 0) {
          if (whiteBalance != null && whiteBalance.length == 3) {
            val = (short) (val * whiteBalance[0].doubleValue());
          }
          pix[redOffset] = val;
        }
        else if (colorMap[mapIndex] == 1) {
          if (whiteBalance != null && whiteBalance.length == 3) {
            val = (short) (val * whiteBalance[1].doubleValue());
          }
          pix[greenOffset] = val;
        }
        else if (colorMap[mapIndex] == 2) {
          if (whiteBalance != null && whiteBalance.length == 3) {
            val = (short) (val * whiteBalance[2].doubleValue());
          }
          pix[blueOffset] = val;
        }

        if (maybeCompressed && !compressed) {
          int toSkip = 0;
          if ((col % 10) == 9) {
            toSkip = 1;
          }
          if (col == getSizeX() - 1) {
            toSkip = 10;
          }
          bb.skipBits(toSkip * 8);
        }
      }
    }

    return ImageTools.interpolate(pix, buf, colorMap, getSizeX(), getSizeY(),
      isLittleEndian());
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    makerNoteOffset = 0;
    original = null;
    split = -1;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // reset image dimensions
    // the actual image data is stored in IFDs referenced by the SubIFD tag
    // in the 'real' IFD

    original = ifds[0];
    long[] subIFDOffsets = TiffTools.getIFDLongArray(original, SUB_IFD, false);

    if (subIFDOffsets != null) {
      Vector tmpIFDs = new Vector();

      for (int i=0; i<subIFDOffsets.length; i++) {
        Hashtable ifd = TiffTools.getIFD(in, i, subIFDOffsets[i]);
        if (TiffTools.getIFDIntValue(ifd, TiffTools.NEW_SUBFILE_TYPE) == 0) {
          tmpIFDs.add(ifd);
        }
      }

      ifds = (Hashtable[]) tmpIFDs.toArray(new Hashtable[0]);

      core[0].imageCount = ifds.length;

      int photo = TiffTools.getPhotometricInterpretation(ifds[0]);
      int samples = TiffTools.getSamplesPerPixel(ifds[0]);
      core[0].rgb = samples > 1 || photo == TiffTools.RGB ||
        photo == TiffTools.CFA_ARRAY;
      if (photo == TiffTools.CFA_ARRAY) samples = 3;

      core[0].sizeX = (int) TiffTools.getImageWidth(ifds[0]);
      core[0].sizeY = (int) TiffTools.getImageLength(ifds[0]);
      core[0].sizeZ = 1;
      core[0].sizeC = isRGB() ? samples : 1;
      core[0].sizeT = ifds.length;
      core[0].pixelType = getPixelType(ifds[0]);
      core[0].indexed = false;
    }

    // now look for the EXIF IFD pointer

    int exif = TiffTools.getIFDIntValue(original, EXIF_IFD_POINTER);
    if (exif != -1) {
      Hashtable exifIFD = TiffTools.getIFD(in, 0, exif);

      // put all the EXIF data in the metadata hashtable

      if (exifIFD != null) {
        Enumeration e = exifIFD.keys();
        Integer key;
        while (e.hasMoreElements()) {
          key = (Integer) e.nextElement();
          int tag = key.intValue();
          if (tag == CFA_PATTERN) {
            byte[] cfa = (byte[]) exifIFD.get(key);
            int[] colorMap = new int[cfa.length];
            for (int i=0; i<cfa.length; i++) colorMap[i] = (int) cfa[i];
            addMeta(getTagName(tag), colorMap);
            cfaPattern = colorMap;
          }
          else {
            addMeta(getTagName(tag), exifIFD.get(key));
            if (getTagName(tag).equals("Offset to maker note")) {
              byte[] b = (byte[]) exifIFD.get(key);
              int extra = new String(b, 0, 10).startsWith("Nikon") ? 10 : 0;
              byte[] buf = new byte[b.length];
              System.arraycopy(b, extra, buf, 0, buf.length - extra);
              RandomAccessStream makerNote = new RandomAccessStream(buf);
                Hashtable note = TiffTools.getFirstIFD(makerNote);
                if (note != null) {
                  Enumeration en = note.keys();
                  Integer nextKey;
                  while (en.hasMoreElements()) {
                    nextKey = (Integer) en.nextElement();
                    int nextTag = nextKey.intValue();
                    addMeta(getTagName(nextTag), note.get(nextKey));
                    if (nextTag == 150) {
                      b = (byte[]) note.get(nextKey);
                      RandomAccessStream s = new RandomAccessStream(b);
                      byte check1 = s.readByte();
                      byte check2 = s.readByte();

                      lossyCompression = check1 != 0x46;

                      vPredictor = new int[4];
                      for (int q=0; q<vPredictor.length; q++) {
                        vPredictor[q] = s.readShort();
                      }

                      curve = new int[16385];

                      int bps = TiffTools.getBitsPerSample(ifds[0])[0];
                      int max = 1 << bps & 0x7fff;
                      int step = 0;
                      int csize = s.readShort();
                      if (csize > 1) {
                        step = max / (csize - 1);
                      }

                      if (check1 == 0x44 && check2 == 0x20 && step > 0) {
                        for (int i=0; i<csize; i++) {
                          curve[i * step] = s.readShort();
                        }
                        for (int i=0; i<max; i++) {
                          int n = i % step;
                          curve[i] = (curve[i - n] * (step - n) +
                            curve[i - n + step] * n) / step;
                        }
                        s.seek(562);
                        split = s.readShort();
                      }
                      else {
                        int maxValue = (int) Math.pow(2, bps) - 1;
                        Arrays.fill(curve, maxValue);
                        int nElements =
                          (int) (s.length() - s.getFilePointer()) / 2;
                        if (nElements < 100) {
                          for (int i=0; i<curve.length; i++) {
                            curve[i] = (short) i;
                          }
                        }
                        else {
                          for (int q=0; q<nElements; q++) {
                            curve[q] = s.readShort();
                          }
                        }
                      }
                      s.close();
                    }
                    else if (nextTag == WHITE_BALANCE_RGB_COEFFS) {
                      whiteBalance = (TiffRational[]) note.get(nextKey);
                    }
                  }
                }
              makerNote.close();
            }
          }
        }
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("NikonReader.initFile(" + id + ")");
    super.initFile(id);

    in = new RandomAccessStream(id);
    if (in.readShort() == 0x4949) in.order(true);

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");

    // look for the SubIFD tag (330);

    int offset = 0;
    try {
      offset = TiffTools.getIFDIntValue(ifds[0], 330, false, 0);
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
      long[] array = TiffTools.getIFDLongArray(ifds[0], 330, false);
      offset = (int) array[array.length - 1];
    }

    Hashtable realImage = TiffTools.getIFD(in, 1, offset);

    original = ifds[0];
    ifds[0] = realImage;
    core[0].imageCount = 1;

    if (cfaPattern != null) {
      ifds[0].put(new Integer(TiffTools.COLOR_MAP), (int[]) cfaPattern);
    }
    core[0].interleaved = true;
  }

  // -- Helper methods --

  /** Gets the name of the IFD tag encoded by the given number. */
  private String getTagName(int tag) {
    switch (tag) {
      case CFA_REPEAT_DIM:
        return "CFA Repeat Dimensions";
      case EXPOSURE_TIME:
        return "Exposure Time";
      case APERTURE:
        return "Aperture";
      case EXPOSURE_PROGRAM:
        return "Exposure Program";
      case DATE_TIME_DIGITIZED:
        return "Date/Time Digitized";
      case DATE_TIME_ORIGINAL:
        return "Date/Time Original";
      case EXPOSURE_BIAS_VALUE:
        return "Exposure Bias Value";
      case MAX_APERTURE_VALUE:
        return "Max Aperture Value";
      case METERING_MODE:
        return "Metering Mode";
      case LIGHT_SOURCE:
        return "Light Source";
      case FLASH:
        return "Flash Enabled?";
      case FOCAL_LENGTH:
        return "Focal length of lens";
      case SENSING_METHOD:
        return "Sensing Method";
      case MAKER_NOTE:
        return "Offset to maker note";
      case USER_COMMENT:
        return "User comment";
      case SUBSEC_TIME:
        return "Subsec. Sampling for Date/Time field";
      case SUBSEC_TIME_ORIGINAL:
        return "Subsec. Sampling for original date";
      case SUBSEC_TIME_DIGITIZED:
        return "Subsec. Sampling for digitized date";
      case COLOR_SPACE:
        return "Color space";
      case FILE_SOURCE:
        return "File source";
      case SCENE_TYPE:
        return "Scene type";
      case CFA_PATTERN:
        return "CFA pattern";
      case CUSTOM_RENDERED:
        return "Custom Rendered?";
      case EXPOSURE_MODE:
        return "Exposure mode";
      case WHITE_BALANCE:
        return "White Balance";
      case DIGITAL_ZOOM_RATIO:
        return "Digital Zoom Ratio";
      case FOCAL_LENGTH_35MM_FILM:
        return "Focal Length of 35mm lens";
      case SCENE_CAPTURE_TYPE:
        return "Scene Capture Type";
      case GAIN_CONTROL:
        return "Gain Control";
      case CONTRAST:
        return "Contrast";
      case SATURATION:
        return "Saturation";
      case SHARPNESS:
        return "Sharpness";
      case SUBJECT_DISTANCE_RANGE:
        return "Subject Distance Range";
      case FIRMWARE_VERSION:
        return "Firmware version";
      case ISO:
        return "ISO";
      case QUALITY:
        return "Quality";
      case MAKER_WHITE_BALANCE:
        return "White Balance (Maker)";
      case SHARPENING:
        return "Sharpening";
      case FOCUS_MODE:
        return "Focus Mode";
      case FLASH_SETTING:
        return "Flash Setting";
      case FLASH_MODE:
        return "Flash Mode";
      case WHITE_BALANCE_FINE:
        return "White Balance Fine";
      case WHITE_BALANCE_RGB_COEFFS:
        return "White Balance (RGB coefficients)";
      case FLASH_COMPENSATION:
        return "Flash compensation";
      case TONE_COMPENSATION:
        return "Tone compensation";
      case LENS_TYPE:
        return "Lens type";
      case LENS:
        return "Lens";
      case FLASH_USED:
        return "Flash used?";
      case CURVE:
        return "Curve";
      case COLOR_MODE:
        return "Color mode";
      case LIGHT_TYPE:
        return "Light type";
      case HUE:
        return "Hue";
      case CAPTURE_EDITOR_DATA:
        return "Capture Editor Data";
    }
    return "" + tag;
  }

}
