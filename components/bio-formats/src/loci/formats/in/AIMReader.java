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
 * AIMReader is the file format reader for .aim files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/AIMReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/AIMReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset + FormatTools.getPlaneSize(this) * no);
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
    core[0].littleEndian = true;
    in.order(isLittleEndian());

    in.seek(56);

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    core[0].sizeZ = in.readInt();
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = getSizeZ();
    core[0].pixelType = FormatTools.INT16;
    core[0].dimensionOrder = "XYZCT";

    in.seek(160);

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
          date = DateTools.formatDate(value, "dd-MMM-yyyy HH:mm:ss.SS");
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
        if (size > 0) {
          store.setPixelsPhysicalSizeX(new PositiveFloat(size), 0);
        }
        else {
          LOGGER.warn(
            "Expected positive value for PhysicalSizeX; got {}", size);
        }
      }
      if (ySize != null && yLength != null) {
        Double size = yLength / ySize;
        if (size > 0) {
          store.setPixelsPhysicalSizeY(new PositiveFloat(size), 0);
        }
        else {
          LOGGER.warn(
            "Expected positive value for PhysicalSizeY; got {}", size);
        }
      }
      if (zSize != null && zLength != null) {
        Double size = zLength / zSize;
        if (size > 0) {
          store.setPixelsPhysicalSizeZ(new PositiveFloat(size), 0);
        }
        else {
          LOGGER.warn(
            "Expected positive value for PhysicalSizeZ; got {}", size);
        }
      }
    }
  }

}
