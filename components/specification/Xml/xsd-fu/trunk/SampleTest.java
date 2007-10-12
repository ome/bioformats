/*
 * org.openmicroscopy.xml2007.SampleTest
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2006 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * Written by:    Curtis Rueden <ctrueden@wisc.edu>
 *
 *-----------------------------------------------------------------------------
 */

package org.openmicroscopy.xml2007;

import java.io.*;
import java.util.Vector;

/** Tests the org.openmicroscopy.xml2007 package. */
public final class SampleTest {

  // -- Constructor --

  private SampleTest() { }

  // -- Testing methods --

  /** Tests the integrity of the given node against the Sample.ome file. */
  public static void testSample(OMENode ome) throws Exception {
    // -- Depth 1 --

    // check OME node
    int projectCount = ome.getProjectCount();
    Vector projectList = ome.getProjectList();
    checkCount("Project", projectCount, projectList, 1);
    int datasetCount = ome.getDatasetCount();
    Vector datasetList = ome.getDatasetList();
    checkCount("Dataset", datasetCount, datasetList, 1);
    int experimentCount = ome.getExperimentCount();
    Vector experimentList = ome.getExperimentList();
    checkCount("Experiment", experimentCount, experimentList, 1);
    int plateCount = ome.getPlateCount();
    Vector plateList = ome.getPlateList();
    checkCount("Plate", plateCount, plateList, 1);
    int screenCount = ome.getScreenCount();
    Vector screenList = ome.getScreenList();
    checkCount("Screen", screenCount, screenList, 1);
    int experimenterCount = ome.getExperimenterCount();
    Vector experimenterList = ome.getExperimenterList();
    checkCount("Experimenter", experimenterCount, experimenterList, 1);
    int groupCount = ome.getGroupCount();
    Vector groupList = ome.getGroupList();
    checkCount("Group", groupCount, groupList, 1);
    int instrumentCount = ome.getInstrumentCount();
    Vector instrumentList = ome.getInstrumentList();
    checkCount("Instrument", instrumentCount, instrumentList, 1);
    int imageCount = ome.getImageCount();
    Vector imageList = ome.getImageList();
    checkCount("Image", imageCount, imageList, 1);
//    SemanticTypeDefinitionsNode semanticTypeDefinitions =
//      ome.getSemanticTypeDefinitions();
//    checkNull("SemanticTypeDefinitions", semanticTypeDefinitions);
//    AnalysisModuleLibraryNode analysisModuleLibrary =
//      ome.getAnalysisModuleLibrary();
//    checkNull("AnalysisModuleLibrary", analysisModuleLibrary);
//    CustomAttributesNode customAttributes = ome.getCustomAttributes();
//    checkNull("CustomAttributes", customAttributes);

    // -- Depth 2 --

    // check OME/Project node

    // check OME/Dataset node

    // check OME/Experiment node
    ExperimentNode experiment = (ExperimentNode) experimentList.get(0);
    String experimentID = experiment.getID();
    checkValue("Experiment ID", experimentID,
      "urn:lsid:foo.bar.com:Experiment:123456");
    String experimentDescription = experiment.getDescription();
    checkValue("Experiment Description", experimentDescription,
      "This was an experiment.");
    ExperimenterNode experimentExperimenterNode = experiment.getExperimenter();
    checkNotNull("Experiment Experimenter", experimentExperimenterNode);
    int experimentMicrobeamManipulationCount =
      experiment.getMicrobeamManipulationCount();
    Vector experimentMicrobeamManipulationList =
      experiment.getMicrobeamManipulationList();
    checkCount("Experiment MicrobeamManipulation",
      experimentMicrobeamManipulationCount,
      experimentMicrobeamManipulationList, 0);
    String experimentType = experiment.getType();
    checkValue("Experiment Type", experimentType, "TimeLapse");

    // check OME/Plate node

    // check OME/Screen node

    // check OME/Experimenter node
    ExperimenterNode experimenter = (ExperimenterNode) experimenterList.get(0);
    String experimenterID = experimenter.getID();
    checkValue("Experimenter ID", experimenterID,
      "urn:lsid:foo.bar.com:Experimenter:123456");
    String experimenterFirstName = experimenter.getFirstName();
    checkValue("Experimenter FirstName", experimenterFirstName, "Nicola");
    String experimenterLastName = experimenter.getLastName();
    checkValue("Experimenter LastName", experimenterLastName, "Sacco");
    String experimenterEmail = experimenter.getEmail();
    checkValue("Experimenter Email",
      experimenterEmail, "Nicola.Sacco@justice.net");
    String experimenterInstitution = experimenter.getInstitution();
    checkNull("Experimenter Institution", experimenterInstitution);
    String experimenterOMEName = experimenter.getOMEName();
    checkValue("Experimenter OMEName", experimenterOMEName, "nico");
    int experimenterGroupCount = experimenter.getGroupCount();
    Vector experimenterGroupList = experimenter.getGroupList();
    checkCount("Experimenter Group",
      experimenterGroupCount, experimenterGroupList, 2);

    // check OME/Group node

    // check OME/Instrument node
    InstrumentNode instrument = (InstrumentNode) instrumentList.get(0);
    checkNotNull("Instrument", instrument);
    MicroscopeNode instrumentMicroscope = instrument.getMicroscope();
    checkNotNull("Instrument Microscope", instrumentMicroscope);
    int instrumentLightSourceCount = instrument.getLightSourceCount();
    Vector instrumentLightSourceList = instrument.getLightSourceList();
    checkCount("Instrument LightSource",
      instrumentLightSourceCount, instrumentLightSourceList, 2);
    int instrumentDetectorCount = instrument.getDetectorCount();
    Vector instrumentDetectorList = instrument.getDetectorList();
    checkCount("Instrument Detector",
      instrumentDetectorCount, instrumentDetectorList, 1);
    int instrumentObjectiveCount = instrument.getObjectiveCount();
    Vector instrumentObjectiveList = instrument.getObjectiveList();
    checkCount("Instrument Objective",
      instrumentObjectiveCount, instrumentObjectiveList, 1);
    int instrumentFilterSetCount = instrument.getFilterSetCount();
    Vector instrumentFilterSetList = instrument.getFilterSetList();
    checkCount("Instrument FilterSet",
      instrumentFilterSetCount, instrumentFilterSetList, 1);
    int instrumentFilterCount = instrument.getFilterCount();
    Vector instrumentFilterList = instrument.getFilterList();
    checkCount("Instrument Filter",
      instrumentFilterCount, instrumentFilterList, 2);
    int instrumentDichroicCount = instrument.getDichroicCount();
    Vector instrumentDichroicList = instrument.getDichroicList();
    checkCount("Instrument Dichroic",
      instrumentDichroicCount, instrumentDichroicList, 0);
    int instrumentOTFCount = instrument.getOTFCount();
    Vector instrumentOTFList = instrument.getOTFList();
    checkCount("Instrument OTF",
      instrumentOTFCount, instrumentOTFList, 1);

    // check OME/Image node
    ImageNode image = (ImageNode) imageList.get(0);
    checkNotNull("Image", image);
    String imageID = image.getID();
    checkValue("Image ID", imageID, "urn:lsid:foo.bar.com:Image:123456");
    String imageCreationDate = image.getCreationDate();
    checkValue("Image CreationDate", imageCreationDate, "1988-04-07T18:39:09");
    ExperimenterNode imageExperimenter = image.getExperimenter();
    checkNotNull("Image Experimenter", imageExperimenter);
    String imageDescription = image.getDescription();
    checkValue("Image Description", imageDescription, "This is an Image");
    ExperimentNode imageExperiment = image.getExperiment();
    checkNotNull("Image Experiment", imageExperiment);
    GroupNode imageGroup = image.getGroup();
    checkNotNull("Image Group", imageGroup);
    int imageDatasetCount = image.getDatasetCount();
    Vector imageDatasetList = image.getDatasetList();
    checkCount("Image Dataset", imageDatasetCount, imageDatasetList, 1);
    InstrumentNode imageInstrument = image.getInstrument();
    checkNotNull("Image Instrument", imageInstrument);
    ObjectiveSettingsNode imageObjectiveSettings = image.getObjectiveSettings();
    checkNull("Image ObjectiveSettings", imageObjectiveSettings);
    ImagingEnvironmentNode imageImagingEnvironment =
      image.getImagingEnvironment();
    checkNotNull("Image ImagingEnvironment", imageImagingEnvironment);
    ThumbnailNode imageThumbnail = image.getThumbnail();
    checkNotNull("Image Thumbnail", imageThumbnail);
    int imageLogicalChannelCount = image.getLogicalChannelCount();
    Vector imageLogicalChannelList = image.getLogicalChannelList();
    checkCount("Image LogicalChannel", imageLogicalChannelCount,
      imageLogicalChannelList, 1);
    DisplayOptionsNode imageDisplayOptions = image.getDisplayOptions();
    checkNotNull("Image DisplayOptions", imageDisplayOptions);
    StageLabelNode imageStageLabel = image.getStageLabel();
    checkNotNull("Image StageLabel", imageStageLabel);
    int imagePixelsCount = image.getPixelsCount();
    Vector imagePixelsList = image.getPixelsList();
    checkCount("Image Pixels", imagePixelsCount, imagePixelsList, 1);
    PixelsNode imageAcquiredPixels = image.getAcquiredPixels();
    checkNull("Image AcquiredPixels", imageAcquiredPixels);
    int imageRegionCount = image.getRegionCount();
    Vector imageRegionList = image.getRegionList();
    checkCount("Image Region", imageRegionCount, imageRegionList, 0);
//    CustomAttributesNode imageCustomAttributes = image.getCustomAttributes();
//    checkNull("Image CustomAttributes", imageCustomAttributes);
    int imageROICount = image.getROICount();
    Vector imageROIList = image.getROIList();
    checkCount("Image ROI", imageROICount, imageROIList, 0);
    int imageMicrobeamManipulationCount =
      image.getMicrobeamManipulationCount();
    Vector imageMicrobeamManipulationList =
      image.getMicrobeamManipulationList();
    checkCount("Image MicrobeamManipulation", imageMicrobeamManipulationCount,
      imageMicrobeamManipulationList, 0);
    String imageName = image.getName();
    checkValue("Image Name", imageName, "P1W1S1");

    // -- Depth 3 --

    // check OME/Dataset/CustomAttributes

    // check OME/Screen/Reagent

    // check OME/Screen/ScreenAcquisition

    // check OME/Instrument/Microscope

    // check OME/Instrument/LightSource-1

    // check OME/Instrument/LightSource-2

    // check OME/Instrument/Detector

    // check OME/Instrument/Objective

    // check OME/Instrument/FilterSet

    // check OME/Instrument/Filter-1
    FilterNode instrumentFilter1 = (FilterNode) instrumentFilterList.get(0);
    String instrumentFilter1ID = instrumentFilter1.getID();
    checkValue("Instrument Filter-1 ID", instrumentFilter1ID,
      "urn:lsid:foo.bar.com:Filter:123456");
    String instrumentFilter1Manufacturer = instrumentFilter1.getManufacturer();
    checkValue("Instrument Filter-1 Manufacturer",
      instrumentFilter1Manufacturer, "Omega");
    String instrumentFilter1Model = instrumentFilter1.getModel();
    checkValue("Instrument Filter-1 Model",
      instrumentFilter1Model, "SuperGFP");
    String instrumentFilter1LotNumber = instrumentFilter1.getLotNumber();
    checkNull("Instrument Filter-1 LotNumber", instrumentFilter1LotNumber);
    TransmittanceRangeNode instrumentFilter1TransmittanceRange =
      instrumentFilter1.getTransmittanceRange();
    checkNotNull("Instrument Filter-1 TransmittanceRange",
      instrumentFilter1TransmittanceRange);
    String instrumentFilter1Type = instrumentFilter1.getType();
    checkNull("Instrument Filter-1 Type", instrumentFilter1Type);
    String instrumentFilter1FilterWheel = instrumentFilter1.getFilterWheel();
    checkNull("Instrument Filter-1 FilterWheel", instrumentFilter1FilterWheel);

    // check OME/Instrument/Filter-2
    FilterNode instrumentFilter2 = (FilterNode) instrumentFilterList.get(1);
    String instrumentFilter2ID = instrumentFilter2.getID();
    checkValue("Instrument Filter-2 ID", instrumentFilter2ID,
      "urn:lsid:foo.bar.com:Filter:1234567");
    String instrumentFilter2Manufacturer = instrumentFilter2.getManufacturer();
    checkValue("Instrument Filter-2 Manufacturer",
      instrumentFilter2Manufacturer, "Omega");
    String instrumentFilter2Model = instrumentFilter2.getModel();
    checkValue("Instrument Filter-2 Model",
      instrumentFilter2Model, "SuperGFP");
    String instrumentFilter2LotNumber = instrumentFilter2.getLotNumber();
    checkNull("Instrument Filter-2 LotNumber", instrumentFilter2LotNumber);
    TransmittanceRangeNode instrumentFilter2TransmittanceRange =
      instrumentFilter2.getTransmittanceRange();
    checkNotNull("Instrument Filter-2 TransmittanceRange",
      instrumentFilter2TransmittanceRange);
    String instrumentFilter2Type = instrumentFilter2.getType();
    checkNull("Instrument Filter-2 Type", instrumentFilter2Type);
    String instrumentFilter2FilterWheel = instrumentFilter2.getFilterWheel();
    checkNull("Instrument Filter-2 FilterWheel", instrumentFilter2FilterWheel);

    // check OME/Instrument/OTF

    // check OME/Image/ImagingEnvironment

    // check OME/Image/Thumbnail

    // check OME/Image/LogicalChannel
    LogicalChannelNode imageLogicalChannel =
      (LogicalChannelNode) imageLogicalChannelList.get(0);
    checkNotNull("Image LogicalChannel", imageLogicalChannel);
    String imageLogicalChannelID = imageLogicalChannel.getID();
    checkValue("Image LogicalChannel ID", imageLogicalChannelID,
      "urn:lsid:foo.bar.com:LogicalChannel:123456");
    LightSourceNode imageLogicalChannelLightSource =
      imageLogicalChannel.getLightSource();
    checkNotNull("Image LogicalChannel LightSource",
      imageLogicalChannelLightSource);
    OTFNode imageLogicalChannelOTF = imageLogicalChannel.getOTF();
    checkNotNull("Image LogicalChannel OTF", imageLogicalChannelOTF);
    DetectorNode imageLogicalChannelDetector =
      imageLogicalChannel.getDetector();
    checkNotNull("Image LogicalChannel Detector", imageLogicalChannelDetector);
    FilterSetNode imageLogicalChannelFilterSet =
      imageLogicalChannel.getFilterSet();
    checkNotNull("Image LogicalChannel FilterSet",
      imageLogicalChannelFilterSet);
    int imageLogicalChannelChannelComponentCount =
      imageLogicalChannel.getChannelComponentCount();
    Vector imageLogicalChannelChannelComponentList =
      imageLogicalChannel.getChannelComponentList();
    checkCount("Image LogicalChannel ChannelComponent",
      imageLogicalChannelChannelComponentCount,
      imageLogicalChannelChannelComponentList, 1);
    String imageLogicalChannelName = imageLogicalChannel.getName();
    checkValue("Image LogicalChannel Name", imageLogicalChannelName, "Ch 1");
    Integer imageLogicalChannelSamplesPerPixel =
      imageLogicalChannel.getSamplesPerPixel();
    checkNull("Image LogicalChannel SamplesPerPixel",
      imageLogicalChannelSamplesPerPixel);
    FilterNode imageLogicalChannelSecondaryEmissionFilter =
      imageLogicalChannel.getSecondaryEmissionFilter();
    checkNull("Image LogicalChannel SecondaryEmissionFilter",
      imageLogicalChannelSecondaryEmissionFilter);
    FilterNode imageLogicalChannelSecondaryExcitationFilter =
      imageLogicalChannel.getSecondaryExcitationFilter();
    checkNull("Image LogicalChannel SecondaryExcitationFilter",
      imageLogicalChannelSecondaryExcitationFilter);
    String imageLogicalChannelIlluminationType =
      imageLogicalChannel.getIlluminationType();
    checkValue("Image LogicalChannel IlluminationType",
      imageLogicalChannelIlluminationType, "Epifluorescence");
    Integer imageLogicalChannelPinholeSize =
      imageLogicalChannel.getPinholeSize();
    checkNull("Image LogicalChannel PinholeSize",
      imageLogicalChannelPinholeSize);
    String imageLogicalChannelPhotometricInterpretation =
      imageLogicalChannel.getPhotometricInterpretation();
    checkNull("Image LogicalChannel PhotometricInterpretation",
      imageLogicalChannelPhotometricInterpretation);
    String imageLogicalChannelMode = imageLogicalChannel.getMode();
    checkNull("Image LogicalChannel Mode", imageLogicalChannelMode);
    String imageLogicalChannelContrastMethod =
      imageLogicalChannel.getContrastMethod();
    checkNull("Image LogicalChannel ContrastMethod",
      imageLogicalChannelContrastMethod);
    Integer imageLogicalChannelExWave = imageLogicalChannel.getExWave();
    checkValue("Image LogicalChannel ExWave",
      imageLogicalChannelExWave, new Integer(490));
    Integer imageLogicalChannelEmWave = imageLogicalChannel.getEmWave();
    checkValue("Image LogicalChannel EmWave",
      imageLogicalChannelEmWave, new Integer(528));
    String imageLogicalChannelFluor = imageLogicalChannel.getFluor();
    checkValue("Image LogicalChannel Fluor",
      imageLogicalChannelFluor, "GFP");
    Float imageLogicalChannelNdFilter = imageLogicalChannel.getNdFilter();
    checkValue("Image LogicalChannel NdFilter",
      imageLogicalChannelNdFilter, new Float(0.0f));
    Integer imageLogicalChannelPockelCellSetting =
      imageLogicalChannel.getPockelCellSetting();
    checkNull("Image LogicalChannel PockelCellSetting",
      imageLogicalChannelPockelCellSetting);

    // check OME/Image/DisplayOptions
    String imageDisplayOptionsID = imageDisplayOptions.getID();
    checkValue("Image DisplayOptions ID", imageDisplayOptionsID,
      "urn:lsid:foo.bar.com:DisplayOptions:123456");
    ChannelSpecTypeNode imageDisplayOptionsRedChannel =
      imageDisplayOptions.getRedChannel();
    checkNotNull("Image DisplayOptions RedChannel",
      imageDisplayOptionsRedChannel);
    ChannelSpecTypeNode imageDisplayOptionsGreenChannel =
      imageDisplayOptions.getGreenChannel();
    checkNotNull("Image DisplayOptions GreenChannel",
      imageDisplayOptionsGreenChannel);
    ChannelSpecTypeNode imageDisplayOptionsBlueChannel =
      imageDisplayOptions.getBlueChannel();
    checkNotNull("Image DisplayOptions BlueChannel",
      imageDisplayOptionsBlueChannel);
    GreyChannelNode imageDisplayOptionsGreyChannel =
      imageDisplayOptions.getGreyChannel();
    checkNotNull("Image DisplayOptions GreyChannel",
      imageDisplayOptionsGreyChannel);
    ProjectionNode imageDisplayOptionsProjection =
      imageDisplayOptions.getProjection();
    checkNotNull("Image DisplayOptions Projection",
      imageDisplayOptionsProjection);
    TimeNode imageDisplayOptionsTime =
      imageDisplayOptions.getTime();
    checkNotNull("Image DisplayOptions Time",
      imageDisplayOptionsTime);
    int imageDisplayOptionsROICount = imageDisplayOptions.getROICount();
    Vector imageDisplayOptionsROIList = imageDisplayOptions.getROIList();
    checkCount("Image DisplayOptions ROI", 
      imageDisplayOptionsROICount, imageDisplayOptionsROIList, 1);
    Float imageDisplayOptionsZoom = imageDisplayOptions.getZoom();
    checkValue("Image DisplayOptions Zoom",
      imageDisplayOptionsZoom, new Float(1.0f));
    String imageDisplayOptionsDisplay = imageDisplayOptions.getDisplay();
    checkValue("Image DisplayOptions Display",
      imageDisplayOptionsDisplay, "RGB");

    // check OME/Image/StageLabel
    String imageStageLabelName = imageStageLabel.getName();
    checkValue("Image StageLabel Name", imageStageLabelName, "Zulu");
    Float imageStageLabelX = imageStageLabel.getX();
    checkValue("Image StageLabel X", imageStageLabelX, new Float(123));
    Float imageStageLabelY = imageStageLabel.getY();
    checkValue("Image StageLabel Y", imageStageLabelY, new Float(456));
    Float imageStageLabelZ = imageStageLabel.getZ();
    checkValue("Image StageLabel Z", imageStageLabelZ, new Float(789));

    // check OME/Image/Pixels
    PixelsNode imagePixels = (PixelsNode) imagePixelsList.get(0);
    checkNotNull("Image Pixels", imagePixels);
    int imagePixelsTiffDataCount = imagePixels.getTiffDataCount();
    Vector imagePixelsTiffDataList = imagePixels.getTiffDataList();
    checkCount("Image Pixels TiffData", imagePixelsTiffDataCount,
      imagePixelsTiffDataList, 0);
    int imagePixelsPlaneCount = imagePixels.getPlaneCount();
    Vector imagePixelsPlaneList = imagePixels.getPlaneList();
    checkCount("Image Pixels Plane", imagePixelsPlaneCount,
      imagePixelsPlaneList, 0);
    String imagePixelsDimensionOrder = imagePixels.getDimensionOrder();
    checkValue("Image Pixels DimensionOrder",
      imagePixelsDimensionOrder, "XYZCT");
    String imagePixelsPixelType = imagePixels.getPixelType();
    checkValue("Image Pixels PixelType", imagePixelsPixelType, "int16");
    Boolean imagePixelsBigEndian = imagePixels.getBigEndian();
    checkValue("Image Pixels BigEndian", imagePixelsBigEndian, Boolean.TRUE);
    Integer imagePixelsSizeX = imagePixels.getSizeX();
    checkValue("Image Pixels SizeX", imagePixelsSizeX, new Integer(20));
    Integer imagePixelsSizeY = imagePixels.getSizeY();
    checkValue("Image Pixels SizeY", imagePixelsSizeY, new Integer(20));
    Integer imagePixelsSizeZ = imagePixels.getSizeZ();
    checkValue("Image Pixels SizeZ", imagePixelsSizeZ, new Integer(5));
    Integer imagePixelsSizeC = imagePixels.getSizeC();
    checkValue("Image Pixels SizeC", imagePixelsSizeC, new Integer(1));
    Integer imagePixelsSizeT = imagePixels.getSizeT();
    checkValue("Image Pixels SizeT", imagePixelsSizeT, new Integer(6));
    Float imagePixelsPhysicalSizeX = imagePixels.getPhysicalSizeX();
    checkValue("Image Pixels PhysicalSizeX",
      imagePixelsPhysicalSizeX, new Float(0.2f));
    Float imagePixelsPhysicalSizeY = imagePixels.getPhysicalSizeY();
    checkValue("Image Pixels PhysicalSizeY",
      imagePixelsPhysicalSizeY, new Float(0.2f));
    Float imagePixelsPhysicalSizeZ = imagePixels.getPhysicalSizeZ();
    checkValue("Image Pixels PhysicalSizeZ",
      imagePixelsPhysicalSizeZ, new Float(0.2f));
    Float imagePixelsTimeIncrement = imagePixels.getTimeIncrement();
    checkNull("Image Pixels TimeIncrement", imagePixelsTimeIncrement);
    Integer imagePixelsWaveStart = imagePixels.getWaveStart();
    checkNull("Image Pixels WaveStart", imagePixelsWaveStart);
    Integer imagePixelsWaveIncrement = imagePixels.getWaveIncrement();
    checkNull("Image Pixels WaveIncrement", imagePixelsWaveIncrement);

    // -- Depth 4 --

    // check OME/Instrument/LightSource-1/Laser

    // check OME/Instrument/LightSource-2/Arc

    // check OME/Instrument/Filter-1/TransmittanceRange
    // TODO START HERE

    // check OME/Instrument/Filter-2/TransmittanceRange
    // TODO START HERE

    // check OME/Instrument/OTF/BinaryFile

    // check OME/Image/LogicalChannel/ChannelComponent

    // check OME/Image/DisplayOptions/RedChannel
    Integer imageDisplayOptionsRedChannelChannelNumber =
      imageDisplayOptionsRedChannel.getChannelNumber();
    checkValue("Image DisplayOptions RedChannel ChannelNumber",
      imageDisplayOptionsRedChannelChannelNumber, new Integer(0));
    Float imageDisplayOptionsRedChannelBlackLevel =
      imageDisplayOptionsRedChannel.getBlackLevel();
    checkValue("Image DisplayOptions RedChannel BlackLevel",
      imageDisplayOptionsRedChannelBlackLevel, new Float(144));
    Float imageDisplayOptionsRedChannelWhiteLevel =
      imageDisplayOptionsRedChannel.getWhiteLevel();
    checkValue("Image DisplayOptions RedChannel WhiteLevel",
      imageDisplayOptionsRedChannelWhiteLevel, new Float(338));
    Float imageDisplayOptionsRedChannelGamma =
      imageDisplayOptionsRedChannel.getGamma();
    checkNull("Image DisplayOptions RedChannel Gamma",
      imageDisplayOptionsRedChannelGamma);
    Boolean imageDisplayOptionsRedChannelisOn =
      imageDisplayOptionsRedChannel.getisOn();
    checkValue("Image DisplayOptions RedChannel isOn",
      imageDisplayOptionsRedChannelisOn, Boolean.TRUE);

    // check OME/Image/DisplayOptions/GreenChannel
    Integer imageDisplayOptionsGreenChannelChannelNumber =
      imageDisplayOptionsGreenChannel.getChannelNumber();
    checkValue("Image DisplayOptions GreenChannel ChannelNumber",
      imageDisplayOptionsGreenChannelChannelNumber, new Integer(0));
    Float imageDisplayOptionsGreenChannelBlackLevel =
      imageDisplayOptionsGreenChannel.getBlackLevel();
    checkValue("Image DisplayOptions GreenChannel BlackLevel",
      imageDisplayOptionsGreenChannelBlackLevel, new Float(144));
    Float imageDisplayOptionsGreenChannelWhiteLevel =
      imageDisplayOptionsGreenChannel.getWhiteLevel();
    checkValue("Image DisplayOptions GreenChannel WhiteLevel",
      imageDisplayOptionsGreenChannelWhiteLevel, new Float(338));
    Float imageDisplayOptionsGreenChannelGamma =
      imageDisplayOptionsGreenChannel.getGamma();
    checkNull("Image DisplayOptions GreenChannel Gamma",
      imageDisplayOptionsGreenChannelGamma);
    Boolean imageDisplayOptionsGreenChannelisOn =
      imageDisplayOptionsGreenChannel.getisOn();
    checkValue("Image DisplayOptions GreenChannel isOn",
      imageDisplayOptionsGreenChannelisOn, Boolean.TRUE);

    // check OME/Image/DisplayOptions/BlueChannel
    Integer imageDisplayOptionsBlueChannelChannelNumber =
      imageDisplayOptionsBlueChannel.getChannelNumber();
    checkValue("Image DisplayOptions BlueChannel ChannelNumber",
      imageDisplayOptionsBlueChannelChannelNumber, new Integer(0));
    Float imageDisplayOptionsBlueChannelBlackLevel =
      imageDisplayOptionsBlueChannel.getBlackLevel();
    checkValue("Image DisplayOptions BlueChannel BlackLevel",
      imageDisplayOptionsBlueChannelBlackLevel, new Float(144));
    Float imageDisplayOptionsBlueChannelWhiteLevel =
      imageDisplayOptionsBlueChannel.getWhiteLevel();
    checkValue("Image DisplayOptions BlueChannel WhiteLevel",
      imageDisplayOptionsBlueChannelWhiteLevel, new Float(338));
    Float imageDisplayOptionsBlueChannelGamma =
      imageDisplayOptionsBlueChannel.getGamma();
    checkNull("Image DisplayOptions BlueChannel Gamma",
      imageDisplayOptionsBlueChannelGamma);
    Boolean imageDisplayOptionsBlueChannelisOn =
      imageDisplayOptionsBlueChannel.getisOn();
    checkValue("Image DisplayOptions BlueChannel isOn",
      imageDisplayOptionsBlueChannelisOn, Boolean.TRUE);

    // check OME/Image/DisplayOptions/GreyChannel
    String imageDisplayOptionsGreyChannelColorMap =
      imageDisplayOptionsGreyChannel.getColorMap();
    checkNull("Image DisplayOptions GreyChannel ColorMap",
      imageDisplayOptionsGreyChannelColorMap);
    Integer imageDisplayOptionsGreyChannelChannelNumber =
      imageDisplayOptionsGreyChannel.getChannelNumber();
    checkValue("Image DisplayOptions GreyChannel ChannelNumber",
      imageDisplayOptionsGreyChannelChannelNumber, new Integer(0));
    Float imageDisplayOptionsGreyChannelBlackLevel =
      imageDisplayOptionsGreyChannel.getBlackLevel();
    checkValue("Image DisplayOptions GreyChannel BlackLevel",
      imageDisplayOptionsGreyChannelBlackLevel, new Float(144));
    Float imageDisplayOptionsGreyChannelWhiteLevel =
      imageDisplayOptionsGreyChannel.getWhiteLevel();
    checkValue("Image DisplayOptions GreyChannel WhiteLevel",
      imageDisplayOptionsGreyChannelWhiteLevel, new Float(338));
    Float imageDisplayOptionsGreyChannelGamma =
      imageDisplayOptionsGreyChannel.getGamma();
    checkNull("Image DisplayOptions GreyChannel Gamma",
      imageDisplayOptionsGreyChannelGamma);
    Boolean imageDisplayOptionsGreyChannelisOn =
      imageDisplayOptionsGreyChannel.getisOn();
    checkNull("Image DisplayOptions GreyChannel isOn",
      imageDisplayOptionsGreyChannelisOn);

    // check OME/Image/DisplayOptions/Projection
    Integer imageDisplayOptionsProjectionZStart =
      imageDisplayOptionsProjection.getZStart();
    checkValue("Image DisplayOptions Projection ZStart",
      imageDisplayOptionsProjectionZStart, new Integer(3));
    Integer imageDisplayOptionsProjectionZStop =
      imageDisplayOptionsProjection.getZStop();
    checkValue("Image DisplayOptions Projection ZStop",
      imageDisplayOptionsProjectionZStop, new Integer(3));

    // check OME/Image/DisplayOptions/Time
    Integer imageDisplayOptionsTimeTStart = imageDisplayOptionsTime.getTStart();
    checkValue("Image DisplayOptions Time TStart",
      imageDisplayOptionsTimeTStart, new Integer(3));
    Integer imageDisplayOptionsTimeTStop = imageDisplayOptionsTime.getTStop();
    checkValue("Image DisplayOptions Time TStop",
      imageDisplayOptionsTimeTStop, new Integer(3));

    // check OME/Image/DisplayOptions/ROI

    // -- Depth 5 --

    // check OME/Instrument/LightSource-1/Laser/Pump

    // check OME/Instrument/OTF/BinaryFile/External
  }

