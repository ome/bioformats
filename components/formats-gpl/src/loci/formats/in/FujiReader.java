/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * FujiReader is the file format reader for Fuji LAS 3000 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/FujiReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/FujiReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class FujiReader extends FormatReader {

  // -- Constants --

  private static final String DATE_FORMAT = "ddd MMM dd HH:mm:ss yyyy";

  // -- Fields --

  private String infFile;
  private String pixelsFile;

  // -- Constructor --

  /** Constructs a new Fuji reader. */
  public FujiReader() {
    super("Fuji LAS 3000", new String[] {"img", "inf"});
    domains = new String[] {FormatTools.GEL_DOMAIN};
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false;
    if (!open) return false;

    String baseName = name.substring(0, name.lastIndexOf("."));

    if (checkSuffix(name, "inf")) {
      return new Location(baseName + ".img").exists();
    }
    else if (checkSuffix(name, "img")) {
      return new Location(baseName + ".inf").exists();
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    RandomAccessInputStream s = new RandomAccessInputStream(pixelsFile);
    readPlane(s, x, y, w, h, buf);
    s.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      return new String[] {infFile};
    }
    return new String[] {infFile, pixelsFile};
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      infFile = null;
      pixelsFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (checkSuffix(id, "inf")) {
      infFile = new Location(id).getAbsolutePath();
      pixelsFile = infFile.substring(0, infFile.lastIndexOf(".")) + ".img";
    }
    else {
      pixelsFile = new Location(id).getAbsolutePath();
      infFile = pixelsFile.substring(0, pixelsFile.lastIndexOf(".")) + ".inf";
    }

    String[] lines = DataTools.readFile(infFile).split("\r{0,1}\n");

    int bits = Integer.parseInt(lines[5]);

    CoreMetadata m = core.get(0);
    m.pixelType = FormatTools.pixelTypeFromBytes(bits / 8, false, false);

    m.sizeX = Integer.parseInt(lines[6]);
    m.sizeY = Integer.parseInt(lines[7]);

    m.sizeC = 1;
    m.sizeT = 1;
    m.sizeZ = 1;
    m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    m.dimensionOrder = "XYCZT";

    for (String line : lines) {
      addGlobalMetaList("Line", line);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String imageName = lines[1];
    String timestamp = lines[10];
    timestamp = DateTools.formatDate(timestamp, DATE_FORMAT);

    store.setImageName(imageName, 0);
    if (timestamp != null) {
      store.setImageAcquisitionDate(new Timestamp(timestamp), 0);
    }

    double physicalWidth = Double.parseDouble(lines[3]);
    double physicalHeight = Double.parseDouble(lines[4]);

    PositiveFloat sizeX = FormatTools.getPhysicalSizeX(physicalWidth);
    PositiveFloat sizeY = FormatTools.getPhysicalSizeY(physicalHeight);

    if (sizeX != null) {
      store.setPixelsPhysicalSizeX(sizeX, 0);
    }
    if (sizeY != null) {
      store.setPixelsPhysicalSizeY(sizeY, 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String instrument = lines[13];

      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setMicroscopeModel(instrument, 0);
    }
  }

}
