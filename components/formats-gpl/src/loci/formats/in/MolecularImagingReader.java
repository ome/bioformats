/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * MolecularImagingReader is the file format reader for Molecular Imaging files.
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
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(MAGIC_STRING) > 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    in.seek(pixelOffset + no * FormatTools.getPlaneSize(this));
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);

    m.sizeZ = 0;
    String date = null;
    double pixelSizeX = 0d, pixelSizeY = 0d;

    String data = in.findString("Data_section  \r\n");
    String[] lines = data.split("\n");
    for (String line : lines) {
      line = line.trim();

      int space = line.indexOf(' ');
      if (space != -1) {
        String key = line.substring(0, space).trim();
        String value = line.substring(space + 1).trim();
        addGlobalMeta(key, value);

        if (key.equals("samples_x")) {
          m.sizeX = Integer.parseInt(value);
        }
        else if (key.equals("samples_y")) {
          m.sizeY = Integer.parseInt(value);
        }
        else if (key.equals("buffer_id")) {
          m.sizeZ++;
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
    }
    pixelOffset = in.getFilePointer();

    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = getSizeZ();
    m.rgb = false;
    m.pixelType = FormatTools.UINT16;
    m.littleEndian = true;
    m.dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    if (date != null) {
      date = DateTools.formatDate(date, DATE_FORMAT);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
