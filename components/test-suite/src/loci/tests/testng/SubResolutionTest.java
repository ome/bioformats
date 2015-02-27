/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.tests.testng;

import static org.testng.AssertJUnit.*;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Demonstration of the sub-resolution API.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/SubResolutionTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/SubResolutionTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SubResolutionTest {

  private String id;
  private IFormatReader reader;

  @Parameters({"id"})
  @BeforeClass
  public void init(String id) throws FormatException, IOException {
    this.id = id;

    reader = new ImageReader();
    reader.setFlattenedResolutions(false);
    reader.setId(id);
  }

  @Test
  public void testSubResolutionCount() {
    int seriesCount = reader.getSeriesCount();

    assertTrue(seriesCount > 0);

    for (int series=0; series<seriesCount; series++) {
      reader.setSeries(series);

      int resolutionCount = reader.getResolutionCount();
      assertTrue(resolutionCount > 0);

      for (int resolution=0; resolution<resolutionCount; resolution++) {
        reader.setResolution(resolution);
        assertTrue(reader.getSizeX() > 0);
        assertTrue(reader.getSizeY() > 0);
      }
    }
  }

  @AfterClass
  public void cleanup() throws IOException {
    reader.close();
  }

}
