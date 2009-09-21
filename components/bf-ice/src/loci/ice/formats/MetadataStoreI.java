//
// MetadataStoreI.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2009 UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by curtis via MetadataAutogen on Sep 10, 2009 7:46:33 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

import Ice.Current;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.meta.MetadataStore} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataStoreI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataStoreI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataStoreI extends _MetadataStoreDisp {

  // -- Fields --

  private IMetadata metadataObject;

  // -- Constructors --

  public MetadataStoreI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  public MetadataStoreI(loci.formats.meta.MetadataStore store) {
    metadataObject = (IMetadata) store;
  }

  // -- MetadataStoreI methods --

  public loci.formats.meta.MetadataStore getWrappedObject() {
    return metadataObject;
  }

  public void setMetadataObject(loci.formats.meta.MetadataStore store) {
    metadataObject = (IMetadata) store;
  }

  // -- MetadataStore methods --

  public MetadataStore getServant(Current current) {
    return this;
  }

  public String getOMEXML(Current current) {
    return MetadataTools.getOMEXML(
      (loci.formats.meta.MetadataRetrieve) metadataObject);
  }

  public void createRoot(Current current) {
    metadataObject.createRoot();
  }

  // - Entity storage -

  public void setUUID(String uuid, Current current) {
    metadataObject.setUUID(uuid);
  }

  // - Arc property storage -

  public void setArcType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setArcType(type, instrumentIndex, lightSourceIndex);
  }

  // - ChannelComponent property storage -

  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    metadataObject.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    metadataObject.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    metadataObject.setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // - Circle property storage -

  public void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setCircleCx(cx, imageIndex, roiIndex, shapeIndex);
  }

  public void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setCircleCy(cy, imageIndex, roiIndex, shapeIndex);
  }

  public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setCircleID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setCircleR(r, imageIndex, roiIndex, shapeIndex);
  }

  public void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setCircleTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  // - Contact property storage -

  public void setContactExperimenter(String experimenter, int groupIndex, Current current) {
    metadataObject.setContactExperimenter(experimenter, groupIndex);
  }

  // - Dataset property storage -

  public void setDatasetDescription(String description, int datasetIndex, Current current) {
    metadataObject.setDatasetDescription(description, datasetIndex);
  }

  public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex, Current current) {
    metadataObject.setDatasetExperimenterRef(experimenterRef, datasetIndex);
  }

  public void setDatasetGroupRef(String groupRef, int datasetIndex, Current current) {
    metadataObject.setDatasetGroupRef(groupRef, datasetIndex);
  }

  public void setDatasetID(String id, int datasetIndex, Current current) {
    metadataObject.setDatasetID(id, datasetIndex);
  }

  public void setDatasetLocked(boolean locked, int datasetIndex, Current current) {
    metadataObject.setDatasetLocked(locked, datasetIndex);
  }

  public void setDatasetName(String name, int datasetIndex, Current current) {
    metadataObject.setDatasetName(name, datasetIndex);
  }

  // - DatasetRef property storage -

  public void setDatasetRefID(String id, int imageIndex, int datasetRefIndex, Current current) {
    metadataObject.setDatasetRefID(id, imageIndex, datasetRefIndex);
  }

  // - Detector property storage -

  public void setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
  }

  public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  public void setDetectorID(String id, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorID(id, instrumentIndex, detectorIndex);
  }

  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex);
  }

  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorModel(model, instrumentIndex, detectorIndex);
  }

  public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
  }

  public void setDetectorType(String type, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  public void setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex);
  }

  // - Dichroic property storage -

  public void setDichroicID(String id, int instrumentIndex, int dichroicIndex, Current current) {
    metadataObject.setDichroicID(id, instrumentIndex, dichroicIndex);
  }

  public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, Current current) {
    metadataObject.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex);
  }

  public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, Current current) {
    metadataObject.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex);
  }

  public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex, Current current) {
    metadataObject.setDichroicModel(model, instrumentIndex, dichroicIndex);
  }

  // - Dimensions property storage -

  public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  // - DisplayOptions property storage -

  public void setDisplayOptionsDisplay(String display, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsDisplay(display, imageIndex);
  }

  public void setDisplayOptionsID(String id, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsID(id, imageIndex);
  }

  public void setDisplayOptionsZoom(float zoom, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // - Ellipse property storage -

  public void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseCx(cx, imageIndex, roiIndex, shapeIndex);
  }

  public void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseCy(cy, imageIndex, roiIndex, shapeIndex);
  }

  public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseRx(rx, imageIndex, roiIndex, shapeIndex);
  }

  public void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseRy(ry, imageIndex, roiIndex, shapeIndex);
  }

  public void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  // - EmFilter property storage -

  public void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
  }

  public void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
  }

  public void setEmFilterModel(String model, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setEmFilterModel(model, instrumentIndex, filterIndex);
  }

  public void setEmFilterType(String type, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setEmFilterType(type, instrumentIndex, filterIndex);
  }

  // - ExFilter property storage -

  public void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
  }

  public void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
  }

  public void setExFilterModel(String model, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setExFilterModel(model, instrumentIndex, filterIndex);
  }

  public void setExFilterType(String type, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setExFilterType(type, instrumentIndex, filterIndex);
  }

  // - Experiment property storage -

  public void setExperimentDescription(String description, int experimentIndex, Current current) {
    metadataObject.setExperimentDescription(description, experimentIndex);
  }

  public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex, Current current) {
    metadataObject.setExperimentExperimenterRef(experimenterRef, experimentIndex);
  }

  public void setExperimentID(String id, int experimentIndex, Current current) {
    metadataObject.setExperimentID(id, experimentIndex);
  }

  public void setExperimentType(String type, int experimentIndex, Current current) {
    metadataObject.setExperimentType(type, experimentIndex);
  }

  // - Experimenter property storage -

  public void setExperimenterEmail(String email, int experimenterIndex, Current current) {
    metadataObject.setExperimenterEmail(email, experimenterIndex);
  }

  public void setExperimenterFirstName(String firstName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterFirstName(firstName, experimenterIndex);
  }

  public void setExperimenterID(String id, int experimenterIndex, Current current) {
    metadataObject.setExperimenterID(id, experimenterIndex);
  }

  public void setExperimenterInstitution(String institution, int experimenterIndex, Current current) {
    metadataObject.setExperimenterInstitution(institution, experimenterIndex);
  }

  public void setExperimenterLastName(String lastName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterLastName(lastName, experimenterIndex);
  }

  public void setExperimenterOMEName(String omeName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterOMEName(omeName, experimenterIndex);
  }

  // - ExperimenterMembership property storage -

  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, Current current) {
    metadataObject.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex);
  }

  // - Filament property storage -

  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setFilamentType(type, instrumentIndex, lightSourceIndex);
  }

  // - Filter property storage -

  public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex);
  }

  public void setFilterID(String id, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterID(id, instrumentIndex, filterIndex);
  }

  public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
  }

  public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
  }

  public void setFilterModel(String model, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterModel(model, instrumentIndex, filterIndex);
  }

  public void setFilterType(String type, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterType(type, instrumentIndex, filterIndex);
  }

  // - FilterSet property storage -

  public void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetID(id, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetModel(model, instrumentIndex, filterSetIndex);
  }

  // - Group property storage -

  public void setGroupID(String id, int groupIndex, Current current) {
    metadataObject.setGroupID(id, groupIndex);
  }

  public void setGroupName(String name, int groupIndex, Current current) {
    metadataObject.setGroupName(name, groupIndex);
  }

  // - GroupRef property storage -

  // - Image property storage -

  public void setImageAcquiredPixels(String acquiredPixels, int imageIndex, Current current) {
    metadataObject.setImageAcquiredPixels(acquiredPixels, imageIndex);
  }

  public void setImageCreationDate(String creationDate, int imageIndex, Current current) {
    metadataObject.setImageCreationDate(creationDate, imageIndex);
  }

  public void setImageDefaultPixels(String defaultPixels, int imageIndex, Current current) {
    metadataObject.setImageDefaultPixels(defaultPixels, imageIndex);
  }

  public void setImageDescription(String description, int imageIndex, Current current) {
    metadataObject.setImageDescription(description, imageIndex);
  }

  public void setImageExperimentRef(String experimentRef, int imageIndex, Current current) {
    metadataObject.setImageExperimentRef(experimentRef, imageIndex);
  }

  public void setImageExperimenterRef(String experimenterRef, int imageIndex, Current current) {
    metadataObject.setImageExperimenterRef(experimenterRef, imageIndex);
  }

  public void setImageGroupRef(String groupRef, int imageIndex, Current current) {
    metadataObject.setImageGroupRef(groupRef, imageIndex);
  }

  public void setImageID(String id, int imageIndex, Current current) {
    metadataObject.setImageID(id, imageIndex);
  }

  public void setImageInstrumentRef(String instrumentRef, int imageIndex, Current current) {
    metadataObject.setImageInstrumentRef(instrumentRef, imageIndex);
  }

  public void setImageName(String name, int imageIndex, Current current) {
    metadataObject.setImageName(name, imageIndex);
  }

  // - ImagingEnvironment property storage -

  public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  public void setImagingEnvironmentHumidity(float humidity, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  public void setImagingEnvironmentTemperature(float temperature, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // - Instrument property storage -

  public void setInstrumentID(String id, int instrumentIndex, Current current) {
    metadataObject.setInstrumentID(id, instrumentIndex);
  }

  // - Laser property storage -

  public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex);
  }

  public void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex);
  }

  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserPulse(pulse, instrumentIndex, lightSourceIndex);
  }

  public void setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex);
  }

  public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserType(type, instrumentIndex, lightSourceIndex);
  }

  public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  // - LightSource property storage -

  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceID(id, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceModel(model, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
  }

  // - LightSourceRef property storage -

  public void setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // - Line property storage -

  public void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  public void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineX1(x1, imageIndex, roiIndex, shapeIndex);
  }

  public void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineX2(x2, imageIndex, roiIndex, shapeIndex);
  }

  public void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineY1(y1, imageIndex, roiIndex, shapeIndex);
  }

  public void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineY2(y2, imageIndex, roiIndex, shapeIndex);
  }

  // - LogicalChannel property storage -

  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelID(id, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelName(name, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex);
  }

  // - Mask property storage -

  public void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskHeight(height, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskWidth(width, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskX(x, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskY(y, imageIndex, roiIndex, shapeIndex);
  }

  // - MaskPixels property storage -

  public void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex);
  }

  public void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex);
  }

  // - MicrobeamManipulation property storage -

  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex);
  }

  public void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex);
  }

  public void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex);
  }

  // - MicrobeamManipulationRef property storage -

  public void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, Current current) {
    metadataObject.setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex);
  }

  // - Microscope property storage -

  public void setMicroscopeID(String id, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeID(id, instrumentIndex);
  }

  public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeManufacturer(manufacturer, instrumentIndex);
  }

  public void setMicroscopeModel(String model, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeModel(model, instrumentIndex);
  }

  public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeSerialNumber(serialNumber, instrumentIndex);
  }

  public void setMicroscopeType(String type, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeType(type, instrumentIndex);
  }

  // - OTF property storage -

  public void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex);
  }

  public void setOTFID(String id, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFID(id, instrumentIndex, otfIndex);
  }

  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjective(objective, instrumentIndex, otfIndex);
  }

  public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFPixelType(pixelType, instrumentIndex, otfIndex);
  }

  public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  // - Objective property storage -

  public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveID(id, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveModel(model, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // - ObjectiveSettings property storage -

  public void setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
  }

  public void setObjectiveSettingsMedium(String medium, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsMedium(medium, imageIndex);
  }

  public void setObjectiveSettingsObjective(String objective, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsObjective(objective, imageIndex);
  }

  public void setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
  }

  // - Path property storage -

  public void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathD(d, imageIndex, roiIndex, shapeIndex);
  }

  public void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathID(id, imageIndex, roiIndex, shapeIndex);
  }

  // - Pixels property storage -

  public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex);
  }

  public void setPixelsID(String id, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsID(id, imageIndex, pixelsIndex);
  }

  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsPixelType(pixelType, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  // - Plane property storage -

  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneID(id, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - PlaneTiming property storage -

  public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // - Plate property storage -

  public void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, Current current) {
    metadataObject.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
  }

  public void setPlateDescription(String description, int plateIndex, Current current) {
    metadataObject.setPlateDescription(description, plateIndex);
  }

  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, Current current) {
    metadataObject.setPlateExternalIdentifier(externalIdentifier, plateIndex);
  }

  public void setPlateID(String id, int plateIndex, Current current) {
    metadataObject.setPlateID(id, plateIndex);
  }

  public void setPlateName(String name, int plateIndex, Current current) {
    metadataObject.setPlateName(name, plateIndex);
  }

  public void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, Current current) {
    metadataObject.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
  }

  public void setPlateStatus(String status, int plateIndex, Current current) {
    metadataObject.setPlateStatus(status, plateIndex);
  }

  public void setPlateWellOriginX(double wellOriginX, int plateIndex, Current current) {
    metadataObject.setPlateWellOriginX(wellOriginX, plateIndex);
  }

  public void setPlateWellOriginY(double wellOriginY, int plateIndex, Current current) {
    metadataObject.setPlateWellOriginY(wellOriginY, plateIndex);
  }

  // - PlateRef property storage -

  public void setPlateRefID(String id, int screenIndex, int plateRefIndex, Current current) {
    metadataObject.setPlateRefID(id, screenIndex, plateRefIndex);
  }

  public void setPlateRefSample(int sample, int screenIndex, int plateRefIndex, Current current) {
    metadataObject.setPlateRefSample(sample, screenIndex, plateRefIndex);
  }

  public void setPlateRefWell(String well, int screenIndex, int plateRefIndex, Current current) {
    metadataObject.setPlateRefWell(well, screenIndex, plateRefIndex);
  }

  // - Point property storage -

  public void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointCx(cx, imageIndex, roiIndex, shapeIndex);
  }

  public void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointCy(cy, imageIndex, roiIndex, shapeIndex);
  }

  public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointR(r, imageIndex, roiIndex, shapeIndex);
  }

  public void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  // - Polygon property storage -

  public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolygonID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolygonPoints(points, imageIndex, roiIndex, shapeIndex);
  }

  public void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  // - Polyline property storage -

  public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylinePoints(points, imageIndex, roiIndex, shapeIndex);
  }

  public void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  // - Project property storage -

  public void setProjectDescription(String description, int projectIndex, Current current) {
    metadataObject.setProjectDescription(description, projectIndex);
  }

  public void setProjectExperimenterRef(String experimenterRef, int projectIndex, Current current) {
    metadataObject.setProjectExperimenterRef(experimenterRef, projectIndex);
  }

  public void setProjectGroupRef(String groupRef, int projectIndex, Current current) {
    metadataObject.setProjectGroupRef(groupRef, projectIndex);
  }

  public void setProjectID(String id, int projectIndex, Current current) {
    metadataObject.setProjectID(id, projectIndex);
  }

  public void setProjectName(String name, int projectIndex, Current current) {
    metadataObject.setProjectName(name, projectIndex);
  }

  // - ProjectRef property storage -

  public void setProjectRefID(String id, int datasetIndex, int projectRefIndex, Current current) {
    metadataObject.setProjectRefID(id, datasetIndex, projectRefIndex);
  }

  // - Pump property storage -

  public void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex);
  }

  // - ROI property storage -

  public void setROIID(String id, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIID(id, imageIndex, roiIndex);
  }

  public void setROIT0(int t0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIT0(t0, imageIndex, roiIndex);
  }

  public void setROIT1(int t1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIT1(t1, imageIndex, roiIndex);
  }

  public void setROIX0(int x0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIX0(x0, imageIndex, roiIndex);
  }

  public void setROIX1(int x1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIX1(x1, imageIndex, roiIndex);
  }

  public void setROIY0(int y0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIY0(y0, imageIndex, roiIndex);
  }

  public void setROIY1(int y1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIY1(y1, imageIndex, roiIndex);
  }

  public void setROIZ0(int z0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIZ0(z0, imageIndex, roiIndex);
  }

  public void setROIZ1(int z1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIZ1(z1, imageIndex, roiIndex);
  }

  // - ROIRef property storage -

  public void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, Current current) {
    metadataObject.setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex);
  }

  // - Reagent property storage -

  public void setReagentDescription(String description, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentDescription(description, screenIndex, reagentIndex);
  }

  public void setReagentID(String id, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentID(id, screenIndex, reagentIndex);
  }

  public void setReagentName(String name, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentName(name, screenIndex, reagentIndex);
  }

  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex);
  }

  // - Rect property storage -

  public void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectHeight(height, imageIndex, roiIndex, shapeIndex);
  }

  public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectTransform(transform, imageIndex, roiIndex, shapeIndex);
  }

  public void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectWidth(width, imageIndex, roiIndex, shapeIndex);
  }

  public void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectX(x, imageIndex, roiIndex, shapeIndex);
  }

  public void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectY(y, imageIndex, roiIndex, shapeIndex);
  }

  // - Region property storage -

  public void setRegionID(String id, int imageIndex, int regionIndex, Current current) {
    metadataObject.setRegionID(id, imageIndex, regionIndex);
  }

  public void setRegionName(String name, int imageIndex, int regionIndex, Current current) {
    metadataObject.setRegionName(name, imageIndex, regionIndex);
  }

  public void setRegionTag(String tag, int imageIndex, int regionIndex, Current current) {
    metadataObject.setRegionTag(tag, imageIndex, regionIndex);
  }

  // - RoiLink property storage -

  public void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    metadataObject.setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex);
  }

  public void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    metadataObject.setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex);
  }

  public void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    metadataObject.setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex);
  }

  // - Screen property storage -

  public void setScreenDescription(String description, int screenIndex, Current current) {
    metadataObject.setScreenDescription(description, screenIndex);
  }

  public void setScreenExtern(String extern, int screenIndex, Current current) {
    metadataObject.setScreenExtern(extern, screenIndex);
  }

  public void setScreenID(String id, int screenIndex, Current current) {
    metadataObject.setScreenID(id, screenIndex);
  }

  public void setScreenName(String name, int screenIndex, Current current) {
    metadataObject.setScreenName(name, screenIndex);
  }

  public void setScreenProtocolDescription(String protocolDescription, int screenIndex, Current current) {
    metadataObject.setScreenProtocolDescription(protocolDescription, screenIndex);
  }

  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, Current current) {
    metadataObject.setScreenProtocolIdentifier(protocolIdentifier, screenIndex);
  }

  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, Current current) {
    metadataObject.setScreenReagentSetDescription(reagentSetDescription, screenIndex);
  }

  public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, Current current) {
    metadataObject.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex);
  }

  public void setScreenType(String type, int screenIndex, Current current) {
    metadataObject.setScreenType(type, screenIndex);
  }

  // - ScreenAcquisition property storage -

  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex);
  }

  // - ScreenRef property storage -

  public void setScreenRefID(String id, int plateIndex, int screenRefIndex, Current current) {
    metadataObject.setScreenRefID(id, plateIndex, screenRefIndex);
  }

  // - Shape property storage -

  public void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeDirection(direction, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeG(g, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeID(id, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeText(text, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex);
  }

  public void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex);
  }

  // - StageLabel property storage -

  public void setStageLabelName(String name, int imageIndex, Current current) {
    metadataObject.setStageLabelName(name, imageIndex);
  }

  public void setStageLabelX(float x, int imageIndex, Current current) {
    metadataObject.setStageLabelX(x, imageIndex);
  }

  public void setStageLabelY(float y, int imageIndex, Current current) {
    metadataObject.setStageLabelY(y, imageIndex);
  }

  public void setStageLabelZ(float z, int imageIndex, Current current) {
    metadataObject.setStageLabelZ(z, imageIndex);
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - Thumbnail property storage -

  public void setThumbnailHref(String href, int imageIndex, Current current) {
    metadataObject.setThumbnailHref(href, imageIndex);
  }

  public void setThumbnailID(String id, int imageIndex, Current current) {
    metadataObject.setThumbnailID(id, imageIndex);
  }

  public void setThumbnailMIMEtype(String mimEtype, int imageIndex, Current current) {
    metadataObject.setThumbnailMIMEtype(mimEtype, imageIndex);
  }

  // - TiffData property storage -

  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex);
  }

  // - TransmittanceRange property storage -

  public void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex);
  }

  public void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex);
  }

  public void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex);
  }

  public void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex);
  }

  public void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
  }

  // - Well property storage -

  public void setWellColumn(int column, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellColumn(column, plateIndex, wellIndex);
  }

  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellExternalDescription(externalDescription, plateIndex, wellIndex);
  }

  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex);
  }

  public void setWellID(String id, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellID(id, plateIndex, wellIndex);
  }

  public void setWellReagent(String reagent, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellReagent(reagent, plateIndex, wellIndex);
  }

  public void setWellRow(int row, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellRow(row, plateIndex, wellIndex);
  }

  public void setWellType(String type, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellType(type, plateIndex, wellIndex);
  }

  // - WellSample property storage -

  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

  // - WellSampleRef property storage -

  public void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Current current) {
    metadataObject.setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
  }

}
