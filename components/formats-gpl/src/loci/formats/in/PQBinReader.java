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

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * PQBinReader is the file format reader for PicoQuant .bin files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PQBinReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PQBinReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * Please Note: This format holds FLIM data arranged so that each decay is stored contiguously. 
 * Therefore, as in other FLIM format readers e.g. SDTReader.java, on the first call to openBytes
 * the whole data cube ( x,y,t) (NB actually t not real-time T) is loaded from the file to  a buffer.
 * On further calls to openBytes the appropriate 2D (x,y)plane (timebin) is returned from this buffer.
 * This is in the interest of significantly improved  performance when all the planes are requested one after another.
 * There will be a performance cost if a single plane is requested but this is highly unlikely for FLIM data.
 *
 */
public class PQBinReader extends FormatReader {

  // -- Constants --

  public static final int HEADER_SIZE = 20;

  // -- Fields --
  /*
   * Number of time bins in lifetime histogram. 
   */
  protected int timeBins;

  /*
   * Array to hold re-ordered data for all the timeBins
   */
  protected byte[] dataStore = null;

  /**
   * Whether to pre-load all lifetime bins for faster loading.
   */
  protected boolean preLoad = true;

  // -- Constructor --
  /**
   * Constructs a new PQBin reader.
   */
  public PQBinReader() {
    super("PicoQuant Bin", "bin");
    domains = new String[]{FormatTools.FLIM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --
  
  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    
    long fileLength = stream.length();
    int bpp = FormatTools.getBytesPerPixel(FormatTools.UINT32);
    stream.order(true);
    // Header
    int sizeX = stream.readInt();
    int sizeY = stream.readInt();
    float pixResol = stream.readFloat();  // resolution of time axis of every Decay (in ns)
    int sizeT = stream.readInt();     
    if (( sizeX * sizeY * sizeT * bpp) + HEADER_SIZE  == fileLength)  {
      return true;
    }
    else  {
      return false;
    } 
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
    
    int planeSize = sizeX * sizeY * timeBins * bpp;
    int timeBin = no;

    int binSize = sizeX * sizeY  * bpp;  // size in Bytes of a single 2D timebin.
    
    // if data is of a manageable size then pre-load for performance
    if (preLoad)  {
      if (dataStore == null)
      {
        // The whole plane (all timebins) is  copied into storage
        // to allow different sub-plane sizes to be used for different timebins
        dataStore = new byte[planeSize];
        byte[] rowBuf = new byte[bpp * timeBins * sizeX];
        in.seek(HEADER_SIZE);

        for (int row = 0; row < sizeY; row++) {
          in.read(rowBuf);

          int input = 0;
          for (int col = 0; col < sizeX; col++) {
            // set output to first pixel of this row in 2D plane
            // corresponding to zeroth timeBin
            int output = (row * sizeX + col) * bpp;

            for (int t = 0; t < timeBins; t++) {
              for (int bb = 0; bb < bpp; bb++) {
                dataStore[output + bb] = rowBuf[input + bb];
              }
              output += binSize;
              input += bpp;
            }
          }
        }
      }
        // chanStore loaded

        // copy 2D plane  from chanStore  into buf
      int iLineSize = sizeX * bpp;
      int oLineSize = w * bpp;
      // offset to correct timebin yth line and xth pixel
      int input = (binSize * timeBin) + (y * iLineSize) + (x * bpp);
      int output = 0;

      for (int row = 0; row < h; row++) {
        System.arraycopy(dataStore, input, buf, output, oLineSize);
        input += iLineSize;
        output += oLineSize;
      }
    }    // endif preLoad
    else  {   // load each plane individually for large data
          
      byte[] rowBuf = new byte[bpp * timeBins * w];
      
      in.seek(HEADER_SIZE +  (y * sizeX * bpp * timeBins));

      for (int row = 0; row < h; row++) {
        in.skipBytes(x * bpp * timeBins);
        in.read(rowBuf);

        for (int col = 0; col < w; col++) {
          int output = (row * w + col) * bpp;
          int input = (col * timeBins + timeBin) * bpp;
          for (int bb = 0; bb < bpp; bb++) {
            buf[output + bb] = rowBuf[input + bb];
          }
        }

        in.skipBytes(bpp * timeBins * (sizeX - x - w));
      }
    }  // end else
   
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      // init preLoading
      dataStore = null;
      timeBins = 0;
      preLoad = true;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    
    CoreMetadata m = core.get(0);

    m.littleEndian = true;
    in.order(isLittleEndian());

    LOGGER.info("Reading header PQBin");
   
    // Header
    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    float pixResol = in.readFloat();    // Resolution of every Pixel in Image (in µm)
    m.sizeT = in.readInt();             // Number of DataPoints per Decay
    
    float timeResol = in.readFloat();   // resolution of time axis of every Decay (in ns)
    
    timeBins = m.sizeT;

    m.sizeZ = 1;
    m.sizeC = 1;
    m.dimensionOrder = "XYZCT";
    
    m.pixelType = FormatTools.UINT32;
    m.rgb = false;
    m.imageCount = m.sizeT;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    m.moduloT.type = FormatTools.LIFETIME;
    m.moduloT.parentType = FormatTools.SPECTRA;
    m.moduloT.typeDescription = "TCSPC";
    m.moduloT.start = 0;

    float timeBase = timeResol * 1000;  // Convert to ps

    m.moduloT.step = timeBase / timeBins;
    m.moduloT.end = m.moduloT.step * (m.sizeT - 1);
    m.moduloT.unit = "ps";
    
    // disable pre-load mode for very large files
    // threshold is set to smaller than the  size of the largest test file currently available
    if ( m.sizeX * m.sizeY * m.sizeT  >  (900 * 200 * 200))  {
      preLoad = false;
    }
    else  {
      preLoad = true;
    }
    
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    
    PositiveFloat pRpf = new PositiveFloat((double)pixResol);
    store.setPixelsPhysicalSizeX(pRpf, 0);
    store.setPixelsPhysicalSizeY(pRpf, 0);
    
  }

}
