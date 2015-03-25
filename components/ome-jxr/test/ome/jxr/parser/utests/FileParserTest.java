/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2015 Open Microscopy Environment:
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

package ome.jxr.parser.utests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.constants.File;
import ome.jxr.parser.FileParser;

import org.testng.annotations.Test;

public class FileParserTest extends StaticDataProvider {

  @Test(dataProvider = "testFileParser")
  public void testGetEncoderVersion(FileParser fileParser)
      throws IOException, JXRException {
    fileParser.parse();
    fileParser.close();
    assertEquals(File.CODESTREAM_VERSION, fileParser.getEncoderVersion());
  }

  @Test(dataProvider = "testFileParser")
  public void testIsLittleEndian(FileParser fileParser)
      throws IOException, JXRException {
    fileParser.parse();
    fileParser.close();
    assertTrue(fileParser.isLittleEndian());
  }

  @Test(dataProvider = "testFileParser")
  public void testGetRootIFDOffset(FileParser fileParser)
      throws IOException, JXRException {
    int expectedIFDOffset = 32;
    fileParser.parse();
    fileParser.close();
    assertEquals(expectedIFDOffset, fileParser.getRootIFDOffset());
  }

  @Test(dataProvider = "testFileParser")
  public void testGetFileSize(FileParser fileParser)
      throws IOException, JXRException {
    long expectedFileSize = 3645;
    fileParser.parse();
    fileParser.close();
    assertEquals(expectedFileSize, fileParser.getFileSize());
  }

}
