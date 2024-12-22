/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * Reader for PreciPoint .vmic WSI files
 *
 * @author Kai Wiechen kai.wiechen at pathologie-worms.de
 */

public class VmicReader extends SubResolutionFormatReader {
  // TODO handle older .vmic files with INNER_CONTAINER = "Image"
  private static final String INNER_CONTAINER = "Image.vmici";
  private static File innerZipFile;
  private transient ZippedDeepZoomImageReader reader;
  private static FileSystem inner_zip;
  private static int resolutionLevels;
  private static int currentSeries;

  // -- Constructor --

  public VmicReader() {
    super("vmic", new String[] {"vmic"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = true;
    suffixSufficient = true;
  }

  // -- IFormatReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      try (ZipFile outerZipFile = new ZipFile(name)) {
        ZipEntry innerZipEntry = outerZipFile.getEntry(INNER_CONTAINER);
        try (InputStream innerZip = outerZipFile.getInputStream(innerZipEntry)) {
          // check inner zip file magic number 50 4B
          byte[] bytes = new byte[2];
          innerZip.read(bytes, 0, 2);
          if( bytes[0] == 0x50 && bytes[1] == 0x4B) return true;
          else return false;
        }
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
      }
    }
    return isThisType;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    Rectangle rect = new Rectangle(x, y, w, h);
    BufferedImage image = reader.readRegionOfLevel(rect, reader.getMaxLevel()); //currentSeries); // getRegion(rect, 1.0);

    byte[] t = AWTImageTools.getBytes(image, false);
    System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
  }

  /* @see IFormatReader#getResolutionCount() */
  /*@Override
  public int getResolutionCount() {
    FormatTools.assertId(currentId, true, 1);
    System.out.println("getResolutionCount");
    return resolutionLevels;
  }
*/
  /* @see IFormatReader#setResolution(int) */
 /* @Override
  public void setResolution(int no) {
    if (no < 0 || no >= getResolutionCount()) {
      throw new IllegalArgumentException("Invalid resolution: " + no);
    }
    if (!hasFlattenedResolutions()) {
      System.out.println("setResolution");
      resolution = resolutionLevels - no;
    }
  }*/

  /*@Override
  public void setSeries(int series) {
    super.setSeries(series);
    currentSeries = resolutionLevels - series;
  }*/

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return reader.getTileSize();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return reader.getTileSize();
  }// -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Path outer_zip = Path.of(id);

    try (FileSystem fs = FileSystems.newFileSystem(outer_zip)) {
      Path jar = fs.getPath(INNER_CONTAINER);
      inner_zip = FileSystems.newFileSystem(jar);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    reader = new ZippedDeepZoomImageReader(inner_zip);

    CoreMetadata m0 = core.get(0, 0);

    m0.interleaved = false;
    m0.littleEndian = false;

    m0.sizeX = reader.getWidth();
    m0.sizeY = reader.getHeight();
    m0.sizeZ = 1;
    m0.sizeT = 1;
    m0.sizeC = 3;
    m0.rgb = getSizeC() > 1;
    m0.imageCount = 1;
    m0.pixelType = FormatTools.UINT8;
    m0.dimensionOrder = "XYCZT";
    m0.metadataComplete = true;
    m0.indexed = false;
    //m0.resolutionCount = reader.getMaxLevel();

    /*resolutionLevels = m0.resolutionCount;
    for (int i = resolutionLevels - 1; i >= 0; i--) {
      CoreMetadata ms = new CoreMetadata(this, 0);
      core.add(0, ms);
      ms.sizeX = (int) Math.round(reader.getWidth() * reader.getZoomOfLevel(i));
      ms.sizeY = (int) Math.round(reader.getHeight() * reader.getZoomOfLevel(i));
      ms.sizeT = m0.sizeT;
      ms.imageCount = m0.imageCount;
      ms.thumbnail = true;
      ms.resolutionCount = 1;
    }*/

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    Length pixelsize = FormatTools.createLength((double) (1 / reader.getPixelPerMicron()), UNITS.MICROMETER);
    store.setPixelsPhysicalSizeX(pixelsize, 0);
    store.setPixelsPhysicalSizeY(pixelsize, 0);
  }
}
