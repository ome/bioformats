/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2014 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#include <ome/xml/meta/Convert.h>

#include <ome/xml/model/AffineTransform.h>

namespace
{

  using namespace ome::xml::meta;
  using namespace ome::xml::model::enums;
  using namespace ome::xml::model::primitives;
  using ome::xml::model::AffineTransform;

  typedef ome::xml::meta::BaseMetadata::index_type index_type;
  typedef ome::xml::model::MapPairs::map_type map_pairs_map_type;

  template<typename T>
  bool
  transfer(const MetadataRetrieve&                         src,
           MetadataStore&                                  dest,
           T                          (MetadataRetrieve::* get)() const,
           void                       (MetadataStore::*    set)(T value))
  {
    bool ok = true;
    try
      {
        (dest.*set)((src.*get)());
      }
    catch (const std::runtime_error& /* e */)
      {
        ok = false;
      }
    return ok;
  }

  template<typename T, typename P>
  bool
  transfer(const MetadataRetrieve&                         src,
           MetadataStore&                                  dest,
           T                          (MetadataRetrieve::* get)(P param) const,
           void                       (MetadataStore::*    set)(T value, P param),
           P                                               param)
  {
    bool ok = true;
    try
      {
        (dest.*set)((src.*get)(param),
                    param);
      }
    catch (const std::runtime_error& /* e */)
      {
        ok = false;
      }
    return ok;
  }

  template<typename T, typename P>
  bool
  transfer(const MetadataRetrieve&                         src,
           MetadataStore&                                  dest,
           T                          (MetadataRetrieve::* get)(P param1,
                                                                P param2) const,
           void                       (MetadataStore::*    set)(T value,
                                                                P param1,
                                                                P param2),
           P                                               param1,
           P                                               param2)
  {
    bool ok = true;
    try
      {
        (dest.*set)((src.*get)(param1, param2),
                    param1, param2);
      }
    catch (const std::runtime_error& /* e */)
      {
        ok = false;
      }
    return ok;
  }

  template<typename T, typename P>
  bool
  transfer(const MetadataRetrieve&                         src,
           MetadataStore&                                  dest,
           T                          (MetadataRetrieve::* get)(P param1,
                                                                P param2,
                                                                P param3) const,
           void                       (MetadataStore::*    set)(T value,
                                                                P param1,
                                                                P param2,
                                                                P param3),
           P                                               param1,
           P                                               param2,
           P                                               param3)
  {
    bool ok = true;
    try
      {
        (dest.*set)((src.*get)(param1, param2, param3),
                    param1, param2, param3);
      }
    catch (const std::runtime_error& /* e */)
      {
        ok = false;
      }
    return ok;
  }

  void
  convertBooleanAnnotations(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type booleanAnnotationCount(src.getBooleanAnnotationCount());
    for (index_type i = 0; i < booleanAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getBooleanAnnotationID,
                 &MetadataStore::setBooleanAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBooleanAnnotationDescription,
                 &MetadataStore::setBooleanAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBooleanAnnotationNamespace,
                 &MetadataStore::setBooleanAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBooleanAnnotationValue,
                 &MetadataStore::setBooleanAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBooleanAnnotationAnnotator,
                 &MetadataStore::setBooleanAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getBooleanAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getBooleanAnnotationAnnotationRef,
                     &MetadataStore::setBooleanAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertCommentAnnotations(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type commentAnnotationCount(src.getCommentAnnotationCount());
    for (index_type i = 0; i < commentAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getCommentAnnotationID,
                 &MetadataStore::setCommentAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getCommentAnnotationDescription,
                 &MetadataStore::setCommentAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getCommentAnnotationNamespace,
                 &MetadataStore::setCommentAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getCommentAnnotationValue,
                 &MetadataStore::setCommentAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getCommentAnnotationAnnotator,
                 &MetadataStore::setCommentAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getCommentAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getCommentAnnotationAnnotationRef,
                     &MetadataStore::setCommentAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertDatasets(const MetadataRetrieve& src,
                  MetadataStore&          dest)
  {
    index_type datasets(src.getDatasetCount());
    for (index_type i = 0; i < datasets; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getDatasetID,
                 &MetadataStore::setDatasetID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDatasetDescription,
                 &MetadataStore::setDatasetDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDatasetExperimenterGroupRef,
                 &MetadataStore::setDatasetExperimenterGroupRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDatasetExperimenterRef,
                 &MetadataStore::setDatasetExperimenterRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDatasetName,
                 &MetadataStore::setDatasetName,
                 i);

        index_type imageRefCount(src.getDatasetImageRefCount(i));
        for (index_type q = 0; q < imageRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getDatasetImageRef,
                     &MetadataStore::setDatasetImageRef,
                     i, q);
          }

        index_type annotationRefCount = src.getDatasetAnnotationRefCount(i);
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getDatasetAnnotationRef,
                     &MetadataStore::setDatasetAnnotationRef,
                     i, q);
          }
      }
  }

