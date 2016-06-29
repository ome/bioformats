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

package spec.schema;

import java.io.InputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.services.OMEXMLService;
import ome.xml.model.Arc;
import ome.xml.model.BinData;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.Microscope;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.Pixels;
import ome.xml.model.Point;
import ome.xml.model.TransmittanceRange;

import ome.units.UNITS;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * import the schema resolver so I can use it to locate
 * transforms in the specification jar
 */
import ome.specification.SchemaResolver;

import ome.units.quantity.ElectricPotential;
/**
 * import the reference strings for the associated sample file
 */
import spec.schema.samples.Instrument2011_06.ref;

/**
 * Collections of tests.
 * Checks if the upgrade from 2011-06 schema to 2012-06 schema works for
 * the file 2011-06/6x4y1z1t1c8b-swatch-instrument.ome
 *
 * @author Chris Allan &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:callan@lifesci.dundee.ac.uk">callan@lifesci.dundee.ac.uk</a>
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 */

@Test(groups = { "all" })
public class Schema2011_06_Instrument_Upgrade_Test {
    private static final Templates UPDATE_201106 =
        XMLTools.getStylesheet("/transforms/2011-06-to-2012-06.xsl",
                SchemaResolver.class);

    private OME ome;

    private Image image0;
    private Instrument instrument0;
    private Pixels pixels0;
    private Objective objective0;
    private Channel channel0;
    private BinData bindata0;
    private Detector detector0;
    private Dichroic dichroic0;
    private Filter filter0;
    private Filter filter1;
    private Filter filter2;
    private Filter filter3;
    private FilterSet filterSet0;
    private LightSource lightsource0;
    private Laser laser0;
    private LightSource lightsource1;
    private Arc arc1;

    @BeforeClass
    public void setUp() throws Exception {
        InputStream source = this.getClass().getResourceAsStream(ref.FILE_LOCATION);
        ServiceFactory sf = new ServiceFactory();
        OMEXMLService service = sf.getInstance(OMEXMLService.class);
        String xml = XMLTools.transformXML(
                new StreamSource(source), UPDATE_201106);
        ome = (OME) service.createOMEXMLRoot(xml);
    }

    @Test
    public void testOmeNode() {
        Assert.assertNotNull(ome);
        Assert.assertEquals(0, ome.sizeOfDatasetList());
        Assert.assertEquals(0, ome.sizeOfExperimenterGroupList());
        Assert.assertEquals(0, ome.sizeOfExperimenterList());
        Assert.assertEquals(1, ome.sizeOfImageList());
        Assert.assertEquals(1, ome.sizeOfInstrumentList());
        Assert.assertEquals(0, ome.sizeOfPlateList());
        Assert.assertEquals(0, ome.sizeOfProjectList());
        Assert.assertEquals(0, ome.sizeOfROIList());
        Assert.assertEquals(0, ome.sizeOfScreenList());
        Assert.assertNull(ome.getCreator());
        Assert.assertNull(ome.getUUID());
        Assert.assertNull(ome.getStructuredAnnotations());
    }

    @Test (groups = {"11-06-i-instrument"}, dependsOnMethods = {"testOmeNode"})
    public void testInstrument0() {
        Assert.assertNotNull(ome);
        instrument0 = ome.getInstrument(0);
        Assert.assertNotNull(instrument0);
        Assert.assertEquals(1, instrument0.sizeOfDetectorList());
        Assert.assertEquals(1, instrument0.sizeOfDichroicList());
        Assert.assertEquals(4, instrument0.sizeOfFilterList());
        Assert.assertEquals(1, instrument0.sizeOfFilterSetList());
        Assert.assertEquals(2, instrument0.sizeOfLightSourceList());
        Assert.assertEquals(1, instrument0.sizeOfObjectiveList());
        Microscope microscope0 = instrument0.getMicroscope();
        Assert.assertNotNull(microscope0);
        Assert.assertEquals(ref.Instrument0MicroscopeManufacturer, microscope0.getManufacturer());
        Assert.assertEquals(ref.Instrument0MicroscopeModel, microscope0.getModel());
        Assert.assertEquals(ref.Instrument0MicroscopeSerialNumber, microscope0.getSerialNumber());
        Assert.assertEquals(ref.Instrument0MicroscopeType, microscope0.getType());
    }

