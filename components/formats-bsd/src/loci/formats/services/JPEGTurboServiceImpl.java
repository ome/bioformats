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

package loci.formats.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.ServiceException;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import org.scijava.nativelib.NativeLibraryUtil;

import org.slf4j.LoggerFactory;

/**
 * Based upon the NDPI to OME-TIFF converter by Matthias Baldauf:
 *
 * http://matthias-baldauf.at/software/ndpi_converter/
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class JPEGTurboServiceImpl implements JPEGTurboService {

  // -- Constants --

  private static final org.slf4j.Logger LOGGER =
    LoggerFactory.getLogger(JPEGTurboServiceImpl.class);

  private static final String NATIVE_LIB_CLASS =
    "org.scijava.nativelib.NativeLibraryUtil";

  private static final int SOF0 = 0xffc0;

  private static final int DRI = 0xffdd;
  private static final int SOS = 0xffda;

  private static final int RST0 = 0xffd0;
  private static final int RST7 = 0xffd7;

  private static final int EOI = 0xffd9;

  // -- Fields --

  private transient Logger logger;
  private int imageWidth;
  private int imageHeight;
  private long offset;
  private RandomAccessInputStream in;

  private int restartInterval = 1;
  private long sos;
  private long imageDimensions;

  private int mcuWidth;
  private int mcuHeight;
  private int tileWidth;
  private int tileHeight;
  private int xTiles;
  private int yTiles;

  private ArrayList<Long> restartMarkers = new ArrayList<Long>();

  private byte[] header;

  private static boolean libraryLoaded = false;

  // -- Constructor --

  public JPEGTurboServiceImpl() {
    logger = Logger.getLogger(NATIVE_LIB_CLASS);
    logger.setLevel(Level.SEVERE);
    if (!libraryLoaded) {
      libraryLoaded = NativeLibraryUtil.loadNativeLibrary(TJ.class, "turbojpeg");
    }
  }

  // -- JPEGTurboService API methods --

  @Override
  public void setRestartMarkers(long[] markers) {
    restartMarkers.clear();
    if (markers != null) {
      for (long marker : markers) {
        restartMarkers.add(marker);
      }
    }
  }

  @Override
  public long[] getRestartMarkers() {
    long[] markers = new long[restartMarkers.size()];
    for (int i=0; i<markers.length; i++) {
      markers[i] = restartMarkers.get(i);
    }
    return markers;
  }

  @Override
  public void initialize(RandomAccessInputStream jpeg, int width, int height)
    throws ServiceException, IOException
  {
    in = jpeg;
    imageWidth = width;
    imageHeight = height;
    offset = jpeg.getFilePointer();

    in.skipBytes(2);
    int marker = in.readShort() & 0xffff;

    boolean inImage = false;
    while (!inImage && in.getFilePointer() + 2 < in.length()) {
      int length = in.readShort() & 0xffff;
      long end = in.getFilePointer() + length - 2;

      LOGGER.debug("found marker = {} at pointer = {}", marker, in.getFilePointer());

      if (marker == DRI) {
        restartInterval = in.readShort() & 0xffff;
        LOGGER.debug("set restart interval to {}", restartInterval);
      }
      else if (marker == SOF0) {
        imageDimensions = in.getFilePointer() + 1;
        parseSOF();
      }
      else if (marker > 0xFFC0 && marker < 0xFFD0 && marker % 4 != 0) {
        throw new IOException("Unsupported JPEG SOF marker: " + marker);
      }
      else if (marker == SOS) {
        sos = end;
        inImage = true;
        if (restartMarkers.size() == 0) {
          restartMarkers.add(sos);
        }
        else {
          long diff = sos - restartMarkers.get(0);
          for (int i=0; i<restartMarkers.size(); i++) {
            long original = restartMarkers.get(i);
            original += diff;
            restartMarkers.set(i, original);
          }
          break;
        }
      }

      if (end < in.length() && !inImage) {
        in.seek(end);
        marker = in.readShort() & 0xffff;
      }
    }

    if (restartMarkers.size() == 1) {
      in.seek(restartMarkers.get(0));

      byte[] buf = new byte[10 * 1024 * 1024];
      in.read(buf, 0, 4);

      while (in.getFilePointer() < in.length()) {
        int n = in.read(buf, 4,
          (int) Math.min(buf.length - 4, in.length() - in.getFilePointer()));
        n += 4;

        for (int i=0; i<n-1; i++) {
          marker = DataTools.bytesToShort(buf, i, 2, false) & 0xffff;
          if (marker >= RST0 && marker <= RST7) {
            restartMarkers.add(in.getFilePointer() - n + i + 2);
            LOGGER.debug("adding RST marker at {}", restartMarkers.get(restartMarkers.size() - 1));
            i += restartInterval;
          }
        }

        // refill buffer

        System.arraycopy(buf, n - 4, buf, 0, 4);
      }
    }

    tileWidth = restartInterval * mcuWidth;
    tileHeight = (int) Math.min(tileWidth, 512);

    xTiles = imageWidth / tileWidth;
    yTiles = imageHeight / tileHeight;

    if (xTiles * tileWidth != imageWidth) {
      xTiles++;
    }
    if (yTiles * tileHeight != imageHeight) {
      yTiles++;
    }

    if (restartInterval == 1 && restartMarkers.size() <= 1) {
      throw new IOException("The tiled-JPEG reader only supports images encoded with restart markers");
    }
  }

  @Override
  public int getTileWidth() {
    return tileWidth;
  }

  @Override
  public int getTileHeight() {
    return tileHeight;
  }

  @Override
  public int getTileRows() {
    return yTiles;
  }

  @Override
  public int getTileColumns() {
    return xTiles;
  }

  @Override
  public byte[] getTile(byte[] buf, int xCoordinate, int yCoordinate,
    int width, int height)
    throws IOException
  {
    Region image = new Region(xCoordinate, yCoordinate, width, height);

    int bufX = 0;
    int bufY = 0;

    int outputRowLen = width * 3;

    Region intersection = null;
    Region tileBoundary = new Region(0, 0, 0, 0);
    byte[] tile = null;
    for (int row=0; row<yTiles; row++) {
      tileBoundary.height = row < yTiles - 1 ? tileHeight : imageHeight - (tileHeight*row);
      tileBoundary.y = row * tileHeight;
      for (int col=0; col<xTiles; col++) {
        tileBoundary.x = col * tileWidth;
        tileBoundary.width = col < xTiles - 1 ? tileWidth : imageWidth - (tileWidth*col);
        if (tileBoundary.intersects(image)) {
          intersection = image.intersection(tileBoundary);
          tile = getTile(col, row);

          int rowLen =
            3 * (int) Math.min(tileBoundary.width, intersection.width);
          int outputOffset = bufY * outputRowLen + bufX;
          int intersectionX = 0;

          if (tileBoundary.x < image.x) {
            intersectionX = image.x - tileBoundary.x;
          }

          for (int trow=0; trow<intersection.height; trow++) {
            int realRow = trow + intersection.y - tileBoundary.y;
            int inputOffset =
              3 * (realRow * tileWidth + intersectionX);
            System.arraycopy(tile, inputOffset, buf, outputOffset, rowLen);
            outputOffset += outputRowLen;
          }
          bufX += rowLen;
        }
      }
      if (intersection != null) {
        bufX = 0;
        bufY += intersection.height;
      }
      if (bufY >= height) {
        break;
      }
    }

    return buf;
  }

  @Override
  public byte[] getTile(int tileX, int tileY) throws IOException {
    byte[] compressedData = getCompressedTile(tileX, tileY);

    // and here we actually decompress it...

    try {
      int pixelType = TJ.PF_RGB;
      int pixelSize = TJ.getPixelSize(pixelType);

      TJDecompressor decoder = new TJDecompressor(compressedData);
      byte[] decompressed = decoder.decompress(tileWidth, tileWidth * pixelSize,
        tileHeight, pixelType, pixelType);
      compressedData = null;
      decoder.close();
      return decompressed;
    }
    catch (Exception e) {
      IOException ioe = new IOException(e.getMessage());
      ioe.initCause(e);
      throw ioe;
    }
  }

  @Override
  public byte[] getCompressedTile(int tileX, int tileY) throws IOException {
    if (header == null) {
      header = getFixedHeader();
    }

    long dataLength = header.length + 2;

    int mult = tileHeight / mcuHeight; // was restartInterval
    int start = tileX + (tileY * xTiles * mult);
    for (int row=0; row<tileHeight/mcuHeight; row++) {
      int end = start + 1;

      long startOffset = restartMarkers.get(start);
      long endOffset = in.length();
      if (end < restartMarkers.size()) {
        endOffset = restartMarkers.get(end);
      }

      dataLength += (endOffset - startOffset);
      start += xTiles;
      if (start >= restartMarkers.size()) {
        break;
      }
    }

    byte[] data = new byte[(int) dataLength];

    int offset = 0;
    System.arraycopy(header, 0, data, offset, header.length);
    offset += header.length;

    start = tileX + (tileY * xTiles * mult);
    for (int row=0; row<tileHeight/mcuHeight; row++) {
      int end = start + 1;

      long endOffset = in.length();

      if (end < restartMarkers.size()) {
        endOffset = restartMarkers.get(end);
      }
      long startOffset = restartMarkers.get(start);

      in.seek(startOffset);
      int toRead = (int) (endOffset - startOffset - 2);
      in.read(data, offset, toRead);
      offset += toRead;

      DataTools.unpackBytes(0xffd0 + (row % 8), data, offset, 2, false);
      offset += 2;
      start += xTiles;

      if (start >= restartMarkers.size()) {
        break;
      }
    }

    DataTools.unpackBytes(EOI, data, offset, 2, false);
    return data;
  }

  @Override
  public void close() throws IOException {
    logger = null;
    imageWidth = 0;
    imageHeight = 0;
    if (in != null) {
      in.close();
    }
    in = null;
    offset = 0;
    restartMarkers.clear();
    restartInterval = 1;
    sos = 0;
    imageDimensions = 0;
    mcuWidth = 0;
    mcuHeight = 0;
    tileWidth = 0;
    tileHeight = 0;
    xTiles = 0;
    yTiles = 0;
    header = null;
  }

  @Override
  public boolean isLibraryLoaded() {
    return libraryLoaded;
  }

  // -- Helper methods --

  private byte[] getFixedHeader() throws IOException {
    in.seek(offset);

    byte[] header = new byte[(int) (sos - offset)];
    in.read(header);

    int index = (int) (imageDimensions - offset);
    DataTools.unpackBytes(tileHeight, header, index, 2, false);
    DataTools.unpackBytes(tileWidth, header, index + 2, 2, false);

    return header;
  }

  private void parseSOF() throws IOException {
    // https://mykb.cipindanci.com/archive/SuperKB/1294/JPEG%20File%20Layout%20and%20Format.htm
    // example: FFC00011 08001100 11030122 00021101 031101
    int bpc = in.readByte() & 0xff;
    if (bpc != 8) {
      throw new IOException("Only 8-bit channels supported by this reader");
    }
    in.skipBytes(4);
    int channels = in.readByte() & 0xff;
    if (channels != 3) {
      // https://stackoverflow.com/questions/51008883/is-there-a-grayscale-jpg-format
      throw new IOException("Only images with 3 channels are supported by this reader");
    }

    // Sampling factors: https://stackoverflow.com/q/43225439
    // https://stackoverflow.com/q/27918757 https://stackoverflow.com/q/43225439
    // mcu_tool.sh shows MCU size for some images
    // JpegSnoop or identify -verbose show subsampling rates
    // convert -sampling rate 2x2 or ffmpeg -i .. -vf format=yuv420p for making images

    // MCU size in X direction divided by 8 is taken as the largest X.
    // Likewise for Y. For each coomponent, first 4 bits is X, the next 4 bits is Y.
    // some examples: 2x1,2x1,2x1 (0x21,0x21,0x21) is 16x8.
    // 2x1,1x1,1x1 is 16x8. 3x2,1x1,1x1 is 24x16

    int maxX = 0x00;
    int maxY = 0x00;

    for (int i = 0; i < channels; i++) {
      int componentId = in.readByte() & 0xff;

      int rates = in.readByte();
      int X = rates & 0xf0; // Shift right later
      int Y = rates & 0x0f;
      maxX = Math.max(maxX, X);
      maxY = Math.max(maxY, Y);

      int quantTableNumber = in.readByte() & 0xff;
    }

    mcuWidth = (maxX >> 4) * 8;
    mcuHeight = maxY * 8;
  }

}
