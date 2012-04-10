//
// JPEG2000Codec.java
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

package loci.formats.codec;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.UnsignedIntBuffer;
import loci.formats.services.JAIIIOService;
import loci.formats.services.JAIIIOServiceImpl;

/**
 * This class implements JPEG 2000 compression and decompression.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/JPEG2000Codec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/JPEG2000Codec.java;hb=HEAD">Gitweb</a></dd></dl>
 * </dl>
 */
public class JPEG2000Codec extends BaseCodec {

  // -- Fields --

  private JAIIIOService service;

  // -- Codec API methods --

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#channels channels}
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#littleEndian littleEndian}
   *  {@link CodecOptions#lossless lossless}
   *
   * @see Codec#compress(byte[], CodecOptions)
   */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    if (data == null || data.length == 0) return data;
    initialize();

    JPEG2000CodecOptions j2kOptions;
    if (options instanceof JPEG2000CodecOptions) {
      j2kOptions = (JPEG2000CodecOptions) options;
    }
    else {
      j2kOptions = (JPEG2000CodecOptions)
        JPEG2000CodecOptions.getDefaultOptions(options);
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedImage img = null;

    int next = 0;

    // NB: Construct BufferedImages manually, rather than using
    // AWTImageTools.makeImage. The AWTImageTools.makeImage methods construct
    // images that are not properly handled by the JPEG2000 writer.
    // Specifically, 8-bit multi-channel images are constructed with type
    // DataBuffer.TYPE_INT (so a single int is used to store all of the
    // channels for a specific pixel).

    int plane = j2kOptions.width * j2kOptions.height;

    if (j2kOptions.bitsPerSample == 8) {
      byte[][] b = new byte[j2kOptions.channels][plane];
      if (j2kOptions.interleaved) {
        for (int q=0; q<plane; q++) {
          for (int c=0; c<j2kOptions.channels; c++) {
            b[c][q] = data[next++];
          }
        }
      }
      else {
        for (int c=0; c<j2kOptions.channels; c++) {
          System.arraycopy(data, c * plane, b[c], 0, plane);
        }
      }
      DataBuffer buffer = new DataBufferByte(b, plane);
      img = AWTImageTools.constructImage(b.length, DataBuffer.TYPE_BYTE,
        j2kOptions.width, j2kOptions.height, false, true, buffer,
        j2kOptions.colorModel);
    }
    else if (j2kOptions.bitsPerSample == 16) {
      short[][] s = new short[j2kOptions.channels][plane];
      if (j2kOptions.interleaved) {
        for (int q=0; q<plane; q++) {
          for (int c=0; c<j2kOptions.channels; c++) {
            s[c][q] = DataTools.bytesToShort(data, next, 2,
              j2kOptions.littleEndian);
            next += 2;
          }
        }
      }
      else {
        for (int c=0; c<j2kOptions.channels; c++) {
          for (int q=0; q<plane; q++) {
            s[c][q] = DataTools.bytesToShort(data, next, 2,
              j2kOptions.littleEndian);
            next += 2;
          }
        }
      }
      DataBuffer buffer = new DataBufferUShort(s, plane);
      img = AWTImageTools.constructImage(s.length, DataBuffer.TYPE_USHORT,
        j2kOptions.width, j2kOptions.height, false, true, buffer,
        j2kOptions.colorModel);
    }
    else if (j2kOptions.bitsPerSample == 32) {
      int[][] s = new int[j2kOptions.channels][plane];
      if (j2kOptions.interleaved) {
        for (int q=0; q<plane; q++) {
          for (int c=0; c<j2kOptions.channels; c++) {
            s[c][q] = DataTools.bytesToInt(data, next, 4,
              j2kOptions.littleEndian);
            next += 4;
          }
        }
      }
      else {
        for (int c=0; c<j2kOptions.channels; c++) {
          for (int q=0; q<plane; q++) {
            s[c][q] = DataTools.bytesToInt(data, next, 4,
              j2kOptions.littleEndian);
            next += 4;
          }
        }
      }

      DataBuffer buffer = new UnsignedIntBuffer(s, plane);
      img = AWTImageTools.constructImage(s.length, DataBuffer.TYPE_INT,
        j2kOptions.width, j2kOptions.height, false, true, buffer,
        j2kOptions.colorModel);
    }

    try {
      service.writeImage(out, img, j2kOptions);
    }
    catch (IOException e) {
      throw new FormatException("Could not compress JPEG-2000 data.", e);
    }
    catch (ServiceException e) {
      throw new FormatException("Could not compress JPEG-2000 data.", e);
    }

    return out.toByteArray();
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   * {@link CodecOptions#interleaved interleaved}
   * {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null) {
      throw new IllegalArgumentException("No data to decompress.");
    }
    if (options == null || !(options instanceof JPEG2000CodecOptions)) {
      options = JPEG2000CodecOptions.getDefaultOptions(options);
    }

    byte[] buf = null;
    long fp = in.getFilePointer();
    if (options.maxBytes == 0) {
      buf = new byte[(int) (in.length() - fp)];
    }
    else {
      buf = new byte[(int) (options.maxBytes - fp)];
    }
    in.read(buf);
    return decompress(buf, options);
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   * {@link CodecOptions#interleaved interleaved}
   * {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#decompress(byte[], CodecOptions)
   */
  public byte[] decompress(byte[] buf, CodecOptions options)
    throws FormatException
  {
    initialize();

    if (options == null || !(options instanceof JPEG2000CodecOptions)) {
      options = JPEG2000CodecOptions.getDefaultOptions(options);
    }
    else {
      options = new JPEG2000CodecOptions(options);
    }

    byte[][] single = null;
    WritableRaster b = null;

    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(buf);
      b = (WritableRaster) service.readRaster(
          bis, (JPEG2000CodecOptions) options);
      single = AWTImageTools.getPixelBytes(b, options.littleEndian);

      bis.close();
      b = null;
    }
    catch (IOException e) {
      throw new FormatException("Could not decompress JPEG2000 image. Please " +
        "make sure that jai_imageio.jar is installed.", e);
    }
    catch (ServiceException e) {
      throw new FormatException("Could not decompress JPEG2000 image. Please " +
        "make sure that jai_imageio.jar is installed.", e);
    }

    if (single.length == 1) return single[0];
    byte[] rtn = new byte[single.length * single[0].length];
    if (options.interleaved) {
      int next = 0;
      for (int i=0; i<single[0].length; i++) {
        for (int j=0; j<single.length; j++) {
          rtn[next++] = single[j][i];
        }
      }
    }
    else {
      for (int i=0; i<single.length; i++) {
        System.arraycopy(single[i], 0, rtn, i * single[0].length,
          single[i].length);
      }
    }
    single = null;

    return rtn;
  }

  // -- Helper methods --

  /**
   * Initializes the JAI ImageIO dependency service. This is called at the
   * beginning of the {@link #compress} and {@link #decompress} methods to
   * avoid having the constructor's method definition contain a checked
   * exception.
   *
   * @throws FormatException If there is an error initializing JAI ImageIO
   *   services.
   */
  private void initialize() throws FormatException {
    if (service != null) return;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(JAIIIOService.class);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(JAIIIOServiceImpl.NO_J2K_MSG, de);
    }
  }

}
