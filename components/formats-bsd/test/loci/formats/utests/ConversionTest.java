/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

package loci.formats.utests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import loci.common.Constants;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.codec.CompressionType;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import org.testng.annotations.Test;

/**
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public class ConversionTest {

  private static final int WIDTH = 64;
  private static final int HEIGHT = 64;
  private static final boolean[] LITTLE_ENDIAN = {true, false};
  private static final String[] DEFAULT_TYPES = {"int8", "uint8", "int16",
    "uint16", "int32", "uint32", "float", "double"};

  private byte[] getPlane(int width, int height, int bytes) {
    byte[] plane = new byte[width * height * bytes];
    for (int i=0; i<plane.length; i++) {
      plane[i] = (byte) i;
    }
    return plane;
  }

  private IMetadata createMetadata(String pixelType, int rgbChannels,
    int seriesCount, boolean littleEndian) throws Exception
  {
    IMetadata metadata;

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      metadata = service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    for (int i=0; i<seriesCount; i++) {
      MetadataTools.populateMetadata(metadata, i, "image #" + i, littleEndian,
        "XYCZT", pixelType, WIDTH, HEIGHT, 1, rgbChannels, 1, rgbChannels);
    }

    return metadata;
  }

  private void testCompressDecompress(String ext, String compression,
    boolean lossy, int seriesCount, String pixelType) throws Exception
  {
    testCompressDecompress(
      ext, compression, lossy, 1, seriesCount, pixelType, true);
    testCompressDecompress(
      ext, compression, lossy, 1, seriesCount, pixelType, false);
    testCompressDecompress(
      ext, compression, lossy, 3, seriesCount, pixelType, true);
    testCompressDecompress(
      ext, compression, lossy, 3, seriesCount, pixelType, false);
  }

  private void testCompressDecompress(String ext, String compression,
    boolean lossy, int rgbChannels, int seriesCount, String pixelType,
    boolean littleEndian)
    throws Exception
  {
    File tmp = File.createTempFile("conversionTest", ext);
    tmp.deleteOnExit();
    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(createMetadata(
      pixelType, rgbChannels, seriesCount, littleEndian));
    writer.setId(tmp.getAbsolutePath());

    int bytes = FormatTools.getBytesPerPixel(pixelType);
    byte[] plane = getPlane(WIDTH, HEIGHT, bytes * rgbChannels);
    Plane originalPlane = new Plane(plane, littleEndian,
      !writer.isInterleaved(), rgbChannels, pixelType);

    for (int s=0; s<seriesCount; s++) {
      writer.setSeries(s);
      writer.saveBytes(0, plane);
    }

    writer.close();

    ImageReader reader = new ImageReader();
    reader.setId(tmp.getAbsolutePath());

    assertEquals(reader.getSeriesCount(), seriesCount);

    for (int s=0; s<seriesCount; s++) {
      reader.setSeries(s);

      assertEquals(reader.getSizeC(), rgbChannels);
      assertTrue(reader.getImageCount() == rgbChannels || reader.isRGB());

      byte[] readPlane = reader.openBytes(0);

      if (!lossy) {
        Plane newPlane = new Plane(readPlane, reader.isLittleEndian(),
          !reader.isInterleaved(), reader.getRGBChannelCount(),
          FormatTools.getPixelTypeString(reader.getPixelType()));

        assertTrue(originalPlane.equals(newPlane), tmp.getAbsolutePath());
      }
    }

    reader.close();
  }

  @Test
  public void testTIFF() throws Exception {
    String ext = ".tiff";

    String[] jpegTypes = {"int8", "uint8", "int16", "uint16"};
    String[] jpeg2000Types = {"int8", "uint8", "int16", "uint16", "int32",
      "uint32", "float"};

    for (String jpegType : jpegTypes) {
      String compression = CompressionType.JPEG.getCompression();
      testCompressDecompress(ext, compression, true, 1, jpegType);
    }

    for (String jpeg2KType : jpeg2000Types) {
      String compression = CompressionType.J2K.getCompression();
      testCompressDecompress(ext, compression, false, 1, jpeg2KType);
    }

    for (String type : DEFAULT_TYPES) {
      String compression = CompressionType.UNCOMPRESSED.getCompression();
      testCompressDecompress(ext, compression, false, 1, type);

      compression = CompressionType.LZW.getCompression();
      testCompressDecompress(ext, compression, false, 1, type);
    }
  }

  @Test
  public void testOMETIFF() throws Exception {
    String ext = ".ome.tiff";

    String[] jpegTypes = {"int8", "uint8", "int16", "uint16"};
    String[] jpeg2000Types = {"int8", "uint8", "int16", "uint16", "int32",
      "uint32", "float"};
    int[] seriesCounts = {1, 2};

    for (int seriesCount : seriesCounts) {
      for (String jpegType : jpegTypes) {
        String compression = CompressionType.JPEG.getCompression();
        testCompressDecompress(ext, compression, true, seriesCount, jpegType);
      }

      for (String jpeg2KType : jpeg2000Types) {
        String compression = CompressionType.J2K.getCompression();
        testCompressDecompress(
          ext, compression, false, seriesCount, jpeg2KType);
      }

      for (String type : DEFAULT_TYPES) {
        String compression = CompressionType.UNCOMPRESSED.getCompression();
        testCompressDecompress(ext, compression, false, seriesCount, type);

        compression = CompressionType.LZW.getCompression();
        testCompressDecompress(ext, compression, false, seriesCount, type);
      }
    }
  }

  @Test
  public void testOMEXML() throws Exception {
    String ext = ".ome";

    int[] seriesCounts = {1, 2};

    String[] types = {"int8", "uint8", "int16", "uint16", "int32",
      "uint32", "float"};

    for (int seriesCount : seriesCounts) {
      for (String type : types) {
        String compression = CompressionType.UNCOMPRESSED.getCompression();
        testCompressDecompress(ext, compression, false, seriesCount, type);

        compression = CompressionType.ZLIB.getCompression();
        testCompressDecompress(ext, compression, false, seriesCount, type);
      }
    }
  }

  @Test
  public void testAPNG() throws Exception {
    String ext = ".png";

    String[] pixelTypes = {"int8", "uint8", "int16", "uint16"};

    for (String type : pixelTypes) {
      String compression = CompressionType.UNCOMPRESSED.getCompression();
      testCompressDecompress(ext, compression, false, 1, type);
    }
  }

  @Test
  public void testAVI() throws Exception {
    String ext = ".avi";

    String compression = CompressionType.UNCOMPRESSED.getCompression();
    testCompressDecompress(ext, compression, false, 1, "uint8");
  }

  @Test
  public void testEPS() throws Exception {
    String ext = ".eps";

    String compression = CompressionType.UNCOMPRESSED.getCompression();
    testCompressDecompress(ext, compression, false, 1, "uint8");
  }

  @Test
  public void testICS() throws Exception {
    String ext = ".ics";

    String[] pixelTypes = {"int8", "uint8", "int16", "uint16", "int32",
      "uint32", "float"};

    for (String type : pixelTypes) {
      String compression = CompressionType.UNCOMPRESSED.getCompression();
      testCompressDecompress(ext, compression, false, 1, type);
    }
  }

  @Test
  public void testJPEG2000() throws Exception {
    String ext = ".jp2";

    String[] pixelTypes = {"int8", "uint8", "int16", "uint16"};

    for (String type : pixelTypes) {
      String compression = CompressionType.J2K.getCompression();
      testCompressDecompress(ext, compression, false, 6, type);

      compression = CompressionType.J2K_LOSSY.getCompression();
      testCompressDecompress(ext, compression, true, 6, type);
    }
  }

  @Test
  public void testJPEG() throws Exception {
    String ext = ".jpg";

    String compression = CompressionType.JPEG.getCompression();
    testCompressDecompress(ext, compression, true, 1, "uint8");
  }

  @Test
  public void testQuickTime() throws Exception {
    String ext = ".mov";

    String compression = CompressionType.UNCOMPRESSED.getCompression();
    testCompressDecompress(ext, compression, false, 1, "uint8");
  }


  class Plane {
    public ByteBuffer backingBuffer;
    public boolean rgbPlanar;
    public int rgbChannels;
    public String pixelType;

    public Plane(byte[] buffer, boolean littleEndian, boolean planar,
      int rgbChannels, String pixelType)
    {
      backingBuffer = ByteBuffer.wrap(buffer);
      backingBuffer.order(
        littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
      this.rgbPlanar = planar;
      this.pixelType = pixelType;
      this.rgbChannels = rgbChannels;
    }

    public boolean equals(Plane other) {
      backingBuffer.position(0);
      other.backingBuffer.position(0);

      int bytes = FormatTools.getBytesPerPixel(pixelType);
      boolean fp =
        FormatTools.isFloatingPoint(FormatTools.pixelTypeFromString(pixelType));

      while (backingBuffer.position() < backingBuffer.capacity()) {
        int otherPos = backingBuffer.position();
        if (rgbPlanar != other.rgbPlanar) {
          int channel = -1;
          int pixel = -1;

          int pos = backingBuffer.position() / bytes;
          int capacity = backingBuffer.capacity();

          if (rgbPlanar) {
            pixel = pos % (capacity / (rgbChannels * bytes));
            channel = pos / (capacity / (rgbChannels * bytes));
          }
          else {
            channel = pos % rgbChannels;
            pixel = pos / rgbChannels;
          }

          if (other.rgbPlanar) {
            otherPos = channel * (capacity / rgbChannels) + pixel * bytes;
          }
          else {
            otherPos = (pixel * rgbChannels + channel) * bytes;
          }
        }

        if (otherPos >= other.backingBuffer.capacity()) {
          break;
        }

        other.backingBuffer.position(otherPos);

        switch (bytes) {
          case 1:
            byte thisB = backingBuffer.get();
            byte otherB = other.backingBuffer.get();

            if (thisB != otherB) {
              if (!pixelType.equals(other.pixelType)) {
                if ((byte) (thisB - 128) != otherB) {
                  return false;
                }
              }
              else {
                return false;
              }
            }
            break;
          case 2:
            short thisS = backingBuffer.getShort();
            short otherS = other.backingBuffer.getShort();

            if (thisS != otherS) {
              if (!pixelType.equals(other.pixelType)) {
                if ((short) (thisS - 32768) != otherS) {
                  return false;
                }
              }
              else {
                return false;
              }
            }
            break;
          case 4:
            if (fp) {
              float thisF = backingBuffer.getFloat();
              float otherF = other.backingBuffer.getFloat();

              if (Math.abs(thisF - otherF) > Constants.EPSILON) {
                return false;
              }
            }
            else {
              int thisI = backingBuffer.getInt();
              int otherI = other.backingBuffer.getInt();

              if (thisI != otherI) {
                return false;
              }
            }
            break;
          case 8:
            if (fp) {
              double thisD = backingBuffer.getDouble();
              double otherD = other.backingBuffer.getDouble();

              if (Math.abs(thisD - otherD) > Constants.EPSILON) {
                return false;
              }
            }
            else {
              long thisL = backingBuffer.getLong();
              long otherL = other.backingBuffer.getLong();

              if (thisL != otherL) {
                return false;
              }
            }
            break;
        }
      }

      return true;
    }
  }

}
