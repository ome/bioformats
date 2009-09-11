// **********************************************************************
//
// Copyright (c) 2003-2008 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.0

package loci.ice.formats;

public abstract class _MetadataStoreDisp extends Ice.ObjectImpl implements MetadataStore
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::formats::MetadataStore"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final void
    createRoot()
    {
        createRoot(null);
    }

    public final String
    getOMEXML()
    {
        return getOMEXML(null);
    }

    public final MetadataStore
    getServant()
    {
        return getServant(null);
    }

    public final void
    setArcType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setArcType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleR(r, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setContactExperimenter(String experimenter, int groupIndex)
    {
        setContactExperimenter(experimenter, groupIndex, null);
    }

    public final void
    setDatasetDescription(String description, int datasetIndex)
    {
        setDatasetDescription(description, datasetIndex, null);
    }

    public final void
    setDatasetExperimenterRef(String experimenterRef, int datasetIndex)
    {
        setDatasetExperimenterRef(experimenterRef, datasetIndex, null);
    }

    public final void
    setDatasetGroupRef(String groupRef, int datasetIndex)
    {
        setDatasetGroupRef(groupRef, datasetIndex, null);
    }

    public final void
    setDatasetID(String id, int datasetIndex)
    {
        setDatasetID(id, datasetIndex, null);
    }

    public final void
    setDatasetLocked(boolean locked, int datasetIndex)
    {
        setDatasetLocked(locked, datasetIndex, null);
    }

    public final void
    setDatasetName(String name, int datasetIndex)
    {
        setDatasetName(name, datasetIndex, null);
    }

    public final void
    setDatasetRefID(String id, int imageIndex, int datasetRefIndex)
    {
        setDatasetRefID(id, imageIndex, datasetRefIndex, null);
    }

    public final void
    setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex)
    {
        setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorGain(float gain, int instrumentIndex, int detectorIndex)
    {
        setDetectorGain(gain, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorID(String id, int instrumentIndex, int detectorIndex)
    {
        setDetectorID(id, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
    {
        setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorModel(String model, int instrumentIndex, int detectorIndex)
    {
        setDetectorModel(model, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorOffset(float offset, int instrumentIndex, int detectorIndex)
    {
        setDetectorOffset(offset, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
    {
        setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorType(String type, int instrumentIndex, int detectorIndex)
    {
        setDetectorType(type, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex)
    {
        setDetectorVoltage(voltage, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex)
    {
        setDetectorZoom(zoom, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDichroicID(String id, int instrumentIndex, int dichroicIndex)
    {
        setDichroicID(id, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
    {
        setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
    {
        setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
    {
        setDichroicModel(model, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, null);
    }

    public final void
    setDisplayOptionsDisplay(String display, int imageIndex)
    {
        setDisplayOptionsDisplay(display, imageIndex, null);
    }

    public final void
    setDisplayOptionsID(String id, int imageIndex)
    {
        setDisplayOptionsID(id, imageIndex, null);
    }

    public final void
    setDisplayOptionsZoom(float zoom, int imageIndex)
    {
        setDisplayOptionsZoom(zoom, imageIndex, null);
    }

    public final void
    setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setEmFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setEmFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setExFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setExFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setExperimentDescription(String description, int experimentIndex)
    {
        setExperimentDescription(description, experimentIndex, null);
    }

    public final void
    setExperimentExperimenterRef(String experimenterRef, int experimentIndex)
    {
        setExperimentExperimenterRef(experimenterRef, experimentIndex, null);
    }

    public final void
    setExperimentID(String id, int experimentIndex)
    {
        setExperimentID(id, experimentIndex, null);
    }

    public final void
    setExperimentType(String type, int experimentIndex)
    {
        setExperimentType(type, experimentIndex, null);
    }

    public final void
    setExperimenterEmail(String email, int experimenterIndex)
    {
        setExperimenterEmail(email, experimenterIndex, null);
    }

    public final void
    setExperimenterFirstName(String firstName, int experimenterIndex)
    {
        setExperimenterFirstName(firstName, experimenterIndex, null);
    }

    public final void
    setExperimenterID(String id, int experimenterIndex)
    {
        setExperimenterID(id, experimenterIndex, null);
    }

    public final void
    setExperimenterInstitution(String institution, int experimenterIndex)
    {
        setExperimenterInstitution(institution, experimenterIndex, null);
    }

    public final void
    setExperimenterLastName(String lastName, int experimenterIndex)
    {
        setExperimenterLastName(lastName, experimenterIndex, null);
    }

    public final void
    setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex)
    {
        setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, null);
    }

    public final void
    setExperimenterOMEName(String omeName, int experimenterIndex)
    {
        setExperimenterOMEName(omeName, experimenterIndex, null);
    }

    public final void
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setFilamentType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
    {
        setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterID(String id, int instrumentIndex, int filterIndex)
    {
        setFilterID(id, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetID(id, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetModel(model, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setGroupID(String id, int groupIndex)
    {
        setGroupID(id, groupIndex, null);
    }

    public final void
    setGroupName(String name, int groupIndex)
    {
        setGroupName(name, groupIndex, null);
    }

    public final void
    setImageAcquiredPixels(String acquiredPixels, int imageIndex)
    {
        setImageAcquiredPixels(acquiredPixels, imageIndex, null);
    }

    public final void
    setImageCreationDate(String creationDate, int imageIndex)
    {
        setImageCreationDate(creationDate, imageIndex, null);
    }

    public final void
    setImageDefaultPixels(String defaultPixels, int imageIndex)
    {
        setImageDefaultPixels(defaultPixels, imageIndex, null);
    }

    public final void
    setImageDescription(String description, int imageIndex)
    {
        setImageDescription(description, imageIndex, null);
    }

    public final void
    setImageExperimentRef(String experimentRef, int imageIndex)
    {
        setImageExperimentRef(experimentRef, imageIndex, null);
    }

    public final void
    setImageExperimenterRef(String experimenterRef, int imageIndex)
    {
        setImageExperimenterRef(experimenterRef, imageIndex, null);
    }

    public final void
    setImageGroupRef(String groupRef, int imageIndex)
    {
        setImageGroupRef(groupRef, imageIndex, null);
    }

    public final void
    setImageID(String id, int imageIndex)
    {
        setImageID(id, imageIndex, null);
    }

    public final void
    setImageInstrumentRef(String instrumentRef, int imageIndex)
    {
        setImageInstrumentRef(instrumentRef, imageIndex, null);
    }

    public final void
    setImageName(String name, int imageIndex)
    {
        setImageName(name, imageIndex, null);
    }

    public final void
    setImagingEnvironmentAirPressure(float airPressure, int imageIndex)
    {
        setImagingEnvironmentAirPressure(airPressure, imageIndex, null);
    }

    public final void
    setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex)
    {
        setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, null);
    }

    public final void
    setImagingEnvironmentHumidity(float humidity, int imageIndex)
    {
        setImagingEnvironmentHumidity(humidity, imageIndex, null);
    }

    public final void
    setImagingEnvironmentTemperature(float temperature, int imageIndex)
    {
        setImagingEnvironmentTemperature(temperature, imageIndex, null);
    }

    public final void
    setInstrumentID(String id, int instrumentIndex)
    {
        setInstrumentID(id, instrumentIndex, null);
    }

    public final void
    setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
    {
        setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex)
    {
        setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPulse(pulse, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex)
    {
        setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex)
    {
        setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setLaserType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex)
    {
        setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceID(String id, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceID(id, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceModel(model, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourcePower(power, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX1(x1, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX2(x2, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY1(y1, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY2(y2, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelID(id, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelName(name, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskHeight(height, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskWidth(width, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskX(x, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskY(y, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex)
    {
        setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, null);
    }

    public final void
    setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicroscopeID(String id, int instrumentIndex)
    {
        setMicroscopeID(id, instrumentIndex, null);
    }

    public final void
    setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
    {
        setMicroscopeManufacturer(manufacturer, instrumentIndex, null);
    }

    public final void
    setMicroscopeModel(String model, int instrumentIndex)
    {
        setMicroscopeModel(model, instrumentIndex, null);
    }

    public final void
    setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
    {
        setMicroscopeSerialNumber(serialNumber, instrumentIndex, null);
    }

    public final void
    setMicroscopeType(String type, int instrumentIndex)
    {
        setMicroscopeType(type, instrumentIndex, null);
    }

    public final void
    setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex)
    {
        setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFID(String id, int instrumentIndex, int otfIndex)
    {
        setOTFID(id, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFObjective(String objective, int instrumentIndex, int otfIndex)
    {
        setOTFObjective(objective, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex)
    {
        setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex)
    {
        setOTFPixelType(pixelType, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex)
    {
        setOTFSizeX(sizeX, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex)
    {
        setOTFSizeY(sizeY, instrumentIndex, otfIndex, null);
    }

    public final void
    setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveID(id, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveIris(iris, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveModel(model, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex)
    {
        setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, null);
    }

    public final void
    setObjectiveSettingsMedium(String medium, int imageIndex)
    {
        setObjectiveSettingsMedium(medium, imageIndex, null);
    }

    public final void
    setObjectiveSettingsObjective(String objective, int imageIndex)
    {
        setObjectiveSettingsObjective(objective, imageIndex, null);
    }

    public final void
    setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex)
    {
        setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, null);
    }

    public final void
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setPathD(String d, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathD(d, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPathID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex)
    {
        setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex)
    {
        setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsID(String id, int imageIndex, int pixelsIndex)
    {
        setPixelsID(id, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex)
    {
        setPixelsPixelType(pixelType, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeC(sizeC, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeT(sizeT, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeX(sizeX, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeY(sizeY, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, null);
    }

    public final void
    setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneID(id, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex)
    {
        setPlateColumnNamingConvention(columnNamingConvention, plateIndex, null);
    }

    public final void
    setPlateDescription(String description, int plateIndex)
    {
        setPlateDescription(description, plateIndex, null);
    }

    public final void
    setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
    {
        setPlateExternalIdentifier(externalIdentifier, plateIndex, null);
    }

    public final void
    setPlateID(String id, int plateIndex)
    {
        setPlateID(id, plateIndex, null);
    }

    public final void
    setPlateName(String name, int plateIndex)
    {
        setPlateName(name, plateIndex, null);
    }

    public final void
    setPlateRefID(String id, int screenIndex, int plateRefIndex)
    {
        setPlateRefID(id, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRefSample(int sample, int screenIndex, int plateRefIndex)
    {
        setPlateRefSample(sample, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRefWell(String well, int screenIndex, int plateRefIndex)
    {
        setPlateRefWell(well, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRowNamingConvention(String rowNamingConvention, int plateIndex)
    {
        setPlateRowNamingConvention(rowNamingConvention, plateIndex, null);
    }

    public final void
    setPlateStatus(String status, int plateIndex)
    {
        setPlateStatus(status, plateIndex, null);
    }

    public final void
    setPlateWellOriginX(double wellOriginX, int plateIndex)
    {
        setPlateWellOriginX(wellOriginX, plateIndex, null);
    }

    public final void
    setPlateWellOriginY(double wellOriginY, int plateIndex)
    {
        setPlateWellOriginY(wellOriginY, plateIndex, null);
    }

    public final void
    setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointR(r, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setProjectDescription(String description, int projectIndex)
    {
        setProjectDescription(description, projectIndex, null);
    }

    public final void
    setProjectExperimenterRef(String experimenterRef, int projectIndex)
    {
        setProjectExperimenterRef(experimenterRef, projectIndex, null);
    }

    public final void
    setProjectGroupRef(String groupRef, int projectIndex)
    {
        setProjectGroupRef(groupRef, projectIndex, null);
    }

    public final void
    setProjectID(String id, int projectIndex)
    {
        setProjectID(id, projectIndex, null);
    }

    public final void
    setProjectName(String name, int projectIndex)
    {
        setProjectName(name, projectIndex, null);
    }

    public final void
    setProjectRefID(String id, int datasetIndex, int projectRefIndex)
    {
        setProjectRefID(id, datasetIndex, projectRefIndex, null);
    }

    public final void
    setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex)
    {
        setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setROIID(String id, int imageIndex, int roiIndex)
    {
        setROIID(id, imageIndex, roiIndex, null);
    }

    public final void
    setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex)
    {
        setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, null);
    }

    public final void
    setROIT0(int t0, int imageIndex, int roiIndex)
    {
        setROIT0(t0, imageIndex, roiIndex, null);
    }

    public final void
    setROIT1(int t1, int imageIndex, int roiIndex)
    {
        setROIT1(t1, imageIndex, roiIndex, null);
    }

    public final void
    setROIX0(int x0, int imageIndex, int roiIndex)
    {
        setROIX0(x0, imageIndex, roiIndex, null);
    }

    public final void
    setROIX1(int x1, int imageIndex, int roiIndex)
    {
        setROIX1(x1, imageIndex, roiIndex, null);
    }

    public final void
    setROIY0(int y0, int imageIndex, int roiIndex)
    {
        setROIY0(y0, imageIndex, roiIndex, null);
    }

    public final void
    setROIY1(int y1, int imageIndex, int roiIndex)
    {
        setROIY1(y1, imageIndex, roiIndex, null);
    }

    public final void
    setROIZ0(int z0, int imageIndex, int roiIndex)
    {
        setROIZ0(z0, imageIndex, roiIndex, null);
    }

    public final void
    setROIZ1(int z1, int imageIndex, int roiIndex)
    {
        setROIZ1(z1, imageIndex, roiIndex, null);
    }

    public final void
    setReagentDescription(String description, int screenIndex, int reagentIndex)
    {
        setReagentDescription(description, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentID(String id, int screenIndex, int reagentIndex)
    {
        setReagentID(id, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentName(String name, int screenIndex, int reagentIndex)
    {
        setReagentName(name, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
    {
        setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, null);
    }

    public final void
    setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectHeight(height, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectWidth(width, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectX(x, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectY(y, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRegionID(String id, int imageIndex, int regionIndex)
    {
        setRegionID(id, imageIndex, regionIndex, null);
    }

    public final void
    setRegionName(String name, int imageIndex, int regionIndex)
    {
        setRegionName(name, imageIndex, regionIndex, null);
    }

    public final void
    setRegionTag(String tag, int imageIndex, int regionIndex)
    {
        setRegionTag(tag, imageIndex, regionIndex, null);
    }

    public final void
    setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenDescription(String description, int screenIndex)
    {
        setScreenDescription(description, screenIndex, null);
    }

    public final void
    setScreenExtern(String extern, int screenIndex)
    {
        setScreenExtern(extern, screenIndex, null);
    }

    public final void
    setScreenID(String id, int screenIndex)
    {
        setScreenID(id, screenIndex, null);
    }

    public final void
    setScreenName(String name, int screenIndex)
    {
        setScreenName(name, screenIndex, null);
    }

    public final void
    setScreenProtocolDescription(String protocolDescription, int screenIndex)
    {
        setScreenProtocolDescription(protocolDescription, screenIndex, null);
    }

    public final void
    setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
    {
        setScreenProtocolIdentifier(protocolIdentifier, screenIndex, null);
    }

    public final void
    setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
    {
        setScreenReagentSetDescription(reagentSetDescription, screenIndex, null);
    }

    public final void
    setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
    {
        setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, null);
    }

    public final void
    setScreenRefID(String id, int plateIndex, int screenRefIndex)
    {
        setScreenRefID(id, plateIndex, screenRefIndex, null);
    }

    public final void
    setScreenType(String type, int screenIndex)
    {
        setScreenType(type, screenIndex, null);
    }

    public final void
    setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeG(g, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeText(text, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setStageLabelName(String name, int imageIndex)
    {
        setStageLabelName(name, imageIndex, null);
    }

    public final void
    setStageLabelX(float x, int imageIndex)
    {
        setStageLabelX(x, imageIndex, null);
    }

    public final void
    setStageLabelY(float y, int imageIndex)
    {
        setStageLabelY(y, imageIndex, null);
    }

    public final void
    setStageLabelZ(float z, int imageIndex)
    {
        setStageLabelZ(z, imageIndex, null);
    }

    public final void
    setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setThumbnailHref(String href, int imageIndex)
    {
        setThumbnailHref(href, imageIndex, null);
    }

    public final void
    setThumbnailID(String id, int imageIndex)
    {
        setThumbnailID(id, imageIndex, null);
    }

    public final void
    setThumbnailMIMEtype(String mimEtype, int imageIndex)
    {
        setThumbnailMIMEtype(mimEtype, imageIndex, null);
    }

    public final void
    setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, null);
    }

    public final void
    setUUID(String uuid)
    {
        setUUID(uuid, null);
    }

    public final void
    setWellColumn(int column, int plateIndex, int wellIndex)
    {
        setWellColumn(column, plateIndex, wellIndex, null);
    }

    public final void
    setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
    {
        setWellExternalDescription(externalDescription, plateIndex, wellIndex, null);
    }

    public final void
    setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
    {
        setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, null);
    }

    public final void
    setWellID(String id, int plateIndex, int wellIndex)
    {
        setWellID(id, plateIndex, wellIndex, null);
    }

    public final void
    setWellReagent(String reagent, int plateIndex, int wellIndex)
    {
        setWellReagent(reagent, plateIndex, wellIndex, null);
    }

    public final void
    setWellRow(int row, int plateIndex, int wellIndex)
    {
        setWellRow(row, plateIndex, wellIndex, null);
    }

    public final void
    setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex)
    {
        setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, null);
    }

    public final void
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellType(String type, int plateIndex, int wellIndex)
    {
        setWellType(type, plateIndex, wellIndex, null);
    }

    public static Ice.DispatchStatus
    ___getServant(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        MetadataStore __ret = __obj.getServant(__current);
        __os.writeObject(__ret);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOMEXML(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOMEXML(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___createRoot(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.createRoot(__current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setUUID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String uuid;
        uuid = __is.readString();
        __is.endReadEncaps();
        __obj.setUUID(uuid, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setArcType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setArcType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentColorDomain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String colorDomain;
        colorDomain = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentIndex(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int index;
        index = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentPixels(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixels;
        pixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleCx(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleCy(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleR(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String r;
        r = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleR(r, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setContactExperimenter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenter;
        experimenter = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setContactExperimenter(experimenter, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetDescription(description, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetExperimenterRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetExperimenterRef(experimenterRef, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetGroupRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetGroupRef(groupRef, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetID(id, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetLocked(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean locked;
        locked = __is.readBool();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetLocked(locked, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetName(name, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int datasetRefIndex;
        datasetRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetRefID(id, imageIndex, datasetRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorAmplificationGain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float amplificationGain;
        amplificationGain = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorGain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float gain;
        gain = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorGain(gain, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorID(id, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorModel(model, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorOffset(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float offset;
        offset = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorOffset(offset, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorType(type, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorVoltage(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float voltage;
        voltage = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorZoom(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float zoom;
        zoom = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorZoom(zoom, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsBinning(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binning;
        binning = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsDetector(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String detector;
        detector = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsGain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float gain;
        gain = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsOffset(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float offset;
        offset = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsReadOutRate(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float readOutRate;
        readOutRate = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsVoltage(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float voltage;
        voltage = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicID(id, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicLotNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicModel(model, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeX;
        physicalSizeX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeY;
        physicalSizeY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeZ;
        physicalSizeZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsTimeIncrement(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float timeIncrement;
        timeIncrement = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsWaveIncrement(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int waveIncrement;
        waveIncrement = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsWaveStart(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int waveStart;
        waveStart = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsDisplay(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String display;
        display = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsDisplay(display, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsZoom(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float zoom;
        zoom = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsZoom(zoom, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseCx(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseCy(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseRx(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String rx;
        rx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseRy(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String ry;
        ry = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterLotNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterLotNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentDescription(description, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentExperimenterRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentExperimenterRef(experimenterRef, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentID(id, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentType(type, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterEmail(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String email;
        email = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterEmail(email, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterFirstName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String firstName;
        firstName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterFirstName(firstName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterID(id, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterInstitution(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String institution;
        institution = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterInstitution(institution, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterLastName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lastName;
        lastName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterLastName(lastName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterOMEName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String omeName;
        omeName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterOMEName(omeName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterMembershipGroup(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String group;
        group = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int groupRefIndex;
        groupRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilamentType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilamentType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterFilterWheel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String filterWheel;
        filterWheel = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterID(id, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterLotNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetDichroic(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String dichroic;
        dichroic = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetEmFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String emFilter;
        emFilter = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetExFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String exFilter;
        exFilter = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetID(id, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetLotNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetModel(model, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setGroupID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setGroupID(id, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setGroupName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setGroupName(name, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageAcquiredPixels(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String acquiredPixels;
        acquiredPixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageAcquiredPixels(acquiredPixels, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageCreationDate(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String creationDate;
        creationDate = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageCreationDate(creationDate, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageDefaultPixels(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String defaultPixels;
        defaultPixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageDefaultPixels(defaultPixels, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageDescription(description, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageExperimentRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimentRef;
        experimentRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageExperimentRef(experimentRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageExperimenterRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageExperimenterRef(experimenterRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageGroupRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageGroupRef(groupRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageInstrumentRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String instrumentRef;
        instrumentRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageInstrumentRef(instrumentRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageName(name, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentAirPressure(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float airPressure;
        airPressure = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentAirPressure(airPressure, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentCO2Percent(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float cO2Percent;
        cO2Percent = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentHumidity(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float humidity;
        humidity = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentHumidity(humidity, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentTemperature(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float temperature;
        temperature = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentTemperature(temperature, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setInstrumentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setInstrumentID(id, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserFrequencyMultiplication(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int frequencyMultiplication;
        frequencyMultiplication = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserLaserMedium(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String laserMedium;
        laserMedium = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserPockelCell(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean pockelCell;
        pockelCell = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserPulse(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pulse;
        pulse = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserRepetitionRate(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean repetitionRate;
        repetitionRate = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserTuneable(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean tuneable;
        tuneable = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserWavelength(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceID(id, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceModel(model, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourcePower(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float power;
        power = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourcePower(power, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefAttenuation(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float attenuation;
        attenuation = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefLightSource(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefWavelength(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsAttenuation(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float attenuation;
        attenuation = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsLightSource(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsWavelength(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineX1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x1;
        x1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineX1(x1, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineX2(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x2;
        x2 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineX2(x2, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineY1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y1;
        y1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineY1(y1, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineY2(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y2;
        y2 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineY2(y2, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelContrastMethod(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String contrastMethod;
        contrastMethod = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelDetector(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String detector;
        detector = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelEmWave(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int emWave;
        emWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelExWave(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int exWave;
        exWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelFilterSet(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String filterSet;
        filterSet = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelFluor(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fluor;
        fluor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelID(id, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelIlluminationType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String illuminationType;
        illuminationType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelLightSource(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelMode(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String mode;
        mode = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelName(name, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelNdFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float ndFilter;
        ndFilter = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelOTF(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String otf;
        otf = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPhotometricInterpretation(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String photometricInterpretation;
        photometricInterpretation = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPinholeSize(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float pinholeSize;
        pinholeSize = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPockelCellSetting(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int pockelCellSetting;
        pockelCellSetting = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSamplesPerPixel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int samplesPerPixel;
        samplesPerPixel = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSecondaryEmissionFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String secondaryEmissionFilter;
        secondaryEmissionFilter = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSecondaryExcitationFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String secondaryExcitationFilter;
        secondaryExcitationFilter = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskHeight(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String height;
        height = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskHeight(height, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskWidth(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String width;
        width = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskWidth(width, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x;
        x = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskX(x, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y;
        y = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskY(y, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsBigEndian(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean bigEndian;
        bigEndian = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsBinData(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binData;
        binData = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsExtendedPixelType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String extendedPixelType;
        extendedPixelType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationExperimenterRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        int microbeamManipulationRefIndex;
        microbeamManipulationRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeID(id, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeManufacturer(manufacturer, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeModel(model, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeSerialNumber(serialNumber, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeType(type, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFBinaryFile(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binaryFile;
        binaryFile = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFID(id, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFObjective(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String objective;
        objective = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFObjective(objective, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFOpticalAxisAveraged(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean opticalAxisAveraged;
        opticalAxisAveraged = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFPixelType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixelType;
        pixelType = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFPixelType(pixelType, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFSizeX(sizeX, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFSizeY(sizeY, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveCalibratedMagnification(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float calibratedMagnification;
        calibratedMagnification = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveCorrection(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String correction;
        correction = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveID(id, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveImmersion(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String immersion;
        immersion = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveIris(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean iris;
        iris = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveIris(iris, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveLensNA(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float lensNA;
        lensNA = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveModel(model, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveNominalMagnification(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int nominalMagnification;
        nominalMagnification = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveWorkingDistance(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float workingDistance;
        workingDistance = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsCorrectionCollar(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float correctionCollar;
        correctionCollar = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsMedium(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String medium;
        medium = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsMedium(medium, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsObjective(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String objective;
        objective = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsObjective(objective, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsRefractiveIndex(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float refractiveIndex;
        refractiveIndex = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPathD(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String d;
        d = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPathD(d, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPathID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPathID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsBigEndian(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean bigEndian;
        bigEndian = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsDimensionOrder(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String dimensionOrder;
        dimensionOrder = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsID(id, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsPixelType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixelType;
        pixelType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeC;
        sizeC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeT;
        sizeT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeZ;
        sizeZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneHashSHA1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hashSHA1;
        hashSHA1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneID(id, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theC;
        theC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theT;
        theT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theZ;
        theZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTimingDeltaT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float deltaT;
        deltaT = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTimingExposureTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float exposureTime;
        exposureTime = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateColumnNamingConvention(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String columnNamingConvention;
        columnNamingConvention = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateColumnNamingConvention(columnNamingConvention, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateDescription(description, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateExternalIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateExternalIdentifier(externalIdentifier, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateID(id, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateName(name, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRowNamingConvention(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String rowNamingConvention;
        rowNamingConvention = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRowNamingConvention(rowNamingConvention, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateStatus(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String status;
        status = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateStatus(status, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateWellOriginX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        double wellOriginX;
        wellOriginX = __is.readDouble();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateWellOriginX(wellOriginX, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateWellOriginY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        double wellOriginY;
        wellOriginY = __is.readDouble();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateWellOriginY(wellOriginY, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefID(id, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefSample(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sample;
        sample = __is.readInt();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefSample(sample, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefWell(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String well;
        well = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefWell(well, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointCx(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointCy(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointR(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String r;
        r = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointR(r, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonPoints(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String points;
        points = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylineID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylineID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylinePoints(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String points;
        points = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylineTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectDescription(description, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectExperimenterRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectExperimenterRef(experimenterRef, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectGroupRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectGroupRef(groupRef, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectID(id, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectName(name, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        int projectRefIndex;
        projectRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectRefID(id, datasetIndex, projectRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPumpLightSource(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIID(id, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIT0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int t0;
        t0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIT0(t0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIT1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int t1;
        t1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIT1(t1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIX0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int x0;
        x0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIX0(x0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIX1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int x1;
        x1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIX1(x1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIY0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int y0;
        y0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIY0(y0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIY1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int y1;
        y1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIY1(y1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIZ0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int z0;
        z0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIZ0(z0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIZ1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int z1;
        z1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIZ1(z1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int roiRefIndex;
        roiRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentDescription(description, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentID(id, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentName(name, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentReagentIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentIdentifier;
        reagentIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectHeight(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String height;
        height = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectHeight(height, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectTransform(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectWidth(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String width;
        width = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectWidth(width, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x;
        x = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectX(x, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y;
        y = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectY(y, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionID(id, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionName(name, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionTag(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String tag;
        tag = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionTag(tag, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkDirection(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String direction;
        direction = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String ref;
        ref = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenDescription(description, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenExtern(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String extern;
        extern = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenExtern(extern, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenID(id, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenName(name, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenProtocolDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String protocolDescription;
        protocolDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenProtocolDescription(protocolDescription, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenProtocolIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String protocolIdentifier;
        protocolIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenReagentSetDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentSetDescription;
        reagentSetDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenReagentSetDescription(reagentSetDescription, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenReagentSetIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentSetIdentifier;
        reagentSetIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenType(type, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionEndTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String endTime;
        endTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionStartTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String startTime;
        startTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int screenRefIndex;
        screenRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenRefID(id, plateIndex, screenRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeBaselineShift(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String baselineShift;
        baselineShift = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeDirection(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String direction;
        direction = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillColor(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillColor;
        fillColor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillOpacity(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillOpacity;
        fillOpacity = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillRule(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillRule;
        fillRule = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontFamily(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontFamily;
        fontFamily = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontSize(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int fontSize;
        fontSize = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontStretch(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontStretch;
        fontStretch = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontStyle(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontStyle;
        fontStyle = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontVariant(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontVariant;
        fontVariant = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontWeight(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontWeight;
        fontWeight = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeG(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String g;
        g = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeG(g, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeGlyphOrientationVertical(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int glyphOrientationVertical;
        glyphOrientationVertical = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeLocked(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean locked;
        locked = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeAttribute(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeAttribute;
        strokeAttribute = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeColor(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeColor;
        strokeColor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeDashArray(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeDashArray;
        strokeDashArray = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeLineCap(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeLineCap;
        strokeLineCap = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeLineJoin(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeLineJoin;
        strokeLineJoin = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeMiterLimit(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int strokeMiterLimit;
        strokeMiterLimit = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeOpacity(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float strokeOpacity;
        strokeOpacity = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeWidth(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int strokeWidth;
        strokeWidth = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeText(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String text;
        text = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeText(text, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextAnchor(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textAnchor;
        textAnchor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextDecoration(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textDecoration;
        textDecoration = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextFill(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textFill;
        textFill = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextStroke(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textStroke;
        textStroke = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTheT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theT;
        theT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTheZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theZ;
        theZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeVectorEffect(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String vectorEffect;
        vectorEffect = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeVisibility(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean visibility;
        visibility = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeWritingMode(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String writingMode;
        writingMode = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelName(name, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float x;
        x = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelX(x, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float y;
        y = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelY(y, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float z;
        z = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelZ(z, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionX;
        positionX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionY;
        positionY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionZ;
        positionZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailHref(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String href;
        href = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailHref(href, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailMIMEtype(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String mimEtype;
        mimEtype = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailMIMEtype(mimEtype, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFileName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fileName;
        fileName = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstC;
        firstC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstT;
        firstT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstZ;
        firstZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataIFD(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int ifd;
        ifd = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataNumPlanes(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int numPlanes;
        numPlanes = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataUUID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String uuid;
        uuid = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutIn(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutIn;
        cutIn = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutInTolerance(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutInTolerance;
        cutInTolerance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutOut(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutOut;
        cutOut = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutOutTolerance(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutOutTolerance;
        cutOutTolerance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeTransmittance(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int transmittance;
        transmittance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellColumn(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int column;
        column = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellColumn(column, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellExternalDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalDescription;
        externalDescription = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellExternalDescription(externalDescription, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellExternalIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellID(id, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellReagent(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagent;
        reagent = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellReagent(reagent, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellRow(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int row;
        row = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellRow(row, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellType(type, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleImageRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String imageRef;
        imageRef = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleIndex(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int index;
        index = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSamplePosX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float posX;
        posX = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSamplePosY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float posY;
        posY = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleTimepoint(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int timepoint;
        timepoint = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        int wellSampleRefIndex;
        wellSampleRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "createRoot",
        "getOMEXML",
        "getServant",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "setArcType",
        "setChannelComponentColorDomain",
        "setChannelComponentIndex",
        "setChannelComponentPixels",
        "setCircleCx",
        "setCircleCy",
        "setCircleID",
        "setCircleR",
        "setCircleTransform",
        "setContactExperimenter",
        "setDatasetDescription",
        "setDatasetExperimenterRef",
        "setDatasetGroupRef",
        "setDatasetID",
        "setDatasetLocked",
        "setDatasetName",
        "setDatasetRefID",
        "setDetectorAmplificationGain",
        "setDetectorGain",
        "setDetectorID",
        "setDetectorManufacturer",
        "setDetectorModel",
        "setDetectorOffset",
        "setDetectorSerialNumber",
        "setDetectorSettingsBinning",
        "setDetectorSettingsDetector",
        "setDetectorSettingsGain",
        "setDetectorSettingsOffset",
        "setDetectorSettingsReadOutRate",
        "setDetectorSettingsVoltage",
        "setDetectorType",
        "setDetectorVoltage",
        "setDetectorZoom",
        "setDichroicID",
        "setDichroicLotNumber",
        "setDichroicManufacturer",
        "setDichroicModel",
        "setDimensionsPhysicalSizeX",
        "setDimensionsPhysicalSizeY",
        "setDimensionsPhysicalSizeZ",
        "setDimensionsTimeIncrement",
        "setDimensionsWaveIncrement",
        "setDimensionsWaveStart",
        "setDisplayOptionsDisplay",
        "setDisplayOptionsID",
        "setDisplayOptionsZoom",
        "setEllipseCx",
        "setEllipseCy",
        "setEllipseID",
        "setEllipseRx",
        "setEllipseRy",
        "setEllipseTransform",
        "setEmFilterLotNumber",
        "setEmFilterManufacturer",
        "setEmFilterModel",
        "setEmFilterType",
        "setExFilterLotNumber",
        "setExFilterManufacturer",
        "setExFilterModel",
        "setExFilterType",
        "setExperimentDescription",
        "setExperimentExperimenterRef",
        "setExperimentID",
        "setExperimentType",
        "setExperimenterEmail",
        "setExperimenterFirstName",
        "setExperimenterID",
        "setExperimenterInstitution",
        "setExperimenterLastName",
        "setExperimenterMembershipGroup",
        "setExperimenterOMEName",
        "setFilamentType",
        "setFilterFilterWheel",
        "setFilterID",
        "setFilterLotNumber",
        "setFilterManufacturer",
        "setFilterModel",
        "setFilterSetDichroic",
        "setFilterSetEmFilter",
        "setFilterSetExFilter",
        "setFilterSetID",
        "setFilterSetLotNumber",
        "setFilterSetManufacturer",
        "setFilterSetModel",
        "setFilterType",
        "setGroupID",
        "setGroupName",
        "setImageAcquiredPixels",
        "setImageCreationDate",
        "setImageDefaultPixels",
        "setImageDescription",
        "setImageExperimentRef",
        "setImageExperimenterRef",
        "setImageGroupRef",
        "setImageID",
        "setImageInstrumentRef",
        "setImageName",
        "setImagingEnvironmentAirPressure",
        "setImagingEnvironmentCO2Percent",
        "setImagingEnvironmentHumidity",
        "setImagingEnvironmentTemperature",
        "setInstrumentID",
        "setLaserFrequencyMultiplication",
        "setLaserLaserMedium",
        "setLaserPockelCell",
        "setLaserPulse",
        "setLaserRepetitionRate",
        "setLaserTuneable",
        "setLaserType",
        "setLaserWavelength",
        "setLightSourceID",
        "setLightSourceManufacturer",
        "setLightSourceModel",
        "setLightSourcePower",
        "setLightSourceRefAttenuation",
        "setLightSourceRefLightSource",
        "setLightSourceRefWavelength",
        "setLightSourceSerialNumber",
        "setLightSourceSettingsAttenuation",
        "setLightSourceSettingsLightSource",
        "setLightSourceSettingsWavelength",
        "setLineID",
        "setLineTransform",
        "setLineX1",
        "setLineX2",
        "setLineY1",
        "setLineY2",
        "setLogicalChannelContrastMethod",
        "setLogicalChannelDetector",
        "setLogicalChannelEmWave",
        "setLogicalChannelExWave",
        "setLogicalChannelFilterSet",
        "setLogicalChannelFluor",
        "setLogicalChannelID",
        "setLogicalChannelIlluminationType",
        "setLogicalChannelLightSource",
        "setLogicalChannelMode",
        "setLogicalChannelName",
        "setLogicalChannelNdFilter",
        "setLogicalChannelOTF",
        "setLogicalChannelPhotometricInterpretation",
        "setLogicalChannelPinholeSize",
        "setLogicalChannelPockelCellSetting",
        "setLogicalChannelSamplesPerPixel",
        "setLogicalChannelSecondaryEmissionFilter",
        "setLogicalChannelSecondaryExcitationFilter",
        "setMaskHeight",
        "setMaskID",
        "setMaskPixelsBigEndian",
        "setMaskPixelsBinData",
        "setMaskPixelsExtendedPixelType",
        "setMaskPixelsID",
        "setMaskPixelsSizeX",
        "setMaskPixelsSizeY",
        "setMaskTransform",
        "setMaskWidth",
        "setMaskX",
        "setMaskY",
        "setMicrobeamManipulationExperimenterRef",
        "setMicrobeamManipulationID",
        "setMicrobeamManipulationRefID",
        "setMicrobeamManipulationType",
        "setMicroscopeID",
        "setMicroscopeManufacturer",
        "setMicroscopeModel",
        "setMicroscopeSerialNumber",
        "setMicroscopeType",
        "setOTFBinaryFile",
        "setOTFID",
        "setOTFObjective",
        "setOTFOpticalAxisAveraged",
        "setOTFPixelType",
        "setOTFSizeX",
        "setOTFSizeY",
        "setObjectiveCalibratedMagnification",
        "setObjectiveCorrection",
        "setObjectiveID",
        "setObjectiveImmersion",
        "setObjectiveIris",
        "setObjectiveLensNA",
        "setObjectiveManufacturer",
        "setObjectiveModel",
        "setObjectiveNominalMagnification",
        "setObjectiveSerialNumber",
        "setObjectiveSettingsCorrectionCollar",
        "setObjectiveSettingsMedium",
        "setObjectiveSettingsObjective",
        "setObjectiveSettingsRefractiveIndex",
        "setObjectiveWorkingDistance",
        "setPathD",
        "setPathID",
        "setPixelsBigEndian",
        "setPixelsDimensionOrder",
        "setPixelsID",
        "setPixelsPixelType",
        "setPixelsSizeC",
        "setPixelsSizeT",
        "setPixelsSizeX",
        "setPixelsSizeY",
        "setPixelsSizeZ",
        "setPlaneHashSHA1",
        "setPlaneID",
        "setPlaneTheC",
        "setPlaneTheT",
        "setPlaneTheZ",
        "setPlaneTimingDeltaT",
        "setPlaneTimingExposureTime",
        "setPlateColumnNamingConvention",
        "setPlateDescription",
        "setPlateExternalIdentifier",
        "setPlateID",
        "setPlateName",
        "setPlateRefID",
        "setPlateRefSample",
        "setPlateRefWell",
        "setPlateRowNamingConvention",
        "setPlateStatus",
        "setPlateWellOriginX",
        "setPlateWellOriginY",
        "setPointCx",
        "setPointCy",
        "setPointID",
        "setPointR",
        "setPointTransform",
        "setPolygonID",
        "setPolygonPoints",
        "setPolygonTransform",
        "setPolylineID",
        "setPolylinePoints",
        "setPolylineTransform",
        "setProjectDescription",
        "setProjectExperimenterRef",
        "setProjectGroupRef",
        "setProjectID",
        "setProjectName",
        "setProjectRefID",
        "setPumpLightSource",
        "setROIID",
        "setROIRefID",
        "setROIT0",
        "setROIT1",
        "setROIX0",
        "setROIX1",
        "setROIY0",
        "setROIY1",
        "setROIZ0",
        "setROIZ1",
        "setReagentDescription",
        "setReagentID",
        "setReagentName",
        "setReagentReagentIdentifier",
        "setRectHeight",
        "setRectID",
        "setRectTransform",
        "setRectWidth",
        "setRectX",
        "setRectY",
        "setRegionID",
        "setRegionName",
        "setRegionTag",
        "setRoiLinkDirection",
        "setRoiLinkName",
        "setRoiLinkRef",
        "setScreenAcquisitionEndTime",
        "setScreenAcquisitionID",
        "setScreenAcquisitionStartTime",
        "setScreenDescription",
        "setScreenExtern",
        "setScreenID",
        "setScreenName",
        "setScreenProtocolDescription",
        "setScreenProtocolIdentifier",
        "setScreenReagentSetDescription",
        "setScreenReagentSetIdentifier",
        "setScreenRefID",
        "setScreenType",
        "setShapeBaselineShift",
        "setShapeDirection",
        "setShapeFillColor",
        "setShapeFillOpacity",
        "setShapeFillRule",
        "setShapeFontFamily",
        "setShapeFontSize",
        "setShapeFontStretch",
        "setShapeFontStyle",
        "setShapeFontVariant",
        "setShapeFontWeight",
        "setShapeG",
        "setShapeGlyphOrientationVertical",
        "setShapeID",
        "setShapeLocked",
        "setShapeStrokeAttribute",
        "setShapeStrokeColor",
        "setShapeStrokeDashArray",
        "setShapeStrokeLineCap",
        "setShapeStrokeLineJoin",
        "setShapeStrokeMiterLimit",
        "setShapeStrokeOpacity",
        "setShapeStrokeWidth",
        "setShapeText",
        "setShapeTextAnchor",
        "setShapeTextDecoration",
        "setShapeTextFill",
        "setShapeTextStroke",
        "setShapeTheT",
        "setShapeTheZ",
        "setShapeVectorEffect",
        "setShapeVisibility",
        "setShapeWritingMode",
        "setStageLabelName",
        "setStageLabelX",
        "setStageLabelY",
        "setStageLabelZ",
        "setStagePositionPositionX",
        "setStagePositionPositionY",
        "setStagePositionPositionZ",
        "setThumbnailHref",
        "setThumbnailID",
        "setThumbnailMIMEtype",
        "setTiffDataFileName",
        "setTiffDataFirstC",
        "setTiffDataFirstT",
        "setTiffDataFirstZ",
        "setTiffDataIFD",
        "setTiffDataNumPlanes",
        "setTiffDataUUID",
        "setTransmittanceRangeCutIn",
        "setTransmittanceRangeCutInTolerance",
        "setTransmittanceRangeCutOut",
        "setTransmittanceRangeCutOutTolerance",
        "setTransmittanceRangeTransmittance",
        "setUUID",
        "setWellColumn",
        "setWellExternalDescription",
        "setWellExternalIdentifier",
        "setWellID",
        "setWellReagent",
        "setWellRow",
        "setWellSampleID",
        "setWellSampleImageRef",
        "setWellSampleIndex",
        "setWellSamplePosX",
        "setWellSamplePosY",
        "setWellSampleRefID",
        "setWellSampleTimepoint",
        "setWellType"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___createRoot(this, in, __current);
            }
            case 1:
            {
                return ___getOMEXML(this, in, __current);
            }
            case 2:
            {
                return ___getServant(this, in, __current);
            }
            case 3:
            {
                return ___ice_id(this, in, __current);
            }
            case 4:
            {
                return ___ice_ids(this, in, __current);
            }
            case 5:
            {
                return ___ice_isA(this, in, __current);
            }
            case 6:
            {
                return ___ice_ping(this, in, __current);
            }
            case 7:
            {
                return ___setArcType(this, in, __current);
            }
            case 8:
            {
                return ___setChannelComponentColorDomain(this, in, __current);
            }
            case 9:
            {
                return ___setChannelComponentIndex(this, in, __current);
            }
            case 10:
            {
                return ___setChannelComponentPixels(this, in, __current);
            }
            case 11:
            {
                return ___setCircleCx(this, in, __current);
            }
            case 12:
            {
                return ___setCircleCy(this, in, __current);
            }
            case 13:
            {
                return ___setCircleID(this, in, __current);
            }
            case 14:
            {
                return ___setCircleR(this, in, __current);
            }
            case 15:
            {
                return ___setCircleTransform(this, in, __current);
            }
            case 16:
            {
                return ___setContactExperimenter(this, in, __current);
            }
            case 17:
            {
                return ___setDatasetDescription(this, in, __current);
            }
            case 18:
            {
                return ___setDatasetExperimenterRef(this, in, __current);
            }
            case 19:
            {
                return ___setDatasetGroupRef(this, in, __current);
            }
            case 20:
            {
                return ___setDatasetID(this, in, __current);
            }
            case 21:
            {
                return ___setDatasetLocked(this, in, __current);
            }
            case 22:
            {
                return ___setDatasetName(this, in, __current);
            }
            case 23:
            {
                return ___setDatasetRefID(this, in, __current);
            }
            case 24:
            {
                return ___setDetectorAmplificationGain(this, in, __current);
            }
            case 25:
            {
                return ___setDetectorGain(this, in, __current);
            }
            case 26:
            {
                return ___setDetectorID(this, in, __current);
            }
            case 27:
            {
                return ___setDetectorManufacturer(this, in, __current);
            }
            case 28:
            {
                return ___setDetectorModel(this, in, __current);
            }
            case 29:
            {
                return ___setDetectorOffset(this, in, __current);
            }
            case 30:
            {
                return ___setDetectorSerialNumber(this, in, __current);
            }
            case 31:
            {
                return ___setDetectorSettingsBinning(this, in, __current);
            }
            case 32:
            {
                return ___setDetectorSettingsDetector(this, in, __current);
            }
            case 33:
            {
                return ___setDetectorSettingsGain(this, in, __current);
            }
            case 34:
            {
                return ___setDetectorSettingsOffset(this, in, __current);
            }
            case 35:
            {
                return ___setDetectorSettingsReadOutRate(this, in, __current);
            }
            case 36:
            {
                return ___setDetectorSettingsVoltage(this, in, __current);
            }
            case 37:
            {
                return ___setDetectorType(this, in, __current);
            }
            case 38:
            {
                return ___setDetectorVoltage(this, in, __current);
            }
            case 39:
            {
                return ___setDetectorZoom(this, in, __current);
            }
            case 40:
            {
                return ___setDichroicID(this, in, __current);
            }
            case 41:
            {
                return ___setDichroicLotNumber(this, in, __current);
            }
            case 42:
            {
                return ___setDichroicManufacturer(this, in, __current);
            }
            case 43:
            {
                return ___setDichroicModel(this, in, __current);
            }
            case 44:
            {
                return ___setDimensionsPhysicalSizeX(this, in, __current);
            }
            case 45:
            {
                return ___setDimensionsPhysicalSizeY(this, in, __current);
            }
            case 46:
            {
                return ___setDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 47:
            {
                return ___setDimensionsTimeIncrement(this, in, __current);
            }
            case 48:
            {
                return ___setDimensionsWaveIncrement(this, in, __current);
            }
            case 49:
            {
                return ___setDimensionsWaveStart(this, in, __current);
            }
            case 50:
            {
                return ___setDisplayOptionsDisplay(this, in, __current);
            }
            case 51:
            {
                return ___setDisplayOptionsID(this, in, __current);
            }
            case 52:
            {
                return ___setDisplayOptionsZoom(this, in, __current);
            }
            case 53:
            {
                return ___setEllipseCx(this, in, __current);
            }
            case 54:
            {
                return ___setEllipseCy(this, in, __current);
            }
            case 55:
            {
                return ___setEllipseID(this, in, __current);
            }
            case 56:
            {
                return ___setEllipseRx(this, in, __current);
            }
            case 57:
            {
                return ___setEllipseRy(this, in, __current);
            }
            case 58:
            {
                return ___setEllipseTransform(this, in, __current);
            }
            case 59:
            {
                return ___setEmFilterLotNumber(this, in, __current);
            }
            case 60:
            {
                return ___setEmFilterManufacturer(this, in, __current);
            }
            case 61:
            {
                return ___setEmFilterModel(this, in, __current);
            }
            case 62:
            {
                return ___setEmFilterType(this, in, __current);
            }
            case 63:
            {
                return ___setExFilterLotNumber(this, in, __current);
            }
            case 64:
            {
                return ___setExFilterManufacturer(this, in, __current);
            }
            case 65:
            {
                return ___setExFilterModel(this, in, __current);
            }
            case 66:
            {
                return ___setExFilterType(this, in, __current);
            }
            case 67:
            {
                return ___setExperimentDescription(this, in, __current);
            }
            case 68:
            {
                return ___setExperimentExperimenterRef(this, in, __current);
            }
            case 69:
            {
                return ___setExperimentID(this, in, __current);
            }
            case 70:
            {
                return ___setExperimentType(this, in, __current);
            }
            case 71:
            {
                return ___setExperimenterEmail(this, in, __current);
            }
            case 72:
            {
                return ___setExperimenterFirstName(this, in, __current);
            }
            case 73:
            {
                return ___setExperimenterID(this, in, __current);
            }
            case 74:
            {
                return ___setExperimenterInstitution(this, in, __current);
            }
            case 75:
            {
                return ___setExperimenterLastName(this, in, __current);
            }
            case 76:
            {
                return ___setExperimenterMembershipGroup(this, in, __current);
            }
            case 77:
            {
                return ___setExperimenterOMEName(this, in, __current);
            }
            case 78:
            {
                return ___setFilamentType(this, in, __current);
            }
            case 79:
            {
                return ___setFilterFilterWheel(this, in, __current);
            }
            case 80:
            {
                return ___setFilterID(this, in, __current);
            }
            case 81:
            {
                return ___setFilterLotNumber(this, in, __current);
            }
            case 82:
            {
                return ___setFilterManufacturer(this, in, __current);
            }
            case 83:
            {
                return ___setFilterModel(this, in, __current);
            }
            case 84:
            {
                return ___setFilterSetDichroic(this, in, __current);
            }
            case 85:
            {
                return ___setFilterSetEmFilter(this, in, __current);
            }
            case 86:
            {
                return ___setFilterSetExFilter(this, in, __current);
            }
            case 87:
            {
                return ___setFilterSetID(this, in, __current);
            }
            case 88:
            {
                return ___setFilterSetLotNumber(this, in, __current);
            }
            case 89:
            {
                return ___setFilterSetManufacturer(this, in, __current);
            }
            case 90:
            {
                return ___setFilterSetModel(this, in, __current);
            }
            case 91:
            {
                return ___setFilterType(this, in, __current);
            }
            case 92:
            {
                return ___setGroupID(this, in, __current);
            }
            case 93:
            {
                return ___setGroupName(this, in, __current);
            }
            case 94:
            {
                return ___setImageAcquiredPixels(this, in, __current);
            }
            case 95:
            {
                return ___setImageCreationDate(this, in, __current);
            }
            case 96:
            {
                return ___setImageDefaultPixels(this, in, __current);
            }
            case 97:
            {
                return ___setImageDescription(this, in, __current);
            }
            case 98:
            {
                return ___setImageExperimentRef(this, in, __current);
            }
            case 99:
            {
                return ___setImageExperimenterRef(this, in, __current);
            }
            case 100:
            {
                return ___setImageGroupRef(this, in, __current);
            }
            case 101:
            {
                return ___setImageID(this, in, __current);
            }
            case 102:
            {
                return ___setImageInstrumentRef(this, in, __current);
            }
            case 103:
            {
                return ___setImageName(this, in, __current);
            }
            case 104:
            {
                return ___setImagingEnvironmentAirPressure(this, in, __current);
            }
            case 105:
            {
                return ___setImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 106:
            {
                return ___setImagingEnvironmentHumidity(this, in, __current);
            }
            case 107:
            {
                return ___setImagingEnvironmentTemperature(this, in, __current);
            }
            case 108:
            {
                return ___setInstrumentID(this, in, __current);
            }
            case 109:
            {
                return ___setLaserFrequencyMultiplication(this, in, __current);
            }
            case 110:
            {
                return ___setLaserLaserMedium(this, in, __current);
            }
            case 111:
            {
                return ___setLaserPockelCell(this, in, __current);
            }
            case 112:
            {
                return ___setLaserPulse(this, in, __current);
            }
            case 113:
            {
                return ___setLaserRepetitionRate(this, in, __current);
            }
            case 114:
            {
                return ___setLaserTuneable(this, in, __current);
            }
            case 115:
            {
                return ___setLaserType(this, in, __current);
            }
            case 116:
            {
                return ___setLaserWavelength(this, in, __current);
            }
            case 117:
            {
                return ___setLightSourceID(this, in, __current);
            }
            case 118:
            {
                return ___setLightSourceManufacturer(this, in, __current);
            }
            case 119:
            {
                return ___setLightSourceModel(this, in, __current);
            }
            case 120:
            {
                return ___setLightSourcePower(this, in, __current);
            }
            case 121:
            {
                return ___setLightSourceRefAttenuation(this, in, __current);
            }
            case 122:
            {
                return ___setLightSourceRefLightSource(this, in, __current);
            }
            case 123:
            {
                return ___setLightSourceRefWavelength(this, in, __current);
            }
            case 124:
            {
                return ___setLightSourceSerialNumber(this, in, __current);
            }
            case 125:
            {
                return ___setLightSourceSettingsAttenuation(this, in, __current);
            }
            case 126:
            {
                return ___setLightSourceSettingsLightSource(this, in, __current);
            }
            case 127:
            {
                return ___setLightSourceSettingsWavelength(this, in, __current);
            }
            case 128:
            {
                return ___setLineID(this, in, __current);
            }
            case 129:
            {
                return ___setLineTransform(this, in, __current);
            }
            case 130:
            {
                return ___setLineX1(this, in, __current);
            }
            case 131:
            {
                return ___setLineX2(this, in, __current);
            }
            case 132:
            {
                return ___setLineY1(this, in, __current);
            }
            case 133:
            {
                return ___setLineY2(this, in, __current);
            }
            case 134:
            {
                return ___setLogicalChannelContrastMethod(this, in, __current);
            }
            case 135:
            {
                return ___setLogicalChannelDetector(this, in, __current);
            }
            case 136:
            {
                return ___setLogicalChannelEmWave(this, in, __current);
            }
            case 137:
            {
                return ___setLogicalChannelExWave(this, in, __current);
            }
            case 138:
            {
                return ___setLogicalChannelFilterSet(this, in, __current);
            }
            case 139:
            {
                return ___setLogicalChannelFluor(this, in, __current);
            }
            case 140:
            {
                return ___setLogicalChannelID(this, in, __current);
            }
            case 141:
            {
                return ___setLogicalChannelIlluminationType(this, in, __current);
            }
            case 142:
            {
                return ___setLogicalChannelLightSource(this, in, __current);
            }
            case 143:
            {
                return ___setLogicalChannelMode(this, in, __current);
            }
            case 144:
            {
                return ___setLogicalChannelName(this, in, __current);
            }
            case 145:
            {
                return ___setLogicalChannelNdFilter(this, in, __current);
            }
            case 146:
            {
                return ___setLogicalChannelOTF(this, in, __current);
            }
            case 147:
            {
                return ___setLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 148:
            {
                return ___setLogicalChannelPinholeSize(this, in, __current);
            }
            case 149:
            {
                return ___setLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 150:
            {
                return ___setLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 151:
            {
                return ___setLogicalChannelSecondaryEmissionFilter(this, in, __current);
            }
            case 152:
            {
                return ___setLogicalChannelSecondaryExcitationFilter(this, in, __current);
            }
            case 153:
            {
                return ___setMaskHeight(this, in, __current);
            }
            case 154:
            {
                return ___setMaskID(this, in, __current);
            }
            case 155:
            {
                return ___setMaskPixelsBigEndian(this, in, __current);
            }
            case 156:
            {
                return ___setMaskPixelsBinData(this, in, __current);
            }
            case 157:
            {
                return ___setMaskPixelsExtendedPixelType(this, in, __current);
            }
            case 158:
            {
                return ___setMaskPixelsID(this, in, __current);
            }
            case 159:
            {
                return ___setMaskPixelsSizeX(this, in, __current);
            }
            case 160:
            {
                return ___setMaskPixelsSizeY(this, in, __current);
            }
            case 161:
            {
                return ___setMaskTransform(this, in, __current);
            }
            case 162:
            {
                return ___setMaskWidth(this, in, __current);
            }
            case 163:
            {
                return ___setMaskX(this, in, __current);
            }
            case 164:
            {
                return ___setMaskY(this, in, __current);
            }
            case 165:
            {
                return ___setMicrobeamManipulationExperimenterRef(this, in, __current);
            }
            case 166:
            {
                return ___setMicrobeamManipulationID(this, in, __current);
            }
            case 167:
            {
                return ___setMicrobeamManipulationRefID(this, in, __current);
            }
            case 168:
            {
                return ___setMicrobeamManipulationType(this, in, __current);
            }
            case 169:
            {
                return ___setMicroscopeID(this, in, __current);
            }
            case 170:
            {
                return ___setMicroscopeManufacturer(this, in, __current);
            }
            case 171:
            {
                return ___setMicroscopeModel(this, in, __current);
            }
            case 172:
            {
                return ___setMicroscopeSerialNumber(this, in, __current);
            }
            case 173:
            {
                return ___setMicroscopeType(this, in, __current);
            }
            case 174:
            {
                return ___setOTFBinaryFile(this, in, __current);
            }
            case 175:
            {
                return ___setOTFID(this, in, __current);
            }
            case 176:
            {
                return ___setOTFObjective(this, in, __current);
            }
            case 177:
            {
                return ___setOTFOpticalAxisAveraged(this, in, __current);
            }
            case 178:
            {
                return ___setOTFPixelType(this, in, __current);
            }
            case 179:
            {
                return ___setOTFSizeX(this, in, __current);
            }
            case 180:
            {
                return ___setOTFSizeY(this, in, __current);
            }
            case 181:
            {
                return ___setObjectiveCalibratedMagnification(this, in, __current);
            }
            case 182:
            {
                return ___setObjectiveCorrection(this, in, __current);
            }
            case 183:
            {
                return ___setObjectiveID(this, in, __current);
            }
            case 184:
            {
                return ___setObjectiveImmersion(this, in, __current);
            }
            case 185:
            {
                return ___setObjectiveIris(this, in, __current);
            }
            case 186:
            {
                return ___setObjectiveLensNA(this, in, __current);
            }
            case 187:
            {
                return ___setObjectiveManufacturer(this, in, __current);
            }
            case 188:
            {
                return ___setObjectiveModel(this, in, __current);
            }
            case 189:
            {
                return ___setObjectiveNominalMagnification(this, in, __current);
            }
            case 190:
            {
                return ___setObjectiveSerialNumber(this, in, __current);
            }
            case 191:
            {
                return ___setObjectiveSettingsCorrectionCollar(this, in, __current);
            }
            case 192:
            {
                return ___setObjectiveSettingsMedium(this, in, __current);
            }
            case 193:
            {
                return ___setObjectiveSettingsObjective(this, in, __current);
            }
            case 194:
            {
                return ___setObjectiveSettingsRefractiveIndex(this, in, __current);
            }
            case 195:
            {
                return ___setObjectiveWorkingDistance(this, in, __current);
            }
            case 196:
            {
                return ___setPathD(this, in, __current);
            }
            case 197:
            {
                return ___setPathID(this, in, __current);
            }
            case 198:
            {
                return ___setPixelsBigEndian(this, in, __current);
            }
            case 199:
            {
                return ___setPixelsDimensionOrder(this, in, __current);
            }
            case 200:
            {
                return ___setPixelsID(this, in, __current);
            }
            case 201:
            {
                return ___setPixelsPixelType(this, in, __current);
            }
            case 202:
            {
                return ___setPixelsSizeC(this, in, __current);
            }
            case 203:
            {
                return ___setPixelsSizeT(this, in, __current);
            }
            case 204:
            {
                return ___setPixelsSizeX(this, in, __current);
            }
            case 205:
            {
                return ___setPixelsSizeY(this, in, __current);
            }
            case 206:
            {
                return ___setPixelsSizeZ(this, in, __current);
            }
            case 207:
            {
                return ___setPlaneHashSHA1(this, in, __current);
            }
            case 208:
            {
                return ___setPlaneID(this, in, __current);
            }
            case 209:
            {
                return ___setPlaneTheC(this, in, __current);
            }
            case 210:
            {
                return ___setPlaneTheT(this, in, __current);
            }
            case 211:
            {
                return ___setPlaneTheZ(this, in, __current);
            }
            case 212:
            {
                return ___setPlaneTimingDeltaT(this, in, __current);
            }
            case 213:
            {
                return ___setPlaneTimingExposureTime(this, in, __current);
            }
            case 214:
            {
                return ___setPlateColumnNamingConvention(this, in, __current);
            }
            case 215:
            {
                return ___setPlateDescription(this, in, __current);
            }
            case 216:
            {
                return ___setPlateExternalIdentifier(this, in, __current);
            }
            case 217:
            {
                return ___setPlateID(this, in, __current);
            }
            case 218:
            {
                return ___setPlateName(this, in, __current);
            }
            case 219:
            {
                return ___setPlateRefID(this, in, __current);
            }
            case 220:
            {
                return ___setPlateRefSample(this, in, __current);
            }
            case 221:
            {
                return ___setPlateRefWell(this, in, __current);
            }
            case 222:
            {
                return ___setPlateRowNamingConvention(this, in, __current);
            }
            case 223:
            {
                return ___setPlateStatus(this, in, __current);
            }
            case 224:
            {
                return ___setPlateWellOriginX(this, in, __current);
            }
            case 225:
            {
                return ___setPlateWellOriginY(this, in, __current);
            }
            case 226:
            {
                return ___setPointCx(this, in, __current);
            }
            case 227:
            {
                return ___setPointCy(this, in, __current);
            }
            case 228:
            {
                return ___setPointID(this, in, __current);
            }
            case 229:
            {
                return ___setPointR(this, in, __current);
            }
            case 230:
            {
                return ___setPointTransform(this, in, __current);
            }
            case 231:
            {
                return ___setPolygonID(this, in, __current);
            }
            case 232:
            {
                return ___setPolygonPoints(this, in, __current);
            }
            case 233:
            {
                return ___setPolygonTransform(this, in, __current);
            }
            case 234:
            {
                return ___setPolylineID(this, in, __current);
            }
            case 235:
            {
                return ___setPolylinePoints(this, in, __current);
            }
            case 236:
            {
                return ___setPolylineTransform(this, in, __current);
            }
            case 237:
            {
                return ___setProjectDescription(this, in, __current);
            }
            case 238:
            {
                return ___setProjectExperimenterRef(this, in, __current);
            }
            case 239:
            {
                return ___setProjectGroupRef(this, in, __current);
            }
            case 240:
            {
                return ___setProjectID(this, in, __current);
            }
            case 241:
            {
                return ___setProjectName(this, in, __current);
            }
            case 242:
            {
                return ___setProjectRefID(this, in, __current);
            }
            case 243:
            {
                return ___setPumpLightSource(this, in, __current);
            }
            case 244:
            {
                return ___setROIID(this, in, __current);
            }
            case 245:
            {
                return ___setROIRefID(this, in, __current);
            }
            case 246:
            {
                return ___setROIT0(this, in, __current);
            }
            case 247:
            {
                return ___setROIT1(this, in, __current);
            }
            case 248:
            {
                return ___setROIX0(this, in, __current);
            }
            case 249:
            {
                return ___setROIX1(this, in, __current);
            }
            case 250:
            {
                return ___setROIY0(this, in, __current);
            }
            case 251:
            {
                return ___setROIY1(this, in, __current);
            }
            case 252:
            {
                return ___setROIZ0(this, in, __current);
            }
            case 253:
            {
                return ___setROIZ1(this, in, __current);
            }
            case 254:
            {
                return ___setReagentDescription(this, in, __current);
            }
            case 255:
            {
                return ___setReagentID(this, in, __current);
            }
            case 256:
            {
                return ___setReagentName(this, in, __current);
            }
            case 257:
            {
                return ___setReagentReagentIdentifier(this, in, __current);
            }
            case 258:
            {
                return ___setRectHeight(this, in, __current);
            }
            case 259:
            {
                return ___setRectID(this, in, __current);
            }
            case 260:
            {
                return ___setRectTransform(this, in, __current);
            }
            case 261:
            {
                return ___setRectWidth(this, in, __current);
            }
            case 262:
            {
                return ___setRectX(this, in, __current);
            }
            case 263:
            {
                return ___setRectY(this, in, __current);
            }
            case 264:
            {
                return ___setRegionID(this, in, __current);
            }
            case 265:
            {
                return ___setRegionName(this, in, __current);
            }
            case 266:
            {
                return ___setRegionTag(this, in, __current);
            }
            case 267:
            {
                return ___setRoiLinkDirection(this, in, __current);
            }
            case 268:
            {
                return ___setRoiLinkName(this, in, __current);
            }
            case 269:
            {
                return ___setRoiLinkRef(this, in, __current);
            }
            case 270:
            {
                return ___setScreenAcquisitionEndTime(this, in, __current);
            }
            case 271:
            {
                return ___setScreenAcquisitionID(this, in, __current);
            }
            case 272:
            {
                return ___setScreenAcquisitionStartTime(this, in, __current);
            }
            case 273:
            {
                return ___setScreenDescription(this, in, __current);
            }
            case 274:
            {
                return ___setScreenExtern(this, in, __current);
            }
            case 275:
            {
                return ___setScreenID(this, in, __current);
            }
            case 276:
            {
                return ___setScreenName(this, in, __current);
            }
            case 277:
            {
                return ___setScreenProtocolDescription(this, in, __current);
            }
            case 278:
            {
                return ___setScreenProtocolIdentifier(this, in, __current);
            }
            case 279:
            {
                return ___setScreenReagentSetDescription(this, in, __current);
            }
            case 280:
            {
                return ___setScreenReagentSetIdentifier(this, in, __current);
            }
            case 281:
            {
                return ___setScreenRefID(this, in, __current);
            }
            case 282:
            {
                return ___setScreenType(this, in, __current);
            }
            case 283:
            {
                return ___setShapeBaselineShift(this, in, __current);
            }
            case 284:
            {
                return ___setShapeDirection(this, in, __current);
            }
            case 285:
            {
                return ___setShapeFillColor(this, in, __current);
            }
            case 286:
            {
                return ___setShapeFillOpacity(this, in, __current);
            }
            case 287:
            {
                return ___setShapeFillRule(this, in, __current);
            }
            case 288:
            {
                return ___setShapeFontFamily(this, in, __current);
            }
            case 289:
            {
                return ___setShapeFontSize(this, in, __current);
            }
            case 290:
            {
                return ___setShapeFontStretch(this, in, __current);
            }
            case 291:
            {
                return ___setShapeFontStyle(this, in, __current);
            }
            case 292:
            {
                return ___setShapeFontVariant(this, in, __current);
            }
            case 293:
            {
                return ___setShapeFontWeight(this, in, __current);
            }
            case 294:
            {
                return ___setShapeG(this, in, __current);
            }
            case 295:
            {
                return ___setShapeGlyphOrientationVertical(this, in, __current);
            }
            case 296:
            {
                return ___setShapeID(this, in, __current);
            }
            case 297:
            {
                return ___setShapeLocked(this, in, __current);
            }
            case 298:
            {
                return ___setShapeStrokeAttribute(this, in, __current);
            }
            case 299:
            {
                return ___setShapeStrokeColor(this, in, __current);
            }
            case 300:
            {
                return ___setShapeStrokeDashArray(this, in, __current);
            }
            case 301:
            {
                return ___setShapeStrokeLineCap(this, in, __current);
            }
            case 302:
            {
                return ___setShapeStrokeLineJoin(this, in, __current);
            }
            case 303:
            {
                return ___setShapeStrokeMiterLimit(this, in, __current);
            }
            case 304:
            {
                return ___setShapeStrokeOpacity(this, in, __current);
            }
            case 305:
            {
                return ___setShapeStrokeWidth(this, in, __current);
            }
            case 306:
            {
                return ___setShapeText(this, in, __current);
            }
            case 307:
            {
                return ___setShapeTextAnchor(this, in, __current);
            }
            case 308:
            {
                return ___setShapeTextDecoration(this, in, __current);
            }
            case 309:
            {
                return ___setShapeTextFill(this, in, __current);
            }
            case 310:
            {
                return ___setShapeTextStroke(this, in, __current);
            }
            case 311:
            {
                return ___setShapeTheT(this, in, __current);
            }
            case 312:
            {
                return ___setShapeTheZ(this, in, __current);
            }
            case 313:
            {
                return ___setShapeVectorEffect(this, in, __current);
            }
            case 314:
            {
                return ___setShapeVisibility(this, in, __current);
            }
            case 315:
            {
                return ___setShapeWritingMode(this, in, __current);
            }
            case 316:
            {
                return ___setStageLabelName(this, in, __current);
            }
            case 317:
            {
                return ___setStageLabelX(this, in, __current);
            }
            case 318:
            {
                return ___setStageLabelY(this, in, __current);
            }
            case 319:
            {
                return ___setStageLabelZ(this, in, __current);
            }
            case 320:
            {
                return ___setStagePositionPositionX(this, in, __current);
            }
            case 321:
            {
                return ___setStagePositionPositionY(this, in, __current);
            }
            case 322:
            {
                return ___setStagePositionPositionZ(this, in, __current);
            }
            case 323:
            {
                return ___setThumbnailHref(this, in, __current);
            }
            case 324:
            {
                return ___setThumbnailID(this, in, __current);
            }
            case 325:
            {
                return ___setThumbnailMIMEtype(this, in, __current);
            }
            case 326:
            {
                return ___setTiffDataFileName(this, in, __current);
            }
            case 327:
            {
                return ___setTiffDataFirstC(this, in, __current);
            }
            case 328:
            {
                return ___setTiffDataFirstT(this, in, __current);
            }
            case 329:
            {
                return ___setTiffDataFirstZ(this, in, __current);
            }
            case 330:
            {
                return ___setTiffDataIFD(this, in, __current);
            }
            case 331:
            {
                return ___setTiffDataNumPlanes(this, in, __current);
            }
            case 332:
            {
                return ___setTiffDataUUID(this, in, __current);
            }
            case 333:
            {
                return ___setTransmittanceRangeCutIn(this, in, __current);
            }
            case 334:
            {
                return ___setTransmittanceRangeCutInTolerance(this, in, __current);
            }
            case 335:
            {
                return ___setTransmittanceRangeCutOut(this, in, __current);
            }
            case 336:
            {
                return ___setTransmittanceRangeCutOutTolerance(this, in, __current);
            }
            case 337:
            {
                return ___setTransmittanceRangeTransmittance(this, in, __current);
            }
            case 338:
            {
                return ___setUUID(this, in, __current);
            }
            case 339:
            {
                return ___setWellColumn(this, in, __current);
            }
            case 340:
            {
                return ___setWellExternalDescription(this, in, __current);
            }
            case 341:
            {
                return ___setWellExternalIdentifier(this, in, __current);
            }
            case 342:
            {
                return ___setWellID(this, in, __current);
            }
            case 343:
            {
                return ___setWellReagent(this, in, __current);
            }
            case 344:
            {
                return ___setWellRow(this, in, __current);
            }
            case 345:
            {
                return ___setWellSampleID(this, in, __current);
            }
            case 346:
            {
                return ___setWellSampleImageRef(this, in, __current);
            }
            case 347:
            {
                return ___setWellSampleIndex(this, in, __current);
            }
            case 348:
            {
                return ___setWellSamplePosX(this, in, __current);
            }
            case 349:
            {
                return ___setWellSamplePosY(this, in, __current);
            }
            case 350:
            {
                return ___setWellSampleRefID(this, in, __current);
            }
            case 351:
            {
                return ___setWellSampleTimepoint(this, in, __current);
            }
            case 352:
            {
                return ___setWellType(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::MetadataStore was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::MetadataStore was not generated with stream support";
        throw ex;
    }
}
