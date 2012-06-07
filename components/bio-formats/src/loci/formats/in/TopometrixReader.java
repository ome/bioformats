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
 * TopometrixReader is the file format reader for TopoMetrix .tfr, .ffr,
 * .zfr, .zfp, and .2fl files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/TopometrixReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/TopometrixReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TopometrixReader extends FormatReader {

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new TopoMetrix reader. */
  public TopometrixReader() {
    super("TopoMetrix", new String[] {"tfr", "ffr", "zfr", "zfp", "2fl"});
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 6;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    if (!check.startsWith("#R")) return false;
    try {
      Double.parseDouble(check.substring(2, 5));
    }
    catch (NumberFormatException e) { }
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset);
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

    in.skipBytes(2);
    int version = (int) Double.parseDouble(in.readString(4));
    in.skipBytes(2);
    pixelOffset = Long.parseLong(in.readString(4));
    in.skipBytes(2);

    long fp = in.getFilePointer();
    String date = in.readLine().trim();
    int commentLength = (int) (240 - in.getFilePointer() + fp);
    String comment = in.readString(commentLength).trim();

    if (version == 5) {
      in.seek(452);
    }

    in.skipBytes(152);

    core[0].sizeX = in.readShort();
    in.skipBytes(2);
    core[0].sizeY = in.readShort();

    double xSize = 0d, ySize = 0d;
    double adc = 0d, dacToWorldZero = 0d;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.skipBytes(10);
      if (version == 5) {
        in.skipBytes(4);
        xSize = in.readDouble();
        in.skipBytes(8);
        ySize = in.readDouble();
        adc = in.readDouble();
        dacToWorldZero = in.readDouble();

        in.skipBytes(1176);

        double sampleVolts = in.readDouble();
        double tunnelCurrent = in.readDouble();
        in.skipBytes(16);
        double timePerPixel = in.readDouble();
        in.skipBytes(40);
        double scanAngle = in.readDouble();

        addGlobalMeta("Sample volts", sampleVolts);
        addGlobalMeta("Tunnel current", tunnelCurrent);
        addGlobalMeta("Scan rate", timePerPixel);
        addGlobalMeta("Scan angle", scanAngle);
      }
      else {
        xSize = in.readFloat();
        in.skipBytes(4);
        ySize = in.readFloat();
        adc = in.readFloat();
        in.skipBytes(764);
        dacToWorldZero = in.readFloat();
      }

      addGlobalMeta("Version", version);
      addGlobalMeta("X size (in um)", xSize);
      addGlobalMeta("Y size (in um)", ySize);
      addGlobalMeta("ADC", adc);
      addGlobalMeta("DAC to world zero", dacToWorldZero);
      addGlobalMeta("Comment", comment);
      addGlobalMeta("Acquisition date", date);
    }

    core[0].pixelType = FormatTools.UINT16;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    date = DateTools.formatDate(date,
      new String[] {"MM/dd/yy HH:mm:ss", "MM/dd/yyyy HH:mm:ss"});
    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (xSize > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat((double) xSize / getSizeX()), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          (double) xSize / getSizeX());
      }
      if (ySize > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat((double) ySize / getSizeY()), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          (double) ySize / getSizeY());
      }
      store.setImageDescription(comment, 0);
    }
  }

}
