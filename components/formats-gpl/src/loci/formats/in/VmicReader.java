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

public class VmicReader extends FormatReader {
  // TODO handle older .vmic files with INNER_CONTAINER = "Image"
  private static final String INNER_CONTAINER = "Image.vmici";
  private static File innerZipFile;
  private transient ZippedDeepZoomImageReader reader;
  private static ZipFile innerZipContainer;
  private static String prevId, prevInnerZip;

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
    BufferedImage image = reader.getRegion(rect, 1.0);

    byte[] t = AWTImageTools.getBytes(image, false);
    System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);

//    if (innerZipFile != null && innerZipContainer != null) {
//      innerZipContainer.close();
//      innerZipFile.delete();
//      innerZipContainer = null;
//      innerZipFile = null;
//    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    innerZipFile = File.createTempFile("Image", ".vmici");
    innerZipFile.deleteOnExit();

    try (ZipFile outerZipFile = new ZipFile(id)) {
      ZipEntry innerZipEntry = outerZipFile.getEntry(INNER_CONTAINER);

      try (InputStream innerZip = outerZipFile.getInputStream(innerZipEntry)) {
        OutputStream out = new FileOutputStream(innerZipFile);
        byte[] buffer = new byte[8192];
        int len;
        while ((len = innerZip.read(buffer)) > 0) {
          out.write(buffer, 0, len);
        }
        out.close();
      }
    }

   /* if (prevId == null) {
      prevId = id;
      prevInnerZip = innerZipFile.getName();
    }

    if (! id.equals(prevId)) {
      File f = new File(prevInnerZip);
      f.delete();
      prevId = id;
      prevInnerZip = innerZipFile.getName();
    }*/

    innerZipContainer = new ZipFile(innerZipFile);
    reader = new ZippedDeepZoomImageReader(innerZipContainer);

    CoreMetadata m = core.get(0);

    m.interleaved = false;
    m.littleEndian = false;

    m.sizeX = reader.getWidth();
    m.sizeY = reader.getHeight();
    m.sizeZ = 1;
    m.sizeT = 1;
    m.sizeC = 3;
    m.rgb = getSizeC() > 1;
    m.imageCount = 1;
    m.pixelType = FormatTools.UINT8;
    m.dimensionOrder = "XYCZT";
    m.metadataComplete = true;
    m.indexed = false;
    //m.resolutionCount = reader.getMaxLevel();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    Length pixelsize = FormatTools.createLength((double) (1 / reader.getPixelPerMicron()), UNITS.MICROMETER);
    store.setPixelsPhysicalSizeX(pixelsize, 0);
    store.setPixelsPhysicalSizeY(pixelsize, 0);
  }
}
