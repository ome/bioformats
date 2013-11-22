/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
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

package ome.jxr.metadata.utests;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.metadata.JXRMetadata;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class JXRMetadataTest {
  
  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetBitsPerPixel(JXRMetadata metadata)
      throws JXRException {
    int expected = 8;
    int actual = metadata.getBitsPerPixel();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetNumberOfChannels(JXRMetadata metadata)
      throws JXRException {
    int expected = 4;
    int actual = metadata.getNumberOfChannels();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageWidth(JXRMetadata metadata)
      throws JXRException {
    long expected = 64;
    long actual = metadata.getImageWidth();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageHeight(JXRMetadata metadata)
      throws JXRException {
    long expected = 64;
    long actual = metadata.getImageHeight();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageOffset(JXRMetadata metadata)
      throws JXRException {
    long expected = 158;
    long actual = metadata.getImageImageOffset();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageByteCount(JXRMetadata metadata)
      throws JXRException {
    long expected = 3487;
    long actual = metadata.getImageByteCount();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetDocumentName(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getDocumentName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageDescription(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageDescription());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetEquipmentMake(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getEquipmentMake());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetEquipmentModel(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getEquipmentModel());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPageName(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getPageName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPageNumber(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getPageNumber());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetSoftwareNameVersion(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getSoftwareNameVersion());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetDateTime(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getDateTime());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetArtistName(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getArtistName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetHostComputer(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getHostComputer());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetCopyrightNotice(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getCopyrightNotice());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetColorSpace(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getColorSpace());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPrefferedSpatialTransformation(JXRMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getPrefferedSpatialTransformation();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageType(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageType());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetWidthResolution(JXRMetadata metadata)
      throws IOException, JXRException {
    float expected = 71.9836f;
    float actual = metadata.getWidthResoulution();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetHeightResolution(JXRMetadata metadata)
      throws IOException, JXRException {
    float expected = 71.9836f;
    float actual = metadata.getHeightResoulution();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaOffset(JXRMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getAlphaOffset();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaByteCount(JXRMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getAlphaByteCount();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageBandPresence(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageBandPresence());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaBandPresence(JXRMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getAlphaBandPresence());
  }

}
