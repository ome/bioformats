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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
import ome.xml.r201004.BinaryFile;
import ome.xml.r201004.BooleanAnnotation;
import ome.xml.r201004.DoubleAnnotation;
import ome.xml.r201004.External;
import ome.xml.r201004.FileAnnotation;
import ome.xml.r201004.ListAnnotation;
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
import ome.xml.r201004.enums.DimensionOrder;
import ome.xml.r201004.enums.EnumerationException;
import ome.xml.r201004.enums.FilterType;
import ome.xml.r201004.enums.LaserType;
import ome.xml.r201004.enums.NamingConvention;
import ome.xml.r201004.enums.PixelType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/ome-xml/test/ome/xml/utests/InOut201004Test.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/ome-xml/test/ome/xml/utests/InOut201004Test.java">SVN</a></dd></dl>
 */
public class InOut201004Test {
  private static String IMAGE_ID = "Image:0";

  private static String IMAGE_ANNOTATION_ID = "Annotation:Boolean0";

  private static String PIXELS_ID = "Pixels:0";

  private static String PIXELS_ANNOTATION_ID = "Annotation:Double0";

  private static String CHANNEL_ANNOTATION_ID = "Annotation:Double0";

  private static String INSTRUMENT_ID = "Instrument:0";

  private static String DETECTOR_ID = "Detector:0";

  private static String LIGHTSOURCE_ID = "LightSource:0";

  private static String DICHROIC_ID = "Dichroic:0";

  private static String FILTERSET_ID = "FilterSet:0";

  private static String EM_FILTER_ID = "Filter:0";

  private static String EX_FILTER_ID = "Filter:1";

  private static String OBJECTIVE_ID = "Objective:0";

  private static String OTF_ID = "OTF:0";

  private static String PLATE_ID = "Plate:0";

  private static String PLATE_ANNOTATION_ID = "Annotation:Timestamp0";

  private static String WELL_ANNOTATION_ID = "Annotation:Long0";

  private static String ROI_ID = "ROI:5";

  private static String ROI_ANNOTATION_ID = "Annotation:String0";

  private static String SHAPE_ID = "Shape:0";

  private static DimensionOrder DIMENSION_ORDER = DimensionOrder.XYZCT;

  private static PixelType PIXEL_TYPE = PixelType.UINT16;

  private static final Integer SIZE_X = 512;

  private static final Integer SIZE_Y = 512;

  private static final Integer SIZE_Z = 64;

  private static final Integer SIZE_C = 3;

  private static final Integer SIZE_T = 50;

  private static final String CHANNEL_ANNOTATION_XML =
    "<TestData><key>foo</key><value>bar</value></TestData>";

  private static final String DETECTOR_MODEL = "ReallySensitive!";

  private static final String LIGHTSOURCE_MODEL = "ReallyBright!";

  private static final String OBJECTIVE_MODEL = "ReallyClear!";

  private static final String DICHROIC_SN = "0123456789";

  private static final String FILTERSET_LOT = "RandomLot";

  private static final FilterType EM_FILTER_TYPE = FilterType.LONGPASS;

  private static final FilterType EX_FILTER_TYPE = FilterType.NEUTRALDENSITY;

  private static final PixelType OTF_PIXELTYPE = PixelType.FLOAT;

  private static final Double LIGHTSOURCE_POWER = 1000.0;

  private static final LaserType LASER_TYPE = LaserType.DYE;

  private static final Integer OTF_SIZE_X = 512;

  private static final Integer OTF_SIZE_Y = 512;

  private static final Boolean OTF_OPTICAL_AXIS_AVERAGED = Boolean.FALSE;

  private static final String PLATE_ANNOTATION_VALUE = "1970-01-01T00:00:00";

  private static final String WELL_ANNOTATION_VALUE = "262144";

  private static final Integer WELL_ROWS = 3;

  private static final Integer WELL_COLS = 2;

  private static final NamingConvention WELL_ROW = NamingConvention.LETTER;

  private static final NamingConvention WELL_COL = NamingConvention.NUMBER;

  private static final String ROI_ANNOTATION_VALUE = "Some extra ROI data";

  private static final Double RECTANGLE_X = 10.0;

  private static final Double RECTANGLE_Y = 20.0;

  private static final Double RECTANGLE_WIDTH = 128.0;

  private static final Double RECTANGLE_HEIGHT = 256.0;

  private static final String OTF_BINARY_FILE_NAME = "abc.bin";

  private static final Integer OTF_BINARY_FILE_SIZE = 64;

  private static final String OTF_BINARY_FILE_EXTERNAL_HREF = "file:///abc.bin";

  private static final String OTF_BINARY_FILE_EXTERNAL_SHA1 =
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

  public OME ome;

  public OMEModel model;

