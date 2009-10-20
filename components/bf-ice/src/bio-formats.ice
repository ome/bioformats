//
// bio-formats.ice
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2009 UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Oct 20, 2009 12:57:18 AM EDT
 *
 *-----------------------------------------------------------------------------
 */

[["java:package:loci.ice"]]

module formats {

  sequence<byte> ByteSeq;
  sequence<ByteSeq> ByteByteSeq;
  sequence<short> ShortSeq;
  sequence<ShortSeq> ShortShortSeq;
  sequence<int> IntSeq;
  sequence<string> StringSeq;

  interface IMetadata;

  interface IFormatReader {
    void setId(string id);

    //const int MUST_GROUP = 0;
    //const int CAN_GROUP = 1;
    //const int CANNOT_GROUP = 2;

    bool isThisType(string name, bool open);
    //bool isThisType2(ByteSeq block);
    int getImageCount();
    bool isRGB();
    int getSizeX();
    int getSizeY();
    int getSizeZ();
    int getSizeC();
    int getSizeT();
    int getPixelType();
    int getEffectiveSizeC();
    int getRGBChannelCount();
    bool isIndexed();
    bool isFalseColor();
    ByteByteSeq get8BitLookupTable();
    ShortShortSeq get16BitLookupTable();
    IntSeq getChannelDimLengths();
    StringSeq getChannelDimTypes();
    int getThumbSizeX();
    int getThumbSizeY();
    bool isLittleEndian();
    string getDimensionOrder();
    bool isOrderCertain();
    bool isInterleaved();
    bool isInterleavedSubC(int subC);
    //ByteSeq openBytes1(int no);
    ByteSeq openBytes(int no, int x, int y, int width, int height);
    //ByteSeq openBytes3(int no, ByteSeq buf);
    //ByteSeq openBytes4(int no, ByteSeq buf,
    //  int x, int y, int width, int height);
    ByteSeq openThumbBytes(int no);
    void closeFile(bool fileOnly);
    string getFormat();
    StringSeq getSuffixes();
    void close();
    int getSeriesCount();
    void setSeries(int no);
    int getSeries();
    void setNormalized(bool normalize);
    bool isNormalized();
    void setMetadataCollected(bool collect);
    bool isMetadataCollected();
    void setOriginalMetadataPopulated(bool populate);
    bool isOriginalMetadataPopulated();
    void setGroupFiles(bool group);
    bool isGroupFiles();
    bool isMetadataComplete();
    int fileGroupOption(string id);
    StringSeq getUsedFiles();
    string getCurrentFile();
    int getIndex(int z, int c, int t);
    IntSeq getZCTCoords(int index);
    //Object getMetadataValue(string field);
    //Hashtable getMetadata();
    //CoreMetadata getCoreMetadata();
    void setMetadataFiltered(bool filter);
    bool isMetadataFiltered();
    void setMetadataStore(IMetadata* store);
    //IMetadata* getMetadataStore();
    //Object getMetadataStoreRoot();
    //IFormatReader[] getUnderlyingReaders();
  };

  interface IFormatWriter {
    void setId(string id);
    void close();
    void saveBytes1(ByteSeq bytes, bool last);
    void saveBytes2(ByteSeq bytes, int series, bool lastInSeries, bool last);
    bool canDoStacks();
    void setMetadataRetrieve(IMetadata* r);
    IMetadata getMetadataRetrieve();
    void setFramesPerSecond(int rate);
    int getFramesPerSecond();
    StringSeq getCompressionTypes();
    IntSeq getPixelTypes();
    bool isSupportedType(int type);
    void setCompression(string compress);
    bool isThisType(string name);
    string getFormat();
    StringSeq getSuffixes();
  };

  interface IMetadata {
    // -- MetadataRetrieve methods --

    string getOMEXML();

