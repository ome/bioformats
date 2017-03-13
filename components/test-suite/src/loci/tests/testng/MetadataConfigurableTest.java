/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.tests.testng;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

import loci.common.DataTools;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.SkipException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static loci.tests.testng.TestTools.getProperty;

/**
 */
public class MetadataConfigurableTest {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MetadataConfigurableTest.class);
  private static final String FILENAME_PROPERTY = "testng.filename";
  private static final String SKIP_MESSAGE = "No image file specified";

  private ImageReader pixelsOnly;
  private ImageReader all;
  private ImageReader noOverlays;
  private String id;

  @BeforeClass
  public void setUp() {
    pixelsOnly = new ImageReader();
    pixelsOnly.setMetadataOptions(
      new DefaultMetadataOptions(MetadataLevel.MINIMUM));
    all = new ImageReader();
    all.setMetadataOptions(new DefaultMetadataOptions(MetadataLevel.ALL));
    noOverlays = new ImageReader();
    noOverlays.setMetadataOptions(
      new DefaultMetadataOptions(MetadataLevel.NO_OVERLAYS));
    id = getProperty(FILENAME_PROPERTY);
    if (null == id) {
      LOGGER.error(SKIP_MESSAGE);
      throw new SkipException(SKIP_MESSAGE);
    }
  }

  @Test
  public void testSetId() throws FormatException, IOException {
    long t0 = System.currentTimeMillis();
    pixelsOnly.setId(id);
    assertEquals(MetadataLevel.MINIMUM,
      pixelsOnly.getMetadataOptions().getMetadataLevel());

    long t1 = System.currentTimeMillis();
    all.setId(id);
    assertEquals(MetadataLevel.ALL,
      all.getMetadataOptions().getMetadataLevel());
    assertFalse(0 ==
      all.getSeriesMetadata().size() + all.getGlobalMetadata().size());

    long t2 = System.currentTimeMillis();
    System.err.println(String.format("Pixels only: %d -- All: %d",
      t1 - t0, t2 - t1));

    IMetadata metadata = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      metadata = service.createOMEXMLMetadata();
      noOverlays.setMetadataStore(metadata);
    } catch (Exception e) {
      throw new FormatException("Cannot initialize OMEXML metadata store");
    }
  
    noOverlays.setId(id);
    assertEquals(MetadataLevel.NO_OVERLAYS,
      noOverlays.getMetadataOptions().getMetadataLevel());
    assertEquals(metadata.getROICount(), 0);
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testDimensions() {
    assertEquals(all.getSeriesCount(), pixelsOnly.getSeriesCount());
    assertEquals(all.getSeriesCount(), noOverlays.getSeriesCount());
    for (int i=0; i<pixelsOnly.getSeriesCount(); i++) {
      all.setSeries(i);
      pixelsOnly.setSeries(i);
      noOverlays.setSeries(i);

      assertEquals(all.getSizeX(), pixelsOnly.getSizeX());
      assertEquals(all.getSizeY(), pixelsOnly.getSizeY());
      assertEquals(all.getSizeZ(), pixelsOnly.getSizeZ());
      assertEquals(all.getSizeC(), pixelsOnly.getSizeC());
      assertEquals(all.getSizeT(), pixelsOnly.getSizeT());
      assertEquals(all.getPixelType(), pixelsOnly.getPixelType());
      assertEquals(all.isLittleEndian(), pixelsOnly.isLittleEndian());
      assertEquals(all.isIndexed(), pixelsOnly.isIndexed());

      assertEquals(all.getSizeX(), noOverlays.getSizeX());
      assertEquals(all.getSizeY(), noOverlays.getSizeY());
      assertEquals(all.getSizeZ(), noOverlays.getSizeZ());
      assertEquals(all.getSizeC(), noOverlays.getSizeC());
      assertEquals(all.getSizeT(), noOverlays.getSizeT());
      assertEquals(all.getPixelType(), noOverlays.getPixelType());
      assertEquals(all.isLittleEndian(), noOverlays.isLittleEndian());
      assertEquals(all.isIndexed(), noOverlays.isIndexed());
    }
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testPlaneData() throws FormatException, IOException {
    for (int i=0; i<pixelsOnly.getSeriesCount(); i++) {
      pixelsOnly.setSeries(i);
      all.setSeries(i);
      noOverlays.setSeries(i);
      assertEquals(all.getImageCount(), pixelsOnly.getImageCount());
      assertEquals(all.getImageCount(), noOverlays.getImageCount());
      for (int j=0; j<pixelsOnly.getImageCount(); j++) {
        byte[] pixelsOnlyPlane = pixelsOnly.openBytes(j);
        String pixelsOnlySHA1 = sha1(pixelsOnlyPlane);
        byte[] allPlane = all.openBytes(j);
        String allSHA1 = sha1(allPlane);
        byte[] noOverlaysPlane = noOverlays.openBytes(j);
        String noOverlaysSHA1 = sha1(noOverlaysPlane);

        if (!pixelsOnlySHA1.equals(allSHA1)) {
          fail(String.format("MISMATCH: Series:%d Image:%d PixelsOnly%s All:%s",
            i, j, pixelsOnlySHA1, allSHA1));
        }
        if (!noOverlaysSHA1.equals(allSHA1)) {
          fail(String.format("MISMATCH: Series:%d Image:%d PixelsOnly%s All:%s",
            i, j, noOverlaysSHA1, allSHA1));
        }
      }
    }
  }

  @Test(dependsOnMethods={"testSetId"})
  public void testUsedFiles() throws FormatException, IOException {
    for (int i=0; i<pixelsOnly.getSeriesCount(); i++) {
      pixelsOnly.setSeries(i);
      all.setSeries(i);
      noOverlays.setSeries(i);

      String[] pixelsOnlyFiles = pixelsOnly.getSeriesUsedFiles();
      String[] allFiles = all.getSeriesUsedFiles();
      String[] noOverlaysFiles = noOverlays.getSeriesUsedFiles();

      assertEquals(allFiles.length, pixelsOnlyFiles.length);
      assertEquals(allFiles.length, noOverlaysFiles.length);

      Arrays.sort(allFiles);
      Arrays.sort(pixelsOnlyFiles);
      Arrays.sort(noOverlaysFiles);

      for (int j=0; j<pixelsOnlyFiles.length; j++) {
        assertEquals(allFiles[j], pixelsOnlyFiles[j]);
        assertEquals(allFiles[j], noOverlaysFiles[j]);
      }
    }
  }

  // -- Utility methods --

  private String sha1(byte[] buf) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      return DataTools.bytesToHex(md.digest(buf));
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
