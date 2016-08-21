/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.services;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import loci.common.services.AbstractService;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;

import com.luratech.lwf.lwfDecoder;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/LuraWaveServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/LuraWaveServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
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

  /** Identifying field in stub class. */
  public static final String STUB_FIELD = "IS_STUB";

  /** LuraWave decoder delegate. */
  private lwfDecoder delegate;
  
  /** License code. */
  private String license;

  /**
   * Default constructor.
   */
  public LuraWaveServiceImpl() throws DependencyException {
    checkClassDependency(com.luratech.lwf.lwfDecoder.class);
    try {
      Field isStub = com.luratech.lwf.lwfDecoder.class.getField(STUB_FIELD);
      if (isStub != null) {
        throw new DependencyException(NO_LURAWAVE_MSG);
      }
    }
    catch (NoSuchFieldException e) { }
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
    initLicense();
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

  private void initLicense() throws DependencyException {
    if (license != null) return; // license already initialized
    license = System.getProperty(LICENSE_PROPERTY);
    if (license == null) throw new DependencyException(NO_LICENSE_MSG);
  }

}
