//
// InOut201004Test.java
//

/*
 * ome.xml.utests
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007-2008 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.utests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.testng.AssertJUnit.*;

import ome.xml.r201004.Annotation;
import ome.xml.r201004.Arc;
import ome.xml.r201004.BinaryFile;
import ome.xml.r201004.BooleanAnnotation;
import ome.xml.r201004.DoubleAnnotation;
import ome.xml.r201004.External;
import ome.xml.r201004.Filament;
import ome.xml.r201004.LightEmittingDiode;
import ome.xml.r201004.LongAnnotation;
import ome.xml.r201004.OMEModel;
import ome.xml.r201004.OMEModelImpl;
import ome.xml.r201004.OMEModelObject;
import ome.xml.r201004.Objective;
import ome.xml.r201004.ObjectiveSettings;
import ome.xml.r201004.Reference;
import ome.xml.r201004.StringAnnotation;
import ome.xml.r201004.StructuredAnnotations;
import ome.xml.r201004.TimestampAnnotation;
import ome.xml.r201004.XMLAnnotation;
import ome.xml.r201004.Channel;
import ome.xml.r201004.Dichroic;
import ome.xml.r201004.Detector;
import ome.xml.r201004.Filter;
import ome.xml.r201004.FilterSet;
import ome.xml.r201004.Image;
import ome.xml.r201004.Instrument;
import ome.xml.r201004.Laser;
import ome.xml.r201004.MetadataOnly;
import ome.xml.r201004.OME;
import ome.xml.r201004.OTF;
import ome.xml.r201004.Pixels;
import ome.xml.r201004.Plate;
import ome.xml.r201004.Rectangle;
import ome.xml.r201004.ROI;
import ome.xml.r201004.Shape;
import ome.xml.r201004.Union;
import ome.xml.r201004.Well;
import ome.xml.r201004.WellSample;
import ome.xml.r201004.enums.ArcType;
import ome.xml.r201004.enums.DimensionOrder;
import ome.xml.r201004.enums.EnumerationException;
import ome.xml.r201004.enums.FilamentType;
import ome.xml.r201004.enums.FilterType;
import ome.xml.r201004.enums.LaserType;
import ome.xml.r201004.enums.NamingConvention;
import ome.xml.r201004.enums.PixelType;
import ome.xml.r201004.primitives.NonNegativeInteger;
import ome.xml.r201004.primitives.PositiveInteger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/InOut201004Test.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/InOut201004Test.java">SVN</a></dd></dl>
 */
@Test(groups={"inOutTests"})
public class InOut201004Test {
  public static final String IMAGE_ID = "Image:0";

  public static final String GENERAL_ANNOTATION_NAMESPACE = "test-ome-InOut201004-namespace";

  public static final String IMAGE_ANNOTATION_ID = "Annotation:Boolean0";

  public static final String PIXELS_ID = "Pixels:0";

  public static final String PIXELS_ANNOTATION_ID = "Annotation:Double0";

  public static final String CHANNEL_ANNOTATION_ID = "Annotation:XML0";

  public static final String INSTRUMENT_ID = "Instrument:0";

  public static final String DETECTOR_ID = "Detector:0";

  public static final String LIGHTSOURCE_LASER_ID = "LightSource:0";

  public static final String LIGHTSOURCE_PUMP_ID = "LightSource:1";

  public static final String LIGHTSOURCE_ARC_ID = "LightSource:2";

  public static final String LIGHTSOURCE_FILAMENT_ID = "LightSource:3";

  public static final String LIGHTSOURCE_LED_ID = "LightSource:4";

  public static final String DICHROIC_ID = "Dichroic:0";

  public static final String FILTERSET_ID = "FilterSet:0";

  public static final String EM_FILTER_ID = "Filter:0";

  public static final String EX_FILTER_ID = "Filter:1";

  public static final String OBJECTIVE_ID = "Objective:0";

  public static final String OTF_ID = "OTF:0";

  public static final String PLATE_ID = "Plate:0";

  public static final String PLATE_ANNOTATION_ID = "Annotation:Timestamp0";

  public static final String WELL_ANNOTATION_ID = "Annotation:Long0";

  public static final String ROI_ID = "ROI:5";

  public static final String ROI_ANNOTATION_ID = "Annotation:String0";

