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

package spec.schema.samples;

import ome.units.quantity.Length;
import ome.units.UNITS;
import loci.formats.FormatTools;

import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.FillRule;
import ome.xml.model.enums.FontFamily;
import ome.xml.model.enums.FontStyle;
import ome.xml.model.enums.Marker;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PositiveInteger;

/**
 * This class represents the sample file 2011-06/6x4y1z1t3c8b-swatch-upgrade.ome
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 */
public class Upgrade2011_06
{
    /**
     * This inner class holds all the string used by this file and the file location.
     * It is imported into the associated test class
     */
    public static class ref {
        public static final String FILE_LOCATION = "/spec/schema/samples/2011-06/6x4y1z1t3c8b-swatch-upgrade.ome";
        public static final String Plate0Description = "Plate 0 Description";
        public static final NonNegativeInteger Plate0Well0Column = NonNegativeInteger.valueOf("1");
        public static final NonNegativeInteger Plate0Well0Row = NonNegativeInteger.valueOf("1");
        public static final String Plate0Well0Status = "TheWell0Status";
        public static final Color Plate0Well0Color = Color.valueOf("-2147483648");
        public static final NonNegativeInteger Plate0Well0WellSample0Index = NonNegativeInteger.valueOf("1");
        public static final String Plate0PlateAcquisition0Description = "Plate Acquisition 0 Description";
        public static final String Screen0Name = "ScreenName0";
        public static final String Screen0ProtocolDescription = "Protocol Description Test0";
        public static final String Screen0ProtocolIdentifier = "ProtocolTest0ID";
        public static final String Screen0ReagentSetDescription = "Reagents Set XYZ";
        public static final String Screen0ReagentSetIdentifier = "ReagentsXYZ-ID";
        public static final String Screen0Type = "ScreenType0";
        public static final String Screen0Description = "Screen 0 Description";
        public static final String Screen1Name = "ScreenName1";
        public static final String Screen1ProtocolDescription = "Protocol Description Test1";
        public static final String Screen1ProtocolIdentifier = "ProtocolTest1ID";
        public static final String Screen1ReagentSetDescription = "Reagents Set PQR";
        public static final String Screen1ReagentSetIdentifier = "ReagentsPQR-ID";
        public static final String Screen1Type = "ScreenType1";
        public static final String Screen1Description = "Screen 1 Description";
        public static final String Screen2Name = "ScreenName2";
        public static final String Screen2ProtocolDescription = "Protocol Description Test2";
        public static final String Screen2ProtocolIdentifier = "ProtocolTest2ID";
        public static final String Screen2ReagentSetDescription = "Reagents Set IJK";
        public static final String Screen2ReagentSetIdentifier = "ReagentsIJK-ID";
        public static final String Screen2Type = "ScreenType2";
        public static final String Screen2Description = "Screen 2 Description";
        public static final String Screen3Name = "ScreenName3";
        public static final String Screen3ProtocolDescription = "Protocol Description Test3";
        public static final String Screen3ProtocolIdentifier = "ProtocolTest3ID";
        public static final String Screen3ReagentSetDescription = "Reagents Set AJP";
        public static final String Screen3ReagentSetIdentifier = "ReagentsAJP-ID";
        public static final String Screen3Type = "ScreenType3";
        public static final String Screen3Description = "Screen 3 Description";
        public static final String Experimenter0DisplayName = "Joe Bloggs";
        public static final String Experimenter1DisplayName = "John Smith";
        public static final String Experimenter1FirstName = "John";
        public static final String Experimenter1MiddleName = "Andrew";
        public static final String Experimenter1LastName = "Smith";
        public static final String Experimenter1Email = "john@example.org";
        public static final String Experimenter2DisplayName = "Jane Bloggs";
        public static final String Experimenter3DisplayName = "Jack Bloggs";
        public static final String Experimenter4DisplayName = "Julian Bloggs";
        public static final String Experimenter5DisplayName = "Jeffery Bloggs";
        public static final String Experimenter6DisplayName = "Jake Bloggs";
        public static final String Group0Name = "MyGroup - Leader+Contact match";
        public static final String Group0Description = "A description for my group 0.\n"+
        "\t\t\tComplete with basic formatting, like new lines.";
        public static final String Group1Name = "MyOtherGroup";
        public static final String Group1Description = "A description for my group 1.\n"+
        "\t\t\tComplete with basic formatting, like new lines.";
        public static final String Group2Name = "NoMatch";
        public static final String Group2Description = "A description for my group 2.\n"+
        "\t\t\tComplete with basic formatting, like new lines.";
        public static final String Group3Name = "LeaderMatch";
        public static final String Group3Description = "A description for my group 3.\n"+
        "\t\t\tComplete with basic formatting, like new lines.";
        public static final String Group4Name = "ContactMatch";
        public static final String Group4Description = "A description for my group 4.\n"+
        "\t\t\tComplete with basic formatting, like new lines.";
        public static final String Instrument0Objective0LotNumber = "123";
        public static final String Instrument0Objective0Manufacturer = "OME-Labs";
        public static final Double Instrument0Objective0NominalMagnification = Double.valueOf("20.0");
        public static final Double Instrument0Objective0CalibratedMagnification = Double.valueOf("20.34");
        public static final String Instrument0Objective0OTF1Type = "bit";
        public static final String Instrument0Objective0OTF1OpticalAxisAveraged = "true";
        public static final String Instrument0Objective0OTF1SizeX = "1";
        public static final String Instrument0Objective0OTF1SizeY = "1";
        public static final String Instrument0Objective0OTF1FileName = "dummy.dat";
        public static final String Image0Name = "6x6x1x8-swatch.tif";
        public static final String Image0AcquiredDate = "2010-02-23T12:51:30";
        public static final DimensionOrder Image0Pixels0_0DimensionOrder = DimensionOrder.XYCZT;
        public static final Length Image0Pixels0_0PhysicalSizeX = FormatTools.createLength(Double.valueOf("10000.0"), UNITS.MICROMETER);
        public static final Length Image0Pixels0_0PhysicalSizeY = FormatTools.createLength(Double.valueOf("10000.0"), UNITS.MICROMETER);
        public static final PixelType Image0Pixels0_0Type = PixelType.UINT8;
        public static final PositiveInteger Image0Pixels0_0SizeC = PositiveInteger.valueOf("3");
        public static final PositiveInteger Image0Pixels0_0SizeT = PositiveInteger.valueOf("1");
        public static final PositiveInteger Image0Pixels0_0SizeX = PositiveInteger.valueOf("6");
        public static final PositiveInteger Image0Pixels0_0SizeY = PositiveInteger.valueOf("4");
        public static final PositiveInteger Image0Pixels0_0SizeZ = PositiveInteger.valueOf("1");
        public static final AcquisitionMode Image0Pixels0_0Channel0AcquisitionMode = AcquisitionMode.LASERSCANNINGCONFOCALMICROSCOPY;
        public static final Color Image0Pixels0_0Channel0Color = Color.valueOf("-1147483648");
        public static final AcquisitionMode Image0Pixels0_0Channel1AcquisitionMode = AcquisitionMode.LASERSCANNINGCONFOCALMICROSCOPY;
        public static final Color Image0Pixels0_0Channel1Color = Color.valueOf("-1474836488");
        public static final AcquisitionMode Image0Pixels0_0Channel2AcquisitionMode = AcquisitionMode.MULTIPHOTONMICROSCOPY;
        public static final Color Image0Pixels0_0Channel2Color = Color.valueOf("-2144364811");
        public static final NonNegativeLong Image0Pixels0_0Bindata0Length = NonNegativeLong.valueOf("32");
        public static final Boolean Image0Pixels0_0Bindata0BigEndian = Boolean.FALSE;
        public static final NonNegativeLong Image0Pixels0_0Bindata1Length = NonNegativeLong.valueOf("32");
        public static final Boolean Image0Pixels0_0Bindata1BigEndian = Boolean.FALSE;
        public static final NonNegativeLong Image0Pixels0_0Bindata2Length = NonNegativeLong.valueOf("32");
        public static final Boolean Image0Pixels0_0Bindata2BigEndian = Boolean.FALSE;
        public static final String Annotation1Value = "<test1/>";
        public static final String Annotation2Value = "<test2/>";
        public static final NonNegativeInteger ROI0Shape0TheC = NonNegativeInteger.valueOf("0");
        public static final Double ROI0Shape0PointX = Double.valueOf("1");
        public static final Double ROI0Shape0PointY = Double.valueOf("1");
        public static final Color ROI1Shape1Fill = Color.valueOf("1");
        public static final FillRule ROI1Shape1FillRule = FillRule.NONZERO;
        public static final FontFamily ROI1Shape1FontFamily = FontFamily.SANSSERIF;
        public static final Length ROI1Shape1FontSize = new Length(1, UNITS.POINT);
        public static final FontStyle ROI1Shape1FontStyle = FontStyle.BOLD;
        public static final Marker ROI1Shape1MarkerStart = Marker.ARROW;
        public static final Marker ROI1Shape1MarkerEnd = Marker.ARROW;
        public static final Color ROI1Shape1Stroke = Color.valueOf("1");
        public static final String ROI1Shape1StrokeDashArray = "1";
        public static final Length ROI1Shape1StrokeWidth = FormatTools.createLength(Double.valueOf("1"), UNITS.PIXEL);
        public static final String ROI1Shape1Label = "Hello";
        public static final NonNegativeInteger ROI1Shape1TheC = NonNegativeInteger.valueOf("2");
        public static final Double ROI1Shape1PointX = Double.valueOf("1");
        public static final Double ROI1Shape1PointY = Double.valueOf("1");
        public static final String ROI1Shape1Description = "Shape 1 Not upgradable description.";
        public static final String ROI1Description = "ROI 1 Upgradable description.";
        public static final FillRule ROI2Shape2FillRule = FillRule.EVENODD;
        public static final String ROI2Shape2Label = "Hi There! (from shape 2)";
        public static final String ROI2Shape2Transform = "10, 20, 30, 40, 50, 60";
        public static final Double ROI2Shape2TransformA00 = Double.valueOf("10");
        public static final Double ROI2Shape2TransformA01 = Double.valueOf("30");
        public static final Double ROI2Shape2TransformA02 = Double.valueOf("50");
        public static final Double ROI2Shape2TransformA10 = Double.valueOf("20");
        public static final Double ROI2Shape2TransformA11 = Double.valueOf("40");
        public static final Double ROI2Shape2TransformA12 = Double.valueOf("60");
        public static final Double ROI2Shape2RectangleX = Double.valueOf("1");
        public static final Double ROI2Shape2RectangleY = Double.valueOf("2");
        public static final Double ROI2Shape2RectangleWidth = Double.valueOf("3");
        public static final Double ROI2Shape2RectangleHeight = Double.valueOf("4");
        public static final FillRule ROI2Shape3FillRule = FillRule.EVENODD;
        public static final FontStyle ROI2Shape3FontStyle = FontStyle.NORMAL;
        public static final FontFamily ROI2Shape3FontFamily = FontFamily.SERIF;
        public static final String ROI2Shape3Name = "Name of shape 3";
        public static final String ROI2Shape3Label = "Hello World Label!(from shape 3)";
        public static final Marker ROI2Shape3MarkerStart = Marker.ARROW;
        public static final Marker ROI2Shape3MarkerEnd = Marker.ARROW;
        public static final Double ROI2Shape3TextX = Double.valueOf("1");
        public static final Double ROI2Shape3TextY = Double.valueOf("1");
        public static final String ROI2Shape3TextValue = "Hello World Text Value!(from shape 3)";
        public static final Marker ROI2Shape4MarkerStart = Marker.ARROW;
        public static final Marker ROI2Shape4MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape4Stroke = Color.valueOf("15");
        public static final Length ROI2Shape4StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI2Shape4PolylinePoints = "1,1 10,20, 20,20 20,10";
        public static final String ROI2Shape4PolylineClosed = "true";
        public static final Marker ROI2Shape5MarkerStart = Marker.ARROW;
        public static final Marker ROI2Shape5MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape5Stroke = Color.valueOf("16");
        public static final Length ROI2Shape5StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI2Shape5PolylinePoints = "15,15 15,25, 25,25 25,15";
        public static final String ROI2Shape5PolylineClosed = "false";
        public static final Marker ROI2Shape6MarkerStart = Marker.ARROW;
        public static final Color ROI2Shape6Stroke = Color.valueOf("161");
        public static final Length ROI2Shape6StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI2Shape6PolylinePoints = "1.1,1.1 10.1,20.1, 20.1,20.1 20.1,10.1";
        public static final String ROI2Shape6PolylineClosed = "false";
        public static final Marker ROI2Shape7MarkerStart = Marker.ARROW;
        public static final Marker ROI2Shape7MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape7Stroke = Color.valueOf("17");
        public static final Length ROI2Shape7StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final Double ROI2Shape7LineX1 = Double.valueOf("1.7");
        public static final Double ROI2Shape7LineY1 = Double.valueOf("2.7");
        public static final Double ROI2Shape7LineX2 = Double.valueOf("3.7");
        public static final Double ROI2Shape7LineY2 = Double.valueOf("4.7");
        public static final Marker ROI2Shape8MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape8Stroke = Color.valueOf("171");
        public static final Length ROI2Shape8StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final Double ROI2Shape8LineX1 = Double.valueOf("1.71");
        public static final Double ROI2Shape8LineY1 = Double.valueOf("2.71");
        public static final Double ROI2Shape8LineX2 = Double.valueOf("3.71");
        public static final Double ROI2Shape8LineY2 = Double.valueOf("4.71");
        public static final Marker ROI2Shape9MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape9Stroke = Color.valueOf("172");
        public static final Length ROI2Shape9StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final Double ROI2Shape9LineX1 = Double.valueOf("1.72");
        public static final Double ROI2Shape9LineY1 = Double.valueOf("2.72");
        public static final Double ROI2Shape9LineX2 = Double.valueOf("3.72");
        public static final Double ROI2Shape9LineY2 = Double.valueOf("4.72");
        public static final Marker ROI2Shape10MarkerStart = Marker.ARROW;
        public static final Marker ROI2Shape10MarkerEnd = Marker.ARROW;
        public static final Color ROI2Shape10Stroke = Color.valueOf("18");
        public static final Length ROI2Shape10StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI2Shape10PathDefinition = "M1,1 L2,2";
        public static final Marker ROI3Shape11MarkerStart = Marker.ARROW;
        public static final Marker ROI3Shape11MarkerEnd = Marker.ARROW;
        public static final Color ROI3Shape11Stroke = Color.valueOf("19");
        public static final Length ROI3Shape11StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI3Shape11PathDefinition = "M1.9,1.9 L2.9,2.9";
        public static final Marker ROI4Shape12MarkerStart = Marker.ARROW;
        public static final Marker ROI4Shape12MarkerEnd = Marker.ARROW;
        public static final Color ROI4Shape12Stroke = Color.valueOf("110");
        public static final Length ROI4Shape12StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI4Shape12PathDefinition = "M1.1,1.1 L2.1,2.1";
        public static final Marker ROI4Shape13MarkerStart = Marker.ARROW;
        public static final Marker ROI4Shape13MarkerEnd = Marker.ARROW;
        public static final Color ROI4Shape13Stroke = Color.valueOf("111");
        public static final Length ROI4Shape13StrokeWidth = FormatTools.createLength(Double.valueOf("2"), UNITS.PIXEL);
        public static final String ROI4Shape13PathDefinition = "M1.11,1.11 L2.11,2.11";
    }
}
