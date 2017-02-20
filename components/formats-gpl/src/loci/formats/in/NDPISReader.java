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

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.*;
import loci.formats.meta.MetadataStore;

import java.io.IOException;
import java.util.ArrayList;



/**
 * NDPISReader is the file format reader for Hamamatsu .ndpis files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Manuel Stritt (manuel.stritt at actelion.com)
 */
public class NDPISReader extends FormatReader {

  // -- Fields --

  private String[] ndpiFiles;
  private NDPIReader[] readers;
  private final static int CHANNELTAG = 65434;

  // -- Constructor --

  /** Constructs a new NDPIS reader. */
  public NDPISReader() {
    super("Hamamatsu NDPIS", "ndpis");
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    datasetDescription = "One .ndpis file and at least one .ndpi file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return readers[0].getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return readers[0].getOptimalTileHeight();
  }


  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
          throws FormatException, IOException
  {
    int ch = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    byte[] newBuffer;
    try {
      newBuffer = DataTools.allocate(w, h, ch, bpp);
    }
    catch (IllegalArgumentException e) {
      throw new FormatException("Image plane too large. Only 2GB of data can " +
              "be extracted at one time. You can workaround the problem by opening " +
              "the plane in tiles; for further details, see: " +
              "http://www.openmicroscopy.org/site/support/bio-formats/about/" +
              "bug-reporting.html#common-issues-to-check", e);
    }
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
          throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);
    int channel = zct[1];
    readers[channel].setId(ndpiFiles[channel]);
    readers[channel].setSeries(getSeries());
    readers[channel].setResolution(getResolution());
    byte[] bufRGB = DataTools.allocate(w, h, 3); // w*h*RGB  - maybe this should be reused...
    bufRGB = readers[channel].openBytes(0, bufRGB, x, y, w, h);
    int intens;
    // each channel is RGB data (with usually only one band used), thus we sum up the intensities
    if (readers[channel].isInterleaved()) {
      for (int i = 0; i < buf.length; i++) {
        intens = ((int)bufRGB[i * 3] & 0xff) + ((int)bufRGB[i * 3 + 1] & 0xff) + ((int)bufRGB[i * 3 + 2] & 0xff);
        buf[i] = (byte) (intens <= 255 ? intens : 255);   // clamp to byte
      }
    } else {    // not interleaved
      int offs = w*h;
      for (int i = 0; i < buf.length; i++) {
        intens = ((int)bufRGB[i] & 0xff) + ((int)bufRGB[i + offs] & 0xff) + ((int)bufRGB[i + offs *2] & 0xff);
        buf[i] = (byte) (intens <= 255 ? intens : 255);   // clamp to byte
      }
    }

    return buf;
  }


  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return new String[] {currentId};
    }
    String[] files = new String[ndpiFiles.length + 1];
    files[0] = currentId;
    System.arraycopy(ndpiFiles, 0, files, 1, ndpiFiles.length);
    return files;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      ndpiFiles = null;
      if (readers != null) {
        for (NDPIReader reader : readers) {
          if (reader != null) {
            reader.close();
          }
        }
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location parent = new Location(id).getAbsoluteFile().getParentFile();

    String[] lines = DataTools.readFile(currentId).split("\r\n");

    for (String line : lines) {
      int eq = line.indexOf('=');
      if (eq < 0) {
        continue;
      }
      String key = line.substring(0, eq).trim();
      String value = line.substring(eq + 1).trim();

      if (key.equals("NoImages")) {
        ndpiFiles = new String[Integer.parseInt(value)];
        readers = new NDPIReader[ndpiFiles.length];
      }
      else if (key.startsWith("Image")) {
        int index = Integer.parseInt(key.replaceAll("Image", ""));
        ndpiFiles[index] = new Location(parent, value).getAbsolutePath();
        readers[index] = new NDPIReader();
        readers[index].setFlattenedResolutions(hasFlattenedResolutions());
      }
    }

    readers[0].setMetadataStore(getMetadataStore());
    readers[0].setId(ndpiFiles[0]);

    core = new ArrayList<CoreMetadata>(readers[0].getCoreMetadataList());
    for (int i=0; i<core.size(); i++) {
      CoreMetadata ms = core.get(i);
      ms.sizeC = readers.length;
      ms.rgb = false;
      ms.imageCount = ms.sizeC * ms.sizeZ * ms.sizeT;
    }

    MetadataStore store = makeFilterMetadata();
    for (int c=0; c<readers.length; c++) {     // populate channel names based on IFD entry
      if (c>0) // 0 is already open
        readers[c].setId(ndpiFiles[c]);
      String channelName = readers[c].getIFDs().get(0).getIFDStringValue(CHANNELTAG);
      store.setChannelName(channelName, getSeries(), c);
    }
    MetadataTools.populatePixels(store, this);
  }



}
