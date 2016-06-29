/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import loci.common.DataTools;
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
 * DNGReader is the file format reader for Canon DNG (TIFF) files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class DNGReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(DNGReader.class);

  private static final int CANON_TAG = 34665;
  private static final int TIFF_EPS_STANDARD = 37398;
  private static final int COLOR_MAP = 33422;
  private static final int WHITE_BALANCE_RGB_COEFFS = 16385;

  // -- Fields --

  /** The original IFD. */
  protected IFD original;

  private double[] whiteBalance;
  private Object cfaPattern;

  private byte[] lastPlane = null;
  private int lastIndex = -1;

  // -- Constructor --

  /** Constructs a new DNG reader. */
  public DNGReader() {
    super("DNG",
      new String[] {"cr2", "crw", "jpg", "thm", "wav", "tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    boolean hasEPSTag = ifd.containsKey(TIFF_EPS_STANDARD);
    if (!hasEPSTag) {
      hasEPSTag = ifd.containsKey(CANON_TAG);
    }
    String make = ifd.getIFDTextValue(IFD.MAKE);
    String model = ifd.getIFDTextValue(IFD.MODEL);
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    return make != null && make.indexOf("Canon") != -1 && hasEPSTag &&
      (model == null || !model.endsWith("S1 IS")) && (software == null ||
      software.indexOf("Canon") != -1);
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
      // don't call super.openBytes here
      // the pixel type of the image as stored in the TIFF is UINT8,
      // but we need to expand it out to UINT16 (based upon the white balance)

      if (tiffParser == null) {
        initTiffParser();
      }

      byte[] b = new byte[buf.length / 2];
      tiffParser.getSamples(ifds.get(0), b, x, y, w, h);

      for (int i=0; i<b.length; i++) {
        int c = isInterleaved() ? i % 3 : i / (b.length / 3);
        short v = (short) (b[i] & 0xff);

        v = adjustForWhiteBalance(v, c);
        DataTools.unpackBytes(v, buf, i * 2, 2, isLittleEndian());
      }
      return buf;
    }

    if (lastPlane == null || lastIndex != no) {
      long[] offsets = ifd.getStripOffsets();

      ByteArrayOutputStream src = new ByteArrayOutputStream();

      for (int i=0; i<byteCounts.length; i++) {
        byte[] t = new byte[(int) byteCounts[i]];

        in.seek(offsets[i]);
        in.read(t);

        src.write(t);
      }

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

      lastPlane = new byte[FormatTools.getPlaneSize(this)];

      RandomAccessInputStream bb =
        new RandomAccessInputStream(new ByteArrayHandle(src.toByteArray()));
      src.close();
      short[] pix = new short[getSizeX() * getSizeY() * 3];

      for (int row=0; row<getSizeY(); row++) {
        int realRow = row;
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
        }
      }
      bb.close();

      ImageTools.interpolate(pix, buf, colorMap, getSizeX(), getSizeY(),
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
      original = null;
      whiteBalance = null;
      cfaPattern = null;
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
    CoreMetadata m = core.get(0);

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
    m.pixelType = FormatTools.UINT16;
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
            int offset = DataTools.bytesToInt(b, b.length - 4, isLittleEndian());
            byte[] buf = new byte[b.length + offset - 8];
            System.arraycopy(b, b.length - 8, buf, 0, 8);
            System.arraycopy(b, 0, buf, offset, b.length - 8);
            RandomAccessInputStream makerNote =
              new RandomAccessInputStream(buf);
            TiffParser tp = new TiffParser(makerNote);
            IFD note = null;
            try {
              note = tp.getFirstIFD();
            }
            catch (Exception e) {
              LOGGER.debug("Failed to parse first IFD", e);
            }
            if (note != null) {
              for (Integer nextKey : note.keySet()) {
                int nextTag = nextKey.intValue();
                addGlobalMeta(name, note.get(nextKey));
                if (nextTag == WHITE_BALANCE_RGB_COEFFS) {
                  if (note.get(nextTag) instanceof TiffRational[]) {
                    TiffRational[] wb = (TiffRational[]) note.get(nextTag);
                    whiteBalance = new double[wb.length];
                    for (int i=0; i<wb.length; i++) {
                      whiteBalance[i] = wb[i].doubleValue();
                    }
                  }
                  else {
                    // use a default white balance table
                    whiteBalance = new double[3];
                    whiteBalance[0] = 2.391381;
                    whiteBalance[1] = 0.929156;
                    whiteBalance[2] = 1.298254;
                  }
                }
              }
            }
            makerNote.close();
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

    CoreMetadata m = core.get(0);
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
      return (short) (val * whiteBalance[index]);
    }
    return val;
  }

}
