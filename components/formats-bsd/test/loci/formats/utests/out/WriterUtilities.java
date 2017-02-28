package loci.formats.utests.out;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Random;

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
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

public final class WriterUtilities {
  
  public static final int PLANE_WIDTH = 160;
  public static final int PLANE_HEIGHT = 160;
  public static final int SIZE_X = 1024;
  public static final int SIZE_Y = 1024;
  public static final int SIZE_Z = 4;
  public static final int SIZE_C = 1;
  public static final int SIZE_T = 20;
  
  public static final String COMPRESSION_UNCOMPRESSED = CompressionType.UNCOMPRESSED.getCompression();
  public static final String COMPRESSION_LZW = CompressionType.LZW.getCompression();
  public static final String COMPRESSION_J2K = CompressionType.J2K.getCompression();
  public static final String COMPRESSION_J2K_LOSSY = CompressionType.J2K_LOSSY.getCompression();
  public static final String COMPRESSION_JPEG = CompressionType.JPEG.getCompression();
  
  public static final int[] pixelTypesJPEG = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16, FormatTools.UINT16};
  public static final int[] pixelTypesJ2K = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,FormatTools.UINT16, 
      FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT};
  public static final int[] pixelTypesOther = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT, FormatTools.DOUBLE};
  
  public static IMetadata createMetadata(String pixelType, int rgbChannels,
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
  
  public static Plane writeImage(File file, int tileSize, boolean littleEndian, boolean interleaved, int rgbChannels, 
      int seriesCount, int sizeT, String compression, int pixelType, boolean bigTiff) throws Exception {
    TiffWriter writer = new TiffWriter();
    String pixelTypeString = FormatTools.getPixelTypeString(pixelType);
    writer.setMetadataRetrieve(createMetadata(pixelTypeString, rgbChannels, seriesCount, littleEndian, sizeT));
    writer.setCompression(compression);
    writer.setInterleaved(interleaved);
    writer.setBigTiff(bigTiff);
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

  public static void checkImage(TiffReader reader, Plane originalPlane, boolean interleaved, int rgbChannels, 
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

  public static byte[] getPlane(int width, int height, int bytes) {
    byte[] plane = new byte[width * height * bytes];
    for (int i=0; i<plane.length; i++) {
      plane[i] = (byte) i;
    }
    return plane;
  }
  
  public static Object[][] getData(int[] tileSizes, int[] channelCounts, int[] seriesCounts, int[] timeCounts, String[] compressions, int percentage) {
    boolean[] booleanValues = {true, false};
    int compressionPixelTypeSizes = (2 * pixelTypesOther.length) + pixelTypesOther.length - 1 + pixelTypesJ2K.length + 2;
    int paramSize = tileSizes.length * compressionPixelTypeSizes * 8 * channelCounts.length * seriesCounts.length * timeCounts.length;
    Object[][] data = new Object[paramSize][];
    int index = 0;
    for (int tileSize : tileSizes) {
      for (boolean endianness : booleanValues) {
        for (boolean interleaved : booleanValues) {
          for (int channelCount : channelCounts) {
            for (int seriesCount : seriesCounts) {
              for (int timeCount : timeCounts) {
                for (Boolean bigTiff : booleanValues) {
                  for (String compression : compressions) {
                    int[] pixelTypes = pixelTypesOther;
                    if (compression.equals(WriterUtilities.COMPRESSION_J2K)) {
                      pixelTypes = pixelTypesJ2K;
                    }
                    if (compression.equals(WriterUtilities.COMPRESSION_J2K_LOSSY)) {
                      // Should also allow for double but JPEG 2K compression codec throws null pointer for 64 bitsPerSample
                      pixelTypes = new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
                          FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32, FormatTools.FLOAT};
                    }
                    else if (compression.equals(WriterUtilities.COMPRESSION_JPEG)) {
                      // Should be using pixelTypesJPEG however JPEGCodec throws exception: > 8 bit data cannot be compressed with JPEG
                      pixelTypes = new int[] {FormatTools.INT8, FormatTools.UINT8};
                    }
                    for (int pixelType : pixelTypes) {
                      if (FormatTools.getBytesPerPixel(pixelType) > 2 &&
                          (compression.equals(WriterUtilities.COMPRESSION_J2K) || compression.equals(WriterUtilities.COMPRESSION_J2K_LOSSY))) {
                        data[index] = new Object[] {tileSize, endianness, false, channelCount, seriesCount, timeCount, compression, pixelType, bigTiff};
                      } else {
                        data[index] = new Object[] {tileSize, endianness, interleaved, channelCount, seriesCount, timeCount, compression, pixelType, bigTiff};
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
    }

    // Return a subset of tests if a percentage is selected
    if (percentage > 0 && percentage < 100) {
      int numTests = (paramSize / 100) * percentage;
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
  
  public static Object[][] getCodecs() {
    return new Object[][] {{null, pixelTypesOther}, {COMPRESSION_UNCOMPRESSED, pixelTypesOther}, {COMPRESSION_LZW, pixelTypesOther}, 
      {COMPRESSION_J2K, pixelTypesJ2K}, {COMPRESSION_J2K_LOSSY, pixelTypesOther}, {COMPRESSION_JPEG, pixelTypesJPEG}};
  }
  
  public static int getPropValue(String propertyName) {
    String prop = System.getProperty(propertyName);
    if (prop == null ||
        prop.equals("${"+ propertyName + "}")) return 0;
    if (DataTools.parseInteger(prop) == null) return 0;
    int propertyValue = DataTools.parseInteger(prop);
    if (propertyValue < 0) propertyValue = 0;
    if (propertyValue > 100) propertyValue = 100;
    return propertyValue;
  }
  
  public static IMetadata createMetadata() throws DependencyException, ServiceException {
    ServiceFactory sf = new ServiceFactory();
    OMEXMLService service = sf.getInstance(OMEXMLService.class);
    IMetadata metadata = service.createOMEXMLMetadata();
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
    return metadata;
  }
}
