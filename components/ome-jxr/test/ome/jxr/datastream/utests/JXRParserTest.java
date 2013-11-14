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

import junit.framework.Assert;
import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.datastream.JXRParser;
import ome.jxr.datastream.JXRReader;

import org.testng.annotations.Test;

public class JXRParserTest {

  @Test
  public void testGetIFDCountShouldReturnZeroForEmptyObject() 
      throws IOException {
    JXRParser parser = new JXRParser();
    int expectedCount = 0;
    int actualCount = parser.getIFDCount();
    Assert.assertEquals(expectedCount, actualCount);
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void testFindAllIFDsShouldThrowIfStreamNotSet()
      throws IllegalStateException, IOException {
    JXRParser parser = new JXRParser();
    parser.findAllIFDs();
  }

  @Test(dataProvider = "testStream", dataProviderClass = StaticDataProvider.class,
      expectedExceptions = IllegalStateException.class)
  public void testFindAllIFDsShouldThrowIfOffsetTooBig(RandomAccessInputStream stream)
      throws IllegalStateException, IOException {
    JXRParser parser = new JXRParser();
    parser.setInputStream(stream);
    parser.setRootIFDOffset((int) stream.length() + 1);
    parser.findAllIFDs();
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void testFindAllIFDsShouldThrowIfOnlyOffsetSet()
      throws IllegalStateException, IOException {
    JXRParser parser = new JXRParser();
    parser.setRootIFDOffset(10);
    parser.findAllIFDs();
  }

  @Test(dataProvider = "testStream", dataProviderClass = StaticDataProvider.class,
      expectedExceptions = IllegalStateException.class)
  public void testFindAllIFDsShouldThrowIfOnlyStreamSet(RandomAccessInputStream stream)
      throws IllegalStateException, IOException {
    JXRParser parser = new JXRParser();
    parser.setInputStream(stream);
    parser.findAllIFDs();
  }

  @Test(dataProvider = "testStream",
      dataProviderClass = StaticDataProvider.class)
  public void testGetIFDCountShouldReturnOneForTestImage(RandomAccessInputStream stream)
      throws IOException, JXRException {
    JXRReader reader = new JXRReader(stream);
    JXRParser parser = new JXRParser();
    parser.setInputStream(stream);
    parser.setRootIFDOffset(reader.getRootIFDOffset());

    int expectedIFDCount = 1;
    parser.findAllIFDs();
    int actualIFDCount = parser.getIFDCount();
    Assert.assertEquals(expectedIFDCount, actualIFDCount);
  }

  @Test(dataProvider = "testStream",
      dataProviderClass = StaticDataProvider.class)
  public void testGetIFDCountShouldReturnZeroForOffsetOutsideOfStream(RandomAccessInputStream stream)
      throws IOException, JXRException {
    JXRParser parser = new JXRParser();
    parser.setInputStream(stream);
    parser.setRootIFDOffset((int) stream.length() + 1);

    int expectedIFDCount = 0;
    int actualIFDCount = parser.getIFDCount();
    Assert.assertEquals(expectedIFDCount, actualIFDCount);
  }

}
