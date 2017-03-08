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
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.MapPair;
import ome.xml.model.OME;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import ome.xml.model.Pixels;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case for ImagingEnvironment Map values
 *
 * @author Andrew Patterson
 */
public class ImagingEnvironmentMapTest {

  private OME ome = new OME();

  @BeforeClass
  public void setUp() throws Exception {
    // Add an Image/Pixels
    Image image = new Image();
    image.setID("Image:0");
    Pixels pixels = new Pixels();
    pixels.setID("Pixels:0");
    image.setPixels(pixels);

    // Add an ImagingEnvironment with an Map
    ImagingEnvironment imagingEnvironment = new ImagingEnvironment();

    List<MapPair> map = new ArrayList<MapPair>();
    map.add(new MapPair("a", "1"));
    map.add(new MapPair("d", "2"));
    map.add(new MapPair("c", "3"));
    map.add(new MapPair("b", "4"));
    map.add(new MapPair("e", "5"));
    map.add(new MapPair("c", "6"));
    assertEquals(6, map.size());
    imagingEnvironment.setMap(map);

    image.setImagingEnvironment(imagingEnvironment);

    ome.addImage(image);
  }

  @Test
  public void testGenericExcitationSourceValid() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.newDocument();
    // Produce a valid OME DOM element hierarchy
    Element root = ome.asXMLElement(document);
    SPWModelMock.postProcess(root, document, false);
    OMEModel model = new OMEModelImpl();
    ome = new OME(document.getDocumentElement(), model);
    model.resolveReferences();
  }

  @Test
  public void testGenericExcitationSourceMapContent() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document document = parser.newDocument();
    // Produce a valid OME DOM element hierarchy
    Element root = ome.asXMLElement(document);
    SPWModelMock.postProcess(root, document, false);
    OMEModel model = new OMEModelImpl();
    ome = new OME(document.getDocumentElement(), model);
    model.resolveReferences();

    assertNotNull(ome); 
    assertEquals(ome.getImage(0).getPixels().getID(), "Pixels:0"); 
    assertNotNull(ome.getImage(0).getImagingEnvironment()); 

    ImagingEnvironment imagingEnvironment = ome.getImage(0).getImagingEnvironment(); 
    List<MapPair> dataMap = imagingEnvironment.getMap();

    assertEquals(6, dataMap.size());
    assertPair(dataMap, 0, "a", "1");
    assertPair(dataMap, 1, "d", "2");
    assertPair(dataMap, 2, "c", "3");
    assertPair(dataMap, 3, "b", "4");
    assertPair(dataMap, 4, "e", "5");
    assertPair(dataMap, 5, "c", "6");
  }

  void assertPair(List<MapPair> dataMap, int idx, String name, String value) {
    assertEquals(name, dataMap.get(idx).getName());
    assertEquals(value, dataMap.get(idx).getValue());
  }
}
