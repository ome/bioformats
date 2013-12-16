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
import ome.jxr.metadata.IFDMetadata;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class IFDMetadataTest {
  
  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetBitsPerPixel(IFDMetadata metadata)
      throws JXRException {
    int expected = 8;
    int actual = metadata.getBitsPerPixel();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetNumberOfChannels(IFDMetadata metadata)
      throws JXRException {
    int expected = 4;
    int actual = metadata.getNumberOfChannels();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageWidth(IFDMetadata metadata)
      throws JXRException {
    long expected = 64;
    long actual = metadata.getImageWidth();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageHeight(IFDMetadata metadata)
      throws JXRException {
    long expected = 64;
    long actual = metadata.getImageHeight();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageOffset(IFDMetadata metadata)
      throws JXRException {
    long expected = 158;
    long actual = metadata.getImageOffset();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageByteCount(IFDMetadata metadata)
      throws JXRException {
    long expected = 3487;
    long actual = metadata.getImageByteCount();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetDocumentName(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getDocumentName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageDescription(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageDescription());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetEquipmentMake(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getEquipmentMake());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetEquipmentModel(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getEquipmentModel());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPageName(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getPageName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPageNumber(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getPageNumber());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetSoftwareNameVersion(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getSoftwareNameVersion());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetDateTime(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getDateTime());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetArtistName(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getArtistName());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetHostComputer(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getHostComputer());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetCopyrightNotice(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getCopyrightNotice());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetColorSpace(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getColorSpace());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetPrefferedSpatialTransformation(IFDMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getPrefferedSpatialTransformation();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageType(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageType());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetWidthResolution(IFDMetadata metadata)
      throws IOException, JXRException {
    float expected = 71.9836f;
    float actual = metadata.getWidthResoulution();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetHeightResolution(IFDMetadata metadata)
      throws IOException, JXRException {
    float expected = 71.9836f;
    float actual = metadata.getHeightResoulution();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaOffset(IFDMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getAlphaOffset();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaByteCount(IFDMetadata metadata)
      throws IOException, JXRException {
    long expected = 0;
    long actual = metadata.getAlphaByteCount();
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetImageBandPresence(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getImageBandPresence());
  }

  @Test(dataProvider = "testMetadata", dataProviderClass = StaticDataProvider.class)
  public void testGetAlphaBandPresence(IFDMetadata metadata)
      throws IOException, JXRException {
    assertNull(metadata.getAlphaBandPresence());
  }

}
