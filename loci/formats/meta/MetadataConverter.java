//
// MetadataConverter.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
 * Created by curtis via MetadataAutogen on May 23, 2008 4:44:30 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A utility class containing a method for piping a source
 * {@link MetadataRetrieve} object into a destination {@link MetadataStore}.
 * This technique allows non-OME-XML-based metadata stores (such as OMERO's
 * metadata store implementation) to be easily converted to another
 * implementation, particularly {@link loci.formats.ome.OMEXMLMetadata},
 * which allows generation of OME-XML from OMERO metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/meta/MetadataConverter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/meta/MetadataConverter.java">SVN</a></dd></dl>
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
    int experimenterCount = src.getExperimenterCount();
    for (int experimenterIndex=0; experimenterIndex<experimenterCount; experimenterIndex++) {
      dest.setExperimenterEmail(src.getExperimenterEmail(experimenterIndex), experimenterIndex);
      dest.setExperimenterFirstName(src.getExperimenterFirstName(experimenterIndex), experimenterIndex);
      dest.setExperimenterID(src.getExperimenterID(experimenterIndex), experimenterIndex);
      dest.setExperimenterInstitution(src.getExperimenterInstitution(experimenterIndex), experimenterIndex);
      dest.setExperimenterLastName(src.getExperimenterLastName(experimenterIndex), experimenterIndex);
    }
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
      dest.setImageCreationDate(src.getImageCreationDate(imageIndex), imageIndex);
      dest.setImageDescription(src.getImageDescription(imageIndex), imageIndex);
      dest.setImageID(src.getImageID(imageIndex), imageIndex);
      dest.setImageName(src.getImageName(imageIndex), imageIndex);
      dest.setDisplayOptionsID(src.getDisplayOptionsID(imageIndex), imageIndex);
      dest.setDisplayOptionsZoom(src.getDisplayOptionsZoom(imageIndex), imageIndex);
      dest.setDisplayOptionsProjectionZStart(src.getDisplayOptionsProjectionZStart(imageIndex), imageIndex);
      dest.setDisplayOptionsProjectionZStop(src.getDisplayOptionsProjectionZStop(imageIndex), imageIndex);
      int roiCount = src.getROICount(imageIndex);
      for (int roiIndex=0; roiIndex<roiCount; roiIndex++) {
        dest.setROIID(src.getROIID(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIT0(src.getROIT0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIT1(src.getROIT1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIX0(src.getROIX0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIX1(src.getROIX1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIY0(src.getROIY0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIY1(src.getROIY1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIZ0(src.getROIZ0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIZ1(src.getROIZ1(imageIndex, roiIndex), imageIndex, roiIndex);
      }
        dest.setDisplayOptionsTimeTStart(src.getDisplayOptionsTimeTStart(imageIndex), imageIndex);
        dest.setDisplayOptionsTimeTStop(src.getDisplayOptionsTimeTStop(imageIndex), imageIndex);
        dest.setImagingEnvironmentAirPressure(src.getImagingEnvironmentAirPressure(imageIndex), imageIndex);
        dest.setImagingEnvironmentCO2Percent(src.getImagingEnvironmentCO2Percent(imageIndex), imageIndex);
        dest.setImagingEnvironmentHumidity(src.getImagingEnvironmentHumidity(imageIndex), imageIndex);
        dest.setImagingEnvironmentTemperature(src.getImagingEnvironmentTemperature(imageIndex), imageIndex);
      int logicalChannelCount = src.getLogicalChannelCount(imageIndex);
      for (int logicalChannelIndex=0; logicalChannelIndex<logicalChannelCount; logicalChannelIndex++) {
        dest.setLogicalChannelContrastMethod(src.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelEmWave(src.getLogicalChannelEmWave(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelExWave(src.getLogicalChannelExWave(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelFluor(src.getLogicalChannelFluor(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelID(src.getLogicalChannelID(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelIlluminationType(src.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelMode(src.getLogicalChannelMode(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelName(src.getLogicalChannelName(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelNdFilter(src.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPhotometricInterpretation(src.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPinholeSize(src.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPockelCellSetting(src.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelSamplesPerPixel(src.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        int channelComponentCount = src.getChannelComponentCount(imageIndex, logicalChannelIndex);
        for (int channelComponentIndex=0; channelComponentIndex<channelComponentCount; channelComponentIndex++) {
          dest.setChannelComponentColorDomain(src.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex), imageIndex, logicalChannelIndex, channelComponentIndex);
          dest.setChannelComponentIndex(src.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex), imageIndex, logicalChannelIndex, channelComponentIndex);
        }
          dest.setDetectorSettingsDetector(src.getDetectorSettingsDetector(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
          dest.setDetectorSettingsGain(src.getDetectorSettingsGain(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
          dest.setDetectorSettingsOffset(src.getDetectorSettingsOffset(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
          dest.setLightSourceSettingsAttenuation(src.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
          dest.setLightSourceSettingsLightSource(src.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
          dest.setLightSourceSettingsWavelength(src.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
      }
      int pixelsCount = src.getPixelsCount(imageIndex);
      for (int pixelsIndex=0; pixelsIndex<pixelsCount; pixelsIndex++) {
        dest.setDimensionsPhysicalSizeX(src.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsPhysicalSizeY(src.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsPhysicalSizeZ(src.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsTimeIncrement(src.getDimensionsTimeIncrement(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsWaveIncrement(src.getDimensionsWaveIncrement(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsWaveStart(src.getDimensionsWaveStart(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsBigEndian(src.getPixelsBigEndian(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsDimensionOrder(src.getPixelsDimensionOrder(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsID(src.getPixelsID(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsPixelType(src.getPixelsPixelType(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeC(src.getPixelsSizeC(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeT(src.getPixelsSizeT(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeX(src.getPixelsSizeX(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeY(src.getPixelsSizeY(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeZ(src.getPixelsSizeZ(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        int planeCount = src.getPlaneCount(imageIndex, pixelsIndex);
        for (int planeIndex=0; planeIndex<planeCount; planeIndex++) {
          dest.setPlaneTheC(src.getPlaneTheC(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTheT(src.getPlaneTheT(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTheZ(src.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTimingDeltaT(src.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTimingExposureTime(src.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionX(src.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionY(src.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionZ(src.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
        }
        int tiffDataCount = src.getTiffDataCount(imageIndex, pixelsIndex);
        for (int tiffDataIndex=0; tiffDataIndex<tiffDataCount; tiffDataIndex++) {
          dest.setTiffDataFileName(src.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataFirstC(src.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataFirstT(src.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataFirstZ(src.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataIFD(src.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataNumPlanes(src.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
          dest.setTiffDataUUID(src.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex), imageIndex, pixelsIndex, tiffDataIndex);
        }
      }
        dest.setStageLabelName(src.getStageLabelName(imageIndex), imageIndex);
        dest.setStageLabelX(src.getStageLabelX(imageIndex), imageIndex);
        dest.setStageLabelY(src.getStageLabelY(imageIndex), imageIndex);
        dest.setStageLabelZ(src.getStageLabelZ(imageIndex), imageIndex);
    }
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
      dest.setInstrumentID(src.getInstrumentID(instrumentIndex), instrumentIndex);
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
        dest.setDetectorGain(src.getDetectorGain(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorID(src.getDetectorID(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorManufacturer(src.getDetectorManufacturer(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorModel(src.getDetectorModel(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorOffset(src.getDetectorOffset(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorSerialNumber(src.getDetectorSerialNumber(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorType(src.getDetectorType(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorVoltage(src.getDetectorVoltage(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
      }
      int lightSourceCount = src.getLightSourceCount(instrumentIndex);
      for (int lightSourceIndex=0; lightSourceIndex<lightSourceCount; lightSourceIndex++) {
        dest.setLightSourceID(src.getLightSourceID(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceManufacturer(src.getLightSourceManufacturer(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceModel(src.getLightSourceModel(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourcePower(src.getLightSourcePower(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceSerialNumber(src.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setArcType(src.getArcType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setFilamentType(src.getFilamentType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserFrequencyMultiplication(src.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserLaserMedium(src.getLaserLaserMedium(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserPulse(src.getLaserPulse(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserTuneable(src.getLaserTuneable(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserType(src.getLaserType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserWavelength(src.getLaserWavelength(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
      }
      int otfCount = src.getOTFCount(instrumentIndex);
      for (int otfIndex=0; otfIndex<otfCount; otfIndex++) {
        dest.setOTFID(src.getOTFID(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFOpticalAxisAveraged(src.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFPixelType(src.getOTFPixelType(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFSizeX(src.getOTFSizeX(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFSizeY(src.getOTFSizeY(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
      }
      int objectiveCount = src.getObjectiveCount(instrumentIndex);
      for (int objectiveIndex=0; objectiveIndex<objectiveCount; objectiveIndex++) {
        dest.setObjectiveCalibratedMagnification(src.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveCorrection(src.getObjectiveCorrection(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveID(src.getObjectiveID(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveImmersion(src.getObjectiveImmersion(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveLensNA(src.getObjectiveLensNA(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveManufacturer(src.getObjectiveManufacturer(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveModel(src.getObjectiveModel(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveNominalMagnification(src.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveSerialNumber(src.getObjectiveSerialNumber(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveWorkingDistance(src.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
      }
    }
    int plateCount = src.getPlateCount();
    for (int plateIndex=0; plateIndex<plateCount; plateIndex++) {
      dest.setPlateDescription(src.getPlateDescription(plateIndex), plateIndex);
      dest.setPlateExternalIdentifier(src.getPlateExternalIdentifier(plateIndex), plateIndex);
      dest.setPlateID(src.getPlateID(plateIndex), plateIndex);
      dest.setPlateName(src.getPlateName(plateIndex), plateIndex);
      dest.setPlateStatus(src.getPlateStatus(plateIndex), plateIndex);
    }
    int screenCount = src.getScreenCount();
    for (int screenIndex=0; screenIndex<screenCount; screenIndex++) {
      dest.setScreenID(src.getScreenID(screenIndex), screenIndex);
      dest.setScreenName(src.getScreenName(screenIndex), screenIndex);
      dest.setScreenProtocolDescription(src.getScreenProtocolDescription(screenIndex), screenIndex);
      dest.setScreenProtocolIdentifier(src.getScreenProtocolIdentifier(screenIndex), screenIndex);
      dest.setScreenReagentSetDescription(src.getScreenReagentSetDescription(screenIndex), screenIndex);
      dest.setScreenType(src.getScreenType(screenIndex), screenIndex);
      int plateRefCount = src.getPlateRefCount(screenIndex);
      for (int plateRefIndex=0; plateRefIndex<plateRefCount; plateRefIndex++) {
        dest.setPlateRefID(src.getPlateRefID(screenIndex, plateRefIndex), screenIndex, plateRefIndex);
      }
      int reagentCount = src.getReagentCount(screenIndex);
      for (int reagentIndex=0; reagentIndex<reagentCount; reagentIndex++) {
        dest.setReagentDescription(src.getReagentDescription(screenIndex, reagentIndex), screenIndex, reagentIndex);
        dest.setReagentID(src.getReagentID(screenIndex, reagentIndex), screenIndex, reagentIndex);
        dest.setReagentName(src.getReagentName(screenIndex, reagentIndex), screenIndex, reagentIndex);
        dest.setReagentReagentIdentifier(src.getReagentReagentIdentifier(screenIndex, reagentIndex), screenIndex, reagentIndex);
      }
      int screenAcquisitionCount = src.getScreenAcquisitionCount(screenIndex);
      for (int screenAcquisitionIndex=0; screenAcquisitionIndex<screenAcquisitionCount; screenAcquisitionIndex++) {
        dest.setScreenAcquisitionEndTime(src.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex), screenIndex, screenAcquisitionIndex);
        dest.setScreenAcquisitionID(src.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex), screenIndex, screenAcquisitionIndex);
        dest.setScreenAcquisitionStartTime(src.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex), screenIndex, screenAcquisitionIndex);
      }
    }
    int wellCount = src.getWellCount();
    for (int wellIndex=0; wellIndex<wellCount; wellIndex++) {
      dest.setWellColumn(src.getWellColumn(wellIndex), wellIndex);
      dest.setWellExternalDescription(src.getWellExternalDescription(wellIndex), wellIndex);
      dest.setWellExternalIdentifier(src.getWellExternalIdentifier(wellIndex), wellIndex);
      dest.setWellID(src.getWellID(wellIndex), wellIndex);
      dest.setWellRow(src.getWellRow(wellIndex), wellIndex);
      dest.setWellType(src.getWellType(wellIndex), wellIndex);
      int wellSampleCount = src.getWellSampleCount(wellIndex);
      for (int wellSampleIndex=0; wellSampleIndex<wellSampleCount; wellSampleIndex++) {
        dest.setWellSampleID(src.getWellSampleID(wellIndex, wellSampleIndex), wellIndex, wellSampleIndex);
        dest.setWellSampleIndex(src.getWellSampleIndex(wellIndex, wellSampleIndex), wellIndex, wellSampleIndex);
        dest.setWellSamplePosX(src.getWellSamplePosX(wellIndex, wellSampleIndex), wellIndex, wellSampleIndex);
        dest.setWellSamplePosY(src.getWellSamplePosY(wellIndex, wellSampleIndex), wellIndex, wellSampleIndex);
        dest.setWellSampleTimepoint(src.getWellSampleTimepoint(wellIndex, wellSampleIndex), wellIndex, wellSampleIndex);
      }
    }
  }

}
