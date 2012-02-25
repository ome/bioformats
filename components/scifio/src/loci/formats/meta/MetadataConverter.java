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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/meta/MetadataConverter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/meta/MetadataConverter.java;hb=HEAD">Gitweb</a></dd></dl>
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
    try {
    int booleanAnnotationCount = src.getBooleanAnnotationCount();
    for (int booleanAnnotationIndex=0; booleanAnnotationIndex<booleanAnnotationCount; booleanAnnotationIndex++) {
    try {
      String booleanAnnotationIDValue = src.getBooleanAnnotationID(booleanAnnotationIndex);
      if (booleanAnnotationIDValue != null) dest.setBooleanAnnotationID(booleanAnnotationIDValue, booleanAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String booleanAnnotationNamespaceValue = src.getBooleanAnnotationNamespace(booleanAnnotationIndex);
      if (booleanAnnotationNamespaceValue != null) dest.setBooleanAnnotationNamespace(booleanAnnotationNamespaceValue, booleanAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      Boolean booleanAnnotationValueValue = src.getBooleanAnnotationValue(booleanAnnotationIndex);
      if (booleanAnnotationValueValue != null) dest.setBooleanAnnotationValue(booleanAnnotationValueValue, booleanAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
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
    } catch (NullPointerException e) { }
    try {
      String datasetExperimenterRefValue = src.getDatasetExperimenterRef(datasetIndex);
      if (datasetExperimenterRefValue != null) dest.setDatasetExperimenterRef(datasetExperimenterRefValue, datasetIndex);
    } catch (NullPointerException e) { }
    try {
      String datasetGroupRefValue = src.getDatasetGroupRef(datasetIndex);
      if (datasetGroupRefValue != null) dest.setDatasetGroupRef(datasetGroupRefValue, datasetIndex);
    } catch (NullPointerException e) { }
    try {
      String datasetNameValue = src.getDatasetName(datasetIndex);
      if (datasetNameValue != null) dest.setDatasetName(datasetNameValue, datasetIndex);
    } catch (NullPointerException e) { }
    try {
      int projectRefCount = src.getProjectRefCount(datasetIndex);
      for (int projectRefIndex=0; projectRefIndex<projectRefCount; projectRefIndex++)
      {
        String datasetProjectRefValue = src.getDatasetProjectRef(datasetIndex, projectRefIndex);
        if (datasetProjectRefValue != null) dest.setDatasetProjectRef(datasetProjectRefValue, datasetIndex, projectRefIndex);
      }
    } catch (NullPointerException e) { }
      try {
      int annotationRefCount = src.getDatasetAnnotationRefCount(datasetIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
        String annotationValue = src.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
        if (annotationValue != null) dest.setDatasetAnnotationRef(annotationValue, datasetIndex, annotationRefIndex);
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int doubleAnnotationCount = src.getDoubleAnnotationCount();
    for (int doubleAnnotationIndex=0; doubleAnnotationIndex<doubleAnnotationCount; doubleAnnotationIndex++) {
    try {
      String doubleAnnotationIDValue = src.getDoubleAnnotationID(doubleAnnotationIndex);
      if (doubleAnnotationIDValue != null) dest.setDoubleAnnotationID(doubleAnnotationIDValue, doubleAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String doubleAnnotationNamespaceValue = src.getDoubleAnnotationNamespace(doubleAnnotationIndex);
      if (doubleAnnotationNamespaceValue != null) dest.setDoubleAnnotationNamespace(doubleAnnotationNamespaceValue, doubleAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      Double doubleAnnotationValueValue = src.getDoubleAnnotationValue(doubleAnnotationIndex);
      if (doubleAnnotationValueValue != null) dest.setDoubleAnnotationValue(doubleAnnotationValueValue, doubleAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int experimentCount = src.getExperimentCount();
    for (int experimentIndex=0; experimentIndex<experimentCount; experimentIndex++) {
    try {
      String experimentDescriptionValue = src.getExperimentDescription(experimentIndex);
      if (experimentDescriptionValue != null) dest.setExperimentDescription(experimentDescriptionValue, experimentIndex);
    } catch (NullPointerException e) { }
    try {
      String experimentExperimenterRefValue = src.getExperimentExperimenterRef(experimentIndex);
      if (experimentExperimenterRefValue != null) dest.setExperimentExperimenterRef(experimentExperimenterRefValue, experimentIndex);
    } catch (NullPointerException e) { }
    try {
      String experimentIDValue = src.getExperimentID(experimentIndex);
      if (experimentIDValue != null) dest.setExperimentID(experimentIDValue, experimentIndex);
    } catch (NullPointerException e) { }
    try {
      ExperimentType experimentTypeValue = src.getExperimentType(experimentIndex);
      if (experimentTypeValue != null) dest.setExperimentType(experimentTypeValue, experimentIndex);
    } catch (NullPointerException e) { }
      try {
      int microbeamManipulationCount = src.getMicrobeamManipulationCount(experimentIndex);
      for (int microbeamManipulationIndex=0; microbeamManipulationIndex<microbeamManipulationCount; microbeamManipulationIndex++) {
      try {
        String microbeamManipulationExperimenterRefValue = src.getMicrobeamManipulationExperimenterRef(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationExperimenterRefValue != null) dest.setMicrobeamManipulationExperimenterRef(microbeamManipulationExperimenterRefValue, experimentIndex, microbeamManipulationIndex);
      } catch (NullPointerException e) { }
      try {
        String microbeamManipulationIDValue = src.getMicrobeamManipulationID(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationIDValue != null) dest.setMicrobeamManipulationID(microbeamManipulationIDValue, experimentIndex, microbeamManipulationIndex);
      } catch (NullPointerException e) { }
      try {
        int roiRefCount = src.getMicrobeamManipulationROIRefCount(experimentIndex, microbeamManipulationIndex);
        for (int roiRefIndex=0; roiRefIndex<roiRefCount; roiRefIndex++) {
          String microbeamManipulationROIRefValue = src.getMicrobeamManipulationROIRef(experimentIndex, microbeamManipulationIndex, roiRefIndex);
          if (microbeamManipulationROIRefValue != null) dest.setMicrobeamManipulationROIRef(microbeamManipulationROIRefValue, experimentIndex, microbeamManipulationIndex, roiRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        MicrobeamManipulationType microbeamManipulationTypeValue = src.getMicrobeamManipulationType(experimentIndex, microbeamManipulationIndex);
        if (microbeamManipulationTypeValue != null) dest.setMicrobeamManipulationType(microbeamManipulationTypeValue, experimentIndex, microbeamManipulationIndex);
      } catch (NullPointerException e) { }
        try {
        int lightSourceSettingsCount = src.getMicrobeamManipulationLightSourceSettingsCount(experimentIndex, microbeamManipulationIndex);
        for (int lightSourceSettingsIndex=0; lightSourceSettingsIndex<lightSourceSettingsCount; lightSourceSettingsIndex++) {
        try {
          PercentFraction microbeamManipulationLightSourceSettingsAttenuationValue = src.getMicrobeamManipulationLightSourceSettingsAttenuation(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsAttenuationValue != null) dest.setMicrobeamManipulationLightSourceSettingsAttenuation(microbeamManipulationLightSourceSettingsAttenuationValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        } catch (NullPointerException e) { }
        try {
          String microbeamManipulationLightSourceSettingsIDValue = src.getMicrobeamManipulationLightSourceSettingsID(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsIDValue != null) dest.setMicrobeamManipulationLightSourceSettingsID(microbeamManipulationLightSourceSettingsIDValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        } catch (NullPointerException e) { }
        try {
          PositiveInteger microbeamManipulationLightSourceSettingsWavelengthValue = src.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
          if (microbeamManipulationLightSourceSettingsWavelengthValue != null) dest.setMicrobeamManipulationLightSourceSettingsWavelength(microbeamManipulationLightSourceSettingsWavelengthValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        } catch (NullPointerException e) { }
        }
        } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int experimenterCount = src.getExperimenterCount();
    for (int experimenterIndex=0; experimenterIndex<experimenterCount; experimenterIndex++) {
    try {
      int annotationRefCount = src.getExperimenterAnnotationRefCount(experimenterIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
        String experimenterAnnotationRefValue = src.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
        if (experimenterAnnotationRefValue != null) dest.setExperimenterAnnotationRef(experimenterAnnotationRefValue, experimenterIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String experimenterDisplayNameValue = src.getExperimenterDisplayName(experimenterIndex);
      if (experimenterDisplayNameValue != null) dest.setExperimenterDisplayName(experimenterDisplayNameValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterEmailValue = src.getExperimenterEmail(experimenterIndex);
      if (experimenterEmailValue != null) dest.setExperimenterEmail(experimenterEmailValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterFirstNameValue = src.getExperimenterFirstName(experimenterIndex);
      if (experimenterFirstNameValue != null) dest.setExperimenterFirstName(experimenterFirstNameValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      int groupRefCount = src.getExperimenterGroupRefCount(experimenterIndex);
      for (int groupRefIndex=0; groupRefIndex<groupRefCount; groupRefIndex++) {
        String experimenterGroupRefValue = src.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
        if (experimenterGroupRefValue != null) dest.setExperimenterGroupRef(experimenterGroupRefValue, experimenterIndex, groupRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String experimenterIDValue = src.getExperimenterID(experimenterIndex);
      if (experimenterIDValue != null) dest.setExperimenterID(experimenterIDValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterInstitutionValue = src.getExperimenterInstitution(experimenterIndex);
      if (experimenterInstitutionValue != null) dest.setExperimenterInstitution(experimenterInstitutionValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterLastNameValue = src.getExperimenterLastName(experimenterIndex);
      if (experimenterLastNameValue != null) dest.setExperimenterLastName(experimenterLastNameValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterMiddleNameValue = src.getExperimenterMiddleName(experimenterIndex);
      if (experimenterMiddleNameValue != null) dest.setExperimenterMiddleName(experimenterMiddleNameValue, experimenterIndex);
    } catch (NullPointerException e) { }
    try {
      String experimenterUserNameValue = src.getExperimenterUserName(experimenterIndex);
      if (experimenterUserNameValue != null) dest.setExperimenterUserName(experimenterUserNameValue, experimenterIndex);
    } catch (NullPointerException e) { }
      try {
        int annotationRefCount = src.getExperimenterAnnotationRefCount(experimenterIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
          String annotationRef = src.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
          if (annotationRef != null) dest.setExperimenterAnnotationRef(annotationRef, experimenterIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        int groupRefCount = src.getExperimenterGroupRefCount(experimenterIndex);
        for (int groupRefIndex=0; groupRefIndex<groupRefCount; groupRefIndex++)
        {
          String groupRef = src.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
          if (groupRef != null) dest.setExperimenterGroupRef(groupRef, experimenterIndex, groupRefIndex);
        }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int fileAnnotationCount = src.getFileAnnotationCount();
    for (int fileAnnotationIndex=0; fileAnnotationIndex<fileAnnotationCount; fileAnnotationIndex++) {
    try {
      String fileAnnotationBinaryFileFileNameValue = src.getFileAnnotationBinaryFileFileName(fileAnnotationIndex);
      if (fileAnnotationBinaryFileFileNameValue != null) dest.setFileAnnotationBinaryFileFileName(fileAnnotationBinaryFileFileNameValue, fileAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String fileAnnotationBinaryFileMIMETypeValue = src.getFileAnnotationBinaryFileMIMEType(fileAnnotationIndex);
      if (fileAnnotationBinaryFileMIMETypeValue != null) dest.setFileAnnotationBinaryFileMIMEType(fileAnnotationBinaryFileMIMETypeValue, fileAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      NonNegativeLong fileAnnotationBinaryFileSizeValue = src.getFileAnnotationBinaryFileSize(fileAnnotationIndex);
      if (fileAnnotationBinaryFileSizeValue != null) dest.setFileAnnotationBinaryFileSize(fileAnnotationBinaryFileSizeValue, fileAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String fileAnnotationIDValue = src.getFileAnnotationID(fileAnnotationIndex);
      if (fileAnnotationIDValue != null) dest.setFileAnnotationID(fileAnnotationIDValue, fileAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String fileAnnotationNamespaceValue = src.getFileAnnotationNamespace(fileAnnotationIndex);
      if (fileAnnotationNamespaceValue != null) dest.setFileAnnotationNamespace(fileAnnotationNamespaceValue, fileAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int groupCount = src.getGroupCount();
    for (int groupIndex=0; groupIndex<groupCount; groupIndex++) {
    try {
      String groupIDValue = src.getGroupID(groupIndex);
      if (groupIDValue != null) dest.setGroupID(groupIDValue, groupIndex);
    } catch (NullPointerException e) { }
    try {
      String groupContactValue = src.getGroupContact(groupIndex);
      if (groupContactValue != null) dest.setGroupContact(groupContactValue, groupIndex);
    } catch (NullPointerException e) { }
    try {
      String groupDescriptionValue = src.getGroupDescription(groupIndex);
      if (groupDescriptionValue != null) dest.setGroupDescription(groupDescriptionValue, groupIndex);
    } catch (NullPointerException e) { }
    try {
      String groupLeaderValue = src.getGroupLeader(groupIndex);
      if (groupLeaderValue != null) dest.setGroupLeader(groupLeaderValue, groupIndex);
    } catch (NullPointerException e) { }
    try {
      String groupNameValue = src.getGroupName(groupIndex);
      if (groupNameValue != null) dest.setGroupName(groupNameValue, groupIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
    try {
      String imageAcquiredDateValue = src.getImageAcquiredDate(imageIndex);
      if (imageAcquiredDateValue != null) dest.setImageAcquiredDate(imageAcquiredDateValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      int annotationRefCount = src.getImageAnnotationRefCount(imageIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
      String imageAnnotationRefValue = src.getImageAnnotationRef(imageIndex, annotationRefIndex);
      if (imageAnnotationRefValue != null) dest.setImageAnnotationRef(imageAnnotationRefValue, imageIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      int datasetRefCount = src.getDatasetRefCount(imageIndex);
      for (int datasetRefIndex=0; datasetRefIndex<datasetRefCount; datasetRefIndex++)
      {
        String imageDatasetRefValue = src.getImageDatasetRef(imageIndex, datasetRefIndex);
        if (imageDatasetRefValue != null) dest.setImageDatasetRef(imageDatasetRefValue, imageIndex, datasetRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String imageDescriptionValue = src.getImageDescription(imageIndex);
      if (imageDescriptionValue != null) dest.setImageDescription(imageDescriptionValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageExperimentRefValue = src.getImageExperimentRef(imageIndex);
      if (imageExperimentRefValue != null) dest.setImageExperimentRef(imageExperimentRefValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageExperimenterRefValue = src.getImageExperimenterRef(imageIndex);
      if (imageExperimenterRefValue != null) dest.setImageExperimenterRef(imageExperimenterRefValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageGroupRefValue = src.getImageGroupRef(imageIndex);
      if (imageGroupRefValue != null) dest.setImageGroupRef(imageGroupRefValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageIDValue = src.getImageID(imageIndex);
      if (imageIDValue != null) dest.setImageID(imageIDValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageInstrumentRefValue = src.getImageInstrumentRef(imageIndex);
      if (imageInstrumentRefValue != null) dest.setImageInstrumentRef(imageInstrumentRefValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      int microbeamManipulationRefCount = src.getMicrobeamManipulationRefCount(imageIndex);
      for (int microbeamManipulationRefIndex=0; microbeamManipulationRefIndex<microbeamManipulationRefCount; microbeamManipulationRefIndex++)
      {
        String imageMicrobeamManipulationRefValue = src.getImageMicrobeamManipulationRef(imageIndex, microbeamManipulationRefIndex);
        if (imageMicrobeamManipulationRefValue != null) dest.setImageMicrobeamManipulationRef(imageMicrobeamManipulationRefValue, imageIndex, microbeamManipulationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String imageNameValue = src.getImageName(imageIndex);
      if (imageNameValue != null) dest.setImageName(imageNameValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      Double imageObjectiveSettingsCorrectionCollarValue = src.getImageObjectiveSettingsCorrectionCollar(imageIndex);
      if (imageObjectiveSettingsCorrectionCollarValue != null) dest.setImageObjectiveSettingsCorrectionCollar(imageObjectiveSettingsCorrectionCollarValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      String imageObjectiveSettingsIDValue = src.getImageObjectiveSettingsID(imageIndex);
      if (imageObjectiveSettingsIDValue != null) dest.setImageObjectiveSettingsID(imageObjectiveSettingsIDValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      Medium imageObjectiveSettingsMediumValue = src.getImageObjectiveSettingsMedium(imageIndex);
      if (imageObjectiveSettingsMediumValue != null) dest.setImageObjectiveSettingsMedium(imageObjectiveSettingsMediumValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      Double imageObjectiveSettingsRefractiveIndexValue = src.getImageObjectiveSettingsRefractiveIndex(imageIndex);
      if (imageObjectiveSettingsRefractiveIndexValue != null) dest.setImageObjectiveSettingsRefractiveIndex(imageObjectiveSettingsRefractiveIndexValue, imageIndex);
    } catch (NullPointerException e) { }
    try {
      int roiRefCount = src.getImageROIRefCount(imageIndex);
      for (int roiRefIndex=0; roiRefIndex<roiRefCount; roiRefIndex++) {
        String imageROIRefValue = src.getImageROIRef(imageIndex, roiRefIndex);
        if (imageROIRefValue != null) dest.setImageROIRef(imageROIRefValue, imageIndex, roiRefIndex);
      }
    } catch (NullPointerException e) { }
      try {
      int channelCount = src.getChannelCount(imageIndex);
      for (int channelIndex=0; channelIndex<channelCount; channelIndex++) {
      try {
        AcquisitionMode channelAcquisitionModeValue = src.getChannelAcquisitionMode(imageIndex, channelIndex);
        if (channelAcquisitionModeValue != null) dest.setChannelAcquisitionMode(channelAcquisitionModeValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        int annotationRefCount = src.getChannelAnnotationRefCount(imageIndex, channelIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++) {
          String channelAnnotationRefValue = src.getChannelAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
          if (channelAnnotationRefValue != null) dest.setChannelAnnotationRef(channelAnnotationRefValue, imageIndex, channelIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        Integer channelColorValue = src.getChannelColor(imageIndex, channelIndex);
        if (channelColorValue != null) dest.setChannelColor(channelColorValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        ContrastMethod channelContrastMethodValue = src.getChannelContrastMethod(imageIndex, channelIndex);
        if (channelContrastMethodValue != null) dest.setChannelContrastMethod(channelContrastMethodValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger channelEmissionWavelengthValue = src.getChannelEmissionWavelength(imageIndex, channelIndex);
        if (channelEmissionWavelengthValue != null) dest.setChannelEmissionWavelength(channelEmissionWavelengthValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger channelExcitationWavelengthValue = src.getChannelExcitationWavelength(imageIndex, channelIndex);
        if (channelExcitationWavelengthValue != null) dest.setChannelExcitationWavelength(channelExcitationWavelengthValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        String channelFilterSetRefValue = src.getChannelFilterSetRef(imageIndex, channelIndex);
        if (channelFilterSetRefValue != null) dest.setChannelFilterSetRef(channelFilterSetRefValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        String channelFluorValue = src.getChannelFluor(imageIndex, channelIndex);
        if (channelFluorValue != null) dest.setChannelFluor(channelFluorValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        String channelIDValue = src.getChannelID(imageIndex, channelIndex);
        if (channelIDValue != null) dest.setChannelID(channelIDValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        IlluminationType channelIlluminationTypeValue = src.getChannelIlluminationType(imageIndex, channelIndex);
        if (channelIlluminationTypeValue != null) dest.setChannelIlluminationType(channelIlluminationTypeValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        PercentFraction channelLightSourceSettingsAttenuationValue = src.getChannelLightSourceSettingsAttenuation(imageIndex, channelIndex);
        if (channelLightSourceSettingsAttenuationValue != null) dest.setChannelLightSourceSettingsAttenuation(channelLightSourceSettingsAttenuationValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        String channelLightSourceSettingsIDValue = src.getChannelLightSourceSettingsID(imageIndex, channelIndex);
        if (channelLightSourceSettingsIDValue != null) dest.setChannelLightSourceSettingsID(channelLightSourceSettingsIDValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger channelLightSourceSettingsWavelengthValue = src.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
        if (channelLightSourceSettingsWavelengthValue != null) dest.setChannelLightSourceSettingsWavelength(channelLightSourceSettingsWavelengthValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        Double channelNDFilterValue = src.getChannelNDFilter(imageIndex, channelIndex);
        if (channelNDFilterValue != null) dest.setChannelNDFilter(channelNDFilterValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        String channelNameValue = src.getChannelName(imageIndex, channelIndex);
        if (channelNameValue != null) dest.setChannelName(channelNameValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        Double channelPinholeSizeValue = src.getChannelPinholeSize(imageIndex, channelIndex);
        if (channelPinholeSizeValue != null) dest.setChannelPinholeSize(channelPinholeSizeValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        Integer channelPockelCellSettingValue = src.getChannelPockelCellSetting(imageIndex, channelIndex);
        if (channelPockelCellSettingValue != null) dest.setChannelPockelCellSetting(channelPockelCellSettingValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger channelSamplesPerPixelValue = src.getChannelSamplesPerPixel(imageIndex, channelIndex);
        if (channelSamplesPerPixelValue != null) dest.setChannelSamplesPerPixel(channelSamplesPerPixelValue, imageIndex, channelIndex);
      } catch (NullPointerException e) { }
        try {
          Binning detectorSettingsBinningValue = src.getDetectorSettingsBinning(imageIndex, channelIndex);
          if (detectorSettingsBinningValue != null) dest.setDetectorSettingsBinning(detectorSettingsBinningValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          Double detectorSettingsGainValue = src.getDetectorSettingsGain(imageIndex, channelIndex);
          if (detectorSettingsGainValue != null) dest.setDetectorSettingsGain(detectorSettingsGainValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          String detectorSettingsIDValue = src.getDetectorSettingsID(imageIndex, channelIndex);
          if (detectorSettingsIDValue != null) dest.setDetectorSettingsID(detectorSettingsIDValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          Double detectorSettingsOffsetValue = src.getDetectorSettingsOffset(imageIndex, channelIndex);
          if (detectorSettingsOffsetValue != null) dest.setDetectorSettingsOffset(detectorSettingsOffsetValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          Double detectorSettingsReadOutRateValue = src.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
          if (detectorSettingsReadOutRateValue != null) dest.setDetectorSettingsReadOutRate(detectorSettingsReadOutRateValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          Double detectorSettingsVoltageValue = src.getDetectorSettingsVoltage(imageIndex, channelIndex);
          if (detectorSettingsVoltageValue != null) dest.setDetectorSettingsVoltage(detectorSettingsVoltageValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          String lightPathDichroicRefValue = src.getLightPathDichroicRef(imageIndex, channelIndex);
          if (lightPathDichroicRefValue != null) dest.setLightPathDichroicRef(lightPathDichroicRefValue, imageIndex, channelIndex);
        } catch (NullPointerException e) { }
        try {
          int emissionFilterRefCount = src.getLightPathEmissionFilterRefCount(imageIndex, channelIndex);
          for (int emissionFilterRefIndex=0; emissionFilterRefIndex<emissionFilterRefCount; emissionFilterRefIndex++)
          {
            String lightPathEmissionFilterRefValue = src.getLightPathEmissionFilterRef(imageIndex, channelIndex, emissionFilterRefIndex);
            if (lightPathEmissionFilterRefValue != null) dest.setLightPathEmissionFilterRef(lightPathEmissionFilterRefValue, imageIndex, channelIndex, emissionFilterRefIndex);
          }
        } catch (NullPointerException e) { }
        try {
          int excitationFilterRefCount = src.getLightPathExcitationFilterRefCount(imageIndex, channelIndex);
          for (int excitationFilterRefIndex=0; excitationFilterRefIndex<excitationFilterRefCount; excitationFilterRefIndex++)
          {
            String lightPathExcitationFilterRefValue = src.getLightPathExcitationFilterRef(imageIndex, channelIndex, excitationFilterRefIndex);
            if (lightPathExcitationFilterRefValue != null) dest.setLightPathExcitationFilterRef(lightPathExcitationFilterRefValue, imageIndex, channelIndex, excitationFilterRefIndex);
          }
        } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int datasetRefCount = src.getDatasetRefCount(imageIndex);
      for (int datasetRefIndex=0; datasetRefIndex<datasetRefCount; datasetRefIndex++) {
      }
      } catch (NullPointerException e) { }
      try {
        Double imagingEnvironmentAirPressureValue = src.getImagingEnvironmentAirPressure(imageIndex);
        if (imagingEnvironmentAirPressureValue != null) dest.setImagingEnvironmentAirPressure(imagingEnvironmentAirPressureValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PercentFraction imagingEnvironmentCO2PercentValue = src.getImagingEnvironmentCO2Percent(imageIndex);
        if (imagingEnvironmentCO2PercentValue != null) dest.setImagingEnvironmentCO2Percent(imagingEnvironmentCO2PercentValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PercentFraction imagingEnvironmentHumidityValue = src.getImagingEnvironmentHumidity(imageIndex);
        if (imagingEnvironmentHumidityValue != null) dest.setImagingEnvironmentHumidity(imagingEnvironmentHumidityValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        Double imagingEnvironmentTemperatureValue = src.getImagingEnvironmentTemperature(imageIndex);
        if (imagingEnvironmentTemperatureValue != null) dest.setImagingEnvironmentTemperature(imagingEnvironmentTemperatureValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
      int microbeamManipulationRefCount = src.getMicrobeamManipulationRefCount(imageIndex);
      for (int microbeamManipulationRefIndex=0; microbeamManipulationRefIndex<microbeamManipulationRefCount; microbeamManipulationRefIndex++) {
      }
      } catch (NullPointerException e) { }
      try {
        int annotationRefCount = src.getPixelsAnnotationRefCount(imageIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
        {
          String pixelsAnnotationRefValue = src.getPixelsAnnotationRef(imageIndex, annotationRefIndex);
          if (pixelsAnnotationRefValue != null) dest.setPixelsAnnotationRef(pixelsAnnotationRefValue, imageIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        DimensionOrder pixelsDimensionOrderValue = src.getPixelsDimensionOrder(imageIndex);
        if (pixelsDimensionOrderValue != null) dest.setPixelsDimensionOrder(pixelsDimensionOrderValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        String pixelsIDValue = src.getPixelsID(imageIndex);
        if (pixelsIDValue != null) dest.setPixelsID(pixelsIDValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveFloat pixelsPhysicalSizeXValue = src.getPixelsPhysicalSizeX(imageIndex);
        if (pixelsPhysicalSizeXValue != null) dest.setPixelsPhysicalSizeX(pixelsPhysicalSizeXValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveFloat pixelsPhysicalSizeYValue = src.getPixelsPhysicalSizeY(imageIndex);
        if (pixelsPhysicalSizeYValue != null) dest.setPixelsPhysicalSizeY(pixelsPhysicalSizeYValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveFloat pixelsPhysicalSizeZValue = src.getPixelsPhysicalSizeZ(imageIndex);
        if (pixelsPhysicalSizeZValue != null) dest.setPixelsPhysicalSizeZ(pixelsPhysicalSizeZValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger pixelsSizeCValue = src.getPixelsSizeC(imageIndex);
        if (pixelsSizeCValue != null) dest.setPixelsSizeC(pixelsSizeCValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger pixelsSizeTValue = src.getPixelsSizeT(imageIndex);
        if (pixelsSizeTValue != null) dest.setPixelsSizeT(pixelsSizeTValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger pixelsSizeXValue = src.getPixelsSizeX(imageIndex);
        if (pixelsSizeXValue != null) dest.setPixelsSizeX(pixelsSizeXValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger pixelsSizeYValue = src.getPixelsSizeY(imageIndex);
        if (pixelsSizeYValue != null) dest.setPixelsSizeY(pixelsSizeYValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger pixelsSizeZValue = src.getPixelsSizeZ(imageIndex);
        if (pixelsSizeZValue != null) dest.setPixelsSizeZ(pixelsSizeZValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        Double pixelsTimeIncrementValue = src.getPixelsTimeIncrement(imageIndex);
        if (pixelsTimeIncrementValue != null) dest.setPixelsTimeIncrement(pixelsTimeIncrementValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        PixelType pixelsTypeValue = src.getPixelsType(imageIndex);
        if (pixelsTypeValue != null) dest.setPixelsType(pixelsTypeValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
      int binDataCount = src.getPixelsBinDataCount(imageIndex);
      for (int binDataIndex=0; binDataIndex<binDataCount; binDataIndex++) {
      try {
        Boolean pixelsBinDataBigEndianValue = src.getPixelsBinDataBigEndian(imageIndex, binDataIndex);
        if (pixelsBinDataBigEndianValue != null) dest.setPixelsBinDataBigEndian(pixelsBinDataBigEndianValue, imageIndex, binDataIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int planeCount = src.getPlaneCount(imageIndex);
      for (int planeIndex=0; planeIndex<planeCount; planeIndex++) {
      try {
        int planeAnnotationRefCount = src.getPlaneAnnotationRefCount(imageIndex, planeIndex);
        for (int annotationRefIndex=0; annotationRefIndex<planeAnnotationRefCount; annotationRefIndex++)
        {
          String planeAnnotationRefValue = src.getPlaneAnnotationRef(imageIndex, planeIndex, annotationRefIndex);
          if (planeAnnotationRefValue != null) dest.setPlaneAnnotationRef(planeAnnotationRefValue, imageIndex, planeIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        Double planeDeltaTValue = src.getPlaneDeltaT(imageIndex, planeIndex);
        if (planeDeltaTValue != null) dest.setPlaneDeltaT(planeDeltaTValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        Double planeExposureTimeValue = src.getPlaneExposureTime(imageIndex, planeIndex);
        if (planeExposureTimeValue != null) dest.setPlaneExposureTime(planeExposureTimeValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        String planeHashSHA1Value = src.getPlaneHashSHA1(imageIndex, planeIndex);
        if (planeHashSHA1Value != null) dest.setPlaneHashSHA1(planeHashSHA1Value, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        Double planePositionXValue = src.getPlanePositionX(imageIndex, planeIndex);
        if (planePositionXValue != null) dest.setPlanePositionX(planePositionXValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        Double planePositionYValue = src.getPlanePositionY(imageIndex, planeIndex);
        if (planePositionYValue != null) dest.setPlanePositionY(planePositionYValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        Double planePositionZValue = src.getPlanePositionZ(imageIndex, planeIndex);
        if (planePositionZValue != null) dest.setPlanePositionZ(planePositionZValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger planeTheCValue = src.getPlaneTheC(imageIndex, planeIndex);
        if (planeTheCValue != null) dest.setPlaneTheC(planeTheCValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger planeTheTValue = src.getPlaneTheT(imageIndex, planeIndex);
        if (planeTheTValue != null) dest.setPlaneTheT(planeTheTValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger planeTheZValue = src.getPlaneTheZ(imageIndex, planeIndex);
        if (planeTheZValue != null) dest.setPlaneTheZ(planeTheZValue, imageIndex, planeIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
        String stageLabelNameValue = src.getStageLabelName(imageIndex);
        if (stageLabelNameValue != null) dest.setStageLabelName(stageLabelNameValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        Double stageLabelXValue = src.getStageLabelX(imageIndex);
        if (stageLabelXValue != null) dest.setStageLabelX(stageLabelXValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        Double stageLabelYValue = src.getStageLabelY(imageIndex);
        if (stageLabelYValue != null) dest.setStageLabelY(stageLabelYValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
        Double stageLabelZValue = src.getStageLabelZ(imageIndex);
        if (stageLabelZValue != null) dest.setStageLabelZ(stageLabelZValue, imageIndex);
      } catch (NullPointerException e) { }
      try {
      int tiffDataCount = src.getTiffDataCount(imageIndex);
      for (int tiffDataIndex=0; tiffDataIndex<tiffDataCount; tiffDataIndex++) {
      try {
        NonNegativeInteger tiffDataFirstCValue = src.getTiffDataFirstC(imageIndex, tiffDataIndex);
        if (tiffDataFirstCValue != null) dest.setTiffDataFirstC(tiffDataFirstCValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger tiffDataFirstTValue = src.getTiffDataFirstT(imageIndex, tiffDataIndex);
        if (tiffDataFirstTValue != null) dest.setTiffDataFirstT(tiffDataFirstTValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger tiffDataFirstZValue = src.getTiffDataFirstZ(imageIndex, tiffDataIndex);
        if (tiffDataFirstZValue != null) dest.setTiffDataFirstZ(tiffDataFirstZValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger tiffDataIFDValue = src.getTiffDataIFD(imageIndex, tiffDataIndex);
        if (tiffDataIFDValue != null) dest.setTiffDataIFD(tiffDataIFDValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger tiffDataPlaneCountValue = src.getTiffDataPlaneCount(imageIndex, tiffDataIndex);
        if (tiffDataPlaneCountValue != null) dest.setTiffDataPlaneCount(tiffDataPlaneCountValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        String uuidFileNameValue = src.getUUIDFileName(imageIndex, tiffDataIndex);
        if (uuidFileNameValue != null) dest.setUUIDFileName(uuidFileNameValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      try {
        String uuidValueValue = src.getUUIDValue(imageIndex, tiffDataIndex);
        if (uuidValueValue != null) dest.setUUIDValue(uuidValueValue, imageIndex, tiffDataIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
    try {
      String instrumentIDValue = src.getInstrumentID(instrumentIndex);
      if (instrumentIDValue != null) dest.setInstrumentID(instrumentIDValue, instrumentIndex);
    } catch (NullPointerException e) { }
      try {
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
      try {
        Double detectorAmplificationGainValue = src.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
        if (detectorAmplificationGainValue != null) dest.setDetectorAmplificationGain(detectorAmplificationGainValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        Double detectorGainValue = src.getDetectorGain(instrumentIndex, detectorIndex);
        if (detectorGainValue != null) dest.setDetectorGain(detectorGainValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        String detectorIDValue = src.getDetectorID(instrumentIndex, detectorIndex);
        if (detectorIDValue != null) dest.setDetectorID(detectorIDValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        String detectorLotNumberValue = src.getDetectorLotNumber(instrumentIndex, detectorIndex);
        if (detectorLotNumberValue != null) dest.setDetectorLotNumber(detectorLotNumberValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        String detectorManufacturerValue = src.getDetectorManufacturer(instrumentIndex, detectorIndex);
        if (detectorManufacturerValue != null) dest.setDetectorManufacturer(detectorManufacturerValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        String detectorModelValue = src.getDetectorModel(instrumentIndex, detectorIndex);
        if (detectorModelValue != null) dest.setDetectorModel(detectorModelValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        Double detectorOffsetValue = src.getDetectorOffset(instrumentIndex, detectorIndex);
        if (detectorOffsetValue != null) dest.setDetectorOffset(detectorOffsetValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        String detectorSerialNumberValue = src.getDetectorSerialNumber(instrumentIndex, detectorIndex);
        if (detectorSerialNumberValue != null) dest.setDetectorSerialNumber(detectorSerialNumberValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        DetectorType detectorTypeValue = src.getDetectorType(instrumentIndex, detectorIndex);
        if (detectorTypeValue != null) dest.setDetectorType(detectorTypeValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        Double detectorVoltageValue = src.getDetectorVoltage(instrumentIndex, detectorIndex);
        if (detectorVoltageValue != null) dest.setDetectorVoltage(detectorVoltageValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      try {
        Double detectorZoomValue = src.getDetectorZoom(instrumentIndex, detectorIndex);
        if (detectorZoomValue != null) dest.setDetectorZoom(detectorZoomValue, instrumentIndex, detectorIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int dichroicCount = src.getDichroicCount(instrumentIndex);
      for (int dichroicIndex=0; dichroicIndex<dichroicCount; dichroicIndex++) {
      try {
        String dichroicIDValue = src.getDichroicID(instrumentIndex, dichroicIndex);
        if (dichroicIDValue != null) dest.setDichroicID(dichroicIDValue, instrumentIndex, dichroicIndex);
      } catch (NullPointerException e) { }
      try {
        String dichroicLotNumberValue = src.getDichroicLotNumber(instrumentIndex, dichroicIndex);
        if (dichroicLotNumberValue != null) dest.setDichroicLotNumber(dichroicLotNumberValue, instrumentIndex, dichroicIndex);
      } catch (NullPointerException e) { }
      try {
        String dichroicManufacturerValue = src.getDichroicManufacturer(instrumentIndex, dichroicIndex);
        if (dichroicManufacturerValue != null) dest.setDichroicManufacturer(dichroicManufacturerValue, instrumentIndex, dichroicIndex);
      } catch (NullPointerException e) { }
      try {
        String dichroicModelValue = src.getDichroicModel(instrumentIndex, dichroicIndex);
        if (dichroicModelValue != null) dest.setDichroicModel(dichroicModelValue, instrumentIndex, dichroicIndex);
      } catch (NullPointerException e) { }
      try {
        String dichroicSerialNumberValue = src.getDichroicSerialNumber(instrumentIndex, dichroicIndex);
        if (dichroicSerialNumberValue != null) dest.setDichroicSerialNumber(dichroicSerialNumberValue, instrumentIndex, dichroicIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int filterCount = src.getFilterCount(instrumentIndex);
      for (int filterIndex=0; filterIndex<filterCount; filterIndex++) {
      try {
        String filterFilterWheelValue = src.getFilterFilterWheel(instrumentIndex, filterIndex);
        if (filterFilterWheelValue != null) dest.setFilterFilterWheel(filterFilterWheelValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        String filterIDValue = src.getFilterID(instrumentIndex, filterIndex);
        if (filterIDValue != null) dest.setFilterID(filterIDValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        String filterLotNumberValue = src.getFilterLotNumber(instrumentIndex, filterIndex);
        if (filterLotNumberValue != null) dest.setFilterLotNumber(filterLotNumberValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        String filterManufacturerValue = src.getFilterManufacturer(instrumentIndex, filterIndex);
        if (filterManufacturerValue != null) dest.setFilterManufacturer(filterManufacturerValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        String filterModelValue = src.getFilterModel(instrumentIndex, filterIndex);
        if (filterModelValue != null) dest.setFilterModel(filterModelValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSerialNumberValue = src.getFilterSerialNumber(instrumentIndex, filterIndex);
        if (filterSerialNumberValue != null) dest.setFilterSerialNumber(filterSerialNumberValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        FilterType filterTypeValue = src.getFilterType(instrumentIndex, filterIndex);
        if (filterTypeValue != null) dest.setFilterType(filterTypeValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger transmittanceRangeCutInValue = src.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInValue != null) dest.setTransmittanceRangeCutIn(transmittanceRangeCutInValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger transmittanceRangeCutInToleranceValue = src.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutInToleranceValue != null) dest.setTransmittanceRangeCutInTolerance(transmittanceRangeCutInToleranceValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger transmittanceRangeCutOutValue = src.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutValue != null) dest.setTransmittanceRangeCutOut(transmittanceRangeCutOutValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger transmittanceRangeCutOutToleranceValue = src.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
        if (transmittanceRangeCutOutToleranceValue != null) dest.setTransmittanceRangeCutOutTolerance(transmittanceRangeCutOutToleranceValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      try {
        PercentFraction transmittanceRangeTransmittanceValue = src.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
        if (transmittanceRangeTransmittanceValue != null) dest.setTransmittanceRangeTransmittance(transmittanceRangeTransmittanceValue, instrumentIndex, filterIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int filterSetCount = src.getFilterSetCount(instrumentIndex);
      for (int filterSetIndex=0; filterSetIndex<filterSetCount; filterSetIndex++) {
      try {
        String filterSetIDValue = src.getFilterSetID(instrumentIndex, filterSetIndex);
        if (filterSetIDValue != null) dest.setFilterSetID(filterSetIDValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSetLotNumberValue = src.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
        if (filterSetLotNumberValue != null) dest.setFilterSetLotNumber(filterSetLotNumberValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSetManufacturerValue = src.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
        if (filterSetManufacturerValue != null) dest.setFilterSetManufacturer(filterSetManufacturerValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSetModelValue = src.getFilterSetModel(instrumentIndex, filterSetIndex);
        if (filterSetModelValue != null) dest.setFilterSetModel(filterSetModelValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSetSerialNumberValue = src.getFilterSetSerialNumber(instrumentIndex, filterSetIndex);
        if (filterSetSerialNumberValue != null) dest.setFilterSetSerialNumber(filterSetSerialNumberValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        String filterSetDichroicRefValue = src.getFilterSetDichroicRef(instrumentIndex, filterSetIndex);
        if (filterSetDichroicRefValue != null) dest.setFilterSetDichroicRef(filterSetDichroicRefValue, instrumentIndex, filterSetIndex);
      } catch (NullPointerException e) { }
      try {
        int emissionFilterRefCount = src.getFilterSetEmissionFilterRefCount(instrumentIndex, filterSetIndex);
        for (int emissionFilterRefIndex=0; emissionFilterRefIndex<emissionFilterRefCount; emissionFilterRefIndex++)
        {
          String filterSetEmissionFilterRefValue = src.getFilterSetEmissionFilterRef(instrumentIndex, filterSetIndex, emissionFilterRefIndex);
          if (filterSetEmissionFilterRefValue != null) dest.setFilterSetEmissionFilterRef(filterSetEmissionFilterRefValue, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        int excitationFilterRefCount = src.getFilterSetExcitationFilterRefCount(instrumentIndex, filterSetIndex);
        for (int excitationFilterRefIndex=0; excitationFilterRefIndex<excitationFilterRefCount; excitationFilterRefIndex++)
        {
          String filterSetExcitationFilterRefValue = src.getFilterSetExcitationFilterRef(instrumentIndex, filterSetIndex, excitationFilterRefIndex);
          if (filterSetExcitationFilterRefValue != null) dest.setFilterSetExcitationFilterRef(filterSetExcitationFilterRefValue, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
        }
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
        String microscopeLotNumberValue = src.getMicroscopeLotNumber(instrumentIndex);
        if (microscopeLotNumberValue != null) dest.setMicroscopeLotNumber(microscopeLotNumberValue, instrumentIndex);
      } catch (NullPointerException e) { }
      try {
        String microscopeManufacturerValue = src.getMicroscopeManufacturer(instrumentIndex);
        if (microscopeManufacturerValue != null) dest.setMicroscopeManufacturer(microscopeManufacturerValue, instrumentIndex);
      } catch (NullPointerException e) { }
      try {
        String microscopeModelValue = src.getMicroscopeModel(instrumentIndex);
        if (microscopeModelValue != null) dest.setMicroscopeModel(microscopeModelValue, instrumentIndex);
      } catch (NullPointerException e) { }
      try {
        String microscopeSerialNumberValue = src.getMicroscopeSerialNumber(instrumentIndex);
        if (microscopeSerialNumberValue != null) dest.setMicroscopeSerialNumber(microscopeSerialNumberValue, instrumentIndex);
      } catch (NullPointerException e) { }
      try {
        MicroscopeType microscopeTypeValue = src.getMicroscopeType(instrumentIndex);
        if (microscopeTypeValue != null) dest.setMicroscopeType(microscopeTypeValue, instrumentIndex);
      } catch (NullPointerException e) { }
      try {
      int objectiveCount = src.getObjectiveCount(instrumentIndex);
      for (int objectiveIndex=0; objectiveIndex<objectiveCount; objectiveIndex++) {
      try {
        Double objectiveCalibratedMagnificationValue = src.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
        if (objectiveCalibratedMagnificationValue != null) dest.setObjectiveCalibratedMagnification(objectiveCalibratedMagnificationValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        Correction objectiveCorrectionValue = src.getObjectiveCorrection(instrumentIndex, objectiveIndex);
        if (objectiveCorrectionValue != null) dest.setObjectiveCorrection(objectiveCorrectionValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        String objectiveIDValue = src.getObjectiveID(instrumentIndex, objectiveIndex);
        if (objectiveIDValue != null) dest.setObjectiveID(objectiveIDValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        Immersion objectiveImmersionValue = src.getObjectiveImmersion(instrumentIndex, objectiveIndex);
        if (objectiveImmersionValue != null) dest.setObjectiveImmersion(objectiveImmersionValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        Boolean objectiveIrisValue = src.getObjectiveIris(instrumentIndex, objectiveIndex);
        if (objectiveIrisValue != null) dest.setObjectiveIris(objectiveIrisValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        Double objectiveLensNAValue = src.getObjectiveLensNA(instrumentIndex, objectiveIndex);
        if (objectiveLensNAValue != null) dest.setObjectiveLensNA(objectiveLensNAValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        String objectiveLotNumberValue = src.getObjectiveLotNumber(instrumentIndex, objectiveIndex);
        if (objectiveLotNumberValue != null) dest.setObjectiveLotNumber(objectiveLotNumberValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        String objectiveManufacturerValue = src.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
        if (objectiveManufacturerValue != null) dest.setObjectiveManufacturer(objectiveManufacturerValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        String objectiveModelValue = src.getObjectiveModel(instrumentIndex, objectiveIndex);
        if (objectiveModelValue != null) dest.setObjectiveModel(objectiveModelValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger objectiveNominalMagnificationValue = src.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
        if (objectiveNominalMagnificationValue != null) dest.setObjectiveNominalMagnification(objectiveNominalMagnificationValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        String objectiveSerialNumberValue = src.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
        if (objectiveSerialNumberValue != null) dest.setObjectiveSerialNumber(objectiveSerialNumberValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      try {
        Double objectiveWorkingDistanceValue = src.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
        if (objectiveWorkingDistanceValue != null) dest.setObjectiveWorkingDistance(objectiveWorkingDistanceValue, instrumentIndex, objectiveIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      convertLightSources(src, dest, instrumentIndex);
    }
    } catch (NullPointerException e) { }
    try {
    int listAnnotationCount = src.getListAnnotationCount();
    for (int listAnnotationIndex=0; listAnnotationIndex<listAnnotationCount; listAnnotationIndex++) {
    try {
      //int annotationRefCount = src.getListAnnotationAnnotationRefCount(listAnnotationIndex);
      //for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      //{
      //  String listAnnotationAnnotationRefValue = src.getListAnnotationAnnotationRef(listAnnotationIndex, annotationRefIndex);
      //  if (listAnnotationAnnotationRefValue != null) dest.setListAnnotationAnnotationRef(listAnnotationAnnotationRefValue, listAnnotationIndex, annotationRefIndex);
      //}
    } catch (NullPointerException e) { }
    try {
      String listAnnotationIDValue = src.getListAnnotationID(listAnnotationIndex);
      if (listAnnotationIDValue != null) dest.setListAnnotationID(listAnnotationIDValue, listAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String listAnnotationNamespaceValue = src.getListAnnotationNamespace(listAnnotationIndex);
      if (listAnnotationNamespaceValue != null) dest.setListAnnotationNamespace(listAnnotationNamespaceValue, listAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int longAnnotationCount = src.getLongAnnotationCount();
    for (int longAnnotationIndex=0; longAnnotationIndex<longAnnotationCount; longAnnotationIndex++) {
    try {
      String longAnnotationIDValue = src.getLongAnnotationID(longAnnotationIndex);
      if (longAnnotationIDValue != null) dest.setLongAnnotationID(longAnnotationIDValue, longAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String longAnnotationNamespaceValue = src.getLongAnnotationNamespace(longAnnotationIndex);
      if (longAnnotationNamespaceValue != null) dest.setLongAnnotationNamespace(longAnnotationNamespaceValue, longAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      Long longAnnotationValueValue = src.getLongAnnotationValue(longAnnotationIndex);
      if (longAnnotationValueValue != null) dest.setLongAnnotationValue(longAnnotationValueValue, longAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int plateCount = src.getPlateCount();
    for (int plateIndex=0; plateIndex<plateCount; plateIndex++) {
    try {
      int annotationRefCount = src.getPlateAnnotationRefCount(plateIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
        String plateAnnotationRefValue = src.getPlateAnnotationRef(plateIndex, annotationRefIndex);
        if (plateAnnotationRefValue != null) dest.setPlateAnnotationRef(plateAnnotationRefValue, plateIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      NamingConvention plateColumnNamingConventionValue = src.getPlateColumnNamingConvention(plateIndex);
      if (plateColumnNamingConventionValue != null) dest.setPlateColumnNamingConvention(plateColumnNamingConventionValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      PositiveInteger plateColumnsValue = src.getPlateColumns(plateIndex);
      if (plateColumnsValue != null) dest.setPlateColumns(plateColumnsValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      String plateDescriptionValue = src.getPlateDescription(plateIndex);
      if (plateDescriptionValue != null) dest.setPlateDescription(plateDescriptionValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      String plateExternalIdentifierValue = src.getPlateExternalIdentifier(plateIndex);
      if (plateExternalIdentifierValue != null) dest.setPlateExternalIdentifier(plateExternalIdentifierValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      String plateIDValue = src.getPlateID(plateIndex);
      if (plateIDValue != null) dest.setPlateID(plateIDValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      String plateNameValue = src.getPlateName(plateIndex);
      if (plateNameValue != null) dest.setPlateName(plateNameValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      NamingConvention plateRowNamingConventionValue = src.getPlateRowNamingConvention(plateIndex);
      if (plateRowNamingConventionValue != null) dest.setPlateRowNamingConvention(plateRowNamingConventionValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      PositiveInteger plateRowsValue = src.getPlateRows(plateIndex);
      if (plateRowsValue != null) dest.setPlateRows(plateRowsValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      int screenRefCount = src.getScreenRefCount(plateIndex);
      for (int screenRefIndex=0; screenRefIndex<screenRefCount; screenRefIndex++)
      {
        String plateScreenRefValue = src.getPlateScreenRef(plateIndex, screenRefIndex);
        if (plateScreenRefValue != null) dest.setPlateScreenRef(plateScreenRefValue, plateIndex, screenRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String plateStatusValue = src.getPlateStatus(plateIndex);
      if (plateStatusValue != null) dest.setPlateStatus(plateStatusValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      Double plateWellOriginXValue = src.getPlateWellOriginX(plateIndex);
      if (plateWellOriginXValue != null) dest.setPlateWellOriginX(plateWellOriginXValue, plateIndex);
    } catch (NullPointerException e) { }
    try {
      Double plateWellOriginYValue = src.getPlateWellOriginY(plateIndex);
      if (plateWellOriginYValue != null) dest.setPlateWellOriginY(plateWellOriginYValue, plateIndex);
    } catch (NullPointerException e) { }
      try {
      int plateAcquisitionCount = src.getPlateAcquisitionCount(plateIndex);
      for (int plateAcquisitionIndex=0; plateAcquisitionIndex<plateAcquisitionCount; plateAcquisitionIndex++) {
      try {
        int annotationRefCount = src.getPlateAcquisitionAnnotationRefCount(plateIndex, plateAcquisitionIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
        {
          String plateAcquisitionAnnotationRefValue = src.getPlateAcquisitionAnnotationRef(plateIndex, plateAcquisitionIndex, annotationRefIndex);
          if (plateAcquisitionAnnotationRefValue != null) dest.setPlateAcquisitionAnnotationRef(plateAcquisitionAnnotationRefValue, plateIndex, plateAcquisitionIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        String plateAcquisitionDescriptionValue = src.getPlateAcquisitionDescription(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionDescriptionValue != null) dest.setPlateAcquisitionDescription(plateAcquisitionDescriptionValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        String plateAcquisitionEndTimeValue = src.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionEndTimeValue != null) dest.setPlateAcquisitionEndTime(plateAcquisitionEndTimeValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        String plateAcquisitionIDValue = src.getPlateAcquisitionID(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionIDValue != null) dest.setPlateAcquisitionID(plateAcquisitionIDValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        PositiveInteger plateAcquisitionMaximumFieldCountValue = src.getPlateAcquisitionMaximumFieldCount(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionMaximumFieldCountValue != null) dest.setPlateAcquisitionMaximumFieldCount(plateAcquisitionMaximumFieldCountValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        String plateAcquisitionNameValue = src.getPlateAcquisitionName(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionNameValue != null) dest.setPlateAcquisitionName(plateAcquisitionNameValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        String plateAcquisitionStartTimeValue = src.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
        if (plateAcquisitionStartTimeValue != null) dest.setPlateAcquisitionStartTime(plateAcquisitionStartTimeValue, plateIndex, plateAcquisitionIndex);
      } catch (NullPointerException e) { }
      try {
        int wellSampleRefCount = src.getWellSampleRefCount(plateIndex, plateAcquisitionIndex);
        for (int wellSampleRefIndex=0; wellSampleRefIndex<wellSampleRefCount; wellSampleRefIndex++)
        {
          String plateAcquisitionWellSampleRefValue = src.getPlateAcquisitionWellSampleRef(plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
          if (plateAcquisitionWellSampleRefValue != null) dest.setPlateAcquisitionWellSampleRef(plateAcquisitionWellSampleRefValue, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
        }
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
      try {
      int wellCount = src.getWellCount(plateIndex);
      for (int wellIndex=0; wellIndex<wellCount; wellIndex++) {
      try {
        int annotationRefCount = src.getWellAnnotationRefCount(plateIndex, wellIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
        {
          String wellAnnotationRefValue = src.getWellAnnotationRef(plateIndex, wellIndex, annotationRefIndex);
          if (wellAnnotationRefValue != null) dest.setWellAnnotationRef(wellAnnotationRefValue, plateIndex, wellIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        Integer wellColorValue = src.getWellColor(plateIndex, wellIndex);
        if (wellColorValue != null) dest.setWellColor(wellColorValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger wellColumnValue = src.getWellColumn(plateIndex, wellIndex);
        if (wellColumnValue != null) dest.setWellColumn(wellColumnValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        String wellExternalDescriptionValue = src.getWellExternalDescription(plateIndex, wellIndex);
        if (wellExternalDescriptionValue != null) dest.setWellExternalDescription(wellExternalDescriptionValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        String wellExternalIdentifierValue = src.getWellExternalIdentifier(plateIndex, wellIndex);
        if (wellExternalIdentifierValue != null) dest.setWellExternalIdentifier(wellExternalIdentifierValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        String wellIDValue = src.getWellID(plateIndex, wellIndex);
        if (wellIDValue != null) dest.setWellID(wellIDValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        String wellReagentRefValue = src.getWellReagentRef(plateIndex, wellIndex);
        if (wellReagentRefValue != null) dest.setWellReagentRef(wellReagentRefValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        NonNegativeInteger wellRowValue = src.getWellRow(plateIndex, wellIndex);
        if (wellRowValue != null) dest.setWellRow(wellRowValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
      try {
        String wellStatusValue = src.getWellStatus(plateIndex, wellIndex);
        if (wellStatusValue != null) dest.setWellStatus(wellStatusValue, plateIndex, wellIndex);
      } catch (NullPointerException e) { }
        try {
        int wellSampleCount = src.getWellSampleCount(plateIndex, wellIndex);
        for (int wellSampleIndex=0; wellSampleIndex<wellSampleCount; wellSampleIndex++) {
        try {
          int annotationRefCount = src.getWellSampleAnnotationRefCount(plateIndex, wellIndex, wellSampleIndex);
          for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
          {
            String wellSampleAnnotationRefValue = src.getWellSampleAnnotationRef(plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
            if (wellSampleAnnotationRefValue != null) dest.setWellSampleAnnotationRef(wellSampleAnnotationRefValue, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
          }
        } catch (NullPointerException e) { }
        try {
          String wellSampleIDValue = src.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIDValue != null) dest.setWellSampleID(wellSampleIDValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        try {
          String wellSampleImageRefValue = src.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleImageRefValue != null) dest.setWellSampleImageRef(wellSampleImageRefValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        try {
          NonNegativeInteger wellSampleIndexValue = src.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIndexValue != null) dest.setWellSampleIndex(wellSampleIndexValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        try {
          Double wellSamplePositionXValue = src.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePositionXValue != null) dest.setWellSamplePositionX(wellSamplePositionXValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        try {
          Double wellSamplePositionYValue = src.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
          if (wellSamplePositionYValue != null) dest.setWellSamplePositionY(wellSamplePositionYValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        try {
          String wellSampleTimepointValue = src.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleTimepointValue != null) dest.setWellSampleTimepoint(wellSampleTimepointValue, plateIndex, wellIndex, wellSampleIndex);
        } catch (NullPointerException e) { }
        }
        } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
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
    } catch (NullPointerException e) { }
    try {
      String projectExperimenterRefValue = src.getProjectExperimenterRef(projectIndex);
      if (projectExperimenterRefValue != null) dest.setProjectExperimenterRef(projectExperimenterRefValue, projectIndex);
    } catch (NullPointerException e) { }
    try {
      String projectGroupRefValue = src.getProjectGroupRef(projectIndex);
      if (projectGroupRefValue != null) dest.setProjectGroupRef(projectGroupRefValue, projectIndex);
    } catch (NullPointerException e) { }
    try {
      String projectNameValue = src.getProjectName(projectIndex);
      if (projectNameValue != null) dest.setProjectName(projectNameValue, projectIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int roiCount = src.getROICount();
    for (int roiIndex=0; roiIndex<roiCount; roiIndex++) {
    try {
      int annotationRefCount = src.getROIAnnotationRefCount(roiIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
        String roiAnnotationRefValue = src.getROIAnnotationRef(roiIndex, annotationRefIndex);
        if (roiAnnotationRefValue != null) dest.setROIAnnotationRef(roiAnnotationRefValue, roiIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String roiDescriptionValue = src.getROIDescription(roiIndex);
      if (roiDescriptionValue != null) dest.setROIDescription(roiDescriptionValue, roiIndex);
    } catch (NullPointerException e) { }
    try {
      String roiidValue = src.getROIID(roiIndex);
      if (roiidValue != null) dest.setROIID(roiidValue, roiIndex);
    } catch (NullPointerException e) { }
    try {
      String roiNameValue = src.getROIName(roiIndex);
      if (roiNameValue != null) dest.setROIName(roiNameValue, roiIndex);
    } catch (NullPointerException e) { }
    try {
      String roiNamespaceValue = src.getROINamespace(roiIndex);
      if (roiNamespaceValue != null) dest.setROINamespace(roiNamespaceValue, roiIndex);
    } catch (NullPointerException e) { }
      try {
      int shapeCount = src.getShapeCount(roiIndex);
      for (int shapeIndex=0; shapeIndex<shapeCount; shapeIndex++) {
        try {
          String ellipseDescriptionValue = src.getEllipseDescription(roiIndex, shapeIndex);
          if (ellipseDescriptionValue != null) dest.setEllipseDescription(ellipseDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer ellipseFillValue = src.getEllipseFill(roiIndex, shapeIndex);
          if (ellipseFillValue != null) dest.setEllipseFill(ellipseFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger ellipseFontSizeValue = src.getEllipseFontSize(roiIndex, shapeIndex);
          if (ellipseFontSizeValue != null) dest.setEllipseFontSize(ellipseFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String ellipseIDValue = src.getEllipseID(roiIndex, shapeIndex);
          if (ellipseIDValue != null) dest.setEllipseID(ellipseIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String ellipseLabelValue = src.getEllipseLabel(roiIndex, shapeIndex);
          if (ellipseLabelValue != null) dest.setEllipseLabel(ellipseLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String ellipseNameValue = src.getEllipseName(roiIndex, shapeIndex);
          if (ellipseNameValue != null) dest.setEllipseName(ellipseNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double ellipseRadiusXValue = src.getEllipseRadiusX(roiIndex, shapeIndex);
          if (ellipseRadiusXValue != null) dest.setEllipseRadiusX(ellipseRadiusXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double ellipseRadiusYValue = src.getEllipseRadiusY(roiIndex, shapeIndex);
          if (ellipseRadiusYValue != null) dest.setEllipseRadiusY(ellipseRadiusYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer ellipseStrokeValue = src.getEllipseStroke(roiIndex, shapeIndex);
          if (ellipseStrokeValue != null) dest.setEllipseStroke(ellipseStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String ellipseStrokeDashArrayValue = src.getEllipseStrokeDashArray(roiIndex, shapeIndex);
          if (ellipseStrokeDashArrayValue != null) dest.setEllipseStrokeDashArray(ellipseStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double ellipseStrokeWidthValue = src.getEllipseStrokeWidth(roiIndex, shapeIndex);
          if (ellipseStrokeWidthValue != null) dest.setEllipseStrokeWidth(ellipseStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger ellipseTheCValue = src.getEllipseTheC(roiIndex, shapeIndex);
          if (ellipseTheCValue != null) dest.setEllipseTheC(ellipseTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger ellipseTheTValue = src.getEllipseTheT(roiIndex, shapeIndex);
          if (ellipseTheTValue != null) dest.setEllipseTheT(ellipseTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger ellipseTheZValue = src.getEllipseTheZ(roiIndex, shapeIndex);
          if (ellipseTheZValue != null) dest.setEllipseTheZ(ellipseTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String ellipseTransformValue = src.getEllipseTransform(roiIndex, shapeIndex);
          if (ellipseTransformValue != null) dest.setEllipseTransform(ellipseTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double ellipseXValue = src.getEllipseX(roiIndex, shapeIndex);
          if (ellipseXValue != null) dest.setEllipseX(ellipseXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double ellipseYValue = src.getEllipseY(roiIndex, shapeIndex);
          if (ellipseYValue != null) dest.setEllipseY(ellipseYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineDescriptionValue = src.getLineDescription(roiIndex, shapeIndex);
          if (lineDescriptionValue != null) dest.setLineDescription(lineDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer lineFillValue = src.getLineFill(roiIndex, shapeIndex);
          if (lineFillValue != null) dest.setLineFill(lineFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger lineFontSizeValue = src.getLineFontSize(roiIndex, shapeIndex);
          if (lineFontSizeValue != null) dest.setLineFontSize(lineFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineIDValue = src.getLineID(roiIndex, shapeIndex);
          if (lineIDValue != null) dest.setLineID(lineIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineLabelValue = src.getLineLabel(roiIndex, shapeIndex);
          if (lineLabelValue != null) dest.setLineLabel(lineLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineNameValue = src.getLineName(roiIndex, shapeIndex);
          if (lineNameValue != null) dest.setLineName(lineNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer lineStrokeValue = src.getLineStroke(roiIndex, shapeIndex);
          if (lineStrokeValue != null) dest.setLineStroke(lineStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineStrokeDashArrayValue = src.getLineStrokeDashArray(roiIndex, shapeIndex);
          if (lineStrokeDashArrayValue != null) dest.setLineStrokeDashArray(lineStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double lineStrokeWidthValue = src.getLineStrokeWidth(roiIndex, shapeIndex);
          if (lineStrokeWidthValue != null) dest.setLineStrokeWidth(lineStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger lineTheCValue = src.getLineTheC(roiIndex, shapeIndex);
          if (lineTheCValue != null) dest.setLineTheC(lineTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger lineTheTValue = src.getLineTheT(roiIndex, shapeIndex);
          if (lineTheTValue != null) dest.setLineTheT(lineTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger lineTheZValue = src.getLineTheZ(roiIndex, shapeIndex);
          if (lineTheZValue != null) dest.setLineTheZ(lineTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String lineTransformValue = src.getLineTransform(roiIndex, shapeIndex);
          if (lineTransformValue != null) dest.setLineTransform(lineTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double lineX1Value = src.getLineX1(roiIndex, shapeIndex);
          if (lineX1Value != null) dest.setLineX1(lineX1Value, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double lineX2Value = src.getLineX2(roiIndex, shapeIndex);
          if (lineX2Value != null) dest.setLineX2(lineX2Value, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double lineY1Value = src.getLineY1(roiIndex, shapeIndex);
          if (lineY1Value != null) dest.setLineY1(lineY1Value, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double lineY2Value = src.getLineY2(roiIndex, shapeIndex);
          if (lineY2Value != null) dest.setLineY2(lineY2Value, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskDescriptionValue = src.getMaskDescription(roiIndex, shapeIndex);
          if (maskDescriptionValue != null) dest.setMaskDescription(maskDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer maskFillValue = src.getMaskFill(roiIndex, shapeIndex);
          if (maskFillValue != null) dest.setMaskFill(maskFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger maskFontSizeValue = src.getMaskFontSize(roiIndex, shapeIndex);
          if (maskFontSizeValue != null) dest.setMaskFontSize(maskFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskIDValue = src.getMaskID(roiIndex, shapeIndex);
          if (maskIDValue != null) dest.setMaskID(maskIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskLabelValue = src.getMaskLabel(roiIndex, shapeIndex);
          if (maskLabelValue != null) dest.setMaskLabel(maskLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskNameValue = src.getMaskName(roiIndex, shapeIndex);
          if (maskNameValue != null) dest.setMaskName(maskNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer maskStrokeValue = src.getMaskStroke(roiIndex, shapeIndex);
          if (maskStrokeValue != null) dest.setMaskStroke(maskStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskStrokeDashArrayValue = src.getMaskStrokeDashArray(roiIndex, shapeIndex);
          if (maskStrokeDashArrayValue != null) dest.setMaskStrokeDashArray(maskStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double maskStrokeWidthValue = src.getMaskStrokeWidth(roiIndex, shapeIndex);
          if (maskStrokeWidthValue != null) dest.setMaskStrokeWidth(maskStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger maskTheCValue = src.getMaskTheC(roiIndex, shapeIndex);
          if (maskTheCValue != null) dest.setMaskTheC(maskTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger maskTheTValue = src.getMaskTheT(roiIndex, shapeIndex);
          if (maskTheTValue != null) dest.setMaskTheT(maskTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger maskTheZValue = src.getMaskTheZ(roiIndex, shapeIndex);
          if (maskTheZValue != null) dest.setMaskTheZ(maskTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String maskTransformValue = src.getMaskTransform(roiIndex, shapeIndex);
          if (maskTransformValue != null) dest.setMaskTransform(maskTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double maskXValue = src.getMaskX(roiIndex, shapeIndex);
          if (maskXValue != null) dest.setMaskX(maskXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double maskYValue = src.getMaskY(roiIndex, shapeIndex);
          if (maskYValue != null) dest.setMaskY(maskYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathDefinitionValue = src.getPathDefinition(roiIndex, shapeIndex);
          if (pathDefinitionValue != null) dest.setPathDefinition(pathDefinitionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathDescriptionValue = src.getPathDescription(roiIndex, shapeIndex);
          if (pathDescriptionValue != null) dest.setPathDescription(pathDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer pathFillValue = src.getPathFill(roiIndex, shapeIndex);
          if (pathFillValue != null) dest.setPathFill(pathFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pathFontSizeValue = src.getPathFontSize(roiIndex, shapeIndex);
          if (pathFontSizeValue != null) dest.setPathFontSize(pathFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathIDValue = src.getPathID(roiIndex, shapeIndex);
          if (pathIDValue != null) dest.setPathID(pathIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathLabelValue = src.getPathLabel(roiIndex, shapeIndex);
          if (pathLabelValue != null) dest.setPathLabel(pathLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathNameValue = src.getPathName(roiIndex, shapeIndex);
          if (pathNameValue != null) dest.setPathName(pathNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer pathStrokeValue = src.getPathStroke(roiIndex, shapeIndex);
          if (pathStrokeValue != null) dest.setPathStroke(pathStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathStrokeDashArrayValue = src.getPathStrokeDashArray(roiIndex, shapeIndex);
          if (pathStrokeDashArrayValue != null) dest.setPathStrokeDashArray(pathStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double pathStrokeWidthValue = src.getPathStrokeWidth(roiIndex, shapeIndex);
          if (pathStrokeWidthValue != null) dest.setPathStrokeWidth(pathStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pathTheCValue = src.getPathTheC(roiIndex, shapeIndex);
          if (pathTheCValue != null) dest.setPathTheC(pathTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pathTheTValue = src.getPathTheT(roiIndex, shapeIndex);
          if (pathTheTValue != null) dest.setPathTheT(pathTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pathTheZValue = src.getPathTheZ(roiIndex, shapeIndex);
          if (pathTheZValue != null) dest.setPathTheZ(pathTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pathTransformValue = src.getPathTransform(roiIndex, shapeIndex);
          if (pathTransformValue != null) dest.setPathTransform(pathTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointDescriptionValue = src.getPointDescription(roiIndex, shapeIndex);
          if (pointDescriptionValue != null) dest.setPointDescription(pointDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer pointFillValue = src.getPointFill(roiIndex, shapeIndex);
          if (pointFillValue != null) dest.setPointFill(pointFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pointFontSizeValue = src.getPointFontSize(roiIndex, shapeIndex);
          if (pointFontSizeValue != null) dest.setPointFontSize(pointFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointIDValue = src.getPointID(roiIndex, shapeIndex);
          if (pointIDValue != null) dest.setPointID(pointIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointLabelValue = src.getPointLabel(roiIndex, shapeIndex);
          if (pointLabelValue != null) dest.setPointLabel(pointLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointNameValue = src.getPointName(roiIndex, shapeIndex);
          if (pointNameValue != null) dest.setPointName(pointNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer pointStrokeValue = src.getPointStroke(roiIndex, shapeIndex);
          if (pointStrokeValue != null) dest.setPointStroke(pointStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointStrokeDashArrayValue = src.getPointStrokeDashArray(roiIndex, shapeIndex);
          if (pointStrokeDashArrayValue != null) dest.setPointStrokeDashArray(pointStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double pointStrokeWidthValue = src.getPointStrokeWidth(roiIndex, shapeIndex);
          if (pointStrokeWidthValue != null) dest.setPointStrokeWidth(pointStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pointTheCValue = src.getPointTheC(roiIndex, shapeIndex);
          if (pointTheCValue != null) dest.setPointTheC(pointTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pointTheTValue = src.getPointTheT(roiIndex, shapeIndex);
          if (pointTheTValue != null) dest.setPointTheT(pointTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger pointTheZValue = src.getPointTheZ(roiIndex, shapeIndex);
          if (pointTheZValue != null) dest.setPointTheZ(pointTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String pointTransformValue = src.getPointTransform(roiIndex, shapeIndex);
          if (pointTransformValue != null) dest.setPointTransform(pointTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double pointXValue = src.getPointX(roiIndex, shapeIndex);
          if (pointXValue != null) dest.setPointX(pointXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double pointYValue = src.getPointY(roiIndex, shapeIndex);
          if (pointYValue != null) dest.setPointY(pointYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Boolean polylineClosedValue = src.getPolylineClosed(roiIndex, shapeIndex);
          if (polylineClosedValue != null) dest.setPolylineClosed(polylineClosedValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineDescriptionValue = src.getPolylineDescription(roiIndex, shapeIndex);
          if (polylineDescriptionValue != null) dest.setPolylineDescription(polylineDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer polylineFillValue = src.getPolylineFill(roiIndex, shapeIndex);
          if (polylineFillValue != null) dest.setPolylineFill(polylineFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger polylineFontSizeValue = src.getPolylineFontSize(roiIndex, shapeIndex);
          if (polylineFontSizeValue != null) dest.setPolylineFontSize(polylineFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineIDValue = src.getPolylineID(roiIndex, shapeIndex);
          if (polylineIDValue != null) dest.setPolylineID(polylineIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineLabelValue = src.getPolylineLabel(roiIndex, shapeIndex);
          if (polylineLabelValue != null) dest.setPolylineLabel(polylineLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineNameValue = src.getPolylineName(roiIndex, shapeIndex);
          if (polylineNameValue != null) dest.setPolylineName(polylineNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylinePointsValue = src.getPolylinePoints(roiIndex, shapeIndex);
          if (polylinePointsValue != null) dest.setPolylinePoints(polylinePointsValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer polylineStrokeValue = src.getPolylineStroke(roiIndex, shapeIndex);
          if (polylineStrokeValue != null) dest.setPolylineStroke(polylineStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineStrokeDashArrayValue = src.getPolylineStrokeDashArray(roiIndex, shapeIndex);
          if (polylineStrokeDashArrayValue != null) dest.setPolylineStrokeDashArray(polylineStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double polylineStrokeWidthValue = src.getPolylineStrokeWidth(roiIndex, shapeIndex);
          if (polylineStrokeWidthValue != null) dest.setPolylineStrokeWidth(polylineStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger polylineTheCValue = src.getPolylineTheC(roiIndex, shapeIndex);
          if (polylineTheCValue != null) dest.setPolylineTheC(polylineTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger polylineTheTValue = src.getPolylineTheT(roiIndex, shapeIndex);
          if (polylineTheTValue != null) dest.setPolylineTheT(polylineTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger polylineTheZValue = src.getPolylineTheZ(roiIndex, shapeIndex);
          if (polylineTheZValue != null) dest.setPolylineTheZ(polylineTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String polylineTransformValue = src.getPolylineTransform(roiIndex, shapeIndex);
          if (polylineTransformValue != null) dest.setPolylineTransform(polylineTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleDescriptionValue = src.getRectangleDescription(roiIndex, shapeIndex);
          if (rectangleDescriptionValue != null) dest.setRectangleDescription(rectangleDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer rectangleFillValue = src.getRectangleFill(roiIndex, shapeIndex);
          if (rectangleFillValue != null) dest.setRectangleFill(rectangleFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger rectangleFontSizeValue = src.getRectangleFontSize(roiIndex, shapeIndex);
          if (rectangleFontSizeValue != null) dest.setRectangleFontSize(rectangleFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double rectangleHeightValue = src.getRectangleHeight(roiIndex, shapeIndex);
          if (rectangleHeightValue != null) dest.setRectangleHeight(rectangleHeightValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleIDValue = src.getRectangleID(roiIndex, shapeIndex);
          if (rectangleIDValue != null) dest.setRectangleID(rectangleIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleLabelValue = src.getRectangleLabel(roiIndex, shapeIndex);
          if (rectangleLabelValue != null) dest.setRectangleLabel(rectangleLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleNameValue = src.getRectangleName(roiIndex, shapeIndex);
          if (rectangleNameValue != null) dest.setRectangleName(rectangleNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer rectangleStrokeValue = src.getRectangleStroke(roiIndex, shapeIndex);
          if (rectangleStrokeValue != null) dest.setRectangleStroke(rectangleStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleStrokeDashArrayValue = src.getRectangleStrokeDashArray(roiIndex, shapeIndex);
          if (rectangleStrokeDashArrayValue != null) dest.setRectangleStrokeDashArray(rectangleStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double rectangleStrokeWidthValue = src.getRectangleStrokeWidth(roiIndex, shapeIndex);
          if (rectangleStrokeWidthValue != null) dest.setRectangleStrokeWidth(rectangleStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger rectangleTheCValue = src.getRectangleTheC(roiIndex, shapeIndex);
          if (rectangleTheCValue != null) dest.setRectangleTheC(rectangleTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger rectangleTheTValue = src.getRectangleTheT(roiIndex, shapeIndex);
          if (rectangleTheTValue != null) dest.setRectangleTheT(rectangleTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger rectangleTheZValue = src.getRectangleTheZ(roiIndex, shapeIndex);
          if (rectangleTheZValue != null) dest.setRectangleTheZ(rectangleTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String rectangleTransformValue = src.getRectangleTransform(roiIndex, shapeIndex);
          if (rectangleTransformValue != null) dest.setRectangleTransform(rectangleTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double rectangleWidthValue = src.getRectangleWidth(roiIndex, shapeIndex);
          if (rectangleWidthValue != null) dest.setRectangleWidth(rectangleWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double rectangleXValue = src.getRectangleX(roiIndex, shapeIndex);
          if (rectangleXValue != null) dest.setRectangleX(rectangleXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double rectangleYValue = src.getRectangleY(roiIndex, shapeIndex);
          if (rectangleYValue != null) dest.setRectangleY(rectangleYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textDescriptionValue = src.getTextDescription(roiIndex, shapeIndex);
          if (textDescriptionValue != null) dest.setTextDescription(textDescriptionValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer textFillValue = src.getTextFill(roiIndex, shapeIndex);
          if (textFillValue != null) dest.setTextFill(textFillValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger textFontSizeValue = src.getTextFontSize(roiIndex, shapeIndex);
          if (textFontSizeValue != null) dest.setTextFontSize(textFontSizeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textIDValue = src.getTextID(roiIndex, shapeIndex);
          if (textIDValue != null) dest.setTextID(textIDValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textLabelValue = src.getTextLabel(roiIndex, shapeIndex);
          if (textLabelValue != null) dest.setTextLabel(textLabelValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textNameValue = src.getTextName(roiIndex, shapeIndex);
          if (textNameValue != null) dest.setTextName(textNameValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Integer textStrokeValue = src.getTextStroke(roiIndex, shapeIndex);
          if (textStrokeValue != null) dest.setTextStroke(textStrokeValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textStrokeDashArrayValue = src.getTextStrokeDashArray(roiIndex, shapeIndex);
          if (textStrokeDashArrayValue != null) dest.setTextStrokeDashArray(textStrokeDashArrayValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double textStrokeWidthValue = src.getTextStrokeWidth(roiIndex, shapeIndex);
          if (textStrokeWidthValue != null) dest.setTextStrokeWidth(textStrokeWidthValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger textTheCValue = src.getTextTheC(roiIndex, shapeIndex);
          if (textTheCValue != null) dest.setTextTheC(textTheCValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger textTheTValue = src.getTextTheT(roiIndex, shapeIndex);
          if (textTheTValue != null) dest.setTextTheT(textTheTValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          NonNegativeInteger textTheZValue = src.getTextTheZ(roiIndex, shapeIndex);
          if (textTheZValue != null) dest.setTextTheZ(textTheZValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textTransformValue = src.getTextTransform(roiIndex, shapeIndex);
          if (textTransformValue != null) dest.setTextTransform(textTransformValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          String textValueValue = src.getTextValue(roiIndex, shapeIndex);
          if (textValueValue != null) dest.setTextValue(textValueValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double textXValue = src.getTextX(roiIndex, shapeIndex);
          if (textXValue != null) dest.setTextX(textXValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
        try {
          Double textYValue = src.getTextY(roiIndex, shapeIndex);
          if (textYValue != null) dest.setTextY(textYValue, roiIndex, shapeIndex);
        } catch (NullPointerException e) { }
        catch (ClassCastException e) { }
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int screenCount = src.getScreenCount();
    for (int screenIndex=0; screenIndex<screenCount; screenIndex++) {
    try {
      int annotationRefCount = src.getScreenAnnotationRefCount(screenIndex);
      for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
      {
        String screenAnnotationRefValue = src.getScreenAnnotationRef(screenIndex, annotationRefIndex);
        if (screenAnnotationRefValue != null) dest.setScreenAnnotationRef(screenAnnotationRefValue, screenIndex, annotationRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String screenDescriptionValue = src.getScreenDescription(screenIndex);
      if (screenDescriptionValue != null) dest.setScreenDescription(screenDescriptionValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenIDValue = src.getScreenID(screenIndex);
      if (screenIDValue != null) dest.setScreenID(screenIDValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenNameValue = src.getScreenName(screenIndex);
      if (screenNameValue != null) dest.setScreenName(screenNameValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      int plateRefCount = src.getPlateRefCount(screenIndex);
      for (int plateRefIndex=0; plateRefIndex<plateRefCount; plateRefIndex++) {
        String screenPlateRefValue = src.getScreenPlateRef(screenIndex, plateRefIndex);
        if (screenPlateRefValue != null) dest.setScreenPlateRef(screenPlateRefValue, screenIndex, plateRefIndex);
      }
    } catch (NullPointerException e) { }
    try {
      String screenProtocolDescriptionValue = src.getScreenProtocolDescription(screenIndex);
      if (screenProtocolDescriptionValue != null) dest.setScreenProtocolDescription(screenProtocolDescriptionValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenProtocolIdentifierValue = src.getScreenProtocolIdentifier(screenIndex);
      if (screenProtocolIdentifierValue != null) dest.setScreenProtocolIdentifier(screenProtocolIdentifierValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenReagentSetDescriptionValue = src.getScreenReagentSetDescription(screenIndex);
      if (screenReagentSetDescriptionValue != null) dest.setScreenReagentSetDescription(screenReagentSetDescriptionValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenReagentSetIdentifierValue = src.getScreenReagentSetIdentifier(screenIndex);
      if (screenReagentSetIdentifierValue != null) dest.setScreenReagentSetIdentifier(screenReagentSetIdentifierValue, screenIndex);
    } catch (NullPointerException e) { }
    try {
      String screenTypeValue = src.getScreenType(screenIndex);
      if (screenTypeValue != null) dest.setScreenType(screenTypeValue, screenIndex);
    } catch (NullPointerException e) { }
      try {
      int reagentCount = src.getReagentCount(screenIndex);
      for (int reagentIndex=0; reagentIndex<reagentCount; reagentIndex++) {
      try {
        int annotationRefCount = src.getReagentAnnotationRefCount(screenIndex, reagentIndex);
        for (int annotationRefIndex=0; annotationRefIndex<annotationRefCount; annotationRefIndex++)
        {
          String reagentAnnotationRefValue = src.getReagentAnnotationRef(screenIndex, reagentIndex, annotationRefIndex);
          if (reagentAnnotationRefValue != null) dest.setReagentAnnotationRef(reagentAnnotationRefValue, screenIndex, reagentIndex, annotationRefIndex);
        }
      } catch (NullPointerException e) { }
      try {
        String reagentDescriptionValue = src.getReagentDescription(screenIndex, reagentIndex);
        if (reagentDescriptionValue != null) dest.setReagentDescription(reagentDescriptionValue, screenIndex, reagentIndex);
      } catch (NullPointerException e) { }
      try {
        String reagentIDValue = src.getReagentID(screenIndex, reagentIndex);
        if (reagentIDValue != null) dest.setReagentID(reagentIDValue, screenIndex, reagentIndex);
      } catch (NullPointerException e) { }
      try {
        String reagentNameValue = src.getReagentName(screenIndex, reagentIndex);
        if (reagentNameValue != null) dest.setReagentName(reagentNameValue, screenIndex, reagentIndex);
      } catch (NullPointerException e) { }
      try {
        String reagentReagentIdentifierValue = src.getReagentReagentIdentifier(screenIndex, reagentIndex);
        if (reagentReagentIdentifierValue != null) dest.setReagentReagentIdentifier(reagentReagentIdentifierValue, screenIndex, reagentIndex);
      } catch (NullPointerException e) { }
      }
      } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int commentAnnotationCount = src.getCommentAnnotationCount();
    for (int stringAnnotationIndex=0; stringAnnotationIndex<commentAnnotationCount; stringAnnotationIndex++) {
    try {
      String stringAnnotationIDValue = src.getCommentAnnotationID(stringAnnotationIndex);
      if (stringAnnotationIDValue != null) dest.setCommentAnnotationID(stringAnnotationIDValue, stringAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String stringAnnotationNamespaceValue = src.getCommentAnnotationNamespace(stringAnnotationIndex);
      if (stringAnnotationNamespaceValue != null) dest.setCommentAnnotationNamespace(stringAnnotationNamespaceValue, stringAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String stringAnnotationValueValue = src.getCommentAnnotationValue(stringAnnotationIndex);
      if (stringAnnotationValueValue != null) dest.setCommentAnnotationValue(stringAnnotationValueValue, stringAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int timestampAnnotationCount = src.getTimestampAnnotationCount();
    for (int timestampAnnotationIndex=0; timestampAnnotationIndex<timestampAnnotationCount; timestampAnnotationIndex++) {
    try {
      String timestampAnnotationIDValue = src.getTimestampAnnotationID(timestampAnnotationIndex);
      if (timestampAnnotationIDValue != null) dest.setTimestampAnnotationID(timestampAnnotationIDValue, timestampAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String timestampAnnotationNamespaceValue = src.getTimestampAnnotationNamespace(timestampAnnotationIndex);
      if (timestampAnnotationNamespaceValue != null) dest.setTimestampAnnotationNamespace(timestampAnnotationNamespaceValue, timestampAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String timestampAnnotationValueValue = src.getTimestampAnnotationValue(timestampAnnotationIndex);
      if (timestampAnnotationValueValue != null) dest.setTimestampAnnotationValue(timestampAnnotationValueValue, timestampAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int xmlAnnotationCount = src.getXMLAnnotationCount();
    for (int xmlAnnotationIndex=0; xmlAnnotationIndex<xmlAnnotationCount; xmlAnnotationIndex++) {
    try {
      String xmlAnnotationIDValue = src.getXMLAnnotationID(xmlAnnotationIndex);
      if (xmlAnnotationIDValue != null) dest.setXMLAnnotationID(xmlAnnotationIDValue, xmlAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String xmlAnnotationNamespaceValue = src.getXMLAnnotationNamespace(xmlAnnotationIndex);
      if (xmlAnnotationNamespaceValue != null) dest.setXMLAnnotationNamespace(xmlAnnotationNamespaceValue, xmlAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String xmlAnnotationValueValue = src.getXMLAnnotationValue(xmlAnnotationIndex);
      if (xmlAnnotationValueValue != null) dest.setXMLAnnotationValue(xmlAnnotationValueValue, xmlAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int tagAnnotationCount = src.getTagAnnotationCount();
    for (int tagAnnotationIndex=0; tagAnnotationIndex<tagAnnotationCount; tagAnnotationIndex++) {
    try {
      String tagAnnotationIDValue = src.getTagAnnotationID(tagAnnotationIndex);
      if (tagAnnotationIDValue != null) dest.setTagAnnotationID(tagAnnotationIDValue, tagAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String tagAnnotationNamespaceValue = src.getTagAnnotationNamespace(tagAnnotationIndex);
      if (tagAnnotationNamespaceValue != null) dest.setTagAnnotationNamespace(tagAnnotationNamespaceValue, tagAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String tagAnnotationValueValue = src.getTagAnnotationValue(tagAnnotationIndex);
      if (tagAnnotationValueValue != null) dest.setTagAnnotationValue(tagAnnotationValueValue, tagAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
    try {
    int termAnnotationCount = src.getTermAnnotationCount();
    for (int termAnnotationIndex=0; termAnnotationIndex<termAnnotationCount; termAnnotationIndex++) {
    try {
      String termAnnotationIDValue = src.getTermAnnotationID(termAnnotationIndex);
      if (termAnnotationIDValue != null) dest.setTermAnnotationID(termAnnotationIDValue, termAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String termAnnotationNamespaceValue = src.getTermAnnotationNamespace(termAnnotationIndex);
      if (termAnnotationNamespaceValue != null) dest.setTermAnnotationNamespace(termAnnotationNamespaceValue, termAnnotationIndex);
    } catch (NullPointerException e) { }
    try {
      String termAnnotationValueValue = src.getTermAnnotationValue(termAnnotationIndex);
      if (termAnnotationValueValue != null) dest.setTermAnnotationValue(termAnnotationValueValue, termAnnotationIndex);
    } catch (NullPointerException e) { }
    }
    } catch (NullPointerException e) { }
  }

  // -- Helper methods --

  private static void convertLightSources(MetadataRetrieve src,
    MetadataStore dest, int instrumentIndex)
  {
    int lightSourceCount = src.getLightSourceCount(instrumentIndex);

    for (int lightSource=0; lightSource<lightSourceCount; lightSource++) {
      String type = src.getLightSourceType(instrumentIndex, lightSource);
      if (type.equals("Arc")) {
        try {
          String id = src.getArcID(instrumentIndex, lightSource);
          if (id != null) dest.setArcID(id, instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getArcLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setArcLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getArcManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setArcManufacturer(manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getArcModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setArcModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Double power = src.getArcPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setArcPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getArcSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setArcSerialNumber(serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          ArcType arcType = src.getArcType(instrumentIndex, lightSource);
          if (arcType != null) {
            dest.setArcType(arcType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }
      }
      else if (type.equals("Filament")) {
        try {
          String id = src.getFilamentID(instrumentIndex, lightSource);
          if (id != null) dest.setFilamentID(id, instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber =
            src.getFilamentLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setFilamentLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getFilamentManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setFilamentManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getFilamentModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setFilamentModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Double power = src.getFilamentPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setFilamentPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getFilamentSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setFilamentSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          FilamentType filamentType =
            src.getFilamentType(instrumentIndex, lightSource);
          if (filamentType != null) {
            dest.setFilamentType(filamentType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }
      }
      else if (type.equals("Laser")) {
        try {
          String id = src.getLaserID(instrumentIndex, lightSource);
          if (id != null) dest.setLaserID(id, instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber =
            src.getLaserLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setLaserLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getLaserManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setLaserManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getLaserModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setLaserModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Double power = src.getLaserPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setLaserPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getLaserSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setLaserSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          LaserType laserType = src.getLaserType(instrumentIndex, lightSource);
          if (laserType != null) {
            dest.setLaserType(laserType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger frequencyMultiplication =
            src.getLaserFrequencyMultiplication(instrumentIndex, lightSource);
          if (frequencyMultiplication != null) {
            dest.setLaserFrequencyMultiplication(
              frequencyMultiplication, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          LaserMedium medium =
            src.getLaserLaserMedium(instrumentIndex, lightSource);
          if (medium != null) {
            dest.setLaserLaserMedium(medium, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Boolean pockelCell =
            src.getLaserPockelCell(instrumentIndex, lightSource);
          if (pockelCell != null) {
            dest.setLaserPockelCell(pockelCell, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Pulse pulse = src.getLaserPulse(instrumentIndex, lightSource);
          if (pulse != null) {
            dest.setLaserPulse(pulse, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String pump = src.getLaserPump(instrumentIndex, lightSource);
          if (pump != null) {
            dest.setLaserPump(pump, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Double repetitionRate =
            src.getLaserRepetitionRate(instrumentIndex, lightSource);
          if (repetitionRate != null) {
            dest.setLaserRepetitionRate(
              repetitionRate, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Boolean tuneable = src.getLaserTuneable(instrumentIndex, lightSource);
          if (tuneable != null) {
            dest.setLaserTuneable(tuneable, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger wavelength =
            src.getLaserWavelength(instrumentIndex, lightSource);
          if (wavelength != null) {
            dest.setLaserWavelength(wavelength, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }
      }
      else if (type.equals("LightEmittingDiode")) {
        try {
          String id = src.getLightEmittingDiodeID(instrumentIndex, lightSource);
          if (id != null) {
            dest.setLightEmittingDiodeID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber =
            src.getLightEmittingDiodeLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setLightEmittingDiodeLotNumber(
              lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getLightEmittingDiodeManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setLightEmittingDiodeManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model =
            src.getLightEmittingDiodeModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setLightEmittingDiodeModel(
              model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Double power =
            src.getLightEmittingDiodePower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setLightEmittingDiodePower(
              power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getLightEmittingDiodeSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setLightEmittingDiodeSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }
      }
    }
  }

}
