/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.POIService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class POIServiceTest {

  private POIService service;

  private static final String TEST_XLS = "test.xls";

  private static final String WORKBOOK_DOCUMENT = "Root Entry" + File.separator + "Workbook";

  private static final int WORKBOOK_LENGTH = 9604;

  @BeforeMethod
  public void setUp() throws DependencyException, IOException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(POIService.class);
    URL file = this.getClass().getResource(TEST_XLS);
    service.initialize(file.getPath());
  }

  @Test
  public void testDocuments() {
    Vector<String> documents = service.getDocumentList();
    for (String document : documents) {
      if (document.equals(WORKBOOK_DOCUMENT)) {
        return;
      }
    }
    fail("Unable to find document: " + WORKBOOK_DOCUMENT);
  }

  @Test
  public void testWorkbookDocumentAsStream() throws IOException {
    RandomAccessInputStream stream = 
      service.getDocumentStream(WORKBOOK_DOCUMENT); 
    assertNotNull(stream);
    assertEquals(WORKBOOK_LENGTH, stream.length());
  }

  @Test
  public void testWorkbookDocumentBytes() throws IOException {
    byte[] bytes = service.getDocumentBytes(WORKBOOK_DOCUMENT); 
    assertNotNull(bytes);
    assertEquals(WORKBOOK_LENGTH, bytes.length);
  }

  @Test
  public void testWorkbookFileSize() {
    assertEquals(WORKBOOK_LENGTH, service.getFileSize(WORKBOOK_DOCUMENT));
  }
}
