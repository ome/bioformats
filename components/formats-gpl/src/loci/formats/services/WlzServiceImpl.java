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
* \file         WlzService.java
* \author       Bill Hill
* \date         August 2013
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
* \brief        Woolz service for bioformats.
*/

package loci.formats.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Constants;
import loci.common.Location;
import loci.common.services.AbstractService;
import loci.common.services.ServiceException;

import loci.formats.FormatTools;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;

import uk.ac.mrc.hgu.Wlz.WlzException;
import uk.ac.mrc.hgu.Wlz.WlzGreyType;
import uk.ac.mrc.hgu.Wlz.WlzObjectType;
import uk.ac.mrc.hgu.Wlz.WlzFileStream;
import uk.ac.mrc.hgu.Wlz.WlzFileInputStream;
import uk.ac.mrc.hgu.Wlz.WlzFileOutputStream;
import uk.ac.mrc.hgu.Wlz.WlzIBox2;
import uk.ac.mrc.hgu.Wlz.WlzIBox3;
import uk.ac.mrc.hgu.Wlz.WlzIVertex2;
import uk.ac.mrc.hgu.Wlz.WlzIVertex3;
import uk.ac.mrc.hgu.Wlz.WlzDVertex2;
import uk.ac.mrc.hgu.Wlz.WlzDVertex3;
import uk.ac.mrc.hgu.Wlz.WlzObject;