  /** Builds a node from scratch (to match the Sample.ome file). */
  public static OMENode createNode() throws Exception {
    OMENode ome = null;
/*
    OMENode ome = new OMENode();

    // -- Depth 1 --

    // create OME/Project
    ProjectNode project = new ProjectNode(ome, "Stress Response Pathway",
      null, null, null);
    project.setID("urn:lsid:foo.bar.com:Project:123456");

    // create OME/Dataset
    DatasetNode dataset = new DatasetNode(ome, "Controls",
      null, Boolean.FALSE, null, null);
    dataset.setID("urn:lsid:foo.bar.com:Dataset:123456");

    // create OME/Experiment

    // create OME/Plate

    // create OME/Screen

    // create OME/Experimenter

    // create OME/Group

    // create OME/Instrument

    // create OME/Image
    ImageNode image = new ImageNode(ome,
      "P1W1S1", "1988-04-07T18:39:09", "This is an Image");
    image.setID("urn:lsid:foo.bar.com:Image:123456");

    // -- Depth 2 --

    // create OME/Dataset/ProjectRef
    dataset.addToProject(project);

    // create OME/Image/DatasetRef
    image.addToDataset(dataset);

    // create OME/Image/CA
    CustomAttributesNode imageCA = new CustomAttributesNode(image);

    // create OME/CA/Experimenter
    ExperimenterNode experimenter = new ExperimenterNode(ca,
      "Nicola", "Sacco", "Nicola.Sacco@justice.net", null, null, null);
    experimenter.setID("urn:lsid:foo.bar.com:Experimenter:123456");
    project.setOwner(experimenter);
    dataset.setOwner(experimenter);
    image.setOwner(experimenter);

    // create first OME/CA/ExperimenterGroup
    ExperimenterGroupNode experimenterGroup1 = new ExperimenterGroupNode(ca,
      experimenter, null);

    // create second OME/CA/ExperimenterGroup
    GroupNode dummyGroup = new GroupNode(ca, false);
    dummyGroup.setID("urn:lsid:foo.bar.com:Group:123789");
    ExperimenterGroupNode experimenterGroup2 = new ExperimenterGroupNode(ca,
      experimenter, dummyGroup);

    // create OME/CA/Group
    GroupNode group = new GroupNode(ca, "IICBU", experimenter, experimenter);
    group.setID("urn:lsid:foo.bar.com:Group:123456");
    project.setGroup(group);
    dataset.setGroup(group);
    image.setGroup(group);
    experimenter.setGroup(group);
    experimenterGroup1.setGroup(group);

    // create OME/CA/Experiment
    ExperimentNode experiment = new ExperimentNode(ca,
      "Time-lapse", "This was an experiment.", experimenter);
    experiment.setID("urn:lsid:foo.bar.com:Experiment:123456");

    // create OME/CA/Instrument
    InstrumentNode instrument = new InstrumentNode(ca,
      "Zeiss", "foo", "bar", "Upright");
    instrument.setID("urn:lsid:foo.bar.com:Instrument:123456");

    // create first OME/CA/LightSource
    LightSourceNode lightSource1 = new LightSourceNode(ca,
      "Olympus", "WMD Laser", "123skdjhf1234", instrument);
    lightSource1.setID("urn:lsid:foo.bar.com:LightSource:123456");

    // create OME/CA/Laser
    LightSourceNode dummyLightSource = new LightSourceNode(ca, false);
    dummyLightSource.setID("urn:lsid:foo.bar.com:LightSource:123789");
    LaserNode laser = new LaserNode(ca, "Semiconductor", "GaAs",
      null, null, null, null, null, lightSource1, dummyLightSource);

    // create second OME/CA/LightSource
    LightSourceNode lightSource2 = new LightSourceNode(ca,
      "Olympus", "Realy Bright Lite", "123skdjhf1456", instrument);
    lightSource2.setID("urn:lsid:foo.bar.com:LightSource:123123");

    // create OME/CA/Arc
    ArcNode arc = new ArcNode(ca, "Hg", null, lightSource2);

    // create OME/CA/Detector
    DetectorNode detector = new DetectorNode(ca, "Kodak", "Instamatic",
      "fnuiprf89uh123498", "CCD", null, null, null, instrument);
    detector.setID("urn:lsid:foo.bar.com:Detector:123456");

    // create OME/CA/Objective
    ObjectiveNode objective = new ObjectiveNode(ca, "Olympus", "SPlanL",
      "456anxcoas123", new Float(2.4f), new Float(40), instrument);
    objective.setID("urn:lsid:foo.bar.com:Objective:123456");

    // create OME/CA/Filter
    FilterNode filter = new FilterNode(ca, instrument);
    filter.setID("urn:lsid:foo.bar.com:Filter:123456");

    // create OME/CA/FilterSet
    FilterSetNode filterSet = new FilterSetNode(ca,
      "Omega", "SuperGFP", "123LJKHG123", filter);

    // create OME/CA/OTF
    OTFNode otf = new OTFNode(ca, objective, filter, new Integer(512),
      new Integer(512), "int8", null, null, Boolean.TRUE, instrument);
    otf.setID("urn:lsid:foo.bar.com:OTF:123456");

    // create OME/CA/Plate
    PlateNode plate = new PlateNode(ca, "SRP001", "PID.SRP001", null);
    plate.setID("urn:lsid:foo.bar.com:Plate:123456");

    // create first OME/CA/PlateScreen
    PlateScreenNode plateScreen1 = new PlateScreenNode(ca, plate, null);

    // create second OME/CA/PlateScreen
    ScreenNode dummyScreen = new ScreenNode(ca, false);
    dummyScreen.setID("urn:lsid:foo.bar.com:Screen:123789");
    PlateScreenNode plateScreen2 = new PlateScreenNode(ca, plate, dummyScreen);

    // create OME/CA/Screen
    ScreenNode screen = new ScreenNode(ca,
      "Stress Response Pathway Controls", null, "SID.SRPC001");
    screen.setID("urn:lsid:foo.bar.com:Screen:123456");
    plateScreen1.setScreen(screen);

    // -- Depth 3 --

    // create OME/Image/CA/Dimensions
    DimensionsNode dimensions = new DimensionsNode(imageCA,
      new Float(0.2f), new Float(0.2f), new Float(0.2f), null, null);

    // create OME/Image/CA/ImageExperiment
    ImageExperimentNode imageExperiment = new ImageExperimentNode(imageCA,
      experiment);

    // create OME/Image/CA/ImageInstrument
    ImageInstrumentNode imageInstrument = new ImageInstrumentNode(imageCA,
      instrument, objective);

    // create OME/Image/CA/ImagingEnvironment
    ImagingEnvironmentNode imagingEnvironment =
      new ImagingEnvironmentNode(imageCA, new Float(.1f),
      new Float(.1f), new Float(.2f), new Float(.3f));

    // create OME/Image/CA/Thumbnail
    ThumbnailNode thumbnail = new ThumbnailNode(imageCA, "image/jpeg", null,
      "http://ome.nia.gov/GetThumbnail?ID=urn:lsid:foo.bar.com:Image:123456");

    // create OME/Image/CA/LogicalChannel
    LogicalChannelNode logicalChannel = new LogicalChannelNode(imageCA,
      "Ch 1", null, filter, lightSource2, null, null, otf, detector, null,
      null, "Epifluorescence", null, null, null, null, lightSource1, null,
      "Photobleaching", null, new Integer(490), new Integer(528), "GFP",
      new Float(0));
    logicalChannel.setID("urn:lsid:foo.bar.com:LogicalChannel:123456");

    // create OME/Image/CA/PixelChannelComponent
    PixelChannelComponentNode pixelChannelComponent =
      new PixelChannelComponentNode(imageCA, null,
      new Integer(0), "foo", logicalChannel);

    // create OME/Image/CA/DisplayOptions
    DisplayOptionsNode displayOptions = new DisplayOptionsNode(imageCA,
      null, new Float(1), null, Boolean.TRUE, null, Boolean.TRUE, null,
      Boolean.TRUE, Boolean.TRUE, null, null, new Integer(3), new Integer(3),
      new Integer(3), new Integer(3));
    displayOptions.setID("urn:lsid:foo.bar.com:DisplayOptions:123456");

    // create first OME/Image/CA/DisplayChannel
    DisplayChannelNode displayChannelRed = new DisplayChannelNode(imageCA,
      new Integer(0), new Double(144), new Double(338), null);
    displayOptions.setRedChannel(displayChannelRed);

    // create second OME/Image/CA/DisplayChannel
    DisplayChannelNode displayChannelGreen = new DisplayChannelNode(imageCA,
      new Integer(0), new Double(144), new Double(338), null);
    displayOptions.setGreenChannel(displayChannelGreen);

    // create third OME/Image/CA/DisplayChannel
    DisplayChannelNode displayChannelBlue = new DisplayChannelNode(imageCA,
      new Integer(0), new Double(144), new Double(338), null);
    displayOptions.setBlueChannel(displayChannelBlue);

    // create fourth OME/Image/CA/DisplayChannel
    DisplayChannelNode displayChannelGrey = new DisplayChannelNode(imageCA,
      new Integer(0), new Double(144), new Double(338), null);
    displayOptions.setGreyChannel(displayChannelGrey);

    // create OME/Image/CA/DisplayROI
    DisplayROINode displayROI = new DisplayROINode(imageCA,
      new Integer(0), new Integer(0), new Integer(0), new Integer(512),
      new Integer(512), new Integer(0), new Integer(0), new Integer(0),
      displayOptions);

    // create OME/Image/CA/StageLabel
    StageLabelNode stageLabel = new StageLabelNode(imageCA,
      "Zulu", new Float(123), new Float(456), new Float(789));

    // create OME/Image/CA/ImagePlate
    ImagePlateNode imagePlate = new ImagePlateNode(imageCA,
      plate, new Integer(1), "A03");

    // create OME/Image/CA/Pixels
    PixelsNode pixels = new PixelsNode(imageCA, new Integer(20),
      new Integer(20), new Integer(5), new Integer(1), new Integer(6),
      "int16", null, null, null);
    pixels.setID("urn:lsid:foo.bar.com:Pixels:123456");
    pixels.setBigEndian(Boolean.TRUE);
    pixels.setDimensionOrder("XYZCT");
    pixelChannelComponent.setPixels(pixels);
*/

    return ome;
  }

