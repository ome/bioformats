/*!
* \file         WlzWriter.java
* \author       Bill Hill
* \date         July 2013
* \version      $Id$
* \par
* Address:
*               MRC Human Genetics Unit,
*               MRC Institute of Genetics and Molecular Medicine,
*               University of Edinburgh,
*               Western General Hospital,
*               Edinburgh, EH4 2XU, UK.
* \par
* Copyright (C), [2013],
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
* \brief	Woolz writer for bioformats.
*/

package loci.formats.out;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.MissingLibraryException;
import loci.formats.services.WlzService;

/**
 * WlzWriter is the file format writer for Woolz files.
 */
public class WlzWriter extends FormatWriter {

  // -- Fields --

  private WlzService wlz = null;

  public static final String NO_WLZ_MSG =
    "\n" +
    "Woolz is required to read and write Woolz objects.\n" +
    "Please obtain the necessary JAR and native library files from:\n" +
    "http://www.emouseatlas.org/emap/analysis_tools_resources/software/woolz.html.\n" +
    "The source code for these is also available from:\n" +
    "https://github.com/ma-tech/Woolz.";

  private String outputOrder = "XYZCT";

  // -- Constructor --

  public WlzWriter() {
    super("Woolz", new String[] {"wlz"});
  }

  // -- ICSWriter API methods --

  /**
   * Set the order in which dimensions should be written to the file.
   * Valid values are specified in the documentation for
   * {@link loci.formats.IFormatReader#getDimensionOrder()}
   */
  public void setOutputOrder(String outputOrder) {
    this.outputOrder = outputOrder;
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if(wlz != null) {
      checkParams(no, buf, x, y, w, h);
      wlz.saveBytes(no, buf, x, y, w, h);
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() {
    return true;
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    int[] spt;
    if(wlz != null) {
      spt = wlz.getSupPixelTypes();
    }
    else {
      spt  = new int[] {};
    }
    return(spt);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    try {
      ServiceFactory factory = new ServiceFactory();
      wlz = factory.getInstance(WlzService.class);
    }
    catch (DependencyException e) {
      throw new FormatException(NO_WLZ_MSG, e);
    }
    if(wlz != null) {
      MetadataRetrieve meta = getMetadataRetrieve();
      MetadataTools.verifyMinimumPopulated(meta, series);
      wlz.open(id, "w");
      String stageLabelName = meta.getStageLabelName(0);
      int oX = 0;
      int oY = 0;
      int oZ = 0;
      if((stageLabelName != null) &&
         stageLabelName.equals(wlz.getWlzOrgLabelName())) {
	oX = (int )Math.rint(meta.getStageLabelX(0));
        oY = (int )Math.rint(meta.getStageLabelY(0));
        oZ = (int )Math.rint(meta.getStageLabelZ(0));
      }
      int nX = meta.getPixelsSizeX(series).getValue().intValue();
      int nY = meta.getPixelsSizeY(series).getValue().intValue();
      int nZ = meta.getPixelsSizeZ(series).getValue().intValue();
      int nC = meta.getPixelsSizeC(series).getValue().intValue();
      int nT = meta.getPixelsSizeT(series).getValue().intValue();
      double vX = 1.0;
      double vY = 1.0;
      double vZ = 1.0;
      if(meta.getPixelsPhysicalSizeX(0) != null) {
        vX = meta.getPixelsPhysicalSizeX(0).getValue();
      }
      if(meta.getPixelsPhysicalSizeY(0) != null) {
        vY = meta.getPixelsPhysicalSizeY(0).getValue();
      }
      if(meta.getPixelsPhysicalSizeZ(0) != null) {
        vZ = meta.getPixelsPhysicalSizeZ(0).getValue();
      }
      int gType = FormatTools.pixelTypeFromString(
		  meta.getPixelsType(series).toString());
      wlz.setupWrite(oX, oY, oZ, nX, nY, nZ, nC, nT, vX, vY, vZ, gType);
    }
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if(wlz != null) {
      wlz.close();
    }
  }

  // -- Helper methods --

}
