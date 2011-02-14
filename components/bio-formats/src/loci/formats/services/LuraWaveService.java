//
// LuraWaveService.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.services;

import java.io.IOException;
import java.io.InputStream;

import loci.common.services.DependencyException;
import loci.common.services.Service;
import loci.common.services.ServiceException;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/LuraWaveService.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/LuraWaveService.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public interface LuraWaveService extends Service {

  /**
   * Overrides the license code to use when initializing the LuraWave decoder.
   * By default the license code is loaded from the "lurawave.license" system
   * property.
   * @param license String license code.
   */
  public void setLicenseCode(String license);

  /**
   * Retrieves the current license code as a string.
   * @return See above.
   */
  public String getLicenseCode();

  /**
   * Wraps {@link com.luratech.lwf.lwfDecoder#lwfDecoder(InputStream, String, String)}.
   * @throws IOException If parsing of the image header fails.
   * @throws DependencyException If no license code was specified.
   * @throws ServiceException If the license code is invalid.
   */
  public void initialize(InputStream stream)
    throws IOException, DependencyException, ServiceException;

  /** Wraps {@link com.luratech.lwf.lwfDecoder#getWidth()} */
  public int getWidth();

  /** Wraps {@link com.luratech.lwf.lwfDecoder#getHeight()} */
  public int getHeight();

  /** 
   * Wraps {@link com.luratech.lwf.lwfDecoder#decodeToMemoryGray8(byte[], int, int, int)}.
   * @throws ServiceException If the license code is invalid.
   */
  public void decodeToMemoryGray8(byte[] image, int limit,
                                  int quality, int scale)
    throws ServiceException;

  /** 
   * Wraps {@link com.luratech.lwf.lwfDecoder#decodeToMemoryGray16(short[], int, int, int, int, int, int, int, int, int, int)}.
   * @throws ServiceException If the license code is invalid.
   */
  public void decodeToMemoryGray16(
      short[] image, int imageoffset, int limit, int quality, int scale,
      int pdx, int pdy, int clip_x, int clip_y, int clip_w, int clip_h)
    throws ServiceException;

}