  public static final String SHAPE_ID = "Shape:0";

  public static final DimensionOrder DIMENSION_ORDER = DimensionOrder.XYZCT;

  public static final PixelType PIXEL_TYPE = PixelType.UINT16;

  public static final Integer SIZE_X = 512;

  public static final Integer SIZE_Y = 512;

  public static final Integer SIZE_Z = 64;

  public static final Integer SIZE_C = 3;

  public static final Integer SIZE_T = 50;

  public static final String DETECTOR_MODEL = "ReallySensitive!";

  public static final String LIGHTSOURCE_LASER_MODEL = "ReallyBrightLaser!";

  public static final String LIGHTSOURCE_PUMP_MODEL = "ReallyBrightPump!";

  public static final String LIGHTSOURCE_ARC_MODEL = "ReallyBrightArc!";

  public static final String LIGHTSOURCE_FILAMENT_MODEL = "ReallyBrightFilament!";

  public static final String LIGHTSOURCE_LED_MODEL = "ReallyBrightLED!";

  public static final String OBJECTIVE_MODEL = "ReallyClear!";

  public static final String DICHROIC_SN = "0123456789";

  public static final String FILTERSET_LOT = "RandomLot";

  public static final FilterType EM_FILTER_TYPE = FilterType.LONGPASS;

  public static final FilterType EX_FILTER_TYPE = FilterType.NEUTRALDENSITY;

  public static final PixelType OTF_PIXELTYPE = PixelType.FLOAT;

  public static final Double LIGHTSOURCE_LASER_POWER = 1000.0;

  public static final Double LIGHTSOURCE_PUMP_POWER = 100.0;

  public static final Double LIGHTSOURCE_ARC_POWER = 500.0;

  public static final Double LIGHTSOURCE_FILAMENT_POWER = 200.0;

  public static final Double LIGHTSOURCE_LED_POWER = 10.0;

  public static final LaserType LASER_TYPE = LaserType.DYE;
  
  public static final ArcType ARC_TYPE = ArcType.HGXE;

  public static final FilamentType FILAMENT_TYPE = FilamentType.HALOGEN;

  public static final Integer OTF_SIZE_X = 512;

  public static final Integer OTF_SIZE_Y = 512;

  public static final Boolean OTF_OPTICAL_AXIS_AVERAGED = Boolean.FALSE;

  public static final Boolean IMAGE_ANNOTATION_VALUE = Boolean.FALSE;

  public static final String CHANNEL_ANNOTATION_VALUE =
  "<TestData><key>foo</key><value>bar</value></TestData>";

  public static final Double PIXELS_ANNOTATION_VALUE = 3.14;

  public static final String PLATE_ANNOTATION_VALUE = "1970-01-01T00:00:00";

  public static final Long WELL_ANNOTATION_VALUE = 262144L;

  public static final Integer WELL_ROWS = 3;

  public static final Integer WELL_COLS = 2;

  public static final NamingConvention WELL_ROW = NamingConvention.LETTER;

  public static final NamingConvention WELL_COL = NamingConvention.NUMBER;

  public static final String ROI_ANNOTATION_VALUE = "Some extra ROI data";

  public static final Double RECTANGLE_X = 10.0;

  public static final Double RECTANGLE_Y = 20.0;

  public static final Double RECTANGLE_WIDTH = 128.0;

  public static final Double RECTANGLE_HEIGHT = 256.0;

  public static final String OTF_BINARY_FILE_NAME = "abc.bin";

  public static final Integer OTF_BINARY_FILE_SIZE = 64;

  public static final String OTF_BINARY_FILE_EXTERNAL_HREF = "file:///abc.bin";

  public static final String OTF_BINARY_FILE_EXTERNAL_SHA1 =
    "1234567890123456789012345678901234567890";

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

  public String asString;

  public OMEModelMock mock;

  public OME ome;

  public OMEModel model;

  @Parameters({"mockClassName"})
  @BeforeClass
  public void setUp(String mockClassName) throws Exception {
    Class mockClass = Class.forName(mockClassName);
    Constructor constructor = mockClass.getDeclaredConstructor();
    mock = (OMEModelMock) constructor.newInstance();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    document = parser.newDocument();
    ome = mock.getRoot();
    // Produce a valid OME DOM element hierarchy
    Element root = ome.asXMLElement(document);
    root.setAttribute("xmlns", XML_NS);
    root.setAttribute("xmlns:xsi", XSI_NS);
    root.setAttribute("xsi:schemaLocation", XML_NS + " " + SCHEMA_LOCATION);
    document.appendChild(root);
    // Produce string XML
    asString = asString();
  }

