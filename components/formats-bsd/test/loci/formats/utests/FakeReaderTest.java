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

package loci.formats.utests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import loci.common.Constants;
import loci.common.Location;
import loci.formats.FormatTools;
import loci.formats.in.FakeReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.tools.FakeImage;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;
import loci.common.services.ServiceFactory;

import ome.units.UNITS;
import ome.units.quantity.Length;

import ome.xml.model.primitives.Timestamp;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class FakeReaderTest {

  private Path wd;
  private FakeReader reader;
  private OMEXMLService service;
  private MetadataRetrieve m;

  @DataProvider(name = "physical sizes")
  public Object[][] physicalSizes() {
    return new Object[][] {
      {"1", new Length(1.0, UNITS.MICROMETER)},
      {"1.0", new Length(1.0, UNITS.MICROMETER)},
      {"1mm", new Length(1.0, UNITS.MILLIMETER)},
      {"1.0mm", new Length(1.0, UNITS.MILLIMETER)},
      {"1.0 mm", new Length(1.0, UNITS.MILLIMETER)},
      {"1.0Å", new Length(1.0, UNITS.ANGSTROM)},
      {"1.0 pixel", new Length(1.0, UNITS.PIXEL)},
      {"1.0 reference frame", new Length(1.0, UNITS.REFERENCEFRAME)},
    };
  }

  @DataProvider(name = "annotations")
  public Object[][] annotations() {
    return new Object[][] {
      {"annBool", "getBooleanAnnotationCount"},
      {"annComment", "getCommentAnnotationCount"},
      {"annDouble", "getDoubleAnnotationCount"},
      {"annLong", "getLongAnnotationCount"},
      {"annMap", "getMapAnnotationCount"},
      {"annTag", "getTagAnnotationCount"},
      {"annTerm", "getTermAnnotationCount"},
      {"annTime", "getTimestampAnnotationCount"},
      {"annXml", "getXMLAnnotationCount"},
    };
  }

  @DataProvider(name = "shapes")
  public Object[][] shapes() {
    return new Object[][] {
      {"ellipses", "Ellipse"},
      {"labels", "Label"},
      {"lines", "Line"},
      {"masks", "Mask"},
      {"points", "Point"},
      {"polygons", "Polygon"},
      {"polylines", "Polyline"},
      {"rectangles", "Rectangle"},
    };
  }

  @DataProvider(name = "acquisition dates")
  public Object[][] acquisitionDates() {
    return new Object[][] {
      {"2016-03-01_16-14-00", new Timestamp("2016-03-01T16:14:00")},
      {"2016-03-01", null},
    };
  }

  @DataProvider(name = "pixelFeatures")
  public Object[][] pixelFeatures() {
    Object[] pixelTypes = {
      FormatTools.UINT8,
      FormatTools.INT8,
      FormatTools.UINT16,
      FormatTools.INT16,
      FormatTools.UINT32,
      FormatTools.INT32,
      FormatTools.FLOAT,
      FormatTools.DOUBLE
    };
    Object[] little = {false, true};
    Object[][] ret = new Object[pixelTypes.length * little.length][2];
    for (int i = 0; i < pixelTypes.length; i++) {
      for (int j = 0; j < little.length; j++) {
        int idx = little.length * i + j;
        ret[idx][0] = pixelTypes[i];
        ret[idx][1] = little[j];
      }
    }
    return ret;
  }

  /** Create a directory under wd */
  private static Location mkSubd(Path parent, String name) throws Exception {
    return new Location(Files.createDirectory(parent.resolve(name)).toFile());
  }

  /** Create a text file under wd with the given basename and content */
  private File mkIni(String basename, String... lines) throws Exception {
    File fakeIni = wd.resolve(basename).toFile();
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(
      new FileOutputStream(fakeIni), Constants.ENCODING));
    try {
      for (String l: lines) {
        pw.println(l);
      }
    } finally {
      pw.close();
    }
    return fakeIni;
  }

  /** Recursively set delete-on-exit for a directory tree. */
  private void deleteTemporaryDirectoryOnExit(Location directoryRoot) {
    directoryRoot.deleteOnExit();
    Location[] children = directoryRoot.listFiles();
    if (children != null) {
      for (Location child : children) {
        if (child.isDirectory()) {
          deleteTemporaryDirectoryOnExit(child);
        } else {
          child.deleteOnExit();
        }
      }
    }
  }

  @BeforeMethod
  public void setUp() throws Exception {
    wd = Files.createTempDirectory(this.getClass().getName());
    reader = new FakeReader();
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);
    reader.setMetadataStore(service.createOMEXMLMetadata());
  }

  @AfterMethod
  public void tearDown() throws Exception {
    reader.close();
    deleteTemporaryDirectoryOnExit(new Location(wd.toFile()));
  }

  @Test
  public void testReopenFile() throws Exception {
    reader.setId("foo.fake");
    reader.reopenFile();
  }

  @Test
  public void testCompanionFile() throws Exception {
    Files.createFile(wd.resolve("foo.fake.ini"));
    reader.setId(Files.createFile(wd.resolve("foo.fake")).toString());
    assertEquals(2, reader.getUsedFiles().length);
    assertEquals(2, reader.getSeriesUsedFiles().length);
    assertEquals(2, reader.getUsedFiles(false).length);
    assertEquals(2, reader.getSeriesUsedFiles(false).length);
    assertEquals(1, reader.getUsedFiles(true).length);
    assertEquals(1, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testNoCompanionFile() throws Exception {
    reader.setId(Files.createFile(wd.resolve("foo.fake")).toString());
    assertEquals(1, reader.getUsedFiles().length);
    assertEquals(1, reader.getSeriesUsedFiles().length);
    assertEquals(1, reader.getUsedFiles(false).length);
    assertEquals(1, reader.getSeriesUsedFiles(false).length);
    assertEquals(0, reader.getUsedFiles(true).length);
    assertEquals(0, reader.getSeriesUsedFiles(true).length);
  }

  @Test
  public void testDefaultValues() throws Exception {
    reader.setId("default.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(reader.getSizeX(), FakeReader.DEFAULT_SIZE_X);
    assertEquals(reader.getSizeY(), FakeReader.DEFAULT_SIZE_Y);
    assertEquals(reader.getSizeZ(), FakeReader.DEFAULT_SIZE_Z);
    assertEquals(reader.getSizeC(), FakeReader.DEFAULT_SIZE_C);
    assertEquals(reader.getSizeT(), FakeReader.DEFAULT_SIZE_T);
    assertEquals(reader.getPixelType(), FakeReader.DEFAULT_PIXEL_TYPE);
    assertEquals(reader.getRGBChannelCount(),
                 FakeReader.DEFAULT_RGB_CHANNEL_COUNT);
    assertEquals(reader.getDimensionOrder(),
                 FakeReader.DEFAULT_DIMENSION_ORDER);
    assertEquals(m.getImageAcquisitionDate(0), null);
    assertEquals(m.getPixelsPhysicalSizeX(0), null);
    assertEquals(m.getPixelsPhysicalSizeY(0), null);
    assertEquals(m.getPixelsPhysicalSizeZ(0), null);
    assertEquals(m.getROICount(), 0);
    assertEquals(m.getExperimentCount(), 0);
  }

  @Test
  public void testValuesFromFilename() throws Exception {
    int sizeX = FakeReader.DEFAULT_SIZE_X + 1;
    reader.setId(String.format("foo&sizeX=%d.fake", sizeX));
    assertEquals(reader.getSizeX(), sizeX);
  }

  @Test
  public void testValuesFromIni() throws Exception {
    int sizeX = FakeReader.DEFAULT_SIZE_X + 1;
    mkIni("foo.fake.ini", String.format("sizeX = %d", sizeX));
    reader.setId(wd.resolve("foo.fake").toString());
    assertEquals(reader.getSizeX(), sizeX);
  }

  @Test
  public void testOneWell() throws Exception {
    Location oneWell = new FakeImage(
        mkSubd(wd, "1W.fake")
    ).generateScreen(1, 1, 1, 1, 1);
    assertTrue(reader.isSingleFile(oneWell.getAbsolutePath()));
    assertTrue(reader.isThisType(oneWell.getAbsolutePath()));
    reader.setId(oneWell.getAbsolutePath());
    assertEquals(reader.getOmeXmlMetadata().getWellCount(0), 1);
    assertEquals(reader.getUsedFiles().length, 1);
    assertEquals(reader.getSeriesUsedFiles(false).length, 1);
    assertEquals(reader.getSeriesUsedFiles(true).length, 0);
  }

  @Test
  public void testTwoWells() throws Exception {
    Location twoWells = new FakeImage(
        mkSubd(wd, "2W.fake")
    ).generateScreen(1, 1, 1, 2, 1);
    assertFalse(reader.isSingleFile(twoWells.getAbsolutePath()));
    assertTrue(reader.isThisType(twoWells.getAbsolutePath()));
    reader.setId(twoWells.getAbsolutePath());
    assertEquals(reader.getOmeXmlMetadata().getWellCount(0), 2);
    assertEquals(reader.getUsedFiles().length, 2);
    assertEquals(reader.getSeriesUsedFiles(false).length, 2);
    assertEquals(reader.getSeriesUsedFiles(true).length, 0);
  }

  @Test
  public void testTwoFields() throws Exception {
    Location twoFields = new FakeImage(
        mkSubd(wd, "2F.fake")
    ).generateScreen(1, 1, 1, 1, 2);
    reader.setId(twoFields.getAbsolutePath());
    assertEquals(reader.getSeriesCount(), 2);
  }

  @Test
  public void testTwoPlates() throws Exception {
    Location twoPlates = new FakeImage(
        mkSubd(wd, "2P.fake")
    ).generateScreen(2, 2, 2, 2, 4);
    reader.setId(twoPlates.getAbsolutePath());
    OMEXMLMetadata metadata = reader.getOmeXmlMetadata();
    int i = reader.getImageCount();
    while (i >= 0) {
        assertEquals(metadata.getChannelCount(i--), reader.getSizeC());
    }
  }

  @Test
  public void testMicrobeamINI() throws Exception {
    mkIni("foo.fake.ini", "plates=1\nwithMicrobeam = true");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getExperimentCount(), 1);
    assertEquals(m.getMicrobeamManipulationCount(0), 1);
    reader.close();

    mkIni("foo.fake.ini", "screens=2\nwithMicrobeam=true");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getExperimentCount(), 2);
    assertEquals(m.getMicrobeamManipulationCount(0), 1);
    assertEquals(m.getMicrobeamManipulationCount(1), 1);
    reader.close();
  }

  @Test
  public void testMicrobeam() throws Exception {
    reader.setId("foo&plates=1&withMicrobeam=true.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getExperimentCount(), 1);
    assertEquals(m.getMicrobeamManipulationCount(0), 1);
    reader.close();

    reader.setId("foo&screens=2&withMicrobeam=true.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getExperimentCount(), 2);
    assertEquals(m.getMicrobeamManipulationCount(0), 1);
    assertEquals(m.getMicrobeamManipulationCount(1), 1);
    reader.close();
    testDefaultValues();
  }

  @Test
  public void testExtraMetadata() throws Exception {
    File fakeIni = mkIni("foo.fake.ini", "[GlobalMetadata]", "foo=bar");
    reader.setId(fakeIni.getAbsolutePath());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(reader.getGlobalMetadata().get("foo"), "bar");
  }

  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeX(String value, Length length) throws Exception {
    reader.setId("foo&physicalSizeX=" + value + ".fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeX(0), length);
    reader.close();
    testDefaultValues();
  }
  
  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeXIni(String value, Length length) throws Exception {
    mkIni("foo.fake.ini", "physicalSizeX = " + value);
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeX(0), length);
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeY(String value, Length length) throws Exception {
    reader.setId("foo&physicalSizeY=" + value + ".fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeY(0), length);
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeYIni(String value, Length length) throws Exception {
    mkIni("foo.fake.ini", "physicalSizeY = " + value);
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeY(0), length);
    reader.close();
    testDefaultValues();
  }
  
  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeZ(String value, Length length) throws Exception {
    reader.setId("foo&physicalSizeZ=" + value + ".fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeZ(0), length);
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "physical sizes")
  public void testPhysicalSizeZIni(String value, Length length) throws Exception {
    mkIni("foo.fake.ini", "physicalSizeZ = " + value);
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getPixelsPhysicalSizeZ(0), length);
    reader.close();
    testDefaultValues();
  }

  @Test(expectedExceptions={ RuntimeException.class })
  public void testPhysicalSizeZBadParsing() throws Exception {
    reader.setId("foo&physicalSizeZ=1 1.fake");
  }

  @Test(expectedExceptions={ RuntimeException.class })
  public void testPhysicalSizeZIniBadParsing() throws Exception {
    mkIni("foo.fake.ini", "physicalSizeZ = 1 1");
    reader.setId(wd.resolve("foo.fake").toString());
  }

  @Test(dataProvider = "acquisition dates")
  public void testAcquisitionDate(String value, Timestamp date) throws Exception {
    reader.setId("foo&acquisitionDate=" + value + ".fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getImageAcquisitionDate(0), date);
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "acquisition dates")
  public void testAcquisitionDateIni(String value, Timestamp date) throws Exception {
    mkIni("foo.fake.ini", "acquisitionDate = " + value);
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getImageAcquisitionDate(0), date);
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "acquisition dates")
  public void testAcquisitionDateMultiSeries(String value, Timestamp date) throws Exception {
    reader.setId("foo&series=10&acquisitionDate=" + value + ".fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    for (int i = 0; i < 10; i++) {
      assertEquals(m.getImageAcquisitionDate(i), date);
    }
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "annotations")
  public void testAnnotations(String key, String methodName) throws Exception {
    reader.setId("foo&series=5&" + key + "=10.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    Method method = Class.forName("loci.formats.meta.MetadataRetrieve").getMethod(methodName);
    assertEquals(method.invoke(m), 50);
    for (int i = 0; i < 5; i++) {
      assertEquals(m.getImageAnnotationRefCount(0), 10);
    }
    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "annotations")
  public void testAnnotationsINI(String key, String methodName) throws Exception {
    mkIni("foo.fake.ini", "series = 5\n" + key + " = 10");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    Method method = Class.forName("loci.formats.meta.MetadataRetrieve").getMethod(methodName);
    assertEquals(method.invoke(m), 50);
    for (int i = 0; i < 5; i++) {
      assertEquals(m.getImageAnnotationRefCount(0), 10);
    }
    reader.close();
    testDefaultValues();
  }

  private void checkROIs(MetadataRetrieve m, int nImages, int nRoisPerImage,
      String shapeType, int nOtherRois) throws Exception {
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    assertEquals(m.getImageCount(), nImages);
    assertEquals(m.getROICount(), nImages * nRoisPerImage + nOtherRois);
    for (int i = 0; i < m.getImageCount(); i++) {
      assertEquals(m.getImageROIRefCount(0), nRoisPerImage);
    }
    for (int i = nOtherRois; i < m.getROICount(); i++) {
      assertEquals(m.getShapeCount(i), 1);
      assertEquals(m.getShapeType(i, 0), shapeType);
    }
  }

  @Test(dataProvider = "shapes")
  public void testShapes(String key, String type) throws Exception {
    reader.setId("foo&series=5&" + key + "=10.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 5, 10, type, 0);

    reader.close();
    reader.setId("foo&plateRows=10&plateCols=10&" + key + "=5&withMicrobeam=true.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 100, 5, type, 1);

    reader.close();
    reader.setId("foo&plateRows=10&plateCols=10&" + key + "=5.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 100, 5, type, 0);

    reader.close();
    reader.setId("foo&screens=2&plateRows=10&plateCols=10&" + key + "=5.fake");
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 200, 5, type, 0);

    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "shapes")
  public void testShapesINI(String key, String type) throws Exception {
    mkIni("foo.fake.ini", "series = 5\n" + key + " = 10");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 5, 10, type, 0);

    reader.close();
    mkIni("foo.fake.ini", "plateRows = 10\nplateCols = 10\n" + key + " = 5\nwithMicrobeam=true");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 100, 5, type, 1);

    reader.close();
    mkIni("foo.fake.ini", "plateRows = 10\nplateCols = 10\n" + key + " = 5");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 100, 5, type, 0);

    reader.close();
    mkIni("foo.fake.ini", "screens = 2\nplateRows = 10\nplateCols = 10\n" + key + " = 5");
    reader.setId(wd.resolve("foo.fake").toString());
    m = service.asRetrieve(reader.getMetadataStore());
    checkROIs(m, 200, 5, type, 0);

    reader.close();
    testDefaultValues();
  }

  @Test(dataProvider = "pixelFeatures")
  public void testSpecialPixels(int pixelType, boolean little)
      throws Exception {
    String pt = FormatTools.getPixelTypeString(pixelType);
    int nSeries = 2;
    int sizeC = 3;
    int sizeZ = 4;
    int sizeT = 5;
    reader.setId(String.format(
        "foo&pixelType=%s&series=%s&sizeZ=%s&sizeC=%s&sizeT=%s&little=%s.fake",
        pt, nSeries, sizeZ, sizeC, sizeT, little));
    reader.setSeries(1);
    int no = sizeC * sizeZ * sizeT - 1;
    byte[] plane = reader.openBytes(no);
    int[] exp = new int[] {1, no, sizeZ - 1, sizeC - 1, sizeT - 1};
    assertEquals(FakeReader.readSpecialPixels(plane, pixelType, little), exp);
    if (pixelType == FormatTools.UINT8) {
      assertEquals(FakeReader.readSpecialPixels(plane), exp);
    }
  }

  @Test
  public void testSpecialPixelsInterleaved() throws Exception {
    int nSeries = 2;
    int rgb = 3;
    int sizeC = 3;
    int effSizeC = sizeC / rgb;
    int sizeZ = 4;
    int sizeT = 5;
    reader.setId(String.format(
        "foo&series=%s&sizeZ=%s&sizeC=%s&sizeT=%s&rgb=%d&interleaved=true.fake",
        nSeries, sizeZ, sizeC, sizeT, rgb));
    reader.setSeries(1);
    int no = effSizeC * sizeZ * sizeT - 1;
    byte[] plane = reader.openBytes(no);
    int[] exp = new int[] {1, no, sizeZ - 1, effSizeC - 1, sizeT - 1};
    assertEquals(FakeReader.readSpecialPixels(
        plane, FormatTools.UINT8, true, rgb, true), exp);
  }

  @Test
  public void testSpecialPixelsIndexed() throws Exception {
    int nSeries = 2;
    int sizeC = 3;
    int sizeZ = 4;
    int sizeT = 5;
    reader.setId(String.format(
        "foo&series=%s&sizeZ=%s&sizeC=%s&sizeT=%s&indexed=true.fake",
        nSeries, sizeZ, sizeC, sizeT));
    reader.setSeries(1);
    int no = sizeC * sizeZ * sizeT - 1;
    byte[] plane = reader.openBytes(no);
    int[] exp = new int[] {1, no, sizeZ - 1, sizeC - 1, sizeT - 1};
    int[] indices = FakeReader.readSpecialPixels(plane);
    assertEquals(indices.length, exp.length);
    int[] specialPixels = new int[indices.length];
    byte[][] lut = reader.get8BitLookupTable();
    for (int i = 0; i < indices.length; i++) {
      specialPixels[i] = lut[0][(int)indices[i]];
    }
    assertEquals(specialPixels, exp);
  }

}