    @Test (groups = {"11-06-i-lightsource"}, dependsOnMethods = {"testInstrument0"})
    public void testLightSource0() {
        Assert.assertNotNull(ome);
        lightsource0 = instrument0.getLightSource(1);
        Assert.assertNotNull(lightsource0);
        Assert.assertEquals(ref.Instrument0LightSource0Manufacturer, lightsource0.getManufacturer());
        Assert.assertEquals(ref.Instrument0LightSource0Model, lightsource0.getModel());
        Assert.assertEquals(ref.Instrument0LightSource0SerialNumber, lightsource0.getSerialNumber());
        Assert.assertEquals(ref.Instrument0LightSource0Power, lightsource0.getPower().value(UNITS.MILLIWATT).doubleValue());
        Assert.assertEquals(Laser.class.getName(), lightsource0.getClass().getName());
        laser0 = (Laser) lightsource0;
        Assert.assertEquals(ref.Instrument0LightSource0LaserType, laser0.getType());
        Assert.assertEquals(ref.Instrument0LightSource0LaserLaserMedium, laser0.getLaserMedium());
        Assert.assertNull(laser0.getFrequencyMultiplication());
        Assert.assertNull(laser0.getPockelCell());
        Assert.assertNull(laser0.getPulse());
        Assert.assertNull(laser0.getRepetitionRate());
        Assert.assertNull(laser0.getTuneable());
        Assert.assertNull(laser0.getWavelength());
    }

    @Test (groups = {"11-06-i-lightsource"}, dependsOnMethods = {"testInstrument0"})
    public void testLightSource1() {
        Assert.assertNotNull(ome);
        lightsource1 = instrument0.getLightSource(0);
        Assert.assertNotNull(lightsource1);
        Assert.assertEquals(ref.Instrument0LightSource1Manufacturer, lightsource1.getManufacturer());
        Assert.assertEquals(ref.Instrument0LightSource1Model, lightsource1.getModel());
        Assert.assertEquals(ref.Instrument0LightSource1SerialNumber, lightsource1.getSerialNumber());
        Assert.assertEquals(ref.Instrument0LightSource1Power, lightsource1.getPower().value(UNITS.MILLIWATT).doubleValue());
        Assert.assertEquals(Arc.class.getName(), lightsource1.getClass().getName());
        arc1 = (Arc) lightsource1;
        Assert.assertEquals(ref.Instrument0LightSource1ArcType, arc1.getType());
    }

    @Test (groups = {"11-06-i-lightsourcelinks"}, dependsOnGroups = {"11-06-i-lightsource"})
    public void testLaser0Pump() {
        Assert.assertNotNull(laser0);
        Assert.assertEquals(arc1, laser0.getLinkedPump());
    }

    @Test (groups = {"11-06-i-filterset"}, dependsOnGroups = {"11-06-i-filter", "11-06-i-dichroic"})
    public void testFilterSet0() {
        Assert.assertNotNull(ome);
        filterSet0 = instrument0.getFilterSet(0);
        Assert.assertNotNull(filterSet0);
        Assert.assertEquals(ref.Instrument0FilterSet0Manufacturer, filterSet0.getManufacturer());
        Assert.assertEquals(ref.Instrument0FilterSet0Model, filterSet0.getModel());
        Assert.assertEquals(ref.Instrument0FilterSet0LotNumber, filterSet0.getLotNumber());
        Assert.assertNull(filterSet0.getSerialNumber());
        Assert.assertEquals(1, filterSet0.sizeOfLinkedExcitationFilterList());
        Assert.assertEquals(filter0, filterSet0.getLinkedExcitationFilter(0));
        Assert.assertEquals(dichroic0, filterSet0.getLinkedDichroic());
        Assert.assertEquals(1, filterSet0.sizeOfLinkedEmissionFilterList());
        Assert.assertEquals(filter1, filterSet0.getLinkedEmissionFilter(0));
    }


