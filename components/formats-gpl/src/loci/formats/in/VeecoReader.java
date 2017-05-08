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

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.NetCDFService;

/**
 * VeecoReader is the file format reader for Veeco HDF files.
 */
public class VeecoReader extends FormatReader {

  // -- Fields --

  private NetCDFService netcdf;
  private Object image;

  private boolean unpackEndian = true;

  // -- Constructor --

  /** Constructs a new Veeco reader. */
  public VeecoReader() {
    super("Veeco", "hdf");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (image instanceof byte[][]) {
      byte[][] byteImage = (byte[][]) image;
      for (int row=h+y-1; row>=y; row--) {
        System.arraycopy(byteImage[row], x, buf, (row - y) * w, w);
      }
    }
    else if (image instanceof short[][]) {
      short[][] shortImage = (short[][]) image;
      int output = 0;

      for (int row=h+y-1; row>=y; row--) {
        for (int col=x; col<x+w; col++) {
          DataTools.unpackBytes(
            shortImage[row][col], buf, output, 2, unpackEndian);
          output += 2;
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (netcdf != null) netcdf.close();
      image = null;
      unpackEndian = true;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    CoreMetadata m = core.get(0);

    try {
      ServiceFactory factory = new ServiceFactory();
      netcdf = factory.getInstance(NetCDFService.class);
      netcdf.setFile(id);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException(e);
    }

    Vector<String> variableList = netcdf.getVariableList();

    // a single variable containing the image data is expected

    if (variableList.size() == 0) {
      throw new FormatException("No image data found");
    }

    String imageName = variableList.get(0);
    try {
      image = netcdf.getVariableValue(imageName);
    }
    catch (ServiceException e) {
      throw new FormatException("Could not retrieve image data", e);
    }

    Hashtable<String, Object> attributes =
      netcdf.getVariableAttributes(imageName);

    for (String attr : attributes.keySet()) {
      addGlobalMeta(attr, attributes.get(attr));
    }

    if (image instanceof byte[][]) {
      byte[][] byteImage = (byte[][]) image;
      m.sizeX = byteImage[0].length;
      m.sizeY = byteImage.length;
      m.pixelType = FormatTools.INT8;
    }
    else if (image instanceof short[][]) {
      short[][] shortImage = (short[][]) image;
      m.sizeX = shortImage[0].length;
      m.sizeY = shortImage.length;
      m.pixelType = FormatTools.INT16;

      // set the endianness to use when unpacking pixels
      // NetCDF may not return the pixels with a constant endianness,
      // so this ensures that the reader corrects accordingly (see ticket 12085)

      short nativeMin = 0;
      short nativeMax = 0;
      short swappedMin = 0;
      short swappedMax = 0;
      for (int y=0; y<shortImage.length; y++) {
        for (int x=0; x<shortImage[y].length; x++) {
          if (shortImage[y][x] < nativeMin) {
            nativeMin = shortImage[y][x];
          }
          if (shortImage[y][x] > nativeMax) {
            nativeMax = shortImage[y][x];
          }
          short swapped = DataTools.swap(shortImage[y][x]);
          if (swapped < swappedMin) {
            swappedMin = swapped;
          }
          if (swapped > swappedMax) {
            swappedMax = swapped;
          }
        }
      }
      unpackEndian = nativeMin <= swappedMin && nativeMax >= swappedMax;
    }

    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = m.sizeZ * m.sizeC * m.sizeT;
    m.dimensionOrder = "XYCZT";
    m.littleEndian = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
