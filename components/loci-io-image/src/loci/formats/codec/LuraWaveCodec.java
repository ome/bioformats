//
// LuraWaveCodec.java
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;
import loci.formats.UnsupportedCompressionException;
import loci.formats.services.LuraWaveService;
import loci.formats.services.LuraWaveServiceImpl;

/**
 * This class provides LuraWave decompression, using LuraWave's Java decoding
 * library. Compression is not supported. Decompression requires a LuraWave
 * license code, specified in the lurawave.license system property (e.g.,
 * <code>-Dlurawave.license=XXXX</code> on the command line).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/LuraWaveCodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/LuraWaveCodec.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LuraWaveCodec extends BaseCodec {

  // -- Fields --

  private LuraWaveService service;

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "LuraWave compression not supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    byte[] buf = new byte[(int) in.length()];
    in.read(buf);
    return decompress(buf, options);
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#maxBytes maxBytes}
   *
   * @see Codec#decompress(byte[], CodecOptions)
   */
  public byte[] decompress(byte[] buf, CodecOptions options)
    throws FormatException
  {
    initialize();

    BufferedInputStream stream =
      new BufferedInputStream(new ByteArrayInputStream(buf), 4096);
    try {
      service.initialize(stream);
    }
    catch (DependencyException e) {
      throw new FormatException(LuraWaveServiceImpl.NO_LICENSE_MSG, e);
    }
    catch (ServiceException e) {
      throw new FormatException(LuraWaveServiceImpl.INVALID_LICENSE_MSG, e);
    }
    catch (IOException e) {
      throw new FormatException(e);
    }

    int w = service.getWidth();
    int h = service.getHeight();

    int nbits = 8 * (options.maxBytes / (w * h));

    if (nbits == 8) {
      byte[] image8 = new byte[w * h];
      try {
        service.decodeToMemoryGray8(image8, -1, 1024, 0);
      }
      catch (ServiceException e) {
        throw new FormatException(LuraWaveServiceImpl.INVALID_LICENSE_MSG, e);
      }
      return image8;
    }
    else if (nbits == 16) {
      short[] image16 = new short[w * h];
      try {
        service.decodeToMemoryGray16(image16, 0, -1, 1024, 0, 1, w, 0, 0, w, h);
      }
      catch (ServiceException e) {
        throw new FormatException(LuraWaveServiceImpl.INVALID_LICENSE_MSG, e);
      }

      byte[] output = new byte[w * h * 2];
      for (int i=0; i<image16.length; i++) {
        DataTools.unpackBytes(image16[i], output, i * 2, 2, true);
      }
      return output;
    }

    throw new FormatException("Unsupported bits per pixel: " + nbits);
  }

  // -- Helper methods --

  /**
   * Initializes the LuraWave dependency service. This is called at the
   * beginning of the {@link #decompress} method to avoid having the
   * constructor's method definition contain a checked exception.
   *
   * @throws FormatException If there is an error initializing LuraWave
   * services.
   */
  private void initialize() throws FormatException {
    if (service != null) return;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(LuraWaveService.class);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException(LuraWaveServiceImpl.NO_LURAWAVE_MSG, e);
    }
  }

}
