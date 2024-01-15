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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipInputStream;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 *
 * Please Note: This format holds FLIM data arranged so that each decay is stored contiguously.
 * Therefore, as in other FLIM format readers e.g. SDTReader.java, on the first call to openBytes
 * the whole data cube (x, y, t) (NB actually t, not real-time T) is loaded from the file and buffered.
 * On further calls to openBytes the appropriate 2D (x, y) plane (timebin) is returned from this buffer.
 * This is in the interest of significantly improved  performance when all the planes are requested one after another.
 * There will be a performance cost if a single plane is requested but this is highly unlikely for FLIM data.
 * In order to limit the size of the buffer, beyond a certain size threshold only a subset of planes (a Block) are
 * retained in the buffer.
 *
 **/

public class SDTReader extends FormatReader {

  // -- Fields --

  /** Object containing SDT header information. */
  protected SDTInfo info;

  /** Number of time bins in lifetime histogram. */
  protected int timeBins;

  /** Number of spectral channels. */
  protected int channels;

  /** Whether to combine lifetime bins into single intensity image planes. */
  protected boolean intensity = false;

  /** Whether to pre-load all lifetime bins for faster loading. */
  protected boolean preLoad = true;

  /*
   * Currently stored channel
   */
  protected int storedChannel = -1;

  /*
   * Currently stored series
   */
  protected int storedSeries = -1;

  /*
   * Array to hold re-ordered data for all the timeBins
   */
  protected byte[] dataStore = null;

  /**
   * Block of time bins currently stored for faster loading.
   */
  protected int currentBlock;

  /**
   * No of timeBins pre-loaded as a block
  */
  protected int blockLength ;

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() {
    super("SPCImage Data", "sdt");
    domains = new String[] {FormatTools.FLIM_DOMAIN};
  }

  // -- SDTReader API methods --

  /**
   * Toggles whether the reader should return intensity
   * data only (the sum of each lifetime histogram).
   */
  public void setIntensity(boolean intensity) {
    FormatTools.assertId(currentId, false, 1);
    this.intensity = intensity;
  }

  /**
   * Toggles whether the reader should pre-load data for increased performance.
   */
  public void setPreLoad(boolean preLoad) {
    this.preLoad = preLoad;
  }

  /**
   * Gets whether the reader is combining each lifetime
   * histogram into a summed intensity image plane.
   */
  public boolean isIntensity() { return intensity; }

  /** Gets the number of bins in the lifetime histogram. */
  public int getTimeBinCount() {
    return timeBins;
  }

  /** Gets the number of spectral channels. */
  public int getChannelCount() {
    return channels;
  }

