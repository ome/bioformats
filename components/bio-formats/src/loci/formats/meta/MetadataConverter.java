//
// MetadataConverter.java
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
 * Created by melissa via MetadataAutogen on Oct 5, 2009 9:38:21 AM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A utility class containing a method for piping a source
 * {@link MetadataRetrieve} object into a destination {@link MetadataStore}.
 *
 * <p>This technique allows conversion between two different storage media.
 * For example, it can be used to convert an <code>OMEROMetadataStore</code>
 * (OMERO's metadata store implementation) into an
 * {@link loci.formats.ome.OMEXMLMetadata}, thus generating OME-XML from
 * information in an OMERO database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataConverter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataConverter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class MetadataConverter {

  // -- Constructor --

  private MetadataConverter() { }

  // -- MetadataConverter API methods --

  /**
   * Copies information from a metadata retrieval object
   * (source) into a metadata store (destination).
   */
  public static void convertMetadata(MetadataRetrieve src, MetadataStore dest) {
    int datasetCount = src.getDatasetCount();
    for (int datasetIndex=0; datasetIndex<datasetCount; datasetIndex++) {
    try {
      String datasetIDValue = src.getDatasetID(datasetIndex);
      if (datasetIDValue != null) dest.setDatasetID(datasetIDValue, datasetIndex);
    } catch (NullPointerException e) { }
    try {
      for (int annotationRefIndex=0; annotationRefIndex<src.getDatasetAnnotationRefCount(datasetIndex); annotationRefIndex++)
      {
        String datasetAnnotationRefValue = src.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
        if (datasetAnnotationRefValue != null) dest.setDatasetAnnotationRef(datasetAnnotationRefValue, datasetIndex, annotationRefIndex);
      } 
    } catch (NullPointerException e) { }
    try {
      String datasetDescriptionValue = src.getDatasetDescription(datasetIndex);
      if (datasetDescriptionValue != null) dest.setDatasetDescription(datasetDescriptionValue, datasetIndex);
      String datasetExperimenterRefValue = src.getDatasetExperimenterRef(datasetIndex);
      if (datasetExperimenterRefValue != null) dest.setDatasetExperimenterRef(datasetExperimenterRefValue, datasetIndex);
      String datasetGroupRefValue = src.getDatasetGroupRef(datasetIndex);
      if (datasetGroupRefValue != null) dest.setDatasetGroupRef(datasetGroupRefValue, datasetIndex);
    } catch (NullPointerException e) { }
    try {
      String datasetNameValue = src.getDatasetName(datasetIndex);
      if (datasetNameValue != null) dest.setDatasetName(datasetNameValue, datasetIndex);
      int projectRefCount = src.getProjectRefCount(datasetIndex);
      for (int projectRefIndex=0; projectRefIndex<projectRefCount; projectRefIndex++) {
        String projectRefIDValue = src.getProjectRefID(datasetIndex, projectRefIndex);
        if (projectRefIDValue != null) dest.setProjectRefID(projectRefIDValue, datasetIndex, projectRefIndex);
      }
    }
    int experimentCount = src.getExperimentCount();
    for (int experimentIndex=0; experimentIndex<experimentCount; experimentIndex++) {
      String experimentDescriptionValue = src.getExperimentDescription(experimentIndex);
      if (experimentDescriptionValue != null) dest.setExperimentDescription(experimentDescriptionValue, experimentIndex);
      String experimentExperimenterRefValue = src.getExperimentExperimenterRef(experimentIndex);
      if (experimentExperimenterRefValue != null) dest.setExperimentExperimenterRef(experimentExperimenterRefValue, experimentIndex);
      String experimentIDValue = src.getExperimentID(experimentIndex);
      if (experimentIDValue != null) dest.setExperimentID(experimentIDValue, experimentIndex);
      String experimentTypeValue = src.getExperimentType(experimentIndex);
      if (experimentTypeValue != null) dest.setExperimentType(experimentTypeValue, experimentIndex);
      int microbeamManipulationRefCount = src.getMicrobeamManipulationRefCount(experimentIndex);
      for (int microbeamManipulationRefIndex=0; microbeamManipulationRefIndex<microbeamManipulationRefCount; microbeamManipulationRefIndex++) {
        String microbeamManipulationRefIDValue = src.getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex);
        if (microbeamManipulationRefIDValue != null) dest.setMicrobeamManipulationRefID(microbeamManipulationRefIDValue, experimentIndex, microbeamManipulationRefIndex);
      }
    }
    int experimenterCount = src.getExperimenterCount();
    for (int experimenterIndex=0; experimenterIndex<experimenterCount; experimenterIndex++) {
      String experimenterEmailValue = src.getExperimenterEmail(experimenterIndex);
      if (experimenterEmailValue != null) dest.setExperimenterEmail(experimenterEmailValue, experimenterIndex);
      String experimenterFirstNameValue = src.getExperimenterFirstName(experimenterIndex);
      if (experimenterFirstNameValue != null) dest.setExperimenterFirstName(experimenterFirstNameValue, experimenterIndex);
      String experimenterIDValue = src.getExperimenterID(experimenterIndex);
      if (experimenterIDValue != null) dest.setExperimenterID(experimenterIDValue, experimenterIndex);
      String experimenterInstitutionValue = src.getExperimenterInstitution(experimenterIndex);
      if (experimenterInstitutionValue != null) dest.setExperimenterInstitution(experimenterInstitutionValue, experimenterIndex);
      String experimenterLastNameValue = src.getExperimenterLastName(experimenterIndex);
      if (experimenterLastNameValue != null) dest.setExperimenterLastName(experimenterLastNameValue, experimenterIndex);
      String experimenterOMENameValue = src.getExperimenterOMEName(experimenterIndex);
      if (experimenterOMENameValue != null) dest.setExperimenterOMEName(experimenterOMENameValue, experimenterIndex);
      int groupRefCount = src.getGroupRefCount(experimenterIndex);
      for (int groupRefIndex=0; groupRefIndex<groupRefCount; groupRefIndex++) {
        String experimenterMembershipGroupValue = src.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex);
        if (experimenterMembershipGroupValue != null) dest.setExperimenterMembershipGroup(experimenterMembershipGroupValue, experimenterIndex, groupRefIndex);
      }
    }
    int groupCount = src.getGroupCount();
    for (int groupIndex=0; groupIndex<groupCount; groupIndex++) {
      String groupIDValue = src.getGroupID(groupIndex);
      if (groupIDValue != null) dest.setGroupID(groupIDValue, groupIndex);
      String groupNameValue = src.getGroupName(groupIndex);
      if (groupNameValue != null) dest.setGroupName(groupNameValue, groupIndex);
      String contactExperimenterValue = src.getContactExperimenter(groupIndex);
      if (contactExperimenterValue != null) dest.setContactExperimenter(contactExperimenterValue, groupIndex);
    }
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
      String imageAcquiredPixelsValue = src.getImageAcquiredPixels(imageIndex);
      if (imageAcquiredPixelsValue != null) dest.setImageAcquiredPixels(imageAcquiredPixelsValue, imageIndex);
      String imageCreationDateValue = src.getImageCreationDate(imageIndex);
      if (imageCreationDateValue != null) dest.setImageCreationDate(imageCreationDateValue, imageIndex);
      String imageDefaultPixelsValue = src.getImageDefaultPixels(imageIndex);
      if (imageDefaultPixelsValue != null) dest.setImageDefaultPixels(imageDefaultPixelsValue, imageIndex);
      String imageDescriptionValue = src.getImageDescription(imageIndex);
      if (imageDescriptionValue != null) dest.setImageDescription(imageDescriptionValue, imageIndex);
      String imageExperimentRefValue = src.getImageExperimentRef(imageIndex);
      if (imageExperimentRefValue != null) dest.setImageExperimentRef(imageExperimentRefValue, imageIndex);
      String imageExperimenterRefValue = src.getImageExperimenterRef(imageIndex);
      if (imageExperimenterRefValue != null) dest.setImageExperimenterRef(imageExperimenterRefValue, imageIndex);
      String imageGroupRefValue = src.getImageGroupRef(imageIndex);
      if (imageGroupRefValue != null) dest.setImageGroupRef(imageGroupRefValue, imageIndex);
      String imageIDValue = src.getImageID(imageIndex);
      if (imageIDValue != null) dest.setImageID(imageIDValue, imageIndex);
      String imageInstrumentRefValue = src.getImageInstrumentRef(imageIndex);
      if (imageInstrumentRefValue != null) dest.setImageInstrumentRef(imageInstrumentRefValue, imageIndex);
      String imageNameValue = src.getImageName(imageIndex);
      if (imageNameValue != null) dest.setImageName(imageNameValue, imageIndex);
      int datasetRefCount = src.getDatasetRefCount(imageIndex);
      for (int datasetRefIndex=0; datasetRefIndex<datasetRefCount; datasetRefIndex++) {
        String datasetRefIDValue = src.getDatasetRefID(imageIndex, datasetRefIndex);
        if (datasetRefIDValue != null) dest.setDatasetRefID(datasetRefIDValue, imageIndex, datasetRefIndex);
      }
        String displayOptionsDisplayValue = src.getDisplayOptionsDisplay(imageIndex);
        if (displayOptionsDisplayValue != null) dest.setDisplayOptionsDisplay(displayOptionsDisplayValue, imageIndex);
        String displayOptionsIDValue = src.getDisplayOptionsID(imageIndex);
        if (displayOptionsIDValue != null) dest.setDisplayOptionsID(displayOptionsIDValue, imageIndex);
        Float displayOptionsZoomValue = src.getDisplayOptionsZoom(imageIndex);
        if (displayOptionsZoomValue != null) dest.setDisplayOptionsZoom(displayOptionsZoomValue, imageIndex);
        Float imagingEnvironmentAirPressureValue = src.getImagingEnvironmentAirPressure(imageIndex);
        if (imagingEnvironmentAirPressureValue != null) dest.setImagingEnvironmentAirPressure(imagingEnvironmentAirPressureValue, imageIndex);
        Float imagingEnvironmentCO2PercentValue = src.getImagingEnvironmentCO2Percent(imageIndex);
        if (imagingEnvironmentCO2PercentValue != null) dest.setImagingEnvironmentCO2Percent(imagingEnvironmentCO2PercentValue, imageIndex);
        Float imagingEnvironmentHumidityValue = src.getImagingEnvironmentHumidity(imageIndex);
        if (imagingEnvironmentHumidityValue != null) dest.setImagingEnvironmentHumidity(imagingEnvironmentHumidityValue, imageIndex);
        Float imagingEnvironmentTemperatureValue = src.getImagingEnvironmentTemperature(imageIndex);
        if (imagingEnvironmentTemperatureValue != null) dest.setImagingEnvironmentTemperature(imagingEnvironmentTemperatureValue, imageIndex);
      int logicalChannelCount = src.getLogicalChannelCount(imageIndex);
      for (int logicalChannelIndex=0; logicalChannelIndex<logicalChannelCount; logicalChannelIndex++) {
        String logicalChannelContrastMethodValue = src.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex);
        if (logicalChannelContrastMethodValue != null) dest.setLogicalChannelContrastMethod(logicalChannelContrastMethodValue, imageIndex, logicalChannelIndex);
        String logicalChannelDetectorValue = src.getLogicalChannelDetector(imageIndex, logicalChannelIndex);
        if (logicalChannelDetectorValue != null) dest.setLogicalChannelDetector(logicalChannelDetectorValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelEmWaveValue = src.getLogicalChannelEmWave(imageIndex, logicalChannelIndex);
        if (logicalChannelEmWaveValue != null) dest.setLogicalChannelEmWave(logicalChannelEmWaveValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelExWaveValue = src.getLogicalChannelExWave(imageIndex, logicalChannelIndex);
        if (logicalChannelExWaveValue != null) dest.setLogicalChannelExWave(logicalChannelExWaveValue, imageIndex, logicalChannelIndex);
        String logicalChannelFilterSetValue = src.getLogicalChannelFilterSet(imageIndex, logicalChannelIndex);
        if (logicalChannelFilterSetValue != null) dest.setLogicalChannelFilterSet(logicalChannelFilterSetValue, imageIndex, logicalChannelIndex);
        String logicalChannelFluorValue = src.getLogicalChannelFluor(imageIndex, logicalChannelIndex);
        if (logicalChannelFluorValue != null) dest.setLogicalChannelFluor(logicalChannelFluorValue, imageIndex, logicalChannelIndex);
        String logicalChannelIDValue = src.getLogicalChannelID(imageIndex, logicalChannelIndex);
        if (logicalChannelIDValue != null) dest.setLogicalChannelID(logicalChannelIDValue, imageIndex, logicalChannelIndex);
        String logicalChannelIlluminationTypeValue = src.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex);
        if (logicalChannelIlluminationTypeValue != null) dest.setLogicalChannelIlluminationType(logicalChannelIlluminationTypeValue, imageIndex, logicalChannelIndex);
        String logicalChannelLightSourceValue = src.getLogicalChannelLightSource(imageIndex, logicalChannelIndex);
        if (logicalChannelLightSourceValue != null) dest.setLogicalChannelLightSource(logicalChannelLightSourceValue, imageIndex, logicalChannelIndex);
        String logicalChannelModeValue = src.getLogicalChannelMode(imageIndex, logicalChannelIndex);
        if (logicalChannelModeValue != null) dest.setLogicalChannelMode(logicalChannelModeValue, imageIndex, logicalChannelIndex);
        String logicalChannelNameValue = src.getLogicalChannelName(imageIndex, logicalChannelIndex);
        if (logicalChannelNameValue != null) dest.setLogicalChannelName(logicalChannelNameValue, imageIndex, logicalChannelIndex);
        Float logicalChannelNdFilterValue = src.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex);
        if (logicalChannelNdFilterValue != null) dest.setLogicalChannelNdFilter(logicalChannelNdFilterValue, imageIndex, logicalChannelIndex);
        String logicalChannelOTFValue = src.getLogicalChannelOTF(imageIndex, logicalChannelIndex);
        if (logicalChannelOTFValue != null) dest.setLogicalChannelOTF(logicalChannelOTFValue, imageIndex, logicalChannelIndex);
        String logicalChannelPhotometricInterpretationValue = src.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex);
        if (logicalChannelPhotometricInterpretationValue != null) dest.setLogicalChannelPhotometricInterpretation(logicalChannelPhotometricInterpretationValue, imageIndex, logicalChannelIndex);
        Float logicalChannelPinholeSizeValue = src.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
        if (logicalChannelPinholeSizeValue != null) dest.setLogicalChannelPinholeSize(logicalChannelPinholeSizeValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelPockelCellSettingValue = src.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex);
        if (logicalChannelPockelCellSettingValue != null) dest.setLogicalChannelPockelCellSetting(logicalChannelPockelCellSettingValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelSamplesPerPixelValue = src.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex);
        if (logicalChannelSamplesPerPixelValue != null) dest.setLogicalChannelSamplesPerPixel(logicalChannelSamplesPerPixelValue, imageIndex, logicalChannelIndex);
        String logicalChannelSecondaryEmissionFilterValue = src.getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex);
        if (logicalChannelSecondaryEmissionFilterValue != null) dest.setLogicalChannelSecondaryEmissionFilter(logicalChannelSecondaryEmissionFilterValue, imageIndex, logicalChannelIndex);
        String logicalChannelSecondaryExcitationFilterValue = src.getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex);
        if (logicalChannelSecondaryExcitationFilterValue != null) dest.setLogicalChannelSecondaryExcitationFilter(logicalChannelSecondaryExcitationFilterValue, imageIndex, logicalChannelIndex);
        int channelComponentCount = src.getChannelComponentCount(imageIndex, logicalChannelIndex);
        for (int channelComponentIndex=0; channelComponentIndex<channelComponentCount; channelComponentIndex++) {
          String channelComponentColorDomainValue = src.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex);
          if (channelComponentColorDomainValue != null) dest.setChannelComponentColorDomain(channelComponentColorDomainValue, imageIndex, logicalChannelIndex, channelComponentIndex);
          Integer channelComponentIndexValue = src.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex);
          if (channelComponentIndexValue != null) dest.setChannelComponentIndex(channelComponentIndexValue, imageIndex, logicalChannelIndex, channelComponentIndex);
          String channelComponentPixelsValue = src.getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex);
          if (channelComponentPixelsValue != null) dest.setChannelComponentPixels(channelComponentPixelsValue, imageIndex, logicalChannelIndex, channelComponentIndex);
        }
          String detectorSettingsBinningValue = src.getDetectorSettingsBinning(imageIndex, logicalChannelIndex);
          if (detectorSettingsBinningValue != null) dest.setDetectorSettingsBinning(detectorSettingsBinningValue, imageIndex, logicalChannelIndex);
          String detectorSettingsDetectorValue = src.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
          if (detectorSettingsDetectorValue != null) dest.setDetectorSettingsDetector(detectorSettingsDetectorValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsGainValue = src.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
          if (detectorSettingsGainValue != null) dest.setDetectorSettingsGain(detectorSettingsGainValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsOffsetValue = src.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
          if (detectorSettingsOffsetValue != null) dest.setDetectorSettingsOffset(detectorSettingsOffsetValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsReadOutRateValue = src.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex);
          if (detectorSettingsReadOutRateValue != null) dest.setDetectorSettingsReadOutRate(detectorSettingsReadOutRateValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsVoltageValue = src.getDetectorSettingsVoltage(imageIndex, logicalChannelIndex);
          if (detectorSettingsVoltageValue != null) dest.setDetectorSettingsVoltage(detectorSettingsVoltageValue, imageIndex, logicalChannelIndex);
          Float lightSourceSettingsAttenuationValue = src.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsAttenuationValue != null) dest.setLightSourceSettingsAttenuation(lightSourceSettingsAttenuationValue, imageIndex, logicalChannelIndex);
          String lightSourceSettingsLightSourceValue = src.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsLightSourceValue != null) dest.setLightSourceSettingsLightSource(lightSourceSettingsLightSourceValue, imageIndex, logicalChannelIndex);
          Integer lightSourceSettingsWavelengthValue = src.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsWavelengthValue != null) dest.setLightSourceSettingsWavelength(lightSourceSettingsWavelengthValue, imageIndex, logicalChannelIndex);
      }
      int microbeamManipulationCount = src.getMicrobeamManipulationCount(imageIndex);
      for (int microbeamManipulationIndex=0; microbeamManipulationIndex<microbeamManipulationCount; microbeamManipulationIndex++) {
        String microbeamManipulationExperimenterRefValue = src.getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex);
        if (microbeamManipulationExperimenterRefValue != null) dest.setMicrobeamManipulationExperimenterRef(microbeamManipulationExperimenterRefValue, imageIndex, microbeamManipulationIndex);
        String microbeamManipulationIDValue = src.getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex);
        if (microbeamManipulationIDValue != null) dest.setMicrobeamManipulationID(microbeamManipulationIDValue, imageIndex, microbeamManipulationIndex);
        String microbeamManipulationTypeValue = src.getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex);
        if (microbeamManipulationTypeValue != null) dest.setMicrobeamManipulationType(microbeamManipulationTypeValue, imageIndex, microbeamManipulationIndex);
        int lightSourceRefCount = src.getLightSourceRefCount(imageIndex, microbeamManipulationIndex);
        for (int lightSourceRefIndex=0; lightSourceRefIndex<lightSourceRefCount; lightSourceRefIndex++) {
          Float lightSourceRefAttenuationValue = src.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
          if (lightSourceRefAttenuationValue != null) dest.setLightSourceRefAttenuation(lightSourceRefAttenuationValue, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
          String lightSourceRefLightSourceValue = src.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
          if (lightSourceRefLightSourceValue != null) dest.setLightSourceRefLightSource(lightSourceRefLightSourceValue, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
          Integer lightSourceRefWavelengthValue = src.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
          if (lightSourceRefWavelengthValue != null) dest.setLightSourceRefWavelength(lightSourceRefWavelengthValue, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
        }
        int roiRefCount = src.getROIRefCount(imageIndex, microbeamManipulationIndex);
        for (int roiRefIndex=0; roiRefIndex<roiRefCount; roiRefIndex++) {
          String roiRefIDValue = src.getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex);
          if (roiRefIDValue != null) dest.setROIRefID(roiRefIDValue, imageIndex, microbeamManipulationIndex, roiRefIndex);
        }
      }
        Float objectiveSettingsCorrectionCollarValue = src.getObjectiveSettingsCorrectionCollar(imageIndex);
        if (objectiveSettingsCorrectionCollarValue != null) dest.setObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollarValue, imageIndex);
        String objectiveSettingsMediumValue = src.getObjectiveSettingsMedium(imageIndex);
        if (objectiveSettingsMediumValue != null) dest.setObjectiveSettingsMedium(objectiveSettingsMediumValue, imageIndex);
        String objectiveSettingsObjectiveValue = src.getObjectiveSettingsObjective(imageIndex);
        if (objectiveSettingsObjectiveValue != null) dest.setObjectiveSettingsObjective(objectiveSettingsObjectiveValue, imageIndex);
        Float objectiveSettingsRefractiveIndexValue = src.getObjectiveSettingsRefractiveIndex(imageIndex);
        if (objectiveSettingsRefractiveIndexValue != null) dest.setObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndexValue, imageIndex);
      int pixelsCount = src.getPixelsCount(imageIndex);
      for (int pixelsIndex=0; pixelsIndex<pixelsCount; pixelsIndex++) {
        Float dimensionsPhysicalSizeXValue = src.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex);
        if (dimensionsPhysicalSizeXValue != null) dest.setDimensionsPhysicalSizeX(dimensionsPhysicalSizeXValue, imageIndex, pixelsIndex);
        Float dimensionsPhysicalSizeYValue = src.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex);
        if (dimensionsPhysicalSizeYValue != null) dest.setDimensionsPhysicalSizeY(dimensionsPhysicalSizeYValue, imageIndex, pixelsIndex);
        Float dimensionsPhysicalSizeZValue = src.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex);
        if (dimensionsPhysicalSizeZValue != null) dest.setDimensionsPhysicalSizeZ(dimensionsPhysicalSizeZValue, imageIndex, pixelsIndex);
        Float dimensionsTimeIncrementValue = src.getDimensionsTimeIncrement(imageIndex, pixelsIndex);
        if (dimensionsTimeIncrementValue != null) dest.setDimensionsTimeIncrement(dimensionsTimeIncrementValue, imageIndex, pixelsIndex);
        Integer dimensionsWaveIncrementValue = src.getDimensionsWaveIncrement(imageIndex, pixelsIndex);
        if (dimensionsWaveIncrementValue != null) dest.setDimensionsWaveIncrement(dimensionsWaveIncrementValue, imageIndex, pixelsIndex);
        Integer dimensionsWaveStartValue = src.getDimensionsWaveStart(imageIndex, pixelsIndex);
        if (dimensionsWaveStartValue != null) dest.setDimensionsWaveStart(dimensionsWaveStartValue, imageIndex, pixelsIndex);
        Boolean pixelsBigEndianValue = src.getPixelsBigEndian(imageIndex, pixelsIndex);
        if (pixelsBigEndianValue != null) dest.setPixelsBigEndian(pixelsBigEndianValue, imageIndex, pixelsIndex);
        String pixelsDimensionOrderValue = src.getPixelsDimensionOrder(imageIndex, pixelsIndex);
        if (pixelsDimensionOrderValue != null) dest.setPixelsDimensionOrder(pixelsDimensionOrderValue, imageIndex, pixelsIndex);
        String pixelsIDValue = src.getPixelsID(imageIndex, pixelsIndex);
        if (pixelsIDValue != null) dest.setPixelsID(pixelsIDValue, imageIndex, pixelsIndex);
        String pixelsPixelTypeValue = src.getPixelsPixelType(imageIndex, pixelsIndex);
        if (pixelsPixelTypeValue != null) dest.setPixelsPixelType(pixelsPixelTypeValue, imageIndex, pixelsIndex);
        Integer pixelsSizeCValue = src.getPixelsSizeC(imageIndex, pixelsIndex);
        if (pixelsSizeCValue != null) dest.setPixelsSizeC(pixelsSizeCValue, imageIndex, pixelsIndex);
        Integer pixelsSizeTValue = src.getPixelsSizeT(imageIndex, pixelsIndex);
        if (pixelsSizeTValue != null) dest.setPixelsSizeT(pixelsSizeTValue, imageIndex, pixelsIndex);
        Integer pixelsSizeXValue = src.getPixelsSizeX(imageIndex, pixelsIndex);
        if (pixelsSizeXValue != null) dest.setPixelsSizeX(pixelsSizeXValue, imageIndex, pixelsIndex);
        Integer pixelsSizeYValue = src.getPixelsSizeY(imageIndex, pixelsIndex);
        if (pixelsSizeYValue != null) dest.setPixelsSizeY(pixelsSizeYValue, imageIndex, pixelsIndex);
        Integer pixelsSizeZValue = src.getPixelsSizeZ(imageIndex, pixelsIndex);
        if (pixelsSizeZValue != null) dest.setPixelsSizeZ(pixelsSizeZValue, imageIndex, pixelsIndex);
        int planeCount = src.getPlaneCount(imageIndex, pixelsIndex);
        for (int planeIndex=0; planeIndex<planeCount; planeIndex++) {
          String planeHashSHA1Value = src.getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex);
          if (planeHashSHA1Value != null) dest.setPlaneHashSHA1(planeHashSHA1Value, imageIndex, pixelsIndex, planeIndex);
          String planeIDValue = src.getPlaneID(imageIndex, pixelsIndex, planeIndex);
          if (planeIDValue != null) dest.setPlaneID(planeIDValue, imageIndex, pixelsIndex, planeIndex);
          Integer planeTheCValue = src.getPlaneTheC(imageIndex, pixelsIndex, planeIndex);
          if (planeTheCValue != null) dest.setPlaneTheC(planeTheCValue, imageIndex, pixelsIndex, planeIndex);
          Integer planeTheTValue = src.getPlaneTheT(imageIndex, pixelsIndex, planeIndex);
          if (planeTheTValue != null) dest.setPlaneTheT(planeTheTValue, imageIndex, pixelsIndex, planeIndex);
          Integer planeTheZValue = src.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex);
          if (planeTheZValue != null) dest.setPlaneTheZ(planeTheZValue, imageIndex, pixelsIndex, planeIndex);
          Float planeTimingDeltaTValue = src.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex);
          if (planeTimingDeltaTValue != null) dest.setPlaneTimingDeltaT(planeTimingDeltaTValue, imageIndex, pixelsIndex, planeIndex);
          Float planeTimingExposureTimeValue = src.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex);
          if (planeTimingExposureTimeValue != null) dest.setPlaneTimingExposureTime(planeTimingExposureTimeValue, imageIndex, pixelsIndex, planeIndex);
          Float stagePositionPositionXValue = src.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex);
          if (stagePositionPositionXValue != null) dest.setStagePositionPositionX(stagePositionPositionXValue, imageIndex, pixelsIndex, planeIndex);
          Float stagePositionPositionYValue = src.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex);
          if (stagePositionPositionYValue != null) dest.setStagePositionPositionY(stagePositionPositionYValue, imageIndex, pixelsIndex, planeIndex);
          Float stagePositionPositionZValue = src.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex);
          if (stagePositionPositionZValue != null) dest.setStagePositionPositionZ(stagePositionPositionZValue, imageIndex, pixelsIndex, planeIndex);
        }
        int tiffDataCount = src.getTiffDataCount(imageIndex, pixelsIndex);
        for (int tiffDataIndex=0; tiffDataIndex<tiffDataCount; tiffDataIndex++) {
          String tiffDataFileNameValue = src.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataFileNameValue != null) dest.setTiffDataFileName(tiffDataFileNameValue, imageIndex, pixelsIndex, tiffDataIndex);
          Integer tiffDataFirstCValue = src.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataFirstCValue != null) dest.setTiffDataFirstC(tiffDataFirstCValue, imageIndex, pixelsIndex, tiffDataIndex);
          Integer tiffDataFirstTValue = src.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataFirstTValue != null) dest.setTiffDataFirstT(tiffDataFirstTValue, imageIndex, pixelsIndex, tiffDataIndex);
          Integer tiffDataFirstZValue = src.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataFirstZValue != null) dest.setTiffDataFirstZ(tiffDataFirstZValue, imageIndex, pixelsIndex, tiffDataIndex);
          Integer tiffDataIFDValue = src.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataIFDValue != null) dest.setTiffDataIFD(tiffDataIFDValue, imageIndex, pixelsIndex, tiffDataIndex);
          Integer tiffDataNumPlanesValue = src.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataNumPlanesValue != null) dest.setTiffDataNumPlanes(tiffDataNumPlanesValue, imageIndex, pixelsIndex, tiffDataIndex);
          String tiffDataUUIDValue = src.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex);
          if (tiffDataUUIDValue != null) dest.setTiffDataUUID(tiffDataUUIDValue, imageIndex, pixelsIndex, tiffDataIndex);
        }
      }
      int roiCount = src.getROICount(imageIndex);
      for (int roiIndex=0; roiIndex<roiCount; roiIndex++) {
        String roiidValue = src.getROIID(imageIndex, roiIndex);
        if (roiidValue != null) dest.setROIID(roiidValue, imageIndex, roiIndex);
        Integer roiT0Value = src.getROIT0(imageIndex, roiIndex);
        if (roiT0Value != null) dest.setROIT0(roiT0Value, imageIndex, roiIndex);
        Integer roiT1Value = src.getROIT1(imageIndex, roiIndex);
        if (roiT1Value != null) dest.setROIT1(roiT1Value, imageIndex, roiIndex);
        Integer roiX0Value = src.getROIX0(imageIndex, roiIndex);
        if (roiX0Value != null) dest.setROIX0(roiX0Value, imageIndex, roiIndex);
        Integer roiX1Value = src.getROIX1(imageIndex, roiIndex);
        if (roiX1Value != null) dest.setROIX1(roiX1Value, imageIndex, roiIndex);
        Integer roiY0Value = src.getROIY0(imageIndex, roiIndex);
        if (roiY0Value != null) dest.setROIY0(roiY0Value, imageIndex, roiIndex);
        Integer roiY1Value = src.getROIY1(imageIndex, roiIndex);
        if (roiY1Value != null) dest.setROIY1(roiY1Value, imageIndex, roiIndex);
        Integer roiZ0Value = src.getROIZ0(imageIndex, roiIndex);
        if (roiZ0Value != null) dest.setROIZ0(roiZ0Value, imageIndex, roiIndex);
        Integer roiZ1Value = src.getROIZ1(imageIndex, roiIndex);
        if (roiZ1Value != null) dest.setROIZ1(roiZ1Value, imageIndex, roiIndex);
        int roiLinkCount = src.getRoiLinkCount(imageIndex, roiIndex);
        for (int roiLinkIndex=0; roiLinkIndex<roiLinkCount; roiLinkIndex++) {
          String roiLinkDirectionValue = src.getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex);
          if (roiLinkDirectionValue != null) dest.setRoiLinkDirection(roiLinkDirectionValue, imageIndex, roiIndex, roiLinkIndex);
          String roiLinkNameValue = src.getRoiLinkName(imageIndex, roiIndex, roiLinkIndex);
          if (roiLinkNameValue != null) dest.setRoiLinkName(roiLinkNameValue, imageIndex, roiIndex, roiLinkIndex);
          String roiLinkRefValue = src.getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex);
          if (roiLinkRefValue != null) dest.setRoiLinkRef(roiLinkRefValue, imageIndex, roiIndex, roiLinkIndex);
        }
        int shapeCount = src.getShapeCount(imageIndex, roiIndex);
        for (int shapeIndex=0; shapeIndex<shapeCount; shapeIndex++) {
          String shapeBaselineShiftValue = src.getShapeBaselineShift(imageIndex, roiIndex, shapeIndex);
          if (shapeBaselineShiftValue != null) dest.setShapeBaselineShift(shapeBaselineShiftValue, imageIndex, roiIndex, shapeIndex);
          String shapeDirectionValue = src.getShapeDirection(imageIndex, roiIndex, shapeIndex);
          if (shapeDirectionValue != null) dest.setShapeDirection(shapeDirectionValue, imageIndex, roiIndex, shapeIndex);
          String shapeFillColorValue = src.getShapeFillColor(imageIndex, roiIndex, shapeIndex);
          if (shapeFillColorValue != null) dest.setShapeFillColor(shapeFillColorValue, imageIndex, roiIndex, shapeIndex);
          String shapeFillOpacityValue = src.getShapeFillOpacity(imageIndex, roiIndex, shapeIndex);
          if (shapeFillOpacityValue != null) dest.setShapeFillOpacity(shapeFillOpacityValue, imageIndex, roiIndex, shapeIndex);
          String shapeFillRuleValue = src.getShapeFillRule(imageIndex, roiIndex, shapeIndex);
          if (shapeFillRuleValue != null) dest.setShapeFillRule(shapeFillRuleValue, imageIndex, roiIndex, shapeIndex);
          String shapeFontFamilyValue = src.getShapeFontFamily(imageIndex, roiIndex, shapeIndex);
          if (shapeFontFamilyValue != null) dest.setShapeFontFamily(shapeFontFamilyValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeFontSizeValue = src.getShapeFontSize(imageIndex, roiIndex, shapeIndex);
          if (shapeFontSizeValue != null) dest.setShapeFontSize(shapeFontSizeValue, imageIndex, roiIndex, shapeIndex);
          String shapeFontStretchValue = src.getShapeFontStretch(imageIndex, roiIndex, shapeIndex);
          if (shapeFontStretchValue != null) dest.setShapeFontStretch(shapeFontStretchValue, imageIndex, roiIndex, shapeIndex);
          String shapeFontStyleValue = src.getShapeFontStyle(imageIndex, roiIndex, shapeIndex);
          if (shapeFontStyleValue != null) dest.setShapeFontStyle(shapeFontStyleValue, imageIndex, roiIndex, shapeIndex);
          String shapeFontVariantValue = src.getShapeFontVariant(imageIndex, roiIndex, shapeIndex);
          if (shapeFontVariantValue != null) dest.setShapeFontVariant(shapeFontVariantValue, imageIndex, roiIndex, shapeIndex);
          String shapeFontWeightValue = src.getShapeFontWeight(imageIndex, roiIndex, shapeIndex);
          if (shapeFontWeightValue != null) dest.setShapeFontWeight(shapeFontWeightValue, imageIndex, roiIndex, shapeIndex);
          String shapeGValue = src.getShapeG(imageIndex, roiIndex, shapeIndex);
          if (shapeGValue != null) dest.setShapeG(shapeGValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeGlyphOrientationVerticalValue = src.getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex);
          if (shapeGlyphOrientationVerticalValue != null) dest.setShapeGlyphOrientationVertical(shapeGlyphOrientationVerticalValue, imageIndex, roiIndex, shapeIndex);
          String shapeIDValue = src.getShapeID(imageIndex, roiIndex, shapeIndex);
          if (shapeIDValue != null) dest.setShapeID(shapeIDValue, imageIndex, roiIndex, shapeIndex);
          Boolean shapeLockedValue = src.getShapeLocked(imageIndex, roiIndex, shapeIndex);
          if (shapeLockedValue != null) dest.setShapeLocked(shapeLockedValue, imageIndex, roiIndex, shapeIndex);
          String shapeStrokeAttributeValue = src.getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeAttributeValue != null) dest.setShapeStrokeAttribute(shapeStrokeAttributeValue, imageIndex, roiIndex, shapeIndex);
          String shapeStrokeColorValue = src.getShapeStrokeColor(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeColorValue != null) dest.setShapeStrokeColor(shapeStrokeColorValue, imageIndex, roiIndex, shapeIndex);
          String shapeStrokeDashArrayValue = src.getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeDashArrayValue != null) dest.setShapeStrokeDashArray(shapeStrokeDashArrayValue, imageIndex, roiIndex, shapeIndex);
          String shapeStrokeLineCapValue = src.getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeLineCapValue != null) dest.setShapeStrokeLineCap(shapeStrokeLineCapValue, imageIndex, roiIndex, shapeIndex);
          String shapeStrokeLineJoinValue = src.getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeLineJoinValue != null) dest.setShapeStrokeLineJoin(shapeStrokeLineJoinValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeStrokeMiterLimitValue = src.getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeMiterLimitValue != null) dest.setShapeStrokeMiterLimit(shapeStrokeMiterLimitValue, imageIndex, roiIndex, shapeIndex);
          Float shapeStrokeOpacityValue = src.getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeOpacityValue != null) dest.setShapeStrokeOpacity(shapeStrokeOpacityValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeStrokeWidthValue = src.getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex);
          if (shapeStrokeWidthValue != null) dest.setShapeStrokeWidth(shapeStrokeWidthValue, imageIndex, roiIndex, shapeIndex);
          String shapeTextValue = src.getShapeText(imageIndex, roiIndex, shapeIndex);
          if (shapeTextValue != null) dest.setShapeText(shapeTextValue, imageIndex, roiIndex, shapeIndex);
          String shapeTextAnchorValue = src.getShapeTextAnchor(imageIndex, roiIndex, shapeIndex);
          if (shapeTextAnchorValue != null) dest.setShapeTextAnchor(shapeTextAnchorValue, imageIndex, roiIndex, shapeIndex);
          String shapeTextDecorationValue = src.getShapeTextDecoration(imageIndex, roiIndex, shapeIndex);
          if (shapeTextDecorationValue != null) dest.setShapeTextDecoration(shapeTextDecorationValue, imageIndex, roiIndex, shapeIndex);
          String shapeTextFillValue = src.getShapeTextFill(imageIndex, roiIndex, shapeIndex);
          if (shapeTextFillValue != null) dest.setShapeTextFill(shapeTextFillValue, imageIndex, roiIndex, shapeIndex);
          String shapeTextStrokeValue = src.getShapeTextStroke(imageIndex, roiIndex, shapeIndex);
          if (shapeTextStrokeValue != null) dest.setShapeTextStroke(shapeTextStrokeValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeTheTValue = src.getShapeTheT(imageIndex, roiIndex, shapeIndex);
          if (shapeTheTValue != null) dest.setShapeTheT(shapeTheTValue, imageIndex, roiIndex, shapeIndex);
          Integer shapeTheZValue = src.getShapeTheZ(imageIndex, roiIndex, shapeIndex);
          if (shapeTheZValue != null) dest.setShapeTheZ(shapeTheZValue, imageIndex, roiIndex, shapeIndex);
          String shapeVectorEffectValue = src.getShapeVectorEffect(imageIndex, roiIndex, shapeIndex);
          if (shapeVectorEffectValue != null) dest.setShapeVectorEffect(shapeVectorEffectValue, imageIndex, roiIndex, shapeIndex);
          Boolean shapeVisibilityValue = src.getShapeVisibility(imageIndex, roiIndex, shapeIndex);
          if (shapeVisibilityValue != null) dest.setShapeVisibility(shapeVisibilityValue, imageIndex, roiIndex, shapeIndex);
          String shapeWritingModeValue = src.getShapeWritingMode(imageIndex, roiIndex, shapeIndex);
          if (shapeWritingModeValue != null) dest.setShapeWritingMode(shapeWritingModeValue, imageIndex, roiIndex, shapeIndex);
          String circleCxValue = src.getCircleCx(imageIndex, roiIndex, shapeIndex);
          if (circleCxValue != null) dest.setCircleCx(circleCxValue, imageIndex, roiIndex, shapeIndex);
          String circleCyValue = src.getCircleCy(imageIndex, roiIndex, shapeIndex);
          if (circleCyValue != null) dest.setCircleCy(circleCyValue, imageIndex, roiIndex, shapeIndex);
          String circleIDValue = src.getCircleID(imageIndex, roiIndex, shapeIndex);
          if (circleIDValue != null) dest.setCircleID(circleIDValue, imageIndex, roiIndex, shapeIndex);
          String circleRValue = src.getCircleR(imageIndex, roiIndex, shapeIndex);
          if (circleRValue != null) dest.setCircleR(circleRValue, imageIndex, roiIndex, shapeIndex);
          String circleTransformValue = src.getCircleTransform(imageIndex, roiIndex, shapeIndex);
          if (circleTransformValue != null) dest.setCircleTransform(circleTransformValue, imageIndex, roiIndex, shapeIndex);
          String ellipseCxValue = src.getEllipseCx(imageIndex, roiIndex, shapeIndex);
          if (ellipseCxValue != null) dest.setEllipseCx(ellipseCxValue, imageIndex, roiIndex, shapeIndex);
          String ellipseCyValue = src.getEllipseCy(imageIndex, roiIndex, shapeIndex);
          if (ellipseCyValue != null) dest.setEllipseCy(ellipseCyValue, imageIndex, roiIndex, shapeIndex);
          String ellipseIDValue = src.getEllipseID(imageIndex, roiIndex, shapeIndex);
          if (ellipseIDValue != null) dest.setEllipseID(ellipseIDValue, imageIndex, roiIndex, shapeIndex);
          String ellipseRxValue = src.getEllipseRx(imageIndex, roiIndex, shapeIndex);
          if (ellipseRxValue != null) dest.setEllipseRx(ellipseRxValue, imageIndex, roiIndex, shapeIndex);
          String ellipseRyValue = src.getEllipseRy(imageIndex, roiIndex, shapeIndex);
          if (ellipseRyValue != null) dest.setEllipseRy(ellipseRyValue, imageIndex, roiIndex, shapeIndex);
          String ellipseTransformValue = src.getEllipseTransform(imageIndex, roiIndex, shapeIndex);
          if (ellipseTransformValue != null) dest.setEllipseTransform(ellipseTransformValue, imageIndex, roiIndex, shapeIndex);
          String lineIDValue = src.getLineID(imageIndex, roiIndex, shapeIndex);
          if (lineIDValue != null) dest.setLineID(lineIDValue, imageIndex, roiIndex, shapeIndex);
          String lineTransformValue = src.getLineTransform(imageIndex, roiIndex, shapeIndex);
          if (lineTransformValue != null) dest.setLineTransform(lineTransformValue, imageIndex, roiIndex, shapeIndex);
          String lineX1Value = src.getLineX1(imageIndex, roiIndex, shapeIndex);
          if (lineX1Value != null) dest.setLineX1(lineX1Value, imageIndex, roiIndex, shapeIndex);
          String lineX2Value = src.getLineX2(imageIndex, roiIndex, shapeIndex);
          if (lineX2Value != null) dest.setLineX2(lineX2Value, imageIndex, roiIndex, shapeIndex);
          String lineY1Value = src.getLineY1(imageIndex, roiIndex, shapeIndex);
          if (lineY1Value != null) dest.setLineY1(lineY1Value, imageIndex, roiIndex, shapeIndex);
          String lineY2Value = src.getLineY2(imageIndex, roiIndex, shapeIndex);
          if (lineY2Value != null) dest.setLineY2(lineY2Value, imageIndex, roiIndex, shapeIndex);
          String maskHeightValue = src.getMaskHeight(imageIndex, roiIndex, shapeIndex);
          if (maskHeightValue != null) dest.setMaskHeight(maskHeightValue, imageIndex, roiIndex, shapeIndex);
          String maskIDValue = src.getMaskID(imageIndex, roiIndex, shapeIndex);
          if (maskIDValue != null) dest.setMaskID(maskIDValue, imageIndex, roiIndex, shapeIndex);
          String maskTransformValue = src.getMaskTransform(imageIndex, roiIndex, shapeIndex);
          if (maskTransformValue != null) dest.setMaskTransform(maskTransformValue, imageIndex, roiIndex, shapeIndex);
          String maskWidthValue = src.getMaskWidth(imageIndex, roiIndex, shapeIndex);
          if (maskWidthValue != null) dest.setMaskWidth(maskWidthValue, imageIndex, roiIndex, shapeIndex);
          String maskXValue = src.getMaskX(imageIndex, roiIndex, shapeIndex);
          if (maskXValue != null) dest.setMaskX(maskXValue, imageIndex, roiIndex, shapeIndex);
          String maskYValue = src.getMaskY(imageIndex, roiIndex, shapeIndex);
          if (maskYValue != null) dest.setMaskY(maskYValue, imageIndex, roiIndex, shapeIndex);
          Boolean maskPixelsBigEndianValue = src.getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsBigEndianValue != null) dest.setMaskPixelsBigEndian(maskPixelsBigEndianValue, imageIndex, roiIndex, shapeIndex);
          byte[] maskPixelsBinDataValue = src.getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsBinDataValue != null) dest.setMaskPixelsBinData(maskPixelsBinDataValue, imageIndex, roiIndex, shapeIndex);
          String maskPixelsExtendedPixelTypeValue = src.getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsExtendedPixelTypeValue != null) dest.setMaskPixelsExtendedPixelType(maskPixelsExtendedPixelTypeValue, imageIndex, roiIndex, shapeIndex);
          String maskPixelsIDValue = src.getMaskPixelsID(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsIDValue != null) dest.setMaskPixelsID(maskPixelsIDValue, imageIndex, roiIndex, shapeIndex);
          Integer maskPixelsSizeXValue = src.getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsSizeXValue != null) dest.setMaskPixelsSizeX(maskPixelsSizeXValue, imageIndex, roiIndex, shapeIndex);
          Integer maskPixelsSizeYValue = src.getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex);
          if (maskPixelsSizeYValue != null) dest.setMaskPixelsSizeY(maskPixelsSizeYValue, imageIndex, roiIndex, shapeIndex);
          String pathDValue = src.getPathD(imageIndex, roiIndex, shapeIndex);
          if (pathDValue != null) dest.setPathD(pathDValue, imageIndex, roiIndex, shapeIndex);
          String pathIDValue = src.getPathID(imageIndex, roiIndex, shapeIndex);
          if (pathIDValue != null) dest.setPathID(pathIDValue, imageIndex, roiIndex, shapeIndex);
          String pointCxValue = src.getPointCx(imageIndex, roiIndex, shapeIndex);
          if (pointCxValue != null) dest.setPointCx(pointCxValue, imageIndex, roiIndex, shapeIndex);
          String pointCyValue = src.getPointCy(imageIndex, roiIndex, shapeIndex);
          if (pointCyValue != null) dest.setPointCy(pointCyValue, imageIndex, roiIndex, shapeIndex);
          String pointIDValue = src.getPointID(imageIndex, roiIndex, shapeIndex);
          if (pointIDValue != null) dest.setPointID(pointIDValue, imageIndex, roiIndex, shapeIndex);
          String pointRValue = src.getPointR(imageIndex, roiIndex, shapeIndex);
          if (pointRValue != null) dest.setPointR(pointRValue, imageIndex, roiIndex, shapeIndex);
          String pointTransformValue = src.getPointTransform(imageIndex, roiIndex, shapeIndex);
          if (pointTransformValue != null) dest.setPointTransform(pointTransformValue, imageIndex, roiIndex, shapeIndex);
          String polygonIDValue = src.getPolygonID(imageIndex, roiIndex, shapeIndex);
          if (polygonIDValue != null) dest.setPolygonID(polygonIDValue, imageIndex, roiIndex, shapeIndex);
          String polygonPointsValue = src.getPolygonPoints(imageIndex, roiIndex, shapeIndex);
          if (polygonPointsValue != null) dest.setPolygonPoints(polygonPointsValue, imageIndex, roiIndex, shapeIndex);
          String polygonTransformValue = src.getPolygonTransform(imageIndex, roiIndex, shapeIndex);
          if (polygonTransformValue != null) dest.setPolygonTransform(polygonTransformValue, imageIndex, roiIndex, shapeIndex);
          String polylineIDValue = src.getPolylineID(imageIndex, roiIndex, shapeIndex);
          if (polylineIDValue != null) dest.setPolylineID(polylineIDValue, imageIndex, roiIndex, shapeIndex);
          String polylinePointsValue = src.getPolylinePoints(imageIndex, roiIndex, shapeIndex);
          if (polylinePointsValue != null) dest.setPolylinePoints(polylinePointsValue, imageIndex, roiIndex, shapeIndex);
          String polylineTransformValue = src.getPolylineTransform(imageIndex, roiIndex, shapeIndex);
          if (polylineTransformValue != null) dest.setPolylineTransform(polylineTransformValue, imageIndex, roiIndex, shapeIndex);
          String rectHeightValue = src.getRectHeight(imageIndex, roiIndex, shapeIndex);
          if (rectHeightValue != null) dest.setRectHeight(rectHeightValue, imageIndex, roiIndex, shapeIndex);
          String rectIDValue = src.getRectID(imageIndex, roiIndex, shapeIndex);
          if (rectIDValue != null) dest.setRectID(rectIDValue, imageIndex, roiIndex, shapeIndex);
          String rectTransformValue = src.getRectTransform(imageIndex, roiIndex, shapeIndex);
          if (rectTransformValue != null) dest.setRectTransform(rectTransformValue, imageIndex, roiIndex, shapeIndex);
          String rectWidthValue = src.getRectWidth(imageIndex, roiIndex, shapeIndex);
          if (rectWidthValue != null) dest.setRectWidth(rectWidthValue, imageIndex, roiIndex, shapeIndex);
          String rectXValue = src.getRectX(imageIndex, roiIndex, shapeIndex);
          if (rectXValue != null) dest.setRectX(rectXValue, imageIndex, roiIndex, shapeIndex);
          String rectYValue = src.getRectY(imageIndex, roiIndex, shapeIndex);
          if (rectYValue != null) dest.setRectY(rectYValue, imageIndex, roiIndex, shapeIndex);
        }
      }
      int regionCount = src.getRegionCount(imageIndex);
      for (int regionIndex=0; regionIndex<regionCount; regionIndex++) {
        String regionIDValue = src.getRegionID(imageIndex, regionIndex);
        if (regionIDValue != null) dest.setRegionID(regionIDValue, imageIndex, regionIndex);
        String regionNameValue = src.getRegionName(imageIndex, regionIndex);
        if (regionNameValue != null) dest.setRegionName(regionNameValue, imageIndex, regionIndex);
        String regionTagValue = src.getRegionTag(imageIndex, regionIndex);
        if (regionTagValue != null) dest.setRegionTag(regionTagValue, imageIndex, regionIndex);
      }
        String stageLabelNameValue = src.getStageLabelName(imageIndex);
        if (stageLabelNameValue != null) dest.setStageLabelName(stageLabelNameValue, imageIndex);
        Float stageLabelXValue = src.getStageLabelX(imageIndex);
        if (stageLabelXValue != null) dest.setStageLabelX(stageLabelXValue, imageIndex);
        Float stageLabelYValue = src.getStageLabelY(imageIndex);
        if (stageLabelYValue != null) dest.setStageLabelY(stageLabelYValue, imageIndex);
        Float stageLabelZValue = src.getStageLabelZ(imageIndex);
        if (stageLabelZValue != null) dest.setStageLabelZ(stageLabelZValue, imageIndex);
        String thumbnailHrefValue = src.getThumbnailHref(imageIndex);
        if (thumbnailHrefValue != null) dest.setThumbnailHref(thumbnailHrefValue, imageIndex);
        String thumbnailIDValue = src.getThumbnailID(imageIndex);
        if (thumbnailIDValue != null) dest.setThumbnailID(thumbnailIDValue, imageIndex);
        String thumbnailMIMEtypeValue = src.getThumbnailMIMEtype(imageIndex);
        if (thumbnailMIMEtypeValue != null) dest.setThumbnailMIMEtype(thumbnailMIMEtypeValue, imageIndex);
    }
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
      String instrumentIDValue = src.getInstrumentID(instrumentIndex);
      if (instrumentIDValue != null) dest.setInstrumentID(instrumentIDValue, instrumentIndex);
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
        Float detectorAmplificationGainValue = src.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
        if (detectorAmplificationGainValue != null) dest.setDetectorAmplificationGain(detectorAmplificationGainValue, instrumentIndex, detectorIndex);
        Float detectorGainValue = src.getDetectorGain(instrumentIndex, detectorIndex);
        if (detectorGainValue != null) dest.setDetectorGain(detectorGainValue, instrumentIndex, detectorIndex);
        String detectorIDValue = src.getDetectorID(instrumentIndex, detectorIndex);
        if (detectorIDValue != null) dest.setDetectorID(detectorIDValue, instrumentIndex, detectorIndex);
        String detectorManufacturerValue = src.getDetectorManufacturer(instrumentIndex, detectorIndex);
        if (detectorManufacturerValue != null) dest.setDetectorManufacturer(detectorManufacturerValue, instrumentIndex, detectorIndex);
        String detectorModelValue = src.getDetectorModel(instrumentIndex, detectorIndex);
        if (detectorModelValue != null) dest.setDetectorModel(detectorModelValue, instrumentIndex, detectorIndex);
        Float detectorOffsetValue = src.getDetectorOffset(instrumentIndex, detectorIndex);
        if (detectorOffsetValue != null) dest.setDetectorOffset(detectorOffsetValue, instrumentIndex, detectorIndex);
        String detectorSerialNumberValue = src.getDetectorSerialNumber(instrumentIndex, detectorIndex);
        if (detectorSerialNumberValue != null) dest.setDetectorSerialNumber(detectorSerialNumberValue, instrumentIndex, detectorIndex);
        String detectorTypeValue = src.getDetectorType(instrumentIndex, detectorIndex);
        if (detectorTypeValue != null) dest.setDetectorType(detectorTypeValue, instrumentIndex, detectorIndex);
        Float detectorVoltageValue = src.getDetectorVoltage(instrumentIndex, detectorIndex);
        if (detectorVoltageValue != null) dest.setDetectorVoltage(detectorVoltageValue, instrumentIndex, detectorIndex);
        Float detectorZoomValue = src.getDetectorZoom(instrumentIndex, detectorIndex);
        if (detectorZoomValue != null) dest.setDetectorZoom(detectorZoomValue, instrumentIndex, detectorIndex);
      }
      int dichroicCount = src.getDichroicCount(instrumentIndex);
      for (int dichroicIndex=0; dichroicIndex<dichroicCount; dichroicIndex++) {
        String dichroicIDValue = src.getDichroicID(instrumentIndex, dichroicIndex);
        if (dichroicIDValue != null) dest.setDichroicID(dichroicIDValue, instrumentIndex, dichroicIndex);
        String dichroicLotNumberValue = src.getDichroicLotNumber(instrumentIndex, dichroicIndex);
        if (dichroicLotNumberValue != null) dest.setDichroicLotNumber(dichroicLotNumberValue, instrumentIndex, dichroicIndex);
        String dichroicManufacturerValue = src.getDichroicManufacturer(instrumentIndex, dichroicIndex);
        if (dichroicManufacturerValue != null) dest.setDichroicManufacturer(dichroicManufacturerValue, instrumentIndex, dichroicIndex);
        String dichroicModelValue = src.getDichroicModel(instrumentIndex, dichroicIndex);
        if (dichroicModelValue != null) dest.setDichroicModel(dichroicModelValue, instrumentIndex, dichroicIndex);
      }
      int filterCount = src.getFilterCount(instrumentIndex);
      for (int filterIndex=0; filterIndex<filterCount; filterIndex++) {
        String filterFilterWheelValue = src.getFilterFilterWheel(instrumentIndex, filterIndex);
        if (filterFilterWheelValue != null) dest.setFilterFilterWheel(filterFilterWheelValue, instrumentIndex, filterIndex);
        String filterIDValue = src.getFilterID(instrumentIndex, filterIndex);
        if (filterIDValue != null) dest.setFilterID(filterIDValue, instrumentIndex, filterIndex);
        String filterLotNumberValue = src.getFilterLotNumber(instrumentIndex, filterIndex);
        if (filterLotNumberValue != null) dest.setFilterLotNumber(filterLotNumberValue, instrumentIndex, filterIndex);
        String filterManufacturerValue = src.getFilterManufacturer(instrumentIndex, filterIndex);
        if (filterManufacturerValue != null) dest.setFilterManufacturer(filterManufacturerValue, instrumentIndex, filterIndex);
        String filterModelValue = src.getFilterModel(instrumentIndex, filterIndex);
        if (filterModelValue != null) dest.setFilterModel(filterModelValue, instrumentIndex, filterIndex);
        String filterTypeValue = src.getFilterType(instrumentIndex, filterIndex);
        if (filterTypeValue != null) dest.setFilterType(filterTypeValue, instrumentIndex, filterIndex);
        String emFilterLotNumberValue = src.getEmFilterLotNumber(instrumentIndex, filterIndex);
        if (emFilterLotNumberValue != null) dest.setEmFilterLotNumber(emFilterLotNumberValue, instrumentIndex, filterIndex);
        String emFilterManufacturerValue = src.getEmFilterManufacturer(instrumentIndex, filterIndex);
        if (emFilterManufacturerValue != null) dest.setEmFilterManufacturer(emFilterManufacturerValue, instrumentIndex, filterIndex);
        String emFilterModelValue = src.getEmFilterModel(instrumentIndex, filterIndex);
        if (emFilterModelValue != null) dest.setEmFilterModel(emFilterModelValue, instrumentIndex, filterIndex);
        String emFilterTypeValue = src.getEmFilterType(instrumentIndex, filterIndex);
        if (emFilterTypeValue != null) dest.setEmFilterType(emFilterTypeValue, instrumentIndex, filterIndex);
        String exFilterLotNumberValue = src.getExFilterLotNumber(instrumentIndex, filterIndex);
        if (exFilterLotNumberValue != null) dest.setExFilterLotNumber(exFilterLotNumberValue, instrumentIndex, filterIndex);
        String exFilterManufacturerValue = src.getExFilterManufacturer(instrumentIndex, filterIndex);
        if (exFilterManufacturerValue != null) dest.setExFilterManufacturer(exFilterManufacturerValue, instrumentIndex, filterIndex);
        String exFilterModelValue = src.getExFilterModel(instrumentIndex, filterIndex);
        if (exFilterModelValue != null) dest.setExFilterModel(exFilterModelValue, instrumentIndex, filterIndex);
        String exFilterTypeValue = src.getExFilterType(instrumentIndex, filterIndex);
        if (exFilterTypeValue != null) dest.setExFilterType(exFilterTypeValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutInValue = src.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInValue != null) dest.setTransmittanceRangeCutIn(transmittanceRangeCutInValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutInToleranceValue = src.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInToleranceValue != null) dest.setTransmittanceRangeCutInTolerance(transmittanceRangeCutInToleranceValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutOutValue = src.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutValue != null) dest.setTransmittanceRangeCutOut(transmittanceRangeCutOutValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutOutToleranceValue = src.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutToleranceValue != null) dest.setTransmittanceRangeCutOutTolerance(transmittanceRangeCutOutToleranceValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeTransmittanceValue = src.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
        if (transmittanceRangeTransmittanceValue != null) dest.setTransmittanceRangeTransmittance(transmittanceRangeTransmittanceValue, instrumentIndex, filterIndex);
      }
      int filterSetCount = src.getFilterSetCount(instrumentIndex);
      for (int filterSetIndex=0; filterSetIndex<filterSetCount; filterSetIndex++) {
        String filterSetDichroicValue = src.getFilterSetDichroic(instrumentIndex, filterSetIndex);
        if (filterSetDichroicValue != null) dest.setFilterSetDichroic(filterSetDichroicValue, instrumentIndex, filterSetIndex);
        String filterSetEmFilterValue = src.getFilterSetEmFilter(instrumentIndex, filterSetIndex);
        if (filterSetEmFilterValue != null) dest.setFilterSetEmFilter(filterSetEmFilterValue, instrumentIndex, filterSetIndex);
        String filterSetExFilterValue = src.getFilterSetExFilter(instrumentIndex, filterSetIndex);
        if (filterSetExFilterValue != null) dest.setFilterSetExFilter(filterSetExFilterValue, instrumentIndex, filterSetIndex);
        String filterSetIDValue = src.getFilterSetID(instrumentIndex, filterSetIndex);
        if (filterSetIDValue != null) dest.setFilterSetID(filterSetIDValue, instrumentIndex, filterSetIndex);
        String filterSetLotNumberValue = src.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
        if (filterSetLotNumberValue != null) dest.setFilterSetLotNumber(filterSetLotNumberValue, instrumentIndex, filterSetIndex);
        String filterSetManufacturerValue = src.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
        if (filterSetManufacturerValue != null) dest.setFilterSetManufacturer(filterSetManufacturerValue, instrumentIndex, filterSetIndex);
        String filterSetModelValue = src.getFilterSetModel(instrumentIndex, filterSetIndex);
        if (filterSetModelValue != null) dest.setFilterSetModel(filterSetModelValue, instrumentIndex, filterSetIndex);
      }
      int lightSourceCount = src.getLightSourceCount(instrumentIndex);
      for (int lightSourceIndex=0; lightSourceIndex<lightSourceCount; lightSourceIndex++) {
        String lightSourceIDValue = src.getLightSourceID(instrumentIndex, lightSourceIndex);
        if (lightSourceIDValue != null) dest.setLightSourceID(lightSourceIDValue, instrumentIndex, lightSourceIndex);
        String lightSourceManufacturerValue = src.getLightSourceManufacturer(instrumentIndex, lightSourceIndex);
        if (lightSourceManufacturerValue != null) dest.setLightSourceManufacturer(lightSourceManufacturerValue, instrumentIndex, lightSourceIndex);
        String lightSourceModelValue = src.getLightSourceModel(instrumentIndex, lightSourceIndex);
        if (lightSourceModelValue != null) dest.setLightSourceModel(lightSourceModelValue, instrumentIndex, lightSourceIndex);
        Float lightSourcePowerValue = src.getLightSourcePower(instrumentIndex, lightSourceIndex);
        if (lightSourcePowerValue != null) dest.setLightSourcePower(lightSourcePowerValue, instrumentIndex, lightSourceIndex);
        String lightSourceSerialNumberValue = src.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex);
        if (lightSourceSerialNumberValue != null) dest.setLightSourceSerialNumber(lightSourceSerialNumberValue, instrumentIndex, lightSourceIndex);
        String arcTypeValue = src.getArcType(instrumentIndex, lightSourceIndex);
        if (arcTypeValue != null) dest.setArcType(arcTypeValue, instrumentIndex, lightSourceIndex);
        String filamentTypeValue = src.getFilamentType(instrumentIndex, lightSourceIndex);
        if (filamentTypeValue != null) dest.setFilamentType(filamentTypeValue, instrumentIndex, lightSourceIndex);
        Integer laserFrequencyMultiplicationValue = src.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex);
        if (laserFrequencyMultiplicationValue != null) dest.setLaserFrequencyMultiplication(laserFrequencyMultiplicationValue, instrumentIndex, lightSourceIndex);
        String laserLaserMediumValue = src.getLaserLaserMedium(instrumentIndex, lightSourceIndex);
        if (laserLaserMediumValue != null) dest.setLaserLaserMedium(laserLaserMediumValue, instrumentIndex, lightSourceIndex);
        Boolean laserPockelCellValue = src.getLaserPockelCell(instrumentIndex, lightSourceIndex);
        if (laserPockelCellValue != null) dest.setLaserPockelCell(laserPockelCellValue, instrumentIndex, lightSourceIndex);
        String laserPulseValue = src.getLaserPulse(instrumentIndex, lightSourceIndex);
        if (laserPulseValue != null) dest.setLaserPulse(laserPulseValue, instrumentIndex, lightSourceIndex);
        Boolean laserRepetitionRateValue = src.getLaserRepetitionRate(instrumentIndex, lightSourceIndex);
        if (laserRepetitionRateValue != null) dest.setLaserRepetitionRate(laserRepetitionRateValue, instrumentIndex, lightSourceIndex);
        Boolean laserTuneableValue = src.getLaserTuneable(instrumentIndex, lightSourceIndex);
        if (laserTuneableValue != null) dest.setLaserTuneable(laserTuneableValue, instrumentIndex, lightSourceIndex);
        String laserTypeValue = src.getLaserType(instrumentIndex, lightSourceIndex);
        if (laserTypeValue != null) dest.setLaserType(laserTypeValue, instrumentIndex, lightSourceIndex);
        Integer laserWavelengthValue = src.getLaserWavelength(instrumentIndex, lightSourceIndex);
        if (laserWavelengthValue != null) dest.setLaserWavelength(laserWavelengthValue, instrumentIndex, lightSourceIndex);
        String pumpLightSourceValue = src.getPumpLightSource(instrumentIndex, lightSourceIndex);
        if (pumpLightSourceValue != null) dest.setPumpLightSource(pumpLightSourceValue, instrumentIndex, lightSourceIndex);
      }
        String microscopeIDValue = src.getMicroscopeID(instrumentIndex);
        if (microscopeIDValue != null) dest.setMicroscopeID(microscopeIDValue, instrumentIndex);
        String microscopeManufacturerValue = src.getMicroscopeManufacturer(instrumentIndex);
        if (microscopeManufacturerValue != null) dest.setMicroscopeManufacturer(microscopeManufacturerValue, instrumentIndex);
        String microscopeModelValue = src.getMicroscopeModel(instrumentIndex);
        if (microscopeModelValue != null) dest.setMicroscopeModel(microscopeModelValue, instrumentIndex);
        String microscopeSerialNumberValue = src.getMicroscopeSerialNumber(instrumentIndex);
        if (microscopeSerialNumberValue != null) dest.setMicroscopeSerialNumber(microscopeSerialNumberValue, instrumentIndex);
        String microscopeTypeValue = src.getMicroscopeType(instrumentIndex);
        if (microscopeTypeValue != null) dest.setMicroscopeType(microscopeTypeValue, instrumentIndex);
      int otfCount = src.getOTFCount(instrumentIndex);
      for (int otfIndex=0; otfIndex<otfCount; otfIndex++) {
        String otfBinaryFileValue = src.getOTFBinaryFile(instrumentIndex, otfIndex);
        if (otfBinaryFileValue != null) dest.setOTFBinaryFile(otfBinaryFileValue, instrumentIndex, otfIndex);
        String otfidValue = src.getOTFID(instrumentIndex, otfIndex);
        if (otfidValue != null) dest.setOTFID(otfidValue, instrumentIndex, otfIndex);
        String otfObjectiveValue = src.getOTFObjective(instrumentIndex, otfIndex);
        if (otfObjectiveValue != null) dest.setOTFObjective(otfObjectiveValue, instrumentIndex, otfIndex);
        Boolean otfOpticalAxisAveragedValue = src.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
        if (otfOpticalAxisAveragedValue != null) dest.setOTFOpticalAxisAveraged(otfOpticalAxisAveragedValue, instrumentIndex, otfIndex);
        String otfPixelTypeValue = src.getOTFPixelType(instrumentIndex, otfIndex);
        if (otfPixelTypeValue != null) dest.setOTFPixelType(otfPixelTypeValue, instrumentIndex, otfIndex);
        Integer otfSizeXValue = src.getOTFSizeX(instrumentIndex, otfIndex);
        if (otfSizeXValue != null) dest.setOTFSizeX(otfSizeXValue, instrumentIndex, otfIndex);
        Integer otfSizeYValue = src.getOTFSizeY(instrumentIndex, otfIndex);
        if (otfSizeYValue != null) dest.setOTFSizeY(otfSizeYValue, instrumentIndex, otfIndex);
      }
      int objectiveCount = src.getObjectiveCount(instrumentIndex);
      for (int objectiveIndex=0; objectiveIndex<objectiveCount; objectiveIndex++) {
        Float objectiveCalibratedMagnificationValue = src.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
        if (objectiveCalibratedMagnificationValue != null) dest.setObjectiveCalibratedMagnification(objectiveCalibratedMagnificationValue, instrumentIndex, objectiveIndex);
        String objectiveCorrectionValue = src.getObjectiveCorrection(instrumentIndex, objectiveIndex);
        if (objectiveCorrectionValue != null) dest.setObjectiveCorrection(objectiveCorrectionValue, instrumentIndex, objectiveIndex);
        String objectiveIDValue = src.getObjectiveID(instrumentIndex, objectiveIndex);
        if (objectiveIDValue != null) dest.setObjectiveID(objectiveIDValue, instrumentIndex, objectiveIndex);
        String objectiveImmersionValue = src.getObjectiveImmersion(instrumentIndex, objectiveIndex);
        if (objectiveImmersionValue != null) dest.setObjectiveImmersion(objectiveImmersionValue, instrumentIndex, objectiveIndex);
        Boolean objectiveIrisValue = src.getObjectiveIris(instrumentIndex, objectiveIndex);
        if (objectiveIrisValue != null) dest.setObjectiveIris(objectiveIrisValue, instrumentIndex, objectiveIndex);
        Float objectiveLensNAValue = src.getObjectiveLensNA(instrumentIndex, objectiveIndex);
        if (objectiveLensNAValue != null) dest.setObjectiveLensNA(objectiveLensNAValue, instrumentIndex, objectiveIndex);
        String objectiveManufacturerValue = src.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
        if (objectiveManufacturerValue != null) dest.setObjectiveManufacturer(objectiveManufacturerValue, instrumentIndex, objectiveIndex);
        String objectiveModelValue = src.getObjectiveModel(instrumentIndex, objectiveIndex);
        if (objectiveModelValue != null) dest.setObjectiveModel(objectiveModelValue, instrumentIndex, objectiveIndex);
        Integer objectiveNominalMagnificationValue = src.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
        if (objectiveNominalMagnificationValue != null) dest.setObjectiveNominalMagnification(objectiveNominalMagnificationValue, instrumentIndex, objectiveIndex);
        String objectiveSerialNumberValue = src.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
        if (objectiveSerialNumberValue != null) dest.setObjectiveSerialNumber(objectiveSerialNumberValue, instrumentIndex, objectiveIndex);
        Float objectiveWorkingDistanceValue = src.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
        if (objectiveWorkingDistanceValue != null) dest.setObjectiveWorkingDistance(objectiveWorkingDistanceValue, instrumentIndex, objectiveIndex);
      }
    }
    int plateCount = src.getPlateCount();
    for (int plateIndex=0; plateIndex<plateCount; plateIndex++) {
      String plateColumnNamingConventionValue = src.getPlateColumnNamingConvention(plateIndex);
      if (plateColumnNamingConventionValue != null) dest.setPlateColumnNamingConvention(plateColumnNamingConventionValue, plateIndex);
      String plateDescriptionValue = src.getPlateDescription(plateIndex);
      if (plateDescriptionValue != null) dest.setPlateDescription(plateDescriptionValue, plateIndex);
      String plateExternalIdentifierValue = src.getPlateExternalIdentifier(plateIndex);
      if (plateExternalIdentifierValue != null) dest.setPlateExternalIdentifier(plateExternalIdentifierValue, plateIndex);
      String plateIDValue = src.getPlateID(plateIndex);
      if (plateIDValue != null) dest.setPlateID(plateIDValue, plateIndex);
      String plateNameValue = src.getPlateName(plateIndex);
      if (plateNameValue != null) dest.setPlateName(plateNameValue, plateIndex);
      String plateRowNamingConventionValue = src.getPlateRowNamingConvention(plateIndex);
      if (plateRowNamingConventionValue != null) dest.setPlateRowNamingConvention(plateRowNamingConventionValue, plateIndex);
      String plateStatusValue = src.getPlateStatus(plateIndex);
      if (plateStatusValue != null) dest.setPlateStatus(plateStatusValue, plateIndex);
      Double plateWellOriginXValue = src.getPlateWellOriginX(plateIndex);
      if (plateWellOriginXValue != null) dest.setPlateWellOriginX(plateWellOriginXValue, plateIndex);
      Double plateWellOriginYValue = src.getPlateWellOriginY(plateIndex);
      if (plateWellOriginYValue != null) dest.setPlateWellOriginY(plateWellOriginYValue, plateIndex);
      int screenRefCount = src.getScreenRefCount(plateIndex);
      for (int screenRefIndex=0; screenRefIndex<screenRefCount; screenRefIndex++) {
        String screenRefIDValue = src.getScreenRefID(plateIndex, screenRefIndex);
        if (screenRefIDValue != null) dest.setScreenRefID(screenRefIDValue, plateIndex, screenRefIndex);
      }
      int wellCount = src.getWellCount(plateIndex);
      for (int wellIndex=0; wellIndex<wellCount; wellIndex++) {
        Integer wellColumnValue = src.getWellColumn(plateIndex, wellIndex);
        if (wellColumnValue != null) dest.setWellColumn(wellColumnValue, plateIndex, wellIndex);
        String wellExternalDescriptionValue = src.getWellExternalDescription(plateIndex, wellIndex);
        if (wellExternalDescriptionValue != null) dest.setWellExternalDescription(wellExternalDescriptionValue, plateIndex, wellIndex);
        String wellExternalIdentifierValue = src.getWellExternalIdentifier(plateIndex, wellIndex);
        if (wellExternalIdentifierValue != null) dest.setWellExternalIdentifier(wellExternalIdentifierValue, plateIndex, wellIndex);
        String wellIDValue = src.getWellID(plateIndex, wellIndex);
        if (wellIDValue != null) dest.setWellID(wellIDValue, plateIndex, wellIndex);
        String wellReagentValue = src.getWellReagent(plateIndex, wellIndex);
        if (wellReagentValue != null) dest.setWellReagent(wellReagentValue, plateIndex, wellIndex);
        Integer wellRowValue = src.getWellRow(plateIndex, wellIndex);
        if (wellRowValue != null) dest.setWellRow(wellRowValue, plateIndex, wellIndex);
        String wellTypeValue = src.getWellType(plateIndex, wellIndex);
        if (wellTypeValue != null) dest.setWellType(wellTypeValue, plateIndex, wellIndex);
        int wellSampleCount = src.getWellSampleCount(plateIndex, wellIndex);
        for (int wellSampleIndex=0; wellSampleIndex<wellSampleCount; wellSampleIndex++) {
          String wellSampleIDValue = src.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIDValue != null) dest.setWellSampleID(wellSampleIDValue, plateIndex, wellIndex, wellSampleIndex);
          String wellSampleImageRefValue = src.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleImageRefValue != null) dest.setWellSampleImageRef(wellSampleImageRefValue, plateIndex, wellIndex, wellSampleIndex);
          Integer wellSampleIndexValue = src.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIndexValue != null) dest.setWellSampleIndex(wellSampleIndexValue, plateIndex, wellIndex, wellSampleIndex);
          Float wellSamplePosXValue = src.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePosXValue != null) dest.setWellSamplePosX(wellSamplePosXValue, plateIndex, wellIndex, wellSampleIndex);
          Float wellSamplePosYValue = src.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePosYValue != null) dest.setWellSamplePosY(wellSamplePosYValue, plateIndex, wellIndex, wellSampleIndex);
          Integer wellSampleTimepointValue = src.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleTimepointValue != null) dest.setWellSampleTimepoint(wellSampleTimepointValue, plateIndex, wellIndex, wellSampleIndex);
        }
      }
    }
    int projectCount = src.getProjectCount();
    for (int projectIndex=0; projectIndex<projectCount; projectIndex++) {
    try {
      String projectIDValue = src.getProjectID(projectIndex);
      if (projectIDValue != null) dest.setProjectID(projectIDValue, projectIndex);
    } catch (NullPointerException e) { }
    try {
      int annotationRefCount = src.getProjectAnnotationRefCount(projectIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
        String projectAnnotationRefValue = src.getProjectAnnotationRef(projectIndex, annotationRefIndex);
        if (projectAnnotationRefValue != null) dest.setProjectAnnotationRef(projectAnnotationRefValue, projectIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String projectDescriptionValue = src.getProjectDescription(projectIndex);
      if (projectDescriptionValue != null) dest.setProjectDescription(projectDescriptionValue, projectIndex);
      String projectExperimenterRefValue = src.getProjectExperimenterRef(projectIndex);
      if (projectExperimenterRefValue != null) dest.setProjectExperimenterRef(projectExperimenterRefValue, projectIndex);
      String projectGroupRefValue = src.getProjectGroupRef(projectIndex);
      if (projectGroupRefValue != null) dest.setProjectGroupRef(projectGroupRefValue, projectIndex);
    } catch (NullPointerException e) { }
    try {
      String projectNameValue = src.getProjectName(projectIndex);
      if (projectNameValue != null) dest.setProjectName(projectNameValue, projectIndex);
    }
    int screenCount = src.getScreenCount();
    for (int screenIndex=0; screenIndex<screenCount; screenIndex++) {
      String screenDescriptionValue = src.getScreenDescription(screenIndex);
      if (screenDescriptionValue != null) dest.setScreenDescription(screenDescriptionValue, screenIndex);
      String screenExternValue = src.getScreenExtern(screenIndex);
      if (screenExternValue != null) dest.setScreenExtern(screenExternValue, screenIndex);
      String screenIDValue = src.getScreenID(screenIndex);
      if (screenIDValue != null) dest.setScreenID(screenIDValue, screenIndex);
      String screenNameValue = src.getScreenName(screenIndex);
      if (screenNameValue != null) dest.setScreenName(screenNameValue, screenIndex);
      String screenProtocolDescriptionValue = src.getScreenProtocolDescription(screenIndex);
      if (screenProtocolDescriptionValue != null) dest.setScreenProtocolDescription(screenProtocolDescriptionValue, screenIndex);
      String screenProtocolIdentifierValue = src.getScreenProtocolIdentifier(screenIndex);
      if (screenProtocolIdentifierValue != null) dest.setScreenProtocolIdentifier(screenProtocolIdentifierValue, screenIndex);
      String screenReagentSetDescriptionValue = src.getScreenReagentSetDescription(screenIndex);
      if (screenReagentSetDescriptionValue != null) dest.setScreenReagentSetDescription(screenReagentSetDescriptionValue, screenIndex);
      String screenReagentSetIdentifierValue = src.getScreenReagentSetIdentifier(screenIndex);
      if (screenReagentSetIdentifierValue != null) dest.setScreenReagentSetIdentifier(screenReagentSetIdentifierValue, screenIndex);
      String screenTypeValue = src.getScreenType(screenIndex);
      if (screenTypeValue != null) dest.setScreenType(screenTypeValue, screenIndex);
      int plateRefCount = src.getPlateRefCount(screenIndex);
      for (int plateRefIndex=0; plateRefIndex<plateRefCount; plateRefIndex++) {
        String plateRefIDValue = src.getPlateRefID(screenIndex, plateRefIndex);
        if (plateRefIDValue != null) dest.setPlateRefID(plateRefIDValue, screenIndex, plateRefIndex);
        Integer plateRefSampleValue = src.getPlateRefSample(screenIndex, plateRefIndex);
        if (plateRefSampleValue != null) dest.setPlateRefSample(plateRefSampleValue, screenIndex, plateRefIndex);
        String plateRefWellValue = src.getPlateRefWell(screenIndex, plateRefIndex);
        if (plateRefWellValue != null) dest.setPlateRefWell(plateRefWellValue, screenIndex, plateRefIndex);
      }
      int reagentCount = src.getReagentCount(screenIndex);
      for (int reagentIndex=0; reagentIndex<reagentCount; reagentIndex++) {
        String reagentDescriptionValue = src.getReagentDescription(screenIndex, reagentIndex);
        if (reagentDescriptionValue != null) dest.setReagentDescription(reagentDescriptionValue, screenIndex, reagentIndex);
        String reagentIDValue = src.getReagentID(screenIndex, reagentIndex);
        if (reagentIDValue != null) dest.setReagentID(reagentIDValue, screenIndex, reagentIndex);
        String reagentNameValue = src.getReagentName(screenIndex, reagentIndex);
        if (reagentNameValue != null) dest.setReagentName(reagentNameValue, screenIndex, reagentIndex);
        String reagentReagentIdentifierValue = src.getReagentReagentIdentifier(screenIndex, reagentIndex);
        if (reagentReagentIdentifierValue != null) dest.setReagentReagentIdentifier(reagentReagentIdentifierValue, screenIndex, reagentIndex);
      }
      int screenAcquisitionCount = src.getScreenAcquisitionCount(screenIndex);
      for (int screenAcquisitionIndex=0; screenAcquisitionIndex<screenAcquisitionCount; screenAcquisitionIndex++) {
        String screenAcquisitionEndTimeValue = src.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex);
        if (screenAcquisitionEndTimeValue != null) dest.setScreenAcquisitionEndTime(screenAcquisitionEndTimeValue, screenIndex, screenAcquisitionIndex);
        String screenAcquisitionIDValue = src.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex);
        if (screenAcquisitionIDValue != null) dest.setScreenAcquisitionID(screenAcquisitionIDValue, screenIndex, screenAcquisitionIndex);
        String screenAcquisitionStartTimeValue = src.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex);
        if (screenAcquisitionStartTimeValue != null) dest.setScreenAcquisitionStartTime(screenAcquisitionStartTimeValue, screenIndex, screenAcquisitionIndex);
        int wellSampleRefCount = src.getWellSampleRefCount(screenIndex, screenAcquisitionIndex);
        for (int wellSampleRefIndex=0; wellSampleRefIndex<wellSampleRefCount; wellSampleRefIndex++) {
          String wellSampleRefIDValue = src.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
          if (wellSampleRefIDValue != null) dest.setWellSampleRefID(wellSampleRefIDValue, screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
        }
      }
    }
  }

}
