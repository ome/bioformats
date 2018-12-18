/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.codec.NikonCodec;
import loci.formats.codec.NikonCodecOptions;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;

/**
 * NikonReader is the file format reader for Nikon NEF (TIFF) files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class NikonReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(NikonReader.class);

  public static final String[] NEF_SUFFIX = {"nef"};

  // Tags that give a good indication of whether this is an NEF file.
  private static final int TIFF_EPS_STANDARD = 37398;
  private static final int COLOR_MAP = 33422;

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

  /** Offset to the Nikon Maker Note. */
  protected int makerNoteOffset;

  /** The original IFD. */
  protected IFD original;

  private TiffRational[] whiteBalance;
  private Object cfaPattern;
  private int[] curve;
  private int[] vPredictor;
  private boolean lossyCompression;
  private int split = -1;

  private byte[] lastPlane = null;
  private int lastIndex = -1;

  // -- Constructor --

  /** Constructs a new Nikon reader. */
  public NikonReader() {
    super("Nikon NEF", new String[] {"nef", "tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    mergeSubIFDs = true;
    canSeparateSeries = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    // extension is sufficient as long as it is NEF
    if (checkSuffix(name, NEF_SUFFIX)) return true;
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    if (ifd.containsKey(TIFF_EPS_STANDARD)) return true;
    String make = ifd.getIFDTextValue(IFD.MAKE);
    return make != null && make.indexOf("Nikon") != -1;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    IFD ifd = ifds.get(no);
    int[] bps = ifd.getBitsPerSample();
    int dataSize = bps[0];

    long[] byteCounts = ifd.getStripByteCounts();
    long totalBytes = 0;
    for (long b : byteCounts) {
      totalBytes += b;
    }
    if (totalBytes == FormatTools.getPlaneSize(this) || bps.length > 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    if (lastPlane == null || lastIndex != no) {
      long[] offsets = ifd.getStripOffsets();

      boolean maybeCompressed = ifd.getCompression() == TiffCompression.NIKON;
      boolean compressed =
        vPredictor != null && curve != null && maybeCompressed;

      if (!maybeCompressed && dataSize == 14) dataSize = 16;

      ByteArrayOutputStream src = new ByteArrayOutputStream();

      NikonCodec codec = new NikonCodec();
      NikonCodecOptions options = new NikonCodecOptions();
      options.width = getSizeX();
      options.height = getSizeY();
      options.bitsPerSample = dataSize;
      options.curve = curve;
      if (vPredictor != null) {
        options.vPredictor = new int[vPredictor.length];
      }
      options.lossless = !lossyCompression;
      options.split = split;

      for (int i=0; i<byteCounts.length; i++) {
        byte[] t = new byte[(int) byteCounts[i]];

        in.seek(offsets[i]);
        in.read(t);

        if (compressed) {
          options.maxBytes = (int) byteCounts[i];
          System.arraycopy(vPredictor, 0, options.vPredictor, 0,
            vPredictor.length);
          t = codec.decompress(t, options);
        }
        src.write(t);
      }

      RandomAccessInputStream bb = new RandomAccessInputStream(
        new ByteArrayHandle(src.toByteArray()));
      short[] pix = new short[getSizeX() * getSizeY() * 3];

      src.close();

      int[] colorMap = {1, 0, 2, 1}; // default color map
      short[] ifdColors = (short[]) ifd.get(COLOR_MAP);
      if (ifdColors != null && ifdColors.length >= colorMap.length) {
        boolean colorsValid = true;
        for (int q=0; q<colorMap.length; q++) {
          if (ifdColors[q] < 0 || ifdColors[q] > 2) {
            // found invalid channel index, use default color map instead
            colorsValid = false;
            break;
          }
        }
        if (colorsValid) {
          for (int q=0; q<colorMap.length; q++) {
            colorMap[q] = ifdColors[q];
          }
        }
      }

      boolean interleaveRows =
        offsets.length == 1 && !maybeCompressed && colorMap[0] != 0;

      for (int row=0; row<getSizeY(); row++) {
        int realRow = interleaveRows ? (row < (getSizeY() / 2) ?
          row * 2 : (row - (getSizeY() / 2)) * 2 + 1) : row;
        for (int col=0; col<getSizeX(); col++) {
          short val = (short) (bb.readBits(dataSize) & 0xffff);
          int mapIndex = (realRow % 2) * 2 + (col % 2);

          int redOffset = realRow * getSizeX() + col;
          int greenOffset = (getSizeY() + realRow) * getSizeX() + col;
          int blueOffset = (2 * getSizeY() + realRow) * getSizeX() + col;

          if (colorMap[mapIndex] == 0) {
            pix[redOffset] = adjustForWhiteBalance(val, 0);
          }
          else if (colorMap[mapIndex] == 1) {
            pix[greenOffset] = adjustForWhiteBalance(val, 1);
          }
          else if (colorMap[mapIndex] == 2) {
            pix[blueOffset] = adjustForWhiteBalance(val, 2);
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
      bb.close();

      lastPlane = new byte[FormatTools.getPlaneSize(this)];
      ImageTools.interpolate(pix, lastPlane, colorMap, getSizeX(), getSizeY(),
        isLittleEndian());
      lastIndex = no;
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType()) * 3;
    int rowLen = w * bpp;
    int width = getSizeX() * bpp;
    for (int row=0; row<h; row++) {
      System.arraycopy(
        lastPlane, (row + y) * width + x * bpp, buf, row * rowLen, rowLen);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      makerNoteOffset = 0;
      original = null;
      split = -1;
      whiteBalance = null;
      cfaPattern = null;
      curve = null;
      vPredictor = null;
      lossyCompression = false;
      lastPlane = null;
      lastIndex = -1;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // reset image dimensions
    // the actual image data is stored in IFDs referenced by the SubIFD tag
    // in the 'real' IFD

    CoreMetadata m = core.get(0, 0);

    m.imageCount = ifds.size();

    IFD firstIFD = ifds.get(0);
    PhotoInterp photo = firstIFD.getPhotometricInterpretation();
    int samples = firstIFD.getSamplesPerPixel();
    m.rgb = samples > 1 || photo == PhotoInterp.RGB ||
      photo == PhotoInterp.CFA_ARRAY;
    if (photo == PhotoInterp.CFA_ARRAY) samples = 3;

    m.sizeX = (int) firstIFD.getImageWidth();
    m.sizeY = (int) firstIFD.getImageLength();
    m.sizeZ = 1;
    m.sizeC = isRGB() ? samples : 1;
    m.sizeT = ifds.size();
    m.pixelType = firstIFD.getPixelType();
    m.indexed = false;

    // now look for the EXIF IFD pointer

    IFDList exifIFDs = tiffParser.getExifIFDs();
    if (exifIFDs.size() > 0) {
      IFD exifIFD = exifIFDs.get(0);
      tiffParser.fillInIFD(exifIFD);

      // put all the EXIF data in the metadata hashtable

      for (Integer key : exifIFD.keySet()) {
        int tag = key.intValue();
        String name = IFD.getIFDTagName(tag);
        if (tag == IFD.CFA_PATTERN) {
          byte[] cfa = (byte[]) exifIFD.get(key);
          int[] colorMap = new int[cfa.length];
          for (int i=0; i<cfa.length; i++) colorMap[i] = (int) cfa[i];
          addGlobalMeta(name, colorMap);
          cfaPattern = colorMap;
        }
        else {
          addGlobalMeta(name, exifIFD.get(key));
          if (name.equals("MAKER_NOTE")) {
            byte[] b = (byte[]) exifIFD.get(key);
            int extra = new String(
              b, 0, 10, Constants.ENCODING).startsWith("Nikon") ? 10 : 0;
            byte[] buf = new byte[b.length];
            System.arraycopy(b, extra, buf, 0, buf.length - extra);
            IFD note = null;
            try (RandomAccessInputStream makerNote =
                  new RandomAccessInputStream(buf)) {
              TiffParser tp = new TiffParser(makerNote);
              note = tp.getFirstIFD();
            }
            catch (Exception e) {
              LOGGER.debug("Failed to parse first IFD", e);
            }
            if (note != null) {
              for (Integer nextKey : note.keySet()) {
                int nextTag = nextKey.intValue();
                addGlobalMeta(name, note.get(nextKey));
                if (nextTag == 150) {
                  b = (byte[]) note.get(nextKey);
                  RandomAccessInputStream s = new RandomAccessInputStream(b);
                  byte check1 = s.readByte();
                  byte check2 = s.readByte();

                  lossyCompression = check1 != 0x46;

                  vPredictor = new int[4];
                  for (int q=0; q<vPredictor.length; q++) {
                    vPredictor[q] = s.readShort();
                  }

                  curve = new int[16385];

                  int bps = ifds.get(0).getBitsPerSample()[0];
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
          }
        }
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    original = ifds.get(0);
    if (cfaPattern != null) {
      original.putIFDValue(IFD.COLOR_MAP, (int[]) cfaPattern);
    }
    ifds.set(0, original);

    CoreMetadata m = core.get(0, 0);

    m.imageCount = 1;
    m.sizeT = 1;
    if (ifds.get(0).getSamplesPerPixel() == 1) {
      m.interleaved = true;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private short adjustForWhiteBalance(short val, int index) {
    if (whiteBalance != null && whiteBalance.length == 3) {
      return (short) (val * whiteBalance[index].doubleValue());
    }
    return val;
  }

}
