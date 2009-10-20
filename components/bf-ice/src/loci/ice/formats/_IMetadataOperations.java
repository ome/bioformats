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

public interface _IMetadataOperations
{
    String getOMEXML(Ice.Current __current);

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getDatasetCount(Ice.Current __current);

    int getDatasetRefCount(int imageIndex, Ice.Current __current);

    int getDetectorCount(int instrumentIndex, Ice.Current __current);

    int getDichroicCount(int instrumentIndex, Ice.Current __current);

    int getExperimentCount(Ice.Current __current);

    int getExperimenterCount(Ice.Current __current);

    int getExperimenterMembershipCount(int experimenterIndex, Ice.Current __current);

    int getFilterCount(int instrumentIndex, Ice.Current __current);

    int getFilterSetCount(int instrumentIndex, Ice.Current __current);

    int getGroupCount(Ice.Current __current);

    int getGroupRefCount(int experimenterIndex, Ice.Current __current);

    int getImageCount(Ice.Current __current);

    int getInstrumentCount(Ice.Current __current);

    int getLightSourceCount(int instrumentIndex, Ice.Current __current);

    int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    int getLogicalChannelCount(int imageIndex, Ice.Current __current);

    int getMicrobeamManipulationCount(int imageIndex, Ice.Current __current);

    int getMicrobeamManipulationRefCount(int experimentIndex, Ice.Current __current);

    int getOTFCount(int instrumentIndex, Ice.Current __current);

    int getObjectiveCount(int instrumentIndex, Ice.Current __current);

    int getPixelsCount(int imageIndex, Ice.Current __current);

    int getPlaneCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPlateCount(Ice.Current __current);

    int getPlateRefCount(int screenIndex, Ice.Current __current);

    int getProjectCount(Ice.Current __current);

    int getProjectRefCount(int datasetIndex, Ice.Current __current);

    int getROICount(int imageIndex, Ice.Current __current);

