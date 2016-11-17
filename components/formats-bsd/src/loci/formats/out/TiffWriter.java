/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.out;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.codec.CompressionType;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import loci.formats.tiff.TiffSaver;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Time;
import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * TiffWriter is the file format writer for TIFF files.
 */
public class TiffWriter extends FormatWriter {

  // -- Constants --

  public static final String COMPRESSION_UNCOMPRESSED =
    CompressionType.UNCOMPRESSED.getCompression();
  public static final String COMPRESSION_LZW =
    CompressionType.LZW.getCompression();
  public static final String COMPRESSION_J2K =
    CompressionType.J2K.getCompression();
  public static final String COMPRESSION_J2K_LOSSY =
    CompressionType.J2K_LOSSY.getCompression();
  public static final String COMPRESSION_JPEG =
    CompressionType.JPEG.getCompression();

  private static final String[] BIG_TIFF_SUFFIXES = {"tf2", "tf8", "btf"};

  /**
   * Number of bytes at which to automatically switch to BigTIFF
   * This is approximately 3.9 GB instead of 4 GB,
   * to allow space for the IFDs.
   */
  private static final long BIG_TIFF_CUTOFF = (long) 1024 * 1024 * 3990;

  /** TIFF tiles must be of a height and width divisible by 16. */
  private static final int TILE_GRANULARITY = 16;

  // -- Fields --

  /** Whether or not the output file is a BigTIFF file. */
  protected boolean isBigTiff;

  /** The TiffSaver that will do most of the writing. */
  protected TiffSaver tiffSaver;

  /** Input stream to use when overwriting data. */
  protected RandomAccessInputStream in;

  /** Whether or not to check the parameters passed to saveBytes. */
  protected boolean checkParams = true;

  /** The tile width which will be used for writing. */
  protected int tileSizeX;

  /** The tile height which will be used for writing. */
  protected int tileSizeY;

  /**
   * Sets the compression code for the specified IFD.
   * 
   * @param ifd The IFD table to handle.
   */
  private void formatCompression(IFD ifd)
    throws FormatException
  {
    if (compression == null) compression = "";
    TiffCompression compressType = TiffCompression.UNCOMPRESSED;
    if (compression.equals(COMPRESSION_LZW)) {
      compressType = TiffCompression.LZW;
    }
    else if (compression.equals(COMPRESSION_J2K)) {
      compressType = TiffCompression.JPEG_2000;
    }
    else if (compression.equals(COMPRESSION_J2K_LOSSY)) {
      compressType = TiffCompression.JPEG_2000_LOSSY;
    }
    else if (compression.equals(COMPRESSION_JPEG)) {
      compressType = TiffCompression.JPEG;
    }
    Object v = ifd.get(new Integer(IFD.COMPRESSION));
    if (v == null)
      ifd.put(new Integer(IFD.COMPRESSION), compressType.getCode());
  }

  // -- Constructors --

  public TiffWriter() {
    this("Tagged Image File Format",
      new String[] {"tif", "tiff", "tf2", "tf8", "btf"});
  }

