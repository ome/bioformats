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
/*!
* \file         WlzReader.java
* \author       Bill Hill
* \date         June 2013
* \version      $Id$
* \par
* Address:
*               MRC Human Genetics Unit,
*               MRC Institute of Genetics and Molecular Medicine,
*               University of Edinburgh,
*               Western General Hospital,
*               Edinburgh, EH4 2XU, UK.
* \par
* Copyright (C), [2013 - 2014],
* The University Court of the University of Edinburgh,
* Old College, Edinburgh, UK.
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be
* useful but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
* PURPOSE.  See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public
* License along with this program; if not, write to the Free
* Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
* Boston, MA  02110-1301, USA.
* \brief	Woolz reader for bioformats.
*/

package loci.formats.in;

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.services.WlzService;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

/**
 * WlzReader is a file format reader for Woolz files.
 * Woolz is available from: https://github.com/ma-tech/Woolz
 */
public class WlzReader extends FormatReader {

  // -- Constants --

  // -- Static initializers --

  static {
  }

  // -- Fields --

  private transient WlzService wlz = null;

  public static final String NO_WLZ_MSG =
    "\n" +
    "Woolz is required to read and write Woolz objects.\n" +
    "Please obtain the necessary JAR and native library files from:\n" +
    "http://www.emouseatlas.org/emap/analysis_tools_resources/software/woolz.html.\n" +
    "The source code for these is also available from:\n" +
    "https://github.com/ma-tech/Woolz.";

  // -- Constructor --

  public WlzReader() {
    super("Woolz", new String[] {"wlz"});
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String file, boolean open) {
    return super.isThisType(file, open);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (wlz != null) {
      buf = wlz.readBytes(no, buf, x, y, w, h);
    }
    else {
      try {
        ServiceFactory factory = new ServiceFactory();
        wlz = factory.getInstance(WlzService.class);
      }
      catch (DependencyException e) {
        throw new FormatException(NO_WLZ_MSG, e);
      }
      if (wlz != null) {
        wlz.open(currentId, "r");
        buf = wlz.readBytes(no, buf, x, y, w, h);
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    try {
      ServiceFactory factory = new ServiceFactory();
      wlz = factory.getInstance(WlzService.class);
    }
    catch (DependencyException e) {
      throw new FormatException(NO_WLZ_MSG, e);
    }
    if (wlz != null) {
      wlz.open(id, "r");
      CoreMetadata md = core.get(0);
      MetadataStore store = makeFilterMetadata();
      md.rgb = wlz.isRGB();
      md.interleaved = false;
      md.indexed = false;
      md.sizeX = wlz.getSizeX();
      md.sizeY = wlz.getSizeY();
      md.sizeZ = wlz.getSizeZ();
      md.sizeC = wlz.getSizeC();
      md.sizeT = wlz.getSizeT();
      md.dimensionOrder = "XYZCT";
      md.imageCount = wlz.getSizeZ();
      md.pixelType = wlz.getPixelType();
      PositiveFloat x = new PositiveFloat(Math.abs(wlz.getVoxSzX()));
      PositiveFloat y = new PositiveFloat(Math.abs(wlz.getVoxSzY()));
      PositiveFloat z = new PositiveFloat(Math.abs(wlz.getVoxSzZ()));
      store.setPixelsPhysicalSizeX(x, 0);
      store.setPixelsPhysicalSizeY(y, 0);
      store.setPixelsPhysicalSizeZ(z, 0);
      store.setStageLabelName(wlz.getWlzOrgLabelName(), 0);
      store.setStageLabelX((double )(wlz.getOrgX()), 0);
      store.setStageLabelY((double )(wlz.getOrgY()), 0);
      store.setStageLabelZ((double )(wlz.getOrgZ()), 0);
      MetadataTools.populatePixels(store, this);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (wlz != null) {
        wlz.close();
        wlz = null;
      }
    }
  }

  // -- Helper methods --

}
