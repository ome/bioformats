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

  void
  convertBooleanAnnotations(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type booleanAnnotationCount = src.getBooleanAnnotationCount();
    for (index_type i = 0; i < booleanAnnotationCount; ++i)
      {
        std::string id = src.getBooleanAnnotationID(i);
        dest.setBooleanAnnotationID(id, i);

        std::string description = src.getBooleanAnnotationDescription(i);
        dest.setBooleanAnnotationDescription(description, i);

        std::string ns = src.getBooleanAnnotationNamespace(i);
        dest.setBooleanAnnotationNamespace(ns, i);

        bool value = src.getBooleanAnnotationValue(i);
        dest.setBooleanAnnotationValue(value, i);

        std::string annotator = src.getBooleanAnnotationAnnotator(i);
        dest.setBooleanAnnotationAnnotator(annotator, i);

        index_type annotationRefCount = 0;
        annotationRefCount = src.getBooleanAnnotationAnnotationCount(i);
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            std::string id = src.getBooleanAnnotationAnnotationRef(i, a);
            dest.setBooleanAnnotationAnnotationRef(id, i, a);
          }
      }
  }

  void
  convertCommentAnnotations(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type commentAnnotationCount = 0;
    commentAnnotationCount = src.getCommentAnnotationCount();
    for (index_type i = 0; i < commentAnnotationCount; ++i)
      {
        std::string id = src.getCommentAnnotationID(i);
        dest.setCommentAnnotationID(id, i);

        std::string description = src.getCommentAnnotationDescription(i);
        dest.setCommentAnnotationDescription(description, i);

        std::string ns = src.getCommentAnnotationNamespace(i);
        dest.setCommentAnnotationNamespace(ns, i);

        std::string value = src.getCommentAnnotationValue(i);
        dest.setCommentAnnotationValue(value, i);

        std::string annotator = src.getCommentAnnotationAnnotator(i);
        dest.setCommentAnnotationAnnotator(annotator, i);

        index_type annotationRefCount = 0;
        annotationRefCount = src.getCommentAnnotationAnnotationCount(i);
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
            std::string id = src.getCommentAnnotationAnnotationRef(i, a);
            dest.setCommentAnnotationAnnotationRef(id, i, a);
          }
      }
  }

  void
  convertDatasets(const MetadataRetrieve& src,
                  MetadataStore&          dest)
  {
    index_type datasets = 0;
    datasets = src.getDatasetCount();
    for (index_type i = 0; i < datasets; ++i)
      {
        std::string id = src.getDatasetID(i);
        dest.setDatasetID(id, i);

        std::string description = src.getDatasetDescription(i);
        dest.setDatasetDescription(description, i);

        std::string experimenterGroupRef = src.getDatasetExperimenterGroupRef(i);
        dest.setDatasetExperimenterGroupRef(experimenterGroupRef, i);

        std::string experimenterRef = src.getDatasetExperimenterRef(i);
        dest.setDatasetExperimenterRef(experimenterRef, i);

        std::string name = src.getDatasetName(i);
        dest.setDatasetName(name, i);

        index_type imageRefCount = src.getDatasetImageRefCount(i);
        for (index_type q = 0; q < imageRefCount; ++q)
          {
            std::string imageRef = src.getDatasetImageRef(i, q);
            dest.setDatasetImageRef(imageRef, i, q);
          }

        index_type annotationRefCount = src.getDatasetAnnotationRefCount(i);
        for (index_type q = 0; q < annotationRefCount; ++q)
          {
            std::string annotationRef = src.getDatasetAnnotationRef(i, q);
            dest.setDatasetAnnotationRef(annotationRef, i, q);
          }
      }
  }

  void
  convertDoubleAnnotations(const MetadataRetrieve& src,
                           MetadataStore&          dest)
  {
    index_type doubleAnnotationCount = 0;
    doubleAnnotationCount = src.getDoubleAnnotationCount();
    for (index_type i = 0; i < doubleAnnotationCount; ++i)
      {
        std::string id = src.getDoubleAnnotationID(i);
        dest.setDoubleAnnotationID(id, i);

        std::string description = src.getDoubleAnnotationDescription(i);
        dest.setDoubleAnnotationDescription(description, i);

        std::string ns = src.getDoubleAnnotationNamespace(i);
        dest.setDoubleAnnotationNamespace(ns, i);

        double value = src.getDoubleAnnotationValue(i);
        dest.setDoubleAnnotationValue(value, i);

        std::string annotator = src.getDoubleAnnotationAnnotator(i);
        dest.setDoubleAnnotationAnnotator(annotator, i);

        index_type annotationRefCount = 0;
        annotationRefCount = src.getDoubleAnnotationAnnotationCount(i);
        for (index_type a = 0; a < annotationRefCount; ++a)
          {
          std::string id = src.getDoubleAnnotationAnnotationRef(i, a);
          dest.setDoubleAnnotationAnnotationRef(id, i, a);
          }
      }
  }

  void
  convertExperiments(const MetadataRetrieve& src,
                     MetadataStore&          dest)
  {
    index_type experimentCount = 0;
    experimentCount = src.getExperimentCount();
    for (index_type i = 0; i < experimentCount; ++i)
      {
        std::string id = src.getExperimentID(i);
        dest.setExperimentID(id, i);

        std::string description = src.getExperimentDescription(i);
        dest.setExperimentDescription(description, i);

        std::string experimenterRef = src.getExperimentExperimenterRef(i);
        dest.setExperimentExperimenterRef(experimenterRef, i);

        ExperimentType type = src.getExperimentType(i);
        dest.setExperimentType(type, i);

        index_type microbeamCount = 0;
        microbeamCount = src.getMicrobeamManipulationCount(i);
        for (index_type q = 0; q < microbeamCount; ++q)
          {
            std::string microbeamID = src.getMicrobeamManipulationID(i, q);
            dest.setMicrobeamManipulationID(microbeamID, i, q);

            std::string microbeamDescription = src.getMicrobeamManipulationDescription(i, q);
            dest.setMicrobeamManipulationDescription(microbeamDescription, i, q);

            std::string microbeamExperimenterRef = src.getMicrobeamManipulationExperimenterRef(i, q);
            dest.setMicrobeamManipulationExperimenterRef(microbeamExperimenterRef, i, q);

            MicrobeamManipulationType microbeamType = src.getMicrobeamManipulationType(i, q);
            dest.setMicrobeamManipulationType(microbeamType, i, q);

            index_type lightSourceCount = 0;
            lightSourceCount = src.getMicrobeamManipulationLightSourceSettingsCount(i, q);
            for (index_type p = 0; p < lightSourceCount; ++p)
              {
                std::string lightSourceID = src.getMicrobeamManipulationLightSourceSettingsID(i, q, p);
                if (!lightSourceID.empty())
                  {
                    dest.setMicrobeamManipulationLightSourceSettingsID(lightSourceID, i, q, p);

                    PercentFraction attenuation = src.getMicrobeamManipulationLightSourceSettingsAttenuation(i, q, p);
                    dest.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, i, q, p);
                  }

                PositiveFloat wavelength = src.getMicrobeamManipulationLightSourceSettingsWavelength(i, q, p);
                dest.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, i, q, p);

                index_type roiRefCount = 0;
                roiRefCount = src.getMicrobeamManipulationROIRefCount(i, q);
                for (index_type p = 0; p < roiRefCount; ++p)
                  {
                    std::string roiRef = src.getMicrobeamManipulationROIRef(i, q, p);
                    dest.setMicrobeamManipulationROIRef(roiRef, i, q, p);
                  }
              }
          }
      }
  }

  void
  convertExperimenters(const MetadataRetrieve& src,
                       MetadataStore&          dest)
  {
    index_type experimenterCount = 0;
    experimenterCount = src.getExperimenterCount();
    for (index_type i = 0; i < experimenterCount; ++i)
      {
        std::string id = src.getExperimenterID(i);
        dest.setExperimenterID(id, i);

        std::string email = src.getExperimenterEmail(i);
        dest.setExperimenterEmail(email, i);

        std::string firstName = src.getExperimenterFirstName(i);
        dest.setExperimenterFirstName(firstName, i);

        std::string institution = src.getExperimenterInstitution(i);
        dest.setExperimenterInstitution(institution, i);

        std::string lastName = src.getExperimenterLastName(i);
        dest.setExperimenterLastName(lastName, i);

        std::string middleName = src.getExperimenterMiddleName(i);
        dest.setExperimenterMiddleName(middleName, i);

        std::string userName = src.getExperimenterUserName(i);
        dest.setExperimenterUserName(userName, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getExperimenterAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getExperimenterAnnotationRef(i, q);
          dest.setExperimenterAnnotationRef(annotationRef, i, q);
      }
    }
  }

  void
  convertExperimenterGroups(const MetadataRetrieve& src,
                            MetadataStore&          dest)
  {
    index_type experimenterGroupCount = 0;
    experimenterGroupCount = src.getExperimenterGroupCount();
    for (index_type i = 0; i < experimenterGroupCount; ++i)
      {
        std::string id = src.getExperimenterGroupID(i);
        dest.setExperimenterGroupID(id, i);

        std::string description = src.getExperimenterGroupDescription(i);
        dest.setExperimenterGroupDescription(description, i);

        std::string name = src.getExperimenterGroupName(i);
        dest.setExperimenterGroupName(name, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getExperimenterGroupAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getExperimenterGroupAnnotationRef(i, q);
          dest.setExperimenterGroupAnnotationRef(annotationRef, i, q);
      }

      index_type experimenterRefCount = 0;
        experimenterRefCount = src.getExperimenterGroupExperimenterRefCount(i);
      for (index_type q = 0; q < experimenterRefCount; ++q)
        {
          std::string experimenterRef = src.getExperimenterGroupExperimenterRef(i, q);
          dest.setExperimenterGroupExperimenterRef(experimenterRef, i, q);
      }

      index_type leaderCount = 0;
        leaderCount = src.getLeaderCount(i);
      for (index_type q = 0; q < leaderCount; ++q)
        {
          std::string leader = src.getExperimenterGroupLeader(i, q);
          dest.setExperimenterGroupLeader(leader, i, q);
      }
    }
  }

  void
  convertFileAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type fileAnnotationCount = 0;
      fileAnnotationCount = src.getFileAnnotationCount();
    for (index_type i = 0; i < fileAnnotationCount; ++i)
      {
        std::string id = src.getFileAnnotationID(i);
        dest.setFileAnnotationID(id, i);

        std::string description = src.getFileAnnotationDescription(i);
        dest.setFileAnnotationDescription(description, i);

        std::string ns = src.getFileAnnotationNamespace(i);
        dest.setFileAnnotationNamespace(ns, i);

        std::string annotator = src.getFileAnnotationAnnotator(i);
        dest.setFileAnnotationAnnotator(annotator, i);

        std::string fileName = src.getBinaryFileFileName(i);
        dest.setBinaryFileFileName(fileName, i);

        std::string mimeType = src.getBinaryFileMIMEType(i);
        dest.setBinaryFileMIMEType(mimeType, i);

        NonNegativeLong fileSize = src.getBinaryFileSize(i);
        dest.setBinaryFileSize(fileSize, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getFileAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getFileAnnotationAnnotationRef(i, a);
          dest.setFileAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertImages(const MetadataRetrieve& src,
                MetadataStore&          dest)
  {
    index_type imageCount = 0;
    imageCount = src.getImageCount();
    for (index_type i = 0; i < imageCount; ++i)
      {
        std::string id = src.getImageID(i);
        dest.setImageID(id, i);

        Timestamp date = src.getImageAcquisitionDate(i);
        dest.setImageAcquisitionDate(date, i);

        std::string description = src.getImageDescription(i);
        dest.setImageDescription(description, i);

        std::string experimentRef = src.getImageExperimentRef(i);
        dest.setImageExperimentRef(experimentRef, i);

        std::string experimenterGroupRef = src.getImageExperimenterGroupRef(i);
        dest.setImageExperimenterGroupRef(experimenterGroupRef, i);

        std::string experimenterRef = src.getImageExperimenterRef(i);
        dest.setImageExperimenterRef(experimenterRef, i);

        std::string instrumentRef = src.getImageInstrumentRef(i);
        dest.setImageInstrumentRef(instrumentRef, i);

        std::string name = src.getImageName(i);
        dest.setImageName(name, i);

        double airPressure = src.getImagingEnvironmentAirPressure(i);
        dest.setImagingEnvironmentAirPressure(airPressure, i);

        PercentFraction co2 = src.getImagingEnvironmentCO2Percent(i);
        dest.setImagingEnvironmentCO2Percent(co2, i);

        PercentFraction humidity = src.getImagingEnvironmentHumidity(i);
        dest.setImagingEnvironmentHumidity(humidity, i);

        const map_pairs_map_type& map = src.getImagingEnvironmentMap(i);
        dest.setImagingEnvironmentMap(map, i);

        double temperature = src.getImagingEnvironmentTemperature(i);
        dest.setImagingEnvironmentTemperature(temperature, i);

        std::string objectiveID = src.getObjectiveSettingsID(i);
        if (!objectiveID.empty())
          {
            dest.setObjectiveSettingsID(objectiveID, i);

            double correction = src.getObjectiveSettingsCorrectionCollar(i);
            dest.setObjectiveSettingsCorrectionCollar(correction, i);

            Medium medium = src.getObjectiveSettingsMedium(i);
            dest.setObjectiveSettingsMedium(medium, i);

            double refractiveIndex = src.getObjectiveSettingsRefractiveIndex(i);
            dest.setObjectiveSettingsRefractiveIndex(refractiveIndex, i);
          }

        std::string stageLabelName = src.getStageLabelName(i);
        if (!stageLabelName.empty())
          {
            dest.setStageLabelName(stageLabelName, i);

            double stageLabelX = src.getStageLabelX(i);
            dest.setStageLabelX(stageLabelX, i);

            double stageLabelY = src.getStageLabelY(i);
            dest.setStageLabelY(stageLabelY, i);

            double stageLabelZ = src.getStageLabelZ(i);
            dest.setStageLabelZ(stageLabelZ, i);
          }

        std::string pixelsID = src.getPixelsID(i);
        dest.setPixelsID(pixelsID, i);

          DimensionOrder order = src.getPixelsDimensionOrder(i);
          dest.setPixelsDimensionOrder(order, i);

          PositiveFloat physicalSizeX = src.getPixelsPhysicalSizeX(i);
          dest.setPixelsPhysicalSizeX(physicalSizeX, i);

          PositiveFloat physicalSizeY = src.getPixelsPhysicalSizeY(i);
          dest.setPixelsPhysicalSizeY(physicalSizeY, i);

          PositiveFloat physicalSizeZ = src.getPixelsPhysicalSizeZ(i);
          dest.setPixelsPhysicalSizeZ(physicalSizeZ, i);

          PositiveInteger sizeC = src.getPixelsSizeC(i);
          dest.setPixelsSizeC(sizeC, i);

          PositiveInteger sizeT = src.getPixelsSizeT(i);
          dest.setPixelsSizeT(sizeT, i);

          PositiveInteger sizeX = src.getPixelsSizeX(i);
          dest.setPixelsSizeX(sizeX, i);

          PositiveInteger sizeY = src.getPixelsSizeY(i);
          dest.setPixelsSizeY(sizeY, i);

          PositiveInteger sizeZ = src.getPixelsSizeZ(i);
          dest.setPixelsSizeZ(sizeZ, i);

          double timeIncrement = src.getPixelsTimeIncrement(i);
          dest.setPixelsTimeIncrement(timeIncrement, i);

          PixelType type = src.getPixelsType(i);
          dest.setPixelsType(type, i);

          bool bigEndian = src.getPixelsBigEndian(i);
          dest.setPixelsBigEndian(bigEndian, i);

          bool interleaved = src.getPixelsInterleaved(i);
          dest.setPixelsInterleaved(interleaved, i);

          PositiveInteger significantBits = src.getPixelsSignificantBits(i);
          dest.setPixelsSignificantBits(significantBits, i);

        index_type binDataCount = 0;
          binDataCount = src.getPixelsBinDataCount(i);
        for (index_type q = 0; q < binDataCount; ++q)
          {
            bool bigEndian = src.getPixelsBinDataBigEndian(i, q);
            dest.setPixelsBinDataBigEndian(bigEndian, i, q);
        }

      index_type annotationRefCount = 0;
        annotationRefCount = src.getImageAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getImageAnnotationRef(i, q);
          dest.setImageAnnotationRef(annotationRef, i, q);
      }

      index_type channelCount = 0;
        channelCount = src.getChannelCount(i);
      for (index_type c = 0; c < channelCount; ++c)
        {
          std::string channelID = src.getChannelID(i, c);
          dest.setChannelID(channelID, i, c);

          AcquisitionMode mode = src.getChannelAcquisitionMode(i, c);
          dest.setChannelAcquisitionMode(mode, i, c);

          Color color = src.getChannelColor(i, c);
          dest.setChannelColor(color, i, c);

          ContrastMethod method = src.getChannelContrastMethod(i, c);
          dest.setChannelContrastMethod(method, i, c);

          PositiveFloat emWave = src.getChannelEmissionWavelength(i, c);
          dest.setChannelEmissionWavelength(emWave, i, c);

          PositiveFloat exWave = src.getChannelExcitationWavelength(i, c);
          dest.setChannelExcitationWavelength(exWave, i, c);

          std::string filterSetRef = src.getChannelFilterSetRef(i, c);
          dest.setChannelFilterSetRef(filterSetRef, i, c);

          std::string fluor = src.getChannelFluor(i, c);
          dest.setChannelFluor(fluor, i, c);

          IlluminationType illumType = src.getChannelIlluminationType(i, c);
          dest.setChannelIlluminationType(illumType, i, c);

          double ndFilter = src.getChannelNDFilter(i, c);
          dest.setChannelNDFilter(ndFilter, i, c);

          std::string channelName = src.getChannelName(i, c);
          dest.setChannelName(channelName, i, c);

          double pinholeSize = src.getChannelPinholeSize(i, c);
          dest.setChannelPinholeSize(pinholeSize, i, c);

          int32_t pockelCell = src.getChannelPockelCellSetting(i, c);
          dest.setChannelPockelCellSetting(pockelCell, i, c);

          PositiveInteger samplesPerPixel = src.getChannelSamplesPerPixel(i, c);
          dest.setChannelSamplesPerPixel(samplesPerPixel, i, c);

          std::string detectorSettingsID = src.getDetectorSettingsID(i, c);
          if (!detectorSettingsID.empty()) {
            dest.setDetectorSettingsID(detectorSettingsID, i, c);

              Binning binning = src.getDetectorSettingsBinning(i, c);
              dest.setDetectorSettingsBinning(binning, i, c);

              double gain = src.getDetectorSettingsGain(i, c);
              dest.setDetectorSettingsGain(gain, i, c);

              PositiveInteger integration =
                src.getDetectorSettingsIntegration(i, c);
              dest.setDetectorSettingsIntegration(integration, i, c);

              double offset = src.getDetectorSettingsOffset(i, c);
              dest.setDetectorSettingsOffset(offset, i, c);

              double readOutRate = src.getDetectorSettingsReadOutRate(i, c);
              dest.setDetectorSettingsReadOutRate(readOutRate, i, c);

              double voltage = src.getDetectorSettingsVoltage(i, c);
              dest.setDetectorSettingsVoltage(voltage, i, c);

              double zoom = src.getDetectorSettingsZoom(i, c);
              dest.setDetectorSettingsZoom(zoom, i, c);
          }

          std::string dichroicRef = src.getLightPathDichroicRef(i, c);
          dest.setLightPathDichroicRef(dichroicRef, i, c);

          std::string lightSourceID = src.getChannelLightSourceSettingsID(i, c);
          if (!lightSourceID.empty())
            {
            dest.setChannelLightSourceSettingsID(lightSourceID, i, c);

              PercentFraction attenuation =
                src.getChannelLightSourceSettingsAttenuation(i, c);
              dest.setChannelLightSourceSettingsAttenuation(attenuation, i, c);

              PositiveFloat wavelength = src.getChannelLightSourceSettingsWavelength(i, c);
              dest.setChannelLightSourceSettingsWavelength(wavelength, i, c);
          }

        index_type channelAnnotationRefCount = 0;
          channelAnnotationRefCount = src.getChannelAnnotationRefCount(i, c);
        for (index_type q = 0; q < channelAnnotationRefCount; ++q)
          {
            std::string channelAnnotationRef = src.getChannelAnnotationRef(i, c, q);
            dest.setChannelAnnotationRef(channelAnnotationRef, i, c, q);
        }

        index_type emFilterRefCount = 0;
          emFilterRefCount = src.getLightPathEmissionFilterRefCount(i, c);
        for (index_type q = 0; q < emFilterRefCount; ++q)
          {
            std::string emFilterRef = src.getLightPathEmissionFilterRef(i, c, q);
            dest.setLightPathEmissionFilterRef(emFilterRef, i, c, q);
        }

        index_type exFilterRefCount = 0;
          exFilterRefCount = src.getLightPathExcitationFilterRefCount(i, c);
        for (index_type q = 0; q < exFilterRefCount; ++q)
          {
            std::string exFilterRef = src.getLightPathExcitationFilterRef(i, c, q);
            dest.setLightPathExcitationFilterRef(exFilterRef, i, c, q);
        }
      }

      index_type planeCount = 0;
        planeCount = src.getPlaneCount(i);
      for (index_type p = 0; p < planeCount; ++p)
        {
          double deltaT = src.getPlaneDeltaT(i, p);
          dest.setPlaneDeltaT(deltaT, i, p);

          double exposureTime = src.getPlaneExposureTime(i, p);
          dest.setPlaneExposureTime(exposureTime, i, p);

          std::string sha1 = src.getPlaneHashSHA1(i, p);
          dest.setPlaneHashSHA1(sha1, i, p);

          double positionX = src.getPlanePositionX(i, p);
          dest.setPlanePositionX(positionX, i, p);

          double positionY = src.getPlanePositionY(i, p);
          dest.setPlanePositionY(positionY, i, p);

          double positionZ = src.getPlanePositionZ(i, p);
          dest.setPlanePositionZ(positionZ, i, p);

          NonNegativeInteger theC = src.getPlaneTheC(i, p);
          dest.setPlaneTheC(theC, i, p);

          NonNegativeInteger theT = src.getPlaneTheT(i, p);
          dest.setPlaneTheT(theT, i, p);

          NonNegativeInteger theZ = src.getPlaneTheZ(i, p);
          dest.setPlaneTheZ(theZ, i, p);

        index_type planeAnnotationRefCount = 0;
          planeAnnotationRefCount = src.getPlaneAnnotationRefCount(i, p);
        for (index_type q = 0; q < planeAnnotationRefCount; ++q)
          {
            std::string planeAnnotationRef = src.getPlaneAnnotationRef(i, p, q);
            dest.setPlaneAnnotationRef(planeAnnotationRef, i, p, q);
        }
      }

      index_type microbeamCount = 0;
        microbeamCount = src.getMicrobeamManipulationRefCount(i);
      for (index_type q = 0; q < microbeamCount; ++q)
        {
          std::string microbeamRef = src.getImageMicrobeamManipulationRef(i, q);
          dest.setImageMicrobeamManipulationRef(microbeamRef, i, q);
      }

      index_type roiRefCount = 0;
        roiRefCount = src.getImageROIRefCount(i);
      for (index_type q = 0; q < roiRefCount; ++q)
        {
          std::string roiRef = src.getImageROIRef(i, q);
          dest.setImageROIRef(roiRef, i, q);
      }

      index_type tiffDataCount = 0;
        tiffDataCount = src.getTiffDataCount(i);
      for (index_type q = 0; q < tiffDataCount; ++q)
        {
          std::string uuid = src.getUUIDValue(i, q);
          dest.setUUIDValue(uuid, i, q);

          std::string filename = src.getUUIDFileName(i, q);
          dest.setUUIDFileName(filename, i, q);

          NonNegativeInteger firstC = src.getTiffDataFirstC(i, q);
          dest.setTiffDataFirstC(firstC, i, q);

          NonNegativeInteger firstT = src.getTiffDataFirstT(i, q);
          dest.setTiffDataFirstT(firstT, i, q);

          NonNegativeInteger firstZ = src.getTiffDataFirstZ(i, q);
          dest.setTiffDataFirstZ(firstZ, i, q);

          NonNegativeInteger ifd = src.getTiffDataIFD(i, q);
          dest.setTiffDataIFD(ifd, i, q);

          NonNegativeInteger tiffDataPlaneCount = src.getTiffDataPlaneCount(i, q);
          dest.setTiffDataPlaneCount(tiffDataPlaneCount, i, q);
      }
    }
  }

  void
  convertLightSources(const MetadataRetrieve& src,
                      MetadataStore&          dest,
                      index_type              instrumentIndex)
  {
    index_type lightSourceCount = 0;
      lightSourceCount = src.getLightSourceCount(instrumentIndex);

    for (index_type lightSource = 0; lightSource < lightSourceCount; ++lightSource)
      {
      std::string type = src.getLightSourceType(instrumentIndex, lightSource);
      if (type == "Arc") {
          std::string id = src.getArcID(instrumentIndex, lightSource);
          if (!id.empty()) dest.setArcID(id, instrumentIndex, lightSource);

          std::string lotNumber = src.getArcLotNumber(instrumentIndex, lightSource);
          if (!lotNumber.empty()) {
            dest.setArcLotNumber(lotNumber, instrumentIndex, lightSource);
          }

          std::string manufacturer =
            src.getArcManufacturer(instrumentIndex, lightSource);
          if (!manufacturer.empty()) {
            dest.setArcManufacturer(manufacturer, instrumentIndex, lightSource);
          }

          std::string model = src.getArcModel(instrumentIndex, lightSource);
          if (!model.empty()) {
            dest.setArcModel(model, instrumentIndex, lightSource);
          }

          double power = src.getArcPower(instrumentIndex, lightSource);
          dest.setArcPower(power, instrumentIndex, lightSource);

          std::string serialNumber =
            src.getArcSerialNumber(instrumentIndex, lightSource);
          if (!serialNumber.empty()) {
            dest.setArcSerialNumber(serialNumber, instrumentIndex, lightSource);
          }

          ArcType arcType = src.getArcType(instrumentIndex, lightSource);
          dest.setArcType(arcType, instrumentIndex, lightSource);

        index_type lightSourceAnnotationRefCount = 0;
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
          {
            std::string lightSourceAnnotationRef = src.getArcAnnotationRef(instrumentIndex, lightSource, r);
            dest.setArcAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
        }
      }
      else if (type == "Filament") {
          std::string id = src.getFilamentID(instrumentIndex, lightSource);
          if (!id.empty()) dest.setFilamentID(id, instrumentIndex, lightSource);

          std::string lotNumber =
            src.getFilamentLotNumber(instrumentIndex, lightSource);
          if (!lotNumber.empty()) {
            dest.setFilamentLotNumber(lotNumber, instrumentIndex, lightSource);
          }

          std::string manufacturer =
            src.getFilamentManufacturer(instrumentIndex, lightSource);
          if (!manufacturer.empty()) {
            dest.setFilamentManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }

          std::string model = src.getFilamentModel(instrumentIndex, lightSource);
          if (!model.empty()) {
            dest.setFilamentModel(model, instrumentIndex, lightSource);
          }

          double power = src.getFilamentPower(instrumentIndex, lightSource);
            dest.setFilamentPower(power, instrumentIndex, lightSource);

          std::string serialNumber =
            src.getFilamentSerialNumber(instrumentIndex, lightSource);
          if (!serialNumber.empty()) {
            dest.setFilamentSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }

          FilamentType filamentType =
            src.getFilamentType(instrumentIndex, lightSource);
            dest.setFilamentType(filamentType, instrumentIndex, lightSource);

        index_type lightSourceAnnotationRefCount = 0;
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
          {
            std::string lightSourceAnnotationRef = src.getFilamentAnnotationRef(instrumentIndex, lightSource, r);
            dest.setFilamentAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
        }
      }
      else if (type == "GenericExcitationSource") {
          std::string id =
            src.getGenericExcitationSourceID(instrumentIndex, lightSource);
            dest.setGenericExcitationSourceID(id, instrumentIndex, lightSource);

            const map_pairs_map_type& map =
              src.getGenericExcitationSourceMap(instrumentIndex, lightSource);
            dest.setGenericExcitationSourceMap(map, instrumentIndex, lightSource);

          std::string lotNumber = src.getGenericExcitationSourceLotNumber(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceLotNumber(lotNumber,
            instrumentIndex, lightSource);

          std::string manufacturer = src.getGenericExcitationSourceManufacturer(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceManufacturer(manufacturer,
            instrumentIndex, lightSource);

          std::string model =
            src.getGenericExcitationSourceModel(instrumentIndex, lightSource);
          dest.setGenericExcitationSourceModel(model,
            instrumentIndex, lightSource);

          double power =
            src.getGenericExcitationSourcePower(instrumentIndex, lightSource);
          dest.setGenericExcitationSourcePower(power,
            instrumentIndex, lightSource);

          std::string serialNumber = src.getGenericExcitationSourceSerialNumber(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceSerialNumber(serialNumber,
            instrumentIndex, lightSource);
      }
      else if (type == "Laser") {
          std::string id = src.getLaserID(instrumentIndex, lightSource);
          if (!id.empty()) dest.setLaserID(id, instrumentIndex, lightSource);

          std::string lotNumber =
            src.getLaserLotNumber(instrumentIndex, lightSource);
          if (!lotNumber.empty()) {
            dest.setLaserLotNumber(lotNumber, instrumentIndex, lightSource);
          }

          std::string manufacturer =
            src.getLaserManufacturer(instrumentIndex, lightSource);
          if (!manufacturer.empty()) {
            dest.setLaserManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }

          std::string model = src.getLaserModel(instrumentIndex, lightSource);
          if (!model.empty()) {
            dest.setLaserModel(model, instrumentIndex, lightSource);
          }

          double power = src.getLaserPower(instrumentIndex, lightSource);
            dest.setLaserPower(power, instrumentIndex, lightSource);

            std::string serialNumber =
            src.getLaserSerialNumber(instrumentIndex, lightSource);
          if (!serialNumber.empty()) {
            dest.setLaserSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }

          LaserType laserType = src.getLaserType(instrumentIndex, lightSource);
            dest.setLaserType(laserType, instrumentIndex, lightSource);

          PositiveInteger frequencyMultiplication =
            src.getLaserFrequencyMultiplication(instrumentIndex, lightSource);
            dest.setLaserFrequencyMultiplication(
              frequencyMultiplication, instrumentIndex, lightSource);

          LaserMedium medium =
            src.getLaserLaserMedium(instrumentIndex, lightSource);
            dest.setLaserLaserMedium(medium, instrumentIndex, lightSource);

          bool pockelCell =
            src.getLaserPockelCell(instrumentIndex, lightSource);
            dest.setLaserPockelCell(pockelCell, instrumentIndex, lightSource);

          Pulse pulse = src.getLaserPulse(instrumentIndex, lightSource);
            dest.setLaserPulse(pulse, instrumentIndex, lightSource);

          std::string pump = src.getLaserPump(instrumentIndex, lightSource);
            dest.setLaserPump(pump, instrumentIndex, lightSource);

          double repetitionRate =
            src.getLaserRepetitionRate(instrumentIndex, lightSource);
            dest.setLaserRepetitionRate(
              repetitionRate, instrumentIndex, lightSource);

          bool tuneable = src.getLaserTuneable(instrumentIndex, lightSource);
          dest.setLaserTuneable(tuneable, instrumentIndex, lightSource);

          PositiveFloat wavelength =
            src.getLaserWavelength(instrumentIndex, lightSource);
          dest.setLaserWavelength(wavelength, instrumentIndex, lightSource);

          index_type lightSourceAnnotationRefCount = 0;
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
          {
            std::string lightSourceAnnotationRef = src.getLaserAnnotationRef(instrumentIndex, lightSource, r);
            dest.setLaserAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
        }
      }
      else if (type == "LightEmittingDiode") {
          std::string id = src.getLightEmittingDiodeID(instrumentIndex, lightSource);
          if (!id.empty()) {
            dest.setLightEmittingDiodeID(id, instrumentIndex, lightSource);
          }

          std::string lotNumber =
            src.getLightEmittingDiodeLotNumber(instrumentIndex, lightSource);
          if (!lotNumber.empty()) {
            dest.setLightEmittingDiodeLotNumber(
              lotNumber, instrumentIndex, lightSource);
          }

          std::string manufacturer =
            src.getLightEmittingDiodeManufacturer(instrumentIndex, lightSource);
          if (!manufacturer.empty()) {
            dest.setLightEmittingDiodeManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }

          std::string model =
            src.getLightEmittingDiodeModel(instrumentIndex, lightSource);
          if (!model.empty()) {
            dest.setLightEmittingDiodeModel(
              model, instrumentIndex, lightSource);
          }

          double power =
            src.getLightEmittingDiodePower(instrumentIndex, lightSource);
            dest.setLightEmittingDiodePower(
              power, instrumentIndex, lightSource);

          std::string serialNumber =
            src.getLightEmittingDiodeSerialNumber(instrumentIndex, lightSource);
          if (!serialNumber.empty()) {
            dest.setLightEmittingDiodeSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }

        index_type lightSourceAnnotationRefCount = 0;
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        for (index_type r = 0; r < lightSourceAnnotationRefCount; ++r)
          {
            std::string lightSourceAnnotationRef = src.getLightEmittingDiodeAnnotationRef(instrumentIndex, lightSource, r);
            dest.setLightEmittingDiodeAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
        }
      }
    }
  }

  void
  convertInstruments(const MetadataRetrieve& src,
                     MetadataStore&          dest)
  {
    index_type instrumentCount = 0;
      instrumentCount = src.getInstrumentCount();
    for (index_type i = 0; i < instrumentCount; ++i)
      {
        std::string id = src.getInstrumentID(i);
        dest.setInstrumentID(id, i);

        std::string microscopeLotNumber = src.getMicroscopeLotNumber(i);
        dest.setMicroscopeLotNumber(microscopeLotNumber, i);

        std::string microscopeManufacturer = src.getMicroscopeManufacturer(i);
        dest.setMicroscopeManufacturer(microscopeManufacturer, i);

        std::string microscopeModel = src.getMicroscopeModel(i);
        dest.setMicroscopeModel(microscopeModel, i);

        std::string microscopeSerialNumber = src.getMicroscopeSerialNumber(i);
        dest.setMicroscopeSerialNumber(microscopeSerialNumber, i);

        MicroscopeType microscopeType = src.getMicroscopeType(i);
        dest.setMicroscopeType(microscopeType, i);

      index_type detectorCount = 0;
        detectorCount = src.getDetectorCount(i);
      for (index_type q = 0; q < detectorCount; ++q)
        {
          std::string detectorID = src.getDetectorID(i, q);
          dest.setDetectorID(detectorID, i, q);

          double amplificationGain = src.getDetectorAmplificationGain(i, q);
          dest.setDetectorAmplificationGain(amplificationGain, i, q);

          double gain = src.getDetectorGain(i, q);
          dest.setDetectorGain(gain, i, q);

          std::string lotNumber = src.getDetectorLotNumber(i, q);
          dest.setDetectorLotNumber(lotNumber, i, q);

          std::string manufacturer = src.getDetectorManufacturer(i, q);
          dest.setDetectorManufacturer(manufacturer, i, q);

          std::string model = src.getDetectorModel(i, q);
          dest.setDetectorModel(model, i, q);

          double offset = src.getDetectorOffset(i, q);
          dest.setDetectorOffset(offset, i, q);

          std::string serialNumber = src.getDetectorSerialNumber(i, q);
          dest.setDetectorSerialNumber(serialNumber, i, q);

          DetectorType detectorType = src.getDetectorType(i, q);
          dest.setDetectorType(detectorType, i, q);

          double voltage = src.getDetectorVoltage(i, q);
          dest.setDetectorVoltage(voltage, i, q);

          double zoom = src.getDetectorZoom(i, q);
          dest.setDetectorZoom(zoom, i, q);

        index_type detectorAnnotationRefCount = 0;
          detectorAnnotationRefCount = src.getDetectorAnnotationRefCount(i, q);
        for (index_type r = 0; r < detectorAnnotationRefCount; ++r)
          {
            std::string detectorAnnotationRef = src.getDetectorAnnotationRef(i, q, r);
            dest.setDetectorAnnotationRef(detectorAnnotationRef, i, q, r);
        }
      }

      index_type dichroicCount = 0;
        dichroicCount = src.getDichroicCount(i);
      for (index_type q = 0; q < dichroicCount; ++q)
        {
          std::string dichroicID = src.getDichroicID(i, q);
          dest.setDichroicID(dichroicID, i, q);

          std::string lotNumber = src.getDichroicLotNumber(i, q);
          dest.setDichroicLotNumber(lotNumber, i, q);

          std::string manufacturer = src.getDichroicManufacturer(i, q);
          dest.setDichroicManufacturer(manufacturer, i, q);

          std::string model = src.getDichroicModel(i, q);
          dest.setDichroicModel(model, i, q);

          std::string serialNumber = src.getDichroicSerialNumber(i, q);
          dest.setDichroicSerialNumber(serialNumber, i, q);

        index_type dichroicAnnotationRefCount = 0;
          dichroicAnnotationRefCount = src.getDichroicAnnotationRefCount(i,q);
        for (index_type r = 0; r < dichroicAnnotationRefCount; ++r)
          {
            std::string dichroicAnnotationRef = src.getDichroicAnnotationRef(i, q, r);
            dest.setDichroicAnnotationRef(dichroicAnnotationRef, i, q, r);
        }
      }

      index_type filterCount = 0;
        filterCount = src.getFilterCount(i);
      for (index_type q = 0; q < filterCount; ++q)
        {
          std::string filterID = src.getFilterID(i, q);
          dest.setFilterID(filterID, i, q);

          std::string filterWheel = src.getFilterFilterWheel(i, q);
          dest.setFilterFilterWheel(filterWheel, i, q);

          std::string lotNumber = src.getFilterLotNumber(i, q);
          dest.setFilterLotNumber(lotNumber, i, q);

          std::string manufacturer = src.getFilterManufacturer(i, q);
          dest.setFilterManufacturer(manufacturer, i, q);

          std::string model = src.getFilterModel(i, q);
          dest.setFilterModel(model, i, q);

          std::string serialNumber = src.getFilterSerialNumber(i, q);
          dest.setFilterSerialNumber(serialNumber, i, q);

          FilterType filterType = src.getFilterType(i, q);
          dest.setFilterType(filterType, i, q);

          PositiveInteger cutIn = src.getTransmittanceRangeCutIn(i, q);
          dest.setTransmittanceRangeCutIn(cutIn, i, q);

          NonNegativeInteger cutInTolerance = src.getTransmittanceRangeCutInTolerance(i, q);
          dest.setTransmittanceRangeCutInTolerance(cutInTolerance, i, q);

          PositiveInteger cutOut = src.getTransmittanceRangeCutOut(i, q);
          dest.setTransmittanceRangeCutOut(cutOut, i, q);

          NonNegativeInteger cutOutTolerance = src.getTransmittanceRangeCutOutTolerance(i, q);
          dest.setTransmittanceRangeCutOutTolerance(cutOutTolerance, i, q);

          PercentFraction transmittance = src.getTransmittanceRangeTransmittance(i, q);
          dest.setTransmittanceRangeTransmittance(transmittance, i, q);

        index_type filterAnnotationRefCount = 0;
          filterAnnotationRefCount = src.getFilterAnnotationRefCount(i, q);
        for (index_type r = 0; r < filterAnnotationRefCount; ++r)
          {
            std::string filterAnnotationRef = src.getFilterAnnotationRef(i, q, r);
            dest.setFilterAnnotationRef(filterAnnotationRef, i, q, r);
        }
      }

      index_type objectiveCount = 0;
        objectiveCount = src.getObjectiveCount(i);
      for (index_type q = 0; q < objectiveCount; ++q)
        {
          std::string objectiveID = src.getObjectiveID(i, q);
          dest.setObjectiveID(objectiveID, i, q);

          double calibratedMag = src.getObjectiveCalibratedMagnification(i, q);
          dest.setObjectiveCalibratedMagnification(calibratedMag, i, q);

          Correction correction = src.getObjectiveCorrection(i, q);
          dest.setObjectiveCorrection(correction, i, q);

          Immersion immersion = src.getObjectiveImmersion(i, q);
          dest.setObjectiveImmersion(immersion, i, q);

          bool iris = src.getObjectiveIris(i, q);
          dest.setObjectiveIris(iris, i, q);

          double lensNA = src.getObjectiveLensNA(i, q);
          dest.setObjectiveLensNA(lensNA, i, q);

          std::string lotNumber = src.getObjectiveLotNumber(i, q);
          dest.setObjectiveLotNumber(lotNumber, i, q);

          std::string manufacturer = src.getObjectiveManufacturer(i, q);
          dest.setObjectiveManufacturer(manufacturer, i, q);

          std::string model = src.getObjectiveModel(i, q);
          dest.setObjectiveModel(model, i, q);

          double nominalMag = src.getObjectiveNominalMagnification(i, q);
          dest.setObjectiveNominalMagnification(nominalMag, i, q);

          std::string serialNumber = src.getObjectiveSerialNumber(i, q);
          dest.setObjectiveSerialNumber(serialNumber, i, q);

          double workingDistance = src.getObjectiveWorkingDistance(i, q);
          dest.setObjectiveWorkingDistance(workingDistance, i, q);

        index_type objectiveAnnotationRefCount = 0;
          objectiveAnnotationRefCount = src.getObjectiveAnnotationRefCount(i, q);

        for (index_type r = 0; r < objectiveAnnotationRefCount; ++r)
          {
            std::string objectiveAnnotationRef = src.getObjectiveAnnotationRef(i, q, r);
            dest.setObjectiveAnnotationRef(objectiveAnnotationRef, i, q, r);
        }
      }

      index_type filterSetCount = 0;
      filterSetCount = src.getFilterSetCount(i);
      for (index_type q = 0; q < filterSetCount; ++q)
        {
          std::string filterSetID = src.getFilterSetID(i, q);
          dest.setFilterSetID(filterSetID, i, q);

          std::string dichroicRef = src.getFilterSetDichroicRef(i, q);
          dest.setFilterSetDichroicRef(dichroicRef, i, q);

          std::string lotNumber = src.getFilterSetLotNumber(i, q);
          dest.setFilterSetLotNumber(lotNumber, i, q);

          std::string manufacturer = src.getFilterSetManufacturer(i, q);
          dest.setFilterSetManufacturer(manufacturer, i, q);

          std::string model = src.getFilterSetModel(i, q);
          dest.setFilterSetModel(model, i, q);

          std::string serialNumber = src.getFilterSetSerialNumber(i, q);
          dest.setFilterSetSerialNumber(serialNumber, i, q);

        index_type emFilterCount = 0;
          emFilterCount = src.getFilterSetEmissionFilterRefCount(i, q);
        for (index_type f = 0; f < emFilterCount; ++f)
          {
            std::string emFilterRef = src.getFilterSetEmissionFilterRef(i, q, f);
            dest.setFilterSetEmissionFilterRef(emFilterRef, i, q, f);
        }

        index_type exFilterCount = 0;
          exFilterCount = src.getFilterSetExcitationFilterRefCount(i, q);
        for (index_type f = 0; f < exFilterCount; ++f)
          {
            std::string exFilterRef = src.getFilterSetExcitationFilterRef(i, q, f);
            dest.setFilterSetExcitationFilterRef(exFilterRef, i, q, f);
        }
      }
      convertLightSources(src, dest, i);
    }
  }

  void
  convertListAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type listAnnotationCount = 0;
      listAnnotationCount = src.getListAnnotationCount();
    for (index_type i = 0; i < listAnnotationCount; ++i)
      {
        std::string id = src.getListAnnotationID(i);
        dest.setListAnnotationID(id, i);

        std::string description = src.getListAnnotationDescription(i);
        dest.setListAnnotationDescription(description, i);

        std::string ns = src.getListAnnotationNamespace(i);
        dest.setListAnnotationNamespace(ns, i);

        std::string annotator = src.getListAnnotationAnnotator(i);
        dest.setListAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getListAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getListAnnotationAnnotationRef(i, a);
          dest.setListAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertLongAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type longAnnotationCount = 0;
      longAnnotationCount = src.getLongAnnotationCount();
    for (index_type i = 0; i < longAnnotationCount; ++i)
      {
        std::string id = src.getLongAnnotationID(i);
        dest.setLongAnnotationID(id, i);

        std::string description = src.getLongAnnotationDescription(i);
        dest.setLongAnnotationDescription(description, i);

        std::string ns = src.getLongAnnotationNamespace(i);
        dest.setLongAnnotationNamespace(ns, i);

        int64_t value = src.getLongAnnotationValue(i);
        dest.setLongAnnotationValue(value, i);

        std::string annotator = src.getLongAnnotationAnnotator(i);
        dest.setLongAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getLongAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getLongAnnotationAnnotationRef(i, a);
          dest.setLongAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertMapAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type mapAnnotationCount = 0;
      mapAnnotationCount = src.getMapAnnotationCount();
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

      index_type annotationRefCount = 0;
        annotationRefCount = src.getMapAnnotationAnnotationCount(i);
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
    index_type plateCount = 0;
      plateCount = src.getPlateCount();
    for (index_type i = 0; i < plateCount; ++i)
      {
        std::string id = src.getPlateID(i);
        dest.setPlateID(id, i);

        NamingConvention columnConvention = src.getPlateColumnNamingConvention(i);
        dest.setPlateColumnNamingConvention(columnConvention, i);

        PositiveInteger columns = src.getPlateColumns(i);
        dest.setPlateColumns(columns, i);

        std::string description = src.getPlateDescription(i);
        dest.setPlateDescription(description, i);

        std::string externalID = src.getPlateExternalIdentifier(i);
        dest.setPlateExternalIdentifier(externalID, i);

        NonNegativeInteger fieldIndex = src.getPlateFieldIndex(i);
        dest.setPlateFieldIndex(fieldIndex, i);

        std::string name = src.getPlateName(i);
        dest.setPlateName(name, i);

        NamingConvention rowConvention = src.getPlateRowNamingConvention(i);
        dest.setPlateRowNamingConvention(rowConvention, i);

        PositiveInteger rows = src.getPlateRows(i);
        dest.setPlateRows(rows, i);

        std::string status = src.getPlateStatus(i);
        dest.setPlateStatus(status, i);

        double wellOriginX = src.getPlateWellOriginX(i);
        dest.setPlateWellOriginX(wellOriginX, i);

        double wellOriginY = src.getPlateWellOriginY(i);
        dest.setPlateWellOriginY(wellOriginY, i);

      index_type wellCount = 0;
      wellCount = src.getWellCount(i);
      for (index_type q = 0; q < wellCount; ++q)
        {
          std::string wellID = src.getWellID(i, q);
          dest.setWellID(wellID, i, q);

          Color color = src.getWellColor(i, q);
          dest.setWellColor(color, i, q);

          NonNegativeInteger column = src.getWellColumn(i, q);
          dest.setWellColumn(column, i, q);

          std::string externalDescription = src.getWellExternalDescription(i, q);
          dest.setWellExternalDescription(externalDescription, i, q);

          std::string wellExternalID = src.getWellExternalIdentifier(i, q);
          dest.setWellExternalIdentifier(wellExternalID, i, q);

          std::string reagentRef = src.getWellReagentRef(i, q);
          dest.setWellReagentRef(reagentRef, i, q);

          NonNegativeInteger row = src.getWellRow(i, q);
          dest.setWellRow(row, i, q);

          std::string type = src.getWellType(i, q);
          dest.setWellType(type, i, q);

        index_type wellAnnotationRefCount = 0;
          src.getWellAnnotationRefCount(i, q);
        for (index_type a = 0; a < wellAnnotationRefCount; ++a)
          {
            std::string wellAnnotationRef = src.getWellAnnotationRef(i, q, a);
            dest.setWellAnnotationRef(wellAnnotationRef, i, q, a);
        }

        index_type wellSampleCount = 0;
          wellSampleCount = src.getWellSampleCount(i, q);
        for (index_type w = 0; w < wellSampleCount; ++w)
          {
            std::string wellSampleID = src.getWellSampleID(i, q, w);
            dest.setWellSampleID(wellSampleID, i, q, w);

            NonNegativeInteger index = src.getWellSampleIndex(i, q, w);
            dest.setWellSampleIndex(index, i, q, w);

            std::string imageRef = src.getWellSampleImageRef(i, q, w);
            dest.setWellSampleImageRef(imageRef, i, q, w);

            double positionX = src.getWellSamplePositionX(i, q, w);
            dest.setWellSamplePositionX(positionX, i, q, w);

            double positionY = src.getWellSamplePositionY(i, q, w);
            dest.setWellSamplePositionY(positionY, i, q, w);

            Timestamp timepoint= src.getWellSampleTimepoint(i, q, w);
            dest.setWellSampleTimepoint(timepoint, i, q, w);
        }
      }

      index_type plateAcquisitionCount = 0;
      plateAcquisitionCount = src.getPlateAcquisitionCount(i);
      for (index_type q = 0; q < plateAcquisitionCount; ++q)
        {
          std::string plateAcquisitionID = src.getPlateAcquisitionID(i, q);
          dest.setPlateAcquisitionID(plateAcquisitionID, i, q);

          std::string acquisitionDescription = src.getPlateAcquisitionDescription(i, q);
          dest.setPlateAcquisitionDescription(acquisitionDescription, i, q);

          Timestamp endTime = src.getPlateAcquisitionEndTime(i, q);
          dest.setPlateAcquisitionEndTime(endTime, i, q);

          PositiveInteger maximumFields = src.getPlateAcquisitionMaximumFieldCount(i, q);
          dest.setPlateAcquisitionMaximumFieldCount(maximumFields, i, q);

          std::string acquisitionName = src.getPlateAcquisitionName(i, q);
          dest.setPlateAcquisitionName(acquisitionName, i, q);

          Timestamp startTime = src.getPlateAcquisitionStartTime(i, q);
          dest.setPlateAcquisitionStartTime(startTime, i, q);

          index_type plateAcquisitionAnnotationRefCount = 0;
        plateAcquisitionAnnotationRefCount = src.getPlateAcquisitionAnnotationRefCount(i, q);
        for (index_type a = 0; a < plateAcquisitionAnnotationRefCount; ++a)
          {
            std::string plateAcquisitionAnnotationRef = src.getPlateAcquisitionAnnotationRef(i, q, a);
            dest.setPlateAcquisitionAnnotationRef(plateAcquisitionAnnotationRef, i, q, a);
        }

        index_type wellSampleRefCount = 0;
          wellSampleRefCount = src.getWellSampleRefCount(i, q);
        for (index_type w = 0; w < wellSampleRefCount; ++w)
          {
            std::string wellSampleRef = src.getPlateAcquisitionWellSampleRef(i, q, w);
            dest.setPlateAcquisitionWellSampleRef(wellSampleRef, i, q, w);
        }
      }

      index_type plateAnnotationRefCount = 0;
        plateAnnotationRefCount = src.getPlateAnnotationRefCount(i);
      for (index_type q = 0; q < plateAnnotationRefCount; ++q)
        {
          std::string annotationRef = src.getPlateAnnotationRef(i, q);
          dest.setPlateAnnotationRef(annotationRef, i, q);
      }
    }
  }

  void
  convertProjects(const MetadataRetrieve& src,
                  MetadataStore&          dest)
  {
    index_type projectCount = 0;
      projectCount = src.getProjectCount();
    for (index_type i = 0; i < projectCount; ++i)
      {
        std::string projectID = src.getProjectID(i);
        dest.setProjectID(projectID, i);

        std::string description = src.getProjectDescription(i);
        dest.setProjectDescription(description, i);

        std::string experimenterGroupRef = src.getProjectExperimenterGroupRef(i);
        dest.setProjectExperimenterGroupRef(experimenterGroupRef, i);

        std::string experimenterRef = src.getProjectExperimenterRef(i);
        dest.setProjectExperimenterRef(experimenterRef, i);

        std::string name = src.getProjectName(i);
        dest.setProjectName(name, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getProjectAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getProjectAnnotationRef(i, q);
          dest.setProjectAnnotationRef(annotationRef, i, q);
      }

      index_type datasetRefCount = 0;
        datasetRefCount = src.getDatasetRefCount(i);
      for (index_type q = 0; q < datasetRefCount; ++q)
        {
          std::string datasetRef = src.getProjectDatasetRef(i, q);
          dest.setProjectDatasetRef(datasetRef, i, q);
      }
    }
  }

  void
  convertROIs(const MetadataRetrieve& src,
              MetadataStore&          dest)
  {
    index_type roiCount = 0;
      roiCount = src.getROICount();
    for (index_type i = 0; i < roiCount; ++i)
      {
        std::string id = src.getROIID(i);
        dest.setROIID(id, i);

        std::string name = src.getROIName(i);
        dest.setROIName(name, i);

        std::string description = src.getROIDescription(i);
        dest.setROIDescription(description, i);

        std::string ns = src.getROINamespace(i);
        dest.setROINamespace(ns, i);

      index_type shapeCount = 0;
        shapeCount = src.getShapeCount(i);
      for (index_type q = 0; q < shapeCount; ++q)
        {
        std::string type = src.getShapeType(i, q);

        if (type =="Ellipse") {
            std::string shapeID = src.getEllipseID(i, q);
            dest.setEllipseID(shapeID, i, q);

            Color fillColor = src.getEllipseFillColor(i, q);
            dest.setEllipseFillColor(fillColor, i, q);

            FillRule fillRule = src.getEllipseFillRule(i, q);
            dest.setEllipseFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getEllipseFontFamily(i, q);
            dest.setEllipseFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getEllipseFontSize(i, q);
            dest.setEllipseFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getEllipseFontStyle(i, q);
            dest.setEllipseFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getEllipseLineCap(i, q);
            dest.setEllipseLineCap(lineCap, i, q);

            bool locked = src.getEllipseLocked(i, q);
            dest.setEllipseLocked(locked, i, q);

            Color strokeColor = src.getEllipseStrokeColor(i, q);
            dest.setEllipseStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getEllipseStrokeDashArray(i, q);
            dest.setEllipseStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getEllipseStrokeWidth(i, q);
            dest.setEllipseStrokeWidth(strokeWidth, i, q);

            std::string text = src.getEllipseText(i, q);
            dest.setEllipseText(text, i, q);

            NonNegativeInteger theC = src.getEllipseTheC(i, q);
            dest.setEllipseTheC(theC, i, q);

            NonNegativeInteger theT = src.getEllipseTheT(i, q);
            dest.setEllipseTheT(theT, i, q);

            NonNegativeInteger theZ = src.getEllipseTheZ(i, q);
            dest.setEllipseTheZ(theZ, i, q);

            AffineTransform transform = src.getEllipseTransform(i, q);
            dest.setEllipseTransform(transform, i, q);

            bool visible = src.getEllipseVisible(i, q);
            dest.setEllipseVisible(visible, i, q);

          double radiusX = src.getEllipseRadiusX(i, q);
          dest.setEllipseRadiusX(radiusX, i, q);

            double radiusY = src.getEllipseRadiusY(i, q);
            dest.setEllipseRadiusY(radiusY, i, q);

            double x = src.getEllipseX(i, q);
            dest.setEllipseX(x, i, q);

            double y = src.getEllipseY(i, q);
            dest.setEllipseY(y, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getEllipseAnnotationRef(i, q, r);
              dest.setEllipseAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Label") {
            std::string shapeID = src.getLabelID(i, q);
            dest.setLabelID(shapeID, i, q);

            Color fillColor = src.getLabelFillColor(i, q);
            dest.setLabelFillColor(fillColor, i, q);

            FillRule fillRule = src.getLabelFillRule(i, q);
            dest.setLabelFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getLabelFontFamily(i, q);
            dest.setLabelFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getLabelFontSize(i, q);
            dest.setLabelFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getLabelFontStyle(i, q);
            dest.setLabelFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getLabelLineCap(i, q);
            dest.setLabelLineCap(lineCap, i, q);

            bool locked = src.getLabelLocked(i, q);
            dest.setLabelLocked(locked, i, q);

            Color strokeColor = src.getLabelStrokeColor(i, q);
            dest.setLabelStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getLabelStrokeDashArray(i, q);
            dest.setLabelStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getLabelStrokeWidth(i, q);
            dest.setLabelStrokeWidth(strokeWidth, i, q);

            std::string text = src.getLabelText(i, q);
            dest.setLabelText(text, i, q);

            NonNegativeInteger theC = src.getLabelTheC(i, q);
            dest.setLabelTheC(theC, i, q);

            NonNegativeInteger theT = src.getLabelTheT(i, q);
            dest.setLabelTheT(theT, i, q);

            NonNegativeInteger theZ = src.getLabelTheZ(i, q);
            dest.setLabelTheZ(theZ, i, q);

            AffineTransform transform = src.getLabelTransform(i, q);
            dest.setLabelTransform(transform, i, q);

            bool visible = src.getLabelVisible(i, q);
            dest.setLabelVisible(visible, i, q);

            double x = src.getLabelX(i, q);
            dest.setLabelX(x, i, q);

            double y = src.getLabelY(i, q);
            dest.setLabelY(y, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getLabelAnnotationRef(i, q, r);
              dest.setLabelAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Line") {
            std::string shapeID = src.getLineID(i, q);
            dest.setLineID(shapeID, i, q);

            Color fillColor = src.getLineFillColor(i, q);
            dest.setLineFillColor(fillColor, i, q);

            FillRule fillRule = src.getLineFillRule(i, q);
            dest.setLineFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getLineFontFamily(i, q);
            dest.setLineFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getLineFontSize(i, q);
            dest.setLineFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getLineFontStyle(i, q);
            dest.setLineFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getLineLineCap(i, q);
            dest.setLineLineCap(lineCap, i, q);

            bool locked = src.getLineLocked(i, q);
            dest.setLineLocked(locked, i, q);

            Color strokeColor = src.getLineStrokeColor(i, q);
            dest.setLineStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getLineStrokeDashArray(i, q);
            dest.setLineStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getLineStrokeWidth(i, q);
            dest.setLineStrokeWidth(strokeWidth, i, q);

            std::string text = src.getLineText(i, q);
            dest.setLineText(text, i, q);

            NonNegativeInteger theC = src.getLineTheC(i, q);
            dest.setLineTheC(theC, i, q);

            NonNegativeInteger theT = src.getLineTheT(i, q);
            dest.setLineTheT(theT, i, q);

            NonNegativeInteger theZ = src.getLineTheZ(i, q);
            dest.setLineTheZ(theZ, i, q);

            AffineTransform transform = src.getLineTransform(i, q);
            dest.setLineTransform(transform, i, q);

            bool visible = src.getLineVisible(i, q);
            dest.setLineVisible(visible, i, q);

            Marker end = src.getLineMarkerEnd(i, q);
            dest.setLineMarkerEnd(end, i, q);

            Marker start = src.getLineMarkerStart(i, q);
            dest.setLineMarkerStart(start, i, q);

            double x1 = src.getLineX1(i, q);
            dest.setLineX1(x1, i, q);

            double x2 = src.getLineX2(i, q);
            dest.setLineX2(x2, i, q);

            double y1 = src.getLineY1(i, q);
            dest.setLineY1(y1, i, q);

            double y2 = src.getLineY2(i, q);
            dest.setLineY2(y2, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getLineAnnotationRef(i, q, r);
              dest.setLineAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Mask") {
            std::string shapeID = src.getMaskID(i, q);
            dest.setMaskID(shapeID, i, q);

            Color fillColor = src.getMaskFillColor(i, q);
            dest.setMaskFillColor(fillColor, i, q);

            FillRule fillRule = src.getMaskFillRule(i, q);
            dest.setMaskFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getMaskFontFamily(i, q);
            dest.setMaskFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getMaskFontSize(i, q);
            dest.setMaskFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getMaskFontStyle(i, q);
            dest.setMaskFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getMaskLineCap(i, q);
            dest.setMaskLineCap(lineCap, i, q);

            bool locked = src.getMaskLocked(i, q);
            dest.setMaskLocked(locked, i, q);

            Color strokeColor = src.getMaskStrokeColor(i, q);
            dest.setMaskStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getMaskStrokeDashArray(i, q);
            dest.setMaskStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getMaskStrokeWidth(i, q);
            dest.setMaskStrokeWidth(strokeWidth, i, q);

            std::string text = src.getMaskText(i, q);
            dest.setMaskText(text, i, q);

            NonNegativeInteger theC = src.getMaskTheC(i, q);
            dest.setMaskTheC(theC, i, q);

            NonNegativeInteger theT = src.getMaskTheT(i, q);
            dest.setMaskTheT(theT, i, q);

            NonNegativeInteger theZ = src.getMaskTheZ(i, q);
            dest.setMaskTheZ(theZ, i, q);

            AffineTransform transform = src.getMaskTransform(i, q);
            dest.setMaskTransform(transform, i, q);

            bool visible = src.getMaskVisible(i, q);
            dest.setMaskVisible(visible, i, q);

            double height = src.getMaskHeight(i, q);
            dest.setMaskHeight(height, i, q);

            double width = src.getMaskWidth(i, q);
            dest.setMaskWidth(width, i, q);

            double x = src.getMaskX(i, q);
            dest.setMaskX(x, i, q);

            double y = src.getMaskY(i, q);
            dest.setMaskY(y, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getMaskAnnotationRef(i, q, r);
              dest.setMaskAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Point") {
            std::string shapeID = src.getPointID(i, q);
            dest.setPointID(shapeID, i, q);

            Color fillColor = src.getPointFillColor(i, q);
            dest.setPointFillColor(fillColor, i, q);

            FillRule fillRule = src.getPointFillRule(i, q);
            dest.setPointFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getPointFontFamily(i, q);
            dest.setPointFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getPointFontSize(i, q);
            dest.setPointFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getPointFontStyle(i, q);
            dest.setPointFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getPointLineCap(i, q);
            dest.setPointLineCap(lineCap, i, q);

            bool locked = src.getPointLocked(i, q);
            dest.setPointLocked(locked, i, q);

            Color strokeColor = src.getPointStrokeColor(i, q);
            dest.setPointStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getPointStrokeDashArray(i, q);
            dest.setPointStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getPointStrokeWidth(i, q);
            dest.setPointStrokeWidth(strokeWidth, i, q);

            std::string text = src.getPointText(i, q);
            dest.setPointText(text, i, q);

            NonNegativeInteger theC = src.getPointTheC(i, q);
            dest.setPointTheC(theC, i, q);

            NonNegativeInteger theT = src.getPointTheT(i, q);
            dest.setPointTheT(theT, i, q);

            NonNegativeInteger theZ = src.getPointTheZ(i, q);
            dest.setPointTheZ(theZ, i, q);

            AffineTransform transform = src.getPointTransform(i, q);
            dest.setPointTransform(transform, i, q);

            bool visible = src.getPointVisible(i, q);
            dest.setPointVisible(visible, i, q);

            double x = src.getPointX(i, q);
            dest.setPointX(x, i, q);

            double y = src.getPointY(i, q);
            dest.setPointY(y, i, q);

          index_type shapeAnnotationRefCount = 0;
          shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getPointAnnotationRef(i, q, r);
              dest.setPointAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Polygon") {
            std::string shapeID = src.getPolygonID(i, q);
            dest.setPolygonID(shapeID, i, q);

            Color fillColor = src.getPolygonFillColor(i, q);
            dest.setPolygonFillColor(fillColor, i, q);

            FillRule fillRule = src.getPolygonFillRule(i, q);
            dest.setPolygonFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getPolygonFontFamily(i, q);
            dest.setPolygonFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getPolygonFontSize(i, q);
            dest.setPolygonFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getPolygonFontStyle(i, q);
            dest.setPolygonFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getPolygonLineCap(i, q);
            dest.setPolygonLineCap(lineCap, i, q);

            bool locked = src.getPolygonLocked(i, q);
            dest.setPolygonLocked(locked, i, q);

            Color strokeColor = src.getPolygonStrokeColor(i, q);
            dest.setPolygonStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getPolygonStrokeDashArray(i, q);
            dest.setPolygonStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getPolygonStrokeWidth(i, q);
            dest.setPolygonStrokeWidth(strokeWidth, i, q);

            std::string text = src.getPolygonText(i, q);
            dest.setPolygonText(text, i, q);

            NonNegativeInteger theC = src.getPolygonTheC(i, q);
            dest.setPolygonTheC(theC, i, q);

            NonNegativeInteger theT = src.getPolygonTheT(i, q);
            dest.setPolygonTheT(theT, i, q);

            NonNegativeInteger theZ = src.getPolygonTheZ(i, q);
            dest.setPolygonTheZ(theZ, i, q);

            AffineTransform transform = src.getPolygonTransform(i, q);
            dest.setPolygonTransform(transform, i, q);

            bool visible = src.getPolygonVisible(i, q);
            dest.setPolygonVisible(visible, i, q);

            std::string points = src.getPolygonPoints(i, q);
            dest.setPolygonPoints(points, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getPolygonAnnotationRef(i, q, r);
              dest.setPolygonAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Polyline") {
            std::string shapeID = src.getPolylineID(i, q);
            dest.setPolylineID(shapeID, i, q);

            Color fillColor = src.getPolylineFillColor(i, q);
            dest.setPolylineFillColor(fillColor, i, q);

            FillRule fillRule = src.getPolylineFillRule(i, q);
            dest.setPolylineFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getPolylineFontFamily(i, q);
            dest.setPolylineFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getPolylineFontSize(i, q);
            dest.setPolylineFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getPolylineFontStyle(i, q);
            dest.setPolylineFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getPolylineLineCap(i, q);
            dest.setPolylineLineCap(lineCap, i, q);

            bool locked = src.getPolylineLocked(i, q);
            dest.setPolylineLocked(locked, i, q);

            Color strokeColor = src.getPolylineStrokeColor(i, q);
            dest.setPolylineStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getPolylineStrokeDashArray(i, q);
            dest.setPolylineStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getPolylineStrokeWidth(i, q);
            dest.setPolylineStrokeWidth(strokeWidth, i, q);

            std::string text = src.getPolylineText(i, q);
            dest.setPolylineText(text, i, q);

            NonNegativeInteger theC = src.getPolylineTheC(i, q);
            dest.setPolylineTheC(theC, i, q);

            NonNegativeInteger theT = src.getPolylineTheT(i, q);
            dest.setPolylineTheT(theT, i, q);

            NonNegativeInteger theZ = src.getPolylineTheZ(i, q);
            dest.setPolylineTheZ(theZ, i, q);

            AffineTransform transform = src.getPolylineTransform(i, q);
            dest.setPolylineTransform(transform, i, q);

            bool visible = src.getPolylineVisible(i, q);
            dest.setPolylineVisible(visible, i, q);

            Marker end = src.getPolylineMarkerEnd(i, q);
            dest.setPolylineMarkerEnd(end, i, q);

            Marker start = src.getPolylineMarkerStart(i, q);
            dest.setPolylineMarkerStart(start, i, q);

            std::string points = src.getPolylinePoints(i, q);
            dest.setPolylinePoints(points, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getPolylineAnnotationRef(i, q, r);
              dest.setPolylineAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
        else if (type == "Rectangle") {
            std::string shapeID = src.getRectangleID(i, q);
            dest.setRectangleID(shapeID, i, q);

            Color fillColor = src.getRectangleFillColor(i, q);
            dest.setRectangleFillColor(fillColor, i, q);

            FillRule fillRule = src.getRectangleFillRule(i, q);
            dest.setRectangleFillRule(fillRule, i, q);

            FontFamily fontFamily = src.getRectangleFontFamily(i, q);
            dest.setRectangleFontFamily(fontFamily, i, q);

            NonNegativeInteger fontSize = src.getRectangleFontSize(i, q);
            dest.setRectangleFontSize(fontSize, i, q);

            FontStyle fontStyle = src.getRectangleFontStyle(i, q);
            dest.setRectangleFontStyle(fontStyle, i, q);

            LineCap lineCap = src.getRectangleLineCap(i, q);
            dest.setRectangleLineCap(lineCap, i, q);

            bool locked = src.getRectangleLocked(i, q);
            dest.setRectangleLocked(locked, i, q);

            Color strokeColor = src.getRectangleStrokeColor(i, q);
            dest.setRectangleStrokeColor(strokeColor, i, q);

            std::string dashArray = src.getRectangleStrokeDashArray(i, q);
            dest.setRectangleStrokeDashArray(dashArray, i, q);

            double strokeWidth = src.getRectangleStrokeWidth(i, q);
            dest.setRectangleStrokeWidth(strokeWidth, i, q);

            std::string text = src.getRectangleText(i, q);
            dest.setRectangleText(text, i, q);

            NonNegativeInteger theC = src.getRectangleTheC(i, q);
            dest.setRectangleTheC(theC, i, q);

            NonNegativeInteger theT = src.getRectangleTheT(i, q);
            dest.setRectangleTheT(theT, i, q);

            NonNegativeInteger theZ = src.getRectangleTheZ(i, q);
            dest.setRectangleTheZ(theZ, i, q);

            AffineTransform transform = src.getRectangleTransform(i, q);
            dest.setRectangleTransform(transform, i, q);

            bool visible = src.getRectangleVisible(i, q);
            dest.setRectangleVisible(visible, i, q);

            double height = src.getRectangleHeight(i, q);
            dest.setRectangleHeight(height, i, q);

            double width = src.getRectangleWidth(i, q);
            dest.setRectangleWidth(width, i, q);

            double x = src.getRectangleX(i, q);
            dest.setRectangleX(x, i, q);

            double y = src.getRectangleY(i, q);
            dest.setRectangleY(y, i, q);

          index_type shapeAnnotationRefCount = 0;
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          for (index_type r = 0; r < shapeAnnotationRefCount; ++r)
            {
              std::string shapeAnnotationRef = src.getRectangleAnnotationRef(i, q, r);
              dest.setRectangleAnnotationRef(shapeAnnotationRef, i, q, r);
          }
        }
      }

      index_type annotationRefCount = 0;
        annotationRefCount = src.getROIAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getROIAnnotationRef(i, q);
          dest.setROIAnnotationRef(annotationRef, i, q);
      }
    }
  }

  void
  convertScreens(const MetadataRetrieve& src,
                 MetadataStore&          dest)
  {
    index_type screenCount = 0;
      screenCount = src.getScreenCount();
    for (index_type i = 0; i < screenCount; ++i)
      {
        std::string id = src.getScreenID(i);
        dest.setScreenID(id, i);

        std::string description = src.getScreenDescription(i);
        dest.setScreenDescription(description, i);

        std::string name = src.getScreenName(i);
        dest.setScreenName(name, i);

        std::string protocolDescription = src.getScreenProtocolDescription(i);
        dest.setScreenProtocolDescription(protocolDescription, i);

        std::string protocolIdentifier = src.getScreenProtocolIdentifier(i);
        dest.setScreenProtocolIdentifier(protocolIdentifier, i);

        std::string reagentSetDescription = src.getScreenReagentSetDescription(i);
        dest.setScreenReagentSetDescription(reagentSetDescription, i);

        std::string reagentSetIdentifier = src.getScreenReagentSetIdentifier(i);
        dest.setScreenReagentSetIdentifier(reagentSetIdentifier, i);

        std::string type = src.getScreenType(i);
        dest.setScreenType(type, i);

      index_type plateRefCount = 0;
        plateRefCount = src.getPlateRefCount(i);
      for (index_type q = 0; q < plateRefCount; ++q)
        {
          std::string plateRef = src.getScreenPlateRef(i, q);
          dest.setScreenPlateRef(plateRef, i, q);
      }

      index_type annotationRefCount = 0;
        annotationRefCount = src.getScreenAnnotationRefCount(i);
      for (index_type q = 0; q < annotationRefCount; ++q)
        {
          std::string annotationRef = src.getScreenAnnotationRef(i, q);
          dest.setScreenAnnotationRef(annotationRef, i, q);
      }

      index_type reagentCount = 0;
        reagentCount = src.getReagentCount(i);
      for (index_type q = 0; q < reagentCount; ++q)
        {
          std::string reagentID = src.getReagentID(i, q);
          dest.setReagentID(reagentID, i, q);

          std::string reagentDescription = src.getReagentDescription(i, q);
          dest.setReagentDescription(reagentDescription, i, q);

          std::string reagentName = src.getReagentName(i, q);
          dest.setReagentName(reagentName, i, q);

          std::string reagentIdentifier = src.getReagentReagentIdentifier(i, q);
          dest.setReagentReagentIdentifier(reagentIdentifier, i ,q);

        index_type reagentAnnotationRefCount = 0;
          reagentAnnotationRefCount = src.getReagentAnnotationRefCount(i, q);
        for (index_type r = 0; r < reagentAnnotationRefCount; ++r)
          {
            std::string reagentAnnotationRef = src.getReagentAnnotationRef(i, q, r);
            dest.setReagentAnnotationRef(reagentAnnotationRef, i, q, r);
        }
      }
    }
  }

  void
  convertTagAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type tagAnnotationCount = 0;
      tagAnnotationCount = src.getTagAnnotationCount();
    for (index_type i = 0; i < tagAnnotationCount; ++i)
      {
        std::string id = src.getTagAnnotationID(i);
        dest.setTagAnnotationID(id, i);

        std::string description = src.getTagAnnotationDescription(i);
        dest.setTagAnnotationDescription(description, i);

        std::string ns = src.getTagAnnotationNamespace(i);
        dest.setTagAnnotationNamespace(ns, i);

        std::string value = src.getTagAnnotationValue(i);
        dest.setTagAnnotationValue(value, i);

        std::string annotator = src.getTagAnnotationAnnotator(i);
        dest.setTagAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getTagAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getTagAnnotationAnnotationRef(i, a);
          dest.setTagAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertTermAnnotations(const MetadataRetrieve& src,
                         MetadataStore&          dest)
  {
    index_type termAnnotationCount = 0;
      termAnnotationCount = src.getTermAnnotationCount();
    for (index_type i = 0; i < termAnnotationCount; ++i)
      {
        std::string id = src.getTermAnnotationID(i);
        dest.setTermAnnotationID(id, i);

        std::string description = src.getTermAnnotationDescription(i);
        dest.setTermAnnotationDescription(description, i);

        std::string ns = src.getTermAnnotationNamespace(i);
        dest.setTermAnnotationNamespace(ns, i);

        std::string value = src.getTermAnnotationValue(i);
        dest.setTermAnnotationValue(value, i);

        std::string annotator = src.getTermAnnotationAnnotator(i);
        dest.setTermAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getTermAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getTermAnnotationAnnotationRef(i, a);
          dest.setTermAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertTimestampAnnotations(const MetadataRetrieve& src,
                              MetadataStore&          dest)
  {
    index_type timestampAnnotationCount = 0;
      timestampAnnotationCount = src.getTimestampAnnotationCount();
    for (index_type i = 0; i < timestampAnnotationCount; ++i)
      {
        std::string id = src.getTimestampAnnotationID(i);
        dest.setTimestampAnnotationID(id, i);

        std::string description = src.getTimestampAnnotationDescription(i);
        dest.setTimestampAnnotationDescription(description, i);

        std::string ns = src.getTimestampAnnotationNamespace(i);
        dest.setTimestampAnnotationNamespace(ns, i);

        Timestamp value = src.getTimestampAnnotationValue(i);
        dest.setTimestampAnnotationValue(value, i);

        std::string annotator = src.getTimestampAnnotationAnnotator(i);
        dest.setTimestampAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getTimestampAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getTimestampAnnotationAnnotationRef(i, a);
          dest.setTimestampAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertXMLAnnotations(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    index_type xmlAnnotationCount = 0;
      xmlAnnotationCount = src.getXMLAnnotationCount();
    for (index_type i = 0; i < xmlAnnotationCount; ++i)
      {
        std::string id = src.getXMLAnnotationID(i);
        dest.setXMLAnnotationID(id, i);

        std::string description = src.getXMLAnnotationDescription(i);
        dest.setXMLAnnotationDescription(description, i);

        std::string ns = src.getXMLAnnotationNamespace(i);
        dest.setXMLAnnotationNamespace(ns, i);

        std::string value = src.getXMLAnnotationValue(i);
        dest.setXMLAnnotationValue(value, i);

        std::string annotator = src.getXMLAnnotationAnnotator(i);
        dest.setXMLAnnotationAnnotator(annotator, i);

      index_type annotationRefCount = 0;
        annotationRefCount = src.getXMLAnnotationAnnotationCount(i);
      for (index_type a = 0; a < annotationRefCount; ++a)
        {
          std::string id = src.getXMLAnnotationAnnotationRef(i, a);
          dest.setXMLAnnotationAnnotationRef(id, i, a);
      }
    }
  }

  void
  convertRootAttributes(const MetadataRetrieve& src,
                        MetadataStore&          dest)
  {
    std::string uuid = src.getUUID();
    dest.setUUID(uuid);

    std::string rightsHeld = src.getRightsRightsHeld();
    dest.setRightsRightsHeld(rightsHeld);

    std::string rightsHolder = src.getRightsRightsHolder();
    dest.setRightsRightsHolder(rightsHolder);

    std::string metadataFile = src.getBinaryOnlyMetadataFile();
    dest.setBinaryOnlyMetadataFile(metadataFile);

    std::string buuid = src.getBinaryOnlyUUID();
        dest.setBinaryOnlyUUID(buuid);
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
