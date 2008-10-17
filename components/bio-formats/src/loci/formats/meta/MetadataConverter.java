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
 * Created by curtis via MetadataAutogen on Oct 17, 2008 4:50:42 PM CDT
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
    int experimentCount = src.getExperimentCount();
    for (int experimentIndex=0; experimentIndex<experimentCount; experimentIndex++) {
      String experimentDescriptionValue = src.getExperimentDescription(experimentIndex);
      if (experimentDescriptionValue != null) dest.setExperimentDescription(experimentDescriptionValue, experimentIndex);
      String experimentIDValue = src.getExperimentID(experimentIndex);
      if (experimentIDValue != null) dest.setExperimentID(experimentIDValue, experimentIndex);
      String experimentTypeValue = src.getExperimentType(experimentIndex);
      if (experimentTypeValue != null) dest.setExperimentType(experimentTypeValue, experimentIndex);
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
      int groupRefCount = src.getGroupRefCount(experimenterIndex);
      for (int groupRefIndex=0; groupRefIndex<groupRefCount; groupRefIndex++) {
        String experimenterMembershipGroupValue = src.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex);
        if (experimenterMembershipGroupValue != null) dest.setExperimenterMembershipGroup(experimenterMembershipGroupValue, experimenterIndex, groupRefIndex);
      }
    }
    int imageCount = src.getImageCount();
    for (int imageIndex=0; imageIndex<imageCount; imageIndex++) {
      String imageCreationDateValue = src.getImageCreationDate(imageIndex);
      if (imageCreationDateValue != null) dest.setImageCreationDate(imageCreationDateValue, imageIndex);
      String imageDescriptionValue = src.getImageDescription(imageIndex);
      if (imageDescriptionValue != null) dest.setImageDescription(imageDescriptionValue, imageIndex);
      String imageIDValue = src.getImageID(imageIndex);
      if (imageIDValue != null) dest.setImageID(imageIDValue, imageIndex);
      String imageInstrumentRefValue = src.getImageInstrumentRef(imageIndex);
      if (imageInstrumentRefValue != null) dest.setImageInstrumentRef(imageInstrumentRefValue, imageIndex);
      String imageNameValue = src.getImageName(imageIndex);
      if (imageNameValue != null) dest.setImageName(imageNameValue, imageIndex);
      String displayOptionsIDValue = src.getDisplayOptionsID(imageIndex);
      if (displayOptionsIDValue != null) dest.setDisplayOptionsID(displayOptionsIDValue, imageIndex);
      Float displayOptionsZoomValue = src.getDisplayOptionsZoom(imageIndex);
      if (displayOptionsZoomValue != null) dest.setDisplayOptionsZoom(displayOptionsZoomValue, imageIndex);
      Integer displayOptionsProjectionZStartValue = src.getDisplayOptionsProjectionZStart(imageIndex);
      if (displayOptionsProjectionZStartValue != null) dest.setDisplayOptionsProjectionZStart(displayOptionsProjectionZStartValue, imageIndex);
      Integer displayOptionsProjectionZStopValue = src.getDisplayOptionsProjectionZStop(imageIndex);
      if (displayOptionsProjectionZStopValue != null) dest.setDisplayOptionsProjectionZStop(displayOptionsProjectionZStopValue, imageIndex);
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
      }
        Integer displayOptionsTimeTStartValue = src.getDisplayOptionsTimeTStart(imageIndex);
        if (displayOptionsTimeTStartValue != null) dest.setDisplayOptionsTimeTStart(displayOptionsTimeTStartValue, imageIndex);
        Integer displayOptionsTimeTStopValue = src.getDisplayOptionsTimeTStop(imageIndex);
        if (displayOptionsTimeTStopValue != null) dest.setDisplayOptionsTimeTStop(displayOptionsTimeTStopValue, imageIndex);
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
        Integer logicalChannelEmWaveValue = src.getLogicalChannelEmWave(imageIndex, logicalChannelIndex);
        if (logicalChannelEmWaveValue != null) dest.setLogicalChannelEmWave(logicalChannelEmWaveValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelExWaveValue = src.getLogicalChannelExWave(imageIndex, logicalChannelIndex);
        if (logicalChannelExWaveValue != null) dest.setLogicalChannelExWave(logicalChannelExWaveValue, imageIndex, logicalChannelIndex);
        String logicalChannelFluorValue = src.getLogicalChannelFluor(imageIndex, logicalChannelIndex);
        if (logicalChannelFluorValue != null) dest.setLogicalChannelFluor(logicalChannelFluorValue, imageIndex, logicalChannelIndex);
        String logicalChannelIDValue = src.getLogicalChannelID(imageIndex, logicalChannelIndex);
        if (logicalChannelIDValue != null) dest.setLogicalChannelID(logicalChannelIDValue, imageIndex, logicalChannelIndex);
        String logicalChannelIlluminationTypeValue = src.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex);
        if (logicalChannelIlluminationTypeValue != null) dest.setLogicalChannelIlluminationType(logicalChannelIlluminationTypeValue, imageIndex, logicalChannelIndex);
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
        Integer logicalChannelPinholeSizeValue = src.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
        if (logicalChannelPinholeSizeValue != null) dest.setLogicalChannelPinholeSize(logicalChannelPinholeSizeValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelPockelCellSettingValue = src.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex);
        if (logicalChannelPockelCellSettingValue != null) dest.setLogicalChannelPockelCellSetting(logicalChannelPockelCellSettingValue, imageIndex, logicalChannelIndex);
        Integer logicalChannelSamplesPerPixelValue = src.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex);
        if (logicalChannelSamplesPerPixelValue != null) dest.setLogicalChannelSamplesPerPixel(logicalChannelSamplesPerPixelValue, imageIndex, logicalChannelIndex);
        int channelComponentCount = src.getChannelComponentCount(imageIndex, logicalChannelIndex);
        for (int channelComponentIndex=0; channelComponentIndex<channelComponentCount; channelComponentIndex++) {
          String channelComponentColorDomainValue = src.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex);
          if (channelComponentColorDomainValue != null) dest.setChannelComponentColorDomain(channelComponentColorDomainValue, imageIndex, logicalChannelIndex, channelComponentIndex);
          Integer channelComponentIndexValue = src.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex);
          if (channelComponentIndexValue != null) dest.setChannelComponentIndex(channelComponentIndexValue, imageIndex, logicalChannelIndex, channelComponentIndex);
        }
          String detectorSettingsDetectorValue = src.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
          if (detectorSettingsDetectorValue != null) dest.setDetectorSettingsDetector(detectorSettingsDetectorValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsGainValue = src.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
          if (detectorSettingsGainValue != null) dest.setDetectorSettingsGain(detectorSettingsGainValue, imageIndex, logicalChannelIndex);
          Float detectorSettingsOffsetValue = src.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
          if (detectorSettingsOffsetValue != null) dest.setDetectorSettingsOffset(detectorSettingsOffsetValue, imageIndex, logicalChannelIndex);
          Float lightSourceSettingsAttenuationValue = src.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsAttenuationValue != null) dest.setLightSourceSettingsAttenuation(lightSourceSettingsAttenuationValue, imageIndex, logicalChannelIndex);
          String lightSourceSettingsLightSourceValue = src.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsLightSourceValue != null) dest.setLightSourceSettingsLightSource(lightSourceSettingsLightSourceValue, imageIndex, logicalChannelIndex);
          Integer lightSourceSettingsWavelengthValue = src.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex);
          if (lightSourceSettingsWavelengthValue != null) dest.setLightSourceSettingsWavelength(lightSourceSettingsWavelengthValue, imageIndex, logicalChannelIndex);
      }
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
        String stageLabelNameValue = src.getStageLabelName(imageIndex);
        if (stageLabelNameValue != null) dest.setStageLabelName(stageLabelNameValue, imageIndex);
        Float stageLabelXValue = src.getStageLabelX(imageIndex);
        if (stageLabelXValue != null) dest.setStageLabelX(stageLabelXValue, imageIndex);
        Float stageLabelYValue = src.getStageLabelY(imageIndex);
        if (stageLabelYValue != null) dest.setStageLabelY(stageLabelYValue, imageIndex);
        Float stageLabelZValue = src.getStageLabelZ(imageIndex);
        if (stageLabelZValue != null) dest.setStageLabelZ(stageLabelZValue, imageIndex);
    }
    int instrumentCount = src.getInstrumentCount();
    for (int instrumentIndex=0; instrumentIndex<instrumentCount; instrumentIndex++) {
      String instrumentIDValue = src.getInstrumentID(instrumentIndex);
      if (instrumentIDValue != null) dest.setInstrumentID(instrumentIDValue, instrumentIndex);
      int detectorCount = src.getDetectorCount(instrumentIndex);
      for (int detectorIndex=0; detectorIndex<detectorCount; detectorIndex++) {
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
        String laserPulseValue = src.getLaserPulse(instrumentIndex, lightSourceIndex);
        if (laserPulseValue != null) dest.setLaserPulse(laserPulseValue, instrumentIndex, lightSourceIndex);
        Boolean laserTuneableValue = src.getLaserTuneable(instrumentIndex, lightSourceIndex);
        if (laserTuneableValue != null) dest.setLaserTuneable(laserTuneableValue, instrumentIndex, lightSourceIndex);
        String laserTypeValue = src.getLaserType(instrumentIndex, lightSourceIndex);
        if (laserTypeValue != null) dest.setLaserType(laserTypeValue, instrumentIndex, lightSourceIndex);
        Integer laserWavelengthValue = src.getLaserWavelength(instrumentIndex, lightSourceIndex);
        if (laserWavelengthValue != null) dest.setLaserWavelength(laserWavelengthValue, instrumentIndex, lightSourceIndex);
      }
      int otfCount = src.getOTFCount(instrumentIndex);
      for (int otfIndex=0; otfIndex<otfCount; otfIndex++) {
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
      String plateDescriptionValue = src.getPlateDescription(plateIndex);
      if (plateDescriptionValue != null) dest.setPlateDescription(plateDescriptionValue, plateIndex);
      String plateExternalIdentifierValue = src.getPlateExternalIdentifier(plateIndex);
      if (plateExternalIdentifierValue != null) dest.setPlateExternalIdentifier(plateExternalIdentifierValue, plateIndex);
      String plateIDValue = src.getPlateID(plateIndex);
      if (plateIDValue != null) dest.setPlateID(plateIDValue, plateIndex);
      String plateNameValue = src.getPlateName(plateIndex);
      if (plateNameValue != null) dest.setPlateName(plateNameValue, plateIndex);
      String plateStatusValue = src.getPlateStatus(plateIndex);
      if (plateStatusValue != null) dest.setPlateStatus(plateStatusValue, plateIndex);
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
        Integer wellRowValue = src.getWellRow(plateIndex, wellIndex);
        if (wellRowValue != null) dest.setWellRow(wellRowValue, plateIndex, wellIndex);
        String wellTypeValue = src.getWellType(plateIndex, wellIndex);
        if (wellTypeValue != null) dest.setWellType(wellTypeValue, plateIndex, wellIndex);
        int wellSampleCount = src.getWellSampleCount(plateIndex, wellIndex);
        for (int wellSampleIndex=0; wellSampleIndex<wellSampleCount; wellSampleIndex++) {
          String wellSampleIDValue = src.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
          if (wellSampleIDValue != null) dest.setWellSampleID(wellSampleIDValue, plateIndex, wellIndex, wellSampleIndex);
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
    int screenCount = src.getScreenCount();
    for (int screenIndex=0; screenIndex<screenCount; screenIndex++) {
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
      String screenTypeValue = src.getScreenType(screenIndex);
      if (screenTypeValue != null) dest.setScreenType(screenTypeValue, screenIndex);
      int plateRefCount = src.getPlateRefCount(screenIndex);
      for (int plateRefIndex=0; plateRefIndex<plateRefCount; plateRefIndex++) {
        String plateRefIDValue = src.getPlateRefID(screenIndex, plateRefIndex);
        if (plateRefIDValue != null) dest.setPlateRefID(plateRefIDValue, screenIndex, plateRefIndex);
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
      }
    }
  }

}
