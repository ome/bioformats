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
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;

/**
 * HRDGDFReader is the file format reader for the Gridded Data Format used
 * by NOAA's Hurricane Research Division.
 *
 * These files do not technically contain image data, but do contain more
 * or less tabular data (surface wind speeds) that might be interesting to
 * visualize as an image.  Note that each surface wind component contains
 * two values (the east-west wind speed and the north-south wind speed); these
 * two values are encoded as channels, so that channel 0 represents east-west
 * speed and channel 1 represents north-south speed.
 *
 * @see <a href=http://www.aoml.noaa.gov/hrd/data_sub/wind.html>http://www.aoml.noaa.gov/hrd/data_sub/wind.html</a>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class HRDGDFReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "SURFACE WIND COMPONENTS";

  // -- Fields --

  private double[][] surfaceWind;

  // -- Constructor --

  /** Constructs a new Gridded Data Format reader. */
  public HRDGDFReader() {
    super("NOAA-HRD Gridded Data Format", "");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.equals(MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int nextBufIndex = 0;

    for (int row=y; row<h+y; row++) {
      for (int col=x; col<w+x; col++) {
        long v =
          Double.doubleToLongBits(surfaceWind[no][row * getSizeX() + col]);
        DataTools.unpackBytes(v, buf, nextBufIndex, 8, isLittleEndian());
        nextBufIndex += 8;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      surfaceWind = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String[] data = DataTools.readFile(id).split("[\r\n]");

    String hurricane = data[0].substring(data[0].lastIndexOf(" ") + 1);

    // size stored in kilometers
    String pixelSize =
      data[1].substring(data[1].indexOf(' ') + 1, data[1].lastIndexOf(" "));
    Double physicalSize = new Double(pixelSize) * 1000000000.0;

    // parse the center coordinates
    String centerLine = data[2];
    centerLine = centerLine.replaceAll("STORM CENTER LOCALE IS ", "");
    String[] center = centerLine.split(" ");
    Double centerLongitude = new Double(center[0]);
    Double centerLatitude = new Double(center[5]);

    // skip ahead to the surface wind section

    int lineNumber = 3;
    while (!data[lineNumber++].startsWith("SURFACE WIND COMPONENTS"));

    String dims = data[lineNumber++].trim();
    String x = dims.substring(0, dims.indexOf(' ')).trim();
    String y = dims.substring(dims.indexOf(' ') + 1).trim();
    surfaceWind = new double[2][Integer.parseInt(y) * Integer.parseInt(x)];

    int pixIndex = 0;

    while (lineNumber < data.length) {
      String line = data[lineNumber++];

      while (line.indexOf('(') != -1) {
        int end = line.indexOf(')');

        String pixel = line.substring(line.indexOf('(') + 1, end);
        line = line.substring(end + 1);

        int comma = pixel.indexOf(',');

        surfaceWind[0][pixIndex] = new Double(pixel.substring(0, comma).trim());
        surfaceWind[1][pixIndex] =
          new Double(pixel.substring(comma + 1).trim());
        pixIndex++;
      }
    }

    addGlobalMeta("Hurricane", hurricane);
    addGlobalMeta("DX (kilometers)", pixelSize);
    addGlobalMeta("DY (kilometers)", pixelSize);
    addGlobalMeta("Storm center (Latitude)", centerLatitude);
    addGlobalMeta("Storm center (Longitude)", centerLongitude);

    CoreMetadata m = core.get(0);

    m.sizeX = Integer.parseInt(x);
    m.sizeY = Integer.parseInt(y);
    m.sizeC = 2;
    m.rgb = false;
    m.pixelType = FormatTools.DOUBLE;
    m.sizeZ = 1;
    m.sizeT = 1;
    m.imageCount = getSizeC() * getSizeZ() * getSizeT();
    m.indexed = false;
    m.littleEndian = false;
    m.dimensionOrder = "XYCTZ";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Length sizeX = FormatTools.getPhysicalSizeX(physicalSize);
      Length sizeY = FormatTools.getPhysicalSizeY(physicalSize);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
