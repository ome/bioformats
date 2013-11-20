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

package ome.jxr.datastream.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.constants.File;
import ome.jxr.datastream.JXRReader;

import org.testng.annotations.Test;

public class JXRReaderTest {

  @Test(expectedExceptions = IOException.class)
  public void testCtorWithEmptyStringShouldThrowIOE() throws IOException,
      JXRException {
    new JXRReader("");
  }

  @Test(dataProvider = "malformedHeaders",
      dataProviderClass = StaticDataProvider.class,
      expectedExceptions = JXRException.class)
  public void testCtorWithMalformedHeadersShouldThrowJXRE(byte[] malformedHeader)
      throws IOException, JXRException {
    new JXRReader(new RandomAccessInputStream(malformedHeader));
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testCtorWithtestReaderShouldNotReturnNull(JXRReader reader)
      throws IOException, JXRException {
    assertNotNull(reader);
    reader.close();
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testGetEncoderVersionShouldReturnSupportedVersion(JXRReader reader) {
    assertEquals(File.ENCODER_VERSION, reader.getEncoderVersion());
    reader.close();
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testIsLittleEndianShouldReturnTrue(JXRReader reader) {
    assertTrue(reader.isLittleEndian());
    reader.close();
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testGetIFDOffsetShouldNotReturnZero(JXRReader reader) {
    int expectedOffset = 32;
    assertEquals(expectedOffset, reader.getRootIFDOffset());
    reader.close();
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testGetMetadataShouldNotReturnNull(JXRReader reader)
      throws IllegalStateException, IOException, JXRException {
    assertNotNull(reader.getMetadata());
    reader.close();
  }

  @Test(dataProvider = "testReader", dataProviderClass = StaticDataProvider.class)
  public void testGetDecompressedImageShouldNotReturnNull(JXRReader reader)
      throws IOException {
    assertNotNull(reader.getDecompressedImage());
    reader.close();
  }

}