  // -- Helper methods --

  private static void checkCount(String field,
    int count, Vector list, int expected)
  {
    if (count != expected || list.size() != expected) {
      // decapitalize field name
      char[] c = field.toCharArray();
      for (int i=0; i<c.length; i++) {
        if (c[i] < 'A' || c[i] > 'Z') break;
        c[i] += 'a' - 'A';
      }
      // remove spaces
      String var = new String(c).replaceAll("[- ]", "");
      System.out.println("Error: Incorrect " + field + " count" +
        " (" + var + "Count=" + count +
        ", " + var + "List.size()=" + list.size() + ")");
    }
  }

  private static void checkNull(String field, Object value) {
    if (value != null) {
      System.out.println("Error: " + field +
        " is not null as expected (" + value + ")");
    }
  }

  private static void checkNotNull(String field, Object value) {
    if (value == null) {
      System.out.println("Error: " + field + " should not be null");
    }
  }

  private static void checkValue(String field, Object value, Object expected) {
    if (value == null && expected == null) return;
    if (value == null || !value.equals(expected)) {
      System.out.println("Error: Incorrect " + field + " (" + value + ")");
    }
  }

  // --  Main method --

  /**
   * Tests the org.openmicroscopy.xml2007 package.
   * <ul>
   * <li>Specify path to sample-2007.ome to check it for errors.</li>
   * <li>Specify -build flag to duplicate the structure in Sample.ome from
   *   scratch, then check it for errors.</li>
   * </ul>
   */
  public static void main(String[] args) throws Exception {
    String id = null;
    boolean build = false;
    for (int i=0; i<args.length; i++) {
      if (args[i] == null) continue;
      if (args[i].equalsIgnoreCase("-build")) build = true;
      else id = args[i];
    }
    if (id == null && !build) {
      System.out.println("Usages:");
      System.out.println("java org.openmicroscopy.xml2007.SampleTest " +
        "/path/to/sample-2007.ome");
      System.out.println("java org.openmicroscopy.xml2007.SampleTest -build");
      System.exit(1);
    }

    System.out.println("Creating OME node...");
    OMENode ome = null;
    if (build) ome = createNode();
    else ome = OMEXMLFactory.newOMEDocument(new File(id));
    System.out.println();

    // perform some tests on Sample.ome structure
    System.out.println("Performing API tests...");
    testSample(ome);
    System.out.println();

    System.out.println("Writing OME-XML to String...");
    // CTR TODO improve this...
    DOMUtil.writeXML(System.out, ome.getDOMElement().getOwnerDocument());
  }

}