  @BeforeClass
  public void setUp()
  throws ParserConfigurationException, TransformerException,
  EnumerationException, UnsupportedEncodingException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    document = parser.newDocument();
    // Put <Image/> under <OME/>
    ome = new OME();
    ome.addImage(makeImage());
    ome.addPlate(makePlate());
    ome.addInstrument(makeInstrument());
    ome.addROI(makeROI());
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
    assertTrue(n instanceof BooleanAnnotation);
    BooleanAnnotation b = (BooleanAnnotation) n;
    assertEquals(b.getValue(), "false");
    assertEquals(b.getID(), IMAGE_ANNOTATION_ID);
  }

  @Test(dependsOnMethods={"testValidImageNode"})
  public void testValidPixelsNode() {
    Pixels pixels = ome.getImage(0).getPixels();
    assertEquals(SIZE_X, pixels.getSizeX());
    assertEquals(SIZE_Y, pixels.getSizeY());
    assertEquals(SIZE_Z, pixels.getSizeZ());
    assertEquals(SIZE_C, pixels.getSizeC());
    assertEquals(SIZE_T, pixels.getSizeT());
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
    XMLAnnotation xml = (XMLAnnotation) n;
    assertEquals(xml.getValue(), CHANNEL_ANNOTATION_XML);
  }

  @Test(dependsOnMethods={"testValidPixelsNode"})
  public void testValidPixelsAnnotation() {
    Annotation n = ome.getImage(0).getPixels().getLinkedAnnotation(0);
    assertNotNull(n);
    assertTrue(n instanceof DoubleAnnotation);
    DoubleAnnotation b = (DoubleAnnotation) n;
    assertEquals(b.getValue(), "3.14");
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
    assertEquals(LIGHTSOURCE_ID, laser.getID());
    assertEquals(LIGHTSOURCE_MODEL, laser.getModel());
    assertEquals(LIGHTSOURCE_POWER, laser.getPower());
    assertEquals(LASER_TYPE, laser.getType());
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
    assertEquals(OTF_SIZE_X, otf.getSizeX());
    assertEquals(OTF_SIZE_Y, otf.getSizeY());
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
        assertEquals(wellSampleIndex, sample.getIndex());
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
    assertTrue(n instanceof StringAnnotation);
    StringAnnotation string = (StringAnnotation) n;
    assertEquals(ROI_ANNOTATION_VALUE, string.getValue());
  }

  private Image makeImage() {
    // Create <Image/>
    Image image = new Image();
    image.setID(IMAGE_ID);
    BooleanAnnotation annotation = new BooleanAnnotation();
    annotation.setID(IMAGE_ANNOTATION_ID);
    annotation.setValue("false");
    image.linkAnnotation(annotation);
    // Create <Pixels/>
    Pixels pixels = new Pixels();
    pixels.setID(PIXELS_ID);
    pixels.setSizeX(SIZE_X);
    pixels.setSizeY(SIZE_Y);
    pixels.setSizeZ(SIZE_Z);
    pixels.setSizeC(SIZE_C);
    pixels.setSizeT(SIZE_T);
    pixels.setDimensionOrder(DIMENSION_ORDER);
    pixels.setType(PIXEL_TYPE);
    pixels.setMetadataOnly(new MetadataOnly());
    // Create <Channel/> under <Pixels/>
    for (int i = 0; i < SIZE_C; i++) {
      Channel channel = new Channel();
      channel.setID("Channel:" + i);
      if (i == 0) {
        XMLAnnotation channelAnnotation = new XMLAnnotation();
        channelAnnotation.setID(CHANNEL_ANNOTATION_ID);
        channelAnnotation.setValue(CHANNEL_ANNOTATION_XML);
        channel.linkAnnotation(channelAnnotation);
      }
      pixels.addChannel(channel);
    }
    // create Annotation for Pixels
    DoubleAnnotation pixelsAnnotation = new DoubleAnnotation();
    pixelsAnnotation.setID(PIXELS_ANNOTATION_ID);
    pixelsAnnotation.setValue("3.14");
    pixels.linkAnnotation(pixelsAnnotation);
    // Put <Pixels/> under <Image/>
    image.setPixels(pixels);
    return image;
  }

  private Instrument makeInstrument() {
    // Create <Instrument/>
    Instrument instrument = new Instrument();
    instrument.setID(INSTRUMENT_ID);
    // Create <Detector/> under <Instrument/>
    Detector detector = new Detector();
    detector.setID(DETECTOR_ID);
    detector.setModel(DETECTOR_MODEL);
    instrument.addDetector(detector);
    // Create <Laser/> under <Instrument/>
    Laser laser = new Laser();
    laser.setID(LIGHTSOURCE_ID);
    laser.setModel(LIGHTSOURCE_MODEL);
    laser.setType(LASER_TYPE);
    laser.setPower(LIGHTSOURCE_POWER);
    instrument.addLightSource(laser);

    // Create <Dichroic/> under <Instrument/>
    Dichroic dichroic = new Dichroic();
    dichroic.setID(DICHROIC_ID);
    dichroic.setSerialNumber(DICHROIC_SN);
    // Create <FilterSet/> under <Dichroic/>
    FilterSet filterSet = new FilterSet();
    filterSet.setID(FILTERSET_ID);
    filterSet.setLotNumber(FILTERSET_LOT);
    filterSet.linkDichroic(dichroic);

    Filter emFilter = new Filter();
    Filter exFilter = new Filter();
    OTF otf = new OTF();
    // Create <Objective/> under <Instrument/>
    Objective objective = new Objective();
    objective.setID(OBJECTIVE_ID);
    objective.setModel(OBJECTIVE_MODEL);

    emFilter.setID(EM_FILTER_ID);
    emFilter.setType(EM_FILTER_TYPE);
    exFilter.setID(EX_FILTER_ID);
    exFilter.setType(EX_FILTER_TYPE);
    otf.setID(OTF_ID);
    otf.setType(OTF_PIXELTYPE);
    otf.setSizeX(OTF_SIZE_X);
    otf.setSizeY(OTF_SIZE_Y);
    otf.setOpticalAxisAveraged(OTF_OPTICAL_AXIS_AVERAGED);
    // Create <ObjectiveSettings/> under <OTF/>
    ObjectiveSettings otfObjectiveSettings = new ObjectiveSettings();
    otfObjectiveSettings.setID(objective.getID());
    otf.setObjectiveSettings(otfObjectiveSettings);
    // Create <BinaryFile/> under <OTF/>
    BinaryFile otfBinaryFile = new BinaryFile();
    otfBinaryFile.setFileName(OTF_BINARY_FILE_NAME);
    otfBinaryFile.setSize(OTF_BINARY_FILE_SIZE);
    External otfBinaryFileExternal = new External();
    otfBinaryFileExternal.sethref(OTF_BINARY_FILE_EXTERNAL_HREF);
    otfBinaryFileExternal.setSHA1(OTF_BINARY_FILE_EXTERNAL_SHA1);
    otfBinaryFile.setExternal(otfBinaryFileExternal);
    otf.setBinaryFile(otfBinaryFile);

    instrument.addFilter(emFilter);
    instrument.addFilter(exFilter);
    instrument.addOTF(otf);
    instrument.addObjective(objective);

    filterSet.linkEmissionFilter(emFilter);
    filterSet.linkExcitationFilter(exFilter);
    filterSet.linkOTF(otf);
    filterSet.linkDichroic(dichroic);
    instrument.addFilterSet(filterSet);
    instrument.addDichroic(dichroic);

    // link Instrument to the first Image
    Image image = ome.getImage(0);
    image.linkInstrument(instrument);

    return instrument;
  }

  private Plate makePlate() {
    Plate plate = new Plate();
    plate.setID(PLATE_ID);
    plate.setRows(WELL_ROWS);
    plate.setColumns(WELL_COLS);
    plate.setRowNamingConvention(WELL_ROW);
    plate.setColumnNamingConvention(WELL_COL);

    TimestampAnnotation plateAnnotation = new TimestampAnnotation();
    plateAnnotation.setID(PLATE_ANNOTATION_ID);
    plateAnnotation.setValue(PLATE_ANNOTATION_VALUE);
    plate.linkAnnotation(plateAnnotation);

    int wellSampleIndex = 0;
    for (int row=0; row<WELL_ROWS; row++) {
      for (int col=0; col<WELL_COLS; col++) {
        Well well = new Well();
        well.setID(String.format("Well:%d_%d", row, col));
        well.setRow(row);
        well.setColumn(col);

        if (row == 0 && col == 0) {
          LongAnnotation annotation = new LongAnnotation();
          annotation.setID(WELL_ANNOTATION_ID);
          annotation.setValue(WELL_ANNOTATION_VALUE);
          well.linkAnnotation(annotation);
        }

        WellSample sample = new WellSample();
        sample.setID(String.format("WellSample:%d_%d", row, col));
        sample.setIndex(wellSampleIndex);
        sample.linkImage(ome.getImage(0));
        well.addWellSample(sample);
        plate.addWell(well);
        wellSampleIndex++;
      }
    }

    return plate;
  }

  private ROI makeROI() {
    ROI roi = new ROI();
    roi.setID(ROI_ID);

    StringAnnotation roiAnnotation = new StringAnnotation();
    roiAnnotation.setID(ROI_ANNOTATION_ID);
    roiAnnotation.setValue(ROI_ANNOTATION_VALUE);
    roi.linkAnnotation(roiAnnotation);

    Union shapeUnion = new Union();
    Rectangle rect = new Rectangle();
    rect.setID(SHAPE_ID);
    rect.setX(RECTANGLE_X);
    rect.setY(RECTANGLE_Y);
    rect.setWidth(RECTANGLE_WIDTH);
    rect.setHeight(RECTANGLE_HEIGHT);

    shapeUnion.addShape(rect);
    roi.setUnion(shapeUnion);

    return roi;
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

  private Document fromString(String xml)
  throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(xml)));
  }

  public static void main(String[] args) throws Exception {
    InOut201004Test t = new InOut201004Test();
    t.setUp();
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
