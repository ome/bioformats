//
// IMetadataI.java
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
 * Created by curtis via MetadataAutogen on Oct 20, 2009 12:57:18 AM EDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

import Ice.Current;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.meta.IMetadata} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/IMetadataI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/IMetadataI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IMetadataI extends _IMetadataDisp {

  // -- Fields --

  private IMetadata metadataObject;

  // -- Constructor --

  public IMetadataI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  // -- IMetadataI methods --

  public loci.formats.meta.IMetadata getWrappedObject() {
    return metadataObject;
  }

  public void setMetadataObject(loci.formats.meta.IMetadata meta) {
    metadataObject = meta;
  }

  // -- MetadataRetrieve methods --

  public String getOMEXML(Current current) {
    return MetadataTools.getOMEXML(metadataObject);
  }

  // - Entity counting -

  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getChannelComponentCount(imageIndex, logicalChannelIndex);
  }

  public int getDatasetCount(Current current) {
    return metadataObject.getDatasetCount();
  }

  public int getDatasetRefCount(int imageIndex, Current current) {
    return metadataObject.getDatasetRefCount(imageIndex);
  }

  public int getDetectorCount(int instrumentIndex, Current current) {
    return metadataObject.getDetectorCount(instrumentIndex);
  }

  public int getDichroicCount(int instrumentIndex, Current current) {
    return metadataObject.getDichroicCount(instrumentIndex);
  }

  public int getExperimentCount(Current current) {
    return metadataObject.getExperimentCount();
  }

  public int getExperimenterCount(Current current) {
    return metadataObject.getExperimenterCount();
  }

  public int getExperimenterMembershipCount(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterMembershipCount(experimenterIndex);
  }

  public int getFilterCount(int instrumentIndex, Current current) {
    return metadataObject.getFilterCount(instrumentIndex);
  }

  public int getFilterSetCount(int instrumentIndex, Current current) {
    return metadataObject.getFilterSetCount(instrumentIndex);
  }

  public int getGroupCount(Current current) {
    return metadataObject.getGroupCount();
  }

  public int getGroupRefCount(int experimenterIndex, Current current) {
    return metadataObject.getGroupRefCount(experimenterIndex);
  }

  public int getImageCount(Current current) {
    return metadataObject.getImageCount();
  }

  public int getInstrumentCount(Current current) {
    return metadataObject.getInstrumentCount();
  }

  public int getLightSourceCount(int instrumentIndex, Current current) {
    return metadataObject.getLightSourceCount(instrumentIndex);
  }

  public int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getLightSourceRefCount(imageIndex, microbeamManipulationIndex);
  }

  public int getLogicalChannelCount(int imageIndex, Current current) {
    return metadataObject.getLogicalChannelCount(imageIndex);
  }

  public int getMicrobeamManipulationCount(int imageIndex, Current current) {
    return metadataObject.getMicrobeamManipulationCount(imageIndex);
  }

  public int getMicrobeamManipulationRefCount(int experimentIndex, Current current) {
    return metadataObject.getMicrobeamManipulationRefCount(experimentIndex);
  }

  public int getOTFCount(int instrumentIndex, Current current) {
    return metadataObject.getOTFCount(instrumentIndex);
  }

  public int getObjectiveCount(int instrumentIndex, Current current) {
    return metadataObject.getObjectiveCount(instrumentIndex);
  }

  public int getPixelsCount(int imageIndex, Current current) {
    return metadataObject.getPixelsCount(imageIndex);
  }

  public int getPlaneCount(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPlaneCount(imageIndex, pixelsIndex);
  }

  public int getPlateCount(Current current) {
    return metadataObject.getPlateCount();
  }

  public int getPlateRefCount(int screenIndex, Current current) {
    return metadataObject.getPlateRefCount(screenIndex);
  }

  public int getProjectCount(Current current) {
    return metadataObject.getProjectCount();
  }

  public int getProjectRefCount(int datasetIndex, Current current) {
    return metadataObject.getProjectRefCount(datasetIndex);
  }

  public int getROICount(int imageIndex, Current current) {
    return metadataObject.getROICount(imageIndex);
  }

  public int getROIRefCount(int imageIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getROIRefCount(imageIndex, microbeamManipulationIndex);
  }

  public int getReagentCount(int screenIndex, Current current) {
    return metadataObject.getReagentCount(screenIndex);
  }

  public int getRegionCount(int imageIndex, Current current) {
    return metadataObject.getRegionCount(imageIndex);
  }

  public int getRoiLinkCount(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getRoiLinkCount(imageIndex, roiIndex);
  }

  public int getScreenCount(Current current) {
    return metadataObject.getScreenCount();
  }

  public int getScreenAcquisitionCount(int screenIndex, Current current) {
    return metadataObject.getScreenAcquisitionCount(screenIndex);
  }

  public int getScreenRefCount(int plateIndex, Current current) {
    return metadataObject.getScreenRefCount(plateIndex);
  }

  public int getShapeCount(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getShapeCount(imageIndex, roiIndex);
  }

  public int getTiffDataCount(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getTiffDataCount(imageIndex, pixelsIndex);
  }

  public int getWellCount(int plateIndex, Current current) {
    return metadataObject.getWellCount(plateIndex);
  }

  public int getWellSampleCount(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellSampleCount(plateIndex, wellIndex);
  }

  public int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, Current current) {
    return metadataObject.getWellSampleRefCount(screenIndex, screenAcquisitionIndex);
  }

  // - Entity retrieval -

  public String getUUID(Current current) {
    return metadataObject.getUUID();
  }

  // - Arc property retrieval -

  public String getArcType(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getArcType(instrumentIndex, lightSourceIndex);
  }

  // - ChannelComponent property retrieval -

  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    return metadataObject.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    return metadataObject.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    return metadataObject.getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // - Circle property retrieval -

  public String getCircleCx(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getCircleCx(imageIndex, roiIndex, shapeIndex);
  }

  public String getCircleCy(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getCircleCy(imageIndex, roiIndex, shapeIndex);
  }

  public String getCircleID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getCircleID(imageIndex, roiIndex, shapeIndex);
  }

  public String getCircleR(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getCircleR(imageIndex, roiIndex, shapeIndex);
  }

  public String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getCircleTransform(imageIndex, roiIndex, shapeIndex);
  }

  // - Contact property retrieval -

  public String getContactExperimenter(int groupIndex, Current current) {
    return metadataObject.getContactExperimenter(groupIndex);
  }

  // - Dataset property retrieval -

  public String getDatasetDescription(int datasetIndex, Current current) {
    return metadataObject.getDatasetDescription(datasetIndex);
  }

  public String getDatasetExperimenterRef(int datasetIndex, Current current) {
    return metadataObject.getDatasetExperimenterRef(datasetIndex);
  }

  public String getDatasetGroupRef(int datasetIndex, Current current) {
    return metadataObject.getDatasetGroupRef(datasetIndex);
  }

  public String getDatasetID(int datasetIndex, Current current) {
    return metadataObject.getDatasetID(datasetIndex);
  }

  public boolean getDatasetLocked(int datasetIndex, Current current) {
    return metadataObject.getDatasetLocked(datasetIndex);
  }

  public String getDatasetName(int datasetIndex, Current current) {
    return metadataObject.getDatasetName(datasetIndex);
  }

  // - DatasetRef property retrieval -

  public String getDatasetRefID(int imageIndex, int datasetRefIndex, Current current) {
    return metadataObject.getDatasetRefID(imageIndex, datasetRefIndex);
  }

  // - Detector property retrieval -

  public double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
  }

  public double getDetectorGain(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorGain(instrumentIndex, detectorIndex);
  }

  public String getDetectorID(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorID(instrumentIndex, detectorIndex);
  }

  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorManufacturer(instrumentIndex, detectorIndex);
  }

  public String getDetectorModel(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorModel(instrumentIndex, detectorIndex);
  }

  public double getDetectorOffset(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorOffset(instrumentIndex, detectorIndex);
  }

  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorSerialNumber(instrumentIndex, detectorIndex);
  }

  public String getDetectorType(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorType(instrumentIndex, detectorIndex);
  }

  public double getDetectorVoltage(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorVoltage(instrumentIndex, detectorIndex);
  }

  public double getDetectorZoom(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorZoom(instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property retrieval -

  public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsBinning(imageIndex, logicalChannelIndex);
  }

  public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
  }

  public double getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
  }

  public double getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
  }

  public double getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex);
  }

  public double getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsVoltage(imageIndex, logicalChannelIndex);
  }

  // - Dichroic property retrieval -

  public String getDichroicID(int instrumentIndex, int dichroicIndex, Current current) {
    return metadataObject.getDichroicID(instrumentIndex, dichroicIndex);
  }

  public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, Current current) {
    return metadataObject.getDichroicLotNumber(instrumentIndex, dichroicIndex);
  }

  public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, Current current) {
    return metadataObject.getDichroicManufacturer(instrumentIndex, dichroicIndex);
  }

  public String getDichroicModel(int instrumentIndex, int dichroicIndex, Current current) {
    return metadataObject.getDichroicModel(instrumentIndex, dichroicIndex);
  }

  // - Dimensions property retrieval -

  public double getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex);
  }

  public double getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex);
  }

  public double getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex);
  }

  public double getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsTimeIncrement(imageIndex, pixelsIndex);
  }

  public int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsWaveIncrement(imageIndex, pixelsIndex);
  }

  public int getDimensionsWaveStart(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsWaveStart(imageIndex, pixelsIndex);
  }

  // - DisplayOptions property retrieval -

  public String getDisplayOptionsDisplay(int imageIndex, Current current) {
    return metadataObject.getDisplayOptionsDisplay(imageIndex);
  }

  public String getDisplayOptionsID(int imageIndex, Current current) {
    return metadataObject.getDisplayOptionsID(imageIndex);
  }

  public double getDisplayOptionsZoom(int imageIndex, Current current) {
    return metadataObject.getDisplayOptionsZoom(imageIndex);
  }

  // - Ellipse property retrieval -

  public String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseCx(imageIndex, roiIndex, shapeIndex);
  }

  public String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseCy(imageIndex, roiIndex, shapeIndex);
  }

  public String getEllipseID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseID(imageIndex, roiIndex, shapeIndex);
  }

  public String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseRx(imageIndex, roiIndex, shapeIndex);
  }

  public String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseRy(imageIndex, roiIndex, shapeIndex);
  }

  public String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseTransform(imageIndex, roiIndex, shapeIndex);
  }

  // - EmFilter property retrieval -

  public String getEmFilterLotNumber(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getEmFilterLotNumber(instrumentIndex, filterIndex);
  }

  public String getEmFilterManufacturer(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getEmFilterManufacturer(instrumentIndex, filterIndex);
  }

  public String getEmFilterModel(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getEmFilterModel(instrumentIndex, filterIndex);
  }

  public String getEmFilterType(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getEmFilterType(instrumentIndex, filterIndex);
  }

  // - ExFilter property retrieval -

  public String getExFilterLotNumber(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getExFilterLotNumber(instrumentIndex, filterIndex);
  }

  public String getExFilterManufacturer(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getExFilterManufacturer(instrumentIndex, filterIndex);
  }

  public String getExFilterModel(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getExFilterModel(instrumentIndex, filterIndex);
  }

  public String getExFilterType(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getExFilterType(instrumentIndex, filterIndex);
  }

  // - Experiment property retrieval -

  public String getExperimentDescription(int experimentIndex, Current current) {
    return metadataObject.getExperimentDescription(experimentIndex);
  }

  public String getExperimentExperimenterRef(int experimentIndex, Current current) {
    return metadataObject.getExperimentExperimenterRef(experimentIndex);
  }

  public String getExperimentID(int experimentIndex, Current current) {
    return metadataObject.getExperimentID(experimentIndex);
  }

  public String getExperimentType(int experimentIndex, Current current) {
    return metadataObject.getExperimentType(experimentIndex);
  }

  // - Experimenter property retrieval -

  public String getExperimenterEmail(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterEmail(experimenterIndex);
  }

  public String getExperimenterFirstName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterFirstName(experimenterIndex);
  }

  public String getExperimenterID(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterID(experimenterIndex);
  }

  public String getExperimenterInstitution(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterInstitution(experimenterIndex);
  }

  public String getExperimenterLastName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterLastName(experimenterIndex);
  }

  public String getExperimenterOMEName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterOMEName(experimenterIndex);
  }

  // - ExperimenterMembership property retrieval -

  public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, Current current) {
    return metadataObject.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex);
  }

  // - Filament property retrieval -

  public String getFilamentType(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getFilamentType(instrumentIndex, lightSourceIndex);
  }

  // - Filter property retrieval -

  public String getFilterFilterWheel(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterFilterWheel(instrumentIndex, filterIndex);
  }

  public String getFilterID(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterID(instrumentIndex, filterIndex);
  }

  public String getFilterLotNumber(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterLotNumber(instrumentIndex, filterIndex);
  }

  public String getFilterManufacturer(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterManufacturer(instrumentIndex, filterIndex);
  }

  public String getFilterModel(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterModel(instrumentIndex, filterIndex);
  }

  public String getFilterType(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterType(instrumentIndex, filterIndex);
  }

  // - FilterSet property retrieval -

  public String getFilterSetDichroic(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetDichroic(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetEmFilter(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetExFilter(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetExFilter(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetID(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetID(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetModel(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetModel(instrumentIndex, filterSetIndex);
  }

  // - Group property retrieval -

  public String getGroupID(int groupIndex, Current current) {
    return metadataObject.getGroupID(groupIndex);
  }

  public String getGroupName(int groupIndex, Current current) {
    return metadataObject.getGroupName(groupIndex);
  }

  // - GroupRef property retrieval -

  // - Image property retrieval -

  public String getImageAcquiredPixels(int imageIndex, Current current) {
    return metadataObject.getImageAcquiredPixels(imageIndex);
  }

  public String getImageCreationDate(int imageIndex, Current current) {
    return metadataObject.getImageCreationDate(imageIndex);
  }

  public String getImageDefaultPixels(int imageIndex, Current current) {
    return metadataObject.getImageDefaultPixels(imageIndex);
  }

  public String getImageDescription(int imageIndex, Current current) {
    return metadataObject.getImageDescription(imageIndex);
  }

  public String getImageExperimentRef(int imageIndex, Current current) {
    return metadataObject.getImageExperimentRef(imageIndex);
  }

  public String getImageExperimenterRef(int imageIndex, Current current) {
    return metadataObject.getImageExperimenterRef(imageIndex);
  }

  public String getImageGroupRef(int imageIndex, Current current) {
    return metadataObject.getImageGroupRef(imageIndex);
  }

  public String getImageID(int imageIndex, Current current) {
    return metadataObject.getImageID(imageIndex);
  }

  public String getImageInstrumentRef(int imageIndex, Current current) {
    return metadataObject.getImageInstrumentRef(imageIndex);
  }

  public String getImageName(int imageIndex, Current current) {
    return metadataObject.getImageName(imageIndex);
  }

  // - ImagingEnvironment property retrieval -

  public double getImagingEnvironmentAirPressure(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentAirPressure(imageIndex);
  }

  public double getImagingEnvironmentCO2Percent(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentCO2Percent(imageIndex);
  }

  public double getImagingEnvironmentHumidity(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentHumidity(imageIndex);
  }

  public double getImagingEnvironmentTemperature(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentTemperature(imageIndex);
  }

  // - Instrument property retrieval -

  public String getInstrumentID(int instrumentIndex, Current current) {
    return metadataObject.getInstrumentID(instrumentIndex);
  }

  // - Laser property retrieval -

  public int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex);
  }

  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserLaserMedium(instrumentIndex, lightSourceIndex);
  }

  public boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserPockelCell(instrumentIndex, lightSourceIndex);
  }

  public String getLaserPulse(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserPulse(instrumentIndex, lightSourceIndex);
  }

  public double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserRepetitionRate(instrumentIndex, lightSourceIndex);
  }

  public boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserTuneable(instrumentIndex, lightSourceIndex);
  }

  public String getLaserType(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserType(instrumentIndex, lightSourceIndex);
  }

  public int getLaserWavelength(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLaserWavelength(instrumentIndex, lightSourceIndex);
  }

  // - LightSource property retrieval -

  public String getLightSourceID(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourceID(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourceManufacturer(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourceModel(instrumentIndex, lightSourceIndex);
  }

  public double getLightSourcePower(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourcePower(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex);
  }

  // - LightSourceRef property retrieval -

  public double getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  // - LightSourceSettings property retrieval -

  public double getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex);
  }

  public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex);
  }

  public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex);
  }

  // - Line property retrieval -

  public String getLineID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineID(imageIndex, roiIndex, shapeIndex);
  }

  public String getLineTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineTransform(imageIndex, roiIndex, shapeIndex);
  }

  public String getLineX1(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineX1(imageIndex, roiIndex, shapeIndex);
  }

  public String getLineX2(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineX2(imageIndex, roiIndex, shapeIndex);
  }

  public String getLineY1(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineY1(imageIndex, roiIndex, shapeIndex);
  }

  public String getLineY2(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineY2(imageIndex, roiIndex, shapeIndex);
  }

  // - LogicalChannel property retrieval -

  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelDetector(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelEmWave(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelExWave(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelFilterSet(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelFluor(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelID(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelID(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelLightSource(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelMode(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelName(imageIndex, logicalChannelIndex);
  }

  public double getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelOTF(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex);
  }

  public double getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex);
  }

  // - Mask property retrieval -

  public String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskHeight(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskID(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskTransform(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskWidth(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskX(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskX(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskY(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskY(imageIndex, roiIndex, shapeIndex);
  }

  // - MaskPixels property retrieval -

  public boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex);
  }

  public byte[] getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex);
  }

  public String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsID(imageIndex, roiIndex, shapeIndex);
  }

  public int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex);
  }

  public int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex);
  }

  // - MicrobeamManipulation property retrieval -

  public String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex);
  }

  public String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex);
  }

  public String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex);
  }

  // - MicrobeamManipulationRef property retrieval -

  public String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, Current current) {
    return metadataObject.getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex);
  }

  // - Microscope property retrieval -

  public String getMicroscopeID(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeID(instrumentIndex);
  }

  public String getMicroscopeManufacturer(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeManufacturer(instrumentIndex);
  }

  public String getMicroscopeModel(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeModel(instrumentIndex);
  }

  public String getMicroscopeSerialNumber(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeSerialNumber(instrumentIndex);
  }

  public String getMicroscopeType(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeType(instrumentIndex);
  }

  // - OTF property retrieval -

  public String getOTFBinaryFile(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFBinaryFile(instrumentIndex, otfIndex);
  }

  public String getOTFID(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFID(instrumentIndex, otfIndex);
  }

  public String getOTFObjective(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFObjective(instrumentIndex, otfIndex);
  }

  public boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
  }

  public String getOTFPixelType(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFPixelType(instrumentIndex, otfIndex);
  }

  public int getOTFSizeX(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFSizeX(instrumentIndex, otfIndex);
  }

  public int getOTFSizeY(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFSizeY(instrumentIndex, otfIndex);
  }

  // - Objective property retrieval -

  public double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveCorrection(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveID(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveID(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveImmersion(instrumentIndex, objectiveIndex);
  }

  public boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveIris(instrumentIndex, objectiveIndex);
  }

  public double getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveLensNA(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveModel(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveModel(instrumentIndex, objectiveIndex);
  }

  public int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
  }

  public double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
  }

  // - ObjectiveSettings property retrieval -

  public double getObjectiveSettingsCorrectionCollar(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsCorrectionCollar(imageIndex);
  }

  public String getObjectiveSettingsMedium(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsMedium(imageIndex);
  }

  public String getObjectiveSettingsObjective(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsObjective(imageIndex);
  }

  public double getObjectiveSettingsRefractiveIndex(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsRefractiveIndex(imageIndex);
  }

  // - Path property retrieval -

  public String getPathD(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathD(imageIndex, roiIndex, shapeIndex);
  }

  public String getPathID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathID(imageIndex, roiIndex, shapeIndex);
  }

  // - Pixels property retrieval -

  public boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsBigEndian(imageIndex, pixelsIndex);
  }

  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsDimensionOrder(imageIndex, pixelsIndex);
  }

  public String getPixelsID(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsID(imageIndex, pixelsIndex);
  }

  public String getPixelsPixelType(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsPixelType(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeC(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsSizeC(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeT(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsSizeT(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeX(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsSizeX(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeY(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsSizeY(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeZ(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getPixelsSizeZ(imageIndex, pixelsIndex);
  }

  // - Plane property retrieval -

  public String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex);
  }

  public String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneID(imageIndex, pixelsIndex, planeIndex);
  }

  public int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheC(imageIndex, pixelsIndex, planeIndex);
  }

  public int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheT(imageIndex, pixelsIndex, planeIndex);
  }

  public int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex);
  }

  // - PlaneTiming property retrieval -

  public double getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex);
  }

  public double getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex);
  }

  // - Plate property retrieval -

  public String getPlateColumnNamingConvention(int plateIndex, Current current) {
    return metadataObject.getPlateColumnNamingConvention(plateIndex);
  }

  public String getPlateDescription(int plateIndex, Current current) {
    return metadataObject.getPlateDescription(plateIndex);
  }

  public String getPlateExternalIdentifier(int plateIndex, Current current) {
    return metadataObject.getPlateExternalIdentifier(plateIndex);
  }

  public String getPlateID(int plateIndex, Current current) {
    return metadataObject.getPlateID(plateIndex);
  }

  public String getPlateName(int plateIndex, Current current) {
    return metadataObject.getPlateName(plateIndex);
  }

  public String getPlateRowNamingConvention(int plateIndex, Current current) {
    return metadataObject.getPlateRowNamingConvention(plateIndex);
  }

  public String getPlateStatus(int plateIndex, Current current) {
    return metadataObject.getPlateStatus(plateIndex);
  }

  public double getPlateWellOriginX(int plateIndex, Current current) {
    return metadataObject.getPlateWellOriginX(plateIndex);
  }

  public double getPlateWellOriginY(int plateIndex, Current current) {
    return metadataObject.getPlateWellOriginY(plateIndex);
  }

  // - PlateRef property retrieval -

  public String getPlateRefID(int screenIndex, int plateRefIndex, Current current) {
    return metadataObject.getPlateRefID(screenIndex, plateRefIndex);
  }

  public int getPlateRefSample(int screenIndex, int plateRefIndex, Current current) {
    return metadataObject.getPlateRefSample(screenIndex, plateRefIndex);
  }

  public String getPlateRefWell(int screenIndex, int plateRefIndex, Current current) {
    return metadataObject.getPlateRefWell(screenIndex, plateRefIndex);
  }

  // - Point property retrieval -

  public String getPointCx(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointCx(imageIndex, roiIndex, shapeIndex);
  }

  public String getPointCy(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointCy(imageIndex, roiIndex, shapeIndex);
  }

  public String getPointID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointID(imageIndex, roiIndex, shapeIndex);
  }

  public String getPointR(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointR(imageIndex, roiIndex, shapeIndex);
  }

  public String getPointTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointTransform(imageIndex, roiIndex, shapeIndex);
  }

  // - Polygon property retrieval -

  public String getPolygonID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolygonID(imageIndex, roiIndex, shapeIndex);
  }

  public String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolygonPoints(imageIndex, roiIndex, shapeIndex);
  }

  public String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolygonTransform(imageIndex, roiIndex, shapeIndex);
  }

  // - Polyline property retrieval -

  public String getPolylineID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineID(imageIndex, roiIndex, shapeIndex);
  }

  public String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylinePoints(imageIndex, roiIndex, shapeIndex);
  }

  public String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineTransform(imageIndex, roiIndex, shapeIndex);
  }

  // - Project property retrieval -

  public String getProjectDescription(int projectIndex, Current current) {
    return metadataObject.getProjectDescription(projectIndex);
  }

  public String getProjectExperimenterRef(int projectIndex, Current current) {
    return metadataObject.getProjectExperimenterRef(projectIndex);
  }

  public String getProjectGroupRef(int projectIndex, Current current) {
    return metadataObject.getProjectGroupRef(projectIndex);
  }

  public String getProjectID(int projectIndex, Current current) {
    return metadataObject.getProjectID(projectIndex);
  }

  public String getProjectName(int projectIndex, Current current) {
    return metadataObject.getProjectName(projectIndex);
  }

  // - ProjectRef property retrieval -

  public String getProjectRefID(int datasetIndex, int projectRefIndex, Current current) {
    return metadataObject.getProjectRefID(datasetIndex, projectRefIndex);
  }

  // - Pump property retrieval -

  public String getPumpLightSource(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getPumpLightSource(instrumentIndex, lightSourceIndex);
  }

  // - ROI property retrieval -

  public String getROIID(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIID(imageIndex, roiIndex);
  }

  public int getROIT0(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIT0(imageIndex, roiIndex);
  }

  public int getROIT1(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIT1(imageIndex, roiIndex);
  }

  public int getROIX0(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIX0(imageIndex, roiIndex);
  }

  public int getROIX1(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIX1(imageIndex, roiIndex);
  }

  public int getROIY0(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIY0(imageIndex, roiIndex);
  }

  public int getROIY1(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIY1(imageIndex, roiIndex);
  }

  public int getROIZ0(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIZ0(imageIndex, roiIndex);
  }

  public int getROIZ1(int imageIndex, int roiIndex, Current current) {
    return metadataObject.getROIZ1(imageIndex, roiIndex);
  }

  // - ROIRef property retrieval -

  public String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, Current current) {
    return metadataObject.getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex);
  }

  // - Reagent property retrieval -

  public String getReagentDescription(int screenIndex, int reagentIndex, Current current) {
    return metadataObject.getReagentDescription(screenIndex, reagentIndex);
  }

  public String getReagentID(int screenIndex, int reagentIndex, Current current) {
    return metadataObject.getReagentID(screenIndex, reagentIndex);
  }

  public String getReagentName(int screenIndex, int reagentIndex, Current current) {
    return metadataObject.getReagentName(screenIndex, reagentIndex);
  }

  public String getReagentReagentIdentifier(int screenIndex, int reagentIndex, Current current) {
    return metadataObject.getReagentReagentIdentifier(screenIndex, reagentIndex);
  }

  // - Rect property retrieval -

  public String getRectHeight(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectHeight(imageIndex, roiIndex, shapeIndex);
  }

  public String getRectID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectID(imageIndex, roiIndex, shapeIndex);
  }

  public String getRectTransform(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectTransform(imageIndex, roiIndex, shapeIndex);
  }

  public String getRectWidth(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectWidth(imageIndex, roiIndex, shapeIndex);
  }

  public String getRectX(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectX(imageIndex, roiIndex, shapeIndex);
  }

  public String getRectY(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectY(imageIndex, roiIndex, shapeIndex);
  }

  // - Region property retrieval -

  public String getRegionID(int imageIndex, int regionIndex, Current current) {
    return metadataObject.getRegionID(imageIndex, regionIndex);
  }

  public String getRegionName(int imageIndex, int regionIndex, Current current) {
    return metadataObject.getRegionName(imageIndex, regionIndex);
  }

  public String getRegionTag(int imageIndex, int regionIndex, Current current) {
    return metadataObject.getRegionTag(imageIndex, regionIndex);
  }

  // - RoiLink property retrieval -

  public String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    return metadataObject.getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex);
  }

  public String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    return metadataObject.getRoiLinkName(imageIndex, roiIndex, roiLinkIndex);
  }

  public String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, Current current) {
    return metadataObject.getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex);
  }

  // - Screen property retrieval -

  public String getScreenDescription(int screenIndex, Current current) {
    return metadataObject.getScreenDescription(screenIndex);
  }

  public String getScreenExtern(int screenIndex, Current current) {
    return metadataObject.getScreenExtern(screenIndex);
  }

  public String getScreenID(int screenIndex, Current current) {
    return metadataObject.getScreenID(screenIndex);
  }

  public String getScreenName(int screenIndex, Current current) {
    return metadataObject.getScreenName(screenIndex);
  }

  public String getScreenProtocolDescription(int screenIndex, Current current) {
    return metadataObject.getScreenProtocolDescription(screenIndex);
  }

  public String getScreenProtocolIdentifier(int screenIndex, Current current) {
    return metadataObject.getScreenProtocolIdentifier(screenIndex);
  }

  public String getScreenReagentSetDescription(int screenIndex, Current current) {
    return metadataObject.getScreenReagentSetDescription(screenIndex);
  }

  public String getScreenReagentSetIdentifier(int screenIndex, Current current) {
    return metadataObject.getScreenReagentSetIdentifier(screenIndex);
  }

  public String getScreenType(int screenIndex, Current current) {
    return metadataObject.getScreenType(screenIndex);
  }

  // - ScreenAcquisition property retrieval -

  public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, Current current) {
    return metadataObject.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex);
  }

  public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, Current current) {
    return metadataObject.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex);
  }

  public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, Current current) {
    return metadataObject.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex);
  }

  // - ScreenRef property retrieval -

  public String getScreenRefID(int plateIndex, int screenRefIndex, Current current) {
    return metadataObject.getScreenRefID(plateIndex, screenRefIndex);
  }

  // - Shape property retrieval -

  public String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeBaselineShift(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeDirection(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFillColor(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFillOpacity(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFillRule(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontFamily(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontSize(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontStretch(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontStyle(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontVariant(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeFontWeight(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeG(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeG(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeID(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeID(imageIndex, roiIndex, shapeIndex);
  }

  public boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeLocked(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeColor(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex);
  }

  public double getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeText(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeText(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTextAnchor(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTextDecoration(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTextFill(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTextStroke(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTheT(imageIndex, roiIndex, shapeIndex);
  }

  public int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeTheZ(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeVectorEffect(imageIndex, roiIndex, shapeIndex);
  }

  public boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeVisibility(imageIndex, roiIndex, shapeIndex);
  }

  public String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeWritingMode(imageIndex, roiIndex, shapeIndex);
  }

  // - StageLabel property retrieval -

  public String getStageLabelName(int imageIndex, Current current) {
    return metadataObject.getStageLabelName(imageIndex);
  }

  public double getStageLabelX(int imageIndex, Current current) {
    return metadataObject.getStageLabelX(imageIndex);
  }

  public double getStageLabelY(int imageIndex, Current current) {
    return metadataObject.getStageLabelY(imageIndex);
  }

  public double getStageLabelZ(int imageIndex, Current current) {
    return metadataObject.getStageLabelZ(imageIndex);
  }

  // - StagePosition property retrieval -

  public double getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex);
  }

  public double getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex);
  }

  public double getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex);
  }

  // - Thumbnail property retrieval -

  public String getThumbnailHref(int imageIndex, Current current) {
    return metadataObject.getThumbnailHref(imageIndex);
  }

  public String getThumbnailID(int imageIndex, Current current) {
    return metadataObject.getThumbnailID(imageIndex);
  }

  public String getThumbnailMIMEtype(int imageIndex, Current current) {
    return metadataObject.getThumbnailMIMEtype(imageIndex);
  }

  // - TiffData property retrieval -

  public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex);
  }

  // - TransmittanceRange property retrieval -

  public int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
  }

  public int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
  }

  public int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
  }

  public int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
  }

  public int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
  }

  // - Well property retrieval -

  public int getWellColumn(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellColumn(plateIndex, wellIndex);
  }

  public String getWellExternalDescription(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellExternalDescription(plateIndex, wellIndex);
  }

  public String getWellExternalIdentifier(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellExternalIdentifier(plateIndex, wellIndex);
  }

  public String getWellID(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellID(plateIndex, wellIndex);
  }

  public String getWellReagent(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellReagent(plateIndex, wellIndex);
  }

  public int getWellRow(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellRow(plateIndex, wellIndex);
  }

  public String getWellType(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellType(plateIndex, wellIndex);
  }

  // - WellSample property retrieval -

  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
  }

  public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
  }

  public double getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex);
  }

  public double getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
  }

  // - WellSampleRef property retrieval -

  public String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Current current) {
    return metadataObject.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
  }

  // -- MetadataStore methods --

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

  public void setDetectorAmplificationGain(double amplificationGain, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
  }

  public void setDetectorGain(double gain, int instrumentIndex, int detectorIndex, Current current) {
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

  public void setDetectorOffset(double offset, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
  }

  public void setDetectorType(String type, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  public void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  public void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsGain(double gain, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsOffset(double offset, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsVoltage(double voltage, int imageIndex, int logicalChannelIndex, Current current) {
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

  public void setDimensionsPhysicalSizeX(double physicalSizeX, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeY(double physicalSizeY, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeZ(double physicalSizeZ, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  public void setDimensionsTimeIncrement(double timeIncrement, int imageIndex, int pixelsIndex, Current current) {
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

  public void setDisplayOptionsZoom(double zoom, int imageIndex, Current current) {
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

  public void setImagingEnvironmentAirPressure(double airPressure, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  public void setImagingEnvironmentCO2Percent(double cO2Percent, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  public void setImagingEnvironmentHumidity(double humidity, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  public void setImagingEnvironmentTemperature(double temperature, int imageIndex, Current current) {
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

  public void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int lightSourceIndex, Current current) {
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

  public void setLightSourcePower(double power, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
  }

  // - LightSourceRef property storage -

  public void setLightSourceRefAttenuation(double attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    metadataObject.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(double attenuation, int imageIndex, int logicalChannelIndex, Current current) {
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

  public void setLogicalChannelNdFilter(double ndFilter, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPinholeSize(double pinholeSize, int imageIndex, int logicalChannelIndex, Current current) {
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

  public void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex, Current current) {
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

  public void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex, Current current) {
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

  public void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex, Current current) {
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

  public void setObjectiveWorkingDistance(double workingDistance, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // - ObjectiveSettings property storage -

  public void setObjectiveSettingsCorrectionCollar(double correctionCollar, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
  }

  public void setObjectiveSettingsMedium(String medium, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsMedium(medium, imageIndex);
  }

  public void setObjectiveSettingsObjective(String objective, int imageIndex, Current current) {
    metadataObject.setObjectiveSettingsObjective(objective, imageIndex);
  }

  public void setObjectiveSettingsRefractiveIndex(double refractiveIndex, int imageIndex, Current current) {
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

  public void setPlaneTimingDeltaT(double deltaT, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTimingExposureTime(double exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
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

  public void setShapeStrokeOpacity(double strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, Current current) {
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

  public void setStageLabelX(double x, int imageIndex, Current current) {
    metadataObject.setStageLabelX(x, imageIndex);
  }

  public void setStageLabelY(double y, int imageIndex, Current current) {
    metadataObject.setStageLabelY(y, imageIndex);
  }

  public void setStageLabelZ(double z, int imageIndex, Current current) {
    metadataObject.setStageLabelZ(z, imageIndex);
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(double positionX, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionY(double positionY, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionZ(double positionZ, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
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

  public void setWellSamplePosX(double posX, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosY(double posY, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
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
