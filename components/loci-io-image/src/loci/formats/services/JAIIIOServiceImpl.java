//
// JAIIIOServiceImpl.java
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
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/JAIIIOServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/JAIIIOServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JAIIIOServiceImpl extends AbstractService
  implements JAIIIOService
{

  // -- Constants --

  public static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read JPEG-2000 files. Please " +
    "obtain jai_imageio.jar from http://loci.wisc.edu/ome/formats-library.html";

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
  }

  /* @see JAIIIOService#writeImage(OutputStream, BufferedImage, JPEG2000CodecOptions) */
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
    param.setLossless(options.lossless);
    param.setFilter(filter);
    param.setCodeBlockSize(options.codeBlockSize);
    param.setEncodingRate(options.quality);
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

  /**
   * @deprecated
   * @see JAIIIOService#writeImage(OutputStream, BufferedImage)
   */
  public void writeImage(OutputStream out, BufferedImage img, boolean lossless,
    int[] codeBlockSize, double quality) throws IOException, ServiceException
  {
    JPEG2000CodecOptions options =
      JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = lossless;
    options.codeBlockSize = codeBlockSize;
    options.quality = quality;
    writeImage(out, img, options);
  }

  /* @see JAIIIOService#readImage(InputStream, JPEG2000CodecOptions) */
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
    return reader.read(0, param);
  }

  /* @see JAIIIOService#readImage(InputStream) */
  public BufferedImage readImage(InputStream in)
    throws IOException, ServiceException
  {
    return readImage(in, JPEG2000CodecOptions.getDefaultOptions());
  }

  /* @see JAIIIOService#readRaster(InputStream, JPEG2000CodecOptions) */
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
    return reader.readRaster(0, param);
  }

  /* @see JAIIIOService#readRaster(InputStream) */
  public Raster readRaster(InputStream in) throws IOException, ServiceException
  {
    return readRaster(in, JPEG2000CodecOptions.getDefaultOptions());
  }

  /** Set up the JPEG-2000 image reader. */
  private J2KImageReader getReader() {
    IIORegistry registry = IIORegistry.getDefaultInstance();
    Iterator<J2KImageReaderSpi> iter =
      ServiceRegistry.lookupProviders(J2KImageReaderSpi.class);
    registry.registerServiceProviders(iter);
    J2KImageReaderSpi spi =
      registry.getServiceProviderByClass(J2KImageReaderSpi.class);
    return new J2KImageReader(spi);
  }

}