    @Test (groups = {"11-06-i-filter"}, dependsOnMethods = {"testInstrument0"})
    public void testFilter0() {
        Assert.assertNotNull(ome);
        filter0 = instrument0.getFilter(0);
        Assert.assertNotNull(filter0);
        Assert.assertEquals(ref.Instrument0Filter0Manufacturer, filter0.getManufacturer());
        Assert.assertEquals(ref.Instrument0Filter0Model, filter0.getModel());
        Assert.assertEquals(ref.Instrument0Filter0FilterWheel, filter0.getFilterWheel());
        Assert.assertNull(filter0.getType());
        Assert.assertNull(filter0.getLotNumber());
        Assert.assertNull(filter0.getSerialNumber());
        TransmittanceRange transmittanceRange = filter0.getTransmittanceRange();
        Assert.assertNotNull(transmittanceRange);
        Assert.assertEquals(ref.Instrument0Filter0TransmittanceRangeCutIn, transmittanceRange.getCutIn());
        Assert.assertEquals(ref.Instrument0Filter0TransmittanceRangeCutInTolerance, transmittanceRange.getCutInTolerance());
        Assert.assertEquals(ref.Instrument0Filter0TransmittanceRangeCutOut, transmittanceRange.getCutOut());
        Assert.assertEquals(ref.Instrument0Filter0TransmittanceRangeCutOutTolerance, transmittanceRange.getCutOutTolerance());
        Assert.assertEquals(ref.Instrument0Filter0TransmittanceRangeTransmittance, transmittanceRange.getTransmittance());
    }

    @Test (groups = {"11-06-i-filter"}, dependsOnMethods = {"testInstrument0"})
    public void testFilter1() {
        Assert.assertNotNull(ome);
        filter1 = instrument0.getFilter(1);
        Assert.assertNotNull(filter1);
        Assert.assertEquals(ref.Instrument0Filter1Manufacturer, filter1.getManufacturer());
        Assert.assertEquals(ref.Instrument0Filter1Model, filter1.getModel());
        Assert.assertNull(filter1.getFilterWheel());
        Assert.assertEquals(ref.Instrument0Filter1Type, filter1.getType());
        Assert.assertNull(filter1.getLotNumber());
        Assert.assertNull(filter1.getSerialNumber());
        TransmittanceRange transmittanceRange = filter1.getTransmittanceRange();
        Assert.assertNotNull(transmittanceRange);
        Assert.assertEquals(ref.Instrument0Filter1TransmittanceRangeCutIn, transmittanceRange.getCutIn());
        Assert.assertEquals(ref.Instrument0Filter1TransmittanceRangeCutInTolerance, transmittanceRange.getCutInTolerance());
        Assert.assertEquals(ref.Instrument0Filter1TransmittanceRangeCutOut, transmittanceRange.getCutOut());
        Assert.assertEquals(ref.Instrument0Filter1TransmittanceRangeCutOutTolerance, transmittanceRange.getCutOutTolerance());
        Assert.assertEquals(ref.Instrument0Filter1TransmittanceRangeTransmittance, transmittanceRange.getTransmittance());
    }

    @Test (groups = {"11-06-i-filter"}, dependsOnMethods = {"testInstrument0"})
    public void testFilter2() {
        Assert.assertNotNull(ome);
        filter2 = instrument0.getFilter(2);
        Assert.assertNotNull(filter2);
        Assert.assertEquals(ref.Instrument0Filter2Manufacturer, filter2.getManufacturer());
        Assert.assertEquals(ref.Instrument0Filter2Model, filter2.getModel());
        Assert.assertNull(filter2.getFilterWheel());
        Assert.assertEquals(ref.Instrument0Filter2Type, filter2.getType());
        Assert.assertNull(filter2.getLotNumber());
        Assert.assertNull(filter2.getSerialNumber());
        TransmittanceRange transmittanceRange = filter2.getTransmittanceRange();
        Assert.assertNotNull(transmittanceRange);
        Assert.assertEquals(ref.Instrument0Filter2TransmittanceRangeCutIn, transmittanceRange.getCutIn());
        Assert.assertEquals(ref.Instrument0Filter2TransmittanceRangeCutInTolerance, transmittanceRange.getCutInTolerance());
        Assert.assertEquals(ref.Instrument0Filter2TransmittanceRangeCutOut, transmittanceRange.getCutOut());
        Assert.assertEquals(ref.Instrument0Filter2TransmittanceRangeCutOutTolerance, transmittanceRange.getCutOutTolerance());
        Assert.assertEquals(ref.Instrument0Filter2TransmittanceRangeTransmittance, transmittanceRange.getTransmittance());
    }

