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

public interface _MetadataStoreDel extends Ice._ObjectDel
{
    MetadataStore getServant(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOMEXML(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void createRoot(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setUUID(String uuid, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setContactExperimenter(String experimenter, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetDescription(String description, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetExperimenterRef(String experimenterRef, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetGroupRef(String groupRef, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetID(String id, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetLocked(boolean locked, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetName(String name, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetRefID(String id, int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicID(String id, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicModel(String model, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsDisplay(String display, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentExperimenterRef(String experimenterRef, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterOMEName(String omeName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterID(String id, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetID(String id, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setGroupID(String id, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setGroupName(String name, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageAcquiredPixels(String acquiredPixels, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageExperimentRef(String experimentRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageExperimenterRef(String experimenterRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageGroupRef(String groupRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeManufacturer(String manufacturer, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeModel(String model, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeType(String type, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsMedium(String medium, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsObjective(String objective, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateWellOriginX(double wellOriginX, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateWellOriginY(double wellOriginY, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefSample(int sample, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefWell(String well, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectDescription(String description, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectExperimenterRef(String experimenterRef, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectGroupRef(String groupRef, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectID(String id, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectName(String name, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectRefID(String id, int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionID(String id, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionName(String name, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionTag(String tag, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenDescription(String description, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenExtern(String extern, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenRefID(String id, int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailHref(String href, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailMIMEtype(String mimEtype, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellReagent(String reagent, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
