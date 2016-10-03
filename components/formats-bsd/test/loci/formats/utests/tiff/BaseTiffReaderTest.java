/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertEquals;
import loci.formats.tiff.IFD;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the functionality of BaseTiffReader
 */
public class BaseTiffReaderTest {

  private IFD ifd;
  private BaseTiffReaderMock reader;

  @BeforeClass
  public void setUp() throws Exception {
    ifd = new IFD();
    reader = new BaseTiffReaderMock();
  }
  
  @AfterClass
  public void tearDown() throws Exception {
    ifd.clear();
    reader.clearIFDs();
  }
  
  @Test
  public void testCreationDateString() {
    ifd.put(IFD.DATE_TIME, "CreationDate");
    reader.addIFD(ifd);
    assertEquals("CreationDate", reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateNull() {
    ifd.put(IFD.DATE_TIME, null);
    reader.addIFD(ifd);
    assertEquals(null, reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateEmptyString() {
    ifd.put(IFD.DATE_TIME, "");
    reader.addIFD(ifd);
    assertEquals("", reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateStringArray() {
    String [] creationDates = new String[2];
    creationDates[0] = "CreationDate1";
    creationDates[1] = "CreationDate2";
    ifd.put(IFD.DATE_TIME, creationDates);
    reader.addIFD(ifd);
    assertEquals("CreationDate1", reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateEmptyStringArray() {
    String [] creationDates = new String[1];
    creationDates[0] = "";
    ifd.put(IFD.DATE_TIME, creationDates);
    reader.addIFD(ifd);
    assertEquals("", reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateNullStringArray() {
    String [] creationDates = new String[1];
    creationDates[0] = null;
    ifd.put(IFD.DATE_TIME, creationDates);
    reader.addIFD(ifd);
    assertEquals(null, reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateZeroLengthArray() {
    String [] creationDates = new String[0];
    ifd.put(IFD.DATE_TIME, creationDates);
    reader.addIFD(ifd);
    assertEquals(null, reader.getCreationDate());
  }
  
  @Test
  public void testCreationDateMultipleIFDs() {
    ifd.put(IFD.DATE_TIME, "CreationDate1");
    reader.addIFD(ifd);
    IFD ifd2 = new IFD();
    ifd2.put(IFD.DATE_TIME, "CreationDate2");
    reader.addIFD(ifd2);
    assertEquals("CreationDate1", reader.getCreationDate());
  }

  // TODO: Test remaining Metadata initialisation and helper methods
}
