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

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * SBIGReader is the file format reader for SBIG files.
 * See the specification at http://diffractionlimited.com/support/sbig-archives/.
 */
public class SBIGReader extends FormatReader {

  // -- Constants --

  private static final long HEADER_SIZE = 2048;
  private static final String DATE_FORMAT = "MM/dd/yy HH:mm:ss";

  // -- Fields --

  private boolean compressed;

  // -- Constructor --

  /** Constructs a new SBIG reader. */
  public SBIGReader() {
    super("SBIG", "");
    domains = new String[] {FormatTools.ASTRONOMY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = (int) HEADER_SIZE;
    final int checkLen = 32;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(checkLen).indexOf("ST-7 Compressed Image") >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    in.seek(HEADER_SIZE);
    int width = getSizeX() * 2;

    if (compressed) {
      byte[] b = new byte[FormatTools.getPlaneSize(this)];
      for (int row=0; row<getSizeY(); row++) {
        int rowLen = in.readShort();
        if (rowLen == width) {
          in.read(b, row * width, rowLen);
        }
        else {
          int bufferPointer = row * width;
          in.read(b, bufferPointer, 2);
          while (bufferPointer - row * width < width - 2) {
            short prevPixel =
              DataTools.bytesToShort(b, bufferPointer, isLittleEndian());
            bufferPointer += 2;
            byte check = in.readByte();
            if (check == -128) {
              in.read(b, bufferPointer, 2);
            }
            else {
              prevPixel += check;
              DataTools.unpackBytes(prevPixel, b, bufferPointer, 2,
                isLittleEndian());
            }
          }
        }
      }
      RandomAccessInputStream stream = new RandomAccessInputStream(b);
      readPlane(stream, x, y, w, h, buf);
      stream.close();
    }
    else readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      compressed = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    Double temperature = null;
    Double sizeX = null, sizeY = null;
    String date = null, description = null;

    String[] lines = DataTools.readFile(currentId).split("\n");
    for (String line : lines) {
      line = line.trim();
      int eq = line.indexOf('=');
      if (eq != -1) {
        String key = line.substring(0, eq).trim();
        String value = line.substring(eq + 1).trim();
        addGlobalMeta(key, value);

        if (key.equals("Width")) {
          m.sizeX = Integer.parseInt(value);
        }
        else if (key.equals("Height")) {
          m.sizeY = Integer.parseInt(value);
        }
        else if (key.equals("Note")) {
          description = value;
        }
        else if (key.equals("X_pixel_size")) {
          sizeX = new Double(value) * 1000;
        }
        else if (key.equals("Y_pixel_size")) {
          sizeY = new Double(value) * 1000;
        }
        else if (key.equals("Date")) {
          date = value;
        }
        else if (key.equals("Time")) {
          date += " " + value;
        }
      }
      else if (line.indexOf("Compressed") != -1) {
        compressed = true;
      }
      else if (line.equals("End")) break;
    }

    m.pixelType = FormatTools.UINT16;
    m.littleEndian = true;
    m.rgb = false;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = 1;
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
      Length x = FormatTools.getPhysicalSizeX(sizeX);
      Length y = FormatTools.getPhysicalSizeY(sizeY);
      if (x != null) {
        store.setPixelsPhysicalSizeX(x, 0);
      }
      if (y != null) {
        store.setPixelsPhysicalSizeY(y, 0);
      }
      store.setImageDescription(description, 0);
    }
  }

}
