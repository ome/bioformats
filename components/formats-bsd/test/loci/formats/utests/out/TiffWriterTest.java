/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.assertEquals;
import java.io.IOException;
import org.junit.Assert;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.codec.CompressionType;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.utests.tiff.TiffWriterMock;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the functionality of TiffWriter
 */
public class TiffWriterTest {

  private IFD ifd;
  private TiffWriter writer;
  private IMetadata metadata;
  
  final long BIG_TIFF_CUTOFF = (long) 1024 * 1024 * 3990;
  private static final int SIZE_X = 1024;
  private static final int SIZE_Y = 1024;
  private static final int SIZE_Z = 4;
  private static final int SIZE_C = 1;
  private static final int SIZE_T = 20;
  private static final byte[] buf = new byte[1024*1024];
  private static final String COMPRESSION_UNCOMPRESSED = CompressionType.UNCOMPRESSED.getCompression();
  private static final String COMPRESSION_LZW = CompressionType.LZW.getCompression();
  private static final String COMPRESSION_J2K = CompressionType.J2K.getCompression();
  private static final String COMPRESSION_J2K_LOSSY = CompressionType.J2K_LOSSY.getCompression();
  private static final String COMPRESSION_JPEG = CompressionType.JPEG.getCompression();
  private static final int[] pixelTypesJPEG = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16, FormatTools.UINT16};
  private static final int[] pixelTypesJ2K = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,FormatTools.UINT16, 
      FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT};
  private static final int[] pixelTypesOther = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT, FormatTools.DOUBLE};

  @DataProvider(name = "bigTiffSuffixes")
  public Object[][] createSuffixes() {
    return new Object[][] {{"tf2"}, {"tf8"}, {"btf"}, {"tif"}, {"tiff"}};
  }

  @DataProvider(name = "codecs")
  public Object[][] createCodecs() {
    return new Object[][] {{null, pixelTypesOther}, {COMPRESSION_UNCOMPRESSED, pixelTypesOther}, {COMPRESSION_LZW, pixelTypesOther}, 
      {COMPRESSION_J2K, pixelTypesJ2K}, {COMPRESSION_J2K_LOSSY, pixelTypesOther}, {COMPRESSION_JPEG, pixelTypesJPEG}};
  }

  @BeforeMethod
  public void setUp() throws Exception {
    ifd = new IFD();
    writer = new TiffWriterMock();
    ServiceFactory sf = new ServiceFactory();
    OMEXMLService service = sf.getInstance(OMEXMLService.class);
    metadata = service.createOMEXMLMetadata();
    metadata.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    metadata.setPixelsSizeX(new PositiveInteger(SIZE_X), 0);
    metadata.setPixelsSizeY(new PositiveInteger(SIZE_Y), 0);
    metadata.setPixelsSizeT(new PositiveInteger(SIZE_T), 0);
    metadata.setPixelsSizeZ(new PositiveInteger(SIZE_Z), 0);
    metadata.setPixelsSizeC(new PositiveInteger(SIZE_C), 0);
    metadata.setPixelsType(PixelType.UINT8, 0);
    metadata.setPixelsBinDataBigEndian(true, 0, 0);
    metadata.setImageID("Image:1", 0);
    metadata.setPixelsID("Pixels:1", 0);
    metadata.setChannelID("Channel:1", 0, 0);
    metadata.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
    for (int i = 0; i < 1024 *1024; i++) {
      buf[i] = (byte)(i%255);
    }
  }

  @AfterMethod
  public void tearDown() throws Exception {
    writer.close();
  }

  @Test
  public void testSetBigTiffFileTooLarge() throws IOException, FormatException {
    // Test that no exception is thrown while below the big tiff limit (2147483648L)
    // Exception is thrown when size is out.length() + 2 * (width * height * c * bytesPerPixel)
    writer.setMetadataRetrieve(metadata);
    ((TiffWriterMock)writer).createOutputBuffer(true);
    long length = 4294967296L - (buf.length * 4);
    ((TiffWriterMock)writer).setBufferLength(length);
    writer.setId("test.tiff");
    writer.saveBytes(0, buf, ifd);

    //Test format exception is thrown after the big tiff limit (2147483648L)
    boolean thrown = false;
    try {
      writer.saveBytes(1, buf, ifd);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("File is too large; call setBigTiff(true)")) {
        thrown = true;
      }
    }
    assert(thrown);
  }
  
  @Test
  public void testSetBigTiff() throws IOException, FormatException {
    //Test that no exception is thrown after setting big tiff
    writer.setMetadataRetrieve(metadata);
    ((TiffWriterMock)writer).createOutputBuffer(true);
    long length = 4294967296L - (buf.length * 2);
    ((TiffWriterMock)writer).setBufferLength(length);
    writer.setBigTiff(true);
    writer.setId("test.tiff");
    writer.saveBytes(0, buf, ifd);
  }

  @Test
  public void testSetBigTiffAutomatic() throws IOException, FormatException {
    //Test that no exception is thrown when bigTiff is set automatically due to size
    metadata.setPixelsSizeT(new PositiveInteger(1000), 0);
    writer.setMetadataRetrieve(metadata);
    ((TiffWriterMock)writer).createOutputBuffer(true);
    long length = 4294967296L;
    ((TiffWriterMock)writer).setBufferLength(length);
    writer.setId("test.tiff");
    writer.saveBytes(0, buf, ifd);
  }

  @Test(dataProvider = "bigTiffSuffixes")
  public void testSetBigTiffSuffixes(String suffix) throws IOException, FormatException {
    //Test that no exception is thrown when bigTiff is set automatically due to size
    writer.setMetadataRetrieve(metadata);
    ((TiffWriterMock)writer).createOutputBuffer(true);
    long length = 4294967296L;
    ((TiffWriterMock)writer).setBufferLength(length);
    writer.setId("test." + suffix);
    boolean thrown = false;
    try {
      writer.saveBytes(0, buf, ifd);
    }
    catch(FormatException e) {
      thrown = true;
    }
    if (suffix.contains("tif")) {
      assertEquals(true,thrown);
    }
    else {
      assertEquals(false,thrown);
    }
  }

  @Test(dataProvider = "codecs")
  public void testgetPixelTypes(String codec, int[] pixelTypes) {
    Assert.assertArrayEquals(pixelTypes, writer.getPixelTypes(codec));
  }

  @Test
  public void testGetPlaneCount() throws IOException, FormatException {
    writer.setMetadataRetrieve(metadata);
    writer.setSeries(0);
    assertEquals(SIZE_T * SIZE_Z * SIZE_C, writer.getPlaneCount());
    metadata.setPixelsSizeC(new PositiveInteger(4), 0);
    metadata.setPixelsType(PixelType.INT16, 0);
    writer.setMetadataRetrieve(metadata);
    assertEquals(SIZE_T * SIZE_Z * 4, writer.getPlaneCount());
  }

  @Test
  public void testGetTileSizeX() throws IOException, FormatException {
    writer.setMetadataRetrieve(metadata);
    assertEquals(SIZE_X, writer.getTileSizeX());
    writer.close();
    writer = new TiffWriter();
    metadata.setPixelsSizeX(new PositiveInteger(100), 0);
    writer.setMetadataRetrieve(metadata);
    assertEquals(100, writer.getTileSizeX());
  }

  @Test
  public void testSetTileSizeX() {
    writer.setMetadataRetrieve(metadata);
    try {
      for (int i = 16; i < SIZE_X; i+=16) {
        writer.setTileSizeX(i);
        assertEquals(i, writer.getTileSizeX());
      }
      writer.setTileSizeX(SIZE_X);
      assertEquals(SIZE_X, writer.getTileSizeX());
      for (int i = 8; i < 24; i++) {
        writer.setTileSizeX(i);
        assertEquals(16, writer.getTileSizeX());
      }
      for (int i = 24; i < 40; i++) {
        writer.setTileSizeX(i);
        assertEquals(32, writer.getTileSizeX());
      }
    }
    catch(FormatException fe) {
      assert(false);
    }
  }

  @Test
  public void testGetTileSizeY() throws IOException, FormatException {
    writer.setMetadataRetrieve(metadata);
    assertEquals(SIZE_Y, writer.getTileSizeY());
    writer.close();
    writer = new TiffWriter();
    metadata.setPixelsSizeY(new PositiveInteger(100), 0);
    writer.setMetadataRetrieve(metadata);
    assertEquals(100, writer.getTileSizeY());
  }

  @Test
  public void testSetTileSizeY() {
    writer.setMetadataRetrieve(metadata);
    try {
      for (int i = 16; i < SIZE_Y; i+=16) {
        writer.setTileSizeY(i);
        assertEquals(i, writer.getTileSizeY());
      }
      writer.setTileSizeY(SIZE_Y);
      assertEquals(SIZE_Y, writer.getTileSizeY());
      for (int i = 8; i < 24; i++) {
        writer.setTileSizeY(i);
        assertEquals(16, writer.getTileSizeY());
      }
      for (int i = 24; i < 40; i++) {
        writer.setTileSizeY(i);
        assertEquals(32, writer.getTileSizeY());
      }
    }
    catch(FormatException fe) {
      assert(false);
    }
  }
  
  @Test
  public void testTileFormatExceptions() {
    boolean thrown = false;
    int tile_size = 16;
    try {
      writer.setTileSizeY(tile_size);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Pixels Size Y must not be null when attempting to set tile size")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.setTileSizeX(tile_size);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Pixels Size X must not be null when attempting to set tile size")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.getTileSizeX();
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Pixels Size X must not be null when attempting to get tile size")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.getTileSizeY();
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Pixels Size Y must not be null when attempting to get tile size")) {
        thrown = true;
      }
    }
    assert(thrown);
    writer.setMetadataRetrieve(metadata);
    thrown = false;
    try {
      writer.setTileSizeX(0);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Tile size must be > 0")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.setTileSizeY(0);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Tile size must be > 0")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.setTileSizeX(SIZE_X);
    }
    catch(FormatException e) {
        thrown = true;
    }
    assert(!thrown);
    thrown = false;
    try {
      writer.setTileSizeY(SIZE_Y);
    }
    catch(FormatException e) {
        thrown = true;
    }
    assert(!thrown);
    thrown = false;
    try {
      writer.setTileSizeX(SIZE_X + 16);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Tile width must be <= image width")) {
        thrown = true;
      }
    }
    assert(thrown);
    thrown = false;
    try {
      writer.setTileSizeY(SIZE_Y + 16);
    }
    catch(FormatException e) {
      if (e.getMessage().contains("Tile height must be <= image height")) {
        thrown = true;
      }
    }
    assert(thrown);
  }

}
