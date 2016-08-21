/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import java.util.StringTokenizer;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * PGMReader is the file format reader for Portable Gray Map (PGM) images.
 *
 * Much of this code was adapted from ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PGMReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PGMReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PGMReader extends FormatReader {

  // -- Constants --

  public static final char PGM_MAGIC_CHAR = 'P';

  // -- Fields --

  private boolean rawBits;

  /** Offset to pixel data. */
  private long offset;

  // -- Constructor --

  /** Constructs a new PGMReader. */
  public PGMReader() {
    super("Portable Gray Map", "pgm");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.read() == PGM_MAGIC_CHAR &&
      Character.isDigit((char) stream.read());
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offset);
    if (rawBits) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      ByteArrayHandle handle = new ByteArrayHandle();
      RandomAccessOutputStream out = new RandomAccessOutputStream(handle);
      out.order(isLittleEndian());

      while (in.getFilePointer() < in.length()) {
        String line = in.readLine().trim();
        line = line.replaceAll("[^0-9]", " ");
        StringTokenizer t = new StringTokenizer(line, " ");
        while (t.hasMoreTokens()) {
          int q = Integer.parseInt(t.nextToken().trim());
          if (getPixelType() == FormatTools.UINT16) {
            out.writeShort(q);
          }
          else out.writeByte(q);
        }
      }

      out.close();
      RandomAccessInputStream s = new RandomAccessInputStream(handle);
      s.seek(0);
      readPlane(s, x, y, w, h, buf);
      s.close();
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      rawBits = false;
      offset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String magic = in.readLine().trim();

    boolean isBlackAndWhite = false;

    CoreMetadata m = core.get(0);

    rawBits = magic.equals("P4") || magic.equals("P5") || magic.equals("P6");
    m.sizeC = (magic.equals("P3") || magic.equals("P6")) ? 3 : 1;
    isBlackAndWhite = magic.equals("P1") || magic.equals("P4");

    String line = readNextLine();

    line = line.replaceAll("[^0-9]", " ");
    int space = line.indexOf(" ");
    m.sizeX = Integer.parseInt(line.substring(0, space).trim());
    m.sizeY = Integer.parseInt(line.substring(space + 1).trim());

    if (!isBlackAndWhite) {
      int max = Integer.parseInt(readNextLine());
      if (max > 255) m.pixelType = FormatTools.UINT16;
      else m.pixelType = FormatTools.UINT8;
    }

    offset = in.getFilePointer();

    addGlobalMeta("Black and white", isBlackAndWhite);

    m.rgb = getSizeC() == 3;
    m.dimensionOrder = "XYCZT";
    m.littleEndian = true;
    m.interleaved = false;
    m.sizeZ = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private String readNextLine() throws IOException {
    String line = in.readLine().trim();
    while (line.startsWith("#") || line.length() == 0) {
      line = in.readLine().trim();
    }
    return line;
  }

}
