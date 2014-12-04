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

  /**
   * Copy metadata between MetadataRetrieve and MetadataStore
   * instances.
   */
  class MetadataConverter
  {
  private:
    /// Source object.
    const MetadataRetrieve& src;
    // Destination object.
    MetadataStore&          dest;

  public:
    /// Index type.
    typedef ome::xml::meta::BaseMetadata::index_type index_type;
    // MetadataRetrieve abbreviation.
    typedef ome::xml::meta::MetadataRetrieve MR;
    // MetadataStore abbrebviation.
    typedef ome::xml::meta::MetadataStore MS;

    /**
     * Constructor.
     *
     * @param src the source object.
     * @param dest the destination object.
     */
    MetadataConverter(const MetadataRetrieve& src,
                      MetadataStore&          dest):
      src(src),
      dest(dest)
    {
    }

    /**
     * Perform metadata conversion.
     */
    void
    operator()()
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

    /**
     * Transfer a single metadata value (no arguments).
     *
     * @param get a getter method from MetadataRetrieve.
     * @param set a setter method from MetadataStore.
     * @returns @c true if the transfer succeeded, @c false if an
     * exception was thrown.
     */
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

    /**
     * Transfer a single metadata value (one index argument).
     *
     * @param get a getter method from MetadataRetrieve.
     * @param set a setter method from MetadataStore.
     * @returns @c true if the transfer succeeded, @c false if an
     * exception was thrown.
     */
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

    /**
     * Transfer a single metadata value (two index arguments).
     *
     * @param get a getter method from MetadataRetrieve.
     * @param set a setter method from MetadataStore.
     * @returns @c true if the transfer succeeded, @c false if an
     * exception was thrown.
     */
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

    /**
     * Transfer a single metadata value (three index arguments).
     *
     * @param get a getter method from MetadataRetrieve.
     * @param set a setter method from MetadataStore.
     * @returns @c true if the transfer succeeded, @c false if an
     * exception was thrown.
     */
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

    /// Convert boolean annotations.
    void
    convertBooleanAnnotations()
    {
      index_type booleanAnnotationCount(src.getBooleanAnnotationCount());
      for (index_type i = 0; i < booleanAnnotationCount; ++i)
        {
          if (transfer(&MR::getBooleanAnnotationID,          &MS::setBooleanAnnotationID,          i))
            {
              transfer(&MR::getBooleanAnnotationDescription, &MS::setBooleanAnnotationDescription, i);
              transfer(&MR::getBooleanAnnotationNamespace,   &MS::setBooleanAnnotationNamespace,   i);
              transfer(&MR::getBooleanAnnotationValue,       &MS::setBooleanAnnotationValue,       i);
              transfer(&MR::getBooleanAnnotationAnnotator,   &MS::setBooleanAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getBooleanAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getBooleanAnnotationAnnotationRef, &MS::setBooleanAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert comment annotations.
    void
    convertCommentAnnotations()
    {
      index_type commentAnnotationCount(src.getCommentAnnotationCount());
      for (index_type i = 0; i < commentAnnotationCount; ++i)
        {
          if (transfer(&MR::getCommentAnnotationID,          &MS::setCommentAnnotationID,          i))
            {
              transfer(&MR::getCommentAnnotationDescription, &MS::setCommentAnnotationDescription, i);
              transfer(&MR::getCommentAnnotationNamespace,   &MS::setCommentAnnotationNamespace,   i);
              transfer(&MR::getCommentAnnotationValue,       &MS::setCommentAnnotationValue,       i);
              transfer(&MR::getCommentAnnotationAnnotator,   &MS::setCommentAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getCommentAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getCommentAnnotationAnnotationRef, &MS::setCommentAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert datasets.
    void
    convertDatasets()
    {
      index_type datasets(src.getDatasetCount());
      for (index_type i = 0; i < datasets; ++i)
        {
          if (transfer(&MR::getDatasetID,                   &MS::setDatasetID,                   i))
            {
              transfer(&MR::getDatasetDescription,          &MS::setDatasetDescription,          i);
              transfer(&MR::getDatasetExperimenterGroupRef, &MS::setDatasetExperimenterGroupRef, i);
              transfer(&MR::getDatasetExperimenterRef,      &MS::setDatasetExperimenterRef,      i);
              transfer(&MR::getDatasetName,                 &MS::setDatasetName,                 i);

              index_type imageRefCount(src.getDatasetImageRefCount(i));
              for (index_type q = 0; q < imageRefCount; ++q)
                transfer(&MR::getDatasetImageRef, &MS::setDatasetImageRef, i, q);

              index_type annotationRefCount = src.getDatasetAnnotationRefCount(i);
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getDatasetAnnotationRef, &MS::setDatasetAnnotationRef, i, q);
            }
        }
    }

    /// Convert double annotations.
    void
    convertDoubleAnnotations()
    {
      index_type doubleAnnotationCount(src.getDoubleAnnotationCount());
      for (index_type i = 0; i < doubleAnnotationCount; ++i)
        {
          if (transfer(&MR::getDoubleAnnotationID,          &MS::setDoubleAnnotationID,          i))
            {
              transfer(&MR::getDoubleAnnotationDescription, &MS::setDoubleAnnotationDescription, i);
              transfer(&MR::getDoubleAnnotationNamespace,   &MS::setDoubleAnnotationNamespace,   i);
              transfer(&MR::getDoubleAnnotationValue,       &MS::setDoubleAnnotationValue,       i);
              transfer(&MR::getDoubleAnnotationAnnotator,   &MS::setDoubleAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getDoubleAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getDoubleAnnotationAnnotationRef, &MS::setDoubleAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert experiments.
    void
    convertExperiments()
    {
      index_type experimentCount(src.getExperimentCount());
      for (index_type i = 0; i < experimentCount; ++i)
        {
          if (transfer(&MR::getExperimentID,              &MS::setExperimentID,              i))
            {
              transfer(&MR::getExperimentDescription,     &MS::setExperimentDescription,     i);
              transfer(&MR::getExperimentDescription,     &MS::setExperimentDescription,     i);
              transfer(&MR::getExperimentExperimenterRef, &MS::setExperimentExperimenterRef, i);
              transfer(&MR::getExperimentType,            &MS::setExperimentType,            i);

              index_type microbeamCount(src.getMicrobeamManipulationCount(i));
              for (index_type q = 0; q < microbeamCount; ++q)
                {
                  if (transfer(&MR::getMicrobeamManipulationID,              &MS::setMicrobeamManipulationID,              i, q))
                    {
                      transfer(&MR::getMicrobeamManipulationDescription,     &MS::setMicrobeamManipulationDescription,     i, q);
                      transfer(&MR::getMicrobeamManipulationExperimenterRef, &MS::setMicrobeamManipulationExperimenterRef, i, q);
                      transfer(&MR::getMicrobeamManipulationType,            &MS::setMicrobeamManipulationType,            i, q);

                      index_type lightSourceCount(src.getMicrobeamManipulationLightSourceSettingsCount(i, q));
                      for (index_type p = 0; p < lightSourceCount; ++p)
                        {
                          transfer(&MR::getMicrobeamManipulationLightSourceSettingsID,          &MS::setMicrobeamManipulationLightSourceSettingsID,          i, q, p);
                          transfer(&MR::getMicrobeamManipulationLightSourceSettingsAttenuation, &MS::setMicrobeamManipulationLightSourceSettingsAttenuation, i, q, p);
                          transfer(&MR::getMicrobeamManipulationLightSourceSettingsWavelength,  &MS::setMicrobeamManipulationLightSourceSettingsWavelength,  i, q, p);
                        }

                      index_type roiRefCount(src.getMicrobeamManipulationROIRefCount(i, q));
                      for (index_type p = 0; p < roiRefCount; ++p)
                        transfer(&MR::getMicrobeamManipulationROIRef, &MS::setMicrobeamManipulationROIRef, i, q, p);
                    }
                }
            }
        }
    }

    /// Convert experimenters.
    void
    convertExperimenters()
    {
      index_type experimenterCount(src.getExperimenterCount());
      for (index_type i = 0; i < experimenterCount; ++i)
        {
          if (transfer(&MR::getExperimenterID,          &MS::setExperimenterID,          i))
            {
              transfer(&MR::getExperimenterEmail,       &MS::setExperimenterEmail,       i);
              transfer(&MR::getExperimenterFirstName,   &MS::setExperimenterFirstName,   i);
              transfer(&MR::getExperimenterInstitution, &MS::setExperimenterInstitution, i);
              transfer(&MR::getExperimenterLastName,    &MS::setExperimenterLastName,    i);
              transfer(&MR::getExperimenterMiddleName,  &MS::setExperimenterMiddleName,  i);
              transfer(&MR::getExperimenterUserName,    &MS::setExperimenterUserName,    i);

              index_type annotationRefCount(src.getExperimenterAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getExperimenterAnnotationRef, &MS::setExperimenterAnnotationRef, i, q);
            }
        }
    }

    /// Convert experimenter groups.
    void
    convertExperimenterGroups()
    {
      index_type experimenterGroupCount(src.getExperimenterGroupCount());
      for (index_type i = 0; i < experimenterGroupCount; ++i)
        {
          if (transfer(&MR::getExperimenterGroupID,          &MS::setExperimenterGroupID,          i))
            {
              transfer(&MR::getExperimenterGroupDescription, &MS::setExperimenterGroupDescription, i);
              transfer(&MR::getExperimenterGroupName,        &MS::setExperimenterGroupName,        i);

              index_type annotationRefCount(src.getExperimenterGroupAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getExperimenterGroupAnnotationRef, &MS::setExperimenterGroupAnnotationRef, i, q);

              index_type experimenterRefCount(src.getExperimenterGroupExperimenterRefCount(i));
              for (index_type q = 0; q < experimenterRefCount; ++q)
                transfer(&MR::getExperimenterGroupExperimenterRef, &MS::setExperimenterGroupExperimenterRef, i, q);

              index_type leaderCount(src.getLeaderCount(i));
              for (index_type q = 0; q < leaderCount; ++q)
                transfer(&MR::getExperimenterGroupLeader, &MS::setExperimenterGroupLeader, i, q);
            }
        }
    }

    /// Convert file annotations.
    void
    convertFileAnnotations()
    {
      index_type fileAnnotationCount(src.getFileAnnotationCount());
      for (index_type i = 0; i < fileAnnotationCount; ++i)
        {
          if (transfer(&MR::getFileAnnotationID,          &MS::setFileAnnotationID,          i))
            {
              transfer(&MR::getFileAnnotationDescription, &MS::setFileAnnotationDescription, i);
              transfer(&MR::getFileAnnotationNamespace,   &MS::setFileAnnotationNamespace,   i);
              transfer(&MR::getFileAnnotationAnnotator,   &MS::setFileAnnotationAnnotator,   i);
              transfer(&MR::getBinaryFileFileName,        &MS::setBinaryFileFileName,        i);
              transfer(&MR::getBinaryFileMIMEType,        &MS::setBinaryFileMIMEType,        i);
              transfer(&MR::getBinaryFileSize,            &MS::setBinaryFileSize,            i);

              index_type annotationRefCount(src.getFileAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getFileAnnotationAnnotationRef, &MS::setFileAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert images.
    void
    convertImages()
    {
      index_type imageCount(src.getImageCount());
      for (index_type i = 0; i < imageCount; ++i)
        {
          if (transfer(&MR::getImageID,                       &MS::setImageID,                       i))
            {
              transfer(&MR::getImageAcquisitionDate,          &MS::setImageAcquisitionDate,          i);
              transfer(&MR::getImageDescription,              &MS::setImageDescription,              i);
              transfer(&MR::getImageExperimentRef,            &MS::setImageExperimentRef,            i);
              transfer(&MR::getImageExperimenterGroupRef,     &MS::setImageExperimenterGroupRef,     i);
              transfer(&MR::getImageExperimenterRef,          &MS::setImageExperimenterRef,          i);
              transfer(&MR::getImageInstrumentRef,            &MS::setImageInstrumentRef,            i);
              transfer(&MR::getImageName,                     &MS::setImageName,                     i);
              transfer(&MR::getImagingEnvironmentAirPressure, &MS::setImagingEnvironmentAirPressure, i);
              transfer(&MR::getImagingEnvironmentCO2Percent,  &MS::setImagingEnvironmentCO2Percent,  i);
              transfer(&MR::getImagingEnvironmentHumidity,    &MS::setImagingEnvironmentHumidity,    i);
              transfer(&MR::getImagingEnvironmentMap,         &MS::setImagingEnvironmentMap,         i);
              transfer(&MR::getImagingEnvironmentTemperature, &MS::setImagingEnvironmentTemperature, i);

              if (transfer(&MR::getObjectiveSettingsID, &MS::setObjectiveSettingsID, i))
                {
                  transfer(&MR::getObjectiveSettingsCorrectionCollar, &MS::setObjectiveSettingsCorrectionCollar, i);
                  transfer(&MR::getObjectiveSettingsMedium,           &MS::setObjectiveSettingsMedium,           i);
                  transfer(&MR::getObjectiveSettingsRefractiveIndex,  &MS::setObjectiveSettingsRefractiveIndex,  i);
                }

              if (transfer(&MR::getStageLabelName, &MS::setStageLabelName, i))
                {
                  transfer(&MR::getStageLabelX, &MS::setStageLabelX, i);
                  transfer(&MR::getStageLabelY, &MS::setStageLabelY, i);
                  transfer(&MR::getStageLabelZ, &MS::setStageLabelZ, i);
                }

              transfer(&MR::getPixelsID,              &MS::setPixelsID,              i);
              transfer(&MR::getPixelsDimensionOrder,  &MS::setPixelsDimensionOrder,  i);
              transfer(&MR::getPixelsPhysicalSizeX,   &MS::setPixelsPhysicalSizeX,   i);
              transfer(&MR::getPixelsPhysicalSizeY,   &MS::setPixelsPhysicalSizeY,   i);
              transfer(&MR::getPixelsPhysicalSizeZ,   &MS::setPixelsPhysicalSizeZ,   i);
              transfer(&MR::getPixelsSizeX,           &MS::setPixelsSizeX,           i);
              transfer(&MR::getPixelsSizeY,           &MS::setPixelsSizeY,           i);
              transfer(&MR::getPixelsSizeZ,           &MS::setPixelsSizeZ,           i);
              transfer(&MR::getPixelsSizeT,           &MS::setPixelsSizeT,           i);
              transfer(&MR::getPixelsSizeC,           &MS::setPixelsSizeC,           i);
              transfer(&MR::getPixelsTimeIncrement,   &MS::setPixelsTimeIncrement,   i);
              transfer(&MR::getPixelsType,            &MS::setPixelsType,            i);
              transfer(&MR::getPixelsBigEndian,       &MS::setPixelsBigEndian,       i);
              transfer(&MR::getPixelsInterleaved,     &MS::setPixelsInterleaved,     i);
              transfer(&MR::getPixelsSignificantBits, &MS::setPixelsSignificantBits, i);

              index_type binDataCount(src.getPixelsBinDataCount(i));
              for (index_type q = 0; q < binDataCount; ++q)
                transfer(&MR::getPixelsBinDataBigEndian, &MS::setPixelsBinDataBigEndian, i, q);

              index_type annotationRefCount(src.getImageAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getImageAnnotationRef, &MS::setImageAnnotationRef, i, q);

              index_type channelCount(src.getChannelCount(i));
              for (index_type c = 0; c < channelCount; ++c)
                {
                  if (transfer(&MR::getChannelID,                   &MS::setChannelID,                   i, c))
                    {
                      transfer(&MR::getChannelAcquisitionMode,      &MS::setChannelAcquisitionMode,      i, c);
                      transfer(&MR::getChannelColor,                &MS::setChannelColor,                i, c);
                      transfer(&MR::getChannelContrastMethod,       &MS::setChannelContrastMethod,       i, c);
                      transfer(&MR::getChannelEmissionWavelength,   &MS::setChannelEmissionWavelength,   i, c);
                      transfer(&MR::getChannelExcitationWavelength, &MS::setChannelExcitationWavelength, i, c);
                      transfer(&MR::getChannelFilterSetRef,         &MS::setChannelFilterSetRef,         i, c);
                      transfer(&MR::getChannelFluor,                &MS::setChannelFluor,                i, c);
                      transfer(&MR::getChannelIlluminationType,     &MS::setChannelIlluminationType,     i, c);
                      transfer(&MR::getChannelNDFilter,             &MS::setChannelNDFilter,             i, c);
                      transfer(&MR::getChannelName,                 &MS::setChannelName,                 i, c);
                      transfer(&MR::getChannelPinholeSize,          &MS::setChannelPinholeSize,          i, c);
                      transfer(&MR::getChannelPockelCellSetting,    &MS::setChannelPockelCellSetting,    i, c);
                      transfer(&MR::getChannelSamplesPerPixel,      &MS::setChannelSamplesPerPixel,      i, c);

                      if (transfer(&MR::getDetectorSettingsID, &MS::setDetectorSettingsID, i, c))
                        {
                          transfer(&MR::getDetectorSettingsBinning,     &MS::setDetectorSettingsBinning,     i, c);
                          transfer(&MR::getDetectorSettingsGain,        &MS::setDetectorSettingsGain,        i, c);
                          transfer(&MR::getDetectorSettingsIntegration, &MS::setDetectorSettingsIntegration, i, c);
                          transfer(&MR::getDetectorSettingsOffset,      &MS::setDetectorSettingsOffset,      i, c);
                          transfer(&MR::getDetectorSettingsReadOutRate, &MS::setDetectorSettingsReadOutRate, i, c);
                          transfer(&MR::getDetectorSettingsVoltage,     &MS::setDetectorSettingsVoltage,     i, c);
                          transfer(&MR::getDetectorSettingsZoom,        &MS::setDetectorSettingsZoom,        i, c);
                        }

                      transfer(&MR::getLightPathDichroicRef, &MS::setLightPathDichroicRef, i, c);

                      if (transfer(&MR::getChannelLightSourceSettingsID, &MS::setChannelLightSourceSettingsID, i, c))
                        {
                          transfer(&MR::getChannelLightSourceSettingsAttenuation, &MS::setChannelLightSourceSettingsAttenuation, i, c);
                          transfer(&MR::getChannelLightSourceSettingsWavelength,  &MS::setChannelLightSourceSettingsWavelength,  i, c);
                        }

                      index_type channelAnnotationRefCount(src.getChannelAnnotationRefCount(i, c));
                      for (index_type q = 0; q < channelAnnotationRefCount; ++q)
                        transfer(&MR::getChannelAnnotationRef, &MS::setChannelAnnotationRef, i, c, q);

                      index_type emFilterRefCount(src.getLightPathEmissionFilterRefCount(i, c));
                      for (index_type q = 0; q < emFilterRefCount; ++q)
                        transfer(&MR::getLightPathEmissionFilterRef, &MS::setLightPathEmissionFilterRef, i, c, q);

                      index_type exFilterRefCount(src.getLightPathExcitationFilterRefCount(i, c));
                      for (index_type q = 0; q < exFilterRefCount; ++q)
                        transfer(&MR::getLightPathExcitationFilterRef, &MS::setLightPathExcitationFilterRef, i, c, q);

                      index_type lightPathAnnotationRefCount(src.getLightPathAnnotationRefCount(i, c));
                      for (index_type q = 0; q < lightPathAnnotationRefCount; ++q)
                        transfer(&MR::getLightPathAnnotationRef, &MS::setLightPathAnnotationRef, i, c, q);
                    }
                }

              index_type planeCount(src.getPlaneCount(i));
              for (index_type p = 0; p < planeCount; ++p)
                {
                  transfer(&MR::getPlaneDeltaT,       &MS::setPlaneDeltaT,       i, p);
                  transfer(&MR::getPlaneExposureTime, &MS::setPlaneExposureTime, i, p);
                  transfer(&MR::getPlaneHashSHA1,     &MS::setPlaneHashSHA1,     i, p);
                  transfer(&MR::getPlanePositionX,    &MS::setPlanePositionX,    i, p);
                  transfer(&MR::getPlanePositionY,    &MS::setPlanePositionY,    i, p);
                  transfer(&MR::getPlanePositionZ,    &MS::setPlanePositionZ,    i, p);
                  transfer(&MR::getPlaneTheZ,         &MS::setPlaneTheZ,         i, p);
                  transfer(&MR::getPlaneTheT,         &MS::setPlaneTheT,         i, p);
                  transfer(&MR::getPlaneTheC,         &MS::setPlaneTheC,         i, p);

                  index_type planeAnnotationRefCount(src.getPlaneAnnotationRefCount(i, p));
                  for (index_type q = 0; q < planeAnnotationRefCount; ++q)
                    transfer(&MR::getPlaneAnnotationRef, &MS::setPlaneAnnotationRef, i, p, q);
                }

              index_type microbeamCount(src.getMicrobeamManipulationRefCount(i));
              for (index_type q = 0; q < microbeamCount; ++q)
                transfer(&MR::getImageMicrobeamManipulationRef, &MS::setImageMicrobeamManipulationRef, i, q);

              index_type roiRefCount(src.getImageROIRefCount(i));
              for (index_type q = 0; q < roiRefCount; ++q)
                transfer(&MR::getImageROIRef, &MS::setImageROIRef, i, q);

              index_type tiffDataCount(src.getTiffDataCount(i));
              for (index_type q = 0; q < tiffDataCount; ++q)
                {
                  transfer(&MR::getUUIDValue,          &MS::setUUIDValue,          i, q);
                  transfer(&MR::getUUIDFileName,       &MS::setUUIDFileName,       i, q);
                  transfer(&MR::getTiffDataFirstZ,     &MS::setTiffDataFirstZ,     i, q);
                  transfer(&MR::getTiffDataFirstT,     &MS::setTiffDataFirstT,     i, q);
                  transfer(&MR::getTiffDataFirstC,     &MS::setTiffDataFirstC,     i, q);
                  transfer(&MR::getTiffDataIFD,        &MS::setTiffDataIFD,        i, q);
                  transfer(&MR::getTiffDataPlaneCount, &MS::setTiffDataPlaneCount, i, q);
                }
            }
        }
    }

    /**
     * Convert light sources.
     *
     * @param instrumentIndex the instrument to convert.
     */
    void
    convertLightSources(index_type instrumentIndex)
    {
      index_type lightSourceCount(src.getLightSourceCount(instrumentIndex));

      for (index_type lightSource = 0; lightSource < lightSourceCount; ++lightSource)
        {
          std::string type(src.getLightSourceType(instrumentIndex, lightSource));
          if (type == "Arc")
            {
              if (transfer(&MR::getArcID, &MS::setArcID, instrumentIndex, lightSource))
                {
                  transfer(&MR::getArcLotNumber,    &MS::setArcLotNumber,    instrumentIndex, lightSource);
                  transfer(&MR::getArcManufacturer, &MS::setArcManufacturer, instrumentIndex, lightSource);
                  transfer(&MR::getArcModel,        &MS::setArcModel,        instrumentIndex, lightSource);
                  transfer(&MR::getArcPower,        &MS::setArcPower,        instrumentIndex, lightSource);
                  transfer(&MR::getArcSerialNumber, &MS::setArcSerialNumber, instrumentIndex, lightSource);
                  transfer(&MR::getArcType,         &MS::setArcType,         instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    transfer(&MR::getArcAnnotationRef, &MS::setArcAnnotationRef, instrumentIndex, lightSource, r);
                }
            }
          else if (type == "Filament")
            {
              if (transfer(&MR::getFilamentID, &MS::setFilamentID, instrumentIndex, lightSource))
                {
                  transfer(&MR::getFilamentID,           &MS::setFilamentID,           instrumentIndex, lightSource);
                  transfer(&MR::getFilamentLotNumber,    &MS::setFilamentLotNumber,    instrumentIndex, lightSource);
                  transfer(&MR::getFilamentManufacturer, &MS::setFilamentManufacturer, instrumentIndex, lightSource);
                  transfer(&MR::getFilamentModel,        &MS::setFilamentModel,        instrumentIndex, lightSource);
                  transfer(&MR::getFilamentPower,        &MS::setFilamentPower,        instrumentIndex, lightSource);
                  transfer(&MR::getFilamentSerialNumber, &MS::setFilamentSerialNumber, instrumentIndex, lightSource);
                  transfer(&MR::getFilamentType,         &MS::setFilamentType,         instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    transfer(&MR::getFilamentAnnotationRef, &MS::setFilamentAnnotationRef, instrumentIndex, lightSource, r);
                }
            }
          else if (type == "GenericExcitationSource")
            {
              if (transfer(&MR::getGenericExcitationSourceID, &MS::setGenericExcitationSourceID, instrumentIndex, lightSource))
                {
                  transfer(&MR::getGenericExcitationSourceMap,          &MS::setGenericExcitationSourceMap,          instrumentIndex, lightSource);
                  transfer(&MR::getGenericExcitationSourceLotNumber,    &MS::setGenericExcitationSourceLotNumber,    instrumentIndex, lightSource);
                  transfer(&MR::getGenericExcitationSourceManufacturer, &MS::setGenericExcitationSourceManufacturer, instrumentIndex, lightSource);
                  transfer(&MR::getGenericExcitationSourceModel,        &MS::setGenericExcitationSourceModel,        instrumentIndex, lightSource);
                  transfer(&MR::getGenericExcitationSourcePower,        &MS::setGenericExcitationSourcePower,        instrumentIndex, lightSource);
                  transfer(&MR::getGenericExcitationSourceSerialNumber, &MS::setGenericExcitationSourceSerialNumber, instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    transfer(&MR::getGenericExcitationSourceAnnotationRef, &MS::setGenericExcitationSourceAnnotationRef, instrumentIndex, lightSource, r);
                }
            }
          else if (type == "Laser")
            {
              if (transfer(&MR::getLaserID, &MS::setLaserID, instrumentIndex, lightSource))
                {
                  transfer(&MR::getLaserLotNumber,               &MS::setLaserLotNumber,               instrumentIndex, lightSource);
                  transfer(&MR::getLaserManufacturer,            &MS::setLaserManufacturer,            instrumentIndex, lightSource);
                  transfer(&MR::getLaserLotNumber,               &MS::setLaserLotNumber,               instrumentIndex, lightSource);
                  transfer(&MR::getLaserModel,                   &MS::setLaserModel,                   instrumentIndex, lightSource);
                  transfer(&MR::getLaserPower,                   &MS::setLaserPower,                   instrumentIndex, lightSource);
                  transfer(&MR::getLaserSerialNumber,            &MS::setLaserSerialNumber,            instrumentIndex, lightSource);
                  transfer(&MR::getLaserType,                    &MS::setLaserType,                    instrumentIndex, lightSource);
                  transfer(&MR::getLaserFrequencyMultiplication, &MS::setLaserFrequencyMultiplication, instrumentIndex, lightSource);
                  transfer(&MR::getLaserLaserMedium,             &MS::setLaserLaserMedium,             instrumentIndex, lightSource);
                  transfer(&MR::getLaserPockelCell,              &MS::setLaserPockelCell,              instrumentIndex, lightSource);
                  transfer(&MR::getLaserPulse,                   &MS::setLaserPulse,                   instrumentIndex, lightSource);
                  transfer(&MR::getLaserPump,                    &MS::setLaserPump,                    instrumentIndex, lightSource);
                  transfer(&MR::getLaserRepetitionRate,          &MS::setLaserRepetitionRate,          instrumentIndex, lightSource);
                  transfer(&MR::getLaserTuneable,                &MS::setLaserTuneable,                instrumentIndex, lightSource);
                  transfer(&MR::getLaserWavelength,              &MS::setLaserWavelength,              instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    transfer(&MR::getLaserAnnotationRef, &MS::setLaserAnnotationRef, instrumentIndex, lightSource, r);
                }
            }
          else if (type == "LightEmittingDiode")
            {
              if (transfer(&MR::getLightEmittingDiodeID, &MS::setLightEmittingDiodeID, instrumentIndex, lightSource))
                {
                  transfer(&MR::getLightEmittingDiodeLotNumber,    &MS::setLightEmittingDiodeLotNumber,    instrumentIndex, lightSource);
                  transfer(&MR::getLightEmittingDiodeManufacturer, &MS::setLightEmittingDiodeManufacturer, instrumentIndex, lightSource);
                  transfer(&MR::getLightEmittingDiodeModel,        &MS::setLightEmittingDiodeModel,        instrumentIndex, lightSource);
                  transfer(&MR::getLightEmittingDiodePower,        &MS::setLightEmittingDiodePower,        instrumentIndex, lightSource);
                  transfer(&MR::getLightEmittingDiodeSerialNumber, &MS::setLightEmittingDiodeSerialNumber, instrumentIndex, lightSource);

                  index_type lightSourceAnnotationRefCount(src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource));
                  for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
                    transfer(&MR::getLightEmittingDiodeAnnotationRef, &MS::setLightEmittingDiodeAnnotationRef, instrumentIndex, lightSource, r);
                }
            }
        }
    }

    /// Convert instruments.
    void
    convertInstruments()
    {
      index_type instrumentCount(src.getInstrumentCount());
      for (index_type i = 0; i < instrumentCount; ++i)
        {
          if (transfer(&MR::getInstrumentID,           &MS::setInstrumentID,           i))
            {
              transfer(&MR::getMicroscopeLotNumber,    &MS::setMicroscopeLotNumber,    i);
              transfer(&MR::getMicroscopeManufacturer, &MS::setMicroscopeManufacturer, i);
              transfer(&MR::getMicroscopeModel,        &MS::setMicroscopeModel,        i);
              transfer(&MR::getMicroscopeSerialNumber, &MS::setMicroscopeSerialNumber, i);
              transfer(&MR::getMicroscopeType,         &MS::setMicroscopeType,         i);

              index_type detectorCount(src.getDetectorCount(i));
              for (index_type q = 0; q < detectorCount; ++q)
                {
                  if (transfer(&MR::getDetectorID,                &MS::setDetectorID,                i, q))
                    {
                      transfer(&MR::getDetectorAmplificationGain, &MS::setDetectorAmplificationGain, i, q);
                      transfer(&MR::getDetectorGain,              &MS::setDetectorGain,              i, q);
                      transfer(&MR::getDetectorLotNumber,         &MS::setDetectorLotNumber,         i, q);
                      transfer(&MR::getDetectorManufacturer,      &MS::setDetectorManufacturer,      i, q);
                      transfer(&MR::getDetectorModel,             &MS::setDetectorModel,             i, q);
                      transfer(&MR::getDetectorOffset,            &MS::setDetectorOffset,            i, q);
                      transfer(&MR::getDetectorSerialNumber,      &MS::setDetectorSerialNumber,      i, q);
                      transfer(&MR::getDetectorType,              &MS::setDetectorType,              i, q);
                      transfer(&MR::getDetectorVoltage,           &MS::setDetectorVoltage,           i, q);
                      transfer(&MR::getDetectorZoom,              &MS::setDetectorZoom,              i, q);

                      index_type detectorAnnotationRefCount(src.getDetectorAnnotationRefCount(i, q));
                      for (index_type r = 0; r < detectorAnnotationRefCount; ++r)
                        transfer(&MR::getDetectorAnnotationRef, &MS::setDetectorAnnotationRef, i, q, r);
                    }
                }

              index_type dichroicCount(src.getDichroicCount(i));
              for (index_type q = 0; q < dichroicCount; ++q)
                {
                  if (transfer(&MR::getDichroicID,           &MS::setDichroicID,           i, q))
                    {
                      transfer(&MR::getDichroicLotNumber,    &MS::setDichroicLotNumber,    i, q);
                      transfer(&MR::getDichroicManufacturer, &MS::setDichroicManufacturer, i, q);
                      transfer(&MR::getDichroicModel,        &MS::setDichroicModel,        i, q);
                      transfer(&MR::getDichroicSerialNumber, &MS::setDichroicSerialNumber, i, q);

                      index_type dichroicAnnotationRefCount(src.getDichroicAnnotationRefCount(i,q));
                      for (index_type r = 0; r < dichroicAnnotationRefCount; ++r)
                        transfer(&MR::getDichroicAnnotationRef, &MS::setDichroicAnnotationRef, i, q, r);
                    }
                }

              index_type filterCount(src.getFilterCount(i));
              for (index_type q = 0; q < filterCount; ++q)
                {
                  if (transfer(&MR::getFilterID,                          &MS::setFilterID,                          i, q))
                    {
                      transfer(&MR::getFilterFilterWheel,                 &MS::setFilterFilterWheel,                 i, q);
                      transfer(&MR::getFilterLotNumber,                   &MS::setFilterLotNumber,                   i, q);
                      transfer(&MR::getFilterManufacturer,                &MS::setFilterManufacturer,                i, q);
                      transfer(&MR::getFilterModel,                       &MS::setFilterModel,                       i, q);
                      transfer(&MR::getFilterSerialNumber,                &MS::setFilterSerialNumber,                i, q);
                      transfer(&MR::getFilterType,                        &MS::setFilterType,                        i, q);
                      transfer(&MR::getTransmittanceRangeCutIn,           &MS::setTransmittanceRangeCutIn,           i, q);
                      transfer(&MR::getTransmittanceRangeCutInTolerance,  &MS::setTransmittanceRangeCutInTolerance,  i, q);
                      transfer(&MR::getTransmittanceRangeCutOut,          &MS::setTransmittanceRangeCutOut,          i, q);
                      transfer(&MR::getTransmittanceRangeCutOutTolerance, &MS::setTransmittanceRangeCutOutTolerance, i, q);
                      transfer(&MR::getTransmittanceRangeTransmittance,   &MS::setTransmittanceRangeTransmittance,   i, q);

                      index_type filterAnnotationRefCount(src.getFilterAnnotationRefCount(i, q));
                      for (index_type r = 0; r < filterAnnotationRefCount; ++r)
                        transfer(&MR::getFilterAnnotationRef, &MS::setFilterAnnotationRef, i, q, r);
                    }
                }

              index_type objectiveCount(src.getObjectiveCount(i));
              for (index_type q = 0; q < objectiveCount; ++q)
                {
                  if (transfer(&MR::getObjectiveID,                      &MS::setObjectiveID,                      i, q))
                    {
                      transfer(&MR::getObjectiveCalibratedMagnification, &MS::setObjectiveCalibratedMagnification, i, q);
                      transfer(&MR::getObjectiveCorrection,              &MS::setObjectiveCorrection,              i, q);
                      transfer(&MR::getObjectiveImmersion,               &MS::setObjectiveImmersion,               i, q);
                      transfer(&MR::getObjectiveIris,                    &MS::setObjectiveIris,                    i, q);
                      transfer(&MR::getObjectiveLensNA,                  &MS::setObjectiveLensNA,                  i, q);
                      transfer(&MR::getObjectiveLotNumber,               &MS::setObjectiveLotNumber,               i, q);
                      transfer(&MR::getObjectiveManufacturer,            &MS::setObjectiveManufacturer,            i, q);
                      transfer(&MR::getObjectiveModel,                   &MS::setObjectiveModel,                   i, q);
                      transfer(&MR::getObjectiveNominalMagnification,    &MS::setObjectiveNominalMagnification,    i, q);
                      transfer(&MR::getObjectiveSerialNumber,            &MS::setObjectiveSerialNumber,            i, q);
                      transfer(&MR::getObjectiveWorkingDistance,         &MS::setObjectiveWorkingDistance,         i, q);

                      index_type objectiveAnnotationRefCount(src.getObjectiveAnnotationRefCount(i, q));
                      for (index_type r = 0; r < objectiveAnnotationRefCount; ++r)
                        transfer(&MR::getObjectiveAnnotationRef, &MS::setObjectiveAnnotationRef, i, q, r);
                    }
                }

              index_type filterSetCount(src.getFilterSetCount(i));
              for (index_type q = 0; q < filterSetCount; ++q)
                {
                  if (transfer(&MR::getFilterSetID,           &MS::setFilterSetID,           i, q))
                    {
                      transfer(&MR::getFilterSetDichroicRef,  &MS::setFilterSetDichroicRef,  i, q);
                      transfer(&MR::getFilterSetLotNumber,    &MS::setFilterSetLotNumber,    i, q);
                      transfer(&MR::getFilterSetManufacturer, &MS::setFilterSetManufacturer, i, q);
                      transfer(&MR::getFilterSetModel,        &MS::setFilterSetModel,        i, q);
                      transfer(&MR::getFilterSetSerialNumber, &MS::setFilterSetSerialNumber, i, q);

                      index_type emFilterCount(src.getFilterSetEmissionFilterRefCount(i, q));
                      for (index_type f = 0; f < emFilterCount; ++f)
                        transfer(&MR::getFilterSetEmissionFilterRef, &MS::setFilterSetEmissionFilterRef, i, q, f);

                      index_type exFilterCount(src.getFilterSetExcitationFilterRefCount(i, q));
                      for (index_type f = 0; f < exFilterCount; ++f)
                        transfer(&MR::getFilterSetExcitationFilterRef, &MS::setFilterSetExcitationFilterRef, i, q, f);
                    }
                }

              convertLightSources(i);

              index_type instrumentAnnotationRefCount(src.getInstrumentAnnotationRefCount(i));
              for (index_type r = 0; r < instrumentAnnotationRefCount; ++r)
                transfer(&MR::getInstrumentAnnotationRef, &MS::setInstrumentAnnotationRef, i, r);
            }
        }
    }

    /// Convert list annotations.
    void
    convertListAnnotations()
    {
      index_type listAnnotationCount(src.getListAnnotationCount());
      for (index_type i = 0; i < listAnnotationCount; ++i)
        {
          if (transfer(&MR::getListAnnotationID,          &MS::setListAnnotationID,          i))
            {
              transfer(&MR::getListAnnotationDescription, &MS::setListAnnotationDescription, i);
              transfer(&MR::getListAnnotationNamespace,   &MS::setListAnnotationNamespace,   i);
              transfer(&MR::getListAnnotationAnnotator,   &MS::setListAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getListAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getListAnnotationAnnotationRef, &MS::setListAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert long annotations.
    void
    convertLongAnnotations()
    {
      index_type longAnnotationCount(src.getLongAnnotationCount());
      for (index_type i = 0; i < longAnnotationCount; ++i)
        {
          if (transfer(&MR::getLongAnnotationID,          &MS::setLongAnnotationID,          i))
            {
              transfer(&MR::getLongAnnotationDescription, &MS::setLongAnnotationDescription, i);
              transfer(&MR::getLongAnnotationNamespace,   &MS::setLongAnnotationNamespace,   i);
              transfer(&MR::getLongAnnotationValue,       &MS::setLongAnnotationValue,       i);
              transfer(&MR::getLongAnnotationAnnotator,   &MS::setLongAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getLongAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getLongAnnotationAnnotationRef, &MS::setLongAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert map annotations.
    void
    convertMapAnnotations()
    {
      index_type mapAnnotationCount(src.getMapAnnotationCount());
      for (index_type i = 0; i < mapAnnotationCount; ++i)
        {
          if (transfer(&MR::getMapAnnotationID,          &MS::setMapAnnotationID,          i))
            {
              transfer(&MR::getMapAnnotationDescription, &MS::setMapAnnotationDescription, i);
              transfer(&MR::getMapAnnotationNamespace,   &MS::setMapAnnotationNamespace,   i);
              transfer(&MR::getMapAnnotationValue,       &MS::setMapAnnotationValue,       i);
              transfer(&MR::getMapAnnotationAnnotator,   &MS::setMapAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getMapAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getMapAnnotationAnnotationRef, &MS::setMapAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert plates.
    void
    convertPlates()
    {
      index_type plateCount(src.getPlateCount());
      for (index_type i = 0; i < plateCount; ++i)
        {
          if (transfer(&MR::getPlateID,                     &MS::setPlateID,                     i))
            {
              transfer(&MR::getPlateColumnNamingConvention, &MS::setPlateColumnNamingConvention, i);
              transfer(&MR::getPlateColumns,                &MS::setPlateColumns,                i);
              transfer(&MR::getPlateDescription,            &MS::setPlateDescription,            i);
              transfer(&MR::getPlateExternalIdentifier,     &MS::setPlateExternalIdentifier,     i);
              transfer(&MR::getPlateFieldIndex,             &MS::setPlateFieldIndex,             i);
              transfer(&MR::getPlateName,                   &MS::setPlateName,                   i);
              transfer(&MR::getPlateRowNamingConvention,    &MS::setPlateRowNamingConvention,    i);
              transfer(&MR::getPlateRows,                   &MS::setPlateRows,                   i);
              transfer(&MR::getPlateStatus,                 &MS::setPlateStatus,                 i);
              transfer(&MR::getPlateWellOriginX,            &MS::setPlateWellOriginX,            i);
              transfer(&MR::getPlateWellOriginY,            &MS::setPlateWellOriginY,            i);

              index_type wellCount(src.getWellCount(i));
              for (index_type q = 0; q < wellCount; ++q)
                {
                  if (transfer(&MR::getWellID,                  &MS::setWellID,                  i, q))
                    {
                      transfer(&MR::getWellColor,               &MS::setWellColor,               i, q);
                      transfer(&MR::getWellColumn,              &MS::setWellColumn,              i, q);
                      transfer(&MR::getWellExternalDescription, &MS::setWellExternalDescription, i, q);
                      transfer(&MR::getWellExternalIdentifier,  &MS::setWellExternalIdentifier,  i, q);
                      transfer(&MR::getWellReagentRef,          &MS::setWellReagentRef,          i, q);
                      transfer(&MR::getWellRow,                 &MS::setWellRow,                 i, q);
                      transfer(&MR::getWellType,                &MS::setWellType,                i, q);

                      index_type wellAnnotationRefCount(src.getWellAnnotationRefCount(i, q));
                      for (index_type a = 0; a < wellAnnotationRefCount; ++a)
                        transfer(&MR::getWellAnnotationRef, &MS::setWellAnnotationRef, i, q, a);

                      index_type wellSampleCount(src.getWellSampleCount(i, q));
                      for (index_type w = 0; w < wellSampleCount; ++w)
                        {
                          if (transfer(&MR::getWellSampleID,        &MS::setWellSampleID,        i, q, w))
                            {
                              transfer(&MR::getWellSampleIndex,     &MS::setWellSampleIndex,     i, q, w);
                              transfer(&MR::getWellSampleImageRef,  &MS::setWellSampleImageRef,  i, q, w);
                              transfer(&MR::getWellSamplePositionX, &MS::setWellSamplePositionX, i, q, w);
                              transfer(&MR::getWellSamplePositionY, &MS::setWellSamplePositionY, i, q, w);
                              transfer(&MR::getWellSampleTimepoint, &MS::setWellSampleTimepoint, i, q, w);
                            }
                        }
                    }
                }

              index_type plateAcquisitionCount(src.getPlateAcquisitionCount(i));
              for (index_type q = 0; q < plateAcquisitionCount; ++q)
                {
                  if (transfer(&MR::getPlateAcquisitionID,                &MS::setPlateAcquisitionID,                i, q))
                    {
                      transfer(&MR::getPlateAcquisitionDescription,       &MS::setPlateAcquisitionDescription,       i, q);
                      transfer(&MR::getPlateAcquisitionEndTime,           &MS::setPlateAcquisitionEndTime,           i, q);
                      transfer(&MR::getPlateAcquisitionMaximumFieldCount, &MS::setPlateAcquisitionMaximumFieldCount, i, q);
                      transfer(&MR::getPlateAcquisitionName,              &MS::setPlateAcquisitionName,              i, q);
                      transfer(&MR::getPlateAcquisitionStartTime,         &MS::setPlateAcquisitionStartTime,         i, q);

                      index_type plateAcquisitionAnnotationRefCount(src.getPlateAcquisitionAnnotationRefCount(i, q));
                      for (index_type a = 0; a < plateAcquisitionAnnotationRefCount; ++a)
                        transfer(&MR::getPlateAcquisitionAnnotationRef, &MS::setPlateAcquisitionAnnotationRef, i, q, a);

                      index_type wellSampleRefCount(src.getWellSampleRefCount(i, q));
                      for (index_type w = 0; w < wellSampleRefCount; ++w)
                        transfer(&MR::getPlateAcquisitionWellSampleRef, &MS::setPlateAcquisitionWellSampleRef, i, q, w);
                    }
                }

              index_type plateAnnotationRefCount(src.getPlateAnnotationRefCount(i));
              for (index_type q = 0; q < plateAnnotationRefCount; ++q)
                transfer(&MR::getPlateAnnotationRef, &MS::setPlateAnnotationRef, i, q);
            }
        }
    }

    /// Convert projects.
    void
    convertProjects()
    {
      index_type projectCount(src.getProjectCount());
      for (index_type i = 0; i < projectCount; ++i)
        {
          if (transfer(&MR::getProjectID,                   &MS::setProjectID,                   i))
            {
              transfer(&MR::getProjectDescription,          &MS::setProjectDescription,          i);
              transfer(&MR::getProjectExperimenterGroupRef, &MS::setProjectExperimenterGroupRef, i);
              transfer(&MR::getProjectExperimenterRef,      &MS::setProjectExperimenterRef,      i);
              transfer(&MR::getProjectName,                 &MS::setProjectName,                 i);

              index_type annotationRefCount(src.getProjectAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getProjectAnnotationRef, &MS::setProjectAnnotationRef, i, q);

              index_type datasetRefCount(src.getDatasetRefCount(i));
              for (index_type q = 0; q < datasetRefCount; ++q)
                transfer(&MR::getProjectDatasetRef, &MS::setProjectDatasetRef, i, q);
            }
        }
    }

    /// Convert regions of interest.
    void
    convertROIs()
    {
      index_type roiCount(src.getROICount());
      for (index_type i = 0; i < roiCount; ++i)
        {
          if (transfer(&MR::getROIID,          &MS::setROIID,          i))
            {
              transfer(&MR::getROIName,        &MS::setROIName,        i);
              transfer(&MR::getROIDescription, &MS::setROIDescription, i);
              transfer(&MR::getROINamespace,   &MS::setROINamespace,   i);

              index_type shapeCount(src.getShapeCount(i));
              for (index_type q = 0; q < shapeCount; ++q)
                {
                  std::string type = src.getShapeType(i, q);

                  if (type == "Ellipse")
                    {
                      if (transfer(&MR::getEllipseID,              &MS::setEllipseID,              i, q))
                        {
                          transfer(&MR::getEllipseFillColor,       &MS::setEllipseFillColor,       i, q);
                          transfer(&MR::getEllipseFillRule,        &MS::setEllipseFillRule,        i, q);
                          transfer(&MR::getEllipseFontFamily,      &MS::setEllipseFontFamily,      i, q);
                          transfer(&MR::getEllipseFontSize,        &MS::setEllipseFontSize,        i, q);
                          transfer(&MR::getEllipseFontStyle,       &MS::setEllipseFontStyle,       i, q);
                          transfer(&MR::getEllipseLineCap,         &MS::setEllipseLineCap,         i, q);
                          transfer(&MR::getEllipseLocked,          &MS::setEllipseLocked,          i, q);
                          transfer(&MR::getEllipseStrokeColor,     &MS::setEllipseStrokeColor,     i, q);
                          transfer(&MR::getEllipseStrokeDashArray, &MS::setEllipseStrokeDashArray, i, q);
                          transfer(&MR::getEllipseStrokeWidth,     &MS::setEllipseStrokeWidth,     i, q);
                          transfer(&MR::getEllipseText,            &MS::setEllipseText,            i, q);
                          transfer(&MR::getEllipseTheZ,            &MS::setEllipseTheZ,            i, q);
                          transfer(&MR::getEllipseTheT,            &MS::setEllipseTheT,            i, q);
                          transfer(&MR::getEllipseTheC,            &MS::setEllipseTheC,            i, q);
                          transfer(&MR::getEllipseTransform,       &MS::setEllipseTransform,       i, q);
                          transfer(&MR::getEllipseVisible,         &MS::setEllipseVisible,         i, q);
                          transfer(&MR::getEllipseRadiusX,         &MS::setEllipseRadiusX,         i, q);
                          transfer(&MR::getEllipseRadiusY,         &MS::setEllipseRadiusY,         i, q);
                          transfer(&MR::getEllipseX,               &MS::setEllipseX,               i, q);
                          transfer(&MR::getEllipseY,               &MS::setEllipseY,               i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getEllipseAnnotationRef, &MS::setEllipseAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Label")
                    {
                      if (transfer(&MR::getLabelID,              &MS::setLabelID,              i, q))
                        {
                          transfer(&MR::getLabelFillColor,       &MS::setLabelFillColor,       i, q);
                          transfer(&MR::getLabelFillRule,        &MS::setLabelFillRule,        i, q);
                          transfer(&MR::getLabelFontFamily,      &MS::setLabelFontFamily,      i, q);
                          transfer(&MR::getLabelFontSize,        &MS::setLabelFontSize,        i, q);
                          transfer(&MR::getLabelFontStyle,       &MS::setLabelFontStyle,       i, q);
                          transfer(&MR::getLabelLineCap,         &MS::setLabelLineCap,         i, q);
                          transfer(&MR::getLabelLocked,          &MS::setLabelLocked,          i, q);
                          transfer(&MR::getLabelStrokeColor,     &MS::setLabelStrokeColor,     i, q);
                          transfer(&MR::getLabelStrokeDashArray, &MS::setLabelStrokeDashArray, i, q);
                          transfer(&MR::getLabelStrokeWidth,     &MS::setLabelStrokeWidth,     i, q);
                          transfer(&MR::getLabelText,            &MS::setLabelText,            i, q);
                          transfer(&MR::getLabelTheZ,            &MS::setLabelTheZ,            i, q);
                          transfer(&MR::getLabelTheT,            &MS::setLabelTheT,            i, q);
                          transfer(&MR::getLabelTheC,            &MS::setLabelTheC,            i, q);
                          transfer(&MR::getLabelTransform,       &MS::setLabelTransform,       i, q);
                          transfer(&MR::getLabelVisible,         &MS::setLabelVisible,         i, q);
                          transfer(&MR::getLabelX,               &MS::setLabelX,               i, q);
                          transfer(&MR::getLabelY,               &MS::setLabelY,               i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getLabelAnnotationRef, &MS::setLabelAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Line")
                    {
                      if (transfer(&MR::getLineID,              &MS::setLineID,              i, q))
                        {
                          transfer(&MR::getLineFillColor,       &MS::setLineFillColor,       i, q);
                          transfer(&MR::getLineFillRule,        &MS::setLineFillRule,        i, q);
                          transfer(&MR::getLineFontFamily,      &MS::setLineFontFamily,      i, q);
                          transfer(&MR::getLineFontSize,        &MS::setLineFontSize,        i, q);
                          transfer(&MR::getLineFontStyle,       &MS::setLineFontStyle,       i, q);
                          transfer(&MR::getLineLineCap,         &MS::setLineLineCap,         i, q);
                          transfer(&MR::getLineLocked,          &MS::setLineLocked,          i, q);
                          transfer(&MR::getLineStrokeColor,     &MS::setLineStrokeColor,     i, q);
                          transfer(&MR::getLineStrokeDashArray, &MS::setLineStrokeDashArray, i, q);
                          transfer(&MR::getLineStrokeWidth,     &MS::setLineStrokeWidth,     i, q);
                          transfer(&MR::getLineText,            &MS::setLineText,            i, q);
                          transfer(&MR::getLineTheZ,            &MS::setLineTheZ,            i, q);
                          transfer(&MR::getLineTheT,            &MS::setLineTheT,            i, q);
                          transfer(&MR::getLineTheC,            &MS::setLineTheC,            i, q);
                          transfer(&MR::getLineTransform,       &MS::setLineTransform,       i, q);
                          transfer(&MR::getLineVisible,         &MS::setLineVisible,         i, q);
                          transfer(&MR::getLineMarkerEnd,       &MS::setLineMarkerEnd,       i, q);
                          transfer(&MR::getLineMarkerStart,     &MS::setLineMarkerStart,     i, q);
                          transfer(&MR::getLineX1,              &MS::setLineX1,              i, q);
                          transfer(&MR::getLineX2,              &MS::setLineX2,              i, q);
                          transfer(&MR::getLineY1,              &MS::setLineY1,              i, q);
                          transfer(&MR::getLineY2,              &MS::setLineY2,              i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getLineAnnotationRef, &MS::setLineAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Mask")
                    {
                      if (transfer(&MR::getMaskID,              &MS::setMaskID,              i, q))
                        {
                          transfer(&MR::getMaskFillColor,       &MS::setMaskFillColor,       i, q);
                          transfer(&MR::getMaskFillRule,        &MS::setMaskFillRule,        i, q);
                          transfer(&MR::getMaskFontFamily,      &MS::setMaskFontFamily,      i, q);
                          transfer(&MR::getMaskFontSize,        &MS::setMaskFontSize,        i, q);
                          transfer(&MR::getMaskFontStyle,       &MS::setMaskFontStyle,       i, q);
                          transfer(&MR::getMaskLineCap,         &MS::setMaskLineCap,         i, q);
                          transfer(&MR::getMaskLocked,          &MS::setMaskLocked,          i, q);
                          transfer(&MR::getMaskStrokeColor,     &MS::setMaskStrokeColor,     i, q);
                          transfer(&MR::getMaskStrokeDashArray, &MS::setMaskStrokeDashArray, i, q);
                          transfer(&MR::getMaskStrokeWidth,     &MS::setMaskStrokeWidth,     i, q);
                          transfer(&MR::getMaskText,            &MS::setMaskText,            i, q);
                          transfer(&MR::getMaskTheZ,            &MS::setMaskTheZ,            i, q);
                          transfer(&MR::getMaskTheT,            &MS::setMaskTheT,            i, q);
                          transfer(&MR::getMaskTheC,            &MS::setMaskTheC,            i, q);
                          transfer(&MR::getMaskTransform,       &MS::setMaskTransform,       i, q);
                          transfer(&MR::getMaskVisible,         &MS::setMaskVisible,         i, q);
                          transfer(&MR::getMaskHeight,          &MS::setMaskHeight,          i, q);
                          transfer(&MR::getMaskWidth,           &MS::setMaskWidth,           i, q);
                          transfer(&MR::getMaskX,               &MS::setMaskX,               i, q);
                          transfer(&MR::getMaskY,               &MS::setMaskY,               i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getMaskAnnotationRef, &MS::setMaskAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Point")
                    {
                      if (transfer(&MR::getPointID,              &MS::setPointID,              i, q))
                        {
                          transfer(&MR::getPointFillColor,       &MS::setPointFillColor,       i, q);
                          transfer(&MR::getPointFillRule,        &MS::setPointFillRule,        i, q);
                          transfer(&MR::getPointFontFamily,      &MS::setPointFontFamily,      i, q);
                          transfer(&MR::getPointFontSize,        &MS::setPointFontSize,        i, q);
                          transfer(&MR::getPointFontStyle,       &MS::setPointFontStyle,       i, q);
                          transfer(&MR::getPointLineCap,         &MS::setPointLineCap,         i, q);
                          transfer(&MR::getPointLocked,          &MS::setPointLocked,          i, q);
                          transfer(&MR::getPointStrokeColor,     &MS::setPointStrokeColor,     i, q);
                          transfer(&MR::getPointStrokeDashArray, &MS::setPointStrokeDashArray, i, q);
                          transfer(&MR::getPointStrokeWidth,     &MS::setPointStrokeWidth,     i, q);
                          transfer(&MR::getPointText,            &MS::setPointText,            i, q);
                          transfer(&MR::getPointTheZ,            &MS::setPointTheZ,            i, q);
                          transfer(&MR::getPointTheT,            &MS::setPointTheT,            i, q);
                          transfer(&MR::getPointTheC,            &MS::setPointTheC,            i, q);
                          transfer(&MR::getPointTransform,       &MS::setPointTransform,       i, q);
                          transfer(&MR::getPointVisible,         &MS::setPointVisible,         i, q);
                          transfer(&MR::getPointX,               &MS::setPointX,               i, q);
                          transfer(&MR::getPointY,               &MS::setPointY,               i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getPointAnnotationRef, &MS::setPointAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Polygon")
                    {
                      if (transfer(&MR::getPolygonID,              &MS::setPolygonID,              i, q))
                        {
                          transfer(&MR::getPolygonFillColor,       &MS::setPolygonFillColor,       i, q);
                          transfer(&MR::getPolygonFillRule,        &MS::setPolygonFillRule,        i, q);
                          transfer(&MR::getPolygonFontFamily,      &MS::setPolygonFontFamily,      i, q);
                          transfer(&MR::getPolygonFontSize,        &MS::setPolygonFontSize,        i, q);
                          transfer(&MR::getPolygonFontStyle,       &MS::setPolygonFontStyle,       i, q);
                          transfer(&MR::getPolygonLineCap,         &MS::setPolygonLineCap,         i, q);
                          transfer(&MR::getPolygonLocked,          &MS::setPolygonLocked,          i, q);
                          transfer(&MR::getPolygonStrokeColor,     &MS::setPolygonStrokeColor,     i, q);
                          transfer(&MR::getPolygonStrokeDashArray, &MS::setPolygonStrokeDashArray, i, q);
                          transfer(&MR::getPolygonStrokeWidth,     &MS::setPolygonStrokeWidth,     i, q);
                          transfer(&MR::getPolygonText,            &MS::setPolygonText,            i, q);
                          transfer(&MR::getPolygonTheZ,            &MS::setPolygonTheZ,            i, q);
                          transfer(&MR::getPolygonTheT,            &MS::setPolygonTheT,            i, q);
                          transfer(&MR::getPolygonTheC,            &MS::setPolygonTheC,            i, q);
                          transfer(&MR::getPolygonTransform,       &MS::setPolygonTransform,       i, q);
                          transfer(&MR::getPolygonVisible,         &MS::setPolygonVisible,         i, q);
                          transfer(&MR::getPolygonPoints,          &MS::setPolygonPoints,          i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getPolygonAnnotationRef, &MS::setPolygonAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Polyline")
                    {
                      if (transfer(&MR::getPolylineID,              &MS::setPolylineID,              i, q))
                        {
                          transfer(&MR::getPolylineFillColor,       &MS::setPolylineFillColor,       i, q);
                          transfer(&MR::getPolylineFillRule,        &MS::setPolylineFillRule,        i, q);
                          transfer(&MR::getPolylineFontFamily,      &MS::setPolylineFontFamily,      i, q);
                          transfer(&MR::getPolylineFontSize,        &MS::setPolylineFontSize,        i, q);
                          transfer(&MR::getPolylineFontStyle,       &MS::setPolylineFontStyle,       i, q);
                          transfer(&MR::getPolylineLineCap,         &MS::setPolylineLineCap,         i, q);
                          transfer(&MR::getPolylineLocked,          &MS::setPolylineLocked,          i, q);
                          transfer(&MR::getPolylineStrokeColor,     &MS::setPolylineStrokeColor,     i, q);
                          transfer(&MR::getPolylineStrokeDashArray, &MS::setPolylineStrokeDashArray, i, q);
                          transfer(&MR::getPolylineStrokeWidth,     &MS::setPolylineStrokeWidth,     i, q);
                          transfer(&MR::getPolylineText,            &MS::setPolylineText,            i, q);
                          transfer(&MR::getPolylineTheZ,            &MS::setPolylineTheZ,            i, q);
                          transfer(&MR::getPolylineTheT,            &MS::setPolylineTheT,            i, q);
                          transfer(&MR::getPolylineTheC,            &MS::setPolylineTheC,            i, q);
                          transfer(&MR::getPolylineTransform,       &MS::setPolylineTransform,       i, q);
                          transfer(&MR::getPolylineVisible,         &MS::setPolylineVisible,         i, q);
                          transfer(&MR::getPolylineMarkerEnd,       &MS::setPolylineMarkerEnd,       i, q);
                          transfer(&MR::getPolylineMarkerStart,     &MS::setPolylineMarkerStart,     i, q);
                          transfer(&MR::getPolylinePoints,          &MS::setPolylinePoints,          i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getPolylineAnnotationRef, &MS::setPolylineAnnotationRef, i, q, r);
                        }
                    }
                  else if (type == "Rectangle")
                    {
                      if (transfer(&MR::getRectangleID,              &MS::setRectangleID,              i, q))
                        {
                          transfer(&MR::getRectangleFillColor,       &MS::setRectangleFillColor,       i, q);
                          transfer(&MR::getRectangleFillRule,        &MS::setRectangleFillRule,        i, q);
                          transfer(&MR::getRectangleFontFamily,      &MS::setRectangleFontFamily,      i, q);
                          transfer(&MR::getRectangleFontSize,        &MS::setRectangleFontSize,        i, q);
                          transfer(&MR::getRectangleFontStyle,       &MS::setRectangleFontStyle,       i, q);
                          transfer(&MR::getRectangleLineCap,         &MS::setRectangleLineCap,         i, q);
                          transfer(&MR::getRectangleLocked,          &MS::setRectangleLocked,          i, q);
                          transfer(&MR::getRectangleStrokeColor,     &MS::setRectangleStrokeColor,     i, q);
                          transfer(&MR::getRectangleStrokeDashArray, &MS::setRectangleStrokeDashArray, i, q);
                          transfer(&MR::getRectangleStrokeWidth,     &MS::setRectangleStrokeWidth,     i, q);
                          transfer(&MR::getRectangleText,            &MS::setRectangleText,            i, q);
                          transfer(&MR::getRectangleTheZ,            &MS::setRectangleTheZ,            i, q);
                          transfer(&MR::getRectangleTheT,            &MS::setRectangleTheT,            i, q);
                          transfer(&MR::getRectangleTheC,            &MS::setRectangleTheC,            i, q);
                          transfer(&MR::getRectangleTransform,       &MS::setRectangleTransform,       i, q);
                          transfer(&MR::getRectangleVisible,         &MS::setRectangleVisible,         i, q);
                          transfer(&MR::getRectangleHeight,          &MS::setRectangleHeight,          i, q);
                          transfer(&MR::getRectangleWidth,           &MS::setRectangleWidth,           i, q);
                          transfer(&MR::getRectangleX,               &MS::setRectangleX,               i, q);
                          transfer(&MR::getRectangleY,               &MS::setRectangleY,               i, q);

                          index_type shapeAnnotationRefCount(src.getShapeAnnotationRefCount(i, q));
                          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
                            transfer(&MR::getRectangleAnnotationRef, &MS::setRectangleAnnotationRef, i, q, r);
                        }
                    }
                }

              index_type annotationRefCount(src.getROIAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getROIAnnotationRef, &MS::setROIAnnotationRef, i, q);
            }
        }
    }

    /// Convert screens.
    void
    convertScreens()
    {
      index_type screenCount(src.getScreenCount());
      for (index_type i = 0; i < screenCount; ++i)
        {
          if (transfer(&MR::getScreenID,                    &MS::setScreenID,                    i))
            {
              transfer(&MR::getScreenName,                  &MS::setScreenName,                  i);
              transfer(&MR::getScreenDescription,           &MS::setScreenDescription,           i);
              transfer(&MR::getScreenProtocolDescription,   &MS::setScreenProtocolDescription,   i);
              transfer(&MR::getScreenProtocolIdentifier,    &MS::setScreenProtocolIdentifier,    i);
              transfer(&MR::getScreenReagentSetDescription, &MS::setScreenReagentSetDescription, i);
              transfer(&MR::getScreenReagentSetIdentifier,  &MS::setScreenReagentSetIdentifier,  i);
              transfer(&MR::getScreenType,                  &MS::setScreenType,                  i);

              index_type plateRefCount(src.getPlateRefCount(i));
              for (index_type q = 0; q < plateRefCount; ++q)
                transfer(&MR::getScreenPlateRef, &MS::setScreenPlateRef, i, q);

              index_type annotationRefCount(src.getScreenAnnotationRefCount(i));
              for (index_type q = 0; q < annotationRefCount; ++q)
                transfer(&MR::getScreenAnnotationRef, &MS::setScreenAnnotationRef, i, q);

              index_type reagentCount(src.getReagentCount(i));
              for (index_type q = 0; q < reagentCount; ++q)
                {
                  if (transfer(&MR::getReagentID,                &MS::setReagentID,                i, q))
                    {
                      transfer(&MR::getReagentDescription,       &MS::setReagentDescription,       i, q);
                      transfer(&MR::getReagentName,              &MS::setReagentName,              i, q);
                      transfer(&MR::getReagentReagentIdentifier, &MS::setReagentReagentIdentifier, i, q);

                      index_type reagentAnnotationRefCount(src.getReagentAnnotationRefCount(i, q));
                      for (index_type r = 0; r < reagentAnnotationRefCount; ++r)
                        transfer(&MR::getReagentAnnotationRef, &MS::setReagentAnnotationRef, i, q, r);
                    }
                }
            }
        }
    }

    /// Convert tag annotations.
    void
    convertTagAnnotations()
    {
      index_type tagAnnotationCount(src.getTagAnnotationCount());
      for (index_type i = 0; i < tagAnnotationCount; ++i)
        {
          if (transfer(&MR::getTagAnnotationID,          &MS::setTagAnnotationID,          i))
            {
              transfer(&MR::getTagAnnotationDescription, &MS::setTagAnnotationDescription, i);
              transfer(&MR::getTagAnnotationNamespace,   &MS::setTagAnnotationNamespace,   i);
              transfer(&MR::getTagAnnotationValue,       &MS::setTagAnnotationValue,       i);
              transfer(&MR::getTagAnnotationAnnotator,   &MS::setTagAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getTagAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getTagAnnotationAnnotationRef, &MS::setTagAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert term annotations.
    void
    convertTermAnnotations()
    {
      index_type termAnnotationCount(src.getTermAnnotationCount());
      for (index_type i = 0; i < termAnnotationCount; ++i)
        {
          if (transfer(&MR::getTermAnnotationID,          &MS::setTermAnnotationID,          i))
            {
              transfer(&MR::getTermAnnotationDescription, &MS::setTermAnnotationDescription, i);
              transfer(&MR::getTermAnnotationNamespace,   &MS::setTermAnnotationNamespace,   i);
              transfer(&MR::getTermAnnotationValue,       &MS::setTermAnnotationValue,       i);
              transfer(&MR::getTermAnnotationAnnotator,   &MS::setTermAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getTermAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getTermAnnotationAnnotationRef, &MS::setTermAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert timestamp annotations.
    void
    convertTimestampAnnotations()
    {
      index_type timestampAnnotationCount(src.getTimestampAnnotationCount());
      for (index_type i = 0; i < timestampAnnotationCount; ++i)
        {
          if (transfer(&MR::getTimestampAnnotationID,          &MS::setTimestampAnnotationID,          i))
            {
              transfer(&MR::getTimestampAnnotationDescription, &MS::setTimestampAnnotationDescription, i);
              transfer(&MR::getTimestampAnnotationNamespace,   &MS::setTimestampAnnotationNamespace,   i);
              transfer(&MR::getTimestampAnnotationValue,       &MS::setTimestampAnnotationValue,       i);
              transfer(&MR::getTimestampAnnotationAnnotator,   &MS::setTimestampAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getTimestampAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getTimestampAnnotationAnnotationRef, &MS::setTimestampAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert XML annotations.
    void
    convertXMLAnnotations()
    {
      index_type xmlAnnotationCount(src.getXMLAnnotationCount());
      for (index_type i = 0; i < xmlAnnotationCount; ++i)
        {
          if (transfer(&MR::getXMLAnnotationID,          &MS::setXMLAnnotationID,          i))
            {
              transfer(&MR::getXMLAnnotationDescription, &MS::setXMLAnnotationDescription, i);
              transfer(&MR::getXMLAnnotationNamespace,   &MS::setXMLAnnotationNamespace,   i);
              transfer(&MR::getXMLAnnotationValue,       &MS::setXMLAnnotationValue,       i);
              transfer(&MR::getXMLAnnotationAnnotator,   &MS::setXMLAnnotationAnnotator,   i);

              index_type annotationRefCount(src.getXMLAnnotationAnnotationCount(i));
              for (index_type a = 0; a < annotationRefCount; ++a)
                transfer(&MR::getXMLAnnotationAnnotationRef,&MS::setXMLAnnotationAnnotationRef, i, a);
            }
        }
    }

    /// Convert root attributes.
    void
    convertRootAttributes()
    {
      transfer(&MR::getUUID,                   &MS::setUUID);
      transfer(&MR::getRightsRightsHeld,       &MS::setRightsRightsHeld);
      transfer(&MR::getRightsRightsHolder,     &MS::setRightsRightsHolder);
      transfer(&MR::getBinaryOnlyMetadataFile, &MS::setBinaryOnlyMetadataFile);
      transfer(&MR::getBinaryOnlyUUID,         &MS::setBinaryOnlyUUID);
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
        MetadataConverter convert(src, dest);
        convert();
      }

    }
  }
}
