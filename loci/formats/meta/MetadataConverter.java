//
// MetadataConverter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Mar 20, 2008 12:34:36 PM CDT
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
      dest.setExperimenterNodeID(src.getExperimenterNodeID(experimenterIndex), experimenterIndex);
      dest.setExperimenterFirstName(src.getExperimenterFirstName(experimenterIndex), experimenterIndex);
      dest.setExperimenterLastName(src.getExperimenterLastName(experimenterIndex), experimenterIndex);
      dest.setExperimenterEmail(src.getExperimenterEmail(experimenterIndex), experimenterIndex);
      dest.setExperimenterInstitution(src.getExperimenterInstitution(experimenterIndex), experimenterIndex);
      dest.setExperimenterDataDirectory(src.getExperimenterDataDirectory(experimenterIndex), experimenterIndex);
    }
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
      dest.setImageNodeID(src.getImageNodeID(imageIndex), imageIndex);
      dest.setImageName(src.getImageName(imageIndex), imageIndex);
      dest.setImageCreationDate(src.getImageCreationDate(imageIndex), imageIndex);
      dest.setImageDescription(src.getImageDescription(imageIndex), imageIndex);
      dest.setImagingEnvironmentTemperature(src.getImagingEnvironmentTemperature(imageIndex), imageIndex);
      dest.setImagingEnvironmentAirPressure(src.getImagingEnvironmentAirPressure(imageIndex), imageIndex);
      dest.setImagingEnvironmentHumidity(src.getImagingEnvironmentHumidity(imageIndex), imageIndex);
      dest.setImagingEnvironmentCO2Percent(src.getImagingEnvironmentCO2Percent(imageIndex), imageIndex);
      dest.setDisplayOptionsNodeID(src.getDisplayOptionsNodeID(imageIndex), imageIndex);
      dest.setDisplayOptionsZoom(src.getDisplayOptionsZoom(imageIndex), imageIndex);
      dest.setDisplayOptionsProjectionZStart(src.getDisplayOptionsProjectionZStart(imageIndex), imageIndex);
      dest.setDisplayOptionsProjectionZStop(src.getDisplayOptionsProjectionZStop(imageIndex), imageIndex);
      dest.setDisplayOptionsTimeTStart(src.getDisplayOptionsTimeTStart(imageIndex), imageIndex);
      dest.setDisplayOptionsTimeTStop(src.getDisplayOptionsTimeTStop(imageIndex), imageIndex);
      dest.setStageLabelName(src.getStageLabelName(imageIndex), imageIndex);
      dest.setStageLabelX(src.getStageLabelX(imageIndex), imageIndex);
      dest.setStageLabelY(src.getStageLabelY(imageIndex), imageIndex);
      dest.setStageLabelZ(src.getStageLabelZ(imageIndex), imageIndex);
      int logicalChannelCount = src.getLogicalChannelCount(imageIndex);
      for (int logicalChannelIndex=0; logicalChannelIndex<logicalChannelCount; logicalChannelIndex++) {
        dest.setLogicalChannelNodeID(src.getLogicalChannelNodeID(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelName(src.getLogicalChannelName(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelSamplesPerPixel(src.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelIlluminationType(src.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPinholeSize(src.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPhotometricInterpretation(src.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelMode(src.getLogicalChannelMode(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelContrastMethod(src.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelExWave(src.getLogicalChannelExWave(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelEmWave(src.getLogicalChannelEmWave(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelFluor(src.getLogicalChannelFluor(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelNdFilter(src.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLogicalChannelPockelCellSetting(src.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setDetectorSettingsDetector(src.getDetectorSettingsDetector(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setDetectorSettingsOffset(src.getDetectorSettingsOffset(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setDetectorSettingsGain(src.getDetectorSettingsGain(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLightSourceSettingsLightSource(src.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLightSourceSettingsAttenuation(src.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        dest.setLightSourceSettingsWavelength(src.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex), imageIndex, logicalChannelIndex);
        int channelComponentCount = src.getChannelComponentCount(imageIndex, logicalChannelIndex);
        for (int channelComponentIndex=0; channelComponentIndex<channelComponentCount; channelComponentIndex++) {
          dest.setChannelComponentIndex(src.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex), imageIndex, logicalChannelIndex, channelComponentIndex);
          dest.setChannelComponentColorDomain(src.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex), imageIndex, logicalChannelIndex, channelComponentIndex);
        }
      }
      int pixelsCount = src.getPixelsCount(imageIndex);
      for (int pixelsIndex=0; pixelsIndex<pixelsCount; pixelsIndex++) {
        dest.setPixelsNodeID(src.getPixelsNodeID(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeX(src.getPixelsSizeX(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeY(src.getPixelsSizeY(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeZ(src.getPixelsSizeZ(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeC(src.getPixelsSizeC(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsSizeT(src.getPixelsSizeT(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsPixelType(src.getPixelsPixelType(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsBigEndian(src.getPixelsBigEndian(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setPixelsDimensionOrder(src.getPixelsDimensionOrder(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsPhysicalSizeX(src.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsPhysicalSizeY(src.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsPhysicalSizeZ(src.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsTimeIncrement(src.getDimensionsTimeIncrement(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsWaveStart(src.getDimensionsWaveStart(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        dest.setDimensionsWaveIncrement(src.getDimensionsWaveIncrement(imageIndex, pixelsIndex), imageIndex, pixelsIndex);
        int planeCount = src.getPlaneCount(imageIndex, pixelsIndex);
        for (int planeIndex=0; planeIndex<planeCount; planeIndex++) {
          dest.setPlaneTheZ(src.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTheC(src.getPlaneTheC(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTheT(src.getPlaneTheT(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTimingDeltaT(src.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setPlaneTimingExposureTime(src.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionX(src.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionY(src.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
          dest.setStagePositionPositionZ(src.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex), imageIndex, pixelsIndex, planeIndex);
        }
      }
      int roiCount = src.getROICount(imageIndex);
      for (int roiIndex=0; roiIndex<roiCount; roiIndex++) {
        dest.setROINodeID(src.getROINodeID(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIX0(src.getROIX0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIY0(src.getROIY0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIX1(src.getROIX1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIY1(src.getROIY1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIZ0(src.getROIZ0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIZ1(src.getROIZ1(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIT0(src.getROIT0(imageIndex, roiIndex), imageIndex, roiIndex);
        dest.setROIT1(src.getROIT1(imageIndex, roiIndex), imageIndex, roiIndex);
      }
    }
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
      dest.setInstrumentNodeID(src.getInstrumentNodeID(instrumentIndex), instrumentIndex);
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
        dest.setDetectorNodeID(src.getDetectorNodeID(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorManufacturer(src.getDetectorManufacturer(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorModel(src.getDetectorModel(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorSerialNumber(src.getDetectorSerialNumber(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorType(src.getDetectorType(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorGain(src.getDetectorGain(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorVoltage(src.getDetectorVoltage(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
        dest.setDetectorOffset(src.getDetectorOffset(instrumentIndex, detectorIndex), instrumentIndex, detectorIndex);
      }
      int lightSourceCount = src.getLightSourceCount(instrumentIndex);
      for (int lightSourceIndex=0; lightSourceIndex<lightSourceCount; lightSourceIndex++) {
        dest.setLightSourceNodeID(src.getLightSourceNodeID(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceManufacturer(src.getLightSourceManufacturer(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceModel(src.getLightSourceModel(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLightSourceSerialNumber(src.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserType(src.getLaserType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserLaserMedium(src.getLaserLaserMedium(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserWavelength(src.getLaserWavelength(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserFrequencyMultiplication(src.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserTuneable(src.getLaserTuneable(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserPulse(src.getLaserPulse(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setLaserPower(src.getLaserPower(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setFilamentType(src.getFilamentType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setFilamentPower(src.getFilamentPower(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setArcType(src.getArcType(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
        dest.setArcPower(src.getArcPower(instrumentIndex, lightSourceIndex), instrumentIndex, lightSourceIndex);
      }
      int otfCount = src.getOTFCount(instrumentIndex);
      for (int otfIndex=0; otfIndex<otfCount; otfIndex++) {
        dest.setOTFNodeID(src.getOTFNodeID(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFSizeX(src.getOTFSizeX(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFSizeY(src.getOTFSizeY(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFPixelType(src.getOTFPixelType(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFPath(src.getOTFPath(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
        dest.setOTFOpticalAxisAveraged(src.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex), instrumentIndex, otfIndex);
      }
      int objectiveCount = src.getObjectiveCount(instrumentIndex);
      for (int objectiveIndex=0; objectiveIndex<objectiveCount; objectiveIndex++) {
        dest.setObjectiveNodeID(src.getObjectiveNodeID(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveManufacturer(src.getObjectiveManufacturer(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveModel(src.getObjectiveModel(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveSerialNumber(src.getObjectiveSerialNumber(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveCorrection(src.getObjectiveCorrection(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveImmersion(src.getObjectiveImmersion(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveLensNA(src.getObjectiveLensNA(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveNominalMagnification(src.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveCalibratedMagnification(src.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
        dest.setObjectiveWorkingDistance(src.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex), instrumentIndex, objectiveIndex);
      }
    }
  }

}
