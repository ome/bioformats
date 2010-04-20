
package ome.xml.utests;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.testng.AssertJUnit.*;

import ome.xml.r201004.Channel;
import ome.xml.r201004.Image;
import ome.xml.r201004.MetadataOnly;
import ome.xml.r201004.OME;
import ome.xml.r201004.Pixels;
import ome.xml.r201004.enums.DimensionOrder;
import ome.xml.r201004.enums.EnumerationException;
import ome.xml.r201004.enums.PixelType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InOut201004Test
{
    private static String IMAGE_ID = "Image:0";

    private static String PIXELS_ID = "Pixels:0";

    private static DimensionOrder dimensionOrder = DimensionOrder.XYZCT;

    private static PixelType pixelType = PixelType.UINT16;

    private static final Integer SIZE_X = 512;

    private static final Integer SIZE_Y = 512;

    private static final Integer SIZE_Z = 64;

    private static final Integer SIZE_C = 3;

    private static final Integer SIZE_T = 50;

    /** XML namespace. */
    public static final String XML_NS = 
        "http://www.openmicroscopy.org/Schemas/OME/2010-04";

    /** XSI namespace. */
    public static final String XSI_NS =
        "http://www.w3.org/2001/XMLSchema-instance";

    /** XML schema location. */
    public static final String SCHEMA_LOCATION =
        "http://svn.openmicroscopy.org.uk/svn/specification/Xml/Working/ome.xsd";

    private Document document;

    private String asString;

    private OME ome;

    @BeforeClass
    public void setUp()
        throws ParserConfigurationException, TransformerException, EnumerationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        document = parser.newDocument();
        makeXML();
        asString = asString();
        ome = OME.fromXMLElement(document.getDocumentElement());
    }

    @Test
    public void testValidOMENode() throws EnumerationException
    {
        assertNotNull(ome);
        assertEquals(1, ome.sizeOfImageList());
    }

    @Test(dependsOnMethods={"testValidOMENode"})
    public void testValidImageNode()
    {
        Image image = ome.getImage(0);
        assertNotNull(image);
        assertEquals(IMAGE_ID, image.getID());
    }

    @Test(dependsOnMethods={"testValidImageNode"})
    public void testValidPixelsNode()
    {
        Pixels pixels = ome.getImage(0).getPixels();
        assertEquals(SIZE_X, pixels.getSizeX());
        assertEquals(SIZE_Y, pixels.getSizeY());
        assertEquals(SIZE_Z, pixels.getSizeZ());
        assertEquals(SIZE_C, pixels.getSizeC());
        assertEquals(SIZE_T, pixels.getSizeT());
        assertEquals(dimensionOrder, pixels.getDimensionOrder());
        assertEquals(pixelType, pixels.getType());
        assertNotNull(pixels.getMetadataOnly());
    }

    @Test(dependsOnMethods={"testValidPixelsNode"})
    public void testValidChannelNode()
    {
        Pixels pixels = ome.getImage(0).getPixels();
        assertEquals(3, pixels.sizeOfChannelList());
        for (Channel channel : pixels.copyChannelList())
        {
            assertNotNull(channel.getID());
        }
    }

    private void makeXML()
    {
        // Create <OME/>
        OME ome = new OME();
        // Create <Image/>
        Image image = new Image();
        image.setID(IMAGE_ID);
        // Create <Pixels/>
        Pixels pixels = new Pixels();
        pixels.setID(PIXELS_ID);
        pixels.setSizeX(SIZE_X);
        pixels.setSizeY(SIZE_Y);
        pixels.setSizeZ(SIZE_Z);
        pixels.setSizeC(SIZE_C);
        pixels.setSizeT(SIZE_T);
        pixels.setDimensionOrder(dimensionOrder);
        pixels.setType(pixelType);
        pixels.setMetadataOnly(new MetadataOnly());
        // Create <Channel/> under <Pixels/>
        for (int i = 0; i < SIZE_C; i++)
        {
            Channel channel = new Channel();
            channel.setID("Channel:" + i);
            pixels.addChannel(channel);
        }
        // Put <Pixels/> under <Image/>
        image.setPixels(pixels);
        // Put <Image/> under <OME/>
        ome.addImage(image);
        Element root = ome.asXMLElement(document);
        root.setAttribute("xmlns", XML_NS);
        root.setAttribute("xmlns:xsi", XSI_NS);
        root.setAttribute("xsi:schemaLocation",
                          XML_NS + " " + SCHEMA_LOCATION);
        document.appendChild(root);
    }

    private String asString() throws TransformerException 
    {
        TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source source = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        return stringWriter.toString();
    }

    private Document fromString(String xml)
        throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}