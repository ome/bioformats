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

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.UNITS;
import loci.formats.FormatTools;

import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;

/**
 * This class represents the sample file 2011-06/6x4y1z1t1c1b-swatch-instrument.ome
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 */
public class Instrument2011_06
{
    /**
     * This inner class holds all the string used by this file and the file location.
     * It is imported into the associated test class
     */
    public static class ref {
        public static final String FILE_LOCATION = "/spec/schema/samples/2011-06/6x4y1z1t1c8b-swatch-instrument.ome";
        public static final MicroscopeType Instrument0MicroscopeType = MicroscopeType.INVERTED;
        public static final String Instrument0MicroscopeManufacturer ="OME Instruments";
        public static final String Instrument0MicroscopeModel = "Lab Mk3";
        public static final String Instrument0MicroscopeSerialNumber ="L3-1234";
        public static final Double Instrument0LightSource0Power = Double.valueOf("200");
        public static final String Instrument0LightSource0Manufacturer = "OME Lights Ltd";
        public static final String Instrument0LightSource0Model = "Ruby60";
        public static final String Instrument0LightSource0SerialNumber = "A654321";
        public static final LaserType Instrument0LightSource0LaserType = LaserType.SOLIDSTATE;
        public static final LaserMedium Instrument0LightSource0LaserLaserMedium = LaserMedium.RUBY;
        public static final Double Instrument0LightSource1Power = Double.valueOf("300");
        public static final String Instrument0LightSource1Manufacturer = "OME Lights Inc";
        public static final String Instrument0LightSource1Model = "Arc60";
        public static final String Instrument0LightSource1SerialNumber = "A123456";
        public static final ArcType Instrument0LightSource1ArcType = ArcType.XE;
        public static final DetectorType Instrument0DetectorType = DetectorType.CCD;
        public static final String Instrument0Detector0Manufacturer = "OME Detectors";
        public static final String Instrument0Detector0Model = "Standard CCD Mk2";
        public static final Correction Instrument0Objective0Correction = Correction.UV;
        public static final Immersion Instrument0Objective0Immersion = Immersion.AIR;
        public static final Double Instrument0Objective0LensNA = Double.valueOf("1.2");
        public static final Double Instrument0Objective0NominalMagnification = Double.valueOf("60.0");
        public static final Double Instrument0Objective0CalibratedMagnification = Double.valueOf("60.12");
        public static final Length Instrument0Objective0WorkingDistance = FormatTools.createLength(Double.valueOf("20"), UNITS.MICROMETER);
        public static final String Instrument0Objective0Manufacturer = "OME Objectives";
        public static final String Instrument0Objective0Model = "60xUV-Air";
        public static final String Instrument0FilterSet0Manufacturer = "OME Filters";
        public static final String Instrument0FilterSet0Model = "Standard Mk3";
        public static final String Instrument0FilterSet0LotNumber = "Lot174-A";
        public static final String Instrument0Filter0Manufacturer = "OME Filters Inc";
        public static final String Instrument0Filter0Model = "Model1";
        public static final String Instrument0Filter0FilterWheel = "Disc A";
        public static final Length Instrument0Filter0TransmittanceRangeCutIn = new Length(350.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter0TransmittanceRangeCutOut = new Length(450.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter0TransmittanceRangeCutInTolerance = new Length(10.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter0TransmittanceRangeCutOutTolerance = new Length(20.0, UNITS.NANOMETER);
        public static final PercentFraction Instrument0Filter0TransmittanceRangeTransmittance = PercentFraction.valueOf("0.3");
        public static final String Instrument0Filter1Manufacturer = "OME Filters Ltd";
        public static final String Instrument0Filter1Model = "Deluxe Mk4";
        public static final FilterType Instrument0Filter1Type = FilterType.BANDPASS;
        public static final Length Instrument0Filter1TransmittanceRangeCutIn = new Length(560.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter1TransmittanceRangeCutOut = new Length(630.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter1TransmittanceRangeCutInTolerance = new Length(25.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter1TransmittanceRangeCutOutTolerance = new Length(30.0, UNITS.NANOMETER);
        public static final PercentFraction Instrument0Filter1TransmittanceRangeTransmittance = PercentFraction.valueOf("0.8");
        public static final String Instrument0Filter2Manufacturer = "OME Filters Asc";
        public static final String Instrument0Filter2Model = "Deluxe Mk5";
        public static final FilterType Instrument0Filter2Type = FilterType.BANDPASS;
        public static final Length Instrument0Filter2TransmittanceRangeCutIn = new Length(562.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter2TransmittanceRangeCutOut = new Length(633.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter2TransmittanceRangeCutInTolerance = new Length(11.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter2TransmittanceRangeCutOutTolerance = new Length(23.0, UNITS.NANOMETER);
        public static final PercentFraction Instrument0Filter2TransmittanceRangeTransmittance = PercentFraction.valueOf("0.5");
        public static final String Instrument0Filter3Manufacturer = "OME Filters.Com";
        public static final String Instrument0Filter3Model = "Deluxe Mk6";
        public static final FilterType Instrument0Filter3Type = FilterType.BANDPASS;
        public static final Length Instrument0Filter3TransmittanceRangeCutIn = new Length(463.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter3TransmittanceRangeCutOut = new Length(535.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter3TransmittanceRangeCutInTolerance = new Length(21.0, UNITS.NANOMETER);
        public static final Length Instrument0Filter3TransmittanceRangeCutOutTolerance = new Length(34.0, UNITS.NANOMETER);
        public static final PercentFraction Instrument0Filter3TransmittanceRangeTransmittance = PercentFraction.valueOf("0.7");
        public static final String Instrument0Dichroic0Model = "Standard Mk3" ;
        public static final String Instrument0Dichroic0Manufacturer = "OME Instruments";
        public static final String Image0Name = "6x6x1x8-swatch.tif";
        public static final String Image0AcquiredDate = "2010-02-23T12:51:30";
        public static final String Image0Description = "This image has instrument data";
        public static final DimensionOrder Image0Pixels0_0DimensionOrder = DimensionOrder.XYCZT;
        public static final Length Image0Pixels0_0PhysicalSizeX = FormatTools.createLength(Double.valueOf("10000.0"), UNITS.MICROMETER);
        public static final Length Image0Pixels0_0PhysicalSizeY = FormatTools.createLength(Double.valueOf("10000.0"), UNITS.MICROMETER);
        public static final PixelType Image0Pixels0_0Type = PixelType.UINT8;
        public static final PositiveInteger Image0Pixels0_0SizeC = PositiveInteger.valueOf("1");
        public static final PositiveInteger Image0Pixels0_0SizeT = PositiveInteger.valueOf("1");
        public static final PositiveInteger Image0Pixels0_0SizeX = PositiveInteger.valueOf("6");
        public static final PositiveInteger Image0Pixels0_0SizeY = PositiveInteger.valueOf("4");
        public static final PositiveInteger Image0Pixels0_0SizeZ = PositiveInteger.valueOf("1");
        public static final Color Image0Pixels0_0Channel0Color = Color.valueOf("-2147483648");
        public static final NonNegativeLong Image0Pixels0_0Bindata0Length = NonNegativeLong.valueOf("32");
        public static final Boolean Image0Pixels0_0Bindata0BigEndian = Boolean.FALSE;
        public static final PercentFraction Image0LightSourceSettings0Attenuation = PercentFraction.valueOf("0.8");
        public static final Length Image0LightSourceSettings0Wavelength = FormatTools.createLength(Double.valueOf("510"), UNITS.NANOMETER);
        public static final Binning Image0DetectorSettings0Binning = Binning.TWOBYTWO;
        public static final Double Image0DetectorSettings0Gain = Double.valueOf("1.2");
        public static final Double Image0DetectorSettings0Offset = Double.valueOf("0.7");
        public static final Frequency Image0DetectorSettings0ReadOutRate = new Frequency(3200.0, UNITS.MEGAHERTZ);
        public static final Double Image0DetectorSettings0Voltage = Double.valueOf("120");
    }
}
