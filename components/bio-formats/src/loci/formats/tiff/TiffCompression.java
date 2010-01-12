//
// TiffCompression.java
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

package loci.formats.tiff;

import java.io.IOException;

import loci.common.DataTools;
import loci.common.LogTools;
import loci.formats.FormatException;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.LZWCodec;
import loci.formats.codec.LuraWaveCodec;
import loci.formats.codec.NikonCodec;
import loci.formats.codec.PackbitsCodec;
import loci.formats.codec.ZlibCodec;

/**
 * Utility class for performing compression operations with a TIFF file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tiff/TiffCompression.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tiff/TiffCompression.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public final class TiffCompression {

  // -- Constants --

  // TODO: Investigate using Java 1.5 enum instead of int enumeration.
  //       http://javahowto.blogspot.com/2008/04/java-enum-examples.html
  public static final int UNCOMPRESSED = 1;
  public static final int CCITT_1D = 2;
  public static final int GROUP_3_FAX = 3;
  public static final int GROUP_4_FAX = 4;
  public static final int LZW = 5;
  //public static final int JPEG = 6;
  public static final int JPEG = 7;
  public static final int PACK_BITS = 32773;
  public static final int PROPRIETARY_DEFLATE = 32946;
  public static final int DEFLATE = 8;
  public static final int THUNDERSCAN = 32809;
  public static final int JPEG_2000 = 33003;
  public static final int JPEG_2000_LOSSY = 33004;
  public static final int ALT_JPEG_2000 = 33005;
  public static final int ALT_JPEG = 33007;
  public static final int NIKON = 34713;
  public static final int LURAWAVE = 65535;

  // -- Constructor --

  private TiffCompression() { }

  // -- TiffCompression methods --

  /** Returns the name of the given codec. */
  public static String getCodecName(int codec) {
    switch (codec) {
      case UNCOMPRESSED:
        return "Uncompressed";
      case CCITT_1D:
        return "CCITT Group 3 1-Dimensional Modified Huffman";
      case GROUP_3_FAX:
        return "CCITT T.4 bi-level encoding (Group 3 Fax)";
      case GROUP_4_FAX:
        return "CCITT T.6 bi-level encoding (Group 4 Fax)";
      case LZW:
        return "LZW";
      case JPEG:
      case ALT_JPEG:
        return "JPEG";
      case PACK_BITS:
        return "PackBits";
      case DEFLATE:
      case PROPRIETARY_DEFLATE:
        return "Deflate (Zlib)";
      case THUNDERSCAN:
        return "Thunderscan";
      case JPEG_2000:
        return "JPEG-2000";
      case JPEG_2000_LOSSY:
        return "JPEG-2000 Lossy";
      case ALT_JPEG_2000:
        return "Aperio JPEG-2000";
      case NIKON:
        return "Nikon";
      case LURAWAVE:
        return "LuraWave";
    }
    return null;
  }

  // -- TiffCompression methods - decompression --

  /** Returns true if the given decompression scheme is supported. */
  public static boolean isSupportedDecompression(int decompression) {
    return
      decompression == UNCOMPRESSED ||
      decompression == LZW ||
      decompression == JPEG ||
      decompression == ALT_JPEG ||
      decompression == JPEG_2000 ||
      decompression == JPEG_2000_LOSSY ||
      decompression == ALT_JPEG_2000 ||
      decompression == PACK_BITS ||
      decompression == PROPRIETARY_DEFLATE ||
      decompression == DEFLATE ||
      decompression == NIKON ||
      decompression == LURAWAVE;
  }

  /** Decodes a strip of data compressed with the given compression scheme. */
  public static byte[] uncompress(byte[] input, int compression,
    CodecOptions options) throws FormatException, IOException
  {
    if (compression < 0) compression += 65536;

    if (!isSupportedDecompression(compression)) {
      String compressionName = getCodecName(compression);
      String message = null;
      if (compressionName != null) {
        message =
          "Sorry, " + compressionName + " compression mode is not supported";
      }
      else message = "Unknown Compression type (" + compression + ")";
      throw new FormatException(message);
    }

    Codec codec = null;

    if (compression == UNCOMPRESSED) return input;
    else if (compression == LZW) codec = new LZWCodec();
    else if (compression == JPEG || compression == ALT_JPEG) {
      codec = new JPEGCodec();
    }
    else if (compression == JPEG_2000 || compression == JPEG_2000_LOSSY ||
      compression == ALT_JPEG_2000)
    {
      codec = new JPEG2000Codec();
    }
    else if (compression == PACK_BITS) codec = new PackbitsCodec();
    else if (compression == PROPRIETARY_DEFLATE || compression == DEFLATE) {
      codec = new ZlibCodec();
    }
    else if (compression == NIKON) codec = new NikonCodec();
    else if (compression == LURAWAVE) codec = new LuraWaveCodec();
    if (codec != null) return codec.decompress(input, options);
    throw new FormatException("Unhandled compression (" + compression + ")");
  }

  /** Undoes in-place differencing according to the given predictor value. */
  public static void undifference(byte[] input, IFD ifd)
    throws FormatException
  {
    int predictor = ifd.getIFDIntValue(IFD.PREDICTOR, false, 1);
    if (predictor == 2) {
      LogTools.debug("reversing horizontal differencing");
      int[] bitsPerSample = ifd.getBitsPerSample();
      int len = bitsPerSample.length;
      long width = ifd.getImageWidth();
      boolean little = ifd.isLittleEndian();
      int planarConfig = ifd.getPlanarConfiguration();

      if (planarConfig == 2 || bitsPerSample[len - 1] == 0) len = 1;
      if (bitsPerSample[0] <= 8) {
        for (int b=0; b<input.length; b++) {
          if (b / len % width == 0) continue;
          input[b] += input[b - len];
        }
      }
      else if (bitsPerSample[0] <= 16) {
        short[] s = (short[]) DataTools.makeDataArray(input, 2, false, little);
        for (int b=0; b<s.length; b++) {
          if (b / len % width == 0) continue;
          s[b] += s[b - len];
        }
        for (int i=0; i<s.length; i++) {
          DataTools.unpackBytes(s[i], input, i*2, 2, little);
        }
      }
    }
    else if (predictor != 1) {
      throw new FormatException("Unknown Predictor (" + predictor + ")");
    }
  }

  // -- TiffCompression methods - compression --

  /** Returns true if the given compression scheme is supported. */
  public static boolean isSupportedCompression(int compression) {
    return compression == UNCOMPRESSED || compression == LZW ||
      compression == JPEG || compression == JPEG_2000 ||
      compression == JPEG_2000_LOSSY || compression == ALT_JPEG_2000;
  }

  /** Encodes a strip of data with the given compression scheme. */
  public static byte[] compress(byte[] input, IFD ifd)
    throws FormatException, IOException
  {
    int compression = ifd.getIFDIntValue(IFD.COMPRESSION, false, UNCOMPRESSED);

    if (!isSupportedCompression(compression)) {
      String compressionName = getCodecName(compression);
      if (compressionName != null) {
        throw new FormatException("Sorry, " + compressionName +
          " compression mode is not supported");
      }
      else {
        throw new FormatException(
          "Unknown Compression type (" + compression + ")");
      }
    }

    CodecOptions options = new CodecOptions();
    options.width = (int) ifd.getImageWidth();
    options.height = (int) ifd.getImageLength();
    options.bitsPerSample = ifd.getBitsPerSample()[0];
    options.channels = ifd.getSamplesPerPixel();
    options.littleEndian = ifd.isLittleEndian();
    options.interleaved = true;
    options.signed = false;

    if (compression == UNCOMPRESSED) return input;
    else if (compression == LZW) {
      return new LZWCodec().compress(input, options);
    }
    else if (compression == JPEG) {
      return new JPEGCodec().compress(input, options);
    }
    else if (compression == JPEG_2000 || compression == ALT_JPEG_2000) {
      options.lossless = true;
      JPEG2000CodecOptions j2kOptions =
        JPEG2000CodecOptions.getDefaultOptions(options);
      return new JPEG2000Codec().compress(input, j2kOptions);
    }
    else if (compression == JPEG_2000_LOSSY) {
      options.lossless = false;
      JPEG2000CodecOptions j2kOptions =
        JPEG2000CodecOptions.getDefaultOptions(options);
      return new JPEG2000Codec().compress(input, j2kOptions);
    }
    throw new FormatException("Unhandled compression (" + compression + ")");
  }

  /** Performs in-place differencing according to the given predictor value. */
  public static void difference(byte[] input, int[] bitsPerSample,
    long width, int planarConfig, int predictor) throws FormatException
  {
    if (predictor == 2) {
      LogTools.debug("performing horizontal differencing");
      for (int b=input.length-1; b>=0; b--) {
        if (b / bitsPerSample.length % width == 0) continue;
        input[b] -= input[b - bitsPerSample.length];
      }
    }
    else if (predictor != 1) {
      throw new FormatException("Unknown Predictor (" + predictor + ")");
    }
  }

}
