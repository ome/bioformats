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

package ome.jxr.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRConstants;
import ome.jxr.JXRException;
import ome.jxr.JXRReader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class JXRReaderTest {

  private static final String TEST_FILE = "test.jxr";

  private String testFilePath;

  @BeforeClass
  public void setUp() throws IOException, JXRException {
    testFilePath = this.getClass().getResource(TEST_FILE).getPath();
  }

  @DataProvider(name = "malformedHeaders")
  public Object[][] malformedHeadersProvider() {
    return new Object[][] {
        /* Too short header */
        {new byte[] {0x01, 0x02, 0x03}},

        /* Big-endian BOM or wrong BOM in header*/
        {new byte[] {0x4d, 0x4d, 0xf, 0xf}},
        {new byte[] {0x49, 0x4d, 0xf, 0xf}},
        {new byte[] {0x4d, 0x49, 0xf, 0xf}},

        /* Wrong magic number*/
        {new byte[] {0x49, 0x49, 0xf, 0xf}},

        /* Wrong format version */
        {new byte[] {0x49, 0x49, 0x0, 0xf}},
        {new byte[] {0x49, 0x49, 0x2, 0xf}},

        /* Wrong IFD offset */
        {new byte[] {0x49, 0x49, (byte) 0xbc, 0x01, 0x00, 0x00, 0x00, 0x00}}
    };
  }

  @DataProvider(name = "testFile")
  public Object[][] testFileProvider() throws IOException, JXRException {
    return new Object[][] {
        {new JXRReader(testFilePath)}
    };
  }

  @Test(expectedExceptions = IOException.class)
  public void testCtorWithEmptyStringShouldThrowIOE()
      throws IOException, JXRException {
    new JXRReader("");
  }

  @Test(dataProvider = "malformedHeaders", expectedExceptions = JXRException.class)
  public void testCtorWithMalformedHeadersShouldThrowJXRE(byte[] malformedHeader)
      throws IOException, JXRException {
    new JXRReader(new RandomAccessInputStream(malformedHeader));
  }

  @Test(dataProvider = "testFile")
  public void testCtorWithTestFileShouldSucceed(JXRReader reader)
      throws IOException, JXRException {
    assertNotNull(reader);
  }

  @Test(dataProvider = "testFile")
  public void testGetEncoderVersionShouldReturnSupportedVersion(JXRReader reader) {
    assertEquals(JXRConstants.ENCODER_VERSION, reader.getEncoderVersion());
  }

  @Test(dataProvider = "testFile")
  public void testIsLittleEndianShouldReturnTrue(JXRReader reader) {
    assertTrue(reader.isLittleEndian());
  }

  @Test(dataProvider = "testFile")
  public void testGetIFDOffsetShouldReturnNotZero(JXRReader reader) {
    int expectedOffset = 32;
    assertEquals(expectedOffset, reader.getIFDOffset());
  }

}
