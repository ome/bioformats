//
// JAIIIOService.java
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
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/JAIIIOService.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/JAIIIOService.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public interface JAIIIOService extends Service {

  /**
   * Writes an image using JAI Image I/O using the JPEG 2000 codec.
   * @param out Target output stream.
   * @param img Source buffered image.
   * @param options Options for the JPEG 2000 codec.
   * @returns An AWT buffered image.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   */
  public void writeImage(OutputStream out, BufferedImage img,
      JPEG2000CodecOptions options) throws IOException, ServiceException;

  /**
   * Writes an image using JAI Image I/O using the JPEG 2000 codec.
   * @param out Target output stream.
   * @param img Source buffered image.
   * @param lossless Whether or not to compress losslessly.
   * @param codeBlockSize JPEG 2000 code block size.
   * @param quality JPEG 2000 quality level.
   * @returns An AWT buffered image.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   * @deprecated Replaced by {@link #writeImage(OutputStream, BufferedImage,
   * JPEG2000CodecOptions)}.
   */
  public void writeImage(OutputStream out, BufferedImage img, boolean lossless,
   int[] codeBlockSize, double quality) throws IOException, ServiceException;

  /**
   * Reads an image using JAI Image I/O using the JPEG 2000 codec.
   * @param in Target input stream.
   * @param options Options for the JPEG 2000 codec.
   * @returns An AWT buffered image.
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
   * @returns An AWT buffered image.
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
   * @returns An AWT image raster.
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
   * @returns An AWT image raster.
   * @throws IOException Thrown if there is an error reading from or writing
   * to one of the target streams / buffers.
   * @throws ServiceException Thrown if there is an error initializing or
   * interacting with the dependencies of the service.
   * @see #readRaster(InputStream, JPEG2000CodecOptions)
   */
  public Raster readRaster(InputStream in) throws IOException, ServiceException;

}
