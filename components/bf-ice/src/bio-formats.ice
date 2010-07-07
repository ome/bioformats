//
// bio-formats.ice
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2010 UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by melissa via MetadataAutogen on Jun 10, 2010 11:51:47 AM CDT
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

    int getBooleanAnnotationCount();
    int getChannelCount(int imageIndex);
    int getChannelAnnotationRefCount(int imageIndex, int channelIndex);
    int getDatasetCount();
    int getDatasetAnnotationRefCount(int datasetIndex);
    int getDatasetRefCount(int imageIndex);
    int getDetectorCount(int instrumentIndex);
    int getDichroicCount(int instrumentIndex);
    int getDoubleAnnotationCount();
    int getExperimentCount();
    int getExperimenterCount();
    int getExperimenterAnnotationRefCount(int experimenterIndex);
    int getExperimenterGroupRefCount(int experimenterIndex);
    int getFileAnnotationCount();
    int getFilterCount(int instrumentIndex);
    int getFilterSetCount(int instrumentIndex);
    int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex);
    int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex);
    int getGroupCount();
    int getImageCount();
    int getImageAnnotationRefCount(int imageIndex);
    int getImageROIRefCount(int imageIndex);
    int getInstrumentCount();
    int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex);
    int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex);
    int getListAnnotationCount();
    int getListAnnotationAnnotationRefCount(int listAnnotationIndex);
    int getLongAnnotationCount();
    int getMicrobeamManipulationCount(int experimentIndex);
    int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex);
    int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex);
    int getMicrobeamManipulationRefCount(int imageIndex);
    int getOTFCount(int instrumentIndex);
    int getObjectiveCount(int instrumentIndex);
    int getPixelsAnnotationRefCount(int imageIndex);
    int getPixelsBinDataCount(int imageIndex);
    int getPlaneCount(int imageIndex);
    int getPlaneAnnotationRefCount(int imageIndex, int planeIndex);
    int getPlateCount();
    int getPlateAcquisitionCount(int plateIndex);
    int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex);
    int getPlateAnnotationRefCount(int plateIndex);
    int getPlateRefCount(int screenIndex);
    int getProjectCount();
    int getProjectAnnotationRefCount(int projectIndex);
    int getProjectRefCount(int datasetIndex);
    int getROICount();
    int getROIAnnotationRefCount(int roiIndex);
    int getReagentCount(int screenIndex);
    int getReagentAnnotationRefCount(int screenIndex, int reagentIndex);
    int getScreenCount();
    int getScreenAnnotationRefCount(int screenIndex);
    int getScreenRefCount(int plateIndex);
    int getShapeAnnotationRefCount(int roiIndex, int shapeIndex);
    int getStringAnnotationCount();
    int getTiffDataCount(int imageIndex);
    int getTimestampAnnotationCount();
    int getWellCount(int plateIndex);
    int getWellAnnotationRefCount(int plateIndex, int wellIndex);
    int getWellSampleCount(int plateIndex, int wellIndex);
    int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex);
    int getXMLAnnotationCount();

    // - Entity retrieval -

    string getUUID();

    // - Arc property retrieval -

    string getArcID(int instrumentIndex, int arcIndex);
    string getArcLotNumber(int instrumentIndex, int arcIndex);
    string getArcManufacturer(int instrumentIndex, int arcIndex);
    string getArcModel(int instrumentIndex, int arcIndex);
    double getArcPower(int instrumentIndex, int arcIndex);
    string getArcSerialNumber(int instrumentIndex, int arcIndex);
    arctype getArcType(int instrumentIndex, int arcIndex);

    // - BooleanAnnotation property retrieval -

    string getBooleanAnnotationID(int booleanAnnotationIndex);
    string getBooleanAnnotationNamespace(int booleanAnnotationIndex);
    bool getBooleanAnnotationValue(int booleanAnnotationIndex);

    // - Channel property retrieval -

    acquisitionmode getChannelAcquisitionMode(int imageIndex, int channelIndex);
    string getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex);
    int getChannelColor(int imageIndex, int channelIndex);
    contrastmethod getChannelContrastMethod(int imageIndex, int channelIndex);
    positiveint getChannelEmissionWavelength(int imageIndex, int channelIndex);
    positiveint getChannelExcitationWavelength(int imageIndex, int channelIndex);
    string getChannelFilterSetRef(int imageIndex, int channelIndex);
    string getChannelFluor(int imageIndex, int channelIndex);
    string getChannelID(int imageIndex, int channelIndex);
    illuminationtype getChannelIlluminationType(int imageIndex, int channelIndex);
    percentfraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex);
    string getChannelLightSourceSettingsID(int imageIndex, int channelIndex);
    positiveint getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex);
    double getChannelNDFilter(int imageIndex, int channelIndex);
    string getChannelName(int imageIndex, int channelIndex);
    string getChannelOTFRef(int imageIndex, int channelIndex);
    double getChannelPinholeSize(int imageIndex, int channelIndex);
    int getChannelPockelCellSetting(int imageIndex, int channelIndex);
    int getChannelSamplesPerPixel(int imageIndex, int channelIndex);

    // - ChannelAnnotationRef property retrieval -


    // - Dataset property retrieval -

    string getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex);
    string getDatasetDescription(int datasetIndex);
    string getDatasetExperimenterRef(int datasetIndex);
    string getDatasetGroupRef(int datasetIndex);
    string getDatasetID(int datasetIndex);
    string getDatasetName(int datasetIndex);
    string getDatasetProjectRef(int datasetIndex, int projectRefIndex);

    // - DatasetAnnotationRef property retrieval -


    // - DatasetRef property retrieval -


    // - Detector property retrieval -

    double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);
    double getDetectorGain(int instrumentIndex, int detectorIndex);
    string getDetectorID(int instrumentIndex, int detectorIndex);
    string getDetectorLotNumber(int instrumentIndex, int detectorIndex);
    string getDetectorManufacturer(int instrumentIndex, int detectorIndex);
    string getDetectorModel(int instrumentIndex, int detectorIndex);
    double getDetectorOffset(int instrumentIndex, int detectorIndex);
    string getDetectorSerialNumber(int instrumentIndex, int detectorIndex);
    detectortype getDetectorType(int instrumentIndex, int detectorIndex);
    double getDetectorVoltage(int instrumentIndex, int detectorIndex);
    double getDetectorZoom(int instrumentIndex, int detectorIndex);

    // - DetectorSettings property retrieval -

    binning getDetectorSettingsBinning(int imageIndex, int channelIndex);
    double getDetectorSettingsGain(int imageIndex, int channelIndex);
    string getDetectorSettingsID(int imageIndex, int channelIndex);
    double getDetectorSettingsOffset(int imageIndex, int channelIndex);
    double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex);
    double getDetectorSettingsVoltage(int imageIndex, int channelIndex);

    // - Dichroic property retrieval -

    string getDichroicID(int instrumentIndex, int dichroicIndex);
    string getDichroicLotNumber(int instrumentIndex, int dichroicIndex);
    string getDichroicManufacturer(int instrumentIndex, int dichroicIndex);
    string getDichroicModel(int instrumentIndex, int dichroicIndex);
    string getDichroicSerialNumber(int instrumentIndex, int dichroicIndex);

    // - DoubleAnnotation property retrieval -

    string getDoubleAnnotationID(int doubleAnnotationIndex);
    string getDoubleAnnotationNamespace(int doubleAnnotationIndex);
    double getDoubleAnnotationValue(int doubleAnnotationIndex);

    // - Ellipse property retrieval -

    string getEllipseDescription(int roiIndex, int shapeIndex);
    int getEllipseFill(int roiIndex, int shapeIndex);
    int getEllipseFontSize(int roiIndex, int shapeIndex);
    string getEllipseID(int roiIndex, int shapeIndex);
    string getEllipseLabel(int roiIndex, int shapeIndex);
    string getEllipseName(int roiIndex, int shapeIndex);
    double getEllipseRadiusX(int roiIndex, int shapeIndex);
    double getEllipseRadiusY(int roiIndex, int shapeIndex);
    int getEllipseStroke(int roiIndex, int shapeIndex);
    string getEllipseStrokeDashArray(int roiIndex, int shapeIndex);
    double getEllipseStrokeWidth(int roiIndex, int shapeIndex);
    int getEllipseTheC(int roiIndex, int shapeIndex);
    int getEllipseTheT(int roiIndex, int shapeIndex);
    int getEllipseTheZ(int roiIndex, int shapeIndex);
    string getEllipseTransform(int roiIndex, int shapeIndex);
    double getEllipseX(int roiIndex, int shapeIndex);
    double getEllipseY(int roiIndex, int shapeIndex);

    // - Experiment property retrieval -

    string getExperimentDescription(int experimentIndex);
    string getExperimentExperimenterRef(int experimentIndex);
    string getExperimentID(int experimentIndex);
    experimenttype getExperimentType(int experimentIndex);

    // - Experimenter property retrieval -

    string getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex);
    string getExperimenterDisplayName(int experimenterIndex);
    string getExperimenterEmail(int experimenterIndex);
    string getExperimenterFirstName(int experimenterIndex);
    string getExperimenterGroupRef(int experimenterIndex, int groupRefIndex);
    string getExperimenterID(int experimenterIndex);
    string getExperimenterInstitution(int experimenterIndex);
    string getExperimenterLastName(int experimenterIndex);
    string getExperimenterMiddleName(int experimenterIndex);
    string getExperimenterUserName(int experimenterIndex);

    // - ExperimenterAnnotationRef property retrieval -


    // - ExperimenterGroupRef property retrieval -


    // - Filament property retrieval -

    string getFilamentID(int instrumentIndex, int filamentIndex);
    string getFilamentLotNumber(int instrumentIndex, int filamentIndex);
    string getFilamentManufacturer(int instrumentIndex, int filamentIndex);
    string getFilamentModel(int instrumentIndex, int filamentIndex);
    double getFilamentPower(int instrumentIndex, int filamentIndex);
    string getFilamentSerialNumber(int instrumentIndex, int filamentIndex);
    filamenttype getFilamentType(int instrumentIndex, int filamentIndex);

    // - FileAnnotation property retrieval -

    string getFileAnnotationBinaryFileFileName(int fileAnnotationIndex);
    string getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex);
    int getFileAnnotationBinaryFileSize(int fileAnnotationIndex);
    string getFileAnnotationID(int fileAnnotationIndex);
    string getFileAnnotationNamespace(int fileAnnotationIndex);

    // - Filter property retrieval -

    string getFilterFilterWheel(int instrumentIndex, int filterIndex);
    string getFilterID(int instrumentIndex, int filterIndex);
    string getFilterLotNumber(int instrumentIndex, int filterIndex);
    string getFilterManufacturer(int instrumentIndex, int filterIndex);
    string getFilterModel(int instrumentIndex, int filterIndex);
    string getFilterSerialNumber(int instrumentIndex, int filterIndex);
    filtertype getFilterType(int instrumentIndex, int filterIndex);

    // - FilterSet property retrieval -

    string getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex);
    string getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);
    string getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);
    string getFilterSetID(int instrumentIndex, int filterSetIndex);
    string getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);
    string getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);
    string getFilterSetModel(int instrumentIndex, int filterSetIndex);
    string getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex);

    // - FilterSetEmissionFilterRef property retrieval -


    // - FilterSetExcitationFilterRef property retrieval -


    // - Group property retrieval -

    string getGroupContact(int groupIndex);
    string getGroupDescription(int groupIndex);
    string getGroupID(int groupIndex);
    string getGroupLeader(int groupIndex);
    string getGroupName(int groupIndex);

    // - Image property retrieval -

    string getImageAcquiredDate(int imageIndex);
    string getImageAnnotationRef(int imageIndex, int annotationRefIndex);
    string getImageDatasetRef(int imageIndex, int datasetRefIndex);
    string getImageDescription(int imageIndex);
    string getImageExperimentRef(int imageIndex);
    string getImageExperimenterRef(int imageIndex);
    string getImageGroupRef(int imageIndex);
    string getImageID(int imageIndex);
    string getImageInstrumentRef(int imageIndex);
    string getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex);
    string getImageName(int imageIndex);
    double getImageObjectiveSettingsCorrectionCollar(int imageIndex);
    string getImageObjectiveSettingsID(int imageIndex);
    medium getImageObjectiveSettingsMedium(int imageIndex);
    double getImageObjectiveSettingsRefractiveIndex(int imageIndex);
    string getImageROIRef(int imageIndex, int roiRefIndex);

    // - ImageAnnotationRef property retrieval -


    // - ImageROIRef property retrieval -


    // - ImagingEnvironment property retrieval -

    double getImagingEnvironmentAirPressure(int imageIndex);
    percentfraction getImagingEnvironmentCO2Percent(int imageIndex);
    percentfraction getImagingEnvironmentHumidity(int imageIndex);
    double getImagingEnvironmentTemperature(int imageIndex);

    // - Instrument property retrieval -

    string getInstrumentID(int instrumentIndex);

    // - Laser property retrieval -

    positiveint getLaserFrequencyMultiplication(int instrumentIndex, int laserIndex);
    string getLaserID(int instrumentIndex, int laserIndex);
    lasermedium getLaserLaserMedium(int instrumentIndex, int laserIndex);
    string getLaserLotNumber(int instrumentIndex, int laserIndex);
    string getLaserManufacturer(int instrumentIndex, int laserIndex);
    string getLaserModel(int instrumentIndex, int laserIndex);
    bool getLaserPockelCell(int instrumentIndex, int laserIndex);
    double getLaserPower(int instrumentIndex, int laserIndex);
    pulse getLaserPulse(int instrumentIndex, int laserIndex);
    string getLaserPump(int instrumentIndex, int laserIndex);
    double getLaserRepetitionRate(int instrumentIndex, int laserIndex);
    string getLaserSerialNumber(int instrumentIndex, int laserIndex);
    bool getLaserTuneable(int instrumentIndex, int laserIndex);
    lasertype getLaserType(int instrumentIndex, int laserIndex);
    positiveint getLaserWavelength(int instrumentIndex, int laserIndex);

    // - LightEmittingDiode property retrieval -

    string getLightEmittingDiodeID(int instrumentIndex, int lightEmittingDiodeIndex);
    string getLightEmittingDiodeLotNumber(int instrumentIndex, int lightEmittingDiodeIndex);
    string getLightEmittingDiodeManufacturer(int instrumentIndex, int lightEmittingDiodeIndex);
    string getLightEmittingDiodeModel(int instrumentIndex, int lightEmittingDiodeIndex);
    double getLightEmittingDiodePower(int instrumentIndex, int lightEmittingDiodeIndex);
    string getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightEmittingDiodeIndex);

    // - LightPath property retrieval -

    string getLightPathDichroicRef(int imageIndex, int channelIndex);
    string getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex);
    string getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex);

    // - LightPathEmissionFilterRef property retrieval -


    // - LightPathExcitationFilterRef property retrieval -


    // - Line property retrieval -

    string getLineDescription(int roiIndex, int shapeIndex);
    int getLineFill(int roiIndex, int shapeIndex);
    int getLineFontSize(int roiIndex, int shapeIndex);
    string getLineID(int roiIndex, int shapeIndex);
    string getLineLabel(int roiIndex, int shapeIndex);
    string getLineName(int roiIndex, int shapeIndex);
    int getLineStroke(int roiIndex, int shapeIndex);
    string getLineStrokeDashArray(int roiIndex, int shapeIndex);
    double getLineStrokeWidth(int roiIndex, int shapeIndex);
    int getLineTheC(int roiIndex, int shapeIndex);
    int getLineTheT(int roiIndex, int shapeIndex);
    int getLineTheZ(int roiIndex, int shapeIndex);
    string getLineTransform(int roiIndex, int shapeIndex);
    double getLineX1(int roiIndex, int shapeIndex);
    double getLineX2(int roiIndex, int shapeIndex);
    double getLineY1(int roiIndex, int shapeIndex);
    double getLineY2(int roiIndex, int shapeIndex);

    // - ListAnnotation property retrieval -

    string getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex);
    string getListAnnotationID(int listAnnotationIndex);
    string getListAnnotationNamespace(int listAnnotationIndex);

    // - ListAnnotationAnnotationRef property retrieval -


    // - LongAnnotation property retrieval -

    string getLongAnnotationID(int longAnnotationIndex);
    string getLongAnnotationNamespace(int longAnnotationIndex);
    long getLongAnnotationValue(int longAnnotationIndex);

    // - Mask property retrieval -

    string getMaskDescription(int roiIndex, int shapeIndex);
    int getMaskFill(int roiIndex, int shapeIndex);
    int getMaskFontSize(int roiIndex, int shapeIndex);
    string getMaskID(int roiIndex, int shapeIndex);
    string getMaskLabel(int roiIndex, int shapeIndex);
    string getMaskName(int roiIndex, int shapeIndex);
    int getMaskStroke(int roiIndex, int shapeIndex);
    string getMaskStrokeDashArray(int roiIndex, int shapeIndex);
    double getMaskStrokeWidth(int roiIndex, int shapeIndex);
    int getMaskTheC(int roiIndex, int shapeIndex);
    int getMaskTheT(int roiIndex, int shapeIndex);
    int getMaskTheZ(int roiIndex, int shapeIndex);
    string getMaskTransform(int roiIndex, int shapeIndex);
    double getMaskX(int roiIndex, int shapeIndex);
    double getMaskY(int roiIndex, int shapeIndex);

    // - MicrobeamManipulation property retrieval -

    string getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex);
    string getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex);
    string getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int roiRefIndex);
    microbeammanipulationtype getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex);

    // - MicrobeamManipulationLightSourceSettings property retrieval -

    percentfraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);
    string getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);
    positiveint getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

    // - MicrobeamManipulationROIRef property retrieval -


    // - MicrobeamManipulationRef property retrieval -


    // - Microscope property retrieval -

    string getMicroscopeLotNumber(int instrumentIndex);
    string getMicroscopeManufacturer(int instrumentIndex);
    string getMicroscopeModel(int instrumentIndex);
    string getMicroscopeSerialNumber(int instrumentIndex);
    microscopetype getMicroscopeType(int instrumentIndex);

    // - OTF property retrieval -

    string getOTFBinaryFileFileName(int instrumentIndex, int otfIndex);
    string getOTFBinaryFileMIMEType(int instrumentIndex, int otfIndex);
    int getOTFBinaryFileSize(int instrumentIndex, int otfIndex);
    string getOTFFilterSetRef(int instrumentIndex, int otfIndex);
    string getOTFID(int instrumentIndex, int otfIndex);
    double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int otfIndex);
    string getOTFObjectiveSettingsID(int instrumentIndex, int otfIndex);
    medium getOTFObjectiveSettingsMedium(int instrumentIndex, int otfIndex);
    double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int otfIndex);
    bool getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);
    positiveint getOTFSizeX(int instrumentIndex, int otfIndex);
    positiveint getOTFSizeY(int instrumentIndex, int otfIndex);
    pixeltype getOTFType(int instrumentIndex, int otfIndex);

    // - Objective property retrieval -

    double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);
    correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex);
    string getObjectiveID(int instrumentIndex, int objectiveIndex);
    immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex);
    bool getObjectiveIris(int instrumentIndex, int objectiveIndex);
    double getObjectiveLensNA(int instrumentIndex, int objectiveIndex);
    string getObjectiveLotNumber(int instrumentIndex, int objectiveIndex);
    string getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);
    string getObjectiveModel(int instrumentIndex, int objectiveIndex);
    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);
    string getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);
    double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

    // - Path property retrieval -

    string getPathDefinition(int roiIndex, int shapeIndex);
    string getPathDescription(int roiIndex, int shapeIndex);
    int getPathFill(int roiIndex, int shapeIndex);
    int getPathFontSize(int roiIndex, int shapeIndex);
    string getPathID(int roiIndex, int shapeIndex);
    string getPathLabel(int roiIndex, int shapeIndex);
    string getPathName(int roiIndex, int shapeIndex);
    int getPathStroke(int roiIndex, int shapeIndex);
    string getPathStrokeDashArray(int roiIndex, int shapeIndex);
    double getPathStrokeWidth(int roiIndex, int shapeIndex);
    int getPathTheC(int roiIndex, int shapeIndex);
    int getPathTheT(int roiIndex, int shapeIndex);
    int getPathTheZ(int roiIndex, int shapeIndex);
    string getPathTransform(int roiIndex, int shapeIndex);

    // - Pixels property retrieval -

    string getPixelsAnnotationRef(int imageIndex, int annotationRefIndex);
    dimensionorder getPixelsDimensionOrder(int imageIndex);
    string getPixelsID(int imageIndex);
    double getPixelsPhysicalSizeX(int imageIndex);
    double getPixelsPhysicalSizeY(int imageIndex);
    double getPixelsPhysicalSizeZ(int imageIndex);
    positiveint getPixelsSizeC(int imageIndex);
    positiveint getPixelsSizeT(int imageIndex);
    positiveint getPixelsSizeX(int imageIndex);
    positiveint getPixelsSizeY(int imageIndex);
    positiveint getPixelsSizeZ(int imageIndex);
    double getPixelsTimeIncrement(int imageIndex);
    pixeltype getPixelsType(int imageIndex);

    // - PixelsAnnotationRef property retrieval -


    // - PixelsBinData property retrieval -

    bool getPixelsBinDataBigEndian(int imageIndex, int binDataIndex);

    // - Plane property retrieval -

    string getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex);
    double getPlaneDeltaT(int imageIndex, int planeIndex);
    double getPlaneExposureTime(int imageIndex, int planeIndex);
    string getPlaneHashSHA1(int imageIndex, int planeIndex);
    double getPlanePositionX(int imageIndex, int planeIndex);
    double getPlanePositionY(int imageIndex, int planeIndex);
    double getPlanePositionZ(int imageIndex, int planeIndex);
    int getPlaneTheC(int imageIndex, int planeIndex);
    int getPlaneTheT(int imageIndex, int planeIndex);
    int getPlaneTheZ(int imageIndex, int planeIndex);

    // - PlaneAnnotationRef property retrieval -


    // - Plate property retrieval -

    string getPlateAnnotationRef(int plateIndex, int annotationRefIndex);
    namingconvention getPlateColumnNamingConvention(int plateIndex);
    int getPlateColumns(int plateIndex);
    string getPlateDescription(int plateIndex);
    string getPlateExternalIdentifier(int plateIndex);
    string getPlateID(int plateIndex);
    string getPlateName(int plateIndex);
    namingconvention getPlateRowNamingConvention(int plateIndex);
    int getPlateRows(int plateIndex);
    string getPlateScreenRef(int plateIndex, int screenRefIndex);
    string getPlateStatus(int plateIndex);
    double getPlateWellOriginX(int plateIndex);
    double getPlateWellOriginY(int plateIndex);

    // - PlateAcquisition property retrieval -

    string getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex);
    string getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex);
    string getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex);
    string getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex);
    int getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex);
    string getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex);
    string getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex);
    string getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

    // - PlateAcquisitionAnnotationRef property retrieval -


    // - PlateAnnotationRef property retrieval -


    // - PlateRef property retrieval -


    // - Point property retrieval -

    string getPointDescription(int roiIndex, int shapeIndex);
    int getPointFill(int roiIndex, int shapeIndex);
    int getPointFontSize(int roiIndex, int shapeIndex);
    string getPointID(int roiIndex, int shapeIndex);
    string getPointLabel(int roiIndex, int shapeIndex);
    string getPointName(int roiIndex, int shapeIndex);
    int getPointStroke(int roiIndex, int shapeIndex);
    string getPointStrokeDashArray(int roiIndex, int shapeIndex);
    double getPointStrokeWidth(int roiIndex, int shapeIndex);
    int getPointTheC(int roiIndex, int shapeIndex);
    int getPointTheT(int roiIndex, int shapeIndex);
    int getPointTheZ(int roiIndex, int shapeIndex);
    string getPointTransform(int roiIndex, int shapeIndex);
    double getPointX(int roiIndex, int shapeIndex);
    double getPointY(int roiIndex, int shapeIndex);

    // - Polyline property retrieval -

    bool getPolylineClosed(int roiIndex, int shapeIndex);
    string getPolylineDescription(int roiIndex, int shapeIndex);
    int getPolylineFill(int roiIndex, int shapeIndex);
    int getPolylineFontSize(int roiIndex, int shapeIndex);
    string getPolylineID(int roiIndex, int shapeIndex);
    string getPolylineLabel(int roiIndex, int shapeIndex);
    string getPolylineName(int roiIndex, int shapeIndex);
    string getPolylinePoints(int roiIndex, int shapeIndex);
    int getPolylineStroke(int roiIndex, int shapeIndex);
    string getPolylineStrokeDashArray(int roiIndex, int shapeIndex);
    double getPolylineStrokeWidth(int roiIndex, int shapeIndex);
    int getPolylineTheC(int roiIndex, int shapeIndex);
    int getPolylineTheT(int roiIndex, int shapeIndex);
    int getPolylineTheZ(int roiIndex, int shapeIndex);
    string getPolylineTransform(int roiIndex, int shapeIndex);

    // - Project property retrieval -

    string getProjectAnnotationRef(int projectIndex, int annotationRefIndex);
    string getProjectDescription(int projectIndex);
    string getProjectExperimenterRef(int projectIndex);
    string getProjectGroupRef(int projectIndex);
    string getProjectID(int projectIndex);
    string getProjectName(int projectIndex);

    // - ProjectAnnotationRef property retrieval -


    // - ProjectRef property retrieval -


    // - ROI property retrieval -

    string getROIAnnotationRef(int roiIndex, int annotationRefIndex);
    string getROIDescription(int roiIndex);
    string getROIID(int roiIndex);
    string getROIName(int roiIndex);
    string getROINamespace(int roiIndex);

    // - ROIAnnotationRef property retrieval -


    // - Reagent property retrieval -

    string getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex);
    string getReagentDescription(int screenIndex, int reagentIndex);
    string getReagentID(int screenIndex, int reagentIndex);
    string getReagentName(int screenIndex, int reagentIndex);
    string getReagentReagentIdentifier(int screenIndex, int reagentIndex);

    // - ReagentAnnotationRef property retrieval -


    // - Rectangle property retrieval -

    string getRectangleDescription(int roiIndex, int shapeIndex);
    int getRectangleFill(int roiIndex, int shapeIndex);
    int getRectangleFontSize(int roiIndex, int shapeIndex);
    double getRectangleHeight(int roiIndex, int shapeIndex);
    string getRectangleID(int roiIndex, int shapeIndex);
    string getRectangleLabel(int roiIndex, int shapeIndex);
    string getRectangleName(int roiIndex, int shapeIndex);
    int getRectangleStroke(int roiIndex, int shapeIndex);
    string getRectangleStrokeDashArray(int roiIndex, int shapeIndex);
    double getRectangleStrokeWidth(int roiIndex, int shapeIndex);
    int getRectangleTheC(int roiIndex, int shapeIndex);
    int getRectangleTheT(int roiIndex, int shapeIndex);
    int getRectangleTheZ(int roiIndex, int shapeIndex);
    string getRectangleTransform(int roiIndex, int shapeIndex);
    double getRectangleWidth(int roiIndex, int shapeIndex);
    double getRectangleX(int roiIndex, int shapeIndex);
    double getRectangleY(int roiIndex, int shapeIndex);

    // - Screen property retrieval -

    string getScreenAnnotationRef(int screenIndex, int annotationRefIndex);
    string getScreenDescription(int screenIndex);
    string getScreenID(int screenIndex);
    string getScreenName(int screenIndex);
    string getScreenPlateRef(int screenIndex, int plateRefIndex);
    string getScreenProtocolDescription(int screenIndex);
    string getScreenProtocolIdentifier(int screenIndex);
    string getScreenReagentSetDescription(int screenIndex);
    string getScreenReagentSetIdentifier(int screenIndex);
    string getScreenType(int screenIndex);

    // - ScreenAnnotationRef property retrieval -


    // - ScreenRef property retrieval -


    // - ShapeAnnotationRef property retrieval -


    // - StageLabel property retrieval -

    string getStageLabelName(int imageIndex);
    double getStageLabelX(int imageIndex);
    double getStageLabelY(int imageIndex);
    double getStageLabelZ(int imageIndex);

    // - StringAnnotation property retrieval -

    string getStringAnnotationID(int stringAnnotationIndex);
    string getStringAnnotationNamespace(int stringAnnotationIndex);
    string getStringAnnotationValue(int stringAnnotationIndex);

    // - Text property retrieval -

    string getTextDescription(int roiIndex, int shapeIndex);
    int getTextFill(int roiIndex, int shapeIndex);
    int getTextFontSize(int roiIndex, int shapeIndex);
    string getTextID(int roiIndex, int shapeIndex);
    string getTextLabel(int roiIndex, int shapeIndex);
    string getTextName(int roiIndex, int shapeIndex);
    int getTextStroke(int roiIndex, int shapeIndex);
    string getTextStrokeDashArray(int roiIndex, int shapeIndex);
    double getTextStrokeWidth(int roiIndex, int shapeIndex);
    int getTextTheC(int roiIndex, int shapeIndex);
    int getTextTheT(int roiIndex, int shapeIndex);
    int getTextTheZ(int roiIndex, int shapeIndex);
    string getTextTransform(int roiIndex, int shapeIndex);
    string getTextValue(int roiIndex, int shapeIndex);
    double getTextX(int roiIndex, int shapeIndex);
    double getTextY(int roiIndex, int shapeIndex);

    // - TiffData property retrieval -

    int getTiffDataFirstC(int imageIndex, int tiffDataIndex);
    int getTiffDataFirstT(int imageIndex, int tiffDataIndex);
    int getTiffDataFirstZ(int imageIndex, int tiffDataIndex);
    int getTiffDataIFD(int imageIndex, int tiffDataIndex);
    int getTiffDataPlaneCount(int imageIndex, int tiffDataIndex);

    // - TimestampAnnotation property retrieval -

    string getTimestampAnnotationID(int timestampAnnotationIndex);
    string getTimestampAnnotationNamespace(int timestampAnnotationIndex);
    string getTimestampAnnotationValue(int timestampAnnotationIndex);

    // - TransmittanceRange property retrieval -

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);
    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);
    percentfraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

    // - UUID property retrieval -

    string getUUIDFileName(int imageIndex, int tiffDataIndex);
    string getUUIDValue(int imageIndex, int tiffDataIndex);

    // - Well property retrieval -

    string getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex);
    int getWellColor(int plateIndex, int wellIndex);
    nonnegativeint getWellColumn(int plateIndex, int wellIndex);
    string getWellExternalDescription(int plateIndex, int wellIndex);
    string getWellExternalIdentifier(int plateIndex, int wellIndex);
    string getWellID(int plateIndex, int wellIndex);
    string getWellReagentRef(int plateIndex, int wellIndex);
    nonnegativeint getWellRow(int plateIndex, int wellIndex);
    string getWellStatus(int plateIndex, int wellIndex);

    // - WellAnnotationRef property retrieval -


    // - WellSample property retrieval -

    string getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);
    string getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);
    string getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);
    nonnegativeint getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);
    double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex);
    double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

    // - WellSampleAnnotationRef property retrieval -


    // - WellSampleRef property retrieval -


    // - XMLAnnotation property retrieval -

    string getXMLAnnotationID(int xmlAnnotationIndex);
    string getXMLAnnotationNamespace(int xmlAnnotationIndex);
    string getXMLAnnotationValue(int xmlAnnotationIndex);

    // -- MetadataStore methods --

    void createRoot();
    //void setRoot(Object root);
    //Object getRoot();

    // - Entity storage -

    void setUUID(string uuid);

    // - Arc property storage -
    void setArcID(string id, int instrumentIndex, int arcIndex);
    void setArcLotNumber(string lotNumber, int instrumentIndex, int arcIndex);
    void setArcManufacturer(string manufacturer, int instrumentIndex, int arcIndex);
    void setArcModel(string model, int instrumentIndex, int arcIndex);
    void setArcPower(double power, int instrumentIndex, int arcIndex);
    void setArcSerialNumber(string serialNumber, int instrumentIndex, int arcIndex);
    void setArcType(arctype type, int instrumentIndex, int arcIndex);

    // - BooleanAnnotation property storage -
    void setBooleanAnnotationID(string id, int booleanAnnotationIndex);
    void setBooleanAnnotationNamespace(string namespace, int booleanAnnotationIndex);
    void setBooleanAnnotationValue(bool value, int booleanAnnotationIndex);

    // - Channel property storage -
    void setChannelAcquisitionMode(acquisitionmode acquisitionMode, int imageIndex, int channelIndex);
    void setChannelAnnotationRef(string annotationRef, int imageIndex, int channelIndex, int annotationRefIndex);
    void setChannelColor(int color, int imageIndex, int channelIndex);
    void setChannelContrastMethod(contrastmethod contrastMethod, int imageIndex, int channelIndex);
    void setChannelEmissionWavelength(positiveint emissionWavelength, int imageIndex, int channelIndex);
    void setChannelExcitationWavelength(positiveint excitationWavelength, int imageIndex, int channelIndex);
    void setChannelFilterSetRef(string filterSetRef, int imageIndex, int channelIndex);
    void setChannelFluor(string fluor, int imageIndex, int channelIndex);
    void setChannelID(string id, int imageIndex, int channelIndex);
    void setChannelIlluminationType(illuminationtype illuminationType, int imageIndex, int channelIndex);
    void setChannelLightSourceSettingsAttenuation(percentfraction lightSourceSettingsAttenuation, int imageIndex, int channelIndex);
    void setChannelLightSourceSettingsID(string lightSourceSettingsID, int imageIndex, int channelIndex);
    void setChannelLightSourceSettingsWavelength(positiveint lightSourceSettingsWavelength, int imageIndex, int channelIndex);
    void setChannelNDFilter(double ndFilter, int imageIndex, int channelIndex);
    void setChannelName(string name, int imageIndex, int channelIndex);
    void setChannelOTFRef(string otfRef, int imageIndex, int channelIndex);
    void setChannelPinholeSize(double pinholeSize, int imageIndex, int channelIndex);
    void setChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int channelIndex);
    void setChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int channelIndex);

    // - ChannelAnnotationRef property storage -

    // - Dataset property storage -
    void setDatasetAnnotationRef(string annotationRef, int datasetIndex, int annotationRefIndex);
    void setDatasetDescription(string description, int datasetIndex);
    void setDatasetExperimenterRef(string experimenterRef, int datasetIndex);
    void setDatasetGroupRef(string groupRef, int datasetIndex);
    void setDatasetID(string id, int datasetIndex);
    void setDatasetName(string name, int datasetIndex);
    void setDatasetProjectRef(string projectRef, int datasetIndex, int projectRefIndex);

    // - DatasetAnnotationRef property storage -

    // - DatasetRef property storage -

    // - Detector property storage -
    void setDetectorAmplificationGain(double amplificationGain, int instrumentIndex, int detectorIndex);
    void setDetectorGain(double gain, int instrumentIndex, int detectorIndex);
    void setDetectorID(string id, int instrumentIndex, int detectorIndex);
    void setDetectorLotNumber(string lotNumber, int instrumentIndex, int detectorIndex);
    void setDetectorManufacturer(string manufacturer, int instrumentIndex, int detectorIndex);
    void setDetectorModel(string model, int instrumentIndex, int detectorIndex);
    void setDetectorOffset(double offset, int instrumentIndex, int detectorIndex);
    void setDetectorSerialNumber(string serialNumber, int instrumentIndex, int detectorIndex);
    void setDetectorType(detectortype type, int instrumentIndex, int detectorIndex);
    void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex);
    void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex);

    // - DetectorSettings property storage -
    void setDetectorSettingsBinning(binning binning, int imageIndex, int channelIndex);
    void setDetectorSettingsGain(double gain, int imageIndex, int channelIndex);
    void setDetectorSettingsID(string id, int imageIndex, int channelIndex);
    void setDetectorSettingsOffset(double offset, int imageIndex, int channelIndex);
    void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int channelIndex);
    void setDetectorSettingsVoltage(double voltage, int imageIndex, int channelIndex);

    // - Dichroic property storage -
    void setDichroicID(string id, int instrumentIndex, int dichroicIndex);
    void setDichroicLotNumber(string lotNumber, int instrumentIndex, int dichroicIndex);
    void setDichroicManufacturer(string manufacturer, int instrumentIndex, int dichroicIndex);
    void setDichroicModel(string model, int instrumentIndex, int dichroicIndex);
    void setDichroicSerialNumber(string serialNumber, int instrumentIndex, int dichroicIndex);

    // - DoubleAnnotation property storage -
    void setDoubleAnnotationID(string id, int doubleAnnotationIndex);
    void setDoubleAnnotationNamespace(string namespace, int doubleAnnotationIndex);
    void setDoubleAnnotationValue(double value, int doubleAnnotationIndex);

    // - Ellipse property storage -
    void setEllipseDescription(string description, int roiIndex, int shapeIndex);
    void setEllipseFill(int fill, int roiIndex, int shapeIndex);
    void setEllipseFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setEllipseID(string id, int roiIndex, int shapeIndex);
    void setEllipseLabel(string label, int roiIndex, int shapeIndex);
    void setEllipseName(string name, int roiIndex, int shapeIndex);
    void setEllipseRadiusX(double radiusX, int roiIndex, int shapeIndex);
    void setEllipseRadiusY(double radiusY, int roiIndex, int shapeIndex);
    void setEllipseStroke(int stroke, int roiIndex, int shapeIndex);
    void setEllipseStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setEllipseStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setEllipseTheC(int theC, int roiIndex, int shapeIndex);
    void setEllipseTheT(int theT, int roiIndex, int shapeIndex);
    void setEllipseTheZ(int theZ, int roiIndex, int shapeIndex);
    void setEllipseTransform(string transform, int roiIndex, int shapeIndex);
    void setEllipseX(double x, int roiIndex, int shapeIndex);
    void setEllipseY(double y, int roiIndex, int shapeIndex);

    // - Experiment property storage -
    void setExperimentDescription(string description, int experimentIndex);
    void setExperimentExperimenterRef(string experimenterRef, int experimentIndex);
    void setExperimentID(string id, int experimentIndex);
    void setExperimentType(experimenttype type, int experimentIndex);

    // - Experimenter property storage -
    void setExperimenterAnnotationRef(string annotationRef, int experimenterIndex, int annotationRefIndex);
    void setExperimenterDisplayName(string displayName, int experimenterIndex);
    void setExperimenterEmail(string email, int experimenterIndex);
    void setExperimenterFirstName(string firstName, int experimenterIndex);
    void setExperimenterGroupRef(string groupRef, int experimenterIndex, int groupRefIndex);
    void setExperimenterID(string id, int experimenterIndex);
    void setExperimenterInstitution(string institution, int experimenterIndex);
    void setExperimenterLastName(string lastName, int experimenterIndex);
    void setExperimenterMiddleName(string middleName, int experimenterIndex);
    void setExperimenterUserName(string userName, int experimenterIndex);

    // - ExperimenterAnnotationRef property storage -

    // - ExperimenterGroupRef property storage -

    // - Filament property storage -
    void setFilamentID(string id, int instrumentIndex, int filamentIndex);
    void setFilamentLotNumber(string lotNumber, int instrumentIndex, int filamentIndex);
    void setFilamentManufacturer(string manufacturer, int instrumentIndex, int filamentIndex);
    void setFilamentModel(string model, int instrumentIndex, int filamentIndex);
    void setFilamentPower(double power, int instrumentIndex, int filamentIndex);
    void setFilamentSerialNumber(string serialNumber, int instrumentIndex, int filamentIndex);
    void setFilamentType(filamenttype type, int instrumentIndex, int filamentIndex);

    // - FileAnnotation property storage -
    void setFileAnnotationBinaryFileFileName(string binaryFileFileName, int fileAnnotationIndex);
    void setFileAnnotationBinaryFileMIMEType(string binaryFileMIMEType, int fileAnnotationIndex);
    void setFileAnnotationBinaryFileSize(int binaryFileSize, int fileAnnotationIndex);
    void setFileAnnotationID(string id, int fileAnnotationIndex);
    void setFileAnnotationNamespace(string namespace, int fileAnnotationIndex);

    // - Filter property storage -
    void setFilterFilterWheel(string filterWheel, int instrumentIndex, int filterIndex);
    void setFilterID(string id, int instrumentIndex, int filterIndex);
    void setFilterLotNumber(string lotNumber, int instrumentIndex, int filterIndex);
    void setFilterManufacturer(string manufacturer, int instrumentIndex, int filterIndex);
    void setFilterModel(string model, int instrumentIndex, int filterIndex);
    void setFilterSerialNumber(string serialNumber, int instrumentIndex, int filterIndex);
    void setFilterType(filtertype type, int instrumentIndex, int filterIndex);

    // - FilterSet property storage -
    void setFilterSetDichroicRef(string dichroicRef, int instrumentIndex, int filterSetIndex);
    void setFilterSetEmissionFilterRef(string emissionFilterRef, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);
    void setFilterSetExcitationFilterRef(string excitationFilterRef, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);
    void setFilterSetID(string id, int instrumentIndex, int filterSetIndex);
    void setFilterSetLotNumber(string lotNumber, int instrumentIndex, int filterSetIndex);
    void setFilterSetManufacturer(string manufacturer, int instrumentIndex, int filterSetIndex);
    void setFilterSetModel(string model, int instrumentIndex, int filterSetIndex);
    void setFilterSetSerialNumber(string serialNumber, int instrumentIndex, int filterSetIndex);

    // - FilterSetEmissionFilterRef property storage -

    // - FilterSetExcitationFilterRef property storage -

    // - Group property storage -
    void setGroupContact(string contact, int groupIndex);
    void setGroupDescription(string description, int groupIndex);
    void setGroupID(string id, int groupIndex);
    void setGroupLeader(string leader, int groupIndex);
    void setGroupName(string name, int groupIndex);

    // - Image property storage -
    void setImageAcquiredDate(string acquiredDate, int imageIndex);
    void setImageAnnotationRef(string annotationRef, int imageIndex, int annotationRefIndex);
    void setImageDatasetRef(string datasetRef, int imageIndex, int datasetRefIndex);
    void setImageDescription(string description, int imageIndex);
    void setImageExperimentRef(string experimentRef, int imageIndex);
    void setImageExperimenterRef(string experimenterRef, int imageIndex);
    void setImageGroupRef(string groupRef, int imageIndex);
    void setImageID(string id, int imageIndex);
    void setImageInstrumentRef(string instrumentRef, int imageIndex);
    void setImageMicrobeamManipulationRef(string microbeamManipulationRef, int imageIndex, int microbeamManipulationRefIndex);
    void setImageName(string name, int imageIndex);
    void setImageObjectiveSettingsCorrectionCollar(double objectiveSettingsCorrectionCollar, int imageIndex);
    void setImageObjectiveSettingsID(string objectiveSettingsID, int imageIndex);
    void setImageObjectiveSettingsMedium(medium objectiveSettingsMedium, int imageIndex);
    void setImageObjectiveSettingsRefractiveIndex(double objectiveSettingsRefractiveIndex, int imageIndex);
    void setImageROIRef(string roiRef, int imageIndex, int roiRefIndex);

    // - ImageAnnotationRef property storage -

    // - ImageROIRef property storage -

    // - ImagingEnvironment property storage -
    void setImagingEnvironmentAirPressure(double airPressure, int imageIndex);
    void setImagingEnvironmentCO2Percent(percentfraction cO2Percent, int imageIndex);
    void setImagingEnvironmentHumidity(percentfraction humidity, int imageIndex);
    void setImagingEnvironmentTemperature(double temperature, int imageIndex);

    // - Instrument property storage -
    void setInstrumentID(string id, int instrumentIndex);

    // - Laser property storage -
    void setLaserFrequencyMultiplication(positiveint frequencyMultiplication, int instrumentIndex, int laserIndex);
    void setLaserID(string id, int instrumentIndex, int laserIndex);
    void setLaserLaserMedium(lasermedium laserMedium, int instrumentIndex, int laserIndex);
    void setLaserLotNumber(string lotNumber, int instrumentIndex, int laserIndex);
    void setLaserManufacturer(string manufacturer, int instrumentIndex, int laserIndex);
    void setLaserModel(string model, int instrumentIndex, int laserIndex);
    void setLaserPockelCell(bool pockelCell, int instrumentIndex, int laserIndex);
    void setLaserPower(double power, int instrumentIndex, int laserIndex);
    void setLaserPulse(pulse pulse, int instrumentIndex, int laserIndex);
    void setLaserPump(string pump, int instrumentIndex, int laserIndex);
    void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int laserIndex);
    void setLaserSerialNumber(string serialNumber, int instrumentIndex, int laserIndex);
    void setLaserTuneable(bool tuneable, int instrumentIndex, int laserIndex);
    void setLaserType(lasertype type, int instrumentIndex, int laserIndex);
    void setLaserWavelength(positiveint wavelength, int instrumentIndex, int laserIndex);

    // - LightEmittingDiode property storage -
    void setLightEmittingDiodeID(string id, int instrumentIndex, int lightEmittingDiodeIndex);
    void setLightEmittingDiodeLotNumber(string lotNumber, int instrumentIndex, int lightEmittingDiodeIndex);
    void setLightEmittingDiodeManufacturer(string manufacturer, int instrumentIndex, int lightEmittingDiodeIndex);
    void setLightEmittingDiodeModel(string model, int instrumentIndex, int lightEmittingDiodeIndex);
    void setLightEmittingDiodePower(double power, int instrumentIndex, int lightEmittingDiodeIndex);
    void setLightEmittingDiodeSerialNumber(string serialNumber, int instrumentIndex, int lightEmittingDiodeIndex);

    // - LightPath property storage -
    void setLightPathDichroicRef(string dichroicRef, int imageIndex, int channelIndex);
    void setLightPathEmissionFilterRef(string emissionFilterRef, int imageIndex, int channelIndex, int emissionFilterRefIndex);
    void setLightPathExcitationFilterRef(string excitationFilterRef, int imageIndex, int channelIndex, int excitationFilterRefIndex);

    // - LightPathEmissionFilterRef property storage -

    // - LightPathExcitationFilterRef property storage -

    // - Line property storage -
    void setLineDescription(string description, int roiIndex, int shapeIndex);
    void setLineFill(int fill, int roiIndex, int shapeIndex);
    void setLineFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setLineID(string id, int roiIndex, int shapeIndex);
    void setLineLabel(string label, int roiIndex, int shapeIndex);
    void setLineName(string name, int roiIndex, int shapeIndex);
    void setLineStroke(int stroke, int roiIndex, int shapeIndex);
    void setLineStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setLineStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setLineTheC(int theC, int roiIndex, int shapeIndex);
    void setLineTheT(int theT, int roiIndex, int shapeIndex);
    void setLineTheZ(int theZ, int roiIndex, int shapeIndex);
    void setLineTransform(string transform, int roiIndex, int shapeIndex);
    void setLineX1(double x1, int roiIndex, int shapeIndex);
    void setLineX2(double x2, int roiIndex, int shapeIndex);
    void setLineY1(double y1, int roiIndex, int shapeIndex);
    void setLineY2(double y2, int roiIndex, int shapeIndex);

    // - ListAnnotation property storage -
    void setListAnnotationAnnotationRef(string annotationRef, int listAnnotationIndex, int annotationRefIndex);
    void setListAnnotationID(string id, int listAnnotationIndex);
    void setListAnnotationNamespace(string namespace, int listAnnotationIndex);

    // - ListAnnotationAnnotationRef property storage -

    // - LongAnnotation property storage -
    void setLongAnnotationID(string id, int longAnnotationIndex);
    void setLongAnnotationNamespace(string namespace, int longAnnotationIndex);
    void setLongAnnotationValue(long value, int longAnnotationIndex);

    // - Mask property storage -
    void setMaskDescription(string description, int roiIndex, int shapeIndex);
    void setMaskFill(int fill, int roiIndex, int shapeIndex);
    void setMaskFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setMaskID(string id, int roiIndex, int shapeIndex);
    void setMaskLabel(string label, int roiIndex, int shapeIndex);
    void setMaskName(string name, int roiIndex, int shapeIndex);
    void setMaskStroke(int stroke, int roiIndex, int shapeIndex);
    void setMaskStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setMaskStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setMaskTheC(int theC, int roiIndex, int shapeIndex);
    void setMaskTheT(int theT, int roiIndex, int shapeIndex);
    void setMaskTheZ(int theZ, int roiIndex, int shapeIndex);
    void setMaskTransform(string transform, int roiIndex, int shapeIndex);
    void setMaskX(double x, int roiIndex, int shapeIndex);
    void setMaskY(double y, int roiIndex, int shapeIndex);

    // - MicrobeamManipulation property storage -
    void setMicrobeamManipulationExperimenterRef(string experimenterRef, int experimentIndex, int microbeamManipulationIndex);
    void setMicrobeamManipulationID(string id, int experimentIndex, int microbeamManipulationIndex);
    void setMicrobeamManipulationROIRef(string roiRef, int experimentIndex, int microbeamManipulationIndex, int roiRefIndex);
    void setMicrobeamManipulationType(microbeammanipulationtype type, int experimentIndex, int microbeamManipulationIndex);

    // - MicrobeamManipulationLightSourceSettings property storage -
    void setMicrobeamManipulationLightSourceSettingsAttenuation(percentfraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);
    void setMicrobeamManipulationLightSourceSettingsID(string id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);
    void setMicrobeamManipulationLightSourceSettingsWavelength(positiveint wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

    // - MicrobeamManipulationROIRef property storage -

    // - MicrobeamManipulationRef property storage -

    // - Microscope property storage -
    void setMicroscopeLotNumber(string lotNumber, int instrumentIndex);
    void setMicroscopeManufacturer(string manufacturer, int instrumentIndex);
    void setMicroscopeModel(string model, int instrumentIndex);
    void setMicroscopeSerialNumber(string serialNumber, int instrumentIndex);
    void setMicroscopeType(microscopetype type, int instrumentIndex);

    // - OTF property storage -
    void setOTFBinaryFileFileName(string binaryFileFileName, int instrumentIndex, int otfIndex);
    void setOTFBinaryFileMIMEType(string binaryFileMIMEType, int instrumentIndex, int otfIndex);
    void setOTFBinaryFileSize(int binaryFileSize, int instrumentIndex, int otfIndex);
    void setOTFFilterSetRef(string filterSetRef, int instrumentIndex, int otfIndex);
    void setOTFID(string id, int instrumentIndex, int otfIndex);
    void setOTFObjectiveSettingsCorrectionCollar(double objectiveSettingsCorrectionCollar, int instrumentIndex, int otfIndex);
    void setOTFObjectiveSettingsID(string objectiveSettingsID, int instrumentIndex, int otfIndex);
    void setOTFObjectiveSettingsMedium(medium objectiveSettingsMedium, int instrumentIndex, int otfIndex);
    void setOTFObjectiveSettingsRefractiveIndex(double objectiveSettingsRefractiveIndex, int instrumentIndex, int otfIndex);
    void setOTFOpticalAxisAveraged(bool opticalAxisAveraged, int instrumentIndex, int otfIndex);
    void setOTFSizeX(positiveint sizeX, int instrumentIndex, int otfIndex);
    void setOTFSizeY(positiveint sizeY, int instrumentIndex, int otfIndex);
    void setOTFType(pixeltype type, int instrumentIndex, int otfIndex);

    // - Objective property storage -
    void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveCorrection(correction correction, int instrumentIndex, int objectiveIndex);
    void setObjectiveID(string id, int instrumentIndex, int objectiveIndex);
    void setObjectiveImmersion(immersion immersion, int instrumentIndex, int objectiveIndex);
    void setObjectiveIris(bool iris, int instrumentIndex, int objectiveIndex);
    void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex);
    void setObjectiveLotNumber(string lotNumber, int instrumentIndex, int objectiveIndex);
    void setObjectiveManufacturer(string manufacturer, int instrumentIndex, int objectiveIndex);
    void setObjectiveModel(string model, int instrumentIndex, int objectiveIndex);
    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveSerialNumber(string serialNumber, int instrumentIndex, int objectiveIndex);
    void setObjectiveWorkingDistance(double workingDistance, int instrumentIndex, int objectiveIndex);

    // - Path property storage -
    void setPathDefinition(string definition, int roiIndex, int shapeIndex);
    void setPathDescription(string description, int roiIndex, int shapeIndex);
    void setPathFill(int fill, int roiIndex, int shapeIndex);
    void setPathFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setPathID(string id, int roiIndex, int shapeIndex);
    void setPathLabel(string label, int roiIndex, int shapeIndex);
    void setPathName(string name, int roiIndex, int shapeIndex);
    void setPathStroke(int stroke, int roiIndex, int shapeIndex);
    void setPathStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setPathStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setPathTheC(int theC, int roiIndex, int shapeIndex);
    void setPathTheT(int theT, int roiIndex, int shapeIndex);
    void setPathTheZ(int theZ, int roiIndex, int shapeIndex);
    void setPathTransform(string transform, int roiIndex, int shapeIndex);

    // - Pixels property storage -
    void setPixelsAnnotationRef(string annotationRef, int imageIndex, int annotationRefIndex);
    void setPixelsDimensionOrder(dimensionorder dimensionOrder, int imageIndex);
    void setPixelsID(string id, int imageIndex);
    void setPixelsPhysicalSizeX(double physicalSizeX, int imageIndex);
    void setPixelsPhysicalSizeY(double physicalSizeY, int imageIndex);
    void setPixelsPhysicalSizeZ(double physicalSizeZ, int imageIndex);
    void setPixelsSizeC(positiveint sizeC, int imageIndex);
    void setPixelsSizeT(positiveint sizeT, int imageIndex);
    void setPixelsSizeX(positiveint sizeX, int imageIndex);
    void setPixelsSizeY(positiveint sizeY, int imageIndex);
    void setPixelsSizeZ(positiveint sizeZ, int imageIndex);
    void setPixelsTimeIncrement(double timeIncrement, int imageIndex);
    void setPixelsType(pixeltype type, int imageIndex);

    // - PixelsAnnotationRef property storage -

    // - PixelsBinData property storage -
    void setPixelsBinDataBigEndian(bool bigEndian, int imageIndex, int binDataIndex);

    // - Plane property storage -
    void setPlaneAnnotationRef(string annotationRef, int imageIndex, int planeIndex, int annotationRefIndex);
    void setPlaneDeltaT(double deltaT, int imageIndex, int planeIndex);
    void setPlaneExposureTime(double exposureTime, int imageIndex, int planeIndex);
    void setPlaneHashSHA1(string hashSHA1, int imageIndex, int planeIndex);
    void setPlanePositionX(double positionX, int imageIndex, int planeIndex);
    void setPlanePositionY(double positionY, int imageIndex, int planeIndex);
    void setPlanePositionZ(double positionZ, int imageIndex, int planeIndex);
    void setPlaneTheC(int theC, int imageIndex, int planeIndex);
    void setPlaneTheT(int theT, int imageIndex, int planeIndex);
    void setPlaneTheZ(int theZ, int imageIndex, int planeIndex);

    // - PlaneAnnotationRef property storage -

    // - Plate property storage -
    void setPlateAnnotationRef(string annotationRef, int plateIndex, int annotationRefIndex);
    void setPlateColumnNamingConvention(namingconvention columnNamingConvention, int plateIndex);
    void setPlateColumns(int columns, int plateIndex);
    void setPlateDescription(string description, int plateIndex);
    void setPlateExternalIdentifier(string externalIdentifier, int plateIndex);
    void setPlateID(string id, int plateIndex);
    void setPlateName(string name, int plateIndex);
    void setPlateRowNamingConvention(namingconvention rowNamingConvention, int plateIndex);
    void setPlateRows(int rows, int plateIndex);
    void setPlateScreenRef(string screenRef, int plateIndex, int screenRefIndex);
    void setPlateStatus(string status, int plateIndex);
    void setPlateWellOriginX(double wellOriginX, int plateIndex);
    void setPlateWellOriginY(double wellOriginY, int plateIndex);

    // - PlateAcquisition property storage -
    void setPlateAcquisitionAnnotationRef(string annotationRef, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex);
    void setPlateAcquisitionDescription(string description, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionEndTime(string endTime, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionID(string id, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionMaximumFieldCount(int maximumFieldCount, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionName(string name, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionStartTime(string startTime, int plateIndex, int plateAcquisitionIndex);
    void setPlateAcquisitionWellSampleRef(string wellSampleRef, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

    // - PlateAcquisitionAnnotationRef property storage -

    // - PlateAnnotationRef property storage -

    // - PlateRef property storage -

    // - Point property storage -
    void setPointDescription(string description, int roiIndex, int shapeIndex);
    void setPointFill(int fill, int roiIndex, int shapeIndex);
    void setPointFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setPointID(string id, int roiIndex, int shapeIndex);
    void setPointLabel(string label, int roiIndex, int shapeIndex);
    void setPointName(string name, int roiIndex, int shapeIndex);
    void setPointStroke(int stroke, int roiIndex, int shapeIndex);
    void setPointStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setPointStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setPointTheC(int theC, int roiIndex, int shapeIndex);
    void setPointTheT(int theT, int roiIndex, int shapeIndex);
    void setPointTheZ(int theZ, int roiIndex, int shapeIndex);
    void setPointTransform(string transform, int roiIndex, int shapeIndex);
    void setPointX(double x, int roiIndex, int shapeIndex);
    void setPointY(double y, int roiIndex, int shapeIndex);

    // - Polyline property storage -
    void setPolylineClosed(bool closed, int roiIndex, int shapeIndex);
    void setPolylineDescription(string description, int roiIndex, int shapeIndex);
    void setPolylineFill(int fill, int roiIndex, int shapeIndex);
    void setPolylineFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setPolylineID(string id, int roiIndex, int shapeIndex);
    void setPolylineLabel(string label, int roiIndex, int shapeIndex);
    void setPolylineName(string name, int roiIndex, int shapeIndex);
    void setPolylinePoints(string points, int roiIndex, int shapeIndex);
    void setPolylineStroke(int stroke, int roiIndex, int shapeIndex);
    void setPolylineStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setPolylineStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setPolylineTheC(int theC, int roiIndex, int shapeIndex);
    void setPolylineTheT(int theT, int roiIndex, int shapeIndex);
    void setPolylineTheZ(int theZ, int roiIndex, int shapeIndex);
    void setPolylineTransform(string transform, int roiIndex, int shapeIndex);

    // - Project property storage -
    void setProjectAnnotationRef(string annotationRef, int projectIndex, int annotationRefIndex);
    void setProjectDescription(string description, int projectIndex);
    void setProjectExperimenterRef(string experimenterRef, int projectIndex);
    void setProjectGroupRef(string groupRef, int projectIndex);
    void setProjectID(string id, int projectIndex);
    void setProjectName(string name, int projectIndex);

    // - ProjectAnnotationRef property storage -

    // - ProjectRef property storage -

    // - ROI property storage -
    void setROIAnnotationRef(string annotationRef, int roiIndex, int annotationRefIndex);
    void setROIDescription(string description, int roiIndex);
    void setROIID(string id, int roiIndex);
    void setROIName(string name, int roiIndex);
    void setROINamespace(string namespace, int roiIndex);

    // - ROIAnnotationRef property storage -

    // - Reagent property storage -
    void setReagentAnnotationRef(string annotationRef, int screenIndex, int reagentIndex, int annotationRefIndex);
    void setReagentDescription(string description, int screenIndex, int reagentIndex);
    void setReagentID(string id, int screenIndex, int reagentIndex);
    void setReagentName(string name, int screenIndex, int reagentIndex);
    void setReagentReagentIdentifier(string reagentIdentifier, int screenIndex, int reagentIndex);

    // - ReagentAnnotationRef property storage -

    // - Rectangle property storage -
    void setRectangleDescription(string description, int roiIndex, int shapeIndex);
    void setRectangleFill(int fill, int roiIndex, int shapeIndex);
    void setRectangleFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setRectangleHeight(double height, int roiIndex, int shapeIndex);
    void setRectangleID(string id, int roiIndex, int shapeIndex);
    void setRectangleLabel(string label, int roiIndex, int shapeIndex);
    void setRectangleName(string name, int roiIndex, int shapeIndex);
    void setRectangleStroke(int stroke, int roiIndex, int shapeIndex);
    void setRectangleStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setRectangleStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setRectangleTheC(int theC, int roiIndex, int shapeIndex);
    void setRectangleTheT(int theT, int roiIndex, int shapeIndex);
    void setRectangleTheZ(int theZ, int roiIndex, int shapeIndex);
    void setRectangleTransform(string transform, int roiIndex, int shapeIndex);
    void setRectangleWidth(double width, int roiIndex, int shapeIndex);
    void setRectangleX(double x, int roiIndex, int shapeIndex);
    void setRectangleY(double y, int roiIndex, int shapeIndex);

    // - Screen property storage -
    void setScreenAnnotationRef(string annotationRef, int screenIndex, int annotationRefIndex);
    void setScreenDescription(string description, int screenIndex);
    void setScreenID(string id, int screenIndex);
    void setScreenName(string name, int screenIndex);
    void setScreenPlateRef(string plateRef, int screenIndex, int plateRefIndex);
    void setScreenProtocolDescription(string protocolDescription, int screenIndex);
    void setScreenProtocolIdentifier(string protocolIdentifier, int screenIndex);
    void setScreenReagentSetDescription(string reagentSetDescription, int screenIndex);
    void setScreenReagentSetIdentifier(string reagentSetIdentifier, int screenIndex);
    void setScreenType(string type, int screenIndex);

    // - ScreenAnnotationRef property storage -

    // - ScreenRef property storage -

    // - ShapeAnnotationRef property storage -

    // - StageLabel property storage -
    void setStageLabelName(string name, int imageIndex);
    void setStageLabelX(double x, int imageIndex);
    void setStageLabelY(double y, int imageIndex);
    void setStageLabelZ(double z, int imageIndex);

    // - StringAnnotation property storage -
    void setStringAnnotationID(string id, int stringAnnotationIndex);
    void setStringAnnotationNamespace(string namespace, int stringAnnotationIndex);
    void setStringAnnotationValue(string value, int stringAnnotationIndex);

    // - Text property storage -
    void setTextDescription(string description, int roiIndex, int shapeIndex);
    void setTextFill(int fill, int roiIndex, int shapeIndex);
    void setTextFontSize(int fontSize, int roiIndex, int shapeIndex);
    void setTextID(string id, int roiIndex, int shapeIndex);
    void setTextLabel(string label, int roiIndex, int shapeIndex);
    void setTextName(string name, int roiIndex, int shapeIndex);
    void setTextStroke(int stroke, int roiIndex, int shapeIndex);
    void setTextStrokeDashArray(string strokeDashArray, int roiIndex, int shapeIndex);
    void setTextStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex);
    void setTextTheC(int theC, int roiIndex, int shapeIndex);
    void setTextTheT(int theT, int roiIndex, int shapeIndex);
    void setTextTheZ(int theZ, int roiIndex, int shapeIndex);
    void setTextTransform(string transform, int roiIndex, int shapeIndex);
    void setTextValue(string value, int roiIndex, int shapeIndex);
    void setTextX(double x, int roiIndex, int shapeIndex);
    void setTextY(double y, int roiIndex, int shapeIndex);

    // - TiffData property storage -
    void setTiffDataFirstC(int firstC, int imageIndex, int tiffDataIndex);
    void setTiffDataFirstT(int firstT, int imageIndex, int tiffDataIndex);
    void setTiffDataFirstZ(int firstZ, int imageIndex, int tiffDataIndex);
    void setTiffDataIFD(int ifd, int imageIndex, int tiffDataIndex);
    void setTiffDataPlaneCount(int planeCount, int imageIndex, int tiffDataIndex);

    // - TimestampAnnotation property storage -
    void setTimestampAnnotationID(string id, int timestampAnnotationIndex);
    void setTimestampAnnotationNamespace(string namespace, int timestampAnnotationIndex);
    void setTimestampAnnotationValue(string value, int timestampAnnotationIndex);

    // - TransmittanceRange property storage -
    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex);
    void setTransmittanceRangeTransmittance(percentfraction transmittance, int instrumentIndex, int filterIndex);

    // - UUID property storage -
    void setUUIDFileName(string fileName, int imageIndex, int tiffDataIndex);
    void setUUIDValue(string value, int imageIndex, int tiffDataIndex);

    // - Well property storage -
    void setWellAnnotationRef(string annotationRef, int plateIndex, int wellIndex, int annotationRefIndex);
    void setWellColor(int color, int plateIndex, int wellIndex);
    void setWellColumn(nonnegativeint column, int plateIndex, int wellIndex);
    void setWellExternalDescription(string externalDescription, int plateIndex, int wellIndex);
    void setWellExternalIdentifier(string externalIdentifier, int plateIndex, int wellIndex);
    void setWellID(string id, int plateIndex, int wellIndex);
    void setWellReagentRef(string reagentRef, int plateIndex, int wellIndex);
    void setWellRow(nonnegativeint row, int plateIndex, int wellIndex);
    void setWellStatus(string status, int plateIndex, int wellIndex);

    // - WellAnnotationRef property storage -

    // - WellSample property storage -
    void setWellSampleAnnotationRef(string annotationRef, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);
    void setWellSampleID(string id, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleImageRef(string imageRef, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleIndex(nonnegativeint index, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePositionX(double positionX, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePositionY(double positionY, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

    // - WellSampleAnnotationRef property storage -

    // - WellSampleRef property storage -

    // - XMLAnnotation property storage -
    void setXMLAnnotationID(string id, int xmlAnnotationIndex);
    void setXMLAnnotationNamespace(string namespace, int xmlAnnotationIndex);
    void setXMLAnnotationValue(string value, int xmlAnnotationIndex);
  };
};
