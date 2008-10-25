// **********************************************************************
//
// Copyright (c) 2003-2007 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.2.1

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
    setDisplayOptionsID(String id, int imageIndex)
    {
        setDisplayOptionsID(id, imageIndex, null);
    }

    public final void
    setDisplayOptionsProjectionZStart(int zStart, int imageIndex)
    {
        setDisplayOptionsProjectionZStart(zStart, imageIndex, null);
    }

    public final void
    setDisplayOptionsProjectionZStop(int zStop, int imageIndex)
    {
        setDisplayOptionsProjectionZStop(zStop, imageIndex, null);
    }

    public final void
    setDisplayOptionsTimeTStart(int tStart, int imageIndex)
    {
        setDisplayOptionsTimeTStart(tStart, imageIndex, null);
    }

    public final void
    setDisplayOptionsTimeTStop(int tStop, int imageIndex)
    {
        setDisplayOptionsTimeTStop(tStop, imageIndex, null);
    }

    public final void
    setDisplayOptionsZoom(float zoom, int imageIndex)
    {
        setDisplayOptionsZoom(zoom, imageIndex, null);
    }

    public final void
    setExperimentDescription(String description, int experimentIndex)
    {
        setExperimentDescription(description, experimentIndex, null);
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
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setFilamentType(type, instrumentIndex, lightSourceIndex, null);
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
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPulse(pulse, instrumentIndex, lightSourceIndex, null);
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
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, null);
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
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, null);
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
    setPlateStatus(String status, int plateIndex)
    {
        setPlateStatus(status, plateIndex, null);
    }

    public final void
    setROIID(String id, int imageIndex, int roiIndex)
    {
        setROIID(id, imageIndex, roiIndex, null);
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
    setScreenType(String type, int screenIndex)
    {
        setScreenType(type, screenIndex, null);
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
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellType(String type, int plateIndex, int wellIndex)
    {
        setWellType(type, plateIndex, wellIndex, null);
    }

    public static IceInternal.DispatchStatus
    ___getServant(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        MetadataStore __ret = __obj.getServant(__current);
        __os.writeObject(__ret);
        __os.writePendingObjects();
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOMEXML(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOMEXML(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___createRoot(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __obj.createRoot(__current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setUUID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String uuid;
        uuid = __is.readString();
        __obj.setUUID(uuid, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setArcType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setArcType(type, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setChannelComponentColorDomain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String colorDomain;
        colorDomain = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __obj.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setChannelComponentIndex(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int index;
        index = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __obj.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorGain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float gain;
        gain = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorGain(gain, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorID(id, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorModel(model, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorOffset(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float offset;
        offset = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorOffset(offset, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorType(type, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorVoltage(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float voltage;
        voltage = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __obj.setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorSettingsDetector(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String detector;
        detector = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorSettingsGain(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float gain;
        gain = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDetectorSettingsOffset(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float offset;
        offset = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsPhysicalSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float physicalSizeX;
        physicalSizeX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsPhysicalSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float physicalSizeY;
        physicalSizeY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsPhysicalSizeZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float physicalSizeZ;
        physicalSizeZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsTimeIncrement(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float timeIncrement;
        timeIncrement = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsWaveIncrement(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int waveIncrement;
        waveIncrement = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDimensionsWaveStart(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int waveStart;
        waveStart = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsID(id, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsZoom(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float zoom;
        zoom = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsZoom(zoom, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsProjectionZStart(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int zStart;
        zStart = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsProjectionZStart(zStart, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsProjectionZStop(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int zStop;
        zStop = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsProjectionZStop(zStop, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsTimeTStart(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int tStart;
        tStart = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsTimeTStart(tStart, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setDisplayOptionsTimeTStop(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int tStop;
        tStop = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setDisplayOptionsTimeTStop(tStop, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimentDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String description;
        description = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __obj.setExperimentDescription(description, experimentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __obj.setExperimentID(id, experimentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimentType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __obj.setExperimentType(type, experimentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterEmail(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String email;
        email = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __obj.setExperimenterEmail(email, experimenterIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterFirstName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String firstName;
        firstName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __obj.setExperimenterFirstName(firstName, experimenterIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __obj.setExperimenterID(id, experimenterIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterInstitution(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String institution;
        institution = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __obj.setExperimenterInstitution(institution, experimenterIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterLastName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String lastName;
        lastName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __obj.setExperimenterLastName(lastName, experimenterIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setExperimenterMembershipGroup(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String group;
        group = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int groupRefIndex;
        groupRefIndex = __is.readInt();
        __obj.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setFilamentType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setFilamentType(type, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageCreationDate(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String creationDate;
        creationDate = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageCreationDate(creationDate, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageDefaultPixels(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String defaultPixels;
        defaultPixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageDefaultPixels(defaultPixels, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String description;
        description = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageDescription(description, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageID(id, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageInstrumentRef(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String instrumentRef;
        instrumentRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageInstrumentRef(instrumentRef, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImageName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImageName(name, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImagingEnvironmentAirPressure(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float airPressure;
        airPressure = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImagingEnvironmentAirPressure(airPressure, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImagingEnvironmentCO2Percent(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float cO2Percent;
        cO2Percent = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImagingEnvironmentHumidity(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float humidity;
        humidity = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImagingEnvironmentHumidity(humidity, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setImagingEnvironmentTemperature(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float temperature;
        temperature = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setImagingEnvironmentTemperature(temperature, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setInstrumentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __obj.setInstrumentID(id, instrumentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserFrequencyMultiplication(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int frequencyMultiplication;
        frequencyMultiplication = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserLaserMedium(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String laserMedium;
        laserMedium = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserPulse(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String pulse;
        pulse = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserTuneable(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean tuneable;
        tuneable = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserType(type, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLaserWavelength(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int wavelength;
        wavelength = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLightSourceID(id, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLightSourceModel(model, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourcePower(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float power;
        power = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLightSourcePower(power, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __obj.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceSettingsAttenuation(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float attenuation;
        attenuation = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceSettingsLightSource(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLightSourceSettingsWavelength(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int wavelength;
        wavelength = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelContrastMethod(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String contrastMethod;
        contrastMethod = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelEmWave(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int emWave;
        emWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelExWave(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int exWave;
        exWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelFluor(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String fluor;
        fluor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelID(id, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelIlluminationType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String illuminationType;
        illuminationType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelMode(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String mode;
        mode = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelName(name, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelNdFilter(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float ndFilter;
        ndFilter = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelOTF(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String otf;
        otf = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelPhotometricInterpretation(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String photometricInterpretation;
        photometricInterpretation = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelPinholeSize(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float pinholeSize;
        pinholeSize = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelPockelCellSetting(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int pockelCellSetting;
        pockelCellSetting = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setLogicalChannelSamplesPerPixel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int samplesPerPixel;
        samplesPerPixel = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __obj.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFID(id, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFObjective(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String objective;
        objective = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFObjective(objective, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFOpticalAxisAveraged(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean opticalAxisAveraged;
        opticalAxisAveraged = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFPixelType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String pixelType;
        pixelType = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFPixelType(pixelType, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeX;
        sizeX = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFSizeX(sizeX, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOTFSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeY;
        sizeY = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __obj.setOTFSizeY(sizeY, instrumentIndex, otfIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveCalibratedMagnification(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float calibratedMagnification;
        calibratedMagnification = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveCorrection(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String correction;
        correction = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveID(id, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveImmersion(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String immersion;
        immersion = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveLensNA(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float lensNA;
        lensNA = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveManufacturer(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveModel(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveModel(model, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveNominalMagnification(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int nominalMagnification;
        nominalMagnification = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveSerialNumber(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setObjectiveWorkingDistance(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float workingDistance;
        workingDistance = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __obj.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsBigEndian(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean bigEndian;
        bigEndian = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsDimensionOrder(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String dimensionOrder;
        dimensionOrder = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsID(id, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsPixelType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String pixelType;
        pixelType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsSizeC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeC;
        sizeC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsSizeT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeT;
        sizeT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsSizeX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeX;
        sizeX = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsSizeY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeY;
        sizeY = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPixelsSizeZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int sizeZ;
        sizeZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __obj.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlaneTheC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int theC;
        theC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlaneTheT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int theT;
        theT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlaneTheZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int theZ;
        theZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlaneTimingDeltaT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float deltaT;
        deltaT = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlaneTimingExposureTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float exposureTime;
        exposureTime = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String description;
        description = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __obj.setPlateDescription(description, plateIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateExternalIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __obj.setPlateExternalIdentifier(externalIdentifier, plateIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __obj.setPlateID(id, plateIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __obj.setPlateName(name, plateIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateStatus(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String status;
        status = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __obj.setPlateStatus(status, plateIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setPlateRefID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __obj.setPlateRefID(id, screenIndex, plateRefIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIID(id, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIT0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int t0;
        t0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIT0(t0, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIT1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int t1;
        t1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIT1(t1, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIX0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int x0;
        x0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIX0(x0, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIX1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int x1;
        x1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIX1(x1, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIY0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int y0;
        y0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIY0(y0, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIY1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int y1;
        y1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIY1(y1, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIZ0(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int z0;
        z0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIZ0(z0, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setROIZ1(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int z1;
        z1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __obj.setROIZ1(z1, imageIndex, roiIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setReagentDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String description;
        description = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __obj.setReagentDescription(description, screenIndex, reagentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setReagentID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __obj.setReagentID(id, screenIndex, reagentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setReagentName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __obj.setReagentName(name, screenIndex, reagentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setReagentReagentIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String reagentIdentifier;
        reagentIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __obj.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenID(id, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenName(name, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenProtocolDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String protocolDescription;
        protocolDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenProtocolDescription(protocolDescription, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenProtocolIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String protocolIdentifier;
        protocolIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenReagentSetDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String reagentSetDescription;
        reagentSetDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenReagentSetDescription(reagentSetDescription, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __obj.setScreenType(type, screenIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenAcquisitionEndTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String endTime;
        endTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __obj.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenAcquisitionID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __obj.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setScreenAcquisitionStartTime(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String startTime;
        startTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __obj.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStageLabelName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setStageLabelName(name, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStageLabelX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float x;
        x = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setStageLabelX(x, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStageLabelY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float y;
        y = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setStageLabelY(y, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStageLabelZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float z;
        z = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __obj.setStageLabelZ(z, imageIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStagePositionPositionX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float positionX;
        positionX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStagePositionPositionY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float positionY;
        positionY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setStagePositionPositionZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float positionZ;
        positionZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __obj.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataFileName(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String fileName;
        fileName = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataFirstC(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int firstC;
        firstC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataFirstT(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int firstT;
        firstT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataFirstZ(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int firstZ;
        firstZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataIFD(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int ifd;
        ifd = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataNumPlanes(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int numPlanes;
        numPlanes = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setTiffDataUUID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String uuid;
        uuid = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __obj.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellColumn(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int column;
        column = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellColumn(column, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellExternalDescription(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String externalDescription;
        externalDescription = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellExternalDescription(externalDescription, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellExternalIdentifier(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellID(id, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellRow(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int row;
        row = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellRow(row, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellType(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String type;
        type = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __obj.setWellType(type, plateIndex, wellIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellSampleID(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __obj.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellSampleIndex(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int index;
        index = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __obj.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellSamplePosX(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float posX;
        posX = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __obj.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellSamplePosY(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        float posY;
        posY = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __obj.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setWellSampleTimepoint(MetadataStore __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int timepoint;
        timepoint = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __obj.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __current);
        return IceInternal.DispatchStatus.DispatchOK;
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
        "setDetectorGain",
        "setDetectorID",
        "setDetectorManufacturer",
        "setDetectorModel",
        "setDetectorOffset",
        "setDetectorSerialNumber",
        "setDetectorSettingsDetector",
        "setDetectorSettingsGain",
        "setDetectorSettingsOffset",
        "setDetectorType",
        "setDetectorVoltage",
        "setDimensionsPhysicalSizeX",
        "setDimensionsPhysicalSizeY",
        "setDimensionsPhysicalSizeZ",
        "setDimensionsTimeIncrement",
        "setDimensionsWaveIncrement",
        "setDimensionsWaveStart",
        "setDisplayOptionsID",
        "setDisplayOptionsProjectionZStart",
        "setDisplayOptionsProjectionZStop",
        "setDisplayOptionsTimeTStart",
        "setDisplayOptionsTimeTStop",
        "setDisplayOptionsZoom",
        "setExperimentDescription",
        "setExperimentID",
        "setExperimentType",
        "setExperimenterEmail",
        "setExperimenterFirstName",
        "setExperimenterID",
        "setExperimenterInstitution",
        "setExperimenterLastName",
        "setExperimenterMembershipGroup",
        "setFilamentType",
        "setImageCreationDate",
        "setImageDefaultPixels",
        "setImageDescription",
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
        "setLaserPulse",
        "setLaserTuneable",
        "setLaserType",
        "setLaserWavelength",
        "setLightSourceID",
        "setLightSourceManufacturer",
        "setLightSourceModel",
        "setLightSourcePower",
        "setLightSourceSerialNumber",
        "setLightSourceSettingsAttenuation",
        "setLightSourceSettingsLightSource",
        "setLightSourceSettingsWavelength",
        "setLogicalChannelContrastMethod",
        "setLogicalChannelEmWave",
        "setLogicalChannelExWave",
        "setLogicalChannelFluor",
        "setLogicalChannelID",
        "setLogicalChannelIlluminationType",
        "setLogicalChannelMode",
        "setLogicalChannelName",
        "setLogicalChannelNdFilter",
        "setLogicalChannelOTF",
        "setLogicalChannelPhotometricInterpretation",
        "setLogicalChannelPinholeSize",
        "setLogicalChannelPockelCellSetting",
        "setLogicalChannelSamplesPerPixel",
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
        "setObjectiveLensNA",
        "setObjectiveManufacturer",
        "setObjectiveModel",
        "setObjectiveNominalMagnification",
        "setObjectiveSerialNumber",
        "setObjectiveWorkingDistance",
        "setPixelsBigEndian",
        "setPixelsDimensionOrder",
        "setPixelsID",
        "setPixelsPixelType",
        "setPixelsSizeC",
        "setPixelsSizeT",
        "setPixelsSizeX",
        "setPixelsSizeY",
        "setPixelsSizeZ",
        "setPlaneTheC",
        "setPlaneTheT",
        "setPlaneTheZ",
        "setPlaneTimingDeltaT",
        "setPlaneTimingExposureTime",
        "setPlateDescription",
        "setPlateExternalIdentifier",
        "setPlateID",
        "setPlateName",
        "setPlateRefID",
        "setPlateStatus",
        "setROIID",
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
        "setScreenAcquisitionEndTime",
        "setScreenAcquisitionID",
        "setScreenAcquisitionStartTime",
        "setScreenID",
        "setScreenName",
        "setScreenProtocolDescription",
        "setScreenProtocolIdentifier",
        "setScreenReagentSetDescription",
        "setScreenType",
        "setStageLabelName",
        "setStageLabelX",
        "setStageLabelY",
        "setStageLabelZ",
        "setStagePositionPositionX",
        "setStagePositionPositionY",
        "setStagePositionPositionZ",
        "setTiffDataFileName",
        "setTiffDataFirstC",
        "setTiffDataFirstT",
        "setTiffDataFirstZ",
        "setTiffDataIFD",
        "setTiffDataNumPlanes",
        "setTiffDataUUID",
        "setUUID",
        "setWellColumn",
        "setWellExternalDescription",
        "setWellExternalIdentifier",
        "setWellID",
        "setWellRow",
        "setWellSampleID",
        "setWellSampleIndex",
        "setWellSamplePosX",
        "setWellSamplePosY",
        "setWellSampleTimepoint",
        "setWellType"
    };

    public IceInternal.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            return IceInternal.DispatchStatus.DispatchOperationNotExist;
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
                return ___setDetectorGain(this, in, __current);
            }
            case 11:
            {
                return ___setDetectorID(this, in, __current);
            }
            case 12:
            {
                return ___setDetectorManufacturer(this, in, __current);
            }
            case 13:
            {
                return ___setDetectorModel(this, in, __current);
            }
            case 14:
            {
                return ___setDetectorOffset(this, in, __current);
            }
            case 15:
            {
                return ___setDetectorSerialNumber(this, in, __current);
            }
            case 16:
            {
                return ___setDetectorSettingsDetector(this, in, __current);
            }
            case 17:
            {
                return ___setDetectorSettingsGain(this, in, __current);
            }
            case 18:
            {
                return ___setDetectorSettingsOffset(this, in, __current);
            }
            case 19:
            {
                return ___setDetectorType(this, in, __current);
            }
            case 20:
            {
                return ___setDetectorVoltage(this, in, __current);
            }
            case 21:
            {
                return ___setDimensionsPhysicalSizeX(this, in, __current);
            }
            case 22:
            {
                return ___setDimensionsPhysicalSizeY(this, in, __current);
            }
            case 23:
            {
                return ___setDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 24:
            {
                return ___setDimensionsTimeIncrement(this, in, __current);
            }
            case 25:
            {
                return ___setDimensionsWaveIncrement(this, in, __current);
            }
            case 26:
            {
                return ___setDimensionsWaveStart(this, in, __current);
            }
            case 27:
            {
                return ___setDisplayOptionsID(this, in, __current);
            }
            case 28:
            {
                return ___setDisplayOptionsProjectionZStart(this, in, __current);
            }
            case 29:
            {
                return ___setDisplayOptionsProjectionZStop(this, in, __current);
            }
            case 30:
            {
                return ___setDisplayOptionsTimeTStart(this, in, __current);
            }
            case 31:
            {
                return ___setDisplayOptionsTimeTStop(this, in, __current);
            }
            case 32:
            {
                return ___setDisplayOptionsZoom(this, in, __current);
            }
            case 33:
            {
                return ___setExperimentDescription(this, in, __current);
            }
            case 34:
            {
                return ___setExperimentID(this, in, __current);
            }
            case 35:
            {
                return ___setExperimentType(this, in, __current);
            }
            case 36:
            {
                return ___setExperimenterEmail(this, in, __current);
            }
            case 37:
            {
                return ___setExperimenterFirstName(this, in, __current);
            }
            case 38:
            {
                return ___setExperimenterID(this, in, __current);
            }
            case 39:
            {
                return ___setExperimenterInstitution(this, in, __current);
            }
            case 40:
            {
                return ___setExperimenterLastName(this, in, __current);
            }
            case 41:
            {
                return ___setExperimenterMembershipGroup(this, in, __current);
            }
            case 42:
            {
                return ___setFilamentType(this, in, __current);
            }
            case 43:
            {
                return ___setImageCreationDate(this, in, __current);
            }
            case 44:
            {
                return ___setImageDefaultPixels(this, in, __current);
            }
            case 45:
            {
                return ___setImageDescription(this, in, __current);
            }
            case 46:
            {
                return ___setImageID(this, in, __current);
            }
            case 47:
            {
                return ___setImageInstrumentRef(this, in, __current);
            }
            case 48:
            {
                return ___setImageName(this, in, __current);
            }
            case 49:
            {
                return ___setImagingEnvironmentAirPressure(this, in, __current);
            }
            case 50:
            {
                return ___setImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 51:
            {
                return ___setImagingEnvironmentHumidity(this, in, __current);
            }
            case 52:
            {
                return ___setImagingEnvironmentTemperature(this, in, __current);
            }
            case 53:
            {
                return ___setInstrumentID(this, in, __current);
            }
            case 54:
            {
                return ___setLaserFrequencyMultiplication(this, in, __current);
            }
            case 55:
            {
                return ___setLaserLaserMedium(this, in, __current);
            }
            case 56:
            {
                return ___setLaserPulse(this, in, __current);
            }
            case 57:
            {
                return ___setLaserTuneable(this, in, __current);
            }
            case 58:
            {
                return ___setLaserType(this, in, __current);
            }
            case 59:
            {
                return ___setLaserWavelength(this, in, __current);
            }
            case 60:
            {
                return ___setLightSourceID(this, in, __current);
            }
            case 61:
            {
                return ___setLightSourceManufacturer(this, in, __current);
            }
            case 62:
            {
                return ___setLightSourceModel(this, in, __current);
            }
            case 63:
            {
                return ___setLightSourcePower(this, in, __current);
            }
            case 64:
            {
                return ___setLightSourceSerialNumber(this, in, __current);
            }
            case 65:
            {
                return ___setLightSourceSettingsAttenuation(this, in, __current);
            }
            case 66:
            {
                return ___setLightSourceSettingsLightSource(this, in, __current);
            }
            case 67:
            {
                return ___setLightSourceSettingsWavelength(this, in, __current);
            }
            case 68:
            {
                return ___setLogicalChannelContrastMethod(this, in, __current);
            }
            case 69:
            {
                return ___setLogicalChannelEmWave(this, in, __current);
            }
            case 70:
            {
                return ___setLogicalChannelExWave(this, in, __current);
            }
            case 71:
            {
                return ___setLogicalChannelFluor(this, in, __current);
            }
            case 72:
            {
                return ___setLogicalChannelID(this, in, __current);
            }
            case 73:
            {
                return ___setLogicalChannelIlluminationType(this, in, __current);
            }
            case 74:
            {
                return ___setLogicalChannelMode(this, in, __current);
            }
            case 75:
            {
                return ___setLogicalChannelName(this, in, __current);
            }
            case 76:
            {
                return ___setLogicalChannelNdFilter(this, in, __current);
            }
            case 77:
            {
                return ___setLogicalChannelOTF(this, in, __current);
            }
            case 78:
            {
                return ___setLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 79:
            {
                return ___setLogicalChannelPinholeSize(this, in, __current);
            }
            case 80:
            {
                return ___setLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 81:
            {
                return ___setLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 82:
            {
                return ___setOTFID(this, in, __current);
            }
            case 83:
            {
                return ___setOTFObjective(this, in, __current);
            }
            case 84:
            {
                return ___setOTFOpticalAxisAveraged(this, in, __current);
            }
            case 85:
            {
                return ___setOTFPixelType(this, in, __current);
            }
            case 86:
            {
                return ___setOTFSizeX(this, in, __current);
            }
            case 87:
            {
                return ___setOTFSizeY(this, in, __current);
            }
            case 88:
            {
                return ___setObjectiveCalibratedMagnification(this, in, __current);
            }
            case 89:
            {
                return ___setObjectiveCorrection(this, in, __current);
            }
            case 90:
            {
                return ___setObjectiveID(this, in, __current);
            }
            case 91:
            {
                return ___setObjectiveImmersion(this, in, __current);
            }
            case 92:
            {
                return ___setObjectiveLensNA(this, in, __current);
            }
            case 93:
            {
                return ___setObjectiveManufacturer(this, in, __current);
            }
            case 94:
            {
                return ___setObjectiveModel(this, in, __current);
            }
            case 95:
            {
                return ___setObjectiveNominalMagnification(this, in, __current);
            }
            case 96:
            {
                return ___setObjectiveSerialNumber(this, in, __current);
            }
            case 97:
            {
                return ___setObjectiveWorkingDistance(this, in, __current);
            }
            case 98:
            {
                return ___setPixelsBigEndian(this, in, __current);
            }
            case 99:
            {
                return ___setPixelsDimensionOrder(this, in, __current);
            }
            case 100:
            {
                return ___setPixelsID(this, in, __current);
            }
            case 101:
            {
                return ___setPixelsPixelType(this, in, __current);
            }
            case 102:
            {
                return ___setPixelsSizeC(this, in, __current);
            }
            case 103:
            {
                return ___setPixelsSizeT(this, in, __current);
            }
            case 104:
            {
                return ___setPixelsSizeX(this, in, __current);
            }
            case 105:
            {
                return ___setPixelsSizeY(this, in, __current);
            }
            case 106:
            {
                return ___setPixelsSizeZ(this, in, __current);
            }
            case 107:
            {
                return ___setPlaneTheC(this, in, __current);
            }
            case 108:
            {
                return ___setPlaneTheT(this, in, __current);
            }
            case 109:
            {
                return ___setPlaneTheZ(this, in, __current);
            }
            case 110:
            {
                return ___setPlaneTimingDeltaT(this, in, __current);
            }
            case 111:
            {
                return ___setPlaneTimingExposureTime(this, in, __current);
            }
            case 112:
            {
                return ___setPlateDescription(this, in, __current);
            }
            case 113:
            {
                return ___setPlateExternalIdentifier(this, in, __current);
            }
            case 114:
            {
                return ___setPlateID(this, in, __current);
            }
            case 115:
            {
                return ___setPlateName(this, in, __current);
            }
            case 116:
            {
                return ___setPlateRefID(this, in, __current);
            }
            case 117:
            {
                return ___setPlateStatus(this, in, __current);
            }
            case 118:
            {
                return ___setROIID(this, in, __current);
            }
            case 119:
            {
                return ___setROIT0(this, in, __current);
            }
            case 120:
            {
                return ___setROIT1(this, in, __current);
            }
            case 121:
            {
                return ___setROIX0(this, in, __current);
            }
            case 122:
            {
                return ___setROIX1(this, in, __current);
            }
            case 123:
            {
                return ___setROIY0(this, in, __current);
            }
            case 124:
            {
                return ___setROIY1(this, in, __current);
            }
            case 125:
            {
                return ___setROIZ0(this, in, __current);
            }
            case 126:
            {
                return ___setROIZ1(this, in, __current);
            }
            case 127:
            {
                return ___setReagentDescription(this, in, __current);
            }
            case 128:
            {
                return ___setReagentID(this, in, __current);
            }
            case 129:
            {
                return ___setReagentName(this, in, __current);
            }
            case 130:
            {
                return ___setReagentReagentIdentifier(this, in, __current);
            }
            case 131:
            {
                return ___setScreenAcquisitionEndTime(this, in, __current);
            }
            case 132:
            {
                return ___setScreenAcquisitionID(this, in, __current);
            }
            case 133:
            {
                return ___setScreenAcquisitionStartTime(this, in, __current);
            }
            case 134:
            {
                return ___setScreenID(this, in, __current);
            }
            case 135:
            {
                return ___setScreenName(this, in, __current);
            }
            case 136:
            {
                return ___setScreenProtocolDescription(this, in, __current);
            }
            case 137:
            {
                return ___setScreenProtocolIdentifier(this, in, __current);
            }
            case 138:
            {
                return ___setScreenReagentSetDescription(this, in, __current);
            }
            case 139:
            {
                return ___setScreenType(this, in, __current);
            }
            case 140:
            {
                return ___setStageLabelName(this, in, __current);
            }
            case 141:
            {
                return ___setStageLabelX(this, in, __current);
            }
            case 142:
            {
                return ___setStageLabelY(this, in, __current);
            }
            case 143:
            {
                return ___setStageLabelZ(this, in, __current);
            }
            case 144:
            {
                return ___setStagePositionPositionX(this, in, __current);
            }
            case 145:
            {
                return ___setStagePositionPositionY(this, in, __current);
            }
            case 146:
            {
                return ___setStagePositionPositionZ(this, in, __current);
            }
            case 147:
            {
                return ___setTiffDataFileName(this, in, __current);
            }
            case 148:
            {
                return ___setTiffDataFirstC(this, in, __current);
            }
            case 149:
            {
                return ___setTiffDataFirstT(this, in, __current);
            }
            case 150:
            {
                return ___setTiffDataFirstZ(this, in, __current);
            }
            case 151:
            {
                return ___setTiffDataIFD(this, in, __current);
            }
            case 152:
            {
                return ___setTiffDataNumPlanes(this, in, __current);
            }
            case 153:
            {
                return ___setTiffDataUUID(this, in, __current);
            }
            case 154:
            {
                return ___setUUID(this, in, __current);
            }
            case 155:
            {
                return ___setWellColumn(this, in, __current);
            }
            case 156:
            {
                return ___setWellExternalDescription(this, in, __current);
            }
            case 157:
            {
                return ___setWellExternalIdentifier(this, in, __current);
            }
            case 158:
            {
                return ___setWellID(this, in, __current);
            }
            case 159:
            {
                return ___setWellRow(this, in, __current);
            }
            case 160:
            {
                return ___setWellSampleID(this, in, __current);
            }
            case 161:
            {
                return ___setWellSampleIndex(this, in, __current);
            }
            case 162:
            {
                return ___setWellSamplePosX(this, in, __current);
            }
            case 163:
            {
                return ___setWellSamplePosY(this, in, __current);
            }
            case 164:
            {
                return ___setWellSampleTimepoint(this, in, __current);
            }
            case 165:
            {
                return ___setWellType(this, in, __current);
            }
        }

        assert(false);
        return IceInternal.DispatchStatus.DispatchOperationNotExist;
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
