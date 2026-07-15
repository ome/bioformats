/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.utests.in;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.in.VmicReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link loci.formats.in.VmicReader}.
 *
 * Every fixture is generated synthetically (a nested-zip Deep Zoom pyramid), so the
 * test carries no proprietary sample data.
 */
public class VmicReaderTest {

  private static final int TILE_SIZE = 256;

  private VmicReader reader;
  private OMEXMLMetadata metadata;

  @BeforeMethod
  public void setUp() throws Exception {
    reader = new VmicReader();
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    metadata = service.createOMEXMLMetadata();
    reader.setMetadataStore(metadata);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    reader.close();
  }

  @Test
  public void testCoreMetadataAndPyramid() throws Exception {
    File vmic = createSyntheticVmic(1024, 1024, true);
    reader.setId(vmic.getAbsolutePath());

    assertEquals(reader.getSizeX(), 1024);
    assertEquals(reader.getSizeY(), 1024);
    assertEquals(reader.getSizeZ(), 1);
    assertEquals(reader.getSizeT(), 1);
    assertEquals(reader.getPixelType(), FormatTools.UINT8);
    assertTrue(reader.isRGB());
    assertEquals(reader.getSizeC(), 3);

    // Deep Zoom pyramid should expose more than one resolution, each ~half the previous.
    assertTrue(reader.getResolutionCount() > 1,
      "expected a multi-resolution pyramid, got " + reader.getResolutionCount());
    reader.setResolution(1);
    assertTrue(reader.getSizeX() < 1024 && reader.getSizeX() >= 512,
      "second resolution should be roughly half width, got " + reader.getSizeX());
    reader.setResolution(0);
  }

  @Test
  public void testTileReadSpanningTiles() throws Exception {
    File vmic = createSyntheticVmic(1024, 1024, false);
    reader.setId(vmic.getAbsolutePath());

    // Read a 300x300 region straddling the 256px tile boundary.
    int w = 300;
    int h = 300;
    byte[] buf = reader.openBytes(0, new byte[w * h * 3], 0, 0, w, h);
    assertNotNull(buf);
    assertEquals(buf.length, w * h * 3);
    // Solid grey tiles: sampled channel byte should be near the fill value (JPEG-lossy).
    int v = buf[0] & 0xff;
    assertTrue(Math.abs(v - 128) <= 12, "unexpected pixel value " + v);
  }

  @Test
  public void testPhysicalPixelSizeFromConfig() throws Exception {
    File vmic = createSyntheticVmic(512, 512, true);
    reader.setId(vmic.getAbsolutePath());

    assertNotNull(metadata.getPixelsPhysicalSizeX(0));
    // PixelPerMicron=4.0 -> 0.25 micron/pixel.
    assertEquals(metadata.getPixelsPhysicalSizeX(0).value().doubleValue(), 0.25, 1e-6);
    assertEquals(metadata.getObjectiveNominalMagnification(0, 0).doubleValue(), 40.0, 1e-6);
  }

  @Test
  public void testMissingConfigStillOpens() throws Exception {
    File vmic = createSyntheticVmic(512, 512, false);
    reader.setId(vmic.getAbsolutePath());
    assertEquals(reader.getSizeX(), 512);
    // No config.osc -> physical size simply absent, not an error.
    assertTrue(metadata.getPixelsPhysicalSizeX(0) == null);
  }

  @Test
  public void testTemporaryFileCleanedUpOnClose() throws Exception {
    File vmic = createSyntheticVmic(512, 512, false);
    long before = countTempVmici();
    reader.setId(vmic.getAbsolutePath());
    reader.close();
    long after = countTempVmici();
    assertTrue(after <= before, "temporary .vmici file was not cleaned up on close");
  }

