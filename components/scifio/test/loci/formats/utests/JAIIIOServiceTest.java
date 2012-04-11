
package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.services.JAIIIOService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/JAIIIOServiceTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/JAIIIOServiceTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class JAIIIOServiceTest {

  private static final int SIZE_X = 64;

  private static final int SIZE_Y = 96;

  //Code block size minimum is 4x4
  private static final int[] CODE_BLOCK = new int[] { 4, 4 };

  private static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

  private JAIIIOService service;

  @BeforeMethod
  public void setUp() throws DependencyException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(JAIIIOService.class);
  }

  private ByteArrayOutputStream writeImage(JPEG2000CodecOptions options)
    throws IOException, ServiceException {
    BufferedImage image = new BufferedImage(SIZE_X, SIZE_Y, IMAGE_TYPE);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    service.writeImage(stream, image, options);
    return stream;
  }

  private ByteArrayOutputStream assertWriteImageLossy()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = false;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    ByteArrayOutputStream stream = writeImage(options);
    assertTrue(stream.size() > 0);
    return stream;
  }

  private ByteArrayOutputStream assertWriteImageLossless()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = true;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    ByteArrayOutputStream stream = writeImage(options);
    assertTrue(stream.size() > 0);
    return stream;
  }

  @Test
  public void testWriteTiledImageLossy()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = false;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    options.tileWidth = 32;
    options.tileHeight = 32;
    options.tileGridXOffset = 0;
    options.tileGridYOffset = 0;
    ByteArrayOutputStream stream = writeImage(options);
    assertTrue(stream.size() > 0);
  }

  @Test
  public void testWriteTiledImageLossless()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = true;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    options.tileWidth = 32;
    options.tileHeight = 32;
    options.tileGridXOffset = 0;
    options.tileGridYOffset = 0;
    ByteArrayOutputStream stream = writeImage(options);
    assertTrue(stream.size() > 0);
  }

  @Test
  public void testReadImageLossy() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossy();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testReadImageLossless() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossless();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testReadRasterLossy() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossy();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    Raster image = service.readRaster(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testReadRasterLossless() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossless();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    Raster image = service.readRaster(inputStream);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testReadImageLevel0Lossy() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossy();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.resolution = 0;
    BufferedImage image = service.readImage(inputStream, options);
    assertNotNull(image);
    assertEquals(2, image.getWidth());
    assertEquals(3, image.getHeight());
  }

  @Test
  public void testReadImageLevel0Lossless() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossless();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.resolution = 0;
    BufferedImage image = service.readImage(inputStream, options);
    assertNotNull(image);
    assertEquals(2, image.getWidth());
    assertEquals(3, image.getHeight());
  }

  @Test
  public void testReadRasterLevel0Lossy() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossy();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.resolution = 0;
    Raster image = service.readRaster(inputStream, options);
    assertNotNull(image);
    assertEquals(2, image.getWidth());
    assertEquals(3, image.getHeight());
  }

  @Test
  public void testReadRasterLevel0Lossless() throws IOException, ServiceException {
    ByteArrayOutputStream outputStream = assertWriteImageLossless();
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.resolution = 0;
    Raster image = service.readRaster(inputStream, options);
    assertNotNull(image);
    assertEquals(2, image.getWidth());
    assertEquals(3, image.getHeight());
  }

  @Test
  public void testNumDecompositionLevelsLossy()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.numDecompositionLevels = 2;
    options.resolution = 2;
    options.lossless = false;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    ByteArrayOutputStream outputStream = writeImage(options);
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream, options);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test
  public void testNumDecompositionLevelsLossless()
    throws IOException, ServiceException {
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.numDecompositionLevels = 2;
    options.resolution = 2;
    options.lossless = true;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;
    ByteArrayOutputStream outputStream = writeImage(options);
    ByteArrayInputStream inputStream =
      new ByteArrayInputStream(outputStream.toByteArray());
    BufferedImage image = service.readImage(inputStream, options);
    assertNotNull(image);
    assertEquals(SIZE_X, image.getWidth());
    assertEquals(SIZE_Y, image.getHeight());
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testWriteFloatingPointLossy() throws Exception {
    // The JAI ImageIO JPEG-2000 codec does not support floating point data.
    JPEG2000CodecOptions options = JPEG2000CodecOptions.getDefaultOptions();
    options.lossless = false;
    options.codeBlockSize = CODE_BLOCK;
    options.quality = 1.0f;

    SampleModel sm = new ComponentSampleModel(
        DataBuffer.TYPE_FLOAT, SIZE_X, SIZE_Y, 1, SIZE_X, new int[] { 0 });
    DataBuffer db = new DataBufferFloat(SIZE_X * SIZE_Y);
    WritableRaster wr = Raster.createWritableRaster(sm, db, null);
    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    ColorModel cm = new ComponentColorModel(
        cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
    BufferedImage image = new BufferedImage(cm, wr, true, null);

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    service.writeImage(stream, image, options);
  }
}
