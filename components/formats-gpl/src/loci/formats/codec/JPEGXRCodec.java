/*
 */

package loci.formats.codec;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;
import loci.formats.UnsupportedCompressionException;
import loci.formats.services.JPEGXRService;

/**
 */
public class JPEGXRCodec extends BaseCodec {

  // -- Fields --

  private JPEGXRService service;

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "JPEG-XR compression not supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
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
  @Override
  public byte[] decompress(byte[] buf, CodecOptions options)
    throws FormatException
  {
    initialize();

    return service.decompress(buf);
  }

  // -- Helper methods --

  /**
   * Initializes the JPEG-XR dependency service. This is called at the
   * beginning of the {@link #decompress} method to avoid having the
   * constructor's method definition contain a checked exception.
   *
   * @throws FormatException If there is an error initializing JPEG-XR
   * services.
   */
  private void initialize() throws FormatException {
    if (service != null) return;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(JPEGXRService.class);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException("JPEG-XR library not available", e);
    }
  }

}