  /** Gets object containing SDT header information. */
  public SDTInfo getInfo() {
    return info;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  @Override
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return !intensity && subC == 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int sizeX = getSizeX();
    int sizeY = getSizeY();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    boolean little = isLittleEndian();

    // This is the Becker Hickl block not the pre-loaded data block
    long blockSize = info.allBlockLengths[getSeries()];

    int paddedWidth = sizeX + ((4 - (sizeX % 4)) % 4);
    int times = timeBins;
    if (info.mcstaPoints == getSizeT()) {
      times = getSizeT();
    }
    long planeSize = (long) paddedWidth * sizeY * times * bpp;

    // remove width padding if we can be reasonably certain
    // that the unpadded width is correct
    if (paddedWidth > sizeX && planeSize * getSizeC() > blockSize &&
      (planeSize / paddedWidth) * sizeX * getSizeC() <= blockSize)
    {
      paddedWidth = sizeX;
      planeSize = sizeX * sizeY * times * bpp;
    }

    if (preLoad && !intensity) {
      int channel = no / times;
      int timeBin = no % times;

      byte[] rowBuf = new byte[bpp * times * paddedWidth];

      int binSize = paddedWidth * sizeY  * bpp;

      int preBlockSize = binSize * blockLength;

      // pre-load data for performance
      if (dataStore == null  || storedSeries != getSeries()) {
        dataStore = new byte[preBlockSize];
        currentBlock = -1;
      }

      if (timeBin/blockLength != currentBlock || storedChannel != channel  ) {

        currentBlock = timeBin / blockLength;

        // A subset of whole timebins (a preBlock) is  copied into storage
        // to allow different sub-plane sizes to be used for different timebin
        in.seek(info.allBlockOffsets[getSeries()]);
        ZipInputStream codec = null;
        String check = in.readString(2);
        in.seek(in.getFilePointer() - 2);
        if (check.equals("PK")) {
          codec = new ZipInputStream(in);
          codec.getNextEntry();
          codec.skip(channel * planeSize);
        }
        else {
          in.skipBytes(channel * planeSize);
        }

        int endOfBlock = (currentBlock + 1) * blockLength;
        int storeLength;
        if (endOfBlock > times) {
          storeLength = times - (currentBlock * blockLength);
        } else {
          storeLength = blockLength;
        }

        for (int row = 0; row < sizeY; row++) {
          readPixels(rowBuf, in, codec, 0);

          for (int col = 0; col < paddedWidth; col++) {
            // set output to first pixel of this row in 2D plane
            // corresponding to zeroth timeBin
            int output = (row * paddedWidth + col) * bpp;
            int input = ((col * times) + (currentBlock * blockLength)) * bpp;
            // copy subset of decay into buffer.

            for (int t = 0; t < storeLength; t++) {
              for (int bb = 0; bb < bpp; bb++) {
                dataStore[output + bb] = rowBuf[input + bb];
              }
              output += binSize;
              input += bpp;
            }
          }
        }
      }
      storedChannel = channel;
      storedSeries = getSeries();
      // dataStore loaded

      // copy 2D plane  from dataStore  into buf
      int iLineSize = paddedWidth * bpp;
      int oLineSize = w * bpp;
      // offset to correct timebin yth line and xth pixel
      int binInStore = timeBin - (currentBlock * blockLength);

      int input = (binSize * binInStore) + (y * iLineSize) + (x * bpp);
      int output = 0;

      for (int row = 0; row < h; row++) {
        System.arraycopy(dataStore, input, buf, output , oLineSize);
        input += iLineSize;
        output += oLineSize;
      }

      // allow for >1 count increments
      // the count increment is the amount by which the data is incremented for each event detected
      // normally this is 1 so each bit represents a photon
      // where it is >1 then divide the 16 bit data to get an answer in photon units
      if (info.incr > 1) {
        int incr = info.incr;

        ByteBuffer bb = ByteBuffer.wrap(buf); // Wrapper around underlying byte[].
        bb.order(ByteOrder.LITTLE_ENDIAN);
        short s;

        for (int i = 0; i < buf.length ; i+=2) {
          s = (short)bb.getShort(i);
          if (s > 0) {  //sign bit is not set
            bb.putShort(i,(short) (s/incr) );
          }
          else  {   // sign bit is set so extend to int to do the division
            int ii = s & 0xffff;
            bb.putShort(i,(short) (ii/incr) );
          }
        }
      }

      return buf;
    }
    else {   // intensity mode so no pre-loading

      int channel = intensity ? no : no / times;
      int timeBin = intensity ? 0 : no % times;

      byte[] b = !intensity ? buf : new byte[sizeY * sizeX * times * bpp];

      byte[] rowBuf = new byte[bpp * times * w];

      in.seek(info.allBlockOffsets[getSeries()]);

      ZipInputStream codec = null;
      String check = in.readString(2);
      in.seek(in.getFilePointer() - 2);
      if (check.equals("PK")) {
        codec = new ZipInputStream(in);
        codec.getNextEntry();
        codec.skip(channel * planeSize + y * paddedWidth * bpp * times);
      }
      else {
        in.skipBytes(
          channel * planeSize + (long) y * paddedWidth * bpp * times);
      }

      for (int row = 0; row < h; row++) {
        readPixels(rowBuf, in, codec, (long) x * bpp * times);
        if (intensity) {
          System.arraycopy(rowBuf, 0, b, row * bpp * times * w, b.length);
        }
        else {
          for (int col = 0; col < w; col++) {
            int output = (row * w + col) * bpp;
            int input = (col * times + timeBin) * bpp;
            for (int bb = 0; bb < bpp; bb++) {
              b[output + bb] = rowBuf[input + bb];
            }
          }
        }
        if (codec == null) {
          in.skipBytes((long) bpp * times * (paddedWidth - x - w));
        }
        else {
          codec.skip(bpp * times * (paddedWidth - x - w));
        }
      }

      if (!intensity) {
        return buf; // no cropping required
      }
      for (int row = 0; row < h; row++) {
        int yi = (y + row) * sizeX * times * bpp;
        int ri = row * w * bpp;
        for (int col = 0; col < w; col++) {
          int xi = yi + (x + col) * times * bpp;
          int ci = ri + col * bpp;
          // combine all lifetime bins into single intensity value
          short sum = 0;
          for (int t = 0; t < times; t++) {
            sum += DataTools.bytesToShort(b, xi + t * bpp, little);
          }
          DataTools.unpackBytes(sum, buf, ci, 2, little);
        }
      }
      return buf;
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      // init preLoading
      preLoad = true;
      dataStore = null;
      storedChannel = -1;
      storedSeries = -1;
      timeBins = channels = 0;
      currentBlock = -1;
      info = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    LOGGER.info("Reading header");

    // read file header information
    info = new SDTInfo(in, metadata);
    timeBins = info.timeBins;
    channels = info.channels;
    addGlobalMeta("time bins", timeBins);
    addGlobalMeta("channels", channels);

    double timeBase = 1e9 * info.tacR / info.tacG;
    addGlobalMeta("time base", timeBase);

    LOGGER.info("Populating metadata");

    CoreMetadata m = core.get(0);

    int timepoints = info.timepoints;
    if (timepoints == 0) {
      timepoints = 1;
    }

    m.sizeX = info.width;
    m.sizeY = info.height;
    m.sizeZ = 1;
    m.sizeT = intensity ? timepoints : timeBins * timepoints;
    m.sizeC = channels;
    m.dimensionOrder = "XYZTC";
    m.pixelType = FormatTools.UINT16;
    m.rgb = false;
    m.littleEndian = true;
    m.imageCount = m.sizeZ * m.sizeC * m.sizeT;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;


    if (intensity) {
      m.moduloT.parentType = FormatTools.SPECTRA;
    }
    else {
      m.moduloT.type = FormatTools.LIFETIME;
      m.moduloT.parentType = FormatTools.SPECTRA;
      m.moduloT.typeDescription = "TCSPC";
      m.moduloT.start = 0;

      timeBase *= 1000;

      m.moduloT.step = timeBase / timeBins;
      m.moduloT.end = m.moduloT.step * (m.sizeT - 1);
      m.moduloT.unit = "ps";
    }

    for (int i=1; i<info.allBlockOffsets.length; i++) {
      CoreMetadata p = new CoreMetadata(m);
      core.add(p);

      int planeSize = m.sizeX * m.sizeY * FormatTools.getBytesPerPixel(m.pixelType);
      if (info.allBlockLengths[i] != planeSize * m.imageCount) {
        if (info.mcstaPoints * planeSize == info.allBlockLengths[i]) {
          p.sizeT = info.mcstaPoints;
          p.moduloT.end = p.moduloT.step * (p.sizeT - 1);
          p.imageCount = p.sizeZ * p.sizeC * p.sizeT;
        }
      }
    }

    int sizeThreshold = 512 * 512 * 512;  // Arbitrarily chosen size limit for buffer
    blockLength = 2048;   //default No of bins in buffer

    while (blockLength * m.sizeX * m.sizeY > sizeThreshold) {
      blockLength = blockLength / 2;
    }

    if (blockLength > timeBins) {
      blockLength = timeBins;
    }


    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  private void readPixels(byte[] rowBuf, RandomAccessInputStream in, ZipInputStream codec, long skip)
    throws IOException
  {
    if (codec == null) {
      in.skipBytes(skip);
      in.read(rowBuf);
    }
    else {
      codec.skip(skip);
      int nread = 0;
      while (nread < rowBuf.length) {
        int n = codec.read(rowBuf, nread, rowBuf.length - nread);
        nread += n;
        if (n <= 0) {
          break;
        }
      }
    }
  }

}
