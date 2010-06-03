//
// DummyMetadata.java
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
 * Created by melissa via MetadataAutogen on May 10, 2010 9:27:03 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import java.util.Iterator;

import ome.xml.model.Image;
import ome.xml.model.OME;
import ome.xml.model.Pixels;
import ome.xml.model.TiffData;
import ome.xml.model.UUID;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

/**
 * A dummy implementation for {@link MetadataStore} and
 * {@link MetadataRetrieve} that is used when no other
 * metadata implementations are available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/DummyMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/DummyMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class DummyMetadata implements IMetadata {

  // -- MetadataRetrieve API methods --

  // - Entity counting -

  /* @see MetadataRetrieve#getPixelsBinDataCount(int) */
  public int getPixelsBinDataCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getBooleanAnnotationCount() */
  public int getBooleanAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getChannelCount(int) */
  public int getChannelCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getChannelAnnotationRefCount(int, int) */
  public int getChannelAnnotationRefCount(int imageIndex, int channelIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetCount() */
  public int getDatasetCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetAnnotationRefCount(int) */
  public int getDatasetAnnotationRefCount(int datasetIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetRefCount(int) */
  public int getDatasetRefCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDichroicCount(int) */
  public int getDichroicCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDoubleAnnotationCount() */
  public int getDoubleAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getExperimentCount() */
  public int getExperimentCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterAnnotationRefCount(int) */
  public int getExperimenterAnnotationRefCount(int experimenterIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterGroupRefCount(int) */
  public int getExperimenterGroupRefCount(int experimenterIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getFileAnnotationCount() */
  public int getFileAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getFilterCount(int) */
  public int getFilterCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetCount(int) */
  public int getFilterSetCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetEmissionFilterRefCount(int, int) */
  public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetExcitationFilterRefCount(int, int) */
  public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getGroupCount() */
  public int getGroupCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getImageAnnotationRefCount(int) */
  public int getImageAnnotationRefCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getImageROIRefCount(int) */
  public int getImageROIRefCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getInstrumentCount() */
  public int getInstrumentCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getLightPathEmissionFilterRefCount(int, int) */
  public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getLightPathExcitationFilterRefCount(int, int) */
  public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getListAnnotationCount() */
  public int getListAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getListAnnotationAnnotationRefCount(int) */
  public int getListAnnotationAnnotationRefCount(int listAnnotationIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getLongAnnotationCount() */
  public int getLongAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationCount(int) */
  public int getMicrobeamManipulationCount(int experimentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsCount(int, int) */
  public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationROIRefCount(int, int) */
  public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationRefCount(int) */
  public int getMicrobeamManipulationRefCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPixelsAnnotationRefCount(int) */
  public int getPixelsAnnotationRefCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneCount(int) */
  public int getPlaneCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneAnnotationRefCount(int, int) */
  public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlateCount() */
  public int getPlateCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionCount(int) */
  public int getPlateAcquisitionCount(int plateIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionAnnotationRefCount(int, int) */
  public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAnnotationRefCount(int) */
  public int getPlateAnnotationRefCount(int plateIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlateRefCount(int) */
  public int getPlateRefCount(int screenIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getProjectCount() */
  public int getProjectCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getProjectAnnotationRefCount(int) */
  public int getProjectAnnotationRefCount(int projectIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getProjectRefCount(int) */
  public int getProjectRefCount(int datasetIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getROICount() */
  public int getROICount() {
    return -1;
  }

  /* @see MetadataRetrieve#getROIAnnotationRefCount(int) */
  public int getROIAnnotationRefCount(int roiIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getReagentCount(int) */
  public int getReagentCount(int screenIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getReagentAnnotationRefCount(int, int) */
  public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getScreenCount() */
  public int getScreenCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getScreenAnnotationRefCount(int) */
  public int getScreenAnnotationRefCount(int screenIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getScreenRefCount(int) */
  public int getScreenRefCount(int plateIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getShapeAnnotationRefCount(int, int) */
  public int getShapeAnnotationRefCount(int roiIndex, int shapeIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getStringAnnotationCount() */
  public int getStringAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getTiffDataCount(int) */
  public int getTiffDataCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getTimestampAnnotationCount() */
  public int getTimestampAnnotationCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getWellCount(int) */
  public int getWellCount(int plateIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getWellAnnotationRefCount(int, int) */
  public int getWellAnnotationRefCount(int plateIndex, int wellIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleCount(int, int) */
  public int getWellSampleCount(int plateIndex, int wellIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleAnnotationRefCount(int, int, int) */
  public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleRefCount(int, int) */
  public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getXMLAnnotationCount() */
  public int getXMLAnnotationCount() {
    return -1;
  }

  // - Entity retrieval -

  /* @see MetadataRetrieve#getUUID() */
  public String getUUID() {
    return null;
  }

  // - Arc property retrieval -

  /* @see MetadataRetrieve#getArcID(int, int) */
  public String getArcID(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcLotNumber(int, int) */
  public String getArcLotNumber(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcManufacturer(int, int) */
  public String getArcManufacturer(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcModel(int, int) */
  public String getArcModel(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcPower(int, int) */
  public Double getArcPower(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcSerialNumber(int, int) */
  public String getArcSerialNumber(int instrumentIndex, int arcIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcType(int, int) */
  public ArcType getArcType(int instrumentIndex, int arcIndex) {
    return null;
  }

  // - BooleanAnnotation property retrieval -

  /* @see MetadataRetrieve#getBooleanAnnotationID(int) */
  public String getBooleanAnnotationID(int booleanAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getBooleanAnnotationNamespace(int) */
  public String getBooleanAnnotationNamespace(int booleanAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getBooleanAnnotationValue(int) */
  public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex) {
    return null;
  }

  // - Channel property retrieval -

  /* @see MetadataRetrieve#getChannelAcquisitionMode(int, int) */
  public AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelAnnotationRef(int, int, int) */
  public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelColor(int, int) */
  public Integer getChannelColor(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelContrastMethod(int, int) */
  public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelEmissionWavelength(int, int) */
  public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelExcitationWavelength(int, int) */
  public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelFilterSetRef(int, int) */
  public String getChannelFilterSetRef(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelFluor(int, int) */
  public String getChannelFluor(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelID(int, int) */
  public String getChannelID(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelIlluminationType(int, int) */
  public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelLightSourceSettingsAttenuation(int, int) */
  public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelLightSourceSettingsID(int, int) */
  public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelLightSourceSettingsWavelength(int, int) */
  public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelNDFilter(int, int) */
  public Double getChannelNDFilter(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelName(int, int) */
  public String getChannelName(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelOTFRef(int, int) */
  public String getChannelOTFRef(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelPinholeSize(int, int) */
  public Double getChannelPinholeSize(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelPockelCellSetting(int, int) */
  public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelSamplesPerPixel(int, int) */
  public Integer getChannelSamplesPerPixel(int imageIndex, int channelIndex) {
    return null;
  }

  // - ChannelAnnotationRef property retrieval -


  // - Dataset property retrieval -

  /* @see MetadataRetrieve#getDatasetAnnotationRef(int, int) */
  public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetDescription(int) */
  public String getDatasetDescription(int datasetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetExperimenterRef(int) */
  public String getDatasetExperimenterRef(int datasetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetGroupRef(int) */
  public String getDatasetGroupRef(int datasetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetID(int) */
  public String getDatasetID(int datasetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetName(int) */
  public String getDatasetName(int datasetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDatasetProjectRef(int, int) */
  public String getDatasetProjectRef(int datasetIndex, int projectRefIndex) {
    return null;
  }

  // - DatasetAnnotationRef property retrieval -


  // - DatasetRef property retrieval -


  // - Detector property retrieval -

  /* @see MetadataRetrieve#getDetectorAmplificationGain(int, int) */
  public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorGain(int, int) */
  public Double getDetectorGain(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorID(int, int) */
  public String getDetectorID(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorLotNumber(int, int) */
  public String getDetectorLotNumber(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorManufacturer(int, int) */
  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorModel(int, int) */
  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorOffset(int, int) */
  public Double getDetectorOffset(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSerialNumber(int, int) */
  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorType(int, int) */
  public DetectorType getDetectorType(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorVoltage(int, int) */
  public Double getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorZoom(int, int) */
  public Double getDetectorZoom(int instrumentIndex, int detectorIndex) {
    return null;
  }

  // - DetectorSettings property retrieval -

  /* @see MetadataRetrieve#getDetectorSettingsBinning(int, int) */
  public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Double getDetectorSettingsGain(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsID(int, int) */
  public String getDetectorSettingsID(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Double getDetectorSettingsOffset(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsReadOutRate(int, int) */
  public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsVoltage(int, int) */
  public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex) {
    return null;
  }

  // - Dichroic property retrieval -

  /* @see MetadataRetrieve#getDichroicID(int, int) */
  public String getDichroicID(int instrumentIndex, int dichroicIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDichroicLotNumber(int, int) */
  public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDichroicManufacturer(int, int) */
  public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDichroicModel(int, int) */
  public String getDichroicModel(int instrumentIndex, int dichroicIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDichroicSerialNumber(int, int) */
  public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex) {
    return null;
  }

  // - DoubleAnnotation property retrieval -

  /* @see MetadataRetrieve#getDoubleAnnotationID(int) */
  public String getDoubleAnnotationID(int doubleAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDoubleAnnotationNamespace(int) */
  public String getDoubleAnnotationNamespace(int doubleAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDoubleAnnotationValue(int) */
  public Double getDoubleAnnotationValue(int doubleAnnotationIndex) {
    return null;
  }

  // - Ellipse property retrieval -

  /* @see MetadataRetrieve#getEllipseDescription(int, int) */
  public String getEllipseDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseFill(int, int) */
  public Integer getEllipseFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseFontSize(int, int) */
  public Integer getEllipseFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseID(int, int) */
  public String getEllipseID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseLabel(int, int) */
  public String getEllipseLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseName(int, int) */
  public String getEllipseName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseRadiusX(int, int) */
  public Double getEllipseRadiusX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseRadiusY(int, int) */
  public Double getEllipseRadiusY(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseStroke(int, int) */
  public Integer getEllipseStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseStrokeDashArray(int, int) */
  public String getEllipseStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseStrokeWidth(int, int) */
  public Double getEllipseStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseTheC(int, int) */
  public Integer getEllipseTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseTheT(int, int) */
  public Integer getEllipseTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseTheZ(int, int) */
  public Integer getEllipseTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseTransform(int, int) */
  public String getEllipseTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseX(int, int) */
  public Double getEllipseX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getEllipseY(int, int) */
  public Double getEllipseY(int roiIndex, int shapeIndex) {
    return null;
  }

  // - Experiment property retrieval -

  /* @see MetadataRetrieve#getExperimentDescription(int) */
  public String getExperimentDescription(int experimentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimentExperimenterRef(int) */
  public String getExperimentExperimenterRef(int experimentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimentID(int) */
  public String getExperimentID(int experimentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimentType(int) */
  public ExperimentType getExperimentType(int experimentIndex) {
    return null;
  }

  // - Experimenter property retrieval -

  /* @see MetadataRetrieve#getExperimenterAnnotationRef(int, int) */
  public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterDisplayName(int) */
  public String getExperimenterDisplayName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterGroupRef(int, int) */
  public String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterID(int) */
  public String getExperimenterID(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterMiddleName(int) */
  public String getExperimenterMiddleName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterUserName(int) */
  public String getExperimenterUserName(int experimenterIndex) {
    return null;
  }

  // - ExperimenterAnnotationRef property retrieval -


  // - ExperimenterGroupRef property retrieval -


  // - Filament property retrieval -

  /* @see MetadataRetrieve#getFilamentID(int, int) */
  public String getFilamentID(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentLotNumber(int, int) */
  public String getFilamentLotNumber(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentManufacturer(int, int) */
  public String getFilamentManufacturer(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentModel(int, int) */
  public String getFilamentModel(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentPower(int, int) */
  public Double getFilamentPower(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentSerialNumber(int, int) */
  public String getFilamentSerialNumber(int instrumentIndex, int filamentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentType(int, int) */
  public FilamentType getFilamentType(int instrumentIndex, int filamentIndex) {
    return null;
  }

  // - FileAnnotation property retrieval -

  /* @see MetadataRetrieve#getFileAnnotationBinaryFileFileName(int) */
  public String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFileAnnotationBinaryFileMIMEType(int) */
  public String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFileAnnotationBinaryFileSize(int) */
  public Integer getFileAnnotationBinaryFileSize(int fileAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFileAnnotationID(int) */
  public String getFileAnnotationID(int fileAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFileAnnotationNamespace(int) */
  public String getFileAnnotationNamespace(int fileAnnotationIndex) {
    return null;
  }

  // - Filter property retrieval -

  /* @see MetadataRetrieve#getFilterFilterWheel(int, int) */
  public String getFilterFilterWheel(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterID(int, int) */
  public String getFilterID(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterLotNumber(int, int) */
  public String getFilterLotNumber(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterManufacturer(int, int) */
  public String getFilterManufacturer(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterModel(int, int) */
  public String getFilterModel(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSerialNumber(int, int) */
  public String getFilterSerialNumber(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterType(int, int) */
  public FilterType getFilterType(int instrumentIndex, int filterIndex) {
    return null;
  }

  // - FilterSet property retrieval -

  /* @see MetadataRetrieve#getFilterSetDichroicRef(int, int) */
  public String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetEmissionFilterRef(int, int, int) */
  public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetExcitationFilterRef(int, int, int) */
  public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetID(int, int) */
  public String getFilterSetID(int instrumentIndex, int filterSetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetLotNumber(int, int) */
  public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetManufacturer(int, int) */
  public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetModel(int, int) */
  public String getFilterSetModel(int instrumentIndex, int filterSetIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilterSetSerialNumber(int, int) */
  public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex) {
    return null;
  }

  // - FilterSetEmissionFilterRef property retrieval -


  // - FilterSetExcitationFilterRef property retrieval -


  // - Group property retrieval -

  /* @see MetadataRetrieve#getGroupContact(int) */
  public String getGroupContact(int groupIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getGroupDescription(int) */
  public String getGroupDescription(int groupIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getGroupID(int) */
  public String getGroupID(int groupIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getGroupLeader(int) */
  public String getGroupLeader(int groupIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getGroupName(int) */
  public String getGroupName(int groupIndex) {
    return null;
  }

  // - Image property retrieval -

  /* @see MetadataRetrieve#getImageAcquiredDate(int) */
  public String getImageAcquiredDate(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageAnnotationRef(int, int) */
  public String getImageAnnotationRef(int imageIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageDatasetRef(int, int) */
  public String getImageDatasetRef(int imageIndex, int datasetRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageExperimentRef(int) */
  public String getImageExperimentRef(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageExperimenterRef(int) */
  public String getImageExperimenterRef(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageGroupRef(int) */
  public String getImageGroupRef(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageID(int) */
  public String getImageID(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageInstrumentRef(int) */
  public String getImageInstrumentRef(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageMicrobeamManipulationRef(int, int) */
  public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageObjectiveSettingsCorrectionCollar(int) */
  public Double getImageObjectiveSettingsCorrectionCollar(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageObjectiveSettingsID(int) */
  public String getImageObjectiveSettingsID(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageObjectiveSettingsMedium(int) */
  public Medium getImageObjectiveSettingsMedium(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageObjectiveSettingsRefractiveIndex(int) */
  public Double getImageObjectiveSettingsRefractiveIndex(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageROIRef(int, int) */
  public String getImageROIRef(int imageIndex, int roiRefIndex) {
    return null;
  }

  // - ImageAnnotationRef property retrieval -


  // - ImageROIRef property retrieval -


  // - ImagingEnvironment property retrieval -

  /* @see MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Double getImagingEnvironmentAirPressure(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public PercentFraction getImagingEnvironmentHumidity(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Double getImagingEnvironmentTemperature(int imageIndex) {
    return null;
  }

  // - Instrument property retrieval -

  /* @see MetadataRetrieve#getInstrumentID(int) */
  public String getInstrumentID(int instrumentIndex) {
    return null;
  }

  // - Laser property retrieval -

  /* @see MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserID(int, int) */
  public String getLaserID(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserLaserMedium(int, int) */
  public LaserMedium getLaserLaserMedium(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserLotNumber(int, int) */
  public String getLaserLotNumber(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserManufacturer(int, int) */
  public String getLaserManufacturer(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserModel(int, int) */
  public String getLaserModel(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPockelCell(int, int) */
  public Boolean getLaserPockelCell(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPower(int, int) */
  public Double getLaserPower(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPulse(int, int) */
  public Pulse getLaserPulse(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPump(int, int) */
  public String getLaserPump(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserRepetitionRate(int, int) */
  public Double getLaserRepetitionRate(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserSerialNumber(int, int) */
  public String getLaserSerialNumber(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserType(int, int) */
  public LaserType getLaserType(int instrumentIndex, int laserIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserWavelength(int, int) */
  public PositiveInteger getLaserWavelength(int instrumentIndex, int laserIndex) {
    return null;
  }

  // - LightEmittingDiode property retrieval -

  /* @see MetadataRetrieve#getLightEmittingDiodeID(int, int) */
  public String getLightEmittingDiodeID(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightEmittingDiodeLotNumber(int, int) */
  public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightEmittingDiodeManufacturer(int, int) */
  public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightEmittingDiodeModel(int, int) */
  public String getLightEmittingDiodeModel(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightEmittingDiodePower(int, int) */
  public Double getLightEmittingDiodePower(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightEmittingDiodeSerialNumber(int, int) */
  public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightEmittingDiodeIndex) {
    return null;
  }

  // - LightPath property retrieval -

  /* @see MetadataRetrieve#getLightPathDichroicRef(int, int) */
  public String getLightPathDichroicRef(int imageIndex, int channelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightPathEmissionFilterRef(int, int, int) */
  public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightPathExcitationFilterRef(int, int, int) */
  public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex) {
    return null;
  }

  // - LightPathEmissionFilterRef property retrieval -


  // - LightPathExcitationFilterRef property retrieval -


  // - Line property retrieval -

  /* @see MetadataRetrieve#getLineDescription(int, int) */
  public String getLineDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineFill(int, int) */
  public Integer getLineFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineFontSize(int, int) */
  public Integer getLineFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineID(int, int) */
  public String getLineID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineLabel(int, int) */
  public String getLineLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineName(int, int) */
  public String getLineName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineStroke(int, int) */
  public Integer getLineStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineStrokeDashArray(int, int) */
  public String getLineStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineStrokeWidth(int, int) */
  public Double getLineStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineTheC(int, int) */
  public Integer getLineTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineTheT(int, int) */
  public Integer getLineTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineTheZ(int, int) */
  public Integer getLineTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineTransform(int, int) */
  public String getLineTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineX1(int, int) */
  public Double getLineX1(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineX2(int, int) */
  public Double getLineX2(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineY1(int, int) */
  public Double getLineY1(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLineY2(int, int) */
  public Double getLineY2(int roiIndex, int shapeIndex) {
    return null;
  }

  // - ListAnnotation property retrieval -

  /* @see MetadataRetrieve#getListAnnotationAnnotationRef(int, int) */
  public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getListAnnotationID(int) */
  public String getListAnnotationID(int listAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getListAnnotationNamespace(int) */
  public String getListAnnotationNamespace(int listAnnotationIndex) {
    return null;
  }

  // - ListAnnotationAnnotationRef property retrieval -


  // - LongAnnotation property retrieval -

  /* @see MetadataRetrieve#getLongAnnotationID(int) */
  public String getLongAnnotationID(int longAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLongAnnotationNamespace(int) */
  public String getLongAnnotationNamespace(int longAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLongAnnotationValue(int) */
  public Long getLongAnnotationValue(int longAnnotationIndex) {
    return null;
  }

  // - Mask property retrieval -

  /* @see MetadataRetrieve#getMaskDescription(int, int) */
  public String getMaskDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskFill(int, int) */
  public Integer getMaskFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskFontSize(int, int) */
  public Integer getMaskFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskID(int, int) */
  public String getMaskID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskLabel(int, int) */
  public String getMaskLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskName(int, int) */
  public String getMaskName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskStroke(int, int) */
  public Integer getMaskStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskStrokeDashArray(int, int) */
  public String getMaskStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskStrokeWidth(int, int) */
  public Double getMaskStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskTheC(int, int) */
  public Integer getMaskTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskTheT(int, int) */
  public Integer getMaskTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskTheZ(int, int) */
  public Integer getMaskTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskTransform(int, int) */
  public String getMaskTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskX(int, int) */
  public Double getMaskX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMaskY(int, int) */
  public Double getMaskY(int roiIndex, int shapeIndex) {
    return null;
  }

  // - MicrobeamManipulation property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationExperimenterRef(int, int) */
  public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicrobeamManipulationID(int, int) */
  public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicrobeamManipulationROIRef(int, int, int) */
  public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int roiRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicrobeamManipulationType(int, int) */
  public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex) {
    return null;
  }

  // - MicrobeamManipulationLightSourceSettings property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsAttenuation(int, int, int) */
  public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsID(int, int, int) */
  public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsWavelength(int, int, int) */
  public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    return null;
  }

  // - MicrobeamManipulationROIRef property retrieval -


  // - MicrobeamManipulationRef property retrieval -


  // - Microscope property retrieval -

  /* @see MetadataRetrieve#getMicroscopeLotNumber(int) */
  public String getMicroscopeLotNumber(int instrumentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicroscopeManufacturer(int) */
  public String getMicroscopeManufacturer(int instrumentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicroscopeModel(int) */
  public String getMicroscopeModel(int instrumentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicroscopeSerialNumber(int) */
  public String getMicroscopeSerialNumber(int instrumentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getMicroscopeType(int) */
  public MicroscopeType getMicroscopeType(int instrumentIndex) {
    return null;
  }

  // - OTF property retrieval -

  /* @see MetadataRetrieve#getOTFBinaryFileFileName(int, int) */
  public String getOTFBinaryFileFileName(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFBinaryFileMIMEType(int, int) */
  public String getOTFBinaryFileMIMEType(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFBinaryFileSize(int, int) */
  public Integer getOTFBinaryFileSize(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFFilterSetRef(int, int) */
  public String getOTFFilterSetRef(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFID(int, int) */
  public String getOTFID(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFObjectiveSettingsCorrectionCollar(int, int) */
  public Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFObjectiveSettingsID(int, int) */
  public String getOTFObjectiveSettingsID(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFObjectiveSettingsMedium(int, int) */
  public Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFObjectiveSettingsRefractiveIndex(int, int) */
  public Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFSizeX(int, int) */
  public PositiveInteger getOTFSizeX(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFSizeY(int, int) */
  public PositiveInteger getOTFSizeY(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFType(int, int) */
  public PixelType getOTFType(int instrumentIndex, int otfIndex) {
    return null;
  }

  // - Objective property retrieval -

  /* @see MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveCorrection(int, int) */
  public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveID(int, int) */
  public String getObjectiveID(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveImmersion(int, int) */
  public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveIris(int, int) */
  public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveLotNumber(int, int) */
  public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveManufacturer(int, int) */
  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveModel(int, int) */
  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    return null;
  }

  // - Path property retrieval -

  /* @see MetadataRetrieve#getPathDefinition(int, int) */
  public String getPathDefinition(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathDescription(int, int) */
  public String getPathDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathFill(int, int) */
  public Integer getPathFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathFontSize(int, int) */
  public Integer getPathFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathID(int, int) */
  public String getPathID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathLabel(int, int) */
  public String getPathLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathName(int, int) */
  public String getPathName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathStroke(int, int) */
  public Integer getPathStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathStrokeDashArray(int, int) */
  public String getPathStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathStrokeWidth(int, int) */
  public Double getPathStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathTheC(int, int) */
  public Integer getPathTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathTheT(int, int) */
  public Integer getPathTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathTheZ(int, int) */
  public Integer getPathTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPathTransform(int, int) */
  public String getPathTransform(int roiIndex, int shapeIndex) {
    return null;
  }

  // - Pixels property retrieval -

  /* @see MetadataRetrieve#getPixelsAnnotationRef(int, int) */
  public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsDimensionOrder(int) */
  public DimensionOrder getPixelsDimensionOrder(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsID(int) */
  public String getPixelsID(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsPhysicalSizeX(int) */
  public Double getPixelsPhysicalSizeX(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsPhysicalSizeY(int) */
  public Double getPixelsPhysicalSizeY(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsPhysicalSizeZ(int) */
  public Double getPixelsPhysicalSizeZ(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeC(int) */
  public PositiveInteger getPixelsSizeC(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeT(int) */
  public PositiveInteger getPixelsSizeT(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeX(int) */
  public PositiveInteger getPixelsSizeX(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeY(int) */
  public PositiveInteger getPixelsSizeY(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeZ(int) */
  public PositiveInteger getPixelsSizeZ(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsTimeIncrement(int) */
  public Double getPixelsTimeIncrement(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsType(int) */
  public PixelType getPixelsType(int imageIndex) {
    return null;
  }

  // - PixelsAnnotationRef property retrieval -


  // - PixelsBinData property retrieval -

  /* @see MetadataRetrieve#getPixelsBinDataBigEndian(int, int) */
  public Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex) {
    return null;
  }

  // - Plane property retrieval -

  /* @see MetadataRetrieve#getPlaneAnnotationRef(int, int, int) */
  public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneDeltaT(int, int) */
  public Double getPlaneDeltaT(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneExposureTime(int, int) */
  public Double getPlaneExposureTime(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneHashSHA1(int, int) */
  public String getPlaneHashSHA1(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlanePositionX(int, int) */
  public Double getPlanePositionX(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlanePositionY(int, int) */
  public Double getPlanePositionY(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlanePositionZ(int, int) */
  public Double getPlanePositionZ(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTheC(int, int) */
  public Integer getPlaneTheC(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTheT(int, int) */
  public Integer getPlaneTheT(int imageIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTheZ(int, int) */
  public Integer getPlaneTheZ(int imageIndex, int planeIndex) {
    return null;
  }

  // - PlaneAnnotationRef property retrieval -


  // - Plate property retrieval -

  /* @see MetadataRetrieve#getPlateAnnotationRef(int, int) */
  public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateColumnNamingConvention(int) */
  public NamingConvention getPlateColumnNamingConvention(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateColumns(int) */
  public Integer getPlateColumns(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateDescription(int) */
  public String getPlateDescription(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateExternalIdentifier(int) */
  public String getPlateExternalIdentifier(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateID(int) */
  public String getPlateID(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateName(int) */
  public String getPlateName(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateRowNamingConvention(int) */
  public NamingConvention getPlateRowNamingConvention(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateRows(int) */
  public Integer getPlateRows(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateScreenRef(int, int) */
  public String getPlateScreenRef(int plateIndex, int screenRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateStatus(int) */
  public String getPlateStatus(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateWellOriginX(int) */
  public Double getPlateWellOriginX(int plateIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateWellOriginY(int) */
  public Double getPlateWellOriginY(int plateIndex) {
    return null;
  }

  // - PlateAcquisition property retrieval -

  /* @see MetadataRetrieve#getPlateAcquisitionAnnotationRef(int, int, int) */
  public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionDescription(int, int) */
  public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionEndTime(int, int) */
  public String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionID(int, int) */
  public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionMaximumFieldCount(int, int) */
  public Integer getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionName(int, int) */
  public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionStartTime(int, int) */
  public String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlateAcquisitionWellSampleRef(int, int, int) */
  public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex) {
    return null;
  }

  // - PlateAcquisitionAnnotationRef property retrieval -


  // - PlateAnnotationRef property retrieval -


  // - PlateRef property retrieval -


  // - Point property retrieval -

  /* @see MetadataRetrieve#getPointDescription(int, int) */
  public String getPointDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointFill(int, int) */
  public Integer getPointFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointFontSize(int, int) */
  public Integer getPointFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointID(int, int) */
  public String getPointID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointLabel(int, int) */
  public String getPointLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointName(int, int) */
  public String getPointName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointStroke(int, int) */
  public Integer getPointStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointStrokeDashArray(int, int) */
  public String getPointStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointStrokeWidth(int, int) */
  public Double getPointStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointTheC(int, int) */
  public Integer getPointTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointTheT(int, int) */
  public Integer getPointTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointTheZ(int, int) */
  public Integer getPointTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointTransform(int, int) */
  public String getPointTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointX(int, int) */
  public Double getPointX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPointY(int, int) */
  public Double getPointY(int roiIndex, int shapeIndex) {
    return null;
  }

  // - Polyline property retrieval -

  /* @see MetadataRetrieve#getPolylineClosed(int, int) */
  public Boolean getPolylineClosed(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineDescription(int, int) */
  public String getPolylineDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineFill(int, int) */
  public Integer getPolylineFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineFontSize(int, int) */
  public Integer getPolylineFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineID(int, int) */
  public String getPolylineID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineLabel(int, int) */
  public String getPolylineLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineName(int, int) */
  public String getPolylineName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylinePoints(int, int) */
  public String getPolylinePoints(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineStroke(int, int) */
  public Integer getPolylineStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineStrokeDashArray(int, int) */
  public String getPolylineStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineStrokeWidth(int, int) */
  public Double getPolylineStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineTheC(int, int) */
  public Integer getPolylineTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineTheT(int, int) */
  public Integer getPolylineTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineTheZ(int, int) */
  public Integer getPolylineTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPolylineTransform(int, int) */
  public String getPolylineTransform(int roiIndex, int shapeIndex) {
    return null;
  }

  // - Project property retrieval -

  /* @see MetadataRetrieve#getProjectAnnotationRef(int, int) */
  public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getProjectDescription(int) */
  public String getProjectDescription(int projectIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getProjectExperimenterRef(int) */
  public String getProjectExperimenterRef(int projectIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getProjectGroupRef(int) */
  public String getProjectGroupRef(int projectIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getProjectID(int) */
  public String getProjectID(int projectIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getProjectName(int) */
  public String getProjectName(int projectIndex) {
    return null;
  }

  // - ProjectAnnotationRef property retrieval -


  // - ProjectRef property retrieval -


  // - ROI property retrieval -

  /* @see MetadataRetrieve#getROIAnnotationRef(int, int) */
  public String getROIAnnotationRef(int roiIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIDescription(int) */
  public String getROIDescription(int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIID(int) */
  public String getROIID(int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIName(int) */
  public String getROIName(int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROINamespace(int) */
  public String getROINamespace(int roiIndex) {
    return null;
  }

  // - ROIAnnotationRef property retrieval -


  // - Reagent property retrieval -

  /* @see MetadataRetrieve#getReagentAnnotationRef(int, int, int) */
  public String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getReagentDescription(int, int) */
  public String getReagentDescription(int screenIndex, int reagentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getReagentID(int, int) */
  public String getReagentID(int screenIndex, int reagentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getReagentName(int, int) */
  public String getReagentName(int screenIndex, int reagentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getReagentReagentIdentifier(int, int) */
  public String getReagentReagentIdentifier(int screenIndex, int reagentIndex) {
    return null;
  }

  // - ReagentAnnotationRef property retrieval -


  // - Rectangle property retrieval -

  /* @see MetadataRetrieve#getRectangleDescription(int, int) */
  public String getRectangleDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleFill(int, int) */
  public Integer getRectangleFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleFontSize(int, int) */
  public Integer getRectangleFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleHeight(int, int) */
  public Double getRectangleHeight(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleID(int, int) */
  public String getRectangleID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleLabel(int, int) */
  public String getRectangleLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleName(int, int) */
  public String getRectangleName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleStroke(int, int) */
  public Integer getRectangleStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleStrokeDashArray(int, int) */
  public String getRectangleStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleStrokeWidth(int, int) */
  public Double getRectangleStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleTheC(int, int) */
  public Integer getRectangleTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleTheT(int, int) */
  public Integer getRectangleTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleTheZ(int, int) */
  public Integer getRectangleTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleTransform(int, int) */
  public String getRectangleTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleWidth(int, int) */
  public Double getRectangleWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleX(int, int) */
  public Double getRectangleX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getRectangleY(int, int) */
  public Double getRectangleY(int roiIndex, int shapeIndex) {
    return null;
  }

  // - Screen property retrieval -

  /* @see MetadataRetrieve#getScreenAnnotationRef(int, int) */
  public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenDescription(int) */
  public String getScreenDescription(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenID(int) */
  public String getScreenID(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenName(int) */
  public String getScreenName(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenPlateRef(int, int) */
  public String getScreenPlateRef(int screenIndex, int plateRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenProtocolDescription(int) */
  public String getScreenProtocolDescription(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenProtocolIdentifier(int) */
  public String getScreenProtocolIdentifier(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenReagentSetDescription(int) */
  public String getScreenReagentSetDescription(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenReagentSetIdentifier(int) */
  public String getScreenReagentSetIdentifier(int screenIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getScreenType(int) */
  public String getScreenType(int screenIndex) {
    return null;
  }

  // - ScreenAnnotationRef property retrieval -


  // - ScreenRef property retrieval -


  // - ShapeAnnotationRef property retrieval -


  // - StageLabel property retrieval -

  /* @see MetadataRetrieve#getStageLabelName(int) */
  public String getStageLabelName(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelX(int) */
  public Double getStageLabelX(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelY(int) */
  public Double getStageLabelY(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelZ(int) */
  public Double getStageLabelZ(int imageIndex) {
    return null;
  }

  // - StringAnnotation property retrieval -

  /* @see MetadataRetrieve#getStringAnnotationID(int) */
  public String getStringAnnotationID(int stringAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStringAnnotationNamespace(int) */
  public String getStringAnnotationNamespace(int stringAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStringAnnotationValue(int) */
  public String getStringAnnotationValue(int stringAnnotationIndex) {
    return null;
  }

  // - Text property retrieval -

  /* @see MetadataRetrieve#getTextDescription(int, int) */
  public String getTextDescription(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextFill(int, int) */
  public Integer getTextFill(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextFontSize(int, int) */
  public Integer getTextFontSize(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextID(int, int) */
  public String getTextID(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextLabel(int, int) */
  public String getTextLabel(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextName(int, int) */
  public String getTextName(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextStroke(int, int) */
  public Integer getTextStroke(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextStrokeDashArray(int, int) */
  public String getTextStrokeDashArray(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextStrokeWidth(int, int) */
  public Double getTextStrokeWidth(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextTheC(int, int) */
  public Integer getTextTheC(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextTheT(int, int) */
  public Integer getTextTheT(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextTheZ(int, int) */
  public Integer getTextTheZ(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextTransform(int, int) */
  public String getTextTransform(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextValue(int, int) */
  public String getTextValue(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextX(int, int) */
  public Double getTextX(int roiIndex, int shapeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTextY(int, int) */
  public Double getTextY(int roiIndex, int shapeIndex) {
    return null;
  }

  // - TiffData property retrieval -

  /* @see MetadataRetrieve#getTiffDataFirstC(int, int) */
  public Integer getTiffDataFirstC(int imageIndex, int tiffDataIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTiffDataFirstT(int, int) */
  public Integer getTiffDataFirstT(int imageIndex, int tiffDataIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTiffDataFirstZ(int, int) */
  public Integer getTiffDataFirstZ(int imageIndex, int tiffDataIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTiffDataIFD(int, int) */
  public Integer getTiffDataIFD(int imageIndex, int tiffDataIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTiffDataPlaneCount(int, int) */
  public Integer getTiffDataPlaneCount(int imageIndex, int tiffDataIndex) {
    return null;
  }

  // - TimestampAnnotation property retrieval -

  /* @see MetadataRetrieve#getTimestampAnnotationID(int) */
  public String getTimestampAnnotationID(int timestampAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTimestampAnnotationNamespace(int) */
  public String getTimestampAnnotationNamespace(int timestampAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTimestampAnnotationValue(int) */
  public String getTimestampAnnotationValue(int timestampAnnotationIndex) {
    return null;
  }

  // - TransmittanceRange property retrieval -

  /* @see MetadataRetrieve#getTransmittanceRangeCutIn(int, int) */
  public Integer getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTransmittanceRangeCutInTolerance(int, int) */
  public Integer getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTransmittanceRangeCutOut(int, int) */
  public Integer getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTransmittanceRangeCutOutTolerance(int, int) */
  public Integer getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getTransmittanceRangeTransmittance(int, int) */
  public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex) {
    return null;
  }

  // - UUID property retrieval -

  public String getUUIDValue(int imageIndex, int tiffDataIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getUUIDFileName(int, int) */
  public String getUUIDFileName(int imageIndex, int tiffDataIndex) {
    return null;
  }

  // - Well property retrieval -

  /* @see MetadataRetrieve#getWellAnnotationRef(int, int, int) */
  public String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellColor(int, int) */
  public Integer getWellColor(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellColumn(int, int) */
  public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellExternalDescription(int, int) */
  public String getWellExternalDescription(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellExternalIdentifier(int, int) */
  public String getWellExternalIdentifier(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellID(int, int) */
  public String getWellID(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellReagentRef(int, int) */
  public String getWellReagentRef(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellRow(int, int) */
  public NonNegativeInteger getWellRow(int plateIndex, int wellIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellStatus(int, int) */
  public String getWellStatus(int plateIndex, int wellIndex) {
    return null;
  }

  // - WellAnnotationRef property retrieval -


  // - WellSample property retrieval -

  /* @see MetadataRetrieve#getWellSampleAnnotationRef(int, int, int, int) */
  public String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSampleID(int, int, int) */
  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSampleImageRef(int, int, int) */
  public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSampleIndex(int, int, int) */
  public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSamplePositionX(int, int, int) */
  public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSamplePositionY(int, int, int) */
  public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getWellSampleTimepoint(int, int, int) */
  public Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex) {
    return null;
  }

  // - WellSampleAnnotationRef property retrieval -


  // - WellSampleRef property retrieval -


  // - XMLAnnotation property retrieval -

  /* @see MetadataRetrieve#getXMLAnnotationID(int) */
  public String getXMLAnnotationID(int xmlAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getXMLAnnotationNamespace(int) */
  public String getXMLAnnotationNamespace(int xmlAnnotationIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getXMLAnnotationValue(int) */
  public String getXMLAnnotationValue(int xmlAnnotationIndex) {
    return null;
  }

  // -- MetadataStore API methods --

  /* @see MetadataStore#createRoot() */
  public void createRoot() {
  }

  /* @see MetadataStore#getRoot() */
  public Object getRoot() {
    return null;
  }

  /* @see MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
  }

  /* @see MetadataStore#setUUID() */
  public void setUUID(String uuid) {
  }

  // - Arc property storage -

  /* @see MetadataStore#setArcID(String, int, int) */
  public void setArcID(String id, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcLotNumber(String, int, int) */
  public void setArcLotNumber(String lotNumber, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcManufacturer(String, int, int) */
  public void setArcManufacturer(String manufacturer, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcModel(String, int, int) */
  public void setArcModel(String model, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcPower(Double, int, int) */
  public void setArcPower(Double power, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcSerialNumber(String, int, int) */
  public void setArcSerialNumber(String serialNumber, int instrumentIndex, int arcIndex) {
  }

  /* @see MetadataStore#setArcType(ArcType, int, int) */
  public void setArcType(ArcType type, int instrumentIndex, int arcIndex) {
  }

  // - BooleanAnnotation property storage -

  /* @see MetadataStore#setBooleanAnnotationID(String, int) */
  public void setBooleanAnnotationID(String id, int booleanAnnotationIndex) {
  }

  /* @see MetadataStore#setBooleanAnnotationNamespace(String, int) */
  public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex) {
  }

  /* @see MetadataStore#setBooleanAnnotationValue(Boolean, int) */
  public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex) {
  }

  // - Channel property storage -

  /* @see MetadataStore#setChannelAcquisitionMode(AcquisitionMode, int, int) */
  public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelAnnotationRef(String, int, int, int) */
  public void setChannelAnnotationRef(String annotationRef, int imageIndex, int channelIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setChannelColor(Integer, int, int) */
  public void setChannelColor(Integer color, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelContrastMethod(ContrastMethod, int, int) */
  public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelEmissionWavelength(PositiveInteger, int, int) */
  public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelExcitationWavelength(PositiveInteger, int, int) */
  public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelFilterSetRef(String, int, int) */
  public void setChannelFilterSetRef(String filterSetRef, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelFluor(String, int, int) */
  public void setChannelFluor(String fluor, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelID(String, int, int) */
  public void setChannelID(String id, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelIlluminationType(IlluminationType, int, int) */
  public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelLightSourceSettingsAttenuation(PercentFraction, int, int) */
  public void setChannelLightSourceSettingsAttenuation(PercentFraction lightSourceSettingsAttenuation, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelLightSourceSettingsID(String, int, int) */
  public void setChannelLightSourceSettingsID(String lightSourceSettingsID, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelLightSourceSettingsWavelength(PositiveInteger, int, int) */
  public void setChannelLightSourceSettingsWavelength(PositiveInteger lightSourceSettingsWavelength, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelNDFilter(Double, int, int) */
  public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelName(String, int, int) */
  public void setChannelName(String name, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelOTFRef(String, int, int) */
  public void setChannelOTFRef(String otfRef, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelPinholeSize(Double, int, int) */
  public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelPockelCellSetting(Integer, int, int) */
  public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setChannelSamplesPerPixel(Integer, int, int) */
  public void setChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int channelIndex) {
  }

  // - ChannelAnnotationRef property storage -

  // - Dataset property storage -

  /* @see MetadataStore#setDatasetAnnotationRef(String, int, int) */
  public void setDatasetAnnotationRef(String annotationRef, int datasetIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setDatasetDescription(String, int) */
  public void setDatasetDescription(String description, int datasetIndex) {
  }

  /* @see MetadataStore#setDatasetExperimenterRef(String, int) */
  public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex) {
  }

  /* @see MetadataStore#setDatasetGroupRef(String, int) */
  public void setDatasetGroupRef(String groupRef, int datasetIndex) {
  }

  /* @see MetadataStore#setDatasetID(String, int) */
  public void setDatasetID(String id, int datasetIndex) {
  }

  /* @see MetadataStore#setDatasetName(String, int) */
  public void setDatasetName(String name, int datasetIndex) {
  }

  /* @see MetadataStore#setDatasetProjectRef(String, int, int) */
  public void setDatasetProjectRef(String projectRef, int datasetIndex, int projectRefIndex) {
  }

  // - DatasetAnnotationRef property storage -

  // - DatasetRef property storage -

  // - Detector property storage -

  /* @see MetadataStore#setDetectorAmplificationGain(Double, int, int) */
  public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorGain(Double, int, int) */
  public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorID(String, int, int) */
  public void setDetectorID(String id, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorLotNumber(String, int, int) */
  public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorOffset(Double, int, int) */
  public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorType(DetectorType, int, int) */
  public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorVoltage(Double, int, int) */
  public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorZoom(Double, int, int) */
  public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex) {
  }

  // - DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsBinning(Binning, int, int) */
  public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsGain(Double, int, int) */
  public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsID(String, int, int) */
  public void setDetectorSettingsID(String id, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Double, int, int) */
  public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Double, int, int) */
  public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Double, int, int) */
  public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex) {
  }

  // - Dichroic property storage -

  /* @see MetadataStore#setDichroicID(String, int, int) */
  public void setDichroicID(String id, int instrumentIndex, int dichroicIndex) {
  }

  /* @see MetadataStore#setDichroicLotNumber(String, int, int) */
  public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex) {
  }

  /* @see MetadataStore#setDichroicManufacturer(String, int, int) */
  public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex) {
  }

  /* @see MetadataStore#setDichroicModel(String, int, int) */
  public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex) {
  }

  /* @see MetadataStore#setDichroicSerialNumber(String, int, int) */
  public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex) {
  }

  // - DoubleAnnotation property storage -

  /* @see MetadataStore#setDoubleAnnotationID(String, int) */
  public void setDoubleAnnotationID(String id, int doubleAnnotationIndex) {
  }

  /* @see MetadataStore#setDoubleAnnotationNamespace(String, int) */
  public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex) {
  }

  /* @see MetadataStore#setDoubleAnnotationValue(Double, int) */
  public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex) {
  }

  // - Ellipse property storage -

  /* @see MetadataStore#setEllipseDescription(String, int, int) */
  public void setEllipseDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseFill(Integer, int, int) */
  public void setEllipseFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseFontSize(Integer, int, int) */
  public void setEllipseFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseID(String, int, int) */
  public void setEllipseID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseLabel(String, int, int) */
  public void setEllipseLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseName(String, int, int) */
  public void setEllipseName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseRadiusX(Double, int, int) */
  public void setEllipseRadiusX(Double radiusX, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseRadiusY(Double, int, int) */
  public void setEllipseRadiusY(Double radiusY, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseStroke(Integer, int, int) */
  public void setEllipseStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseStrokeDashArray(String, int, int) */
  public void setEllipseStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseStrokeWidth(Double, int, int) */
  public void setEllipseStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseTheC(Integer, int, int) */
  public void setEllipseTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseTheT(Integer, int, int) */
  public void setEllipseTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseTheZ(Integer, int, int) */
  public void setEllipseTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseTransform(String, int, int) */
  public void setEllipseTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseX(Double, int, int) */
  public void setEllipseX(Double x, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setEllipseY(Double, int, int) */
  public void setEllipseY(Double y, int roiIndex, int shapeIndex) {
  }

  // - Experiment property storage -

  /* @see MetadataStore#setExperimentDescription(String, int) */
  public void setExperimentDescription(String description, int experimentIndex) {
  }

  /* @see MetadataStore#setExperimentExperimenterRef(String, int) */
  public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex) {
  }

  /* @see MetadataStore#setExperimentID(String, int) */
  public void setExperimentID(String id, int experimentIndex) {
  }

  /* @see MetadataStore#setExperimentType(ExperimentType, int) */
  public void setExperimentType(ExperimentType type, int experimentIndex) {
  }

  // - Experimenter property storage -

  /* @see MetadataStore#setExperimenterAnnotationRef(String, int, int) */
  public void setExperimenterAnnotationRef(String annotationRef, int experimenterIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setExperimenterDisplayName(String, int) */
  public void setExperimenterDisplayName(String displayName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterGroupRef(String, int, int) */
  public void setExperimenterGroupRef(String groupRef, int experimenterIndex, int groupRefIndex) {
  }

  /* @see MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterMiddleName(String, int) */
  public void setExperimenterMiddleName(String middleName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterUserName(String, int) */
  public void setExperimenterUserName(String userName, int experimenterIndex) {
  }

  // - ExperimenterAnnotationRef property storage -

  // - ExperimenterGroupRef property storage -

  // - Filament property storage -

  /* @see MetadataStore#setFilamentID(String, int, int) */
  public void setFilamentID(String id, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentLotNumber(String, int, int) */
  public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentManufacturer(String, int, int) */
  public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentModel(String, int, int) */
  public void setFilamentModel(String model, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentPower(Double, int, int) */
  public void setFilamentPower(Double power, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentSerialNumber(String, int, int) */
  public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int filamentIndex) {
  }

  /* @see MetadataStore#setFilamentType(FilamentType, int, int) */
  public void setFilamentType(FilamentType type, int instrumentIndex, int filamentIndex) {
  }

  // - FileAnnotation property storage -

  /* @see MetadataStore#setFileAnnotationBinaryFileFileName(String, int) */
  public void setFileAnnotationBinaryFileFileName(String binaryFileFileName, int fileAnnotationIndex) {
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileMIMEType(String, int) */
  public void setFileAnnotationBinaryFileMIMEType(String binaryFileMIMEType, int fileAnnotationIndex) {
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileSize(Integer, int) */
  public void setFileAnnotationBinaryFileSize(Integer binaryFileSize, int fileAnnotationIndex) {
  }

  /* @see MetadataStore#setFileAnnotationID(String, int) */
  public void setFileAnnotationID(String id, int fileAnnotationIndex) {
  }

  /* @see MetadataStore#setFileAnnotationNamespace(String, int) */
  public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex) {
  }

  // - Filter property storage -

  /* @see MetadataStore#setFilterFilterWheel(String, int, int) */
  public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterID(String, int, int) */
  public void setFilterID(String id, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterLotNumber(String, int, int) */
  public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterManufacturer(String, int, int) */
  public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterModel(String, int, int) */
  public void setFilterModel(String model, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterSerialNumber(String, int, int) */
  public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setFilterType(FilterType, int, int) */
  public void setFilterType(FilterType type, int instrumentIndex, int filterIndex) {
  }

  // - FilterSet property storage -

  /* @see MetadataStore#setFilterSetDichroicRef(String, int, int) */
  public void setFilterSetDichroicRef(String dichroicRef, int instrumentIndex, int filterSetIndex) {
  }

  /* @see MetadataStore#setFilterSetEmissionFilterRef(String, int, int, int) */
  public void setFilterSetEmissionFilterRef(String emissionFilterRef, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex) {
  }

  /* @see MetadataStore#setFilterSetExcitationFilterRef(String, int, int, int) */
  public void setFilterSetExcitationFilterRef(String excitationFilterRef, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex) {
  }

  /* @see MetadataStore#setFilterSetID(String, int, int) */
  public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex) {
  }

  /* @see MetadataStore#setFilterSetLotNumber(String, int, int) */
  public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex) {
  }

  /* @see MetadataStore#setFilterSetManufacturer(String, int, int) */
  public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex) {
  }

  /* @see MetadataStore#setFilterSetModel(String, int, int) */
  public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex) {
  }

  /* @see MetadataStore#setFilterSetSerialNumber(String, int, int) */
  public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex) {
  }

  // - FilterSetEmissionFilterRef property storage -

  // - FilterSetExcitationFilterRef property storage -

  // - Group property storage -

  /* @see MetadataStore#setGroupContact(String, int) */
  public void setGroupContact(String contact, int groupIndex) {
  }

  /* @see MetadataStore#setGroupDescription(String, int) */
  public void setGroupDescription(String description, int groupIndex) {
  }

  /* @see MetadataStore#setGroupID(String, int) */
  public void setGroupID(String id, int groupIndex) {
  }

  /* @see MetadataStore#setGroupLeader(String, int) */
  public void setGroupLeader(String leader, int groupIndex) {
  }

  /* @see MetadataStore#setGroupName(String, int) */
  public void setGroupName(String name, int groupIndex) {
  }

  // - Image property storage -

  /* @see MetadataStore#setImageAcquiredDate(String, int) */
  public void setImageAcquiredDate(String acquiredDate, int imageIndex) {
  }

  /* @see MetadataStore#setImageAnnotationRef(String, int, int) */
  public void setImageAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setImageDatasetRef(String, int, int) */
  public void setImageDatasetRef(String datasetRef, int imageIndex, int datasetRefIndex) {
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
  }

  /* @see MetadataStore#setImageExperimentRef(String, int) */
  public void setImageExperimentRef(String experimentRef, int imageIndex) {
  }

  /* @see MetadataStore#setImageExperimenterRef(String, int) */
  public void setImageExperimenterRef(String experimenterRef, int imageIndex) {
  }

  /* @see MetadataStore#setImageGroupRef(String, int) */
  public void setImageGroupRef(String groupRef, int imageIndex) {
  }

  /* @see MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
  }

  /* @see MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
  }

  /* @see MetadataStore#setImageMicrobeamManipulationRef(String, int, int) */
  public void setImageMicrobeamManipulationRef(String microbeamManipulationRef, int imageIndex, int microbeamManipulationRefIndex) {
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setImageObjectiveSettingsCorrectionCollar(Double, int) */
  public void setImageObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int imageIndex) {
  }

  /* @see MetadataStore#setImageObjectiveSettingsID(String, int) */
  public void setImageObjectiveSettingsID(String objectiveSettingsID, int imageIndex) {
  }

  /* @see MetadataStore#setImageObjectiveSettingsMedium(Medium, int) */
  public void setImageObjectiveSettingsMedium(Medium objectiveSettingsMedium, int imageIndex) {
  }

  /* @see MetadataStore#setImageObjectiveSettingsRefractiveIndex(Double, int) */
  public void setImageObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int imageIndex) {
  }

  /* @see MetadataStore#setImageROIRef(String, int, int) */
  public void setImageROIRef(String roiRef, int imageIndex, int roiRefIndex) {
  }

  // - ImageAnnotationRef property storage -

  // - ImageROIRef property storage -

  // - ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Double, int) */
  public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(PercentFraction, int) */
  public void setImagingEnvironmentCO2Percent(PercentFraction cO2Percent, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(PercentFraction, int) */
  public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Double, int) */
  public void setImagingEnvironmentTemperature(Double temperature, int imageIndex) {
  }

  // - Instrument property storage -

  /* @see MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
  }

  // - Laser property storage -

  /* @see MetadataStore#setLaserFrequencyMultiplication(PositiveInteger, int, int) */
  public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserID(String, int, int) */
  public void setLaserID(String id, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserLaserMedium(LaserMedium, int, int) */
  public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserLotNumber(String, int, int) */
  public void setLaserLotNumber(String lotNumber, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserManufacturer(String, int, int) */
  public void setLaserManufacturer(String manufacturer, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserModel(String, int, int) */
  public void setLaserModel(String model, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserPockelCell(Boolean, int, int) */
  public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserPower(Double, int, int) */
  public void setLaserPower(Double power, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserPulse(Pulse, int, int) */
  public void setLaserPulse(Pulse pulse, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserPump(String, int, int) */
  public void setLaserPump(String pump, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserRepetitionRate(Double, int, int) */
  public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserSerialNumber(String, int, int) */
  public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserType(LaserType, int, int) */
  public void setLaserType(LaserType type, int instrumentIndex, int laserIndex) {
  }

  /* @see MetadataStore#setLaserWavelength(PositiveInteger, int, int) */
  public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int laserIndex) {
  }

  // - LightEmittingDiode property storage -

  /* @see MetadataStore#setLightEmittingDiodeID(String, int, int) */
  public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  /* @see MetadataStore#setLightEmittingDiodeLotNumber(String, int, int) */
  public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  /* @see MetadataStore#setLightEmittingDiodeManufacturer(String, int, int) */
  public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  /* @see MetadataStore#setLightEmittingDiodeModel(String, int, int) */
  public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  /* @see MetadataStore#setLightEmittingDiodePower(Double, int, int) */
  public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  /* @see MetadataStore#setLightEmittingDiodeSerialNumber(String, int, int) */
  public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
  }

  // - LightPath property storage -

  /* @see MetadataStore#setLightPathDichroicRef(String, int, int) */
  public void setLightPathDichroicRef(String dichroicRef, int imageIndex, int channelIndex) {
  }

  /* @see MetadataStore#setLightPathEmissionFilterRef(String, int, int, int) */
  public void setLightPathEmissionFilterRef(String emissionFilterRef, int imageIndex, int channelIndex, int emissionFilterRefIndex) {
  }

  /* @see MetadataStore#setLightPathExcitationFilterRef(String, int, int, int) */
  public void setLightPathExcitationFilterRef(String excitationFilterRef, int imageIndex, int channelIndex, int excitationFilterRefIndex) {
  }

  // - LightPathEmissionFilterRef property storage -

  // - LightPathExcitationFilterRef property storage -

  // - Line property storage -

  /* @see MetadataStore#setLineDescription(String, int, int) */
  public void setLineDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineFill(Integer, int, int) */
  public void setLineFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineFontSize(Integer, int, int) */
  public void setLineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineID(String, int, int) */
  public void setLineID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineLabel(String, int, int) */
  public void setLineLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineName(String, int, int) */
  public void setLineName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineStroke(Integer, int, int) */
  public void setLineStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineStrokeDashArray(String, int, int) */
  public void setLineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineStrokeWidth(Double, int, int) */
  public void setLineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineTheC(Integer, int, int) */
  public void setLineTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineTheT(Integer, int, int) */
  public void setLineTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineTheZ(Integer, int, int) */
  public void setLineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineTransform(String, int, int) */
  public void setLineTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineX1(Double, int, int) */
  public void setLineX1(Double x1, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineX2(Double, int, int) */
  public void setLineX2(Double x2, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineY1(Double, int, int) */
  public void setLineY1(Double y1, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setLineY2(Double, int, int) */
  public void setLineY2(Double y2, int roiIndex, int shapeIndex) {
  }

  // - ListAnnotation property storage -

  /* @see MetadataStore#setListAnnotationAnnotationRef(String, int, int) */
  public void setListAnnotationAnnotationRef(String annotationRef, int listAnnotationIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setListAnnotationID(String, int) */
  public void setListAnnotationID(String id, int listAnnotationIndex) {
  }

  /* @see MetadataStore#setListAnnotationNamespace(String, int) */
  public void setListAnnotationNamespace(String namespace, int listAnnotationIndex) {
  }

  // - ListAnnotationAnnotationRef property storage -

  // - LongAnnotation property storage -

  /* @see MetadataStore#setLongAnnotationID(String, int) */
  public void setLongAnnotationID(String id, int longAnnotationIndex) {
  }

  /* @see MetadataStore#setLongAnnotationNamespace(String, int) */
  public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex) {
  }

  /* @see MetadataStore#setLongAnnotationValue(Long, int) */
  public void setLongAnnotationValue(Long value, int longAnnotationIndex) {
  }

  // - Mask property storage -

  /* @see MetadataStore#setMaskDescription(String, int, int) */
  public void setMaskDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskFill(Integer, int, int) */
  public void setMaskFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskFontSize(Integer, int, int) */
  public void setMaskFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskID(String, int, int) */
  public void setMaskID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskLabel(String, int, int) */
  public void setMaskLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskName(String, int, int) */
  public void setMaskName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskStroke(Integer, int, int) */
  public void setMaskStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskStrokeDashArray(String, int, int) */
  public void setMaskStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskStrokeWidth(Double, int, int) */
  public void setMaskStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskTheC(Integer, int, int) */
  public void setMaskTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskTheT(Integer, int, int) */
  public void setMaskTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskTheZ(Integer, int, int) */
  public void setMaskTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskTransform(String, int, int) */
  public void setMaskTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskX(Double, int, int) */
  public void setMaskX(Double x, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setMaskY(Double, int, int) */
  public void setMaskY(Double y, int roiIndex, int shapeIndex) {
  }

  // - MicrobeamManipulation property storage -

  /* @see MetadataStore#setMicrobeamManipulationExperimenterRef(String, int, int) */
  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int experimentIndex, int microbeamManipulationIndex) {
  }

  /* @see MetadataStore#setMicrobeamManipulationID(String, int, int) */
  public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex) {
  }

  /* @see MetadataStore#setMicrobeamManipulationROIRef(String, int, int, int) */
  public void setMicrobeamManipulationROIRef(String roiRef, int experimentIndex, int microbeamManipulationIndex, int roiRefIndex) {
  }

  /* @see MetadataStore#setMicrobeamManipulationType(MicrobeamManipulationType, int, int) */
  public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex) {
  }

  // - MicrobeamManipulationLightSourceSettings property storage -

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsID(String, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
  }

  // - MicrobeamManipulationROIRef property storage -

  // - MicrobeamManipulationRef property storage -

  // - Microscope property storage -

  /* @see MetadataStore#setMicroscopeLotNumber(String, int) */
  public void setMicroscopeLotNumber(String lotNumber, int instrumentIndex) {
  }

  /* @see MetadataStore#setMicroscopeManufacturer(String, int) */
  public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex) {
  }

  /* @see MetadataStore#setMicroscopeModel(String, int) */
  public void setMicroscopeModel(String model, int instrumentIndex) {
  }

  /* @see MetadataStore#setMicroscopeSerialNumber(String, int) */
  public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex) {
  }

  /* @see MetadataStore#setMicroscopeType(MicroscopeType, int) */
  public void setMicroscopeType(MicroscopeType type, int instrumentIndex) {
  }

  // - OTF property storage -

  /* @see MetadataStore#setOTFBinaryFileFileName(String, int, int) */
  public void setOTFBinaryFileFileName(String binaryFileFileName, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFBinaryFileMIMEType(String, int, int) */
  public void setOTFBinaryFileMIMEType(String binaryFileMIMEType, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFBinaryFileSize(Integer, int, int) */
  public void setOTFBinaryFileSize(Integer binaryFileSize, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFFilterSetRef(String, int, int) */
  public void setOTFFilterSetRef(String filterSetRef, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFObjectiveSettingsCorrectionCollar(Double, int, int) */
  public void setOTFObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFObjectiveSettingsID(String, int, int) */
  public void setOTFObjectiveSettingsID(String objectiveSettingsID, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFObjectiveSettingsMedium(Medium, int, int) */
  public void setOTFObjectiveSettingsMedium(Medium objectiveSettingsMedium, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFObjectiveSettingsRefractiveIndex(Double, int, int) */
  public void setOTFObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFSizeX(PositiveInteger, int, int) */
  public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFSizeY(PositiveInteger, int, int) */
  public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFType(PixelType, int, int) */
  public void setOTFType(PixelType type, int instrumentIndex, int otfIndex) {
  }

  // - Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Double, int, int) */
  public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveCorrection(Correction, int, int) */
  public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveImmersion(Immersion, int, int) */
  public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveIris(Boolean, int, int) */
  public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveLensNA(Double, int, int) */
  public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveLotNumber(String, int, int) */
  public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Double, int, int) */
  public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex) {
  }

  // - Path property storage -

  /* @see MetadataStore#setPathDefinition(String, int, int) */
  public void setPathDefinition(String definition, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathDescription(String, int, int) */
  public void setPathDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathFill(Integer, int, int) */
  public void setPathFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathFontSize(Integer, int, int) */
  public void setPathFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathID(String, int, int) */
  public void setPathID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathLabel(String, int, int) */
  public void setPathLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathName(String, int, int) */
  public void setPathName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathStroke(Integer, int, int) */
  public void setPathStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathStrokeDashArray(String, int, int) */
  public void setPathStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathStrokeWidth(Double, int, int) */
  public void setPathStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathTheC(Integer, int, int) */
  public void setPathTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathTheT(Integer, int, int) */
  public void setPathTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathTheZ(Integer, int, int) */
  public void setPathTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPathTransform(String, int, int) */
  public void setPathTransform(String transform, int roiIndex, int shapeIndex) {
  }

  // - Pixels property storage -

  /* @see MetadataStore#setPixelsAnnotationRef(String, int, int) */
  public void setPixelsAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setPixelsDimensionOrder(DimensionOrder, int) */
  public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsID(String, int) */
  public void setPixelsID(String id, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsPhysicalSizeX(Double, int) */
  public void setPixelsPhysicalSizeX(Double physicalSizeX, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsPhysicalSizeY(Double, int) */
  public void setPixelsPhysicalSizeY(Double physicalSizeY, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsPhysicalSizeZ(Double, int) */
  public void setPixelsPhysicalSizeZ(Double physicalSizeZ, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsSizeC(PositiveInteger, int) */
  public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsSizeT(PositiveInteger, int) */
  public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsSizeX(PositiveInteger, int) */
  public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsSizeY(PositiveInteger, int) */
  public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsSizeZ(PositiveInteger, int) */
  public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsTimeIncrement(Double, int) */
  public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex) {
  }

  /* @see MetadataStore#setPixelsType(PixelType, int) */
  public void setPixelsType(PixelType type, int imageIndex) {
  }

  // - PixelsAnnotationRef property storage -

  // - PixelsBinData property storage -

  /* @see MetadataStore#setPixelsBinDataBigEndian(Boolean, int, int) */
  public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex) {
  }

  // - Plane property storage -

  /* @see MetadataStore#setPlaneAnnotationRef(String, int, int, int) */
  public void setPlaneAnnotationRef(String annotationRef, int imageIndex, int planeIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setPlaneDeltaT(Double, int, int) */
  public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneExposureTime(Double, int, int) */
  public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneHashSHA1(String, int, int) */
  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlanePositionX(Double, int, int) */
  public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlanePositionY(Double, int, int) */
  public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlanePositionZ(Double, int, int) */
  public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int planeIndex) {
  }

  // - PlaneAnnotationRef property storage -

  // - Plate property storage -

  /* @see MetadataStore#setPlateAnnotationRef(String, int, int) */
  public void setPlateAnnotationRef(String annotationRef, int plateIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setPlateColumnNamingConvention(NamingConvention, int) */
  public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex) {
  }

  /* @see MetadataStore#setPlateColumns(Integer, int) */
  public void setPlateColumns(Integer columns, int plateIndex) {
  }

  /* @see MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
  }

  /* @see MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
  }

  /* @see MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
  }

  /* @see MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
  }

  /* @see MetadataStore#setPlateRowNamingConvention(NamingConvention, int) */
  public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex) {
  }

  /* @see MetadataStore#setPlateRows(Integer, int) */
  public void setPlateRows(Integer rows, int plateIndex) {
  }

  /* @see MetadataStore#setPlateScreenRef(String, int, int) */
  public void setPlateScreenRef(String screenRef, int plateIndex, int screenRefIndex) {
  }

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
  }

  /* @see MetadataStore#setPlateWellOriginX(Double, int) */
  public void setPlateWellOriginX(Double wellOriginX, int plateIndex) {
  }

  /* @see MetadataStore#setPlateWellOriginY(Double, int) */
  public void setPlateWellOriginY(Double wellOriginY, int plateIndex) {
  }

  // - PlateAcquisition property storage -

  /* @see MetadataStore#setPlateAcquisitionAnnotationRef(String, int, int, int) */
  public void setPlateAcquisitionAnnotationRef(String annotationRef, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionDescription(String, int, int) */
  public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionEndTime(String, int, int) */
  public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionID(String, int, int) */
  public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionMaximumFieldCount(Integer, int, int) */
  public void setPlateAcquisitionMaximumFieldCount(Integer maximumFieldCount, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionName(String, int, int) */
  public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionStartTime(String, int, int) */
  public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex) {
  }

  /* @see MetadataStore#setPlateAcquisitionWellSampleRef(String, int, int, int) */
  public void setPlateAcquisitionWellSampleRef(String wellSampleRef, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex) {
  }

  // - PlateAcquisitionAnnotationRef property storage -

  // - PlateAnnotationRef property storage -

  // - PlateRef property storage -

  // - Point property storage -

  /* @see MetadataStore#setPointDescription(String, int, int) */
  public void setPointDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointFill(Integer, int, int) */
  public void setPointFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointFontSize(Integer, int, int) */
  public void setPointFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointID(String, int, int) */
  public void setPointID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointLabel(String, int, int) */
  public void setPointLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointName(String, int, int) */
  public void setPointName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointStroke(Integer, int, int) */
  public void setPointStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointStrokeDashArray(String, int, int) */
  public void setPointStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointStrokeWidth(Double, int, int) */
  public void setPointStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointTheC(Integer, int, int) */
  public void setPointTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointTheT(Integer, int, int) */
  public void setPointTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointTheZ(Integer, int, int) */
  public void setPointTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointTransform(String, int, int) */
  public void setPointTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointX(Double, int, int) */
  public void setPointX(Double x, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPointY(Double, int, int) */
  public void setPointY(Double y, int roiIndex, int shapeIndex) {
  }

  // - Polyline property storage -

  /* @see MetadataStore#setPolylineClosed(Boolean, int, int) */
  public void setPolylineClosed(Boolean closed, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineDescription(String, int, int) */
  public void setPolylineDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineFill(Integer, int, int) */
  public void setPolylineFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineFontSize(Integer, int, int) */
  public void setPolylineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineID(String, int, int) */
  public void setPolylineID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineLabel(String, int, int) */
  public void setPolylineLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineName(String, int, int) */
  public void setPolylineName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylinePoints(String, int, int) */
  public void setPolylinePoints(String points, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineStroke(Integer, int, int) */
  public void setPolylineStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineStrokeDashArray(String, int, int) */
  public void setPolylineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineStrokeWidth(Double, int, int) */
  public void setPolylineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineTheC(Integer, int, int) */
  public void setPolylineTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineTheT(Integer, int, int) */
  public void setPolylineTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineTheZ(Integer, int, int) */
  public void setPolylineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setPolylineTransform(String, int, int) */
  public void setPolylineTransform(String transform, int roiIndex, int shapeIndex) {
  }

  // - Project property storage -

  /* @see MetadataStore#setProjectAnnotationRef(String, int, int) */
  public void setProjectAnnotationRef(String annotationRef, int projectIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setProjectDescription(String, int) */
  public void setProjectDescription(String description, int projectIndex) {
  }

  /* @see MetadataStore#setProjectExperimenterRef(String, int) */
  public void setProjectExperimenterRef(String experimenterRef, int projectIndex) {
  }

  /* @see MetadataStore#setProjectGroupRef(String, int) */
  public void setProjectGroupRef(String groupRef, int projectIndex) {
  }

  /* @see MetadataStore#setProjectID(String, int) */
  public void setProjectID(String id, int projectIndex) {
  }

  /* @see MetadataStore#setProjectName(String, int) */
  public void setProjectName(String name, int projectIndex) {
  }

  // - ProjectAnnotationRef property storage -

  // - ProjectRef property storage -

  // - ROI property storage -

  /* @see MetadataStore#setROIAnnotationRef(String, int, int) */
  public void setROIAnnotationRef(String annotationRef, int roiIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setROIDescription(String, int) */
  public void setROIDescription(String description, int roiIndex) {
  }

  /* @see MetadataStore#setROIID(String, int) */
  public void setROIID(String id, int roiIndex) {
  }

  /* @see MetadataStore#setROIName(String, int) */
  public void setROIName(String name, int roiIndex) {
  }

  /* @see MetadataStore#setROINamespace(String, int) */
  public void setROINamespace(String namespace, int roiIndex) {
  }

  // - ROIAnnotationRef property storage -

  // - Reagent property storage -

  /* @see MetadataStore#setReagentAnnotationRef(String, int, int, int) */
  public void setReagentAnnotationRef(String annotationRef, int screenIndex, int reagentIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
  }

  /* @see MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
  }

  /* @see MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
  }

  /* @see MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
  }

  // - ReagentAnnotationRef property storage -

  // - Rectangle property storage -

  /* @see MetadataStore#setRectangleDescription(String, int, int) */
  public void setRectangleDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleFill(Integer, int, int) */
  public void setRectangleFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleFontSize(Integer, int, int) */
  public void setRectangleFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleHeight(Double, int, int) */
  public void setRectangleHeight(Double height, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleID(String, int, int) */
  public void setRectangleID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleLabel(String, int, int) */
  public void setRectangleLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleName(String, int, int) */
  public void setRectangleName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleStroke(Integer, int, int) */
  public void setRectangleStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleStrokeDashArray(String, int, int) */
  public void setRectangleStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleStrokeWidth(Double, int, int) */
  public void setRectangleStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleTheC(Integer, int, int) */
  public void setRectangleTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleTheT(Integer, int, int) */
  public void setRectangleTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleTheZ(Integer, int, int) */
  public void setRectangleTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleTransform(String, int, int) */
  public void setRectangleTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleWidth(Double, int, int) */
  public void setRectangleWidth(Double width, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleX(Double, int, int) */
  public void setRectangleX(Double x, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setRectangleY(Double, int, int) */
  public void setRectangleY(Double y, int roiIndex, int shapeIndex) {
  }

  // - Screen property storage -

  /* @see MetadataStore#setScreenAnnotationRef(String, int, int) */
  public void setScreenAnnotationRef(String annotationRef, int screenIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setScreenDescription(String, int) */
  public void setScreenDescription(String description, int screenIndex) {
  }

  /* @see MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
  }

  /* @see MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
  }

  /* @see MetadataStore#setScreenPlateRef(String, int, int) */
  public void setScreenPlateRef(String plateRef, int screenIndex, int plateRefIndex) {
  }

  /* @see MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
  }

  /* @see MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
  }

  /* @see MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
  }

  /* @see MetadataStore#setScreenReagentSetIdentifier(String, int) */
  public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex) {
  }

  /* @see MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
  }

  // - ScreenAnnotationRef property storage -

  // - ScreenRef property storage -

  // - ShapeAnnotationRef property storage -

  // - StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelX(Double, int) */
  public void setStageLabelX(Double x, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelY(Double, int) */
  public void setStageLabelY(Double y, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelZ(Double, int) */
  public void setStageLabelZ(Double z, int imageIndex) {
  }

  // - StringAnnotation property storage -

  /* @see MetadataStore#setStringAnnotationID(String, int) */
  public void setStringAnnotationID(String id, int stringAnnotationIndex) {
  }

  /* @see MetadataStore#setStringAnnotationNamespace(String, int) */
  public void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex) {
  }

  /* @see MetadataStore#setStringAnnotationValue(String, int) */
  public void setStringAnnotationValue(String value, int stringAnnotationIndex) {
  }

  // - Text property storage -

  /* @see MetadataStore#setTextDescription(String, int, int) */
  public void setTextDescription(String description, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextFill(Integer, int, int) */
  public void setTextFill(Integer fill, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextFontSize(Integer, int, int) */
  public void setTextFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextID(String, int, int) */
  public void setTextID(String id, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextLabel(String, int, int) */
  public void setTextLabel(String label, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextName(String, int, int) */
  public void setTextName(String name, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextStroke(Integer, int, int) */
  public void setTextStroke(Integer stroke, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextStrokeDashArray(String, int, int) */
  public void setTextStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextStrokeWidth(Double, int, int) */
  public void setTextStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextTheC(Integer, int, int) */
  public void setTextTheC(Integer theC, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextTheT(Integer, int, int) */
  public void setTextTheT(Integer theT, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextTheZ(Integer, int, int) */
  public void setTextTheZ(Integer theZ, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextTransform(String, int, int) */
  public void setTextTransform(String transform, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextValue(String, int, int) */
  public void setTextValue(String value, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextX(Double, int, int) */
  public void setTextX(Double x, int roiIndex, int shapeIndex) {
  }

  /* @see MetadataStore#setTextY(Double, int, int) */
  public void setTextY(Double y, int roiIndex, int shapeIndex) {
  }

  // - TiffData property storage -

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int tiffDataIndex) {
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int tiffDataIndex) {
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int tiffDataIndex) {
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int tiffDataIndex) {
  }

  /* @see MetadataStore#setTiffDataPlaneCount(Integer, int, int) */
  public void setTiffDataPlaneCount(Integer planeCount, int imageIndex, int tiffDataIndex) {
  }

  // - TimestampAnnotation property storage -

  /* @see MetadataStore#setTimestampAnnotationID(String, int) */
  public void setTimestampAnnotationID(String id, int timestampAnnotationIndex) {
  }

  /* @see MetadataStore#setTimestampAnnotationNamespace(String, int) */
  public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex) {
  }

  /* @see MetadataStore#setTimestampAnnotationValue(String, int) */
  public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex) {
  }

  // - TransmittanceRange property storage -

  /* @see MetadataStore#setTransmittanceRangeCutIn(Integer, int, int) */
  public void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setTransmittanceRangeCutInTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setTransmittanceRangeCutOut(Integer, int, int) */
  public void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setTransmittanceRangeCutOutTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex) {
  }

  /* @see MetadataStore#setTransmittanceRangeTransmittance(PercentFraction, int, int) */
  public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex) {
  }

  // - UUID property storage -

  public void setUUIDValue(String value, int imageIndex, int tiffDataIndex) {
  }

  /* @see MetadataStore#setUUIDFileName(String, int, int) */
  public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex) {
  }

  // - Well property storage -

  /* @see MetadataStore#setWellAnnotationRef(String, int, int, int) */
  public void setWellAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setWellColor(Integer, int, int) */
  public void setWellColor(Integer color, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellColumn(NonNegativeInteger, int, int) */
  public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellReagentRef(String, int, int) */
  public void setWellReagentRef(String reagentRef, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellRow(NonNegativeInteger, int, int) */
  public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex) {
  }

  /* @see MetadataStore#setWellStatus(String, int, int) */
  public void setWellStatus(String status, int plateIndex, int wellIndex) {
  }

  // - WellAnnotationRef property storage -

  // - WellSample property storage -

  /* @see MetadataStore#setWellSampleAnnotationRef(String, int, int, int, int) */
  public void setWellSampleAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex) {
  }

  /* @see MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  /* @see MetadataStore#setWellSampleImageRef(String, int, int, int) */
  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  /* @see MetadataStore#setWellSampleIndex(NonNegativeInteger, int, int, int) */
  public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  /* @see MetadataStore#setWellSamplePositionX(Double, int, int, int) */
  public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  /* @see MetadataStore#setWellSamplePositionY(Double, int, int, int) */
  public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  /* @see MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
  }

  // - WellSampleAnnotationRef property storage -

  // - WellSampleRef property storage -

  // - XMLAnnotation property storage -

  /* @see MetadataStore#setXMLAnnotationID(String, int) */
  public void setXMLAnnotationID(String id, int xmlAnnotationIndex) {
  }

  /* @see MetadataStore#setXMLAnnotationNamespace(String, int) */
  public void setXMLAnnotationNamespace(String namespace, int xmlAnnotationIndex) {
  }

  /* @see MetadataStore#setXMLAnnotationValue(String, int) */
  public void setXMLAnnotationValue(String value, int xmlAnnotationIndex) {
  }

}
