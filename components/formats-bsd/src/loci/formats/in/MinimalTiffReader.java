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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.SubResolutionFormatReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;

/**
 * MinimalTiffReader is the superclass for file format readers compatible with
 * or derived from the TIFF 6.0 file format.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MinimalTiffReader extends SubResolutionFormatReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(MinimalTiffReader.class);

  // -- Fields --

  /** List of IFDs for the current TIFF. */
  protected IFDList ifds;

  /** List of thumbnail IFDs for the current TIFF. */
  protected IFDList thumbnailIFDs;

  /**
   * List of sub-resolution IFDs for each IFD in the current TIFF with the
   * same order as <code>ifds</code>.
   */
  protected List<IFDList> subResolutionIFDs;

  protected transient TiffParser tiffParser;

  protected boolean equalStrips = false;

  protected boolean use64Bit = false;

  protected int lastPlane = 0;

  protected boolean noSubresolutions = false;

  protected boolean seriesToIFD = false;

  /** Number of JPEG 2000 resolution levels. */
  private Integer resolutionLevels;

  /** Codec options to use when decoding JPEG 2000 data. */
  private JPEG2000CodecOptions j2kCodecOptions;

  // -- Constructors --

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader() {
    this("Minimal TIFF", new String[] {"tif", "tiff", "tf2", "tf8", "btf"});
  }

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader(String name, String suffix) {
    this(name, new String[] {suffix});
  }

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader(String name, String[] suffixes) {
    super(name, suffixes);
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- MinimalTiffReader API methods --

  /** Gets the list of IFDs associated with the current TIFF's image planes. */
  public IFDList getIFDs() {
    return ifds;
  }

  /** Gets the list of IFDs associated with the current TIFF's thumbnails. */
  public IFDList getThumbnailIFDs() {
    return thumbnailIFDs;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return new TiffParser(stream).isValidHeader();
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (ifds == null || lastPlane < 0 || lastPlane >= ifds.size()) return null;
    IFD lastIFD = ifds.get(lastPlane);
    int[] bits = lastIFD.getBitsPerSample();
    if (bits[0] <= 8) {
      int[] colorMap = tiffParser.getColorMap(lastIFD);
      if (colorMap == null) {
        // it's possible that the LUT is only present in the first IFD
        if (lastPlane != 0) {
          lastIFD = ifds.get(0);
          colorMap = tiffParser.getColorMap(lastIFD);
          if (colorMap == null) return null;
        }
        else return null;
      }

      byte[][] table = new byte[3][colorMap.length / 3];
      int next = 0;
      for (int j=0; j<table.length; j++) {
        for (int i=0; i<table[0].length; i++) {
          if (colorMap[next] > 255) {
            table[j][i] = (byte) ((colorMap[next++] >> 8) & 0xff);
          }
          else {
            table[j][i] = (byte) (colorMap[next++] & 0xff);
          }
        }
      }

      return table;
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (ifds == null || lastPlane < 0 || lastPlane >= ifds.size()) return null;
    IFD lastIFD = ifds.get(lastPlane);
    int[] bits = lastIFD.getBitsPerSample();
    if (bits[0] <= 16 && bits[0] > 8) {
      int[] colorMap = tiffParser.getColorMap(lastIFD);
      if (colorMap == null || colorMap.length < 65536 * 3) {
        // it's possible that the LUT is only present in the first IFD
        if (lastPlane != 0) {
          lastIFD = ifds.get(0);
          colorMap = tiffParser.getColorMap(lastIFD);
          if (colorMap == null || colorMap.length < 65536 * 3) return null;
        }
        else return null;
      }

      short[][] table = new short[3][colorMap.length / 3];
      int next = 0;
      for (int i=0; i<table.length; i++) {
        for (int j=0; j<table[0].length; j++) {
          table[i][j] = (short) (colorMap[next++] & 0xffff);
        }
      }
      return table;
    }
    return null;
  }

  /* @see loci.formats.FormatReader#getThumbSizeX() */
  @Override
  public int getThumbSizeX() {
    if (thumbnailIFDs != null && thumbnailIFDs.size() > 0) {
      try {
        return (int) thumbnailIFDs.get(0).getImageWidth();
      }
      catch (FormatException e) {
        LOGGER.debug("Could not retrieve thumbnail width", e);
      }
    }
    return super.getThumbSizeX();
  }

  /* @see loci.formats.FormatReader#getThumbSizeY() */
  @Override
  public int getThumbSizeY() {
    if (thumbnailIFDs != null && thumbnailIFDs.size() > 0) {
      try {
        return (int) thumbnailIFDs.get(0).getImageLength();
      }
      catch (FormatException e) {
        LOGGER.debug("Could not retrieve thumbnail height", e);
      }
    }
    return super.getThumbSizeY();
  }

  /* @see loci.formats.FormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (thumbnailIFDs == null || thumbnailIFDs.size() <= no) {
      return super.openThumbBytes(no);
    }
    if (tiffParser == null) {
      initTiffParser();
    }
    tiffParser.fillInIFD(thumbnailIFDs.get(no));
    int[] bps = null;
    try {
      bps = thumbnailIFDs.get(no).getBitsPerSample();
    }
    catch (FormatException e) { }

    if (bps == null) {
      return super.openThumbBytes(no);
    }

    int b = bps[0];
    while ((b % 8) != 0) b++;
    b /= 8;
    if (b != FormatTools.getBytesPerPixel(getPixelType()) ||
      bps.length != getRGBChannelCount())
    {
      return super.openThumbBytes(no);
    }

    byte[] buf = new byte[getThumbSizeX() * getThumbSizeY() *
      getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType())];
    return tiffParser.getSamples(thumbnailIFDs.get(no), buf);
  }

  /**
   * @see loci.formats.FormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    IFD firstIFD = ifds.get(0);
    lastPlane = no;
    IFD ifd;
    if (seriesToIFD) {
      ifd = ifds.get(getSeries());
    } else {
      ifd = ifds.get(no);
    }
    if ((firstIFD.getCompression() == TiffCompression.JPEG_2000
        || firstIFD.getCompression() == TiffCompression.JPEG_2000_LOSSY)
        && resolutionLevels != null) {
      if (getCoreIndex() > 0) {
        ifd = subResolutionIFDs.get(no).get(getCoreIndex() - 1);
      }
      setResolutionLevel(ifd);
    }

    if (tiffParser == null) {
      initTiffParser();
    }

    tiffParser.getSamples(ifd, buf, x, y, w, h);

    boolean float16 = getPixelType() == FormatTools.FLOAT &&
      firstIFD.getBitsPerSample()[0] == 16;
    boolean float24 = getPixelType() == FormatTools.FLOAT &&
      firstIFD.getBitsPerSample()[0] == 24;

    if (float16 || float24) {
      int nPixels = w * h * getRGBChannelCount();
      int nBytes = float16 ? 2 : 3;
      int mantissaBits = float16 ? 10 : 16;
      int exponentBits = float16 ? 5 : 7;
      int maxExponent = (int) Math.pow(2, exponentBits) - 1;
      int bits = (nBytes * 8) - 1;

      byte[] newBuf = new byte[buf.length];
      for (int i=0; i<nPixels; i++) {
        int v =
          DataTools.bytesToInt(buf, i * nBytes, nBytes, isLittleEndian());
        int sign = v >> bits;
        int exponent =
          (v >> mantissaBits) & (int) (Math.pow(2, exponentBits) - 1);
        int mantissa = v & (int) (Math.pow(2, mantissaBits) - 1);

        if (exponent == 0) {
          if (mantissa != 0) {
            while ((mantissa & (int) Math.pow(2, mantissaBits)) == 0) {
              mantissa <<= 1;
              exponent--;
            }
            exponent++;
            mantissa &= (int) (Math.pow(2, mantissaBits) - 1);
            exponent += 127 - (Math.pow(2, exponentBits - 1) - 1);
          }
        }
        else if (exponent == maxExponent) {
          exponent = 255;
        }
        else {
          exponent += 127 - (Math.pow(2, exponentBits - 1) - 1);
        }

        mantissa <<= (23 - mantissaBits);

        int value = (sign << 31) | (exponent << 23) | mantissa;
        DataTools.unpackBytes(value, newBuf, i * 4, 4, isLittleEndian());
      }
      System.arraycopy(newBuf, 0, buf, 0, newBuf.length);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    initTiffParser();
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (ifds != null) {
        for (IFD ifd : ifds) {
          try {
            if (ifd.getOnDemandStripOffsets() != null) {
              ifd.getOnDemandStripOffsets().close();
            }
          }
          catch (FormatException e) {
            LOGGER.debug("", e);
          }
        }
      }
      ifds = null;
      thumbnailIFDs = null;
      subResolutionIFDs = null;
      lastPlane = 0;
      tiffParser = null;
      resolutionLevels = null;
      j2kCodecOptions = null;
      seriesToIFD = false;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(0).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      int height = (int) ifds.get(0).getTileLength();
      if (height <= 0) {
        height = getSizeY();
      }

      // Some TIFF files only store a single tile, even if the image is
      // very, very large.  In those cases, we don't want to open the whole
      // tile if we can avoid it.
      if (DataTools.safeMultiply32(height, getOptimalTileWidth()) >
        10 * 1024 * 1024)
      {
        return super.getOptimalTileHeight();
      }
      if (height > 1) {
        return height;
      }
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id, 16);
    initTiffParser();
    Boolean littleEndian = tiffParser.checkHeader();
    if (littleEndian == null) {
      throw new FormatException("Invalid TIFF file: " + id);
    }
    boolean little = littleEndian.booleanValue();
    in.order(little);

    LOGGER.info("Reading IFDs");

    IFDList allIFDs = tiffParser.getIFDs();

    if (allIFDs == null || allIFDs.size() == 0) {
      throw new FormatException("No IFDs found");
    }

    ifds = new IFDList();
    thumbnailIFDs = new IFDList();
    subResolutionIFDs = new ArrayList<IFDList>();
    for (IFD ifd : allIFDs) {
      tiffParser.fillInIFD(ifd);
      Number subfile = (Number) ifd.getIFDValue(IFD.NEW_SUBFILE_TYPE);
      int subfileType = subfile == null ? 0 : subfile.intValue();
      if (subfileType != 1 || allIFDs.size() <= 1) {
        ifds.add(ifd);
      }
      else if (subfileType == 1) {
        thumbnailIFDs.add(ifd);
      }
    }

    LOGGER.info("Populating metadata");

    CoreMetadata ms0 = core.get(0, 0);

    ms0.imageCount = ifds.size();

    tiffParser.setAssumeEqualStrips(equalStrips);
    for (IFD ifd : ifds) {
      if ((ifd.getCompression() == TiffCompression.JPEG_2000
          || ifd.getCompression() == TiffCompression.JPEG_2000_LOSSY) &&
          ifd.getImageWidth() == ifds.get(0).getImageWidth()) {
        LOGGER.debug("Found IFD with JPEG 2000 compression");
        long[] stripOffsets = ifd.getStripOffsets();
        long[] stripByteCounts = ifd.getStripByteCounts();

        if (stripOffsets.length > 0) {
          long stripOffset = stripOffsets[0];
          in.seek(stripOffset);
          JPEG2000MetadataParser metadataParser =
            new JPEG2000MetadataParser(in, stripOffset + stripByteCounts[0]);
          resolutionLevels = metadataParser.getResolutionLevels();
          if (resolutionLevels != null && !noSubresolutions) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug(String.format(
                  "Original resolution IFD Levels %d %dx%d Tile %dx%d",
                  resolutionLevels, ifd.getImageWidth(), ifd.getImageLength(),
                  ifd.getTileWidth(), ifd.getTileLength()));
            }
            IFDList theseSubResolutionIFDs = new IFDList();
            subResolutionIFDs.add(theseSubResolutionIFDs);
            for (int level = 1; level <= resolutionLevels; level++) {
              IFD newIFD = new IFD(ifd);
              long imageWidth = ifd.getImageWidth();
              long imageLength = ifd.getImageLength();
              long tileWidth = ifd.getTileWidth();
              long tileLength = ifd.getTileLength();
              long factor = (long) Math.pow(2, level);
              long newTileWidth = Math.round((double) tileWidth / factor);
              newTileWidth = newTileWidth < 1? 1 : newTileWidth;
              long newTileLength = Math.round((double) tileLength / factor);
              newTileLength = newTileLength < 1? 1 : newTileLength;
              long evenTilesPerRow = imageWidth / tileWidth;
              long evenTilesPerColumn = imageLength / tileLength;
              double remainingWidth =
                  ((double) (imageWidth - (evenTilesPerRow * tileWidth))) /
                  factor;
              remainingWidth = remainingWidth < 1? Math.ceil(remainingWidth) :
                  Math.round(remainingWidth);
              double remainingLength =
                  ((double) (imageLength - (evenTilesPerColumn * tileLength))) /
                  factor;
              remainingLength =
                remainingLength < 1? Math.ceil(remainingLength) :
                Math.round(remainingLength);
              long newImageWidth = (long) ((evenTilesPerRow * newTileWidth) +
                  remainingWidth);
              long newImageLength =
                (long) ((evenTilesPerColumn * newTileLength) + remainingLength);

              int resolutionLevel = Math.abs(level - resolutionLevels);
              newIFD.put(IFD.IMAGE_WIDTH, newImageWidth);
              newIFD.put(IFD.IMAGE_LENGTH, newImageLength);
              newIFD.put(IFD.TILE_WIDTH, newTileWidth);
              newIFD.put(IFD.TILE_LENGTH, newTileLength);
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format(
                    "Added JPEG 2000 sub-resolution IFD Level %d %dx%d " +
                    "Tile %dx%d", resolutionLevel, newImageWidth,
                    newImageLength, newTileWidth, newTileLength));
              }
              theseSubResolutionIFDs.add(newIFD);
            }
          }
        }
        else {
          LOGGER.warn("IFD has no strip offsets!");
        }
      }
    }

    IFD firstIFD = ifds.get(0);

    PhotoInterp photo = firstIFD.getPhotometricInterpretation();
    int samples = firstIFD.getSamplesPerPixel();
    ms0.rgb = samples > 1 || photo == PhotoInterp.RGB;
    ms0.interleaved = false;
    ms0.littleEndian = firstIFD.isLittleEndian();

    ms0.sizeX = (int) firstIFD.getImageWidth();
    ms0.sizeY = (int) firstIFD.getImageLength();
    ms0.sizeZ = 1;
    ms0.sizeC = isRGB() ? samples : 1;
    ms0.sizeT = ifds.size();
    ms0.pixelType = firstIFD.getPixelType();
    ms0.metadataComplete = true;
    ms0.indexed = photo == PhotoInterp.RGB_PALETTE &&
      (get8BitLookupTable() != null || get16BitLookupTable() != null);
    if (isIndexed()) {
      ms0.sizeC = 1;
      ms0.rgb = false;
      for (IFD ifd : ifds) {
        ifd.putIFDValue(IFD.PHOTOMETRIC_INTERPRETATION,
          PhotoInterp.RGB_PALETTE);
      }
    }
    if (getSizeC() == 1 && !isIndexed()) ms0.rgb = false;
    ms0.dimensionOrder = "XYCZT";
    ms0.bitsPerPixel = firstIFD.getBitsPerSample()[0];

    // New core metadata now that we know how many sub-resolutions we have.
    if (resolutionLevels != null && subResolutionIFDs.size() > 0) {
      IFDList ifds = subResolutionIFDs.get(0);
      int seriesCount = ifds.size() + 1;
      if (!hasFlattenedResolutions()) {
        ms0.resolutionCount = seriesCount;
      }

      ms0.sizeT = subResolutionIFDs.size();
      ms0.imageCount = ms0.sizeT;

      if (ms0.sizeT <= 0) {
        ms0.sizeT = 1;
      }
      if (ms0.imageCount <= 0) {
        ms0.imageCount = 1;
      }

      for (IFD ifd : ifds) {
        CoreMetadata ms =  new CoreMetadata(this, 0);
        core.add(0, ms);
        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeT = ms0.sizeT;
        ms.imageCount = ms0.imageCount;
        ms.thumbnail = true;
        ms.resolutionCount = 1;
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  /**
   * Sets the resolution level when we have JPEG 2000 compressed data.
   * @param ifd The active IFD that is being used in our current
   * <code>openBytes()</code> calling context. It will be the sub-resolution
   * IFD if <code>currentSeries > 0</code>.
   */
  protected void setResolutionLevel(IFD ifd) {
    if (tiffParser == null) {
      initTiffParser();
    }
    if (j2kCodecOptions == null) {
      j2kCodecOptions = new JPEG2000CodecOptions();
    }
    j2kCodecOptions.resolution = Math.abs(getCoreIndex() - resolutionLevels);
    LOGGER.debug("Using JPEG 2000 resolution level {}",
        j2kCodecOptions.resolution);
    tiffParser.setCodecOptions(j2kCodecOptions);
  }


  /** Reinitialize the underlying TiffParser. */
  protected void initTiffParser() {
    if (in == null) {
      try {
        in = new RandomAccessInputStream(getCurrentFile(), 16);
      }
      catch (IOException e) {
        LOGGER.error("Could not initialize stream", e);
      }
    }
    tiffParser = new TiffParser(in);
    tiffParser.setDoCaching(false);
    tiffParser.setUse64BitOffsets(use64Bit);
  }

}
