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

public interface MetadataStorePrx extends Ice.ObjectPrx
{
    public MetadataStore getServant();
    public MetadataStore getServant(java.util.Map<String, String> __ctx);

    public String getOMEXML();
    public String getOMEXML(java.util.Map<String, String> __ctx);

    public void createRoot();
    public void createRoot(java.util.Map<String, String> __ctx);

    public void setUUID(String uuid);
    public void setUUID(String uuid, java.util.Map<String, String> __ctx);

    public void setArcType(String type, int instrumentIndex, int lightSourceIndex);
    public void setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex);
    public void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex);
    public void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex);
    public void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setContactExperimenter(String experimenter, int groupIndex);
    public void setContactExperimenter(String experimenter, int groupIndex, java.util.Map<String, String> __ctx);

    public void setDatasetDescription(String description, int datasetIndex);
    public void setDatasetDescription(String description, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex);
    public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetGroupRef(String groupRef, int datasetIndex);
    public void setDatasetGroupRef(String groupRef, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetID(String id, int datasetIndex);
    public void setDatasetID(String id, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetLocked(boolean locked, int datasetIndex);
    public void setDatasetLocked(boolean locked, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetName(String name, int datasetIndex);
    public void setDatasetName(String name, int datasetIndex, java.util.Map<String, String> __ctx);

    public void setDatasetRefID(String id, int imageIndex, int datasetRefIndex);
    public void setDatasetRefID(String id, int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx);

    public void setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex);
    public void setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex);
    public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorID(String id, int instrumentIndex, int detectorIndex);
    public void setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);
    public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorModel(String model, int instrumentIndex, int detectorIndex);
    public void setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex);
    public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);
    public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorType(String type, int instrumentIndex, int detectorIndex);
    public void setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex);
    public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex);
    public void setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDichroicID(String id, int instrumentIndex, int dichroicIndex);
    public void setDichroicID(String id, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);
    public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);
    public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);
    public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex);
    public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);
    public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);
    public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsDisplay(String display, int imageIndex);
    public void setDisplayOptionsDisplay(String display, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsID(String id, int imageIndex);
    public void setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsZoom(float zoom, int imageIndex);
    public void setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx);

    public void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);
    public void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);
    public void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setEmFilterModel(String model, int instrumentIndex, int filterIndex);
    public void setEmFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setEmFilterType(String type, int instrumentIndex, int filterIndex);
    public void setEmFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);
    public void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);
    public void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setExFilterModel(String model, int instrumentIndex, int filterIndex);
    public void setExFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setExFilterType(String type, int instrumentIndex, int filterIndex);
    public void setExFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setExperimentDescription(String description, int experimentIndex);
    public void setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex);
    public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimentID(String id, int experimentIndex);
    public void setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimentType(String type, int experimentIndex);
    public void setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterEmail(String email, int experimenterIndex);
    public void setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterFirstName(String firstName, int experimenterIndex);
    public void setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterID(String id, int experimenterIndex);
    public void setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterInstitution(String institution, int experimenterIndex);
    public void setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterLastName(String lastName, int experimenterIndex);
    public void setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterOMEName(String omeName, int experimenterIndex);
    public void setExperimenterOMEName(String omeName, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);
    public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx);

    public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);
    public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);
    public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterID(String id, int instrumentIndex, int filterIndex);
    public void setFilterID(String id, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);
    public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);
    public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterModel(String model, int instrumentIndex, int filterIndex);
    public void setFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterType(String type, int instrumentIndex, int filterIndex);
    public void setFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex);
    public void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex);
    public void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex);
    public void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex);
    public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex);
    public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex);
    public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex);
    public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public void setGroupID(String id, int groupIndex);
    public void setGroupID(String id, int groupIndex, java.util.Map<String, String> __ctx);

    public void setGroupName(String name, int groupIndex);
    public void setGroupName(String name, int groupIndex, java.util.Map<String, String> __ctx);

    public void setImageAcquiredPixels(String acquiredPixels, int imageIndex);
    public void setImageAcquiredPixels(String acquiredPixels, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageCreationDate(String creationDate, int imageIndex);
    public void setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageDefaultPixels(String defaultPixels, int imageIndex);
    public void setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageDescription(String description, int imageIndex);
    public void setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageExperimentRef(String experimentRef, int imageIndex);
    public void setImageExperimentRef(String experimentRef, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageExperimenterRef(String experimenterRef, int imageIndex);
    public void setImageExperimenterRef(String experimenterRef, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageGroupRef(String groupRef, int imageIndex);
    public void setImageGroupRef(String groupRef, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageID(String id, int imageIndex);
    public void setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageInstrumentRef(String instrumentRef, int imageIndex);
    public void setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageName(String name, int imageIndex);
    public void setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex);
    public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex);
    public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentHumidity(float humidity, int imageIndex);
    public void setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentTemperature(float temperature, int imageIndex);
    public void setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx);

    public void setInstrumentID(String id, int instrumentIndex);
    public void setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);
    public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);
    public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex);
    public void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);
    public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex);
    public void setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex);
    public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserType(String type, int instrumentIndex, int lightSourceIndex);
    public void setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);
    public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex);
    public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public void setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex);
    public void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex);
    public void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex);
    public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex);
    public void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex);
    public void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex);
    public void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx);

    public void setMicroscopeID(String id, int instrumentIndex);
    public void setMicroscopeID(String id, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex);
    public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setMicroscopeModel(String model, int instrumentIndex);
    public void setMicroscopeModel(String model, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex);
    public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setMicroscopeType(String type, int instrumentIndex);
    public void setMicroscopeType(String type, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex);
    public void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFID(String id, int instrumentIndex, int otfIndex);
    public void setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFObjective(String objective, int instrumentIndex, int otfIndex);
    public void setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);
    public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);
    public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);
    public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);
    public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex);
    public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);
    public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);
    public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);
    public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex);
    public void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex);
    public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);
    public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);
    public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);
    public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);
    public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex);
    public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex);
    public void setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSettingsMedium(String medium, int imageIndex);
    public void setObjectiveSettingsMedium(String medium, int imageIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSettingsObjective(String objective, int imageIndex);
    public void setObjectiveSettingsObjective(String objective, int imageIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex);
    public void setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex, java.util.Map<String, String> __ctx);

    public void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex);
    public void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex);
    public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);
    public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsID(String id, int imageIndex, int pixelsIndex);
    public void setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);
    public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);
    public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);
    public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);
    public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);
    public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);
    public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex);
    public void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateDescription(String description, int plateIndex);
    public void setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);
    public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateID(String id, int plateIndex);
    public void setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateName(String name, int plateIndex);
    public void setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex);
    public void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateStatus(String status, int plateIndex);
    public void setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateWellOriginX(double wellOriginX, int plateIndex);
    public void setPlateWellOriginX(double wellOriginX, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateWellOriginY(double wellOriginY, int plateIndex);
    public void setPlateWellOriginY(double wellOriginY, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateRefID(String id, int screenIndex, int plateRefIndex);
    public void setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public void setPlateRefSample(int sample, int screenIndex, int plateRefIndex);
    public void setPlateRefSample(int sample, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public void setPlateRefWell(String well, int screenIndex, int plateRefIndex);
    public void setPlateRefWell(String well, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex);
    public void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex);
    public void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex);
    public void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setProjectDescription(String description, int projectIndex);
    public void setProjectDescription(String description, int projectIndex, java.util.Map<String, String> __ctx);

    public void setProjectExperimenterRef(String experimenterRef, int projectIndex);
    public void setProjectExperimenterRef(String experimenterRef, int projectIndex, java.util.Map<String, String> __ctx);

    public void setProjectGroupRef(String groupRef, int projectIndex);
    public void setProjectGroupRef(String groupRef, int projectIndex, java.util.Map<String, String> __ctx);

    public void setProjectID(String id, int projectIndex);
    public void setProjectID(String id, int projectIndex, java.util.Map<String, String> __ctx);

    public void setProjectName(String name, int projectIndex);
    public void setProjectName(String name, int projectIndex, java.util.Map<String, String> __ctx);

    public void setProjectRefID(String id, int datasetIndex, int projectRefIndex);
    public void setProjectRefID(String id, int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx);

    public void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex);
    public void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setROIID(String id, int imageIndex, int roiIndex);
    public void setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIT0(int t0, int imageIndex, int roiIndex);
    public void setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIT1(int t1, int imageIndex, int roiIndex);
    public void setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIX0(int x0, int imageIndex, int roiIndex);
    public void setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIX1(int x1, int imageIndex, int roiIndex);
    public void setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIY0(int y0, int imageIndex, int roiIndex);
    public void setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIY1(int y1, int imageIndex, int roiIndex);
    public void setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIZ0(int z0, int imageIndex, int roiIndex);
    public void setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIZ1(int z1, int imageIndex, int roiIndex);
    public void setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex);
    public void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx);

    public void setReagentDescription(String description, int screenIndex, int reagentIndex);
    public void setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentID(String id, int screenIndex, int reagentIndex);
    public void setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentName(String name, int screenIndex, int reagentIndex);
    public void setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);
    public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex);
    public void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setRegionID(String id, int imageIndex, int regionIndex);
    public void setRegionID(String id, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public void setRegionName(String name, int imageIndex, int regionIndex);
    public void setRegionName(String name, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public void setRegionTag(String tag, int imageIndex, int regionIndex);
    public void setRegionTag(String tag, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex);
    public void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex);
    public void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex);
    public void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public void setScreenDescription(String description, int screenIndex);
    public void setScreenDescription(String description, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenExtern(String extern, int screenIndex);
    public void setScreenExtern(String extern, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenID(String id, int screenIndex);
    public void setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenName(String name, int screenIndex);
    public void setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenProtocolDescription(String protocolDescription, int screenIndex);
    public void setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);
    public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);
    public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);
    public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenType(String type, int screenIndex);
    public void setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setScreenRefID(String id, int plateIndex, int screenRefIndex);
    public void setScreenRefID(String id, int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx);

    public void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex);
    public void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelName(String name, int imageIndex);
    public void setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelX(float x, int imageIndex);
    public void setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelY(float y, int imageIndex);
    public void setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelZ(float z, int imageIndex);
    public void setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setThumbnailHref(String href, int imageIndex);
    public void setThumbnailHref(String href, int imageIndex, java.util.Map<String, String> __ctx);

    public void setThumbnailID(String id, int imageIndex);
    public void setThumbnailID(String id, int imageIndex, java.util.Map<String, String> __ctx);

    public void setThumbnailMIMEtype(String mimEtype, int imageIndex);
    public void setThumbnailMIMEtype(String mimEtype, int imageIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex);
    public void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex);
    public void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex);
    public void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex);
    public void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex);
    public void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public void setWellColumn(int column, int plateIndex, int wellIndex);
    public void setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);
    public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);
    public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellID(String id, int plateIndex, int wellIndex);
    public void setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellReagent(String reagent, int plateIndex, int wellIndex);
    public void setWellReagent(String reagent, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellRow(int row, int plateIndex, int wellIndex);
    public void setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellType(String type, int plateIndex, int wellIndex);
    public void setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);
    public void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx);
}
