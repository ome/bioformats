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

import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import org.junit.Assert;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CompressionType;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.utests.tiff.TiffWriterMock;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
  private static final int PLANE_WIDTH = 160;
  private static final int PLANE_HEIGHT = 160;
  private static final int TILE_GRANULARITY = 16;

  /* Percentage of tiling tests to be executed */
  private static int percentageOfTilingTests = 0;
  private static int percentageOfSaveBytesTests = 0;

  @DataProvider(name = "bigTiffSuffixes")
  public Object[][] createSuffixes() {
    return new Object[][] {{"tf2"}, {"tf8"}, {"btf"}, {"tif"}, {"tiff"}};
  }

  @DataProvider(name = "codecs")
  public Object[][] createCodecs() {
    return new Object[][] {{null, pixelTypesOther}, {COMPRESSION_UNCOMPRESSED, pixelTypesOther}, {COMPRESSION_LZW, pixelTypesOther}, 
      {COMPRESSION_J2K, pixelTypesJ2K}, {COMPRESSION_J2K_LOSSY, pixelTypesOther}, {COMPRESSION_JPEG, pixelTypesJPEG}};
  }
  
  @DataProvider(name = "tiling")
  public Object[][] createTiling() {
    if (percentageOfTilingTests == 0) {
      return new Object[][] {{0, false, false, 0, 0, 0, null, 0}};
    }

    int[] tileSizes = {1, 32, 43, 64};
    int[] channelCounts = {1, 3};
    int[] seriesCounts = {1, 5};
    int[] timeCounts = {1};
    String[] compressions = {COMPRESSION_UNCOMPRESSED, COMPRESSION_LZW, COMPRESSION_J2K, COMPRESSION_J2K_LOSSY, COMPRESSION_JPEG};
    return getData(tileSizes, channelCounts, seriesCounts, timeCounts, compressions);
  }
  
  @DataProvider(name = "nonTiling")
  public Object[][] createNonTiling() {
    if (percentageOfTilingTests == 0) {
      return new Object[][] {{0, false, false, 0, 0, 0, null, 0}};
    }
    int[] tileSizes = {PLANE_WIDTH};
    int[] channelCounts = {1, 3};
    int[] seriesCounts = {1};
    int[] timeCounts = {1, 5};
    String[] compressions = {COMPRESSION_UNCOMPRESSED, COMPRESSION_LZW, COMPRESSION_J2K, COMPRESSION_J2K_LOSSY, COMPRESSION_JPEG};
    return getData(tileSizes, channelCounts, seriesCounts, timeCounts, compressions);
  }

  private Object[][] getData(int[] tileSizes, int[] channelCounts, int[] seriesCounts, int[] timeCounts, String[] compressions) {
    boolean[] booleanValues = {true, false};
    int compressionPixelTypeSizes = (2 * pixelTypesOther.length) + pixelTypesOther.length - 1 + pixelTypesJ2K.length + 2;
    int paramSize = tileSizes.length * compressionPixelTypeSizes * 4 * channelCounts.length * seriesCounts.length * timeCounts.length;
    Object[][] data = new Object[paramSize][];
    int index = 0;
    for (int tileSize : tileSizes) {
      for (boolean endianness : booleanValues) {
        for (boolean interleaved : booleanValues) {
          for (int channelCount : channelCounts) {
            for (int seriesCount : seriesCounts) {
              for (int timeCount : timeCounts) {
                for (String compression : compressions) {
                  int[] pixelTypes = pixelTypesOther;
                  if (compression.equals(COMPRESSION_J2K)) {
                    pixelTypes = pixelTypesJ2K;
                  }
                  if (compression.equals(COMPRESSION_J2K_LOSSY)) {
                    // Should also allow for double but JPEG 2K compression codec throws null pointer for 64 bitsPerSample
                    pixelTypes = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
                        FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT};
                  }
                  else if (compression.equals(COMPRESSION_JPEG)) {
                    // Should be using pixelTypesJPEG however JPEGCodec throws exception: > 8 bit data cannot be compressed with JPEG
                    pixelTypes = new int[] {FormatTools.INT8, FormatTools.UINT8};
                  }
                  for (int pixelType : pixelTypes) {
                    if (FormatTools.getBytesPerPixel(pixelType) > 2 &&
                        (compression.equals(COMPRESSION_J2K) || compression.equals(COMPRESSION_J2K_LOSSY))) {
                      data[index] = new Object[] {tileSize, endianness, false, channelCount, seriesCount, timeCount, compression, pixelType};
                    } else {
                      data[index] = new Object[] {tileSize, endianness, interleaved, channelCount, seriesCount, timeCount, compression, pixelType};
                    }
                    index ++;
                  }
                }
              }
            }
          }
        }
      }
    }

    // Return a subset of tests if a percentage is selected
    if (percentageOfTilingTests > 0 && percentageOfTilingTests < 100) {
      int numTests = (paramSize / 100) * percentageOfTilingTests;
      Object[][] returnSubset = new Object[numTests][];
      for (int i = 0; i < numTests; i++) {
        Random rand = new Random();
        int randIndex = rand.nextInt(paramSize);
        returnSubset[i] = data[randIndex];
      }
      return returnSubset;
    }
    return data;
  }

  @BeforeClass
  public void readProperty() throws Exception {
    percentageOfTilingTests = getPropValue("testng.runWriterTilingTests");
    percentageOfSaveBytesTests = getPropValue("testng.runWriterSaveBytesTests");
  }
  
  private int getPropValue(String propertyName) {
    String prop = System.getProperty(propertyName);
    if (prop == null ||
        prop.equals("${"+ propertyName + "}")) return 0;
    if (DataTools.parseInteger(prop) == null) return 0;
    int propertyValue = DataTools.parseInteger(prop);
    if (propertyValue < 0) propertyValue = 0;
    if (propertyValue > 100) propertyValue = 100;
    return propertyValue;
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
      for (int i = 1; i < 24; i++) {
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
      for (int i = 1; i < 24; i++) {
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
      thrown = true;
    }
    assert(!thrown);
    thrown = false;
    try {
      writer.setTileSizeY(SIZE_Y + 16);
    }
    catch(FormatException e) {
      thrown = true;
    }
    assert(!thrown);
  }

  @Test(dataProvider = "tiling")
  public void testSaveBytesTiling(int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression, int pixelType) throws Exception {
    if (percentageOfTilingTests == 0) return;

    File tmp = File.createTempFile("tiffWriterTest_Tiling", ".tiff");
    tmp.deleteOnExit();
    Plane originalPlane = writeImage(tmp, tileSize, littleEndian, interleaved, rgbChannels, seriesCount, sizeT, compression, pixelType);

    TiffReader reader = new TiffReader();
    reader.setId(tmp.getAbsolutePath());

    int expectedTileSize = tileSize;
    if (tileSize < TILE_GRANULARITY) {
      expectedTileSize = TILE_GRANULARITY;
    }
    else {
      expectedTileSize = Math.round((float)tileSize/TILE_GRANULARITY) * TILE_GRANULARITY;
    }

    IFD tileIFd = reader.getIFDs().get(0);
    assertEquals(tileIFd.getIFDIntValue(IFD.TILE_LENGTH), expectedTileSize);
    assertEquals(tileIFd.getIFDIntValue(IFD.TILE_WIDTH), expectedTileSize);

    checkImage(reader, originalPlane, interleaved, rgbChannels, seriesCount, sizeT, compression);

    tmp.delete();
    reader.close();
  }

  @Test(dataProvider = "nonTiling")
  public void testSaveBytes(int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression, int pixelType) throws Exception {
    if (percentageOfSaveBytesTests == 0) return;

    File tmp = File.createTempFile("tiffWriterTest", ".tiff");
    tmp.deleteOnExit();
    Plane originalPlane = writeImage(tmp, tileSize, littleEndian, interleaved, rgbChannels, seriesCount, sizeT, compression, pixelType);

    TiffReader reader = new TiffReader();
    reader.setId(tmp.getAbsolutePath());

    checkImage(reader, originalPlane, interleaved, rgbChannels, seriesCount, sizeT, compression);

    tmp.delete();
    reader.close();
  }

  private Plane writeImage(File file, int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression, int pixelType) throws Exception {
    TiffWriter writer = new TiffWriter();
    String pixelTypeString = FormatTools.getPixelTypeString(pixelType);
    writer.setMetadataRetrieve(createMetadata(pixelTypeString, rgbChannels, seriesCount, littleEndian, sizeT));
    writer.setCompression(compression);
    writer.setInterleaved(interleaved);
    if (tileSize != PLANE_WIDTH) {
      writer.setTileSizeX(tileSize);
      writer.setTileSizeY(tileSize);
    }
    writer.setId(file.getAbsolutePath());

    int bytes = FormatTools.getBytesPerPixel(pixelType);
    byte[] plane = getPlane(PLANE_WIDTH, PLANE_HEIGHT, bytes * rgbChannels);
    Plane originalPlane = new Plane(plane, littleEndian,
      !writer.isInterleaved(), rgbChannels, FormatTools.getPixelTypeString(pixelType));

    for (int s=0; s<seriesCount; s++) {
      writer.setSeries(s);
      for (int t=0; t<sizeT; t++) {
        writer.saveBytes(t, plane);
      }
    }

    writer.close();
    return originalPlane;
  }

  private void checkImage(TiffReader reader, Plane originalPlane, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression) throws FormatException, IOException {
    for (int s=0; s<reader.getSeriesCount(); s++) {
      reader.setSeries(s);
      assertEquals(reader.getSizeC(), rgbChannels);
      int imageCount = reader.isRGB() ? seriesCount * sizeT : rgbChannels * sizeT * seriesCount;
      assertEquals(reader.getImageCount(), imageCount);
      for (int image=0; image<reader.getImageCount(); image++) {
        byte[] readPlane = reader.openBytes(image);
        boolean lossy = compression.equals(COMPRESSION_JPEG) || compression.equals(COMPRESSION_J2K_LOSSY);
        boolean isJ2K = compression.equals(COMPRESSION_J2K) || compression.equals(COMPRESSION_J2K_LOSSY);
        boolean interleavedDiffs = interleaved || (isJ2K && !interleaved);
        if (!(lossy || interleavedDiffs)) {
          Plane newPlane = new Plane(readPlane, reader.isLittleEndian(),
            !reader.isInterleaved(), reader.getRGBChannelCount(),
            FormatTools.getPixelTypeString(reader.getPixelType()));

          assert(originalPlane.equals(newPlane));
        }
      }
    }
  }

  private byte[] getPlane(int width, int height, int bytes) {
    byte[] plane = new byte[width * height * bytes];
    for (int i=0; i<plane.length; i++) {
      plane[i] = (byte) i;
    }
    return plane;
  }

  private IMetadata createMetadata(String pixelType, int rgbChannels,
      int seriesCount, boolean littleEndian, int sizeT) throws Exception {
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
        "XYCZT", pixelType, 160, 160, 1, rgbChannels, sizeT, rgbChannels);
    }

    return metadata;
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
