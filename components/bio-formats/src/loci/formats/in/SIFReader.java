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

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * SIFReader is the file format reader for Andor SIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SIFReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SIFReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SIFReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "Andor Technology";

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new SIF reader. */
  public SIFReader() {
    super("Andor SIF", "sif");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset + no * FormatTools.getPlaneSize(this));
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    double[] timestamp = null;

    int lineNumber = 1;
    String line = in.readLine();
    int endLine = -1;
    while (endLine < 0 || lineNumber < endLine) {
      lineNumber++;

      if (line.startsWith("Pixel number")) {
        String[] tokens = line.split(" ");
        if (tokens.length > 2) {
          m.sizeC = Integer.parseInt(tokens[2]);
          m.sizeX = Integer.parseInt(tokens[3]);
          m.sizeY = Integer.parseInt(tokens[4]);
          m.sizeZ = Integer.parseInt(tokens[5]);
          m.sizeT = Integer.parseInt(tokens[6]);
          m.imageCount = getSizeZ() * getSizeT() * getSizeC();
          timestamp = new double[getImageCount()];
          endLine = lineNumber + getImageCount();
        }
      }
      else if (lineNumber < endLine) {
        int index = lineNumber - (endLine - getImageCount()) - 1;
        if (index >= 0) {
          try {
            timestamp[index] = Double.parseDouble(line.trim());
          }
          catch (NumberFormatException e) {
            LOGGER.debug("Could not parse timestamp #" + index, e);
          }
        }
      }
      else {
        addGlobalMeta("Line #" + (lineNumber - 1), line.trim());
      }
      line = in.readLine();
    }
    pixelOffset = in.getFilePointer();

    m.pixelType = FormatTools.FLOAT;
    m.dimensionOrder = "XYCZT";
    m.littleEndian = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this,
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneDeltaT(timestamp[i], 0, i);
      }
    }
  }

}
