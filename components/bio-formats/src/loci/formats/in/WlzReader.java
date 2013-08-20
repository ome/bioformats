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
* \brief	Woolz reader for bioformats.
*/

package loci.formats.in;

import java.io.IOException;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

import uk.ac.mrc.hgu.Wlz.WlzGreyType;
import uk.ac.mrc.hgu.Wlz.WlzObjectType;
import uk.ac.mrc.hgu.Wlz.WlzIBox2;
import uk.ac.mrc.hgu.Wlz.WlzIBox3;
import uk.ac.mrc.hgu.Wlz.WlzIVertex2;
import uk.ac.mrc.hgu.Wlz.WlzIVertex3;
import uk.ac.mrc.hgu.Wlz.WlzDVertex2;
import uk.ac.mrc.hgu.Wlz.WlzDVertex3;
import uk.ac.mrc.hgu.Wlz.WlzFileStream;
import uk.ac.mrc.hgu.Wlz.WlzFileInputStream;
import uk.ac.mrc.hgu.Wlz.WlzException;
import uk.ac.mrc.hgu.Wlz.WlzObject;

/**
 * WlzReader is a file format reader for Woolz files.
 * Woolz is available from: https://github.com/ma-tech/Woolz
 */
public class WlzReader extends FormatReader {

  // -- Constants --

  /** Modality types. */
  // HACK move these to a service
  private static final String URL_WLZ = "https://github.com/ma-tech/Woolz";
  private static final String NO_WLZ_MSG = "Woolz library not found. " +
    "Please see " + URL_WLZ + " for details.";

  // -- Static initializers --

  static {
  }

