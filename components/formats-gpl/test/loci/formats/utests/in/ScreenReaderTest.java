/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2017 Open Microscopy Environment:
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

package loci.formats.utests.in;

import java.io.File;
import java.io.IOException;

import loci.formats.in.ScreenReader;
import loci.formats.FormatException;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;
import loci.common.services.ServiceFactory;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link ScreenReader}.
 */
public class ScreenReaderTest {

  public ScreenReader reader;
  public File file;
  private OMEXMLService service;
  private MetadataRetrieve m;
 
  @BeforeMethod
  public void setUp() throws Exception {
    reader = new ScreenReader();
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);
    reader.setMetadataStore(service.createOMEXMLMetadata());
  }

  @Test
  public void testMinimalScreen() throws FormatException, IOException {
    file = new File(this.getClass().getResource("minimal.screen").getFile());
    System.out.println(file.getAbsolutePath());
    reader.setId(file.getAbsolutePath());
    m = service.asRetrieve(reader.getMetadataStore());
    
    assertEquals(m.getPlateCount(), 1);
    assertEquals(m.getWellCount(0), 1);
    assertEquals(m.getWellSampleCount(0, 0), 1);
  }
}
