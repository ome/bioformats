//
// LuraWaveServiceImpl.java
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

import com.luratech.lwf.lwfDecoder;

import loci.common.services.AbstractService;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/services/LuraWaveServiceImpl.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/services/LuraWaveService.java">SVN</a></dd></dl>
 */
public class LuraWaveServiceImpl extends AbstractService
  implements LuraWaveService {

  /** System property to check for the LuraWave license code. */
  public static final String LICENSE_PROPERTY = "lurawave.license";

  /** Message displayed if the LuraWave LWF decoder library is not found. */
  public static final String NO_LURAWAVE_MSG =
    "The LuraWave decoding library, lwf_jsdk2.6.jar, is required to decode " +
    "this file.\r\nPlease make sure it is present in your classpath.";

  /** Message to display if no LuraWave license code is given. */
  public static final String NO_LICENSE_MSG =
    "No LuraWave license code was specified.\r\nPlease set one in the " +
    LICENSE_PROPERTY + " system property (e.g., with -D" + LICENSE_PROPERTY +
    "=XXXX from the command line).";

  /** Message to display if an invalid LuraWave license code is given. */
  public static final String INVALID_LICENSE_MSG = "Invalid license code: ";

  /** LuraWave decoder delegate. */
  private lwfDecoder delegate;
  
  /** License code. */
  private String license;

  /**
   * Default constructor.
   */
  public LuraWaveServiceImpl() {
    license = System.getProperty(LICENSE_PROPERTY);
    checkClassDependency(com.luratech.lwf.lwfDecoder.class);
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#setLicenseCode(java.lang.String)
   */
  public void setLicenseCode(String license) {
    this.license = license;
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#getLicenseCode()
   */
  public String getLicenseCode() {
    return license;
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#initialize(java.io.InputStream)
   */
  public void initialize(InputStream stream)
    throws IOException, DependencyException, ServiceException {
    if (license == null) {
      throw new DependencyException(NO_LICENSE_MSG);
    }
    try {
      delegate = new lwfDecoder(stream, null, license);
    }
    catch (SecurityException e) {
      throw new ServiceException(e);
    }
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#getWidth()
   */
  public int getWidth() {
    return delegate.getWidth();
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#getHeight()
   */
  public int getHeight() {
    return delegate.getHeight();
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#decodeToMemoryGray8(byte[], int, int, int)
   */
  public void decodeToMemoryGray8(byte[] image, int limit,
                                  int quality, int scale)
    throws ServiceException {
    try {
      delegate.decodeToMemoryGray8(image, limit, quality, scale);
    }
    catch (SecurityException e) {
      throw new ServiceException(e);
    }
  }

  /* (non-Javadoc)
   * @see loci.formats.services.LuraWaveService#decodeToMemoryGray16(short[], int, int, int, int, int, int, int, int, int, int)
   */
  public void decodeToMemoryGray16(
      short[] image, int imageoffset, int limit, int quality, int scale,
      int pdx, int pdy, int clip_x, int clip_y, int clip_w, int clip_h)
    throws ServiceException {
    try {
      delegate.decodeToMemoryGray16(image, imageoffset, limit, quality, scale,
                                    pdx, pdy, clip_x, clip_y, clip_w, clip_h);
    }
    catch (SecurityException e) {
      throw new ServiceException(e);
    }
  }

}
