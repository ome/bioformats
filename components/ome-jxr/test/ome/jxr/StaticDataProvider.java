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

package ome.jxr;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
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
        {new byte[] {0x4D, 0x4D, 0xF, 0xF}},
        {new byte[] {0x49, 0x4D, 0xF, 0xF}},
        {new byte[] {0x4D, 0x49, 0xF, 0xF}},

        /* Wrong magic number*/
        {new byte[] {0x49, 0x49, 0xF, 0xF}},

        /* Wrong format version */
        {new byte[] {0x49, 0x49, 0x0, 0xF}},
        {new byte[] {0x49, 0x49, 0x2, 0xF}},

        /* Wrong IFD offset */
        {new byte[] {0x49, 0x49, (byte) 0xBC, 0x01, 0x00, 0x00, 0x00, 0x00}}
    };
  }

  @DataProvider(name = "testStream")
  public static Object[][] testStreamProvider() throws IOException, JXRException {
    String testFilePath = StaticDataProvider.class.getResource(TEST_FILE).getPath();
    return new Object[][] {
        {new RandomAccessInputStream(testFilePath)}
    };
  }

  @DataProvider(name = "testReader")
  public static Object[][] testReaderProvider() throws IOException, JXRException {
    String testFilePath = StaticDataProvider.class.getResource(TEST_FILE).getPath();
    return new Object[][] {
        {new JXRReader(testFilePath)}
    };
  }

}