  void
  convertDoubleAnnotations(const MetadataRetrieve& src,
                           MetadataStore&          dest)
  {
    index_type doubleAnnotationCount(src.getDoubleAnnotationCount());
    for (index_type i = 0; i < doubleAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getDoubleAnnotationID,
                 &MetadataStore::setDoubleAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDoubleAnnotationDescription,
                 &MetadataStore::setDoubleAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDoubleAnnotationNamespace,
                 &MetadataStore::setDoubleAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDoubleAnnotationValue,
                 &MetadataStore::setDoubleAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getDoubleAnnotationAnnotator,
                 &MetadataStore::setDoubleAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getDoubleAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getDoubleAnnotationAnnotationRef,
                     &MetadataStore::setDoubleAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertExperiments(const MetadataRetrieve& src,
                     MetadataStore&          dest)
  {
    index_type experimentCount(src.getExperimentCount());
    for (index_type i = 0; i < experimentCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getExperimentID,
                 &MetadataStore::setExperimentID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimentDescription,
                 &MetadataStore::setExperimentDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimentDescription,
                 &MetadataStore::setExperimentDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimentExperimenterRef,
                 &MetadataStore::setExperimentExperimenterRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimentType,
                 &MetadataStore::setExperimentType,
                 i);

        index_type microbeamCount(src.getMicrobeamManipulationCount(i));
        for (index_type q = 0; q < microbeamCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getMicrobeamManipulationID,
                     &MetadataStore::setMicrobeamManipulationID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getMicrobeamManipulationDescription,
                     &MetadataStore::setMicrobeamManipulationDescription,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getMicrobeamManipulationExperimenterRef,
                     &MetadataStore::setMicrobeamManipulationExperimenterRef,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getMicrobeamManipulationType,
                     &MetadataStore::setMicrobeamManipulationType,
                     i, q);

            index_type lightSourceCount(src.getMicrobeamManipulationLightSourceSettingsCount(i, q));
            for (index_type p = 0; p < lightSourceCount; ++p)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsID,
                         &MetadataStore::setMicrobeamManipulationLightSourceSettingsID,
                         i, q, p);
                transfer(src, dest,
                         &MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsAttenuation,
                         &MetadataStore::setMicrobeamManipulationLightSourceSettingsAttenuation,
                         i, q, p);
                transfer(src, dest,
                         &MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsWavelength,
                         &MetadataStore::setMicrobeamManipulationLightSourceSettingsWavelength,
                         i, q, p);
              }

            index_type roiRefCount(src.getMicrobeamManipulationROIRefCount(i, q));
            for (index_type p = 0; p < roiRefCount; ++p)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getMicrobeamManipulationROIRef,
                         &MetadataStore::setMicrobeamManipulationROIRef,
                         i, q, p);
              }
          }
      }
  }

  void
  convertExperimenters(const MetadataRetrieve& src,
                       MetadataStore&          dest)
  {
    index_type experimenterCount(src.getExperimenterCount());
    for (index_type i = 0; i < experimenterCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterID,
                 &MetadataStore::setExperimenterID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterEmail,
                 &MetadataStore::setExperimenterEmail,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterFirstName,
                 &MetadataStore::setExperimenterFirstName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterInstitution,
                 &MetadataStore::setExperimenterInstitution,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterLastName,
                 &MetadataStore::setExperimenterLastName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterMiddleName,
                 &MetadataStore::setExperimenterMiddleName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterUserName,
                 &MetadataStore::setExperimenterUserName,
                 i);

        index_type annotationRefCount(src.getExperimenterAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getExperimenterAnnotationRef,
                     &MetadataStore::setExperimenterAnnotationRef,
                     i, q);
          }
      }
  }

  void
  convertExperimenterGroups(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type experimenterGroupCount(src.getExperimenterGroupCount());
    for (index_type i = 0; i < experimenterGroupCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterGroupID,
                 &MetadataStore::setExperimenterGroupID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterGroupDescription,
                 &MetadataStore::setExperimenterGroupDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getExperimenterGroupName,
                 &MetadataStore::setExperimenterGroupName,
                 i);

        index_type annotationRefCount(src.getExperimenterGroupAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getExperimenterGroupAnnotationRef,
                     &MetadataStore::setExperimenterGroupAnnotationRef,
                     i, q);
          }

        index_type experimenterRefCount(src.getExperimenterGroupExperimenterRefCount(i));
        for (index_type q = 0; q < experimenterRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getExperimenterGroupExperimenterRef,
                     &MetadataStore::setExperimenterGroupExperimenterRef,
                     i, q);
          }

        index_type leaderCount(src.getLeaderCount(i));
        for (index_type q = 0; q < leaderCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getExperimenterGroupLeader,
                     &MetadataStore::setExperimenterGroupLeader,
                     i, q);
          }
      }
  }

  void
  convertFileAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type fileAnnotationCount(src.getFileAnnotationCount());
    for (index_type i = 0; i < fileAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getFileAnnotationID,
                 &MetadataStore::setFileAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getFileAnnotationDescription,
                 &MetadataStore::setFileAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getFileAnnotationNamespace,
                 &MetadataStore::setFileAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getFileAnnotationAnnotator,
                 &MetadataStore::setFileAnnotationAnnotator,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBinaryFileFileName,
                 &MetadataStore::setBinaryFileFileName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBinaryFileMIMEType,
                 &MetadataStore::setBinaryFileMIMEType,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getBinaryFileSize,
                 &MetadataStore::setBinaryFileSize,
                 i);

        index_type annotationRefCount(src.getFileAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getFileAnnotationAnnotationRef,
                     &MetadataStore::setFileAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertImages(const MetadataRetrieve& src,
                MetadataStore&          dest)
  {
    index_type imageCount(src.getImageCount());
    for (index_type i = 0; i < imageCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getImageID,
                 &MetadataStore::setImageID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageAcquisitionDate,
                 &MetadataStore::setImageAcquisitionDate,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageDescription,
                 &MetadataStore::setImageDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageExperimentRef,
                 &MetadataStore::setImageExperimentRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageExperimenterGroupRef,
                 &MetadataStore::setImageExperimenterGroupRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageExperimenterRef,
                 &MetadataStore::setImageExperimenterRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageInstrumentRef,
                 &MetadataStore::setImageInstrumentRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImageName,
                 &MetadataStore::setImageName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImagingEnvironmentAirPressure,
                 &MetadataStore::setImagingEnvironmentAirPressure,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImagingEnvironmentCO2Percent,
                 &MetadataStore::setImagingEnvironmentCO2Percent,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImagingEnvironmentHumidity,
                 &MetadataStore::setImagingEnvironmentHumidity,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImagingEnvironmentMap,
                 &MetadataStore::setImagingEnvironmentMap,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getImagingEnvironmentTemperature,
                 &MetadataStore::setImagingEnvironmentTemperature,
                 i);

        if (transfer(src, dest,
                     &MetadataRetrieve::getObjectiveSettingsID,
                     &MetadataStore::setObjectiveSettingsID,
                     i))
          {
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveSettingsCorrectionCollar,
                     &MetadataStore::setObjectiveSettingsCorrectionCollar,
                     i);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveSettingsMedium,
                     &MetadataStore::setObjectiveSettingsMedium,
                     i);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveSettingsRefractiveIndex,
                     &MetadataStore::setObjectiveSettingsRefractiveIndex,
                     i);
          }

        if (transfer(src, dest,
                     &MetadataRetrieve::getStageLabelName,
                     &MetadataStore::setStageLabelName,
                     i))
          {
            transfer(src, dest,
                     &MetadataRetrieve::getStageLabelX,
                     &MetadataStore::setStageLabelX,
                     i);
            transfer(src, dest,
                     &MetadataRetrieve::getStageLabelY,
                     &MetadataStore::setStageLabelY,
                     i);
            transfer(src, dest,
                     &MetadataRetrieve::getStageLabelZ,
                     &MetadataStore::setStageLabelZ,
                     i);
          }

        transfer(src, dest,
                 &MetadataRetrieve::getPixelsID,
                 &MetadataStore::setPixelsID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsDimensionOrder,
                 &MetadataStore::setPixelsDimensionOrder,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsPhysicalSizeX,
                 &MetadataStore::setPixelsPhysicalSizeX,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsPhysicalSizeY,
                 &MetadataStore::setPixelsPhysicalSizeY,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsPhysicalSizeZ,
                 &MetadataStore::setPixelsPhysicalSizeZ,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSizeX,
                 &MetadataStore::setPixelsSizeX,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSizeY,
                 &MetadataStore::setPixelsSizeY,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSizeZ,
                 &MetadataStore::setPixelsSizeZ,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSizeT,
                 &MetadataStore::setPixelsSizeT,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSizeC,
                 &MetadataStore::setPixelsSizeC,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsTimeIncrement,
                 &MetadataStore::setPixelsTimeIncrement,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsType,
                 &MetadataStore::setPixelsType,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsBigEndian,
                 &MetadataStore::setPixelsBigEndian,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsInterleaved,
                 &MetadataStore::setPixelsInterleaved,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPixelsSignificantBits,
                 &MetadataStore::setPixelsSignificantBits,
                 i);

        index_type binDataCount(src.getPixelsBinDataCount(i));
        for (index_type q = 0; q < binDataCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getPixelsBinDataBigEndian,
                     &MetadataStore::setPixelsBinDataBigEndian,
                     i, q);
          }

        index_type annotationRefCount(src.getImageAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getImageAnnotationRef,
                     &MetadataStore::setImageAnnotationRef,
                     i, q);
          }

        index_type channelCount(src.getChannelCount(i));
        for (index_type c = 0; c < channelCount; ++c)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getChannelID,
                     &MetadataStore::setChannelID,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelAcquisitionMode,
                     &MetadataStore::setChannelAcquisitionMode,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelColor,
                     &MetadataStore::setChannelColor,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelContrastMethod,
                     &MetadataStore::setChannelContrastMethod,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelEmissionWavelength,
                     &MetadataStore::setChannelEmissionWavelength,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelExcitationWavelength,
                     &MetadataStore::setChannelExcitationWavelength,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelFilterSetRef,
                     &MetadataStore::setChannelFilterSetRef,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelFluor,
                     &MetadataStore::setChannelFluor,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelIlluminationType,
                     &MetadataStore::setChannelIlluminationType,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelNDFilter,
                     &MetadataStore::setChannelNDFilter,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelName,
                     &MetadataStore::setChannelName,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelPinholeSize,
                     &MetadataStore::setChannelPinholeSize,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelPockelCellSetting,
                     &MetadataStore::setChannelPockelCellSetting,
                     i, c);
            transfer(src, dest,
                     &MetadataRetrieve::getChannelSamplesPerPixel,
                     &MetadataStore::setChannelSamplesPerPixel,
                     i, c);

            if (transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsID,
                         &MetadataStore::setDetectorSettingsID,
                         i, c))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsBinning,
                         &MetadataStore::setDetectorSettingsBinning,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsGain,
                         &MetadataStore::setDetectorSettingsGain,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsIntegration,
                         &MetadataStore::setDetectorSettingsIntegration,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsOffset,
                         &MetadataStore::setDetectorSettingsOffset,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsReadOutRate,
                         &MetadataStore::setDetectorSettingsReadOutRate,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsVoltage,
                         &MetadataStore::setDetectorSettingsVoltage,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getDetectorSettingsZoom,
                         &MetadataStore::setDetectorSettingsZoom,
                         i, c);
              }

            transfer(src, dest,
                     &MetadataRetrieve::getLightPathDichroicRef,
                     &MetadataStore::setLightPathDichroicRef,
                     i, c);

            if (transfer(src, dest,
                         &MetadataRetrieve::getChannelLightSourceSettingsID,
                         &MetadataStore::setChannelLightSourceSettingsID,
                         i, c))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getChannelLightSourceSettingsAttenuation,
                         &MetadataStore::setChannelLightSourceSettingsAttenuation,
                         i, c);
                transfer(src, dest,
                         &MetadataRetrieve::getChannelLightSourceSettingsWavelength,
                         &MetadataStore::setChannelLightSourceSettingsWavelength,
                         i, c);
              }

            index_type channelAnnotationRefCount(src.getChannelAnnotationRefCount(i, c));
            for (index_type q = 0; q < channelAnnotationRefCount; ++q)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getChannelAnnotationRef,
                         &MetadataStore::setChannelAnnotationRef,
                         i, c, q);
              }

            index_type emFilterRefCount(src.getLightPathEmissionFilterRefCount(i, c));
            for (index_type q = 0; q < emFilterRefCount; ++q)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLightPathEmissionFilterRef,
                         &MetadataStore::setLightPathEmissionFilterRef,
                         i, c, q);
              }

            index_type exFilterRefCount(src.getLightPathExcitationFilterRefCount(i, c));
            for (index_type q = 0; q < exFilterRefCount; ++q)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLightPathExcitationFilterRef,
                         &MetadataStore::setLightPathExcitationFilterRef,
                         i, c, q);
              }
          }

        index_type planeCount(src.getPlaneCount(i));
        for (index_type p = 0; p < planeCount; ++p)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneDeltaT,
                     &MetadataStore::setPlaneDeltaT,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneExposureTime,
                     &MetadataStore::setPlaneExposureTime,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneHashSHA1,
                     &MetadataStore::setPlaneHashSHA1,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlanePositionX,
                     &MetadataStore::setPlanePositionX,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlanePositionY,
                     &MetadataStore::setPlanePositionY,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlanePositionZ,
                     &MetadataStore::setPlanePositionZ,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneTheZ,
                     &MetadataStore::setPlaneTheZ,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneTheT,
                     &MetadataStore::setPlaneTheT,
                     i, p);
            transfer(src, dest,
                     &MetadataRetrieve::getPlaneTheC,
                     &MetadataStore::setPlaneTheC,
                     i, p);

            index_type planeAnnotationRefCount(src.getPlaneAnnotationRefCount(i, p));
            for (index_type q = 0; q < planeAnnotationRefCount; ++q)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getPlaneAnnotationRef,
                         &MetadataStore::setPlaneAnnotationRef,
                         i, p, q);
              }
          }

        index_type microbeamCount(src.getMicrobeamManipulationRefCount(i));
        for (index_type q = 0; q < microbeamCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getImageMicrobeamManipulationRef,
                     &MetadataStore::setImageMicrobeamManipulationRef,
                     i, q);
          }

        index_type roiRefCount(src.getImageROIRefCount(i));
        for (index_type q = 0; q < roiRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getImageROIRef,
                     &MetadataStore::setImageROIRef,
                     i, q);
          }

        index_type tiffDataCount(src.getTiffDataCount(i));
        for (index_type q = 0; q < tiffDataCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getUUIDValue,
                     &MetadataStore::setUUIDValue,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getUUIDFileName,
                     &MetadataStore::setUUIDFileName,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTiffDataFirstZ,
                     &MetadataStore::setTiffDataFirstZ,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTiffDataFirstT,
                     &MetadataStore::setTiffDataFirstT,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTiffDataFirstC,
                     &MetadataStore::setTiffDataFirstC,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTiffDataIFD,
                     &MetadataStore::setTiffDataIFD,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTiffDataPlaneCount,
                     &MetadataStore::setTiffDataPlaneCount,
                     i, q);
          }
      }
  }

  void
  convertLightSources(const MetadataRetrieve& src,
                      MetadataStore&          dest,
                      index_type              instrumentIndex)
  {
    index_type lightSourceCount(src.getLightSourceCount(instrumentIndex));

    for (index_type lightSource = 0; lightSource < lightSourceCount; ++lightSource)
      {
        std::string type(src.getLightSourceType(instrumentIndex, lightSource));
        if (type == "Arc")
          {
            if (transfer(src, dest,
                         &MetadataRetrieve::getArcID,
                         &MetadataStore::setArcID,
                         instrumentIndex, lightSource))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getArcLotNumber,
                         &MetadataStore::setArcLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getArcManufacturer,
                         &MetadataStore::setArcManufacturer,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getArcModel,
                         &MetadataStore::setArcModel,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getArcPower,
                         &MetadataStore::setArcPower,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getArcSerialNumber,
                         &MetadataStore::setArcSerialNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getArcType,
                         &MetadataStore::setArcType,
                         instrumentIndex, lightSource);

                index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getArcAnnotationRef,
                             &MetadataStore::setArcAnnotationRef,
                             instrumentIndex, lightSource, r);
                  }
              }
          }
        else if (type == "Filament")
          {
            if (transfer(src, dest,
                         &MetadataRetrieve::getFilamentID,
                         &MetadataStore::setFilamentID,
                         instrumentIndex, lightSource))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentID,
                         &MetadataStore::setFilamentID,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentLotNumber,
                         &MetadataStore::setFilamentLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentManufacturer,
                         &MetadataStore::setFilamentManufacturer,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentModel,
                         &MetadataStore::setFilamentModel,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentPower,
                         &MetadataStore::setFilamentPower,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentSerialNumber,
                         &MetadataStore::setFilamentSerialNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getFilamentType,
                         &MetadataStore::setFilamentType,
                         instrumentIndex, lightSource);

                index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getFilamentAnnotationRef,
                             &MetadataStore::setFilamentAnnotationRef,
                             instrumentIndex, lightSource, r);
                  }
              }
          }
        else if (type == "GenericExcitationSource")
          {
            if (transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceID,
                         &MetadataStore::setGenericExcitationSourceID,
                         instrumentIndex, lightSource))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceMap,
                         &MetadataStore::setGenericExcitationSourceMap,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceLotNumber,
                         &MetadataStore::setGenericExcitationSourceLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceManufacturer,
                         &MetadataStore::setGenericExcitationSourceManufacturer,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceModel,
                         &MetadataStore::setGenericExcitationSourceModel,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourcePower,
                         &MetadataStore::setGenericExcitationSourcePower,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getGenericExcitationSourceSerialNumber,
                         &MetadataStore::setGenericExcitationSourceSerialNumber,
                         instrumentIndex, lightSource);

                index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getGenericExcitationSourceAnnotationRef,
                             &MetadataStore::setGenericExcitationSourceAnnotationRef,
                             instrumentIndex, lightSource, r);
                  }
              }
          }
        else if (type == "Laser")
          {
            if (transfer(src, dest,
                         &MetadataRetrieve::getLaserID,
                         &MetadataStore::setLaserID,
                         instrumentIndex, lightSource))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLaserLotNumber,
                         &MetadataStore::setLaserLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserManufacturer,
                         &MetadataStore::setLaserManufacturer,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserLotNumber,
                         &MetadataStore::setLaserLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserModel,
                         &MetadataStore::setLaserModel,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserPower,
                         &MetadataStore::setLaserPower,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserSerialNumber,
                         &MetadataStore::setLaserSerialNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserType,
                         &MetadataStore::setLaserType,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserFrequencyMultiplication,
                         &MetadataStore::setLaserFrequencyMultiplication,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserLaserMedium,
                         &MetadataStore::setLaserLaserMedium,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserPockelCell,
                         &MetadataStore::setLaserPockelCell,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserPulse,
                         &MetadataStore::setLaserPulse,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserPump,
                         &MetadataStore::setLaserPump,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserRepetitionRate,
                         &MetadataStore::setLaserRepetitionRate,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserTuneable,
                         &MetadataStore::setLaserTuneable,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLaserWavelength,
                         &MetadataStore::setLaserWavelength,
                         instrumentIndex, lightSource);

                index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getLaserAnnotationRef,
                             &MetadataStore::setLaserAnnotationRef,
                             instrumentIndex, lightSource, r);
                  }
              }
          }
        else if (type == "LightEmittingDiode")
          {
            if (transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodeID,
                         &MetadataStore::setLightEmittingDiodeID,
                         instrumentIndex, lightSource))
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodeLotNumber,
                         &MetadataStore::setLightEmittingDiodeLotNumber,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodeManufacturer,
                         &MetadataStore::setLightEmittingDiodeManufacturer,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodeModel,
                         &MetadataStore::setLightEmittingDiodeModel,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodePower,
                         &MetadataStore::setLightEmittingDiodePower,
                         instrumentIndex, lightSource);
                transfer(src, dest,
                         &MetadataRetrieve::getLightEmittingDiodeSerialNumber,
                         &MetadataStore::setLightEmittingDiodeSerialNumber,
                         instrumentIndex, lightSource);

                index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getLightEmittingDiodeAnnotationRef,
                             &MetadataStore::setLightEmittingDiodeAnnotationRef,
                             instrumentIndex, lightSource, r);
                  }
              }
          }
      }
  }

  void
  convertInstruments(const MetadataRetrieve& src,
                     MetadataStore&          dest)
  {
    index_type instrumentCount(src.getInstrumentCount());
    for (index_type i = 0; i < instrumentCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getInstrumentID,
                 &MetadataStore::setInstrumentID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getMicroscopeLotNumber,
                 &MetadataStore::setMicroscopeLotNumber,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getMicroscopeManufacturer,
                 &MetadataStore::setMicroscopeManufacturer,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getMicroscopeModel,
                 &MetadataStore::setMicroscopeModel,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getMicroscopeSerialNumber,
                 &MetadataStore::setMicroscopeSerialNumber,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getMicroscopeType,
                 &MetadataStore::setMicroscopeType,
                 i);

        index_type detectorCount(src.getDetectorCount(i));
        for (index_type q = 0; q < detectorCount; ++q)
        {
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorID,
                   &MetadataStore::setDetectorID,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorAmplificationGain,
                   &MetadataStore::setDetectorAmplificationGain,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorGain,
                   &MetadataStore::setDetectorGain,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorLotNumber,
                   &MetadataStore::setDetectorLotNumber,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorManufacturer,
                   &MetadataStore::setDetectorManufacturer,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorModel,
                   &MetadataStore::setDetectorModel,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorOffset,
                   &MetadataStore::setDetectorOffset,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorSerialNumber,
                   &MetadataStore::setDetectorSerialNumber,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorType,
                   &MetadataStore::setDetectorType,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorVoltage,
                   &MetadataStore::setDetectorVoltage,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getDetectorZoom,
                   &MetadataStore::setDetectorZoom,
                   i, q);

          index_type detectorAnnotationRefCount(src.getDetectorAnnotationRefCount(i, q));
          for (index_type r = 0; r < detectorAnnotationRefCount; ++r)
            {
              transfer(src, dest,
                       &MetadataRetrieve::getDetectorAnnotationRef,
                       &MetadataStore::setDetectorAnnotationRef,
                       i, q, r);
            }
        }

        index_type dichroicCount(src.getDichroicCount(i));
        for (index_type q = 0; q < dichroicCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getDichroicID,
                     &MetadataStore::setDichroicID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getDichroicLotNumber,
                     &MetadataStore::setDichroicLotNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getDichroicManufacturer,
                     &MetadataStore::setDichroicManufacturer,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getDichroicModel,
                     &MetadataStore::setDichroicModel,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getDichroicSerialNumber,
                     &MetadataStore::setDichroicSerialNumber,
                     i, q);

            index_type dichroicAnnotationRefCount(src.getDichroicAnnotationRefCount(i,q));
            for (index_type r = 0; r < dichroicAnnotationRefCount; ++r)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getDichroicAnnotationRef,
                         &MetadataStore::setDichroicAnnotationRef,
                         i, q, r);
              }
          }

        index_type filterCount(src.getFilterCount(i));
        for (index_type q = 0; q < filterCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getFilterID,
                     &MetadataStore::setFilterID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterFilterWheel,
                     &MetadataStore::setFilterFilterWheel,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterLotNumber,
                     &MetadataStore::setFilterLotNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterManufacturer,
                     &MetadataStore::setFilterManufacturer,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterModel,
                     &MetadataStore::setFilterModel,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSerialNumber,
                     &MetadataStore::setFilterSerialNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterType,
                     &MetadataStore::setFilterType,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTransmittanceRangeCutIn,
                     &MetadataStore::setTransmittanceRangeCutIn,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTransmittanceRangeCutInTolerance,
                     &MetadataStore::setTransmittanceRangeCutInTolerance,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTransmittanceRangeCutOut,
                     &MetadataStore::setTransmittanceRangeCutOut,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTransmittanceRangeCutOutTolerance,
                     &MetadataStore::setTransmittanceRangeCutOutTolerance,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getTransmittanceRangeTransmittance,
                     &MetadataStore::setTransmittanceRangeTransmittance,
                     i, q);

            index_type filterAnnotationRefCount(src.getFilterAnnotationRefCount(i, q));
            for (index_type r = 0; r < filterAnnotationRefCount; ++r)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getFilterAnnotationRef,
                         &MetadataStore::setFilterAnnotationRef,
                         i, q, r);
              }
          }

        index_type objectiveCount(src.getObjectiveCount(i));
        for (index_type q = 0; q < objectiveCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveID,
                     &MetadataStore::setObjectiveID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveCalibratedMagnification,
                     &MetadataStore::setObjectiveCalibratedMagnification,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveCorrection,
                     &MetadataStore::setObjectiveCorrection,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveImmersion,
                     &MetadataStore::setObjectiveImmersion,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveIris,
                     &MetadataStore::setObjectiveIris,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveLensNA,
                     &MetadataStore::setObjectiveLensNA,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveLotNumber,
                     &MetadataStore::setObjectiveLotNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveManufacturer,
                     &MetadataStore::setObjectiveManufacturer,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveModel,
                     &MetadataStore::setObjectiveModel,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveNominalMagnification,
                     &MetadataStore::setObjectiveNominalMagnification,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveSerialNumber,
                     &MetadataStore::setObjectiveSerialNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getObjectiveWorkingDistance,
                     &MetadataStore::setObjectiveWorkingDistance,
                     i, q);

            index_type objectiveAnnotationRefCount(src.getObjectiveAnnotationRefCount(i, q));
            for (index_type r = 0; r < objectiveAnnotationRefCount; ++r)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getObjectiveAnnotationRef,
                         &MetadataStore::setObjectiveAnnotationRef,
                         i, q, r);
              }
          }

        index_type filterSetCount(src.getFilterSetCount(i));
        for (index_type q = 0; q < filterSetCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetID,
                     &MetadataStore::setFilterSetID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetDichroicRef,
                     &MetadataStore::setFilterSetDichroicRef,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetLotNumber,
                     &MetadataStore::setFilterSetLotNumber,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetManufacturer,
                     &MetadataStore::setFilterSetManufacturer,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetModel,
                     &MetadataStore::setFilterSetModel,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getFilterSetSerialNumber,
                     &MetadataStore::setFilterSetSerialNumber,
                     i, q);

            index_type emFilterCount(src.getFilterSetEmissionFilterRefCount(i, q));
            for (index_type f = 0; f < emFilterCount; ++f)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getFilterSetEmissionFilterRef,
                         &MetadataStore::setFilterSetEmissionFilterRef,
                         i, q, f);
              }

            index_type exFilterCount(src.getFilterSetExcitationFilterRefCount(i, q));
            for (index_type f = 0; f < exFilterCount; ++f)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getFilterSetExcitationFilterRef,
                         &MetadataStore::setFilterSetExcitationFilterRef,
                         i, q, f);
              }
          }
        convertLightSources(src, dest, i);
      }
  }

  void
  convertListAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type listAnnotationCount(src.getListAnnotationCount());
    for (index_type i = 0; i < listAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getListAnnotationID,
                 &MetadataStore::setListAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getListAnnotationDescription,
                 &MetadataStore::setListAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getListAnnotationNamespace,
                 &MetadataStore::setListAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getListAnnotationAnnotator,
                 &MetadataStore::setListAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getListAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getListAnnotationAnnotationRef,
                     &MetadataStore::setListAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertLongAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type longAnnotationCount(src.getLongAnnotationCount());
    for (index_type i = 0; i < longAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getLongAnnotationID,
                 &MetadataStore::setLongAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getLongAnnotationDescription,
                 &MetadataStore::setLongAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getLongAnnotationNamespace,
                 &MetadataStore::setLongAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getLongAnnotationValue,
                 &MetadataStore::setLongAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getLongAnnotationAnnotator,
                 &MetadataStore::setLongAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getLongAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getLongAnnotationAnnotationRef,
                     &MetadataStore::setLongAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertMapAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type mapAnnotationCount(src.getMapAnnotationCount());
    for (index_type i = 0; i < mapAnnotationCount; ++i)
      {
        std::string id = src.getMapAnnotationID(i);
        dest.setMapAnnotationID(id, i);

        std::string description = src.getMapAnnotationDescription(i);
        dest.setMapAnnotationDescription(description, i);

        std::string ns = src.getMapAnnotationNamespace(i);
        dest.setMapAnnotationNamespace(ns, i);

        const map_pairs_map_type& value = src.getMapAnnotationValue(i);
        dest.setMapAnnotationValue(value, i);

        std::string annotator = src.getMapAnnotationAnnotator(i);
        dest.setMapAnnotationAnnotator(annotator, i);

        index_type annotationRefCount(src.getMapAnnotationAnnotationCount(i));
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getMapAnnotationAnnotationRef(i, a);
          dest.setMapAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertPlates(const MetadataRetrieve& src,
                MetadataStore&          dest)
  {
    index_type plateCount(src.getPlateCount());
    for (index_type i = 0; i < plateCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getPlateID,
                 &MetadataStore::setPlateID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateColumnNamingConvention,
                 &MetadataStore::setPlateColumnNamingConvention,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateColumns,
                 &MetadataStore::setPlateColumns,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateDescription,
                 &MetadataStore::setPlateDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateExternalIdentifier,
                 &MetadataStore::setPlateExternalIdentifier,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateFieldIndex,
                 &MetadataStore::setPlateFieldIndex,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateName,
                 &MetadataStore::setPlateName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateRowNamingConvention,
                 &MetadataStore::setPlateRowNamingConvention,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateRows,
                 &MetadataStore::setPlateRows,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateStatus,
                 &MetadataStore::setPlateStatus,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateWellOriginX,
                 &MetadataStore::setPlateWellOriginX,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getPlateWellOriginY,
                 &MetadataStore::setPlateWellOriginY,
                 i);

        index_type wellCount(src.getWellCount(i));
        for (index_type q = 0; q < wellCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getWellID,
                     &MetadataStore::setWellID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellColor,
                     &MetadataStore::setWellColor,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellColumn,
                     &MetadataStore::setWellColumn,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellExternalDescription,
                     &MetadataStore::setWellExternalDescription,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellExternalIdentifier,
                     &MetadataStore::setWellExternalIdentifier,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellReagentRef,
                     &MetadataStore::setWellReagentRef,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellRow,
                     &MetadataStore::setWellRow,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getWellType,
                     &MetadataStore::setWellType,
                     i, q);

            index_type wellAnnotationRefCount(src.getWellAnnotationRefCount(i, q));
            for (index_type a = 0; a < wellAnnotationRefCount; ++a)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getWellAnnotationRef,
                         &MetadataStore::setWellAnnotationRef,
                         i, q, a);
              }

            index_type wellSampleCount(src.getWellSampleCount(i, q));
            for (index_type w = 0; w < wellSampleCount; ++w)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getWellSampleID,
                         &MetadataStore::setWellSampleID,
                         i, q, w);
                transfer(src, dest,
                         &MetadataRetrieve::getWellSampleIndex,
                         &MetadataStore::setWellSampleIndex,
                         i, q, w);
                transfer(src, dest,
                         &MetadataRetrieve::getWellSampleImageRef,
                         &MetadataStore::setWellSampleImageRef,
                         i, q, w);
                transfer(src, dest,
                         &MetadataRetrieve::getWellSamplePositionX,
                         &MetadataStore::setWellSamplePositionX,
                         i, q, w);
                transfer(src, dest,
                         &MetadataRetrieve::getWellSamplePositionY,
                         &MetadataStore::setWellSamplePositionY,
                         i, q, w);
                transfer(src, dest,
                         &MetadataRetrieve::getWellSampleTimepoint,
                         &MetadataStore::setWellSampleTimepoint,
                         i, q, w);
              }
          }

        index_type plateAcquisitionCount(src.getPlateAcquisitionCount(i));
        for (index_type q = 0; q < plateAcquisitionCount; ++q)
        {
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionID,
                   &MetadataStore::setPlateAcquisitionID,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionDescription,
                   &MetadataStore::setPlateAcquisitionDescription,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionEndTime,
                   &MetadataStore::setPlateAcquisitionEndTime,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionMaximumFieldCount,
                   &MetadataStore::setPlateAcquisitionMaximumFieldCount,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionName,
                   &MetadataStore::setPlateAcquisitionName,
                   i, q);
          transfer(src, dest,
                   &MetadataRetrieve::getPlateAcquisitionStartTime,
                   &MetadataStore::setPlateAcquisitionStartTime,
                   i, q);

          index_type plateAcquisitionAnnotationRefCount(src.getPlateAcquisitionAnnotationRefCount(i, q));
          for (index_type a = 0; a < plateAcquisitionAnnotationRefCount; ++a)
            {
              transfer(src, dest,
                       &MetadataRetrieve::getPlateAcquisitionAnnotationRef,
                       &MetadataStore::setPlateAcquisitionAnnotationRef,
                       i, q, a);
            }

          index_type wellSampleRefCount(src.getWellSampleRefCount(i, q));
          for (index_type w = 0; w < wellSampleRefCount; ++w)
            {
              transfer(src, dest,
                       &MetadataRetrieve::getPlateAcquisitionWellSampleRef,
                       &MetadataStore::setPlateAcquisitionWellSampleRef,
                       i, q, w);
            }
        }

        index_type plateAnnotationRefCount(src.getPlateAnnotationRefCount(i));
        for (index_type q = 0; q < plateAnnotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getPlateAnnotationRef,
                     &MetadataStore::setPlateAnnotationRef,
                     i, q);
          }
      }
  }

  void
  convertProjects(const MetadataRetrieve& src,
                  MetadataStore&          dest)
  {
    index_type projectCount(src.getProjectCount());
    for (index_type i = 0; i < projectCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getProjectID,
                 &MetadataStore::setProjectID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getProjectDescription,
                 &MetadataStore::setProjectDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getProjectExperimenterGroupRef,
                 &MetadataStore::setProjectExperimenterGroupRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getProjectExperimenterRef,
                 &MetadataStore::setProjectExperimenterRef,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getProjectName,
                 &MetadataStore::setProjectName,
                 i);

        index_type annotationRefCount(src.getProjectAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getProjectAnnotationRef,
                     &MetadataStore::setProjectAnnotationRef,
                     i, q);
          }

        index_type datasetRefCount(src.getDatasetRefCount(i));
        for (index_type q = 0; q < datasetRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getProjectDatasetRef,
                     &MetadataStore::setProjectDatasetRef,
                     i, q);
          }
      }
  }

  void
  convertROIs(const MetadataRetrieve& src,
              MetadataStore&          dest)
  {
    index_type roiCount(src.getROICount());
    for (index_type i = 0; i < roiCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getROIID,
                 &MetadataStore::setROIID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getROIName,
                 &MetadataStore::setROIName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getROIDescription,
                 &MetadataStore::setROIDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getROINamespace,
                 &MetadataStore::setROINamespace,
                 i);

        index_type shapeCount(src.getShapeCount(i));
        for (index_type q = 0; q < shapeCount; ++q)
          {
            std::string type = src.getShapeType(i, q);

            if (type == "Ellipse")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseID,
                         &MetadataStore::setEllipseID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseFillColor,
                         &MetadataStore::setEllipseFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseFillRule,
                         &MetadataStore::setEllipseFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseFontFamily,
                         &MetadataStore::setEllipseFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseFontSize,
                         &MetadataStore::setEllipseFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseFontStyle,
                         &MetadataStore::setEllipseFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseLineCap,
                         &MetadataStore::setEllipseLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseLocked,
                         &MetadataStore::setEllipseLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseStrokeColor,
                         &MetadataStore::setEllipseStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseStrokeDashArray,
                         &MetadataStore::setEllipseStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseStrokeWidth,
                         &MetadataStore::setEllipseStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseText,
                         &MetadataStore::setEllipseText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseTheZ,
                         &MetadataStore::setEllipseTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseTheT,
                         &MetadataStore::setEllipseTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseTheC,
                         &MetadataStore::setEllipseTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseTransform,
                         &MetadataStore::setEllipseTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseVisible,
                         &MetadataStore::setEllipseVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseRadiusX,
                         &MetadataStore::setEllipseRadiusX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseRadiusY,
                         &MetadataStore::setEllipseRadiusY,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseX,
                         &MetadataStore::setEllipseX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getEllipseY,
                         &MetadataStore::setEllipseY,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getEllipseAnnotationRef,
                             &MetadataStore::setEllipseAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Label")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLabelID,
                         &MetadataStore::setLabelID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelFillColor,
                         &MetadataStore::setLabelFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelFillRule,
                         &MetadataStore::setLabelFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelFontFamily,
                         &MetadataStore::setLabelFontFamily,
                         i, q);
                transfer(src, dest,
                     &MetadataRetrieve::getLabelFontSize,
                         &MetadataStore::setLabelFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelFontStyle,
                         &MetadataStore::setLabelFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelLineCap,
                         &MetadataStore::setLabelLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelLocked,
                         &MetadataStore::setLabelLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelStrokeColor,
                         &MetadataStore::setLabelStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelStrokeDashArray,
                         &MetadataStore::setLabelStrokeDashArray,
                     i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelStrokeWidth,
                         &MetadataStore::setLabelStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelText,
                         &MetadataStore::setLabelText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelTheZ,
                         &MetadataStore::setLabelTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelTheT,
                         &MetadataStore::setLabelTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelTheC,
                         &MetadataStore::setLabelTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelTransform,
                         &MetadataStore::setLabelTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelVisible,
                         &MetadataStore::setLabelVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelX,
                         &MetadataStore::setLabelX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLabelY,
                         &MetadataStore::setLabelY,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getLabelAnnotationRef,
                             &MetadataStore::setLabelAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Line")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getLineID,
                         &MetadataStore::setLineID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineFillColor,
                         &MetadataStore::setLineFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineFillRule,
                         &MetadataStore::setLineFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineFontFamily,
                         &MetadataStore::setLineFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineFontSize,
                         &MetadataStore::setLineFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineFontStyle,
                         &MetadataStore::setLineFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineLineCap,
                         &MetadataStore::setLineLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineLocked,
                         &MetadataStore::setLineLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineStrokeColor,
                         &MetadataStore::setLineStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineStrokeDashArray,
                         &MetadataStore::setLineStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineStrokeWidth,
                         &MetadataStore::setLineStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineText,
                         &MetadataStore::setLineText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineTheZ,
                         &MetadataStore::setLineTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineTheT,
                         &MetadataStore::setLineTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineTheC,
                         &MetadataStore::setLineTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineTransform,
                         &MetadataStore::setLineTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineVisible,
                         &MetadataStore::setLineVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineMarkerEnd,
                         &MetadataStore::setLineMarkerEnd,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineMarkerStart,
                         &MetadataStore::setLineMarkerStart,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineX1,
                         &MetadataStore::setLineX1,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineX2,
                         &MetadataStore::setLineX2,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineY1,
                         &MetadataStore::setLineY1,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getLineY2,
                         &MetadataStore::setLineY2,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getLineAnnotationRef,
                             &MetadataStore::setLineAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Mask")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getMaskID,
                         &MetadataStore::setMaskID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskFillColor,
                         &MetadataStore::setMaskFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskFillRule,
                         &MetadataStore::setMaskFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskFontFamily,
                         &MetadataStore::setMaskFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskFontSize,
                         &MetadataStore::setMaskFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskFontStyle,
                         &MetadataStore::setMaskFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskLineCap,
                         &MetadataStore::setMaskLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskLocked,
                         &MetadataStore::setMaskLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskStrokeColor,
                         &MetadataStore::setMaskStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskStrokeDashArray,
                         &MetadataStore::setMaskStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskStrokeWidth,
                         &MetadataStore::setMaskStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskText,
                         &MetadataStore::setMaskText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskTheZ,
                         &MetadataStore::setMaskTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskTheT,
                         &MetadataStore::setMaskTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskTheC,
                         &MetadataStore::setMaskTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskTransform,
                         &MetadataStore::setMaskTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskVisible,
                         &MetadataStore::setMaskVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskHeight,
                         &MetadataStore::setMaskHeight,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskWidth,
                         &MetadataStore::setMaskWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskX,
                         &MetadataStore::setMaskX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getMaskY,
                         &MetadataStore::setMaskY,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getMaskAnnotationRef,
                             &MetadataStore::setMaskAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Point")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getPointID,
                         &MetadataStore::setPointID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointFillColor,
                         &MetadataStore::setPointFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointFillRule,
                         &MetadataStore::setPointFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointFontFamily,
                         &MetadataStore::setPointFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointFontSize,
                         &MetadataStore::setPointFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointFontStyle,
                         &MetadataStore::setPointFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointLineCap,
                         &MetadataStore::setPointLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointLocked,
                         &MetadataStore::setPointLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointStrokeColor,
                         &MetadataStore::setPointStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointStrokeDashArray,
                         &MetadataStore::setPointStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointStrokeWidth,
                         &MetadataStore::setPointStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointText,
                         &MetadataStore::setPointText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointTheZ,
                         &MetadataStore::setPointTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointTheT,
                         &MetadataStore::setPointTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointTheC,
                         &MetadataStore::setPointTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointTransform,
                         &MetadataStore::setPointTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointVisible,
                         &MetadataStore::setPointVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointX,
                         &MetadataStore::setPointX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPointY,
                         &MetadataStore::setPointY,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                transfer(src, dest,
                         &MetadataRetrieve::getPointAnnotationRef,
                         &MetadataStore::setPointAnnotationRef,
                         i, q, r);
                  }
              }
            else if (type == "Polygon")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonID,
                         &MetadataStore::setPolygonID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonFillColor,
                         &MetadataStore::setPolygonFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonFillRule,
                         &MetadataStore::setPolygonFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonFontFamily,
                         &MetadataStore::setPolygonFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonFontSize,
                         &MetadataStore::setPolygonFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonFontStyle,
                         &MetadataStore::setPolygonFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonLineCap,
                         &MetadataStore::setPolygonLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonLocked,
                         &MetadataStore::setPolygonLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonStrokeColor,
                         &MetadataStore::setPolygonStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonStrokeDashArray,
                         &MetadataStore::setPolygonStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonStrokeWidth,
                         &MetadataStore::setPolygonStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonText,
                         &MetadataStore::setPolygonText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonTheZ,
                         &MetadataStore::setPolygonTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonTheT,
                         &MetadataStore::setPolygonTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonTheC,
                         &MetadataStore::setPolygonTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonTransform,
                         &MetadataStore::setPolygonTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonVisible,
                         &MetadataStore::setPolygonVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolygonPoints,
                         &MetadataStore::setPolygonPoints,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getPolygonAnnotationRef,
                             &MetadataStore::setPolygonAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Polyline")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineID,
                         &MetadataStore::setPolylineID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineFillColor,
                         &MetadataStore::setPolylineFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineFillRule,
                         &MetadataStore::setPolylineFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineFontFamily,
                         &MetadataStore::setPolylineFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineFontSize,
                         &MetadataStore::setPolylineFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineFontStyle,
                         &MetadataStore::setPolylineFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineLineCap,
                         &MetadataStore::setPolylineLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineLocked,
                         &MetadataStore::setPolylineLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineStrokeColor,
                         &MetadataStore::setPolylineStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineStrokeDashArray,
                         &MetadataStore::setPolylineStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineStrokeWidth,
                         &MetadataStore::setPolylineStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineText,
                         &MetadataStore::setPolylineText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineTheZ,
                         &MetadataStore::setPolylineTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineTheT,
                         &MetadataStore::setPolylineTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineTheC,
                         &MetadataStore::setPolylineTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineTransform,
                         &MetadataStore::setPolylineTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineVisible,
                         &MetadataStore::setPolylineVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineMarkerEnd,
                         &MetadataStore::setPolylineMarkerEnd,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylineMarkerStart,
                         &MetadataStore::setPolylineMarkerStart,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getPolylinePoints,
                         &MetadataStore::setPolylinePoints,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getPolylineAnnotationRef,
                             &MetadataStore::setPolylineAnnotationRef,
                             i, q, r);
                  }
              }
            else if (type == "Rectangle")
              {
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleID,
                         &MetadataStore::setRectangleID,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleFillColor,
                         &MetadataStore::setRectangleFillColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleFillRule,
                         &MetadataStore::setRectangleFillRule,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleFontFamily,
                         &MetadataStore::setRectangleFontFamily,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleFontSize,
                         &MetadataStore::setRectangleFontSize,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleFontStyle,
                         &MetadataStore::setRectangleFontStyle,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleLineCap,
                         &MetadataStore::setRectangleLineCap,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleLocked,
                         &MetadataStore::setRectangleLocked,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleStrokeColor,
                         &MetadataStore::setRectangleStrokeColor,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleStrokeDashArray,
                         &MetadataStore::setRectangleStrokeDashArray,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleStrokeWidth,
                         &MetadataStore::setRectangleStrokeWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleText,
                         &MetadataStore::setRectangleText,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleTheZ,
                         &MetadataStore::setRectangleTheZ,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleTheT,
                         &MetadataStore::setRectangleTheT,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleTheC,
                         &MetadataStore::setRectangleTheC,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleTransform,
                         &MetadataStore::setRectangleTransform,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleVisible,
                         &MetadataStore::setRectangleVisible,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleHeight,
                         &MetadataStore::setRectangleHeight,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleWidth,
                         &MetadataStore::setRectangleWidth,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleX,
                         &MetadataStore::setRectangleX,
                         i, q);
                transfer(src, dest,
                         &MetadataRetrieve::getRectangleY,
                         &MetadataStore::setRectangleY,
                         i, q);

                index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                  {
                    transfer(src, dest,
                             &MetadataRetrieve::getRectangleAnnotationRef,
                             &MetadataStore::setRectangleAnnotationRef,
                             i, q, r);
                  }
              }
          }

        index_type annotationRefCount(src.getROIAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getROIAnnotationRef,
                     &MetadataStore::setROIAnnotationRef,
                     i, q);
          }
      }
  }

  void
  convertScreens(const MetadataRetrieve& src,
                 MetadataStore&          dest)
  {
    index_type screenCount(src.getScreenCount());
    for (index_type i = 0; i < screenCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getScreenID,
                 &MetadataStore::setScreenID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenName,
                 &MetadataStore::setScreenName,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenDescription,
                 &MetadataStore::setScreenDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenProtocolDescription,
                 &MetadataStore::setScreenProtocolDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenProtocolIdentifier,
                 &MetadataStore::setScreenProtocolIdentifier,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenReagentSetDescription,
                 &MetadataStore::setScreenReagentSetDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenReagentSetIdentifier,
                 &MetadataStore::setScreenReagentSetIdentifier,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getScreenType,
                 &MetadataStore::setScreenType,
                 i);

        index_type plateRefCount(src.getPlateRefCount(i));
        for (index_type q = 0; q < plateRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getScreenPlateRef,
                     &MetadataStore::setScreenPlateRef,
                     i, q);
          }

        index_type annotationRefCount(src.getScreenAnnotationRefCount(i));
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getScreenAnnotationRef,
                     &MetadataStore::setScreenAnnotationRef,
                     i, q);
          }

        index_type reagentCount(src.getReagentCount(i));
        for (index_type q = 0; q < reagentCount; ++q)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getReagentID,
                     &MetadataStore::setReagentID,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getReagentDescription,
                     &MetadataStore::setReagentDescription,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getReagentName,
                     &MetadataStore::setReagentName,
                     i, q);
            transfer(src, dest,
                     &MetadataRetrieve::getReagentReagentIdentifier,
                     &MetadataStore::setReagentReagentIdentifier,
                     i, q);

            index_type reagentAnnotationRefCount(src.getReagentAnnotationRefCount(i, q));
            for (index_type r = 0; r < reagentAnnotationRefCount; ++r)
              {
                transfer(src, dest,
                         &MetadataRetrieve::getReagentAnnotationRef,
                         &MetadataStore::setReagentAnnotationRef,
                         i, q, r);
              }
          }
      }
  }

  void
  convertTagAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type tagAnnotationCount(src.getTagAnnotationCount());
    for (index_type i = 0; i < tagAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getTagAnnotationID,
                 &MetadataStore::setTagAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTagAnnotationDescription,
                 &MetadataStore::setTagAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTagAnnotationNamespace,
                 &MetadataStore::setTagAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTagAnnotationValue,
                 &MetadataStore::setTagAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTagAnnotationAnnotator,
                 &MetadataStore::setTagAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getTagAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getTagAnnotationAnnotationRef,
                     &MetadataStore::setTagAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertTermAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type termAnnotationCount(src.getTermAnnotationCount());
    for (index_type i = 0; i < termAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getTermAnnotationID,
                 &MetadataStore::setTermAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTermAnnotationDescription,
                 &MetadataStore::setTermAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTermAnnotationNamespace,
                 &MetadataStore::setTermAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTermAnnotationValue,
                 &MetadataStore::setTermAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTermAnnotationAnnotator,
                 &MetadataStore::setTermAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getTermAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getTermAnnotationAnnotationRef,
                     &MetadataStore::setTermAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertTimestampAnnotations(const MetadataRetrieve& src,
                              MetadataStore&          dest)
  {
    index_type timestampAnnotationCount(src.getTimestampAnnotationCount());
    for (index_type i = 0; i < timestampAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getTimestampAnnotationID,
                 &MetadataStore::setTimestampAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTimestampAnnotationDescription,
                 &MetadataStore::setTimestampAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTimestampAnnotationNamespace,
                 &MetadataStore::setTimestampAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTimestampAnnotationValue,
                 &MetadataStore::setTimestampAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getTimestampAnnotationAnnotator,
                 &MetadataStore::setTimestampAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getTimestampAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getTimestampAnnotationAnnotationRef,
                     &MetadataStore::setTimestampAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertXMLAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type xmlAnnotationCount(src.getXMLAnnotationCount());
    for (index_type i = 0; i < xmlAnnotationCount; ++i)
      {
        transfer(src, dest,
                 &MetadataRetrieve::getXMLAnnotationID,
                 &MetadataStore::setXMLAnnotationID,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getXMLAnnotationDescription,
                 &MetadataStore::setXMLAnnotationDescription,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getXMLAnnotationNamespace,
                 &MetadataStore::setXMLAnnotationNamespace,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getXMLAnnotationValue,
                 &MetadataStore::setXMLAnnotationValue,
                 i);
        transfer(src, dest,
                 &MetadataRetrieve::getXMLAnnotationAnnotator,
                 &MetadataStore::setXMLAnnotationAnnotator,
                 i);

        index_type annotationRefCount(src.getXMLAnnotationAnnotationCount(i));
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            transfer(src, dest,
                     &MetadataRetrieve::getXMLAnnotationAnnotationRef,
                     &MetadataStore::setXMLAnnotationAnnotationRef,
                     i, a);
          }
      }
  }

  void
  convertRootAttributes(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    transfer(src, dest,
             &MetadataRetrieve::getUUID,
             &MetadataStore::setUUID);
    transfer(src, dest,
             &MetadataRetrieve::getRightsRightsHeld,
             &MetadataStore::setRightsRightsHeld);
    transfer(src, dest,
             &MetadataRetrieve::getRightsRightsHolder,
             &MetadataStore::setRightsRightsHolder);
    transfer(src, dest,
             &MetadataRetrieve::getBinaryOnlyMetadataFile,
             &MetadataStore::setBinaryOnlyMetadataFile);
    transfer(src, dest,
             &MetadataRetrieve::getBinaryOnlyUUID,
             &MetadataStore::setBinaryOnlyUUID);
  }

}


namespace ome
{
  namespace xml
  {
    namespace meta
    {

      void
      convert(const MetadataRetrieve& src,
              MetadataStore&          dest)
      {
        convertBooleanAnnotations(src, dest);
        convertCommentAnnotations(src, dest);
        convertDoubleAnnotations(src, dest);
        convertFileAnnotations(src, dest);
        convertListAnnotations(src, dest);
        convertLongAnnotations(src, dest);
        convertMapAnnotations(src, dest);
        convertTagAnnotations(src, dest);
        convertTermAnnotations(src, dest);
        convertTimestampAnnotations(src, dest);
        convertXMLAnnotations(src, dest);

        convertROIs(src, dest);
        convertInstruments(src, dest);
        convertExperimenters(src, dest);
        convertExperimenterGroups(src, dest);
        convertExperiments(src, dest);
        convertImages(src, dest);
        convertPlates(src, dest);
        convertScreens(src, dest);
        convertDatasets(src, dest);
        convertProjects(src, dest);

        convertRootAttributes(src, dest);
      }

    }
  }
}
