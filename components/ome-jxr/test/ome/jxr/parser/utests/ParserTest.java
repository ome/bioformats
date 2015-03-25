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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.StaticDataProvider;
import ome.jxr.parser.Parser;

import org.testng.annotations.Test;

public class ParserTest extends StaticDataProvider {

  @Test(expectedExceptions = NullPointerException.class)
  public void testCtorShouldThrowIfNullStream()
      throws JXRException {
    new Parser(null, null);
  }

  @Test(dataProvider = "testStream", expectedExceptions = IllegalArgumentException.class)
  public void testParseShouldThrowIfOffsetTooBig(RandomAccessInputStream stream)
      throws IllegalStateException, IOException, JXRException {
    Parser parser = new Parser(null, stream);
    parser.parse(stream.length() + 1);
  }

  @Test(dataProvider = "testStream", expectedExceptions = IllegalArgumentException.class)
  public void testCtorShouldThrowIfOffsetTooSmall(RandomAccessInputStream stream)
      throws IllegalStateException, JXRException {
    Parser parser = new Parser(null, stream);
    parser.parse(-1);
  }

  @Test(dataProvider = "testStream")
  public void testCtorAndClose(RandomAccessInputStream stream)
      throws JXRException, IOException {
    long safeParsingOffset = 32;
    Parser parser = new Parser(null, stream);
    parser.parse(safeParsingOffset);
    parser.close();
  }
}
