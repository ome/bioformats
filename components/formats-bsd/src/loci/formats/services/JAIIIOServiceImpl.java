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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import loci.common.services.AbstractService;
import loci.common.services.ServiceException;
import loci.formats.codec.JPEG2000CodecOptions;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReader;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderSpi;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriter;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriterSpi;

/**
 * Implementation of JAIIIOService for reading and writing JPEG-2000 data.
 */
public class JAIIIOServiceImpl extends AbstractService
  implements JAIIIOService
{

  // -- Constants --

  public static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read JPEG-2000 files. " +
    "Please obtain jai_imageio.jar from " +
    "http://www.openmicroscopy.org/site/support/bio-formats/developers/java-library.html";

  // -- Fields --

  private IIORegistry serviceRegistry;

  // -- JAIIIOService API methods --

  /**
   * Default constructor.
   */
  public JAIIIOServiceImpl() {
    // Thorough class checking
    checkClassDependency(J2KImageWriteParam.class);
    checkClassDependency(J2KImageWriter.class);
    checkClassDependency(J2KImageWriterSpi.class);
    checkClassDependency(J2KImageReadParam.class);
    checkClassDependency(J2KImageReader.class);
    checkClassDependency(J2KImageReaderSpi.class);

    serviceRegistry = registerServiceProviders();
  }

  /* @see JAIIIOService#writeImage(OutputStream, BufferedImage, JPEG2000CodecOptions) */
  @Override
  public void writeImage(OutputStream out, BufferedImage img,
      JPEG2000CodecOptions options) throws IOException, ServiceException
  {
    ImageOutputStream ios = ImageIO.createImageOutputStream(out);

    IIORegistry registry = IIORegistry.getDefaultInstance();
    Iterator<J2KImageWriterSpi> iter = 
      ServiceRegistry.lookupProviders(J2KImageWriterSpi.class);
    registry.registerServiceProviders(iter);
    J2KImageWriterSpi spi =
      registry.getServiceProviderByClass(J2KImageWriterSpi.class);
    J2KImageWriter writer = new J2KImageWriter(spi);
    writer.setOutput(ios);

    String filter = options.lossless ? J2KImageWriteParam.FILTER_53 :
      J2KImageWriteParam.FILTER_97;

    IIOImage iioImage = new IIOImage(img, null, null);
    J2KImageWriteParam param =
      (J2KImageWriteParam) writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionType("JPEG2000");
    if (!options.lossless) {
      param.setEncodingRate(options.quality);
    }
    param.setLossless(options.lossless);
    param.setFilter(filter);
    param.setCodeBlockSize(options.codeBlockSize);
    param.setComponentTransformation(false);
    if (options.tileWidth > 0 && options.tileHeight > 0) {
      param.setTiling(options.tileWidth, options.tileHeight,
                      options.tileGridXOffset, options.tileGridYOffset);
    }
    if (options.numDecompositionLevels != null) {
      param.setNumDecompositionLevels(
          options.numDecompositionLevels.intValue());
    }
    writer.write(null, iioImage, param);
    ios.close();
  }

  /* @see JAIIIOService#readImage(InputStream, JPEG2000CodecOptions) */
  @Override
  public BufferedImage readImage(InputStream in, JPEG2000CodecOptions options)
    throws IOException, ServiceException
  {
    J2KImageReader reader = getReader();
    MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(in);
    reader.setInput(mciis, false, true);
    J2KImageReadParam param = (J2KImageReadParam) reader.getDefaultReadParam();
    if (options.resolution != null) {
      param.setResolution(options.resolution.intValue());
    }
    BufferedImage image = reader.read(0, param);
    reader.dispose();
    return image;
  }

  /* @see JAIIIOService#readImage(InputStream) */
  @Override
  public BufferedImage readImage(InputStream in)
    throws IOException, ServiceException
  {
    return readImage(in, JPEG2000CodecOptions.getDefaultOptions());
  }

  /* @see JAIIIOService#readRaster(InputStream, JPEG2000CodecOptions) */
  @Override
  public Raster readRaster(InputStream in, JPEG2000CodecOptions options)
    throws IOException, ServiceException
  {
    J2KImageReader reader = getReader();
    MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(in);
    reader.setInput(mciis, false, true);
    J2KImageReadParam param = (J2KImageReadParam) reader.getDefaultReadParam();
    if (options.resolution != null) {
      param.setResolution(options.resolution.intValue());
    }
    Raster raster = reader.readRaster(0, param);
    reader.dispose();
    return raster;
  }

  /* @see JAIIIOService#readRaster(InputStream) */
  @Override
  public Raster readRaster(InputStream in) throws IOException, ServiceException
  {
    return readRaster(in, JPEG2000CodecOptions.getDefaultOptions());
  }

  /** Set up the JPEG-2000 image reader. */
  private J2KImageReader getReader() {
    J2KImageReaderSpi spi =
      serviceRegistry.getServiceProviderByClass(J2KImageReaderSpi.class);
    return new J2KImageReader(spi);
  }

  /** Register the JPEG-2000 readers with the reader service. */
  private static IIORegistry registerServiceProviders() {
    IIORegistry registry = IIORegistry.getDefaultInstance();
    Iterator<J2KImageReaderSpi> iter =
      ServiceRegistry.lookupProviders(J2KImageReaderSpi.class);
    registry.registerServiceProviders(iter);
    return registry;
  }

}