    @Test (groups = {"11-06-i-filter"}, dependsOnMethods = {"testInstrument0"})
    public void testFilter3() {
        Assert.assertNotNull(ome);
        filter3 = instrument0.getFilter(3);
        Assert.assertNotNull(filter3);
        Assert.assertEquals(ref.Instrument0Filter3Manufacturer, filter3.getManufacturer());
        Assert.assertEquals(ref.Instrument0Filter3Model, filter3.getModel());
        Assert.assertNull(filter3.getFilterWheel());
        Assert.assertEquals(ref.Instrument0Filter3Type, filter3.getType());
        Assert.assertNull(filter3.getLotNumber());
        Assert.assertNull(filter3.getSerialNumber());
        TransmittanceRange transmittanceRange = filter3.getTransmittanceRange();
        Assert.assertNotNull(transmittanceRange);
        Assert.assertEquals(ref.Instrument0Filter3TransmittanceRangeCutIn, transmittanceRange.getCutIn());
        Assert.assertEquals(ref.Instrument0Filter3TransmittanceRangeCutInTolerance, transmittanceRange.getCutInTolerance());
        Assert.assertEquals(ref.Instrument0Filter3TransmittanceRangeCutOut, transmittanceRange.getCutOut());
        Assert.assertEquals(ref.Instrument0Filter3TransmittanceRangeCutOutTolerance, transmittanceRange.getCutOutTolerance());
        Assert.assertEquals(ref.Instrument0Filter3TransmittanceRangeTransmittance, transmittanceRange.getTransmittance());
    }

    @Test (groups = {"11-06-i-dichroic"}, dependsOnMethods = {"testInstrument0"})
    public void testDichroic0() {
        Assert.assertNotNull(ome);
        dichroic0 = instrument0.getDichroic(0);
        Assert.assertNotNull(dichroic0);
        Assert.assertEquals(ref.Instrument0Dichroic0Manufacturer, dichroic0.getManufacturer());
        Assert.assertEquals(ref.Instrument0Dichroic0Model, dichroic0.getModel());
        Assert.assertNull(dichroic0.getLotNumber());
        Assert.assertNull(dichroic0.getSerialNumber());
    }

    @Test (groups = {"11-06-i-detector"}, dependsOnMethods = {"testInstrument0"})
    public void testDetector0() {
        Assert.assertNotNull(ome);
        detector0 = instrument0.getDetector(0);
        Assert.assertNotNull(detector0);
        Assert.assertEquals(ref.Instrument0Detector0Manufacturer, detector0.getManufacturer());
        Assert.assertEquals(ref.Instrument0Detector0Model, detector0.getModel());
        Assert.assertEquals(ref.Instrument0DetectorType, detector0.getType());
        Assert.assertNull(detector0.getAmplificationGain());
        Assert.assertNull(detector0.getGain());
        Assert.assertNull(detector0.getLotNumber());
        Assert.assertNull(detector0.getOffset());
        Assert.assertNull(detector0.getSerialNumber());
        Assert.assertNull(detector0.getVoltage());
        Assert.assertNull(detector0.getZoom());
    }

