/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.PackbitsCodec;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;
import ome.units.UNITS;

import static loci.formats.in.DicomAttribute.*;
import static loci.formats.in.DicomVR.*;

/**
 * DicomReader is the file format reader for DICOM files.
 * Much of this code is adapted from ImageJ's DICOM reader; see
 * http://rsb.info.nih.gov/ij/developer/source/ij/plugin/DICOM.java.html
 */
public class DicomReader extends FormatReader {

  // -- Constants --

  public static final String DICOM_MAGIC_STRING = "DICM";
  public static final int HEADER_LENGTH = 128;

  private static final String[] DICOM_SUFFIXES = {
    "dic", "dcm", "dicom", "j2ki", "j2kr"
  };

  // -- Fields --

  /** Bits per pixel. */
  private int bitsPerPixel;

  private int location;
  private int elementLength;
  private DicomVR vr;
  private boolean oddLocations;
  private boolean inSequence;
  private boolean bigEndianTransferSyntax;
  private byte[][] lut;
  private short[][] shortLut;
  private long[] offsets;
  private int maxPixelRange;
  private int centerPixelValue;
  
  private double rescaleSlope = 1.0, rescaleIntercept = 0.0;

  private boolean isJP2K = false;
  private boolean isJPEG = false;
  private boolean isRLE = false;
  private boolean isDeflate = false;
  private boolean inverted;

  private String date, time, imageType;
  private String pixelSizeX, pixelSizeY;
  private Double pixelSizeZ;
  private List<Double> positionX = new ArrayList<Double>();
  private List<Double> positionY = new ArrayList<Double>();
  private List<Double> positionZ = new ArrayList<Double>();

  private Map<Integer, List<String>> fileList;
  private int imagesPerFile;

  private String originalDate, originalTime, originalInstance;
  private int originalSeries;
  private int originalX, originalY;

  private DicomReader helper;