  public TiffWriter(String format, String[] exts) {
    super(format, exts);
    compressionTypes = new String[] {
      COMPRESSION_UNCOMPRESSED,
      COMPRESSION_LZW,
      COMPRESSION_J2K,
      COMPRESSION_J2K_LOSSY,
      COMPRESSION_JPEG
    };
    isBigTiff = false;
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    // if a BigTIFF extension is used, or we know that
    // more than 4GB of data will be written, then automatically
    // switch to BigTIFF
    if (!isBigTiff) {
      if (checkSuffix(id, BIG_TIFF_SUFFIXES)) {
        LOGGER.info("Switching to BigTIFF (by file extension)");
        isBigTiff = true;
      }
      else if (compression == null || compression.equals(COMPRESSION_UNCOMPRESSED)) {
        MetadataRetrieve retrieve = getMetadataRetrieve();
        long totalBytes = 0;
        for (int i=0; i<retrieve.getImageCount(); i++) {
          int sizeX = retrieve.getPixelsSizeX(i).getValue();
          int sizeY = retrieve.getPixelsSizeY(i).getValue();
          int sizeZ = retrieve.getPixelsSizeZ(i).getValue();
          int sizeC = retrieve.getPixelsSizeC(i).getValue();
          int sizeT = retrieve.getPixelsSizeT(i).getValue();
          int type = FormatTools.pixelTypeFromString(
            retrieve.getPixelsType(i).toString());
          long bpp = FormatTools.getBytesPerPixel(type);
          totalBytes += sizeX * sizeY * sizeZ * sizeC * sizeT * bpp;
        }

        if (totalBytes >= BIG_TIFF_CUTOFF) {
          LOGGER.info("Switching to BigTIFF (by file size)");
          isBigTiff = true;
        }
      }
    }

    tileSizeX = super.getTileSizeX();
    tileSizeY = super.getTileSizeY();

    synchronized (this) {
      setupTiffSaver();
    }
  }

  // -- TiffWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.
   */
  public void saveBytes(int no, byte[] buf, IFD ifd)
    throws IOException, FormatException
  {
    MetadataRetrieve r = getMetadataRetrieve();
    int w = r.getPixelsSizeX(series).getValue().intValue();
    int h = r.getPixelsSizeY(series).getValue().intValue();
    saveBytes(no, buf, ifd, 0, 0, w, h);
  }

