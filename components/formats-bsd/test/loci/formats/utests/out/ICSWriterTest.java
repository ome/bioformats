/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2016 - 2017 Open Microscopy Environment:
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

package loci.formats.utests.out;

import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.in.ICSReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.ICSWriter;
import ome.units.UNITS;
import ome.units.quantity.Length;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the functionality of ICSWriter
 */
public class ICSWriterTest {

  private ICSWriter writer;
  private static int percentageOfSaveBytesTests = 0;
  
  @DataProvider(name = "writeParams")
  public Object[][] createParams() {
    if (percentageOfSaveBytesTests == 0) {
      return new Object[][] {{0, false, false, 0, 0, 0, null, 0, false}};
    }
    int[] tileSizes = {0};
    int[] channelCounts = {1, 3};
    int[] seriesCounts = {1, 3};
    int[] timeCounts = {1, 5};
    String[] compressions = {WriterUtilities.COMPRESSION_UNCOMPRESSED};
    return WriterUtilities.getData(tileSizes, channelCounts, seriesCounts, timeCounts, compressions, percentageOfSaveBytesTests);
  }

  @BeforeClass
  public void readProperty() throws Exception {
    percentageOfSaveBytesTests = WriterUtilities.getPropValue("testng.runWriterSaveBytesTests");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    writer = new ICSWriter();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    writer.close();
  }

  @Test
  public void testGetPixelTypes() {
    Assert.assertArrayEquals(WriterUtilities.pixelTypesICS, writer.getPixelTypes(WriterUtilities.COMPRESSION_UNCOMPRESSED));
  }
  
  @Test
  public void testIncompatiblePhysicalSizeUnit() throws DependencyException, ServiceException, FormatException, IOException {
    IMetadata metadata = WriterUtilities.createMetadata();
    Length physicalSizeX = FormatTools.getPhysicalSizeX(10.0, UNITS.PIXEL);
    metadata.setPixelsPhysicalSizeX(physicalSizeX, 0);
    Length physicalSizeY = FormatTools.getPhysicalSizeY(10.0, UNITS.PIXEL);
    metadata.setPixelsPhysicalSizeY(physicalSizeY, 0);
    Length physicalSizeZ = FormatTools.getPhysicalSizeZ(10.0, UNITS.PIXEL);
    metadata.setPixelsPhysicalSizeZ(physicalSizeZ, 0);
    writer.setMetadataRetrieve(metadata);

    File tmp = File.createTempFile("icsWriterTest", ".ics");
    tmp.deleteOnExit();

    writer.setId(tmp.getAbsolutePath());
  }

  // TODO: fix saveBytes parameters
  // @Test(dataProvider = "writeParams")
  public void testSaveBytes(int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression, int pixelType, boolean bigTiff) throws Exception {
    if (percentageOfSaveBytesTests == 0) return;

    File tmp = File.createTempFile("icsWriterTest", ".ics");
    tmp.deleteOnExit();

    String pixelTypeString = FormatTools.getPixelTypeString(pixelType);
    writer.setMetadataRetrieve(WriterUtilities.createMetadata(pixelTypeString, rgbChannels, seriesCount, littleEndian, sizeT));
    writer.setCompression(compression);
    writer.setInterleaved(interleaved);
    if (tileSize != 0) {
      writer.setTileSizeX(tileSize);
      writer.setTileSizeY(tileSize);
    }
    writer.setId(tmp.getAbsolutePath());

    int bytes = FormatTools.getBytesPerPixel(pixelType);
    byte[] plane = WriterUtilities.getPlane(WriterUtilities.PLANE_WIDTH, WriterUtilities.PLANE_HEIGHT, bytes * rgbChannels);
    Plane originalPlane = new Plane(plane, littleEndian,
      !writer.isInterleaved(), rgbChannels, FormatTools.getPixelTypeString(pixelType));

    for (int s=0; s<seriesCount; s++) {
      writer.setSeries(s);
      for (int t=0; t<sizeT; t++) {
        writer.saveBytes(t, plane);
      }
    }

    ICSReader reader = new ICSReader();
    reader.setId(tmp.getAbsolutePath());

    for (int s=0; s<reader.getSeriesCount(); s++) {
      reader.setSeries(s);
      assertEquals(reader.getSizeC(), rgbChannels);
      int imageCount = reader.isRGB() ? seriesCount * sizeT : rgbChannels * sizeT * seriesCount;
      assertEquals(reader.getImageCount(), imageCount);
      for (int image=0; image<reader.getImageCount(); image++) {
        byte[] readPlane = reader.openBytes(image);
        Plane newPlane = new Plane(readPlane, reader.isLittleEndian(),
          !reader.isInterleaved(), reader.getRGBChannelCount(),
          FormatTools.getPixelTypeString(reader.getPixelType()));
        assert(originalPlane.equals(newPlane));
      }
    }
    tmp.delete();
    reader.close();
  }

  // TODO: fix saveBytes parameters
  // @Test(dataProvider = "writeParams")
  public void testSaveBytesInMemory(int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels,
    int seriesCount, int sizeT, String compression, int pixelType, boolean bigTiff) throws Exception
  {
    if (percentageOfSaveBytesTests == 0) return;

    ByteArrayHandle handle = new ByteArrayHandle();
    String id = Math.random() + "-" + System.currentTimeMillis() + ".ics";
    Location.mapFile(id, handle);

    String pixelTypeString = FormatTools.getPixelTypeString(pixelType);
    writer.setMetadataRetrieve(WriterUtilities.createMetadata(pixelTypeString, rgbChannels, seriesCount, littleEndian, sizeT));
    writer.setCompression(compression);
    writer.setInterleaved(interleaved);
    if (tileSize != 0) {
      writer.setTileSizeX(tileSize);
      writer.setTileSizeY(tileSize);
    }
    writer.setId(id);

    int bpp = FormatTools.getBytesPerPixel(pixelType);
    byte[] plane = WriterUtilities.getPlane(WriterUtilities.PLANE_WIDTH, WriterUtilities.PLANE_HEIGHT, bpp * rgbChannels);
    Plane originalPlane = new Plane(plane, littleEndian,
      !writer.isInterleaved(), rgbChannels, FormatTools.getPixelTypeString(pixelType));
    
    for (int s=0; s<seriesCount; s++) {
      writer.setSeries(s);
      for (int t=0; t<sizeT; t++) {
        writer.saveBytes(t, plane);
      }
    }

    ByteBuffer bytes = handle.getByteBuffer();
    byte[] file = new byte[(int) handle.length()];
    bytes.position(0);
    bytes.get(file);
    handle = new ByteArrayHandle(file);
    Location.mapFile(id, handle);

    ICSReader reader = new ICSReader();
    reader.setId(id);

    for (int s=0; s<reader.getSeriesCount(); s++) {
      reader.setSeries(s);
      assertEquals(reader.getSizeC(), rgbChannels);
      int imageCount = reader.isRGB() ? seriesCount * sizeT : rgbChannels * sizeT * seriesCount;
      assertEquals(reader.getImageCount(), imageCount);
      for (int image=0; image<reader.getImageCount(); image++) {
        byte[] readPlane = reader.openBytes(image);
        Plane newPlane = new Plane(readPlane, reader.isLittleEndian(),
          !reader.isInterleaved(), reader.getRGBChannelCount(),
          FormatTools.getPixelTypeString(reader.getPixelType()));
        assert(originalPlane.equals(newPlane));
      }
    }
    reader.close();
    Location.mapFile(id, null);
  }

}
