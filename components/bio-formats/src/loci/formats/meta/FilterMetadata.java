//
// FilterMetadata.vm
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

import loci.common.DataTools;

import ome.xml.model.Image;
import ome.xml.model.OME;
import ome.xml.model.Pixels;
import ome.xml.model.TiffData;
import ome.xml.model.UUID;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

/**
 * An implementation of {@link MetadataStore} that removes unprintable
 * characters from metadata values before storing them in a delegate
 * MetadataStore.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/FilterMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/FilterMetadata.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FilterMetadata implements MetadataStore {

  // -- Fields --

  private MetadataStore store;
  private boolean filter;

  // -- Constructor --

  public FilterMetadata(MetadataStore store, boolean filter) {
    this.store = store;
    this.filter = filter;
  }

  // -- MetadataStore API methods --

  /* @see MetadataStore#createRoot() */
  public void createRoot() {
    store.createRoot();
  }

  /* @see MetadataStore#getRoot() */
  public Object getRoot() {
    return store.getRoot();
  }

  /* @see MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    store.setRoot(root);
  }

  /* @see MetadataStore#setUUID(String) */
  public void setUUID(String uuid) {
    store.setUUID(uuid);
  }

  // -- Arc property storage -

  /* @see MetadataStore#setArcID(String, int, int) */
  public void setArcID(String id, int instrumentIndex, int arcIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setArcID(filteredValue, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcLotNumber(String, int, int) */
  public void setArcLotNumber(String lotNumber, int instrumentIndex, int arcIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setArcLotNumber(filteredValue, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcManufacturer(String, int, int) */
  public void setArcManufacturer(String manufacturer, int instrumentIndex, int arcIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setArcManufacturer(filteredValue, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcModel(String, int, int) */
  public void setArcModel(String model, int instrumentIndex, int arcIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setArcModel(filteredValue, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcPower(Double, int, int) */
  public void setArcPower(Double power, int instrumentIndex, int arcIndex) {
    store.setArcPower(power, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcSerialNumber(String, int, int) */
  public void setArcSerialNumber(String serialNumber, int instrumentIndex, int arcIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setArcSerialNumber(filteredValue, instrumentIndex, arcIndex);
  }

  /* @see MetadataStore#setArcType(ArcType, int, int) */
  public void setArcType(ArcType type, int instrumentIndex, int arcIndex) {
    store.setArcType(type, instrumentIndex, arcIndex);
  }

  // -- BooleanAnnotation property storage -

  /* @see MetadataStore#setBooleanAnnotationID(String, int) */
  public void setBooleanAnnotationID(String id, int booleanAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setBooleanAnnotationID(filteredValue, booleanAnnotationIndex);
  }

  /* @see MetadataStore#setBooleanAnnotationNamespace(String, int) */
  public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setBooleanAnnotationNamespace(filteredValue, booleanAnnotationIndex);
  }

  /* @see MetadataStore#setBooleanAnnotationValue(Boolean, int) */
  public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex) {
    store.setBooleanAnnotationValue(value, booleanAnnotationIndex);
  }

  // -- Channel property storage -

  /* @see MetadataStore#setChannelAcquisitionMode(AcquisitionMode, int, int) */
  public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex) {
    store.setChannelAcquisitionMode(acquisitionMode, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelAnnotationRef(String, int, int, int) */
  public void setChannelAnnotationRef(String annotationRef, int imageIndex, int channelIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setChannelAnnotationRef(filteredValue, imageIndex, channelIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setChannelColor(Integer, int, int) */
  public void setChannelColor(Integer color, int imageIndex, int channelIndex) {
    store.setChannelColor(color, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelContrastMethod(ContrastMethod, int, int) */
  public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex) {
    store.setChannelContrastMethod(contrastMethod, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelEmissionWavelength(PositiveInteger, int, int) */
  public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex) {
    store.setChannelEmissionWavelength(emissionWavelength, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelExcitationWavelength(PositiveInteger, int, int) */
  public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex) {
    store.setChannelExcitationWavelength(excitationWavelength, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelFilterSetRef(String, int, int) */
  public void setChannelFilterSetRef(String filterSetRef, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(filterSetRef) : filterSetRef;
    store.setChannelFilterSetRef(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelFluor(String, int, int) */
  public void setChannelFluor(String fluor, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(fluor) : fluor;
    store.setChannelFluor(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelID(String, int, int) */
  public void setChannelID(String id, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setChannelID(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelIlluminationType(IlluminationType, int, int) */
  public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex) {
    store.setChannelIlluminationType(illuminationType, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelLightSourceSettingsAttenuation(PercentFraction, int, int) */
  public void setChannelLightSourceSettingsAttenuation(PercentFraction lightSourceSettingsAttenuation, int imageIndex, int channelIndex) {
    store.setChannelLightSourceSettingsAttenuation(lightSourceSettingsAttenuation, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelLightSourceSettingsID(String, int, int) */
  public void setChannelLightSourceSettingsID(String lightSourceSettingsID, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(lightSourceSettingsID) : lightSourceSettingsID;
    store.setChannelLightSourceSettingsID(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelLightSourceSettingsWavelength(PositiveInteger, int, int) */
  public void setChannelLightSourceSettingsWavelength(PositiveInteger lightSourceSettingsWavelength, int imageIndex, int channelIndex) {
    store.setChannelLightSourceSettingsWavelength(lightSourceSettingsWavelength, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelNDFilter(Double, int, int) */
  public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex) {
    store.setChannelNDFilter(ndFilter, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelName(String, int, int) */
  public void setChannelName(String name, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setChannelName(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelOTFRef(String, int, int) */
  public void setChannelOTFRef(String otfRef, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(otfRef) : otfRef;
    store.setChannelOTFRef(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelPinholeSize(Double, int, int) */
  public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex) {
    store.setChannelPinholeSize(pinholeSize, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelPockelCellSetting(Integer, int, int) */
  public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex) {
    store.setChannelPockelCellSetting(pockelCellSetting, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setChannelSamplesPerPixel(Integer, int, int) */
  public void setChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int channelIndex) {
    store.setChannelSamplesPerPixel(samplesPerPixel, imageIndex, channelIndex);
  }

  // -- ChannelAnnotationRef property storage -

  // -- Dataset property storage -

  /* @see MetadataStore#setDatasetAnnotationRef(String, int, int) */
  public void setDatasetAnnotationRef(String annotationRef, int datasetIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setDatasetAnnotationRef(filteredValue, datasetIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setDatasetDescription(String, int) */
  public void setDatasetDescription(String description, int datasetIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setDatasetDescription(filteredValue, datasetIndex);
  }

  /* @see MetadataStore#setDatasetExperimenterRef(String, int) */
  public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setDatasetExperimenterRef(filteredValue, datasetIndex);
  }

  /* @see MetadataStore#setDatasetGroupRef(String, int) */
  public void setDatasetGroupRef(String groupRef, int datasetIndex) {
    String filteredValue = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setDatasetGroupRef(filteredValue, datasetIndex);
  }

  /* @see MetadataStore#setDatasetID(String, int) */
  public void setDatasetID(String id, int datasetIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setDatasetID(filteredValue, datasetIndex);
  }

  /* @see MetadataStore#setDatasetName(String, int) */
  public void setDatasetName(String name, int datasetIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setDatasetName(filteredValue, datasetIndex);
  }

  /* @see MetadataStore#setDatasetProjectRef(String, int, int) */
  public void setDatasetProjectRef(String projectRef, int datasetIndex, int projectRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(projectRef) : projectRef;
    store.setDatasetProjectRef(filteredValue, datasetIndex, projectRefIndex);
  }

  // -- DatasetAnnotationRef property storage -

  // -- DatasetRef property storage -

  // -- Detector property storage -

  /* @see MetadataStore#setDetectorAmplificationGain(Double, int, int) */
  public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex) {
    store.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorGain(Double, int, int) */
  public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex) {
    store.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorID(String, int, int) */
  public void setDetectorID(String id, int instrumentIndex, int detectorIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setDetectorID(filteredValue, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorLotNumber(String, int, int) */
  public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setDetectorLotNumber(filteredValue, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setDetectorManufacturer(filteredValue, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setDetectorModel(filteredValue, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorOffset(Double, int, int) */
  public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex) {
    store.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setDetectorSerialNumber(filteredValue, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorType(DetectorType, int, int) */
  public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex) {
    store.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorVoltage(Double, int, int) */
  public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex) {
    store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorZoom(Double, int, int) */
  public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex) {
    store.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
  }

  // -- DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsBinning(Binning, int, int) */
  public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex) {
    store.setDetectorSettingsBinning(binning, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsGain(Double, int, int) */
  public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex) {
    store.setDetectorSettingsGain(gain, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsID(String, int, int) */
  public void setDetectorSettingsID(String id, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setDetectorSettingsID(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Double, int, int) */
  public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex) {
    store.setDetectorSettingsOffset(offset, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Double, int, int) */
  public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex) {
    store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Double, int, int) */
  public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex) {
    store.setDetectorSettingsVoltage(voltage, imageIndex, channelIndex);
  }

  // -- Dichroic property storage -

  /* @see MetadataStore#setDichroicID(String, int, int) */
  public void setDichroicID(String id, int instrumentIndex, int dichroicIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setDichroicID(filteredValue, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicLotNumber(String, int, int) */
  public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setDichroicLotNumber(filteredValue, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicManufacturer(String, int, int) */
  public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setDichroicManufacturer(filteredValue, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicModel(String, int, int) */
  public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setDichroicModel(filteredValue, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicSerialNumber(String, int, int) */
  public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setDichroicSerialNumber(filteredValue, instrumentIndex, dichroicIndex);
  }

  // -- DoubleAnnotation property storage -

  /* @see MetadataStore#setDoubleAnnotationID(String, int) */
  public void setDoubleAnnotationID(String id, int doubleAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setDoubleAnnotationID(filteredValue, doubleAnnotationIndex);
  }

  /* @see MetadataStore#setDoubleAnnotationNamespace(String, int) */
  public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setDoubleAnnotationNamespace(filteredValue, doubleAnnotationIndex);
  }

  /* @see MetadataStore#setDoubleAnnotationValue(Double, int) */
  public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex) {
    store.setDoubleAnnotationValue(value, doubleAnnotationIndex);
  }

  // -- Ellipse property storage -

  /* @see MetadataStore#setEllipseDescription(String, int, int) */
  public void setEllipseDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setEllipseDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseFill(Integer, int, int) */
  public void setEllipseFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setEllipseFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseFontSize(Integer, int, int) */
  public void setEllipseFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setEllipseFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseID(String, int, int) */
  public void setEllipseID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setEllipseID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseLabel(String, int, int) */
  public void setEllipseLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setEllipseLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseName(String, int, int) */
  public void setEllipseName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setEllipseName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseRadiusX(Double, int, int) */
  public void setEllipseRadiusX(Double radiusX, int roiIndex, int shapeIndex) {
    store.setEllipseRadiusX(radiusX, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseRadiusY(Double, int, int) */
  public void setEllipseRadiusY(Double radiusY, int roiIndex, int shapeIndex) {
    store.setEllipseRadiusY(radiusY, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseStroke(Integer, int, int) */
  public void setEllipseStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setEllipseStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseStrokeDashArray(String, int, int) */
  public void setEllipseStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setEllipseStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseStrokeWidth(Double, int, int) */
  public void setEllipseStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setEllipseStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseTheC(Integer, int, int) */
  public void setEllipseTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setEllipseTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseTheT(Integer, int, int) */
  public void setEllipseTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setEllipseTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseTheZ(Integer, int, int) */
  public void setEllipseTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setEllipseTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseTransform(String, int, int) */
  public void setEllipseTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setEllipseTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseX(Double, int, int) */
  public void setEllipseX(Double x, int roiIndex, int shapeIndex) {
    store.setEllipseX(x, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseY(Double, int, int) */
  public void setEllipseY(Double y, int roiIndex, int shapeIndex) {
    store.setEllipseY(y, roiIndex, shapeIndex);
  }

  // -- Experiment property storage -

  /* @see MetadataStore#setExperimentDescription(String, int) */
  public void setExperimentDescription(String description, int experimentIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setExperimentDescription(filteredValue, experimentIndex);
  }

  /* @see MetadataStore#setExperimentExperimenterRef(String, int) */
  public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setExperimentExperimenterRef(filteredValue, experimentIndex);
  }

  /* @see MetadataStore#setExperimentID(String, int) */
  public void setExperimentID(String id, int experimentIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setExperimentID(filteredValue, experimentIndex);
  }

  /* @see MetadataStore#setExperimentType(ExperimentType, int) */
  public void setExperimentType(ExperimentType type, int experimentIndex) {
    store.setExperimentType(type, experimentIndex);
  }

  // -- Experimenter property storage -

  /* @see MetadataStore#setExperimenterAnnotationRef(String, int, int) */
  public void setExperimenterAnnotationRef(String annotationRef, int experimenterIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setExperimenterAnnotationRef(filteredValue, experimenterIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setExperimenterDisplayName(String, int) */
  public void setExperimenterDisplayName(String displayName, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(displayName) : displayName;
    store.setExperimenterDisplayName(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(email) : email;
    store.setExperimenterEmail(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(firstName) : firstName;
    store.setExperimenterFirstName(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterGroupRef(String, int, int) */
  public void setExperimenterGroupRef(String groupRef, int experimenterIndex, int groupRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setExperimenterGroupRef(filteredValue, experimenterIndex, groupRefIndex);
  }

  /* @see MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setExperimenterID(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(institution) : institution;
    store.setExperimenterInstitution(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(lastName) : lastName;
    store.setExperimenterLastName(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterMiddleName(String, int) */
  public void setExperimenterMiddleName(String middleName, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(middleName) : middleName;
    store.setExperimenterMiddleName(filteredValue, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterUserName(String, int) */
  public void setExperimenterUserName(String userName, int experimenterIndex) {
    String filteredValue = filter ? DataTools.sanitize(userName) : userName;
    store.setExperimenterUserName(filteredValue, experimenterIndex);
  }

  // -- ExperimenterAnnotationRef property storage -

  // -- ExperimenterGroupRef property storage -

  // -- Filament property storage -

  /* @see MetadataStore#setFilamentID(String, int, int) */
  public void setFilamentID(String id, int instrumentIndex, int filamentIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setFilamentID(filteredValue, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentLotNumber(String, int, int) */
  public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int filamentIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setFilamentLotNumber(filteredValue, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentManufacturer(String, int, int) */
  public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int filamentIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setFilamentManufacturer(filteredValue, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentModel(String, int, int) */
  public void setFilamentModel(String model, int instrumentIndex, int filamentIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setFilamentModel(filteredValue, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentPower(Double, int, int) */
  public void setFilamentPower(Double power, int instrumentIndex, int filamentIndex) {
    store.setFilamentPower(power, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentSerialNumber(String, int, int) */
  public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int filamentIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setFilamentSerialNumber(filteredValue, instrumentIndex, filamentIndex);
  }

  /* @see MetadataStore#setFilamentType(FilamentType, int, int) */
  public void setFilamentType(FilamentType type, int instrumentIndex, int filamentIndex) {
    store.setFilamentType(type, instrumentIndex, filamentIndex);
  }

  // -- FileAnnotation property storage -

  /* @see MetadataStore#setFileAnnotationBinaryFileFileName(String, int) */
  public void setFileAnnotationBinaryFileFileName(String binaryFileFileName, int fileAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(binaryFileFileName) : binaryFileFileName;
    store.setFileAnnotationBinaryFileFileName(filteredValue, fileAnnotationIndex);
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileMIMEType(String, int) */
  public void setFileAnnotationBinaryFileMIMEType(String binaryFileMIMEType, int fileAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(binaryFileMIMEType) : binaryFileMIMEType;
    store.setFileAnnotationBinaryFileMIMEType(filteredValue, fileAnnotationIndex);
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileSize(Integer, int) */
  public void setFileAnnotationBinaryFileSize(Integer binaryFileSize, int fileAnnotationIndex) {
    store.setFileAnnotationBinaryFileSize(binaryFileSize, fileAnnotationIndex);
  }

  /* @see MetadataStore#setFileAnnotationID(String, int) */
  public void setFileAnnotationID(String id, int fileAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setFileAnnotationID(filteredValue, fileAnnotationIndex);
  }

  /* @see MetadataStore#setFileAnnotationNamespace(String, int) */
  public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setFileAnnotationNamespace(filteredValue, fileAnnotationIndex);
  }

  // -- Filter property storage -

  /* @see MetadataStore#setFilterFilterWheel(String, int, int) */
  public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(filterWheel) : filterWheel;
    store.setFilterFilterWheel(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterID(String, int, int) */
  public void setFilterID(String id, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setFilterID(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterLotNumber(String, int, int) */
  public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setFilterLotNumber(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterManufacturer(String, int, int) */
  public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setFilterManufacturer(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterModel(String, int, int) */
  public void setFilterModel(String model, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setFilterModel(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterSerialNumber(String, int, int) */
  public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setFilterSerialNumber(filteredValue, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterType(FilterType, int, int) */
  public void setFilterType(FilterType type, int instrumentIndex, int filterIndex) {
    store.setFilterType(type, instrumentIndex, filterIndex);
  }

  // -- FilterSet property storage -

  /* @see MetadataStore#setFilterSetDichroicRef(String, int, int) */
  public void setFilterSetDichroicRef(String dichroicRef, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(dichroicRef) : dichroicRef;
    store.setFilterSetDichroicRef(filteredValue, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetEmissionFilterRef(String, int, int, int) */
  public void setFilterSetEmissionFilterRef(String emissionFilterRef, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(emissionFilterRef) : emissionFilterRef;
    store.setFilterSetEmissionFilterRef(filteredValue, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
  }

  /* @see MetadataStore#setFilterSetExcitationFilterRef(String, int, int, int) */
  public void setFilterSetExcitationFilterRef(String excitationFilterRef, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(excitationFilterRef) : excitationFilterRef;
    store.setFilterSetExcitationFilterRef(filteredValue, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
  }

  /* @see MetadataStore#setFilterSetID(String, int, int) */
  public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setFilterSetID(filteredValue, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetLotNumber(String, int, int) */
  public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setFilterSetLotNumber(filteredValue, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetManufacturer(String, int, int) */
  public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setFilterSetManufacturer(filteredValue, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetModel(String, int, int) */
  public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setFilterSetModel(filteredValue, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetSerialNumber(String, int, int) */
  public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setFilterSetSerialNumber(filteredValue, instrumentIndex, filterSetIndex);
  }

  // -- FilterSetEmissionFilterRef property storage -

  // -- FilterSetExcitationFilterRef property storage -

  // -- Group property storage -

  /* @see MetadataStore#setGroupContact(String, int) */
  public void setGroupContact(String contact, int groupIndex) {
    String filteredValue = filter ? DataTools.sanitize(contact) : contact;
    store.setGroupContact(filteredValue, groupIndex);
  }

  /* @see MetadataStore#setGroupDescription(String, int) */
  public void setGroupDescription(String description, int groupIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setGroupDescription(filteredValue, groupIndex);
  }

  /* @see MetadataStore#setGroupID(String, int) */
  public void setGroupID(String id, int groupIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setGroupID(filteredValue, groupIndex);
  }

  /* @see MetadataStore#setGroupLeader(String, int) */
  public void setGroupLeader(String leader, int groupIndex) {
    String filteredValue = filter ? DataTools.sanitize(leader) : leader;
    store.setGroupLeader(filteredValue, groupIndex);
  }

  /* @see MetadataStore#setGroupName(String, int) */
  public void setGroupName(String name, int groupIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setGroupName(filteredValue, groupIndex);
  }

  // -- Image property storage -

  /* @see MetadataStore#setImageAcquiredDate(String, int) */
  public void setImageAcquiredDate(String acquiredDate, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(acquiredDate) : acquiredDate;
    store.setImageAcquiredDate(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageAnnotationRef(String, int, int) */
  public void setImageAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setImageAnnotationRef(filteredValue, imageIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setImageDatasetRef(String, int, int) */
  public void setImageDatasetRef(String datasetRef, int imageIndex, int datasetRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(datasetRef) : datasetRef;
    store.setImageDatasetRef(filteredValue, imageIndex, datasetRefIndex);
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setImageDescription(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageExperimentRef(String, int) */
  public void setImageExperimentRef(String experimentRef, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimentRef) : experimentRef;
    store.setImageExperimentRef(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageExperimenterRef(String, int) */
  public void setImageExperimenterRef(String experimenterRef, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setImageExperimenterRef(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageGroupRef(String, int) */
  public void setImageGroupRef(String groupRef, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setImageGroupRef(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setImageID(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(instrumentRef) : instrumentRef;
    store.setImageInstrumentRef(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageMicrobeamManipulationRef(String, int, int) */
  public void setImageMicrobeamManipulationRef(String microbeamManipulationRef, int imageIndex, int microbeamManipulationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(microbeamManipulationRef) : microbeamManipulationRef;
    store.setImageMicrobeamManipulationRef(filteredValue, imageIndex, microbeamManipulationRefIndex);
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setImageName(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageObjectiveSettingsCorrectionCollar(Double, int) */
  public void setImageObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int imageIndex) {
    store.setImageObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, imageIndex);
  }

  /* @see MetadataStore#setImageObjectiveSettingsID(String, int) */
  public void setImageObjectiveSettingsID(String objectiveSettingsID, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(objectiveSettingsID) : objectiveSettingsID;
    store.setImageObjectiveSettingsID(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setImageObjectiveSettingsMedium(Medium, int) */
  public void setImageObjectiveSettingsMedium(Medium objectiveSettingsMedium, int imageIndex) {
    store.setImageObjectiveSettingsMedium(objectiveSettingsMedium, imageIndex);
  }

  /* @see MetadataStore#setImageObjectiveSettingsRefractiveIndex(Double, int) */
  public void setImageObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int imageIndex) {
    store.setImageObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, imageIndex);
  }

  /* @see MetadataStore#setImageROIRef(String, int, int) */
  public void setImageROIRef(String roiRef, int imageIndex, int roiRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(roiRef) : roiRef;
    store.setImageROIRef(filteredValue, imageIndex, roiRefIndex);
  }

  // -- ImageAnnotationRef property storage -

  // -- ImageROIRef property storage -

  // -- ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Double, int) */
  public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex) {
    store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(PercentFraction, int) */
  public void setImagingEnvironmentCO2Percent(PercentFraction cO2Percent, int imageIndex) {
    store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(PercentFraction, int) */
  public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex) {
    store.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Double, int) */
  public void setImagingEnvironmentTemperature(Double temperature, int imageIndex) {
    store.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // -- Instrument property storage -

  /* @see MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setInstrumentID(filteredValue, instrumentIndex);
  }

  // -- Laser property storage -

  /* @see MetadataStore#setLaserFrequencyMultiplication(PositiveInteger, int, int) */
  public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int laserIndex) {
    store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserID(String, int, int) */
  public void setLaserID(String id, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setLaserID(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserLaserMedium(LaserMedium, int, int) */
  public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int laserIndex) {
    store.setLaserLaserMedium(laserMedium, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserLotNumber(String, int, int) */
  public void setLaserLotNumber(String lotNumber, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setLaserLotNumber(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserManufacturer(String, int, int) */
  public void setLaserManufacturer(String manufacturer, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setLaserManufacturer(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserModel(String, int, int) */
  public void setLaserModel(String model, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setLaserModel(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserPockelCell(Boolean, int, int) */
  public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int laserIndex) {
    store.setLaserPockelCell(pockelCell, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserPower(Double, int, int) */
  public void setLaserPower(Double power, int instrumentIndex, int laserIndex) {
    store.setLaserPower(power, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserPulse(Pulse, int, int) */
  public void setLaserPulse(Pulse pulse, int instrumentIndex, int laserIndex) {
    store.setLaserPulse(pulse, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserPump(String, int, int) */
  public void setLaserPump(String pump, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(pump) : pump;
    store.setLaserPump(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserRepetitionRate(Double, int, int) */
  public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int laserIndex) {
    store.setLaserRepetitionRate(repetitionRate, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserSerialNumber(String, int, int) */
  public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int laserIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setLaserSerialNumber(filteredValue, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int laserIndex) {
    store.setLaserTuneable(tuneable, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserType(LaserType, int, int) */
  public void setLaserType(LaserType type, int instrumentIndex, int laserIndex) {
    store.setLaserType(type, instrumentIndex, laserIndex);
  }

  /* @see MetadataStore#setLaserWavelength(PositiveInteger, int, int) */
  public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int laserIndex) {
    store.setLaserWavelength(wavelength, instrumentIndex, laserIndex);
  }

  // -- LightEmittingDiode property storage -

  /* @see MetadataStore#setLightEmittingDiodeID(String, int, int) */
  public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightEmittingDiodeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setLightEmittingDiodeID(filteredValue, instrumentIndex, lightEmittingDiodeIndex);
  }

  /* @see MetadataStore#setLightEmittingDiodeLotNumber(String, int, int) */
  public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setLightEmittingDiodeLotNumber(filteredValue, instrumentIndex, lightEmittingDiodeIndex);
  }

  /* @see MetadataStore#setLightEmittingDiodeManufacturer(String, int, int) */
  public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightEmittingDiodeIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setLightEmittingDiodeManufacturer(filteredValue, instrumentIndex, lightEmittingDiodeIndex);
  }

  /* @see MetadataStore#setLightEmittingDiodeModel(String, int, int) */
  public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightEmittingDiodeIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setLightEmittingDiodeModel(filteredValue, instrumentIndex, lightEmittingDiodeIndex);
  }

  /* @see MetadataStore#setLightEmittingDiodePower(Double, int, int) */
  public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightEmittingDiodeIndex) {
    store.setLightEmittingDiodePower(power, instrumentIndex, lightEmittingDiodeIndex);
  }

  /* @see MetadataStore#setLightEmittingDiodeSerialNumber(String, int, int) */
  public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setLightEmittingDiodeSerialNumber(filteredValue, instrumentIndex, lightEmittingDiodeIndex);
  }

  // -- LightPath property storage -

  /* @see MetadataStore#setLightPathDichroicRef(String, int, int) */
  public void setLightPathDichroicRef(String dichroicRef, int imageIndex, int channelIndex) {
    String filteredValue = filter ? DataTools.sanitize(dichroicRef) : dichroicRef;
    store.setLightPathDichroicRef(filteredValue, imageIndex, channelIndex);
  }

  /* @see MetadataStore#setLightPathEmissionFilterRef(String, int, int, int) */
  public void setLightPathEmissionFilterRef(String emissionFilterRef, int imageIndex, int channelIndex, int emissionFilterRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(emissionFilterRef) : emissionFilterRef;
    store.setLightPathEmissionFilterRef(filteredValue, imageIndex, channelIndex, emissionFilterRefIndex);
  }

  /* @see MetadataStore#setLightPathExcitationFilterRef(String, int, int, int) */
  public void setLightPathExcitationFilterRef(String excitationFilterRef, int imageIndex, int channelIndex, int excitationFilterRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(excitationFilterRef) : excitationFilterRef;
    store.setLightPathExcitationFilterRef(filteredValue, imageIndex, channelIndex, excitationFilterRefIndex);
  }

  // -- LightPathEmissionFilterRef property storage -

  // -- LightPathExcitationFilterRef property storage -

  // -- Line property storage -

  /* @see MetadataStore#setLineDescription(String, int, int) */
  public void setLineDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setLineDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineFill(Integer, int, int) */
  public void setLineFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setLineFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineFontSize(Integer, int, int) */
  public void setLineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setLineFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineID(String, int, int) */
  public void setLineID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setLineID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineLabel(String, int, int) */
  public void setLineLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setLineLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineName(String, int, int) */
  public void setLineName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setLineName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineStroke(Integer, int, int) */
  public void setLineStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setLineStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineStrokeDashArray(String, int, int) */
  public void setLineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setLineStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineStrokeWidth(Double, int, int) */
  public void setLineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setLineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineTheC(Integer, int, int) */
  public void setLineTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setLineTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineTheT(Integer, int, int) */
  public void setLineTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setLineTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineTheZ(Integer, int, int) */
  public void setLineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setLineTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineTransform(String, int, int) */
  public void setLineTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setLineTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineX1(Double, int, int) */
  public void setLineX1(Double x1, int roiIndex, int shapeIndex) {
    store.setLineX1(x1, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineX2(Double, int, int) */
  public void setLineX2(Double x2, int roiIndex, int shapeIndex) {
    store.setLineX2(x2, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineY1(Double, int, int) */
  public void setLineY1(Double y1, int roiIndex, int shapeIndex) {
    store.setLineY1(y1, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineY2(Double, int, int) */
  public void setLineY2(Double y2, int roiIndex, int shapeIndex) {
    store.setLineY2(y2, roiIndex, shapeIndex);
  }

  // -- ListAnnotation property storage -

  /* @see MetadataStore#setListAnnotationAnnotationRef(String, int, int) */
  public void setListAnnotationAnnotationRef(String annotationRef, int listAnnotationIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setListAnnotationAnnotationRef(filteredValue, listAnnotationIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setListAnnotationID(String, int) */
  public void setListAnnotationID(String id, int listAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setListAnnotationID(filteredValue, listAnnotationIndex);
  }

  /* @see MetadataStore#setListAnnotationNamespace(String, int) */
  public void setListAnnotationNamespace(String namespace, int listAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setListAnnotationNamespace(filteredValue, listAnnotationIndex);
  }

  // -- ListAnnotationAnnotationRef property storage -

  // -- LongAnnotation property storage -

  /* @see MetadataStore#setLongAnnotationID(String, int) */
  public void setLongAnnotationID(String id, int longAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setLongAnnotationID(filteredValue, longAnnotationIndex);
  }

  /* @see MetadataStore#setLongAnnotationNamespace(String, int) */
  public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setLongAnnotationNamespace(filteredValue, longAnnotationIndex);
  }

  /* @see MetadataStore#setLongAnnotationValue(Long, int) */
  public void setLongAnnotationValue(Long value, int longAnnotationIndex) {
    store.setLongAnnotationValue(value, longAnnotationIndex);
  }

  // -- Mask property storage -

  /* @see MetadataStore#setMaskDescription(String, int, int) */
  public void setMaskDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setMaskDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskFill(Integer, int, int) */
  public void setMaskFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setMaskFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskFontSize(Integer, int, int) */
  public void setMaskFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setMaskFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskID(String, int, int) */
  public void setMaskID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setMaskID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskLabel(String, int, int) */
  public void setMaskLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setMaskLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskName(String, int, int) */
  public void setMaskName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setMaskName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskStroke(Integer, int, int) */
  public void setMaskStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setMaskStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskStrokeDashArray(String, int, int) */
  public void setMaskStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setMaskStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskStrokeWidth(Double, int, int) */
  public void setMaskStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setMaskStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskTheC(Integer, int, int) */
  public void setMaskTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setMaskTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskTheT(Integer, int, int) */
  public void setMaskTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setMaskTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskTheZ(Integer, int, int) */
  public void setMaskTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setMaskTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskTransform(String, int, int) */
  public void setMaskTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setMaskTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskX(Double, int, int) */
  public void setMaskX(Double x, int roiIndex, int shapeIndex) {
    store.setMaskX(x, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskY(Double, int, int) */
  public void setMaskY(Double y, int roiIndex, int shapeIndex) {
    store.setMaskY(y, roiIndex, shapeIndex);
  }

  // -- MicrobeamManipulation property storage -

  /* @see MetadataStore#setMicrobeamManipulationExperimenterRef(String, int, int) */
  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int experimentIndex, int microbeamManipulationIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setMicrobeamManipulationExperimenterRef(filteredValue, experimentIndex, microbeamManipulationIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationID(String, int, int) */
  public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setMicrobeamManipulationID(filteredValue, experimentIndex, microbeamManipulationIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationROIRef(String, int, int, int) */
  public void setMicrobeamManipulationROIRef(String roiRef, int experimentIndex, int microbeamManipulationIndex, int roiRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(roiRef) : roiRef;
    store.setMicrobeamManipulationROIRef(filteredValue, experimentIndex, microbeamManipulationIndex, roiRefIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationType(MicrobeamManipulationType, int, int) */
  public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex) {
    store.setMicrobeamManipulationType(type, experimentIndex, microbeamManipulationIndex);
  }

  // -- MicrobeamManipulationLightSourceSettings property storage -

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    store.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsID(String, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setMicrobeamManipulationLightSourceSettingsID(filteredValue, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    store.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
  }

  // -- MicrobeamManipulationROIRef property storage -

  // -- MicrobeamManipulationRef property storage -

  // -- Microscope property storage -

  /* @see MetadataStore#setMicroscopeLotNumber(String, int) */
  public void setMicroscopeLotNumber(String lotNumber, int instrumentIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setMicroscopeLotNumber(filteredValue, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeManufacturer(String, int) */
  public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setMicroscopeManufacturer(filteredValue, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeModel(String, int) */
  public void setMicroscopeModel(String model, int instrumentIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setMicroscopeModel(filteredValue, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeSerialNumber(String, int) */
  public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setMicroscopeSerialNumber(filteredValue, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeType(MicroscopeType, int) */
  public void setMicroscopeType(MicroscopeType type, int instrumentIndex) {
    store.setMicroscopeType(type, instrumentIndex);
  }

  // -- OTF property storage -

  /* @see MetadataStore#setOTFBinaryFileFileName(String, int, int) */
  public void setOTFBinaryFileFileName(String binaryFileFileName, int instrumentIndex, int otfIndex) {
    String filteredValue = filter ? DataTools.sanitize(binaryFileFileName) : binaryFileFileName;
    store.setOTFBinaryFileFileName(filteredValue, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFBinaryFileMIMEType(String, int, int) */
  public void setOTFBinaryFileMIMEType(String binaryFileMIMEType, int instrumentIndex, int otfIndex) {
    String filteredValue = filter ? DataTools.sanitize(binaryFileMIMEType) : binaryFileMIMEType;
    store.setOTFBinaryFileMIMEType(filteredValue, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFBinaryFileSize(Integer, int, int) */
  public void setOTFBinaryFileSize(Integer binaryFileSize, int instrumentIndex, int otfIndex) {
    store.setOTFBinaryFileSize(binaryFileSize, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFFilterSetRef(String, int, int) */
  public void setOTFFilterSetRef(String filterSetRef, int instrumentIndex, int otfIndex) {
    String filteredValue = filter ? DataTools.sanitize(filterSetRef) : filterSetRef;
    store.setOTFFilterSetRef(filteredValue, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setOTFID(filteredValue, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFObjectiveSettingsCorrectionCollar(Double, int, int) */
  public void setOTFObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int instrumentIndex, int otfIndex) {
    store.setOTFObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFObjectiveSettingsID(String, int, int) */
  public void setOTFObjectiveSettingsID(String objectiveSettingsID, int instrumentIndex, int otfIndex) {
    String filteredValue = filter ? DataTools.sanitize(objectiveSettingsID) : objectiveSettingsID;
    store.setOTFObjectiveSettingsID(filteredValue, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFObjectiveSettingsMedium(Medium, int, int) */
  public void setOTFObjectiveSettingsMedium(Medium objectiveSettingsMedium, int instrumentIndex, int otfIndex) {
    store.setOTFObjectiveSettingsMedium(objectiveSettingsMedium, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFObjectiveSettingsRefractiveIndex(Double, int, int) */
  public void setOTFObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int instrumentIndex, int otfIndex) {
    store.setOTFObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    store.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeX(PositiveInteger, int, int) */
  public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int otfIndex) {
    store.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeY(PositiveInteger, int, int) */
  public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int otfIndex) {
    store.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFType(PixelType, int, int) */
  public void setOTFType(PixelType type, int instrumentIndex, int otfIndex) {
    store.setOTFType(type, instrumentIndex, otfIndex);
  }

  // -- Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Double, int, int) */
  public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveCorrection(Correction, int, int) */
  public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setObjectiveID(filteredValue, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveImmersion(Immersion, int, int) */
  public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveIris(Boolean, int, int) */
  public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveLensNA(Double, int, int) */
  public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveLotNumber(String, int, int) */
  public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex) {
    String filteredValue = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setObjectiveLotNumber(filteredValue, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    String filteredValue = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setObjectiveManufacturer(filteredValue, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    String filteredValue = filter ? DataTools.sanitize(model) : model;
    store.setObjectiveModel(filteredValue, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    String filteredValue = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setObjectiveSerialNumber(filteredValue, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Double, int, int) */
  public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // -- Path property storage -

  /* @see MetadataStore#setPathDefinition(String, int, int) */
  public void setPathDefinition(String definition, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(definition) : definition;
    store.setPathDefinition(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathDescription(String, int, int) */
  public void setPathDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setPathDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathFill(Integer, int, int) */
  public void setPathFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setPathFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathFontSize(Integer, int, int) */
  public void setPathFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setPathFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathID(String, int, int) */
  public void setPathID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPathID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathLabel(String, int, int) */
  public void setPathLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setPathLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathName(String, int, int) */
  public void setPathName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setPathName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathStroke(Integer, int, int) */
  public void setPathStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setPathStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathStrokeDashArray(String, int, int) */
  public void setPathStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setPathStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathStrokeWidth(Double, int, int) */
  public void setPathStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setPathStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathTheC(Integer, int, int) */
  public void setPathTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setPathTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathTheT(Integer, int, int) */
  public void setPathTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setPathTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathTheZ(Integer, int, int) */
  public void setPathTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setPathTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathTransform(String, int, int) */
  public void setPathTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setPathTransform(filteredValue, roiIndex, shapeIndex);
  }

  // -- Pixels property storage -

  /* @see MetadataStore#setPixelsAnnotationRef(String, int, int) */
  public void setPixelsAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setPixelsAnnotationRef(filteredValue, imageIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setPixelsDimensionOrder(DimensionOrder, int) */
  public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex) {
    store.setPixelsDimensionOrder(dimensionOrder, imageIndex);
  }

  /* @see MetadataStore#setPixelsID(String, int) */
  public void setPixelsID(String id, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPixelsID(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setPixelsPhysicalSizeX(Double, int) */
  public void setPixelsPhysicalSizeX(Double physicalSizeX, int imageIndex) {
    store.setPixelsPhysicalSizeX(physicalSizeX, imageIndex);
  }

  /* @see MetadataStore#setPixelsPhysicalSizeY(Double, int) */
  public void setPixelsPhysicalSizeY(Double physicalSizeY, int imageIndex) {
    store.setPixelsPhysicalSizeY(physicalSizeY, imageIndex);
  }

  /* @see MetadataStore#setPixelsPhysicalSizeZ(Double, int) */
  public void setPixelsPhysicalSizeZ(Double physicalSizeZ, int imageIndex) {
    store.setPixelsPhysicalSizeZ(physicalSizeZ, imageIndex);
  }

  /* @see MetadataStore#setPixelsSizeC(PositiveInteger, int) */
  public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex) {
    store.setPixelsSizeC(sizeC, imageIndex);
  }

  /* @see MetadataStore#setPixelsSizeT(PositiveInteger, int) */
  public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex) {
    store.setPixelsSizeT(sizeT, imageIndex);
  }

  /* @see MetadataStore#setPixelsSizeX(PositiveInteger, int) */
  public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex) {
    store.setPixelsSizeX(sizeX, imageIndex);
  }

  /* @see MetadataStore#setPixelsSizeY(PositiveInteger, int) */
  public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex) {
    store.setPixelsSizeY(sizeY, imageIndex);
  }

  /* @see MetadataStore#setPixelsSizeZ(PositiveInteger, int) */
  public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex) {
    store.setPixelsSizeZ(sizeZ, imageIndex);
  }

  /* @see MetadataStore#setPixelsTimeIncrement(Double, int) */
  public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex) {
    store.setPixelsTimeIncrement(timeIncrement, imageIndex);
  }

  /* @see MetadataStore#setPixelsType(PixelType, int) */
  public void setPixelsType(PixelType type, int imageIndex) {
    store.setPixelsType(type, imageIndex);
  }

  // -- PixelsAnnotationRef property storage -

  // -- PixelsBinData property storage -

  /* @see MetadataStore#setPixelsBinDataBigEndian(Boolean, int, int) */
  public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex) {
    store.setPixelsBinDataBigEndian(bigEndian, imageIndex, binDataIndex);
  }

  // -- Plane property storage -

  /* @see MetadataStore#setPlaneAnnotationRef(String, int, int, int) */
  public void setPlaneAnnotationRef(String annotationRef, int imageIndex, int planeIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setPlaneAnnotationRef(filteredValue, imageIndex, planeIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setPlaneDeltaT(Double, int, int) */
  public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex) {
    store.setPlaneDeltaT(deltaT, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneExposureTime(Double, int, int) */
  public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex) {
    store.setPlaneExposureTime(exposureTime, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneHashSHA1(String, int, int) */
  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex) {
    String filteredValue = filter ? DataTools.sanitize(hashSHA1) : hashSHA1;
    store.setPlaneHashSHA1(filteredValue, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlanePositionX(Double, int, int) */
  public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex) {
    store.setPlanePositionX(positionX, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlanePositionY(Double, int, int) */
  public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex) {
    store.setPlanePositionY(positionY, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlanePositionZ(Double, int, int) */
  public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex) {
    store.setPlanePositionZ(positionZ, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int planeIndex) {
    store.setPlaneTheC(theC, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int planeIndex) {
    store.setPlaneTheT(theT, imageIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int planeIndex) {
    store.setPlaneTheZ(theZ, imageIndex, planeIndex);
  }

  // -- PlaneAnnotationRef property storage -

  // -- Plate property storage -

  /* @see MetadataStore#setPlateAnnotationRef(String, int, int) */
  public void setPlateAnnotationRef(String annotationRef, int plateIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setPlateAnnotationRef(filteredValue, plateIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setPlateColumnNamingConvention(NamingConvention, int) */
  public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex) {
    store.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
  }

  /* @see MetadataStore#setPlateColumns(Integer, int) */
  public void setPlateColumns(Integer columns, int plateIndex) {
    store.setPlateColumns(columns, plateIndex);
  }

  /* @see MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setPlateDescription(filteredValue, plateIndex);
  }

  /* @see MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
    String filteredValue = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setPlateExternalIdentifier(filteredValue, plateIndex);
  }

  /* @see MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPlateID(filteredValue, plateIndex);
  }

  /* @see MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setPlateName(filteredValue, plateIndex);
  }

  /* @see MetadataStore#setPlateRowNamingConvention(NamingConvention, int) */
  public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex) {
    store.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
  }

  /* @see MetadataStore#setPlateRows(Integer, int) */
  public void setPlateRows(Integer rows, int plateIndex) {
    store.setPlateRows(rows, plateIndex);
  }

  /* @see MetadataStore#setPlateScreenRef(String, int, int) */
  public void setPlateScreenRef(String screenRef, int plateIndex, int screenRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(screenRef) : screenRef;
    store.setPlateScreenRef(filteredValue, plateIndex, screenRefIndex);
  }

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    String filteredValue = filter ? DataTools.sanitize(status) : status;
    store.setPlateStatus(filteredValue, plateIndex);
  }

  /* @see MetadataStore#setPlateWellOriginX(Double, int) */
  public void setPlateWellOriginX(Double wellOriginX, int plateIndex) {
    store.setPlateWellOriginX(wellOriginX, plateIndex);
  }

  /* @see MetadataStore#setPlateWellOriginY(Double, int) */
  public void setPlateWellOriginY(Double wellOriginY, int plateIndex) {
    store.setPlateWellOriginY(wellOriginY, plateIndex);
  }

  // -- PlateAcquisition property storage -

  /* @see MetadataStore#setPlateAcquisitionAnnotationRef(String, int, int, int) */
  public void setPlateAcquisitionAnnotationRef(String annotationRef, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setPlateAcquisitionAnnotationRef(filteredValue, plateIndex, plateAcquisitionIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionDescription(String, int, int) */
  public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setPlateAcquisitionDescription(filteredValue, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionEndTime(String, int, int) */
  public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex) {
    String filteredValue = filter ? DataTools.sanitize(endTime) : endTime;
    store.setPlateAcquisitionEndTime(filteredValue, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionID(String, int, int) */
  public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPlateAcquisitionID(filteredValue, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionMaximumFieldCount(Integer, int, int) */
  public void setPlateAcquisitionMaximumFieldCount(Integer maximumFieldCount, int plateIndex, int plateAcquisitionIndex) {
    store.setPlateAcquisitionMaximumFieldCount(maximumFieldCount, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionName(String, int, int) */
  public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setPlateAcquisitionName(filteredValue, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionStartTime(String, int, int) */
  public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex) {
    String filteredValue = filter ? DataTools.sanitize(startTime) : startTime;
    store.setPlateAcquisitionStartTime(filteredValue, plateIndex, plateAcquisitionIndex);
  }

  /* @see MetadataStore#setPlateAcquisitionWellSampleRef(String, int, int, int) */
  public void setPlateAcquisitionWellSampleRef(String wellSampleRef, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(wellSampleRef) : wellSampleRef;
    store.setPlateAcquisitionWellSampleRef(filteredValue, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
  }

  // -- PlateAcquisitionAnnotationRef property storage -

  // -- PlateAnnotationRef property storage -

  // -- PlateRef property storage -

  // -- Point property storage -

  /* @see MetadataStore#setPointDescription(String, int, int) */
  public void setPointDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setPointDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointFill(Integer, int, int) */
  public void setPointFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setPointFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointFontSize(Integer, int, int) */
  public void setPointFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setPointFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointID(String, int, int) */
  public void setPointID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPointID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointLabel(String, int, int) */
  public void setPointLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setPointLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointName(String, int, int) */
  public void setPointName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setPointName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointStroke(Integer, int, int) */
  public void setPointStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setPointStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointStrokeDashArray(String, int, int) */
  public void setPointStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setPointStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointStrokeWidth(Double, int, int) */
  public void setPointStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setPointStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointTheC(Integer, int, int) */
  public void setPointTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setPointTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointTheT(Integer, int, int) */
  public void setPointTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setPointTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointTheZ(Integer, int, int) */
  public void setPointTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setPointTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointTransform(String, int, int) */
  public void setPointTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setPointTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointX(Double, int, int) */
  public void setPointX(Double x, int roiIndex, int shapeIndex) {
    store.setPointX(x, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointY(Double, int, int) */
  public void setPointY(Double y, int roiIndex, int shapeIndex) {
    store.setPointY(y, roiIndex, shapeIndex);
  }

  // -- Polyline property storage -

  /* @see MetadataStore#setPolylineClosed(Boolean, int, int) */
  public void setPolylineClosed(Boolean closed, int roiIndex, int shapeIndex) {
    store.setPolylineClosed(closed, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineDescription(String, int, int) */
  public void setPolylineDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setPolylineDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineFill(Integer, int, int) */
  public void setPolylineFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setPolylineFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineFontSize(Integer, int, int) */
  public void setPolylineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setPolylineFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineID(String, int, int) */
  public void setPolylineID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setPolylineID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineLabel(String, int, int) */
  public void setPolylineLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setPolylineLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineName(String, int, int) */
  public void setPolylineName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setPolylineName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylinePoints(String, int, int) */
  public void setPolylinePoints(String points, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(points) : points;
    store.setPolylinePoints(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineStroke(Integer, int, int) */
  public void setPolylineStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setPolylineStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineStrokeDashArray(String, int, int) */
  public void setPolylineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setPolylineStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineStrokeWidth(Double, int, int) */
  public void setPolylineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setPolylineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineTheC(Integer, int, int) */
  public void setPolylineTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setPolylineTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineTheT(Integer, int, int) */
  public void setPolylineTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setPolylineTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineTheZ(Integer, int, int) */
  public void setPolylineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setPolylineTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineTransform(String, int, int) */
  public void setPolylineTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setPolylineTransform(filteredValue, roiIndex, shapeIndex);
  }

  // -- Project property storage -

  /* @see MetadataStore#setProjectAnnotationRef(String, int, int) */
  public void setProjectAnnotationRef(String annotationRef, int projectIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setProjectAnnotationRef(filteredValue, projectIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setProjectDescription(String, int) */
  public void setProjectDescription(String description, int projectIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setProjectDescription(filteredValue, projectIndex);
  }

  /* @see MetadataStore#setProjectExperimenterRef(String, int) */
  public void setProjectExperimenterRef(String experimenterRef, int projectIndex) {
    String filteredValue = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setProjectExperimenterRef(filteredValue, projectIndex);
  }

  /* @see MetadataStore#setProjectGroupRef(String, int) */
  public void setProjectGroupRef(String groupRef, int projectIndex) {
    String filteredValue = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setProjectGroupRef(filteredValue, projectIndex);
  }

  /* @see MetadataStore#setProjectID(String, int) */
  public void setProjectID(String id, int projectIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setProjectID(filteredValue, projectIndex);
  }

  /* @see MetadataStore#setProjectName(String, int) */
  public void setProjectName(String name, int projectIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setProjectName(filteredValue, projectIndex);
  }

  // -- ProjectAnnotationRef property storage -

  // -- ProjectRef property storage -

  // -- ROI property storage -

  /* @see MetadataStore#setROIAnnotationRef(String, int, int) */
  public void setROIAnnotationRef(String annotationRef, int roiIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setROIAnnotationRef(filteredValue, roiIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setROIDescription(String, int) */
  public void setROIDescription(String description, int roiIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setROIDescription(filteredValue, roiIndex);
  }

  /* @see MetadataStore#setROIID(String, int) */
  public void setROIID(String id, int roiIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setROIID(filteredValue, roiIndex);
  }

  /* @see MetadataStore#setROIName(String, int) */
  public void setROIName(String name, int roiIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setROIName(filteredValue, roiIndex);
  }

  /* @see MetadataStore#setROINamespace(String, int) */
  public void setROINamespace(String namespace, int roiIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setROINamespace(filteredValue, roiIndex);
  }

  // -- ROIAnnotationRef property storage -

  // -- Reagent property storage -

  /* @see MetadataStore#setReagentAnnotationRef(String, int, int, int) */
  public void setReagentAnnotationRef(String annotationRef, int screenIndex, int reagentIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setReagentAnnotationRef(filteredValue, screenIndex, reagentIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setReagentDescription(filteredValue, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setReagentID(filteredValue, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setReagentName(filteredValue, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
    String filteredValue = filter ? DataTools.sanitize(reagentIdentifier) : reagentIdentifier;
    store.setReagentReagentIdentifier(filteredValue, screenIndex, reagentIndex);
  }

  // -- ReagentAnnotationRef property storage -

  // -- Rectangle property storage -

  /* @see MetadataStore#setRectangleDescription(String, int, int) */
  public void setRectangleDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setRectangleDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleFill(Integer, int, int) */
  public void setRectangleFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setRectangleFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleFontSize(Integer, int, int) */
  public void setRectangleFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setRectangleFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleHeight(Double, int, int) */
  public void setRectangleHeight(Double height, int roiIndex, int shapeIndex) {
    store.setRectangleHeight(height, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleID(String, int, int) */
  public void setRectangleID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setRectangleID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleLabel(String, int, int) */
  public void setRectangleLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setRectangleLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleName(String, int, int) */
  public void setRectangleName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setRectangleName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleStroke(Integer, int, int) */
  public void setRectangleStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setRectangleStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleStrokeDashArray(String, int, int) */
  public void setRectangleStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setRectangleStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleStrokeWidth(Double, int, int) */
  public void setRectangleStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setRectangleStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleTheC(Integer, int, int) */
  public void setRectangleTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setRectangleTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleTheT(Integer, int, int) */
  public void setRectangleTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setRectangleTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleTheZ(Integer, int, int) */
  public void setRectangleTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setRectangleTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleTransform(String, int, int) */
  public void setRectangleTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setRectangleTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleWidth(Double, int, int) */
  public void setRectangleWidth(Double width, int roiIndex, int shapeIndex) {
    store.setRectangleWidth(width, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleX(Double, int, int) */
  public void setRectangleX(Double x, int roiIndex, int shapeIndex) {
    store.setRectangleX(x, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectangleY(Double, int, int) */
  public void setRectangleY(Double y, int roiIndex, int shapeIndex) {
    store.setRectangleY(y, roiIndex, shapeIndex);
  }

  // -- Screen property storage -

  /* @see MetadataStore#setScreenAnnotationRef(String, int, int) */
  public void setScreenAnnotationRef(String annotationRef, int screenIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setScreenAnnotationRef(filteredValue, screenIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setScreenDescription(String, int) */
  public void setScreenDescription(String description, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setScreenDescription(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setScreenID(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setScreenName(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenPlateRef(String, int, int) */
  public void setScreenPlateRef(String plateRef, int screenIndex, int plateRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(plateRef) : plateRef;
    store.setScreenPlateRef(filteredValue, screenIndex, plateRefIndex);
  }

  /* @see MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(protocolDescription) : protocolDescription;
    store.setScreenProtocolDescription(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(protocolIdentifier) : protocolIdentifier;
    store.setScreenProtocolIdentifier(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(reagentSetDescription) : reagentSetDescription;
    store.setScreenReagentSetDescription(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenReagentSetIdentifier(String, int) */
  public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(reagentSetIdentifier) : reagentSetIdentifier;
    store.setScreenReagentSetIdentifier(filteredValue, screenIndex);
  }

  /* @see MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
    String filteredValue = filter ? DataTools.sanitize(type) : type;
    store.setScreenType(filteredValue, screenIndex);
  }

  // -- ScreenAnnotationRef property storage -

  // -- ScreenRef property storage -

  // -- ShapeAnnotationRef property storage -

  // -- StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setStageLabelName(filteredValue, imageIndex);
  }

  /* @see MetadataStore#setStageLabelX(Double, int) */
  public void setStageLabelX(Double x, int imageIndex) {
    store.setStageLabelX(x, imageIndex);
  }

  /* @see MetadataStore#setStageLabelY(Double, int) */
  public void setStageLabelY(Double y, int imageIndex) {
    store.setStageLabelY(y, imageIndex);
  }

  /* @see MetadataStore#setStageLabelZ(Double, int) */
  public void setStageLabelZ(Double z, int imageIndex) {
    store.setStageLabelZ(z, imageIndex);
  }

  // -- StringAnnotation property storage -

  /* @see MetadataStore#setStringAnnotationID(String, int) */
  public void setStringAnnotationID(String id, int stringAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setStringAnnotationID(filteredValue, stringAnnotationIndex);
  }

  /* @see MetadataStore#setStringAnnotationNamespace(String, int) */
  public void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setStringAnnotationNamespace(filteredValue, stringAnnotationIndex);
  }

  /* @see MetadataStore#setStringAnnotationValue(String, int) */
  public void setStringAnnotationValue(String value, int stringAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(value) : value;
    store.setStringAnnotationValue(filteredValue, stringAnnotationIndex);
  }

  // -- Text property storage -

  /* @see MetadataStore#setTextDescription(String, int, int) */
  public void setTextDescription(String description, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(description) : description;
    store.setTextDescription(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextFill(Integer, int, int) */
  public void setTextFill(Integer fill, int roiIndex, int shapeIndex) {
    store.setTextFill(fill, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextFontSize(Integer, int, int) */
  public void setTextFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    store.setTextFontSize(fontSize, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextID(String, int, int) */
  public void setTextID(String id, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setTextID(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextLabel(String, int, int) */
  public void setTextLabel(String label, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(label) : label;
    store.setTextLabel(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextName(String, int, int) */
  public void setTextName(String name, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(name) : name;
    store.setTextName(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextStroke(Integer, int, int) */
  public void setTextStroke(Integer stroke, int roiIndex, int shapeIndex) {
    store.setTextStroke(stroke, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextStrokeDashArray(String, int, int) */
  public void setTextStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setTextStrokeDashArray(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextStrokeWidth(Double, int, int) */
  public void setTextStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    store.setTextStrokeWidth(strokeWidth, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextTheC(Integer, int, int) */
  public void setTextTheC(Integer theC, int roiIndex, int shapeIndex) {
    store.setTextTheC(theC, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextTheT(Integer, int, int) */
  public void setTextTheT(Integer theT, int roiIndex, int shapeIndex) {
    store.setTextTheT(theT, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextTheZ(Integer, int, int) */
  public void setTextTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    store.setTextTheZ(theZ, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextTransform(String, int, int) */
  public void setTextTransform(String transform, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(transform) : transform;
    store.setTextTransform(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextValue(String, int, int) */
  public void setTextValue(String value, int roiIndex, int shapeIndex) {
    String filteredValue = filter ? DataTools.sanitize(value) : value;
    store.setTextValue(filteredValue, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextX(Double, int, int) */
  public void setTextX(Double x, int roiIndex, int shapeIndex) {
    store.setTextX(x, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setTextY(Double, int, int) */
  public void setTextY(Double y, int roiIndex, int shapeIndex) {
    store.setTextY(y, roiIndex, shapeIndex);
  }

  // -- TiffData property storage -

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int tiffDataIndex) {
    store.setTiffDataFirstC(firstC, imageIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int tiffDataIndex) {
    store.setTiffDataFirstT(firstT, imageIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int tiffDataIndex) {
    store.setTiffDataFirstZ(firstZ, imageIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int tiffDataIndex) {
    store.setTiffDataIFD(ifd, imageIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataPlaneCount(Integer, int, int) */
  public void setTiffDataPlaneCount(Integer planeCount, int imageIndex, int tiffDataIndex) {
    store.setTiffDataPlaneCount(planeCount, imageIndex, tiffDataIndex);
  }

  // -- TimestampAnnotation property storage -

  /* @see MetadataStore#setTimestampAnnotationID(String, int) */
  public void setTimestampAnnotationID(String id, int timestampAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setTimestampAnnotationID(filteredValue, timestampAnnotationIndex);
  }

  /* @see MetadataStore#setTimestampAnnotationNamespace(String, int) */
  public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setTimestampAnnotationNamespace(filteredValue, timestampAnnotationIndex);
  }

  /* @see MetadataStore#setTimestampAnnotationValue(String, int) */
  public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(value) : value;
    store.setTimestampAnnotationValue(filteredValue, timestampAnnotationIndex);
  }

  // -- TransmittanceRange property storage -

  /* @see MetadataStore#setTransmittanceRangeCutIn(Integer, int, int) */
  public void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setTransmittanceRangeCutInTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setTransmittanceRangeCutOut(Integer, int, int) */
  public void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setTransmittanceRangeCutOutTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setTransmittanceRangeTransmittance(PercentFraction, int, int) */
  public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
  }

  // -- UUID property storage -

  public void setUUIDValue(String value, int imageIndex, int tiffDataIndex)
  {
    String filteredValue = filter ? DataTools.sanitize(value) : value;
    store.setUUIDValue(filteredValue, imageIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setUUIDFileName(String, int, int) */
  public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex) {
    String filteredValue = filter ? DataTools.sanitize(fileName) : fileName;
    store.setUUIDFileName(filteredValue, imageIndex, tiffDataIndex);
  }

  // -- Well property storage -

  /* @see MetadataStore#setWellAnnotationRef(String, int, int, int) */
  public void setWellAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setWellAnnotationRef(filteredValue, plateIndex, wellIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setWellColor(Integer, int, int) */
  public void setWellColor(Integer color, int plateIndex, int wellIndex) {
    store.setWellColor(color, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellColumn(NonNegativeInteger, int, int) */
  public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex) {
    store.setWellColumn(column, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
    String filteredValue = filter ? DataTools.sanitize(externalDescription) : externalDescription;
    store.setWellExternalDescription(filteredValue, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
    String filteredValue = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setWellExternalIdentifier(filteredValue, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setWellID(filteredValue, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellReagentRef(String, int, int) */
  public void setWellReagentRef(String reagentRef, int plateIndex, int wellIndex) {
    String filteredValue = filter ? DataTools.sanitize(reagentRef) : reagentRef;
    store.setWellReagentRef(filteredValue, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellRow(NonNegativeInteger, int, int) */
  public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex) {
    store.setWellRow(row, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellStatus(String, int, int) */
  public void setWellStatus(String status, int plateIndex, int wellIndex) {
    String filteredValue = filter ? DataTools.sanitize(status) : status;
    store.setWellStatus(filteredValue, plateIndex, wellIndex);
  }

  // -- WellAnnotationRef property storage -

  // -- WellSample property storage -

  /* @see MetadataStore#setWellSampleAnnotationRef(String, int, int, int, int) */
  public void setWellSampleAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex) {
    String filteredValue = filter ? DataTools.sanitize(annotationRef) : annotationRef;
    store.setWellSampleAnnotationRef(filteredValue, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
  }

  /* @see MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setWellSampleID(filteredValue, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleImageRef(String, int, int, int) */
  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex) {
    String filteredValue = filter ? DataTools.sanitize(imageRef) : imageRef;
    store.setWellSampleImageRef(filteredValue, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleIndex(NonNegativeInteger, int, int, int) */
  public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePositionX(Double, int, int, int) */
  public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePositionX(positionX, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePositionY(Double, int, int, int) */
  public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePositionY(positionY, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

  // -- WellSampleAnnotationRef property storage -

  // -- WellSampleRef property storage -

  // -- XMLAnnotation property storage -

  /* @see MetadataStore#setXMLAnnotationID(String, int) */
  public void setXMLAnnotationID(String id, int xmlAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(id) : id;
    store.setXMLAnnotationID(filteredValue, xmlAnnotationIndex);
  }

  /* @see MetadataStore#setXMLAnnotationNamespace(String, int) */
  public void setXMLAnnotationNamespace(String namespace, int xmlAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(namespace) : namespace;
    store.setXMLAnnotationNamespace(filteredValue, xmlAnnotationIndex);
  }

  /* @see MetadataStore#setXMLAnnotationValue(String, int) */
  public void setXMLAnnotationValue(String value, int xmlAnnotationIndex) {
    String filteredValue = filter ? DataTools.sanitize(value) : value;
    store.setXMLAnnotationValue(filteredValue, xmlAnnotationIndex);
  }

}
