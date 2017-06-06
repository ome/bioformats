/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import ome.xml.model.Channel;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.GenericExcitationSource;
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
 * Test case for GenericExcitationSource Map values
 *
 * @author Andrew Patterson
 */
public class GenericExcitationMapTest {

  private OME ome = new OME();

  @BeforeClass
  public void setUp() throws Exception {
    Instrument instrument = new Instrument();
    instrument.setID("Instrument:0");
    // Add a GenericExcitationSource with an Map
    GenericExcitationSource geSource = new GenericExcitationSource();
    geSource.setID("LightSource:0");
    List<MapPair> dataMap = new ArrayList<MapPair>();
    dataMap.add(new MapPair("a", "1"));
    dataMap.add(new MapPair("d", "2"));
    dataMap.add(new MapPair("c", "3"));
    dataMap.add(new MapPair("b", "4"));
    dataMap.add(new MapPair("e", "5"));
    dataMap.add(new MapPair("c", "6"));
    assertEquals(6, dataMap.size());
    geSource.setMap(dataMap);

    instrument.addLightSource(geSource);
    ome.addInstrument(instrument);
    // Add an Image/Pixels with a LightSourceSettings reference to the
    // GenericExcitationSource on one of its channels.
    Image image = new Image();
    image.setID("Image:0");
    Pixels pixels = new Pixels();
    pixels.setID("Pixels:0");
    Channel channel = new Channel();
    channel.setID("Channel:0");
    LightSourceSettings settings = new LightSourceSettings();
    settings.setID("LightSource:0");
    channel.setLightSourceSettings(settings);
    pixels.addChannel(channel);
    image.setPixels(pixels);
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
    assertEquals(ome.getImage(0).getPixels().getChannel(0).getLightSourceSettings().getID(), "LightSource:0");

    assertNotNull(ome.getInstrument(0).getLightSource(0));
    GenericExcitationSource geSource = (GenericExcitationSource) ome.getInstrument(0).getLightSource(0);
    List<MapPair> dataMap = geSource.getMap();

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
