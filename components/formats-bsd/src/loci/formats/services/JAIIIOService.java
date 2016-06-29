/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import loci.common.services.Service;
import loci.common.services.ServiceException;
import loci.formats.codec.JPEG2000CodecOptions;

/**
 * Interface defining methods for reading data using JAI Image I/O.
 */
public interface JAIIIOService extends Service {

  /**
   * Writes an image using JAI Image I/O using the JPEG 2000 codec.
   * @param out Target output stream.
   * @param img Source buffered image.
   * @param options Options for the JPEG 2000 codec.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   */
  public void writeImage(OutputStream out, BufferedImage img,
      JPEG2000CodecOptions options) throws IOException, ServiceException;

  /**
   * Reads an image using JAI Image I/O using the JPEG 2000 codec.
   * @param in Target input stream.
   * @param options Options for the JPEG 2000 codec.
   * @return An AWT buffered image.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   */
  public BufferedImage readImage(InputStream in, JPEG2000CodecOptions options)
    throws IOException, ServiceException;

  /**
   * Reads an image using JAI Image I/O using the JPEG 2000 codec.
   * @param in Target input stream.
   * @return An AWT buffered image.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   * @see #readImage(InputStream, JPEG2000CodecOptions)
   */
  public BufferedImage readImage(InputStream in)
    throws IOException, ServiceException;

  /**
   * Reads an image into a raster using JAI Image I/O using the JPEG 2000 codec.
   * @param in Target input stream.
   * @param options Options for the JPEG 2000 codec.
   * @return An AWT image raster.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   */
  public Raster readRaster(InputStream in, JPEG2000CodecOptions options)
    throws IOException, ServiceException;

  /**
   * Reads an image into a raster using JAI Image I/O using the JPEG 2000 codec.
   * @param in Target input stream.
   * @return An AWT image raster.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   * @see #readRaster(InputStream, JPEG2000CodecOptions)
   */
  public Raster readRaster(InputStream in) throws IOException, ServiceException;

}
