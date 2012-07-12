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
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * MolecularImagingReader is the file format reader for Molecular Imaging files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/MolecularImagingReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/MolecularImagingReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MolecularImagingReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "UK SOFT";
  private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

  // -- Fields --

  private long pixelOffset = 0;

  // -- Constructor --

  /** Constructs a new Molecular Imaging reader. */
  public MolecularImagingReader() {
    super("Molecular Imaging", "stp");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(MAGIC_STRING) > 0;
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

    core[0].sizeZ = 0;
    String date = null;
    double pixelSizeX = 0d, pixelSizeY = 0d;

    String line = in.readLine().trim();
    while (!line.endsWith("Data_section")) {
      int space = line.indexOf(" ");
      if (space != -1) {
        String key = line.substring(0, space).trim();
        String value = line.substring(space + 1).trim();
        addGlobalMeta(key, value);

        if (key.equals("samples_x")) {
          core[0].sizeX = Integer.parseInt(value);
        }
        else if (key.equals("samples_y")) {
          core[0].sizeY = Integer.parseInt(value);
        }
        else if (key.equals("buffer_id")) {
          core[0].sizeZ++;
        }
        else if (key.equals("Date")) {
          date = value;
        }
        else if (key.equals("time")) {
          date += " " + value;
        }
        else if (key.equals("length_x")) {
          pixelSizeX = Double.parseDouble(value) / getSizeX();
        }
        else if (key.equals("length_y")) {
          pixelSizeY = Double.parseDouble(value) / getSizeY();
        }
      }

      line = in.readLine().trim();
    }
    pixelOffset = in.getFilePointer();

    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = getSizeZ();
    core[0].rgb = false;
    core[0].pixelType = FormatTools.UINT16;
    core[0].littleEndian = true;
    core[0].dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    if (date != null) {
      date = DateTools.formatDate(date, DATE_FORMAT);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (pixelSizeX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSizeX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          pixelSizeX);
      }
      if (pixelSizeY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSizeY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          pixelSizeY);
      }
    }
  }

}
