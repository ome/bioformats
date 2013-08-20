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

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;

import uk.ac.mrc.hgu.Wlz.WlzException;
import uk.ac.mrc.hgu.Wlz.WlzFileStream;
import uk.ac.mrc.hgu.Wlz.WlzFileOutputStream;
import uk.ac.mrc.hgu.Wlz.WlzIBox3;
import uk.ac.mrc.hgu.Wlz.WlzIVertex2;
import uk.ac.mrc.hgu.Wlz.WlzDVertex3;
import uk.ac.mrc.hgu.Wlz.WlzIVertex3;
import uk.ac.mrc.hgu.Wlz.WlzObject;
import uk.ac.mrc.hgu.Wlz.WlzGreyType;
import uk.ac.mrc.hgu.Wlz.WlzObjectType;

/**
 * WlzWriter is the file format writer for Woolz files.
 */
public class WlzWriter extends FormatWriter {

  // -- Fields --

  private int           objType = WlzObjectType.WLZ_NULL;
  /* objGType is the Woolz value type, but may be WLZ_GREY_ERROR to indicate
   * an object with no values, just a domain. */
  private int		objGType = WlzGreyType.WLZ_GREY_ERROR;
  private String	wlzVersion = new String("unknown");
  private WlzIBox3	bBox = null;
  private WlzObject	wlzObj = null;
  private WlzFileStream	wlzFP = null;
  private WlzDVertex3 	voxSz = new WlzDVertex3(1.0, 1.0, 1.0);

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
   *
   * By default, the ordering is "XYZTC".
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
    WlzIVertex3 og = new WlzIVertex3(x + bBox.xMin, y + bBox.yMin,
                                     no + bBox.zMin);
    WlzIVertex2 sz = new WlzIVertex2(w, h);

    checkParams(no, buf, x, y, w, h);
    try {
      wlzObj = WlzObject.WlzBuildObj3B(wlzObj, og, sz, objGType,
                                       buf.length, buf);
    }
    catch (WlzException e) {
      throw new FormatException("Failed save bytes to Woolz object", e);
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() {
    return true;
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.UINT8, FormatTools.INT16,
      	              FormatTools.INT32, FormatTools.FLOAT,
		      FormatTools.DOUBLE};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    MetadataRetrieve meta = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(meta, series);

    String v[] = new String[1];
    WlzObject.WlzGetVersion(v);
    wlzVersion = new String(v[0]);
    bBox = new WlzIBox3();

    /* TODO somehow set the bounding box using the WoolzOrigin stage
     * label if it exists. */

    bBox.xMax += meta.getPixelsSizeX(series).getValue().intValue() - 1;
    bBox.yMax += meta.getPixelsSizeY(series).getValue().intValue() - 1;
    bBox.zMax += meta.getPixelsSizeZ(series).getValue().intValue() - 1;
    int nC = meta.getPixelsSizeC(series).getValue().intValue();
    int nT = meta.getPixelsSizeT(series).getValue().intValue();
    if((bBox.xMax < bBox.xMin) ||
       (bBox.yMax < bBox.yMin) ||
       (bBox.zMax < bBox.zMin) ||
       (nC <= 0) ||
       (nT <= 0)) {
      throw new FormatException("Invalid image size (" +
				(bBox.xMax - bBox.xMin + 1) + ", " +
				(bBox.yMax - bBox.yMin + 1) + ", " +
				(bBox.zMax - bBox.zMin + 1) + ", " +
				nC + ", " +
				nT + ")");
    }
    int gType = FormatTools.pixelTypeFromString(
		    meta.getPixelsType(series).toString());
    switch(gType) {
      case FormatTools.UINT8:
	objGType = WlzGreyType.WLZ_GREY_UBYTE;
	break;
      case FormatTools.INT16:
	objGType = WlzGreyType.WLZ_GREY_SHORT;
	break;
      case FormatTools.INT32:
	objGType = WlzGreyType.WLZ_GREY_INT;
	break;
      case FormatTools.FLOAT:
	objGType = WlzGreyType.WLZ_GREY_FLOAT;
	break;
      case FormatTools.DOUBLE:
	objGType = WlzGreyType.WLZ_GREY_DOUBLE;
	break;
      default:
	throw new FormatException("Invalid image value type");
    }
    if(bBox.zMax == bBox.zMin) {
      objType = WlzObjectType.WLZ_2D_DOMAINOBJ;
    }
    else {
      objType = WlzObjectType.WLZ_3D_DOMAINOBJ;
      if(meta.getPixelsPhysicalSizeX(0) != null) {
        voxSz.vtX = meta.getPixelsPhysicalSizeX(0).getValue();
      }
      if(meta.getPixelsPhysicalSizeY(0) != null) {
        voxSz.vtY = meta.getPixelsPhysicalSizeY(0).getValue();
      }
      if(meta.getPixelsPhysicalSizeZ(0) != null) {
        voxSz.vtZ = meta.getPixelsPhysicalSizeZ(0).getValue();
      }
    }
    try {
      wlzObj = WlzObject.WlzMakeEmpty();
    }
    catch (WlzException e) {
      throw new FormatException("Failed to create Woolz object", e);
    }
    try {
      wlzFP = new WlzFileOutputStream(id);
    }
    catch (IOException e) {
      throw new IOException("Failed to write " + id +" (" + e + ")");
    }
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if((wlzFP != null) && (wlzObj != null)) {
      try {
	if(objType == WlzObjectType.WLZ_3D_DOMAINOBJ) {
	  WlzObject.WlzSetVoxelSize(wlzObj, voxSz.vtX, voxSz.vtY, voxSz.vtZ);
	}
	WlzObject.WlzWriteObj(wlzFP, wlzObj);
      }
      catch(WlzException e) {
	throw new IOException("Failed to write to Woolz object (" + e + ")");
      }
    }
    if(wlzFP != null) {
      wlzFP.close();
      wlzFP = null;
    }
    if(wlzObj != null) {
      // Object is freed by garbage collection.
      wlzObj = null;
    }
  }

  // -- Helper methods --

}