  /**
   * Saves the given image to the specified series in the current file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.
   */
  public void saveBytes(int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
    throws IOException, FormatException
  {
    if (checkParams) checkParams(no, buf, x, y, w, h);
    if (ifd == null) ifd = new IFD();
    MetadataRetrieve retrieve = getMetadataRetrieve();
    int type = FormatTools.pixelTypeFromString(
        retrieve.getPixelsType(series).toString());
    int index = no;
    int imageWidth = retrieve.getPixelsSizeX(series).getValue().intValue();
    int imageHeight = retrieve.getPixelsSizeY(series).getValue().intValue();
    if (tileSizeX < imageWidth || tileSizeY < imageHeight) {
      ifd.put(new Integer(IFD.TILE_WIDTH), new Long(tileSizeX));
      ifd.put(new Integer(IFD.TILE_LENGTH), new Long(tileSizeY));
    }
    if (tileSizeX < w || tileSizeY < h) {
      int nXTiles = w / tileSizeX;
      int nYTiles = h / tileSizeY;
   
      for (int yTileIndex=0; yTileIndex<nYTiles; yTileIndex++) {
        for (int xTileIndex=0; xTileIndex<nXTiles; xTileIndex++) {
          byte [] tileBuf = getTile(buf, xTileIndex, yTileIndex, x, y, w, h);
          int tileX = x + (xTileIndex * tileSizeX);
          int tileY = y + (yTileIndex * tileSizeY);
         
          // This operation is synchronized
          synchronized (this) {
            // This operation is synchronized against the TIFF saver.
            synchronized (tiffSaver) {
              index = prepareToWriteImage(no++, tileBuf, ifd, tileX, tileY, tileSizeX, tileSizeY);
              if (index == -1) {
                return;
              }
            }
          }     
  
          tiffSaver.writeImage(tileBuf, ifd, index, type, tileX, tileY, tileSizeX, tileSizeY,
          no == getPlaneCount() - 1 && getSeries() == retrieve.getImageCount() - 1);
        }
      }
    }
    else {
      // This operation is synchronized
      synchronized (this) {
        // This operation is synchronized against the TIFF saver.
        synchronized (tiffSaver) {
          index = prepareToWriteImage(no++, buf, ifd, x, y, w, h);
          if (index == -1) {
            return;
          }
        }
      }     

      tiffSaver.writeImage(buf, ifd, index, type, x, y, w, h,
      no == getPlaneCount() - 1 && getSeries() == retrieve.getImageCount() - 1);
    }
  }

  /**
   * Performs the preparation for work prior to the usage of the TIFF saver.
   * This method is factored out from <code>saveBytes()</code> in an attempt to
   * ensure thread safety.
   */
  protected int prepareToWriteImage(
      int no, byte[] buf, IFD ifd, int x, int y, int w, int h)
  throws IOException, FormatException {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    boolean littleEndian = false;
    if (retrieve.getPixelsBigEndian(series) != null) {
      littleEndian = !retrieve.getPixelsBigEndian(series).booleanValue();
    }
    else if (retrieve.getPixelsBinDataCount(series) == 0) {
      littleEndian = !retrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }

    // Ensure that no more than one thread manipulated the initialized array
    // at one time.
    synchronized (this) {
      if (no < initialized[series].length && !initialized[series][no]) {
        initialized[series][no] = true;

        RandomAccessInputStream tmp = new RandomAccessInputStream(currentId);
        if (tmp.length() == 0) {
          synchronized (this) {
            // write TIFF header
            tiffSaver.writeHeader();
          }
        }
        tmp.close();
      }
    }

    int c = getSamplesPerPixel();
    int type = FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(type);

    int blockSize = w * h * c * bytesPerPixel;
    if (blockSize > buf.length) {
      c = buf.length / (w * h * bytesPerPixel);
    }

    if (bytesPerPixel > 1 && c != 1 && c != 3) {
      // split channels
      checkParams = false;

      if (no == 0) {
        initialized[series] = new boolean[initialized[series].length * c];
      }

      for (int i=0; i<c; i++) {
        byte[] b = ImageTools.splitChannels(buf, i, c, bytesPerPixel,
          false, interleaved);

        saveBytes(no * c + i, b, (IFD) ifd.clone(), x, y, w, h);
      }
      checkParams = true;
      return -1;
    }

    formatCompression(ifd);
    byte[][] lut = AWTImageTools.get8BitLookupTable(cm);
    if (lut != null) {
      int[] colorMap = new int[lut.length * lut[0].length];
      for (int i=0; i<lut.length; i++) {
        for (int j=0; j<lut[0].length; j++) {
          colorMap[i * lut[0].length + j] = (int) ((lut[i][j] & 0xff) << 8);
        }
      }
      ifd.putIFDValue(IFD.COLOR_MAP, colorMap);
    }
    else {
      short[][] lut16 = AWTImageTools.getLookupTable(cm);
      if (lut16 != null) {
        int[] colorMap = new int[lut16.length * lut16[0].length];
        for (int i=0; i<lut16.length; i++) {
          for (int j=0; j<lut16[0].length; j++) {
            colorMap[i * lut16[0].length + j] = (int) (lut16[i][j] & 0xffff);
          }
        }
        ifd.putIFDValue(IFD.COLOR_MAP, colorMap);
      }
    }

    int width = retrieve.getPixelsSizeX(series).getValue().intValue();
    int height = retrieve.getPixelsSizeY(series).getValue().intValue();
    ifd.put(new Integer(IFD.IMAGE_WIDTH), new Long(width));
    ifd.put(new Integer(IFD.IMAGE_LENGTH), new Long(height));

    Length px = retrieve.getPixelsPhysicalSizeX(series);
    Double physicalSizeX = px == null || px.value(UNITS.MICROMETER) == null ? null : px.value(UNITS.MICROMETER).doubleValue();
    if (physicalSizeX == null || physicalSizeX.doubleValue() == 0) {
      physicalSizeX = 0d;
    }
    else physicalSizeX = 1d / physicalSizeX;

    Length py = retrieve.getPixelsPhysicalSizeY(series);
    Double physicalSizeY = py == null || py.value(UNITS.MICROMETER) == null ? null : py.value(UNITS.MICROMETER).doubleValue();
    if (physicalSizeY == null || physicalSizeY.doubleValue() == 0) {
      physicalSizeY = 0d;
    }
    else physicalSizeY = 1d / physicalSizeY;

    ifd.put(IFD.RESOLUTION_UNIT, 3);
    ifd.put(IFD.X_RESOLUTION,
      new TiffRational((long) (physicalSizeX * 1000 * 10000), 1000));
    ifd.put(IFD.Y_RESOLUTION,
      new TiffRational((long) (physicalSizeY * 1000 * 10000), 1000));

    if (!isBigTiff) {
      isBigTiff = (out.length() + 2
          * (width * height * c * bytesPerPixel)) >= 4294967296L;
      if (isBigTiff) {
        throw new FormatException("File is too large; call setBigTiff(true)");
      }
    }

    // write the image
    ifd.put(new Integer(IFD.LITTLE_ENDIAN), new Boolean(littleEndian));
    if (!ifd.containsKey(IFD.REUSE)) {
      ifd.put(IFD.REUSE, out.length());
      out.seek(out.length());
    }
    else {
      out.seek((Long) ifd.get(IFD.REUSE));
    }

    ifd.putIFDValue(IFD.PLANAR_CONFIGURATION,
      interleaved || getSamplesPerPixel() == 1 ? 1 : 2);

    int sampleFormat = 1;
    if (FormatTools.isSigned(type)) sampleFormat = 2;
    if (FormatTools.isFloatingPoint(type)) sampleFormat = 3;
    ifd.putIFDValue(IFD.SAMPLE_FORMAT, sampleFormat);

    int channels = retrieve.getPixelsSizeC(series).getValue().intValue();
    int z = retrieve.getPixelsSizeZ(series).getValue().intValue();
    int t = retrieve.getPixelsSizeT(series).getValue().intValue();
    ifd.putIFDValue(IFD.IMAGE_DESCRIPTION,
      "ImageJ=\nhyperstack=true\nimages=" + (channels * z * t) + "\nchannels=" +
      channels + "\nslices=" + z + "\nframes=" + t);

    int index = no;
    for (int i=0; i<getSeries(); i++) {
      index += getPlaneCount(i);
    }
    return index;
  }

  // -- FormatWriter API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatWriter#close()
   */
  @Override
  public void close() throws IOException {
    super.close();
    if (in != null) {
      in.close();
    }
    if (tiffSaver != null) {
      tiffSaver.close();
    }
  }

  /* @see loci.formats.FormatWriter#getPlaneCount() */
  @Override
  public int getPlaneCount() {
    return getPlaneCount(series);
  }
  
  @Override
  protected int getPlaneCount(int series) {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    int c = getSamplesPerPixel(series);
    int type = FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(type);

    if (bytesPerPixel > 1 && c != 1 && c != 3) {
      return super.getPlaneCount(series) * c;
    }
    return super.getPlaneCount(series);
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    IFD ifd = new IFD();
    if (!sequential) {
      TiffParser parser = new TiffParser(currentId);
      try {
        long[] ifdOffsets = parser.getIFDOffsets();
        if (no < ifdOffsets.length) {
          ifd = parser.getIFD(ifdOffsets[no]);
        }
      }
      finally {
        RandomAccessInputStream tiffParserStream = parser.getStream();
        if (tiffParserStream != null) {
          tiffParserStream.close();
        }
      }
    }

    saveBytes(no, buf, ifd, x, y, w, h);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks(String) */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    if (codec != null && codec.equals(COMPRESSION_JPEG)) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8,
        FormatTools.INT16, FormatTools.UINT16};
    }
    else if (codec != null && codec.equals(COMPRESSION_J2K)) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8,
        FormatTools.INT16, FormatTools.UINT16, FormatTools.INT32,
        FormatTools.UINT32, FormatTools.FLOAT};
    }
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT, FormatTools.DOUBLE};
  }

  // -- TiffWriter API methods --

  /**
   * Sets whether or not BigTIFF files should be written.
   * This flag is not reset when close() is called.
   */
  public void setBigTiff(boolean bigTiff) {
    FormatTools.assertId(currentId, false, 1);
    isBigTiff = bigTiff;
  }

  // -- Helper methods --

  protected void setupTiffSaver() throws IOException {
    out.close();
    out = new RandomAccessOutputStream(currentId);
    tiffSaver = new TiffSaver(out, currentId);

    MetadataRetrieve retrieve = getMetadataRetrieve();
    boolean littleEndian = false;
    if (retrieve.getPixelsBigEndian(series) != null) {
      littleEndian = !retrieve.getPixelsBigEndian(series).booleanValue();
    }
    else if (retrieve.getPixelsBinDataCount(series) == 0) {
      littleEndian = !retrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }

    tiffSaver.setWritingSequentially(sequential);
    tiffSaver.setLittleEndian(littleEndian);
    tiffSaver.setBigTiff(isBigTiff);
    tiffSaver.setCodecOptions(options);
  }

  @Override
  public int getTileSizeX() {
    if (tileSizeX == 0) {
      tileSizeX = super.getTileSizeX();
    }
    return tileSizeX;
  }

  @Override
  public int setTileSizeX(int tileSize) throws FormatException {
    tileSizeX = super.setTileSizeX(tileSize);
    tileSizeX = Math.round(tileSize/TILE_GRANULARITY) * TILE_GRANULARITY;
    return tileSizeX;
  }

  @Override
  public int getTileSizeY() {
    if (tileSizeY == 0) {
      tileSizeY = super.getTileSizeY();
    }
    return tileSizeY;
  }

  @Override
  public int setTileSizeY(int tileSize) throws FormatException {
    tileSizeY = super.setTileSizeY(tileSize);
    tileSizeY = Math.round(tileSize/TILE_GRANULARITY) * TILE_GRANULARITY;
    return tileSizeY;
  }

  private byte[] getTile(byte[] buf, int xTileIndex, int yTileIndex, int x, int y, int w, int h) {
    int nXTiles = w / tileSizeX;
    int nYTiles = h / tileSizeY;
    int tileX = x + (xTileIndex * tileSizeX);
    int tileY = y + (yTileIndex * tileSizeY);
    int effTileSizeX = xTileIndex < nXTiles - 1 ? tileSizeX : w - (tileSizeX * xTileIndex);
    int effTileSizeY = yTileIndex < nYTiles - 1 ? tileSizeY : h - (tileSizeY * yTileIndex);
    MetadataRetrieve retrieve = getMetadataRetrieve();
    int type = FormatTools.pixelTypeFromString(retrieve.getPixelsType(series).toString());
    int channel_count = getSamplesPerPixel();
    int bytesPerPixel = FormatTools.getBytesPerPixel(type);
    int tileSize = effTileSizeX * effTileSizeY * bytesPerPixel * channel_count;
    byte [] returnBuf = new byte[tileSize];
   
    for (int row = tileY; row != tileY + effTileSizeY; row++) {
      for (int sampleoffset = 0; sampleoffset < (effTileSizeX * channel_count); sampleoffset++) {
        int channel_index = sampleoffset / effTileSizeX;
        int channel_offset = (sampleoffset - (effTileSizeX * channel_index)) * bytesPerPixel;
        int full_row_width = w * bytesPerPixel;
        int full_plane_size = full_row_width * h;
        int xoffset = (tileX - x) * bytesPerPixel;
        int yoffset = (row - y) * full_row_width;
        int src_index = yoffset + xoffset + channel_offset + (channel_index * full_plane_size);
        int dest_index = (effTileSizeY * effTileSizeX * channel_index) + ((row - tileY) * effTileSizeX);
        for (int pixelByte = 0; pixelByte < bytesPerPixel; pixelByte++) {
          returnBuf[dest_index + channel_offset + pixelByte] = buf[src_index + pixelByte];
        }
      }
    }
    return returnBuf;
  }

}
