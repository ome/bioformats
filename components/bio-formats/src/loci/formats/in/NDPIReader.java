/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.codec.JPEGTileDecoder;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * NDPIReader is the file format reader for Hamamatsu .ndpi files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/NDPIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/NDPIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NDPIReader extends BaseTiffReader {

  // -- Constants --

  private static final int MAX_SIZE = 8192;
  private static final int THUMB_TAG_1 = 65426;
  private static final int THUMB_TAG_2 = 65439;
  private static final int METADATA_TAG = 65449;

  // -- Fields --

  private JPEGTileDecoder decoder;
  private int initializedSeries = -1;
  private int initializedPlane = -1;

  private int sizeZ = 1;
  private int pyramidHeight = 1;

  // -- Constructor --

  /** Constructs a new NDPI reader. */
  public NDPIReader() {
    super("Hamamatsu NDPI", new String[] {"ndpi"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = true;
  }

  // -- IFormatReader API methods --

  /** @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (x == 0 && y == 0 && w == 1 && h == 1) {
      return buf;
    }
    else if (getSizeX() <= MAX_SIZE && getSizeY() <= MAX_SIZE) {
      int ifdIndex = getIFDIndex(getCoreIndex(), no);
      in = new RandomAccessInputStream(currentId);
      tiffParser = new TiffParser(in);
      tiffParser.setUse64BitOffsets(true);
      return tiffParser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
    }

    if (initializedSeries != getCoreIndex() || initializedPlane != no) {
      if (x == 0 && y == 0 && w == getOptimalTileWidth() &&
        h == getOptimalTileHeight())
      {
        // it looks like we'll be reading lots of tiles
        setupService(0, 0, no);
      }
      else {
        // it looks like we'll only read one tile
        setupService(y, h, no);
      }
      initializedSeries = getCoreIndex();
      initializedPlane = no;
    }
    else if (decoder.getScanline(y) == null) {
      setupService(y, h, no);
    }

    int c = getRGBChannelCount();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int row = w * c * bytes;

    for (int yy=y; yy<y + h; yy++) {
      byte[] scanline = decoder.getScanline(yy);
      if (scanline != null) {
        int copy = (int) Math.min(row, buf.length - (yy - y) * row - 1);
        if (copy < 0) break;
        System.arraycopy(scanline, x * c * bytes, buf, (yy - y) * row, copy);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    int currentSeries = getSeries();
    if (getCoreIndex() >= pyramidHeight) {
      return super.openThumbBytes(no);
    }

    int thumbX = getThumbSizeX();
    int thumbY = getThumbSizeY();
    int rgbCount = getRGBChannelCount();

    if (hasFlattenedResolutions()) {
      setSeries(pyramidHeight - 1);
    }
    else {
      setResolution(pyramidHeight - 1);
    }

    byte[] thumb = null;

    if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
      rgbCount == getRGBChannelCount())
    {
      thumb = FormatTools.openThumbBytes(this, no);
      setSeries(currentSeries);
      setResolution(0);
    }
    else {
      // find the smallest series with the same aspect ratio
      for (int s=getSeriesCount()-1; s>=0; s--) {
        setSeries(s);
        if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
          s != currentSeries && rgbCount == getRGBChannelCount())
        {
          thumb = FormatTools.openThumbBytes(this, no);
          break;
        }
      }
      setSeries(currentSeries);
      if (thumb == null) {
        thumb = FormatTools.openThumbBytes(this, no);
      }
    }
    return thumb;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (decoder != null) {
        decoder.close();
      }
      decoder = null;
      initializedSeries = -1;
      initializedPlane = -1;
      sizeZ = 1;
      pyramidHeight = 1;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int maxHeight = (1024 * 1024) / (getSizeX() * getRGBChannelCount() * bpp);
    return (int) Math.min(maxHeight, getSizeY());
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(id);
    use64Bit = s.length() >= Math.pow(2, 32);
    s.close();
    super.initFile(id);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    decoder = new JPEGTileDecoder();

    ifds = tiffParser.getIFDs();

    // fix the offsets for > 4 GB files
    RandomAccessInputStream stream = new RandomAccessInputStream(currentId);
    for (int i=0; i<ifds.size(); i++) {
      long[] stripOffsets = ifds.get(i).getStripOffsets();

      boolean neededAdjustment = false;
      for (int j=0; j<stripOffsets.length; j++) {
        long newOffset = stripOffsets[j] + 0x100000000L;
        if (newOffset < stream.length()) {
          stripOffsets[j] = newOffset;
          neededAdjustment = true;
        }
      }
      if (neededAdjustment) {
        ifds.get(i).putIFDValue(IFD.STRIP_OFFSETS, stripOffsets);
      }
    }
    stream.close();

    for (int i=1; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      if (ifd.getImageWidth() == ifds.get(0).getImageWidth() &&
        ifd.getImageLength() == ifds.get(0).getImageLength())
      {
        sizeZ++;
      }
      else if (sizeZ == 1) {
        pyramidHeight++;
      }
    }

    int seriesCount = pyramidHeight + (ifds.size() - pyramidHeight * sizeZ);
    core = new CoreMetadata[seriesCount];

    // repopulate core metadata

    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      ifd.remove(THUMB_TAG_1);
      ifd.remove(THUMB_TAG_2);
      ifds.set(i, ifd);
      tiffParser.fillInIFD(ifds.get(i));

      int[] bpp = ifds.get(i).getBitsPerSample();
      for (int q=0; q<bpp.length; q++) {
        if (bpp[q] < 8) {
          bpp[q] = 8;
        }
      }
      ifds.get(i).putIFDValue(IFD.BITS_PER_SAMPLE, bpp);
    }

    for (int s=0; s<core.length; s++) {
      core[s] = new CoreMetadata();
      if (s == 0) {
        core[s].resolutionCount = pyramidHeight;
      }
    }

    for (int s=0; s<core.length; s++) {
      IFD ifd = ifds.get(getIFDIndex(s, 0));
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      core[s].rgb = samples > 1 || p == PhotoInterp.RGB;

      core[s].sizeX = (int) ifd.getImageWidth();
      core[s].sizeY = (int) ifd.getImageLength();
      core[s].sizeZ = s < pyramidHeight ? sizeZ : 1;
      core[s].sizeT = 1;
      core[s].sizeC = core[s].rgb ? samples : 1;
      core[s].littleEndian = ifd.isLittleEndian();
      core[s].indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      core[s].imageCount = core[s].sizeZ * core[s].sizeT;
      core[s].pixelType = ifd.getPixelType();
      core[s].metadataComplete = true;
      core[s].interleaved =
        core[s].sizeX > MAX_SIZE || core[s].sizeY > MAX_SIZE;
      core[s].falseColor = false;
      core[s].dimensionOrder = "XYCZT";
      core[s].thumbnail = s != 0;
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);

      if (i > 0) {
        int ifdIndex = getIFDIndex(i, 0);
        String creationDate = ifds.get(ifdIndex).getIFDTextValue(IFD.DATE_TIME);
        creationDate = DateTools.formatDate(creationDate, DATE_FORMATS);
        if (creationDate != null) {
          store.setImageAcquisitionDate(new Timestamp(creationDate), i);
        }

        double xResolution = ifds.get(ifdIndex).getXResolution();
        double yResolution = ifds.get(ifdIndex).getYResolution();

        if (xResolution > 0) {
          store.setPixelsPhysicalSizeX(new PositiveFloat(xResolution), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
            xResolution);
        }
        if (yResolution > 0) {
          store.setPixelsPhysicalSizeY(new PositiveFloat(yResolution), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
            yResolution);
        }
      }
    }
  }

  // -- Helper methods --

  private void setupService(int y, int h, int z)
    throws FormatException, IOException
  {
    decoder.close();

    IFD ifd = ifds.get(getIFDIndex(getCoreIndex(), z));

    long offset = ifd.getStripOffsets()[0];
    int byteCount = (int) ifd.getStripByteCounts()[0];
    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(currentId);
    in.seek(offset);
    in.setLength(offset + byteCount);

    decoder.initialize(in, y, h, getSizeX());
  }

  private int getIFDIndex(int seriesIndex, int zIndex) {
    if (seriesIndex < pyramidHeight) {
      return zIndex * pyramidHeight + seriesIndex;
    }

    return sizeZ * pyramidHeight + (seriesIndex - pyramidHeight);
  }

}
