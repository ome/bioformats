//
// FilterMetadata.vm
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

import loci.formats.DataTools;

/**
 * An implementation of {@link MetadataStore} that removes unprintable
 * characters from metadata values before storing them in a delegate
 * MetadataStore.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/meta/FilterMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/meta/FilterMetadata.java">SVN</a></dd></dl>
 *
 */
public class FilterMetadata implements MetadataStore {

  // -- Fields --

  private MetadataStore delegate;
  private boolean filter;

  // -- Constructor --

  public FilterMetadata(MetadataStore delegate, boolean filter) {
    this.delegate = delegate;
    this.filter = filter;
  }

  // -- MetadataStore API methods --

  public void createRoot() {
    delegate.createRoot();
  }

  public Object getRoot() {
    return delegate.getRoot();
  }

  public void setRoot(Object root) {
    delegate.setRoot(root);
  }


  // -- Image property storage -

  /* @see MetadataStore#setImageNodeID(String, int) */
  public void setImageNodeID(String nodeID, int imageIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setImageNodeID(temp, imageIndex);
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    String temp = filter ? DataTools.sanitize(name) : name;
    delegate.setImageName(temp, imageIndex);
  }

  /* @see MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    String temp = filter ? DataTools.sanitize(creationDate) : creationDate;
    delegate.setImageCreationDate(temp, imageIndex);
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    String temp = filter ? DataTools.sanitize(description) : description;
    delegate.setImageDescription(temp, imageIndex);
  }

  // -- Pixels property storage -

  /* @see MetadataStore#setPixelsNodeID(String, int, int) */
  public void setPixelsNodeID(String nodeID, int imageIndex, int pixelsIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setPixelsNodeID(temp, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    delegate.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    delegate.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    delegate.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    delegate.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    delegate.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    String temp = filter ? DataTools.sanitize(pixelType) : pixelType;
    delegate.setPixelsPixelType(temp, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    delegate.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    String temp = filter ? DataTools.sanitize(dimensionOrder) : dimensionOrder;
    delegate.setPixelsDimensionOrder(temp, imageIndex, pixelsIndex);
  }

  // -- Dimensions property storage -

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    delegate.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  // -- ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    delegate.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    delegate.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    delegate.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    delegate.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  // -- Plane property storage -

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  // -- PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // -- StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    delegate.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannelNodeID(String, int, int) */
  public void setLogicalChannelNodeID(String nodeID, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setLogicalChannelNodeID(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(name) : name;
    delegate.setLogicalChannelName(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(illuminationType) : illuminationType;
    delegate.setLogicalChannelIlluminationType(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(photometricInterpretation) : photometricInterpretation;
    delegate.setLogicalChannelPhotometricInterpretation(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(mode) : mode;
    delegate.setLogicalChannelMode(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(contrastMethod) : contrastMethod;
    delegate.setLogicalChannelContrastMethod(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    String temp = filter ? DataTools.sanitize(fluor) : fluor;
    delegate.setLogicalChannelFluor(temp, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    delegate.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  // -- ChannelComponent property storage -

  /* @see MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    delegate.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  /* @see MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    String temp = filter ? DataTools.sanitize(colorDomain) : colorDomain;
    delegate.setChannelComponentColorDomain(temp, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // -- DisplayOptions property storage -

  /* @see MetadataStore#setDisplayOptionsNodeID(String, int) */
  public void setDisplayOptionsNodeID(String nodeID, int imageIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setDisplayOptionsNodeID(temp, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    delegate.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // -- DisplayOptionsProjection property storage -

  /* @see MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
    delegate.setDisplayOptionsProjectionZStart(zStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
    delegate.setDisplayOptionsProjectionZStop(zStop, imageIndex);
  }

  // -- DisplayOptionsTime property storage -

  /* @see MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
    delegate.setDisplayOptionsTimeTStart(tStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
    delegate.setDisplayOptionsTimeTStop(tStop, imageIndex);
  }

  // -- ROI property storage -

  /* @see MetadataStore#setROINodeID(String, int, int) */
  public void setROINodeID(String nodeID, int imageIndex, int roiIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setROINodeID(temp, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    delegate.setROIX0(x0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    delegate.setROIY0(y0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    delegate.setROIX1(x1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    delegate.setROIY1(y1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    delegate.setROIZ0(z0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    delegate.setROIZ1(z1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    delegate.setROIT0(t0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    delegate.setROIT1(t1, imageIndex, roiIndex);
  }

  // -- DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsDetector(Object, int, int) */
  public void setDetectorSettingsDetector(Object detector, int imageIndex, int logicalChannelIndex) {
    delegate.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    delegate.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    delegate.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  // -- LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettingsLightSource(Object, int, int) */
  public void setLightSourceSettingsLightSource(Object lightSource, int imageIndex, int logicalChannelIndex) {
    delegate.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    delegate.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    delegate.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // -- StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    String temp = filter ? DataTools.sanitize(name) : name;
    delegate.setStageLabelName(temp, imageIndex);
  }

  /* @see MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    delegate.setStageLabelX(x, imageIndex);
  }

  /* @see MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    delegate.setStageLabelY(y, imageIndex);
  }

  /* @see MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    delegate.setStageLabelZ(z, imageIndex);
  }

  // -- Instrument property storage -

  /* @see MetadataStore#setInstrumentNodeID(String, int) */
  public void setInstrumentNodeID(String nodeID, int instrumentIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setInstrumentNodeID(temp, instrumentIndex);
  }

  // -- LightSource property storage -

  /* @see MetadataStore#setLightSourceNodeID(String, int, int) */
  public void setLightSourceNodeID(String nodeID, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setLightSourceNodeID(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    delegate.setLightSourceManufacturer(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(model) : model;
    delegate.setLightSourceModel(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    delegate.setLightSourceSerialNumber(temp, instrumentIndex, lightSourceIndex);
  }

  // -- Laser property storage -

  /* @see MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(type) : type;
    delegate.setLaserType(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(laserMedium) : laserMedium;
    delegate.setLaserLaserMedium(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    delegate.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    delegate.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    delegate.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(pulse) : pulse;
    delegate.setLaserPulse(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserPower(Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    delegate.setLaserPower(power, instrumentIndex, lightSourceIndex);
  }

  // -- Filament property storage -

  /* @see MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(type) : type;
    delegate.setFilamentType(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setFilamentPower(Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
    delegate.setFilamentPower(power, instrumentIndex, lightSourceIndex);
  }

  // -- Arc property storage -

  /* @see MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    String temp = filter ? DataTools.sanitize(type) : type;
    delegate.setArcType(temp, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setArcPower(Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
    delegate.setArcPower(power, instrumentIndex, lightSourceIndex);
  }

  // -- Detector property storage -

  /* @see MetadataStore#setDetectorNodeID(String, int, int) */
  public void setDetectorNodeID(String nodeID, int instrumentIndex, int detectorIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setDetectorNodeID(temp, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    String temp = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    delegate.setDetectorManufacturer(temp, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    String temp = filter ? DataTools.sanitize(model) : model;
    delegate.setDetectorModel(temp, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    String temp = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    delegate.setDetectorSerialNumber(temp, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    String temp = filter ? DataTools.sanitize(type) : type;
    delegate.setDetectorType(temp, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    delegate.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    delegate.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    delegate.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  // -- Objective property storage -

  /* @see MetadataStore#setObjectiveNodeID(String, int, int) */
  public void setObjectiveNodeID(String nodeID, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setObjectiveNodeID(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    delegate.setObjectiveManufacturer(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(model) : model;
    delegate.setObjectiveModel(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    delegate.setObjectiveSerialNumber(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(correction) : correction;
    delegate.setObjectiveCorrection(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    String temp = filter ? DataTools.sanitize(immersion) : immersion;
    delegate.setObjectiveImmersion(temp, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    delegate.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    delegate.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    delegate.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    delegate.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // -- OTF property storage -

  /* @see MetadataStore#setOTFNodeID(String, int, int) */
  public void setOTFNodeID(String nodeID, int instrumentIndex, int otfIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setOTFNodeID(temp, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    delegate.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    delegate.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    String temp = filter ? DataTools.sanitize(pixelType) : pixelType;
    delegate.setOTFPixelType(temp, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFPath(String, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
    String temp = filter ? DataTools.sanitize(path) : path;
    delegate.setOTFPath(temp, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    delegate.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  // -- Experimenter property storage -

  /* @see MetadataStore#setExperimenterNodeID(String, int) */
  public void setExperimenterNodeID(String nodeID, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(nodeID) : nodeID;
    delegate.setExperimenterNodeID(temp, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(firstName) : firstName;
    delegate.setExperimenterFirstName(temp, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(lastName) : lastName;
    delegate.setExperimenterLastName(temp, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(email) : email;
    delegate.setExperimenterEmail(temp, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(institution) : institution;
    delegate.setExperimenterInstitution(temp, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterDataDirectory(String, int) */
  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
    String temp = filter ? DataTools.sanitize(dataDirectory) : dataDirectory;
    delegate.setExperimenterDataDirectory(temp, experimenterIndex);
  }

}
