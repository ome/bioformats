/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.assertNotNull;

import java.io.IOException;

import loci.common.RandomAccessInputStream;

import org.testng.annotations.Test;

public class JXRReaderTest extends StaticDataProvider {

  @Test(expectedExceptions = IOException.class)
  public void testCtorWithEmptyStringShouldThrowIOE() throws IOException,
      JXRException {
    new JXRReader("");
  }

  @Test(dataProvider = "malformedHeaders", expectedExceptions = JXRException.class)
  public void testCtorWithMalformedHeadersShouldThrowJXRE(byte[] malformedHeader)
      throws IOException, JXRException {
    new JXRReader(new RandomAccessInputStream(malformedHeader));
  }

  @Test(dataProvider = "testStream")
  public void testCtor(RandomAccessInputStream stream) throws IOException,
      JXRException {
    JXRReader reader = new JXRReader(stream);
    assertNotNull(reader);
    reader.close();
  }

  @Test(dataProvider = "testStream")
  public void testGetMetadata(RandomAccessInputStream stream)
      throws IllegalStateException, IOException, JXRException {
    JXRReader reader = new JXRReader(stream);
    assertNotNull(reader.getMetadata());
    reader.close();
  }

}
