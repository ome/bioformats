/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.Hashtable;

import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.JPEGTileDecoder;
import loci.formats.meta.MetadataStore;

/**
 * Reader for decoding JPEG images using java.awt.Toolkit.
 * This reader is useful for reading very large JPEG images, as it supports
 * tile-based access.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/TileJPEGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/TileJPEGReader.java;hb=HEAD">Gitweb</a></dd></dl>
 * @author Melissa Linkert melissa at glencoesoftware.com
 */

public class TileJPEGReader extends FormatReader {

  // -- Fields --

  private JPEGTileDecoder decoder;

  // -- Constructor --

  public TileJPEGReader() {
    super("Tile JPEG", new String[] {"jpg", "jpeg"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int c = getRGBChannelCount();

    for (int ty=y; ty<y+h; ty++) {
      byte[] scanline = decoder.getScanline(ty);
      if (scanline == null) {
        decoder.initialize(currentId, 0);
        scanline = decoder.getScanline(ty);
      }
      System.arraycopy(scanline, c * x, buf, (ty - y) * c * w, c * w);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (decoder != null) {
        decoder.close();
      }
      decoder = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);
    decoder = new JPEGTileDecoder();
    decoder.initialize(in, 0, 1, 0);

    CoreMetadata m = core.get(0);

    m.interleaved = true;
    m.littleEndian = false;

    m.sizeX = decoder.getWidth();
    m.sizeY = decoder.getHeight();
    m.sizeZ = 1;
    m.sizeT = 1;
    m.sizeC = decoder.getScanline(0).length / getSizeX();
    m.rgb = getSizeC() > 1;
    m.imageCount = 1;
    m.pixelType = FormatTools.UINT8;
    m.dimensionOrder = "XYCZT";
    m.metadataComplete = true;
    m.indexed = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
