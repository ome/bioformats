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
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * AIMReader is the file format reader for .aim files.
 */
public class AIMReader extends FormatReader {

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new AIM reader. */
  public AIMReader() {
    super("AIM", "aim");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = FormatTools.getPlaneSize(this);
    long offset = pixelOffset + planeSize * (long) no;

    if (offset < in.length()) {
      in.seek(offset);
      readPlane(in, x, y, w, h, buf);
    }
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

    m.littleEndian = true;
    in.order(isLittleEndian());

    // check for newer version of AIM format with wider offsets

    String version = in.readString(16);
    boolean widerOffsets = version.startsWith("AIMDATA_V030");

    if (widerOffsets) {
      in.seek(96);

      m.sizeX = (int) in.readLong();
      m.sizeY = (int) in.readLong();
      m.sizeZ = (int) in.readLong();
      in.seek(280);
    }
    else {
      in.seek(56);

      m.sizeX = in.readInt();
      m.sizeY = in.readInt();
      m.sizeZ = in.readInt();
      in.seek(160);
    }

    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = getSizeZ();
    m.pixelType = FormatTools.INT16;
    m.dimensionOrder = "XYZCT";

    String processingLog = in.readCString();
    pixelOffset = in.getFilePointer();

    String date = null;
    Double xSize = null, xLength = null;
    Double ySize = null, yLength = null;
    Double zSize = null, zLength = null;

    String[] lines = processingLog.split("\n");
    for (String line : lines) {
      line = line.trim();
      int split = line.indexOf("  ");
      if (split > 0) {
        String key = line.substring(0, split).trim();
        String value = line.substring(split).trim();

        addGlobalMeta(key, value);

        if (key.equals("Original Creation-Date")) {
          date = DateTools.formatDate(value, "dd-MMM-yyyy HH:mm:ss", ".");
        }
        else if (key.equals("Orig-ISQ-Dim-p")) {
          String[] tokens = value.split(" ");
          for (String token : tokens) {
            token = token.trim();
            if (token.length() > 0) {
              if (xSize == null) {
                xSize = new Double(token);
              }
              else if (ySize == null) {
                ySize = new Double(token);
              }
              else if (zSize == null) {
                zSize = new Double(token);
              }
            }
          }
        }
        else if (key.equals("Orig-ISQ-Dim-um")) {
          String[] tokens = value.split(" ");
          for (String token : tokens) {
            token = token.trim();
            if (token.length() > 0) {
              if (xLength == null) {
                xLength = new Double(token);
              }
              else if (yLength == null) {
                yLength = new Double(token);
              }
              else if (zLength == null) {
                zLength = new Double(token);
              }
            }
          }
        }
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (xSize != null && xLength != null) {
        Double size = xLength / xSize;
        Length physicalSize = FormatTools.getPhysicalSizeX(size);
        if (physicalSize != null) {
          store.setPixelsPhysicalSizeX(physicalSize, 0);
        }
      }
      if (ySize != null && yLength != null) {
        Double size = yLength / ySize;
        Length physicalSize = FormatTools.getPhysicalSizeY(size);
        if (physicalSize != null) {
          store.setPixelsPhysicalSizeY(physicalSize, 0);
        }
      }
      if (zSize != null && zLength != null) {
        Double size = zLength / zSize;
        Length physicalSize = FormatTools.getPhysicalSizeZ(size);
        if (physicalSize != null) {
          store.setPixelsPhysicalSizeZ(physicalSize, 0);
        }
      }
    }
  }

}