  private List<String> companionFiles = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new DICOM reader. */
  // "Digital Imaging and Communications in Medicine" is nasty long.
  public DicomReader() {
    super("DICOM", new String[] {
      "dic", "dcm", "dicom", "jp2", "j2ki", "j2kr", "raw", "ima"});
    suffixNecessary = false;
    suffixSufficient = false;
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    datasetDescription = "One or more .dcm or .dicom files";
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    // extension is sufficient as long as it is DIC, DCM, DICOM, J2KI, or J2KR
    if (checkSuffix(name, DICOM_SUFFIXES)) return true;
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1024;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;

    stream.seek(HEADER_LENGTH);
    if (stream.readString(4).equals(DICOM_MAGIC_STRING)) return true;
    stream.seek(0);

    try {
      int tag = getNextTag(stream, false);
      DicomAttribute attribute = DicomAttribute.get(tag);
      return attribute != null && attribute != INVALID;
    }
    catch (NullPointerException e) { }
    catch (FormatException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files) throws FormatException, IOException {
    for (String file : files) {
      if (file.endsWith("DICOMDIR")) {
        return 1;
      }
    }
    return super.getRequiredDirectories(files);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.INT8 &&
      getPixelType() != FormatTools.UINT8)
    {
      return null;
    }
    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.INT16 &&
      getPixelType() != FormatTools.UINT16)
   {
      return null;
   }
    return shortLut;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels || fileList == null) return null;
    Integer[] keys = fileList.keySet().toArray(new Integer[0]);
    Arrays.sort(keys);
    final List<String> files = fileList.get(keys[getSeries()]);
    if (files == null) {
      return null;
    }
    final List<String> uniqueFiles = new ArrayList<String>();
    for (String f : files) {
      if (!uniqueFiles.contains(f)) {
        uniqueFiles.add(f);
      }
    }
    for (String f : companionFiles) {
      if (!uniqueFiles.contains(f)) {
        uniqueFiles.add(f);
      }
    }
    return uniqueFiles.toArray(new String[uniqueFiles.size()]);
  }

  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return CAN_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    Integer[] keys = fileList.keySet().toArray(new Integer[0]);
    Arrays.sort(keys);
    if (fileList.size() > 1 || fileList.get(keys[getSeries()]).size() > 1) {
      int fileNumber = 0;
      if (fileList.get(keys[getSeries()]).size() > 1) {
        fileNumber = no / imagesPerFile;
        no = no % imagesPerFile;
      }
      String file = fileList.get(keys[getSeries()]).get(fileNumber);
      helper.setId(file);
      return helper.openBytes(no, buf, x, y, w, h);
    }

    int ec = isIndexed() ? 1 : getSizeC();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int bytes = getSizeX() * getSizeY() * bpp * ec;
    if (in == null) {
      in = new RandomAccessInputStream(currentId);
    }
    in.seek(offsets[no]);

    if (isRLE) {
      // plane is compressed using run-length encoding
      CodecOptions options = new CodecOptions();
      options.maxBytes = getSizeX() * getSizeY();
      for (int c=0; c<ec; c++) {
        PackbitsCodec codec = new PackbitsCodec();
        byte[] t = null;

        if (bpp > 1) {
          int plane = bytes / (bpp * ec);
          byte[][] tmp = new byte[bpp][];
          long start = in.getFilePointer();
          for (int i=0; i<bpp; i++) {
            // one or more extra 0 bytes can be inserted between
            // the planes, but there isn't a good way to know in advance
            // only way to know is to see if decompressing produces the
            // correct number of bytes
            tmp[i] = codec.decompress(in, options);
            if (i > 0 && tmp[i].length > options.maxBytes) {
              in.seek(start);
              tmp[i] = codec.decompress(in, options);
            }
            if (no < imagesPerFile - 1 || i < bpp - 1) {
              start = in.getFilePointer();
              while (in.read() == 0);
              long end = in.getFilePointer();
              in.seek(end - 1);
            }
          }
          t = new byte[bytes / ec];
          for (int i=0; i<plane; i++) {
            for (int j=0; j<bpp; j++) {
              int byteIndex = isLittleEndian() ? bpp - j - 1 : j;
              if (i < tmp[byteIndex].length) {
                t[i * bpp + j] = tmp[byteIndex][i];
              }
            }
          }
        }
        else {
          t = codec.decompress(in, options);
          if (t.length < (bytes / ec)) {
            byte[] tmp = t;
            t = new byte[bytes / ec];
            System.arraycopy(tmp, 0, t, 0, tmp.length);
          }
          if (no < imagesPerFile - 1 || c < ec - 1) {
            while (in.read() == 0);
            in.seek(in.getFilePointer() - 1);
          }
        }

        int rowLen = w * bpp;
        int srcRowLen = getSizeX() * bpp;

        for (int row=0; row<h; row++) {
          int src = (row + y) * srcRowLen + x * bpp;
          int dest = (h * c + row) * rowLen;
          int len = (int) Math.min(rowLen, t.length - src - 1);
          if (len < 0) break;
          System.arraycopy(t, src, buf, dest, len);
        }
      }
    }
    else if (isJPEG || isJP2K) {
      // plane is compressed using JPEG or JPEG-2000
      long end = no < offsets.length - 1 ? offsets[no + 1] : in.length();
      byte[] b = new byte[(int) (end - in.getFilePointer())];
      in.read(b);

      if (b[2] != (byte) 0xff) {
        byte[] tmp = new byte[b.length + 1];
        tmp[0] = b[0];
        tmp[1] = b[1];
        tmp[2] = (byte) 0xff;
        System.arraycopy(b, 2, tmp, 3, b.length - 2);
        b = tmp;
      }
      if ((b[3] & 0xff) >= 0xf0) {
        b[3] -= (byte) 0x30;
      }

      int pt = b.length - 2;
      while (pt >= 0 && b[pt] != (byte) 0xff || b[pt + 1] != (byte) 0xd9) {
        pt--;
      }
      if (pt < b.length - 2) {
        byte[] tmp = b;
        b = new byte[pt + 2];
        System.arraycopy(tmp, 0, b, 0, b.length);
      }

      Codec codec = null;
      CodecOptions options = new CodecOptions();
      options.littleEndian = isLittleEndian();
      options.interleaved = isInterleaved();
      if (isJPEG) codec = new JPEGCodec();
      else codec = new JPEG2000Codec();
      b = codec.decompress(b, options);

      int rowLen = w * bpp;
      int srcRowLen = getSizeX() * bpp;

      int srcPlane = getSizeY() * srcRowLen;

      for (int c=0; c<ec; c++) {
        for (int row=0; row<h; row++) {
          System.arraycopy(b, c * srcPlane + (row + y) * srcRowLen + x * bpp,
            buf, h * rowLen * c + row * rowLen, rowLen);
        }
      }
    }
    else if (isDeflate) {
      // TODO
      throw new UnsupportedCompressionException(
        "Deflate data is not supported.");
    }
    else {
      // plane is not compressed
      readPlane(in, x, y, w, h, buf);
    }

    if (inverted) {
      // pixels are stored such that white -> 0; invert the values so that
      // white -> 255 (or 65535)
      if (bpp == 1) {
        for (int i=0; i<buf.length; i++) {
          buf[i] = (byte) (255 - buf[i]);
        }
      }
      else if (bpp == 2) {
        long maxPixelValue = maxPixelRange + (centerPixelValue/2);
        if (maxPixelRange == -1 || centerPixelValue < (maxPixelRange/2)) {
          maxPixelValue = FormatTools.defaultMinMax(getPixelType())[1];
        }
        boolean little = isLittleEndian();
        for (int i=0; i<buf.length; i+=2) {
          short s = DataTools.bytesToShort(buf, i, 2, little);
          DataTools.unpackBytes(maxPixelValue - s, buf, i, 2, little);
        }
      }
    }

    // NB: do *not* apply the rescale function

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helper != null) helper.close(fileOnly);
    if (!fileOnly) {
      vr = null;
      bitsPerPixel = location = elementLength = 0;
      oddLocations = inSequence = bigEndianTransferSyntax = false;
      isJPEG = isJP2K = isRLE = isDeflate = false;
      lut = null;
      offsets = null;
      shortLut = null;
      maxPixelRange = 0;
      centerPixelValue = 0;
      rescaleSlope = 1.0;
      rescaleIntercept = 0.0;
      pixelSizeX = pixelSizeY = null;
      pixelSizeZ = null;
      imagesPerFile = 0;
      fileList = null;
      inverted = false;
      date = time = imageType = null;
      originalDate = originalTime = originalInstance = null;
      originalSeries = 0;
      originalX = 0;
      originalY = 0;
      helper = null;
      companionFiles.clear();
      positionX.clear();
      positionY.clear();
      positionZ.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(id);
    in.order(true);
    CoreMetadata m = core.get(0);

    // look for companion files
    attachCompanionFiles();

    helper = new DicomReader();
    helper.setGroupFiles(false);

    m.littleEndian = true;
    location = 0;
    isJPEG = false;
    isRLE = false;
    bigEndianTransferSyntax = false;
    oddLocations = false;
    inSequence = false;
    bitsPerPixel = 0;
    elementLength = 0;
    vr = null;
    lut = null;
    offsets = null;
    inverted = false;

    // some DICOM files have a 128 byte header followed by a 4 byte identifier

    LOGGER.info("Verifying DICOM format");
    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    in.seek(HEADER_LENGTH);
    if (in.readString(4).equals("DICM")) {
      if (level != MetadataLevel.MINIMUM) {
        // header exists, so we'll read it
        in.seek(0);
        addSeriesMeta("Header information", in.readString(HEADER_LENGTH));
        in.skipBytes(4);
      }
      location = HEADER_LENGTH;
    }
    else in.seek(0);

    LOGGER.info("Reading tags");

    long baseOffset = 0;

    boolean decodingTags = true;
    boolean signed = false;
    String currentType = "";

    while (decodingTags) {
      if (in.getFilePointer() + 4 >= in.length()) {
        break;
      }
      LOGGER.debug("Reading tag from {}", in.getFilePointer());
      int tag = getNextTag(in);

      if (elementLength <= 0) continue;

      oddLocations = (location & 1) != 0;

      LOGGER.debug("  tag={} len={} fp={}",
        new Object[] {DicomAttribute.formatTag(tag), elementLength, in.getFilePointer()});

      DicomAttribute attribute = DicomAttribute.get(tag);
      if (attribute == null) {
        long oldfp = in.getFilePointer();
        addInfo(tag, null);
        in.seek(oldfp + elementLength);
        continue;
      }

      String s = null;
      switch (attribute) {
        case TRANSFER_SYNTAX_UID:
          // this tag can indicate which compression scheme is used
          s = in.readString(elementLength);
          addInfo(tag, s);
          if (s.startsWith("1.2.840.10008.1.2.4.9")) isJP2K = true;
          else if (s.startsWith("1.2.840.10008.1.2.4")) isJPEG = true;
          else if (s.startsWith("1.2.840.10008.1.2.5")) isRLE = true;
          else if (s.equals("1.2.8.10008.1.2.1.99")) isDeflate = true;
          else if (s.indexOf("1.2.4") > -1 || s.indexOf("1.2.5") > -1) {
            throw new UnsupportedCompressionException(
              "Sorry, compression type " + s + " not supported");
          }
          if (s.indexOf("1.2.840.10008.1.2.2") >= 0) {
            bigEndianTransferSyntax = true;
          }
          break;
        case NUMBER_OF_FRAMES:
          s = in.readString(elementLength);
          addInfo(tag, s);
          double frames = Double.parseDouble(s);
          if (frames > 1.0) imagesPerFile = (int) frames;
          break;
        case SAMPLES_PER_PIXEL:
          addInfo(tag, in.readShort());
          break;
        case PLANAR_CONFIGURATION:
          int config = in.readShort();
          m.interleaved = config == 0;
          addInfo(tag, config);
          break;
        case ROWS:
          int y = in.readShort();
          if (y > getSizeY()) {
            m.sizeY = y;
            originalY = y;
          }
          addInfo(tag, getSizeY());
          break;
        case COLUMNS:
          int x = in.readShort();
          if (x > getSizeX()) {
            m.sizeX = x;
            originalX = x;
          }
          addInfo(tag, getSizeX());
          break;
        case PHOTOMETRIC_INTERPRETATION:
        case PIXEL_SPACING:
        case SLICE_SPACING:
        case RESCALE_INTERCEPT:
        case WINDOW_CENTER:
          String winCenter = in.readString(elementLength);
          if (winCenter.trim().length() == 0) centerPixelValue = -1;
          else {
            try {
              centerPixelValue = new Double(winCenter).intValue();
            }
            catch (NumberFormatException e) {
              centerPixelValue = -1;
            }
          }
          addInfo(tag, winCenter);
          break;
        case RESCALE_SLOPE:
          addInfo(tag, in.readString(elementLength));
          break;
        case BITS_ALLOCATED:
          if (bitsPerPixel == 0) bitsPerPixel = in.readShort();
          else in.skipBytes(2);
          addInfo(tag, bitsPerPixel);
          break;
        case PIXEL_REPRESENTATION:
        case PIXEL_SIGN:
          short ss = in.readShort();
          signed = ss == 1;
          addInfo(tag, ss);
          break;
        //case 537262910:
        case WINDOW_WIDTH:
          String t = in.readString(elementLength);
          if (t.trim().length() == 0) maxPixelRange = -1;
          else {
            try {
              maxPixelRange = new Double(t.trim()).intValue();
            }
            catch (NumberFormatException e) {
              maxPixelRange = -1;
            }
          }
          addInfo(tag, t);
          break;
        case PIXEL_DATA:
        case ITEM:
        case INVALID_PIXEL_DATA:
          if (elementLength != 0) {
            baseOffset = in.getFilePointer();
            addInfo(tag, location);
            decodingTags = false;
          }
          else addInfo(tag, null);
          break;
        case VARIABLE_PIXEL_DATA:
          if (elementLength != 0) {
            baseOffset = location + 4;
            decodingTags = false;
          }
          break;
        case PIXEL_DATA_LENGTH:
          in.skipBytes(elementLength);
          break;
        case INVALID:
          in.seek(in.getFilePointer() - 4);
          break;
        case DIRECTORY_RECORD_TYPE:
          currentType = getHeaderInfo(tag, s).trim();
          break;
        case REFERENCED_FILE_ID:
          if (currentType.equals("IMAGE")) {
            if (fileList == null) {
              fileList = new HashMap<Integer, List<String>>();
            }
            int seriesIndex = originalSeries;
            if (fileList.get(seriesIndex) == null) {
              fileList.put(seriesIndex, new ArrayList<String>());
            }
            fileList.get(seriesIndex).add(getHeaderInfo(tag, s).trim());
          }
          else {
            companionFiles.add(getHeaderInfo(tag, s).trim());
          }
          currentType = "";
          break;
        default:
          long oldfp = in.getFilePointer();
          addInfo(tag, s);
          in.seek(oldfp + elementLength);
      }
      if (in.getFilePointer() >= (in.length() - 4)) {
        decodingTags = false;
      }
    }
    if (imagesPerFile == 0) imagesPerFile = 1;

    if (id.endsWith("DICOMDIR")) {
      String parent = new Location(currentId).getAbsoluteFile().getParent();
      Integer[] fileKeys = fileList.keySet().toArray(new Integer[0]);
      Arrays.sort(fileKeys);
      for (int q=0; q<fileList.size(); q++) {
        for (int i=0; i<fileList.get(fileKeys[q]).size(); i++) {
          String file = fileList.get(fileKeys[q]).get(i);
          file = file.replace('\\', File.separatorChar);
          file = file.replaceAll("/", File.separator);
          fileList.get(fileKeys[q]).set(i, parent + File.separator + file);
        }
      }
      for (int i=0; i<companionFiles.size(); i++) {
        String file = companionFiles.get(i);
        file = file.replace('\\', File.separatorChar);
        file = file.replaceAll("/", File.separator);
        companionFiles.set(i, parent + File.separator + file);
      }
      companionFiles.add(new Location(currentId).getAbsolutePath());
      initFile(fileList.get(fileKeys[0]).get(0));
      return;
    }

    m.bitsPerPixel = bitsPerPixel;
    while (bitsPerPixel % 8 != 0) bitsPerPixel++;
    if (bitsPerPixel == 24 || bitsPerPixel == 48) {
      bitsPerPixel /= 3;
      m.bitsPerPixel /= 3;
    }

    m.pixelType =
      FormatTools.pixelTypeFromBytes(bitsPerPixel / 8, signed, false);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int plane = getSizeX() * getSizeY() * (lut == null ? getSizeC() : 1) * bpp;

    LOGGER.info("Calculating image offsets");

    // calculate the offset to each plane

    in.seek(baseOffset - 12);
    int len = in.readInt();
    if (len >= 0 && len + in.getFilePointer() < in.length()) {
      in.skipBytes(len);
      int check = in.readShort() & 0xffff;
      if (check == 0xfffe) {
        baseOffset = in.getFilePointer() + 2;
      }
    }

    offsets = new long[imagesPerFile];
    for (int i=0; i<imagesPerFile; i++) {
      if (isRLE) {
        if (i == 0) in.seek(baseOffset);
        else {
          in.seek(offsets[i - 1]);
          CodecOptions options = new CodecOptions();
          options.maxBytes = plane / bpp;
          for (int q=0; q<bpp; q++) {
            new PackbitsCodec().decompress(in, options);
            while (in.read() == 0);
            in.seek(in.getFilePointer() - 1);
          }
        }
        in.skipBytes(i == 0 ? 64 : 53);
        while (in.read() == 0);
        offsets[i] = in.getFilePointer() - 1;
      }
      else if (isJPEG || isJP2K) {
        // scan for next JPEG magic byte sequence
        if (i == 0) offsets[i] = baseOffset;
        else offsets[i] = offsets[i - 1] + 3;

        byte secondCheck = isJPEG ? (byte) 0xd8 : (byte) 0x4f;

        in.seek(offsets[i]);
        byte[] buf = new byte[8192];
        int n = in.read(buf);
        boolean found = false;
        while (!found) {
          for (int q=0; q<n-2; q++) {
            if (buf[q] == (byte) 0xff && buf[q + 1] == secondCheck &&
              buf[q + 2] == (byte) 0xff)
            {
              if (isJPEG || (isJP2K && buf[q + 3] == 0x51)) {
                found = true;
                offsets[i] = in.getFilePointer() + q - n;
                break;
              }
            }
          }
          if (!found) {
            for (int q=0; q<4; q++) {
              buf[q] = buf[buf.length + q - 4];
            }
            n = in.read(buf, 4, buf.length - 4) + 4;
          }
        }
      }
      else offsets[i] = baseOffset + plane*i;
    }

    makeFileList();

    LOGGER.info("Populating metadata");

    int seriesCount = fileList.size();

    Integer[] keys = fileList.keySet().toArray(new Integer[0]);
    Arrays.sort(keys);

    if (seriesCount > 1) {
      core.clear();
    }

    for (int i=0; i<seriesCount; i++) {
      if (seriesCount == 1) {
        CoreMetadata ms = core.get(i);
        ms.sizeZ = imagesPerFile * fileList.get(keys[i]).size();
        if (ms.sizeC == 0) ms.sizeC = 1;
        ms.rgb = ms.sizeC > 1;
        ms.sizeT = 1;
        ms.dimensionOrder = "XYCZT";
        ms.metadataComplete = true;
        ms.falseColor = false;
        if (isRLE) core.get(i).interleaved = false;
        ms.imageCount = ms.sizeZ;
      }
      else {
        helper.close();
        helper.setId(fileList.get(keys[i]).get(0));
        CoreMetadata ms = helper.getCoreMetadataList().get(0);
        ms.sizeZ *= fileList.get(keys[i]).size();
        ms.imageCount = ms.sizeZ;
        core.add(ms);
      }
    }

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String stamp = null;

    if (date != null && time != null) {
      stamp = date + " " + time;
      stamp = DateTools.formatDate(stamp, "yyyy.MM.dd HH:mm:ss", ".");
    }

    if (stamp == null || stamp.trim().equals("")) stamp = null;

    for (int i=0; i<core.size(); i++) {
      if (stamp != null) store.setImageAcquisitionDate(new Timestamp(stamp), i);
      store.setImageName("Series " + i, i);
    }

    if (level != MetadataLevel.MINIMUM) {
      for (int i=0; i<core.size(); i++) {
        store.setImageDescription(imageType, i);

        // all physical sizes were stored in mm, so must be converted to um
        if (pixelSizeX != null) {
          Length x = FormatTools.getPhysicalSizeX(new Double(pixelSizeX), UNITS.MILLIMETER);
          if (x != null) {
            store.setPixelsPhysicalSizeX(x, i);
          }
        }
        if (pixelSizeY != null) {
          Length y = FormatTools.getPhysicalSizeY(new Double(pixelSizeY), UNITS.MILLIMETER);
          if (y != null) {
            store.setPixelsPhysicalSizeY(y, i);
          }
        }
        if (pixelSizeZ != null) {
          Length z = FormatTools.getPhysicalSizeZ(new Double(pixelSizeZ), UNITS.MILLIMETER);
          if (z != null) {
            store.setPixelsPhysicalSizeZ(z, i);
          }
        }

        for (int p=0; p<getImageCount(); p++) {
          if (p < positionX.size()) {
            if (positionX.get(p) != null) {
              Length x = new Length(positionX.get(p), UNITS.MILLIMETER);
              if (x != null) {
                store.setPlanePositionX(x, 0, p);
              }
            }
          }
          if (p < positionY.size()) {
            if (positionY.get(p) != null) {
              Length y = new Length(positionY.get(p), UNITS.MILLIMETER);
              if (y != null) {
                store.setPlanePositionY(y, 0, p);
              }
            }
          }
          if (p < positionZ.size()) {
            if (positionZ.get(p) != null) {
              Length z = new Length(positionZ.get(p), UNITS.MILLIMETER);
              if (z != null) {
                store.setPlanePositionZ(z, 0, p);
              }
            }
          }
        }
      }
    }
  }

  // -- Helper methods --

  private void addInfo(int tag, String value) throws IOException {
    String oldValue = value;
    String info = getHeaderInfo(tag, value);
    CoreMetadata m = core.get(0);

    if (info != null && tag != ITEM.getTag()) {
      info = info.trim();
      if (info.equals("")) info = oldValue == null ? "" : oldValue.trim();

      DicomAttribute attribute = DicomAttribute.get(tag);
      String key = null;
      if (attribute != null) {
        key = attribute.getDescription();
      }
      if (key == null) {
        key = DicomAttribute.formatTag(tag);
      }
      if (attribute != null) {
        switch (attribute) {
          case SAMPLES_PER_PIXEL:
            m.sizeC = Integer.parseInt(info);
            if (getSizeC() > 1) m.rgb = true;
            break;
          case PHOTOMETRIC_INTERPRETATION:
            if (info.equals("PALETTE COLOR")) {
              m.indexed = true;
              m.sizeC = 1;
              m.rgb = false;
              lut = new byte[3][];
              shortLut = new short[3][];
            }
            else if (info.startsWith("MONOCHROME")) {
              inverted = info.endsWith("1");
            }
            break;
          case ACQUISITION_DATE:
            originalDate = info;
            break;
          case ACQUISITION_TIME:
            originalTime = info;
            break;
          case INSTANCE_NUMBER:
            if (info.trim().length() > 0) {
              originalInstance = info;
            }
            break;
          case SERIES_NUMBER: 
            originalSeries = parseIntValue(info, 0);
            break;
          case RED_LUT_DATA:
          case GREEN_LUT_DATA:
          case BLUE_LUT_DATA:
          case SEGMENTED_RED_LUT_DATA:
          case SEGMENTED_GREEN_LUT_DATA:
          case SEGMENTED_BLUE_LUT_DATA:
            String color = key.substring(0, key.indexOf(' ')).trim();
            int ndx = color.equals("Red") ? 0 : color.equals("Green") ? 1 : 2;
            long fp = in.getFilePointer();
            in.seek(in.getFilePointer() - elementLength + 1);
            shortLut[ndx] = new short[elementLength / 2];
            lut[ndx] = new byte[elementLength / 2];
            for (int i=0; i<lut[ndx].length; i++) {
              shortLut[ndx][i] = in.readShort();
              lut[ndx][i] = (byte) (shortLut[ndx][i] & 0xff);
            }
            in.seek(fp);
            break;
          case CONTENT_TIME:
            time = info;
            break;
          case CONTENT_DATE:
            date = info;
            break;
          case IMAGE_TYPE:
            imageType = info;
            break;
          case RESCALE_INTERCEPT:
            rescaleIntercept = Double.parseDouble(info);
            break;
          case RESCALE_SLOPE:
            rescaleSlope = Double.parseDouble(info);
            break;
          case PIXEL_SPACING:
            pixelSizeY = info.substring(0, info.indexOf("\\"));
            pixelSizeX = info.substring(info.lastIndexOf("\\") + 1);
            break;
          case SLICE_SPACING:
            pixelSizeZ = new Double(info);
            break;
          case IMAGE_POSITION_PATIENT:
            String[] positions = info.replace('\\', '_').split("_");
            if (positions.length > 0) {
              try {
                positionX.add(Double.valueOf(positions[0]));
              }
              catch (NumberFormatException e) {
                positionX.add(null);
              }
            }
            else {
              positionX.add(null);
              positionY.add(null);
              positionZ.add(null);
            }
            if (positions.length > 1) {
              try {
                positionY.add(Double.valueOf(positions[1]));
              }
              catch (NumberFormatException e) {
                positionY.add(null);
              }
            }
            else {
              positionY.add(null);
              positionZ.add(null);
            }
            if (positions.length > 2) {
              try {
                positionZ.add(Double.valueOf(positions[2]));
              }
              catch (NumberFormatException e) {
                positionZ.add(null);
              }
            }
            else {
              positionZ.add(null);
            }
            break;
        }
      }

      if (((tag & 0xffff0000) >> 16) != 0x7fe0) {
        key = DicomAttribute.formatTag(tag) + " " + key;
        if (metadata.containsKey(key)) {
          // make sure that values are not overwritten
          Object v = getMetadataValue(key);
          metadata.remove(key);
          addSeriesMetaList(key, v);
          addSeriesMetaList(key, info);
        }
        else {
          addSeriesMetaList(key, info);
        }
      }
    }
  }

  private void addInfo(int tag, int value) throws IOException {
    addInfo(tag, Integer.toString(value));
  }

  private String getHeaderInfo(int tag, String value) throws IOException {
    DicomAttribute attribute = DicomAttribute.get(tag);
    String id = null;

    if (attribute != null) {
      if (attribute == ITEM_DELIMITATION_ITEM || attribute == SEQUENCE_DELIMITATION_ITEM) {
        inSequence = false;
      }

      id = attribute.getDescription();

      if (vr == IMPLICIT && id != null) {
        vr = DicomVR.get((id.charAt(0) << 8) + id.charAt(1));
      }
      if (id.length() > 2) id = id.substring(2);
    }

    if (attribute == ITEM) return id;
    if (value != null) return value;

    boolean skip = vr == null;
    LOGGER.debug("    vr = {}", vr);
    if (vr != null) {
      switch (vr) {
        case AE:
        case AS:
        case AT:
        case CS:
        case DA:
        case DS:
        case DT:
        case IS:
        case LO:
        case LT:
        case PN:
        case SH:
        case ST:
        case TM:
        case UI:
          value = in.readString(elementLength);
          break;
        case US:
          if (elementLength == 2) value = Integer.toString(in.readShort());
          else {
            StringBuilder sb = new StringBuilder();
            int n = elementLength / 2;
            for (int i=0; i<n; i++) {
              sb.append(in.readShort());
              sb.append(" ");
            }
            value = sb.toString();
          }
          break;
        case IMPLICIT:
          value = in.readString(elementLength);
          if (elementLength <= 4 || elementLength > 44) value = null;
          break;
        case SQ:
          value = "";
          boolean privateTag = ((tag >> 16) & 1) != 0;
          if (attribute == ICON_IMAGE_SEQUENCE || privateTag) skip = true;
          break;
        default:
          skip = true;
      }
    }
    if (skip) {
      long skipCount = (long) elementLength;
      if (in.getFilePointer() + skipCount <= in.length()) {
        in.skipBytes(skipCount);
      }
      location += elementLength;
      value = "";
    }

    if (value != null && id == null && !value.isEmpty()) {
      return value;
    }
    else if (id == null) {
      return null;
    }
    else {
      return value;
    }
  }

  private int getLength(RandomAccessInputStream stream, int tag)
    throws IOException
  {
    DicomAttribute attribute = DicomAttribute.get(tag);
    byte[] b = new byte[4];
    stream.read(b);

    // We cannot know whether the VR is implicit or explicit
    // without the full DICOM Data Dictionary for public and
    // private groups.

    // We will assume the VR is explicit if the two bytes
    // match the known codes. It is possible that these two
    // bytes are part of a 32-bit length for an implicit VR.

    // see http://dicom.nema.org/medical/dicom/current/output/html/part05.html#sect_7.1.2

    vr = DicomVR.get(((b[0] & 0xff) << 8) | (b[1] & 0xff));

    if (vr == null) {
      vr = IMPLICIT;
      int len = DataTools.bytesToInt(b, stream.isLittleEndian());
      if (len + stream.getFilePointer() > stream.length() || len < 0) {
        len = DataTools.bytesToInt(b, 2, 2, stream.isLittleEndian());
        len &= 0xffff;
      }
      return len;
    }

    switch (vr) {
      case OB:
      case OW:
      case SQ:
      case UN:
      case UT:
        // Explicit VR with 32-bit length if other two bytes are zero
        if ((b[2] == 0) || (b[3] == 0)) {
          return stream.readInt();
        }
        vr = IMPLICIT;
        return DataTools.bytesToInt(b, stream.isLittleEndian());
      case AE:
      case AS:
      case AT:
      case CS:
      case DA:
      case DS:
      case DT:
      case FD:
      case FL:
      case IS:
      case LO:
      case LT:
      case PN:
      case SH:
      case SL:
      case SS:
      case ST:
      case TM:
      case UI:
      case UL:
      case US:
      case QQ:
        // Explicit VR with 16-bit length
        if (attribute == LUT_DATA) {
          return DataTools.bytesToInt(b, 2, 2, stream.isLittleEndian());
        }
        int n1 = DataTools.bytesToShort(b, 2, 2, stream.isLittleEndian());
        int n2 = DataTools.bytesToShort(b, 2, 2, !stream.isLittleEndian());
        n1 &= 0xffff;
        n2 &= 0xffff;
        if (n1 < 0 || n1 + stream.getFilePointer() > stream.length()) return n2;
        if (n2 < 0 || n2 + stream.getFilePointer() > stream.length()) return n1;
        return n1;
      case RESERVED:
        vr = IMPLICIT;
        return 8;
      default:
        throw new IllegalArgumentException(vr.toString());
    }
  }

  private int getNextTag(RandomAccessInputStream stream)
    throws FormatException, IOException
  {
    return getNextTag(stream, true);
  }

  private int getNextTag(RandomAccessInputStream stream, boolean setMetadata)
    throws FormatException, IOException
  {
    long fp = stream.getFilePointer();
    if (fp >= stream.length() - 2) {
      return 0;
    }
    int groupWord = stream.readShort() & 0xffff;
    if (groupWord == 0x0800 && bigEndianTransferSyntax) {
      if (setMetadata) {
        core.get(0).littleEndian = false;
      }
      groupWord = 0x0008;
      stream.order(false);
    }
    else if (groupWord == 0xfeff || groupWord == 0xfffe) {
      stream.skipBytes(6);
      return getNextTag(stream, setMetadata);
    }

    int elementWord = stream.readShort();
    int tag = ((groupWord << 16) & 0xffff0000) | (elementWord & 0xffff);

    elementLength = getLength(stream, tag);
    if (elementLength > stream.length()) {
      stream.seek(fp);

      stream.order(!core.get(0).littleEndian);
      if (setMetadata) {
        core.get(0).littleEndian = !core.get(0).littleEndian;
      }

      groupWord = stream.readShort() & 0xffff;
      elementWord = stream.readShort();
      tag = ((groupWord << 16) & 0xffff0000) | (elementWord & 0xffff);
      elementLength = getLength(stream, tag);

      if (elementLength > stream.length()) {
        throw new FormatException("Invalid tag length " + elementLength);
      }
      return tag;
    }

    if (elementLength < 0 && groupWord == 0x7fe0) {
      stream.skipBytes(12);
      elementLength = stream.readInt();
      if (elementLength < 0) elementLength = stream.readInt();
    }

    if (elementLength == 0 && (groupWord == 0x7fe0 || tag == 0x291014)) {
      elementLength = getLength(stream, tag);
    }
    else if (elementLength == 0) {
      stream.seek(stream.getFilePointer() - 4);
      DicomVR v = DicomVR.get(stream.readShort() & 0xffff);
      if (v == UT) {
        stream.skipBytes(2);
        elementLength = stream.readInt();
      }
      else stream.skipBytes(2);
    }

    // HACK - needed to read some GE files
    // The element length must be even!
    if (!oddLocations && (elementLength % 2) == 1) elementLength++;

    // "Undefined" element length.
    // This is a sort of bracket that encloses a sequence of elements.
    DicomAttribute attribute = DicomAttribute.get(tag); 
    if (elementLength == -1 || (attribute != null && attribute != SCANNING_SEQUENCE &&
      attribute.getDescription().endsWith("Sequence")))
    {
      elementLength = 0;
      inSequence = true;
    }
    return tag;
  }

  private void makeFileList() throws FormatException, IOException {
    LOGGER.info("Building file list");

    if (fileList == null && originalInstance != null && originalDate != null &&
      originalTime != null && isGroupFiles())
    {
      currentId = new Location(currentId).getAbsolutePath();
      fileList = new HashMap<Integer, List<String>>();
      final Integer s = originalSeries;
      fileList.put(s, new ArrayList<String>());

      int instanceNumber = Integer.parseInt(originalInstance) - 1;
      if (instanceNumber == 0) fileList.get(s).add(currentId);
      else {
        while (instanceNumber > fileList.get(s).size()) {
          fileList.get(s).add(null);
        }
        fileList.get(s).add(currentId);
      }

      Location currentFile = new Location(currentId).getAbsoluteFile();
      Location directory = currentFile.getParentFile();

      scanDirectory(directory, true);

      for (final List<String> files : fileList.values()) {
        final Iterator<String> fileIterator = files.iterator();
        while (fileIterator.hasNext()) {
          if (fileIterator.next() == null) {
            fileIterator.remove();
          }
        }
      }
    }
    else if (fileList == null || !isGroupFiles()) {
      fileList = new HashMap<Integer, List<String>>();
      fileList.put(0, new ArrayList<String>());
      fileList.get(0).add(currentId);
    }
  }

  // -- Utility methods --

  /**
   * Scan the given directory for files that belong to this dataset.
   */
  private void scanDirectory(Location dir, boolean checkSeries)
    throws FormatException, IOException
  {
    Location currentFile = new Location(currentId).getAbsoluteFile();
    FilePattern pattern =
      new FilePattern(currentFile.getName(), dir.getAbsolutePath());
    String[] patternFiles = pattern.getFiles();
    if (patternFiles == null) patternFiles = new String[0];
    Arrays.sort(patternFiles);

    // make sure that the file names are normalized
    // this prevents files from being missed on Windows if the
    // path separator normalization is inconsistent
    for (int i=0; i<patternFiles.length; i++) {
      patternFiles[i] = new Location(patternFiles[i]).getAbsolutePath();
    }

    String[] files = dir.list(true);
    if (files == null) return;
    Arrays.sort(files);
    for (String f : files) {
      String file = new Location(dir, f).getAbsolutePath();
      LOGGER.debug("Checking file {}", file);
      if (!f.equals(currentId) && !file.equals(currentId) &&
        isThisType(file) && Arrays.binarySearch(patternFiles, file) >= 0)
      {
        addFileToList(file, checkSeries);
      }
    }
  }

  /**
   * Determine if the given file belongs in the same dataset as this file.
   */
  private void addFileToList(String file, boolean checkSeries)
    throws FormatException, IOException
  {
    int currentX = 0, currentY = 0;
    int fileSeries = -1;

    String date = null, time = null, instance = null;
    try (RandomAccessInputStream stream = new RandomAccessInputStream(file)) {
      if (!isThisType(stream)) {
        return;
      }
      stream.order(true);
      stream.seek(HEADER_LENGTH);
      if (!stream.readString(4).equals("DICM")) {
        stream.seek(0);
      }

      while (date == null || time == null || instance == null ||
        (checkSeries && fileSeries < 0) || currentX == 0 || currentY == 0)
      {
        long fp = stream.getFilePointer();
        if (fp + 4 >= stream.length() || fp < 0) break;
        int tag = getNextTag(stream);

        DicomAttribute attribute = DicomAttribute.get(tag);
        if (attribute == null) {
          stream.skipBytes(elementLength);
          continue;
        }

        switch (attribute) {
          case INSTANCE_NUMBER:
            instance = stream.readString(elementLength).trim();
            if (instance.length() == 0) instance = null;
            break;
          case ACQUISITION_TIME:
            time = stream.readString(elementLength);
            break;
          case ACQUISITION_DATE:
            date = stream.readString(elementLength);
            break;
          case SERIES_NUMBER:
            fileSeries = Integer.parseInt(stream.readString(elementLength).trim());
            break;
          case ROWS:
            int y = stream.readShort();
            if (y > currentY) {
              currentY = y;
            }
            break;
          case COLUMNS:
            int x = stream.readShort();
            if (x > currentX) {
              currentX = x;
            }
            break;
          default:
            stream.skipBytes(elementLength);
        }
      }
    }

    LOGGER.trace("  date = {}, originalDate = {}", date, originalDate);
    LOGGER.trace("  time = {}, originalTime = {}", time, originalTime);
    LOGGER.trace("  instance = {}, originalInstance = {}", instance, originalInstance);
    LOGGER.trace("  checkSeries = {}", checkSeries);
    LOGGER.trace("  fileSeries = {}, originalSeries = {}", fileSeries, originalSeries);
    LOGGER.trace("  currentX = {}, originalX = {}", currentX, originalX);
    LOGGER.trace("  currentY = {}, originalY = {}", currentY, originalY);

    if (date == null || time == null || instance == null ||
      (checkSeries && fileSeries != originalSeries))
    {
      return;
    }

    if (currentX != originalX || currentY != originalY) {
      fileSeries++;
    }

    int stamp = parseIntValue(time, 0);
    int timestamp = parseIntValue(originalTime, 0);

    LOGGER.trace("  stamp = {}", stamp);
    LOGGER.trace("  timestamp = {}", timestamp);

    if (date.equals(originalDate) && (Math.abs(stamp - timestamp) < 150)) {
      int position = Integer.parseInt(instance) - 1;
      if (position < 0) position = 0;
      if (fileList.get(fileSeries) == null) {
        fileList.put(fileSeries, new ArrayList<String>());
      }
      if (position < fileList.get(fileSeries).size()) {
        while (position < fileList.get(fileSeries).size() &&
          fileList.get(fileSeries).get(position) != null)
        {
          position++;
        }
        if (position < fileList.get(fileSeries).size()) {
          fileList.get(fileSeries).set(position, file);
        }
        else if (!fileList.get(fileSeries).contains(file)) {
          fileList.get(fileSeries).add(file);
        }
      }
      else if (!fileList.get(fileSeries).contains(file)) {
        while (position > fileList.get(fileSeries).size()) {
          fileList.get(fileSeries).add(null);
        }
        fileList.get(fileSeries).add(file);
      }
    }
  }

  private int parseIntValue(String v, int defaultValue) {
    Integer parsedValue = DataTools.parseInteger(v);
    return parsedValue == null ? defaultValue : parsedValue;
  }

  /**
   * DICOM datasets produced by:
   * http://www.ct-imaging.de/index.php/en/ct-systeme-e/mikro-ct-e.html
   * contain a bunch of extra metadata and log files.
   *
   * We do not parse these extra files, but do locate and attach them to the
   * DICOM file(s).
   */
  private void attachCompanionFiles() throws IOException {
    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    Location grandparent = parent.getParentFile();

    if (new Location(grandparent, parent.getName() + ".mif").exists()) {
      String[] list = grandparent.list(true);
      for (String f : list) {
        Location file = new Location(grandparent, f);
        if (!file.isDirectory()) {
          companionFiles.add(file.getAbsolutePath());
        }
      }
    }
  }

}
