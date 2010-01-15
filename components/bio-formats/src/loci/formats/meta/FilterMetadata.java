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
 * Created by melissa via MetadataAutogen on Jan 15, 2010 2:15:58 PM CST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import loci.common.DataTools;

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

  /* @see MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setArcType(value, instrumentIndex, lightSourceIndex);
  }

  // -- ChannelComponent property storage -

  /* @see MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    String value = filter ? DataTools.sanitize(colorDomain) : colorDomain;
    store.setChannelComponentColorDomain(value, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  /* @see MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    store.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  /* @see MetadataStore#setChannelComponentPixels(String, int, int, int) */
  public void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    String value = filter ? DataTools.sanitize(pixels) : pixels;
    store.setChannelComponentPixels(value, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // -- Circle property storage -

  /* @see MetadataStore#setCircleCx(String, int, int, int) */
  public void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setCircleCx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircleCy(String, int, int, int) */
  public void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setCircleCy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircleID(String, int, int, int) */
  public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setCircleID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircleR(String, int, int, int) */
  public void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(r) : r;
    store.setCircleR(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircleTransform(String, int, int, int) */
  public void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setCircleTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Contact property storage -

  /* @see MetadataStore#setContactExperimenter(String, int) */
  public void setContactExperimenter(String experimenter, int groupIndex) {
    String value = filter ? DataTools.sanitize(experimenter) : experimenter;
    store.setContactExperimenter(value, groupIndex);
  }

  // -- Dataset property storage -

  /* @see MetadataStore#setDatasetDescription(String, int) */
  public void setDatasetDescription(String description, int datasetIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setDatasetDescription(value, datasetIndex);
  }

  /* @see MetadataStore#setDatasetExperimenterRef(String, int) */
  public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex) {
    String value = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setDatasetExperimenterRef(value, datasetIndex);
  }

  /* @see MetadataStore#setDatasetGroupRef(String, int) */
  public void setDatasetGroupRef(String groupRef, int datasetIndex) {
    String value = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setDatasetGroupRef(value, datasetIndex);
  }

  /* @see MetadataStore#setDatasetID(String, int) */
  public void setDatasetID(String id, int datasetIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDatasetID(value, datasetIndex);
  }

  /* @see MetadataStore#setDatasetLocked(Boolean, int) */
  public void setDatasetLocked(Boolean locked, int datasetIndex) {
    store.setDatasetLocked(locked, datasetIndex);
  }

  /* @see MetadataStore#setDatasetName(String, int) */
  public void setDatasetName(String name, int datasetIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setDatasetName(value, datasetIndex);
  }

  // -- DatasetRef property storage -

  /* @see MetadataStore#setDatasetRefID(String, int, int) */
  public void setDatasetRefID(String id, int imageIndex, int datasetRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDatasetRefID(value, imageIndex, datasetRefIndex);
  }

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
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDetectorID(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setDetectorManufacturer(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setDetectorModel(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorOffset(Double, int, int) */
  public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex) {
    store.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setDetectorSerialNumber(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setDetectorType(value, instrumentIndex, detectorIndex);
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

  /* @see MetadataStore#setDetectorSettingsBinning(String, int, int) */
  public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(binning) : binning;
    store.setDetectorSettingsBinning(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsDetector(String, int, int) */
  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(detector) : detector;
    store.setDetectorSettingsDetector(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsGain(Double, int, int) */
  public void setDetectorSettingsGain(Double gain, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Double, int, int) */
  public void setDetectorSettingsOffset(Double offset, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Double, int, int) */
  public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Double, int, int) */
  public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex);
  }

  // -- Dichroic property storage -

  /* @see MetadataStore#setDichroicID(String, int, int) */
  public void setDichroicID(String id, int instrumentIndex, int dichroicIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDichroicID(value, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicLotNumber(String, int, int) */
  public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex) {
    String value = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setDichroicLotNumber(value, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicManufacturer(String, int, int) */
  public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setDichroicManufacturer(value, instrumentIndex, dichroicIndex);
  }

  /* @see MetadataStore#setDichroicModel(String, int, int) */
  public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setDichroicModel(value, instrumentIndex, dichroicIndex);
  }

  // -- Dimensions property storage -

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Double, int, int) */
  public void setDimensionsPhysicalSizeX(Double physicalSizeX, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Double, int, int) */
  public void setDimensionsPhysicalSizeY(Double physicalSizeY, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Double, int, int) */
  public void setDimensionsPhysicalSizeZ(Double physicalSizeZ, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Double, int, int) */
  public void setDimensionsTimeIncrement(Double timeIncrement, int imageIndex, int pixelsIndex) {
    store.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    store.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    store.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  // -- DisplayOptions property storage -

  /* @see MetadataStore#setDisplayOptionsDisplay(String, int) */
  public void setDisplayOptionsDisplay(String display, int imageIndex) {
    String value = filter ? DataTools.sanitize(display) : display;
    store.setDisplayOptionsDisplay(value, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsID(String, int) */
  public void setDisplayOptionsID(String id, int imageIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDisplayOptionsID(value, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsZoom(Double, int) */
  public void setDisplayOptionsZoom(Double zoom, int imageIndex) {
    store.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // -- Ellipse property storage -

  /* @see MetadataStore#setEllipseCx(String, int, int, int) */
  public void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setEllipseCx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseCy(String, int, int, int) */
  public void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setEllipseCy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseID(String, int, int, int) */
  public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setEllipseID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseRx(String, int, int, int) */
  public void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(rx) : rx;
    store.setEllipseRx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseRy(String, int, int, int) */
  public void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(ry) : ry;
    store.setEllipseRy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipseTransform(String, int, int, int) */
  public void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setEllipseTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- EmFilter property storage -

  /* @see MetadataStore#setEmFilterLotNumber(String, int, int) */
  public void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setEmFilterLotNumber(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setEmFilterManufacturer(String, int, int) */
  public void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setEmFilterManufacturer(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setEmFilterModel(String, int, int) */
  public void setEmFilterModel(String model, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setEmFilterModel(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setEmFilterType(String, int, int) */
  public void setEmFilterType(String type, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setEmFilterType(value, instrumentIndex, filterIndex);
  }

  // -- ExFilter property storage -

  /* @see MetadataStore#setExFilterLotNumber(String, int, int) */
  public void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setExFilterLotNumber(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setExFilterManufacturer(String, int, int) */
  public void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setExFilterManufacturer(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setExFilterModel(String, int, int) */
  public void setExFilterModel(String model, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setExFilterModel(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setExFilterType(String, int, int) */
  public void setExFilterType(String type, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setExFilterType(value, instrumentIndex, filterIndex);
  }

  // -- Experiment property storage -

  /* @see MetadataStore#setExperimentDescription(String, int) */
  public void setExperimentDescription(String description, int experimentIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setExperimentDescription(value, experimentIndex);
  }

  /* @see MetadataStore#setExperimentExperimenterRef(String, int) */
  public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex) {
    String value = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setExperimentExperimenterRef(value, experimentIndex);
  }

  /* @see MetadataStore#setExperimentID(String, int) */
  public void setExperimentID(String id, int experimentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setExperimentID(value, experimentIndex);
  }

  /* @see MetadataStore#setExperimentType(String, int) */
  public void setExperimentType(String type, int experimentIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setExperimentType(value, experimentIndex);
  }

  // -- Experimenter property storage -

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(email) : email;
    store.setExperimenterEmail(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(firstName) : firstName;
    store.setExperimenterFirstName(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setExperimenterID(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(institution) : institution;
    store.setExperimenterInstitution(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(lastName) : lastName;
    store.setExperimenterLastName(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterOMEName(String, int) */
  public void setExperimenterOMEName(String omeName, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(omeName) : omeName;
    store.setExperimenterOMEName(value, experimenterIndex);
  }

  // -- ExperimenterMembership property storage -

  /* @see MetadataStore#setExperimenterMembershipGroup(String, int, int) */
  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex) {
    String value = filter ? DataTools.sanitize(group) : group;
    store.setExperimenterMembershipGroup(value, experimenterIndex, groupRefIndex);
  }

  // -- Filament property storage -

  /* @see MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setFilamentType(value, instrumentIndex, lightSourceIndex);
  }

  // -- Filter property storage -

  /* @see MetadataStore#setFilterFilterWheel(String, int, int) */
  public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(filterWheel) : filterWheel;
    store.setFilterFilterWheel(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterID(String, int, int) */
  public void setFilterID(String id, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setFilterID(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterLotNumber(String, int, int) */
  public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setFilterLotNumber(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterManufacturer(String, int, int) */
  public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setFilterManufacturer(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterModel(String, int, int) */
  public void setFilterModel(String model, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setFilterModel(value, instrumentIndex, filterIndex);
  }

  /* @see MetadataStore#setFilterType(String, int, int) */
  public void setFilterType(String type, int instrumentIndex, int filterIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setFilterType(value, instrumentIndex, filterIndex);
  }

  // -- FilterSet property storage -

  /* @see MetadataStore#setFilterSetDichroic(String, int, int) */
  public void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(dichroic) : dichroic;
    store.setFilterSetDichroic(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetEmFilter(String, int, int) */
  public void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(emFilter) : emFilter;
    store.setFilterSetEmFilter(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetExFilter(String, int, int) */
  public void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(exFilter) : exFilter;
    store.setFilterSetExFilter(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetID(String, int, int) */
  public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setFilterSetID(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetLotNumber(String, int, int) */
  public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(lotNumber) : lotNumber;
    store.setFilterSetLotNumber(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetManufacturer(String, int, int) */
  public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setFilterSetManufacturer(value, instrumentIndex, filterSetIndex);
  }

  /* @see MetadataStore#setFilterSetModel(String, int, int) */
  public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setFilterSetModel(value, instrumentIndex, filterSetIndex);
  }

  // -- Group property storage -

  /* @see MetadataStore#setGroupID(String, int) */
  public void setGroupID(String id, int groupIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setGroupID(value, groupIndex);
  }

  /* @see MetadataStore#setGroupName(String, int) */
  public void setGroupName(String name, int groupIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setGroupName(value, groupIndex);
  }

  // -- GroupRef property storage -

  // -- Image property storage -

  /* @see MetadataStore#setImageAcquiredPixels(String, int) */
  public void setImageAcquiredPixels(String acquiredPixels, int imageIndex) {
    String value = filter ? DataTools.sanitize(acquiredPixels) : acquiredPixels;
    store.setImageAcquiredPixels(value, imageIndex);
  }

  /* @see MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    String value = filter ? DataTools.sanitize(creationDate) : creationDate;
    store.setImageCreationDate(value, imageIndex);
  }

  /* @see MetadataStore#setImageDefaultPixels(String, int) */
  public void setImageDefaultPixels(String defaultPixels, int imageIndex) {
    String value = filter ? DataTools.sanitize(defaultPixels) : defaultPixels;
    store.setImageDefaultPixels(value, imageIndex);
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setImageDescription(value, imageIndex);
  }

  /* @see MetadataStore#setImageExperimentRef(String, int) */
  public void setImageExperimentRef(String experimentRef, int imageIndex) {
    String value = filter ? DataTools.sanitize(experimentRef) : experimentRef;
    store.setImageExperimentRef(value, imageIndex);
  }

  /* @see MetadataStore#setImageExperimenterRef(String, int) */
  public void setImageExperimenterRef(String experimenterRef, int imageIndex) {
    String value = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setImageExperimenterRef(value, imageIndex);
  }

  /* @see MetadataStore#setImageGroupRef(String, int) */
  public void setImageGroupRef(String groupRef, int imageIndex) {
    String value = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setImageGroupRef(value, imageIndex);
  }

  /* @see MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setImageID(value, imageIndex);
  }

  /* @see MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
    String value = filter ? DataTools.sanitize(instrumentRef) : instrumentRef;
    store.setImageInstrumentRef(value, imageIndex);
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setImageName(value, imageIndex);
  }

  // -- ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Double, int) */
  public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex) {
    store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Double, int) */
  public void setImagingEnvironmentCO2Percent(Double cO2Percent, int imageIndex) {
    store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Double, int) */
  public void setImagingEnvironmentHumidity(Double humidity, int imageIndex) {
    store.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Double, int) */
  public void setImagingEnvironmentTemperature(Double temperature, int imageIndex) {
    store.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // -- Instrument property storage -

  /* @see MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setInstrumentID(value, instrumentIndex);
  }

  // -- Laser property storage -

  /* @see MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(laserMedium) : laserMedium;
    store.setLaserLaserMedium(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserPockelCell(Boolean, int, int) */
  public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex) {
    store.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(pulse) : pulse;
    store.setLaserPulse(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserRepetitionRate(Double, int, int) */
  public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int lightSourceIndex) {
    store.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    store.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setLaserType(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    store.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  // -- LightSource property storage -

  /* @see MetadataStore#setLightSourceID(String, int, int) */
  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setLightSourceID(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setLightSourceManufacturer(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setLightSourceModel(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourcePower(Double, int, int) */
  public void setLightSourcePower(Double power, int instrumentIndex, int lightSourceIndex) {
    store.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setLightSourceSerialNumber(value, instrumentIndex, lightSourceIndex);
  }

  // -- LightSourceRef property storage -

  /* @see MetadataStore#setLightSourceRefAttenuation(Double, int, int, int) */
  public void setLightSourceRefAttenuation(Double attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    store.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  /* @see MetadataStore#setLightSourceRefLightSource(String, int, int, int) */
  public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    String value = filter ? DataTools.sanitize(lightSource) : lightSource;
    store.setLightSourceRefLightSource(value, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  /* @see MetadataStore#setLightSourceRefWavelength(Integer, int, int, int) */
  public void setLightSourceRefWavelength(Integer wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    store.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
  }

  // -- LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Double, int, int) */
  public void setLightSourceSettingsAttenuation(Double attenuation, int imageIndex, int logicalChannelIndex) {
    store.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsLightSource(String, int, int) */
  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(lightSource) : lightSource;
    store.setLightSourceSettingsLightSource(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    store.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // -- Line property storage -

  /* @see MetadataStore#setLineID(String, int, int, int) */
  public void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setLineID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineTransform(String, int, int, int) */
  public void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setLineTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineX1(String, int, int, int) */
  public void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x1) : x1;
    store.setLineX1(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineX2(String, int, int, int) */
  public void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x2) : x2;
    store.setLineX2(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineY1(String, int, int, int) */
  public void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y1) : y1;
    store.setLineY1(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLineY2(String, int, int, int) */
  public void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y2) : y2;
    store.setLineY2(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(contrastMethod) : contrastMethod;
    store.setLogicalChannelContrastMethod(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelDetector(String, int, int) */
  public void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(detector) : detector;
    store.setLogicalChannelDetector(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelFilterSet(String, int, int) */
  public void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(filterSet) : filterSet;
    store.setLogicalChannelFilterSet(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(fluor) : fluor;
    store.setLogicalChannelFluor(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelID(String, int, int) */
  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setLogicalChannelID(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(illuminationType) : illuminationType;
    store.setLogicalChannelIlluminationType(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelLightSource(String, int, int) */
  public void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(lightSource) : lightSource;
    store.setLogicalChannelLightSource(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(mode) : mode;
    store.setLogicalChannelMode(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setLogicalChannelName(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelNdFilter(Double, int, int) */
  public void setLogicalChannelNdFilter(Double ndFilter, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelOTF(String, int, int) */
  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(otf) : otf;
    store.setLogicalChannelOTF(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(photometricInterpretation) : photometricInterpretation;
    store.setLogicalChannelPhotometricInterpretation(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPinholeSize(Double, int, int) */
  public void setLogicalChannelPinholeSize(Double pinholeSize, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelSecondaryEmissionFilter(String, int, int) */
  public void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(secondaryEmissionFilter) : secondaryEmissionFilter;
    store.setLogicalChannelSecondaryEmissionFilter(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelSecondaryExcitationFilter(String, int, int) */
  public void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(secondaryExcitationFilter) : secondaryExcitationFilter;
    store.setLogicalChannelSecondaryExcitationFilter(value, imageIndex, logicalChannelIndex);
  }

  // -- Mask property storage -

  /* @see MetadataStore#setMaskHeight(String, int, int, int) */
  public void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(height) : height;
    store.setMaskHeight(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskID(String, int, int, int) */
  public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMaskID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskTransform(String, int, int, int) */
  public void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setMaskTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskWidth(String, int, int, int) */
  public void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(width) : width;
    store.setMaskWidth(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskX(String, int, int, int) */
  public void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x) : x;
    store.setMaskX(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskY(String, int, int, int) */
  public void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y) : y;
    store.setMaskY(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- MaskPixels property storage -

  /* @see MetadataStore#setMaskPixelsBigEndian(Boolean, int, int, int) */
  public void setMaskPixelsBigEndian(Boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex) {
    store.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsBinData(byte[], int, int, int) */
  public void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex) {
    store.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsExtendedPixelType(String, int, int, int) */
  public void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(extendedPixelType) : extendedPixelType;
    store.setMaskPixelsExtendedPixelType(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsID(String, int, int, int) */
  public void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMaskPixelsID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsSizeX(Integer, int, int, int) */
  public void setMaskPixelsSizeX(Integer sizeX, int imageIndex, int roiIndex, int shapeIndex) {
    store.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsSizeY(Integer, int, int, int) */
  public void setMaskPixelsSizeY(Integer sizeY, int imageIndex, int roiIndex, int shapeIndex) {
    store.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex);
  }

  // -- MicrobeamManipulation property storage -

  /* @see MetadataStore#setMicrobeamManipulationExperimenterRef(String, int, int) */
  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex) {
    String value = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setMicrobeamManipulationExperimenterRef(value, imageIndex, microbeamManipulationIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationID(String, int, int) */
  public void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMicrobeamManipulationID(value, imageIndex, microbeamManipulationIndex);
  }

  /* @see MetadataStore#setMicrobeamManipulationType(String, int, int) */
  public void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setMicrobeamManipulationType(value, imageIndex, microbeamManipulationIndex);
  }

  // -- MicrobeamManipulationRef property storage -

  /* @see MetadataStore#setMicrobeamManipulationRefID(String, int, int) */
  public void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMicrobeamManipulationRefID(value, experimentIndex, microbeamManipulationRefIndex);
  }

  // -- Microscope property storage -

  /* @see MetadataStore#setMicroscopeID(String, int) */
  public void setMicroscopeID(String id, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMicroscopeID(value, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeManufacturer(String, int) */
  public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setMicroscopeManufacturer(value, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeModel(String, int) */
  public void setMicroscopeModel(String model, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setMicroscopeModel(value, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeSerialNumber(String, int) */
  public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setMicroscopeSerialNumber(value, instrumentIndex);
  }

  /* @see MetadataStore#setMicroscopeType(String, int) */
  public void setMicroscopeType(String type, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setMicroscopeType(value, instrumentIndex);
  }

  // -- OTF property storage -

  /* @see MetadataStore#setOTFBinaryFile(String, int, int) */
  public void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(binaryFile) : binaryFile;
    store.setOTFBinaryFile(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setOTFID(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFObjective(String, int, int) */
  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(objective) : objective;
    store.setOTFObjective(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    store.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(pixelType) : pixelType;
    store.setOTFPixelType(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    store.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    store.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  // -- Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Double, int, int) */
  public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(correction) : correction;
    store.setObjectiveCorrection(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setObjectiveID(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(immersion) : immersion;
    store.setObjectiveImmersion(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveIris(Boolean, int, int) */
  public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveLensNA(Double, int, int) */
  public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setObjectiveManufacturer(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setObjectiveModel(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setObjectiveSerialNumber(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Double, int, int) */
  public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // -- ObjectiveSettings property storage -

  /* @see MetadataStore#setObjectiveSettingsCorrectionCollar(Double, int) */
  public void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex) {
    store.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
  }

  /* @see MetadataStore#setObjectiveSettingsMedium(String, int) */
  public void setObjectiveSettingsMedium(String medium, int imageIndex) {
    String value = filter ? DataTools.sanitize(medium) : medium;
    store.setObjectiveSettingsMedium(value, imageIndex);
  }

  /* @see MetadataStore#setObjectiveSettingsObjective(String, int) */
  public void setObjectiveSettingsObjective(String objective, int imageIndex) {
    String value = filter ? DataTools.sanitize(objective) : objective;
    store.setObjectiveSettingsObjective(value, imageIndex);
  }

  /* @see MetadataStore#setObjectiveSettingsRefractiveIndex(Double, int) */
  public void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex) {
    store.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
  }

  // -- Path property storage -

  /* @see MetadataStore#setPathD(String, int, int, int) */
  public void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(d) : d;
    store.setPathD(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPathID(String, int, int, int) */
  public void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPathID(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Pixels property storage -

  /* @see MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    store.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(dimensionOrder) : dimensionOrder;
    store.setPixelsDimensionOrder(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsID(String, int, int) */
  public void setPixelsID(String id, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPixelsID(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(pixelType) : pixelType;
    store.setPixelsPixelType(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  // -- Plane property storage -

  /* @see MetadataStore#setPlaneHashSHA1(String, int, int, int) */
  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex) {
    String value = filter ? DataTools.sanitize(hashSHA1) : hashSHA1;
    store.setPlaneHashSHA1(value, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneID(String, int, int, int) */
  public void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPlaneID(value, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTimingDeltaT(Double, int, int, int) */
  public void setPlaneTimingDeltaT(Double deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Double, int, int, int) */
  public void setPlaneTimingExposureTime(Double exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // -- Plate property storage -

  /* @see MetadataStore#setPlateColumnNamingConvention(String, int) */
  public void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex) {
    String value = filter ? DataTools.sanitize(columnNamingConvention) : columnNamingConvention;
    store.setPlateColumnNamingConvention(value, plateIndex);
  }

  /* @see MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setPlateDescription(value, plateIndex);
  }

  /* @see MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
    String value = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setPlateExternalIdentifier(value, plateIndex);
  }

  /* @see MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPlateID(value, plateIndex);
  }

  /* @see MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setPlateName(value, plateIndex);
  }

  /* @see MetadataStore#setPlateRowNamingConvention(String, int) */
  public void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex) {
    String value = filter ? DataTools.sanitize(rowNamingConvention) : rowNamingConvention;
    store.setPlateRowNamingConvention(value, plateIndex);
  }

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    String value = filter ? DataTools.sanitize(status) : status;
    store.setPlateStatus(value, plateIndex);
  }

  /* @see MetadataStore#setPlateWellOriginX(Double, int) */
  public void setPlateWellOriginX(Double wellOriginX, int plateIndex) {
    store.setPlateWellOriginX(wellOriginX, plateIndex);
  }

  /* @see MetadataStore#setPlateWellOriginY(Double, int) */
  public void setPlateWellOriginY(Double wellOriginY, int plateIndex) {
    store.setPlateWellOriginY(wellOriginY, plateIndex);
  }

  // -- PlateRef property storage -

  /* @see MetadataStore#setPlateRefID(String, int, int) */
  public void setPlateRefID(String id, int screenIndex, int plateRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPlateRefID(value, screenIndex, plateRefIndex);
  }

  /* @see MetadataStore#setPlateRefSample(Integer, int, int) */
  public void setPlateRefSample(Integer sample, int screenIndex, int plateRefIndex) {
    store.setPlateRefSample(sample, screenIndex, plateRefIndex);
  }

  /* @see MetadataStore#setPlateRefWell(String, int, int) */
  public void setPlateRefWell(String well, int screenIndex, int plateRefIndex) {
    String value = filter ? DataTools.sanitize(well) : well;
    store.setPlateRefWell(value, screenIndex, plateRefIndex);
  }

  // -- Point property storage -

  /* @see MetadataStore#setPointCx(String, int, int, int) */
  public void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setPointCx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointCy(String, int, int, int) */
  public void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setPointCy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointID(String, int, int, int) */
  public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPointID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointR(String, int, int, int) */
  public void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(r) : r;
    store.setPointR(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointTransform(String, int, int, int) */
  public void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPointTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Polygon property storage -

  /* @see MetadataStore#setPolygonID(String, int, int, int) */
  public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPolygonID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolygonPoints(String, int, int, int) */
  public void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(points) : points;
    store.setPolygonPoints(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolygonTransform(String, int, int, int) */
  public void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPolygonTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Polyline property storage -

  /* @see MetadataStore#setPolylineID(String, int, int, int) */
  public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPolylineID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylinePoints(String, int, int, int) */
  public void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(points) : points;
    store.setPolylinePoints(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylineTransform(String, int, int, int) */
  public void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPolylineTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Project property storage -

  /* @see MetadataStore#setProjectDescription(String, int) */
  public void setProjectDescription(String description, int projectIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setProjectDescription(value, projectIndex);
  }

  /* @see MetadataStore#setProjectExperimenterRef(String, int) */
  public void setProjectExperimenterRef(String experimenterRef, int projectIndex) {
    String value = filter ? DataTools.sanitize(experimenterRef) : experimenterRef;
    store.setProjectExperimenterRef(value, projectIndex);
  }

  /* @see MetadataStore#setProjectGroupRef(String, int) */
  public void setProjectGroupRef(String groupRef, int projectIndex) {
    String value = filter ? DataTools.sanitize(groupRef) : groupRef;
    store.setProjectGroupRef(value, projectIndex);
  }

  /* @see MetadataStore#setProjectID(String, int) */
  public void setProjectID(String id, int projectIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setProjectID(value, projectIndex);
  }

  /* @see MetadataStore#setProjectName(String, int) */
  public void setProjectName(String name, int projectIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setProjectName(value, projectIndex);
  }

  // -- ProjectRef property storage -

  /* @see MetadataStore#setProjectRefID(String, int, int) */
  public void setProjectRefID(String id, int datasetIndex, int projectRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setProjectRefID(value, datasetIndex, projectRefIndex);
  }

  // -- Pump property storage -

  /* @see MetadataStore#setPumpLightSource(String, int, int) */
  public void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(lightSource) : lightSource;
    store.setPumpLightSource(value, instrumentIndex, lightSourceIndex);
  }

  // -- ROI property storage -

  /* @see MetadataStore#setROIID(String, int, int) */
  public void setROIID(String id, int imageIndex, int roiIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setROIID(value, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    store.setROIT0(t0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    store.setROIT1(t1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    store.setROIX0(x0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    store.setROIX1(x1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    store.setROIY0(y0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    store.setROIY1(y1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    store.setROIZ0(z0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    store.setROIZ1(z1, imageIndex, roiIndex);
  }

  // -- ROIRef property storage -

  /* @see MetadataStore#setROIRefID(String, int, int, int) */
  public void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setROIRefID(value, imageIndex, microbeamManipulationIndex, roiRefIndex);
  }

  // -- Reagent property storage -

  /* @see MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setReagentDescription(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setReagentID(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setReagentName(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(reagentIdentifier) : reagentIdentifier;
    store.setReagentReagentIdentifier(value, screenIndex, reagentIndex);
  }

  // -- Rect property storage -

  /* @see MetadataStore#setRectHeight(String, int, int, int) */
  public void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(height) : height;
    store.setRectHeight(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectID(String, int, int, int) */
  public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setRectID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectTransform(String, int, int, int) */
  public void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setRectTransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectWidth(String, int, int, int) */
  public void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(width) : width;
    store.setRectWidth(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectX(String, int, int, int) */
  public void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x) : x;
    store.setRectX(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectY(String, int, int, int) */
  public void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y) : y;
    store.setRectY(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Region property storage -

  /* @see MetadataStore#setRegionID(String, int, int) */
  public void setRegionID(String id, int imageIndex, int regionIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setRegionID(value, imageIndex, regionIndex);
  }

  /* @see MetadataStore#setRegionName(String, int, int) */
  public void setRegionName(String name, int imageIndex, int regionIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setRegionName(value, imageIndex, regionIndex);
  }

  /* @see MetadataStore#setRegionTag(String, int, int) */
  public void setRegionTag(String tag, int imageIndex, int regionIndex) {
    String value = filter ? DataTools.sanitize(tag) : tag;
    store.setRegionTag(value, imageIndex, regionIndex);
  }

  // -- RoiLink property storage -

  /* @see MetadataStore#setRoiLinkDirection(String, int, int, int) */
  public void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex) {
    String value = filter ? DataTools.sanitize(direction) : direction;
    store.setRoiLinkDirection(value, imageIndex, roiIndex, roiLinkIndex);
  }

  /* @see MetadataStore#setRoiLinkName(String, int, int, int) */
  public void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setRoiLinkName(value, imageIndex, roiIndex, roiLinkIndex);
  }

  /* @see MetadataStore#setRoiLinkRef(String, int, int, int) */
  public void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex) {
    String value = filter ? DataTools.sanitize(ref) : ref;
    store.setRoiLinkRef(value, imageIndex, roiIndex, roiLinkIndex);
  }

  // -- Screen property storage -

  /* @see MetadataStore#setScreenDescription(String, int) */
  public void setScreenDescription(String description, int screenIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setScreenDescription(value, screenIndex);
  }

  /* @see MetadataStore#setScreenExtern(String, int) */
  public void setScreenExtern(String extern, int screenIndex) {
    String value = filter ? DataTools.sanitize(extern) : extern;
    store.setScreenExtern(value, screenIndex);
  }

  /* @see MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setScreenID(value, screenIndex);
  }

  /* @see MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setScreenName(value, screenIndex);
  }

  /* @see MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
    String value = filter ? DataTools.sanitize(protocolDescription) : protocolDescription;
    store.setScreenProtocolDescription(value, screenIndex);
  }

  /* @see MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
    String value = filter ? DataTools.sanitize(protocolIdentifier) : protocolIdentifier;
    store.setScreenProtocolIdentifier(value, screenIndex);
  }

  /* @see MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
    String value = filter ? DataTools.sanitize(reagentSetDescription) : reagentSetDescription;
    store.setScreenReagentSetDescription(value, screenIndex);
  }

  /* @see MetadataStore#setScreenReagentSetIdentifier(String, int) */
  public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex) {
    String value = filter ? DataTools.sanitize(reagentSetIdentifier) : reagentSetIdentifier;
    store.setScreenReagentSetIdentifier(value, screenIndex);
  }

  /* @see MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setScreenType(value, screenIndex);
  }

  // -- ScreenAcquisition property storage -

  /* @see MetadataStore#setScreenAcquisitionEndTime(String, int, int) */
  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(endTime) : endTime;
    store.setScreenAcquisitionEndTime(value, screenIndex, screenAcquisitionIndex);
  }

  /* @see MetadataStore#setScreenAcquisitionID(String, int, int) */
  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setScreenAcquisitionID(value, screenIndex, screenAcquisitionIndex);
  }

  /* @see MetadataStore#setScreenAcquisitionStartTime(String, int, int) */
  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(startTime) : startTime;
    store.setScreenAcquisitionStartTime(value, screenIndex, screenAcquisitionIndex);
  }

  // -- ScreenRef property storage -

  /* @see MetadataStore#setScreenRefID(String, int, int) */
  public void setScreenRefID(String id, int plateIndex, int screenRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setScreenRefID(value, plateIndex, screenRefIndex);
  }

  // -- Shape property storage -

  /* @see MetadataStore#setShapeBaselineShift(String, int, int, int) */
  public void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(baselineShift) : baselineShift;
    store.setShapeBaselineShift(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeDirection(String, int, int, int) */
  public void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(direction) : direction;
    store.setShapeDirection(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFillColor(String, int, int, int) */
  public void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fillColor) : fillColor;
    store.setShapeFillColor(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFillOpacity(String, int, int, int) */
  public void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fillOpacity) : fillOpacity;
    store.setShapeFillOpacity(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFillRule(String, int, int, int) */
  public void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fillRule) : fillRule;
    store.setShapeFillRule(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontFamily(String, int, int, int) */
  public void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fontFamily) : fontFamily;
    store.setShapeFontFamily(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontSize(Integer, int, int, int) */
  public void setShapeFontSize(Integer fontSize, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontStretch(String, int, int, int) */
  public void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fontStretch) : fontStretch;
    store.setShapeFontStretch(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontStyle(String, int, int, int) */
  public void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fontStyle) : fontStyle;
    store.setShapeFontStyle(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontVariant(String, int, int, int) */
  public void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fontVariant) : fontVariant;
    store.setShapeFontVariant(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeFontWeight(String, int, int, int) */
  public void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(fontWeight) : fontWeight;
    store.setShapeFontWeight(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeG(String, int, int, int) */
  public void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(g) : g;
    store.setShapeG(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeGlyphOrientationVertical(Integer, int, int, int) */
  public void setShapeGlyphOrientationVertical(Integer glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeID(String, int, int, int) */
  public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setShapeID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeLocked(Boolean, int, int, int) */
  public void setShapeLocked(Boolean locked, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeAttribute(String, int, int, int) */
  public void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(strokeAttribute) : strokeAttribute;
    store.setShapeStrokeAttribute(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeColor(String, int, int, int) */
  public void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(strokeColor) : strokeColor;
    store.setShapeStrokeColor(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeDashArray(String, int, int, int) */
  public void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(strokeDashArray) : strokeDashArray;
    store.setShapeStrokeDashArray(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeLineCap(String, int, int, int) */
  public void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(strokeLineCap) : strokeLineCap;
    store.setShapeStrokeLineCap(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeLineJoin(String, int, int, int) */
  public void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(strokeLineJoin) : strokeLineJoin;
    store.setShapeStrokeLineJoin(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeMiterLimit(Integer, int, int, int) */
  public void setShapeStrokeMiterLimit(Integer strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeOpacity(Double, int, int, int) */
  public void setShapeStrokeOpacity(Double strokeOpacity, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeStrokeWidth(Integer, int, int, int) */
  public void setShapeStrokeWidth(Integer strokeWidth, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeText(String, int, int, int) */
  public void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(text) : text;
    store.setShapeText(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTextAnchor(String, int, int, int) */
  public void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(textAnchor) : textAnchor;
    store.setShapeTextAnchor(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTextDecoration(String, int, int, int) */
  public void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(textDecoration) : textDecoration;
    store.setShapeTextDecoration(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTextFill(String, int, int, int) */
  public void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(textFill) : textFill;
    store.setShapeTextFill(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTextStroke(String, int, int, int) */
  public void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(textStroke) : textStroke;
    store.setShapeTextStroke(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTheT(Integer, int, int, int) */
  public void setShapeTheT(Integer theT, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeTheZ(Integer, int, int, int) */
  public void setShapeTheZ(Integer theZ, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeVectorEffect(String, int, int, int) */
  public void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(vectorEffect) : vectorEffect;
    store.setShapeVectorEffect(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeVisibility(Boolean, int, int, int) */
  public void setShapeVisibility(Boolean visibility, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapeWritingMode(String, int, int, int) */
  public void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(writingMode) : writingMode;
    store.setShapeWritingMode(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setStageLabelName(value, imageIndex);
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

  // -- StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Double, int, int, int) */
  public void setStagePositionPositionX(Double positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionY(Double, int, int, int) */
  public void setStagePositionPositionY(Double positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionZ(Double, int, int, int) */
  public void setStagePositionPositionZ(Double positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- Thumbnail property storage -

  /* @see MetadataStore#setThumbnailHref(String, int) */
  public void setThumbnailHref(String href, int imageIndex) {
    String value = filter ? DataTools.sanitize(href) : href;
    store.setThumbnailHref(value, imageIndex);
  }

  /* @see MetadataStore#setThumbnailID(String, int) */
  public void setThumbnailID(String id, int imageIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setThumbnailID(value, imageIndex);
  }

  /* @see MetadataStore#setThumbnailMIMEtype(String, int) */
  public void setThumbnailMIMEtype(String mimEtype, int imageIndex) {
    String value = filter ? DataTools.sanitize(mimEtype) : mimEtype;
    store.setThumbnailMIMEtype(value, imageIndex);
  }

  // -- TiffData property storage -

  /* @see MetadataStore#setTiffDataFileName(String, int, int, int) */
  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    String value = filter ? DataTools.sanitize(fileName) : fileName;
    store.setTiffDataFileName(value, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataNumPlanes(Integer, int, int, int) */
  public void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataUUID(String, int, int, int) */
  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    String value = filter ? DataTools.sanitize(uuid) : uuid;
    store.setTiffDataUUID(value, imageIndex, pixelsIndex, tiffDataIndex);
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

  /* @see MetadataStore#setTransmittanceRangeTransmittance(Integer, int, int) */
  public void setTransmittanceRangeTransmittance(Integer transmittance, int instrumentIndex, int filterIndex) {
    store.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
  }

  // -- Well property storage -

  /* @see MetadataStore#setWellColumn(Integer, int, int) */
  public void setWellColumn(Integer column, int plateIndex, int wellIndex) {
    store.setWellColumn(column, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(externalDescription) : externalDescription;
    store.setWellExternalDescription(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setWellExternalIdentifier(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setWellID(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellReagent(String, int, int) */
  public void setWellReagent(String reagent, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(reagent) : reagent;
    store.setWellReagent(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellRow(Integer, int, int) */
  public void setWellRow(Integer row, int plateIndex, int wellIndex) {
    store.setWellRow(row, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellType(String, int, int) */
  public void setWellType(String type, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setWellType(value, plateIndex, wellIndex);
  }

  // -- WellSample property storage -

  /* @see MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setWellSampleID(value, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleImageRef(String, int, int, int) */
  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex) {
    String value = filter ? DataTools.sanitize(imageRef) : imageRef;
    store.setWellSampleImageRef(value, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleIndex(Integer, int, int, int) */
  public void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePosX(Double, int, int, int) */
  public void setWellSamplePosX(Double posX, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePosY(Double, int, int, int) */
  public void setWellSamplePosY(Double posY, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

  // -- WellSampleRef property storage -

  /* @see MetadataStore#setWellSampleRefID(String, int, int, int) */
  public void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setWellSampleRefID(value, screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
  }

}
