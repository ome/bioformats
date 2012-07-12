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

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.codec.BitBuffer;
import loci.formats.meta.MetadataStore;

/**
 * CanonRawReader is the file format reader for Canon RAW files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CanonRawReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CanonRawReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CanonRawReader extends FormatReader {

  // -- Constants --

  private static final int FILE_LENGTH = 18653760;
  private static final int[] COLOR_MAP = {1, 0, 2, 1};

  // -- Fields --

  private byte[] plane;

  // -- Constructor --

  /** Constructs a new Canon RAW reader. */
  public CanonRawReader() {
    super("Canon RAW", new String[] {"cr2", "crw", "jpg", "thm", "wav"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return stream.length() == FILE_LENGTH;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    RandomAccessInputStream s = new RandomAccessInputStream(plane);
    readPlane(s, x, y, w, h, buf);
    s.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      plane = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    byte[] pixBuffer = new byte[FILE_LENGTH];
    in.read(pixBuffer);

    // reverse bytes in pairs

    for (int i=0; i<pixBuffer.length; i+=2) {
      byte v = pixBuffer[i];
      pixBuffer[i] = pixBuffer[i + 1];
      pixBuffer[i + 1] = v;
    }

    core[0].sizeX = 4080;
    core[0].sizeY = 3048;
    core[0].sizeC = 3;
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].imageCount = getSizeZ() * getSizeT();
    core[0].indexed = false;
    core[0].littleEndian = true;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.UINT16;
    core[0].bitsPerPixel = 12;
    core[0].rgb = true;
    core[0].interleaved = true;

    BitBuffer bb = new BitBuffer(pixBuffer);
    plane = new byte[FormatTools.getPlaneSize(this)];
    short[] pix = new short[getSizeX() * getSizeY() * 3];

    for (int row=0; row<getSizeY(); row++) {
      for (int col=0; col<getSizeX(); col++) {
        short val = (short) (bb.getBits(12) & 0xffff);
        int mapIndex = (row % 2) * 2 + (col % 2);

        int redOffset = row * getSizeX() + col;
        int greenOffset = (getSizeY() + row) * getSizeX() + col;
        int blueOffset = (2 * getSizeY() + row) * getSizeX() + col;

        switch (COLOR_MAP[mapIndex]) {
          case 0:
            pix[redOffset] = val;
            break;
          case 1:
            pix[greenOffset] = val;
            break;
          case 2:
            pix[blueOffset] = val;
            break;
        }
      }
    }

    ImageTools.interpolate(
      pix, plane, COLOR_MAP, getSizeX(), getSizeY(), isLittleEndian());

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
