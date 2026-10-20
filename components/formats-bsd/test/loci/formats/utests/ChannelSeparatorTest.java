/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2025 Open Microscopy Environment:
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

package loci.formats.utests;

import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
import loci.formats.IFormatReader;
import loci.formats.in.FakeReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;

import ome.xml.model.primitives.PositiveInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChannelSeparatorTest {

  private ChannelSeparator reader;
  private OMEXMLService service;
  private MetadataRetrieve m;

  @BeforeMethod
  public void setUp() throws Exception {
    reader = new ChannelSeparator(new FakeReader());
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);
    reader.setMetadataStore(service.createOMEXMLMetadata());
    reader.setFlattenedResolutions(false);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    reader.close();
  }

  @Test
  public void testSingleChannel() throws Exception {
    reader.setId("1C&sizeC=1.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 1);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
  }

  @Test
  public void testTwoChannels() throws Exception {
    reader.setId("2C&sizeC=2.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 2);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
  }

  @Test
  public void testThreeChannels() throws Exception {
    reader.setId("3C&sizeC=3.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 3);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
  }

  @Test
  public void testFourChannels() throws Exception {
    reader.setId("4C&sizeC=4.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 4);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 3),
      new PositiveInteger(1));
  }

  @Test
  public void testFiveChannels() throws Exception {
    reader.setId("5C&sizeC=5.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 5);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 3),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 4),
      new PositiveInteger(1));
  }

  @Test
  public void testSixChannels() throws Exception {
    reader.setId("6C&sizeC=6.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 6);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 3),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 4),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 5),
      new PositiveInteger(1));
  }

  @Test
  public void testOneRGBChannel() throws Exception {
    reader.setId("rgb&sizeC=3&rgb=3.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 3);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
  }

  @Test
  public void testTwoRGBChannels() throws Exception {
    reader.setId("rgb&sizeC=6&rgb=3.fake");
    assertTrue(service.validateOMEXML(service.getOMEXML(m)));
    m = service.asRetrieve(reader.getMetadataStore());
    assertEquals(m.getChannelCount(0), 6);
    assertEquals(m.getChannelSamplesPerPixel(0, 0),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 1),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 2),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 3),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 4),
      new PositiveInteger(1));
    assertEquals(m.getChannelSamplesPerPixel(0, 5),
      new PositiveInteger(1));
  }
}
