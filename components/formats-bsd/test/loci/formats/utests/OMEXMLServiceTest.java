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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Hashtable;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.meta.OriginalMetadataAnnotation;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.OME;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.XMLAnnotation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 */
public class OMEXMLServiceTest {

  private OMEXMLService service;

  @BeforeMethod
  public void setUp() throws DependencyException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(OMEXMLService.class);
  }

  @Test
  public void testOriginalMetadata() throws ServiceException {
    OMEXMLMetadata metadata = service.createOMEXMLMetadata();
    service.populateOriginalMetadata(metadata, "testKey", "testValue");

    Hashtable metadataTable = service.getOriginalMetadata(metadata);
    assertEquals(metadataTable.size(), 1);
    assertTrue("testValue".equals(metadataTable.get("testKey")));

    OME root = (OME) metadata.getRoot();
    StructuredAnnotations annotations = root.getStructuredAnnotations();
    assertEquals(annotations.sizeOfXMLAnnotationList(), 1);
    XMLAnnotation xmlAnn = annotations.getXMLAnnotation(0);
    String txt = "<OriginalMetadata><Key>testKey</Key><Value>testValue</Value></OriginalMetadata>";
    assertEquals(txt, xmlAnn.getValue());
    OriginalMetadataAnnotation omAnn = (OriginalMetadataAnnotation) xmlAnn;
    assertEquals("testValue", omAnn.getValueForKey());
  }

  /**
   * Test that the XML serialization of floating point unit properties
   * includes a decimal point. In the schema a shape's stroke width is
   * {@code type="xsd:float"} which in OMERO is mapped to a {@code double}.
   * @throws ServiceException unexpected
   */
  @Test
  public void testFloatingPointUnitProperty() throws ServiceException {
    final Length propertyValue = new Length(3.0d, UNITS.PIXEL);

    final StringBuffer expectedText = new StringBuffer();
    expectedText.append(" StrokeWidth=");
    expectedText.append('"');
    expectedText.append(propertyValue.value().doubleValue());
    expectedText.append('"');

    final OMEXMLMetadata metadata = service.createOMEXMLMetadata();
    metadata.setROIID("test ROI", 0);
    metadata.setPointID("test point", 0, 0);
    metadata.setPointX(0.0, 0, 0);
    metadata.setPointY(0.0, 0, 0);
    metadata.setPointStrokeWidth(propertyValue, 0, 0);
    final String xml = service.getOMEXML(metadata);

    assertTrue(xml.contains(expectedText));
  }


  /**
   * Test that the XML serialization of integer unit properties does not
   * include a decimal point. In the schema a shape's font size is
   * {@code type="OME:NonNegativeInt"} which in OMERO is mapped to a
   * {@code double}.
   * @throws ServiceException unexpected
   */
  @Test
  public void testIntegerUnitProperty() throws ServiceException {
    final Length propertyValue = new Length(12.0d, UNITS.POINT);

    final StringBuffer expectedText = new StringBuffer();
    expectedText.append(" FontSize=");
    expectedText.append('"');
    expectedText.append(propertyValue.value().longValue());
    expectedText.append('"');

    final OMEXMLMetadata metadata = service.createOMEXMLMetadata();
    metadata.setROIID("test ROI", 0);
    metadata.setPointID("test point", 0, 0);
    metadata.setPointX(0.0, 0, 0);
    metadata.setPointY(0.0, 0, 0);
    metadata.setPointFontSize(propertyValue, 0, 0);
    final String xml = service.getOMEXML(metadata);

    assertTrue(xml.contains(expectedText));
  }
}
