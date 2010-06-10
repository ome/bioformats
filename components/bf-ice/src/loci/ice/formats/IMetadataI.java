//
// IMetadataI.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2010 UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by melissa via MetadataAutogen on Jun 10, 2010 11:37:54 AM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

import Ice.Current;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

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
  private OMEXMLService service;

  // -- Constructor --

  public IMetadataI() {
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      metadataObject = service.createOMEXMLMetadata();
    }
    catch (DependencyException de) { }
    catch (ServiceException se) { }
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
    try {
      return service.getOMEXML(metadataObject);
    }
    catch (ServiceException se) { }
    return null;
  }

  // - Entity counting -

  public int getBooleanAnnotationCount(Current current) {
    return metadataObject.getBooleanAnnotationCount();
  }

  public int getChannelCount(int imageIndex, Current current) {
    return metadataObject.getChannelCount(imageIndex);
  }

  public int getChannelAnnotationRefCount(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelAnnotationRefCount(imageIndex, channelIndex);
  }

  public int getDatasetCount(Current current) {
    return metadataObject.getDatasetCount();
  }

  public int getDatasetAnnotationRefCount(int datasetIndex, Current current) {
    return metadataObject.getDatasetAnnotationRefCount(datasetIndex);
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

  public int getDoubleAnnotationCount(Current current) {
    return metadataObject.getDoubleAnnotationCount();
  }

  public int getExperimentCount(Current current) {
    return metadataObject.getExperimentCount();
  }

  public int getExperimenterCount(Current current) {
    return metadataObject.getExperimenterCount();
  }

  public int getExperimenterAnnotationRefCount(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterAnnotationRefCount(experimenterIndex);
  }

  public int getExperimenterGroupRefCount(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterGroupRefCount(experimenterIndex);
  }

  public int getFileAnnotationCount(Current current) {
    return metadataObject.getFileAnnotationCount();
  }

  public int getFilterCount(int instrumentIndex, Current current) {
    return metadataObject.getFilterCount(instrumentIndex);
  }

  public int getFilterSetCount(int instrumentIndex, Current current) {
    return metadataObject.getFilterSetCount(instrumentIndex);
  }

  public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetEmissionFilterRefCount(instrumentIndex, filterSetIndex);
  }

  public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetExcitationFilterRefCount(instrumentIndex, filterSetIndex);
  }

  public int getGroupCount(Current current) {
    return metadataObject.getGroupCount();
  }

  public int getImageCount(Current current) {
    return metadataObject.getImageCount();
  }

  public int getImageAnnotationRefCount(int imageIndex, Current current) {
    return metadataObject.getImageAnnotationRefCount(imageIndex);
  }

  public int getImageROIRefCount(int imageIndex, Current current) {
    return metadataObject.getImageROIRefCount(imageIndex);
  }

  public int getInstrumentCount(Current current) {
    return metadataObject.getInstrumentCount();
  }

  public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getLightPathEmissionFilterRefCount(imageIndex, channelIndex);
  }

  public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getLightPathExcitationFilterRefCount(imageIndex, channelIndex);
  }

  public int getListAnnotationCount(Current current) {
    return metadataObject.getListAnnotationCount();
  }

  public int getListAnnotationAnnotationRefCount(int listAnnotationIndex, Current current) {
    return metadataObject.getListAnnotationAnnotationRefCount(listAnnotationIndex);
  }

  public int getLongAnnotationCount(Current current) {
    return metadataObject.getLongAnnotationCount();
  }

  public int getMicrobeamManipulationCount(int experimentIndex, Current current) {
    return metadataObject.getMicrobeamManipulationCount(experimentIndex);
  }

  public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationLightSourceSettingsCount(experimentIndex, microbeamManipulationIndex);
  }

  public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationROIRefCount(experimentIndex, microbeamManipulationIndex);
  }

  public int getMicrobeamManipulationRefCount(int imageIndex, Current current) {
    return metadataObject.getMicrobeamManipulationRefCount(imageIndex);
  }

  public int getOTFCount(int instrumentIndex, Current current) {
    return metadataObject.getOTFCount(instrumentIndex);
  }

  public int getObjectiveCount(int instrumentIndex, Current current) {
    return metadataObject.getObjectiveCount(instrumentIndex);
  }

  public int getPixelsAnnotationRefCount(int imageIndex, Current current) {
    return metadataObject.getPixelsAnnotationRefCount(imageIndex);
  }

  public int getPixelsBinDataCount(int imageIndex, Current current) {
    return metadataObject.getPixelsBinDataCount(imageIndex);
  }

  public int getPlaneCount(int imageIndex, Current current) {
    return metadataObject.getPlaneCount(imageIndex);
  }

  public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneAnnotationRefCount(imageIndex, planeIndex);
  }

  public int getPlateCount(Current current) {
    return metadataObject.getPlateCount();
  }

  public int getPlateAcquisitionCount(int plateIndex, Current current) {
    return metadataObject.getPlateAcquisitionCount(plateIndex);
  }

  public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionAnnotationRefCount(plateIndex, plateAcquisitionIndex);
  }

  public int getPlateAnnotationRefCount(int plateIndex, Current current) {
    return metadataObject.getPlateAnnotationRefCount(plateIndex);
  }

  public int getPlateRefCount(int screenIndex, Current current) {
    return metadataObject.getPlateRefCount(screenIndex);
  }

  public int getProjectCount(Current current) {
    return metadataObject.getProjectCount();
  }

  public int getProjectAnnotationRefCount(int projectIndex, Current current) {
    return metadataObject.getProjectAnnotationRefCount(projectIndex);
  }

  public int getProjectRefCount(int datasetIndex, Current current) {
    return metadataObject.getProjectRefCount(datasetIndex);
  }

  public int getROICount(Current current) {
    return metadataObject.getROICount();
  }

  public int getROIAnnotationRefCount(int roiIndex, Current current) {
    return metadataObject.getROIAnnotationRefCount(roiIndex);
  }

  public int getReagentCount(int screenIndex, Current current) {
    return metadataObject.getReagentCount(screenIndex);
  }

  public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex, Current current) {
    return metadataObject.getReagentAnnotationRefCount(screenIndex, reagentIndex);
  }

  public int getScreenCount(Current current) {
    return metadataObject.getScreenCount();
  }

  public int getScreenAnnotationRefCount(int screenIndex, Current current) {
    return metadataObject.getScreenAnnotationRefCount(screenIndex);
  }

  public int getScreenRefCount(int plateIndex, Current current) {
    return metadataObject.getScreenRefCount(plateIndex);
  }

  public int getShapeAnnotationRefCount(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getShapeAnnotationRefCount(roiIndex, shapeIndex);
  }

  public int getStringAnnotationCount(Current current) {
    return metadataObject.getStringAnnotationCount();
  }

  public int getTiffDataCount(int imageIndex, Current current) {
    return metadataObject.getTiffDataCount(imageIndex);
  }

  public int getTimestampAnnotationCount(Current current) {
    return metadataObject.getTimestampAnnotationCount();
  }

  public int getWellCount(int plateIndex, Current current) {
    return metadataObject.getWellCount(plateIndex);
  }

  public int getWellAnnotationRefCount(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellAnnotationRefCount(plateIndex, wellIndex);
  }

  public int getWellSampleCount(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellSampleCount(plateIndex, wellIndex);
  }

  public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleAnnotationRefCount(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getWellSampleRefCount(plateIndex, plateAcquisitionIndex);
  }

  public int getXMLAnnotationCount(Current current) {
    return metadataObject.getXMLAnnotationCount();
  }

  // - Entity retrieval -

  public String getUUID(Current current) {
    return metadataObject.getUUID();
  }

  // - Arc property retrieval -

  public String getArcID(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcID(instrumentIndex, arcIndex);
  }

  public String getArcLotNumber(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcLotNumber(instrumentIndex, arcIndex);
  }

  public String getArcManufacturer(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcManufacturer(instrumentIndex, arcIndex);
  }

  public String getArcModel(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcModel(instrumentIndex, arcIndex);
  }

  public double getArcPower(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcPower(instrumentIndex, arcIndex);
  }

  public String getArcSerialNumber(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcSerialNumber(instrumentIndex, arcIndex);
  }

  public ArcType getArcType(int instrumentIndex, int arcIndex, Current current) {
    return metadataObject.getArcType(instrumentIndex, arcIndex);
  }

  // - BooleanAnnotation property retrieval -

  public String getBooleanAnnotationID(int booleanAnnotationIndex, Current current) {
    return metadataObject.getBooleanAnnotationID(booleanAnnotationIndex);
  }

  public String getBooleanAnnotationNamespace(int booleanAnnotationIndex, Current current) {
    return metadataObject.getBooleanAnnotationNamespace(booleanAnnotationIndex);
  }

  public boolean getBooleanAnnotationValue(int booleanAnnotationIndex, Current current) {
    return metadataObject.getBooleanAnnotationValue(booleanAnnotationIndex);
  }

  // - Channel property retrieval -

  public AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelAcquisitionMode(imageIndex, channelIndex);
  }

  public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex, Current current) {
    return metadataObject.getChannelAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
  }

  public int getChannelColor(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelColor(imageIndex, channelIndex);
  }

  public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelContrastMethod(imageIndex, channelIndex);
  }

  public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelEmissionWavelength(imageIndex, channelIndex);
  }

  public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelExcitationWavelength(imageIndex, channelIndex);
  }

  public String getChannelFilterSetRef(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelFilterSetRef(imageIndex, channelIndex);
  }

  public String getChannelFluor(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelFluor(imageIndex, channelIndex);
  }

  public String getChannelID(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelID(imageIndex, channelIndex);
  }

  public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelIlluminationType(imageIndex, channelIndex);
  }

  public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelLightSourceSettingsAttenuation(imageIndex, channelIndex);
  }

  public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelLightSourceSettingsID(imageIndex, channelIndex);
  }

  public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
  }

  public double getChannelNDFilter(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelNDFilter(imageIndex, channelIndex);
  }

  public String getChannelName(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelName(imageIndex, channelIndex);
  }

  public String getChannelOTFRef(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelOTFRef(imageIndex, channelIndex);
  }

  public double getChannelPinholeSize(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelPinholeSize(imageIndex, channelIndex);
  }

  public int getChannelPockelCellSetting(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelPockelCellSetting(imageIndex, channelIndex);
  }

  public int getChannelSamplesPerPixel(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getChannelSamplesPerPixel(imageIndex, channelIndex);
  }

  // - ChannelAnnotationRef property retrieval -

  // - Dataset property retrieval -

  public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex, Current current) {
    return metadataObject.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
  }

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

  public String getDatasetName(int datasetIndex, Current current) {
    return metadataObject.getDatasetName(datasetIndex);
  }

  public String getDatasetProjectRef(int datasetIndex, int projectRefIndex, Current current) {
    return metadataObject.getDatasetProjectRef(datasetIndex, projectRefIndex);
  }

  // - DatasetAnnotationRef property retrieval -

  // - DatasetRef property retrieval -

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

  public String getDetectorLotNumber(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorLotNumber(instrumentIndex, detectorIndex);
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

  public DetectorType getDetectorType(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorType(instrumentIndex, detectorIndex);
  }

  public double getDetectorVoltage(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorVoltage(instrumentIndex, detectorIndex);
  }

  public double getDetectorZoom(int instrumentIndex, int detectorIndex, Current current) {
    return metadataObject.getDetectorZoom(instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property retrieval -

  public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsBinning(imageIndex, channelIndex);
  }

  public double getDetectorSettingsGain(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsGain(imageIndex, channelIndex);
  }

  public String getDetectorSettingsID(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsID(imageIndex, channelIndex);
  }

  public double getDetectorSettingsOffset(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsOffset(imageIndex, channelIndex);
  }

  public double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
  }

  public double getDetectorSettingsVoltage(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getDetectorSettingsVoltage(imageIndex, channelIndex);
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

  public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex, Current current) {
    return metadataObject.getDichroicSerialNumber(instrumentIndex, dichroicIndex);
  }

  // - DoubleAnnotation property retrieval -

  public String getDoubleAnnotationID(int doubleAnnotationIndex, Current current) {
    return metadataObject.getDoubleAnnotationID(doubleAnnotationIndex);
  }

  public String getDoubleAnnotationNamespace(int doubleAnnotationIndex, Current current) {
    return metadataObject.getDoubleAnnotationNamespace(doubleAnnotationIndex);
  }

  public double getDoubleAnnotationValue(int doubleAnnotationIndex, Current current) {
    return metadataObject.getDoubleAnnotationValue(doubleAnnotationIndex);
  }

  // - Ellipse property retrieval -

  public String getEllipseDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseDescription(roiIndex, shapeIndex);
  }

  public int getEllipseFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseFill(roiIndex, shapeIndex);
  }

  public int getEllipseFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseFontSize(roiIndex, shapeIndex);
  }

  public String getEllipseID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseID(roiIndex, shapeIndex);
  }

  public String getEllipseLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseLabel(roiIndex, shapeIndex);
  }

  public String getEllipseName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseName(roiIndex, shapeIndex);
  }

  public double getEllipseRadiusX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseRadiusX(roiIndex, shapeIndex);
  }

  public double getEllipseRadiusY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseRadiusY(roiIndex, shapeIndex);
  }

  public int getEllipseStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseStroke(roiIndex, shapeIndex);
  }

  public String getEllipseStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getEllipseStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseStrokeWidth(roiIndex, shapeIndex);
  }

  public int getEllipseTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseTheC(roiIndex, shapeIndex);
  }

  public int getEllipseTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseTheT(roiIndex, shapeIndex);
  }

  public int getEllipseTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseTheZ(roiIndex, shapeIndex);
  }

  public String getEllipseTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseTransform(roiIndex, shapeIndex);
  }

  public double getEllipseX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseX(roiIndex, shapeIndex);
  }

  public double getEllipseY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getEllipseY(roiIndex, shapeIndex);
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

  public ExperimentType getExperimentType(int experimentIndex, Current current) {
    return metadataObject.getExperimentType(experimentIndex);
  }

  // - Experimenter property retrieval -

  public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex, Current current) {
    return metadataObject.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
  }

  public String getExperimenterDisplayName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterDisplayName(experimenterIndex);
  }

  public String getExperimenterEmail(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterEmail(experimenterIndex);
  }

  public String getExperimenterFirstName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterFirstName(experimenterIndex);
  }

  public String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex, Current current) {
    return metadataObject.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
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

  public String getExperimenterMiddleName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterMiddleName(experimenterIndex);
  }

  public String getExperimenterUserName(int experimenterIndex, Current current) {
    return metadataObject.getExperimenterUserName(experimenterIndex);
  }

  // - ExperimenterAnnotationRef property retrieval -

  // - ExperimenterGroupRef property retrieval -

  // - Filament property retrieval -

  public String getFilamentID(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentID(instrumentIndex, filamentIndex);
  }

  public String getFilamentLotNumber(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentLotNumber(instrumentIndex, filamentIndex);
  }

  public String getFilamentManufacturer(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentManufacturer(instrumentIndex, filamentIndex);
  }

  public String getFilamentModel(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentModel(instrumentIndex, filamentIndex);
  }

  public double getFilamentPower(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentPower(instrumentIndex, filamentIndex);
  }

  public String getFilamentSerialNumber(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentSerialNumber(instrumentIndex, filamentIndex);
  }

  public FilamentType getFilamentType(int instrumentIndex, int filamentIndex, Current current) {
    return metadataObject.getFilamentType(instrumentIndex, filamentIndex);
  }

  // - FileAnnotation property retrieval -

  public String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex, Current current) {
    return metadataObject.getFileAnnotationBinaryFileFileName(fileAnnotationIndex);
  }

  public String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex, Current current) {
    return metadataObject.getFileAnnotationBinaryFileMIMEType(fileAnnotationIndex);
  }

  public int getFileAnnotationBinaryFileSize(int fileAnnotationIndex, Current current) {
    return metadataObject.getFileAnnotationBinaryFileSize(fileAnnotationIndex);
  }

  public String getFileAnnotationID(int fileAnnotationIndex, Current current) {
    return metadataObject.getFileAnnotationID(fileAnnotationIndex);
  }

  public String getFileAnnotationNamespace(int fileAnnotationIndex, Current current) {
    return metadataObject.getFileAnnotationNamespace(fileAnnotationIndex);
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

  public String getFilterSerialNumber(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterSerialNumber(instrumentIndex, filterIndex);
  }

  public FilterType getFilterType(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getFilterType(instrumentIndex, filterIndex);
  }

  // - FilterSet property retrieval -

  public String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetDichroicRef(instrumentIndex, filterSetIndex);
  }

  public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex, Current current) {
    return metadataObject.getFilterSetEmissionFilterRef(instrumentIndex, filterSetIndex, emissionFilterRefIndex);
  }

  public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex, Current current) {
    return metadataObject.getFilterSetExcitationFilterRef(instrumentIndex, filterSetIndex, excitationFilterRefIndex);
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

  public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex, Current current) {
    return metadataObject.getFilterSetSerialNumber(instrumentIndex, filterSetIndex);
  }

  // - FilterSetEmissionFilterRef property retrieval -

  // - FilterSetExcitationFilterRef property retrieval -

  // - Group property retrieval -

  public String getGroupContact(int groupIndex, Current current) {
    return metadataObject.getGroupContact(groupIndex);
  }

  public String getGroupDescription(int groupIndex, Current current) {
    return metadataObject.getGroupDescription(groupIndex);
  }

  public String getGroupID(int groupIndex, Current current) {
    return metadataObject.getGroupID(groupIndex);
  }

  public String getGroupLeader(int groupIndex, Current current) {
    return metadataObject.getGroupLeader(groupIndex);
  }

  public String getGroupName(int groupIndex, Current current) {
    return metadataObject.getGroupName(groupIndex);
  }

  // - Image property retrieval -

  public String getImageAcquiredDate(int imageIndex, Current current) {
    return metadataObject.getImageAcquiredDate(imageIndex);
  }

  public String getImageAnnotationRef(int imageIndex, int annotationRefIndex, Current current) {
    return metadataObject.getImageAnnotationRef(imageIndex, annotationRefIndex);
  }

  public String getImageDatasetRef(int imageIndex, int datasetRefIndex, Current current) {
    return metadataObject.getImageDatasetRef(imageIndex, datasetRefIndex);
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

  public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex, Current current) {
    return metadataObject.getImageMicrobeamManipulationRef(imageIndex, microbeamManipulationRefIndex);
  }

  public String getImageName(int imageIndex, Current current) {
    return metadataObject.getImageName(imageIndex);
  }

  public double getImageObjectiveSettingsCorrectionCollar(int imageIndex, Current current) {
    return metadataObject.getImageObjectiveSettingsCorrectionCollar(imageIndex);
  }

  public String getImageObjectiveSettingsID(int imageIndex, Current current) {
    return metadataObject.getImageObjectiveSettingsID(imageIndex);
  }

  public Medium getImageObjectiveSettingsMedium(int imageIndex, Current current) {
    return metadataObject.getImageObjectiveSettingsMedium(imageIndex);
  }

  public double getImageObjectiveSettingsRefractiveIndex(int imageIndex, Current current) {
    return metadataObject.getImageObjectiveSettingsRefractiveIndex(imageIndex);
  }

  public String getImageROIRef(int imageIndex, int roiRefIndex, Current current) {
    return metadataObject.getImageROIRef(imageIndex, roiRefIndex);
  }

  // - ImageAnnotationRef property retrieval -

  // - ImageROIRef property retrieval -

  // - ImagingEnvironment property retrieval -

  public double getImagingEnvironmentAirPressure(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentAirPressure(imageIndex);
  }

  public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex, Current current) {
    return metadataObject.getImagingEnvironmentCO2Percent(imageIndex);
  }

  public PercentFraction getImagingEnvironmentHumidity(int imageIndex, Current current) {
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

  public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserFrequencyMultiplication(instrumentIndex, laserIndex);
  }

  public String getLaserID(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserID(instrumentIndex, laserIndex);
  }

  public LaserMedium getLaserLaserMedium(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserLaserMedium(instrumentIndex, laserIndex);
  }

  public String getLaserLotNumber(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserLotNumber(instrumentIndex, laserIndex);
  }

  public String getLaserManufacturer(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserManufacturer(instrumentIndex, laserIndex);
  }

  public String getLaserModel(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserModel(instrumentIndex, laserIndex);
  }

  public boolean getLaserPockelCell(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserPockelCell(instrumentIndex, laserIndex);
  }

  public double getLaserPower(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserPower(instrumentIndex, laserIndex);
  }

  public Pulse getLaserPulse(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserPulse(instrumentIndex, laserIndex);
  }

  public String getLaserPump(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserPump(instrumentIndex, laserIndex);
  }

  public double getLaserRepetitionRate(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserRepetitionRate(instrumentIndex, laserIndex);
  }

  public String getLaserSerialNumber(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserSerialNumber(instrumentIndex, laserIndex);
  }

  public boolean getLaserTuneable(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserTuneable(instrumentIndex, laserIndex);
  }

  public LaserType getLaserType(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserType(instrumentIndex, laserIndex);
  }

  public PositiveInteger getLaserWavelength(int instrumentIndex, int laserIndex, Current current) {
    return metadataObject.getLaserWavelength(instrumentIndex, laserIndex);
  }

  // - LightEmittingDiode property retrieval -

  public String getLightEmittingDiodeID(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodeID(instrumentIndex, lightEmittingDiodeIndex);
  }

  public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodeLotNumber(instrumentIndex, lightEmittingDiodeIndex);
  }

  public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodeManufacturer(instrumentIndex, lightEmittingDiodeIndex);
  }

  public String getLightEmittingDiodeModel(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodeModel(instrumentIndex, lightEmittingDiodeIndex);
  }

  public double getLightEmittingDiodePower(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodePower(instrumentIndex, lightEmittingDiodeIndex);
  }

  public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    return metadataObject.getLightEmittingDiodeSerialNumber(instrumentIndex, lightEmittingDiodeIndex);
  }

  // - LightPath property retrieval -

  public String getLightPathDichroicRef(int imageIndex, int channelIndex, Current current) {
    return metadataObject.getLightPathDichroicRef(imageIndex, channelIndex);
  }

  public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex, Current current) {
    return metadataObject.getLightPathEmissionFilterRef(imageIndex, channelIndex, emissionFilterRefIndex);
  }

  public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex, Current current) {
    return metadataObject.getLightPathExcitationFilterRef(imageIndex, channelIndex, excitationFilterRefIndex);
  }

  // - LightPathEmissionFilterRef property retrieval -

  // - LightPathExcitationFilterRef property retrieval -

  // - Line property retrieval -

  public String getLineDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineDescription(roiIndex, shapeIndex);
  }

  public int getLineFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineFill(roiIndex, shapeIndex);
  }

  public int getLineFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineFontSize(roiIndex, shapeIndex);
  }

  public String getLineID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineID(roiIndex, shapeIndex);
  }

  public String getLineLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineLabel(roiIndex, shapeIndex);
  }

  public String getLineName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineName(roiIndex, shapeIndex);
  }

  public int getLineStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineStroke(roiIndex, shapeIndex);
  }

  public String getLineStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getLineStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineStrokeWidth(roiIndex, shapeIndex);
  }

  public int getLineTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineTheC(roiIndex, shapeIndex);
  }

  public int getLineTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineTheT(roiIndex, shapeIndex);
  }

  public int getLineTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineTheZ(roiIndex, shapeIndex);
  }

  public String getLineTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineTransform(roiIndex, shapeIndex);
  }

  public double getLineX1(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineX1(roiIndex, shapeIndex);
  }

  public double getLineX2(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineX2(roiIndex, shapeIndex);
  }

  public double getLineY1(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineY1(roiIndex, shapeIndex);
  }

  public double getLineY2(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getLineY2(roiIndex, shapeIndex);
  }

  // - ListAnnotation property retrieval -

  public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex, Current current) {
    return metadataObject.getListAnnotationAnnotationRef(listAnnotationIndex, annotationRefIndex);
  }

  public String getListAnnotationID(int listAnnotationIndex, Current current) {
    return metadataObject.getListAnnotationID(listAnnotationIndex);
  }

  public String getListAnnotationNamespace(int listAnnotationIndex, Current current) {
    return metadataObject.getListAnnotationNamespace(listAnnotationIndex);
  }

  // - ListAnnotationAnnotationRef property retrieval -

  // - LongAnnotation property retrieval -

  public String getLongAnnotationID(int longAnnotationIndex, Current current) {
    return metadataObject.getLongAnnotationID(longAnnotationIndex);
  }

  public String getLongAnnotationNamespace(int longAnnotationIndex, Current current) {
    return metadataObject.getLongAnnotationNamespace(longAnnotationIndex);
  }

  public long getLongAnnotationValue(int longAnnotationIndex, Current current) {
    return metadataObject.getLongAnnotationValue(longAnnotationIndex);
  }

  // - Mask property retrieval -

  public String getMaskDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskDescription(roiIndex, shapeIndex);
  }

  public int getMaskFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskFill(roiIndex, shapeIndex);
  }

  public int getMaskFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskFontSize(roiIndex, shapeIndex);
  }

  public String getMaskID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskID(roiIndex, shapeIndex);
  }

  public String getMaskLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskLabel(roiIndex, shapeIndex);
  }

  public String getMaskName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskName(roiIndex, shapeIndex);
  }

  public int getMaskStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskStroke(roiIndex, shapeIndex);
  }

  public String getMaskStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getMaskStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskStrokeWidth(roiIndex, shapeIndex);
  }

  public int getMaskTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskTheC(roiIndex, shapeIndex);
  }

  public int getMaskTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskTheT(roiIndex, shapeIndex);
  }

  public int getMaskTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskTheZ(roiIndex, shapeIndex);
  }

  public String getMaskTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskTransform(roiIndex, shapeIndex);
  }

  public double getMaskX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskX(roiIndex, shapeIndex);
  }

  public double getMaskY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getMaskY(roiIndex, shapeIndex);
  }

  // - MicrobeamManipulation property retrieval -

  public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationExperimenterRef(experimentIndex, microbeamManipulationIndex);
  }

  public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationID(experimentIndex, microbeamManipulationIndex);
  }

  public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int roiRefIndex, Current current) {
    return metadataObject.getMicrobeamManipulationROIRef(experimentIndex, microbeamManipulationIndex, roiRefIndex);
  }

  public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex, Current current) {
    return metadataObject.getMicrobeamManipulationType(experimentIndex, microbeamManipulationIndex);
  }

  // - MicrobeamManipulationLightSourceSettings property retrieval -

  public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    return metadataObject.getMicrobeamManipulationLightSourceSettingsAttenuation(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    return metadataObject.getMicrobeamManipulationLightSourceSettingsID(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    return metadataObject.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  // - MicrobeamManipulationROIRef property retrieval -

  // - MicrobeamManipulationRef property retrieval -

  // - Microscope property retrieval -

  public String getMicroscopeLotNumber(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeLotNumber(instrumentIndex);
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

  public MicroscopeType getMicroscopeType(int instrumentIndex, Current current) {
    return metadataObject.getMicroscopeType(instrumentIndex);
  }

  // - OTF property retrieval -

  public String getOTFBinaryFileFileName(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFBinaryFileFileName(instrumentIndex, otfIndex);
  }

  public String getOTFBinaryFileMIMEType(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFBinaryFileMIMEType(instrumentIndex, otfIndex);
  }

  public int getOTFBinaryFileSize(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFBinaryFileSize(instrumentIndex, otfIndex);
  }

  public String getOTFFilterSetRef(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFFilterSetRef(instrumentIndex, otfIndex);
  }

  public String getOTFID(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFID(instrumentIndex, otfIndex);
  }

  public double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFObjectiveSettingsCorrectionCollar(instrumentIndex, otfIndex);
  }

  public String getOTFObjectiveSettingsID(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFObjectiveSettingsID(instrumentIndex, otfIndex);
  }

  public Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFObjectiveSettingsMedium(instrumentIndex, otfIndex);
  }

  public double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFObjectiveSettingsRefractiveIndex(instrumentIndex, otfIndex);
  }

  public boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
  }

  public PositiveInteger getOTFSizeX(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFSizeX(instrumentIndex, otfIndex);
  }

  public PositiveInteger getOTFSizeY(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFSizeY(instrumentIndex, otfIndex);
  }

  public PixelType getOTFType(int instrumentIndex, int otfIndex, Current current) {
    return metadataObject.getOTFType(instrumentIndex, otfIndex);
  }

  // - Objective property retrieval -

  public double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
  }

  public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveCorrection(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveID(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveID(instrumentIndex, objectiveIndex);
  }

  public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveImmersion(instrumentIndex, objectiveIndex);
  }

  public boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveIris(instrumentIndex, objectiveIndex);
  }

  public double getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveLensNA(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex, Current current) {
    return metadataObject.getObjectiveLotNumber(instrumentIndex, objectiveIndex);
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

  // - Path property retrieval -

  public String getPathDefinition(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathDefinition(roiIndex, shapeIndex);
  }

  public String getPathDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathDescription(roiIndex, shapeIndex);
  }

  public int getPathFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathFill(roiIndex, shapeIndex);
  }

  public int getPathFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathFontSize(roiIndex, shapeIndex);
  }

  public String getPathID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathID(roiIndex, shapeIndex);
  }

  public String getPathLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathLabel(roiIndex, shapeIndex);
  }

  public String getPathName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathName(roiIndex, shapeIndex);
  }

  public int getPathStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathStroke(roiIndex, shapeIndex);
  }

  public String getPathStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getPathStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathStrokeWidth(roiIndex, shapeIndex);
  }

  public int getPathTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathTheC(roiIndex, shapeIndex);
  }

  public int getPathTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathTheT(roiIndex, shapeIndex);
  }

  public int getPathTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathTheZ(roiIndex, shapeIndex);
  }

  public String getPathTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPathTransform(roiIndex, shapeIndex);
  }

  // - Pixels property retrieval -

  public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex, Current current) {
    return metadataObject.getPixelsAnnotationRef(imageIndex, annotationRefIndex);
  }

  public DimensionOrder getPixelsDimensionOrder(int imageIndex, Current current) {
    return metadataObject.getPixelsDimensionOrder(imageIndex);
  }

  public String getPixelsID(int imageIndex, Current current) {
    return metadataObject.getPixelsID(imageIndex);
  }

  public double getPixelsPhysicalSizeX(int imageIndex, Current current) {
    return metadataObject.getPixelsPhysicalSizeX(imageIndex);
  }

  public double getPixelsPhysicalSizeY(int imageIndex, Current current) {
    return metadataObject.getPixelsPhysicalSizeY(imageIndex);
  }

  public double getPixelsPhysicalSizeZ(int imageIndex, Current current) {
    return metadataObject.getPixelsPhysicalSizeZ(imageIndex);
  }

  public PositiveInteger getPixelsSizeC(int imageIndex, Current current) {
    return metadataObject.getPixelsSizeC(imageIndex);
  }

  public PositiveInteger getPixelsSizeT(int imageIndex, Current current) {
    return metadataObject.getPixelsSizeT(imageIndex);
  }

  public PositiveInteger getPixelsSizeX(int imageIndex, Current current) {
    return metadataObject.getPixelsSizeX(imageIndex);
  }

  public PositiveInteger getPixelsSizeY(int imageIndex, Current current) {
    return metadataObject.getPixelsSizeY(imageIndex);
  }

  public PositiveInteger getPixelsSizeZ(int imageIndex, Current current) {
    return metadataObject.getPixelsSizeZ(imageIndex);
  }

  public double getPixelsTimeIncrement(int imageIndex, Current current) {
    return metadataObject.getPixelsTimeIncrement(imageIndex);
  }

  public PixelType getPixelsType(int imageIndex, Current current) {
    return metadataObject.getPixelsType(imageIndex);
  }

  // - PixelsAnnotationRef property retrieval -

  // - PixelsBinData property retrieval -

  public boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex, Current current) {
    return metadataObject.getPixelsBinDataBigEndian(imageIndex, binDataIndex);
  }

  // - Plane property retrieval -

  public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex, Current current) {
    return metadataObject.getPlaneAnnotationRef(imageIndex, planeIndex, annotationRefIndex);
  }

  public double getPlaneDeltaT(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneDeltaT(imageIndex, planeIndex);
  }

  public double getPlaneExposureTime(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneExposureTime(imageIndex, planeIndex);
  }

  public String getPlaneHashSHA1(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneHashSHA1(imageIndex, planeIndex);
  }

  public double getPlanePositionX(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlanePositionX(imageIndex, planeIndex);
  }

  public double getPlanePositionY(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlanePositionY(imageIndex, planeIndex);
  }

  public double getPlanePositionZ(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlanePositionZ(imageIndex, planeIndex);
  }

  public int getPlaneTheC(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheC(imageIndex, planeIndex);
  }

  public int getPlaneTheT(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheT(imageIndex, planeIndex);
  }

  public int getPlaneTheZ(int imageIndex, int planeIndex, Current current) {
    return metadataObject.getPlaneTheZ(imageIndex, planeIndex);
  }

  // - PlaneAnnotationRef property retrieval -

  // - Plate property retrieval -

  public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex, Current current) {
    return metadataObject.getPlateAnnotationRef(plateIndex, annotationRefIndex);
  }

  public NamingConvention getPlateColumnNamingConvention(int plateIndex, Current current) {
    return metadataObject.getPlateColumnNamingConvention(plateIndex);
  }

  public int getPlateColumns(int plateIndex, Current current) {
    return metadataObject.getPlateColumns(plateIndex);
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

  public NamingConvention getPlateRowNamingConvention(int plateIndex, Current current) {
    return metadataObject.getPlateRowNamingConvention(plateIndex);
  }

  public int getPlateRows(int plateIndex, Current current) {
    return metadataObject.getPlateRows(plateIndex);
  }

  public String getPlateScreenRef(int plateIndex, int screenRefIndex, Current current) {
    return metadataObject.getPlateScreenRef(plateIndex, screenRefIndex);
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

  // - PlateAcquisition property retrieval -

  public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex, Current current) {
    return metadataObject.getPlateAcquisitionAnnotationRef(plateIndex, plateAcquisitionIndex, annotationRefIndex);
  }

  public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionDescription(plateIndex, plateAcquisitionIndex);
  }

  public String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
  }

  public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionID(plateIndex, plateAcquisitionIndex);
  }

  public int getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionMaximumFieldCount(plateIndex, plateAcquisitionIndex);
  }

  public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionName(plateIndex, plateAcquisitionIndex);
  }

  public String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex, Current current) {
    return metadataObject.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
  }

  public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex, Current current) {
    return metadataObject.getPlateAcquisitionWellSampleRef(plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
  }

  // - PlateAcquisitionAnnotationRef property retrieval -

  // - PlateAnnotationRef property retrieval -

  // - PlateRef property retrieval -

  // - Point property retrieval -

  public String getPointDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointDescription(roiIndex, shapeIndex);
  }

  public int getPointFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointFill(roiIndex, shapeIndex);
  }

  public int getPointFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointFontSize(roiIndex, shapeIndex);
  }

  public String getPointID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointID(roiIndex, shapeIndex);
  }

  public String getPointLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointLabel(roiIndex, shapeIndex);
  }

  public String getPointName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointName(roiIndex, shapeIndex);
  }

  public int getPointStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointStroke(roiIndex, shapeIndex);
  }

  public String getPointStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getPointStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointStrokeWidth(roiIndex, shapeIndex);
  }

  public int getPointTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointTheC(roiIndex, shapeIndex);
  }

  public int getPointTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointTheT(roiIndex, shapeIndex);
  }

  public int getPointTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointTheZ(roiIndex, shapeIndex);
  }

  public String getPointTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointTransform(roiIndex, shapeIndex);
  }

  public double getPointX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointX(roiIndex, shapeIndex);
  }

  public double getPointY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPointY(roiIndex, shapeIndex);
  }

  // - Polyline property retrieval -

  public boolean getPolylineClosed(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineClosed(roiIndex, shapeIndex);
  }

  public String getPolylineDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineDescription(roiIndex, shapeIndex);
  }

  public int getPolylineFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineFill(roiIndex, shapeIndex);
  }

  public int getPolylineFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineFontSize(roiIndex, shapeIndex);
  }

  public String getPolylineID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineID(roiIndex, shapeIndex);
  }

  public String getPolylineLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineLabel(roiIndex, shapeIndex);
  }

  public String getPolylineName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineName(roiIndex, shapeIndex);
  }

  public String getPolylinePoints(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylinePoints(roiIndex, shapeIndex);
  }

  public int getPolylineStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineStroke(roiIndex, shapeIndex);
  }

  public String getPolylineStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getPolylineStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineStrokeWidth(roiIndex, shapeIndex);
  }

  public int getPolylineTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineTheC(roiIndex, shapeIndex);
  }

  public int getPolylineTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineTheT(roiIndex, shapeIndex);
  }

  public int getPolylineTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineTheZ(roiIndex, shapeIndex);
  }

  public String getPolylineTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getPolylineTransform(roiIndex, shapeIndex);
  }

  // - Project property retrieval -

  public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex, Current current) {
    return metadataObject.getProjectAnnotationRef(projectIndex, annotationRefIndex);
  }

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

  // - ProjectAnnotationRef property retrieval -

  // - ProjectRef property retrieval -

  // - ROI property retrieval -

  public String getROIAnnotationRef(int roiIndex, int annotationRefIndex, Current current) {
    return metadataObject.getROIAnnotationRef(roiIndex, annotationRefIndex);
  }

  public String getROIDescription(int roiIndex, Current current) {
    return metadataObject.getROIDescription(roiIndex);
  }

  public String getROIID(int roiIndex, Current current) {
    return metadataObject.getROIID(roiIndex);
  }

  public String getROIName(int roiIndex, Current current) {
    return metadataObject.getROIName(roiIndex);
  }

  public String getROINamespace(int roiIndex, Current current) {
    return metadataObject.getROINamespace(roiIndex);
  }

  // - ROIAnnotationRef property retrieval -

  // - Reagent property retrieval -

  public String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex, Current current) {
    return metadataObject.getReagentAnnotationRef(screenIndex, reagentIndex, annotationRefIndex);
  }

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

  // - ReagentAnnotationRef property retrieval -

  // - Rectangle property retrieval -

  public String getRectangleDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleDescription(roiIndex, shapeIndex);
  }

  public int getRectangleFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleFill(roiIndex, shapeIndex);
  }

  public int getRectangleFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleFontSize(roiIndex, shapeIndex);
  }

  public double getRectangleHeight(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleHeight(roiIndex, shapeIndex);
  }

  public String getRectangleID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleID(roiIndex, shapeIndex);
  }

  public String getRectangleLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleLabel(roiIndex, shapeIndex);
  }

  public String getRectangleName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleName(roiIndex, shapeIndex);
  }

  public int getRectangleStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleStroke(roiIndex, shapeIndex);
  }

  public String getRectangleStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getRectangleStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleStrokeWidth(roiIndex, shapeIndex);
  }

  public int getRectangleTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleTheC(roiIndex, shapeIndex);
  }

  public int getRectangleTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleTheT(roiIndex, shapeIndex);
  }

  public int getRectangleTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleTheZ(roiIndex, shapeIndex);
  }

  public String getRectangleTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleTransform(roiIndex, shapeIndex);
  }

  public double getRectangleWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleWidth(roiIndex, shapeIndex);
  }

  public double getRectangleX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleX(roiIndex, shapeIndex);
  }

  public double getRectangleY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getRectangleY(roiIndex, shapeIndex);
  }

  // - Screen property retrieval -

  public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex, Current current) {
    return metadataObject.getScreenAnnotationRef(screenIndex, annotationRefIndex);
  }

  public String getScreenDescription(int screenIndex, Current current) {
    return metadataObject.getScreenDescription(screenIndex);
  }

  public String getScreenID(int screenIndex, Current current) {
    return metadataObject.getScreenID(screenIndex);
  }

  public String getScreenName(int screenIndex, Current current) {
    return metadataObject.getScreenName(screenIndex);
  }

  public String getScreenPlateRef(int screenIndex, int plateRefIndex, Current current) {
    return metadataObject.getScreenPlateRef(screenIndex, plateRefIndex);
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

  // - ScreenAnnotationRef property retrieval -

  // - ScreenRef property retrieval -

  // - ShapeAnnotationRef property retrieval -

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

  // - StringAnnotation property retrieval -

  public String getStringAnnotationID(int stringAnnotationIndex, Current current) {
    return metadataObject.getStringAnnotationID(stringAnnotationIndex);
  }

  public String getStringAnnotationNamespace(int stringAnnotationIndex, Current current) {
    return metadataObject.getStringAnnotationNamespace(stringAnnotationIndex);
  }

  public String getStringAnnotationValue(int stringAnnotationIndex, Current current) {
    return metadataObject.getStringAnnotationValue(stringAnnotationIndex);
  }

  // - Text property retrieval -

  public String getTextDescription(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextDescription(roiIndex, shapeIndex);
  }

  public int getTextFill(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextFill(roiIndex, shapeIndex);
  }

  public int getTextFontSize(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextFontSize(roiIndex, shapeIndex);
  }

  public String getTextID(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextID(roiIndex, shapeIndex);
  }

  public String getTextLabel(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextLabel(roiIndex, shapeIndex);
  }

  public String getTextName(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextName(roiIndex, shapeIndex);
  }

  public int getTextStroke(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextStroke(roiIndex, shapeIndex);
  }

  public String getTextStrokeDashArray(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextStrokeDashArray(roiIndex, shapeIndex);
  }

  public double getTextStrokeWidth(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextStrokeWidth(roiIndex, shapeIndex);
  }

  public int getTextTheC(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextTheC(roiIndex, shapeIndex);
  }

  public int getTextTheT(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextTheT(roiIndex, shapeIndex);
  }

  public int getTextTheZ(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextTheZ(roiIndex, shapeIndex);
  }

  public String getTextTransform(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextTransform(roiIndex, shapeIndex);
  }

  public String getTextValue(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextValue(roiIndex, shapeIndex);
  }

  public double getTextX(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextX(roiIndex, shapeIndex);
  }

  public double getTextY(int roiIndex, int shapeIndex, Current current) {
    return metadataObject.getTextY(roiIndex, shapeIndex);
  }

  // - TiffData property retrieval -

  public int getTiffDataFirstC(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstC(imageIndex, tiffDataIndex);
  }

  public int getTiffDataFirstT(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstT(imageIndex, tiffDataIndex);
  }

  public int getTiffDataFirstZ(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataFirstZ(imageIndex, tiffDataIndex);
  }

  public int getTiffDataIFD(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataIFD(imageIndex, tiffDataIndex);
  }

  public int getTiffDataPlaneCount(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getTiffDataPlaneCount(imageIndex, tiffDataIndex);
  }

  // - TimestampAnnotation property retrieval -

  public String getTimestampAnnotationID(int timestampAnnotationIndex, Current current) {
    return metadataObject.getTimestampAnnotationID(timestampAnnotationIndex);
  }

  public String getTimestampAnnotationNamespace(int timestampAnnotationIndex, Current current) {
    return metadataObject.getTimestampAnnotationNamespace(timestampAnnotationIndex);
  }

  public String getTimestampAnnotationValue(int timestampAnnotationIndex, Current current) {
    return metadataObject.getTimestampAnnotationValue(timestampAnnotationIndex);
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

  public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, Current current) {
    return metadataObject.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
  }

  // - UUID property retrieval -

  public String getUUIDFileName(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getUUIDFileName(imageIndex, tiffDataIndex);
  }

  public String getUUIDValue(int imageIndex, int tiffDataIndex, Current current) {
    return metadataObject.getUUIDValue(imageIndex, tiffDataIndex);
  }

  // - Well property retrieval -

  public String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex, Current current) {
    return metadataObject.getWellAnnotationRef(plateIndex, wellIndex, annotationRefIndex);
  }

  public int getWellColor(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellColor(plateIndex, wellIndex);
  }

  public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex, Current current) {
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

  public String getWellReagentRef(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellReagentRef(plateIndex, wellIndex);
  }

  public NonNegativeInteger getWellRow(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellRow(plateIndex, wellIndex);
  }

  public String getWellStatus(int plateIndex, int wellIndex, Current current) {
    return metadataObject.getWellStatus(plateIndex, wellIndex);
  }

  // - WellAnnotationRef property retrieval -

  // - WellSample property retrieval -

  public String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex, Current current) {
    return metadataObject.getWellSampleAnnotationRef(plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
  }

  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
  }

  public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
  }

  public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
  }

  public double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
  }

  public double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    return metadataObject.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
  }

  // - WellSampleAnnotationRef property retrieval -

  // - WellSampleRef property retrieval -

  // - XMLAnnotation property retrieval -

  public String getXMLAnnotationID(int xmlAnnotationIndex, Current current) {
    return metadataObject.getXMLAnnotationID(xmlAnnotationIndex);
  }

  public String getXMLAnnotationNamespace(int xmlAnnotationIndex, Current current) {
    return metadataObject.getXMLAnnotationNamespace(xmlAnnotationIndex);
  }

  public String getXMLAnnotationValue(int xmlAnnotationIndex, Current current) {
    return metadataObject.getXMLAnnotationValue(xmlAnnotationIndex);
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

  public void setArcID(String id, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcID(id, instrumentIndex, arcIndex);
  }

  public void setArcLotNumber(String lotNumber, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcLotNumber(lotNumber, instrumentIndex, arcIndex);
  }

  public void setArcManufacturer(String manufacturer, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcManufacturer(manufacturer, instrumentIndex, arcIndex);
  }

  public void setArcModel(String model, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcModel(model, instrumentIndex, arcIndex);
  }

  public void setArcPower(double power, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcPower(power, instrumentIndex, arcIndex);
  }

  public void setArcSerialNumber(String serialNumber, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcSerialNumber(serialNumber, instrumentIndex, arcIndex);
  }

  public void setArcType(ArcType type, int instrumentIndex, int arcIndex, Current current) {
    metadataObject.setArcType(type, instrumentIndex, arcIndex);
  }

  // - BooleanAnnotation property storage -

  public void setBooleanAnnotationID(String id, int booleanAnnotationIndex, Current current) {
    metadataObject.setBooleanAnnotationID(id, booleanAnnotationIndex);
  }

  public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex, Current current) {
    metadataObject.setBooleanAnnotationNamespace(namespace, booleanAnnotationIndex);
  }

  public void setBooleanAnnotationValue(boolean value, int booleanAnnotationIndex, Current current) {
    metadataObject.setBooleanAnnotationValue(value, booleanAnnotationIndex);
  }

  // - Channel property storage -

  public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelAcquisitionMode(acquisitionMode, imageIndex, channelIndex);
  }

  public void setChannelAnnotationRef(String annotationRef, int imageIndex, int channelIndex, int annotationRefIndex, Current current) {
    metadataObject.setChannelAnnotationRef(annotationRef, imageIndex, channelIndex, annotationRefIndex);
  }

  public void setChannelColor(int color, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelColor(color, imageIndex, channelIndex);
  }

  public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelContrastMethod(contrastMethod, imageIndex, channelIndex);
  }

  public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelEmissionWavelength(emissionWavelength, imageIndex, channelIndex);
  }

  public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelExcitationWavelength(excitationWavelength, imageIndex, channelIndex);
  }

  public void setChannelFilterSetRef(String filterSetRef, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelFilterSetRef(filterSetRef, imageIndex, channelIndex);
  }

  public void setChannelFluor(String fluor, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelFluor(fluor, imageIndex, channelIndex);
  }

  public void setChannelID(String id, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelID(id, imageIndex, channelIndex);
  }

  public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelIlluminationType(illuminationType, imageIndex, channelIndex);
  }

  public void setChannelLightSourceSettingsAttenuation(PercentFraction lightSourceSettingsAttenuation, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelLightSourceSettingsAttenuation(lightSourceSettingsAttenuation, imageIndex, channelIndex);
  }

  public void setChannelLightSourceSettingsID(String lightSourceSettingsID, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelLightSourceSettingsID(lightSourceSettingsID, imageIndex, channelIndex);
  }

  public void setChannelLightSourceSettingsWavelength(PositiveInteger lightSourceSettingsWavelength, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelLightSourceSettingsWavelength(lightSourceSettingsWavelength, imageIndex, channelIndex);
  }

  public void setChannelNDFilter(double ndFilter, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelNDFilter(ndFilter, imageIndex, channelIndex);
  }

  public void setChannelName(String name, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelName(name, imageIndex, channelIndex);
  }

  public void setChannelOTFRef(String otfRef, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelOTFRef(otfRef, imageIndex, channelIndex);
  }

  public void setChannelPinholeSize(double pinholeSize, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelPinholeSize(pinholeSize, imageIndex, channelIndex);
  }

  public void setChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelPockelCellSetting(pockelCellSetting, imageIndex, channelIndex);
  }

  public void setChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int channelIndex, Current current) {
    metadataObject.setChannelSamplesPerPixel(samplesPerPixel, imageIndex, channelIndex);
  }

  // - ChannelAnnotationRef property storage -

  // - Dataset property storage -

  public void setDatasetAnnotationRef(String annotationRef, int datasetIndex, int annotationRefIndex, Current current) {
    metadataObject.setDatasetAnnotationRef(annotationRef, datasetIndex, annotationRefIndex);
  }

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

  public void setDatasetName(String name, int datasetIndex, Current current) {
    metadataObject.setDatasetName(name, datasetIndex);
  }

  public void setDatasetProjectRef(String projectRef, int datasetIndex, int projectRefIndex, Current current) {
    metadataObject.setDatasetProjectRef(projectRef, datasetIndex, projectRefIndex);
  }

  // - DatasetAnnotationRef property storage -

  // - DatasetRef property storage -

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

  public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorLotNumber(lotNumber, instrumentIndex, detectorIndex);
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

  public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  public void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  public void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsBinning(binning, imageIndex, channelIndex);
  }

  public void setDetectorSettingsGain(double gain, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsGain(gain, imageIndex, channelIndex);
  }

  public void setDetectorSettingsID(String id, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsID(id, imageIndex, channelIndex);
  }

  public void setDetectorSettingsOffset(double offset, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsOffset(offset, imageIndex, channelIndex);
  }

  public void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsReadOutRate(readOutRate, imageIndex, channelIndex);
  }

  public void setDetectorSettingsVoltage(double voltage, int imageIndex, int channelIndex, Current current) {
    metadataObject.setDetectorSettingsVoltage(voltage, imageIndex, channelIndex);
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

  public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex, Current current) {
    metadataObject.setDichroicSerialNumber(serialNumber, instrumentIndex, dichroicIndex);
  }

  // - DoubleAnnotation property storage -

  public void setDoubleAnnotationID(String id, int doubleAnnotationIndex, Current current) {
    metadataObject.setDoubleAnnotationID(id, doubleAnnotationIndex);
  }

  public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex, Current current) {
    metadataObject.setDoubleAnnotationNamespace(namespace, doubleAnnotationIndex);
  }

  public void setDoubleAnnotationValue(double value, int doubleAnnotationIndex, Current current) {
    metadataObject.setDoubleAnnotationValue(value, doubleAnnotationIndex);
  }

  // - Ellipse property storage -

  public void setEllipseDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseDescription(description, roiIndex, shapeIndex);
  }

  public void setEllipseFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseFill(fill, roiIndex, shapeIndex);
  }

  public void setEllipseFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setEllipseID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseID(id, roiIndex, shapeIndex);
  }

  public void setEllipseLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseLabel(label, roiIndex, shapeIndex);
  }

  public void setEllipseName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseName(name, roiIndex, shapeIndex);
  }

  public void setEllipseRadiusX(double radiusX, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseRadiusX(radiusX, roiIndex, shapeIndex);
  }

  public void setEllipseRadiusY(double radiusY, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseRadiusY(radiusY, roiIndex, shapeIndex);
  }

  public void setEllipseStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseStroke(stroke, roiIndex, shapeIndex);
  }

  public void setEllipseStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setEllipseStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setEllipseTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseTheC(theC, roiIndex, shapeIndex);
  }

  public void setEllipseTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseTheT(theT, roiIndex, shapeIndex);
  }

  public void setEllipseTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setEllipseTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseTransform(transform, roiIndex, shapeIndex);
  }

  public void setEllipseX(double x, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseX(x, roiIndex, shapeIndex);
  }

  public void setEllipseY(double y, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setEllipseY(y, roiIndex, shapeIndex);
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

  public void setExperimentType(ExperimentType type, int experimentIndex, Current current) {
    metadataObject.setExperimentType(type, experimentIndex);
  }

  // - Experimenter property storage -

  public void setExperimenterAnnotationRef(String annotationRef, int experimenterIndex, int annotationRefIndex, Current current) {
    metadataObject.setExperimenterAnnotationRef(annotationRef, experimenterIndex, annotationRefIndex);
  }

  public void setExperimenterDisplayName(String displayName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterDisplayName(displayName, experimenterIndex);
  }

  public void setExperimenterEmail(String email, int experimenterIndex, Current current) {
    metadataObject.setExperimenterEmail(email, experimenterIndex);
  }

  public void setExperimenterFirstName(String firstName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterFirstName(firstName, experimenterIndex);
  }

  public void setExperimenterGroupRef(String groupRef, int experimenterIndex, int groupRefIndex, Current current) {
    metadataObject.setExperimenterGroupRef(groupRef, experimenterIndex, groupRefIndex);
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

  public void setExperimenterMiddleName(String middleName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterMiddleName(middleName, experimenterIndex);
  }

  public void setExperimenterUserName(String userName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterUserName(userName, experimenterIndex);
  }

  // - ExperimenterAnnotationRef property storage -

  // - ExperimenterGroupRef property storage -

  // - Filament property storage -

  public void setFilamentID(String id, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentID(id, instrumentIndex, filamentIndex);
  }

  public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentLotNumber(lotNumber, instrumentIndex, filamentIndex);
  }

  public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentManufacturer(manufacturer, instrumentIndex, filamentIndex);
  }

  public void setFilamentModel(String model, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentModel(model, instrumentIndex, filamentIndex);
  }

  public void setFilamentPower(double power, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentPower(power, instrumentIndex, filamentIndex);
  }

  public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentSerialNumber(serialNumber, instrumentIndex, filamentIndex);
  }

  public void setFilamentType(FilamentType type, int instrumentIndex, int filamentIndex, Current current) {
    metadataObject.setFilamentType(type, instrumentIndex, filamentIndex);
  }

  // - FileAnnotation property storage -

  public void setFileAnnotationBinaryFileFileName(String binaryFileFileName, int fileAnnotationIndex, Current current) {
    metadataObject.setFileAnnotationBinaryFileFileName(binaryFileFileName, fileAnnotationIndex);
  }

  public void setFileAnnotationBinaryFileMIMEType(String binaryFileMIMEType, int fileAnnotationIndex, Current current) {
    metadataObject.setFileAnnotationBinaryFileMIMEType(binaryFileMIMEType, fileAnnotationIndex);
  }

  public void setFileAnnotationBinaryFileSize(int binaryFileSize, int fileAnnotationIndex, Current current) {
    metadataObject.setFileAnnotationBinaryFileSize(binaryFileSize, fileAnnotationIndex);
  }

  public void setFileAnnotationID(String id, int fileAnnotationIndex, Current current) {
    metadataObject.setFileAnnotationID(id, fileAnnotationIndex);
  }

  public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex, Current current) {
    metadataObject.setFileAnnotationNamespace(namespace, fileAnnotationIndex);
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

  public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterSerialNumber(serialNumber, instrumentIndex, filterIndex);
  }

  public void setFilterType(FilterType type, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setFilterType(type, instrumentIndex, filterIndex);
  }

  // - FilterSet property storage -

  public void setFilterSetDichroicRef(String dichroicRef, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetDichroicRef(dichroicRef, instrumentIndex, filterSetIndex);
  }

  public void setFilterSetEmissionFilterRef(String emissionFilterRef, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex, Current current) {
    metadataObject.setFilterSetEmissionFilterRef(emissionFilterRef, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
  }

  public void setFilterSetExcitationFilterRef(String excitationFilterRef, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex, Current current) {
    metadataObject.setFilterSetExcitationFilterRef(excitationFilterRef, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
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

  public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex, Current current) {
    metadataObject.setFilterSetSerialNumber(serialNumber, instrumentIndex, filterSetIndex);
  }

  // - FilterSetEmissionFilterRef property storage -

  // - FilterSetExcitationFilterRef property storage -

  // - Group property storage -

  public void setGroupContact(String contact, int groupIndex, Current current) {
    metadataObject.setGroupContact(contact, groupIndex);
  }

  public void setGroupDescription(String description, int groupIndex, Current current) {
    metadataObject.setGroupDescription(description, groupIndex);
  }

  public void setGroupID(String id, int groupIndex, Current current) {
    metadataObject.setGroupID(id, groupIndex);
  }

  public void setGroupLeader(String leader, int groupIndex, Current current) {
    metadataObject.setGroupLeader(leader, groupIndex);
  }

  public void setGroupName(String name, int groupIndex, Current current) {
    metadataObject.setGroupName(name, groupIndex);
  }

  // - Image property storage -

  public void setImageAcquiredDate(String acquiredDate, int imageIndex, Current current) {
    metadataObject.setImageAcquiredDate(acquiredDate, imageIndex);
  }

  public void setImageAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex, Current current) {
    metadataObject.setImageAnnotationRef(annotationRef, imageIndex, annotationRefIndex);
  }

  public void setImageDatasetRef(String datasetRef, int imageIndex, int datasetRefIndex, Current current) {
    metadataObject.setImageDatasetRef(datasetRef, imageIndex, datasetRefIndex);
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

  public void setImageMicrobeamManipulationRef(String microbeamManipulationRef, int imageIndex, int microbeamManipulationRefIndex, Current current) {
    metadataObject.setImageMicrobeamManipulationRef(microbeamManipulationRef, imageIndex, microbeamManipulationRefIndex);
  }

  public void setImageName(String name, int imageIndex, Current current) {
    metadataObject.setImageName(name, imageIndex);
  }

  public void setImageObjectiveSettingsCorrectionCollar(double objectiveSettingsCorrectionCollar, int imageIndex, Current current) {
    metadataObject.setImageObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, imageIndex);
  }

  public void setImageObjectiveSettingsID(String objectiveSettingsID, int imageIndex, Current current) {
    metadataObject.setImageObjectiveSettingsID(objectiveSettingsID, imageIndex);
  }

  public void setImageObjectiveSettingsMedium(Medium objectiveSettingsMedium, int imageIndex, Current current) {
    metadataObject.setImageObjectiveSettingsMedium(objectiveSettingsMedium, imageIndex);
  }

  public void setImageObjectiveSettingsRefractiveIndex(double objectiveSettingsRefractiveIndex, int imageIndex, Current current) {
    metadataObject.setImageObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, imageIndex);
  }

  public void setImageROIRef(String roiRef, int imageIndex, int roiRefIndex, Current current) {
    metadataObject.setImageROIRef(roiRef, imageIndex, roiRefIndex);
  }

  // - ImageAnnotationRef property storage -

  // - ImageROIRef property storage -

  // - ImagingEnvironment property storage -

  public void setImagingEnvironmentAirPressure(double airPressure, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  public void setImagingEnvironmentCO2Percent(PercentFraction cO2Percent, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex, Current current) {
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

  public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, laserIndex);
  }

  public void setLaserID(String id, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserID(id, instrumentIndex, laserIndex);
  }

  public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserLaserMedium(laserMedium, instrumentIndex, laserIndex);
  }

  public void setLaserLotNumber(String lotNumber, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserLotNumber(lotNumber, instrumentIndex, laserIndex);
  }

  public void setLaserManufacturer(String manufacturer, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserManufacturer(manufacturer, instrumentIndex, laserIndex);
  }

  public void setLaserModel(String model, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserModel(model, instrumentIndex, laserIndex);
  }

  public void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserPockelCell(pockelCell, instrumentIndex, laserIndex);
  }

  public void setLaserPower(double power, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserPower(power, instrumentIndex, laserIndex);
  }

  public void setLaserPulse(Pulse pulse, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserPulse(pulse, instrumentIndex, laserIndex);
  }

  public void setLaserPump(String pump, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserPump(pump, instrumentIndex, laserIndex);
  }

  public void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserRepetitionRate(repetitionRate, instrumentIndex, laserIndex);
  }

  public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserSerialNumber(serialNumber, instrumentIndex, laserIndex);
  }

  public void setLaserTuneable(boolean tuneable, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserTuneable(tuneable, instrumentIndex, laserIndex);
  }

  public void setLaserType(LaserType type, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserType(type, instrumentIndex, laserIndex);
  }

  public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int laserIndex, Current current) {
    metadataObject.setLaserWavelength(wavelength, instrumentIndex, laserIndex);
  }

  // - LightEmittingDiode property storage -

  public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodeID(id, instrumentIndex, lightEmittingDiodeIndex);
  }

  public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodeLotNumber(lotNumber, instrumentIndex, lightEmittingDiodeIndex);
  }

  public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodeManufacturer(manufacturer, instrumentIndex, lightEmittingDiodeIndex);
  }

  public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodeModel(model, instrumentIndex, lightEmittingDiodeIndex);
  }

  public void setLightEmittingDiodePower(double power, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodePower(power, instrumentIndex, lightEmittingDiodeIndex);
  }

  public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightEmittingDiodeIndex, Current current) {
    metadataObject.setLightEmittingDiodeSerialNumber(serialNumber, instrumentIndex, lightEmittingDiodeIndex);
  }

  // - LightPath property storage -

  public void setLightPathDichroicRef(String dichroicRef, int imageIndex, int channelIndex, Current current) {
    metadataObject.setLightPathDichroicRef(dichroicRef, imageIndex, channelIndex);
  }

  public void setLightPathEmissionFilterRef(String emissionFilterRef, int imageIndex, int channelIndex, int emissionFilterRefIndex, Current current) {
    metadataObject.setLightPathEmissionFilterRef(emissionFilterRef, imageIndex, channelIndex, emissionFilterRefIndex);
  }

  public void setLightPathExcitationFilterRef(String excitationFilterRef, int imageIndex, int channelIndex, int excitationFilterRefIndex, Current current) {
    metadataObject.setLightPathExcitationFilterRef(excitationFilterRef, imageIndex, channelIndex, excitationFilterRefIndex);
  }

  // - LightPathEmissionFilterRef property storage -

  // - LightPathExcitationFilterRef property storage -

  // - Line property storage -

  public void setLineDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineDescription(description, roiIndex, shapeIndex);
  }

  public void setLineFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineFill(fill, roiIndex, shapeIndex);
  }

  public void setLineFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setLineID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineID(id, roiIndex, shapeIndex);
  }

  public void setLineLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineLabel(label, roiIndex, shapeIndex);
  }

  public void setLineName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineName(name, roiIndex, shapeIndex);
  }

  public void setLineStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineStroke(stroke, roiIndex, shapeIndex);
  }

  public void setLineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setLineStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setLineTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineTheC(theC, roiIndex, shapeIndex);
  }

  public void setLineTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineTheT(theT, roiIndex, shapeIndex);
  }

  public void setLineTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setLineTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineTransform(transform, roiIndex, shapeIndex);
  }

  public void setLineX1(double x1, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineX1(x1, roiIndex, shapeIndex);
  }

  public void setLineX2(double x2, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineX2(x2, roiIndex, shapeIndex);
  }

  public void setLineY1(double y1, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineY1(y1, roiIndex, shapeIndex);
  }

  public void setLineY2(double y2, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setLineY2(y2, roiIndex, shapeIndex);
  }

  // - ListAnnotation property storage -

  public void setListAnnotationAnnotationRef(String annotationRef, int listAnnotationIndex, int annotationRefIndex, Current current) {
    metadataObject.setListAnnotationAnnotationRef(annotationRef, listAnnotationIndex, annotationRefIndex);
  }

  public void setListAnnotationID(String id, int listAnnotationIndex, Current current) {
    metadataObject.setListAnnotationID(id, listAnnotationIndex);
  }

  public void setListAnnotationNamespace(String namespace, int listAnnotationIndex, Current current) {
    metadataObject.setListAnnotationNamespace(namespace, listAnnotationIndex);
  }

  // - ListAnnotationAnnotationRef property storage -

  // - LongAnnotation property storage -

  public void setLongAnnotationID(String id, int longAnnotationIndex, Current current) {
    metadataObject.setLongAnnotationID(id, longAnnotationIndex);
  }

  public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex, Current current) {
    metadataObject.setLongAnnotationNamespace(namespace, longAnnotationIndex);
  }

  public void setLongAnnotationValue(long value, int longAnnotationIndex, Current current) {
    metadataObject.setLongAnnotationValue(value, longAnnotationIndex);
  }

  // - Mask property storage -

  public void setMaskDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskDescription(description, roiIndex, shapeIndex);
  }

  public void setMaskFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskFill(fill, roiIndex, shapeIndex);
  }

  public void setMaskFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setMaskID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskID(id, roiIndex, shapeIndex);
  }

  public void setMaskLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskLabel(label, roiIndex, shapeIndex);
  }

  public void setMaskName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskName(name, roiIndex, shapeIndex);
  }

  public void setMaskStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskStroke(stroke, roiIndex, shapeIndex);
  }

  public void setMaskStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setMaskStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setMaskTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskTheC(theC, roiIndex, shapeIndex);
  }

  public void setMaskTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskTheT(theT, roiIndex, shapeIndex);
  }

  public void setMaskTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setMaskTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskTransform(transform, roiIndex, shapeIndex);
  }

  public void setMaskX(double x, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskX(x, roiIndex, shapeIndex);
  }

  public void setMaskY(double y, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setMaskY(y, roiIndex, shapeIndex);
  }

  // - MicrobeamManipulation property storage -

  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int experimentIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationExperimenterRef(experimenterRef, experimentIndex, microbeamManipulationIndex);
  }

  public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationID(id, experimentIndex, microbeamManipulationIndex);
  }

  public void setMicrobeamManipulationROIRef(String roiRef, int experimentIndex, int microbeamManipulationIndex, int roiRefIndex, Current current) {
    metadataObject.setMicrobeamManipulationROIRef(roiRef, experimentIndex, microbeamManipulationIndex, roiRefIndex);
  }

  public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex, Current current) {
    metadataObject.setMicrobeamManipulationType(type, experimentIndex, microbeamManipulationIndex);
  }

  // - MicrobeamManipulationLightSourceSettings property storage -

  public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    metadataObject.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    metadataObject.setMicrobeamManipulationLightSourceSettingsID(id, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex, Current current) {
    metadataObject.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  // - MicrobeamManipulationROIRef property storage -

  // - MicrobeamManipulationRef property storage -

  // - Microscope property storage -

  public void setMicroscopeLotNumber(String lotNumber, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeLotNumber(lotNumber, instrumentIndex);
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

  public void setMicroscopeType(MicroscopeType type, int instrumentIndex, Current current) {
    metadataObject.setMicroscopeType(type, instrumentIndex);
  }

  // - OTF property storage -

  public void setOTFBinaryFileFileName(String binaryFileFileName, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFBinaryFileFileName(binaryFileFileName, instrumentIndex, otfIndex);
  }

  public void setOTFBinaryFileMIMEType(String binaryFileMIMEType, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFBinaryFileMIMEType(binaryFileMIMEType, instrumentIndex, otfIndex);
  }

  public void setOTFBinaryFileSize(int binaryFileSize, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFBinaryFileSize(binaryFileSize, instrumentIndex, otfIndex);
  }

  public void setOTFFilterSetRef(String filterSetRef, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFFilterSetRef(filterSetRef, instrumentIndex, otfIndex);
  }

  public void setOTFID(String id, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFID(id, instrumentIndex, otfIndex);
  }

  public void setOTFObjectiveSettingsCorrectionCollar(double objectiveSettingsCorrectionCollar, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, instrumentIndex, otfIndex);
  }

  public void setOTFObjectiveSettingsID(String objectiveSettingsID, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjectiveSettingsID(objectiveSettingsID, instrumentIndex, otfIndex);
  }

  public void setOTFObjectiveSettingsMedium(Medium objectiveSettingsMedium, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjectiveSettingsMedium(objectiveSettingsMedium, instrumentIndex, otfIndex);
  }

  public void setOTFObjectiveSettingsRefractiveIndex(double objectiveSettingsRefractiveIndex, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, instrumentIndex, otfIndex);
  }

  public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  public void setOTFType(PixelType type, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFType(type, instrumentIndex, otfIndex);
  }

  // - Objective property storage -

  public void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveID(id, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveLotNumber(lotNumber, instrumentIndex, objectiveIndex);
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

  // - Path property storage -

  public void setPathDefinition(String definition, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathDefinition(definition, roiIndex, shapeIndex);
  }

  public void setPathDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathDescription(description, roiIndex, shapeIndex);
  }

  public void setPathFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathFill(fill, roiIndex, shapeIndex);
  }

  public void setPathFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setPathID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathID(id, roiIndex, shapeIndex);
  }

  public void setPathLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathLabel(label, roiIndex, shapeIndex);
  }

  public void setPathName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathName(name, roiIndex, shapeIndex);
  }

  public void setPathStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathStroke(stroke, roiIndex, shapeIndex);
  }

  public void setPathStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setPathStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setPathTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathTheC(theC, roiIndex, shapeIndex);
  }

  public void setPathTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathTheT(theT, roiIndex, shapeIndex);
  }

  public void setPathTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setPathTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPathTransform(transform, roiIndex, shapeIndex);
  }

  // - Pixels property storage -

  public void setPixelsAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex, Current current) {
    metadataObject.setPixelsAnnotationRef(annotationRef, imageIndex, annotationRefIndex);
  }

  public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex, Current current) {
    metadataObject.setPixelsDimensionOrder(dimensionOrder, imageIndex);
  }

  public void setPixelsID(String id, int imageIndex, Current current) {
    metadataObject.setPixelsID(id, imageIndex);
  }

  public void setPixelsPhysicalSizeX(double physicalSizeX, int imageIndex, Current current) {
    metadataObject.setPixelsPhysicalSizeX(physicalSizeX, imageIndex);
  }

  public void setPixelsPhysicalSizeY(double physicalSizeY, int imageIndex, Current current) {
    metadataObject.setPixelsPhysicalSizeY(physicalSizeY, imageIndex);
  }

  public void setPixelsPhysicalSizeZ(double physicalSizeZ, int imageIndex, Current current) {
    metadataObject.setPixelsPhysicalSizeZ(physicalSizeZ, imageIndex);
  }

  public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex, Current current) {
    metadataObject.setPixelsSizeC(sizeC, imageIndex);
  }

  public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex, Current current) {
    metadataObject.setPixelsSizeT(sizeT, imageIndex);
  }

  public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex, Current current) {
    metadataObject.setPixelsSizeX(sizeX, imageIndex);
  }

  public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex, Current current) {
    metadataObject.setPixelsSizeY(sizeY, imageIndex);
  }

  public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex, Current current) {
    metadataObject.setPixelsSizeZ(sizeZ, imageIndex);
  }

  public void setPixelsTimeIncrement(double timeIncrement, int imageIndex, Current current) {
    metadataObject.setPixelsTimeIncrement(timeIncrement, imageIndex);
  }

  public void setPixelsType(PixelType type, int imageIndex, Current current) {
    metadataObject.setPixelsType(type, imageIndex);
  }

  // - PixelsAnnotationRef property storage -

  // - PixelsBinData property storage -

  public void setPixelsBinDataBigEndian(boolean bigEndian, int imageIndex, int binDataIndex, Current current) {
    metadataObject.setPixelsBinDataBigEndian(bigEndian, imageIndex, binDataIndex);
  }

  // - Plane property storage -

  public void setPlaneAnnotationRef(String annotationRef, int imageIndex, int planeIndex, int annotationRefIndex, Current current) {
    metadataObject.setPlaneAnnotationRef(annotationRef, imageIndex, planeIndex, annotationRefIndex);
  }

  public void setPlaneDeltaT(double deltaT, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneDeltaT(deltaT, imageIndex, planeIndex);
  }

  public void setPlaneExposureTime(double exposureTime, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneExposureTime(exposureTime, imageIndex, planeIndex);
  }

  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneHashSHA1(hashSHA1, imageIndex, planeIndex);
  }

  public void setPlanePositionX(double positionX, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlanePositionX(positionX, imageIndex, planeIndex);
  }

  public void setPlanePositionY(double positionY, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlanePositionY(positionY, imageIndex, planeIndex);
  }

  public void setPlanePositionZ(double positionZ, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlanePositionZ(positionZ, imageIndex, planeIndex);
  }

  public void setPlaneTheC(int theC, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheC(theC, imageIndex, planeIndex);
  }

  public void setPlaneTheT(int theT, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheT(theT, imageIndex, planeIndex);
  }

  public void setPlaneTheZ(int theZ, int imageIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheZ(theZ, imageIndex, planeIndex);
  }

  // - PlaneAnnotationRef property storage -

  // - Plate property storage -

  public void setPlateAnnotationRef(String annotationRef, int plateIndex, int annotationRefIndex, Current current) {
    metadataObject.setPlateAnnotationRef(annotationRef, plateIndex, annotationRefIndex);
  }

  public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex, Current current) {
    metadataObject.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
  }

  public void setPlateColumns(int columns, int plateIndex, Current current) {
    metadataObject.setPlateColumns(columns, plateIndex);
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

  public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex, Current current) {
    metadataObject.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
  }

  public void setPlateRows(int rows, int plateIndex, Current current) {
    metadataObject.setPlateRows(rows, plateIndex);
  }

  public void setPlateScreenRef(String screenRef, int plateIndex, int screenRefIndex, Current current) {
    metadataObject.setPlateScreenRef(screenRef, plateIndex, screenRefIndex);
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

  // - PlateAcquisition property storage -

  public void setPlateAcquisitionAnnotationRef(String annotationRef, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex, Current current) {
    metadataObject.setPlateAcquisitionAnnotationRef(annotationRef, plateIndex, plateAcquisitionIndex, annotationRefIndex);
  }

  public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionDescription(description, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionEndTime(endTime, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionID(id, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionMaximumFieldCount(int maximumFieldCount, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionMaximumFieldCount(maximumFieldCount, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionName(name, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex, Current current) {
    metadataObject.setPlateAcquisitionStartTime(startTime, plateIndex, plateAcquisitionIndex);
  }

  public void setPlateAcquisitionWellSampleRef(String wellSampleRef, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex, Current current) {
    metadataObject.setPlateAcquisitionWellSampleRef(wellSampleRef, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
  }

  // - PlateAcquisitionAnnotationRef property storage -

  // - PlateAnnotationRef property storage -

  // - PlateRef property storage -

  // - Point property storage -

  public void setPointDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointDescription(description, roiIndex, shapeIndex);
  }

  public void setPointFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointFill(fill, roiIndex, shapeIndex);
  }

  public void setPointFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setPointID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointID(id, roiIndex, shapeIndex);
  }

  public void setPointLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointLabel(label, roiIndex, shapeIndex);
  }

  public void setPointName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointName(name, roiIndex, shapeIndex);
  }

  public void setPointStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointStroke(stroke, roiIndex, shapeIndex);
  }

  public void setPointStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setPointStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setPointTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointTheC(theC, roiIndex, shapeIndex);
  }

  public void setPointTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointTheT(theT, roiIndex, shapeIndex);
  }

  public void setPointTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setPointTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointTransform(transform, roiIndex, shapeIndex);
  }

  public void setPointX(double x, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointX(x, roiIndex, shapeIndex);
  }

  public void setPointY(double y, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPointY(y, roiIndex, shapeIndex);
  }

  // - Polyline property storage -

  public void setPolylineClosed(boolean closed, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineClosed(closed, roiIndex, shapeIndex);
  }

  public void setPolylineDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineDescription(description, roiIndex, shapeIndex);
  }

  public void setPolylineFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineFill(fill, roiIndex, shapeIndex);
  }

  public void setPolylineFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setPolylineID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineID(id, roiIndex, shapeIndex);
  }

  public void setPolylineLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineLabel(label, roiIndex, shapeIndex);
  }

  public void setPolylineName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineName(name, roiIndex, shapeIndex);
  }

  public void setPolylinePoints(String points, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylinePoints(points, roiIndex, shapeIndex);
  }

  public void setPolylineStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineStroke(stroke, roiIndex, shapeIndex);
  }

  public void setPolylineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setPolylineStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setPolylineTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineTheC(theC, roiIndex, shapeIndex);
  }

  public void setPolylineTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineTheT(theT, roiIndex, shapeIndex);
  }

  public void setPolylineTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setPolylineTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setPolylineTransform(transform, roiIndex, shapeIndex);
  }

  // - Project property storage -

  public void setProjectAnnotationRef(String annotationRef, int projectIndex, int annotationRefIndex, Current current) {
    metadataObject.setProjectAnnotationRef(annotationRef, projectIndex, annotationRefIndex);
  }

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

  // - ProjectAnnotationRef property storage -

  // - ProjectRef property storage -

  // - ROI property storage -

  public void setROIAnnotationRef(String annotationRef, int roiIndex, int annotationRefIndex, Current current) {
    metadataObject.setROIAnnotationRef(annotationRef, roiIndex, annotationRefIndex);
  }

  public void setROIDescription(String description, int roiIndex, Current current) {
    metadataObject.setROIDescription(description, roiIndex);
  }

  public void setROIID(String id, int roiIndex, Current current) {
    metadataObject.setROIID(id, roiIndex);
  }

  public void setROIName(String name, int roiIndex, Current current) {
    metadataObject.setROIName(name, roiIndex);
  }

  public void setROINamespace(String namespace, int roiIndex, Current current) {
    metadataObject.setROINamespace(namespace, roiIndex);
  }

  // - ROIAnnotationRef property storage -

  // - Reagent property storage -

  public void setReagentAnnotationRef(String annotationRef, int screenIndex, int reagentIndex, int annotationRefIndex, Current current) {
    metadataObject.setReagentAnnotationRef(annotationRef, screenIndex, reagentIndex, annotationRefIndex);
  }

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

  // - ReagentAnnotationRef property storage -

  // - Rectangle property storage -

  public void setRectangleDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleDescription(description, roiIndex, shapeIndex);
  }

  public void setRectangleFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleFill(fill, roiIndex, shapeIndex);
  }

  public void setRectangleFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setRectangleHeight(double height, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleHeight(height, roiIndex, shapeIndex);
  }

  public void setRectangleID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleID(id, roiIndex, shapeIndex);
  }

  public void setRectangleLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleLabel(label, roiIndex, shapeIndex);
  }

  public void setRectangleName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleName(name, roiIndex, shapeIndex);
  }

  public void setRectangleStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleStroke(stroke, roiIndex, shapeIndex);
  }

  public void setRectangleStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setRectangleStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setRectangleTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleTheC(theC, roiIndex, shapeIndex);
  }

  public void setRectangleTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleTheT(theT, roiIndex, shapeIndex);
  }

  public void setRectangleTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setRectangleTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleTransform(transform, roiIndex, shapeIndex);
  }

  public void setRectangleWidth(double width, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleWidth(width, roiIndex, shapeIndex);
  }

  public void setRectangleX(double x, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleX(x, roiIndex, shapeIndex);
  }

  public void setRectangleY(double y, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setRectangleY(y, roiIndex, shapeIndex);
  }

  // - Screen property storage -

  public void setScreenAnnotationRef(String annotationRef, int screenIndex, int annotationRefIndex, Current current) {
    metadataObject.setScreenAnnotationRef(annotationRef, screenIndex, annotationRefIndex);
  }

  public void setScreenDescription(String description, int screenIndex, Current current) {
    metadataObject.setScreenDescription(description, screenIndex);
  }

  public void setScreenID(String id, int screenIndex, Current current) {
    metadataObject.setScreenID(id, screenIndex);
  }

  public void setScreenName(String name, int screenIndex, Current current) {
    metadataObject.setScreenName(name, screenIndex);
  }

  public void setScreenPlateRef(String plateRef, int screenIndex, int plateRefIndex, Current current) {
    metadataObject.setScreenPlateRef(plateRef, screenIndex, plateRefIndex);
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

  // - ScreenAnnotationRef property storage -

  // - ScreenRef property storage -

  // - ShapeAnnotationRef property storage -

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

  // - StringAnnotation property storage -

  public void setStringAnnotationID(String id, int stringAnnotationIndex, Current current) {
    metadataObject.setStringAnnotationID(id, stringAnnotationIndex);
  }

  public void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex, Current current) {
    metadataObject.setStringAnnotationNamespace(namespace, stringAnnotationIndex);
  }

  public void setStringAnnotationValue(String value, int stringAnnotationIndex, Current current) {
    metadataObject.setStringAnnotationValue(value, stringAnnotationIndex);
  }

  // - Text property storage -

  public void setTextDescription(String description, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextDescription(description, roiIndex, shapeIndex);
  }

  public void setTextFill(int fill, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextFill(fill, roiIndex, shapeIndex);
  }

  public void setTextFontSize(int fontSize, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextFontSize(fontSize, roiIndex, shapeIndex);
  }

  public void setTextID(String id, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextID(id, roiIndex, shapeIndex);
  }

  public void setTextLabel(String label, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextLabel(label, roiIndex, shapeIndex);
  }

  public void setTextName(String name, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextName(name, roiIndex, shapeIndex);
  }

  public void setTextStroke(int stroke, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextStroke(stroke, roiIndex, shapeIndex);
  }

  public void setTextStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
  }

  public void setTextStrokeWidth(double strokeWidth, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  public void setTextTheC(int theC, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextTheC(theC, roiIndex, shapeIndex);
  }

  public void setTextTheT(int theT, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextTheT(theT, roiIndex, shapeIndex);
  }

  public void setTextTheZ(int theZ, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextTheZ(theZ, roiIndex, shapeIndex);
  }

  public void setTextTransform(String transform, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextTransform(transform, roiIndex, shapeIndex);
  }

  public void setTextValue(String value, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextValue(value, roiIndex, shapeIndex);
  }

  public void setTextX(double x, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextX(x, roiIndex, shapeIndex);
  }

  public void setTextY(double y, int roiIndex, int shapeIndex, Current current) {
    metadataObject.setTextY(y, roiIndex, shapeIndex);
  }

  // - TiffData property storage -

  public void setTiffDataFirstC(int firstC, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstC(firstC, imageIndex, tiffDataIndex);
  }

  public void setTiffDataFirstT(int firstT, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstT(firstT, imageIndex, tiffDataIndex);
  }

  public void setTiffDataFirstZ(int firstZ, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstZ(firstZ, imageIndex, tiffDataIndex);
  }

  public void setTiffDataIFD(int ifd, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataIFD(ifd, imageIndex, tiffDataIndex);
  }

  public void setTiffDataPlaneCount(int planeCount, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataPlaneCount(planeCount, imageIndex, tiffDataIndex);
  }

  // - TimestampAnnotation property storage -

  public void setTimestampAnnotationID(String id, int timestampAnnotationIndex, Current current) {
    metadataObject.setTimestampAnnotationID(id, timestampAnnotationIndex);
  }

  public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex, Current current) {
    metadataObject.setTimestampAnnotationNamespace(namespace, timestampAnnotationIndex);
  }

  public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex, Current current) {
    metadataObject.setTimestampAnnotationValue(value, timestampAnnotationIndex);
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

  public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex, Current current) {
    metadataObject.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
  }

  // - UUID property storage -

  public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setUUIDFileName(fileName, imageIndex, tiffDataIndex);
  }

  public void setUUIDValue(String value, int imageIndex, int tiffDataIndex, Current current) {
    metadataObject.setUUIDValue(value, imageIndex, tiffDataIndex);
  }

  // - Well property storage -

  public void setWellAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int annotationRefIndex, Current current) {
    metadataObject.setWellAnnotationRef(annotationRef, plateIndex, wellIndex, annotationRefIndex);
  }

  public void setWellColor(int color, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellColor(color, plateIndex, wellIndex);
  }

  public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex, Current current) {
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

  public void setWellReagentRef(String reagentRef, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellReagentRef(reagentRef, plateIndex, wellIndex);
  }

  public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellRow(row, plateIndex, wellIndex);
  }

  public void setWellStatus(String status, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellStatus(status, plateIndex, wellIndex);
  }

  // - WellAnnotationRef property storage -

  // - WellSample property storage -

  public void setWellSampleAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex, Current current) {
    metadataObject.setWellSampleAnnotationRef(annotationRef, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
  }

  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePositionX(double positionX, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePositionX(positionX, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePositionY(double positionY, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePositionY(positionY, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

  // - WellSampleAnnotationRef property storage -

  // - WellSampleRef property storage -

  // - XMLAnnotation property storage -

  public void setXMLAnnotationID(String id, int xmlAnnotationIndex, Current current) {
    metadataObject.setXMLAnnotationID(id, xmlAnnotationIndex);
  }

  public void setXMLAnnotationNamespace(String namespace, int xmlAnnotationIndex, Current current) {
    metadataObject.setXMLAnnotationNamespace(namespace, xmlAnnotationIndex);
  }

  public void setXMLAnnotationValue(String value, int xmlAnnotationIndex, Current current) {
    metadataObject.setXMLAnnotationValue(value, xmlAnnotationIndex);
  }

}