    @Test (groups = {"11-06-i-objective"}, dependsOnMethods = {"testInstrument0"})
    public void testObjective0() {
        Assert.assertNotNull(ome);
        objective0 = instrument0.getObjective(0);
        Assert.assertNotNull(objective0);
        Assert.assertEquals(ref.Instrument0Objective0CalibratedMagnification, objective0.getCalibratedMagnification());
        Assert.assertEquals(ref.Instrument0Objective0Manufacturer, objective0.getManufacturer());
        Assert.assertEquals(ref.Instrument0Objective0NominalMagnification, objective0.getNominalMagnification());
        Assert.assertEquals(ref.Instrument0Objective0Correction, objective0.getCorrection());
        Assert.assertEquals(ref.Instrument0Objective0Immersion, objective0.getImmersion());
        Assert.assertNull(objective0.getIris());
        Assert.assertEquals(ref.Instrument0Objective0LensNA, objective0.getLensNA());
        Assert.assertEquals(ref.Instrument0Objective0Model, objective0.getModel());
        Assert.assertNull(objective0.getSerialNumber());
        Assert.assertEquals(ref.Instrument0Objective0WorkingDistance, objective0.getWorkingDistance());
    }

    @Test (groups = {"11-06-i-image"}, dependsOnMethods = {"testOmeNode"})
    public void testImage0Name() {
        Assert.assertNotNull(ome);
        Assert.assertEquals(1, ome.sizeOfImageList());
        image0 = ome.getImage(0);
        Assert.assertNotNull(image0);
        Assert.assertEquals(ref.Image0Name, image0.getName());
    }

    @Test (groups = {"11-06-i-image"}, dependsOnMethods = {"testImage0Name"})
    public void testImage0Values() {
        Assert.assertNotNull(image0);
        Assert.assertEquals(ref.Image0AcquiredDate, image0.getAcquisitionDate());
        Assert.assertEquals(ref.Image0Description, image0.getDescription());
    }

    @Test (groups = {"11-06-i-image"}, dependsOnMethods = {"testImage0Name"})
    public void testPixels0() {
        Assert.assertNotNull(image0);
        pixels0 = image0.getPixels();
        Assert.assertEquals(1, pixels0.sizeOfBinDataList());
        Assert.assertEquals(1, pixels0.sizeOfChannelList());
        Assert.assertEquals(0, pixels0.sizeOfPlaneList());
        Assert.assertEquals(0, pixels0.sizeOfTiffDataList());
        Assert.assertEquals(ref.Image0Pixels0_0DimensionOrder, pixels0.getDimensionOrder());
        Assert.assertEquals(ref.Image0Pixels0_0PhysicalSizeX, pixels0.getPhysicalSizeX());
        Assert.assertEquals(ref.Image0Pixels0_0PhysicalSizeY, pixels0.getPhysicalSizeY());
        Assert.assertEquals(ref.Image0Pixels0_0Type, pixels0.getType());
        Assert.assertEquals(ref.Image0Pixels0_0SizeC, pixels0.getSizeC());
        Assert.assertEquals(ref.Image0Pixels0_0SizeT, pixels0.getSizeT());
        Assert.assertEquals(ref.Image0Pixels0_0SizeX, pixels0.getSizeX());
        Assert.assertEquals(ref.Image0Pixels0_0SizeY, pixels0.getSizeY());
        Assert.assertEquals(ref.Image0Pixels0_0SizeZ, pixels0.getSizeZ());
        Assert.assertNull(pixels0.getMetadataOnly());
        Assert.assertNull(pixels0.getPhysicalSizeZ());
        Assert.assertNull(pixels0.getTimeIncrement());
    }

    @Test (groups = {"11-06-i-image"}, dependsOnMethods = {"testPixels0"})
    public void testChannel0() {
        Assert.assertNotNull(pixels0);
        channel0 = pixels0.getChannel(0);
        Assert.assertEquals(ref.Image0Pixels0_0Channel0Color, channel0.getColor());
        Assert.assertNull(channel0.getContrastMethod());
        Assert.assertNull(channel0.getEmissionWavelength());
        Assert.assertNull(channel0.getExcitationWavelength());
        Assert.assertNull(channel0.getFluor());
        Assert.assertNull(channel0.getIlluminationType());
        Assert.assertNull(channel0.getName());
        Assert.assertNull(channel0.getNDFilter());
        Assert.assertNull(channel0.getPinholeSize());
        Assert.assertNull(channel0.getPockelCellSetting());
        Assert.assertNull(channel0.getSamplesPerPixel());
        Assert.assertNull(channel0.getAcquisitionMode());
        Assert.assertNotNull(channel0.getLightPath());
        Assert.assertNotNull(channel0.getLightSourceSettings());
        Assert.assertNotNull(channel0.getDetectorSettings());
    }

