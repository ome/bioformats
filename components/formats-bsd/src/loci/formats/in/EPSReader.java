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

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;

/**
 * Reader is the file format reader for Encapsulated PostScript (EPS) files.
 * Some regular PostScript files are also supported.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class EPSReader extends FormatReader {

  // -- Fields --

  /** Starting line of pixel data. */
  private int start;

  /** Flag indicating binary data. */
  private boolean binary;

  private boolean isTiff;
  private IFDList ifds;
  private int[] map;

  // -- Constructor --

  /** Constructs a new EPS reader. */
  public EPSReader() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi", "ps"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      if (isTiff) {
        return (int) ifds.get(0).getTileWidth();
      }
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
      if (isTiff) {
        return (int) ifds.get(0).getTileLength();
      }
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (isTiff) {
      long[] offsets = ifds.get(0).getStripOffsets();
      long[] byteCounts = ifds.get(0).getStripByteCounts();
      in.seek(offsets[0]);

      if (map == null) {
        readPlane(in, x, y, w, h, buf);
        return buf;
      }

      byte[] b = new byte[w * h];
      int bpp = (int) (byteCounts[0] / (getSizeX() * getSizeY()));
      in.skipBytes(bpp * y * getSizeX());
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bpp);
        for (int col=0; col<w; col++) {
          if (bpp == 1) {
            b[row * w + col] = in.readByte();
          }
          else if (bpp == 2) {
            b[row * w + col] = (byte) (in.readShort() & 0xff);
          }
        }
        in.skipBytes(bpp * (getSizeX() - w - x));
      }

      for (int i=0; i<b.length; i++) {
        int ndx = b[i] & 0xff;
        for (int j=0; j<getSizeC(); j++) {
          if (j < 3) {
            buf[i*getSizeC() + j] = (byte) ((map[ndx + j*256] >> 8) & 0xff);
          }
          else {
            boolean zero =
              map[ndx] == 0 && map[ndx + 256] == 0 && map[ndx + 512] == 0;
            buf[i * getSizeC() + j] = zero ? (byte) 0 : (byte) 255;
          }
        }
      }

      return buf;
    }

    if (start == 0) {
      throw new FormatException("Vector data not supported.");
    }

    in.seek(0);
    for (int line=0; line<=start; line++) {
      readLine();
    }

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    if (binary) {
      // pixels are stored as raw bytes
      readPlane(in, x, y, w, h, buf);
    }
    else {
      // pixels are stored as a 2 character hexadecimal value
      String pix = in.readString((int) (in.length() - in.getFilePointer()));
      pix = pix.replaceAll("\n", "");
      pix = pix.replaceAll("\r", "");

      int ndx = getSizeC() * y * bytes * getSizeX();
      int destNdx = 0;

      for (int row=0; row<h; row++) {
        ndx += x * getSizeC() * bytes;
        for (int col=0; col<w*getSizeC()*bytes; col++) {
          buf[destNdx++] =
            (byte) Integer.parseInt(pix.substring(2*ndx, 2*(ndx+1)), 16);
          ndx++;
        }
        ndx += getSizeC() * bytes * (getSizeX() - w - x);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      start = 0;
      binary = isTiff = false;
      ifds = null;
      map = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    LOGGER.info("Verifying EPS format");

    String line = readLine();
    if (!line.trim().startsWith("%!PS")) {
      // read the TIFF preview

      isTiff = true;

      in.order(true);
      in.seek(20);
      int offset = in.readInt();
      int len = in.readInt();

      byte[] b = new byte[len];
      in.seek(offset);
      in.read(b);

      //close the first stream
      in.close();
      in = new RandomAccessInputStream(b);
      TiffParser tp = new TiffParser(in);
      ifds = tp.getMainIFDs();

      IFD firstIFD = ifds.get(0);
      map = tp.getColorMap(firstIFD);

      m.sizeX = (int) firstIFD.getImageWidth();
      m.sizeY = (int) firstIFD.getImageLength();
      m.sizeZ = 1;
      m.sizeT = 1;
      m.sizeC = firstIFD.getSamplesPerPixel();
      if (map != null && getSizeC() == 1) {
        m.sizeC = 3;
      }
      if (getSizeC() == 2) m.sizeC = 4;
      m.littleEndian = firstIFD.isLittleEndian();
      m.interleaved = true;
      m.rgb = getSizeC() > 1;
      m.pixelType = firstIFD.getPixelType();

      m.imageCount = 1;
      m.dimensionOrder = "XYCZT";
      m.metadataComplete = true;
      m.indexed = false;
      m.falseColor = false;

      MetadataStore store = makeFilterMetadata();
      MetadataTools.populatePixels(store, this);
      return;
    }

    LOGGER.info("Finding image data");

    binary = false;

    String image = "image";
    int lineNum = 1;

    line = readLine().trim();

    while (line != null && !line.equals("%%EOF")) {
      if (line.endsWith(image)) {
        if (!line.startsWith(image)) {
          if (line.indexOf("colorimage") != -1) m.sizeC = 3;
          String[] t = line.split(" ");
          try {
            int newX = Integer.parseInt(t[0]);
            int newY = Integer.parseInt(t[1]);
            if (t.length > 2 && Integer.parseInt(t[2]) >= 8) {
              m.sizeX = newX;
              m.sizeY = newY;
              start = lineNum;
            }
          }
          catch (NumberFormatException exc) {
            LOGGER.debug("Could not parse image dimensions", exc);
            if (t.length > 3) {
              m.sizeC = Integer.parseInt(t[3]);
            }
          }
        }

        break;
      }
      else if (line.startsWith("%%")) {
        if (line.startsWith("%%BoundingBox:")) {
          line = line.substring(14).trim();
          String[] t = line.split(" ");
          try {
            int originX = Integer.parseInt(t[0].trim());
            int originY = Integer.parseInt(t[1].trim());
            m.sizeX = Integer.parseInt(t[2].trim()) - originX;
            m.sizeY = Integer.parseInt(t[3].trim()) - originY;

            addGlobalMeta("X-coordinate of origin", originX);
            addGlobalMeta("Y-coordinate of origin", originY);
          }
          catch (NumberFormatException e) {
            throw new FormatException(
              "Files without image data are not supported.");
          }
        }
        else if (line.startsWith("%%BeginBinary")) {
          binary = true;
        }
        else {
          // parse key/value pairs

          int ndx = line.indexOf(':');
          if (ndx != -1) {
            String key = line.substring(0, ndx);
            String value = line.substring(ndx + 1);
            addGlobalMeta(key, value);
          }
        }
      }
      else if (line.startsWith("%ImageData:")) {
        line = line.substring(11);
        String[] t = line.split(" ");
        m.sizeX = Integer.parseInt(t[0]);
        m.sizeY = Integer.parseInt(t[1]);
        m.sizeC = Integer.parseInt(t[3]);
        for (int i=4; i<t.length; i++) {
          image = t[i].trim();
          if (image.length() > 1) {
            image = image.substring(1, image.length() - 1);
          }
        }
      }
      lineNum++;
      line = readLine().trim();
    }

    LOGGER.info("Populating metadata");

    if (getSizeC() == 0) m.sizeC = 1;

    m.sizeZ = 1;
    m.sizeT = 1;
    m.dimensionOrder = "XYCZT";
    m.pixelType = FormatTools.UINT8;
    m.rgb = getSizeC() == 3;
    m.interleaved = true;
    m.littleEndian = true;
    m.imageCount = 1;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    MetadataTools.populatePixels(store, this);
  }

  private String readLine() throws IOException {
    String s = in.findString("\r", "\n");
    return s.length() == 0 ? null : s;
  }

}