  @Test(expectedExceptions = {FormatException.class})
  public void testRejectsZipWithoutInnerContainer() throws Exception {
    File notVmic = File.createTempFile("bogus", ".vmic");
    notVmic.deleteOnExit();
    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(notVmic))) {
      zip.putNextEntry(new ZipEntry("something-else.txt"));
      zip.write("not a vmic".getBytes(StandardCharsets.UTF_8));
      zip.closeEntry();
    }
    reader.setId(notVmic.getAbsolutePath());
  }

  // -- Helpers --

  private long countTempVmici() {
    File tmp = new File(System.getProperty("java.io.tmpdir"));
    File[] matches = tmp.listFiles((d, name) ->
      name.startsWith("Image") && name.endsWith(".vmici"));
    return matches == null ? 0 : matches.length;
  }

  /**
   * Build a synthetic .vmic: an outer zip whose sole entry {@code Image.vmici} is
   * itself a zip holding a Deep Zoom descriptor, a full tile pyramid and (optionally)
   * a PreciPoint config.osc.
   */
  private File createSyntheticVmic(int width, int height, boolean includeConfig)
    throws Exception
  {
    ByteArrayOutputStream innerBytes = new ByteArrayOutputStream();
    try (ZipOutputStream inner = new ZipOutputStream(innerBytes)) {
      String dzi = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<Image TileSize=\"" + TILE_SIZE + "\" Overlap=\"0\" Format=\"jpg\">\n" +
        "  <Size Width=\"" + width + "\" Height=\"" + height + "\"/>\n" +
        "</Image>";
      writeEntry(inner, "dzc_output.xml", dzi.getBytes(StandardCharsets.UTF_8));

      int maxLevel = (int) Math.ceil(Math.log(Math.max(width, height)) / Math.log(2));
      for (int level = 0; level <= maxLevel; level++) {
        double zoom = Math.pow(2, level - maxLevel);
        int levelW = (int) Math.max(1, Math.round(width * zoom));
        int levelH = (int) Math.max(1, Math.round(height * zoom));
        int cols = (int) Math.ceil(levelW / (double) TILE_SIZE);
        int rows = (int) Math.ceil(levelH / (double) TILE_SIZE);
        for (int c = 0; c < cols; c++) {
          for (int r = 0; r < rows; r++) {
            int tw = Math.min(TILE_SIZE, levelW - c * TILE_SIZE);
            int th = Math.min(TILE_SIZE, levelH - r * TILE_SIZE);
            writeEntry(inner, "dzc_output_files/" + level + "/" + c + "_" + r + ".jpg",
              solidJpeg(tw, th));
          }
        }
      }

      if (includeConfig) {
        String osc = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
          "<ObjectScanConfig xmlns:ObjectScanConfig=\"urn:vmic\">\n" +
          "  <Objective>\n" +
          "    <ObjectScanConfig:ShortName>testobjective</ObjectScanConfig:ShortName>\n" +
          "    <ObjectScanConfig:Magnification>40</ObjectScanConfig:Magnification>\n" +
          "    <ObjectScanConfig:PixelPerMicron>4.0</ObjectScanConfig:PixelPerMicron>\n" +
          "  </Objective>\n" +
          "</ObjectScanConfig>";
        writeEntry(inner, "VMCF/config.osc", osc.getBytes(StandardCharsets.UTF_8));
      }
    }

    File vmic = File.createTempFile("synthetic", ".vmic");
    vmic.deleteOnExit();
    try (ZipOutputStream outer = new ZipOutputStream(new FileOutputStream(vmic))) {
      writeEntry(outer, "Image.vmici", innerBytes.toByteArray());
    }
    return vmic;
  }

  private static void writeEntry(ZipOutputStream zip, String name, byte[] data)
    throws Exception
  {
    zip.putNextEntry(new ZipEntry(name));
    zip.write(data);
    zip.closeEntry();
  }

  private static byte[] solidJpeg(int w, int h) throws Exception {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setColor(new Color(128, 128, 128));
    g.fillRect(0, 0, w, h);
    g.dispose();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(img, "jpg", out);
    return out.toByteArray();
  }
}