    @Test (groups = {"11-06-i-image"}, dependsOnMethods = {"testPixels0"})
    public void testBinData0() {
        Assert.assertNotNull(pixels0);
        bindata0 = pixels0.getBinData(0);
        Assert.assertEquals(ref.Image0Pixels0_0Bindata0Length, bindata0.getLength());
        Assert.assertEquals(ref.Image0Pixels0_0Bindata0BigEndian, bindata0.getBigEndian());
        Assert.assertNull(bindata0.getCompression());
    }

    @Test (groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-instrument", "11-06-i-lightsourcelinks", "11-06-i-filterset", "11-06-i-detector"})
    public void testImage0Linkage() {
        Assert.assertNotNull(image0);
        Assert.assertEquals(0, image0.sizeOfLinkedAnnotationList());
        Assert.assertEquals(0, image0.sizeOfLinkedROIList());
        Assert.assertEquals(instrument0, image0.getLinkedInstrument());
     }

    @Test (groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-lightsource"})
    public void testChannel0ToLightsourceSettings() {
        Assert.assertNotNull(channel0);
        LightSourceSettings lightsourceSettings = channel0.getLightSourceSettings();
        Assert.assertNotNull(lightsourceSettings);
        Assert.assertEquals(ref.Image0LightSourceSettings0Attenuation, lightsourceSettings.getAttenuation());
        Assert.assertEquals(ref.Image0LightSourceSettings0Wavelength, lightsourceSettings.getWavelength());
        Assert.assertNull(lightsourceSettings.getMicrobeamManipulation());
    }

    @Test (enabled=false, groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-lightsource"})
    public void testChannel0ToLightsourceLinkage() {
        Assert.assertNotNull(channel0);
        LightSourceSettings lightsourceSettings = channel0.getLightSourceSettings();
        Assert.assertNotNull(lightsourceSettings);
        Assert.assertNotNull(lightsourceSettings.getLightSource());
        Assert.assertEquals(lightsource0, lightsourceSettings.getLightSource());
    }

    @Test (groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-filter"})
    public void testChannel0LightpathLinkage() {
        Assert.assertNotNull(channel0);
        LightPath lightpath = channel0.getLightPath();
        Assert.assertNotNull(lightpath);
        Assert.assertEquals(1, lightpath.sizeOfLinkedExcitationFilterList());
        Assert.assertEquals(filter3, lightpath.getLinkedExcitationFilter(0));
        Assert.assertNull(lightpath.getLinkedDichroic());
        Assert.assertEquals(1, lightpath.sizeOfLinkedEmissionFilterList());
        Assert.assertEquals(filter2, lightpath.getLinkedEmissionFilter(0));
    }

    @Test (groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-detector"})
    public void testChannel0DetectorSettings() {
        Assert.assertNotNull(channel0);
        DetectorSettings detectorSettings = channel0.getDetectorSettings();
        Assert.assertNotNull(detectorSettings);
        Assert.assertEquals(ref.Image0DetectorSettings0Binning, detectorSettings.getBinning());
        Assert.assertEquals(ref.Image0DetectorSettings0Gain, detectorSettings.getGain());
        Assert.assertEquals(ref.Image0DetectorSettings0Offset, detectorSettings.getOffset());
        Assert.assertEquals(ref.Image0DetectorSettings0ReadOutRate, detectorSettings.getReadOutRate());
        Assert.assertEquals(ref.Image0DetectorSettings0Voltage, detectorSettings.getVoltage().value());
    }

    @Test (enabled=false, groups = {"11-06-i-links"}, dependsOnGroups = {"11-06-i-image", "11-06-i-detector"})
    public void testChannel0ToDetectorLinkage() {
        Assert.assertNotNull(channel0);
        DetectorSettings detectorSettings = channel0.getDetectorSettings();
        Assert.assertNotNull(detectorSettings);
        Assert.assertNotNull(detectorSettings.getDetector());
        Assert.assertEquals(detector0, detectorSettings.getDetector());
    }

}
