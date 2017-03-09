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

package loci.formats.codec;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
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
  @Override
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

    ByteArrayHandle handle = new ByteArrayHandle();
    RandomAccessOutputStream out = new RandomAccessOutputStream(handle);
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
            if (next < data.length) {
              b[c][q] = data[next++];
            }
            else {
              break;
            }
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
    finally {
      try {
        out.close();
      }
      catch (IOException e) {
        throw new FormatException("Failed to close RandomAccessOutputStream.", e);
      }
    }

    try {
      RandomAccessInputStream is = new RandomAccessInputStream(handle);
      try {
        is.seek(0);
        if (!j2kOptions.writeBox) {
          while (is.getFilePointer() < is.length()) {
            // checking both 0xff4f and 0xff51 prevents this from
            // stopping at an escaped 0xff4f (i.e. 0x00ff4f) sequence
            // in the box
            while ((is.readShort() & 0xffff) != 0xff4f) {
              is.seek(is.getFilePointer() - 1);
            }
            if ((is.readShort() & 0xffff) == 0xff51) {
              break;
            }
            is.seek(is.getFilePointer() - 2);
          }
          is.seek(is.getFilePointer() - 4);
        }
        byte[] buf = new byte[(int) (is.length() - is.getFilePointer())];
        is.readFully(buf);
        return buf;
      }
      finally {
        is.close();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   * {@link CodecOptions#interleaved interleaved}
   * {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  @Override
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
  @Override
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
    int bpp = options.bitsPerSample / 8;

    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(buf);
      b = (WritableRaster) service.readRaster(
          bis, (JPEG2000CodecOptions) options);
      single = AWTImageTools.getPixelBytes(b, options.littleEndian);
      bpp = single[0].length / (b.getWidth() * b.getHeight());

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
      for (int i=0; i<single[0].length/bpp; i++) {
        for (int j=0; j<single.length; j++) {
          for (int bb=0; bb<bpp; bb++) {
            rtn[next++] = single[j][i * bpp + bb];
          }
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
