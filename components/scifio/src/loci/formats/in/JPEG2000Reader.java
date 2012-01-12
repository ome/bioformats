//
// JPEG2000Reader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.JPEG2000BoxType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.meta.MetadataStore;

/**
 * JPEG2000Reader is the file format reader for JPEG-2000 images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPEG2000Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPEG2000Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPEG2000Reader extends FormatReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(JPEG2000Reader.class);

  // -- Fields --

  /** The number of JPEG 2000 resolution levels the file has. */
  private Integer resolutionLevels;

  /** The color lookup table associated with this file. */
  private int[][] lut;

  private long pixelsOffset;

  private int lastSeries = -1;
  private byte[] lastSeriesPlane;

  // -- Constructor --

  /** Constructs a new JPEG2000Reader. */
  public JPEG2000Reader() {
    super("JPEG-2000", new String[] {"jp2", "j2k", "jpf"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    boolean validStart = (stream.readShort() & 0xffff) == 0xff4f;
    if (!validStart) {
      stream.skipBytes(2);
      validStart = stream.readInt() == JPEG2000BoxType.SIGNATURE.getCode();
    }
    stream.seek(stream.length() - 2);
    boolean validEnd = (stream.readShort() & 0xffff) == 0xffd9;
    return validStart && validEnd;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || FormatTools.getBytesPerPixel(getPixelType()) != 1) {
      return null;
    }

    byte[][] byteLut = new byte[lut.length][lut[0].length];
    for (int i=0; i<lut.length; i++) {
      for (int j=0; j<lut[i].length; j++) {
        byteLut[i][j] = (byte) (lut[i][j] & 0xff);
      }
    }
    return byteLut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null || FormatTools.getBytesPerPixel(getPixelType()) != 2) {
      return null;
    }

    short[][] shortLut = new short[lut.length][lut[0].length];
    for (int i=0; i<lut.length; i++) {
      for (int j=0; j<lut[i].length; j++) {
        shortLut[i][j] = (short) (lut[i][j] & 0xffff);
      }
    }
    return shortLut;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      resolutionLevels = null;
      lut = null;
      pixelsOffset = 0;
      lastSeries = -1;
      lastSeriesPlane = null;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (lastSeries == getSeries() && lastSeriesPlane != null) {
      RandomAccessInputStream s = new RandomAccessInputStream(lastSeriesPlane);
      readPlane(s, x, y, w, h, buf);
      s.close();
      return buf;
    }

    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    if (resolutionLevels != null) {
      options.resolution = Math.abs(series - resolutionLevels);
    }
    else if (getSeriesCount() > 1) {
      options.resolution = series;
    }

    in.seek(pixelsOffset);
    lastSeriesPlane = new JPEG2000Codec().decompress(in, options);
    RandomAccessInputStream s = new RandomAccessInputStream(lastSeriesPlane);
    readPlane(s, x, y, w, h, buf);
    s.close();
    lastSeries = getSeries();
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    JPEG2000MetadataParser metadataParser = new JPEG2000MetadataParser(in);
    if (metadataParser.isRawCodestream()) {
      LOGGER.info("Codestream is raw, using codestream dimensions.");
      core[0].sizeX = metadataParser.getCodestreamSizeX();
      core[0].sizeY = metadataParser.getCodestreamSizeY();
      core[0].sizeC = metadataParser.getCodestreamSizeC();
      core[0].pixelType = metadataParser.getCodestreamPixelType();
    }
    else {
      LOGGER.info("Codestream is JP2 boxed, using header dimensions.");
      core[0].sizeX = metadataParser.getHeaderSizeX();
      core[0].sizeY = metadataParser.getHeaderSizeY();
      core[0].sizeC = metadataParser.getHeaderSizeC();
      core[0].pixelType = metadataParser.getHeaderPixelType();
    }
    lut = metadataParser.getLookupTable();

    pixelsOffset = metadataParser.getCodestreamOffset();

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = true;
    core[0].littleEndian = false;
    core[0].indexed = !isRGB() && lut != null;

    // New core metadata now that we know how many sub-resolutions we have.
    if (resolutionLevels != null) {
      CoreMetadata[] newCore = new CoreMetadata[resolutionLevels + 1];
      newCore[0] = core[0];
      for (int i = 1; i < newCore.length; i++) {
        newCore[i] = new CoreMetadata(this, 0);
        newCore[i].sizeX = newCore[i - 1].sizeX / 2;
        newCore[i].sizeY = newCore[i - 1].sizeY / 2;
        newCore[i].thumbnail = true;
      }
      core = newCore;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);
  }

  // -- Helper methods --

}
