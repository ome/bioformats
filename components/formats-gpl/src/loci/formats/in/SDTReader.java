/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SDTReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SDTReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
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
   * Array to hold re-ordered data for all the timeBins in one channel
   */
  protected byte[] chanStore = null;

  /*
   * Currently stored channel
   */
  protected int storedChannel = -1;

  /*
   * Currently stored series
   */
  protected int storedSeries = -1;

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
   * Toggles whether the reader should pre-load
   * data for increased performance.
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
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return !intensity && subC == 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int sizeX = getSizeX();
    int sizeY = getSizeY();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    boolean little = isLittleEndian();
    
   
   
    int paddedWidth = sizeX + ((4 - (sizeX % 4)) % 4);
    int planeSize = paddedWidth * sizeY * timeBins * bpp;
    
    if (preLoad  && !intensity)  {
      int channel =  no / timeBins;
      int timeBin = no % timeBins;
      
      byte[] rowBuf = new byte[bpp * timeBins * paddedWidth];

      int binSize = paddedWidth * sizeY  * bpp;
      
      if (chanStore == null || storedChannel != channel ||
        storedSeries != getSeries() )
      {
        // The whole plane (all timebins) is  copied into storage
        // to allow different sub-plane sizes to be used for different timebins
        chanStore = new byte[planeSize];
        in.seek(info.allBlockOffsets[getSeries()] + (channel * planeSize));
        
        for (int row = 0; row < sizeY; row++) {
          in.read(rowBuf);

          int input = 0;
          for (int col = 0; col < paddedWidth; col++) {
            // set output to first pixel of this row in 2D plane
            // corresponding to zeroth timeBin
            int output = (row * paddedWidth + col) * bpp;

            for (int t = 0; t < timeBins; t++)  {
              for (int bb = 0; bb < bpp; bb++) {
                chanStore[output + bb] = rowBuf[input + bb];
              }
              output += binSize;
              input += bpp;
            }
          }
        }

        storedChannel = channel;
        storedSeries = getSeries();
      }  // chanStore loaded

      // copy 2D plane  from chanStore  into buf

      int iLineSize = paddedWidth * bpp;
      int oLineSize = w * bpp;
      // offset to correct timebin yth line and xth pixel
      int input = (binSize * timeBin) + (y * iLineSize) + (x * bpp);
      int output = 0;

      for (int row = 0; row < h; row++) {
        System.arraycopy(chanStore, input, buf, output , oLineSize);
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
    else {
      int channel = intensity ? no : no / timeBins;
      int timeBin = intensity ? 0 : no % timeBins;

      byte[] b = !intensity ? buf : new byte[sizeY * sizeX * timeBins * bpp];

      byte[] rowBuf = new byte[bpp * timeBins * w];

      in.seek(info.allBlockOffsets[getSeries()] +
        channel * planeSize + y * paddedWidth * bpp * timeBins);

      for (int row = 0; row < h; row++) {
        in.skipBytes(x * bpp * timeBins);
        in.read(rowBuf);
        if (intensity) {
          System.arraycopy(rowBuf, 0, b, row * bpp * timeBins * w, b.length);
        }
        else {
          for (int col = 0; col < w; col++) {
            int output = (row * w + col) * bpp;
            int input = (col * timeBins + timeBin) * bpp;
            for (int bb = 0; bb < bpp; bb++) {
              b[output + bb] = rowBuf[input + bb];
            }
          }
        }
        in.skipBytes(bpp * timeBins * (paddedWidth - x - w));
      }

      if (!intensity) {
        return buf; // no cropping required
      }
      for (int row = 0; row < h; row++) {
        int yi = (y + row) * sizeX * timeBins * bpp;
        int ri = row * w * bpp;
        for (int col = 0; col < w; col++) {
          int xi = yi + (x + col) * timeBins * bpp;
          int ci = ri + col * bpp;
          // combine all lifetime bins into single intensity value
          short sum = 0;
          for (int t = 0; t < timeBins; t++) {
            sum += DataTools.bytesToShort(b, xi + t * bpp, little);
          }
          DataTools.unpackBytes(sum, buf, ci, 2, little);
        }
      }
      return buf;
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      // init preLoading
      preLoad = true;
      chanStore = null;
      storedChannel = -1;
      storedSeries = -1;
      timeBins = channels = 0;
      info = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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
    
    // disable pre-load mode for very large files
    // threshold is set to the size of the largest test file currently available
    if ( m.sizeX * m.sizeY * m.sizeT  >  (512 * 512 * 512))  {
      preLoad = false;
    }
    else  {
      preLoad = true;
    }

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
      core.add(m);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