    int getROIRefCount(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    int getReagentCount(int screenIndex, Ice.Current __current);

    int getRegionCount(int imageIndex, Ice.Current __current);

    int getRoiLinkCount(int imageIndex, int roiIndex, Ice.Current __current);

    int getScreenCount(Ice.Current __current);

    int getScreenAcquisitionCount(int screenIndex, Ice.Current __current);

    int getScreenRefCount(int plateIndex, Ice.Current __current);

    int getShapeCount(int imageIndex, int roiIndex, Ice.Current __current);

    int getTiffDataCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getWellCount(int plateIndex, Ice.Current __current);

    int getWellSampleCount(int plateIndex, int wellIndex, Ice.Current __current);

    int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getUUID(Ice.Current __current);

    String getArcType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    String getCircleCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleR(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getContactExperimenter(int groupIndex, Ice.Current __current);

    String getDatasetDescription(int datasetIndex, Ice.Current __current);

    String getDatasetExperimenterRef(int datasetIndex, Ice.Current __current);

    String getDatasetGroupRef(int datasetIndex, Ice.Current __current);

    String getDatasetID(int datasetIndex, Ice.Current __current);

    boolean getDatasetLocked(int datasetIndex, Ice.Current __current);

    String getDatasetName(int datasetIndex, Ice.Current __current);

    String getDatasetRefID(int imageIndex, int datasetRefIndex, Ice.Current __current);

    double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, Ice.Current __current);

    double getDetectorGain(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorID(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorModel(int instrumentIndex, int detectorIndex, Ice.Current __current);

    double getDetectorOffset(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorType(int instrumentIndex, int detectorIndex, Ice.Current __current);

    double getDetectorVoltage(int instrumentIndex, int detectorIndex, Ice.Current __current);

    double getDetectorZoom(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getDichroicID(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicModel(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    double getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    double getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    double getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    double getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getDisplayOptionsDisplay(int imageIndex, Ice.Current __current);

    String getDisplayOptionsID(int imageIndex, Ice.Current __current);

    double getDisplayOptionsZoom(int imageIndex, Ice.Current __current);

    String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEmFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExperimentDescription(int experimentIndex, Ice.Current __current);

    String getExperimentExperimenterRef(int experimentIndex, Ice.Current __current);

    String getExperimentID(int experimentIndex, Ice.Current __current);

    String getExperimentType(int experimentIndex, Ice.Current __current);

    String getExperimenterEmail(int experimenterIndex, Ice.Current __current);

    String getExperimenterFirstName(int experimenterIndex, Ice.Current __current);

    String getExperimenterID(int experimenterIndex, Ice.Current __current);

    String getExperimenterInstitution(int experimenterIndex, Ice.Current __current);

    String getExperimenterLastName(int experimenterIndex, Ice.Current __current);

    String getExperimenterOMEName(int experimenterIndex, Ice.Current __current);

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, Ice.Current __current);

    String getFilamentType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getFilterFilterWheel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterID(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterSetDichroic(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetExFilter(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetID(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetModel(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getGroupID(int groupIndex, Ice.Current __current);

    String getGroupName(int groupIndex, Ice.Current __current);

    String getImageAcquiredPixels(int imageIndex, Ice.Current __current);

    String getImageCreationDate(int imageIndex, Ice.Current __current);

    String getImageDefaultPixels(int imageIndex, Ice.Current __current);

    String getImageDescription(int imageIndex, Ice.Current __current);

    String getImageExperimentRef(int imageIndex, Ice.Current __current);

    String getImageExperimenterRef(int imageIndex, Ice.Current __current);

    String getImageGroupRef(int imageIndex, Ice.Current __current);

    String getImageID(int imageIndex, Ice.Current __current);

    String getImageInstrumentRef(int imageIndex, Ice.Current __current);

    String getImageName(int imageIndex, Ice.Current __current);

    double getImagingEnvironmentAirPressure(int imageIndex, Ice.Current __current);

    double getImagingEnvironmentCO2Percent(int imageIndex, Ice.Current __current);

    double getImagingEnvironmentHumidity(int imageIndex, Ice.Current __current);

    double getImagingEnvironmentTemperature(int imageIndex, Ice.Current __current);

    String getInstrumentID(int instrumentIndex, Ice.Current __current);

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserPulse(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceID(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    double getLightSourcePower(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    double getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    double getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLineID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineX1(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineX2(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineY1(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineY2(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    double getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    byte[] getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, Ice.Current __current);

    String getMicroscopeID(int instrumentIndex, Ice.Current __current);

    String getMicroscopeManufacturer(int instrumentIndex, Ice.Current __current);

    String getMicroscopeModel(int instrumentIndex, Ice.Current __current);

    String getMicroscopeSerialNumber(int instrumentIndex, Ice.Current __current);

    String getMicroscopeType(int instrumentIndex, Ice.Current __current);

    String getOTFBinaryFile(int instrumentIndex, int otfIndex, Ice.Current __current);

    String getOTFID(int instrumentIndex, int otfIndex, Ice.Current __current);

    String getOTFObjective(int instrumentIndex, int otfIndex, Ice.Current __current);

    boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, Ice.Current __current);

    String getOTFPixelType(int instrumentIndex, int otfIndex, Ice.Current __current);

    int getOTFSizeX(int instrumentIndex, int otfIndex, Ice.Current __current);

    int getOTFSizeY(int instrumentIndex, int otfIndex, Ice.Current __current);

    double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveID(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    double getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveModel(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    double getObjectiveSettingsCorrectionCollar(int imageIndex, Ice.Current __current);

    String getObjectiveSettingsMedium(int imageIndex, Ice.Current __current);

    String getObjectiveSettingsObjective(int imageIndex, Ice.Current __current);

    double getObjectiveSettingsRefractiveIndex(int imageIndex, Ice.Current __current);

    String getPathD(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPathID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsID(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsPixelType(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeC(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeT(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    double getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    double getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getPlateColumnNamingConvention(int plateIndex, Ice.Current __current);

    String getPlateDescription(int plateIndex, Ice.Current __current);

    String getPlateExternalIdentifier(int plateIndex, Ice.Current __current);

    String getPlateID(int plateIndex, Ice.Current __current);

    String getPlateName(int plateIndex, Ice.Current __current);

    String getPlateRowNamingConvention(int plateIndex, Ice.Current __current);

    String getPlateStatus(int plateIndex, Ice.Current __current);

    double getPlateWellOriginX(int plateIndex, Ice.Current __current);

    double getPlateWellOriginY(int plateIndex, Ice.Current __current);

    String getPlateRefID(int screenIndex, int plateRefIndex, Ice.Current __current);

    int getPlateRefSample(int screenIndex, int plateRefIndex, Ice.Current __current);

    String getPlateRefWell(int screenIndex, int plateRefIndex, Ice.Current __current);

    String getPointCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointR(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylineID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getProjectDescription(int projectIndex, Ice.Current __current);

    String getProjectExperimenterRef(int projectIndex, Ice.Current __current);

    String getProjectGroupRef(int projectIndex, Ice.Current __current);

    String getProjectID(int projectIndex, Ice.Current __current);

    String getProjectName(int projectIndex, Ice.Current __current);

    String getProjectRefID(int datasetIndex, int projectRefIndex, Ice.Current __current);

    String getPumpLightSource(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getROIID(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ1(int imageIndex, int roiIndex, Ice.Current __current);

    String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, Ice.Current __current);

    String getReagentDescription(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentID(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentName(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex, Ice.Current __current);

    String getRectHeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRegionID(int imageIndex, int regionIndex, Ice.Current __current);

    String getRegionName(int imageIndex, int regionIndex, Ice.Current __current);

    String getRegionTag(int imageIndex, int regionIndex, Ice.Current __current);

    String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getScreenDescription(int screenIndex, Ice.Current __current);

    String getScreenExtern(int screenIndex, Ice.Current __current);

    String getScreenID(int screenIndex, Ice.Current __current);

    String getScreenName(int screenIndex, Ice.Current __current);

    String getScreenProtocolDescription(int screenIndex, Ice.Current __current);

    String getScreenProtocolIdentifier(int screenIndex, Ice.Current __current);

    String getScreenReagentSetDescription(int screenIndex, Ice.Current __current);

    String getScreenReagentSetIdentifier(int screenIndex, Ice.Current __current);

    String getScreenType(int screenIndex, Ice.Current __current);

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenRefID(int plateIndex, int screenRefIndex, Ice.Current __current);

    String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeG(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    double getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeText(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getStageLabelName(int imageIndex, Ice.Current __current);

    double getStageLabelX(int imageIndex, Ice.Current __current);

    double getStageLabelY(int imageIndex, Ice.Current __current);

    double getStageLabelZ(int imageIndex, Ice.Current __current);

    double getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    double getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    double getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getThumbnailHref(int imageIndex, Ice.Current __current);

    String getThumbnailID(int imageIndex, Ice.Current __current);

    String getThumbnailMIMEtype(int imageIndex, Ice.Current __current);

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getWellColumn(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalDescription(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalIdentifier(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellID(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellReagent(int plateIndex, int wellIndex, Ice.Current __current);

    int getWellRow(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellType(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    double getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    double getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Ice.Current __current);

    void createRoot(Ice.Current __current);

    void setUUID(String uuid, Ice.Current __current);

    void setArcType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setContactExperimenter(String experimenter, int groupIndex, Ice.Current __current);

    void setDatasetDescription(String description, int datasetIndex, Ice.Current __current);

    void setDatasetExperimenterRef(String experimenterRef, int datasetIndex, Ice.Current __current);

    void setDatasetGroupRef(String groupRef, int datasetIndex, Ice.Current __current);

    void setDatasetID(String id, int datasetIndex, Ice.Current __current);

    void setDatasetLocked(boolean locked, int datasetIndex, Ice.Current __current);

    void setDatasetName(String name, int datasetIndex, Ice.Current __current);

    void setDatasetRefID(String id, int imageIndex, int datasetRefIndex, Ice.Current __current);

    void setDetectorAmplificationGain(double amplificationGain, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorGain(double gain, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorID(String id, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorOffset(double offset, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorType(String type, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsGain(double gain, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsOffset(double offset, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsVoltage(double voltage, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDichroicID(String id, int instrumentIndex, int dichroicIndex, Ice.Current __current);

    void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, Ice.Current __current);

    void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, Ice.Current __current);

    void setDichroicModel(String model, int instrumentIndex, int dichroicIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeX(double physicalSizeX, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeY(double physicalSizeY, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeZ(double physicalSizeZ, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsTimeIncrement(double timeIncrement, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDisplayOptionsDisplay(String display, int imageIndex, Ice.Current __current);

    void setDisplayOptionsID(String id, int imageIndex, Ice.Current __current);

    void setDisplayOptionsZoom(double zoom, int imageIndex, Ice.Current __current);

    void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setEmFilterModel(String model, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setEmFilterType(String type, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setExFilterModel(String model, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setExFilterType(String type, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setExperimentDescription(String description, int experimentIndex, Ice.Current __current);

    void setExperimentExperimenterRef(String experimenterRef, int experimentIndex, Ice.Current __current);

    void setExperimentID(String id, int experimentIndex, Ice.Current __current);

    void setExperimentType(String type, int experimentIndex, Ice.Current __current);

    void setExperimenterEmail(String email, int experimenterIndex, Ice.Current __current);

    void setExperimenterFirstName(String firstName, int experimenterIndex, Ice.Current __current);

    void setExperimenterID(String id, int experimenterIndex, Ice.Current __current);

    void setExperimenterInstitution(String institution, int experimenterIndex, Ice.Current __current);

    void setExperimenterLastName(String lastName, int experimenterIndex, Ice.Current __current);

    void setExperimenterOMEName(String omeName, int experimenterIndex, Ice.Current __current);

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, Ice.Current __current);

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterID(String id, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterModel(String model, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterType(String type, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetID(String id, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, Ice.Current __current);

    void setGroupID(String id, int groupIndex, Ice.Current __current);

    void setGroupName(String name, int groupIndex, Ice.Current __current);

    void setImageAcquiredPixels(String acquiredPixels, int imageIndex, Ice.Current __current);

    void setImageCreationDate(String creationDate, int imageIndex, Ice.Current __current);

    void setImageDefaultPixels(String defaultPixels, int imageIndex, Ice.Current __current);

    void setImageDescription(String description, int imageIndex, Ice.Current __current);

    void setImageExperimentRef(String experimentRef, int imageIndex, Ice.Current __current);

    void setImageExperimenterRef(String experimenterRef, int imageIndex, Ice.Current __current);

    void setImageGroupRef(String groupRef, int imageIndex, Ice.Current __current);

    void setImageID(String id, int imageIndex, Ice.Current __current);

    void setImageInstrumentRef(String instrumentRef, int imageIndex, Ice.Current __current);

    void setImageName(String name, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentAirPressure(double airPressure, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentCO2Percent(double cO2Percent, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentHumidity(double humidity, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentTemperature(double temperature, int imageIndex, Ice.Current __current);

    void setInstrumentID(String id, int instrumentIndex, Ice.Current __current);

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourcePower(double power, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceRefAttenuation(double attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    void setLightSourceSettingsAttenuation(double attenuation, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelNdFilter(double ndFilter, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPinholeSize(double pinholeSize, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, Ice.Current __current);

    void setMicroscopeID(String id, int instrumentIndex, Ice.Current __current);

    void setMicroscopeManufacturer(String manufacturer, int instrumentIndex, Ice.Current __current);

    void setMicroscopeModel(String model, int instrumentIndex, Ice.Current __current);

    void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, Ice.Current __current);

    void setMicroscopeType(String type, int instrumentIndex, Ice.Current __current);

    void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFID(String id, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveWorkingDistance(double workingDistance, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveSettingsCorrectionCollar(double correctionCollar, int imageIndex, Ice.Current __current);

    void setObjectiveSettingsMedium(String medium, int imageIndex, Ice.Current __current);

    void setObjectiveSettingsObjective(String objective, int imageIndex, Ice.Current __current);

    void setObjectiveSettingsRefractiveIndex(double refractiveIndex, int imageIndex, Ice.Current __current);

    void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsID(String id, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTimingDeltaT(double deltaT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTimingExposureTime(double exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, Ice.Current __current);

    void setPlateDescription(String description, int plateIndex, Ice.Current __current);

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, Ice.Current __current);

    void setPlateID(String id, int plateIndex, Ice.Current __current);

    void setPlateName(String name, int plateIndex, Ice.Current __current);

    void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, Ice.Current __current);

    void setPlateStatus(String status, int plateIndex, Ice.Current __current);

    void setPlateWellOriginX(double wellOriginX, int plateIndex, Ice.Current __current);

    void setPlateWellOriginY(double wellOriginY, int plateIndex, Ice.Current __current);

    void setPlateRefID(String id, int screenIndex, int plateRefIndex, Ice.Current __current);

    void setPlateRefSample(int sample, int screenIndex, int plateRefIndex, Ice.Current __current);

    void setPlateRefWell(String well, int screenIndex, int plateRefIndex, Ice.Current __current);

    void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setProjectDescription(String description, int projectIndex, Ice.Current __current);

    void setProjectExperimenterRef(String experimenterRef, int projectIndex, Ice.Current __current);

    void setProjectGroupRef(String groupRef, int projectIndex, Ice.Current __current);

    void setProjectID(String id, int projectIndex, Ice.Current __current);

    void setProjectName(String name, int projectIndex, Ice.Current __current);

    void setProjectRefID(String id, int datasetIndex, int projectRefIndex, Ice.Current __current);

    void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setROIID(String id, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIT0(int t0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIT1(int t1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIX0(int x0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIX1(int x1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIY0(int y0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIY1(int y1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIZ0(int z0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIZ1(int z1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, Ice.Current __current);

    void setReagentDescription(String description, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentID(String id, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentName(String name, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, Ice.Current __current);

    void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setRegionID(String id, int imageIndex, int regionIndex, Ice.Current __current);

    void setRegionName(String name, int imageIndex, int regionIndex, Ice.Current __current);

    void setRegionTag(String tag, int imageIndex, int regionIndex, Ice.Current __current);

    void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    void setScreenDescription(String description, int screenIndex, Ice.Current __current);

    void setScreenExtern(String extern, int screenIndex, Ice.Current __current);

    void setScreenID(String id, int screenIndex, Ice.Current __current);

    void setScreenName(String name, int screenIndex, Ice.Current __current);

    void setScreenProtocolDescription(String protocolDescription, int screenIndex, Ice.Current __current);

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, Ice.Current __current);

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, Ice.Current __current);

    void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, Ice.Current __current);

    void setScreenType(String type, int screenIndex, Ice.Current __current);

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setScreenRefID(String id, int plateIndex, int screenRefIndex, Ice.Current __current);

    void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeOpacity(double strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    void setStageLabelName(String name, int imageIndex, Ice.Current __current);

    void setStageLabelX(double x, int imageIndex, Ice.Current __current);

    void setStageLabelY(double y, int imageIndex, Ice.Current __current);

    void setStageLabelZ(double z, int imageIndex, Ice.Current __current);

    void setStagePositionPositionX(double positionX, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setStagePositionPositionY(double positionY, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setStagePositionPositionZ(double positionZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setThumbnailHref(String href, int imageIndex, Ice.Current __current);

    void setThumbnailID(String id, int imageIndex, Ice.Current __current);

    void setThumbnailMIMEtype(String mimEtype, int imageIndex, Ice.Current __current);

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, Ice.Current __current);

    void setWellColumn(int column, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellID(String id, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellReagent(String reagent, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellRow(int row, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellType(String type, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSamplePosX(double posX, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSamplePosY(double posY, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Ice.Current __current);
}
