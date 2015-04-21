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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.parser.IFDParser;

import org.testng.annotations.Test;

public class IFDParserTest extends StaticDataProvider {

  @Test(dataProvider = "testIFDParser")
  public void testGetIFDCount(IFDParser ifdParser)
      throws IOException, JXRException {
    int expectedIFDCount = 1;
    ifdParser.parse();
    int actualIFDCount = ifdParser.getIFDCount();
    ifdParser.close();
    assertEquals(expectedIFDCount, actualIFDCount);
  }

  @Test(dataProvider = "testIFDParser")
  public void testGetIFDMetadata(IFDParser ifdParser)
      throws IOException, JXRException {
    ifdParser.parse();
    assertNotNull(ifdParser.getIFDMetadata());
    ifdParser.close();
  }

  @Test(dataProvider = "testIFDParser")
  public void testClose(IFDParser ifdParser) throws IOException, JXRException {
    ifdParser.parse();
    ifdParser.close();
    assertNull(ifdParser.getIFDMetadata());
  }

}