  // -- Woolz stuff --
  private int	objType = WlzObjectType.WLZ_NULL;
  /* objGType is the Woolz value type, but may be WLZ_GREY_ERROR to indicate
   * an object with no values, just a domain. */
  private int	objGType = WlzGreyType.WLZ_GREY_ERROR;
  private 	String wlzVersion = new String("unknown");
  private 	WlzIBox3 bBox = new WlzIBox3();
  private 	WlzObject wlzObj = null;
  private 	WlzFileStream wlzFP = null;

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
    if(wlzObj != null) {
      try {
	switch(objType)
	{
	  case WlzObjectType.WLZ_2D_DOMAINOBJ:
	    buf = bytesFromWlz2DDomObj(buf, x, y, w, h);
	    break;
	  case WlzObjectType.WLZ_3D_DOMAINOBJ:
	    buf = bytesFromWlz3DDomObj(buf, x, y, no, w, h);
	    break;
	  default:
	    throw new WlzException("Unsupported Woolz object type");
	}
      }
      catch (WlzException e) {
        throw new FormatException(
	    "Failed to copy bytes from Woolz object (" + e + ")");
      }
    }
    return(buf);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    String v[] = new String[1];
    WlzObject.WlzGetVersion(v);
    wlzVersion = new String(v[0]);
    CoreMetadata md = core.get(0);
    MetadataStore store = makeFilterMetadata();
    try {
      wlzFP = new WlzFileInputStream(id);
      wlzObj = WlzObject.WlzReadObj(wlzFP);
    }
    catch (UnsatisfiedLinkError e) {
      throw new MissingLibraryException(NO_WLZ_MSG, e);
    }
    catch (WlzException e) {
      throw new IOException("Failed to read " + id + " (" +  e + ")");
    }
    try {
      objType = WlzObject.WlzGetObjectType(wlzObj);
    }
    catch (WlzException e) {
      throw new IOException("Unable to find Woolz object type (" + e + ")");
    }
    switch(objType) {
      case WlzObjectType.WLZ_2D_DOMAINOBJ:
	populateMetadata2D();
	populatePixelType();
	break;
      case WlzObjectType.WLZ_3D_DOMAINOBJ:
	populateMetadata3D();
	populatePixelType();
	WlzDVertex3 vSz;
	try {
	  vSz = WlzObject.WlzGetVoxelSize(wlzObj);
	}
	catch (WlzException e) {
	  throw new IOException("Unable to find Woolz voxel size (" + e + ")");
	}
	PositiveFloat x = new PositiveFloat(Math.abs(vSz.vtX));
	PositiveFloat y = new PositiveFloat(Math.abs(vSz.vtY));
	PositiveFloat z = new PositiveFloat(Math.abs(vSz.vtZ));
	store.setPixelsPhysicalSizeX(x, 0);
	store.setPixelsPhysicalSizeY(y, 0);
	store.setPixelsPhysicalSizeZ(z, 0);
        break;
      default:
	throw new IOException("Invalid Woolz object type (type = " +
			      objType + ")");
    }
    store.setStageLabelName("WoolzOrigin", 0);
    store.setStageLabelX((double )(bBox.xMin), 0);
    store.setStageLabelY((double )(bBox.yMin), 0);
    store.setStageLabelZ((double )(bBox.zMin), 0);
    MetadataTools.populatePixels(store, this);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if(wlzFP != null) {
      wlzFP.close();
      wlzFP = null;
    }
    if(wlzObj != null) {
      // Object is freed by garbage collection.
      wlzObj = null;
    }
    super.close(fileOnly);
  }

  // -- Helper methods --

  private byte[] bytesFromWlz2DDomObj(byte[] buf, int x, int y, int w, int h)
      throws WlzException {
    WlzIVertex2 og = new WlzIVertex2(x + bBox.xMin, y + bBox.yMin);
    WlzIVertex2 sz = new WlzIVertex2(w, h);
    WlzIVertex2 dstSz[] = new WlzIVertex2[1];
    dstSz[0] = null;
    switch(objGType) {
      case WlzGreyType.WLZ_GREY_UBYTE:
	{
	  byte dstDat[][][] = new byte[1][][];
	  WlzObject.WlzToUArray2D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      buf[idYW + idX] = dstDat[0][idY][idX];
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_SHORT:
	{
	  short m = 0xff;
	  short dstDat[][][] = new short[1][][];
	  WlzObject.WlzToSArray2D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      short p = dstDat[0][idY][idX];
	      buf[2 * (idYW + idX)]     = (byte )((p >>> 8) & m);
	      buf[2 * (idYW + idX) + 1] = (byte )(p & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_INT:
	{
	  int m = 0xff;
	  int dstDat[][][] = new int[1][][];
	  WlzObject.WlzToIArray2D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      int p = dstDat[0][idY][idX];
	      buf[idYW + (4 * idX)]     = (byte )((p >> 24) & m);
	      buf[idYW + (4 * idX) + 1] = (byte )((p >> 16) & m);
	      buf[idYW + (4 * idX) + 2] = (byte )((p >> 8) & m);
	      buf[idYW + (4 * idX) + 3] = (byte )(p & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_RGBA:
	{
	  int m = 0xff;
	  int cOff = h * w;
	  int dstDat[][][] = new int[1][][];
	  WlzObject.WlzToRArray2D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      int idYWX = idYW + idX;
	      int p = dstDat[0][idY][idX];
	      buf[idYWX]              = (byte )((p >>  0) & m);
	      buf[idYWX + cOff]       = (byte )((p >>  8) & m);
	      buf[idYWX + (2 * cOff)] = (byte )((p >> 16) & m);
	      buf[idYWX + (3 * cOff)] = (byte )((p >> 24) & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_ERROR: /* Indicates no values. */
        {
	  int	w8 = (w + 7) / 8;
	  byte dstDat[][][] = new byte[1][][];
	  WlzObject.WlzToBArray2D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    int idYW8 = idY * w8;
	    for(int idX = 0; idX < w; ++idX) {
	      byte p = dstDat[0][idY][idX / 8];
	      byte b = (byte )(p & (0x01 << (idX % 8)));
	      if(b == 0){
	        buf[idYW + idX] = (byte )0x00;
	      }
	      else {
	        buf[idYW + idX] = (byte )0xff;
	      }
	    }
	  }
	}
	break;
      default:
        throw new WlzException("Unsupported pixel type");
    }
    return(buf);
  }

  private byte[] bytesFromWlz3DDomObj(byte[] buf, int x, int y, int z,
                                      int w, int h)
      throws WlzException {
    WlzIVertex3 og = new WlzIVertex3(x + bBox.xMin, y + bBox.yMin,
				     z + bBox.zMin);
    WlzIVertex3 sz = new WlzIVertex3(w, h, 1);
    WlzIVertex3 dstSz[] = new WlzIVertex3[1];
    dstSz[0] = null;
    switch(objGType) {
      case WlzGreyType.WLZ_GREY_UBYTE:
	{
	  byte dstDat[][][][] = new byte[1][][][];
	  WlzObject.WlzToUArray3D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      buf[idYW + idX] = dstDat[0][0][idY][idX];
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_SHORT:
	{
	  short m = 0xff;
	  short dstDat[][][][] = new short[1][][][];
	  WlzObject.WlzToSArray3D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      short p = dstDat[0][0][idY][idX];
	      buf[2 * (idYW + idX)]     = (byte )((p >>> 8) & m);
	      buf[2 * (idYW + idX) + 1] = (byte )(p & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_INT:
	{
	  int m = 0xff;
	  int dstDat[][][][] = new int[1][][][];
	  WlzObject.WlzToIArray3D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      int p = dstDat[0][0][idY][idX];
	      buf[idYW + (4 * idX)]     = (byte )((p >> 24) & m);
	      buf[idYW + (4 * idX) + 1] = (byte )((p >> 16) & m);
	      buf[idYW + (4 * idX) + 2] = (byte )((p >> 8) & m);
	      buf[idYW + (4 * idX) + 3] = (byte )(p & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_RGBA:
	{
	  int m = 0xff;
	  int cOff = h * w;
	  int dstDat[][][][] = new int[1][][][];
	  WlzObject.WlzToRArray3D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    for(int idX = 0; idX < w; ++idX) {
	      int idYWX = idYW + idX;
	      int p = dstDat[0][0][idY][idX];
	      buf[idYWX]              = (byte )((p >>  0) & m);
	      buf[idYWX + cOff]       = (byte )((p >>  8) & m);
	      buf[idYWX + (2 * cOff)] = (byte )((p >> 16) & m);
	      buf[idYWX + (3 * cOff)] = (byte )((p >> 24) & m);
	    }
	  }
	}
	break;
      case WlzGreyType.WLZ_GREY_ERROR: /* Indicates no values. */
        {
	  int	w8 = (w + 7) / 8;
	  byte dstDat[][][][] = new byte[1][][][];
	  WlzObject.WlzToBArray3D(dstSz, dstDat, wlzObj, og, sz, 0);
	  for(int idY = 0; idY < h; ++idY) {
	    int idYW = idY * w;
	    int idYW8 = idY * w8;
	    for(int idX = 0; idX < w; ++idX) {
	      byte p = dstDat[0][0][idY][idX / 8];
	      byte b = (byte )(p & (0x01 << (idX % 8)));
	      if(b == 0){
	        buf[idYW + idX] = (byte )0x00;
	      }
	      else {
	        buf[idYW + idX] = (byte )0xff;
	      }
	    }
	  }
	}
	break;
      default:
        throw new WlzException("Unsupported pixel type");
    }
    return(buf);
  }

  private void populatePixelType() throws FormatException {
    CoreMetadata md = core.get(0);
    try {
      if(WlzObject.WlzObjectValuesIsNull(wlzObj) != 0) {
	/* Here we use WLZ_GREY_ERROR to indicate that the object has no
	 * values not an error. */
	objGType = WlzGreyType.WLZ_GREY_ERROR;
      }
      else {
	objGType = WlzObject.WlzGreyTypeFromObj(wlzObj);
      }
    }
    catch (WlzException e) {
      throw new FormatException(
	  "Unable to determine Woolz object value type (" + e + ")");
    }
    switch(objGType) {
      case WlzGreyType.WLZ_GREY_UBYTE:
	md.pixelType = FormatTools.UINT8;
	break;
      case WlzGreyType.WLZ_GREY_SHORT:
	md.pixelType = FormatTools.INT16;
	break;
      case WlzGreyType.WLZ_GREY_INT:
	md.pixelType = FormatTools.INT32;
	break;
      case WlzGreyType.WLZ_GREY_FLOAT:
	md.pixelType = FormatTools.FLOAT;
	break;
      case WlzGreyType.WLZ_GREY_DOUBLE:
	md.pixelType = FormatTools.DOUBLE;
	break;
      case WlzGreyType.WLZ_GREY_RGBA:
	md.rgb = true;
	md.sizeC = 4;
	md.pixelType = FormatTools.UINT8;
	break;
      case WlzGreyType.WLZ_GREY_ERROR:
        md.pixelType = FormatTools.UINT8;
	break;
      default:
	throw new FormatException(
	    "Inappropriate Woolz object value type (type = " + objGType + ")");
    }
  }

  private void populateMetadata2D() throws FormatException {
    CoreMetadata md = core.get(0);
    WlzIBox2 bBox2;
    try {
      bBox2 = WlzObject.WlzBoundingBox2I(wlzObj);
    }
    catch (WlzException e) {
      throw new FormatException("Inappropriate Woolz object domain (" +
                                e + ")");
    }
    bBox.xMin = bBox2.xMin;
    bBox.xMax = bBox2.xMax;
    bBox.yMin = bBox2.yMin;
    bBox.yMax = bBox2.yMax;
    md.rgb = false;
    md.interleaved = false;
    md.indexed = false;
    md.sizeX = bBox.xMax - bBox.xMin + 1;
    md.sizeY = bBox.yMax - bBox.yMin + 1;
    md.sizeZ = 1;
    md.sizeC = 1;
    md.sizeT = 1;
    md.dimensionOrder = "XYZCT";
    md.imageCount = 1;
  }

  private void populateMetadata3D() throws FormatException {
    CoreMetadata md = core.get(0);
    try {
      bBox = WlzObject.WlzBoundingBox3I(wlzObj);
    }
    catch (WlzException e) {
      throw new FormatException("Inappropriate Woolz object domain (" +
                                e + ")");
    }
    md.rgb = false;
    md.interleaved = false;
    md.indexed = false;
    md.sizeX = bBox.xMax - bBox.xMin + 1;
    md.sizeY = bBox.yMax - bBox.yMin + 1;
    md.sizeZ = bBox.zMax - bBox.zMin + 1;
    md.sizeC = 1;
    md.sizeT = 1;
    md.dimensionOrder = "XYZCT";
    md.imageCount = md.sizeZ;
  }
}