    // - Entity counting -

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex);
    int getDatasetCount();
    int getDatasetRefCount(int imageIndex);
    int getDetectorCount(int instrumentIndex);
    int getDichroicCount(int instrumentIndex);
    int getExperimentCount();
    int getExperimenterCount();
    int getExperimenterMembershipCount(int experimenterIndex);
    int getFilterCount(int instrumentIndex);
    int getFilterSetCount(int instrumentIndex);
    int getGroupCount();
    int getGroupRefCount(int experimenterIndex);
    int getImageCount();
    int getInstrumentCount();
    int getLightSourceCount(int instrumentIndex);
    int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex);
    int getLogicalChannelCount(int imageIndex);
    int getMicrobeamManipulationCount(int imageIndex);
    int getMicrobeamManipulationRefCount(int experimentIndex);
    int getOTFCount(int instrumentIndex);
    int getObjectiveCount(int instrumentIndex);
    int getPixelsCount(int imageIndex);
    int getPlaneCount(int imageIndex, int pixelsIndex);
    int getPlateCount();
    int getPlateRefCount(int screenIndex);
    int getProjectCount();
    int getProjectRefCount(int datasetIndex);
    int getROICount(int imageIndex);
    int getROIRefCount(int imageIndex, int microbeamManipulationIndex);
    int getReagentCount(int screenIndex);
    int getRegionCount(int imageIndex);
    int getRoiLinkCount(int imageIndex, int roiIndex);
    int getScreenCount();
    int getScreenAcquisitionCount(int screenIndex);
    int getScreenRefCount(int plateIndex);
    int getShapeCount(int imageIndex, int roiIndex);
    int getTiffDataCount(int imageIndex, int pixelsIndex);
    int getWellCount(int plateIndex);
    int getWellSampleCount(int plateIndex, int wellIndex);
    int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex);

    // - Entity retrieval -

    string getUUID();

    // - Arc property retrieval -

    string getArcType(int instrumentIndex, int lightSourceIndex);

    // - ChannelComponent property retrieval -

    string getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    string getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    // - Circle property retrieval -

    string getCircleCx(int imageIndex, int roiIndex, int shapeIndex);
    string getCircleCy(int imageIndex, int roiIndex, int shapeIndex);
    string getCircleID(int imageIndex, int roiIndex, int shapeIndex);
    string getCircleR(int imageIndex, int roiIndex, int shapeIndex);
    string getCircleTransform(int imageIndex, int roiIndex, int shapeIndex);

    // - Contact property retrieval -

    string getContactExperimenter(int groupIndex);

    // - Dataset property retrieval -

    string getDatasetDescription(int datasetIndex);
    string getDatasetExperimenterRef(int datasetIndex);
    string getDatasetGroupRef(int datasetIndex);
    string getDatasetID(int datasetIndex);
    bool getDatasetLocked(int datasetIndex);
    string getDatasetName(int datasetIndex);

    // - DatasetRef property retrieval -

    string getDatasetRefID(int imageIndex, int datasetRefIndex);

    // - Detector property retrieval -

    double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);
    double getDetectorGain(int instrumentIndex, int detectorIndex);
    string getDetectorID(int instrumentIndex, int detectorIndex);
    string getDetectorManufacturer(int instrumentIndex, int detectorIndex);
    string getDetectorModel(int instrumentIndex, int detectorIndex);
    double getDetectorOffset(int instrumentIndex, int detectorIndex);
    string getDetectorSerialNumber(int instrumentIndex, int detectorIndex);
    string getDetectorType(int instrumentIndex, int detectorIndex);
    double getDetectorVoltage(int instrumentIndex, int detectorIndex);
    double getDetectorZoom(int instrumentIndex, int detectorIndex);

    // - DetectorSettings property retrieval -

    string getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex);
    string getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);
    double getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);
    double getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);
    double getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex);
    double getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex);

    // - Dichroic property retrieval -

    string getDichroicID(int instrumentIndex, int dichroicIndex);
    string getDichroicLotNumber(int instrumentIndex, int dichroicIndex);
    string getDichroicManufacturer(int instrumentIndex, int dichroicIndex);
    string getDichroicModel(int instrumentIndex, int dichroicIndex);

    // - Dimensions property retrieval -

    double getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);
    double getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);
    double getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);
    double getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);
    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);
    int getDimensionsWaveStart(int imageIndex, int pixelsIndex);

    // - DisplayOptions property retrieval -

    string getDisplayOptionsDisplay(int imageIndex);
    string getDisplayOptionsID(int imageIndex);
    double getDisplayOptionsZoom(int imageIndex);

    // - Ellipse property retrieval -

    string getEllipseCx(int imageIndex, int roiIndex, int shapeIndex);
    string getEllipseCy(int imageIndex, int roiIndex, int shapeIndex);
    string getEllipseID(int imageIndex, int roiIndex, int shapeIndex);
    string getEllipseRx(int imageIndex, int roiIndex, int shapeIndex);
    string getEllipseRy(int imageIndex, int roiIndex, int shapeIndex);
    string getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex);

    // - EmFilter property retrieval -

    string getEmFilterLotNumber(int instrumentIndex, int filterIndex);
    string getEmFilterManufacturer(int instrumentIndex, int filterIndex);
    string getEmFilterModel(int instrumentIndex, int filterIndex);
    string getEmFilterType(int instrumentIndex, int filterIndex);

    // - ExFilter property retrieval -

    string getExFilterLotNumber(int instrumentIndex, int filterIndex);
    string getExFilterManufacturer(int instrumentIndex, int filterIndex);
    string getExFilterModel(int instrumentIndex, int filterIndex);
    string getExFilterType(int instrumentIndex, int filterIndex);

    // - Experiment property retrieval -

    string getExperimentDescription(int experimentIndex);
    string getExperimentExperimenterRef(int experimentIndex);
    string getExperimentID(int experimentIndex);
    string getExperimentType(int experimentIndex);

    // - Experimenter property retrieval -

    string getExperimenterEmail(int experimenterIndex);
    string getExperimenterFirstName(int experimenterIndex);
    string getExperimenterID(int experimenterIndex);
    string getExperimenterInstitution(int experimenterIndex);
    string getExperimenterLastName(int experimenterIndex);
    string getExperimenterOMEName(int experimenterIndex);

    // - ExperimenterMembership property retrieval -

    string getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

    // - Filament property retrieval -

    string getFilamentType(int instrumentIndex, int lightSourceIndex);

    // - Filter property retrieval -

    string getFilterFilterWheel(int instrumentIndex, int filterIndex);
    string getFilterID(int instrumentIndex, int filterIndex);
    string getFilterLotNumber(int instrumentIndex, int filterIndex);
    string getFilterManufacturer(int instrumentIndex, int filterIndex);
    string getFilterModel(int instrumentIndex, int filterIndex);
    string getFilterType(int instrumentIndex, int filterIndex);

    // - FilterSet property retrieval -

    string getFilterSetDichroic(int instrumentIndex, int filterSetIndex);
    string getFilterSetEmFilter(int instrumentIndex, int filterSetIndex);
    string getFilterSetExFilter(int instrumentIndex, int filterSetIndex);
    string getFilterSetID(int instrumentIndex, int filterSetIndex);
    string getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);
    string getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);
    string getFilterSetModel(int instrumentIndex, int filterSetIndex);

    // - Group property retrieval -

    string getGroupID(int groupIndex);
    string getGroupName(int groupIndex);

    // - GroupRef property retrieval -


    // - Image property retrieval -

    string getImageAcquiredPixels(int imageIndex);
    string getImageCreationDate(int imageIndex);
    string getImageDefaultPixels(int imageIndex);
    string getImageDescription(int imageIndex);
    string getImageExperimentRef(int imageIndex);
    string getImageExperimenterRef(int imageIndex);
    string getImageGroupRef(int imageIndex);
    string getImageID(int imageIndex);
    string getImageInstrumentRef(int imageIndex);
    string getImageName(int imageIndex);

    // - ImagingEnvironment property retrieval -

    double getImagingEnvironmentAirPressure(int imageIndex);
    double getImagingEnvironmentCO2Percent(int imageIndex);
    double getImagingEnvironmentHumidity(int imageIndex);
    double getImagingEnvironmentTemperature(int imageIndex);

    // - Instrument property retrieval -

    string getInstrumentID(int instrumentIndex);

    // - Laser property retrieval -

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);
    string getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);
    bool getLaserPockelCell(int instrumentIndex, int lightSourceIndex);
    string getLaserPulse(int instrumentIndex, int lightSourceIndex);
    double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);
    bool getLaserTuneable(int instrumentIndex, int lightSourceIndex);
    string getLaserType(int instrumentIndex, int lightSourceIndex);
    int getLaserWavelength(int instrumentIndex, int lightSourceIndex);

    // - LightSource property retrieval -

    string getLightSourceID(int instrumentIndex, int lightSourceIndex);
    string getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);
    string getLightSourceModel(int instrumentIndex, int lightSourceIndex);
    double getLightSourcePower(int instrumentIndex, int lightSourceIndex);
    string getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

    // - LightSourceRef property retrieval -

    double getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    string getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    // - LightSourceSettings property retrieval -

    double getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);
    string getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);
    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

    // - Line property retrieval -

    string getLineID(int imageIndex, int roiIndex, int shapeIndex);
    string getLineTransform(int imageIndex, int roiIndex, int shapeIndex);
    string getLineX1(int imageIndex, int roiIndex, int shapeIndex);
    string getLineX2(int imageIndex, int roiIndex, int shapeIndex);
    string getLineY1(int imageIndex, int roiIndex, int shapeIndex);
    string getLineY2(int imageIndex, int roiIndex, int shapeIndex);

    // - LogicalChannel property retrieval -

    string getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelDetector(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelID(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelMode(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelName(int imageIndex, int logicalChannelIndex);
    double getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);
    double getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex);

    // - Mask property retrieval -

    string getMaskHeight(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskID(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskTransform(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskWidth(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskX(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskY(int imageIndex, int roiIndex, int shapeIndex);

    // - MaskPixels property retrieval -

    bool getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex);
    ByteSeq getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex);
    string getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex);
    int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex);
    int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex);

    // - MicrobeamManipulation property retrieval -

    string getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex);
    string getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex);
    string getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex);

    // - MicrobeamManipulationRef property retrieval -

    string getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex);

    // - Microscope property retrieval -

    string getMicroscopeID(int instrumentIndex);
    string getMicroscopeManufacturer(int instrumentIndex);
    string getMicroscopeModel(int instrumentIndex);
    string getMicroscopeSerialNumber(int instrumentIndex);
    string getMicroscopeType(int instrumentIndex);

    // - OTF property retrieval -

    string getOTFBinaryFile(int instrumentIndex, int otfIndex);
    string getOTFID(int instrumentIndex, int otfIndex);
    string getOTFObjective(int instrumentIndex, int otfIndex);
    bool getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);
    string getOTFPixelType(int instrumentIndex, int otfIndex);
    int getOTFSizeX(int instrumentIndex, int otfIndex);
    int getOTFSizeY(int instrumentIndex, int otfIndex);

    // - Objective property retrieval -

    double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);
    string getObjectiveCorrection(int instrumentIndex, int objectiveIndex);
    string getObjectiveID(int instrumentIndex, int objectiveIndex);
    string getObjectiveImmersion(int instrumentIndex, int objectiveIndex);
    bool getObjectiveIris(int instrumentIndex, int objectiveIndex);
    double getObjectiveLensNA(int instrumentIndex, int objectiveIndex);
    string getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);
    string getObjectiveModel(int instrumentIndex, int objectiveIndex);
    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);
    string getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);
    double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

    // - ObjectiveSettings property retrieval -

    double getObjectiveSettingsCorrectionCollar(int imageIndex);
    string getObjectiveSettingsMedium(int imageIndex);
    string getObjectiveSettingsObjective(int imageIndex);
    double getObjectiveSettingsRefractiveIndex(int imageIndex);

    // - Path property retrieval -

    string getPathD(int imageIndex, int roiIndex, int shapeIndex);
    string getPathID(int imageIndex, int roiIndex, int shapeIndex);

    // - Pixels property retrieval -

    bool getPixelsBigEndian(int imageIndex, int pixelsIndex);
    string getPixelsDimensionOrder(int imageIndex, int pixelsIndex);
    string getPixelsID(int imageIndex, int pixelsIndex);
    string getPixelsPixelType(int imageIndex, int pixelsIndex);
    int getPixelsSizeC(int imageIndex, int pixelsIndex);
    int getPixelsSizeT(int imageIndex, int pixelsIndex);
    int getPixelsSizeX(int imageIndex, int pixelsIndex);
    int getPixelsSizeY(int imageIndex, int pixelsIndex);
    int getPixelsSizeZ(int imageIndex, int pixelsIndex);

    // - Plane property retrieval -

    string getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex);
    string getPlaneID(int imageIndex, int pixelsIndex, int planeIndex);
    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);
    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);
    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

    // - PlaneTiming property retrieval -

    double getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);
    double getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

    // - Plate property retrieval -

    string getPlateColumnNamingConvention(int plateIndex);
    string getPlateDescription(int plateIndex);
    string getPlateExternalIdentifier(int plateIndex);
    string getPlateID(int plateIndex);
    string getPlateName(int plateIndex);
    string getPlateRowNamingConvention(int plateIndex);
    string getPlateStatus(int plateIndex);
    double getPlateWellOriginX(int plateIndex);
    double getPlateWellOriginY(int plateIndex);

    // - PlateRef property retrieval -

    string getPlateRefID(int screenIndex, int plateRefIndex);
    int getPlateRefSample(int screenIndex, int plateRefIndex);
    string getPlateRefWell(int screenIndex, int plateRefIndex);

    // - Point property retrieval -

    string getPointCx(int imageIndex, int roiIndex, int shapeIndex);
    string getPointCy(int imageIndex, int roiIndex, int shapeIndex);
    string getPointID(int imageIndex, int roiIndex, int shapeIndex);
    string getPointR(int imageIndex, int roiIndex, int shapeIndex);
    string getPointTransform(int imageIndex, int roiIndex, int shapeIndex);

    // - Polygon property retrieval -

    string getPolygonID(int imageIndex, int roiIndex, int shapeIndex);
    string getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex);
    string getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex);

    // - Polyline property retrieval -

    string getPolylineID(int imageIndex, int roiIndex, int shapeIndex);
    string getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex);
    string getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex);

    // - Project property retrieval -

    string getProjectDescription(int projectIndex);
    string getProjectExperimenterRef(int projectIndex);
    string getProjectGroupRef(int projectIndex);
    string getProjectID(int projectIndex);
    string getProjectName(int projectIndex);

    // - ProjectRef property retrieval -

    string getProjectRefID(int datasetIndex, int projectRefIndex);

    // - Pump property retrieval -

    string getPumpLightSource(int instrumentIndex, int lightSourceIndex);

    // - ROI property retrieval -

    string getROIID(int imageIndex, int roiIndex);
    int getROIT0(int imageIndex, int roiIndex);
    int getROIT1(int imageIndex, int roiIndex);
    int getROIX0(int imageIndex, int roiIndex);
    int getROIX1(int imageIndex, int roiIndex);
    int getROIY0(int imageIndex, int roiIndex);
    int getROIY1(int imageIndex, int roiIndex);
    int getROIZ0(int imageIndex, int roiIndex);
    int getROIZ1(int imageIndex, int roiIndex);

    // - ROIRef property retrieval -

    string getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

    // - Reagent property retrieval -

    string getReagentDescription(int screenIndex, int reagentIndex);
    string getReagentID(int screenIndex, int reagentIndex);
    string getReagentName(int screenIndex, int reagentIndex);
    string getReagentReagentIdentifier(int screenIndex, int reagentIndex);

    // - Rect property retrieval -

    string getRectHeight(int imageIndex, int roiIndex, int shapeIndex);
    string getRectID(int imageIndex, int roiIndex, int shapeIndex);
    string getRectTransform(int imageIndex, int roiIndex, int shapeIndex);
    string getRectWidth(int imageIndex, int roiIndex, int shapeIndex);
    string getRectX(int imageIndex, int roiIndex, int shapeIndex);
    string getRectY(int imageIndex, int roiIndex, int shapeIndex);

    // - Region property retrieval -

    string getRegionID(int imageIndex, int regionIndex);
    string getRegionName(int imageIndex, int regionIndex);
    string getRegionTag(int imageIndex, int regionIndex);

    // - RoiLink property retrieval -

    string getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex);
    string getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex);
    string getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex);

    // - Screen property retrieval -

    string getScreenDescription(int screenIndex);
    string getScreenExtern(int screenIndex);
    string getScreenID(int screenIndex);
    string getScreenName(int screenIndex);
    string getScreenProtocolDescription(int screenIndex);
    string getScreenProtocolIdentifier(int screenIndex);
    string getScreenReagentSetDescription(int screenIndex);
    string getScreenReagentSetIdentifier(int screenIndex);
    string getScreenType(int screenIndex);

    // - ScreenAcquisition property retrieval -

    string getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);
    string getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);
    string getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

    // - ScreenRef property retrieval -

    string getScreenRefID(int plateIndex, int screenRefIndex);

    // - Shape property retrieval -

    string getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeDirection(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeG(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeID(int imageIndex, int roiIndex, int shapeIndex);
    bool getShapeLocked(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex);
    double getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeText(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex);
    int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex);
    bool getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex);
    string getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex);

    // - StageLabel property retrieval -

    string getStageLabelName(int imageIndex);
    double getStageLabelX(int imageIndex);
    double getStageLabelY(int imageIndex);
    double getStageLabelZ(int imageIndex);

    // - StagePosition property retrieval -

    double getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);
    double getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);
    double getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

    // - Thumbnail property retrieval -

    string getThumbnailHref(int imageIndex);
    string getThumbnailID(int imageIndex);
    string getThumbnailMIMEtype(int imageIndex);

    // - TiffData property retrieval -

    string getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);
    string getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

    // - TransmittanceRange property retrieval -

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

    // - Well property retrieval -

    int getWellColumn(int plateIndex, int wellIndex);
    string getWellExternalDescription(int plateIndex, int wellIndex);
    string getWellExternalIdentifier(int plateIndex, int wellIndex);
    string getWellID(int plateIndex, int wellIndex);
    string getWellReagent(int plateIndex, int wellIndex);
    int getWellRow(int plateIndex, int wellIndex);
    string getWellType(int plateIndex, int wellIndex);

    // - WellSample property retrieval -

    string getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);
    string getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);
    double getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);
    double getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

    // - WellSampleRef property retrieval -

    string getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);

    // -- MetadataStore methods --

    void createRoot();
    //void setRoot(Object root);
    //Object getRoot();

    // - Entity storage -

    void setUUID(string uuid);

    // - Arc property storage -
    void setArcType(string type, int instrumentIndex, int lightSourceIndex);

    // - ChannelComponent property storage -
    void setChannelComponentColorDomain(string colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    void setChannelComponentPixels(string pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    // - Circle property storage -
    void setCircleCx(string cx, int imageIndex, int roiIndex, int shapeIndex);
    void setCircleCy(string cy, int imageIndex, int roiIndex, int shapeIndex);
    void setCircleID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setCircleR(string r, int imageIndex, int roiIndex, int shapeIndex);
    void setCircleTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);

    // - Contact property storage -
    void setContactExperimenter(string experimenter, int groupIndex);

    // - Dataset property storage -
    void setDatasetDescription(string description, int datasetIndex);
    void setDatasetExperimenterRef(string experimenterRef, int datasetIndex);
    void setDatasetGroupRef(string groupRef, int datasetIndex);
    void setDatasetID(string id, int datasetIndex);
    void setDatasetLocked(bool locked, int datasetIndex);
    void setDatasetName(string name, int datasetIndex);

    // - DatasetRef property storage -
    void setDatasetRefID(string id, int imageIndex, int datasetRefIndex);

    // - Detector property storage -
    void setDetectorAmplificationGain(double amplificationGain, int instrumentIndex, int detectorIndex);
    void setDetectorGain(double gain, int instrumentIndex, int detectorIndex);
    void setDetectorID(string id, int instrumentIndex, int detectorIndex);
    void setDetectorManufacturer(string manufacturer, int instrumentIndex, int detectorIndex);
    void setDetectorModel(string model, int instrumentIndex, int detectorIndex);
    void setDetectorOffset(double offset, int instrumentIndex, int detectorIndex);
    void setDetectorSerialNumber(string serialNumber, int instrumentIndex, int detectorIndex);
    void setDetectorType(string type, int instrumentIndex, int detectorIndex);
    void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex);
    void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex);

    // - DetectorSettings property storage -
    void setDetectorSettingsBinning(string binning, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsDetector(string detector, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsGain(double gain, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsOffset(double offset, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsVoltage(double voltage, int imageIndex, int logicalChannelIndex);

    // - Dichroic property storage -
    void setDichroicID(string id, int instrumentIndex, int dichroicIndex);
    void setDichroicLotNumber(string lotNumber, int instrumentIndex, int dichroicIndex);
    void setDichroicManufacturer(string manufacturer, int instrumentIndex, int dichroicIndex);
    void setDichroicModel(string model, int instrumentIndex, int dichroicIndex);

    // - Dimensions property storage -
    void setDimensionsPhysicalSizeX(double physicalSizeX, int imageIndex, int pixelsIndex);
    void setDimensionsPhysicalSizeY(double physicalSizeY, int imageIndex, int pixelsIndex);
    void setDimensionsPhysicalSizeZ(double physicalSizeZ, int imageIndex, int pixelsIndex);
    void setDimensionsTimeIncrement(double timeIncrement, int imageIndex, int pixelsIndex);
    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);
    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);

    // - DisplayOptions property storage -
    void setDisplayOptionsDisplay(string display, int imageIndex);
    void setDisplayOptionsID(string id, int imageIndex);
    void setDisplayOptionsZoom(double zoom, int imageIndex);

    // - Ellipse property storage -
    void setEllipseCx(string cx, int imageIndex, int roiIndex, int shapeIndex);
    void setEllipseCy(string cy, int imageIndex, int roiIndex, int shapeIndex);
    void setEllipseID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setEllipseRx(string rx, int imageIndex, int roiIndex, int shapeIndex);
    void setEllipseRy(string ry, int imageIndex, int roiIndex, int shapeIndex);
    void setEllipseTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);

    // - EmFilter property storage -
    void setEmFilterLotNumber(string lotNumber, int instrumentIndex, int filterIndex);
    void setEmFilterManufacturer(string manufacturer, int instrumentIndex, int filterIndex);
    void setEmFilterModel(string model, int instrumentIndex, int filterIndex);
    void setEmFilterType(string type, int instrumentIndex, int filterIndex);

    // - ExFilter property storage -
    void setExFilterLotNumber(string lotNumber, int instrumentIndex, int filterIndex);
    void setExFilterManufacturer(string manufacturer, int instrumentIndex, int filterIndex);
    void setExFilterModel(string model, int instrumentIndex, int filterIndex);
    void setExFilterType(string type, int instrumentIndex, int filterIndex);

    // - Experiment property storage -
    void setExperimentDescription(string description, int experimentIndex);
    void setExperimentExperimenterRef(string experimenterRef, int experimentIndex);
    void setExperimentID(string id, int experimentIndex);
    void setExperimentType(string type, int experimentIndex);

    // - Experimenter property storage -
    void setExperimenterEmail(string email, int experimenterIndex);
    void setExperimenterFirstName(string firstName, int experimenterIndex);
    void setExperimenterID(string id, int experimenterIndex);
    void setExperimenterInstitution(string institution, int experimenterIndex);
    void setExperimenterLastName(string lastName, int experimenterIndex);
    void setExperimenterOMEName(string omeName, int experimenterIndex);

    // - ExperimenterMembership property storage -
    void setExperimenterMembershipGroup(string group, int experimenterIndex, int groupRefIndex);

    // - Filament property storage -
    void setFilamentType(string type, int instrumentIndex, int lightSourceIndex);

    // - Filter property storage -
    void setFilterFilterWheel(string filterWheel, int instrumentIndex, int filterIndex);
    void setFilterID(string id, int instrumentIndex, int filterIndex);
    void setFilterLotNumber(string lotNumber, int instrumentIndex, int filterIndex);
    void setFilterManufacturer(string manufacturer, int instrumentIndex, int filterIndex);
    void setFilterModel(string model, int instrumentIndex, int filterIndex);
    void setFilterType(string type, int instrumentIndex, int filterIndex);

    // - FilterSet property storage -
    void setFilterSetDichroic(string dichroic, int instrumentIndex, int filterSetIndex);
    void setFilterSetEmFilter(string emFilter, int instrumentIndex, int filterSetIndex);
    void setFilterSetExFilter(string exFilter, int instrumentIndex, int filterSetIndex);
    void setFilterSetID(string id, int instrumentIndex, int filterSetIndex);
    void setFilterSetLotNumber(string lotNumber, int instrumentIndex, int filterSetIndex);
    void setFilterSetManufacturer(string manufacturer, int instrumentIndex, int filterSetIndex);
    void setFilterSetModel(string model, int instrumentIndex, int filterSetIndex);

    // - Group property storage -
    void setGroupID(string id, int groupIndex);
    void setGroupName(string name, int groupIndex);

    // - GroupRef property storage -

    // - Image property storage -
    void setImageAcquiredPixels(string acquiredPixels, int imageIndex);
    void setImageCreationDate(string creationDate, int imageIndex);
    void setImageDefaultPixels(string defaultPixels, int imageIndex);
    void setImageDescription(string description, int imageIndex);
    void setImageExperimentRef(string experimentRef, int imageIndex);
    void setImageExperimenterRef(string experimenterRef, int imageIndex);
    void setImageGroupRef(string groupRef, int imageIndex);
    void setImageID(string id, int imageIndex);
    void setImageInstrumentRef(string instrumentRef, int imageIndex);
    void setImageName(string name, int imageIndex);

    // - ImagingEnvironment property storage -
    void setImagingEnvironmentAirPressure(double airPressure, int imageIndex);
    void setImagingEnvironmentCO2Percent(double cO2Percent, int imageIndex);
    void setImagingEnvironmentHumidity(double humidity, int imageIndex);
    void setImagingEnvironmentTemperature(double temperature, int imageIndex);

    // - Instrument property storage -
    void setInstrumentID(string id, int instrumentIndex);

    // - Laser property storage -
    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);
    void setLaserLaserMedium(string laserMedium, int instrumentIndex, int lightSourceIndex);
    void setLaserPockelCell(bool pockelCell, int instrumentIndex, int lightSourceIndex);
    void setLaserPulse(string pulse, int instrumentIndex, int lightSourceIndex);
    void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int lightSourceIndex);
    void setLaserTuneable(bool tuneable, int instrumentIndex, int lightSourceIndex);
    void setLaserType(string type, int instrumentIndex, int lightSourceIndex);
    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);

    // - LightSource property storage -
    void setLightSourceID(string id, int instrumentIndex, int lightSourceIndex);
    void setLightSourceManufacturer(string manufacturer, int instrumentIndex, int lightSourceIndex);
    void setLightSourceModel(string model, int instrumentIndex, int lightSourceIndex);
    void setLightSourcePower(double power, int instrumentIndex, int lightSourceIndex);
    void setLightSourceSerialNumber(string serialNumber, int instrumentIndex, int lightSourceIndex);

    // - LightSourceRef property storage -
    void setLightSourceRefAttenuation(double attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    void setLightSourceRefLightSource(string lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    // - LightSourceSettings property storage -
    void setLightSourceSettingsAttenuation(double attenuation, int imageIndex, int logicalChannelIndex);
    void setLightSourceSettingsLightSource(string lightSource, int imageIndex, int logicalChannelIndex);
    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);

    // - Line property storage -
    void setLineID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setLineTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);
    void setLineX1(string x1, int imageIndex, int roiIndex, int shapeIndex);
    void setLineX2(string x2, int imageIndex, int roiIndex, int shapeIndex);
    void setLineY1(string y1, int imageIndex, int roiIndex, int shapeIndex);
    void setLineY2(string y2, int imageIndex, int roiIndex, int shapeIndex);

    // - LogicalChannel property storage -
    void setLogicalChannelContrastMethod(string contrastMethod, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelDetector(string detector, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelFilterSet(string filterSet, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelFluor(string fluor, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelID(string id, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelIlluminationType(string illuminationType, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelLightSource(string lightSource, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelMode(string mode, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelName(string name, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelNdFilter(double ndFilter, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelOTF(string otf, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPhotometricInterpretation(string photometricInterpretation, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPinholeSize(double pinholeSize, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelSecondaryEmissionFilter(string secondaryEmissionFilter, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelSecondaryExcitationFilter(string secondaryExcitationFilter, int imageIndex, int logicalChannelIndex);

    // - Mask property storage -
    void setMaskHeight(string height, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskWidth(string width, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskX(string x, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskY(string y, int imageIndex, int roiIndex, int shapeIndex);

    // - MaskPixels property storage -
    void setMaskPixelsBigEndian(bool bigEndian, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskPixelsBinData(ByteSeq binData, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskPixelsExtendedPixelType(string extendedPixelType, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskPixelsID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex);
    void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex);

    // - MicrobeamManipulation property storage -
    void setMicrobeamManipulationExperimenterRef(string experimenterRef, int imageIndex, int microbeamManipulationIndex);
    void setMicrobeamManipulationID(string id, int imageIndex, int microbeamManipulationIndex);
    void setMicrobeamManipulationType(string type, int imageIndex, int microbeamManipulationIndex);

    // - MicrobeamManipulationRef property storage -
    void setMicrobeamManipulationRefID(string id, int experimentIndex, int microbeamManipulationRefIndex);

    // - Microscope property storage -
    void setMicroscopeID(string id, int instrumentIndex);
    void setMicroscopeManufacturer(string manufacturer, int instrumentIndex);
    void setMicroscopeModel(string model, int instrumentIndex);
    void setMicroscopeSerialNumber(string serialNumber, int instrumentIndex);
    void setMicroscopeType(string type, int instrumentIndex);

    // - OTF property storage -
    void setOTFBinaryFile(string binaryFile, int instrumentIndex, int otfIndex);
    void setOTFID(string id, int instrumentIndex, int otfIndex);
    void setOTFObjective(string objective, int instrumentIndex, int otfIndex);
    void setOTFOpticalAxisAveraged(bool opticalAxisAveraged, int instrumentIndex, int otfIndex);
    void setOTFPixelType(string pixelType, int instrumentIndex, int otfIndex);
    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);
    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);

    // - Objective property storage -
    void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveCorrection(string correction, int instrumentIndex, int objectiveIndex);
    void setObjectiveID(string id, int instrumentIndex, int objectiveIndex);
    void setObjectiveImmersion(string immersion, int instrumentIndex, int objectiveIndex);
    void setObjectiveIris(bool iris, int instrumentIndex, int objectiveIndex);
    void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex);
    void setObjectiveManufacturer(string manufacturer, int instrumentIndex, int objectiveIndex);
    void setObjectiveModel(string model, int instrumentIndex, int objectiveIndex);
    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveSerialNumber(string serialNumber, int instrumentIndex, int objectiveIndex);
    void setObjectiveWorkingDistance(double workingDistance, int instrumentIndex, int objectiveIndex);

    // - ObjectiveSettings property storage -
    void setObjectiveSettingsCorrectionCollar(double correctionCollar, int imageIndex);
    void setObjectiveSettingsMedium(string medium, int imageIndex);
    void setObjectiveSettingsObjective(string objective, int imageIndex);
    void setObjectiveSettingsRefractiveIndex(double refractiveIndex, int imageIndex);

    // - Path property storage -
    void setPathD(string d, int imageIndex, int roiIndex, int shapeIndex);
    void setPathID(string id, int imageIndex, int roiIndex, int shapeIndex);

    // - Pixels property storage -
    void setPixelsBigEndian(bool bigEndian, int imageIndex, int pixelsIndex);
    void setPixelsDimensionOrder(string dimensionOrder, int imageIndex, int pixelsIndex);
    void setPixelsID(string id, int imageIndex, int pixelsIndex);
    void setPixelsPixelType(string pixelType, int imageIndex, int pixelsIndex);
    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);
    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);
    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);
    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);
    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);

    // - Plane property storage -
    void setPlaneHashSHA1(string hashSHA1, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneID(string id, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);

    // - PlaneTiming property storage -
    void setPlaneTimingDeltaT(double deltaT, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTimingExposureTime(double exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

    // - Plate property storage -
    void setPlateColumnNamingConvention(string columnNamingConvention, int plateIndex);
    void setPlateDescription(string description, int plateIndex);
    void setPlateExternalIdentifier(string externalIdentifier, int plateIndex);
    void setPlateID(string id, int plateIndex);
    void setPlateName(string name, int plateIndex);
    void setPlateRowNamingConvention(string rowNamingConvention, int plateIndex);
    void setPlateStatus(string status, int plateIndex);
    void setPlateWellOriginX(double wellOriginX, int plateIndex);
    void setPlateWellOriginY(double wellOriginY, int plateIndex);

    // - PlateRef property storage -
    void setPlateRefID(string id, int screenIndex, int plateRefIndex);
    void setPlateRefSample(int sample, int screenIndex, int plateRefIndex);
    void setPlateRefWell(string well, int screenIndex, int plateRefIndex);

    // - Point property storage -
    void setPointCx(string cx, int imageIndex, int roiIndex, int shapeIndex);
    void setPointCy(string cy, int imageIndex, int roiIndex, int shapeIndex);
    void setPointID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setPointR(string r, int imageIndex, int roiIndex, int shapeIndex);
    void setPointTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);

    // - Polygon property storage -
    void setPolygonID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setPolygonPoints(string points, int imageIndex, int roiIndex, int shapeIndex);
    void setPolygonTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);

    // - Polyline property storage -
    void setPolylineID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setPolylinePoints(string points, int imageIndex, int roiIndex, int shapeIndex);
    void setPolylineTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);

    // - Project property storage -
    void setProjectDescription(string description, int projectIndex);
    void setProjectExperimenterRef(string experimenterRef, int projectIndex);
    void setProjectGroupRef(string groupRef, int projectIndex);
    void setProjectID(string id, int projectIndex);
    void setProjectName(string name, int projectIndex);

    // - ProjectRef property storage -
    void setProjectRefID(string id, int datasetIndex, int projectRefIndex);

    // - Pump property storage -
    void setPumpLightSource(string lightSource, int instrumentIndex, int lightSourceIndex);

    // - ROI property storage -
    void setROIID(string id, int imageIndex, int roiIndex);
    void setROIT0(int t0, int imageIndex, int roiIndex);
    void setROIT1(int t1, int imageIndex, int roiIndex);
    void setROIX0(int x0, int imageIndex, int roiIndex);
    void setROIX1(int x1, int imageIndex, int roiIndex);
    void setROIY0(int y0, int imageIndex, int roiIndex);
    void setROIY1(int y1, int imageIndex, int roiIndex);
    void setROIZ0(int z0, int imageIndex, int roiIndex);
    void setROIZ1(int z1, int imageIndex, int roiIndex);

    // - ROIRef property storage -
    void setROIRefID(string id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

    // - Reagent property storage -
    void setReagentDescription(string description, int screenIndex, int reagentIndex);
    void setReagentID(string id, int screenIndex, int reagentIndex);
    void setReagentName(string name, int screenIndex, int reagentIndex);
    void setReagentReagentIdentifier(string reagentIdentifier, int screenIndex, int reagentIndex);

    // - Rect property storage -
    void setRectHeight(string height, int imageIndex, int roiIndex, int shapeIndex);
    void setRectID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setRectTransform(string transform, int imageIndex, int roiIndex, int shapeIndex);
    void setRectWidth(string width, int imageIndex, int roiIndex, int shapeIndex);
    void setRectX(string x, int imageIndex, int roiIndex, int shapeIndex);
    void setRectY(string y, int imageIndex, int roiIndex, int shapeIndex);

    // - Region property storage -
    void setRegionID(string id, int imageIndex, int regionIndex);
    void setRegionName(string name, int imageIndex, int regionIndex);
    void setRegionTag(string tag, int imageIndex, int regionIndex);

    // - RoiLink property storage -
    void setRoiLinkDirection(string direction, int imageIndex, int roiIndex, int roiLinkIndex);
    void setRoiLinkName(string name, int imageIndex, int roiIndex, int roiLinkIndex);
    void setRoiLinkRef(string ref, int imageIndex, int roiIndex, int roiLinkIndex);

    // - Screen property storage -
    void setScreenDescription(string description, int screenIndex);
    void setScreenExtern(string extern, int screenIndex);
    void setScreenID(string id, int screenIndex);
    void setScreenName(string name, int screenIndex);
    void setScreenProtocolDescription(string protocolDescription, int screenIndex);
    void setScreenProtocolIdentifier(string protocolIdentifier, int screenIndex);
    void setScreenReagentSetDescription(string reagentSetDescription, int screenIndex);
    void setScreenReagentSetIdentifier(string reagentSetIdentifier, int screenIndex);
    void setScreenType(string type, int screenIndex);

    // - ScreenAcquisition property storage -
    void setScreenAcquisitionEndTime(string endTime, int screenIndex, int screenAcquisitionIndex);
    void setScreenAcquisitionID(string id, int screenIndex, int screenAcquisitionIndex);
    void setScreenAcquisitionStartTime(string startTime, int screenIndex, int screenAcquisitionIndex);

    // - ScreenRef property storage -
    void setScreenRefID(string id, int plateIndex, int screenRefIndex);

    // - Shape property storage -
    void setShapeBaselineShift(string baselineShift, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeDirection(string direction, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFillColor(string fillColor, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFillOpacity(string fillOpacity, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFillRule(string fillRule, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontFamily(string fontFamily, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontStretch(string fontStretch, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontStyle(string fontStyle, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontVariant(string fontVariant, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeFontWeight(string fontWeight, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeG(string g, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeID(string id, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeLocked(bool locked, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeAttribute(string strokeAttribute, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeColor(string strokeColor, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeDashArray(string strokeDashArray, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeLineCap(string strokeLineCap, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeLineJoin(string strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeOpacity(double strokeOpacity, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeText(string text, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTextAnchor(string textAnchor, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTextDecoration(string textDecoration, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTextFill(string textFill, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTextStroke(string textStroke, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeVectorEffect(string vectorEffect, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeVisibility(bool visibility, int imageIndex, int roiIndex, int shapeIndex);
    void setShapeWritingMode(string writingMode, int imageIndex, int roiIndex, int shapeIndex);

    // - StageLabel property storage -
    void setStageLabelName(string name, int imageIndex);
    void setStageLabelX(double x, int imageIndex);
    void setStageLabelY(double y, int imageIndex);
    void setStageLabelZ(double z, int imageIndex);

    // - StagePosition property storage -
    void setStagePositionPositionX(double positionX, int imageIndex, int pixelsIndex, int planeIndex);
    void setStagePositionPositionY(double positionY, int imageIndex, int pixelsIndex, int planeIndex);
    void setStagePositionPositionZ(double positionZ, int imageIndex, int pixelsIndex, int planeIndex);

    // - Thumbnail property storage -
    void setThumbnailHref(string href, int imageIndex);
    void setThumbnailID(string id, int imageIndex);
    void setThumbnailMIMEtype(string mimEtype, int imageIndex);

    // - TiffData property storage -
    void setTiffDataFileName(string fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataUUID(string uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

    // - TransmittanceRange property storage -
    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex);

    // - Well property storage -
    void setWellColumn(int column, int plateIndex, int wellIndex);
    void setWellExternalDescription(string externalDescription, int plateIndex, int wellIndex);
    void setWellExternalIdentifier(string externalIdentifier, int plateIndex, int wellIndex);
    void setWellID(string id, int plateIndex, int wellIndex);
    void setWellReagent(string reagent, int plateIndex, int wellIndex);
    void setWellRow(int row, int plateIndex, int wellIndex);
    void setWellType(string type, int plateIndex, int wellIndex);

    // - WellSample property storage -
    void setWellSampleID(string id, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleImageRef(string imageRef, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePosX(double posX, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePosY(double posY, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

    // - WellSampleRef property storage -
    void setWellSampleRefID(string id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);
  };
};
