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

package ome.jxr.parser.utests;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.metadata.IFDMetadata;
import ome.jxr.parser.DatastreamParser;
import ome.jxr.parser.FileParser;
import ome.jxr.parser.IFDParser;
import ome.scifio.io.RandomAccessInputStream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DatastreamParserTest extends StaticDataProvider {

  private int actualParsingOffset = 158;

  private IFDMetadata metadataStub;

  @BeforeClass
  public void setUp() {
    // set up a stub here for testing alpha plane presence conditions
    this.metadataStub = new IFDMetadata(0);
  }

  @Test(dataProvider = "testStream")
  public void testParse(RandomAccessInputStream stream)
      throws IOException, JXRException {
    FileParser fileParser = new FileParser(stream);
    fileParser.parse();
    IFDParser ifdParser = new IFDParser(stream, fileParser.getRootIFDOffset());
    ifdParser.parse();
    DatastreamParser parser = new DatastreamParser(stream,
        ifdParser.getIFDMetadata(), fileParser.getEncoderVersion());
    parser.parse();
    parser.close();
  }
}
