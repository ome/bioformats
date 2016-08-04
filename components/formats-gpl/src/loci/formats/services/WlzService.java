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

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.MissingLibraryException;

import loci.common.services.Service;

public interface WlzService extends Service {
  
  /**
   * Initializes the service for the given file path.
   * @param     file            File path with which to initialize the service.
   * @throws    IOException
   * @throws     FormatException
   */
  public void open(String file, String rw)
    throws IOException, FormatException;

  /**
   * Gets the text string for when Woolz has not been found.
   */
  public String getNoWlzMsg();

  /**
   * Gets the text string used for the Woolz origin label.
   */
  public String getWlzOrgLabelName();

  /**
   * Gets width in pixels/voxels.
   * @return                    width.
   */
  public int getSizeX();

  /**
   * Gets height in pixels/voxels.
   * @return                    height.
   */
  public int getSizeY();

  /**
   * Gets depth (number of x-y planes) in voxels.
   * @return                    depth.
   */
  public int getSizeZ();

  /**
   * Gets number of channels.
   * @return                    channels.
   */
  public int getSizeC();

  /**
   * Gets number of time samples.
   * @return                    time.
   */
  public int getSizeT();

  /**
   * Gets boolean for whether image is colour or not.
   * @return                    true if image is colour.
   */
  public boolean isRGB();

  /**
   * Gets the image pixel type.
   * @return                    pixel type.
   */
  public int     getPixelType();

  /**
   * Gets voxel width.
   * @return                    voxel width.
   */
  public double getVoxSzX();

  /**
   * Gets voxel height.
   * @return                    voxel height.
   */
  public double getVoxSzY();

  /**
   * Gets voxel depth.
   * @return                    voxel depth.
   */
  public double getVoxSzZ();

  /**
   * Gets column origin.
   * @return                    column origin.
   */
  public double getOrgX();

  /**
   * Gets line origin.
   * @return                    line origin.
   */
  public double getOrgY();

  /**
   * Gets plane origin.
   * @return                    plane origin.
   */
  public double getOrgZ();

  /**
   * Gets supported pixel types.
   * @return                    array of supported pixel types.
   */
  public int[] getSupPixelTypes();

  /**
   * Sets up the service, which must have already been opened for writing.
   * @param     orgX            x origin.
   * @param     orgY            y origin.
   * @param     orgZ            z origin (set to 0 for 2D).
   * @param     pixSzX          width.
   * @param     pixSzY          height.
   * @param     pixSzZ          depth (number of planes, set to 1 for 2D).
   * @param     pixSzC          number of channels.
   * @param     pixSzT          number of time samples.
   * @param     voxSzX          pixel/voxel width.
   * @param     voxSzY          pixel/voxel heigth.
   * @param     voxSzZ          voxel deoth.
   * @param     gType           image value type.
   * @throws    FormatException
   */
  public void setupWrite(int orgX, int orgY, int orgZ,
                         int pixSzX, int pixSzY, int pixSzZ,
                         int pixSzC, int pixSzT,
                         double voxSzX, double voxSzY, double voxSzZ,
                         int gType)
    throws FormatException;

  /**
   * Closes the file.
   */
  public void close()
    throws IOException;

  /**
  * Reads a rectangle of bytes in an x-y plane from the opened Woolz
  * object.
  * @return     Buffer of bytes read.
  * @param      no              plane coordinate (set to 0 for 2D).
  * @param      buf             buffer for bytes.
  * @param      x               rectangle first column.
  * @param      y               rectangle first line.
  * @param      w               rectangle width (in columns).
  * @param      h               rectangle heigth (in lines).
  */
  public byte[] readBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
  * Adds a rectangle of bytes in an x-y plane to the opened Woolz object.
  * @param      no              plane coordinate (set to 0 for 2D).
  * @param      buf             buffer with bytes.
  * @param      x               rectangle first column.
  * @param      y               rectangle first line.
  * @param      w               rectangle width (in columns).
  * @param      h               rectangle heigth (in lines).
  */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException;

}