public class WlzServiceImpl extends AbstractService
  implements WlzService {

  // -- Constants --

  public static final String WLZ_ORG_LABEL = "WoolzOrigin";
  public static final String NO_WLZ_MSG =
    "\n" +
    "Woolz is required to read and write Woolz objects.\n" +
    "Please obtain the necessary JAR and native library files from:\n" +
    "http://www.emouseatlas.org/emap/analysis_tools_resources/software/woolz.html.\n" +
    "The source code for these is also available from:\n" +
    "https://github.com/ma-tech/Woolz.";
  
  public static final int WLZ_SERVICE_UNKNOWN = 0;
  public static final int WLZ_SERVICE_READ    = 1;
  public static final int WLZ_SERVICE_WRITE   = 2;

  /*
  * Fields
  */

  private int   state = WLZ_SERVICE_UNKNOWN;
  private int   pixelType = FormatTools.UINT8;
  private int   objType = WlzObjectType.WLZ_NULL;
  /* objGType is the Woolz value type, but may be WLZ_GREY_ERROR to indicate
   * an object with no values, just a domain. */
  private int   objGType = WlzGreyType.WLZ_GREY_ERROR;
  private String wlzVersion = new String("unknown");
  private WlzIBox3 bBox = null;
  private WlzDVertex3 voxSz = null;
  private WlzObject wlzObj = null;
  private WlzFileStream wlzFP = null; 

  /*
  * Default constructor.
  */
  public WlzServiceImpl() {
    checkClassDependency(WlzObject.class);
  }

  @Override
  protected void checkClassDependency(Class<? extends Object> klass) {
    String v[] = new String[1];
    WlzObject.WlzGetVersion(v);
  }

  /*
  * Service methods
  */

  @Override
  public String getNoWlzMsg() {
    return(new String(NO_WLZ_MSG));
  }

  @Override
  public String getWlzOrgLabelName() {
    return(new String(WLZ_ORG_LABEL));
  }

  @Override
  public void open(String file, String rw)
    throws FormatException, IOException {
    try {
      String v[] = new String[1];
      WlzObject.WlzGetVersion(v);
      wlzVersion = new String(v[0]);
    }
    catch (UnsatisfiedLinkError e) {
      throw new FormatException(NO_WLZ_MSG, e);
    }
    if(rw.equals("r")) {
      openRead(file);
    }
    else if(rw.equals("w")) {
      openWrite(file);
    }
    else {
      throw new IOException("Failed to open file " + file);
    }
  }

  @Override
  public int    getSizeX() {
    int         sz;
    if(bBox == null) {
      sz = 0;
    }
    else {
      sz = bBox.xMax - bBox.xMin + 1;
    }
    return(sz);
  }

  @Override
  public int    getSizeY() {
    int         sz;
    if(bBox == null) {
      sz = 0;
    }
    else {
      sz = bBox.yMax - bBox.yMin + 1;
    }                 
    return(sz);
  }

  @Override
  public int    getSizeZ() {
    int         sz;
    if(bBox == null) {
      sz = 0;
    }
    else {
      sz = bBox.zMax - bBox.zMin + 1;
    }                 
    return(sz);
  }

  @Override
  public int    getSizeC() {
    int         sz;
    if(objGType == WlzGreyType.WLZ_GREY_RGBA) {
      sz = 4;
    }
    else {
      sz = 1;
    }
    return(sz);
  }

  @Override
  public int    getSizeT() {
    return(1);
  }

  @Override
  public boolean isRGB() {
    boolean     rgb;
    if(objGType == WlzGreyType.WLZ_GREY_RGBA) {
      rgb = true;
    }
    else {
      rgb = false;
    }
    return(rgb);
  }

  @Override
  public double getVoxSzX() {
    double sz;

    if(voxSz == null) {
      sz = 1.0;
    }
    else {
      sz = voxSz.vtX;
    }
    return(sz);
  }

  @Override
  public double getVoxSzY() {
    double sz;

    if(voxSz == null) {
      sz = 1.0;
    }
    else {
      sz = voxSz.vtY;
    }
    return(sz);
  }

  @Override
  public double getVoxSzZ() {
    double sz;

    if(voxSz == null) {
      sz = 1.0;
    }
    else {
      sz = voxSz.vtZ;
    }
    return(sz);
  }

  @Override
  public double getOrgX() {
    int og;

    if(bBox == null) {
      og = 0;
    }
    else {
      og = bBox.xMin;
    }
    return(og);
  }

  @Override
  public double getOrgY() {
    int og;

    if(bBox == null) {
      og = 0;
    }
    else {
      og = bBox.yMin;
    }
    return(og);
  }

  @Override
  public double getOrgZ() {
    int og;

    if(bBox == null) {
      og = 0;
    }
    else {
      og = bBox.zMin;
    }
    return(og);
  }

  @Override
  public int[] getSupPixelTypes() {
    return new int[] {FormatTools.UINT8,
                      FormatTools.INT16,
                      FormatTools.INT32,
                      FormatTools.FLOAT,
                      FormatTools.DOUBLE};
  }

  @Override
  public int    getPixelType() {
    int         pixType;
    switch(objGType) {
      case WlzGreyType.WLZ_GREY_SHORT:
        pixelType = FormatTools.INT16;
        break;
      case WlzGreyType.WLZ_GREY_INT:
        pixelType = FormatTools.INT32;
        break;
      case WlzGreyType.WLZ_GREY_FLOAT:
        pixelType = FormatTools.FLOAT;
        break;
      case WlzGreyType.WLZ_GREY_DOUBLE:
        pixelType = FormatTools.DOUBLE;
        break;
      default:
        pixelType = FormatTools.UINT8;
        break;
    }
    return(pixelType);
  }

  @Override
  public void setupWrite(int orgX, int orgY, int orgZ,
                         int pixSzX, int pixSzY, int pixSzZ,
                         int pixSzC, int pixSzT, 
                         double voxSzX, double voxSzY, double voxSzZ,
                         int gType)
    throws FormatException {

    bBox = new WlzIBox3();
    bBox.xMin = orgX;
    bBox.yMin = orgY;
    bBox.zMin = orgZ;
    bBox.xMax = orgX + pixSzX - 1;
    bBox.yMax = orgY + pixSzY - 1;
    bBox.zMax = orgZ + pixSzZ - 1;
    voxSz = new WlzDVertex3(voxSzX, voxSzY, voxSzZ);
    if((bBox.xMax < bBox.xMin) ||
       (bBox.yMax < bBox.yMin) ||
       (bBox.zMax < bBox.zMin) ||
       (pixSzC <= 0) ||
       (pixSzT <= 0)) {
      throw new FormatException("Invalid image size (" +
                                (bBox.xMax - bBox.xMin + 1) + ", " +
                                (bBox.yMax - bBox.yMin + 1) + ", " +
                                (bBox.zMax - bBox.zMin + 1) + ", " +
                                pixSzC + ", " +
                                pixSzT + ")");
    }
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
    }
    try {
      wlzObj = WlzObject.WlzMakeEmpty();
    }
    catch (WlzException e) {
      throw new FormatException("Failed to create Woolz object", e);
    }
  }

  @Override
  public void close()
    throws IOException {
    if(wlzObj != null) {
      if(state == WLZ_SERVICE_WRITE) {
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
      // Object is freed by garbage collection.
      wlzObj = null;
    }
    if(wlzFP != null) {
      wlzFP.close();
      wlzFP = null;
    }
  }

  @Override
  public byte[] readBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException {
    if((wlzObj == null) || (state != WLZ_SERVICE_READ)) {
      throw new FormatException("Uninitialised Woolz service");
    }
    else {
      try {
        switch(objType)
        {
          case WlzObjectType.WLZ_2D_DOMAINOBJ:
            buf = readBytes2DDomObj(buf, x, y, w, h);
            break;
          case WlzObjectType.WLZ_3D_DOMAINOBJ:
            buf = readBytes3DDomObj(buf, x, y, no, w, h);
            break;
          default:
            throw new FormatException("Unsupported Woolz object type " + objType);
        }
      }
      catch (WlzException e) {
        throw new FormatException(
            "Failed to copy bytes from Woolz object (" + e + ")");
      }
    }
    return(buf);
  }

  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if(state == WLZ_SERVICE_WRITE) {
      WlzIVertex3 og = new WlzIVertex3(x + bBox.xMin, y + bBox.yMin,
                                       no + bBox.zMin);
      WlzIVertex2 sz = new WlzIVertex2(w, h);
      try {
        wlzObj = WlzObject.WlzBuildObj3B(wlzObj, og, sz, objGType,
                                         buf.length, buf);
      }
      catch (WlzException e) {
        throw new FormatException("Failed save bytes to Woolz object", e);
      }
    }
  }


  /*
  * Helper methods.
  */
 
  private void openRead(String file)
    throws FormatException, IOException {
    state = WLZ_SERVICE_READ;
    try {
      wlzFP = new WlzFileInputStream(file);
      wlzObj = WlzObject.WlzReadObj(wlzFP);
      bBox = WlzObject.WlzBoundingBox3I(wlzObj);
      objType = WlzObject.WlzGetObjectType(wlzObj);
      if(objType == WlzObjectType.WLZ_3D_DOMAINOBJ) {
        voxSz = WlzObject.WlzGetVoxelSize(wlzObj);
      }
      else {
        voxSz = new WlzDVertex3(1.0, 1.0, 1.0);
      }
    }
    catch (WlzException e) {
      throw new IOException("Failed to read Woolz object (" +
                            e + ")", e);
    }
    try {
      if (objType == WlzObjectType.WLZ_COMPOUND_ARR_1 ||
        objType == WlzObjectType.WLZ_COMPOUND_ARR_2)
      {
        int count = objType == WlzObjectType.WLZ_COMPOUND_ARR_1 ? 1 : 2;
        WlzObject[][] dest = new WlzObject[1][count];
        WlzObject.WlzExplode(new int[] {count}, dest, wlzObj);
        wlzObj = dest[0][0];

        bBox = WlzObject.WlzBoundingBox3I(wlzObj);
        objType = WlzObject.WlzGetObjectType(wlzObj);
        if (objType == WlzObjectType.WLZ_3D_DOMAINOBJ) {
          voxSz = WlzObject.WlzGetVoxelSize(wlzObj);
        }
      }

      if(WlzObject.WlzObjectValuesIsNull(wlzObj) != 0) {
        /* Here we use WLZ_GREY_ERROR to indicate that the object has no
         * values not an error. */
        objGType = WlzGreyType.WLZ_GREY_ERROR;
      }
      else {
        // throw an exception here instead of segfaulting during readBytes*
        if (WlzObject.WlzGetObjectValuesType(wlzObj) > WlzObjectType.WLZ_GREY_TAB_TILED) {
          throw new FormatException("Value table data not supported");
        }

        objGType = WlzObject.WlzGreyTypeFromObj(wlzObj);
      }
    }
    catch (WlzException e) {
      throw new FormatException(
          "Unable to determine Woolz object value type (" +
          e + ")", e);
    }
    switch(objGType) {
      case WlzGreyType.WLZ_GREY_UBYTE:
        break;
      case WlzGreyType.WLZ_GREY_SHORT:
        break;
      case WlzGreyType.WLZ_GREY_INT:
        break;
      case WlzGreyType.WLZ_GREY_FLOAT:
        break;
      case WlzGreyType.WLZ_GREY_DOUBLE:
        break;
      case WlzGreyType.WLZ_GREY_RGBA:
        break;
      case WlzGreyType.WLZ_GREY_ERROR:
        break;
      default:
        throw new FormatException(
            "Inappropriate Woolz object value type (type = " + objGType + ")");
    }
  }

  private void openWrite(String file)
    throws FormatException, IOException {
    state = WLZ_SERVICE_WRITE;
    try {
      wlzFP = new WlzFileOutputStream(file);
    }
    catch (IOException e) {
      throw new IOException("Failed to open " + file + "for writing.");
    }
  }

  private byte[] readBytes2DDomObj(byte[] buf, int x, int y, int w, int h)
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
          int   w8 = (w + 7) / 8;
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

  private byte[] readBytes3DDomObj(byte[] buf, int x, int y, int z,
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
          int   w8 = (w + 7) / 8;
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
}

