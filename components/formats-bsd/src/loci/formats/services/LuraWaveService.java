/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import loci.common.services.DependencyException;
import loci.common.services.Service;
import loci.common.services.ServiceException;

/**
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
