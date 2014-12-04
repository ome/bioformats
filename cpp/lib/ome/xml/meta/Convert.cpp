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

  class MetadataConverter
  {
  private:
    const MetadataRetrieve& src;
    MetadataStore&          dest;

  public:
    MetadataConverter(const MetadataRetrieve& src,
                      MetadataStore&          dest):
      src(src),
      dest(dest)
    {
      convertBooleanAnnotations();
      convertCommentAnnotations();
      convertDoubleAnnotations();
      convertFileAnnotations();
      convertListAnnotations();
      convertLongAnnotations();
      convertMapAnnotations();
      convertTagAnnotations();
      convertTermAnnotations();
      convertTimestampAnnotations();
      convertXMLAnnotations();

      convertROIs();
      convertInstruments();
      convertExperimenters();
      convertExperimenterGroups();
      convertExperiments();
      convertImages();
      convertPlates();
      convertScreens();
      convertDatasets();
      convertProjects();

      convertRootAttributes();
    }
    template<typename T>
    bool
    transfer(T    (MetadataRetrieve::* get)() const,
             void (MetadataStore::*    set)(T value))
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
    transfer(T    (MetadataRetrieve::* get)(P param) const,
             void (MetadataStore::*    set)(T value, P param),
             P                                param)
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
    transfer(T    (MetadataRetrieve::* get)(P param1,
                                            P param2) const,
             void (MetadataStore::*    set)(T value,
                                            P param1,
                                            P param2),
             P                         param1,
             P                         param2)
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
    transfer(T    (MetadataRetrieve::* get)(P param1,
                                            P param2,
                                            P param3) const,
             void (MetadataStore::*    set)(T value,
                                            P param1,
                                            P param2,
                                            P param3),
             P                         param1,
             P                         param2,
             P                         param3)
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
    convertBooleanAnnotations()
    {
      index_type booleanAnnotationCount(src.getBooleanAnnotationCount());
      for (index_type i = 0; i < booleanAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getBooleanAnnotationID,
                   &MetadataStore::setBooleanAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getBooleanAnnotationDescription,
                   &MetadataStore::setBooleanAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getBooleanAnnotationNamespace,
                   &MetadataStore::setBooleanAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getBooleanAnnotationValue,
                   &MetadataStore::setBooleanAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getBooleanAnnotationAnnotator,
                   &MetadataStore::setBooleanAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getBooleanAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getBooleanAnnotationAnnotationRef,
                       &MetadataStore::setBooleanAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertCommentAnnotations()
    {
      index_type commentAnnotationCount(src.getCommentAnnotationCount());
      for (index_type i = 0; i < commentAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getCommentAnnotationID,
                   &MetadataStore::setCommentAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getCommentAnnotationDescription,
                   &MetadataStore::setCommentAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getCommentAnnotationNamespace,
                   &MetadataStore::setCommentAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getCommentAnnotationValue,
                   &MetadataStore::setCommentAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getCommentAnnotationAnnotator,
                   &MetadataStore::setCommentAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getCommentAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getCommentAnnotationAnnotationRef,
                       &MetadataStore::setCommentAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertDatasets()
    {
      index_type datasets(src.getDatasetCount());
      for (index_type i = 0; i < datasets; ++i)
        {
          transfer(&MetadataRetrieve::getDatasetID,
                   &MetadataStore::setDatasetID,
                   i);
          transfer(&MetadataRetrieve::getDatasetDescription,
                   &MetadataStore::setDatasetDescription,
                   i);
          transfer(&MetadataRetrieve::getDatasetExperimenterGroupRef,
                   &MetadataStore::setDatasetExperimenterGroupRef,
                   i);
          transfer(&MetadataRetrieve::getDatasetExperimenterRef,
                   &MetadataStore::setDatasetExperimenterRef,
                   i);
          transfer(&MetadataRetrieve::getDatasetName,
                   &MetadataStore::setDatasetName,
                   i);

          index_type imageRefCount(src.getDatasetImageRefCount(i));
          for (index_type q = 0; q < imageRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getDatasetImageRef,
                       &MetadataStore::setDatasetImageRef,
                       i, q);
            }

          index_type annotationRefCount = src.getDatasetAnnotationRefCount(i);
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getDatasetAnnotationRef,
                       &MetadataStore::setDatasetAnnotationRef,
                       i, q);
            }
        }
    }

    void
    convertDoubleAnnotations()
    {
      index_type doubleAnnotationCount(src.getDoubleAnnotationCount());
      for (index_type i = 0; i < doubleAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getDoubleAnnotationID,
                   &MetadataStore::setDoubleAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getDoubleAnnotationDescription,
                   &MetadataStore::setDoubleAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getDoubleAnnotationNamespace,
                   &MetadataStore::setDoubleAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getDoubleAnnotationValue,
                   &MetadataStore::setDoubleAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getDoubleAnnotationAnnotator,
                   &MetadataStore::setDoubleAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getDoubleAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getDoubleAnnotationAnnotationRef,
                       &MetadataStore::setDoubleAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertExperiments()
    {
      index_type experimentCount(src.getExperimentCount());
      for (index_type i = 0; i < experimentCount; ++i)
        {
          transfer(&MetadataRetrieve::getExperimentID,
                   &MetadataStore::setExperimentID,
                   i);
          transfer(&MetadataRetrieve::getExperimentDescription,
                   &MetadataStore::setExperimentDescription,
                   i);
          transfer(&MetadataRetrieve::getExperimentDescription,
                   &MetadataStore::setExperimentDescription,
                   i);
          transfer(&MetadataRetrieve::getExperimentExperimenterRef,
                   &MetadataStore::setExperimentExperimenterRef,
                   i);
          transfer(&MetadataRetrieve::getExperimentType,
                   &MetadataStore::setExperimentType,
                   i);

          index_type microbeamCount(src.getMicrobeamManipulationCount(i));
          for (index_type q = 0; q < microbeamCount; ++q)
            {
              transfer(&MetadataRetrieve::getMicrobeamManipulationID,
                       &MetadataStore::setMicrobeamManipulationID,
                       i, q);
              transfer(&MetadataRetrieve::getMicrobeamManipulationDescription,
                       &MetadataStore::setMicrobeamManipulationDescription,
                       i, q);
              transfer(&MetadataRetrieve::getMicrobeamManipulationExperimenterRef,
                       &MetadataStore::setMicrobeamManipulationExperimenterRef,
                       i, q);
              transfer(&MetadataRetrieve::getMicrobeamManipulationType,
                       &MetadataStore::setMicrobeamManipulationType,
                       i, q);

              index_type lightSourceCount(src.getMicrobeamManipulationLightSourceSettingsCount(i, q));
              for (index_type p = 0; p < lightSourceCount; ++p)
                {
                  transfer(&MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsID,
                           &MetadataStore::setMicrobeamManipulationLightSourceSettingsID,
                           i, q, p);
                  transfer(&MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsAttenuation,
                           &MetadataStore::setMicrobeamManipulationLightSourceSettingsAttenuation,
                           i, q, p);
                  transfer(&MetadataRetrieve::getMicrobeamManipulationLightSourceSettingsWavelength,
                           &MetadataStore::setMicrobeamManipulationLightSourceSettingsWavelength,
                           i, q, p);
                }

              index_type roiRefCount(src.getMicrobeamManipulationROIRefCount(i, q));
              for (index_type p = 0; p < roiRefCount; ++p)
                {
                  transfer(&MetadataRetrieve::getMicrobeamManipulationROIRef,
                           &MetadataStore::setMicrobeamManipulationROIRef,
                           i, q, p);
                }
            }
        }
    }

    void
    convertExperimenters()
    {
      index_type experimenterCount(src.getExperimenterCount());
      for (index_type i = 0; i < experimenterCount; ++i)
        {
          transfer(&MetadataRetrieve::getExperimenterID,
                   &MetadataStore::setExperimenterID,
                   i);
          transfer(&MetadataRetrieve::getExperimenterEmail,
                   &MetadataStore::setExperimenterEmail,
                   i);
          transfer(&MetadataRetrieve::getExperimenterFirstName,
                   &MetadataStore::setExperimenterFirstName,
                   i);
          transfer(&MetadataRetrieve::getExperimenterInstitution,
                   &MetadataStore::setExperimenterInstitution,
                   i);
          transfer(&MetadataRetrieve::getExperimenterLastName,
                   &MetadataStore::setExperimenterLastName,
                   i);
          transfer(&MetadataRetrieve::getExperimenterMiddleName,
                   &MetadataStore::setExperimenterMiddleName,
                   i);
          transfer(&MetadataRetrieve::getExperimenterUserName,
                   &MetadataStore::setExperimenterUserName,
                   i);

          index_type annotationRefCount(src.getExperimenterAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getExperimenterAnnotationRef,
                       &MetadataStore::setExperimenterAnnotationRef,
                       i, q);
            }
        }
    }

    void
    convertExperimenterGroups()
    {
      index_type experimenterGroupCount(src.getExperimenterGroupCount());
      for (index_type i = 0; i < experimenterGroupCount; ++i)
        {
          transfer(&MetadataRetrieve::getExperimenterGroupID,
                   &MetadataStore::setExperimenterGroupID,
                   i);
          transfer(&MetadataRetrieve::getExperimenterGroupDescription,
                   &MetadataStore::setExperimenterGroupDescription,
                   i);
          transfer(&MetadataRetrieve::getExperimenterGroupName,
                   &MetadataStore::setExperimenterGroupName,
                   i);

          index_type annotationRefCount(src.getExperimenterGroupAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getExperimenterGroupAnnotationRef,
                       &MetadataStore::setExperimenterGroupAnnotationRef,
                       i, q);
            }

          index_type experimenterRefCount(src.getExperimenterGroupExperimenterRefCount(i));
          for (index_type q = 0; q < experimenterRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getExperimenterGroupExperimenterRef,
                       &MetadataStore::setExperimenterGroupExperimenterRef,
                       i, q);
            }

          index_type leaderCount(src.getLeaderCount(i));
          for (index_type q = 0; q < leaderCount; ++q)
            {
              transfer(&MetadataRetrieve::getExperimenterGroupLeader,
                       &MetadataStore::setExperimenterGroupLeader,
                       i, q);
            }
        }
    }

    void
    convertFileAnnotations()
    {
      index_type fileAnnotationCount(src.getFileAnnotationCount());
      for (index_type i = 0; i < fileAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getFileAnnotationID,
                   &MetadataStore::setFileAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getFileAnnotationDescription,
                   &MetadataStore::setFileAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getFileAnnotationNamespace,
                   &MetadataStore::setFileAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getFileAnnotationAnnotator,
                   &MetadataStore::setFileAnnotationAnnotator,
                   i);
          transfer(&MetadataRetrieve::getBinaryFileFileName,
                   &MetadataStore::setBinaryFileFileName,
                   i);
          transfer(&MetadataRetrieve::getBinaryFileMIMEType,
                   &MetadataStore::setBinaryFileMIMEType,
                   i);
          transfer(&MetadataRetrieve::getBinaryFileSize,
                   &MetadataStore::setBinaryFileSize,
                   i);

          index_type annotationRefCount(src.getFileAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getFileAnnotationAnnotationRef,
                       &MetadataStore::setFileAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertImages()
    {
      index_type imageCount(src.getImageCount());
      for (index_type i = 0; i < imageCount; ++i)
        {
          transfer(&MetadataRetrieve::getImageID,
                   &MetadataStore::setImageID,
                   i);
          transfer(&MetadataRetrieve::getImageAcquisitionDate,
                   &MetadataStore::setImageAcquisitionDate,
                   i);
          transfer(&MetadataRetrieve::getImageDescription,
                   &MetadataStore::setImageDescription,
                   i);
          transfer(&MetadataRetrieve::getImageExperimentRef,
                   &MetadataStore::setImageExperimentRef,
                   i);
          transfer(&MetadataRetrieve::getImageExperimenterGroupRef,
                   &MetadataStore::setImageExperimenterGroupRef,
                   i);
          transfer(&MetadataRetrieve::getImageExperimenterRef,
                   &MetadataStore::setImageExperimenterRef,
                   i);
          transfer(&MetadataRetrieve::getImageInstrumentRef,
                   &MetadataStore::setImageInstrumentRef,
                   i);
          transfer(&MetadataRetrieve::getImageName,
                   &MetadataStore::setImageName,
                   i);
          transfer(&MetadataRetrieve::getImagingEnvironmentAirPressure,
                   &MetadataStore::setImagingEnvironmentAirPressure,
                   i);
          transfer(&MetadataRetrieve::getImagingEnvironmentCO2Percent,
                   &MetadataStore::setImagingEnvironmentCO2Percent,
                   i);
          transfer(&MetadataRetrieve::getImagingEnvironmentHumidity,
                   &MetadataStore::setImagingEnvironmentHumidity,
                   i);
          transfer(&MetadataRetrieve::getImagingEnvironmentMap,
                   &MetadataStore::setImagingEnvironmentMap,
                   i);
          transfer(&MetadataRetrieve::getImagingEnvironmentTemperature,
                   &MetadataStore::setImagingEnvironmentTemperature,
                   i);

          if (transfer(&MetadataRetrieve::getObjectiveSettingsID,
                       &MetadataStore::setObjectiveSettingsID,
                       i))
            {
              transfer(&MetadataRetrieve::getObjectiveSettingsCorrectionCollar,
                       &MetadataStore::setObjectiveSettingsCorrectionCollar,
                       i);
              transfer(&MetadataRetrieve::getObjectiveSettingsMedium,
                       &MetadataStore::setObjectiveSettingsMedium,
                       i);
              transfer(&MetadataRetrieve::getObjectiveSettingsRefractiveIndex,
                       &MetadataStore::setObjectiveSettingsRefractiveIndex,
                       i);
            }

          if (transfer(&MetadataRetrieve::getStageLabelName,
                       &MetadataStore::setStageLabelName,
                       i))
            {
              transfer(&MetadataRetrieve::getStageLabelX,
                       &MetadataStore::setStageLabelX,
                       i);
              transfer(&MetadataRetrieve::getStageLabelY,
                       &MetadataStore::setStageLabelY,
                       i);
              transfer(&MetadataRetrieve::getStageLabelZ,
                       &MetadataStore::setStageLabelZ,
                       i);
            }

          transfer(&MetadataRetrieve::getPixelsID,
                   &MetadataStore::setPixelsID,
                   i);
          transfer(&MetadataRetrieve::getPixelsDimensionOrder,
                   &MetadataStore::setPixelsDimensionOrder,
                   i);
          transfer(&MetadataRetrieve::getPixelsPhysicalSizeX,
                   &MetadataStore::setPixelsPhysicalSizeX,
                   i);
          transfer(&MetadataRetrieve::getPixelsPhysicalSizeY,
                   &MetadataStore::setPixelsPhysicalSizeY,
                   i);
          transfer(&MetadataRetrieve::getPixelsPhysicalSizeZ,
                   &MetadataStore::setPixelsPhysicalSizeZ,
                   i);
          transfer(&MetadataRetrieve::getPixelsSizeX,
                   &MetadataStore::setPixelsSizeX,
                   i);
          transfer(&MetadataRetrieve::getPixelsSizeY,
                   &MetadataStore::setPixelsSizeY,
                   i);
          transfer(&MetadataRetrieve::getPixelsSizeZ,
                   &MetadataStore::setPixelsSizeZ,
                   i);
          transfer(&MetadataRetrieve::getPixelsSizeT,
                   &MetadataStore::setPixelsSizeT,
                   i);
          transfer(&MetadataRetrieve::getPixelsSizeC,
                   &MetadataStore::setPixelsSizeC,
                   i);
          transfer(&MetadataRetrieve::getPixelsTimeIncrement,
                   &MetadataStore::setPixelsTimeIncrement,
                   i);
          transfer(&MetadataRetrieve::getPixelsType,
                   &MetadataStore::setPixelsType,
                   i);
          transfer(&MetadataRetrieve::getPixelsBigEndian,
                   &MetadataStore::setPixelsBigEndian,
                   i);
          transfer(&MetadataRetrieve::getPixelsInterleaved,
                   &MetadataStore::setPixelsInterleaved,
                   i);
          transfer(&MetadataRetrieve::getPixelsSignificantBits,
                   &MetadataStore::setPixelsSignificantBits,
                   i);

          index_type binDataCount(src.getPixelsBinDataCount(i));
          for (index_type q = 0; q < binDataCount; ++q)
            {
              transfer(&MetadataRetrieve::getPixelsBinDataBigEndian,
                       &MetadataStore::setPixelsBinDataBigEndian,
                       i, q);
            }

          index_type annotationRefCount(src.getImageAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getImageAnnotationRef,
                       &MetadataStore::setImageAnnotationRef,
                       i, q);
            }

          index_type channelCount(src.getChannelCount(i));
          for (index_type c = 0; c < channelCount; ++c)
            {
              transfer(&MetadataRetrieve::getChannelID,
                       &MetadataStore::setChannelID,
                       i, c);
              transfer(&MetadataRetrieve::getChannelAcquisitionMode,
                       &MetadataStore::setChannelAcquisitionMode,
                       i, c);
              transfer(&MetadataRetrieve::getChannelColor,
                       &MetadataStore::setChannelColor,
                       i, c);
              transfer(&MetadataRetrieve::getChannelContrastMethod,
                       &MetadataStore::setChannelContrastMethod,
                       i, c);
              transfer(&MetadataRetrieve::getChannelEmissionWavelength,
                       &MetadataStore::setChannelEmissionWavelength,
                       i, c);
              transfer(&MetadataRetrieve::getChannelExcitationWavelength,
                       &MetadataStore::setChannelExcitationWavelength,
                       i, c);
              transfer(&MetadataRetrieve::getChannelFilterSetRef,
                       &MetadataStore::setChannelFilterSetRef,
                       i, c);
              transfer(&MetadataRetrieve::getChannelFluor,
                       &MetadataStore::setChannelFluor,
                       i, c);
              transfer(&MetadataRetrieve::getChannelIlluminationType,
                       &MetadataStore::setChannelIlluminationType,
                       i, c);
              transfer(&MetadataRetrieve::getChannelNDFilter,
                       &MetadataStore::setChannelNDFilter,
                       i, c);
              transfer(&MetadataRetrieve::getChannelName,
                       &MetadataStore::setChannelName,
                       i, c);
              transfer(&MetadataRetrieve::getChannelPinholeSize,
                       &MetadataStore::setChannelPinholeSize,
                       i, c);
              transfer(&MetadataRetrieve::getChannelPockelCellSetting,
                       &MetadataStore::setChannelPockelCellSetting,
                       i, c);
              transfer(&MetadataRetrieve::getChannelSamplesPerPixel,
                       &MetadataStore::setChannelSamplesPerPixel,
                       i, c);

              if (transfer(&MetadataRetrieve::getDetectorSettingsID,
                           &MetadataStore::setDetectorSettingsID,
                           i, c))
                {
                  transfer(&MetadataRetrieve::getDetectorSettingsBinning,
                           &MetadataStore::setDetectorSettingsBinning,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsGain,
                           &MetadataStore::setDetectorSettingsGain,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsIntegration,
                           &MetadataStore::setDetectorSettingsIntegration,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsOffset,
                           &MetadataStore::setDetectorSettingsOffset,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsReadOutRate,
                           &MetadataStore::setDetectorSettingsReadOutRate,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsVoltage,
                           &MetadataStore::setDetectorSettingsVoltage,
                           i, c);
                  transfer(&MetadataRetrieve::getDetectorSettingsZoom,
                           &MetadataStore::setDetectorSettingsZoom,
                           i, c);
                }

              transfer(&MetadataRetrieve::getLightPathDichroicRef,
                       &MetadataStore::setLightPathDichroicRef,
                       i, c);

              if (transfer(&MetadataRetrieve::getChannelLightSourceSettingsID,
                           &MetadataStore::setChannelLightSourceSettingsID,
                           i, c))
                {
                  transfer(&MetadataRetrieve::getChannelLightSourceSettingsAttenuation,
                           &MetadataStore::setChannelLightSourceSettingsAttenuation,
                           i, c);
                  transfer(&MetadataRetrieve::getChannelLightSourceSettingsWavelength,
                           &MetadataStore::setChannelLightSourceSettingsWavelength,
                           i, c);
                }

              index_type channelAnnotationRefCount(src.getChannelAnnotationRefCount(i, c));
              for (index_type q = 0; q < channelAnnotationRefCount; ++q)
                {
                  transfer(&MetadataRetrieve::getChannelAnnotationRef,
                           &MetadataStore::setChannelAnnotationRef,
                           i, c, q);
                }

              index_type emFilterRefCount(src.getLightPathEmissionFilterRefCount(i, c));
              for (index_type q = 0; q < emFilterRefCount; ++q)
                {
                  transfer(&MetadataRetrieve::getLightPathEmissionFilterRef,
                           &MetadataStore::setLightPathEmissionFilterRef,
                           i, c, q);
                }

              index_type exFilterRefCount(src.getLightPathExcitationFilterRefCount(i, c));
              for (index_type q = 0; q < exFilterRefCount; ++q)
                {
                  transfer(&MetadataRetrieve::getLightPathExcitationFilterRef,
                           &MetadataStore::setLightPathExcitationFilterRef,
                           i, c, q);
                }
            }

          index_type planeCount(src.getPlaneCount(i));
          for (index_type p = 0; p < planeCount; ++p)
            {
              transfer(&MetadataRetrieve::getPlaneDeltaT,
                       &MetadataStore::setPlaneDeltaT,
                       i, p);
              transfer(&MetadataRetrieve::getPlaneExposureTime,
                       &MetadataStore::setPlaneExposureTime,
                       i, p);
              transfer(&MetadataRetrieve::getPlaneHashSHA1,
                       &MetadataStore::setPlaneHashSHA1,
                       i, p);
              transfer(&MetadataRetrieve::getPlanePositionX,
                       &MetadataStore::setPlanePositionX,
                       i, p);
              transfer(&MetadataRetrieve::getPlanePositionY,
                       &MetadataStore::setPlanePositionY,
                       i, p);
              transfer(&MetadataRetrieve::getPlanePositionZ,
                       &MetadataStore::setPlanePositionZ,
                       i, p);
              transfer(&MetadataRetrieve::getPlaneTheZ,
                       &MetadataStore::setPlaneTheZ,
                       i, p);
              transfer(&MetadataRetrieve::getPlaneTheT,
                       &MetadataStore::setPlaneTheT,
                       i, p);
              transfer(&MetadataRetrieve::getPlaneTheC,
                       &MetadataStore::setPlaneTheC,
                       i, p);

              index_type planeAnnotationRefCount(src.getPlaneAnnotationRefCount(i, p));
              for (index_type q = 0; q < planeAnnotationRefCount; ++q)
                {
                  transfer(&MetadataRetrieve::getPlaneAnnotationRef,
                           &MetadataStore::setPlaneAnnotationRef,
                           i, p, q);
                }
            }

          index_type microbeamCount(src.getMicrobeamManipulationRefCount(i));
          for (index_type q = 0; q < microbeamCount; ++q)
            {
              transfer(&MetadataRetrieve::getImageMicrobeamManipulationRef,
                       &MetadataStore::setImageMicrobeamManipulationRef,
                       i, q);
            }

          index_type roiRefCount(src.getImageROIRefCount(i));
          for (index_type q = 0; q < roiRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getImageROIRef,
                       &MetadataStore::setImageROIRef,
                       i, q);
            }

          index_type tiffDataCount(src.getTiffDataCount(i));
          for (index_type q = 0; q < tiffDataCount; ++q)
            {
              transfer(&MetadataRetrieve::getUUIDValue,
                       &MetadataStore::setUUIDValue,
                       i, q);
              transfer(&MetadataRetrieve::getUUIDFileName,
                       &MetadataStore::setUUIDFileName,
                       i, q);
              transfer(&MetadataRetrieve::getTiffDataFirstZ,
                       &MetadataStore::setTiffDataFirstZ,
                       i, q);
              transfer(&MetadataRetrieve::getTiffDataFirstT,
                       &MetadataStore::setTiffDataFirstT,
                       i, q);
              transfer(&MetadataRetrieve::getTiffDataFirstC,
                       &MetadataStore::setTiffDataFirstC,
                       i, q);
              transfer(&MetadataRetrieve::getTiffDataIFD,
                       &MetadataStore::setTiffDataIFD,
                       i, q);
              transfer(&MetadataRetrieve::getTiffDataPlaneCount,
                       &MetadataStore::setTiffDataPlaneCount,
                       i, q);
            }
        }
    }

    void
    convertLightSources(index_type instrumentIndex)
    {
      index_type lightSourceCount(src.getLightSourceCount(instrumentIndex));

      for (index_type lightSource = 0; lightSource < lightSourceCount; ++lightSource)
        {
          std::string type(src.getLightSourceType(instrumentIndex, lightSource));
          if (type == "Arc")
            {
              if (transfer(&MetadataRetrieve::getArcID,
                           &MetadataStore::setArcID,
                           instrumentIndex, lightSource))
                {
                  transfer(&MetadataRetrieve::getArcLotNumber,
                           &MetadataStore::setArcLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getArcManufacturer,
                           &MetadataStore::setArcManufacturer,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getArcModel,
                           &MetadataStore::setArcModel,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getArcPower,
                           &MetadataStore::setArcPower,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getArcSerialNumber,
                           &MetadataStore::setArcSerialNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getArcType,
                           &MetadataStore::setArcType,
                           instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getArcAnnotationRef,
                               &MetadataStore::setArcAnnotationRef,
                               instrumentIndex, lightSource, r);
                    }
                }
            }
          else if (type == "Filament")
            {
              if (transfer(&MetadataRetrieve::getFilamentID,
                           &MetadataStore::setFilamentID,
                           instrumentIndex, lightSource))
                {
                  transfer(&MetadataRetrieve::getFilamentID,
                           &MetadataStore::setFilamentID,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentLotNumber,
                           &MetadataStore::setFilamentLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentManufacturer,
                           &MetadataStore::setFilamentManufacturer,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentModel,
                           &MetadataStore::setFilamentModel,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentPower,
                           &MetadataStore::setFilamentPower,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentSerialNumber,
                           &MetadataStore::setFilamentSerialNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getFilamentType,
                           &MetadataStore::setFilamentType,
                           instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getFilamentAnnotationRef,
                               &MetadataStore::setFilamentAnnotationRef,
                               instrumentIndex, lightSource, r);
                    }
                }
            }
          else if (type == "GenericExcitationSource")
            {
              if (transfer(&MetadataRetrieve::getGenericExcitationSourceID,
                           &MetadataStore::setGenericExcitationSourceID,
                           instrumentIndex, lightSource))
                {
                  transfer(&MetadataRetrieve::getGenericExcitationSourceMap,
                           &MetadataStore::setGenericExcitationSourceMap,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getGenericExcitationSourceLotNumber,
                           &MetadataStore::setGenericExcitationSourceLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getGenericExcitationSourceManufacturer,
                           &MetadataStore::setGenericExcitationSourceManufacturer,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getGenericExcitationSourceModel,
                           &MetadataStore::setGenericExcitationSourceModel,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getGenericExcitationSourcePower,
                           &MetadataStore::setGenericExcitationSourcePower,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getGenericExcitationSourceSerialNumber,
                           &MetadataStore::setGenericExcitationSourceSerialNumber,
                           instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getGenericExcitationSourceAnnotationRef,
                               &MetadataStore::setGenericExcitationSourceAnnotationRef,
                               instrumentIndex, lightSource, r);
                    }
                }
            }
          else if (type == "Laser")
            {
              if (transfer(&MetadataRetrieve::getLaserID,
                           &MetadataStore::setLaserID,
                           instrumentIndex, lightSource))
                {
                  transfer(&MetadataRetrieve::getLaserLotNumber,
                           &MetadataStore::setLaserLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserManufacturer,
                           &MetadataStore::setLaserManufacturer,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserLotNumber,
                           &MetadataStore::setLaserLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserModel,
                           &MetadataStore::setLaserModel,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserPower,
                           &MetadataStore::setLaserPower,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserSerialNumber,
                           &MetadataStore::setLaserSerialNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserType,
                           &MetadataStore::setLaserType,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserFrequencyMultiplication,
                           &MetadataStore::setLaserFrequencyMultiplication,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserLaserMedium,
                           &MetadataStore::setLaserLaserMedium,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserPockelCell,
                           &MetadataStore::setLaserPockelCell,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserPulse,
                           &MetadataStore::setLaserPulse,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserPump,
                           &MetadataStore::setLaserPump,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserRepetitionRate,
                           &MetadataStore::setLaserRepetitionRate,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserTuneable,
                           &MetadataStore::setLaserTuneable,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLaserWavelength,
                           &MetadataStore::setLaserWavelength,
                           instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getLaserAnnotationRef,
                               &MetadataStore::setLaserAnnotationRef,
                               instrumentIndex, lightSource, r);
                    }
                }
            }
          else if (type == "LightEmittingDiode")
            {
              if (transfer(&MetadataRetrieve::getLightEmittingDiodeID,
                           &MetadataStore::setLightEmittingDiodeID,
                           instrumentIndex, lightSource))
                {
                  transfer(&MetadataRetrieve::getLightEmittingDiodeLotNumber,
                           &MetadataStore::setLightEmittingDiodeLotNumber,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLightEmittingDiodeManufacturer,
                           &MetadataStore::setLightEmittingDiodeManufacturer,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLightEmittingDiodeModel,
                           &MetadataStore::setLightEmittingDiodeModel,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLightEmittingDiodePower,
                           &MetadataStore::setLightEmittingDiodePower,
                           instrumentIndex, lightSource);
                  transfer(&MetadataRetrieve::getLightEmittingDiodeSerialNumber,
                           &MetadataStore::setLightEmittingDiodeSerialNumber,
                           instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getLightEmittingDiodeAnnotationRef,
                               &MetadataStore::setLightEmittingDiodeAnnotationRef,
                               instrumentIndex, lightSource, r);
                    }
                }
            }
        }
    }

    void
    convertInstruments()
    {
      index_type instrumentCount(src.getInstrumentCount());
      for (index_type i = 0; i < instrumentCount; ++i)
        {
          transfer(&MetadataRetrieve::getInstrumentID,
                   &MetadataStore::setInstrumentID,
                   i);
          transfer(&MetadataRetrieve::getMicroscopeLotNumber,
                   &MetadataStore::setMicroscopeLotNumber,
                   i);
          transfer(&MetadataRetrieve::getMicroscopeManufacturer,
                   &MetadataStore::setMicroscopeManufacturer,
                   i);
          transfer(&MetadataRetrieve::getMicroscopeModel,
                   &MetadataStore::setMicroscopeModel,
                   i);
          transfer(&MetadataRetrieve::getMicroscopeSerialNumber,
                   &MetadataStore::setMicroscopeSerialNumber,
                   i);
          transfer(&MetadataRetrieve::getMicroscopeType,
                   &MetadataStore::setMicroscopeType,
                   i);

          index_type detectorCount(src.getDetectorCount(i));
          for (index_type q = 0; q < detectorCount; ++q)
            {
              transfer(&MetadataRetrieve::getDetectorID,
                       &MetadataStore::setDetectorID,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorAmplificationGain,
                       &MetadataStore::setDetectorAmplificationGain,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorGain,
                       &MetadataStore::setDetectorGain,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorLotNumber,
                       &MetadataStore::setDetectorLotNumber,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorManufacturer,
                       &MetadataStore::setDetectorManufacturer,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorModel,
                       &MetadataStore::setDetectorModel,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorOffset,
                       &MetadataStore::setDetectorOffset,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorSerialNumber,
                       &MetadataStore::setDetectorSerialNumber,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorType,
                       &MetadataStore::setDetectorType,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorVoltage,
                       &MetadataStore::setDetectorVoltage,
                       i, q);
              transfer(&MetadataRetrieve::getDetectorZoom,
                       &MetadataStore::setDetectorZoom,
                       i, q);

              index_type detectorAnnotationRefCount(src.getDetectorAnnotationRefCount(i, q));
              for (index_type r = 0; r < detectorAnnotationRefCount; ++r)
                {
                  transfer(&MetadataRetrieve::getDetectorAnnotationRef,
                           &MetadataStore::setDetectorAnnotationRef,
                           i, q, r);
                }
            }

          index_type dichroicCount(src.getDichroicCount(i));
          for (index_type q = 0; q < dichroicCount; ++q)
            {
              transfer(&MetadataRetrieve::getDichroicID,
                       &MetadataStore::setDichroicID,
                       i, q);
              transfer(&MetadataRetrieve::getDichroicLotNumber,
                       &MetadataStore::setDichroicLotNumber,
                       i, q);
              transfer(&MetadataRetrieve::getDichroicManufacturer,
                       &MetadataStore::setDichroicManufacturer,
                       i, q);
              transfer(&MetadataRetrieve::getDichroicModel,
                       &MetadataStore::setDichroicModel,
                       i, q);
              transfer(&MetadataRetrieve::getDichroicSerialNumber,
                       &MetadataStore::setDichroicSerialNumber,
                       i, q);

              index_type dichroicAnnotationRefCount(src.getDichroicAnnotationRefCount(i,q));
              for (index_type r = 0; r < dichroicAnnotationRefCount; ++r)
                {
                  transfer(&MetadataRetrieve::getDichroicAnnotationRef,
                           &MetadataStore::setDichroicAnnotationRef,
                           i, q, r);
                }
            }

          index_type filterCount(src.getFilterCount(i));
          for (index_type q = 0; q < filterCount; ++q)
            {
              transfer(&MetadataRetrieve::getFilterID,
                       &MetadataStore::setFilterID,
                       i, q);
              transfer(&MetadataRetrieve::getFilterFilterWheel,
                       &MetadataStore::setFilterFilterWheel,
                       i, q);
              transfer(&MetadataRetrieve::getFilterLotNumber,
                       &MetadataStore::setFilterLotNumber,
                       i, q);
              transfer(&MetadataRetrieve::getFilterManufacturer,
                       &MetadataStore::setFilterManufacturer,
                       i, q);
              transfer(&MetadataRetrieve::getFilterModel,
                       &MetadataStore::setFilterModel,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSerialNumber,
                       &MetadataStore::setFilterSerialNumber,
                       i, q);
              transfer(&MetadataRetrieve::getFilterType,
                       &MetadataStore::setFilterType,
                       i, q);
              transfer(&MetadataRetrieve::getTransmittanceRangeCutIn,
                       &MetadataStore::setTransmittanceRangeCutIn,
                       i, q);
              transfer(&MetadataRetrieve::getTransmittanceRangeCutInTolerance,
                       &MetadataStore::setTransmittanceRangeCutInTolerance,
                       i, q);
              transfer(&MetadataRetrieve::getTransmittanceRangeCutOut,
                       &MetadataStore::setTransmittanceRangeCutOut,
                       i, q);
              transfer(&MetadataRetrieve::getTransmittanceRangeCutOutTolerance,
                       &MetadataStore::setTransmittanceRangeCutOutTolerance,
                       i, q);
              transfer(&MetadataRetrieve::getTransmittanceRangeTransmittance,
                       &MetadataStore::setTransmittanceRangeTransmittance,
                       i, q);

              index_type filterAnnotationRefCount(src.getFilterAnnotationRefCount(i, q));
              for (index_type r = 0; r < filterAnnotationRefCount; ++r)
                {
                  transfer(&MetadataRetrieve::getFilterAnnotationRef,
                           &MetadataStore::setFilterAnnotationRef,
                           i, q, r);
                }
            }

          index_type objectiveCount(src.getObjectiveCount(i));
          for (index_type q = 0; q < objectiveCount; ++q)
            {
              transfer(&MetadataRetrieve::getObjectiveID,
                       &MetadataStore::setObjectiveID,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveCalibratedMagnification,
                       &MetadataStore::setObjectiveCalibratedMagnification,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveCorrection,
                       &MetadataStore::setObjectiveCorrection,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveImmersion,
                       &MetadataStore::setObjectiveImmersion,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveIris,
                       &MetadataStore::setObjectiveIris,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveLensNA,
                       &MetadataStore::setObjectiveLensNA,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveLotNumber,
                       &MetadataStore::setObjectiveLotNumber,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveManufacturer,
                       &MetadataStore::setObjectiveManufacturer,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveModel,
                       &MetadataStore::setObjectiveModel,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveNominalMagnification,
                       &MetadataStore::setObjectiveNominalMagnification,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveSerialNumber,
                       &MetadataStore::setObjectiveSerialNumber,
                       i, q);
              transfer(&MetadataRetrieve::getObjectiveWorkingDistance,
                       &MetadataStore::setObjectiveWorkingDistance,
                       i, q);

              index_type objectiveAnnotationRefCount(src.getObjectiveAnnotationRefCount(i, q));
              for (index_type r = 0; r < objectiveAnnotationRefCount; ++r)
                {
                  transfer(&MetadataRetrieve::getObjectiveAnnotationRef,
                           &MetadataStore::setObjectiveAnnotationRef,
                           i, q, r);
                }
            }

          index_type filterSetCount(src.getFilterSetCount(i));
          for (index_type q = 0; q < filterSetCount; ++q)
            {
              transfer(&MetadataRetrieve::getFilterSetID,
                       &MetadataStore::setFilterSetID,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSetDichroicRef,
                       &MetadataStore::setFilterSetDichroicRef,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSetLotNumber,
                       &MetadataStore::setFilterSetLotNumber,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSetManufacturer,
                       &MetadataStore::setFilterSetManufacturer,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSetModel,
                       &MetadataStore::setFilterSetModel,
                       i, q);
              transfer(&MetadataRetrieve::getFilterSetSerialNumber,
                       &MetadataStore::setFilterSetSerialNumber,
                       i, q);

              index_type emFilterCount(src.getFilterSetEmissionFilterRefCount(i, q));
              for (index_type f = 0; f < emFilterCount; ++f)
                {
                  transfer(&MetadataRetrieve::getFilterSetEmissionFilterRef,
                           &MetadataStore::setFilterSetEmissionFilterRef,
                           i, q, f);
                }

              index_type exFilterCount(src.getFilterSetExcitationFilterRefCount(i, q));
              for (index_type f = 0; f < exFilterCount; ++f)
                {
                  transfer(&MetadataRetrieve::getFilterSetExcitationFilterRef,
                           &MetadataStore::setFilterSetExcitationFilterRef,
                           i, q, f);
                }
            }
          convertLightSources(i);
        }
    }

    void
    convertListAnnotations()
    {
      index_type listAnnotationCount(src.getListAnnotationCount());
      for (index_type i = 0; i < listAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getListAnnotationID,
                   &MetadataStore::setListAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getListAnnotationDescription,
                   &MetadataStore::setListAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getListAnnotationNamespace,
                   &MetadataStore::setListAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getListAnnotationAnnotator,
                   &MetadataStore::setListAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getListAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getListAnnotationAnnotationRef,
                       &MetadataStore::setListAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertLongAnnotations()
    {
      index_type longAnnotationCount(src.getLongAnnotationCount());
      for (index_type i = 0; i < longAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getLongAnnotationID,
                   &MetadataStore::setLongAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getLongAnnotationDescription,
                   &MetadataStore::setLongAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getLongAnnotationNamespace,
                   &MetadataStore::setLongAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getLongAnnotationValue,
                   &MetadataStore::setLongAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getLongAnnotationAnnotator,
                   &MetadataStore::setLongAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getLongAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getLongAnnotationAnnotationRef,
                       &MetadataStore::setLongAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertMapAnnotations()
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
    convertPlates()
    {
      index_type plateCount(src.getPlateCount());
      for (index_type i = 0; i < plateCount; ++i)
        {
          transfer(&MetadataRetrieve::getPlateID,
                   &MetadataStore::setPlateID,
                   i);
          transfer(&MetadataRetrieve::getPlateColumnNamingConvention,
                   &MetadataStore::setPlateColumnNamingConvention,
                   i);
          transfer(&MetadataRetrieve::getPlateColumns,
                   &MetadataStore::setPlateColumns,
                   i);
          transfer(&MetadataRetrieve::getPlateDescription,
                   &MetadataStore::setPlateDescription,
                   i);
          transfer(&MetadataRetrieve::getPlateExternalIdentifier,
                   &MetadataStore::setPlateExternalIdentifier,
                   i);
          transfer(&MetadataRetrieve::getPlateFieldIndex,
                   &MetadataStore::setPlateFieldIndex,
                   i);
          transfer(&MetadataRetrieve::getPlateName,
                   &MetadataStore::setPlateName,
                   i);
          transfer(&MetadataRetrieve::getPlateRowNamingConvention,
                   &MetadataStore::setPlateRowNamingConvention,
                   i);
          transfer(&MetadataRetrieve::getPlateRows,
                   &MetadataStore::setPlateRows,
                   i);
          transfer(&MetadataRetrieve::getPlateStatus,
                   &MetadataStore::setPlateStatus,
                   i);
          transfer(&MetadataRetrieve::getPlateWellOriginX,
                   &MetadataStore::setPlateWellOriginX,
                   i);
          transfer(&MetadataRetrieve::getPlateWellOriginY,
                   &MetadataStore::setPlateWellOriginY,
                   i);

          index_type wellCount(src.getWellCount(i));
          for (index_type q = 0; q < wellCount; ++q)
            {
              transfer(&MetadataRetrieve::getWellID,
                       &MetadataStore::setWellID,
                       i, q);
              transfer(&MetadataRetrieve::getWellColor,
                       &MetadataStore::setWellColor,
                       i, q);
              transfer(&MetadataRetrieve::getWellColumn,
                       &MetadataStore::setWellColumn,
                       i, q);
              transfer(&MetadataRetrieve::getWellExternalDescription,
                       &MetadataStore::setWellExternalDescription,
                       i, q);
              transfer(&MetadataRetrieve::getWellExternalIdentifier,
                       &MetadataStore::setWellExternalIdentifier,
                       i, q);
              transfer(&MetadataRetrieve::getWellReagentRef,
                       &MetadataStore::setWellReagentRef,
                       i, q);
              transfer(&MetadataRetrieve::getWellRow,
                       &MetadataStore::setWellRow,
                       i, q);
              transfer(&MetadataRetrieve::getWellType,
                       &MetadataStore::setWellType,
                       i, q);

              index_type wellAnnotationRefCount(src.getWellAnnotationRefCount(i, q));
              for (index_type a = 0; a < wellAnnotationRefCount; ++a)
                {
                  transfer(&MetadataRetrieve::getWellAnnotationRef,
                           &MetadataStore::setWellAnnotationRef,
                           i, q, a);
                }

              index_type wellSampleCount(src.getWellSampleCount(i, q));
              for (index_type w = 0; w < wellSampleCount; ++w)
                {
                  transfer(&MetadataRetrieve::getWellSampleID,
                           &MetadataStore::setWellSampleID,
                           i, q, w);
                  transfer(&MetadataRetrieve::getWellSampleIndex,
                           &MetadataStore::setWellSampleIndex,
                           i, q, w);
                  transfer(&MetadataRetrieve::getWellSampleImageRef,
                           &MetadataStore::setWellSampleImageRef,
                           i, q, w);
                  transfer(&MetadataRetrieve::getWellSamplePositionX,
                           &MetadataStore::setWellSamplePositionX,
                           i, q, w);
                  transfer(&MetadataRetrieve::getWellSamplePositionY,
                           &MetadataStore::setWellSamplePositionY,
                           i, q, w);
                  transfer(&MetadataRetrieve::getWellSampleTimepoint,
                           &MetadataStore::setWellSampleTimepoint,
                           i, q, w);
                }
            }

          index_type plateAcquisitionCount(src.getPlateAcquisitionCount(i));
          for (index_type q = 0; q < plateAcquisitionCount; ++q)
            {
              transfer(&MetadataRetrieve::getPlateAcquisitionID,
                       &MetadataStore::setPlateAcquisitionID,
                       i, q);
              transfer(&MetadataRetrieve::getPlateAcquisitionDescription,
                       &MetadataStore::setPlateAcquisitionDescription,
                       i, q);
              transfer(&MetadataRetrieve::getPlateAcquisitionEndTime,
                       &MetadataStore::setPlateAcquisitionEndTime,
                       i, q);
              transfer(&MetadataRetrieve::getPlateAcquisitionMaximumFieldCount,
                       &MetadataStore::setPlateAcquisitionMaximumFieldCount,
                       i, q);
              transfer(&MetadataRetrieve::getPlateAcquisitionName,
                       &MetadataStore::setPlateAcquisitionName,
                       i, q);
              transfer(&MetadataRetrieve::getPlateAcquisitionStartTime,
                       &MetadataStore::setPlateAcquisitionStartTime,
                       i, q);

              index_type plateAcquisitionAnnotationRefCount(src.getPlateAcquisitionAnnotationRefCount(i, q));
              for (index_type a = 0; a < plateAcquisitionAnnotationRefCount; ++a)
                {
                  transfer(&MetadataRetrieve::getPlateAcquisitionAnnotationRef,
                           &MetadataStore::setPlateAcquisitionAnnotationRef,
                           i, q, a);
                }

              index_type wellSampleRefCount(src.getWellSampleRefCount(i, q));
              for (index_type w = 0; w < wellSampleRefCount; ++w)
                {
                  transfer(&MetadataRetrieve::getPlateAcquisitionWellSampleRef,
                           &MetadataStore::setPlateAcquisitionWellSampleRef,
                           i, q, w);
                }
            }

          index_type plateAnnotationRefCount(src.getPlateAnnotationRefCount(i));
          for (index_type q = 0; q < plateAnnotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getPlateAnnotationRef,
                       &MetadataStore::setPlateAnnotationRef,
                       i, q);
            }
        }
    }

    void
    convertProjects()
    {
      index_type projectCount(src.getProjectCount());
      for (index_type i = 0; i < projectCount; ++i)
        {
          transfer(&MetadataRetrieve::getProjectID,
                   &MetadataStore::setProjectID,
                   i);
          transfer(&MetadataRetrieve::getProjectDescription,
                   &MetadataStore::setProjectDescription,
                   i);
          transfer(&MetadataRetrieve::getProjectExperimenterGroupRef,
                   &MetadataStore::setProjectExperimenterGroupRef,
                   i);
          transfer(&MetadataRetrieve::getProjectExperimenterRef,
                   &MetadataStore::setProjectExperimenterRef,
                   i);
          transfer(&MetadataRetrieve::getProjectName,
                   &MetadataStore::setProjectName,
                   i);

          index_type annotationRefCount(src.getProjectAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getProjectAnnotationRef,
                       &MetadataStore::setProjectAnnotationRef,
                       i, q);
            }

          index_type datasetRefCount(src.getDatasetRefCount(i));
          for (index_type q = 0; q < datasetRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getProjectDatasetRef,
                       &MetadataStore::setProjectDatasetRef,
                       i, q);
            }
        }
    }

    void
    convertROIs()
    {
      index_type roiCount(src.getROICount());
      for (index_type i = 0; i < roiCount; ++i)
        {
          transfer(&MetadataRetrieve::getROIID,
                   &MetadataStore::setROIID,
                   i);
          transfer(&MetadataRetrieve::getROIName,
                   &MetadataStore::setROIName,
                   i);
          transfer(&MetadataRetrieve::getROIDescription,
                   &MetadataStore::setROIDescription,
                   i);
          transfer(&MetadataRetrieve::getROINamespace,
                   &MetadataStore::setROINamespace,
                   i);

          index_type shapeCount(src.getShapeCount(i));
          for (index_type q = 0; q < shapeCount; ++q)
            {
              std::string type = src.getShapeType(i, q);

              if (type == "Ellipse")
                {
                  transfer(&MetadataRetrieve::getEllipseID,
                           &MetadataStore::setEllipseID,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseFillColor,
                           &MetadataStore::setEllipseFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseFillRule,
                           &MetadataStore::setEllipseFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseFontFamily,
                           &MetadataStore::setEllipseFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseFontSize,
                           &MetadataStore::setEllipseFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseFontStyle,
                           &MetadataStore::setEllipseFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseLineCap,
                           &MetadataStore::setEllipseLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseLocked,
                           &MetadataStore::setEllipseLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseStrokeColor,
                           &MetadataStore::setEllipseStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseStrokeDashArray,
                           &MetadataStore::setEllipseStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseStrokeWidth,
                           &MetadataStore::setEllipseStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseText,
                           &MetadataStore::setEllipseText,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseTheZ,
                           &MetadataStore::setEllipseTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseTheT,
                           &MetadataStore::setEllipseTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseTheC,
                           &MetadataStore::setEllipseTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseTransform,
                           &MetadataStore::setEllipseTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseVisible,
                           &MetadataStore::setEllipseVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseRadiusX,
                           &MetadataStore::setEllipseRadiusX,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseRadiusY,
                           &MetadataStore::setEllipseRadiusY,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseX,
                           &MetadataStore::setEllipseX,
                           i, q);
                  transfer(&MetadataRetrieve::getEllipseY,
                           &MetadataStore::setEllipseY,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getEllipseAnnotationRef,
                               &MetadataStore::setEllipseAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Label")
                {
                  transfer(&MetadataRetrieve::getLabelID,
                           &MetadataStore::setLabelID,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelFillColor,
                           &MetadataStore::setLabelFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelFillRule,
                           &MetadataStore::setLabelFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelFontFamily,
                           &MetadataStore::setLabelFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelFontSize,
                           &MetadataStore::setLabelFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelFontStyle,
                           &MetadataStore::setLabelFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelLineCap,
                           &MetadataStore::setLabelLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelLocked,
                           &MetadataStore::setLabelLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelStrokeColor,
                           &MetadataStore::setLabelStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelStrokeDashArray,
                           &MetadataStore::setLabelStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelStrokeWidth,
                           &MetadataStore::setLabelStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelText,
                           &MetadataStore::setLabelText,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelTheZ,
                           &MetadataStore::setLabelTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelTheT,
                           &MetadataStore::setLabelTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelTheC,
                           &MetadataStore::setLabelTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelTransform,
                           &MetadataStore::setLabelTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelVisible,
                           &MetadataStore::setLabelVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelX,
                           &MetadataStore::setLabelX,
                           i, q);
                  transfer(&MetadataRetrieve::getLabelY,
                           &MetadataStore::setLabelY,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getLabelAnnotationRef,
                               &MetadataStore::setLabelAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Line")
                {
                  transfer(&MetadataRetrieve::getLineID,
                           &MetadataStore::setLineID,
                           i, q);
                  transfer(&MetadataRetrieve::getLineFillColor,
                           &MetadataStore::setLineFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getLineFillRule,
                           &MetadataStore::setLineFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getLineFontFamily,
                           &MetadataStore::setLineFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getLineFontSize,
                           &MetadataStore::setLineFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getLineFontStyle,
                           &MetadataStore::setLineFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getLineLineCap,
                           &MetadataStore::setLineLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getLineLocked,
                           &MetadataStore::setLineLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getLineStrokeColor,
                           &MetadataStore::setLineStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getLineStrokeDashArray,
                           &MetadataStore::setLineStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getLineStrokeWidth,
                           &MetadataStore::setLineStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getLineText,
                           &MetadataStore::setLineText,
                           i, q);
                  transfer(&MetadataRetrieve::getLineTheZ,
                           &MetadataStore::setLineTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getLineTheT,
                           &MetadataStore::setLineTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getLineTheC,
                           &MetadataStore::setLineTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getLineTransform,
                           &MetadataStore::setLineTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getLineVisible,
                           &MetadataStore::setLineVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getLineMarkerEnd,
                           &MetadataStore::setLineMarkerEnd,
                           i, q);
                  transfer(&MetadataRetrieve::getLineMarkerStart,
                           &MetadataStore::setLineMarkerStart,
                           i, q);
                  transfer(&MetadataRetrieve::getLineX1,
                           &MetadataStore::setLineX1,
                           i, q);
                  transfer(&MetadataRetrieve::getLineX2,
                           &MetadataStore::setLineX2,
                           i, q);
                  transfer(&MetadataRetrieve::getLineY1,
                           &MetadataStore::setLineY1,
                           i, q);
                  transfer(&MetadataRetrieve::getLineY2,
                           &MetadataStore::setLineY2,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getLineAnnotationRef,
                               &MetadataStore::setLineAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Mask")
                {
                  transfer(&MetadataRetrieve::getMaskID,
                           &MetadataStore::setMaskID,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskFillColor,
                           &MetadataStore::setMaskFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskFillRule,
                           &MetadataStore::setMaskFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskFontFamily,
                           &MetadataStore::setMaskFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskFontSize,
                           &MetadataStore::setMaskFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskFontStyle,
                           &MetadataStore::setMaskFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskLineCap,
                           &MetadataStore::setMaskLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskLocked,
                           &MetadataStore::setMaskLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskStrokeColor,
                           &MetadataStore::setMaskStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskStrokeDashArray,
                           &MetadataStore::setMaskStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskStrokeWidth,
                           &MetadataStore::setMaskStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskText,
                           &MetadataStore::setMaskText,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskTheZ,
                           &MetadataStore::setMaskTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskTheT,
                           &MetadataStore::setMaskTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskTheC,
                           &MetadataStore::setMaskTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskTransform,
                           &MetadataStore::setMaskTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskVisible,
                           &MetadataStore::setMaskVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskHeight,
                           &MetadataStore::setMaskHeight,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskWidth,
                           &MetadataStore::setMaskWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskX,
                           &MetadataStore::setMaskX,
                           i, q);
                  transfer(&MetadataRetrieve::getMaskY,
                           &MetadataStore::setMaskY,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getMaskAnnotationRef,
                               &MetadataStore::setMaskAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Point")
                {
                  transfer(&MetadataRetrieve::getPointID,
                           &MetadataStore::setPointID,
                           i, q);
                  transfer(&MetadataRetrieve::getPointFillColor,
                           &MetadataStore::setPointFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPointFillRule,
                           &MetadataStore::setPointFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getPointFontFamily,
                           &MetadataStore::setPointFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getPointFontSize,
                           &MetadataStore::setPointFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getPointFontStyle,
                           &MetadataStore::setPointFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getPointLineCap,
                           &MetadataStore::setPointLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getPointLocked,
                           &MetadataStore::setPointLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getPointStrokeColor,
                           &MetadataStore::setPointStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPointStrokeDashArray,
                           &MetadataStore::setPointStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getPointStrokeWidth,
                           &MetadataStore::setPointStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getPointText,
                           &MetadataStore::setPointText,
                           i, q);
                  transfer(&MetadataRetrieve::getPointTheZ,
                           &MetadataStore::setPointTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getPointTheT,
                           &MetadataStore::setPointTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getPointTheC,
                           &MetadataStore::setPointTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getPointTransform,
                           &MetadataStore::setPointTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getPointVisible,
                           &MetadataStore::setPointVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getPointX,
                           &MetadataStore::setPointX,
                           i, q);
                  transfer(&MetadataRetrieve::getPointY,
                           &MetadataStore::setPointY,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getPointAnnotationRef,
                               &MetadataStore::setPointAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Polygon")
                {
                  transfer(&MetadataRetrieve::getPolygonID,
                           &MetadataStore::setPolygonID,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonFillColor,
                           &MetadataStore::setPolygonFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonFillRule,
                           &MetadataStore::setPolygonFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonFontFamily,
                           &MetadataStore::setPolygonFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonFontSize,
                           &MetadataStore::setPolygonFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonFontStyle,
                           &MetadataStore::setPolygonFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonLineCap,
                           &MetadataStore::setPolygonLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonLocked,
                           &MetadataStore::setPolygonLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonStrokeColor,
                           &MetadataStore::setPolygonStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonStrokeDashArray,
                           &MetadataStore::setPolygonStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonStrokeWidth,
                           &MetadataStore::setPolygonStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonText,
                           &MetadataStore::setPolygonText,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonTheZ,
                           &MetadataStore::setPolygonTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonTheT,
                           &MetadataStore::setPolygonTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonTheC,
                           &MetadataStore::setPolygonTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonTransform,
                           &MetadataStore::setPolygonTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonVisible,
                           &MetadataStore::setPolygonVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getPolygonPoints,
                           &MetadataStore::setPolygonPoints,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getPolygonAnnotationRef,
                               &MetadataStore::setPolygonAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Polyline")
                {
                  transfer(&MetadataRetrieve::getPolylineID,
                           &MetadataStore::setPolylineID,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineFillColor,
                           &MetadataStore::setPolylineFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineFillRule,
                           &MetadataStore::setPolylineFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineFontFamily,
                           &MetadataStore::setPolylineFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineFontSize,
                           &MetadataStore::setPolylineFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineFontStyle,
                           &MetadataStore::setPolylineFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineLineCap,
                           &MetadataStore::setPolylineLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineLocked,
                           &MetadataStore::setPolylineLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineStrokeColor,
                           &MetadataStore::setPolylineStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineStrokeDashArray,
                           &MetadataStore::setPolylineStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineStrokeWidth,
                           &MetadataStore::setPolylineStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineText,
                           &MetadataStore::setPolylineText,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineTheZ,
                           &MetadataStore::setPolylineTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineTheT,
                           &MetadataStore::setPolylineTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineTheC,
                           &MetadataStore::setPolylineTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineTransform,
                           &MetadataStore::setPolylineTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineVisible,
                           &MetadataStore::setPolylineVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineMarkerEnd,
                           &MetadataStore::setPolylineMarkerEnd,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylineMarkerStart,
                           &MetadataStore::setPolylineMarkerStart,
                           i, q);
                  transfer(&MetadataRetrieve::getPolylinePoints,
                           &MetadataStore::setPolylinePoints,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getPolylineAnnotationRef,
                               &MetadataStore::setPolylineAnnotationRef,
                               i, q, r);
                    }
                }
              else if (type == "Rectangle")
                {
                  transfer(&MetadataRetrieve::getRectangleID,
                           &MetadataStore::setRectangleID,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleFillColor,
                           &MetadataStore::setRectangleFillColor,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleFillRule,
                           &MetadataStore::setRectangleFillRule,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleFontFamily,
                           &MetadataStore::setRectangleFontFamily,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleFontSize,
                           &MetadataStore::setRectangleFontSize,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleFontStyle,
                           &MetadataStore::setRectangleFontStyle,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleLineCap,
                           &MetadataStore::setRectangleLineCap,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleLocked,
                           &MetadataStore::setRectangleLocked,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleStrokeColor,
                           &MetadataStore::setRectangleStrokeColor,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleStrokeDashArray,
                           &MetadataStore::setRectangleStrokeDashArray,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleStrokeWidth,
                           &MetadataStore::setRectangleStrokeWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleText,
                           &MetadataStore::setRectangleText,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleTheZ,
                           &MetadataStore::setRectangleTheZ,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleTheT,
                           &MetadataStore::setRectangleTheT,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleTheC,
                           &MetadataStore::setRectangleTheC,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleTransform,
                           &MetadataStore::setRectangleTransform,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleVisible,
                           &MetadataStore::setRectangleVisible,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleHeight,
                           &MetadataStore::setRectangleHeight,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleWidth,
                           &MetadataStore::setRectangleWidth,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleX,
                           &MetadataStore::setRectangleX,
                           i, q);
                  transfer(&MetadataRetrieve::getRectangleY,
                           &MetadataStore::setRectangleY,
                           i, q);

                  index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                  for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                    {
                      transfer(&MetadataRetrieve::getRectangleAnnotationRef,
                               &MetadataStore::setRectangleAnnotationRef,
                               i, q, r);
                    }
                }
            }

          index_type annotationRefCount(src.getROIAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getROIAnnotationRef,
                       &MetadataStore::setROIAnnotationRef,
                       i, q);
            }
        }
    }

    void
    convertScreens()
    {
      index_type screenCount(src.getScreenCount());
      for (index_type i = 0; i < screenCount; ++i)
        {
          transfer(&MetadataRetrieve::getScreenID,
                   &MetadataStore::setScreenID,
                   i);
          transfer(&MetadataRetrieve::getScreenName,
                   &MetadataStore::setScreenName,
                   i);
          transfer(&MetadataRetrieve::getScreenDescription,
                   &MetadataStore::setScreenDescription,
                   i);
          transfer(&MetadataRetrieve::getScreenProtocolDescription,
                   &MetadataStore::setScreenProtocolDescription,
                   i);
          transfer(&MetadataRetrieve::getScreenProtocolIdentifier,
                   &MetadataStore::setScreenProtocolIdentifier,
                   i);
          transfer(&MetadataRetrieve::getScreenReagentSetDescription,
                   &MetadataStore::setScreenReagentSetDescription,
                   i);
          transfer(&MetadataRetrieve::getScreenReagentSetIdentifier,
                   &MetadataStore::setScreenReagentSetIdentifier,
                   i);
          transfer(&MetadataRetrieve::getScreenType,
                   &MetadataStore::setScreenType,
                   i);

          index_type plateRefCount(src.getPlateRefCount(i));
          for (index_type q = 0; q < plateRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getScreenPlateRef,
                       &MetadataStore::setScreenPlateRef,
                       i, q);
            }

          index_type annotationRefCount(src.getScreenAnnotationRefCount(i));
          for (index_type q = 0; q < annotationRefCount; ++q)
            {
              transfer(&MetadataRetrieve::getScreenAnnotationRef,
                       &MetadataStore::setScreenAnnotationRef,
                       i, q);
            }

          index_type reagentCount(src.getReagentCount(i));
          for (index_type q = 0; q < reagentCount; ++q)
            {
              transfer(&MetadataRetrieve::getReagentID,
                       &MetadataStore::setReagentID,
                       i, q);
              transfer(&MetadataRetrieve::getReagentDescription,
                       &MetadataStore::setReagentDescription,
                       i, q);
              transfer(&MetadataRetrieve::getReagentName,
                       &MetadataStore::setReagentName,
                       i, q);
              transfer(&MetadataRetrieve::getReagentReagentIdentifier,
                       &MetadataStore::setReagentReagentIdentifier,
                       i, q);

              index_type reagentAnnotationRefCount(src.getReagentAnnotationRefCount(i, q));
              for (index_type r = 0; r < reagentAnnotationRefCount; ++r)
                {
                  transfer(&MetadataRetrieve::getReagentAnnotationRef,
                           &MetadataStore::setReagentAnnotationRef,
                           i, q, r);
                }
            }
        }
    }

    void
    convertTagAnnotations()
    {
      index_type tagAnnotationCount(src.getTagAnnotationCount());
      for (index_type i = 0; i < tagAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getTagAnnotationID,
                   &MetadataStore::setTagAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getTagAnnotationDescription,
                   &MetadataStore::setTagAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getTagAnnotationNamespace,
                   &MetadataStore::setTagAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getTagAnnotationValue,
                   &MetadataStore::setTagAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getTagAnnotationAnnotator,
                   &MetadataStore::setTagAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getTagAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getTagAnnotationAnnotationRef,
                       &MetadataStore::setTagAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertTermAnnotations()
    {
      index_type termAnnotationCount(src.getTermAnnotationCount());
      for (index_type i = 0; i < termAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getTermAnnotationID,
                   &MetadataStore::setTermAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getTermAnnotationDescription,
                   &MetadataStore::setTermAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getTermAnnotationNamespace,
                   &MetadataStore::setTermAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getTermAnnotationValue,
                   &MetadataStore::setTermAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getTermAnnotationAnnotator,
                   &MetadataStore::setTermAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getTermAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getTermAnnotationAnnotationRef,
                       &MetadataStore::setTermAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertTimestampAnnotations()
    {
      index_type timestampAnnotationCount(src.getTimestampAnnotationCount());
      for (index_type i = 0; i < timestampAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getTimestampAnnotationID,
                   &MetadataStore::setTimestampAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getTimestampAnnotationDescription,
                   &MetadataStore::setTimestampAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getTimestampAnnotationNamespace,
                   &MetadataStore::setTimestampAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getTimestampAnnotationValue,
                   &MetadataStore::setTimestampAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getTimestampAnnotationAnnotator,
                   &MetadataStore::setTimestampAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getTimestampAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getTimestampAnnotationAnnotationRef,
                       &MetadataStore::setTimestampAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertXMLAnnotations()
    {
      index_type xmlAnnotationCount(src.getXMLAnnotationCount());
      for (index_type i = 0; i < xmlAnnotationCount; ++i)
        {
          transfer(&MetadataRetrieve::getXMLAnnotationID,
                   &MetadataStore::setXMLAnnotationID,
                   i);
          transfer(&MetadataRetrieve::getXMLAnnotationDescription,
                   &MetadataStore::setXMLAnnotationDescription,
                   i);
          transfer(&MetadataRetrieve::getXMLAnnotationNamespace,
                   &MetadataStore::setXMLAnnotationNamespace,
                   i);
          transfer(&MetadataRetrieve::getXMLAnnotationValue,
                   &MetadataStore::setXMLAnnotationValue,
                   i);
          transfer(&MetadataRetrieve::getXMLAnnotationAnnotator,
                   &MetadataStore::setXMLAnnotationAnnotator,
                   i);

          index_type annotationRefCount(src.getXMLAnnotationAnnotationCount(i));
          for (index_type a = 0; a < annotationRefCount; ++a)
            {
              transfer(&MetadataRetrieve::getXMLAnnotationAnnotationRef,
                       &MetadataStore::setXMLAnnotationAnnotationRef,
                       i, a);
            }
        }
    }

    void
    convertRootAttributes()
    {
      transfer(&MetadataRetrieve::getUUID,
               &MetadataStore::setUUID);
      transfer(&MetadataRetrieve::getRightsRightsHeld,
               &MetadataStore::setRightsRightsHeld);
      transfer(&MetadataRetrieve::getRightsRightsHolder,
               &MetadataStore::setRightsRightsHolder);
      transfer(&MetadataRetrieve::getBinaryOnlyMetadataFile,
               &MetadataStore::setBinaryOnlyMetadataFile);
      transfer(&MetadataRetrieve::getBinaryOnlyUUID,
               &MetadataStore::setBinaryOnlyUUID);
    }
  };

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
        MetadataConverter(src, dest);
      }

    }
  }
}
