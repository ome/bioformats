/*
 * Copyright (C) 2012 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package spec.schema.samples;

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
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;

/**
 * This class represents the sample file 2011-06/6x4y1z1t1c1b-swatch-instrument.ome
 * 
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 *
 */
public class Instrument2011_06
{
    /**
     * This inner class holds all the string used by this file and the file location.
     * It is imported into the associated test class
     */
    public static class ref {
        public static final String FILE_LOCATION = "/OmeFiles/2011-06/6x4y1z1t1c8b-swatch-instrument.ome";
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
        public static final PositiveInteger Instrument0Objective0NominalMagnification = PositiveInteger.valueOf("60");
        public static final Double Instrument0Objective0CalibratedMagnification = Double.valueOf("60.12");
        public static final Double Instrument0Objective0WorkingDistance = Double.valueOf("20");
        public static final String Instrument0Objective0Manufacturer = "OME Objectives";
        public static final String Instrument0Objective0Model = "60xUV-Air";
        public static final String Instrument0FilterSet0Manufacturer = "OME Filters";
        public static final String Instrument0FilterSet0Model = "Standard Mk3";
        public static final String Instrument0FilterSet0LotNumber = "Lot174-A";
        public static final String Instrument0Filter0Manufacturer = "OME Filters Inc";
        public static final String Instrument0Filter0Model = "Model1";
        public static final String Instrument0Filter0FilterWheel = "Disc A";
        public static final PositiveInteger Instrument0Filter0TransmittanceRangeCutIn = PositiveInteger.valueOf("350");
        public static final PositiveInteger Instrument0Filter0TransmittanceRangeCutOut = PositiveInteger.valueOf("450");
        public static final NonNegativeInteger Instrument0Filter0TransmittanceRangeCutInTolerance = NonNegativeInteger.valueOf("10");
        public static final NonNegativeInteger Instrument0Filter0TransmittanceRangeCutOutTolerance = NonNegativeInteger.valueOf("20");
        public static final PercentFraction Instrument0Filter0TransmittanceRangeTransmittance = PercentFraction.valueOf("0.3");
        public static final String Instrument0Filter1Manufacturer = "OME Filters Ltd";
        public static final String Instrument0Filter1Model = "Deluxe Mk4";
        public static final FilterType Instrument0Filter1Type = FilterType.BANDPASS;;
        public static final PositiveInteger Instrument0Filter1TransmittanceRangeCutIn = PositiveInteger.valueOf("560");
        public static final PositiveInteger Instrument0Filter1TransmittanceRangeCutOut = PositiveInteger.valueOf("630");
        public static final NonNegativeInteger Instrument0Filter1TransmittanceRangeCutInTolerance = NonNegativeInteger.valueOf("25");
        public static final NonNegativeInteger Instrument0Filter1TransmittanceRangeCutOutTolerance = NonNegativeInteger.valueOf("30");
        public static final PercentFraction Instrument0Filter1TransmittanceRangeTransmittance = PercentFraction.valueOf("0.8");
        public static final String Instrument0Filter2Manufacturer = "OME Filters Asc";
        public static final String Instrument0Filter2Model = "Deluxe Mk5";
        public static final FilterType Instrument0Filter2Type = FilterType.BANDPASS;
        public static final PositiveInteger Instrument0Filter2TransmittanceRangeCutIn = PositiveInteger.valueOf("562");
        public static final PositiveInteger Instrument0Filter2TransmittanceRangeCutOut = PositiveInteger.valueOf("633");
        public static final NonNegativeInteger Instrument0Filter2TransmittanceRangeCutInTolerance = NonNegativeInteger.valueOf("11");
        public static final NonNegativeInteger Instrument0Filter2TransmittanceRangeCutOutTolerance = NonNegativeInteger.valueOf("23");
        public static final PercentFraction Instrument0Filter2TransmittanceRangeTransmittance = PercentFraction.valueOf("0.5");
        public static final String Instrument0Filter3Manufacturer = "OME Filters.Com";
        public static final String Instrument0Filter3Model = "Deluxe Mk6";
        public static final FilterType Instrument0Filter3Type = FilterType.BANDPASS;;
        public static final PositiveInteger Instrument0Filter3TransmittanceRangeCutIn = PositiveInteger.valueOf("463");
        public static final PositiveInteger Instrument0Filter3TransmittanceRangeCutOut = PositiveInteger.valueOf("535");
        public static final NonNegativeInteger Instrument0Filter3TransmittanceRangeCutInTolerance = NonNegativeInteger.valueOf("21");
        public static final NonNegativeInteger Instrument0Filter3TransmittanceRangeCutOutTolerance = NonNegativeInteger.valueOf("34");
        public static final PercentFraction Instrument0Filter3TransmittanceRangeTransmittance = PercentFraction.valueOf("0.7");
        public static final String Instrument0Dichroic0Model = "Standard Mk3" ;
        public static final String Instrument0Dichroic0Manufacturer = "OME Instruments";
        public static final String Image0Name = "6x6x1x8-swatch.tif";
        public static final String Image0AcquiredDate = "2010-02-23T12:51:30";
        public static final String Image0Description = "This image has instrument data";
        public static final DimensionOrder Image0Pixels0_0DimensionOrder = DimensionOrder.XYCZT;
        public static final Double Image0Pixels0_0PhysicalSizeX = Double.valueOf("10000.0");
        public static final Double Image0Pixels0_0PhysicalSizeY = Double.valueOf("10000.0");
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
        public static final PositiveInteger Image0LightSourceSettings0Wavelength = PositiveInteger.valueOf("510");
        public static final Binning Image0DetectorSettings0Binning = Binning.TWOXTWO;
        public static final Double Image0DetectorSettings0Gain = Double.valueOf("1.2");
        public static final Double Image0DetectorSettings0Offset = Double.valueOf("0.7");
        public static final Double Image0DetectorSettings0ReadOutRate = Double.valueOf("3200");
        public static final Double Image0DetectorSettings0Voltage = Double.valueOf("120");
    }
}
