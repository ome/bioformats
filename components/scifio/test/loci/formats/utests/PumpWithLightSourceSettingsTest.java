
package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ome.xml.model.Arc;
import ome.xml.model.Channel;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.OME;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import ome.xml.model.Pixels;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case which outlines the problems seen in ticket:571.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/PumpWithLightSourceSettingsTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/PumpWithLightSourceSettingsTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class PumpWithLightSourceSettingsTest {

  private OME ome = new OME();

  @BeforeClass
  public void setUp() throws Exception {
    Instrument instrument = new Instrument();
    instrument.setID("Instrument:0");
    // Add a Laser with an Arc pump
    Laser laser = new Laser();
    laser.setID("Laser:0");
    Arc pump = new Arc();
    pump.setID("Arc:0");
    laser.linkPump(pump);
    instrument.addLightSource(laser);
    instrument.addLightSource(pump);
    ome.addInstrument(instrument);
    // Add an Image/Pixels with a LightSourceSettings reference to the Pump
    // on one of its channels.
    Image image = new Image();
    image.setID("Image:0");
    Pixels pixels = new Pixels();
    pixels.setID("Pixels:0");
    Channel channel = new Channel();
    channel.setID("Channel:0");
    LightSourceSettings settings = new LightSourceSettings();
    settings.setID("Arc:0");
    channel.setLightSourceSettings(settings);
    pixels.addChannel(channel);
    image.setPixels(pixels);
    ome.addImage(image);
  }

  @Test
  public void testLightSourceType() throws Exception {
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

}