  @Test
  public void testValidOMENode() throws EnumerationException {
    model = new OMEModelImpl();
    // Read string XML in as a DOM tree and parse into the object hierarchy
    ome = new OME(document.getDocumentElement(), model);
    model.resolveReferences();
    assertNotNull(ome);
    assertEquals(1, ome.sizeOfImageList());
  }

  @Test(dependsOnMethods={"testValidOMENode"})
  public void testValidImageNode() {
    Image image = ome.getImage(0);
    assertNotNull(image);
    assertEquals(IMAGE_ID, image.getID());
  }

  @Test(dependsOnMethods={"testValidImageNode"})
  public void testValidImageAnnotation() {
    Annotation n = ome.getImage(0).getLinkedAnnotation(0);
    assertNotNull(n);
    assertEquals(BooleanAnnotation.class, n.getClass());
    BooleanAnnotation b = (BooleanAnnotation) n;
    assertEquals(b.getValue(), IMAGE_ANNOTATION_VALUE);
    assertEquals(b.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    assertEquals(b.getID(), IMAGE_ANNOTATION_ID);
  }

  @Test(dependsOnMethods={"testValidImageNode"})
  public void testValidPixelsNode() {
    Pixels pixels = ome.getImage(0).getPixels();
    assertEquals(SIZE_X, pixels.getSizeX().getValue());
    assertEquals(SIZE_Y, pixels.getSizeY().getValue());
    assertEquals(SIZE_Z, pixels.getSizeZ().getValue());
    assertEquals(SIZE_C, pixels.getSizeC().getValue());
    assertEquals(SIZE_T, pixels.getSizeT().getValue());
    assertEquals(DIMENSION_ORDER, pixels.getDimensionOrder());
    assertEquals(PIXEL_TYPE, pixels.getType());
    assertNotNull(pixels.getMetadataOnly());
  }

  @Test(dependsOnMethods={"testValidPixelsNode"})
  public void testValidChannelNode() {
    Pixels pixels = ome.getImage(0).getPixels();
    assertEquals(3, pixels.sizeOfChannelList());
    for (Channel channel : pixels.copyChannelList()) {
      assertNotNull(channel.getID());
    }
  }

  @Test(dependsOnMethods={"testValidChannelNode"})
  public void testValidChannelAnnotation() {
    Channel c = ome.getImage(0).getPixels().getChannel(0);
    Annotation n = c.getLinkedAnnotation(0);
    assertNotNull(n);
    assertTrue(n instanceof XMLAnnotation);
    assertEquals(CHANNEL_ANNOTATION_ID, n.getID());
    assertEquals(n.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    XMLAnnotation xml = (XMLAnnotation) n;
    assertEquals(xml.getValue(), CHANNEL_ANNOTATION_VALUE);
  }

  @Test(dependsOnMethods={"testValidPixelsNode"})
  public void testValidPixelsAnnotation() {
    Annotation n = ome.getImage(0).getPixels().getLinkedAnnotation(0);
    assertNotNull(n);
    assertTrue(n instanceof DoubleAnnotation);
    DoubleAnnotation b = (DoubleAnnotation) n;
    assertEquals(b.getValue(), PIXELS_ANNOTATION_VALUE);
    assertEquals(b.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    assertEquals(b.getID(), PIXELS_ANNOTATION_ID);
  }

  @Test(dependsOnMethods={"testValidOMENode"})
  public void testValidInstrumentNode() {
    Instrument instrument = ome.getInstrument(0);
    assertNotNull(instrument);
    assertEquals(INSTRUMENT_ID, instrument.getID());
  }

  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidDetectorNode() {
    Detector detector = ome.getInstrument(0).getDetector(0);
    assertNotNull(detector);
    assertEquals(DETECTOR_ID, detector.getID());
    assertEquals(DETECTOR_MODEL, detector.getModel());
  }

  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidLaserNode() {
    Laser laser = (Laser) ome.getInstrument(0).getLightSource(0);
    assertNotNull(laser);
    assertEquals(LIGHTSOURCE_LASER_ID, laser.getID());
    assertEquals(LIGHTSOURCE_LASER_MODEL, laser.getModel());
    assertEquals(LIGHTSOURCE_LASER_POWER, laser.getPower());
    assertEquals(LASER_TYPE, laser.getType());
  }

  @Test(dependsOnMethods={"testValidLaserNode"})
  public void testValidPumpNode() {
    Laser laser = (Laser) ome.getInstrument(0).getLightSource(0);
    Laser laserPump = (Laser) ome.getInstrument(0).getLightSource(1);
    assertNotNull(laserPump);
    assertEquals(LIGHTSOURCE_PUMP_ID, laserPump.getID());
    assertEquals(LIGHTSOURCE_PUMP_MODEL, laserPump.getModel());
    assertEquals(LIGHTSOURCE_PUMP_POWER, laserPump.getPower());
    assertEquals(LASER_TYPE, laserPump.getType());
    assertEquals(laser.getLinkedPump(),laserPump);
  }

  // Create <Arc/> under <Instrument/>
  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidArcNode() {
    Arc arc = (Arc) ome.getInstrument(0).getLightSource(2);
    assertNotNull(arc);
    assertEquals(LIGHTSOURCE_ARC_ID, arc.getID());
    assertEquals(LIGHTSOURCE_ARC_MODEL, arc.getModel());
    assertEquals(LIGHTSOURCE_ARC_POWER, arc.getPower());
    assertEquals(ARC_TYPE, arc.getType());
  }
  
  // Create <Filament/> under <Instrument/>
  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidFilamentNode() {
    Filament filament = (Filament) ome.getInstrument(0).getLightSource(3);
    assertNotNull(filament);
    assertEquals(LIGHTSOURCE_FILAMENT_ID, filament.getID());
    assertEquals(LIGHTSOURCE_FILAMENT_MODEL, filament.getModel());
    assertEquals(LIGHTSOURCE_FILAMENT_POWER, filament.getPower());
    assertEquals(FILAMENT_TYPE, filament.getType());
  }

  // Create <LightEmittingDiode/> under <Instrument/>
  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidLightEmittingDiodeNode() {
    LightEmittingDiode led = (LightEmittingDiode) ome.getInstrument(0).getLightSource(4);
    assertNotNull(led);
    assertEquals(LIGHTSOURCE_LED_ID, led.getID());
    assertEquals(LIGHTSOURCE_LED_MODEL, led.getModel());
    assertEquals(LIGHTSOURCE_LED_POWER, led.getPower());
  }

  
  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidDichroicNode() {
    Dichroic dichroic = ome.getInstrument(0).getDichroic(0);
    assertNotNull(dichroic);
    assertEquals(DICHROIC_ID, dichroic.getID());
    assertEquals(DICHROIC_SN, dichroic.getSerialNumber());
  }

  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidObjectiveNode()
  {
    Objective objective = ome.getInstrument(0).getObjective(0);
    assertNotNull(objective);
    assertEquals(OBJECTIVE_ID, objective.getID());
    assertEquals(OBJECTIVE_MODEL, objective.getModel());
  }

  @Test(dependsOnMethods={"testValidDichroicNode"})
  public void testValidFilterSetNode() {
    Dichroic dichroic = ome.getInstrument(0).getDichroic(0);
    assertEquals(1, dichroic.sizeOfLinkedFilterSetList());
    FilterSet filterSet = dichroic.getLinkedFilterSet(0);
    assertNotNull(filterSet);
    assertEquals(FILTERSET_ID, filterSet.getID());
    assertEquals(FILTERSET_LOT, filterSet.getLotNumber());
    assertEquals(filterSet.getLinkedDichroic().getID(), dichroic.getID());
  }

  @Test(dependsOnMethods={"testValidFilterSetNode"})
  public void testValidEmissionFilter() {
    Filter emFilter = ome.getInstrument(0).getFilter(0);
    assertNotNull(emFilter);
    assertEquals(EM_FILTER_ID, emFilter.getID());
    assertEquals(EM_FILTER_TYPE, emFilter.getType());
    FilterSet filterSet = ome.getInstrument(0).getDichroic(0).getLinkedFilterSet(0);
    assertEquals(EM_FILTER_ID, filterSet.getLinkedEmissionFilter(0).getID());
  }

  @Test(dependsOnMethods={"testValidFilterSetNode"})
  public void testValidExcitationFilter() {
    Filter exFilter = ome.getInstrument(0).getFilter(1);
    assertNotNull(exFilter);
    assertEquals(EX_FILTER_ID, exFilter.getID());
    assertEquals(EX_FILTER_TYPE, exFilter.getType());
    FilterSet filterSet = ome.getInstrument(0).getDichroic(0).getLinkedFilterSet(0);
    assertEquals(EX_FILTER_ID, filterSet.getLinkedExcitationFilter(0).getID());
  }

  @Test(dependsOnMethods={"testValidInstrumentNode"})
  public void testValidOTF() {
    OTF otf = ome.getInstrument(0).getOTF(0);
    assertNotNull(otf);
    assertEquals(OTF_ID, otf.getID());
    assertEquals(OTF_PIXELTYPE, otf.getType());
    assertEquals(OTF_SIZE_X, otf.getSizeX().getValue());
    assertEquals(OTF_SIZE_Y, otf.getSizeY().getValue());
    assertEquals(OTF_OPTICAL_AXIS_AVERAGED, otf.getOpticalAxisAveraged());
    ObjectiveSettings settings = otf.getObjectiveSettings();
    assertNotNull(settings);
    assertEquals(OBJECTIVE_ID, settings.getID());
    assertEquals(otf, ome.getInstrument(0).getFilterSet(0).getLinkedOTF(0));
    BinaryFile bf = otf.getBinaryFile();
    assertNotNull(bf);
    assertEquals(OTF_BINARY_FILE_NAME, bf.getFileName());
    assertEquals(OTF_BINARY_FILE_SIZE, bf.getSize());
    External external = bf.getExternal();
    assertNotNull(external);
    assertEquals(OTF_BINARY_FILE_EXTERNAL_HREF, external.gethref());
    assertEquals(OTF_BINARY_FILE_EXTERNAL_SHA1, external.getSHA1());
  }

  @Test(dependsOnMethods={"testValidInstrumentNode", "testValidImageNode"})
  public void testImageInstrumentLinkage() {
    Instrument instrument = ome.getInstrument(0);
    Image image = ome.getImage(0);

    Instrument linkedInstrument = image.getLinkedInstrument();
    assertNotNull(linkedInstrument);
    assertEquals(instrument.getID(), linkedInstrument.getID());

    assertEquals(1, instrument.sizeOfLinkedImageList());
    Image linkedImage = instrument.getLinkedImage(0);
    assertNotNull(linkedImage);
    assertEquals(image.getID(), linkedImage.getID());
  }

  @Test(dependsOnMethods={"testValidOMENode"})
  public void testValidPlateNode() {
    Plate plate = ome.getPlate(0);
    assertNotNull(plate);
    assertEquals(PLATE_ID, plate.getID());
    assertEquals(plate.getRows(), WELL_ROWS);
    assertEquals(plate.getColumns(), WELL_COLS);
    assertEquals(plate.getRowNamingConvention(), WELL_ROW);
    assertEquals(plate.getColumnNamingConvention(), WELL_COL);
    assertEquals(plate.sizeOfWellList(), WELL_ROWS * WELL_COLS);
    for (Integer row=0; row<WELL_ROWS; row++) {
      for (Integer col=0; col<WELL_COLS; col++) {
        Well well = plate.getWell(row * WELL_COLS + col);
        assertNotNull(well);
        assertEquals(String.format("Well:%d_%d", row, col), well.getID());
        assertEquals(well.getRow(), row);
        assertEquals(well.getColumn(), col);
      }
    }
  }

  @Test(dependsOnMethods={"testValidPlateNode"})
  public void testValidPlateAnnotation() {
    Annotation n = ome.getPlate(0).getLinkedAnnotation(0);
    assertNotNull(n);
    assertEquals(PLATE_ANNOTATION_ID, n.getID());
    assertEquals(n.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    assertTrue(n instanceof TimestampAnnotation);
    TimestampAnnotation timestamp = (TimestampAnnotation) n;
    assertEquals(timestamp.getValue(), PLATE_ANNOTATION_VALUE);
  }

  @Test(dependsOnMethods={"testValidPlateNode"})
  public void testValidWellSamples() {
    Plate plate = ome.getPlate(0);
    Integer wellSampleIndex = 0;
    for (int row=0; row<plate.getRows(); row++) {
      for (int col=0; col<plate.getColumns(); col++) {
        Well well = plate.getWell(row * plate.getColumns() + col);
        assertEquals(1, well.sizeOfWellSampleList());
        WellSample sample = well.getWellSample(0);
        assertNotNull(sample);
        assertEquals(String.format("WellSample:%d_%d", row, col),
          sample.getID());
        assertEquals(wellSampleIndex, sample.getIndex().getValue());
        Image image = sample.getLinkedImage();
        assertNotNull(image);
        assertEquals(IMAGE_ID, image.getID());
        wellSampleIndex++;
      }
    }
  }

  @Test(dependsOnMethods={"testValidWellSamples"})
  public void testValidWellAnnotation() {
    Annotation n = ome.getPlate(0).getWell(0).getLinkedAnnotation(0);
    assertNotNull(n);
    assertEquals(WELL_ANNOTATION_ID, n.getID());
    assertEquals(n.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    assertTrue(n instanceof LongAnnotation);
    LongAnnotation longAnnotation = (LongAnnotation) n;
    assertEquals(longAnnotation.getValue(), WELL_ANNOTATION_VALUE);
  }

  @Test(dependsOnMethods={"testValidOMENode"})
  public void testValidROINode() {
    ROI roi = ome.getROI(0);
    assertNotNull(roi);
    assertEquals(ROI_ID, roi.getID());

    Union shapeUnion = roi.getUnion();
    assertNotNull(shapeUnion);
    assertEquals(1, shapeUnion.sizeOfShapeList());
    Shape s = shapeUnion.getShape(0);
    assertNotNull(s);
    assertEquals(SHAPE_ID, s.getID());
    assertTrue(s instanceof Rectangle);

    Rectangle rect = (Rectangle) s;
    assertEquals(RECTANGLE_X, rect.getX());
    assertEquals(RECTANGLE_Y, rect.getY());
    assertEquals(RECTANGLE_WIDTH, rect.getWidth());
    assertEquals(RECTANGLE_HEIGHT, rect.getHeight());
  }

  @Test(dependsOnMethods={"testValidROINode"})
  public void testValidROIAnnotation() {
    Annotation n = ome.getROI(0).getLinkedAnnotation(0);
    assertNotNull(n);
    assertEquals(ROI_ANNOTATION_ID, n.getID());
    assertEquals(n.getNamespace(), GENERAL_ANNOTATION_NAMESPACE);
    assertTrue(n instanceof StringAnnotation);
    StringAnnotation string = (StringAnnotation) n;
    assertEquals(ROI_ANNOTATION_VALUE, string.getValue());
  }

  private String asString()
  throws TransformerException, UnsupportedEncodingException {
    TransformerFactory transformerFactory =
      TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    //Setup indenting to "pretty print"
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(
        "{http://xml.apache.org/xslt}indent-amount", "4");
    Source source = new DOMSource(document);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Result result = new StreamResult(new OutputStreamWriter(os, "utf-8"));
    transformer.transform(source, result);
    return os.toString();
  }

  @Test(groups={"disabled"})
  public static void main(String[] args) throws Exception {
    InOut201004Test t = new InOut201004Test();
    t.setUp("ome.xml.utests.ObjectBasedOMEModelMock");
    System.out.println("###\n### XML\n###");
    System.out.println(t.asString);
    t.testValidOMENode();
    System.out.println("###\n### Model Objects\n###");
    Map<String, OMEModelObject> objects = t.model.getModelObjects();
    for (Entry<String, OMEModelObject> entry : objects.entrySet())
    {
      System.out.println(String.format(
          "%s -- %s", entry.getKey(), entry.getValue().toString()));
    }
    System.out.println("###\n### References\n###");
    Map<OMEModelObject, List<Reference>> references = t.model.getReferences();
    for (Entry<OMEModelObject, List<Reference>> entry : references.entrySet())
    {
      System.out.println(String.format(
          "%s -- %s", entry.getKey(), entry.getValue().toString()));
    }
  }
}
