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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.JXRReader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class JXRReaderTest {

  private static final String TEST_FILE = "test.jxr";

  private final List<byte[]> malformedHeaders = new ArrayList<byte[]>();

  private String testFilePath;

  @BeforeClass
  public void setUp() throws IOException, JXRException {
    testFilePath = this.getClass().getResource(TEST_FILE).getPath();

    /* Too short header */
    malformedHeaders.add(new byte[] {0x01, 0x02, 0x03});

    /* Big-endian BOM or wrong BOM in header*/
    malformedHeaders.add(new byte[] {0x4d, 0x4d, 0xf, 0xf});
    malformedHeaders.add(new byte[] {0x49, 0x4d, 0xf, 0xf});
    malformedHeaders.add(new byte[] {0x4d, 0x49, 0xf, 0xf});

    /* Wrong magic number*/
    malformedHeaders.add(new byte[] {0x49, 0x49, 0xf, 0xf});

    /* Wrong format version */
    malformedHeaders.add(new byte[] {0x49, 0x49, 0x0, 0xf});
    malformedHeaders.add(new byte[] {0x49, 0x49, 0x2, 0xf});
  }

  @Test(expectedExceptions = IOException.class)
  public void testCtorWithEmptyStringShouldThrowIOE()
      throws IOException, JXRException {
    new JXRReader("");
  }

  @Test(expectedExceptions = JXRException.class)
  public void testCtorWithMalformedHeadersShouldThrowJXRE()
      throws IOException, JXRException {
    for (byte[] header : malformedHeaders) {
      new JXRReader(new RandomAccessInputStream(header));
    }
  }

  @Test
  public void testCtorWithTestFileShouldSucceed()
      throws IOException, JXRException {
    new JXRReader(testFilePath);
  }

}
