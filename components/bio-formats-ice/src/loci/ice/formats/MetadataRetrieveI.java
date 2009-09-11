//
// MetadataRetrieveI.java
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
//import loci.ice.formats._MetadataRetrieveDisp;
//import loci.formats.*;
//import loci.formats.meta.*;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.meta.MetadataRetrieve} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataRetrieveI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataRetrieveI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataRetrieveI extends _MetadataRetrieveDisp {

  // -- Fields --

  private IMetadata metadataObject;

  // -- Constructor --

  public MetadataRetrieveI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  // -- MetadataRetrieveI methods --

  public loci.formats.meta.MetadataRetrieve getWrappedObject() {
    return metadataObject;
  }

  // -- MetadataRetrieve methods --

  public MetadataRetrieveI getServant(Current current) {
    return this;
  }

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

  public float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
  }

  public float getDetectorGain(int instrumentIndex, int detectorIndex, Current current) {
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

  public float getDetectorOffset(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorOffset(instrumentIndex, detectorIndex);
  }

  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorSerialNumber(instrumentIndex, detectorIndex);
  }

  public String getDetectorType(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorType(instrumentIndex, detectorIndex);
  }

  public float getDetectorVoltage(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorVoltage(instrumentIndex, detectorIndex);
  }

  public float getDetectorZoom(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorZoom(instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property retrieval -

  public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsBinning(imageIndex, logicalChannelIndex);
  }

  public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, Current current) {
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

  public float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex);
  }

  public float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex);
  }

  public float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Current current) {
    return metadataObject.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex);
  }

  public float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Current current) {
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

  public float getDisplayOptionsZoom(int imageIndex, Current current) {
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

  public float getImagingEnvironmentAirPressure(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentAirPressure(imageIndex);
  }

  public float getImagingEnvironmentCO2Percent(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentCO2Percent(imageIndex);
  }

  public float getImagingEnvironmentHumidity(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentHumidity(imageIndex);
  }

  public float getImagingEnvironmentTemperature(int imageIndex, Current current) {
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

  public boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, Current current) {
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

  public float getLightSourcePower(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourcePower(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Current current) {
    return metadataObject.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex);
  }

  // - LightSourceRef property retrieval -

  public float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  public int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Current current) {
    return metadataObject.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  // - LightSourceSettings property retrieval -

  public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Current current) {
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

  public float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelOTF(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Current current) {
    return metadataObject.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex);
  }

  public float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Current current) {
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

  public String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, Current current) {
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

  public float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Current current) {
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

  public float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Current current) {
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

  public float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
  }

  // - ObjectiveSettings property retrieval -

  public float getObjectiveSettingsCorrectionCollar(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsCorrectionCollar(imageIndex);
  }

  public String getObjectiveSettingsMedium(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsMedium(imageIndex);
  }

  public String getObjectiveSettingsObjective(int imageIndex, Current current) {
    return metadataObject.getObjectiveSettingsObjective(imageIndex);
  }

  public float getObjectiveSettingsRefractiveIndex(int imageIndex, Current current) {
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

  public float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex);
  }

  public float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
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

  public float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, Current current) {
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

  public float getStageLabelX(int imageIndex, Current current) {
    return metadataObject.getStageLabelX(imageIndex);
  }

  public float getStageLabelY(int imageIndex, Current current) {
    return metadataObject.getStageLabelY(imageIndex);
  }

  public float getStageLabelZ(int imageIndex, Current current) {
    return metadataObject.getStageLabelZ(imageIndex);
  }

  // - StagePosition property retrieval -

  public float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex);
  }

  public float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    return metadataObject.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex);
  }

  public float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Current current) {
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

  public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex);
  }

  public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
  }

  // - WellSampleRef property retrieval -

  public String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Current current) {
    return metadataObject.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
  }

}
