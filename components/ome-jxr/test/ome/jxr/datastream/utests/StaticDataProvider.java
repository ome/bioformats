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

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.datastream.JXRReader;

import org.testng.annotations.DataProvider;

public class StaticDataProvider {

  private static final String TEST_FILE = "test.jxr";

  @DataProvider(name = "malformedHeaders")
  public static Object[][] malformedHeadersProvider() {
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
  public static Object[][] testFileProvider() throws IOException, JXRException {
    String testFilePath = StaticDataProvider.class.getResource(TEST_FILE).getPath();
    return new Object[][] {
        {new JXRReader(testFilePath)}
    };
  }

}
