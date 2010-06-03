//
// MetadataConverter.java
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
 * Created by melissa via MetadataAutogen on Jun 2, 2010 7:57:49 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

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
    int booleanAnnotationCount = src.getBooleanAnnotationCount();
    for (int booleanAnnotationIndex=0; booleanAnnotationIndex<booleanAnnotationCount; booleanAnnotationIndex++) {
      String booleanAnnotationIDValue = src.getBooleanAnnotationID(booleanAnnotationIndex);
      if (booleanAnnotationIDValue != null) dest.setBooleanAnnotationID(booleanAnnotationIDValue, booleanAnnotationIndex);
      String booleanAnnotationNamespaceValue = src.getBooleanAnnotationNamespace(booleanAnnotationIndex);
      if (booleanAnnotationNamespaceValue != null) dest.setBooleanAnnotationNamespace(booleanAnnotationNamespaceValue, booleanAnnotationIndex);
      Boolean booleanAnnotationValueValue = src.getBooleanAnnotationValue(booleanAnnotationIndex);
      if (booleanAnnotationValueValue != null) dest.setBooleanAnnotationValue(booleanAnnotationValueValue, booleanAnnotationIndex);
    }
    int datasetCount = src.getDatasetCount();
    for (int datasetIndex=0; datasetIndex<datasetCount; datasetIndex++) {
      //String datasetAnnotationRefValue = src.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
      //if (datasetAnnotationRefValue != null) dest.setDatasetAnnotationRef(datasetAnnotationRefValue, datasetIndex, annotationRefIndex);
      String datasetDescriptionValue = src.getDatasetDescription(datasetIndex);
      if (datasetDescriptionValue != null) dest.setDatasetDescription(datasetDescriptionValue, datasetIndex);
      String datasetExperimenterRefValue = src.getDatasetExperimenterRef(datasetIndex);
      if (datasetExperimenterRefValue != null) dest.setDatasetExperimenterRef(datasetExperimenterRefValue, datasetIndex);
      String datasetGroupRefValue = src.getDatasetGroupRef(datasetIndex);
      if (datasetGroupRefValue != null) dest.setDatasetGroupRef(datasetGroupRefValue, datasetIndex);
      String datasetIDValue = src.getDatasetID(datasetIndex);
      if (datasetIDValue != null) dest.setDatasetID(datasetIDValue, datasetIndex);
      String datasetNameValue = src.getDatasetName(datasetIndex);
      if (datasetNameValue != null) dest.setDatasetName(datasetNameValue, datasetIndex);
      //String datasetProjectRefValue = src.getDatasetProjectRef(datasetIndex, projectRefIndex);
      //if (datasetProjectRefValue != null) dest.setDatasetProjectRef(datasetProjectRefValue, datasetIndex, projectRefIndex);
      //int annotationRefCount = src.getAnnotationRefCount(datasetIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      int projectRefCount = src.getProjectRefCount(datasetIndex);
      for (int projectRefIndex=0; projectRefIndex<projectRefCount; projectRefIndex++) {
      }
    }
    int doubleAnnotationCount = src.getDoubleAnnotationCount();
    for (int doubleAnnotationIndex=0; doubleAnnotationIndex<doubleAnnotationCount; doubleAnnotationIndex++) {
      String doubleAnnotationIDValue = src.getDoubleAnnotationID(doubleAnnotationIndex);
      if (doubleAnnotationIDValue != null) dest.setDoubleAnnotationID(doubleAnnotationIDValue, doubleAnnotationIndex);
      String doubleAnnotationNamespaceValue = src.getDoubleAnnotationNamespace(doubleAnnotationIndex);
      if (doubleAnnotationNamespaceValue != null) dest.setDoubleAnnotationNamespace(doubleAnnotationNamespaceValue, doubleAnnotationIndex);
      Double doubleAnnotationValueValue = src.getDoubleAnnotationValue(doubleAnnotationIndex);
      if (doubleAnnotationValueValue != null) dest.setDoubleAnnotationValue(doubleAnnotationValueValue, doubleAnnotationIndex);
    }
    int experimentCount = src.getExperimentCount();
    for (int experimentIndex=0; experimentIndex<experimentCount; experimentIndex++) {
      String experimentDescriptionValue = src.getExperimentDescription(experimentIndex);
      if (experimentDescriptionValue != null) dest.setExperimentDescription(experimentDescriptionValue, experimentIndex);
      String experimentExperimenterRefValue = src.getExperimentExperimenterRef(experimentIndex);
      if (experimentExperimenterRefValue != null) dest.setExperimentExperimenterRef(experimentExperimenterRefValue, experimentIndex);
      String experimentIDValue = src.getExperimentID(experimentIndex);
      if (experimentIDValue != null) dest.setExperimentID(experimentIDValue, experimentIndex);
      ExperimentType experimentTypeValue = src.getExperimentType(experimentIndex);
      if (experimentTypeValue != null) dest.setExperimentType(experimentTypeValue, experimentIndex);
      int microbeamManipulationCount = src.getMicrobeamManipulationCount(experimentIndex);
      for (int microbeamManipulationIndex=0; microbeamManipulationIndex<microbeamManipulationCount; microbeamManipulationIndex++) {
        String microbeamManipulationExperimenterRefValue = src.getMicrobeamManipulationExperimenterRef(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationExperimenterRefValue != null) dest.setMicrobeamManipulationExperimenterRef(microbeamManipulationExperimenterRefValue, experimentIndex, microbeamManipulationIndex);
        String microbeamManipulationIDValue = src.getMicrobeamManipulationID(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationIDValue != null) dest.setMicrobeamManipulationID(microbeamManipulationIDValue, experimentIndex, microbeamManipulationIndex);
        //String microbeamManipulationROIRefValue = src.getMicrobeamManipulationROIRef(experimentIndex, microbeamManipulationIndex, roiRefIndex);
        //if (microbeamManipulationROIRefValue != null) dest.setMicrobeamManipulationROIRef(microbeamManipulationROIRefValue, experimentIndex, microbeamManipulationIndex, roiRefIndex);
        MicrobeamManipulationType microbeamManipulationTypeValue = src.getMicrobeamManipulationType(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationTypeValue != null) dest.setMicrobeamManipulationType(microbeamManipulationTypeValue, experimentIndex, microbeamManipulationIndex);
        /*
        int lightSourceSettingsCount = src.getLightSourceSettingsCount(experimentIndex, microbeamManipulationIndex);
        for (int lightSourceSettingsIndex=0; lightSourceSettingsIndex<lightSourceSettingsCount; lightSourceSettingsIndex++) {
          PercentFraction microbeamManipulationLightSourceSettingsAttenuationValue = src.getMicrobeamManipulationLightSourceSettingsAttenuation(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsAttenuationValue != null) dest.setMicrobeamManipulationLightSourceSettingsAttenuation(microbeamManipulationLightSourceSettingsAttenuationValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          String microbeamManipulationLightSourceSettingsIDValue = src.getMicrobeamManipulationLightSourceSettingsID(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsIDValue != null) dest.setMicrobeamManipulationLightSourceSettingsID(microbeamManipulationLightSourceSettingsIDValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          PositiveInteger microbeamManipulationLightSourceSettingsWavelengthValue = src.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsWavelengthValue != null) dest.setMicrobeamManipulationLightSourceSettingsWavelength(microbeamManipulationLightSourceSettingsWavelengthValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        }
        */
        //int roiRefCount = src.getROIRefCount(experimentIndex, microbeamManipulationIndex);
        //for (int roiRefIndex=0; roiRefIndex<roiRefCount; roiRefIndex++) {
        //}
      }
    }
    int experimenterCount = src.getExperimenterCount();
    for (int experimenterIndex=0; experimenterIndex<experimenterCount; experimenterIndex++) {
      //String experimenterAnnotationRefValue = src.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
      //if (experimenterAnnotationRefValue != null) dest.setExperimenterAnnotationRef(experimenterAnnotationRefValue, experimenterIndex, annotationRefIndex);
      String experimenterDisplayNameValue = src.getExperimenterDisplayName(experimenterIndex);
      if (experimenterDisplayNameValue != null) dest.setExperimenterDisplayName(experimenterDisplayNameValue, experimenterIndex);
      String experimenterEmailValue = src.getExperimenterEmail(experimenterIndex);
      if (experimenterEmailValue != null) dest.setExperimenterEmail(experimenterEmailValue, experimenterIndex);
      String experimenterFirstNameValue = src.getExperimenterFirstName(experimenterIndex);
      if (experimenterFirstNameValue != null) dest.setExperimenterFirstName(experimenterFirstNameValue, experimenterIndex);
      //String experimenterGroupRefValue = src.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
      //if (experimenterGroupRefValue != null) dest.setExperimenterGroupRef(experimenterGroupRefValue, experimenterIndex, groupRefIndex);
      String experimenterIDValue = src.getExperimenterID(experimenterIndex);
      if (experimenterIDValue != null) dest.setExperimenterID(experimenterIDValue, experimenterIndex);
      String experimenterInstitutionValue = src.getExperimenterInstitution(experimenterIndex);
      if (experimenterInstitutionValue != null) dest.setExperimenterInstitution(experimenterInstitutionValue, experimenterIndex);
      String experimenterLastNameValue = src.getExperimenterLastName(experimenterIndex);
      if (experimenterLastNameValue != null) dest.setExperimenterLastName(experimenterLastNameValue, experimenterIndex);
      String experimenterMiddleNameValue = src.getExperimenterMiddleName(experimenterIndex);
      if (experimenterMiddleNameValue != null) dest.setExperimenterMiddleName(experimenterMiddleNameValue, experimenterIndex);
      String experimenterUserNameValue = src.getExperimenterUserName(experimenterIndex);
      if (experimenterUserNameValue != null) dest.setExperimenterUserName(experimenterUserNameValue, experimenterIndex);
      //int annotationRefCount = src.getAnnotationRefCount(experimenterIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      //int groupRefCount = src.getGroupRefCount(experimenterIndex);
      //for (int groupRefIndex=0; groupRefIndex<groupRefCount; groupRefIndex++) {
      //}
    }
    int fileAnnotationCount = src.getFileAnnotationCount();
    for (int fileAnnotationIndex=0; fileAnnotationIndex<fileAnnotationCount; fileAnnotationIndex++) {
      String fileAnnotationBinaryFileFileNameValue = src.getFileAnnotationBinaryFileFileName(fileAnnotationIndex);
      if (fileAnnotationBinaryFileFileNameValue != null) dest.setFileAnnotationBinaryFileFileName(fileAnnotationBinaryFileFileNameValue, fileAnnotationIndex);
      String fileAnnotationBinaryFileMIMETypeValue = src.getFileAnnotationBinaryFileMIMEType(fileAnnotationIndex);
      if (fileAnnotationBinaryFileMIMETypeValue != null) dest.setFileAnnotationBinaryFileMIMEType(fileAnnotationBinaryFileMIMETypeValue, fileAnnotationIndex);
      Integer fileAnnotationBinaryFileSizeValue = src.getFileAnnotationBinaryFileSize(fileAnnotationIndex);
      if (fileAnnotationBinaryFileSizeValue != null) dest.setFileAnnotationBinaryFileSize(fileAnnotationBinaryFileSizeValue, fileAnnotationIndex);
      String fileAnnotationIDValue = src.getFileAnnotationID(fileAnnotationIndex);
      if (fileAnnotationIDValue != null) dest.setFileAnnotationID(fileAnnotationIDValue, fileAnnotationIndex);
      String fileAnnotationNamespaceValue = src.getFileAnnotationNamespace(fileAnnotationIndex);
      if (fileAnnotationNamespaceValue != null) dest.setFileAnnotationNamespace(fileAnnotationNamespaceValue, fileAnnotationIndex);
    }
    int groupCount = src.getGroupCount();
    for (int groupIndex=0; groupIndex<groupCount; groupIndex++) {
      String groupContactValue = src.getGroupContact(groupIndex);
      if (groupContactValue != null) dest.setGroupContact(groupContactValue, groupIndex);
      String groupDescriptionValue = src.getGroupDescription(groupIndex);
      if (groupDescriptionValue != null) dest.setGroupDescription(groupDescriptionValue, groupIndex);
      String groupIDValue = src.getGroupID(groupIndex);
      if (groupIDValue != null) dest.setGroupID(groupIDValue, groupIndex);
      String groupLeaderValue = src.getGroupLeader(groupIndex);
      if (groupLeaderValue != null) dest.setGroupLeader(groupLeaderValue, groupIndex);
      String groupNameValue = src.getGroupName(groupIndex);
      if (groupNameValue != null) dest.setGroupName(groupNameValue, groupIndex);
    }
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
      String imageAcquiredDateValue = src.getImageAcquiredDate(imageIndex);
      if (imageAcquiredDateValue != null) dest.setImageAcquiredDate(imageAcquiredDateValue, imageIndex);
      //String imageAnnotationRefValue = src.getImageAnnotationRef(imageIndex, annotationRefIndex);
      //if (imageAnnotationRefValue != null) dest.setImageAnnotationRef(imageAnnotationRefValue, imageIndex, annotationRefIndex);
      //String imageDatasetRefValue = src.getImageDatasetRef(imageIndex, datasetRefIndex);
      //if (imageDatasetRefValue != null) dest.setImageDatasetRef(imageDatasetRefValue, imageIndex, datasetRefIndex);
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
      //String imageMicrobeamManipulationRefValue = src.getImageMicrobeamManipulationRef(imageIndex, microbeamManipulationRefIndex);
      //if (imageMicrobeamManipulationRefValue != null) dest.setImageMicrobeamManipulationRef(imageMicrobeamManipulationRefValue, imageIndex, microbeamManipulationRefIndex);
      String imageNameValue = src.getImageName(imageIndex);
      if (imageNameValue != null) dest.setImageName(imageNameValue, imageIndex);
      Double imageObjectiveSettingsCorrectionCollarValue = src.getImageObjectiveSettingsCorrectionCollar(imageIndex);
      if (imageObjectiveSettingsCorrectionCollarValue != null) dest.setImageObjectiveSettingsCorrectionCollar(imageObjectiveSettingsCorrectionCollarValue, imageIndex);
      String imageObjectiveSettingsIDValue = src.getImageObjectiveSettingsID(imageIndex);
      if (imageObjectiveSettingsIDValue != null) dest.setImageObjectiveSettingsID(imageObjectiveSettingsIDValue, imageIndex);
      Medium imageObjectiveSettingsMediumValue = src.getImageObjectiveSettingsMedium(imageIndex);
      if (imageObjectiveSettingsMediumValue != null) dest.setImageObjectiveSettingsMedium(imageObjectiveSettingsMediumValue, imageIndex);
      Double imageObjectiveSettingsRefractiveIndexValue = src.getImageObjectiveSettingsRefractiveIndex(imageIndex);
      if (imageObjectiveSettingsRefractiveIndexValue != null) dest.setImageObjectiveSettingsRefractiveIndex(imageObjectiveSettingsRefractiveIndexValue, imageIndex);
      //String imageROIRefValue = src.getImageROIRef(imageIndex, roiRefIndex);
      //if (imageROIRefValue != null) dest.setImageROIRef(imageROIRefValue, imageIndex, roiRefIndex);
      //int annotationRefCount = src.getAnnotationRefCount(imageIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      int channelCount = src.getChannelCount(imageIndex);
      for (int channelIndex=0; channelIndex<channelCount; channelIndex++) {
        AcquisitionMode channelAcquisitionModeValue = src.getChannelAcquisitionMode(imageIndex, channelIndex);
        if (channelAcquisitionModeValue != null) dest.setChannelAcquisitionMode(channelAcquisitionModeValue, imageIndex, channelIndex);
        //String channelAnnotationRefValue = src.getChannelAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
        //if (channelAnnotationRefValue != null) dest.setChannelAnnotationRef(channelAnnotationRefValue, imageIndex, channelIndex, annotationRefIndex);
        Integer channelColorValue = src.getChannelColor(imageIndex, channelIndex);
        if (channelColorValue != null) dest.setChannelColor(channelColorValue, imageIndex, channelIndex);
        ContrastMethod channelContrastMethodValue = src.getChannelContrastMethod(imageIndex, channelIndex);
        if (channelContrastMethodValue != null) dest.setChannelContrastMethod(channelContrastMethodValue, imageIndex, channelIndex);
        PositiveInteger channelEmissionWavelengthValue = src.getChannelEmissionWavelength(imageIndex, channelIndex);
        if (channelEmissionWavelengthValue != null) dest.setChannelEmissionWavelength(channelEmissionWavelengthValue, imageIndex, channelIndex);
        PositiveInteger channelExcitationWavelengthValue = src.getChannelExcitationWavelength(imageIndex, channelIndex);
        if (channelExcitationWavelengthValue != null) dest.setChannelExcitationWavelength(channelExcitationWavelengthValue, imageIndex, channelIndex);
        String channelFilterSetRefValue = src.getChannelFilterSetRef(imageIndex, channelIndex);
        if (channelFilterSetRefValue != null) dest.setChannelFilterSetRef(channelFilterSetRefValue, imageIndex, channelIndex);
        String channelFluorValue = src.getChannelFluor(imageIndex, channelIndex);
        if (channelFluorValue != null) dest.setChannelFluor(channelFluorValue, imageIndex, channelIndex);
        String channelIDValue = src.getChannelID(imageIndex, channelIndex);
        if (channelIDValue != null) dest.setChannelID(channelIDValue, imageIndex, channelIndex);
        IlluminationType channelIlluminationTypeValue = src.getChannelIlluminationType(imageIndex, channelIndex);
        if (channelIlluminationTypeValue != null) dest.setChannelIlluminationType(channelIlluminationTypeValue, imageIndex, channelIndex);
        PercentFraction channelLightSourceSettingsAttenuationValue = src.getChannelLightSourceSettingsAttenuation(imageIndex, channelIndex);
        if (channelLightSourceSettingsAttenuationValue != null) dest.setChannelLightSourceSettingsAttenuation(channelLightSourceSettingsAttenuationValue, imageIndex, channelIndex);
        String channelLightSourceSettingsIDValue = src.getChannelLightSourceSettingsID(imageIndex, channelIndex);
        if (channelLightSourceSettingsIDValue != null) dest.setChannelLightSourceSettingsID(channelLightSourceSettingsIDValue, imageIndex, channelIndex);
        PositiveInteger channelLightSourceSettingsWavelengthValue = src.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
        if (channelLightSourceSettingsWavelengthValue != null) dest.setChannelLightSourceSettingsWavelength(channelLightSourceSettingsWavelengthValue, imageIndex, channelIndex);
        Double channelNDFilterValue = src.getChannelNDFilter(imageIndex, channelIndex);
        if (channelNDFilterValue != null) dest.setChannelNDFilter(channelNDFilterValue, imageIndex, channelIndex);
        String channelNameValue = src.getChannelName(imageIndex, channelIndex);
        if (channelNameValue != null) dest.setChannelName(channelNameValue, imageIndex, channelIndex);
        String channelOTFRefValue = src.getChannelOTFRef(imageIndex, channelIndex);
        if (channelOTFRefValue != null) dest.setChannelOTFRef(channelOTFRefValue, imageIndex, channelIndex);
        Double channelPinholeSizeValue = src.getChannelPinholeSize(imageIndex, channelIndex);
        if (channelPinholeSizeValue != null) dest.setChannelPinholeSize(channelPinholeSizeValue, imageIndex, channelIndex);
        Integer channelPockelCellSettingValue = src.getChannelPockelCellSetting(imageIndex, channelIndex);
        if (channelPockelCellSettingValue != null) dest.setChannelPockelCellSetting(channelPockelCellSettingValue, imageIndex, channelIndex);
        Integer channelSamplesPerPixelValue = src.getChannelSamplesPerPixel(imageIndex, channelIndex);
        if (channelSamplesPerPixelValue != null) dest.setChannelSamplesPerPixel(channelSamplesPerPixelValue, imageIndex, channelIndex);
        //int annotationRefCount = src.getAnnotationRefCount(imageIndex, channelIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
          Binning detectorSettingsBinningValue = src.getDetectorSettingsBinning(imageIndex, channelIndex);
          if (detectorSettingsBinningValue != null) dest.setDetectorSettingsBinning(detectorSettingsBinningValue, imageIndex, channelIndex);
          Double detectorSettingsGainValue = src.getDetectorSettingsGain(imageIndex, channelIndex);
          if (detectorSettingsGainValue != null) dest.setDetectorSettingsGain(detectorSettingsGainValue, imageIndex, channelIndex);
          String detectorSettingsIDValue = src.getDetectorSettingsID(imageIndex, channelIndex);
          if (detectorSettingsIDValue != null) dest.setDetectorSettingsID(detectorSettingsIDValue, imageIndex, channelIndex);
          Double detectorSettingsOffsetValue = src.getDetectorSettingsOffset(imageIndex, channelIndex);
          if (detectorSettingsOffsetValue != null) dest.setDetectorSettingsOffset(detectorSettingsOffsetValue, imageIndex, channelIndex);
          Double detectorSettingsReadOutRateValue = src.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
          if (detectorSettingsReadOutRateValue != null) dest.setDetectorSettingsReadOutRate(detectorSettingsReadOutRateValue, imageIndex, channelIndex);
          Double detectorSettingsVoltageValue = src.getDetectorSettingsVoltage(imageIndex, channelIndex);
          if (detectorSettingsVoltageValue != null) dest.setDetectorSettingsVoltage(detectorSettingsVoltageValue, imageIndex, channelIndex);
          String lightPathDichroicRefValue = src.getLightPathDichroicRef(imageIndex, channelIndex);
          if (lightPathDichroicRefValue != null) dest.setLightPathDichroicRef(lightPathDichroicRefValue, imageIndex, channelIndex);
          for (int emissionFilterRefIndex=0; emissionFilterRefIndex<src.getLightPathEmissionFilterRefCount(imageIndex, channelIndex); emissionFilterRefIndex++) { 
            String lightPathEmissionFilterRefValue = src.getLightPathEmissionFilterRef(imageIndex, channelIndex, emissionFilterRefIndex);
            if (lightPathEmissionFilterRefValue != null) dest.setLightPathEmissionFilterRef(lightPathEmissionFilterRefValue, imageIndex, channelIndex, emissionFilterRefIndex);
          }
          for (int excitationFilterRefIndex=0; excitationFilterRefIndex<src.getLightPathExcitationFilterRefCount(imageIndex, channelIndex); excitationFilterRefIndex++) {
            String lightPathExcitationFilterRefValue = src.getLightPathExcitationFilterRef(imageIndex, channelIndex, excitationFilterRefIndex);
            if (lightPathExcitationFilterRefValue != null) dest.setLightPathExcitationFilterRef(lightPathExcitationFilterRefValue, imageIndex, channelIndex, excitationFilterRefIndex);
          }
          /*
        int emissionFilterRefCount = src.getEmissionFilterRefCount(imageIndex, channelIndex);
        for (int emissionFilterRefIndex=0; emissionFilterRefIndex<emissionFilterRefCount; emissionFilterRefIndex++) {
        }
        int excitationFilterRefCount = src.getExcitationFilterRefCount(imageIndex, channelIndex);
        for (int excitationFilterRefIndex=0; excitationFilterRefIndex<excitationFilterRefCount; excitationFilterRefIndex++) {
        }
        */
      }
      int datasetRefCount = src.getDatasetRefCount(imageIndex);
      for (int datasetRefIndex=0; datasetRefIndex<datasetRefCount; datasetRefIndex++) {
      }
        Double imagingEnvironmentAirPressureValue = src.getImagingEnvironmentAirPressure(imageIndex);
        if (imagingEnvironmentAirPressureValue != null) dest.setImagingEnvironmentAirPressure(imagingEnvironmentAirPressureValue, imageIndex);
        PercentFraction imagingEnvironmentCO2PercentValue = src.getImagingEnvironmentCO2Percent(imageIndex);
        if (imagingEnvironmentCO2PercentValue != null) dest.setImagingEnvironmentCO2Percent(imagingEnvironmentCO2PercentValue, imageIndex);
        PercentFraction imagingEnvironmentHumidityValue = src.getImagingEnvironmentHumidity(imageIndex);
        if (imagingEnvironmentHumidityValue != null) dest.setImagingEnvironmentHumidity(imagingEnvironmentHumidityValue, imageIndex);
        Double imagingEnvironmentTemperatureValue = src.getImagingEnvironmentTemperature(imageIndex);
        if (imagingEnvironmentTemperatureValue != null) dest.setImagingEnvironmentTemperature(imagingEnvironmentTemperatureValue, imageIndex);
      int microbeamManipulationRefCount = src.getMicrobeamManipulationRefCount(imageIndex);
      for (int microbeamManipulationRefIndex=0; microbeamManipulationRefIndex<microbeamManipulationRefCount; microbeamManipulationRefIndex++) {
      }
        //String pixelsAnnotationRefValue = src.getPixelsAnnotationRef(imageIndex, annotationRefIndex);
        //if (pixelsAnnotationRefValue != null) dest.setPixelsAnnotationRef(pixelsAnnotationRefValue, imageIndex, annotationRefIndex);
        DimensionOrder pixelsDimensionOrderValue = src.getPixelsDimensionOrder(imageIndex);
        if (pixelsDimensionOrderValue != null) dest.setPixelsDimensionOrder(pixelsDimensionOrderValue, imageIndex);
        String pixelsIDValue = src.getPixelsID(imageIndex);
        if (pixelsIDValue != null) dest.setPixelsID(pixelsIDValue, imageIndex);
        Double pixelsPhysicalSizeXValue = src.getPixelsPhysicalSizeX(imageIndex);
        if (pixelsPhysicalSizeXValue != null) dest.setPixelsPhysicalSizeX(pixelsPhysicalSizeXValue, imageIndex);
        Double pixelsPhysicalSizeYValue = src.getPixelsPhysicalSizeY(imageIndex);
        if (pixelsPhysicalSizeYValue != null) dest.setPixelsPhysicalSizeY(pixelsPhysicalSizeYValue, imageIndex);
        Double pixelsPhysicalSizeZValue = src.getPixelsPhysicalSizeZ(imageIndex);
        if (pixelsPhysicalSizeZValue != null) dest.setPixelsPhysicalSizeZ(pixelsPhysicalSizeZValue, imageIndex);
        PositiveInteger pixelsSizeCValue = src.getPixelsSizeC(imageIndex);
        if (pixelsSizeCValue != null) dest.setPixelsSizeC(pixelsSizeCValue, imageIndex);
        PositiveInteger pixelsSizeTValue = src.getPixelsSizeT(imageIndex);
        if (pixelsSizeTValue != null) dest.setPixelsSizeT(pixelsSizeTValue, imageIndex);
        PositiveInteger pixelsSizeXValue = src.getPixelsSizeX(imageIndex);
        if (pixelsSizeXValue != null) dest.setPixelsSizeX(pixelsSizeXValue, imageIndex);
        PositiveInteger pixelsSizeYValue = src.getPixelsSizeY(imageIndex);
        if (pixelsSizeYValue != null) dest.setPixelsSizeY(pixelsSizeYValue, imageIndex);
        PositiveInteger pixelsSizeZValue = src.getPixelsSizeZ(imageIndex);
        if (pixelsSizeZValue != null) dest.setPixelsSizeZ(pixelsSizeZValue, imageIndex);
        Double pixelsTimeIncrementValue = src.getPixelsTimeIncrement(imageIndex);
        if (pixelsTimeIncrementValue != null) dest.setPixelsTimeIncrement(pixelsTimeIncrementValue, imageIndex);
        PixelType pixelsTypeValue = src.getPixelsType(imageIndex);
        if (pixelsTypeValue != null) dest.setPixelsType(pixelsTypeValue, imageIndex);
      //int annotationRefCount = src.getAnnotationRefCount(imageIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      int binDataCount = src.getPixelsBinDataCount(imageIndex);
      for (int binDataIndex=0; binDataIndex<binDataCount; binDataIndex++) {
        Boolean pixelsBinDataBigEndianValue = src.getPixelsBinDataBigEndian(imageIndex, binDataIndex);
        if (pixelsBinDataBigEndianValue != null) dest.setPixelsBinDataBigEndian(pixelsBinDataBigEndianValue, imageIndex, binDataIndex);
      }
      int planeCount = src.getPlaneCount(imageIndex);
      for (int planeIndex=0; planeIndex<planeCount; planeIndex++) {
        //String planeAnnotationRefValue = src.getPlaneAnnotationRef(imageIndex, planeIndex, annotationRefIndex);
        //if (planeAnnotationRefValue != null) dest.setPlaneAnnotationRef(planeAnnotationRefValue, imageIndex, planeIndex, annotationRefIndex);
        Double planeDeltaTValue = src.getPlaneDeltaT(imageIndex, planeIndex);
        if (planeDeltaTValue != null) dest.setPlaneDeltaT(planeDeltaTValue, imageIndex, planeIndex);
        Double planeExposureTimeValue = src.getPlaneExposureTime(imageIndex, planeIndex);
        if (planeExposureTimeValue != null) dest.setPlaneExposureTime(planeExposureTimeValue, imageIndex, planeIndex);
        String planeHashSHA1Value = src.getPlaneHashSHA1(imageIndex, planeIndex);
        if (planeHashSHA1Value != null) dest.setPlaneHashSHA1(planeHashSHA1Value, imageIndex, planeIndex);
        Double planePositionXValue = src.getPlanePositionX(imageIndex, planeIndex);
        if (planePositionXValue != null) dest.setPlanePositionX(planePositionXValue, imageIndex, planeIndex);
        Double planePositionYValue = src.getPlanePositionY(imageIndex, planeIndex);
        if (planePositionYValue != null) dest.setPlanePositionY(planePositionYValue, imageIndex, planeIndex);
        Double planePositionZValue = src.getPlanePositionZ(imageIndex, planeIndex);
        if (planePositionZValue != null) dest.setPlanePositionZ(planePositionZValue, imageIndex, planeIndex);
        Integer planeTheCValue = src.getPlaneTheC(imageIndex, planeIndex);
        if (planeTheCValue != null) dest.setPlaneTheC(planeTheCValue, imageIndex, planeIndex);
        Integer planeTheTValue = src.getPlaneTheT(imageIndex, planeIndex);
        if (planeTheTValue != null) dest.setPlaneTheT(planeTheTValue, imageIndex, planeIndex);
        Integer planeTheZValue = src.getPlaneTheZ(imageIndex, planeIndex);
        if (planeTheZValue != null) dest.setPlaneTheZ(planeTheZValue, imageIndex, planeIndex);
        //int annotationRefCount = src.getAnnotationRefCount(imageIndex, planeIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
      }
      int roiRefCount = src.getImageROIRefCount(imageIndex);
      for (int roiRefIndex=0; roiRefIndex<roiRefCount; roiRefIndex++) {
      }
        String stageLabelNameValue = src.getStageLabelName(imageIndex);
        if (stageLabelNameValue != null) dest.setStageLabelName(stageLabelNameValue, imageIndex);
        Double stageLabelXValue = src.getStageLabelX(imageIndex);
        if (stageLabelXValue != null) dest.setStageLabelX(stageLabelXValue, imageIndex);
        Double stageLabelYValue = src.getStageLabelY(imageIndex);
        if (stageLabelYValue != null) dest.setStageLabelY(stageLabelYValue, imageIndex);
        Double stageLabelZValue = src.getStageLabelZ(imageIndex);
        if (stageLabelZValue != null) dest.setStageLabelZ(stageLabelZValue, imageIndex);
      int tiffDataCount = src.getTiffDataCount(imageIndex);
      for (int tiffDataIndex=0; tiffDataIndex<tiffDataCount; tiffDataIndex++) {
        Integer tiffDataFirstCValue = src.getTiffDataFirstC(imageIndex, tiffDataIndex);
        if (tiffDataFirstCValue != null) dest.setTiffDataFirstC(tiffDataFirstCValue, imageIndex, tiffDataIndex);
        Integer tiffDataFirstTValue = src.getTiffDataFirstT(imageIndex, tiffDataIndex);
        if (tiffDataFirstTValue != null) dest.setTiffDataFirstT(tiffDataFirstTValue, imageIndex, tiffDataIndex);
        Integer tiffDataFirstZValue = src.getTiffDataFirstZ(imageIndex, tiffDataIndex);
        if (tiffDataFirstZValue != null) dest.setTiffDataFirstZ(tiffDataFirstZValue, imageIndex, tiffDataIndex);
        Integer tiffDataIFDValue = src.getTiffDataIFD(imageIndex, tiffDataIndex);
        if (tiffDataIFDValue != null) dest.setTiffDataIFD(tiffDataIFDValue, imageIndex, tiffDataIndex);
        Integer tiffDataPlaneCountValue = src.getTiffDataPlaneCount(imageIndex, tiffDataIndex);
        if (tiffDataPlaneCountValue != null) dest.setTiffDataPlaneCount(tiffDataPlaneCountValue, imageIndex, tiffDataIndex);
        String uuidFileNameValue = src.getUUIDFileName(imageIndex, tiffDataIndex);
        if (uuidFileNameValue != null) dest.setUUIDFileName(uuidFileNameValue, imageIndex, tiffDataIndex);
      }
    }
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
      String instrumentIDValue = src.getInstrumentID(instrumentIndex);
      if (instrumentIDValue != null) dest.setInstrumentID(instrumentIDValue, instrumentIndex);
      /*
      int arcCount = src.getArcCount(instrumentIndex);
      for (int arcIndex=0; arcIndex<arcCount; arcIndex++) {
        String arcIDValue = src.getArcID(instrumentIndex, arcIndex);
        if (arcIDValue != null) dest.setArcID(arcIDValue, instrumentIndex, arcIndex);
        String arcLotNumberValue = src.getArcLotNumber(instrumentIndex, arcIndex);
        if (arcLotNumberValue != null) dest.setArcLotNumber(arcLotNumberValue, instrumentIndex, arcIndex);
        String arcManufacturerValue = src.getArcManufacturer(instrumentIndex, arcIndex);
        if (arcManufacturerValue != null) dest.setArcManufacturer(arcManufacturerValue, instrumentIndex, arcIndex);
        String arcModelValue = src.getArcModel(instrumentIndex, arcIndex);
        if (arcModelValue != null) dest.setArcModel(arcModelValue, instrumentIndex, arcIndex);
        Double arcPowerValue = src.getArcPower(instrumentIndex, arcIndex);
        if (arcPowerValue != null) dest.setArcPower(arcPowerValue, instrumentIndex, arcIndex);
        String arcSerialNumberValue = src.getArcSerialNumber(instrumentIndex, arcIndex);
        if (arcSerialNumberValue != null) dest.setArcSerialNumber(arcSerialNumberValue, instrumentIndex, arcIndex);
        ArcType arcTypeValue = src.getArcType(instrumentIndex, arcIndex);
        if (arcTypeValue != null) dest.setArcType(arcTypeValue, instrumentIndex, arcIndex);
      }
      */
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
        Double detectorAmplificationGainValue = src.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
        if (detectorAmplificationGainValue != null) dest.setDetectorAmplificationGain(detectorAmplificationGainValue, instrumentIndex, detectorIndex);
        Double detectorGainValue = src.getDetectorGain(instrumentIndex, detectorIndex);
        if (detectorGainValue != null) dest.setDetectorGain(detectorGainValue, instrumentIndex, detectorIndex);
        String detectorIDValue = src.getDetectorID(instrumentIndex, detectorIndex);
        if (detectorIDValue != null) dest.setDetectorID(detectorIDValue, instrumentIndex, detectorIndex);
        String detectorLotNumberValue = src.getDetectorLotNumber(instrumentIndex, detectorIndex);
        if (detectorLotNumberValue != null) dest.setDetectorLotNumber(detectorLotNumberValue, instrumentIndex, detectorIndex);
        String detectorManufacturerValue = src.getDetectorManufacturer(instrumentIndex, detectorIndex);
        if (detectorManufacturerValue != null) dest.setDetectorManufacturer(detectorManufacturerValue, instrumentIndex, detectorIndex);
        String detectorModelValue = src.getDetectorModel(instrumentIndex, detectorIndex);
        if (detectorModelValue != null) dest.setDetectorModel(detectorModelValue, instrumentIndex, detectorIndex);
        Double detectorOffsetValue = src.getDetectorOffset(instrumentIndex, detectorIndex);
        if (detectorOffsetValue != null) dest.setDetectorOffset(detectorOffsetValue, instrumentIndex, detectorIndex);
        String detectorSerialNumberValue = src.getDetectorSerialNumber(instrumentIndex, detectorIndex);
        if (detectorSerialNumberValue != null) dest.setDetectorSerialNumber(detectorSerialNumberValue, instrumentIndex, detectorIndex);
        DetectorType detectorTypeValue = src.getDetectorType(instrumentIndex, detectorIndex);
        if (detectorTypeValue != null) dest.setDetectorType(detectorTypeValue, instrumentIndex, detectorIndex);
        Double detectorVoltageValue = src.getDetectorVoltage(instrumentIndex, detectorIndex);
        if (detectorVoltageValue != null) dest.setDetectorVoltage(detectorVoltageValue, instrumentIndex, detectorIndex);
        Double detectorZoomValue = src.getDetectorZoom(instrumentIndex, detectorIndex);
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
        String dichroicSerialNumberValue = src.getDichroicSerialNumber(instrumentIndex, dichroicIndex);
        if (dichroicSerialNumberValue != null) dest.setDichroicSerialNumber(dichroicSerialNumberValue, instrumentIndex, dichroicIndex);
      }
      /*
      int filamentCount = src.getFilamentCount(instrumentIndex);
      for (int filamentIndex=0; filamentIndex<filamentCount; filamentIndex++) {
        String filamentIDValue = src.getFilamentID(instrumentIndex, filamentIndex);
        if (filamentIDValue != null) dest.setFilamentID(filamentIDValue, instrumentIndex, filamentIndex);
        String filamentLotNumberValue = src.getFilamentLotNumber(instrumentIndex, filamentIndex);
        if (filamentLotNumberValue != null) dest.setFilamentLotNumber(filamentLotNumberValue, instrumentIndex, filamentIndex);
        String filamentManufacturerValue = src.getFilamentManufacturer(instrumentIndex, filamentIndex);
        if (filamentManufacturerValue != null) dest.setFilamentManufacturer(filamentManufacturerValue, instrumentIndex, filamentIndex);
        String filamentModelValue = src.getFilamentModel(instrumentIndex, filamentIndex);
        if (filamentModelValue != null) dest.setFilamentModel(filamentModelValue, instrumentIndex, filamentIndex);
        Double filamentPowerValue = src.getFilamentPower(instrumentIndex, filamentIndex);
        if (filamentPowerValue != null) dest.setFilamentPower(filamentPowerValue, instrumentIndex, filamentIndex);
        String filamentSerialNumberValue = src.getFilamentSerialNumber(instrumentIndex, filamentIndex);
        if (filamentSerialNumberValue != null) dest.setFilamentSerialNumber(filamentSerialNumberValue, instrumentIndex, filamentIndex);
        FilamentType filamentTypeValue = src.getFilamentType(instrumentIndex, filamentIndex);
        if (filamentTypeValue != null) dest.setFilamentType(filamentTypeValue, instrumentIndex, filamentIndex);
      }
      */
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
        String filterSerialNumberValue = src.getFilterSerialNumber(instrumentIndex, filterIndex);
        if (filterSerialNumberValue != null) dest.setFilterSerialNumber(filterSerialNumberValue, instrumentIndex, filterIndex);
        FilterType filterTypeValue = src.getFilterType(instrumentIndex, filterIndex);
        if (filterTypeValue != null) dest.setFilterType(filterTypeValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutInValue = src.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInValue != null) dest.setTransmittanceRangeCutIn(transmittanceRangeCutInValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutInToleranceValue = src.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInToleranceValue != null) dest.setTransmittanceRangeCutInTolerance(transmittanceRangeCutInToleranceValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutOutValue = src.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutValue != null) dest.setTransmittanceRangeCutOut(transmittanceRangeCutOutValue, instrumentIndex, filterIndex);
        Integer transmittanceRangeCutOutToleranceValue = src.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutToleranceValue != null) dest.setTransmittanceRangeCutOutTolerance(transmittanceRangeCutOutToleranceValue, instrumentIndex, filterIndex);
        PercentFraction transmittanceRangeTransmittanceValue = src.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
        if (transmittanceRangeTransmittanceValue != null) dest.setTransmittanceRangeTransmittance(transmittanceRangeTransmittanceValue, instrumentIndex, filterIndex);
      }
      int filterSetCount = src.getFilterSetCount(instrumentIndex);
      for (int filterSetIndex=0; filterSetIndex<filterSetCount; filterSetIndex++) {
        String filterSetDichroicRefValue = src.getFilterSetDichroicRef(instrumentIndex, filterSetIndex);
        if (filterSetDichroicRefValue != null) dest.setFilterSetDichroicRef(filterSetDichroicRefValue, instrumentIndex, filterSetIndex);
        //String filterSetEmissionFilterRefValue = src.getFilterSetEmissionFilterRef(instrumentIndex, filterSetIndex, emissionFilterRefIndex);
        //if (filterSetEmissionFilterRefValue != null) dest.setFilterSetEmissionFilterRef(filterSetEmissionFilterRefValue, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
        //String filterSetExcitationFilterRefValue = src.getFilterSetExcitationFilterRef(instrumentIndex, filterSetIndex, excitationFilterRefIndex);
        //if (filterSetExcitationFilterRefValue != null) dest.setFilterSetExcitationFilterRef(filterSetExcitationFilterRefValue, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
        String filterSetIDValue = src.getFilterSetID(instrumentIndex, filterSetIndex);
        if (filterSetIDValue != null) dest.setFilterSetID(filterSetIDValue, instrumentIndex, filterSetIndex);
        String filterSetLotNumberValue = src.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
        if (filterSetLotNumberValue != null) dest.setFilterSetLotNumber(filterSetLotNumberValue, instrumentIndex, filterSetIndex);
        String filterSetManufacturerValue = src.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
        if (filterSetManufacturerValue != null) dest.setFilterSetManufacturer(filterSetManufacturerValue, instrumentIndex, filterSetIndex);
        String filterSetModelValue = src.getFilterSetModel(instrumentIndex, filterSetIndex);
        if (filterSetModelValue != null) dest.setFilterSetModel(filterSetModelValue, instrumentIndex, filterSetIndex);
        String filterSetSerialNumberValue = src.getFilterSetSerialNumber(instrumentIndex, filterSetIndex);
        if (filterSetSerialNumberValue != null) dest.setFilterSetSerialNumber(filterSetSerialNumberValue, instrumentIndex, filterSetIndex);
        /*
        int emissionFilterRefCount = src.getEmissionFilterRefCount(instrumentIndex, filterSetIndex);
        for (int emissionFilterRefIndex=0; emissionFilterRefIndex<emissionFilterRefCount; emissionFilterRefIndex++) {
        }
        int excitationFilterRefCount = src.getExcitationFilterRefCount(instrumentIndex, filterSetIndex);
        for (int excitationFilterRefIndex=0; excitationFilterRefIndex<excitationFilterRefCount; excitationFilterRefIndex++) {
        }
        */
      }
      /*
      int laserCount = src.getLaserCount(instrumentIndex);
      for (int laserIndex=0; laserIndex<laserCount; laserIndex++) {
        PositiveInteger laserFrequencyMultiplicationValue = src.getLaserFrequencyMultiplication(instrumentIndex, laserIndex);
        if (laserFrequencyMultiplicationValue != null) dest.setLaserFrequencyMultiplication(laserFrequencyMultiplicationValue, instrumentIndex, laserIndex);
        String laserIDValue = src.getLaserID(instrumentIndex, laserIndex);
        if (laserIDValue != null) dest.setLaserID(laserIDValue, instrumentIndex, laserIndex);
        LaserMedium laserLaserMediumValue = src.getLaserLaserMedium(instrumentIndex, laserIndex);
        if (laserLaserMediumValue != null) dest.setLaserLaserMedium(laserLaserMediumValue, instrumentIndex, laserIndex);
        String laserLotNumberValue = src.getLaserLotNumber(instrumentIndex, laserIndex);
        if (laserLotNumberValue != null) dest.setLaserLotNumber(laserLotNumberValue, instrumentIndex, laserIndex);
        String laserManufacturerValue = src.getLaserManufacturer(instrumentIndex, laserIndex);
        if (laserManufacturerValue != null) dest.setLaserManufacturer(laserManufacturerValue, instrumentIndex, laserIndex);
        String laserModelValue = src.getLaserModel(instrumentIndex, laserIndex);
        if (laserModelValue != null) dest.setLaserModel(laserModelValue, instrumentIndex, laserIndex);
        Boolean laserPockelCellValue = src.getLaserPockelCell(instrumentIndex, laserIndex);
        if (laserPockelCellValue != null) dest.setLaserPockelCell(laserPockelCellValue, instrumentIndex, laserIndex);
        Double laserPowerValue = src.getLaserPower(instrumentIndex, laserIndex);
        if (laserPowerValue != null) dest.setLaserPower(laserPowerValue, instrumentIndex, laserIndex);
        Pulse laserPulseValue = src.getLaserPulse(instrumentIndex, laserIndex);
        if (laserPulseValue != null) dest.setLaserPulse(laserPulseValue, instrumentIndex, laserIndex);
        String laserPumpValue = src.getLaserPump(instrumentIndex, laserIndex);
        if (laserPumpValue != null) dest.setLaserPump(laserPumpValue, instrumentIndex, laserIndex);
        Double laserRepetitionRateValue = src.getLaserRepetitionRate(instrumentIndex, laserIndex);
        if (laserRepetitionRateValue != null) dest.setLaserRepetitionRate(laserRepetitionRateValue, instrumentIndex, laserIndex);
        String laserSerialNumberValue = src.getLaserSerialNumber(instrumentIndex, laserIndex);
        if (laserSerialNumberValue != null) dest.setLaserSerialNumber(laserSerialNumberValue, instrumentIndex, laserIndex);
        Boolean laserTuneableValue = src.getLaserTuneable(instrumentIndex, laserIndex);
        if (laserTuneableValue != null) dest.setLaserTuneable(laserTuneableValue, instrumentIndex, laserIndex);
        LaserType laserTypeValue = src.getLaserType(instrumentIndex, laserIndex);
        if (laserTypeValue != null) dest.setLaserType(laserTypeValue, instrumentIndex, laserIndex);
        PositiveInteger laserWavelengthValue = src.getLaserWavelength(instrumentIndex, laserIndex);
        if (laserWavelengthValue != null) dest.setLaserWavelength(laserWavelengthValue, instrumentIndex, laserIndex);
      }
      */
      /*
      int lightEmittingDiodeCount = src.getLightEmittingDiodeCount(instrumentIndex);
      for (int lightEmittingDiodeIndex=0; lightEmittingDiodeIndex<lightEmittingDiodeCount; lightEmittingDiodeIndex++) {
        String lightEmittingDiodeIDValue = src.getLightEmittingDiodeID(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodeIDValue != null) dest.setLightEmittingDiodeID(lightEmittingDiodeIDValue, instrumentIndex, lightEmittingDiodeIndex);
        String lightEmittingDiodeLotNumberValue = src.getLightEmittingDiodeLotNumber(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodeLotNumberValue != null) dest.setLightEmittingDiodeLotNumber(lightEmittingDiodeLotNumberValue, instrumentIndex, lightEmittingDiodeIndex);
        String lightEmittingDiodeManufacturerValue = src.getLightEmittingDiodeManufacturer(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodeManufacturerValue != null) dest.setLightEmittingDiodeManufacturer(lightEmittingDiodeManufacturerValue, instrumentIndex, lightEmittingDiodeIndex);
        String lightEmittingDiodeModelValue = src.getLightEmittingDiodeModel(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodeModelValue != null) dest.setLightEmittingDiodeModel(lightEmittingDiodeModelValue, instrumentIndex, lightEmittingDiodeIndex);
        Double lightEmittingDiodePowerValue = src.getLightEmittingDiodePower(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodePowerValue != null) dest.setLightEmittingDiodePower(lightEmittingDiodePowerValue, instrumentIndex, lightEmittingDiodeIndex);
        String lightEmittingDiodeSerialNumberValue = src.getLightEmittingDiodeSerialNumber(instrumentIndex, lightEmittingDiodeIndex);
        if (lightEmittingDiodeSerialNumberValue != null) dest.setLightEmittingDiodeSerialNumber(lightEmittingDiodeSerialNumberValue, instrumentIndex, lightEmittingDiodeIndex);
      }
      */
        String microscopeLotNumberValue = src.getMicroscopeLotNumber(instrumentIndex);
        if (microscopeLotNumberValue != null) dest.setMicroscopeLotNumber(microscopeLotNumberValue, instrumentIndex);
        String microscopeManufacturerValue = src.getMicroscopeManufacturer(instrumentIndex);
        if (microscopeManufacturerValue != null) dest.setMicroscopeManufacturer(microscopeManufacturerValue, instrumentIndex);
        String microscopeModelValue = src.getMicroscopeModel(instrumentIndex);
        if (microscopeModelValue != null) dest.setMicroscopeModel(microscopeModelValue, instrumentIndex);
        String microscopeSerialNumberValue = src.getMicroscopeSerialNumber(instrumentIndex);
        if (microscopeSerialNumberValue != null) dest.setMicroscopeSerialNumber(microscopeSerialNumberValue, instrumentIndex);
        MicroscopeType microscopeTypeValue = src.getMicroscopeType(instrumentIndex);
        if (microscopeTypeValue != null) dest.setMicroscopeType(microscopeTypeValue, instrumentIndex);
      int otfCount = src.getOTFCount(instrumentIndex);
      for (int otfIndex=0; otfIndex<otfCount; otfIndex++) {
        String otfBinaryFileFileNameValue = src.getOTFBinaryFileFileName(instrumentIndex, otfIndex);
        if (otfBinaryFileFileNameValue != null) dest.setOTFBinaryFileFileName(otfBinaryFileFileNameValue, instrumentIndex, otfIndex);
        String otfBinaryFileMIMETypeValue = src.getOTFBinaryFileMIMEType(instrumentIndex, otfIndex);
        if (otfBinaryFileMIMETypeValue != null) dest.setOTFBinaryFileMIMEType(otfBinaryFileMIMETypeValue, instrumentIndex, otfIndex);
        Integer otfBinaryFileSizeValue = src.getOTFBinaryFileSize(instrumentIndex, otfIndex);
        if (otfBinaryFileSizeValue != null) dest.setOTFBinaryFileSize(otfBinaryFileSizeValue, instrumentIndex, otfIndex);
        String otfFilterSetRefValue = src.getOTFFilterSetRef(instrumentIndex, otfIndex);
        if (otfFilterSetRefValue != null) dest.setOTFFilterSetRef(otfFilterSetRefValue, instrumentIndex, otfIndex);
        String otfidValue = src.getOTFID(instrumentIndex, otfIndex);
        if (otfidValue != null) dest.setOTFID(otfidValue, instrumentIndex, otfIndex);
        Double otfObjectiveSettingsCorrectionCollarValue = src.getOTFObjectiveSettingsCorrectionCollar(instrumentIndex, otfIndex);
        if (otfObjectiveSettingsCorrectionCollarValue != null) dest.setOTFObjectiveSettingsCorrectionCollar(otfObjectiveSettingsCorrectionCollarValue, instrumentIndex, otfIndex);
        String otfObjectiveSettingsIDValue = src.getOTFObjectiveSettingsID(instrumentIndex, otfIndex);
        if (otfObjectiveSettingsIDValue != null) dest.setOTFObjectiveSettingsID(otfObjectiveSettingsIDValue, instrumentIndex, otfIndex);
        Medium otfObjectiveSettingsMediumValue = src.getOTFObjectiveSettingsMedium(instrumentIndex, otfIndex);
        if (otfObjectiveSettingsMediumValue != null) dest.setOTFObjectiveSettingsMedium(otfObjectiveSettingsMediumValue, instrumentIndex, otfIndex);
        Double otfObjectiveSettingsRefractiveIndexValue = src.getOTFObjectiveSettingsRefractiveIndex(instrumentIndex, otfIndex);
        if (otfObjectiveSettingsRefractiveIndexValue != null) dest.setOTFObjectiveSettingsRefractiveIndex(otfObjectiveSettingsRefractiveIndexValue, instrumentIndex, otfIndex);
        Boolean otfOpticalAxisAveragedValue = src.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
        if (otfOpticalAxisAveragedValue != null) dest.setOTFOpticalAxisAveraged(otfOpticalAxisAveragedValue, instrumentIndex, otfIndex);
        PositiveInteger otfSizeXValue = src.getOTFSizeX(instrumentIndex, otfIndex);
        if (otfSizeXValue != null) dest.setOTFSizeX(otfSizeXValue, instrumentIndex, otfIndex);
        PositiveInteger otfSizeYValue = src.getOTFSizeY(instrumentIndex, otfIndex);
        if (otfSizeYValue != null) dest.setOTFSizeY(otfSizeYValue, instrumentIndex, otfIndex);
        PixelType otfTypeValue = src.getOTFType(instrumentIndex, otfIndex);
        if (otfTypeValue != null) dest.setOTFType(otfTypeValue, instrumentIndex, otfIndex);
      }
      int objectiveCount = src.getObjectiveCount(instrumentIndex);
      for (int objectiveIndex=0; objectiveIndex<objectiveCount; objectiveIndex++) {
        Double objectiveCalibratedMagnificationValue = src.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
        if (objectiveCalibratedMagnificationValue != null) dest.setObjectiveCalibratedMagnification(objectiveCalibratedMagnificationValue, instrumentIndex, objectiveIndex);
        Correction objectiveCorrectionValue = src.getObjectiveCorrection(instrumentIndex, objectiveIndex);
        if (objectiveCorrectionValue != null) dest.setObjectiveCorrection(objectiveCorrectionValue, instrumentIndex, objectiveIndex);
        String objectiveIDValue = src.getObjectiveID(instrumentIndex, objectiveIndex);
        if (objectiveIDValue != null) dest.setObjectiveID(objectiveIDValue, instrumentIndex, objectiveIndex);
        Immersion objectiveImmersionValue = src.getObjectiveImmersion(instrumentIndex, objectiveIndex);
        if (objectiveImmersionValue != null) dest.setObjectiveImmersion(objectiveImmersionValue, instrumentIndex, objectiveIndex);
        Boolean objectiveIrisValue = src.getObjectiveIris(instrumentIndex, objectiveIndex);
        if (objectiveIrisValue != null) dest.setObjectiveIris(objectiveIrisValue, instrumentIndex, objectiveIndex);
        Double objectiveLensNAValue = src.getObjectiveLensNA(instrumentIndex, objectiveIndex);
        if (objectiveLensNAValue != null) dest.setObjectiveLensNA(objectiveLensNAValue, instrumentIndex, objectiveIndex);
        String objectiveLotNumberValue = src.getObjectiveLotNumber(instrumentIndex, objectiveIndex);
        if (objectiveLotNumberValue != null) dest.setObjectiveLotNumber(objectiveLotNumberValue, instrumentIndex, objectiveIndex);
        String objectiveManufacturerValue = src.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
        if (objectiveManufacturerValue != null) dest.setObjectiveManufacturer(objectiveManufacturerValue, instrumentIndex, objectiveIndex);
        String objectiveModelValue = src.getObjectiveModel(instrumentIndex, objectiveIndex);
        if (objectiveModelValue != null) dest.setObjectiveModel(objectiveModelValue, instrumentIndex, objectiveIndex);
        Integer objectiveNominalMagnificationValue = src.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
        if (objectiveNominalMagnificationValue != null) dest.setObjectiveNominalMagnification(objectiveNominalMagnificationValue, instrumentIndex, objectiveIndex);
        String objectiveSerialNumberValue = src.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
        if (objectiveSerialNumberValue != null) dest.setObjectiveSerialNumber(objectiveSerialNumberValue, instrumentIndex, objectiveIndex);
        Double objectiveWorkingDistanceValue = src.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
        if (objectiveWorkingDistanceValue != null) dest.setObjectiveWorkingDistance(objectiveWorkingDistanceValue, instrumentIndex, objectiveIndex);
      }
    }
    int listAnnotationCount = src.getListAnnotationCount();
    for (int listAnnotationIndex=0; listAnnotationIndex<listAnnotationCount; listAnnotationIndex++) {
      //String listAnnotationAnnotationRefValue = src.getListAnnotationAnnotationRef(listAnnotationIndex, annotationRefIndex);
      //if (listAnnotationAnnotationRefValue != null) dest.setListAnnotationAnnotationRef(listAnnotationAnnotationRefValue, listAnnotationIndex, annotationRefIndex);
      String listAnnotationIDValue = src.getListAnnotationID(listAnnotationIndex);
      if (listAnnotationIDValue != null) dest.setListAnnotationID(listAnnotationIDValue, listAnnotationIndex);
      String listAnnotationNamespaceValue = src.getListAnnotationNamespace(listAnnotationIndex);
      if (listAnnotationNamespaceValue != null) dest.setListAnnotationNamespace(listAnnotationNamespaceValue, listAnnotationIndex);
      //int annotationRefCount = src.getAnnotationRefCount(listAnnotationIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
    }
    int longAnnotationCount = src.getLongAnnotationCount();
    for (int longAnnotationIndex=0; longAnnotationIndex<longAnnotationCount; longAnnotationIndex++) {
      String longAnnotationIDValue = src.getLongAnnotationID(longAnnotationIndex);
      if (longAnnotationIDValue != null) dest.setLongAnnotationID(longAnnotationIDValue, longAnnotationIndex);
      String longAnnotationNamespaceValue = src.getLongAnnotationNamespace(longAnnotationIndex);
      if (longAnnotationNamespaceValue != null) dest.setLongAnnotationNamespace(longAnnotationNamespaceValue, longAnnotationIndex);
      Long longAnnotationValueValue = src.getLongAnnotationValue(longAnnotationIndex);
      if (longAnnotationValueValue != null) dest.setLongAnnotationValue(longAnnotationValueValue, longAnnotationIndex);
    }
    int plateCount = src.getPlateCount();
    for (int plateIndex=0; plateIndex<plateCount; plateIndex++) {
      //String plateAnnotationRefValue = src.getPlateAnnotationRef(plateIndex, annotationRefIndex);
      //if (plateAnnotationRefValue != null) dest.setPlateAnnotationRef(plateAnnotationRefValue, plateIndex, annotationRefIndex);
      NamingConvention plateColumnNamingConventionValue = src.getPlateColumnNamingConvention(plateIndex);
      if (plateColumnNamingConventionValue != null) dest.setPlateColumnNamingConvention(plateColumnNamingConventionValue, plateIndex);
      Integer plateColumnsValue = src.getPlateColumns(plateIndex);
      if (plateColumnsValue != null) dest.setPlateColumns(plateColumnsValue, plateIndex);
      String plateDescriptionValue = src.getPlateDescription(plateIndex);
      if (plateDescriptionValue != null) dest.setPlateDescription(plateDescriptionValue, plateIndex);
      String plateExternalIdentifierValue = src.getPlateExternalIdentifier(plateIndex);
      if (plateExternalIdentifierValue != null) dest.setPlateExternalIdentifier(plateExternalIdentifierValue, plateIndex);
      String plateIDValue = src.getPlateID(plateIndex);
      if (plateIDValue != null) dest.setPlateID(plateIDValue, plateIndex);
      String plateNameValue = src.getPlateName(plateIndex);
      if (plateNameValue != null) dest.setPlateName(plateNameValue, plateIndex);
      NamingConvention plateRowNamingConventionValue = src.getPlateRowNamingConvention(plateIndex);
      if (plateRowNamingConventionValue != null) dest.setPlateRowNamingConvention(plateRowNamingConventionValue, plateIndex);
      Integer plateRowsValue = src.getPlateRows(plateIndex);
      if (plateRowsValue != null) dest.setPlateRows(plateRowsValue, plateIndex);
      //String plateScreenRefValue = src.getPlateScreenRef(plateIndex, screenRefIndex);
      //if (plateScreenRefValue != null) dest.setPlateScreenRef(plateScreenRefValue, plateIndex, screenRefIndex);
      String plateStatusValue = src.getPlateStatus(plateIndex);
      if (plateStatusValue != null) dest.setPlateStatus(plateStatusValue, plateIndex);
      Double plateWellOriginXValue = src.getPlateWellOriginX(plateIndex);
      if (plateWellOriginXValue != null) dest.setPlateWellOriginX(plateWellOriginXValue, plateIndex);
      Double plateWellOriginYValue = src.getPlateWellOriginY(plateIndex);
      if (plateWellOriginYValue != null) dest.setPlateWellOriginY(plateWellOriginYValue, plateIndex);
      //int annotationRefCount = src.getAnnotationRefCount(plateIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      int plateAcquisitionCount = src.getPlateAcquisitionCount(plateIndex);
      for (int plateAcquisitionIndex=0; plateAcquisitionIndex<plateAcquisitionCount; plateAcquisitionIndex++) {
        //String plateAcquisitionAnnotationRefValue = src.getPlateAcquisitionAnnotationRef(plateIndex, plateAcquisitionIndex, annotationRefIndex);
        //if (plateAcquisitionAnnotationRefValue != null) dest.setPlateAcquisitionAnnotationRef(plateAcquisitionAnnotationRefValue, plateIndex, plateAcquisitionIndex, annotationRefIndex);
        String plateAcquisitionDescriptionValue = src.getPlateAcquisitionDescription(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionDescriptionValue != null) dest.setPlateAcquisitionDescription(plateAcquisitionDescriptionValue, plateIndex, plateAcquisitionIndex);
        String plateAcquisitionEndTimeValue = src.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionEndTimeValue != null) dest.setPlateAcquisitionEndTime(plateAcquisitionEndTimeValue, plateIndex, plateAcquisitionIndex);
        String plateAcquisitionIDValue = src.getPlateAcquisitionID(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionIDValue != null) dest.setPlateAcquisitionID(plateAcquisitionIDValue, plateIndex, plateAcquisitionIndex);
        Integer plateAcquisitionMaximumFieldCountValue = src.getPlateAcquisitionMaximumFieldCount(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionMaximumFieldCountValue != null) dest.setPlateAcquisitionMaximumFieldCount(plateAcquisitionMaximumFieldCountValue, plateIndex, plateAcquisitionIndex);
        String plateAcquisitionNameValue = src.getPlateAcquisitionName(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionNameValue != null) dest.setPlateAcquisitionName(plateAcquisitionNameValue, plateIndex, plateAcquisitionIndex);
        String plateAcquisitionStartTimeValue = src.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionStartTimeValue != null) dest.setPlateAcquisitionStartTime(plateAcquisitionStartTimeValue, plateIndex, plateAcquisitionIndex);
        //String plateAcquisitionWellSampleRefValue = src.getPlateAcquisitionWellSampleRef(plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
        //if (plateAcquisitionWellSampleRefValue != null) dest.setPlateAcquisitionWellSampleRef(plateAcquisitionWellSampleRefValue, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
        //int annotationRefCount = src.getAnnotationRefCount(plateIndex, plateAcquisitionIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
        int wellSampleRefCount = src.getWellSampleRefCount(plateIndex, plateAcquisitionIndex);
        for (int wellSampleRefIndex=0; wellSampleRefIndex<wellSampleRefCount; wellSampleRefIndex++) {
        }
      }
      int screenRefCount = src.getScreenRefCount(plateIndex);
      for (int screenRefIndex=0; screenRefIndex<screenRefCount; screenRefIndex++) {
      }
      int wellCount = src.getWellCount(plateIndex);
      for (int wellIndex=0; wellIndex<wellCount; wellIndex++) {
        //String wellAnnotationRefValue = src.getWellAnnotationRef(plateIndex, wellIndex, annotationRefIndex);
        //if (wellAnnotationRefValue != null) dest.setWellAnnotationRef(wellAnnotationRefValue, plateIndex, wellIndex, annotationRefIndex);
        Integer wellColorValue = src.getWellColor(plateIndex, wellIndex);
        if (wellColorValue != null) dest.setWellColor(wellColorValue, plateIndex, wellIndex);
        NonNegativeInteger wellColumnValue = src.getWellColumn(plateIndex, wellIndex);
        if (wellColumnValue != null) dest.setWellColumn(wellColumnValue, plateIndex, wellIndex);
        String wellExternalDescriptionValue = src.getWellExternalDescription(plateIndex, wellIndex);
        if (wellExternalDescriptionValue != null) dest.setWellExternalDescription(wellExternalDescriptionValue, plateIndex, wellIndex);
        String wellExternalIdentifierValue = src.getWellExternalIdentifier(plateIndex, wellIndex);
        if (wellExternalIdentifierValue != null) dest.setWellExternalIdentifier(wellExternalIdentifierValue, plateIndex, wellIndex);
        String wellIDValue = src.getWellID(plateIndex, wellIndex);
        if (wellIDValue != null) dest.setWellID(wellIDValue, plateIndex, wellIndex);
        String wellReagentRefValue = src.getWellReagentRef(plateIndex, wellIndex);
        if (wellReagentRefValue != null) dest.setWellReagentRef(wellReagentRefValue, plateIndex, wellIndex);
        NonNegativeInteger wellRowValue = src.getWellRow(plateIndex, wellIndex);
        if (wellRowValue != null) dest.setWellRow(wellRowValue, plateIndex, wellIndex);
        String wellStatusValue = src.getWellStatus(plateIndex, wellIndex);
        if (wellStatusValue != null) dest.setWellStatus(wellStatusValue, plateIndex, wellIndex);
        //int annotationRefCount = src.getAnnotationRefCount(plateIndex, wellIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
        int wellSampleCount = src.getWellSampleCount(plateIndex, wellIndex);
        for (int wellSampleIndex=0; wellSampleIndex<wellSampleCount; wellSampleIndex++) {
          //String wellSampleAnnotationRefValue = src.getWellSampleAnnotationRef(plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
          //if (wellSampleAnnotationRefValue != null) dest.setWellSampleAnnotationRef(wellSampleAnnotationRefValue, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
          String wellSampleIDValue = src.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIDValue != null) dest.setWellSampleID(wellSampleIDValue, plateIndex, wellIndex, wellSampleIndex);
          String wellSampleImageRefValue = src.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleImageRefValue != null) dest.setWellSampleImageRef(wellSampleImageRefValue, plateIndex, wellIndex, wellSampleIndex);
          NonNegativeInteger wellSampleIndexValue = src.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIndexValue != null) dest.setWellSampleIndex(wellSampleIndexValue, plateIndex, wellIndex, wellSampleIndex);
          Double wellSamplePositionXValue = src.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePositionXValue != null) dest.setWellSamplePositionX(wellSamplePositionXValue, plateIndex, wellIndex, wellSampleIndex);
          Double wellSamplePositionYValue = src.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePositionYValue != null) dest.setWellSamplePositionY(wellSamplePositionYValue, plateIndex, wellIndex, wellSampleIndex);
          Integer wellSampleTimepointValue = src.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleTimepointValue != null) dest.setWellSampleTimepoint(wellSampleTimepointValue, plateIndex, wellIndex, wellSampleIndex);
          //int annotationRefCount = src.getAnnotationRefCount(plateIndex, wellIndex, wellSampleIndex);
          //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
          //}
        }
      }
    }
    int projectCount = src.getProjectCount();
    for (int projectIndex=0; projectIndex<projectCount; projectIndex++) {
      //String projectAnnotationRefValue = src.getProjectAnnotationRef(projectIndex, annotationRefIndex);
      //if (projectAnnotationRefValue != null) dest.setProjectAnnotationRef(projectAnnotationRefValue, projectIndex, annotationRefIndex);
      String projectDescriptionValue = src.getProjectDescription(projectIndex);
      if (projectDescriptionValue != null) dest.setProjectDescription(projectDescriptionValue, projectIndex);
      String projectExperimenterRefValue = src.getProjectExperimenterRef(projectIndex);
      if (projectExperimenterRefValue != null) dest.setProjectExperimenterRef(projectExperimenterRefValue, projectIndex);
      String projectGroupRefValue = src.getProjectGroupRef(projectIndex);
      if (projectGroupRefValue != null) dest.setProjectGroupRef(projectGroupRefValue, projectIndex);
      String projectIDValue = src.getProjectID(projectIndex);
      if (projectIDValue != null) dest.setProjectID(projectIDValue, projectIndex);
      String projectNameValue = src.getProjectName(projectIndex);
      if (projectNameValue != null) dest.setProjectName(projectNameValue, projectIndex);
      //int annotationRefCount = src.getAnnotationRefCount(projectIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
    }
    int roiCount = src.getROICount();
    for (int roiIndex=0; roiIndex<roiCount; roiIndex++) {
      //String roiAnnotationRefValue = src.getROIAnnotationRef(roiIndex, annotationRefIndex);
      //if (roiAnnotationRefValue != null) dest.setROIAnnotationRef(roiAnnotationRefValue, roiIndex, annotationRefIndex);
      String roiDescriptionValue = src.getROIDescription(roiIndex);
      if (roiDescriptionValue != null) dest.setROIDescription(roiDescriptionValue, roiIndex);
      String roiidValue = src.getROIID(roiIndex);
      if (roiidValue != null) dest.setROIID(roiidValue, roiIndex);
      String roiNameValue = src.getROIName(roiIndex);
      if (roiNameValue != null) dest.setROIName(roiNameValue, roiIndex);
      String roiNamespaceValue = src.getROINamespace(roiIndex);
      if (roiNamespaceValue != null) dest.setROINamespace(roiNamespaceValue, roiIndex);
      //int annotationRefCount = src.getAnnotationRefCount(roiIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      //int shapeCount = src.getShapeCount(roiIndex, shapeIndex);
      /*
      for (int shapeIndex=0; shapeIndex<shapeCount; shapeIndex++) {
        //int annotationRefCount = src.getAnnotationRefCount(roiIndex, shapeIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
          String ellipseDescriptionValue = src.getEllipseDescription(roiIndex, shapeIndex);
          if (ellipseDescriptionValue != null) dest.setEllipseDescription(ellipseDescriptionValue, roiIndex, shapeIndex);
          Integer ellipseFillValue = src.getEllipseFill(roiIndex, shapeIndex);
          if (ellipseFillValue != null) dest.setEllipseFill(ellipseFillValue, roiIndex, shapeIndex);
          Integer ellipseFontSizeValue = src.getEllipseFontSize(roiIndex, shapeIndex);
          if (ellipseFontSizeValue != null) dest.setEllipseFontSize(ellipseFontSizeValue, roiIndex, shapeIndex);
          String ellipseIDValue = src.getEllipseID(roiIndex, shapeIndex);
          if (ellipseIDValue != null) dest.setEllipseID(ellipseIDValue, roiIndex, shapeIndex);
          String ellipseLabelValue = src.getEllipseLabel(roiIndex, shapeIndex);
          if (ellipseLabelValue != null) dest.setEllipseLabel(ellipseLabelValue, roiIndex, shapeIndex);
          String ellipseNameValue = src.getEllipseName(roiIndex, shapeIndex);
          if (ellipseNameValue != null) dest.setEllipseName(ellipseNameValue, roiIndex, shapeIndex);
          Double ellipseRadiusXValue = src.getEllipseRadiusX(roiIndex, shapeIndex);
          if (ellipseRadiusXValue != null) dest.setEllipseRadiusX(ellipseRadiusXValue, roiIndex, shapeIndex);
          Double ellipseRadiusYValue = src.getEllipseRadiusY(roiIndex, shapeIndex);
          if (ellipseRadiusYValue != null) dest.setEllipseRadiusY(ellipseRadiusYValue, roiIndex, shapeIndex);
          Integer ellipseStrokeValue = src.getEllipseStroke(roiIndex, shapeIndex);
          if (ellipseStrokeValue != null) dest.setEllipseStroke(ellipseStrokeValue, roiIndex, shapeIndex);
          String ellipseStrokeDashArrayValue = src.getEllipseStrokeDashArray(roiIndex, shapeIndex);
          if (ellipseStrokeDashArrayValue != null) dest.setEllipseStrokeDashArray(ellipseStrokeDashArrayValue, roiIndex, shapeIndex);
          Double ellipseStrokeWidthValue = src.getEllipseStrokeWidth(roiIndex, shapeIndex);
          if (ellipseStrokeWidthValue != null) dest.setEllipseStrokeWidth(ellipseStrokeWidthValue, roiIndex, shapeIndex);
          Integer ellipseTheCValue = src.getEllipseTheC(roiIndex, shapeIndex);
          if (ellipseTheCValue != null) dest.setEllipseTheC(ellipseTheCValue, roiIndex, shapeIndex);
          Integer ellipseTheTValue = src.getEllipseTheT(roiIndex, shapeIndex);
          if (ellipseTheTValue != null) dest.setEllipseTheT(ellipseTheTValue, roiIndex, shapeIndex);
          Integer ellipseTheZValue = src.getEllipseTheZ(roiIndex, shapeIndex);
          if (ellipseTheZValue != null) dest.setEllipseTheZ(ellipseTheZValue, roiIndex, shapeIndex);
          String ellipseTransformValue = src.getEllipseTransform(roiIndex, shapeIndex);
          if (ellipseTransformValue != null) dest.setEllipseTransform(ellipseTransformValue, roiIndex, shapeIndex);
          Double ellipseXValue = src.getEllipseX(roiIndex, shapeIndex);
          if (ellipseXValue != null) dest.setEllipseX(ellipseXValue, roiIndex, shapeIndex);
          Double ellipseYValue = src.getEllipseY(roiIndex, shapeIndex);
          if (ellipseYValue != null) dest.setEllipseY(ellipseYValue, roiIndex, shapeIndex);
          String lineDescriptionValue = src.getLineDescription(roiIndex, shapeIndex);
          if (lineDescriptionValue != null) dest.setLineDescription(lineDescriptionValue, roiIndex, shapeIndex);
          Integer lineFillValue = src.getLineFill(roiIndex, shapeIndex);
          if (lineFillValue != null) dest.setLineFill(lineFillValue, roiIndex, shapeIndex);
          Integer lineFontSizeValue = src.getLineFontSize(roiIndex, shapeIndex);
          if (lineFontSizeValue != null) dest.setLineFontSize(lineFontSizeValue, roiIndex, shapeIndex);
          String lineIDValue = src.getLineID(roiIndex, shapeIndex);
          if (lineIDValue != null) dest.setLineID(lineIDValue, roiIndex, shapeIndex);
          String lineLabelValue = src.getLineLabel(roiIndex, shapeIndex);
          if (lineLabelValue != null) dest.setLineLabel(lineLabelValue, roiIndex, shapeIndex);
          String lineNameValue = src.getLineName(roiIndex, shapeIndex);
          if (lineNameValue != null) dest.setLineName(lineNameValue, roiIndex, shapeIndex);
          Integer lineStrokeValue = src.getLineStroke(roiIndex, shapeIndex);
          if (lineStrokeValue != null) dest.setLineStroke(lineStrokeValue, roiIndex, shapeIndex);
          String lineStrokeDashArrayValue = src.getLineStrokeDashArray(roiIndex, shapeIndex);
          if (lineStrokeDashArrayValue != null) dest.setLineStrokeDashArray(lineStrokeDashArrayValue, roiIndex, shapeIndex);
          Double lineStrokeWidthValue = src.getLineStrokeWidth(roiIndex, shapeIndex);
          if (lineStrokeWidthValue != null) dest.setLineStrokeWidth(lineStrokeWidthValue, roiIndex, shapeIndex);
          Integer lineTheCValue = src.getLineTheC(roiIndex, shapeIndex);
          if (lineTheCValue != null) dest.setLineTheC(lineTheCValue, roiIndex, shapeIndex);
          Integer lineTheTValue = src.getLineTheT(roiIndex, shapeIndex);
          if (lineTheTValue != null) dest.setLineTheT(lineTheTValue, roiIndex, shapeIndex);
          Integer lineTheZValue = src.getLineTheZ(roiIndex, shapeIndex);
          if (lineTheZValue != null) dest.setLineTheZ(lineTheZValue, roiIndex, shapeIndex);
          String lineTransformValue = src.getLineTransform(roiIndex, shapeIndex);
          if (lineTransformValue != null) dest.setLineTransform(lineTransformValue, roiIndex, shapeIndex);
          Double lineX1Value = src.getLineX1(roiIndex, shapeIndex);
          if (lineX1Value != null) dest.setLineX1(lineX1Value, roiIndex, shapeIndex);
          Double lineX2Value = src.getLineX2(roiIndex, shapeIndex);
          if (lineX2Value != null) dest.setLineX2(lineX2Value, roiIndex, shapeIndex);
          Double lineY1Value = src.getLineY1(roiIndex, shapeIndex);
          if (lineY1Value != null) dest.setLineY1(lineY1Value, roiIndex, shapeIndex);
          Double lineY2Value = src.getLineY2(roiIndex, shapeIndex);
          if (lineY2Value != null) dest.setLineY2(lineY2Value, roiIndex, shapeIndex);
          String maskDescriptionValue = src.getMaskDescription(roiIndex, shapeIndex);
          if (maskDescriptionValue != null) dest.setMaskDescription(maskDescriptionValue, roiIndex, shapeIndex);
          Integer maskFillValue = src.getMaskFill(roiIndex, shapeIndex);
          if (maskFillValue != null) dest.setMaskFill(maskFillValue, roiIndex, shapeIndex);
          Integer maskFontSizeValue = src.getMaskFontSize(roiIndex, shapeIndex);
          if (maskFontSizeValue != null) dest.setMaskFontSize(maskFontSizeValue, roiIndex, shapeIndex);
          String maskIDValue = src.getMaskID(roiIndex, shapeIndex);
          if (maskIDValue != null) dest.setMaskID(maskIDValue, roiIndex, shapeIndex);
          String maskLabelValue = src.getMaskLabel(roiIndex, shapeIndex);
          if (maskLabelValue != null) dest.setMaskLabel(maskLabelValue, roiIndex, shapeIndex);
          String maskNameValue = src.getMaskName(roiIndex, shapeIndex);
          if (maskNameValue != null) dest.setMaskName(maskNameValue, roiIndex, shapeIndex);
          Integer maskStrokeValue = src.getMaskStroke(roiIndex, shapeIndex);
          if (maskStrokeValue != null) dest.setMaskStroke(maskStrokeValue, roiIndex, shapeIndex);
          String maskStrokeDashArrayValue = src.getMaskStrokeDashArray(roiIndex, shapeIndex);
          if (maskStrokeDashArrayValue != null) dest.setMaskStrokeDashArray(maskStrokeDashArrayValue, roiIndex, shapeIndex);
          Double maskStrokeWidthValue = src.getMaskStrokeWidth(roiIndex, shapeIndex);
          if (maskStrokeWidthValue != null) dest.setMaskStrokeWidth(maskStrokeWidthValue, roiIndex, shapeIndex);
          Integer maskTheCValue = src.getMaskTheC(roiIndex, shapeIndex);
          if (maskTheCValue != null) dest.setMaskTheC(maskTheCValue, roiIndex, shapeIndex);
          Integer maskTheTValue = src.getMaskTheT(roiIndex, shapeIndex);
          if (maskTheTValue != null) dest.setMaskTheT(maskTheTValue, roiIndex, shapeIndex);
          Integer maskTheZValue = src.getMaskTheZ(roiIndex, shapeIndex);
          if (maskTheZValue != null) dest.setMaskTheZ(maskTheZValue, roiIndex, shapeIndex);
          String maskTransformValue = src.getMaskTransform(roiIndex, shapeIndex);
          if (maskTransformValue != null) dest.setMaskTransform(maskTransformValue, roiIndex, shapeIndex);
          Double maskXValue = src.getMaskX(roiIndex, shapeIndex);
          if (maskXValue != null) dest.setMaskX(maskXValue, roiIndex, shapeIndex);
          Double maskYValue = src.getMaskY(roiIndex, shapeIndex);
          if (maskYValue != null) dest.setMaskY(maskYValue, roiIndex, shapeIndex);
          String pathDefinitionValue = src.getPathDefinition(roiIndex, shapeIndex);
          if (pathDefinitionValue != null) dest.setPathDefinition(pathDefinitionValue, roiIndex, shapeIndex);
          String pathDescriptionValue = src.getPathDescription(roiIndex, shapeIndex);
          if (pathDescriptionValue != null) dest.setPathDescription(pathDescriptionValue, roiIndex, shapeIndex);
          Integer pathFillValue = src.getPathFill(roiIndex, shapeIndex);
          if (pathFillValue != null) dest.setPathFill(pathFillValue, roiIndex, shapeIndex);
          Integer pathFontSizeValue = src.getPathFontSize(roiIndex, shapeIndex);
          if (pathFontSizeValue != null) dest.setPathFontSize(pathFontSizeValue, roiIndex, shapeIndex);
          String pathIDValue = src.getPathID(roiIndex, shapeIndex);
          if (pathIDValue != null) dest.setPathID(pathIDValue, roiIndex, shapeIndex);
          String pathLabelValue = src.getPathLabel(roiIndex, shapeIndex);
          if (pathLabelValue != null) dest.setPathLabel(pathLabelValue, roiIndex, shapeIndex);
          String pathNameValue = src.getPathName(roiIndex, shapeIndex);
          if (pathNameValue != null) dest.setPathName(pathNameValue, roiIndex, shapeIndex);
          Integer pathStrokeValue = src.getPathStroke(roiIndex, shapeIndex);
          if (pathStrokeValue != null) dest.setPathStroke(pathStrokeValue, roiIndex, shapeIndex);
          String pathStrokeDashArrayValue = src.getPathStrokeDashArray(roiIndex, shapeIndex);
          if (pathStrokeDashArrayValue != null) dest.setPathStrokeDashArray(pathStrokeDashArrayValue, roiIndex, shapeIndex);
          Double pathStrokeWidthValue = src.getPathStrokeWidth(roiIndex, shapeIndex);
          if (pathStrokeWidthValue != null) dest.setPathStrokeWidth(pathStrokeWidthValue, roiIndex, shapeIndex);
          Integer pathTheCValue = src.getPathTheC(roiIndex, shapeIndex);
          if (pathTheCValue != null) dest.setPathTheC(pathTheCValue, roiIndex, shapeIndex);
          Integer pathTheTValue = src.getPathTheT(roiIndex, shapeIndex);
          if (pathTheTValue != null) dest.setPathTheT(pathTheTValue, roiIndex, shapeIndex);
          Integer pathTheZValue = src.getPathTheZ(roiIndex, shapeIndex);
          if (pathTheZValue != null) dest.setPathTheZ(pathTheZValue, roiIndex, shapeIndex);
          String pathTransformValue = src.getPathTransform(roiIndex, shapeIndex);
          if (pathTransformValue != null) dest.setPathTransform(pathTransformValue, roiIndex, shapeIndex);
          String pointDescriptionValue = src.getPointDescription(roiIndex, shapeIndex);
          if (pointDescriptionValue != null) dest.setPointDescription(pointDescriptionValue, roiIndex, shapeIndex);
          Integer pointFillValue = src.getPointFill(roiIndex, shapeIndex);
          if (pointFillValue != null) dest.setPointFill(pointFillValue, roiIndex, shapeIndex);
          Integer pointFontSizeValue = src.getPointFontSize(roiIndex, shapeIndex);
          if (pointFontSizeValue != null) dest.setPointFontSize(pointFontSizeValue, roiIndex, shapeIndex);
          String pointIDValue = src.getPointID(roiIndex, shapeIndex);
          if (pointIDValue != null) dest.setPointID(pointIDValue, roiIndex, shapeIndex);
          String pointLabelValue = src.getPointLabel(roiIndex, shapeIndex);
          if (pointLabelValue != null) dest.setPointLabel(pointLabelValue, roiIndex, shapeIndex);
          String pointNameValue = src.getPointName(roiIndex, shapeIndex);
          if (pointNameValue != null) dest.setPointName(pointNameValue, roiIndex, shapeIndex);
          Integer pointStrokeValue = src.getPointStroke(roiIndex, shapeIndex);
          if (pointStrokeValue != null) dest.setPointStroke(pointStrokeValue, roiIndex, shapeIndex);
          String pointStrokeDashArrayValue = src.getPointStrokeDashArray(roiIndex, shapeIndex);
          if (pointStrokeDashArrayValue != null) dest.setPointStrokeDashArray(pointStrokeDashArrayValue, roiIndex, shapeIndex);
          Double pointStrokeWidthValue = src.getPointStrokeWidth(roiIndex, shapeIndex);
          if (pointStrokeWidthValue != null) dest.setPointStrokeWidth(pointStrokeWidthValue, roiIndex, shapeIndex);
          Integer pointTheCValue = src.getPointTheC(roiIndex, shapeIndex);
          if (pointTheCValue != null) dest.setPointTheC(pointTheCValue, roiIndex, shapeIndex);
          Integer pointTheTValue = src.getPointTheT(roiIndex, shapeIndex);
          if (pointTheTValue != null) dest.setPointTheT(pointTheTValue, roiIndex, shapeIndex);
          Integer pointTheZValue = src.getPointTheZ(roiIndex, shapeIndex);
          if (pointTheZValue != null) dest.setPointTheZ(pointTheZValue, roiIndex, shapeIndex);
          String pointTransformValue = src.getPointTransform(roiIndex, shapeIndex);
          if (pointTransformValue != null) dest.setPointTransform(pointTransformValue, roiIndex, shapeIndex);
          Double pointXValue = src.getPointX(roiIndex, shapeIndex);
          if (pointXValue != null) dest.setPointX(pointXValue, roiIndex, shapeIndex);
          Double pointYValue = src.getPointY(roiIndex, shapeIndex);
          if (pointYValue != null) dest.setPointY(pointYValue, roiIndex, shapeIndex);
          Boolean polylineClosedValue = src.getPolylineClosed(roiIndex, shapeIndex);
          if (polylineClosedValue != null) dest.setPolylineClosed(polylineClosedValue, roiIndex, shapeIndex);
          String polylineDescriptionValue = src.getPolylineDescription(roiIndex, shapeIndex);
          if (polylineDescriptionValue != null) dest.setPolylineDescription(polylineDescriptionValue, roiIndex, shapeIndex);
          Integer polylineFillValue = src.getPolylineFill(roiIndex, shapeIndex);
          if (polylineFillValue != null) dest.setPolylineFill(polylineFillValue, roiIndex, shapeIndex);
          Integer polylineFontSizeValue = src.getPolylineFontSize(roiIndex, shapeIndex);
          if (polylineFontSizeValue != null) dest.setPolylineFontSize(polylineFontSizeValue, roiIndex, shapeIndex);
          String polylineIDValue = src.getPolylineID(roiIndex, shapeIndex);
          if (polylineIDValue != null) dest.setPolylineID(polylineIDValue, roiIndex, shapeIndex);
          String polylineLabelValue = src.getPolylineLabel(roiIndex, shapeIndex);
          if (polylineLabelValue != null) dest.setPolylineLabel(polylineLabelValue, roiIndex, shapeIndex);
          String polylineNameValue = src.getPolylineName(roiIndex, shapeIndex);
          if (polylineNameValue != null) dest.setPolylineName(polylineNameValue, roiIndex, shapeIndex);
          String polylinePointsValue = src.getPolylinePoints(roiIndex, shapeIndex);
          if (polylinePointsValue != null) dest.setPolylinePoints(polylinePointsValue, roiIndex, shapeIndex);
          Integer polylineStrokeValue = src.getPolylineStroke(roiIndex, shapeIndex);
          if (polylineStrokeValue != null) dest.setPolylineStroke(polylineStrokeValue, roiIndex, shapeIndex);
          String polylineStrokeDashArrayValue = src.getPolylineStrokeDashArray(roiIndex, shapeIndex);
          if (polylineStrokeDashArrayValue != null) dest.setPolylineStrokeDashArray(polylineStrokeDashArrayValue, roiIndex, shapeIndex);
          Double polylineStrokeWidthValue = src.getPolylineStrokeWidth(roiIndex, shapeIndex);
          if (polylineStrokeWidthValue != null) dest.setPolylineStrokeWidth(polylineStrokeWidthValue, roiIndex, shapeIndex);
          Integer polylineTheCValue = src.getPolylineTheC(roiIndex, shapeIndex);
          if (polylineTheCValue != null) dest.setPolylineTheC(polylineTheCValue, roiIndex, shapeIndex);
          Integer polylineTheTValue = src.getPolylineTheT(roiIndex, shapeIndex);
          if (polylineTheTValue != null) dest.setPolylineTheT(polylineTheTValue, roiIndex, shapeIndex);
          Integer polylineTheZValue = src.getPolylineTheZ(roiIndex, shapeIndex);
          if (polylineTheZValue != null) dest.setPolylineTheZ(polylineTheZValue, roiIndex, shapeIndex);
          String polylineTransformValue = src.getPolylineTransform(roiIndex, shapeIndex);
          if (polylineTransformValue != null) dest.setPolylineTransform(polylineTransformValue, roiIndex, shapeIndex);
          String rectangleDescriptionValue = src.getRectangleDescription(roiIndex, shapeIndex);
          if (rectangleDescriptionValue != null) dest.setRectangleDescription(rectangleDescriptionValue, roiIndex, shapeIndex);
          Integer rectangleFillValue = src.getRectangleFill(roiIndex, shapeIndex);
          if (rectangleFillValue != null) dest.setRectangleFill(rectangleFillValue, roiIndex, shapeIndex);
          Integer rectangleFontSizeValue = src.getRectangleFontSize(roiIndex, shapeIndex);
          if (rectangleFontSizeValue != null) dest.setRectangleFontSize(rectangleFontSizeValue, roiIndex, shapeIndex);
          Double rectangleHeightValue = src.getRectangleHeight(roiIndex, shapeIndex);
          if (rectangleHeightValue != null) dest.setRectangleHeight(rectangleHeightValue, roiIndex, shapeIndex);
          String rectangleIDValue = src.getRectangleID(roiIndex, shapeIndex);
          if (rectangleIDValue != null) dest.setRectangleID(rectangleIDValue, roiIndex, shapeIndex);
          String rectangleLabelValue = src.getRectangleLabel(roiIndex, shapeIndex);
          if (rectangleLabelValue != null) dest.setRectangleLabel(rectangleLabelValue, roiIndex, shapeIndex);
          String rectangleNameValue = src.getRectangleName(roiIndex, shapeIndex);
          if (rectangleNameValue != null) dest.setRectangleName(rectangleNameValue, roiIndex, shapeIndex);
          Integer rectangleStrokeValue = src.getRectangleStroke(roiIndex, shapeIndex);
          if (rectangleStrokeValue != null) dest.setRectangleStroke(rectangleStrokeValue, roiIndex, shapeIndex);
          String rectangleStrokeDashArrayValue = src.getRectangleStrokeDashArray(roiIndex, shapeIndex);
          if (rectangleStrokeDashArrayValue != null) dest.setRectangleStrokeDashArray(rectangleStrokeDashArrayValue, roiIndex, shapeIndex);
          Double rectangleStrokeWidthValue = src.getRectangleStrokeWidth(roiIndex, shapeIndex);
          if (rectangleStrokeWidthValue != null) dest.setRectangleStrokeWidth(rectangleStrokeWidthValue, roiIndex, shapeIndex);
          Integer rectangleTheCValue = src.getRectangleTheC(roiIndex, shapeIndex);
          if (rectangleTheCValue != null) dest.setRectangleTheC(rectangleTheCValue, roiIndex, shapeIndex);
          Integer rectangleTheTValue = src.getRectangleTheT(roiIndex, shapeIndex);
          if (rectangleTheTValue != null) dest.setRectangleTheT(rectangleTheTValue, roiIndex, shapeIndex);
          Integer rectangleTheZValue = src.getRectangleTheZ(roiIndex, shapeIndex);
          if (rectangleTheZValue != null) dest.setRectangleTheZ(rectangleTheZValue, roiIndex, shapeIndex);
          String rectangleTransformValue = src.getRectangleTransform(roiIndex, shapeIndex);
          if (rectangleTransformValue != null) dest.setRectangleTransform(rectangleTransformValue, roiIndex, shapeIndex);
          Double rectangleWidthValue = src.getRectangleWidth(roiIndex, shapeIndex);
          if (rectangleWidthValue != null) dest.setRectangleWidth(rectangleWidthValue, roiIndex, shapeIndex);
          Double rectangleXValue = src.getRectangleX(roiIndex, shapeIndex);
          if (rectangleXValue != null) dest.setRectangleX(rectangleXValue, roiIndex, shapeIndex);
          Double rectangleYValue = src.getRectangleY(roiIndex, shapeIndex);
          if (rectangleYValue != null) dest.setRectangleY(rectangleYValue, roiIndex, shapeIndex);
          String textDescriptionValue = src.getTextDescription(roiIndex, shapeIndex);
          if (textDescriptionValue != null) dest.setTextDescription(textDescriptionValue, roiIndex, shapeIndex);
          Integer textFillValue = src.getTextFill(roiIndex, shapeIndex);
          if (textFillValue != null) dest.setTextFill(textFillValue, roiIndex, shapeIndex);
          Integer textFontSizeValue = src.getTextFontSize(roiIndex, shapeIndex);
          if (textFontSizeValue != null) dest.setTextFontSize(textFontSizeValue, roiIndex, shapeIndex);
          String textIDValue = src.getTextID(roiIndex, shapeIndex);
          if (textIDValue != null) dest.setTextID(textIDValue, roiIndex, shapeIndex);
          String textLabelValue = src.getTextLabel(roiIndex, shapeIndex);
          if (textLabelValue != null) dest.setTextLabel(textLabelValue, roiIndex, shapeIndex);
          String textNameValue = src.getTextName(roiIndex, shapeIndex);
          if (textNameValue != null) dest.setTextName(textNameValue, roiIndex, shapeIndex);
          Integer textStrokeValue = src.getTextStroke(roiIndex, shapeIndex);
          if (textStrokeValue != null) dest.setTextStroke(textStrokeValue, roiIndex, shapeIndex);
          String textStrokeDashArrayValue = src.getTextStrokeDashArray(roiIndex, shapeIndex);
          if (textStrokeDashArrayValue != null) dest.setTextStrokeDashArray(textStrokeDashArrayValue, roiIndex, shapeIndex);
          Double textStrokeWidthValue = src.getTextStrokeWidth(roiIndex, shapeIndex);
          if (textStrokeWidthValue != null) dest.setTextStrokeWidth(textStrokeWidthValue, roiIndex, shapeIndex);
          Integer textTheCValue = src.getTextTheC(roiIndex, shapeIndex);
          if (textTheCValue != null) dest.setTextTheC(textTheCValue, roiIndex, shapeIndex);
          Integer textTheTValue = src.getTextTheT(roiIndex, shapeIndex);
          if (textTheTValue != null) dest.setTextTheT(textTheTValue, roiIndex, shapeIndex);
          Integer textTheZValue = src.getTextTheZ(roiIndex, shapeIndex);
          if (textTheZValue != null) dest.setTextTheZ(textTheZValue, roiIndex, shapeIndex);
          String textTransformValue = src.getTextTransform(roiIndex, shapeIndex);
          if (textTransformValue != null) dest.setTextTransform(textTransformValue, roiIndex, shapeIndex);
          String textValueValue = src.getTextValue(roiIndex, shapeIndex);
          if (textValueValue != null) dest.setTextValue(textValueValue, roiIndex, shapeIndex);
          Double textXValue = src.getTextX(roiIndex, shapeIndex);
          if (textXValue != null) dest.setTextX(textXValue, roiIndex, shapeIndex);
          Double textYValue = src.getTextY(roiIndex, shapeIndex);
          if (textYValue != null) dest.setTextY(textYValue, roiIndex, shapeIndex);
      }
      */
    }
    int screenCount = src.getScreenCount();
    for (int screenIndex=0; screenIndex<screenCount; screenIndex++) {
      //String screenAnnotationRefValue = src.getScreenAnnotationRef(screenIndex, annotationRefIndex);
      //if (screenAnnotationRefValue != null) dest.setScreenAnnotationRef(screenAnnotationRefValue, screenIndex, annotationRefIndex);
      String screenDescriptionValue = src.getScreenDescription(screenIndex);
      if (screenDescriptionValue != null) dest.setScreenDescription(screenDescriptionValue, screenIndex);
      String screenIDValue = src.getScreenID(screenIndex);
      if (screenIDValue != null) dest.setScreenID(screenIDValue, screenIndex);
      String screenNameValue = src.getScreenName(screenIndex);
      if (screenNameValue != null) dest.setScreenName(screenNameValue, screenIndex);
      //String screenPlateRefValue = src.getScreenPlateRef(screenIndex, plateRefIndex);
      //if (screenPlateRefValue != null) dest.setScreenPlateRef(screenPlateRefValue, screenIndex, plateRefIndex);
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
      //int annotationRefCount = src.getAnnotationRefCount(screenIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
      //}
      int plateRefCount = src.getPlateRefCount(screenIndex);
      for (int plateRefIndex=0; plateRefIndex<plateRefCount; plateRefIndex++) {
      }
      int reagentCount = src.getReagentCount(screenIndex);
      for (int reagentIndex=0; reagentIndex<reagentCount; reagentIndex++) {
        //String reagentAnnotationRefValue = src.getReagentAnnotationRef(screenIndex, reagentIndex, annotationRefIndex);
        //if (reagentAnnotationRefValue != null) dest.setReagentAnnotationRef(reagentAnnotationRefValue, screenIndex, reagentIndex, annotationRefIndex);
        String reagentDescriptionValue = src.getReagentDescription(screenIndex, reagentIndex);
        if (reagentDescriptionValue != null) dest.setReagentDescription(reagentDescriptionValue, screenIndex, reagentIndex);
        String reagentIDValue = src.getReagentID(screenIndex, reagentIndex);
        if (reagentIDValue != null) dest.setReagentID(reagentIDValue, screenIndex, reagentIndex);
        String reagentNameValue = src.getReagentName(screenIndex, reagentIndex);
        if (reagentNameValue != null) dest.setReagentName(reagentNameValue, screenIndex, reagentIndex);
        String reagentReagentIdentifierValue = src.getReagentReagentIdentifier(screenIndex, reagentIndex);
        if (reagentReagentIdentifierValue != null) dest.setReagentReagentIdentifier(reagentReagentIdentifierValue, screenIndex, reagentIndex);
        //int annotationRefCount = src.getAnnotationRefCount(screenIndex, reagentIndex);
        //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        //}
      }
    }
    int stringAnnotationCount = src.getStringAnnotationCount();
    for (int stringAnnotationIndex=0; stringAnnotationIndex<stringAnnotationCount; stringAnnotationIndex++) {
      String stringAnnotationIDValue = src.getStringAnnotationID(stringAnnotationIndex);
      if (stringAnnotationIDValue != null) dest.setStringAnnotationID(stringAnnotationIDValue, stringAnnotationIndex);
      String stringAnnotationNamespaceValue = src.getStringAnnotationNamespace(stringAnnotationIndex);
      if (stringAnnotationNamespaceValue != null) dest.setStringAnnotationNamespace(stringAnnotationNamespaceValue, stringAnnotationIndex);
      String stringAnnotationValueValue = src.getStringAnnotationValue(stringAnnotationIndex);
      if (stringAnnotationValueValue != null) dest.setStringAnnotationValue(stringAnnotationValueValue, stringAnnotationIndex);
    }
    int timestampAnnotationCount = src.getTimestampAnnotationCount();
    for (int timestampAnnotationIndex=0; timestampAnnotationIndex<timestampAnnotationCount; timestampAnnotationIndex++) {
      String timestampAnnotationIDValue = src.getTimestampAnnotationID(timestampAnnotationIndex);
      if (timestampAnnotationIDValue != null) dest.setTimestampAnnotationID(timestampAnnotationIDValue, timestampAnnotationIndex);
      String timestampAnnotationNamespaceValue = src.getTimestampAnnotationNamespace(timestampAnnotationIndex);
      if (timestampAnnotationNamespaceValue != null) dest.setTimestampAnnotationNamespace(timestampAnnotationNamespaceValue, timestampAnnotationIndex);
      String timestampAnnotationValueValue = src.getTimestampAnnotationValue(timestampAnnotationIndex);
      if (timestampAnnotationValueValue != null) dest.setTimestampAnnotationValue(timestampAnnotationValueValue, timestampAnnotationIndex);
    }
    int xmlAnnotationCount = src.getXMLAnnotationCount();
    for (int xmlAnnotationIndex=0; xmlAnnotationIndex<xmlAnnotationCount; xmlAnnotationIndex++) {
      String xmlAnnotationIDValue = src.getXMLAnnotationID(xmlAnnotationIndex);
      if (xmlAnnotationIDValue != null) dest.setXMLAnnotationID(xmlAnnotationIDValue, xmlAnnotationIndex);
      String xmlAnnotationNamespaceValue = src.getXMLAnnotationNamespace(xmlAnnotationIndex);
      if (xmlAnnotationNamespaceValue != null) dest.setXMLAnnotationNamespace(xmlAnnotationNamespaceValue, xmlAnnotationIndex);
      String xmlAnnotationValueValue = src.getXMLAnnotationValue(xmlAnnotationIndex);
      if (xmlAnnotationValueValue != null) dest.setXMLAnnotationValue(xmlAnnotationValueValue, xmlAnnotationIndex);
    }
  }

}
